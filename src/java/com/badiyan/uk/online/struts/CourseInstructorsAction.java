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
CourseInstructorsAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseInstructorsAction");

		ActionErrors errors = new ActionErrors();

		CourseAudienceBean adminCourseAudience = null;
		CourseBean adminCourse = null;
		CompanyBean adminCompany = null;
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
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) && !person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
							return (_mapping.findForward("session_expired"));
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				adminCompany = (CompanyBean)session.getAttribute("adminCompany");
				adminCourse = (CourseBean)session.getAttribute("adminCourse");
				adminCourseAudience = (CourseAudienceBean)session.getAttribute("adminCourseAudience");
			}
			else
				return (_mapping.findForward("session_expired"));

			Integer[] instructorSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "instructorSelect");

			Vector instructors = new Vector();
			if (instructorSelect != null)
			{
				adminCourse.removeInstructors();
				
				for (int i = 0; i < instructorSelect.length; i++)
				{
					UKOnlinePersonBean instructor = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(instructorSelect[i]);
					System.out.println("INSTRUCTOR FOUND >" + instructor.getLabel());
					//adminCourse.addInstructor(instructor);
					instructors.addElement(instructor);
				}
			}
			adminCourse.setInstructors(instructors);
			adminCourse.saveInstructors();
			
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