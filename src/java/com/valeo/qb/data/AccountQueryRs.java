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
AccountQueryRs
	extends QueryRs
{
	// INSTANCE VARIABLES

	private Vector<AccountRet> accountRetObjects = new Vector<AccountRet>();

	// INSTANCE METHODS

	public void
	add(AccountRet _object)
	{
		accountRetObjects.addElement(_object);
	}

	public Vector<AccountRet>
	getAccountRetObjects()
	{
		return accountRetObjects;
	}
}