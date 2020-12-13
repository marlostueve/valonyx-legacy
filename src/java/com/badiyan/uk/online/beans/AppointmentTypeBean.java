/*
 * AppointmentTypeBean.java
 *
 * Created on November 10, 2007, 8:43 AM
 *
 * To change this template, choose Tools | Template Manager
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
AppointmentTypeBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    public static final short CLIENT_APPOINTMENT_TYPE_TYPE = 1;
    public static final short OFFTIME_APPOINTMENT_TYPE_TYPE = 2;
    public static final short MEETING_APPOINTMENT_TYPE_TYPE = 3;

    protected static HashMap<Integer,AppointmentTypeBean> hash = new HashMap<Integer,AppointmentTypeBean>(11);
	
	//protected static AppointmentTypeBean practitioner_offtime_appt_type;
	protected static HashMap practitioner_offtime_appt_type_hash = new HashMap(11);
    public static final String PRACTITIONER_OFFTIME_APPOINTMENT_TYPE_NAME = "Practitioner Off-Time";

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_ID, _id);
		AppointmentTypePeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }

	public static AppointmentTypeBean
	getAppointmentType(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		AppointmentTypeBean type = (AppointmentTypeBean)hash.get(key);
		if (type == null) {
			Criteria crit = new Criteria();
			crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_ID, _id);
			List objList = AppointmentTypePeer.doSelect(crit);
			if (objList.size() == 0) {
				throw new ObjectNotFoundException("Could not locate Appointment Type with id: " + _id);
			}

			type = AppointmentTypeBean.getAppointmentType((AppointmentType) objList.get(0));
		}

		return type;
	}

    private static AppointmentTypeBean
    getAppointmentType(AppointmentType _type)
		throws TorqueException
    {
		Integer key = new Integer(_type.getAppointmentTypeId());
		AppointmentTypeBean type = (AppointmentTypeBean)hash.get(key);
		if (type == null)
		{
			type = new AppointmentTypeBean(_type);
			hash.put(key, type);
		}

		return type;
    }
    
    public static AppointmentTypeBean
    getAppointmentType(CompanyBean _company, String _type_name)
		throws TorqueException, ObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(AppointmentTypePeer.COMPANY_ID, _company.getId());
		crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_NAME, _type_name);
		List l = AppointmentTypePeer.doSelect(crit);
		if (l.size() == 1) {
			return AppointmentTypeBean.getAppointmentType((AppointmentType)l.get(0));
		}
		throw new ObjectNotFoundException("Unable to locate AppointmentType for name >" + _type_name);
    }
    
    public static Vector
    getAppointmentTypes(CompanyBean _company)
		throws TorqueException
    {
		try {
			AppointmentTypeBean.getPractitionerScheduleOfftimeAppointmentType(_company);
		} catch (Exception x) {
			x.printStackTrace();
		}

		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentTypePeer.COMPANY_ID, _company.getId());
		crit.addDescendingOrderByColumn(AppointmentTypePeer.PRACTICE_AREA_ID);
		crit.addAscendingOrderByColumn(AppointmentTypePeer.APPOINTMENT_TYPE_NAME);
		Iterator itr = AppointmentTypePeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentTypeBean.getAppointmentType((AppointmentType)itr.next()));

		return vec;
    }

    public static Vector
    getAppointmentTypesNoOfftime(CompanyBean _company)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentTypePeer.COMPANY_ID, _company.getId());
		crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_TYPE, AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE, Criteria.NOT_EQUAL);
		crit.addDescendingOrderByColumn(AppointmentTypePeer.PRACTICE_AREA_ID);
		crit.addAscendingOrderByColumn(AppointmentTypePeer.APPOINTMENT_TYPE_NAME);
		Iterator itr = AppointmentTypePeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentTypeBean.getAppointmentType((AppointmentType)itr.next()));

		return vec;
    }
    
    public static Vector
    getAppointmentTypes(CompanyBean _company, short _type)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentTypePeer.COMPANY_ID, _company.getId());
		crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_TYPE, _type);
		crit.addAscendingOrderByColumn(AppointmentTypePeer.APPOINTMENT_TYPE_NAME);
		Iterator itr = AppointmentTypePeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentTypeBean.getAppointmentType((AppointmentType)itr.next()));

		return vec;
    }
    
    public static Vector getAppointmentTypes(UKOnlinePersonBean _practitioner) throws TorqueException, ObjectNotFoundException {
		
		Vector practice_areas = PracticeAreaBean.getPracticeAreas(_practitioner);
		int[] arr = new int[practice_areas.size()];
		for (int i = 0; i < practice_areas.size(); i++) {
			PracticeAreaBean practice_area = (PracticeAreaBean)practice_areas.elementAt(i);
			arr[i] = practice_area.getId();
		}

		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(AppointmentTypePeer.PRACTICE_AREA_ID, (Object)arr, Criteria.IN);
		//crit.addDescendingOrderByColumn(AppointmentTypePeer.PRACTICE_AREA_ID); // why sort by this??
		crit.addAscendingOrderByColumn(AppointmentTypePeer.APPOINTMENT_TYPE_NAME);
		Iterator itr = AppointmentTypePeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(AppointmentTypeBean.getAppointmentType((AppointmentType)itr.next()));
		}
		return vec;
    }

	public static AppointmentTypeBean
	getPractitionerScheduleOfftimeAppointmentType(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		AppointmentTypeBean practitioner_offtime_appt_type = (AppointmentTypeBean)practitioner_offtime_appt_type_hash.get(_company);
		if (practitioner_offtime_appt_type == null)
		{
			Criteria crit = new Criteria();
			crit.add(AppointmentTypePeer.COMPANY_ID, _company.getId());
			crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_NAME, AppointmentTypeBean.PRACTITIONER_OFFTIME_APPOINTMENT_TYPE_NAME);
			List objList = AppointmentTypePeer.doSelect(crit);
			if (objList.size() == 0)
			{
				practitioner_offtime_appt_type = new AppointmentTypeBean();
				practitioner_offtime_appt_type.setCompany(_company);
			    practitioner_offtime_appt_type.setName(AppointmentTypeBean.PRACTITIONER_OFFTIME_APPOINTMENT_TYPE_NAME);
			    practitioner_offtime_appt_type.setBGColorCode("DDDDDD");
			    practitioner_offtime_appt_type.setTextColorCode("000000");
				practitioner_offtime_appt_type.setType(AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE);
			    //practitioner_offtime_appt_type.setPracticeAreaId(practice_area.intValue());
				practitioner_offtime_appt_type.save();
			}
			else if (objList.size() == 1)
				practitioner_offtime_appt_type = AppointmentTypeBean.getAppointmentType((AppointmentType)objList.get(0));
			else
				throw new UniqueObjectNotFoundException("Unique appointment type not found :" + AppointmentTypeBean.PRACTITIONER_OFFTIME_APPOINTMENT_TYPE_NAME);

			practitioner_offtime_appt_type_hash.put(_company, practitioner_offtime_appt_type);
		}

		return practitioner_offtime_appt_type;
	}
    
    // SQL
    
    /*
     *        <table name="APPOINTMENT_TYPE" idMethod="native">
	    <column name="APPOINTMENT_TYPE_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	    
            <column name="COMPANY_ID" required="true" type="INTEGER"/>
	    
	    <column name="APPOINTMENT_TYPE_NAME" required="true" size="50" type="VARCHAR" />
	    <column name="DEFAULT_DURATION_MINUTES" required="true" type="SMALLINT" />
	    <column name="BG_COLOR_CODE" required="true" size="6" type="VARCHAR" />
	    <column name="TEXT_COLOR_CODE" required="true" size="6" type="VARCHAR" />
	    <column name="IMAGE_URL" required="false" size="255" type="VARCHAR" />
	    <column name="PRACTICE_AREA_ID" required="true" type="INTEGER" />
	    
	    <column name="CREATION_DATE" required="true" type="DATE"/>
	    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
	    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
	    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

            <foreign-key foreignTable="COMPANY">
                <reference local="COMPANY_ID" foreign="COMPANYID"/>
            </foreign-key>
	    <foreign-key foreignTable="PRACTICE_AREA">
		<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
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
	
    private AppointmentType type;
    
    private Vector allowed_appointment_types = null;
    
    // CONSTRUCTORS
    
    public
    AppointmentTypeBean()
    {
		type = new AppointmentType();
		isNew = true;
    }
    
    public
    AppointmentTypeBean(AppointmentType _type)
    {
		type = _type;
		isNew = false;
    }
    
    // INSTANCE METHODS
	
	public void
	invalidate() {
		allowed_appointment_types = null;
	}
    
    public boolean
    equals(Object _obj)
    {
		if (_obj == null)
			return false;
		if (_obj instanceof AppointmentTypeBean)
			return (this.getId() == ((AppointmentTypeBean)_obj).getId());
		else
			return false;
    }
    
    public Vector
    getAllowedAppointmentTypes()
	throws TorqueException, ObjectNotFoundException
    {
		/*
		 *<table name="ALLOWED_APPOINTMENTS_OFF_HOURS_TYPE">
			<column name="APPOINTMENT_TYPE_ID" required="true" primaryKey="true" type="INTEGER"/>
			<column name="ALLOWED_APPOINTMENT_TYPE_ID" required="true" primaryKey="true" type="INTEGER"/>

			<foreign-key foreignTable="APPOINTMENT_TYPE">
			<reference local="APPOINTMENT_TYPE_ID" foreign="APPOINTMENT_TYPE_ID"/>
			</foreign-key>
			<foreign-key foreignTable="APPOINTMENT_TYPE">
			<reference local="ALLOWED_APPOINTMENT_TYPE_ID" foreign="APPOINTMENT_TYPE_ID"/>
			</foreign-key>
		</table>
		 */

		if (allowed_appointment_types == null)
		{
			allowed_appointment_types = new Vector();

			Criteria crit = new Criteria();
			crit.add(AllowedAppointmentsOffHoursTypePeer.APPOINTMENT_TYPE_ID, this.getId());
			Iterator itr = AllowedAppointmentsOffHoursTypePeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				AllowedAppointmentsOffHoursType obj = (AllowedAppointmentsOffHoursType)itr.next();
				allowed_appointment_types.addElement(AppointmentTypeBean.getAppointmentType(obj.getAllowedAppointmentTypeId()));
			}
		}

		return allowed_appointment_types;
    }
    
    public String
    getBGColorCodeString()
    {
		String str = type.getBgColorCode();
		if (str == null)
			return "";
		return str;
    }
    
    public CompanyBean
    getCompany()
	throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return CompanyBean.getCompany(type.getCompanyId());
    }
    
    public short
    getDuration()
    {
		return type.getDefaultDurationMinutes();
    }
    
    public String
    getDurationString()
    {
		short duration = type.getDefaultDurationMinutes();
		if (duration == 0)
			return "";
		return duration + "";
    }
    
    public int
    getId()
    {
		return type.getAppointmentTypeId();
    }
    
    public String
    getImageUrlString()
    {
		String str = type.getImageUrl();
		if (str == null)
			return "";
		return str;
    }
    
    public String
    getLabel()
    {
		String str = type.getAppointmentTypeName();
		if (str == null)
			return "";
		return str;
    }
    
    public PracticeAreaBean
    getPracticeArea()
		throws TorqueException, ObjectNotFoundException
    {
		return PracticeAreaBean.getPracticeArea(type.getPracticeAreaId());
    }
    
    public int
    getPracticeAreaId()
    {
		return type.getPracticeAreaId();
    }
    
    public String
    getTextColorCodeString()
    {
		String str = type.getTextColorCode();
		if (str == null)
			return "";
		return str;
    }
    
    public short
    getType()
    {
		return type.getAppointmentTypeType();
    }
    
    public String
    getValue()
    {
		return type.getAppointmentTypeId() + "";
    }
    
    protected void
    insertObject()
		throws Exception
    {
		type.setCreationDate(new Date());
		type.save();

		this.saveAllowedAppointmentTypes();
    }

    public boolean
    isActive()
    {
		return (type.getIsActive() == (short)1);
    }
    
    public boolean
    isAllowedAppointmentType(AppointmentTypeBean _appt_type)
		throws TorqueException, ObjectNotFoundException
    {
		/*
		Vector allowed_appointment_types = this.getAllowedAppointmentTypes();
		return allowed_appointment_types.contains(_appt_type);
		*/
		return true; // changed 11/7/18 - seems to be less confusing for everyone to just allow scheduling during off-times
    }

    public boolean
    isDoubleBookingAllowed()
    {
		return (type.getAllowDoubleBooking() == (short)1);
    }
	
	public boolean
	isClientAppointmentType() {
		return (this.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE);
	}
	
	public boolean
	isMeetingType() {
		return (this.getType() == AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE);
	}

    public void
    setAllowedAppointmentTypes(Vector _allowed_appointment_types)
		throws IllegalValueException
    {
		if (this.getType() != AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
		{
			if (_allowed_appointment_types.size() > 0)
			throw new IllegalValueException("You can only specify allowed appointments for Practitioner Off-Hours appointment types.");
		}
		allowed_appointment_types = _allowed_appointment_types;
    }

    private void
    saveAllowedAppointmentTypes()
		throws TorqueException, Exception
    {
		/*
		 *<table name="ALLOWED_APPOINTMENTS_OFF_HOURS_TYPE">
			<column name="APPOINTMENT_TYPE_ID" required="true" primaryKey="true" type="INTEGER"/>
			<column name="ALLOWED_APPOINTMENT_TYPE_ID" required="true" primaryKey="true" type="INTEGER"/>

			<foreign-key foreignTable="APPOINTMENT_TYPE">
			<reference local="APPOINTMENT_TYPE_ID" foreign="APPOINTMENT_TYPE_ID"/>
			</foreign-key>
			<foreign-key foreignTable="APPOINTMENT_TYPE">
			<reference local="ALLOWED_APPOINTMENT_TYPE_ID" foreign="APPOINTMENT_TYPE_ID"/>
			</foreign-key>
		</table>
		 */

		if (this.allowed_appointment_types != null)
		{
			HashMap db_allowed_appointments_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(AllowedAppointmentsOffHoursTypePeer.APPOINTMENT_TYPE_ID, this.getId());
			System.out.println("crit >" + crit.toString());
			Iterator itr = AllowedAppointmentsOffHoursTypePeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				AllowedAppointmentsOffHoursType value = (AllowedAppointmentsOffHoursType)itr.next();
				Integer key = new Integer(value.getAllowedAppointmentTypeId());
				db_allowed_appointments_hash.put(key, value);
			}

			System.out.println("num allowed >" + this.allowed_appointment_types.size());
			itr = allowed_appointment_types.iterator();
			while (itr.hasNext())
			{
				AppointmentTypeBean allowed_appt_type = (AppointmentTypeBean)itr.next();
				Integer key = new Integer(allowed_appt_type.getId());
				AllowedAppointmentsOffHoursType obj = (AllowedAppointmentsOffHoursType)db_allowed_appointments_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("creating new ass for " + allowed_appt_type.getLabel());

					obj = new AllowedAppointmentsOffHoursType();
					obj.setAppointmentTypeId(this.getId());
					obj.setAllowedAppointmentTypeId(allowed_appt_type.getId());
					obj.save();
				}
			}

			itr = db_allowed_appointments_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				AllowedAppointmentsOffHoursType obj = (AllowedAppointmentsOffHoursType)db_allowed_appointments_hash.get(key);
				AllowedAppointmentsOffHoursTypePeer.doDelete(obj);
			}
		}
    }

    public void
    setActive(boolean _active)
    {
		type.setIsActive(_active ? (short)1 : (short)0);
    }
    
    public void
    setBGColorCode(String _str)
    {
		type.setBgColorCode(_str);
    }
    
    public void
    setCompany(CompanyBean _company)
		throws TorqueException
    {
		type.setCompanyId(_company.getId());
    }
    
    public void
    setCreatePerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		type.setCreatePersonId(_person.getId());
    }

    public void
    setDoubleBookingAllowed(boolean _allow)
    {
		type.setAllowDoubleBooking(_allow ? (short)1 : (short)0);
    }
    
    public void
    setDuration(short _duration)
    {
		type.setDefaultDurationMinutes(_duration);
    }
    
    public void
    setImageUrl(String _url)
    {
		type.setImageUrl(_url);
    }
    
    public void
    setModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		type.setModifyPersonId(_person.getId());
    }
    
    public void
    setName(String _name)
    {
		type.setAppointmentTypeName(_name);
    }
    
    public void
    setPracticeAreaId(int _practice_area_id)
		throws TorqueException
    {
		type.setPracticeAreaId(_practice_area_id);
    }
    
    public void
    setTextColorCode(String _str)
    {
		type.setTextColorCode(_str);
    }
    
    public void
    setType(short _type)
    {
		type.setAppointmentTypeType(_type);
    }
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		type.setModificationDate(new Date());
		type.save();

		this.saveAllowedAppointmentTypes();
    }

    public void
    setAllowClientScheduling(boolean _allow) {
		type.setAllowClientScheduling(_allow ? (short)1 : (short)0);
    }
	
	public boolean
	allowClientScheduling() {
		return (type.getAllowClientScheduling() == (short)1);
	}
}