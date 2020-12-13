/*
 * CourseDropRequestResponseAction.java
 *
 * Created on August 22, 2006, 2:37 PM
 *
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

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
CourseDropRequestResponseAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseDropRequestResponseAction");
		
		ActionErrors errors = new ActionErrors();
		
		EnrollmentBean userEnrollment = null;
		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);
		
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
				
				userEnrollment = (EnrollmentBean)session.getAttribute("userEnrollment");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			
			String comment = (String)PropertyUtils.getSimpleProperty(_form, "comment");
			
			System.out.println("comment >" + comment);
			
			
			UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
			ApprovalQueueBean queue = ApprovalQueueBean.getQueue(person);
			
			System.out.println("`` - person found >" + person.getFullName());
			
			
			
			
			Enumeration enu = _request.getParameterNames();
			while (enu.hasMoreElements())
			{
				String paramName = (String)enu.nextElement();
				System.out.println("`` - param found >" + paramName);
			}
			
			if (_request.getParameter("approve_request.x") != null)
			{
				if ((comment == null) || comment.equals(""))
					queue.approve(userEnrollment);
				else
					queue.approve(userEnrollment, comment);
			}
			else if (_request.getParameter("deny_request.x") != null)
			{
				if ((comment == null) || comment.equals(""))
					queue.deny(userEnrollment);
				else
					queue.deny(userEnrollment, comment);
			}
			
		}
		catch (IllegalValueException x)
		{
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", CUBean.formatJavascriptString(x.getMessage())));
		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", CUBean.formatJavascriptString(x.getMessage())));
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