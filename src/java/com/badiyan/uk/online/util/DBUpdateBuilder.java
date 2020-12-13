package com.badiyan.uk.online.util;

import java.math.*;
import java.text.*;
import java.util.*;

public class
DBUpdateBuilder
{
	// CLASS METHODS

	public static String
	verifySQLString(String _string)
	{
		/*
		int apostropheIndex = -1;
		if (_string != null)
			apostropheIndex = _string.indexOf(39);
		if (apostropheIndex != -1)
			return _string.substring(0, apostropheIndex) + "\\" + _string.substring(apostropheIndex);
		else
			return _string;
		*/

		if (_string != null)
		{
			int apostropheIndex = -1;
			int index = 0;

			apostropheIndex = _string.indexOf(39, index);
			if (apostropheIndex != -1)
			{
				StringBuffer buf = new StringBuffer(_string.length() + 10);
				while(apostropheIndex != -1)
				{
					buf.append(_string.substring(index, apostropheIndex));
					buf.append("'");
					index = apostropheIndex;
					apostropheIndex = _string.indexOf(39, index + 1);
				}
				buf.append(_string.substring(index));

				return buf.toString();
			}
			return _string;
		}

		return null;
	}

	// INSTANCE METHODS

	protected String tableName;
	protected Vector whereClauseColumns;
	protected Vector whereClauseValues;

	protected Vector columnNames;
	protected Vector columnValues;

	// CONTRUCTOR

	public
	DBUpdateBuilder(String _tableName)
	{
		tableName = _tableName;

		whereClauseColumns = new Vector();
		whereClauseValues = new Vector();
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

	public void
	addWhereClause(String _columnName, String _columnValue)
	{
		whereClauseColumns.addElement(_columnName);
		whereClauseValues.addElement("'" + verifySQLString(_columnValue) + "'");
	}

	public void
	addWhereClause(String _columnName, int _columnValue)
	{
		whereClauseColumns.addElement(_columnName);
		whereClauseValues.addElement("" + _columnValue);
	}

	public String
	toSQLString()
	{
		StringBuffer buf1 = new StringBuffer(512);

		buf1.append("UPDATE ");
		buf1.append(tableName);
		buf1.append(" SET ");

		int sizer = columnNames.size() - 1;
		for (int i = 0; i < sizer; i++)
		{
			buf1.append((String)columnNames.elementAt(i));
			buf1.append(" = ");
			buf1.append((String)columnValues.elementAt(i));
			buf1.append(", ");
		}

		buf1.append((String)columnNames.elementAt(sizer));
		buf1.append(" = ");
		buf1.append((String)columnValues.elementAt(sizer));

		sizer = whereClauseColumns.size();
		if (sizer > 0)
		{
			buf1.append(" WHERE ");
			buf1.append((String)whereClauseColumns.elementAt(0));
			buf1.append(" = ");
			buf1.append((String)whereClauseValues.elementAt(0));
		}
		for (int i = 1; i < sizer; i++)
		{
			buf1.append(" AND ");
			buf1.append((String)whereClauseColumns.elementAt(i));
			buf1.append(" = ");
			buf1.append((String)whereClauseValues.elementAt(i));
		}

		return buf1.toString();
	}
}