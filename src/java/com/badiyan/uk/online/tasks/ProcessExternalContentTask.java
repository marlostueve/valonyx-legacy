/*
 * ProcessExternalContentTask.java
 *
 * Created on March 28, 2007, 11:14 AM
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
import java.util.zip.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
ProcessExternalContentTask
    extends TimerTask
{
    // INSTANCE VARIABLES

    private ProcessExternalContentServlet parent = null;
    private Vector global_messages = null;

    // CONSTRUCTORS

    public
    ProcessExternalContentTask(ProcessExternalContentServlet _parent, Vector _messages)
	throws TorqueException
    {
	parent = _parent;
	global_messages = _messages;
    }

    // INSTANCE METHODS

    public void
    run()
    {
	//System.out.println("run() invoked in ProcessExternalContentTask");

	/*
	if (global_messages == null)
	    global_messages = new Vector();
	 */

	try
	{
	    if (!CUBean.isMasterServer())
	    {
		global_messages.addElement("<status><![CDATA[Installing Activities]]></status>");

		UKOnlinePersonBean tablet_user = UKOnlinePersonBean.getTabletUser();

		String file_path = "C:\\ESM\\EcoLMS\\content_from_CD";
		boolean content_found = false;

		//String file_path = CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + "index";

		File test_file = new File(file_path);
		if (test_file.isDirectory())
		{
		    String[] file_list = test_file.list();
		    if (file_list.length > 0)
		    {
			content_found = true;
			for (int i = 0; i < file_list.length; i++)
			{
			    //System.out.println("file name >" + file_list[i]);

			    if (file_list[i].indexOf("zip") > -1)
			    {
				try
				{
					ZipFile zip = new ZipFile("C:\\ESM\\EcoLMS\\content_from_CD\\" + file_list[i]);

					// check to see if this is an index file

					if (file_list[i].equals("index.zip"))
					{
					    global_messages.addElement("<status><![CDATA[Installing Index]]></status>");

					    CUBean.createDirectory("C:\\ESM\\EcoLMS\\", "index");
						// ensure that the index directory exists

					    String indexFolder = "C:\\ESM\\EcoLMS\\index\\";

					    Enumeration entries = zip.entries();
					    while (entries.hasMoreElements())
					    {
						    ZipEntry entry = (ZipEntry)entries.nextElement();
						    //String entry_name = entry.getName().toLowerCase();
						    String entry_name = entry.getName();

						    if(entry.isDirectory())
						    {
							    CUBean.createDirectory(indexFolder, entry_name);
						    }
						    else
						    {
							    // determine if this file entry has a directory attached to it

							    int last_back_slash_index = entry_name.lastIndexOf('\\');
							    int last_forward_slash_index = entry_name.lastIndexOf('/');
							    if ((last_back_slash_index != -1) || (last_forward_slash_index != -1))
							    {
								    // a back or forward slash has been detected.  extract the directory

								    int slash_index;
								    if (last_back_slash_index > last_forward_slash_index)
									    slash_index = last_back_slash_index;
								    else
									    slash_index = last_forward_slash_index;

								    String folder_to_create = entry_name.substring(0, slash_index);

								    CUBean.createDirectory(indexFolder, folder_to_create);
							    }

							    CUBean.createFile(zip.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(indexFolder + entry_name)));
						    }


					    }
					    zip.close();
					    File zip_file = new File("C:\\ESM\\EcoLMS\\content_from_CD\\" + file_list[i]);
					    zip_file.delete();
					}
					else
					{

					    String course_id_string = file_list[i].substring(0, file_list[i].indexOf('_'));
					    String courseFolder = "C:\\ESM\\EcoLMS\\courses\\" + course_id_string + "\\";

					    global_messages.addElement("<download><![CDATA[Installing Activity " + file_list[i] + "]]></download>");

					    CUBean.createDirectory("C:\\ESM\\EcoLMS\\", "courses\\" + course_id_string);
						// ensure that an upload directory exists for this course


					    Enumeration entries = zip.entries();
					    while (entries.hasMoreElements())
					    {
						    ZipEntry entry = (ZipEntry)entries.nextElement();
						    //String entry_name = entry.getName().toLowerCase();
						    String entry_name = entry.getName();

						    if(entry.isDirectory())
						    {
							    CUBean.createDirectory(courseFolder, entry_name);
						    }
						    else
						    {
							    // determine if this file entry has a directory attached to it

							    int last_back_slash_index = entry_name.lastIndexOf('\\');
							    int last_forward_slash_index = entry_name.lastIndexOf('/');
							    if ((last_back_slash_index != -1) || (last_forward_slash_index != -1))
							    {
								    // a back or forward slash has been detected.  extract the directory

								    int slash_index;
								    if (last_back_slash_index > last_forward_slash_index)
									    slash_index = last_back_slash_index;
								    else
									    slash_index = last_forward_slash_index;

								    String folder_to_create = entry_name.substring(0, slash_index);

								    CUBean.createDirectory(courseFolder, folder_to_create);
							    }

							    CUBean.createFile(zip.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(courseFolder + entry_name)));
						    }


					    }
					    zip.close();
					    File zip_file = new File("C:\\ESM\\EcoLMS\\content_from_CD\\" + file_list[i]);
					    zip_file.delete();

					    // Read from disk using FileInputStream
					    FileInputStream f_in = new FileInputStream(courseFolder + "sync.data");

					    // Read object using ObjectInputStream
					    ObjectInputStream obj_in = new ObjectInputStream (f_in);

					    // Read an object
					    Object obj = obj_in.readObject();

					    if (obj instanceof Vector)
					    {
						SyncServerIDBean id_obj = SyncServerIDBean.getInstance();
						Date last_update_date = id_obj.getLastUpdateDate();
						Date message_date = null;

						// Cast object to a Vector
						Vector vec = (Vector)obj;

						Iterator itr = vec.iterator();
						while (itr.hasNext())
						{
						    DBSyncMessage message = (DBSyncMessage)itr.next();
						    message_date = message.getTimestamp().getTime();

						    if ((last_update_date == null) || (!message_date.before(last_update_date)))
						    {
							Boolean success = CUBean.dbSync(message);
							//System.out.println("update >" + success.booleanValue());
						    }
						}

						if (message_date != null)
						{
						    id_obj.setLastUpdateDate(message_date);
						    id_obj.save();
						}
					    }

					    // I should be able to enroll in the course now, I guess

					    CourseBean new_course_from_cd_I_guess = CourseBean.getCourse(Integer.parseInt(course_id_string));
					    //tablet_user.enroll(new_course_from_cd_I_guess);

					    EnrollmentBean enrollment = tablet_user.getEnrollment(new_course_from_cd_I_guess, true);

					    //System.out.println("enrollment >" + enrollment);

					    // copy any resources for this course to the resouces folder

					    CUBean.createDirectory("C:\\ESM\\EcoLMS\\", "resources");
						// ensure that the resource directory exists

					    new_course_from_cd_I_guess.invalidate();
					    Iterator resources_itr = new_course_from_cd_I_guess.getResources().iterator();
					    while (resources_itr.hasNext())
					    {
						ResourceBean resource_obj = (ResourceBean)resources_itr.next();

						try
						{
							File resource_source = new File(courseFolder + resource_obj.getURL());
							File resource_destination = new File("C:\\ESM\\EcoLMS\\resources\\" + resource_obj.getURL());
							CUBean.copyFile(resource_source, resource_destination);
							resource_source.delete();
						}
						catch (Exception copy_ex)
						{
						}
					    }
					}




				}
				catch (ZipException zip_exception)
				{
					// assuming at this point that a zip file was not uploaded

					System.out.println("error >" + zip_exception.getMessage());

					global_messages.addElement("<error><![CDATA[Error :" + zip_exception.getMessage() + "]]></error>");
				}
				catch (Exception x)
				{
				    global_messages.addElement("<error><![CDATA[Error : " + x.getMessage() + "]]></error>");

				    x.printStackTrace();

				}
			    }
			}
		    }
		    else
		    {
			//System.out.println("no files found!!");

			global_messages.addElement("<complete><![CDATA[<strong>Installation Complete</strong>]]></complete>");
		    }
		}

		global_messages.addElement("<complete><![CDATA[<strong>Installation Complete</strong>]]></complete>");
	    }
	}
	catch (Exception ex_1)
	{
	    ex_1.printStackTrace();
	}

	parent.notifyComplete();
    }
}