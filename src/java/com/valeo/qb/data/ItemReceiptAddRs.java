/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb.data;

import com.valeo.qbpos.data.QueryRs;
import com.valeo.qb.data.ItemReceiptRet;
import java.util.Vector;

/**
 *
 * @author marlo
 */
public class
ItemReceiptAddRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<ItemReceiptRet> objects = new Vector<ItemReceiptRet>();

	// INSTANCE METHODS

	public void
	add(ItemReceiptRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<ItemReceiptRet>
	getItemReceiptRetObjects()
	{
		return objects;
	}
}
