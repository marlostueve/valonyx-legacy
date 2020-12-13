/*
 * IndexResourceSearchAction.java
 *
 * Created on February 24, 2007, 1:13 PM
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
IndexResourceSearchAction
extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in IndexResourceSearchAction");
	
	// Get the username and password...
	ActionErrors errors = new ActionErrors();
	
	// Check the session to see if there's an existing courseSearch bean...
	
	UKOnlineCourseSearchBean courseSearch = null;
	UKOnlineResourceSearchBean resourceSearch = null;
	
	HttpSession session = _request.getSession(false);
	if (session != null)
	{
	    courseSearch = (UKOnlineCourseSearchBean)session.getAttribute("courseSearch");
	    resourceSearch = (UKOnlineResourceSearchBean)session.getAttribute("resourceSearch");
	}
	else
	    return (_mapping.findForward("session_expired"));
	
	try
	{
	    String keyword = (String)PropertyUtils.getSimpleProperty(_form, "keyword");
	    System.out.println("keyword - " + keyword);
	
	    courseSearch.clearSearchCriteria();
	    resourceSearch.clearSearchCriteria();
	    
	    if (keyword.length() > 0)
	    {
		courseSearch.setKeyword(keyword);
		resourceSearch.setKeyword(keyword);
	    }
	    
	    courseSearch.setStatus(CourseBean.DEFAULT_COURSE_STATUS);
	    
	    resourceSearch.setResultsType(2);
	    
	    courseSearch.search();
	    resourceSearch.search();
	    
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