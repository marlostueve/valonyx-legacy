package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
//import com.badiyan.uk.forms.*;

import com.badiyan.uk.online.beans.*;

import java.text.ParseException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

import java.io.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
CourseUrlAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseUrlAction");

		ActionErrors errors = new ActionErrors();

		CourseBean courseBean = null;
		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
					return (_mapping.findForward("session_expired"));
				else
				{
					try
					{
						UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) && !person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
							return (_mapping.findForward("session_expired"));
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				courseBean = (CourseBean)session.getAttribute("adminCourse");
				if (courseBean == null)
				{
					courseBean = new CourseBean();
					session.setAttribute("adminCourse", courseBean);
				}

				/*
				loginBean = (LoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
				{
					loginBean = new LoginBean();
					session.setAttribute("loginBean", loginBean);
				}
				loginBean.setUsername("admin");
				loginBean.setPassword("spork");
				loginBean.getPerson();
					// This actually does the login.
				 */
			}
			else
				return (_mapping.findForward("session_expired"));

			String urlInput = (String)PropertyUtils.getSimpleProperty(_form, "urlInput");
			
			/*
			boolean m6 = false;
			boolean m7 = false;
			boolean m8 = false;
			if (mediaType6 != null)
				m6 = mediaType6.booleanValue();
			if (mediaType7 != null)
				m7 = mediaType7.booleanValue();
			if (mediaType8 != null)
				m8 = mediaType8.booleanValue();

			if (m6)
				courseBean.setResourceType(ResourceBean.INTERNET_RESOURCE_TYPE);
			else if (m7)
				courseBean.setResourceType(ResourceBean.CD_RESOURCE_TYPE);
			else if (m8)
				courseBean.setResourceType(ResourceBean.CLASSROOM_RESOURCE_TYPE);
			 */
			
			if (urlInput.equals("http://") || urlInput.equals(""))
			{
			    courseBean.setResourceType(ResourceBean.UNKNOWN_RESOURCE_TYPE);
			    courseBean.setUrl("");
			}
			else
			{
			    courseBean.setResourceType(ResourceBean.INTERNET_RESOURCE_TYPE);
			    courseBean.setUrl(urlInput);
			}
			    
			courseBean.save();
			
		}
        catch (IllegalValueException x)
        {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
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
		return (_mapping.findForward("success"));
	}
}