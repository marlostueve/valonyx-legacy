/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;

import com.badiyan.torque.CheckoutOrderline;
import com.badiyan.torque.QbRequestDb;
import com.badiyan.torque.QbRequestDbPeer;
import com.badiyan.torque.TenderRetDbOrderMapping;
import com.badiyan.torque.TenderRetDbOrderMappingPeer;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.CCUtils;
import com.generationjava.io.WritingException;
import com.valeo.qb.data.AccountRet;
import com.valeo.qb.data.ItemPaymentRet;
import com.valeo.qb.data.PaymentMethodRet;
import com.valeo.qb.data.SalesTaxCodeRet;
import com.valeo.qbpos.QBPOSXMLRequest;
import com.valeo.qbpos.data.TenderRet;
import com.valonyx.events.QBCustomerAddEvent;
import com.valonyx.events.QBCustomerAddEventListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.generationjava.io.xml.XmlWriter;

/**
 *
 * @author marlo
 */
public class QBXMLSalesReceiptAddRequest
	extends QBXMLRequest
	implements QBCustomerAddEventListener {
	
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

			qb_request = QBXMLSalesReceiptAddRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLSalesReceiptAddRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private UKOnlinePersonBean client;
	private ValeoOrderBean order;
	
	// CONSTRUCTORS

    public
    QBXMLSalesReceiptAddRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLSalesReceiptAddRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

	public void
	setOrder(ValeoOrderBean _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		if (_order.isUpdated())
			throw new IllegalValueException("Order " + _order.getLabel() + " has already been updated to QuickBooks");

		order = _order;
		client = _order.getClient();
		
		// if this client hasn't been synced with quickbooks, I need to listen for when it is synced
		
		try
		{
			client.getQBFSListID();
		}
		catch (IllegalValueException x)
		{
			client.addQBCustomerAddEventListener(this);
		}
			
	}

	@Override
	protected void assembleRequestBody() throws WritingException {

		/*
    <SalesReceiptAddRq requestID = "UUIDTYPE">
      <!-- SalesReceiptAdd contains 1 optional attribute: 'defMacro' -->
      <SalesReceiptAdd defMacro = "MACROTYPE">
        <CustomerRef>                                       <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 209 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
        </CustomerRef>
        <ClassRef>                                          <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
        </ClassRef>
        <TemplateRef>                                       <!-- opt, not in QBOE, v3.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
        </TemplateRef>
        <TxnDate>DATETYPE</TxnDate>                         <!-- opt -->
        <RefNumber>STRTYPE</RefNumber>                      <!-- opt, max length = 11 for QBD|QBCA|QBUK|QBAU, max length = 21 for QBOE -->
        <BillAddress>                                       <!-- opt -->
          <Addr1>STRTYPE</Addr1>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr2>STRTYPE</Addr2>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr3>STRTYPE</Addr3>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr4>STRTYPE</Addr4>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE, v2.0 -->
          <Addr5>STRTYPE</Addr5>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
          <City>STRTYPE</City>                              <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <State>STRTYPE</State>                            <!-- opt, max length = 21 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <PostalCode>STRTYPE</PostalCode>                  <!-- opt, max length = 13 for QBD|QBCA|QBUK|QBAU, max length = 30 for QBOE -->
          <Country>STRTYPE</Country>                        <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <Note>STRTYPE</Note>                              <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
        </BillAddress>
        <ShipAddress>                                       <!-- opt -->
          <Addr1>STRTYPE</Addr1>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr2>STRTYPE</Addr2>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr3>STRTYPE</Addr3>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr4>STRTYPE</Addr4>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE, v2.0 -->
          <Addr5>STRTYPE</Addr5>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
          <City>STRTYPE</City>                              <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <State>STRTYPE</State>                            <!-- opt, max length = 21 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <PostalCode>STRTYPE</PostalCode>                  <!-- opt, max length = 13 for QBD|QBCA|QBUK|QBAU, max length = 30 for QBOE -->
          <Country>STRTYPE</Country>                        <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <Note>STRTYPE</Note>                              <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
        </ShipAddress>
        <IsPending>BOOLTYPE</IsPending>                     <!-- opt, not in QBOE -->
        <CheckNumber>STRTYPE</CheckNumber>                  <!-- opt, max length = 25 -->
        <PaymentMethodRef>                                  <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 100 for QBOE -->
        </PaymentMethodRef>
        <DueDate>DATETYPE</DueDate>                         <!-- opt, not in QBOE -->
        <SalesRepRef>                                       <!-- opt, not in QBOE -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 5 for QBD|QBCA|QBUK|QBAU -->
        </SalesRepRef>
        <ShipDate>DATETYPE</ShipDate>                       <!-- opt -->
        <ShipMethodRef>                                     <!-- opt, not in QBOE -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 15 for QBD|QBCA|QBUK|QBAU -->
        </ShipMethodRef>
        <FOB>STRTYPE</FOB>                                  <!-- opt, max length = 13 for QBD|QBCA|QBUK|QBAU, not in QBOE -->
        <ItemSalesTaxRef>                                   <!-- opt, not in QBOE -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
        </ItemSalesTaxRef>
        <Memo>STRTYPE</Memo>                                <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU, max length = 4000 for QBOE -->
        <CustomerMsgRef>                                    <!-- opt, not in QBOE -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 101 for QBD|QBCA|QBUK|QBAU -->
        </CustomerMsgRef>
        <IsToBePrinted>BOOLTYPE</IsToBePrinted>             <!-- opt -->
        <IsToBeEmailed>BOOLTYPE</IsToBeEmailed>             <!-- opt, not in QBOE, v6.0 -->
        <IsTaxIncluded>BOOLTYPE</IsTaxIncluded>             <!-- opt, not in QBD|QBOE, v6.0 -->
        <CustomerSalesTaxCodeRef>                           <!-- opt, not in QBOE -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 3 for QBD|QBCA|QBUK, max length = 6 for QBAU -->
        </CustomerSalesTaxCodeRef>
        <DepositToAccountRef>                               <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
        </DepositToAccountRef>
        <CreditCardTxnInfo>                                 <!-- opt, not in QBOE, v4.1 -->
          <CreditCardTxnInputInfo>
            <CreditCardNumber>STRTYPE</CreditCardNumber>    <!-- max length = 25 for QBD|QBCA|QBUK|QBAU -->
            <ExpirationMonth>INTTYPE</ExpirationMonth>      <!-- min value = 1, max value = 12 -->
            <ExpirationYear>INTTYPE</ExpirationYear>
            <NameOnCard>STRTYPE</NameOnCard>                <!-- max length = 41 for QBD|QBCA|QBUK|QBAU -->
            <CreditCardAddress>STRTYPE</CreditCardAddress>  <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU -->
            <CreditCardPostalCode>STRTYPE</CreditCardPostalCode> <!-- opt, max length = 18 for QBD|QBCA|QBUK|QBAU -->
            <CommercialCardCode>STRTYPE</CommercialCardCode> <!-- opt, max length = 24 for QBD|QBCA|QBUK|QBAU -->
            <!-- TransactionMode may have one of the following values: CardNotPresent [DEFAULT], CardPresent -->
            <TransactionMode>ENUMTYPE</TransactionMode>     <!-- opt, v6.0 -->
            <!-- CreditCardTxnType may have one of the following values: Authorization, Capture, Charge, Refund, VoiceAuthorization -->
            <CreditCardTxnType>ENUMTYPE</CreditCardTxnType> <!-- opt, v7.0 -->
          </CreditCardTxnInputInfo>
          <CreditCardTxnResultInfo>
            <ResultCode>INTTYPE</ResultCode>
            <ResultMessage>STRTYPE</ResultMessage>          <!-- max length = 60 for QBD|QBCA|QBUK|QBAU -->
            <CreditCardTransID>STRTYPE</CreditCardTransID>  <!-- max length = 24 for QBD|QBCA|QBUK|QBAU -->
            <MerchantAccountNumber>STRTYPE</MerchantAccountNumber> <!-- max length = 32 for QBD|QBCA|QBUK|QBAU -->
            <AuthorizationCode>STRTYPE</AuthorizationCode>  <!-- opt, max length = 12 for QBD|QBCA|QBUK|QBAU -->
            <!-- AVSStreet may have one of the following values: Pass, Fail, NotAvailable -->
            <AVSStreet>ENUMTYPE</AVSStreet>                 <!-- opt -->
            <!-- AVSZip may have one of the following values: Pass, Fail, NotAvailable -->
            <AVSZip>ENUMTYPE</AVSZip>                       <!-- opt -->
            <!-- CardSecurityCodeMatch may have one of the following values: Pass, Fail, NotAvailable -->
            <CardSecurityCodeMatch>ENUMTYPE</CardSecurityCodeMatch> <!-- opt, v6.0 -->
            <ReconBatchID>STRTYPE</ReconBatchID>            <!-- opt, max length = 84 for QBD|QBCA|QBUK|QBAU -->
            <PaymentGroupingCode>INTTYPE</PaymentGroupingCode> <!-- opt -->
            <!-- PaymentStatus may have one of the following values: Unknown, Completed -->
            <PaymentStatus>ENUMTYPE</PaymentStatus>
            <TxnAuthorizationTime>DATETIMETYPE</TxnAuthorizationTime>
            <TxnAuthorizationStamp>INTTYPE</TxnAuthorizationStamp> <!-- opt -->
            <ClientTransID>STRTYPE</ClientTransID>          <!-- opt, max length = 16 for QBD|QBCA|QBUK|QBAU, v6.0 -->
          </CreditCardTxnResultInfo>
        </CreditCardTxnInfo>
        <Other>STRTYPE</Other>                              <!-- opt, max length = 29 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
        <ExchangeRate>FLOATTYPE</ExchangeRate>              <!-- opt, not in QBOE, v8.0 -->
        <!-- BEGIN OR: You may have 1 or more SalesReceiptLineAdd OR SalesReceiptLineGroupAdd -->
        <!-- SalesReceiptLineAdd contains 1 optional attribute: 'defMacro' -->
        <SalesReceiptLineAdd defMacro = "MACROTYPE">
          <ItemRef>                                         <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt -->
          </ItemRef>
          <Desc>STRTYPE</Desc>                              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU, max length = 4000 for QBOE -->
          <Quantity>QUANTYPE</Quantity>                     <!-- opt -->
          <UnitOfMeasure>STRTYPE</UnitOfMeasure>            <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, not in QBOE, v7.0 -->
          <!-- BEGIN OR: You may optionally have Rate OR RatePercent OR PriceLevelRef -->
          <Rate>PRICETYPE</Rate>
          <!-- OR -->
          <RatePercent>PERCENTTYPE</RatePercent>
          <!-- OR -->
          <PriceLevelRef>                                   <!-- not in QBOE, v4.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
          </PriceLevelRef>
          <!-- END OR -->
          <ClassRef>                                        <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
          </ClassRef>
          <Amount>AMTTYPE</Amount>                          <!-- opt -->
          <TaxAmount>AMTTYPE</TaxAmount>                    <!-- opt, not in QBD|QBCA|QBUK|QBOE, v6.1 -->
          <ServiceDate>DATETYPE</ServiceDate>               <!-- opt -->
          <SalesTaxCodeRef>                                 <!-- opt, not in QBOE -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 3 for QBD|QBCA|QBUK, max length = 6 for QBAU -->
          </SalesTaxCodeRef>
          <IsTaxable>BOOLTYPE</IsTaxable>                   <!-- opt, not in QBD|QBCA|QBUK|QBAU, v4.0 -->
          <OverrideItemAccountRef>                          <!-- opt, v2.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
          </OverrideItemAccountRef>
          <Other1>STRTYPE</Other1>                          <!-- opt, max length = 29 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
          <Other2>STRTYPE</Other2>                          <!-- opt, max length = 29 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
          <CreditCardTxnInfo>                               <!-- opt, not in QBOE, v7.0 -->
            <CreditCardTxnInputInfo>
              <CreditCardNumber>STRTYPE</CreditCardNumber>  <!-- max length = 25 for QBD|QBCA|QBUK|QBAU -->
              <ExpirationMonth>INTTYPE</ExpirationMonth>    <!-- min value = 1, max value = 12 -->
              <ExpirationYear>INTTYPE</ExpirationYear>
              <NameOnCard>STRTYPE</NameOnCard>              <!-- max length = 41 for QBD|QBCA|QBUK|QBAU -->
              <CreditCardAddress>STRTYPE</CreditCardAddress> <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU -->
              <CreditCardPostalCode>STRTYPE</CreditCardPostalCode> <!-- opt, max length = 18 for QBD|QBCA|QBUK|QBAU -->
              <CommercialCardCode>STRTYPE</CommercialCardCode> <!-- opt, max length = 24 for QBD|QBCA|QBUK|QBAU -->
              <!-- TransactionMode may have one of the following values: CardNotPresent [DEFAULT], CardPresent -->
              <TransactionMode>ENUMTYPE</TransactionMode>   <!-- opt, v6.0 -->
              <!-- CreditCardTxnType may have one of the following values: Authorization, Capture, Charge, Refund, VoiceAuthorization -->
              <CreditCardTxnType>ENUMTYPE</CreditCardTxnType> <!-- opt, v7.0 -->
            </CreditCardTxnInputInfo>
            <CreditCardTxnResultInfo>
              <ResultCode>INTTYPE</ResultCode>
              <ResultMessage>STRTYPE</ResultMessage>        <!-- max length = 60 for QBD|QBCA|QBUK|QBAU -->
              <CreditCardTransID>STRTYPE</CreditCardTransID> <!-- max length = 24 for QBD|QBCA|QBUK|QBAU -->
              <MerchantAccountNumber>STRTYPE</MerchantAccountNumber> <!-- max length = 32 for QBD|QBCA|QBUK|QBAU -->
              <AuthorizationCode>STRTYPE</AuthorizationCode> <!-- opt, max length = 12 for QBD|QBCA|QBUK|QBAU -->
              <!-- AVSStreet may have one of the following values: Pass, Fail, NotAvailable -->
              <AVSStreet>ENUMTYPE</AVSStreet>               <!-- opt -->
              <!-- AVSZip may have one of the following values: Pass, Fail, NotAvailable -->
              <AVSZip>ENUMTYPE</AVSZip>                     <!-- opt -->
              <!-- CardSecurityCodeMatch may have one of the following values: Pass, Fail, NotAvailable -->
              <CardSecurityCodeMatch>ENUMTYPE</CardSecurityCodeMatch> <!-- opt, v6.0 -->
              <ReconBatchID>STRTYPE</ReconBatchID>          <!-- opt, max length = 84 for QBD|QBCA|QBUK|QBAU -->
              <PaymentGroupingCode>INTTYPE</PaymentGroupingCode> <!-- opt -->
              <!-- PaymentStatus may have one of the following values: Unknown, Completed -->
              <PaymentStatus>ENUMTYPE</PaymentStatus>
              <TxnAuthorizationTime>DATETIMETYPE</TxnAuthorizationTime>
              <TxnAuthorizationStamp>INTTYPE</TxnAuthorizationStamp> <!-- opt -->
              <ClientTransID>STRTYPE</ClientTransID>        <!-- opt, max length = 16 for QBD|QBCA|QBUK|QBAU, v6.0 -->
            </CreditCardTxnResultInfo>
          </CreditCardTxnInfo>
          <DataExt>                                         <!-- opt, may rep, not in QBOE, v5.0 -->
            <OwnerID>GUIDTYPE</OwnerID>
            <DataExtName>STRTYPE</DataExtName>              <!-- max length = 31 for QBD|QBCA|QBUK|QBAU -->
            <DataExtValue>STRTYPE</DataExtValue>
          </DataExt>
        </SalesReceiptLineAdd>
        <!-- OR -->
        <SalesReceiptLineGroupAdd>                          <!-- not in QBOE -->
          <ItemGroupRef>
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
          </ItemGroupRef>
          <Desc>STRTYPE</Desc>                              <!-- opt, not in QBD|QBCA|QBUK|QBAU -->
          <Quantity>QUANTYPE</Quantity>                     <!-- opt -->
          <UnitOfMeasure>STRTYPE</UnitOfMeasure>            <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, v7.0 -->
          <ServiceDate>DATETYPE</ServiceDate>               <!-- opt, not in QBD|QBCA|QBUK|QBAU -->
          <DataExt>                                         <!-- opt, may rep, v5.0 -->
            <OwnerID>GUIDTYPE</OwnerID>
            <DataExtName>STRTYPE</DataExtName>              <!-- max length = 31 for QBD|QBCA|QBUK|QBAU -->
            <DataExtValue>STRTYPE</DataExtValue>
          </DataExt>
        </SalesReceiptLineGroupAdd>
        <!-- END OR -->
        <DiscountLineAdd>                                   <!-- opt, not in QBD|QBCA|QBUK|QBAU, v4.0 -->
          <!-- BEGIN OR: You may optionally have Amount OR RatePercent -->
          <Amount>AMTTYPE</Amount>
          <!-- OR -->
          <RatePercent>PERCENTTYPE</RatePercent>
          <!-- END OR -->
          <IsTaxable>BOOLTYPE</IsTaxable>                   <!-- opt -->
          <AccountRef>                                      <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 1000 for QBOE -->
          </AccountRef>
        </DiscountLineAdd>
        <SalesTaxLineAdd>                                   <!-- opt, not in QBD|QBCA|QBUK|QBAU, v4.0 -->
          <!-- BEGIN OR: You may optionally have Amount OR RatePercent -->
          <Amount>AMTTYPE</Amount>
          <!-- OR -->
          <RatePercent>PERCENTTYPE</RatePercent>
          <!-- END OR -->
          <AccountRef>                                      <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 1000 for QBOE -->
          </AccountRef>
        </SalesTaxLineAdd>
        <ShippingLineAdd>                                   <!-- opt, not in QBD|QBCA|QBUK|QBAU, v4.0 -->
          <Amount>AMTTYPE</Amount>
          <AccountRef>                                      <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 1000 for QBOE -->
          </AccountRef>
        </ShippingLineAdd>
      </SalesReceiptAdd>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, not in QBOE, v4.0 -->
    </SalesReceiptAddRq>
		 */

		BigDecimal negative_one = new BigDecimal(-1);

		String requestId = getRequestId();

		xmlwriter.writeEntity("SalesReceiptAddRq");
		xmlwriter.writeAttribute("requestID", requestId);

		try
		{
			order.setRequestID(requestId);
			order.save();
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		xmlwriter.writeEntity("SalesReceiptAdd");

		/*
			<CustomerRef>                                       <!-- opt -->
				<ListID>IDTYPE</ListID>                           <!-- opt -->
				<FullName>STRTYPE</FullName>                      <!-- opt, max length = 209 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
			</CustomerRef>
		 */
		
		xmlwriter.writeEntity("CustomerRef");
		xmlwriter.writeEntity("ListID");

		if (client != null)
		{
			try
			{
				xmlwriter.writeText(client.getQBFSListID());
			}
			catch (IllegalValueException x)
			{
				x.printStackTrace();
			}
		}
		
		xmlwriter.endEntity();
		xmlwriter.endEntity();

		// <TxnDate>DATETYPE</TxnDate>

		xmlwriter.writeEntity("TxnDate");
		xmlwriter.writeText(dateTypeFormat.format(order.getOrderDate()));
		xmlwriter.endEntity();

		// <RefNumber>STRTYPE</RefNumber>

		xmlwriter.writeEntity("RefNumber");
		xmlwriter.writeText(order.getValue());
		xmlwriter.endEntity();
		
		this.setRefNumber(order.getValue());

		/*
        <BillAddress>                                       <!-- opt -->
          <Addr1>STRTYPE</Addr1>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr2>STRTYPE</Addr2>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr3>STRTYPE</Addr3>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr4>STRTYPE</Addr4>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE, v2.0 -->
          <Addr5>STRTYPE</Addr5>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
          <City>STRTYPE</City>                              <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <State>STRTYPE</State>                            <!-- opt, max length = 21 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <PostalCode>STRTYPE</PostalCode>                  <!-- opt, max length = 13 for QBD|QBCA|QBUK|QBAU, max length = 30 for QBOE -->
          <Country>STRTYPE</Country>                        <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <Note>STRTYPE</Note>                              <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
        </BillAddress>
		 */

		/*
		if (client != null)
		{
			try
			{
				AddressBean address = AddressBean.getAddress(client, AddressBean.PERSON_ADDRESS_TYPE);
				xmlwriter.writeEntity("BillAddress");
				xmlwriter.writeEntity("Addr1");
				xmlwriter.writeText(client.getLabel());
				xmlwriter.endEntity();
				xmlwriter.writeEntity("Addr2");
				xmlwriter.writeText(address.getStreet1String());
				xmlwriter.endEntity();
				if (address.getStreet2String().length() > 0)
				{
					xmlwriter.writeEntity("Addr3");
					xmlwriter.writeText(address.getStreet2String());
					xmlwriter.endEntity();
				}
				xmlwriter.writeEntity("City");
				xmlwriter.writeText(address.getCityString());
				xmlwriter.endEntity();
				xmlwriter.writeEntity("State");
				xmlwriter.writeText(address.getStateString());
				xmlwriter.endEntity();
				xmlwriter.writeEntity("PostalCode");
				xmlwriter.writeText(address.getZipCodeString());
				xmlwriter.endEntity();
				xmlwriter.endEntity();
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
		}
		*/

		// <IsPending>BOOLTYPE</IsPending>                     <!-- opt, not in QBOE -->

		xmlwriter.writeEntity("IsPending");
		xmlwriter.writeText("false");
		xmlwriter.endEntity();

		/*
        <CheckNumber>STRTYPE</CheckNumber>                  <!-- opt, max length = 25 -->
        <PaymentMethodRef>                                  <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 100 for QBOE -->
        </PaymentMethodRef>
		 */

		try
		{
			if (order.hasCheckTender())
			{
				String list_id = "";
				String name = "";
				try
				{
					ItemPaymentRet payment_type = ItemPaymentRet.getPaymentItem(order.getCompany(), (short)ItemPaymentRet.CHECK);
					PaymentMethodRet check_payment = PaymentMethodRet.getPaymentMethod(payment_type.getPaymentMethodId());
					list_id = check_payment.getListID();
					name = check_payment.getName();
				}
				catch (Exception x)
				{
					x.printStackTrace();
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), "Check Payment issue");
				}
				
				if (list_id.equals(""))
				{
					System.out.println("***Unable to determine list id for Check payment");
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Unable to determine list id", "SalesReceiptAdd: Check Payment issue");
				}
				
				TenderRet check_tender = order.getCheckTender();

				xmlwriter.writeEntity("CheckNumber");
				xmlwriter.writeText(check_tender.getCheckNumber() + "");
				xmlwriter.endEntity();

				xmlwriter.writeEntity("PaymentMethodRef");
				xmlwriter.writeEntity("ListID");
				xmlwriter.writeText(list_id);
				xmlwriter.endEntity();
				xmlwriter.writeEntity("FullName");
				xmlwriter.writeText(name); // should be "Check"
				xmlwriter.endEntity();
				xmlwriter.endEntity(); // </PaymentMethodRef>
				

			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		
		
		
		try
		{
			if (order.hasCashTender())
			{
				String list_id = "";
				String name = "";
				try
				{
					ItemPaymentRet payment_type = ItemPaymentRet.getPaymentItem(order.getCompany(), (short)ItemPaymentRet.CASH);
					PaymentMethodRet cash_payment = PaymentMethodRet.getPaymentMethod(payment_type.getPaymentMethodId());
					list_id = cash_payment.getListID();
					name = cash_payment.getName();
				}
				catch (Exception x)
				{
					x.printStackTrace();
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), "Cash Payment issue");
				}
				
				if (list_id.equals(""))
				{
					System.out.println("***Unable to determine list id for Cash payment");
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Unable to determine list id", "SalesReceiptAdd: Cash Payment issue");
				}
				
				TenderRet check_tender = order.getCheckTender();

				// what about the amount tendered??? // shrug

				xmlwriter.writeEntity("PaymentMethodRef");
				xmlwriter.writeEntity("ListID");
				xmlwriter.writeText(list_id);
				xmlwriter.endEntity();
				xmlwriter.writeEntity("FullName");
				xmlwriter.writeText(name); // should be "Cash"
				xmlwriter.endEntity();
				xmlwriter.endEntity(); // </PaymentMethodRef>
				

			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		
		
		

		/*
        <DueDate>DATETYPE</DueDate>                         <!-- opt, not in QBOE -->
        <SalesRepRef>                                       <!-- opt, not in QBOE -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 5 for QBD|QBCA|QBUK|QBAU -->
        </SalesRepRef>
        <ShipDate>DATETYPE</ShipDate>                       <!-- opt -->
		 */

		xmlwriter.writeEntity("DueDate");
		xmlwriter.writeText(dateTypeFormat.format(order.getOrderDate()));
		xmlwriter.endEntity();

		// include subtotal here

		/*
        <ItemSalesTaxRef>                                   <!-- opt, not in QBOE -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
        </ItemSalesTaxRef>
		 */
		
		String tax_code_list_id = "";
		String tax_code_name = "";
		try
		{
			ValeoTaxCodeBean tax_code_for_add_request = (ValeoTaxCodeBean)ValeoTaxCodeBean.getSalesTaxForAddRequest((UKOnlineCompanyBean)order.getCompany());
			tax_code_list_id = tax_code_for_add_request.getQBListID();
			tax_code_name = tax_code_for_add_request.getLabel();
		}
		catch (Exception x)
		{
			x.printStackTrace();
			CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), "SalesReceiptAdd: Tax Code issue");
		}
		
		if (tax_code_list_id.equals(""))
		{
			System.out.println("***Unable to determine list id for Tax Code");
			CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Unable to determine list id", "SalesReceiptAdd: Tax Code issue");
		}

		xmlwriter.writeEntity("ItemSalesTaxRef");
		xmlwriter.writeEntity("ListID");
		xmlwriter.writeText(tax_code_list_id);
		xmlwriter.endEntity();
		xmlwriter.writeEntity("FullName");
		xmlwriter.writeText(tax_code_name);
		xmlwriter.endEntity();
		xmlwriter.endEntity(); // </ItemSalesTaxRef>

		/*
        <IsToBePrinted>BOOLTYPE</IsToBePrinted>             <!-- opt -->
        <IsToBeEmailed>BOOLTYPE</IsToBeEmailed>             <!-- opt, not in QBOE, v6.0 -->
		 */

		xmlwriter.writeEntity("IsToBePrinted");
		xmlwriter.writeText("true");
		xmlwriter.endEntity();
		xmlwriter.writeEntity("IsToBeEmailed");
		xmlwriter.writeText("false");
		xmlwriter.endEntity();

		/*
        <DepositToAccountRef>                               <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
        </DepositToAccountRef>
		 */
		
		try
		{
			AccountRet undeposited_funds_account = AccountRet.getAccountByName(order.getCompany(), AccountRet.UNDEPOSITED_FUNDS_ACCOUNT_NAME);

			xmlwriter.writeEntity("DepositToAccountRef");
			xmlwriter.writeEntity("ListID");
			//xmlwriter.writeText("800000B5-1230928949");  // this is going to have to be changed to be dynamic
			xmlwriter.writeText(undeposited_funds_account.getListID());
			xmlwriter.endEntity();
			xmlwriter.writeEntity("FullName");
			//xmlwriter.writeText("Undeposited Funds");  // this is going to have to be changed to be dynamic
			xmlwriter.writeText(AccountRet.UNDEPOSITED_FUNDS_ACCOUNT_NAME);
			xmlwriter.endEntity();
			xmlwriter.endEntity(); // </DepositToAccountRef>
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		// add the purchased items

		HashMap<ValeoTaxCodeBean,Vector> tax_orderline_hash = new HashMap<ValeoTaxCodeBean,Vector>(3);
		Vector non_tax_orderlines = new Vector();

		try
		{
			Iterator orderline_itr = order.getOrdersVec().iterator();
			while (orderline_itr.hasNext())
			{
				CheckoutOrderline orderline = (CheckoutOrderline)orderline_itr.next();
				CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
				System.out.println("checkout_code >" + checkout_code.getLabel());
				if (checkout_code.isTaxable() && (orderline.getTax().compareTo(BigDecimal.ZERO) == 1))
				{
					ValeoTaxCodeBean tax_code = checkout_code.getTaxCode();
					System.out.println("tax_code >" + tax_code.getLabel());
					Vector orderlines_for_tax_code = tax_orderline_hash.get(tax_code);
					if (orderlines_for_tax_code == null)
					{
						orderlines_for_tax_code = new Vector();
						tax_orderline_hash.put(tax_code, orderlines_for_tax_code);
					}
					orderlines_for_tax_code.addElement(orderline);
				}
				else
					non_tax_orderlines.addElement(orderline);
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		// grab the keys and go

		Iterator key_itr = tax_orderline_hash.keySet().iterator();
		while (key_itr.hasNext())
		{
			ValeoTaxCodeBean key = (ValeoTaxCodeBean)key_itr.next();
			Vector orderlines_for_tax_code = tax_orderline_hash.get(key);
			BigDecimal subtotal = CUBean.zero;
			BigDecimal tax = CUBean.zero;

			try
			{
				Iterator itr = orderlines_for_tax_code.iterator();
				while (itr.hasNext())
				{
					CheckoutOrderline orderline = (CheckoutOrderline)itr.next();
					CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
					
					// does this checkout_code represent a group?
					
					if (checkout_code.getType() == CheckoutCodeBean.GROUP_TYPE)
					{
						/*
							<SalesReceiptLineGroupAdd>                          <!-- not in QBOE -->
							  <ItemGroupRef>
								<ListID>IDTYPE</ListID>                         <!-- opt -->
								<FullName>STRTYPE</FullName>                    <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
							  </ItemGroupRef>
							  <Desc>STRTYPE</Desc>                              <!-- opt, not in QBD|QBCA|QBUK|QBAU -->
							  <Quantity>QUANTYPE</Quantity>                     <!-- opt -->
							  <UnitOfMeasure>STRTYPE</UnitOfMeasure>            <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, v7.0 -->
							  <ServiceDate>DATETYPE</ServiceDate>               <!-- opt, not in QBD|QBCA|QBUK|QBAU -->
							  <DataExt>                                         <!-- opt, may rep, v5.0 -->
								<OwnerID>GUIDTYPE</OwnerID>
								<DataExtName>STRTYPE</DataExtName>              <!-- max length = 31 for QBD|QBCA|QBUK|QBAU -->
								<DataExtValue>STRTYPE</DataExtValue>
							  </DataExt>
							</SalesReceiptLineGroupAdd>
						*/
						
						xmlwriter.writeEntity("SalesReceiptLineGroupAdd");

						xmlwriter.writeEntity("ItemGroupRef");
						xmlwriter.writeEntity("ListID");
						xmlwriter.writeText(checkout_code.getQBListID());
						xmlwriter.endEntity();
						xmlwriter.endEntity(); // </ItemRef>

						xmlwriter.writeEntity("Quantity");
						xmlwriter.writeText(orderline.getQuantity().toString());
						xmlwriter.endEntity();
						
						xmlwriter.endEntity(); // </SalesReceiptLineGroupAdd>
						
					}
	 				else
					{

						xmlwriter.writeEntity("SalesReceiptLineAdd");

						/*
						  <ItemRef>                                         <!-- opt -->
							<ListID>IDTYPE</ListID>                         <!-- opt -->
							<FullName>STRTYPE</FullName>                    <!-- opt -->
						  </ItemRef>
						 */

						xmlwriter.writeEntity("ItemRef");
						xmlwriter.writeEntity("ListID");
						xmlwriter.writeText(checkout_code.getQBListID());
						xmlwriter.endEntity();
						//xmlwriter.writeEntity("FullName");
						//xmlwriter.writeText("");  // this is going to have to be changed to be dynamic
						//xmlwriter.endEntity();
						xmlwriter.endEntity(); // </ItemRef>

						// <Quantity>QUANTYPE</Quantity>                     <!-- opt -->

						xmlwriter.writeEntity("Quantity");
						xmlwriter.writeText(orderline.getQuantity().toString());
						xmlwriter.endEntity();

						/*
						  <Rate>PRICETYPE</Rate>
						  <Amount>AMTTYPE</Amount>                          <!-- opt -->
						 */

						xmlwriter.writeEntity("Rate");
						xmlwriter.writeText(orderline.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						xmlwriter.endEntity();

						xmlwriter.writeEntity("Amount");
						xmlwriter.writeText(orderline.getPrice().multiply(orderline.getQuantity()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						xmlwriter.endEntity();

						subtotal = subtotal.add(orderline.getActualAmount());

						/*
						  <TaxAmount>AMTTYPE</TaxAmount>                    <!-- opt, not in QBD|QBCA|QBUK|QBOE, v6.1 -->
						 */

						xmlwriter.writeEntity("TaxAmount");
						xmlwriter.writeText(orderline.getTax().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						xmlwriter.endEntity();

						tax = tax.add(orderline.getTax());

						/*
						  <SalesTaxCodeRef>                                 <!-- opt, not in QBOE -->
							<ListID>IDTYPE</ListID>                         <!-- opt -->
							<FullName>STRTYPE</FullName>                    <!-- opt, max length = 3 for QBD|QBCA|QBUK, max length = 6 for QBAU -->
						  </SalesTaxCodeRef>
						 */

						//if (checkout_code.isTaxable())
						//{
							// the QuickBooks sales tax code needs to either come from the item, or the sales tax "code" assigned to the item...
							// I'm thinking it makes more sense for it to come from the code

							try
							{
								/*
								System.out.println("GETTING TAX CODE FOR CHECKOUT CODE >" + checkout_code.getLabel());
								ValeoTaxCodeBean tax_code = checkout_code.getTaxCode();
								System.out.println("FOUND TAX CODE FOR CHECKOUT CODE >" + tax_code.getLabel());
								SalesTaxCodeRet qbfs_sales_tax_code = SalesTaxCodeRet.getSalesTaxCode(order.getCompany(), tax_code.getSalesTaxCodeListID());
								*/

								SalesTaxCodeRet qbfs_sales_tax_code= SalesTaxCodeRet.getSalesTaxCode(checkout_code.getSalesTaxCodeId());

								xmlwriter.writeEntity("SalesTaxCodeRef");
								xmlwriter.writeEntity("ListID");
								xmlwriter.writeText(qbfs_sales_tax_code.getListID());
								xmlwriter.endEntity();
								xmlwriter.writeEntity("FullName");
								xmlwriter.writeText(qbfs_sales_tax_code.getName());
								xmlwriter.endEntity();
								xmlwriter.endEntity(); // </SalesTaxCodeRef>
							}
							catch (ObjectNotFoundException x)
							{
								x.printStackTrace();
							}
						//}
							
						
						try
						{
							if (checkout_code.getIncomeAccountId() > 0)
							{
								AccountRet account_obj = AccountRet.getAccount(checkout_code.getIncomeAccountId());

							/*
							 * 
				  <OverrideItemAccountRef>                          <!-- opt, v2.0 -->
					<ListID>IDTYPE</ListID>                         <!-- opt -->
					<FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
				  </OverrideItemAccountRef>
							 */


								xmlwriter.writeEntity("OverrideItemAccountRef");
								xmlwriter.writeEntity("ListID");
								xmlwriter.writeText(account_obj.getListID());
								xmlwriter.endEntity();
								xmlwriter.writeEntity("FullName");
								xmlwriter.writeText(account_obj.getFullName());
								xmlwriter.endEntity();
								xmlwriter.endEntity(); // OverrideItemAccountRef
							}
						}
						catch (Exception x)
						{
							x.printStackTrace();
							CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Trouble with COGS account in QBXMLItemReceiptAddRequest");
						}
						

						xmlwriter.endEntity(); // </SalesReceiptLineAdd>
					
					}
				}

				// post the subtotal

				/*
					<SalesReceiptLineRet>
						<TxnLineID>3B175-1264638721</TxnLineID>
						<ItemRef>
							<ListID>80000012-1231210536</ListID>
							<FullName>POS Subtotal</FullName>
						</ItemRef>
						<Amount>200.00</Amount>
					</SalesReceiptLineRet>
				 */
				
							
				String list_id = "";
				String name = "";
				
				try
				{
					CheckoutCodeBean subtotal_code = CheckoutCodeBean.getSubtotalForAddRequest(order.getCompany());
					list_id = subtotal_code.getQBListID();
					name = subtotal_code.getLabel();
				}
				catch (Exception x)
				{
					System.out.println("***Unable to determine list id for Subtotal");
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Subtotal Issue", "SalesReceiptAdd: No subtotal codes found");
				}
				
				/*
				Vector subtotals = CheckoutCodeBean.getCheckoutCodes(order.getCompany(), CheckoutCodeBean.SUBTOTAL);
				if (subtotals.size() > 0)
				{
					CheckoutCodeBean subtotal_code = (CheckoutCodeBean)subtotals.get(0);
					list_id = subtotal_code.getQBListID();
					name = subtotal_code.getLabel();
				}
				else
				{
					System.out.println("***Unable to determine list id for Subtotal");
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Subtotal Issue", "SalesReceiptAdd: No subtotal codes found");
				}
				 * 
				 */

				xmlwriter.writeEntity("SalesReceiptLineAdd");
				xmlwriter.writeEntity("ItemRef");
				xmlwriter.writeEntity("ListID");
				xmlwriter.writeText(list_id);
				xmlwriter.endEntity();
				xmlwriter.writeEntity("FullName");
				xmlwriter.writeText(name);
				xmlwriter.endEntity();
				xmlwriter.endEntity();
				xmlwriter.endEntity(); // </SalesReceiptLineAdd>
				

				// post the tax

				/*
					<SalesReceiptLineRet>
						<TxnLineID>3B176-1264638721</TxnLineID>
						<ItemRef>
							<ListID>80000013-1231210541</ListID>
							<FullName>2.00%MinnesotaCare Tax</FullName>
						</ItemRef>
						<Desc>2.00% MinnesotaCare Tax for Minnesota Department of Revenue</Desc>
						<RatePercent>2.00</RatePercent>
						<Amount>4.00</Amount>
					</SalesReceiptLineRet>
				 */

				if (!key.isGroup())
				{
					xmlwriter.writeEntity("SalesReceiptLineAdd");
					xmlwriter.writeEntity("ItemRef");
					if (key.getQBListID() != null)
					{
						xmlwriter.writeEntity("ListID");
						xmlwriter.writeText(key.getQBListID());
						xmlwriter.endEntity();
					}
					xmlwriter.writeEntity("FullName");
					xmlwriter.writeText(key.getLabel());
					xmlwriter.endEntity();
					xmlwriter.endEntity(); // </ItemRef>
					/*
					xmlwriter.writeEntity("RatePercent");
					xmlwriter.writeText(key.getPercentage().setScale(3, BigDecimal.ROUND_HALF_UP).toString());
					xmlwriter.endEntity();
					 */
					xmlwriter.writeEntity("Amount");
					xmlwriter.writeText(tax.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					xmlwriter.endEntity();
					xmlwriter.endEntity(); // </SalesReceiptLineAdd>
				}
				else
				{
					/*
					xmlwriter.writeEntity("SalesReceiptLineGroupAdd");
					xmlwriter.writeEntity("ItemGroupRef");
					if (key.getQBListID() != null)
					{
						xmlwriter.writeEntity("ListID");
						xmlwriter.writeText(key.getQBListID()); // this is going to have to be changed to be dynamic
						xmlwriter.endEntity();
					}
					xmlwriter.writeEntity("FullName");
					xmlwriter.writeText(key.getLabel()); // this is going to have to be changed to be dynamic
					xmlwriter.endEntity();
					xmlwriter.endEntity(); // </ItemGroupRef>
					
					xmlwriter.endEntity(); // </SalesReceiptLineGroupAdd>
					
					*/
					
					MathContext mc = new MathContext(3, RoundingMode.HALF_UP);
					BigDecimal tax_amount_allocated = BigDecimal.ZERO;
					tax_amount_allocated = tax_amount_allocated.setScale(2, BigDecimal.ROUND_HALF_UP);
					
					Iterator child_itr = key.getChildren((UKOnlineCompanyBean)order.getCompany()).iterator();
					while (child_itr.hasNext())
					{
						ValeoTaxCodeBean child_tax_code = (ValeoTaxCodeBean)child_itr.next();
						
						BigDecimal child_tax_amount = BigDecimal.ZERO;
						if (child_itr.hasNext())
						{
							child_tax_amount = child_tax_code.getPercentage().multiply(tax, mc).divide(key.getPercentage(), mc);
							child_tax_amount = child_tax_amount.setScale(2, BigDecimal.ROUND_HALF_UP);
							tax_amount_allocated = tax_amount_allocated.add(child_tax_amount);
						}
						else
						{
							child_tax_amount = tax.subtract(tax_amount_allocated);
						}
						
						xmlwriter.writeEntity("SalesReceiptLineAdd");
						xmlwriter.writeEntity("ItemRef");
						if (key.getQBListID() != null)
						{
							xmlwriter.writeEntity("ListID");
							xmlwriter.writeText(child_tax_code.getQBListID());
							xmlwriter.endEntity();
						}
						xmlwriter.writeEntity("FullName");
						xmlwriter.writeText(child_tax_code.getLabel());
						xmlwriter.endEntity();
						xmlwriter.endEntity(); // </ItemRef>
						
						xmlwriter.writeEntity("Amount");
						xmlwriter.writeText(child_tax_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						xmlwriter.endEntity();
						xmlwriter.endEntity(); // </SalesReceiptLineAdd>
					}
				}

			}
			catch (Exception x)
			{
				x.printStackTrace();
			}

		}

		// post the non-tax items.  assuming that they need to be subtotaled also

		try
		{
			Iterator itr = non_tax_orderlines.iterator();
			if (itr.hasNext())
			{
				BigDecimal subtotal = CUBean.zero;

				while (itr.hasNext())
				{
					CheckoutOrderline orderline = (CheckoutOrderline)itr.next();
					CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
					
					// does this checkout_code represent a group?
					
					if (checkout_code.getType() == CheckoutCodeBean.GROUP_TYPE)
					{
						/*
							<SalesReceiptLineGroupAdd>                          <!-- not in QBOE -->
							  <ItemGroupRef>
								<ListID>IDTYPE</ListID>                         <!-- opt -->
								<FullName>STRTYPE</FullName>                    <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
							  </ItemGroupRef>
							  <Desc>STRTYPE</Desc>                              <!-- opt, not in QBD|QBCA|QBUK|QBAU -->
							  <Quantity>QUANTYPE</Quantity>                     <!-- opt -->
							  <UnitOfMeasure>STRTYPE</UnitOfMeasure>            <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, v7.0 -->
							  <ServiceDate>DATETYPE</ServiceDate>               <!-- opt, not in QBD|QBCA|QBUK|QBAU -->
							  <DataExt>                                         <!-- opt, may rep, v5.0 -->
								<OwnerID>GUIDTYPE</OwnerID>
								<DataExtName>STRTYPE</DataExtName>              <!-- max length = 31 for QBD|QBCA|QBUK|QBAU -->
								<DataExtValue>STRTYPE</DataExtValue>
							  </DataExt>
							</SalesReceiptLineGroupAdd>
						*/
						
						xmlwriter.writeEntity("SalesReceiptLineGroupAdd");

						xmlwriter.writeEntity("ItemGroupRef");
						xmlwriter.writeEntity("ListID");
						xmlwriter.writeText(checkout_code.getQBListID());
						xmlwriter.endEntity();
						xmlwriter.endEntity(); // </ItemRef>

						xmlwriter.writeEntity("Quantity");
						xmlwriter.writeText(orderline.getQuantity().toString());
						xmlwriter.endEntity();
						
						xmlwriter.endEntity(); // </SalesReceiptLineGroupAdd>
						
					}
					else
					{

						xmlwriter.writeEntity("SalesReceiptLineAdd");

						if (checkout_code.getType() == CheckoutCodeBean.GIFT_CARD)
						{
							// a gift card is being purchased
							// find the gift card checkout code

							String payment_list_id = "";
							String payment_full_name = "";
							Vector gift_cards = CheckoutCodeBean.getCheckoutCodes(order.getCompany(), CheckoutCodeBean.GIFT_CARD);
							if (gift_cards.size() > 0)
							{
								CheckoutCodeBean gift_card = (CheckoutCodeBean)gift_cards.get(0);
								payment_list_id = gift_card.getQBListID();
								payment_full_name = gift_card.getLabel();
							}

							xmlwriter.writeEntity("ItemRef");
							xmlwriter.writeEntity("ListID");
							//xmlwriter.writeText("8000000A-1231019994");
							xmlwriter.writeText(payment_list_id);
							xmlwriter.endEntity();
							xmlwriter.writeEntity("FullName");
							//xmlwriter.writeText("POS Gift Card");
							xmlwriter.writeText(payment_full_name);
							xmlwriter.endEntity();
							xmlwriter.endEntity(); // </ItemRef>
						}
						else if (checkout_code.getType() == CheckoutCodeBean.GIFT_CERTIFICATE)
						{
							// a gift certificate is being purchased

							String payment_list_id = "";
							String payment_full_name = "";
							Vector gift_certs = CheckoutCodeBean.getCheckoutCodes(order.getCompany(), CheckoutCodeBean.GIFT_CERTIFICATE);
							if (gift_certs.size() > 0)
							{
								CheckoutCodeBean gift_cert = (CheckoutCodeBean)gift_certs.get(0);
								payment_list_id = gift_cert.getQBListID();
								payment_full_name = gift_cert.getLabel();
							}

							xmlwriter.writeEntity("ItemRef");
							xmlwriter.writeEntity("ListID");
							//xmlwriter.writeText("800000F6-1232501123");
							xmlwriter.writeText(payment_list_id);
							xmlwriter.endEntity();
							xmlwriter.writeEntity("FullName");
							//xmlwriter.writeText("POS Gift Certificate");
							xmlwriter.writeText(payment_full_name);
							xmlwriter.endEntity();
							xmlwriter.endEntity(); // </ItemRef>
						}
						else
						{
							xmlwriter.writeEntity("ItemRef");
							xmlwriter.writeEntity("ListID");
							xmlwriter.writeText(checkout_code.getQBListID());
							xmlwriter.endEntity();
							//xmlwriter.writeEntity("FullName");
							//xmlwriter.writeText("");
							//xmlwriter.endEntity();
							xmlwriter.endEntity(); // </ItemRef>
						}

						xmlwriter.writeEntity("Quantity");
						xmlwriter.writeText(orderline.getQuantity().toString());
						xmlwriter.endEntity();

						xmlwriter.writeEntity("Rate");
						xmlwriter.writeText(orderline.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						xmlwriter.endEntity();

						/*
						if (checkout_code.getType() == CheckoutCodeBean.GIFT_CARD)
						{
							BigDecimal negative_one = new BigDecimal(-1);
							xmlwriter.writeEntity("Amount");
							xmlwriter.writeText(orderline.getActualAmount().multiply(negative_one).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
							xmlwriter.endEntity();

							// ummm...  why am I doing this???
						}
						else
						{
							xmlwriter.writeEntity("Amount");
							xmlwriter.writeText(orderline.getActualAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
							xmlwriter.endEntity();
						}
						 *
						 */

						xmlwriter.writeEntity("Amount");
						xmlwriter.writeText(orderline.getActualAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						xmlwriter.endEntity();


						subtotal = subtotal.add(orderline.getActualAmount());

						xmlwriter.writeEntity("TaxAmount");
						xmlwriter.writeText(orderline.getTax().setScale(2, BigDecimal.ROUND_HALF_UP).toString()); // should be zero...
						xmlwriter.endEntity();
						
						try
						{
							if (checkout_code.getIncomeAccountId() > 0)
							{
								AccountRet account_obj = AccountRet.getAccount(checkout_code.getIncomeAccountId());

							/*
							 * 
				  <OverrideItemAccountRef>                          <!-- opt, v2.0 -->
					<ListID>IDTYPE</ListID>                         <!-- opt -->
					<FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
				  </OverrideItemAccountRef>
							 */


								xmlwriter.writeEntity("OverrideItemAccountRef");
								xmlwriter.writeEntity("ListID");
								xmlwriter.writeText(account_obj.getListID());
								xmlwriter.endEntity();
								xmlwriter.writeEntity("FullName");
								xmlwriter.writeText(account_obj.getFullName());
								xmlwriter.endEntity();
								xmlwriter.endEntity(); // OverrideItemAccountRef
							}
						}
						catch (Exception x)
						{
							x.printStackTrace();
							CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Trouble with COGS account in QBXMLItemReceiptAddRequest");
						}

						xmlwriter.endEntity(); // </SalesReceiptLineAdd>
					
					}
				}

				// post the subtotal
							
				String list_id = "";
				String name = "";
				
				try
				{
					CheckoutCodeBean subtotal_code = CheckoutCodeBean.getSubtotalForAddRequest(order.getCompany());
					list_id = subtotal_code.getQBListID();
					name = subtotal_code.getLabel();
				}
				catch (Exception x)
				{
					System.out.println("***Unable to determine list id for Subtotal");
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Subtotal Issue", "SalesReceiptAdd: No subtotal codes found");
				}
				
				/*
				Vector subtotals = CheckoutCodeBean.getCheckoutCodes(order.getCompany(), CheckoutCodeBean.SUBTOTAL);
				if (subtotals.size() > 0)
				{
					CheckoutCodeBean subtotal_code = (CheckoutCodeBean)subtotals.get(0);
					list_id = subtotal_code.getQBListID();
					name = subtotal_code.getLabel();
				}
				else
				{
					System.out.println("***Unable to determine list id for Subtotal");
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Subtotal Issue", "SalesReceiptAdd: No subtotal codes found");
				}
				 * 
				 */

				xmlwriter.writeEntity("SalesReceiptLineAdd");
				xmlwriter.writeEntity("ItemRef");
				xmlwriter.writeEntity("ListID");
				//xmlwriter.writeText("80000012-1231210536");
				xmlwriter.writeText(list_id);
				xmlwriter.endEntity();
				xmlwriter.writeEntity("FullName");
				//xmlwriter.writeText("POS Subtotal");
				xmlwriter.writeText(name);
				xmlwriter.endEntity();
				xmlwriter.endEntity();
				/*
				xmlwriter.writeEntity("Amount");
				xmlwriter.writeText(subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				xmlwriter.endEntity();
				 */
				xmlwriter.endEntity(); // </SalesReceiptLineAdd>
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}


		
		// add the payment line items

		/*
			<SalesReceiptLineRet>
				<TxnLineID>3B177-1264638721</TxnLineID>
				<ItemRef>
					<ListID>8000000B-1231019996</ListID>
					<FullName>POS Visa Credit Card</FullName>
				</ItemRef>
				<Desc>Visa</Desc>
				<Rate>-252.63</Rate>
				<Amount>-252.63</Amount>
			</SalesReceiptLineRet>
		 */

		
		try
		{
			Iterator tender_itr = order.getTenders().iterator();
			while (tender_itr.hasNext())
			{
				TenderRet tender = (TenderRet)tender_itr.next();
				
				if (!tender.getType().equals(TenderRet.CHECK_TENDER_TYPE) &&
						!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE) ) // checks (and cash as of 3/12/19) are handled differently, above, I guess
				{

					// I'm going to have to create a wizard page or something where the user sets up payment types, requests payment
					// items from quickbooks or something, asks about credit cards that are accepted - that kind thing

					// for now I'm going to kludge it up...

					String payment_list_id = "";
					String payment_full_name = "";

					ItemPaymentRet payment_type = null;
					if (tender.getType().equals(TenderRet.CASH_TENDER_TYPE)) // 3/12/19 REMOVED ABOVE // 3/18/19 - added back in
					{
						// find the Payment Type designated for cash...
						
						payment_type = ItemPaymentRet.getPaymentItem(order.getCompany(), (short)ItemPaymentRet.CASH);
						
						//payment_list_id = "80000026-1231210603";
						//payment_full_name = "POS Cash";
					}
					else if (tender.getType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE))
					{
						String card_type = tender.getCreditCardType();

						System.out.println("card_type >" + card_type);
						System.out.println("CCUtils.VISA >" + CCUtils.getCardName(CCUtils.VISA));
						System.out.println("CCUtils.MASTERCARD >" + CCUtils.getCardName(CCUtils.MASTERCARD));
						System.out.println("CCUtils.DISCOVER >" + CCUtils.getCardName(CCUtils.DISCOVER));
						System.out.println("CCUtils.AMERICAN_EXPRESS >" + CCUtils.getCardName(CCUtils.AMERICAN_EXPRESS));

						//if (card_type.equals(CCUtils.getCardName(CCUtils.VISA)))
						if (CCUtils.isVisa(card_type))
						{
							System.out.println("A >");
							payment_type = ItemPaymentRet.getPaymentItem(order.getCompany(), (short)ItemPaymentRet.VISA);
							//payment_list_id = "8000000B-1231019996";
							//payment_full_name = "POS Visa Credit Card";
						}
						//else if (card_type.equals(CCUtils.getCardName(CCUtils.MASTERCARD)))
						else if (CCUtils.isMasterCard(card_type))
						{
							System.out.println("B >");
							payment_type = ItemPaymentRet.getPaymentItem(order.getCompany(), (short)ItemPaymentRet.MASTERCARD);
							//payment_list_id = "80000016-1231210549";
							//payment_full_name = "POS MasterCard Credit Card";
						}
						//else if (card_type.equals(CCUtils.getCardName(CCUtils.DISCOVER)))
						else if (CCUtils.isDiscover(card_type))
						{
							System.out.println("C >");
							payment_type = ItemPaymentRet.getPaymentItem(order.getCompany(), (short)ItemPaymentRet.DISCOVER);
							//payment_list_id = "8000002A-1231210621";
							//payment_full_name = "POS Discover Credit Card";
						}
						//else if (card_type.equals(CCUtils.getCardName(CCUtils.AMERICAN_EXPRESS)))
						else if (CCUtils.isAmericanExpress(card_type))
						{
							System.out.println("D >");
							payment_type = ItemPaymentRet.getPaymentItem(order.getCompany(), (short)ItemPaymentRet.AMERICAN_EXPRESS);
							//payment_list_id = "8000002A-1231210621";
							//payment_full_name = "POS Discover Credit Card";
						}
						else
						{
							System.out.println("Unable to resolve card type >" + card_type);
						}
					}
					else if (tender.getType().equals(TenderRet.GIFT_CARD_TENDER_TYPE))
					{
						// find the gift card checkout code
						
						Vector gift_cards = CheckoutCodeBean.getCheckoutCodes(order.getCompany(), CheckoutCodeBean.GIFT_CARD);
						if (gift_cards.size() > 0)
						{
							CheckoutCodeBean gift_card = (CheckoutCodeBean)gift_cards.get(0);
							payment_list_id = gift_card.getQBListID();
							payment_full_name = gift_card.getLabel();
						}
					}
					else if (tender.getType().equals(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE))
					{
						
						Vector gift_certs = CheckoutCodeBean.getCheckoutCodes(order.getCompany(), CheckoutCodeBean.GIFT_CERTIFICATE);
						if (gift_certs.size() > 0)
						{
							CheckoutCodeBean gift_cert = (CheckoutCodeBean)gift_certs.get(0);
							payment_list_id = gift_cert.getQBListID();
							payment_full_name = gift_cert.getLabel();
						}
					}
					
					if (payment_type != null)
					{
						payment_list_id = payment_type.getListID();
						payment_full_name = payment_type.getName();
					}

					if (payment_list_id.equals(""))
					{
						//throw new IllegalValueException("Unable to resolve QB payment item details for " + tender.getType());
						
						System.out.println("***Unable to resolve QB payment item details for " + tender.getType());
						CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Payment Resolution Issue", "Unable to resolve QB payment item details for " + tender.getType() + " in QBXMLSalesReceiptAddRequest");
					}

					xmlwriter.writeEntity("SalesReceiptLineAdd");
					xmlwriter.writeEntity("ItemRef");
					xmlwriter.writeEntity("ListID");
					xmlwriter.writeText(payment_list_id);
					xmlwriter.endEntity();
					xmlwriter.writeEntity("FullName");
					xmlwriter.writeText(payment_full_name);
					xmlwriter.endEntity();
					xmlwriter.endEntity();
					xmlwriter.writeEntity("Rate");
					xmlwriter.writeText(tender.getAmountBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					xmlwriter.endEntity();
					xmlwriter.writeEntity("Amount");

					// get the tender amount from the mapping...

					Criteria crit = new Criteria();
					crit.add(TenderRetDbOrderMappingPeer.TENDER_RET_DB_ID, tender.getId());
					crit.add(TenderRetDbOrderMappingPeer.ORDER_ID, order.getId());
					System.out.println("crit >" + crit.toString());
					List obj_list = TenderRetDbOrderMappingPeer.doSelect(crit);
					if (obj_list.size() == 1)
					{
						TenderRetDbOrderMapping mapping = (TenderRetDbOrderMapping)obj_list.get(0);
						BigDecimal tender_amount_applied_to_order = mapping.getTenderAmountApplied();

						if (tender_amount_applied_to_order == null)
							throw new IllegalValueException("null tender amount applied for tender >" + tender.getId() + " and order >" + order.getId());
						if (tender_amount_applied_to_order.compareTo(BigDecimal.ZERO) == 0)
							throw new IllegalValueException("zero tender amount applied for tender >" + tender.getId() + " and order >" + order.getId());

						System.out.println("found tender >" + tender.getId() + " amount to apply to order >" + order.getId() + " >" + tender_amount_applied_to_order.toString());

						if (tender.getType().equals(TenderRet.GIFT_CARD_TENDER_TYPE) || tender.getType().equals(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE))
							xmlwriter.writeText(tender_amount_applied_to_order.multiply(negative_one).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						else
							xmlwriter.writeText(tender_amount_applied_to_order.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					}
					else
						throw new IllegalValueException("Unable to find mapping for tender >" + tender.getId() + " and order >" + order.getId());


					xmlwriter.endEntity();
					xmlwriter.endEntity(); // </SalesReceiptLineAdd>
				}
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}


		xmlwriter.endEntity(); // </SalesReceiptAdd>
		xmlwriter.endEntity(); // </SalesReceiptAddRq>
	}

	@Override
	public String getLabel() {
		//System.out.println("QBXMLSalesReceiptAddRequest >" + this.toXMLString());
		return CUBean.getUserTimeString(super.creation_date) + " - Sales Receipt Add Request (" + client.getLabel() + " " + order.getTotalString() + ")";
	}

	@Override
	public String getRequestType() {
		return "SalesReceiptAddRq";
	}

	@Override
	public void customerAdded(QBCustomerAddEvent _event) {
		//UKOnlinePersonBean event_source = (UKOnlinePersonBean)_event.getSource();
		
		System.out.println("customerAdded() invoked in QBXMLSalesReceiptAddRequest");
		
		try
		{
			writer = new java.io.StringWriter();
			xmlwriter = new XmlWriter(writer);

			xmlwriter.writeEntity("QBXML");
			xmlwriter.writeEntity("QBXMLMsgsRq");
			xmlwriter.writeAttribute("onError", "stopOnError");

			assembleRequestBody();

			xmlwriter.endEntity();
			xmlwriter.endEntity();
			xmlwriter.close();

			xml_str = writer.toString();
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		
		System.out.println("regenerated xml_str >" + xml_str);
		
	}

}