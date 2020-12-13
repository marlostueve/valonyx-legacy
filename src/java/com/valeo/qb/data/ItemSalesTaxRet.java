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
ItemSalesTaxRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,ItemSalesTaxRet> hash = new HashMap<Integer,ItemSalesTaxRet>(11);
	private static HashMap<UKOnlineCompanyBean,Date> last_update_hash = new HashMap<UKOnlineCompanyBean,Date>(11);

	// CLASS METHODS

	public static Date
	getLastUpdateDate(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		Date last_update = ItemSalesTaxRet.last_update_hash.get(_company);
		if (last_update == null)
		{
			Criteria crit = new Criteria();
			crit.add(ItemSalesTaxRetDbPeer.COMPANY_ID, _company.getId());
			crit.addDescendingOrderByColumn(ItemSalesTaxRetDbPeer.TIME_MODIFIED);
			List objList = ItemSalesTaxRetDbPeer.doSelect(crit);
			if (objList.size() > 0)
			{
				ItemSalesTaxRetDb sales_tax_item_obj = (ItemSalesTaxRetDb)objList.get(0);
				last_update = sales_tax_item_obj.getTimeModified();

				ItemSalesTaxRet.last_update_hash.put(_company, last_update);
			}
		}

		return last_update;
	}

    public static ItemSalesTaxRet
    getSalesTaxItem(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ItemSalesTaxRet obj = (ItemSalesTaxRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ItemSalesTaxRetDbPeer.ITEM_SALES_TAX_RET_DB_ID, _id);
			List objList = ItemSalesTaxRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Sales Tax Item with id: " + _id);

			obj = ItemSalesTaxRet.getSalesTaxItem((ItemSalesTaxRetDb)objList.get(0));
		}

		return obj;
    }

    private static ItemSalesTaxRet
    getSalesTaxItem(ItemSalesTaxRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getItemSalesTaxRetDbId());
		ItemSalesTaxRet obj = (ItemSalesTaxRet)hash.get(key);
		if (obj == null)
		{
			obj = new ItemSalesTaxRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static ItemSalesTaxRet
    getSalesTaxItem(CompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ItemSalesTaxRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ItemSalesTaxRetDbPeer.LIST_I_D, _list_id);
		List obj_list = ItemSalesTaxRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ItemSalesTaxRet.getSalesTaxItem((ItemSalesTaxRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Item Sales Tax with list id: " + _list_id);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Item Sales Tax with list id: " + _list_id);
    }

    public static ItemSalesTaxRet
    getSalesTaxItem(CompanyBean _company, ValeoTaxCodeBean _tax_code)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ItemSalesTaxRetDbPeer.TAX_CODE_ID, _tax_code.getId());
		List obj_list = ItemSalesTaxRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ItemSalesTaxRet.getSalesTaxItem((ItemSalesTaxRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
		{
			// also do a search based on ListID
			
			try {
				ItemSalesTaxRet obj = ItemSalesTaxRet.getSalesTaxItem(_company, _tax_code.getQBListID());
				obj.setTaxCode(_tax_code);
				obj.save();
				return ItemSalesTaxRet.getSalesTaxItem(_company, _tax_code.getQBListID());
			}
			catch (Exception x) { x.printStackTrace(); }
			
			throw new ObjectNotFoundException("Could not locate Item Sales Tax for tax code: " + _tax_code.getLabel());
		}
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Item Sales Tax for tax code: " + _tax_code.getLabel());
    }
	
	public static ItemSalesTaxRet
	getSalesTaxItemForRequestID(String _requestID)
		throws TorqueException, ObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(ItemSalesTaxRetDbPeer.REQUEST_I_D, _requestID);
		List objList = ItemSalesTaxRetDbPeer.doSelect(crit);
		if (objList.size() != 1)
			throw new ObjectNotFoundException("Could not locate sales tax item for request: " + _requestID);

		return ItemSalesTaxRet.getSalesTaxItem((ItemSalesTaxRetDb)objList.get(0));
	}

    public static Vector
    getSalesTaxItems(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ItemSalesTaxRetDbPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(ItemSalesTaxRetDbPeer.NAME);
		Iterator obj_itr = ItemSalesTaxRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemSalesTaxRet.getSalesTaxItem((ItemSalesTaxRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getSalesTaxItems(CompanyBean _company, Date _last_update, boolean _only_previously_synced)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ItemSalesTaxRetDbPeer.COMPANY_ID, _company.getId());
		if (_last_update != null)
			crit.add(ItemSalesTaxRetDbPeer.TIME_MODIFIED, _last_update, Criteria.GREATER_EQUAL);
		if (_only_previously_synced)
			crit.add(ItemSalesTaxRetDbPeer.LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(ItemSalesTaxRetDbPeer.NAME);
		Iterator obj_itr = ItemSalesTaxRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemSalesTaxRet.getSalesTaxItem((ItemSalesTaxRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getNewSalesTaxItems(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ItemSalesTaxRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ItemSalesTaxRetDbPeer.LIST_I_D, (Object)"", Criteria.ISNULL);
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = ItemSalesTaxRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemSalesTaxRet.getSalesTaxItem((ItemSalesTaxRetDb)obj_itr.next()));

		return vec;
    }

	// INSTANCE VARIABLES

	private ItemSalesTaxRetDb sales_tax_item;

	private String taxVendorListID;
	private String taxVendorFullName;

	private String name;

	// CONSTRUCTORS

	public
	ItemSalesTaxRet()
	{
		sales_tax_item = new ItemSalesTaxRetDb();
		isNew = true;
	}

	public
	ItemSalesTaxRet(ItemSalesTaxRetDb _sales_tax_item)
	{
		sales_tax_item = _sales_tax_item;
		isNew = false;
	}

	// INSTANCE METHODS

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(sales_tax_item.getCompanyId());
	}

	public String
	getEditSequence()
	{
		return sales_tax_item.getEditSequence();
	}

	public int
	getId()
	{
		return sales_tax_item.getItemSalesTaxRetDbId();
	}
	
	public String
	getItemDesc()
	{
		return sales_tax_item.getItemDesc();
	}
	
	public String
	getItemDescString()
	{
		String str = sales_tax_item.getItemDesc();
		if (str == null)
			return "";
		return str;
	}

	public String
	getLabel()
	{
		return this.getName();
	}

	public String
	getListID()
	{
		return sales_tax_item.getListID();
	}

	public String
	getName()
	{
		String str = sales_tax_item.getName();
		if (str == null)
			return "";
		return str;
	}

	public BigDecimal
	getTaxRate()
	{
		return sales_tax_item.getTaxRate();
	}

	public Date
	getTimeCreated()
	{
		return sales_tax_item.getTimeCreated();
	}

	public Date
	getTimeModified()
	{
		return sales_tax_item.getTimeModified();
	}

	public String
	getValue()
	{
		return sales_tax_item.getItemSalesTaxRetDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		// see if this sales_tax_item conflicts with any others...

		Criteria crit = new Criteria();
		crit.add(ItemSalesTaxRetDbPeer.COMPANY_ID, sales_tax_item.getCompanyId());
		if ((sales_tax_item.getListID() != null) && (sales_tax_item.getListID().length() > 0))
			crit.add(ItemSalesTaxRetDbPeer.LIST_I_D, sales_tax_item.getListID());
		else
			crit.add(ItemSalesTaxRetDbPeer.NAME, sales_tax_item.getName());
		List obj_list = ItemSalesTaxRetDbPeer.doSelect(crit);
		if (obj_list.size() > 0)
			throw new ObjectAlreadyExistsException("Matching Sales Tax Item already exists.");

		// ensure that company id is set...

		if (sales_tax_item.getCompanyId() == 0)
			throw new IllegalValueException("Company not set for " + this.getLabel());

		maintainTaxVendor();

		if (sales_tax_item.getTimeCreated() == null)
			sales_tax_item.setTimeCreated(new Date());

		sales_tax_item.save();
		this.updateLastUpdateTime();
    }

	public boolean
	isActive()
	{
		return (sales_tax_item.getIsActive().equals("true"));
	}

	private void
	maintainTaxVendor()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
	{
		// find the tax line stuff

		if (this.taxVendorListID != null)
		{
			Criteria crit = new Criteria();
			crit.add(TaxVendorRefDbPeer.COMPANY_ID, sales_tax_item.getCompanyId());
			crit.add(TaxVendorRefDbPeer.LIST_I_D, this.taxVendorListID);
			List obj_list = TaxVendorRefDbPeer.doSelect(crit);
			if (obj_list.size() == 1)
			{
				TaxVendorRefDb terms_obj = (TaxVendorRefDb)obj_list.get(0);
				sales_tax_item.setTaxVendorRefDbId(terms_obj.getTaxVendorRefDbId());
			}
			else if (obj_list.size() == 0)
			{
				System.out.println("TaxVendor not found for ListID >" + this.taxVendorListID);

				TaxVendorRefDb terms_obj = new TaxVendorRefDb();
				terms_obj.setCompanyId(sales_tax_item.getCompanyId());
				terms_obj.setListID(this.taxVendorListID);
				terms_obj.setFullName(this.taxVendorFullName);
				terms_obj.save();

				sales_tax_item.setTaxVendorRefDbId(terms_obj.getTaxVendorRefDbId());
			}
			else
				throw new UniqueObjectNotFoundException("Unique TaxVendor not found >" + this.taxVendorListID);
		}
	}

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		sales_tax_item.setCompanyId(_company.getId());
	}

	public void
	setEditSequence(String _edit_sequence)
	{
		sales_tax_item.setEditSequence(_edit_sequence);
	}

	public void
	setItemDesc(String _item_desc)
		throws TorqueException
	{
		sales_tax_item.setItemDesc(_item_desc);
	}

	public void
	setListID(String _listID)
		throws TorqueException
	{
		if (_listID != null)
			sales_tax_item.setListID(_listID);
	}

	public void
	setIsActive(boolean _active)
	{
		sales_tax_item.setIsActive(_active ? "true" : "false");
	}

	public void
	setName(String _name)
	{
		sales_tax_item.setName(_name);
	}

	public void
	setNameFromParse(String _name)
	{
		if (this.name == null)
			this.name = _name;
		else
			name = name + _name;
		sales_tax_item.setName(name);
	}

	public void
	setTaxRate(BigDecimal _tax_rate)
	{
		sales_tax_item.setTaxRate(_tax_rate);
	}

	public String
	getTaxVendorListID()
	{
		return this.taxVendorListID;
	}

	public void
	setTaxVendorListID(String _str)
	{
		this.taxVendorListID = _str;
	}

	public String
	getTaxVendorFullName()
	{
		return this.taxVendorFullName;
	}

	public void
	setTaxVendorFullName(String _str)
	{
		this.taxVendorFullName = _str;
	}

	public VendorRet
	getTaxVendor()
		throws TorqueException, ObjectNotFoundException
	{
		return VendorRet.getVendor(sales_tax_item.getTaxVendorRefDbId());
	}

	public int
	getTaxVendorRefDbId()
	{
		return sales_tax_item.getTaxVendorRefDbId();
	}

	public void
	setTaxVendor(VendorRet _vendor)
		throws TorqueException
	{
		sales_tax_item.setTaxVendorRefDbId(_vendor.getId());
	}

	public void
	removeTaxVendor()
		throws TorqueException
	{
		sales_tax_item.setTaxVendorRefDbId(0);
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		sales_tax_item.setTimeCreated(_timeCreated);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		sales_tax_item.setTimeModified(_timeModified);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		maintainTaxVendor();

		if (sales_tax_item.getTimeModified() == null)
			sales_tax_item.setTimeModified(new Date());

		sales_tax_item.save();
		this.updateLastUpdateTime();
    }

	private void
	updateLastUpdateTime()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Date timeModified = this.getTimeModified();
		if (timeModified != null)
		{
			UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(sales_tax_item.getCompanyId());
			Date last_update = ItemSalesTaxRet.last_update_hash.get(company);
			if ((last_update == null) || timeModified.after(last_update))
			{
				Calendar mod_time = Calendar.getInstance();
				mod_time.setTime(timeModified);
				last_update = mod_time.getTime();
				ItemSalesTaxRet.last_update_hash.put(company, last_update);
			}
		}
	}
	
	public void
	setRequestID(String _str)
	{
		sales_tax_item.setRequestID(_str);
	}
	
	public String
	getRequestID()
	{
		return sales_tax_item.getRequestID();
	}
	
	public void
	setTaxCode(ValeoTaxCodeBean _code)
		throws TorqueException
	{
		sales_tax_item.setTaxCodeId(_code.getId());
	}
	
	public String
	getIsActive()
	{
		return sales_tax_item.getIsActive();
	}
	
	public ValeoTaxCodeBean
	getTaxCode()
		throws TorqueException, ObjectNotFoundException
	{
		return (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode(sales_tax_item.getTaxCodeId());
	}
	
	public boolean
	setItemRet(ItemSalesTaxRet _obj, boolean _force_update)
		throws TorqueException, UniqueObjectNotFoundException
	{
		boolean modified = false;
		
		Date item_modification_date = _obj.getTimeModified();
		Date code_modification_date = sales_tax_item.getTimeModified();
		if (_force_update || ( (code_modification_date == null) || item_modification_date.after(code_modification_date) ) )
		{
			// the passed object has been modified since this checkout code has
			
			if (sales_tax_item.getEditSequence() == null || !sales_tax_item.getEditSequence().equals(_obj.getEditSequence()))
			{
				sales_tax_item.setEditSequence(_obj.getEditSequence());
				modified = true;
			}
			
			if (sales_tax_item.getListID() == null || !sales_tax_item.getListID().equals(_obj.getListID()))
			{
				sales_tax_item.setListID(_obj.getListID());
				modified = true;
			}
			
			if (sales_tax_item.getName() == null || !sales_tax_item.getName().equals(_obj.getName()))
			{
				sales_tax_item.setName(_obj.getName());
				modified = true;
			}
			
			if (sales_tax_item.getItemDesc() == null || !sales_tax_item.getItemDesc().equals(_obj.getItemDesc()))
			{
				sales_tax_item.setItemDesc(_obj.getItemDesc());
				modified = true;
			}
			
			if (sales_tax_item.getTaxRate() == null || sales_tax_item.getTaxRate().compareTo(_obj.getTaxRate()) != 0)
			{
				sales_tax_item.setTaxRate(_obj.getTaxRate());
				modified = true;
			}
			
			if (sales_tax_item.getIsActive() == null || !sales_tax_item.getIsActive().equals(_obj.getIsActive()))
			{
				sales_tax_item.setIsActive(_obj.getIsActive());
				modified = true;
			}
			
			System.out.println("_obj.getVendorListID() >" + _obj.getTaxVendorListID());
			if (_obj.getTaxVendorListID() != null)
			{
				VendorRet existing_vendor = null;
				try
				{
					existing_vendor = this.getTaxVendor();
				}
				catch (ObjectNotFoundException x)
				{
				}
				if (existing_vendor == null || !existing_vendor.getListID().equals(_obj.getTaxVendorListID()))
				{
					try
					{
						VendorRet vendor_ret = VendorRet.getVendor(this.getCompany(), _obj.getTaxVendorListID());
						this.setTaxVendor(vendor_ret);
						modified = true;
					}
					catch (ObjectNotFoundException x)
					{
						x.printStackTrace();
					}
				}
			}
		}
		
		return modified;
		
	}
}