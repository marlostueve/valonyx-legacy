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
 * @version $Revision: 1.2 $ $Date: 2009/07/17 16:23:51 $
 */
public final class
AdvancedSearchAction2
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in AdvancedSearchAction2");
	
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
	
	Integer group1 = (Integer)PropertyUtils.getSimpleProperty(_form, "group1");
	
	Boolean typeActivityELRN = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeActivityELRN");
	Boolean typeActivityMNTR = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeActivityMNTR");
	Boolean typeActivityILED = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeActivityILED");
	Boolean typeActivityASMT = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeActivityASMT");
	
	Boolean typeResourceDOC = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeResourceDOC");
	Boolean typeResourcePPT = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeResourcePPT");
	Boolean typeResourceXLS = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeResourceXLS");
	Boolean typeResourcePDF = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeResourcePDF");
	Boolean typeResourceSWF = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeResourceSWF");
	Boolean typeResourceWEB = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeResourceWEB");
	Boolean typeResourceVID = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeResourceVID");
	Boolean typeResourceCD = (Boolean)PropertyUtils.getSimpleProperty(_form, "typeResourceCD");
	
	String releaseDate = (String)PropertyUtils.getSimpleProperty(_form, "releaseDate");
	Integer group2 = (Integer)PropertyUtils.getSimpleProperty(_form, "group2");
	String keyword = (String)PropertyUtils.getSimpleProperty(_form, "keyword");
	
	/*
	System.out.println("group1 - " + group1);
	
	System.out.println("typeActivityELRN - " + typeActivityELRN);
	System.out.println("typeActivityMNTR - " + typeActivityMNTR);
	System.out.println("typeActivityILED - " + typeActivityILED);
	System.out.println("typeActivityASMT - " + typeActivityASMT);
	
	System.out.println("typeResourceDOC - " + typeResourceDOC);
	System.out.println("typeResourcePPT - " + typeResourcePPT);
	System.out.println("typeResourceXLS - " + typeResourceXLS);
	System.out.println("typeResourcePDF - " + typeResourcePDF);
	System.out.println("typeResourceSWF - " + typeResourceSWF);
	System.out.println("typeResourceWEB - " + typeResourceWEB);
	System.out.println("typeResourceVID - " + typeResourceVID);
	System.out.println("typeResourceCD - " + typeResourceCD);
	
	System.out.println("releaseDate - " + releaseDate);
	System.out.println("group2 - " + group2);
	System.out.println("keyword - " + keyword);
	 */
	
	try
	{
	    courseSearch.clearSearchCriteria();
	    resourceSearch.clearSearchCriteria();
	    
	    if (typeActivityELRN != null)
		courseSearch.addResourceType(com.badiyan.uk.online.beans.CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME);
	    if (typeActivityMNTR != null)
		courseSearch.addResourceType(com.badiyan.uk.online.beans.CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME);
	    if (typeActivityILED != null)
		courseSearch.addResourceType(com.badiyan.uk.online.beans.CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME);
	    if (typeActivityASMT != null)
		courseSearch.addResourceType(com.badiyan.uk.online.beans.CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME);
	    
	    if (typeResourceDOC != null)
		resourceSearch.addResourceType(ResourceBean.WORD_RESOURCE_TYPE);
	    if (typeResourcePPT != null)
		resourceSearch.addResourceType(ResourceBean.POWERPOINT_RESOURCE_TYPE);
	    if (typeResourceXLS != null)
		resourceSearch.addResourceType(ResourceBean.XLS_RESOURCE_TYPE);
	    if (typeResourcePDF != null)
		resourceSearch.addResourceType(ResourceBean.ACROBAT_RESOURCE_TYPE);
	    if (typeResourceSWF != null)
		resourceSearch.addResourceType(ResourceBean.FLASH_RESOURCE_TYPE);
	    if (typeResourceWEB != null)
		resourceSearch.addResourceType(ResourceBean.INTERNET_RESOURCE_TYPE);
	    if (typeResourceVID != null)
		resourceSearch.addResourceType(ResourceBean.VIDEO_RESOURCE_TYPE);
	    if (typeResourceCD != null)
		resourceSearch.addResourceType(ResourceBean.CD_RESOURCE_TYPE);
	    
	    
	    if (keyword.length() > 0)
	    {
		courseSearch.setKeyword(keyword);
		resourceSearch.setKeyword(keyword);
	    }
	    
	    if (releaseDate != null)
	    {
		if (releaseDate.length() > 0)
		{
		    Date date = CUBean.getDateFromUserString(releaseDate);
		    courseSearch.setReleasedDate(date);
		    resourceSearch.setReleasedDate(date);
		}
	    }
	    
	    resourceSearch.setReleasedDateCompareType(group2.intValue());
	    courseSearch.setReleasedDateCompareType(group2.intValue());
	    resourceSearch.setResultsType(group1.intValue());
	    
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