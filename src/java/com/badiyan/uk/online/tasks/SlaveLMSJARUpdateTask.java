/*
 * SlaveLMSJARUpdateTask.java
 *
 * Created on March 6, 2007, 4:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.tasks.*;

import com.badiyan.uk.online.beans.*;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
SlaveLMSJARUpdateTask
    extends SlaveLMSTask
{
    // INSTANCE VARIABLES

    UKOnlinePersonBean tablet_user = null;

    // CONSTRUCTORS

    public
    SlaveLMSJARUpdateTask()
	throws TorqueException
    {

    }

    // INSTANCE METHODS

    public void
    setTabletUser(UKOnlinePersonBean _user)
    {
	tablet_user = _user;
    }

    public void
    run()
    {

	try
	{
	    // check local log file to see if there's any data that needs to be sent to the master LMS

	    // assume that I'm connected to the server at this point


	    // check to see if the uk-common.jar file is up to date

	    String server_mod_1 = "unknown";
	    String server_mod_2 = "unknown";
	    String server_mod_3 = "unknown";
	    String update_str = tablet_user.getUKCommonLastUpdateDate();

	    if (update_str != null)
	    {
		StringTokenizer tokenizer = new StringTokenizer(update_str, "|");
		if (tokenizer.hasMoreTokens())
		    server_mod_1 = tokenizer.nextToken();
		if (tokenizer.hasMoreTokens())
		    server_mod_2 = tokenizer.nextToken();
		if (tokenizer.hasMoreTokens())
		    server_mod_3 = tokenizer.nextToken();
	    }

	    server_mod_1 = this.grabJAR("uk-online.jar", server_mod_1);
	    tablet_user.setUKCommonLastUpdateDate(server_mod_1 + "|" + server_mod_2 + "|" + server_mod_3);
	    tablet_user.save();
	    server_mod_2 = this.grabJAR("uk-common.jar", server_mod_2);
	    tablet_user.setUKCommonLastUpdateDate(server_mod_1 + "|" + server_mod_2 + "|" + server_mod_3);
	    tablet_user.save();
	    server_mod_3 = this.grabJAR("uk-db.jar", server_mod_3);
	    tablet_user.setUKCommonLastUpdateDate(server_mod_1 + "|" + server_mod_2 + "|" + server_mod_3);
	    tablet_user.save();
	}
	catch (Exception x)
	{
	    x.printStackTrace();
	}
    }

    private String
    grabJAR(String _jarfile, String _last_mod_from_person)
    {
	System.out.println("grabJAR() invoked - "  + _jarfile + ", " + _last_mod_from_person);
	try
	{
	    File local_uk_common_jar_file = new File(CUBean.getProperty("cu.realPath") + "/WEB-INF/lib/" + _jarfile);
	    String server_mod = CUBean.getJARLastModified(_jarfile);
	    String local_mod = local_uk_common_jar_file.lastModified() + "";

	    System.out.println("server_mod >" + server_mod);
	    System.out.println("local_mod >" + local_mod);

	    boolean needs_update = ((!server_mod.equals(local_mod)) && (!server_mod.equals(_last_mod_from_person)));

	    System.out.println("needs_update_1 >" + needs_update);

	    if (needs_update)
	    {
		// download the latest version from the server

		String url_string = "http://" + CUBean.getProperty("cu.host") + ":" + CUBean.getProperty("cu.port") + "/" + CUBean.getProperty("cu.clientPackageName") + CUBean.getProperty("cu.resourcesFolder") + _jarfile;
		URL url = new URL(url_string.replace(" ", "%20"));
		InputStream in_stream = url.openStream();
		BufferedInputStream in = new BufferedInputStream(in_stream);
		CUBean.createFile(in, new BufferedOutputStream(new FileOutputStream(CUBean.getProperty("cu.realPath") + "/WEB-INF/lib/" + _jarfile)));

		/*
		tablet_user.setUKCommonLastUpdateDate(server_mod);
		tablet_user.save();
		 */

		//throw new IllegalValueException("Files have been updated.  You must restart your computer before you can synchronize.");
	    }

	    return server_mod;
	}
	catch (Exception x)
	{
	    x.printStackTrace();
	}

	return null;
    }
}