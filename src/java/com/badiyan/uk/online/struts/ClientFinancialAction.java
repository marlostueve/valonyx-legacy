/*
 * ClientFinancialAction.java
 *
 * Created on March 16, 2008, 4:36 PM
 * 
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.*;

import java.text.*;
import java.util.*;
import java.math.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a client profile.
 *
 * @author Marlo Stueve
 */
public final class
ClientFinancialAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in ClientFinancialAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;
		
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
				
				admin_company = (CompanyBean)session.getAttribute("adminCompany");
				adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
			}
			else
				return (_mapping.findForward("session_expired"));

			/*
			 * 
      <form-property name="includeGroupCF" type="java.lang.Boolean"/>
      <form-property name="planStartDateCF" type="java.lang.String"/>
      <form-property name="prepaidVisitsCF" type="java.lang.Integer"/>
      <form-property name="visitsUsedCF" type="java.lang.Integer"/>
      <form-property name="visitsRemainingCF" type="java.lang.Integer"/>
      <form-property name="perVisitChargeCF" type="java.lang.Integer"/>
      <form-property name="amountPaidCF" type="java.lang.Integer"/>
      <form-property name="escrowAmountCF" type="java.lang.Integer"/>
      <form-property name="dropPlanChargeCF" type="java.lang.Integer"/>
			 */

			Integer purchasePlanSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "purchasePlanSelect");
			Integer planSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "planSelect");
			
			Boolean includeGroupCF = (Boolean)PropertyUtils.getSimpleProperty(_form, "includeGroupCF");
			String planStartDateCF = (String)PropertyUtils.getSimpleProperty(_form, "planStartDateCF");
			Integer prepaidVisitsCF = (Integer)PropertyUtils.getSimpleProperty(_form, "prepaidVisitsCF");
			Integer visitsUsedCF = (Integer)PropertyUtils.getSimpleProperty(_form, "visitsUsedCF");
			Integer visitsRemainingCF = (Integer)PropertyUtils.getSimpleProperty(_form, "visitsRemainingCF");
			Integer perVisitChargeCF = (Integer)PropertyUtils.getSimpleProperty(_form, "perVisitChargeCF");
			Integer amountPaidCF = (Integer)PropertyUtils.getSimpleProperty(_form, "amountPaidCF");
			Integer escrowAmountCF = (Integer)PropertyUtils.getSimpleProperty(_form, "escrowAmountCF");
			Integer dropPlanChargeCF = (Integer)PropertyUtils.getSimpleProperty(_form, "dropPlanChargeCF");
			
			
			System.out.println("purchasePlanSelect >" + purchasePlanSelect);
			System.out.println("planSelect >" + planSelect);
			
			System.out.println("includeGroupCF >" + includeGroupCF);
			System.out.println("planStartDateCF >" + planStartDateCF);
			System.out.println("prepaidVisitsCF >" + prepaidVisitsCF);
			System.out.println("visitsUsedCF >" + visitsUsedCF);
			System.out.println("visitsRemainingCF >" + visitsRemainingCF);
			System.out.println("perVisitChargeCF >" + perVisitChargeCF);
			System.out.println("amountPaidCF >" + amountPaidCF);
			System.out.println("escrowAmountCF >" + escrowAmountCF);
			System.out.println("dropPlanChargeCF >" + dropPlanChargeCF);
			
			if (_request.getParameter("purchasePlan") != null)
			{
				if (purchasePlanSelect == null)
					throw new IllegalValueException("You must select a plan to purchase.");
				
				session.setAttribute("purchasePlan", purchasePlanSelect);
				forwardString = "purchase_plan";
			}
			else
			{
				PaymentPlanInstanceBean plan_instance = PaymentPlanInstanceBean.getPaymentPlanInstance(planSelect);
				plan_instance.setGroupPoolingAllowed((includeGroupCF == null) ? false : includeGroupCF.booleanValue());
				if (!planStartDateCF.equals(""))
					plan_instance.setStartDate(CUBean.getDateFromUserString(planStartDateCF));
				plan_instance.setNumberOfVisitsRemaining(visitsRemainingCF.intValue());
				plan_instance.save();
			}
			

		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			session.setAttribute("error-message", x.getMessage());
			forwardString = "failure";
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
