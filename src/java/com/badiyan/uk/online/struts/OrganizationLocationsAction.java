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
OrganizationLocationsAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in OrganizationLocationsAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		LocationBean adminLocation = null;
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
				adminLocation = (LocationBean)session.getAttribute("adminLocation");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-bean       name="organizationLocationsForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="map_url" type="java.lang.String"/>
      <form-property name="address" type="java.lang.Integer"/>
      <form-property name="comment" type="java.lang.String"/>
    </form-bean>
			 */

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			String map_url = (String)PropertyUtils.getSimpleProperty(_form, "map_url");
			Integer address = (Integer)PropertyUtils.getSimpleProperty(_form, "address");
			String comment = (String)PropertyUtils.getSimpleProperty(_form, "comment");
			
			adminLocation.setName(nameInput);
			adminLocation.setMapURL(map_url);
			if (address.intValue() > 0)
				adminLocation.setAddress(AddressBean.getAddress(address.intValue()));
			if (comment != null)
				adminLocation.setComment(comment);
			adminLocation.setCompany(adminCompany);
			
			if (nameInput == null || nameInput.equals(""))
				throw new IllegalValueException("Please specify a location name.");
			
			adminLocation.setCreateOrModifyPerson(loginBean.getPerson());
			adminLocation.save();
			
			session.removeAttribute("adminLocation");

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