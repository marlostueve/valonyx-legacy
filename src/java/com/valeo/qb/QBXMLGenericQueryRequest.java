/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;

import com.badiyan.torque.QbRequestDb;
import com.badiyan.torque.QbRequestDbPeer;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.generationjava.io.WritingException;
import com.valeo.qbpos.QBPOSXMLRequest;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
QBXMLGenericQueryRequest
	extends QBXMLRequest
{

    // CLASS METHODS

    public static QBPOSXMLRequest
    getRequest(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		//QB_REQUEST_DB
		QBPOSXMLRequest qb_request = (QBPOSXMLRequest)hash.get(key);
		if (qb_request == null)
		{
			Criteria crit = new Criteria();
			crit.add(QbRequestDbPeer.QB_REQUEST_DB_ID, _id);
			List objList = QbRequestDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate QB Request with id: " + _id);

			qb_request = QBXMLGenericQueryRequest.getRequest((QbRequestDb)objList.get(0));
		}

		return qb_request;
    }

    public static Vector
    getUnresolvedRequests()
		throws TorqueException, ObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(QbRequestDbPeer.RESPONSE_STATUS_CODE, -1);
		Iterator itr = QbRequestDbPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(QBXMLGenericQueryRequest.getRequest((QbRequestDb)itr.next()));
		
		return vec;
    }

    private static QBPOSXMLRequest
    getRequest(QbRequestDb _request)
		throws TorqueException
    {
		Integer key = new Integer(_request.getQbRequestDbId());
		QBPOSXMLRequest qb_request = (QBPOSXMLRequest)hash.get(key);
		if (qb_request == null)
		{
			qb_request = new QBXMLGenericQueryRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private String request_name;

	private int max_returned = 10;

	private Date time_modified;
	private String match_numeric_crit = GREATER_THAN_EQUAL_MATCH_NUMERIC_CRITERION;

	private String active_status = null;
	private boolean use_modified_date_range_filter = true;
	
	private boolean include_line_items = false;

	private String raw_request_str = null;
	
	// CONSTRUCTORS

    public
    QBXMLGenericQueryRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLGenericQueryRequest(QbRequestDb _request)
    {
		request_name = _request.getRequestType();
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS
	
	public int
	getMaxReturned()
	{
		return max_returned;
	}

	public String
	getRequestNameString()
	{
		if (request_name == null)
			return "";
		return request_name;
	}

	public String
	getTimeModifiedString()
	{
		if (time_modified == null)
			return "";
		return CUBean.getUserDateString(time_modified);
	}

	public void
	setMatchNumericCriterion(String _match_criterion)
	{
		match_numeric_crit = _match_criterion;
	}

	public void
	setTimeModified(Date _time_modified)
	{
		time_modified = _time_modified;
	}

	@Override
	protected void
	assembleRequestBody()
		throws WritingException
	{
		//xmlwriter.writeEntity("AccountQueryRq");
		xmlwriter.writeEntity(request_name);
		xmlwriter.writeAttribute("requestID", getRequestId());

		if (continuation)
		{
			xmlwriter.writeAttribute("iterator", "Continue");
			xmlwriter.writeAttribute("iteratorID", super.iteratorID);
		}
		else if (iterate)
			xmlwriter.writeAttribute("iterator", "Start");

		xmlwriter.writeEntity("MaxReturned");
        xmlwriter.writeText(max_returned + "");
		xmlwriter.endEntity();

		if (active_status != null)
		{
			xmlwriter.writeEntity("ActiveStatus");
			xmlwriter.writeText(active_status);
			xmlwriter.endEntity();
		}

		if (time_modified != null)
		{
			/*
			xmlwriter.writeEntity("TimeModifiedFilter");
			xmlwriter.writeEntity("MatchNumericCriterion");
			xmlwriter.writeText(match_numeric_crit);
			xmlwriter.endEntity();
			xmlwriter.writeEntity("TimeModified");
			xmlwriter.writeText(dateTimeTypeFormat.format(time_modified));
			xmlwriter.endEntity();
			xmlwriter.endEntity();
			 */

			if (use_modified_date_range_filter)
				xmlwriter.writeEntity("ModifiedDateRangeFilter");
			xmlwriter.writeEntity("FromModifiedDate");
			xmlwriter.writeText(dateTimeTypeFormat.format(time_modified));
			xmlwriter.endEntity();
			if (use_modified_date_range_filter)
				xmlwriter.endEntity();
		}
		
		if (include_line_items)
		{
			xmlwriter.writeEntity("IncludeLineItems");
			xmlwriter.writeText("true");
			xmlwriter.endEntity();
		}

		xmlwriter.endEntity();
	}

	@Override
	public String
	getLabel()
	{
		return CUBean.getUserTimeString(super.creation_date) + " - " + request_name + " Request";
	}

	public void
	setActiveStatus(String _active_status)
	{
		active_status = _active_status;
	}

	public void
	setRequestName(String _name)
	{
		request_name = _name;
	}

	public void
	setRawRequest(String _raw_request_str)
	{
		raw_request_str = _raw_request_str;
		qb_request.setRequestXml(raw_request_str);
	}

	public void
	setMaxReturned(int _max_returned)
	{
		max_returned = _max_returned;
	}
	
	public void
	setUseModifiedDateRangeFilter(boolean _b)
	{
		use_modified_date_range_filter = _b; 
	}
	
	public void
	setIncludeLineItems(boolean _b)
	{
		include_line_items = _b; 
	}

	@Override
	public String
	toXMLString()
	{
		try
		{
			if (raw_request_str == null)
			{
				String str = assembleRequest();
				System.out.println("GEN XML >" + str);
				return "<?xml version=\"1.0\" ?><?qbxml version=\"7.0\"?>" + str;
			}
			else
			{
				getRequestId();
				System.out.println("GEN XML >" + raw_request_str);
				return "<?xml version=\"1.0\" ?><?qbxml version=\"7.0\"?>" + raw_request_str;
			}

		}
		catch (WritingException x)
		{
			x.printStackTrace();
			return "";
		}
	}

	@Override
	public String getRequestType() {
		return request_name;
	}
}
