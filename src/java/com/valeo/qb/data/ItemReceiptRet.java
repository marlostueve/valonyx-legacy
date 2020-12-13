/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb.data;

import java.math.BigDecimal;

import java.util.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.online.beans.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
ItemReceiptRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,ItemReceiptRet> hash = new HashMap<Integer,ItemReceiptRet>(11);

	// CLASS METHODS

    public static ItemReceiptRet
    getItemReceipt(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ItemReceiptRet obj = (ItemReceiptRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ItemReceiptRetDbPeer.ITEM_RECEIPT_RET_DB_ID, _id);
			List objList = ItemReceiptRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Item Receipt with id: " + _id);

			obj = ItemReceiptRet.getItemReceipt((ItemReceiptRetDb)objList.get(0));
		}

		return obj;
    }

    private static ItemReceiptRet
    getItemReceipt(ItemReceiptRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getItemReceiptRetDbId());
		ItemReceiptRet obj = (ItemReceiptRet)hash.get(key);
		if (obj == null)
		{
			obj = new ItemReceiptRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static ItemReceiptRet
    getItemReceipt(CompanyBean _company, String _txn_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.addJoin(PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID, ItemReceiptRetDbPeer.PURCHASE_ORDER_DB_ID);
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.add(ItemReceiptRetDbPeer.TXN_I_D, _txn_id);
		List obj_list = ItemReceiptRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ItemReceiptRet.getItemReceipt((ItemReceiptRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Item Receipt with txn id: " + _txn_id);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Item Receipt with txn id: " + _txn_id);
    }

    public static Vector
    getItemReceipts(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID, ItemReceiptRetDbPeer.PURCHASE_ORDER_DB_ID);
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		Iterator obj_itr = ItemReceiptRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemReceiptRet.getItemReceipt((ItemReceiptRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getItemReceipts(PurchaseOrder _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ItemReceiptRetDbPeer.PURCHASE_ORDER_DB_ID, _order.getId());
		Iterator obj_itr = ItemReceiptRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemReceiptRet.getItemReceipt((ItemReceiptRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getItemReceiptsThatNeedToBeSyncedWithQuickBooks(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		System.out.println("getItemReceiptsThatNeedToBeSyncedWithQuickBooks() invoked >" + _company.getLabel());

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID, ItemReceiptRetDbPeer.PURCHASE_ORDER_DB_ID);
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.add(ItemReceiptRetDbPeer.TXN_I_D, (Object)"", Criteria.ISNULL);
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = ItemReceiptRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemReceiptRet.getItemReceipt((ItemReceiptRetDb)obj_itr.next()));

		System.out.println("sizer >" + vec.size());

		return vec;
    }

	public static Vector
	getItemReceiptsThatNeedToBeSyncedWithQuickBooks(CompanyBean _company, Date _start_date, Date _end_date)
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
		crit.addJoin(PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID, ItemReceiptRetDbPeer.PURCHASE_ORDER_DB_ID);
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.add(ItemReceiptRetDbPeer.TXN_I_D, (Object)"", Criteria.ISNULL); // TXN_I_D is currently ALWAYS null !?!?!? - so this is worthless
		// strat - when I receive a BillRet, I need to fill this in with some value so that I can remove this dumb date restriction for future stuff
		/* removed 2/8/19 - counting on the TXN_I_D being null for all item receipts that need syncing
		crit.add(ItemReceiptRetDbPeer.TIME_CREATED, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(ItemReceiptRetDbPeer.TIME_CREATED, end_date.getTime(), Criteria.LESS_EQUAL);
		*/
		System.out.println("getItemReceiptsThatNeedToBeSyncedWithQuickBooks() crit >" + crit.toString());
		Iterator obj_itr = ItemReceiptRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemReceiptRet.getItemReceipt((ItemReceiptRetDb)obj_itr.next()));

		System.out.println("sizer >" + vec.size());

		return vec;
		
	}

	// INSTANCE VARIABLES

	private ItemReceiptRetDb item_receipt;
	private Vector item_lines;

	/*
	private String parent_list_id;

	private String tax_line_id;
	private String tax_line_name;
	 *
	 */

	// CONSTRUCTORS

	public
	ItemReceiptRet()
	{
		item_receipt = new ItemReceiptRetDb();
		isNew = true;
	}

	public
	ItemReceiptRet(ItemReceiptRetDb _receipt)
	{
		item_receipt = _receipt;
		isNew = false;
	}

	/*
	 * <table name="ITEM_RECEIPT_RET_DB" idMethod="native">
    <column name="ITEM_RECEIPT_RET_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PURCHASE_ORDER_DB_ID" required="true" type="INTEGER"/>
	<column name="TXN_I_D" required="true" type="VARCHAR" size="20"/>
    <column name="TIME_CREATED" required="true" type="DATE"/>
    <column name="TIME_MODIFIED" required="false" type="DATE"/>
    <column name="EDIT_SEQUENCE" required="false" type="VARCHAR" size="15"/>
    <column name="TXN_NUMBER" required="false" type="INTEGER"/>
    <column name="VENDOR_ID" required="false" type="INTEGER"/>
    <column name="TXN_DATE" required="true" type="DATE"/>
    <column name="TOTAL_AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="REF_NUMBER" required="false" type="VARCHAR" size="15"/>
    <column name="MEMO" type="LONGVARCHAR"/>

    <foreign-key foreignTable="PURCHASE_ORDER_DB">
		<reference local="PURCHASE_ORDER_DB_ID" foreign="PURCHASE_ORDER_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="VENDOR_RET_DB">
		<reference local="VENDOR_ID" foreign="VENDOR_RET_DB_ID"/>
    </foreign-key>
</table>
	 */

	// INSTANCE METHODS

	public ItemLineRet
	getItemLine(int _mapping_id)
		throws TorqueException, ObjectNotFoundException
	{
		this.getItemLines();
		Iterator item_line_itr = this.item_lines.iterator();
		while (item_line_itr.hasNext())
		{
			ItemLineRet item_line = (ItemLineRet)item_line_itr.next();
			if (item_line.getMappingId() == _mapping_id)
				return item_line;
		}
		ItemLineRet item_line = new ItemLineRet();
		item_line.setItemReceipt(this);
		item_line.setMappingId(_mapping_id);
		this.item_lines.addElement(item_line);
		return item_line;
	}

	public Vector
	getItemLines()
		throws TorqueException, ObjectNotFoundException
	{
		if (this.item_lines == null)
		{
			this.item_lines = new Vector();

			Criteria crit = new Criteria();
			crit.add(ItemLineRetDbPeer.ITEM_RECEIPT_RET_DB_ID, this.getId());
			Iterator itr = ItemLineRetDbPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				ItemLineRetDb obj = (ItemLineRetDb)itr.next();
				this.item_lines.addElement(ItemLineRet.getItemLine(obj));
			}
		}
		return this.item_lines;
	}

	public String
	getTxnID()
	{
		return item_receipt.getTxnID();
	}

	public String
	getMemo()
	{
		return item_receipt.getMemo();
	}

	public String
	getEditSequence()
	{
		return item_receipt.getEditSequence();
	}

	public String
	getRefNumber()
	{
		return item_receipt.getRefNumber();
	}

	public int
	getId()
	{
		return item_receipt.getItemReceiptRetDbId();
	}

	public String
	getLabel()
	{
		return this.getTxnID();
	}

	public PurchaseOrder
	getPurchaseOrder()
		throws TorqueException, ObjectNotFoundException
	{
		return PurchaseOrder.getPurchaseOrder(item_receipt.getPurchaseOrderDbId());
	}

	/*
	public String
	getParentListID()
	{
		return this.parent_list_id;
	}

	public String
	getTaxLineID()
	{
		return this.tax_line_id;
	}

	public String
	getTaxLineName()
	{
		return this.tax_line_name;
	}
	 *
	 */

	public Date
	getTimeCreated()
	{
		return item_receipt.getTimeCreated();
	}

	public Date
	getTimeModified()
	{
		return item_receipt.getTimeModified();
	}

	public float
	getTaxAmount()
	{
		BigDecimal amount = item_receipt.getTax();
		if (amount == null)
			return 0f;
		return amount.floatValue();
	}

	public String
	getTaxAmountString()
	{
		BigDecimal amount = item_receipt.getTax();
		if (amount == null)
			return "0.00";
		return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public float
	getShippingAmount()
	{
		BigDecimal amount = item_receipt.getShipping();
		if (amount == null)
			return 0f;
		return amount.floatValue();
	}

	public String
	getShippingAmountString()
	{
		BigDecimal amount = item_receipt.getShipping();
		if (amount == null)
			return "0.00";
		return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public float
	getDiscountAmount()
	{
		BigDecimal amount = item_receipt.getDiscount();
		if (amount == null)
			return 0f;
		return amount.floatValue();
	}

	public String
	getDiscountAmountString()
	{
		BigDecimal amount = item_receipt.getDiscount();
		if (amount == null)
			return "0.00";
		return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public float
	getTotalAmount()
	{
		BigDecimal amount = item_receipt.getTotalAmount();
		if (amount == null)
			return 0f;
		return amount.floatValue();
	}

	public String
	getTotalAmountString()
	{
		BigDecimal amount = item_receipt.getTotalAmount();
		if (amount == null)
			return "0.00";
		return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public String
	getValue()
	{
		return item_receipt.getItemReceiptRetDbId() + "";
	}

	public int
	getTxnNumber()
	{
		return item_receipt.getTxnNumber();
	}

    protected void
    insertObject()
		throws Exception
    {
		item_receipt.setTimeCreated(new Date());
		item_receipt.save();
		this.saveItemLines();
    }

	private void
    saveItemLines()
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

		if (this.item_lines != null)
		{
			System.out.println("sizer >" + this.item_lines.size());

			HashMap db_item_lines_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(ItemLineRetDbPeer.ITEM_RECEIPT_RET_DB_ID, this.getId());
			Iterator itr = ItemLineRetDbPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				//System.out.println("found existing ass...");
				ItemLineRetDb value = (ItemLineRetDb)itr.next();
				Integer key = new Integer(value.getItemLineRetDbId());
				db_item_lines_hash.put(key, value);
			}

			//System.out.println("num orders >" + this.item_lines.size());
			itr = this.item_lines.iterator();
			while (itr.hasNext())
			{
				ItemLineRet line = (ItemLineRet)itr.next();
				Integer key = new Integer(line.getId());
				ItemLineRetDb obj = (ItemLineRetDb)db_item_lines_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					//System.out.println("creating new ass for " + orderline);

					line.setItemReceipt(this);
					line.save();

				}
			}

			itr = db_item_lines_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				ItemLineRetDb obj = (ItemLineRetDb)db_item_lines_hash.get(key);
				ItemLineRetDbPeer.doDelete(obj);
			}
		}
    }

	public void
	setItems(Vector _items)
	{
		this.item_lines = _items;
	}

	public void
	setTxnID(String _txn_id)
	{
		item_receipt.setTxnID(_txn_id);
	}

	public void
	setPurchaseOrder(PurchaseOrder _order)
		throws TorqueException
	{
		item_receipt.setPurchaseOrderDbId(_order.getId());
	}

	public void
	setRefNumber(String _refNumber)
	{
		item_receipt.setRefNumber(_refNumber);
	}

	public void
	setMemo(String _memo)
	{
		item_receipt.setMemo(_memo);
	}

	public void
	setEditSequence(String _edit_sequence)
	{
		item_receipt.setEditSequence(_edit_sequence);
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		item_receipt.setTimeCreated(_timeCreated);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		item_receipt.setTimeModified(_timeModified);
	}

	public void
	setTaxAmount(float _amount)
	{
		item_receipt.setTax(new BigDecimal(_amount));
	}

	public void
	setShippingAmount(float _amount)
	{
		item_receipt.setShipping(new BigDecimal(_amount));
	}

	public void
	setDiscountAmount(float _amount)
	{
		item_receipt.setDiscount(new BigDecimal(_amount));
	}

	public void
	setTotalAmount(float _amount)
	{
		item_receipt.setTotalAmount(new BigDecimal(_amount));
	}

	public void
	setTxnNumber(int _txn_number)
	{
		item_receipt.setTxnNumber(_txn_number);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		item_receipt.setTimeModified(new Date());
		item_receipt.save();
		this.saveItemLines();
    }
}
