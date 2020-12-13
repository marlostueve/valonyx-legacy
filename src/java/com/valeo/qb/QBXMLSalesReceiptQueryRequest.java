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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
QBXMLSalesReceiptQueryRequest
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

			qb_request = QBXMLSalesReceiptQueryRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLSalesReceiptQueryRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private int max_returned = 10;
	private boolean include_line_items = false;
	
	// CONSTRUCTORS

    public
    QBXMLSalesReceiptQueryRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLSalesReceiptQueryRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

	public void
	setMaxReturned(int _max_returned)
	{
		max_returned = _max_returned;
	}

	public void
	setIncludeLineItems(boolean _include_line_items)
	{
		include_line_items = _include_line_items;
	}

	@Override
	protected void
	assembleRequestBody()
		throws WritingException
	{
		xmlwriter.writeEntity("SalesReceiptQueryRq");
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
		
		Calendar from_date = Calendar.getInstance();
		from_date.set(2010, Calendar.AUGUST, 1);
		Calendar to_date = Calendar.getInstance();
		to_date.set(2011, Calendar.JANUARY, 1);
		
		SimpleDateFormat xFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		xmlwriter.writeEntity("TxnDateRangeFilter");
		xmlwriter.writeEntity("FromTxnDate");
		xmlwriter.writeText(xFormat.format(from_date.getTime()));
		xmlwriter.endEntity();
		xmlwriter.writeEntity("ToTxnDate");
		xmlwriter.writeText(xFormat.format(to_date.getTime()));
		xmlwriter.endEntity();
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

		if (this.include_line_items)
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
		return CUBean.getUserTimeString(super.creation_date) + " - Sales Receipt Request";
	}

	@Override
	public String getRequestType() {
		return "SalesReceiptQueryRq";
	}
}
