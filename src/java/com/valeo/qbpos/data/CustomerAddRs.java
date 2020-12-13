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
CustomerAddRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<CustomerRet> customerRetObjects = new Vector<CustomerRet>();

	// INSTANCE METHODS

	public void
	add(CustomerRet _object)
	{
		customerRetObjects.addElement(_object);
	}

	public Vector<CustomerRet>
	getCustomerRetObjects()
	{
		return customerRetObjects;
	}
}
