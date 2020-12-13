/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valeo.qb.data;

import com.valeo.qbpos.data.QueryRs;
import com.valeo.qbpos.data.SalesReceiptRet;
import java.util.Vector;

/**
 *
 * @author marlo
 */
public class
ItemPaymentAddRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<ItemPaymentRet> objects = new Vector<ItemPaymentRet>();

	// INSTANCE METHODS

	public void
	add(ItemPaymentRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<ItemPaymentRet>
	getItemPaymentRetObjects()
	{
		return objects;
	}
}
