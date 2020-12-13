/*
 * AppointmentTypesAction.java
 *
 * Created on November 10, 2007, 7:24 PM
 *
 * To change this template, choose Tools | Template Manager
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
AppointmentTypesAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in AppointmentTypesAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		AppointmentTypeBean appointment_type = null;
		
		HttpSession session = _request.getSession(false);

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
				appointment_type = (AppointmentTypeBean)session.getAttribute("adminAppointmentType");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-bean       name="appointmentTypesForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="duration" type="java.lang.String"/>
      <form-property name="image_url" type="java.lang.String"/>
      <form-property name="bg_color" type="java.lang.String"/>
      <form-property name="text_color" type="java.lang.String"/>
      <form-property name="appointment_type_type" type="java.lang.Short"/>
      <form-property name="practice_area" type="java.lang.Integer"/>
    </form-bean>
			 */

			if (_request.getParameter("delete_id").equals("0"))
			{
			    String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			    String duration = (String)PropertyUtils.getSimpleProperty(_form, "duration");
			    String image_url = (String)PropertyUtils.getSimpleProperty(_form, "image_url");
			    String bg_color = (String)PropertyUtils.getSimpleProperty(_form, "bg_color");
			    String text_color = (String)PropertyUtils.getSimpleProperty(_form, "text_color");
			    Short appointment_type_type = (Short)PropertyUtils.getSimpleProperty(_form, "appointment_type_type");
			    Integer practice_area = (Integer)PropertyUtils.getSimpleProperty(_form, "practice_area");

			    System.out.println("nameInput >" + nameInput);
			    System.out.println("duration >" + duration);
			    System.out.println("image_url >" + image_url);
			    System.out.println("bg_color >" + bg_color);
			    System.out.println("text_color >" + text_color);
			    System.out.println("appointment_type_type >" + appointment_type_type);
			    System.out.println("practice_area >" + practice_area);
			    
			    appointment_type.setCompany(admin_company);
			    appointment_type.setName(nameInput);
			    appointment_type.setImageUrl(image_url);
			    appointment_type.setBGColorCode(bg_color);
			    appointment_type.setTextColorCode(text_color);
			    if (appointment_type_type.shortValue() != (short)0)
					appointment_type.setType(appointment_type_type.shortValue());
			    appointment_type.setPracticeAreaId(practice_area.intValue());
			    
			    try
			    {
					appointment_type.setDuration(Short.parseShort(duration));
			    }
			    catch (Exception x)
			    {
					//throw new IllegalValueException("You must specify a duration.");
			    }
			    
			    if (appointment_type_type.shortValue() == (short)0)
				throw new IllegalValueException("You must specify a type.");
			    
			    appointment_type.save();
			    
			    /*
			    practice_area.setName(nameInput);
			    practice_area.setCompany(admin_company);
			    if (parent.intValue() > 0)
				practice_area.setParentId(parent.intValue());
			    practice_area.save();
			     */

			    session.removeAttribute("adminAppointmentType");
			}
			else
			{
				AppointmentTypeBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
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
		return (_mapping.findForward("success"));
	}
}
