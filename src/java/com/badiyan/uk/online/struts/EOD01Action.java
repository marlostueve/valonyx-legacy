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
EOD01Action
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in EOD01Action");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		UKOnlinePersonBean adminPerson = null;
		UKOnlineCompanyBean adminCompany = null;
		Boolean isPractitioner = null;

		HttpSession session = _request.getSession(false);

		String forward_string = "success";

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				isPractitioner = (Boolean)session.getAttribute("isPractitioner");
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
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="is_primary" type="java.lang.Boolean"/>
    </form-bean>
			 */

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			Boolean is_primary_obj = (Boolean)PropertyUtils.getSimpleProperty(_form, "is_primary");

			System.out.println("nameInput >" + nameInput);
			System.out.println("is_primary >" + is_primary_obj);

			if (submit_button.equals("next") || submit_button.equals("submit"))
			{
				// place the values into a cookie

				String workstation_cookie_name = "valeo.workstation_name";
				String is_primary_cookie_name = "valeo.workstation_primary";

				Cookie workstation_cookie = new Cookie(workstation_cookie_name, nameInput);
				workstation_cookie.setMaxAge(365 * 24 * 60 * 60); // expires in one year provided server time matches client time.  may be an issue for several reasons...
				_response.addCookie(workstation_cookie);

				boolean is_primary = ((is_primary_obj != null) && is_primary_obj.booleanValue());
				Cookie primary_cookie = new Cookie(is_primary_cookie_name, is_primary ? "true" : "false");
				primary_cookie.setMaxAge(365 * 24 * 60 * 60);
				_response.addCookie(primary_cookie);

				session.setAttribute("is_primary_workstation", is_primary ? "true" : "false");

				if (is_primary)
					forward_string = "primary";
				else
					forward_string = "secondary";

				// is there a cash out currently in progress

				CashOut cash_out = null;

				try
				{
					cash_out = CashOut.getOpenCashOut(adminCompany);
				}
				catch (ObjectNotFoundException x)
				{
					// apparently not.  start a new cash out...

					cash_out = new CashOut();
					cash_out.setCompany(adminCompany);
					cash_out.setCreateOrModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
				}

				session.setAttribute("cashOut", cash_out);
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