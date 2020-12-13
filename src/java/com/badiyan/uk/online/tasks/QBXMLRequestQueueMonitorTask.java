/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.tasks;

import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.QuickBooksSettings;
import java.util.*;

import com.badiyan.uk.online.webservices.*;

import com.valeo.qb.*;
import com.valeo.qb.data.*;

import com.badiyan.uk.online.beans.UKOnlineCompanyBean;
import com.valeo.qbpos.QBPOSXMLRequestQueue;
import com.valeo.qbpos.data.CustomerRet;
import org.apache.torque.TorqueException;

/**
 *
 * @author marlo
 */
public class
QBXMLRequestQueueMonitorTask
	extends TimerTask
{
	// INSTANCE VARIABLES

	private boolean run_once = true;

    // CONSTRUCTORS

    public
    QBXMLRequestQueueMonitorTask()
    {
    }

    // INSTANCE METHODS

	@Override
	public void
	run()
	{
		//System.out.println("run() invoked in QBXMLRequestQueueMonitorTask >" + QBWebConnectorSvcSoapImpl.qbfs_request_queue.size(QBWebConnectorSvcSoapImpl.qbfs_company_key));

		try
		{
			//this.generateRequests(QBWebConnectorSvcSoapImpl.qbfs_company_key);

			Iterator itr = QuickBooksSettings.getSettings().iterator();
			while (itr.hasNext())
			{
				QuickBooksSettings qb_settings = (QuickBooksSettings)itr.next();
				this.generateRequests(qb_settings);
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		//QuickBooksSettings.get

	}

	private void
	generateRequests(QuickBooksSettings _qb_settings)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		System.out.println("generateRequests invoked in QBXMLRequestQueueMonitorTask >" + _qb_settings.getCompany().getLabel());

		String company_key = _qb_settings.getCompanyKeyString();
		System.out.println("company_key >" + company_key);
		if (company_key.length() > 0)
		{

			// obtain the proper queue

			QBPOSXMLRequestQueue queue;
			if (_qb_settings.getCompany().getId() == 1)
				queue = QBWebConnectorSvcSoapImpl.qbfs_request_queue;
			else
				queue = QBWebConnectorSvcSoapImpl.getQueue(_qb_settings);

			

			if (!queue.hasPendingQBFSReceivePaymentQuery(company_key))
			{
				// there is no pending receive payment request.  Generate a request...

				// get the last date a request was made...

				try
				{
					QBXMLReceivePaymentQueryRequest receive_payment_req_obj = new QBXMLReceivePaymentQueryRequest();

					Date last_update = ReceivePaymentRet.getLastUpdateDate(_qb_settings.getCompany());
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
					}

					receive_payment_req_obj.setCompany(_qb_settings.getCompany());
					queue.add(company_key, receive_payment_req_obj);



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


					queue.setHasPendingQBFSReceivePaymentQuery(company_key, true);
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}

			// removed from here to do daily instead...

			System.out.println("_qb_settings.isSyncAccounts() >" + _qb_settings.isSyncAccounts());

			if (_qb_settings.isSyncAccounts())
			{
				System.out.println("queue.hasPendingQBFSAccountsQuery(company_key) >" + queue.hasPendingQBFSAccountQuery(company_key));

				if (!queue.hasPendingQBFSAccountQuery(company_key))
				{
					// there is no pending accounts query request.  Generate a request...

					// get the last date a request was made...

					try
					{
						QBXMLAccountQueryRequest account_req_obj = new QBXMLAccountQueryRequest();

						Date last_update = AccountRet.getLastUpdateDate(_qb_settings.getCompany());
						if (last_update != null)
						{
							Calendar some_date = Calendar.getInstance();
							some_date.setTime(last_update);
							some_date.add(Calendar.SECOND, 1);
							last_update = some_date.getTime();
							account_req_obj.setFromModifiedDate(last_update);
						}

						account_req_obj.setCompany(_qb_settings.getCompany());
						queue.add(company_key, account_req_obj);
						queue.setHasPendingQBFSAccountQuery(company_key, true);
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}

			if (_qb_settings.isSyncClients())
			{
				if (!queue.hasPendingQBFSCustomerQuery(company_key))
				{
					try
					{
						QBXMLCustomerQueryRequest customer_req_obj = new QBXMLCustomerQueryRequest();
						Date last_update = CustomerRet.getLastUpdateDate(_qb_settings.getCompany());

						// modify to get clients incrementally...

						if (last_update == null)
						{
							// get all clients

							customer_req_obj.setIterate(true);
						}
						else
						{
							Calendar some_date = Calendar.getInstance();
							some_date.setTime(last_update);
							some_date.add(Calendar.SECOND, 1);
							last_update = some_date.getTime();
							customer_req_obj.setFromModifiedDate(last_update);
						}

						customer_req_obj.setCompany(_qb_settings.getCompany());
						queue.add(company_key, customer_req_obj);
						queue.setHasPendingQBFSCustomerQuery(company_key, true);
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}

			if (_qb_settings.isSyncVendors())
			{
				if (!queue.hasPendingQBFSVendorQuery(company_key))
				{
					try
					{
						QBXMLVendorQueryRequest vendor_req_obj = new QBXMLVendorQueryRequest();
						Date last_update = VendorRet.getLastUpdateDate(_qb_settings.getCompany());

						if (last_update != null)
						{
							Calendar some_date = Calendar.getInstance();
							some_date.setTime(last_update);
							some_date.add(Calendar.SECOND, 1);
							last_update = some_date.getTime();
							vendor_req_obj.setFromModifiedDate(last_update);
						}

						vendor_req_obj.setCompany(_qb_settings.getCompany());
						queue.add(company_key, vendor_req_obj);
						queue.setHasPendingQBFSVendorQuery(company_key, true);
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}

		}
	}
}
