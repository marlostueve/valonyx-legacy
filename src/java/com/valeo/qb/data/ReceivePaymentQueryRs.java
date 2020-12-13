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
ReceivePaymentQueryRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<ReceivePaymentRet> receivePaymentRetObjects = new Vector<ReceivePaymentRet>();

	// INSTANCE METHODS

	public void
	add(ReceivePaymentRet _object)
	{
		receivePaymentRetObjects.addElement(_object);
	}

	public Vector<ReceivePaymentRet>
	getReceivePaymentRetObjects()
	{
		return receivePaymentRetObjects;
	}
}
