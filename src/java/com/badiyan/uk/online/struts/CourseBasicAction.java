package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.ParseException;
import java.util.*;
import java.io.*;

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
CourseBasicAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseBasicAction");

		ActionErrors errors = new ActionErrors();

		CompanyBean adminCompany = null;
		CourseBean courseBean = null;
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


				String titleInput = (String)PropertyUtils.getSimpleProperty(_form, "titleInput");
				Integer categorySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "categorySelect");
				Integer groupSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "groupSelect");
				String typeSelect = (String)PropertyUtils.getSimpleProperty(_form, "typeSelect");
				String descriptionInput = (String)PropertyUtils.getSimpleProperty(_form, "descriptionInput");
				Integer durationHoursInput = (Integer)PropertyUtils.getSimpleProperty(_form, "durationHoursInput");
				Integer durationMinutesInput = (Integer)PropertyUtils.getSimpleProperty(_form, "durationMinutesInput");
				Integer creditsInput = (Integer)PropertyUtils.getSimpleProperty(_form, "creditsInput");
				Integer interoperabilitySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "interoperabilitySelect");

				/*
				Integer rewardsSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "rewardsSelect");
				String rewardDescInput = (String)PropertyUtils.getSimpleProperty(_form, "rewardDescInput");
				Integer surveySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "surveySelect");
				String surveyURLInput = (String)PropertyUtils.getSimpleProperty(_form, "surveyURLInput");
				 */

				Integer statusSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "statusSelect");
				Integer visibilitySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "visibilitySelect");
				Boolean scheduleActive = (Boolean)PropertyUtils.getSimpleProperty(_form, "scheduleActive");
				Boolean displayAsNew = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayAsNew");
				Integer displayAsNewInput = (Integer)PropertyUtils.getSimpleProperty(_form, "displayAsNewInput");

				Integer contactSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "contactSelect");
				String updateNotesInput = (String)PropertyUtils.getSimpleProperty(_form, "updateNotesInput");

				courseBean = (CourseBean)session.getAttribute("adminCourse");
				adminCompany = (CompanyBean)session.getAttribute("adminCompany");

				boolean aicc_compliant = false;
				boolean scorm_compliant = false;
				if (interoperabilitySelect != null)
				{
				    aicc_compliant = (interoperabilitySelect.intValue() == 1) ? true : false;
				    scorm_compliant = (interoperabilitySelect.intValue() == 2) ? true : false;
				}

				if (aicc_compliant)
				{
					if (!(courseBean instanceof AICCCourseBean))
					{
						if (courseBean.isNew())
							courseBean = new AICCCourseBean();
						else
						{
							int course_id = courseBean.getId();
							courseBean = AICCCourseBean.getCourse(course_id);
						}
					}
				}
				else if (scorm_compliant)
				{
					if (!(courseBean instanceof SCORMCourseBean))
					{
						if (courseBean.isNew())
							courseBean = new SCORMCourseBean();
						else
						{
							int course_id = courseBean.getId();
							courseBean = SCORMCourseBean.getCourse(course_id);
						}
					}
				}
				else
				{

				}


				courseBean.setName(titleInput);
				courseBean.setCreatePerson(loginBean.getPerson());
				courseBean.setComment(descriptionInput);
				courseBean.setDurationHours(durationHoursInput);
				courseBean.setDurationMinutes(durationMinutesInput);
				courseBean.setCredits(creditsInput);

				courseBean.setAICCCompliant(aicc_compliant);
				courseBean.setSCORMCompliant(scorm_compliant);

				/*
				if (rewardsSelect.intValue() == 1)
					courseBean.setSummary(rewardDescInput);
				else
					courseBean.setSummary("");

				if (surveySelect.intValue() == 1)
					courseBean.setCompletionRequirements(surveyURLInput);
				else
					courseBean.setCompletionRequirements("");
				 */

				if (statusSelect.intValue() == 0)
					courseBean.setStatus(CourseBean.IN_DEVELOPMENT_COURSE_STATUS);
				else if (statusSelect.intValue() == 1)
					courseBean.setStatus(CourseBean.INACTIVE_COURSE_STATUS);
				else if (statusSelect.intValue() == 2)
					courseBean.setStatus(CourseBean.DEFAULT_COURSE_STATUS);

				/*
				if (visibilitySelect.intValue() == 0)
					courseBean.setVisibility(CourseBean.AUDIENCE_COURSE_VISIBILITY);
				else if (visibilitySelect.intValue() == 1)
					courseBean.setVisibility(CourseBean.ORGANIZATION_COURSE_VISIBILITY);
				else if (visibilitySelect.intValue() == 2)
					courseBean.setVisibility(CourseBean.PUBLIC_COURSE_VISIBILITY);
				 */

				Date releasedDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "releasedDateInput"));
				courseBean.setReleasedDate(releasedDateInput);

				if (scheduleActive != null)
				{
					if (scheduleActive.booleanValue())
					{
						Date activeDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "activeDateInput"));
						courseBean.setBroadcastDate(activeDateInput);
					}
				}

				if (displayAsNew != null)
				{
					if (displayAsNew.booleanValue())
						courseBean.setDisplayAsNewFor(displayAsNewInput.shortValue());
					else
						courseBean.setDisplayAsNewFor((short)0);
				}
				else
					courseBean.setDisplayAsNewFor((short)0);

				Date expiresDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "expiresDateInput"));
				courseBean.setOffDate(expiresDateInput);
				if (courseBean.isNew())
					courseBean.setResourceType(ResourceBean.UNKNOWN_RESOURCE_TYPE);
				if (courseBean.isNew())
					courseBean.setOwnerPerson(loginBean.getPerson());
				courseBean.setModifyPerson(loginBean.getPerson());
				if (courseBean.isNew())
					courseBean.setContactPerson(loginBean.getPerson());





				/*
				if (groupSelect.intValue() > 0)
				{
				    CourseGroupBean parent_group = CourseGroupBean.getCourseGroup(groupSelect.intValue());
				    if (!parent_group.getTypeString().equals("Course"))
					throw new IllegalValueException("Parent group must be Course type.");
				    courseBean.setCourseGroup(parent_group);
				}
				else
				    courseBean.setCourseGroupId(0);
				 */




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
				else
				{
				    // assume that this is an activity.  must have a course parent - not a course group...

				    if (groupSelect.intValue() > 0)
				    {
					CourseBean parent_course = CourseBean.getCourse(groupSelect.intValue());
					courseBean.setParent(parent_course);
				    }
				}





				courseBean.setType(typeSelect);

				courseBean.setCompany(adminCompany);

				if ((contactSelect != null) && (contactSelect.intValue() > 0))
				    courseBean.setOwnerPerson(PersonBean.getPerson(contactSelect.intValue()));
				courseBean.setEnrollmentEmailMessage(updateNotesInput);



				courseBean.save();

				/*
				 *<form-property name="passFailPercent" type="java.lang.String"/>
      <form-property name="allowRetakes" type="java.lang.Boolean"/>
      <form-property name="introduction" type="java.lang.String"/>
      <form-property name="instructions" type="java.lang.String"/>
				 */

				if (courseBean.getType().equals(com.badiyan.uk.online.beans.CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME))
				{
				    String passFailPercent = (String)PropertyUtils.getSimpleProperty(_form, "passFailPercent");
				    Boolean allowRetakes = (Boolean)PropertyUtils.getSimpleProperty(_form, "allowRetakes");
				    String introduction = (String)PropertyUtils.getSimpleProperty(_form, "introduction");
				    String instructions = (String)PropertyUtils.getSimpleProperty(_form, "instructions");

				    // find any existing assessment for this course...

				    try
				    {
					AssessmentBean assessment = null;
					if (courseBean.hasAssessment())
					{
					    assessment = courseBean.getAssessment();
					}
					else
					{
					    assessment = new AssessmentBean();
					}

					assessment.setCompany(adminCompany);
					assessment.setName(courseBean.getLabel());
					try
					{
					    assessment.setPassFailPercentile(Short.parseShort(passFailPercent));
					}
					catch (NumberFormatException numFormEx)
					{
					}
					assessment.setIntroduction(introduction);
					assessment.setIntructions(instructions);
					assessment.setOwner(loginBean.getPerson());
					assessment.setActive(true);
					if (allowRetakes != null)
						assessment.setAllowRetake(allowRetakes.booleanValue());
					else
						assessment.setAllowRetake(false);
					assessment.save();

					Boolean mustBeCompleted = new Boolean(true);

					courseBean.setAssessment(assessment, mustBeCompleted);
				    }
				    catch (Exception x)
				    {
					x.printStackTrace();
				    }
				}

				// course CD stuff



				/*
				String realPath = CUBean.getProperty("cu.realPath");
				String coursesFolder = CUBean.getProperty("cu.coursesFolder");
				if (coursesFolder == null)
					throw new IllegalValueException("Property not defined: cu.coursesFolder");

				CUBean.createDirectory(realPath, coursesFolder + courseBean.getIdString());
					// ensure that an upload directory exists for this course

				Vector cd_sync_objects = courseBean.getCreateFromCDSyncObjects();

				String sync_file_name = realPath + coursesFolder + courseBean.getIdString() + "/sync.data";
				File sync_file = new File(sync_file_name);
				FileOutputStream outStream = new FileOutputStream(sync_file);
				ObjectOutputStream objStream = new ObjectOutputStream(outStream);
				objStream.writeObject(cd_sync_objects);
				objStream.flush();
				outStream.flush();
				objStream.close();
				outStream.close();
				 */



				//ZipFile zip = new ZipFile(sync_file_name);
				//zip.


				session.setAttribute("adminCourse", courseBean);
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