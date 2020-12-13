/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valeo.qb.data;

import com.valeo.qbpos.data.QueryRs;
import com.valeo.qb.data.PurchaseOrderRet;
import java.util.Vector;

/**
 * created 5/11/18
 * @author marlo
 */
public class
PurchaseOrderAddRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<PurchaseOrderRet> objects = new Vector<PurchaseOrderRet>();

	// INSTANCE METHODS

	public void
	add(PurchaseOrderRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<PurchaseOrderRet>
	getPurchaseOrderRetObjects()
	{
		return objects;
	}
}
