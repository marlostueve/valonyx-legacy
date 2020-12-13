/*
 * EvaluationFITBQuestionAction.java
 *
 * Created on May 23, 2007, 10:23 AM
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

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
EvaluationFITBQuestionAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in EvaluationFITBQuestionAction");

		ActionErrors errors = new ActionErrors();

		CompanyBean adminCompany = null;
		AssessmentBean assessmentBean = null;
		StemBean stemBean = null;
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

				adminCompany = (CompanyBean)session.getAttribute("adminCompany");
				assessmentBean = (AssessmentBean)session.getAttribute("adminAssessment");
				if (assessmentBean.isNew())
				{
				    assessmentBean.setOwner(loginBean.getPerson());
				    assessmentBean.setActive(true);
				    assessmentBean.setCompany(adminCompany);
				    assessmentBean.save();
				}
				

				stemBean = (StemBean)session.getAttribute("adminStem");
				System.out.println("````````````````SESSION ADMIN STEM >" + stemBean);
				if (stemBean == null)
				{
					stemBean = new StemBean();
					session.setAttribute("adminStem", stemBean);
				}
			}
			else
				return (_mapping.findForward("session_expired"));

			String headingInput = (String)PropertyUtils.getSimpleProperty(_form, "headingInput");
			String questionInput = (String)PropertyUtils.getSimpleProperty(_form, "questionInput");


			if (errors.isEmpty())
			{
				stemBean.setType(StemBean.FILL_IN_THE_BLANK_STEM_TYPE);
				stemBean.setHeadingText(headingInput);
				stemBean.setName(questionInput);
				stemBean.setParent(assessmentBean);
				//stemBean.setDisplayOrder((short)(assessmentBean.getNumStems()));
				if (stemBean.isNew()) {
					stemBean.setDisplayOrder((short)(assessmentBean.getNumStems()));
				}
				stemBean.save();

				Vector distractorsToSave = new Vector();

				assessmentBean.invalidateStems();

				System.out.println("ASSESSMENT STEM STUFF SAVED/CREATED >" + assessmentBean.getNameString());
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

