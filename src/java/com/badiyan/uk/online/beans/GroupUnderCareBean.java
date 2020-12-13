/*
 * GroupUnderCareBean.java
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
GroupUnderCareBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    public static final short RELATIONSHIP_SELF_TYPE = 1;
    public static final short RELATIONSHIP_SPOUSE_TYPE = 2;
	public static final short RELATIONSHIP_CHILD_TYPE = 3;
	public static final short RELATIONSHIP_GUARDIAN_TYPE = 4;
	public static final short RELATIONSHIP_PARENT_TYPE = 5;
	public static final short RELATIONSHIP_PARTNER_TYPE = 6;
	public static final short RELATIONSHIP_OTHER_TYPE = 7;

    protected static HashMap<Integer,GroupUnderCareBean> hash = new HashMap<Integer,GroupUnderCareBean>(11);

    // CLASS METHODS
	
	public static GroupUnderCareBean
	getGroupUnderCare(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		GroupUnderCareBean group = (GroupUnderCareBean)hash.get(key);
		if (group == null)
		{
			Criteria crit = new Criteria();
			crit.add(GroupUnderCarePeer.GROUP_UNDER_CARE_ID, _id);
			List objList = GroupUnderCarePeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate group under care with id: " + _id);

			group = GroupUnderCareBean.getGroupUnderCare((GroupUnderCare)objList.get(0));
		}

		return group;
	}

    private static GroupUnderCareBean
    getGroupUnderCare(GroupUnderCare _group)
		throws TorqueException
    {
		Integer key = new Integer(_group.getGroupUnderCareId());
		GroupUnderCareBean group = (GroupUnderCareBean)hash.get(key);
		if (group == null)
		{
			group = new GroupUnderCareBean(_group);
			hash.put(key, group);
		}

		return group;
    }
	
	public static GroupUnderCareBean
	getGroupUnderCare(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		Criteria crit = new Criteria();
		crit.addJoin(GroupUnderCareMemberPeer.GROUP_UNDER_CARE_ID, GroupUnderCarePeer.GROUP_UNDER_CARE_ID);
		crit.add(GroupUnderCareMemberPeer.PERSON_ID, _person.getId());
		List obj_list = GroupUnderCarePeer.doSelect(crit);
		if (obj_list.size() == 0)
		{
			// no group found.  why not create one???
			
			GroupUnderCareBean group_under_care = new GroupUnderCareBean();
			group_under_care.setCreatePerson((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(1));
			group_under_care.setCompany(_person.getDepartment().getCompany());
			group_under_care.setPrimaryClient(_person);
			Vector members = new Vector();
			GroupUnderCareMember member = new GroupUnderCareMember();
			member.setPersonId(_person.getId());
			member.setRelationshipToPrimaryClient(GroupUnderCareBean.RELATIONSHIP_SELF_TYPE);
			member.setGroupUnderCareMemberTypeId(1);
			members.addElement(member);
			group_under_care.setGroupUnderCareMembers(members);
			group_under_care.setNote("");
			group_under_care.save();

			//throw new ObjectNotFoundException("Unable to locate group under care for " + _person.getLabel());

			return group_under_care;
		}
		if (obj_list.size() == 1)
			return GroupUnderCareBean.getGroupUnderCare((GroupUnderCare)obj_list.get(0));
		throw new UniqueObjectNotFoundException("Unable to locate unique group under care for " + _person.getLabel());
	}
    
    public static Vector
    getGroupsUnderCare(CompanyBean _company)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(GroupUnderCarePeer.COMPANY_ID, _company.getId());
		Iterator itr = GroupUnderCarePeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(GroupUnderCareBean.getGroupUnderCare((GroupUnderCare)itr.next()));

		return vec;
    }
	
	public static String
	getRelationshipString(short _type)
	{
		switch (_type)
		{
			case GroupUnderCareBean.RELATIONSHIP_SELF_TYPE: return "Self";
			case GroupUnderCareBean.RELATIONSHIP_SPOUSE_TYPE: return "Spouse";
			case GroupUnderCareBean.RELATIONSHIP_CHILD_TYPE: return "Child";
			case GroupUnderCareBean.RELATIONSHIP_GUARDIAN_TYPE: return "Guardian";
			case GroupUnderCareBean.RELATIONSHIP_PARENT_TYPE: return "Parent";
			case GroupUnderCareBean.RELATIONSHIP_PARTNER_TYPE: return "Partner";
			case GroupUnderCareBean.RELATIONSHIP_OTHER_TYPE: return "Other";
		}
		
		return "UNKNOWN";
	}
	
	/*
	public static Vector
	getPersonsByLastNameIncludeGroupMembers(CompanyBean _company, String _search_string)
	{
		Vector people = new Vector();
		
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());
		
		String search_string = _lastName + "%";
		crit.add(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
		
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		
		System.out.println("crit >" + crit.toString());

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			Person obj = (Person)itr.next();
			people.addElement(UKOnlinePersonBean.getPerson(obj));
		}
		return people;
	}
	 */
    
    // SQL
    
    /*
     * <table name="GROUP_UNDER_CARE">
	    <column name="GROUP_UNDER_CARE_ID" required="true" primaryKey="true" type="INTEGER"/>
		<column name="COMPANY_ID" required="true" type="INTEGER"/>
		<column name="NOTE" required="true" type="VARCHAR" size="255"/>
		
		<column name="PRIMARY_CLIENT_ID" required="true" type="INTEGER"/>
	    
	    <column name="CREATION_DATE" required="true" type="DATE"/>
	    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
	    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
	    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

		<foreign-key foreignTable="COMPANY">
			<reference local="COMPANY_ID" foreign="COMPANYID"/>
		</foreign-key>
	    <foreign-key foreignTable="PERSON">
			<reference local="PRIMARY_CLIENT_ID" foreign="PERSONID"/>
	    </foreign-key>
	    <foreign-key foreignTable="PERSON">
			<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
	    </foreign-key>
	    <foreign-key foreignTable="PERSON">
			<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
	    </foreign-key>
	</table>
     */
	
    // INSTANCE VARIABLES
	
    private GroupUnderCare group;
	private Vector members = null;
    
    // CONSTRUCTORS
    
    public
    GroupUnderCareBean()
    {
		group = new GroupUnderCare();
		isNew = true;
    }
    
    public
    GroupUnderCareBean(GroupUnderCare _group)
    {
		group = _group;
		isNew = false;
    }
    
    // INSTANCE METHODS
    
    public boolean
    equals(Object _obj)
    {
		if (_obj == null)
			return false;
		if (_obj instanceof GroupUnderCareBean)
			return (this.getId() == ((GroupUnderCareBean)_obj).getId());
		else
			return false;
    }
    
    public CompanyBean
    getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return CompanyBean.getCompany(group.getCompanyId());
    }
    
    public int
    getId()
    {
		return group.getGroupUnderCareId();
    }
    
    public String
    getLabel()
    {
		try
		{
			return this.getPrimaryClient().getLabel() + " group";
		}
		catch (Exception x)
		{
			return this.getId() + "";
		}
    }

	public GroupUnderCareMember
	getMember(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Iterator itr = this.getMembers().iterator();
		while (itr.hasNext())
		{
			GroupUnderCareMember member = (GroupUnderCareMember)itr.next();
			if (member.getPersonId() == _person.getId())
				return member;
		}

		throw new ObjectNotFoundException("Member not found.");
	}
	
	public Vector
	getMembers()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (this.members == null)
		{
			this.members = new Vector();
			Criteria crit = new Criteria();
			crit.add(GroupUnderCareMemberPeer.GROUP_UNDER_CARE_ID, this.getId());
			crit.addAscendingOrderByColumn(GroupUnderCareMemberPeer.RELATIONSHIP_TO_PRIMARY_CLIENT);
			Iterator itr = GroupUnderCareMemberPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				GroupUnderCareMember member = (GroupUnderCareMember)itr.next();
				//UKOnlinePersonBean group_under_care_member = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(member.getPersonId());
				this.members.add(member);
			}
		}
		
		return this.members;
	}
	
	public String
	getNote()
	{
		return group.getNote();
	}
	
	public UKOnlinePersonBean
	getPrimaryClient()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(group.getPrimaryClientId());
	}
    
    public String
    getValue()
    {
		return group.getGroupUnderCareId() + "";
    }
    
    protected void
    insertObject()
		throws Exception
    {
		group.setCreationDate(new Date());
		group.save();

		this.saveGroupUnderCareMembers();
    }
	
	public void
	invalidate()
	{
		this.members = null;
	}

    public void
    setGroupUnderCareMembers(Vector _members)
		throws IllegalValueException
    {
		members = _members;
    }

    private void
    saveGroupUnderCareMembers()
		throws TorqueException, Exception
    {
		
		/*
		 *<table name="GROUP_UNDER_CARE_MEMBER">
			<column name="GROUP_UNDER_CARE_ID" required="true" type="INTEGER"/>
			<column name="PERSON_ID" required="true" primaryKey="true" type="INTEGER"/>

			<column name="RELATIONSHIP_TO_PRIMARY_CLIENT" required="true" type="SMALLINT" />

			<foreign-key foreignTable="GROUP_UNDER_CARE">
				<reference local="GROUP_UNDER_CARE_ID" foreign="GROUP_UNDER_CARE_ID"/>
			</foreign-key>
			<foreign-key foreignTable="PERSON">
				<reference local="PERSON_ID" foreign="PERSONID"/>
			</foreign-key>
		</table>
		 */

		if (this.members != null)
		{
			HashMap db_group_member_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(GroupUnderCareMemberPeer.GROUP_UNDER_CARE_ID, this.getId());
			System.out.println("crit >" + crit.toString());
			Iterator itr = GroupUnderCareMemberPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				GroupUnderCareMember value = (GroupUnderCareMember)itr.next();
				Integer key = new Integer(value.getPersonId());
				db_group_member_hash.put(key, value);
			}

			System.out.println("num members >" + this.members.size());
			itr = this.members.iterator();
			while (itr.hasNext())
			{
				GroupUnderCareMember member = (GroupUnderCareMember)itr.next();
				Integer key = new Integer(member.getPersonId());
				GroupUnderCareMember obj = (GroupUnderCareMember)db_group_member_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("creating new ass for " + member);

					member.setGroupUnderCareId(this.getId());
					member.save();
				}
			}

			itr = db_group_member_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				GroupUnderCareMember obj = (GroupUnderCareMember)db_group_member_hash.get(key);
				GroupUnderCareMemberPeer.doDelete(obj);
			}
		}
    }
    
    public void
    setCompany(CompanyBean _company)
		throws TorqueException
    {
		group.setCompanyId(_company.getId());
    }
    
    public void
    setCreatePerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		group.setCreatePersonId(_person.getId());
    }
    
    public void
    setModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		group.setModifyPersonId(_person.getId());
    }
    
    public void
    setNote(String _note)
    {
		group.setNote(_note);
    }
	
	public void
	setPrimaryClient(UKOnlinePersonBean _primary_client)
		throws TorqueException
	{
		group.setPrimaryClientId(_primary_client.getId());
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		group.setModificationDate(new Date());
		group.save();

		this.saveGroupUnderCareMembers();
    }
}