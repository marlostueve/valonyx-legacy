/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.tasks;

import com.badiyan.uk.beans.CUBean;
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
QBPOSXMLRequestQueueDailyMonitorTask
	extends TimerTask
{
	// INSTANCE VARIABLES

	private boolean run_once = true;

    // CONSTRUCTORS

    public
    QBPOSXMLRequestQueueDailyMonitorTask()
    {
    }

    // INSTANCE METHODS

	@Override
	public void
	run()
	{
		//System.out.println("run() invoked in QBPOSXMLRequestQueueDailyMonitorTask >" + QBWebConnectorSvcSoapImpl.qbpos_request_queue.size(QBWebConnectorSvcSoapImpl.qbpos_company_key));
		// is there an existing pending QBPOSXMLSalesReceiptQueryRequest in the queue?

		CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "QBPOSXMLRequestQueueDailyMonitorTask Issue",
								"run() invoked >" + QBWebConnectorSvcSoapImpl.qbpos_request_queue.hasPendingDailySalesReceiptQuery(QBWebConnectorSvcSoapImpl.qbpos_company_key));

		// why was this commented out?!?!?!

		if (!QBWebConnectorSvcSoapImpl.qbpos_request_queue.hasPendingDailySalesReceiptQuery(QBWebConnectorSvcSoapImpl.qbpos_company_key))
		{
			try
			{
				QBPOSXMLDailySalesReceiptQueryRequest req_obj = new QBPOSXMLDailySalesReceiptQueryRequest();
				req_obj.setIterate(true);

				QBWebConnectorSvcSoapImpl.qbpos_request_queue.add(QBWebConnectorSvcSoapImpl.qbpos_company_key, req_obj);
				QBWebConnectorSvcSoapImpl.qbpos_request_queue.setHasPendingDailySalesReceiptQuery(QBWebConnectorSvcSoapImpl.qbpos_company_key, true);
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
		}

		if (!QBWebConnectorSvcSoapImpl.qbpos_request_queue.hasPendingDailyCustomerQuery(QBWebConnectorSvcSoapImpl.qbpos_company_key))
		{
			try
			{
				QBPOSXMLDailyCustomerQueryRequest req_obj = new QBPOSXMLDailyCustomerQueryRequest();
				req_obj.setIterate(true);

				QBWebConnectorSvcSoapImpl.qbpos_request_queue.add(QBWebConnectorSvcSoapImpl.qbpos_company_key, req_obj);
				QBWebConnectorSvcSoapImpl.qbpos_request_queue.setHasPendingDailyCustomerQuery(QBWebConnectorSvcSoapImpl.qbpos_company_key, true);
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
		}


	}
}
