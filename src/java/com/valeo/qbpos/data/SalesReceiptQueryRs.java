/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbpos.data;

import java.util.Vector;

/**
 *
 * @author marlo
 */
public class
SalesReceiptQueryRs
	extends QueryRs
{
	// INSTANCE VARIABLES
	
	private Vector<SalesReceiptRet> salesReceiptRetObjects = new Vector<SalesReceiptRet>();
	
	// INSTANCE METHODS
	
	public void
	add(SalesReceiptRet _object)
	{
		salesReceiptRetObjects.addElement(_object);
	}
	
	public Vector<SalesReceiptRet>
	getSalesReceiptRetObjects()
	{
		return salesReceiptRetObjects;
	}
}
