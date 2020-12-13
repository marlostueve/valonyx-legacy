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
AccountRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES
	
	public static final String CASH_IN_DRAWER_ACCOUNT_NAME = "Cash in Drawer";
	public static final String UNDEPOSITED_FUNDS_ACCOUNT_NAME = "Undeposited Funds";
	public static final String ACCOUNTS_RECEIVABLE_ACCOUNT_NAME = "Accounts Receivable";

    protected static HashMap<Integer,AccountRet> hash = new HashMap<Integer,AccountRet>(11);
	private static HashMap<UKOnlineCompanyBean,Date> last_update_hash = new HashMap<UKOnlineCompanyBean,Date>(11);

	// CLASS METHODS

	public static Date
	getLastUpdateDate(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		Date last_update = AccountRet.last_update_hash.get(_company);
		if (last_update == null)
		{
			Criteria crit = new Criteria();
			crit.add(AccountRetDbPeer.COMPANY_ID, _company.getId());
			crit.addDescendingOrderByColumn(AccountRetDbPeer.TIME_MODIFIED);
			List objList = AccountRetDbPeer.doSelect(crit);
			if (objList.size() > 0)
			{
				AccountRetDb account_obj = (AccountRetDb)objList.get(0);
				last_update = account_obj.getTimeModified();

				AccountRet.last_update_hash.put(_company, last_update);
			}
		}

		return last_update;
	}

    public static AccountRet
    getAccount(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		AccountRet obj = (AccountRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(AccountRetDbPeer.ACCOUNT_RET_DB_ID, _id);
			List objList = AccountRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Account with id: " + _id);

			obj = AccountRet.getAccount((AccountRetDb)objList.get(0));
		}

		return obj;
    }

    private static AccountRet
    getAccount(AccountRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getAccountRetDbId());
		AccountRet obj = (AccountRet)hash.get(key);
		if (obj == null)
		{
			obj = new AccountRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static AccountRet
    getAccount(CompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(AccountRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(AccountRetDbPeer.LIST_I_D, _list_id);
		List obj_list = AccountRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return AccountRet.getAccount((AccountRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Account with list id: " + _list_id);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Account with list id: " + _list_id);
    }

    public static AccountRet
    getAccount(CompanyBean _company, int _account_number)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(AccountRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(AccountRetDbPeer.ACCOUNT_NUMBER, _account_number);
		List obj_list = AccountRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return AccountRet.getAccount((AccountRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Sales Receipt with sales receipt number: " + _account_number);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Sales Receipt with sales receipt number: " + _account_number);
    }

    public static AccountRet
    getAccountByName(CompanyBean _company, String _name)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(AccountRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(AccountRetDbPeer.FULL_NAME, _name);
		List obj_list = AccountRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return AccountRet.getAccount((AccountRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Account with full name: " + _name);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Account with _name: " + _name);
    }

    public static Vector
    getAccounts(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(AccountRetDbPeer.COMPANY_ID, _company.getId());
		Iterator obj_itr = AccountRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(AccountRet.getAccount((AccountRetDb)obj_itr.next()));

		return vec;
    }

	// INSTANCE VARIABLES

	private AccountRetDb account;
	private String parent_list_id;

	private String tax_line_id;
	private String tax_line_name;

	// CONSTRUCTORS

	public
	AccountRet()
	{
		account = new AccountRetDb();
		isNew = true;
	}

	public
	AccountRet(AccountRetDb _account)
	{
		account = _account;
		isNew = false;
	}

	// INSTANCE METHODS

	public String
	getAccountNumber()
	{
		return account.getAccountNumber();
	}

	public String
	getAccountType()
	{
		return account.getAccountType();
	}

	public float
	getBalance()
	{
		return account.getBalance().floatValue();
	}

	public String
	getCashFlowClassification()
	{
		return account.getCashFlowClassification();
	}

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(account.getCompanyId());
	}

	public String
	getDesc()
	{
		return account.getDescription();
	}

	public String
	getEditSequence()
	{
		return account.getEditSequence();
	}

	public String
	getFullName()
	{
		return account.getFullName();
	}

	public int
	getId()
	{
		return account.getAccountRetDbId();
	}

	public String
	getLabel()
	{
		if (this.getAccountNumber() != null)
			return account.getAccountNumber() + ": " + account.getName();
		return account.getName();
	}

	public String
	getListID()
	{
		return account.getListID();
	}

	public String
	getParentListID()
	{
		return this.parent_list_id;
	}

	public String
	getName()
	{
		return account.getName();
	}

	public short
	getSublevel()
	{
		return account.getSublevel();
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

	public Date
	getTimeCreated()
	{
		return account.getTimeCreated();
	}

	public Date
	getTimeModified()
	{
		return account.getTimeModified();
	}

	public float
	getTotalBalance()
	{
		return account.getTotalBalance().floatValue();
	}

	public String
	getValue()
	{
		return account.getAccountRetDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		// see if this account conflicts with any others...

		Criteria crit = new Criteria();
		crit.add(AccountRetDbPeer.COMPANY_ID, account.getCompanyId());
		crit.add(AccountRetDbPeer.LIST_I_D, account.getListID());
		crit.or(AccountRetDbPeer.ACCOUNT_NUMBER, account.getAccountNumber());
		List obj_list = AccountRetDbPeer.doSelect(crit);
		if (obj_list.size() > 0)
			throw new ObjectAlreadyExistsException("Matching Account already exists.");
		
		// ensure that company id is set...
		
		if (account.getCompanyId() == 0)
			throw new IllegalValueException("Company not set for " + this.getLabel());

		maintainParent();
		maintainTaxInfo();

		account.save();

		this.updateLastUpdateTime();
    }

	public boolean
	isActive()
	{
		return (account.getIsActive() == (short)1);
	}

	private void
	maintainParent()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		// find the parent if it's set...

		if (parent_list_id != null)
		{
			AccountRet parent = AccountRet.getAccount(this.getCompany(), parent_list_id);
			account.setParentId(parent.getId());
		}
	}

	private void
	maintainTaxInfo()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
	{
		// find the tax line stuff

		if (this.tax_line_id != null)
		{
			Criteria crit = new Criteria();
			crit.add(TaxLineInfoRetDbPeer.COMPANY_ID, account.getCompanyId());
			crit.add(TaxLineInfoRetDbPeer.TAX_LINE_ID, tax_line_id);
			List obj_list = TaxLineInfoRetDbPeer.doSelect(crit);
			if (obj_list.size() == 1)
			{
				TaxLineInfoRetDb tax_info_obj = (TaxLineInfoRetDb)obj_list.get(0);
				account.setTaxLineInfoRetDbId(tax_info_obj.getTaxLineInfoRetDbId());
			}
			else if (obj_list.size() == 0)
			{
				// create it, I guess...

				TaxLineInfoRetDb tax_info_obj = new TaxLineInfoRetDb();
				tax_info_obj.setCompanyId(account.getCompanyId());
				tax_info_obj.setTaxLineId(Integer.parseInt(tax_line_id));
				tax_info_obj.setTaxLineName(this.tax_line_name);
				tax_info_obj.save();

				account.setTaxLineInfoRetDbId(tax_info_obj.getTaxLineInfoRetDbId());
			}
			else
				throw new UniqueObjectNotFoundException("Unique Tax Line Info not found >" + tax_line_id);
		}
	}

	public void
	setAccountNumber(String _account_number)
	{
		account.setAccountNumber(_account_number);
	}

	public void
	setAccountType(String _account_type)
	{
		account.setAccountType(_account_type);
	}

	public void
	setBalance(float _total)
	{
		account.setBalance(new BigDecimal(_total));
	}

	public void
	setCashFlowClassification(String _cash_flow_classification)
	{
		account.setCashFlowClassification(_cash_flow_classification);
	}

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		account.setCompanyId(_company.getId());
	}

	public void
	setDesc(String _desc)
	{
		account.setDescription(_desc);
	}

	public void
	setEditSequence(String _edit_sequence)
	{
		account.setEditSequence(_edit_sequence);
	}

	public void
	setFullName(String _full_name)
	{
		account.setFullName(_full_name);
	}

	public void
	setListID(String _listID)
		throws TorqueException
	{
		if (_listID != null)
			account.setListID(_listID);
	}
	
	public void
	setIsActive(boolean _active)
	{
		account.setIsActive(_active ? (short)1 : (short)0);
	}

	public void
	setName(String _name)
	{
		account.setName(_name);
	}

	public void
	setParentListID(String _parent_list_id)
	{
		this.parent_list_id = _parent_list_id;
	}

	public void
	setSublevel(short _sublevel)
	{
		account.setSublevel(_sublevel);
	}

	public void
	setTaxLineID(String _tax_line_id)
	{
		this.tax_line_id = _tax_line_id;
	}

	public void
	setTaxLineName(String _tax_line_name)
	{
		this.tax_line_name = _tax_line_name;
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		account.setTimeCreated(_timeCreated);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		account.setTimeModified(_timeModified);
	}

	public void
	setTotalBalance(float _total)
	{
		account.setTotalBalance(new BigDecimal(_total));
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		maintainParent();
		maintainTaxInfo();

		account.save();

		this.updateLastUpdateTime();
    }

	private void
	updateLastUpdateTime()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Date timeModified = this.getTimeModified();
		UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(account.getCompanyId());
		Date last_update = AccountRet.last_update_hash.get(company);
		if ((last_update == null) || timeModified.after(last_update))
		{
			Calendar mod_time = Calendar.getInstance();
			mod_time.setTime(timeModified);
			last_update = mod_time.getTime();
			AccountRet.last_update_hash.put(company, last_update);
		}
	}
}
