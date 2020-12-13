/*
 * ConfirmPasswordAction.java
 *
 * Created on April 17, 2007, 2:49 PM
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

import com.badiyan.uk.conformance.aicc.rte.datamodel.*;

import com.badiyan.uk.online.tasks.*;

import java.io.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
ConfirmPasswordAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute invoked in ConfirmPasswordAction");
		
		String forward_string = "success";
		

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);
		
		UKOnlinePersonBean person;

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				person = (UKOnlinePersonBean)loginBean.getPerson();
			}
			else
				return (_mapping.findForward("session_expired"));

			String password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			String confirm_password = (String)PropertyUtils.getSimpleProperty(_form, "confirm_password");
			
			System.out.println("password >" + password);
			System.out.println("confirm_password >" + confirm_password);
			
			person.setPassword(password);
			person.setConfirmPassword(confirm_password);
			person.save();
			
			loginBean.setUsername(person.getUsernameString());
			loginBean.setPassword(password);
			person = (UKOnlinePersonBean)loginBean.getPerson();
		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			
			session.setAttribute("forgot-password-message", "<strong>Error:</strong> " + x.getMessage());
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