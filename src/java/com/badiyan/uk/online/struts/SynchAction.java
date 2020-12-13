/*
 * SynchAction.java
 *
 * Created on January 31, 2007, 11:08 AM
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
SynchAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in SynchAction");
	
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
	    String username = (String)PropertyUtils.getSimpleProperty(_form, "username");
	    
	   
	    
	    System.out.println("status >" + status);
	    
	    
	    session.setAttribute("initial_sync", "false");
	    
	    if (status != null)
	    {
		if (status.equals("sync"))
		{
		    session.setAttribute("sync", "true");
		}
		else if (status.equals("verify"))
		{
		    
		    System.out.println("loginBean >" + loginBean.getPerson());
		    if ((username != null) && (username.length() > 0))
		    {
		 	UKOnlinePersonBean verify_person = (UKOnlinePersonBean)loginBean.getPerson();
		        verify_person.setUsername(username);
			verify_person.setEmployeeId(username);
			verify_person.save();
		    }
		    
		    if (_request.getParameter("name_confirm") != null)
		    {
			//name_confirmed = true;
			UKOnlinePersonBean tablet_user = UKOnlinePersonBean.getTabletUser();
			String employee_id = tablet_user.getUsername();
			DBSyncMessage sync_obj = UKOnlinePersonBean.approveTabletUser(employee_id);
			
			boolean update_user_from_server_success = false;
			
			String first_name = null;
			String last_name = null;
			Object person_obj = sync_obj.getObject();
			if (person_obj instanceof Person)
			{
			    first_name = ((Person)person_obj).getFirstname();
			    last_name = ((Person)person_obj).getLastname();
			    update_user_from_server_success = true;
			}
			
			/*
			System.out.println("employee_id >" + employee_id);
			System.out.println("sync_obj >" + sync_obj);
			boolean update_user_from_server_success = CUBean.dbSync(sync_obj).booleanValue();
			 */
			
			
			
			if (update_user_from_server_success)
			{
			    /*
			    tablet_user.setActive(false);
			    tablet_user.save();
			     */
			    
			    UKOnlinePersonBean.cacheClear();
			    
			    //UKOnlinePersonBean tablet_user_from_server = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(employee_id);
			    
			    //tablet_user.setServerApproved(true);
			    tablet_user.setFirstName(first_name);
			    tablet_user.setLastName(last_name);
			    tablet_user.save();
			    boolean confirmed = UKOnlinePersonBean.confirmApproveTabletUser(employee_id);
			    if (confirmed)
			    {
				// log the "new" user in

				/*
				loginBean.setUsername(tablet_user_from_server.getUsername());
				loginBean.setPassword("spork");
				loginBean.getPerson();
				 */

				//is_tablet_user_approved = true;
			    }
			    else
			    {
				//really_bad_error = true;
				//really_bad_error_message = "The server was unable to confirm your employee id as a tablet user.  Please re-enter your employee id and try to synchronize again.";
			    }
			}
		    }
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