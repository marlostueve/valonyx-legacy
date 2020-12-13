/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbms.data;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.intuit.qbmsconnector.response.*;
import com.valeo.authorize.net.GatewayResponseParser;
import com.valeo.qbpos.data.TenderRet;
import java.math.BigDecimal;


/**
 *
 * @author marlo
 */
public class
QBMSCreditCardResponse
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,QBMSCreditCardResponse> hash = new HashMap<Integer,QBMSCreditCardResponse>(11);

    public static final short AUTHORIZE_RESPONSE_TYPE = 1;
    public static final short CAPTURE_RESPONSE_TYPE = 2;
    public static final short CHARGE_RESPONSE_TYPE = 3;
    public static final short REFUND_RESPONSE_TYPE = 4;
    public static final short VOICE_AUTHORIZATION_RESPONSE_TYPE = 5;
    public static final short VOID_TRANSACTION_RESPONSE_TYPE = 6;

    // CLASS METHODS

    public static QBMSCreditCardResponse
    getResponse(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		QBMSCreditCardResponse response = (QBMSCreditCardResponse)hash.get(key);
		if (response == null)
		{
			Criteria crit = new Criteria();
			crit.add(ChargeResponseDbPeer.CHARGE_RESPONSE_DB_ID, _id);
			List objList = ChargeResponseDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Charge Response with id: " + _id);

			response = QBMSCreditCardResponse.getResponse((ChargeResponseDb)objList.get(0));
		}

		return response;
    }

    public static QBMSCreditCardResponse
    getResponse(TenderRet _tender)
		throws TorqueException, ObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.TENDER_RET_DB_ID, _tender.getId());
		List objList = ChargeResponseDbPeer.doSelect(crit);
		if (objList.size() != 1)
			throw new ObjectNotFoundException("Could not locate Charge Response for tender: " + _tender.getId());

		return QBMSCreditCardResponse.getResponse((ChargeResponseDb)objList.get(0));
    }

    private static QBMSCreditCardResponse
    getResponse(ChargeResponseDb _response)
		throws TorqueException
    {
		Integer key = new Integer(_response.getChargeResponseDbId());
		QBMSCreditCardResponse response = (QBMSCreditCardResponse)hash.get(key);
		if (response == null)
		{
			response = new QBMSCreditCardResponse(_response);
			hash.put(key, response);
		}

		return response;
    }

    public static Vector
    getResponses(UKOnlinePersonBean _client)
		throws TorqueException, ObjectNotFoundException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.CLIENT_ID, _client.getId());
		List objList = ChargeResponseDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			Iterator itr = objList.iterator();
			while (itr.hasNext())
				vec.addElement(QBMSCreditCardResponse.getResponse((ChargeResponseDb)itr.next()));
		}
		else
			throw new ObjectNotFoundException("Could not locate Charge Response for " + _client.getLabel());

		return vec;
    }

    public static Vector
    getResponses(CompanyBean _company, short _type)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.COMPANY_ID, _company.getId());
		crit.add(ChargeResponseDbPeer.RESPONSE_TYPE, _type);
		Vector vec = new Vector();
		List objList = ChargeResponseDbPeer.doSelect(crit);
		Iterator itr = objList.iterator();
		while (itr.hasNext())
			vec.addElement(QBMSCreditCardResponse.getResponse((ChargeResponseDb)itr.next()));
		
		return vec;
    }

    public static Vector
    getResponses(TenderRet _tender)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.TENDER_RET_DB_ID, _tender.getId());
		List objList = ChargeResponseDbPeer.doSelect(crit);
		Iterator itr = objList.iterator();
		Vector vec = new Vector();
		if (itr.hasNext()) {
			
			QBMSCreditCardResponse obj = QBMSCreditCardResponse.getResponse((ChargeResponseDb)itr.next());
			
			crit = new Criteria();
			crit.add(ChargeResponseDbPeer.COMPANY_ID, _tender.getCompanyId());
			crit.add(ChargeResponseDbPeer.CREDIT_CARD_TRANS_ID, obj.getCreditCardTransId());
			objList = ChargeResponseDbPeer.doSelect(crit);
			itr = objList.iterator();
			while (itr.hasNext())
				vec.addElement(QBMSCreditCardResponse.getResponse((ChargeResponseDb)itr.next()));
			
			if (obj.getCreditCardRefTransId() != null)
			{
				crit = new Criteria();
				crit.add(ChargeResponseDbPeer.COMPANY_ID, _tender.getCompanyId());
				crit.add(ChargeResponseDbPeer.CREDIT_CARD_REF_TRANS_ID, obj.getCreditCardRefTransId());
				objList = ChargeResponseDbPeer.doSelect(crit);
				itr = objList.iterator();
				while (itr.hasNext())
					vec.addElement(QBMSCreditCardResponse.getResponse((ChargeResponseDb)itr.next()));
			}
		}
		
		return vec;
    }

    public static Vector
    getUncapturedAndUnvoidedAndUnrefundedResponses(CompanyBean _company)
		throws TorqueException
    {
		HashMap captured_response_hash = new HashMap();
		HashMap voided_response_hash = new HashMap();
		HashMap refund_response_hash = new HashMap();
		
		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.COMPANY_ID, _company.getId());
		//crit.add(ChargeResponseDbPeer.RESPONSE_TYPE, _type);
		
		//crit.add(ChargeResponseDbPeer.RESPONSE_TYPE, QBMSCreditCardResponse.CAPTURE_RESPONSE_TYPE);
		//crit.or(ChargeResponseDbPeer.RESPONSE_TYPE, QBMSCreditCardResponse.VOID_TRANSACTION_RESPONSE_TYPE);
		
		System.out.println("getUncapturedAndUnvoidedResponses() crit >" + crit.toString());
		
		List objList = ChargeResponseDbPeer.doSelect(crit);
		Iterator itr = objList.iterator();
		while (itr.hasNext())
		{
			QBMSCreditCardResponse response_obj = QBMSCreditCardResponse.getResponse((ChargeResponseDb)itr.next());
			if (response_obj.getResponseType() == QBMSCreditCardResponse.CAPTURE_RESPONSE_TYPE)
				captured_response_hash.put(response_obj.getCreditCardTransId(), response_obj);
			else if (response_obj.getResponseType() == QBMSCreditCardResponse.REFUND_RESPONSE_TYPE)
				refund_response_hash.put(response_obj.getCreditCardTransId(), response_obj);
			else if (response_obj.getResponseType() == QBMSCreditCardResponse.VOID_TRANSACTION_RESPONSE_TYPE)
				voided_response_hash.put(response_obj.getCreditCardTransId(), response_obj);
		}
		
		Vector vec = new Vector();
		
		itr = objList.iterator();
		while (itr.hasNext())
		{
			QBMSCreditCardResponse response_obj = QBMSCreditCardResponse.getResponse((ChargeResponseDb)itr.next());
			if (response_obj.getResponseType() == QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE)
			{
				if ((captured_response_hash.get(response_obj.getCreditCardTransId()) == null) &&
						(voided_response_hash.get(response_obj.getCreditCardTransId()) == null) &&
						(refund_response_hash.get(response_obj.getCreditCardTransId()) == null))
				{
					vec.addElement(response_obj);
				}
			}
		}
		
		return vec;
    }

    public static Vector
    getResponses(CompanyBean _company, Date _start_date, Date _end_date, short _type)
		throws TorqueException
    {
		Calendar start_date = Calendar.getInstance();
		start_date.setTime(_start_date);
		start_date.set(Calendar.HOUR_OF_DAY, 0);
		start_date.set(Calendar.MINUTE, 0);
		start_date.set(Calendar.SECOND, 0);

		Calendar end_date = Calendar.getInstance();
		end_date.setTime(_end_date);
		end_date.set(Calendar.HOUR_OF_DAY, 23);
		end_date.set(Calendar.MINUTE, 59);
		end_date.set(Calendar.SECOND, 59);

		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.COMPANY_ID, _company.getId());
		crit.add(ChargeResponseDbPeer.RESPONSE_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(ChargeResponseDbPeer.RESPONSE_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.add(ChargeResponseDbPeer.RESPONSE_TYPE, _type);

		Vector vec = new Vector();
		List objList = ChargeResponseDbPeer.doSelect(crit);
		Iterator itr = objList.iterator();
		while (itr.hasNext())
			vec.addElement(QBMSCreditCardResponse.getResponse((ChargeResponseDb)itr.next()));
		
		return vec;
    }

	public static boolean
	hasBeenCaptured(QBMSCreditCardResponse _auth_response)
		throws TorqueException
	{
		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.CREDIT_CARD_TRANS_ID, _auth_response.getCreditCardTransId());
		crit.add(ChargeResponseDbPeer.RESPONSE_TYPE, QBMSCreditCardResponse.CAPTURE_RESPONSE_TYPE);
		List objList = ChargeResponseDbPeer.doSelect(crit);
		return (objList.size() == 1);
	}

	public static boolean
	hasBeenVoided(QBMSCreditCardResponse _auth_response)
		throws TorqueException
	{
		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.CREDIT_CARD_TRANS_ID, _auth_response.getCreditCardTransId());
		crit.add(ChargeResponseDbPeer.RESPONSE_TYPE, QBMSCreditCardResponse.VOID_TRANSACTION_RESPONSE_TYPE);
		List objList = ChargeResponseDbPeer.doSelect(crit);
		return (objList.size() == 1);
	}

	public static boolean
	hasBeenCapturedOrVoided(QBMSCreditCardResponse _auth_response)
		throws TorqueException
	{
		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.CREDIT_CARD_TRANS_ID, _auth_response.getCreditCardTransId());
		crit.add(ChargeResponseDbPeer.RESPONSE_TYPE, QBMSCreditCardResponse.CAPTURE_RESPONSE_TYPE);
		crit.or(ChargeResponseDbPeer.RESPONSE_TYPE, QBMSCreditCardResponse.VOID_TRANSACTION_RESPONSE_TYPE);
		List objList = ChargeResponseDbPeer.doSelect(crit);
		return (objList.size() == 1);
	}

	public static QBMSCreditCardResponse
	getResponseToRefund(UKOnlinePersonBean _client, BigDecimal _refund_amount)
		throws TorqueException, ObjectNotFoundException
	{
		HashMap auth_hash = new HashMap(3);
		HashMap captured_hash = new HashMap(3);
		HashMap void_hash = new HashMap(3);
		HashMap refund_hash = new HashMap(3);

		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.CLIENT_ID, _client.getId());
		crit.addDescendingOrderByColumn(ChargeResponseDbPeer.RESPONSE_DATE); // there should be a bias towards refunding newer stuff
		System.out.println("REFUND CRIT >" + crit.toString());
		Iterator obj_itr = ChargeResponseDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			QBMSCreditCardResponse response_obj = QBMSCreditCardResponse.getResponse((ChargeResponseDb)obj_itr.next());
			if (response_obj.getResponseType() == QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE)
				auth_hash.put(response_obj.getCreditCardTransId(), response_obj);
			else if(response_obj.getResponseType() == QBMSCreditCardResponse.CAPTURE_RESPONSE_TYPE)
				captured_hash.put(response_obj.getCreditCardTransId(), response_obj);
			else if(response_obj.getResponseType() == QBMSCreditCardResponse.VOID_TRANSACTION_RESPONSE_TYPE)
				void_hash.put(response_obj.getCreditCardTransId(), response_obj);
			else if(response_obj.getResponseType() == QBMSCreditCardResponse.REFUND_RESPONSE_TYPE)
				refund_hash.put(response_obj.getCreditCardTransId(), response_obj);
		}

		// find exact amount first

		Iterator key_itr = auth_hash.keySet().iterator();
		while (key_itr.hasNext())
		{
			String credit_card_trans_id = (String)key_itr.next();
			QBMSCreditCardResponse response = (QBMSCreditCardResponse)auth_hash.get(credit_card_trans_id);
			System.out.println("response.getChargeAmount() >" + response.getChargeAmount());
			System.out.println("response.getChargeAmount().compareTo(_refund_amount) >" + response.getChargeAmount().compareTo(_refund_amount));
			if (response.getChargeAmount().compareTo(_refund_amount) == 0)
			{
				// it must have been captured, but should not be voided or already refunded...
				if (captured_hash.containsKey(credit_card_trans_id))
				{
					if (!void_hash.containsKey(credit_card_trans_id) && !refund_hash.containsKey(credit_card_trans_id))
						return response;
				}
			}
		}

		// find any greater amount

		key_itr = auth_hash.keySet().iterator();
		while (key_itr.hasNext())
		{
			String credit_card_trans_id = (String)key_itr.next();
			QBMSCreditCardResponse response = (QBMSCreditCardResponse)auth_hash.get(credit_card_trans_id);
			System.out.println("response.getChargeAmount() >" + response.getChargeAmount());
			System.out.println("response.getChargeAmount().compareTo(_refund_amount) >" + response.getChargeAmount().compareTo(_refund_amount));
			if (response.getChargeAmount().compareTo(_refund_amount) == 1)
			{
				// it must have been captured, but should not be voided or already refunded...
				if (captured_hash.containsKey(credit_card_trans_id))
				{
					if (!void_hash.containsKey(credit_card_trans_id) && !refund_hash.containsKey(credit_card_trans_id))
						return response;
				}
			}
		}

		throw new ObjectNotFoundException("Unable to find previous charge to refund " + _refund_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

	}

    public static Vector
    getUnusedAuthorizedResponsesForToday(UKOnlinePersonBean _client)
		throws TorqueException, ObjectNotFoundException
    {
		Vector vec = new Vector();

		Date now = new Date();

		Calendar start_date = Calendar.getInstance();
		start_date.setTime(now);
		start_date.set(Calendar.HOUR_OF_DAY, 0);
		start_date.set(Calendar.MINUTE, 0);
		start_date.set(Calendar.SECOND, 0);

		Calendar end_date = Calendar.getInstance();
		end_date.setTime(now);
		end_date.set(Calendar.HOUR_OF_DAY, 23);
		end_date.set(Calendar.MINUTE, 59);
		end_date.set(Calendar.SECOND, 59);


		Criteria crit = new Criteria();
		crit.add(ChargeResponseDbPeer.CLIENT_ID, _client.getId());
		crit.add(ChargeResponseDbPeer.RESPONSE_TYPE, QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE);
		crit.add(ChargeResponseDbPeer.RESPONSE_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(ChargeResponseDbPeer.RESPONSE_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		Iterator itr = ChargeResponseDbPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			QBMSCreditCardResponse auth_response = QBMSCreditCardResponse.getResponse((ChargeResponseDb)itr.next());
			if (!auth_response.hasTender())
			{
				if ((auth_response.getCreditCardTransId() != null) && (auth_response.getCreditCardTransId().length() > 0))
				{
					if (!QBMSCreditCardResponse.hasBeenCapturedOrVoided(auth_response))
						vec.addElement(auth_response);
				}
			}
		}
		
		/* just for testing
		QBMSCreditCardResponse auth_response_obj = new QBMSCreditCardResponse();
		auth_response_obj.response.setAuthorizationCode("Appr");
		auth_response_obj.response.setChargeAmount(BigDecimal.TEN);
		auth_response_obj.response.setChargeResponseDbId(7);
		vec.addElement(auth_response_obj);
		*/

		return vec;
    }

    // SQL

    /*
     *        <table name="CHARGE_RESPONSE_DB" idMethod="native">
				<column name="CHARGE_RESPONSE_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>

				<column name="AUTHORIZATION_CODE" required="true" size="15" type="VARCHAR"/>
				<column name="CLIENT_TRANS_ID" required="false" size="15" type="VARCHAR"/>
				<column name="CREDIT_CARD_TRANS_ID" required="false" size="30" type="VARCHAR"/>
				<column name="MERCHANT_ACCOUNT_NUMBER" required="false" size="30" type="VARCHAR"/>
				<column name="PAYMENT_GROUPING_CODE" required="false" size="5" type="VARCHAR"/>
				<column name="PAYMENT_STATUS" required="false" size="15" type="VARCHAR"/>
				<column name="TXN_AUTHORIZATION_TIME" required="true" type="DATE"/>
				<column name="TXN_AUTHORIZATION_STAMP" required="false" size="15" type="VARCHAR"/>
				<column name="CARD_SECURITY_CODE_MATCH" required="false" size="15" type="VARCHAR"/>
				<column name="AVS_STREET" required="false" size="15" type="VARCHAR"/>
				<column name="AVS_ZIP" required="false" size="15" type="VARCHAR"/>
				<column name="RECON_BATCH_ID" required="false" size="50" type="VARCHAR"/>
			</table>

			<table name="CHARGE_RESPONSE_DB_ORDER_MAPPING">
				<column name="CHARGE_RESPONSE_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
				<column name="ORDER_ID" primaryKey="true" required="true" type="INTEGER"/>

				<foreign-key foreignTable="CHARGE_RESPONSE_DB">
					<reference local="CHARGE_RESPONSE_DB_ID" foreign="CHARGE_RESPONSE_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="PERSONORDER">
					<reference local="ORDER_ID" foreign="ORDERID"/>
				</foreign-key>
			</table>
     */

    // INSTANCE VARIABLES

    private ChargeResponseDb response;
	private TenderRet tender;

    // CONSTRUCTORS

    public
    QBMSCreditCardResponse()
    {
		response = new ChargeResponseDb();
		isNew = true;
    }

    public
    QBMSCreditCardResponse(ChargeResponseDb _response)
    {
		response = _response;
		isNew = false;
    }

    // INSTANCE METHODS

	public String
	getAuthorizationCode()
	{
		return response.getAuthorizationCode();
	}

	public String
	getCardType()
	{
		return response.getCardType();
	}

	public UKOnlinePersonBean
	getClient()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(response.getClientId());
	}

	public String
	getCreditCardTransId()
	{
		return response.getCreditCardTransId();
	}

	public String
	getCreditCardRefTransId()
	{
		return response.getCreditCardRefTransId();
	}

    public int
    getId()
    {
		return response.getChargeResponseDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return this.getClient().getLabel() + " Charge Response";
    }

	public String
	getMerchantAccountNumber()
	{
		return response.getMerchantAccountNumber();
	}

	public TenderRet
	getTender()
		throws TorqueException, ObjectNotFoundException
	{
		return TenderRet.getTender(response.getTenderRetDbId());
	}

    public String
    getValue()
    {
		return response.getChargeResponseDbId() + "";
    }

	public boolean
	hasTender()
		throws TorqueException, ObjectNotFoundException
	{
		return (response.getTenderRetDbId() > 0);
	}

    protected void
    insertObject()
		throws Exception
    {
		if (tender != null)
		{
			if (tender.isNew())
				throw new IllegalValueException("Tender not initialized");
			response.setTenderRetDbId(tender.getId());
		}
		response.setResponseDate(new Date());
		response.save();
    }

	public void
	setErrorCode(short _error_code)
	{
		response.setErrorCode(_error_code);
	}

    public void
    setClient(PersonBean _client)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, Exception
    {
		response.setCompanyId(_client.getSelectedCompany().getId());
		response.setClientId(_client.getId());
    }

	public void
	setCreditCardTransId(String _credit_card_trans_id)
	{
		response.setCreditCardTransId(_credit_card_trans_id);
	}

	public void
	setResponse(GatewayResponseParser _response)
	{
		System.out.println("setResponse(GatewayResponseParser) invoked in QBMSCreditCardResponse >" + _response.getAccountType());
		response.setResponseType(QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE);
		response.setAuthorizationCode(_response.getAuthCode());
		response.setAvsStreet(_response.getAVSResultCode());
		//response.setClientTransId(_response.getTransID() + "");
		response.setCreditCardTransId(_response.getTransID());
		response.setCreditCardRefTransId(_response.getRefTransID());
		response.setMerchantAccountNumber(_response.getAccountNumber());
		response.setCardType(_response.getAccountType());
	}

	public void
	setResponse(AuthorizeResponse _response)
	{
		response.setResponseType(QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE);

		response.setAuthorizationCode(_response.getAuthorizationCode());
		response.setAvsStreet(_response.getAvsStreet());
		response.setAvsZip(_response.getAvsZip());
		response.setCardSecurityCodeMatch(_response.getCardSecurityCodeMatch());
		response.setClientTransId(_response.getClientTransId());
		response.setCreditCardTransId(_response.getCreditCardTransId());
	}

	public void
	setResponse(CaptureResponse _response)
	{
		response.setResponseType(QBMSCreditCardResponse.CAPTURE_RESPONSE_TYPE);

		response.setAuthorizationCode(_response.getAuthorizationCode());
		response.setClientTransId(_response.getClientTransId());
		response.setCreditCardTransId(_response.getCreditCardTransId());
		response.setMerchantAccountNumber(_response.getMerchantAccountNumber());
		response.setPaymentGroupingCode(_response.getPaymentGroupingCode().toString());
		response.setPaymentStatus(_response.getPaymentStatus());
		response.setReconBatchId(_response.getReconBatchId());
		response.setTxnAuthorizationStamp(_response.getTxnAuthorizationStamp().toString());
		response.setTxnAuthorizationTime(_response.getTxnAuthorizationTime());
	}

	public void
	setResponse(ChargeResponse _response)
	{
		response.setResponseType(QBMSCreditCardResponse.CHARGE_RESPONSE_TYPE);

		response.setAuthorizationCode(_response.getAuthorizationCode());
		response.setAvsStreet(_response.getAvsStreet());
		response.setAvsZip(_response.getAvsZip());
		response.setCardSecurityCodeMatch(_response.getCardSecurityCodeMatch());
		response.setClientTransId(_response.getClientTransId());
		response.setCreditCardTransId(_response.getCreditCardTransId());
		response.setMerchantAccountNumber(_response.getMerchantAccountNumber());
		response.setPaymentGroupingCode(_response.getPaymentGroupingCode().toString());
		response.setPaymentStatus(_response.getPaymentStatus());
		response.setReconBatchId(_response.getReconBatchId());
		response.setTxnAuthorizationStamp(_response.getTxnAuthorizationStamp().toString());
		response.setTxnAuthorizationTime(_response.getTxnAuthorizationTime());
	}

	public void
	setResponse(RefundResponse _response)
	{
		response.setResponseType(QBMSCreditCardResponse.REFUND_RESPONSE_TYPE);

		response.setClientTransId(_response.getClientTransId());
		response.setCreditCardTransId(_response.getCreditCardTransId());
		response.setMerchantAccountNumber(_response.getMerchantAccountNumber());
		response.setPaymentGroupingCode(_response.getPaymentGroupingCode().toString());
		response.setPaymentStatus(_response.getPaymentStatus());
		response.setReconBatchId(_response.getReconBatchId());
		response.setTxnAuthorizationStamp(_response.getTxnAuthorizationStamp().toString());
		response.setTxnAuthorizationTime(_response.getTxnAuthorizationTime());
	}

	public void
	setResponse(VoiceAuthorizationResponse _response)
	{
		response.setResponseType(QBMSCreditCardResponse.VOICE_AUTHORIZATION_RESPONSE_TYPE);

		response.setAuthorizationCode(_response.getAuthorizationCode());
		response.setClientTransId(_response.getClientTransId());
		response.setCreditCardTransId(_response.getCreditCardTransId());
		response.setMerchantAccountNumber(_response.getMerchantAccountNumber());
		response.setPaymentGroupingCode(_response.getPaymentGroupingCode().toString());
		response.setPaymentStatus(_response.getPaymentStatus());
		response.setReconBatchId(_response.getReconBatchId());
		response.setTxnAuthorizationStamp(_response.getTxnAuthorizationStamp().toString());
		response.setTxnAuthorizationTime(_response.getTxnAuthorizationTime());
	}

	public void
	setResponse(VoidTransactionResponse _response)
	{
		response.setResponseType(QBMSCreditCardResponse.VOID_TRANSACTION_RESPONSE_TYPE);

		response.setClientTransId(_response.getClientTransId());
		response.setCreditCardTransId(_response.getCreditCardTransId());
	}

	public void
	setResponseCode(short _response_code)
	{
		response.setResponseCode(_response_code);
	}

	public void
	setResponseType(short _response_type)
	{
		response.setResponseType(_response_type);
	}

	public short
	getResponseType()
	{
		return response.getResponseType();
	}

    public void
    setTender(TenderRet _tender)
		throws TorqueException
    {
		tender = _tender;
    }

	/*
	 *

    <column name="CHARGE_AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>
	<column name="CARD_TYPE" required="true" size="20" type="VARCHAR"/>
	<column name="EXP_M_M" required="false" size="2" type="VARCHAR"/>
	<column name="EXP_Y_Y" required="false" size="2" type="VARCHAR"/>
	<column name="ZIP_CODE" required="false" size="20" type="VARCHAR"/>
	<column name="STREET_ADDRESS" required="false" size="200" type="VARCHAR"/>
	<column name="CVC_CODE" required="false" size="10" type="VARCHAR"/>
	 */

	public BigDecimal
	getChargeAmount()
	{
		return response.getChargeAmount();
	}

	public void
	setChargeAmount(BigDecimal _charge_amount)
	{
		response.setChargeAmount(_charge_amount);
	}

	public void
	setCardType(String _card_type)
	{
		response.setCardType(_card_type);
	}

	public void
	setExpMM(String _mm)
	{
		response.setExpMM(_mm);
	}

	public void
	setExpYY(String _yy)
	{
		response.setExpYY(_yy);
	}

	public void
	setZipCode(String _zip)
	{
		response.setZipCode(_zip);
	}

	public void
	setStreetAddress(String _street)
	{
		response.setStreetAddress(_street);
	}

	public void
	setCVCCode(String _cvc)
	{
		response.setCvcCode(_cvc);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		if (tender != null)
		{
			if (tender.isNew())
				throw new IllegalValueException("Tender not initialized");
			response.setTenderRetDbId(tender.getId());
		}
		response.save();
    }


}