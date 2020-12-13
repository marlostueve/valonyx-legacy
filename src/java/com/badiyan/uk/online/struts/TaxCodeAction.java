/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.ObjectNotFoundException;

import com.badiyan.uk.online.beans.*;
import com.valeo.qb.data.ItemSalesTaxRet;
import com.valeo.qb.data.SalesTaxCodeRet;
import com.valeo.qb.data.VendorRet;

import java.math.BigDecimal;

import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
TaxCodeAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in TaxCodeAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;

		UKOnlineCompanyBean admin_company = null;
		ValeoTaxCodeBean admin_code = null;
		ItemSalesTaxRet item_sales_tax = null;

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
				admin_code = (ValeoTaxCodeBean)session.getAttribute("adminTaxCode");
				item_sales_tax = (ItemSalesTaxRet)session.getAttribute("itemSalesTaxRet");
			}
			else
				return (_mapping.findForward("session_expired"));

			/*
			 *<form-bean       name="taxCodeForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="code" type="java.lang.String"/>
      <form-property name="percentage" type="java.lang.String"/>
      <form-property name="sales_tax_code" type="java.lang.Integer"/>
    </form-bean>
			 */


			if (_request.getParameter("delete_id").equals(""))
			{
			    String code = (String)PropertyUtils.getSimpleProperty(_form, "code");
			    String percentage = (String)PropertyUtils.getSimpleProperty(_form, "percentage");
			    Integer sales_tax_code = (Integer)PropertyUtils.getSimpleProperty(_form, "sales_tax_code");
			    Boolean is_default = (Boolean)PropertyUtils.getSimpleProperty(_form, "default");
			    Integer vendorSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "vendorSelect");
			    String itemDescInput = (String)PropertyUtils.getSimpleProperty(_form, "itemDescInput");

			    System.out.println("code >" + code);
			    System.out.println("percentage >" + percentage);
			    System.out.println("sales_tax_code >" + sales_tax_code);
			    System.out.println("vendorSelect >" + vendorSelect);
			    System.out.println("is_default >" + is_default);
				
				boolean is_new_code = admin_code.isNew();

			    admin_code.setCode(code);
				if (percentage != null && !percentage.isEmpty())
					admin_code.setPercentage(new BigDecimal(percentage));
				admin_code.setCompany(admin_company);

			    admin_code.save();
				
				
				if (sales_tax_code.intValue() > 0)
				{
					SalesTaxCodeRet sales_tax_code_ret = SalesTaxCodeRet.getSalesTaxCode(sales_tax_code.intValue());
					//admin_code.setSalesTaxCodeListID(sales_tax_code_ret.getListID());
					sales_tax_code_ret.setDefaultTaxCode(admin_code);
					sales_tax_code_ret.save();
				}
				
				
				// I need to create/maintain the corresponding ItemSalesTaxRet object
				
				if (is_new_code)
				{
					item_sales_tax = new ItemSalesTaxRet();
					item_sales_tax.setCompany(admin_company);
				}
				else
				{
					try
					{
						item_sales_tax = ItemSalesTaxRet.getSalesTaxItem(admin_company, admin_code);
					}
					catch (ObjectNotFoundException x)
					{
						x.printStackTrace();
					}
				}
				item_sales_tax.setName(code);
				if (percentage != null && !percentage.isEmpty())
					item_sales_tax.setTaxRate(new BigDecimal(percentage));
				if (vendorSelect.intValue() > 0)
					item_sales_tax.setTaxVendor(VendorRet.getVendor(vendorSelect.intValue()));
				else
					item_sales_tax.removeTaxVendor();
				item_sales_tax.setTaxCode(admin_code);
				item_sales_tax.setIsActive(true);
				item_sales_tax.setItemDesc(itemDescInput);
				item_sales_tax.save();
				
				
			    session.removeAttribute("adminTaxCode");
				session.removeAttribute("itemSalesTaxRet");
			}
			else
			{
			    TaxCodeBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
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
