/*
 * AppointmentMonitorTask.java
 *
 * Created on April 21, 2008, 2:52 PM
 * 
 */

package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.tasks.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.servlets.*;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
AppointmentMonitorTask
    extends TimerTask
{
    // INSTANCE VARIABLES
	
	private boolean first_run = true;
	private CompanyBean company;

	private int num_minutes_late_before_late = 5;
	private int num_minutes_late_before_no_show = 30;
	private boolean auto_no_show_late_appointments_at_closing_time = false;
	
	private int start_hour_of_day = 7;
	private int start_minute = 0;
	private int end_hour_of_day = 20;
	private int end_minute = 0;

	private TimeZone time_zone;

    // CONSTRUCTORS

    public
    AppointmentMonitorTask(CompanyBean _company)
    {
		System.out.println("AppointmentMonitorTask(CompanyBean) invoked >" + _company.getLabel());
		company = _company;
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in AppointmentMonitorTask for >" + company.getLabel());
		
		try
		{
			CompanySettingsBean settings = company.getSettings();
			start_hour_of_day = settings.getOpenHour();
			start_minute = settings.getOpenMinute();
			end_hour_of_day = settings.getCloseHour();
			end_minute = settings.getCloseMinute();

			time_zone = settings.getTimeZone();

			//System.out.println(company.getLabel() + " time_zone >" + time_zone);
			if (time_zone == null)
				return;

			Calendar now = Calendar.getInstance();
			Date now_time_zone_adjusted = this.changeTimeZone(now.getTime(), time_zone);
			
			SimpleDateFormat date_format = new SimpleDateFormat("MM-dd-yy h:mm a");
			//date_format.setTimeZone(time_zone);
			//System.out.println(company.getLabel() + " - " + time_zone.getDisplayName() + " - " + date_format.format(now.getTime()));

			Calendar closing_time = Calendar.getInstance();
			closing_time.set(Calendar.HOUR_OF_DAY, end_hour_of_day);
			closing_time.set(Calendar.MINUTE, end_minute);
			
			boolean is_after_closing_time = now.compareTo(closing_time) > 0;
			
			
			Iterator appointments = AppointmentBean.getOpenAppointments(company, now_time_zone_adjusted).iterator();
				// get open appointments "before" the passed date
			while (appointments.hasNext())
			{
				AppointmentBean appointment = (AppointmentBean)appointments.next();
				//System.out.println("found appointment with state >" + appointment.getState() + " < -- " + appointment.getAppointmentDateTimeString());
				if (appointment.getState() == AppointmentBean.DEFAULT_APPOINTMENT_STATUS)
				{
					Calendar late_time = Calendar.getInstance();
					late_time.setTime(appointment.getAppointmentDate());
					late_time.add(Calendar.MINUTE, num_minutes_late_before_late);

					//if (now.compareTo(late_time) > 0)
					if (now_time_zone_adjusted.after(late_time.getTime()))
					{
						appointment.setState(AppointmentBean.LATE_APPOINTMENT_STATUS);
						appointment.save();
					}
				}
				else if (appointment.getState() == AppointmentBean.LATE_APPOINTMENT_STATUS)
				{
					// when should a late appointment get changed to something else???
					// 
					
					//System.out.println("found late appointment with state >" + appointment.getState() + " < -- " + appointment.getLateDateTimeString());
					
					boolean change_to_no_show = false;
					if (auto_no_show_late_appointments_at_closing_time)
					{
						if (is_after_closing_time)
							change_to_no_show = true;
					}
					else
					{
						Calendar late_time = Calendar.getInstance();
						late_time.setTime(appointment.getLateDate());
						late_time.add(Calendar.MINUTE, num_minutes_late_before_no_show);
						
						Date late_time_time_zone_adjusted = this.changeTimeZone(late_time.getTime(), time_zone);

						//System.out.println(company.getLabel() + " xLATE COMPARE now - " + date_format.format(now_time_zone_adjusted));
						//System.out.println(company.getLabel() + " xLATE COMPARE late_time - " + date_format.format(late_time_time_zone_adjusted));
						//System.out.println("now_time_zone_adjusted.after(late_time_time_zone_adjusted) >" + now_time_zone_adjusted.after(late_time_time_zone_adjusted));
						
						//if (now.compareTo(late_time) > 0)
						if (now_time_zone_adjusted.after(late_time_time_zone_adjusted))
							change_to_no_show = true;
					}
					
					//System.out.println("change to no show >" + change_to_no_show);
					
					
					if (change_to_no_show)
					{
						appointment.setState(AppointmentBean.NO_SHOW_APPOINTMENT_STATUS);
						appointment.save();
					}
					
				}
				else if (appointment.getState() == AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS)
				{
					
				}
			}
			
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }
	
	private Date changeTimeZone(Date date, TimeZone zone) {
        Calendar first = Calendar.getInstance(zone);
        first.setTimeInMillis(date.getTime());

        Calendar output = Calendar.getInstance();
        output.set(Calendar.YEAR, first.get(Calendar.YEAR));
        output.set(Calendar.MONTH, first.get(Calendar.MONTH));
        output.set(Calendar.DAY_OF_MONTH, first.get(Calendar.DAY_OF_MONTH));
        output.set(Calendar.HOUR_OF_DAY, first.get(Calendar.HOUR_OF_DAY));
        output.set(Calendar.MINUTE, first.get(Calendar.MINUTE));
        output.set(Calendar.SECOND, first.get(Calendar.SECOND));
        output.set(Calendar.MILLISECOND, first.get(Calendar.MILLISECOND));

        return output.getTime();
    }
}