/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.badiyan.uk.tasks.*;

/**
 *
 * @author marlo
 */
public class
MarketingPlan
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,MarketingPlan> hash = new HashMap<Integer,MarketingPlan>(11);

    // CLASS METHODS

    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(MarketingPlanDbPeer.MARKETING_PLAN_DB_ID, _id);
		MarketingPlanDbPeer.doDelete(crit);
    }

    public static MarketingPlan
    getMarketingPlan(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		MarketingPlan obj = (MarketingPlan)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(MarketingPlanDbPeer.MARKETING_PLAN_DB_ID, _id);
			List objList = MarketingPlanDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Marketing Plan with id: " + _id);

			obj = MarketingPlan.getMarketingPlan((MarketingPlanDb)objList.get(0));
		}

		return obj;
    }

    private static MarketingPlan
    getMarketingPlan(MarketingPlanDb _marketing_plan)
		throws TorqueException
    {
		Integer key = new Integer(_marketing_plan.getMarketingPlanDbId());
		MarketingPlan obj = (MarketingPlan)hash.get(key);
		if (obj == null)
		{
			obj = new MarketingPlan(_marketing_plan);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getMarketingPlans(UKOnlineCompanyBean _company)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(MarketingPlanDbPeer.COMPANY_ID, _company.getId());
		crit.addDescendingOrderByColumn(MarketingPlanDbPeer.START_DATE);
		Iterator itr = MarketingPlanDbPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(MarketingPlan.getMarketingPlan((MarketingPlanDb)itr.next()));

		return vec;
    }

    public static Vector
    getMarketingPlans(UKOnlineCompanyBean _company, boolean _active)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(MarketingPlanDbPeer.COMPANY_ID, _company.getId());
		crit.add(MarketingPlanDbPeer.ISACTIVE, _active ? (short)1 : (short)0);
		crit.addDescendingOrderByColumn(MarketingPlanDbPeer.START_DATE);
		Iterator itr = MarketingPlanDbPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(MarketingPlan.getMarketingPlan((MarketingPlanDb)itr.next()));

		return vec;
    }

    // SQL

    /*
     * <table name="MARKETING_PLAN_DB" idMethod="native">
    <column name="MARKETING_PLAN_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="NAME" required="true" type="VARCHAR" size="100"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="START_DATE" required="false" type="DATE"/>
    <column name="END_DATE" required="false" type="DATE"/>
    <column name="ISACTIVE" required="true" type="SMALLINT"/>

    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
</table>
     */

    // INSTANCE VARIABLES

    private MarketingPlanDb marketing_plan;
	private Vector<EmailList> email_lists;

    // CONSTRUCTORS

    public
    MarketingPlan()
    {
		marketing_plan = new MarketingPlanDb();
		isNew = true;
    }

    public
    MarketingPlan(MarketingPlanDb _marketing_plan)
    {
		marketing_plan = _marketing_plan;
		isNew = false;
    }

    // INSTANCE METHODS

    public String
    getStartDateString()
    {
		Date date = marketing_plan.getStartDate();
		if (date == null)
			return "";
		return CUBean.getUserDateString(date);
    }

    public String
    getEndDateString()
    {
		Date date = marketing_plan.getEndDate();
		if (date == null)
			return "";
		return CUBean.getUserDateString(date);
    }

    public int
    getId()
    {
		return marketing_plan.getMarketingPlanDbId();
    }

    public String
    getLabel()
    {
		return this.getNameString();
    }

    public String
    getNameString()
    {
		String str = marketing_plan.getName();
		if (str == null)
			return "";
		return str;
    }

    public String
    getValue()
    {
		return marketing_plan.getMarketingPlanDbId() + "";
    }

	@Override
    protected void
    insertObject()
		throws Exception
    {
		marketing_plan.save();
    }

	public boolean
	isActive()
	{
		return (marketing_plan.getIsactive() == (short)1);
	}

	public boolean
	isBulkEmail()
	{
		return (marketing_plan.getIsBulkEmail() == (short)1);
	}

	public Vector
	getEmailLists()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (this.email_lists == null)
		{
			this.email_lists = new Vector();

			Criteria crit = new Criteria();
			crit.add(EmailListMarketingPlanMappingPeer.MARKETING_PLAN_DB_ID, this.getId());
			Iterator itr = EmailListMarketingPlanMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				EmailListMarketingPlanMapping obj = (EmailListMarketingPlanMapping)itr.next();
				this.email_lists.addElement(EmailList.getEmailList(obj.getEmailListDbId()));
			}
		}
		
		return this.email_lists;
	}

	private void
    saveEmailLists()
		throws TorqueException, Exception
    {
		/*
		 *
		<table name="EMAIL_LIST_MARKETING_PLAN_MAPPING">
			<column name="MARKETING_PLAN_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
			<column name="EMAIL_LIST_DB_ID" primaryKey="true" required="true" type="INTEGER"/>

			<foreign-key foreignTable="MARKETING_PLAN_DB">
				<reference local="MARKETING_PLAN_DB_ID" foreign="MARKETING_PLAN_DB_ID"/>
			</foreign-key>
			<foreign-key foreignTable="EMAIL_LIST_DB">
				<reference local="EMAIL_LIST_DB_ID" foreign="EMAIL_LIST_DB_ID"/>
			</foreign-key>
		</table>
		 * 
		 */

		
		if (this.email_lists != null)
		{
			System.out.println("saveEmailLists() sizer >" + this.email_lists.size());

			HashMap<Integer,EmailListMarketingPlanMapping> db_obj_hash = new HashMap<Integer,EmailListMarketingPlanMapping>(3);
			Criteria crit = new Criteria();
			crit.add(EmailListMarketingPlanMappingPeer.EMAIL_LIST_DB_ID, this.getId());
			Iterator itr = EmailListMarketingPlanMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				EmailListMarketingPlanMapping value = (EmailListMarketingPlanMapping)itr.next();
				Integer key = new Integer(value.getEmailListDbId());
				db_obj_hash.put(key, value);
			}

			System.out.println("num members >" + this.email_lists.size());
			itr = this.email_lists.iterator();
			while (itr.hasNext())
			{
				EmailList list = (EmailList)itr.next();
				Integer key = new Integer(list.getId());
				EmailListMarketingPlanMapping obj = db_obj_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("creating new ass for " + list.getLabel());

					obj = new EmailListMarketingPlanMapping();
					obj.setMarketingPlanDbId(this.getId());
					obj.setEmailListDbId(key.intValue());
					obj.save();

				}
			}

			itr = db_obj_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				EmailListMarketingPlanMapping obj = db_obj_hash.get(key);
				System.out.println("del obj.getEmailListDbId() >" + obj.getEmailListDbId());
				EmailListMarketingPlanMappingPeer.doDelete(obj);
			}
		}
		
    }
	
	public void
	setEmailLists(Vector _lists)
	{
		this.email_lists = _lists;
	}

    public void
    setStartDate(Date _date)
    {
		marketing_plan.setStartDate(_date);
    }

    public void
    setEndDate(Date _date)
    {
		marketing_plan.setEndDate(_date);
    }

    public void
    setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
    {
		marketing_plan.setCompanyId(_company.getId());
    }

    public void
    setName(String _str)
    {
		marketing_plan.setName(_str);
    }

    public void
    setIsActive(boolean _active)
    {
		marketing_plan.setIsactive(_active ? (short)1 : (short)0);
    }

    public void
    setIsBulkEmail(boolean _active)
    {
		marketing_plan.setIsBulkEmail(_active ? (short)1 : (short)0);
    }

	@Override
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		marketing_plan.save();
    }
}