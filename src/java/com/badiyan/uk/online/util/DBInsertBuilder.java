package com.badiyan.uk.online.util;

import java.math.*;
import java.text.*;
import java.util.*;

public class
DBInsertBuilder
{
	// INSTANCE METHODS

	protected String tableName;

	protected Vector columnNames;
	protected Vector columnValues;

	// CONTRUCTOR

	public
	DBInsertBuilder(String _tableName)
	{
		tableName = _tableName;

		columnNames = new Vector();
		columnValues = new Vector();
	}

	// INSTANCE METHODS

	public void
	addColumnValue(String _columnName, String _columnValue)
	{
		columnNames.addElement(_columnName);
		columnValues.addElement("'" + DBUpdateBuilder.verifySQLString(_columnValue) + "'");
	}

	public void
	addColumnValue(String _columnName, int _columnValue)
	{
		columnNames.addElement(_columnName);
		columnValues.addElement("" + _columnValue);
	}

	public void
	addColumnValue(String _columnName, boolean _columnValue)
	{
		columnNames.addElement(_columnName);

		if (_columnValue)
			columnValues.addElement("1");
		else
			columnValues.addElement("0");
	}

	public void
	addColumnValue(String _columnName, Date _columnValue)
	{
		if (_columnValue != null)
		{
			columnNames.addElement(_columnName);

			SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateTimeString = dtFormat.format(_columnValue);

			columnValues.addElement("'" + dateTimeString + "'");
		}
	}

	public void
	addColumnValue(String _columnName, BigDecimal _columnValue)
	{
		columnNames.addElement(_columnName);
		columnValues.addElement("" + _columnValue);
	}

	public String
	toSQLString()
	{
		StringBuffer buf1 = new StringBuffer(256);
		StringBuffer buf2 = new StringBuffer(256);

		buf1.append("INSERT INTO ");
		buf1.append(tableName);
		buf1.append(" (");

		int sizer = columnNames.size() - 1;
		for (int i = 0; i < sizer; i++)
		{
			buf1.append((String)columnNames.elementAt(i));
			buf1.append(", ");
			buf2.append((String)columnValues.elementAt(i));
			buf2.append(", ");
		}

		buf1.append((String)columnNames.elementAt(sizer));
		buf1.append(") VALUES (");
		buf2.append((String)columnValues.elementAt(sizer));
		buf2.append(");");

		return buf1.toString() + buf2.toString();
	}
}
