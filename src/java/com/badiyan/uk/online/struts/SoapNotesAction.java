/*
 * SoapNotesAction.java
 *
 * Created on October 18, 2007, 6:33 PM
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
SoapNotesAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in SoapNotesAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		UKOnlinePersonBean adminPerson = null;
		//MineralRatioBean mineralRatio = null;
		
		SOAPNotesBean soap_notes = null;
		
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
				
				//mineralRatio = (MineralRatioBean)session.getAttribute("mineralRatio");
				soap_notes = (SOAPNotesBean)session.getAttribute("soapNotes");
				adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-bean       name="userSoapNotesForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="dateInput" type="java.lang.String"/>
      <form-property name="s_notes" type="java.lang.String"/>
      <form-property name="o_notes" type="java.lang.String"/>
      <form-property name="a_notes" type="java.lang.String"/>
      <form-property name="p_notes" type="java.lang.String"/>
    </form-bean>
			 */

			if (_request.getParameter("delete_id").equals("0"))
			{
			    String dateInput = (String)PropertyUtils.getSimpleProperty(_form, "dateInput");
			    String s_notes = (String)PropertyUtils.getSimpleProperty(_form, "s_notes");
			    String o_notes = (String)PropertyUtils.getSimpleProperty(_form, "o_notes");
			    String a_notes = (String)PropertyUtils.getSimpleProperty(_form, "a_notes");
			    String p_notes = (String)PropertyUtils.getSimpleProperty(_form, "p_notes");

			    System.out.println("dateInput >" + dateInput);
			    System.out.println("s_notes >" + s_notes);
			    System.out.println("o_notes >" + o_notes);
			    System.out.println("a_notes >" + a_notes);
			    System.out.println("p_notes >" + p_notes);
			    
			    soap_notes.setAnalysisDate(CUBean.getDateFromUserString(dateInput));
			    soap_notes.setSNote(s_notes);
			    soap_notes.setONote(o_notes);
			    soap_notes.setANote(a_notes);
			    soap_notes.setPNote(p_notes);
			    
			    soap_notes.setPerson(adminPerson);
				soap_notes.setCreateOrModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
			    
			    soap_notes.save();

			    session.removeAttribute("soapNotes");
			    
			    /*

			    mineralRatio.setAnalysisDate(CUBean.getDateFromUserString(dateInput));

			    if (CA_MG.length() > 0)
				mineralRatio.setCaMg(CA_MG);
			    if (CA_K.length() > 0)
				mineralRatio.setCaK(CA_K);
			    if (NA_MG.length() > 0)
				mineralRatio.setNaMg(NA_MG);
			    if (NA_K.length() > 0)
				mineralRatio.setNaK(NA_K);
			    if (ZN_CU.length() > 0)
				mineralRatio.setZnCu(ZN_CU);
			    if (CA_P.length() > 0)
				mineralRatio.setCaP(CA_P);
			    
			    if (CA.length() > 0)
				mineralRatio.setCa(CA);
			    if (K.length() > 0)
				mineralRatio.setK(K);
			    if (CU.length() > 0)
				mineralRatio.setCu(CU);
			    if (HG.length() > 0)
				mineralRatio.setHg(HG);

			    if (oxidationType.intValue() == 1)
			    {
				mineralRatio.setOxidationType("MIXED");
			    }
			    else if (oxidationType.intValue() == 2)
			    {

				mineralRatio.setOxidationType("FAST");
			    }
			    else if (oxidationType.intValue() == 3)
				mineralRatio.setOxidationType("SLOW");

			    mineralRatio.setPerson(adminPerson);
			    mineralRatio.setNotes(notes);
			    mineralRatio.setCreateOrModifyPerson(loginBean.getPerson());

			    if (oxidationType.intValue() == 0)
				throw new IllegalValueException("Please chose an oxidation type.");

			    mineralRatio.save();

			    session.removeAttrib//ute("mineralRatio");
			     */
			}
			else
			{
			    //MineralRatioBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
			    
			    SOAPNotesBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
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