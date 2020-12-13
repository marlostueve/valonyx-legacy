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
UserRollAssignAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in UserRollAssignAction");
	
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
	    
	    Integer[] userRoleAssignSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "userRoleAssignSelect");
	    System.out.println("userRoleAssignSelect >" + userRoleAssignSelect);
	    
	    System.out.println("userRoleAssignSelect.length >"  + userRoleAssignSelect.length);
	    
	    Vector rolesToRemove = new Vector();
	    Iterator itr = RoleBean.getRoles(adminPerson).iterator();
	    while (itr.hasNext())
		rolesToRemove.addElement((RoleBean)itr.next());
	    itr = rolesToRemove.iterator();
	    while (itr.hasNext())
		adminPerson.removeRole((RoleBean)itr.next());
	    
	    for (int i = 0; i < userRoleAssignSelect.length; i++)
	    {
		RoleBean role_obj = RoleBean.getRole(userRoleAssignSelect[i].intValue());
		System.out.println("ass role >" + role_obj);
		adminPerson.addRole(role_obj);
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
