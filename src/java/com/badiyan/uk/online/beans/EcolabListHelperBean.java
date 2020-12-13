/*
 * EcolabListHelperBean.java
 *
 * Created on March 21, 2007, 1:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

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
 *
 * @author marlo
 */
public class
EcolabListHelperBean
    extends ListHelperBean
    implements java.io.Serializable
{

	public List
	getRoles()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return UKOnlineRoleBean.getRoles();
	}
	
	public List
	getAssignableRoles()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (person == null)
			return new Vector();
		return UKOnlineRoleBean.getAssignableRoles(person);
	}
    
}
