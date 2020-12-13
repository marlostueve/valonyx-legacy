/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;


import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.tasks.AppointmentMonitorTask;

import com.badiyan.uk.online.tasks.QBFSDataRequestTask;
import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
Setup01Action
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in Setup01Action");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		UKOnlinePersonBean adminPerson = null;
		UKOnlineCompanyBean adminCompany = null;
		Boolean isPractitioner = null;

		HttpSession session = _request.getSession(false);

		String forward_string = "success";

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				isPractitioner = (Boolean)session.getAttribute("isPractitioner");
			}
			else
				return (_mapping.findForward("session_expired"));

			String submit_button = _request.getParameter("submit_button");

			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements())
			{
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));
			}

			/*
			 * <form-bean       name="setup01Form"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="addressInput" type="java.lang.String"/>
      <form-property name="cityInput" type="java.lang.String"/>
      <form-property name="stateInput" type="java.lang.String"/>
      <form-property name="zipInput" type="java.lang.String"/>
      <form-property name="log_in_method" type="java.lang.String"/>
      <form-property name="emailInput" type="java.lang.String"/>
      <form-property name="usernameInput" type="java.lang.String"/>
      <form-property name="firstNameInput" type="java.lang.String"/>
      <form-property name="lastNameInput" type="java.lang.String"/>
      <form-property name="is_practitioner" type="java.lang.Boolean"/>
      <form-property name="password" type="java.lang.String"/>
      <form-property name="confirmPassword" type="java.lang.String"/>
      <form-property name="is_quickbooks" type="java.lang.Boolean"/>
      <form-property name="qb_username" type="java.lang.String"/>
      <form-property name="qb_password" type="java.lang.String"/>
      <form-property name="is_pos" type="java.lang.Boolean"/>
    </form-bean>
			 */

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");

			String addressInput = (String)PropertyUtils.getSimpleProperty(_form, "addressInput");
			String cityInput = (String)PropertyUtils.getSimpleProperty(_form, "cityInput");
			String stateInput = (String)PropertyUtils.getSimpleProperty(_form, "stateInput");
			String zipInput = (String)PropertyUtils.getSimpleProperty(_form, "zipInput");

			String log_in_method = (String)PropertyUtils.getSimpleProperty(_form, "log_in_method");
			String emailInput = (String)PropertyUtils.getSimpleProperty(_form, "emailInput");
			String usernameInput = (String)PropertyUtils.getSimpleProperty(_form, "usernameInput");
			String password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			String confirmPassword = (String)PropertyUtils.getSimpleProperty(_form, "confirmPassword");
			String firstNameInput = (String)PropertyUtils.getSimpleProperty(_form, "firstNameInput");
			String lastNameInput = (String)PropertyUtils.getSimpleProperty(_form, "lastNameInput");
			Boolean is_practitioner = (Boolean)PropertyUtils.getSimpleProperty(_form, "is_practitioner");
			Boolean is_quickbooks = (Boolean)PropertyUtils.getSimpleProperty(_form, "is_quickbooks");
			String qb_username = (String)PropertyUtils.getSimpleProperty(_form, "qb_username");
			String qb_password = (String)PropertyUtils.getSimpleProperty(_form, "qb_password");
			Boolean is_pos = (Boolean)PropertyUtils.getSimpleProperty(_form, "is_pos");
			
			String x_login = (String)PropertyUtils.getSimpleProperty(_form, "x_login");
			String x_tran_key = (String)PropertyUtils.getSimpleProperty(_form, "x_tran_key");

			System.out.println("nameInput >" + nameInput);
			System.out.println("log_in_method >" + log_in_method);
			System.out.println("emailInput >" + emailInput);
			System.out.println("usernameInput >" + usernameInput);
			System.out.println("password >" + password);
			System.out.println("confirmPassword >" + confirmPassword);
			System.out.println("firstNameInput >" + firstNameInput);
			System.out.println("lastNameInput >" + lastNameInput);
			System.out.println("is_practitioner >" + is_practitioner);
			System.out.println("is_quickbooks >" + is_quickbooks);
			System.out.println("qb_username >" + qb_username);
			System.out.println("qb_password >" + qb_password);
			System.out.println("is_pos >" + is_pos);
			
			System.out.println("x_login >" + x_login);
			System.out.println("x_tran_key >" + x_tran_key);
			
			if (submit_button.equals("next") || submit_button.equals("submit"))
			{
				adminCompany.setName(nameInput);
				adminCompany.setType("Corporation");

				CompanySettingsBean settings = adminCompany.getSettings();
				
				settings.setLoginLabel(log_in_method.equals("Log in using Email Address") ? "Email Address" : "Username");

				boolean is_new = adminPerson.isNew();

				// create the admin user

				boolean changing_email_address = true;
				if (!adminPerson.isNew())
				{
					if (adminPerson.getEmail1String().equals(emailInput))
						changing_email_address = false;
				}

				boolean changing_username = true;
				if (!adminPerson.isNew())
				{
					if (adminPerson.getUsernameString().equals(usernameInput))
						changing_username = false;
				}

				adminPerson.setUsernameNoVerify(log_in_method.equals("Log in using Email Address") ? emailInput : usernameInput);
				adminPerson.setPasswordNoVerify(password);
				adminPerson.setConfirmPassword(confirmPassword);
				adminPerson.setFirstName(firstNameInput);
				adminPerson.setLastName(lastNameInput);
				adminPerson.setEmailNoVerify(emailInput);

				if ((is_practitioner != null) && is_practitioner.booleanValue())
					isPractitioner = new Boolean(true);
				else
					isPractitioner = new Boolean(false);
				session.setAttribute("isPractitioner", isPractitioner);


				if (nameInput.length() == 0)
					throw new IllegalValueException("Please provide a Practice Name.");
				if (emailInput.length() == 0)
					throw new IllegalValueException("Please provide an Email Address.");

				if (changing_email_address)
				{
					if (UKOnlinePersonBean.emailExists(emailInput))
						throw new IllegalValueException("Your selected email address is already in use.");
				}

				if (!log_in_method.equals("Log in using Email Address"))
				{
					if (changing_username)
					{
						if (usernameInput.length() == 0)
							throw new IllegalValueException("Please provide a Username.");

						if (UKOnlinePersonBean.usernameExists(usernameInput))
							throw new IllegalValueException("Your selected username is already in use.");
					}
				}

				if (firstNameInput.length() == 0)
					throw new IllegalValueException("Please provide a First Name.");
				if (lastNameInput.length() == 0)
					throw new IllegalValueException("Please provide a Last Name.");

				if (password.length() == 0)
					throw new IllegalValueException("Please provide a Password.");
				if (!password.equals(confirmPassword))
					throw new IllegalValueException("The passwords that you entered do not match.");

				adminPerson.setUsername(log_in_method.equals("Log in using Email Address") ? emailInput : usernameInput);
				adminPerson.setEmail(emailInput);
				adminPerson.setPassword(password);
				adminPerson.save();

				// assign company admin role

				RoleBean adminRole = RoleBean.getRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);
				if (!adminPerson.hasRole(adminRole))
					adminPerson.addRole(adminRole);

				RoleBean practitionerRole = (RoleBean)UKOnlineRoleBean.getRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME);
				if ((is_practitioner != null) && is_practitioner.booleanValue())
				{
					if (!adminPerson.hasRole(practitionerRole))
						adminPerson.addRole(practitionerRole);
				}
				else
				{
					if (adminPerson.hasRole(practitionerRole))
						adminPerson.removeRole(practitionerRole);
				}

				UKOnlinePersonBean.invalidatePractitioners(adminCompany);

				// create the organization

				adminCompany.save();
				
				int company_id = adminCompany.getId();
				
				// this is a stupid work-around hack to prevent dups - not sure what was happening exactly... 9/3/11
				session.removeAttribute("adminCompany");
				adminCompany = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(company_id);
				session.setAttribute("adminCompany", adminCompany);
				
				settings.setCompany(adminCompany);
				
				settings.save();

				
				if (is_new)
				{
					DepartmentBean default_dept = adminCompany.getDefaultDepartment();
					PersonTitleBean.maintainDefaultData(adminCompany);
					PersonGroupBean.maintainGroup(adminCompany, PersonGroupBean.DEFAULT_PERSON_GROUP_NAME);

					adminPerson.setDepartment(default_dept);
					adminPerson.save();

					// associate this person as the admin of the new organization

					CompanyAdministratorMapBean map = new CompanyAdministratorMapBean();
					map.setCompany(adminCompany);
					map.setPerson(adminPerson);
					map.save();
				}

				// now log the new person in

				if (!loginBean.isLoggedIn())
				{
					loginBean.setUsername(log_in_method.equals("Log in using Email Address") ? emailInput : usernameInput);
					loginBean.setPassword(password);
					loginBean.getPerson();
				}

				QuickBooksSettings qb_settings = adminCompany.getQuickBooksSettings();
				qb_settings.setQuickBooksFSEnabled((is_quickbooks != null) && is_quickbooks.booleanValue());
				qb_settings.setUserName(qb_username);
				qb_settings.setPassword(qb_password);
				qb_settings.setPOSEnabled((is_pos != null) && is_pos.booleanValue());
				qb_settings.setXLogin(x_login);
				qb_settings.setXTranKey(x_tran_key);
				qb_settings.save();
				

				AddressBean practice_address;
				try
				{
					practice_address = adminCompany.getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
				}
				catch (Exception x)
				{
					practice_address = new AddressBean();
					practice_address.setType(AddressBean.PRACTICE_ADDRESS_TYPE);
				}
				practice_address.setType(AddressBean.PRACTICE_ADDRESS_TYPE);
				practice_address.setStreet1(addressInput);
				practice_address.setCity(cityInput);
				practice_address.setState(stateInput);
				practice_address.setZipCode(zipInput);
				practice_address.setCompany(adminCompany);
				practice_address.save();


				if (qb_settings.isQuickBooksFSEnabled())
				{
					Timer timerObj = new Timer(true);
					Calendar now = Calendar.getInstance();
					timerObj.schedule(new QBFSDataRequestTask(adminCompany), now.getTime()); // don't repeat
				}

				
				
				SubscriptionInfo subscription_info = adminCompany.getSubscriptionInfo();
				System.out.println("subscription_info >" + subscription_info.isNew());
				if (subscription_info.isNew()) {
					subscription_info.setSubscriber(adminPerson);
					subscription_info.save();
					
					CUBean.timerObj.schedule(new AppointmentMonitorTask(adminCompany), new Date(), 60 * 1000); // every minute
						// I'm placing this here because this will only get run if this is a new Company here - didn't want to worry about whether or not adminCompany may or may not be "new" at this point
				}
				
			}

			forward_string = submit_button;

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

		System.out.println("xxforward_string >" + forward_string);

		// Forward control to the specified success URI
		return (_mapping.findForward(forward_string));
	}
}