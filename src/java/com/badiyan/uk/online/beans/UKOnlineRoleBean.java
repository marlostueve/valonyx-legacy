package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.events.*;
import com.badiyan.uk.interfaces.*;

import com.badiyan.torque.*;

import java.beans.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.om.NumberKey;
import org.apache.torque.util.Criteria;

/**
 * Created on February 5, 2003, 12:00 PM
 *
 * @author  marlo
 * @version
 */
public class
UKOnlineRoleBean
    extends RoleBean
    implements java.io.Serializable
{
    // CLASS VARIABLES
    
    public static final String DEPARTMENT_ADMINISTRATOR_ROLE_NAME = "Department Administrator";
    
    public static final String PRACTITIONER_ROLE_NAME = "Practitioner";
    public static final String CASHIER_ROLE_NAME = "Cashier";
    public static final String CLIENT_ROLE_NAME = "Client";
	
    public static final String CUSTOMER_SERVICE_ROLE_NAME = "Customer Service";
    
    // CLASS METHODS
    
    public static Vector
    getAssignableRoles(PersonBean _person)
	    throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
	    //System.out.println("getRoles() invoked in RoleBean - " + _person.getName());

	    Vector roles = new Vector();
	    if (_person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
	    {
			roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME));
			roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.CASHIER_ROLE_NAME));
			roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.CLIENT_ROLE_NAME));
		
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.STUDENT_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.INSTRUCTOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.MANAGER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.COURSE_OWNER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.RESOURCE_OWNER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.CERTIFICATION_OWNER_ROLE_NAME));
	    }
	    else if (_person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
	    {
			roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME));
			roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.CASHIER_ROLE_NAME));
			roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.CLIENT_ROLE_NAME));
		
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.STUDENT_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.INSTRUCTOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.MANAGER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.COURSE_OWNER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.RESOURCE_OWNER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.CERTIFICATION_OWNER_ROLE_NAME));
	    }
	    else if (_person.hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME))
	    {
			roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME));
			roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.CASHIER_ROLE_NAME));
			roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.CLIENT_ROLE_NAME));
		
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.STUDENT_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.INSTRUCTOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.MANAGER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.COURSE_OWNER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.RESOURCE_OWNER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.CERTIFICATION_OWNER_ROLE_NAME));
	    }

	    return roles;
    }
	
    public static Vector
    getEssentialAssignableRoles(PersonBean _person)
	    throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
	    //System.out.println("getEssentialAssignableRoles() invoked in RoleBean - " + _person.getName() + " -- " + _person.getRolesString() + " -- " + _person.getId());
		

	    Vector roles = new Vector();
	    if (_person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME)) {
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME));
			
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME));
			/*
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.STUDENT_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.INSTRUCTOR_ROLE_NAME));
			*/
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.MANAGER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.CUSTOMER_SERVICE_ROLE_NAME));

	    } else if (_person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME)) {
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME));
			/*
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.STUDENT_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.INSTRUCTOR_ROLE_NAME));
			*/
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.MANAGER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.CUSTOMER_SERVICE_ROLE_NAME));

	    } else if (_person.hasRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME)) {
		    roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME));
			/*
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.STUDENT_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.INSTRUCTOR_ROLE_NAME));
			*/
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.MANAGER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.CUSTOMER_SERVICE_ROLE_NAME));
			
			
	    } else if (_person.hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME)) {
			
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME));
			/*
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.STUDENT_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.INSTRUCTOR_ROLE_NAME));
			*/
		    roles.addElement(UKOnlineRoleBean.getRole(RoleBean.MANAGER_ROLE_NAME));
		    roles.addElement(UKOnlineRoleBean.getRole(UKOnlineRoleBean.CUSTOMER_SERVICE_ROLE_NAME));
	    }

	    return roles;
    }
    
    public static RoleBean
    getRole(String _name)
	throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
	System.out.println("getRole(String) invoked in RoleBean - " + _name);
	
	// First look for the role in the Hashtable
	
	RoleBean role = (RoleBean)roles.get(_name);
	if (role == null)
	{
	    System.out.println("looking in db for role...");
	    // The named role was not located in the hashtable.  Search the db for it.
	    
	    Criteria crit = new Criteria();
	    crit.add(RolePeer.NAME, _name);
	    List objList = RolePeer.doSelect(crit);
	    if (objList.size() == 0)
		throw new ObjectNotFoundException("Could not locate role with name: " + _name);
	    if (objList.size() > 1)
		throw new UniqueObjectNotFoundException("Multiple roles found for name: " + _name);
	    
	    System.out.println("roles found - " + objList.size());
	    
	    Role testRole = (Role)objList.get(0);
	    System.out.println("role found - " + testRole);
	    if (testRole == null)
		throw new ObjectNotFoundException("Could not locate role with name: " + _name);
	    role = new UKOnlineRoleBean(testRole);
	    roles.put(_name, role);
	}
	
	return role;
    }
    
    
    
    public static RoleBean
    getRole(Role _role)
	throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
	System.out.println("getRole(Role) invoked in RoleBean - " + _role);
	
	// First look for the role in the Hashtable
	
	RoleBean role = (RoleBean)roles.get(_role.getName());
	if (role == null)
	{
	    role = new UKOnlineRoleBean(_role);
	    roles.put(_role.getName(), role);
	}
	
	return role;
    }
    
    public static Vector
    getRoles()
	throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
	Vector vec = new Vector();
	
	// First look for the role in the Hashtable
	
	Criteria crit = new Criteria();
	crit.addDescendingOrderByColumn(RolePeer.NAME);
	List objList = RolePeer.doSelect(crit);
	Iterator itr = objList.iterator();
	while (itr.hasNext())
	{
	    Role role = (Role)itr.next();
	    UKOnlineRoleBean rb = (UKOnlineRoleBean)UKOnlineRoleBean.getRole(role);
	    vec.addElement(rb);
	}
	
	return vec;
    }
    
    public static Vector
    getRoles(PersonBean _person)
	throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
	Vector vec = new Vector();
	
	// First look for the role in the Hashtable
	
	Criteria crit = new Criteria();
	crit.add(PersonrolePeer.PERSONID, _person.getId());
	List objList = PersonrolePeer.doSelect(crit);
	Iterator itr = objList.iterator();
	while (itr.hasNext())
	{
	    Personrole personRole = (Personrole)itr.next();
	    UKOnlineRoleBean rb = (UKOnlineRoleBean)getRole(personRole.getRole());
	    vec.addElement(rb);
	}
	
	return vec;
    }
    
    public static void
    maintainDefaultData()
	throws TorqueException, IllegalValueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
    {
		RoleBean.maintainRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME);
		RoleBean.maintainRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);
		RoleBean.maintainRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME);

		RoleBean.maintainRole(RoleBean.MANAGER_ROLE_NAME);
		RoleBean.maintainRole(RoleBean.STUDENT_ROLE_NAME);
		RoleBean.maintainRole(RoleBean.INSTRUCTOR_ROLE_NAME);

		RoleBean.maintainRole(RoleBean.COURSE_OWNER_ROLE_NAME);
		RoleBean.maintainRole(RoleBean.RESOURCE_OWNER_ROLE_NAME);
		RoleBean.maintainRole(RoleBean.CERTIFICATION_OWNER_ROLE_NAME);

		RoleBean.maintainRole(RoleBean.CERTIFICATION_OWNER_ROLE_NAME);

		RoleBean.maintainRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME);
		RoleBean.maintainRole(UKOnlineRoleBean.CASHIER_ROLE_NAME);
		RoleBean.maintainRole(UKOnlineRoleBean.CLIENT_ROLE_NAME);
		
		RoleBean.maintainRole(UKOnlineRoleBean.CUSTOMER_SERVICE_ROLE_NAME);
    }
    
    // INSTANCE VARIABLES
    
    // CONSTRUCTORS
    
    /**
     * The base, no-arg constructor.
     */
    public UKOnlineRoleBean()
    {
		super();
    }
    
    /**
     * Construct a RoleBean from an existing Role.
     */
    public UKOnlineRoleBean(Role _role)
    {
		super(_role);
    }
    
    // INSTANCE METHODS
    
    protected void
    insertObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		super.insertObject();
    }
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		super.updateObject();
    }
}