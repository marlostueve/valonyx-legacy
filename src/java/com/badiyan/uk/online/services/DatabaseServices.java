package com.badiyan.uk.mtp.services;

import org.apache.torque.TorqueException;
import org.apache.torque.Torque;

import java.sql.Connection;
import java.sql.Statement;

import com.badiyan.uk.exceptions.IllegalValueException;

/**
 * Provides database connection services.
 * These are currently primarily wrappers for Torque methods.
 *
 * @author Randy Marchessault
 * $Id: DatabaseServices.java,v 1.1 2007/02/23 23:39:39 marlo Exp $
 */
public class DatabaseServices {

    /**
     * Provides a database connection based on a property name.
     *
     * For example, if Torque.properties contains:
     *      torque.dsfactory.ep.factory=org.apache.torque.dsfactory.SharedPoolDataSourceFactory
     * then this method would be called with "ep" as the databaseName
     *
     * @param databaseName name of database
     * @return database Connection
     * @throws TorqueException if Torque throws an exception getting the connection
     * @throws IllegalValueException if databaseName is null or empty String
     */
    public synchronized static Connection
	getConnection(String databaseName)
		throws TorqueException, IllegalValueException
	{
        if (databaseName == null || databaseName.length() == 0)
            throw new IllegalValueException("Must provide a valid databaseName.");
        return (Torque.getConnection(databaseName));
		//return Torque.getConnection();
    }

	public synchronized static Connection
	getConnection()
		throws TorqueException, IllegalValueException
	{
		return Torque.getConnection();
    }

    /**
     * Closes the specified database Connection, returning to pool if appropriate.
     * Does nothing if Connection is null.
     *
     * @param con the database connection to close
     */
    public synchronized static void closeConnection(Connection con) {
        if (con !=null) {
            Torque.closeConnection(con);
        }
    }

    /**
     * Closes the specified database Statement.
     * Does nothing if Statement is null.
     *
     * @param stmt the Statement to close
     */
    public synchronized static void closeStatement(Statement stmt) {
        if (stmt !=null) {
            try {
                stmt.close();
            } catch (Exception ignore) {}
        }
    }

}
