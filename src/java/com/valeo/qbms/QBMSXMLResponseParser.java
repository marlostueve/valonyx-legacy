/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbms;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;
import com.valeo.qb.QBXMLRequest;

import java.io.IOException;
import java.io.StringReader;

import java.text.SimpleDateFormat;

import java.util.*;
import java.math.BigDecimal;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.valeo.qbpos.data.*;
import com.valeo.qb.data.*;
import com.valeo.qbms.data.SignonAppCertRs;

/**
 *
 * @author marlo
 */
public class
QBMSXMLResponseParser
	extends DefaultHandler
{
	// CLASS VARIABLES

	private static SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat date_time_type_date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			// 2008-10-15T07:41:47-05:00
			// 2008-11-08T09:53:34-06:00

	// INSTANCE VARIABLES

	private UKOnlineCompanyBean company;

	private SignonAppCertRs signonAppCertRsObject;

	private String current_element;

	// CONSTRUCTORS

	public
	QBMSXMLResponseParser(UKOnlineCompanyBean _company)
	{
		company = _company;
	}

	// INSTANCE METHODS

	public SignonAppCertRs getSignonAppCertRsObject() {
		return signonAppCertRsObject;
	}

	public void
	parse(String _response)
	{
		System.out.println("parse invoked >" + this);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			//parse the file and also register this class for call backs
			sp.parse(new InputSource(new StringReader(_response)), this);



			if (signonAppCertRsObject != null)
			{
				System.out.println("NUM signonAppCertRsObject >" + signonAppCertRsObject);

				// do anything???
			}


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
	@Override
	public void startElement(String _uri, String _localName, String _qName, Attributes _attributes) throws SAXException {
		//reset
		System.out.println("ELEMENT >" + _qName);
		current_element = _qName;

		try
		{
			if (_qName.equals("SignonAppCertRs"))
			{
				this.invalidate();
				signonAppCertRsObject = new SignonAppCertRs();
				this.fetchQueryRsAttributes(signonAppCertRsObject, _attributes);
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	private void
	invalidate()
	{
		signonAppCertRsObject = null;
	}

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

			QBXMLRequest iterator_request_obj = (QBXMLRequest)QBWebConnectorSvcSoapImpl.pending_iterator_hash.get(_response_obj.getRequestID());
			if (iterator_request_obj == null)
				iterator_request_obj = (QBXMLRequest)QBWebConnectorSvcSoapImpl.iterator_hash.get(value);
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


	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String value = new String(ch,start,length);
		try
		{
			if (!value.trim().equals(""))
			{
				System.out.println(current_element + " = " + value);
				if (current_element.equals("ServerDateTime"))
				{
					//Date time_modified = time_modified_date_format.parse(value.substring(0, value.lastIndexOf("-")));
					Date server_date_time = date_time_type_date_format.parse(value);
					if (signonAppCertRsObject != null)
						signonAppCertRsObject.setServerDateTime(server_date_time);
				}
				else if (current_element.equals("SessionTicket"))
				{
					if (signonAppCertRsObject != null)
						signonAppCertRsObject.setSessionTicket(value);
				}

			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

	}
}
