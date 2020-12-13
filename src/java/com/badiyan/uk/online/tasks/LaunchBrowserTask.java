/*
 * LaunchBrowserTask.java
 *
 * Created on March 12, 2007, 9:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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

import com.centerkey.utils.*;

public class
LaunchBrowserTask
    extends TimerTask
{
    // INSTANCE VARIABLES

    private boolean exit = false;

    // CONSTRUCTORS

    public
    LaunchBrowserTask(boolean _exit)
	throws TorqueException
    {
	exit = _exit;
    }

    // INSTANCE METHODS

    public void
    run()
    {
	System.out.println("run() invoked in LaunchBrowserTask");

	try
	{
	    boolean ready_to_open_browser = false;
	    while (!ready_to_open_browser)
	    {

		try
		{
		    InputStream i_stream = (new URL("http://localhost:" + CUBean.getProperty("cu.localPort") + "/" + CUBean.getProperty("cu.clientPackageName") + "/index.jsp")).openStream();
		    i_stream.close();

		    ready_to_open_browser = true;
		}
		catch (Exception x)
		{
		    Thread.sleep(500);
		}
	    }

	    BareBonesBrowserLaunch.openURL("http://localhost:" + CUBean.getProperty("cu.localPort") + "/" + CUBean.getProperty("cu.clientPackageName") + "/index.jsp");
	    if (exit)
		System.exit(0);
	}
	catch (Exception ex_1)
	{
	    ex_1.printStackTrace();
	}
    }
}