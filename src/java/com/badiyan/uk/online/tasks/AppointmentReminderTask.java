/*
 * AppointmentReminderTask.java
 *
 * Created on March 16, 2012, 3:41 PM
 * 
 */

package com.badiyan.uk.online.tasks;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.ICalBuilder;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.torque.*;

public class
AppointmentReminderTask
    extends TimerTask
{
	private static Vector clients_alread_emailed = new Vector();
	
	
    // INSTANCE VARIABLES
	
	private boolean first_run = true;
	private UKOnlineCompanyBean company;
	private AppointmentBean appt;

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
    AppointmentReminderTask(UKOnlineCompanyBean _company)
    {
		System.out.println("AppointmentReminderTask(CompanyBean) invoked >" + _company.getLabel());
		company = _company;
    }

    public
    AppointmentReminderTask(UKOnlineCompanyBean _company, AppointmentBean _appointment)
    {
		System.out.println("AppointmentReminderTask(CompanyBean) invoked >" + _company.getLabel());
		company = _company;
		appt = _appointment;
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		SimpleDateFormat date_format = new SimpleDateFormat("MM/d/yyyy");
		System.out.println("run() invoked in AppointmentReminderTask for >" + company.getLabel() + " - " + date_format.format(new Date()));
		clients_alread_emailed = new Vector();
		
		try
		{
			CompanySettingsBean settings = company.getSettings();
			
			/*
			start_hour_of_day = settings.getOpenHour();
			start_minute = settings.getOpenMinute();
			end_hour_of_day = settings.getCloseHour();
			end_minute = settings.getCloseMinute();
			*/

			time_zone = settings.getTimeZone();

			//System.out.println(company.getLabel() + " time_zone >" + time_zone);
			if (time_zone == null) {
				return;
			}

			if (appt == null) {
				
				Calendar now = Calendar.getInstance();
				if (now.get(Calendar.HOUR_OF_DAY) > 6) {
					CUBean.sendEmail("marlo@badiyan.com", "cstueve@sanowc.com", "Appointment Reminders Not Sent", "nt");
					return;
				}
				
				Calendar tomorrow_start = Calendar.getInstance();
				tomorrow_start.add(Calendar.DATE, 2);
				tomorrow_start.set(Calendar.HOUR_OF_DAY, 0);
				tomorrow_start.set(Calendar.MINUTE, 0);
				tomorrow_start.set(Calendar.SECOND, 0);

				Calendar tomorrow_end = Calendar.getInstance();
				tomorrow_end.add(Calendar.DATE, 2);
				tomorrow_end.set(Calendar.HOUR_OF_DAY, 23);
				tomorrow_end.set(Calendar.MINUTE, 59);
				tomorrow_end.set(Calendar.SECOND, 59);

				Date tomorrow_start_time_zone_adjusted = this.changeTimeZone(tomorrow_start.getTime(), time_zone);
				Date tomorrow_end_time_zone_adjusted = this.changeTimeZone(tomorrow_end.getTime(), time_zone);

				//SimpleDateFormat date_format = new SimpleDateFormat("MM-dd-yy h:mm a");

				Vector appts = AppointmentBean.getAppointments(company, tomorrow_start_time_zone_adjusted, tomorrow_end_time_zone_adjusted, AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
				System.out.println("num appts for " + company.getLabel() + " >" + appts.size());
				Iterator appointments = appts.iterator();
					// get open appointments "before" the passed date
				while (appointments.hasNext()) {
					
					AppointmentBean appointment = (AppointmentBean)appointments.next();

					//CUBean.sendEmail("marlo@badiyan.com", "marlo@valeowc.com", appointment.getCompany().getLabel() + " Appointment Reminder", appointment.getInitialAppointmentEmailString());
					System.out.println(" @1appointment.isClientAppointment() >" + appointment.isClientAppointment());
					if (appointment.isClientAppointment()) {

						System.out.println(" @found appointment for tomorrow >" + appointment.getLabelDateTime() + ", client >" + appointment.getClient().getLabel());

						if (!AppointmentReminderTask.clients_alread_emailed.contains(appointment.getClient())) {
							System.out.println(" appointment.getClient().getEmail1String() >" + appointment.getClient().getEmail1String());
							if (!appointment.getClient().getEmail1String().isEmpty()) {
								System.out.println(" &&** sending email to client >" + appointment.getClient().getLabel());
								CUBean.sendEmailWithMeeting(appointment.getClient().getEmail1String(), "do_not_reply@sanowc.com", appointment.getCompany().getLabel() + " Upcoming Appointment", ICalBuilder.toICal(appointment), appointment.getInitialAppointmentEmailString());
								CUBean.sendEmailWithMeeting("marlo@badiyan.com", "do_not_reply@sanowc.com", appointment.getCompany().getLabel() + " Upcoming Appointment --", ICalBuilder.toICal(appointment), appointment.getInitialAppointmentEmailString());
							}

							AppointmentReminderTask.clients_alread_emailed.addElement(appointment.getClient());

						} else {
							System.out.println("   @email already sent?!?! client >" + appointment.getClient().getLabel() + "   -- " + company.getLabel());
						}
					}

				}
			
			} else {
				
				//CUBean.sendEmail("marlo@badiyan.com", "marlo@valeowc.com", appointment.getCompany().getLabel() + " Appointment Reminder", appointment.getInitialAppointmentEmailString());
				System.out.println(" @2appointment.isClientAppointment() >" + appt.isClientAppointment());
				if (appt.isClientAppointment()) {

					System.out.println(" @found appointment for tomorrow >" + appt.getLabelDateTime() + ", client >" + appt.getClient().getLabel());

					if (!AppointmentReminderTask.clients_alread_emailed.contains(appt.getClient())) {
						System.out.println(" appointment.getClient().getEmail1String() >" + appt.getClient().getEmail1String());
						if (!appt.getClient().getEmail1String().isEmpty()) {
							System.out.println(" &&** sending email to client >" + appt.getClient().getLabel());
							CUBean.sendEmailWithMeeting(appt.getClient().getEmail1String(), "do_not_reply@sanowc.com", appt.getCompany().getLabel() + " Upcoming Appointment", ICalBuilder.toICal(appt), appt.getInitialAppointmentEmailString());
							CUBean.sendEmailWithMeeting("marlo@badiyan.com", "do_not_reply@sanowc.com", appt.getCompany().getLabel() + " Upcoming Appointment --", ICalBuilder.toICal(appt), appt.getInitialAppointmentEmailString());
						}

						AppointmentReminderTask.clients_alread_emailed.addElement(appt.getClient());

					} else {
						System.out.println("   @email already sent?!?! client >" + appt.getClient().getLabel() + "   -- " + company.getLabel());
					}
				}
			}
			
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }
	
	private void
	sendReminderEmail(AppointmentBean _appointment) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		
			
		//_appointment.	
		
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