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
OrganizationLanguagesAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in OrganizationLanguagesAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		LanguageBean adminLanguage = null;
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
				
				adminCompany = (CompanyBean)session.getAttribute("adminCompany");
				//adminAddress = (AddressBean)session.getAttribute("adminAddress");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			

			Integer language = (Integer)PropertyUtils.getSimpleProperty(_form, "language");
			if (language == null)
				throw new IllegalValueException("Please select a language to assign.");
			else
				System.out.println("language >" + language.intValue());
			
			LanguageBean language_obj = LanguageBean.getLanguage(language.intValue());
			/*
			adinCompany.addSupportForLanguage(language_obj);
			 */
			
			/*
			adminAddress.setStreet1(address_1);
			adminAddress.setStreet2(address_2);
			adminAddress.setStreet3(address_3);
			adminAddress.setCity(city);
			adminAddress.setState(state);
			adminAddress.setCountry(country);
			adminAddress.setZipCode(zip);
			adminAddress.setType(AddressBean.DEFAULT_ADDRESS_TYPE);
			adminAddress.setCompany(adminCompany);
			adminAddress.setCreateOrModifyPerson(loginBean.getPerson());
			adminAddress.save();
			
			session.removeAttribute("adminAddress");
			 */

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