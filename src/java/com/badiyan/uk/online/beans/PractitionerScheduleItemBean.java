/*
 * PractitionerScheduleItemBean.java
 *
 * Created on August 3, 2008, 3:07 PM
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
PractitionerScheduleItemBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    protected static HashMap<Integer,PractitionerScheduleItemBean> hash = new HashMap<Integer,PractitionerScheduleItemBean>(11);
	
	protected static SimpleDateFormat schedule_item_label_start = new SimpleDateFormat("EEEE h:mm a");
	protected static SimpleDateFormat schedule_item_label_end = new SimpleDateFormat("h:mm a");

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(PractitionerScheduleItemPeer.PRACTITIONER_SCHEDULE_ITEM_ID, _id);
		PractitionerScheduleItemPeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }

    public static PractitionerScheduleItemBean
    getPractitionerScheduleItem(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		PractitionerScheduleItemBean schedule_item = (PractitionerScheduleItemBean)hash.get(key);
		if (schedule_item == null)
		{
			Criteria crit = new Criteria();
			crit.add(PractitionerScheduleItemPeer.PRACTITIONER_SCHEDULE_ITEM_ID, _id);
			List objList = PractitionerScheduleItemPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate practitioner with id: " + _id);

			schedule_item = PractitionerScheduleItemBean.getPractitionerScheduleItem((PractitionerScheduleItem)objList.get(0));
		}

		return schedule_item;
    }

    private static PractitionerScheduleItemBean
    getPractitionerScheduleItem(PractitionerScheduleItem _schedule_item)
		throws TorqueException
    {
		Integer key = new Integer(_schedule_item.getPractitionerScheduleItemId());
		PractitionerScheduleItemBean schedule_item = (PractitionerScheduleItemBean)hash.get(key);
		if (schedule_item == null)
		{
			schedule_item = new PractitionerScheduleItemBean(_schedule_item);
			hash.put(key, schedule_item);
		}

		return schedule_item;
    }
    
    public static Vector
    getPractitionerScheduleItems(PractitionerScheduleBean _schedule)
		throws TorqueException, ObjectNotFoundException
    {
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(PractitionerScheduleItemPeer.PRACTITIONER_SCHEDULE_ID, _schedule.getId());
		crit.addAscendingOrderByColumn(PractitionerScheduleItemPeer.DAY_OF_WEEK);
		crit.addAscendingOrderByColumn(PractitionerScheduleItemPeer.START_HOUR_OF_DAY);
		Iterator obj_itr = PractitionerScheduleItemPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(PractitionerScheduleItemBean.getPractitionerScheduleItem((PractitionerScheduleItem)obj_itr.next()));
		
		return vec;
    }
    
	/*
    public static Vector
    getPractitionerScheduleTemplateItems(UKOnlinePersonBean _practitioner)
		throws TorqueException, ObjectNotFoundException
    {
		System.out.println("getPractitionerScheduleTemplateItems invoked >");
		
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		
		crit.addJoin(PractitionerSchedulePeer.PRACTITIONER_SCHEDULE_ID, PractitionerScheduleItemPeer.PRACTITIONER_SCHEDULE_ID);
		crit.add(PractitionerSchedulePeer.PRACTITIONER_ID, _practitioner.getId());
		
		crit.add(PractitionerScheduleItemPeer.TEMPLATED_APPOINTMENT_TYPE_ID, 0, Criteria.GREATER_THAN); // has template
		
		//crit.addAscendingOrderByColumn(PractitionerSchedulePeer.PRACTITIONER_ID); // not sure about this
		crit.addAscendingOrderByColumn(PractitionerScheduleItemPeer.DAY_OF_WEEK);
		crit.addAscendingOrderByColumn(PractitionerScheduleItemPeer.START_HOUR_OF_DAY);
		
		System.out.println("crit >" + crit.toString());
		
		Iterator obj_itr = PractitionerScheduleItemPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(PractitionerScheduleItemBean.getPractitionerScheduleItem((PractitionerScheduleItem)obj_itr.next()));
		
		return vec;
    }
	*/
    
    public static Vector
    getPractitionerScheduleTemplateItems(Vector _practitioners)
		throws TorqueException, ObjectNotFoundException
    {
		
		//System.out.println("getPractitionerScheduleTemplateItems invoked >");

		int[] arr = new int[_practitioners.size()];
		for (int i = 0; i < _practitioners.size(); i++) {
			UKOnlinePersonBean practitioner = (UKOnlinePersonBean)_practitioners.elementAt(i);
			arr[i] = practitioner.getId();
		}
		
		Criteria crit = new Criteria();
		
		crit.addJoin(PractitionerSchedulePeer.PRACTITIONER_SCHEDULE_ID, PractitionerScheduleItemPeer.PRACTITIONER_SCHEDULE_ID);
		crit.add(PractitionerSchedulePeer.PRACTITIONER_ID, (Object)arr, Criteria.IN);
		
		crit.add(PractitionerScheduleItemPeer.TEMPLATED_APPOINTMENT_TYPE_ID, 0, Criteria.GREATER_THAN); // has template
		
		//crit.addAscendingOrderByColumn(PractitionerSchedulePeer.PRACTITIONER_ID); // not sure about this
		crit.addAscendingOrderByColumn(PractitionerScheduleItemPeer.DAY_OF_WEEK);
		crit.addAscendingOrderByColumn(PractitionerScheduleItemPeer.START_HOUR_OF_DAY);
		crit.addAscendingOrderByColumn(PractitionerScheduleItemPeer.START_MINUTE);
		
		//System.out.println("crit >" + crit.toString());
		
		Vector vec = new Vector();
		Iterator obj_itr = PractitionerScheduleItemPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext()) {
			vec.addElement(PractitionerScheduleItemBean.getPractitionerScheduleItem((PractitionerScheduleItem)obj_itr.next()));
		}
		
		return vec;
    }

    
    // SQL
    
    /*
<table name="PRACTITIONER_SCHEDULE_ITEM" idMethod="native">
    <column name="PRACTITIONER_SCHEDULE_ITEM_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PRACTITIONER_SCHEDULE_ID" required="true" type="INTEGER"/>
	
    <column name="DAY_OF_WEEK" required="true" type="INTEGER" />
	<column name="START_HOUR_OF_DAY" required="true" type="INTEGER" />
	<column name="START_MINUTE" required="true" type="INTEGER" />
	<column name="END_HOUR_OF_DAY" required="true" type="INTEGER" />
	<column name="END_MINUTE" required="true" type="INTEGER" />
	
	<column name="TEMPLATED_APPOINTMENT_TYPE_ID" required="false" type="INTEGER"/>
    
    <foreign-key foreignTable="PRACTITIONER_SCHEDULE">
		<reference local="PRACTITIONER_SCHEDULE_ID" foreign="PRACTITIONER_SCHEDULE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="APPOINTMENT_TYPE">
		<reference local="TEMPLATED_APPOINTMENT_TYPE_ID" foreign="APPOINTMENT_TYPE_ID"/>
    </foreign-key>
</table>
     */
	
    // INSTANCE VARIABLES
	
    private PractitionerScheduleItem schedule_item;
    
    // CONSTRUCTORS
    
    public
    PractitionerScheduleItemBean()
    {
		schedule_item = new PractitionerScheduleItem();
		isNew = true;
    }
    
    public
    PractitionerScheduleItemBean(PractitionerScheduleItem _schedule_item)
    {
		schedule_item = _schedule_item;
		isNew = false;
    }
    
    // INSTANCE METHODS
	
	public int
	getDayOfWeek()
	{
		return schedule_item.getDayOfWeek();
	}
	
	public int
	getEndHourOfDay()
	{
		return schedule_item.getEndHourOfDay();
	}
	
	public int
	getEndMinute()
	{
		return schedule_item.getEndMinute();
	}
    
    public int
    getId()
    {
		return schedule_item.getPractitionerScheduleItemId();
    }
    
    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Calendar label_cal_start = Calendar.getInstance();
		label_cal_start.set(Calendar.DAY_OF_WEEK, this.getDayOfWeek());
		label_cal_start.set(Calendar.HOUR_OF_DAY, this.getStartHourOfDay());
		label_cal_start.set(Calendar.MINUTE, this.getStartMinute());
		
		Calendar label_cal_end = Calendar.getInstance();
		label_cal_end.set(Calendar.DAY_OF_WEEK, this.getDayOfWeek());
		label_cal_end.set(Calendar.HOUR_OF_DAY, this.getEndHourOfDay());
		label_cal_end.set(Calendar.MINUTE, this.getEndMinute());
		
		if (this.hasTemplate()) {
			return PractitionerScheduleItemBean.schedule_item_label_start.format(label_cal_start.getTime()) + " - " + PractitionerScheduleItemBean.schedule_item_label_end.format(label_cal_end.getTime()) + " [" + this.getAppointmentTypeTemplate().getLabel() + "]";
		} else {
			return PractitionerScheduleItemBean.schedule_item_label_start.format(label_cal_start.getTime()) + " - " + PractitionerScheduleItemBean.schedule_item_label_end.format(label_cal_end.getTime());
		}
    }
	
	public int
	getStartHourOfDay()
	{
		return schedule_item.getStartHourOfDay();
	}
	
	public int
	getStartMinute()
	{
		return schedule_item.getStartMinute();
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
		schedule_item.save();
    }
	
	public void
	setDayOfWeek(int _day_of_week)
	{
		schedule_item.setDayOfWeek(_day_of_week);
	}
	
	public void
	setEndHourOfDay(int _hour_of_day)
	{
		schedule_item.setEndHourOfDay(_hour_of_day);
	}
	
	public void
	setPractitionerSchedule(PractitionerScheduleBean _schedule)
		throws TorqueException
	{
		schedule_item.setPractitionerScheduleId(_schedule.getId());
	}
	
	public void
	setEndMinute(int _minute)
	{
		schedule_item.setEndMinute(_minute);
	}
    
    public void
    setSchedule(PractitionerScheduleBean _schedule)
		throws TorqueException
    {
		schedule_item.setPractitionerScheduleId(_schedule.getId());
    }
	
	public void
	setStartHourOfDay(int _hour_of_day)
	{
		schedule_item.setStartHourOfDay(_hour_of_day);
	}
	
	public void
	setStartMinute(int _minute)
	{
		schedule_item.setStartMinute(_minute);
	}
	
	public boolean
	hasTemplate() {
		return schedule_item.getTemplatedAppointmentTypeId() > 0;
	}
	
	public AppointmentTypeBean
	getAppointmentTypeTemplate() throws TorqueException, ObjectNotFoundException {
		return AppointmentTypeBean.getAppointmentType(schedule_item.getTemplatedAppointmentTypeId());
	}
	
	public void
	setAppointmentTypeTemplate(AppointmentTypeBean _type) throws TorqueException {
		schedule_item.setTemplatedAppointmentTypeId(_type.getId());
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		schedule_item.save();
    }
	
	public PractitionerScheduleBean
	getParent() throws TorqueException, ObjectNotFoundException {
		return PractitionerScheduleBean.getPractitionerSchedule(schedule_item.getPractitionerScheduleId());
	}
}