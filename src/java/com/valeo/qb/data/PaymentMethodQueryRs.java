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
PaymentMethodQueryRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<PaymentMethodRet> objects = new Vector<PaymentMethodRet>();

	// INSTANCE METHODS

	public void
	add(PaymentMethodRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<PaymentMethodRet>
	getPaymentMethodRetObjects()
	{
		return objects;
	}
}
