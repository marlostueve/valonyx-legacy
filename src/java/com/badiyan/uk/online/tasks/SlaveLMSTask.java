/*
 * SlaveLMSTask.java
 *
 * Created on September 28, 2006, 9:48 AM
 *
 */

package com.badiyan.uk.online.tasks;

import java.net.*;
import java.io.*;
import java.util.*;

import com.badiyan.uk.beans.*;

/**
 *
 * @author marlo
 */
public abstract class
SlaveLMSTask
	extends TimerTask
{

	protected boolean
	isConnected()
	{
		try
		{
			InputStream i_stream = (new URL("http://" + CUBean.getProperty("cu.host") + ":" + CUBean.getProperty("cu.port") + "/uk-online/DBSyncServlet.html")).openStream();
			i_stream.close();

			return true;
		}
		catch (Exception x)
		{
			//x.printStackTrace();
		}

		return false;
	}

}
