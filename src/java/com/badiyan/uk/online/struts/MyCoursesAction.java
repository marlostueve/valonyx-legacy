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
MyCoursesAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in MyCoursesAction");

		String forwardString = "success";

		ActionErrors errors = new ActionErrors();

		PersonBean personBean = null;
		UKOnlineLoginBean loginBean = null;
		UKOnlineCourseSearchBean courseSearch = null;
		
		
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
								person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) ||
								person.hasRole(UKOnlineRoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME)))
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
				courseSearch = (UKOnlineCourseSearchBean)session.getAttribute("courseSearch");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			
			HashMap enrolled_courses = new HashMap(11);
			Iterator itr = personBean.getEnrollments().iterator();
			while (itr.hasNext())
			{
			    EnrollmentBean enrollment = (EnrollmentBean)itr.next();
			    CourseBean course = enrollment.getCourse();
			    enrolled_courses.put(course, enrollment);
			}
			
			HashMap grouped_courses = new HashMap(11);
			
			// scan activity enrollments
			
			Enumeration courses = courseSearch.getList();
			while (courses.hasMoreElements())
			{
			    CourseBean course = (CourseBean)courses.nextElement();
			    
			    CourseGroupBean course_group = course.getCourseGroup();
			    Vector vec = (Vector)grouped_courses.get(course_group);
			    if (vec == null)
			    {
				vec = new Vector();
				grouped_courses.put(course_group, vec);
			    }
			    vec.addElement(course);
			    
			    String assign_string = _request.getParameter("assign_" + course.getId());
			    String started_string = _request.getParameter("started_" + course.getId());
			    String completed_string = _request.getParameter("completed_" + course.getId());
			    
			    System.out.println("assign_" + course.getId() + " >" + _request.getParameter("assign_" + course.getId()));
			    System.out.println("started_" + course.getId() + " >" + _request.getParameter("started_" + course.getId()));
			    System.out.println("completed_" + course.getId() + " >" + _request.getParameter("completed_" + course.getId()));
			    
			    if (assign_string != null)
			    {
				EnrollmentBean enrollment = personBean.getEnrollment(course, true);
				
				if (started_string.length() > 0)
				{
				    enrollment.setEnrollmentDate(CUBean.getDateFromUserString(started_string));
				    
				    if (completed_string.length() > 0)
				    {
					enrollment.setCompletionDate(CUBean.getDateFromUserString(completed_string));
					enrollment.setStatus(EnrollmentBean.COMPLETED_ENROLLMENT_STATUS);
				    }
				    else
					enrollment.setStatus(EnrollmentBean.STARTED_ENROLLMENT_STATUS);
				    
				    enrollment.save();
				}
				else if (completed_string.length() > 0)
				    throw new IllegalValueException("You must specify a started date.");
			    }
			    else
			    {
				// yank the enrollment from the user
				
				try
				{
				    // is this user already enrolled?
				    
				    if (enrolled_courses.containsKey(course))
					personBean.removeEnrollment(course);
				}
				catch (ObjectNotFoundException x)
				{
				    // not enrolled
				}
				
			    }
			}
			
			// scan group enrollments
			
			/*
			Iterator group_itr = grouped_courses.keySet().iterator();
			while (group_itr.hasNext())
			{
			    CourseGroupBean course_group = (CourseGroupBean)group_itr.next();
			    
			    String assign_string = _request.getParameter("assignCourse_" + course_group.getId());
			    
			    System.out.println("assignCourse_" + course_group.getId() + " >" + _request.getParameter("assignCourse_" + course_group.getId()));
			    
			    if (assign_string != null)
			    {
				EnrollmentBean enrollment = personBean.getEnrollment(course, true);
				
				
			    }
			    else
			    {
				// yank the group enrollment from the user
				
				
				
			    }
			}
			 */
			
			personBean.invalidateEnrollments();

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