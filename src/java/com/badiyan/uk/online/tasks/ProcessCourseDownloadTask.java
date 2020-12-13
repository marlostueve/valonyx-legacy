/*
 * ProcessCourseDownloadTask.java
 *
 * Created on March 9, 2007, 11:10 AM
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

public class
ProcessCourseDownloadTask
    extends SlaveLMSTask
{
    // INSTANCE VARIABLES

    private ProcessCourseDownloadServlet parent = null;
    private Vector global_messages = null;
    private boolean update;
    private UKOnlineLoginBean loginBean;
    private CourseBean courseBean;

    // CONSTRUCTORS

    public
    ProcessCourseDownloadTask(ProcessCourseDownloadServlet _parent, Vector _messages, boolean _update, UKOnlineLoginBean _loginBean, CourseBean _course)
	throws TorqueException
    {
	parent = _parent;
	global_messages = _messages;
	update = _update;
	loginBean = _loginBean;
	courseBean = _course;
    }

    // INSTANCE METHODS

    public void
    run()
    {
	System.out.println("run() invoked in ProcessCourseDownloadTask");

	try
	{
	    //<status><![CDATA[xxx]]></status>
	    //global_messages.addElement("<status><![CDATA[Downloading Global LMS Data]]></status>");

	    global_messages.addElement("<status><![CDATA[" + (update ? "Updating" : "Downloading") + " Activity...]]></status>");

	    boolean in_audience = false;
	    Iterator audienceItr = courseBean.getAudiences().iterator();
	    while (audienceItr.hasNext())
	    {
		CourseAudienceBean course_audience = (CourseAudienceBean)audienceItr.next();
		AudienceBean audience = course_audience.getAudience();
		if (audience.contains(loginBean.getPerson()))
		{
		    in_audience = true;
		    break;
		}
	    }
	    if (!in_audience)
		throw new Exception("Cannot " + (update ? "update" : "download") + ".  You're not in the audience for this course.");

	    String realPath = CUBean.getProperty("cu.realPath");
	    String coursesFolder = CUBean.getProperty("cu.coursesFolder");

	    CUBean.createDirectory(realPath, coursesFolder + courseBean.getIdString());
		    // ensure that an upload directory exists for this course
	    String courseFolder = realPath + coursesFolder + courseBean.getIdString() + "/";

	    Iterator itr = courseBean.getFiles().iterator();
	    int i = 0;
	    while (itr.hasNext())
	    {
		CourseFile course_file = (CourseFile)itr.next();
		boolean fetch_file = false;
		if (update)
		    fetch_file = courseBean.isFileOutOfDate(course_file);
		else
		    fetch_file = courseBean.isFileMissing(course_file);
		if (fetch_file)
		{
		    i++;
		    String course_url_string = course_file.getUrl();
		    int folder_index = course_url_string.lastIndexOf('/');
		    if (folder_index == -1)
			course_url_string.lastIndexOf('\\');
		    if (folder_index > -1)
		    {
			// ensure that this folder is created...
			CUBean.createDirectory(courseFolder, course_url_string.substring(0, folder_index));
		    }

		    global_messages.addElement("<download><![CDATA[" + (update ? "Updating" : "Downloading") + " File " + course_url_string + "]]></download>");

		    String url_string = "http://" + CUBean.getProperty("cu.host") + ":" + CUBean.getProperty("cu.port") + "/" + CUBean.getProperty("cu.clientPackageName") + coursesFolder + courseBean.getIdString() + "/" + course_url_string;
		    //URL url = new URL(java.net.URLEncoder.encode(url_string));
		    URL url = new URL(url_string.replace(" ", "%20"));
		    InputStream in_stream = url.openStream();
		    BufferedInputStream in = new BufferedInputStream(in_stream);
		    CUBean.createFile(in, new BufferedOutputStream(new FileOutputStream(courseFolder + course_url_string)));


		}
	    }

	    // download index files also

	    Iterator index_file_names_itr = UKOnlineResourceSearchBean.getIndexFileNames().iterator();
	    while (index_file_names_itr.hasNext())
	    {
		String index_file = (String)index_file_names_itr.next();
		System.out.println("index file >" + index_file);
		String url_string = "http://" + CUBean.getProperty("cu.host") + ":" + CUBean.getProperty("cu.port") + "/" + CUBean.getProperty("cu.clientPackageName") + CUBean.getProperty("cu.resourcesFolder") + "index/" + index_file;
		URL url = new URL(url_string.replace(" ", "%20"));
		InputStream in_stream = url.openStream();
		BufferedInputStream in = new BufferedInputStream(in_stream);
		CUBean.createFile(in, new BufferedOutputStream(new FileOutputStream(CUBean.getProperty("cu.tabletContentPath") + File.separator + "index/" + index_file)));
	    }

	    global_messages.addElement("<status><![CDATA[" + i + " File" + ((i == 1) ? " " : "s ") + (update ? "Updated" : "Downloaded") + "]]></status>");
	    global_messages.addElement("<status><![CDATA[" + (update ? "Update" : "Download") + " Complete]]></status>");
	    global_messages.addElement("<status><![CDATA[Installing Activity]]></status>");
	    global_messages.addElement("<complete><![CDATA[<strong>Installation Complete</strong>]]></complete>");

	}
	catch (Exception ex_1)
	{
	    global_messages.addElement("<error><![CDATA[" + ex_1.getMessage() + "]]></error>");
	    ex_1.printStackTrace();
	}

	parent.notifyComplete();
    }
}