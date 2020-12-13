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
UserProfileEditAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in UserProfileEditAction");
		
		ActionErrors errors = new ActionErrors();
		
		UKOnlineLoginBean loginBean = null;
		UKOnlinePersonBean adminPerson = null;
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
				
				adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *      <form-bean       name="userProfileEditForm"
			 type="org.apache.struts.validator.DynaValidatorForm">
	  <form-property name="username" type="java.lang.String"/>
	  <form-property name="password" type="java.lang.String"/>
	  <form-property name="confirm_password" type="java.lang.String"/>
	  <form-property name="first_name" type="java.lang.String"/>
	  <form-property name="last_name" type="java.lang.String"/>
	  <form-property name="employee_number" type="java.lang.String"/>
	  <form-property name="email" type="java.lang.String"/>
	  <form-property name="user_group" type="java.lang.Integer"/>
	  <form-property name="department" type="java.lang.Integer"/>
	  <form-property name="job_title" type="java.lang.Integer"/>
	  <form-property name="managers" type="java.lang.Integer[]"/>
	</form-bean>
			 */
			
			String username = (String)PropertyUtils.getSimpleProperty(_form, "username");
			String password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			String confirm_password = (String)PropertyUtils.getSimpleProperty(_form, "confirm_password");
			String first_name = (String)PropertyUtils.getSimpleProperty(_form, "first_name");
			String last_name = (String)PropertyUtils.getSimpleProperty(_form, "last_name");
			String employee_number = (String)PropertyUtils.getSimpleProperty(_form, "employee_number");
			String email = (String)PropertyUtils.getSimpleProperty(_form, "email");
			Integer user_group = (Integer)PropertyUtils.getSimpleProperty(_form, "user_group");
			Integer department = (Integer)PropertyUtils.getSimpleProperty(_form, "department");
			Integer job_title = (Integer)PropertyUtils.getSimpleProperty(_form, "job_title");
			Integer[] managers = (Integer[])PropertyUtils.getSimpleProperty(_form, "managers");
			
			adminPerson.setUsername(username);
			if ((password != null) && !password.equals(""))
				adminPerson.setPassword(password);
			if ((confirm_password != null) && !confirm_password.equals(""))
				adminPerson.setConfirmPassword(confirm_password);
			adminPerson.setFirstName(first_name);
			adminPerson.setLastName(last_name);
			adminPerson.setEmployeeId(employee_number);
			adminPerson.setEmail(email);
			
			
			if (department.intValue() > 0)
				adminPerson.setDepartment(DepartmentBean.getDepartment(department.intValue()));
			else
				throw new IllegalValueException("Please select a department.");
			if (job_title.intValue() > 0)
				adminPerson.setTitle(PersonTitleBean.getPersonTitle(job_title.intValue()));
			
			adminPerson.save();
			
			for (int i = 0; i < managers.length; i++)
				adminPerson.setSupervisor(UKOnlinePersonBean.getPerson(managers[i].intValue()), PersonBean.MANAGER_SUPERVISOR_TYPE);
			
			if (user_group.intValue() > 0)
				adminPerson.setGroup(PersonGroupBean.getPersonGroup(user_group.intValue()));
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