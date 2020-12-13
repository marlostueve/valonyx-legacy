/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb.data;

import com.valeo.qbpos.data.QueryRs;
import java.util.Vector;

/**
 *
 * @author marlo
 */
public class
ItemQueryRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<ItemRet> objects = new Vector<ItemRet>();
	private Vector<ItemSalesTaxRet> item_sales_tax_ret_objects = new Vector<ItemSalesTaxRet>();
	private Vector<ItemSalesTaxGroupRet> item_sales_tax_group_ret_objects = new Vector<ItemSalesTaxGroupRet>();
	private Vector<ItemPaymentRet> item_payment_ret_objects = new Vector<ItemPaymentRet>();
	private Vector<ItemGroupRet> item_group_ret_objects = new Vector<ItemGroupRet>();

	// INSTANCE METHODS

	public void
	add(ItemRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<ItemRet>
	getItemRetObjects()
	{
		return objects;
	}

	public void
	add(ItemSalesTaxRet _object)
	{
		item_sales_tax_ret_objects.addElement(_object);
	}

	public Vector<ItemSalesTaxRet>
	getItemSalesTaxRetObjects()
	{
		return item_sales_tax_ret_objects;
	}

	public void
	add(ItemSalesTaxGroupRet _object)
	{
		item_sales_tax_group_ret_objects.addElement(_object);
	}

	public Vector<ItemSalesTaxGroupRet>
	getItemSalesTaxGroupRetObjects()
	{
		return item_sales_tax_group_ret_objects;
	}

	public void
	add(ItemPaymentRet _object)
	{
		item_payment_ret_objects.addElement(_object);
	}

	public Vector<ItemPaymentRet>
	getItemPaymentObjects()
	{
		return item_payment_ret_objects;
	}

	public void
	add(ItemGroupRet _object)
	{
		item_group_ret_objects.addElement(_object);
	}

	public Vector<ItemGroupRet>
	getItemGroupRetObjects()
	{
		return item_group_ret_objects;
	}
}
