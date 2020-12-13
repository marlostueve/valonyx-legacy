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
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
QBXMLCreditMemoQueryRequest
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

			qb_request = QBXMLCreditMemoQueryRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLCreditMemoQueryRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private int max_returned = 10;
	
	// CONSTRUCTORS

    public
    QBXMLCreditMemoQueryRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLCreditMemoQueryRequest(QbRequestDb _request)
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
		xmlwriter.writeEntity("CreditMemoQueryRq");
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

		if (time_modified != null)
		{
			xmlwriter.writeEntity("TimeModifiedFilter");
			xmlwriter.writeEntity("MatchNumericCriterion");
			xmlwriter.writeText(match_numeric_crit);
			xmlwriter.endEntity();
			xmlwriter.writeEntity("TimeModified");
			xmlwriter.writeText(dateTimeTypeFormat.format(time_modified));
			xmlwriter.endEntity();
			xmlwriter.endEntity();
		}
		 */

		xmlwriter.endEntity();
	}

	@Override
	public String
	getLabel()
	{
		return CUBean.getUserTimeString(super.creation_date) + " - Credit Memo Request";
	}

	@Override
	public String getRequestType() {
		return "CreditMemoQueryRq";
	}
}
