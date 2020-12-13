package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author  marlo
 */
public class UKOnlineNewsItemBean extends NewsItemBean implements java.io.Serializable
{
	// CLASS METHODS

	public static NewsItemBean
	getNewsItem(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);

		UKOnlineNewsItemBean newsItem = (UKOnlineNewsItemBean)idNewsItems.get(key);
		if (newsItem == null)
		{
			// category not found in hash.  grab it from the database
			Criteria crit = new Criteria();
			crit.add(NewsitemPeer.NEWSITEMID, _id);
			List objList = NewsitemPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate news item with id: " + _id);
			newsItem = (UKOnlineNewsItemBean)UKOnlineNewsItemBean.getNewsItem((Newsitem)objList.get(0));
		}

		return newsItem;
	}

	protected static NewsItemBean
	getNewsItem(Newsitem _newsItem)
		throws TorqueException
	{
		System.out.println("getNewsItem(Newsitem) invoked in UKOnlineNewsItemBean");
		//NumberKey key = _department.getDepartmentid();
		Integer key = new Integer(_newsItem.getNewsitemid());

		UKOnlineNewsItemBean newsItem = (UKOnlineNewsItemBean)idNewsItems.get(key);
		if (newsItem == null)
		{
			newsItem = new UKOnlineNewsItemBean(_newsItem);
			idNewsItems.put(key, newsItem);
		}

		return newsItem;
	}

	public static Vector
	getNewsItems(boolean _active)
		throws TorqueException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		if (_active)
			crit.add(NewsitemPeer.EXPIRATIONDATE, new Date(), Criteria.GREATER_THAN);
		else
			crit.add(NewsitemPeer.EXPIRATIONDATE, new Date(), Criteria.LESS_THAN);
		crit.addDescendingOrderByColumn(NewsitemPeer.CREATIONDATE);
		List objList = NewsitemPeer.doSelect(crit);
		Iterator itr = objList.iterator();
		while (itr.hasNext())
		{
			Newsitem newsItem = (Newsitem)itr.next();
			vec.addElement(getNewsItem(newsItem));
		}
		return vec;
	}

	public static Vector
	getNewsItems(PersonGroupBean _personGroup)
		throws TorqueException
	{
		System.out.println("getNewsItems(PersonGroupBean, int) invoked in UKOnlineNewsItemBean");
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(NewsitemPeer.PERSON_GROUP_ID, _personGroup.getId());
		//crit.add(NewsitemPeer.EXPIRATIONDATE, new Date(), Criteria.GREATER_THAN);
		crit.add(NewsitemPeer.EFFECTIVEDATE, new Date(), Criteria.LESS_THAN);
		crit.addDescendingOrderByColumn(NewsitemPeer.CREATIONDATE);
		List objList = NewsitemPeer.doSelect(crit);
		Iterator itr = objList.iterator();
		while (itr.hasNext())
		{
			Newsitem newsItem = (Newsitem)itr.next();
			vec.addElement(getNewsItem(newsItem));
		}
		return vec;
	}

	public static Vector
	getNewsItems(PersonGroupBean _personGroup, int _numItems)
		throws TorqueException
	{
		System.out.println("getNewsItems(PersonGroupBean, int) invoked in UKOnlineNewsItemBean");
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(NewsitemPeer.PERSON_GROUP_ID, _personGroup.getId());
		crit.add(NewsitemPeer.EXPIRATIONDATE, new Date(), Criteria.GREATER_THAN);
		crit.add(NewsitemPeer.EFFECTIVEDATE, new Date(), Criteria.LESS_THAN);
		crit.addDescendingOrderByColumn(NewsitemPeer.CREATIONDATE);
		crit.setLimit(_numItems);
		List objList = NewsitemPeer.doSelect(crit);
		Iterator itr = objList.iterator();
		while (itr.hasNext())
		{
			Newsitem newsItem = (Newsitem)itr.next();
			vec.addElement(getNewsItem(newsItem));
		}
		return vec;
	}

	public static Vector
	getNewsItems(PersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		List personGroups = PersonGroupBean.getPersonGroups(_person);
		if (personGroups.size() == 0)
			throw new ObjectNotFoundException("Can't find PersonGroup for Person - " + _person.getFullName());
		if (personGroups.size() > 1)
			throw new UniqueObjectNotFoundException("Can't find unique PersonGroup for Person - " + _person.getFullName());
		return UKOnlineNewsItemBean.getNewsItems((PersonGroupBean)personGroups.get(0));
	}

	public static Vector
	getNewsItems(PersonBean _person, int _numItems)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		List personGroups = PersonGroupBean.getPersonGroups(_person);
		if (personGroups.size() == 0)
			throw new ObjectNotFoundException("Can't find PersonGroup for Person - " + _person.getFullName());
		if (personGroups.size() > 1)
			throw new UniqueObjectNotFoundException("Can't find unique PersonGroup for Person - " + _person.getFullName());
		return UKOnlineNewsItemBean.getNewsItems((PersonGroupBean)personGroups.get(0), _numItems);
	}

	// CONSTRUCTORS

	public UKOnlineNewsItemBean() throws TorqueException
	{
		super();
	}

	public UKOnlineNewsItemBean(Newsitem _newsItem) throws TorqueException
	{
		super(_newsItem);
	}

	// INSTANCE METHODS

	public String
	getNewsItemName()
	{
		return (String)super.getProperty("news_item_name");
	}

	public String
	getName()
	{
		return (String)super.getProperty("news_item_name");
	}

	public String
	getPublishedDateString()
	{
		return super.getCreationDateString();
	}

	public String
	getReleasedDateString()
	{
		return super.getEffectiveDateString();
	}

	public String
	getShortDescription()
	{
		return (String)super.getProperty("short_description");
	}

	public String
	getNewsText()
	{
		return (String)super.getProperty("news_text");
	}

	public String
	getURLString()
	{
		//System.out.println("getURLString() invoked in UKOnlineNewsItemBean - " + (String)super.getProperty("URL"));
		return (String)super.getProperty("URL");
	}

	public void
	setNewsItemName(String _string)
	{
		super.setProperty("news_item_name", _string);
	}

	public void
	setShortDescription(String _string)
	{
		super.setProperty("short_description", _string);
	}

	public void
	setNewsText(String _string)
	{
		super.setProperty("news_text", _string);
	}

	public void
	setReleasedDate(Date _releasedDate)
	{
		super.setEffectiveDate(_releasedDate);
	}

	public void
	setURLString(String _string)
	{
		super.setProperty("URL", _string);
	}

	/*
	public void
	setUserGroup(DepartmentBean _userGroup)
		throws TorqueException
	{
		super.setDepartment(_userGroup);
	}
	 */

}
