package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.ParseException;
import java.util.Date;

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
AssessmentMainAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in AssessmentMainAction");

		ActionErrors errors = new ActionErrors();

		CompanyBean adminCompany = null;
		AssessmentBean assessmentBean = null;
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

				assessmentBean = (AssessmentBean)session.getAttribute("adminAssessment");
				adminCompany = (CompanyBean)session.getAttribute("adminCompany");
			}
			else
				return (_mapping.findForward("session_expired"));

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			Integer passFailInput = (Integer)PropertyUtils.getSimpleProperty(_form, "passFailInput");
			String introductionInput = (String)PropertyUtils.getSimpleProperty(_form, "introductionInput");
			String instructionsInput = (String)PropertyUtils.getSimpleProperty(_form, "instructionsInput");
			Boolean allowRetakes = (Boolean)PropertyUtils.getSimpleProperty(_form, "allowRetakes");

			assessmentBean.setCompany(adminCompany);
			assessmentBean.setName(nameInput);
			assessmentBean.setPassFailPercentile(passFailInput.shortValue());
			assessmentBean.setIntroduction(introductionInput);
			assessmentBean.setIntructions(instructionsInput);
			assessmentBean.setOwner(loginBean.getPerson());
			assessmentBean.setActive(true);
			if (allowRetakes != null)
				assessmentBean.setAllowRetake(allowRetakes.booleanValue());
			else
				assessmentBean.setAllowRetake(false);
			assessmentBean.save();

			System.out.println("ASSESSMENT SAVED/CREATED >" + assessmentBean.getNameString());
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
