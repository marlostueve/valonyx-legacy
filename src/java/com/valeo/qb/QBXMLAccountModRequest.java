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
import com.valeo.qb.data.AccountRet;
import com.valeo.qbpos.QBPOSXMLRequest;
import java.math.BigDecimal;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class QBXMLAccountModRequest
	extends QBXMLRequest {

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

			qb_request = QBXMLAccountModRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLAccountModRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private AccountRet account;
	
	// CONSTRUCTORS

    public
    QBXMLAccountModRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLAccountModRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }
	
	// INSTANCE METHODS

	public void
	setAccount(AccountRet _account)
	{
		account = _account;
	}

	@Override
	protected void assembleRequestBody() throws WritingException {
		
		/*
		 * 
    <!-- AccountModRq contains 1 optional attribute: 'requestID' -->
    <AccountModRq requestID = "UUIDTYPE">                   <!-- not in QBOE, v6.0 -->
      <AccountMod>
        <ListID>IDTYPE</ListID>
        <EditSequence>STRTYPE</EdstID>IDTYPE</itSequence>                <!-- max length = 16 for QBD|QBCA|QBUK|QBAU -->
        <Name>STRTYPE</Name>                                <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
        <IsActive>BOOLTYPE</IsActive>                       <!-- opt -->
        <ParentRef>                                         <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt -->
        </ParentRef>
        <!-- AccountType may have one of the following values: AccountsPayable, AccountsReceivable, Bank, CostOfGoodsSold, CreditCard, Equity, Expense, FixedAsset, Income, LongTermLiability, NonPosting, OtherAsset, OtherCurrentAsset, OtherCurrentLiability, OtherExpense, OtherIncome -->
        <AccountType>ENUMTYPE</AccountType>                 <!-- opt -->
        <AccountNumber>STRTYPE</AccountNumber>              <!-- opt, max length = 7 for QBD|QBCA|QBUK|QBAU -->
        <BankNumber>STRTYPE</BankNumber>                    <!-- opt, max length = 25 for QBD|QBCA|QBUK|QBAU -->
        <Desc>STRTYPE</Desc>                                <!-- opt, max length = 200 for QBD|QBCA|QBUK|QBAU -->
        <OpenBalance>AMTTYPE</OpenBalance>                  <!-- opt -->
        <OpenBalanceDate>DATETYPE</OpenBalanceDate>         <!-- opt -->
        <SalesTaxCodeRef>                                   <!-- opt, not in QBD, v6.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
        </SalesTaxCodeRef>
        <TaxLineID>INTTYPE</TaxLineID>                      <!-- opt, v7.0 -->
        <CurrencyRef>                                       <!-- opt, v8.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 64 for QBD|QBCA|QBUK|QBAU -->
        </CurrencyRef>
      </AccountMod>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, v4.0 -->
    </AccountModRq>
		 */

		xmlwriter.writeEntity("AccountModRq");
		xmlwriter.writeAttribute("requestID", getRequestId());

		xmlwriter.writeEntity("AccountMod");

		xmlwriter.writeEntity("ListID");
		xmlwriter.writeText(account.getListID());
		xmlwriter.endEntity();
		
		xmlwriter.writeEntity("EditSequence");
		xmlwriter.writeText(account.getEditSequence());
		xmlwriter.endEntity();

		BigDecimal balance = new BigDecimal(account.getBalance());

		xmlwriter.writeEntity("OpenBalance");
		xmlwriter.writeText(balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
		xmlwriter.endEntity();

		xmlwriter.endEntity(); // </AccountMod>

		xmlwriter.endEntity(); // </AccountModRq>

	}

	@Override
	public String getLabel() {
		return CUBean.getUserTimeString(super.creation_date) + " - Account Mod Request";
	}

	@Override
	public String getRequestType() {
		return "AccountModRq";
	}

}
