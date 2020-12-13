/*
 * ContactStatusBean.java
 *
 * Created on May 15, 2008, 10:13 AM
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
ContactStatusBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    protected static HashMap<Integer,ContactStatusBean> hash = new HashMap<Integer,ContactStatusBean>(11);

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(ContactStatusPeer.CONTACT_STATUS_ID, _id);
		ContactStatusPeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }

    public static ContactStatusBean
    getContactStatus(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ContactStatusBean obj = (ContactStatusBean)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ContactStatusPeer.CONTACT_STATUS_ID, _id);
			List objList = ContactStatusPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate contact status with id: " + _id);

			obj = ContactStatusBean.getContactStatus((ContactStatus)objList.get(0));
		}

		return obj;
    }

    private static ContactStatusBean
    getContactStatus(ContactStatus _status)
		throws TorqueException
    {
		Integer key = new Integer(_status.getContactStatusId());
		ContactStatusBean obj = (ContactStatusBean)hash.get(key);
		if (obj == null)
		{
			obj = new ContactStatusBean(_status);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getContactStatus(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException
    {
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(ContactStatusPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(ContactStatusPeer.CONTACT_DATE);
		Iterator itr = ContactStatusPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(ContactStatusBean.getContactStatus((ContactStatus)itr.next()));
	
		return vec;
    }

    public static ContactStatusBean
    getLastContactStatus(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ContactStatusPeer.PERSON_ID, _person.getId());
		crit.add(ContactStatusPeer.PRACTICE_AREA_ID, 0);
		crit.addDescendingOrderByColumn(ContactStatusPeer.CONTACT_DATE);
		crit.setLimit(1);
		crit.setSingleRecord(true);
		List obj_list = ContactStatusPeer.doSelect(crit);
		if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Last contact status not found for " + _person.getLabel());
		return ContactStatusBean.getContactStatus((ContactStatus)obj_list.get(0));
    }

    public static ContactStatusBean
    getLastContactStatus(UKOnlinePersonBean _person, PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(ContactStatusPeer.PERSON_ID, _person.getId());
		crit.add(ContactStatusPeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.addDescendingOrderByColumn(ContactStatusPeer.CONTACT_DATE);
		crit.setLimit(1);
		crit.setSingleRecord(true);
		List obj_list = ContactStatusPeer.doSelect(crit);
		if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Last contact status not found for " + _person.getLabel());
		return ContactStatusBean.getContactStatus((ContactStatus)obj_list.get(0));
    }

    public static Vector
    getPractitionerCallList(UKOnlineCompanyBean _practice)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector clients = new Vector();
		Vector contacts = new Vector();

		Criteria crit = new Criteria();
		crit.add(ContactStatusPeer.COMPANY_ID, _practice.getId());
		crit.add(ContactStatusPeer.CONTACT_ATTEMPT, 4);
		crit.addDescendingOrderByColumn(ContactStatusPeer.CONTACT_DATE);
		Iterator obj_itr = ContactStatusPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			ContactStatusBean contact = ContactStatusBean.getContactStatus((ContactStatus)obj_itr.next());
			UKOnlinePersonBean client = contact.getPerson();
			if (!clients.contains(client))
			{
				clients.add(client);
				contacts.add(contact);
			}
		}

		return contacts;
    }

    public static Vector
    getPractitionerCallList(UKOnlineCompanyBean _practice, PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector clients = new Vector();
		Vector contacts = new Vector();

		Criteria crit = new Criteria();
		crit.add(ContactStatusPeer.COMPANY_ID, _practice.getId());
		crit.add(ContactStatusPeer.CONTACT_ATTEMPT, 4);
		crit.add(ContactStatusPeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.addDescendingOrderByColumn(ContactStatusPeer.CONTACT_DATE);
		Iterator obj_itr = ContactStatusPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			ContactStatusBean contact = ContactStatusBean.getContactStatus((ContactStatus)obj_itr.next());
			UKOnlinePersonBean client = contact.getPerson();
			if (!clients.contains(client))
			{
				clients.add(client);
				contacts.add(contact);
			}
		}

		return contacts;
    }

    public static Vector
    getPractitionerCallList(UKOnlineCompanyBean _practice, UKOnlinePersonBean _practitioner)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector clients = new Vector();
		Vector contacts = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(CareDetailsPeer.PERSON_ID, ContactStatusPeer.PERSON_ID);
		crit.add(ContactStatusPeer.COMPANY_ID, _practice.getId());
		crit.add(ContactStatusPeer.CONTACT_ATTEMPT, 4);
		Iterator practice_areas_itr = _practitioner.getPracticeAreas().iterator();
		for (int i = 0; practice_areas_itr.hasNext(); i++)
		{
			PracticeAreaBean practice_area = (PracticeAreaBean)practice_areas_itr.next();
			if (i == 0)
				crit.add(ContactStatusPeer.PRACTICE_AREA_ID, practice_area.getId());
			else
				crit.or(ContactStatusPeer.PRACTICE_AREA_ID, practice_area.getId());
		}
		crit.add(CareDetailsPeer.PREFERRED_PRACTITIONER_ID, _practitioner.getId());
		crit.addDescendingOrderByColumn(ContactStatusPeer.CONTACT_DATE);
		Iterator obj_itr = ContactStatusPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			ContactStatusBean contact = ContactStatusBean.getContactStatus((ContactStatus)obj_itr.next());
			UKOnlinePersonBean client = contact.getPerson();
			if (!clients.contains(client) && !client.isActive())
			{
				clients.add(client);
				contacts.add(contact);
			}
		}

		return contacts;
    }
    
    // SQL
    
    /*
     * <table name="CONTACT_STATUS" idMethod="native">
    <column name="CONTACT_STATUS_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
	
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="ADMIN_PERSON_ID" required="false" type="INTEGER" />
	
    <column name="STATUS" required="true" type="VARCHAR" size="50"/>
    <column name="CONTACT_DATE" required="true" type="DATE"/>
    
    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="ADMIN_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
     */
	
    // INSTANCE VARIABLES
	
    private ContactStatus status;
    
    // CONSTRUCTORS
    
    public
    ContactStatusBean()
    {
		status = new ContactStatus();
		isNew = true;
    }
    
    public
    ContactStatusBean(ContactStatus _status)
    {
		status = _status;
		isNew = false;
    }
    
    // INSTANCE METHODS
	
	public Date
	getContactDate()
	{
		return status.getContactDate();
	}
    
    public int
    getId()
    {
		return status.getContactStatusId();
    }
    
    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		String contact_attempt_str = "";
		if (this.hasContactAttempt())
		{
			switch (status.getContactAttempt())
			{
				case 1: contact_attempt_str = "1st Attempt "; break;
				case 2: contact_attempt_str = "2nd Attempt "; break;
				case 3: contact_attempt_str = "3rd Attempt "; break;
				case 4: contact_attempt_str = "Practitioner Call List "; break;
			}
		}

		if (this.getToDoDate() != null)
			return CUBean.getUserDateString(status.getContactDate()) + " " + contact_attempt_str + (this.hasPracticeArea() ? this.getPracticeArea().getLabel() + ":" : "") + status.getStatus() + " (Contact again on " + CUBean.getUserDateString(status.getToDoDate()) + ")";
		else
			return CUBean.getUserDateString(status.getContactDate()) + " " + contact_attempt_str + (this.hasPracticeArea() ? this.getPracticeArea().getLabel() + ":" : "") + status.getStatus();
    }

    public String
    getLabelCallList()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		if (this.getToDoDate() != null)
			return CUBean.getUserDateString(status.getContactDate()) + " " + this.getPerson().getLabel() + ": " + status.getStatus() + " (Contact again on " + CUBean.getUserDateString(status.getToDoDate()) + ")";
		else
			return CUBean.getUserDateString(status.getContactDate()) + " " + this.getPerson().getLabel() + ": " + status.getStatus();
    }
	
	public UKOnlinePersonBean
	getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(status.getPersonId());
	}

	public PracticeAreaBean
	getPracticeArea()
		throws TorqueException, ObjectNotFoundException
	{
		return PracticeAreaBean.getPracticeArea(status.getPracticeAreaId());
	}
	
	public String
	getStatus()
	{
		return status.getStatus();
	}

	public Date
	getToDoDate()
	{
		return status.getToDoDate();
	}
    
    public String
    getValue()
    {
		return this.getId() + "";
    }

	public boolean
	hasContactAttempt()
	{
		return (status.getContactAttempt() > 0);
	}

	public boolean
	hasPracticeArea()
	{
		return (status.getPracticeAreaId() > 0);
	}
    
    protected void
    insertObject()
		throws Exception
    {
		status.save();
    }
    
    public void
    setAdminPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		status.setAdminPersonId(_person.getId());
    }
	
	public void
	setCompany(CompanyBean _company)
		throws TorqueException
	{
		status.setCompanyId(_company.getId());
	}

	public void
	setContactAttempt(int _attempt)
	{
		status.setContactAttempt(_attempt);
	}
	
	public void
	setContactDate(Date _date)
	{
		status.setContactDate(_date);
	}
    
    public void
    setPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		status.setPersonId(_person.getId());
    }

	public void
	setPracticeArea(PracticeAreaBean _practice_area)
		throws TorqueException
	{
		status.setPracticeAreaId(_practice_area.getId());
	}
	
	public void
	setStatus(String _status)
	{
		status.setStatus(_status);
	}

	public void
	setToDoDate(Date _date)
	{
		status.setToDoDate(_date);
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		status.save();
    }
}