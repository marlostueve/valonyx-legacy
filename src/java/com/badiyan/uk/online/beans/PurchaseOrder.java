/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.util.CSVUtils;
import com.valeo.qb.data.ItemLineRet;
import com.valeo.qb.data.ItemReceiptRet;
import com.valeo.qb.data.VendorRet;
import java.io.FileWriter;
import java.io.IOException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
PurchaseOrder
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    public static final short OPEN_STATUS = 0;
    public static final short COMPLETE_STATUS = 1;
    public static final short CANCELLED_STATUS = 2;

    protected static HashMap<Integer,PurchaseOrder> hash = new HashMap<Integer,PurchaseOrder>(11);

    // CLASS METHODS

    public static void
    delete(UKOnlineCompanyBean _company, int _id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		/*
		Criteria crit = new Criteria();
		crit.addJoin(PurchaseOrderItemMappingPeer.PURCHASE_ORDER_DB_ID, PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID);
		crit.add(PurchaseOrderItemMappingPeer.PURCHASE_ORDER_DB_ID, _id);
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		PurchaseOrderItemMappingPeer.doDelete(crit);
		System.out.println("po delete crit 1 >" + crit.toString());
		*/

		Criteria crit = new Criteria();
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.add(PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID, _id);
		PurchaseOrderDbPeer.doDelete(crit);
		System.out.println("po delete crit 2 >" + crit.toString());
		hash.remove(new Integer(_id));
    }

    public static Vector
    getOpenPurchaseOrders(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.add(PurchaseOrderDbPeer.IS_COMPLETE, 0);
		crit.addAscendingOrderByColumn(PurchaseOrderDbPeer.PURCHASE_ORDER_DATE);
		Iterator obj_itr = PurchaseOrderDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(PurchaseOrder.getPurchaseOrder((PurchaseOrderDb)obj_itr.next()));

		return vec;
    }

    public static PurchaseOrder
    getPurchaseOrder(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		PurchaseOrder purchase_order = (PurchaseOrder)hash.get(key);
		if (purchase_order == null)
		{
			Criteria crit = new Criteria();
			crit.add(PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID, _id);
			List objList = PurchaseOrderDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Purchase Order with id: " + _id);

			purchase_order = PurchaseOrder.getPurchaseOrder((PurchaseOrderDb)objList.get(0));
		}

		return purchase_order;
    }

    private static PurchaseOrder
    getPurchaseOrder(PurchaseOrderDb _purchase_order)
		throws TorqueException
    {
		Integer key = new Integer(_purchase_order.getPurchaseOrderDbId());
		PurchaseOrder purchase_order = (PurchaseOrder)hash.get(key);
		if (purchase_order == null)
		{
			purchase_order = new PurchaseOrder(_purchase_order);
			hash.put(key, purchase_order);
		}

		return purchase_order;
    }

    public static Vector
    getPurchaseOrders(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(PurchaseOrderDbPeer.PURCHASE_ORDER_DATE);
		List objList = PurchaseOrderDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			Iterator itr = objList.iterator();
			while (itr.hasNext())
				vec.addElement(PurchaseOrder.getPurchaseOrder((PurchaseOrderDb)itr.next()));
		}

		return vec;
    }

    public static Vector
    getPurchaseOrders(UKOnlineCompanyBean _company, String _desc)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		if (_desc != null) {
			crit.addJoin(CheckoutCodePeer.CHECKOUT_CODE_ID, PurchaseOrderItemMappingPeer.CHECKOUT_CODE_ID);
			crit.addJoin(PurchaseOrderItemMappingPeer.PURCHASE_ORDER_DB_ID, PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID);
			crit.addJoin(PurchaseOrderDbPeer.VENDOR_RET_DB_ID, VendorRetDbPeer.VENDOR_RET_DB_ID);
			String search_string = "%" + _desc + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(VendorRetDbPeer.NAME, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b));
		}
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(PurchaseOrderDbPeer.PURCHASE_ORDER_DATE);
		crit.setDistinct();
		//System.out.println("crit >" + crit.toString());
		List objList = PurchaseOrderDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			Iterator itr = objList.iterator();
			while (itr.hasNext())
				vec.addElement(PurchaseOrder.getPurchaseOrder((PurchaseOrderDb)itr.next()));
		}

		return vec;
    }

    public static Vector
    getPurchaseOrdersThatNeedToBeSyncedWithQuickBooks(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		System.out.println("getPurchaseOrdersThatNeedToBeSyncedWithQuickBooks() invoked >" + _company.getLabel());

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		//crit.addJoin(PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID, ItemReceiptRetDbPeer.PURCHASE_ORDER_DB_ID);
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.add(PurchaseOrderDbPeer.TXN_I_D, (Object)"", Criteria.ISNULL);
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = PurchaseOrderDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext()) {
			vec.addElement(PurchaseOrder.getPurchaseOrder((PurchaseOrderDb)obj_itr.next()));
		}

		System.out.println("sizer >" + vec.size());

		return vec;
    }
	
	public static Vector
	getPurchaseOrdersModifiedAfter(CompanyBean _company, Date _date)
		throws TorqueException
	{
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.add(PurchaseOrderDbPeer.MODIFICATION_DATE, _date, Criteria.GREATER_THAN);
		System.out.println("getPurchaseOrdersModifiedAfter crit >" + crit.toString());
		Iterator itr = PurchaseOrderDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(PurchaseOrder.getPurchaseOrder((PurchaseOrderDb)itr.next()));
		}

		return vec;
	}
	
	public static PurchaseOrder
	getPurchaseOrderForNumber(CompanyBean _company, int _po_number)
		throws TorqueException, IllegalValueException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.add(PurchaseOrderDbPeer.PURCHASE_ORDER_NUMBER, _po_number);
		System.out.println("getPurchaseOrderForNumber crit >" + crit.toString());
		List l = PurchaseOrderDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			throw new IllegalValueException("Unable to locate purchase order for number >" + _po_number);
		}
		if (l.size() == 1) {
			return PurchaseOrder.getPurchaseOrder((PurchaseOrderDb)l.get(0));
		}
		
		throw new UniqueObjectNotFoundException("Unable to locate unique purchase order for number >" + _po_number);
	}

    // SQL

    /*
     *        <table name="PURCHASE_ORDER_DB" idMethod="native">
				<column name="PURCHASE_ORDER_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
				<column name="COMPANY_ID" required="true" type="INTEGER"/>
				<column name="VENDOR_RET_DB_ID" required="true" type="INTEGER"/>
				<column name="PURCHASE_ORDER_NUMBER" required="true" type="INTEGER"/>
				<column name="PURCHASE_ORDER_DATE" required="true" type="DATE"/>
				<column name="IS_COMPLETE" type="SMALLINT" default="0"/>

				<column name="TOTAL" required="true" scale="2" size="7" type="DECIMAL"/>

				<column name="NOTE" required="false" type="VARCHAR" size="250"/>

				<column name="CREATION_DATE" required="true" type="DATE"/>
				<column name="MODIFICATION_DATE" required="false" type="DATE"/>
				<column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
				<column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

				<foreign-key foreignTable="COMPANY">
					<reference local="COMPANY_ID" foreign="COMPANYID"/>
				</foreign-key>
				<foreign-key foreignTable="VENDOR_RET_DB">
					<reference local="VENDOR_RET_DB_ID" foreign="VENDOR_RET_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="PERSON">
					<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
				</foreign-key>
				<foreign-key foreignTable="PERSON">
					<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
				</foreign-key>
			</table>

			<table name="PURCHASE_ORDER_ITEM_MAPPING">
				<column name="PURCHASE_ORDER_DB_ID" required="true" type="INTEGER"/>
				<column name="CHECKOUT_CODE_ID" required="true" type="INTEGER"/>
				<column name="QUANTITY" required="true" scale="2" size="7" type="DECIMAL"/>
				<column name="RATE" required="true" scale="2" size="7" type="DECIMAL"/>
				<column name="AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>

				<foreign-key foreignTable="PURCHASE_ORDER_DB">
					<reference local="PURCHASE_ORDER_DB_ID" foreign="PURCHASE_ORDER_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="CHECKOUT_CODE">
					<reference local="CHECKOUT_CODE_ID" foreign="CHECKOUT_CODE_ID"/>
				</foreign-key>
			</table>
     */

    // INSTANCE VARIABLES

    private PurchaseOrderDb purchase_order;
	private Vector<PurchaseOrderItemMapping> items;

    // CONSTRUCTORS

    public
    PurchaseOrder()
    {
		purchase_order = new PurchaseOrderDb();
		isNew = true;
    }

    public
    PurchaseOrder(PurchaseOrderDb _purchase_order)
    {
		purchase_order = _purchase_order;
		isNew = false;
    }

    // INSTANCE METHODS

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(purchase_order.getCompanyId());
	}

	public int
	getCompanyId()
	{
		return purchase_order.getCompanyId();
	}

	public Date
	getPurchaseOrderDate()
	{
		return purchase_order.getPurchaseOrderDate();
	}

	public String
	getPurchaseOrderDateString()
	{
		if (purchase_order.getPurchaseOrderDate() == null)
			return CUBean.getUserDateString(new Date());
		return CUBean.getUserDateString(purchase_order.getPurchaseOrderDate());
	}

	public int
	getPurchaseOrderNumber(CompanyBean _company)
		throws TorqueException
	{
		//System.out.println("purchase_order.getPurchaseOrderNumber() >" + purchase_order.getPurchaseOrderNumber());
		if (purchase_order.getPurchaseOrderNumber() == 0)
		{
			Criteria crit = new Criteria();
			crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
			crit.addDescendingOrderByColumn(PurchaseOrderDbPeer.PURCHASE_ORDER_NUMBER);
			Iterator itr = PurchaseOrderDbPeer.doSelect(crit).iterator();
			if (itr.hasNext())
			{
				PurchaseOrderDb obj = (PurchaseOrderDb)itr.next();
				return obj.getPurchaseOrderNumber() + 1;
			}
			else
				return 1;
		}
		return purchase_order.getPurchaseOrderNumber();
	}

    public int
    getId()
    {
		return purchase_order.getPurchaseOrderDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return this.getVendor().getLabel() + " Purchase Order";
    }

	public String
	getNoteString()
	{
		String str = purchase_order.getNote();
		if (str == null)
			return "";
		return str;
	}
	
	public PurchaseOrderItemMapping
	getMapping(CheckoutCodeBean _code) throws TorqueException, ObjectNotFoundException {
		Iterator itr = this.getItems().iterator();
		while (itr.hasNext()) {
			PurchaseOrderItemMapping obj = (PurchaseOrderItemMapping)itr.next();
			if (obj.getCheckoutCodeId() == _code.getId()) {
				return obj;
			}
		}
		throw new ObjectNotFoundException("Unable to find mapping for code >" + _code.getLabel());
	}

	public Vector
	getItems()
		throws TorqueException, ObjectNotFoundException
	{
		if (items == null)
		{
			items = new Vector();

			Criteria crit = new Criteria();
			crit.add(PurchaseOrderItemMappingPeer.PURCHASE_ORDER_DB_ID, this.getId());
			Iterator itr = PurchaseOrderItemMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				PurchaseOrderItemMapping obj = (PurchaseOrderItemMapping)itr.next();
				items.addElement(obj);
			}
		}
		return items;
	}

	public String
	getStatus() {
		switch (purchase_order.getIsComplete())
		{
			case 0: return "Open";
			case 1: return "Complete";
			case 2: return "Cancelled";
		}

		return "[NOT FOUND]";
	}

	public BigDecimal
	getTotal()
	{
		return purchase_order.getTotal();
	}

	public String
	getTotalString()
	{
		if (purchase_order.getTotal() == null)
			return "0.00";

		return purchase_order.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public String
	getTaxString()
	{
		if (purchase_order.getTax() == null)
			return "0.00";

		return purchase_order.getTax().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public BigDecimal
	getTax() {
		if (purchase_order.getTax() == null) {
			return BigDecimal.ZERO;
		}

		return purchase_order.getTax().setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public String
	getShippingString()
	{
		if (purchase_order.getShipping() == null)
			return "0.00";

		return purchase_order.getShipping().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public BigDecimal
	getShipping() {
		if (purchase_order.getShipping() == null) {
			return BigDecimal.ZERO;
		}

		return purchase_order.getShipping().setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public String
	getDiscountString() {
		if (purchase_order.getDiscount() == null)
			return "0.00";

		return purchase_order.getDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public BigDecimal
	getDiscount() {
		if (purchase_order.getDiscount() == null)
			return BigDecimal.ZERO;

		return purchase_order.getDiscount().setScale(2, BigDecimal.ROUND_HALF_UP);
	}

    public String
    getValue()
    {
		return purchase_order.getPurchaseOrderDbId() + "";
    }

	public VendorRet
	getVendor()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return VendorRet.getVendor(purchase_order.getVendorRetDbId());
	}

	public int
	getVendorId()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return purchase_order.getVendorRetDbId();
	}
	
	public boolean
	hasVendor() {
		return ( purchase_order.getVendorRetDbId() > 0 );
	}

	public String
	getVendorStr()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (purchase_order.getVendorRetDbId() > 0) {
			return VendorRet.getVendor(purchase_order.getVendorRetDbId()).getLabel();
		}
		return "[NOT SELECTED]";
	}

	public BigDecimal
	getRemainingQuantityToBeReceived()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		BigDecimal original_qty_ordered = CUBean.zero;
		BigDecimal total_qty_received = CUBean.zero;

		Iterator itr = this.getItems().iterator();
		while (itr.hasNext())
		{
			PurchaseOrderItemMapping item = (PurchaseOrderItemMapping)itr.next();
			original_qty_ordered = original_qty_ordered.add(item.getQuantity());

			Iterator item_receipt_itr = ItemReceiptRet.getItemReceipts(this).iterator();
			while (item_receipt_itr.hasNext())
			{
				ItemReceiptRet item_receipt = (ItemReceiptRet)item_receipt_itr.next();
				ItemLineRet item_line = item_receipt.getItemLine(item.getPurchaseOrderItemMappingId());
				BigDecimal item_line_qty_received = item_line.getQuantity() == null ? BigDecimal.ZERO : item_line.getQuantity();
				total_qty_received = total_qty_received.add(item_line_qty_received);
			}
		}

		BigDecimal remaining_quantity_to_be_received = original_qty_ordered.subtract(total_qty_received);

		if (remaining_quantity_to_be_received.compareTo(CUBean.zero) == -1)
			remaining_quantity_to_be_received = CUBean.zero;

		return remaining_quantity_to_be_received;
	}

	public BigDecimal
	getRemainingQuantityToBeReceived(PurchaseOrderItemMapping _item)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		BigDecimal remaining_quantity_to_be_received = _item.getQuantity();
			// start with the original quantity ordered

		// find all of the ItemReceiptRet objects already applied against this purchase order

		Iterator item_receipt_itr = ItemReceiptRet.getItemReceipts(this).iterator();
		while (item_receipt_itr.hasNext())
		{
			ItemReceiptRet item_receipt = (ItemReceiptRet)item_receipt_itr.next();
			ItemLineRet item_line = item_receipt.getItemLine(_item.getPurchaseOrderItemMappingId());
			BigDecimal item_line_qty_received = item_line.getQuantity() == null ? BigDecimal.ZERO : item_line.getQuantity();
			remaining_quantity_to_be_received = remaining_quantity_to_be_received.subtract(item_line_qty_received);
		}

		if (remaining_quantity_to_be_received.compareTo(CUBean.zero) == -1)
			remaining_quantity_to_be_received = CUBean.zero;

		return remaining_quantity_to_be_received;
	}

    protected void
    insertObject()
		throws Exception
    {
		purchase_order.setCreationDate(new Date());
		purchase_order.save();

		this.saveItems();
		//this.saveWaitItems();
    }

	public boolean
	isComplete()
	{
		return purchase_order.getIsComplete() == (short)1;
	}

	private void
    saveItems()
		throws TorqueException, Exception
    {
		System.out.println("saveItems() invoked");
		/*
		 *<table name="PURCHASE_ORDER_ITEM_MAPPING">
				<column name="PURCHASE_ORDER_DB_ID" required="true" type="INTEGER"/>
				<column name="CHECKOUT_CODE_ID" required="true" type="INTEGER"/>
				<column name="QUANTITY" required="true" scale="2" size="7" type="DECIMAL"/>
				<column name="RATE" required="true" scale="2" size="7" type="DECIMAL"/>
				<column name="AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>

				<foreign-key foreignTable="PURCHASE_ORDER_DB">
					<reference local="PURCHASE_ORDER_DB_ID" foreign="PURCHASE_ORDER_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="CHECKOUT_CODE">
					<reference local="CHECKOUT_CODE_ID" foreign="CHECKOUT_CODE_ID"/>
				</foreign-key>
			</table>
		 */

		if (this.items != null)
		{
			System.out.println("sizer >" + this.items.size());

			HashMap db_order_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(PurchaseOrderItemMappingPeer.PURCHASE_ORDER_DB_ID, this.getId());
			Iterator itr = PurchaseOrderItemMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				PurchaseOrderItemMapping value = (PurchaseOrderItemMapping)itr.next();
				//System.out.println("found existing ass... " + value.getPurchaseOrderItemMappingId());
				Integer key = new Integer(value.getPurchaseOrderItemMappingId());
				db_order_hash.put(key, value);
			}

			//System.out.println("num items >" + this.items.size());
			itr = this.items.iterator();
			while (itr.hasNext())
			{
				PurchaseOrderItemMapping item = (PurchaseOrderItemMapping)itr.next();
				Integer key = new Integer(item.getPurchaseOrderItemMappingId());
				PurchaseOrderItemMapping obj = (PurchaseOrderItemMapping)db_order_hash.remove(key);
				if ((obj == null) || (key.intValue() == 0))
				{
					// association does not exist in db.  need to create

					CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(item.getCheckoutCodeId());
					//System.out.println("creating new ass for " + checkout_code.getLabel() + " key >" + key.intValue());

					item.setPurchaseOrderDbId(this.getId());
					item.setQuantityReceived(BigDecimal.ZERO);
					item.save();
					
					System.out.println("mapping saved (1) >" + item.getPurchaseOrderItemMappingId());
				}
				else
				{
					// association exists, but the values may be different.  need to update...

					obj.setAmount(item.getAmount());
					obj.setCheckoutCodeId(item.getCheckoutCodeId());
					obj.setQuantity(item.getQuantity());
					obj.setRate(item.getRate());
					obj.save();
					
					System.out.println("mapping saved (2) >" + obj.getPurchaseOrderItemMappingId());
				}
			}

			itr = db_order_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				//System.out.println("key >" + key.intValue());
				PurchaseOrderItemMapping obj = (PurchaseOrderItemMapping)db_order_hash.get(key);
				PurchaseOrderItemMappingPeer.doDelete(obj);
			}

			this.items = null;
		}
    }

    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			purchase_order.setCreatePersonId(_person.getId());
		else
			purchase_order.setModifyPersonId(_person.getId());
    }

    public void
    setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
    {
		purchase_order.setCompanyId(_company.getId());
    }

	public void
	setPurchaseOrderDate(Date _date)
	{
		purchase_order.setPurchaseOrderDate(_date);
	}

	public void
	setPurchaseOrderNumber(int _purchase_order_number)
	{
		purchase_order.setPurchaseOrderNumber(_purchase_order_number);
	}

	public void
	setStatus(short _status)
	{
		purchase_order.setIsComplete(_status);
	}

	public void
	setItems(Vector _items)
	{
		items = _items;

		BigDecimal total = CUBean.zero;
		if (items != null)
		{
			Iterator item_itr = items.iterator();
			while (item_itr.hasNext())
			{
				PurchaseOrderItemMapping item = (PurchaseOrderItemMapping)item_itr.next();
				total = total.add(item.getAmount());
			}
		}
		this.setTotal(total);
	}

	/*
	Vector waitListItems = null;
	public void
	setWaitItems(Vector _items)
	{
		waitListItems = _items;
	}
	private void
	saveWaitItems() throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		if (waitListItems != null) {
			Iterator itr = waitListItems.iterator();
			while (itr.hasNext()) {
				ProductWaitList wait_item_obj = (ProductWaitList)itr.next();
				wait_item_obj.saveMapping();
				wait_item_obj.save();
			}
		}
	}
	*/
	
	public void
	setTxnID(String _txn_id) {
		purchase_order.setTxnID(_txn_id);
	}

	public void
	setNote(String _str)
	{
		purchase_order.setNote(_str);
	}

	public void
	setTotal(BigDecimal _total)
	{
		purchase_order.setTotal(_total);
	}

	public void
	setTax(BigDecimal _total)
	{
		purchase_order.setTax(_total);
	}

	public void
	setShipping(BigDecimal _total)
	{
		purchase_order.setShipping(_total);
	}

	public void
	setDiscount(BigDecimal _total)
	{
		purchase_order.setDiscount(_total);
	}

	public void
	setVendor(VendorRet _vendor)
		throws TorqueException
	{
		purchase_order.setVendorRetDbId(_vendor.getId());
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		purchase_order.setModificationDate(new Date());
		purchase_order.save();

		this.saveItems();
		//this.saveWaitItems();
    }
	
	public String
	toCSVFile(UKOnlineCompanyBean _selected_bu) throws IllegalValueException, IOException, TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		
		
		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		String file = "" + this.getVendorStr() + "-purchase-order-" + this.getPurchaseOrderNumber(_selected_bu) + ".csv";
		String csvFile = resourcesFolder + "pdf/" + file;
		
        FileWriter writer = new FileWriter(csvFile);
		
		Iterator item_itr = this.getItems().iterator();
		while (item_itr.hasNext()) {
			PurchaseOrderItemMapping item = (PurchaseOrderItemMapping)item_itr.next();
			CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(item.getCheckoutCodeId());
			if (!checkout_code.getVendorProductNumberString().isEmpty()) {
				CSVUtils.writeLine(writer, Arrays.asList(checkout_code.getVendorProductNumberString(), item.getQuantity().setScale(0, RoundingMode.HALF_UP).toString(), this.getPurchaseOrderNumber(_selected_bu) + ""));
			} else {
				throw new IllegalValueException("Unable to export.  " + checkout_code.getLabel() + " has no vendor item number assigned.");
			}
		}
		
		writer.flush();
		writer.close();
		
		//return csvFile;
		
		return CUBean.getProperty("cu.webDomain") + "/resources/pdf/" + file;
	}
}