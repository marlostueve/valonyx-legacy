/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
WellnessSurveyAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in WellnessSurveyAction");

		String forwardString = "success";

		ActionErrors errors = new ActionErrors();

		UKOnlineCompanyBean userCompany = null;
		UKOnlinePersonBean client = null;
		AssessmentBean assessmentBean = null;
		AssessmentIterator assItr = null;
		UKOnlineLoginBean loginBean = null;
		EvaluationProgress progress = null;
		
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				if (loginBean == null) return (_mapping.findForward("session_expired"));

				userCompany = (UKOnlineCompanyBean)session.getAttribute("userCompany");
				client = (UKOnlinePersonBean)loginBean.getPerson();
				assessmentBean = (AssessmentBean)session.getAttribute("userAssessment2");

				assItr = (AssessmentIterator)session.getAttribute("assessmentItr2");
				if (assItr == null)
				{
					assItr = new AssessmentIterator();
					assItr.setAssessment(loginBean.getPerson(), assessmentBean);
					session.setAttribute("assessmentItr2", assItr);
				}
				
				progress = (EvaluationProgress)session.getAttribute("evaluationProgress2");
				if (progress == null) {
					
					// see if there's already a progress object for this user session
					
					try {
						//progress = EvaluationProgress.getProgress(client, assessmentBean, session.getId());
						progress = EvaluationProgress.getProgress(client, assessmentBean, new Date());
					} catch (Exception x) {
						progress = new EvaluationProgress();
						progress.setClient(client);
						progress.setCompany(userCompany);
						progress.setEvaluation(assessmentBean);
						progress.setSession(session.getId());
						progress.setProgressDate(new Date());
						progress.save();
					}
					
					session.setAttribute("evaluationProgress2", progress);
					
				}
				
			}
			else
				return (_mapping.findForward("session_expired"));


			System.out.println("_request.getParameter(\"submitButton\") >" + _request.getParameter("submitButton"));
			
			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements()) {
				String param_name = (String)enumx.nextElement();
				String param_value = _request.getParameter(param_name);
				System.out.println("paramx >" + param_name + " = " + param_value);
			}
			
			
			
			
			StemBean stem = assItr.current();
			
			System.out.println("stem at current pos >" + stem.getLabel());
			
			
			if (stem.isFillInTheBlank())
			{
				String response = _request.getParameter("fitbResponse");
				System.out.println("response --  >" + response);
				//evaluation_interaction.setFillInTheBlankResponse(response);
				progress.recordInteraction(stem, response);
			}
			else if (stem.isCheckAllThatApply())
			{
				Iterator distractors = stem.getDistractors().iterator();
				String response_str = "";
				while (distractors.hasNext())
				{
					DistractorBean distractor = (DistractorBean)distractors.next();
					String response = _request.getParameter("answer-dis-" + distractor.getId());
					
					System.out.println("CATA response --  >" + response);
					if (response != null) {
						if (response.equals("on")) {
							if (response_str.isEmpty())
								response_str = distractor.getNameString();
							else
								response_str += (", " + distractor.getNameString());
						}
					}
				}
				System.out.println("response_str --  >" + response_str);
				//evaluation_interaction.setFillInTheBlankResponse(response_str);
				progress.recordInteraction(stem, response_str);
			}
			else
			{
				//String response = _request.getParameter("answer-stem-" + stem.getId());
				//System.out.println("MCTF response --  >" + response);
				//evaluation_interaction.setFillInTheBlankResponse(response + "-");
				
				
				DistractorBean selectedDistractor = (DistractorBean)stem.getDistractors().elementAt(Integer.parseInt(_request.getParameter("answer")) - 1);
				System.out.println("selectedDistractor >" + selectedDistractor.getNameString());
				
				progress.recordInteraction(stem, selectedDistractor);
				
			}
			
			if (assItr.hasNext())
				assItr.next();
			else {
				forwardString = "finished";
				progress.setCompleted();
				progress.save();
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
