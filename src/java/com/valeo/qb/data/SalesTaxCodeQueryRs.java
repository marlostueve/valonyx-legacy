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
SalesTaxCodeQueryRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<SalesTaxCodeRet> objects = new Vector<SalesTaxCodeRet>();

	// INSTANCE METHODS

	public void
	add(SalesTaxCodeRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<SalesTaxCodeRet>
	getItemRetObjects()
	{
		return objects;
	}
}