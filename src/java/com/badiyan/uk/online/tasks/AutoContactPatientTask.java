/*
 * AutoContactPatientTask.java
 * Created on May 20, 2018, 12:19 PM
 */
package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
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
AutoContactPatientTask
    extends TimerTask
{
    // INSTANCE VARIABLES
	
	private CompanyBean company;

    // CONSTRUCTORS

    public
    AutoContactPatientTask(CompanyBean _company) {
		company = _company;
    }

    // INSTANCE METHODS

	@Override
    public void
    run() {
		System.out.println("run() invoked in AutoContactPatientTask for >" + company.getLabel());
		
		try {
			
			Calendar now = Calendar.getInstance();
			if (now.get(Calendar.HOUR_OF_DAY) > 6) {
				CUBean.sendEmail("marlo@badiyan.com", "cstueve@sanowc.com", "Auto Contact Patient Stuff Not Sent", "nt");
				return;
			}
			
			Calendar two_months_ago = Calendar.getInstance();
			two_months_ago.add(Calendar.MONTH, -2);
			String two_months_ago_str = CUBean.getUserDateString(two_months_ago.getTime());
			//System.out.println("two_months_ago_str >" + two_months_ago_str);
			
			Calendar three_months_ago = Calendar.getInstance();
			three_months_ago.add(Calendar.MONTH, -3);
			String three_months_ago_str = CUBean.getUserDateString(three_months_ago.getTime());
			
			
			
			Iterator people_itr = company.getPeople().iterator();
			while (people_itr.hasNext()) {
				PersonBean person_obj = (PersonBean)people_itr.next();
				UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person_obj.getId());
				//System.out.println("client >" + client.getLabel());
				if (client.isActive()) {
					ValeoOrderBean.getOrders(client);
					//AppointmentBean appointment = AppointmentBean.getLastAppointmentForClient(client);
					OrderBean lastOrder = ValeoOrderBean.getLastOrder(client);
					if (lastOrder != null) {
						String last_order_str = lastOrder.getOrderDateString();
						if (two_months_ago_str.equals(last_order_str)) {
							System.out.println("client >" + client.getLabel() + "(" + client.getId() + ")  two months .. >" + last_order_str);
							//CUBean.sendEmailWithMeeting(client.getEmail1String(), "do_not_reply@sanowc.com", company.getLabel() + " Followup Reminder", null, this.getEmailString(client));
							CUBean.sendEmailWithMeeting("marlo@badiyan.com", "do_not_reply@sanowc.com", company.getLabel() + " Followup Reminder", null, this.getEmailString(client));
						} else if (three_months_ago_str.equals(last_order_str)) {
							System.out.println("client >" + client.getLabel() + "(" + client.getId() + ")  three months .. >" + last_order_str);
							
							ClientReviewReason client_review_reason = null;
							ReviewReason review_reason = ReviewReason.getReviewReason(20); // Other

							if (client_review_reason == null) {
								client_review_reason = new ClientReviewReason();
							}
							
							Calendar review_date = Calendar.getInstance();
							review_date.add(Calendar.DATE, 1);
							
							client_review_reason.getEmailString();

							client_review_reason.setPerson(client);
							client_review_reason.setReviewDate(review_date.getTime());
							client_review_reason.setReviewReason(review_reason);
							if (client.isFemale()) {
								client_review_reason.setNote("Follow-up with " + client.getLabel() + ".  She hasn't been in for 3 months.");
							} else {
								client_review_reason.setNote("Follow-up with " + client.getLabel() + ".  He hasn't been in for 3 months.");
							}
							Vector practitioners = new Vector();
							practitioners.addElement((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(3121)); // Christine
							practitioners.addElement((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(3213)); // Lisa
							client_review_reason.setPractitioners(practitioners);

							client_review_reason.setReviewed(false);
							client_review_reason.setCreateOrModifyPerson((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(3121));
							client_review_reason.setEmailClient(false);
							client_review_reason.save();
			
						}
						
					}
				}
				
			}
			
		} catch (Exception x) {
			x.printStackTrace();
		}
    }
	
	public String
	getEmailString(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		AddressBean address = company.getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
		//String appt_date_str = CUBean.getUserDateString(this.getReviewDate(), "EEEE, MMMM d, yyyy");
		
		//String review_str = "Dr. Stueve has requested that you contact Sano for your follow-up.";
		
		String review_str = "It's been awhile since we've seen you!  Please consider making an appointment at Sano soon to address any of your health concerns.";
		
		StringBuffer buf = new StringBuffer();
		buf.append("<div>");
		buf.append("  <table cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px #acacac solid;width:615px\" align=\"center\">");
		buf.append("    <tbody><tr>");
		buf.append("      <td bgcolor=\"#11100e\" height=\"89\" valign=\"top\">");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:613px\">");
		buf.append("          <tbody><tr>");
		buf.append("            <td valign=\"middle\" height=\"89\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:24px;color:#8e8d8d;padding-left:30px;overflow:hidden;word-wrap:break-word;width:350px\">" + company.getLabel());
		buf.append("            <br><span style=\"font-size:20px\">Appointment Reminder</span></td>");
		if (company.getId() == 5)
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
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word;padding:14px 26px 14px 26px\">" + company.getLabel());
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
		buf.append("                  Dear " + _person.getFirstNameString() + ",");
		buf.append("                  <br>" + review_str + "</td>");
		//buf.append("                  <td style=\"color:#6e6e6e;font-family:Arial,Helvetica,sans-serif;font-size:14px;padding-left:51px;padding-right:10px;line-height:22px;overflow:auto;word-wrap:break-word;width:218px\">" +  this.getReviewReason().getLabel() );
		//buf.append("                  <br>" + this.getPractitioner().getLabel());
		//buf.append("                  <br>" + appt_date_str);
		//buf.append("                  <br>" + CUBean.getUserTimeString(this.getReviewDate()));
		buf.append("                  <br></td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        <tr>");
		buf.append("            <td style=\"padding:12px 26px 16px 26px\">");
		buf.append("              <table width=\"561\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:561px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word\">");
		buf.append("If you have any questions, please contact " + company.getLabel() + " at <a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a> . ");
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
		buf.append("                  <a href=\"http://www.sanowc.com\" style=\"color:#317679!important\" target=\"_blank\">" + company.getLabel() + "</a>");
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
	
}