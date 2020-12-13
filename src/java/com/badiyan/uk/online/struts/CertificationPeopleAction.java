package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
//import com.badiyan.uk.forms.*;

import com.badiyan.uk.online.beans.*;

import java.text.ParseException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

import java.io.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
CertificationPeopleAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CertificationPeopleAction");


		ActionErrors errors = new ActionErrors();

		CertificationBean certBean = null;
		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
					return (_mapping.findForward("session_expired"));
				else
				{
					try
					{
						UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
							return (_mapping.findForward("session_expired"));
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				certBean = (CertificationBean)session.getAttribute("adminCert");
				if (certBean == null)
				{
					certBean = new CertificationBean();
					session.setAttribute("adminCert", certBean);
				}
			}
			else
				return (_mapping.findForward("session_expired"));

			Integer ownerSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "ownerSelect");
			String contactNameInput = (String)PropertyUtils.getSimpleProperty(_form, "contactNameInput");
			String contactEmailInput = (String)PropertyUtils.getSimpleProperty(_form, "contactEmailInput");
			String contactDescInput = (String)PropertyUtils.getSimpleProperty(_form, "contactDescInput");

			if (ownerSelect.intValue() > 0)
			{
				PersonBean owner = PersonBean.getPerson(ownerSelect.intValue());
				certBean.setOwnerPerson(owner);
			}

			certBean.setContactEmail(contactEmailInput);
			certBean.setContact(contactNameInput);
			certBean.setContactDescription(contactDescInput);
			certBean.save();
		}
        catch (IllegalValueException x)
        {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
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