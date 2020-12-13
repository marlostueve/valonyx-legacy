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
VendorQueryRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<VendorRet> vendorRetObjects = new Vector<VendorRet>();

	// INSTANCE METHODS

	public void
	add(VendorRet _object)
	{
		vendorRetObjects.addElement(_object);
	}

	public Vector<VendorRet>
	getVendorRetObjects()
	{
		return vendorRetObjects;
	}
}
