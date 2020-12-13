/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb.data;


import java.util.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.CCUtils;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
ItemPaymentRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,ItemPaymentRet> hash = new HashMap<Integer,ItemPaymentRet>(11);
	private static HashMap<UKOnlineCompanyBean,Date> last_update_hash = new HashMap<UKOnlineCompanyBean,Date>(11);
	
	public static final int CASH				=  7;
	public static final int CHECK				=  8;
	public static final int GIFT_CARD			=  9;
	public static final int GIFT_CERT			= 10;
	
	public static final int VISA				=  1;
	public static final int MASTERCARD			=  2;
	public static final int DISCOVER			=  3;
	public static final int AMERICAN_EXPRESS	=  4;
	public static final int EN_ROUTE			=  5;
	public static final int DINERS_CLUB			=  6;

	// CLASS METHODS
	
	public static String
	getPaymentMethodString(int _method)
	{
		switch (_method) {
			case  1: return "Visa";
			case  2: return "MasterCard";
			case  3: return "Discover";
			case  4: return "American Express";
			case  5: return "enRoute";
			case  6: return "Diner's Club/Carte Blanche";
			case  7: return "Cash";
			case  8: return "Check";
			case  9: return "Gift Card";
			case 10: return "Gift Certificate";
		}
		
		return "[UNKNOWN]";
	}

	public static Date
	getLastUpdateDate(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		Date last_update = ItemPaymentRet.last_update_hash.get(_company);
		if (last_update == null)
		{
			Criteria crit = new Criteria();
			crit.add(ItemPaymentRetDbPeer.COMPANY_ID, _company.getId());
			crit.addDescendingOrderByColumn(ItemPaymentRetDbPeer.TIME_MODIFIED);
			List objList = ItemPaymentRetDbPeer.doSelect(crit);
			if (objList.size() > 0)
			{
				ItemPaymentRetDb item_payment_obj = (ItemPaymentRetDb)objList.get(0);
				last_update = item_payment_obj.getTimeModified();

				ItemPaymentRet.last_update_hash.put(_company, last_update);
			}
		}

		return last_update;
	}

    public static ItemPaymentRet
    getPaymentItem(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ItemPaymentRet obj = (ItemPaymentRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ItemPaymentRetDbPeer.ITEM_PAYMENT_RET_DB_ID, _id);
			List objList = ItemPaymentRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Payment Item with id: " + _id);

			obj = ItemPaymentRet.getPaymentItem((ItemPaymentRetDb)objList.get(0));
		}

		return obj;
    }

    private static ItemPaymentRet
    getPaymentItem(ItemPaymentRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getItemPaymentRetDbId());
		ItemPaymentRet obj = (ItemPaymentRet)hash.get(key);
		if (obj == null)
		{
			obj = new ItemPaymentRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static ItemPaymentRet
    getPaymentItem(CompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ItemPaymentRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ItemPaymentRetDbPeer.LIST_I_D, _list_id);
		List obj_list = ItemPaymentRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ItemPaymentRet.getPaymentItem((ItemPaymentRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Payment Item with list id: " + _list_id);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Payment Item with list id: " + _list_id);
    }

    public static ItemPaymentRet
    getPaymentItem(PaymentMethodRet _payment_method)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ItemPaymentRetDbPeer.PAYMENT_METHOD_REF_DB_ID, _payment_method.getId());
		List obj_list = ItemPaymentRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ItemPaymentRet.getPaymentItem((ItemPaymentRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Payment Item for payment method: " + _payment_method.getLabel());
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Payment Item for payment method: " + _payment_method.getLabel());
    }

    public static ItemPaymentRet
    getPaymentItem(CompanyBean _company, short _cc_type)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ItemPaymentRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ItemPaymentRetDbPeer.CC_TYPE, _cc_type);
		List obj_list = ItemPaymentRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ItemPaymentRet.getPaymentItem((ItemPaymentRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Payment Item for " + CCUtils.getCardName(_cc_type));
		else
			throw new UniqueObjectNotFoundException("Could not locate Payment Item for " + CCUtils.getCardName(_cc_type));
    }

    public static Vector
    getPaymentItems(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ItemPaymentRetDbPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(ItemPaymentRetDbPeer.NAME);
		Iterator obj_itr = ItemPaymentRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemPaymentRet.getPaymentItem((ItemPaymentRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getPaymentItems(CompanyBean _company, Date _last_update, boolean _only_previously_synced)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ItemPaymentRetDbPeer.COMPANY_ID, _company.getId());
		if (_last_update != null)
			crit.add(ItemPaymentRetDbPeer.TIME_MODIFIED, _last_update, Criteria.GREATER_EQUAL);
		if (_only_previously_synced)
			crit.add(ItemPaymentRetDbPeer.LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(ItemPaymentRetDbPeer.NAME);
		Iterator obj_itr = ItemPaymentRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemPaymentRet.getPaymentItem((ItemPaymentRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getNewSalesTaxItems(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ItemPaymentRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ItemPaymentRetDbPeer.LIST_I_D, (Object)"", Criteria.ISNULL);
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = ItemPaymentRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ItemPaymentRet.getPaymentItem((ItemPaymentRetDb)obj_itr.next()));

		return vec;
    }
	
	public static ItemPaymentRet
	getPaymentItemForRequestID(String _requestID)
		throws TorqueException, ObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(ItemPaymentRetDbPeer.REQUEST_I_D, _requestID);
		List objList = ItemPaymentRetDbPeer.doSelect(crit);
		if (objList.size() != 1)
			throw new ObjectNotFoundException("Could not locate ItemPaymentRet for request: " + _requestID);

		return ItemPaymentRet.getPaymentItem((ItemPaymentRetDb)objList.get(0));
	}

	// INSTANCE VARIABLES

	private ItemPaymentRetDb payment_item;
	
	private String depositToAccountRefListID;

	private String paymentMethodListID;
	private String paymentMethodFullName;

	private String name;

	// CONSTRUCTORS

	public
	ItemPaymentRet()
	{
		payment_item = new ItemPaymentRetDb();
		isNew = true;
	}

	public
	ItemPaymentRet(ItemPaymentRetDb _payment_item)
	{
		payment_item = _payment_item;
		isNew = false;
	}

	// INSTANCE METHODS

	public short
	getCCType()
	{
		return payment_item.getCcType();
	}

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(payment_item.getCompanyId());
	}

	public int
	getCompanyId()
	{
		return payment_item.getCompanyId();
	}

	public String
	getEditSequence()
	{
		return payment_item.getEditSequence();
	}

	public int
	getId()
	{
		return payment_item.getItemPaymentRetDbId();
	}

	public String
	getLabel()
	{
		return this.getName();
	}

	public String
	getListID()
	{
		return payment_item.getListID();
	}

	public String
	getName()
	{
		String str = payment_item.getName();
		if (str == null)
			return "";
		return str;
	}

	public Date
	getTimeCreated()
	{
		return payment_item.getTimeCreated();
	}

	public Date
	getTimeModified()
	{
		return payment_item.getTimeModified();
	}

	public String
	getValue()
	{
		return payment_item.getItemPaymentRetDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		// see if this payment_item conflicts with any others...

		Criteria crit = new Criteria();
		crit.add(ItemPaymentRetDbPeer.COMPANY_ID, payment_item.getCompanyId());
		if ((payment_item.getListID() != null) && (payment_item.getListID().length() > 0))
			crit.add(ItemPaymentRetDbPeer.LIST_I_D, payment_item.getListID());
		else
			crit.add(ItemPaymentRetDbPeer.NAME, payment_item.getName());
		List obj_list = ItemPaymentRetDbPeer.doSelect(crit);
		if (obj_list.size() > 0)
			throw new ObjectAlreadyExistsException("Matching Payment Item already exists.");

		// ensure that company id is set...

		if (payment_item.getCompanyId() == 0)
			throw new IllegalValueException("Company not set for " + this.getLabel());

		maintainPaymentMethod();

		if (payment_item.getTimeCreated() == null)
			payment_item.setTimeCreated(new Date());

		payment_item.save();
		this.updateLastUpdateTime();
    }

	public boolean
	isActive()
	{
		return (payment_item.getIsActive().equals("true"));
	}

	public boolean
	isSynchronized()
	{
		if (this.isNew())
			return false;
		
		if (this.getEditSequence() != null && !this.getEditSequence().equals(""))
		{
			if (this.getListID() != null && !this.getListID().equals(""))
				return true;
		}
		
		return false;
	}

	private void
	maintainPaymentMethod()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
	{
		/*
		<table name="PAYMENT_METHOD_REF_DB" idMethod="native">
			<column name="PAYMENT_METHOD_REF_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
			<column name="COMPANY_ID" required="true" type="INTEGER"/>
			<column name="LIST_I_D" required="true" type="VARCHAR" size="25"/>
			<column name="FULL_NAME" required="true" type="VARCHAR" size="50"/>

			<foreign-key foreignTable="COMPANY">
				<reference local="COMPANY_ID" foreign="COMPANYID"/>
			</foreign-key>
		</table>
		 */

		if (this.paymentMethodListID != null)
		{
			Criteria crit = new Criteria();
			crit.add(PaymentMethodRefDbPeer.COMPANY_ID, payment_item.getCompanyId());
			crit.add(PaymentMethodRefDbPeer.LIST_I_D, this.paymentMethodListID);
			List obj_list = PaymentMethodRefDbPeer.doSelect(crit);
			if (obj_list.size() == 1)
			{
				PaymentMethodRefDb payment_method_obj = (PaymentMethodRefDb)obj_list.get(0);
				payment_item.setPaymentMethodRefDbId(payment_method_obj.getPaymentMethodRefDbId());
			}
			else if (obj_list.size() == 0)
			{
				System.out.println("Payment Method not found for ListID >" + this.paymentMethodListID);

				PaymentMethodRefDb payment_method_obj = new PaymentMethodRefDb();
				payment_method_obj.setCompanyId(payment_item.getCompanyId());
				payment_method_obj.setListID(this.paymentMethodListID);
				payment_method_obj.setFullName(this.paymentMethodFullName);
				payment_method_obj.save();

				payment_item.setPaymentMethodRefDbId(payment_method_obj.getPaymentMethodRefDbId());
			}
			else
				throw new UniqueObjectNotFoundException("Unique Payment Method not found >" + this.paymentMethodListID);
		}
		else if (this.getPaymentMethodId() < 1)
		{
			// create a new payment method with the same name as the new payment type.  this'll need to be synced with QB

			PaymentMethodRefDb payment_method_obj = new PaymentMethodRefDb();
			payment_method_obj.setCompanyId(payment_item.getCompanyId());
			payment_method_obj.setFullName(this.getName());
			payment_method_obj.save();

			payment_item.setPaymentMethodRefDbId(payment_method_obj.getPaymentMethodRefDbId());
		}
	}

	public void
	setCCType(short _type)
	{
		payment_item.setCcType(_type);
	}

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		payment_item.setCompanyId(_company.getId());
	}

	public void
	setEditSequence(String _edit_sequence)
	{
		payment_item.setEditSequence(_edit_sequence);
	}

	public void
	setListID(String _listID)
		throws TorqueException
	{
		if (_listID != null)
			payment_item.setListID(_listID);
	}

	public void
	setIsActive(boolean _active)
	{
		payment_item.setIsActive(_active ? "true" : "false");
	}

	public void
	setName(String _name)
	{
		payment_item.setName(_name);
	}

	public void
	setNameFromParse(String _name)
	{
		if (this.name == null)
			this.name = _name;
		else
			name = name + _name;
		payment_item.setName(name);
	}
	
	public int
	getPaymentMethodId()
	{
		return payment_item.getPaymentMethodRefDbId();
	}
	
	public void
	setPaymentMethodId(int _id)
		throws TorqueException
	{
		payment_item.setPaymentMethodRefDbId(_id);
	}

	public String
	getPaymentMethodListID()
	{
		return this.paymentMethodListID;
	}

	public void
	setPaymentMethodListID(String _str)
	{
		this.paymentMethodListID = _str;
	}

	public String
	getDepositToAccountRefListID()
	{
		return this.depositToAccountRefListID;
	}

	public void
	setDepositToAccountRefListID(String _str)
	{
		this.depositToAccountRefListID = _str;
	}

	public String
	getPaymentMethodFullName()
	{
		return this.paymentMethodFullName;
	}

	public void
	setPaymentMethodFullName(String _str)
	{
		this.paymentMethodFullName = _str;
	}
	
	public void
	setRequestID(String _str)
	{
		payment_item.setRequestID(_str);
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		payment_item.setTimeCreated(_timeCreated);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		payment_item.setTimeModified(_timeModified);
	}

	public void
	setDepositToAccount(AccountRet _account)
		throws TorqueException
	{
		payment_item.setDepositToAccountId(_account.getId());
	}

	public int
	getDepositToAccountId()
	{
		return payment_item.getDepositToAccountId();
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		maintainPaymentMethod();

		if (payment_item.getTimeModified() == null)
			payment_item.setTimeModified(new Date());

		payment_item.save();
		this.updateLastUpdateTime();
    }

	private void
	updateLastUpdateTime()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Date timeModified = this.getTimeModified();
		if (timeModified != null)
		{
			UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(payment_item.getCompanyId());
			Date last_update = ItemPaymentRet.last_update_hash.get(company);
			if ((last_update == null) || timeModified.after(last_update))
			{
				Calendar mod_time = Calendar.getInstance();
				mod_time.setTime(timeModified);
				last_update = mod_time.getTime();
				ItemPaymentRet.last_update_hash.put(company, last_update);
			}
		}
	}
}