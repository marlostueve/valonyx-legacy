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
ItemSalesTaxGroupRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,ItemSalesTaxGroupRet> hash = new HashMap<Integer,ItemSalesTaxGroupRet>(11);
	private static HashMap<UKOnlineCompanyBean,Date> last_update_hash = new HashMap<UKOnlineCompanyBean,Date>(11);

	// CLASS METHODS

    public static ItemSalesTaxGroupRet
    getSalesTaxGroupItem(int _id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ItemSalesTaxGroupRet obj = (ItemSalesTaxGroupRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ItemSalesTaxGroupRetDbPeer.ITEM_SALES_TAX_GROUP_RET_DB_ID, _id);
			List objList = ItemSalesTaxGroupRetDbPeer.doSelect(crit);
			if (objList.size() == 1)
				return ItemSalesTaxGroupRet.getSalesTaxGroupItem((ItemSalesTaxGroupRetDb)objList.get(0));
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Item Sales Tax Group with id: " + _id);
			throw new UniqueObjectNotFoundException("Could not locate unique Item Sales Tax Group with id: " + _id);
		}

		return obj;
    }

    private static ItemSalesTaxGroupRet
    getSalesTaxGroupItem(ItemSalesTaxGroupRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getItemSalesTaxGroupRetDbId());
		ItemSalesTaxGroupRet obj = (ItemSalesTaxGroupRet)hash.get(key);
		if (obj == null)
		{
			obj = new ItemSalesTaxGroupRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static ItemSalesTaxGroupRet
    getSalesTaxGroupItem(CompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ItemSalesTaxGroupRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ItemSalesTaxGroupRetDbPeer.LIST_I_D, _list_id);
		List obj_list = ItemSalesTaxGroupRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ItemSalesTaxGroupRet.getSalesTaxGroupItem((ItemSalesTaxGroupRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Item Sales Tax Group with list id: " + _list_id);
		throw new UniqueObjectNotFoundException("Could not locate unique Item Sales Tax Group with list id: " + _list_id);
    }

    public static Vector
    getSalesTaxGroupItems(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ItemSalesTaxGroupRetDbPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(ItemSalesTaxGroupRetDbPeer.NAME);
		Iterator obj_itr = ItemSalesTaxGroupRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemSalesTaxGroupRet.getSalesTaxGroupItem((ItemSalesTaxGroupRetDb)obj_itr.next()));

		return vec;
    }

	// INSTANCE VARIABLES

	private ItemSalesTaxGroupRetDb group;
	private String name;

	private Vector<ItemSalesTaxRet> salesTaxItems;
	private Vector<String> salesTaxItemListIDs;

	// CONSTRUCTORS

	public
	ItemSalesTaxGroupRet()
	{
		group = new ItemSalesTaxGroupRetDb();
		isNew = true;
	}

	public
	ItemSalesTaxGroupRet(ItemSalesTaxGroupRetDb _group)
	{
		group = _group;
		isNew = false;
	}

	// INSTANCE METHODS

	public void
	addSalesTaxItem(ItemSalesTaxRet _item)
		throws TorqueException, ObjectNotFoundException
	{
		this.getSalesTaxItems();
		salesTaxItems.addElement(_item);
	}

	public void
	addSalesTaxItemListID(String _list_id)
		throws TorqueException, ObjectNotFoundException
	{
		if (salesTaxItemListIDs == null)
			salesTaxItemListIDs = new Vector<String>();
		salesTaxItemListIDs.addElement(_list_id);
	}

	/*
<table name="ITEM_SALES_TAX_GROUP_MAPPING">
    <column name="ITEM_SALES_TAX_GROUP_RET_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="ITEM_SALES_TAX_RET_DB_ID" primaryKey="true" required="true" type="INTEGER"/>

    <foreign-key foreignTable="ITEM_SALES_TAX_GROUP_RET_DB">
		<reference local="ITEM_SALES_TAX_GROUP_RET_DB_ID" foreign="ITEM_SALES_TAX_GROUP_RET_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="ITEM_SALES_TAX_RET_DB">
		<reference local="ITEM_SALES_TAX_RET_DB_ID" foreign="ITEM_SALES_TAX_RET_DB_ID"/>
    </foreign-key>
</table>
	 */

	public synchronized Vector
	getSalesTaxItems()
		throws TorqueException, ObjectNotFoundException
	{
		if (this.salesTaxItems == null)
		{
			this.salesTaxItems = new Vector();

			Criteria crit = new Criteria();
			crit.add(ItemSalesTaxGroupMappingPeer.ITEM_SALES_TAX_GROUP_RET_DB_ID, this.getId());
			Iterator itr = ItemSalesTaxGroupMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				ItemSalesTaxGroupMapping mapping_obj = (ItemSalesTaxGroupMapping)itr.next();
				ItemSalesTaxRet item = ItemSalesTaxRet.getSalesTaxItem(mapping_obj.getItemSalesTaxRetDbId());
				this.salesTaxItems.addElement(item);
			}
		}
		return this.salesTaxItems;
	}

	private void
	saveSalesTaxItems()
		throws TorqueException, Exception
	{
		if (this.salesTaxItemListIDs != null)
		{
			Iterator itr = this.salesTaxItemListIDs.iterator();
			while (itr.hasNext())
			{
				String list_id = (String)itr.next();
				this.addSalesTaxItem(ItemSalesTaxRet.getSalesTaxItem(this.getCompany(), list_id));
			}
		}
		
		if (this.salesTaxItems != null)
		{
			HashMap db_items_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(ItemSalesTaxGroupMappingPeer.ITEM_SALES_TAX_GROUP_RET_DB_ID, this.getId());
			Iterator itr = ItemSalesTaxGroupMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				ItemSalesTaxGroupMapping value = (ItemSalesTaxGroupMapping)itr.next();
				Integer key = new Integer(value.getItemSalesTaxRetDbId());
				db_items_hash.put(key, value);

				System.out.println("found existing ass... >" + key.intValue());
			}

			System.out.println("num members >" + this.salesTaxItems.size());

			itr = this.salesTaxItems.iterator();
			while (itr.hasNext())
			{
				ItemSalesTaxRet item = (ItemSalesTaxRet)itr.next();
				Integer key = new Integer(item.getId());
				ItemSalesTaxGroupMapping obj = (ItemSalesTaxGroupMapping)db_items_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("association does not exist in db.  need to create >" + key.intValue());

					obj = new ItemSalesTaxGroupMapping();
					obj.setItemSalesTaxRetDbId(item.getId());
					obj.setItemSalesTaxGroupRetDbId(this.getId());
					obj.save();
				}
			}

			itr = db_items_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				ItemSalesTaxGroupMapping obj = (ItemSalesTaxGroupMapping)db_items_hash.get(key);
				System.out.println("deleting...");
				ItemSalesTaxGroupMappingPeer.doDelete(obj);
			}
		}
	}

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(group.getCompanyId());
	}

	public String
	getEditSequence()
	{
		return group.getEditSequence();
	}

	public int
	getId()
	{
		return group.getItemSalesTaxGroupRetDbId();
	}

	public String
	getItemDesc()
	{
		return group.getItemDesc();
	}

	public String
	getLabel()
	{
		return this.getName();
	}

	public String
	getListID()
	{
		return group.getListID();
	}

	public String
	getName()
	{
		String str = group.getName();
		if (str == null)
			return "";
		return str;
	}

	public Date
	getTimeCreated()
	{
		return group.getTimeCreated();
	}

	public Date
	getTimeModified()
	{
		return group.getTimeModified();
	}

	public String
	getValue()
	{
		return group.getItemSalesTaxGroupRetDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		// see if this vendor conflicts with any others...

		Criteria crit = new Criteria();
		crit.add(ItemSalesTaxGroupRetDbPeer.COMPANY_ID, group.getCompanyId());
		if ((group.getListID() != null) && (group.getListID().length() > 0))
			crit.add(ItemSalesTaxGroupRetDbPeer.LIST_I_D, group.getListID());
		else
			crit.add(ItemSalesTaxGroupRetDbPeer.NAME, group.getName());
		List obj_list = ItemSalesTaxGroupRetDbPeer.doSelect(crit);
		if (obj_list.size() > 0)
			throw new ObjectAlreadyExistsException("Matching Item Sales Tax Group already exists.");

		// ensure that company id is set...

		if (group.getCompanyId() == 0)
			throw new IllegalValueException("Company not set for " + this.getLabel());

		if (group.getTimeCreated() == null)
			group.setTimeCreated(new Date());

		group.save();
		this.saveSalesTaxItems();
    }

	public boolean
	isActive()
	{
		return (group.getIsActive().equals("true"));
	}

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		group.setCompanyId(_company.getId());
	}

	public void
	setEditSequence(String _edit_sequence)
	{
		group.setEditSequence(_edit_sequence);
	}

	public void
	setListID(String _listID)
		throws TorqueException
	{
		if (_listID != null)
			group.setListID(_listID);
	}

	public void
	setIsActive(boolean _active)
	{
		group.setIsActive(_active ? "true" : "false");
	}

	public void
	setItemDesc(String _item_desc)
	{
		group.setItemDesc(_item_desc);
	}

	public void
	setName(String _name)
	{
		group.setName(_name);
	}

	public void
	setNameFromParse(String _name)
	{
		if (this.name == null)
			this.name = _name;
		else
			name = name + _name;
		group.setName(name);
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		group.setTimeCreated(_timeCreated);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		group.setTimeModified(_timeModified);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		if (group.getTimeModified() == null)
			group.setTimeModified(new Date());

		group.save();
		this.saveSalesTaxItems();
    }
}