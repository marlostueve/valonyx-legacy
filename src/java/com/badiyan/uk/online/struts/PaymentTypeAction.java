/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import com.valeo.qb.data.AccountRet;
import com.valeo.qb.data.ItemPaymentRet;
import com.valeo.qb.data.PaymentMethodRet;


import java.util.Date;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
PaymentTypeAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in PaymentTypeAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;

		UKOnlineCompanyBean admin_company = null;
		ItemPaymentRet admin_payment = null;

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

				admin_company = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				admin_payment = (ItemPaymentRet)session.getAttribute("itemPayment");
			}
			else
				return (_mapping.findForward("session_expired"));

			/*
			 *<form-bean       name="paymentTypeForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="paymentName" type="java.lang.String"/>
      <form-property name="creditCardMapping" type="java.lang.Short"/>
    </form-bean>
			 */

			System.out.println("_request.getParameter(\"delete_id\") >" + _request.getParameter("delete_id"));

			if (_request.getParameter("delete_id").equals(""))
			{
			    String paymentName = (String)PropertyUtils.getSimpleProperty(_form, "paymentName");
			    Short paymentMethodMapping = (Short)PropertyUtils.getSimpleProperty(_form, "paymentMethodMapping");
			    Short creditCardMapping = (Short)PropertyUtils.getSimpleProperty(_form, "creditCardMapping");
			    Integer depositToAccountSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "depositToAccountSelect");
				

			    System.out.println("paymentName >" + paymentName);
			    System.out.println("paymentMethodMapping >" + paymentMethodMapping);
			    System.out.println("creditCardMapping >" + creditCardMapping);
			    System.out.println("depositToAccountSelect >" + depositToAccountSelect);
				

				if (paymentName.equals(""))
					throw new IllegalValueException("Please specify a Payment Name.");
				
				admin_payment.setName(paymentName);
				
				if (paymentMethodMapping == -1)
					throw new IllegalValueException("Please specify a QB Payment Method.");
				
				if (creditCardMapping == -1)
					throw new IllegalValueException("Please specify a Payment Method.");
					
				// is there an existing mapping for this credit card type?

				try
				{
					ItemPaymentRet existing_payment_type = ItemPaymentRet.getPaymentItem(admin_company, creditCardMapping);
					if (existing_payment_type.getId() != admin_payment.getId())
						throw new IllegalValueException(existing_payment_type.getLabel() + " is already specified as a " + ItemPaymentRet.getPaymentMethodString(creditCardMapping) + " payment type.");
				}
				catch (ObjectNotFoundException x)
				{

				}
				
				
				
				admin_payment.setCompany(admin_company);
				
				if (paymentMethodMapping == 0)
				{
					// create a new Payment Method
					
					PaymentMethodRet new_payment_method_that_will_need_to_be_synced_with_qb = new PaymentMethodRet();
					new_payment_method_that_will_need_to_be_synced_with_qb.setCompany(admin_company);
					new_payment_method_that_will_need_to_be_synced_with_qb.setName(ItemPaymentRet.getPaymentMethodString(creditCardMapping));
						// give it the same name as my local mapping
					new_payment_method_that_will_need_to_be_synced_with_qb.save();
					
					admin_payment.setPaymentMethodId(new_payment_method_that_will_need_to_be_synced_with_qb.getId());
				}
				else
					admin_payment.setPaymentMethodId(paymentMethodMapping);
				
				admin_payment.setCCType(creditCardMapping);
				
				admin_payment.setTimeCreated(new Date());
				if (admin_payment.isNew())
					admin_payment.setIsActive(true);
				else
					admin_payment.setTimeModified(new Date());
				
				if (depositToAccountSelect.intValue() > 0)
				{
					AccountRet account = AccountRet.getAccount(depositToAccountSelect.intValue());
					admin_payment.setDepositToAccount(account);
				}

				admin_payment.save();
			}
			else
			{
			    //UKOnlineCompanyBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
			}

			session.removeAttribute("itemPayment");

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
