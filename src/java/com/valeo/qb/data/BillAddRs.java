
package com.valeo.qb.data;

import com.valeo.qbpos.data.QueryRs;
import com.valeo.qb.data.BillRet;
import java.util.Vector;

/**
 * created 5/11/18
 * @author marlo
 */
public class
BillAddRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<BillRet> objects = new Vector<BillRet>();

	// INSTANCE METHODS

	public void
	add(BillRet _object)
	{
		objects.addElement(_object);
	}

	public Vector<BillRet>
	getBillRetObjects()
	{
		return objects;
	}
}
