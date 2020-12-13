/*
 * CertificationMainAction.java
 *
 * Created on February 18, 2007, 3:15 PM
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
CertificationMainAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CertificationMainAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		EcolabCertificationBean adminCertification = null;
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
				
				adminCertification = (EcolabCertificationBean)session.getAttribute("adminCertification");
			}
			else
				return (_mapping.findForward("session_expired"));

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			Boolean active = (Boolean)PropertyUtils.getSimpleProperty(_form, "active");
			String description = (String)PropertyUtils.getSimpleProperty(_form, "description");
			Integer contact = (Integer)PropertyUtils.getSimpleProperty(_form, "contact");
			
			System.out.println("nameInput >" + nameInput);
			System.out.println("active >" + active);
			System.out.println("description >" + description);
			System.out.println("contact >" + contact);
			
			adminCertification.setName(nameInput);
			adminCertification.setDescription(description);
			
			if (active != null)
			    adminCertification.setIsActive(active.booleanValue());
			
			if (contact.intValue() != 0)
			    adminCertification.setContact((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(contact.intValue()));
			adminCertification.save();
			
			// associate this person as the admin of the new organization
			
			

			// select this company
			
			//loginBean.getPerson().selectCompany(adminCompany);
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