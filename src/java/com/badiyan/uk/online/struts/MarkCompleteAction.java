/*
 * MarkCompleteAction.java
 *
 * Created on April 2, 2007, 8:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a user login.
 *
 * @author Marlo Stueve
 * @version $Revision: 1.2 $ $Date: 2009/07/17 16:23:52 $
 */
public final class
MarkCompleteAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in MarkCompleteAction");
	
	// Get the username and password...
	ActionErrors errors = new ActionErrors();
	
	// Check the session to see if there's an existing courseSearch bean...
	
	UKOnlineLoginBean loginBean = null;
	
	CourseBean userCourse = null;
	
	HttpSession session = _request.getSession(false);
	if (session != null)
	{
	    loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
	    if (loginBean == null)
		return (_mapping.findForward("session_expired"));
	    
	    userCourse = (CourseBean)session.getAttribute("userCourse");
	}
	else
	    return (_mapping.findForward("session_expired"));
	

	
	String manager = (String)PropertyUtils.getSimpleProperty(_form, "manager");
	String comment = (String)PropertyUtils.getSimpleProperty(_form, "comment");
	
	System.out.println("manager - " + manager);
	System.out.println("comment - " + comment);
	
	try
	{
	    if (userCourse.getType().equals(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME) || userCourse.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME))
	    {
		if (manager.length() == 0)
		    throw new IllegalValueException("Please specify the manager marking this activity complete.");
		
		EnrollmentBean userEnrollment = loginBean.getPerson().getEnrollment(userCourse, false);

		if (!userEnrollment.isComplete())
		{
		    userEnrollment.setComment(manager + "~|~" + comment);
		    userEnrollment.setStatus(EnrollmentBean.COMPLETED_ENROLLMENT_STATUS);
		    userEnrollment.save();
		}
	    }
	}
	catch (Exception x)
	{
	    x.printStackTrace();
	    //_request.setAttribute("error", x.getMessage());
	    //RequestDispatcher rd = _request.getRequestDispatcher("/login.jsp");
	    //rd.forward(_request, _response);
	    
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