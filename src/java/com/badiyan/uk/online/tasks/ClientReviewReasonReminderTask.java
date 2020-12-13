/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.tasks;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.ICalBuilder;
import java.util.*;

import org.apache.torque.*;

public class
ClientReviewReasonReminderTask
    extends TimerTask
{
	private static Vector clients_alread_emailed = new Vector();
	
	
    // INSTANCE VARIABLES
	
	private boolean first_run = true;
	private UKOnlineCompanyBean company;

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
    ClientReviewReasonReminderTask(UKOnlineCompanyBean _company)
    {
		System.out.println("ClientReviewReasonReminderTask(CompanyBean) invoked >" + _company.getLabel());
		company = _company;
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in ClientReviewReasonReminderTask for >" + company.getLabel());
		
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.HOUR_OF_DAY) > 6) {
			CUBean.sendEmail("marlo@badiyan.com", "cstueve@sanowc.com", "Client Review Reminders Not Sent", "nt");
			return;
		}
		
		try
		{
			CompanySettingsBean settings = company.getSettings();
			
			time_zone = settings.getTimeZone();

			//System.out.println(company.getLabel() + " time_zone >" + time_zone);
			if (time_zone == null) {
				return;
			}

			Calendar tomorrow_start = Calendar.getInstance();
			//tomorrow_start.add(Calendar.DATE, 1);
			tomorrow_start.set(Calendar.HOUR_OF_DAY, 0);
			tomorrow_start.set(Calendar.MINUTE, 0);
			tomorrow_start.set(Calendar.SECOND, 0);


			Calendar tomorrow_end = Calendar.getInstance();
			//tomorrow_end.add(Calendar.DATE, 1);
			tomorrow_end.set(Calendar.HOUR_OF_DAY, 23);
			tomorrow_end.set(Calendar.MINUTE, 59);
			tomorrow_end.set(Calendar.SECOND, 59);

			Date tomorrow_start_time_zone_adjusted = this.changeTimeZone(tomorrow_start.getTime(), time_zone);
			Date tomorrow_end_time_zone_adjusted = this.changeTimeZone(tomorrow_end.getTime(), time_zone);

			//SimpleDateFormat date_format = new SimpleDateFormat("MM-dd-yy h:mm a");

			//Vector appts = AppointmentBean.getAppointments(company, tomorrow_start_time_zone_adjusted, tomorrow_end_time_zone_adjusted, AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
			Vector review_items = ClientReviewReason.getClientReviewReasons(company, tomorrow_start_time_zone_adjusted, tomorrow_end_time_zone_adjusted);
			
			System.out.println("num review items for " + company.getLabel() + " >" + review_items.size());
			Iterator itr = review_items.iterator();
				
			while (itr.hasNext()) {

				ClientReviewReason review_reason = (ClientReviewReason)itr.next();

				System.out.println(" @found ClientReviewReason for today >" + review_reason.getLabel() + ", client >" + review_reason.getPerson().getLabel());

				CUBean.sendEmailWithMeeting("marlo@badiyan.com", "do_not_reply@sanowc.com", company.getLabel() + " Followup Reminder", null, review_reason.getPractitionerEmailString());
				Iterator practitioner_itr = review_reason.getPractitioners().iterator();
				while (practitioner_itr.hasNext()) {
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
					CUBean.sendEmailWithMeeting(practitioner.getEmail1String(), "do_not_reply@sanowc.com", company.getLabel() + " Followup Reminder", null, review_reason.getPractitionerEmailString());
				}
					
				if (!ClientReviewReasonReminderTask.clients_alread_emailed.contains(review_reason.getPerson())) {
					System.out.println(" review_reason.getClient().getEmail1String() >" + review_reason.getPerson().getEmail1String());
					
					
					if (!review_reason.getPerson().getEmail1String().isEmpty()) {
						
						// removed 2/6/18 - based on call
						if (review_reason.emailClient()) {
							System.out.println(" &&** sending email to client >" + review_reason.getPerson().getLabel());
							CUBean.sendEmailWithMeeting(review_reason.getPerson().getEmail1String(), "do_not_reply@sanowc.com", company.getLabel() + " Followup Reminder", null, review_reason.getEmailString());
						}
						//CUBean.sendEmailWithMeeting("cstueve@sanowc.com", "cstueve@sanowc.com", company.getLabel() + " Followup Reminder Sent to " + review_reason.getPerson().getLabel(), ICalBuilder.toICal(review_reason), review_reason.getEmailString());
						//CUBean.sendEmailWithMeeting("lredding@sanowc.com", "cstueve@sanowc.com", company.getLabel() + " Followup Reminder Sent to " + review_reason.getPerson().getLabel(), ICalBuilder.toICal(review_reason), review_reason.getEmailString());
						
					}

					ClientReviewReasonReminderTask.clients_alread_emailed.addElement(review_reason.getPerson());

				} else {
					System.out.println("   @email already sent?!?! client >" + review_reason.getPerson().getLabel() + "   -- " + company.getLabel());
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