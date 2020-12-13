
package com.badiyan.uk.online.tasks;

import com.badiyan.torque.CheckoutOrderline;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.struts.SendBulkEmailAction;
import com.badiyan.uk.online.util.ICalBuilder;
import java.util.*;

import org.apache.torque.*;

public class
LowInventoryReminderTask
    extends TimerTask
{
	private static Vector clients_alread_emailed = new Vector();
	
    // INSTANCE VARIABLES
	
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
    LowInventoryReminderTask(UKOnlineCompanyBean _company) {
		System.out.println("LowInventoryReminderTask(CompanyBean) invoked >" + _company.getLabel());
		company = _company;
    }

    // INSTANCE METHODS

	@Override
    public void
    run() {
		
		System.out.println("run() invoked in LowInventoryReminderTaskXXX for >" + company.getLabel());
		
		try {
			
			CompanySettingsBean settings = company.getSettings();
			time_zone = settings.getTimeZone();

			//System.out.println(company.getLabel() + " time_zone >" + time_zone);
			if (time_zone == null) {
				return;
			}

			Calendar now = Calendar.getInstance();
			if (now.get(Calendar.HOUR_OF_DAY) > 6) {
				CUBean.sendEmail("marlo@badiyan.com", "cstueve@sanowc.com", "Low Inventory Reminder Not Sent", "nt");
				return;
			}
			
			// get all of the products (CheckoutCodes) sold yesterday
			
			Vector yesterday_codes = new Vector();

			Calendar yesterday = Calendar.getInstance();
			//yesterday.add(Calendar.DATE, -17); // was there a reason that this was -17?
			yesterday.add(Calendar.DATE, -1);
			Date yesterday_time_zone_adjusted = this.changeTimeZone(yesterday.getTime(), time_zone);
			Iterator itr = ValeoOrderBean.getOrders(yesterday_time_zone_adjusted).iterator();
			while (itr.hasNext()) {
				ValeoOrderBean order_obj = (ValeoOrderBean)itr.next();
				System.out.println("found order >" + order_obj.getLabel());
				
				// get all the 
				
				Iterator orderline_itr = order_obj.getOrdersVec().iterator();
				while (orderline_itr.hasNext()) {
					CheckoutOrderline orderline_obj = (CheckoutOrderline)orderline_itr.next();
					try {
						CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(orderline_obj.getCheckoutCodeId());

						if (checkout_code.isLowInStock() && !yesterday_codes.contains(checkout_code)) {
							System.out.println("adding code >" + checkout_code.getLabel());
							yesterday_codes.addElement(checkout_code);
						}
						
					} catch (ObjectNotFoundException x) {
						x.printStackTrace();
					}
				}
				
			}
			
			System.out.println("low stock >" + yesterday_codes.size());
			
			if (!yesterday_codes.isEmpty()) {
			
				StringBuilder b = new StringBuilder();
				Iterator code_itr = yesterday_codes.iterator();
				while (code_itr.hasNext()) {
					CheckoutCodeBean low_stock_code = (CheckoutCodeBean)code_itr.next();
					System.out.println("low_stock_code >" + low_stock_code.getLabel());
					b.append("<p>[" + low_stock_code.getOnHandQuantityString() + "]  " + low_stock_code.getLabel() + "</p>");
				}
				
				UKOnlinePersonBean lynette = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4206);
				UKOnlinePersonBean marlo = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(3124);
	
				String html_str = SendBulkEmailAction.getHTMLEmailString(lynette, "Low Stock Alert", b.toString());
				CUBean.sendEmailWithMeeting(lynette.getEmail1String(), "cstueve@sanowc.com", "Low Stock Alert", null, html_str);
				CUBean.sendEmailWithMeeting("marlo@badiyan.com", "cstueve@sanowc.com", "Low Stock Alert", null, html_str);
				
			}
			
			/*
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

					if (!LowInventoryReminderTask.clients_alread_emailed.contains(appointment.getClient())) {
						System.out.println(" appointment.getClient().getEmail1String() >" + appointment.getClient().getEmail1String());
						if (!appointment.getClient().getEmail1String().isEmpty()) {
							System.out.println(" &&** sending email to client >" + appointment.getClient().getLabel());
							CUBean.sendEmailWithMeeting(appointment.getClient().getEmail1String(), "cstueve@sanowc.com", appointment.getCompany().getLabel() + " Appointment Reminder", ICalBuilder.toICal(appointment), appointment.getInitialAppointmentEmailString());
							CUBean.sendEmailWithMeeting("marlo@badiyan.com", "cstueve@sanowc.com", appointment.getCompany().getLabel() + " Appointment Reminder --", ICalBuilder.toICal(appointment), appointment.getInitialAppointmentEmailString());
						}

						LowInventoryReminderTask.clients_alread_emailed.addElement(appointment.getClient());

					} else {
						System.out.println("   @email already sent?!?! client >" + appointment.getClient().getLabel() + "   -- " + company.getLabel());
					}
				}

			}
			
			*/
			
			
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