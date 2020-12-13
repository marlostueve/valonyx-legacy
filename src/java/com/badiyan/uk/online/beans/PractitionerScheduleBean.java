/*
 * AppointmentBean.java
 *
 * Created on August 2, 2008, 9:45 PM
 *
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
PractitionerScheduleBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    protected static HashMap<Integer,PractitionerScheduleBean> hash = new HashMap<Integer,PractitionerScheduleBean>(11);
	protected static HashMap<String,PractitionerScheduleBean> prac_schedule_date_hash = new HashMap<String,PractitionerScheduleBean>(11);

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(PractitionerSchedulePeer.PRACTITIONER_SCHEDULE_ID, _id);
		PractitionerSchedulePeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }
    
    public static PractitionerScheduleBean
    getActivePractitionerSchedule(UKOnlinePersonBean _practitioner)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		// is there a non-primary schedule for now?
		
		Calendar now = Calendar.getInstance();
		
		Criteria crit = new Criteria();
		crit.add(PractitionerSchedulePeer.PRACTITIONER_ID, _practitioner.getId());
		crit.add(PractitionerSchedulePeer.IS_BASE_SCHEDULE, (short)0);
		crit.add(PractitionerSchedulePeer.START_DATE, now.getTime(), Criteria.LESS_EQUAL);
		crit.add(PractitionerSchedulePeer.END_DATE, now.getTime(), Criteria.GREATER_EQUAL);
		List obj_list = PractitionerSchedulePeer.doSelect(crit);
		if (obj_list.size() == 1)
			return PractitionerScheduleBean.getPractitionerSchedule((PractitionerSchedule)obj_list.get(0));
		else if (obj_list.size() > 1)
			throw new UniqueObjectNotFoundException(_practitioner.getLabel() + " has multiple schedules in effect at the moment.");
		
		crit = new Criteria();
		crit.add(PractitionerSchedulePeer.PRACTITIONER_ID, _practitioner.getId());
		crit.add(PractitionerSchedulePeer.IS_BASE_SCHEDULE, (short)1);
		obj_list = PractitionerSchedulePeer.doSelect(crit);
		if (obj_list.size() == 1)
			return PractitionerScheduleBean.getPractitionerSchedule((PractitionerSchedule)obj_list.get(0));
		else if (obj_list.size() > 1)
			throw new UniqueObjectNotFoundException(_practitioner.getLabel() + " has multiple base schedules in effect.");
		
		throw new ObjectNotFoundException("Unable to determine active schedule for " + _practitioner.getLabel());
    }

    public static PractitionerScheduleBean
    getPractitionerSchedule(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		PractitionerScheduleBean schedule = (PractitionerScheduleBean)hash.get(key);
		if (schedule == null)
		{
			Criteria crit = new Criteria();
			crit.add(PractitionerSchedulePeer.PRACTITIONER_SCHEDULE_ID, _id);
			List objList = PractitionerSchedulePeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate practitioner schedule with id: " + _id);

			schedule = PractitionerScheduleBean.getPractitionerSchedule((PractitionerSchedule)objList.get(0));
		}

		return schedule;
    }

    private static PractitionerScheduleBean
    getPractitionerSchedule(PractitionerSchedule _practitioner_schedule)
		throws TorqueException
    {
		Integer key = new Integer(_practitioner_schedule.getPractitionerScheduleId());
		PractitionerScheduleBean practitioner_schedule = (PractitionerScheduleBean)hash.get(key);
		if (practitioner_schedule == null)
		{
			practitioner_schedule = new PractitionerScheduleBean(_practitioner_schedule);
			hash.put(key, practitioner_schedule);
		}

		return practitioner_schedule;
    }
    
    public static PractitionerScheduleBean
    getPractitionerSchedule(UKOnlinePersonBean _practitioner, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		// is there a non-primary schedule for now?
		
		/*
		try
		{
			throw new IllegalValueException("show st");
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		 */
		
		// look in the hash
		
		String key = _practitioner.getId() + "|" + CUBean.getUserDateString(_date);
		PractitionerScheduleBean schedule = prac_schedule_date_hash.get(key);
		if (schedule == null) {
			Calendar start_of_day = Calendar.getInstance();
			start_of_day.setTime(_date);
			start_of_day.set(Calendar.HOUR_OF_DAY, 0);
			start_of_day.set(Calendar.MINUTE, 0);
			start_of_day.set(Calendar.SECOND, 0);

			Calendar end_of_day = Calendar.getInstance();
			end_of_day.setTime(_date);
			end_of_day.set(Calendar.HOUR_OF_DAY, 23);
			end_of_day.set(Calendar.MINUTE, 59);
			end_of_day.set(Calendar.SECOND, 59);

			Criteria crit = new Criteria();
			crit.add(PractitionerSchedulePeer.PRACTITIONER_ID, _practitioner.getId());
			crit.add(PractitionerSchedulePeer.IS_BASE_SCHEDULE, (short)0);
			crit.add(PractitionerSchedulePeer.START_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
			crit.add(PractitionerSchedulePeer.END_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
			List obj_list = PractitionerSchedulePeer.doSelect(crit);
			if (obj_list.size() == 1) {
				schedule = PractitionerScheduleBean.getPractitionerSchedule((PractitionerSchedule)obj_list.get(0));
				prac_schedule_date_hash.put(key, schedule);
				return schedule;
			} else if (obj_list.size() > 1) {
				throw new UniqueObjectNotFoundException(_practitioner.getLabel() + " has multiple schedules in effect for " + CUBean.getUserDateString(_date));
			}

			crit = new Criteria();
			crit.add(PractitionerSchedulePeer.PRACTITIONER_ID, _practitioner.getId());
			crit.add(PractitionerSchedulePeer.IS_BASE_SCHEDULE, (short)1);
			obj_list = PractitionerSchedulePeer.doSelect(crit);
			if (obj_list.size() == 1) {
				schedule = PractitionerScheduleBean.getPractitionerSchedule((PractitionerSchedule)obj_list.get(0));
				prac_schedule_date_hash.put(key, schedule);
				return schedule;
			} else if (obj_list.size() > 1) {
				throw new UniqueObjectNotFoundException(_practitioner.getLabel() + " has multiple base schedules in effect.");
			}

			throw new ObjectNotFoundException("Unable to determine schedule for " + _practitioner.getLabel() + " for " + CUBean.getUserDateString(_date));
			
		} else {
			return schedule;
		}
    }
	
	public static void
	invalidateHash()
	{
		prac_schedule_date_hash.clear();
	}
    
    public static Vector
    getPractitionerSchedules(UKOnlinePersonBean _practitioner)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector schedules = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(PractitionerSchedulePeer.PRACTITIONER_ID, _practitioner.getId());
		crit.addAscendingOrderByColumn(PractitionerSchedulePeer.NAME);
		Iterator obj_itr = PractitionerSchedulePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			schedules.addElement(PractitionerScheduleBean.getPractitionerSchedule((PractitionerSchedule)obj_itr.next()));
		
		return schedules;
    }
    
    public static Vector
    getPractitionerSchedules(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector schedules = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(PractitionerSchedulePeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(PractitionerSchedulePeer.NAME);
		Iterator obj_itr = PractitionerSchedulePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			schedules.addElement(PractitionerScheduleBean.getPractitionerSchedule((PractitionerSchedule)obj_itr.next()));
		
		return schedules;
    }
    
    public static boolean
    hasBaseSchedule(UKOnlinePersonBean _practitioner)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(PractitionerSchedulePeer.PRACTITIONER_ID, _practitioner.getId());
		crit.add(PractitionerSchedulePeer.IS_BASE_SCHEDULE, (short)1);
		Iterator obj_itr = PractitionerSchedulePeer.doSelect(crit).iterator();
		return (obj_itr.hasNext());
    }
    
    // SQL
    
    /*
     * <table name="PRACTITIONER_SCHEDULE" idMethod="native">
    <column name="PRACTITIONER_SCHEDULE_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PRACTITIONER_ID" required="true" type="INTEGER"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
	
	<column name="NAME" required="true" type="VARCHAR" size="50"/>
	<column name="IS_BASE_SCHEDULE" required="true" type="SMALLINT" />
    <column name="START_DATE" required="false" type="DATE"/>
	<column name="END_DATE" required="false" type="DATE"/>
    
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
    
    <foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
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
	
    private PractitionerSchedule practitioner_schedule;
	private Vector items;
    
    // CONSTRUCTORS
    
    public
    PractitionerScheduleBean()
    {
		practitioner_schedule = new PractitionerSchedule();
		isNew = true;
    }
    
    public
    PractitionerScheduleBean(PractitionerSchedule _practitioner_schedule)
    {
		practitioner_schedule = _practitioner_schedule;
		isNew = false;
    }
    
    // INSTANCE METHODS
	
	public void
	addItem(PractitionerScheduleItemBean _item)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		
		// I need to determine if there's a conflict.  what day of week is the new item for?
		
		boolean is_template_item = _item.hasTemplate();
		
		Calendar start_a = Calendar.getInstance();
		Calendar end_a = Calendar.getInstance();
		
		Calendar start_b = Calendar.getInstance();
		Calendar end_b = Calendar.getInstance();
		
		Iterator items_itr = this.getItems().iterator();
		while (items_itr.hasNext()) {
			
			PractitionerScheduleItemBean item_obj = (PractitionerScheduleItemBean)items_itr.next();
			
			// if one of the items is a template and the other isn't, there's no conflict I'm thinking
			
			if ( (item_obj.getDayOfWeek() == _item.getDayOfWeek()) && (item_obj.hasTemplate() == _item.hasTemplate()) ) { // sad, hoping to use ^ (XOR operator) - this is essentially an XNOR, one day...
				
				// the day of the week matches.  is there a time conflict?
				
				start_a.set(Calendar.HOUR_OF_DAY, item_obj.getStartHourOfDay());
				start_a.set(Calendar.MINUTE, item_obj.getStartMinute());
				end_a.set(Calendar.HOUR_OF_DAY, item_obj.getEndHourOfDay());
				end_a.set(Calendar.MINUTE, item_obj.getEndMinute());
				
				System.out.println("_item.getStartHourOfDay() >" + _item.getStartHourOfDay());
				System.out.println("_item.getEndHourOfDay() >" + _item.getEndHourOfDay());
				
				start_b.set(Calendar.HOUR_OF_DAY, _item.getStartHourOfDay());
				start_b.set(Calendar.MINUTE, _item.getStartMinute());
				end_b.set(Calendar.HOUR_OF_DAY, _item.getEndHourOfDay());
				end_b.set(Calendar.MINUTE, _item.getEndMinute());
				
				if (!(start_b.compareTo(end_b) < 0)) {
					throw new IllegalValueException("Start time must be before end time.");
				}
				
				int start_compare = start_a.compareTo(start_b);
				
				if (start_compare < 0) {
					// start_a is before start_b.  end_a must be before or equal to start_b
					
					if (end_a.compareTo(start_b) > 0) {
						throw new IllegalValueException(this.getPractitioner().getLabel() + " is already scheduled from " + PractitionerScheduleItemBean.schedule_item_label_start.format(start_a.getTime()) + " to " + PractitionerScheduleItemBean.schedule_item_label_end.format(end_a.getTime()) );
					}
				} else if (start_compare > 0) {
					// start_a is after start_b.  end_b must be before or equals to start_a
					
					if (end_b.compareTo(start_a) > 0)
						throw new IllegalValueException(this.getPractitioner().getLabel() + " is already scheduled from " + PractitionerScheduleItemBean.schedule_item_label_start.format(start_a.getTime()) + " to " + PractitionerScheduleItemBean.schedule_item_label_end.format(end_a.getTime()) );
				} else {
					// start_a coincides with start_b, this is an automatic conflict
					
					throw new IllegalValueException(this.getPractitioner().getLabel() + " is already scheduled for " + PractitionerScheduleItemBean.schedule_item_label_start.format(start_a.getTime()));
				}
			}
		}
		
		// no conflict.  perform the add
		
		this.items.addElement(_item);
	}
	
	public Date
	getEndDate()
	{
		return practitioner_schedule.getEndDate();
	}
	
	public String
	getEndDateString()
	{
		return CUBean.getUserDateString(practitioner_schedule.getEndDate());
	}
    
    public int
    getId()
    {
		return practitioner_schedule.getPractitionerScheduleId();
    }
    
    public UKOnlinePersonBean
    getPractitioner()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(practitioner_schedule.getPractitionerId());
    }
    
    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return this.getPractitioner().getLabel() + " : " + this.practitioner_schedule.getName();
    }
	
	public String
	getName()
	{
		return this.practitioner_schedule.getName();
	}
	
	public String
	getNameString()
	{
		String str = this.practitioner_schedule.getName();
		if (str == null)
			return "";
		return str;
	}
	
	public Vector
	getItems()
		throws TorqueException, ObjectNotFoundException {
		
		if (this.items == null) {
			this.items = PractitionerScheduleItemBean.getPractitionerScheduleItems(this);
		}

		return this.items;
	}
	
	public Vector
	getItems(int _day_of_week)
		throws TorqueException, ObjectNotFoundException {
		
		Vector vec = new Vector();
		Iterator itr = this.getItems().iterator();
		while (itr.hasNext()) {
			PractitionerScheduleItemBean item = (PractitionerScheduleItemBean)itr.next();
			if (item.getDayOfWeek() == _day_of_week) {
				vec.addElement(item);
			}
		}

		return vec;
	}

	public String
	getWeekDisplayString()
		throws TorqueException, ObjectNotFoundException
	{
		boolean has_sunday = false;
		boolean has_monday = false;
		boolean has_tuesday = false;
		boolean has_wednesday = false;
		boolean has_thursday = false;
		boolean has_friday = false;
		boolean has_saturday = false;


		Iterator itr = this.getItems().iterator();
		while (itr.hasNext())
		{
			PractitionerScheduleItemBean item = (PractitionerScheduleItemBean)itr.next();
			switch (item.getDayOfWeek())
			{
				case Calendar.SUNDAY: has_sunday = true; break;
				case Calendar.MONDAY: has_monday = true; break;
				case Calendar.TUESDAY: has_tuesday = true; break;
				case Calendar.WEDNESDAY: has_wednesday = true; break;
				case Calendar.THURSDAY: has_thursday = true; break;
				case Calendar.FRIDAY: has_friday = true; break;
				case Calendar.SATURDAY: has_saturday = true; break;
			}
		}

		StringBuffer buf = new StringBuffer();
		buf.append(has_sunday ? "<strong>S</strong>" : "-");
		buf.append(has_monday ? "<strong>M</strong>" : "-");
		buf.append(has_tuesday ? "<strong>T</strong>" : "-");
		buf.append(has_wednesday ? "<strong>W</strong>" : "-");
		buf.append(has_thursday ? "<strong>T</strong>" : "-");
		buf.append(has_friday ? "<strong>F</strong>" : "-");
		buf.append(has_saturday ? "<strong>S</strong>" : "-");

		return buf.toString();
	}
	
	public String
	getStartDateString()
	{
		return CUBean.getUserDateString(practitioner_schedule.getStartDate());
	}
    
    public String
    getValue()
    {
		return this.getId() + "";
    }
	
	public boolean
	hasConflict(AppointmentBean _appointment)
		throws TorqueException, ObjectNotFoundException
	{
		Iterator items_itr = this.getItems().iterator();
		while (items_itr.hasNext())
		{
			PractitionerScheduleItemBean schedule_item = (PractitionerScheduleItemBean)items_itr.next();
			
			// day of week of the appointment needs to match the day of week for the schedule item
			
			Calendar appointment_start = Calendar.getInstance();
			appointment_start.setTime(_appointment.getAppointmentDate());
			System.out.println("appt day of week >" + appointment_start.get(Calendar.DAY_OF_WEEK));
			System.out.println("schedule_item.getDayOfWeek() >" + schedule_item.getDayOfWeek());
			if (schedule_item.getDayOfWeek() == appointment_start.get(Calendar.DAY_OF_WEEK))
			{
				// start hour and minute of appointment needs to be after or at schedule start time
				
				System.out.println("appointment_start.get(Calendar.HOUR_OF_DAY) >" + appointment_start.get(Calendar.HOUR_OF_DAY));
				System.out.println("schedule_item.getStartHourOfDay() >" + schedule_item.getStartHourOfDay());
				
				System.out.println("appointment_start.get(Calendar.MINUTE) >" + appointment_start.get(Calendar.MINUTE));
				System.out.println("schedule_item.getStartMinute() >" + schedule_item.getStartMinute());
				
				if ((appointment_start.get(Calendar.HOUR_OF_DAY) > schedule_item.getStartHourOfDay()) || ( (appointment_start.get(Calendar.HOUR_OF_DAY) == schedule_item.getStartHourOfDay()) && (appointment_start.get(Calendar.MINUTE) >= schedule_item.getStartMinute()) ) )
				{
					Calendar appointment_end = Calendar.getInstance();
					appointment_end.setTime(_appointment.getAppointmentDate());
					appointment_end.add(Calendar.MINUTE, _appointment.getDuration());
					
					
					System.out.println("appointment_end.get(Calendar.HOUR_OF_DAY) >" + appointment_end.get(Calendar.HOUR_OF_DAY));
					System.out.println("schedule_item.getEndHourOfDay() >" + schedule_item.getEndHourOfDay());
					
					
					System.out.println("appointment_end.get(Calendar.MINUTE) >" + appointment_end.get(Calendar.MINUTE));
					System.out.println("schedule_item.getEndMinute() >" + schedule_item.getEndMinute());
				
					if ((appointment_end.get(Calendar.HOUR_OF_DAY) < schedule_item.getEndHourOfDay()) || ( (appointment_end.get(Calendar.HOUR_OF_DAY) == schedule_item.getEndHourOfDay()) && (appointment_end.get(Calendar.MINUTE) <= schedule_item.getEndMinute()) ) )
						return false;  // the passed appointment fits into this schedule item - no conflict here
				}
			}
		}
		
		return true;  // the appointment conflicts with this schedule
	}

	public boolean
	hasItems()
		throws TorqueException, ObjectNotFoundException
	{
		Vector items = this.getItems();
		return (items.size() > 0);
	}
    
    protected void
    insertObject()
		throws Exception
    {
		practitioner_schedule.setCreationDate(new Date());
		practitioner_schedule.save();
		
		saveItems();
    }
	
	public void
	invalidate()
	{
		this.items = null;
	}
	
	public boolean
	isBaseSchedule()
	{
		return (practitioner_schedule.getIsBaseSchedule() == (short)1);
	}
	
	private void
    saveItems()
		throws TorqueException, Exception
    {
		/*
		 *<table name="PRACTITIONER_SCHEDULE_ITEM" idMethod="native">
    <column name="PRACTITIONER_SCHEDULE_ITEM_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PRACTITIONER_SCHEDULE_ID" required="true" type="INTEGER"/>
	
    <column name="DAY_OF_WEEK" required="true" type="INTEGER" />
	<column name="START_HOUR_OF_DAY" required="true" type="INTEGER" />
	<column name="START_MINUTE" required="true" type="INTEGER" />
	<column name="END_HOUR_OF_DAY" required="true" type="INTEGER" />
	<column name="END_MINUTE" required="true" type="INTEGER" />
    
    <foreign-key foreignTable="PRACTITIONER_SCHEDULE">
		<reference local="PRACTITIONER_SCHEDULE_ID" foreign="PRACTITIONER_SCHEDULE_ID"/>
    </foreign-key>
</table>
		 */

		if (this.items != null)
		{
			HashMap db_practitioner_schedule_items_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(PractitionerScheduleItemPeer.PRACTITIONER_SCHEDULE_ID, this.getId());
			System.out.println("crit >" + crit.toString());
			Iterator itr = PractitionerScheduleItemPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				PractitionerScheduleItem value = (PractitionerScheduleItem)itr.next();
				Integer key = new Integer(value.getPractitionerScheduleItemId());
				db_practitioner_schedule_items_hash.put(key, value);
			}

			System.out.println("num items >" + this.items.size());
			itr = this.items.iterator();
			while (itr.hasNext())
			{
				PractitionerScheduleItemBean item = (PractitionerScheduleItemBean)itr.next();
				Integer key = new Integer(item.getId());
				PractitionerScheduleItem obj = (PractitionerScheduleItem)db_practitioner_schedule_items_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("creating new ass for " + item);

					item.setPractitionerSchedule(this);
					item.save();
				}
			}

			itr = db_practitioner_schedule_items_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				PractitionerScheduleItem obj = (PractitionerScheduleItem)db_practitioner_schedule_items_hash.get(key);
				PractitionerScheduleItemPeer.doDelete(obj);
			}
		}
    }
	
	public void
	setBaseSchedule(boolean _base_schedule)
	{
		practitioner_schedule.setIsBaseSchedule(_base_schedule ? (short)1 : (short)0);
	}
	
	public void
	setCompany(CompanyBean _company)
		throws TorqueException
	{
		practitioner_schedule.setCompanyId(_company.getId());
	}
    
    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			practitioner_schedule.setCreatePersonId(_person.getId());
		else
			practitioner_schedule.setModifyPersonId(_person.getId());
    }
	
	public void
	setEndDate(Date _date)
	{
		practitioner_schedule.setEndDate(_date);
	}
	
	public void
	setItems(Vector _items)
	{
		items = _items;
	}
	
	public void
	setName(String _name)
	{
		practitioner_schedule.setName(_name);
	}
    
    public void
    setPractitioner(UKOnlinePersonBean _practitioner)
		throws TorqueException
    {
		practitioner_schedule.setPractitionerId(_practitioner.getId());
    }
	
	public void
	setStartDate(Date _date)
	{
		practitioner_schedule.setStartDate(_date);
	}
	
	public Vector
	toOfftimeAppointments(Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		System.out.println("toOfftimeAppointments() invoked >" + CUBean.getUserDateString(_date));
		
		Vector offtime_appointments = new Vector();
		UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(practitioner_schedule.getCompanyId());
		
		Calendar appt_start = Calendar.getInstance();
		appt_start.setTime(_date);
			
		int last_start_hour_of_day = company.getStartHourOfDay();
		int last_start_minute = company.getStartMinute();
		int close_hour_of_day = company.getEndHourOfDay();
		int close_minute = company.getEndMinute();
		
		Iterator itr = this.getItems().iterator();
		while (itr.hasNext()) {
			
			PractitionerScheduleItemBean schedule_item = (PractitionerScheduleItemBean)itr.next();
			
			if (schedule_item.getDayOfWeek() == appt_start.get(Calendar.DAY_OF_WEEK)) {
				

				if (schedule_item.hasTemplate()) {

					appt_start.set(Calendar.HOUR_OF_DAY, schedule_item.getStartHourOfDay());
					appt_start.set(Calendar.MINUTE, schedule_item.getStartMinute());
					appt_start.set(Calendar.SECOND, 0);
					appt_start.set(Calendar.MILLISECOND, 0);

					Calendar appt_end = Calendar.getInstance();
					appt_end.setTime(_date);
					appt_end.set(Calendar.HOUR_OF_DAY, schedule_item.getEndHourOfDay());
					appt_end.set(Calendar.MINUTE, schedule_item.getEndMinute());
					appt_end.set(Calendar.SECOND, 0);
					appt_end.set(Calendar.MILLISECOND, 0);

					AppointmentBean schedule_offtime_appointment = new AppointmentBean();
					schedule_offtime_appointment.setType(schedule_item.getAppointmentTypeTemplate());
					schedule_offtime_appointment.setAppointmentDate(appt_start.getTime());
					short duration_minutes = (short)TimeUnit.MILLISECONDS.toMinutes(appt_end.getTimeInMillis() - appt_start.getTimeInMillis());
					//if (duration_minutes > 5) {
					//	
					//}
					schedule_offtime_appointment.setDuration((short) (duration_minutes - (short)2));
					schedule_offtime_appointment.setPractitioner(this.getPractitioner());
					schedule_offtime_appointment.setCompany(company);
					schedule_offtime_appointment.setIsTemplate(true);

					System.out.println("  %%% - adding template >" + schedule_item.getId());
					offtime_appointments.addElement(schedule_offtime_appointment);

				} else {

					if ((last_start_hour_of_day < schedule_item.getStartHourOfDay()) || ((last_start_hour_of_day == schedule_item.getStartHourOfDay()) && (last_start_minute < schedule_item.getStartMinute()))) {

						appt_start.set(Calendar.HOUR_OF_DAY, last_start_hour_of_day);
						appt_start.set(Calendar.MINUTE, last_start_minute);
						appt_start.set(Calendar.SECOND, 0);
						appt_start.set(Calendar.MILLISECOND, 0);

						AppointmentBean schedule_offtime_appointment = new AppointmentBean();
						schedule_offtime_appointment.setType(AppointmentTypeBean.getPractitionerScheduleOfftimeAppointmentType(company));
						schedule_offtime_appointment.setAppointmentDate(appt_start.getTime());
						schedule_offtime_appointment.setDuration( (short)(((schedule_item.getStartHourOfDay() - last_start_hour_of_day) * 60 ) + ( schedule_item.getStartMinute() - last_start_minute )) );
						schedule_offtime_appointment.setPractitioner(this.getPractitioner());
						schedule_offtime_appointment.setCompany(company);

						offtime_appointments.addElement(schedule_offtime_appointment);
					}

					last_start_hour_of_day = schedule_item.getEndHourOfDay();
					last_start_minute = schedule_item.getEndMinute();
				}
			}
			
		}
		
		if ((last_start_hour_of_day < close_hour_of_day) || ((last_start_hour_of_day == close_hour_of_day) && (last_start_minute < close_minute))) {
			
			appt_start.set(Calendar.HOUR_OF_DAY, last_start_hour_of_day);
			appt_start.set(Calendar.MINUTE, last_start_minute);
			appt_start.set(Calendar.SECOND, 0);
			appt_start.set(Calendar.MILLISECOND, 0);
				
			AppointmentBean schedule_offtime_appointment = new AppointmentBean();
			schedule_offtime_appointment.setType(AppointmentTypeBean.getPractitionerScheduleOfftimeAppointmentType(company));
			schedule_offtime_appointment.setAppointmentDate(appt_start.getTime());
			schedule_offtime_appointment.setDuration( (short)(((close_hour_of_day - last_start_hour_of_day) * 60 ) + ( close_minute - last_start_minute )) );
			schedule_offtime_appointment.setPractitioner(this.getPractitioner());
			schedule_offtime_appointment.setCompany(company);

			offtime_appointments.addElement(schedule_offtime_appointment);
		}
		
		return offtime_appointments;
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		practitioner_schedule.setModificationDate(new Date());
		practitioner_schedule.save();
		
		saveItems();
    }
}