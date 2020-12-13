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
CourseSettingsAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseSettingsAction");
		
		ActionErrors errors = new ActionErrors();
		
		CourseBean courseBean = null;
		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);
		
		try
		{
			// Check the session to see if there's a course in progress...
			
			if (session == null)
				return (_mapping.findForward("session_expired"));
			
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
			
			courseBean = (CourseBean)session.getAttribute("adminCourse");
			if (courseBean == null)
			{
				courseBean = new CourseBean();
				session.setAttribute("adminCourse", courseBean);
			}
			
			
			Boolean allow_waitlisting = (Boolean)PropertyUtils.getSimpleProperty(_form, "allow_waitlisting");
			Boolean require_approval_for_enroll = (Boolean)PropertyUtils.getSimpleProperty(_form, "require_approval_for_enroll");
			Boolean manager_approve_enroll = (Boolean)PropertyUtils.getSimpleProperty(_form, "manager_approve_enroll");
			Boolean instructor_approve_enroll = (Boolean)PropertyUtils.getSimpleProperty(_form, "instructor_approve_enroll");
			String enrolled_email_message = (String)PropertyUtils.getSimpleProperty(_form, "enrolled_email_message");
			
			Boolean allow_drop = (Boolean)PropertyUtils.getSimpleProperty(_form, "allow_drop");
			Boolean require_approval_for_drop = (Boolean)PropertyUtils.getSimpleProperty(_form, "require_approval_for_drop");
			Boolean manager_approve_drop = (Boolean)PropertyUtils.getSimpleProperty(_form, "manager_approve_drop");
			Boolean instructor_approve_drop = (Boolean)PropertyUtils.getSimpleProperty(_form, "instructor_approve_drop");
			Boolean require_reason_for_drop = (Boolean)PropertyUtils.getSimpleProperty(_form, "require_reason_for_drop");
			Boolean allow_enroll_after_drop = (Boolean)PropertyUtils.getSimpleProperty(_form, "allow_enroll_after_drop");
			String drop_course_by_datetime = (String)PropertyUtils.getSimpleProperty(_form, "drop_course_by_datetime");
			
		/*
		System.out.println("require_approval_for_enroll >" + require_approval_for_enroll);
		System.out.println("manager_approve_enroll >" + manager_approve_enroll);
		System.out.println("instructor_approve_enroll >" + instructor_approve_enroll);
		System.out.println("enrolled_email_message >" + enrolled_email_message);
		 
		System.out.println("allow_drop >" + allow_drop);
		System.out.println("require_approval_for_drop >" + require_approval_for_drop);
		System.out.println("manager_approve_drop >" + manager_approve_drop);
		System.out.println("instructor_approve_drop >" + instructor_approve_drop);
		System.out.println("require_reason_for_drop >" + require_reason_for_drop);
		System.out.println("allow_enroll_after_drop >" + allow_enroll_after_drop);
		System.out.println("drop_course_by_datetime >" + drop_course_by_datetime);
		 */
			
			courseBean.setAllowWaitlisting((allow_waitlisting == null) ? false : allow_waitlisting.booleanValue());
			courseBean.setRequireApprovalForEnroll((require_approval_for_enroll == null) ? false : require_approval_for_enroll.booleanValue());
			courseBean.setAllowManagerEnrollApproval((manager_approve_enroll == null) ? false : manager_approve_enroll.booleanValue());
			courseBean.setAllowInstructorEnrollApproval((instructor_approve_enroll == null) ? false : instructor_approve_enroll.booleanValue());
			courseBean.setEnrollmentEmailMessage(enrolled_email_message);
			
			courseBean.setAllowDrop((allow_drop == null) ? false : allow_drop.booleanValue());
			courseBean.setRequireApprovalForDrop((require_approval_for_drop == null) ? false : require_approval_for_drop.booleanValue());
			courseBean.setAllowManagerDropApproval((manager_approve_drop == null) ? false : manager_approve_drop.booleanValue());
			courseBean.setAllowInstructorDropApproval((instructor_approve_drop == null) ? false : instructor_approve_drop.booleanValue());
			courseBean.setRequireReasonForDrop((require_reason_for_drop == null) ? false : require_reason_for_drop.booleanValue());
			courseBean.setAllowEnrollAfterDrop((allow_enroll_after_drop == null) ? false : allow_enroll_after_drop.booleanValue());
			
			String drop_deadline_string = (String)PropertyUtils.getSimpleProperty(_form, "drop_course_by_datetime");
			if (drop_deadline_string != null && !drop_deadline_string.equals(""))
				courseBean.setDropDeadline(CUBean.getDateFromUserString(drop_deadline_string));
			
			courseBean.save();
			
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