/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
AppointmentWaitList
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,AppointmentWaitList> hash = new HashMap<Integer,AppointmentWaitList>(11);

    // CLASS METHODS

    public static AppointmentWaitList
    getAppointmentWaitListEntry(int _id)
		throws TorqueException, ObjectNotFoundException {
		Integer key = new Integer(_id);
		AppointmentWaitList obj = (AppointmentWaitList)hash.get(key);
		if (obj == null) {
			Criteria crit = new Criteria();
			crit.add(AppointmentWaitListDbPeer.APPOINTMENT_WAIT_LIST_DB_ID, _id);
			List objList = AppointmentWaitListDbPeer.doSelect(crit);
			if (objList.size() == 0) {
				throw new ObjectNotFoundException("Could not locate Wait List with id: " + _id);
			}

			obj = AppointmentWaitList.getAppointmentWaitListEntry((AppointmentWaitListDb)objList.get(0));
		}

		return obj;
    }

    private static AppointmentWaitList
    getAppointmentWaitListEntry(AppointmentWaitListDb _obj)
		throws TorqueException {
		Integer key = new Integer(_obj.getAppointmentWaitListDbId());
		AppointmentWaitList obj = (AppointmentWaitList)hash.get(key);
		if (obj == null) {
			obj = new AppointmentWaitList(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static AppointmentWaitList
    addAppointmentWaitListEntry(UKOnlinePersonBean _person, AppointmentTypeBean _appt_type, UKOnlinePersonBean _mod_person) throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		AppointmentWaitList obj = null;
		Criteria crit = new Criteria();
		crit.add(AppointmentWaitListDbPeer.WAITING_PERSON_ID, _person.getId());
		crit.add(AppointmentWaitListDbPeer.APPOINTMENT_TYPE_ID, _appt_type.getId());
		crit.add(AppointmentWaitListDbPeer.IS_ACTIVE, (short)1 );
		List l = AppointmentWaitListDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			obj = new AppointmentWaitList();
			obj.setCreateOrModifyPerson(_mod_person);
			obj.setIsActive(true);
			obj.setType(_appt_type);
			obj.setWaitingPerson(_person);
			obj.setDuration(_appt_type.getDuration());
			obj.save();
		} else {
			obj = AppointmentWaitList.getAppointmentWaitListEntry((AppointmentWaitListDb)l.get(0));
			obj.setDuration(_appt_type.getDuration());
			obj.save();
		}
		return obj;
    }

    public static void
    removeAppointmentWaitList(int _entry_id)
		throws TorqueException {
		
		// remove from the hash
		
		hash.remove(_entry_id);
		
		// remove from the database

		Criteria crit = new Criteria();
		crit.add(AppointmentWaitListDbPeer.APPOINTMENT_WAIT_LIST_DB_ID, _entry_id);
		AppointmentWaitListDbPeer.doDelete(crit);
    }

    public static Vector
    getAppointmentWaitList(UKOnlinePersonBean _person, boolean _active_only)
		throws TorqueException {
		
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentWaitListDbPeer.WAITING_PERSON_ID, _person.getId());
		if (_active_only) {
			crit.add(AppointmentWaitListDbPeer.IS_ACTIVE, (short)1 );
		}
		crit.addAscendingOrderByColumn(AppointmentWaitListDbPeer.CREATION_DATE);
		Iterator itr = AppointmentWaitListDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(AppointmentWaitList.getAppointmentWaitListEntry((AppointmentWaitListDb)itr.next()));
		}

		return vec;
    }

    public static Vector
    getAppointmentWaitList(AppointmentTypeBean _type, boolean _active_only)
		throws TorqueException {
		
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentWaitListDbPeer.APPOINTMENT_TYPE_ID, _type.getId());
		if (_active_only) {
			crit.add(AppointmentWaitListDbPeer.IS_ACTIVE, (short)1 );
		}
		crit.addAscendingOrderByColumn(AppointmentWaitListDbPeer.CREATION_DATE);
		Iterator itr = AppointmentWaitListDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(AppointmentWaitList.getAppointmentWaitListEntry((AppointmentWaitListDb)itr.next()));
		}

		return vec;
    }

	public static Vector
	getPersonsByKeyword(CompanyBean _company, String _keyword, UKOnlinePersonBean _logged_in_person, int _limit)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		if (_keyword != null) {
			String search_string = "%" + _keyword + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.FILE_NUMBER, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b).or(crit_c));
		}
		
		crit.add(PersonPeer.ISACTIVE, (short)1);
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		Vector people = new Vector();
		while (itr.hasNext()) {
			UKOnlinePersonBean anoka_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)itr.next());
			people.addElement(anoka_person);
		}
		return people;
	}

    public static Vector
    searchAppointmentWaitList(CompanyBean _company, String _search_str, int _limit)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(AppointmentWaitListDbPeer.WAITING_PERSON_ID, PersonPeer.PERSONID);
		crit.addJoin(AppointmentWaitListDbPeer.APPOINTMENT_TYPE_ID, AppointmentTypePeer.APPOINTMENT_TYPE_ID);
		
		if ( (_search_str != null) && !_search_str.isEmpty() ) {
			String search_string = "%" + _search_str + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(AppointmentTypePeer.APPOINTMENT_TYPE_NAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b).or(crit_c));
		}
		
		crit.add(AppointmentWaitListDbPeer.IS_ACTIVE, (short)1); // is this set by anything??  it should be set when the sale is done, at least
		
		crit.addAscendingOrderByColumn(AppointmentWaitListDbPeer.CREATION_DATE);
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		System.out.println("crit >" + crit.toString());
		
		Iterator itr = AppointmentWaitListDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			AppointmentWaitList obj = AppointmentWaitList.getAppointmentWaitListEntry((AppointmentWaitListDb)itr.next());
			vec.addElement(obj);
		}

		return vec;
    }

    // SQL

    /*
	 * 
<table name="APPOINTMENT_WAIT_LIST_DB" idMethod="native">
    <column name="APPOINTMENT_WAIT_LIST_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="WAITING_PERSON_ID" required="true" type="INTEGER"/>
    <column name="APPOINTMENT_TYPE_ID" required="true" type="INTEGER"/>
    <column name="DURATION_MINUTES" required="true" type="SMALLINT" />
    <column name="IS_ACTIVE" required="true" type="SMALLINT" default="1" />
	
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="true" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
		<reference local="WAITING_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="APPOINTMENT_TYPE">
		<reference local="APPOINTMENT_TYPE_ID" foreign="APPOINTMENT_TYPE_ID"/>
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

    private AppointmentWaitListDb appointment_wait_list;
	//private Vector members;

    // CONSTRUCTORS

    public
    AppointmentWaitList() {
		appointment_wait_list = new AppointmentWaitListDb();
		isNew = true;
    }

    public
    AppointmentWaitList(AppointmentWaitListDb _appointment_wait_list) {
		appointment_wait_list = _appointment_wait_list;
		isNew = false;
    }

    // INSTANCE METHODS

    public int
    getId() {
		return appointment_wait_list.getAppointmentWaitListDbId();
    }

    public String
    getLabel() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		return CUBean.getUserDateString(appointment_wait_list.getCreationDate()) + " " + "(" + appointment_wait_list.getDurationMinutes() + ") " + UKOnlinePersonBean.getPerson(appointment_wait_list.getWaitingPersonId()).getLabel();
    }

    public String
    getValue() {
		return appointment_wait_list.getAppointmentWaitListDbId() + "";
    }

	@Override
    protected void
    insertObject()
		throws Exception {
		appointment_wait_list.setCreationDate(new Date());
		appointment_wait_list.save();
    }

	public boolean
	isActive() {
		return (appointment_wait_list.getIsActive() == (short)1);
	}

    public short
    getDuration() {
		return appointment_wait_list.getDurationMinutes();
    }

    public AppointmentTypeBean
    getAppointmenType() throws TorqueException, ObjectNotFoundException {
		return AppointmentTypeBean.getAppointmentType(appointment_wait_list.getAppointmentTypeId());
    }

    public UKOnlinePersonBean
    getWaitingPerson() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(appointment_wait_list.getWaitingPersonId());
    }

    public void
    setType(AppointmentTypeBean _appt_type)
		throws TorqueException {
		appointment_wait_list.setAppointmentTypeId(_appt_type.getId());
    }

    public void
    setWaitingPerson(UKOnlinePersonBean _waiting_person)
		throws TorqueException {
		appointment_wait_list.setWaitingPersonId(_waiting_person.getId());
    }

    public void
    setDuration(short _duration) {
		appointment_wait_list.setDurationMinutes(_duration);
    }

    public void
    setIsActive(boolean _active) {
		appointment_wait_list.setIsActive(_active ? (short)1 : (short)0);
    }
    
    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew()) {
			appointment_wait_list.setCreatePersonId(_person.getId());
		} else {
			appointment_wait_list.setModifyPersonId(_person.getId());
		}
    }

	@Override
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		appointment_wait_list.setModificationDate(new Date());
		appointment_wait_list.save();
    }
}