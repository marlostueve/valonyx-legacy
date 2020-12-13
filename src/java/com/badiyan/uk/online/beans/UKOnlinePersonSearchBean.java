package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class UKOnlinePersonSearchBean extends PersonSearchBean implements java.io.Serializable
{
	// CONSTRUCTORS

	public UKOnlinePersonSearchBean()
	{
		super();
	}

	// INSTANCE METHODS

	protected CUBean
	getNewListBeanItem(Object _obj)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		Person person = (Person)_obj;
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person);
	}
}