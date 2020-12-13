/*
 * RegistrationAction.java
 *
 * Created on September 28, 2006, 2:04 PM
 *
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
RegistrationAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in RegistrationAction");

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
			 *<form-property name="username" type="java.lang.String"/>
      <form-property name="password" type="java.lang.String"/>
      <form-property name="confirmPassword" type="java.lang.String"/>
      <form-property name="firstName" type="java.lang.String"/>
      <form-property name="lastName" type="java.lang.String"/>
      <form-property name="email" type="java.lang.String"/>
			 */

			String username = (String)PropertyUtils.getSimpleProperty(_form, "username");
			
			/*
			String password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			String confirmPassword = (String)PropertyUtils.getSimpleProperty(_form, "confirmPassword");
			String firstName = (String)PropertyUtils.getSimpleProperty(_form, "firstName");
			String lastName = (String)PropertyUtils.getSimpleProperty(_form, "lastName");
			String email = (String)PropertyUtils.getSimpleProperty(_form, "email");
			 */

			
			//System.out.println("Employee ID >" + username);
			
			/*
			System.out.println("password >" + password);
			System.out.println("confirmPassword >" + confirmPassword);
			System.out.println("firstName >" + firstName);
			System.out.println("lastName >" + lastName);
			System.out.println("email >" + email);
			 */
			
			// ensure that no tablet user already exits
			
			/*
			if (!CUBean.isMasterServer())
			{
			    // check to see if there are any user records for this user   

			    boolean tablet_user_exists = UKOnlinePersonBean.tabletUserExists();
			    if (!tablet_user_exists)
			    {
					// create the user

					UKOnlinePersonBean adminPerson = new UKOnlinePersonBean();
					adminPerson.setUsername(username);
					adminPerson.setEmployeeId(username);
					adminPerson.setPassword("spork");
					adminPerson.setConfirmPassword("spork");

					adminPerson.setFirstName("");
					adminPerson.setLastName("");
					//adminPerson.setEmail("");

					adminPerson.setTitle(PersonTitleBean.getPersonTitle());
					adminPerson.setPersonType(UKOnlinePersonBean.TABLET_USER_PERSON_TYPE);
					adminPerson.save();

					// assign company admin role

					RoleBean student = RoleBean.getRole(RoleBean.STUDENT_ROLE_NAME);
					adminPerson.addRole(student);

					// find the default department in the default company
			    }
			    else
			    {
					UKOnlinePersonBean adminPerson = UKOnlinePersonBean.getTabletUser();
					if (!adminPerson.isServerApproved())
					{
						// if the person is already approved by the server don't allow changes to Employee ID

						adminPerson.setUsername(username);
						adminPerson.save();
					}
					else
						throw new IllegalValueException("User already approved by server.  Cannot change Employee ID.");
			    }
			    
			    // now log the tablet user in

			    loginBean.setUsername(username);
			    loginBean.setPassword("spork");
			    loginBean.getPerson();
			    
			    //UKOnlineLoginBean.processExternalContent();
			    
			    
			}
			 */
		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage().replaceAll("\"", "")));
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