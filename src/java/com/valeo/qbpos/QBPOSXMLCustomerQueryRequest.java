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
QBPOSXMLCustomerQueryRequest
	extends QBPOSXMLRequest
{	
	// INSTANCE VARIABLES
	
	private int max_returned = 10;
	private String search_str = null;
	private String match_crit = STARTS_WITH_MATCH_STRING_CRITERION;
	private String match_numeric_crit = GREATER_THAN_EQUAL_MATCH_NUMERIC_CRITERION;

	private Date time_created;
	private Date time_modified;

	private String list_id = null;
	
	// INSTANCE METHODS

	public void
	setListId(String _list_id)
	{
		list_id = _list_id;
	}
	
	public void
	setMatchCriterion(String _match_criterion)
	{
		match_crit = _match_criterion;
	}

	public void
	setMatchNumericCriterion(String _match_criterion)
	{
		match_numeric_crit = _match_criterion;
	}
	public void
	setMaxReturned(int _max_returned)
	{
		max_returned = _max_returned;
	}
	
	public void
	setSearchString(String _search_str)
	{
		search_str = _search_str;
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
	
	public String
	toXMLString()
	{
		/*
		 * <?xml version="1.0" ?>
<?qbposxml version="1.0"?>
<QBPOSXML>
  <QBPOSXMLMsgsRq onError="stopOnError">
    <CustomerQueryRq requestID = "52">
      <MaxReturned>2</MaxReturned>                    <!-- opt -->
      <!-- BEGIN OR: You may optionally have LastNameFilter OR LastNameRangeFilter -->
      <LastNameFilter>
        <!-- MatchStringCriterion may have one of the following values: Equal, StartsWith, Contains, EndsWith -->
        <MatchStringCriterion>Contains</MatchStringCriterion>
        <LastName>LastName</LastName>                        <!-- field max = 30 -->
      </LastNameFilter>
    </CustomerQueryRq>
  </QBPOSXMLMsgsRq>
</QBPOSXML>

		 */
		
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
	
	protected void
	assembleRequestBody()
		throws WritingException
	{
		xmlwriter.writeEntity("CustomerQueryRq");
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
		
		if (list_id != null)
		{
			xmlwriter.writeEntity("ListIDFilter");
			xmlwriter.writeEntity("MatchStringCriterion");
			xmlwriter.writeText(EQUAL_MATCH_STRING_CRITERION);
			xmlwriter.endEntity();
			xmlwriter.writeEntity("ListID");
			xmlwriter.writeText(list_id);
			xmlwriter.endEntity();
			xmlwriter.endEntity();
		}

		if (search_str != null)
		{
			xmlwriter.writeEntity("LastNameFilter");
			xmlwriter.writeEntity("MatchStringCriterion");
			xmlwriter.writeText(match_crit);
			xmlwriter.endEntity();
			xmlwriter.writeEntity("LastName");
			xmlwriter.writeText(search_str);
			xmlwriter.endEntity();
			xmlwriter.endEntity();
		}
		
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
		return CUBean.getUserTimeString(super.creation_date) + " - Customer Query Request";
	}

	@Override
	public String getRequestType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
