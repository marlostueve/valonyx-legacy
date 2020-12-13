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
import com.badiyan.uk.online.beans.CheckoutCodeBean;
import java.math.RoundingMode;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
InvoiceLineRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,InvoiceLineRet> hash = new HashMap<Integer,InvoiceLineRet>(11);

	// CLASS METHODS

    public static void
    deleteInvoiceLines(InvoiceRet _invoice)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(InvoiceItemRetDbPeer.INVOICE_RET_DB_ID, _invoice.getId());
		InvoiceItemRetDbPeer.doDelete(crit);
    }

    public static InvoiceLineRet
    getInvoiceLine(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		InvoiceLineRet obj = (InvoiceLineRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(InvoiceItemRetDbPeer.INVOICE_ITEM_RET_DB_ID, _id);
			List objList = InvoiceItemRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate invoice Line with id: " + _id);

			obj = InvoiceLineRet.getInvoiceLine((InvoiceItemRetDb)objList.get(0));
		}

		return obj;
    }

    public static InvoiceLineRet
    getInvoiceLine(InvoiceRet _invoice, String _txn_line_id)
		throws TorqueException, ObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(InvoiceItemRetDbPeer.INVOICE_RET_DB_ID, _invoice.getId());
		crit.add(InvoiceItemRetDbPeer.TXN_LINE_I_D, _txn_line_id);
		List objList = InvoiceItemRetDbPeer.doSelect(crit);
		if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate invoice Line with txn id: " + _txn_line_id);

		return InvoiceLineRet.getInvoiceLine((InvoiceItemRetDb)objList.get(0));
		
    }

    private static InvoiceLineRet
    getInvoiceLine(InvoiceItemRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getInvoiceItemRetDbId());
		InvoiceLineRet obj = (InvoiceLineRet)hash.get(key);
		if (obj == null)
		{
			obj = new InvoiceLineRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getInvoiceLines(InvoiceRet _invoice)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
    {
		if (_invoice.getId() == 0)
			throw new IllegalValueException("invoice is new.  Unable to fetch lines.");

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(InvoiceItemRetDbPeer.INVOICE_RET_DB_ID, _invoice.getId());
		Iterator obj_itr = InvoiceItemRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(InvoiceLineRet.getInvoiceLine((InvoiceItemRetDb)obj_itr.next()));

		return vec;
    }

	// INSTANCE VARIABLES

	private InvoiceItemRetDb line;

	private String sales_tax_code_list_id;

	// CONSTRUCTORS

	public
	InvoiceLineRet()
	{
		line = new InvoiceItemRetDb();
		isNew = true;
	}

	public
	InvoiceLineRet(InvoiceItemRetDb _line)
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
		return line.getInvoiceItemRetDbId();
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

	public InvoiceRet
	getInvoice()
		throws TorqueException, ObjectNotFoundException
	{
		return InvoiceRet.getInvoice(line.getInvoiceRetDbId());
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
		return line.getInvoiceItemRetDbId() + "";
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
	setInvoice(InvoiceRet _invoice)
		throws TorqueException, IllegalValueException
	{
		if (_invoice.getId() == 0)
			throw new IllegalValueException("Attempt to set invoice with id of zero.");
		line.setInvoiceRetDbId(_invoice.getId());
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
