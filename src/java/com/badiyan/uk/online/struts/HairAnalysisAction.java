/*
 * HairAnalysisAction.java
 *
 * Created on June 30, 2007, 9:21 PM
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
HairAnalysisAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in HairAnalysisAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		UKOnlinePersonBean adminPerson = null;
		MineralRatioBean mineralRatio = null;
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
				
				mineralRatio = (MineralRatioBean)session.getAttribute("mineralRatio");
				adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-bean       name="userHairAnalysisForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="dateInput" type="java.lang.String"/>
      <form-property name="CA_MG" type="java.lang.String"/>
      <form-property name="CA_K" type="java.lang.String"/>
      <form-property name="NA_MG" type="java.lang.String"/>
      <form-property name="NA_K" type="java.lang.String"/>
      <form-property name="ZN_CU" type="java.lang.String"/>
      <form-property name="CA_P" type="java.lang.String"/>
      <form-property name="oxidationType" type="java.lang.Integer"/>
    </form-bean>
			 */

			if (_request.getParameter("delete_id").equals("0"))
			{
			    String dateInput = (String)PropertyUtils.getSimpleProperty(_form, "dateInput");
			    String CA_MG = (String)PropertyUtils.getSimpleProperty(_form, "CA_MG");
			    String CA_K = (String)PropertyUtils.getSimpleProperty(_form, "CA_K");
			    String NA_MG = (String)PropertyUtils.getSimpleProperty(_form, "NA_MG");
			    String NA_K = (String)PropertyUtils.getSimpleProperty(_form, "NA_K");
			    String ZN_CU = (String)PropertyUtils.getSimpleProperty(_form, "ZN_CU");
			    String CA_P = (String)PropertyUtils.getSimpleProperty(_form, "CA_P");
			    Integer oxidationType = (Integer)PropertyUtils.getSimpleProperty(_form, "oxidationType");
			    String notes = (String)PropertyUtils.getSimpleProperty(_form, "notes");
			    
			    String CA = (String)PropertyUtils.getSimpleProperty(_form, "CA");
			    String K = (String)PropertyUtils.getSimpleProperty(_form, "K");
			    String CU = (String)PropertyUtils.getSimpleProperty(_form, "CU");
			    String HG = (String)PropertyUtils.getSimpleProperty(_form, "HG");

			    System.out.println("dateInput >" + dateInput);
			    System.out.println("CA_MG >" + CA_MG);
			    System.out.println("CA_K >" + CA_K);
			    System.out.println("NA_MG >" + NA_MG);
			    System.out.println("NA_K >" + NA_K);
			    System.out.println("ZN_CU >" + ZN_CU);
			    System.out.println("CA_P >" + CA_P);
			    System.out.println("oxidationType >" + oxidationType);
			    System.out.println("notes >" + notes);

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

			    session.removeAttribute("mineralRatio");
			}
			else
			{
			    MineralRatioBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
			}
			
			/*
			
			adminBuilding.setName(nameInput);
			adminBuilding.setMapURL(map_url);
			if (address.intValue() > 0)
				adminBuilding.setAddress(AddressBean.getAddress(address.intValue()));
			if (comment != null)
				adminBuilding.setComment(comment);
			if (location.intValue() > 0)
				adminBuilding.setLocation(LocationBean.getLocation(location.intValue()));
			
			if (nameInput == null || nameInput.equals(""))
				throw new IllegalValueException("Please specify a building name.");
			if (location.intValue() == 0)
				throw new IllegalValueException("Please specify a building location.");
			
			adminBuilding.setCreateOrModifyPerson(loginBean.getPerson());
			adminBuilding.save();
			
			session.removeAttribute("adminBuilding");
			 */

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