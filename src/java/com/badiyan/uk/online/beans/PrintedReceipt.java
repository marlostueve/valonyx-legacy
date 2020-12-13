/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import com.valeo.qbpos.data.TenderRet;

import java.math.BigDecimal;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
PrintedReceipt
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,PrintedReceipt> hash = new HashMap<Integer,PrintedReceipt>(11);

    // CLASS METHODS

    public static PrintedReceipt
    getPrintedReceipt(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		PrintedReceipt printed_receipt = (PrintedReceipt)hash.get(key);
		if (printed_receipt == null) {
			Criteria crit = new Criteria();
			crit.add(PrintedReceiptDbPeer.PRINTED_RECEIPT_DB_ID, _id);
			List objList = PrintedReceiptDbPeer.doSelect(crit);
			if (objList.isEmpty())
				throw new ObjectNotFoundException("Could not locate Printed Receipt with id: " + _id);

			printed_receipt = PrintedReceipt.getPrintedReceipt((PrintedReceiptDb)objList.get(0));
		}

		return printed_receipt;
    }

    private static PrintedReceipt
    getPrintedReceipt(PrintedReceiptDb _printed_receipt)
		throws TorqueException
    {
		Integer key = new Integer(_printed_receipt.getPrintedReceiptDbId());
		PrintedReceipt printed_receipt = (PrintedReceipt)hash.get(key);
		if (printed_receipt == null)
		{
			printed_receipt = new PrintedReceipt(_printed_receipt);
			hash.put(key, printed_receipt);
		}

		return printed_receipt;
    }

    public static PrintedReceipt
    getPrintedReceipt(ValeoOrderBean _primary_order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		if (_primary_order.isNew())
			throw new ObjectNotFoundException("Could not locate Printed Receipt for new order.");
		
		Criteria crit = new Criteria();
		crit.add(PrintedReceiptDbPeer.PRIMARY_ORDER_ID, _primary_order.getId());
		List objList = PrintedReceiptDbPeer.doSelect(crit);
		if (objList.size() == 1)
			return PrintedReceipt.getPrintedReceipt((PrintedReceiptDb)objList.get(0));
		else if (objList.isEmpty())
			throw new ObjectNotFoundException("Could not locate Printed Receipt for order: " + _primary_order.getLabel());
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Printed Receipt for order: " + _primary_order.getLabel());
    }

    public static Vector
    getPrintedReceipts(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(PersonorderPeer.ORDERID, PrintedReceiptDbPeer.PRIMARY_ORDER_ID);
		crit.add(PrintedReceiptDbPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
		List objList = PrintedReceiptDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			Iterator itr = objList.iterator();
			while (itr.hasNext())
				vec.addElement(PrintedReceipt.getPrintedReceipt((PrintedReceiptDb)itr.next()));
		}

		return vec;
    }

    public static Vector
    getPrintedReceipts(UKOnlinePersonBean _person, Date _start_date, Date _end_date)
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
		crit.add(PrintedReceiptDbPeer.PERSON_ID, _person.getId());
		crit.add(PrintedReceiptDbPeer.RECEIPT_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(PrintedReceiptDbPeer.RECEIPT_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.addDescendingOrderByColumn(PrintedReceiptDbPeer.RECEIPT_DATE);
		List objList = PrintedReceiptDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			Iterator itr = objList.iterator();
			while (itr.hasNext())
				vec.addElement(PrintedReceipt.getPrintedReceipt((PrintedReceiptDb)itr.next()));
		}

		return vec;
    }

    public static Vector
    getPrintedReceipts(UKOnlineCompanyBean _company, int _num)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		/*
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
		*/
		
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PrintedReceiptDbPeer.PERSON_ID);
		//crit.add(PrintedReceiptDbPeer., _person.getId());
		//crit.add(PrintedReceiptDbPeer.PERSON_ID, _person.getId());
		crit.add(CompanyPeer.COMPANYID, _company.getId());
		//crit.add(PrintedReceiptDbPeer.RECEIPT_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		//crit.and(PrintedReceiptDbPeer.RECEIPT_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.addDescendingOrderByColumn(PrintedReceiptDbPeer.RECEIPT_DATE);
		crit.setLimit(_num);
		List objList = PrintedReceiptDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			Iterator itr = objList.iterator();
			while (itr.hasNext())
				vec.addElement(PrintedReceipt.getPrintedReceipt((PrintedReceiptDb)itr.next()));
		}

		return vec;
    }

    public static PrintedReceipt
    getLastCompletedPrintedReceipt(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.addJoin(PersonorderPeer.ORDERID, PrintedReceiptDbPeer.PRIMARY_ORDER_ID);
		crit.add(PrintedReceiptDbPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(PersonorderPeer.ORDERDATE);
		System.out.println("getLastCompletedPrintedReceipt crit >" + crit.toString());
		List objList = PrintedReceiptDbPeer.doSelect(crit);
		if (objList.isEmpty())
		{
			crit = new Criteria();
			crit.add(PrintedReceiptDbPeer.PERSON_ID, _person.getId());
			crit.addDescendingOrderByColumn(PrintedReceiptDbPeer.RECEIPT_DATE);
			objList = PrintedReceiptDbPeer.doSelect(crit);
			if (objList.isEmpty())
				throw new ObjectNotFoundException("Could not locate Printed Receipt for : " + _person.getLabel());
		}

		return PrintedReceipt.getPrintedReceipt((PrintedReceiptDb)objList.get(0));
    }

	/*
    public static Date
    getSuggestedPrintedReceiptStartDate(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		// find the last end date of a completed Printed Receipt

		Criteria crit = new Criteria();
		crit.add(PrintedReceiptDbPeer.COMPANY_ID, _company.getId());
		crit.add(PrintedReceiptDbPeer.IS_COMPLETE, 1);
		crit.addDescendingOrderByColumn(PrintedReceiptDbPeer.END_DATE);
		List objList = PrintedReceiptDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			PrintedReceipt last_printed_receipt = PrintedReceipt.getPrintedReceipt((PrintedReceiptDb)objList.get(0));
			Calendar last_printed_receipt_end_date = Calendar.getInstance();
			last_printed_receipt_end_date.setTime(last_printed_receipt.getEndDate());
			last_printed_receipt_end_date.add(Calendar.DATE, 1);
			return last_printed_receipt_end_date.getTime();
		}
		else
			return new Date();
    }
	 */

    // SQL

    /*
     *        <table name="PRINTED_RECEIPT_DB" idMethod="native">
				<column name="PRINTED_RECEIPT_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
				<column name="PERSON_ID" required="true" type="INTEGER"/>
				<column name="PRIMARY_ORDER_ID" required="true" type="INTEGER"/>

				<column name="SUBTOTAL" required="true" scale="2" size="7" type="DECIMAL"/>
				<column name="TAX" required="true" scale="2" size="7" type="DECIMAL"/>
				<column name="TOTAL" required="true" scale="2" size="7" type="DECIMAL"/>

				<foreign-key foreignTable="PERSON">
					<reference local="PERSON_ID" foreign="PERSONID"/>
				</foreign-key>
				<foreign-key foreignTable="PERSONORDER">
					<reference local="PRIMARY_ORDER_ID" foreign="ORDERID"/>
				</foreign-key>
			</table>

			<table name="PRINTED_RECEIPT_DB_ORDER_MAPPING">
				<column name="PRINTED_RECEIPT_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
				<column name="PREVIOUS_ORDER_ID" primaryKey="true" required="true" type="INTEGER"/>

				<foreign-key foreignTable="PRINTED_RECEIPT_DB">
					<reference local="PRINTED_RECEIPT_DB_ID" foreign="PRINTED_RECEIPT_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="PERSONORDER">
					<reference local="PREVIOUS_ORDER_ID" foreign="ORDERID"/>
				</foreign-key>
			</table>

			<table name="PRINTED_RECEIPT_DB_TENDER_MAPPING">
				<column name="PRINTED_RECEIPT_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
				<column name="TENDER_RET_DB_ID" primaryKey="true" required="true" type="INTEGER"/>

				<foreign-key foreignTable="PRINTED_RECEIPT_DB">
					<reference local="PRINTED_RECEIPT_DB_ID" foreign="PRINTED_RECEIPT_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="TENDER_RET_DB">
					<reference local="TENDER_RET_DB_ID" foreign="TENDER_RET_DB_ID"/>
				</foreign-key>
			</table>
     */

    // INSTANCE VARIABLES

    private PrintedReceiptDb printed_receipt;
	private ValeoOrderBean primary_order;
	private Vector previous_orders;
	private Vector tenders;
	
	private String printedReceiptURL;

    // CONSTRUCTORS

    public
    PrintedReceipt()
    {
		printed_receipt = new PrintedReceiptDb();
		isNew = true;
    }

    public
    PrintedReceipt(PrintedReceiptDb _printed_receipt)
    {
		printed_receipt = _printed_receipt;
		isNew = false;
    }

    // INSTANCE METHODS

	public String getPrintedReceiptURL() {
		return printedReceiptURL;
	}

	public void setPrintedReceiptURL(String printedReceiptURL) {
		this.printedReceiptURL = printedReceiptURL;
	}
	
	public Date
	getReceiptDate() {
		return printed_receipt.getReceiptDate();
	}

	public UKOnlinePersonBean
	getCashier()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(printed_receipt.getCashierId());
	}

	public void
	setCashier(UKOnlinePersonBean _person)
		throws TorqueException
	{
		printed_receipt.setCashierId(_person.getId());
	}

	public UKOnlinePersonBean
	getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(printed_receipt.getPersonId());
	}

	public void
	setPerson(UKOnlinePersonBean _person)
		throws TorqueException
	{
		printed_receipt.setPersonId(_person.getId());
	}

	public void
	setPrimaryOrder(ValeoOrderBean _primary_order)
		throws TorqueException
	{
		printed_receipt.setPrimaryOrderId(_primary_order.getId());
	}

	public boolean
	hasPrimaryOrder()
		throws TorqueException, ObjectNotFoundException
	{
		return (printed_receipt.getPrimaryOrderId() > 0);
	}

	public ValeoOrderBean
	getPrimaryOrder()
		throws TorqueException, ObjectNotFoundException
	{
		return (ValeoOrderBean)ValeoOrderBean.getOrder(printed_receipt.getPrimaryOrderId());
	}

	public BigDecimal
	getSubTotal()
		throws TorqueException
	{
		return printed_receipt.getSubtotal();
	}

	public BigDecimal
	getTax()
		throws TorqueException
	{
		return printed_receipt.getTax();
	}

	public BigDecimal
	getTotal()
		throws TorqueException
	{
		return printed_receipt.getTotal();
	}

	public void
	setSubtotal(BigDecimal _v)
		throws TorqueException
	{
		printed_receipt.setSubtotal(_v);
	}

	public void
	setTax(BigDecimal _v)
		throws TorqueException
	{
		printed_receipt.setTax(_v);
	}

	public void
	setTotal(BigDecimal _v)
		throws TorqueException
	{
		printed_receipt.setTotal(_v);
	}

	/*
	public BigDecimal
	getDiscount()
	{
		return printed_receipt.getDiscount();
	}

	public BigDecimal
	getDiscountPercentage()
	{
		return printed_receipt.getDiscountPercentage();
	}

	public void
	setDiscount(BigDecimal _v)
		throws TorqueException
	{
		printed_receipt.setDiscount(_v);
	}

	public void
	setDiscountPercentage(BigDecimal _v)
		throws TorqueException
	{
		printed_receipt.setDiscountPercentage(_v);
	}
	*/
	
	public BigDecimal
	getDiscountProducts()
	{
		return printed_receipt.getDiscountProduct();
	}

	public BigDecimal
	getDiscountPercentageProducts()
	{
		return printed_receipt.getDiscountPercentageProduct();
	}

	public void
	setDiscountProducts(BigDecimal _v)
		throws TorqueException
	{
		printed_receipt.setDiscountProduct(_v);
	}

	public void
	setDiscountPercentageProducts(BigDecimal _v)
		throws TorqueException
	{
		printed_receipt.setDiscountPercentageProduct(_v);
	}
	
	
	
	
	
	public BigDecimal
	getDiscountServices()
	{
		return printed_receipt.getDiscountServices();
	}

	public BigDecimal
	getDiscountPercentageServices()
	{
		return printed_receipt.getDiscountPercentageServices();
	}

	public void
	setDiscountServices(BigDecimal _v)
		throws TorqueException
	{
		printed_receipt.setDiscountServices(_v);
	}

	public void
	setDiscountPercentageServices(BigDecimal _v)
		throws TorqueException
	{
		printed_receipt.setDiscountPercentageServices(_v);
	}
	
	
	

	public void
	setShipping(BigDecimal _v)
		throws TorqueException
	{
		printed_receipt.setShipping(_v);
	}

	public BigDecimal
	getShipping()
	{
		return printed_receipt.getShipping();
	}

	public Vector
	getOrders()
		throws TorqueException, ObjectNotFoundException
	{
		if (previous_orders == null)
		{
			previous_orders = new Vector();

			Criteria crit = new Criteria();
			crit.add(PrintedReceiptDbOrderMappingPeer.PRINTED_RECEIPT_DB_ID, this.getId());
			Iterator itr = PrintedReceiptDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				PrintedReceiptDbOrderMapping obj = (PrintedReceiptDbOrderMapping)itr.next();
				previous_orders.addElement(ValeoOrderBean.getOrder(obj.getPreviousOrderId()));
			}
		}
		return previous_orders;
	}

	public Vector
	getTenders()
		throws TorqueException, ObjectNotFoundException
	{
		if (tenders == null)
		{
			tenders = new Vector();

			Criteria crit = new Criteria();
			crit.add(PrintedReceiptDbTenderMappingPeer.PRINTED_RECEIPT_DB_ID, this.getId());
			Iterator itr = PrintedReceiptDbTenderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				PrintedReceiptDbTenderMapping obj = (PrintedReceiptDbTenderMapping)itr.next();
				tenders.addElement(TenderRet.getTender(obj.getTenderRetDbId()));
			}
		}
		return tenders;
	}

    public int
    getId()
    {
		return printed_receipt.getPrintedReceiptDbId();
    }

    public String
    getLabel()
    {
		return printed_receipt.getPrintedReceiptDbId() + "";
    }

    public String
    getValue()
    {
		return printed_receipt.getPrintedReceiptDbId() + "";
    }

    protected void
    insertObject()
		throws Exception
    {
		System.out.println("insertObject() invoked in PrintedReceipt");
		
		printed_receipt.setReceiptDate(new Date());
		
		printed_receipt.setDiscount(BigDecimal.ZERO);
		printed_receipt.setDiscountPercentage(BigDecimal.ZERO);
		
		printed_receipt.save();
		this.saveOrders();
		this.saveTenders();
    }

	private void
    saveOrders()
		throws TorqueException, Exception
    {
		/*
		 *<table name="PRINTED_RECEIPT_DB_ORDER_MAPPING">
				<column name="PRINTED_RECEIPT_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
				<column name="PREVIOUS_ORDER_ID" primaryKey="true" required="true" type="INTEGER"/>

				<foreign-key foreignTable="PRINTED_RECEIPT_DB">
					<reference local="PRINTED_RECEIPT_DB_ID" foreign="PRINTED_RECEIPT_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="PERSONORDER">
					<reference local="PREVIOUS_ORDER_ID" foreign="ORDERID"/>
				</foreign-key>
			</table>
		 */

		if (this.previous_orders != null)
		{
			System.out.println("saveOrders sizer >" + this.previous_orders.size());
			System.out.println("this.getId() >" + this.getId());

			HashMap db_order_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(PrintedReceiptDbOrderMappingPeer.PRINTED_RECEIPT_DB_ID, this.getId());
			Iterator itr = PrintedReceiptDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				PrintedReceiptDbOrderMapping value = (PrintedReceiptDbOrderMapping)itr.next();
				Integer key = new Integer(value.getPreviousOrderId());
				db_order_hash.put(key, value);
			}

			//System.out.println("num orders >" + this.orders.size());
			itr = this.previous_orders.iterator();
			while (itr.hasNext())
			{
				ValeoOrderBean order = (ValeoOrderBean)itr.next();
				Integer key = new Integer(order.getId());
				PrintedReceiptDbOrderMapping obj = (PrintedReceiptDbOrderMapping)db_order_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("creating new ass for " + order.getValue());

					obj = new PrintedReceiptDbOrderMapping();
					obj.setPrintedReceiptDbId(this.getId());
					obj.setPreviousOrderId(key.intValue());
					obj.save();

				}
			}

			itr = db_order_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				PrintedReceiptDbOrderMapping obj = (PrintedReceiptDbOrderMapping)db_order_hash.get(key);
				System.out.println("obj.getPrintedReceiptDbId() >" + obj.getPrintedReceiptDbId());
				System.out.println("obj.getOrderId() >" + obj.getPreviousOrderId());
				PrintedReceiptDbOrderMappingPeer.doDelete(obj);
			}
		}
    }

	private void
    saveTenders()
		throws TorqueException, Exception
    {
		/*
		 *<table name="PRINTED_RECEIPT_DB_TENDER_MAPPING">
				<column name="PRINTED_RECEIPT_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
				<column name="TENDER_RET_DB_ID" primaryKey="true" required="true" type="INTEGER"/>

				<foreign-key foreignTable="PRINTED_RECEIPT_DB">
					<reference local="PRINTED_RECEIPT_DB_ID" foreign="PRINTED_RECEIPT_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="TENDER_RET_DB">
					<reference local="TENDER_RET_DB_ID" foreign="TENDER_RET_DB_ID"/>
				</foreign-key>
			</table>
		 */

		if (this.tenders != null)
		{
			System.out.println("saveTenders sizer >" + this.tenders.size());
			System.out.println("this.getId() >" + this.getId());

			HashMap db_tender_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(PrintedReceiptDbTenderMappingPeer.PRINTED_RECEIPT_DB_ID, this.getId());
			Iterator itr = PrintedReceiptDbTenderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				//System.out.println("found existing ass...");
				PrintedReceiptDbTenderMapping value = (PrintedReceiptDbTenderMapping)itr.next();
				Integer key = new Integer(value.getTenderRetDbId());
				db_tender_hash.put(key, value);
			}

			itr = this.tenders.iterator();
			while (itr.hasNext())
			{
				TenderRet tender = (TenderRet)itr.next();
				Integer key = new Integer(tender.getId());
				PrintedReceiptDbTenderMapping obj = (PrintedReceiptDbTenderMapping)db_tender_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					//System.out.println("creating new ass for " + orderline);

					obj = new PrintedReceiptDbTenderMapping();
					obj.setPrintedReceiptDbId(this.getId());
					obj.setTenderRetDbId(key.intValue());
					obj.save();
				}
			}

			itr = db_tender_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				PrintedReceiptDbTenderMapping obj = (PrintedReceiptDbTenderMapping)db_tender_hash.get(key);
				System.out.println("obj.getPrintedReceiptDbId() >" + obj.getPrintedReceiptDbId());
				System.out.println("obj.getTenderId() >" + obj.getTenderRetDbId());
				PrintedReceiptDbTenderMappingPeer.doDelete(obj);
			}
		}
    }

	public void
	setPreviousOrders(Vector _previous_orders)
	{
		previous_orders = _previous_orders;
	}

	public void
	setTenders(Vector _tenders)
	{
		tenders = _tenders;
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		System.out.println("updateObject() invoked in PrintedReceipt");
		
		printed_receipt.save();

		this.saveOrders();
		this.saveTenders();
    }
}