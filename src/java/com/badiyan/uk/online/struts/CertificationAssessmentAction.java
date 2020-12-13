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
CertificationAssessmentAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CertificationAssessmentAction");

		ActionErrors errors = new ActionErrors();

		CertificationBean certBean = null;
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
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
							return (_mapping.findForward("session_expired"));
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				certBean = (CertificationBean)session.getAttribute("adminCert");
				if (certBean == null)
				{
					certBean = new CertificationBean();
					session.setAttribute("adminCert", certBean);
				}
			}
			else
				return (_mapping.findForward("session_expired"));

			String assessmentSelect = (String)PropertyUtils.getSimpleProperty(_form, "assessmentSelect");
			Boolean mustBeCompleted = (Boolean)PropertyUtils.getSimpleProperty(_form, "mustBeCompleted");


			if (!assessmentSelect.equals("0"))
			{

				//courseBean..setPersonTitle(jobTitleSelect);

				AssessmentBean assessment = AssessmentBean.getAssessment(Integer.parseInt(assessmentSelect));
				System.out.println("ASSESSMENT FOUND >" + assessment.getNameString());
				certBean.setAssessment(assessment, mustBeCompleted);
			}

			//audience.save();
			//System.out.println("ASSESSMENT ASSIGNED TO COURSE >" + courseBean.getNameString());
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