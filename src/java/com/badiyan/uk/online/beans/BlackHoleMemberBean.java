/*
 * BlackHoleMemberBean.java
 *
 * Created on May 9, 2008, 11:15 AM
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
BlackHoleMemberBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    protected static HashMap<Integer,BlackHoleMemberBean> hash = new HashMap<Integer,BlackHoleMemberBean>(11);

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(BlackHoleMemberPeer.BLACK_HOLE_MEMBER_ID, _id);
		BlackHoleMemberPeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }

    public static BlackHoleMemberBean
    getBlackHoleMember(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		BlackHoleMemberBean member = (BlackHoleMemberBean)hash.get(key);
		if (member == null)
		{
			Criteria crit = new Criteria();
			crit.add(BlackHoleMemberPeer.BLACK_HOLE_MEMBER_ID, _id);
			List objList = BlackHoleMemberPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate black hole member with id: " + _id);

			member = BlackHoleMemberBean.getBlackHoleMember((BlackHoleMember)objList.get(0));
		}

		return member;
    }

    private static BlackHoleMemberBean
    getBlackHoleMember(BlackHoleMember _member)
		throws TorqueException
    {
		Integer key = new Integer(_member.getBlackHoleMemberId());
		BlackHoleMemberBean member = (BlackHoleMemberBean)hash.get(key);
		if (member == null)
		{
			member = new BlackHoleMemberBean(_member);
			hash.put(key, member);
		}

		return member;
    }
    
    // SQL
    
    /*
     * <table name="BLACK_HOLE_MEMBER">
    <column name="BLACK_HOLE_MEMBER_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	
    <column name="BLACK_HOLE_ID" required="true" type="INTEGER"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="PRACTICE_AREA_ID" required="true" type="INTEGER" />
	
    <column name="IS_CURRENT_MEMBER" required="true" type="SMALLINT"/>
    <column name="BLACK_HOLE_ADD_DATE" required="true" type="DATE"/>
    <column name="BLACK_HOLE_REMOVE_DATE" required="true" type="DATE"/>
    
    <foreign-key foreignTable="BLACK_HOLE">
		<reference local="BLACK_HOLE_ID" foreign="BLACK_HOLE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PRACTICE_AREA">
		<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
    </foreign-key>
</table>
     */
	
    // INSTANCE VARIABLES
	
    private BlackHoleMember black_hole_member;
    
    // CONSTRUCTORS
    
    public
    BlackHoleMemberBean()
    {
		black_hole_member = new BlackHoleMember();
		isNew = true;
    }
    
    public
    BlackHoleMemberBean(BlackHoleMember _member)
    {
		black_hole_member = _member;
		isNew = false;
    }
    
    // INSTANCE METHODS
	
	public Date
	getAddDate()
	{
		return black_hole_member.getBlackHoleAddDate();
	}
    
    public int
    getId()
    {
		return black_hole_member.getBlackHoleMemberId();
    }
    
    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return this.getPerson().getLabel();
    }
	
	public UKOnlinePersonBean
	getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(black_hole_member.getPersonId());
	}
	
	public PracticeAreaBean
	getPracticeArea()
		throws TorqueException, ObjectNotFoundException
	{
		return PracticeAreaBean.getPracticeArea(black_hole_member.getPracticeAreaId());
	}
	
	public Date
	getRemoveDate()
	{
		return black_hole_member.getBlackHoleRemoveDate();
	}
    
    public String
    getValue()
    {
		return this.getId() + "";
    }
	
	public boolean
	hasPracticeArea()
		throws TorqueException, ObjectNotFoundException
	{
		return (black_hole_member.getPracticeAreaId() > 0);
	}
    
    protected void
    insertObject()
		throws Exception
    {
		black_hole_member.save();
    }
	
	public boolean
	isCurrentMember()
	{
		return (black_hole_member.getIsCurrentMember() == (short)1);
	}
	
	public void
	setAddDate(Date _date)
	{
		black_hole_member.setBlackHoleAddDate(_date);
	}
    
    public void
    setBlackHole(BlackHoleBean _black_hole)
		throws TorqueException
    {
		black_hole_member.setBlackHoleId(_black_hole.getId());
    }
	
	public void
	setIsCurrentMember(boolean _is_current_member)
	{
		black_hole_member.setIsCurrentMember(_is_current_member ? (short)1 : (short)0);
	}
    
    public void
    setPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		black_hole_member.setPersonId(_person.getId());
    }
	
	public void
	setPracticeArea(PracticeAreaBean _practice_area)
		throws TorqueException
	{
		black_hole_member.setPracticeAreaId(_practice_area.getId());
	}
	
	public void
	setRemoveDate(Date _date)
	{
		black_hole_member.setBlackHoleRemoveDate(_date);
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		black_hole_member.save();
    }
}