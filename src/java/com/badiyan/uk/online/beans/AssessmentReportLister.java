package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
AssessmentReportLister
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
	public static final short SCORE_ASC_SORT = 9;
	public static final short SCORE_DESC_SORT = 10;
	public static final short STATUS_ASC_SORT = 11;
	public static final short STATUS_DESC_SORT = 12;
	public static final short ASS_NAME_ASC_SORT = 13;
	public static final short ASS_NAME_DESC_SORT = 14;

	// INSTANCE VARIABLES

	private short sort;

	// CONSTRUCTORS

	public
	AssessmentReportLister()
	{
		sort = AssessmentReportLister.USER_ASC_SORT;
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
			case AssessmentReportLister.USER_ASC_SORT:
			{
				crit.addJoin(AssessmentpersonPeer.PERSONID, PersonPeer.PERSONID);
				crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
				needs_secondary_sort = false;
			}
			break;
			case AssessmentReportLister.USER_DESC_SORT:
			{
				crit.addJoin(AssessmentpersonPeer.PERSONID, PersonPeer.PERSONID);
				crit.addDescendingOrderByColumn(PersonPeer.LASTNAME);
				needs_secondary_sort = false;
			}
			break;
			case AssessmentReportLister.GROUP_ASC_SORT:
			{
				crit.addJoin(AssessmentpersonPeer.PERSONID, PersonPeer.PERSONID);
				crit.addJoin(PersonPeer.PERSONID, PersonGroupMemberPeer.PERSONID);
				crit.addAscendingOrderByColumn(PersonGroupMemberPeer.PERSON_GROUP_ID);
			}
			break;
			case AssessmentReportLister.GROUP_DESC_SORT:
			{
				crit.addJoin(AssessmentpersonPeer.PERSONID, PersonPeer.PERSONID);
				crit.addJoin(PersonPeer.PERSONID, PersonGroupMemberPeer.PERSONID);
				crit.addDescendingOrderByColumn(PersonGroupMemberPeer.PERSON_GROUP_ID);
			}
			break;
			case AssessmentReportLister.LOCATION_ASC_SORT:
			{
				crit.addJoin(AssessmentpersonPeer.PERSONID, PersonPeer.PERSONID);
				crit.addJoin(PersonPeer.PERSONID, DepartmentpersonPeer.PERSONID);
				crit.addJoin(DepartmentpersonPeer.DEPARTMENTID, DepartmentPeer.DEPARTMENTID);
				crit.addAscendingOrderByColumn(DepartmentPeer.DEPARTMENTNAME);
			}
			break;
			case AssessmentReportLister.LOCATION_DESC_SORT:
			{
				crit.addJoin(AssessmentpersonPeer.PERSONID, PersonPeer.PERSONID);
				crit.addJoin(PersonPeer.PERSONID, DepartmentpersonPeer.PERSONID);
				crit.addJoin(DepartmentpersonPeer.DEPARTMENTID, DepartmentPeer.DEPARTMENTID);
				crit.addDescendingOrderByColumn(DepartmentPeer.DEPARTMENTNAME);
			}
			break;
			case AssessmentReportLister.JOB_ASC_SORT:
			{
				crit.addJoin(AssessmentpersonPeer.PERSONID, PersonPeer.PERSONID);
				crit.addAscendingOrderByColumn(PersonPeer.PERSON_TITLE_ID);
			}
			break;
			case AssessmentReportLister.JOB_DESC_SORT:
			{
				crit.addJoin(AssessmentpersonPeer.PERSONID, PersonPeer.PERSONID);
				crit.addDescendingOrderByColumn(PersonPeer.PERSON_TITLE_ID);
			}
			break;
			case AssessmentReportLister.SCORE_ASC_SORT: crit.addAscendingOrderByColumn(AssessmentpersonPeer.PERCENTAGESCORE); break;
			case AssessmentReportLister.SCORE_DESC_SORT: crit.addDescendingOrderByColumn(AssessmentpersonPeer.PERCENTAGESCORE); break;
			case AssessmentReportLister.STATUS_ASC_SORT: crit.addAscendingOrderByColumn(AssessmentpersonPeer.ISCOMPLETE); break;
			case AssessmentReportLister.STATUS_DESC_SORT: crit.addDescendingOrderByColumn(AssessmentpersonPeer.ISCOMPLETE); break;
			case AssessmentReportLister.ASS_NAME_ASC_SORT:
			{
				crit.addJoin(AssessmentpersonPeer.ASSESSMENTID, AssessmentPeer.ASSESSMENTID);
				crit.addAscendingOrderByColumn(AssessmentPeer.NAME);
			}
			break;
			case AssessmentReportLister.ASS_NAME_DESC_SORT:
			{
				crit.addJoin(AssessmentpersonPeer.ASSESSMENTID, AssessmentPeer.ASSESSMENTID);
				crit.addDescendingOrderByColumn(AssessmentPeer.NAME);
			}
			break;
		}

		if (needs_secondary_sort)
		{
			crit.addJoin(AssessmentpersonPeer.PERSONID, PersonPeer.PERSONID);
			crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		}

		System.out.println("crit >" + crit.toString());

		list = AssessmentpersonPeer.doSelect(crit);
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