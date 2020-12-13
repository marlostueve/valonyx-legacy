/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbpos.data;

/**
 *
 * @author marlo
 */
public class
QueryRs
{
	// INSTANCE VARIABLES

	private String requestID;
	private int retCount;
	private String statusCode;
	private String statusSeverity;
	private String statusMessage;
	private int iteratorRemainingCount;
	private String iteratorID;

	// INSTANCE METHODS

	public String
	getRequestID()
	{
		return requestID;
	}

	public int
	getIteratorRemainingCount()
	{
		return iteratorRemainingCount;
	}

	public void
	setIteratorID(String _iteratorID)
	{
		iteratorID = _iteratorID;
	}

	public void
	setIteratorRemainingCount(int _iteratorRemainingCount)
	{
		iteratorRemainingCount = _iteratorRemainingCount;
	}

	public void
	setRequestID(String _requestID)
	{
		requestID = _requestID;
	}

	public void
	setRetCount(int _retCount)
	{
		retCount = _retCount;
	}

	public void
	setStatusCode(String _statusCode)
	{
		statusCode = _statusCode;
	}

	public String
	getStatusCode()
	{
		return statusCode;
	}

	public void
	setStatusMessage(String _statusMessage)
	{
		statusMessage = _statusMessage;
	}

	public String
	getStatusMessage()
	{
		return statusMessage;
	}

	public void
	setStatusSeverity(String _statusSeverity)
	{
		statusSeverity = _statusSeverity;
	}

	public String
	getStatusSeverity()
	{
		return statusSeverity ;
	}

}
