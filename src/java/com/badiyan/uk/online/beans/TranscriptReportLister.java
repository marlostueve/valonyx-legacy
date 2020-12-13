package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
TranscriptReportLister
	extends SearchBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

	public static final short USER_ASC_SORT = 1;
	public static final short USER_DESC_SORT = 2;
	public static final short GROUP_ASC_SORT = 3;
	public static final short GROUP_DESC_SORT = 4;
	public static final short LOCATION_ASC_SORT = 5;
	public static final short LOCATION_DESC_SORT = 6;
	public static final short JOB_ASC_SORT = 7;
	public static final short JOB_DESC_SORT = 8;

	// INSTANCE VARIABLES

	private short sort;

	// CONSTRUCTORS

	public
	TranscriptReportLister()
	{
		sort = TranscriptReportLister.USER_ASC_SORT;
	}

	// INSTANCE METHODS

	protected void
	getAllList()
		throws TorqueException
	{
	}

	protected CUBean
	getNewListBeanItem(Object _obj)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		return null;
	}

	public void
	search()
		throws TorqueException
	{
		boolean needs_secondary_sort = true;

		Criteria crit = new Criteria();
		switch (sort)
		{
			case TranscriptReportLister.USER_ASC_SORT:
			{
				crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
				needs_secondary_sort = false;
			}
			break;
			case TranscriptReportLister.USER_DESC_SORT:
			{
				crit.addDescendingOrderByColumn(PersonPeer.LASTNAME);
				needs_secondary_sort = false;
			}
			break;
			case TranscriptReportLister.GROUP_ASC_SORT:
			{
				crit.addJoin(PersonPeer.PERSONID, PersonGroupMemberPeer.PERSONID);
				crit.addAscendingOrderByColumn(PersonGroupMemberPeer.PERSON_GROUP_ID);
			}
			break;
			case TranscriptReportLister.GROUP_DESC_SORT:
			{
				crit.addJoin(PersonPeer.PERSONID, PersonGroupMemberPeer.PERSONID);
				crit.addDescendingOrderByColumn(PersonGroupMemberPeer.PERSON_GROUP_ID);
			}
			break;
			case TranscriptReportLister.LOCATION_ASC_SORT:
			{
				crit.addJoin(PersonPeer.PERSONID, DepartmentpersonPeer.PERSONID);
				crit.addJoin(DepartmentpersonPeer.DEPARTMENTID, DepartmentPeer.DEPARTMENTID);
				crit.addAscendingOrderByColumn(DepartmentPeer.DEPARTMENTNAME);
			}
			break;
			case TranscriptReportLister.LOCATION_DESC_SORT:
			{
				crit.addJoin(PersonPeer.PERSONID, DepartmentpersonPeer.PERSONID);
				crit.addJoin(DepartmentpersonPeer.DEPARTMENTID, DepartmentPeer.DEPARTMENTID);
				crit.addDescendingOrderByColumn(DepartmentPeer.DEPARTMENTNAME);
			}
			break;
			case TranscriptReportLister.JOB_ASC_SORT:
			{
				crit.addAscendingOrderByColumn(PersonPeer.PERSON_TITLE_ID);
			}
			break;
			case TranscriptReportLister.JOB_DESC_SORT:
			{
				crit.addDescendingOrderByColumn(PersonPeer.PERSON_TITLE_ID);
			}
			break;
		}

		if (needs_secondary_sort)
			crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);

		System.out.println("crit >" + crit.toString());

		list = PersonPeer.doSelect(crit);

	}

	public void
	searchByKeyword(String _keyword)
		throws TorqueException
	{
	}

	public List
	getListStuff()
	{
		return list;
	}

	public short
	getSort()
	{
		return sort;
	}

	public void
	setSort(short _sort)
	{
		sort = _sort;
	}
}