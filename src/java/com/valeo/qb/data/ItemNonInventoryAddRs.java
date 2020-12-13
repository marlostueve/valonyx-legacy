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
ItemNonInventoryAddRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<ItemRet> objects = new Vector<ItemRet>();

	// INSTANCE METHODS

	public void
	add(ItemRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<ItemRet>
	getItemRetObjects()
	{
		return objects;
	}
}
