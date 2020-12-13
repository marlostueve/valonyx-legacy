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
SalesReceiptAddRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<SalesReceiptRet> objects = new Vector<SalesReceiptRet>();

	// INSTANCE METHODS

	public void
	add(SalesReceiptRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<SalesReceiptRet>
	getSalesReceiptRetObjects()
	{
		return objects;
	}
}
