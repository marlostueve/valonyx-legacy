/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbpos;

import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import java.util.Date;

import com.generationjava.io.WritingException;

/**
 *
 * @author marlo
 */
public class
QBPOSXMLSalesReceiptQueryRequest
	extends QBPOSXMLRequest
{
	// CLASS METHODS
	
	// INSTANCE VARIABLES
	
	private int max_returned = 10;
	private String match_string_crit = EQUAL_MATCH_STRING_CRITERION;
	private String match_numeric_crit = GREATER_THAN_EQUAL_MATCH_NUMERIC_CRITERION;

	private Date time_created;
	private Date time_modified;
	
	// INSTANCE METHODS

	public void
	setMatchNumericCriterion(String _match_criterion)
	{
		match_numeric_crit = _match_criterion;
	}
	
	public void
	setMatchStringCriterion(String _match_criterion)
	{
		match_string_crit = _match_criterion;
	}
	
	public void
	setMaxReturned(int _max_returned)
	{
		max_returned = _max_returned;
	}
	
	public void
	setTimeCreated(Date _time_created)
	{
		time_created = _time_created;
	}

	public void
	setTimeModified(Date _time_modified)
	{
		time_modified = _time_modified;
	}
	
	@Override
	public String
	toXMLString()
	{
		try
		{
			super.assembleRequest();
			
			System.out.println("GEN XML >" + writer.toString());
			return "<?xml version=\"1.0\" ?><?qbposxml version=\"1.0\"?>" + writer.toString();
		}
		catch (WritingException x)
		{
			x.printStackTrace();
			return "";
		}
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
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
