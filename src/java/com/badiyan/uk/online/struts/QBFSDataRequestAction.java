/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.ObjectNotFoundException;

import com.badiyan.uk.online.beans.*;

import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;
import com.valeo.qb.QBXMLAccountQueryRequest;
import com.valeo.qb.QBXMLCustomerQueryRequest;
import com.valeo.qb.QBXMLGenericQueryRequest;
import com.valeo.qb.QBXMLInvoiceQueryRequest;
import com.valeo.qb.QBXMLItemAddRequest;
import com.valeo.qb.QBXMLItemModRequest;
import com.valeo.qb.QBXMLItemReceiptAddRequest;
import com.valeo.qb.QBXMLReceivePaymentQueryRequest;
import com.valeo.qb.QBXMLSalesReceiptQueryRequest;
import com.valeo.qb.QBXMLVendorQueryRequest;
import com.valeo.qb.data.ItemPaymentRet;
import com.valeo.qb.data.ItemReceiptRet;
import com.valeo.qb.data.ItemSalesTaxRet;

import com.valeo.qbpos.QBPOSXMLRequestQueue;
import com.valeo.qbpos.data.CustomerRet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a client profile.
 *
 * @author Marlo Stueve
 */
public final class
QBFSDataRequestAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in QBFSDataRequestAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;

		UKOnlineCompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;

		QBXMLGenericQueryRequest adminQBXMLRequest = null;

		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)loginBean.getPerson();

				admin_company = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				adminQBXMLRequest = (QBXMLGenericQueryRequest)session.getAttribute("adminQBXMLRequest");
			}
			else
				return (_mapping.findForward("session_expired"));

			/*
			 *
    <form-bean       name="qbfsDataRequestForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="requestInput" type="java.lang.String"/>
      <form-property name="typeSelect" type="java.lang.String"/>
      <form-property name="iterator" type="java.lang.Boolean"/>
      <form-property name="maxReturned" type="java.lang.Integer"/>
      <form-property name="timeModified" type="java.lang.String"/>
    </form-bean>
			 */

			String requestInput = (String)PropertyUtils.getSimpleProperty(_form, "requestInput");
			Boolean iterator = (Boolean)PropertyUtils.getSimpleProperty(_form, "iterator");
			Integer maxReturned = (Integer)PropertyUtils.getSimpleProperty(_form, "maxReturned");
			String timeModified = (String)PropertyUtils.getSimpleProperty(_form, "timeModified");
			
			Boolean sync_accounts = (Boolean)PropertyUtils.getSimpleProperty(_form, "sync_accounts");
			Boolean sync_vendors = (Boolean)PropertyUtils.getSimpleProperty(_form, "sync_vendors");
			Boolean sync_clients = (Boolean)PropertyUtils.getSimpleProperty(_form, "sync_clients");
			Boolean sync_inventory = (Boolean)PropertyUtils.getSimpleProperty(_form, "sync_inventory");
			Boolean sync_sales_receipts = (Boolean)PropertyUtils.getSimpleProperty(_form, "sync_sales_receipts");
			Boolean sync_invoices = (Boolean)PropertyUtils.getSimpleProperty(_form, "sync_invoices");
			
			

			System.out.println("requestInput >" + requestInput);
			System.out.println("iterator >" + iterator);
			System.out.println("maxReturned >" + maxReturned);
			System.out.println("timeModified >" + timeModified);
			
			System.out.println("sync_accounts >" + sync_accounts);
			System.out.println("sync_vendors >" + sync_vendors);
			System.out.println("sync_clients >" + sync_clients);
			System.out.println("sync_inventory >" + sync_inventory);
			System.out.println("sync_sales_receipts >" + sync_sales_receipts);

			

			QuickBooksSettings qb_settings = admin_company.getQuickBooksSettings();
			QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.getQueue(qb_settings);
			
			if (sync_sales_receipts != null && sync_sales_receipts.booleanValue())
			{
				// query qb for all sales receipts...

				QBXMLSalesReceiptQueryRequest sales_receipt_query_req_obj = new QBXMLSalesReceiptQueryRequest();
				sales_receipt_query_req_obj.setMaxReturned(100);
				sales_receipt_query_req_obj.setIterate(true);
				sales_receipt_query_req_obj.setIncludeLineItems(true);
				sales_receipt_query_req_obj.setCompany(admin_company);
				queue.add(qb_settings.getCompanyKeyString(), sales_receipt_query_req_obj);
				
				
			}
			
			if (sync_invoices != null && sync_invoices.booleanValue())
			{
				// query qb for all invoices...
				
				QBXMLInvoiceQueryRequest invoice_query_req_obj = new QBXMLInvoiceQueryRequest();
				invoice_query_req_obj.setMaxReturned(100);
				invoice_query_req_obj.setIterate(true);
				invoice_query_req_obj.setIncludeLineItems(true);
				invoice_query_req_obj.setCompany(admin_company);
				queue.add(qb_settings.getCompanyKeyString(), invoice_query_req_obj);
				
				// grab receive payments too
				
				QBXMLReceivePaymentQueryRequest receive_payment_req_obj = new QBXMLReceivePaymentQueryRequest();
				receive_payment_req_obj.setIterate(true);
				receive_payment_req_obj.setMaxReturned(100);
				receive_payment_req_obj.setCompany(admin_company);
				queue.add(qb_settings.getCompanyKeyString(), receive_payment_req_obj);
				
			}
			
			if (sync_accounts != null && sync_accounts.booleanValue())
			{
				// query qb for all accounts...


				
				QBXMLAccountQueryRequest account_query_req_obj = new QBXMLAccountQueryRequest();
				account_query_req_obj.setMaxReturned(50000);
				account_query_req_obj.setCompany(admin_company);
				queue.add(qb_settings.getCompanyKeyString(), account_query_req_obj);
				
				
				
				
				/*
				Calendar one_month_ago = Calendar.getInstance();
				one_month_ago.set(Calendar.MONTH, -1);
				QBXMLGenericQueryRequest item_request = new QBXMLGenericQueryRequest();
				item_request.setTimeModified(one_month_ago.getTime());
				item_request.setRequestName("SalesReceiptQueryRq");
				//item_request.setActiveStatus("All");
				item_request.setMaxReturned(10000);
				//item_request.setIterate(true);
				item_request.setUseModifiedDateRangeFilter(true);
				queue.add(qb_settings.getCompanyKeyString(), item_request);
				 */


				/*
				Calendar one_month_ago = Calendar.getInstance();
				one_month_ago.set(Calendar.MONTH, -12);
				QBXMLGenericQueryRequest item_request = new QBXMLGenericQueryRequest();
				item_request.setTimeModified(one_month_ago.getTime());
				item_request.setRequestName("InvoiceQueryRq");
				//item_request.setActiveStatus("All");
				item_request.setMaxReturned(10000);
				//item_request.setIterate(true);
				item_request.setUseModifiedDateRangeFilter(true);
				item_request.setIncludeLineItems(true);
				queue.add(qb_settings.getCompanyKeyString(), item_request);
				 *
				 */
				 

/*
				Calendar one_month_ago = Calendar.getInstance();
				one_month_ago.set(Calendar.MONTH, -2);
				QBXMLGenericQueryRequest item_request = new QBXMLGenericQueryRequest();
				item_request.setTimeModified(one_month_ago.getTime());
				item_request.setRequestName("CreditMemoQueryRq");
				//item_request.setActiveStatus("All");
				item_request.setMaxReturned(100);
				//item_request.setIterate(true);
				item_request.setUseModifiedDateRangeFilter(true);
				item_request.setIncludeLineItems(true);
				queue.add(qb_settings.getCompanyKeyString(), item_request);
 * 
 */


				/*
				Timer timerObj = new Timer(true);
				timerObj.schedule(new ResubmitQuickbooksTransactionsTask(session, admin_company), (long)0);
				*/
				
				
				
				
				
				
				/*
				
				QBXMLReceivePaymentQueryRequest receive_payment_req_obj = new QBXMLReceivePaymentQueryRequest();

				Date last_update = ReceivePaymentRet.getLastUpdateDate(admin_company);
				System.out.println("ReceivePaymentRet.getLastUpdateDate >" + last_update);
				if (last_update == null)
				{
					// no receive payments found in the database.  get them all

					receive_payment_req_obj.setIterate(true);
				}
				else
				{
					Calendar some_date = Calendar.getInstance();
					some_date.setTime(last_update);
					some_date.add(Calendar.SECOND, 1);
					last_update = some_date.getTime();
					receive_payment_req_obj.setTimeModified(last_update);
					receive_payment_req_obj.setMatchNumericCriterion(QBXMLRequest.GREATER_THAN_MATCH_NUMERIC_CRITERION);
					receive_payment_req_obj.setIterate(true);
				}
				
				queue.add(qb_settings.getCompanyKeyString(), receive_payment_req_obj);
				 * 
				 */
				
			}
			
			if (sync_vendors != null && sync_vendors.booleanValue())
			{
				// query qb for all clients...

				QBXMLVendorQueryRequest vendor_query_req_obj = new QBXMLVendorQueryRequest();
				vendor_query_req_obj.setMaxReturned(100);
				vendor_query_req_obj.setIterate(true);
				vendor_query_req_obj.setCompany(admin_company);
				queue.add(qb_settings.getCompanyKeyString(), vendor_query_req_obj);
			}
			
			if (sync_inventory != null && sync_inventory.booleanValue())
			{
				
				try
				{
					CheckoutCodeBean.getSubtotalForAddRequest(admin_company);
				}
				catch (ObjectNotFoundException x) {
					x.printStackTrace();
				}
				
				try
				{
					ValeoTaxCodeBean.getSalesTaxForAddRequest(admin_company);
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
					admin_code.setCompany(admin_company);
					admin_code.save();

					// I need to create/maintain the corresponding ItemSalesTaxRet object

					ItemSalesTaxRet item_sales_tax = new ItemSalesTaxRet();
					item_sales_tax = new ItemSalesTaxRet();
					item_sales_tax.setCompany(admin_company);
					
					item_sales_tax.setName(code);
					if (percentage != null && !percentage.isEmpty())
						item_sales_tax.setTaxRate(new BigDecimal(percentage));
					item_sales_tax.removeTaxVendor();
					item_sales_tax.setTaxCode(admin_code);
					item_sales_tax.setIsActive(true);
					item_sales_tax.setItemDesc(itemDescInput);
					item_sales_tax.save();
					
				}
				
				Iterator itr = ItemSalesTaxRet.getNewSalesTaxItems(admin_company).iterator();
				while (itr.hasNext())
				{
					ItemSalesTaxRet new_tax_code = (ItemSalesTaxRet)itr.next();
					
					QBXMLItemAddRequest sales_tax_item_add_req_obj = new QBXMLItemAddRequest();
					sales_tax_item_add_req_obj.setCompany(admin_company);
					sales_tax_item_add_req_obj.setItemSalesTaxRet(new_tax_code);
					
					queue.add(qb_settings.getCompanyKeyString(), sales_tax_item_add_req_obj);
					
				}
				
				// find any local inventory items that need to be added

				itr = CheckoutCodeBean.getNewCheckoutCodes(admin_company).iterator();
				while (itr.hasNext())
				{
					CheckoutCodeBean new_code = (CheckoutCodeBean)itr.next();
					System.out.println("new code found >" + new_code.getLabel());
					QBXMLItemAddRequest item_add_req_obj = new QBXMLItemAddRequest();
					item_add_req_obj.setCheckoutCode(new_code);
					item_add_req_obj.setCompany(admin_company);
					queue.add(qb_settings.getCompanyKeyString(), item_add_req_obj);
				}
				
				// find any local ItemPaymentRet objects that need to be added
				
				itr = ItemPaymentRet.getNewSalesTaxItems(admin_company).iterator();
				while (itr.hasNext())
				{
					ItemPaymentRet payment_ret = (ItemPaymentRet)itr.next();
					QBXMLItemAddRequest item_add_req_obj = new QBXMLItemAddRequest();
					item_add_req_obj.setItemPaymentRet(payment_ret);
					item_add_req_obj.setCompany(admin_company);
					queue.add(qb_settings.getCompanyKeyString(), item_add_req_obj);
				}
				
				// find any local inventory items that need to be updated
				
				Date last_update_date = qb_settings.getLastInventoryUpdateDate();
				System.out.println("last_update_date >" + last_update_date);
				if (last_update_date != null)
				{
					// find any checkout codes that have been modified since the last update date
					
					itr = CheckoutCodeBean.getCheckoutCodesModifiedAfter(admin_company, last_update_date).iterator();
					while (itr.hasNext())
					{
						CheckoutCodeBean mod_code = (CheckoutCodeBean)itr.next();
						System.out.println("mod code found >" + mod_code.getLabel());
						QBXMLItemModRequest item_mod_req_obj = new QBXMLItemModRequest();
						item_mod_req_obj.setCheckoutCode(mod_code);
						item_mod_req_obj.setCompany(admin_company);
						queue.add(qb_settings.getCompanyKeyString(), item_mod_req_obj);
					}
				}
				
				

				// add item receipts (from received purchase orders)

				try
				{

					Iterator item_receipt_itr = ItemReceiptRet.getItemReceiptsThatNeedToBeSyncedWithQuickBooks(admin_company).iterator();
					while (item_receipt_itr.hasNext())
					{
						ItemReceiptRet item_receipt = (ItemReceiptRet)item_receipt_itr.next();
						System.out.println("FOUND CASH OUT ITEM RECEIPT >" + item_receipt.getLabel());
						try
						{
							QBXMLItemReceiptAddRequest item_receipt_add_req_obj = new QBXMLItemReceiptAddRequest();
							item_receipt_add_req_obj.setItemReceipt(item_receipt);
							item_receipt_add_req_obj.setCompany(admin_company);
							queue.add(qb_settings.getCompanyKeyString(), item_receipt_add_req_obj);
						}
						catch (Exception x)
						{
							x.printStackTrace();
						}

					}
				}
				catch (Exception x)
				{
					x.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					x.printStackTrace(pw);
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), sw.toString());
				}
				
				// query for PaymentMethodRet objects
				
				/*
				
				QBXMLGenericQueryRequest payment_method_request = new QBXMLGenericQueryRequest();
				payment_method_request.setRequestName("PaymentMethodQueryRq");
				payment_method_request.setActiveStatus("All");
				payment_method_request.setMaxReturned(1000);
				payment_method_request.setCompany(admin_company);
				queue.add(qb_settings.getCompanyKeyString(), payment_method_request);
				
				// query qb for all inventory items...

				QBXMLGenericQueryRequest tax_request = new QBXMLGenericQueryRequest();
				tax_request.setRequestName("SalesTaxCodeQueryRq");
				tax_request.setActiveStatus("All");
				tax_request.setMaxReturned(1000);
				tax_request.setIterate(false);
				tax_request.setCompany(admin_company);
				queue.add(qb_settings.getCompanyKeyString(), tax_request);


				QBXMLGenericQueryRequest item_request = new QBXMLGenericQueryRequest();
				//if (last_update_date != null)
				//	item_request.setTimeModified(last_update_date);
				item_request.setRequestName("ItemQueryRq");
				item_request.setActiveStatus("All");
				item_request.setMaxReturned(1000);
				item_request.setIterate(true);
				item_request.setUseModifiedDateRangeFilter(false);
				item_request.setCompany(admin_company);
				queue.add(qb_settings.getCompanyKeyString(), item_request);
				
				qb_settings.setInventoryUpdateRequestInQueue();
				
				
				*/
				
				try
				{
					/*
					QBXMLReceivePaymentQueryRequest receive_payment_req_obj = new QBXMLReceivePaymentQueryRequest();

					Date last_update = ReceivePaymentRet.getLastUpdateDate(admin_company);
					if (last_update == null)
					{
						// no receive payments found in the database.  get them all

						receive_payment_req_obj.setIterate(true);
					}
					else
					{
						Calendar some_date = Calendar.getInstance();
						some_date.setTime(last_update);
						some_date.add(Calendar.SECOND, 1);
						last_update = some_date.getTime();
						receive_payment_req_obj.setTimeModified(last_update);
						receive_payment_req_obj.setMatchNumericCriterion(QBXMLRequest.GREATER_THAN_MATCH_NUMERIC_CRITERION);
						receive_payment_req_obj.setIterate(true);
					}

					queue.add(qb_settings.getCompanyKeyString(), receive_payment_req_obj);

					 * 
					 */


					/*
					QBXMLInvoiceQueryRequest invoice_req_obj = new QBXMLInvoiceQueryRequest();
					queue.add(company_key, invoice_req_obj);

					QBXMLSalesReceiptQueryRequest sales_receipt_req_obj = new QBXMLSalesReceiptQueryRequest();
					queue.add(company_key, sales_receipt_req_obj);

					QBXMLCreditMemoQueryRequest credit_memo_req_obj = new QBXMLCreditMemoQueryRequest();
					queue.add(company_key, credit_memo_req_obj);

					QBXMLJournalEntryQueryRequest journal_entry_req_obj = new QBXMLJournalEntryQueryRequest();
					queue.add(company_key, journal_entry_req_obj);
					 */


					/*
					QBXMLCustomerQuery customer_req_obj = new QBXMLCustomerQuery();
					customer_req_obj.setIterate(true);
					queue.add(company_key, customer_req_obj);
					 */


					//queue.setHasPendingQBFSReceivePaymentQuery(qb_settings.getCompanyKeyString(), true);
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
				
				
			}
			
			if (sync_clients != null && sync_clients.booleanValue())
			{
				// 1.  add new local customers to QuickBooks.  the Person objects with no QBListID

				/*
				HashMap hash_customer = new HashMap();
				Iterator itr = UKOnlinePersonBean.getNewCustomersByQBListId(admin_company).iterator();
				while (itr.hasNext())
				{
					UKOnlinePersonBean customer = (UKOnlinePersonBean)itr.next();
					if ((customer.getFirstNameString().length() > 0) &&
							(customer.getLastNameString().length() > 0) &&
							(customer.getLabel().toLowerCase().indexOf("duplicate") == -1) &&
							(customer.getLabel().toLowerCase().indexOf("(dup)") == -1))
					{
						// has first and last name and hasn't been flagged as a duplicate

						if (hash_customer.get(customer.getLabel()) == null)
						{
							hash_customer.put(customer.getLabel(), customer);
							System.out.println("NEW CUSTOMER >" + customer.getLabel());
							QBXMLCustomerAddRequest customer_add_req_obj = new QBXMLCustomerAddRequest();
							customer_add_req_obj.setCustomer(customer);
							queue.add(qb_settings.getCompanyKeyString(), customer_add_req_obj);
						}
					}
				}
				 */

				// 2.  find locally modified customers since the last CashOut and update QuickBooks

				/*
				Date last_update_date = qb_settings.getLastClientUpdateDate();
				System.out.println("last_update_date >" + last_update_date);
				if (last_update_date != null)
				{
					itr = UKOnlinePersonBean.getUpdatedCustomersByQBListId(admin_company, last_update_date).iterator();
					while (itr.hasNext())
					{
						UKOnlinePersonBean customer = (UKOnlinePersonBean)itr.next();
						System.out.println("UPDATED CUSTOMER >" + customer.getLabel());
						QBXMLCustomerModRequest customer_mod_req_obj = new QBXMLCustomerModRequest();
						customer_mod_req_obj.setCustomer(customer);
						queue.add(qb_settings.getCompanyKeyString(), customer_mod_req_obj);
					}
				}
				 */

				// 3. find any CustomerRet objects that have no EDIT_SEQUENCE value.  this happens if the CustomerRet initial came from QBPOS
				// do anything with this???
				
				// query qb for all clients...

				/*
				QBXMLCustomerQueryRequest customer_query_req_obj = new QBXMLCustomerQueryRequest();
				customer_query_req_obj.setActiveStatus("All");
				customer_query_req_obj.setMaxReturned(100);
				customer_query_req_obj.setIterate(true);
				queue.add(qb_settings.getCompanyKeyString(), customer_query_req_obj);
				 */

				CustomerRet.resetCustomers(admin_company);

				

				QBXMLCustomerQueryRequest customer_query_req_obj = new QBXMLCustomerQueryRequest();
				//if (last_update_date != null)
				//	customer_query_req_obj.setFromModifiedDate(last_update_date);
				customer_query_req_obj.setActiveStatus("All");
				customer_query_req_obj.setMaxReturned(5);
				customer_query_req_obj.setIterate(true);
				customer_query_req_obj.setCompany(admin_company);
				queue.add(qb_settings.getCompanyKeyString(), customer_query_req_obj);
				
					
				qb_settings.setClientUpdateRequestInQueue();
				
			}
			
			

			if (requestInput.length() > 0)
			{

				if (requestInput.startsWith("<QBXML>") || requestInput.startsWith("<qbxml>"))
				{
					adminQBXMLRequest.setRawRequest(requestInput);
				}
				else if (requestInput.equals("inactivateCustomersNoLongerInQB"))
				{
					CustomerRet.inactivateCustomersNoLongerInQB(admin_company);
				}
				else
				{
					// add the request to the queue...

					adminQBXMLRequest.setRequestName(requestInput);
					adminQBXMLRequest.setIterate((iterator == null) ? false : iterator.booleanValue());
					adminQBXMLRequest.setMaxReturned(maxReturned.intValue());
					adminQBXMLRequest.setTimeModified((timeModified.length() == 0) ? null : CUBean.getDateFromUserString(timeModified));

					/*
					QBWebConnectorSvcSoapImpl.qbfs_request_queue.add(QBWebConnectorSvcSoapImpl.qbfs_company_key, adminQBXMLRequest);
					QBWebConnectorSvcSoapImpl.qbfs_request_queue.setHasPendingQBFSQuery(QBWebConnectorSvcSoapImpl.qbfs_company_key, true);
					 */
				}

				adminQBXMLRequest.setCompany(admin_company);
				queue.add(qb_settings.getCompanyKeyString(), adminQBXMLRequest);
				queue.setHasPendingQBFSQuery(qb_settings.getCompanyKeyString(), true);

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
}

