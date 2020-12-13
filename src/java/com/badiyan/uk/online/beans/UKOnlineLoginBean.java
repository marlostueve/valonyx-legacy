package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.tasks.*;
import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.apache.torque.*;
import com.badiyan.uk.beans.*;
import org.apache.torque.util.Criteria;

import com.badiyan.corpu.manager.*;

import java.lang.reflect.*;

/**
 *
 * @author  marlo
 */
public class UKOnlineLoginBean extends LoginBean implements java.io.Serializable
{
	// CLASS METHODS

	public static void
	doLoginStuff(String _firstname,
			String _lastname,
			String _employeeid,
			String _email,
			String _usergroup,
			String _subgroup,
			String _branch,
			String _region,
			String _jobtitle)
		throws TorqueException, ObjectNotFoundException,
				UniqueObjectNotFoundException, ObjectAlreadyExistsException,
				IllegalValueException, Exception
	{
		// ensure that the passed region and branch exist and are assigned properly
		DepartmentBean region = null;
		DepartmentBean branch = null;
		//region = DepartmentBean.getDepartment(_region, "PARENT NOT FOUND", DepartmentBean.REGION_DEPARTMENT_TYPE, true); // create the region if necessary
		//branch = DepartmentBean.getDepartment(_branch, _region, DepartmentBean.BRANCH_DEPARTMENT_TYPE, true); // create the branch if necessary

		// ensure that the passed job title exists

		//PersonTitleBean.getPersonTitle(_jobtitle, true); // create the person title if necessary

		boolean needsRole = false;

		// ensure that the passed person is in the system
		UKOnlinePersonBean person;
		try
		{
			try
			{
				// first try to find the person using employee id as the username
				person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_employeeid);
			}
			catch (ObjectNotFoundException y)
			{
				// unable to find the person using the passed employee id.  find using
				person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_email);
				person.setUsername(_employeeid);
			}
		}
		catch (ObjectNotFoundException x)
		{
			// A person with the passed employee id was not found.  create this person

			cat.debug("ALERT: " + x.getMessage());
			person = new UKOnlinePersonBean();
			person.setUsername(_employeeid);
			needsRole = true;
		}

		/*
		person.setPassword(_email);
		person.setConfirmPassword(_email);
		person.setFirstName(_firstname);
		person.setLastName(_lastname);
		person.setEmail(_email);
		
		person.setTitle(_jobtitle);
		person.save();

		if (needsRole)
		{
			RoleBean studentRole = RoleBean.getRole(RoleBean.STUDENT_ROLE_NAME);
			person.addRole(studentRole);
		}
		 */

		// Maintain the passed user group.  first see if it exists
		
		/*
		PersonGroupBean personGroup;
		try
		{
			personGroup = PersonGroupBean.getPersonGroup(_usergroup, true);
		}
		catch (ObjectNotFoundException x)
		{
			cat.debug("ALERT: " + x.getMessage());
			throw x;
		}
		 */

		//cat.debug("personGroup >" + personGroup);

		// ensure that this person is a member of the passed user group
		
		/*
		person.setGroup(personGroup);
		 */



		person.setDepartment(branch);



		// I need to setup the supervisor passed along for this Person.  first see if this supervisor exists

		/*
		System.out.println("GETTING SUPERVISOR...");
		UKOnlinePersonBean supervisor = null;
		try
		{
			supervisor = (UKOnlinePersonBean)person.getSupervisor();
			System.out.println("SUPERVISOR FOUND!  ensuring that supervisor is attached to person");
		}
		catch (ObjectNotFoundException y)
		{
			// there's no entry in the SUPERVISOR table mapping a supervisor to the person logging in
			// find the supervisor for the specified region/location
			Vector persons = UKOnlinePersonBean.getDepartmentPersons(branch, "Branch Manager");
			if (persons.size() != 1)
				throw new IllegalValueException("Too many managers for branch or supervisor not found...");
			supervisor = (UKOnlinePersonBean)persons.elementAt(0);
		}

		// assign it to the person
		person.setSupervisor(supervisor);
		 */

		//Vector supervisors = person.getSupervisors();
		//System.out.println("NUM SUPERVISORS >" + supervisors.size());


		person.save();
	}
	
	public static void
	critTest2()
	{
		System.out.println("critTest2() invoked");
		try
		{
			Criteria crit = new Criteria();
			crit.add(PersonPeer.LASTNAME, "User");
			
			UKOnlineLoginBean.critTest(crit);
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	public static void
	critTest(Object _obj)
	{
		try
		{
			System.out.println("critTest() invoked");
			
			Class peer_class = Class.forName("com.badiyan.torque.PersonPeer");
			Class[] parameterTypes = new Class[] {_obj.getClass()};
			
			Method do_select_method = peer_class.getMethod("doSelect", parameterTypes);
			
			System.out.println("do_select_method >" + do_select_method.getName());
			
			Object[] arguments = new Object[] {_obj};
			List obj_list = (List)do_select_method.invoke(null, arguments);
			
			System.out.println("obj_list sizer >" + obj_list.size());
			

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	/*
	public static void
	processExternalContent()
	{
	    
	}
	 */
	
	public static boolean
	externalContentExists()
	{
	    boolean content_found = false;
	    
	    if (!CUBean.isMasterServer())
	    {
		String file_path = "C:\\ESM\\EcoLMS\\content_from_CD";

		File test_file = new File(file_path);
		if (test_file.isDirectory())
		{
		    String[] file_list = test_file.list();
		    if (file_list.length > 0)
		    {
			for (int i = 0; i < file_list.length; i++)
			{
			    if (file_list[i].toLowerCase().indexOf(".zip") > -1)
				content_found = true;
			}
		    }
		}
	    }
	    
	    return content_found;
	}

	// INSTANCE METHODS

	public PersonBean
	getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		//System.out.println("getPerson() invoked >" + username + ", " + password);
		
		setupLogging();
			// It would seem that under some circumstances I can lose my logging category.  This will re-initialize it.

		// Given well defined username and password properties, ask the CU server for access...

		if (person == null)
		{
			//cat.debug("USERNAME and PASSWORD >" + username + ", " + password);
		    
			
			// if no username or password is present, fail.
			if (username == null || password == null)
				throw new IllegalValueException("Username or password not specified.  Login Failed, Please Try Again");

			UKOnlineAccessManager am = (UKOnlineAccessManager)UKOnlineAccessManager.getInstance();
			try {
				person = (UKOnlinePersonBean)am.getUserWithCredentialsByEmailNoEncryption(username, password);
			} catch (Exception x) {
				person = (UKOnlinePersonBean)am.getUserWithCredentialsNoEncryption(username, password);
			}

			if (person == null)
				throw new IllegalValueException("Login Failed, Please Try Again");

			//this.enrollRequiredCourses();
		}

		return person;
	}

	// INSTANCE METHODS

	public PersonBean
	getPersonPasswordOnly()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		setupLogging();

		if (person == null)
		{
			if (password == null)
				throw new IllegalValueException("Password not specified.  Login Failed, Please Try Again");

			UKOnlineAccessManager am = (UKOnlineAccessManager)UKOnlineAccessManager.getInstance();
			person = (UKOnlinePersonBean)am.getUserWithCredentialsNoEncryptionPasswordOnly(password);

			if (person == null)
				throw new IllegalValueException("Login Failed, Please Try Again");
		}

		return person;
	}
	
	public void
	enrollRequiredCourses()
	{
	    try
	    {
		    UKOnlinePersonBean person_obj = (UKOnlinePersonBean)person;
		    Vector requiredCourses = person_obj.getRequiredCourses();
		    Iterator itr = requiredCourses.iterator();
		    while (itr.hasNext())
		    {
			    CourseBean course = (CourseBean)itr.next();
			    System.out.println("FOUND REQUIRED COURSE >" + course.getLabel());
			    try
			    {
				    //person_obj.enroll(course);
				    person_obj.enroll(course);
			    }
			    catch (Exception x)
			    {
				    //cat.debug("REQUIRED ENROLL ERROR >" + x.getMessage());
				    //x.printStackTrace();
			    }
		    }
	    }
	    catch (Exception xx)
	    {
		    cat.debug("ERROR >" + xx.getMessage());
	    }
	}

	public PersonBean
	getPersonByPersonid(String _personid)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		try
		{
			int id = Integer.parseInt(_personid);
			return UKOnlinePersonBean.getPerson(id);
		}
		catch (NumberFormatException x)
		{
			throw new IllegalValueException("The specified person id is invalid >" + _personid);
		}
	}
}