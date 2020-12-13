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
ItemSalesTaxAddRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<ItemSalesTaxRet> objects = new Vector<ItemSalesTaxRet>();

	// INSTANCE METHODS

	public void
	add(ItemSalesTaxRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<ItemSalesTaxRet>
	getItemRetObjects()
	{
		return objects;
	}
}
