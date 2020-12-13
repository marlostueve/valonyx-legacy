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
ApprovalQueueAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in ApprovalQueueAction");
		
		String forward_string = "enroll";
		
		ActionErrors errors = new ActionErrors();
		
		//FAQSearchBean faqSearchBean = null;
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
			}
			else
				return (_mapping.findForward("session_expired"));
			
			
			
			Enumeration enu = _request.getParameterNames();
			while (enu.hasMoreElements())
			{
				String paramName = (String)enu.nextElement();
				System.out.println("`` - param found >" + paramName);
				
				if (paramName.indexOf("approve-") != -1)
				{
					int end_index = paramName.indexOf(".x");
					if (end_index != -1)
					{
						EnrollmentBean enrollment_to_approve = EnrollmentBean.getEnrollment(Integer.parseInt(paramName.substring(8, end_index)));
						System.out.println("enrollment_to_approve >" + enrollment_to_approve);
						
						if (enrollment_to_approve.getStatus().equals(EnrollmentBean.DROP_PENDING_ENROLLMENT_STATUS))
							forward_string = "drop";
						
						// put the enrollment in the session
						
						session.setAttribute("userEnrollment", enrollment_to_approve);
					}
				}
				else if (paramName.indexOf("deny-") != -1)
				{
					int end_index = paramName.indexOf(".x");
					if (end_index != -1)
					{
						EnrollmentBean enrollment_to_deny = EnrollmentBean.getEnrollment(Integer.parseInt(paramName.substring(5, end_index)));
						System.out.println("enrollment_to_deny >" + enrollment_to_deny);
						
						if (enrollment_to_deny.getStatus().equals(EnrollmentBean.DROP_PENDING_ENROLLMENT_STATUS))
							forward_string = "drop";
						
						session.setAttribute("userEnrollment", enrollment_to_deny);
					}
				}
			}
			
			if (_request.getParameter("approve.x") != null)
			{
				//person.requestEnrollment(course, comment);
				
				System.out.println("HEY!");
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
		return (_mapping.findForward(forward_string));
	}
}