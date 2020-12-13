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
AssessmentTFQuestionAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in AssessmentTFQuestionAction");

		ActionErrors errors = new ActionErrors();

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

				assessmentBean = (AssessmentBean)session.getAttribute("adminAssessment");
				if (assessmentBean == null)
				{
					assessmentBean = new AssessmentBean();
					session.setAttribute("adminAssessment", assessmentBean);
				}

				stemBean = (StemBean)session.getAttribute("adminStem");
				if (stemBean == null)
				{
					stemBean = new StemBean();
					session.setAttribute("adminStem", stemBean);
				}
			}
			else
				return (_mapping.findForward("session_expired"));

			String questionInput = (String)PropertyUtils.getSimpleProperty(_form, "questionInput");
			String correctSelect = (String)PropertyUtils.getSimpleProperty(_form, "correctSelect");
			
			String answerAInput = (String)PropertyUtils.getSimpleProperty(_form, "answerAInput");
			String answerAFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerAFeedbackInput");

			
			String answerBInput = (String)PropertyUtils.getSimpleProperty(_form, "answerBInput");
			String answerBFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerBFeedbackInput");

			// first verify the input to ensure that it makes sense...

			boolean aEntered = true;
			boolean bEntered = true;

			boolean aCorrect = correctSelect.equals("A");
			boolean bCorrect = correctSelect.equals("B");

			if (!aCorrect && !bCorrect)
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "Either the true answer or the false answer must be marked correct."));

			if (errors.isEmpty())
			{
				stemBean.setType(StemBean.TF_STEM_TYPE);
				stemBean.setName(questionInput);
				stemBean.setParent(assessmentBean);
				stemBean.save();

				Vector distractorsToSave = new Vector();

				// I need to replace the stem's distractors with the passed distractors...

				DistractorBean distractor = null;
				Vector distractors = stemBean.getDistractors();
				Iterator itr = distractors.iterator();

				System.out.println("NUM DISTRACOTRS FOUND >" + distractors.size());

				if (aEntered)
				{
					if (itr.hasNext())
						distractor = (DistractorBean)itr.next();
					else
						distractor = new DistractorBean();
					distractor.setName(answerAInput);
					distractor.setPrescriptiveFeedback(answerAFeedbackInput);
					distractor.setCorrect(aCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}

				if (bEntered)
				{
					if (itr.hasNext())
						distractor = (DistractorBean)itr.next();
					else
						distractor = new DistractorBean();
					distractor.setName(answerBInput);
					distractor.setPrescriptiveFeedback(answerBFeedbackInput);
					distractor.setCorrect(bCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}

				for (int i = 0; i < distractorsToSave.size(); i++)
				{
					DistractorBean distractorToSave = (DistractorBean)distractorsToSave.elementAt(i);
					stemBean.addDistractor(distractorToSave);
				}

				assessmentBean.invalidateStems();

				System.out.println("ASSESSMENT TF STEM STUFF SAVED/CREATED >" + assessmentBean.getNameString());
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