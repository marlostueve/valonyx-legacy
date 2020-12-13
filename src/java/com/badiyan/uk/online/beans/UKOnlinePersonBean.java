package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.torque.*;
import com.badiyan.uk.interfaces.*;
import com.badiyan.uk.events.*;
import com.badiyan.uk.tasks.*;


import com.valeo.qbpos.data.*;

import com.valonyx.events.QBCustomerAddEvent;
import com.valonyx.events.QBCustomerAddEventListener;
import java.util.*;
import java.math.*;

import java.net.*;
import java.io.*;
import org.apache.commons.lang.StringUtils;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author  marlo
 */
public class
UKOnlinePersonBean
	extends PersonBean
	implements java.io.Serializable, AudienceEventListener
{
	// CLASS VARIABLES

	public static final String TABLET_USER_PERSON_TYPE = "Tablet";
	public static final String PRACTITIONER_USER_PERSON_TYPE = "Practitioner";

	public static final String PROSPECT_PERSON_TYPE = "Prospect";
	public static final String CLIENT_PERSON_TYPE = "Client";
	
	public static final short ACTIVE_PERSON_STATUS = 1;
	public static final short BLACK_HOLE_PERSON_STATUS = 2;
	public static final short INACTIVE_PERSON_STATUS = 3;
	
	protected static final HashMap<CompanyBean,Vector> practitioner_hash = new HashMap<CompanyBean,Vector>(11);
	protected static final HashMap<CompanyBean,Vector> cashier_hash = new HashMap<CompanyBean,Vector>(11);
	
	private static Vector first_names = null;
	private static Vector last_names = null;

	// CLASS METHODS

	public static void
	cacheUpdate(String _key)
	{
	    try
	    {
			//courses.remove(_key);

			// simply removing this object creates potential object identity problems.  there may be references to the object removed in a user's session, for example.

			Integer key = new Integer(_key);
			//UKPersonBean cache_person = idPersons.get(key);
			UKOnlinePersonBean cache_person = (UKOnlinePersonBean)idPersons.get(key);
			if (cache_person != null)
			{
				Criteria crit = new Criteria();
				crit.add(PersonPeer.PERSONID, key.intValue());
				List objList = PersonPeer.doSelect(crit);
				if (objList.size() == 1)
				{
					Person obj = (Person)objList.get(0);
					cache_person.person = obj;
				}
			}
	    }
	    catch (Exception x)
	    {
			x.printStackTrace();
	    }
	}

	public static UKOnlinePersonBean
	getCashier(CompanyBean _company, String _name)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector cashiers = new Vector();
		Iterator cashiers_itr = UKOnlinePersonBean.getCashiers(_company).iterator();
		while (cashiers_itr.hasNext())
		{
			UKOnlinePersonBean cashier = (UKOnlinePersonBean)cashiers_itr.next();
			if (cashier.getLabel().equals(_name))
				cashiers.addElement(cashier);
			else if (cashier.getLastNameString().equals(_name))
				cashiers.addElement(cashier);
			else if (cashier.getFirstNameString().equals(_name))
				cashiers.addElement(cashier);
		}

		if (cashiers.size() == 0)
			throw new ObjectNotFoundException("Cashier not found for name >" + _name);
		else if (cashiers.size() > 1)
			throw new ObjectNotFoundException("Unique cashier not found for name >" + _name);

		return (UKOnlinePersonBean)cashiers.get(0);
	}

	public static Vector
	getCashiers(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector cashiers = (Vector)cashier_hash.get(_company);
		if (cashiers == null)
		{
			cashiers = new Vector();
			RoleBean role_obj = RoleBean.getRole(UKOnlineRoleBean.CASHIER_ROLE_NAME);

			Criteria crit = new Criteria();
			crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
			crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
			crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
			crit.addJoin(PersonPeer.PERSONID, PersonrolePeer.PERSONID);
			crit.addJoin(PersonrolePeer.ROLEID, RolePeer.ROLEID);
			crit.add(CompanyPeer.COMPANYID, _company.getId());
			crit.add(RolePeer.ROLEID, role_obj.getId());
			crit.add(PersonPeer.FACILITATORACTIVE, 1);
			crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
			System.out.println("critvv >" + crit.toString());
			Iterator itr = PersonPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				Person obj = (Person)itr.next();
				cashiers.addElement(UKOnlinePersonBean.getPerson(obj));
			}

			cashier_hash.put(_company, cashiers);
		}

		return cashiers;
	}

	public static void
	invalidateCashiers(CompanyBean _company)
	{
		cashier_hash.remove(_company);
	}

	public static Vector
	getDepartmentPersons(DepartmentBean _department, String _personType)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Hashtable departmentPersons = _department.getDepartmentPersonsHash();
		Vector persons = (Vector)departmentPersons.get(_personType);
		if (persons == null)
		{
			persons = new Vector();
			Criteria crit = new Criteria();
			crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
			crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
			crit.add(DepartmentPeer.DEPARTMENTID, _department.getId());
			crit.add(PersonPeer.PERSONTYPE, _personType);
			crit.add(PersonPeer.FACILITATORACTIVE, 1);
			List objList = PersonPeer.doSelect(crit);
			Iterator itr = objList.iterator();
			while (itr.hasNext())
			{
				Person personObj = (Person)itr.next();
				persons.addElement(UKOnlinePersonBean.getPerson(personObj));
			}
			departmentPersons.put(_personType, persons);
		}
		return persons;
	}

	public static int
	getNextFileNumber(CompanyBean _company)
		throws TorqueException
	{
		int next_file_number = 1;

		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());
		crit.addDescendingOrderByColumn(PersonPeer.FILE_NUMBER);
		crit.setLimit(1);

		List obj_list = PersonPeer.doSelect(crit);
		if (obj_list.size() == 1)
		{
			Person person_obj = (Person)obj_list.get(0);
			next_file_number = person_obj.getFileNumber() + 1;
		}

		return next_file_number;
	}

	public static PersonBean
	getPerson(int _id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		/*
		if (_id == 0)
			throw new ObjectNotFoundException("Could not locate person with id: " + _id);
		 */

		// look for the person in the Hashtable

		Integer key = new Integer(_id);
		PersonBean obj = idPersons.get(key);
		if (obj != null)
		{
			if (!(obj instanceof UKOnlinePersonBean))
			{
				idPersons.remove(key);
				usernamePersons.remove(obj.getUsernameString());
			}
		}
		UKOnlinePersonBean person = (UKOnlinePersonBean)idPersons.get(key);
		if (person == null)
		{
			// The named person was not located in the hashtable.  Search the db for it.

			//System.out.println("The named person was not located in the hashtable.  Search the db for it. - " + _id);

			Criteria crit = new Criteria();
			crit.add(PersonPeer.PERSONID, _id);
			List objList = PersonPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate person with id: " + _id);
			if (objList.size() > 1)
				throw new UniqueObjectNotFoundException("Multiple persons found for id: " + _id);

			person = new UKOnlinePersonBean((Person)objList.get(0));
			if (person == null)
				throw new ObjectNotFoundException("Could not locate person with id: " + _id);
			idPersons.put(key, person);
			usernamePersons.put(person.getUsername(), person);
		}

		return person;
	}

	public static PersonBean
	getPerson(String _username)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		// First check the username hashtable for a user with the specified username...

		Object obj = usernamePersons.get(_username);
		if (obj != null)
		{
			if (!(obj instanceof UKOnlinePersonBean))
			{
				PersonBean personObj = (PersonBean)obj;
				idPersons.remove(personObj.getIdString());
				usernamePersons.remove(_username);
			}
		}
		UKOnlinePersonBean person = (UKOnlinePersonBean)usernamePersons.get(_username);
		if (person == null)
		{
			// A person with the specified username does not exist in the hashtable.  Query the db...

			Criteria crit = new Criteria();
			crit.add(PersonPeer.USERNAME, _username);
			//crit.add(PersonPeer.ISACTIVE, 1); // commented out 2/17/11 - ISACTIVE is being used for black hole stuff
			crit.add(PersonPeer.FACILITATORACTIVE, 1);
			System.out.println("log in crit >" + crit.toString());
			List objList = PersonPeer.doSelect(crit);

			if (objList.size() == 0)
				throw new ObjectNotFoundException("User not found.");
			else if (objList.size() > 1)
				throw new UniqueObjectNotFoundException("Could not find unique user.");

			Person testUser = (Person)objList.get(0);
			person = new UKOnlinePersonBean(testUser);

			// Put the object in the in the respective hashtables...

			Integer key = new Integer(person.getId());
			idPersons.put(key, person);
			usernamePersons.put(_username, person);
		}

		return person;
	}

	public static PersonBean
	getPersonByPassword(String _password)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		// A person with the specified username does not exist in the hashtable.  Query the db...

		Criteria crit = new Criteria();
		crit.add(PersonPeer.PASSWORD, _password);
		//crit.add(PersonPeer.ISACTIVE, 1); // commented out 2/17/11 - ISACTIVE is being used for black hole stuff
		crit.add(PersonPeer.FACILITATORACTIVE, 1);
		System.out.println("getPersonByPassword log in crit >" + crit.toString());
		List objList = PersonPeer.doSelect(crit);

		if (objList.size() == 0)
			throw new ObjectNotFoundException("User not found.");
		else if (objList.size() > 1)
			throw new UniqueObjectNotFoundException("Could not find unique user.");

		Person testUser = (Person)objList.get(0);
		UKOnlinePersonBean person = new UKOnlinePersonBean(testUser);

		// Put the object in the in the respective hashtables...

		Integer key = new Integer(person.getId());
		idPersons.put(key, person);
		
		return person;
	}

	public static PersonBean
	getPerson(Person _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Integer key = new Integer(_person.getPersonid());
		PersonBean obj = idPersons.get(key);
		if (obj != null)
		{
			if (!(obj instanceof UKOnlinePersonBean))
			{
				idPersons.remove(key);
				usernamePersons.remove(obj.getUsernameString());
			}
		}
		UKOnlinePersonBean person = (UKOnlinePersonBean)idPersons.get(key);
		if (person == null)
		{
			person = new UKOnlinePersonBean(_person);

			// Put the object in the in the respective hashtables...
			idPersons.put(key, person);
			usernamePersons.put(person.getUsername(), person);
		}

		return person;
	}

	public static UKOnlinePersonBean
	getPerson(CompanyBean _company, int _id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		UKOnlinePersonBean person_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_id);
		if (_company.contains(person_obj))
			return person_obj;
		throw new ObjectNotFoundException("Could not locate person with id: " + _id + " for " + _company.getLabel());
	}

	public static UKOnlinePersonBean
	getPerson(CompanyBean _company, int _id, String _role)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		UKOnlinePersonBean person_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_id);
		//System.out.println("person_obj found >" + person_obj.getLabel());
		//System.out.println("_company >" + _company.getLabel());
		//System.out.println("_company.contains(person_obj) >" + _company.contains(person_obj));
		if (_company.contains(person_obj))
		{
			System.out.println("looking for role >" + _role);
			if (person_obj.hasRole(_role))
				return person_obj;
			else
				throw new ObjectNotFoundException("Could not locate person with id: " + _id + " with role " + _role + " in " + _company.getLabel());
		}
		throw new ObjectNotFoundException("Could not locate person with id: " + _id + " in " + _company.getLabel());
	}

	public static UKOnlinePersonBean
	getPersonByEmail(String _email)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();

		crit.add(PersonPeer.EMAIL1, _email);
		crit.add(PersonPeer.FACILITATORACTIVE, 1);

		List obj_list = PersonPeer.doSelect(crit);
		if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Person not found with email address >" + _email);
		else if (obj_list.size() > 1)
			throw new UniqueObjectNotFoundException("Unique person not found with email address  >" + _email);

		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)obj_list.get(0));
	}

	public static UKOnlinePersonBean
	getPersonByEmailPassword(String _email, String _password)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(PersonPeer.EMAIL1, _email);
		crit.add(PersonPeer.PASSWORD, _password);
		crit.add(PersonPeer.ISACTIVE, (short)1);
		List obj_list = PersonPeer.doSelect(crit);
		if (obj_list.size() == 0) {
			throw new ObjectNotFoundException("Person not found with email address >" + _email);
		} else if (obj_list.size() > 1) {
			throw new UniqueObjectNotFoundException("Unique person not found with email address  >" + _email);
		}

		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)obj_list.get(0));
	}

	public static UKOnlinePersonBean
	getPersonByEmail(UKOnlineCompanyBean _company, String _email)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		crit.add(PersonPeer.EMAIL1, _email);
		crit.add(PersonPeer.FACILITATORACTIVE, 1);

		List obj_list = PersonPeer.doSelect(crit);
		if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Person not found with email address >" + _email);
		else if (obj_list.size() > 1)
			throw new UniqueObjectNotFoundException("Unique person not found with email address  >" + _email);

		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)obj_list.get(0));
	}

	public static UKOnlinePersonBean
	getPersonByEmployeeId(UKOnlineCompanyBean _company, String _employeeId)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		crit.add(PersonPeer.EMPLOYEEID, _employeeId);
		crit.add(PersonPeer.FACILITATORACTIVE, 1);

		List obj_list = PersonPeer.doSelect(crit);
		if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Person not found with employee id >" + _employeeId);
		else if (obj_list.size() > 1)
			throw new UniqueObjectNotFoundException("Unique person not found with employee id >" + _employeeId);

		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)obj_list.get(0));
	}

	public static UKOnlinePersonBean
	getPersonByPoSListId(UKOnlineCompanyBean _company, String _listId)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		crit.add(PersonPeer.EMPLOYEEID, _listId);

		List obj_list = PersonPeer.doSelect(crit);
		if (obj_list.size() == 0)
		{
			System.out.println("Person not found with list id >" + _listId);
			System.out.println("search through CustomerRet objects for a matching list id...");
			CustomerRet obj = null;
			try
			{
				obj = CustomerRet.getCustomer(_company, _listId);

				Vector persons = new Vector();

				if ((obj.getPhone() != null) && (obj.getPhone().length() > 0))
					persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(_company, obj.getLastName(), obj.getFirstName(), obj.getPhone(), true);

				if (persons.size() == 0)
				{
					if ((obj.getPhone2() != null) && (obj.getPhone2().length() > 0))
						persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(_company, obj.getLastName(), obj.getFirstName(), obj.getPhone2(), true);
				}

				if (persons.size() == 0)
				{
					if ((obj.getEMail() != null) && (obj.getEMail().length() > 0))
						persons = UKOnlinePersonBean.getPersonsByLastNameFirstNameEmail(_company, obj.getLastName(), obj.getFirstName(), obj.getEMail(), true);
				}

				if (persons.size() == 0)
					persons = UKOnlinePersonBean.getPersonsByLastNameFirstName(_company, obj.getLastName(), obj.getFirstName());

				if (persons.size() == 1)
				{
					UKOnlinePersonBean person_obj = (UKOnlinePersonBean)persons.get(0);
					person_obj.setEmployeeId(_listId);
					try
					{
						person_obj.save();
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
					return person_obj;
				}
				else if (persons.size() > 1)
				{
					System.out.println("multiple customer ret objects found for criteria!?!?!");
					throw new UniqueObjectNotFoundException("Unique person not found with list id >" + _listId);
				}

			}
			catch (ObjectNotFoundException x)
			{
				System.out.println("CustomerRet not found for >" + _listId);
			}

			throw new ObjectNotFoundException("Person not found with list id >" + _listId);
		}
		else if (obj_list.size() > 1)
			throw new UniqueObjectNotFoundException("Unique person not found with list id >" + _listId);

		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)obj_list.get(0));
	}

	public static UKOnlinePersonBean
	getPersonByQBListId(UKOnlineCompanyBean _company, String _listId, boolean _only_those_not_deleted)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());
		if (_only_those_not_deleted)
			crit.add(PersonPeer.FACILITATORACTIVE, 1);
		crit.add(PersonPeer.EMAIL2, _listId);
		crit.add(PersonPeer.ISACTIVE, 1);

		List obj_list = PersonPeer.doSelect(crit);
		if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Person not found with list id >" + _listId);
		else if (obj_list.size() > 1)
			throw new UniqueObjectNotFoundException("Unique person not found with list id >" + _listId);

		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)obj_list.get(0));
	}

	public static Vector
	getNewCustomersByQBListId(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());
		crit.add(PersonPeer.EMAIL2, (Object)"", Criteria.ISNULL);
		crit.add(PersonPeer.FACILITATORACTIVE, 1);
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		Iterator obj_itr = PersonPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement((UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)obj_itr.next()));

		return vec;
	}

	public static Vector
	getUpdatedCustomersByQBListId(UKOnlineCompanyBean _company, Date _last_update)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		System.out.println("getUpdatedCustomersByQBListId >" + _last_update);
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());
		crit.add(PersonPeer.EMAIL2, (Object)"", Criteria.ISNOTNULL);
		crit.add(PersonPeer.FACILITATORACTIVE, 1);
		if (_last_update != null)
			crit.add(PersonPeer.MODIFICATIONDATE, _last_update, Criteria.GREATER_EQUAL);
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = PersonPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			UKOnlinePersonBean person_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)obj_itr.next());
			try
			{
				CustomerRet obj = CustomerRet.getCustomerQBFS(_company, person_obj.getQBFSListID());
				if (obj.getEditSequence() != null)
					vec.addElement(person_obj);
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
		}
		
		System.out.println("Customers found to update >" + vec.size());

		return vec;
	}

	public static Vector
	getPersons(CompanyBean _company, RoleBean _role)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector people = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.addJoin(PersonPeer.PERSONID, PersonrolePeer.PERSONID);
		crit.addJoin(PersonrolePeer.ROLEID, RolePeer.ROLEID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());
		crit.add(RolePeer.ROLEID, _role.getId());
		crit.add(PersonPeer.FACILITATORACTIVE, 1);
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			Person obj = (Person)itr.next();
			people.addElement(UKOnlinePersonBean.getPerson(obj));
		}
		return people;
	}

	public static Vector
	getPersons(CompanyBean _company, String _role)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		RoleBean role_obj = RoleBean.getRole(_role);

		Vector people = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.addJoin(PersonPeer.PERSONID, PersonrolePeer.PERSONID);
		crit.addJoin(PersonrolePeer.ROLEID, RolePeer.ROLEID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());
		crit.add(RolePeer.ROLEID, role_obj.getId());
		crit.add(PersonPeer.FACILITATORACTIVE, 1);
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			Person obj = (Person)itr.next();
			people.addElement(UKOnlinePersonBean.getPerson(obj));
		}
		return people;
	}

	public static Vector
	getPersons(CompanyBean _company, String _role, PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		RoleBean role_obj = RoleBean.getRole(_role);

		Vector people = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.addJoin(PersonPeer.PERSONID, PersonrolePeer.PERSONID);
		crit.addJoin(PersonrolePeer.ROLEID, RolePeer.ROLEID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());
		crit.add(RolePeer.ROLEID, role_obj.getId());
		crit.add(PersonPeer.FACILITATORACTIVE, 1);
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			Person obj = (Person)itr.next();
			people.addElement(UKOnlinePersonBean.getPerson(obj));
		}
		return people;
	}

	public static Vector
	getPersonsByFileNumber(CompanyBean _company, String _fileNumber)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector people = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		String search_string = _fileNumber + "%";
		crit.add(PersonPeer.FILE_NUMBER, _fileNumber);
		crit.add(PersonPeer.FACILITATORACTIVE, 1);

		crit.addAscendingOrderByColumn(PersonPeer.FILE_NUMBER);

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			Person obj = (Person)itr.next();
			people.addElement(UKOnlinePersonBean.getPerson(obj));
		}
		return people;
	}

	public static Vector
	getPersonsByFirstName(CompanyBean _company, String _firstName)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector people = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		String search_string = "%" + _firstName + "%";
		crit.add(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
		crit.add(PersonPeer.FACILITATORACTIVE, 1);

		crit.addAscendingOrderByColumn(PersonPeer.FIRSTNAME);
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			Person obj = (Person)itr.next();
			people.addElement(UKOnlinePersonBean.getPerson(obj));
		}
		return people;
	}

	public static Vector
	getPersonsByLastName(CompanyBean _company, String _lastName)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector people = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		String search_string = "%" + _lastName + "%";
		crit.add(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
		crit.add(PersonPeer.FACILITATORACTIVE, 1);

		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		crit.addAscendingOrderByColumn(PersonPeer.FIRSTNAME);

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			Person obj = (Person)itr.next();
			people.addElement(UKOnlinePersonBean.getPerson(obj));
		}
		return people;
	}

	public static Vector
	getPersonsByLastNameFirstName(CompanyBean _company, String _lastName, String _firstName)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector people = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		String search_string = _lastName + "%";
		crit.add(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
		search_string = _firstName + "%";
		crit.add(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
		
		crit.add(PersonPeer.FACILITATORACTIVE, 1);

		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			Person obj = (Person)itr.next();
			people.addElement(UKOnlinePersonBean.getPerson(obj));
		}
		return people;
	}

	public static Vector
	getPersonsByLastNameFirstNameEmail(CompanyBean _company, String _lastName, String _firstName, String _email, boolean _only_those_not_deleted)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector people = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		String search_string = _lastName + "%";
		crit.add(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
		search_string = _firstName + "%";
		crit.add(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);

		crit.add(PersonPeer.EMAIL1, _email);
		
		if (_only_those_not_deleted)
			crit.add(PersonPeer.FACILITATORACTIVE, 1);

		System.out.println("getPersonsByLastNameFirstNameEmail crit >" + crit.toString());

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			Person obj = (Person)itr.next();
			people.addElement(UKOnlinePersonBean.getPerson(obj));
		}
		return people;
	}

	public static Vector
	getPersonsByLastNameFirstNamePhone(CompanyBean _company, String _lastName, String _firstName, String _phone, boolean _only_those_not_deleted)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector people = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		String search_string = _lastName + "%";
		crit.add(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
		search_string = _firstName + "%";
		crit.add(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
		if (_only_those_not_deleted)
			crit.add(PersonPeer.FACILITATORACTIVE, 1);

		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);

		System.out.println("getPersonsByLastNameFirstNamePhone crit >" + crit.toString());

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)itr.next());

			// 952-380-4812 partially matches phone_str (952) 380-4812 >false

			try
			{
				PhoneNumberBean home_phone = person.getPhoneNumber(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
				boolean matches = home_phone.partiallyMatches(_phone, 4);
				System.out.println(home_phone.getLabel() + " partially matches phone_str " + _phone + " >" + matches);
				if (matches)
					people.addElement(person);
			}
			catch (ObjectNotFoundException x)
			{
			}
			
			if (!people.contains(person))
			{
				try
				{
					PhoneNumberBean cell_phone = person.getPhoneNumber(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
					boolean matches = cell_phone.partiallyMatches(_phone, 4);
					System.out.println(cell_phone.getLabel() + " partially matches phone_str " + _phone + " >" + matches);
					if (matches)
						people.addElement(person);
				}
				catch (ObjectNotFoundException y)
				{
				}
			}

			if (!people.contains(person))
			{
				try
				{
					PhoneNumberBean work_phone = person.getPhoneNumber(PhoneNumberBean.WORK_PHONE_NUMBER_TYPE);
					boolean matches = work_phone.partiallyMatches(_phone, 4);
					System.out.println(work_phone.getLabel() + " partially matches phone_str " + _phone + " >" + matches);
					if (matches)
						people.addElement(person);
				}
				catch (ObjectNotFoundException z)
				{
				}
			}
		}

		return people;
	}

	public static Vector
	getPersonsByKeyword(CompanyBean _company, String _keyword, UKOnlinePersonBean _logged_in_person, int _limit)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		if (_keyword != null) {
			String search_string = "%" + _keyword + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.FILE_NUMBER, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b).or(crit_c));
		}
		
		crit.add(PersonPeer.ISACTIVE, (short)1);
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		Vector people = new Vector();
		while (itr.hasNext()) {
			UKOnlinePersonBean anoka_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)itr.next());
			people.addElement(anoka_person);
		}
		return people;
	}

	public static Vector
	getPersonsByKeyword(CompanyBean _company, String _keyword, int _limit, boolean _include_inactives)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		boolean is_numeric_keyword = StringUtils.isNumeric(_keyword);
		
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		if (is_numeric_keyword) {
			crit.addJoin(PersonPeer.PERSONID, PhonenumberPeer.PERSONID);
		}
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		if (_keyword != null) {
			String search_string = "%" + _keyword + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			if (is_numeric_keyword) {
				Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.FILE_NUMBER, (Object)search_string, Criteria.LIKE);
				Criteria.Criterion crit_d = crit.getNewCriterion(PhonenumberPeer.PHONENUMBER, (Object)search_string, Criteria.LIKE);
				crit.add(crit_a.or(crit_b).or(crit_c).or(crit_d));
			} else {
				crit.add(crit_a.or(crit_b));
			}
		}
		
		if (!_include_inactives) {
			crit.add(PersonPeer.ISACTIVE, (short)1);
		}
		
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		crit.setDistinct();
		System.out.println("getPersonsByKeyword >" + crit.toString());
		Iterator itr = PersonPeer.doSelect(crit).iterator();
		Vector people = new Vector();
		while (itr.hasNext()) {
			UKOnlinePersonBean anoka_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)itr.next());
			people.addElement(anoka_person);
		}
		return people;
	}

	public static Vector
	getPersonsByKeyword(CompanyBean _company, String _keyword, UKOnlinePersonBean _logged_in_person, int _limit, int _criterion, int _active_filter)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		boolean restrict_search_to_logged_in_person_department = false;
		/*
		if (_logged_in_person != null) {
			boolean is_high_level_admin = _logged_in_person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) || _logged_in_person.hasRole(AnokaRoleBean.Q_COMP_ADMINISTRATOR_ROLE_NAME);
			//boolean is_pas_admin = _logged_in_person.hasRole(AnokaRoleBean.PAS_ADMINISTRATOR_ROLE_NAME);
			restrict_search_to_logged_in_person_department = !is_high_level_admin;
		}
		*/

		Criteria crit = new Criteria();
		if (!restrict_search_to_logged_in_person_department) {
			crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		}
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.addJoin(PersonPeer.PERSON_TITLE_ID, PersonTitlePeer.PERSON_TITLE_ID);
		if (restrict_search_to_logged_in_person_department && (_logged_in_person != null)) {
			crit.add(DepartmentPeer.DEPARTMENTID, _logged_in_person.getDepartment().getId());
		} else {
			crit.add(CompanyPeer.COMPANYID, _company.getId());
		}

		if (_keyword != null) {
			
			//crit.add(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			//crit.or(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			//crit.or(DepartmentPeer.DEPARTMENTNAME, (Object)search_string, Criteria.LIKE);
			
			if (_keyword.length() == 1) {
				String search_string = _keyword + "%";
				Criteria.Criterion crit_a = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
				crit.add(crit_a);
			} else {
			
				String first_keyword = null;
				String last_keyword = null;
				boolean is_first_last_search = false;
				int space_index = _keyword.indexOf(' ');
				if (space_index > -1) {
					is_first_last_search = true;
					first_keyword = _keyword.substring(0, space_index);
					last_keyword = _keyword.substring(space_index + 1);
				}

				if (_criterion == 0) {
					String search_string = _keyword + "%";
					Criteria.Criterion crit_a = crit.getNewCriterion(PersonPeer.LASTNAME, is_first_last_search ? (Object)last_keyword : (Object)search_string, Criteria.LIKE);
					Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, is_first_last_search ? (Object)first_keyword : (Object)search_string, Criteria.LIKE);
					Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.EMPLOYEEID, (Object)search_string, Criteria.LIKE);
					Criteria.Criterion crit_d = crit.getNewCriterion(DepartmentPeer.DEPARTMENTNAME, (Object)search_string, Criteria.LIKE);
					Criteria.Criterion crit_e = crit.getNewCriterion(PersonTitlePeer.TITLE, (Object)search_string, Criteria.LIKE);

					if (is_first_last_search) {
						crit.add(crit_a.and(crit_b).or(crit_c).or(crit_d).or(crit_e));
					} else {
						crit.add(crit_a.or(crit_b).or(crit_c).or(crit_d).or(crit_e));
					}
				} else {
					String search_string = _keyword + "%";
					Criteria.Criterion crit_a = null;
					if (_criterion == 1) {
						crit_a = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
					} else if (_criterion == 2) {
						crit_a = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
					} else if (_criterion == 3) {
						crit_a = crit.getNewCriterion(PersonPeer.EMPLOYEEID, (Object)search_string, Criteria.LIKE);
					} else if (_criterion == 4) {
						crit_a = crit.getNewCriterion(DepartmentPeer.DEPARTMENTNAME, (Object)search_string, Criteria.LIKE);
					} else if (_criterion == 5) {
						crit_a = crit.getNewCriterion(PersonTitlePeer.TITLE, (Object)search_string, Criteria.LIKE);
					}
					crit.add(crit_a);
				}
				//crit.add(crit_a.or(crit_b));
			}
		}
		
		if (_active_filter == 1) {
			crit.add(PersonPeer.ISACTIVE, (short)1);
		} else if (_active_filter == 2) {
			crit.add(PersonPeer.ISACTIVE, (short)0);
		} else if (_active_filter == 3) {
			crit.add(PersonPeer.FACILITATORACTIVE, (short)1);
		} else if (_active_filter == 4) {
			crit.add(PersonPeer.FACILITATORACTIVE, (short)0);
		}

		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		crit.addAscendingOrderByColumn(PersonPeer.FIRSTNAME);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		//System.out.println("crit .. >" + crit.toString());

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		Vector people = new Vector();
		while (itr.hasNext()) {
			UKOnlinePersonBean anoka_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)itr.next());
			/*
			if (_role == null) {
				people.addElement(anoka_person);
			} else {
				if (anoka_person.hasRole(_role)) {
					people.addElement(anoka_person);
				}
			}
			*/
			people.addElement(anoka_person);
		}
		return people;
	}

	public static Vector
	getManagedByPersonsByKeyword(UKOnlinePersonBean _manager, String _keyword, int _limit)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		//boolean is_numeric_keyword = StringUtils.isNumeric(_keyword);
		
		Criteria crit = new Criteria();
		
		crit.addJoin(SupervisorPeer.PERSONID, PersonPeer.PERSONID);
		
		crit.add(SupervisorPeer.SUPERVISORID, _manager.getId());

		if (_keyword != null) {
			String search_string = "%" + _keyword + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.EMAIL1, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b).or(crit_c));
		}
		
		crit.add(PersonPeer.ISACTIVE, (short)1);
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		crit.addAscendingOrderByColumn(PersonPeer.EMAIL1);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		//crit.setDistinct();
		System.out.println("getManagedByPersonsByKeyword >" + crit.toString());
		Iterator itr = PersonPeer.doSelect(crit).iterator();
		Vector people = new Vector();
		while (itr.hasNext()) {
			UKOnlinePersonBean anoka_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)itr.next());
			people.addElement(anoka_person);
		}
		return people;
	}

	public static UKOnlinePersonBean
	getPractitioner(CompanyBean _company, String _name)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector practitioners = new Vector();
		Iterator practitioners_itr = UKOnlinePersonBean.getPractitioners(_company).iterator();
		while (practitioners_itr.hasNext())
		{
			UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners_itr.next();
			if (practitioner.getLabel().equals(_name))
				practitioners.addElement(practitioner);
			else if (practitioner.getLastNameString().equals(_name))
				practitioners.addElement(practitioner);
			else if (practitioner.getFirstNameString().equals(_name))
				practitioners.addElement(practitioner);
		}

		if (practitioners.size() == 0)
			throw new ObjectNotFoundException("Practitioner not found for name >" + _name);
		else if (practitioners.size() > 1)
			throw new ObjectNotFoundException("Unique practitioner not found for name >" + _name);

		return (UKOnlinePersonBean)practitioners.get(0);
	}

	public static Vector
	getPractitioners(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		//System.out.println("getPractitioners() invoked in UKOnlinePersonBean");
		
		Vector practitioners = (Vector)practitioner_hash.get(_company);
		//System.out.println("practitioners >" + practitioners);
		if (practitioners == null)
		{
			practitioners = new Vector();
			RoleBean role_obj = RoleBean.getRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME);

			Criteria crit = new Criteria();
			crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
			crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
			crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
			crit.addJoin(PersonPeer.PERSONID, PersonrolePeer.PERSONID);
			crit.addJoin(PersonrolePeer.ROLEID, RolePeer.ROLEID);
			crit.add(CompanyPeer.COMPANYID, _company.getId());
			crit.add(RolePeer.ROLEID, role_obj.getId());
			crit.add(PersonPeer.FACILITATORACTIVE, 1);
			crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
			
			//System.out.println("crit >" + crit.toString());
			Iterator itr = PersonPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				Person obj = (Person)itr.next();
				practitioners.addElement(UKOnlinePersonBean.getPerson(obj));
			}
			
			practitioner_hash.put(_company, practitioners);
		}
		
		return practitioners;
	}

	public static void
	invalidatePractitioners(CompanyBean _company)
	{
		practitioner_hash.remove(_company);
	}

	public static UKOnlinePersonBean
	getTabletUser()
	    throws UniqueObjectNotFoundException, TorqueException, ObjectNotFoundException
	{
	    Criteria crit = new Criteria();
	    crit.add(PersonPeer.ISACTIVE, 1);
	    crit.add(PersonPeer.PERSONTYPE, UKOnlinePersonBean.TABLET_USER_PERSON_TYPE);
	    List obj_list = PersonPeer.doSelect(crit);
	    if (obj_list.size() == 1)
	    {
		UKOnlinePersonBean obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)obj_list.get(0));
		return obj;
	    }
	    else if (obj_list.size() == 0)
		throw new ObjectNotFoundException("Cannot find a student for this tablet.");
	    else
		throw new UniqueObjectNotFoundException("There are multiple students assigned to this tablet.");
	}

	public static boolean
	doesEmployeeIDExist(String _employee_id)
	    throws MalformedURLException, IOException, ClassNotFoundException
	{
	    String servletLocation = "http://" + CUBean.getProperty("cu.host") + ":" + CUBean.getProperty("cu.port") + "/" + CUBean.getProperty("cu.clientPackageName") + "/DBSyncTabletUserApproveServlet.html";

	    URL myServlet = new URL(servletLocation);
	    URLConnection servletConnection = myServlet.openConnection();

	    servletConnection.setDoInput(true);
	    servletConnection.setDoOutput(true);
	    servletConnection.setUseCaches(false);
	    servletConnection.setRequestProperty("Content-type","application/x-java-serialized-object");

	    ObjectOutputStream obj_out = new ObjectOutputStream(servletConnection.getOutputStream());

	    String[] command_array = new String[2];
	    command_array[0] = "1";
	    command_array[1] = _employee_id;

	    obj_out.writeObject(command_array);
	    obj_out.flush();

	    ObjectInputStream obj_in = new ObjectInputStream(servletConnection.getInputStream());
	    Boolean confirm = (Boolean)obj_in.readObject();

	    obj_in.close();
	    obj_out.close();

	    return confirm.booleanValue();
	}

	public static boolean
	isEmployeeIDAvailable(String _employee_id)
	    throws MalformedURLException, IOException, ClassNotFoundException
	{
	    String servletLocation = "http://" + CUBean.getProperty("cu.host") + ":" + CUBean.getProperty("cu.port") + "/" + CUBean.getProperty("cu.clientPackageName") + "/DBSyncTabletUserApproveServlet.html";

	    URL myServlet = new URL(servletLocation);
	    URLConnection servletConnection = myServlet.openConnection();

	    servletConnection.setDoInput(true);
	    servletConnection.setDoOutput(true);
	    servletConnection.setUseCaches(false);
	    servletConnection.setRequestProperty("Content-type","application/x-java-serialized-object");

	    ObjectOutputStream obj_out = new ObjectOutputStream(servletConnection.getOutputStream());

	    String[] command_array = new String[2];
	    command_array[0] = "2";
	    command_array[1] = _employee_id;

	    obj_out.writeObject(command_array);
	    obj_out.flush();

	    ObjectInputStream obj_in = new ObjectInputStream(servletConnection.getInputStream());
	    Boolean confirm = (Boolean)obj_in.readObject();

	    obj_in.close();
	    obj_out.close();

	    return confirm.booleanValue();
	}

	public static DBSyncMessage
	approveTabletUser(String _employee_id)
	    throws MalformedURLException, IOException, ClassNotFoundException
	{
	    String servletLocation = "http://" + CUBean.getProperty("cu.host") + ":" + CUBean.getProperty("cu.port") + "/" + CUBean.getProperty("cu.clientPackageName") + "/DBSyncTabletUserApproveServlet.html";

	    URL myServlet = new URL(servletLocation);
	    URLConnection servletConnection = myServlet.openConnection();

	    servletConnection.setDoInput(true);
	    servletConnection.setDoOutput(true);
	    servletConnection.setUseCaches(false);
	    servletConnection.setRequestProperty("Content-type","application/x-java-serialized-object");

	    ObjectOutputStream obj_out = new ObjectOutputStream(servletConnection.getOutputStream());

	    String[] command_array = new String[2];
	    command_array[0] = "3";
	    command_array[1] = _employee_id;

	    obj_out.writeObject(command_array);
	    obj_out.flush();

	    ObjectInputStream obj_in = new ObjectInputStream(servletConnection.getInputStream());
	    DBSyncMessage sync_obj = (DBSyncMessage)obj_in.readObject();

	    obj_in.close();
	    obj_out.close();

	    return sync_obj;
	}

	public static boolean
	confirmApproveTabletUser(String _employee_id)
	    throws MalformedURLException, IOException, ClassNotFoundException
	{
	    String servletLocation = "http://" + CUBean.getProperty("cu.host") + ":" + CUBean.getProperty("cu.port") + "/" + CUBean.getProperty("cu.clientPackageName") + "/DBSyncTabletUserApproveServlet.html";

	    URL myServlet = new URL(servletLocation);
	    URLConnection servletConnection = myServlet.openConnection();

	    servletConnection.setDoInput(true);
	    servletConnection.setDoOutput(true);
	    servletConnection.setUseCaches(false);
	    servletConnection.setRequestProperty("Content-type","application/x-java-serialized-object");

	    ObjectOutputStream obj_out = new ObjectOutputStream(servletConnection.getOutputStream());

	    String[] command_array = new String[2];
	    command_array[0] = "4";
	    command_array[1] = _employee_id;

	    obj_out.writeObject(command_array);
	    obj_out.flush();

	    ObjectInputStream obj_in = new ObjectInputStream(servletConnection.getInputStream());
	    Boolean confirm = (Boolean)obj_in.readObject();

	    obj_in.close();
	    obj_out.close();

	    return confirm.booleanValue();
	}

	public static String
	getFullName(String _employee_id)
	    throws MalformedURLException, IOException, ClassNotFoundException
	{
	    String servletLocation = "http://" + CUBean.getProperty("cu.host") + ":" + CUBean.getProperty("cu.port") + "/" + CUBean.getProperty("cu.clientPackageName") + "/DBSyncTabletUserApproveServlet.html";

	    URL myServlet = new URL(servletLocation);
	    URLConnection servletConnection = myServlet.openConnection();

	    servletConnection.setDoInput(true);
	    servletConnection.setDoOutput(true);
	    servletConnection.setUseCaches(false);
	    servletConnection.setRequestProperty("Content-type","application/x-java-serialized-object");

	    ObjectOutputStream obj_out = new ObjectOutputStream(servletConnection.getOutputStream());

	    String[] command_array = new String[2];
	    command_array[0] = "5";
	    command_array[1] = _employee_id;

	    obj_out.writeObject(command_array);
	    obj_out.flush();

	    ObjectInputStream obj_in = new ObjectInputStream(servletConnection.getInputStream());
	    String name = (String)obj_in.readObject();

	    obj_in.close();
	    obj_out.close();

	    return name;
	}

	// INSTANCE VARIABLES

	private Vector completedCourses;
	
	private GroupUnderCareBean group_under_care;
	private GroupUnderCareMemberTypeBean group_under_care_member_type;
	
	private PractitionerScheduleBean active_schedule = null;
	private Vector practice_areas = null;

	private boolean left_expanded = true;
	private boolean right_expanded = false;

	private HealthHistoryBean history;
	
	private String randomName = null;

	// CONSTRUCTORS

	public UKOnlinePersonBean() throws TorqueException
	{
		super();
		AudienceBean.addAudienceEventListener(this);
	}

	public UKOnlinePersonBean(Person _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		super(_person);
		AudienceBean.addAudienceEventListener(this);
	}

	// INSTANCE METHODS

	public boolean
	isLeftExpanded()
	{
		return left_expanded;
	}

	public boolean
	isRightExpanded()
	{
		return right_expanded;
	}

	public boolean
	isSynced() {
		try {
			return (this.getQBFSListID() != null);
		} catch (Exception x) {
			x.printStackTrace();
		}
		return false;
	}

	public void
	setLeftExpanded(boolean _expanded)
	{
		left_expanded = _expanded;
	}

	public void
	setRightExpanded(boolean _expanded)
	{
		right_expanded = _expanded;
	}

	public EnrollmentBean
	enroll(CourseBean _course)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		System.out.println("enroll(CourseBean) invoked in UKOnlinePersonBean");
		//getEnrollmentManager().enrollUserInCourse(person, _course.getProductLanguage());

		//verifyEnrollment(_course);

		UKOnlinePersonBean administrator = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(CUBean.getProperty("cu.adminUsername"));

		// create a new enrollment

		EnrollmentBean enrollment = new EnrollmentBean();
		enrollment.setPerson(this);
		enrollment.setCourse(_course);
		enrollment.setCreatePerson(administrator);

		Iterator itr = _course.getAudiences(this).iterator();
		if (itr.hasNext())
		{
			CourseAudienceBean audience = (CourseAudienceBean)itr.next();
			enrollment.setDueDate(audience.getCompleteByDate());
		}

		enrollment.save();
		enrollments = null;

		return enrollment;
	}
	
	public PractitionerScheduleBean
	getActiveSchedule()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (active_schedule == null)
			active_schedule = PractitionerScheduleBean.getActivePractitionerSchedule(this);
		
		return active_schedule;
	}
	
	public PractitionerScheduleBean
	getPractitionerSchedule(Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(this, _date);
		return practitioner_schedule;
	}
	
	public Vector
	getPractitionerScheduleAsAppointments(Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		Calendar now = Calendar.getInstance();
		
		Calendar schedule_date = Calendar.getInstance();
		schedule_date.setTime(_date);
		
		PractitionerScheduleBean practitioner_schedule_obj = null;
		
		if ((now.get(Calendar.DATE) == schedule_date.get(Calendar.DATE)) && (now.get(Calendar.MONTH) == schedule_date.get(Calendar.MONTH)) && (now.get(Calendar.YEAR) == schedule_date.get(Calendar.YEAR)))
			practitioner_schedule_obj = this.getActiveSchedule();
		else
		{
			// the requested schedule is on a different day than now.  I need to fetch the schedule for this practioner for the specified day
			
			practitioner_schedule_obj = PractitionerScheduleBean.getPractitionerSchedule(this, _date);
		}
		
		return practitioner_schedule_obj.toOfftimeAppointments(_date);
		
	}

	/*
	public BigDecimal
	getCommission(CheckoutCodeBean _item, BigDecimal _amount)
		throws TorqueException, Exception
	{
		if (!_item.isCommissionable())
			return BigDecimal.ZERO;
		
		String[] arr = this.getDefaultCommissions();
		String commission_perc_str = "";
		if (_item.getType() == CheckoutCodeBean.PROCEDURE_TYPE)
			commission_perc_str = arr[1];
		else if (_item.getType() == CheckoutCodeBean.INVENTORY_TYPE)
			commission_perc_str = arr[0];
			
		if (commission_perc_str.isEmpty())
			return BigDecimal.ZERO;
		
		BigDecimal commission_perc = new BigDecimal(commission_perc_str);
		return commission_perc.multiply(_amount).divide(one_hundred, 2, RoundingMode.HALF_UP);
		
		// this needs to be completed, obviously
		// need to be able to account for different commission percentages for different item types
	} 
	 */

	public BigDecimal
	getCommission(CheckoutCodeBean _item)
		throws TorqueException, Exception
	{
		System.out.println("getCommission() invoked in UKOnlinePersonBean >" + this.getLabel());
		System.out.println("_item >" + _item.getLabel());
		System.out.println("_amount >" + _item.getAmount().toString());
		System.out.println("_item.isCommissionable() >" + _item.isCommissionable());

		if (!_item.isCommissionable())
			return BigDecimal.ZERO;

		int department_id = _item.getDepartmentId();

		if (department_id == 0)
			return BigDecimal.ZERO;

		// PRACTITIONER_INVENTORY_DEPARTMENT_COMMISSION_SETTINGS_DB

		Criteria crit = new Criteria();
		crit.add(PractitionerInventoryDepartmentCommissionSettingsDbPeer.PRACTITIONER_ID, this.getId());
		crit.add(PractitionerInventoryDepartmentCommissionSettingsDbPeer.INVENTORY_DEPARTMENT_DB_ID, department_id);
		List objList = PractitionerInventoryDepartmentCommissionSettingsDbPeer.doSelect(crit);
		if (objList.size() != 1)
			return BigDecimal.ZERO;
		else
		{
			PractitionerInventoryDepartmentCommissionSettingsDb settings = (PractitionerInventoryDepartmentCommissionSettingsDb)objList.get(0);
			return settings.getCommission().multiply(_item.getAmount()).divide(one_hundred, 2, RoundingMode.HALF_UP);
		}
	}

	public BigDecimal
	getCommission(CheckoutCodeBean _item, BigDecimal _amount)
		throws TorqueException, Exception
	{
		/*
		System.out.println("getCommission() invoked in UKOnlinePersonBean >" + this.getLabel());
		System.out.println("_item >" + _item.getLabel());
		System.out.println("_amount >" + _amount.toString());
		System.out.println("_item.isCommissionable() >" + _item.isCommissionable());
		 */

		if (!_item.isCommissionable())
			return BigDecimal.ZERO;
		
		int department_id = _item.getDepartmentId();

		//System.out.println("department_id >" + department_id);

		if (department_id == 0)
			return BigDecimal.ZERO;
		
		// PRACTITIONER_INVENTORY_DEPARTMENT_COMMISSION_SETTINGS_DB
		
		Criteria crit = new Criteria();
		crit.add(PractitionerInventoryDepartmentCommissionSettingsDbPeer.PRACTITIONER_ID, this.getId());
		crit.add(PractitionerInventoryDepartmentCommissionSettingsDbPeer.INVENTORY_DEPARTMENT_DB_ID, department_id);
		//System.out.println("crit >" + crit.toString());
		List objList = PractitionerInventoryDepartmentCommissionSettingsDbPeer.doSelect(crit);
		//System.out.println("objList.size() >" + objList.size());
		if (objList.size() != 1)
			return BigDecimal.ZERO;
		else
		{
			PractitionerInventoryDepartmentCommissionSettingsDb settings = (PractitionerInventoryDepartmentCommissionSettingsDb)objList.get(0);
			return settings.getCommission().multiply(_amount).divide(one_hundred, 2, RoundingMode.HALF_UP);
		}
	}

	public String
	getCommissionPercentageString(InventoryDepartment _department)
		throws TorqueException, Exception
	{
		Criteria crit = new Criteria();
		crit.add(PractitionerInventoryDepartmentCommissionSettingsDbPeer.PRACTITIONER_ID, this.getId());
		crit.add(PractitionerInventoryDepartmentCommissionSettingsDbPeer.INVENTORY_DEPARTMENT_DB_ID, _department.getId());
		List objList = PractitionerInventoryDepartmentCommissionSettingsDbPeer.doSelect(crit);
		if (objList.size() != 1)
			return "";
		else
		{
			PractitionerInventoryDepartmentCommissionSettingsDb settings = (PractitionerInventoryDepartmentCommissionSettingsDb)objList.get(0);
			return settings.getCommission().setScale(2, RoundingMode.HALF_UP).toString();
		}
	}

	public void
	setCommissionPercentage(InventoryDepartment _department, BigDecimal _perc)
		throws TorqueException, Exception
	{
		PractitionerInventoryDepartmentCommissionSettingsDb settings = null;
				
		Criteria crit = new Criteria();
		crit.add(PractitionerInventoryDepartmentCommissionSettingsDbPeer.PRACTITIONER_ID, this.getId());
		crit.add(PractitionerInventoryDepartmentCommissionSettingsDbPeer.INVENTORY_DEPARTMENT_DB_ID, _department.getId());
		List objList = PractitionerInventoryDepartmentCommissionSettingsDbPeer.doSelect(crit);
		if (objList.size() == 0)
		{
			settings = new PractitionerInventoryDepartmentCommissionSettingsDb();
			settings.setInventoryDepartmentDbId(_department.getId());
			settings.setPractitionerId(this.getId());
		}
		else
			settings = (PractitionerInventoryDepartmentCommissionSettingsDb)objList.get(0);
		
		settings.setCommission(_perc);
		settings.save();
	}

	public Iterator
	getCompletedCourses()
	{
		if (completedCourses == null)
		{
			completedCourses = new Vector();

		}

		return completedCourses.iterator();
	}

	public String
	getCostCenterString()
	{
	    String str = person.getEmail2();
	    if (str == null)
		return "";
	    return str;
	}

	public void
	setCostCenter(String _cost_center)
		throws TorqueException
	{
	    person.setEmail2(_cost_center);
	}

	/*
	public String
	getQBFSListID()
	{
	    String str = person.getEmail2();
	    if (str == null)
		return "";
	    return str;
	}
	 * 
	 */

	public void
	setQBFSListID(String _str)
		throws TorqueException
	{
	    person.setEmail2(_str);
		notifyQBCustomerAddEvent();
	}

	public int
	getDepartmentId()
		throws ObjectNotFoundException
	{
		if (department == null)
			throw new ObjectNotFoundException(this.getFullName() + " not assigned to a department.");

		return department.getId();
	}

	public String
	getDepartmentString()
	{
		return super.getDepartmentNameString();
	}

	public String
	getEmployeeNumber()
	{
		return person.getEmployeeid();
	}

	public String
	getEmployeeNumberString()
	{
		if (person.getEmployeeid() == null)
			return "";
		return person.getEmployeeid();
	}
	
	public GroupUnderCareBean
	getGroupUnderCare()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		if (this.group_under_care == null)
			this.group_under_care = GroupUnderCareBean.getGroupUnderCare(this);
		
		return this.group_under_care;
	}
	
	public GroupUnderCareMemberTypeBean
	getGroupUnderCareMemberType()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (this.group_under_care_member_type == null)
			this.group_under_care_member_type = GroupUnderCareMemberTypeBean.getMemberType(this);
		
		return this.group_under_care_member_type;
	}
	
	public String
	getGroupUnderCareMemberTypeLabel()
	{
		try
		{
			if (this.group_under_care_member_type == null)
				this.group_under_care_member_type = GroupUnderCareMemberTypeBean.getMemberType(this);
			
			return this.group_under_care_member_type.getLabel();
		}
		catch (Exception x)
		{
			return "Type Not Found";
		}
	}
	
	public String
	getGroupUnderCareMemberTypeValue()
	{
		try
		{
			if (this.group_under_care_member_type == null)
				this.group_under_care_member_type = GroupUnderCareMemberTypeBean.getMemberType(this);
			
			return this.group_under_care_member_type.getValue();
		}
		catch (Exception x)
		{
			return "-1";
		}
	}

	public String
	getHealthHistoryString()
	{
		try
		{
			if (history == null)
				history = HealthHistoryBean.getHealthHistory(this, true);
			return history.getHistoryString();
		}
		catch (Exception x)
		{
			x.printStackTrace();
			return "";
		}
	}
	
	public String
	getInitials() {
		try
		{
			String first = this.getFirstNameString().toUpperCase();
			String last = this.getLastNameString().toUpperCase();
			String initials = "";
			if (!first.isEmpty())
				initials += first.charAt(0);
			if (!last.isEmpty())
				initials += last.charAt(0);
			return initials;
		}
		catch (Exception x)
		{
			x.printStackTrace();
			return "";
		}
	}

	public PersonTitleBean
	getJobTitle()
		throws TorqueException, ObjectNotFoundException
	{
		return PersonTitleBean.getPersonTitle(person.getPersonTitleId());
	}

	public String
	getJobTitleString()
		throws TorqueException
	{
		try
		{
			PersonTitleBean obj = PersonTitleBean.getPersonTitle(person.getPersonTitleId());
			return obj.getNameString();
		}
		catch (ObjectNotFoundException x)
		{
			return "[NOT FOUND]";
		}
	}

	public int
	getLocationId()
		throws ObjectNotFoundException
	{
		return 0;
	}

	public String
	getLocationString()
	{
		return "[REMOVED]";
	}
	
	public String
	getNextAppointmentString()
	{
		try
		{
			AppointmentBean next_appointment = AppointmentBean.getNextAppointmentForClient(this, AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
			if (next_appointment == null)
				return "Not Scheduled";
			return next_appointment.getLabelDateTime();
		}
		catch (Exception x)
		{
			return x.getMessage();
		}
	}
	
	public String
	getLastAppointmentString()
	{
		try
		{
			AppointmentBean last_appointment = AppointmentBean.getLastAppointmentForClient(this);
			if (last_appointment == null)
				return "Not Scheduled";
			return last_appointment.getLabelDateTime();
		}
		catch (Exception x)
		{
			return x.getMessage();
		}
	}
	
	public Vector
	getOpenOrders()
		throws TorqueException
	{
		return ValeoOrderBean.getOpenOrders(this);
	}
	
	public Vector
	getOpenOrders(Date _date)
		throws TorqueException
	{
		return ValeoOrderBean.getOpenOrders(this, _date);
	}

	public Vector
	getRecentOrders(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return ValeoOrderBean.getRecentOrders(_company, this);
	}

	public Vector
	getRecentMostCommonPurchases()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -6); // go back 6 months
		return ValeoOrderBean.getRecentMostCommonPurchases(this, c.getTime());
	}

	public Vector
	getRecentMostCommonPurchasesInventoryOnly()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -6); // go back 6 months
		return ValeoOrderBean.getRecentMostCommonPurchasesInventoryOnly(this, c.getTime());
	}
	
	public String
	getPlanStatusString()
	{
		return "[PAYMENT PLAN STATUS]";
	}
	
	public String
	getPlanBalanceString()
	{
		return "[BALANCE]";
	}

	public boolean
	hasPlan(PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Iterator itr = PaymentPlanInstanceBean.getPaymentPlanInstances(this, _practice_area).iterator();
		return itr.hasNext();
	}

	public PaymentPlanInstanceBean
	getPlan(AppointmentBean _appointment)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Iterator itr = PaymentPlanInstanceBean.getPaymentPlanInstances(this, _appointment.getPracticeArea()).iterator();
		if (itr.hasNext())
			return (PaymentPlanInstanceBean)itr.next();
		else
			throw new ObjectNotFoundException(this.getLabel() + " has no active payment plan for " + _appointment.getPracticeArea().getLabel());
	}
	
	public PaymentPlanInstanceBean
	getPlan(PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Iterator itr = PaymentPlanInstanceBean.getPaymentPlanInstances(this, _practice_area).iterator();
		if (itr.hasNext())
			return (PaymentPlanInstanceBean)itr.next();
		else
			throw new ObjectNotFoundException(this.getLabel() + " has no active payment plan for " + _practice_area.getLabel());
	}

	public Vector
	getPracticeAreas()
		throws TorqueException, ObjectNotFoundException
	{
		if (practice_areas == null)
			practice_areas = PracticeAreaBean.getPracticeAreas(this);

		return practice_areas;
	}

	public String
	getPracticeAreasIdString()
		throws TorqueException, ObjectNotFoundException
	{
		String str = "";
		this.getPracticeAreas();
		Iterator itr = practice_areas.iterator();
		while (itr.hasNext())
		{
			PracticeAreaBean obj = (PracticeAreaBean)itr.next();
			if (str.equals(""))
				str = "|" + obj.getValue() + "|";
			else
				str += obj.getValue() + "|";
		}
		return str;
	}

	public void
	invalidatePracticeAreas()
	{
		practice_areas = null;
	}
	
	public BigDecimal
	getPreviousBalance(CompanyBean _company)
		throws TorqueException, UniqueObjectNotFoundException
	{
		/*
		System.out.println("getPreviousBalance() invoked in UKOnlinePersonBean");
		BigDecimal previous_balance = new BigDecimal(0);
		
		Criteria crit = new Criteria();
		crit.addJoin(PersonorderPeer.ORDERID, CheckoutOrderlinePeer.ORDERID);
		crit.add(PersonorderPeer.PERSONID, this.getId());
		System.out.println("crit >" + crit.toString());
		Iterator obj_itr = CheckoutOrderlinePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			CheckoutOrderline orderline = (CheckoutOrderline)obj_itr.next();
			//BigDecimal amount = orderline.getActualAmount();
			BigDecimal amount = orderline.getPrice();
			previous_balance = previous_balance.add(amount);
		}
		
		return previous_balance;
		 */

		// The CustomerRet object is going to have the balance according to QuickBooks.
		// This balance is only going to be current (presumably) from the latest cash out.
		// Any transactions after the latest cash out would not be included here.

		// Of course if QB is not integrated, there would be no CustomerRet object at all.
		// I'm thinking that I should maintain a balance variable in addition to a previous/external balance value.  I can import all
		// previous balances when we switch over to PoS and then just add that value on to the PoS only balance.

		try
		{
			CustomerRet customer_ret_obj = CustomerRet.getCustomer(_company, this.getEmployeeNumberString());
			return new BigDecimal(customer_ret_obj.getAccountBalance());
		}
		catch (ObjectNotFoundException x)
		{
			return new BigDecimal(0);
		}
	}
	
	public String
	getPreviousBalanceString(CompanyBean _company)
		throws TorqueException
	{
		try
		{
			BigDecimal previous_balance = this.getPreviousBalance(_company);
			return previous_balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		}
		catch (Exception x)
		{
			x.printStackTrace();
			return "0.00";
		}
	}

	public void
	setInitialBalance(BigDecimal _initial_balance)
	{
		person.setPreviousBalance(_initial_balance);
	}

	public BigDecimal
	getInitialBalance()
	{
		return person.getPreviousBalance();
	}

	public BigDecimal
	getBalance()
	{
		// balance = initial balance + current balance

		BigDecimal initial_balance = person.getPreviousBalance();
		BigDecimal current_balance = person.getCurrentBalance();

		if (initial_balance == null && current_balance == null)
			return BigDecimal.ZERO;
		else if (current_balance == null)
			return initial_balance;
		else if (initial_balance == null)
			return current_balance;
		else
			return initial_balance.add(current_balance);
	}

	public String
	getBalanceString()
	{
		return this.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public void
	adjustBalanceDown(BigDecimal _adjustment)
	{
		// the balance will be adjusted during checkout anytime the person is paying on account.  any other time???

		BigDecimal current_balance = person.getCurrentBalance();
		if (current_balance == null)
			person.setCurrentBalance(_adjustment);
		else
			person.setCurrentBalance(current_balance.subtract(_adjustment));
	}

	public void
	adjustBalanceUp(BigDecimal _adjustment)
	{
		// the balance will be adjusted during checkout anytime the person is paying on account.  any other time???

		BigDecimal current_balance = person.getCurrentBalance();
		if (current_balance == null)
			person.setCurrentBalance(_adjustment);
		else
			person.setCurrentBalance(current_balance.add(_adjustment));
	}
	
	public void
	setCurrentBalance(BigDecimal _current_balance)
	{
		person.setCurrentBalance(_current_balance);
	}

	public int
	getRegionId()
		throws ObjectNotFoundException
	{
		return 0;
	}

	public String
	getRegionString()
	{
		/*
		try
		{
			if (department == null)
				return "[REGION NOT FOUND]";
			String parentNameString = department.getParentNameString();
			if (parentNameString.equals(""))
				return "[REGION NOT FOUND]";
			return parentNameString;
		}
		catch (Exception x)
		{
			return "[REGION NOT FOUND]";
		}
		 */
		return "[REMOVED]";
	}

	public Vector
	getRecommendedCourseCertifications(String _sortBy, boolean _all, boolean _applyAudience)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		cat.debug("getRecommendedCourseCertifications() invoked in UKOnlinePersonBean");
		Vector courses = getRecommendedCourses(_sortBy, _all, _applyAudience);

		String certSortString = _sortBy;
		if (_sortBy.equals(CoursePeer.RELEASEDDATE))
			certSortString = RequirementsetPeer.RELEASEDDATE;
		else if (_sortBy.equals(CoursePeer.COURSENAME))
			certSortString = RequirementsetPeer.SETNAME;
		Vector certifications = getRecommendedCertifications(certSortString, _all, _applyAudience);

		Vector vec = doSortStuff(_sortBy, courses, certifications);

		return vec;
	}

	public Vector
	getRecommendedCourseCertifications(String _sortBy, boolean _all, PersonGroupBean _group)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		cat.debug("getRecommendedCourseCertifications() invoked in UKOnlinePersonBean");
		Vector courses = getRecommendedCourses(_sortBy, _all, _group);

		String certSortString = _sortBy;
		if (_sortBy.equals(CoursePeer.RELEASEDDATE))
			certSortString = RequirementsetPeer.RELEASEDDATE;
		else if (_sortBy.equals(CoursePeer.COURSENAME))
			certSortString = RequirementsetPeer.SETNAME;
		/*
		else if (_sortBy.equals(CoursePeer.CATEGORYNAME))
			certSortString = RequirementsetPeer.CATEGORYNAME;
		 */
		Vector certifications = getRecommendedCertifications(certSortString, _all, _group);

		return doSortStuff(_sortBy, courses, certifications);
	}

	private Vector
	doSortStuff(String _sortBy, Vector courses, Vector certifications)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		Vector vec = new Vector();
		Iterator courseItr = courses.iterator();
		Iterator certItr = certifications.iterator();

		CourseBean courseObj = null;
		CertificationBean certObj = null;
		while (courseItr.hasNext() || certItr.hasNext())
		{
			if ((courseObj == null) && courseItr.hasNext())
				courseObj = (CourseBean)courseItr.next();
			if ((certObj == null) && certItr.hasNext())
				certObj = (CertificationBean)certItr.next();

			if ((courseObj == null) && (certObj != null))
			{
				vec.addElement(certObj);
				certObj = null;
			}
			else if ((certObj == null) && (courseObj != null))
			{
				vec.addElement(courseObj);
				courseObj = null;
			}
			else
			{
				if (_sortBy.equals(CoursePeer.RELEASEDDATE))
				{
					Date courseReleasedDate = courseObj.getReleasedDate();
					Date certReleasedDate = certObj.getReleasedDate();
					if (courseReleasedDate.compareTo(certReleasedDate) < 0)
					{
						vec.addElement(courseObj);
						courseObj = null;
					}
					else if (certReleasedDate.compareTo(courseReleasedDate) < 0)
					{
						vec.addElement(certObj);
						certObj = null;
					}
					else
					{
						vec.addElement(certObj);
						vec.addElement(courseObj);
						courseObj = null;
						certObj = null;
					}
				}
				else if (_sortBy.equals(CoursePeer.COURSENAME))
				{
					String courseNameString = courseObj.getName();
					String certNameString = certObj.getName();
					if (courseNameString.compareTo(certNameString) < 0)
					{
						vec.addElement(courseObj);
						courseObj = null;
					}
					else if (certNameString.compareTo(courseNameString) < 0)
					{
						vec.addElement(certObj);
						certObj = null;
					}
					else
					{
						vec.addElement(certObj);
						vec.addElement(courseObj);
						courseObj = null;
						certObj = null;
					}
				}
				/*
				else if (_sortBy.equals(CoursePeer.CATEGORYNAME))
				{
					String courseNameString = courseObj.getCategoryNameString();
					String certNameString = certObj.getCategoryNameString();
					if (courseNameString.compareTo(certNameString) < 0)
					{
						vec.addElement(courseObj);
						courseObj = null;
					}
					else if (certNameString.compareTo(courseNameString) < 0)
					{
						vec.addElement(certObj);
						certObj = null;
					}
					else
					{
						vec.addElement(certObj);
						vec.addElement(courseObj);
						courseObj = null;
						certObj = null;
					}
				}
				 */
			}
		}
		if (certObj != null)
			vec.addElement(certObj);
		if (courseObj != null)
			vec.addElement(courseObj);
		return vec;
	}

	public String
	getQBFSListID()
		throws IllegalValueException
	{
		String str = person.getEmail2();
		if (str == null)
			throw new IllegalValueException("QBFS ListID not found for " + this.getLabel());
		return str;
	}

	public boolean
	hasQBFSListID()
	{
		String str = person.getEmail2();
		if (str == null || str.equals(""))
			return false;
		return true;
	}

	public String
	getQBPOSListID()
		throws IllegalValueException
	{
		String str = person.getEmployeeid();
		if (str == null)
			throw new IllegalValueException("QBPOS ListID not found for " + this.getLabel());
		return str;
	}

	/**
	 * Returns the last company that this person was working with
	 *
	 * @return				selected class
	 */
	public CompanyBean
	getSelectedCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, Exception
	{
		System.out.println("getSelectedCompany() invoked in UKOnlinePersonBean");
		
		if (selected_company == null) {
			Criteria crit = new Criteria();
			crit.add(SelectedCompanyPeer.PERSON_ID, this.getId());
			List obj_list = SelectedCompanyPeer.doSelect(crit);
			if (obj_list.size() == 0)
			{
				if (this.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME)) {
					crit = new Criteria();
					crit.add(CompanyAdministratorPeer.PERSON_ID, this.getId());
					obj_list = CompanyAdministratorPeer.doSelect(crit);
					if (obj_list.size() > 0)
					{
						CompanyAdministrator obj = (CompanyAdministrator)obj_list.get(0);
						selected_company = UKOnlineCompanyBean.getCompany(obj.getCompanyId());

						// create an entry in the selected company table

						crit = new Criteria();
						crit.add(SelectedCompanyPeer.PERSON_ID, this.getId());
						crit.add(SelectedCompanyPeer.COMPANY_ID, selected_company.getId());
						SelectedCompanyPeer.doInsert(crit);
					}
					else
						throw new ObjectNotFoundException("No company selected for " + this.getLabel());
				} else {
					crit = new Criteria();
					crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
					crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
					crit.add(DepartmentpersonPeer.PERSONID, person.getPersonid());
					obj_list = CompanyPeer.doSelect(crit);
					if (obj_list.size() == 0)
						throw new ObjectNotFoundException("No company selected for " + this.getLabel());
					Company obj = (Company)obj_list.get(0);
					selected_company = UKOnlineCompanyBean.getCompany(obj.getCompanyid());
				}

				this.selectCompany(selected_company);
			}
			else if (obj_list.size() == 1) {
				SelectedCompany obj = (SelectedCompany)obj_list.get(0);
				selected_company = UKOnlineCompanyBean.getCompany(obj.getCompanyId());
			} else {
				throw new UniqueObjectNotFoundException("Multiple Companies Selected");
			}

		}
		
		return selected_company;
	}
	
	public short
	getStatus()
	{
		/* removed 4/16/19
		if (this.isInBlackHole())
			return UKOnlinePersonBean.BLACK_HOLE_PERSON_STATUS;
		*/
		if (this.isActive())
			return UKOnlinePersonBean.ACTIVE_PERSON_STATUS;
		else
			return UKOnlinePersonBean.INACTIVE_PERSON_STATUS;
	}
	
	public boolean
	supervises(PersonBean _person) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(SupervisorPeer.PERSONID, _person.getId());
		crit.add(SupervisorPeer.SUPERVISORID, this.getId());
		return (SupervisorPeer.doSelect(crit).size() == 1);
	}

	public PersonBean
	getSupervisor()
		throws ObjectNotFoundException, UniqueObjectNotFoundException, TorqueException, Exception
	{
		if (supervisors == null)
		{
			supervisors = new Vector();

			Criteria crit = new Criteria();
			crit.add(SupervisorPeer.PERSONID, this.getId());
			List objList = SupervisorPeer.doSelect(crit);

			if (objList.size() == 0)
				throw new ObjectNotFoundException("Supervisor not found.");

			Iterator itr = objList.iterator();
			while (itr.hasNext())
			{
				Supervisor supervisorObj = (Supervisor)itr.next();
				UKOnlinePersonBean supervisor = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(supervisorObj.getSupervisorid());
				supervisors.addElement(supervisor);
			}
		}

		if (supervisors.size() == 0)
			throw new ObjectNotFoundException("Supervisor not found.");
		if (supervisors.size() > 1)
				throw new UniqueObjectNotFoundException("Multiple supervisors exist for " + getFullName() + ".  You'll have to use a different interface.");

		return (PersonBean)supervisors.elementAt(0);
	}

	public String
	getUserGroupString()
		throws TorqueException, ObjectNotFoundException
	{
		try
		{
			if (group == null)
				super.getGroup();
			if (group == null)
				return "";
			return group.getNameString();
		}
		catch (ObjectNotFoundException x)
		{
			return "[NOT FOUND]";
		}
	}

	public String
	getUKCommonLastUpdateDate()
	{
	    return super.getComment();
	}

	public boolean
	hasPreviousLogin()
	{
	    return (person.getFacilitatoractive() == (short)1);
	}

	public boolean
	hasBirthdayDuringWeek(Date _date)
	{
		Date bd = person.getBirthdate();
		if (bd == null)
			return false;

		Calendar birth_date = Calendar.getInstance();
		birth_date.setTime(bd);
		Calendar week_in_question = Calendar.getInstance();
		week_in_question.setTime(_date);

		return (week_in_question.get(Calendar.WEEK_OF_YEAR) == birth_date.get(Calendar.WEEK_OF_YEAR));
	}

	public boolean
	isAdult() {
		
		Date bd = person.getBirthdate();
		if (bd == null)
			return false;

		Calendar birth_date = Calendar.getInstance();
		birth_date.setTime(bd);
		Calendar eighteen_years_ago = Calendar.getInstance();
		eighteen_years_ago.add(Calendar.YEAR, -18);
		
		return birth_date.before(eighteen_years_ago);
	}

	public boolean
	isDeceased()
	{
	    return (person.getIscoursecontact() == (short)1);
	}

	public boolean
	isFemale()
	{
	    return !this.isOKSendEmail();
	}

	public boolean
	isMale()
	{
	    return this.isOKSendEmail();
	}

	public boolean
	isNewClient()
		throws TorqueException
	{
		// we're going to define a new client as a client that has no checked-out appointments...

		return !(AppointmentBean.hasAppointmentForClient(this, AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS));
	}

	public void
	setDeceased(boolean _deceased)
	{
		person.setIscoursecontact(_deceased ? (short)1 : (short)0);
	}

	public void
	setGroup(PersonGroupBean _group)
		throws TorqueException, ObjectAlreadyExistsException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
	{
		if (group == null)
		{
			// first see if there's an existing record

			Criteria crit = new Criteria();
			crit.add(PersonGroupMemberPeer.PERSONID, this.getId());
			List objList = PersonGroupMemberPeer.doSelect(crit);
			if (objList.size() == 1)
			{
				PersonGroupMember obj = (PersonGroupMember)objList.get(0);
				group = PersonGroupBean.getPersonGroup(obj.getPersonGroupId());
			}
			else if (objList.size() == 0)
			{

				// try to make the assignment in the database.  create a new Persongroupmember object
				PersonGroupMember personGroupMember = new PersonGroupMember();
				personGroupMember.setPersonid(this.getId());
				personGroupMember.setPersonGroupId(_group.getId());
				try {
					personGroupMember.save();
					group = _group;
				} catch (Exception x) {
					x.printStackTrace();
					throw new ObjectAlreadyExistsException("Can't assign " + this.getFullName() + " to group " + _group.getName() + ".  This person already has a group assignment.");
				}
			}
			else
				throw new UniqueObjectNotFoundException("Multiple group assignment error");
		}

		if (!group.equals(_group))
		{
			// I need to change the assigned group
			Criteria crit = new Criteria();
			crit.add(PersonGroupMemberPeer.PERSONID, this.getId());
			List objList = PersonGroupMemberPeer.doSelect(crit);
			if (objList.size() == 1)
			{
			    PersonGroupMember obj = (PersonGroupMember)objList.get(0);
			    PersonGroupMemberPeer.doDelete(obj);

			    if (CUBean.isMasterServer())
				CUBean.storeDBSyncObject(new DBSyncMessage(-1, this.getIdString(), obj, DBSyncMessage.DB_DELETE, Calendar.getInstance(), "com.badiyan.torque.PersonGroupMemberPeer", "com.badiyan.uk.online.beans.UKOnlinePersonBean"), this);
			}

			PersonGroupMember personGroupMember = new PersonGroupMember();
			personGroupMember.setPersonid(this.getId());
			personGroupMember.setPersonGroupId(_group.getId());
			try
			{
				personGroupMember.save();

				if (CUBean.isMasterServer())
					    CUBean.storeDBSyncObject(new DBSyncMessage(-1, this.getIdString(), personGroupMember, DBSyncMessage.DB_INSERT, Calendar.getInstance(), "com.badiyan.torque.PersonGroupMemberPeer", "com.badiyan.uk.online.beans.UKOnlinePersonBean"), this);
			}
			catch (Exception x)
			{
				System.out.println("Berror >" + x.getMessage());
				x.printStackTrace();
				throw new ObjectAlreadyExistsException("Can't assign " + this.getFullName() + " to group " + _group.getName() + ".  This person already has a group assignment.");
			}
		}

		group = _group;
	}

	public void
	setHasPreviousLogin(boolean _bool)
	{
	    person.setFacilitatoractive(_bool ? (short)1 : (short)0);
	}

	public void
	setUKCommonLastUpdateDate(String _long_str)
	{
	    super.setComment(_long_str);
	}

	public void
	setMale()
	{
		if (super.person.getOksendemail() > 2) { // lame - fix this
			return;
		}
	    this.setOKSendEmail(true);
	}

	public void
	setFemale()
	{
		if (super.person.getOksendemail() > 2) { // lame - fix this
			return;
		}
	    this.setOKSendEmail(false);
	}

	public void
	setSSN(String _ssn)
	{
		person.setSsn(_ssn);
	}

	public String
	getSSNString()
	{
		String str = person.getSsn();
		if (str == null)
			return "";
		return str;
	}

	public void
	setFileNumber(int _number)
	{
		person.setFileNumber(_number);
	}

	public String
	getFileNumberString()
	{
		int file_number = person.getFileNumber();
		if (file_number == 0)
			return "";
		return file_number + "";
	}
	
	public void
	setGroupUnderCareMemberType(GroupUnderCareMemberTypeBean _file_type)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
	{
		GroupUnderCareMemberTypeBean.setMemberType(this, _file_type);
		this.group_under_care_member_type = _file_type;
	}
	
	public void
	invalidateActiveSchedule()
	{
		active_schedule = null;
	}
	
	public void
	invalidateGroup()
	{
		this.group_under_care = null;
	}
	
	public boolean
	isInBlackHole()
	{
		return (person.getIsfacilitator() == (short)1);
	}
	
	public void
	setIsInBlackHole(boolean _is_in_black_hole)
	{
		person.setIsfacilitator(_is_in_black_hole ? (short)1 : (short)0);
	}

	public void
	setDefaultCommissions(String _product_commission, String _service_commission)
		throws TorqueException, Exception
	{
		/*
		 * <table name="PRACTITIONER_COMMISSION_SETTINGS_DB">
    <column name="PRACTITIONER_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="PRODUCT_COMMISSION" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="SERVICE_COMMISSION" required="true" scale="2" size="7" type="DECIMAL"/>

    <foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
		 */

		BigDecimal product_commission = new BigDecimal(_product_commission.isEmpty() ? "0" : _product_commission);
		BigDecimal service_commission = new BigDecimal(_service_commission.isEmpty() ? "0" : _service_commission);

		Criteria crit = new Criteria();
		crit.add(PractitionerCommissionSettingsDbPeer.PRACTITIONER_ID, this.getId());
		List objList = PractitionerCommissionSettingsDbPeer.doSelect(crit);
		if (objList.size() == 0)
		{
			crit.add(PractitionerCommissionSettingsDbPeer.PRODUCT_COMMISSION, product_commission);
			crit.add(PractitionerCommissionSettingsDbPeer.SERVICE_COMMISSION, service_commission);
			PractitionerCommissionSettingsDbPeer.doInsert(crit);
		}
		else
		{
			PractitionerCommissionSettingsDb obj = (PractitionerCommissionSettingsDb)objList.get(0);
			obj.setProductCommission(product_commission);
			obj.setServiceCommission(service_commission);
			obj.save();
		}
	}

	public String[]
	getDefaultCommissions()
		throws TorqueException, Exception
	{
		String[] arr = new String[2];

		Criteria crit = new Criteria();
		crit.add(PractitionerCommissionSettingsDbPeer.PRACTITIONER_ID, this.getId());
		List objList = PractitionerCommissionSettingsDbPeer.doSelect(crit);
		if (objList.size() == 0)
		{
			arr[0] = "";
			arr[1] = "";
		}
		else
		{
			PractitionerCommissionSettingsDb obj = (PractitionerCommissionSettingsDb)objList.get(0);
			arr[0] = obj.getProductCommission().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
			arr[1] = obj.getServiceCommission().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		}

		return arr;
	}
	
	public boolean
	isDeleted()
	{
		return (person.getFacilitatoractive() == (short)0);
	}
	
	public void
	setIsDeleted(boolean _deleted)
	{
		person.setFacilitatoractive(_deleted ? (short)0 : (short)1);
	}

	protected void
	insertObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
	{
		cat.debug("insertObject() invoked in PersonBean.");

		if (passwordRequired)
		{
			if (confirmPassword == null)
				throw new IllegalValueException("Please confirm your password.");
			if (!confirmPassword.equals(password))
				throw new IllegalValueException("The passwords entered do not match.");
		}

		// I need to ensure that there isn't another user with this Person's username...

		Criteria crit = new Criteria();
		crit.add(PersonPeer.FACILITATORACTIVE, 1);
		crit.add(PersonPeer.USERNAME, getUsername());
		List objList = PersonPeer.doSelect(crit);
		if (objList.size() > 0)
			throw new ObjectAlreadyExistsException("The selected username is already in use.  Please select another username.");

		if (passwordRequired)
		{
			if (!password.equals(person.getPassword()))
			{
			    /*
				CleartextPassword clearTextPassword = new CleartextPassword(password);
				person.setPassword(clearTextPassword.getEncryptedPassword());
			     */

			    person.setPassword(password);
			}
		}

		person.setCreationdate(new Date());
		person.setIsadministrator((short)0);
		person.setIsactive((short)1);
		person.setFacilitatoractive((short)1);
		if (person.getPersontype() == null)
			person.setPersontype(DEFAULT_NEW_USER_PERSON_TYPE);


		if (!CUBean.useAutoIncrementID())
			person.setPersonid(getRandomDatabaseID());

		/*
		String emp_id_string = "";
		if (person.getEmployeeid() != null)
		    emp_id_string = person.getEmployeeid() + "";

		if (emp_id_string.length() > 2)
		    person.setPersonid(Integer.parseInt(emp_id_string));
		else
		    person.setPersonid(getRandomDatabaseID());
		 */

		person.save();

		updateDepartmentAssociation();

		this.saveSupervisors();
		this.saveAddresses();
	}

	protected void
	verifyEnrollmentRequest(CourseBean _course)
		throws TorqueException, ObjectAlreadyExistsException, IllegalValueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
	{
		// determine if this user is already enrolled in the specified course...

		EnrollmentBean existing_enrollment = this.getEnrollment(_course);

		if (existing_enrollment != null)
		{
			if (existing_enrollment.getStatus().equals(EnrollmentBean.WAITLIST_ENROLLMENT_STATUS))
				throw new ObjectAlreadyExistsException(this.getFullName() + " is already on the wait list for " + _course.getNameString());
			else if (existing_enrollment.getStatus().equals(EnrollmentBean.DROPPED_ENROLLMENT_STATUS))
			{
				// do nothing for now, I guess
				if (!_course.allowEnrollAfterDrop())
					throw new ObjectAlreadyExistsException(this.getFullName() + " can't enroll in " + _course.getNameString() + " because this course was previously dropped.");
			}
			else if (existing_enrollment.getStatus().equals(EnrollmentBean.PENDING_ENROLLMENT_STATUS))
				throw new ObjectAlreadyExistsException(this.getFullName() + " already has an enrollment pending approval for " + _course.getNameString());
			else if (existing_enrollment.getStatus().equals(EnrollmentBean.DENIED_ENROLLMENT_STATUS))
				throw new ObjectAlreadyExistsException(this.getFullName() + " has already been denied enrollment for " + _course.getNameString());
			else
				throw new ObjectAlreadyExistsException(this.getFullName() + " is already enrolled in " + _course.getNameString());
		}

		// determine if there are prerequisites for this course

		// ecolab allows enrollment despite incomplete prereqs - 4/2/07 mws

		/*
		Iterator itr = _course.getCoursePrerequisites().iterator();
		while (itr.hasNext())
		{
			CourseBean courseObj = (CourseBean)itr.next();
			if (!this.hasCompleted(courseObj))
				throw new IllegalValueException(this.getFullName() + " cannot be enrolled in " + _course.getNameString() + ".  " + courseObj.getNameString() + " must be completed first.");
		}

		itr = _course.getCertificationPrerequisites().iterator();
		while (itr.hasNext())
		{
			CertificationBean certificationObj = (CertificationBean)itr.next();
			if (!this.hasCompleted(certificationObj))
				throw new IllegalValueException(this.getFullName() + " cannot be enrolled in " + _course.getNameString() + ".  " + certificationObj.getNameString() + " must be completed first.");
		}
		 */
	}

	public boolean
	updateBlackHoleStatus(BlackHoleBean _black_hole)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		System.out.println("updateBlackHoleStatus() invoked in UKOnlinePersonBean");

		//BlackHoleBean black_hole = BlackHoleBean.getInstance(this.getDepartment().getCompany());

		boolean black_hole_modified = false;

		Calendar now = Calendar.getInstance();

		Vector practice_areas_send_to_black_hole = new Vector();
		Vector practice_areas_remove_from_black_hole = new Vector();

		boolean send_to_black_hole_no_practice_area = false;

		/*
		PersonBean person_obj = (PersonBean)itr.next();
		UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person_obj.getId());
		 */

		//num_analyzed++;

		boolean has_checked_in_out_appointment = (AppointmentBean.hasAppointmentForClient(this, AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS) || AppointmentBean.hasAppointmentForClient(this, AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS));
		System.out.println("has_checked_in_out_appointment >" + has_checked_in_out_appointment);

		System.out.println("Analyzing " + this.getLabel() + " black hole >" + this.isInBlackHole() + " ... has_checked_out_appointment >" + has_checked_in_out_appointment);

		if (this.isActive())
		{
			if (has_checked_in_out_appointment)
			{
				Iterator care_details = CareDetailsBean.getCareDetails(this).iterator();

				System.out.println("care_details.hasNext() >" + care_details.hasNext());

				if (care_details.hasNext())
				{
					while (care_details.hasNext())
					{
						boolean send_to_black_hole = false;

						CareDetailsBean care_details_obj = (CareDetailsBean)care_details.next();
						PracticeAreaBean practice_area = care_details_obj.getPracticeArea();
						AppointmentBean last_appointment = AppointmentBean.getLastAppointmentForClientCheckedInOrCheckedOut(this, practice_area);
						System.out.println("last appointment " + practice_area.getLabel() + " >" + last_appointment);

						boolean has_future_scheduled_appointment = (AppointmentBean.getNextAppointmentForClient(this, practice_area, AppointmentBean.DEFAULT_APPOINTMENT_STATUS) != null);

						System.out.println("last_appointment >" + last_appointment);
						System.out.println("has_future_scheduled_appointment >" + has_future_scheduled_appointment);

						if (!care_details_obj.isPRN())
						{
							if (last_appointment == null)
							{
								System.out.println("pa has_future_scheduled_appointment " + practice_area.getLabel() + " >" + has_future_scheduled_appointment);
								if (!has_future_scheduled_appointment)
								{
									Date last_contact_date = null;

									try
									{
										ContactStatusBean contact_status = ContactStatusBean.getLastContactStatus(this, practice_area);
										last_contact_date = contact_status.getToDoDate();
									}
									catch (ObjectNotFoundException x)
									{
										//x.printStackTrace();
									}
									catch (Exception x)
									{
										x.printStackTrace();
									}

									send_to_black_hole = false;

									if (last_contact_date == null)
										send_to_black_hole = true;
									else
										send_to_black_hole = last_contact_date.before(now.getTime());
								}
							}
							else
							{
								// how long has it been since their last appointment?

								Calendar last_appt_date = Calendar.getInstance();
								last_appt_date.setTime(last_appointment.getAppointmentDate());

								System.out.println("last_appt_date str >" + CUBean.getUserDateString(last_appointment.getAppointmentDate()));

								Long days_since_last_appointment = new Long((now.getTime().getTime() - last_appt_date.getTime().getTime()) / 86400000);
								System.out.println("days since last appt >" + days_since_last_appointment.intValue());


								// how many times should this person have visited in this time?

								int foq_days = care_details_obj.getFrequencyOfCareDays();
								if (foq_days > 0)
								{
									//int missed_visits = days_since_last_appointment_or_contact.intValue() / foq_days;
									int missed_visits = days_since_last_appointment.intValue() / foq_days;

									System.out.println("missed_visits >" + missed_visits);

									if ((missed_visits >= com.badiyan.uk.online.tasks.BlackHoleMonitorTask.num_missed_appt_black_hole) && !has_future_scheduled_appointment)
									{
										send_to_black_hole = true;

										// they've missed enough visits to be put into the black hole, if there's a future contact, no need to black hole

										Date last_contact_date = null;

										try
										{
											ContactStatusBean contact_status = ContactStatusBean.getLastContactStatus(this, practice_area);
											System.out.println("last contact status found for " + this.getLabel() + " ,  " + contact_status.getLabel());
											last_contact_date = contact_status.getToDoDate();
										}
										catch (ObjectNotFoundException x)
										{
											//x.printStackTrace();
										}
										catch (Exception x)
										{
											x.printStackTrace();
										}

										if (last_contact_date != null)
											send_to_black_hole = last_contact_date.before(now.getTime());
									}
									else
										send_to_black_hole = false;
								}
								else
								{
									System.out.println("Invalid foq_days >" + foq_days);
								}
							}
						}

						if (send_to_black_hole)
							practice_areas_send_to_black_hole.addElement(practice_area);
						else
							practice_areas_remove_from_black_hole.addElement(practice_area);
					}
				}
				else
				{
					System.out.println("no care details found");

					// this person has appointments either checked in or checked out, but they have no care details established...  should I do anything with this?


				}
			}
			else
			{
				Iterator care_details_itr = CareDetailsBean.getCareDetails(this).iterator();

				//System.out.println(" _care_details_itr.hasNext() >" + care_details_itr.hasNext());

				if (care_details_itr.hasNext())
				{
					while (care_details_itr.hasNext())
					{
						CareDetailsBean care_details = (CareDetailsBean)care_details_itr.next();
						PracticeAreaBean practice_area = care_details.getPracticeArea();

						boolean has_future_scheduled_appointment = (AppointmentBean.getNextAppointmentForClient(this, practice_area, AppointmentBean.DEFAULT_APPOINTMENT_STATUS) != null);
						//System.out.println("has_future_scheduled_appointment " + practice_area.getLabel() + " >" + has_future_scheduled_appointment);
						if (!has_future_scheduled_appointment && !care_details.isPRN())
						{
							Date last_contact_date = null;

							try
							{
								ContactStatusBean contact_status = ContactStatusBean.getLastContactStatus(this, practice_area);
								last_contact_date = contact_status.getToDoDate();
							}
							catch (ObjectNotFoundException x)
							{
								//x.printStackTrace();
							}
							catch (Exception x)
							{
								x.printStackTrace();
							}

							boolean send_to_black_hole = false;

							if (last_contact_date == null)
								send_to_black_hole = true;
							else
								send_to_black_hole = last_contact_date.before(now.getTime());

							if (send_to_black_hole)
								practice_areas_send_to_black_hole.addElement(practice_area);
						}
						else
							practice_areas_remove_from_black_hole.addElement(practice_area);
					}
				}
				else
				{
					// no past appointments or care details.  should I worry about contact status?  sure...


					boolean has_future_scheduled_appointment = (AppointmentBean.getNextAppointmentForClient(this, AppointmentBean.DEFAULT_APPOINTMENT_STATUS) != null);

					//System.out.println("__has_future_scheduled_appointment >" + has_future_scheduled_appointment);

					if (!has_future_scheduled_appointment)
					{
						// no past or future appointments or care details...

						Date last_contact_date = null;

						try
						{
							ContactStatusBean contact_status = ContactStatusBean.getLastContactStatus(this); // find contact status for all practice areas
							last_contact_date = contact_status.getToDoDate();
						}
						catch (ObjectNotFoundException x)
						{
							//x.printStackTrace();
						}
						catch (Exception x)
						{
							x.printStackTrace();
						}

						boolean send_to_black_hole = false;

						if (last_contact_date == null)
							send_to_black_hole = true;
						else
						{
							// if the last contact date is in the past, black hole this user, otherwise no black hole

							send_to_black_hole = last_contact_date.before(now.getTime());
						}

						if (send_to_black_hole)
							send_to_black_hole_no_practice_area = true;
					}
					else
					{
						Iterator itr = AppointmentBean.getNextAppointmentsForClient(this, AppointmentBean.DEFAULT_APPOINTMENT_STATUS).iterator();
						while (itr.hasNext())
						{
							AppointmentBean appointment = (AppointmentBean)itr.next();
							PracticeAreaBean practice_area = appointment.getPracticeArea();
							if (!practice_areas_remove_from_black_hole.contains(practice_area))
								practice_areas_remove_from_black_hole.addElement(practice_area);
						}

					}
				}			

			}
		}

		boolean is_in_black_hole = (!practice_areas_send_to_black_hole.isEmpty() || send_to_black_hole_no_practice_area);

		if (send_to_black_hole_no_practice_area)
		{
			if (!_black_hole.containsNoPracticeArea(this))
			{
				_black_hole.add(this);
				black_hole_modified = true;
			}
		}
		else
		{
			// remove from black hole no practice area

			if (_black_hole.containsNoPracticeArea(this))
			{
				_black_hole.remove(this);
				black_hole_modified = true;
			}

			// is this person in the black hole now for any spe

			Vector black_hole_practice_areas = _black_hole.getPracticeAreas(this);

			// send to black hole for practice areas

			Iterator itr = practice_areas_send_to_black_hole.iterator();
			while (itr.hasNext())
			{
				PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
				if (!_black_hole.contains(this, practice_area))
				{
					_black_hole.add(this, practice_area);
					black_hole_modified = true;
				}
				black_hole_practice_areas.remove(practice_area);
			}

			// remove from black hole for practice areas

			practice_areas_remove_from_black_hole.addAll(black_hole_practice_areas);
			itr = practice_areas_remove_from_black_hole.iterator();
			while(itr.hasNext())
			{
				PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
				if (_black_hole.contains(this, practice_area))
				{
					_black_hole.remove(this, practice_area);
					black_hole_modified = true;
				}
			}
		}

		if (this.isInBlackHole())
		{
			if (!is_in_black_hole)
			{
				this.setIsInBlackHole(false);
				this.save();
			}
		}
		else
		{
			if (is_in_black_hole)
			{
				this.setIsInBlackHole(true);
				this.save();
			}
		}

		return black_hole_modified;
	}

	protected void
	updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
	{
		cat.debug("updateObject() invoked in PersonBean. - " + getMinimumPasswordLength());
		System.out.println("updateObject() invoked in PersonBean. - " + getMinimumPasswordLength());

		if ((password != null) && (password.equals(confirmPassword)) && (password.length() >= getMinimumPasswordLength()))
			// This should be modified to better reflect realistic business requirements for a password.  What is the actual minimum length?
		{
		    /*
			CleartextPassword clearTextPassword = new CleartextPassword(password);
			person.setPassword(clearTextPassword.getEncryptedPassword());
		     */

		    person.setPassword(password);
		}
		else
		{
			if (password != null)
			{
				if (!password.equals(confirmPassword))
					throw new IllegalValueException("The passwords do not match.  Please try again.");
				if (password.length() < getMinimumPasswordLength())
					throw new IllegalValueException("Your password is too short.  Your password must contain at least " + getMinimumPasswordLength() + " characters.");
			}
		}

		if (usernameUpdated)
		{
			//Person testUser = (Person)getObjectManager().selectOneObjectOfType("Person",
			//	"where isactive = 1 and username = '" + getUsername() + "'");

			/*
			Criteria crit = new Criteria();
			crit.add(PersonPeer.ISACTIVE, 1);
			crit.add(PersonPeer.USERNAME, getUsername());
			List objList = PersonPeer.doSelect(crit);

			if (testUser != null)
				throw new ObjectAlreadyExistsException("The selected username is already in use.  Please select another username.");
			 */
		}

		updateDepartmentAssociation();

		//System.out.println("UPDATING " + this.getFullName() + " TITLE >" + person.getTitle());
		
		person.setModificationdate(new Date());

		person.save();

		this.saveSupervisors();
		this.saveAddresses();
		this.saveRoles();
		this.saveAudiences();
	}

	// EVENTS

	public void
	newAudience(com.badiyan.uk.events.NewAudienceEvent _event)
	{
		cat.debug("newAudience() invoked in UKOnlinePersonBean - " + _event);
		recommendedCourses = null;
		recommendedCertifications = null;
	}

	public void
	updatedAudience(UpdatedAudienceEvent _event)
	{
		cat.debug("updatedAudience() invoked in UKOnlinePersonBean - " + _event);
		recommendedCourses = null;
		recommendedCertifications = null;
	}
	
	
	private Vector customerAddEventListeners;
	
	public void
	addQBCustomerAddEventListener(QBCustomerAddEventListener _listener)
	{
		if (customerAddEventListeners == null)
			customerAddEventListeners = new Vector();

        customerAddEventListeners.addElement(_listener);
    }
	
	public void
	notifyQBCustomerAddEvent()
	{
		System.out.println("notifyQBCustomerAddEvent() invoked in UKOnlinePersonBean - " + customerAddEventListeners);
		
		if (customerAddEventListeners != null)
		{
			Vector temp = null;
			synchronized (this) { temp = (Vector)customerAddEventListeners.clone(); }
			QBCustomerAddEvent event = new QBCustomerAddEvent(this);
			Iterator itr = temp.iterator();
			while (itr.hasNext())
			{
				QBCustomerAddEventListener listener = (QBCustomerAddEventListener)itr.next();
				listener.customerAdded(event);
			}
		}
	}
	
	public boolean
	hasValidEmailAddress() {
		String str = this.getEmail1String();
		if (str.isEmpty()) {
			return false;
		}
		
		return true;
	}
	
	public String
	getLastLogInDateString() throws TorqueException {
		Date last_log_in_date = super.getLastLogInDate();
		//System.out.println("last_log_in_date >" + last_log_in_date);
		String days_ago_str = "";
		String last_log_in_date_str = "Never";
		if (last_log_in_date != null)
		{
			last_log_in_date_str = CUBean.getUserDateString(last_log_in_date);
			Calendar midnight = Calendar.getInstance();
			midnight.set(Calendar.HOUR_OF_DAY, 0);
			midnight.set(Calendar.MINUTE, 0);
			midnight.set(Calendar.SECOND, 0);
			//long diff = now2.getTime() - last_log_in_date.getTime();
			long diff = midnight.getTimeInMillis() - last_log_in_date.getTime();
			long days_ago = diff / (1000 * 60 * 60 * 24);
			if (diff < 0)
				days_ago_str = " (today)";
			else if (days_ago == 0)
				days_ago_str = " (yesterday)";
			else
				days_ago_str = " (" + (days_ago + 1) + " days ago)";
		}
		return (last_log_in_date_str + days_ago_str);
	}


	private boolean randomize = false;
	
	public String getLabel() {
		
		if (randomize) {
		
			//System.out.println("getLabel() invoked >" + UKOnlinePersonBean.first_names);

			if (UKOnlinePersonBean.first_names == null)
			{
				UKOnlinePersonBean.first_names = new Vector();
				UKOnlinePersonBean.last_names = new Vector();

				try {
					Criteria crit = new Criteria();
					Iterator itr = PersonPeer.doSelect(crit).iterator();
					System.out.println("itr.hasNext() >" + itr.hasNext());
					while (itr.hasNext()) {
						Person obj = (Person)itr.next();
						if (obj.getFirstname() != null && obj.getLastname() != null) {
							UKOnlinePersonBean.first_names.addElement(obj.getFirstname());
							UKOnlinePersonBean.last_names.addElement(obj.getLastname());
						}
					}
				}
				catch (Exception x) {
					x.printStackTrace();
				}

			}

			if (randomName == null)
			{

				int num_first = num_first = UKOnlinePersonBean.first_names.size();
				int num_last = num_last = UKOnlinePersonBean.last_names.size();

				Random randy = new Random();
				int rand_1 = Math.abs(randy.nextInt());
				int rand_2 = Math.abs(randy.nextInt());
				randomName = (String)UKOnlinePersonBean.first_names.elementAt(rand_1 % num_first) + " " + (String)UKOnlinePersonBean.last_names.elementAt(rand_2 % num_last);

			}

			return randomName;
		
		}
		
		String label = super.getLabel();
		if (label.trim().isEmpty()) {
			return this.getEmail1String();
		} else {
			return label;
		}
		
	}
	
	public String getLabelWithEmail() {
		
		String label = super.getLabel();
		if (label.trim().isEmpty()) {
			return this.getEmail1String();
		} else {
			return label + " - " + this.getEmail1String();
		}
		
	}

	public boolean isStaff() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return this.hasRole(UKOnlineRoleBean.CASHIER_ROLE_NAME);
	}

	public boolean isPractitioner() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return this.hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME);
	}

	public boolean isRoom() {
		return (person.getIsadministrator() == (short)1);
	}

	public void setIsRoom(boolean _b) {
		person.setIsadministrator(_b ? (short)1 : (short)0);
	}
	
	

	public boolean
	showPractitioner()
	{
		return person.getOksendemail() > (short)0;
	}
	
	private PractitionerSettingsDb practitioner_settings = null;
	private PractitionerSettingsDb
	getPractitionerSettings() throws TorqueException, UniqueObjectNotFoundException, Exception {
		if (practitioner_settings == null) {
			Criteria crit = new Criteria();
			crit.add(PractitionerSettingsDbPeer.PRACTITIONER_ID, this.getId());
			List<PractitionerSettingsDb> l = PractitionerSettingsDbPeer.doSelect(crit);
			if (l.size() == 1) {
				practitioner_settings = l.get(0);
			} else if (l.isEmpty()) {
				practitioner_settings = new PractitionerSettingsDb();
				practitioner_settings.setPractitionerId(this.getId());
				practitioner_settings.save();
			} else {
				throw new UniqueObjectNotFoundException("Multiple Practitioner settings found for >" + this.getLabel());
			}
		}
		return practitioner_settings;
	}
	
	public boolean
	allowClientScheduling() throws TorqueException, UniqueObjectNotFoundException, Exception {
		return (this.getPractitionerSettings().getAllowClientScheduling() == (short)1);
	}
	
	public void
	setAllowClientScheduling(boolean _b) throws TorqueException, UniqueObjectNotFoundException, Exception {
		this.getPractitionerSettings().setAllowClientScheduling(_b ? (short)1 : (short)0);
		this.getPractitionerSettings().save();
	}
	
	
}