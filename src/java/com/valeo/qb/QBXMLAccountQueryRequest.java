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
import com.generationjava.io.WritingException;
import com.valeo.qbpos.QBPOSXMLRequest;
import java.util.Date;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
QBXMLAccountQueryRequest
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

			qb_request = QBXMLAccountQueryRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLAccountQueryRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private int max_returned = -1;

	private Date from_modified_date;
	private Date to_modified_date;
	
	// CONSTRUCTORS

    public
    QBXMLAccountQueryRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLAccountQueryRequest(QbRequestDb _request)
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
		xmlwriter.writeEntity("AccountQueryRq");
		xmlwriter.writeAttribute("requestID", getRequestId());

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

		if (from_modified_date != null)
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

			//xmlwriter.writeEntity("ModifiedDateRangeFilter"); // apparently this isn't supported.
			xmlwriter.writeEntity("FromModifiedDate"); // I think this is supported
			xmlwriter.writeText(dateTimeTypeFormat.format(from_modified_date));
			xmlwriter.endEntity();
			//xmlwriter.endEntity();
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
		return CUBean.getUserTimeString(super.creation_date) + " - Account Request";
	}

	public void
	setMaxReturned(int _max_returned)
	{
		max_returned = _max_returned;
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
	public void
	setIterate(boolean _iterate)
		throws IllegalValueException
	{
		throw new IllegalValueException("Element AccountQueryRq does not support the iterator attribute.");
	}

	@Override
	public String getRequestType() {
		return "AccountQueryRq";
	}
}
