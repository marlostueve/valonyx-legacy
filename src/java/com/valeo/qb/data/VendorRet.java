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
VendorRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,VendorRet> hash = new HashMap<Integer,VendorRet>(11);
	private static HashMap<UKOnlineCompanyBean,Date> last_update_hash = new HashMap<UKOnlineCompanyBean,Date>(11);

	// CLASS METHODS

	public static Date
	getLastUpdateDate(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		Date last_update = VendorRet.last_update_hash.get(_company);
		if (last_update == null)
		{
			Criteria crit = new Criteria();
			crit.add(VendorRetDbPeer.COMPANY_ID, _company.getId());
			crit.addDescendingOrderByColumn(VendorRetDbPeer.TIME_MODIFIED);
			List objList = VendorRetDbPeer.doSelect(crit);
			if (objList.size() > 0)
			{
				VendorRetDb vendor_obj = (VendorRetDb)objList.get(0);
				last_update = vendor_obj.getTimeModified();

				VendorRet.last_update_hash.put(_company, last_update);
			}
		}

		return last_update;
	}

    public static VendorRet
    getVendor(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		VendorRet obj = (VendorRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(VendorRetDbPeer.VENDOR_RET_DB_ID, _id);
			List objList = VendorRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Vendor with id: " + _id);

			obj = VendorRet.getVendor((VendorRetDb)objList.get(0));
		}

		return obj;
    }

    private static VendorRet
    getVendor(VendorRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getVendorRetDbId());
		VendorRet obj = (VendorRet)hash.get(key);
		if (obj == null)
		{
			obj = new VendorRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static VendorRet
    getVendor(CompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(VendorRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(VendorRetDbPeer.LIST_I_D, _list_id);
		List obj_list = VendorRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return VendorRet.getVendor((VendorRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Vendor with list id: " + _list_id);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Vendor with list id: " + _list_id);
    }

    public static Vector
    getVendors(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(VendorRetDbPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(VendorRetDbPeer.NAME);
		Iterator obj_itr = VendorRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(VendorRet.getVendor((VendorRetDb)obj_itr.next()));

		return vec;
    }
	
	private static Vector common_vendors = null;

    public static Vector
    getCommonVendors(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		if (common_vendors == null) {
			common_vendors = new Vector();
		
			/*
			Vector vec = new Vector();
			Criteria crit = new Criteria();
			crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
			Iterator obj_itr = VendorRetDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext()) {
				vec.addElement(VendorRet.getVendor((VendorRetDb)obj_itr.next()));
			}
			*/
			
			Criteria crit = new Criteria();
			
			crit.addJoin(VendorRetDbPeer.VENDOR_RET_DB_ID, CheckoutCodePeer.VENDOR_ID);
			crit.add(VendorRetDbPeer.COMPANY_ID, _company.getId());
			crit.addAscendingOrderByColumn(VendorRetDbPeer.NAME);
			crit.setDistinct();
			System.out.println("getVendorsSortCommon crit >" + crit.toString());
			Iterator obj_itr = VendorRetDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext()) {
				common_vendors.addElement(VendorRet.getVendor((VendorRetDb)obj_itr.next()));
			}
		
		}

		return common_vendors;
    }

    public static Vector
    getVendors(CompanyBean _company, Date _last_update, boolean _only_previously_synced)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(VendorRetDbPeer.COMPANY_ID, _company.getId());
		if (_last_update != null)
			crit.add(VendorRetDbPeer.TIME_MODIFIED, _last_update, Criteria.GREATER_EQUAL);
		if (_only_previously_synced)
			crit.add(VendorRetDbPeer.LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(VendorRetDbPeer.NAME);
		Iterator obj_itr = VendorRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(VendorRet.getVendor((VendorRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getNewVendors(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(VendorRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(VendorRetDbPeer.LIST_I_D, (Object)"", Criteria.ISNULL);
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = VendorRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(VendorRet.getVendor((VendorRetDb)obj_itr.next()));

		return vec;
    }

	// INSTANCE VARIABLES

	private VendorRetDb vendor;

	private String termsListID;
	private String termsFullName;

	private String address_1;
	private String address_2;
	private String city;
	private String state;
	private String zip;

	private String name;

	// CONSTRUCTORS

	public
	VendorRet()
	{
		vendor = new VendorRetDb();
		isNew = true;
	}

	public
	VendorRet(VendorRetDb _vendor)
	{
		vendor = _vendor;
		isNew = false;
	}

	// INSTANCE METHODS
	
	public boolean
	equals(VendorRet _vendor) {
		if (_vendor == null) {
			return false;
		}
		return ( this.getId() == _vendor.getId() );
	}

	public AddressBean
	getAddress()
		throws TorqueException, ObjectNotFoundException
	{
		return AddressBean.getAddress(vendor.getVendorAddress());
	}

	public float
	getBalance()
	{
		return vendor.getBalance().floatValue();
	}

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(vendor.getCompanyId());
	}

	public String
	getEditSequence()
	{
		return vendor.getEditSequence();
	}

	public int
	getId()
	{
		return vendor.getVendorRetDbId();
	}

	public String
	getLabel()
	{
		return this.getName();
	}

	public String
	getListID()
	{
		return vendor.getListID();
	}

	public String
	getName()
	{
		String str = vendor.getName();
		if (str == null)
			return "";
		return str;
	}

	public Date
	getTimeCreated()
	{
		return vendor.getTimeCreated();
	}

	public Date
	getTimeModified()
	{
		return vendor.getTimeModified();
	}

	public String
	getValue()
	{
		return vendor.getVendorRetDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		// see if this vendor conflicts with any others...

		Criteria crit = new Criteria();
		crit.add(VendorRetDbPeer.COMPANY_ID, vendor.getCompanyId());
		if ((vendor.getListID() != null) && (vendor.getListID().length() > 0))
			crit.add(VendorRetDbPeer.LIST_I_D, vendor.getListID());
		else
			crit.add(VendorRetDbPeer.NAME, vendor.getName());
		List obj_list = VendorRetDbPeer.doSelect(crit);
		if (obj_list.size() > 0)
			throw new ObjectAlreadyExistsException("Matching Vendor already exists.");

		// ensure that company id is set...

		if (vendor.getCompanyId() == 0)
			throw new IllegalValueException("Company not set for " + this.getLabel());

		maintainTerms();
		maintainAddress();

		if (vendor.getTimeCreated() == null)
			vendor.setTimeCreated(new Date());

		vendor.save();
		this.updateLastUpdateTime();
    }

	public boolean
	isActive()
	{
		return (vendor.getIsActive() == (short)1);
	}

	private void
	maintainAddress()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
	{
		if (this.address_1 != null)
		{
			AddressBean vendor_address;

			try
			{
				vendor_address = AddressBean.getAddress(vendor.getVendorAddress());
			}
			catch (ObjectNotFoundException x)
			{
				vendor_address = new AddressBean();
			}

			vendor_address.setType(AddressBean.DEFAULT_ADDRESS_TYPE);
			vendor_address.setStreet1(this.address_1);
			vendor_address.setStreet2(this.address_2);
			vendor_address.setCity(this.city);
			vendor_address.setState(this.state);
			vendor_address.setZipCode(this.zip);
			vendor_address.setCompany(this.getCompany());
			vendor_address.save();

			vendor.setVendorAddress(vendor_address.getId());
		}
	}

	private void
	maintainTerms()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
	{
		// find the tax line stuff

		if (this.termsListID != null)
		{
			Criteria crit = new Criteria();
			crit.add(TermsRefDbPeer.COMPANY_ID, vendor.getCompanyId());
			crit.add(TermsRefDbPeer.LIST_I_D, this.termsListID);
			List obj_list = TermsRefDbPeer.doSelect(crit);
			if (obj_list.size() == 1)
			{
				TermsRefDb terms_obj = (TermsRefDb)obj_list.get(0);
				vendor.setTermsRefDbId(terms_obj.getTermsRefDbId());
			}
			else if (obj_list.size() == 0)
			{
				System.out.println("Terms not found for ListID >" + this.termsListID);

				TermsRefDb terms_obj = new TermsRefDb();
				terms_obj.setCompanyId(vendor.getCompanyId());
				terms_obj.setListID(this.termsListID);
				terms_obj.setFullName(this.termsFullName);
				terms_obj.save();

				vendor.setTermsRefDbId(terms_obj.getTermsRefDbId());
			}
			else
				throw new UniqueObjectNotFoundException("Unique Terms not found >" + this.termsListID);
		}
	}

	public void
	setBalance(float _total)
	{
		vendor.setBalance(new BigDecimal(_total));
	}

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		vendor.setCompanyId(_company.getId());
	}

	public void
	setEditSequence(String _edit_sequence)
	{
		vendor.setEditSequence(_edit_sequence);
	}

	public void
	setListID(String _listID)
		throws TorqueException
	{
		if (_listID != null)
			vendor.setListID(_listID);
	}

	public void
	setIsActive(boolean _active)
	{
		vendor.setIsActive(_active ? (short)1 : (short)0);
	}

	public void
	setName(String _name)
	{
		vendor.setName(_name);
	}

	public void
	setNameFromParse(String _name)
	{
		if (this.name == null)
			this.name = _name;
		else
			name = name + _name;
		vendor.setName(name);
	}

	public String
	getPhone()
	{
		String str = vendor.getPhone();
		if (str == null)
			return "";
		return str;
	}

	public void
	setPhone(String _str)
	{
		System.out.println("SETTGIN PHONE >" + _str);
		vendor.setPhone(_str);
	}

	public String
	getFax()
	{
		String str = vendor.getFax();
		if (str == null)
			return "";
		return str;
	}

	public void
	setFax(String _str)
	{
		vendor.setFax(_str);
	}

	public String
	getAccountNumber()
	{
		String str = vendor.getAccountNumber();
		if (str == null)
			return "";
		return str;
	}

	public void
	setAccountNumber(String _str)
	{
		vendor.setAccountNumber(_str);
	}

	public String
	getTermsListID()
	{
		return this.termsListID;
	}

	public void
	setTermsListID(String _str)
	{
		this.termsListID = _str;
	}

	public String
	getTermsFullName()
	{
		return this.termsFullName;
	}

	public void
	setTermsFullName(String _str)
	{
		this.termsFullName = _str;
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		vendor.setTimeCreated(_timeCreated);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		vendor.setTimeModified(_timeModified);
	}

	public String
	getAddress1()
	{
		return address_1;
	}

	public void
	setAddress1(String _str)
	{
		System.out.println("setAddress1() invoked >" + _str);
		address_1 = _str;
	}

	public String
	getAddress2()
	{
		return address_2;
	}

	public void
	setAddress2(String _str)
	{
		address_2 = _str;
	}

	public String
	getCity()
	{
		return city;
	}

	public void
	setCity(String _str)
	{
		city = _str;
	}

	public String
	getState()
	{
		return state;
	}

	public void
	setState(String _str)
	{
		state = _str;
	}

	public String
	getZip()
	{
		return zip;
	}

	public void
	setZip(String _str)
	{
		zip = _str;
	}

	public boolean
	isVendorEligibleFor1099()
	{
		return (vendor.getIsVendorEligibleFor1099() == (short)1) ? true : false;
	}

	public void
	setIsVendorEligibleFor1099(boolean _boolean)
	{
		vendor.setIsVendorEligibleFor1099(_boolean ? (short)1 : (short)0);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		maintainTerms();
		maintainAddress();

		if (vendor.getTimeModified() == null)
			vendor.setTimeModified(new Date());

		vendor.save();
		this.updateLastUpdateTime();
    }

	private void
	updateLastUpdateTime()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Date timeModified = this.getTimeModified();
		if (timeModified != null)
		{
			UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(vendor.getCompanyId());
			Date last_update = VendorRet.last_update_hash.get(company);
			if ((last_update == null) || timeModified.after(last_update))
			{
				Calendar mod_time = Calendar.getInstance();
				mod_time.setTime(timeModified);
				last_update = mod_time.getTime();
				VendorRet.last_update_hash.put(company, last_update);
			}
		}
	}
}