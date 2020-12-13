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
SalesReceiptItemRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES
    
    protected static HashMap<Integer,SalesReceiptItemRet> hash = new HashMap<Integer,SalesReceiptItemRet>(11);
	
	// CLASS METHODS

    public static void
    deleteSalesReceiptItems(SalesReceiptRet _sales_receipt)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(SalesReceiptItemRetDbPeer.SALES_RECEIPT_RET_DB_ID, _sales_receipt.getId());
		SalesReceiptItemRetDbPeer.doDelete(crit);
    }

    public static SalesReceiptItemRet
    getSalesReceiptItem(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		SalesReceiptItemRet obj = (SalesReceiptItemRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(SalesReceiptItemRetDbPeer.SALES_RECEIPT_ITEM_RET_DB_ID, _id);
			List objList = SalesReceiptItemRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Sales Receipt Item with id: " + _id);

			obj = SalesReceiptItemRet.getSalesReceiptItem((SalesReceiptItemRetDb)objList.get(0));
		}

		return obj;
    }

    private static SalesReceiptItemRet
    getSalesReceiptItem(SalesReceiptItemRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getSalesReceiptItemRetDbId());
		SalesReceiptItemRet obj = (SalesReceiptItemRet)hash.get(key);
		if (obj == null)
		{
			obj = new SalesReceiptItemRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getSalesReceiptItems(SalesReceiptRet _sales_receipt)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
    {
		if (_sales_receipt.getId() == 0)
			throw new IllegalValueException("Sales receipt is new.  Unable to fetch items.");

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(SalesReceiptItemRetDbPeer.SALES_RECEIPT_RET_DB_ID, _sales_receipt.getId());
		Iterator obj_itr = SalesReceiptItemRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(SalesReceiptItemRet.getSalesReceiptItem((SalesReceiptItemRetDb)obj_itr.next()));
		
		return vec;
    }
	
	// INSTANCE VARIABLES
	
	private SalesReceiptItemRetDb item;
	
	// CONSTRUCTORS
	
	public
	SalesReceiptItemRet()
	{
		item = new SalesReceiptItemRetDb();
		isNew = true;
	}
	
	public
	SalesReceiptItemRet(SalesReceiptItemRetDb _item)
	{
		item = _item;
		isNew = false;
	}
	
	// INSTANCE METHODS

	public UKOnlinePersonBean
	getAssociate()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(item.getPractitionerId());
	}

	public BigDecimal
	getCommission()
	{
		return item.getCommission();
	}
	
	public String
	getDesc1()
	{
		return item.getDesc1();
	}
	
	public String
	getListID()
	{
		return item.getListID();
	}
	
	public int
	getId()
	{
		return item.getSalesReceiptItemRetDbId();
	}
	
	public int
	getItemNumber()
	{
		return item.getItemNumber();
	}
	
	public String
	getLabel()
	{
		return item.getDesc1();
	}
	
	public float
	getPrice()
	{
		return item.getPrice().floatValue();
	}
	
	public float
	getQty()
	{
		return item.getQty().floatValue();
	}

	public SalesReceiptRet
	getSalesReceipt()
		throws TorqueException, ObjectNotFoundException
	{
		return SalesReceiptRet.getSalesReceipt(item.getSalesReceiptRetDbId());
	}
	
	public float
	getTaxAmount()
	{
		return item.getTaxAmount().floatValue();
	}
	
	public String
	getValue()
	{
		return item.getSalesReceiptItemRetDbId() + "";
	}
    
    protected void
    insertObject()
		throws Exception
    {
		item.save();
    }

	public void
	setAssociate(UKOnlinePersonBean _associate)
		throws TorqueException
	{
		item.setPractitionerId(_associate.getId());
	}

	public void
	setCommission(float _commission)
	{
		item.setCommission(new BigDecimal(_commission));
	}
	
	public void
	setDesc1(String _desc1)
	{
		item.setDesc1(_desc1);
	}
	
	public void
	setListID(String _listID)
		throws TorqueException
	{
		item.setListID(_listID);
	}
	
	public void
	setItemNumber(int _itemNumber)
		throws TorqueException
	{
		item.setItemNumber(_itemNumber);
	}
	
	public void
	setPrice(float _price)
	{
		item.setPrice(new BigDecimal(_price));
	}
	
	public void
	setQty(float _qty)
	{
		item.setQty(new BigDecimal(_qty));
	}
	
	public void
	setSalesReceipt(SalesReceiptRet _sales_receipt)
		throws TorqueException, IllegalValueException
	{
		if (_sales_receipt.getId() == 0)
			throw new IllegalValueException("Attempt to set sales receipt with id of zero.");
		item.setSalesReceiptRetDbId(_sales_receipt.getId());
	}
	
	public void
	setTaxAmount(float _taxAmount)
	{
		item.setTaxAmount(new BigDecimal(_taxAmount));
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		item.save();
    }
}
