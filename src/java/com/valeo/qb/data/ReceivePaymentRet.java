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
ReceivePaymentRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,ReceivePaymentRet> hash = new HashMap<Integer,ReceivePaymentRet>(11);
	private static HashMap<UKOnlineCompanyBean,Date> last_update_hash = new HashMap<UKOnlineCompanyBean,Date>(11);

	// CLASS METHODS

	public static Date
	getLastUpdateDate(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		Date last_update = ReceivePaymentRet.last_update_hash.get(_company);
		if (last_update == null)
		{
			Criteria crit = new Criteria();
			crit.add(ReceivePaymentRetDbPeer.COMPANY_ID, _company.getId());
			crit.addDescendingOrderByColumn(ReceivePaymentRetDbPeer.TIME_MODIFIED);
			List objList = ReceivePaymentRetDbPeer.doSelect(crit);
			if (objList.size() > 0)
			{
				ReceivePaymentRetDb receive_payment_obj = (ReceivePaymentRetDb)objList.get(0);
				last_update = receive_payment_obj.getTimeModified();

				ReceivePaymentRet.last_update_hash.put(_company, last_update);
			}
		}

		return last_update;
	}

    public static ReceivePaymentRet
    getReceivePayment(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ReceivePaymentRet obj = (ReceivePaymentRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ReceivePaymentRetDbPeer.RECEIVE_PAYMENT_RET_DB_ID, _id);
			List objList = ReceivePaymentRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Receive Payment with id: " + _id);

			obj = ReceivePaymentRet.getReceivePayment((ReceivePaymentRetDb)objList.get(0));
		}

		return obj;
    }

    private static ReceivePaymentRet
    getReceivePayment(ReceivePaymentRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getReceivePaymentRetDbId());
		ReceivePaymentRet obj = (ReceivePaymentRet)hash.get(key);
		if (obj == null)
		{
			obj = new ReceivePaymentRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static ReceivePaymentRet
    getReceivePayment(CompanyBean _company, String _txn_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ReceivePaymentRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ReceivePaymentRetDbPeer.TXN_I_D, _txn_id);
		List obj_list = ReceivePaymentRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ReceivePaymentRet.getReceivePayment((ReceivePaymentRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Receive Payment with transaction id: " + _txn_id);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Receive Payment with transaction id: " + _txn_id);
    }

    public static ReceivePaymentRet
    getReceivePayment(CompanyBean _company, int _receive_payment_number)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ReceivePaymentRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ReceivePaymentRetDbPeer.TXN_NUMBER, _receive_payment_number);
		List obj_list = ReceivePaymentRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ReceivePaymentRet.getReceivePayment((ReceivePaymentRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Sales Receipt with sales receipt number: " + _receive_payment_number);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Sales Receipt with sales receipt number: " + _receive_payment_number);
    }

    public static ReceivePaymentRet
    getReceivePayment(CompanyBean _company, String _customer_list_id, Date _time_modified)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ReceivePaymentRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ReceivePaymentRetDbPeer.CUSTOMER_LIST_I_D, _customer_list_id);
		crit.add(ReceivePaymentRetDbPeer.TIME_MODIFIED, _time_modified);

		List obj_list = ReceivePaymentRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ReceivePaymentRet.getReceivePayment((ReceivePaymentRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Receive Payment with customer list id: " + _customer_list_id + " at " + CUBean.getUserDateString(_time_modified));
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Receive Payment with customer list id: " + _customer_list_id + " at " + CUBean.getUserDateString(_time_modified));
    }

    public static ReceivePaymentRet
    getReceivePayment(CompanyBean _company, OrderBean _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ReceivePaymentRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ReceivePaymentRetDbPeer.TXN_I_D, _order.getAuthorizationCode());
		List obj_list = ReceivePaymentRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ReceivePaymentRet.getReceivePayment((ReceivePaymentRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Receive Payment with sales txn id: " + _order.getAuthorizationCode());
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Receive Payment with txn id: " + _order.getAuthorizationCode());
    }

    public static Vector
    getReceivePayments(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ReceivePaymentRetDbPeer.COMPANY_ID, _company.getId());
		Iterator obj_itr = ReceivePaymentRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ReceivePaymentRet.getReceivePayment((ReceivePaymentRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getReceivePayments(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ReceivePaymentRetDbPeer.COMPANY_ID, _person.getDepartment().getCompany().getId());
		crit.add(ReceivePaymentRetDbPeer.CUSTOMER_LIST_I_D, _person.getEmail2String());
		crit.addDescendingOrderByColumn(ReceivePaymentRetDbPeer.TXN_DATE);
		Iterator obj_itr = ReceivePaymentRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ReceivePaymentRet.getReceivePayment((ReceivePaymentRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getReceivePayments(UKOnlinePersonBean _person, Date _start_date, Date _end_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Calendar start_date = Calendar.getInstance();
		start_date.setTime(_start_date);
		start_date.set(Calendar.HOUR_OF_DAY, 0);
		start_date.set(Calendar.MINUTE, 0);
		start_date.set(Calendar.SECOND, 0);

		Calendar end_date = Calendar.getInstance();
		end_date.setTime(_end_date);
		end_date.set(Calendar.HOUR_OF_DAY, 23);
		end_date.set(Calendar.MINUTE, 59);
		end_date.set(Calendar.SECOND, 59);
		
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(ReceivePaymentRetDbPeer.COMPANY_ID, _person.getDepartment().getCompany().getId());
		crit.add(ReceivePaymentRetDbPeer.CUSTOMER_LIST_I_D, _person.getEmail2String());
		crit.add(ReceivePaymentRetDbPeer.TXN_DATE, start_date.getTime(), Criteria.GREATER_EQUAL);
		crit.and(ReceivePaymentRetDbPeer.TXN_DATE, end_date.getTime(), Criteria.LESS_EQUAL);
		crit.addDescendingOrderByColumn(ReceivePaymentRetDbPeer.TXN_DATE);
		Iterator obj_itr = ReceivePaymentRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ReceivePaymentRet.getReceivePayment((ReceivePaymentRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getReceivePayments(CompanyBean _company, String _customer_list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ReceivePaymentRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(ReceivePaymentRetDbPeer.CUSTOMER_LIST_I_D, _customer_list_id);
		Iterator obj_list_itr = ReceivePaymentRetDbPeer.doSelect(crit).iterator();
		while (obj_list_itr.hasNext())
		{
			ReceivePaymentRetDb obj = (ReceivePaymentRetDb)obj_list_itr.next();
			vec.addElement(ReceivePaymentRet.getReceivePayment(obj));
		}

		return vec;
    }

	// INSTANCE VARIABLES

	private ReceivePaymentRetDb receive_payment;
	private CustomerRef customer;
	private PaymentMethodRef paymentMethod;

	// CONSTRUCTORS

	public
	ReceivePaymentRet()
	{
		receive_payment = new ReceivePaymentRetDb();
		isNew = true;
	}

	public
	ReceivePaymentRet(ReceivePaymentRetDb _receive_payment)
	{
		receive_payment = _receive_payment;
		isNew = false;
	}

	// INSTANCE METHODS

	public String
	getCustomerListID()
	{
		return receive_payment.getCustomerListID();
	}

	public int
	getId()
	{
		return receive_payment.getReceivePaymentRetDbId();
	}

	public String
	getLabel()
	{
		return receive_payment.getTotalAmount().toString();
	}

	public String
	getMemo()
	{
		String str = receive_payment.getMemo();
		if (str == null)
			return "";
		return str;
	}

	public String
	getPaymentMethodListID()
	{
		return receive_payment.getPaymentMethodListID();
	}

	public String
	getRequestId()
	{
		return receive_payment.getRequestID();
	}

	public Date
	getTimeCreated()
	{
		return receive_payment.getTimeCreated();
	}

	public Date
	getTimeModified()
	{
		return receive_payment.getTimeModified();
	}

	public float
	getTotalAmount()
	{
		return receive_payment.getTotalAmount().floatValue();
	}

	public Date
	getTxnDate()
	{
		return receive_payment.getTxnDate();
	}

	public String
	getTxnID()
	{
		return receive_payment.getTxnID();
	}

	public int
	getTxnNumber()
	{
		return receive_payment.getTxnNumber();
	}

	public String
	getValue()
	{
		return receive_payment.getReceivePaymentRetDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		// see if this receive payment conflicts with any others...

		Criteria crit = new Criteria();
		crit.add(ReceivePaymentRetDbPeer.COMPANY_ID, receive_payment.getCompanyId());
		crit.add(ReceivePaymentRetDbPeer.TXN_NUMBER, receive_payment.getTxnNumber());
		List obj_list = ReceivePaymentRetDbPeer.doSelect(crit);
		if (obj_list.size() > 0)
			throw new ObjectAlreadyExistsException("Matching receive payment already exists.");

		receive_payment.save();
		updateLastUpdateTime();
    }

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		receive_payment.setCompanyId(_company.getId());
	}

	public void
	setCustomer(CustomerRef _customer)
		throws TorqueException
	{
		customer = _customer;
		this.setCustomerListID(_customer.getListID());
	}

	public void
	setCustomerListID(String _customerListID)
		throws TorqueException
	{
		if (_customerListID != null)
			receive_payment.setCustomerListID(_customerListID);
	}

	public void
	setMemo(String _memo)
	{
		receive_payment.setMemo(_memo);
	}

	public void
	setPaymentMethod(PaymentMethodRef _payment_method)
		throws TorqueException
	{
		paymentMethod = _payment_method;
		this.setPaymentMethodListID(_payment_method.getListID());
	}

	public void
	setPaymentMethodListID(String _paymentMethodListID)
		throws TorqueException
	{
		if (_paymentMethodListID != null)
			receive_payment.setPaymentMethodListID(_paymentMethodListID);
	}

	public void
	setRequestId(String _request_id)
	{
		receive_payment.setRequestID(_request_id);
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		receive_payment.setTimeCreated(_timeCreated);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		receive_payment.setTimeModified(_timeModified);
	}

	public void
	setTotalAmount(float _total)
	{
		receive_payment.setTotalAmount(new BigDecimal(_total));
	}

	public void
	setTxnDate(Date _txnDate)
	{
		receive_payment.setTxnDate(_txnDate);
	}

	public void
	setTxnID(String _txn_id)
	{
		receive_payment.setTxnID(_txn_id);
	}

	public void
	setTxnNumber(int _txn_number)
	{
		receive_payment.setTxnNumber(_txn_number);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		receive_payment.save();

		updateLastUpdateTime();
    }

	private void
	updateLastUpdateTime()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Date timeModified = this.getTimeModified();
		UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(receive_payment.getCompanyId());
		Date last_update = ReceivePaymentRet.last_update_hash.get(company);
		if ((last_update == null) || timeModified.after(last_update))
		{
			Calendar mod_time = Calendar.getInstance();
			mod_time.setTime(timeModified);
			last_update = mod_time.getTime();
			ReceivePaymentRet.last_update_hash.put(company, last_update);
		}
	}
}
