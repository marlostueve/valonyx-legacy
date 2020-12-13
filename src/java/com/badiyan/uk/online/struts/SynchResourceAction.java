/*
 * SynchResourceAction.java
 *
 * Created on March 2, 2007, 3:38 PM
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
SynchResourceAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in SynchResourceAction");
	
	ActionErrors errors = new ActionErrors();
	
	UKOnlineLoginBean loginBean = null;
	HttpSession session = _request.getSession(false);
	
	String forward_string = "success";
	
	try
	{
	    if (session != null)
	    {
		loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
	    }
	    else
		return (_mapping.findForward("session_expired"));
	    
	    String status = (String)PropertyUtils.getSimpleProperty(_form, "status");
	    
	    System.out.println("status >" + status);
	    
	    if (status != null)
	    {
		if (status.equals("sync"))
		{
		    session.setAttribute("sync-resource", "true");
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