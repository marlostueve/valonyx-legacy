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
OrganizationRegistrationAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in OrganizationRegistrationAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
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
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-property name="organization" type="java.lang.String"/>
      <form-property name="username" type="java.lang.String"/>
      <form-property name="password" type="java.lang.String"/>
      <form-property name="confirmPassword" type="java.lang.String"/>
      <form-property name="firstName" type="java.lang.String"/>
      <form-property name="lastName" type="java.lang.String"/>
      <form-property name="email" type="java.lang.String"/>
			 */

			String organization = (String)PropertyUtils.getSimpleProperty(_form, "organization");
			String username = (String)PropertyUtils.getSimpleProperty(_form, "username");
			String password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			String confirmPassword = (String)PropertyUtils.getSimpleProperty(_form, "confirmPassword");
			String firstName = (String)PropertyUtils.getSimpleProperty(_form, "firstName");
			String lastName = (String)PropertyUtils.getSimpleProperty(_form, "lastName");
			String email = (String)PropertyUtils.getSimpleProperty(_form, "email");

			System.out.println("organization >" + organization);
			System.out.println("username >" + username);
			System.out.println("password >" + password);
			System.out.println("confirmPassword >" + confirmPassword);
			System.out.println("firstName >" + firstName);
			System.out.println("lastName >" + lastName);
			System.out.println("email >" + email);
			
			// create the admin user
			
			UKOnlinePersonBean adminPerson = new UKOnlinePersonBean();
			adminPerson.setUsername(username);
			adminPerson.setPassword(password);
			adminPerson.setConfirmPassword(confirmPassword);
			adminPerson.setFirstName(firstName);
			adminPerson.setLastName(lastName);
			adminPerson.setEmail(username);
			adminPerson.save();
			
			// assign company admin role
			
			RoleBean adminRole = RoleBean.getRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);
			adminPerson.addRole(adminRole);
			
			// create the organization
			
			UKOnlineCompanyBean company = new UKOnlineCompanyBean();
			company.setName(organization);
			company.save();
			session.setAttribute("adminCompany", company);
			
			CourseGroupBean.maintainCourseGroupType(company, "Level");
			CourseGroupBean.maintainCourseGroupType(company, "Course");
			
			company.getDefaultDepartment();
			PersonTitleBean.maintainDefaultData(company);
			PersonGroupBean.maintainGroup(company, PersonGroupBean.DEFAULT_PERSON_GROUP_NAME);
			
			
			// associate this person as the admin of the new organization
			
			CompanyAdministratorMapBean map = new CompanyAdministratorMapBean();
			map.setCompany(company);
			map.setPerson(adminPerson);
			map.save();

			// now log the new person in
			
			loginBean.setUsername(username);
			loginBean.setPassword(password);
			loginBean.getPerson();
			

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