/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;

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
import com.valeo.qbpos.QBPOSXMLRequest;
import com.valeo.qbpos.data.TenderRet;
import com.valonyx.events.QBCustomerAddEvent;
import com.valonyx.events.QBCustomerAddEventListener;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.generationjava.io.xml.XmlWriter;

/**
 *
 * @author marlo
 */
public class QBXMLReceivePaymentAddRequest
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

			qb_request = QBXMLReceivePaymentAddRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLReceivePaymentAddRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private UKOnlineCompanyBean company;
	
	private UKOnlinePersonBean client;
	private ValeoOrderBean order;
	private TenderRet tender;
	private BigDecimal payment_amount;
	
	// CONSTRUCTORS

    public
    QBXMLReceivePaymentAddRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLReceivePaymentAddRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

	public void
	setCompany(UKOnlineCompanyBean _company) throws TorqueException
	{
		super.setCompany(_company);
		company = _company;
	}

	public void
	setOrder(ValeoOrderBean _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		order = _order;
		client = _order.getClient();
	}

	public void
	setTender(TenderRet _tender)
	{
		tender = _tender;
	}

	public void
	setPaymentAmount(BigDecimal _payment_amount)
	{
		payment_amount = _payment_amount;
	}

	@Override
	protected void assembleRequestBody() throws WritingException {

		/*
    <ReceivePaymentAddRq requestID = "UUIDTYPE">            <!-- v1.1 -->
      <!-- ReceivePaymentAdd contains 1 optional attribute: 'defMacro' -->
      <ReceivePaymentAdd defMacro = "MACROTYPE">
        <CustomerRef>
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 209 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
        </CustomerRef>
        <ARAccountRef>                                      <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
        </ARAccountRef>
        <TxnDate>DATETYPE</TxnDate>                         <!-- opt -->
        <RefNumber>STRTYPE</RefNumber>                      <!-- opt, max length = 20 for QBD|QBCA|QBUK|QBAU -->
        <TotalAmount>AMTTYPE</TotalAmount>                  <!-- opt -->
        <ExchangeRate>FLOATTYPE</ExchangeRate>              <!-- opt, not in QBOE, v8.0 -->
        <PaymentMethodRef>                                  <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 100 for QBOE -->
        </PaymentMethodRef>
        <Memo>STRTYPE</Memo>                                <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
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
        <!-- BEGIN OR: You may have IsAutoApply OR AppliedToTxnAdd -->
        <IsAutoApply>BOOLTYPE</IsAutoApply>                 <!-- not in QBOE -->
        <!-- OR -->
        <AppliedToTxnAdd>                                   <!-- rep (1 or more) -->
          <TxnID>IDTYPE</TxnID>                             <!-- may be macro value -->
          <PaymentAmount>AMTTYPE</PaymentAmount>            <!-- opt -->
          <TxnLineDetail>                                   <!-- opt, may rep, not in QBD|QBCA|QBUK|QBAU -->
            <TxnLineID>IDTYPE</TxnLineID>
            <Amount>AMTTYPE</Amount>
          </TxnLineDetail>
          <SetCredit>                                       <!-- opt, may rep -->
            <CreditTxnID>IDTYPE</CreditTxnID>               <!-- may be macro value -->
            <TxnLineID>IDTYPE</TxnLineID>                   <!-- opt, not in QBD|QBCA|QBUK|QBAU -->
            <AppliedAmount>AMTTYPE</AppliedAmount>
          </SetCredit>
          <DiscountAmount>AMTTYPE</DiscountAmount>          <!-- opt -->
          <DiscountAccountRef>                              <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
          </DiscountAccountRef>
        </AppliedToTxnAdd>
        <!-- END OR -->
      </ReceivePaymentAdd>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, not in QBOE, v4.0 -->
    </ReceivePaymentAddRq>
		 */


		try
		{

			String requestId = getRequestId();

			xmlwriter.writeEntity("ReceivePaymentAddRq");
			xmlwriter.writeAttribute("requestID", requestId);

			xmlwriter.writeEntity("ReceivePaymentAdd");

			/*
				<CustomerRef>                                       <!-- opt -->
					<ListID>IDTYPE</ListID>                           <!-- opt -->
					<FullName>STRTYPE</FullName>                      <!-- opt, max length = 209 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
				</CustomerRef>
			 */
			
			if (client == null)
			{
				client = tender.getClient();
			}

			if (client != null)
			{
				try
				{
					xmlwriter.writeEntity("CustomerRef");
					xmlwriter.writeEntity("ListID");
					xmlwriter.writeText(client.getQBFSListID());
					xmlwriter.endEntity();
					xmlwriter.endEntity();
				}
				catch (IllegalValueException x)
				{
					x.printStackTrace();
					client.addQBCustomerAddEventListener(this);
				}
			}

			/*
			<ARAccountRef>                                      <!-- opt -->
			  <ListID>IDTYPE</ListID>                           <!-- opt -->
			  <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
			</ARAccountRef>
			 */
			
			AccountRet accounts_receivable_account = AccountRet.getAccountByName(company, AccountRet.ACCOUNTS_RECEIVABLE_ACCOUNT_NAME);
			
			xmlwriter.writeEntity("ARAccountRef");
			xmlwriter.writeEntity("ListID");
			//xmlwriter.writeText("80000031-1227017045"); // this is going to have to be changed to be dynamic
			xmlwriter.writeText(accounts_receivable_account.getListID());
			xmlwriter.endEntity();
			xmlwriter.writeEntity("FullName");
			//xmlwriter.writeText("Accounts Receivable");
			xmlwriter.writeText(AccountRet.ACCOUNTS_RECEIVABLE_ACCOUNT_NAME);
			xmlwriter.endEntity();
			xmlwriter.endEntity(); // </ARAccountRef>

			// <TxnDate>DATETYPE</TxnDate>

			xmlwriter.writeEntity("TxnDate");
			xmlwriter.writeText(dateTypeFormat.format(new Date()));
			xmlwriter.endEntity();

			// <RefNumber>STRTYPE</RefNumber>

			xmlwriter.writeEntity("RefNumber");
			xmlwriter.writeText(tender.getValue());
			xmlwriter.endEntity();
		
			this.setRefNumber(tender.getValue());

			// <TotalAmount>AMTTYPE</TotalAmount>                  <!-- opt -->

			// get the amount from the mapping of the tender/order
			
			System.out.println("order >" + order);
			
			if (order == null)
			{
				xmlwriter.writeEntity("TotalAmount");
				xmlwriter.writeText(payment_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				xmlwriter.endEntity();
			}
			else
			{
				Criteria crit = new Criteria();
				crit.add(TenderRetDbOrderMappingPeer.TENDER_RET_DB_ID, tender.getId());
				crit.add(TenderRetDbOrderMappingPeer.ORDER_ID, order.getId());
				System.out.println("QBXMLReceivePaymentAddRequest assembleRequestBody crit >" + crit.toString());
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

					xmlwriter.writeEntity("TotalAmount");
					xmlwriter.writeText(tender_amount_applied_to_order.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					xmlwriter.endEntity();

					try
					{
						mapping.setRequestID(requestId); // move this down to the mapping???
						mapping.save();
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
					
				}
				else
					throw new IllegalValueException("Unable to find mapping for tender >" + tender.getId() + " and order >" + order.getId());
			}

			/*
			<PaymentMethodRef>                                  <!-- opt -->
			  <ListID>IDTYPE</ListID>                           <!-- opt -->
			  <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 100 for QBOE -->
			</PaymentMethodRef>
			 */

			// query PaymentMethodRef to make all of these dynamic
			
			AccountRet undeposited_funds_account = AccountRet.getAccountByName(company, AccountRet.UNDEPOSITED_FUNDS_ACCOUNT_NAME);
			
			//String deposit_to_account_list_id = "800000B5-1230928949"; // dynamic!
			//String deposit_to_account_full_name = "Undeposited Funds"; // dynamic!
			String deposit_to_account_list_id = undeposited_funds_account.getListID();
			String deposit_to_account_full_name = AccountRet.UNDEPOSITED_FUNDS_ACCOUNT_NAME;

			String payment_list_id = ""; // dynamic!
			String payment_full_name = ""; // dynamic!
			
			ItemPaymentRet payment_type = null;
			PaymentMethodRet payment_method = null;

			if (tender.getType().equals(TenderRet.CASH_TENDER_TYPE))
			{
			    //payment_list_id = "80000001-1227016684";
			    //payment_full_name = "Cash";

			    payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.CASH);

			    AccountRet cash_in_drawer = AccountRet.getAccountByName(company, AccountRet.CASH_IN_DRAWER_ACCOUNT_NAME);
			    //deposit_to_account_list_id = "800000B9-1231019096"; // dynamic!
			    //deposit_to_account_full_name = "Cash in Drawer"; // dynamic!
			    deposit_to_account_list_id = cash_in_drawer.getListID();
			    deposit_to_account_full_name = AccountRet.CASH_IN_DRAWER_ACCOUNT_NAME;
			}
			else if (tender.getType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE))
			{
			    String card_type = tender.getCreditCardType();

			    System.out.println("card_type >" + card_type);
			    System.out.println("CCUtils.VISA >" + CCUtils.getCardName(CCUtils.VISA));
			    System.out.println("CCUtils.MASTERCARD >" + CCUtils.getCardName(CCUtils.MASTERCARD));
			    System.out.println("CCUtils.DISCOVER >" + CCUtils.getCardName(CCUtils.DISCOVER));

			    System.out.println("CCUtils.VISA(1) >" + CCUtils.getCardName(1));
			    System.out.println("CCUtils.MASTERCARD(2) >" + CCUtils.getCardName(2));
			    System.out.println("CCUtils.DISCOVER(3) >" + CCUtils.getCardName(3));

			    /*
			    if (card_type.equals(CCUtils.getCardName(CCUtils.VISA)))
			    {
				    payment_list_id = "80000006-1227016707";
				    payment_full_name = "Visa";
			    }
			    else if (card_type.equals(CCUtils.getCardName(CCUtils.MASTERCARD)))
			    {
				    payment_list_id = "80000005-1227016707";
				    payment_full_name = "MasterCard";
			    }
			    else if (card_type.equals(CCUtils.getCardName(CCUtils.DISCOVER)))
			    {
				    payment_list_id = "80000004-1227016707";
				    payment_full_name = "Discover";
			    }
			    else
			    {
				    System.out.println("Unable to resolve card type >" + card_type);
			    }
			     * 
			     */

				/*
			    if (card_type.equals(CCUtils.getCardName(CCUtils.VISA)))
					payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.VISA);
			    else if (card_type.equals(CCUtils.getCardName(CCUtils.MASTERCARD)))
					payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.MASTERCARD);
			    else if (card_type.equals(CCUtils.getCardName(CCUtils.DISCOVER)))
					payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.DISCOVER);
			    else if (card_type.equals(CCUtils.getCardName(CCUtils.AMERICAN_EXPRESS)))
					payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.AMERICAN_EXPRESS);
			    else
					System.out.println("Unable to resolve card type >" + card_type);
				 * 
				 */
				
				if (CCUtils.isVisa(card_type))
					payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.VISA);
			    else if (CCUtils.isMasterCard(card_type))
					payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.MASTERCARD);
			    else if (CCUtils.isDiscover(card_type))
					payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.DISCOVER);
			    else if (CCUtils.isAmericanExpress(card_type))
					payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.AMERICAN_EXPRESS);
			    else
					System.out.println("Unable to resolve card type >" + card_type);
			}
			else if (tender.getType().equals(TenderRet.CHECK_TENDER_TYPE))
			{
			    //payment_list_id = "80000002-1227016684";
			    //payment_full_name = "Check";
				
				payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.CHECK);
				//list_id = check_payment.getListID();
				//name = check_payment.getName();
			}
			else if (tender.getType().equals(TenderRet.GIFT_CARD_TENDER_TYPE))
			{
			    //payment_list_id = "80000008-1227016707";
			    //payment_full_name = "Gift Card";
				
				payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.GIFT_CARD);
			}
			else if (tender.getType().equals(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE))
			{
				//payment_list_id = "800000F6-1232501123";
				//payment_full_name = "POS Gift Certificate";
				
				payment_type = ItemPaymentRet.getPaymentItem(company, (short)ItemPaymentRet.GIFT_CERT);
			}
			
			if (payment_type != null)
			{
				payment_method = PaymentMethodRet.getPaymentMethod(payment_type.getPaymentMethodId());
			}
			
			if (payment_method != null)
			{
			    payment_list_id = payment_method.getListID();
			    payment_full_name = payment_method.getName();
			}

			if (payment_list_id.equals(""))
			{
			    //throw new IllegalValueException("Unable to resolve QB payment item details for " + tender.getType());

			    System.out.println("***Unable to resolve QB payment item details for " + tender.getType());
			    CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Payment Resolution Issue", "Unable to resolve QB payment item details for " + tender.getType() + " in QBXMLSalesReceiptAddRequest");
			}

			xmlwriter.writeEntity("PaymentMethodRef");
			xmlwriter.writeEntity("ListID");
			xmlwriter.writeText(payment_list_id); // this is going to have to be changed to be dynamic
			xmlwriter.endEntity();
			xmlwriter.writeEntity("FullName");
			xmlwriter.writeText(payment_full_name);
			xmlwriter.endEntity();
			xmlwriter.endEntity(); // </PaymentMethodRef>

			// <Memo>STRTYPE</Memo>                                <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->

			if (order != null)
			{
			    xmlwriter.writeEntity("Memo");
			    xmlwriter.writeText("Order #" + order.getValue());
			    xmlwriter.endEntity();
			}

			/*
			<DepositToAccountRef>                               <!-- opt -->
			  <ListID>IDTYPE</ListID>                           <!-- opt -->
			  <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
			</DepositToAccountRef>
			 */
			
			xmlwriter.writeEntity("DepositToAccountRef");
			xmlwriter.writeEntity("ListID");
			xmlwriter.writeText(deposit_to_account_list_id);  // this is going to have to be changed to be dynamic
			xmlwriter.endEntity();
			xmlwriter.writeEntity("FullName");
			xmlwriter.writeText(deposit_to_account_full_name);  // this is going to have to be changed to be dynamic
			xmlwriter.endEntity();
			xmlwriter.endEntity(); // </DepositToAccountRef>

			// <IsAutoApply>BOOLTYPE</IsAutoApply>                 <!-- not in QBOE -->

			xmlwriter.writeEntity("IsAutoApply");
			xmlwriter.writeText("1");
			xmlwriter.endEntity();

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}





		xmlwriter.endEntity();
		xmlwriter.endEntity();
	}

	@Override
	public String getLabel() {
		
		try
		{
			if (client == null)
				client = tender.getClient();

			if (tender != null)
			{
				if (order == null)
					return CUBean.getUserTimeString(super.creation_date) + " - Receive Payment Add Request (" + client.getLabel() + " " + payment_amount.toString() + ")";
				return CUBean.getUserTimeString(super.creation_date) + " - Receive Payment Add Request (" + client.getLabel() + " " + tender.getAmountString() + ")";
			}
			
			return CUBean.getUserTimeString(super.creation_date) + " - Receive Payment Add Request (" + client.getLabel() + ")";
		}
		catch (Exception x)
		{
			x.printStackTrace();
			return CUBean.getUserTimeString(super.creation_date) + " - Receive Payment Add Request (" + x.getMessage() + ")";
		}
	}

	@Override
	public String getRequestType() {
		return "ReceivePaymentAddRq";
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
