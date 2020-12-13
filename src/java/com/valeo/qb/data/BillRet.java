
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
 * created 5/12/18
 * @author marlo
 */
public class
BillRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,BillRet> hash = new HashMap<Integer,BillRet>(11);

	// CLASS METHODS

    public static BillRet
    getBillForTxnNumber(CompanyBean _company, int _txn_number)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		//crit.add(BillRetDbPeer., _company.getId()); // ugh
		crit.add(BillRetDbPeer.TXN_NUMBER, _txn_number);
		List obj_list = SalesReceiptRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return BillRet.getBill((BillRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Bill with txn number: " + _txn_number);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Bill with txn number: " + _txn_number);
    }

    public static BillRet
    getBill(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		BillRet obj = (BillRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(BillRetDbPeer.BILL_RET_DB_ID, _id);
			List objList = BillRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Bill with id: " + _id);

			obj = BillRet.getBill((BillRetDb)objList.get(0));
		}

		return obj;
    }

    private static BillRet
    getBill(BillRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getBillRetDbId());
		BillRet obj = (BillRet)hash.get(key);
		if (obj == null)
		{
			obj = new BillRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static BillRet
    getBill(CompanyBean _company, String _txn_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.addJoin(PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID, BillRetDbPeer.PURCHASE_ORDER_DB_ID);
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.add(BillRetDbPeer.TXN_I_D, _txn_id);
		List obj_list = BillRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return BillRet.getBill((BillRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Bill with txn id: " + _txn_id);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Bill with txn id: " + _txn_id);
    }

    public static Vector
    getBills(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID, BillRetDbPeer.PURCHASE_ORDER_DB_ID);
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		Iterator obj_itr = BillRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(BillRet.getBill((BillRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getBills(PurchaseOrder _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(BillRetDbPeer.PURCHASE_ORDER_DB_ID, _order.getId());
		Iterator obj_itr = BillRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(BillRet.getBill((BillRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getBillsThatNeedToBeSyncedWithQuickBooks(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		System.out.println("getBillsThatNeedToBeSyncedWithQuickBooks() invoked >" + _company.getLabel());

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(PurchaseOrderDbPeer.PURCHASE_ORDER_DB_ID, BillRetDbPeer.PURCHASE_ORDER_DB_ID);
		crit.add(PurchaseOrderDbPeer.COMPANY_ID, _company.getId());
		crit.add(BillRetDbPeer.TXN_I_D, (Object)"", Criteria.ISNULL);
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = BillRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(BillRet.getBill((BillRetDb)obj_itr.next()));

		System.out.println("sizer >" + vec.size());

		return vec;
    }

	// INSTANCE VARIABLES

	private BillRetDb bill;
	private Vector item_lines;

	/*
	private String parent_list_id;

	private String tax_line_id;
	private String tax_line_name;
	 *
	 */

	// CONSTRUCTORS

	public
	BillRet()
	{
		bill = new BillRetDb();
		isNew = true;
	}

	public
	BillRet(BillRetDb _bill)
	{
		bill = _bill;
		isNew = false;
	}

	/*
	 * 
<table name="BILL_RET_DB" idMethod="native">
    <column name="BILL_RET_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PURCHASE_ORDER_DB_ID" required="true" type="INTEGER"/>
	<column name="TXN_I_D" required="false" type="VARCHAR" size="20"/>
    <column name="TIME_CREATED" required="true" type="DATE"/>
    <column name="TIME_MODIFIED" required="false" type="DATE"/>
    <column name="EDIT_SEQUENCE" required="false" type="VARCHAR" size="15"/>
    <column name="TXN_NUMBER" required="false" type="INTEGER"/>
    <column name="VENDOR_ID" required="false" type="INTEGER"/>
	<column name="A_P_ACCOUNT_ID" required="false" type="INTEGER"/>
    <column name="TXN_DATE" required="false" type="DATE"/>
    <column name="DUE_DATE" required="false" type="DATE"/>
    <column name="AMOUNT_DUE" required="false" scale="2" size="7" type="DECIMAL"/>
    <column name="REF_NUMBER" required="false" type="VARCHAR" size="15"/>
    <column name="IS_PAID" required="true" type="SMALLINT" default="0"/>

    <foreign-key foreignTable="PURCHASE_ORDER_DB">
		<reference local="PURCHASE_ORDER_DB_ID" foreign="PURCHASE_ORDER_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="VENDOR_RET_DB">
		<reference local="VENDOR_ID" foreign="VENDOR_RET_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="ACCOUNT_RET_DB">
		<reference local="A_P_ACCOUNT_ID" foreign="ACCOUNT_RET_DB_ID"/>
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
		item_line.setBill(this);
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
			crit.add(ItemLineRetDbPeer.BILL_RET_DB_ID, this.getId());
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
		return bill.getTxnID();
	}

	public String
	getEditSequence()
	{
		return bill.getEditSequence();
	}

	public String
	getRefNumber()
	{
		return bill.getRefNumber();
	}

	public int
	getId()
	{
		return bill.getBillRetDbId();
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
		return PurchaseOrder.getPurchaseOrder(bill.getPurchaseOrderDbId());
	}

	public Date
	getTimeCreated()
	{
		return bill.getTimeCreated();
	}

	public Date
	getTimeModified()
	{
		return bill.getTimeModified();
	}

	public float
	getAmountDue()
	{
		BigDecimal amount = bill.getAmountDue();
		if (amount == null)
			return 0f;
		return amount.floatValue();
	}

	public String
	getAmountDueString()
	{
		BigDecimal amount = bill.getAmountDue();
		if (amount == null)
			return "0.00";
		return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public float
	getOpenAmount()
	{
		BigDecimal amount = bill.getOpenAmount();
		if (amount == null)
			return 0f;
		return amount.floatValue();
	}

	public String
	getOpenAmountString()
	{
		BigDecimal amount = bill.getOpenAmount();
		if (amount == null)
			return "0.00";
		return amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public String
	getValue()
	{
		return bill.getBillRetDbId() + "";
	}

	public int
	getTxnNumber()
	{
		return bill.getTxnNumber();
	}

    protected void
    insertObject()
		throws Exception
    {
		bill.setTimeCreated(new Date());
		bill.save();
		this.saveItemLines();
		
		this.updateCorrespondingPurchaseOrders();
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
			crit.add(ItemLineRetDbPeer.BILL_RET_DB_ID, this.getId());
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

					line.setBill(this);
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
		bill.setTxnID(_txn_id);
	}

	public void
	setPurchaseOrder(PurchaseOrder _order)
		throws TorqueException
	{
		bill.setPurchaseOrderDbId(_order.getId());
	}

	public void
	setRefNumber(String _refNumber)
	{
		bill.setRefNumber(_refNumber);
	}

	public void
	setEditSequence(String _edit_sequence)
	{
		bill.setEditSequence(_edit_sequence);
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		bill.setTimeCreated(_timeCreated);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		bill.setTimeModified(_timeModified);
	}

	public void
	setAmountDue(float _amount)
	{
		bill.setAmountDue(new BigDecimal(_amount));
	}

	public void
	setOpenAmount(float _amount)
	{
		bill.setOpenAmount(new BigDecimal(_amount));
	}

	public void
	setTxnNumber(int _txn_number)
	{
		bill.setTxnNumber(_txn_number);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		bill.setTimeModified(new Date());
		bill.save();
		this.saveItemLines();
		
		this.updateCorrespondingPurchaseOrders();
    }
	
	private void
	updateCorrespondingPurchaseOrders() {
		// also find the corresponding ItemReceiptRet in order to set the TxnID
		
		System.out.println("this.getRefNumber() >" + this.getRefNumber());
		System.out.println("this.getTxnId() >" + this.getTxnID());
		
		try {
			if ( (this.getRefNumber() != null) && (this.getTxnID() != null) ) {
				// get the purchase order corresponding to this ref #
				
				PurchaseOrder po = PurchaseOrder.getPurchaseOrderForNumber(CompanyBean.getCompany(5), Integer.parseInt(this.getRefNumber()));
				System.out.println("found corresponding PO >" + po.getLabel());
				
				Iterator itr = ItemReceiptRet.getItemReceipts(po).iterator();
				while (itr.hasNext()) {
					ItemReceiptRet itemReceipt = (ItemReceiptRet)itr.next();
					itemReceipt.setTxnID(this.getTxnID());
					itemReceipt.setRefNumber(this.getRefNumber());
					itemReceipt.save();
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void
	setIsPaid(String _is_paid)
	{
		bill.setIsPaid(_is_paid);
	}
}
