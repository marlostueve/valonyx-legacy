/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbpos;

import com.badiyan.torque.QbRequestDb;
import com.badiyan.torque.QbRequestDbPeer;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.beans.UKOnlineCompanyBean;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.generationjava.io.WritingException;
import com.generationjava.io.xml.XmlWriter;
import java.util.HashMap;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public abstract class
QBPOSXMLRequest
	extends CUBean
{
	// CLASS VARIABLES
	
	public static final String STARTS_WITH_MATCH_STRING_CRITERION = "StartsWith";
	public static final String CONTAINS_MATCH_STRING_CRITERION = "Contains";
	public static final String EQUAL_MATCH_STRING_CRITERION = "Equal";
	public static final String ENDS_WITH_MATCH_STRING_CRITERION = "EndsWith";
	
	public static final String LESS_THAN_MATCH_NUMERIC_CRITERION = "LessThan";
	public static final String LESS_THAN_EQUAL_MATCH_NUMERIC_CRITERION = "LessThanEqual";
	public static final String EQUAL_MATCH_NUMERIC_CRITERION = "Equal";
	public static final String GREATER_THAN_MATCH_NUMERIC_CRITERION = "GreaterThan";
	public static final String GREATER_THAN_EQUAL_MATCH_NUMERIC_CRITERION = "GreaterThanEqual";
	
	//protected static SimpleDateFormat dateTimeTypeFormat = new SimpleDateFormat("MM-dd-yy");
	//protected static SimpleDateFormat dateTimeTypeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'-05:00'");
	protected static SimpleDateFormat dateTimeTypeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    protected static HashMap<Integer,QBPOSXMLRequest> hash = new HashMap<Integer,QBPOSXMLRequest>(11);
	

    // SQL

    /*
     *        <table name="QB_REQUEST_DB" idMethod="native">
				<column name="QB_REQUEST_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
				<column name="COMPANY_ID" required="true" type="INTEGER"/>

				<column name="REQUEST_ID" required="true" type="VARCHAR" size="50"/>
				<column name="REQUEST_TYPE" required="true" type="VARCHAR" size="30"/>

				<column name="REQUEST_XML" type="LONGVARCHAR"/>

				<column name="RESPONSE_STATUS_CODE"  required="false" type="INTEGER"/>
				<column name="RESPONSE_STATUS_SEVERITY" required="false" type="VARCHAR" size="10"/>
				<column name="RESPONSE_STATUS_MESSAGE" required="false" type="VARCHAR" size="250"/>

				<foreign-key foreignTable="COMPANY">
					<reference local="COMPANY_ID" foreign="COMPANYID"/>
				</foreign-key>
			</table>
     */

    public static String
    getResponseStatusMessageForRequestID(String _requestID)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(QbRequestDbPeer.REQUEST_ID, _requestID);
		List list = QbRequestDbPeer.doSelect(crit);
		if (list.size() == 1) {
			QbRequestDb obj = (QbRequestDb)list.get(0);
			return obj.getResponseStatusMessage();
		} else if (list.size() == 0) {
			throw new ObjectNotFoundException("Error message not found for RequestID :" + _requestID);
		} else {
			throw new UniqueObjectNotFoundException("Error message not found for RequestID :" + _requestID);
		}
    }
	
	// INSTANCE VARIABLES

	protected Date creation_date = new Date();
	
	protected String xml_str;
	protected Writer writer;
	protected XmlWriter xmlwriter;
	
	//private String request_id;
		
	protected boolean iterate = false;
	protected int iteration = 0;
	protected int iteratorRemainingCount = 0;
	protected String iteratorID;

	protected boolean continuation = false;
	protected boolean exhaustedIterator = false;
	
	private boolean isAddRequest = false;
	private String refNumber;
	
	protected QbRequestDb qb_request;
	
	// METHODS
	
	public void
	setIsAddRequest(boolean _b) {
		isAddRequest = _b;
	}
	
	public boolean
	isAddRequest() {
		return isAddRequest;
	}
	
	protected void
	setRefNumber(String _s) {
		refNumber = _s;
	}
	
	public boolean
	hasRefNumber() {
		return (refNumber != null);
	}
	
	public String
	getRefNumber() {
		return refNumber;
	}
	
	protected String
	assembleRequest()
		throws WritingException
	{
		if (writer == null)
		{
			writer = new java.io.StringWriter();
			xmlwriter = new XmlWriter(writer);

			xmlwriter.writeEntity("QBPOSXML");
			xmlwriter.writeEntity("QBPOSXMLMsgsRq");
			xmlwriter.writeAttribute("onError", "stopOnError");

			assembleRequestBody();

			xmlwriter.endEntity();
			xmlwriter.endEntity();
			xmlwriter.close();
		}
		
		return writer.toString();
	}
	
	protected abstract void
	assembleRequestBody()
		throws WritingException;

	public int
	getIteratorRemainingCount()
	{
		return iteratorRemainingCount;
	}

	public abstract String
	getLabel();

	public abstract String
	getRequestType();
	
	public String
	getRequestId()
	{
		String request_id = qb_request.getRequestId();
		if (request_id == null)
		{
			UUID generated_uuid = UUID.randomUUID();
			request_id = generated_uuid.toString();
			qb_request.setRequestId(request_id);
		}
		//System.out.println("getRequestId() invoked in QBPOSXMLRequest >" + request_id);
		return request_id;
	}

	public boolean
	isContinuation()
	{
		return continuation;
	}

	public boolean
	isInitialIterator()
	{
		//System.out.println("iterate >" + iterate);
		//System.out.println("iteratorID >" + iteratorID);
		return (iterate && (iteratorID == null));
	}

	public boolean
	isIterator()
	{
		return iterate;
	}

	public boolean
	isExhaustedIterator()
	{
		return exhaustedIterator;
	}

	public void
	setContinuation(boolean _continuation)
	{
		continuation = _continuation;
	}

	public void
	setExhaustedIterator(boolean _exhaustedIterator)
	{
		exhaustedIterator = _exhaustedIterator;
	}
	
	public void
	setIterate(boolean _iterate)
		throws IllegalValueException
	{
		iterate = _iterate;
	}

	public void
	setIteratorID(String _iteratorID)
	{
		iteratorID = _iteratorID;
	}

	public void
	setIteratorRemainingCount(int _iteratorRemainingCount)
	{
		iteratorRemainingCount = _iteratorRemainingCount;
	}
	
	public abstract String
	toXMLString();

	public UKOnlineCompanyBean getCompany() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(qb_request.getCompanyId());
	}

	public void setCompany(UKOnlineCompanyBean _company) throws TorqueException {
		qb_request.setCompanyId(_company.getId());
	}

	public String getRequestXMLString() {
		String str = qb_request.getRequestXml();
		if (str == null)
			return "";
		return str;
	}

	public String getRequestTypeString() {
		return qb_request.getRequestType();
	}

	public int getResponseStatusCode() {
		return qb_request.getResponseStatusCode();
	}

	public void setResponseStatusCode(int _responseStatusCode) {
		qb_request.setResponseStatusCode(_responseStatusCode);
	}

	public String getResponseStatusMessage() {
		return qb_request.getResponseStatusMessage();
	}

	public void setResponseStatusMessage(String _responseStatusMessage) {
		qb_request.setResponseStatusMessage(_responseStatusMessage);
	}

	public String getResponseStatusSeverity() {
		return qb_request.getResponseStatusSeverity();
	}

	public void setResponseStatusSeverity(String _responseStatusSeverity) {
		qb_request.setResponseStatusSeverity(_responseStatusSeverity);
	}

	@Override
	protected void insertObject() throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		if ((qb_request.getRequestXml() == null) || qb_request.getRequestXml().equals(""))
			qb_request.setRequestXml(assembleRequest());
		qb_request.setResponseStatusCode(-1);
		
		qb_request.setCreationDate(new Date());
		
		//System.out.println("qb_request.getRequestId() >" + qb_request.getRequestId());
		
		this.getRequestId();
		if (this.getRequestType() == null)
			qb_request.setRequestType("Unknown");
		else
			qb_request.setRequestType(this.getRequestType());
		
		qb_request.save();
	}

	@Override
	protected void updateObject() throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		qb_request.setModificationDate(new Date());
		qb_request.save();
	}
}
