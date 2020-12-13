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
OrganizationAddressesAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in OrganizationAddressesAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		AddressBean adminAddress = null;
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
				adminAddress = (AddressBean)session.getAttribute("adminAddress");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-property name="address_1" type="java.lang.String"/>
      <form-property name="address_2" type="java.lang.String"/>
      <form-property name="address_3" type="java.lang.String"/>
      <form-property name="city" type="java.lang.String"/>
      <form-property name="state" type="java.lang.String"/>
      <form-property name="intlstateprovince" type="java.lang.String"/>
      <form-property name="country" type="java.lang.String"/>
      <form-property name="zip" type="java.lang.String"/>
			 */

			String address_1 = (String)PropertyUtils.getSimpleProperty(_form, "address_1");
			String address_2 = (String)PropertyUtils.getSimpleProperty(_form, "address_2");
			String address_3 = (String)PropertyUtils.getSimpleProperty(_form, "address_3");
			String city = (String)PropertyUtils.getSimpleProperty(_form, "city");
			String state = (String)PropertyUtils.getSimpleProperty(_form, "state");
			String country = (String)PropertyUtils.getSimpleProperty(_form, "country");
			String zip = (String)PropertyUtils.getSimpleProperty(_form, "zip");
			
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