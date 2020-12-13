/*
 * ProcessSyncObjectTask.java
 *
 * Created on March 8, 2007, 8:17 PM
 *
 */

package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.tasks.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.servlets.*;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
ProcessSyncObjectTask
    extends SlaveLMSTask
{
    // INSTANCE VARIABLES
    
    private ProcessSyncObjectsServlet parent = null;
    private Vector global_messages = null;
    
    // CONSTRUCTORS
    
    public
    ProcessSyncObjectTask(ProcessSyncObjectsServlet _parent, Vector _messages)
	throws TorqueException
    {
	parent = _parent;
	global_messages = _messages;
    }
    
    // INSTANCE METHODS
    
    public void
    run()
    {
	System.out.println("run() invoked in ProcessSyncObjectTask");
	
	try
	{
	    //<status><![CDATA[xxx]]></status>
	    global_messages.addElement("<status><![CDATA[Downloading Global LMS Data]]></status>");
	    Vector global_syn_objects = CUBean.receiveSyncObjectsFromServer();
	    global_messages.addElement("<status><![CDATA[Download Complete]]></status>");
	    global_messages.addElement("<status><![CDATA[" + global_syn_objects.size() + " Object" + ((global_syn_objects.size() == 1) ? "" : "s") + " Downloaded]]></status>");
	    
	    try
	    {
			global_messages.addElement("<status><![CDATA[Updating Global LMS Data...]]></status>");
			CUBean.receiveSyncObjectsFromServerUpdate(global_syn_objects);
			global_messages.addElement("<complete><![CDATA[<strong>Global Update Complete</strong>]]></complete>");

			UKOnlinePersonBean tablet_user = UKOnlinePersonBean.getTabletUser();
			//if (tablet_user.isServerApproved())
			//{
		    try
		    {
				global_messages.addElement("<personal_status><![CDATA[Downloading Personal LMS Data...]]></personal_status>");
				Vector personal_sync_objects = CUBean.receivePersonSyncObjectsFromServer(tablet_user);
				global_messages.addElement("<personal_status><![CDATA[Download Complete]]></personal_status>");
				global_messages.addElement("<personal_status><![CDATA[" + personal_sync_objects.size() + " Object" + ((personal_sync_objects.size() == 1) ? "" : "s") + " Downloaded]]></personal_status>");

				try
				{
					global_messages.addElement("<personal_status><![CDATA[Updating Personal LMS Data...]]></personal_status>");
					CUBean.receivePersonSyncObjectsFromServerUpdate(tablet_user, personal_sync_objects);
					global_messages.addElement("<personal_status><![CDATA[Update Complete]]></personal_status>");

					try
					{
					global_messages.addElement("<personal_status><![CDATA[Uploading Personal LMS Data...]]></personal_status>");
					while (SyncObjectBean.dataExists())
					{
						// try to send the local data to the server

						System.out.println("local data exists");

						// grab the data to be sent

						SyncObjectBean obj = SyncObjectBean.getData();
						obj.setState(SyncObjectBean.PROCESSING_STATE);
						obj.save();
						CUBean.sendSyncObjectToServer(obj);
					}
					global_messages.addElement("<personal_status><![CDATA[Upload Complete]]></personal_status>");
					global_messages.addElement("<personal_complete><![CDATA[<strong>Personal Update Complete</strong>]]></personal_complete>");
					}
					catch (Exception ex_5)
					{
					global_messages.addElement("<personal_error><![CDATA[" + ex_5.getMessage() + "]]></personal_error>");
					ex_5.printStackTrace();
					}
				}
				catch (Exception ex_4)
				{
					global_messages.addElement("<personal_error><![CDATA[" + ex_4.getMessage() + "]]></personal_error>");
					ex_4.printStackTrace();
				}
		    }
		    catch(Exception ex_3)
		    {
				global_messages.addElement("<personal_error><![CDATA[" + ex_3.getMessage() + "]]></personal_error>");
				ex_3.printStackTrace();
		    }
		//}
	    }
	    catch (Exception ex_2)
	    {
			global_messages.addElement("<error><![CDATA[" + ex_2.getMessage() + "]]></error>");
			ex_2.printStackTrace();
	    }
	    
	}
	catch (Exception ex_1)
	{
	    global_messages.addElement("<error><![CDATA[" + ex_1.getMessage() + "]]></error>");
	    ex_1.printStackTrace();
	}
	
	parent.notifyComplete();
    }
}