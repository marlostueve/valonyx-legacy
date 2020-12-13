/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.tasks;

import java.util.*;

import com.badiyan.uk.online.webservices.*;

import com.valeo.qb.*;

import com.valeo.qbpos.*;
import com.valeo.qbpos.data.*;

import com.badiyan.uk.online.beans.UKOnlineCompanyBean;

/**
 *
 * @author marlo
 */
public class
QBPOSXMLRequestQueueMonitorTask
	extends TimerTask
{
	// INSTANCE VARIABLES

	private boolean run_once = true;
	
    // CONSTRUCTORS

    public
    QBPOSXMLRequestQueueMonitorTask()
    {
    }

    // INSTANCE METHODS

	@Override
	public void
	run()
	{
		//System.out.println("run() invoked in QBPOSXMLRequestQueueMonitorTask >" + QBWebConnectorSvcSoapImpl.qbpos_request_queue.size(QBWebConnectorSvcSoapImpl.qbpos_company_key));
		// is there an existing pending QBPOSXMLSalesReceiptQueryRequest in the queue?

		/*
		
		if (run_once)
		{
			run_once = false;


			QBPOSXMLSalesReceiptQueryRequest req_obj = new QBPOSXMLSalesReceiptQueryRequest();
			req_obj.setIterate(true);
			QBWebConnectorSvcSoapImpl.request_queue.add(QBWebConnectorSvcSoapImpl.company_key, req_obj);
			QBWebConnectorSvcSoapImpl.request_queue.setHasPendingSalesReceiptQuery(QBWebConnectorSvcSoapImpl.company_key, true);

		}
		 */
		
		

		if (!QBWebConnectorSvcSoapImpl.qbpos_request_queue.hasPendingCustomerQuery(QBWebConnectorSvcSoapImpl.qbpos_company_key))
		{
			// get the last date a request was made

			try
			{
				QBPOSXMLCustomerQueryRequest req_obj = new QBPOSXMLCustomerQueryRequest();

				Date last_update = CustomerRet.getLastUpdateDate((UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance());

				if (last_update == null)
				{
					// no customers in db.  request all clients, I guess...

					req_obj.setIterate(true);
				}
				else
				{
					Calendar some_date = Calendar.getInstance();
					some_date.setTime(last_update);
					some_date.add(Calendar.SECOND, 1);
					last_update = some_date.getTime();
					req_obj.setTimeModified(last_update);
					req_obj.setMatchNumericCriterion(QBPOSXMLRequest.GREATER_THAN_MATCH_NUMERIC_CRITERION);
				}

				System.out.println("CustomerRet.last_update >" + last_update);
				System.out.println("QBWebConnectorSvcSoapImpl.company_key >" + QBWebConnectorSvcSoapImpl.qbpos_company_key);
				System.out.println("QBWebConnectorSvcSoapImpl.qbpos_request_queue.size() >" + QBWebConnectorSvcSoapImpl.qbpos_request_queue.getQueue(QBWebConnectorSvcSoapImpl.qbpos_company_key).size());


				QBWebConnectorSvcSoapImpl.qbpos_request_queue.add(QBWebConnectorSvcSoapImpl.qbpos_company_key, req_obj);
				QBWebConnectorSvcSoapImpl.qbpos_request_queue.setHasPendingCustomerQuery(QBWebConnectorSvcSoapImpl.qbpos_company_key, true);
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
		}

		if (!QBWebConnectorSvcSoapImpl.qbpos_request_queue.hasPendingSalesReceiptQuery(QBWebConnectorSvcSoapImpl.qbpos_company_key))
		{
			// there is no pending sales receipt request.  Generate a request to see if there have been any sales...

			// get the last date a request was made...

			try
			{
				Date last_update = SalesReceiptRet.getLastUpdateDate((UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance());
				if (last_update == null)
				{
					// no requests found in the database.  just get the requests for the last week, I guess...

					Calendar some_date = Calendar.getInstance();
					some_date.add(Calendar.DATE, -7);
					last_update = some_date.getTime();
				}
				else
				{
					Calendar some_date = Calendar.getInstance();
					some_date.setTime(last_update);
					some_date.add(Calendar.SECOND, 1);
					last_update = some_date.getTime();
				}

				System.out.println("SalesReceiptRet.last_update >" + last_update);
				System.out.println("QBWebConnectorSvcSoapImpl.qbpos_company_key >" + QBWebConnectorSvcSoapImpl.qbpos_company_key);
				System.out.println("QBWebConnectorSvcSoapImpl.qbpos_request_queue.size() >" + QBWebConnectorSvcSoapImpl.qbpos_request_queue.getQueue(QBWebConnectorSvcSoapImpl.qbpos_company_key).size());

				QBPOSXMLSalesReceiptQueryRequest req_obj = new QBPOSXMLSalesReceiptQueryRequest();
				req_obj.setMatchNumericCriterion(QBPOSXMLRequest.GREATER_THAN_MATCH_NUMERIC_CRITERION);
				//req_obj.setTimeCreated(last_update);
				req_obj.setTimeModified(last_update);


				QBWebConnectorSvcSoapImpl.qbpos_request_queue.add(QBWebConnectorSvcSoapImpl.qbpos_company_key, req_obj);
				QBWebConnectorSvcSoapImpl.qbpos_request_queue.setHasPendingSalesReceiptQuery(QBWebConnectorSvcSoapImpl.qbpos_company_key, true);
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
		}

		
	}
}
