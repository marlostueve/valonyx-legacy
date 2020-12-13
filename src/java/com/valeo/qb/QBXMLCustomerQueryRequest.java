/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;

import com.badiyan.torque.QbRequestDb;
import com.badiyan.torque.QbRequestDbPeer;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import java.util.Date;

import com.generationjava.io.WritingException;
import com.valeo.qbpos.QBPOSXMLRequest;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
QBXMLCustomerQueryRequest
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

			qb_request = QBXMLCustomerQueryRequest.getRequest((QbRequestDb)objList.get(0));
		}

		return qb_request;
    }

    private static QBPOSXMLRequest
    getRequest(QbRequestDb _request)
		throws TorqueException
    {
		Integer key = new Integer(_request.getQbRequestDbId());
		QBPOSXMLRequest qb_request = (QBPOSXMLRequest)hash.get(key);
		if (qb_request == null)
		{
			qb_request = new QBXMLCustomerQueryRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private int max_returned = 0;
	private Date from_modified_date;
	private Date to_modified_date;
	
	private String active_status = null;
	
	// CONSTRUCTORS

    public
    QBXMLCustomerQueryRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLCustomerQueryRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

	@Override
	protected void
	assembleRequestBody()
		throws WritingException
	{
		xmlwriter.writeEntity("CustomerQueryRq");
		xmlwriter.writeAttribute("requestID", getRequestId());
		
		System.out.println("assembleRequestBody() continuation >" + continuation);
		System.out.println("assembleRequestBody() iterate >" + iterate);
		System.out.println("assembleRequestBody() iteratorID >" + super.iteratorID);

		if (continuation)
		{
			xmlwriter.writeAttribute("iterator", "Continue");
			xmlwriter.writeAttribute("iteratorID", super.iteratorID);
		}
		else if (iterate)
			xmlwriter.writeAttribute("iterator", "Start");

		if (max_returned > 0)
		{
			xmlwriter.writeEntity("MaxReturned");
			xmlwriter.writeText(max_returned + "");
			xmlwriter.endEntity();
		}

		if (active_status != null)
		{
			xmlwriter.writeEntity("ActiveStatus");
			xmlwriter.writeText(active_status);
			xmlwriter.endEntity();
		}

		/*
		if (time_created != null)
		{
			xmlwriter.writeEntity("TimeCreatedFilter");
			xmlwriter.writeEntity("MatchNumericCriterion");
			xmlwriter.writeText(match_numeric_crit);
			xmlwriter.endEntity();
			xmlwriter.writeEntity("TimeCreated");
			xmlwriter.writeText(dateTimeTypeFormat.format(time_created));
			xmlwriter.endEntity();
			xmlwriter.endEntity();
		}
		 */

		if (from_modified_date != null)
		{
			xmlwriter.writeEntity("FromModifiedDate");
			xmlwriter.writeText(dateTimeTypeFormat.format(from_modified_date));
			xmlwriter.endEntity();
		}

		if (to_modified_date != null)
		{
			xmlwriter.writeEntity("ToModifiedDate");
			xmlwriter.writeText(dateTimeTypeFormat.format(to_modified_date));
			xmlwriter.endEntity();
		}

		xmlwriter.endEntity();
	}

	@Override
	public String
	getLabel()
	{
		return CUBean.getUserTimeString(super.creation_date) + " - Customer Query Request";
	}

	public void
	setMaxReturned(int _max_returned)
	{
		this.max_returned = _max_returned;
	}

	public void
	setActiveStatus(String _active_status)
	{
		active_status = _active_status;
	}

	public void
	setFromModifiedDate(Date _from_modified_date)
	{
		from_modified_date = _from_modified_date;
	}

	public void
	setToModifiedDate(Date _to_modified_date)
	{
		from_modified_date = _to_modified_date;
	}

	@Override
	public String getRequestType() {
		return "CustomerQueryRq";
	}
}
