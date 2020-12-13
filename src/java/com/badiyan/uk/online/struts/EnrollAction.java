/*
 * EnrollAction.java
 *
 * Created on March 1, 2007, 9:46 PM
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

import com.badiyan.uk.online.tasks.*;
import com.badiyan.uk.tasks.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
EnrollAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in EnrollAction");
	
	ActionErrors errors = new ActionErrors();
	
	UKOnlineLoginBean loginBean = null;
	ListerBean requiredEnrollmentList = null;
	ListerBean recommendedList = null;
	HttpSession session = _request.getSession(false);
	
	String forward_string = "success";
	
	try
	{
	    if (session != null)
	    {
		loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
		
		requiredEnrollmentList = (ListerBean)session.getAttribute("requiredEnrollmentList");
		recommendedList = (ListerBean)session.getAttribute("recommendedList");
	    }
	    else
		return (_mapping.findForward("session_expired"));
	    
	    String course_id = (String)PropertyUtils.getSimpleProperty(_form, "course_id");
	    
	   
	    
	    System.out.println("course_id >" + course_id);
	    
	    
	    CourseBean course_to_enroll_in = CourseBean.getCourse(Integer.parseInt(course_id));
	    
	    boolean can_enroll = loginBean.getPerson().canEnrollIn(course_to_enroll_in);
	    boolean is_enrolled = loginBean.getPerson().isEnrolled(course_to_enroll_in);
	    
	    System.out.println("can_enroll >" + can_enroll);
	    System.out.println("is_enrolled >" + is_enrolled);
	    
	    if (!is_enrolled)
	    {
		if (can_enroll)
		{
		    EnrollmentBean new_enrollment = loginBean.getPerson().getEnrollment(course_to_enroll_in, true);
		    System.out.println("enrollment created >" + new_enrollment);
		    
		    requiredEnrollmentList.invalidateSearchResults();
		    if (recommendedList != null)
			recommendedList.invalidateSearchResults();
		}
	    }
	    
	    
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
	return (_mapping.findForward(forward_string));
    }
}