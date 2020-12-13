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
EOD02Action
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in EOD02Action");

		ActionErrors errors = new ActionErrors();

		CashOut cashOut = null;

		HttpSession session = _request.getSession(false);

		String forward_string = "success";

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				cashOut = (CashOut)session.getAttribute("cashOut");
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
			 * <form-bean       name="eod01Form"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="start_date" type="java.lang.String"/>
      <form-property name="end_date" type="java.lang.String"/>
    </form-bean>
			 */

			String start_date = (String)PropertyUtils.getSimpleProperty(_form, "start_date");
			String end_date = (String)PropertyUtils.getSimpleProperty(_form, "end_date");

			System.out.println("start_date >" + start_date);
			System.out.println("end_date >" + end_date);

			if (submit_button.equals("next") || submit_button.equals("submit"))
			{
				Date start_date_obj = CUBean.getDateFromUserString(start_date);
				Date end_date_obj = CUBean.getDateFromUserString(end_date);

				//session.setAttribute("start_date", start_date_obj);
				//session.setAttribute("end_date", end_date_obj);

				if (start_date_obj.after(end_date_obj))
					throw new IllegalValueException("Start date must be before end date.");
				Date now = new Date();
				if (end_date_obj.after(now))
					throw new IllegalValueException("End date cannot be in the future.");

				cashOut.setStartDate(start_date_obj);
				cashOut.setEndDate(end_date_obj);
				cashOut.save();

				forward_string = "next";
			}
			else if (submit_button.equals("previous"))
				forward_string = "previous";

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