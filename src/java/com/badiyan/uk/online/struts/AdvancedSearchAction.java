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
AdvancedSearchAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in AdvancedSearchAction");
	
	// Get the username and password...
	ActionErrors errors = new ActionErrors();
	
	
	
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
	
	
	String titleInput = (String)PropertyUtils.getSimpleProperty(_form, "titleInput");
	Integer categorySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "categorySelect");
	String dateInput = (String)PropertyUtils.getSimpleProperty(_form, "dateInput");
	Integer dateSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "dateSelect");
	String titleSearchBox = (String)PropertyUtils.getSimpleProperty(_form, "titleSearchBox");
	
	
	System.out.println("titleInput - " + titleInput);
	System.out.println("categorySelect - " + categorySelect);
	System.out.println("dateInput - " + dateInput);
	System.out.println("dateSelect - " + dateSelect);
	System.out.println("titleSearchBox - " + titleSearchBox);
	
	
	
	// Check the session to see if there's an existing courseSearch bean...
	
	CourseSearchBean courseSearch = null;
	CertificationSearchBean certSearch = null;
	ResourceSearchBean resourceSearch = null;
	FAQSearchBean faqSearch = null;
	UKOnlineNewsItemSearchBean newsItemSearch = null;
	UKOnlineLoginBean loginBean = null;
	HttpSession session = _request.getSession(false);
	if (session != null)
	{
	    loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
	    if (loginBean == null)
		return (_mapping.findForward("session_expired"));
	    
	    courseSearch = (CourseSearchBean)session.getAttribute("courseSearch");
	    if (courseSearch == null)
	    {
		courseSearch = new CourseSearchBean();
		session.setAttribute("courseSearch", courseSearch);
	    }
	    
	    certSearch = (CertificationSearchBean)session.getAttribute("certSearch");
	    if (certSearch == null)
	    {
		certSearch = new CertificationSearchBean();
		session.setAttribute("certSearch", certSearch);
	    }
	    
	    resourceSearch = (ResourceSearchBean)session.getAttribute("resourceSearch");
	    if (resourceSearch == null)
	    {
		resourceSearch = new ResourceSearchBean();
		session.setAttribute("resourceSearch", resourceSearch);
	    }
	    
	    faqSearch = (FAQSearchBean)session.getAttribute("faqSearch");
	    if (faqSearch == null)
	    {
		faqSearch = new FAQSearchBean();
		session.setAttribute("faqSearch", faqSearch);
	    }
	    
	    newsItemSearch = (UKOnlineNewsItemSearchBean)session.getAttribute("newsItemSearch");
	    if (newsItemSearch == null)
	    {
		newsItemSearch = new UKOnlineNewsItemSearchBean();
		session.setAttribute("newsItemSearch", newsItemSearch);
	    }
	}
	else
	    return (_mapping.findForward("session_expired"));
	
	try
	{
	    courseSearch.clearSearchCriteria();
	    certSearch.clearSearchCriteria();
	    resourceSearch.clearSearchCriteria();
	    faqSearch.clearSearchCriteria();
	    newsItemSearch.clearSearchCriteria();
	    
	    if (titleInput.length() > 0)
	    {
		courseSearch.setKeyword(titleInput);
		certSearch.setKeyword(titleInput);
		resourceSearch.setKeyword(titleInput);
		faqSearch.setKeyword(titleInput);
		newsItemSearch.setKeyword(titleInput);
	    }
	    
	    if (categorySelect.intValue() > 0)
	    {
		courseSearch.setCategory(CategoryBean.getCategory(categorySelect.intValue()));
		certSearch.setCategory(CategoryBean.getCategory(categorySelect.intValue()));
		resourceSearch.setCategory(CategoryBean.getCategory(categorySelect.intValue()));
		faqSearch.setCategory(CategoryBean.getCategory(categorySelect.intValue()));
		newsItemSearch.setCategory(CategoryBean.getCategory(categorySelect.intValue()));
	    }
	    
	    if (dateInput != null)
	    {
		if (dateInput.length() > 0)
		{
		    if (!(dateSelect.intValue() == 3))
		    {
			Date date = CUBean.getDateFromUserString(dateInput);
			courseSearch.setReleasedDate(date);
			courseSearch.setReleasedDateCompareType(dateSelect.intValue());
			certSearch.setReleasedDate(date);
			certSearch.setReleasedDateCompareType(dateSelect.intValue());
			resourceSearch.setReleasedDate(date);
			resourceSearch.setReleasedDateCompareType(dateSelect.intValue());
			faqSearch.setReleasedDate(date);
			faqSearch.setReleasedDateCompareType(dateSelect.intValue());
			newsItemSearch.setReleasedDate(date);
			newsItemSearch.setReleasedDateCompareType(dateSelect.intValue());
		    }
		}
	    }
	    
	    if (titleSearchBox.length() > 0)
	    {
		courseSearch.setDescriptionKeyword(titleSearchBox);
		certSearch.setDescriptionKeyword(titleSearchBox);
		resourceSearch.setKeyword(titleSearchBox);
		faqSearch.setKeyword(titleSearchBox);
		newsItemSearch.setDescriptionKeyword(titleSearchBox);
	    }
	    
	    courseSearch.setStatus(CourseBean.DEFAULT_COURSE_STATUS);
	    
	    
	    boolean applyAudience = true;
	    try
	    {
		if (((String)session.getAttribute("corporate")).equals("true"))
		    applyAudience = false;
		if (applyAudience)
		{
		    UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
		    Vector groups = PersonGroupBean.getPersonGroups(person_obj.getUserGroupString());
		    if (groups.size() > 0)
		    {
			courseSearch.setUserGroup((PersonGroupBean)groups.elementAt(0));
			certSearch.setUserGroup((PersonGroupBean)groups.elementAt(0));
			resourceSearch.setUserGroup((PersonGroupBean)groups.elementAt(0));
			faqSearch.setUserGroup((PersonGroupBean)groups.elementAt(0));
			newsItemSearch.setUserGroup((PersonGroupBean)groups.elementAt(0));
		    }
		    
		    courseSearch.setJobTitle(person_obj.getJobTitle());
		    courseSearch.setDepartment(person_obj.getDepartment());
		    
		    certSearch.setJobTitle(person_obj.getJobTitle());
		    certSearch.setDepartment(person_obj.getDepartment());
		    
		    resourceSearch.setJobTitle(person_obj.getJobTitle());
		    resourceSearch.setDepartment(person_obj.getDepartment());
		    
		    faqSearch.setJobTitle(person_obj.getJobTitle());
		    faqSearch.setDepartment(person_obj.getDepartment());
		    
		}
	    }
	    catch (Exception x)
	    {
		x.printStackTrace();
	    }
	    
	    
	    
	    courseSearch.search();
	    certSearch.search();
	    
	    courseSearch.addSearchResults(certSearch);
	    
	    resourceSearch.search();
	    faqSearch.search();
	    
	    newsItemSearch.search();
	    
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