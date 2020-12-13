/*
 * BlackHoleBean.java
 *
 * Created on May 8, 2008, 4:30 PM
 *
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.text.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.badiyan.uk.tasks.*;

/**
 *
 * @author marlo
 */
public class
BlackHoleBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    protected static HashMap<Integer,BlackHoleBean> hash = new HashMap<Integer,BlackHoleBean>(11);

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(BlackHolePeer.BLACK_HOLE_ID, _id);
		BlackHolePeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }

    public static BlackHoleBean
    getBlackHole(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		BlackHoleBean black_hole = (BlackHoleBean)hash.get(key);
		if (black_hole == null)
		{
			Criteria crit = new Criteria();
			crit.add(BlackHolePeer.BLACK_HOLE_ID, _id);
			List objList = BlackHolePeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate black hole with id: " + _id);

			black_hole = BlackHoleBean.getBlackHole((BlackHole)objList.get(0));
		}

		return black_hole;
    }

    private static BlackHoleBean
    getBlackHole(BlackHole _black_hole)
		throws TorqueException
    {
		Integer key = new Integer(_black_hole.getBlackHoleId());
		BlackHoleBean black_hole = (BlackHoleBean)hash.get(key);
		if (black_hole == null)
		{
			black_hole = new BlackHoleBean(_black_hole);
			hash.put(key, black_hole);
		}

		return black_hole;
    }
    
    public static BlackHoleBean
    getBlackHole(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(BlackHolePeer.COMPANY_ID, _company.getId());
		List obj_list = BlackHolePeer.doSelect(crit);
		if (obj_list.size() == 1)
			return BlackHoleBean.getBlackHole((BlackHole)obj_list.get(0));
		if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Black Hole not found for " + _company.getLabel());
		throw new UniqueObjectNotFoundException("Unique Black Hole not found for " + _company.getLabel());
    }
    
    public static BlackHoleBean
    getInstance(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		BlackHoleBean black_hole = null;
		try
		{
			black_hole = BlackHoleBean.getBlackHole(_company);
		}
		catch (ObjectNotFoundException x)
		{
			black_hole = new BlackHoleBean();
			black_hole.setCompany(_company);
			black_hole.save();
		}
		
		return black_hole;
    }
    
    public static BlackHoleBean
    getInstance(CompanyBean _company, UKOnlinePersonBean _create_or_modify_person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		BlackHoleBean black_hole = null;
		try
		{
			black_hole = BlackHoleBean.getBlackHole(_company);
		}
		catch (ObjectNotFoundException x)
		{
			black_hole = new BlackHoleBean();
			black_hole.setCompany(_company);
			black_hole.setCreateOrModifyPerson(_create_or_modify_person);
			black_hole.save();
		}
		
		return black_hole;
    }
    
    // SQL
    
    /*
     * <table name="BLACK_HOLE" idMethod="native">
    <column name="BLACK_HOLE_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
    
    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
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
	
    private BlackHole black_hole;
	private Vector members;
    
    // CONSTRUCTORS
    
    public
    BlackHoleBean()
    {
		black_hole = new BlackHole();
		isNew = true;
    }
    
    public
    BlackHoleBean(BlackHole _black_hole)
    {
		black_hole = _black_hole;
		isNew = false;
    }
    
    // INSTANCE METHODS
	
	public void
	add(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException
	{
		BlackHoleMemberBean member = new BlackHoleMemberBean();
		member.setPerson(_person);
		member.setAddDate(new Date());
		member.setIsCurrentMember(true);
						
		this.getMembers();
		members.addElement(member);
	}
	
	public void
	add(UKOnlinePersonBean _person, PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException
	{
		BlackHoleMemberBean member = new BlackHoleMemberBean();
		member.setPerson(_person);
		member.setPracticeArea(_practice_area);
		member.setAddDate(new Date());
		member.setIsCurrentMember(true);
						
		this.getMembers();
		members.addElement(member);
	}
	
	public boolean
	contains(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Iterator itr = this.getMembers().iterator();
		while (itr.hasNext())
		{
			BlackHoleMemberBean member = (BlackHoleMemberBean)itr.next();

			UKOnlinePersonBean member_person = member.getPerson();
			if (member_person.equals(_person))
				return true;
		}
		
		return false;
	}
	
	public boolean
	contains(UKOnlinePersonBean _person, PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Iterator itr = this.getMembers().iterator();
		while (itr.hasNext())
		{
			BlackHoleMemberBean member = (BlackHoleMemberBean)itr.next();
			if (member.hasPracticeArea())
			{
				UKOnlinePersonBean member_person = member.getPerson();
				PracticeAreaBean practice_area = member.getPracticeArea();
				if (member_person.equals(_person) && practice_area.equals(_practice_area))
					return true;
			}
		}
		
		return false;
	}

	public boolean
	containsNoPracticeArea(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Iterator itr = this.getMembers().iterator();
		while (itr.hasNext())
		{
			BlackHoleMemberBean member = (BlackHoleMemberBean)itr.next();

			if (!member.hasPracticeArea())
			{
				UKOnlinePersonBean member_person = member.getPerson();
				if (member_person.equals(_person))
					return true;
			}
		}

		return false;
	}
    
    public CompanyBean
    getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return CompanyBean.getCompany(black_hole.getCompanyId());
    }
    
    public int
    getId()
    {
		return black_hole.getBlackHoleId();
    }
    
    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return this.getCompany().getLabel() + " Black Hole";
    }

	public Vector
	getPracticeAreas(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector practice_areas = new Vector();
		Iterator itr = this.getMembers().iterator();
		while (itr.hasNext())
		{
			BlackHoleMemberBean member = (BlackHoleMemberBean)itr.next();
			if (member.hasPracticeArea())
			{
				UKOnlinePersonBean member_person = member.getPerson();
				if (member_person.equals(_person))
				{
					PracticeAreaBean practice_area = member.getPracticeArea();
					practice_areas.addElement(practice_area);
				}
			}
		}

		return practice_areas;
	}
	
	public Vector
	getMembers()
		throws TorqueException, ObjectNotFoundException
	{
		if (this.members == null)
		{
			this.members = new Vector();
			
			Criteria crit = new Criteria();
			crit.add(BlackHoleMemberPeer.BLACK_HOLE_ID, this.getId());
			crit.add(BlackHoleMemberPeer.IS_CURRENT_MEMBER, 1);
			crit.addAscendingOrderByColumn(BlackHoleMemberPeer.BLACK_HOLE_ADD_DATE);
			//System.out.println("ANALYZE crit >" + crit.toString());
			Iterator itr = BlackHoleMemberPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				BlackHoleMember member_obj = (BlackHoleMember)itr.next();
				BlackHoleMemberBean member = BlackHoleMemberBean.getBlackHoleMember(member_obj.getBlackHoleMemberId());
				this.members.addElement(member);
			}
		}
		return this.members;
	}

	public Vector
	getMembers(PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException
	{
		Vector vec = new Vector();
		Iterator members_itr = this.getMembers().iterator();
		while (members_itr.hasNext())
		{
			BlackHoleMemberBean member = (BlackHoleMemberBean)members_itr.next();
			if (member.hasPracticeArea())
			{
				if (member.getPracticeArea().getId() == _practice_area.getId())
					vec.addElement(member);
			}
		}
		return vec;
	}
    
    public String
    getValue()
    {
		return this.getId() + "";
    }
    
    protected void
    insertObject()
		throws Exception
    {
		black_hole.setCreationDate(new Date());
		black_hole.save();
		
		saveMembers();
    }
	
	public void
	remove(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		//System.out.println("remove invoked in BlackHoleBean - " + _person.getLabel());
		this.getMembers();
		//System.out.println("this.members sise >" + this.members.size());
		for (int i = this.members.size(); i > 0; i--)
		{
			BlackHoleMemberBean member = (BlackHoleMemberBean)this.members.elementAt(i - 1);
			if (member.getPerson().equals(_person))
			{
				//System.out.println("remove member at >" + (i - 1));
				this.members.removeElementAt(i - 1);
			}
		}
	}
	
	public void
	remove(UKOnlinePersonBean _person, PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		//System.out.println("remove invoked in BlackHoleBean - " + _person.getLabel());
		this.getMembers();
		//System.out.println("this.members sise >" + this.members.size());
		for (int i = this.members.size(); i > 0; i--)
		{
			BlackHoleMemberBean member = (BlackHoleMemberBean)this.members.elementAt(i - 1);
			if (member.hasPracticeArea())
			{
				PracticeAreaBean practice_area = member.getPracticeArea();
				if (member.getPerson().equals(_person) && practice_area.equals(_practice_area))
				{
					//System.out.println("remove member at >" + (i - 1));
					this.members.removeElementAt(i - 1);
				}
			}
		}
	}
	
	private void
    saveMembers()
		throws TorqueException, Exception
    {
		/*
		 *<table name="BLACK_HOLE_MEMBER">
    <column name="BLACK_HOLE_MEMBER_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	
    <column name="BLACK_HOLE_ID" required="true" type="INTEGER"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
	
    <column name="IS_CURRENT_MEMBER" required="true" type="SMALLINT"/>
    <column name="BLACK_HOLE_ADD_DATE" required="true" type="DATE"/>
    <column name="BLACK_HOLE_REMOVE_DATE" required="true" type="DATE"/>
    
    <foreign-key foreignTable="BLACK_HOLE">
		<reference local="BLACK_HOLE_ID" foreign="BLACK_HOLE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
		 */

		if (this.members != null)
		{
			HashMap db_black_hole_member_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(BlackHoleMemberPeer.BLACK_HOLE_ID, this.getId());
			crit.add(BlackHoleMemberPeer.IS_CURRENT_MEMBER, 1);
			//System.out.println("crit >" + crit.toString());
			Iterator itr = BlackHoleMemberPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				//System.out.println("found existing ass...");
				BlackHoleMember value = (BlackHoleMember)itr.next();
				Integer key = new Integer(value.getBlackHoleMemberId());
				db_black_hole_member_hash.put(key, value);
			}

			//System.out.println("num members >" + this.members.size());
			itr = this.members.iterator();
			while (itr.hasNext())
			{
				BlackHoleMemberBean member = (BlackHoleMemberBean)itr.next();
				Integer key = new Integer(member.getId());
				BlackHoleMember obj = (BlackHoleMember)db_black_hole_member_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					//System.out.println("creating new ass for " + member);

					member.setBlackHole(this);
					member.save();
				}
			}

			itr = db_black_hole_member_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				BlackHoleMember obj = (BlackHoleMember)db_black_hole_member_hash.get(key);
				//BlackHoleMemberPeer.doDelete(obj);
				
				// don't delete.  I want to keep track of past black hole events
				
				BlackHoleMemberBean member = BlackHoleMemberBean.getBlackHoleMember(obj.getBlackHoleMemberId());
				member.setIsCurrentMember(false);
				member.setRemoveDate(new Date());
				member.save();
			}
		}
    }
    
    public void
    setCompany(CompanyBean _company)
		throws TorqueException
    {
		black_hole.setCompanyId(_company.getId());
    }
    
    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			black_hole.setCreatePersonId(_person.getId());
		else
			black_hole.setModifyPersonId(_person.getId());
    }
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		black_hole.setModificationDate(new Date());
		black_hole.save();
		
		saveMembers();
    }
}