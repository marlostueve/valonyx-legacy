/*
 * SlaveLMSUpdateTask.java
 *
 * Created on September 18, 2006, 8:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

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
SlaveLMSUpdateTask
    extends SlaveLMSTask
{
    // INSTANCE VARIABLES
    
    
    
    // CONSTRUCTORS
    
    public
    SlaveLMSUpdateTask()
    throws TorqueException
    {
	
    }
    
    // INSTANCE METHODS
    
    public void
    run()
    {
	try
	{
	    if (this.isConnected())
	    {
		// send this Slave's LMS id to the server.  the master lms will respond with a Vector of objects that were created/updated since the last sync
		
		
		String servletLocation = "http://216.139.232.124:8080/uk-online/DBUpdateServlet.html";
		
		URL myServlet = new URL(servletLocation);
		URLConnection servletConnection = myServlet.openConnection();
		
		servletConnection.setDoInput(true);
		servletConnection.setDoOutput(true);
		servletConnection.setUseCaches(false);
		servletConnection.setRequestProperty("Content-type","application/x-java-serialized-object");
		
		ObjectOutputStream obj_out = new ObjectOutputStream(servletConnection.getOutputStream());
		
		System.out.println("PROCESSING Update >");
		SyncServerIDBean id_obj = SyncServerIDBean.getInstance();
		obj_out.writeObject(new Integer(id_obj.getId()));
		obj_out.flush();
		
		// wait for a response object from the server
		
		ObjectInputStream obj_in = new ObjectInputStream(servletConnection.getInputStream());
		Vector sync_objects = (Vector)obj_in.readObject();
		
		obj_in.close();
		obj_out.close();
		
		System.out.println("vec from server >" + sync_objects.size());
		
		Calendar timestamp = null;
		
		Iterator itr = sync_objects.iterator();
		while (itr.hasNext())
		{
		    DBSyncMessage message = (DBSyncMessage)itr.next();
		    Boolean success = CUBean.dbSync(message);
		    System.out.println("success >" + success.booleanValue());
		    
		    // TODO: what should I do here if this fails?
		    
		    timestamp = message.getTimestamp();
		}
		
		
		
		
		if (sync_objects.size() > 0)
		{
		    System.out.println("last update date >" + CUBean.getUserDateString(timestamp.getTime()));
		    
		    servletLocation = "http://216.139.232.124:8080/uk-online/DBUpdateConfirmServlet.html";
		    
		    myServlet = new URL(servletLocation);
		    servletConnection = myServlet.openConnection();
		    
		    servletConnection.setDoInput(true);
		    servletConnection.setDoOutput(true);
		    servletConnection.setUseCaches(false);
		    servletConnection.setRequestProperty("Content-type","application/x-java-serialized-object");
		    
		    obj_out = new ObjectOutputStream(servletConnection.getOutputStream());
		    
		    id_obj.setLastUpdateDate(timestamp.getTime());
		    
		    System.out.println("WRITE OBJ >" + id_obj);
		    obj_out.writeObject(id_obj);
		    obj_out.flush();
		    
		    obj_in = new ObjectInputStream(servletConnection.getInputStream());
		    Boolean confirm = (Boolean)obj_in.readObject();
		    
		    obj_in.close();
		    obj_out.close();
		}
		
	    }
	    
	}
	catch (Exception x)
	{
	    x.printStackTrace();
	}
    }
    
}