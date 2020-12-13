package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.tasks.*;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
SlaveLMSCommunicatorTask
    extends SlaveLMSTask
{
    // INSTANCE VARIABLES
    
    
    
    // CONSTRUCTORS
    
    public
    SlaveLMSCommunicatorTask()
	throws TorqueException
    {
	
    }
    
    // INSTANCE METHODS
    
    public void
    run()
    {
	
	try
	{
	    // check local log file to see if there's any data that needs to be sent to the master LMS
	    
	    System.out.println("is connected ?>" + this.isConnected());
	    
	    while (this.localDataExists() && this.isConnected())
	    {
		// try to send the local data to the server
		
		System.out.println("local data exists");
		
		// grab the data to be sent
		
		SyncObjectBean obj = SyncObjectBean.getData();
		obj.setState(SyncObjectBean.PROCESSING_STATE);
		obj.save();
		
				/*
				DBSyncClientTask sync_client = new DBSyncClientTask(obj);
				new Thread(sync_client).start();
				 *
				 */
		
		// doing this in a thread may cause a race condition
		
		this.sync(obj);
		
	    }
	    
	}
	catch (Exception x)
	{
	    x.printStackTrace();
	}
    }
    
    private boolean
    localDataExists()
    throws TorqueException
    {
	// check the SyncObject table to see if data exists that needs syncing with the server
	
	return SyncObjectBean.dataExists();
    }
    
    private void
    sync(SyncObjectBean _sync_object)
    {
	try
	{
	    String servletLocation = "http://216.139.232.124:8080/uk-online/DBSyncServlet.html";
	    
	    URL myServlet = new URL(servletLocation);
	    URLConnection servletConnection = myServlet.openConnection();
	    
	    servletConnection.setDoInput(true);
	    servletConnection.setDoOutput(true);
	    servletConnection.setUseCaches(false);
	    servletConnection.setRequestProperty("Content-type","application/x-java-serialized-object");
	    
	    ObjectOutputStream obj_out = new ObjectOutputStream(servletConnection.getOutputStream());
	    
	    System.out.println("PROCESSING SyncObjectBean >" + _sync_object.getId());
	    DBSyncMessage message = (DBSyncMessage)_sync_object.getObjectData();
	    obj_out.writeObject(message);
	    obj_out.flush();
	    
	    // wait for a response object from the server
	    
	    ObjectInputStream obj_in = new ObjectInputStream(servletConnection.getInputStream());
	    Boolean success = (Boolean)obj_in.readObject();
	    
	    obj_in.close();
	    obj_out.close();
	    
	    if (success.booleanValue())
	    {
		// apparently the sync was a success.  delete from sync message table
		
		SyncObjectBean.delete(_sync_object);
	    }
	    else
	    {
		_sync_object.setState(SyncObjectBean.FAILED_STATE);
		_sync_object.save();
	    }
	}
	catch (ClassNotFoundException x)
	{
	    x.printStackTrace();
	}
	catch (IOException x)
	{
	    x.printStackTrace();
	}
	catch (TorqueException x)
	{
	    x.printStackTrace();
	}
	catch (Exception x)
	{
	    x.printStackTrace();
	}
    }
}