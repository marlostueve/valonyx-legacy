package com.badiyan.uk.online.beans;

import com.badiyan.corpu.utility.*;
import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.corpu.manager.*;

import java.lang.*;
import java.io.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 * This class is implemented as a "singleton".  We only have
 * once instance which handles all system access functions.
 */
public class
UKOnlineAccessManager
	extends AccessManager
{
	// CLASS METHODS
	
	/**
	 * The authorized "constructor".
	 */
	public synchronized static AccessManager
	getInstance()
	{
		System.out.println("getInstance() invoked in UKOnlineAccessManager");
		if ((AccessManager.onlyInstance == null) || (AccessManager.onlyInstance instanceof AccessManager))
		{
			AccessManager.onlyInstance = new UKOnlineAccessManager();
		}

		return AccessManager.onlyInstance;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return com.badiyan.corpu.business.Person
	 * @param username java.lang.String
	 * @param password java.lang.String
	 */
	public PersonBean
	getUserWithCredentials(String _username, String _password)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		UKOnlinePersonBean testPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_username);
		if (testPerson != null)
		{
			// See if password also matches...
		    
		    
		    
			
			CiphertextPassword storedPw = new CiphertextPassword(testPerson.getPassword());
			
			
			
			if (storedPw.matchesCleartextPassword(_password))
				return testPerson;
			else
			{
			    // maybe the password is clear text???...
			    
			    CleartextPassword clearTextPassword = new CleartextPassword(testPerson.getPassword());
			    storedPw = new CiphertextPassword(clearTextPassword.getEncryptedPassword());
			    
			    
			    if (storedPw.matchesCleartextPassword(_password))
				return testPerson;
			    
			}
		}
		
		throw new IllegalValueException("Login Failed, Please Try Again");
	}
	
	
	
	
	public PersonBean
	getUserWithCredentialsByEmailNoEncryption(String _email, String _password)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		System.out.println("getUserWithCredentialsByEmailNoEncryption(String,String) invoked >" + _email + ", " + _password);
		UKOnlinePersonBean testPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPersonByEmail(_email);
		System.out.println("testPerson >" + testPerson);
		if (testPerson != null)
		{
			// See if password also matches...
			
			System.out.println("testPerson.getPasswordString() >" + testPerson.getPasswordString());
			System.out.println("_password >" + _password);
			
			if (testPerson.getPasswordString().equals(_password))
			    return testPerson;
		}
		
		throw new IllegalValueException("Login Failed, Please Try Again");
	}
	
	public PersonBean
	getUserWithCredentialsNoEncryption(String _username, String _password)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		System.out.println("getUserWithCredentialsNoEncryption(String,String) invoked >" + _username + ", " + _password);
		UKOnlinePersonBean testPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_username);
		System.out.println("testPerson >" + testPerson);
		if (testPerson != null)
		{
			// See if password also matches...
			
			System.out.println("testPerson.getPasswordString() >" + testPerson.getPasswordString());
			System.out.println("_password >" + _password);
			
			if (testPerson.getPasswordString().equals(_password))
			    return testPerson;
		}
		
		throw new IllegalValueException("Login Failed, Please Try Again");
	}
	
	public PersonBean
	getUserWithCredentialsNoEncryptionPasswordOnly(String _password)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		//System.out.println("getUserWithCredentialsNoEncryptionPasswordOnly(String) invoked >" + _password);
		UKOnlinePersonBean testPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPersonByPassword(_password);
		//System.out.println("testPerson >" + testPerson);
		if (testPerson != null)
		{
			// See if password also matches...
			
			//System.out.println("testPerson.getPasswordString() >" + testPerson.getPasswordString());
			//System.out.println("_password >" + _password);
			
			if (testPerson.getPasswordString().equals(_password))
			    return testPerson;
			
			if (testPerson.getPasswordString().equals(_password.toUpperCase()))
			    return testPerson;
		}
		
		throw new IllegalValueException("Login Failed, Please Try Again");
	}
}