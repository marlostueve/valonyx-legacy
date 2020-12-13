/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;


import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.math.BigDecimal;
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
Setup02Action
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in Setup02Action");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		UKOnlinePersonBean practitionerPerson = null;
		UKOnlineCompanyBean adminCompany = null;

		HttpSession session = _request.getSession(false);

		String forward_string = "success";

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				practitionerPerson = (UKOnlinePersonBean)session.getAttribute("adminPractitioner");
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
      <form-property name="already_exists" type="java.lang.Boolean"/>
    </form-bean>
			 */

			String emailInput = (String)PropertyUtils.getSimpleProperty(_form, "emailInput");
			String usernameInput = (String)PropertyUtils.getSimpleProperty(_form, "usernameInput");
			String password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			String firstNameInput = (String)PropertyUtils.getSimpleProperty(_form, "firstNameInput");
			String lastNameInput = (String)PropertyUtils.getSimpleProperty(_form, "lastNameInput");
			Boolean is_administrator = (Boolean)PropertyUtils.getSimpleProperty(_form, "is_administrator");
			Boolean already_exists = (Boolean)PropertyUtils.getSimpleProperty(_form, "already_exists");
			
			Boolean allow_client_scheduling = (Boolean)PropertyUtils.getSimpleProperty(_form, "allow_client_scheduling");
			
			

			//String product_commission = (String)PropertyUtils.getSimpleProperty(_form, "product_commission");
			//String service_commission = (String)PropertyUtils.getSimpleProperty(_form, "service_commission");

			System.out.println("emailInput >" + emailInput);
			System.out.println("usernameInput >" + usernameInput);
			System.out.println("password >" + password);
			System.out.println("firstNameInput >" + firstNameInput);
			System.out.println("lastNameInput >" + lastNameInput);
			System.out.println("is_administrator >" + is_administrator);
			System.out.println("already_exists >" + already_exists);
			System.out.println("allow_client_scheduling >" + allow_client_scheduling);

			//System.out.println("product_commission >" + product_commission);
			//System.out.println("service_commission >" + service_commission);

			if ((_request.getParameter("delete_id") != null) && (((String)_request.getParameter("delete_id")).length() > 0) && (Integer.parseInt(_request.getParameter("delete_id")) > 0))
			{
				UKOnlinePersonBean delete_person = UKOnlinePersonBean.getPerson(adminCompany, Integer.parseInt(_request.getParameter("delete_id")));
				//UKOnlinePersonBean.delete(delete_person);
				delete_person.removeRole(RoleBean.getRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME));

				UKOnlinePersonBean.invalidatePractitioners(adminCompany);
				adminCompany.invalidateCache();
				session.removeAttribute("adminPractitioner");

				forward_string = "add";
			}
			else if (submit_button.equals("Add Practitioner"))
			{
				CompanySettingsBean settings = adminCompany.getSettings();

				// create the practitioner
				
				boolean does_already_exist = (already_exists != null) && already_exists.booleanValue();
				if (does_already_exist)
				{
					// find the user with the provided email address
					
					if (emailInput.length() > 0) {
						
						practitionerPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPersonByEmail(adminCompany, emailInput);
						session.setAttribute("adminPractitioner", practitionerPerson);
					}
					
				}
				

				boolean changing_email_address = true;
				if (!practitionerPerson.isNew())
				{
					if (practitionerPerson.getEmail1String().equals(emailInput))
						changing_email_address = false;
				}

				boolean changing_username = true;
				if (!practitionerPerson.isNew())
				{
					if (practitionerPerson.getUsernameString().equals(usernameInput))
						changing_username = false;
				}

				practitionerPerson.setUsernameNoVerify((!settings.getLoginLabelString().equals("Username")) ? emailInput : usernameInput);
				practitionerPerson.setPasswordNoVerify(password);
				practitionerPerson.setFirstName(firstNameInput);
				practitionerPerson.setLastName(lastNameInput);
				practitionerPerson.setEmailNoVerify(emailInput);

				Vector roles_vec = new Vector();

				if ((is_administrator != null) && is_administrator.booleanValue())
				{
					RoleBean admin_role = RoleBean.getRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);
					roles_vec.addElement(admin_role);
				}

				RoleBean practitionerRole = (RoleBean)UKOnlineRoleBean.getRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME);
				roles_vec.addElement(practitionerRole);
				practitionerPerson.setRoles(roles_vec);

				if (firstNameInput.length() == 0)
					throw new IllegalValueException("Please provide a First Name.");
				if (lastNameInput.length() == 0)
					throw new IllegalValueException("Please provide a Last Name.");

				if (emailInput.length() == 0)
					throw new IllegalValueException("Please provide an Email Address.");

				if (changing_email_address)
				{
					if (!does_already_exist) {
						if (UKOnlinePersonBean.emailExists(emailInput))
							throw new IllegalValueException("Your selected email address is already in use.");
					}
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

				practitionerPerson.setUsername((!settings.getLoginLabelString().equals("Username")) ? emailInput : usernameInput);
				practitionerPerson.setEmail(emailInput);
				practitionerPerson.setPassword(password);
				practitionerPerson.setConfirmPassword(password);
				
				practitionerPerson.setIsRoom(false);
				
				practitionerPerson.save();

				// assign roles
				
				practitionerPerson.saveRoles();

				// assign the practitioner to the default department

				DepartmentBean default_dept = adminCompany.getDefaultDepartment();

				practitionerPerson.setDepartment(default_dept);
				practitionerPerson.save();

				CompanyAdministratorMapBean.maintainAdministratorStatus(adminCompany, practitionerPerson);

				//if (!product_commission.isEmpty() || !service_commission.isEmpty())
				//	practitionerPerson.setDefaultCommissions(product_commission, service_commission);
				
				Iterator dept_itr = InventoryDepartment.getInventoryDepartments(adminCompany).iterator();
				while (dept_itr.hasNext())
				{
					InventoryDepartment dept_obj = (InventoryDepartment)dept_itr.next();
					String perc_str = (String)_request.getParameter("commission_" + dept_obj.getValue());
					if (!perc_str.isEmpty())
						practitionerPerson.setCommissionPercentage(dept_obj, new BigDecimal(perc_str));
				}
				
						
				practitionerPerson.setAllowClientScheduling((allow_client_scheduling != null) && allow_client_scheduling.booleanValue());


				UKOnlinePersonBean.invalidatePractitioners(adminCompany);
				adminCompany.invalidateCache();
				session.removeAttribute("adminPractitioner");

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