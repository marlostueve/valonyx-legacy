/*
 * GroupUnderCareMemberTypeBean.java
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.math.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.badiyan.uk.tasks.*;

/**
 *
 * @author marlo
 */
public class
GroupUnderCareMemberTypeBean
	extends CUBean
	implements java.io.Serializable
{
    protected static HashMap<Integer,GroupUnderCareMemberTypeBean> hash = new HashMap<Integer,GroupUnderCareMemberTypeBean>(11);

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(GroupUnderCareMemberTypePeer.GROUP_UNDER_CARE_MEMBER_TYPE_ID, _id);
		GroupUnderCareMemberTypePeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }
	
	public static GroupUnderCareMemberTypeBean
	getMemberType(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		GroupUnderCareMemberTypeBean type = (GroupUnderCareMemberTypeBean)hash.get(key);
		if (type == null)
		{
			Criteria crit = new Criteria();
			crit.add(GroupUnderCareMemberTypePeer.GROUP_UNDER_CARE_MEMBER_TYPE_ID, _id);
			List objList = GroupUnderCareMemberTypePeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate member type with id: " + _id);

			type = GroupUnderCareMemberTypeBean.getMemberType((GroupUnderCareMemberType)objList.get(0));
		}

		return type;
	}

    private static GroupUnderCareMemberTypeBean
    getMemberType(GroupUnderCareMemberType _type)
		throws TorqueException
    {
		Integer key = new Integer(_type.getGroupUnderCareMemberTypeId());
		GroupUnderCareMemberTypeBean type = (GroupUnderCareMemberTypeBean)hash.get(key);
		if (type == null)
		{
			type = new GroupUnderCareMemberTypeBean(_type);
			hash.put(key, type);
		}

		return type;
    }

    public static GroupUnderCareMemberTypeBean
    getMemberType(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(GroupUnderCareMemberPeer.PERSON_ID, _person.getId());
		List objList = GroupUnderCareMemberPeer.doSelect(crit);
		if (objList.size() == 0)
			throw new ObjectNotFoundException(_person.getLabel() + " is not a member of a group under care.");
		if (objList.size() > 1)
			throw new UniqueObjectNotFoundException(_person.getLabel() + " is a member of multiple groups under care.");
		
		GroupUnderCareMember member = (GroupUnderCareMember)objList.get(0);
		return GroupUnderCareMemberTypeBean.getMemberType(member.getGroupUnderCareMemberTypeId());
    }
    
    public static Vector
    getMemberTypes(CompanyBean _company)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(GroupUnderCareMemberTypePeer.COMPANY_ID, _company.getId());
		Iterator itr = GroupUnderCareMemberTypePeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(GroupUnderCareMemberTypeBean.getMemberType((GroupUnderCareMemberType)itr.next()));

		return vec;
    }
	
	public static Vector
	getVisitCharges(CompanyBean _company)
		throws TorqueException
	{
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		crit.addJoin(PracticeAreaPeer.PRACTICE_AREA_ID, GroupUnderCareMemberPracticeAreaVisitChargePeer.PRACTICE_AREA_ID);
		crit.add(PracticeAreaPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(PracticeAreaPeer.PRACTICE_AREA_NAME);
		Iterator obj_itr = GroupUnderCareMemberPracticeAreaVisitChargePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			GroupUnderCareMemberPracticeAreaVisitCharge obj = (GroupUnderCareMemberPracticeAreaVisitCharge)obj_itr.next();
			vec.addElement(obj);
		}
		return vec;
	}

    public static void
    setMemberType(UKOnlinePersonBean _person, GroupUnderCareMemberTypeBean _type)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
    {
		Criteria crit = new Criteria();
		crit.add(GroupUnderCareMemberPeer.PERSON_ID, _person.getId());
		List objList = GroupUnderCareMemberPeer.doSelect(crit);
		if (objList.size() == 0)
			throw new ObjectNotFoundException(_person.getLabel() + " is not a member of a group under care.");
		if (objList.size() > 1)
			throw new UniqueObjectNotFoundException(_person.getLabel() + " is a member of multiple groups under care.");
		
		GroupUnderCareMember member = (GroupUnderCareMember)objList.get(0);
		member.setGroupUnderCareMemberTypeId(_type.getId());
		member.save();
    }
    
    // SQL
    
    /*
     * <table name="GROUP_UNDER_CARE_MEMBER_TYPE" idMethod="native">
    <column name="GROUP_UNDER_CARE_MEMBER_TYPE_ID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="TYPE_NAME" required="true" type="VARCHAR" size="50"/>
    <column name="CHIROPRACTIC_VISIT_CHARGE" required="true" type="DECIMAL"/>
    <foreign-key foreignTable="COMPANY">
	<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
</table>
     */
	
    // INSTANCE VARIABLES
	
    private GroupUnderCareMemberType type;
    
    // CONSTRUCTORS
    
    public
    GroupUnderCareMemberTypeBean()
    {
		type = new GroupUnderCareMemberType();
		isNew = true;
    }
    
    public
    GroupUnderCareMemberTypeBean(GroupUnderCareMemberType _type)
    {
		type = _type;
		isNew = false;
    }
    
    // INSTANCE METHODS
    
    public boolean
    equals(Object _obj)
    {
		if (_obj == null)
			return false;
		if (_obj instanceof GroupUnderCareMemberTypeBean)
			return (this.getId() == ((GroupUnderCareMemberTypeBean)_obj).getId());
		else
			return false;
    }
    
    public CompanyBean
    getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return CompanyBean.getCompany(type.getCompanyId());
    }
    
    public int
    getId()
    {
		return type.getGroupUnderCareMemberTypeId();
    }
    
    public String
    getLabel()
    {
		String str = type.getTypeName();
		if (str == null)
			return "";
		return str;
    }
    
    public String
    getValue()
    {
		return type.getGroupUnderCareMemberTypeId() + "";
    }
    
    public BigDecimal
    getVisitCharge(PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException
    {
		//return type.getChiropracticVisitCharge();
		
		Criteria crit = new Criteria();
		crit.add(GroupUnderCareMemberPracticeAreaVisitChargePeer.GROUP_UNDER_CARE_MEMBER_TYPE_ID, this.getId());
		crit.add(GroupUnderCareMemberPracticeAreaVisitChargePeer.PRACTICE_AREA_ID, _practice_area.getId());
		List obj_list = GroupUnderCareMemberPracticeAreaVisitChargePeer.doSelect(crit);
		if (obj_list.size() == 1)
		{
			GroupUnderCareMemberPracticeAreaVisitCharge obj = (GroupUnderCareMemberPracticeAreaVisitCharge)obj_list.get(0);
			return obj.getVisitCharge();
		}
		else
			throw new ObjectNotFoundException(_practice_area.getLabel() + " visit charge not found for " + this.getLabel());
    }
    
    public String
    getVisitChargeString(PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException
    {
		return this.getVisitCharge(_practice_area).toString();
    }
    
    protected void
    insertObject()
		throws Exception
    {
		type.save();
    }
    
    public void
    setCompany(CompanyBean _company)
		throws TorqueException
    {
		type.setCompanyId(_company.getId());
    }
	
	public void
	setName(String _name)
	{
		type.setTypeName(_name);
	}
    
    public void
    setVisitCharge(PracticeAreaBean _practice_area, BigDecimal _amount)
		throws TorqueException
    {
		/*
		 * <table name="GROUP_UNDER_CARE_MEMBER_PRACTICE_AREA_VISIT_CHARGE">
    <column name="GROUP_UNDER_CARE_MEMBER_TYPE_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="PRACTICE_AREA_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="VISIT_CHARGE" required="true" scale="2" size="7" type="DECIMAL"/>
	
    <foreign-key foreignTable="GROUP_UNDER_CARE_MEMBER_TYPE">
		<reference local="GROUP_UNDER_CARE_MEMBER_TYPE_ID" foreign="GROUP_UNDER_CARE_MEMBER_TYPE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PRACTICE_AREA">
		<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
    </foreign-key>
</table>
		 */
		
		Criteria crit = new Criteria();
		crit.add(GroupUnderCareMemberPracticeAreaVisitChargePeer.GROUP_UNDER_CARE_MEMBER_TYPE_ID, this.getId());
		crit.add(GroupUnderCareMemberPracticeAreaVisitChargePeer.PRACTICE_AREA_ID, _practice_area.getId());
		List obj_list = GroupUnderCareMemberPracticeAreaVisitChargePeer.doSelect(crit);
		crit.add(GroupUnderCareMemberPracticeAreaVisitChargePeer.VISIT_CHARGE, _amount);
		if (obj_list.size() == 1)
			GroupUnderCareMemberPracticeAreaVisitChargePeer.doUpdate(crit);
		else
			GroupUnderCareMemberPracticeAreaVisitChargePeer.doInsert(crit);
		
		//type.setChiropracticVisitCharge(_amount);
    }
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		type.save();
    }
}