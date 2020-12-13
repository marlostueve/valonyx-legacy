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
LostPasswordAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in LostPasswordAction");

		String forward_string = "success";


		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);
		
		
		UKOnlineCompanyBean userCompany = null;
		
		String email = null;

		try
		{

			email = (String)PropertyUtils.getSimpleProperty(_form, "email");
			
			System.out.println("email >" + email);
			
			if (email.length() == 0)
				throw new IllegalValueException("<strong>Error:</strong> Please provide your email address.");
			
			// find a user that corresponds to this email address
			
			userCompany = (UKOnlineCompanyBean)session.getAttribute("userCompany");
			
			UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPersonByEmail(userCompany, email);
			
			System.out.println("person found for email >" + person.getLabel());

			CUBean.sendEmail(email, "admin@valonyx.com", "Your Valonyx Password", "Your Valonyx password is:\r\n" + person.getPasswordString());
			CUBean.sendEmail("marlo@valonyx.com", "admin@valonyx.com", "Your Valonyx Password", "Your Valonyx password is:\r\n" + person.getPasswordString());
			
			session.setAttribute("forgot-password-message", "Your password has been sent to " + email);
		}
		catch (IllegalValueException x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			
			session.setAttribute("forgot-password-message", "<strong>Error:</strong> " + x.getMessage());
		}
		catch (ObjectNotFoundException x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			
			session.setAttribute("forgot-password-message", "<strong>Error:</strong> Unable to locate account for email " + email);
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