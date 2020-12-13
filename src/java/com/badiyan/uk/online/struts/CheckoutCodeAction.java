/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.UnitOfMeasureDbItemMapping;
import com.badiyan.torque.UnitOfMeasureDbItemMappingPeer;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import com.valeo.qb.data.AccountRet;
import com.valeo.qb.data.SalesTaxCodeRet;
import com.valeo.qb.data.VendorRet;
import java.math.*;
import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 * Implementation of <strong>Action</strong> that validates a client profile.
 *
 * @author Marlo Stueve
 */
public final class
CheckoutCodeAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CheckoutCodeAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;

		CompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;
		CheckoutCodeBean adminCheckoutCode = null;

		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null) {
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)loginBean.getPerson();

				admin_company = (CompanyBean)session.getAttribute("adminCompany");
				adminCheckoutCode = (CheckoutCodeBean)session.getAttribute("adminCheckoutCode");
			} else {
				return (_mapping.findForward("session_expired"));
			}
			
			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements()) {
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));
			}

			/*
			 *
    <form-bean       name="newCheckoutCodeForm"
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
      <form-property name="practiceAreaSelect" type="java.lang.Integer"/>
      <form-property name="planUse" type="java.lang.Boolean"/>
      <form-property name="commission" type="java.lang.Boolean"/>
      <form-property name="taxCode" type="java.lang.Integer"/>
      <form-property name="cogsAccountSelect" type="java.lang.Integer"/>
      <form-property name="incomeAccountSelect" type="java.lang.Integer"/>
      <form-property name="assetAccountSelect" type="java.lang.Integer"/>
    </form-bean>
			 */

			String itemNumberInput = (String)PropertyUtils.getSimpleProperty(_form, "itemNumberInput");
			String upcInput = (String)PropertyUtils.getSimpleProperty(_form, "upcInput");
			String itemNameInput = (String)PropertyUtils.getSimpleProperty(_form, "descriptionInput");
			Boolean isActive = (Boolean)PropertyUtils.getSimpleProperty(_form, "isActive");
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
			//Integer taxItem = (Integer)PropertyUtils.getSimpleProperty(_form, "taxItem");

			Integer cogsAccountSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "cogsAccountSelect");
			Integer incomeAccountSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "incomeAccountSelect");
			Integer assetAccountSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "assetAccountSelect");
			Integer expenseAccountSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "expenseAccountSelect");

			String amountPerMLInput = (String)PropertyUtils.getSimpleProperty(_form, "amountPerMLInput");
			
			if (_request.getParameter("delete_button") != null && _request.getParameter("delete_button").equals("Delete")) {
				
				//CheckoutCodeBean code_to_delete = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(_request.getParameter("checkoutCodeId")));
				CheckoutCodeBean.delete(Integer.parseInt(_request.getParameter("checkoutCodeId")));
				
			} else {
				CheckoutCodeAction.saveCheckoutCode(admin_company, adminPerson, adminCheckoutCode.getId(), itemNumberInput, upcInput, codeInput, itemNameInput, salesDescInput, isActive, amountInput, costInput, amountPerMLInput, unitCostInput, typeSelect, vendorSelect, departmentSelect, practiceAreaSelect, planUse, commission, taxCode, onHandInput, reorderPointInput, cogsAccountSelect, incomeAccountSelect, assetAccountSelect, expenseAccountSelect);
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
		return (_mapping.findForward(forwardString));
	}
	
	
	
	public static CheckoutCodeBean
	saveCheckoutCode(CompanyBean admin_company, UKOnlinePersonBean _mod_person, int _id, String itemNumberInput, String upcInput, String codeInput, String itemNameInput, String salesDescInput, Boolean isActive, String amountInput, String costInput, String amountPerMLInput, String unitCostInput, Short typeSelect, Integer vendorSelect, Integer departmentSelect, Integer practiceAreaSelect, Boolean planUse, Boolean commission, Integer taxCode, Short onHandInput, Short reorderPointInput, Integer cogsAccountSelect, Integer incomeAccountSelect, Integer assetAccountSelect, Integer expenseAccountSelect) throws TorqueException, ObjectNotFoundException, IllegalValueException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, Exception {

		CheckoutCodeBean adminCheckoutCode = null;
		
		if (_id > 0) {
			adminCheckoutCode = CheckoutCodeBean.getCheckoutCode(_id);
		} else {
			adminCheckoutCode = new CheckoutCodeBean();
		}
		
		adminCheckoutCode.setCode(codeInput);
		adminCheckoutCode.setDescription(itemNameInput);
		adminCheckoutCode.setSalesDescription(salesDescInput);

		if (isActive != null && isActive.booleanValue()) {
			adminCheckoutCode.setIsActive(true);
		} else {
			adminCheckoutCode.setIsActive(false);
		}

		if (amountInput != null && !amountInput.isEmpty()) {
			BigDecimal amount = new BigDecimal(amountInput);
			adminCheckoutCode.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		if (amountPerMLInput != null && !amountPerMLInput.isEmpty()) {
			BigDecimal perMl = new BigDecimal(amountPerMLInput);
			if (perMl.compareTo(BigDecimal.ZERO) == 1) {

				UnitOfMeasure unitOfMeasure = UnitOfMeasure.maintainUnitOfMeasure(admin_company, "ml");
				Criteria crit = new Criteria();
				crit.add(UnitOfMeasureDbItemMappingPeer.CHECKOUT_CODE_ID, adminCheckoutCode.getId());
				crit.add(UnitOfMeasureDbItemMappingPeer.UNIT_OF_MEASURE_DB_ID, unitOfMeasure.getId());
				UnitOfMeasureDbItemMappingPeer.doDelete(crit);

				UnitOfMeasureDbItemMapping mapping = new UnitOfMeasureDbItemMapping();
				mapping.setCheckoutCodeId(adminCheckoutCode.getId());
				mapping.setUnitOfMeasureDbId(unitOfMeasure.getId());
				mapping.setAmount(perMl);
				mapping.save();
			}
		}

		if (costInput != null && !costInput.isEmpty()) {
			BigDecimal amount = new BigDecimal(costInput);
			adminCheckoutCode.setOrderCost(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		if (unitCostInput != null && !unitCostInput.isEmpty()) {
			BigDecimal amount = new BigDecimal(unitCostInput);
			adminCheckoutCode.setAverageUnitCost(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
		}

		adminCheckoutCode.setType(typeSelect.shortValue());

		if (vendorSelect > 0) {
			adminCheckoutCode.setVendor(VendorRet.getVendor(vendorSelect));
		} else {
			adminCheckoutCode.removeVendor();
		}

		if (departmentSelect > 0) {
			adminCheckoutCode.setInventoryDepartment(InventoryDepartment.getInventoryDepartment(departmentSelect));
		} else {
			adminCheckoutCode.removeDepartment();
		}

		if (practiceAreaSelect > 0) {
			adminCheckoutCode.setPracticeArea(PracticeAreaBean.getPracticeArea(practiceAreaSelect.intValue()));
		}

		adminCheckoutCode.setCompany(admin_company);
		//adminCheckoutCode.setModifyPerson(_mod_person);
		adminCheckoutCode.setCreateOrModifyPerson(_mod_person);

		boolean is_plan_use = (planUse == null) ? false : (planUse.booleanValue() ? true : false);
		if (is_plan_use && (adminCheckoutCode.getType() != CheckoutCodeBean.PROCEDURE_TYPE)) {
			throw new IllegalValueException("This checkout code must be a procdure in order to be a plan use.");
		}
		adminCheckoutCode.setPlanUse(is_plan_use);

		adminCheckoutCode.setCommissionable((commission == null) ? false : commission.booleanValue());

		/*
		if (taxItem.intValue() == 0)
			adminCheckoutCode.removeTaxCode();
		else
			adminCheckoutCode.setTaxCode(ValeoTaxCodeBean.getTaxCode(taxItem.intValue()));
		 */

		if (taxCode == 0) {
			adminCheckoutCode.removeSalesTaxCode();
		} else {
			adminCheckoutCode.setSalesTaxCode(SalesTaxCodeRet.getSalesTaxCode(taxCode));
		}

		if (itemNumberInput != null && !itemNumberInput.isEmpty()) {
			//adminCheckoutCode.setItemNumber(Integer.parseInt(itemNumberInput));
			adminCheckoutCode.setVendorProductNumber(itemNumberInput);
		}
		adminCheckoutCode.setUPC(upcInput);

		adminCheckoutCode.setOnHandQuantity(onHandInput);
		adminCheckoutCode.setReorderPoint(reorderPointInput);

		if (cogsAccountSelect > 0) {
			AccountRet cogsAccount = AccountRet.getAccount(cogsAccountSelect);
			adminCheckoutCode.setCostOfGoodsSoldAccount(cogsAccount);
		}

		if (incomeAccountSelect > 0) {
			AccountRet incomeAccount = AccountRet.getAccount(incomeAccountSelect);
			adminCheckoutCode.setIncomeAccount(incomeAccount);
		}

		if (assetAccountSelect > 0) {
			AccountRet assetAccount = AccountRet.getAccount(assetAccountSelect);
			adminCheckoutCode.setAssetAccount(assetAccount);
		}

		if (expenseAccountSelect > 0) {
			AccountRet expenseAccount = AccountRet.getAccount(expenseAccountSelect);
			adminCheckoutCode.setExpenseAccount(expenseAccount);
		}

		if (itemNameInput.length() == 0) {
			throw new IllegalValueException("Please provide an item name.");
		}
		if (typeSelect.shortValue() == 0) {
			throw new IllegalValueException("Please specify a type.");
		}

		adminCheckoutCode.save();
		
		return adminCheckoutCode;
	}
}

