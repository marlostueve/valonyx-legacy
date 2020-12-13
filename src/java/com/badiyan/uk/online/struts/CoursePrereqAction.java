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
 * Implementation of <strong>Action</strong> that validates a course prereq.
 *
 * @author Marlo Stueve
 */
public final class
CoursePrereqAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in CoursePrereqAction");
	
	ActionErrors errors = new ActionErrors();
	
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
		
		courseBean = (CourseBean)session.getAttribute("adminCourse");
	    }
	    else
		return (_mapping.findForward("session_expired"));
	    
	    Integer[] activities = (Integer[])PropertyUtils.getSimpleProperty(_form, "activities");
	    
			/*
			String manufacturerSelect = (String)PropertyUtils.getSimpleProperty(_form, "manufacturerSelect");
			String categorySelect = (String)PropertyUtils.getSimpleProperty(_form, "categorySelect");
			String titleSelect = (String)PropertyUtils.getSimpleProperty(_form, "titleSelect");
			 */
	    
	    
	    Vector prereqs = new Vector();
	    for (int i = 0; i < activities.length; i++)
	    {
		try
		{
		    CourseBean prereqCourse = CourseBean.getCourse(activities[i].intValue());
		    prereqs.addElement(prereqCourse);
		}
		catch (Exception x)
		{
		    x.printStackTrace();
		}
	    }
	    courseBean.setPrerequisites(prereqs);
	    courseBean.savePrerequisites();
	    
			/*
			CourseBean prereqCourse = CourseBean.getCourse(Integer.parseInt(titleSelect));
			courseBean.addPrerequisite(prereqCourse);
			 */
	    
	    
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