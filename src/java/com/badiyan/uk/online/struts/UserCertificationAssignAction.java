/*
 * UserCertificationAssignAction.java
 *
 * Created on February 18, 2007, 5:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

import java.util.*;

/**
 * Implementation of <strong>Action</strong> that registers a new user.
 *
 * @author Marlo Stueve
 * @version $Revision: 1.2 $ $Date: 2009/07/17 16:23:52 $
 */
public final class
UserCertificationAssignAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in UserCertificationAssignAction");
	
	ActionErrors errors = new ActionErrors();
	
	UKOnlineLoginBean loginBean = null;
	HttpSession session = _request.getSession(false);
	UKOnlinePersonBean adminPerson = null;
	
	try
	{
	    // Check the session to see if there's a course in progress...
	    
	    if (session != null)
	    {
		loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
		if (loginBean == null)
		{
		    loginBean = new UKOnlineLoginBean();
		    session.setAttribute("loginBean", loginBean);
		}
		loginBean.getPerson();
		// This actually does the login.
		
		adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
	    }
	    else
		return (_mapping.findForward("session_expired"));
	    
	    /*
	    
	    Integer userCertificationAssignSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "userCertificationAssignSelect");
	    String startDateSelect = (String)PropertyUtils.getSimpleProperty(_form, "startDateSelect");
	    String endDateSelect = (String)PropertyUtils.getSimpleProperty(_form, "endDateSelect");
	    
	    
	    System.out.println("userCertificationAssignSelect >" + userCertificationAssignSelect);
	    System.out.println("startDateSelect >" + startDateSelect);
	    System.out.println("endDateSelect >" + endDateSelect);
	    
	    System.out.println("adminPerson >" + adminPerson.getFullName());
	    
	    EcolabCertificationBean certification_obj = EcolabCertificationBean.getCertification(userCertificationAssignSelect.intValue());
	    certification_obj.assign(adminPerson, CUBean.getDateFromUserString(startDateSelect), CUBean.getDateFromUserString(endDateSelect), (UKOnlinePersonBean)loginBean.getPerson());
	     */
	    

	    Iterator cert_itr = EcolabCertificationBean.getCertifications().iterator();
	    while (cert_itr.hasNext())
	    {
		EcolabCertificationBean certification = (EcolabCertificationBean)cert_itr.next();
		String startDateSelect = _request.getParameter("received_" + certification.getId());
		String endDateSelect = _request.getParameter("expires_" + certification.getId());
		//System.out.println("start string >" + _request.getParameter("received_" + certification.getId()));
		//System.out.println("end string >" + _request.getParameter("expires_" + certification.getId()));
		
		Date start_date = null;
		Date end_date = null;
		
		if (startDateSelect.length() > 0)
		{
		    if (endDateSelect.length() == 0)
			throw new IllegalValueException("You must specify an expiration date for " + certification.getLabel());
		    
		    start_date = CUBean.getDateFromUserString(startDateSelect);
		    end_date = CUBean.getDateFromUserString(endDateSelect);
		}
		else if (endDateSelect.length() > 0)
		    throw new IllegalValueException("You must specify a received date for " + certification.getLabel());
		
		//System.out.println("start_date >" + start_date);
		//System.out.println("end_date >" + end_date);
		certification.assign(adminPerson, start_date, end_date, (UKOnlinePersonBean)loginBean.getPerson());
	    }
	    
	}
	catch (Exception x)
	{
	    x.printStackTrace();
	    errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
	}
	
	
	
	
	//RequestDispatcher rd = getServletContext().getRequestDispatcher("/user-profile.jsp?id=" + editPerson.getIdString());
	//rd.forward(_request, _response);
	
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
