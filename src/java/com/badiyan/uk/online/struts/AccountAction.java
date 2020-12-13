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
AccountAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in AccountAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineCompanyBean adminCompany = null;
		UKOnlineLoginBean loginBean = null;

		HttpSession session = _request.getSession(false);

		String forward_string = "success";

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
			}
			else
				return (_mapping.findForward("session_expired"));

			String submit_button = _request.getParameter("submit_button");

			String browser = "unknown";
			String version = "unknown";

			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements())
			{
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));

				if (param_name.equals("browser"))
					browser = _request.getParameter(param_name);
				if (param_name.equals("version"))
					version = _request.getParameter(param_name);
			}


			QuickBooksSettings qb_settings = adminCompany.getQuickBooksSettings();
			
			if (_request.getParameter("is_pos") != null && ((String)_request.getParameter("is_pos")).equals("1"))
				qb_settings.setPOSEnabled(true);
			else
				qb_settings.setPOSEnabled(false);
			
			qb_settings.save();

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
		
		
		
		
		ActionForward af = _mapping.findForward(forward_string);
		if (af == null)
			af = _mapping.findForward("success");
		
		ActionForward af2 = new ActionForward();
		//af2.setContextRelative(true);
		af2.setName(af.getName());
		af2.setRedirect(true);
		af2.setPath(af.getPath());
		af2.freeze();
		
		// Forward control to the specified success URI
		return af2;

		

		// Forward control to the specified success URI
		//return (_mapping.findForward(forward_string));
	}
}