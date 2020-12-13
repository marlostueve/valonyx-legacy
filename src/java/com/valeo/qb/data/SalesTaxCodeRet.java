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
SalesTaxCodeRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,SalesTaxCodeRet> hash = new HashMap<Integer,SalesTaxCodeRet>(11);
	private static HashMap<UKOnlineCompanyBean,Date> last_update_hash = new HashMap<UKOnlineCompanyBean,Date>(11);

	// CLASS METHODS

    public static SalesTaxCodeRet
    getSalesTaxCode(int _id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Integer key = new Integer(_id);
		SalesTaxCodeRet obj = (SalesTaxCodeRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(SalesTaxCodeRetDbPeer.SALES_TAX_CODE_RET_DB_ID, _id);
			List objList = SalesTaxCodeRetDbPeer.doSelect(crit);
			if (objList.size() == 1)
				return SalesTaxCodeRet.getSalesTaxCode((SalesTaxCodeRetDb)objList.get(0));
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Sales Tax Code with id: " + _id);
			throw new UniqueObjectNotFoundException("Could not locate unique Sales Tax Code with id: " + _id);
		}

		return obj;
    }

    private static SalesTaxCodeRet
    getSalesTaxCode(SalesTaxCodeRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getSalesTaxCodeRetDbId());
		SalesTaxCodeRet obj = (SalesTaxCodeRet)hash.get(key);
		if (obj == null)
		{
			obj = new SalesTaxCodeRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static SalesTaxCodeRet
    getSalesTaxCode(CompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(SalesTaxCodeRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(SalesTaxCodeRetDbPeer.LIST_I_D, _list_id);
		List obj_list = SalesTaxCodeRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return SalesTaxCodeRet.getSalesTaxCode((SalesTaxCodeRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Sales Tax Code with list id: " + _list_id);
		throw new UniqueObjectNotFoundException("Could not locate unique Sales Tax Code with list id: " + _list_id);
    }

    public static Vector
    getSalesTaxCodes(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(SalesTaxCodeRetDbPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(SalesTaxCodeRetDbPeer.NAME);
		Iterator obj_itr = SalesTaxCodeRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(SalesTaxCodeRet.getSalesTaxCode((SalesTaxCodeRetDb)obj_itr.next()));

		return vec;
    }

	// INSTANCE VARIABLES

	private SalesTaxCodeRetDb code;
	private String name;

	// CONSTRUCTORS

	public
	SalesTaxCodeRet()
	{
		code = new SalesTaxCodeRetDb();
		isNew = true;
	}

	public
	SalesTaxCodeRet(SalesTaxCodeRetDb _code)
	{
		code = _code;
		isNew = false;
	}

	// INSTANCE METHODS

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(code.getCompanyId());
	}

	public String
	getEditSequence()
	{
		return code.getEditSequence();
	}

	public int
	getId()
	{
		return code.getSalesTaxCodeRetDbId();
	}

	public String
	getDesc()
	{
		return code.getSalesTaxCodeDesc();
	}

	public String
	getLabel()
	{
		return this.getName();
	}

	public String
	getListID()
	{
		return code.getListID();
	}

	public String
	getName()
	{
		String str = code.getName();
		if (str == null)
			return "";
		return str;
	}

	public Date
	getTimeCreated()
	{
		return code.getTimeCreated();
	}

	public Date
	getTimeModified()
	{
		return code.getTimeModified();
	}

	public String
	getValue()
	{
		return code.getSalesTaxCodeRetDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		// see if this vendor conflicts with any others...

		Criteria crit = new Criteria();
		crit.add(SalesTaxCodeRetDbPeer.COMPANY_ID, code.getCompanyId());
		if ((code.getListID() != null) && (code.getListID().length() > 0))
			crit.add(SalesTaxCodeRetDbPeer.LIST_I_D, code.getListID());
		else
			crit.add(SalesTaxCodeRetDbPeer.NAME, code.getName());
		List obj_list = SalesTaxCodeRetDbPeer.doSelect(crit);
		if (obj_list.size() > 0)
			throw new ObjectAlreadyExistsException("Matching Sales Tax Code already exists.");

		// ensure that company id is set...

		if (code.getCompanyId() == 0)
			throw new IllegalValueException("Company not set for " + this.getLabel());

		if (code.getTimeCreated() == null)
			code.setTimeCreated(new Date());

		code.save();
    }

	public boolean
	isActive()
	{
		return (code.getIsActive().equals("true"));
	}

	public boolean
	isTaxable()
	{
		return (code.getIsTaxable().equals("true"));
	}

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		code.setCompanyId(_company.getId());
	}

	public void
	setEditSequence(String _edit_sequence)
	{
		code.setEditSequence(_edit_sequence);
	}

	public void
	setListID(String _listID)
		throws TorqueException
	{
		if (_listID != null)
			code.setListID(_listID);
	}

	public void
	setIsActive(boolean _active)
	{
		code.setIsActive(_active ? "true" : "false");
	}

	public void
	setIsTaxable(boolean _active)
	{
		code.setIsTaxable(_active ? "true" : "false");
	}

	public void
	setDesc(String _desc)
	{
		code.setSalesTaxCodeDesc(_desc);
	}

	public void
	setName(String _name)
	{
		code.setName(_name);
	}

	public void
	setNameFromParse(String _name)
	{
		if (this.name == null)
			this.name = _name;
		else
			name = name + _name;
		code.setName(name);
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		code.setTimeCreated(_timeCreated);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		code.setTimeModified(_timeModified);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		if (code.getTimeModified() == null)
			code.setTimeModified(new Date());

		code.save();
    }
	
	public ValeoTaxCodeBean
	getDefaultTaxCode()
		throws TorqueException, ObjectNotFoundException
	{
		return (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode(code.getTaxCodeId());
	}
	
	public int
	getDefaultTaxCodeId()
		throws TorqueException, ObjectNotFoundException
	{
		return code.getTaxCodeId();
	}
	
	public void
	setDefaultTaxCode(ValeoTaxCodeBean _default)
		throws TorqueException, ObjectNotFoundException
	{
		code.setTaxCodeId(_default.getId());
	}
}