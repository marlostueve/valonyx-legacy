package com.badiyan.uk.online.struts;

import com.badiyan.uk.online.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

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
AssessmentDetailAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in AssessmentDetailAction");

		String forwardString = "success";

		ActionErrors errors = new ActionErrors();

		AssessmentBean assessmentBean = null;
		AssessmentIterator assItr = null;
		UKOnlineLoginBean loginBean = null;
		CourseBean userCourse = null;
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
						/*
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
							return (_mapping.findForward("session_expired"));
						 */
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				assessmentBean = (AssessmentBean)session.getAttribute("userAssessment");
				if (assessmentBean == null)
				{
					assessmentBean = new AssessmentBean();
					session.setAttribute("userAssessment", assessmentBean);
				}

				assItr = (AssessmentIterator)session.getAttribute("assessmentItr");
				if (assItr == null)
				{
					assItr = new AssessmentIterator();
					assItr.setAssessment(loginBean.getPerson(), assessmentBean);
					session.setAttribute("assessmentItr", assItr);
				}
				
				
			}
			else
				return (_mapping.findForward("session_expired"));

			// ensure that the enrollment that this is connected to is marked as started
			
			
			EnrollmentBean enrollment = loginBean.getPerson().getEnrollment(assessmentBean.getCourse());
			if (enrollment.getStatus().equals(EnrollmentBean.NOT_STARTED_ENROLLMENT_STATUS))
			{
			    enrollment.setStatus(EnrollmentBean.STARTED_ENROLLMENT_STATUS);
			    enrollment.save();
			}


			if (_request.getParameter("submitButton").equals("Submit Answer"))
			{
				Integer hiddenPosition = new Integer(_request.getParameter("position"));
				if (hiddenPosition.intValue() != assItr.getPosition())
					errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "Please do not use the browser's back button."));
				else if (hiddenPosition.intValue() == assItr.getLastAnsweredPosition())
					errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "Please do not use the browser's back button."));
				if (errors.size() == 0)
				{

					Integer answer = (Integer)PropertyUtils.getSimpleProperty(_form, "answer");

					System.out.println("ANSER >" + answer);

					if (answer == null)
						errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "Please choose an answer."));
					else
					{
						// first get the current stem

						StemBean stem = (StemBean)assItr.current();

						// get the distractor that matches the selected input

						Vector distractors = stem.getDistractors();
						DistractorBean distractor = (DistractorBean)distractors.elementAt(answer.intValue() - 1);

						// record the answer

						assItr.chooseDistractor(distractor);
						assItr.setLastAnsweredPosition(hiddenPosition.intValue());
						loginBean.getPerson().answerAssessmentQuestion(assItr, distractor);


						System.out.println("~~DISTRACTOR SELECTED >" + distractor.getName());
						System.out.println("ASSESSMENT SAVED/CREATED >" + assessmentBean.getNameString());
					}
				}
			}
			else if (_request.getParameter("submitButton").equals("Next Question") ||
					_request.getParameter("submitButton").equals("Finish"))
			{
				assItr.next();
				if (!assItr.hasNext())
					forwardString = "assessmentComplete";
			}
			else if (_request.getParameter("submitButton").equals("Retake Assessment"))
			{
				assItr.first();
				loginBean.getPerson().resetAssessment(assessmentBean);
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
		return (_mapping.findForward(forwardString));
	}
}
