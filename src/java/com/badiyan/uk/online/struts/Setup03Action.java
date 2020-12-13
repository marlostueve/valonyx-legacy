/*
 * To change this template, choose Tools | Templates
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
Setup03Action
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in Setup03Action");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		UKOnlinePersonBean staffPerson = null;
		UKOnlineCompanyBean adminCompany = null;

		HttpSession session = _request.getSession(false);

		String forward_string = "success";

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				staffPerson = (UKOnlinePersonBean)session.getAttribute("adminStaff");
				adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
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
			 * <form-bean       name="setup02Form"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="emailInput" type="java.lang.String"/>
      <form-property name="usernameInput" type="java.lang.String"/>
      <form-property name="password" type="java.lang.String"/>
      <form-property name="firstNameInput" type="java.lang.String"/>
      <form-property name="lastNameInput" type="java.lang.String"/>
      <form-property name="is_administrator" type="java.lang.Boolean"/>
    </form-bean>
			 */

			String emailInput = (String)PropertyUtils.getSimpleProperty(_form, "emailInput");
			String usernameInput = (String)PropertyUtils.getSimpleProperty(_form, "usernameInput");
			String password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			String firstNameInput = (String)PropertyUtils.getSimpleProperty(_form, "firstNameInput");
			String lastNameInput = (String)PropertyUtils.getSimpleProperty(_form, "lastNameInput");
			Boolean is_administrator = (Boolean)PropertyUtils.getSimpleProperty(_form, "is_administrator");

			System.out.println("emailInput >" + emailInput);
			System.out.println("usernameInput >" + usernameInput);
			System.out.println("password >" + password);
			System.out.println("firstNameInput >" + firstNameInput);
			System.out.println("lastNameInput >" + lastNameInput);
			System.out.println("is_administrator >" + is_administrator);

			if ((_request.getParameter("delete_id") != null) && (((String)_request.getParameter("delete_id")).length() > 0) && (Integer.parseInt(_request.getParameter("delete_id")) > 0))
			{
				UKOnlinePersonBean delete_person = UKOnlinePersonBean.getPerson(adminCompany, Integer.parseInt(_request.getParameter("delete_id")));
				//UKOnlinePersonBean.delete(delete_person);
				delete_person.removeRole(RoleBean.getRole(UKOnlineRoleBean.CASHIER_ROLE_NAME));

				UKOnlinePersonBean.invalidateCashiers(adminCompany);
				adminCompany.invalidateCache();
				session.removeAttribute("adminStaff");

				forward_string = "add";
			}
			else if (submit_button.equals("Add Staff"))
			{
				CompanySettingsBean settings = adminCompany.getSettings();

				// create the staff member

				boolean changing_email_address = true;
				if (!staffPerson.isNew())
				{
					if (staffPerson.getEmail1String().equals(emailInput))
						changing_email_address = false;
				}

				boolean changing_username = true;
				if (!staffPerson.isNew())
				{
					if (staffPerson.getUsernameString().equals(usernameInput))
						changing_username = false;
				}

				staffPerson.setUsernameNoVerify((!settings.getLoginLabelString().equals("Username")) ? emailInput : usernameInput);
				staffPerson.setPasswordNoVerify(password);
				staffPerson.setFirstName(firstNameInput);
				staffPerson.setLastName(lastNameInput);
				staffPerson.setEmailNoVerify(emailInput);

				Vector roles_vec = new Vector();
				if ((is_administrator != null) && is_administrator.booleanValue())
				{
					RoleBean admin_role = RoleBean.getRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);
					roles_vec.addElement(admin_role);
				}
				RoleBean cashierRole = (RoleBean)UKOnlineRoleBean.getRole(UKOnlineRoleBean.CASHIER_ROLE_NAME);
				roles_vec.addElement(cashierRole);
				staffPerson.setRoles(roles_vec);

				if (firstNameInput.length() == 0)
					throw new IllegalValueException("Please provide a First Name.");
				if (lastNameInput.length() == 0)
					throw new IllegalValueException("Please provide a Last Name.");

				if (emailInput.length() == 0)
					throw new IllegalValueException("Please provide an Email Address.");

				if (changing_email_address)
				{
					if (UKOnlinePersonBean.emailExists(emailInput))
						throw new IllegalValueException("Your selected email address is already in use.");
				}

				if (settings.getLoginLabelString().equals("Username"))
				{
					if (changing_username)
					{
						if (usernameInput.length() == 0)
							throw new IllegalValueException("Please provide a Username.");

						if (UKOnlinePersonBean.usernameExists(usernameInput))
							throw new IllegalValueException("Your selected username is already in use.");
					}
				}

				if (password.length() == 0)
					throw new IllegalValueException("Please provide a Password.");

				staffPerson.setUsername((!settings.getLoginLabelString().equals("Username")) ? emailInput : usernameInput);
				staffPerson.setEmail(emailInput);
				staffPerson.setPassword(password);
				staffPerson.setConfirmPassword(password);
				staffPerson.save();

				// assign roles

				staffPerson.saveRoles();

				// assign the cashier to the default department

				DepartmentBean default_dept = adminCompany.getDefaultDepartment();

				staffPerson.setDepartment(default_dept);
				staffPerson.save();

				UKOnlinePersonBean.invalidateCashiers(adminCompany);
				adminCompany.invalidateCache();
				session.removeAttribute("adminStaff");

				forward_string = "add";
			}
			else if (submit_button.equals("next"))
			{
				forward_string = "next";
			}
			else if (submit_button.equals("previous"))
			{
				forward_string = "previous";
			}

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