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
CourseAudienceAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseAudienceAction");

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

			Integer audienceSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "audienceSelect");
			String courseIsSelect = (String)PropertyUtils.getSimpleProperty(_form, "courseIsSelect");
			String completeByDateInputString = (String)PropertyUtils.getSimpleProperty(_form, "completeByDateInput");


			
			adminCourseAudience.setAudience(AudienceBean.getAudience(audienceSelect.intValue()));
			adminCourseAudience.setCourse(adminCourse);
			adminCourseAudience.setRequirementType(courseIsSelect);
			
			if ((completeByDateInputString != null) && (completeByDateInputString.length() > 0))
			{
			    Calendar today = Calendar.getInstance();
			    today.set(Calendar.HOUR_OF_DAY, 0);
			    today.set(Calendar.MINUTE, 0);
			    today.set(Calendar.SECOND, 0);
			    today.set(Calendar.MILLISECOND, 0);
			    Date today_date = today.getTime();
			    Date complete_by_date = CUBean.getDateFromUserString(completeByDateInputString);
			    if (complete_by_date.before(today_date))
				throw new IllegalValueException("You cannot use a date in the past for start in date.");
			    
				adminCourseAudience.setCompleteByDate(complete_by_date);
				
				adminCourseAudience.setAvailableHolidays(true);
			}
			else
			{
			    // MySQL is setting this to today's date even when nothing is provided.  force this to some stupid value to indicate that no date was specified...
			    
			    Calendar my_birthday = Calendar.getInstance();
			    my_birthday.set(Calendar.YEAR, 1970);
			    my_birthday.set(Calendar.MONTH, Calendar.DECEMBER);
			    my_birthday.set(Calendar.DATE, 2);
			    my_birthday.set(Calendar.HOUR_OF_DAY, 0);
			    my_birthday.set(Calendar.MINUTE, 0);
			    my_birthday.set(Calendar.SECOND, 0);
			    my_birthday.set(Calendar.MILLISECOND, 0);
			    
			    adminCourseAudience.setCompleteByDate(my_birthday.getTime());
			    
			    adminCourseAudience.setAvailableHolidays(false);
			}
			
			/*
			audience.setCourseReqType(courseIsSelect);
			if (completeByDateInputString != null && completeByDateInputString.length() > 0)
				audience.setCompleteByDate(CUBean.getDateFromUserString(completeByDateInputString));
			audience.setCourse(courseBean);
			 */
			
			

			adminCourseAudience.save();
			
			adminCourse.invalidate();


			System.out.println("AUDIENCE SAVED/CREATED >" + adminCourseAudience.getLabel());
			
			session.removeAttribute("adminCourseAudience");
		}
        catch (IllegalValueException x)
        {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}
		/*
		catch (ParseException x)
		{
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}
		 */
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