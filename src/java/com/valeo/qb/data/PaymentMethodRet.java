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
PaymentMethodRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,PaymentMethodRet> hash = new HashMap<Integer,PaymentMethodRet>(11);

    public static PaymentMethodRet
    getPaymentMethod(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		PaymentMethodRet obj = (PaymentMethodRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(PaymentMethodRefDbPeer.PAYMENT_METHOD_REF_DB_ID, _id);
			List objList = PaymentMethodRefDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Payment Method with id: " + _id);

			obj = PaymentMethodRet.getPaymentMethod((PaymentMethodRefDb)objList.get(0));
		}

		return obj;
    }

    private static PaymentMethodRet
    getPaymentMethod(PaymentMethodRefDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getPaymentMethodRefDbId());
		PaymentMethodRet obj = (PaymentMethodRet)hash.get(key);
		if (obj == null)
		{
			obj = new PaymentMethodRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static PaymentMethodRet
    getPaymentMethod(CompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(PaymentMethodRefDbPeer.COMPANY_ID, _company.getId());
		crit.add(PaymentMethodRefDbPeer.LIST_I_D, _list_id);
		List obj_list = PaymentMethodRefDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return PaymentMethodRet.getPaymentMethod((PaymentMethodRefDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Vendor with list id: " + _list_id);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Vendor with list id: " + _list_id);
    }

    public static Vector
    getPaymentMethods(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(PaymentMethodRefDbPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(PaymentMethodRefDbPeer.FULL_NAME);
		Iterator obj_itr = PaymentMethodRefDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(PaymentMethodRet.getPaymentMethod((PaymentMethodRefDb)obj_itr.next()));

		return vec;
    }

	/*
    public static Vector
    getPaymentMethods(CompanyBean _company, Date _last_update, boolean _only_previously_synced)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(PaymentMethodRefDbPeer.COMPANY_ID, _company.getId());
		if (_last_update != null)
			crit.add(PaymentMethodRefDbPeer.TIME_MODIFIED, _last_update, Criteria.GREATER_EQUAL);
		if (_only_previously_synced)
			crit.add(PaymentMethodRefDbPeer.LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(PaymentMethodRefDbPeer.NAME);
		Iterator obj_itr = PaymentMethodRefDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(PaymentMethodRet.getPaymentMethod((PaymentMethodRefDb)obj_itr.next()));

		return vec;
    }
	 * 
	 */

    public static Vector
    getNewVendors(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(PaymentMethodRefDbPeer.COMPANY_ID, _company.getId());
		crit.add(PaymentMethodRefDbPeer.LIST_I_D, (Object)"", Criteria.ISNULL);
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = PaymentMethodRefDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(PaymentMethodRet.getPaymentMethod((PaymentMethodRefDb)obj_itr.next()));

		return vec;
    }

	// INSTANCE VARIABLES

	private PaymentMethodRefDb paymentMethod;

	// CONSTRUCTORS

	public
	PaymentMethodRet()
	{
		paymentMethod = new PaymentMethodRefDb();
		isNew = true;
	}

	public
	PaymentMethodRet(PaymentMethodRefDb _paymentMethod)
	{
		paymentMethod = _paymentMethod;
		isNew = false;
	}

	// INSTANCE METHODS

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(paymentMethod.getCompanyId());
	}

	/*
	public String
	getEditSequence()
	{
		return paymentMethod.getEditSequence();
	}
	 * 
	 */

	public int
	getId()
	{
		return paymentMethod.getPaymentMethodRefDbId();
	}

	public String
	getLabel()
	{
		return this.getName();
	}

	public String
	getListID()
	{
		return paymentMethod.getListID();
	}

	public String
	getName()
	{
		String str = paymentMethod.getFullName();
		if (str == null)
			return "";
		return str;
	}

	public String
	getValue()
	{
		return paymentMethod.getPaymentMethodRefDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		// see if this payment method conflicts with any others...

		Criteria crit = new Criteria();
		crit.add(PaymentMethodRefDbPeer.COMPANY_ID, paymentMethod.getCompanyId());
		if ((paymentMethod.getListID() != null) && (paymentMethod.getListID().length() > 0))
			crit.add(PaymentMethodRefDbPeer.LIST_I_D, paymentMethod.getListID());
		else
			crit.add(PaymentMethodRefDbPeer.FULL_NAME, paymentMethod.getFullName());
		List obj_list = PaymentMethodRefDbPeer.doSelect(crit);
		if (obj_list.size() > 0)
			throw new ObjectAlreadyExistsException("Matching Payment Method already exists.");

		// ensure that company id is set...

		if (paymentMethod.getCompanyId() == 0)
			throw new IllegalValueException("Company not set for " + this.getLabel());

		paymentMethod.save();
    }

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		paymentMethod.setCompanyId(_company.getId());
	}

	/*
	public void
	setEditSequence(String _edit_sequence)
	{
		paymentMethod.setEditSequence(_edit_sequence);
	}
	 * 
	 */

	public void
	setListID(String _listID)
		throws TorqueException
	{
		if (_listID != null)
			paymentMethod.setListID(_listID);
	}

	public void
	setName(String _name)
	{
		paymentMethod.setFullName(_name);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		paymentMethod.save();
    }
}