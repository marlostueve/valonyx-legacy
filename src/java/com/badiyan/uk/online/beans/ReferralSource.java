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
ReferralSource
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,ReferralSource> hash = new HashMap<Integer,ReferralSource>(11);

    // CLASS METHODS

    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(ReferralSourceDbPeer.REFERRAL_SOURCE_DB_ID, _id);
		ReferralSourceDbPeer.doDelete(crit);
    }

    public static ReferralSource
    getReferralSource(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ReferralSource obj = (ReferralSource)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ReferralSourceDbPeer.REFERRAL_SOURCE_DB_ID, _id);
			List objList = ReferralSourceDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Referral Source with id: " + _id);

			obj = ReferralSource.getReferralSource((ReferralSourceDb)objList.get(0));
		}

		return obj;
    }

    private static ReferralSource
    getReferralSource(ReferralSourceDb _referral_source)
		throws TorqueException
    {
		Integer key = new Integer(_referral_source.getReferralSourceDbId());
		ReferralSource obj = (ReferralSource)hash.get(key);
		if (obj == null)
		{
			obj = new ReferralSource(_referral_source);
			hash.put(key, obj);
		}

		return obj;
    }

    public static ReferralSource
    getReferralSource(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ReferralSourceDbPeer.PERSON_ID, _person.getId());
		List obj_list = ReferralSourceDbPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return ReferralSource.getReferralSource((ReferralSourceDb)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Referral source not found for " + _person.getLabel());
		throw new UniqueObjectNotFoundException("Unique referral source not found for " + _person.getLabel());
    }

    public static Vector
    getReferralSources(MarketingPlan _marketing_plan)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(ReferralSourceDbPeer.MARKETING_PLAN_DB_ID, _marketing_plan.getId());
		Iterator obj_itr = ReferralSourceDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ReferralSource.getReferralSource((ReferralSourceDb)obj_itr.next()));

		return vec;
    }

    // SQL

    /*
     * <table name="REFERRAL_SOURCE_DB" idMethod="native">
    <column name="REFERRAL_SOURCE_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="REFERRED_BY_ID" required="false" type="INTEGER"/>
    <column name="MARKETING_PLAN_DB_ID" required="false" type="INTEGER"/>
    <column name="REFERRAL_DATE" required="true" type="DATE"/>

    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="REFERRED_BY_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="MARKETING_PLAN_DB">
		<reference local="MARKETING_PLAN_DB_ID" foreign="MARKETING_PLAN_DB_ID"/>
    </foreign-key>
</table>
     */

    // INSTANCE VARIABLES

    private ReferralSourceDb referral_source;

    // CONSTRUCTORS

    public
    ReferralSource()
    {
		referral_source = new ReferralSourceDb();
		isNew = true;
    }

    public
    ReferralSource(ReferralSourceDb _referral_source)
    {
		referral_source = _referral_source;
		isNew = false;
    }

    // INSTANCE METHODS

    public String
    getReferralDateString()
    {
		Date date = referral_source.getReferralDate();
		if (date == null)
			return "";
		return CUBean.getUserDateString(date);
    }

    public int
    getId()
    {
		return referral_source.getReferralSourceDbId();
    }

    public String
    getLabel()
    {
		return this.getValue();
    }

    public MarketingPlan
    getMarketingPlan()
		throws TorqueException, ObjectNotFoundException
    {
		return MarketingPlan.getMarketingPlan(referral_source.getMarketingPlanDbId());
    }

    public int
    getMarketingPlanId()
    {
		return referral_source.getMarketingPlanDbId();
    }

	public UKOnlinePersonBean
	getReferredByClient()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(referral_source.getReferredById());
	}

	public int
	getReferredByClientId()
	{
		return referral_source.getReferredById();
	}

    public String
    getValue()
    {
		return referral_source.getReferralSourceDbId() + "";
    }

	@Override
    protected void
    insertObject()
		throws Exception
    {
		referral_source.setReferralDate(new Date());
		referral_source.save();
    }

	public boolean
	isClientReferral()
	{
		return (referral_source.getReferredById() > 0);
	}

	public boolean
	isMarketingPlanReferral()
	{
		return (referral_source.getMarketingPlanDbId() > 0);
	}

    public void
    setReferralDate(Date _date)
    {
		referral_source.setReferralDate(_date);
    }

    public void
    setPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		referral_source.setPersonId(_person.getId());
    }

    public void
    setReferredBy(UKOnlinePersonBean _person)
		throws TorqueException
    {
		referral_source.setReferredById(_person.getId());
    }

    public void
    setMarketingPlan(MarketingPlan _plan)
		throws TorqueException
    {
		referral_source.setMarketingPlanDbId(_plan.getId());
    }

	@Override
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		referral_source.save();
    }
}