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
ReceivePaymentAddRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<ReceivePaymentRet> objects = new Vector<ReceivePaymentRet>();

	// INSTANCE METHODS

	public void
	add(ReceivePaymentRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<ReceivePaymentRet>
	getReceivePaymentRetObjects()
	{
		return objects;
	}
}
