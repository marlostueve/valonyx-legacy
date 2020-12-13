/*
 * AppointmentBean.java
 *
 * Created on November 7, 2007, 7:26 PM
 *
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
//import com.badiyan.torque.Appointment;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.tasks.AppointmentReminderTask;
import com.badiyan.uk.online.util.EncryptionUtils;
import java.text.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.valonyx.events.AppointmentEventListener;
import com.valonyx.events.DeletedAppointmentEvent;
import com.valonyx.events.NewAppointmentEvent;
import com.valonyx.events.UpdatedAppointmentEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 * @author marlo
 */
public class
AppointmentBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    public static boolean allow_multiple_appointments_at_same_time = true;
    public static boolean allow_multiple_appointments_at_same_time_of_varying_types = false;

    public static final short DEFAULT_APPOINTMENT_STATUS = 1;
    public static final short RESCHEDULE_APPOINTMENT_STATUS = 2;
    public static final short LATE_APPOINTMENT_STATUS = 3;
    public static final short CHECKED_IN_APPOINTMENT_STATUS = 4;
    public static final short CHECKED_OUT_APPOINTMENT_STATUS = 5;
    public static final short CANCELLED_APPOINTMENT_STATUS = 6;
    public static final short NO_SHOW_APPOINTMENT_STATUS = 7;
    //public static final short DELETED_APPOINTMENT_STATUS = 8;
    public static final short PENDING_APPOINTMENT_STATUS = 9;
    
    protected static HashMap<Integer,AppointmentBean> hash = new HashMap<Integer,AppointmentBean>(11);
	public static HashMap<String,Vector> appointment_hash = new HashMap<String,Vector>(11);
	public static HashMap<String,Date> appointment_update_hash = new HashMap<String,Date>(11);
    
    public static SimpleDateFormat date_time_format = new SimpleDateFormat("EEEE, MMMM d - h:mm a");
    public static SimpleDateFormat date_format = new SimpleDateFormat("h:mm a");
	public static SimpleDateFormat long_date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
	
	private static HashMap<String,Boolean> new_appts_hash = new HashMap<String,Boolean>(11);
	private static HashMap<String,Boolean> is_new_for_session_hash = new HashMap<String,Boolean>(11);
	
	private static Vector eventListeners;

	// CLASS METHODS

	public static void
	addAppointmentEventListener(AppointmentEventListener _listener) {
	    if (eventListeners == null) {
			eventListeners = new Vector();
		}
	    if (!eventListeners.contains(_listener)) {
			System.out.println("adding listener >" + _listener);
			eventListeners.addElement(_listener);
		}
    }

	public static void
	removeAppointmentEventListener(AppointmentEventListener _listener) {
        eventListeners.removeElement(_listener);
    }
	
	public static boolean isNewlyCreated(AppointmentBean _appointment, String _sessionId) {
		
		if (_sessionId == null) {
			return false;
		}
		
		String appt_key = _appointment.getValue();
		if (new_appts_hash.containsKey(appt_key)) {
		
			String key = _appointment.getId() + "-" + _sessionId;
			if (is_new_for_session_hash.containsKey(key)) {
				return false;
			} else {
				is_new_for_session_hash.put(key, Boolean.TRUE);
				return true;
			}
		} else {
			return false;
		}
	}

	public static void
	invalidateHash() {
		appointment_hash.clear();
		appointment_update_hash.clear();
	}
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		System.out.println("deleting appointment >" + _id);
		
		
		String client_name_str = "[NOT FOUND]";
		boolean is_client_appt = false;
		AppointmentBean delete_appointment = null;
		try {
			delete_appointment = AppointmentBean.getAppointment(_id);
			is_client_appt = delete_appointment.isClientAppointment();
			client_name_str = delete_appointment.getClient().getLabel();
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		
		try {
			throw new IllegalValueException("delete trace >");
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.APPOINTMENT_ID, _id);
		AppointmentPeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
				
				
		if (is_client_appt) {
			try {

				System.out.println("deleting appt");
				System.out.println("appt_id_str >" + _id);
				System.out.println("client_name_str >" + client_name_str);

				if (!client_name_str.equals("Christine Stueve")) {
					CUBean.sendEmail("marlo@badiyan.com", "marlo@valonyx.com", "An Appointment had been Deleted - trace", "appt_id_str >" + _id + ",    " + "client_name_str >" + client_name_str);
				}
			} catch (Exception x) {
				//x.printStackTrace();
			}
		}
		
		if (delete_appointment != null) {
			delete_appointment.notifyDeletedAppointmentEvent();
		}
    }
	
	public static boolean
	hasPendingAppointmentRequest(UKOnlinePersonBean _client) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.PENDING_APPOINTMENT_STATUS);
		List objList = AppointmentPeer.doSelect(crit);
		return (objList.size() > 0);
	}

    public static AppointmentBean
    getAppointment(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		if (_id == 0)
			throw new ObjectNotFoundException("Could not locate appointment with id: " + _id);

		Integer key = new Integer(_id);
		AppointmentBean appointment = (AppointmentBean)hash.get(key);
		if (appointment == null)
		{
			Criteria crit = new Criteria();
			crit.add(AppointmentPeer.APPOINTMENT_ID, _id);
			List objList = AppointmentPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate appointment with id: " + _id);

			appointment = AppointmentBean.getAppointment((Appointment)objList.get(0));
		}

		return appointment;
    }
    
    public static Vector
    getAppointments(CompanyBean _company)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }
    
    public static Vector
    getAppointments(CompanyBean _company, short _state)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		if (_state == AppointmentBean.CANCELLED_APPOINTMENT_STATUS)
			crit.addAscendingOrderByColumn(AppointmentPeer.CANCELLED_DATE);
		else if (_state == AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS)
			crit.addAscendingOrderByColumn(AppointmentPeer.CHECKED_IN_DATE);
		else if (_state == AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS)
			crit.addAscendingOrderByColumn(AppointmentPeer.CHECKED_OUT_DATE);
		else if (_state == AppointmentBean.DEFAULT_APPOINTMENT_STATUS)
			crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		else if (_state == AppointmentBean.LATE_APPOINTMENT_STATUS)
			crit.addAscendingOrderByColumn(AppointmentPeer.LATE_DATE);
		else if (_state == AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS)
			crit.addAscendingOrderByColumn(AppointmentPeer.RESCHEDULE_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }
    
    public static HashMap
    getAppointments(CompanyBean _company, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
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

		HashMap appts_hash = new HashMap();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
			/*
			String time_str = date_format.format(appointment.getAppointmentDate());
			String key = time_str + "|" + appointment.getPractitioner().getId();
			 */
			String key = appointment.getKey();
			Vector hash_vec = (Vector)appts_hash.get(key);
			if (hash_vec == null)
			{
				hash_vec = new Vector();
				appts_hash.put(key, hash_vec);
			}
			hash_vec.addElement(appointment);
		}

		return appts_hash;
    }
    
    public static HashMap
    getAppointments(CompanyBean _company, Date _date, Date _last_update_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
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

		HashMap appts_hash = new HashMap();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.MODIFICATION_DATE, _last_update_date, Criteria.GREATER_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		
		//System.out.println("getAppointments() crit >" + crit.toString());
		
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
			String key = appointment.getKey();
			Vector hash_vec = (Vector)appts_hash.get(key);
			if (hash_vec == null)
			{
				hash_vec = new Vector();
				appts_hash.put(key, hash_vec);
			}
			hash_vec.addElement(appointment);
		}

		return appts_hash;
    }
	
    public static HashMap
    getAppointments(CompanyBean _company, PracticeAreaBean _practice_area, Date _date, Date _last_update_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		HashMap appts_hash = new HashMap();

		// see if there's stuff in the cache

		String key;
		if ((_practice_area != null) && (!_practice_area.isNew()))
			key = _company.getId() + "|" + _practice_area.getId() + "|" + CUBean.getUserDateString(_date);
		else
			key = _company.getId() + "|" + CUBean.getUserDateString(_date);

		//System.out.println("getAppointments() key >" + key);

		Date last_updated = AppointmentBean.appointment_update_hash.get(key);
		if ((last_updated != null) && last_updated.after(_last_update_date))
		{
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
			crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
			//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
			if ((_practice_area != null) && (!_practice_area.isNew()))
			{
				Vector practitioners = _practice_area.getPractitioners();

				int[] arr = new int[practitioners.size()];
				for (int i = 0; i < practitioners.size(); i++)
				{
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners.elementAt(i);
					arr[i] = practitioner.getId();
				}

				crit.add(AppointmentPeer.PRACTITIONER_ID, (Object)arr, Criteria.IN);
			}
			crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
			crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
			crit.add(AppointmentPeer.MODIFICATION_DATE, _last_update_date, Criteria.GREATER_EQUAL);
			crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);

			Iterator itr = AppointmentPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
				String appt_key = appointment.getKey();
				Vector hash_vec = (Vector)appts_hash.get(appt_key);
				if (hash_vec == null)
				{
					hash_vec = new Vector();
					appts_hash.put(appt_key, hash_vec);
				}
				hash_vec.addElement(appointment);
			}
		}

		return appts_hash;
    }
	
    public static HashMap
    getAppointmentsFromStartDateToEndDate(CompanyBean _company, PracticeAreaBean _practice_area, Date _start_date, Date _end_date, Date _last_update_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		HashMap appts_hash = new HashMap();

		// see if there's stuff in the cache

		String key;
		if ((_practice_area != null) && (!_practice_area.isNew()))
			key = _company.getId() + "|" + _practice_area.getId() + "|" + CUBean.getUserDateString(_start_date) + "|" + CUBean.getUserDateString(_end_date);
		else
			key = _company.getId() + "|" + CUBean.getUserDateString(_start_date) + "|" + CUBean.getUserDateString(_end_date);

		System.out.println("getAppointments() key >" + key);

		Date last_updated = AppointmentBean.appointment_update_hash.get(key);
		System.out.println("last_updated >" + last_updated);
		if ((last_updated != null) && last_updated.after(_last_update_date))
		{
			Calendar start_of_day = Calendar.getInstance();
			start_of_day.setTime(_start_date);
			start_of_day.set(Calendar.HOUR_OF_DAY, 0);
			start_of_day.set(Calendar.MINUTE, 0);
			start_of_day.set(Calendar.SECOND, 0);

			Calendar end_of_day = Calendar.getInstance();
			end_of_day.setTime(_end_date);
			end_of_day.set(Calendar.HOUR_OF_DAY, 23);
			end_of_day.set(Calendar.MINUTE, 59);
			end_of_day.set(Calendar.SECOND, 59);

			Criteria crit = new Criteria();
			crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
			//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
			if ((_practice_area != null) && (!_practice_area.isNew()))
			{
				Vector practitioners = _practice_area.getPractitioners();

				int[] arr = new int[practitioners.size()];
				for (int i = 0; i < practitioners.size(); i++)
				{
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners.elementAt(i);
					arr[i] = practitioner.getId();
				}

				crit.add(AppointmentPeer.PRACTITIONER_ID, (Object)arr, Criteria.IN);
			}
			crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
			crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
			crit.add(AppointmentPeer.MODIFICATION_DATE, _last_update_date, Criteria.GREATER_EQUAL);
			crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
			System.out.println("getAppointmentsFromStartDateToEndDate crit >" + crit.toString());
			Iterator itr = AppointmentPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
				String appt_key = appointment.getKey();
				Vector hash_vec = (Vector)appts_hash.get(appt_key);
				if (hash_vec == null)
				{
					hash_vec = new Vector();
					appts_hash.put(appt_key, hash_vec);
				}
				hash_vec.addElement(appointment);
			}
		}

		return appts_hash;
    }

	/*
    public static HashMap
    getAppointments(CompanyBean _company, PracticeAreaBean _practice_area, Date _date, Date _last_update_date, boolean _include_deleted)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
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

		HashMap appts_hash = new HashMap();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		if (!_include_deleted)
			crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		if ((_practice_area != null) && (!_practice_area.isNew()))
		{
			Vector practitioners = _practice_area.getPractitioners();

			int[] arr = new int[practitioners.size()];
			for (int i = 0; i < practitioners.size(); i++)
			{
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners.elementAt(i);
				arr[i] = practitioner.getId();
			}

			crit.add(AppointmentPeer.PRACTITIONER_ID, (Object)arr, Criteria.IN);
		}
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.MODIFICATION_DATE, _last_update_date, Criteria.GREATER_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);

		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
			String key = appointment.getKey();
			Vector hash_vec = (Vector)appts_hash.get(key);
			if (hash_vec == null)
			{
				hash_vec = new Vector();
				appts_hash.put(key, hash_vec);
			}
			hash_vec.addElement(appointment);
		}

		return appts_hash;
    }
	 */
	
    public static HashMap
    getAppointments(CompanyBean _company, UKOnlinePersonBean _practitioner, Date _start_date, Date _end_date, Date _last_update_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Calendar start_of_week = Calendar.getInstance();
		start_of_week.setTime(_start_date);
		start_of_week.set(Calendar.HOUR_OF_DAY, 0);
		start_of_week.set(Calendar.MINUTE, 0);
		start_of_week.set(Calendar.SECOND, 0);

		Calendar end_of_week = Calendar.getInstance();
		end_of_week.setTime(_end_date);
		end_of_week.set(Calendar.HOUR_OF_DAY, 23);
		end_of_week.set(Calendar.MINUTE, 59);
		end_of_week.set(Calendar.SECOND, 59);

		HashMap appts_hash = new HashMap();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _practitioner.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_week.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_week.getTime(), Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.MODIFICATION_DATE, _last_update_date, Criteria.GREATER_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
			String key = appointment.getKey();
			Vector hash_vec = (Vector)appts_hash.get(key);
			if (hash_vec == null)
			{
				hash_vec = new Vector();
				appts_hash.put(key, hash_vec);
			}
			hash_vec.addElement(appointment);
		}

		return appts_hash;
    }

	/*
    public static HashMap
    getAppointments(CompanyBean _company, UKOnlinePersonBean _practitioner, Date _start_date, Date _end_date, Date _last_update_date, boolean _include_deleted)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Calendar start_of_week = Calendar.getInstance();
		start_of_week.setTime(_start_date);
		start_of_week.set(Calendar.HOUR_OF_DAY, 0);
		start_of_week.set(Calendar.MINUTE, 0);
		start_of_week.set(Calendar.SECOND, 0);

		Calendar end_of_week = Calendar.getInstance();
		end_of_week.setTime(_end_date);
		end_of_week.set(Calendar.HOUR_OF_DAY, 23);
		end_of_week.set(Calendar.MINUTE, 59);
		end_of_week.set(Calendar.SECOND, 59);

		HashMap appts_hash = new HashMap();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		if (!_include_deleted)
			crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _practitioner.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_week.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_week.getTime(), Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.MODIFICATION_DATE, _last_update_date, Criteria.GREATER_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);

		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
			String key = appointment.getKey();
			Vector hash_vec = (Vector)appts_hash.get(key);
			if (hash_vec == null)
			{
				hash_vec = new Vector();
				appts_hash.put(key, hash_vec);
			}
			hash_vec.addElement(appointment);
		}

		return appts_hash;
    }
	 */
    
    public static Vector
    getAppointments(CompanyBean _company, Date _date, short _state)
		throws TorqueException
    {
		Vector vec = new Vector();
		
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
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		if (_state > 0)
		{
			crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
			if (_state == AppointmentBean.CANCELLED_APPOINTMENT_STATUS)
				crit.addAscendingOrderByColumn(AppointmentPeer.CANCELLED_DATE);
			else if (_state == AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS)
				crit.addAscendingOrderByColumn(AppointmentPeer.CHECKED_IN_DATE);
			else if (_state == AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS)
				crit.addAscendingOrderByColumn(AppointmentPeer.CHECKED_OUT_DATE);
			else if (_state == AppointmentBean.DEFAULT_APPOINTMENT_STATUS)
				crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
			else if (_state == AppointmentBean.LATE_APPOINTMENT_STATUS)
				crit.addAscendingOrderByColumn(AppointmentPeer.LATE_DATE);
			else if (_state == AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS)
				crit.addAscendingOrderByColumn(AppointmentPeer.RESCHEDULE_DATE);
		}
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }
    
    public static Vector
    getAppointments(UKOnlineCompanyBean _company, PracticeAreaBean _practice_area, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		// see if there's stuff in the cache

		String key;
		if ((_practice_area != null) && (!_practice_area.isNew()))
			key = _company.getId() + "|" + _practice_area.getId() + "|" + CUBean.getUserDateString(_date);
		else
			key = _company.getId() + "|" + CUBean.getUserDateString(_date);

		//System.out.println("getAppointments() key >" + key);

		Vector vec = AppointmentBean.appointment_hash.get(key);
		if (vec == null) {
			
			vec = new Vector();

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

			Vector practitioners = null;

			Criteria crit = new Criteria();
			crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
			//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
			if ((_practice_area != null) && (!_practice_area.isNew()))
			{
				practitioners = _practice_area.getPractitioners();

				int[] arr = new int[practitioners.size()];
				for (int i = 0; i < practitioners.size(); i++)
				{
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners.elementAt(i);
					arr[i] = practitioner.getId();
				}

				crit.add(AppointmentPeer.PRACTITIONER_ID, (Object)arr, Criteria.IN);
			}
			crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
			crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
			//System.out.println("getAppointments crit >" + crit.toString());
			Iterator itr = AppointmentPeer.doSelect(crit).iterator();
			while (itr.hasNext())
				vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

			if (practitioners == null)
				practitioners = UKOnlinePersonBean.getPractitioners(_company);

			Iterator practitioner_itr = practitioners.iterator();
			while (practitioner_itr.hasNext())
			{
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
				try {
					PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(practitioner, _date);
					vec.addAll(practitioner_schedule.toOfftimeAppointments(_date));
				}
				catch (ObjectNotFoundException x)
				{
					x.printStackTrace();
				}
				catch (UniqueObjectNotFoundException x)
				{
					x.printStackTrace();
				}
			}
			
			appointment_hash.put(key, vec);
		}
		//else
		//	System.out.println("cached");

		return vec;
    }
    
    public static Vector
    getAppointmentsNoCache(UKOnlineCompanyBean _company, PracticeAreaBean _practice_area, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		Vector vec = new Vector();
		
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

		Vector practitioners = null;

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		if ((_practice_area != null) && (!_practice_area.isNew()))
		{
			practitioners = _practice_area.getPractitioners();

			int[] arr = new int[practitioners.size()];
			for (int i = 0; i < practitioners.size(); i++)
			{
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners.elementAt(i);
				arr[i] = practitioner.getId();
			}

			crit.add(AppointmentPeer.PRACTITIONER_ID, (Object)arr, Criteria.IN);
		}
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		//System.out.println("getAppointments crit >" + crit.toString());
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		if (practitioners == null)
			practitioners = UKOnlinePersonBean.getPractitioners(_company);

		Iterator practitioner_itr = practitioners.iterator();
		while (practitioner_itr.hasNext())
		{
			UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
			try {
				PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(practitioner, _date);
				vec.addAll(practitioner_schedule.toOfftimeAppointments(_date));
			}
			catch (ObjectNotFoundException x)
			{
				x.printStackTrace();
			}
			catch (UniqueObjectNotFoundException x)
			{
				x.printStackTrace();
			}
		}
			
			

		return vec;
    }
    
    public static Vector
    getAppointments(UKOnlineCompanyBean _company, PracticeAreaBean _practice_area, Date _start_date, Date _end_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {

		String key;
		if ((_practice_area != null) && (!_practice_area.isNew()))
			key = _company.getId() + "|" + _practice_area.getId() + "|" + CUBean.getUserDateString(_start_date) + "-" + CUBean.getUserDateString(_end_date);
		else
			key = _company.getId() + "|" + CUBean.getUserDateString(_start_date) + "-" + CUBean.getUserDateString(_end_date);

		//System.out.println("getAppointments() key >" + key);

		Vector vec = AppointmentBean.appointment_hash.get(key);
		if (vec == null)
		{
			//System.out.println("un-cached");

			vec = new Vector();

			Calendar start_of_day = Calendar.getInstance();
			start_of_day.setTime(_start_date);
			start_of_day.set(Calendar.HOUR_OF_DAY, 0);
			start_of_day.set(Calendar.MINUTE, 0);
			start_of_day.set(Calendar.SECOND, 0);

			Calendar end_of_day = Calendar.getInstance();
			end_of_day.setTime(_end_date);
			end_of_day.set(Calendar.HOUR_OF_DAY, 23);
			end_of_day.set(Calendar.MINUTE, 59);
			end_of_day.set(Calendar.SECOND, 59);

			Vector practitioners = null;

			Criteria crit = new Criteria();
			crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
			//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
			if ((_practice_area != null) && (!_practice_area.isNew()))
			{
				practitioners = _practice_area.getPractitioners();

				int[] arr = new int[practitioners.size()];
				for (int i = 0; i < practitioners.size(); i++)
				{
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners.elementAt(i);
					arr[i] = practitioner.getId();
				}

				crit.add(AppointmentPeer.PRACTITIONER_ID, (Object)arr, Criteria.IN);
			}
			crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
			crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
			//System.out.println("getAppointments crit >" + crit.toString());
			Iterator itr = AppointmentPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));
			}

			if (practitioners == null) {
				practitioners = UKOnlinePersonBean.getPractitioners(_company);
			}

			Iterator practitioner_itr = practitioners.iterator();
			while (practitioner_itr.hasNext())
			{
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
				try {
					Calendar cal_itr = Calendar.getInstance();
					cal_itr.setTime(_start_date);
					String start_str = null;
					String end_str = CUBean.getUserDateString(_end_date);
					do {
						PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(practitioner, cal_itr.getTime());
						vec.addAll(practitioner_schedule.toOfftimeAppointments(cal_itr.getTime()));
						start_str = CUBean.getUserDateString(cal_itr.getTime());
						cal_itr.add(Calendar.DATE, 1);
					} while (!start_str.equals(end_str));
				} catch (ObjectNotFoundException x) {
					x.printStackTrace();
				}
				catch (UniqueObjectNotFoundException x)
				{
					x.printStackTrace();
				}
			}

			appointment_hash.put(key, vec);
		}
		

		return vec;
    }
    
    public static Vector
    getAppointmentsNoCache(UKOnlineCompanyBean _company, PracticeAreaBean _practice_area, Date _start_date, Date _end_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {


		//System.out.println("getAppointments() key >" + key);

		Vector vec = new Vector();

		Calendar start_of_day = Calendar.getInstance();
		start_of_day.setTime(_start_date);
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);

		Calendar end_of_day = Calendar.getInstance();
		end_of_day.setTime(_end_date);
		end_of_day.set(Calendar.HOUR_OF_DAY, 23);
		end_of_day.set(Calendar.MINUTE, 59);
		end_of_day.set(Calendar.SECOND, 59);

		Vector practitioners = null;

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		if ((_practice_area != null) && (!_practice_area.isNew()))
		{
			practitioners = _practice_area.getPractitioners();

			int[] arr = new int[practitioners.size()];
			for (int i = 0; i < practitioners.size(); i++)
			{
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners.elementAt(i);
				arr[i] = practitioner.getId();
			}

			crit.add(AppointmentPeer.PRACTITIONER_ID, (Object)arr, Criteria.IN);
		}
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		//System.out.println("getAppointments crit >" + crit.toString());
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));
		}

		if (practitioners == null) {
			practitioners = UKOnlinePersonBean.getPractitioners(_company);
		}

		Iterator practitioner_itr = practitioners.iterator();
		while (practitioner_itr.hasNext())
		{
			UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
			try {
				Calendar cal_itr = Calendar.getInstance();
				cal_itr.setTime(_start_date);
				String start_str = null;
				String end_str = CUBean.getUserDateString(_end_date);
				do {
					PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(practitioner, cal_itr.getTime());
					vec.addAll(practitioner_schedule.toOfftimeAppointments(cal_itr.getTime()));
					start_str = CUBean.getUserDateString(cal_itr.getTime());
					cal_itr.add(Calendar.DATE, 1);
				} while (!start_str.equals(end_str));
			} catch (ObjectNotFoundException x) {
				x.printStackTrace();
			}
			catch (UniqueObjectNotFoundException x)
			{
				x.printStackTrace();
			}
		}

		return vec;
    }
    
    public static Vector
    getAppointmentsFromStartDateToEndDate(UKOnlineCompanyBean _company, PracticeAreaBean _practice_area, Date _start_date, Date _end_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		// see if there's stuff in the cache

		String key;
		if ((_practice_area != null) && (!_practice_area.isNew()))
			key = _company.getId() + "|" + _practice_area.getId() + "|" + CUBean.getUserDateString(_start_date) + "|" + CUBean.getUserDateString(_end_date);
		else
			key = _company.getId() + "|" + CUBean.getUserDateString(_start_date) + "|" + CUBean.getUserDateString(_end_date);

		//System.out.println("getAppointments() key >" + key);

		Vector vec = AppointmentBean.appointment_hash.get(key);
		if (vec == null)
		{
			//System.out.println("un-cached");

			vec = new Vector();

			Calendar start_of_day = Calendar.getInstance();
			start_of_day.setTime(_start_date);
			start_of_day.set(Calendar.HOUR_OF_DAY, 0);
			start_of_day.set(Calendar.MINUTE, 0);
			start_of_day.set(Calendar.SECOND, 0);

			Calendar end_of_day = Calendar.getInstance();
			end_of_day.setTime(_end_date);
			end_of_day.set(Calendar.HOUR_OF_DAY, 23);
			end_of_day.set(Calendar.MINUTE, 59);
			end_of_day.set(Calendar.SECOND, 59);

			Vector practitioners = null;

			Criteria crit = new Criteria();
			crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
			//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
			if ((_practice_area != null) && (!_practice_area.isNew()))
			{
				practitioners = _practice_area.getPractitioners();

				int[] arr = new int[practitioners.size()];
				for (int i = 0; i < practitioners.size(); i++)
				{
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners.elementAt(i);
					arr[i] = practitioner.getId();
				}

				crit.add(AppointmentPeer.PRACTITIONER_ID, (Object)arr, Criteria.IN);
			}
			crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
			crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
			System.out.println("  tty getAppointments crit >" + crit.toString());
			Iterator itr = AppointmentPeer.doSelect(crit).iterator();
			while (itr.hasNext())
				vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

			if (practitioners == null)
				practitioners = UKOnlinePersonBean.getPractitioners(_company);

			Iterator practitioner_itr = practitioners.iterator();
			while (practitioner_itr.hasNext())
			{
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
				try
				{
					PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(practitioner, _start_date);
					vec.addAll(practitioner_schedule.toOfftimeAppointments(_start_date));
				}
				catch (ObjectNotFoundException x)
				{
					x.printStackTrace();
				}
				catch (UniqueObjectNotFoundException x)
				{
					x.printStackTrace();
				}
			}
			
			appointment_hash.put(key, vec);
		}

		return vec;
    }
    
    public static Vector
    getAppointments(UKOnlineCompanyBean _company, UKOnlinePersonBean _practitioner, Date _start_date, Date _end_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		if (!_start_date.before(_end_date))
			throw new IllegalValueException("Start date must be before end date.");

		String key = _company.getId() + "|" + _practitioner.getId() + "|" + CUBean.getUserDateString(_start_date) + "|" + CUBean.getUserDateString(_end_date);
		//System.out.println("getAppointments key >" + key);
		Vector vec = AppointmentBean.appointment_hash.get(key);
		if (vec == null)
		{
			vec = new Vector();

			Calendar start_of_week = Calendar.getInstance();
			start_of_week.setTime(_start_date);
			start_of_week.set(Calendar.HOUR_OF_DAY, 0);
			start_of_week.set(Calendar.MINUTE, 0);
			start_of_week.set(Calendar.SECOND, 0);

			Calendar end_of_week = Calendar.getInstance();
			end_of_week.setTime(_end_date);
			end_of_week.set(Calendar.HOUR_OF_DAY, 23);
			end_of_week.set(Calendar.MINUTE, 59);
			end_of_week.set(Calendar.SECOND, 59);

			Criteria crit = new Criteria();
			crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
			//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
			crit.add(AppointmentPeer.PRACTITIONER_ID, _practitioner.getId());
			crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_week.getTime(), Criteria.GREATER_EQUAL);
			crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_week.getTime(), Criteria.LESS_EQUAL);

			Iterator itr = AppointmentPeer.doSelect(crit).iterator();
			while (itr.hasNext())
				vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

			try
			{
				//end_of_week.add(Calendar.DATE, 1);
				//System.out.println("start_of_week.get(Calendar.DATE) >" + start_of_week.get(Calendar.DATE));
				//System.out.println("end_of_week.get(Calendar.DATE) >" + end_of_week.get(Calendar.DATE));
				//while (start_of_week.get(Calendar.DATE) < end_of_week.get(Calendar.DATE))
				while (start_of_week.before(end_of_week))
				{
					PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(_practitioner, start_of_week.getTime());
					vec.addAll(practitioner_schedule.toOfftimeAppointments(start_of_week.getTime()));

					start_of_week.add(Calendar.DATE, 1);

					//System.out.println("start_of_week.get(Calendar.DATE) >" + start_of_week.get(Calendar.DATE));
					//System.out.println("end_of_week.get(Calendar.DATE) >" + end_of_week.get(Calendar.DATE));
				}


			}
			catch (ObjectNotFoundException x)
			{
				x.printStackTrace();
			}

			appointment_hash.put(key, vec);
		}

		return vec;
    }

	/*
    public static Vector
    getAppointments(UKOnlineCompanyBean _company, UKOnlinePersonBean _practitioner, Date _start_date, Date _end_date, boolean _show_deleted)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		if (!_start_date.before(_end_date))
			throw new IllegalValueException("Start date must be before end date.");

		Vector vec = new Vector();
		
		Calendar start_of_week = Calendar.getInstance();
		start_of_week.setTime(_start_date);
		start_of_week.set(Calendar.HOUR_OF_DAY, 0);
		start_of_week.set(Calendar.MINUTE, 0);
		start_of_week.set(Calendar.SECOND, 0);

		Calendar end_of_week = Calendar.getInstance();
		end_of_week.setTime(_end_date);
		end_of_week.set(Calendar.HOUR_OF_DAY, 23);
		end_of_week.set(Calendar.MINUTE, 59);
		end_of_week.set(Calendar.SECOND, 59);
		
		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		if (!_show_deleted)
			crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _practitioner.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_week.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_week.getTime(), Criteria.LESS_EQUAL);
		
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));
		
		try
		{
			//end_of_week.add(Calendar.DATE, 1);
			//System.out.println("start_of_week.get(Calendar.DATE) >" + start_of_week.get(Calendar.DATE));
			//System.out.println("end_of_week.get(Calendar.DATE) >" + end_of_week.get(Calendar.DATE));
			//while (start_of_week.get(Calendar.DATE) < end_of_week.get(Calendar.DATE))
			while (start_of_week.before(end_of_week))
			{
				PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(_practitioner, start_of_week.getTime());
				vec.addAll(practitioner_schedule.toOfftimeAppointments(start_of_week.getTime()));
				
				start_of_week.add(Calendar.DATE, 1);

				//System.out.println("start_of_week.get(Calendar.DATE) >" + start_of_week.get(Calendar.DATE));
				//System.out.println("end_of_week.get(Calendar.DATE) >" + end_of_week.get(Calendar.DATE));
			}
			 

		}
		catch (ObjectNotFoundException x)
		{
			x.printStackTrace();
		}

		return vec;
    }
	 */

    public static Vector
    getAppointments(UKOnlinePersonBean _practitioner, Date _start_date, Date _end_date, short _state)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		if (!_start_date.before(_end_date))
			throw new IllegalValueException("Start date must be before end date.");

		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.PRACTITIONER_ID, _practitioner.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, _start_date, Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, _end_date, Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }

    public static Vector
    getAppointments(UKOnlineCompanyBean _company, Date _start_date, Date _end_date, short _state)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		if (!_start_date.before(_end_date))
			throw new IllegalValueException("Start date must be before end date.");

		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, _start_date, Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, _end_date, Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		System.out.println("getAppointments() crit >" + crit.toString());
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }

    public static Vector
    getAppointments(AppointmentTypeBean _type)
		throws TorqueException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.APPOINTMENT_TYPE_ID, _type.getId());
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));
		return vec;
    }
    
    public static Vector
    getAppointmentsForClient(UKOnlinePersonBean _client)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }
    
    public static Vector
    getAppointmentsForClient(UKOnlinePersonBean _client, Date _after_date)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_DATE, _after_date, Criteria.GREATER_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }

    public static Vector
    getAppointmentsForClient(UKOnlinePersonBean _client, Date _start_date, Date _end_date)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		if (_start_date != null)
			crit.add(AppointmentPeer.APPOINTMENT_DATE, _start_date, Criteria.GREATER_EQUAL);
		if (_end_date != null)
			crit.add(AppointmentPeer.APPOINTMENT_DATE, _end_date, Criteria.LESS_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }

    public static Vector
    getAppointmentsForClientOnDate(UKOnlinePersonBean _client, Date _date, short _state)
		throws TorqueException
    {
		Vector vec = new Vector();

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
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }

    public static Vector
    getAppointmentsForClientOnDate(UKOnlinePersonBean _client, PracticeAreaBean _practice_area, Date _date, short _state)
		throws TorqueException
    {
		Vector vec = new Vector();

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
		crit.addJoin(AppointmentTypePeer.APPOINTMENT_TYPE_ID, AppointmentPeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentTypePeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }
    
    public static Vector
    getAppointmentsForPractitioner(UKOnlinePersonBean _practitioner)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.PRACTITIONER_ID, _practitioner.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }
    
    public static Vector
    getAppointmentsForPractitioner(UKOnlinePersonBean _practitioner, Date _date)
		throws TorqueException
    {
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
		
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.PRACTITIONER_ID, _practitioner.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));

		return vec;
    }

    public static Vector
    getAppointmentsForDate(CompanyBean _company, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
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

		Vector appts = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
			appts.addElement(appointment);
		}

		return appts;
    }

    public static Vector
    getAppointmentsForDate(CompanyBean _company, Date _start_date, Date _end_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.setTime(_start_date);
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);

		Calendar end_of_day = Calendar.getInstance();
		end_of_day.setTime(_end_date);
		end_of_day.set(Calendar.HOUR_OF_DAY, 23);
		end_of_day.set(Calendar.MINUTE, 59);
		end_of_day.set(Calendar.SECOND, 59);

		Vector appts = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
			appts.addElement(appointment);
		}

		return appts;
    }

    private static AppointmentBean
    getAppointment(Appointment _appointment)
		throws TorqueException
    {
		Integer key = new Integer(_appointment.getAppointmentId());
		AppointmentBean appointment = (AppointmentBean)hash.get(key);
		if (appointment == null)
		{
			appointment = new AppointmentBean(_appointment);
			hash.put(key, appointment);
		}

		return appointment;
    }

    public static Vector
    getConflicts(AppointmentBean _appointment)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector conflicts = new Vector();

		Calendar appointment_start = Calendar.getInstance();
		Calendar appointment_end = Calendar.getInstance();
		appointment_start.setTime(_appointment.getAppointmentDate());
		appointment_end.setTime(_appointment.getAppointmentDate());
		appointment_end.add(Calendar.MINUTE, _appointment.getDuration());

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.GREATER_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		System.out.println("getConflicts crit1 >" + crit.toString());
		Iterator obj_itr = AppointmentPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			conflicts.addElement(AppointmentBean.getAppointment((Appointment)obj_itr.next()));


		crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.LESS_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.LESS_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		System.out.println("getConflicts crit2 >" + crit.toString());
		obj_itr = AppointmentPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			conflicts.addElement(AppointmentBean.getAppointment((Appointment)obj_itr.next()));

		crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.LESS_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		System.out.println("getConflicts crit3 >" + crit.toString());
		obj_itr = AppointmentPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			conflicts.addElement(AppointmentBean.getAppointment((Appointment)obj_itr.next()));

		crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.LESS_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.GREATER_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		System.out.println("getConflicts crit4 >" + crit.toString());
		obj_itr = AppointmentPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			conflicts.addElement(AppointmentBean.getAppointment((Appointment)obj_itr.next()));

		return conflicts;
    }
    
    public static AppointmentBean
    getNextAppointmentForClient(UKOnlinePersonBean _client)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_DATE, new Date(), Criteria.GREATER_THAN);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		List obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() == 0)
			return null;
		else
			return AppointmentBean.getAppointment((Appointment)obj_list.get(0));
    }

    public static AppointmentBean
    getNextAppointmentForClient(UKOnlinePersonBean _client, short _state)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, new Date(), Criteria.GREATER_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		List obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() == 0)
			return null;
		else
			return AppointmentBean.getAppointment((Appointment)obj_list.get(0));
    }
    
    public static AppointmentBean
    getNextAppointmentForClient(UKOnlinePersonBean _client, PracticeAreaBean _practice_area)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.addJoin(AppointmentPeer.APPOINTMENT_TYPE_ID, AppointmentTypePeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_DATE, new Date(), Criteria.GREATER_THAN);
		crit.add(AppointmentTypePeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		List obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() == 0)
			return null;
		else
			return AppointmentBean.getAppointment((Appointment)obj_list.get(0));
    }

    public static AppointmentBean
    getNextAppointmentForClient(UKOnlinePersonBean _client, PracticeAreaBean _practice_area, short _state)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.addJoin(AppointmentPeer.APPOINTMENT_TYPE_ID, AppointmentTypePeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, new Date(), Criteria.GREATER_THAN);
		crit.add(AppointmentTypePeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		List obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() == 0)
			return null;
		else
			return AppointmentBean.getAppointment((Appointment)obj_list.get(0));
    }

    public static Vector
    getNextAppointmentsForClient(UKOnlinePersonBean _client, short _state)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, new Date(), Criteria.GREATER_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		Iterator obj_itr = AppointmentPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(AppointmentBean.getAppointment((Appointment)obj_itr.next()));

		return vec;
    }
	
	public static AppointmentBean
	getNextAvailableAppointmentTimeForPractitioner(UKOnlineCompanyBean _company, UKOnlinePersonBean _practitioner, AppointmentTypeBean _type, Calendar _date, short _duration)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		System.out.println("getNextAvailableAppointmentTimeForPractitioner invoked >");
		
		_date.add(Calendar.DATE, 1);
			// assume that we're looking for the next available appointment on a day other than the current one
		
		AppointmentBean test_appt = new AppointmentBean();
		test_appt.setCompany(_company);
		test_appt.setPractitioner(_practitioner);
		test_appt.setType(_type);
		//test_appt.setDuration(_type.getDuration());
		test_appt.setDuration(_duration);
		
		PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(_practitioner, _date.getTime());
		
		for (int day = 0; day < 365; day++)
		{
			Iterator itr = practitioner_schedule.getItems(_date.get(Calendar.DAY_OF_WEEK)).iterator();
			System.out.println("practitioner_schedule >" + practitioner_schedule.getLabel());
			if (itr.hasNext())
			{
				while (itr.hasNext())
				{
					PractitionerScheduleItemBean item = (PractitionerScheduleItemBean)itr.next();
					System.out.println("item >" + item.getLabel());
					
					// I need to loop through this item in 5 minute increments until I get to the end of it.
					
					int item_start_hour_of_day = item.getStartHourOfDay();
					int item_start_minute = item.getStartMinute();
					int item_end_hour_of_day = item.getEndHourOfDay();
					int item_end_minute = item.getEndMinute();
					
					_date.set(Calendar.HOUR_OF_DAY, item_start_hour_of_day);
					_date.set(Calendar.MINUTE, item_start_minute);
					
					boolean appointment_still_fits_within_item = true;
					while (appointment_still_fits_within_item)
					{
						// does the appointment fit within the item?
						
						System.out.println("does the appointment fit within the item?");
						
						Calendar appt_end_time = Calendar.getInstance();
						appt_end_time.setTime(_date.getTime());
						appt_end_time.add(Calendar.MINUTE, test_appt.getDuration());
						
						int appt_end_hour_of_day = appt_end_time.get(Calendar.HOUR_OF_DAY);
						int appt_end_minute = appt_end_time.get(Calendar.MINUTE);
						
						if ((item_end_hour_of_day > appt_end_hour_of_day) || ( (item_end_hour_of_day == appt_end_hour_of_day) && (item_end_minute > appt_end_minute) ) )
						{
							// appointment fits within the item
							
							test_appt.setAppointmentDate(_date.getTime());
							if (!AppointmentBean.hasConflict(test_appt, false))
								return test_appt;
							
							_date.add(Calendar.MINUTE, 5);
						}
						else
							appointment_still_fits_within_item = false;
					}
				}
			}
			
			_date.add(Calendar.DATE, 1);
			practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(_practitioner, _date.getTime());
			
		}
		
		throw new IllegalValueException("Cannot find an available appointment within the next year.");
	}
	
	public static AppointmentBean
	getNextAvailableAppointmentTimeForPractitioner(UKOnlineCompanyBean _company, UKOnlinePersonBean _practitioner, AppointmentTypeBean _type, Calendar _date, short _duration, int _days_to_look)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		System.out.println("getNextAvailableAppointmentTimeForPractitioner invoked >");
		
		//_date.add(Calendar.DATE, 1); //removed 9/13/17 - might be useful to return results for the passed day, especially if comparing available dates among practitioners
			// assume that we're looking for the next available appointment on a day other than the current one
		
		AppointmentBean test_appt = new AppointmentBean();
		test_appt.setCompany(_company);
		test_appt.setPractitioner(_practitioner);
		test_appt.setType(_type);
		//test_appt.setDuration(_type.getDuration());
		test_appt.setDuration(_duration);
		
		PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(_practitioner, _date.getTime());
		
		for (int day = 0; day < _days_to_look; day++)
		{
			Iterator itr = practitioner_schedule.getItems(_date.get(Calendar.DAY_OF_WEEK)).iterator();
			System.out.println("practitioner_schedule >" + practitioner_schedule.getLabel());
			if (itr.hasNext())
			{
				while (itr.hasNext())
				{
					PractitionerScheduleItemBean item = (PractitionerScheduleItemBean)itr.next();
					System.out.println("item >" + item.getLabel());
					
					// I need to loop through this item in 5 minute increments until I get to the end of it.
					
					int item_start_hour_of_day = item.getStartHourOfDay();
					int item_start_minute = item.getStartMinute();
					int item_end_hour_of_day = item.getEndHourOfDay();
					int item_end_minute = item.getEndMinute();
					
					_date.set(Calendar.HOUR_OF_DAY, item_start_hour_of_day);
					_date.set(Calendar.MINUTE, item_start_minute);
					
					boolean appointment_still_fits_within_item = true;
					while (appointment_still_fits_within_item)
					{
						// does the appointment fit within the item?
						
						System.out.println("does the appointment fit within the item?");
						
						Calendar appt_end_time = Calendar.getInstance();
						appt_end_time.setTime(_date.getTime());
						appt_end_time.add(Calendar.MINUTE, test_appt.getDuration());
						
						int appt_end_hour_of_day = appt_end_time.get(Calendar.HOUR_OF_DAY);
						int appt_end_minute = appt_end_time.get(Calendar.MINUTE);
						
						if ((item_end_hour_of_day > appt_end_hour_of_day) || ( (item_end_hour_of_day == appt_end_hour_of_day) && (item_end_minute > appt_end_minute) ) )
						{
							// appointment fits within the item
							
							test_appt.setAppointmentDate(_date.getTime());
							if (!AppointmentBean.hasConflict(test_appt, false))
								return test_appt;
							
							_date.add(Calendar.MINUTE, 5);
						}
						else
							appointment_still_fits_within_item = false;
					}
				}
			}
			
			_date.add(Calendar.DATE, 1); // go to the next day
			practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(_practitioner, _date.getTime());
			
		}
		
		throw new IllegalValueException("Cannot find an available appointment within the next year.");
	}
	
	/*
	public static Vector
	getNextAvailableAppointmentTimesForPractitioner(UKOnlineCompanyBean _company, UKOnlinePersonBean _practitioner, AppointmentTypeBean _type, Calendar _date, short _duration, int _days_to_look)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		System.out.println("getNextAvailableAppointmentTimeForPractitioner invoked >");
		
		Vector available_appointments = new Vector();
		
		
		_date.add(Calendar.DATE, 1);
			// assume that we're looking for the next available appointment on a day other than the current one
		_date.set(Calendar.HOUR_OF_DAY, 4);
		_date.set(Calendar.SECOND, 0);
		_date.set(Calendar.MILLISECOND, 0);
		
		
		
		PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(_practitioner, _date.getTime());
		
		for (int day = 0; day < _days_to_look; day++)
		{
			Iterator itr = practitioner_schedule.getItems(_date.get(Calendar.DAY_OF_WEEK)).iterator();
			if (itr.hasNext())
			{
				while (itr.hasNext())
				{
					PractitionerScheduleItemBean item = (PractitionerScheduleItemBean)itr.next();
					System.out.println("item >" + item.getLabel());
					
					// I need to loop through this item in 5 minute increments until I get to the end of it.
					
					int item_start_hour_of_day = item.getStartHourOfDay();
					int item_start_minute = item.getStartMinute();
					int item_end_hour_of_day = item.getEndHourOfDay();
					int item_end_minute = item.getEndMinute();
					
					_date.set(Calendar.HOUR_OF_DAY, item_start_hour_of_day);
					_date.set(Calendar.MINUTE, item_start_minute);
					
					boolean appointment_still_fits_within_item = true;
					while (appointment_still_fits_within_item)
					{
						// does the appointment fit within the item?
						
						System.out.println("does the appointment fit within the item?");
						
						AppointmentBean test_appt = new AppointmentBean();
						test_appt.setCompany(_company);
						test_appt.setPractitioner(_practitioner);
						test_appt.setType(_type);
						//test_appt.setDuration(_type.getDuration());
						test_appt.setDuration(_duration);
						
						Calendar appt_end_time = Calendar.getInstance();
						appt_end_time.setTime(_date.getTime());
						appt_end_time.add(Calendar.MINUTE, test_appt.getDuration());
						appt_end_time.set(Calendar.SECOND, 0);
						appt_end_time.set(Calendar.MILLISECOND, 0);
						
						int appt_end_hour_of_day = appt_end_time.get(Calendar.HOUR_OF_DAY);
						int appt_end_minute = appt_end_time.get(Calendar.MINUTE);
						
						if ((item_end_hour_of_day > appt_end_hour_of_day) || ( (item_end_hour_of_day == appt_end_hour_of_day) && (item_end_minute > appt_end_minute) ) )
						{
							// appointment fits within the item
							
							test_appt.setAppointmentDate(_date.getTime());
							if (!AppointmentBean.hasConflict(test_appt, false)) {
								//return test_appt;
								available_appointments.addElement(test_appt);
								_date.add(Calendar.HOUR, 4);
							}
							else
								_date.add(Calendar.MINUTE, 15);
						}
						else
							appointment_still_fits_within_item = false;
					}
				}
			}
			
			_date.add(Calendar.DATE, 1);
			practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(_practitioner, _date.getTime());
			
		}
		
		return available_appointments;
	}
	*/
	
	
	/*
	public static Vector
	getNextAvailableAppointmentTimesForPractitioners(UKOnlineCompanyBean _company, Vector _practitioners, AppointmentTypeBean _type, Calendar _date, short _duration, int _days_to_look)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		System.out.println("getNextAvailableAppointmentTimeForPractitioner invoked >");
		
		Vector available_appointments = new Vector();
		
		
		_date.add(Calendar.DATE, 1); //removed 9/13/17 - might be useful to return results for the passed day, especially if comparing available dates among practitioners
			// assume that we're looking for the next available appointment on a day other than the current one
		_date.set(Calendar.HOUR_OF_DAY, 4);
		_date.set(Calendar.SECOND, 0);
		_date.set(Calendar.MILLISECOND, 0);
		
		
		PractitionerScheduleBean practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(_practitioner, _date.getTime());
		
		for (int day = 0; day < _days_to_look; day++)
		{
			Iterator itr = practitioner_schedule.getItems(_date.get(Calendar.DAY_OF_WEEK)).iterator();
			if (itr.hasNext())
			{
				while (itr.hasNext())
				{
					PractitionerScheduleItemBean item = (PractitionerScheduleItemBean)itr.next();
					System.out.println("item >" + item.getLabel());
					
					
					
					// I need to loop through this item in 5 minute increments until I get to the end of it.
					
					int item_start_hour_of_day = item.getStartHourOfDay();
					int item_start_minute = item.getStartMinute();
					int item_end_hour_of_day = item.getEndHourOfDay();
					int item_end_minute = item.getEndMinute();
					
					_date.set(Calendar.HOUR_OF_DAY, item_start_hour_of_day);
					_date.set(Calendar.MINUTE, item_start_minute);
					
					boolean appointment_still_fits_within_item = true;
					while (appointment_still_fits_within_item)
					{
						// does the appointment fit within the item?
						
						System.out.println("does the appointment fit within the item?");
						
						AppointmentBean test_appt = new AppointmentBean();
						test_appt.setCompany(_company);
						test_appt.setPractitioner(_practitioner);
						test_appt.setType(_type);
						//test_appt.setDuration(_type.getDuration());
						test_appt.setDuration(_duration);
						
						Calendar appt_end_time = Calendar.getInstance();
						appt_end_time.setTime(_date.getTime());
						appt_end_time.add(Calendar.MINUTE, test_appt.getDuration());
						appt_end_time.set(Calendar.SECOND, 0);
						appt_end_time.set(Calendar.MILLISECOND, 0);
						
						int appt_end_hour_of_day = appt_end_time.get(Calendar.HOUR_OF_DAY);
						int appt_end_minute = appt_end_time.get(Calendar.MINUTE);
						
						if ((item_end_hour_of_day > appt_end_hour_of_day) || ( (item_end_hour_of_day == appt_end_hour_of_day) && (item_end_minute > appt_end_minute) ) )
						{
							// appointment fits within the item
							
							test_appt.setAppointmentDate(_date.getTime());
							if (!AppointmentBean.hasConflict(test_appt, false)) {
								//return test_appt;
								available_appointments.addElement(test_appt);
								_date.add(Calendar.HOUR, 4);
							}
							else
								_date.add(Calendar.MINUTE, 15);
						}
						else
							appointment_still_fits_within_item = false;
					}
				}
			}
			
			_date.add(Calendar.DATE, 1);
			practitioner_schedule = PractitionerScheduleBean.getPractitionerSchedule(_practitioner, _date.getTime());
			
		}
		
		return available_appointments;
	}
	
	*/
    
    public static AppointmentBean
    getLastAppointmentForClient(UKOnlinePersonBean _client)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
		crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
		crit.add(AppointmentPeer.APPOINTMENT_DATE, new Date(), Criteria.LESS_THAN);
		crit.addDescendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		List obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() == 0)
			return null;
		else
			return AppointmentBean.getAppointment((Appointment)obj_list.get(0));
    }
    
    public static AppointmentBean
    getLastAppointmentForClient(UKOnlinePersonBean _client, PracticeAreaBean _practice_area, short _state)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.addJoin(AppointmentPeer.APPOINTMENT_TYPE_ID, AppointmentTypePeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		crit.add(AppointmentPeer.APPOINTMENT_DATE, new Date(), Criteria.LESS_THAN);
		crit.add(AppointmentTypePeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.addDescendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		List obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() == 0)
			return null;
		else
			return AppointmentBean.getAppointment((Appointment)obj_list.get(0));
    }

    public static AppointmentBean
    getLastAppointmentForClientCheckedInOrCheckedOut(UKOnlinePersonBean _client, PracticeAreaBean _practice_area)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.addJoin(AppointmentPeer.APPOINTMENT_TYPE_ID, AppointmentTypePeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
		crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
		crit.add(AppointmentPeer.APPOINTMENT_DATE, new Date(), Criteria.LESS_THAN);
		crit.add(AppointmentTypePeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.addDescendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		List obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() == 0)
			return null;
		else
			return AppointmentBean.getAppointment((Appointment)obj_list.get(0));
    }
    
    public static HashMap
    getOffTime(CompanyBean _company, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
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

		HashMap off_time_hash = new HashMap();

		Criteria crit = new Criteria();
		crit.addJoin(AppointmentTypePeer.APPOINTMENT_TYPE_ID, AppointmentPeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);

		crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_TYPE, AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE);

		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		//System.out.println("crit >" + crit.toString());
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
			/*
			String time_str = date_format.format(appointment.getAppointmentDate());
			String key = time_str + "|" + appointment.getPractitioner().getId();
			 */
			String key = appointment.getKey();
			Vector hash_vec = (Vector)off_time_hash.get(key);
			if (hash_vec == null)
			{
				hash_vec = new Vector();
				off_time_hash.put(key, hash_vec);
			}
			//hash.put(key, appointment);
			hash_vec.addElement(appointment);
		}

		return off_time_hash;
    }
    
    public static Vector
    getOpenAppointments(CompanyBean _company, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(AppointmentTypePeer.APPOINTMENT_TYPE_ID, AppointmentPeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_TYPE, AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE);
		crit.add(AppointmentPeer.COMPANY_ID, _company.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, _date, Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
		crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.LATE_APPOINTMENT_STATUS);
		crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		//System.out.println("getOpenAppointments crit >" + crit.toString());
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = AppointmentBean.getAppointment((Appointment)itr.next());
			vec.addElement(appointment);
		}

		return vec;
    }

    public static boolean
    hasAppointmentTodayForClientNotAlreadyCheckedOut(UKOnlinePersonBean _client)
		throws TorqueException
    {	
		Date now = new Date();
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.setTime(now);
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);

		Calendar end_of_day = Calendar.getInstance();
		end_of_day.setTime(now);
		end_of_day.set(Calendar.HOUR_OF_DAY, 23);
		end_of_day.set(Calendar.MINUTE, 59);
		end_of_day.set(Calendar.SECOND, 59);

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
		crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.LATE_APPOINTMENT_STATUS);
		crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
		crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.NO_SHOW_APPOINTMENT_STATUS);
		crit.setLimit(1);
		System.out.println("hasAppointmentTodayForClientNotAlreadyCheckedOut CRIT >" + crit.toString());
		return !AppointmentPeer.doSelect(crit).isEmpty();
    }

    public static AppointmentBean
    getAppointmentTodayForClientNotAlreadyCheckedOut(UKOnlinePersonBean _client)
		throws TorqueException, ObjectNotFoundException
    {
		Date now = new Date();
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.setTime(now);
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);

		Calendar end_of_day = Calendar.getInstance();
		end_of_day.setTime(now);
		end_of_day.set(Calendar.HOUR_OF_DAY, 23);
		end_of_day.set(Calendar.MINUTE, 59);
		end_of_day.set(Calendar.SECOND, 59);

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
		crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.LATE_APPOINTMENT_STATUS);
		crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
		crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.NO_SHOW_APPOINTMENT_STATUS);
		crit.setLimit(1);
		List list = AppointmentPeer.doSelect(crit);
		if (list.size() == 1) {
			return AppointmentBean.getAppointment((Appointment)list.get(0));
		}
		throw new ObjectNotFoundException("Unable to locate appointment for today that is not already checked out for " + _client.getLabel());
    }

    public static boolean
    hasAppointmentForClient(UKOnlinePersonBean _client, short _state)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		crit.setLimit(1);
		//System.out.println("ANALYZE CRIT >" + crit.toString());
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		return (itr.hasNext());
    }

    public static boolean
    hasAppointmentForClient(UKOnlinePersonBean _client, PracticeAreaBean _practice_area, short _state)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.addJoin(AppointmentPeer.APPOINTMENT_TYPE_ID, AppointmentTypePeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentPeer.CLIENT_ID, _client.getId());
		crit.add(AppointmentPeer.APPOINTMENT_STATE, _state);
		crit.add(AppointmentTypePeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
		crit.setLimit(1);
		//System.out.println("ANALYZE CRIT >" + crit.toString());
		Iterator itr = AppointmentPeer.doSelect(crit).iterator();
		return (itr.hasNext());
    }

    public static boolean
    hasConflict(AppointmentBean _appointment, boolean _allow_multiple_appointments_at_same_time)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		
		boolean default_value = allow_multiple_appointments_at_same_time;
		allow_multiple_appointments_at_same_time = _allow_multiple_appointments_at_same_time;

		try
		{
			boolean has_conflict = AppointmentBean.hasConflict(_appointment);
			allow_multiple_appointments_at_same_time = default_value;
			return has_conflict;
		}
		finally
		{
			allow_multiple_appointments_at_same_time = default_value;
		}
    }
    
    public static boolean
    hasConflict(AppointmentBean _appointment)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		
		if (_appointment.getType().getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
			return AppointmentBean.hasConflictOffTime(_appointment);

		if (allow_multiple_appointments_at_same_time && allow_multiple_appointments_at_same_time_of_varying_types)
			return false;
		
		

		Calendar appointment_start = Calendar.getInstance();
		Calendar appointment_end = Calendar.getInstance();
		appointment_start.setTime(_appointment.getAppointmentDate());
		appointment_end.setTime(_appointment.getAppointmentDate());
		appointment_end.add(Calendar.MINUTE, _appointment.getDuration());
		
		appointment_start.set(Calendar.SECOND, 0);
		appointment_start.set(Calendar.MILLISECOND, 0);
		
		appointment_end.set(Calendar.SECOND, 0);
		appointment_end.set(Calendar.MILLISECOND, 0);
		
		
		boolean showOut= false;
		//if (appointment_start.get(Calendar.DATE) == 22)
		//	showOut= true;

		Criteria crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.GREATER_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		if (showOut)
			System.out.println("hasConflict crit1 >" + crit.toString());
		List obj_list = AppointmentPeer.doSelect(crit);

		if (allow_multiple_appointments_at_same_time)
		{
			// multiple appointments are allowed, but they must be of the same appointment type

		   if (AppointmentBean.hasTypeConflict(_appointment, obj_list))
			   return true;
		}
		else
		{
			// both allow_multiple_appointments_at_same_time and allow_multiple_appointments_at_same_time_of_varying_types must be false at this point...

			if (obj_list.size() > 0)
				return true;
		}

		crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.LESS_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.LESS_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		if (showOut)
			System.out.println("hasConflict crit2 >" + crit.toString());
		obj_list = AppointmentPeer.doSelect(crit);

		if (allow_multiple_appointments_at_same_time)
		{
			// multiple appointments are allowed, but they must be of the same appointment type

		   if (AppointmentBean.hasTypeConflict(_appointment, obj_list))
			   return true;
		}
		else
		{
			// both allow_multiple_appointments_at_same_time and allow_multiple_appointments_at_same_time_of_varying_types must be false at this point...

			if (obj_list.size() > 0)
				return true;
		}

		crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.LESS_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		if (showOut)
			System.out.println("hasConflict crit3 >" + crit.toString());
		obj_list = AppointmentPeer.doSelect(crit);

		if (allow_multiple_appointments_at_same_time)
		{
			// multiple appointments are allowed, but they must be of the same appointment type

		   if (AppointmentBean.hasTypeConflict(_appointment, obj_list))
			   return true;
		}
		else
		{
			// both allow_multiple_appointments_at_same_time and allow_multiple_appointments_at_same_time_of_varying_types must be false at this point...

			if (obj_list.size() > 0)
				return true;
		}

		crit = new Criteria();
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.LESS_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.GREATER_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		if (showOut)
			System.out.println("hasConflict crit4 >" + crit.toString());
		obj_list = AppointmentPeer.doSelect(crit);

		if (allow_multiple_appointments_at_same_time)
		{
			// multiple appointments are allowed, but they must be of the same appointment type

		   if (AppointmentBean.hasTypeConflict(_appointment, obj_list))
			   return true;
		}
		else
		{
			// both allow_multiple_appointments_at_same_time and allow_multiple_appointments_at_same_time_of_varying_types must be false at this point...

			if (obj_list.size() > 0)
				return true;
		}

		return false;
    }
    
	public static boolean
	hasPractitionerScheduleConflict(AppointmentBean _appointment)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		//System.out.println("hasPractitionerScheduleConflict() invoked");
		UKOnlinePersonBean practitioner = _appointment.getPractitioner();
		//System.out.println("practitioner >" + practitioner.getLabel());
		PractitionerScheduleBean practitioner_schedule = practitioner.getPractitionerSchedule(_appointment.getAppointmentDate());
		//System.out.println("practitioner_schedule >" + practitioner_schedule.getLabel());
		return practitioner_schedule.hasConflict(_appointment);
	}
    
    private static boolean
    hasConflictOffTime(AppointmentBean _appointment)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Calendar appointment_start = Calendar.getInstance();
		Calendar appointment_end = Calendar.getInstance();
		appointment_start.setTime(_appointment.getAppointmentDate());
		appointment_end.setTime(_appointment.getAppointmentDate());
		appointment_end.add(Calendar.MINUTE, _appointment.getDuration());

		Criteria crit = new Criteria();
		crit.addJoin(AppointmentTypePeer.APPOINTMENT_TYPE_ID, AppointmentPeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_TYPE, AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE);
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.GREATER_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		System.out.println("hasConflictOffTime crit1 >" + crit.toString());
		List obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() > 0)
			return true;

		crit = new Criteria();
		crit.addJoin(AppointmentTypePeer.APPOINTMENT_TYPE_ID, AppointmentPeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_TYPE, AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE);
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.LESS_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.LESS_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		System.out.println("hasConflictOffTime crit2 >" + crit.toString());
		obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() > 0)
			return true;

		crit = new Criteria();
		crit.addJoin(AppointmentTypePeer.APPOINTMENT_TYPE_ID, AppointmentPeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_TYPE, AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE);
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.GREATER_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.LESS_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		System.out.println("hasConflictOffTime crit3 >" + crit.toString());
		obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() > 0)
			return true;

		crit = new Criteria();
		crit.addJoin(AppointmentTypePeer.APPOINTMENT_TYPE_ID, AppointmentPeer.APPOINTMENT_TYPE_ID);
		crit.add(AppointmentTypePeer.APPOINTMENT_TYPE_TYPE, AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE);
		crit.add(AppointmentPeer.COMPANY_ID, _appointment.getCompany().getId());
		crit.add(AppointmentPeer.PRACTITIONER_ID, _appointment.getPractitioner().getId());
		crit.add(AppointmentPeer.APPOINTMENT_DATE, appointment_start.getTime(), Criteria.LESS_EQUAL);
		crit.and(AppointmentPeer.APPOINTMENT_DATE, appointment_end.getTime(), Criteria.LESS_THAN);
		crit.add(AppointmentPeer.APPOINTMENT_END_DATE, appointment_start.getTime(), Criteria.GREATER_THAN);
		crit.and(AppointmentPeer.APPOINTMENT_END_DATE, appointment_end.getTime(), Criteria.GREATER_EQUAL);
		if (!_appointment.isNew())
			crit.add(AppointmentPeer.APPOINTMENT_ID, _appointment.getId(), Criteria.NOT_EQUAL);
		System.out.println("hasConflictOffTime crit4 >" + crit.toString());
		obj_list = AppointmentPeer.doSelect(crit);
		if (obj_list.size() > 0)
			return true;

		return false;
    }
    
    private static boolean
    hasTypeConflict(AppointmentBean _appointment, List _obj_list)
		throws TorqueException, ObjectNotFoundException
    {
		AppointmentTypeBean passed_appointment_type = _appointment.getType();
		Iterator apt_itr = _obj_list.iterator();
		while (apt_itr.hasNext())
		{
			AppointmentBean appointment_obj = AppointmentBean.getAppointment((Appointment)apt_itr.next());
			AppointmentTypeBean appointment_type_obj = appointment_obj.getType();
			
			if (appointment_type_obj.getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
			{
				// I'm trying to add the passed appointment to an off-time "appointment".  Check to see if it's an allowed type...

				System.out.println("I'm trying to add the passed appointment to an off-time \"appointment\".  Check to see if it's an allowed type...");
				
				if (!appointment_type_obj.isAllowedAppointmentType(passed_appointment_type))
				{
					// the passed appointment type is not allowed for this off-time appointment
					
					return true;
				}
			}
			else //if (appointment_type_obj.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
			{
				// I'm trying to add the passed appointment to another client appointment, or a meeting, I guess

				System.out.println("I'm trying to add the passed appointment to another client appointment, or a meeting, I guess >" + allow_multiple_appointments_at_same_time);
				
				if (allow_multiple_appointments_at_same_time)
				{
					System.out.println("appointment_obj.isCancelled() >" + appointment_obj.isCancelled());
					System.out.println("appointment_obj.isRescheduled() >" + appointment_obj.isRescheduled());

					if (!appointment_obj.isCancelled() && !appointment_obj.isRescheduled())
					{
						System.out.println("appointment_type_obj.equals(passed_appointment_type) >" + appointment_type_obj.equals(passed_appointment_type));

						if (!appointment_type_obj.equals(passed_appointment_type))
							return true;
					}
				}
				else
					return true;
			}
			
		}

		return false;
    }
    
    // SQL
    
    /*
     * <table name="APPOINTMENT" idMethod="native">
	    <column name="APPOINTMENT_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	    
            <column name="COMPANY_ID" required="true" type="INTEGER"/>
	    
	    <column name="CLIENT_ID" required="true" type="INTEGER"/>
	    <column name="PRACTITIONER_ID" required="true" type="INTEGER"/>
	    
	    <column name="APPOINTMENT_TYPE_ID" required="false" type="INTEGER"/>
	    <column name="PARENT_ID" required="false" type="INTEGER"/>
	    
	    <column name="APPOINTMENT_STATE" type="SMALLINT" default="0"/>
	    <column name="APPOINTMENT_DATE" required="true" type="DATE"/>
	    <column name="DURATION_MINUTES" required="true" type="SMALLINT" />
	    
	    <column name="CREATION_DATE" required="true" type="DATE"/>
	    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
	    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
	    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

            <foreign-key foreignTable="COMPANY">
                <reference local="COMPANY_ID" foreign="COMPANYID"/>
            </foreign-key>
	    <foreign-key foreignTable="PERSON">
		<reference local="CLIENT_ID" foreign="PERSONID"/>
	    </foreign-key>
	    <foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
	    </foreign-key>
	    <foreign-key foreignTable="APPOINTMENT_TYPE">
		<reference local="APPOINTMENT_TYPE_ID" foreign="APPOINTMENT_TYPE_ID"/>
	    </foreign-key>
	    <foreign-key foreignTable="APPOINTMENT">
		<reference local="PARENT_ID" foreign="APPOINTMENT_ID"/>
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
	
    private Appointment appointment;
	private Date old_appointment_date;
	private boolean changing_time = false;
	
	private Vector child_appointments = null;
    
    // CONSTRUCTORS
    
    public
    AppointmentBean()
    {
		appointment = new Appointment();
		isNew = true;
    }
    
    public
    AppointmentBean(Appointment _appointment)
    {
		appointment = _appointment;
		isNew = false;
    }
    
    // INSTANCE METHODS
	
	public boolean
	equals(AppointmentBean _appointment)
	{
		if (_appointment == null)
			return false;
		if (this.isNew())
			return false;
		if (_appointment.isNew())
			return false;
		return (this.getId() == _appointment.getId());
	}
    
    public Date
    getAppointmentDate()
    {
		return appointment.getAppointmentDate();
    }
    
    public String
    getAppointmentDateString()
    {
		Date date = appointment.getAppointmentDate();
		if (date == null)
			return "";
		return CUBean.getUserDateString(date);
    }
	
	/*
	public static long convertTimeZone(long lngDate, String fromTimeZone,
        String toTimeZone) {        
		Calendar fromTime = Calendar.getInstance();
		fromTime.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
		fromTime.setTimeInMillis(lngDate);
		Calendar toTime = new GregorianCalendar(
				TimeZone.getTimeZone(toTimeZone));
		toTime.set(Calendar.DATE, fromTime.get(Calendar.DATE));
		toTime.set(Calendar.MONTH, fromTime.get(Calendar.MONTH));
		toTime.set(Calendar.YEAR, fromTime.get(Calendar.YEAR));
		toTime.set(Calendar.HOUR_OF_DAY, fromTime.get(Calendar.HOUR_OF_DAY));
		toTime.set(Calendar.MINUTE, fromTime.get(Calendar.MINUTE));
		toTime.set(Calendar.SECOND, fromTime.get(Calendar.SECOND));
		toTime.set(Calendar.MILLISECOND, fromTime.get(Calendar.MILLISECOND));
		LOG.debug("Converted " + fromTime.getTimeInMillis() + " to "
				+ toTime.getTimeInMillis());
		SimpleDateFormat sdf = new SimpleDateFormat(
				"dd-MMM-yyyy HH:mm:ss.SSS z");      
		sdf.setTimeZone(TimeZone.getTimeZone(fromTimeZone));        
		SimpleDateFormat sdf1 = new SimpleDateFormat(
				"dd-MMM-yyyy HH:mm:ss.SSS z");      
		sdf1.setTimeZone(TimeZone.getTimeZone(toTimeZone));
		LOG.debug("Converted " + sdf.format(fromTime.getTime()) + " to " + sdf1.format(toTime.getTime()));
		return toTime.getTimeInMillis();
	}
	*/
	
	
    public long
    getAppointmentStartUnixTimestampInMillisNonAdjusted() {
		return appointment.getAppointmentDate().getTime();
    }
	
    public long
    getAppointmentEndUnixTimestampInMillisNonAdjusted() {
		return appointment.getAppointmentDate().getTime() + (appointment.getDurationMinutes() * 60 * 1000);
    }
    
    public long
    getAppointmentStartUnixTimestamp() {
		Date date = appointment.getAppointmentDate();
		return date.getTime() / 1000;
    }
    
    public long
    getAppointmentStartUnixTimestampInMillis() {
		Calendar c = Calendar.getInstance();
		c.setTime(appointment.getAppointmentDate());
		return c.getTimeInMillis() - this.getTimezoneOffsetForTimestampInMillis();
    }
    
    public long
    getAppointmentEndUnixTimestamp() {
		Calendar c = Calendar.getInstance();
		c.setTime(appointment.getAppointmentDate());
		c.add(Calendar.MINUTE, this.getDuration());
		return c.getTimeInMillis() / 1000;
    }
    
    public long
    getAppointmentEndUnixTimestampInMillis() {
		Calendar c = Calendar.getInstance();
		c.setTime(appointment.getAppointmentDate());
		c.add(Calendar.MINUTE, this.getDuration());
		return c.getTimeInMillis() - this.getTimezoneOffsetForTimestampInMillis();
    }
	
	//long timezone_utc_offset = -1l;
	public long
	getTimezoneOffsetForTimestampInMillis() {
		
		//if (timezone_utc_offset == -1l) {
			//TimeZone tz = TimeZone.getTimeZone("America/Chicago");
			//if (TimeZone.getTimeZone("CST").inDaylightTime(new Date())) { // this should be modified to include other time zones at some point
			if (TimeZone.getTimeZone("CST").inDaylightTime(appointment.getAppointmentDate())) { // this should be modified to include other time zones at some point
				//System.out.println(" we are in DST");
				//timezone_utc_offset = 18000000l; // 5 hour offset in millis
				return 18000000l; // 5 hour offset in millis
			} else {
				//System.out.println(" we are NOT in DST");
				//timezone_utc_offset = 21600000l; // 6 hour offset in millis
				return 21600000l; // 6 hour offset in millis
			}
		//}
		//return timezone_utc_offset;
	}
    
    public String
    getAppointmentDateTimeString()
    {
		Date date = appointment.getAppointmentDate();
		if (date == null)
			return "";
		
		return AppointmentBean.date_time_format.format(date);
    }
    
    public String
    getAppointmentTimeString() {
		Date date = appointment.getAppointmentDate();
		if (date == null) {
			return "";
		}
		
		return AppointmentBean.date_format.format(date);
    }
	
	public Vector
	getChildren()
		throws TorqueException
	{
		if (child_appointments == null)
		{
			child_appointments = new Vector();
			Criteria crit = new Criteria();
			crit.add(AppointmentPeer.PARENT_ID, this.getId());
			//crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DELETED_APPOINTMENT_STATUS, Criteria.NOT_EQUAL);
			crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
			Iterator itr = AppointmentPeer.doSelect(crit).iterator();
			while (itr.hasNext())
				child_appointments.addElement(AppointmentBean.getAppointment((Appointment)itr.next()));
		}
		
		return child_appointments;
	}
    
    public UKOnlinePersonBean
    getClient()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(appointment.getClientId());
    }
	
	public String
	getComments()
	{
		String str = appointment.getComments();
		if (str == null)
			return "";
		return str;
	}
    
    public CompanyBean
    getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return CompanyBean.getCompany(appointment.getCompanyId());
    }
    
    public int
    getDuration()
    {
		return appointment.getDurationMinutes();
    }
    
    public String
    getDurationString()
    {
		return appointment.getDurationMinutes() + "";
    }
    
    public int
    getId()
    {
		return appointment.getAppointmentId();
    }
	
	public String
	getKey()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		String time_str = AppointmentBean.date_format.format(appointment.getAppointmentDate());
		return time_str + "|" + this.getPractitioner().getId();
	}
	
	public String
	getKeyDayOfWeek()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Date date_obj = appointment.getAppointmentDate();
		Calendar appt_date = Calendar.getInstance();
		appt_date.setTime(date_obj);
		String time_str = AppointmentBean.date_format.format(date_obj);
		return time_str + "|" + appt_date.get(Calendar.DAY_OF_WEEK);
	}
    
    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		if (!this.hasType()) {
			return "";
		} else if (this.getType().getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE) {
			if (!this.hasClient()) {
				return "";
			}
			String client_type = "";
			String title_str = this.getClient().getTitleString();
			if (title_str.length() > 0)
			{
				client_type = " (" + title_str + ")";
				if (client_type.equals(" ([DEFAULT])"))
					client_type = "";
			}
			return this.getClient().getLabel() + client_type;
		}
		else if (this.getType().getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
			return this.getType().getLabel();
		else if (this.getType().getType() == AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE)
		{
			String comments = this.getComments();
			if (comments.equals(""))
				return this.getType().getLabel();
			if (comments.length() < 50)
				return comments;
			return comments.substring(0, 49) + "...";
		}
		else
			return "[Label]";
    }
    
    public String
    getLabelLong()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		if (this.getType().getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
		{
			String client_type = "";
			String title_str = this.getClient().getTitleString();
			if (title_str.length() > 0)
			{
				client_type = " (" + title_str + ")";
				if (client_type.equals(" ([DEFAULT])"))
					client_type = "";
			}
			return this.getClient().getLabel() + client_type;
		}
		else if (this.getType().getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
			return this.getType().getLabel();
		else if (this.getType().getType() == AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE)
		{
			String comments = this.getComments();
			if (comments.equals(""))
				return this.getType().getLabel();
			return comments;
		}
		else
			return "[Label]";
    }
    
    public String
    getLabelDateTime()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		if (this.getType().getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
			return this.getAppointmentDateTimeString();
		else if (this.getType().getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
			return this.getType().getLabel();
		else if (this.getType().getType() == AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE)
			return "Meeting";
		else
			return "[Label]";
    }
    
    public Date
    getLateDate()
    {
		return appointment.getLateDate();
    }
    
    public String
    getLateDateTimeString()
    {
		Date date = appointment.getLateDate();
		if (date == null)
			return "";
		
		return AppointmentBean.date_time_format.format(date);
    }
	
	public AppointmentBean
	getParent()
		throws TorqueException, ObjectNotFoundException
	{
		return AppointmentBean.getAppointment(appointment.getParentId());
	}
	
	public PracticeAreaBean
	getPracticeArea()
		throws TorqueException, ObjectNotFoundException
	{
		return this.getType().getPracticeArea();
	}

	public int
	getPracticeAreaId()
		throws TorqueException, ObjectNotFoundException
	{
		return this.getType().getPracticeAreaId();
	}
    
    public UKOnlinePersonBean
    getPractitioner()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(appointment.getPractitionerId());
    }

    public int
    getPractitionerId()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return appointment.getPractitionerId();
    }
    
    public TreatmentRoomBean
    getTreatmentRoom()
		throws TorqueException, ObjectNotFoundException
    {
		return TreatmentRoomBean.getTreatmentRoom(appointment.getRoomId());
    }

    public int
    getTreatmentRoomId()
    {
		return appointment.getRoomId();
    }
	
	public short
	getRecurVal()
	{
		return appointment.getRecurVal();
	}
	
	public short
	getRecurPeriod()
	{
		return appointment.getRecurPeriod();
	}
	
	public short
	getRecurPW()
	{
		return appointment.getRecurPw();
	}
	
	public short
	getRecurPWS()
	{
		return appointment.getRecurPws();
	}
	
	public short
	getRecurStopAfterN()
	{
		return appointment.getStopAfterN();
	}
	
	public Date
	getRecurStopByDate()
	{
		return appointment.getStopByDate();
	}
    
    public String
    getRecurStopByDateString()
    {
		Date date = appointment.getStopByDate();
		if (date == null)
			return "";
		return AppointmentBean.long_date_format.format(date);
    }
    
    public short
    getState()
    {
		return appointment.getAppointmentState();
    }
    
    public AppointmentTypeBean
    getType()
		throws TorqueException, ObjectNotFoundException
    {
		return AppointmentTypeBean.getAppointmentType(appointment.getAppointmentTypeId());
    }
    
    public String
    getValue()
    {
		return this.getId() + "";
    }
    
    public boolean
    hasClient()
    {
		return (appointment.getClientId() > 0);
    }
	
	public boolean
	hasParent()
	{
		return (appointment.getParentId() > 0);
	}
    
    public boolean hasType() {
		return (appointment.getAppointmentTypeId() > 0);
    }
	
	private Timer reminderTimer = null;
    
    protected void
    insertObject()
		throws Exception
    {
		maintainAppointmentEndDate();

		// check for conflicting appointments...

		if (AppointmentBean.hasConflict(this))
			throw new Exception("Can't create appointment.  It conflicts with another appointment.");
		
		// check to see if this conflicts with the practitioner's schedule
		
		UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(appointment.getCompanyId());
		
		if (AppointmentBean.hasPractitionerScheduleConflict(this))
		{
			// it conflicts, but is it allowed for this appointment type?

			AppointmentTypeBean practitioner_offtime_appointment_type = AppointmentTypeBean.getPractitionerScheduleOfftimeAppointmentType(company);
			if (!practitioner_offtime_appointment_type.isAllowedAppointmentType(this.getType()))
				throw new Exception("Can't create appointment.  It conflicts with the practitioner schedule for " + this.getPractitioner().getLabel() + ".");
		}

		appointment.setCreationDate(new Date());
		appointment.setModificationDate(new Date()); // meh

		this.calculateIsFirstTimeAppointmentForClientInPracticeArea();

		appointment.save();
		
		notifyNewAppointmentEvent();
		
		/* - removed 2/3/16 - wonky stuff was happening - not sure....
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, 60);
		reminderTimer = new Timer(true);
		reminderTimer.schedule(new AppointmentReminderTask(company, this), now.getTime());
		*/
		
		
		String apptLabel = this.getValue();
		System.out.println("insertObject() apptLabel >" + apptLabel);
		AppointmentBean.new_appts_hash.put(apptLabel, Boolean.TRUE);
		
    }
	
	public void
	invalidateChildren()
	{
		child_appointments = null;
	}

	public boolean
	isCancelled()
	{
		return (this.getState() == AppointmentBean.CANCELLED_APPOINTMENT_STATUS);
	}
	
	public boolean
	isChangingTime()
	{
		return changing_time;
	}

	public boolean
	isCheckedIn()
	{
		return (this.getState() == AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
	}
	
	public boolean
	isCheckedOut()
	{
		return (this.getState() == AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
	}
	
	public boolean
	isClientAppointment() throws TorqueException, ObjectNotFoundException {
		return (this.getType().getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE);
	}

	/*
	public boolean
	isDeleted()
	{
		return (this.getState() == AppointmentBean.DELETED_APPOINTMENT_STATUS);
	}
	 */

	public boolean
	isFirstTimeAppointmentForClientInPracticeArea()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (!this.isClientAppointment())
			return false;

		try {
			this.calculateIsFirstTimeAppointmentForClientInPracticeArea();
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		return (appointment.getFirstTime() == (short)1);
	}

	private void
	calculateIsFirstTimeAppointmentForClientInPracticeArea()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		//System.out.println("calculateIsFirstTimeAppointmentForClientInPracticeArea() invoked >" + this.getLabel());

		if (this.isClientAppointment())
		{
			if (this.isNew())
			{
				//System.out.println("doing new stuff");

				Criteria crit = new Criteria();
				crit.addJoin(AppointmentPeer.APPOINTMENT_TYPE_ID, AppointmentTypePeer.APPOINTMENT_TYPE_ID);
				crit.add(AppointmentPeer.CLIENT_ID, this.getClient().getId());
				crit.add(AppointmentPeer.FIRST_TIME, (short)1);
				crit.add(AppointmentTypePeer.PRACTICE_AREA_ID, this.getPracticeArea().getId());
				crit.setLimit(1);
				Iterator itr = AppointmentPeer.doSelect(crit).iterator();
				if (!itr.hasNext())
				{
					// unable to find another appointment in this practice area for this client that's specifically flagged as "first time" - look for other candidates...

					//System.out.println("unable to find another appointment in this practice area for this client that's specifically flagged as \"first time\" - look for other candidates...");

					crit = new Criteria();
					crit.addJoin(AppointmentPeer.APPOINTMENT_TYPE_ID, AppointmentTypePeer.APPOINTMENT_TYPE_ID);
					crit.add(AppointmentPeer.CLIENT_ID, this.getClient().getId());
					crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
					crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
					crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
					crit.add(AppointmentTypePeer.PRACTICE_AREA_ID, this.getPracticeArea().getId());
					crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
					crit.setLimit(1);
					//System.out.println("ANALYZE CRIT >" + crit.toString());
					itr = AppointmentPeer.doSelect(crit).iterator();
					if (itr.hasNext())
					{
						AppointmentBean candidate_appointment = AppointmentBean.getAppointment((Appointment)itr.next());
						boolean candidate_first_time = false;
						if (candidate_appointment.isCheckedOut() || candidate_appointment.isCheckedIn())
							candidate_first_time = true;
						else
						{
							// candidate appt is in default status.  if this appointment is newer, mark it as first time, otherwise mark the candidate as first time

							if (this.getAppointmentDate().before(candidate_appointment.getAppointmentDate()))
								this.setIsFirstTimeAppointmentForClientInPracticeArea(true);
							else
								candidate_first_time = true;
						}

						//System.out.println("candidate_first_time >" + candidate_first_time);

						if (candidate_first_time)
						{
							candidate_appointment.setIsFirstTimeAppointmentForClientInPracticeArea(true);
							candidate_appointment.save();
						}
					}
					else
						this.setIsFirstTimeAppointmentForClientInPracticeArea(true);
				}
				else
				{
					//System.out.println("existing appointment with stuff already");
					this.setIsFirstTimeAppointmentForClientInPracticeArea(false);
				}
			}
			else
			{
				//System.out.println("doing old stuff >" + appointment.getFirstTime());

				if (appointment.getFirstTime() == (short)0)
				{
					
					boolean this_is_no_show = false;
					if (this.getState() == AppointmentBean.NO_SHOW_APPOINTMENT_STATUS)
					{
						this_is_no_show = true;
						appointment.setFirstTime((short)2);
					}

					// has not been calculated previously.  find all the candidate appointments for this client/practice area and set the correct values...

					//System.out.println("has not been calculated previously.  find all the candidate appointments for this client/practice area and set the correct values...");

					Criteria crit = new Criteria();
					crit.addJoin(AppointmentPeer.APPOINTMENT_TYPE_ID, AppointmentTypePeer.APPOINTMENT_TYPE_ID);
					crit.add(AppointmentPeer.CLIENT_ID, this.getClient().getId());
					crit.add(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
					crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
					crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
					crit.or(AppointmentPeer.APPOINTMENT_STATE, AppointmentBean.LATE_APPOINTMENT_STATUS);
					crit.add(AppointmentTypePeer.PRACTICE_AREA_ID, this.getPracticeArea().getId());
					crit.addAscendingOrderByColumn(AppointmentPeer.APPOINTMENT_DATE);
					//crit.setLimit(1);
					//System.out.println("ANALYZE CRIT >" + crit.toString());
					Iterator itr = AppointmentPeer.doSelect(crit).iterator();
					boolean first_set = false;
					while (itr.hasNext())
					{
						Appointment obj = (Appointment)itr.next();
						if (!first_set)
						{
							// this is the first time appointment...
							
							boolean set_obj = false;

							if (this_is_no_show)
							{
								if (obj.getAppointmentId() != this.getId())
								{
									if (obj.getFirstTime() != (short)1)
									{
										// not 1, but needs to be
										AppointmentBean appointment_obj = AppointmentBean.getAppointment(obj);
										appointment_obj.setIsFirstTimeAppointmentForClientInPracticeArea(true);
										appointment_obj.save();
									}

									first_set = true;
								}
							}
							else
							{
								if (obj.getFirstTime() != (short)1)
								{
									// not 1, but needs to be
									AppointmentBean appointment_obj = AppointmentBean.getAppointment(obj);
									appointment_obj.setIsFirstTimeAppointmentForClientInPracticeArea(true);
									appointment_obj.save();
								}

								first_set = true;
							}
							
						}
						else
						{
							if (obj.getFirstTime() != (short)2)
							{
								// not 1, but needs to be
								AppointmentBean appointment_obj = AppointmentBean.getAppointment(obj);
								appointment_obj.setIsFirstTimeAppointmentForClientInPracticeArea(false);
								appointment_obj.save();
							}
						}
					}


				}

			}
		}
	}

	public void
	setIsFirstTimeAppointmentForClientInPracticeArea(boolean _value)
	{
		appointment.setFirstTime(_value ? (short)1 : (short)2);
	}
	
	public boolean
	isMeeting()
		throws TorqueException, ObjectNotFoundException
	{
		return (this.getType().getType() == AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE);
	}
	
	public boolean
	isOfftime()
		throws TorqueException, ObjectNotFoundException
	{
		return (this.getType().getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE);
	}
	
	public boolean
	isRecurring()
	{
		return (appointment.getRecurring() == (short)1);
	}

	public boolean
	isRescheduled()
	{
		return (this.getState() == AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS);
	}
    
    private void
    maintainAppointmentEndDate()
    {
		Calendar end_date = Calendar.getInstance();
		end_date.setTime(appointment.getAppointmentDate());
		end_date.add(Calendar.MINUTE, appointment.getDurationMinutes());

		appointment.setAppointmentEndDate(end_date.getTime());
    }
    
    public void
    setAppointmentDate(Date _date)
    {
		changing_time = true;
		old_appointment_date = appointment.getAppointmentDate();
		appointment.setAppointmentDate(_date);
    }
    
    public void
    setClient(UKOnlinePersonBean _client)
		throws TorqueException
    {
		try
		{
			if (!_client.isActive())
			{
				_client.setActive(true);
				_client.save();
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		appointment.setClientId(_client.getId());
    }
	
	public void
	setComments(String _comments)
	{
		appointment.setComments(_comments);
	}
    
    public void
    setCompany(CompanyBean _company)
		throws TorqueException
    {
		appointment.setCompanyId(_company.getId());
    }
    
    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			appointment.setCreatePersonId(_person.getId());
		else
			appointment.setModifyPersonId(_person.getId());
    }
    
    public void
    setDuration(short _duration)
    {
		changing_time = true;
		appointment.setDurationMinutes(_duration);
    }
	
	public void
	setIsRecurring(boolean _recurring)
	{
		appointment.setRecurring(_recurring ? (short)1 : (short)0);
	}
	
	public void
	setParent(AppointmentBean _parent)
		throws TorqueException
	{
		appointment.setParentId(_parent.getId());
	}
    
    public void
    setPractitioner(UKOnlinePersonBean _practitioner)
		throws TorqueException
    {
		appointment.setPractitionerId(_practitioner.getId());
    }
    
    public void
    setRoom(TreatmentRoomBean _room)
		throws TorqueException
    {
		appointment.setRoomId(_room.getId());
    }
	
	public void
	setRecurVal(short _val)
	{
		appointment.setRecurVal(_val);
	}
	
	public void
	setRecurPeriod(short _period)
	{
		appointment.setRecurPeriod(_period);
	}
	
	public void
	setRecurPW(short _pw)
	{
		appointment.setRecurPw(_pw);
	}
	
	public void
	setRecurPWS(short _pws)
	{
		appointment.setRecurPws(_pws);
	}
	
	public void
	setRecurStopAfterN(short _n)
	{
		appointment.setStopAfterN(_n);
	}
	
	public void
	setRecurStopByDate(Date _n_date)
	{
		appointment.setStopByDate(_n_date);
	}
    
	public void
	setState(short _state)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		appointment.setAppointmentState(_state);

		switch (_state)
		{
			case AppointmentBean.CANCELLED_APPOINTMENT_STATUS: appointment.setCancelledDate(new Date()); break;
			case AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS: appointment.setCheckedInDate(new Date()); break;
			case AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS: appointment.setCheckedOutDate(new Date()); break;
			case AppointmentBean.LATE_APPOINTMENT_STATUS: appointment.setLateDate(new Date()); break;
			case AppointmentBean.NO_SHOW_APPOINTMENT_STATUS: appointment.setNoShowDate(new Date()); appointment.setFirstTime((short)0); this.calculateIsFirstTimeAppointmentForClientInPracticeArea(); break;
			case AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS: appointment.setRescheduleDate(new Date()); break;
		}
	}
    
    public void
    setType(AppointmentTypeBean _type)
		throws TorqueException
    {
		appointment.setAppointmentTypeId(_type.getId());
    }
	
	private boolean is_template = false;
	public void
	setIsTemplate(boolean _b) {
		is_template = _b;
	}
	public boolean
	isTemplate() {
		return is_template;
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		maintainAppointmentEndDate();

		if (this.isChangingTime()) {
			
			// check for conflicting appointments...
			
			try {

				if (AppointmentBean.hasConflict(this)) {
					throw new Exception("Can't update appointment.  It conflicts with another appointment.");
				}

				// check to see if this conflicts with the practitioner's schedule

				if (AppointmentBean.hasPractitionerScheduleConflict(this)) {
					
					// it conflicts, but is it allowed for this appointment type?

					AppointmentTypeBean practitioner_offtime_appointment_type = AppointmentTypeBean.getPractitionerScheduleOfftimeAppointmentType(this.getCompany());
					if (!practitioner_offtime_appointment_type.isAllowedAppointmentType(this.getType()))
						throw new Exception("Can't update appointment.  It conflicts with the practitioner schedule for " + this.getPractitioner().getLabel() + ".");
				}
			} catch (Exception x) {
				appointment.setAppointmentDate(old_appointment_date);
				throw x;
			}
			
		}

		appointment.setModificationDate(new Date());
		appointment.save();
		
		notifyUpdatedAppointmentEvent();

		this.updateCache();

		this.calculateIsFirstTimeAppointmentForClientInPracticeArea();
		
		
		if (reminderTimer != null) {
			reminderTimer.cancel();

			Calendar now = Calendar.getInstance();
			now.add(Calendar.MINUTE, 60);
			UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(appointment.getCompanyId());
			reminderTimer.schedule(new AppointmentReminderTask(company, this), now.getTime());
		}
    }

	private void
	updateCache()
	{
		Date appointment_date = this.getAppointmentDate();
		int practitioner_id = 0;
		try
		{
			practitioner_id = this.getPractitioner().getId();
		}
		catch (Exception x)
		{
		}
		int practice_area_id = 0;
		try
		{
			practice_area_id = this.getPracticeAreaId();
		}
		catch (Exception x)
		{
		}

		String key1 = appointment.getCompanyId() + "|" + CUBean.getUserDateString(appointment_date);
		String key2 = appointment.getCompanyId() + "|" + practice_area_id + "|" + CUBean.getUserDateString(appointment_date);

		Calendar start_of_week = Calendar.getInstance();
		start_of_week.setTime(appointment_date);
		Calendar end_of_week = Calendar.getInstance();
		end_of_week.setTime(appointment_date);

		start_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		end_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

		String key3 = appointment.getCompanyId() + "|" + practitioner_id + "|" + CUBean.getUserDateString(start_of_week.getTime()) + "|" + CUBean.getUserDateString(end_of_week.getTime());
		String key6 = appointment.getCompanyId() + "|" + CUBean.getUserDateString(start_of_week.getTime()) + "|" + CUBean.getUserDateString(end_of_week.getTime());
		
				
				
		Calendar start_of_month = Calendar.getInstance();
		start_of_month.setTime(appointment_date);
		Calendar end_of_month = Calendar.getInstance();
		end_of_month.setTime(appointment_date);
		start_of_month.set(Calendar.DATE, 1);
		end_of_month.set(Calendar.DATE, start_of_month.getActualMaximum(Calendar.DATE));

		String key4 = appointment.getCompanyId() + "|" + CUBean.getUserDateString(start_of_month.getTime()) + "|" + CUBean.getUserDateString(end_of_month.getTime());
		String key5 = appointment.getCompanyId() + "|" + practice_area_id + "|" + CUBean.getUserDateString(start_of_month.getTime()) + "|" + CUBean.getUserDateString(end_of_month.getTime());
		
		
		
		

		Date update_timestamp = new Date();
		AppointmentBean.appointment_update_hash.put(key1, update_timestamp);
		AppointmentBean.appointment_update_hash.put(key2, update_timestamp);
		AppointmentBean.appointment_update_hash.put(key3, update_timestamp);
		
		AppointmentBean.appointment_update_hash.put(key4, update_timestamp);
		AppointmentBean.appointment_update_hash.put(key5, update_timestamp);
		
		AppointmentBean.appointment_update_hash.put(key6, update_timestamp);
	}
	
	public String
	getInitialAppointmentEmailString() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, UnsupportedEncodingException {
		
		AddressBean address = this.getCompany().getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
		String appt_date_str = CUBean.getUserDateString(this.getAppointmentDate(), "EEEE, MMMM d, yyyy");
		
		
		String encryptedStr = EncryptionUtils.encryptString(this.getValue());
		
		StringBuffer buf = new StringBuffer();
		buf.append("<div>");
		buf.append("  <table cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px #acacac solid;width:615px\" align=\"center\">");
		buf.append("    <tbody><tr>");
		buf.append("      <td bgcolor=\"#11100e\" height=\"89\" valign=\"top\">");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:613px\">");
		buf.append("          <tbody><tr>");
		buf.append("            <td valign=\"middle\" height=\"89\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:24px;color:#8e8d8d;padding-left:30px;overflow:hidden;word-wrap:break-word;width:350px\">" + this.getCompany().getLabel());
		buf.append("            <br><span style=\"font-size:20px\">Appointment Reminder</span></td>");
		if (this.getCompany().getId() == 5)
			buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"><img src=\"http://3.bp.blogspot.com/-F3clsArp_q4/TshlXxkxJ_I/AAAAAAAAAAg/K_kSbulF0gw/s1600/Sano%2Blogo-small.jpg\"  /></td>");
		else
			buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"></td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("      </td>");
		buf.append("    </tr>");
		buf.append("    <tr>");
		buf.append("      <td>");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		buf.append("          <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word;padding:14px 26px 14px 26px\">" + this.getCompany().getLabel());
		buf.append("                  <br>" + address.getStreet1String() + " <br>" + address.getStreet2String() );
		buf.append("                  <br>" + address.getCityString() + ", " + address.getStateString() + " " + address.getZipCodeString());
		buf.append("                  <br><a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a></td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr>");
		buf.append("            <td style=\"padding-left:26px\">");
		buf.append("              <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #acacac;width:559px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td bgcolor=\"#e1eeef\" style=\"padding:13px 20px 13px 20px;font-family:Arial,Helvetica,sans-serif;color:#317679;font-size:18px;font-weight:bold;line-height:24px;overflow:auto;word-wrap:break-word;width:240px\">");
		buf.append("                  Dear " + this.getClient().getFirstNameString() + ",");
		buf.append("                  <br />This is an appointment reminder.  Click the link below to confirm:<br /> <a href=\"https://www.valonyx.com/ScheduleServlet2?command=confirmAppointment&arg1=" + URLEncoder.encode(encryptedStr, "UTF-8") + "\" target=\"_blank\">Confirm My Appointment</a> <br />You can check in, on the day of this appointment, by logging in with your password <strong>" + this.getClient().getPasswordString() + "</strong> at <a href=\"http://www.valonyx.com/client/index.jsp\" target=\"_blank\">www.valonyx.com/client</a>.</td>");
		buf.append("                  <td style=\"color:#6e6e6e;font-family:Arial,Helvetica,sans-serif;font-size:14px;padding-left:51px;padding-right:10px;line-height:22px;overflow:auto;word-wrap:break-word;width:218px\">" + this.getType().getLabel());
		//buf.append("                  <br>" + this.getPractitioner().getLabel());
		buf.append("                  <br />" + appt_date_str);
		buf.append("                  <br />" + CUBean.getUserTimeString(this.getAppointmentDate()));
		buf.append("                  <br /></td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        <tr>");
		buf.append("            <td style=\"padding:12px 26px 16px 26px\">");
		buf.append("              <table width=\"561\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:561px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word\">");
		buf.append("If you have any questions, please contact " + this.getCompany().getLabel() + " at <a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a> . ");
		buf.append("                  <br>&nbsp;");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr bgcolor=\"#f2f2f2\">");
		buf.append("            <td style=\"border-top:1px solid #acacac;padding:26px 0px 14px 26px\">");
		buf.append("              <table width=\"587\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:587px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#6e6e6e;width:420px;overflow:auto;word-wrap:break-word\">This e-mail was sent on behalf of  ");
		buf.append("                  <a href=\"http://www.sanowc.com\" style=\"color:#317679!important\" target=\"_blank\">" + this.getCompany().getLabel() + "</a>");
		buf.append("                  <br>by <a href=\"http://www.valonyx.com\">valonyx.com</a>. This is an automated email; please do not reply. ");
		buf.append("                  <br>&nbsp;");
		buf.append("                  <br>");
		buf.append("                  ");
		buf.append("                  <br></td>");
		buf.append("                  <td width=\"167\" valign=\"bottom\">");
		buf.append("                    <a href=\"http://www.valonyx.com\"><img src=\"http://www.valonyx.com/images/valonyx-logo-grey-198.png\" /></a>");
		buf.append("                  </td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("</div>");
		
		return buf.toString();
	}
	
	public void
	confirm() {
		this.appointment.setConfirmedDate(new Date());
	}
	
	public boolean
	isConfirmed() {
		return ( this.appointment.getConfirmedDate() != null );
	}
	
	public String
	getConfirmationDateString() {
		return CUBean.getUserDateString(this.appointment.getConfirmedDate());
	}

	private void
	notifyNewAppointmentEvent() {
		try {
			if (eventListeners != null) {
				//if (this.isNew()) { // why was this here???
					Vector temp = null;
					synchronized (this) { temp = (Vector)eventListeners.clone(); }
					NewAppointmentEvent event = new NewAppointmentEvent(this);
					Iterator itr = temp.iterator();
					while (itr.hasNext()) {
						AppointmentEventListener listener = (AppointmentEventListener)itr.next();
						listener.newAppointment(event);
					}
				//}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private void
	notifyUpdatedAppointmentEvent() {
		System.out.println("notifyUpdatedAppointmentEvent() invoked in AppointmentBean >" + eventListeners);
		try {
			if (eventListeners != null) {
				Vector temp = null;
				synchronized (this) { temp = (Vector)eventListeners.clone(); }
				UpdatedAppointmentEvent event = new UpdatedAppointmentEvent(this);
				Iterator itr = temp.iterator();
				while (itr.hasNext()) {
					AppointmentEventListener listener = (AppointmentEventListener)itr.next();
					listener.updatedAppointment(event);
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private void
	notifyDeletedAppointmentEvent() {
		try {
			if (eventListeners != null) {
				Vector temp = null;
				synchronized (this) { temp = (Vector)eventListeners.clone(); }
				DeletedAppointmentEvent event = new DeletedAppointmentEvent(this);
				Iterator itr = temp.iterator();
				while (itr.hasNext()) {
					AppointmentEventListener listener = (AppointmentEventListener)itr.next();
					listener.deletedAppointment(event);
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}