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
import com.valeo.qb.data.PaymentMethodRet;
import com.valeo.qb.data.SalesReceiptLineRet;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
SalesReceiptRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES
	
	private static Date last_update;
    
    protected static HashMap<Integer,SalesReceiptRet> hash = new HashMap<Integer,SalesReceiptRet>(11);
	
	// CLASS METHODS
	
	public static Date
	getLastUpdateDate(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		if (last_update == null)
		{
			Criteria crit = new Criteria();
			crit.add(SalesReceiptRetDbPeer.COMPANY_ID, _company.getId());
			crit.addDescendingOrderByColumn(SalesReceiptRetDbPeer.TIME_MODIFIED);
			List objList = SalesReceiptRetDbPeer.doSelect(crit);
			if (objList.size() > 0)
			{
				SalesReceiptRetDb sales_receipt_obj = (SalesReceiptRetDb)objList.get(0);
				last_update = sales_receipt_obj.getTimeModified();
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

    public static SalesReceiptRet
    getSalesReceipt(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		SalesReceiptRet obj = (SalesReceiptRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(SalesReceiptRetDbPeer.SALES_RECEIPT_RET_DB_ID, _id);
			List objList = SalesReceiptRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Sales Receipt with id: " + _id);

			obj = SalesReceiptRet.getSalesReceipt((SalesReceiptRetDb)objList.get(0));
		}

		return obj;
    }

    private static SalesReceiptRet
    getSalesReceipt(SalesReceiptRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getSalesReceiptRetDbId());
		SalesReceiptRet obj = (SalesReceiptRet)hash.get(key);
		if (obj == null)
		{
			obj = new SalesReceiptRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static SalesReceiptRet
    getSalesReceipt(CompanyBean _company, String _txn_id)
		throws TorqueException, ObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(SalesReceiptRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(SalesReceiptRetDbPeer.TXN_I_D, _txn_id);
		List obj_list = SalesReceiptRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return SalesReceiptRet.getSalesReceipt((SalesReceiptRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Sales Receipt with transaction id: " + _txn_id);
		else
			throw new ObjectNotFoundException("Could not locate unique Sales Receipt with transaction id: " + _txn_id);
    }

    public static SalesReceiptRet
    getSalesReceipt(CompanyBean _company, int _sales_receipt_number)
		throws TorqueException, ObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(SalesReceiptRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(SalesReceiptRetDbPeer.SALES_RECEIPT_NUMBER, _sales_receipt_number);
		List obj_list = SalesReceiptRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return SalesReceiptRet.getSalesReceipt((SalesReceiptRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Sales Receipt with sales receipt number: " + _sales_receipt_number);
		else
			throw new ObjectNotFoundException("Could not locate unique Sales Receipt with sales receipt number: " + _sales_receipt_number);
    }

    public static SalesReceiptRet
    getSalesReceipt(CompanyBean _company, OrderBean _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(SalesReceiptRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(SalesReceiptRetDbPeer.TXN_I_D, _order.getAuthorizationCode());
		List obj_list = SalesReceiptRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return SalesReceiptRet.getSalesReceipt((SalesReceiptRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Sales Receipt for order: " + _order.getAuthorizationCode());
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Sales Receipt for order: " + _order.getAuthorizationCode());
    }

    public static SalesReceiptRet
    getSalesReceipt(CompanyBean _company, String _customer_list_id, Date _time_modified)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(SalesReceiptRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(SalesReceiptRetDbPeer.CUSTOMER_LIST_I_D, _customer_list_id);
		crit.add(SalesReceiptRetDbPeer.TIME_MODIFIED, _time_modified);

		List obj_list = SalesReceiptRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return SalesReceiptRet.getSalesReceipt((SalesReceiptRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Sales Receipt with customer list id: " + _customer_list_id + " at " + CUBean.getUserDateString(_time_modified));
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Sales Receipt with customer list id: " + _customer_list_id + " at " + CUBean.getUserDateString(_time_modified));
    }

    public static SalesReceiptRet
    getSalesReceiptForTxnNumber(CompanyBean _company, int _txn_number)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(SalesReceiptRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(SalesReceiptRetDbPeer.TXN_NUMBER, _txn_number);
		List obj_list = SalesReceiptRetDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return SalesReceiptRet.getSalesReceipt((SalesReceiptRetDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Could not locate Sales Receipt with txn number: " + _txn_number);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Sales Receipt with txn number: " + _txn_number);
    }

    public static Vector
    getSalesReceipts(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(SalesReceiptRetDbPeer.COMPANY_ID, _company.getId());
		Iterator obj_itr = SalesReceiptRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(SalesReceiptRet.getSalesReceipt((SalesReceiptRetDb)obj_itr.next()));
		
		return vec;
    }

    public static Vector
    getSalesReceipts(CompanyBean _company, String _type, String _history_doc_status)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(SalesReceiptRetDbPeer.COMPANY_ID, _company.getId());
		crit.add(SalesReceiptRetDbPeer.SALES_RECEIPT_TYPE, _type);
		crit.add(SalesReceiptRetDbPeer.HISTORY_DOC_STATUS, _history_doc_status);
		Iterator obj_itr = SalesReceiptRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(SalesReceiptRet.getSalesReceipt((SalesReceiptRetDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getSalesReceipts(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(SalesReceiptRetDbPeer.CUSTOMER_LIST_I_D, _person.getEmployeeNumberString());
		Iterator obj_itr = SalesReceiptRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(SalesReceiptRet.getSalesReceipt((SalesReceiptRetDb)obj_itr.next()));

		return vec;
    }
	
	// INSTANCE VARIABLES

	private SalesReceiptRetDb sales_receipt;
	
	private PaymentMethodRet payment_method;
	
	private Vector<SalesReceiptItemRet> salesRecieptItemRetObjects;
	private Vector<SalesReceiptLineRet> salesRecieptLineRetObjects;
	private Vector<TenderRet> salesReceiptTenders;
	
	// CONSTRUCTORS
	
	public
	SalesReceiptRet()
	{
		sales_receipt = new SalesReceiptRetDb();
		isNew = true;
	}
	
	public
	SalesReceiptRet(SalesReceiptRetDb _sales_receipt)
	{
		sales_receipt = _sales_receipt;
		isNew = false;
	}
	
	// INSTANCE METHODS
	
	public void
	add(SalesReceiptItemRet _object)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		if (salesRecieptItemRetObjects == null)
		{
			if (this.isNew())
				salesRecieptItemRetObjects = new Vector();
			else
				this.getSalesRecieptItemRetObjects();
		}

		System.out.println("salesRecieptItemRetObjects >" + salesRecieptItemRetObjects.size());

		salesRecieptItemRetObjects.addElement(_object);
	}

	public void
	add(SalesReceiptLineRet _object)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		if (salesRecieptLineRetObjects == null)
		{
			if (this.isNew())
				salesRecieptLineRetObjects = new Vector();
			else
				this.getSalesRecieptLineRetObjects();
		}

		System.out.println("salesRecieptLineRetObjects >" + salesRecieptLineRetObjects.size());

		salesRecieptLineRetObjects.addElement(_object);
	}

	public void
	add(TenderRet _object)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (salesReceiptTenders == null)
		{
			if (this.isNew())
				salesReceiptTenders = new Vector();
			else
				this.getTenderRetObjects();
		}

		salesReceiptTenders.addElement(_object);
	}

	public UKOnlinePersonBean
	getAssociate()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(sales_receipt.getPractitionerId());
	}

	public UKOnlinePersonBean
	getCashier()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(sales_receipt.getCashierId());
	}

	public int
	getCheckNumber()
	{
		return sales_receipt.getCheckNumber();
	}

	public void
	setCheckNumber(int _check_number)
	{
		sales_receipt.setCheckNumber(_check_number);
	}
	
	public String
	getCustomerListID()
	{
		return sales_receipt.getCustomerListID();
	}

	public String
	getHistoryDocStatus()
	{
		return sales_receipt.getHistoryDocStatus();
	}
	
	public int
	getId()
	{
		return sales_receipt.getSalesReceiptRetDbId();
	}
	
	public String
	getLabel()
	{
		return sales_receipt.getTotal().toString();
	}

	public PaymentMethodRet
	getPaymentMethod()
	{
		return payment_method;
	}
	
	public String
	getRequestId()
	{
		return sales_receipt.getRequestID();
	}
	
	public Vector<SalesReceiptItemRet>
	getSalesRecieptItemRetObjects()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		if (salesRecieptItemRetObjects == null)
		{
			if (this.isNew())
				salesRecieptItemRetObjects = new Vector<SalesReceiptItemRet>();
			else
				salesRecieptItemRetObjects = SalesReceiptItemRet.getSalesReceiptItems(this);
		}
		
		return salesRecieptItemRetObjects;
	}

	public Vector<SalesReceiptLineRet>
	getSalesRecieptLineRetObjects()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		if (salesRecieptLineRetObjects == null)
		{
			if (this.isNew())
				salesRecieptLineRetObjects = new Vector<SalesReceiptLineRet>();
			else
				salesRecieptLineRetObjects = SalesReceiptLineRet.getSalesReceiptLines(this);
		}

		return salesRecieptLineRetObjects;
	}

	public int
	getSalesReceiptNumber()
	{
		return sales_receipt.getSalesReceiptNumber();
	}

	public String
	getSalesReceiptType()
	{
		return sales_receipt.getSalesReceiptType();
	}
	
	public float
	getSubtotal()
	{
		return sales_receipt.getSubtotal().floatValue();
	}
	
	public float
	getTaxAmount()
	{
		return sales_receipt.getTaxAmount().floatValue();
	}

	public Vector<TenderRet>
	getTenderRetObjects()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (salesReceiptTenders == null)
		{
			if (this.isNew())
				salesReceiptTenders = new Vector<TenderRet>();
			else
				salesReceiptTenders = TenderRet.getTenders(this);
		}

		return salesReceiptTenders;
	}

	public String
	getTenderType()
	{
		return sales_receipt.getTenderType();
	}

	public Date
	getTimeModified()
	{
		return sales_receipt.getTimeModified();
	}
	
	public float
	getTotal()
	{
		return sales_receipt.getTotal().floatValue();
	}
	
	public Date
	getTxnDate()
	{
		return sales_receipt.getTxnDate();
	}

	public String
	getTxnID()
	{
		return sales_receipt.getTxnID();
	}

	public int
	getTxnNumber()
	{
		return sales_receipt.getTxnNumber();
	}
	
	public String
	getValue()
	{
		return sales_receipt.getSalesReceiptRetDbId() + "";
	}
    
    protected void
    insertObject()
		throws Exception
    {
		// see if this sales receipt conflicts with any others...

		if (sales_receipt.getSalesReceiptNumber() > 0)
		{
			Criteria crit = new Criteria();
			crit.add(SalesReceiptRetDbPeer.COMPANY_ID, sales_receipt.getCompanyId());
			crit.add(SalesReceiptRetDbPeer.SALES_RECEIPT_NUMBER, sales_receipt.getSalesReceiptNumber());
			List obj_list = SalesReceiptRetDbPeer.doSelect(crit);
			if (obj_list.size() > 0)
				throw new ObjectAlreadyExistsException("Matching sales receipt already exists.");
		}
		
		sales_receipt.save();
    }

	public void
	setAssociate(UKOnlinePersonBean _associate)
		throws TorqueException
	{
		sales_receipt.setPractitionerId(_associate.getId());
	}

	public void
	setCashier(UKOnlinePersonBean _cashier)
		throws TorqueException
	{
		sales_receipt.setCashierId(_cashier.getId());
	}
	
	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		sales_receipt.setCompanyId(_company.getId());
	}
	
	public void
	setCustomerListID(String _customerListID)
		throws TorqueException
	{
		sales_receipt.setCustomerListID(_customerListID);
	}

	public void
	setHistoryDocStatus(String _history_doc_status)
	{
		sales_receipt.setHistoryDocStatus(_history_doc_status);
	}

	public void
	setPaymentMethod(PaymentMethodRet _payment_method)
	{
		payment_method = _payment_method;
	}
	
	public void
	setRequestId(String _request_id)
	{
		sales_receipt.setRequestID(_request_id);
	}

	public void
	setSalesRecieptItemRetObjects(Vector<SalesReceiptItemRet> _salesRecieptItemRetObjects)
	{
		salesRecieptItemRetObjects = _salesRecieptItemRetObjects;
	}

	public void
	setSalesRecieptLineRetObjects(Vector<SalesReceiptLineRet> _salesRecieptLineRetObjects)
	{
		salesRecieptLineRetObjects = _salesRecieptLineRetObjects;
	}

	public void
	setSalesReceiptNumber(int _sales_receipt_number)
	{
		sales_receipt.setSalesReceiptNumber(_sales_receipt_number);
	}

	public void
	setSalesReceiptType(String _sales_receipt_type)
	{
		sales_receipt.setSalesReceiptType(_sales_receipt_type);
	}
	
	public void
	setSubtotal(float _subtotal)
	{
		sales_receipt.setSubtotal(new BigDecimal(_subtotal));
	}
	
	public void
	setTaxAmount(float _taxAmount)
	{
		sales_receipt.setTaxAmount(new BigDecimal(_taxAmount));
	}

	public void
	setTenderRetObjects(Vector<TenderRet> _salesReceiptTenders)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		salesReceiptTenders = _salesReceiptTenders;
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		sales_receipt.setTimeCreated(_timeCreated);
	}

	public void
	setEditSequence(String _editSequence)
	{
		sales_receipt.setEditSequence(_editSequence);
	}

	public void
	setTxnNumber(int _txnNumber)
	{
		sales_receipt.setTxnNumber(_txnNumber);
	}
	
	public void
	setTimeModified(Date _timeModified)
	{
		sales_receipt.setTimeModified(_timeModified);
		
		if ((SalesReceiptRet.last_update == null) || _timeModified.after(SalesReceiptRet.last_update))
		{
			// apparently "greater than" still return time values that are equal in PoS.  so add a second to the last modified date, I guess..
			
			Calendar mod_time = Calendar.getInstance();
			mod_time.setTime(_timeModified);
			mod_time.add(Calendar.SECOND, 1);
			SalesReceiptRet.last_update = mod_time.getTime();
		}
	}

	public void
	setTenderType(String _tender_type)
	{
		sales_receipt.setTenderType(_tender_type);
	}
	
	public void
	setTotal(float _total)
	{
		sales_receipt.setTotal(new BigDecimal(_total));
	}
	
	public void
	setTxnDate(Date _txnDate)
	{
		sales_receipt.setTxnDate(_txnDate);
	}

	public void
	setTxnID(String _txn_id)
	{
		sales_receipt.setTxnID(_txn_id);
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		sales_receipt.save();
    }
}
