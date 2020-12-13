/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.webservices;

import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.online.beans.*;

import com.intuit.developer.QBWebConnectorSvcSoap;
import com.valeo.qb.QBXMLResponseParser;
import javax.jws.WebService;

import java.util.*;

import com.valeo.qbpos.*;
import com.valeo.qbpos.data.QueryRs;


/**
 *
 * @author marlo
 */
@WebService(serviceName = "QBWebConnectorSvc", portName = "QBWebConnectorSvcSoap", endpointInterface = "com.intuit.developer.QBWebConnectorSvcSoap", targetNamespace = "http://developer.intuit.com/", wsdlLocation = "WEB-INF/wsdl/QBWebConnectorSvcSoapImpl/developer.intuit.com/uploadedFiles/Support/QBWebConnectorSvc.wsdl")
public class QBWebConnectorSvcSoapImpl implements QBWebConnectorSvcSoap {

	// CLASS VARIABLES

	public static final String qbpos_company_key = "Computer Name=frontdesk-2;Company Data=valeo h&w pllc;Version=7";
	public static final String qbfs_company_key = "C:\\Documents and Settings\\All Users\\Documents\\Intuit\\QuickBooks\\Company Files\\Valeo Health & Wellness Center PLLC.QBW";

	public static final QBPOSXMLRequestQueue qbpos_request_queue = new QBPOSXMLRequestQueue();
	public static final QBPOSXMLRequestQueue qbfs_request_queue = new QBPOSXMLRequestQueue();

	public static HashMap<QuickBooksSettings,QBPOSXMLRequestQueue> queue_hash = new HashMap<QuickBooksSettings,QBPOSXMLRequestQueue>(3);

	public static HashMap<String,QBPOSXMLRequest> pending_iterator_hash = new HashMap<String,QBPOSXMLRequest>(3);
	public static HashMap<String,QBPOSXMLRequest> iterator_hash = new HashMap<String,QBPOSXMLRequest>(3);

	private static Vector unprocessed_sales_receipts;
	private static Vector unprocessed_customers;

	public static Vector authentication_requests = new Vector();
	public static HashMap<String, QBPOSXMLRequest> request_id_request_map = new HashMap<String, QBPOSXMLRequest>();
	public static HashMap<QBPOSXMLRequest, String> request_response_map = new HashMap<QBPOSXMLRequest, String>();

	// CLASS METHODS

	public static QBPOSXMLRequestQueue
	getQueue(QuickBooksSettings _settings)
	{
		QBPOSXMLRequestQueue queue = (QBPOSXMLRequestQueue)QBWebConnectorSvcSoapImpl.queue_hash.get(_settings);
		if (queue == null)
		{
			queue = new QBPOSXMLRequestQueue();
			QBWebConnectorSvcSoapImpl.queue_hash.put(_settings, queue);
		}
		return queue;
	}

	// INSTANCE VARIABLES

	// INSTANCE CLASSES

	class AuthenticationRequest
	{
		Date request_date;
		String username;
		String password;
	}

	// INSTANCE METHODS

	public com.intuit.developer.ArrayOfString authenticate(java.lang.String strUserName, java.lang.String strPassword) {

		System.out.println("QBWebConnectorSvcSoapImpl.authenticate() invoked - ");
		System.out.println("strUserName >" + strUserName);
		System.out.println("strPassword >" + strPassword);
		com.intuit.developer.ArrayOfString return_array = new com.intuit.developer.ArrayOfString();

		UUID generated_uuid = UUID.randomUUID();
		System.out.println("generated_uuid >" + generated_uuid);

		AuthenticationRequest authentication_request_obj = new AuthenticationRequest();
		authentication_request_obj.request_date = new Date();
		authentication_request_obj.username = strUserName;
		authentication_request_obj.password = strPassword;
		authentication_requests.addElement(authentication_request_obj);

		return_array.getString().add(generated_uuid.toString());
		
		if (strUserName.equals("marlo") && strPassword.equals("spork"))
		{
			synchronized (QBWebConnectorSvcSoapImpl.qbpos_request_queue)
			{
				System.out.println("QBWebConnectorSvcSoapImpl.qbpos_request_queue.hasDataToExchange(qbpos_company_key) >" + QBWebConnectorSvcSoapImpl.qbpos_request_queue.hasDataToExchange(qbpos_company_key));

				if (QBWebConnectorSvcSoapImpl.qbpos_request_queue.hasDataToExchange(qbpos_company_key))
				{
					System.out.println("adding key (1) >" + qbpos_company_key);
					return_array.getString().add(qbpos_company_key);
					QBWebConnectorSvcSoapImpl.qbpos_request_queue.openTicket(qbpos_company_key, generated_uuid.toString());
				}
				else
					return_array.getString().add("none");
			}
		}
		/* // commented out on 3/18/10 - Valeo should be setup to use the QuickBooksSettings mechanism
		else if (strUserName.equals("qbfs") && strPassword.equals("spork"))
		{
			synchronized (QBWebConnectorSvcSoapImpl.qbfs_request_queue)
			{
				System.out.println("QBWebConnectorSvcSoapImpl.qbfs_request_queue.hasDataToExchange(qbfs_company_key) >" + QBWebConnectorSvcSoapImpl.qbfs_request_queue.hasDataToExchange(qbfs_company_key));

				if (QBWebConnectorSvcSoapImpl.qbfs_request_queue.hasDataToExchange(qbfs_company_key))
				{
					System.out.println("adding key >" + qbfs_company_key);
					return_array.getString().add(qbfs_company_key);
					QBWebConnectorSvcSoapImpl.qbfs_request_queue.openTicket(qbfs_company_key, generated_uuid.toString());
				}
				else
					return_array.getString().add("none");
			}
		}
		 */
		else
		{
			// see if the passed username & password are setup for a company

			try
			{
				QuickBooksSettings settings = QuickBooksSettings.getSettings(strUserName, strPassword);
				System.out.println("settings >" + settings);
				if (!settings.isQuickBooksFSLogInAttempted())
				{
					settings.setQuickBooksFSLogInAttempted(true);
					settings.save();
				}

				settings.addMessage("Authentication request received from QuickBooks");

				// maintain a separate request queue for each practice

				QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.getQueue(settings);
				System.out.println("queue >" + queue);
				synchronized (queue)
				{
					System.out.println("queue.hasDataToExchange(" + settings.getCompanyKeyString() + ") >" + queue.hasDataToExchange(settings.getCompanyKeyString()));
					if (queue.hasDataToExchange(settings.getCompanyKeyString()))
					{
						System.out.println("adding key >" + settings.getCompanyKeyString());
						return_array.getString().add(settings.getCompanyKeyString());
						settings.addMessage("Opening ticket " + generated_uuid.toString());
						queue.openTicket(settings.getCompanyKeyString(), generated_uuid.toString());
					}
					else
					{
						System.out.println("queue >" + queue);

						settings.addMessage("No data found to exchange for " + settings.getCompany().getLabel());
						return_array.getString().add("none");

						CashOut active_cash_out = settings.getActiveCashOut();
						if (active_cash_out != null)
						{
							active_cash_out.setIsQuickBooksUpdateInProgress(false);
							active_cash_out.save();
						}
					}
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
				return_array.getString().add("nvu");
			}
		}
		//else
		//	return_array.getString().add("nvu");

		return return_array;
	}

	public java.lang.String sendRequestXML(java.lang.String ticket, java.lang.String strHCPResponse, java.lang.String strCompanyFileName, java.lang.String qbXMLCountry, int qbXMLMajorVers, int qbXMLMinorVers) {

		System.out.println("sendRequestXML invoked");
		System.out.println("ticket >" + ticket);
		//System.out.println("strHCPResponse >" + strHCPResponse);
		System.out.println("strCompanyFileName >" + strCompanyFileName);
		System.out.println("qbXMLCountry >" + qbXMLCountry);
		System.out.println("qbXMLMajorVers >" + qbXMLMajorVers);
		System.out.println("qbXMLMinorVers >" + qbXMLMinorVers);
		
		QBPOSXMLRequest request = null;

		// determine the proper queue for this ticket
		
		System.out.println("QBWebConnectorSvcSoapImpl.qbpos_request_queue.containsTicket(ticket) >" + QBWebConnectorSvcSoapImpl.qbpos_request_queue.containsTicket(ticket));

		if (QBWebConnectorSvcSoapImpl.qbpos_request_queue.containsTicket(ticket))
		{
			synchronized (QBWebConnectorSvcSoapImpl.qbpos_request_queue)
			{
				request = QBWebConnectorSvcSoapImpl.qbpos_request_queue.getNextRequestForTicket(ticket);
			}
		}
		/* // commented out on 3/18/10 - Valeo should be setup to use the QuickBooksSettings mechanism
		else if (QBWebConnectorSvcSoapImpl.qbfs_request_queue.containsTicket(ticket))
		{
			synchronized (QBWebConnectorSvcSoapImpl.qbfs_request_queue)
			{
				request = QBWebConnectorSvcSoapImpl.qbfs_request_queue.getNextRequestForTicket(ticket);
			}
		}
		 */
		else
		{
			// search through the queues to find this ticket.  it might be better to setup a ticket -> queue mapping for this

			System.out.println("QBWebConnectorSvcSoapImpl.queue_hash SIZER >" + QBWebConnectorSvcSoapImpl.queue_hash.size());
			
			Iterator keys = QBWebConnectorSvcSoapImpl.queue_hash.keySet().iterator();
			while (keys.hasNext() && (request == null))
			{
				QuickBooksSettings key = (QuickBooksSettings)keys.next();
				QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.queue_hash.get(key);

				synchronized (queue)
				{
					request = queue.getNextRequestForTicket(ticket);
				}

				if (key != null && request != null)
					key.addMessage("Processing Request " + request.getLabel());
			}

		}
		
		//System.out.println("xxx request >" + request);

		if (request == null)
			return "";

		System.out.println("putting request_id_request_map >" + request.getRequestId());
		request_id_request_map.put(request.getRequestId(), request);

		System.out.println("request.isInitialIterator() >" + request.isInitialIterator());
		System.out.println("request.isContinuation() >" + request.isContinuation());
		if (request.isInitialIterator())
		{
			System.out.println("putting request.getRequestId() >" + request.getRequestId());
			QBWebConnectorSvcSoapImpl.pending_iterator_hash.put(request.getRequestId(), request);
		}

		return request.toXMLString();
	}

	public int receiveResponseXML(java.lang.String ticket, java.lang.String response, java.lang.String hresult, java.lang.String message) {

		System.out.println("receiveResponseXML invoked");
		System.out.println("ticket >" + ticket);
		System.out.println("response >" + response);
		System.out.println("hresult >" + hresult);
		System.out.println("message >" + message);
		
		if (!message.equals(""))
			CUBean.sendEmail("marlo@badiyan.com", "valonyx@valeowc.com", message, ticket);

		/*
		QBPOSXMLRequest request = (QBPOSXMLRequest)request_id_request_map.get(ticket);
		if (request != null)
			request_response_map.put(request, response);
		 * 
		 */

		if (QBWebConnectorSvcSoapImpl.qbpos_request_queue.containsTicket(ticket))
		{
			// parsey parse

			try
			{
				QBPOSXMLResponseParser request_parser = new QBPOSXMLResponseParser((UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance(), unprocessed_sales_receipts, unprocessed_customers);
				request_parser.parse(response);
				unprocessed_sales_receipts = request_parser.getUnprocessedSalesReceipts();
				unprocessed_customers = request_parser.getUnprocessedCustomers();
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}

			return QBWebConnectorSvcSoapImpl.qbpos_request_queue.getCompletionPercentage(ticket);
		}
		/* // commented out on 3/18/10 - Valeo should be setup to use the QuickBooksSettings mechanism
		else if (QBWebConnectorSvcSoapImpl.qbfs_request_queue.containsTicket(ticket))
		{
			// parsey parse

			try
			{
				QBXMLResponseParser request_parser = new QBXMLResponseParser((UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance());
				request_parser.parse(response);
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}

			return QBWebConnectorSvcSoapImpl.qbfs_request_queue.getCompletionPercentage(ticket);
		}
		 */
		else
		{
			Iterator keys = QBWebConnectorSvcSoapImpl.queue_hash.keySet().iterator();
			while (keys.hasNext())
			{
				QuickBooksSettings key = (QuickBooksSettings)keys.next();
				if (!key.isQuickBooksFSTalking())
				{
					try
					{
						key.setQuickBooksFSTalking(true);
						key.save();
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
				QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.queue_hash.get(key);

				if (queue.containsTicket(ticket))
				{
					key.addMessage("Parsing Response " + message);

					try
					{
						QBXMLResponseParser request_parser = new QBXMLResponseParser(key.getCompany());
						QueryRs query_resp = request_parser.parse(response);
						
						System.out.println("query_resp >" + query_resp);
						if (query_resp != null)
						{
							System.out.println("query_resp.getRequestID() >" + query_resp.getRequestID());
						
							QBPOSXMLRequest request = (QBPOSXMLRequest)request_id_request_map.get(query_resp.getRequestID());
							System.out.println("QBPOSXMLRequest found >" + request);
							if (request != null)
							{
								request.setResponseStatusCode(Integer.parseInt(query_resp.getStatusCode()));
								request.setResponseStatusMessage(query_resp.getStatusMessage());
								request.setResponseStatusSeverity(query_resp.getStatusSeverity());
								request.save();
							}
						}
						

						/*
						CashOut cash_out = key.getActiveCashOut();
						if (cash_out != null)
							cash_out.setLogFile(parse_log);
						 */
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
					
					int completion_perc = queue.getCompletionPercentage(ticket);
					key.addMessage(completion_perc + "% Complete");
					return completion_perc;
				}
			}
		}

		return 100;
	}

	public java.lang.String connectionError(java.lang.String ticket, java.lang.String hresult, java.lang.String message) {

		System.out.println("connectionError invoked");
		System.out.println("ticket >" + ticket);
		System.out.println("hresult >" + hresult);
		System.out.println("message >" + message);
		
		// I need to do something with this error message

		//QBPOSXMLRequest request = null;
		Iterator keys = QBWebConnectorSvcSoapImpl.queue_hash.keySet().iterator();
		//while (keys.hasNext() && (request == null))
		while (keys.hasNext())
		{
			QuickBooksSettings key = (QuickBooksSettings)keys.next();
			QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.queue_hash.get(key);

			synchronized (queue)
			{
				//request = queue.getNextRequestForTicket(ticket); // this is doing a remove, which I think is wrong...
				if (queue.containsTicket(ticket)) {
					queue.closeTicket(ticket);
					if (key != null)
						key.addMessage("Error: " + message);
				}
			}

			//if (key != null && request != null)
			
		}
		
		CUBean.sendEmail("marlo@badiyan.com", "valonyx@valeowc.com", message, ticket);

		return "DONE";
	}

	public java.lang.String getLastError(java.lang.String ticket) {

		System.out.println("getLastError invoked");
		System.out.println("ticket >" + ticket);

		return "No errors found...";
	}

	public java.lang.String closeConnection(java.lang.String ticket) {

		System.out.println("closeConnection invoked");
		System.out.println("ticket >" + ticket);
		
		try
		{
		
			if (QBWebConnectorSvcSoapImpl.qbpos_request_queue.containsTicket(ticket))
			{
				synchronized (QBWebConnectorSvcSoapImpl.qbpos_request_queue)
				{
					QBWebConnectorSvcSoapImpl.qbpos_request_queue.closeTicket(ticket);
				}
			}
			/* // commented out on 3/18/10 - Valeo should be setup to use the QuickBooksSettings mechanism
			else if (QBWebConnectorSvcSoapImpl.qbfs_request_queue.containsTicket(ticket))
			{
				synchronized (QBWebConnectorSvcSoapImpl.qbfs_request_queue)
				{
					QBWebConnectorSvcSoapImpl.qbfs_request_queue.closeTicket(ticket);
				}
			}
			 */
			else
			{
				Iterator keys = QBWebConnectorSvcSoapImpl.queue_hash.keySet().iterator();
				while (keys.hasNext())
				{
					QuickBooksSettings key = (QuickBooksSettings)keys.next();
					QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.queue_hash.get(key);

					if (queue.containsTicket(ticket))
					{
						key.addMessage("Closing QuickBooks connection.  Update complete.");

						synchronized (QBWebConnectorSvcSoapImpl.qbfs_request_queue)
						{
							QBWebConnectorSvcSoapImpl.qbfs_request_queue.closeTicket(ticket);
						}

						CashOut active_cash_out = key.getActiveCashOut();

						if (active_cash_out != null)
							active_cash_out.setIsQuickBooksUpdateInProgress(false);


						if (key.isClientUpdateRequestInQueue())
						{
							key.setLastClientUpdateDate(new Date());
							key.save();
						}

						if (key.isInventoryUpdateRequestInQueue())
						{
							key.setLastInventoryUpdateDate(new Date());
							key.save();
						}

						break;
					}
				}


			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		return "OK";
	}

}
