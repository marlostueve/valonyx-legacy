/*
 * EcolabPersonTitleBean.java
 *
 * Created on March 20, 2007, 4:13 PM
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

/**
 *
 * @author marlo
 */
public class
EcolabPersonTitleBean
    extends PersonTitleBean
    implements java.io.Serializable
{
    // CLASS VARIABLES
    
    public static final String TM_PERSON_TITLE_NAME = "Territory Manager";
    public static final String DM_PERSON_TITLE_NAME = "District Manager";
    public static final String AM_PERSON_TITLE_NAME = "Area Manager";
    public static final String RM_PERSON_TITLE_NAME = "Route Manager";
    public static final String RS_PERSON_TITLE_NAME = "Route Supervisor";
    public static final String ARM_PERSON_TITLE_NAME = "Area Route Manager";
    public static final String FTM_PERSON_TITLE_NAME = "Field Training Manager";
    public static final String RVP_PERSON_TITLE_NAME = "Regional Vice President of Sales";
    
    public static final String STUDENT_TITLE_NAME = "Student";
    public static final String DEPARTMENT_MANAGER_TITLE_NAME = "Department Manager";
    public static final String TRAINING_MANAGER_TITLE_NAME = "Training Manager";
    
    // INSTANCE METHODS
    
    protected void
    insertObject()
	throws ObjectAlreadyExistsException, IllegalValueException
    {
	super.insertObject();
    }
    
    protected void
    updateObject()
	throws ObjectAlreadyExistsException, IllegalValueException
    {
	super.updateObject();
    }
    
}
