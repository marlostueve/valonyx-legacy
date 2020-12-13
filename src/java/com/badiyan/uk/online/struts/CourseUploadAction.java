package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;

import com.badiyan.uk.beans.*;

import com.badiyan.uk.conformance.aicc.rte.datamodel.*;
import com.badiyan.uk.conformance.aicc.rte.server.*;

import com.badiyan.uk.conformance.scorm.cam.server.*;
import com.badiyan.uk.conformance.scorm.rte.server.*;

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
import org.apache.struts.upload.FormFile;

import org.xml.sax.InputSource;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
CourseUploadAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseUploadAction");


		ActionErrors errors = new ActionErrors();

		CourseBean courseBean = null;
		UKOnlineLoginBean loginBean = null;
		AICCCourseUploadManager aicc_upload_manager = null;
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

				courseBean = (CourseBean)session.getAttribute("adminCourse");
				if (courseBean.isSCORMCompliant())
				{
				    courseBean = SCORMCourseBean.getCourse(courseBean.getId());
				    session.setAttribute("adminCourse", courseBean);
				}

				aicc_upload_manager = (AICCCourseUploadManager)session.getAttribute("aicc_upload_manager");
			}
			else
				return (_mapping.findForward("session_expired"));


			String destination_file_path = null;
			String filename = null;

			String realPath = CUBean.getProperty("cu.realPath");
			String coursesFolder = CUBean.getProperty("cu.coursesFolder");
			if (coursesFolder == null)
				throw new IllegalValueException("Property not defined: cu.coursesFolder");

			CUBean.createDirectory(realPath, coursesFolder + courseBean.getIdString());
				// ensure that an upload directory exists for this course

			String courseFolder = realPath + coursesFolder + courseBean.getIdString() + "/";


			CourseUploadForm fileForm = (CourseUploadForm)_form;
			FormFile uploadedFile = fileForm.getUploadInput();

			System.out.println("++++++++++++++ - " + uploadedFile.getFileName());

			filename = uploadedFile.getFileName();
			InputStream uploadInStream = uploadedFile.getInputStream();

			int c = 0;
			c = uploadInStream.read();
			if (c != -1)
			{
				destination_file_path = courseFolder + filename;
				System.out.println("destination_file_path >" + destination_file_path);
				FileOutputStream fOut = new FileOutputStream(destination_file_path);
				while (c != -1)
				{
					fOut.write(c);
					c = uploadInStream.read();
				}
				fOut.flush();
				fOut.close();
			}
			else
				throw new IllegalValueException("Cannot find specified file.");

			/*
			if (filename.indexOf(".doc") != -1)
				courseBean.setResourceType(ResourceBean.WORD_RESOURCE_TYPE);
			else if (filename.indexOf(".ppt") != -1)
				courseBean.setResourceType(ResourceBean.POWERPOINT_RESOURCE_TYPE);
			else if (filename.indexOf(".pdf") != -1)
				courseBean.setResourceType(ResourceBean.ACROBAT_RESOURCE_TYPE);

			courseBean.setUrl(destination_file_path);
			courseBean.save();
			 */

			boolean aicc_related_file_added = false;

			try
			{
				String crs_file_entry_name = null;
				String au_file_entry_name = null;
				String des_file_entry_name = null;
				String cst_file_entry_name = null;

				ZipFile zip = new ZipFile(destination_file_path);
				Enumeration entries = zip.entries();
				while (entries.hasMoreElements())
				{
					ZipEntry entry = (ZipEntry)entries.nextElement();
					//String entry_name = entry.getName().toLowerCase();
					String entry_name = entry.getName();

					if(entry.isDirectory())
						CUBean.createDirectory(courseFolder, entry_name);
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
						courseBean.addFile(entry_name);
						if (aicc_upload_manager.isAICCRelatedFile(entry_name))
							aicc_related_file_added = true;
					}


				}
				zip.close();
				File zip_file = new File(destination_file_path);
				zip_file.delete();


			}
			catch (ZipException zip_exception)
			{
				// assuming at this point that a zip file was not uploaded

				courseBean.addFile(filename);
				aicc_related_file_added = aicc_upload_manager.isAICCRelatedFile(filename);
			}

			if (courseBean.isAICCCompliant())
			{

				System.out.println("");
				System.out.println("");
				System.out.println("");
				System.out.println("DOING AICC STUFF");

				//AICCCourseBean aicc_course = (AICCCourseBean)courseBean;

				//if (aicc_related_file_added)
					aicc_upload_manager.parse();


				System.out.println("DONE SETTING UP AICC STUFF");
			}
			else if (courseBean.isSCORMCompliant())
			{


			    System.out.println("");
				System.out.println("");
				System.out.println("");
				System.out.println("DOING SCORM1.2 STUFF");

				// see

				Iterator file_itr = courseBean.getFiles().iterator();
				while (file_itr.hasNext())
				{
					CourseFile course_file = (CourseFile)file_itr.next();
					String url = course_file.getUrl();
					System.out.println("courseFolder >" + courseFolder + ", url >" + url);

					if (url.endsWith("imsmanifest.xml"))
					{
						IMSManifestParser parser = new IMSManifestParser();
						parser.parse((SCORMCourseBean)courseBean, loginBean.getPerson(), courseFolder, url);
					}
				}

			}
			else
			{

			}

			courseBean.index();

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

	/*
	private void
	createFile(InputStream in, OutputStream out)
		throws IOException
	{
		byte[] buffer = new byte[1024];
		int len;

		while((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	private void
	createDirectory(String _base_directory, String _path_to_create)
	{
		String path = null;
		StringTokenizer tokenizer = new StringTokenizer(_path_to_create, "\\/");
		while (tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if (path == null)
				path = token;
			else
				path += File.separatorChar + token;

			if (_base_directory.endsWith("/") || _base_directory.endsWith("\\"))
				(new File(_base_directory + path)).mkdir();
			else
				(new File(_base_directory + File.separatorChar + path)).mkdir();
		}
	}
	 */

	/*
	private InputSource setUpInputSource(String fileName)
	{
		InputSource is = new InputSource();
		is = setupFileSource(fileName);
		return is;
	}

	private InputSource setupFileSource(String filename)
	{
		try
		{
			java.io.File xmlFile = new java.io.File( filename );
			if ( xmlFile.isFile() )
			{
				FileReader fr = new FileReader( xmlFile );
				InputSource is = new InputSource(fr);
				return is;
			}
			else
			{
			}
		}
		catch ( NullPointerException  npe )
		{
			System.out.println( "Null pointer exception" + npe);
		}
		catch ( SecurityException se )
		{
			System.out.println( "Security Exception" + se);
		}
		catch ( FileNotFoundException fnfe )
		{
			System.out.println("File Not Found Exception" + fnfe);
		}
		return new InputSource();
	}
	 */
}