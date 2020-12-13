/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valeo.qb.data;

/**
 *
 * @author marlo
 */
public class PurchaseOrderRet {
	
	String txnId = null;
	String refNumber = null;
	
	public String
	getTxnID()
	{
		return txnId;
	}

	public void
	setTxnID(String _txn_id)
	{
		//bill.setTxnID(_txn_id);
		txnId = _txn_id;
	}
	
	public String
	getRefNumber()
	{
		return refNumber;
	}

	public void
	setRefNumber(String _refNumber)
	{
		//bill.setRefNumber(_refNumber);
		refNumber = _refNumber;
	}
	
}
