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
CertificationDetailAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CertificationDetailAction");

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

			Integer courseId = (Integer)PropertyUtils.getSimpleProperty(_form, "certificationId");


			UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
			CertificationBean certification = CertificationBean.getCertification(courseId.intValue());

			System.out.println("`` - person found >" + person.getFullName());
			System.out.println("`` - cert found >" + certification.getNameString());

			System.out.println("`` - takeCourse param found >" + _request.getParameter("takeCourse.x"));
			System.out.println("`` - enroll param found >" + _request.getParameter("enroll.x"));

			if (_request.getParameter("takeCourse.x") != null)
			{
			}
			else if (_request.getParameter("enroll.x") != null)
			{
				Iterator audienceItr = certification.getAudiences(person).iterator();
				AudienceBean targetAudience = null;
				while (audienceItr.hasNext())
				{
					AudienceBean audience = (AudienceBean)audienceItr.next();
					if (audience.contains(loginBean.getPerson()))
					{
						targetAudience = audience;
						break;
					}
				}
				if (targetAudience == null)
					errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", loginBean.getPerson().getFullName() + " not found in any audience for the selected certification."));
				else
				{
					/*
					person.enroll(person, certification, targetAudience.getCompleteByDate());
					ListerBean list = (ListerBean)session.getAttribute("pendingEnrollmentList");
					list.invalidateSearchResults();
					 */
				}

			}

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