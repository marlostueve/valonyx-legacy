/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;


import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import java.math.BigDecimal;

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
EOD04Action
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in EOD04Action");

		ActionErrors errors = new ActionErrors();

		CashOut cashOut = null;
		UKOnlineLoginBean loginBean = null;

		HttpSession session = _request.getSession(false);

		String forward_string = "success";

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				cashOut = (CashOut)session.getAttribute("cashOut");
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
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
			 * <form-bean       name="eod04Form"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="begin" type="java.lang.String"/>
	  <form-property name="count" type="java.lang.String"/>
	  <form-property name="leave" type="java.lang.String"/>
      <form-property name="count_100" type="java.lang.Short"/>
      <form-property name="count_50" type="java.lang.Short"/>
      <form-property name="count_20" type="java.lang.Short"/>
      <form-property name="count_10" type="java.lang.Short"/>
      <form-property name="count_5" type="java.lang.Short"/>
      <form-property name="count_1" type="java.lang.Short"/>
      <form-property name="count_50cent" type="java.lang.Short"/>
      <form-property name="count_25cent" type="java.lang.Short"/>
      <form-property name="count_10cent" type="java.lang.Short"/>
      <form-property name="count_5cent" type="java.lang.Short"/>
      <form-property name="count_1cent" type="java.lang.Short"/>
    </form-bean>
			 */

			String begin = (String)PropertyUtils.getSimpleProperty(_form, "begin");
			String count = (String)PropertyUtils.getSimpleProperty(_form, "count");
			String leave = (String)PropertyUtils.getSimpleProperty(_form, "leave");

			Short count_100 = (Short)PropertyUtils.getSimpleProperty(_form, "count_100");
			Short count_50 = (Short)PropertyUtils.getSimpleProperty(_form, "count_50");
			Short count_20 = (Short)PropertyUtils.getSimpleProperty(_form, "count_20");
			Short count_10 = (Short)PropertyUtils.getSimpleProperty(_form, "count_10");
			Short count_5 = (Short)PropertyUtils.getSimpleProperty(_form, "count_5");
			Short count_1 = (Short)PropertyUtils.getSimpleProperty(_form, "count_1");
			Short count_50cent = (Short)PropertyUtils.getSimpleProperty(_form, "count_50cent");
			Short count_25cent = (Short)PropertyUtils.getSimpleProperty(_form, "count_25cent");
			Short count_10cent = (Short)PropertyUtils.getSimpleProperty(_form, "count_10cent");
			Short count_5cent = (Short)PropertyUtils.getSimpleProperty(_form, "count_5cent");
			Short count_1cent = (Short)PropertyUtils.getSimpleProperty(_form, "count_1cent");

			System.out.println("begin >" + begin);
			System.out.println("count >" + count);
			System.out.println("leave >" + leave);

			System.out.println("count_100 >" + count_100);
			System.out.println("count_50 >" + count_50);
			System.out.println("count_20 >" + count_20);
			System.out.println("count_10 >" + count_10);
			System.out.println("count_5 >" + count_5);
			System.out.println("count_1 >" + count_1);
			System.out.println("count_50cent >" + count_50cent);
			System.out.println("count_25cent >" + count_25cent);
			System.out.println("count_10cent >" + count_10cent);
			System.out.println("count_5cent >" + count_5cent);
			System.out.println("count_1cent >" + count_1cent);

			if (submit_button.equals("next") || submit_button.equals("submit"))
			{

				// grab the workstation name from the cookie

				String workstation_cookie_name = "valeo.workstation_name";
				String is_primary_cookie_name = "valeo.workstation_primary";

				Cookie[] cookies = _request.getCookies();
				Cookie workstation_cookie = null;
				Cookie primary_cookie = null;
				if (cookies != null)
				{
					for (int i = 0; i < cookies.length; i++)
					{
						Cookie cookie = cookies[i];
						if (cookie.getName().equals(workstation_cookie_name))
							workstation_cookie = cookie;
						else if (cookie.getName().equals(is_primary_cookie_name))
							primary_cookie = cookie;
					}
				}

				String workstation_name = workstation_cookie.getValue();
				boolean is_primary = primary_cookie.getValue().equals("true") ? true : false;

				BigDecimal begin_amount = new BigDecimal(begin);
				BigDecimal count_amount = new BigDecimal(count);
				BigDecimal leave_amount = new BigDecimal(leave);

				// deposit = count - leave

				BigDecimal deposit_amount = count_amount.subtract(leave_amount);
				BigDecimal paid_out_amount = CUBean.zero; // don't know where this came from...

				cashOut.setDrawerCountDollars((UKOnlinePersonBean)loginBean.getPerson(), workstation_name, begin_amount, paid_out_amount, count_amount, leave_amount, deposit_amount);
				cashOut.setDrawerCount((UKOnlinePersonBean)loginBean.getPerson(), workstation_name, is_primary, count_100, count_50, count_20, count_10, count_5, count_1, count_50cent, count_25cent, count_10cent, count_5cent, count_1cent);

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