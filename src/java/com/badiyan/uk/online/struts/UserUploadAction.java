/*
 * UserUploadAction.java
 *
 * Created on March 20, 2007, 9:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

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

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.hssf.usermodel.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
UserUploadAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in UserUploadAction");


	ActionErrors errors = new ActionErrors();

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
	    }
	    else
		return (_mapping.findForward("session_expired"));

	    CompanyBean ecolab = CompanyBean.getInstance();


	    String destination_file_path = null;
	    String filename = null;

	    String realPath = CUBean.getProperty("cu.realPath");
	    String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
	    if (resourcesFolder == null)
		throw new IllegalValueException("Property not defined: cu.resourcesFolder");

	    CUBean.createDirectory(realPath, resourcesFolder + "user-upload-data");
	    // ensure that an upload directory exists for this course

	    String destination_folder = realPath + resourcesFolder + "user-upload-data/";


	    UserUploadForm fileForm = (UserUploadForm)_form;
	    FormFile uploadedFile = fileForm.getUploadInput();

	    System.out.println("++++++++++++++ - " + uploadedFile.getFileName());

	    filename = uploadedFile.getFileName();
	    InputStream uploadInStream = uploadedFile.getInputStream();

	    int c = 0;
	    c = uploadInStream.read();
	    if (c != -1)
	    {
		destination_file_path = destination_folder + filename;
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

	    UKOnlineAccessManager am = (UKOnlineAccessManager)UKOnlineAccessManager.getInstance();

	    POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(destination_file_path));
	    HSSFWorkbook wb = new HSSFWorkbook(fs);

	    int sheet_index = 0;

	    StringBuffer results = new StringBuffer();

	    HSSFSheet sheet = wb.getSheetAt(sheet_index);
	    int rows_modified = 0;
	    int row_index;
	    for (row_index = 1; row_index < 99999; row_index++)
	    {

		HSSFRow obj_row = sheet.getRow(row_index);

		if (obj_row == null)
		    break;
		else
		{
		    try
		    {
			HSSFCell first_name_cell = obj_row.getCell((short)0);
			HSSFCell last_name_cell = obj_row.getCell((short)1);
			HSSFCell employee_id_cell = obj_row.getCell((short)2);
			HSSFCell email_cell = obj_row.getCell((short)3);
			HSSFCell user_name_cell = obj_row.getCell((short)4);
			HSSFCell password_cell = obj_row.getCell((short)5);
			HSSFCell user_group_cell = obj_row.getCell((short)6);
			HSSFCell department_cell = obj_row.getCell((short)7);
			HSSFCell job_title_cell = obj_row.getCell((short)8);
			HSSFCell cost_center_cell = obj_row.getCell((short)9);

			String first_name = this.getStringValueFromCell(first_name_cell);
			String last_name = this.getStringValueFromCell(last_name_cell);
			String employee_id = this.getStringValueFromCell(employee_id_cell);
			String email = this.getStringValueFromCell(email_cell);
			String user_name = this.getStringValueFromCell(user_name_cell);
			String password = this.getStringValueFromCell(password_cell);
			String user_group_str = this.getStringValueFromCell(user_group_cell);
			String department_str = this.getStringValueFromCell(department_cell);
			String job_title_str = this.getStringValueFromCell(job_title_cell);
			String cost_center = this.getStringValueFromCell(cost_center_cell);

			System.out.println("");
			System.out.println("row >" + row_index + "<");
			System.out.println("first_name >" + first_name + "<");
			System.out.println("last_name >" + last_name + "<");
			System.out.println("employee_id >" + employee_id + "<");
			System.out.println("email >" + email + "<");
			System.out.println("user_name >" + user_name + "<");
			System.out.println("password >" + password + "<");
			System.out.println("user_group_str >" + user_group_str + "<");
			System.out.println("department_str >" + department_str + "<");
			System.out.println("job_title_str >" + job_title_str + "<");
			System.out.println("cost_center >" + cost_center + "<");



			PersonGroupBean group = null;
			if (user_group_str.equals(""))
			    group = PersonGroupBean.getDefaultPersonGroup(ecolab);
			else
			    group = PersonGroupBean.getPersonGroup(ecolab, user_group_str);

			DepartmentBean department = null;
			if (department_str.equals(""))
			    department = ecolab.getDefaultDepartment();
			else
			    department = DepartmentBean.getDepartment(ecolab, department_str);

			PersonTitleBean title = null;
			if (job_title_str.equals(""))
			    title = PersonTitleBean.getDefaultTitle(ecolab);
			else
			    title = PersonTitleBean.getPersonTitle(ecolab, job_title_str);

			UKOnlinePersonBean adminPerson = null;

			try
			{
			    adminPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(user_name);
			}
			catch (ObjectNotFoundException x)
			{
			    adminPerson = new UKOnlinePersonBean();
			}


			boolean first_name_modified = (first_name.length() > 0);
			boolean last_name_modified = (last_name.length() > 0);
			boolean employee_id_modified = (employee_id.length() > 0);
			boolean email_modified = (email.length() > 0);
			boolean user_name_modified = (user_name.length() > 0);
			boolean password_modified = (password.length() > 0);
			boolean user_group_modified = (user_group_str.length() > 0);
			boolean department_modified = (department_str.length() > 0);
			boolean job_title_modified = (job_title_str.length() > 0);
			boolean cost_center_modified = (cost_center.length() > 0);
			    // assume modified, for now, as long as data was provided

			if (!adminPerson.isNew())
			{
			    first_name_modified = (first_name_modified && !first_name.equals(adminPerson.getFirstNameString()));
			    last_name_modified = (last_name_modified && !last_name.equals(adminPerson.getLastNameString()));
			    employee_id_modified = (employee_id_modified && !employee_id.equals(adminPerson.getEmployeeNumberString()));
			    email_modified = (email_modified && !email.equals(adminPerson.getEmail1String()));
			    user_name_modified = (user_name_modified && !user_name.equals(adminPerson.getUsernameString()));

			    if (password_modified)
			    {
				try
				{
				    am.getUserWithCredentials(adminPerson.getUsernameString(), password);
				    password_modified = false;
				}
				catch (Exception x)
				{
				    // apparently the password is being changed...
				}
			    }

			    user_group_modified = (user_group_modified && !user_group_str.equals(adminPerson.getUserGroupString()));
			    department_modified = (department_modified && !department_str.equals(adminPerson.getDepartmentString()));
			    job_title_modified = (job_title_modified && !job_title_str.equals(adminPerson.getJobTitleString()));
			    cost_center_modified = (cost_center_modified && !cost_center.equals(adminPerson.getCostCenterString()));
			}

			boolean modified = first_name_modified || last_name_modified || employee_id_modified || email_modified || user_name_modified || password_modified || user_group_modified || department_modified || job_title_modified || cost_center_modified;

			if (modified)
			{
			    try
			    {
				Integer.parseInt(employee_id);
			    }
			    catch (Exception x)
			    {
				throw new IllegalValueException("Employee # must be numeric");
			    }

			    rows_modified++;

			    if (user_name_modified)
				adminPerson.setUsername(user_name);
			    if (password_modified)
			    {
				adminPerson.setPassword(password);
				adminPerson.setConfirmPassword(password);
			    }
			    if (first_name_modified)
				adminPerson.setFirstName(first_name);
			    if (last_name_modified)
				adminPerson.setLastName(last_name);
			    if (employee_id_modified)
				adminPerson.setEmployeeId(employee_id);
			    if (email_modified)
				adminPerson.setEmail(email);
			    if (cost_center_modified)
				adminPerson.setCostCenter(cost_center);

			    if (department_modified)
				adminPerson.setDepartment(department);
			    if (job_title_modified)
				adminPerson.setTitle(title);

			    adminPerson.save();

			    if (user_group_modified)
				adminPerson.setGroup(group);

			    if (job_title_modified)
			    {
				// don't remove any other roles that may have been assigned, I guess...

				RoleBean student_role = RoleBean.getRole(RoleBean.STUDENT_ROLE_NAME);
				RoleBean manager_role = RoleBean.getRole(RoleBean.MANAGER_ROLE_NAME);
				RoleBean training_administrator_role = RoleBean.getRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME); //  department activity start/completion
				RoleBean department_administrator_role = RoleBean.getRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME); // department reporting access

				adminPerson.addRole(student_role);

				if (title.getLabel().equals(EcolabPersonTitleBean.TRAINING_MANAGER_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.DEPARTMENT_MANAGER_TITLE_NAME))
				{
				    adminPerson.addRole(manager_role);
				}

				if (title.getLabel().equals(EcolabPersonTitleBean.DEPARTMENT_MANAGER_TITLE_NAME))
				{
				    adminPerson.addRole(department_administrator_role);
				}

				if (title.getLabel().equals(EcolabPersonTitleBean.TRAINING_MANAGER_TITLE_NAME))
				{
				    adminPerson.addRole(training_administrator_role);
				}

				/*
				if (title.getLabel().equals(EcolabPersonTitleBean.TM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.RM_PERSON_TITLE_NAME))
				{
				    adminPerson.addRole(student_role);
				}

				if (title.getLabel().equals(EcolabPersonTitleBean.DM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.RS_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.AM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.ARM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.RVP_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.FTM_PERSON_TITLE_NAME))
				{
				    adminPerson.addRole(manager_role);
				}

				if (title.getLabel().equals(EcolabPersonTitleBean.FTM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.RVP_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.AM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.DM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.ARM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.RS_PERSON_TITLE_NAME))
				{
				    adminPerson.addRole(training_administrator_role);
				}

				if (title.getLabel().equals(EcolabPersonTitleBean.FTM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.AM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.DM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.ARM_PERSON_TITLE_NAME) ||
					title.getLabel().equals(EcolabPersonTitleBean.RS_PERSON_TITLE_NAME))
				{
				    adminPerson.addRole(department_administrator_role);
				}
				 */
			    }

			    results.append(adminPerson.getLabel() + " " + (adminPerson.isNew() ? "imported" : "modified") + " <br />");
			}

		    }
		    catch (Exception x)
		    {
			results.append("Error processing user :" + x.getMessage() + " <br />");
			x.printStackTrace();
		    }
		}


	    }

	    results.append(rows_modified + " user" + ((rows_modified == 1) ? "" : "s")  + " affected");

	    session.setAttribute("upload_results", results);
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

    private String
    getStringValueFromCell(HSSFCell _cell)
    {
	if (_cell == null)
	    return "";
	String str = null;
	if (_cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
	    str = (int)_cell.getNumericCellValue() + "";
	else
	    str = _cell.getStringCellValue();

	return str;
    }
}