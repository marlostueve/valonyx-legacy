package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class UKOnlineNewsItemSearchBean extends NewsItemSearchBean implements java.io.Serializable
{
	// CONSTRUCTORS

	public UKOnlineNewsItemSearchBean()
	{
		super();
	}

	// INSTANCE METHODS

	protected CUBean
	getNewListBeanItem(Object _obj)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		Newsitem obj = (Newsitem)_obj;
		return (UKOnlineNewsItemBean)UKOnlineNewsItemBean.getNewsItem(obj);
	}
}