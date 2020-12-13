/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.tasks;


import com.badiyan.torque.TenderRetDbOrderMapping;
import com.badiyan.torque.TenderRetDbOrderMappingPeer;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.online.beans.*;

import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;
import com.valeo.qb.*;
import com.valeo.qb.data.*;
import com.valeo.qbpos.*;
import com.valeo.qbpos.data.TenderRet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;


import javax.servlet.http.HttpSession;
import org.apache.torque.util.Criteria;



public class
UpdateQuickBooksTask
    extends TimerTask
{
    // INSTANCE VARIABLES

	private CashOut cash_out;
	private UKOnlineCompanyBean company;

	private HttpSession session;
	private StringBuffer status;

    // CONSTRUCTORS

    public
    UpdateQuickBooksTask(HttpSession _session, CashOut _cash_out, UKOnlineCompanyBean _company)
    {
		session = _session;
		cash_out = _cash_out;
		company = _company;
		status = (StringBuffer)session.getAttribute("update_quickbooks_status");
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in UpdateQuickBooksTask ");
		
		QuickBooksSettings qb_settings = null;

		try
		{
			Date last_completed_cash_out_date = CashOut.getLastCompletedCashOutDate(company);

			System.out.println("last_completed_cash_out_date >" + last_completed_cash_out_date);

			qb_settings = company.getQuickBooksSettings();
			qb_settings.setActiveCashOut(cash_out);
			String company_key = qb_settings.getCompanyKeyString();
			System.out.println("company_key >" + company_key);
			if ((company_key == null) || (company_key.length() == 0))
				throw new IllegalValueException("Company Key not found for " + company.getLabel());

			qb_settings.setMessageBuffer(status);
			qb_settings.addMessage("Building Update Queue");
			//status.append("Building Update Queue|");

			// obtain the proper queue

			QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.getQueue(qb_settings);

			// I need to load up the queue...


			if (last_completed_cash_out_date != null)
			{
				try
				{
					// query QuickBooks for any modified accounts since the last CashOut

					QBXMLAccountQueryRequest account_req_obj = new QBXMLAccountQueryRequest();
					if (last_completed_cash_out_date != null)
						account_req_obj.setFromModifiedDate(last_completed_cash_out_date);
					account_req_obj.setCompany(company);
					queue.add(company_key, account_req_obj);

				}
				catch (Exception x)
				{
					x.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					x.printStackTrace(pw);
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), sw.toString());
				}
			}



			if (last_completed_cash_out_date != null)
			{
				try
				{
					// 1.  add new local vendors to QuickBooks

					Iterator itr = VendorRet.getNewVendors(company).iterator();
					while (itr.hasNext())
					{
						VendorRet vendor = (VendorRet)itr.next();
						QBXMLVendorAddRequest vendor_add_req_obj = new QBXMLVendorAddRequest();
						vendor_add_req_obj.setIsAddRequest(true);
						vendor_add_req_obj.setVendor(vendor);
						vendor_add_req_obj.setCompany(company);
						queue.add(company_key, vendor_add_req_obj);
					}

					// 2.  find locally modified vendors since the last CashOut and update QuickBooks

					itr = VendorRet.getVendors(company, last_completed_cash_out_date, true).iterator();
					while (itr.hasNext())
					{
						VendorRet vendor = (VendorRet)itr.next();
						QBXMLVendorModRequest vendor_mod_req_obj = new QBXMLVendorModRequest();
						vendor_mod_req_obj.setVendor(vendor);
						vendor_mod_req_obj.setCompany(company);
						queue.add(company_key, vendor_mod_req_obj);
					}

					// 3.  query QuickBooks for any modified vendors since the last CashOut

					QBXMLVendorQueryRequest vendor_query_req_obj = new QBXMLVendorQueryRequest();
					//Date last_update = VendorRet.getLastUpdateDate(qb_settings.getCompany());
					if (last_completed_cash_out_date != null)
						vendor_query_req_obj.setFromModifiedDate(last_completed_cash_out_date);
					vendor_query_req_obj.setCompany(company);
					queue.add(company_key, vendor_query_req_obj);


				}
				catch (Exception x)
				{
					x.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					x.printStackTrace(pw);
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), sw.toString());
				}
			}

			
			try
			{
				// find any local inventory items that need to be added

				Iterator itr = CheckoutCodeBean.getNewCheckoutCodes(company).iterator();
				while (itr.hasNext())
				{
					CheckoutCodeBean new_code = (CheckoutCodeBean)itr.next();
					System.out.println("new code found >" + new_code.getLabel());
					QBXMLItemAddRequest item_add_req_obj = new QBXMLItemAddRequest();
					item_add_req_obj.setIsAddRequest(true);
					item_add_req_obj.setCheckoutCode(new_code);
					item_add_req_obj.setCompany(company);
					queue.add(qb_settings.getCompanyKeyString(), item_add_req_obj);
				}

				// find any local inventory items that need to be updated

				Date last_update_date = qb_settings.getLastInventoryUpdateDate();
				System.out.println("last_update_date >" + last_update_date);
				if (last_update_date != null)
				{
					// find any checkout codes that have been modified since the last update date

					itr = CheckoutCodeBean.getCheckoutCodesModifiedAfter(company, last_update_date).iterator();
					while (itr.hasNext())
					{
						CheckoutCodeBean mod_code = (CheckoutCodeBean)itr.next();
						System.out.println("mod code found >" + mod_code.getLabel());
						QBXMLItemModRequest item_mod_req_obj = new QBXMLItemModRequest();
						item_mod_req_obj.setCheckoutCode(mod_code);
						item_mod_req_obj.setCompany(company);
						queue.add(qb_settings.getCompanyKeyString(), item_mod_req_obj);
					}
				}

				// 1. get all of the items from QuickBooks and see if I can sync them with whatever inventory is local

				QBXMLGenericQueryRequest tax_request = new QBXMLGenericQueryRequest();
				tax_request.setRequestName("SalesTaxCodeQueryRq");
				tax_request.setActiveStatus("All");
				tax_request.setMaxReturned(1000);
				tax_request.setIterate(false);
				tax_request.setCompany(company);
				queue.add(company_key, tax_request);

				// do I really need to be getting all inventory items here???

				/*
				QBXMLGenericQueryRequest item_request = new QBXMLGenericQueryRequest();
				System.out.println("last_completed_cash_out_date >" + last_completed_cash_out_date);
				if (last_completed_cash_out_date != null)
					item_request.setTimeModified(last_completed_cash_out_date);
				item_request.setRequestName("ItemQueryRq");
				item_request.setActiveStatus("All");
				item_request.setMaxReturned(1000);
				item_request.setIterate(true);
				item_request.setUseModifiedDateRangeFilter(false);
				queue.add(company_key, item_request);
				*/

				QBXMLGenericQueryRequest item_request = new QBXMLGenericQueryRequest();
				if (last_update_date != null)
					item_request.setTimeModified(last_update_date);
				item_request.setRequestName("ItemQueryRq");
				item_request.setActiveStatus("All");
				item_request.setMaxReturned(1000);
				item_request.setIterate(true);
				item_request.setUseModifiedDateRangeFilter(false);
				item_request.setCompany(company);
				queue.add(qb_settings.getCompanyKeyString(), item_request);

				qb_settings.setInventoryUpdateRequestInQueue();
			}
			catch (Exception x)
			{
				x.printStackTrace();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				x.printStackTrace(pw);
				CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), sw.toString());
			}
			

			//if (qb_settings.isSyncClients() && (last_completed_cash_out_date != null))
			
			try
			{
				// 1.  add new local customers to QuickBooks.  the Person objects with no QBListID

				HashMap hash_customer = new HashMap();
				Iterator itr = UKOnlinePersonBean.getNewCustomersByQBListId(company).iterator();
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
							customer_add_req_obj.setIsAddRequest(true);
							customer_add_req_obj.setCustomer(customer);
							customer_add_req_obj.setCompany(company);
							queue.add(company_key, customer_add_req_obj);
						}
					}
				}

				// 2.  find locally modified customers since the last CashOut and update QuickBooks

				Date last_update_date = qb_settings.getLastClientUpdateDate();
				System.out.println("last_update_date >" + last_update_date);
				if (last_update_date != null)
				{
					itr = UKOnlinePersonBean.getUpdatedCustomersByQBListId(company, last_update_date).iterator();
					while (itr.hasNext())
					{
						UKOnlinePersonBean customer = (UKOnlinePersonBean)itr.next();
						System.out.println("UPDATED CUSTOMER >" + customer.getLabel());
						QBXMLCustomerModRequest customer_mod_req_obj = new QBXMLCustomerModRequest();
						customer_mod_req_obj.setCustomer(customer);
						customer_mod_req_obj.setCompany(company);
						queue.add(company_key, customer_mod_req_obj);
					}
				}

				// 3. find any CustomerRet objects that have no EDIT_SEQUENCE value.  this happens if the CustomerRet initial came from QBPOS
				// do anything with this???

				qb_settings.setClientUpdateRequestInQueue();


			}
			catch (Exception x)
			{
				x.printStackTrace();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				x.printStackTrace(pw);
				CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), sw.toString());
			}
			

			// add sales receipts and invoices

			try
			{
				// Find all of the Orders in the cash out window.  it would be nice to have a mechanism to only grab orders that haven't already been settled with QB

				Vector cash_out_orders = cash_out.getOrders();

				Iterator cash_out_orders_itr = cash_out_orders.iterator();
				while (cash_out_orders_itr.hasNext())
				{
					ValeoOrderBean cash_out_order = (ValeoOrderBean)cash_out_orders_itr.next();
					
					if (cash_out_order.isReturn())
					{
						System.out.println("FOUND RETURN ORDER >" + cash_out_order.getLabel());

						try
						{
							// what if the tenders for this order are greater than than the order amount?
							// that would indicate that I need to share (split) the tenders among multiple orders.

							System.out.println("return cash_out_order.hasAccountTender() >" + cash_out_order.hasAccountTender());

							if (cash_out_order.hasAccountTender())
							{
								// hmmm.... so, I have a return sales receipt that has an account tender.
								// will QB accept an Invoice for a negative amount???  no.  I need to do a credit memo...

								/*
								QBXMLInvoiceAddRequest invoice_add_req_obj = new QBXMLInvoiceAddRequest();
								invoice_add_req_obj.setOrder(cash_out_order);
								queue.add(company_key, invoice_add_req_obj);
								 */

								QBXMLCreditMemoAddRequest credit_memo_add_req_obj = new QBXMLCreditMemoAddRequest();
								credit_memo_add_req_obj.setIsAddRequest(true);
								credit_memo_add_req_obj.setOrder(cash_out_order);
								credit_memo_add_req_obj.setCompany(company);
								queue.add(company_key, credit_memo_add_req_obj);

								// does this order have any non-account tenders?  if so, create payments for them

								Iterator tender_itr = cash_out_order.getTenders().iterator();
								while (tender_itr.hasNext())
								{
									TenderRet tender = (TenderRet)tender_itr.next();
									if (!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
									{
										// umm, even this tender may be shared.  I can't necessarily report the entire amount
										// the QBXMLReceivePaymentAddRequest object will get the proper amount from the tender<>object map

										QBXMLReceivePaymentAddRequest payment_add_req_obj = new QBXMLReceivePaymentAddRequest();
										payment_add_req_obj.setIsAddRequest(true);
										payment_add_req_obj.setOrder(cash_out_order);
										payment_add_req_obj.setTender(tender);
										payment_add_req_obj.setCompany(company);
										queue.add(company_key, payment_add_req_obj);
									}
								}
							}
							else
							{
								QBXMLCreditMemoAddRequest credit_memo_add_req_obj = new QBXMLCreditMemoAddRequest();
								credit_memo_add_req_obj.setIsAddRequest(true);
								credit_memo_add_req_obj.setOrder(cash_out_order);
								credit_memo_add_req_obj.setCompany(company);
								queue.add(company_key, credit_memo_add_req_obj);
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


					}
					else if (!cash_out_order.isReversed())
					{
						
						if (!cash_out_order.isUpdated())
						{
						
							System.out.println("FOUND CASH OUT ORDER >" + cash_out_order.getLabel());

							try
							{
								// what if the tenders for this order are greater than than the order amount?
								// that would indicate that I need to share (split) the tenders among multiple orders.

								System.out.println("cash_out_order.hasAccountTender() >" + cash_out_order.hasAccountTender());
								System.out.println("cash_out_order.hasNSIPMTender() >" + cash_out_order.hasNSIPMTender());

								if (cash_out_order.hasAccountTender() || cash_out_order.hasNSIPMTender())
								{
									QBXMLInvoiceAddRequest invoice_add_req_obj = new QBXMLInvoiceAddRequest();
									invoice_add_req_obj.setIsAddRequest(true);
									invoice_add_req_obj.setOrder(cash_out_order);
									invoice_add_req_obj.setCompany(company);
									queue.add(company_key, invoice_add_req_obj);

									// does this order have any non-account tenders?  if so, create payments for them

									Iterator tender_itr = cash_out_order.getTenders().iterator();
									while (tender_itr.hasNext()) {
										TenderRet tender = (TenderRet)tender_itr.next();
										if (!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE) &&
												!tender.getType().equals(TenderRet.NSIPM_TENDER_TYPE))
										{
											// umm, even this tender may be shared.  I can't necessarily report the entire amount
											// the QBXMLReceivePaymentAddRequest object will get the proper amount from the tender<>object map

											QBXMLReceivePaymentAddRequest payment_add_req_obj = new QBXMLReceivePaymentAddRequest();
											payment_add_req_obj.setIsAddRequest(true);
											payment_add_req_obj.setOrder(cash_out_order);
											payment_add_req_obj.setTender(tender);
											payment_add_req_obj.setCompany(company);
											queue.add(company_key, payment_add_req_obj);
										}
									}
								}
								else
								{
									QBXMLSalesReceiptAddRequest sales_receipt_add_req_obj = new QBXMLSalesReceiptAddRequest();
									sales_receipt_add_req_obj.setIsAddRequest(true);
									sales_receipt_add_req_obj.setOrder(cash_out_order);
									sales_receipt_add_req_obj.toXMLString();
									sales_receipt_add_req_obj.setCompany(company);
									queue.add(company_key, sales_receipt_add_req_obj);
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

						}
						else
							System.out.println("ATTEMPT TO SYNC ALREADY UPDATE ORDER >" + cash_out_order.getLabel());
					}
					
					
				}

				// find any tenders that are for a previous on account order...
				// get all the tenders in the cash out window.  eliminate the tender (mappings) that have already been used on cash out orders.
				// whatever tenders remain must be entered as received payments...

				System.out.println("GRAB ALL TENDERS IN THE CASHOUT WINDOW");
				Vector payment_on_account_tenders = TenderRet.getTendersUnsubmitted(company, cash_out.getStartDate(), cash_out.getEndDate());
				Iterator payment_on_account_tender_itr = payment_on_account_tenders.iterator();
				while (payment_on_account_tender_itr.hasNext())
				{
					TenderRet payment_on_account_tender = (TenderRet)payment_on_account_tender_itr.next();
					System.out.println("tender found type >" + payment_on_account_tender.getType());
					if (!payment_on_account_tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
					{
						System.out.println("payment_on_account_tender >" + payment_on_account_tender.getValue());

						// is this tender shared by another order???
						
						payment_on_account_tender.maintainOrderAssignment();

						// it's possible that only a portion of this tender is a payment on account
						// get the order mapping for this Tender
						
						Criteria crit = new Criteria();
						crit.add(TenderRetDbOrderMappingPeer.TENDER_RET_DB_ID, payment_on_account_tender.getId());
						System.out.println("mapping crit >" + crit.toString());
						BigDecimal tender_amount_not_applied_to_orders = payment_on_account_tender.getAmountBigDecimal();
						Iterator itr = TenderRetDbOrderMappingPeer.doSelect(crit).iterator();
						while (itr.hasNext())
						{
							TenderRetDbOrderMapping mapping_obj = (TenderRetDbOrderMapping)itr.next();
							
							BigDecimal tender_amount_applied_to_order = mapping_obj.getTenderAmountApplied();
							tender_amount_not_applied_to_orders = tender_amount_not_applied_to_orders.subtract(tender_amount_applied_to_order);
							
							ValeoOrderBean order_for_tender = (ValeoOrderBean)ValeoOrderBean.getOrder(mapping_obj.getOrderId());
							System.out.println("cash_out_orders.contains(order_for_tender) >" + cash_out_orders.contains(order_for_tender));
							System.out.println("order_for_tender.isReversed() >" + order_for_tender.isReversed());
							if (!cash_out_orders.contains(order_for_tender) && !order_for_tender.isReversed())
							{
								QBXMLReceivePaymentAddRequest payment_add_req_obj = new QBXMLReceivePaymentAddRequest();
								payment_add_req_obj.setIsAddRequest(true);
								payment_add_req_obj.setOrder(order_for_tender);
								payment_add_req_obj.setTender(payment_on_account_tender);
								payment_add_req_obj.setCompany(company);
								queue.add(company_key, payment_add_req_obj);
								
								// ummm...  this is adding the tender amount in full for each mapping!?!?!
								// no, I don't think so

								// again, the QBXMLReceivePaymentAddRequest object will get the proper amount from the tender<>object map
							}
						}

						System.out.println("tender_amount_not_applied_to_orders >" + tender_amount_not_applied_to_orders.toString());

						if (tender_amount_not_applied_to_orders.compareTo(BigDecimal.ZERO) == 1)
						{
							System.out.println("found tender amount not mapped to order.  report as received payment");

							QBXMLReceivePaymentAddRequest payment_add_req_obj = new QBXMLReceivePaymentAddRequest();
							payment_add_req_obj.setIsAddRequest(true);
							payment_add_req_obj.setCompany(company);
							payment_add_req_obj.setPaymentAmount(tender_amount_not_applied_to_orders);
							payment_add_req_obj.setTender(payment_on_account_tender);
							payment_add_req_obj.setCompany(company);
							queue.add(company_key, payment_add_req_obj);
						}
						

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

			

			try {
				
				// add purchase orders
				
				Iterator po_itr = PurchaseOrder.getPurchaseOrdersThatNeedToBeSyncedWithQuickBooks(company).iterator();
				while (po_itr.hasNext()) {
					PurchaseOrder purchase_order = (PurchaseOrder)po_itr.next();
					System.out.println("FOUND PURCHASE ORDER(1) >" + purchase_order.getLabel());
					try {
						QBXMLPurchaseOrderAddRequest purchase_order_add_req_obj = new QBXMLPurchaseOrderAddRequest();
						purchase_order_add_req_obj.setIsAddRequest(true);
						purchase_order_add_req_obj.setPurchaseOrder(purchase_order);
						purchase_order_add_req_obj.setCompany(company);
						queue.add(company_key, purchase_order_add_req_obj);
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				
				
				// find any modified purchase orders

				/*
				Date last_update_date = qb_settings.getLastInventoryUpdateDate();
				
				if (last_update_date != null) {
					// find any checkout codes that have been modified since the last update date
					
					Iterator itr = PurchaseOrder.getPurchaseOrdersModifiedAfter(company, last_update_date).iterator();
					while (itr.hasNext()) {
						PurchaseOrder mod_po = (PurchaseOrder)itr.next();
						System.out.println("mod po found >" + mod_po.getLabel());
						QBXMLPurchaseOrderModRequest po_mod_req_obj = new QBXMLPurchaseOrderModRequest();
						po_mod_req_obj.setPurchaseOrder(mod_po);
						po_mod_req_obj.setCompany(company);
						queue.add(qb_settings.getCompanyKeyString(), po_mod_req_obj);
					}
				}
				*/
				
				
			} catch (Exception x) {
				x.printStackTrace();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				x.printStackTrace(pw);
				CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), sw.toString());
			}

			// add item receipts (from received purchase orders)

			try
			{
				HashMap<PurchaseOrder, Vector> purchaseOrderItemReceiptMap = new HashMap<PurchaseOrder, Vector>();
				Vector item_receipt_vec = ItemReceiptRet.getItemReceiptsThatNeedToBeSyncedWithQuickBooks(company, cash_out.getStartDate(), cash_out.getEndDate());
				Iterator item_receipt_itr = item_receipt_vec.iterator();
				while (item_receipt_itr.hasNext())
				{
					ItemReceiptRet item_receipt = (ItemReceiptRet)item_receipt_itr.next();
					System.out.println("FOUND CASH OUT ITEM RECEIPT >" + item_receipt.getLabel());
					try
					{
						QBXMLItemReceiptAddRequest item_receipt_add_req_obj = new QBXMLItemReceiptAddRequest();
						item_receipt_add_req_obj.setIsAddRequest(true);
						item_receipt_add_req_obj.setItemReceipt(item_receipt);
						item_receipt_add_req_obj.setCompany(company);
						// queue.add(company_key, item_receipt_add_req_obj); removed 6/26/18 - Christine doesn't want these in QB
						
						PurchaseOrder purchaseOrderConnectedToReceipt = item_receipt.getPurchaseOrder();
						Vector receivedItemsForPurchaseOrder = null;
						if (purchaseOrderItemReceiptMap.containsKey(purchaseOrderConnectedToReceipt)) {
							receivedItemsForPurchaseOrder = purchaseOrderItemReceiptMap.get(purchaseOrderConnectedToReceipt);
						} else {
							receivedItemsForPurchaseOrder = new Vector();
							purchaseOrderItemReceiptMap.put(purchaseOrderConnectedToReceipt, receivedItemsForPurchaseOrder);
						}
						receivedItemsForPurchaseOrder.addElement(item_receipt);
						
						
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
					
				}
				
				// change this to bill on item receipt only
				
				Iterator po_keys = purchaseOrderItemReceiptMap.keySet().iterator();
				while (po_keys.hasNext()) {
					PurchaseOrder purchaseOrderConnectedToReceipt = (PurchaseOrder)po_keys.next();
					Vector item_receipts_for_po = purchaseOrderItemReceiptMap.get(purchaseOrderConnectedToReceipt);
					
					System.out.println("FOUND PURCHASE ORDER TO BE BILLED >" + purchaseOrderConnectedToReceipt.getLabel());
					QBXMLBillAddRequest bill_add_req_obj = new QBXMLBillAddRequest();
					bill_add_req_obj.setIsAddRequest(true);
					bill_add_req_obj.setPurchaseOrder(purchaseOrderConnectedToReceipt);
					bill_add_req_obj.setItemReceipts(item_receipts_for_po);
					bill_add_req_obj.setCompany(company);
					queue.add(company_key, bill_add_req_obj);
					System.out.println("bill added to queue");
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



			try
			{
				QBXMLReceivePaymentQueryRequest receive_payment_req_obj = new QBXMLReceivePaymentQueryRequest();

				Date last_update = ReceivePaymentRet.getLastUpdateDate(company);
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

				receive_payment_req_obj.setCompany(company);
				queue.add(company_key, receive_payment_req_obj);


				/*
				QBXMLGenericQueryRequest item_request = new QBXMLGenericQueryRequest();
				last_update = InvoiceRet.getLastUpdateDate(company);
				item_request.setTimeModified(last_update);
				item_request.setRequestName("InvoiceQueryRq");
				//item_request.setActiveStatus("All");
				item_request.setMaxReturned(10000);
				//item_request.setIterate(true);
				item_request.setUseModifiedDateRangeFilter(true);
				item_request.setIncludeLineItems(true);
				queue.add(qb_settings.getCompanyKeyString(), item_request);
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
			}
			catch (Exception x)
			{
				x.printStackTrace();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				x.printStackTrace(pw);
				CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), sw.toString());
			}


			
			try
			{
				// 4.  query QuickBooks for any modified customers since the last CashOut

				Date last_update_date = qb_settings.getLastClientUpdateDate();
				System.out.println("last_update_date >" + last_update_date);


				/*
				QBXMLCustomerQueryRequest customer_query_req_obj = new QBXMLCustomerQueryRequest();
				if (last_update_date != null)
					customer_query_req_obj.setFromModifiedDate(last_update_date);
				customer_query_req_obj.setMaxReturned(100);
				customer_query_req_obj.setIterate(true);
				queue.add(company_key, customer_query_req_obj);
				*/



				QBXMLCustomerQueryRequest customer_query_req_obj = new QBXMLCustomerQueryRequest();
				if (last_update_date != null)
					customer_query_req_obj.setFromModifiedDate(last_update_date);
				customer_query_req_obj.setActiveStatus("All");
				customer_query_req_obj.setMaxReturned(100);
				customer_query_req_obj.setIterate(true);
				customer_query_req_obj.setCompany(company);
				queue.add(company_key, customer_query_req_obj);


				/*

				QBXMLGenericQueryRequest item_request = new QBXMLGenericQueryRequest();
				if (last_update_date != null)
					item_request.setTimeModified(last_update_date);
				item_request.setRequestName("ItemQueryRq");
				item_request.setActiveStatus("All");
				item_request.setMaxReturned(1000);
				item_request.setIterate(true);
				item_request.setUseModifiedDateRangeFilter(false);
				queue.add(qb_settings.getCompanyKeyString(), item_request);

				*/


				qb_settings.setClientUpdateRequestInQueue();

			}
			catch (Exception x)
			{
				x.printStackTrace();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				x.printStackTrace(pw);
				CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), sw.toString());
			}
			


			qb_settings.addMessage("Waiting for response from QuickBooks");

		}
		catch (Exception x)
		{
			x.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			x.printStackTrace(pw);
			CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), sw.toString());
		} finally {
			System.out.println("finally in UpdateQuickBooksTask >" + qb_settings);
			if (qb_settings != null) {
				
				QBPOSXMLRequestQueue qbfs_request_queue = QBWebConnectorSvcSoapImpl.queue_hash.get(qb_settings);
				System.out.println("qbfs_request_queue >" + qbfs_request_queue);
				if (qbfs_request_queue != null) {
					System.out.println("qbfs_request_queue.size(qb_settings.getCompanyKeyString()) >" + qbfs_request_queue.size(qb_settings.getCompanyKeyString()));
					boolean has_data_to_exchange = qbfs_request_queue.hasDataToExchange(qb_settings.getCompanyKeyString());
					System.out.println("has_data_to_exchange >" + has_data_to_exchange);
					if (!has_data_to_exchange) {
					
						//cash_out.setIsQuickBooksUpdateInProgress(false);
						cash_out.setAllow_regen_stuff(true);
						System.out.println("not in progress >");
					
					}
				}
			}
				
			
		}
    }

}
