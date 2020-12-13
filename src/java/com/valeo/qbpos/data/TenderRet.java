/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbpos.data;

import java.math.BigDecimal;

import java.util.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.online.beans.*;
import com.valeo.qb.data.InvoiceRet;
import com.valeo.qbms.data.QBMSCreditCardResponse;
import java.math.RoundingMode;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
TenderRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

	private static Date last_update;

    protected static HashMap<Integer,TenderRet> hash = new HashMap<Integer,TenderRet>(11);

	public static final String ACCOUNT_TENDER_TYPE = "Account";
	public static final String CHECK_TENDER_TYPE = "Check";
	public static final String CREDIT_CARD_TENDER_TYPE = "CreditCard";
	public static final String CASH_TENDER_TYPE = "Cash";

	public static final String GIFT_CERTIFICATE_TENDER_TYPE = "Gift Certificate";
	public static final String GIFT_CARD_TENDER_TYPE = "Gift Card";
	
	public static final String UNKNOWN_TENDER_TYPE = "Unknown";
	
	public static final String NSIPM_TENDER_TYPE = "NSIPM";

	// CLASS METHODS

    public static void
    deleteSalesReceiptItems(SalesReceiptRet _sales_receipt)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.SALES_RECEIPT_RET_DB_ID, _sales_receipt.getId());
		TenderRetDbPeer.doDelete(crit);
    }

	/*
    public static void
    deleteSalesReceiptItemsXX(SalesReceiptRet _sales_receipt)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		//Give you set of Threads
		java.util.Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();

		//Iterate over set to find yours
		for(Thread thread : setOfThread){

			//if(thread.getId()==yourThread.getId()){
			//    thread.interrupt();
			//}

			//out.println("Thread id: "+thread.getId()+" <BR>");
			if (thread.getState() == java.lang.Thread.State.BLOCKED) {
				//out.println("Thread id: "+ thread.getId()+ " - " + thread.getState() + " <BR>");
				thread.interrupt();
				thread.getName()
			}
		}
    }
	*/

    public static TenderRet
    getTender(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		TenderRet obj = (TenderRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(TenderRetDbPeer.TENDER_RET_DB_ID, _id);
			List objList = TenderRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Tender with id: " + _id);

			obj = TenderRet.getTender((TenderRetDb)objList.get(0));
		}

		return obj;
    }

    private static TenderRet
    getTender(TenderRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getTenderRetDbId());
		TenderRet obj = (TenderRet)hash.get(key);
		if (obj == null)
		{
			obj = new TenderRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

	
	public static TenderRetDbOrderMapping
	getMapping(CompanyBean _company, String _requestID)
		throws TorqueException, ObjectNotFoundException
	{
		/*
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(TenderRetDbPeer.REQUEST_I_D, _requestID);
		List objList = TenderRetDbPeer.doSelect(crit);
		if (objList.size() == 1)
			return TenderRet.getTender((TenderRetDb)objList.get(0));
		else
			throw new ObjectNotFoundException("Could not locate tender with requestID code: " + _requestID);
		 * 
		 */
		
		Criteria crit = new Criteria();
		crit.addJoin(TenderRetDbPeer.TENDER_RET_DB_ID, TenderRetDbOrderMappingPeer.TENDER_RET_DB_ID);
		crit.add(TenderRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(TenderRetDbOrderMappingPeer.REQUEST_I_D, _requestID);
		System.out.println("getMapping() for request (" + _requestID + ") crit >" + crit.toString());
		List obj_list = TenderRetDbOrderMappingPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return (TenderRetDbOrderMapping)obj_list.get(0);
		else
			throw new ObjectNotFoundException("Could not locate tender mappping for requestID: " + _requestID);
	}

	/*
    public static Vector
    getTenders(OrderBean _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.ORDER_ID, _order.getId());
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(TenderRet.getTender((TenderRetDb)obj_itr.next()));

		return vec;
    }
	 */

    public static Vector
    getTenders(OrderBean _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TenderRetDbOrderMappingPeer.ORDER_ID, _order.getId());
		Iterator obj_itr = TenderRetDbOrderMappingPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			TenderRetDbOrderMapping obj = (TenderRetDbOrderMapping)obj_itr.next();
			vec.addElement(TenderRet.getTender(obj.getTenderRetDbId()));
		}

		return vec;
    }

    public static Vector
    getTenders(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.CLIENT_ID, _person.getId());
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			TenderRetDb obj = (TenderRetDb)obj_itr.next();
			vec.addElement(TenderRet.getTender(obj));
		}

		return vec;
    }

    public static Vector
    getTendersByDateDesc(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.CLIENT_ID, _person.getId());
		crit.addDescendingOrderByColumn(TenderRetDbPeer.TENDER_DATE);
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			TenderRetDb obj = (TenderRetDb)obj_itr.next();
			vec.addElement(TenderRet.getTender(obj));
		}

		return vec;
    }

    public static Vector
    getTenders(UKOnlinePersonBean _person, Date _start_date, Date _end_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
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
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.CLIENT_ID, _person.getId());
		crit.add(TenderRetDbPeer.TENDER_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(TenderRetDbPeer.TENDER_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.addDescendingOrderByColumn(TenderRetDbPeer.TENDER_DATE);
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			TenderRetDb obj = (TenderRetDb)obj_itr.next();
			vec.addElement(TenderRet.getTender(obj));
		}

		return vec;
    }

    public static Vector
    getTenders(SalesReceiptRet _sales_receipt)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.SALES_RECEIPT_RET_DB_ID, _sales_receipt.getId());
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(TenderRet.getTender((TenderRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getTenders(InvoiceRet _invoice)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.INVOICE_RET_DB_ID, _invoice.getId());
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(TenderRet.getTender((TenderRetDb)obj_itr.next()));

		return vec;
    }

	public static Vector
	getTenders(CompanyBean _company, Date _start_date, Date _end_date)
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

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.TENDER_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(TenderRetDbPeer.TENDER_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.add(TenderRetDbPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(TenderRetDbPeer.TENDER_DATE);
		System.out.println("getTenders() crit >" + crit.toString());
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			TenderRetDb tender_obj = (TenderRetDb)obj_itr.next();
			vec.addElement(TenderRet.getTender(tender_obj));
		}
		return vec;
	}

	public static Vector
	getTendersUnsubmitted(CompanyBean _company, Date _start_date, Date _end_date)
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

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.TENDER_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(TenderRetDbPeer.TENDER_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.add(TenderRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(TenderRetDbPeer.TXN_I_D, (Object)"", Criteria.ISNULL);
		crit.addAscendingOrderByColumn(TenderRetDbPeer.TENDER_DATE);
		System.out.println("getTendersUnsubmitted() crit >" + crit.toString());
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			TenderRetDb tender_obj = (TenderRetDb)obj_itr.next();
			vec.addElement(TenderRet.getTender(tender_obj));
		}
		return vec;
	}

	public static Vector
	getTenders(CompanyBean _company, Date _start_date, Date _end_date, String _workstation_name)
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

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(PersonorderPeer.ORDERID, TenderRetDbOrderMappingPeer.ORDER_ID);
		crit.addJoin(TenderRetDbOrderMappingPeer.TENDER_RET_DB_ID, TenderRetDbPeer.TENDER_RET_DB_ID);
		crit.add(TenderRetDbPeer.TENDER_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(TenderRetDbPeer.TENDER_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.add(PersonorderPeer.COMPANY_ID, _company.getId());
		crit.add(PersonorderPeer.WORKSTATION_NAME, _workstation_name);
		crit.addAscendingOrderByColumn(TenderRetDbPeer.TENDER_DATE);
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			TenderRetDb tender_obj = (TenderRetDb)obj_itr.next();
			TenderRet tender_ret_obj = TenderRet.getTender(tender_obj);
			if (!vec.contains(tender_ret_obj))
				vec.addElement(tender_ret_obj);
		}
		return vec;
	}

	public static Vector
	getTendersUnsubmitted(CompanyBean _company, Date _start_date, Date _end_date, String _workstation_name)
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

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(PersonorderPeer.ORDERID, TenderRetDbOrderMappingPeer.ORDER_ID);
		crit.addJoin(TenderRetDbOrderMappingPeer.TENDER_RET_DB_ID, TenderRetDbPeer.TENDER_RET_DB_ID);
		crit.add(TenderRetDbPeer.TENDER_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(TenderRetDbPeer.TENDER_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.add(TenderRetDbPeer.TXN_I_D, (Object)"", Criteria.ISNULL);
		crit.add(PersonorderPeer.COMPANY_ID, _company.getId());
		crit.add(PersonorderPeer.WORKSTATION_NAME, _workstation_name);
		crit.addAscendingOrderByColumn(TenderRetDbPeer.TENDER_DATE);
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			TenderRetDb tender_obj = (TenderRetDb)obj_itr.next();
			TenderRet tender_ret_obj = TenderRet.getTender(tender_obj);
			if (!vec.contains(tender_ret_obj))
				vec.addElement(tender_ret_obj);
		}
		return vec;
	}

	public static Vector
	getPaymentOnAccountTenders(CompanyBean _company, Date _start_date, Date _end_date)
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

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TenderRetDbPeer.TENDER_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(TenderRetDbPeer.TENDER_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.add(TenderRetDbPeer.IS_PAYMENT_ON_ACCOUNT, (short)1);
		crit.add(TenderRetDbPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(TenderRetDbPeer.TENDER_DATE);
		Iterator obj_itr = TenderRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			TenderRetDb tender_obj = (TenderRetDb)obj_itr.next();
			vec.addElement(TenderRet.getTender(tender_obj));
		}
		return vec;
	}

	// INSTANCE VARIABLES

	private TenderRetDb tender;
	private Vector orders;
	private HashMap tender_payment_info;

	// CONSTRUCTORS

	public
	TenderRet()
	{
		tender = new TenderRetDb();
		isNew = true;
	}

	public
	TenderRet(TenderRetDb _tender)
	{
		tender = _tender;
		isNew = false;
	}

	// INSTANCE METHODS

	public float
	getAmount()
	{
		if (tender.getTenderChange() == null)
			return tender.getTenderAmount().setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		return tender.getTenderAmount().subtract(tender.getTenderChange()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public BigDecimal
	getAmountBigDecimal()
	{
		if (tender.getTenderChange() == null)
			return tender.getTenderAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		return tender.getTenderAmount().subtract(tender.getTenderChange()).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public String
	getAmountString()
	{
		if (tender.getTenderChange() == null)
			return tender.getTenderAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		return tender.getTenderAmount().subtract(tender.getTenderChange()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public String
	getAuthorizationCodeString()
	{
		try
		{
			QBMSCreditCardResponse charge_response = QBMSCreditCardResponse.getResponse(this);
			return charge_response.getAuthorizationCode();
		}
		catch (Exception x)
		{
			
		}
		
		return "[NOT FOUND]";
	}

	public String
	getMerchantAccountNumberString()
	{
		try
		{
			QBMSCreditCardResponse charge_response = QBMSCreditCardResponse.getResponse(this);
			return charge_response.getMerchantAccountNumber();
		}
		catch (Exception x)
		{
			
		}
		
		return "[NOT FOUND]";
	}

	public BigDecimal
	getChangeAmountBigDecimal()
	{
		if (tender.getTenderChange() == null)
			return CUBean.zero.setScale(2, BigDecimal.ROUND_HALF_UP);
		return tender.getTenderChange().setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public String
	getChangeAmountString()
	{
		if (tender.getTenderChange() == null) {
			//return CUBean.zero.setScale(2, BigDecimal.ROUND_HALF_UP).toString(); // seems odd
			return "0.00";
		}
		return tender.getTenderChange().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public UKOnlinePersonBean
	getClient()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(tender.getClientId());
	}

	public BigDecimal
	getTenderAmountBigDecimal()
	{
		if (tender.getTenderAmount() == null)
			return CUBean.zero.setScale(2, BigDecimal.ROUND_HALF_UP);
		return tender.getTenderAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public String
	getTenderedAmountString()
	{
		if (tender.getTenderAmount() == null) {
			return "0.00";
		}
		return tender.getTenderAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public int
	getCheckNumber()
	{
		return tender.getCheckNumber();
	}

	public String
	getGiftCertCardNumber()
	{
		return tender.getGiftCertCardNumber();
	}

	public int
	getId()
	{
		return tender.getTenderRetDbId();
	}

	public String
	getLabel()
	{
		if (this.getType().equals("Unknown")) {
			return "QB Tender : " + this.getAmountString();
		}
		
		return this.getType() + " : " + this.getAmountString();
	}

	public Vector
	getOrders()
		throws TorqueException, ObjectNotFoundException
	{
		if (this.orders == null || this.tender_payment_info == null) {
			this.orders = new Vector();
			this.tender_payment_info = new HashMap(3);

			Criteria crit = new Criteria();
			crit.add(TenderRetDbOrderMappingPeer.TENDER_RET_DB_ID, this.getId());
			Iterator itr = TenderRetDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				TenderRetDbOrderMapping mapping_obj = (TenderRetDbOrderMapping)itr.next();
				try {
					ValeoOrderBean order = (ValeoOrderBean)ValeoOrderBean.getOrder(mapping_obj.getOrderId());
					BigDecimal amount_applied_to_order = mapping_obj.getTenderAmountApplied();

					//System.out.println("found amount applied to order >" + amount_applied_to_order.toString());

					HashMap tender_to_amount_hash = (HashMap)this.tender_payment_info.get(order);
					if (tender_to_amount_hash == null) {
						tender_to_amount_hash = new HashMap(3);
						this.tender_payment_info.put(order, tender_to_amount_hash);
					}

					tender_to_amount_hash.put(this, amount_applied_to_order);

					this.orders.addElement(order);
				} catch (ObjectNotFoundException x) {
					x.printStackTrace();
				}
			}
		}
		return this.orders;
	}
	
	public BigDecimal
	getAmountAppliedToOrder(ValeoOrderBean _order) throws TorqueException, ObjectNotFoundException {
		
		this.getOrders();
		HashMap tender_to_amount_hash = (HashMap)this.tender_payment_info.get(_order);
		if (tender_to_amount_hash != null) {
			BigDecimal amount_applied_to_order = (BigDecimal)tender_to_amount_hash.get(this);
			if (amount_applied_to_order != null) {
				return amount_applied_to_order;
			}
		}
		return BigDecimal.ZERO;
	}

	public Date
	getTenderDate()
	{
		return tender.getTenderDate();
	}

	public String
	getType()
	{
		return tender.getTenderType();
	}
	
	public String
	getMethod1() {
		return this.method_1;
	}
	
	public String
	getMethod2() {
		return this.method_2;
	}

	public String
	getValue()
	{
		return tender.getTenderRetDbId() + "";
	}

	public String
	getWorkstationName()
	{
		return tender.getWorkstationName();
	}
	
	public boolean
	hasOrder()
	{
		try
		{
			return (this.getOrders().size() > 0);
		}
		catch (Exception x)
		{
			x.printStackTrace();
			return false;
		}
	}

    protected void
    insertObject()
		throws Exception
    {
		if (tender.getTenderDate() == null)
			tender.setTenderDate(new Date());
		tender.save();
		this.saveOrders();
    }

	public void
	invalidate()
	{
		this.orders = null;
	}

	public boolean
	isPaymentOnAccount()
	{
		return (tender.getIsPaymentOnAccount() == (short)1);
	}
	
	public boolean
	isCreditCardPaymentType() {
		return tender.getTenderType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE);
	}

	public void
	setIsPaymentOnAccount(boolean _b)
	{
		tender.setIsPaymentOnAccount(_b ? (short)1 : (short)0);
	}

	public boolean
	isVoidedOrRefunded()
	{
		return (tender.getIsVoidedOrRefunded() == (short)1);
	}

	public void
	setVoidedOrRefunded()
		throws TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		/*
		 * public static Vector
			getOpenOrders(UKOnlinePersonBean _person, Date _from_order_date)
				throws TorqueException
			{
				//System.out.println("getOpenOrders() invoked in ValeoOrderBean - " + CUBean.getUserDateString(_from_order_date));
				Vector vec = new Vector();
				Criteria crit = new Criteria();
				crit.add(PersonorderPeer.PERSONID, _person.getId());
				//crit.add(PersonorderPeer.ORDERSTATUS, OrderBean.OPEN_ORDER_STATUS);
				crit.add(PersonorderPeer.IS_PAID, 0);
				crit.add(PersonorderPeer.BALANCE, 0, Criteria.GREATER_THAN);
				crit.add(PersonorderPeer.ORDERDATE, _from_order_date, Criteria.GREATER_THAN);
				crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
				//System.out.println("getOpenOrders crit2 >" + crit.toString());
				Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
				while (obj_itr.hasNext())
				{
					Personorder order_obj = (Personorder)obj_itr.next();
					vec.addElement(ValeoOrderBean.getOrder(order_obj));
				}
				return vec;
			}
		 */
		
		// get the order mappings for this tender
		
		Vector orders_to_save = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(TenderRetDbOrderMappingPeer.TENDER_RET_DB_ID, this.getId());
		Iterator itr = TenderRetDbOrderMappingPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			TenderRetDbOrderMapping mapping = (TenderRetDbOrderMapping)itr.next();
			ValeoOrderBean mapped_order = (ValeoOrderBean)ValeoOrderBean.getOrder(mapping.getOrderId());
			mapped_order.setIsPaid(false);
			// balance is increasing
			BigDecimal balance = mapped_order.getBalance();
			mapped_order.setBalance(balance.add(mapping.getTenderAmountApplied()));
			orders_to_save.addElement(mapped_order);
		}
		
		itr = orders_to_save.iterator();
		while (itr.hasNext())
		{
			ValeoOrderBean mapped_order = (ValeoOrderBean)itr.next();
			mapped_order.save();
		}
		
		tender.setIsVoidedOrRefunded((short)1);
	}

	public BigDecimal
	getAmountMappedToOrder(OrderBean _order)
		throws TorqueException, ObjectNotFoundException
	{
		this.getOrders();

		HashMap payment_hash = (HashMap)tender_payment_info.get(_order);
		if (payment_hash != null)
		{
			BigDecimal amount_applied = (BigDecimal)payment_hash.get(this);
			if (amount_applied == null)
				return BigDecimal.ZERO;
			else
				return amount_applied;
		}

		return BigDecimal.ZERO;
	}

	private void
	saveOrders()
		throws TorqueException, Exception
	{
		/*
		 *<table name="TENDER_RET_DB_ORDER_MAPPING">
    <column name="TENDER_RET_DB_ORDER_MAPPING_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="TENDER_RET_DB_ID" required="true" type="INTEGER"/>
    <column name="ORDER_ID" required="true" type="INTEGER"/>

    <foreign-key foreignTable="TENDER_RET_DB">
	<reference local="TENDER_RET_DB_ID" foreign="TENDER_RET_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSONORDER">
	<reference local="ORDER_ID" foreign="ORDERID"/>
    </foreign-key>
</table>
		 */

		if (this.orders != null)
		{
			HashMap db_orders_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(TenderRetDbOrderMappingPeer.TENDER_RET_DB_ID, this.getId());
			Iterator itr = TenderRetDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				TenderRetDbOrderMapping value = (TenderRetDbOrderMapping)itr.next();
				Integer key = new Integer(value.getOrderId());
				db_orders_hash.put(key, value);
			}

			System.out.println("num members >" + this.orders.size());
			itr = this.orders.iterator();
			while (itr.hasNext())
			{
				OrderBean order = (OrderBean)itr.next();
				Integer key = new Integer(order.getId());
				TenderRetDbOrderMapping obj = (TenderRetDbOrderMapping)db_orders_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("association does not exist in db.  need to create");

					obj = new TenderRetDbOrderMapping();
					obj.setTenderRetDbId(this.getId());
					obj.setOrderId(order.getId());
					if (tender_payment_info != null)
					{
						HashMap payment_hash = (HashMap)tender_payment_info.get(order);
						if (payment_hash == null)
							System.out.println("payment info not found for order >" + order.getValue());
						else
						{
							System.out.println("PAYMENT INFO SIZER >" + payment_hash.size());
							BigDecimal amount_applied = (BigDecimal)payment_hash.get(this);
							if (amount_applied == null)
								System.out.println("payment info not found for tender >" + this.getValue());
							else
							{
								System.out.println("amount_applied >" + amount_applied.toString());
								obj.setTenderAmountApplied(amount_applied);
							}
						}
					}
					obj.save();
				}
			}

			itr = db_orders_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				TenderRetDbOrderMapping obj = (TenderRetDbOrderMapping)db_orders_hash.get(key);
				System.out.println("BLOCKING deleting map to order >" + obj.getOrderId());
				//TenderRetDbOrderMappingPeer.doDelete(obj);
			}
		}
	}

	public void
	setAmount(float _amount)
	{
		tender.setTenderAmount(new BigDecimal(_amount));
	}

	public void
	setChangeAmount(float _change_amount)
	{
		tender.setTenderChange(new BigDecimal(_change_amount));
	}

	public void
	setCheckNumber(int _number)
	{
		tender.setCheckNumber(_number);
	}

	public void
	setGiftCertCardNumber(String _number)
	{
		tender.setGiftCertCardNumber(_number);
	}

	public void
	setClient(UKOnlinePersonBean _client)
		throws TorqueException
	{
		tender.setClientId(_client.getId());
	}

	public int
	getCompanyId()
	{
		return tender.getCompanyId();
	}

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		tender.setCompanyId(_company.getId());
	}

	public String
	getCreditCardType()
	{
		return tender.getCreditCardType();
	}

	public String
	getCreditCardTypeString()
	{
		//System.out.println("getCreditCardTypeString invoked >" + tender.getCreditCardType());
		String str = tender.getCreditCardType();
		if (str == null)
			return "";
		return str;
	}

	public void
	setCreditCardType(String _card_type)
	{
		//System.out.println("setCreditCardType invoked >" + _card_type);
		tender.setCreditCardType(_card_type);
	}

	public void
	setOrder(OrderBean _order)
		throws TorqueException
	{
		tender.setOrderId(_order.getId());
	}

	public void
	setOrders(Vector<OrderBean> _orders)
		throws TorqueException
	{
		orders = _orders;

		Iterator itr = _orders.iterator();
		while (itr.hasNext())
		{
			OrderBean order = (OrderBean)itr.next();
			System.out.println("SETTING ORDER >" + order.getValue() + " FOR TENDER >" + this.getValue());
		}
	}

	public void
	setRequestID(String _request_id)
	{
		tender.setRequestID(_request_id);
	}

	public void
	setSalesReceipt(SalesReceiptRet _sales_receipt)
		throws TorqueException
	{
		tender.setSalesReceiptRetDbId(_sales_receipt.getId());
	}

	public void
	setTenderDate(Date _tender_date)
	{
		tender.setTenderDate(_tender_date);
	}

	public void
	setTenderPaymentInfo(HashMap _tender_payment_info)
	{
		tender_payment_info = _tender_payment_info;
	}

	public void
	setTxnID(String _txn_id)
	{
		tender.setTxnID(_txn_id);
	}
	
	String method_1 = null;
	String method_2 = null;

	public void
	setType(short _tender_type) throws IllegalValueException {
		
		/*<select name="method" id="method">
			<option value="4">Account</option>
			<option value="1">Credit Card</option>
			<option value="2">Check</option>
			<option value="3">Cash</option>
			<option value="5">Gift Certificate</option>
			<option value="6">Gift Card</option>
		</select>
		*/
		
		
		/*
		        methodSelect: function () {
            switch (document.checkoutForm.method.value) {
                case '1': {
                    document.getElementById('method_1').firstChild.nodeValue = 'Charge To';
                    document.getElementById('method_2').firstChild.nodeValue = 'Refund';
                    break;
                } case '2': case '3': {
                    document.getElementById('method_1').firstChild.nodeValue = 'Tendered';
                    document.getElementById('method_2').firstChild.nodeValue = 'Change';
                    break;
                } case '4': {
                    document.getElementById('method_1').firstChild.nodeValue = 'Charge To';
                    document.getElementById('method_2').firstChild.nodeValue = 'Payment';
                    break;
                } case '5': case '6': {
                    document.getElementById('method_1').firstChild.nodeValue = 'Redeem';
                    document.getElementById('method_2').firstChild.nodeValue = 'Sell';
                    break;
                }
            }
        },
		*/
		
		switch (_tender_type) {
			case (short)4: tender.setTenderType(TenderRet.ACCOUNT_TENDER_TYPE); method_1 = "Charge To"; method_2 = "Payment"; return;
			case (short)1: tender.setTenderType(TenderRet.CREDIT_CARD_TENDER_TYPE); method_1 = "Charge To"; method_2 = "Refund"; return;
			case (short)2: tender.setTenderType(TenderRet.CHECK_TENDER_TYPE); method_1 = "Tendered"; method_2 = "Change"; return;
			case (short)3: tender.setTenderType(TenderRet.CASH_TENDER_TYPE); method_1 = "Tendered"; method_2 = "Change"; return;
			case (short)5: tender.setTenderType(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE); method_1 = "Redeem"; method_2 = "Sell"; return;
			case (short)6: tender.setTenderType(TenderRet.GIFT_CARD_TENDER_TYPE); method_1 = "Redeem"; method_2 = "Sell"; return;
			
			case (short)7: tender.setTenderType(TenderRet.CREDIT_CARD_TENDER_TYPE); method_1 = "Charge To"; method_2 = "Refund"; this.setCreditCardType("Visa"); System.out.println("setting VISA"); return;
			case (short)8: tender.setTenderType(TenderRet.CREDIT_CARD_TENDER_TYPE); method_1 = "Charge To"; method_2 = "Refund"; this.setCreditCardType("Mastercard"); return;
			case (short)9: tender.setTenderType(TenderRet.CREDIT_CARD_TENDER_TYPE); method_1 = "Charge To"; method_2 = "Refund"; this.setCreditCardType("Discover"); return;
			case (short)10: tender.setTenderType(TenderRet.NSIPM_TENDER_TYPE); method_1 = "Charge To"; method_2 = "Payment"; return;
		}
		throw new IllegalValueException("Illegal tender type >" + _tender_type);
	}

	public void
	setType(String _tender_type)
	{
		tender.setTenderType(_tender_type);
	}

	public void
	setWorkstationName(String _workstation_name)
	{
		tender.setWorkstationName(_workstation_name);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		tender.save();
		this.saveOrders();
    }
	
	public BigDecimal
	getAmountMapped()
		throws TorqueException, ObjectNotFoundException
	{
		this.getOrders();

		Iterator itr = tender_payment_info.keySet().iterator();
		
		BigDecimal total_amount_mapped = BigDecimal.ZERO;
		while (itr.hasNext())
		{
			ValeoOrderBean mapped_order = (ValeoOrderBean)itr.next();
			HashMap payment_hash = (HashMap)tender_payment_info.get(mapped_order);
			if (payment_hash != null)
			{
				BigDecimal amount_applied = (BigDecimal)payment_hash.get(this);
				if (amount_applied != null)
					return total_amount_mapped = total_amount_mapped.add(amount_applied);
			}
		}

		return total_amount_mapped;
	}
	
	public void
	maintainOrderAssignment()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, Exception
	{
		System.out.println("maintainOrderAssignment() invoked in TenderRet");
		
		// find an open order to apply this against (if ther's an unmapped portion)
		// a potential problem here is that QB may assign payments to orders differently than I am below.  not sure how much of a problem that might be...

		BigDecimal tender_amount = this.getAmountBigDecimal().subtract(this.getAmountMapped()).setScale(2, RoundingMode.HALF_UP);
		if (tender_amount.compareTo(BigDecimal.ZERO) == 0) // no amount not mapped
			return;

		Calendar date_range_for_open_orders = Calendar.getInstance();
		date_range_for_open_orders.add(Calendar.YEAR, -5);
		
		
		Vector open_orders = this.getClient().getOpenOrders(date_range_for_open_orders.getTime()); // newest orders'll be on top
		System.out.println("num open orders >" + open_orders.size());

		this.getOrders();
		
		Vector orders_to_apply_payment_to = new Vector();
		//HashMap order_to_tender_amount_hash = new HashMap(3);

		// is there a single order to apply this against?  loop the orders and see

		Iterator open_orders_itr = open_orders.iterator();
		while (open_orders_itr.hasNext())
		{
			ValeoOrderBean open_order_obj = (ValeoOrderBean)open_orders_itr.next();

			BigDecimal open_order_balance = open_order_obj.getBalance();

			System.out.println("tender_amount.compareTo(open_order_balance) == 0 >" + (tender_amount.compareTo(open_order_balance) == 0));
			System.out.println("tender_amount >" + tender_amount);
			System.out.println("open_order_balance >" + open_order_balance);

			if (tender_amount.compareTo(open_order_balance) == 0)
			{
				// found open order to apply receive payment against
				System.out.println("found single order to apply receive payment against - " + open_order_obj.getValue());
				orders_to_apply_payment_to.addElement(open_order_obj);
				HashMap hash = new HashMap(3);
				hash.put(this, tender_amount);
				tender_payment_info.put(open_order_obj, hash);

				open_order_obj.setBalance(BigDecimal.ZERO);
				open_order_obj.setIsPaid(true);

			}
		}

		if (orders_to_apply_payment_to.isEmpty())
		{
			BigDecimal tender_amount_available_to_be_consumed = tender_amount;
			BigDecimal tender_amount_consumed = BigDecimal.ZERO;

			// first apply to the orders that can be covered completely

			open_orders_itr = open_orders.iterator();
			while (open_orders_itr.hasNext())
			{
				ValeoOrderBean open_order_obj = (ValeoOrderBean)open_orders_itr.next();

				BigDecimal open_order_balance = open_order_obj.getBalance();
				if ((open_order_balance.compareTo(tender_amount_available_to_be_consumed) == -1) ||
						(open_order_balance.compareTo(tender_amount_available_to_be_consumed) == 0))
				{
					System.out.println("found order amount(1) (" + open_order_balance.toString() + ") to apply against - " + tender_amount_available_to_be_consumed.toString());

					orders_to_apply_payment_to.addElement(open_order_obj);
					HashMap hash = new HashMap(3);
					hash.put(this, open_order_balance);
					tender_payment_info.put(open_order_obj, hash);
					tender_amount_available_to_be_consumed = tender_amount_available_to_be_consumed.subtract(open_order_balance);
					tender_amount_consumed = tender_amount_consumed.add(open_order_balance);

					open_order_obj.setBalance(BigDecimal.ZERO);
					open_order_obj.setIsPaid(true);
				}
			}

			// find an order to apply this to.  the first order with a balance larger than the amount available, I guess

			open_orders_itr = open_orders.iterator();
			while ((tender_amount_available_to_be_consumed.compareTo(BigDecimal.ZERO) == 1) && open_orders_itr.hasNext())
			{
				System.out.println("still some amount available to be consumed >" + tender_amount_available_to_be_consumed.toString());

				ValeoOrderBean open_order_obj = (ValeoOrderBean)open_orders_itr.next();

				if (!tender_payment_info.containsKey(open_order_obj))
				{
					BigDecimal open_order_balance = open_order_obj.getBalance();
					if ((open_order_balance.compareTo(tender_amount_available_to_be_consumed) == 1) ||
							(open_order_balance.compareTo(tender_amount_available_to_be_consumed) == 0)) // open order balance is greater than the available tender
					{
						System.out.println("found order amount(2) (" + open_order_balance.toString() + ") to apply against - " + tender_amount_available_to_be_consumed.toString());

						orders_to_apply_payment_to.addElement(open_order_obj);
						HashMap hash = new HashMap(3);
						hash.put(this, tender_amount_available_to_be_consumed);
						tender_payment_info.put(open_order_obj, hash);

						open_order_obj.setBalance(open_order_balance.subtract(tender_amount_available_to_be_consumed));

						tender_amount_consumed = tender_amount_consumed.add(tender_amount_available_to_be_consumed);
						tender_amount_available_to_be_consumed = BigDecimal.ZERO;
					}
				}
			}


		}
		
		Vector tender_orders = this.getOrders();


		// save the orders

		Iterator order_itr = orders_to_apply_payment_to.iterator();
		while (order_itr.hasNext())
		{
			ValeoOrderBean open_order_obj = (ValeoOrderBean)order_itr.next();
			open_order_obj.save();
			tender_orders.addElement(open_order_obj);
		}
		
		System.out.println("SETTING ORDERS FOR TENDERy >" + orders_to_apply_payment_to.size());
		System.out.println("tender_payment_info sizer >" + tender_payment_info.size());
		this.setOrders(tender_orders);
		//tender.setTenderPaymentInfo(order_to_tender_amount_hash);
		
		System.out.println("SAVING TENDER");
		this.save();
		System.out.println("DONE SAVING");
	}
}
