/*
 * ClientBillingAction.java
 *
 * Created on April 19, 2008, 5:11 PM
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
ClientBillingAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in ClientBillingAction");

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
			 *       <form-property name="bill_id" type="java.lang.Integer"/>
      <form-property name="amount" type="java.lang.String"/>
      <form-property name="collections" type="java.lang.Integer"/>
      <form-property name="collections_amount" type="java.lang.String"/>
			 */

			Integer bill_id = (Integer)PropertyUtils.getSimpleProperty(_form, "bill_id");
			String amount_str = (String)PropertyUtils.getSimpleProperty(_form, "amount");
			Boolean collections = (Boolean)PropertyUtils.getSimpleProperty(_form, "collections");
			String collections_amount_str = (String)PropertyUtils.getSimpleProperty(_form, "collections_amount");
			
			System.out.println("bill_id >" + bill_id);
			System.out.println("amount >" + amount_str);
			System.out.println("collections >" + collections);
			System.out.println("collections_amount >" + collections_amount_str);
			
			Vector orders_still_on_bill = new Vector();
			
			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements())
			{
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));
				
				if (param_name.indexOf("hiddenId") > -1)
				{
					ValeoOrderBean order_still_on_bill = (ValeoOrderBean)ValeoOrderBean.getOrder(Integer.parseInt(param_name.substring(8)));
					System.out.println("order_still_on_bill >" + order_still_on_bill.getLabel());
					orders_still_on_bill.addElement(order_still_on_bill);
				}
			}
			
			if (_request.getParameter("Re-Bill") != null)
			{
				System.out.println("re-bill >" + bill_id);
			}
			else if (_request.getParameter("Update") != null)
			{
				System.out.println("update >" + bill_id);
				
				BillingBean bill = BillingBean.getBilling(bill_id);
				bill.setCreateOrModifyPerson(loginBean.getPerson());
				if (amount_str.length() > 0)
				{
					BigDecimal amount = new BigDecimal(amount_str);
					bill.setAmountBilled(amount);
				}
				if (collections != null)
				{
					bill.setSentToCollections(collections.booleanValue());
					
					if (collections_amount_str.length() > 0)
					{
						BigDecimal collections_amount = new BigDecimal(collections_amount_str);
						bill.setCollectionsAmount(collections_amount);
					}
				}
				else
				{
					bill.setSentToCollections(false);
					bill.setCollectionsAmount(new BigDecimal(0));
				}
				
				bill.setOrders(orders_still_on_bill);
				
				bill.save();
			}
			else if (_request.getParameter("Print") != null)
			{
				System.out.println("print >" + bill_id);
			}
			
			/*
			if (_request.getParameter("purchasePlan") != null)
			{
				if (purchasePlanSelect == null)
					throw new IllegalValueException("You must select a plan to purchase.");
				
				session.setAttribute("purchasePlan", purchasePlanSelect);
				forwardString = "purchase_plan";
			}
			 */

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
