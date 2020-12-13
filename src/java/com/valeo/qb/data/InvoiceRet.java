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
import com.valeo.qbpos.data.TenderRet;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
InvoiceRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

	private static Date last_update;

    protected static HashMap<Integer,InvoiceRet> hash = new HashMap<Integer,InvoiceRet>(11);

	// CLASS METHODS

	public static Date
	getLastUpdateDate(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		if (last_update == null)
		{
			Criteria crit = new Criteria();
			crit.add(InvoiceRetDbPeer.COMPANY_ID, _company.getId());
			crit.addDescendingOrderByColumn(InvoiceRetDbPeer.TIME_MODIFIED);
			List objList = InvoiceRetDbPeer.doSelect(crit);
			if (objList.size() > 0)
			{
				InvoiceRetDb invoice_obj = (InvoiceRetDb)objList.get(0);
				last_update = invoice_obj.getTimeModified();
			}
		}

		return last_update;
	}

	public static OrderBean
	getOrder(CompanyBean _company, String _txn_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(PersonorderPeer.COMPANY_ID, _company.getId());
		crit.add(PersonorderPeer.AUTHORIZATIONCODE, _txn_id);
		List obj_list = PersonorderPeer.doSelect(crit);
		if (obj_list.size() == 1)
		{
			Personorder order_obj = (Personorder)obj_list.get(0);
			return ValeoOrderBean.getOrder(order_obj.getOrderid());
		}
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate order with transaction id: " + _txn_id);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique order with transaction id: " + _txn_id);
	}

    public static InvoiceRet
    getInvoice(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		InvoiceRet obj = (InvoiceRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(InvoiceRetDbPeer.INVOICE_RET_DB_ID, _id);
			List objList = InvoiceRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Invoice with id: " + _id);

			obj = InvoiceRet.getInvoice((InvoiceRetDb)objList.get(0));
		}

		return obj;
    }

    private static InvoiceRet
    getInvoice(InvoiceRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getInvoiceRetDbId());
		InvoiceRet obj = (InvoiceRet)hash.get(key);
		if (obj == null)
		{
			obj = new InvoiceRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static InvoiceRet
    getInvoice(CompanyBean _company, String _txn_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(InvoiceRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(InvoiceRetDbPeer.TXN_I_D, _txn_id);
		List obj_list = InvoiceRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return InvoiceRet.getInvoice((InvoiceRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Invoice with transaction id: " + _txn_id);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Invoice with transaction id: " + _txn_id);
    }

	/*
    public static InvoiceRet
    getInvoice(CompanyBean _company, int _invoice_number)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(InvoiceRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(InvoiceRetDbPeer.invoice_NUMBER, _invoice_number);
		List obj_list = InvoiceRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return InvoiceRet.getInvoice((InvoiceRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Invoice with Invoice number: " + _invoice_number);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Invoice with Invoice number: " + _invoice_number);
    }
	 */

    public static InvoiceRet
    getInvoice(CompanyBean _company, OrderBean _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(InvoiceRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(InvoiceRetDbPeer.TXN_I_D, _order.getAuthorizationCode());
		List obj_list = InvoiceRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return InvoiceRet.getInvoice((InvoiceRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Invoice for order: " + _order.getAuthorizationCode());
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Invoice for order: " + _order.getAuthorizationCode());
    }

    public static InvoiceRet
    getInvoice(CompanyBean _company, String _customer_list_id, Date _time_modified)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(InvoiceRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(InvoiceRetDbPeer.CUSTOMER_LIST_I_D, _customer_list_id);
		crit.add(InvoiceRetDbPeer.TIME_MODIFIED, _time_modified);

		List obj_list = InvoiceRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return InvoiceRet.getInvoice((InvoiceRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Invoice with customer list id: " + _customer_list_id + " at " + CUBean.getUserDateString(_time_modified));
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Invoice with customer list id: " + _customer_list_id + " at " + CUBean.getUserDateString(_time_modified));
    }

    public static InvoiceRet
    getInvoiceForTxnNumber(CompanyBean _company, int _txn_number)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(InvoiceRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(InvoiceRetDbPeer.TXN_NUMBER, _txn_number);
		List obj_list = InvoiceRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return InvoiceRet.getInvoice((InvoiceRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Invoice with txn number: " + _txn_number);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Invoice with txn number: " + _txn_number);
    }

    public static Vector
    getInvoices(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(InvoiceRetDbPeer.COMPANY_ID, _company.getId());
		Iterator obj_itr = InvoiceRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(InvoiceRet.getInvoice((InvoiceRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getInvoices(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(InvoiceRetDbPeer.CUSTOMER_LIST_I_D, _person.getQBFSListID());
		Iterator obj_itr = InvoiceRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(InvoiceRet.getInvoice((InvoiceRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getOldOpenInvoices(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
    {
		Calendar launch_date = Calendar.getInstance();
		launch_date.set(2011, Calendar.FEBRUARY, 1);
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(InvoiceRetDbPeer.CUSTOMER_LIST_I_D, _person.getQBFSListID());
		crit.add(InvoiceRetDbPeer.BALANCE_REMAINING, 0, Criteria.GREATER_THAN);
		crit.add(InvoiceRetDbPeer.TXN_DATE, launch_date.getTime(), Criteria.LESS_THAN);
		crit.addDescendingOrderByColumn(InvoiceRetDbPeer.TXN_DATE);
		Iterator obj_itr = InvoiceRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(InvoiceRet.getInvoice((InvoiceRetDb)obj_itr.next()));

		return vec;
    }

	// INSTANCE VARIABLES

	private InvoiceRetDb invoice;
	
	private PaymentMethodRet payment_method;

	private Vector<InvoiceLineRet> invoiceLineRetObjects;
	private Vector<TenderRet> invoiceTenders;

	// CONSTRUCTORS

	public
	InvoiceRet()
	{
		invoice = new InvoiceRetDb();
		isNew = true;
	}

	public
	InvoiceRet(InvoiceRetDb _invoice)
	{
		invoice = _invoice;
		isNew = false;
	}

	// INSTANCE METHODS

	public void
	add(InvoiceLineRet _object)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		if (invoiceLineRetObjects == null)
		{
			if (this.isNew())
				invoiceLineRetObjects = new Vector();
			else
				this.getInvoiceLineRetObjects();
		}

		System.out.println("invoiceLineRetObjects >" + invoiceLineRetObjects.size());

		invoiceLineRetObjects.addElement(_object);
	}

	public void
	add(TenderRet _object)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (invoiceTenders == null)
		{
			if (this.isNew())
				invoiceTenders = new Vector();
			else
				this.getTenderRetObjects();
		}

		invoiceTenders.addElement(_object);
	}

	public UKOnlinePersonBean
	getAssociate()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(invoice.getPractitionerId());
	}

	public UKOnlinePersonBean
	getCashier()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(invoice.getCashierId());
	}

	public int
	getCheckNumber()
	{
		return invoice.getCheckNumber();
	}

	public void
	setCheckNumber(int _check_number)
	{
		invoice.setCheckNumber(_check_number);
	}

	public String
	getCustomerListID()
	{
		return invoice.getCustomerListID();
	}

	public int
	getId()
	{
		return invoice.getInvoiceRetDbId();
	}

	public String
	getLabel()
	{
		return invoice.getTxnID();
	}

	public PaymentMethodRet
	getPaymentMethod()
	{
		return payment_method;
	}

	public String
	getRequestId()
	{
		return invoice.getRequestID();
	}

	public Vector<InvoiceLineRet>
	getInvoiceLineRetObjects()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		if (invoiceLineRetObjects == null)
		{
			if (this.isNew())
				invoiceLineRetObjects = new Vector<InvoiceLineRet>();
			else
				invoiceLineRetObjects = InvoiceLineRet.getInvoiceLines(this);
		}

		return invoiceLineRetObjects;
	}

	public float
	getSubtotal()
	{
		return invoice.getSubtotal().floatValue();
	}

	public float
	getBalanceRemaining()
	{
		return invoice.getBalanceRemaining().floatValue();
	}

	public float
	getTaxAmount()
	{
		return invoice.getSalesTaxTotal().floatValue();
	}

	public Vector<TenderRet>
	getTenderRetObjects()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (invoiceTenders == null)
		{
			if (this.isNew())
				invoiceTenders = new Vector<TenderRet>();
			else
				invoiceTenders = TenderRet.getTenders(this);
		}

		return invoiceTenders;
	}

	public Date
	getTimeModified()
	{
		return invoice.getTimeModified();
	}

	public Date
	getTxnDate()
	{
		return invoice.getTxnDate();
	}

	public String
	getTxnID()
	{
		return invoice.getTxnID();
	}

	public int
	getTxnNumber()
	{
		return invoice.getTxnNumber();
	}

	public String
	getValue()
	{
		return invoice.getInvoiceRetDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		// see if this Invoice conflicts with any others...

		if (invoice.getInvoiceNumber() > 0)
		{
			Criteria crit = new Criteria();
			crit.add(InvoiceRetDbPeer.COMPANY_ID, invoice.getCompanyId());
			crit.add(InvoiceRetDbPeer.INVOICE_NUMBER, invoice.getInvoiceNumber());
			List obj_list = InvoiceRetDbPeer.doSelect(crit);
			if (obj_list.size() > 0)
				throw new ObjectAlreadyExistsException("Matching invoice already exists.");
		}

		invoice.save();
    }

	public boolean
	isPaid()
	{
		return invoice.getIsPaid().equals("true");
	}

	public void
	setAssociate(UKOnlinePersonBean _associate)
		throws TorqueException
	{
		invoice.setPractitionerId(_associate.getId());
	}

	public void
	setCashier(UKOnlinePersonBean _cashier)
		throws TorqueException
	{
		invoice.setCashierId(_cashier.getId());
	}

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		invoice.setCompanyId(_company.getId());
	}

	public void
	setCustomerListID(String _customerListID)
		throws TorqueException
	{
		invoice.setCustomerListID(_customerListID);
	}

	public void
	setPaymentMethod(PaymentMethodRet _payment_method)
	{
		payment_method = _payment_method;
	}

	public void
	setRequestId(String _request_id)
	{
		invoice.setRequestID(_request_id);
	}

	public void
	setinvoiceLineRetObjects(Vector<InvoiceLineRet> _invoiceLineRetObjects)
	{
		invoiceLineRetObjects = _invoiceLineRetObjects;
	}

	public void
	setInvoiceNumber(int _invoice_number)
	{
		invoice.setInvoiceNumber(_invoice_number);
	}

	public String
	getRefNumber()
	{
		return invoice.getRefNumber();
	}

	public void
	setRefNumber(String _ref_number)
	{
		invoice.setRefNumber(_ref_number);
	}

	public void
	setIsPending(String _is_pending)
	{
		invoice.setIsPending(_is_pending);
	}

	public void
	setIsFinanceCharge(String _is_finance_charge)
	{
		invoice.setIsFinanceCharge(_is_finance_charge);
	}

	public void
	setIsPaid(String _is_paid)
	{
		invoice.setIsPaid(_is_paid);
	}

	public void
	setPONumber(String _po_number)
	{
		invoice.setPONumber(_po_number);
	}

	public void
	setSubtotal(float _subtotal)
	{
		invoice.setSubtotal(new BigDecimal(_subtotal));
	}

	public void
	setDueDate(Date _due_date)
	{
		invoice.setDueDate(_due_date);
	}

	public void
	setBalanceRemaining(float _subtotal)
	{
		invoice.setBalanceRemaining(new BigDecimal(_subtotal));
	}

	public void
	setTaxAmount(float _taxAmount)
	{
		invoice.setSalesTaxTotal(new BigDecimal(_taxAmount));
	}

	public void
	setTenderRetObjects(Vector<TenderRet> _invoiceTenders)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		invoiceTenders = _invoiceTenders;
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		invoice.setTimeCreated(_timeCreated);
	}

	public void
	setEditSequence(String _editSequence)
	{
		invoice.setEditSequence(_editSequence);
	}

	public void
	setMemo(String _memo)
	{
		invoice.setMemo(_memo);
	}

	public void
	setTxnNumber(int _txnNumber)
	{
		invoice.setTxnNumber(_txnNumber);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		invoice.setTimeModified(_timeModified);

		if ((InvoiceRet.last_update == null) || _timeModified.after(InvoiceRet.last_update))
		{
			// apparently "greater than" still return time values that are equal in PoS.  so add a second to the last modified date, I guess..

			Calendar mod_time = Calendar.getInstance();
			mod_time.setTime(_timeModified);
			mod_time.add(Calendar.SECOND, 1);
			InvoiceRet.last_update = mod_time.getTime();
		}
	}

	public void
	setTxnDate(Date _txnDate)
	{
		invoice.setTxnDate(_txnDate);
	}

	public void
	setTxnID(String _txn_id)
	{
		invoice.setTxnID(_txn_id);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		invoice.save();
    }
}
