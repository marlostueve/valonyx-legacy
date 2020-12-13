/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.authorize.net;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.PasswordGenerator;
import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;

import java.io.IOException;
import java.io.StringReader;

import java.text.SimpleDateFormat;

import java.util.*;
import java.math.BigDecimal;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.torque.*;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author marlo
 */
public class
GatewayResponseParser
	extends DefaultHandler
{
	// INSTANCE VARIABLES

	private String current_element;

	private int responseCode;
	private int errorCode;
	private String errorText;
	private int code;
	private String description;
	private String authCode;
	private String avsResultCode;
	private String cvvResultCode;
	private String transID;
	private String refTransID;
	private String transHash;
	private int testMode;
	private String accountNumber;
	private String accountType;

	// INSTANCE METHODS

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getAccountType() {
		System.out.println("Getting accountType in GatewayResponseParser >" + accountType);
		return accountType;
	}

	public String getAuthCode() {
		return authCode;
	}

	public String getAVSResultCode() {
		return avsResultCode;
	}

	public int getCode() {
		return code;
	}

	public String getCVVResultCode() {
		return cvvResultCode;
	}

	public String getDescription() {
		return description;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorText() {
		return errorText;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public int getTestMode() {
		return testMode;
	}

	public String getTransHash() {
		return transHash;
	}

	public String getTransID() {
		return transID;
	}

	public String getRefTransID() {
		return refTransID;
	}

	public void
	parse(String _response)
	{
		System.out.println("parse invoked >" + this);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try
		{

			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			//parse the file and also register this class for call backs
			sp.parse(new InputSource(new StringReader(_response)), this);


		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	//Event Handlers
	public void startElement(String _uri, String _localName, String _qName, Attributes _attributes) throws SAXException {
		//reset
		System.out.println("ELEMENT >" + _qName);
		current_element = _qName;

		try
		{
			/*
			if (_qName.equals("SalesReceiptQueryRs"))
			{
				salesReceiptQueryRsObject = new SalesReceiptQueryRs();
				if (previous_unprocessed_sales_receipts != null)
				{
					Iterator itr = previous_unprocessed_sales_receipts.iterator();
					while (itr.hasNext())
					{
						SalesReceiptRet previous_unprocessed_sales_receipt = (SalesReceiptRet)itr.next();
						System.out.println("adding previous unprocessed >");
						salesReceiptQueryRsObject.add(previous_unprocessed_sales_receipt);
					}
				}
				this.fetchQueryRsAttributes(salesReceiptQueryRsObject, _attributes);
			}
			 *
			 */

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	/*
	private void
	fetchQueryRsAttributes(QueryRs _response_obj, Attributes _attributes)
	{
		_response_obj.setRequestID(_attributes.getValue("requestID"));
		String value = _attributes.getValue("retCount");
		if (value != null)
			_response_obj.setRetCount(Integer.parseInt(value));
		value = _attributes.getValue("statusCode");
		if (value != null)
			_response_obj.setStatusCode(value);
		value = _attributes.getValue("statusMessage");
		if (value != null)
			_response_obj.setStatusMessage(value);
		value = _attributes.getValue("statusSeverity");
		if (value != null)
			_response_obj.setStatusSeverity(value);
		value = _attributes.getValue("iteratorRemainingCount");
		if (value != null)
			_response_obj.setIteratorRemainingCount(Integer.parseInt(value));
		value = _attributes.getValue("iteratorID");
		if (value != null)
		{
			// find the corresponding request object

			QBPOSXMLRequest iterator_request_obj = QBWebConnectorSvcSoapImpl.pending_iterator_hash.get(_response_obj.getRequestID());
			if (iterator_request_obj == null)
				iterator_request_obj = QBWebConnectorSvcSoapImpl.iterator_hash.get(value);
			else
			{
				QBWebConnectorSvcSoapImpl.pending_iterator_hash.remove(value);
				QBWebConnectorSvcSoapImpl.iterator_hash.put(value, iterator_request_obj);
			}

			if (iterator_request_obj != null)
			{

				iterator_request_obj.setIteratorRemainingCount(_response_obj.getIteratorRemainingCount());

				if (_response_obj.getIteratorRemainingCount() == 0)
				{
					QBWebConnectorSvcSoapImpl.pending_iterator_hash.remove(_response_obj.getRequestID());
					QBWebConnectorSvcSoapImpl.iterator_hash.remove(value);
					iterator_request_obj.setExhaustedIterator(true);
				}
				else
				{
					// convert to continuation

					iterator_request_obj.setContinuation(true);
					iterator_request_obj.setIteratorID(value);
				}


			}

			_response_obj.setIteratorID(value);
		}
	}
	 *
	 */

	public void characters(char[] ch, int start, int length) throws SAXException
	{
		String value = new String(ch,start,length);
		try
		{
			System.out.println(current_element + " = " + value);

			if (current_element.equals("ResponseCode"))
				this.responseCode = Integer.parseInt(value);
			else if (current_element.equals("ErrorCode"))
				this.errorCode = Integer.parseInt(value);
			else if (current_element.equals("ErrorText"))
				this.errorText = value;
			else if (current_element.equals("Code"))
				this.code = Integer.parseInt(value);
			else if (current_element.equals("Description"))
				this.description = value;
			else if (current_element.equals("AuthCode"))
				this.authCode = value;
			else if (current_element.equals("AVSResultCode"))
				this.avsResultCode = value;
			else if (current_element.equals("CVVResultCode"))
				this.cvvResultCode = value;
			else if (current_element.equals("TransID"))
				this.transID = value;
			else if (current_element.equals("RefTransID"))
				this.refTransID = value;
			else if (current_element.equals("TransHash"))
				this.transHash = value;
			else if (current_element.equals("TestMode"))
				this.testMode = Integer.parseInt(value);
			else if (current_element.equals("AccountNumber"))
				this.accountNumber = value;
			else if (current_element.equals("AccountType"))
			{
				System.out.println("found account type >" + value);
				this.accountType = value;
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {}

}
