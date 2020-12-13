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
CourseSectionAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseSectionAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		CourseBean adminCourse = null;
		CourseSectionBean adminCourseSection = null;
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
				
				adminCompany = (CompanyBean)session.getAttribute("adminCompany");
				adminCourse = (CourseBean)session.getAttribute("adminCourse");
				adminCourseSection = (CourseSectionBean)session.getAttribute("adminCourseSection");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-bean       name="courseSectionForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="status" type="java.lang.Integer"/>
      <form-property name="room" type="java.lang.Integer"/>
      <form-property name="capacity" type="java.lang.Short"/>
      <form-property name="enable_waitlisting" type="java.lang.Boolean"/>
      
      <form-property name="allow_drop" type="java.lang.Boolean"/>
      <form-property name="require_drop_reason" type="java.lang.Boolean"/>
      
      <form-property name="language" type="java.lang.Integer"/>
      <form-property name="instructors" type="java.lang.Integer[]"/>
      <form-property name="instructor_comment" type="java.lang.String"/>
      <form-property name="schedule" type="java.lang.String"/>
      <form-property name="comment" type="java.lang.String"/>
      
      <form-property name="section_start_date" type="java.lang.String"/>
      <form-property name="section_end_date" type="java.lang.String"/>
      <form-property name="section_drop_deadline_date" type="java.lang.String"/>
    </form-bean>
    
    <form-bean       name="courseSectionPeriodForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      
      <form-property name="day_of_week" type="java.lang.Integer"/>
      <form-property name="recurring" type="java.lang.Boolean"/>
      
      <form-property name="period_start_date" type="java.lang.String"/>
      <form-property name="period_end_date" type="java.lang.String"/>
      
      <form-property name="completeByDateInput" type="java.lang.String"/>
    </form-bean>
			 */

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			Integer status = (Integer)PropertyUtils.getSimpleProperty(_form, "status");
			Integer room = (Integer)PropertyUtils.getSimpleProperty(_form, "room");
			Short capacity = (Short)PropertyUtils.getSimpleProperty(_form, "capacity");
			Boolean enable_waitlisting = (Boolean)PropertyUtils.getSimpleProperty(_form, "enable_waitlisting");
			Boolean allow_drop = (Boolean)PropertyUtils.getSimpleProperty(_form, "allow_drop");
			Boolean require_drop_reason = (Boolean)PropertyUtils.getSimpleProperty(_form, "require_drop_reason");
			Integer language = (Integer)PropertyUtils.getSimpleProperty(_form, "language");
			Integer[] instructors = (Integer[])PropertyUtils.getSimpleProperty(_form, "instructors");
			String instructor_comment = (String)PropertyUtils.getSimpleProperty(_form, "instructor_comment");
			String schedule = (String)PropertyUtils.getSimpleProperty(_form, "schedule");
			String comment = (String)PropertyUtils.getSimpleProperty(_form, "comment");
			
			String section_start = (String)PropertyUtils.getSimpleProperty(_form, "section_start_date");
			String section_end = (String)PropertyUtils.getSimpleProperty(_form, "section_end_date");
			String section_drop_deadline_date = (String)PropertyUtils.getSimpleProperty(_form, "section_drop_deadline_date");
			
			System.out.println("nameInput >" + nameInput);
			System.out.println("status >" + status);
			System.out.println("room >" + room);
			System.out.println("capacity >" + capacity);
			System.out.println("enable_waitlisting >" + enable_waitlisting);
			System.out.println("allow_drop >" + allow_drop);
			System.out.println("require_drop_reason >" + require_drop_reason);
			System.out.println("language >" + language);
			System.out.println("instructors >" + instructors);
			System.out.println("instructor_comment >" + instructor_comment);
			System.out.println("schedule >" + schedule);
			System.out.println("comment >" + comment);
			System.out.println("section_start_date >" + section_start);
			System.out.println("section_end_date >" + section_end);
			System.out.println("section_drop_deadline_date >" + section_drop_deadline_date);
			
			adminCourseSection.setName(nameInput);
			if (status != null)
				adminCourseSection.setStatus(CourseSectionStatusBean.getStatus(status.intValue()));
			if (room.intValue() > 0)
				adminCourseSection.setRoom(ClassroomBean.getClassroom(room.intValue()));
			adminCourseSection.setCapacity(capacity.shortValue());
			
			/*
			if (enable_waitlisting == null)
			    adminCourseSection.setAllowWaitinglisting(false);
			else
			    adminCourseSection.setAllowWaitinglisting(enable_waitlisting.booleanValue());
			
			if (allow_drop == null)
			    adminCourseSection.setAllowDrop(false);
			else
			    adminCourseSection.setAllowDrop(allow_drop.booleanValue());
			
			if (require_drop_reason == null)
			    adminCourseSection.setRequiresReasonForDrop(false);
			else
			    adminCourseSection.setRequiresReasonForDrop(require_drop_reason.booleanValue());
			 */
			
			adminCourseSection.clearInstructors();
			for (int i = 0; i < instructors.length; i++)
			{
				UKOnlinePersonBean instructor = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(instructors[i].intValue());
				adminCourseSection.addInstructor(instructor);
			}
			adminCourseSection.setInstructorComment(instructor_comment);
			adminCourseSection.setScheduleComment(schedule);
			adminCourseSection.setComment(comment);
			
			Date section_start_date = null;
			if (!section_start.equals(""))
			{
			    section_start_date = CUBean.getDateFromUserString(section_start);
			    adminCourseSection.setStartDate(section_start_date);
			}
			
			Date section_end_date = null;
			if (!section_end.equals(""))
			{
			    section_end_date = CUBean.getDateFromUserString(section_end);
			    adminCourseSection.setEndDate(section_end_date);
			}
			
			Date drop_dead_date = null;
			if (!section_drop_deadline_date.equals(""))
			{
			    drop_dead_date = CUBean.getDateFromUserString(section_drop_deadline_date);
			    adminCourseSection.setDropDeadline(drop_dead_date);
			}
			
			adminCourseSection.setLanguage(LanguageBean.getLanguage(language.intValue()));
			adminCourseSection.setCourse(adminCourse);
			
			
			if (nameInput.equals(""))
				throw new IllegalValueException("Please specify a name or number for this class section.");
			if (status == null)
				throw new IllegalValueException("Please specify a status for this class section.  You likely need to define new section status entries.");
			if (room.intValue() == 0)
				throw new IllegalValueException("Please specify a room for this class section.");
			if (section_start.equals(""))
				throw new IllegalValueException("Please specify a section start date.");
			if (section_end.equals(""))
				throw new IllegalValueException("Please specify a section end date.");
			
			
			adminCourseSection.setCreateOrModifyPerson(loginBean.getPerson());
			adminCourseSection.save();
			
			session.removeAttribute("adminCourseSection");

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