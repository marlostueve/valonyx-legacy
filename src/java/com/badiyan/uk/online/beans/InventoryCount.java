/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import com.valeo.qb.data.ItemLineRet;
import com.valeo.qb.data.ItemReceiptRet;
import com.valeo.qb.data.VendorRet;

import java.math.BigDecimal;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
InventoryCount
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,InventoryCount> hash = new HashMap<Integer,InventoryCount>(11);

    // CLASS METHODS

    public static InventoryCount
    getActiveInventoryCount(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		Criteria crit = new Criteria();
		crit.add(InventoryCountDbPeer.COMPANY_ID, _company.getId());
		crit.add(InventoryCountDbPeer.IS_COMPLETE, 0);
		List obj_list = InventoryCountDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return InventoryCount.getInventoryCount((InventoryCountDb)obj_list.get(0));
		else if (obj_list.size() == 0)
		{
			// create an active count...

			InventoryCount active_count = new InventoryCount();
			active_count.setCompany(_company);
			active_count.setIsComplete(false);
			active_count.save();

			return active_count;
		}
		else
			throw new ObjectNotFoundException("Unable to find or create active inventory count.");
    }

    public static InventoryCount
    getInventoryCount(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		InventoryCount purchase_order = (InventoryCount)hash.get(key);
		if (purchase_order == null)
		{
			Criteria crit = new Criteria();
			crit.add(InventoryCountDbPeer.INVENTORY_COUNT_DB_ID, _id);
			List objList = InventoryCountDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Inventory Count with id: " + _id);

			purchase_order = InventoryCount.getInventoryCount((InventoryCountDb)objList.get(0));
		}

		return purchase_order;
    }

    private static InventoryCount
    getInventoryCount(InventoryCountDb _purchase_order)
		throws TorqueException
    {
		Integer key = new Integer(_purchase_order.getInventoryCountDbId());
		InventoryCount purchase_order = (InventoryCount)hash.get(key);
		if (purchase_order == null)
		{
			purchase_order = new InventoryCount(_purchase_order);
			hash.put(key, purchase_order);
		}

		return purchase_order;
    }

    public static Vector
    getInventoryCounts(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(InventoryCountDbPeer.COMPANY_ID, _company.getId());
		List objList = InventoryCountDbPeer.doSelect(crit);
		if (objList.size() > 0)
		{
			Iterator itr = objList.iterator();
			while (itr.hasNext())
				vec.addElement(InventoryCount.getInventoryCount((InventoryCountDb)itr.next()));
		}

		return vec;
    }

    public static InventoryCount
    getLastCompletedInventoryCount(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(InventoryCountDbPeer.COMPANY_ID, _company.getId());
		crit.add(InventoryCountDbPeer.IS_COMPLETE, (short)1);
		crit.addDescendingOrderByColumn(InventoryCountDbPeer.MODIFICATION_DATE);
		System.out.println("getLastCompletedInventoryCountDate crit >" + crit.toString());
		List objList = InventoryCountDbPeer.doSelect(crit);
		if (objList.size() > 0) {
			InventoryCountDb inventoryCount = (InventoryCountDb)objList.get(0);
			return InventoryCount.getInventoryCount(inventoryCount);
		}
		return null;
    }

    // SQL

    /*
     *        <table name="INVENTORY_COUNT_DB" idMethod="native">
				<column name="INVENTORY_COUNT_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
				<column name="COMPANY_ID" required="true" type="INTEGER"/>
				<column name="IS_COMPLETE" type="SMALLINT" default="0"/>

				<column name="CREATION_DATE" required="true" type="DATE"/>
				<column name="MODIFICATION_DATE" required="false" type="DATE"/>
				<column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
				<column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

				<foreign-key foreignTable="COMPANY">
					<reference local="COMPANY_ID" foreign="COMPANYID"/>
				</foreign-key>
			</table>
     */

    // INSTANCE VARIABLES

    private InventoryCountDb inventory_count;
	private Vector<InventoryCountItemMapping> count;
	private HashMap<CheckoutCodeBean,BigDecimal> count_hash = new HashMap();

    // CONSTRUCTORS

    public
    InventoryCount()
    {
		inventory_count = new InventoryCountDb();
		isNew = true;
    }

    public
    InventoryCount(InventoryCountDb _inventory_count)
    {
		inventory_count = _inventory_count;
		isNew = false;
    }

    // INSTANCE METHODS

	public void
	addCount(CheckoutCodeBean _code, int _count)
		throws TorqueException, ObjectNotFoundException
	{
		Vector items = this.getItems();
		Iterator itr = items.iterator();
		BigDecimal c = count_hash.get(_code);
		BigDecimal count_bd = new BigDecimal(_count);
		count_hash.put(_code, count_bd);
		if (c != null)
		{
			// current count for this item exists.  replace current mapping

			while (itr.hasNext())
			{
				InventoryCountItemMapping obj = (InventoryCountItemMapping)itr.next();
				if (obj.getCheckoutCodeId() == _code.getId())
				{
					obj.setCount(count_bd);
					return;
				}
			}
		}

		InventoryCountItemMapping obj = new InventoryCountItemMapping();
		obj.setInventoryCountDbId(this.getId());
		obj.setCheckoutCodeId(_code.getId());
		obj.setCount(count_bd);
		items.addElement(obj);
	}

	public void
	finishAndApplyChanges()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		/*
		Iterator itr = this.getItems().iterator();
		while (itr.hasNext())
		{
			InventoryCountItemMapping item_count = (InventoryCountItemMapping)itr.next();

		}
		 *
		 */

		Iterator itr = CheckoutCodeBean.getCheckoutCodes(this.getCompany(), CheckoutCodeBean.INVENTORY_TYPE).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean checkout_code = (CheckoutCodeBean)itr.next();
			int on_hand_qty = this.getCount(checkout_code);
			checkout_code.setOnHandQuantity((short)on_hand_qty);
			checkout_code.save();
		}

		this.setIsComplete(true);
		this.save();
	}

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(inventory_count.getCompanyId());
	}

	public int
	getCompanyId()
	{
		return inventory_count.getCompanyId();
	}

	public int
	getCount(CheckoutCodeBean _code)
		throws TorqueException, ObjectNotFoundException
	{
		this.getItems();
		BigDecimal c = count_hash.get(_code);
		if (c == null)
			return 0;
		return c.intValue();
	}

	public Date
	getInventoryCountDate()
	{
		return inventory_count.getCreationDate();
	}

	public String
	getInventoryCountDateString()
	{
		if (inventory_count.getCreationDate() == null)
			return CUBean.getUserDateString(new Date());
		return CUBean.getUserDateString(inventory_count.getCreationDate());
	}

	public Date
	getInventoryCountCompletionDate()
	{
		return inventory_count.getModificationDate();
	}

    public int
    getId()
    {
		return inventory_count.getInventoryCountDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return this.getCompany().getLabel() + " Inventory Count";
    }

	public Vector
	getItems()
		throws TorqueException, ObjectNotFoundException
	{
		if (count == null)
		{
			count = new Vector();

			Criteria crit = new Criteria();
			crit.add(InventoryCountItemMappingPeer.INVENTORY_COUNT_DB_ID, this.getId());
			Iterator itr = InventoryCountItemMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				InventoryCountItemMapping obj = (InventoryCountItemMapping)itr.next();
				count.addElement(obj);
				count_hash.put(CheckoutCodeBean.getCheckoutCode(obj.getCheckoutCodeId()), obj.getCount());
			}
		}
		return count;
	}

    public String
    getValue()
    {
		return inventory_count.getInventoryCountDbId() + "";
    }

    protected void
    insertObject()
		throws Exception
    {
		inventory_count.setCreationDate(new Date());
		inventory_count.save();

		this.saveItems();
    }

	public boolean
	isComplete()
	{
		return inventory_count.getIsComplete() == (short)1;
	}

	private void
    saveItems()
		throws TorqueException, Exception
    {
		/*
		 *

			<table name="INVENTORY_COUNT_ITEM_MAPPING">
				<column name="INVENTORY_COUNT_ITEM_MAPPING_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
				<column name="INVENTORY_COUNT_DB_ID" required="true" type="INTEGER"/>
				<column name="CHECKOUT_CODE_ID" required="true" type="INTEGER"/>
				<column name="COUNT" required="true" scale="2" size="7" type="DECIMAL"/>

				<foreign-key foreignTable="INVENTORY_COUNT_DB">
					<reference local="INVENTORY_COUNT_DB_ID" foreign="INVENTORY_COUNT_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="CHECKOUT_CODE">
					<reference local="CHECKOUT_CODE_ID" foreign="CHECKOUT_CODE_ID"/>
				</foreign-key>
			</table>
		 */

		if (this.count != null)
		{
			//System.out.println("sizer >" + this.items.size());

			HashMap db_count_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(InventoryCountItemMappingPeer.INVENTORY_COUNT_DB_ID, this.getId());
			Iterator itr = InventoryCountItemMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				InventoryCountItemMapping value = (InventoryCountItemMapping)itr.next();
				//System.out.println("found existing ass... " + value.getInventoryCountItemMappingId());
				Integer key = new Integer(value.getInventoryCountItemMappingId());
				db_count_hash.put(key, value);
			}

			//System.out.println("num items >" + this.items.size());
			itr = this.count.iterator();
			while (itr.hasNext())
			{
				InventoryCountItemMapping item = (InventoryCountItemMapping)itr.next();
				Integer key = new Integer(item.getInventoryCountItemMappingId());
				InventoryCountItemMapping obj = (InventoryCountItemMapping)db_count_hash.remove(key);
				if ((obj == null) || (key.intValue() == 0))
				{
					// association does not exist in db.  need to create

					item.setInventoryCountDbId(this.getId());
					item.save();
				}
				else
				{
					// association exists, but the values may be different.  need to update...

					obj.setCount(item.getCount());
					obj.setCheckoutCodeId(item.getCheckoutCodeId());
					obj.save();
				}
			}

			itr = db_count_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				//System.out.println("key >" + key.intValue());
				InventoryCountItemMapping obj = (InventoryCountItemMapping)db_count_hash.get(key);
				InventoryCountItemMappingPeer.doDelete(obj);
			}

			this.count = null;
		}
    }

    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			inventory_count.setCreatePersonId(_person.getId());
		else
			inventory_count.setModifyPersonId(_person.getId());
    }

    public void
    setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
    {
		inventory_count.setCompanyId(_company.getId());
    }

	public void
	setItems(Vector _count)
		throws TorqueException, ObjectNotFoundException
	{
		count = _count;

		Iterator count_itr = count.iterator();
		while (count_itr.hasNext())
		{
			InventoryCountItemMapping obj = (InventoryCountItemMapping)count_itr.next();
			count_hash.put(CheckoutCodeBean.getCheckoutCode(obj.getCheckoutCodeId()), obj.getCount());
		}

	}

	public void
	setIsComplete(boolean _complete)
	{
		inventory_count.setIsComplete(_complete ? (short)1 : (short)0);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		inventory_count.setModificationDate(new Date());
		inventory_count.save();

		this.saveItems();
    }
}