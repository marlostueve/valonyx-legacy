/*
 * MineralRatiosAction.java
 *
 * Created on July 21, 2007, 9:40 PM
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
MineralRatiosAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in MineralRatiosAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		UKOnlinePersonBean adminPerson = null;
		MineralRatiosClientDocumentBean document = null;
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
				
				document = (MineralRatiosClientDocumentBean)session.getAttribute("document");
				adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-bean       name="userMineralRatiosForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="dateInput" type="java.lang.String"/>
      <form-property name="initial_recommendations" type="java.lang.String"/>
      <form-property name="supplements_herbal_mixtures" type="java.lang.String"/>
      <form-property name="detox_exercise_diet_stress_management" type="java.lang.String"/>
      <form-property name="next_appointment" type="java.lang.String"/>
      <form-property name="template" type="java.lang.String"/>
      <form-property name="dateInputPhaseI" type="java.lang.String"/>
      <form-property name="supplements_header_phaseI" type="java.lang.String"/>
      <form-property name="detox_header_phaseI" type="java.lang.String"/>
      <form-property name="exercise_header_phaseI" type="java.lang.String"/>
      <form-property name="diet_header_phaseI" type="java.lang.String"/>
      <form-property name="stress_header_phaseI" type="java.lang.String"/>
      <form-property name="supplements_phaseI" type="java.lang.String"/>
      <form-property name="detox_phaseI" type="java.lang.String"/>
      <form-property name="exercise_phaseI" type="java.lang.String"/>
      <form-property name="diet_phaseI" type="java.lang.String"/>
      <form-property name="stress_phaseI" type="java.lang.String"/>
      <form-property name="supplements_header_phaseII" type="java.lang.String"/>
      <form-property name="detox_header_phaseII" type="java.lang.String"/>
      <form-property name="exercise_header_phaseII" type="java.lang.String"/>
      <form-property name="diet_header_phaseII" type="java.lang.String"/>
      <form-property name="stress_header_phaseII" type="java.lang.String"/>
      <form-property name="supplements_phaseII" type="java.lang.String"/>
      <form-property name="detox_phaseII" type="java.lang.String"/>
      <form-property name="exercise_phaseII" type="java.lang.String"/>
      <form-property name="diet_phaseII" type="java.lang.String"/>
      <form-property name="stress_phaseII" type="java.lang.String"/>
      <form-property name="supplements_header_phaseIII" type="java.lang.String"/>
      <form-property name="detox_header_phaseIII" type="java.lang.String"/>
      <form-property name="exercise_header_phaseIII" type="java.lang.String"/>
      <form-property name="diet_header_phaseIII" type="java.lang.String"/>
      <form-property name="stress_header_phaseIII" type="java.lang.String"/>
      <form-property name="supplements_phaseIII" type="java.lang.String"/>
      <form-property name="detox_phaseIII" type="java.lang.String"/>
      <form-property name="exercise_phaseIII" type="java.lang.String"/>
      <form-property name="diet_phaseIII" type="java.lang.String"/>
      <form-property name="stress_phaseIII" type="java.lang.String"/>
      <form-property name="supplements_header_phaseIV" type="java.lang.String"/>
      <form-property name="detox_header_phaseIV" type="java.lang.String"/>
      <form-property name="exercise_header_phaseIV" type="java.lang.String"/>
      <form-property name="diet_header_phaseIV" type="java.lang.String"/>
      <form-property name="stress_header_phaseIV" type="java.lang.String"/>
      <form-property name="supplements_phaseIV" type="java.lang.String"/>
      <form-property name="detox_phaseIV" type="java.lang.String"/>
      <form-property name="exercise_phaseIV" type="java.lang.String"/>
      <form-property name="diet_phaseIV" type="java.lang.String"/>
      <form-property name="stress_phaseIV" type="java.lang.String"/>
      <form-property name="new_template" type="java.lang.String"/>
    </form-bean>
			 */
			
			String dateInput = (String)PropertyUtils.getSimpleProperty(_form, "dateInput");
			String initial_recommendations = (String)PropertyUtils.getSimpleProperty(_form, "initial_recommendations");
			String supplements_herbal_mixtures = (String)PropertyUtils.getSimpleProperty(_form, "supplements_herbal_mixtures");
			String detox_exercise_diet_stress_management = (String)PropertyUtils.getSimpleProperty(_form, "detox_exercise_diet_stress_management");
			String next_appointment = (String)PropertyUtils.getSimpleProperty(_form, "next_appointment");
			String template = (String)PropertyUtils.getSimpleProperty(_form, "template");
			String dateInputPhaseI = (String)PropertyUtils.getSimpleProperty(_form, "dateInputPhaseI");
			String dateInputPhaseII = (String)PropertyUtils.getSimpleProperty(_form, "dateInputPhaseII");
			String dateInputPhaseIII = (String)PropertyUtils.getSimpleProperty(_form, "dateInputPhaseIII");
			String dateInputPhaseIV = (String)PropertyUtils.getSimpleProperty(_form, "dateInputPhaseIV");
			

			String supplements_header_phaseI = (String)PropertyUtils.getSimpleProperty(_form, "supplements_header_phaseI");
			String detox_header_phaseI = (String)PropertyUtils.getSimpleProperty(_form, "detox_header_phaseI");
			String exercise_header_phaseI = (String)PropertyUtils.getSimpleProperty(_form, "exercise_header_phaseI");
			String diet_header_phaseI = (String)PropertyUtils.getSimpleProperty(_form, "diet_header_phaseI");
			String stress_header_phaseI = (String)PropertyUtils.getSimpleProperty(_form, "stress_header_phaseI");
			String[] supplements_phaseI = (String[])PropertyUtils.getSimpleProperty(_form, "supplements_phaseI");
			String[] detox_phaseI = (String[])PropertyUtils.getSimpleProperty(_form, "detox_phaseI");
			String[] exercise_phaseI = (String[])PropertyUtils.getSimpleProperty(_form, "exercise_phaseI");
			String[] diet_phaseI = (String[])PropertyUtils.getSimpleProperty(_form, "diet_phaseI");
			String[] stress_phaseI = (String[])PropertyUtils.getSimpleProperty(_form, "stress_phaseI");

			String supplements_header_phaseII = (String)PropertyUtils.getSimpleProperty(_form, "supplements_header_phaseII");
			String detox_header_phaseII = (String)PropertyUtils.getSimpleProperty(_form, "detox_header_phaseII");
			String exercise_header_phaseII = (String)PropertyUtils.getSimpleProperty(_form, "exercise_header_phaseII");
			String diet_header_phaseII = (String)PropertyUtils.getSimpleProperty(_form, "diet_header_phaseII");
			String stress_header_phaseII = (String)PropertyUtils.getSimpleProperty(_form, "stress_header_phaseII");
			String[] supplements_phaseII = (String[])PropertyUtils.getSimpleProperty(_form, "supplements_phaseII");
			String[] detox_phaseII = (String[])PropertyUtils.getSimpleProperty(_form, "detox_phaseII");
			String[] exercise_phaseII = (String[])PropertyUtils.getSimpleProperty(_form, "exercise_phaseII");
			String[] diet_phaseII = (String[])PropertyUtils.getSimpleProperty(_form, "diet_phaseII");
			String[] stress_phaseII = (String[])PropertyUtils.getSimpleProperty(_form, "stress_phaseII");

			String supplements_header_phaseIII = (String)PropertyUtils.getSimpleProperty(_form, "supplements_header_phaseIII");
			String detox_header_phaseIII = (String)PropertyUtils.getSimpleProperty(_form, "detox_header_phaseIII");
			String exercise_header_phaseIII = (String)PropertyUtils.getSimpleProperty(_form, "exercise_header_phaseIII");
			String diet_header_phaseIII = (String)PropertyUtils.getSimpleProperty(_form, "diet_header_phaseIII");
			String stress_header_phaseIII = (String)PropertyUtils.getSimpleProperty(_form, "stress_header_phaseIII");
			String[] supplements_phaseIII = (String[])PropertyUtils.getSimpleProperty(_form, "supplements_phaseIII");
			String[] detox_phaseIII = (String[])PropertyUtils.getSimpleProperty(_form, "detox_phaseIII");
			String[] exercise_phaseIII = (String[])PropertyUtils.getSimpleProperty(_form, "exercise_phaseIII");
			String[] diet_phaseIII = (String[])PropertyUtils.getSimpleProperty(_form, "diet_phaseIII");
			String[] stress_phaseIII = (String[])PropertyUtils.getSimpleProperty(_form, "stress_phaseIII");

			String supplements_header_phaseIV = (String)PropertyUtils.getSimpleProperty(_form, "supplements_header_phaseIV");
			String detox_header_phaseIV = (String)PropertyUtils.getSimpleProperty(_form, "detox_header_phaseIV");
			String exercise_header_phaseIV = (String)PropertyUtils.getSimpleProperty(_form, "exercise_header_phaseIV");
			String diet_header_phaseIV = (String)PropertyUtils.getSimpleProperty(_form, "diet_header_phaseIV");
			String stress_header_phaseIV = (String)PropertyUtils.getSimpleProperty(_form, "stress_header_phaseIV");
			String[] supplements_phaseIV = (String[])PropertyUtils.getSimpleProperty(_form, "supplements_phaseIV");
			String[] detox_phaseIV = (String[])PropertyUtils.getSimpleProperty(_form, "detox_phaseIV");
			String[] exercise_phaseIV = (String[])PropertyUtils.getSimpleProperty(_form, "exercise_phaseIV");
			String[] diet_phaseIV = (String[])PropertyUtils.getSimpleProperty(_form, "diet_phaseIV");
			String[] stress_phaseIV = (String[])PropertyUtils.getSimpleProperty(_form, "stress_phaseIV");

			String new_template = (String)PropertyUtils.getSimpleProperty(_form, "new_template");

			System.out.println("dateInput >" + dateInput);
			System.out.println("initial_recommendations >" + initial_recommendations);
			System.out.println("supplements_herbal_mixtures >" + supplements_herbal_mixtures);
			System.out.println("detox_exercise_diet_stress_management >" + detox_exercise_diet_stress_management);
			System.out.println("next_appointment >" + next_appointment);
			System.out.println("template >" + template);
			System.out.println("dateInputPhaseI >" + dateInputPhaseI);

			System.out.println("supplements_header_phaseI >" + supplements_header_phaseI);
			System.out.println("detox_header_phaseI >" + detox_header_phaseI);
			System.out.println("exercise_header_phaseI >" + exercise_header_phaseI);
			System.out.println("diet_header_phaseI >" + diet_header_phaseI);
			System.out.println("stress_header_phaseI >" + stress_header_phaseI);
			System.out.println("supplements_phaseI >" + supplements_phaseI);
			System.out.println("detox_phaseI >" + detox_phaseI);
			System.out.println("exercise_phaseI >" + exercise_phaseI);
			System.out.println("diet_phaseI >" + diet_phaseI);
			System.out.println("stress_phaseI >" + stress_phaseI);

			System.out.println("supplements_header_phaseII >" + supplements_header_phaseII);
			System.out.println("detox_header_phaseII >" + detox_header_phaseII);
			System.out.println("exercise_header_phaseII >" + exercise_header_phaseII);
			System.out.println("diet_header_phaseII >" + diet_header_phaseII);
			System.out.println("stress_header_phaseII >" + stress_header_phaseII);
			System.out.println("supplements_phaseII >" + supplements_phaseII);
			System.out.println("detox_phaseII >" + detox_phaseII);
			System.out.println("exercise_phaseII >" + exercise_phaseII);
			System.out.println("diet_phaseII >" + diet_phaseII);
			System.out.println("stress_phaseII >" + stress_phaseII);

			System.out.println("supplements_header_phaseIII >" + supplements_header_phaseIII);
			System.out.println("detox_header_phaseIII >" + detox_header_phaseIII);
			System.out.println("exercise_header_phaseIII >" + exercise_header_phaseIII);
			System.out.println("diet_header_phaseIII >" + diet_header_phaseIII);
			System.out.println("stress_header_phaseIII >" + stress_header_phaseIII);
			System.out.println("supplements_phaseIII >" + supplements_phaseIII);
			System.out.println("detox_phaseIII >" + detox_phaseIII);
			System.out.println("exercise_phaseIII >" + exercise_phaseIII);
			System.out.println("diet_phaseIII >" + diet_phaseIII);
			System.out.println("stress_phaseIII >" + stress_phaseIII);

			System.out.println("supplements_header_phaseIV >" + supplements_header_phaseIV);
			System.out.println("detox_header_phaseIV >" + detox_header_phaseIV);
			System.out.println("exercise_header_phaseIV >" + exercise_header_phaseIV);
			System.out.println("diet_header_phaseIV >" + diet_header_phaseIV);
			System.out.println("stress_header_phaseIV >" + stress_header_phaseIV);
			System.out.println("supplements_phaseIV >" + supplements_phaseIV);
			System.out.println("detox_phaseIV >" + detox_phaseIV);
			System.out.println("exercise_phaseIV >" + exercise_phaseIV);
			System.out.println("diet_phaseIV >" + diet_phaseIV);
			System.out.println("stress_phaseIV >" + stress_phaseIV);

			System.out.println("new_template >" + new_template);
			
			System.out.println("submit_button >" + _request.getParameter("submit_button"));
			
			if (_request.getParameter("submit_button") != null)
			{
			    // add / update
			    
			    if ((dateInput != null) && (dateInput.length() > 0))
				document.setReportDate(CUBean.getDateFromUserString(dateInput));
			    if ((dateInputPhaseI != null) && (dateInputPhaseI.length() > 0))
				document.setPhaseIDate(CUBean.getDateFromUserString(dateInputPhaseI));
			    if ((dateInputPhaseII != null) && (dateInputPhaseII.length() > 0))
				document.setPhaseIIDate(CUBean.getDateFromUserString(dateInputPhaseII));
			    if ((dateInputPhaseIII != null) && (dateInputPhaseIII.length() > 0))
				document.setPhaseIIIDate(CUBean.getDateFromUserString(dateInputPhaseIII));
			    if ((dateInputPhaseIV != null) && (dateInputPhaseIV.length() > 0))
				document.setPhaseIVDate(CUBean.getDateFromUserString(dateInputPhaseIV));
			    document.setCreateOrModifyPerson(loginBean.getPerson());
			    document.setDetoxExerciseDietStressManagementString(detox_exercise_diet_stress_management);
			    document.setInitialRecommendations(initial_recommendations);
			    document.setIsTemplate(_request.getParameter("submit_button").equals("Save As Template"));
			    document.setNextAppointmentString(next_appointment);
			    if (_request.getParameter("submit_button").equals("Save As Template"))
			    {
				String template_name = _request.getParameter("new_template");
				if (template_name.length() > 0)
				    document.setTemplateName(template_name);
				else
				    throw new IllegalValueException("You must choose a template name.");
			    }
			    else
				document.setPerson(adminPerson);
			    document.setSupplementsHerbalMixturesString(supplements_herbal_mixtures);
			    document.save();


			    document.put("supplements_header_phaseI", supplements_header_phaseI);
			    document.put("supplements_header_phaseII", supplements_header_phaseII);
			    document.put("supplements_header_phaseIII", supplements_header_phaseIII);
			    document.put("supplements_header_phaseIV", supplements_header_phaseIV);

			    document.put("detox_header_phaseI", detox_header_phaseI);
			    document.put("detox_header_phaseII", detox_header_phaseII);
			    document.put("detox_header_phaseIII", detox_header_phaseIII);
			    document.put("detox_header_phaseIV", detox_header_phaseIV);

			    document.put("exercise_header_phaseI", exercise_header_phaseI);
			    document.put("exercise_header_phaseII", exercise_header_phaseII);
			    document.put("exercise_header_phaseIII", exercise_header_phaseIII);
			    document.put("exercise_header_phaseIV", exercise_header_phaseIV);

			    document.put("diet_header_phaseI", diet_header_phaseI);
			    document.put("diet_header_phaseII", diet_header_phaseII);
			    document.put("diet_header_phaseIII", diet_header_phaseIII);
			    document.put("diet_header_phaseIV", diet_header_phaseIV);

			    document.put("stress_header_phaseI", stress_header_phaseI);
			    document.put("stress_header_phaseII", stress_header_phaseII);
			    document.put("stress_header_phaseIII", stress_header_phaseIII);
			    document.put("stress_header_phaseIV", stress_header_phaseIV);



			    String str = "";
			    for (int i = 0; i < supplements_phaseI.length; i++)
				str += (supplements_phaseI[i] + "|");
			    document.put("supplements_phaseI", str);

			    str = "";
			    for (int i = 0; i < supplements_phaseII.length; i++)
				str += (supplements_phaseII[i] + "|");
			    document.put("supplements_phaseII", str);

			    str = "";
			    for (int i = 0; i < supplements_phaseIII.length; i++)
				str += (supplements_phaseIII[i] + "|");
			    document.put("supplements_phaseIII", str);

			    str = "";
			    for (int i = 0; i < supplements_phaseIV.length; i++)
				str += (supplements_phaseIV[i] + "|");
			    document.put("supplements_phaseIV", str);


			    str = "";
			    for (int i = 0; i < detox_phaseI.length; i++)
				str += (detox_phaseI[i] + "|");
			    document.put("detox_phaseI", str);

			    str = "";
			    for (int i = 0; i < detox_phaseII.length; i++)
				str += (detox_phaseII[i] + "|");
			    document.put("detox_phaseII", str);

			    str = "";
			    for (int i = 0; i < detox_phaseIII.length; i++)
				str += (detox_phaseIII[i] + "|");
			    document.put("detox_phaseIII", str);

			    str = "";
			    for (int i = 0; i < detox_phaseIV.length; i++)
				str += (detox_phaseIV[i] + "|");
			    document.put("detox_phaseIV", str);


			    str = "";
			    for (int i = 0; i < exercise_phaseI.length; i++)
				str += (exercise_phaseI[i] + "|");
			    document.put("exercise_phaseI", str);

			    str = "";
			    for (int i = 0; i < exercise_phaseII.length; i++)
				str += (exercise_phaseII[i] + "|");
			    document.put("exercise_phaseII", str);

			    str = "";
			    for (int i = 0; i < exercise_phaseIII.length; i++)
				str += (exercise_phaseIII[i] + "|");
			    document.put("exercise_phaseIII", str);

			    str = "";
			    for (int i = 0; i < exercise_phaseIV.length; i++)
				str += (exercise_phaseIV[i] + "|");
			    document.put("exercise_phaseIV", str);


			    str = "";
			    for (int i = 0; i < diet_phaseI.length; i++)
				str += (diet_phaseI[i] + "|");
			    document.put("diet_phaseI", str);

			    str = "";
			    for (int i = 0; i < diet_phaseII.length; i++)
				str += (diet_phaseII[i] + "|");
			    document.put("diet_phaseII", str);

			    str = "";
			    for (int i = 0; i < diet_phaseIII.length; i++)
				str += (diet_phaseIII[i] + "|");
			    document.put("diet_phaseIII", str);

			    str = "";
			    for (int i = 0; i < diet_phaseIV.length; i++)
				str += (diet_phaseIV[i] + "|");
			    document.put("diet_phaseIV", str);


			    str = "";
			    for (int i = 0; i < stress_phaseI.length; i++)
				str += (stress_phaseI[i] + "|");
			    document.put("stress_phaseI", str);

			    str = "";
			    for (int i = 0; i < stress_phaseII.length; i++)
				str += (stress_phaseII[i] + "|");
			    document.put("stress_phaseII", str);

			    str = "";
			    for (int i = 0; i < stress_phaseIII.length; i++)
				str += (stress_phaseIII[i] + "|");
			    document.put("stress_phaseIII", str);

			    str = "";
			    for (int i = 0; i < stress_phaseIV.length; i++)
				str += (stress_phaseIV[i] + "|");
			    document.put("stress_phaseIV", str);
			    
			    /*
			    if (!_request.getParameter("submit_button").equals("Save As Template"))
				session.removeAttribute("document");
			     */
			}
			else if (!_request.getParameter("template").equals("0"))
			{
			    System.out.println("template >" + _request.getParameter("template"));
			    
			    MineralRatiosClientDocumentBean template_document = MineralRatiosClientDocumentBean.getMineralRatiosClientDocument(Integer.parseInt(_request.getParameter("template")));
			    
			    document.setDetoxExerciseDietStressManagementString(template_document.getDetoxExerciseDietStressManagementString());
			    document.setInitialRecommendations(template_document.getInitialRecommendationsString());
			    document.setIsTemplate(false);
			    document.setNextAppointmentString(template_document.getNextAppointmentString());
			    document.setSupplementsHerbalMixturesString(template_document.getSupplementsHerbalMixturesString());
			    
			    document.put("supplements_header_phaseI", template_document.get("supplements_header_phaseI"));
			    document.put("supplements_header_phaseII", template_document.get("supplements_header_phaseII"));
			    document.put("supplements_header_phaseIII", template_document.get("supplements_header_phaseIII"));
			    document.put("supplements_header_phaseIV", template_document.get("supplements_header_phaseIV"));

			    document.put("detox_header_phaseI", template_document.get("detox_header_phaseI"));
			    document.put("detox_header_phaseII", template_document.get("detox_header_phaseII"));
			    document.put("detox_header_phaseIII", template_document.get("detox_header_phaseIII"));
			    document.put("detox_header_phaseIV", template_document.get("detox_header_phaseIV"));

			    document.put("exercise_header_phaseI", template_document.get("exercise_header_phaseI"));
			    document.put("exercise_header_phaseII", template_document.get("exercise_header_phaseII"));
			    document.put("exercise_header_phaseIII", template_document.get("exercise_header_phaseIII"));
			    document.put("exercise_header_phaseIV", template_document.get("exercise_header_phaseIV"));

			    document.put("diet_header_phaseI", template_document.get("diet_header_phaseI"));
			    document.put("diet_header_phaseII", template_document.get("diet_header_phaseII"));
			    document.put("diet_header_phaseIII", template_document.get("diet_header_phaseIII"));
			    document.put("diet_header_phaseIV", template_document.get("diet_header_phaseIV"));

			    document.put("stress_header_phaseI", template_document.get("stress_header_phaseI"));
			    document.put("stress_header_phaseII", template_document.get("stress_header_phaseII"));
			    document.put("stress_header_phaseIII", template_document.get("stress_header_phaseIII"));
			    document.put("stress_header_phaseIV", template_document.get("stress_header_phaseIV"));
			    
			    
			    
			    document.put("supplements_phaseI", template_document.get("supplements_phaseI"));
			    document.put("supplements_phaseII", template_document.get("supplements_phaseII"));
			    document.put("supplements_phaseIII", template_document.get("supplements_phaseIII"));
			    document.put("supplements_phaseIV", template_document.get("supplements_phaseIV"));

			    document.put("detox_phaseI", template_document.get("detox_phaseI"));
			    document.put("detox_phaseII", template_document.get("detox_phaseII"));
			    document.put("detox_phaseIII", template_document.get("detox_phaseIII"));
			    document.put("detox_phaseIV", template_document.get("detox_phaseIV"));

			    document.put("exercise_phaseI", template_document.get("exercise_phaseI"));
			    document.put("exercise_phaseII", template_document.get("exercise_phaseII"));
			    document.put("exercise_phaseIII", template_document.get("exercise_phaseIII"));
			    document.put("exercise_phaseIV", template_document.get("exercise_phaseIV"));

			    document.put("diet_phaseI", template_document.get("diet_phaseI"));
			    document.put("diet_phaseII", template_document.get("diet_phaseII"));
			    document.put("diet_phaseIII", template_document.get("diet_phaseIII"));
			    document.put("diet_phaseIV", template_document.get("diet_phaseIV"));

			    document.put("stress_phaseI", template_document.get("stress_phaseI"));
			    document.put("stress_phaseII", template_document.get("stress_phaseII"));
			    document.put("stress_phaseIII", template_document.get("stress_phaseIII"));
			    document.put("stress_phaseIV", template_document.get("stress_phaseIV"));
			}
			else
			{
			    
			}
			
			
			
			
			
			

			if (_request.getParameter("delete_id").equals("0"))
			{
			    
			    
			}
			else
			{
			    //MineralRatioBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
			}
			


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
