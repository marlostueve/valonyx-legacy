/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbpos;

import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.online.beans.*;

import com.valeo.qb.*;

import java.util.*;

/**
 *
 * @author marlo
 */
public class
QBPOSXMLRequestQueue
{
	// INSTANCE VARIABLES
	
	private HashMap<String,Vector> request_hash = new HashMap<String,Vector>(11);
	private HashMap<String,String> active_tickets = new HashMap<String,String>(11);
	private HashMap<String,Integer> ticket_start_queue_size = new HashMap<String,Integer>(11);

	private HashMap<String,Boolean> has_pending_sales_receipt_query_hash = new HashMap<String,Boolean>(11);
	private HashMap<String,Boolean> has_pending_customer_query_hash = new HashMap<String,Boolean>(11);
	private HashMap<String,Boolean> has_pending_daily_customer_query_hash = new HashMap<String,Boolean>(11);
	private HashMap<String,Boolean> has_pending_daily_sales_receipt_query_hash = new HashMap<String,Boolean>(11);
	private HashMap<String,Boolean> has_pending_customer_qbfs_query_hash = new HashMap<String,Boolean>(11);
	private HashMap<String,Boolean> has_pending_daily_customer_qbfs_query_hash = new HashMap<String,Boolean>(11);

	private HashMap<String,Boolean> has_pending_qbfs_query_hash = new HashMap<String,Boolean>(11);
	private HashMap<String,Boolean> has_pending_qbfs_account_query_hash = new HashMap<String,Boolean>(11);
	private HashMap<String,Boolean> has_pending_qbfs_vendor_query_hash = new HashMap<String,Boolean>(11);
	private HashMap<String,Boolean> has_pending_daily_qbfs_account_query_hash = new HashMap<String,Boolean>(11);
	private HashMap<String,Boolean> has_pending_qbfs_receive_payment_query_hash = new HashMap<String,Boolean>(11);
	private HashMap<String,Boolean> has_pending_daily_qbfs_receive_payment_query_hash = new HashMap<String,Boolean>(11);
	
	// INSTANCE METHODS
			
	public void
	add(String _company_key, QBXMLRequest _request)
	{
		// persist the request
		
		try
		{
			_request.save();
			Vector queue = this.getQueue(_company_key);
			if (!this.isRequestAlreadyInQueue(queue, _request))
				queue.addElement(_request);
			else
				CUBean.sendEmail("marlo@badiyan.com", "valonyx@valeowc.com", "Request already in queue(1)", _request.getRequestId());
		}
		catch (Exception x)
		{
			x.printStackTrace();
			CUBean.sendEmail("marlo@badiyan.com", "valonyx@valeowc.com", "Trouble adding QBXML request to queue", x.getMessage());
		}
	}

	public void
	add(String _company_key, QBPOSXMLRequest _request)
	{
		Vector queue = this.getQueue(_company_key);
		if (!this.isRequestAlreadyInQueue(queue, _request))
			queue.addElement(_request);
		else
			CUBean.sendEmail("marlo@badiyan.com", "valonyx@valeowc.com", "Request already in queue(2)", _request.getRequestId());
	}
	
	private boolean
	isRequestAlreadyInQueue(Vector _queue, QBPOSXMLRequest _request) {
		
		System.out.println("isRequestAlreadyInQueue() invoked");
		Vector vec = new Vector();
		synchronized (_queue) {
			Iterator itr = _queue.iterator();
			while (itr.hasNext()) {
				QBPOSXMLRequest existingRequest = (QBPOSXMLRequest)itr.next();
				if (existingRequest.isAddRequest() && existingRequest.hasRefNumber()) {
					String key = existingRequest.getRequestType() + "-" + existingRequest.getRefNumber();
					System.out.println("found key >" + key);
					if (vec.contains(key))
						return true;
					System.out.println("adding key >" + key);
					vec.addElement(key);
				}
			}
		}
		
		return false;
	}
	
	public void
	closeTicket(String _ticket)
	{
		active_tickets.remove(_ticket);
		ticket_start_queue_size.remove(_ticket);
	}

	public boolean
	containsMatchingCustomer(String _company_key, UKOnlinePersonBean _person)
	{
		Iterator queue_itr = this.getQueue(_company_key).iterator();
		while (queue_itr.hasNext())
		{
			QBPOSXMLRequest request = (QBPOSXMLRequest)queue_itr.next();
			if (request instanceof QBPOSXMLCustomerAddRequest)
			{
				QBPOSXMLCustomerAddRequest customer_add_request = (QBPOSXMLCustomerAddRequest)request;
				String xml_str = customer_add_request.toXMLString();
				if ((xml_str.indexOf(_person.getFirstNameString()) > -1) && (xml_str.indexOf(_person.getLastNameString()) > -1))
					return true;
			}
		}

		return false;
	}

	public boolean
	containsTicket(String _ticket)
	{
		return active_tickets.containsKey(_ticket);
	}
	
	public int
	getCompletionPercentage(String _ticket)
	{
		String company_key = (String)active_tickets.get(_ticket);
		if (company_key == null)
			return 100;
		Vector queue = (Vector)request_hash.get(company_key);
		if (queue == null)
			return 100;
		if (queue.size() == 0)
			return 100;
		
		Integer start_queue_size = ticket_start_queue_size.get(_ticket);
		
		return 100 * (start_queue_size.intValue() - queue.size()) / start_queue_size.intValue();
	}
	
	public QBPOSXMLRequest
	getNextRequestForTicket(String _ticket)
	{
		String company_key = (String)active_tickets.get(_ticket);
		if (company_key == null)
			return null;
		Vector queue = (Vector)request_hash.get(company_key);
		if (queue == null)
			return null;
		
		if (!ticket_start_queue_size.containsKey(_ticket))
			ticket_start_queue_size.put(_ticket, new Integer(queue.size()));

		flushExhaustedIterators(queue);

		QBPOSXMLRequest next_request = (QBPOSXMLRequest)queue.get(0);

		if (!(next_request.isInitialIterator() || next_request.isContinuation()))
		{
			queue.remove(0);
			if (next_request instanceof QBXMLDailyAccountQueryRequest)
				setHasPendingDailyQBFSAccountQuery(company_key, false);
			else if (next_request instanceof QBXMLDailyCustomerQueryRequest)
				setHasPendingDailyQBFSCustomerQuery(company_key, false);
			else if (next_request instanceof QBPOSXMLDailyCustomerQueryRequest)
				setHasPendingDailyCustomerQuery(company_key, false);
			else if (next_request instanceof QBPOSXMLDailySalesReceiptQueryRequest)
				setHasPendingDailySalesReceiptQuery(company_key, false);
			else if (next_request instanceof QBPOSXMLSalesReceiptQueryRequest)
				setHasPendingSalesReceiptQuery(company_key, false);
			else if (next_request instanceof QBPOSXMLCustomerQueryRequest)
				setHasPendingCustomerQuery(company_key, false);
			else if (next_request instanceof QBXMLAccountQueryRequest)
				setHasPendingQBFSAccountQuery(company_key, false);
			else if (next_request instanceof QBXMLReceivePaymentQueryRequest)
				setHasPendingQBFSReceivePaymentQuery(company_key, false);
			else if (next_request instanceof QBXMLVendorQueryRequest)
				setHasPendingQBFSVendorQuery(company_key, false);
			else if (next_request instanceof QBXMLRequest)
				setHasPendingQBFSQuery(company_key, false);
		}

		return next_request;
	}

	private void
	flushExhaustedIterators(Vector _queue)
	{
		Vector requestsToRemove = new Vector();
		Iterator itr = _queue.iterator();
		while (itr.hasNext())
		{
			QBPOSXMLRequest request = (QBPOSXMLRequest)itr.next();
			if (request.isExhaustedIterator())
				requestsToRemove.addElement(request);
		}
		itr = requestsToRemove.iterator();
		while (itr.hasNext())
		{
			QBPOSXMLRequest request = (QBPOSXMLRequest)itr.next();
			boolean did_contain = _queue.remove(request);

		}
	}
	
	public Vector
	getQueue(String _company_key)
	{
		Vector queue = (Vector)request_hash.get(_company_key);
		if (queue == null)
		{
			queue = new Vector();
			request_hash.put(_company_key, queue);
		}
		
		return queue;
	}
	
	public boolean
	hasPendingCustomerQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_customer_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}

	public boolean
	hasPendingDailyCustomerQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_daily_customer_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}

	public boolean
	hasPendingQBFSCustomerQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_customer_qbfs_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}

	public boolean
	hasPendingDailyQBFSCustomerQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_daily_customer_qbfs_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}

	public boolean
	hasPendingDailySalesReceiptQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_daily_sales_receipt_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}
	
	public boolean
	hasPendingSalesReceiptQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_sales_receipt_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}

	public boolean
	hasPendingQBFSReceivePaymentQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_qbfs_receive_payment_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}

	public boolean
	hasPendingDailyQBFSReceivePaymentQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_daily_qbfs_receive_payment_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}

	public boolean
	hasPendingQBFSAccountQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_qbfs_account_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}

	public boolean
	hasPendingDailyQBFSAccountQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_daily_qbfs_account_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}

	public boolean
	hasPendingQBFSVendorQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_qbfs_vendor_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}

	public boolean
	hasPendingQBFSQuery(String _company_key)
	{
		Boolean has_pending = (Boolean)has_pending_qbfs_query_hash.get(_company_key);
		if (has_pending == null)
			return false;
		return has_pending.booleanValue();
	}
	
	public boolean
	hasDataToExchange(String _company_key)
	{
		Vector queue = (Vector)request_hash.get(_company_key);
		if (queue == null)
			return false;
		return (queue.size() > 0);
	}
	
	public void
	openTicket(String _company_key, String _ticket)
	{
		active_tickets.put(_ticket, _company_key);
	}
	
	public void
	setHasPendingCustomerQuery(String _company_key, boolean _value)
	{
		has_pending_customer_query_hash.put(_company_key, new Boolean(_value));
	}

	public void
	setHasPendingQBFSCustomerQuery(String _company_key, boolean _value)
	{
		has_pending_customer_qbfs_query_hash.put(_company_key, new Boolean(_value));
	}

	public void
	setHasPendingQBFSVendorQuery(String _company_key, boolean _value)
	{
		has_pending_qbfs_vendor_query_hash.put(_company_key, new Boolean(_value));
	}

	public void
	setHasPendingDailyQBFSCustomerQuery(String _company_key, boolean _value)
	{
		has_pending_daily_customer_qbfs_query_hash.put(_company_key, new Boolean(_value));
	}

	public void
	setHasPendingDailyCustomerQuery(String _company_key, boolean _value)
	{
		has_pending_daily_customer_query_hash.put(_company_key, new Boolean(_value));
	}

	public void
	setHasPendingDailySalesReceiptQuery(String _company_key, boolean _value)
	{
		has_pending_daily_sales_receipt_query_hash.put(_company_key, new Boolean(_value));
	}
	
	public void
	setHasPendingSalesReceiptQuery(String _company_key, boolean _value)
	{
		has_pending_sales_receipt_query_hash.put(_company_key, new Boolean(_value));
	}

	public void
	setHasPendingQBFSReceivePaymentQuery(String _company_key, boolean _value)
	{
		has_pending_qbfs_receive_payment_query_hash.put(_company_key, new Boolean(_value));
	}

	public void
	setHasPendingDailyQBFSReceivePaymentQuery(String _company_key, boolean _value)
	{
		has_pending_daily_qbfs_receive_payment_query_hash.put(_company_key, new Boolean(_value));
	}

	public void
	setHasPendingQBFSAccountQuery(String _company_key, boolean _value)
	{
		has_pending_qbfs_account_query_hash.put(_company_key, new Boolean(_value));
	}

	public void
	setHasPendingDailyQBFSAccountQuery(String _company_key, boolean _value)
	{
		has_pending_daily_qbfs_account_query_hash.put(_company_key, new Boolean(_value));
	}

	public void
	setHasPendingQBFSQuery(String _company_key, boolean _value)
	{
		has_pending_qbfs_query_hash.put(_company_key, new Boolean(_value));
	}

	public int
	size(String _company_key)
	{
		Vector queue = (Vector)request_hash.get(_company_key);
		if (queue == null)
			return 0;
		return queue.size();
	}
}