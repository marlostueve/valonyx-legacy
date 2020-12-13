/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import com.valeo.qbms.data.QBMSCreditCardResponse;
import com.valeo.qbpos.data.TenderRet;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.math.BigDecimal;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
CashOut
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,CashOut> hash = new HashMap<Integer,CashOut>(11);

    // CLASS METHODS

    public static CashOut
    getCashOut(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		CashOut cash_out = (CashOut)hash.get(key);
		if (cash_out == null)
		{
			Criteria crit = new Criteria();
			crit.add(CashOutDbPeer.CASH_OUT_DB_ID, _id);
			List objList = CashOutDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Cash Out with id: " + _id);

			cash_out = CashOut.getCashOut((CashOutDb)objList.get(0));
		}

		return cash_out;
    }

    private static CashOut
    getCashOut(CashOutDb _cash_out)
		throws TorqueException
    {
		Integer key = new Integer(_cash_out.getCashOutDbId());
		CashOut cash_out = (CashOut)hash.get(key);
		if (cash_out == null)
		{
			cash_out = new CashOut(_cash_out);
			hash.put(key, cash_out);
		}

		return cash_out;
    }

    public static CashOut
    getOpenCashOut(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(CashOutDbPeer.COMPANY_ID, _company.getId());
		crit.add(CashOutDbPeer.IS_COMPLETE, 0);
		List objList = CashOutDbPeer.doSelect(crit);
		if (objList.size() == 1)
			return CashOut.getCashOut((CashOutDb)objList.get(0));
		else if (objList.size() == 0)
			throw new ObjectNotFoundException("No cash outs in progress.");
		else
			throw new UniqueObjectNotFoundException("Multiple cash outs in progress.");
    }

    public static Vector
    getCashOuts(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CashOutDbPeer.COMPANY_ID, _company.getId());
		List objList = CashOutDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			Iterator itr = objList.iterator();
			while (itr.hasNext())
				vec.addElement(CashOut.getCashOut((CashOutDb)itr.next()));
		}

		return vec;
    }

    public static Date
    getLastCompletedCashOutDate(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(CashOutDbPeer.COMPANY_ID, _company.getId());
		crit.add(CashOutDbPeer.IS_COMPLETE, 1);
		crit.addDescendingOrderByColumn(CashOutDbPeer.MODIFICATION_DATE);
		crit.setLimit(1); // added 11/20/18
		System.out.println("getLastCompletedCashOutDate crit >" + crit.toString());
		List objList = CashOutDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			CashOutDb obj = (CashOutDb)objList.get(0);
			//return obj.getModificationDate(); // removed 11/20/18
			return obj.getEndDate(); // added 11/20/18
		}
		return null;
    }

    public static Date
    getSuggestedCashOutStartDate(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		// find the last end date of a completed cash out

		Criteria crit = new Criteria();
		crit.add(CashOutDbPeer.COMPANY_ID, _company.getId());
		crit.add(CashOutDbPeer.IS_COMPLETE, 1);
		crit.addDescendingOrderByColumn(CashOutDbPeer.END_DATE);
		List objList = CashOutDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			CashOut last_cash_out = CashOut.getCashOut((CashOutDb)objList.get(0));
			Calendar last_cash_out_end_date = Calendar.getInstance();
			last_cash_out_end_date.setTime(last_cash_out.getEndDate());
			last_cash_out_end_date.add(Calendar.DATE, 1);
			return last_cash_out_end_date.getTime();
		}
		else
			return new Date();
    }

    // SQL

    /*
     *        <table name="CASH_OUT_DB" idMethod="native">
				<column name="CASH_OUT_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
				<column name="COMPANY_ID" required="true" type="INTEGER"/>
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
     */

    // INSTANCE VARIABLES

    private CashOutDb cash_out;
	private Vector orders;

	private boolean settle_merchant_account_in_progress = false;
	private boolean update_quickbooks_in_progress = false;
	
	private boolean allow_regen_stuff = true;

	//private HashMap<String,CashOutDrawerCountDb> workstation_drawer_counts = new HashMap<String,CashOutDrawerCountDb>();

	private String log_file_pdf_location;
	private Vector log;

    // CONSTRUCTORS

    public
    CashOut()
    {
		cash_out = new CashOutDb();
		isNew = true;
    }

    public
    CashOut(CashOutDb _cash_out)
    {
		cash_out = _cash_out;
		isNew = false;
    }

    // INSTANCE METHODS

	public void
	clearLog()
	{
		log = new Vector();
	}

	public boolean isAllow_regen_stuff() {
		return allow_regen_stuff;
	}

	public void setAllow_regen_stuff(boolean allow_regen_stuff) {
		this.allow_regen_stuff = allow_regen_stuff;
	}

	public boolean
	isQuickBooksUpdateInProgress()
	{
		return update_quickbooks_in_progress;
	}

	public void
	setIsQuickBooksUpdateInProgress(boolean _in_progress)
	{
		System.out.println("setIsQuickBooksUpdateInProgress(boolean) invoked >" + _in_progress);
		try {
			throw new IllegalValueException("draw a clear divide");
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		update_quickbooks_in_progress = _in_progress;
	}

	public boolean
	isMerchantAccountSettlementInProgress()
	{
		return settle_merchant_account_in_progress;
	}

	public void
	setIsMerchantAccountSettlementInProgress(boolean _in_progress)
	{
		settle_merchant_account_in_progress = _in_progress;
	}

	UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(cash_out.getCompanyId());
	}

	public short
	getCountHundreds(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountHundreds();
	}

	public short
	getCountFifties(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountFifties();
	}

	public short
	getCountTwenties(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountTwenties();
	}

	public short
	getCountTens(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountTens();
	}

	public short
	getCountFives(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountFives();
	}

	public short
	getCountOnes(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountOnes();
	}

	public short
	getCountHalfDollars(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountHalfDollars();
	}

	public short
	getCountQuarters(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountQuarters();
	}

	public short
	getCountDimes(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountDimes();
	}

	public short
	getCountNickels(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountNickels();
	}

	public short
	getCountPennies(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountPennies();
	}

	public BigDecimal
	getBeginAmount(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getBeginAmount();
	}

	public BigDecimal
	getCountAmount(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getCountAmount();
	}

	public BigDecimal
	getDepositAmount(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getDepositAmount();
	}

	public BigDecimal
	getLeaveAmount(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getLeaveAmount();
	}

	public Vector
	getLog()
	{
		if (log == null)
			log = new Vector();
		return log;
	}

	public String
	getLogFilePDFLocation()
	{
		return log_file_pdf_location;
	}

	public BigDecimal
	getPaidOutAmount(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = this.getDrawerCountForWorkstation(_workstation);
		return drawer_count.getPaidOutAmount();
	}

	private CashOutDrawerCountDb
	getDrawerCountForWorkstation(String _workstation)
		throws TorqueException
	{
		CashOutDrawerCountDb drawer_count = null;
		Criteria crit = new Criteria();
		crit.add(CashOutDrawerCountDbPeer.CASH_OUT_DB_ID, this.getId());
		crit.add(CashOutDrawerCountDbPeer.WORKSTATION_NAME, _workstation);
		List obj_list = CashOutDrawerCountDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			drawer_count = (CashOutDrawerCountDb)obj_list.get(0);
		else
		{
			drawer_count = new CashOutDrawerCountDb();
			drawer_count.setCashOutDbId(this.getId());
			drawer_count.setWorkstationName(_workstation);
		}
		return drawer_count;
	}
	
	public Date
	getEndDate()
	{
		return cash_out.getEndDate();
	}

    public int
    getId()
    {
		return cash_out.getCashOutDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return this.getCompany().getLabel() + " Cash Out";
    }

	public Vector
	getAllUncapturedResponses()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		System.out.println("getAllUncapturedResponses() invoked in CashOut");

		Vector vec = new Vector();
		//Iterator itr = QBMSCreditCardResponse.getResponses(this.getCompany(), QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE).iterator();
		Vector responses = QBMSCreditCardResponse.getUncapturedAndUnvoidedAndUnrefundedResponses(this.getCompany());
		System.out.println("num candidate responses >" + responses.size());
		Iterator itr = responses.iterator();
		
			// this is returning every transaction that has ever been authorized, apparently
		while (itr.hasNext())
		{
			QBMSCreditCardResponse auth_response = (QBMSCreditCardResponse)itr.next();
			if (auth_response.hasTender())
			{
				try
				{
					TenderRet tender_for_cc_auth_response = auth_response.getTender();
					Vector orders_connected_to_tender = tender_for_cc_auth_response.getOrders();

					int num_orders_connected_to_tender = orders_connected_to_tender.size();
					int num_reversed_orders_connected_to_tender = 0;
					BigDecimal total_tender_amount_connected_to_reversed_order = BigDecimal.ZERO;

					System.out.println("num_orders_connected_to_tender >" + num_orders_connected_to_tender);

					Iterator order_itr = tender_for_cc_auth_response.getOrders().iterator();
					while (order_itr.hasNext())
					{
						ValeoOrderBean order_for_tender = (ValeoOrderBean)order_itr.next();
						if (order_for_tender.isReversed())
						{
							// the tender for this response is connected to an order that has been reversed.

							System.out.println("the tender for this response is connected to an order that has been reversed");

							num_reversed_orders_connected_to_tender++;
							total_tender_amount_connected_to_reversed_order = total_tender_amount_connected_to_reversed_order.add(tender_for_cc_auth_response.getAmountMappedToOrder(order_for_tender));
						}
					}

					System.out.println("num_reversed_orders_connected_to_tender >" + num_reversed_orders_connected_to_tender);
					System.out.println("total_tender_amount_connected_to_reversed_order >" + total_tender_amount_connected_to_reversed_order.toString());
					System.out.println("tender id >" + tender_for_cc_auth_response.getId());
					System.out.println("tender amount >" + tender_for_cc_auth_response.getAmountString());

					// what if

					if (num_reversed_orders_connected_to_tender == 0)
					{
						if (!QBMSCreditCardResponse.hasBeenCapturedOrVoided(auth_response))
							vec.addElement(auth_response);
					}
					else
					{
						StringBuffer buf = new StringBuffer();
						buf.append("num_orders_connected_to_tender >" + num_orders_connected_to_tender + "\r\n");
						buf.append("num_reversed_orders_connected_to_tender >" + num_reversed_orders_connected_to_tender + "\r\n");
						buf.append("total_tender_amount_connected_to_reversed_order >" + total_tender_amount_connected_to_reversed_order.toString() + "\r\n");
						buf.append("tender id >" + tender_for_cc_auth_response.getId() + "\r\n");
						buf.append("tender amount >" + tender_for_cc_auth_response.getAmountString() + "\r\n");
						CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "SETTLEMENT TENDER FOUND FOR REVERSED RECEIPT", buf.toString());
					}
				}
				catch (Exception x)
				{
					try
					{
						x.printStackTrace();
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						x.printStackTrace(pw);
						CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", x.getMessage(), sw.toString());
					}
					catch (Exception y)
					{
						System.out.println("really?!?!");
						y.printStackTrace();
					}
				}
			}
		}
		return vec;
	}

	public Vector
	getUncapturedResponses()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Iterator itr = QBMSCreditCardResponse.getResponses(this.getCompany(), this.getStartDate(), this.getEndDate(), QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE).iterator();
		while (itr.hasNext())
		{
			QBMSCreditCardResponse auth_response = (QBMSCreditCardResponse)itr.next();
			if (auth_response.hasTender())
			{
				if (!QBMSCreditCardResponse.hasBeenCaptured(auth_response))
					vec.addElement(auth_response);
			}
		}
		return vec;
	}

	public Vector
	getAllOrphanedUncapturedResponses()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Iterator itr = QBMSCreditCardResponse.getResponses(this.getCompany(), QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE).iterator();
		while (itr.hasNext())
		{
			QBMSCreditCardResponse auth_response = (QBMSCreditCardResponse)itr.next();
			if (!auth_response.hasTender())
			{
				if ((auth_response.getCreditCardTransId() != null) && (auth_response.getCreditCardTransId().length() > 0))
				{
					if (!QBMSCreditCardResponse.hasBeenCaptured(auth_response))
						vec.addElement(auth_response);
				}
			}
		}
		return vec;
	}

	public Vector
	getOrphanedUncapturedResponses()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Iterator itr = QBMSCreditCardResponse.getResponses(this.getCompany(), this.getStartDate(), this.getEndDate(), QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE).iterator();
		while (itr.hasNext())
		{
			QBMSCreditCardResponse auth_response = (QBMSCreditCardResponse)itr.next();
			if (!auth_response.hasTender())
			{
				if ((auth_response.getCreditCardTransId() != null) && (auth_response.getCreditCardTransId().length() > 0))
				{
					if (!QBMSCreditCardResponse.hasBeenCaptured(auth_response))
						vec.addElement(auth_response);
				}
			}
		}
		return vec;
	}

	public Vector
	getOrders()
		throws TorqueException, ObjectNotFoundException
	{
		if (orders == null)
		{
			orders = new Vector();

			Criteria crit = new Criteria();
			crit.add(CashOutDbOrderMappingPeer.CASH_OUT_DB_ID, this.getId());
			Iterator itr = CashOutDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				CashOutDbOrderMapping obj = (CashOutDbOrderMapping)itr.next();
				orders.addElement(ValeoOrderBean.getOrder(obj.getOrderId()));
			}
		}
		return orders;
	}

	public Vector
	getOrders(String _workstation_name)
		throws TorqueException, ObjectNotFoundException
	{
		Vector workstation_orders = new Vector();

		System.out.println("_workstation_name >" + _workstation_name);
		Iterator itr = this.getOrders().iterator();
		while (itr.hasNext())
		{
			ValeoOrderBean order = (ValeoOrderBean)itr.next();
			System.out.println("order workstation >" + order.getWorkstationName());
			if ((order.getWorkstationName() != null) && order.getWorkstationName().equals(_workstation_name))
				workstation_orders.addElement(order);
		}

		return workstation_orders;
	}

	public Date
	getStartDate()
	{
		return cash_out.getStartDate();
	}

    public String
    getValue()
    {
		return cash_out.getCashOutDbId() + "";
    }

    protected void
    insertObject()
		throws Exception
    {
		// Ensure that there isn't another open CashOut

		try
		{
			CashOut.getOpenCashOut(this.getCompany());
			throw new IllegalValueException("Cash out already in progress.");
		}
		catch (ObjectNotFoundException x)
		{
			// there shouldn't be any

			cash_out.setCreationDate(new Date());
			cash_out.save();

			this.saveOrders();
		}
    }

	public boolean
	isComplete()
	{
		return cash_out.getIsComplete() == (short)1;
	}

	private void
    saveOrders()
		throws TorqueException, Exception
    {
		/*
		 *<table name="CASH_OUT_DB_ORDER_MAPPING">
				<column name="CASH_OUT_DB_ID" required="true" type="INTEGER"/>
				<column name="ORDER_ID" required="true" type="INTEGER"/>

				<foreign-key foreignTable="CASH_OUT_DB">
					<reference local="CASH_OUT_DB_ID" foreign="CASH_OUT_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="PERSONORDER">
					<reference local="ORDER_ID" foreign="ORDERID"/>
				</foreign-key>
			</table>
		 */

		if (this.orders != null)
		{
			//System.out.println("sizer >" + this.orders.size());

			HashMap db_order_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(CashOutDbOrderMappingPeer.CASH_OUT_DB_ID, this.getId());
			Iterator itr = CashOutDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				//System.out.println("found existing ass...");
				CashOutDbOrderMapping value = (CashOutDbOrderMapping)itr.next();
				Integer key = new Integer(value.getOrderId());
				db_order_hash.put(key, value);
			}

			//System.out.println("num orders >" + this.orders.size());
			itr = this.orders.iterator();
			while (itr.hasNext())
			{
				ValeoOrderBean order = (ValeoOrderBean)itr.next();
				Integer key = new Integer(order.getId());
				CashOutDbOrderMapping obj = (CashOutDbOrderMapping)db_order_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					//System.out.println("creating new ass for " + orderline);

					obj = new CashOutDbOrderMapping();
					obj.setCashOutDbId(this.getId());
					obj.setOrderId(key.intValue());
					obj.save();

				}
			}

			itr = db_order_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				CashOutDbOrderMapping obj = (CashOutDbOrderMapping)db_order_hash.get(key);
				//System.out.println("obj.getCashOutDbId() >" + obj.getCashOutDbId());
				//System.out.println("obj.getOrderId() >" + obj.getOrderId());
				CashOutDbOrderMappingPeer.doDelete(obj);
			}
		}
    }

    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			cash_out.setCreatePersonId(_person.getId());
		else
			cash_out.setModifyPersonId(_person.getId());
    }

    public void
    setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
    {
		cash_out.setCompanyId(_company.getId());
    }

	public void
	setDrawerCountDollars(UKOnlinePersonBean _counter, String _workstation, BigDecimal _begin_amount, BigDecimal _paid_out_amount, BigDecimal _count_amount, BigDecimal _leave_amount, BigDecimal _deposit_amount)
		throws TorqueException, Exception
	{
		// does a drawer count already exist for this cash out on this workstation?

		CashOutDrawerCountDb drawer_count = null;

		Criteria crit = new Criteria();
		crit.add(CashOutDrawerCountDbPeer.CASH_OUT_DB_ID, this.getId());
		crit.add(CashOutDrawerCountDbPeer.WORKSTATION_NAME, _workstation);
		List obj_list = CashOutDrawerCountDbPeer.doSelect(crit);
		if (obj_list.size() == 0)
		{
			drawer_count = new CashOutDrawerCountDb();
			drawer_count.setCashOutDbId(this.getId());
			drawer_count.setWorkstationName(_workstation);
			drawer_count.setCreatePersonId(_counter.getId());
			drawer_count.setCreationDate(new Date());
		}
		else
		{
			drawer_count = (CashOutDrawerCountDb)obj_list.get(0);
			drawer_count.setModifyPersonId(_counter.getId());
			drawer_count.setModificationDate(new Date());
		}

		drawer_count.setBeginAmount(_begin_amount);
		drawer_count.setPaidOutAmount(_paid_out_amount);
		drawer_count.setCountAmount(_count_amount);
		drawer_count.setLeaveAmount(_leave_amount);
		drawer_count.setDepositAmount(_deposit_amount);

		drawer_count.save();
	}

	public void
	setDrawerCount(UKOnlinePersonBean _counter, String _workstation, boolean _primary, short _hundreds, short _fifties, short _twenties, short _tens, short _fives, short _ones, short _half_dollars, short _quarters, short _dimes, short _nickels, short _pennies)
		throws TorqueException, Exception
	{
		// does a drawer count already exist for this cash out on this workstation?

		CashOutDrawerCountDb drawer_count = null;

		Criteria crit = new Criteria();
		crit.add(CashOutDrawerCountDbPeer.CASH_OUT_DB_ID, this.getId());
		crit.add(CashOutDrawerCountDbPeer.WORKSTATION_NAME, _workstation);
		List obj_list = CashOutDrawerCountDbPeer.doSelect(crit);
		if (obj_list.size() == 0)
		{
			drawer_count = new CashOutDrawerCountDb();
			drawer_count.setCashOutDbId(this.getId());
			drawer_count.setWorkstationName(_workstation);
			drawer_count.setCreatePersonId(_counter.getId());
			drawer_count.setCreationDate(new Date());
		}
		else
		{
			drawer_count = (CashOutDrawerCountDb)obj_list.get(0);
			drawer_count.setModifyPersonId(_counter.getId());
			drawer_count.setModificationDate(new Date());
		}

		drawer_count.setIsPrimary(_primary ? (short)1 : (short)0);

		drawer_count.setCountHundreds(_hundreds);
		drawer_count.setCountFifties(_fifties);
		drawer_count.setCountTwenties(_twenties);
		drawer_count.setCountTens(_tens);
		drawer_count.setCountFives(_fives);
		drawer_count.setCountOnes(_ones);
		drawer_count.setCountHalfDollars(_half_dollars);
		drawer_count.setCountQuarters(_quarters);
		drawer_count.setCountDimes(_dimes);
		drawer_count.setCountNickels(_nickels);
		drawer_count.setCountPennies(_pennies);

		drawer_count.save();
	}

	public void
	setEndDate(Date _end_date)
	{
		cash_out.setEndDate(_end_date);
	}

	public void
	setIsComplete(boolean _b)
	{
		cash_out.setIsComplete(_b ? (short)1 : (short)0);
	}

	public void
	setLogFilePDFLocation(String _location)
	{
		log_file_pdf_location = _location;
	}

	public void
	setOrders(Vector _orders)
	{
		orders = _orders;
	}

	public void
	setStartDate(Date _start_date)
	{
		cash_out.setStartDate(_start_date);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		cash_out.setModificationDate(new Date());
		cash_out.save();

		this.saveOrders();
    }
}