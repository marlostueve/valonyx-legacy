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
QBXMLReceivePaymentQueryRequest
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

			qb_request = QBXMLReceivePaymentQueryRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLReceivePaymentQueryRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private int max_returned = 10;

	private Date time_modified;
	private String match_numeric_crit = GREATER_THAN_EQUAL_MATCH_NUMERIC_CRITERION;
	
	// CONSTRUCTORS

    public
    QBXMLReceivePaymentQueryRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLReceivePaymentQueryRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

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
	
	/*
    <ReceivePaymentQueryRq requestID = "UUIDTYPE" metaData = "ENUMTYPE" iterator = "ENUMTYPE" iteratorID = "UUIDTYPE"> <!-- v1.1 -->
      <!-- BEGIN OR: You may optionally have TxnID OR RefNumber OR RefNumberCaseSensitive OR ( MaxReturned AND ( ModifiedDateRangeFilter OR TxnDateRangeFilter ) AND EntityFilter AND AccountFilter AND ( RefNumberFilter OR RefNumberRangeFilter ) AND CurrencyFilter )  -->
      <TxnID>IDTYPE</TxnID>                                 <!-- rep (1 or more) -->
      <!-- OR -->
      <RefNumber>STRTYPE</RefNumber>                        <!-- rep (1 or more) -->
      <!-- OR -->
      <RefNumberCaseSensitive>STRTYPE</RefNumberCaseSensitive> <!-- rep (1 or more), v4.0 -->
      <!-- OR -->
      <MaxReturned>INTTYPE</MaxReturned>                    <!-- opt, min value = 1 -->
      <!-- BEGIN OR: You may optionally have ModifiedDateRangeFilter OR TxnDateRangeFilter -->
      <ModifiedDateRangeFilter>
        <FromModifiedDate>DATETIMETYPE</FromModifiedDate>   <!-- opt -->
        <ToModifiedDate>DATETIMETYPE</ToModifiedDate>       <!-- opt -->
      </ModifiedDateRangeFilter>
      <!-- OR -->
      <TxnDateRangeFilter>
        <!-- BEGIN OR: You may have  ( FromTxnDate AND ToTxnDate )  OR DateMacro -->
        <FromTxnDate>DATETYPE</FromTxnDate>                 <!-- opt -->
        <ToTxnDate>DATETYPE</ToTxnDate>                     <!-- opt -->
        <!-- DateMacro may have one of the following values: All, Today, ThisWeek, ThisWeekToDate, ThisMonth, ThisMonthToDate, ThisCalendarQuarter, ThisCalendarQuarterToDate, ThisFiscalQuarter, ThisFiscalQuarterToDate, ThisCalendarYear, ThisCalendarYearToDate, ThisFiscalYear, ThisFiscalYearToDate, Yesterday, LastWeek, LastWeekToDate, LastMonth, LastMonthToDate, LastCalendarQuarter, LastCalendarQuarterToDate, LastFiscalQuarter, LastFiscalQuarterToDate, LastCalendarYear, LastCalendarYearToDate, LastFiscalYear, LastFiscalYearToDate, NextWeek, NextFourWeeks, NextMonth, NextCalendarQuarter, NextCalendarYear, NextFiscalQuarter, NextFiscalYear -->
        <!-- OR -->
        <DateMacro>ENUMTYPE</DateMacro>
        <!-- END OR -->
      </TxnDateRangeFilter>
      <!-- END OR -->
      <EntityFilter>                                        <!-- opt -->
        <!-- BEGIN OR: You may have ListID OR FullName OR ListIDWithChildren OR FullNameWithChildren -->
        <ListID>IDTYPE</ListID>                             <!-- rep (1 or more), v2.0 -->
        <!-- OR -->
        <FullName>STRTYPE</FullName>                        <!-- rep (1 or more) -->
        <!-- OR -->
        <ListIDWithChildren>IDTYPE</ListIDWithChildren>     <!-- v2.0 -->
        <!-- OR -->
        <FullNameWithChildren>STRTYPE</FullNameWithChildren>
        <!-- END OR -->
      </EntityFilter>
      <AccountFilter>                                       <!-- opt, not in QBOE -->
        <!-- BEGIN OR: You may have ListID OR FullName OR ListIDWithChildren OR FullNameWithChildren -->
        <ListID>IDTYPE</ListID>                             <!-- rep (1 or more), v2.0 -->
        <!-- OR -->
        <FullName>STRTYPE</FullName>                        <!-- rep (1 or more) -->
        <!-- OR -->
        <ListIDWithChildren>IDTYPE</ListIDWithChildren>     <!-- v2.0 -->
        <!-- OR -->
        <FullNameWithChildren>STRTYPE</FullNameWithChildren>
        <!-- END OR -->
      </AccountFilter>
      <!-- BEGIN OR: You may optionally have RefNumberFilter OR RefNumberRangeFilter -->
      <RefNumberFilter>
        <!-- MatchCriterion may have one of the following values: StartsWith, Contains, EndsWith -->
        <MatchCriterion>ENUMTYPE</MatchCriterion>
        <RefNumber>STRTYPE</RefNumber>
      </RefNumberFilter>
      <!-- OR -->
      <RefNumberRangeFilter>
        <FromRefNumber>STRTYPE</FromRefNumber>              <!-- opt -->
        <ToRefNumber>STRTYPE</ToRefNumber>                  <!-- opt -->
      </RefNumberRangeFilter>
      <!-- END OR -->
      <CurrencyFilter>                                      <!-- opt, not in QBOE, v8.0 -->
        <!-- BEGIN OR: You may have ListID OR FullName -->
        <ListID>IDTYPE</ListID>                             <!-- rep (1 or more) -->
        <!-- OR -->
        <FullName>STRTYPE</FullName>                        <!-- rep (1 or more), max length = 64 for QBD|QBCA|QBUK|QBAU -->
        <!-- END OR -->
      </CurrencyFilter>
      <!-- END OR -->
      <IncludeLineItems>BOOLTYPE</IncludeLineItems>         <!-- opt, not in QBOE -->
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, not in QBOE, v4.0 -->
      <OwnerID>GUIDTYPE</OwnerID>                           <!-- opt, may rep, not in QBOE, v2.0 -->
    </ReceivePaymentQueryRq>
	*/
	
	@Override
	protected void
	assembleRequestBody()
		throws WritingException
	{
		xmlwriter.writeEntity("ReceivePaymentQueryRq");
		xmlwriter.writeAttribute("requestID", getRequestId());
		
		System.out.println("ReceivePaymentQueryRq iterate >" + iterate);

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
		 * <?qbxml version="7.0"?>
<QBXML>
	<QBXMLMsgsRq onError="stopOnError">
		<ReceivePaymentQueryRq requestID="33d870e4-0e3e-4623-8857-9e0ed8cfb2af">
			<MaxReturned>10</MaxReturned>
			<ModifiedDateRangeFilter>
				<FromModifiedDate>2009-03-19T19:55:45</FromModifiedDate>
			</ModifiedDateRangeFilter>
		</ReceivePaymentQueryRq>
	</QBXMLMsgsRq>
</QBXML>
		 */

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

			xmlwriter.writeEntity("ModifiedDateRangeFilter");
			xmlwriter.writeEntity("FromModifiedDate");
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
		return CUBean.getUserTimeString(super.creation_date) + " - Receive Payment Request";
	}

	public void
	setMaxReturned(int _max_returned)
	{
		max_returned = _max_returned;
	}

	@Override
	public String getRequestType() {
		return "ReceivePaymentQueryRq";
	}
}
