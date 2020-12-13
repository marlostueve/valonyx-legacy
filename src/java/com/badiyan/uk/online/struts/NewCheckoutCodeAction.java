/*
 * NewCheckoutCodeAction.java - 3/3/08
 */

package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;
import com.valeo.qb.QBXMLItemAddRequest;
import com.valeo.qb.data.AccountRet;
import com.valeo.qb.data.SalesTaxCodeRet;

import com.valeo.qb.data.VendorRet;
import com.valeo.qbpos.QBPOSXMLRequestQueue;
import java.math.*;

import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a client profile.
 *
 * @author Marlo Stueve
 */
public final class
NewCheckoutCodeAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in NewCheckoutCodeAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;
		
		UKOnlineCompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;
		CheckoutCodeBean adminCheckoutCode = null;
		
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)loginBean.getPerson();
				
				admin_company = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				adminCheckoutCode = (CheckoutCodeBean)session.getAttribute("adminCheckoutCode");

			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 * <form-bean       name="newCheckoutCodeForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="itemNumberInput" type="java.lang.String"/>
      <form-property name="upcInput" type="java.lang.String"/>
      <form-property name="descriptionInput" type="java.lang.String"/>
      <form-property name="onHandInput" type="java.lang.Short"/>
      <form-property name="reorderPointInput" type="java.lang.Short"/>
      <form-property name="amountInput" type="java.lang.String"/>
      <form-property name="costInput" type="java.lang.String"/>
      <form-property name="unitCostInput" type="java.lang.String"/>
      <form-property name="typeSelect" type="java.lang.Short"/>
      <form-property name="codeInput" type="java.lang.String"/>
      <form-property name="vendorSelect" type="java.lang.Integer"/>
      <form-property name="departmentSelect" type="java.lang.Integer"/>
      <form-property name="practiceAreaSelect" type="java.lang.Integer"/>
      <form-property name="planUse" type="java.lang.Boolean"/>
      <form-property name="commission" type="java.lang.Boolean"/>
      <form-property name="taxCode" type="java.lang.Integer"/>
    </form-bean>
			 */
			
			String itemNumberInput = (String)PropertyUtils.getSimpleProperty(_form, "itemNumberInput");
			String upcInput = (String)PropertyUtils.getSimpleProperty(_form, "upcInput");
			String itemNameInput = (String)PropertyUtils.getSimpleProperty(_form, "descriptionInput");
			String salesDescInput = (String)PropertyUtils.getSimpleProperty(_form, "salesDescInput");
			Short onHandInput = (Short)PropertyUtils.getSimpleProperty(_form, "onHandInput");
			Short reorderPointInput = (Short)PropertyUtils.getSimpleProperty(_form, "reorderPointInput");
			String amountInput = (String)PropertyUtils.getSimpleProperty(_form, "amountInput");
			String costInput = (String)PropertyUtils.getSimpleProperty(_form, "costInput");
			String unitCostInput = (String)PropertyUtils.getSimpleProperty(_form, "unitCostInput");
			Short typeSelect = (Short)PropertyUtils.getSimpleProperty(_form, "typeSelect");
			String codeInput = (String)PropertyUtils.getSimpleProperty(_form, "codeInput");
			Integer vendorSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "vendorSelect");
			Integer departmentSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "departmentSelect");
			Integer practiceAreaSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "practiceAreaSelect");
			Boolean planUse = (Boolean)PropertyUtils.getSimpleProperty(_form, "planUse");
			Boolean commission = (Boolean)PropertyUtils.getSimpleProperty(_form, "commission");
			Integer taxCode = (Integer)PropertyUtils.getSimpleProperty(_form, "taxCode");

			Integer cogsAccountSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "cogsAccountSelect");
			Integer incomeAccountSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "incomeAccountSelect");
			Integer assetAccountSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "assetAccountSelect");
			Integer expenseAccountSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "expenseAccountSelect");
			

			adminCheckoutCode.setCode(codeInput);
			adminCheckoutCode.setDescription(itemNameInput);
			adminCheckoutCode.setSalesDescription(salesDescInput);
			
			adminCheckoutCode.setIsActive(true);

			if (amountInput.length() > 0)
			{
				BigDecimal amount = new BigDecimal(amountInput);
				adminCheckoutCode.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
			}

			if (costInput.length() > 0)
			{
				BigDecimal amount = new BigDecimal(costInput);
				adminCheckoutCode.setOrderCost(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
			}

			if (unitCostInput.length() > 0)
			{
				BigDecimal amount = new BigDecimal(unitCostInput);
				adminCheckoutCode.setAverageUnitCost(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
			}

			adminCheckoutCode.setType(typeSelect.shortValue());

			if (vendorSelect.intValue() > 0)
				adminCheckoutCode.setVendor(VendorRet.getVendor(vendorSelect.intValue()));

			if (departmentSelect.intValue() > 0)
				adminCheckoutCode.setInventoryDepartment(InventoryDepartment.getInventoryDepartment(departmentSelect.intValue()));

			if (practiceAreaSelect.intValue() > 0)
				adminCheckoutCode.setPracticeArea(PracticeAreaBean.getPracticeArea(practiceAreaSelect.intValue()));

			adminCheckoutCode.setCompany(admin_company);
			//adminCheckoutCode.setModifyPerson(adminPerson);
			adminCheckoutCode.setCreateOrModifyPerson(adminPerson);

			boolean is_plan_use = (planUse == null) ? false : (planUse.booleanValue() ? true : false);
			if (is_plan_use && (adminCheckoutCode.getType() != CheckoutCodeBean.PROCEDURE_TYPE))
				throw new IllegalValueException("This checkout code must be a procdure in order to be a plan use.");
			adminCheckoutCode.setPlanUse(is_plan_use);

			adminCheckoutCode.setCommissionable((commission == null) ? false : commission.booleanValue());

			/*
			if (taxItem.intValue() == 0)
				adminCheckoutCode.removeTaxCode();
			else
				adminCheckoutCode.setTaxCode(ValeoTaxCodeBean.getTaxCode(taxItem.intValue()));
			 */

			if (taxCode.intValue() == 0)
				adminCheckoutCode.removeSalesTaxCode();
			else
				adminCheckoutCode.setSalesTaxCode(SalesTaxCodeRet.getSalesTaxCode(taxCode.intValue()));

			if (itemNumberInput.length() > 0)
				adminCheckoutCode.setItemNumber(Integer.parseInt(itemNumberInput));
			else
				adminCheckoutCode.setItemNumber(0);

			adminCheckoutCode.setUPC(upcInput);

			adminCheckoutCode.setOnHandQuantity(onHandInput.shortValue());
			adminCheckoutCode.setReorderPoint(reorderPointInput.shortValue());


			if (cogsAccountSelect.intValue() > 0)
			{
				AccountRet cogsAccount = AccountRet.getAccount(cogsAccountSelect.intValue());
				adminCheckoutCode.setCostOfGoodsSoldAccount(cogsAccount);
			}

			if (incomeAccountSelect.intValue() > 0)
			{
				AccountRet incomeAccount = AccountRet.getAccount(incomeAccountSelect.intValue());
				adminCheckoutCode.setIncomeAccount(incomeAccount);
			}
			//else
			//	throw new IllegalValueException("An income account must be specified.");

			if (assetAccountSelect.intValue() > 0)
			{
				AccountRet assetAccount = AccountRet.getAccount(assetAccountSelect.intValue());
				adminCheckoutCode.setAssetAccount(assetAccount);
			}

			if (expenseAccountSelect.intValue() > 0)
			{
				AccountRet expenseAccount = AccountRet.getAccount(expenseAccountSelect.intValue());
				adminCheckoutCode.setExpenseAccount(expenseAccount);
			}
			//else
			//	throw new IllegalValueException("An expense account must be specified.");


			if (itemNameInput.length() == 0)
				throw new IllegalValueException("Please provide an item name.");
			if (amountInput.length() == 0)
				throw new IllegalValueException("Please specify a regular price.");
			if (typeSelect.shortValue() == 0)
				throw new IllegalValueException("Please specify a type.");


			adminCheckoutCode.save();
			
			
			if (admin_company.getQuickBooksSettings().isQuickBooksFSEnabled()) {
				
				QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.getQueue(admin_company.getQuickBooksSettings());
			
				//CheckoutCodeBean new_code = (CheckoutCodeBean)itr.next();
				//System.out.println("new code found >" + new_code.getLabel());
				QBXMLItemAddRequest item_add_req_obj = new QBXMLItemAddRequest();
				item_add_req_obj.setCheckoutCode(adminCheckoutCode);
				item_add_req_obj.setCompany(admin_company);
				queue.add(admin_company.getQuickBooksSettings().getCompanyKeyString(), item_add_req_obj);
			
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));

			/*
			session.setAttribute("error-message", x.getMessage());
			forwardString = "failure";
			 */
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

