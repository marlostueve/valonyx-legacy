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
ItemLineRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,ItemLineRet> hash = new HashMap<Integer,ItemLineRet>(11);

	// CLASS METHODS

    public static ItemLineRet
    getItemLine(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ItemLineRet obj = (ItemLineRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ItemLineRetDbPeer.ITEM_RECEIPT_RET_DB_ID, _id);
			List objList = ItemLineRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Item Line with id: " + _id);

			obj = ItemLineRet.getItemLine((ItemLineRetDb)objList.get(0));
		}

		return obj;
    }

    public static ItemLineRet
    getItemLine(ItemLineRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getItemLineRetDbId());
		ItemLineRet obj = (ItemLineRet)hash.get(key);
		if (obj == null)
		{
			obj = new ItemLineRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

	// INSTANCE VARIABLES

	private ItemLineRetDb item_line;

	/*
	private String termsListID;
	private String termsFullName;

	private String name;
	 *
	 */

	// CONSTRUCTORS

	public
	ItemLineRet()
	{
		item_line = new ItemLineRetDb();
		isNew = true;
	}

	public
	ItemLineRet(ItemLineRetDb _item_line)
	{
		item_line = _item_line;
		isNew = false;
	}

	// INSTANCE METHODS

	public String
	getTxnLineID()
	{
		return item_line.getTxnLineID();
	}

	public CheckoutCodeBean
	getCheckoutCode()
		throws TorqueException, ObjectNotFoundException
	{
		return CheckoutCodeBean.getCheckoutCode(item_line.getCheckoutCodeId());
	}

	public int
	getId()
	{
		return item_line.getItemLineRetDbId();
	}

	public String
	getLabel()
	{
		return this.getValue();
	}

	public String
	getValue()
	{
		return item_line.getItemLineRetDbId() + "";
	}

	public BigDecimal
	getQuantity()
	{
		return item_line.getQuantity();
	}

	public BigDecimal
	getCost()
	{
		return item_line.getCost();
	}

	public BigDecimal
	getAmount()
	{
		return item_line.getAmount();
	}

    protected void
    insertObject()
		throws Exception
    {
		item_line.save();
    }

	public void
	setTxnLineID(String _str)
	{
		item_line.setTxnLineID(_str);
	}

	public void
	setQuantity(float _f)
	{
		item_line.setQuantity(new BigDecimal(_f));
	}

	public void
	setCost(float _f)
	{
		item_line.setCost(new BigDecimal(_f));
	}

	public void
	setAmount(float _f)
	{
		item_line.setAmount(new BigDecimal(_f));
	}

	public void
	setDesc(String _str)
	{
		item_line.setDescription(_str);
	}

	public void
	setCheckoutCode(CheckoutCodeBean _code)
		throws TorqueException
	{
		item_line.setCheckoutCodeId(_code.getId());
	}

	public void
	setCheckoutCodeId(int _code_id)
		throws TorqueException
	{
		item_line.setCheckoutCodeId(_code_id);
	}

	public void
	setBill(BillRet _item_receipt)
		throws TorqueException
	{
		item_line.setBillRetDbId(_item_receipt.getId());
	}

	public void
	setItemReceipt(ItemReceiptRet _item_receipt)
		throws TorqueException
	{
		item_line.setItemReceiptRetDbId(_item_receipt.getId());
	}

	public int
	getMappingId()
	{
		return item_line.getPurchaseOrderItemMappingId();
	}

	public void
	setMappingId(int _mapping_id)
		throws TorqueException
	{
		item_line.setPurchaseOrderItemMappingId(_mapping_id);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		item_line.save();
    }
}