/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.ParseException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
Setup06Action
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in Setup06Action");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean admin_company = null;
		HttpSession session = _request.getSession(false);

		String forward_string = "success";

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
				{
					loginBean = new UKOnlineLoginBean();
					session.setAttribute("loginBean", loginBean);
				}

				admin_company = (CompanyBean)session.getAttribute("adminCompany");
			}
			else
				return (_mapping.findForward("session_expired"));

			String submit_button = _request.getParameter("submit_button");

			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements())
			{
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));
			}

			/*
			 *<form-bean       name="setup06Form"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="startHour" type="java.lang.Integer"/>
      <form-property name="startMinute" type="java.lang.Integer"/>
      <form-property name="startAMPM" type="java.lang.Integer"/>
      <form-property name="endHour" type="java.lang.Integer"/>
      <form-property name="endMinute" type="java.lang.Integer"/>
      <form-property name="endAMPM" type="java.lang.Integer"/>
      <form-property name="state" type="java.lang.String"/>
      <form-property name="time_zone" type="java.lang.String"/>
    </form-bean>
			 */

			if (submit_button.equals("next") || submit_button.equals("submit"))
			{
			    Short startHour = (Short)PropertyUtils.getSimpleProperty(_form, "startHour");
			    Short startMinute = (Short)PropertyUtils.getSimpleProperty(_form, "startMinute");
			    Integer startAMPM = (Integer)PropertyUtils.getSimpleProperty(_form, "startAMPM");
			    Short endHour = (Short)PropertyUtils.getSimpleProperty(_form, "endHour");
			    Short endMinute = (Short)PropertyUtils.getSimpleProperty(_form, "endMinute");
			    Integer endAMPM = (Integer)PropertyUtils.getSimpleProperty(_form, "endAMPM");

			    String state = (String)PropertyUtils.getSimpleProperty(_form, "state");
			    String time_zone = (String)PropertyUtils.getSimpleProperty(_form, "time_zone");

			    System.out.println("startHour >" + startHour);
			    System.out.println("startMinute >" + startMinute);
			    System.out.println("startAMPM >" + startAMPM);
			    System.out.println("endHour >" + endHour);
			    System.out.println("endMinute >" + endMinute);
			    System.out.println("endAMPM >" + endAMPM);
			    System.out.println("state >" + state);
			    System.out.println("time_zone >" + time_zone);

				CompanySettingsBean settings = admin_company.getSettings();
				settings.setOpenHour((startAMPM.intValue() == Calendar.AM) ? startHour.shortValue() : (short)(startHour.shortValue() + 12));
				settings.setOpenMinute(startMinute.shortValue());
				settings.setCloseHour((endAMPM.intValue() == Calendar.AM) ? endHour.shortValue() : (short)(endHour.shortValue() + 12));
				settings.setCloseMinute(endMinute.shortValue());

				if (state.length() > 0)
				{
					settings.setState(state);
					settings.setTimeZone(time_zone);
				}

				if (startAMPM.intValue() == Calendar.PM && endAMPM.intValue() == Calendar.AM)
					throw new IllegalValueException("Open Time must be before Closing Time.");
				else if (startAMPM.intValue() == endAMPM.intValue())
				{
					if (startHour.shortValue() > endHour.shortValue())
						throw new IllegalValueException("Open Time must be before Closing Time.");
					else if (startHour.shortValue() == endHour.shortValue())
					{
						if (startMinute.shortValue() > endMinute.shortValue())
							throw new IllegalValueException("Open Time must be before Closing Time.");
						else if (startMinute.shortValue() == endMinute.shortValue())
							throw new IllegalValueException("Open Time must be before Closing Time.");
					}
				}

				if (state.length() == 0)
					throw new IllegalValueException("Please specify a State.");

				settings.save();

				if (submit_button.equals("next"))
					forward_string = "next";
				else
					forward_string = "success";
			}
			else if (submit_button.equals("previous"))
			{
				forward_string = "previous";
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty())
		{
			saveErrors(_request, errors);
			return _mapping.getInputForward();
		}

		// Remove the obsolete form bean
		if (_mapping.getAttribute() != null)
		{
            if ("request".equals(_mapping.getScope()))
                _request.removeAttribute(_mapping.getAttribute());
            else
                session.removeAttribute(_mapping.getAttribute());
        }

		// Forward control to the specified success URI
		return (_mapping.findForward(forward_string));
	}
}
