/*
 * ScheduleServletPlastique.java
 *
 * Created on November 23, 2007, 9:15 AM
 */

package com.badiyan.uk.online.servlets;

import com.badiyan.torque.*;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.*;

import com.badiyan.uk.online.PDF.*;
import com.badiyan.uk.online.tasks.ProcessCreditCardTask;
import com.badiyan.uk.online.tasks.SettleMerchantAccountTask;
import com.badiyan.uk.online.tasks.UpdateQuickBooksTask;
import com.badiyan.uk.online.tasks.VoidChargeTask;
import com.valeo.qb.data.InvoiceLineRet;
import com.valeo.qb.data.InvoiceRet;
import com.valeo.qbms.data.QBMSCreditCardResponse;
import com.valeo.qbpos.data.TenderRet;
import com.valonyx.beans.ShoppingCartBean;

import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.torque.*;

/**
 *
 * @author marlo
 * @version
 */
public class
ScheduleServletPlastique
    extends HttpServlet
{
	// "Tue Feb 12 00:00:00 CST 2008" - Internet Explorer
	// "Tue Feb 12 2008 00:00:00 GMT-0600 (Central Standard Time)"
	private static SimpleDateFormat ie_date_format = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
	private static SimpleDateFormat ns_date_format = new SimpleDateFormat("EEE MMM d yyyy HH");
	private static SimpleDateFormat yui_date_format = new SimpleDateFormat("M/d/yyyy"); // 2/1/2008
	private static SimpleDateFormat short_date_format = new SimpleDateFormat("MM/dd/yy"); // 2/1/2008
	
	private static SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
	
	//private static HashMap<String,Date> last_update_hash = new HashMap<String,Date>(11);
	private static HashMap<String,HashMap> last_update_hash = new HashMap<String,HashMap>(11);
	public static HashMap<String,Boolean> needs_update_hash = new HashMap<String,Boolean>(11);

	private static Timer timerObj = new Timer(true);
	
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void
    processRequest(HttpServletRequest _request, HttpServletResponse _response)
		throws ServletException, IOException
    {
		System.out.println("processRequest() invoked in ScheduleServletPlastique");

		HttpSession session = _request.getSession(false);

		//set the content type
		_response.setContentType("text/xml");
		_response.setHeader("Cache-Control", "no-cache");

		//get the PrintWriter object to write the html page
		PrintWriter writer = _response.getWriter();

		try
		{
			UKOnlineCompanyBean adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
			PracticeAreaBean adminPracticeArea = (PracticeAreaBean)session.getAttribute("adminPracticeArea");
			UKOnlinePersonBean adminPractitioner = (UKOnlinePersonBean)session.getAttribute("adminPractitioner");
			UKOnlineLoginBean adminLoginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");

			String command = _request.getParameter("command");
			String parameter = _request.getParameter("parameter");
			String arg1 = _request.getParameter("arg1");
			String arg2 = _request.getParameter("arg2");
			String arg3 = _request.getParameter("arg3");
			String arg4 = _request.getParameter("arg4");
			String arg5 = _request.getParameter("arg5");
			String arg6 = _request.getParameter("arg6");
			String arg7 = _request.getParameter("arg7");
			String session_str = session.getId();
			
			Date stamper = new Date();
			System.out.println("");
			System.out.println("stamp >" + CUBean.getUserDateString(stamper, "HH:mm:ss"));
			System.out.println("command >" + command);
			System.out.println("parameter >" + parameter);
			System.out.println("arg1 >" + arg1);
			System.out.println("arg2 >" + arg2);
			System.out.println("arg3 >" + arg3);
			System.out.println("arg4 >" + arg4);
					
			if (command.equals("refresh"))
			{
				session.setAttribute("calendarView", parameter);
				
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				if (display_date == null) {
					display_date = Calendar.getInstance();
					session.setAttribute("display_date", display_date);
				}
				
				String date_str = ScheduleServletPlastique.date_format.format(display_date.getTime());
				//String key = session_str + "" + date_str;
				
				Calendar startTime = null;
				Calendar endTime = null;
				
				String key = null;
				
				
				if (parameter == null) {
					
				} else if (parameter.equals("resourceDay")) {
					
				} else if (parameter.equals("agendaWeek")) {
					
					startTime = (Calendar)display_date.clone();
					startTime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
					endTime = (Calendar)display_date.clone();
					endTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
					
				} else if (parameter.equals("month")) {
					
					startTime = (Calendar)display_date.clone();
					startTime.set(Calendar.DATE, 1);
					endTime = (Calendar)display_date.clone();
					endTime.set(Calendar.DATE, display_date.getActualMaximum(Calendar.DATE));
				}
				
				
				if (endTime == null) {
					key = date_str;
				} else {
					key = ScheduleServletPlastique.short_date_format.format(startTime.getTime()) + "-" + ScheduleServletPlastique.short_date_format.format(endTime.getTime());
				}
				
				System.out.println("  ** key >" + key);

				boolean get_all_appointments = false;

				String refresh_tag_name = null;
				Boolean needs_update = (Boolean)ScheduleServletPlastique.needs_update_hash.get(session_str);
				System.out.println("needs_update >" + needs_update);
				if ((needs_update == null) || !needs_update.booleanValue()) {
					//Date last_update = (Date)ScheduleServletPlastique.last_update_hash.get(key);
					HashMap hash = (HashMap)ScheduleServletPlastique.last_update_hash.get(session_str);
					if (hash == null) {
						hash = new HashMap();
						last_update_hash.put(session_str, hash);
					}
					Date last_update = (Date)hash.get(key);
					System.out.println("  ** last_update >" + last_update);
					refresh_tag_name = (last_update == null) ? "status" : "refresh";
					System.out.println("  ** refresh_tag_name >" + refresh_tag_name);
				}
				else
				{
					System.out.println("NEEDS UPDATE!!!");
					refresh_tag_name = needs_update.booleanValue() ? "status" : "refresh";
					get_all_appointments = true;
					ScheduleServletPlastique.needs_update_hash.remove(session_str);
				}
				
				ScheduleServletPlastique.needs_update_hash.put(session_str, Boolean.FALSE);
				
				
				System.out.println("get_all_appointments >" + get_all_appointments);
				
				String return_str = null;
				
				if (parameter == null) {
					return_str = "<" + refresh_tag_name + ">" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, get_all_appointments).toString() + "</" + refresh_tag_name + ">";
				} else if (parameter.equals("resourceDay")) {
					return_str = "<" + refresh_tag_name + ">" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, get_all_appointments).toString() + "</" + refresh_tag_name + ">";
				} else if (parameter.equals("agendaWeek")) {
					
					//return_str = "<" + refresh_tag_name + ">" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, get_all_appointments).toString() + "</" + refresh_tag_name + ">";
					return_str = "<" + refresh_tag_name + ">" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, startTime, endTime, session_str, get_all_appointments).toString() + "</" + refresh_tag_name + ">";
					
				} else if (parameter.equals("month")) {
					
					//writer.println("<status>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, startOfMonth, endOfMonth, session_str, true).toString() + "</status>");
					return_str = "<" + refresh_tag_name + ">" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, startTime, endTime, session_str, get_all_appointments).toString() + "</" + refresh_tag_name + ">";
				}

				System.out.println("return_str >" + return_str);
				
				writer.println(return_str);
			}
			else if (command.equals("forceRefresh"))
			{
				ScheduleServletPlastique.needs_update_hash.remove(session_str);
				ScheduleServletPlastique.last_update_hash.remove(session_str);
				
				session.setAttribute("calendarView", parameter);
				
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				
				if (parameter == null) {
					writer.println("<status>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true).toString() + "</status>");
				} else if (parameter.equals("resourceDay")) {
					writer.println("<status>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true).toString() + "</status>");
				} else if (parameter.equals("agendaWeek")) {
					writer.println("<status>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true).toString() + "</status>");
				} else if (parameter.equals("month")) {
					
					Calendar startOfMonth = (Calendar)display_date.clone();
					startOfMonth.set(Calendar.DATE, 1);
					Calendar endOfMonth = (Calendar)display_date.clone();
					endOfMonth.set(Calendar.DATE, display_date.getActualMaximum(Calendar.DATE));
					
					writer.println("<status>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, startOfMonth, endOfMonth, session_str, true).toString() + "</status>");
				}
			}
			else if (command.equals("newAppointment"))
			{
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				writer.println("<status>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true).toString() + "</status>");
			}
			else if (command.equals("pickDay"))
			{
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				
				try
				{
					display_date.setTime(ScheduleServletPlastique.short_date_format.parse(parameter));
				}
				catch (java.text.ParseException x)
				{
					x.printStackTrace();
				}
				
				/*
				StringBuffer returnBuf = this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true);
				System.out.println("returnBuf >" + returnBuf.toString());
				writer.println("<status>" + returnBuf.toString() + "</status>");
				*/
				
				writer.println("<pdr y=\"" + display_date.get(Calendar.YEAR) + "\" m=\"" + display_date.get(Calendar.MONTH) + "\" d=\"" + display_date.get(Calendar.DATE) + "\" ></pdr>");
			}
			else if (command.equals("gotoDay"))
			{
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				
				try
				{
					display_date.setTime(ScheduleServletPlastique.ie_date_format.parse(parameter));
				}
				catch (java.text.ParseException x)
				{
					display_date.setTime(ScheduleServletPlastique.ns_date_format.parse(parameter.substring(0, parameter.indexOf(':'))));
				}
				
				StringBuffer returnBuf = this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true);
				
				String date_str = ScheduleServletPlastique.date_format.format(display_date.getTime());
				String key = session_str + "" + date_str;

				writer.println("<status>" + returnBuf.toString() + "</status>");
			}
			else if (command.equals("getCalHLPerson"))
			{
				//Calendar display_date = (Calendar)session.getAttribute("display_date");

				UKOnlinePersonBean person_to_get_calendar_highlights_for = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(parameter));
				String date_str = null;
				Iterator itr = AppointmentBean.getAppointmentsForClient(person_to_get_calendar_highlights_for).iterator();
				while (itr.hasNext())
				{
					AppointmentBean appointment = (AppointmentBean)itr.next();
					String appt_date_str = yui_date_format.format(appointment.getAppointmentDate());
					if (date_str == null)
						date_str = appt_date_str;
					else
						date_str += ',' + appt_date_str;
				}

				writer.println("<calhl>" + date_str + "</calhl>");
			}
			else if (command.equals("nextDay"))
			{
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				display_date.add(Calendar.DATE, 1);
				
				StringBuffer returnBuf = this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true);

				writer.println("<status>" + returnBuf.toString() + "</status>");
			}
			else if (command.equals("previousDay"))
			{
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				display_date.add(Calendar.DATE, -1);
				
				StringBuffer returnBuf = this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true);

				writer.println("<status>" + returnBuf.toString() + "</status>");
			}
			else if (command.equals("getPeopleByKeyword"))
			{
				StringBuffer buf = new StringBuffer();

				Vector last_vec = new Vector();
				Vector first_vec = null;
				Vector file_vec = null;
				try {
					file_vec = UKOnlinePersonBean.getPersonsByFileNumber(adminCompany, Integer.parseInt(parameter) + "");
				} catch (NumberFormatException x) {
					last_vec = UKOnlinePersonBean.getPersonsByLastName(adminCompany, parameter);
					first_vec = UKOnlinePersonBean.getPersonsByFirstName(adminCompany, parameter);
				}
				
				Iterator people_itr;
				if (first_vec != null) {
					people_itr = first_vec.iterator();
					while (people_itr.hasNext()) {
						UKOnlinePersonBean person = (UKOnlinePersonBean)people_itr.next();
						if (!last_vec.contains(person)) {
							last_vec.addElement(person);
						}
					}
				}
				
				if (file_vec != null) {
					people_itr = file_vec.iterator();
					while (people_itr.hasNext()) {
						UKOnlinePersonBean person = (UKOnlinePersonBean)people_itr.next();
						if (!last_vec.contains(person)) {
							last_vec.addElement(person);
						}
					}
				}
				
				people_itr = last_vec.iterator();
				while (people_itr.hasNext())
				{
					UKOnlinePersonBean person = (UKOnlinePersonBean)people_itr.next();
					buf.append(ScheduleServletPlastique.getXML(person));
				}

				//System.out.println("<clients>" + buf.toString() + "</clients>");

				writer.println("<clients>" + buf.toString() + "</clients>");
			}
			else if (command.equals("getPeopleByLastName"))
			{
				StringBuffer buf = new StringBuffer();

				Iterator people_itr = UKOnlinePersonBean.getPersonsByLastName(adminCompany, parameter).iterator();
				while (people_itr.hasNext())
				{
					UKOnlinePersonBean person = (UKOnlinePersonBean)people_itr.next();
					buf.append(ScheduleServletPlastique.getXML(person));
				}

				//System.out.println("<clients>" + buf.toString() + "</clients>");

				writer.println("<clients>" + buf.toString() + "</clients>");
			}
			else if (command.equals("getPeopleByFirstName"))
			{
				StringBuffer buf = new StringBuffer();

				Iterator people_itr = UKOnlinePersonBean.getPersonsByFirstName(adminCompany, parameter).iterator();
				while (people_itr.hasNext())
				{
					UKOnlinePersonBean person = (UKOnlinePersonBean)people_itr.next();
					buf.append(ScheduleServletPlastique.getXML(person));
				}

				//System.out.println("<clients>" + buf.toString() + "</clients>");

				writer.println("<clients>" + buf.toString() + "</clients>");
			}
			else if (command.equals("getPeopleByFileNumber"))
			{
				StringBuffer buf = new StringBuffer();

				Iterator people_itr = UKOnlinePersonBean.getPersonsByFileNumber(adminCompany, parameter).iterator();
				while (people_itr.hasNext())
				{
					UKOnlinePersonBean person = (UKOnlinePersonBean)people_itr.next();
					buf.append(ScheduleServletPlastique.getXML(person));
				}

				//System.out.println("<clients>" + buf.toString() + "</clients>");

				writer.println("<clients>" + buf.toString() + "</clients>");
			}
			else if (command.equals("getCheckoutCodesByDesc"))
			{
				StringBuffer buf = new StringBuffer();

				Iterator code_itr = CheckoutCodeBean.getCheckoutCodes(adminCompany, parameter).iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					//System.out.println("code >" + code.getLabel());
					if (code.isSynced() || !adminCompany.getQuickBooksSettings().isQuickBooksFSEnabled())
						buf.append(ScheduleServletPlastique.getXML(code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("getCheckoutCodesByItemNumber"))
			{
				StringBuffer buf = new StringBuffer();

				Iterator code_itr = CheckoutCodeBean.getCheckoutCodes(adminCompany, Integer.parseInt(parameter)).iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					buf.append(ScheduleServletPlastique.getXML(code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("nextAppt"))
			{
				// find the next appointment for the selected practitioner...
				
				StringTokenizer tokenizer = new StringTokenizer(parameter, "|");
				UKOnlinePersonBean practitioner = null;
				AppointmentTypeBean appointment_type = null;
				Calendar find_appt_after_date = Calendar.getInstance();
				short duration = 5;
				// Wednesday, September 17, 2008-9:00AM
				for (int i = 0; tokenizer.hasMoreTokens(); i++)
				{
					String token = (String)tokenizer.nextToken();
					if (i == 0)
						practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(token));
					else if (i == 1)
						appointment_type = AppointmentTypeBean.getAppointmentType(Integer.parseInt(token));
					else if (i == 2)
					{
						try
						{
							duration = Short.parseShort(token);
						}
						catch (Exception x)
						{
						}
					}
					else if (i == 3)
						find_appt_after_date.setTime(CUBean.getDateFromUserString(token, "EEEE, MMMM d, yyyy-h:mma"));
				}
				
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				AppointmentBean next_available_appt = AppointmentBean.getNextAvailableAppointmentTimeForPractitioner(adminCompany, practitioner, appointment_type, find_appt_after_date, duration);
				next_available_appt.setComments("nva");
				display_date.setTime(next_available_appt.getAppointmentDate());
				
				/*
				try
				{
					display_date.setTime(ScheduleServletPlastique.ie_date_format.parse(parameter));
				}
				catch (java.text.ParseException x)
				{
					display_date.setTime(ScheduleServletPlastique.ns_date_format.parse(parameter.substring(0, parameter.indexOf(':'))));
				}
				 */
				
				String date_str = ScheduleServletPlastique.date_format.format(display_date.getTime());
				String key = null;
				StringBuffer returnBuf = null;
				if (command.equals("nextAppt"))
				{
					returnBuf = this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true);
					key = session_str + "" + date_str;
				}
					
				
				String status_str = returnBuf.toString();
				int appt_index = status_str.indexOf("<appt>");
				String str1 = status_str.substring(0, appt_index);
				String str2 = status_str.substring(appt_index);
				String return_str = null;
				if (command.equals("nextAppt"))
					return_str = str1 + getXML(next_available_appt, session_str) + str2;
				else
					return_str = str1 + getPractitionerXML(next_available_appt) + str2;
				
				writer.println("<status>" + return_str + "</status>");

				ScheduleServletPlastique.needs_update_hash.put(session_str, new Boolean(false));
			}
			else if (command.equals("offTime"))
			{
				StringBuffer returnBuf = new StringBuffer();
				
				Calendar display_date = (Calendar)session.getAttribute("display_date");

				returnBuf.append("<date>" + ScheduleServletPlastique.date_format.format(display_date.getTime()) + "</date>");
				returnBuf.append("<schedule>");
				returnBuf.append(this.getOffTimeXML(adminCompany, display_date.getTime()));
				returnBuf.append("</schedule>");

				writer.println("<status>" + returnBuf.toString() + "</status>");
			}
			else if (command.equals("nextDayOffTime"))
			{
				StringBuffer returnBuf = new StringBuffer();
				
				Calendar display_date = (Calendar)session.getAttribute("display_date");

				display_date.add(Calendar.DATE, 1);

				returnBuf.append("<date>" + ScheduleServletPlastique.date_format.format(display_date.getTime()) + "</date>");
				returnBuf.append("<schedule>");
				returnBuf.append(this.getOffTimeXML(adminCompany, display_date.getTime()));
				returnBuf.append("</schedule>");

				writer.println("<status>" + returnBuf.toString() + "</status>");
			}
			else if (command.equals("previousDayOffTime"))
			{
				StringBuffer returnBuf = new StringBuffer();
				
				Calendar display_date = (Calendar)session.getAttribute("display_date");

				display_date.add(Calendar.DATE, -1);

				returnBuf.append("<date>" + ScheduleServletPlastique.date_format.format(display_date.getTime()) + "</date>");
				returnBuf.append("<schedule>");
				returnBuf.append(this.getOffTimeXML(adminCompany, display_date.getTime()));
				returnBuf.append("</schedule>");

				writer.println("<status>" + returnBuf.toString() + "</status>");
			}
			else if (command.equals("getPersonStats"))
			{
				UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(parameter));
				session.setAttribute("adminPerson", person);
				
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("checkin"))
			{
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));
				session.setAttribute("adminPerson", selected_appointment.getClient());
				
				boolean has_alert = false;

				StringBuffer buf = new StringBuffer();
				buf.append("<alerts>");
				Iterator itr = PersonNote.getPersonNotes(selected_appointment.getClient()).iterator();
				while (itr.hasNext())
				{
					PersonNote note_obj = (PersonNote)itr.next();
					if (note_obj.showOnCheckIn())
					{
						has_alert = true;
						buf.append(ScheduleServletPlastique.getXMLDetail(note_obj));
					}
				}
				buf.append("</alerts>");

				if (selected_appointment.isCheckedOut())
					throw new IllegalValueException(selected_appointment.getLabel() + " is already checked out.");
				if (selected_appointment.isRescheduled())
					throw new IllegalValueException(selected_appointment.getLabel() + " has already been rescheduled.");
				if (selected_appointment.isCancelled())
					throw new IllegalValueException(selected_appointment.getLabel() + " has already been cancelled.");

				selected_appointment.setState(AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
				selected_appointment.save();

				writer.println("<refresh>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, (Calendar)session.getAttribute("display_date"), session_str, false).toString() + (has_alert ? buf.toString() : "") + "</refresh>");
			}
			else if (command.equals("checkout"))
			{
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));

				if (selected_appointment.isCheckedOut())
					throw new IllegalValueException(selected_appointment.getLabel() + " is already checked out.");
				if (selected_appointment.isRescheduled())
					throw new IllegalValueException(selected_appointment.getLabel() + " has already been rescheduled.");
				if (selected_appointment.isCancelled())
					throw new IllegalValueException(selected_appointment.getLabel() + " has already been cancelled.");

				selected_appointment.setState(AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
				selected_appointment.save();

				writer.println("<refresh>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, (Calendar)session.getAttribute("display_date"), session_str, false).toString() + "</refresh>");
			}
			else if (command.equals("reschedule"))
			{
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));

				if (selected_appointment.isCheckedOut())
					throw new IllegalValueException(selected_appointment.getLabel() + " is already checked out.");
				if (selected_appointment.isCheckedIn())
					throw new IllegalValueException(selected_appointment.getLabel() + " is checked in.");
				if (selected_appointment.isRescheduled())
					throw new IllegalValueException(selected_appointment.getLabel() + " has already been rescheduled.");
				if (selected_appointment.isCancelled())
					throw new IllegalValueException(selected_appointment.getLabel() + " has already been cancelled.");

				selected_appointment.setState(AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS);
				selected_appointment.setIsFirstTimeAppointmentForClientInPracticeArea(false);
				selected_appointment.save();

				writer.println("<refresh>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, (Calendar)session.getAttribute("display_date"), session_str, false).toString() + "</refresh>");
			}
			else if (command.equals("cancel"))
			{
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));

				if (selected_appointment.isCheckedOut())
					throw new IllegalValueException(selected_appointment.getLabel() + " is already checked out.");
				if (selected_appointment.isCheckedIn())
					throw new IllegalValueException(selected_appointment.getLabel() + " is checked in.");
				if (selected_appointment.isRescheduled())
					throw new IllegalValueException(selected_appointment.getLabel() + " has already been rescheduled.");
				if (selected_appointment.isCancelled())
					throw new IllegalValueException(selected_appointment.getLabel() + " has already been cancelled.");

				selected_appointment.setState(AppointmentBean.CANCELLED_APPOINTMENT_STATUS);
				selected_appointment.setIsFirstTimeAppointmentForClientInPracticeArea(false);
				selected_appointment.save();

				writer.println("<refresh>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, (Calendar)session.getAttribute("display_date"), session_str, false).toString() + "</refresh>");
			}
			else if (command.equals("clear"))
			{
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));
				if (selected_appointment.getState() == AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS)
				{
					Iterator itr = selected_appointment.getChildren().iterator();
					if (itr.hasNext())
					{
						while (itr.hasNext())
						{
							AppointmentBean child_appt = (AppointmentBean)itr.next();
							if (child_appt.isCheckedOut())
								throw new IllegalValueException("Rescheduled appointment is already checked out.");
							if (child_appt.isCheckedIn())
								throw new IllegalValueException("Rescheduled appointment is checked in.");
							System.out.println("was going to delete appt >" + child_appt.getId());
							//AppointmentBean.delete(child_appt.getId());
						}

						last_update_hash.clear();
						if (selected_appointment.getComments().indexOf("Rescheduled") > -1)
							selected_appointment.setComments("");
					}
				}
				selected_appointment.setState(AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
				selected_appointment.save();

				writer.println("<refresh>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, (Calendar)session.getAttribute("display_date"), session_str, false).toString() + "</refresh>");
			}
			else if (command.equals("getCheckoutDetails") || command.equals("getCheckoutDetailsCID"))
			{
				session.removeAttribute("charge_response");

				System.out.println("");
				System.out.println("getCheckoutDetails -- ");

				UKOnlinePersonBean client;
				AppointmentBean selected_appointment = null;

				if (command.equals("getCheckoutDetails"))
				{
					selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));
					client = selected_appointment.getClient();
					session.setAttribute("adminAppointment", selected_appointment);
				}
				else
				{
					client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(parameter));
					session.removeAttribute("adminAppointment");
				}

				session.setAttribute("adminPerson", client);
				
				String group_under_care_str = "";
				try
				{
					GroupUnderCareBean group_under_care = client.getGroupUnderCare();
					group_under_care_str = ScheduleServletPlastique.getXML(group_under_care);
				}
				catch (ObjectNotFoundException x)
				{
				}
				
				StringBuffer open_orderlines_buf = new StringBuffer();
				open_orderlines_buf.append("<open-orderlines>");
				/*
				Iterator open_orderline_itr = client.getOpenOrders().iterator();
				while (open_orderline_itr.hasNext())
				{
					OrderBean open_order_obj = (OrderBean)open_orderline_itr.next();
					Iterator orderlines_itr = open_order_obj.getOrders();
					while (orderlines_itr.hasNext())
					{
						CheckoutOrderline orderline = (CheckoutOrderline)orderlines_itr.next();
						if (orderline.getOrderstatus().equals(OrderBean.OPEN_ORDER_STATUS))
							open_orderlines_buf.append(ScheduleServletPlastique.getXML(open_order_obj, orderline));
					}
				}
				 */
				open_orderlines_buf.append("</open-orderlines>");
				
				StringBuffer open_orders_buf = new StringBuffer();
				open_orders_buf.append("<open-orders>");
	
				Calendar date_range_for_open_orders = Calendar.getInstance();
				date_range_for_open_orders.add(Calendar.YEAR, -5);

				Vector orders_to_display_as_open_orders = client.getRecentOrders(adminCompany);
				Iterator open_orders_itr = client.getOpenOrders(date_range_for_open_orders.getTime()).iterator();
				while (open_orders_itr.hasNext())
				{
					ValeoOrderBean open_order_obj = (ValeoOrderBean)open_orders_itr.next();
					//open_orders_buf.append(ScheduleServletPlastique.getXML(open_order_obj));
					if (!orders_to_display_as_open_orders.contains(open_order_obj))
						orders_to_display_as_open_orders.addElement(open_order_obj);
				}

				Iterator itr = orders_to_display_as_open_orders.iterator();
				while (itr.hasNext())
				{
					ValeoOrderBean obj = (ValeoOrderBean)itr.next();
					open_orders_buf.append(ScheduleServletPlastique.getXML(obj));
				}
				
				if (client.hasQBFSListID())
				{
					itr = InvoiceRet.getOldOpenInvoices(client).iterator();
					while (itr.hasNext())
					{
						InvoiceRet invoice_obj = (InvoiceRet)itr.next();
						open_orders_buf.append(ScheduleServletPlastique.getXML(invoice_obj, adminCompany));
					}
				}

				open_orders_buf.append("</open-orders>");




				
				// get appropriate payment plan for the appointment...
				String payment_plan_str = "";

				/*
				try
				{
					PaymentPlanInstanceBean payment_plan_instance = client.getPlan(selected_appointment);
					payment_plan_str = ClientServlet.getDetailedXML(payment_plan_instance);
				}
				catch (ObjectNotFoundException x)
				{
					x.printStackTrace();
				}
				 */

				StringBuffer open_payments_buf = new StringBuffer();
				open_payments_buf.append("<open-payments>");
				Iterator open_payments_itr = QBMSCreditCardResponse.getUnusedAuthorizedResponsesForToday(client).iterator();
				while (open_payments_itr.hasNext())
				{
					QBMSCreditCardResponse auth_response = (QBMSCreditCardResponse)open_payments_itr.next();
					open_payments_buf.append(ScheduleServletPlastique.getXML(auth_response));
				}
				open_payments_buf.append("</open-payments>");
				
				
				StringBuffer shopping_cart_buf = new StringBuffer();
				if (ShoppingCartBean.hasCart(client)) {
					ShoppingCartBean cart = ShoppingCartBean.getCart(client);
					Iterator cartItr = cart.getCheckoutOrderlines();
					if (cartItr.hasNext()) {
						shopping_cart_buf.append("<shopping-cart>");
						while (cartItr.hasNext()) {
							CheckoutOrderline cartLine = (CheckoutOrderline)cartItr.next();
							shopping_cart_buf.append(ScheduleServletPlastique.getXML(cartLine));
						}
						shopping_cart_buf.append("</shopping-cart>");
					}
				}
				
				System.out.println("shopping_cart_buf >" + shopping_cart_buf.toString());
				

				String xml_str = "<checkout p=\"" + ((selected_appointment == null) ? 0 : selected_appointment.getPractitionerId()) + "\">" + ScheduleServletPlastique.getXMLIncludeFileType(client) + "<previous>" + client.getBalanceString() + "</previous>" + group_under_care_str + open_orderlines_buf.toString() + open_orders_buf.toString() + payment_plan_str + open_payments_buf.toString() + shopping_cart_buf.toString() + "</checkout>";
				System.out.println(xml_str);
				writer.println(xml_str);
			}
			else if (command.equals("addContactStatus"))
			{
				System.out.println("addContactStatus parameter >" + parameter);
				UKOnlineLoginBean loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				
				String status_str = "";

				/*
				if (parameter.equals("0"))
					status_str = "Scheduled New Appointment";
				else if (parameter.equals("1"))
					status_str = "Confirmed Next Appointment";
				else if (parameter.equals("2"))
					status_str = "No Answer / Busy";
				else if (parameter.equals("3"))
					status_str = "Sent Email";
				else if (parameter.equals("4"))
					status_str = "Left without future Appointment";
				else if (parameter.equals("5"))
					status_str = "Left Voice Mail";
				else if (parameter.equals("6"))
					status_str = "Left Message with Family Member";
				else if (parameter.equals("7"))
					status_str = "Requested Call Back";
				else
					status_str = parameter;
				 */


				String parameter_a = null;
				String parameter_b = null;

				PracticeAreaBean contact_practice_area = null;
				int contact_attempt = 0;

				int char_index = parameter.indexOf('~');
				if (char_index > -1)
				{
					String[] split_parameter = parameter.split("~");
					for (int i = 0; i < split_parameter.length; i++)
					{
						System.out.println("split_parameter >" + split_parameter[i]);
					}

					//parameter_a = parameter.substring(0, char_index);
					//parameter_b = parameter.substring(char_index + 1);

					parameter_a = split_parameter[0];
					parameter_b = split_parameter[1];
					if (!split_parameter[2].equals("0"))
						contact_practice_area = PracticeAreaBean.getPracticeArea(Integer.parseInt(split_parameter[2]));
					contact_attempt = Integer.parseInt(split_parameter[3]);
				}
				else
					parameter_a = parameter;

				if (parameter_a.equals("0"))
					status_str = "No Answer / Busy";
				else if (parameter_a.equals("1"))
					status_str = "Left Voice Mail";
				else if (parameter_a.equals("2"))
					status_str = "Left Message with Family Member";
				else if (parameter_a.equals("3"))
					status_str = "Requested Call Back";
				else if (parameter_a.equals("4"))
					status_str = "Sent Email";
				else
					status_str = parameter_a;


				Date toDoDate = null;

				if (parameter_b != null && !parameter_b.equals(""))
				{
					if (parameter_b.indexOf("|") == -1)
						toDoDate = CUBean.getDateFromUserString(parameter_b);
					else
					{
						Calendar now = Calendar.getInstance();
						int value = Integer.parseInt(parameter_b.substring(0, parameter_b.indexOf("|")));
						int period = Integer.parseInt(parameter_b.substring(parameter_b.indexOf("|") + 1));
						switch (period)
						{
							case 1: now.add(Calendar.DATE, value); break;
							case 2: now.add(Calendar.WEEK_OF_YEAR, value); break;
							case 3: now.add(Calendar.MONTH, value); break;
							case 4: now.add(Calendar.YEAR, value); break;
						}
						toDoDate = now.getTime();
					}
				}
				
				ContactStatusBean status = new ContactStatusBean();
				status.setAdminPerson((UKOnlinePersonBean)loginBean.getPerson());
				status.setCompany(adminCompany);
				status.setContactDate(new Date());
				if (toDoDate != null)
					status.setToDoDate(toDoDate);
				status.setPerson(person);
				status.setStatus(status_str);
				if (contact_practice_area != null)
					status.setPracticeArea(contact_practice_area);
				status.setContactAttempt(contact_attempt);
				status.save();
				
				if (contact_attempt == 4) // being placed on the practitioner call list; inactivate person
				{
					if (person.isActive())
					{
						person.setActive(false);
						person.save();
					}
				}
				
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));

				// update this person's black hole status to reflect the change in care details....

				BlackHoleBean black_hole = BlackHoleBean.getInstance(adminCompany);
				if (person.updateBlackHoleStatus(black_hole))
					black_hole.save();

			}
			else if (command.equals("deleteContactStatus"))
			{
				ContactStatusBean.delete(Integer.parseInt(parameter));
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));

				BlackHoleBean black_hole = BlackHoleBean.getInstance(adminCompany);
				if (person.updateBlackHoleStatus(black_hole))
					black_hole.save();
			}
			else if (command.equals("addToDo"))
			{
				UKOnlineLoginBean loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				
				Date toDoDate = null;
				if (parameter.indexOf("|") == -1)
					toDoDate = CUBean.getDateFromUserString(parameter);
				else
				{
					Calendar now = Calendar.getInstance();
					int value = Integer.parseInt(parameter.substring(0, parameter.indexOf("|")));
					int period = Integer.parseInt(parameter.substring(parameter.indexOf("|") + 1));
					switch (period)
					{
						case 1: now.add(Calendar.DATE, value); break;
						case 2: now.add(Calendar.WEEK_OF_YEAR, value); break;
						case 3: now.add(Calendar.MONTH, value); break;
						case 4: now.add(Calendar.YEAR, value); break;
					}
					toDoDate = now.getTime();
				}
				
				ToDoItemBean item = new ToDoItemBean();
				item.setAssignPerson((UKOnlinePersonBean)loginBean.getPerson());
				item.setPerson(person);
				item.setCompany(adminCompany);
				item.setToDoDate(toDoDate);
				item.setToDoText("Contact " + person.getLabel());
				item.save();
				
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("addClientReview"))
			{
				System.out.println("addClientReview >" + parameter);

				UKOnlineLoginBean loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				
				ClientReviewReason client_review_reason = null;

				if (parameter.startsWith("|"))
					client_review_reason = new ClientReviewReason();
				else
					client_review_reason = ClientReviewReason.getClientReviewReason(Integer.parseInt(parameter.substring(0, parameter.indexOf("|"))));

				parameter = parameter.substring(parameter.indexOf("|") + 1);
				
				ReviewReason review_reason = ReviewReason.getReviewReason(Integer.parseInt(parameter.substring(0, parameter.indexOf("|"))));
				String parse_str = parameter.substring(parameter.indexOf("|") + 1);
				//System.out.println("parse_str(1) >" + parse_str);
				int practitioner_id = Integer.parseInt(parse_str.substring(0, parse_str.indexOf("|")));
				parse_str = parse_str.substring(parse_str.indexOf("|") + 1);
				//System.out.println("parse_str(2) >" + parse_str);

				String note = parse_str.substring(parse_str.lastIndexOf('|') + 1);
				System.out.println("note >" + note);

				parse_str = parse_str.substring(0, parse_str.lastIndexOf('|'));

				String contact_str = parse_str.substring(parse_str.lastIndexOf('|') + 1);
				parse_str = parse_str.substring(0, parse_str.lastIndexOf('|'));

				Date reviewDate = null;
				if (parse_str.indexOf("|") == -1)
					reviewDate = CUBean.getDateFromUserString(parse_str);
				else
				{
					Calendar now = Calendar.getInstance();
					int value = Integer.parseInt(parse_str.substring(0, parse_str.indexOf("|")));
					int period = Integer.parseInt(parse_str.substring(parse_str.indexOf("|") + 1));
					switch (period)
					{
						case 1: now.add(Calendar.DATE, value); break;
						case 2: now.add(Calendar.WEEK_OF_YEAR, value); break;
						case 3: now.add(Calendar.MONTH, value); break;
						case 4: now.add(Calendar.YEAR, value); break;
					}
					reviewDate = now.getTime();
				}

				if (!contact_str.equals("undefined") && !contact_str.equals("-1"))
				{
					try
					{
						int contact_value = Integer.parseInt(contact_str);
						switch (contact_value)
						{
							case 0: contact_str = "Left Voice Mail"; break;
							case 1: contact_str = "Requested Call Back"; break;
							case 2: contact_str = "Sent Email"; break;
						}
					}
					catch (NumberFormatException x)
					{
					}

					if (note != null && note.length() > 0)
						note += ", " + contact_str;
					else
						note = contact_str;
				}

				
				client_review_reason.setPerson(person);
				client_review_reason.setReviewDate(reviewDate);
				client_review_reason.setReviewReason(review_reason);
				client_review_reason.setNote(note);
				if (practitioner_id > 0)
					client_review_reason.setPractitioner((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(practitioner_id));
				client_review_reason.save();

				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("deleteClientReview"))
			{
				ClientReviewReason.delete(Integer.parseInt(parameter));
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("completeClientReview"))
			{
				ClientReviewReason client_review_reason = ClientReviewReason.getClientReviewReason(Integer.parseInt(parameter));
				client_review_reason.setReviewed(true);
				client_review_reason.save();
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("deleteToDo"))
			{
				ToDoItemBean.delete(Integer.parseInt(parameter));
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("addCareDetails"))
			{
				UKOnlineLoginBean loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				
				PracticeAreaBean practice_area = null;
				UKOnlinePersonBean preferred_practitioner = null;
				int foq_value = -1;
				int foq_period = -1;
				int foq_period_rec = -1;
				boolean is_prn = false;

				System.out.println("parameter >" + parameter);
				
				StringTokenizer tokenizer = new StringTokenizer(parameter, "|");
				for (int i = 0; tokenizer.hasMoreTokens(); i++)
				{
					String token = (String)tokenizer.nextToken();
					if (i == 0)
						practice_area = PracticeAreaBean.getPracticeArea(Integer.parseInt(token));
					else if (i == 1)
						preferred_practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(token));
					else if (i == 2)
						foq_value = Integer.parseInt(token);
					else if (i == 3)
						foq_period = Integer.parseInt(token);
					else if (i == 4)
						foq_period_rec = Integer.parseInt(token);
					else if (i == 5)
						is_prn = token.equals("on");
				}
				
				// see if there's an existing foq care detail for this user/practice area
				
				CareDetailsBean care_details = null;
				
				try
				{
					care_details = CareDetailsBean.getCareDetails(person, practice_area);
				}
				catch (ObjectNotFoundException x)
				{
					care_details = new CareDetailsBean();
					care_details.setCompany(adminCompany);
					care_details.setPerson(person);
					care_details.setPracticeArea(practice_area);
				}
				
				care_details.setCreateOrModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
				care_details.setPreferredPractioner(preferred_practitioner);
				care_details.setFrequencyOfCarePeriod(foq_period);
				care_details.setFrequencyOfCarePeriodRecurrence(foq_period_rec);
				care_details.setFrequencyOfCareValue(foq_value);
				care_details.setPRN(is_prn);
				care_details.save();
				
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));

				// update this person's black hole status to reflect the change in care details....

				BlackHoleBean black_hole = BlackHoleBean.getInstance(adminCompany);
				if (person.updateBlackHoleStatus(black_hole))
					black_hole.save();
			}
			else if (command.equals("deleteCareDetails"))
			{
				int care_details_id = Integer.parseInt(parameter);
				CareDetailsBean care_details = CareDetailsBean.getCareDetails(care_details_id);
				PracticeAreaBean practice_area = care_details.getPracticeArea();
				CareDetailsBean.delete(care_details_id);
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));

				BlackHoleBean black_hole = BlackHoleBean.getInstance(adminCompany);
				black_hole.remove(person, practice_area);
				person.updateBlackHoleStatus(black_hole);
				black_hole.save();
			}
			else if (command.equals("cut"))
			{
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));
				session.setAttribute("cut_appt", selected_appointment);
			}
			else if (command.equals("paste"))
			{
				AppointmentBean selected_appointment = (AppointmentBean)session.getAttribute("cut_appt");
				if (selected_appointment != null)
				{
					if (selected_appointment.isCheckedOut())
						throw new IllegalValueException(selected_appointment.getLabel() + " is already checked out.");
					if (selected_appointment.isCheckedIn())
						throw new IllegalValueException(selected_appointment.getLabel() + " is already checked in.");

					Date appointment_date = selected_appointment.getAppointmentDate();
					int practitioner_id = 0;
					try
					{
						practitioner_id = selected_appointment.getPractitioner().getId();
					}
					catch (Exception x)
					{
					}
					int practice_area_id = 0;
					try
					{
						practice_area_id = selected_appointment.getPracticeAreaId();
					}
					catch (Exception x)
					{
					}
					
					last_update_hash.clear();

					String key1 = adminCompany.getId() + "|" + CUBean.getUserDateString(appointment_date);
					String key2 = adminCompany.getId() + "|" + practice_area_id + "|" + CUBean.getUserDateString(appointment_date);

					Calendar start_of_week = Calendar.getInstance();
					start_of_week.setTime(appointment_date);
					Calendar end_of_week = Calendar.getInstance();
					end_of_week.setTime(appointment_date);

					start_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
					end_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

					String key3 = adminCompany.getId() + "|" + practitioner_id + "|" + CUBean.getUserDateString(start_of_week.getTime()) + "|" + CUBean.getUserDateString(end_of_week.getTime());

					AppointmentBean.appointment_hash.remove(key1);
					AppointmentBean.appointment_hash.remove(key2);
					AppointmentBean.appointment_hash.remove(key3);

					session.removeAttribute("cut_appt");
					
					Date original_date = selected_appointment.getAppointmentDate();
					UKOnlinePersonBean orig_practitioner = selected_appointment.getPractitioner();

					String[] key_arr = parameter.split("\\|");

					Calendar display_date = (Calendar)session.getAttribute("display_date");
					Calendar paste_date = Calendar.getInstance();
					paste_date.setTime(display_date.getTime());

					SimpleDateFormat key_date_format = new SimpleDateFormat("hh:mm aa");
					Calendar key_date = Calendar.getInstance();
					key_date.setTime(key_date_format.parse(key_arr[0]));
					paste_date.set(Calendar.AM_PM, key_date.get(Calendar.AM_PM));
					paste_date.set(Calendar.HOUR, key_date.get(Calendar.HOUR));
					paste_date.set(Calendar.MINUTE, key_date.get(Calendar.MINUTE));

					UKOnlinePersonBean practitioner = null;
					if (command.equals("paste"))
						practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(key_arr[1]));
					else
						paste_date.set(Calendar.DAY_OF_WEEK, Integer.parseInt(key_arr[1]));

					try
					{
						selected_appointment.setAppointmentDate(paste_date.getTime());
						if (practitioner != null)
							selected_appointment.setPractitioner(practitioner);
						Calendar now = Calendar.getInstance();
						if (paste_date.after(now))
							selected_appointment.setState(AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
						selected_appointment.save();
					}
					catch (Exception iv_x)
					{
						selected_appointment.setPractitioner(orig_practitioner);
						selected_appointment.setAppointmentDate(original_date);
						throw iv_x;
					}

					if (command.equals("paste"))
					{
						StringBuffer returnBuf = this.assembleScheduleXML(adminCompany, adminPracticeArea, paste_date, session_str, true);
						writer.println("<status>" + returnBuf.toString() + "</status>");
					}
				}
			}
			else if (command.equals("delete"))
			{
				AppointmentBean delete_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));
				Date appointment_date = delete_appointment.getAppointmentDate();
				int practitioner_id = 0;
				try
				{
					practitioner_id = delete_appointment.getPractitioner().getId();
				}
				catch (Exception x)
				{
				}
				int practice_area_id = 0;
				try
				{
					practice_area_id = delete_appointment.getPracticeAreaId();
				}
				catch (Exception x)
				{
				}

				AppointmentBean.delete(Integer.parseInt(parameter));
				last_update_hash.clear();

				String key1 = adminCompany.getId() + "|" + CUBean.getUserDateString(appointment_date);
				String key2 = adminCompany.getId() + "|" + practice_area_id + "|" + CUBean.getUserDateString(appointment_date);

				Calendar start_of_week = Calendar.getInstance();
				start_of_week.setTime(appointment_date);
				Calendar end_of_week = Calendar.getInstance();
				end_of_week.setTime(appointment_date);

				start_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				end_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

				String key3 = adminCompany.getId() + "|" + practitioner_id + "|" + CUBean.getUserDateString(start_of_week.getTime()) + "|" + CUBean.getUserDateString(end_of_week.getTime());

				AppointmentBean.appointment_hash.remove(key1);
				AppointmentBean.appointment_hash.remove(key2);
				AppointmentBean.appointment_hash.remove(key3);

				StringBuffer returnBuf;
				Calendar display_date = (Calendar)session.getAttribute("display_date");

				returnBuf = this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true);

				String date_str = ScheduleServletPlastique.date_format.format(display_date.getTime());


				writer.println("<status>" + returnBuf.toString() + "</status>");

				Date update_timestamp = new Date();
				AppointmentBean.appointment_update_hash.put(key1, update_timestamp);
				AppointmentBean.appointment_update_hash.put(key2, update_timestamp);
				AppointmentBean.appointment_update_hash.put(key3, update_timestamp);

			}
			else if (command.equals("apptReport"))
			{
				System.out.println("apptReport >" + parameter);

				String p1 = null;
				String p2 = null;
				String p3 = null;

				StringTokenizer tokenizer = new StringTokenizer(parameter, "|");
				while (tokenizer.hasMoreTokens())
				{
					if (p1 == null)
						p1 = tokenizer.nextToken();
					else if (p2 == null)
						p2 = tokenizer.nextToken();
					else if (p3 == null)
						p3 = tokenizer.nextToken();
				}

				System.out.println("p1 >" + p1);
				System.out.println("p2 >" + p2);
				System.out.println("p3 >" + p3);

				UKOnlinePersonBean client = null;
				if (p1.startsWith("c"))
					client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(p1.substring(1)));
				else
				{
					AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(p1));
					client = selected_appointment.getClient();
				}

				Date start_date = null;
				Date end_date = null;

				try
				{
					if (p2 != null && p2.length() > 0)
					{
						start_date = CUBean.getDateFromUserString(p2);
					}
				}
				catch (Exception x)
				{
				}

				try
				{
					if (p3 != null && p3.length() > 0)
					{
						end_date = CUBean.getDateFromUserString(p3);
					}
				}
				catch (Exception x)
				{
				}

				String appointment_report_url = "../resources/pdf/" + ClientAppointmentBuilder.generateClientAppointmentWorksheet(adminCompany, client, start_date, end_date);
				writer.println("<appt_report_url><![CDATA[" + appointment_report_url + "]]></appt_report_url>");
			}
			else if (command.equals("checkOutSheet"))
			{
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));
				UKOnlinePersonBean client = selected_appointment.getClient();
				String checkout_sheet_url = "../resources/pdf/" + CheckoutSheetBuilder.generateCheckoutSheet(adminCompany, client);
				writer.println("<checkout_sheet_url><![CDATA[" + checkout_sheet_url + "]]></checkout_sheet_url>");
			}
			else if (command.equals("pracReport"))
			{
				//AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));
				//UKOnlinePersonBean client = selected_appointment.getClient();
				
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				Calendar report_date = Calendar.getInstance();
				report_date.setTime(display_date.getTime());
				report_date.set(Calendar.DAY_OF_WEEK, Integer.parseInt(parameter));
				
				String practitioner_report_url = "../resources/pdf/" + PractitionerAppointmentsBuilder.generatePractitionerAppointmentWorksheet(adminCompany, adminPractitioner, report_date.getTime());
				writer.println("<prac_report_url><![CDATA[" + practitioner_report_url + "]]></prac_report_url>");
			}
			else if (command.equals("salesReceipt"))
			{
				// how am I going to get the order(s) and tender(s) that I need to print on this sales receipt

				ValeoOrderBean adminOrder = (ValeoOrderBean)session.getAttribute("adminOrder");
				Vector previous_orders = (Vector<ValeoOrderBean>)session.getAttribute("previous_orders");
				Vector tenders = (Vector<TenderRet>)session.getAttribute("tenders");

				BigDecimal subtotal = (BigDecimal)session.getAttribute("h_subtotal");
				BigDecimal tax = (BigDecimal)session.getAttribute("h_tax");
				BigDecimal total = (BigDecimal)session.getAttribute("h_total");
				
				BigDecimal total_discount = (BigDecimal)session.getAttribute("h_total_discount");
				BigDecimal discount_perc = (BigDecimal)session.getAttribute("h_discount_percentage");
				
				BigDecimal shipping = (BigDecimal)session.getAttribute("h_shipping");


				//UKOnlinePersonBean client = UKOnlinePersonBean.getPerson(adminCompany, adminOrder.getPersonId());
				UKOnlinePersonBean client = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				String sales_receipt_url = "../resources/pdf/" + SalesReceiptBuilder.generateSalesReceipt(adminCompany, (UKOnlinePersonBean)adminLoginBean.getPerson(), client, adminOrder, previous_orders, tenders, subtotal, tax, total, total_discount, discount_perc, shipping);
				writer.println("<sales_receipt_url><![CDATA[" + sales_receipt_url + "]]></sales_receipt_url>");

				session.removeAttribute("adminOrder");
				session.removeAttribute("previous_orders");
				session.removeAttribute("tenders");

				session.removeAttribute("h_subtotal");
				session.removeAttribute("h_tax");
				session.removeAttribute("h_total");
			}
			else if (command.equals("lastReceipt"))
			{
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));
				UKOnlinePersonBean client = selected_appointment.getClient();

				PrintedReceipt printed_receipt = PrintedReceipt.getLastCompletedPrintedReceipt(client);
				ValeoOrderBean primary_order;
				try
				{
					primary_order = printed_receipt.getPrimaryOrder();
				}
				catch (ObjectNotFoundException x)
				{
					primary_order = new ValeoOrderBean();
				}
				Vector previous_orders = printed_receipt.getOrders();
				Vector tenders = printed_receipt.getTenders();

				BigDecimal subtotal = printed_receipt.getSubTotal();
				BigDecimal tax = printed_receipt.getTax();
				BigDecimal total = printed_receipt.getTotal();

				BigDecimal total_discount = printed_receipt.getDiscount();
				BigDecimal discount_perc = printed_receipt.getDiscountPercentage();
				
				BigDecimal shipping = printed_receipt.getShipping();

				//ValeoOrderBean adminOrder = (ValeoOrderBean)session.getAttribute("adminOrder");
				//Vector previous_orders = (Vector<ValeoOrderBean>)session.getAttribute("previous_orders");
				//Vector tenders = (Vector<TenderRet>)session.getAttribute("tenders");

				//BigDecimal subtotal = (BigDecimal)session.getAttribute("h_subtotal");
				//BigDecimal tax = (BigDecimal)session.getAttribute("h_tax");
				//BigDecimal total = (BigDecimal)session.getAttribute("h_total");

				String sales_receipt_url = "../resources/pdf/" + SalesReceiptBuilder.generateSalesReceipt(adminCompany, (UKOnlinePersonBean)adminLoginBean.getPerson(), client, primary_order, previous_orders, tenders, subtotal, tax, total, total_discount, discount_perc, shipping);
				writer.println("<sales_receipt_url><![CDATA[" + sales_receipt_url + "]]></sales_receipt_url>");
			}
			else if (command.equals("blackHole"))
			{
				BlackHoleBean black_hole = BlackHoleBean.getInstance(adminCompany);

				Vector members = null;
				if (parameter.equals("undefined") || parameter.equals("0"))
					members = black_hole.getMembers();
				else
				{
					PracticeAreaBean bh_pa = PracticeAreaBean.getPracticeArea(Integer.parseInt(parameter));
					members = black_hole.getMembers(bh_pa);
				}

				StringBuffer returnBuf = new StringBuffer();
				returnBuf.append("<black_hole num=\"" + members.size() + "\">");

				try
				{
					Iterator members_itr = members.iterator();
					while (members_itr.hasNext())
					{
						BlackHoleMemberBean member_obj = (BlackHoleMemberBean)members_itr.next();
						//System.out.println("MEMBER FOUND >" + member_obj.getLabel());
						returnBuf.append(ScheduleServletPlastique.getXML(member_obj));
					}
				}
				catch (java.util.ConcurrentModificationException x)
				{
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "java.util.ConcurrentModificationException", x.getMessage());
				}

				returnBuf.append("</black_hole>");

				//System.out.println(returnBuf.toString());
				writer.println(returnBuf.toString());
			}
			else if (command.equals("clientReview"))
			{
				System.out.println("cr paramter = " + parameter);

				Vector items = null;
				if (parameter.equals("undefined") || parameter.equals("0"))
					items = new Vector();
				else
				{
					ReviewReason review_reason = ReviewReason.getReviewReason(Integer.parseInt(parameter));
					items = ClientReviewReason.getClientReviewReasons(adminCompany, review_reason);
				}

				StringBuffer returnBuf = new StringBuffer();
				returnBuf.append("<client_review num=\"" + items.size() + "\">");

				Iterator items_itr = items.iterator();
				while (items_itr.hasNext())
				{
					ClientReviewReason obj = (ClientReviewReason)items_itr.next();
					//System.out.println("MEMBER FOUND >" + obj.getLabel());
					returnBuf.append(ScheduleServletPlastique.getXMLWithClientInfo(obj));
				}

				returnBuf.append("</client_review>");

				//System.out.println(returnBuf.toString());
				writer.println(returnBuf.toString());
			}
			else if (command.equals("pracList"))
			{
				System.out.println("pracList paramter = " + parameter);

				Vector call_list = null;
				if (parameter.equals("undefined") || parameter.equals("0"))
					call_list = ContactStatusBean.getPractitionerCallList(adminCompany);
				else
				{
					//PracticeAreaBean pl_pa = PracticeAreaBean.getPracticeArea(Integer.parseInt(parameter));
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(parameter));
					call_list = ContactStatusBean.getPractitionerCallList(adminCompany, practitioner);
				}

				StringBuffer returnBuf = new StringBuffer();
				returnBuf.append("<call_list num=\"" + call_list.size() + "\">");

				try
				{
					Iterator call_list_itr = call_list.iterator();
					while (call_list_itr.hasNext())
					{
						ContactStatusBean contact_obj = (ContactStatusBean)call_list_itr.next();
						returnBuf.append(ScheduleServletPlastique.getXMLWithClientInfo(contact_obj));
					}
				}
				catch (java.util.ConcurrentModificationException x)
				{
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "java.util.ConcurrentModificationException", x.getMessage());
				}

				returnBuf.append("</call_list>");

				//System.out.println(returnBuf.toString());
				writer.println(returnBuf.toString());
			}
			else if (command.equals("nextFileNumber"))
			{
				StringBuffer returnBuf = new StringBuffer();
				returnBuf.append("<nfn num=\"" + UKOnlinePersonBean.getNextFileNumber(adminCompany) + "\"></nfn>");

				//System.out.println(returnBuf.toString());
				writer.println(returnBuf.toString());
			}
			else if (command.equals("gotoLastAppt") || command.equals("gotoNextAppt"))
			{
				UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(parameter));
				session.setAttribute("adminPerson", person);

				AppointmentBean appt = null;
				if (command.equals("gotoNextAppt"))
					appt = AppointmentBean.getNextAppointmentForClient(person);
				else
					appt = AppointmentBean.getLastAppointmentForClient(person);
				
				if (appt != null)
				{
					Calendar display_date = (Calendar)session.getAttribute("display_date");
					display_date.setTime(appt.getAppointmentDate());

					StringBuffer returnBuf = this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, session_str, true);

					String ret_str = returnBuf.toString();
					// "id" : "xx",
					
					String search_str = "\"id\" : \"" + appt.getId() + "\",";
					int search_index = ret_str.indexOf(search_str);
					if (search_index > -1)
					{
						String insert_str = "\"f\" : \"1\",";
						ret_str = ret_str.substring(0, search_index) + insert_str + ret_str.substring(search_index);
					}

					writer.println("<status>" + ret_str + "</status>");

				}

			}
			else if (command.equals("saveNote"))
			{
				System.out.println("saveNote invoked >" + parameter);
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");

				String id_string = "";
				String check_string = "";
				String subject_string = "";
				String body_string = "";

				StringTokenizer tokenizer = new StringTokenizer(parameter, "|");
				if (tokenizer.hasMoreTokens())
					id_string = ((String)tokenizer.nextToken()).trim();
				if (tokenizer.hasMoreTokens())
					check_string = ((String)tokenizer.nextToken()).trim();
				if (tokenizer.hasMoreTokens())
					subject_string = ((String)tokenizer.nextToken()).trim();
				body_string = parameter.substring(parameter.lastIndexOf('|') + 1);

				if (subject_string.equals(""))
					throw new IllegalValueException("Please enter a subject.");
				if (body_string.equals(""))
					throw new IllegalValueException("Please enter a note.");

				PersonNote note_obj = null;
				if (id_string.equals(""))
					note_obj = new PersonNote();
				else
					note_obj = PersonNote.getPersonNote(Integer.parseInt(id_string));
				note_obj.setShowOnCheckIn(!check_string.equals(""));
				note_obj.setNote(body_string);
				note_obj.setPerson(person);
				note_obj.setSubject(subject_string);
				note_obj.save();

				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("saveSoap"))
			{
				//System.out.println("saveSoap invoked >" + parameter);

				String id_string = "";
				String pa_string = "";
				String s_note_string = "";
				String o_note_string = "";
				String a_note_string = "";
				String p_note_string = "";

				StringTokenizer tokenizer = new StringTokenizer(parameter, "|");
				if (tokenizer.hasMoreTokens())
					id_string = ((String)tokenizer.nextToken()).trim();
				if (tokenizer.hasMoreTokens())
					pa_string = ((String)tokenizer.nextToken()).trim();
				if (tokenizer.hasMoreTokens())
					s_note_string = ((String)tokenizer.nextToken()).trim();
				if (tokenizer.hasMoreTokens())
					o_note_string = ((String)tokenizer.nextToken()).trim();
				if (tokenizer.hasMoreTokens())
					a_note_string = ((String)tokenizer.nextToken()).trim();
				if (tokenizer.hasMoreTokens())
					p_note_string = ((String)tokenizer.nextToken()).trim();

				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				if (person == null)
					throw new IllegalValueException("No person selected.");

				SOAPNotesBean note = new SOAPNotesBean();
				if (id_string.equals("") || id_string.equals("-1"))
				{
					note = new SOAPNotesBean();
					note.setAnalysisDate(new Date());
				}
				else
					note = SOAPNotesBean.getSOAPNotes(Integer.parseInt(id_string));
				
				note.setPerson(person);
				note.setPracticeArea(PracticeAreaBean.getPracticeArea(Integer.parseInt(pa_string)));
				note.setSNote(s_note_string);
				note.setONote(o_note_string);
				note.setANote(a_note_string);
				note.setPNote(p_note_string);
				note.setCreateOrModifyPerson((UKOnlinePersonBean)adminLoginBean.getPerson());
				boolean is_new_note = note.isNew();
				note.save();

				try
				{
					Iterator itr = CareDetailsBean.getCareDetails(person).iterator();
					if (itr.hasNext())
					{
						String message = "S : " + note.getSNoteString() + "\r\n\r\nO : " + note.getONoteString() + "\r\n\r\nA : " + note.getANoteString() + "\r\n\r\nP : " + note.getPNoteString();
						while (itr.hasNext())
						{
							CareDetailsBean care_details = (CareDetailsBean)itr.next();
							CUBean.sendEmail(care_details.getPreferredPractitioner().getEmailString(), CUBean.getProperty("cu.adminEmail"), (is_new_note ? "New" : "Updated") + " SOAP Note for " + person.getLabel(), message);
						}
					}
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}

				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("saveSoapS"))
			{	
				SOAPNotesBean note = (SOAPNotesBean)session.getAttribute("soapNote");
				note.setSNote(parameter.trim());
				//note.setCreateOrModifyPerson((UKOnlinePersonBean)adminLoginBean.getPerson());
				note.save();
				
				//writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("saveSoapO"))
			{
				SOAPNotesBean note = (SOAPNotesBean)session.getAttribute("soapNote");
				note.setONote(parameter.trim());
				note.save();
			}
			else if (command.equals("saveSoapA"))
			{
				SOAPNotesBean note = (SOAPNotesBean)session.getAttribute("soapNote");
				note.setANote(parameter.trim());
				note.save();
			}
			else if (command.equals("saveSoapP"))
			{
				SOAPNotesBean note = (SOAPNotesBean)session.getAttribute("soapNote");
				note.setPNote(parameter.trim());
				note.save();
			}
			else if (command.equals("showNote"))
			{
				PersonNote note_obj = PersonNote.getPersonNote(Integer.parseInt(parameter));
				writer.println("<note>" + ScheduleServletPlastique.getXMLDetail(note_obj) + "</note>");
			}
			else if (command.equals("showSoap"))
			{
				SOAPNotesBean note_obj = SOAPNotesBean.getSOAPNotes(Integer.parseInt(parameter));
				writer.println("<soap>" + ScheduleServletPlastique.getXMLDetail(note_obj) + "</soap>");
			}
			else if (command.equals("deleteSoap"))
			{
				SOAPNotesBean.delete(Integer.parseInt(parameter));
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("printSoap"))
			{
				SOAPNotesBean note = SOAPNotesBean.getSOAPNotes(Integer.parseInt(parameter));
				String soap_report_url = "../resources/pdf/" + SOAPNoteReportBuilder.generateSalesReceipt(adminCompany, note.getPerson(), note.getCreatePerson(), note.getCreationDate(), note);
				writer.println("<soap_report_url><![CDATA[" + soap_report_url + "]]></soap_report_url>");
			}
			else if (command.equals("saveHistory"))
			{
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				if (person == null)
					throw new IllegalValueException("No person selected.");

				HealthHistoryBean history = HealthHistoryBean.getHealthHistory(person, true);
				history.setHistory(parameter);
				history.setCreateOrModifyPerson((UKOnlinePersonBean)adminLoginBean.getPerson());
				history.save();

				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			/*
			else if (command.equals("showAlert"))
			{
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				boolean has_alert = false;

				StringBuffer buf = new StringBuffer();
				buf.append("<alerts>");
				Iterator itr = PersonNote.getPersonNotes(person).iterator();
				while (itr.hasNext())
				{
					PersonNote note_obj = (PersonNote)itr.next();
					if (note_obj.showOnCheckIn())
					{
						has_alert = true;
						buf.append(ScheduleServletPlastique.getXMLDetail(note_obj));
					}
				}
				buf.append("</alerts>");

				if (has_alert)
					writer.println(buf.toString());
			}
			 */
			else if (command.equals("emailNote"))
			{
				System.out.println("emailNote invoked >" + parameter);

				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");

				String to_email = person.getEmail1String();
				if (to_email.equals(""))
					throw new IllegalValueException("No email address found for " + person.getLabel());

				String from_email = adminLoginBean.getPerson().getEmail1String();
				if (from_email.equals(""))
					throw new IllegalValueException("No email address found for " + adminLoginBean.getPerson().getLabel());

				String id_string = "";
				String check_string = "";
				String subject_string = "";
				String body_string = "";

				StringTokenizer tokenizer = new StringTokenizer(parameter, "|");
				if (tokenizer.hasMoreTokens())
					id_string = ((String)tokenizer.nextToken()).trim();
				if (tokenizer.hasMoreTokens())
					check_string = ((String)tokenizer.nextToken()).trim();
				if (tokenizer.hasMoreTokens())
					subject_string = ((String)tokenizer.nextToken()).trim();
				body_string = parameter.substring(parameter.lastIndexOf('|') + 1);

				if (subject_string.equals(""))
					throw new IllegalValueException("Please enter a subject.");
				if (body_string.equals(""))
					throw new IllegalValueException("Please enter a note.");

				PersonNote note_obj = null;
				if (id_string.equals(""))
					note_obj = new PersonNote();
				else
					note_obj = PersonNote.getPersonNote(Integer.parseInt(id_string));
				note_obj.setShowOnCheckIn(!check_string.equals(""));
				note_obj.setNote(body_string);
				note_obj.setPerson(person);
				note_obj.setSubject(subject_string);
				note_obj.setEmailDate(new Date());
				note_obj.save();

				// get the email address of the logged in person

				CUBean.sendEmail(to_email, from_email, subject_string, body_string);
				CUBean.sendEmail(from_email, from_email, subject_string, body_string);
				
				if (from_email.equals("cstueve@sanowc.com"))
					CUBean.sendEmail("mstewart@sanowc.com", from_email, subject_string, body_string);
				else if (from_email.equals("mstewart@sanowc.com"))
					CUBean.sendEmail("cstueve@sanowc.com", from_email, subject_string, body_string);
				
				//CUBean.sendEmail("tbrown@valeowc.com", from_email, subject_string, body_string);
				CUBean.sendEmail("marlo@valeowc.com", from_email, subject_string, body_string);

				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("deleteNote"))
			{
				PersonNote.delete(Integer.parseInt(parameter));
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				writer.println(ScheduleServletPlastique.getStatsXML(person, adminCompany));
			}
			else if (command.equals("soapSelect"))
			{
				System.out.println("soapSelect invoked >" + parameter);
				//session.setAttribute("soapSelect", parameter);

				// show statements in the box apropos to this selection
				
				StringBuffer buf = new StringBuffer();

				int pa_id = Integer.parseInt(parameter.substring(0, parameter.indexOf("|")));
				if (pa_id > 0)
				{
					PracticeAreaBean practice_area = PracticeAreaBean.getPracticeArea(pa_id);

					Iterator itr = SOAPNotesBean.getStatements(adminCompany, practice_area, parameter.substring(parameter.indexOf("|") + 1)).iterator();
					while (itr.hasNext())
					{
						SoapStatement statement = (SoapStatement)itr.next();
						System.out.println("statement >" + statement.getStatement());
						buf.append(ScheduleServletPlastique.getXML(statement));
					}
				}

				writer.println("<statements>" + buf.toString() + "</statements>");
			}
			else if (command.equals("statementSelect"))
			{
				System.out.println("statementSelect invoked >" + parameter);
				//session.setAttribute("soapSelect", parameter);

				// show statements in the box apropos to this selection

				StringBuffer buf = new StringBuffer();

				Iterator itr = SOAPNotesBean.getStatements(Integer.parseInt(parameter)).iterator();
				if (itr.hasNext())
				{
					while (itr.hasNext())
					{
						SoapStatement statement = (SoapStatement)itr.next();
						System.out.println("statement >" + statement.getStatement());
						buf.append(ScheduleServletPlastique.getXML(statement));
					}

					writer.println("<statements>" + buf.toString() + "</statements>");
				}
			}
			else if (command.equals("statementUpSelect"))
			{
				System.out.println("statementUpSelect invoked >" + parameter);
				//session.setAttribute("soapSelect", parameter);

				// show statements in the box apropos to this selection

				StringBuffer buf = new StringBuffer();

				Iterator itr = SOAPNotesBean.getStatementParents(adminCompany, Integer.parseInt(parameter)).iterator();
				if (itr.hasNext())
				{
					while (itr.hasNext())
					{
						SoapStatement statement = (SoapStatement)itr.next();
						System.out.println("statement >" + statement.getStatement());
						buf.append(ScheduleServletPlastique.getXML(statement));
					}

					writer.println("<statements>" + buf.toString() + "</statements>");
				}
			}
			else if (command.equals("saveStatus"))
			{
				UKOnlinePersonBean person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				boolean modified = false;

				if (parameter.equals("0"))
				{
					// active

					if (!person.isActive())
					{
						person.setActive(true);

						// remove from Black Hole

						if (person.isInBlackHole())
						{
							BlackHoleBean black_hole = BlackHoleBean.getInstance(adminCompany, (UKOnlinePersonBean)adminLoginBean.getPerson());
							if (black_hole.contains(person))
							{
								black_hole.remove(person);
								black_hole.setCreateOrModifyPerson((UKOnlinePersonBean)adminLoginBean.getPerson());
								black_hole.save();
							}

							person.setIsInBlackHole(false);
						}

						modified = true;
					}
				}
				else if (parameter.equals("1"))
				{
					// black hole

					if (!person.isInBlackHole())
					{

						BlackHoleBean black_hole = BlackHoleBean.getInstance(adminCompany, (UKOnlinePersonBean)adminLoginBean.getPerson());

						if (!black_hole.contains(person))
						{
							black_hole.add(person);
							black_hole.setCreateOrModifyPerson((UKOnlinePersonBean)adminLoginBean.getPerson());
							black_hole.save();
						}

						person.setIsInBlackHole(true);

						modified = true;
					}
				}
				else
				{
					if (person.isInBlackHole())
					{
						BlackHoleBean black_hole = BlackHoleBean.getInstance(adminCompany, (UKOnlinePersonBean)adminLoginBean.getPerson());
						if (black_hole.contains(person))
						{
							black_hole.remove(person);
							black_hole.setCreateOrModifyPerson((UKOnlinePersonBean)adminLoginBean.getPerson());
							black_hole.save();
						}

						person.setIsInBlackHole(false);
						modified = true;
					}

					// inactivate

					if (person.isActive())
					{
						person.setActive(false);
						modified = true;
					}
				}

				if (modified)
					person.save();
			}
			else if (command.equals("expandLeft"))
			{
				UKOnlinePersonBean admin_person = (UKOnlinePersonBean)adminLoginBean.getPerson();
				admin_person.setLeftExpanded(true);
			}
			else if (command.equals("collapseLeft"))
			{
				UKOnlinePersonBean admin_person = (UKOnlinePersonBean)adminLoginBean.getPerson();
				admin_person.setLeftExpanded(false);
			}
			else if (command.equals("expandRight"))
			{
				UKOnlinePersonBean admin_person = (UKOnlinePersonBean)adminLoginBean.getPerson();
				admin_person.setRightExpanded(true);
			}
			else if (command.equals("collapseRight"))
			{
				UKOnlinePersonBean admin_person = (UKOnlinePersonBean)adminLoginBean.getPerson();
				admin_person.setRightExpanded(false);
			}
			else if (command.equals("acceptCreditCard"))
			{
				System.out.println("acceptCreditCard invoked >" + parameter);

				StringBuffer credit_card_status = (StringBuffer)session.getAttribute("credit_card_status");
				if (credit_card_status != null)
					throw new IllegalValueException("Credit card processing already in progress.");

				credit_card_status = new StringBuffer();
				session.setAttribute("credit_card_status", credit_card_status);

				timerObj.schedule(new ProcessCreditCardTask(adminCompany, session, parameter), (long)0);

				writer.println("<credit-card-start><![CDATA[dummy]]></credit-card-start>");
			}
			else if (command.equals("acceptCreditCardStatus"))
			{
				StringBuffer credit_card_status = (StringBuffer)session.getAttribute("credit_card_status");
				if (credit_card_status == null)
				{
					QBMSCreditCardResponse response = (QBMSCreditCardResponse)session.getAttribute("charge_response");
					writer.println("<credit-card-complete><![CDATA[" + response.getValue() + "]]></credit-card-complete>");
					session.removeAttribute("charge_response");
				}
				else
				{
					String value = credit_card_status.toString();
					//StringTokenizer tokenizer = new StringTokenizer();
					String[] values = value.split("\\|");

					System.out.println("status >" + value);
					//return "<person><key>" + person.getId() + "</key><value><![CDATA[" + value + "]]></value></person>";
					String current_value = values[values.length - 1];
					
					if (current_value.indexOf("Error:") > -1)
					{
						String str = "<credit-card-error><![CDATA[" + current_value + "]]></credit-card-error>";
						System.out.println(str);
						writer.println(str);
						session.removeAttribute("credit_card_status");
					}
					else
					{
						String str = "<credit-card-status><![CDATA[" + current_value + "]]></credit-card-status>";
						System.out.println(str);
						writer.println(str);
					}

				}
			}
			else if (command.equals("settleMerchantAccount"))
			{
				CashOut cash_out = CashOut.getCashOut(Integer.parseInt(parameter));

				StringBuffer merchant_account_status = (StringBuffer)session.getAttribute("settle_merchant_account_status");
				if (merchant_account_status != null)
					throw new IllegalValueException("Settling merchant account already in progress.");

				merchant_account_status = new StringBuffer();
				session.setAttribute("settle_merchant_account_status", merchant_account_status);

				timerObj.schedule(new SettleMerchantAccountTask(session, cash_out), (long)0);

				writer.println("<settle-merchant-account-start><![CDATA[dummy]]></settle-merchant-account-start>");
			}
			else if (command.equals("settleMerchantAccountStatus"))
			{
				StringBuffer merchant_account_status = (StringBuffer)session.getAttribute("settle_merchant_account_status");

				System.out.println("settleMerchantAccountStatus invoked >" + merchant_account_status);
				
				if (merchant_account_status == null)
					writer.println("<settle-merchant-account-complete><![CDATA[dummy]]></settle-merchant-account-complete>");
				else
				{
					String value = merchant_account_status.toString();
					//StringTokenizer tokenizer = new StringTokenizer();
					String[] values = value.split("\\|");

					System.out.println("status >" + value);
					//return "<person><key>" + person.getId() + "</key><value><![CDATA[" + value + "]]></value></person>";
					String current_value = values[values.length - 1];

					if (current_value.indexOf("Complete") > -1)
					{
						writer.println("<settle-merchant-account-complete><![CDATA[" + current_value + "]]></settle-merchant-account-complete>");
						session.removeAttribute("settle_merchant_account_status");
					}
					else if (current_value.indexOf("Error:") > -1)
					{
						writer.println("<settle-merchant-account-error><![CDATA[" + current_value + "]]></settle-merchant-account-error>");
						session.removeAttribute("settle_merchant_account_status");
					}
					else
						writer.println("<settle-merchant-account-status><![CDATA[" + current_value + "]]></settle-merchant-account-status>");

				}
			}
			else if (command.equals("voidCharge"))
			{
				QBMSCreditCardResponse charge_to_void = QBMSCreditCardResponse.getResponse(Integer.parseInt(parameter));
				System.out.println("charge_to_void >" + charge_to_void);

				/*
				StringBuffer merchant_account_status = (StringBuffer)session.getAttribute("settle_merchant_account_status");
				if (merchant_account_status != null)
					throw new IllegalValueException("Settling merchant account already in progress.");
				 */

				/*
				merchant_account_status = new StringBuffer();
				session.setAttribute("settle_merchant_account_status", merchant_account_status);
				 */

				timerObj.schedule(new VoidChargeTask(adminCompany, session, charge_to_void), (long)0);

				//writer.println("<settle-merchant-account-start><![CDATA[dummy]]></settle-merchant-account-start>");


				//writer.println("<void-charge><![CDATA[" + parameter + "]]></void-charge>");
			}
			else if (command.equals("updateQuickBooks"))
			{
				//System.out.println("updateQuickBooks invoked >" + parameter);

				CashOut cash_out = CashOut.getCashOut(Integer.parseInt(parameter));
				if (cash_out.isQuickBooksUpdateInProgress())
					throw new IllegalValueException("QuickBooks update already in progress.");
				if (cash_out.isComplete())
					throw new IllegalValueException("QuickBooks update already completed.");
				
				cash_out.setIsQuickBooksUpdateInProgress(true);
				cash_out.clearLog();

				StringBuffer status = (StringBuffer)session.getAttribute("update_quickbooks_status");
				

				status = new StringBuffer();
				session.setAttribute("update_quickbooks_status", status);
				
				session.setAttribute("cashOut", cash_out);

				timerObj.schedule(new UpdateQuickBooksTask(session, cash_out, adminCompany), (long)0);
				
				//System.out.println("cash_out.isQuickBooksUpdateInProgress(1) - " + cash_out + " >" + cash_out.isQuickBooksUpdateInProgress());

				writer.println("<update-quickbooks-start><![CDATA[dummy]]></update-quickbooks-start>");
			}
			else if (command.equals("updateQuickBooksStatus"))
			{
				//System.out.println("updateQuickBooksStatus invoked >" + parameter);
				
				StringBuffer status = (StringBuffer)session.getAttribute("update_quickbooks_status");

				//System.out.println("updateQuickBooksStatus invoked >" + status);

				CashOut cash_out = (CashOut)session.getAttribute("cashOut");
				
				//System.out.println("cash_out.isQuickBooksUpdateInProgress() - " + cash_out + " >" + cash_out.isQuickBooksUpdateInProgress());
				
					
				boolean is_complete = false;
				boolean is_error = false;
				String current_value = "[NO STATUS FOUND]";
				
				//System.out.println("cash_out.isQuickBooksUpdateInProgress(2) - " + cash_out + " >" + cash_out.isQuickBooksUpdateInProgress());
				
				if (cash_out.isQuickBooksUpdateInProgress())
				{
					String value = status.toString();
					String[] values = value.split("\\|");
					
					for (int i = 0; i < values.length; i++)
					{
						current_value = values[i];
						
						//System.out.println("current_value >" + current_value);

						if (current_value.indexOf("Update complete") > -1 || current_value.indexOf("No data found") > -1)
						{
							is_complete = true;
						}
						else if (current_value.indexOf("Error:") > -1)
						{
							is_error = true;
						}
					}
				}
				else
					is_complete = true;
				
				//System.out.println("is_complete >" + is_complete);
					
				if (is_complete)
				{
					String log_file = EndOfDayLogBuilder.generateEndOfDayLog(cash_out.getLog());
					cash_out.setLogFilePDFLocation(log_file);
					writer.println("<update-quickbooks-complete><![CDATA[" + log_file + "]]></update-quickbooks-complete>");
					session.removeAttribute("update_quickbooks_status");

					try
					{
						cash_out.setIsComplete(true);
						cash_out.save();
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
				else if (is_error)
				{
					writer.println("<update-quickbooks-error><![CDATA[" + current_value + "]]></update-quickbooks-error>");
					session.removeAttribute("update_quickbooks_status");
				}
				else
					writer.println("<update-quickbooks-status><![CDATA[" + current_value + "]]></update-quickbooks-status>");
						
			}
			else if (command.equals("showQuickBooksMessages"))
			{
				
				String message = "";
				QuickBooksSettings settings = adminCompany.getQuickBooksSettings();
				if (settings != null)
					message = settings.getLastMessage();
				
				writer.println("<quickbooks-status><![CDATA[" + message + "]]></quickbooks-status>");
						
			}
			else if (command.equals("selectReviewItem"))
			{
				System.out.println("selectReviewItem invoked >" + parameter);

				ClientReviewReason client_review_reason = ClientReviewReason.getClientReviewReason(Integer.parseInt(parameter));
				writer.println(ScheduleServletPlastique.getXMLDetail(client_review_reason));
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
			writer.println("<error><![CDATA[Unexpected Error >" + x.getMessage() + "]]></error>");
			if (session != null)
				x.printStackTrace();
		}
    }
	
	private StringBuffer
	assembleScheduleXML(UKOnlineCompanyBean _company, PracticeAreaBean _practice_area, Calendar _display_date, String _session_id, boolean _get_all_appts)
		throws Exception
	{
		return this.assembleScheduleXML(_company, _practice_area, _display_date, null, null, _session_id, _get_all_appts);
	}
	
	private StringBuffer
	assembleScheduleXML(UKOnlineCompanyBean _company, PracticeAreaBean _practice_area, Calendar _display_date, Calendar _start_date, Calendar _end_date, String _session_id, boolean _get_all_appts)
		throws Exception
	{
		StringBuffer returnBuf = new StringBuffer();
		
		int num_total = 0;
		int num_scheduled = 0;
		int num_cancelled = 0;
		int num_checked_out = 0;
		int num_rescheduled = 0;
		
		Vector scheduled = new Vector();
		Vector checked_in = new Vector();
		Vector late = new Vector();
		Vector no_show = new Vector();
		
		/*
		Iterator appt_itr = null;
		if (_end_date == null) {
			appt_itr = AppointmentBean.getAppointments(_company, _practice_area, _start_date.getTime()).iterator();
		} else {
			appt_itr = AppointmentBean.getAppointmentsFromStartDateToEndDate(_company, _practice_area, _start_date.getTime(), _end_date.getTime()).iterator();
		}
		*/
		
		Iterator appt_itr = AppointmentBean.getAppointments(_company, _practice_area, new Date()).iterator();
		
		while (appt_itr.hasNext())
		{
			AppointmentBean appointment = (AppointmentBean)appt_itr.next();
			if (appointment.isClientAppointment())
			{
				short state = appointment.getState();
				switch (state)
				{
					case AppointmentBean.CANCELLED_APPOINTMENT_STATUS: num_cancelled++; break;
					case AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS: checked_in.addElement(appointment); num_total++; break;
					case AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS: num_checked_out++; num_total++; break;
					case AppointmentBean.DEFAULT_APPOINTMENT_STATUS: scheduled.addElement(appointment); num_scheduled++; num_total++; break;
					case AppointmentBean.LATE_APPOINTMENT_STATUS: late.addElement(appointment); num_total++; break;
					case AppointmentBean.NO_SHOW_APPOINTMENT_STATUS: no_show.addElement(appointment); break;
					case AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS:num_rescheduled++; num_total++; break;
				}
			}
		}
		
		// has the schedule been updated since the last client request for this date?
		
		
		String date_str = ScheduleServletPlastique.short_date_format.format(_display_date.getTime());
		returnBuf.append("<date y=\"" + _display_date.get(Calendar.YEAR) + "\" m=\"" + _display_date.get(Calendar.MONTH) + "\" d=\"" + _display_date.get(Calendar.DATE) + "\" tot=\"" + num_total + "\" sc=\"" + num_scheduled + "\" cn=\"" + num_cancelled + "\" co=\"" + num_checked_out + "\" rs=\"" + num_rescheduled + "\">" + date_str + "</date>");
		returnBuf.append("<schedule>");

		String key = null;
		if (_end_date == null) {
			key = date_str;
		} else {
			key = ScheduleServletPlastique.short_date_format.format(_start_date.getTime()) + "-" + ScheduleServletPlastique.short_date_format.format(_end_date.getTime());
		}
		
		System.out.println("key >" + key + "  _get_all_appts >" + _get_all_appts);

		String appt_str = null;
		if (_get_all_appts) {
			if (_end_date == null) {
				appt_str = this.getScheduleXML(_company, _practice_area, _display_date.getTime(), _session_id);
				System.out.println("A >" + appt_str);
			} else {
				appt_str = this.getScheduleXMLStartToEnd(_company, _practice_area, _start_date.getTime(), _end_date.getTime(), _session_id);
				System.out.println("B >" + appt_str);
			}
		} else {
			//Date last_update = ScheduleServletPlastique.last_update_hash.get(key);
			HashMap hash = (HashMap)ScheduleServletPlastique.last_update_hash.get(_session_id);
			if (hash == null) {
				hash = new HashMap();
				last_update_hash.put(_session_id, hash);
			}
			Date last_update = (Date)hash.get(key);
			
			System.out.println("last_update >" + CUBean.getUserDateString(last_update));
			
			if (last_update == null) {
				if (_end_date == null) {
					appt_str = this.getScheduleXML(_company, _practice_area, _display_date.getTime(), _session_id);
					System.out.println("C appt_str >" + appt_str);
				} else {
					appt_str = this.getScheduleXMLStartToEnd(_company, _practice_area, _start_date.getTime(), _end_date.getTime(), _session_id);
					System.out.println("D appt_str >" + appt_str);
				}
			} else {
				if (_end_date == null) {
					appt_str = this.getScheduleXML(_company, _practice_area, _display_date.getTime(), last_update, _session_id);
					System.out.println("E appt_str >" + appt_str);
				} else {
					appt_str = this.getScheduleXMLStartToEnd(_company, _practice_area, _start_date.getTime(), _end_date.getTime(), last_update, _session_id);
					System.out.println("F appt_str >" + appt_str);
				}
			}
		}

		if (appt_str.length() > 0)
		{
			returnBuf.append(appt_str);
			//ScheduleServletPlastique.last_update_hash.put(key, new Date());
			HashMap hash = (HashMap)ScheduleServletPlastique.last_update_hash.get(_session_id);
			if (hash == null) {
				hash = new HashMap();
				last_update_hash.put(_session_id, hash);
			}
			hash.put(key, new Date());
		}

		returnBuf.append("</schedule>");
		
		
		if (num_scheduled > 0) {
			returnBuf.append("<schd num=\"" + num_scheduled + "\">");
			Iterator itr = scheduled.iterator();
			while (itr.hasNext())
			{
				AppointmentBean appointment = (AppointmentBean)itr.next();
				returnBuf.append(ScheduleServletPlastique.getXML(appointment, _session_id));
			}
			returnBuf.append("</schd>");
		}
		
		if (!checked_in.isEmpty()) {
			returnBuf.append("<c-in num=\"" + checked_in.size() + "\">");
			Iterator itr = checked_in.iterator();
			while (itr.hasNext())
			{
				AppointmentBean appointment = (AppointmentBean)itr.next();
				returnBuf.append(ScheduleServletPlastique.getXML(appointment, _session_id));
			}
			returnBuf.append("</c-in>");
		}
		
		if (!late.isEmpty()) {
			returnBuf.append("<late num=\"" + late.size() + "\">");
			Iterator itr = late.iterator();
			while (itr.hasNext())
			{
				AppointmentBean appointment = (AppointmentBean)itr.next();
				returnBuf.append(ScheduleServletPlastique.getXML(appointment, _session_id));
			}
			returnBuf.append("</late>");
		}
		
		if (!no_show.isEmpty()) {
			returnBuf.append("<no-show num=\"" + no_show.size() + "\">");
			Iterator itr = no_show.iterator();
			while (itr.hasNext())
			{
				AppointmentBean appointment = (AppointmentBean)itr.next();
				returnBuf.append(ScheduleServletPlastique.getXML(appointment, _session_id));
			}
			returnBuf.append("</no-show>");
		}

		return returnBuf;
	}
    
    private String
    getScheduleXML(UKOnlineCompanyBean _company, PracticeAreaBean _practice_area, Date _date, String _session_id)
		throws Exception
    {
		return this.getScheduleXMLStartToEnd(_company, _practice_area, _date, null, _session_id);
    }
    
    private String
    getScheduleXMLStartToEnd(UKOnlineCompanyBean _company, PracticeAreaBean _practice_area, Date _start_date, Date _end_date, String _session_id)
		throws Exception
    {
		StringBuffer appt_buf = new StringBuffer();
		StringBuffer meeting_buf = new StringBuffer();
		StringBuffer off_time_buf = null;
		//if (_includeOffTime) {
			off_time_buf = new StringBuffer();
		//}
		StringBuffer cancelled_rescheduled_buf = new StringBuffer();
		
		Iterator appointment_itr = null;
		if (_end_date == null) {
			appointment_itr = AppointmentBean.getAppointments(_company, _practice_area, _start_date).iterator();
		} else {
			appointment_itr = AppointmentBean.getAppointmentsFromStartDateToEndDate(_company, _practice_area, _start_date, _end_date).iterator();
		}
		
		while (appointment_itr.hasNext())
		{
			AppointmentBean appointment = (AppointmentBean)appointment_itr.next();

			if (appointment.isCancelled() || appointment.isRescheduled())
				cancelled_rescheduled_buf.append(ScheduleServletPlastique.getXML(appointment, _session_id));
			else
			{
				AppointmentTypeBean type = appointment.getType();
				if (type.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
					appt_buf.append(ScheduleServletPlastique.getXML(appointment, _session_id));
				else if (type.getType() == AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE)
					meeting_buf.append(ScheduleServletPlastique.getXML(appointment, _session_id));
				else
					off_time_buf.append(ScheduleServletPlastique.getXML(appointment, _session_id));
			}
		}
		
		return off_time_buf.toString() + cancelled_rescheduled_buf.toString() + meeting_buf.toString() + appt_buf.toString();
    }
    
    private String
    getScheduleXML(CompanyBean _company, PracticeAreaBean _practice_area, Date _date, Date _last_update_date, String _session_id)
		throws Exception
    {
		return this.getScheduleXMLStartToEnd(_company, _practice_area, _date, null, _last_update_date, _session_id);
    }
    
    private String
    getScheduleXMLStartToEnd(CompanyBean _company, PracticeAreaBean _practice_area, Date _start_date, Date _end_date, Date _last_update_date, String _session_id)
		throws Exception
    {
		StringBuffer appt_buf = new StringBuffer();
		StringBuffer off_time_buf = new StringBuffer();

		//HashMap appointments = AppointmentBean.getAppointments(_company, _practice_area, _date, _last_update_date, true);
		
		HashMap appointments = null;
		if (_end_date == null)
			appointments = AppointmentBean.getAppointments(_company, _practice_area, _start_date, _last_update_date);
		else
			appointments = AppointmentBean.getAppointmentsFromStartDateToEndDate(_company, _practice_area, _start_date, _end_date, _last_update_date);
		
		Iterator keys = appointments.keySet().iterator();
		while (keys.hasNext())
		{
			String key = (String)keys.next();
			Iterator appointment_itr = ((Vector)appointments.get(key)).iterator();
			while (appointment_itr.hasNext())
			{
				AppointmentBean appointment = (AppointmentBean)appointment_itr.next();
				
				AppointmentTypeBean type = appointment.getType();
				if (type.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
					appt_buf.append(ScheduleServletPlastique.getXML(appointment, _session_id));
				else //if (type.getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
					off_time_buf.append(ScheduleServletPlastique.getXML(appointment, _session_id));
			}
		}
		
		return off_time_buf.toString() + appt_buf.toString();
    }
    
    private String
    getPractitionerScheduleXML(UKOnlineCompanyBean _company, UKOnlinePersonBean _practitioner, Date _start_date, Date _end_date)
		throws Exception
    {
		StringBuffer appt_buf = new StringBuffer();
		StringBuffer meeting_buf = new StringBuffer();
		StringBuffer off_time_buf = new StringBuffer();
		StringBuffer cancelled_rescheduled_buf = new StringBuffer();
		
		Iterator appointment_itr = AppointmentBean.getAppointments(_company, _practitioner, _start_date, _end_date).iterator();
		while (appointment_itr.hasNext())
		{
			AppointmentBean appointment = (AppointmentBean)appointment_itr.next();

			if (appointment.isCancelled() || appointment.isRescheduled())
				cancelled_rescheduled_buf.append(ScheduleServletPlastique.getPractitionerXML(appointment));
			else
			{
				AppointmentTypeBean type = appointment.getType();
				if (type.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
					appt_buf.append(ScheduleServletPlastique.getPractitionerXML(appointment));
				else if (type.getType() == AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE)
					meeting_buf.append(ScheduleServletPlastique.getPractitionerXML(appointment));
				else
					off_time_buf.append(ScheduleServletPlastique.getPractitionerXML(appointment));
			}
		}
		
		return off_time_buf.toString() + cancelled_rescheduled_buf.toString() + meeting_buf.toString() + appt_buf.toString();
    }

    private String
    getPractitionerScheduleXML(CompanyBean _company, UKOnlinePersonBean _practitioner, Date _start_date, Date _end_date, Date _last_update_date)
		throws Exception
    {
		StringBuffer appt_buf = new StringBuffer();
		StringBuffer off_time_buf = new StringBuffer();

		//HashMap appointments = AppointmentBean.getAppointments(_company, _practitioner, _start_date, _end_date, _last_update_date, true);
		HashMap appointments = AppointmentBean.getAppointments(_company, _practitioner, _start_date, _end_date, _last_update_date);
		//System.out.println("getPractitionerScheduleXML - " + appointments.size());
		Iterator keys = appointments.keySet().iterator();
		while (keys.hasNext())
		{
			String key = (String)keys.next();
			Iterator appointment_itr = ((Vector)appointments.get(key)).iterator();
			while (appointment_itr.hasNext())
			{
				AppointmentBean appointment = (AppointmentBean)appointment_itr.next();

				AppointmentTypeBean type = appointment.getType();
				if (type.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
					appt_buf.append(ScheduleServletPlastique.getPractitionerXML(appointment));
				else //if (type.getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
					off_time_buf.append(ScheduleServletPlastique.getPractitionerXML(appointment));
			}
		}

		return off_time_buf.toString() + appt_buf.toString();
    }
    
    private String
    getOffTimeXML(CompanyBean _company, Date _date)
		throws Exception
    {
		StringBuffer buf = new StringBuffer();

		HashMap appointments = AppointmentBean.getOffTime(_company, _date);
		Iterator keys = appointments.keySet().iterator();
		while (keys.hasNext())
		{
			String key = (String)keys.next();
			Iterator appointment_itr = ((Vector)appointments.get(key)).iterator();
			while (appointment_itr.hasNext())
			{
				AppointmentBean appointment = (AppointmentBean)appointment_itr.next();
				/*
				AppointmentTypeBean type = appointment.getType();
				String value = null;
				if (type.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
					value = "{\"label\" : \"" + appointment.getLabel() + "\", \"cid\" : \"" + appointment.getClient().getId() + "\", \"state\" : \"" + appointment.getState() + "\", \"id\" : \"" + appointment.getId() + "\", \"duration\" : \"" + appointment.getDurationString() + "\", \"type\" : \"" + type.getId() + "\" }";
				else if (type.getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
					value = "{\"label\" : \"" + appointment.getLabel() + "\", \"cid\" : \"0\", \"state\" : \"" + appointment.getState() + "\", \"id\" : \"" + appointment.getId() + "\", \"duration\" : \"" + appointment.getDurationString() + "\", \"type\" : \"" + type.getId() + "\" }";
				 */
				//buf.append("<appt><key>" + key + "</key><value><![CDATA[" + value + "]]></value></appt>");
				buf.append(ScheduleServletPlastique.getXML(appointment, null));
			}
		}

		return buf.toString();
    }
	
	public static String
	getXML(AppointmentBean _appointment)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return ScheduleServletPlastique.getXML(_appointment, null);
	}
	
	public static String
	getXML(AppointmentBean _appointment, String _sessionId)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		String appt_comments = _appointment.getComments().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_');
		AppointmentTypeBean type = _appointment.getType();
		String value = null;
		if (type.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
		{
			if (_appointment.isRecurring()) {
				value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"" + _appointment.getClient().getId() + "\", \"state\" : \"" + _appointment.getState() + "\"" + (_appointment.getClient().hasBirthdayDuringWeek(_appointment.getAppointmentDate()) ? (", \"bd\" : \"" + _appointment.getClient().getBirthDateString() + "\"") : "") + ", \"n\" : \"" + _appointment.isFirstTimeAppointmentForClientInPracticeArea() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\", \"r\" : \"1\", \"r1\" : \"" + _appointment.getRecurVal() + "\", \"r2\" : \"" + _appointment.getRecurPeriod() + "\", \"r3\" : \"" + _appointment.getRecurPW() + "\", \"r4\" : \"" + _appointment.getRecurPWS() + "\", \"r5\" : \"" + _appointment.getRecurStopAfterN() + "\", \"r6\" : \"" + _appointment.getRecurStopByDateString() + "\" }";
			} else {
				if (_appointment.hasClient()) {
					boolean isNew = AppointmentBean.isNewlyCreated(_appointment, _sessionId);
					if (isNew) {
						value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"" + _appointment.getClient().getId() + "\", \"state\" : \"" + _appointment.getState() + "\"" + (_appointment.getClient().hasBirthdayDuringWeek(_appointment.getAppointmentDate()) ? (", \"bd\" : \"" + _appointment.getClient().getBirthDateString() + "\"") : "") + ", \"n\" : \"" + _appointment.isFirstTimeAppointmentForClientInPracticeArea() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"start\" : \"" + _appointment.getAppointmentStartUnixTimestamp() + "\", \"end\" : \"" + _appointment.getAppointmentEndUnixTimestamp() + "\", \"type\" : \"" + type.getId() + "\", \"new\" : \"y\", \"cm\" : \"" + appt_comments + "\" }";
					} else {
						value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"" + _appointment.getClient().getId() + "\", \"state\" : \"" + _appointment.getState() + "\"" + (_appointment.getClient().hasBirthdayDuringWeek(_appointment.getAppointmentDate()) ? (", \"bd\" : \"" + _appointment.getClient().getBirthDateString() + "\"") : "") + ", \"n\" : \"" + _appointment.isFirstTimeAppointmentForClientInPracticeArea() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"start\" : \"" + _appointment.getAppointmentStartUnixTimestamp() + "\", \"end\" : \"" + _appointment.getAppointmentEndUnixTimestamp() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
					}
				}
				else
					value = "{\"label\" : \"\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
			}
		}
		else// if (type.getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
		{
			if (_appointment.isRecurring()) {
				value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\", \"r\" : \"1\", \"r1\" : \"" + _appointment.getRecurVal() + "\", \"r2\" : \"" + _appointment.getRecurPeriod() + "\", \"r3\" : \"" + _appointment.getRecurPW() + "\", \"r4\" : \"" + _appointment.getRecurPWS() + "\", \"r5\" : \"" + _appointment.getRecurStopAfterN() + "\", \"r6\" : \"" + _appointment.getRecurStopByDateString() + "\" }";
			} else {
				value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"start\" : \"" + _appointment.getAppointmentStartUnixTimestamp() + "\", \"end\" : \"" + _appointment.getAppointmentEndUnixTimestamp() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
			}
		}
		return "<appt><key>" + _appointment.getKey() + "</key><value><![CDATA[" + value + "]]></value></appt>";
	}
	
	public static String
	getPractitionerXML(AppointmentBean _appointment)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		String appt_comments = _appointment.getComments().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_');
		AppointmentTypeBean type = _appointment.getType();
		String value = null;
		if (type.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
		{
			if (_appointment.isRecurring())
				value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"" + _appointment.getClient().getId() + "\", \"state\" : \"" + _appointment.getState() + "\"" + (_appointment.getClient().hasBirthdayDuringWeek(_appointment.getAppointmentDate()) ? (", \"bd\" : \"" + _appointment.getClient().getBirthDateString() + "\"") : "") + ", \"n\" : \"" + _appointment.isFirstTimeAppointmentForClientInPracticeArea() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\", \"r\" : \"1\", \"r1\" : \"" + _appointment.getRecurVal() + "\", \"r2\" : \"" + _appointment.getRecurPeriod() + "\", \"r3\" : \"" + _appointment.getRecurPW() + "\", \"r4\" : \"" + _appointment.getRecurPWS() + "\", \"r5\" : \"" + _appointment.getRecurStopAfterN() + "\", \"r6\" : \"" + _appointment.getRecurStopByDateString() + "\" }";
			else
			{
				if (_appointment.hasClient())
					value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"" + _appointment.getClient().getId() + "\", \"state\" : \"" + _appointment.getState() + "\"" + (_appointment.getClient().hasBirthdayDuringWeek(_appointment.getAppointmentDate()) ? (", \"bd\" : \"" + _appointment.getClient().getBirthDateString() + "\"") : "") + ", \"n\" : \"" + _appointment.isFirstTimeAppointmentForClientInPracticeArea() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
				else
					value = "{\"label\" : \"\", \"cid\" : \"\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
			}
		}
		else// if (type.getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
		{
			if (_appointment.isRecurring())
				value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\", \"r\" : \"1\", \"r1\" : \"" + _appointment.getRecurVal() + "\", \"r2\" : \"" + _appointment.getRecurPeriod() + "\", \"r3\" : \"" + _appointment.getRecurPW() + "\", \"r4\" : \"" + _appointment.getRecurPWS() + "\", \"r5\" : \"" + _appointment.getRecurStopAfterN() + "\", \"r6\" : \"" + _appointment.getRecurStopByDateString() + "\" }";
			else
				value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
		}
		return "<appt><key>" + _appointment.getKeyDayOfWeek() + "</key><value><![CDATA[" + value + "]]></value></appt>";
	}
    
    public static String
    getXML(UKOnlinePersonBean _person)
		throws Exception
    {
		String value = "{\"label\" : \"" + _person.getLabel() + "\" }";
		return "<person><key>" + _person.getId() + "</key><value><![CDATA[" + value + "]]></value></person>";
    }
    
    public static String
    getXMLIncludeFileType(UKOnlinePersonBean _person)
		throws Exception
    {
		/*
		String value = "{\"label\" : \"" + _person.getLabel() + "\"," +
						" \"fts\" : \"" + _person.getGroupUnderCareMemberTypeLabel() + "\" }";
		*/
		
		String label_w_title = _person.getLabel();
		String title_str = _person.getTitleString();
		if (!(title_str.equals("") || title_str.equals("[DEFAULT]")))
			label_w_title = label_w_title + " (" + title_str + ")";
			
		
		String value = "{\"label\" : \"" + _person.getLabel() + "\"," +
						" \"ltl\" : \"" + label_w_title + "\"," +
						" \"fts\" : \"" + _person.getTitleString() + "\" }";
		return "<person><key>" + _person.getId() + "</key><value><![CDATA[" + value + "]]></value></person>";
    }
    
    public static String
    getXML(UKOnlinePersonBean _person, GroupUnderCareMember _member)
		throws Exception
    {
		String value = "{\"label\" : \"" + _person.getLabel() + "\"," +
						" \"relate\" : \"" + GroupUnderCareBean.getRelationshipString(_member.getRelationshipToPrimaryClient()) + "\" }";
		return "<person><key>" + _person.getId() + "</key><value><![CDATA[" + value + "]]></value></person>";
    }
	
	public static String
	getXML(ContactStatusBean _status)
		throws Exception
	{
		String label_str = _status.getLabel().replaceAll("\"", "\\\\\"").replaceAll("\\\\", "\\\\\\\\");
		String value = "{\"label\" : \"" + label_str + "\" }";
		return "<item><key>" + _status.getId() + "</key><value><![CDATA[" + value + "]]></value></item>";
	}
	
	public static String
	getXML(ToDoItemBean _to_do)
		throws Exception
	{
		String value = "{\"label\" : \"" + _to_do.getLabel() + "\" }";
		return "<item><key>" + _to_do.getId() + "</key><value><![CDATA[" + value + "]]></value></item>";
	}

	public static String
	getXML(ClientReviewReason _client_review_reason)
		throws Exception
	{
		//String label_str = _client_review_reason.getLabel().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\");
		String label_str = _client_review_reason.getLabel().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_');
		String value = "{\"label\" : \"" + label_str + "\" }";
		return "<item><key>" + _client_review_reason.getId() + "</key><value><![CDATA[" + value + "]]></value></item>";
	}

	public static String
	getXMLDetail(ClientReviewReason _client_review_reason)
		throws Exception
	{
		//String note_str = _client_review_reason.getNoteString().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\");
		String note_str = _client_review_reason.getNoteString().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_');
		String value = "{\"note\" : \"" + note_str + "\"," +
						" \"practitioner\" : \"" + _client_review_reason.getPractitioner().getId() + "\"," +
						" \"reason\" : \"" + _client_review_reason.getReviewReason().getId() + "\"," +
						" \"date\" : \"" + _client_review_reason.getReviewDateString() + "\" }";
		return "<followup><key>" + _client_review_reason.getId() + "</key><value><![CDATA[" + value + "]]></value></followup>";
	}
	
	public static String
	getXML(CareDetailsBean _details)
		throws Exception
	{
		String value = "{\"label\" : \"" + _details.getLabel() + "\" }";
		return "<item><key>" + _details.getId() + "</key><value><![CDATA[" + value + "]]></value></item>";
	}

	public static String
	getXML(PersonNote _note)
		throws Exception
	{
		String value = "{\"label\" : \"" + _note.getLabel().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\") + "\" }";
		return "<item><key>" + _note.getId() + "</key><value><![CDATA[" + value + "]]></value></item>";
	}

	public static String
	getXML(SOAPNotesBean _note)
		throws Exception
	{
		/*
		String value = "{\"label\" : \"" + _note.getLabel().replaceAll("\"", "\\\\\"").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\" }";
		return "<item><key>" + _note.getId() + "</key><value><![CDATA[" + value + "]]></value></item>";
		*/
		
		String value = "{\"label\" : \"" + _note.getLabel().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\" }";
		return "<item><key>" + _note.getId() + "</key><value><![CDATA[" + value + "]]></value></item>";
	}

	public static String
	getXML(SoapStatement _statement)
		throws Exception
	{
		String value = "{\"label\" : \"" + _statement.getStatement() + "\" }";
		return "<item><key>" + _statement.getSoapStatementId() + "</key><value><![CDATA[" + value + "]]></value></item>";
	}

	public static String
	getXMLDetail(PersonNote _note)
		throws Exception
	{
		//String note_str = _note.getNoteString().replaceAll("\"", "\\\\\"").replaceAll("\\\\", "\\\\\\\\");
		String note_str = _note.getNoteString().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_');
		String value = "{\"note\" : \"" + note_str + "\"," +
						" \"person\" : \"" + _note.getPerson().getLabel() + "\"," +
						" \"subject\" : \"" + _note.getSubjectString().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\") + "\"," +
						" \"show\" : \"" + _note.showOnCheckIn() + "\" }";
		return "<item><key>" + _note.getId() + "</key><value><![CDATA[" + value + "]]></value></item>";
	}

	public static String
	getXMLDetail(SOAPNotesBean _note)
		throws Exception
	{
		System.out.println("chazing cars >" + _note.getSNoteString());

		/*
		String value = "{\"s\" : \"" + _note.getSNoteString().replaceAll("\"", "\\\\\"").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\"," +
						" \"o\" : \"" + _note.getONoteString().replaceAll("\"", "\\\\\"").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\"," +
						" \"a\" : \"" + _note.getANoteString().replaceAll("\"", "\\\\\"").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\"," +
						" \"p\" : \"" + _note.getPNoteString().replaceAll("\"", "\\\\\"").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\"," +
						" \"pa\" : \"" + _note.getPracticeArea().getId() + "\" }";
		*/
				
		String value = "{\"s\" : \"" + _note.getSNoteString().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\"," +
						" \"o\" : \"" + _note.getONoteString().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\"," +
						" \"a\" : \"" + _note.getANoteString().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\"," +
						" \"p\" : \"" + _note.getPNoteString().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\"," +
						" \"pa\" : \"" + _note.getPracticeArea().getId() + "\" }";
		
		return "<item><key>" + _note.getId() + "</key><value><![CDATA[" + value + "]]></value></item>";
	}
	
	public static String
	getXML(GroupUnderCareBean _group_under_care)
		throws Exception
	{
		StringBuffer buf = new StringBuffer();

		Iterator group_under_care_members_itr = _group_under_care.getMembers().iterator();
		while (group_under_care_members_itr.hasNext())
		{
			GroupUnderCareMember member = (GroupUnderCareMember)group_under_care_members_itr.next();
			UKOnlinePersonBean person_member = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(member.getPersonId());
			buf.append(getXML(person_member, member));
		}

		return "<group id=\"" + _group_under_care.getId() + "\" label=\"" + _group_under_care.getLabel() + "\">" + buf.toString() + "</group>";
	}

	public static String
	getXML(CheckoutCodeBean _code)
		throws Exception
	{
		String value = "{\"code\" : \"" + _code.getCode() + "\"," +
						" \"desc\" : \"" + _code.getDescription() + "\"," +
						" \"type\" : \"" + _code.getType() + "\"," +
						" \"id\" : \"" + _code.getId() + "\"," +
						" \"amount\" : \"" + _code.getAmountString() + "\"," +
						" \"cost\" : \"" + _code.getOrderCostString() + "\"," +
						" \"tax\" : \"" + _code.getTaxPercentageString() + "\"," +
						" \"pa\" : \"" + _code.getPracticeAreaId() + "\" }";
		return "<code><key>" + _code.getId() + "</key><value><![CDATA[" + value + "]]></value></code>";
	}

	public static String
	getXML(ValeoOrderBean _order)
		throws Exception
	{
		String label = _order.getExtendedLabel();

		String value = "{\"total\" : \"" + _order.getTotalString() + "\"," +
						" \"tax\" : \"" + _order.getTaxString() + "\"," +
						" \"subtotal\" : \"" + _order.getSubtotalString() + "\"," +
						" \"balance\" : \"" + _order.getBalanceString() + "\"," +
						" \"label\" : \"" + label + "\"," +
						" \"id\" : \"" + _order.getId() + "\"," +
						" \"open\" : \"" + (_order.isReturn() ? !_order.isReturn() : _order.isOpen()) + "\"," +
						" \"reversed\" : \"" + _order.isReversed() + "\"," +
						" \"legacy\" : \"" + false + "\"," +
						" \"date\" : \"" + _order.getOrderDateString() + "\" }";
		return "<order><key>" + _order.getId() + "</key><value><![CDATA[" + value + "]]></value></order>";
	}
	
	public static String
	getXML(OrderBean _order, CheckoutOrderline _orderline)
		throws Exception
	{
		CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(_orderline.getCheckoutCodeId());
		
		String value = "{\"amount\" : \"" + _orderline.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
						" \"actual\" : \"" + _orderline.getActualAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
						" \"code\" : \"" + checkout_code.getCode() + "\"," +
						" \"desc\" : \"" + checkout_code.getDescription() + "\"," +
						" \"code_id\" : \"" + checkout_code.getId() + "\"," +
						" \"id\" : \"" + _orderline.getCheckoutOrderlineId() + "\"," +
						" \"qty\" : \"" + _orderline.getQuantity() + "\"," +
						" \"date\" : \"" + _order.getOrderDateString() + "\" }";
		return "<orderline><key>" + _orderline.getCheckoutOrderlineId() + "</key><value><![CDATA[" + value + "]]></value></orderline>";
	}
	
	public static String
	getXML(CheckoutOrderline _orderline)
		throws Exception
	{
		CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(_orderline.getCheckoutCodeId());
		
		if (_orderline.getPrice() == null || _orderline.getActualAmount() == null) {
			String value = "{\"amount\" : \"" + checkout_code.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
							" \"code\" : \"" + checkout_code.getCode() + "\"," +
							" \"desc\" : \"" + checkout_code.getDescription() + "\"," +
							" \"code_id\" : \"" + checkout_code.getId() + "\"," +
							" \"type\" : \"" + checkout_code.getType() + "\"," +
							" \"tax\" : \"" + checkout_code.getTaxPercentageString() + "\"," +
							" \"qty\" : \"" + _orderline.getQuantity() + "\" }";
			return "<orderline><key>" + _orderline.getCheckoutOrderlineId() + "</key><value><![CDATA[" + value + "]]></value></orderline>";
			
		} else {
			String value = "{\"amount\" : \"" + _orderline.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
							" \"actual\" : \"" + _orderline.getActualAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
							" \"code\" : \"" + checkout_code.getCode() + "\"," +
							" \"desc\" : \"" + checkout_code.getDescription() + "\"," +
							" \"code_id\" : \"" + checkout_code.getId() + "\"," +
							" \"type\" : \"" + checkout_code.getType() + "\"," +
							" \"tax\" : \"" + checkout_code.getTaxPercentageString() + "\"," +
							" \"qty\" : \"" + _orderline.getQuantity() + "\" }";
			return "<orderline><key>" + _orderline.getCheckoutOrderlineId() + "</key><value><![CDATA[" + value + "]]></value></orderline>";
		}
		
		
	}

	public static String
	getXML(QBMSCreditCardResponse _response)
		throws Exception
	{
		String value = "{\"amount\" : \"" + _response.getChargeAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
						" \"auth\" : \"" + _response.getAuthorizationCode() + "\" }";
		return "<credit><key>" + _response.getId() + "</key><value><![CDATA[" + value + "]]></value></credit>";
	}

	public static String
	getXML(InvoiceRet _invoice, UKOnlineCompanyBean _company)
		throws Exception
	{
		String label = null;
		try
		{
			Iterator itr = _invoice.getInvoiceLineRetObjects().iterator();
			while (itr.hasNext())
			{
				InvoiceLineRet line = (InvoiceLineRet)itr.next();
				if (!line.getItemListId().equals("80000013-1231210541"))
				{
					try
					{
						CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCodeByQBListID(_company, line.getItemListId());
						boolean is_reversed = false;
						boolean is_return = false;
						boolean is_open = true;
						if (label == null)
							label = (is_reversed ? "REVERSED: " : (is_return ? "RETURN: " : (is_open ? "ON ACCOUNT: " : ""))) + code.getLabel();
						else
							label += ", " + code.getLabel();
					}
					catch (ObjectNotFoundException x)
					{
						
					}
				}
			}
		}
		catch (Exception x)
		{
			label = "Error: " + x.getMessage();
		}
		
		BigDecimal balance_remaining = new BigDecimal(_invoice.getBalanceRemaining());
		BigDecimal sub_total = new BigDecimal(_invoice.getSubtotal());
		BigDecimal tax_amount = new BigDecimal(_invoice.getTaxAmount());

		String value = "{\"total\" : \"" + balance_remaining.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
						" \"tax\" : \"" + tax_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
						" \"subtotal\" : \"" + sub_total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
						" \"balance\" : \"" + balance_remaining.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
						" \"label\" : \"" + label + "\"," +
						" \"id\" : \"" + _invoice.getTxnNumber() + "\"," +
						" \"open\" : \"" + !_invoice.isPaid() + "\"," +
						" \"reversed\" : \"" + false + "\"," +
						" \"legacy\" : \"" + true + "\"," +
						" \"date\" : \"" + short_date_format.format(_invoice.getTxnDate()) + "\" }";
		return "<order><key>" + _invoice.getId() + "</key><value><![CDATA[" + value + "]]></value></order>";
	}
    
    public static String
    getStatsXML(UKOnlinePersonBean _person, UKOnlineCompanyBean _company)
		throws Exception
    {
		String group_under_care_str = "";

		try
		{
			GroupUnderCareBean group_under_care = _person.getGroupUnderCare();
			group_under_care_str = ScheduleServletPlastique.getXML(group_under_care);
		}
		catch (ObjectNotFoundException x)
		{
			x.printStackTrace();
		}

		String date_str = null;
		Iterator itr = AppointmentBean.getAppointmentsForClient(_person).iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = (AppointmentBean)itr.next();
			String appt_date_str = yui_date_format.format(appointment.getAppointmentDate());
			if (date_str == null)
				date_str = appt_date_str;
			else
				date_str += ',' + appt_date_str;
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("<contact-status>");
		itr = ContactStatusBean.getContactStatus(_person).iterator();
		while (itr.hasNext())
		{
			ContactStatusBean contact_status_obj = (ContactStatusBean)itr.next();
			buf.append(ScheduleServletPlastique.getXML(contact_status_obj));
		}
		buf.append("</contact-status>");
		
		buf.append("<to-do>");
		itr = ToDoItemBean.getToDoItem(_person).iterator();
		while (itr.hasNext())
		{
			ToDoItemBean to_do_item_obj = (ToDoItemBean)itr.next();
			buf.append(ScheduleServletPlastique.getXML(to_do_item_obj));
		}
		buf.append("</to-do>");
		
		buf.append("<care>");
		itr = CareDetailsBean.getCareDetails(_person).iterator();
		while (itr.hasNext())
		{
			CareDetailsBean details_obj = (CareDetailsBean)itr.next();
			buf.append(ScheduleServletPlastique.getXML(details_obj));
		}
		buf.append("</care>");

		buf.append("<notes>");
		itr = PersonNote.getPersonNotes(_person).iterator();
		while (itr.hasNext())
		{
			PersonNote note_obj = (PersonNote)itr.next();
			buf.append(ScheduleServletPlastique.getXML(note_obj));
		}
		buf.append("</notes>");

		buf.append("<review>");
		itr = ClientReviewReason.getClientReviewReasons(_person).iterator();
		while (itr.hasNext())
		{
			ClientReviewReason review_reason = (ClientReviewReason)itr.next();
			buf.append(ScheduleServletPlastique.getXML(review_reason));
		}
		buf.append("</review>");

		buf.append(group_under_care_str);

		buf.append("<soap>");
		itr = SOAPNotesBean.getSOAPNotes(_person).iterator();
		while (itr.hasNext())
		{
			SOAPNotesBean note_obj = (SOAPNotesBean)itr.next();
			buf.append(ScheduleServletPlastique.getXML(note_obj));
		}
		buf.append("</soap>");

		//String comment_str = _person.getCommentString().replaceAll("\"", "\\\\\"");
		//String comment_str = _person.getCommentString().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_');
		String comment_str = "";
		String history_str = _person.getHealthHistoryString().replaceAll("\"", "\\\\\"");
		// " \"h\" : \"" + _person.getHealthHistoryString() + "\"," +
		
		String value = "{\"label\" : \"" + _person.getLabel() + "\"," +
						" \"id\" : \"" + _person.getValue() + "\"," +
						" \"ph\" : \"" + _person.getPhoneNumbersString() + "\"," +
						" \"addr\" : \"" + _person.getAddressesString() + "\"," +
						" \"next\" : \"" + _person.getNextAppointmentString() + "\"," +
						" \"last\" : \"" + _person.getLastAppointmentString() + "\"," +
						" \"ps\" : \"" + _person.getPlanStatusString() + "\"," +
						" \"bal\" : \"" + _person.getBalanceString() + "\"," +
						" \"status\" : \"" + _person.getStatus() + "\"," +
						" \"as\" : \"" + date_str + "\"," +
						" \"pw\" : \"" + _person.getPasswordString() + "\"," +
						" \"cm\" : \"" + comment_str + "\"," +
						" \"file\" : \"" + _person.getFileNumberString() + "\"," +
						" \"email\" : \"" + _person.getEmailString() + "\" }";
		
		return "<person><key>" + _person.getId() + "</key><value><![CDATA[" + value + "]]></value>" + buf.toString() + "<history><![CDATA[" + history_str + "]]></history></person>";
    }

    public static String
    getXML(BlackHoleMemberBean _member)
		throws Exception
    {
		UKOnlinePersonBean person = _member.getPerson();

		String group_relationship_label = "";
		try
		{
			short relationship_id = person.getGroupUnderCare().getMember(person).getRelationshipToPrimaryClient();
			if (relationship_id != GroupUnderCareBean.RELATIONSHIP_SELF_TYPE)
				group_relationship_label = GroupUnderCareBean.getRelationshipString(relationship_id);
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		String value = null;
		if (_member.hasPracticeArea())
		{
			PracticeAreaBean practice_area = _member.getPracticeArea();
			value = "{\"label\" : \"" + person.getLabel() + (group_relationship_label.equals("") ? "" : (" (" + group_relationship_label + ")")) + " - " + practice_area.getLabel() + " - " + CUBean.getUserDateString(_member.getAddDate()) + "\"," +
						" \"cid\" : \"" + person.getId() + "\" }";
		}
		else
			value = "{\"label\" : \"" + person.getLabel() + " - " + CUBean.getUserDateString(_member.getAddDate()) + "\", \"cid\" : \"" + person.getId() + "\" }";

		return "<person><key>" + person.getId() + "</key><value><![CDATA[" + value + "]]></value></person>";
    }

    public static String
    getXMLWithClientInfo(ClientReviewReason _client_review_reason)
		throws Exception
    {
		UKOnlinePersonBean person = _client_review_reason.getPerson();
		String value = "{\"label\" : \"" + _client_review_reason.getLabelWithoutReason().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_') + "\"," +
					" \"cid\" : \"" + person.getId() + "\" }";

		return "<person><key>" + person.getId() + "</key><value><![CDATA[" + value + "]]></value></person>";
    }

    public static String
    getXMLWithClientInfo(ContactStatusBean _contact_status)
		throws Exception
    {
		UKOnlinePersonBean person = _contact_status.getPerson();
		String str = _contact_status.getLabelCallList().replaceAll("\"", "\\\\\"").replaceAll("\\\\", "\\\\\\\\");
		String value = "{\"label\" : \"" + str + "\"," +
					" \"cid\" : \"" + person.getId() + "\" }";

		return "<person><key>" + person.getId() + "</key><value><![CDATA[" + value + "]]></value></person>";
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
	processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
	processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo()
    {
	return "Short description";
    }
    // </editor-fold>
}
