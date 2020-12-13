/*
 * EvaluationQuestionAction.java
 *
 * Created on May 16, 2007, 10:32 AM
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
EvaluationQuestionAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in EvaluationQuestionAction");

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
			String correctSelect = (String)PropertyUtils.getSimpleProperty(_form, "correctSelect");
			
			String answerAInput = (String)PropertyUtils.getSimpleProperty(_form, "answerAInput");
			//String answerAFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerAFeedbackInput");
			String answerBInput = (String)PropertyUtils.getSimpleProperty(_form, "answerBInput");
			//String answerBFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerBFeedbackInput");
			String answerCInput = (String)PropertyUtils.getSimpleProperty(_form, "answerCInput");
			//String answerCFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerCFeedbackInput");
			String answerDInput = (String)PropertyUtils.getSimpleProperty(_form, "answerDInput");
			//String answerDFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerDFeedbackInput");
			String answerEInput = (String)PropertyUtils.getSimpleProperty(_form, "answerEInput");
			//String answerEFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerEFeedbackInput");
			String answerFInput = (String)PropertyUtils.getSimpleProperty(_form, "answerFInput");
			//String answerEFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerEFeedbackInput");
			String answerGInput = (String)PropertyUtils.getSimpleProperty(_form, "answerGInput");
			//String answerEFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerEFeedbackInput");
			String answerHInput = (String)PropertyUtils.getSimpleProperty(_form, "answerHInput");
			//String answerEFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerEFeedbackInput");
			String answerIInput = (String)PropertyUtils.getSimpleProperty(_form, "answerIInput");
			//String answerEFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerEFeedbackInput");
			String answerJInput = (String)PropertyUtils.getSimpleProperty(_form, "answerJInput");
			//String answerEFeedbackInput = (String)PropertyUtils.getSimpleProperty(_form, "answerEFeedbackInput");
			
			
			String womenOnly = (String)PropertyUtils.getSimpleProperty(_form, "womenOnly");

			// first verify the input to ensure that it makes sense...

			boolean aEntered = true;
			boolean bEntered = true;
			boolean cEntered = false;
			boolean dEntered = false;
			boolean eEntered = false;
			boolean fEntered = false;
			
			boolean gEntered = false;
			boolean hEntered = false;
			boolean iEntered = false;
			boolean jEntered = false;

			boolean aCorrect = correctSelect.equals("A");
			boolean bCorrect = correctSelect.equals("B");
			boolean cCorrect = correctSelect.equals("C");
			boolean dCorrect = correctSelect.equals("D");
			boolean eCorrect = correctSelect.equals("E");
			boolean fCorrect = correctSelect.equals("F");
			
			boolean gCorrect = correctSelect.equals("G");
			boolean hCorrect = correctSelect.equals("H");
			boolean iCorrect = correctSelect.equals("I");
			boolean jCorrect = correctSelect.equals("J");
			
			boolean all_that_apply = correctSelect.equals("Z");
			
			/*
			System.out.println("aCorrect >" + aCorrect);
			System.out.println("bCorrect >" + bCorrect);
			System.out.println("cCorrect >" + cCorrect);
			System.out.println("cCorrect >" + cCorrect);
			System.out.println("eCorrect >" + eCorrect);
			*/
			
			System.out.println("all_that_apply >" + all_that_apply);
			System.out.println("womenOnly >" + womenOnly);
			
			//System.out.println("all_that_apply >" + all_that_apply);

			if (answerCInput.length() > 0)
				cEntered = true;
			if (answerDInput.length() > 0)
				dEntered = true;
			if (answerEInput.length() > 0)
				eEntered = true;
			if (answerFInput.length() > 0)
				fEntered = true;
			
			if (answerGInput.length() > 0)
				gEntered = true;
			if (answerHInput.length() > 0)
				hEntered = true;
			if (answerIInput.length() > 0)
				iEntered = true;
			if (answerJInput.length() > 0)
				jEntered = true;

			if (dEntered && !cEntered)
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "You can't specify answer D without specifying answer C."));
			else if (eEntered && (!cEntered || !dEntered))
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "You can't specify answer E without specifying answer C and D."));

			
			if (errors.isEmpty())
			{
				boolean wo = false;
				if (womenOnly != null) {
					if (womenOnly.equals("Y")) {
						AssessmentCategoryBean category = AssessmentCategoryBean.maintainCategory(assessmentBean, "Women Only");
						stemBean.setCategory(category);
						wo = true;
					}
				}
				if (!wo) {
					stemBean.setCategory(null);
				}
				
				
				stemBean.setType(all_that_apply ? StemBean.ALL_THAT_APPLY : StemBean.MC_STEM_TYPE);
				stemBean.setHeadingText(headingInput);
				stemBean.setName(questionInput);
				stemBean.setParent(assessmentBean);
				if (stemBean.isNew()) {
					stemBean.setDisplayOrder((short)(assessmentBean.getNumStems()));
				}
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
					//distractor.setPrescriptiveFeedback(answerAFeedbackInput);
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
					//distractor.setPrescriptiveFeedback(answerBFeedbackInput);
					distractor.setCorrect(bCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}

				if (cEntered)
				{
					if (itr.hasNext())
						distractor = (DistractorBean)itr.next();
					else
						distractor = new DistractorBean();
					distractor.setName(answerCInput);
					//distractor.setPrescriptiveFeedback(answerCFeedbackInput);
					distractor.setCorrect(cCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}

				if (dEntered)
				{
					if (itr.hasNext())
						distractor = (DistractorBean)itr.next();
					else
						distractor = new DistractorBean();
					distractor.setName(answerDInput);
					//distractor.setPrescriptiveFeedback(answerDFeedbackInput);
					distractor.setCorrect(dCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}

				if (eEntered)
				{
					if (itr.hasNext())
						distractor = (DistractorBean)itr.next();
					else
						distractor = new DistractorBean();
					distractor.setName(answerEInput);
					//distractor.setPrescriptiveFeedback(answerEFeedbackInput);
					distractor.setCorrect(eCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}

				if (fEntered)
				{
					if (itr.hasNext())
						distractor = (DistractorBean)itr.next();
					else
						distractor = new DistractorBean();
					distractor.setName(answerFInput);
					//distractor.setPrescriptiveFeedback(answerEFeedbackInput);
					distractor.setCorrect(fCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}
				
				

				if (gEntered)
				{
					if (itr.hasNext())
						distractor = (DistractorBean)itr.next();
					else
						distractor = new DistractorBean();
					distractor.setName(answerGInput);
					//distractor.setPrescriptiveFeedback(answerEFeedbackInput);
					distractor.setCorrect(gCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}

				if (hEntered)
				{
					if (itr.hasNext())
						distractor = (DistractorBean)itr.next();
					else
						distractor = new DistractorBean();
					distractor.setName(answerHInput);
					//distractor.setPrescriptiveFeedback(answerEFeedbackInput);
					distractor.setCorrect(hCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}

				if (iEntered)
				{
					if (itr.hasNext())
						distractor = (DistractorBean)itr.next();
					else
						distractor = new DistractorBean();
					distractor.setName(answerIInput);
					//distractor.setPrescriptiveFeedback(answerEFeedbackInput);
					distractor.setCorrect(iCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}

				if (jEntered)
				{
					if (itr.hasNext())
						distractor = (DistractorBean)itr.next();
					else
						distractor = new DistractorBean();
					distractor.setName(answerJInput);
					//distractor.setPrescriptiveFeedback(answerEFeedbackInput);
					distractor.setCorrect(jCorrect);
					//stemBean.addDistractor(distractor);
					distractorsToSave.addElement(distractor);
				}

				for (int i = 0; i < distractorsToSave.size(); i++)
				{
					DistractorBean distractorToSave = (DistractorBean)distractorsToSave.elementAt(i);
					stemBean.addDistractor(distractorToSave);
				}

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
