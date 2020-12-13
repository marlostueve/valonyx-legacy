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
SuggestionAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in SuggestionAction");

		String forward_string = "success";


		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);
		
		String email = null;
		String suggestion = null;

		try
		{
			email = (String)PropertyUtils.getSimpleProperty(_form, "email");
			suggestion = (String)PropertyUtils.getSimpleProperty(_form, "message");
			
			//System.out.println("email >" + email);
			
			if (email.length() == 0)
				throw new IllegalValueException("Please provide your email address.");
			
			if (suggestion.length() == 0)
				throw new IllegalValueException("Please provide your suggestion.");
			
			CUBean.sendEmail("marlo@valonyx.com", email, "Valonyx Suggestion", suggestion);
			
			session.setAttribute("suggestion-message", "Thanks for your suggestion!");
		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			
			session.setAttribute("forgot-password-message", "<strong>Error:</strong> Unable to locate account for email " + email);
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