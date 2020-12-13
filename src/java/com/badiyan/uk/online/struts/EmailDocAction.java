/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import com.badiyan.uk.online.util.CCUtils;
import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;




import com.valeo.qbpos.data.*;
import java.math.BigDecimal;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
EmailDocAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in EmailDocAction");

		String forward_string = "success";


		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...


			String email = (String)PropertyUtils.getSimpleProperty(_form, "email");
			String subject = (String)PropertyUtils.getSimpleProperty(_form, "subject");
			String text = (String)PropertyUtils.getSimpleProperty(_form, "text");
			
			System.out.println("email >" + email);
			System.out.println("subject >" + subject);
			System.out.println("text >" + text);
			
			if (email.length() == 0)
				throw new IllegalValueException("Please provide your email address.");
			if (text.length() == 0)
				throw new IllegalValueException("Please provide email text.");

			CUBean.sendEmail("cstueve@valeowc.com", email, subject, text);
			CUBean.sendEmail("marlo@valeowc.com", email, subject, text);
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