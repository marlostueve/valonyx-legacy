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
IndexSearchAction
extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in IndexSearchAction");
	
	// Get the username and password...
	ActionErrors errors = new ActionErrors();
	
	// Check the session to see if there's an existing courseSearch bean...
	
	UKOnlineLoginBean loginBean = null;
	
	UKOnlineCourseSearchBean courseSearch = null;
	UKOnlineResourceSearchBean resourceSearch = null;
	
	HttpSession session = _request.getSession(false);
	if (session != null)
	{
	    loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
	    if (loginBean == null)
		return (_mapping.findForward("session_expired"));
	    
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
	    
	    try
	    {
		UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
		if (!person_obj.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
		{
		    Vector groups = PersonGroupBean.getPersonGroups(person_obj.getUserGroupString());
		    if (groups.size() > 0)
		    {
			courseSearch.setUserGroup((PersonGroupBean)groups.elementAt(0));
			resourceSearch.setUserGroup((PersonGroupBean)groups.elementAt(0));
		    }

		    courseSearch.setJobTitle(person_obj.getJobTitle());
		    courseSearch.setDepartment(person_obj.getDepartment());

		    resourceSearch.setJobTitle(person_obj.getJobTitle());
		    resourceSearch.setDepartment(person_obj.getDepartment());
		}
	    }
	    catch (Exception x)
	    {
		x.printStackTrace();
	    }
	    
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