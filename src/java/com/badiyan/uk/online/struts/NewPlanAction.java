/*
 * NewPlanAction - 2/16/08
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.*;
import java.math.*;
import java.util.*;

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
NewPlanAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in NewPlanAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;
		PaymentPlanBean payment_plan = null;
		
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)loginBean.getPerson();
				
				admin_company = (CompanyBean)session.getAttribute("adminCompany");
				payment_plan = (PaymentPlanBean)session.getAttribute("adminPaymentPlan");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 * <form-bean       name="newPlanForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="typeSelect" type="java.lang.Short"/>
      <form-property name="practiceAreaSelect" type="java.lang.Short"/>
      <form-property name="costInput" type="java.lang.String"/>
      <form-property name="visitsInput" type="java.lang.Integer"/>
      <form-property name="allowMonthlyPayments" type="java.lang.Boolean"/>
      <form-property name="monthsInput" type="java.lang.Integer"/>
      <form-property name="allowPooling" type="java.lang.Boolean"/>
    </form-bean>
			 */
			
			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			Short typeSelect = (Short)PropertyUtils.getSimpleProperty(_form, "typeSelect");
			Integer practiceAreaSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "practiceAreaSelect");
			String costInput = (String)PropertyUtils.getSimpleProperty(_form, "costInput");
			Integer visitsInput = (Integer)PropertyUtils.getSimpleProperty(_form, "visitsInput");
			Boolean allowMonthlyPayments = (Boolean)PropertyUtils.getSimpleProperty(_form, "allowMonthlyPayments");
			Integer monthsInput = (Integer)PropertyUtils.getSimpleProperty(_form, "monthsInput");
			Boolean allowPooling = (Boolean)PropertyUtils.getSimpleProperty(_form, "allowPooling");

			System.out.println("nameInput >" + nameInput);
			System.out.println("typeSelect >" + typeSelect);
			System.out.println("practiceAreaSelect >" + practiceAreaSelect);
			System.out.println("costInput >" + costInput);
			System.out.println("visitsInput >" + visitsInput);
			System.out.println("allowMonthlyPayments >" + allowMonthlyPayments);
			System.out.println("monthsInput >" + monthsInput);
			System.out.println("allowPooling >" + allowPooling);
			
			BigDecimal plan_cost = null;
			if (costInput.length() == 0)
				throw new IllegalValueException("You must specify a plan cost.");
			try
			{
				plan_cost = new BigDecimal(costInput);
			}
			catch (NumberFormatException x)
			{
				throw new IllegalValueException("Invalid plan cost value.");
			}

			if (nameInput.length() == 0)
				throw new IllegalValueException("You must specify a plan name.");
			payment_plan.setName(nameInput);

			if (visitsInput.intValue() == 0)
				throw new IllegalValueException("You must specify the number of visits available for this plan.");
			payment_plan.setVisits(visitsInput.intValue());


			CheckoutCodeBean code = null;
			if (payment_plan.isNew())
			{
				// Create a checkout code for the new payment plan

				// I need to x-refer these

				code = new CheckoutCodeBean();
				code.setCode("PLAN");
				code.setDescription(nameInput);
				code.setAmount(plan_cost.setScale(2, BigDecimal.ROUND_HALF_UP));
				code.setType(CheckoutCodeBean.PLAN_TYPE);
				code.setCompany(admin_company);
				code.setCreatePerson(adminPerson);
				code.setPracticeArea(PracticeAreaBean.getPracticeArea(practiceAreaSelect.intValue()));
				code.save();
			}
			else
			{
				// update the associated checkout code

				try
				{
					CheckoutCodeBean checkout_code = payment_plan.getCheckoutCode();
					checkout_code.setDescription(nameInput);
					checkout_code.setAmount(plan_cost.setScale(2, BigDecimal.ROUND_HALF_UP));
					checkout_code.setPracticeArea(PracticeAreaBean.getPracticeArea(practiceAreaSelect.intValue()));
					checkout_code.save();
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}

			if (allowMonthlyPayments != null)
			{
				payment_plan.setAllowMonthlyPayments(allowMonthlyPayments.booleanValue());
				payment_plan.setMonthsToPay(monthsInput.intValue());
			}

			if (allowPooling != null)
			{
				payment_plan.setAllowGroupPooling(allowPooling.booleanValue());
			}

			payment_plan.setType(typeSelect.shortValue());
			payment_plan.setPracticeArea(PracticeAreaBean.getPracticeArea(practiceAreaSelect.intValue()));
			payment_plan.setCost(plan_cost);
			payment_plan.setNote("");
			payment_plan.setCompany(admin_company);
			payment_plan.setCreatePerson(adminPerson);
			if (code != null)
				payment_plan.setCheckoutCode(code);
			payment_plan.save();
			
			
			
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
