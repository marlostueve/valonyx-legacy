/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbpos.data;

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
CustomerRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES
	
	private static Date last_update;
    
    protected static HashMap<Integer,CustomerRet> hash = new HashMap<Integer,CustomerRet>(11);
	
	// CLASS METHODS
	
	public static Date
	getLastUpdateDate(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		if (last_update == null)
		{
			Criteria crit = new Criteria();
			crit.add(CustomerRetDbPeer.COMPANY_ID, _company.getId());
			crit.addDescendingOrderByColumn(CustomerRetDbPeer.TIME_MODIFIED);
			List objList = CustomerRetDbPeer.doSelect(crit);
			System.out.println("LAST UPDATE CRIT >");
			if (objList.size() > 0)
			{
				CustomerRetDb customer_obj = (CustomerRetDb)objList.get(0);
				System.out.println("customer_obj >" + customer_obj.getFullName());
				last_update = customer_obj.getTimeModified();
			}
		}

		System.out.println("last_update >" + last_update);
		
		return last_update;
	}

    public static CustomerRet
    getCustomer(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		CustomerRet obj = (CustomerRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(CustomerRetDbPeer.CUSTOMER_RET_DB_ID, _id);
			List objList = CustomerRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Customer with id: " + _id);

			obj = CustomerRet.getCustomer((CustomerRetDb)objList.get(0));
		}

		return obj;
    }

    private static CustomerRet
    getCustomer(CustomerRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getCustomerRetDbId());
		CustomerRet obj = (CustomerRet)hash.get(key);
		if (obj == null)
		{
			obj = new CustomerRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }
	
	public static CustomerRet
	getCustomer(CompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(CustomerRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(CustomerRetDbPeer.LIST_I_D, _list_id);
		List obj_list = CustomerRetDbPeer.doSelect(crit);
		if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Customer not found for ListID >" + _list_id);
		else if (obj_list.size() == 1)
			return CustomerRet.getCustomer((CustomerRetDb)obj_list.get(0));
		else
			throw new UniqueObjectNotFoundException("Unique customer not found for ListID >" + _list_id);
	}

	public static CustomerRet
	getCustomerQBFS(CompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(CustomerRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(CustomerRetDbPeer.QB_LIST_I_D, _list_id);
		List obj_list = CustomerRetDbPeer.doSelect(crit);
		if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Customer not found for ListID >" + _list_id);
		else if (obj_list.size() == 1)
			return CustomerRet.getCustomer((CustomerRetDb)obj_list.get(0));
		else
		{
			// I can't allow this to happen.  gonna have to delete the extras...

			Iterator itr = obj_list.iterator();
			if (itr.hasNext())
				itr.next();
			while (itr.hasNext())
			{
				CustomerRetDb obj = (CustomerRetDb)itr.next();
				CustomerRetDbPeer.doDelete(obj);
			}
			return CustomerRet.getCustomer((CustomerRetDb)obj_list.get(0));

			//throw new UniqueObjectNotFoundException("Unique customer not found for ListID >" + _list_id);
		}
	}

	public static CustomerRet
	getCustomerByFirstLastPhone(CompanyBean _company, String _first_name, String _last_name, String _phone_str)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(CustomerRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(CustomerRetDbPeer.FIRST_NAME, _first_name);
		crit.add(CustomerRetDbPeer.LAST_NAME, _last_name);
		crit.add(CustomerRetDbPeer.PHONE, _phone_str);
		List obj_list = CustomerRetDbPeer.doSelect(crit);
		if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Customer not found for >" + _first_name + " " + _last_name + " - phone >" + _phone_str);
		else if (obj_list.size() == 1)
			return CustomerRet.getCustomer((CustomerRetDb)obj_list.get(0));
		else
			throw new UniqueObjectNotFoundException("Customer not found for >" + _first_name + " " + _last_name + " - phone >" + _phone_str);
	}

    public static Vector
    getCustomers(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(CustomerRetDbPeer.COMPANY_ID, _company.getId());
		Iterator obj_itr = CustomerRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(CustomerRet.getCustomer((CustomerRetDb)obj_itr.next()));
		
		return vec;
    }

    public static void
    resetCustomers(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
    {
		Criteria crit = new Criteria();
		crit.add(CustomerRetDbPeer.COMPANY_ID, _company.getId());
		Iterator obj_itr = CustomerRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			CustomerRetDb obj = (CustomerRetDb)obj_itr.next();
			obj.setIsActive(null);
			obj.save();
		}
    }

    public static void
    inactivateCustomersNoLongerInQB(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
    {
		System.out.println("inactivateCustomersNoLongerInQB invoked");

		int num_deleted = 0;
		Criteria crit = new Criteria();
		crit.add(CustomerRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(CustomerRetDbPeer.IS_ACTIVE, (Object)"", Criteria.ISNULL);
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = CustomerRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			CustomerRetDb obj = (CustomerRetDb)obj_itr.next();

			// find the corresponding person

			try
			{

				if (obj.getQbListID() != null)
				{
					System.out.println("INACTIVATE SCAN >" + obj.getFullName() + " - " + obj.getQbListID());

					UKOnlinePersonBean person_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPersonByQBListId(_company, obj.getQbListID(), false);
					
					if (person_obj.isDeleted())
						System.out.println("Already Deleted");
					else
					{
						System.out.println("\"deleting\" person >" + person_obj.getLabel());
						num_deleted++;
						person_obj.setIsDeleted(true);
						person_obj.save();
					}
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}

		}

		System.out.println("num_deleted >" + num_deleted);
    }
	
	// INSTANCE VARIABLES
	
	private CustomerRetDb customer;
	
	// CONSTRUCTORS
	
	public
	CustomerRet()
	{
		customer = new CustomerRetDb();
		isNew = true;
	}
	
	public
	CustomerRet(CustomerRetDb _customer)
	{
		customer = _customer;
		isNew = false;
	}
	
	// INSTANCE METHODS
	
	public float
	getAccountBalance()
	{
		if (customer.getAccountBalance() != null)
			return customer.getAccountBalance().floatValue();
		else
			return 0f;
	}

	public String
	getEMail()
	{
		return customer.getEMail();
	}

	public String
	getFirstName()
	{
		return customer.getFirstName();
	}

	public String
	getFullName()
	{
		return customer.getFullName();
	}

	public String
	getLastName()
	{
		return customer.getLastName();
	}
	
	public String
	getListID()
	{
		return customer.getListID();
	}
	
	public int
	getId()
	{
		return customer.getCustomerRetDbId();
	}

	public String
	getIsActive()
	{
		return customer.getIsActive();
	}
	
	public String
	getLabel()
	{
		return customer.getFullName();
	}
	
	public String
	getRequestId()
	{
		return customer.getRequestID();
	}
	
	public Date
	getLastSaleDate()
	{
		return customer.getLastSale();
	}

	public String
	getPhone()
	{
		return customer.getPhone();
	}

	public String
	getPhone2()
	{
		return customer.getPhone2();
	}

	public short
	getPriceLevelNumber()
		throws TorqueException
	{
		return customer.getPriceLevelNumber();
	}

	public Date
	getTimeModified()
	{
		return customer.getTimeModified();
	}

	public String
	getQBFSListID()
		throws TorqueException
	{
		return customer.getQbListID();
	}
	
	public String
	getValue()
	{
		return customer.getCustomerRetDbId() + "";
	}
    
    protected void
    insertObject()
		throws Exception
    {
		// see if this sales receipt conflicts with any others...
		
		Criteria crit = new Criteria();
		crit.add(CustomerRetDbPeer.COMPANY_ID, customer.getCompanyId());
		if (customer.getListID() != null)
			crit.add(CustomerRetDbPeer.LIST_I_D, customer.getListID());
		else if (customer.getQbListID() != null)
			crit.add(CustomerRetDbPeer.QB_LIST_I_D, customer.getQbListID());
		List obj_list = CustomerRetDbPeer.doSelect(crit);
		if (obj_list.size() > 0)
			throw new ObjectAlreadyExistsException("Matching customer already exists.");
		
		customer.save();
    }
	
	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		customer.setCompanyId(_company.getId());
	}

	public void
	setEMail(String _email)
		throws TorqueException
	{
		customer.setEMail(_email);
	}
	
	public void
	setFirstName(String _firstName)
	{
		customer.setFirstName(_firstName);
	}
	
	public void
	setFullName(String _fullName)
	{
		customer.setFullName(_fullName);
	}

	public void
	setLastName(String _lastName)
	{
		customer.setLastName(_lastName);
	}
	
	public void
	setIsActive(boolean _active)
	{
		customer.setIsActive(_active ? "true" : "false");
	}

	public boolean
	isActive()
	{
		return (customer.getIsActive() != null) && customer.getIsActive().toLowerCase().equals("true");
	}

	public void
	setEditSequence(String _str)
		throws TorqueException
	{
		System.out.println("SETTING EDIT SEQUENCE >" + _str);
		customer.setEditSequence(_str);
	}

	public String
	getEditSequence()
		throws TorqueException
	{
		return customer.getEditSequence();
	}
	
	public void
	setListID(String _customerListID)
		throws TorqueException
	{
		customer.setListID(_customerListID);
	}

	public void
	setQBFSListID(String _customerListID)
		throws TorqueException
	{
		customer.setQbListID(_customerListID);
	}

	public void
	setPhone(String _phone)
		throws TorqueException
	{
		customer.setPhone(_phone);
	}

	public void
	setPhone2(String _phone)
		throws TorqueException
	{
		customer.setPhone2(_phone);
	}

	public void
	setPriceLevelNumber(short _price_level_number)
		throws TorqueException
	{
		customer.setPriceLevelNumber(_price_level_number);
	}
	
	public void
	setRequestId(String _request_id)
	{
		customer.setRequestID(_request_id);
	}
	
	public void
	setAccountBalance(float _subtotal)
	{
		customer.setAccountBalance(new BigDecimal(_subtotal));
	}
	
	public boolean
	setCustomerRet(CustomerRet _obj, boolean _force_update)
		throws TorqueException
	{
		boolean modified = false;
		
		Date obj_modification_date = _obj.getTimeModified();
		Date customer_modification_date = customer.getTimeModified();
		if (_force_update || ( (customer_modification_date == null) || obj_modification_date.after(customer_modification_date) || (customer.getEditSequence() == null) ) )
		{
			if (customer.getAccountBalance() == null || !customer.getAccountBalance().equals(_obj.getAccountBalance()))
			{
				customer.setAccountBalance(new BigDecimal(_obj.getAccountBalance()));
				modified = true;
			}
			
			if (customer.getEditSequence() == null || !customer.getEditSequence().equals(_obj.getEditSequence()))
			{
				customer.setEditSequence(_obj.getEditSequence());
				modified = true;
			}
			
			if (customer.getEMail() == null || !customer.getEMail().equals(_obj.getEMail()))
			{
				customer.setEMail(_obj.getEMail());
				modified = true;
			}
			
			if (customer.getFirstName() == null || !customer.getFirstName().equals(_obj.getFirstName()))
			{
				customer.setFirstName(_obj.getFirstName());
				modified = true;
			}
			
			if (customer.getFullName() == null || !customer.getFullName().equals(_obj.getFullName()))
			{
				customer.setFullName(_obj.getFullName());
				modified = true;
			}
			
			if (customer.getLastName() == null || !customer.getLastName().equals(_obj.getLastName()))
			{
				customer.setLastName(_obj.getLastName());
				modified = true;
			}
			
			if (_obj.getLastSaleDate() != null)
			{
				if (customer.getLastSale() == null || customer.getLastSale().compareTo(_obj.getLastSaleDate()) != 0)
				{
					customer.setLastSale(_obj.getLastSaleDate());
					modified = true;
				}
			}
			
			if (customer.getPhone() == null || !customer.getPhone().equals(_obj.getPhone()))
			{
				customer.setPhone(_obj.getPhone());
				modified = true;
			}
			
			if (customer.getPhone2() == null || !customer.getPhone2().equals(_obj.getPhone2()))
			{
				customer.setPhone2(_obj.getPhone2());
				modified = true;
			}
			
			if (customer.getPriceLevelNumber() != _obj.getPriceLevelNumber())
			{
				customer.setPriceLevelNumber(_obj.getPriceLevelNumber());
				modified = true;
			}
			
			if (customer.getRequestID() == null || !customer.getRequestID().equals(_obj.getRequestId()))
			{
				customer.setRequestID(_obj.getRequestId());
				modified = true;
			}
			
			if (customer.getTimeModified() == null || customer.getTimeModified().compareTo(_obj.getTimeModified()) != 0)
			{
				customer.setTimeModified(_obj.getTimeModified());
				modified = true;
			}

			if (_obj.getIsActive() != null)
			{
				if (customer.getIsActive() == null || !customer.getIsActive().equals(_obj.getIsActive()))
				{
					customer.setIsActive(_obj.getIsActive());
					modified = true;
				}
			}
			else
				System.out.println("_obj.getIsActive() IS NULL >");
		}
		
		return modified;
	}
	
	public void
	setTimeModified(Date _timeModified)
	{
		System.out.println("setTimeModified() invoked in CustomerRet >" + _timeModified);
		customer.setTimeModified(_timeModified);
		
		if ((CustomerRet.last_update == null) || _timeModified.after(CustomerRet.last_update))
		{
			// apparently "greater than" still return time values that are equal in PoS.  so add a second to the last modified date, I guess..
			
			Calendar mod_time = Calendar.getInstance();
			mod_time.setTime(_timeModified);
			mod_time.add(Calendar.SECOND, 1);
			CustomerRet.last_update = mod_time.getTime();
		}
	}
	
	public void
	setLastSaleDate(Date _lastSaleDate)
	{
		customer.setLastSale(_lastSaleDate);
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		customer.save();
    }
}
