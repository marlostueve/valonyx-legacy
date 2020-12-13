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
InvoiceQueryRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<InvoiceRet> invoiceRetObjects = new Vector<InvoiceRet>();

	// INSTANCE METHODS

	public void
	add(InvoiceRet _object)
	{
		invoiceRetObjects.addElement(_object);
	}

	public Vector<InvoiceRet>
	getInvoiceRetObjects()
	{
		return invoiceRetObjects;
	}
}
