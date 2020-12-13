/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.tasks;


import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.online.beans.*;

import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;
import com.valeo.qb.*;
import com.valeo.qb.data.ItemSalesTaxRet;
import com.valeo.qb.data.SalesTaxCodeRet;
import com.valeo.qb.data.VendorRet;
import com.valeo.qbpos.*;
import com.valeo.qbpos.data.CustomerRet;
import java.math.BigDecimal;
import java.util.*;





public class
QBFSDataRequestTask
    extends TimerTask
{
    // INSTANCE VARIABLES

	private UKOnlineCompanyBean company;

    // CONSTRUCTORS

    public
    QBFSDataRequestTask(UKOnlineCompanyBean _company)
    {
		company = _company;
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in QBFSDataRequestTask ");

		try
		{
			
			QuickBooksSettings qb_settings = company.getQuickBooksSettings();
			String company_key = qb_settings.getCompanyKeyString();
			System.out.println("company_key >" + company_key);
			if ((company_key == null) || (company_key.length() == 0))
				throw new IllegalValueException("Company Key not found for " + company.getLabel());

			// obtain the proper queue

			QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.getQueue(qb_settings);

			// I need to load up the queue...

			/*
			Boolean sync_accounts = new Boolean(qb_settings.isSyncAccounts());
			Boolean sync_vendors = new Boolean(qb_settings.isSyncVendors());
			Boolean sync_clients = new Boolean(qb_settings.isSyncClients());
			Boolean sync_inventory = new Boolean(qb_settings.isSyncInventory());
			 * 
			 */
			
			
			if (!qb_settings.isSyncAccounts())
			{
				// query qb for all accounts...

				QBXMLAccountQueryRequest account_query_req_obj = new QBXMLAccountQueryRequest();
				account_query_req_obj.setMaxReturned(50000);
				account_query_req_obj.setCompany(company);
				queue.add(qb_settings.getCompanyKeyString(), account_query_req_obj);
			}
			
			if (!qb_settings.isSyncVendors())
			{
				// query qb for all clients...

				QBXMLVendorQueryRequest vendor_query_req_obj = new QBXMLVendorQueryRequest();
				vendor_query_req_obj.setMaxReturned(100);
				vendor_query_req_obj.setIterate(true);
				vendor_query_req_obj.setCompany(company);
				queue.add(qb_settings.getCompanyKeyString(), vendor_query_req_obj);
			}
			
			if (!qb_settings.isSyncInventory())
			{
				
				try
				{
					CheckoutCodeBean.getSubtotalForAddRequest(company);
				}
				catch (ObjectNotFoundException x) {
					x.printStackTrace();
				}
				
				try
				{
					ValeoTaxCodeBean.getSalesTaxForAddRequest(company);
				}
				catch (ObjectNotFoundException x) {
					
					
					String code = ValeoTaxCodeBean.SALES_TAX_FOR_ADD_REQUEST_NAME;
					String percentage = "0";
					//Integer sales_tax_code = (Integer)PropertyUtils.getSimpleProperty(_form, "sales_tax_code");
					//Boolean is_default = (Boolean)PropertyUtils.getSimpleProperty(_form, "default");
					//Integer vendorSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "vendorSelect");
					String itemDescInput = ValeoTaxCodeBean.SALES_TAX_FOR_ADD_REQUEST_DESC;

					ValeoTaxCodeBean admin_code = new ValeoTaxCodeBean();
					admin_code.setCode(code);
					if (percentage != null && !percentage.isEmpty())
						admin_code.setPercentage(new BigDecimal(percentage));
					admin_code.setCompany(company);
					admin_code.save();

					// I need to create/maintain the corresponding ItemSalesTaxRet object

					ItemSalesTaxRet item_sales_tax = new ItemSalesTaxRet();
					item_sales_tax = new ItemSalesTaxRet();
					item_sales_tax.setCompany(company);
					
					item_sales_tax.setName(code);
					if (percentage != null && !percentage.isEmpty())
						item_sales_tax.setTaxRate(new BigDecimal(percentage));
					item_sales_tax.removeTaxVendor();
					item_sales_tax.setTaxCode(admin_code);
					item_sales_tax.setIsActive(true);
					item_sales_tax.setItemDesc(itemDescInput);
					item_sales_tax.save();
					
					
					QBXMLItemAddRequest sales_tax_item_add_req_obj = new QBXMLItemAddRequest();
					sales_tax_item_add_req_obj.setCompany(company);
					sales_tax_item_add_req_obj.setItemSalesTaxRet(item_sales_tax);
					
					queue.add(qb_settings.getCompanyKeyString(), sales_tax_item_add_req_obj);
				}
				
				// query for PaymentMethodRet objects
				
				QBXMLGenericQueryRequest payment_method_request = new QBXMLGenericQueryRequest();
				payment_method_request.setRequestName("PaymentMethodQueryRq");
				payment_method_request.setActiveStatus("All");
				payment_method_request.setMaxReturned(1000);
				payment_method_request.setCompany(company);
				queue.add(qb_settings.getCompanyKeyString(), payment_method_request);
				
				// query qb for all inventory items...

				QBXMLGenericQueryRequest tax_request = new QBXMLGenericQueryRequest();
				tax_request.setRequestName("SalesTaxCodeQueryRq");
				tax_request.setActiveStatus("All");
				tax_request.setMaxReturned(1000);
				tax_request.setIterate(false);
				tax_request.setCompany(company);
				queue.add(qb_settings.getCompanyKeyString(), tax_request);

				QBXMLGenericQueryRequest item_request = new QBXMLGenericQueryRequest();
				item_request.setRequestName("ItemQueryRq");
				item_request.setActiveStatus("All");
				item_request.setMaxReturned(1000);
				item_request.setIterate(true);
				item_request.setUseModifiedDateRangeFilter(false);
				item_request.setCompany(company);
				queue.add(qb_settings.getCompanyKeyString(), item_request);

				item_request = new QBXMLGenericQueryRequest();
				item_request.setRequestName("ItemQueryRq");
				item_request.setActiveStatus("All");
				item_request.setMaxReturned(1000);
				item_request.setIterate(true);
				item_request.setUseModifiedDateRangeFilter(false);
				item_request.setCompany(company);
				queue.add(qb_settings.getCompanyKeyString(), item_request);
				
				qb_settings.setInventoryUpdateRequestInQueue();
				
			}
			
			if (!qb_settings.isSyncClients())
			{
				CustomerRet.resetCustomers(company);

				QBXMLCustomerQueryRequest customer_query_req_obj = new QBXMLCustomerQueryRequest();
				//if (last_update_date != null)
				//	customer_query_req_obj.setFromModifiedDate(last_update_date);
				customer_query_req_obj.setActiveStatus("All");
				customer_query_req_obj.setMaxReturned(100);
				customer_query_req_obj.setIterate(true);
				customer_query_req_obj.setCompany(company);
				queue.add(qb_settings.getCompanyKeyString(), customer_query_req_obj);
				
				qb_settings.setClientUpdateRequestInQueue();
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }

}
