/*
 * NewActivityAction.java
 *
 * Created on March 4, 2007, 10:06 AM
 *
 * To change this template, choose Tools | Template Manager
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
 * Implementation of <strong>Action</strong> that validates a course.
 *
 * @author Marlo Stueve
 * @version $Revision: 1.2 $ $Date: 2009/07/17 16:23:52 $
 */
public final class
NewActivityAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in NewActivityAction");

		ActionErrors errors = new ActionErrors();

		CompanyBean adminCompany = null;
		CourseBean courseBean = null;
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
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) && !person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
							return (_mapping.findForward("session_expired"));
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
					
					courseSearch = (UKOnlineCourseSearchBean)session.getAttribute("courseSearch");
				}
				
				/*
				 *    <form-bean       name="newActivityForm"
						     type="org.apache.struts.validator.DynaValidatorForm">
				      <form-property name="titleInput" type="java.lang.String"/>
				      <form-property name="groupSelect" type="java.lang.Integer"/>
				      <form-property name="typeSelect" type="java.lang.String"/>
				      <form-property name="descriptionInput" type="java.lang.String"/>
				      <form-property name="contactSelect" type="java.lang.Integer"/>
				    </form-bean>
				 */

				
				String titleInput = (String)PropertyUtils.getSimpleProperty(_form, "titleInput");
				Integer groupSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "groupSelect");
				String typeSelect = (String)PropertyUtils.getSimpleProperty(_form, "typeSelect");
				String descriptionInput = (String)PropertyUtils.getSimpleProperty(_form, "descriptionInput");
				Integer contactSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "contactSelect");
				
				if (typeSelect.equals(UKOnlineCourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME))
				{
				    courseBean = new SCORMCourseBean();
				    courseBean.setSCORMCompliant(true);
				    session.setAttribute("adminCourse", courseBean);
				}
				else
				    courseBean = (CourseBean)session.getAttribute("adminCourse");
				
				adminCompany = (CompanyBean)session.getAttribute("adminCompany");

				courseBean.setName(titleInput);
				courseBean.setComment(descriptionInput);

				courseBean.setStatus(CourseBean.IN_DEVELOPMENT_COURSE_STATUS);
				
				courseBean.setVisibility(CourseBean.AUDIENCE_COURSE_VISIBILITY);
				
				if (courseBean.isNew())
					courseBean.setResourceType(ResourceBean.UNKNOWN_RESOURCE_TYPE);
				
				if (courseBean.isNew())
					courseBean.setOwnerPerson(loginBean.getPerson());
				
				courseBean.setCreatePerson(loginBean.getPerson());
				courseBean.setModifyPerson(loginBean.getPerson());
				
				if (contactSelect.intValue() > 0)
				    courseBean.setContactPerson(UKOnlinePersonBean.getPerson(contactSelect.intValue()));

				courseBean.setCategory(CategoryBean.getDefaultCategory(adminCompany));
				
				
				
				
				if (typeSelect.equals("Course"))
				{
				    if (groupSelect.intValue() > 0)
				    {
					CourseGroupBean parent_group = CourseGroupBean.getCourseGroup(groupSelect.intValue());
					if (!parent_group.getTypeString().equals("Level"))
					    throw new IllegalValueException("Parent group must be Level type.");
					courseBean.setCourseGroup(parent_group);
				    }
				    else
					throw new IllegalValueException("You must choose a level for this course.");
				}
				    
				
				
				courseBean.setType(typeSelect);
				
				courseBean.setCompany(adminCompany);
				
				Calendar now = Calendar.getInstance();
				courseBean.setReleasedDate(now.getTime());
				now.add(Calendar.YEAR, 1);
				courseBean.setOffDate(now.getTime());

				courseBean.save();
				
				if (!typeSelect.equals("Course"))
				{
				    // assume that this is an activity.  must have a course parent - not a course group...

				    if (groupSelect.intValue() > 0)
				    {
					CourseBean parent_course = CourseBean.getCourse(groupSelect.intValue());
					courseBean.setParent(parent_course);
				    }
				}
				
				session.setAttribute("adminCourse", courseBean);
				
				courseSearch.invalidateSearchResults();
			}
			else
				return (_mapping.findForward("session_expired"));
		}
        catch (IllegalValueException x)
        {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}
		catch (ParseException x)
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
		return (_mapping.findForward("success"));
	}
}