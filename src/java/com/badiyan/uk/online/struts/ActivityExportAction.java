/*
 * ActivityExportAction.java
 *
 * Created on March 27, 2007, 3:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.zip.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
ActivityExportAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in ActivityExportAction");

		ActionErrors errors = new ActionErrors();

		ResourceBean resourceBean = null;
		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
					return (_mapping.findForward("session_expired"));
				else
				{
					try
					{
						UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) && !person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
							return (_mapping.findForward("session_expired"));
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				resourceBean = (ResourceBean)session.getAttribute("adminResource");
			}
			else
				return (_mapping.findForward("session_expired"));

			Integer[] titleSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "titleSelect");

			Vector courses = new Vector();
			if (titleSelect != null)
			{
			    for (int i = 0; i < titleSelect.length; i++)
			    {
				courses.addElement(CourseBean.getCourse(titleSelect[i]));
			    }
			}

			StringBuffer results = new StringBuffer();

			String realPath = CUBean.getProperty("cu.realPath");
			String coursesFolder = CUBean.getProperty("cu.coursesFolder");
			if (coursesFolder == null)
			    throw new IllegalValueException("Property not defined: cu.coursesFolder");


			Iterator itr = courses.iterator();
			while (itr.hasNext())
			{
			    CourseBean course_obj = (CourseBean)itr.next();

			    CUBean.createDirectory(realPath, coursesFolder + course_obj.getIdString());
				// ensure that an upload directory exists for this course

			    Vector cd_sync_objects = SyncObjectBean.getAllData();
				// get all globals

			    String sync_file_name = realPath + coursesFolder + course_obj.getIdString() + "/sync.data";
			    File sync_file = new File(sync_file_name);
			    FileOutputStream outStream = new FileOutputStream(sync_file);
			    ObjectOutputStream objStream = new ObjectOutputStream(outStream);
			    objStream.writeObject(cd_sync_objects);
			    objStream.flush();
			    outStream.flush();
			    objStream.close();
			    outStream.close();

			    Iterator resource_itr = course_obj.getResources().iterator();
			    while (resource_itr.hasNext())
			    {
				ResourceBean resource_obj = (ResourceBean)resource_itr.next();
				String resource_url = resource_obj.getURL();
				if (resource_url != null)
				{
				    File resource_source = new File(realPath + CUBean.getProperty("cu.resourcesFolder") + resource_url);
				    File resource_destination = new File(realPath + coursesFolder + course_obj.getIdString() + "/" + resource_url);
				    CUBean.copyFile(resource_source, resource_destination);
				}
			    }

			    //create a ZipOutputStream to zip the data to

			    String course_label = course_obj.getLabel().replace('#', '_');


			    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(realPath + coursesFolder + File.separator + course_obj.getIdString() + "_" + course_label + ".zip"));
			    //assuming that there is a directory named inFolder (If there
			    //isn't create one) in the same directory as the one the code runs from,
			    //call the zipDir method
			    CUBean.zipDir(realPath + coursesFolder + course_obj.getIdString(), zos, File.separator + course_obj.getIdString() + File.separator);
			    //CUBean.zipDir(course_obj.getIdString(), zos);
			    //close the stream
			    zos.close();

			    results.append("<a href=\"../courses/" + course_obj.getIdString() + "_" + course_label + ".zip\">" + course_obj.getIdString() + "_" + course_label + ".zip</a><br />");

			}

			// make the index files available also

			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(realPath + CUBean.getProperty("cu.resourcesFolder") + "index.zip"));
			CUBean.zipDir(realPath + CUBean.getProperty("cu.resourcesFolder") + "index", zos, File.separator + "index" + File.separator);
			zos.close();

			results.append("<a href=\"../resources/index.zip\">index.zip</a><br />");

			session.setAttribute("export_results", results);

		}
        catch (IllegalValueException x)
        {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));

		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty())
		{
			saveErrors(_request, errors);
			return _mapping.getInputForward();
		}

		// Remove the obsolete form bean
		if (_mapping.getAttribute() != null)
		{
            if ("request".equals(_mapping.getScope()))
                _request.removeAttribute(_mapping.getAttribute());
            else
                session.removeAttribute(_mapping.getAttribute());
        }

		// Forward control to the specified success URI
		return (_mapping.findForward("success"));
	}
}
