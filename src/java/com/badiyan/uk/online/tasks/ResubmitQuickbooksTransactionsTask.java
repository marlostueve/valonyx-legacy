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
import com.valeo.qbpos.*;
import com.valeo.qbpos.data.TenderRet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;


import javax.servlet.http.HttpSession;
import org.apache.torque.util.Criteria;



public class
ResubmitQuickbooksTransactionsTask
    extends TimerTask
{
    // INSTANCE VARIABLES

	private UKOnlineCompanyBean company;

	private HttpSession session;
	private StringBuffer status;

    // CONSTRUCTORS

    public
    ResubmitQuickbooksTransactionsTask(UKOnlineCompanyBean _company)
    {
		company = _company;
		status = (StringBuffer)session.getAttribute("update_quickbooks_status");
    }

    public
    ResubmitQuickbooksTransactionsTask(HttpSession _session, UKOnlineCompanyBean _company)
    {
		session = _session;
		company = _company;
		status = (StringBuffer)session.getAttribute("update_quickbooks_status");
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in ResubmitQuickbooksTransactionsTask ");

		try
		{
			
			QuickBooksSettings qb_settings = company.getQuickBooksSettings();
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



			// add sales receipts and invoices

			try
			{
				// Find all of the Orders in the cash out window.  it would be nice to have a mechanism to only grab orders that haven't already been settled with QB
				
				

				Calendar start_date = Calendar.getInstance();
				start_date.set(2011, Calendar.APRIL, 14);
				start_date.set(Calendar.HOUR_OF_DAY, 0);
				start_date.set(Calendar.MINUTE, 0);
				start_date.set(Calendar.SECOND, 0);

				Calendar end_date = Calendar.getInstance();
				start_date.set(2011, Calendar.APRIL, 14);
				end_date.set(Calendar.HOUR_OF_DAY, 23);
				end_date.set(Calendar.MINUTE, 59);
				end_date.set(Calendar.SECOND, 59);
				

				Calendar launch_date = Calendar.getInstance();
				launch_date.set(2011, Calendar.FEBRUARY, 1);
				//Vector orders_for_resubmission = ValeoOrderBean.getOrdersForResubmission(company, launch_date.getTime(), new Date());
				Vector orders_for_resubmission = ValeoOrderBean.getOrdersForResubmission(company, start_date.getTime(), end_date.getTime());
				System.out.println("NUM orders_for_resubmission >" + orders_for_resubmission.size());
				//Iterator orders_for_resubmission_itr = orders_for_resubmission.iterator();
				//Vector cash_out_orders = cash_out.getOrders();

				Iterator cash_out_orders_itr = orders_for_resubmission.iterator();
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
						System.out.println("FOUND CASH OUT ORDER >" + cash_out_order.getLabel());

						try
						{
							// what if the tenders for this order are greater than than the order amount?
							// that would indicate that I need to share (split) the tenders among multiple orders.

							System.out.println("cash_out_order.hasAccountTender() >" + cash_out_order.hasAccountTender());

							if (cash_out_order.hasAccountTender())
							{
								QBXMLInvoiceAddRequest invoice_add_req_obj = new QBXMLInvoiceAddRequest();
								invoice_add_req_obj.setOrder(cash_out_order);
								invoice_add_req_obj.setCompany(company);
								queue.add(company_key, invoice_add_req_obj);

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
				}

				// find any tenders that are for a previous on account order...
				// get all the tenders in the cash out window.  eliminate the tender (mappings) that have already been used on cash out orders.
				// whatever tenders remain must be entered as received payments...

				System.out.println("GRAB ALL TENDERS IN THE CASHOUT WINDOW");
				Vector payment_on_account_tenders = TenderRet.getTenders(company, start_date.getTime(), end_date.getTime());
				Iterator payment_on_account_tender_itr = payment_on_account_tenders.iterator();
				while (payment_on_account_tender_itr.hasNext())
				{
					TenderRet payment_on_account_tender = (TenderRet)payment_on_account_tender_itr.next();
					System.out.println("tender found type >" + payment_on_account_tender.getType());
					if (!payment_on_account_tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
					{
						System.out.println("payment_on_account_tender >" + payment_on_account_tender.getValue());

						// is this tender shared by another order???

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
							System.out.println("cash_out_orders.contains(order_for_tender) >" + orders_for_resubmission.contains(order_for_tender));
							System.out.println("order_for_tender.isReversed() >" + order_for_tender.isReversed());
							if (!orders_for_resubmission.contains(order_for_tender) && !order_for_tender.isReversed())
							{
								QBXMLReceivePaymentAddRequest payment_add_req_obj = new QBXMLReceivePaymentAddRequest();
								payment_add_req_obj.setOrder(order_for_tender);
								payment_add_req_obj.setTender(payment_on_account_tender);
								payment_add_req_obj.setCompany(company);
								queue.add(company_key, payment_add_req_obj);
								
								// ummm...  this is adding the tender amount in full for each mapping!?!?!

								// again, the QBXMLReceivePaymentAddRequest object will get the proper amount from the tender<>object map
							}
						}

						System.out.println("tender_amount_not_applied_to_orders >" + tender_amount_not_applied_to_orders.toString());

						if (tender_amount_not_applied_to_orders.compareTo(BigDecimal.ZERO) == 1)
						{
							System.out.println("found tender amount not mapped to order.  report as received payment");

							QBXMLReceivePaymentAddRequest payment_add_req_obj = new QBXMLReceivePaymentAddRequest();
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
			}




		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }

}
