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
import com.valeo.qbpos.data.SalesReceiptRet;
import java.math.RoundingMode;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
SalesReceiptLineRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,SalesReceiptLineRet> hash = new HashMap<Integer,SalesReceiptLineRet>(11);

	// CLASS METHODS

    public static void
    deleteSalesReceiptLines(SalesReceiptRet _sales_receipt)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(SalesReceiptLineRetDbPeer.SALES_RECEIPT_RET_DB_ID, _sales_receipt.getId());
		SalesReceiptLineRetDbPeer.doDelete(crit);
    }

    public static SalesReceiptLineRet
    getSalesReceiptLine(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		SalesReceiptLineRet obj = (SalesReceiptLineRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(SalesReceiptLineRetDbPeer.SALES_RECEIPT_LINE_RET_DB_ID, _id);
			List objList = SalesReceiptLineRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Sales Receipt Line with id: " + _id);

			obj = SalesReceiptLineRet.getSalesReceiptLine((SalesReceiptLineRetDb)objList.get(0));
		}

		return obj;
    }

    private static SalesReceiptLineRet
    getSalesReceiptLine(SalesReceiptLineRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getSalesReceiptLineRetDbId());
		SalesReceiptLineRet obj = (SalesReceiptLineRet)hash.get(key);
		if (obj == null)
		{
			obj = new SalesReceiptLineRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getSalesReceiptLines(SalesReceiptRet _sales_receipt)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
    {
		if (_sales_receipt.getId() == 0)
			throw new IllegalValueException("Sales receipt is new.  Unable to fetch lines.");

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(SalesReceiptLineRetDbPeer.SALES_RECEIPT_RET_DB_ID, _sales_receipt.getId());
		Iterator obj_itr = SalesReceiptLineRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(SalesReceiptLineRet.getSalesReceiptLine((SalesReceiptLineRetDb)obj_itr.next()));

		return vec;
    }

	// INSTANCE VARIABLES

	private SalesReceiptLineRetDb line;

	private String sales_tax_code_list_id;

	// CONSTRUCTORS

	public
	SalesReceiptLineRet()
	{
		line = new SalesReceiptLineRetDb();
		isNew = true;
	}

	public
	SalesReceiptLineRet(SalesReceiptLineRetDb _line)
	{
		line = _line;
		isNew = false;
	}

	// INSTANCE METHODS

	public float
	getAmount()
	{
		if (line.getAmount() == null)
			return 0f;
		return line.getAmount().floatValue();
	}

	public String
	getDesc()
	{
		return line.getDesc1();
	}

	public int
	getId()
	{
		return line.getSalesReceiptLineRetDbId();
	}

	public String
	getItemListId()
	{
		return line.getItemListID();
	}

	public String
	getLabel()
	{
		return line.getDesc1();
	}

	public float
	getQuantity()
		throws IllegalValueException
	{
		if (line.getQuantity() == null)
		{
			if (line.getRate() != null && line.getAmount() != null)
			{
				BigDecimal rate = new BigDecimal(this.getRate());
				BigDecimal amount = new BigDecimal(this.getAmount());
				return amount.divide(rate, 2, RoundingMode.HALF_UP).floatValue();
			}
			else
				throw new IllegalValueException("Unable to determine quantity.");
		}
		else
			return line.getQuantity().floatValue();
	}

	public boolean
	hasRate()
	{
		return (line.getRate() != null);
	}

	public float
	getRate()
	{
		return line.getRate().floatValue();
	}

	public SalesReceiptRet
	getSalesReceipt()
		throws TorqueException, ObjectNotFoundException
	{
		return SalesReceiptRet.getSalesReceipt(line.getSalesReceiptRetDbId());
	}

	public String
	getSalesTaxCodeListID()
	{
		return sales_tax_code_list_id;
	}

	public SalesTaxCodeRet
	getTaxCode()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return SalesTaxCodeRet.getSalesTaxCode(line.getSalesTaxCodeRetDbId());
	}

	public String
	getTxnLineID()
	{
		return line.getTxnLineID();
	}

	public String
	getValue()
	{
		return line.getSalesReceiptLineRetDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		line.save();
    }

	public void
	setAmount(float _amount)
	{
		line.setAmount(new BigDecimal(_amount));
	}

	public void
	setDesc(String _desc)
	{
		line.setDesc1(_desc);
	}

	public void
	setItemListID(String _listID)
		throws TorqueException
	{
		line.setItemListID(_listID);
	}

	public void
	setQuantity(float _quantity)
	{
		line.setQuantity(new BigDecimal(_quantity));
	}

	public void
	setRate(float _rate)
	{
		line.setRate(new BigDecimal(_rate));
	}

	public void
	setSalesReceipt(SalesReceiptRet _sales_receipt)
		throws TorqueException, IllegalValueException
	{
		if (_sales_receipt.getId() == 0)
			throw new IllegalValueException("Attempt to set sales receipt with id of zero.");
		line.setSalesReceiptRetDbId(_sales_receipt.getId());
	}

	public void
	setSalesTaxCodeListID(String _listID)
	{
		sales_tax_code_list_id = _listID;
	}

	public void
	setTaxCode(SalesTaxCodeRet _tax_code)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		line.setSalesTaxCodeRetDbId(_tax_code.getId());
	}

	public void
	setTxnLineID(String _txnLineID)
	{
		line.setTxnLineID(_txnLineID);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		line.save();
    }
}
