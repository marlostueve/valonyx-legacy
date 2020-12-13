/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.beans.OrderBean;
import com.badiyan.uk.exceptions.*;
import com.valeo.qb.data.InvoiceLineRet;
import com.valeo.qb.data.InvoiceRet;
import com.valeo.qbpos.data.TenderRet;
import java.math.BigDecimal;

import java.util.*;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
ValeoOrderBean
	extends OrderBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

	public static final String LEGACY_ORDER_STATUS = "Legacy Order";

	// CLASS METHODS

	public static Vector
	getOpenOrders(UKOnlinePersonBean _person)
		throws TorqueException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(PersonorderPeer.PERSONID, _person.getId());
		//crit.add(PersonorderPeer.ORDERSTATUS, OrderBean.OPEN_ORDER_STATUS);
		crit.add(PersonorderPeer.IS_PAID, 0);
		crit.add(PersonorderPeer.BALANCE, 0, Criteria.GREATER_THAN);
		crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
		//System.out.println("getOpenOrders crit1 >" + crit.toString());
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}
		return vec;
	}

	public static Vector
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

	/*
	 * <table name="CASH_OUT_DB" idMethod="native">
    <column name="CASH_OUT_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>

    <column name="START_DATE" required="true" type="DATE"/>
    <column name="END_DATE" required="true" type="DATE"/>
    <column name="IS_COMPLETE" type="SMALLINT" default="0"/>

    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>

<table name="CASH_OUT_DB_ORDER_MAPPING">
    <column name="CASH_OUT_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="ORDER_ID" primaryKey="true" required="true" type="INTEGER"/>

    <foreign-key foreignTable="CASH_OUT_DB">
		<reference local="CASH_OUT_DB_ID" foreign="CASH_OUT_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSONORDER">
		<reference local="ORDER_ID" foreign="ORDERID"/>
    </foreign-key>
</table>
	 */

	public static Vector
	getRecentOrders(UKOnlineCompanyBean _company, UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		// get any orders for this person that have not been "cashed out"

		/*
		 *

		if (enrollment_list.size() != 0)
		{
		    int[] arr = new int[enrollment_list.size()];
		    for (int i = 0; enrollment_list_itr.hasNext(); i++)
		    {
			EnrollmentRef obj = (EnrollmentRef)enrollment_list_itr.next();
			arr[i] = obj.getPersonid();
		    }

		    crit.add(PersonPeer.PERSONID, (Object)arr, Criteria.NOT_IN);
		}
		 */

		// just get the orders since the last cash out date

		Date last_cashout_date = CashOut.getLastCompletedCashOutDate(_company);
		System.out.println("last_cashout_date >" + last_cashout_date);

		if (last_cashout_date == null)
		{
			Calendar one_month_ago = Calendar.getInstance();
			one_month_ago.set(Calendar.MONTH, -1);
			last_cashout_date = one_month_ago.getTime();
		}

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		//crit.addJoin(CashOutDbPeer.CASH_OUT_DB_ID, CashOutDbOrderMappingPeer.CASH_OUT_DB_ID);
		//crit.addJoin(CashOutDbOrderMappingPeer.ORDER_ID, PersonorderPeer.ORDERID);
		crit.add(PersonorderPeer.PERSONID, _person.getId());
		if (last_cashout_date != null)
			crit.add(PersonorderPeer.ORDERDATE, last_cashout_date, Criteria.GREATER_THAN);
		crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
		System.out.println("getRecentOrders crit >" + crit.toString());
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}
		return vec;
	}

	public static Vector
	getRecentOrders(UKOnlinePersonBean _person, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		//crit.addJoin(CashOutDbPeer.CASH_OUT_DB_ID, CashOutDbOrderMappingPeer.CASH_OUT_DB_ID);
		//crit.addJoin(CashOutDbOrderMappingPeer.ORDER_ID, PersonorderPeer.ORDERID);
		crit.add(PersonorderPeer.PERSONID, _person.getId());
		if (_date != null)
			crit.add(PersonorderPeer.ORDERDATE, _date, Criteria.GREATER_THAN);
		crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
		System.out.println("getRecentOrders crit >" + crit.toString());
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}
		return vec;
	}
	
	public static Vector
	getRecentMostCommonPurchases(UKOnlinePersonBean _person, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		
		int highest_freq = 0;
		//HashMap<Integer,CheckoutCode> hash = new HashMap<Integer,CheckoutCode>();
		HashMap<CheckoutCode,Integer> freq_hash = new HashMap<CheckoutCode,Integer>();
		
		Iterator itr = ValeoOrderBean.getRecentOrders(_person, _date).iterator();
		while (itr.hasNext()) {
			ValeoOrderBean order = (ValeoOrderBean)itr.next();
			Iterator orders = order.getOrdersVec().iterator();
			while (orders.hasNext()) {
				CheckoutOrderline obj = (CheckoutOrderline)orders.next();
				int quantity = obj.getQuantity().intValue();
				CheckoutCode code = obj.getCheckoutCode();
				
				//hash.put(code.getCheckoutCodeId(), code);
				
				Integer existing_quantity = freq_hash.get(code);
				if (existing_quantity != null)
					quantity += existing_quantity;
				freq_hash.put(code, quantity);
					
				if (quantity > highest_freq)
					highest_freq = quantity;
			}
		
		}
		
		HashMap<Integer,Vector> freq_vec_hash = new HashMap<Integer,Vector>();
		Iterator keys = freq_hash.keySet().iterator();
		while (keys.hasNext()) {
			CheckoutCode key = (CheckoutCode)keys.next();
			Integer freq = freq_hash.get(key);
			
			Vector codes_with_freq = freq_vec_hash.get(freq);
			if (codes_with_freq == null) {
				codes_with_freq = new Vector();
				freq_vec_hash.put(freq, codes_with_freq);
			}
			
			codes_with_freq.addElement(key);
		}
		
		for (int i = highest_freq; i > 0; i--) {
			
			Vector codes_with_freq = freq_vec_hash.get(i);
			if (codes_with_freq != null)
				vec.addAll(codes_with_freq);
			
		}
		
		return vec;
		
	}
	
	public static Vector
	getRecentMostCommonPurchasesInventoryOnly(UKOnlinePersonBean _person, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		
		int highest_freq = 0;
		//HashMap<Integer,CheckoutCode> hash = new HashMap<Integer,CheckoutCode>();
		HashMap<CheckoutCode,Integer> freq_hash = new HashMap<CheckoutCode,Integer>();
		
		Iterator itr = ValeoOrderBean.getRecentOrders(_person, _date).iterator();
		while (itr.hasNext()) {
			ValeoOrderBean order = (ValeoOrderBean)itr.next();
			Iterator orders = order.getOrdersVec().iterator();
			while (orders.hasNext()) {
				CheckoutOrderline obj = (CheckoutOrderline)orders.next();
				int quantity = obj.getQuantity().intValue();
				CheckoutCode code = obj.getCheckoutCode();
				if (code.getType() == CheckoutCodeBean.INVENTORY_TYPE) {
				
					Integer existing_quantity = freq_hash.get(code);
					if (existing_quantity != null)
						quantity += existing_quantity;
					freq_hash.put(code, quantity);

					if (quantity > highest_freq)
						highest_freq = quantity;
				}
			}
		
		}
		
		HashMap<Integer,Vector> freq_vec_hash = new HashMap<Integer,Vector>();
		Iterator keys = freq_hash.keySet().iterator();
		while (keys.hasNext()) {
			CheckoutCode key = (CheckoutCode)keys.next();
			Integer freq = freq_hash.get(key);
			
			Vector codes_with_freq = freq_vec_hash.get(freq);
			if (codes_with_freq == null) {
				codes_with_freq = new Vector();
				freq_vec_hash.put(freq, codes_with_freq);
			}
			
			codes_with_freq.addElement(key);
		}
		
		for (int i = highest_freq; i > 0; i--) {
			
			Vector codes_with_freq = freq_vec_hash.get(i);
			if (codes_with_freq != null) {
				vec.addAll(codes_with_freq);
			}
			
		}
		
		return vec;
		
	}

	public static OrderBean
	getOrder(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		Object order_obj = hash.get(key);
		if (!(order_obj instanceof ValeoOrderBean))
			hash.remove(key);
		ValeoOrderBean order = (ValeoOrderBean)hash.get(key);
		if (order == null)
		{
			Criteria crit = new Criteria();
			crit.add(PersonorderPeer.ORDERID, _id);
			List objList = PersonorderPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate order with id: " + _id);

			order = (ValeoOrderBean)ValeoOrderBean.getOrder((Personorder)objList.get(0));
		}

		return order;
	}

    private static OrderBean
    getOrder(Personorder _order)
		throws TorqueException
    {
		Integer key = new Integer(_order.getOrderid());
		Object order_obj = hash.get(key);
		if (!(order_obj instanceof ValeoOrderBean))
			hash.remove(key);
		ValeoOrderBean order = (ValeoOrderBean)hash.get(key);
		if (order == null)
		{
			order = new ValeoOrderBean(_order);
			hash.put(key, order);
		}

		return order;
    }

	public static OrderBean
	getOrder(String _code)
		throws TorqueException, ObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(PersonorderPeer.AUTHORIZATIONCODE, _code);
		List objList = PersonorderPeer.doSelect(crit);
		if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate order with authorization code: " + _code);

		return ValeoOrderBean.getOrder((Personorder)objList.get(0));
	}
	
	public static OrderBean
	getLastOrder(UKOnlinePersonBean _person)
		throws TorqueException
	{
		Criteria crit = new Criteria();
		crit.add(PersonorderPeer.PERSONID, _person.getId());
		crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
		List obj_list = PersonorderPeer.doSelect(crit);
		if (obj_list.size() == 0) {
			return null;
		} else {
			return ValeoOrderBean.getOrder((Personorder)obj_list.get(0));
		}
	}

	public static Vector
	getOrders(UKOnlinePersonBean _person)
		throws TorqueException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(PersonorderPeer.PERSONID, _person.getId());
		crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}
		return vec;
	}

	public static Vector
	getOrders(Date _date)
		throws TorqueException
	{
		Calendar start_date = Calendar.getInstance();
		start_date.setTime(_date);
		start_date.set(Calendar.HOUR_OF_DAY, 0);
		start_date.set(Calendar.MINUTE, 0);
		start_date.set(Calendar.SECOND, 0);

		Calendar end_date = Calendar.getInstance();
		end_date.setTime(_date);
		end_date.set(Calendar.HOUR_OF_DAY, 23);
		end_date.set(Calendar.MINUTE, 59);
		end_date.set(Calendar.SECOND, 59);

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(PersonorderPeer.ORDERDATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(PersonorderPeer.ORDERDATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}
		return vec;
	}

	public static Vector
	getOrders(Date _start_date, Date _end_date)
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
		crit.add(PersonorderPeer.ORDERDATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(PersonorderPeer.ORDERDATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}
		return vec;
	}

	public static Vector
	getOrders(UKOnlinePersonBean _person, Date _start_date, Date _end_date)
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
		crit.add(PersonorderPeer.PERSONID, _person.getId());
		crit.add(PersonorderPeer.ORDERDATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(PersonorderPeer.ORDERDATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}
		return vec;
	}

	public static Vector
	getOrders(UKOnlineCompanyBean _company, Date _start_date, Date _end_date)
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
		crit.add(PersonorderPeer.COMPANY_ID, _company.getId());
		crit.add(PersonorderPeer.ORDERDATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(PersonorderPeer.ORDERDATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.addAscendingOrderByColumn(PersonorderPeer.ORDERDATE);
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}
		return vec;
	}

	public static Vector
	getOrdersForCashOut(UKOnlineCompanyBean _company, Date _start_date, Date _end_date)
		throws TorqueException
	{
		System.out.println("getOrdersForCashOut() invoked");

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
		crit.add(PersonorderPeer.COMPANY_ID, _company.getId());
		crit.add(PersonorderPeer.ORDERDATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(PersonorderPeer.ORDERDATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.addAscendingOrderByColumn(PersonorderPeer.ORDERDATE);
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			ValeoOrderBean cash_out_order = (ValeoOrderBean)ValeoOrderBean.getOrder(order_obj);

			System.out.println("ORDER >" + cash_out_order.getId());
			System.out.println("cash_out_order.isReversed() >" + cash_out_order.isReversed());
			System.out.println("cash_out_order.isLegacy() >" + cash_out_order.isLegacy());


			if (!cash_out_order.isReversed() && !cash_out_order.isLegacy())
				vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}
		return vec;
	}

	public static Vector
	getOrdersForResubmission(UKOnlineCompanyBean _company, Date _start_date, Date _end_date)
		throws TorqueException
	{
		System.out.println("getOrdersForResubmission() invoked");

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
		crit.add(PersonorderPeer.COMPANY_ID, _company.getId());
		crit.add(PersonorderPeer.ORDERDATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(PersonorderPeer.ORDERDATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.and(PersonorderPeer.IS_UPDATED, (short)0);
		crit.addAscendingOrderByColumn(PersonorderPeer.ORDERDATE);
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			ValeoOrderBean order_for_resubmission = (ValeoOrderBean)ValeoOrderBean.getOrder(order_obj);

			System.out.println("order_for_resubmission >" + order_for_resubmission.getId());
			System.out.println("order_for_resubmission.isReversed() >" + order_for_resubmission.isReversed());
			System.out.println("order_for_resubmission.isLegacy() >" + order_for_resubmission.isLegacy());

			if (!order_for_resubmission.isReversed() && !order_for_resubmission.isLegacy())
				vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}
		return vec;
	}

	public static CheckoutOrderline
	getCheckoutOrderline(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(CheckoutOrderlinePeer.CHECKOUT_ORDERLINE_ID, _id);
		List objList = CheckoutOrderlinePeer.doSelect(crit);
		if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate orderline with id: " + _id);

		return (CheckoutOrderline)objList.get(0);
	}

    public static Vector
    getOrdersForDate(UKOnlinePersonBean _person, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Calendar start_date = Calendar.getInstance();
		start_date.setTime(_date);
		start_date.set(Calendar.HOUR_OF_DAY, 0);
		start_date.set(Calendar.MINUTE, 0);
		start_date.set(Calendar.SECOND, 0);

		Calendar end_date = Calendar.getInstance();
		end_date.setTime(_date);
		end_date.set(Calendar.HOUR_OF_DAY, 23);
		end_date.set(Calendar.MINUTE, 59);
		end_date.set(Calendar.SECOND, 59);
		
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(PersonorderPeer.PERSONID, _person.getId());
		crit.add(PersonorderPeer.ORDERDATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(PersonorderPeer.ORDERDATE, end_date.getTime(), Criteria.LESS_EQUAL);
		Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			Personorder order_obj = (Personorder)obj_itr.next();
			vec.addElement(ValeoOrderBean.getOrder(order_obj));
		}

		return vec;
    }

	// INSTANCE VARIABLES

	private Vector tenders;
	private HashMap tender_payments;

	// CONSTRUCTORS

    /**
     * The base, no-arg constructor.
     */
    public ValeoOrderBean()
    {
		super();
    }

    /**
     * Construct a RoleBean from an existing Role.
     */
    public ValeoOrderBean(Personorder _order)
    {
		super(_order);
    }

	// INSTANCE METHODS

	/*
	public BigDecimal
	getBalance()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		System.out.println("getBalanceString() invoked in ValeoOrderBean");

		BigDecimal payments_on_order_total = CUBean.zero;

		// find the tenders for this order

		Iterator itr = TenderRet.getTenders(this).iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();
			if (!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
			{
				// ummm.  I'm gonna have to maintain the balance of the order.  this tender could be used for other orders, potentially

				payments_on_order_total = payments_on_order_total.add(tender.getAmountBigDecimal());
			}
		}

		System.out.println("this.getTotal() >" + this.getTotal());
		System.out.println("payments_on_order_total >" + payments_on_order_total);

		return this.getTotal().subtract(payments_on_order_total);
	}

	@Override
	public String
	getBalanceString()
		throws TorqueException
	{
		BigDecimal payments_on_order_total = CUBean.zero;

		// find the tenders for this order

		try
		{
			Iterator itr = TenderRet.getTenders(this).iterator();
			while (itr.hasNext())
			{
				TenderRet tender = (TenderRet)itr.next();
				if (!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
					payments_on_order_total = payments_on_order_total.add(tender.getAmountBigDecimal());
			}
		}
		catch(ObjectNotFoundException x)
		{
			x.printStackTrace();
		}
		catch(UniqueObjectNotFoundException x)
		{
			x.printStackTrace();
		}

		return this.getTotal().subtract(payments_on_order_total).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}
	 */
	
	public boolean
	equals(InvoiceRet _obj)
	{
		System.out.println("equals invoked in ValeoOrderBean");
		
		try
		{
			if (_obj == null)
				return false;

			// my strategy is to make sure that the order lines are equivalent.  have the same things been sold?

			// testing same date elsewhere.  may want to move it here as well depending on how this method gets used.
		
			HashMap<CheckoutCodeBean, BigDecimal> checkout_code_qty_hash = new HashMap<CheckoutCodeBean, BigDecimal>(3);
			Iterator orders = this.getOrders();
			while (orders.hasNext())
			{
				CheckoutOrderline orderline = (CheckoutOrderline)orders.next();
				try
				{
					CheckoutCodeBean key = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCode().getCheckoutCodeId());
					if (key.getType() == CheckoutCodeBean.INVENTORY_TYPE || key.getType() == CheckoutCodeBean.PROCEDURE_TYPE)
					{
						BigDecimal qty = checkout_code_qty_hash.get(key);
						if (qty == null)
							qty = BigDecimal.ZERO;
						qty = qty.add(orderline.getQuantity());
						checkout_code_qty_hash.put(key, qty);
					}
				}
				catch (ObjectNotFoundException x)
				{
					System.out.println(x.getMessage());
				}
			}
			
			HashMap<CheckoutCodeBean, BigDecimal> invoice_checkout_code_qty_hash = new HashMap<CheckoutCodeBean, BigDecimal>(3);
			Iterator invoice_line_ret_itr = _obj.getInvoiceLineRetObjects().iterator();
			while (invoice_line_ret_itr.hasNext())
			{
				InvoiceLineRet invoice_line = (InvoiceLineRet)invoice_line_ret_itr.next();
				String item_list_id = invoice_line.getItemListId();
				if (item_list_id != null)
				{
					try
					{
						CheckoutCodeBean key = CheckoutCodeBean.getCheckoutCodeByQBListID(this.getCompany(), item_list_id);
						if (key.getType() == CheckoutCodeBean.INVENTORY_TYPE || key.getType() == CheckoutCodeBean.PROCEDURE_TYPE)
						{
							// if the initial hash doesn't have this checkout code, then, no, this is not a match

							if (checkout_code_qty_hash.get(key) == null)
								return false;

							BigDecimal qty = invoice_checkout_code_qty_hash.get(key);
							if (qty == null)
								qty = BigDecimal.ZERO;
							qty = qty.add(new BigDecimal(invoice_line.getQuantity()));
							invoice_checkout_code_qty_hash.put(key, qty);
						}
					}
					catch (ObjectNotFoundException x)
					{
						System.out.println(x.getMessage());
					}
				}
			}
			
			System.out.println("compare hashes");
			
			// now compare the hashes
			
			Iterator keys = checkout_code_qty_hash.keySet().iterator();
			while (keys.hasNext())
			{
				CheckoutCodeBean key = (CheckoutCodeBean)keys.next();
				BigDecimal qty = checkout_code_qty_hash.get(key);
				BigDecimal alt_qty = invoice_checkout_code_qty_hash.get(key);
				if (alt_qty == null)
					return false;
				
				if (qty.compareTo(alt_qty) != 0)
					return false;
			}
		
			return true;
		}
		catch (Exception x)
		{
			System.out.println("equals invoice error >" + x.getMessage());
			x.printStackTrace();
		}
		
		return false;
	}

	public UKOnlinePersonBean
	getClient()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(this.getPersonId());
	}

	public Vector
	getTenders()
		throws TorqueException, ObjectNotFoundException
	{
		if (this.tenders == null)
		{
			this.tenders = new Vector();

			Criteria crit = new Criteria();
			crit.add(TenderRetDbOrderMappingPeer.ORDER_ID, this.getId());
			Iterator itr = TenderRetDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				TenderRetDbOrderMapping mapping_obj = (TenderRetDbOrderMapping)itr.next();
				TenderRet tender = TenderRet.getTender(mapping_obj.getTenderRetDbId());
				this.tenders.addElement(tender);
			}
		}
		return this.tenders;
	}

	private void
	saveTenders()
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

		if (this.tenders != null)
		{
			HashMap db_tenders_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(TenderRetDbOrderMappingPeer.ORDER_ID, this.getId());
			Iterator itr = TenderRetDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{

				TenderRetDbOrderMapping value = (TenderRetDbOrderMapping)itr.next();
				Integer key = new Integer(value.getTenderRetDbId());
				db_tenders_hash.put(key, value);

				System.out.println("found existing ass... >" + key.intValue());
			}

			System.out.println("num members >" + this.tenders.size());

			itr = this.tenders.iterator();
			while (itr.hasNext())
			{
				TenderRet tender = (TenderRet)itr.next();
				Integer key = new Integer(tender.getId());
				TenderRetDbOrderMapping obj = (TenderRetDbOrderMapping)db_tenders_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("association does not exist in db.  need to create >" + key.intValue());

					obj = new TenderRetDbOrderMapping();
					if (tender_payments != null)
					{
						BigDecimal amount_applied = (BigDecimal)tender_payments.get(tender);
						System.out.println("amount_applied >" + amount_applied);
						if (amount_applied != null)
							obj.setTenderAmountApplied(amount_applied);
					}
					obj.setTenderRetDbId(tender.getId());
					obj.setOrderId(this.getId());
					obj.save();
				}
			}

			itr = db_tenders_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				TenderRetDbOrderMapping obj = (TenderRetDbOrderMapping)db_tenders_hash.get(key);
				System.out.println("BLOCKING deleting map to tender >" + obj.getTenderRetDbId());
				//TenderRetDbOrderMappingPeer.doDelete(obj);
			}
		}
	}

	public void
	setTenders(Vector _tenders)
	{
		tenders = _tenders;

		Iterator itr = _tenders.iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();
			System.out.println("SETTING TENDER >" + tender.getValue() + " FOR ORDER >" + this.getValue());
		}
	}

	public void
	setTenderPaymentInfo(HashMap _tender_payments)
	{
		System.out.println("setTenderPaymentInfo invoked >" + _tender_payments);
		tender_payments = _tender_payments;
	}

	public boolean
	hasAccountTender()
		throws TorqueException, ObjectNotFoundException
	{
		Iterator itr = this.getTenders().iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();
			if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
				return true;
		}
		return false;
	}

	public boolean
	hasNSIPMTender()
		throws TorqueException, ObjectNotFoundException
	{
		Iterator itr = this.getTenders().iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();
			if (tender.getType().equals(TenderRet.NSIPM_TENDER_TYPE))
				return true;
		}
		return false;
	}

	public boolean
	hasCheckTender()
		throws TorqueException, ObjectNotFoundException
	{
		Iterator itr = this.getTenders().iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();
			if (tender.getType().equals(TenderRet.CHECK_TENDER_TYPE))
				return true;
		}
		return false;
	}

	public TenderRet
	getCheckTender()
		throws TorqueException, ObjectNotFoundException
	{
		Iterator itr = this.getTenders().iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();
			if (tender.getType().equals(TenderRet.CHECK_TENDER_TYPE))
				return tender;
		}
		throw new ObjectNotFoundException("Check tender not found for " + this.getLabel());
	}

	public boolean
	hasCashTender()
		throws TorqueException, ObjectNotFoundException
	{
		Iterator itr = this.getTenders().iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();
			if (tender.getType().equals(TenderRet.CASH_TENDER_TYPE))
				return true;
		}
		return false;
	}

	public TenderRet
	getCashTender()
		throws TorqueException, ObjectNotFoundException
	{
		Iterator itr = this.getTenders().iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();
			if (tender.getType().equals(TenderRet.CASH_TENDER_TYPE))
				return tender;
		}
		throw new ObjectNotFoundException("Check tender not found for " + this.getLabel());
	}

	/*
	public HashMap<AccountRet,BigDecimal>
	getAccountAdjustments()
		throws TorqueException, ObjectNotFoundException
	{
		// loop the stuff sold and see if by virtue of selling any of the checkout codes that an account needs modification

		HashMap<AccountRet,BigDecimal> account_hash = new HashMap(1);

		Iterator itr = this.getOrders();
		while (itr.hasNext())
		{
			CheckoutOrderline orderline = (CheckoutOrderline)itr.next();
			CheckoutCode thing_sold = orderline.getCheckoutCode();
			if (thing_sold.getType() == CheckoutCodeBean.GIFT_CARD)
			{
				// Gift Card Sold.  I need to modify (increase the value of) the "Gift Cards Outstanding" liability account
				// When a customer buys a gift card, the gift card unredeemed is credited (it's a liability account so crediting makes it larger) and undeposited funds is debited (increasing that account).

				System.out.println("Gift Card sale found");

				AccountRet cogs_account = AccountRet.getAccount(thing_sold.getCOGSAccountId());
				System.out.println("cogs_account >" + cogs_account.getLabel());

				BigDecimal amount_credited = (BigDecimal)account_hash.get(cogs_account);
				if (amount_credited == null)
					amount_credited = orderline.getActualAmount();
				else
					amount_credited = amount_credited.add(orderline.getActualAmount());

				account_hash.put(cogs_account, amount_credited);

				//AccountRet.getAcc
			}
		}

		return account_hash;

	}
	 *
	 */

    protected void
    insertObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		super.insertObject();
		this.saveTenders();
    }

	public void
	invalidate()
	{
		super.invalidate();
		this.tenders = null;
	}
	public boolean
	isOpen()
	{
		//return this.getStatus().equals(ValeoOrderBean.OPEN_ORDER_STATUS);
		return !this.isPaid();
	}

	public boolean
	isReversed()
	{
		return this.getStatus().equals(ValeoOrderBean.REVERSED_RECEIPT_ORDER_STATUS);
	}

	public void
	reverse()
		throws TorqueException
	{
		this.setStatus(ValeoOrderBean.REVERSED_RECEIPT_ORDER_STATUS);
	}

	public boolean
	isLegacy()
	{
		return this.getStatus().equals(ValeoOrderBean.LEGACY_ORDER_STATUS);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		super.updateObject();
		this.saveTenders();
    }
	
	public String
	getExtendedLabel()
	{
		
		String label = null;
		try
		{
			if (this.isLegacy())
				label = "PAYMENT ON ACCOUNT";
			else
			{
				Iterator itr = this.getOrdersVec().iterator();
				while (itr.hasNext())
				{
					CheckoutOrderline orderline = (CheckoutOrderline)itr.next();
					CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
					String code_label = code.getLabel();
					if (!code_label.equals("POS Subtotal"))
					{
						if (label == null)
							label = (this.isReversed() ? "REVERSED: " : (this.isReturn() ? "RETURN: " : (this.isOpen() ? "ON ACCOUNT: " : ""))) + code_label;
						else
							label += ", " + code_label;
					}
				}
			}
		}
		catch (Exception x)
		{
			label = "Error: " + x.getMessage();
		}

		if (label == null)
			label = (this.isReversed() ? "REVERSED" : (this.isReturn() ? "RETURN" : (this.isOpen() ? "ON ACCOUNT" : "")));
		
		return label;
	}

}
