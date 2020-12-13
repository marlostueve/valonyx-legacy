/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.tasks;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.QuickBooksSettings;
import java.util.*;

import com.badiyan.uk.online.webservices.*;

import com.valeo.qb.*;
import com.valeo.qb.data.*;

import com.badiyan.uk.online.beans.UKOnlineCompanyBean;
import com.valeo.qbpos.QBPOSXMLRequestQueue;
import org.apache.torque.TorqueException;

/**
 *
 * @author marlo
 */
public class
QBXMLRequestQueueDailyMonitorTask
	extends TimerTask
{
	// INSTANCE VARIABLES

	private boolean run_once = true;

    // CONSTRUCTORS

    public
    QBXMLRequestQueueDailyMonitorTask()
    {
    }

    // INSTANCE METHODS

	@Override
	public void
	run()
	{
		//System.out.println("run() invoked in QBXMLRequestQueueMonitorTask >" + QBWebConnectorSvcSoapImpl.qbfs_request_queue.size(QBWebConnectorSvcSoapImpl.qbfs_company_key));

		CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "QBXMLRequestQueueDailyMonitorTask Issue",
								"run() invoked >" + QBWebConnectorSvcSoapImpl.qbfs_request_queue.hasPendingDailyQBFSCustomerQuery(QBWebConnectorSvcSoapImpl.qbfs_company_key));

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
	}

	private void
	generateRequests(QuickBooksSettings _qb_settings)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		System.out.println("generateRequests invoked in QBXMLRequestQueueDailyMonitorTask >" + _qb_settings.getCompany().getLabel());

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

			if (!queue.hasPendingDailyQBFSReceivePaymentQuery(company_key))
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
						// since this is once a day, go back a week and iterate

						Calendar some_date = Calendar.getInstance();
						some_date.setTime(last_update);
						some_date.add(Calendar.DATE, -7);
						last_update = some_date.getTime();
						receive_payment_req_obj.setTimeModified(last_update);
						receive_payment_req_obj.setMatchNumericCriterion(QBXMLRequest.GREATER_THAN_MATCH_NUMERIC_CRITERION);
						receive_payment_req_obj.setIterate(true);
					}

					receive_payment_req_obj.setCompany(_qb_settings.getCompany());
					queue.add(company_key, receive_payment_req_obj);
					queue.setHasPendingDailyQBFSReceivePaymentQuery(company_key, true);
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}

			System.out.println("_qb_settings.isSyncAccounts() >" + _qb_settings.isSyncAccounts());

			if (_qb_settings.isSyncAccounts())
			{
				System.out.println("queue.hasPendingDailyQBFSAccountsQuery(company_key) >" + queue.hasPendingDailyQBFSAccountQuery(company_key));

				if (!queue.hasPendingDailyQBFSAccountQuery(company_key))
				{
					// there is no pending accounts query request.  Generate a request...

					try
					{
						QBXMLAccountQueryRequest account_req_obj = new QBXMLAccountQueryRequest();

						account_req_obj.setCompany(_qb_settings.getCompany());
						queue.add(company_key, account_req_obj);
						queue.setHasPendingDailyQBFSAccountQuery(company_key, true);
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}

			if (_qb_settings.isSyncClients())
			{

				if (!queue.hasPendingDailyQBFSCustomerQuery(QBWebConnectorSvcSoapImpl.qbfs_company_key))
				{
					try
					{
						QBXMLDailyCustomerQueryRequest customer_req_obj = new QBXMLDailyCustomerQueryRequest();
						customer_req_obj.setIterate(true);
						customer_req_obj.setCompany(_qb_settings.getCompany());
						queue.add(QBWebConnectorSvcSoapImpl.qbfs_company_key, customer_req_obj);

						queue.setHasPendingDailyQBFSCustomerQuery(QBWebConnectorSvcSoapImpl.qbfs_company_key, true);
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
