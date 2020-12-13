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
ExtCourseEnrollAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in ExtCourseEnrollAction");

		String forwardString = "success";

		ActionErrors errors = new ActionErrors();

		PersonBean personBean = null;
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
						if (!(person.hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME) ||
								person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME)))
						{
							return (_mapping.findForward("session_expired"));
						}
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				personBean = (PersonBean)session.getAttribute("adminPerson");
				if (personBean == null)
				{
					personBean = new PersonBean();
					session.setAttribute("adminPerson", personBean);
				}

				/*
				loginBean = (LoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
				{
					loginBean = new LoginBean();
					session.setAttribute("loginBean", loginBean);
				}
				loginBean.setUsername("admin");
				loginBean.setPassword("spork");
				loginBean.getPerson();
					// This actually does the login.
				 */
			}
			else
				return (_mapping.findForward("session_expired"));

			String courseSelect = (String)PropertyUtils.getSimpleProperty(_form, "courseSelect");

			/*
			if (!assessmentSelect.equals("0"))
			{

				//courseBean..setPersonTitle(jobTitleSelect);

				AssessmentBean assessment = AssessmentBean.getAssessment(Integer.parseInt(assessmentSelect));
				System.out.println("ASSESSMENT FOUND >" + assessment.getNameString());
				certBean.setAssessment(assessment, mustBeCompleted);
			}
			 */

			//audience.save();
			System.out.println("~~~~~~~~~~~~~ASSESSMENT ASSIGNED TO COURSE >" + courseSelect);
			System.out.println("~~~~~~~~~~~~~ASSESSMENT ASSIGNED TO COURSE >" + _request.getParameter("courseDetails"));
			System.out.println("~~~~~~~~~~~~~ASSESSMENT ASSIGNED TO COURSE >" + _request.getParameter("courseEnroll"));

			CourseBean course = null;
			if (!courseSelect.equals("0"))
				course = CourseBean.getCourse(Integer.parseInt(courseSelect));

			if (_request.getParameter("courseDetails") != null)
			{
				session.setAttribute("adminCourse", course);
				forwardString = "detail";
			}
			else if (_request.getParameter("courseEnroll") != null)
			{
				personBean.enroll(loginBean.getPerson(), course);
			}
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
		return (_mapping.findForward(forwardString));
	}
}