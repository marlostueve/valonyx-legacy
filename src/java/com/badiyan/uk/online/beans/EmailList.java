/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
EmailList
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,EmailList> hash = new HashMap<Integer,EmailList>(11);

    // CLASS METHODS

    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(EmailListDbPeer.EMAIL_LIST_DB_ID, _id);
		EmailListDbPeer.doDelete(crit);
    }

    public static EmailList
    getEmailList(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		EmailList obj = (EmailList)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(EmailListDbPeer.EMAIL_LIST_DB_ID, _id);
			List objList = EmailListDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Email List with id: " + _id);

			obj = EmailList.getEmailList((EmailListDb)objList.get(0));
		}

		return obj;
    }

    private static EmailList
    getEmailList(EmailListDb _email_list)
		throws TorqueException
    {
		Integer key = new Integer(_email_list.getEmailListDbId());
		EmailList obj = (EmailList)hash.get(key);
		if (obj == null)
		{
			obj = new EmailList(_email_list);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getEmailLists(UKOnlineCompanyBean _company)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(EmailListDbPeer.COMPANY_ID, _company.getId());
		crit.add(EmailListDbPeer.IS_ACTIVE, (short)1);
		crit.addAscendingOrderByColumn(EmailListDbPeer.NAME);
		Iterator itr = EmailListDbPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(EmailList.getEmailList((EmailListDb)itr.next()));

		return vec;
    }

    public static Vector
    getEmailLists(UKOnlineCompanyBean _company, boolean _active)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(EmailListDbPeer.COMPANY_ID, _company.getId());
		crit.add(EmailListDbPeer.IS_ACTIVE, _active ? (short)1 : (short)0);
		crit.addAscendingOrderByColumn(EmailListDbPeer.NAME);
		Iterator itr = EmailListDbPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(EmailList.getEmailList((EmailListDb)itr.next()));

		return vec;
    }

    // SQL

    /*
	 * 
<table name="EMAIL_LIST_DB" idMethod="native">
    <column name="EMAIL_LIST_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="NAME" required="true" type="VARCHAR" size="100"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
	
    <column name="IS_ACTIVE" type="SMALLINT" default="1"/>

    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>

    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
</table>

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

    // INSTANCE VARIABLES

    private EmailListDb email_list;
	private Vector members;

    // CONSTRUCTORS

    public
    EmailList()
    {
		email_list = new EmailListDb();
		isNew = true;
    }

    public
    EmailList(EmailListDb _email_list)
    {
		email_list = _email_list;
		isNew = false;
    }

    // INSTANCE METHODS

    public int
    getId()
    {
		return email_list.getEmailListDbId();
    }

    public String
    getLabel()
    {
		return this.getNameString();
    }

    public String
    getNameString()
    {
		String str = email_list.getName();
		if (str == null)
			return "";
		return str;
    }

    public String
    getValue()
    {
		return email_list.getEmailListDbId() + "";
    }

	@Override
    protected void
    insertObject()
		throws Exception
    {
		email_list.setCreationDate(new Date());
		email_list.save();
		
		this.saveMembers();
    }

	public boolean
	isActive()
	{
		return (email_list.getIsActive() == (short)1);
	}

	public Vector
	getMembers()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (members == null)
		{
			members = new Vector();

			Criteria crit = new Criteria();
			crit.add(EmailListMemberDbPeer.EMAIL_LIST_DB_ID, this.getId());
			Iterator itr = EmailListMemberDbPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				EmailListMemberDb obj = (EmailListMemberDb)itr.next();
				members.addElement(UKOnlinePersonBean.getPerson(obj.getMemberId()));
			}
		}
		return members;
	}

	private void
    saveMembers()
		throws TorqueException, Exception
    {
		/*
		 *
		<table name="EMAIL_LIST_MEMBER_DB">
			<column name="EMAIL_LIST_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
			<column name="MEMBER_ID" primaryKey="true" required="true" type="INTEGER"/>

			<foreign-key foreignTable="EMAIL_LIST_DB">
				<reference local="EMAIL_LIST_DB_ID" foreign="EMAIL_LIST_DB_ID"/>
			</foreign-key>
			<foreign-key foreignTable="PERSON">
				<reference local="MEMBER_ID" foreign="PERSONID"/>
			</foreign-key>
		</table>
		 * 
		 */

		if (this.members != null)
		{
			System.out.println("saveMembers() sizer >" + this.members.size());

			HashMap<Integer,EmailListMemberDb> db_order_hash = new HashMap<Integer,EmailListMemberDb>(3);
			Criteria crit = new Criteria();
			crit.add(EmailListMemberDbPeer.EMAIL_LIST_DB_ID, this.getId());
			Iterator itr = EmailListMemberDbPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				EmailListMemberDb value = (EmailListMemberDb)itr.next();
				Integer key = new Integer(value.getMemberId());
				db_order_hash.put(key, value);
			}

			System.out.println("num members >" + this.members.size());
			itr = this.members.iterator();
			while (itr.hasNext())
			{
				UKOnlinePersonBean member = (UKOnlinePersonBean)itr.next();
				Integer key = new Integer(member.getId());
				EmailListMemberDb obj = db_order_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("creating new ass for " + member.getLabel());

					obj = new EmailListMemberDb();
					obj.setEmailListDbId(this.getId());
					obj.setMemberId(key.intValue());
					obj.save();

				}
			}

			itr = db_order_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				EmailListMemberDb obj = db_order_hash.get(key);
				System.out.println("del obj.getMemberId() >" + obj.getMemberId());
				EmailListMemberDbPeer.doDelete(obj);
			}
		}
    }
	
	public void
	setMembers(Vector _members)
	{
		this.members = _members;
	}

    public void
    setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
    {
		email_list.setCompanyId(_company.getId());
    }

    public void
    setName(String _str)
    {
		email_list.setName(_str);
    }

    public void
    setIsActive(boolean _active)
    {
		email_list.setIsActive(_active ? (short)1 : (short)0);
    }

	@Override
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		email_list.setModificationDate(new Date());
		email_list.save();
		
		this.saveMembers();
    }
}