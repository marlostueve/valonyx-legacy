
package com.badiyan.uk.online.servlets;

import com.badiyan.torque.CheckoutOrderline;
import com.badiyan.torque.GroupUnderCareMember;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.beans.CompanyBean;
import com.badiyan.uk.beans.PersonBean;
import com.badiyan.uk.beans.PersonTitleBean;
import com.badiyan.uk.beans.RoleBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.PDF.ClientAppointmentBuilder;
import com.badiyan.uk.online.PDF.EndOfDayLogBuilder;
import com.badiyan.uk.online.PDF.PractitionerAppointmentsBuilder;
import com.badiyan.uk.online.PDF.SOAPNoteReportBuilder;
import com.badiyan.uk.online.PDF.SalesReceiptBuilder;
import com.badiyan.uk.online.beans.AppointmentBean;
import com.badiyan.uk.online.beans.AppointmentTypeBean;
import com.badiyan.uk.online.beans.CareDetailsBean;
import com.badiyan.uk.online.beans.CheckoutCodeBean;
import com.badiyan.uk.online.beans.ClientReviewReason;
import com.badiyan.uk.online.beans.ContactStatusBean;
import com.badiyan.uk.online.beans.GroupUnderCareBean;
import com.badiyan.uk.online.beans.MarketingPlan;
import com.badiyan.uk.online.beans.PersonNote;
import com.badiyan.uk.online.beans.PracticeAreaBean;
import com.badiyan.uk.online.beans.PractitionerScheduleBean;
import com.badiyan.uk.online.beans.PractitionerScheduleItemBean;
import com.badiyan.uk.online.beans.PrintedReceipt;
import com.badiyan.uk.online.beans.ProductWaitList;
import com.badiyan.uk.online.beans.ReviewReason;
import com.badiyan.uk.online.beans.SOAPNotesBean;
import com.badiyan.uk.online.beans.ToDoItemBean;
import com.badiyan.uk.online.beans.UKOnlineCompanyBean;
import com.badiyan.uk.online.beans.UKOnlineLoginBean;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import com.badiyan.uk.online.beans.UKOnlineRoleBean;
import com.badiyan.uk.online.beans.ValeoOrderBean;
import com.badiyan.uk.online.struts.AppointmentAction;
import com.badiyan.uk.online.struts.CheckoutAction;
import com.badiyan.uk.online.struts.ClientNewAction;
import com.badiyan.uk.online.struts.ClientProfileAction;
import com.badiyan.uk.online.util.EncryptionUtils;
import com.badiyan.uk.online.util.SessionCounter;
import com.valeo.qb.data.InvoiceLineRet;
import com.valeo.qb.data.InvoiceRet;
import com.valeo.qbms.data.QBMSCreditCardResponse;
import com.valeo.qbpos.data.TenderRet;
import com.valonyx.beans.PersonSettingsBean;
import com.valonyx.beans.ShoppingCartBean;
import com.valonyx.events.AppointmentEventListener;
import com.valonyx.events.DeletedAppointmentEvent;
import com.valonyx.events.NewAppointmentEvent;
import com.valonyx.events.UpdatedAppointmentEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import org.apache.torque.TorqueException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
*/


/**
 *
 * @author
 * marlo
 */
public class ScheduleServlet2 extends HttpServlet implements AppointmentEventListener {
	
	
	private static SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
	private static SimpleDateFormat short_date_format = new SimpleDateFormat("MM/dd/yy"); // 2/1/2008
	
	private static HashMap<String,UKOnlinePersonBean> auth_key_hash = new HashMap<String,UKOnlinePersonBean>(11);
	//private static HashMap<String,HttpSession> key_session_hash = new HashMap<String,HttpSession>(11);
	
	private static HashMap<String,Date> last_update_hash = new HashMap<String,Date>(11);
	public static HashMap<String,Boolean> needs_update_hash = new HashMap<String,Boolean>(11);
	
	public static UKOnlinePersonBean dr_christine = null;


	protected void processRequest(HttpServletRequest _request, HttpServletResponse _response)
			throws ServletException, IOException {
		
		_response.setContentType("application/json;charset=UTF-8");
		_response.setHeader("Access-Control-Allow-Origin", "*");
		
		PrintWriter writer = _response.getWriter();
		
		try {

			String command = _request.getParameter("command");
			String key = _request.getParameter("key");
			String parameter = _request.getParameter("parameter");
			String arg1 = _request.getParameter("arg1");
			String arg2 = _request.getParameter("arg2");
			String arg3 = _request.getParameter("arg3");
			String arg4 = _request.getParameter("arg4");
			String arg5 = _request.getParameter("arg5");
			String arg6 = _request.getParameter("arg6");
			String arg7 = _request.getParameter("arg7");
			String arg8 = _request.getParameter("arg8");
			
			Date stamper = new Date();
			System.out.println("");
			System.out.println("stamp >" + CUBean.getUserDateString(stamper, "HH:mm:ss.SSS"));
			System.out.println("command >" + command);
			System.out.println("parameter >" + parameter);
			System.out.println("arg1 >" + arg1);
			System.out.println("arg2 >" + arg2);
			System.out.println("arg3 >" + arg3);
			System.out.println("arg4 >" + arg4);
			System.out.println("arg5 >" + arg5);
			System.out.println("arg6 >" + arg6);
			System.out.println("arg7 >" + arg7);
			System.out.println("arg8 >" + arg8);
			
			
			HttpSession session = _request.getSession(true);
			System.out.println("session >" + session.getId());
			
			if (ScheduleServlet2.dr_christine == null) {
				ScheduleServlet2.dr_christine = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(3121);
			}
			
			
			if (command.equals("scheduleFeed")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				//HttpSession session = ScheduleServlet2.getSessionForKey(arg1, _request);
				
				// http://localhost:8080/valeo/ScheduleServlet2?command=feedTest&start=2016-11-01&end=2016-11-02&_=1478277791458
				
				/*
				UKOnlinePersonBean request_person = null;
				try {
					if (arg1 != null) {
						request_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				*/
				
				//CompanyBean selected_bu = this.getSelectedCompany(session, request_person);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5);
				PracticeAreaBean selected_practice_area = null;
				
				String start = _request.getParameter("start");
				String end = _request.getParameter("end");
				
				// the end date is exclusive apparently - construct a String for end minus 1 day
				
				if (end != null) {
					Calendar end_cal = Calendar.getInstance();
					end_cal.setTime(CUBean.getDateFromUserString(end, "yyyy-MM-dd"));
					end_cal.add(Calendar.DATE, -1);
					end = CUBean.getUserDateString(end_cal.getTime(), "yyyy-MM-dd");
				}
				
				
				Date date_arg = null;
				if (start != null && !start.isEmpty()) {
					
					date_arg = CUBean.getDateFromUserString(start, "yyyy-MM-dd");
				
				} else if (arg2 != null && !arg2.isEmpty()) {
					date_arg = CUBean.getDateFromUserString(arg2);
				}
				
				System.out.println("selected_bu >" + selected_bu.getLabel());
				
				Calendar display_date = null;
				if (date_arg != null) {
					display_date = Calendar.getInstance();
					display_date.setTime(date_arg);
				} else {
					display_date = (Calendar)session.getAttribute("display_date"); // probably shouldn't be using the session for this - probz fine to make client responsible for this
				}
				 
				if (display_date == null) {
					display_date = Calendar.getInstance();
					session.setAttribute("display_date", display_date);
				}
				
				String date_str = ScheduleServlet2.date_format.format(display_date.getTime());
				System.out.println("date_str >" + date_str);
				String date_key = key + "" + date_str; // key needs to be some unique id, like the session.  grant to client in auth request
				if ((parameter != null) && parameter.equals("p")) {
					date_key += "p";
				}

				boolean get_all_appointments = false;

				String refresh_tag_name = null;
				Boolean needs_update = (Boolean)ScheduleServlet2.needs_update_hash.get(key);
				if ((needs_update == null) || !needs_update.booleanValue()) {
					refresh_tag_name = (ScheduleServlet2.last_update_hash.get(date_key) == null) ? "status" : "refresh";
				} else {
					System.out.println("NEEDS UPDATE!!!");
					refresh_tag_name = needs_update.booleanValue() ? "status" : "refresh";
					get_all_appointments = true;
					ScheduleServlet2.needs_update_hash.remove(key);
				}

				String return_str = null;
				if ((parameter != null) && parameter.equals("p")) {
					//return_str = "<" + refresh_tag_name + ">" + this.assemblePractitionerScheduleXML(adminCompany, adminPractitioner, display_date, key, get_all_appointments).toString() + "</" + refresh_tag_name + ">";
				} else {
					//return_str = "<" + refresh_tag_name + ">" + this.assembleScheduleXML(adminCompany, adminPracticeArea, display_date, key, get_all_appointments).toString() + "</" + refresh_tag_name + ">";
					
					if (start.equals(end)) {
						return_str = this.assembleScheduleJSON(selected_bu, selected_practice_area, display_date, null, date_key, get_all_appointments);
					} else {
						Calendar end_dt_cal = Calendar.getInstance();
						end_dt_cal.setTime(CUBean.getDateFromUserString(end, "yyyy-MM-dd"));
						return_str = this.assembleScheduleJSON(selected_bu, selected_practice_area, display_date, end_dt_cal, date_key, get_all_appointments);
					}
				}

				//System.out.println("return_str (1) >" + return_str);
				
				writer.println(return_str);
				
				
			} else if (command.equals("getStats")) {
				
				AppointmentBean appointment = AppointmentBean.getAppointment(Integer.parseInt(arg1));
				writer.println(ScheduleServlet2.getStatsJSON(appointment));
				
			} else if (command.equals("resourceSelect")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				PracticeAreaBean selected_practice_area = null;
				
				// arg2 = 3121 // resource id
				
				AppointmentBean appointment = null;
				if (arg5 == null) {
					appointment = new AppointmentBean();
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
					appointment.setPractitioner(practitioner);
					
					SimpleDateFormat resource_select_date_formatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm");

					Calendar selected_start_date = Calendar.getInstance();
					selected_start_date.setTime(resource_select_date_formatter.parse(arg3));
					Calendar selected_end_date = Calendar.getInstance();
					selected_end_date.setTime(resource_select_date_formatter.parse(arg4));
					if (arg3.indexOf("GMT-0600") > -1) {
						selected_start_date.add(Calendar.HOUR, 6);
						selected_end_date.add(Calendar.HOUR, 6);
					} else {
						selected_start_date.add(Calendar.HOUR, 5);
						selected_end_date.add(Calendar.HOUR, 5);
					}
					
					Date selected_date = selected_start_date.getTime();

					//System.out.println("date >" + CUBean.getUserDateString(selected_date));
					//System.out.println("time >" + CUBean.getUserTimeString(selected_date));

					short duration = (short)((selected_end_date.getTimeInMillis() - selected_start_date.getTimeInMillis()) / 60000l);

					//System.out.println("duration >" + duration);

					appointment.setAppointmentDate(selected_date);
					appointment.setCompany(selected_bu);
					appointment.setCreateOrModifyPerson(logged_in_person);
					appointment.setDuration(duration);
					
					// see if there's an appointment template in place for the selected resource/practitioner
					try {
						//PractitionerScheduleBean schedule = PractitionerScheduleBean.getActivePractitionerSchedule(practitioner);
						
						int selected_hour_of_day = selected_start_date.get(Calendar.HOUR_OF_DAY);
						int selected_minute = selected_start_date.get(Calendar.MINUTE);
						
						PractitionerScheduleBean schedule = PractitionerScheduleBean.getPractitionerSchedule(practitioner, selected_date);
						Iterator schedule_items = schedule.getItems(selected_start_date.get(Calendar.DAY_OF_WEEK)).iterator();
						while (schedule_items.hasNext()) {
							PractitionerScheduleItemBean schedule_item = (PractitionerScheduleItemBean)schedule_items.next();
							if (schedule_item.hasTemplate()) {
								int item_start_hour_of_day = schedule_item.getStartHourOfDay();
								int item_start_minute = schedule_item.getStartMinute();
								
								if (selected_hour_of_day >= item_start_hour_of_day && selected_minute >= item_start_minute) {
									
									int item_end_hour_of_day = schedule_item.getStartHourOfDay();
									int item_end_minute = schedule_item.getStartMinute();
									
									if (selected_hour_of_day <= item_end_hour_of_day && selected_minute <= item_end_minute) {
										
										appointment.setType(schedule_item.getAppointmentTypeTemplate());
										if (duration == (short)5) {
											
											Calendar appt_start = Calendar.getInstance();
											appt_start.setTime(selected_date);
											appt_start.set(Calendar.HOUR_OF_DAY, schedule_item.getStartHourOfDay());
											appt_start.set(Calendar.MINUTE, schedule_item.getStartMinute());
											appt_start.set(Calendar.SECOND, 0);
											appt_start.set(Calendar.MILLISECOND, 0);

											Calendar appt_end = Calendar.getInstance();
											appt_end.setTime(selected_date);
											appt_end.set(Calendar.HOUR_OF_DAY, schedule_item.getEndHourOfDay());
											appt_end.set(Calendar.MINUTE, schedule_item.getEndMinute());
											appt_end.set(Calendar.SECOND, 0);
											appt_end.set(Calendar.MILLISECOND, 0);
					
											appointment.setDuration((short)TimeUnit.MILLISECONDS.toMinutes(appt_end.getTimeInMillis() - appt_start.getTimeInMillis()));
										}
										break;
									}
									
								}
							}
						}
						
					} catch (Exception x) {
						x.printStackTrace();
					}
					
				} else {
					appointment = AppointmentBean.getAppointment(Integer.parseInt(arg5));
				}
				
				//System.out.println("selected practitioner >" + practitioner.getLabel());
				
				// arg3 = Fri Nov 18 2016 02:30:00 GMT-0600 (CST)
				
				// GMT-0500 (CDT)
				// GMT-0600 (CST)
				
				//appointment.setType(); // I may be able to calculate the default type to use, that would be cool
				writer.println(this.toJSONResourceSelect(appointment));
				
						
				
				/*
				Calendar display_date = Calendar.getInstance();
				display_date.setTimeInMillis(Long.parseLong(arg2) + 43200000l); // addind a 12 hour offset in milliseconds - pretty lame - wasn't able to quite wrap my brain around the Unix time stamp defaulting to UTC and timezone stuff
				
				//Calendar display_date = (Calendar)session.getAttribute("display_date");
				
				String date_str = ScheduleServlet2.date_format.format(display_date.getTime());
				String date_key = arg1 + "" + date_str; // key needs to be some unique id, like the session.  grant to client in auth request
				
				boolean get_all_appointments = false;

				String refresh_tag_name = null;
				Boolean needs_update = (Boolean)ScheduleServlet2.needs_update_hash.get(key);
				if ((needs_update == null) || !needs_update.booleanValue()) {
					refresh_tag_name = (ScheduleServlet2.last_update_hash.get(date_key) == null) ? "status" : "refresh";
				} else {
					System.out.println("NEEDS UPDATE!!!");
					refresh_tag_name = needs_update.booleanValue() ? "status" : "refresh";
					get_all_appointments = true;
					ScheduleServlet2.needs_update_hash.remove(key);
				}
				
				writer.println(this.assembleScheduleJSON(selected_bu, selected_practice_area, display_date, date_key, get_all_appointments));
				*/
				
			} else if (command.equals("getAppointmentWizardStuff")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				StringBuilder b = new StringBuilder();
				b.append("{\"type\":[");
				Iterator itr = AppointmentTypeBean.getAppointmentTypes(selected_bu, AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE ).iterator();
				boolean needs_comma = false;
				while (itr.hasNext()) {
					AppointmentTypeBean appointment_type = (AppointmentTypeBean)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSONDropdown(appointment_type));
					needs_comma = true;
				}
				b.append("],");
				
				b.append("\"resource\":[");
				
				needs_comma = false;
				Iterator practitioner_itr = PracticeAreaBean.getAllPractitioners().iterator();
				while (practitioner_itr.hasNext()) {
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
					String room_label = practitioner.getFirstNameString();
					if (practitioner.isRoom()) {
						room_label = practitioner.getLastNameString();
					}
					
					if (room_label.equals("TX 1")) {
						room_label = "Lisa";
					} else if (room_label.equals("Leah")) {
						room_label = "Leah - TX3";
					} else if (room_label.equals("Lesya")) {
						room_label = "Lesya - BW2";
					}
					
					if (needs_comma) { b.append(','); }
					//b.append(this.toJSONDropdown(room_label, practitioner.getValue()));
					b.append("{\"label\":\"" + JSONObject.escape(room_label) + "\",\"id\":" + practitioner.getId() + "}");
					needs_comma = true;

				}
				b.append("]}");
				
				
				
				writer.println(b.toString());
				
			} else if (command.equals("userSearch")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				PracticeAreaBean selected_practice_area = null;
				
				int active_filter = Integer.parseInt(arg3);
				
				int limit = 0;
				if (arg4 != null && !arg4.isEmpty()) {
					limit = Integer.parseInt(arg4);
				}

				String sort = null;
				if (arg5 != null && !arg5.isEmpty()) {
					sort = arg5;
				}

				boolean show_details = false;
				if (arg6 != null && !arg6.isEmpty()) {
					show_details = true;
				}
				
				boolean include_inactives = false;
				if (arg7 != null && !arg7.isEmpty()) {
					include_inactives = true;
				}
				
				StringBuffer b = new StringBuffer();
				b.append("{\"user\":[");
				
				Vector vec = new Vector();
				
				if (arg2 != null) {
					if (arg2.length() > 2) {

						//session.setAttribute("keyword", arg2.replaceAll("\\\\", ""));

						String[] param_arr = arg2.split(" ");

						//getPersonsByKeyword(CompanyBean _company, String _keyword, UKOnlinePersonBean _logged_in_person, int _limit)

						for (int i = 0; i < param_arr.length; i++) {
							Vector new_vec = new Vector();
							Vector temp_vec = null;
							if (sort == null) {
								temp_vec = UKOnlinePersonBean.getPersonsByKeyword(selected_bu, JSONObject.escape(param_arr[i]), limit, include_inactives);
							} else {
								temp_vec = UKOnlinePersonBean.getPersonsByKeyword(selected_bu, JSONObject.escape(param_arr[i]), limit, include_inactives);
							}
							System.out.println("temp_vec sizer >" + temp_vec.size());
							if (i == 0) {
								vec.addAll(temp_vec);
							} else {
								Iterator itr = temp_vec.iterator();
								while (itr.hasNext()) {
									UKOnlinePersonBean person = (UKOnlinePersonBean)itr.next();
									if (vec.contains(person)) {
										new_vec.addElement(person);
									}
								}
								vec = new_vec;
							}
						}

					} else if (arg2.length() == 1) {
						vec = UKOnlinePersonBean.getPersonsByKeyword(selected_bu, JSONObject.escape(arg2), logged_in_person, limit, 0, active_filter);
					}
				}
				
				Iterator itr = vec.iterator();
				while (itr.hasNext()) {
					UKOnlinePersonBean person = (UKOnlinePersonBean)itr.next();
					if (show_details) {
						b.append(this.toJSONSearch(person));
					} else {
						String fileNumberStr = person.getFileNumberString();
						if (fileNumberStr.isEmpty()) {
							b.append(this.toJSONDropdown(person.getLabel(), person.getId()));
						} else {
							b.append(this.toJSONDropdown(person.getLabel() + " [" + person.getFileNumberString() + "]", person.getId()));
						}
					}
					
					b.append(itr.hasNext() ? "," : "");
				}
				
				b.append("]}");
				
				System.out.println("retStr >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getAllUsers")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				Vector vec = UKOnlinePersonBean.getPersons(selected_bu);
								
				StringBuffer b = new StringBuffer();
				b.append("{\"user\":[");
				
				Iterator itr = vec.iterator();
				while (itr.hasNext()) {
					PersonBean person = (PersonBean)itr.next();
					b.append(this.toJSONDataExport((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person.getId())));
					b.append(itr.hasNext() ? "," : "");
				}
				
				b.append("]}");
				
				System.out.println("retStr >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getResources")) {
				
				Date now = new Date();
				
				StringBuilder b = new StringBuilder();
				b.append("{\"resource\":[");
				
				
				boolean needs_comma = false;
				Iterator practitioner_itr = PracticeAreaBean.getAllPractitioners().iterator();
				for (int i = 2; practitioner_itr.hasNext(); i++) {
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
					String room_label = practitioner.getFirstNameString();
					if (practitioner.isRoom()) {
						room_label = practitioner.getLastNameString();
					}
					if (room_label.equals("Christine")) {
						room_label = "Christine / Lymph";
					} else if (room_label.equals("TX 1")) {
						//room_label = "Lisa - TX3";
						continue;
					} else if (room_label.equals("Kevin")) {
						room_label = "Pod 1";
					} else if (room_label.equals("TX 4")) {
						room_label = "Bemer - TX4";
					} else if (room_label.equals("TX4")) {
						room_label = "Bemer - TX4";
					} else if (room_label.equals("Lisa")) {
						//room_label = "Lisa - TX2";
						continue;
					} else if (room_label.equals("Leah")) {
						room_label = "Leah - TX3";
					} else if (room_label.equals("Stori")) {
						room_label = "Stori / HRV / Bioscan";
					} else if (room_label.equals("Lesya")) {
						room_label = "Lesya - BW2";
					} else if (room_label.equals("Lymph 1")) {
						room_label = "Lymph / PEMF";
					} else if (room_label.equals("Katie")) {
						room_label = "POD - BW1";
					} else if (room_label.equals("Counseling(C) - TX2")) {
						continue;
					} else if (room_label.equals("Counseling(M) - TX2")) {
						continue;
					} else if (room_label.equals("Neuro - TX4")) {
						room_label = "Angie / Neuro";
					} else if (room_label.equals("Sauna 1")) {
						room_label = "Pod 2";
					} else if (room_label.equals("Sauna 2")) {
						continue;
					} else if (room_label.equals("Bioscan 1")) {
						continue;
					} else if (room_label.equals("PEMF - TX6")) {
						continue;
					} else if (room_label.equals("Lymph 2")) {
						//continue;
					} else if (room_label.equals("HRV / BioScan - TX5")) {
						continue;
					}
					
					
					if (needs_comma) { b.append(','); }
					//b.append(this.toJSONDropdown(room_label, practitioner.getValue()));
					b.append("{\"title\":\"" + JSONObject.escape(room_label) + "\",\"id\":" + practitioner.getId() + "}");
					needs_comma = true;

				}
				b.append("]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("comboSearch")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				int limit = 10;

				StringBuffer b = new StringBuffer();
				b.append("{\"user\":[");
				
				Vector vec = new Vector();
				
				if (arg2 != null && arg2.length() > 2) {

					String[] param_arr = arg2.split(" ");

					for (int i = 0; i < param_arr.length; i++) {
						Vector new_vec = new Vector();
						Vector temp_vec = UKOnlinePersonBean.getPersonsByKeyword(selected_bu, JSONObject.escape(param_arr[i]), limit, false);
						if (i == 0) {
							vec.addAll(temp_vec);
						} else {
							Iterator itr = temp_vec.iterator();
							while (itr.hasNext()) {
								UKOnlinePersonBean person = (UKOnlinePersonBean)itr.next();
								if (vec.contains(person)) {
									new_vec.addElement(person);
								}
							}
							vec = new_vec;
						}
					}
					
				}
				
				Iterator itr = vec.iterator();
				while (itr.hasNext()) {
					UKOnlinePersonBean person = (UKOnlinePersonBean)itr.next();
					b.append(this.toJSONDropdownWithCategory(person.getLabel(), person.getId(), "Client"));
					b.append(itr.hasNext() ? "," : "");
				}
				
				b.append("],");
				b.append("\"inventory\":[");
				
				if (arg2 != null && arg2.length() > 2) {
					itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(selected_bu, arg2, "").iterator();
					while (itr.hasNext()) {
						CheckoutCodeBean code = (CheckoutCodeBean)itr.next();
						b.append(this.toJSONDropdownWithCategory(code.getLabel(), code.getId(), "Inventory"));
						b.append(itr.hasNext() ? "," : "");
					}
				}
				
				b.append("],");
				b.append("\"appointment\":[");
				
				//AppointmentBean.
				
				
				b.append("],");
				b.append("\"vendor\":[");
				b.append("],");
				b.append("\"purchaseOrder\":[");
				b.append("]}");
				
				System.out.println("retStr >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getPersonStats")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean person = null;
						
				if (arg2 == null || arg2.isEmpty()) {
					if (arg3 == null || arg3.isEmpty()) {
						person = logged_in_person;
					} else {
						person = AppointmentBean.getAppointment(Integer.parseInt(arg3)).getClient();
					}
				} else {
					person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				}
				
				int selected_followup_id = 0;
				//if (arg4 != null) {
				//	selected_followup_id = Integer.parseInt(arg4);
				//}
				
				writer.println(this.getStatsJSON(person, selected_bu, selected_followup_id));
				
			} else if (command.equals("saveAppointment")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				writer.println(this.saveAppointment(selected_bu, logged_in_person, arg2));
				
			} else if (command.equals("saveNewClient")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				writer.println(this.saveNewClient(selected_bu, logged_in_person, arg2));
				
			} else if (command.equals("dropAppointment")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				AppointmentBean appointment_obj = AppointmentBean.getAppointment(Integer.parseInt(arg2));
				
				UKOnlinePersonBean selected_practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg5));
				Vector types_for_selected_practitioner = AppointmentTypeBean.getAppointmentTypes(selected_practitioner);
				AppointmentTypeBean appointment_type = appointment_obj.getType();
				if (!types_for_selected_practitioner.contains(appointment_type)) {
					//appointment_obj.save(); // trigger update event to move appointment back
					throw new IllegalValueException("Unable to move appointment.  " + appointment_type.getLabel() + " is not within the practice area for " + selected_practitioner.getLabel());
				}
				
				//System.out.println("appointment_obj found >" + appointment_obj.getLabel());
				Date appt_start_date = new Date();
				appt_start_date.setTime(Long.parseLong(arg3) + appointment_obj.getTimezoneOffsetForTimestampInMillis());
				appointment_obj.setAppointmentDate(appt_start_date);
				appointment_obj.setDuration((short)((Long.parseLong(arg4) - Long.parseLong(arg3)) / 60000l));
				appointment_obj.setPractitioner(selected_practitioner);
				appointment_obj.save();
				
				//System.out.println("date >" + CUBean.getUserDateString(appt_start_date) );
				//System.out.println("time >" + CUBean.getUserTimeString(appt_start_date) );
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"success\"," +
						"\"heading\":\"" + appointment_obj.getLabel() + "\"," +
						"\"text\":\"Moved to " + appointment_obj.getAppointmentDateTimeString() + " - " + appointment_obj.getPractitioner().getLabel() + ".\"}]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("resizeAppointment")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				AppointmentBean appointment_obj = AppointmentBean.getAppointment(Integer.parseInt(arg2));
				
				//System.out.println("appointment_obj found >" + appointment_obj.getLabel());
				Date appt_start_date = new Date();
				appt_start_date.setTime(Long.parseLong(arg3) + appointment_obj.getTimezoneOffsetForTimestampInMillis());
				appointment_obj.setAppointmentDate(appt_start_date);
				appointment_obj.setDuration((short)((Long.parseLong(arg4) - Long.parseLong(arg3)) / 60000l));
				appointment_obj.save();
				
				//System.out.println("date >" + CUBean.getUserDateString(appt_start_date) );
				//System.out.println("time >" + CUBean.getUserTimeString(appt_start_date) );
				
				StringBuilder b = new StringBuilder();
				b.append("{\"message\":[");
				b.append("{\"type\":\"success\"," +
						"\"heading\":\"" + appointment_obj.getLabel() + "\"," +
						"\"text\":\"Moved to " + appointment_obj.getAppointmentDateTimeString() + " - " + appointment_obj.getPractitioner().getLabel() + ".\"}]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("checkin")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg2));
				//session.setAttribute("adminPerson", selected_appointment.getClient());
				

				if (selected_appointment.isCheckedOut()) {
					throw new IllegalValueException(selected_appointment.getLabel() + " is already checked out.");
				}
				if (selected_appointment.isRescheduled()) {
					throw new IllegalValueException(selected_appointment.getLabel() + " has already been rescheduled.");
				}
				if (selected_appointment.isCancelled()) {
					throw new IllegalValueException(selected_appointment.getLabel() + " has already been cancelled.");
				}
				
				
				boolean has_alert = false;

				StringBuilder b = new StringBuilder();
				//buf.append("<alerts>");
				b.append("{\"alert\":[");
				Iterator itr = PersonNote.getPersonNotesToShowOnCheckIn(selected_appointment.getClient()).iterator();
				while (itr.hasNext()) {
					PersonNote note_obj = (PersonNote)itr.next();
					if (has_alert) { b.append(','); }
					b.append(this.toJSON(note_obj));
					has_alert = true;
				}
				b.append("]}");

				selected_appointment.setState(AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
				selected_appointment.save();

				//writer.println("<refresh>" + this.assembleScheduleXML(adminCompany, adminPracticeArea, (Calendar)session.getAttribute("display_date"), session_str, false).toString() + (has_alert ? buf.toString() : "") + "</refresh>");
				
				writer.println(b.toString()); // 12/30/17 - so, I wasn't returning anything here - need to return something
				// if there are alerts, I'll have to add something to the new client to display them
				
			} else if (command.equals("saveClientProfile")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean person = null;
				if (arg3 == null || arg3.isEmpty()) {
					if (arg4 != null) {
						person = AppointmentBean.getAppointment(Integer.parseInt(arg4)).getClient();
					}
				} else {
					person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
				}
				
				writer.println(this.saveClientProfile(selected_bu, logged_in_person, arg2, person));
				
			} else if (command.equals("getPersonStatsFollowup")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				
				short reason_type = ReviewReason.SERVICES_TYPE;
				if (arg3 != null) {
					reason_type = Short.parseShort(arg3);
				}
				
				writer.println(this.getFollowupJSON(client, selected_bu, reason_type));
				
			} else if (command.equals("selectReasonType")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				short reason_type = ReviewReason.SERVICES_TYPE;
				if (arg2 != null) {
					reason_type = Short.parseShort(arg2);
				}
				
				writer.println(this.getFollowupReasonsJSON(selected_bu, reason_type));
				
			} else if (command.equals("addClientReview")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				boolean mark_as_reviewed = (arg3 != null) && arg3.equals("true");
				boolean delete_selected = (arg4 != null) && arg4.equals("true");
				
				writer.println(this.saveFollowUp(logged_in_person, arg2, mark_as_reviewed, delete_selected));
				
			} else if (command.equals("selectReviewItem")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();

				ClientReviewReason client_review_reason = ClientReviewReason.getClientReviewReason(Integer.parseInt(arg2));
				writer.println(this.toJSONDetail(logged_in_person, client_review_reason));
				
			} else if (command.equals("getPersonStatsSOAP")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				
				writer.println(this.getSOAPJSON(client));
				
			} else if (command.equals("selectSOAP")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();

				SOAPNotesBean soap = SOAPNotesBean.getSOAPNotes(Integer.parseInt(arg2));
				writer.println(this.toJSONDetail(soap));
				
			} else if (command.equals("saveSOAP")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean person = null;
				if (arg3 == null || arg3.isEmpty()) {
					person = AppointmentBean.getAppointment(Integer.parseInt(arg4)).getClient();
				} else {
					person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
				}
				
				writer.println(this.saveSOAP(selected_bu, logged_in_person, arg2, person));
				
			} else if (command.equals("deleteSoap")) {
				
				ScheduleServlet2.verifyKey(arg1);
				
				SOAPNotesBean soap_note_obj = SOAPNotesBean.getSOAPNotes(Integer.parseInt(arg2));
				UKOnlinePersonBean person = soap_note_obj.getPerson();
				SOAPNotesBean.delete(soap_note_obj.getId());
				
				String heading = "SOAP Note Deleted";
				String text = "SOAP Note deleted for " + person.getLabel() + ".";

				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"" + heading + "\",\"text\":\"" + JSONObject.escape(text) + "\"}],");
				b.append("\"soap\":[");
				boolean needs_comma = false;
				Iterator itr = SOAPNotesBean.getSOAPNotes(person).iterator();
				while (itr.hasNext()) {
					SOAPNotesBean obj = (SOAPNotesBean)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(obj));
					needs_comma = true;
				}
				b.append("]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("printSOAP")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				SOAPNotesBean note = SOAPNotesBean.getSOAPNotes(Integer.parseInt(arg2));
				UKOnlinePersonBean create_person = null;
				try {
					create_person = note.getCreatePerson();
				} catch (ObjectNotFoundException x) { }
				String soap_report_url = CUBean.getProperty("cu.webDomain") + "/resources/pdf/" + SOAPNoteReportBuilder.generateSOAPNote(selected_bu, note.getPerson(), create_person, note.getCreationDate(), note);
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"\",\"pdfURL\":\"" + JSONObject.escape(soap_report_url) + "\"}]}");
				writer.println(b.toString());
			
			} else if (command.equals("apptReport")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean client = null;
				if (arg2 == null || arg2.isEmpty()) {
					client = AppointmentBean.getAppointment(Integer.parseInt(arg3)).getClient();
				} else {
					client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				}
				
				/*
				command >apptReport
				parameter >null
				arg1 >A6B43AB5B683EB45187C9A7210A8B9E8
				arg2 >null
				arg3 >94459
				arg4 >02/24/17
				arg5 >false
				arg6 >
				arg7 >true
				*/
				
				Date start_date = null;
				Date end_date = null;

				if (arg4 != null && !arg4.isEmpty() && !(arg5 != null && arg5.equals("true"))) {
					start_date = CUBean.getDateFromUserString(arg4);
				}
				if (arg6 != null && !arg6.isEmpty() && !(arg7 != null && arg7.equals("true"))) {
					end_date = CUBean.getDateFromUserString(arg6);
				}
				
				String appointment_report_url = CUBean.getProperty("cu.webDomain") + "/resources/pdf/" + ClientAppointmentBuilder.generateClientAppointmentWorksheet(selected_bu, client, start_date, end_date);
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"\",\"pdfURL\":\"" + JSONObject.escape(appointment_report_url) + "\"}]}");
				writer.println(b.toString());
				
			} else if (command.equals("pracReport")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				Date report_date = null;
				if ( arg2 != null && !arg2.isEmpty() ) {
					report_date = CUBean.getDateFromUserString(arg2);
				}
				
				String practitioner_report_url = CUBean.getProperty("cu.webDomain") + "/resources/pdf/" + PractitionerAppointmentsBuilder.generatePractitionerAppointmentWorksheet(selected_bu, logged_in_person, report_date);
				//writer.println("<prac_report_url><![CDATA[" + practitioner_report_url + "]]></prac_report_url>");
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"\",\"pdfURL\":\"" + JSONObject.escape(practitioner_report_url) + "\"}]}");
				writer.println(b.toString());
				
			} else if (command.equals("searchProductWaitList")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				String search_str = arg2;

				//session.setAttribute("invSearchStr", item_search_str); // never rely on a session for anything
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"waitList\":[");
				Iterator itr = ProductWaitList.searchProductWaitList(selected_bu, search_str, 30).iterator();
				while (itr.hasNext()) {
					ProductWaitList wait_list_entry = (ProductWaitList)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(wait_list_entry));
					needs_comma = true;
				}
				b.append("]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("getRecentPurchasesAndWaitList")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean client = null;
				if (arg2 == null || arg2.isEmpty()) {
					client = AppointmentBean.getAppointment(Integer.parseInt(arg3)).getClient();
				} else {
					client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				}
				
				int limit = 12;
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"recentPurchase\":[");
				Iterator itr = client.getRecentMostCommonPurchasesInventoryOnly().iterator();
				for (int i = 0; (i < limit) && itr.hasNext(); i++) {
					try {
						com.badiyan.torque.CheckoutCode code = (com.badiyan.torque.CheckoutCode)itr.next();
						if ( (code.getIsActive() == (short)1) && (code.getCompanyId() == selected_bu.getId()) ) {
							if (needs_comma) { b.append(","); }
							b.append(this.toJSONShort(code));
							needs_comma = true;
						}
						
					} catch (java.util.ConcurrentModificationException y) { // this seems a little weird
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("],");
				needs_comma = false;
				b.append("\"waitList\":[");
				itr = ProductWaitList.getProductWaitList(client, true).iterator();
				while (itr.hasNext()) {
					ProductWaitList wait_entry = (ProductWaitList)itr.next();
					if (wait_entry.isActive()) {
						if (needs_comma) { b.append(","); }
						b.append(this.toJSON(wait_entry));
						needs_comma = true;
					}
				}
				b.append("]}");
				writer.println(b.toString());
				
			} else if (command.equals("searchCheckoutCodes")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean client = null;
				if (arg2 == null || arg2.isEmpty()) {
					client = AppointmentBean.getAppointment(Integer.parseInt(arg3)).getClient();
				} else {
					client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				}
				
				String item_search_str = arg4;

				//session.setAttribute("invSearchStr", item_search_str); // never rely on a session for anything
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"searchResult\":[");
				Iterator itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(selected_bu, item_search_str, "", 12).iterator();
				while (itr.hasNext()) {
					try {
						
						CheckoutCodeBean code = (CheckoutCodeBean)itr.next();
						if (code.isSynced()) {
							if (needs_comma) {
								b.append(",");
							}
							b.append(this.toJSONShort(code));
							needs_comma = true;
						}
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("addToProductWaitList")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean client = null;
				if (arg2 == null || arg2.isEmpty()) {
					client = AppointmentBean.getAppointment(Integer.parseInt(arg3)).getClient();
				} else {
					client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				}
				
				CheckoutCodeBean product_to_add = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg4));
				
				ProductWaitList.addProductWaitListEntry(client, product_to_add, logged_in_person);
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"" + JSONObject.escape(client.getLabel()) + " added to the waitlist for " + JSONObject.escape(product_to_add.getLabel()) + "\",");
				
				b.append("\"waitList\":[");
				Iterator itr = ProductWaitList.getProductWaitList(client, true).iterator();
				boolean needs_comma = false;
				while (itr.hasNext()) {
					ProductWaitList wait_entry = (ProductWaitList)itr.next();
					if (wait_entry.isActive()) {
						if (needs_comma) { b.append(","); }
						b.append(this.toJSON(wait_entry));
						needs_comma = true;
					}
				}
				b.append("]}]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("deleteProductWaitEntry")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				int entry_id = Integer.parseInt(arg2);
				
				ProductWaitList entry_to_delete = ProductWaitList.getProductWaitListEntry(entry_id);
				UKOnlinePersonBean waiting_person = entry_to_delete.getWaitingPerson();
				ProductWaitList.removeProductWaitList(entry_id);
				
				boolean show_all_entries = false;
				if ((arg3 != null) && !arg3.isEmpty()) {
					show_all_entries = true;
				}
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Waitlist entry removed.\",\"text\":\"" + entry_to_delete.getLabel() + " removed.\",");
				
				b.append("\"waitList\":[");
				Iterator itr = null;
				if (show_all_entries) {
					itr = ProductWaitList.searchProductWaitList(selected_bu, "", 30).iterator();
				} else {
					itr = ProductWaitList.getProductWaitList(waiting_person, true).iterator();
				}
				
				boolean needs_comma = false;
				while (itr.hasNext()) {
					ProductWaitList wait_entry = (ProductWaitList)itr.next();
					if (wait_entry.isActive()) {
						if (needs_comma) { b.append(","); }
						b.append(this.toJSON(wait_entry));
						needs_comma = true;
					}
				}
				b.append("]}]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("cancel")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg2));

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
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"success\"," +
						"\"heading\":\"Appointment Canceled\"," +
						"\"text\":\"" + selected_appointment.getLabel() + " canceled.\"" +
						"}");
				b.append("]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("delete")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				System.out.println("person doing delete >" + logged_in_person.getLabel());
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				AppointmentBean delete_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg2));
				Date appointment_date = delete_appointment.getAppointmentDate();
				
				int practitioner_id = 0;
				try {
					practitioner_id = delete_appointment.getPractitioner().getId();
				} catch (Exception x) {}
				int practice_area_id = 0;
				try
				{
					practice_area_id = delete_appointment.getPracticeAreaId();
				}
				catch (Exception x)
				{
				}
				
				try {
					// delete any children also
					Iterator itr = delete_appointment.getChildren().iterator();
					if (itr.hasNext()) {
						AppointmentBean child_appt = (AppointmentBean)itr.next();
						System.out.println("was going to delete appt >" + child_appt.getId());
						//AppointmentBean.delete(child_appt.getId());
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				AppointmentBean.delete(Integer.parseInt(arg2));
				
				last_update_hash.clear();

				String key1 = selected_bu.getId() + "|" + CUBean.getUserDateString(appointment_date);
				String key2 = selected_bu.getId() + "|" + practice_area_id + "|" + CUBean.getUserDateString(appointment_date);

				Calendar start_of_week = Calendar.getInstance();
				start_of_week.setTime(appointment_date);
				Calendar end_of_week = Calendar.getInstance();
				end_of_week.setTime(appointment_date);

				start_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				end_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

				String key3 = selected_bu.getId() + "|" + practitioner_id + "|" + CUBean.getUserDateString(start_of_week.getTime()) + "|" + CUBean.getUserDateString(end_of_week.getTime());

				AppointmentBean.appointment_hash.remove(key1);
				AppointmentBean.appointment_hash.remove(key2);
				AppointmentBean.appointment_hash.remove(key3);

				/*
				StringBuffer returnBuf;
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				if (command.equals("delete"))
					returnBuf = this.assembleScheduleXML(selected_bu, adminPracticeArea, display_date, session_str, true);
				else
					returnBuf = this.assemblePractitionerScheduleXML(selected_bu, adminPractitioner, display_date, session_str, true);

				String date_str = ScheduleServlet.date_format.format(display_date.getTime());
				*/
				

				//String key = session_str + "" + date_str;
				//ScheduleServlet.last_update_hash.put(key, new Date());

				//writer.println("<status>" + returnBuf.toString() + "</status>");
				
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"success\"," +
						"\"heading\":\"Appointment Deleted\"," +
						"\"text\":\"" + delete_appointment.getLabel() + " appointment deleted.\"" +
						"}");
				b.append("]}");
				writer.println(b.toString());

				Date update_timestamp = new Date();
				AppointmentBean.appointment_update_hash.put(key1, update_timestamp);
				AppointmentBean.appointment_update_hash.put(key2, update_timestamp);
				AppointmentBean.appointment_update_hash.put(key3, update_timestamp);
				

			} else if (command.equals("getNewUserFormData")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();

				StringBuilder b = new StringBuilder();
				b.append("{\"marketingPlan\":[");
				Iterator itr = MarketingPlan.getMarketingPlans(selected_bu).iterator();
				boolean needs_comma = false;
				while (itr.hasNext()) {
					MarketingPlan marketing_plan = (MarketingPlan)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSONDropdown(marketing_plan.getLabel(), marketing_plan.getId()));
					needs_comma = true;
				}
				b.append("],");
				b.append("\"nextFileNumber\":" + UKOnlinePersonBean.getNextFileNumber(selected_bu) + "}");
				
				writer.println(b.toString());
				
			} else if (command.equals("sendChatMessage")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				Vector practitioners = UKOnlinePersonBean.getPractitioners(selected_bu);
				Vector staff = UKOnlinePersonBean.getCashiers(selected_bu);
				
				UKOnlinePersonBean send_to_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				
				// send to practitioner app
				
				String message_json = PractitionerServlet.messageToJSON(logged_in_person, send_to_person, "Message from " + logged_in_person.getLabel(), arg3, false);
				System.out.println("message_json >" + message_json);
				System.out.println("send_to_person.getValue() >" + send_to_person.getValue());
				
				boolean connected = true; // assume
				
				try {
					PractitionerAppSocketServlet.sendMessage(send_to_person.getValue(), message_json);
				} catch (IllegalValueException x) {
					x.printStackTrace();
					//throw new IllegalValueException("Unable to send message.  " + send_to_person.getFirstNameString() + " is not connected.(1)");
					connected = false;
				}
				
				// send to valonynx client
				
				message_json = this.messageToJSON(logged_in_person, send_to_person, "", arg3, false);
				
				// get the key for the specified person
				
				Vector keys_for_person = ScheduleServlet2.getKeysForPerson(send_to_person);
				if (keys_for_person.isEmpty()) {
					if (!connected) {
						throw new IllegalValueException("Unable to send message.  " + send_to_person.getFirstNameString() + " is not connected.(2)");
					}
				} else {
				
					Iterator itr = keys_for_person.iterator();
					while (itr.hasNext()) {
						String key_for_person = (String)itr.next();
						try {
							//PractitionerAppSocketServlet.sendMessage(send_to_person.getValue(), message_json);
							ScheduleSocketServlet.sendMessage(key_for_person, message_json);
						} catch (IllegalValueException x) {
							x.printStackTrace();
						}
					}
				}
				
				writer.println("{\"message\":[{\"type\":\"success\"}]}");
				
			} else if (command.equals("getChatUsers")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				Vector practitioners = UKOnlinePersonBean.getPractitioners(selected_bu);
				Vector staff = UKOnlinePersonBean.getCashiers(selected_bu);
				
				StringBuilder b = new StringBuilder();
				b.append("{\"chatUser\":[");
				boolean needs_comma = false;
				Iterator itr = practitioners.iterator();
				while (itr.hasNext()) {
					UKOnlinePersonBean chat_user = (UKOnlinePersonBean)itr.next();
					System.out.println("found practitioner >" + chat_user.getLabel());
					if (!logged_in_person.equals(chat_user) && chat_user.isActive() && !chat_user.isRoom()) {
						if (needs_comma) { b.append(','); }
						//b.append(this.toJSONDropdown(chat_user.getLabel(), chat_user.getId()));
						b.append(this.toJSONChat(chat_user));
						needs_comma = true;
					}
				}
				itr = staff.iterator();
				while (itr.hasNext()) {
					UKOnlinePersonBean chat_user = (UKOnlinePersonBean)itr.next();
					System.out.println("found staff >" + chat_user.getLabel());
					if (!logged_in_person.equals(chat_user) && chat_user.isActive()) {
						if (needs_comma) { b.append(','); }
						//b.append(this.toJSONDropdown(chat_user.getLabel(), chat_user.getId()));
						b.append(this.toJSONChat(chat_user));
						needs_comma = true;
					}
				}
				b.append("]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("getCheckoutCodesByDesc")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				int limit = -1;
				if (arg4 != null && !arg4.isEmpty()) {
					limit = Integer.parseInt(arg4);
				}
				
				boolean needs_comma = false;
				StringBuilder b = new StringBuilder();
				b.append("{\"code\":[");
				Iterator code_itr = CheckoutCodeBean.getCheckoutCodes(selected_bu, arg2, limit).iterator();
				while (code_itr.hasNext()) {
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					if (code.isActive()) { // added 3/8/19 (!!)
						if (code.isSynced() || !selected_bu.getQuickBooksSettings().isQuickBooksFSEnabled()) {
							if (needs_comma) { b.append(','); }
							b.append(this.toJSONDropdown(code.getLabel(), code.getId()));
							needs_comma = true;
						}
					}
				}
				b.append("]}");
				writer.println(b.toString());
				
			} else if (command.equals("getCheckoutDetails") || command.equals("getCheckoutDetailsCID")) {
				
				// try to print a barcode
				
				/*
				try {
					//Create the barcode bean
					Code39Bean bean = new Code39Bean();

					final int dpi = 150;

					//Configure the barcode generator
					bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); //makes the narrow bar 
																	 //width exactly one pixel
					bean.setWideFactor(3);
					bean.doQuietZone(false);

					//Open output file
					File outputFile = new File("/home/marlo/Downloads/out.jpg");
					OutputStream out = new FileOutputStream(outputFile);
					try {
						//Set up the canvas provider for monochrome JPEG output 
						BitmapCanvasProvider canvas = new BitmapCanvasProvider(
								out, "image/jpeg", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

						//Generate the barcode
						bean.generateBarcode(canvas, "123456");

						//Signal end of generation
						canvas.finish();
					} finally {
						out.close();
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				*/
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				//HttpSession session = ScheduleServlet2.getSessionForKey(arg1, _request);
				
				session.removeAttribute("charge_response");

				System.out.println("");
				System.out.println("getCheckoutDetails -- ");

				UKOnlinePersonBean client;
				AppointmentBean selected_appointment = null;
				
				int defaultPractitionerId = 0;

				if (command.equals("getCheckoutDetails")) {
					selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg2));
					client = selected_appointment.getClient();
					session.setAttribute("adminAppointment", selected_appointment); // stop doing session stuff.  I can't rely on this
					defaultPractitionerId = selected_appointment.getPractitionerId();
				} else {
					client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
					session.removeAttribute("adminAppointment");
				}

				session.setAttribute("adminPerson", client);
				
				GroupUnderCareBean group_under_care = null;
				try {
					group_under_care = client.getGroupUnderCare();
				} catch (ObjectNotFoundException x) { }
				
				StringBuilder b = new StringBuilder();
				b.append("{\"label\":\"" + JSONObject.escape(client.getLabel()) + "\",");
				b.append("\"p\":" + defaultPractitionerId + ",");
				try {
					PersonSettingsBean settings = PersonSettingsBean.getPersonSettings(client, true);
					if (settings.isNSIPMClient()) {
						BigDecimal productDiscount = new BigDecimal(10);
						BigDecimal serviceDiscount = new BigDecimal(10);
						b.append("\"serviceDiscountPercentage\":\"10\",");
						b.append("\"productDiscountPercentage\":\"10\",");
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				if (selected_appointment != null) {
					b.append("\"selectedAppointment\":" + selected_appointment.getId() + ",");
				}
				b.append("\"previous\":\"" + client.getBalanceString() + "\",");
				b.append("\"group\":[");
				if (group_under_care != null) {
					b.append(this.toJSON(group_under_care));
				}
				b.append("],");
				
				
				
				//StringBuilder open_orderlines_buf = new StringBuilder();
				//open_orderlines_buf.append("<open-orderlines>");
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
							open_orderlines_buf.append(ScheduleServlet.getXML(open_order_obj, orderline));
					}
				}
				 */
				//open_orderlines_buf.append("</open-orderlines>");
				
				//StringBuilder open_orders_buf = new StringBuilder();
				//open_orders_buf.append("<open-orders>");
				
				b.append("\"openOrder\":[");
				boolean needs_comma = false;
	
				Calendar date_range_for_open_orders = Calendar.getInstance();
				date_range_for_open_orders.add(Calendar.YEAR, -5);

				Vector orders_to_display_as_open_orders = client.getRecentOrders(selected_bu);
				/*
				Calendar three_months_ago = Calendar.getInstance();
				three_months_ago.add(Calendar.MONTH, -3);
				Vector orders_to_display_as_open_orders = ValeoOrderBean.getRecentOrders(client, three_months_ago.getTime());
				*/
				Iterator open_orders_itr = client.getOpenOrders(date_range_for_open_orders.getTime()).iterator();
				while (open_orders_itr.hasNext())
				{
					ValeoOrderBean open_order_obj = (ValeoOrderBean)open_orders_itr.next();
					if (!orders_to_display_as_open_orders.contains(open_order_obj)) {
						orders_to_display_as_open_orders.addElement(open_order_obj);
					}
				}

				Iterator itr = orders_to_display_as_open_orders.iterator();
				while (itr.hasNext()) {
					ValeoOrderBean obj = (ValeoOrderBean)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(obj));
					needs_comma = true;
					//open_orders_buf.append(ScheduleServlet.getXML(obj));
				}
				
				if (client.hasQBFSListID()) {
					itr = InvoiceRet.getOldOpenInvoices(client).iterator();
					while (itr.hasNext())
					{
						InvoiceRet invoice_obj = (InvoiceRet)itr.next();
						//open_orders_buf.append(ScheduleServlet.getXML(invoice_obj, selected_bu));
						if (needs_comma) { b.append(','); }
						b.append(this.toJSON(invoice_obj, selected_bu));
						needs_comma = true;
					}
				}

				//open_orders_buf.append("</open-orders>");
				b.append("],");
				
				
				
				
				/*
				if (group_under_care != null) {
					b.append(this.toJSON(group_under_care));
				}
				*/
				
				




				
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

				//StringBuilder open_payments_buf = new StringBuilder();
				//open_payments_buf.append("<open-payments>");
				
				b.append("\"openPayment\":[");
				needs_comma = false;
				Iterator open_payments_itr = QBMSCreditCardResponse.getUnusedAuthorizedResponsesForToday(client).iterator();
				while (open_payments_itr.hasNext()) {
					QBMSCreditCardResponse auth_response = (QBMSCreditCardResponse)open_payments_itr.next();
					//open_payments_buf.append(ScheduleServlet.getXML(auth_response));
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(auth_response));
					needs_comma = true;
				}
				//open_payments_buf.append("</open-payments>");
				b.append("],");
				
				
				
				
				//b.append("\"cart\":[");
				needs_comma = false;
				//StringBuilder shopping_cart_buf = new StringBuilder();
				ShoppingCartBean cart = null;
				System.out.println("ShoppingCartBean.hasNonEmptyCart(client) >" + ShoppingCartBean.hasNonEmptyCart(client));
				if (ShoppingCartBean.hasNonEmptyCart(client)) {
					cart = ShoppingCartBean.getCart(client);
				} else {
					ShoppingCartBean unassigned_cart = ShoppingCartBean.getUnassignedCart(logged_in_person);
					System.out.println("getting unassigned cart >");
					unassigned_cart.printCart();
					if (!unassigned_cart.isEmpty()) { // if the unassigned cart has stuff in it.
						System.out.println("unassigned cart has stuff in it. >");
						ShoppingCartBean.assignCartTo(unassigned_cart, client, logged_in_person);
						cart = unassigned_cart;
					}
				}
				if (cart != null) {
					/*
					Iterator cartItr = cart.getCheckoutOrderlines();
					if (cartItr.hasNext()) {
						//shopping_cart_buf.append("<shopping-cart>");
						while (cartItr.hasNext()) {
							CheckoutOrderline cartLine = (CheckoutOrderline)cartItr.next();
							//shopping_cart_buf.append(ScheduleServlet.getXML(cartLine));
							if (needs_comma) { b.append(','); }
							b.append(this.toJSON(cartLine));
							needs_comma = true;
						}
						//shopping_cart_buf.append("</shopping-cart>");
					}
					*/
					
					// meh
					//String cartJSON = this.toJSON(cart, null, false, client.getBalance());
					String cartJSON = this.toJSON(cart, null, false, null);
					b.append(cartJSON);
					if (!cartJSON.isEmpty()) {
						b.append(",");
					}
					
				}
				//b.append("],");
				
				
				
				
				
				//System.out.println("shopping_cart_buf >" + shopping_cart_buf.toString());
				
				//String xml_str = "<checkout p=\"" + ((selected_appointment == null) ? 0 : selected_appointment.getPractitionerId()) + "\">" + ScheduleServlet.getXMLIncludeFileType(client) + "<previous>" + client.getBalanceString() + "</previous>" + group_under_care_str + open_orderlines_buf.toString() + open_orders_buf.toString() + payment_plan_str + open_payments_buf.toString() + shopping_cart_buf.toString() + "</checkout>";
				//System.out.println(xml_str);
				
				
				b.append("\"id\":" + client.getId() + "}");
				
				
				//writer.println(xml_str);
				writer.println(b.toString());
				
			} else if (command.equals("addToCart")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				// is there a client selected at this point??  if so, add this to their cart
				
				ShoppingCartBean cart = null;
				if (arg3 == null || arg3.isEmpty()) {
					cart = ShoppingCartBean.getUnassignedCart(logged_in_person); // grab the generic cart
				} else {
					UKOnlinePersonBean selected_client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
					cart = ShoppingCartBean.getCart(selected_client);
				}
				
				CheckoutCodeBean selected_code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg2));
				com.badiyan.torque.CheckoutOrderline orderline = null;
				if (arg4 != null && arg4.equals("true")) {
					// this is a return
					orderline = cart.addSkipAlreadyInCartCheck(selected_code, true);
				} else {
					orderline = cart.addSkipAlreadyInCartCheck(selected_code);
				}
						
				writer.println(this.toJSON(orderline));
				
			} else if (command.equals("updateQty")) {
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				
				ShoppingCartBean cart = null;
				if (arg2 == null || arg2.isEmpty()) {
					cart = ShoppingCartBean.getUnassignedCart(logged_in_person); // grab the generic cart
				} else {
					UKOnlinePersonBean selected_client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
					cart = ShoppingCartBean.getCart(selected_client);
				}
				
				CheckoutCodeBean selected_code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg3));
				cart.updateQty(selected_code, Integer.parseInt(arg4), new BigDecimal(arg5));
				
				writer.println("{\"message\":[]}");
				
			} else if (command.equals("updateAmount")) {
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				
				ShoppingCartBean cart = null;
				if (arg2 == null || arg2.isEmpty()) {
					cart = ShoppingCartBean.getUnassignedCart(logged_in_person); // grab the generic cart
				} else {
					UKOnlinePersonBean selected_client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
					cart = ShoppingCartBean.getCart(selected_client);
				}
				
				CheckoutCodeBean selected_code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg3));
				cart.updateAmount(selected_code, Integer.parseInt(arg4), new BigDecimal(arg5));
				
				writer.println("{\"message\":[]}");
				
			} else if (command.equals("selectPractitionerForOrderline")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				
				UKOnlinePersonBean selected_client_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				UKOnlinePersonBean selected_practitioner_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg4));
				
				System.out.println("selected_client_obj >" + selected_client_obj.getLabel());
				
				// is there a client selected at this point??  if so, add this to their cart
				
				ShoppingCartBean cart = ShoppingCartBean.getCart(selected_client_obj);
				cart.printCart();
				
				CheckoutCodeBean selected_code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg3));
				//cart.remove(selected_code, Integer.parseInt(arg4));
				//cart.printCart();
				
				int row = Integer.parseInt(arg5);
				Iterator itr = cart.getCheckoutOrderlines();
				System.out.println("itr.hasNext() >" + itr.hasNext());
				for (int i = 1; itr.hasNext(); i++) {
					com.badiyan.torque.CheckoutOrderline orderline_obj = (com.badiyan.torque.CheckoutOrderline)itr.next();
					System.out.println("orderline_obj >" + orderline_obj);
					if (i == row) {
						System.out.println("selected_practitioner_obj.getId() >" + selected_practitioner_obj.getId());
						orderline_obj.setPractitionerId(selected_practitioner_obj.getId());
					}
					
				}
				
				writer.println(this.toJSON(cart, selected_code.getLabel() + " removed from current charges.", true, null));
				
			} else if (command.equals("removeFromCart")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				// is there a client selected at this point??  if so, add this to their cart
				
				ShoppingCartBean cart = null;
				if (arg2 == null || arg2.isEmpty()) {
					cart = ShoppingCartBean.getUnassignedCart(logged_in_person); // grab the generic cart
				} else {
					UKOnlinePersonBean selected_client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
					cart = ShoppingCartBean.getCart(selected_client);
				}
				
				CheckoutCodeBean selected_code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg3));
				cart.remove(selected_code, Integer.parseInt(arg4));
				//cart.printCart();
				
				writer.println(this.toJSON(cart, selected_code.getLabel() + " removed from current charges.", true, null));
				
			} else if (command.equals("addCredit")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				// is there a client selected at this point??  if so, add this to their cart
				
				ShoppingCartBean cart = null;
				if (arg2 == null || arg2.isEmpty()) {
					cart = ShoppingCartBean.getUnassignedCart(logged_in_person); // grab the generic cart
				} else {
					UKOnlinePersonBean selected_client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
					cart = ShoppingCartBean.getCart(selected_client);
				}
				
				if (arg4 == null || arg4.isEmpty()) {
					if (arg5 == null || arg5.isEmpty()) {
						throw new IllegalValueException("Please choose a tender amount.");
					}
				}
				
				// create a TenderRet object
				
				TenderRet tender_obj = new TenderRet();
				
				short methodType = Short.parseShort(arg3);
				
				/*
				if (methodType == (short)7) {
					tender_obj.setCreditCardType("Visa");
					tender_obj.setType(TenderRet.CREDIT_CARD_TENDER_TYPE);
				} else if (methodType == (short)8) {
					tender_obj.setCreditCardType("Mastercard");
					tender_obj.setType(TenderRet.CREDIT_CARD_TENDER_TYPE);
				} else if (methodType == (short)9) {
					tender_obj.setCreditCardType("Discover");
					tender_obj.setType(TenderRet.CREDIT_CARD_TENDER_TYPE);
				} else {
					tender_obj.setType(methodType);
				}
				*/
				
				tender_obj.setType(methodType);
				
				if (arg4 != null && !arg4.isEmpty()) {
					tender_obj.setAmount(Float.parseFloat(arg4.replace(",", "")));
				}
				if (arg5 != null && !arg5.isEmpty()) {
					tender_obj.setChangeAmount(Float.parseFloat(arg5.replace(",", "")));
				}
				cart.add(tender_obj);
				//cart.printCart();
				
				writer.println(this.toJSON(cart, null, true, null));
				
			} else if (command.equals("removeCredit")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				// is there a client selected at this point??  if so, add this to their cart
				
				ShoppingCartBean cart = null;
				if (arg2 == null || arg2.isEmpty()) {
					cart = ShoppingCartBean.getUnassignedCart(logged_in_person); // grab the generic cart
				} else {
					UKOnlinePersonBean selected_client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
					cart = ShoppingCartBean.getCart(selected_client);
				}
				
				cart.removeTenderAt(Integer.parseInt(arg4));
				
				writer.println(this.toJSON(cart, "Payment removed.", true, null));
				
			} else if (command.equals("doCheckout")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				//HttpSession session = ScheduleServlet2.getSessionForKey(arg1, _request);
				
				Cookie[] cookies = _request.getCookies();
				Cookie workstation_cookie = null;
				Cookie primary_cookie = null;

				if (cookies != null) {
					for (int i = 0; i < cookies.length; i++)
					{
						Cookie cookie = cookies[i];
						if (cookie.getName().equals(CheckoutAction.workstation_cookie_name))
							workstation_cookie = cookie;
					}
				}

				if (workstation_cookie == null) {
					UUID generated_uuid = UUID.randomUUID();
					workstation_cookie = new Cookie(CheckoutAction.workstation_cookie_name, generated_uuid.toString());
					workstation_cookie.setMaxAge(365 * 24 * 60 * 60); // expires in one year provided server time matches client time.  may be an issue for several reasons...
					_response.addCookie(workstation_cookie);
				}
				
				//arg2 = "[{\"name\":\"qty1tblCharges\",\"value\":\"22\"},{\"name\":\"inputName1tblCharges\",\"value\":\"6.12\"},{\"name\":\"sel1tblCharges\",\"value\":\"0\"},{\"name\":\"hidden1tblCharges\",\"value\":\"2424\"},{\"name\":\"qty2tblCharges\",\"value\":\"46\"},{\"name\":\"inputName2tblCharges\",\"value\":\"4.05\"},{\"name\":\"sel2tblCharges\",\"value\":\"0\"},{\"name\":\"hidden2tblCharges\",\"value\":\"2425\"},{\"name\":\"qty3tblCharges\",\"value\":\"15\"},{\"name\":\"inputName3tblCharges\",\"value\":\"2.19\"},{\"name\":\"sel3tblCharges\",\"value\":\"0\"},{\"name\":\"hidden3tblCharges\",\"value\":\"2418\"},{\"name\":\"qty4tblCharges\",\"value\":\"1.5\"},{\"name\":\"inputName4tblCharges\",\"value\":\"25.00\"},{\"name\":\"sel4tblCharges\",\"value\":\"0\"},{\"name\":\"hidden4tblCharges\",\"value\":\"2247\"},{\"name\":\"qty5tblCharges\",\"value\":\"1\"},{\"name\":\"inputName5tblCharges\",\"value\":\"67.00\"},{\"name\":\"sel5tblCharges\",\"value\":\"0\"},{\"name\":\"hidden5tblCharges\",\"value\":\"2407\"},{\"name\":\"hiddeninputCheck1tblPrevious\",\"value\":\"49285\"},{\"name\":\"reverseCheck1tblPrevious\",\"value\":\"\"},{\"name\":\"hiddeninputCheck1tblPrevious\",\"value\":\"49285\"},{\"name\":\"hiddenAmount1tblPrevious\",\"value\":\"11.50\"},{\"name\":\"hiddenreverseCheck1tblPrevious\",\"value\":\"49285\"},{\"name\":\"method\",\"value\":\"8\"},{\"name\":\"amount_1\",\"value\":\"450.66\"},{\"name\":\"amount_2\",\"value\":\"\"},{\"name\":\"method1tblCredits\",\"value\":\"Mastercard\"},{\"name\":\"tendered1tblCredits\",\"value\":\"450.66\"},{\"name\":\"change1tblCredits\",\"value\":\"0.00\"},{\"name\":\"id1tblCredits\",\"value\":\"0\"},{\"name\":\"discount_perc\",\"value\":\"\"},{\"name\":\"client_taxable\",\"value\":\"1\"},{\"name\":\"co_clientSelect\",\"value\":\"3750\"},{\"name\":\"numPrevious\",\"value\":\"0\"},{\"name\":\"isCheckout\",\"value\":\"1\"},{\"name\":\"order_id\",\"value\":\"0\"},{\"name\":\"h_subtotal\",\"value\":\"445.79\"},{\"name\":\"h_tax\",\"value\":\"4.87\"},{\"name\":\"h_total\",\"value\":\"450.66\"},{\"name\":\"h_previousBalance\",\"value\":\"-161.53\"},{\"name\":\"h_charges\",\"value\":\"450.66\"},{\"name\":\"h_credits\",\"value\":\"450.66\"},{\"name\":\"h_owes\",\"value\":\"0\"},{\"name\":\"h_reverse\",\"value\":\"0\"},{\"name\":\"memo\",\"value\":\"\"}]";
				
				writer.println(this.doCheckout(session, workstation_cookie, selected_bu, logged_in_person, arg2));
				
			} else if (command.equals("salesReceipt")) {
				// how am I going to get the order(s) and tender(s) that I need to print on this sales receipt
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				//HttpSession session = ScheduleServlet2.getSessionForKey(arg1, _request);

				ValeoOrderBean adminOrder = (ValeoOrderBean)session.getAttribute("adminOrder");
				Vector previous_orders = (Vector<ValeoOrderBean>)session.getAttribute("previous_orders");
				Vector tenders = (Vector<TenderRet>)session.getAttribute("tenders");

				BigDecimal subtotal = (BigDecimal)session.getAttribute("h_subtotal");
				BigDecimal tax = (BigDecimal)session.getAttribute("h_tax");
				BigDecimal total = (BigDecimal)session.getAttribute("h_total");
				
				BigDecimal total_discount_products = (BigDecimal)session.getAttribute("h_total_discount_products");
				BigDecimal total_discount_services = (BigDecimal)session.getAttribute("h_total_discount_services");
				BigDecimal discount_perc_products = (BigDecimal)session.getAttribute("h_discount_percentage_products");
				BigDecimal discount_perc_services = (BigDecimal)session.getAttribute("h_discount_percentage_services");
				
				BigDecimal shipping = (BigDecimal)session.getAttribute("h_shipping");


				//UKOnlinePersonBean client = UKOnlinePersonBean.getPerson(adminCompany, adminOrder.getPersonId());
				UKOnlinePersonBean client = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				String sales_receipt_url = "../resources/pdf/" + SalesReceiptBuilder.generateSalesReceipt(selected_bu, logged_in_person, client, adminOrder, previous_orders, tenders, subtotal, tax, total, total_discount_products, total_discount_services, discount_perc_products, discount_perc_services, shipping);
				writer.println("<sales_receipt_url><![CDATA[" + sales_receipt_url + "]]></sales_receipt_url>");

				session.removeAttribute("adminOrder");
				session.removeAttribute("previous_orders");
				session.removeAttribute("tenders");

				session.removeAttribute("h_subtotal");
				session.removeAttribute("h_tax");
				session.removeAttribute("h_total");
				
			} else if (command.equals("lastReceipt")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
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

				BigDecimal total_discount_products = printed_receipt.getDiscountProducts();
				BigDecimal discount_perc_products = printed_receipt.getDiscountPercentageProducts();
				BigDecimal total_discount_services = printed_receipt.getDiscountServices();
				BigDecimal discount_perc_services = printed_receipt.getDiscountPercentageServices();
				
				BigDecimal shipping = printed_receipt.getShipping();

				//ValeoOrderBean adminOrder = (ValeoOrderBean)session.getAttribute("adminOrder");
				//Vector previous_orders = (Vector<ValeoOrderBean>)session.getAttribute("previous_orders");
				//Vector tenders = (Vector<TenderRet>)session.getAttribute("tenders");

				//BigDecimal subtotal = (BigDecimal)session.getAttribute("h_subtotal");
				//BigDecimal tax = (BigDecimal)session.getAttribute("h_tax");
				//BigDecimal total = (BigDecimal)session.getAttribute("h_total");

				String sales_receipt_url = "../resources/pdf/" + SalesReceiptBuilder.generateSalesReceipt(selected_bu, logged_in_person, client, primary_order, previous_orders, tenders, subtotal, tax, total, total_discount_products, total_discount_services, discount_perc_products, discount_perc_services, shipping);
				writer.println("<sales_receipt_url><![CDATA[" + sales_receipt_url + "]]></sales_receipt_url>");
			} else if (command.equals("searchFollowUp")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				String search_str = arg2;
				
				boolean yours_only = (arg4 != null) && (arg4.equals("true"));
				boolean reviewed_only = (arg5 != null) && (arg5.equals("true"));
				boolean past_due_only = (arg6 != null) && (arg6.equals("true"));
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"review\":[");
				//Iterator itr = ProductWaitList.searchProductWaitList(selected_bu, search_str, 30).iterator();
				Iterator itr = ClientReviewReason.searchClientReviewReasons(logged_in_person, search_str, 0, yours_only, reviewed_only, past_due_only).iterator();
				while (itr.hasNext()) {
					ClientReviewReason review_reason = (ClientReviewReason)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSONDetailAlt(review_reason));
					needs_comma = true;
				}
				b.append("]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("auth")) {
				
				writer.println(this.processAuth(arg1, _request));
				
			} else if (command.equals("deAuth")) {
				
				/*
				HttpSession session = ScheduleServlet2.getSessionForKey(arg1, _request);
				session.invalidate(); // shrug
				*/
				
				StringBuffer b = new StringBuffer();
				b.append("{\"deAuth\":[");
				b.append("]}");
				
				System.out.println(b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("saveOrderLines")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				//String json_blob = arg2;
				
				writer.println(this.saveLedgerRow(logged_in_person, arg2));
				
				//sdfsdf;
				
			} else if (command.equals("register")) {
				
				String email = parameter;
				String password = arg1;
				String confirm = arg2;
				
				if (email.isEmpty()) {
					throw new IllegalValueException("Please provide an email address.");
				}
				
				if (password.isEmpty()) {
					throw new IllegalValueException("Please provide a password.");
				}
				
				if (confirm.isEmpty()) {
					throw new IllegalValueException("Please confirm your password.");
				}
				
				CompanyBean defaultCompany = CompanyBean.maintainCompany("[DEFAULT]");
				
				
				UKOnlinePersonBean new_user = null;
				
				try {
					UKOnlinePersonBean.getPersonByEmail(email);
					throw new IllegalValueException(email + " is already in use.");
				} catch (ObjectNotFoundException x) {
					new_user = new UKOnlinePersonBean();
					new_user.setUsername(email);
					new_user.setEmail1(email);
					new_user.setPassword(password);
					new_user.setConfirmPassword(confirm);
					
					new_user.setDepartment(defaultCompany.getDefaultDepartment());
					PersonTitleBean.maintainDefaultData(defaultCompany);
					new_user.setTitle(PersonTitleBean.getDefaultTitle(defaultCompany));
					
					new_user.save();
					
					/*
					try
					{
						PersonGroupBean.maintainGroup(defaultCompany, PersonGroupBean.DEFAULT_PERSON_GROUP_NAME);
						new_user.setGroup(PersonGroupBean.getDefaultPersonGroup(defaultCompany));
					}
					catch (ObjectAlreadyExistsException already_exists)
					{
						already_exists.printStackTrace();
					}
					*/
				}
				
				
				StringBuffer b = new StringBuffer();
				b.append("{\"register\":[");
				b.append(this.toJSON(new_user));
				b.append("]}");
				
		
				/*
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"success\"," +
						"\"heading\":\"Success!\"," +
						"\"text\":\"Registration complete.\"" +
						"}");
				b.append("]}");
				*/
				
				System.out.println(b.toString());
				
				writer.println(b.toString());
				
			} else if (command.equals("getAvailableAppointmentTimes")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				if (arg3 == null) {
					more_map.remove(logged_in_person);
				} else {
					Integer num_more = (Integer)more_map.get(logged_in_person);
					if (num_more == null) {
						num_more = 0;
					}
					num_more++;
					more_map.put(logged_in_person, num_more);
				}
				
				writer.println(this.getAvailableAppointmentTimes(selected_bu, logged_in_person, arg2));
				
			} else if (command.equals("saveAppointments")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				writer.println(this.saveAppointmentTimes(selected_bu, logged_in_person, arg2));
				
			} else if (command.equals("getSessionStats")) {
				
				Date now = new Date();
				
				StringBuffer b = new StringBuffer();
				b.append("{\"active\":\"" + SessionCounter.getNumStudentSessions() + "\",");
				b.append("\"session\":[");
				Iterator itr = SessionCounter.getActiveSessionKeys();
				while (itr.hasNext()) {
					try {
						String sessionId = (String)itr.next();
						UKOnlinePersonBean session_person = SessionCounter.getStudent(sessionId);
						b.append(this.toJSON(sessionId, session_person, now));
						if (itr.hasNext()) {
							b.append(",");
						}
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("signature")) {
				
				String realPath = CUBean.getProperty("cu.realPath");
				String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
				if (resourcesFolder == null)
					throw new IllegalValueException("Property not defined: cu.resourcesFolder");
				resourcesFolder = realPath + resourcesFolder;
				
				String img64 = parameter.substring(parameter.indexOf(',') + 1);
				byte[] decodedBytes = DatatypeConverter.parseBase64Binary(img64);
				BufferedImage bfi = ImageIO.read(new ByteArrayInputStream(decodedBytes));
				File outputfile = new File(resourcesFolder + "signature.png");
				ImageIO.write(bfi, "png", outputfile);
				bfi.flush();
				
				String file_loc = EndOfDayLogBuilder.generateEndOfDayLog(new Vector());
				
				String pdf_file_loc = resourcesFolder + "pdf/" + file_loc;
				
				CUBean.sendEmail(arg1, "marlo@badiyan.com", "Badiyan - Test Send of Signature", "Please see attached...", pdf_file_loc);
				
			
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"success\"," +
						"\"heading\":\"Oh snap!\"," +
						"\"text\":\"stuffx >" + file_loc + "\"" +
						"}");
				b.append("]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("sanoContact")) {
				
				String clientName = parameter;
				String clientEmail = arg1;
				String clientPhone = arg2;
				String clientExisting = arg3;
				String contactSubject = arg4;
				String contactText = arg5;
				//String clientName = arg6;
				
				CUBean.sendEmail("cstueve@sanowc.com", clientEmail, contactSubject, "<b>Name: </b>" + clientName + "<br />" +
																					"<b>Phone: </b>" + clientPhone + "<br />" +
																					"<b>Existing: </b>" + clientExisting + "<br /><br />" +
																							contactText );
				
				
			
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"success\"," +
						"\"heading\":\"Success\"," +
						"\"text\":\"Email Sent\"" +
						"}");
				b.append("]}");
				
				System.out.println("ret >" + b.toString());
				
				writer.println(b.toString());
				
			} else if (command.equals("confirmAppointment")) {
				
				String decryptedStr = EncryptionUtils.decryptString(arg1);
				System.out.println("confirmAppointment >" + decryptedStr);
				
				AppointmentBean appointment_to_confirm = AppointmentBean.getAppointment(Integer.parseInt(decryptedStr));
				System.out.println("appointment_to_confirm >" + appointment_to_confirm.getClient().getLabel());
				appointment_to_confirm.confirm();
				appointment_to_confirm.save();
				
				StringBuilder b = new StringBuilder();
				b.append("<!DOCTYPE html>");
				b.append("<html>");
				b.append("<head><title>Appointment Confirmed</title></head>");
				b.append("<body><h1>Appointment for " + appointment_to_confirm.getClient().getLabel() + " confirmed.  You can close this page.</h1></body>");
				b.append("</html>");
				
				_response.setContentType("text/html");
				writer.println(b.toString());
				
			} else if (command.equals("unsubscribe")) {
				
				String email_to_unsub = EncryptionUtils.decryptString(arg1);
				System.out.println("email_to_unsub >" + email_to_unsub);
				
				try {
					UKOnlinePersonBean unsubscriber = UKOnlinePersonBean.getPersonByEmail((UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5), email_to_unsub);
					unsubscriber.setEmail1(email_to_unsub + "-removed");
					unsubscriber.save();
					writer.println("You have been unsubscribed.");
				} catch (ObjectNotFoundException x) {
					x.printStackTrace();
					writer.println("Unable to unsubscribe email address >" + email_to_unsub);
				}
				
			} else {
				throw new IllegalValueException("Command not implemented >" + command);
			}
			
			
		} catch (Exception x) {
			x.printStackTrace();
			/*
			StringBuffer b = new StringBuffer();
			b.append("{\"message\": {");
			b.append(" \"type\": \"ERROR\",");
			b.append(" \"heading\": \"Oh Snap!\",");
			b.append(" \"text\": \"" + JSONObject.escape(x.getMessage()) + "\"");
			b.append("}}");
			*/
			
			StringBuffer b = new StringBuffer();
			b.append("{\"message\":[");
			b.append("{\"type\":\"danger\"," +
					"\"heading\":\"Oh snap!\"," +
					"\"text\":\"" + JSONObject.escape(x.getMessage()) + "\"}");
			b.append("]}");
			
			System.out.println(b.toString());
			
			_response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			writer.println(b.toString());
		} finally {			
			writer.close();
		}
	}
	
	
	/*
	<table name="PERSON" idMethod="native">
    <column name="PERSONID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true"/>
    <column name="PREFIX" required="false" size="12" type="VARCHAR"/>
    <column name="FIRSTNAME" required="true" size="20" type="VARCHAR"/>
    <column name="MIDDLENAME" size="20" type="VARCHAR"/><!-- used as workday number for Ecolab -->
    <column name="LASTNAME" required="true" size="30" type="VARCHAR"/>
    <column name="SUFFIX" required="false" size="10" type="VARCHAR"/>
    <column name="USERNAME" required="true" size="80" type="VARCHAR"/>
    <column name="PASSWORD" required="true" size="50" type="VARCHAR"/>
    <column name="EMPLOYEEID" size="50" type="VARCHAR"/><!-- used for department display for ePredix -->
    <column name="EMAIL1" size="80" type="VARCHAR"/>
    <column name="EMAIL2" size="80" type="VARCHAR"/><!-- used as job for ePredix -->
    <column name="SSN" size="15" type="VARCHAR"/><!-- used as region/business unit for ePredix -->
    <column name="PERSONTYPE" required="true" size="40" type="VARCHAR"/>
    <column name="PERSON_TITLE_ID" type="INTEGER"/>
    <column name="ISACTIVE" required="true" type="SMALLINT"/>
    <column name="ISADMINISTRATOR" required="true" type="SMALLINT"/>
    <column name="OKSENDEMAIL" required="true" type="SMALLINT"/><!-- used if the user selected an unlisted coach for ePredix -->
    <column name="ISCOURSECONTACT" required="true" type="SMALLINT"/><!-- used if the user selected an manager coach for ePredix -->
    <column name="ISFACILITATOR" required="true" type="SMALLINT"/>
    <column name="FACILITATORACTIVE" required="true" type="SMALLINT"/><!-- used to track login for ePredix -->
    <column name="HIREDATE" type="DATE"/>
    <column name="BIRTHDATE" type="DATE"/>
    <column name="CREATIONDATE" type="DATE"/>
    <column name="MODIFICATIONDATE" type="DATE"/>
    <column name="PERSONCOMMENT" type="LONGVARCHAR" javaName="comment"/><!-- used to track unlisted info for ePredix -->
    <column name="FILE_NUMBER" required="false" type="INTEGER"/>

    <column name="PREVIOUS_BALANCE" required="false" scale="2" size="7" type="DECIMAL"/>
    <column name="CURRENT_BALANCE" required="false" scale="2" size="7" type="DECIMAL"/>

    <foreign-key foreignTable="PERSONTYPE">
	<reference local="PERSONTYPE" foreign="PERSONTYPE"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON_TITLE">
	<reference local="PERSON_TITLE_ID" foreign="PERSON_TITLE_ID"/>
    </foreign-key>
</table>
	*/
	
	private String toJSONDataExport(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		SimpleDateFormat export_date_format = new SimpleDateFormat("MM/dd/yyyy");
		String birthDateStr = "";
		Date birthDate = _person.getBirthDate();
		if (birthDate != null) {
			birthDateStr = CUBean.getUserDateString(birthDate, export_date_format);
		}
		return "{\"employeeNumber\":\"" + JSONObject.escape(_person.getEmployeeNumberString()) + "\"," +
				" \"prefix\":\"" + JSONObject.escape(_person.getPrefixString()) + "\"," +
				" \"firstName\":\"" + JSONObject.escape(_person.getFirstNameString()) + "\"," +
				" \"middleName\":\"" + JSONObject.escape(_person.getMiddleNameString()) + "\"," +
				" \"lastName\":\"" + JSONObject.escape(_person.getLastNameString()) + "\"," +
				" \"suffix\":\"" + JSONObject.escape(_person.getSuffixString()) + "\"," +
				" \"email1\":\"" + JSONObject.escape(_person.getEmail1String()) + "\"," +
				" \"email2\":\"" + JSONObject.escape(_person.getEmail2String()) + "\"," +
				" \"type\":\"" + JSONObject.escape(_person.getPersonTypeString()) + "\"," +
				" \"username\":\"" + JSONObject.escape(_person.getUsernameString()) + "\"," +
				" \"password\":\"" + JSONObject.escape(_person.getPasswordString()) + "\"," +
				" \"title\":\"" + JSONObject.escape(_person.getTitleString()) + "\"," +
				" \"birthDate\":\"" + JSONObject.escape(birthDateStr) + "\"," +
				" \"comment\":\"" + JSONObject.escape(_person.getCommentString()) + "\"," +
				" \"fileNumber\":\"" + JSONObject.escape(_person.getFileNumberString()) + "\"," +
				" \"active\":\"" + _person.isActive() + "\"," +
				" \"department\":\"" + JSONObject.escape(_person.getDepartmentNameString()) + "\"," +
				" \"group\":\"" + JSONObject.escape(_person.getGroupNameString()) + "\"," +
				" \"id\":\"" + _person.getId() + "\"}";
	}
	
	private String toJSONDropdown(AppointmentTypeBean _type) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return "{\"label\":\"" + JSONObject.escape(_type.getLabel()) + "\",\"minutes\":" + _type.getDuration() + ",\"id\":" + _type.getId() + "}";
	}
	
	private String toJSONDropdown(String _label, int _id) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return "{\"label\":\"" + JSONObject.escape(_label) + "\",\"id\":" + _id + "}";
	}
	
	private String toJSONDropdown(String _label, String _value) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return "{\"label\":\"" + JSONObject.escape(_label) + "\"," +
		"\"value\":\"" + _value + "\"}";
	}
	
	private String toJSONDropdownWithCategory(String _label, int _id, String _category) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return "{\"label\":\"" + JSONObject.escape(_label) + "\",\"id\":" + _id + ",\"category\":\"" + _category + "\"}";
	}
	
	public String
	getXML(AppointmentBean _appointment)
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
					value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"" + _appointment.getClient().getId() + "\", \"state\" : \"" + _appointment.getState() + "\"" + (_appointment.getClient().hasBirthdayDuringWeek(_appointment.getAppointmentDate()) ? (", \"bd\" : \"" + _appointment.getClient().getBirthDateString() + "\"") : "") + ", \"n\" : \"" + _appointment.isFirstTimeAppointmentForClientInPracticeArea() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"start\" : \"" + _appointment.getAppointmentStartUnixTimestamp() + "\", \"end\" : \"" + _appointment.getAppointmentEndUnixTimestamp() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
				else
					value = "{\"label\" : \"\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
			}
		}
		else// if (type.getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE)
		{
			if (_appointment.isRecurring())
				value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\", \"r\" : \"1\", \"r1\" : \"" + _appointment.getRecurVal() + "\", \"r2\" : \"" + _appointment.getRecurPeriod() + "\", \"r3\" : \"" + _appointment.getRecurPW() + "\", \"r4\" : \"" + _appointment.getRecurPWS() + "\", \"r5\" : \"" + _appointment.getRecurStopAfterN() + "\", \"r6\" : \"" + _appointment.getRecurStopByDateString() + "\" }";
			else
				value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
		}
		return "<appt><key>" + _appointment.getKey() + "</key><value><![CDATA[" + value + "]]></value></appt>";
	}
	
	private String toJSONFeed(AppointmentBean _appointment) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuilder b = new StringBuilder();
		
		b.append("{\"id\":" + _appointment.getId() + ",");
		b.append("\"resourceId\":" + _appointment.getPractitionerId() + ",");
		if (_appointment.isOfftime()) {
			// rendering: 'background'
			b.append("\"rendering\":\"background\",");
			b.append("\"color\":\"#CDCDB4\",");
		} else {
			
			boolean is_template = _appointment.isTemplate();
			
			/*
			if (!is_template) {
				is_template = _appointment.isClientAppointment() && (_appointment.getClient().getId() == 3121);
			}
			*/
			
			b.append("\"color\":\"#" + _appointment.getType().getBGColorCodeString() + "\",");
			b.append("\"selectedType\":" + _appointment.getType().getId() + ",");
			
			if (is_template) {
				b.append("\"rendering\":\"background\",");
				b.append("\"title\":\"" + JSONObject.escape(_appointment.getLabel()) + "\",");
			} else {
				b.append("\"title\":\"" + JSONObject.escape(_appointment.getLabel()) + "\",");

				String icon = "";
				if (_appointment.isMeeting()) {
					icon = "calendar";
				} else {
					switch (_appointment.getState()) {
						case AppointmentBean.DEFAULT_APPOINTMENT_STATUS: break; // icon = "square-o"; break;
						case AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS: icon = "check"; break;
						case AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS: icon = "check-square-o"; break;
						case AppointmentBean.LATE_APPOINTMENT_STATUS: icon = "hourglass-half"; break;
						case AppointmentBean.NO_SHOW_APPOINTMENT_STATUS: icon = "hourglass-end"; break;
						case AppointmentBean.PENDING_APPOINTMENT_STATUS: icon = "question"; break;
						case AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS: icon = "share"; b.append("\"rendering\":\"background\","); break; // figure out what to do for rescheduled or cancelled appointments 
						case AppointmentBean.CANCELLED_APPOINTMENT_STATUS: icon = "remove"; b.append("\"rendering\":\"background\","); break;
					}
				}
				if (!icon.isEmpty()) {
					b.append("\"icon\":\"" + icon + "\",");
				}
				b.append("\"popTitle\":\"" + JSONObject.escape(_appointment.getAppointmentTimeString()) + " - " + JSONObject.escape(_appointment.getType().getLabel()) + "\",");
				b.append("\"cm\":\"" + JSONObject.escape(_appointment.getComments()) + "\",");
				if (_appointment.isConfirmed()) {
					b.append("\"cd\":\"" + JSONObject.escape(_appointment.getConfirmationDateString()) + "\",");
				}
			}
		}
		b.append("\"start\":" + _appointment.getAppointmentStartUnixTimestampInMillis() + ",");
		b.append("\"end\":" + _appointment.getAppointmentEndUnixTimestampInMillis() + "}");
		
		return b.toString();
	}
	
	private String toJSONResourceSelect(AppointmentBean _appointment) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuilder b = new StringBuilder();
		
		b.append("{\"title\":\"" + JSONObject.escape(_appointment.getLabel()) + "\",");
		b.append("\"id\":" + _appointment.getId() + ",");
		b.append("\"resourceId\":" + _appointment.getPractitionerId() + ",");
		b.append("\"state\":" + _appointment.getState() + ",");
		if (_appointment.hasType() && _appointment.isOfftime()) {
			// rendering: 'background'
			b.append("\"rendering\":\"background\",");
		} else {
			// color: '#002255'
			
			if (_appointment.hasType() && _appointment.isClientAppointment() && _appointment.hasClient()) {
				if (_appointment.getClient().getId() == 3121) {
					b.append("\"rendering\":\"background\",");
				} else {
					b.append("\"clientId\":" + _appointment.getClient().getId() + ",");
					b.append("\"clientLabel\":\"" + _appointment.getClient().getLabel() + "\",");
				}
			}
			
			if (_appointment.hasType()) {
				b.append("\"color\":\"#" + _appointment.getType().getBGColorCodeString() + "\",");
				b.append("\"selectedType\":" + _appointment.getType().getId() + ",");
			} else {
				b.append("\"selectedType\":0,");
			}
			
			b.append("\"type\":[");
			Iterator itr = AppointmentTypeBean.getAppointmentTypes(_appointment.getPractitioner()).iterator();
			boolean needs_comma = false;
			while (itr.hasNext()) {
				AppointmentTypeBean appointment_type = (AppointmentTypeBean)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSONDropdown(appointment_type));
				needs_comma = true;
			}
			b.append("],");
			
			String icon = "";
			if (_appointment.hasType() && _appointment.isMeeting()) {
				icon = "calendar";
			} else {
				switch (_appointment.getState()) {
					case AppointmentBean.DEFAULT_APPOINTMENT_STATUS: icon = "square-o"; break;
					case AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS: icon = "check"; break;
					case AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS: icon = "check-square-o"; break;
					case AppointmentBean.LATE_APPOINTMENT_STATUS: icon = "hourglass-half"; break;
					case AppointmentBean.NO_SHOW_APPOINTMENT_STATUS: icon = "hourglass-end"; break;
					case AppointmentBean.PENDING_APPOINTMENT_STATUS: icon = "question"; break;
					case AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS: icon = "share"; b.append("\"rendering\":\"background\","); break; // figure out what to do for rescheduled or cancelled appointments 
					case AppointmentBean.CANCELLED_APPOINTMENT_STATUS: icon = "remove"; b.append("\"rendering\":\"background\","); break;
				}
			}
			
			b.append("\"icon\":\"" + icon + "\",");
			b.append("\"cm\":\"" + JSONObject.escape(_appointment.getComments()) + "\",");
		}
		// Wednesday, November 23rd 2016
		// EEEE, MMMM dd yyyy
		b.append("\"duration\":" + _appointment.getDuration() + ",");
		b.append("\"start\":" + _appointment.getAppointmentStartUnixTimestamp() + ",");
		b.append("\"end\":" + _appointment.getAppointmentEndUnixTimestamp() + "}");
		
		return b.toString();
	}
	
	private String toJSON(NewAppointmentEvent _event) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		AppointmentBean event_source = (AppointmentBean)_event.getSource();

		StringBuffer b = new StringBuffer();
		b.append("{\"apptCreate\":[");
		b.append(this.toJSONFeed(event_source));
		b.append("]}");

		System.out.println("ret >" + b.toString());

		return b.toString();
	}
	
	private String toJSON(UpdatedAppointmentEvent _event) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		AppointmentBean event_source = (AppointmentBean)_event.getSource();

		StringBuffer b = new StringBuffer();
		b.append("{\"apptUpdate\":[");
		b.append(this.toJSONFeed(event_source));
		b.append("]}");

		System.out.println("ret >" + b.toString());

		return b.toString();
	}
	
	private String toJSON(DeletedAppointmentEvent _event) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		AppointmentBean event_source = (AppointmentBean)_event.getSource();

		StringBuffer b = new StringBuffer();
		b.append("{\"apptDelete\":[");
		b.append(this.toJSONFeed(event_source));
		b.append("]}");

		System.out.println("ret >" + b.toString());

		return b.toString();
	}

	// Thursday, December 1st 2016
	// 9:25 am
	public static final SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEEE, MMMM d yyyy h:mm a");
	public static final Pattern ordinalPattern = Pattern.compile("([0-9]+)(st|nd|rd|th)");

	private static Date getDateFromDateString(String dateString) throws java.text.ParseException {
		return displayDateFormat.parse(deleteOrdinal(dateString));
	}

	private static String deleteOrdinal(String dateString) {
		Matcher m = ordinalPattern.matcher(dateString);
		while (m.find()) {
			dateString = dateString.replaceAll(Matcher.quoteReplacement(m.group(0)), m.group(1));
		}
		return dateString;
	}
	
	
	private String toJSON(AppointmentBean _appointment) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		/*
      <appt>
         <key>9:00 AM|3121</key>
         <value><![CDATA[{"label" : "Robert Doyle", "cid" : "4081", "state" : "7", "n" : "false", "id" : "85584", "duration" : "20", "date" : "10/31/16", "start" : "1477922400", "end" : "1477923600", "type" : "66", "cm" : "" }]]></value>
      </appt>
		*/
		
		StringBuilder b = new StringBuilder();
		b.append("{\"state\":" + _appointment.getState() + ",");
		
		String appt_comments = _appointment.getComments();
		
		AppointmentTypeBean type = null;
		if (_appointment.hasType()) {
			type = _appointment.getType();
		}
		String value = null;
		if ((type == null) || (type.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)) {
			if (_appointment.isRecurring()) {
				
				//value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"" + _appointment.getClient().getId() + "\", \"state\" : \"" + _appointment.getState() + "\"" + (_appointment.getClient().hasBirthdayDuringWeek(_appointment.getAppointmentDate()) ? (", \"bd\" : \"" + _appointment.getClient().getBirthDateString() + "\"") : "") + ", \"n\" : \"" + _appointment.isFirstTimeAppointmentForClientInPracticeArea() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\", \"r\" : \"1\", \"r1\" : \"" + _appointment.getRecurVal() + "\", \"r2\" : \"" + _appointment.getRecurPeriod() + "\", \"r3\" : \"" + _appointment.getRecurPW() + "\", \"r4\" : \"" + _appointment.getRecurPWS() + "\", \"r5\" : \"" + _appointment.getRecurStopAfterN() + "\", \"r6\" : \"" + _appointment.getRecurStopByDateString() + "\" }";
				
				b.append("\"label\":\"" + JSONObject.escape(_appointment.getLabel()) + "\",");
				b.append("\"cid\":" + _appointment.getClient().getId() + ",");
				if (_appointment.getClient().hasBirthdayDuringWeek(_appointment.getAppointmentDate())) {
					b.append("\"bd\":\"" + _appointment.getClient().getBirthDateString() + "\",");
				}
				b.append("\"n\":" + _appointment.isFirstTimeAppointmentForClientInPracticeArea() + ",");
				b.append("\"duration\":" + _appointment.getDuration() + ",");
				b.append("\"date\":\"" + _appointment.getAppointmentDateString() + "\",");
				b.append("\"type\":" + type.getId() + ",");
				b.append("\"cm\":\"" + JSONObject.escape(appt_comments) + "\",");
				b.append("\"r\":1,");
				b.append("\"r1\":" + _appointment.getRecurVal() + ",");
				b.append("\"r2\":" + _appointment.getRecurPeriod() + ",");
				b.append("\"r3\":" + _appointment.getRecurPW() + ",");
				b.append("\"r4\":" + _appointment.getRecurPWS() + ",");
				b.append("\"r5\":" + _appointment.getRecurStopAfterN() + ",");
				b.append("\"r6\":\"" + _appointment.getRecurStopByDateString() + "\",");
				
			} else {
				if (_appointment.hasClient()) {
					
					//value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"" + _appointment.getClient().getId() + "\", \"state\" : \"" + _appointment.getState() + "\"" + (_appointment.getClient().hasBirthdayDuringWeek(_appointment.getAppointmentDate()) ? (", \"bd\" : \"" + _appointment.getClient().getBirthDateString() + "\"") : "") + ", \"n\" : \"" + _appointment.isFirstTimeAppointmentForClientInPracticeArea() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"start\" : \"" + _appointment.getAppointmentStartUnixTimestamp() + "\", \"end\" : \"" + _appointment.getAppointmentEndUnixTimestamp() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
				
					b.append("\"label\":\"" + JSONObject.escape(_appointment.getLabel()) + "\",");
					b.append("\"cid\":" + _appointment.getClient().getId() + ",");
					if (_appointment.getClient().hasBirthdayDuringWeek(_appointment.getAppointmentDate())) {
						b.append("\"bd\":\"" + _appointment.getClient().getBirthDateString() + "\",");
					}
					b.append("\"n\":" + _appointment.isFirstTimeAppointmentForClientInPracticeArea() + ",");
					b.append("\"duration\":" + _appointment.getDuration() + ",");
					b.append("\"date\":\"" + _appointment.getAppointmentDateString() + "\",");
					b.append("\"start\":" + _appointment.getAppointmentStartUnixTimestampInMillis() + ",");
					b.append("\"end\":" + _appointment.getAppointmentEndUnixTimestampInMillis() + ",");
					b.append("\"type\":" + type.getId() + ",");
					b.append("\"cm\":\"" + JSONObject.escape(appt_comments) + "\",");
				
				} else {
					//value = "{\"label\" : \"\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"date\" : \"" + _appointment.getAppointmentDateString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
				
					b.append("\"label\":\"\",");
					b.append("\"cid\":0,");
					b.append("\"duration\":" + _appointment.getDuration() + ",");
					b.append("\"date\":\"" + _appointment.getAppointmentDateString() + "\",");
					b.append("\"type\":" + ((type == null) ? 0 : type.getId()) + ",");
					b.append("\"cm\":\"" + JSONObject.escape(appt_comments) + "\",");
				}
			}
		} else {
			if (_appointment.isRecurring()) {
				//value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\", \"r\" : \"1\", \"r1\" : \"" + _appointment.getRecurVal() + "\", \"r2\" : \"" + _appointment.getRecurPeriod() + "\", \"r3\" : \"" + _appointment.getRecurPW() + "\", \"r4\" : \"" + _appointment.getRecurPWS() + "\", \"r5\" : \"" + _appointment.getRecurStopAfterN() + "\", \"r6\" : \"" + _appointment.getRecurStopByDateString() + "\" }";
			
				b.append("\"label\":\"" + JSONObject.escape(_appointment.getLabel()) + "\",");
				b.append("\"cid\":0,");
				b.append("\"duration\":" + _appointment.getDuration() + ",");
				b.append("\"type\":" + type.getId() + ",");
				b.append("\"cm\":\"" + JSONObject.escape(appt_comments) + "\",");
				b.append("\"r\":1,");
				b.append("\"r1\":" + _appointment.getRecurVal() + ",");
				b.append("\"r2\":" + _appointment.getRecurPeriod() + ",");
				b.append("\"r3\":" + _appointment.getRecurPW() + ",");
				b.append("\"r4\":" + _appointment.getRecurPWS() + ",");
				b.append("\"r5\":" + _appointment.getRecurStopAfterN() + ",");
				b.append("\"r6\":\"" + _appointment.getRecurStopByDateString() + "\",");
			
			} else {
				//value = "{\"label\" : \"" + _appointment.getLabel() + "\", \"cid\" : \"0\", \"state\" : \"" + _appointment.getState() + "\", \"id\" : \"" + _appointment.getId() + "\", \"duration\" : \"" + _appointment.getDurationString() + "\", \"type\" : \"" + type.getId() + "\", \"cm\" : \"" + appt_comments + "\" }";
			
				b.append("\"label\":\"" + JSONObject.escape(_appointment.getLabel()) + "\",");
				b.append("\"cid\":0,");
				b.append("\"duration\":" + _appointment.getDuration() + ",");
				b.append("\"date\":\"" + _appointment.getAppointmentDateString() + "\",");
				b.append("\"type\":" + type.getId() + ",");
				b.append("\"cm\":\"" + JSONObject.escape(appt_comments) + "\",");
			}
		}
		//return "<appt><key>" + _appointment.getKey() + "</key><value><![CDATA[" + value + "]]></value></appt>";
		
		b.append("\"id\":" + _appointment.getId() + "}");
		
		return b.toString();
		
	}
    
    public static String
    getStatsJSON(AppointmentBean _appointment) throws Exception {
		
		/*
		String group_under_care_str = "";

		try
		{
			GroupUnderCareBean group_under_care = _person.getGroupUnderCare();
			group_under_care_str = ScheduleServlet.getXML(group_under_care);
		}
		catch (ObjectNotFoundException x)
		{
			x.printStackTrace();
		}
		*/

		/* not really sure why I was sending this, to highlight the person's appointments in the schedule or something...
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
		*/
		
		/*
		StringBuffer buf = new StringBuffer();
		buf.append("<contact-status>");
		itr = ContactStatusBean.getContactStatus(_person).iterator();
		while (itr.hasNext())
		{
			ContactStatusBean contact_status_obj = (ContactStatusBean)itr.next();
			buf.append(ScheduleServlet.getXML(contact_status_obj));
		}
		buf.append("</contact-status>");
		
		buf.append("<to-do>");
		itr = ToDoItemBean.getToDoItem(_person).iterator();
		while (itr.hasNext())
		{
			ToDoItemBean to_do_item_obj = (ToDoItemBean)itr.next();
			buf.append(ScheduleServlet.getXML(to_do_item_obj));
		}
		buf.append("</to-do>");
		
		buf.append("<care>");
		itr = CareDetailsBean.getCareDetails(_person).iterator();
		while (itr.hasNext())
		{
			CareDetailsBean details_obj = (CareDetailsBean)itr.next();
			buf.append(ScheduleServlet.getXML(details_obj));
		}
		buf.append("</care>");

		buf.append("<notes>");
		itr = PersonNote.getPersonNotes(_person).iterator();
		while (itr.hasNext())
		{
			PersonNote note_obj = (PersonNote)itr.next();
			buf.append(ScheduleServlet.getXML(note_obj));
		}
		buf.append("</notes>");

		buf.append("<review>");
		itr = ClientReviewReason.getClientReviewReasons(_person).iterator();
		while (itr.hasNext())
		{
			ClientReviewReason review_reason = (ClientReviewReason)itr.next();
			buf.append(ScheduleServlet.getXML(review_reason));
		}
		buf.append("</review>");

		buf.append(group_under_care_str);

		buf.append("<soap>");
		itr = SOAPNotesBean.getSOAPNotes(_person).iterator();
		while (itr.hasNext())
		{
			SOAPNotesBean note_obj = (SOAPNotesBean)itr.next();
			buf.append(ScheduleServlet.getXML(note_obj));
		}
		buf.append("</soap>");
		*/

		/*
		String comment_str = "";
		String history_str = _person.getHealthHistoryString().replaceAll("\"", "\\\\\"");
		*/
		
		/*
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
		*/
		
		StringBuilder b = new StringBuilder();
		
		if (_appointment.hasClient()) {
			
			UKOnlinePersonBean person = _appointment.getClient();

			b.append("{\"ph\":\"" + JSONObject.escape(person.getPhoneNumbersString()) + "\",");
			b.append("\"addr\":\"" + JSONObject.escape(person.getAddressesString()) + "\",");
			b.append("\"bal\":\"" + JSONObject.escape(person.getBalanceString()) + "\",");
			b.append("\"next\":\"" + JSONObject.escape(person.getNextAppointmentString()) + "\",");
			b.append("\"email\":\"" + JSONObject.escape(person.getEmailString()) + "\"}");
		} else {
			
			b.append("{\"isMeeting\":true}");
		}
		
		return b.toString();
    }
	
	private String toJSON(String _sessionId, UKOnlinePersonBean _session_person, Date _now) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		long nowTime = _now.getTime();
		long creationTime = -1l;
		long lastAccess = -1l;
		boolean displaySessionTimes = true;
		try {
			creationTime = SessionCounter.getSessionCreationTime(_sessionId);
			lastAccess = SessionCounter.getSessionLastAccessedTime(_sessionId);
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		if ((creationTime == -1l) || (lastAccess == -1l))
			displaySessionTimes = false;
		
		long created_seconds_ago = -1l;
		long last_access_seconds_ago = -1l;
		long created_minutes_ago = -1l;
		long last_access_minutes_ago = -1l;
		
		if (displaySessionTimes) {
			created_seconds_ago = (nowTime - creationTime) / 1000;
			last_access_seconds_ago = (nowTime - lastAccess) / 1000;
			created_minutes_ago = created_seconds_ago / 60;
			last_access_minutes_ago = last_access_seconds_ago / 60;
			created_seconds_ago = created_seconds_ago - (created_minutes_ago * 60);
			last_access_seconds_ago = last_access_seconds_ago - (last_access_minutes_ago * 60);
		}
		
		String browserDetect = SessionCounter.getBrowserInfoString(_sessionId);
		
		StringBuilder b = new StringBuilder();
		b.append("{\"id\":\"" + _session_person.getId() + "\",");
		b.append("\"label\":\"" + JSONObject.escape(_session_person.getLabel()) + "\",");
		b.append("\"sessionId\":\"" + _sessionId + "\",");
		b.append("\"browser\":\"" + JSONObject.escape(browserDetect) + "\",");
		b.append("\"creation\":\"" + created_minutes_ago + ":" + (created_seconds_ago < 10 ? ("0" + created_seconds_ago) : ("" + created_seconds_ago)) + "\",");
		b.append("\"access\":\"" + last_access_minutes_ago + ":" + (last_access_seconds_ago < 10 ? ("0" + last_access_seconds_ago) : ("" + last_access_seconds_ago)) + "\"");
		b.append("}");
		
		return b.toString();
	}
	
	private String toJSON(RoleBean _role) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return "{ \"name\":\"" + _role.getLabel() + "\" }";
	}
	
	private String toJSON(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_person.getLabel()) + "\",");
		b.append("\"empId\":\"" + _person.getEmployeeNumberString() + "\",");
		b.append("\"active\":" + _person.isActive() + ",");
		b.append("\"lastLogIn\":\"" + _person.getLastLogInDateString() + "\",");
		b.append("\"role\":\"" + JSONObject.escape(_person.getRolesString()) + "\",");
		b.append("\"location\":\"" + JSONObject.escape(_person.getDepartmentNameString()) + "\",");
		b.append("\"title\":\"" + JSONObject.escape(_person.getJobTitleString()) + "\",");
		b.append("\"id\":" + _person.getId() + "}");
		return b.toString();
	}
	
	private String toJSONSearch(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		
		b.append("{\"label\":\"" + JSONObject.escape(_person.getLabel()) + "\",");
		b.append("\"fileNumber\":\"" + _person.getFileNumberString() + "\",");
		b.append("\"email\":\"" + JSONObject.escape(_person.getEmailString()) + "\",");
		//b.append("\"addr1\":\"" + JSONObject.escape(_person.getHomeAddress1String()) + "\",");
		//b.append("\"addr2\":\"" + JSONObject.escape(_person.getHomeAddress2String()) + "\",");
		b.append("\"city\":\"" + JSONObject.escape(_person.getHomeAddressCityString()) + "\",");
		b.append("\"state\":\"" + JSONObject.escape(_person.getHomeAddressStateString()) + "\",");
		b.append("\"zip\":\"" + JSONObject.escape(_person.getHomeAddressZipString()) + "\",");
		b.append("\"cellPhone\":\"" + JSONObject.escape(_person.getCellPhoneNumberString()) + "\",");
		b.append("\"homePhone\":\"" + JSONObject.escape(_person.getHomePhoneNumberString()) + "\",");
		b.append("\"status\":\"" + (_person.isActive() ? "Active" : "Inactive") + "\",");
		//b.append("\"bal\":\"" + _person.getBalanceString() + "\",");
		//b.append("\"last\":\"" + _person.getLastAppointmentString() + "\",");
		//b.append("\"next\":\"" + _person.getNextAppointmentString() + "\",");
		b.append("\"isMale\":" + _person.isMale() +  ",");
		b.append("\"id\":" + _person.getId() + "}");
		
		return b.toString();
	}
	
	private String toJSONChat(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
	
		Vector keys_for_person = ScheduleServlet2.getKeysForPerson(_person);
		boolean connected = PractitionerAppSocketServlet.isConnected(_person);
		if (!connected) {
			if (!keys_for_person.isEmpty()) {
				Iterator itr = keys_for_person.iterator();
				while (itr.hasNext() && !connected) {
					String key_for_person = (String)itr.next();
					connected = ScheduleSocketServlet.isConnected(key_for_person);
				}
			}
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_person.getLabel()) + "\",");
		b.append("\"first\":\"" + JSONObject.escape(_person.getFirstNameString()) + "\",");
		b.append("\"last\":\"" + JSONObject.escape(_person.getLastNameString()) + "\",");
		b.append("\"offline\":\"\","); // offline
		b.append("\"status\":\"" + (connected ? "online" : "busy") + "\","); // online, busy, away, incognito
		b.append("\"statusDesc\":\"" + JSONObject.escape(_person.getCommentString()) + "\",");
		b.append("\"title\":\"" + JSONObject.escape(_person.getJobTitleString()) + "\",");
		b.append("\"image\":\"" + JSONObject.escape("assets/avatars/" + _person.getFirstNameString().toLowerCase() + ".jpg") + "\",");
		b.append("\"id\":" + _person.getId() + "}");
		return b.toString();
	}
	
	private String messageToJSON(UKOnlinePersonBean _from, UKOnlinePersonBean _to, String _subject, String _message, boolean _showAlert) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		StringBuilder b = new StringBuilder();
		b.append("{\"first\":\"" + JSONObject.escape(_from.getFirstNameString()) + "\",");
		b.append("\"last\":\"" + JSONObject.escape(_from.getLastNameString()) + "\",");
		b.append("\"fromId\":\"" + _from.getId() + "\",");
		//b.append("\"to\":\"" + JSONObject.escape(_to.getLabel()) + "\",");
		b.append("\"alert\":\"" + JSONObject.escape(_subject) + "\",");
		b.append("\"showAlert\":" + _showAlert + ",");
		b.append("\"status\":\"online\",");
		b.append("\"message\":\"" + JSONObject.escape(_message) + "\"");
		b.append("}");
		
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _person, String _key) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_person.getLabel()) + "\",");
		b.append("\"empId\":\"" + _person.getEmployeeNumberString() + "\",");
		b.append("\"active\":" + _person.isActive() + ",");
		b.append("\"lastLogIn\":\"" + _person.getLastLogInDateString() + "\",");
		b.append("\"role\":\"" + JSONObject.escape(_person.getRolesString()) + "\",");
		b.append("\"location\":\"" + JSONObject.escape(_person.getDepartmentNameString()) + "\",");
		b.append("\"title\":\"" + JSONObject.escape(_person.getJobTitleString()) + "\",");
		b.append("\"image\":\"" + JSONObject.escape("assets/avatars/" + _person.getFirstNameString().toLowerCase() + "-50.png") + "\",");
		b.append("\"key\":\"" + _key + "\",");
		b.append("\"id\":" + _person.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(GroupUnderCareBean _group_under_care) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_group_under_care.getLabel()) + "\",");
		b.append("\"member\":[");
		boolean needs_comma = false;
		Iterator group_under_care_members_itr = _group_under_care.getMembers().iterator();
		while (group_under_care_members_itr.hasNext()) {
			GroupUnderCareMember member = (GroupUnderCareMember)group_under_care_members_itr.next();
			UKOnlinePersonBean person_member = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(member.getPersonId());
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(person_member, member));
			needs_comma = true;
		}
		b.append("],");
		b.append("\"id\":" + _group_under_care.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _person, GroupUnderCareMember _member) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_person.getLabel()) + "\",");
		b.append("\"relate\":\"" + GroupUnderCareBean.getRelationshipString(_member.getRelationshipToPrimaryClient()) + "\",");
		b.append("\"id\":" + _person.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(ValeoOrderBean _order) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_order.getExtendedLabel()) + "\",");
		b.append("\"total\":\"" + _order.getTotalString() + "\",");
		b.append("\"tax\":\"" + _order.getTaxString() + "\",");
		b.append("\"subtotal\":\"" + _order.getSubtotalString() + "\",");
		b.append("\"balance\":\"" + _order.getBalanceString() + "\",");
		b.append("\"open\":" + (_order.isReturn() ? !_order.isReturn() : _order.isOpen()) + ",");
		b.append("\"reversed\":" + _order.isReversed() + ",");
		b.append("\"legacy\":false,");
		b.append("\"date\":\"" + JSONObject.escape(_order.getOrderDateString()) + "\",");
		b.append("\"id\":" + _order.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(InvoiceRet _invoice, UKOnlineCompanyBean _company) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		String label = null;
		try {
			Iterator itr = _invoice.getInvoiceLineRetObjects().iterator();
			while (itr.hasNext()) {
				InvoiceLineRet line = (InvoiceLineRet)itr.next();
				if (!line.getItemListId().equals("80000013-1231210541")) {
					try {
						CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCodeByQBListID(_company, line.getItemListId());
						boolean is_reversed = false;
						boolean is_return = false;
						boolean is_open = true;
						if (label == null)
							label = (is_reversed ? "REVERSED: " : (is_return ? "RETURN: " : (is_open ? "ON ACCOUNT: " : ""))) + code.getLabel();
						else
							label += ", " + code.getLabel();
					} catch (ObjectNotFoundException x) { }
				}
			}
		} catch (Exception x) {
			label = "Error: " + x.getMessage();
		}
		
		BigDecimal balance_remaining = new BigDecimal(_invoice.getBalanceRemaining());
		BigDecimal sub_total = new BigDecimal(_invoice.getSubtotal());
		BigDecimal tax_amount = new BigDecimal(_invoice.getTaxAmount());
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(label) + "\",");
		b.append("\"total\":\"" + balance_remaining.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		b.append("\"tax\":\"" + tax_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		b.append("\"subtotal\":\"" + sub_total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		b.append("\"balance\":\"" + balance_remaining.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		b.append("\"open\":" + !_invoice.isPaid() + ",");
		b.append("\"reversed\":false,");
		b.append("\"legacy\":true,");
		b.append("\"date\":\"" + JSONObject.escape(short_date_format.format(_invoice.getTxnDate())) + "\",");
		b.append("\"id\":" + _invoice.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(ShoppingCartBean _cart, String _message, boolean _include_curly_braces, BigDecimal _balance) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		boolean needs_comma = false;
		StringBuilder b = new StringBuilder();
		if (_include_curly_braces) {
			b.append("{");
		}
		if (_cart != null) {
			b.append("\"cart\":[");
			Iterator itr = _cart.getCheckoutOrderlines();
			while (itr.hasNext()) {
				com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(orderline));
				needs_comma = true;
			}
			b.append("],");
		}
		needs_comma = false;
		b.append("\"tender\":[");
		Iterator itr = null;
		if (_cart != null) {
			itr = _cart.getTenders();
			if (itr.hasNext()) {
				while (itr.hasNext()) {
					TenderRet tender = (TenderRet)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(tender));
					needs_comma = true;
				}
			}
		}
		if (itr == null) {
			if (_balance != null) {
				TenderRet account_tender = new TenderRet();
				//account_tender.setCompany(_cart.getClient().getComp);
				//account_tender.setClient(_client);
				account_tender.setType(TenderRet.ACCOUNT_TENDER_TYPE);
				account_tender.setAmount(Math.abs(_balance.floatValue()));
				account_tender.setType((short)4);
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(account_tender));
				needs_comma = true;
			}
		}
		if (_message == null || !_include_curly_braces) {
			if (_include_curly_braces) {
				b.append("]}");
			} else {
				b.append("]");
			}
		} else {
			b.append("],");
			b.append("\"message\":[");
			b.append("{\"type\":\"success\"," +
					"\"heading\":\"Item Removed\"," +
					"\"text\":\"" + _message + "\"}]}");
		}
		
		return b.toString();
	}
	
	private String toJSONShort(CheckoutCodeBean _code) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_code.getLabel()) + "\",");
		b.append("\"id\":" + _code.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(CheckoutCodeBean _code) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_code.getLabel()) + "\",");
		b.append("\"amount\":\"" + _code.getAmountString() + "\",");
		b.append("\"id\":" + _code.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(ProductWaitList _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"productLabel\":\"" + JSONObject.escape(_obj.getProduct().getLabel()) + "\",");
		b.append("\"personLabel\":\"" + JSONObject.escape(_obj.getWaitingPerson().getLabel()) + "\",");
		b.append("\"vendorId\":" + _obj.getProduct().getVendorId() + ",");
		b.append("\"qty\":" + _obj.getQuantity().setScale(0, RoundingMode.HALF_UP).toString() + ",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(QBMSCreditCardResponse _response) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"amount\":\"" + _response.getChargeAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		b.append("\"auth\":\"" + JSONObject.escape(_response.getAuthorizationCode()) + "\",");
		b.append("\"id\":" + _response.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(TenderRet _tender) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"method\":\"" + _tender.getType() + "\",");
		if (_tender.isCreditCardPaymentType()) {
			b.append("\"creditCardType\":\"" + _tender.getCreditCardTypeString() + "\",");
		}
		b.append("\"amount\":\"" + _tender.getTenderedAmountString() + "\",");
		b.append("\"change\":\"" + _tender.getChangeAmountString() + "\",");
		b.append("\"method_1\":\"" + _tender.getMethod1() + "\",");
		b.append("\"method_2\":\"" + _tender.getMethod2() + "\",");
		b.append("\"id\":" + _tender.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(CheckoutOrderline _orderline) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(_orderline.getCheckoutCodeId());
		if (_orderline.getPrice() != null) {
			b.append("{\"amount\":\"" + _orderline.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		} else if (_orderline.getPrice() == null || _orderline.getActualAmount() == null) {
			b.append("{\"amount\":\"" + checkout_code.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		} else {
			b.append("{\"amount\":\"" + _orderline.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
			b.append("\"actual\":\"" + _orderline.getActualAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		}
		b.append("\"code\":\"" + checkout_code.getCode() + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(checkout_code.getDescription()) + "\",");
		b.append("\"code_id\":" + checkout_code.getId() + ",");
		b.append("\"type\":" + checkout_code.getType() + ",");
		b.append("\"tax\":\"" + checkout_code.getTaxPercentageString() + "\",");
		b.append("\"qty\":" + _orderline.getQuantity() + ",");
		try {
			b.append("\"pId\":" + _orderline.getPractitionerId() + ",");
			b.append("\"pLabel\":\"" + JSONObject.escape( PersonBean.getPerson(_orderline.getPractitionerId()).getLabel() ) + "\",");
		} catch (Exception x) {
			
		}
		b.append("\"id\":" + _orderline.getCheckoutOrderlineId() + "}");

		return b.toString();
	}
	
	private String toJSONShort(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return "{ \"label\":\"" + _person.getLabel() + "\", \"id\":\"" + _person.getId() + "\"}";
	}
	
	private String toJSON(ContactStatusBean _contact_status) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_contact_status.getLabel()) + "\",");
		b.append("\"id\":" + _contact_status.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(ToDoItemBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(CareDetailsBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(PersonNote _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(ClientReviewReason _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
	private String toJSONDetail(UKOnlinePersonBean _logged_in_person, ClientReviewReason _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"note\":\"" + JSONObject.escape(_obj.getNoteString()) + "\",");
		
		Vector practitioners = _obj.getPractitioners();
		b.append("\"isMe\":" + practitioners.contains(_logged_in_person) + ",");
		b.append("\"isChristine\":" + practitioners.contains(ScheduleServlet2.dr_christine) + ",");
		
		b.append("\"practitioner\":[");
		boolean needs_comma = false;
		//itr = UKOnlinePersonBean.getPractitioners(_company).iterator();
		Iterator itr = _obj.getPractitioners().iterator();
		while (itr.hasNext()) {
			UKOnlinePersonBean practitioner_obj = (UKOnlinePersonBean)itr.next();
			if (!practitioner_obj.equals(ScheduleServlet2.dr_christine) && !practitioner_obj.equals(_logged_in_person)) {
				if (needs_comma) { b.append(','); }
				b.append(this.toJSONDropdown(practitioner_obj.getLabel(), practitioner_obj.getId()));
				needs_comma = true;
			}
		}
		b.append("],");
			
		//b.append("\"practitionerId\":" + _obj.getPractitioner().getId() + ",");
		b.append("\"reasonId\":" + _obj.getReviewReason().getId() + ",");
		b.append("\"date\":\"" + _obj.getReviewDateString() + "\",");
		b.append("\"sendClientEmail\":" + _obj.emailClient() + ",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
	private String toJSONDetailAlt(ClientReviewReason _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"reason\":\"" + JSONObject.escape(_obj.getReviewReason().getLabel()) + "\",");
		b.append("\"practitioner\":\"" + JSONObject.escape(_obj.getPractitionersString()) + "\",");
		b.append("\"person\":\"" + JSONObject.escape(_obj.getPerson().getLabel()) + "\",");
		b.append("\"date\":\"" + _obj.getReviewDateString() + "\",");
		b.append("\"reviewed\":" + _obj.isReviewed() + ",");
		b.append("\"pastDue\":" + _obj.isPastDue() + ",");
		b.append("\"personId\":" + _obj.getPerson().getId() + ",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(SOAPNotesBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
			
	private String toJSONShort(com.badiyan.torque.CheckoutCode _code) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		return "{\"label\":\"" + JSONObject.escape(_code.getDescription()) + "\"," +
		" \"id\":\"" + _code.getCheckoutCodeId() + "\"}";
	}
			
	private String toJSON(com.badiyan.torque.CheckoutCode _code) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		return "{\"label\":\"" + JSONObject.escape(_code.getDescription()) + "\"," +
		" \"amount\":\"" + _code.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
		" \"active\":" + (_code.getIsActive() == (short)1) + "," +
		" \"id\":\"" + _code.getCheckoutCodeId() + "\"}";
	}
	
	private String toJSONDetail(SOAPNotesBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"s\":\"" + JSONObject.escape(_obj.getSNoteString()) + "\",");
		b.append("\"o\":\"" + JSONObject.escape(_obj.getONoteString()) + "\",");
		b.append("\"a\":\"" + JSONObject.escape(_obj.getANoteString()) + "\",");
		b.append("\"p\":\"" + JSONObject.escape(_obj.getPNoteString()) + "\",");
		b.append("\"pa\":" + _obj.getPracticeArea().getId() + ",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
    
    public String
    getStatsJSON(UKOnlinePersonBean _person, UKOnlineCompanyBean _company, int selected_review_id)
		throws Exception
    {
		
		PersonSettingsBean settings = PersonSettingsBean.getPersonSettings(_person, true);
		
		StringBuilder b = new StringBuilder();

		//String comment_str = _person.getCommentString().replaceAll("\"", "\\\\\"");
		//String comment_str = _person.getCommentString().replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\").replace('\r', '-').replace('\n', '_');
		String comment_str = "";
		//String history_str = _person.getHealthHistoryString().replaceAll("\"", "\\\\\"");
		
		
		/*
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
		*/
		
		/*
		String value = "{\"label\" : \"" + _person.getLabel() + "\"," +
						" \"id\" : \"" + _person.getValue() + "\"," +
						" \"ph\" : \"" + _person.getPhoneNumbersString() + "\"," +
						" \"addr\" : \"" + _person.getAddressesString() + "\"," +
						" \"next\" : \"" + _person.getNextAppointmentString() + "\"," +
						" \"last\" : \"" + _person.getLastAppointmentString() + "\"," +
						" \"ps\" : \"" + _person.getPlanStatusString() + "\"," +
						" \"bal\" : \"" + _person.getBalanceString() + "\"," +
						" \"status\" : \"" + _person.getStatus() + "\"," +
						" \"as\" : \"\"," +
						" \"pw\" : \"" + _person.getPasswordString() + "\"," +
						" \"cm\" : \"" + comment_str + "\"," +
						" \"file\" : \"" + _person.getFileNumberString() + "\"," +
						" \"email\" : \"" + _person.getEmailString() + "\" }";
		
		*/
		
		
		b.append("{\"label\":\"" + JSONObject.escape(_person.getLabel()) + "\",");
		b.append("\"firstName\":\"" + JSONObject.escape(_person.getFirstNameString()) + "\",");
		b.append("\"lastName\":\"" + JSONObject.escape(_person.getLastNameString()) + "\",");
		b.append("\"email\":\"" + JSONObject.escape(_person.getEmailString()) + "\",");
		b.append("\"addr1\":\"" + JSONObject.escape(_person.getHomeAddress1String()) + "\",");
		b.append("\"addr2\":\"" + JSONObject.escape(_person.getHomeAddress2String()) + "\",");
		b.append("\"city\":\"" + JSONObject.escape(_person.getHomeAddressCityString()) + "\",");
		b.append("\"state\":\"" + JSONObject.escape(_person.getHomeAddressStateString()) + "\",");
		b.append("\"zip\":\"" + JSONObject.escape(_person.getHomeAddressZipString()) + "\",");
		b.append("\"cellPhone\":\"" + JSONObject.escape(_person.getCellPhoneNumberString()) + "\",");
		b.append("\"homePhone\":\"" + JSONObject.escape(_person.getHomePhoneNumberString()) + "\",");
		b.append("\"dob\":\"" + JSONObject.escape(_person.getBirthDateString()) + "\",");
		b.append("\"cm\":\"" + JSONObject.escape(comment_str) + "\",");
		b.append("\"file\":\"" + _person.getFileNumberString() + "\",");
		b.append("\"as\":\"\",");
		b.append("\"pw\":\"" + JSONObject.escape(_person.getPasswordString()) + "\",");
		b.append("\"status\":\"" + _person.getStatus() + "\",");
		b.append("\"bal\":\"" + _person.getBalanceString() + "\",");
		b.append("\"last\":\"" + _person.getLastAppointmentString() + "\",");
		b.append("\"next\":\"" + _person.getNextAppointmentString() + "\",");
		b.append("\"history\":\"" + JSONObject.escape(_person.getHealthHistoryString()) + "\",");
		b.append("\"isMale\":" + _person.isMale() +  ",");
		
		b.append("\"isNSIPMClient\":" + settings.isNSIPMClient() +  ",");
		b.append("\"NSIPMCoverage\":\"" + ScheduleServlet2.getStringFromBigDecimal(settings.getNSIPMCoveragePercentage()) + "\",");
		
		b.append("\"group\":[");
		try {
			GroupUnderCareBean group_under_care = _person.getGroupUnderCare();
			b.append(this.toJSON(group_under_care));
		} catch (ObjectNotFoundException x) {
			x.printStackTrace();
		}
		b.append("],");
		
		/*
		b.append("\"contact-status\":[");
		boolean needs_comma = false;
		Iterator itr = ContactStatusBean.getContactStatus(_person).iterator();
		while (itr.hasNext()) {
			ContactStatusBean contact_status_obj = (ContactStatusBean)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(contact_status_obj));
			needs_comma = true;
		}
		b.append("],");
		
		b.append("\"to-do\":[");
		needs_comma = false;
		itr = ToDoItemBean.getToDoItem(_person).iterator();
		while (itr.hasNext()) {
			ToDoItemBean to_do_item_obj = (ToDoItemBean)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(to_do_item_obj));
			needs_comma = true;
		}
		b.append("],");
		
		b.append("\"care\":[");
		needs_comma = false;
		itr = CareDetailsBean.getCareDetails(_person).iterator();
		while (itr.hasNext()) {
			CareDetailsBean details_obj = (CareDetailsBean)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(details_obj));
			needs_comma = true;
		}
		b.append("],");
		
		b.append("\"note\":[");
		needs_comma = false;
		itr = PersonNote.getPersonNotes(_person).iterator();
		while (itr.hasNext()) {
			PersonNote note_obj = (PersonNote)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(note_obj));
			needs_comma = true;
		}
		b.append("],");
		
		b.append("\"review\":[");
		needs_comma = false;
		itr = ClientReviewReason.getClientReviewReasons(_person).iterator();
		while (itr.hasNext()) {
			ClientReviewReason review_reason = (ClientReviewReason)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(review_reason));
			needs_comma = true;
		}
		b.append("],");
		
		b.append("\"soap\":[");
		needs_comma = false;
		itr = SOAPNotesBean.getSOAPNotes(_person).iterator();
		while (itr.hasNext()) {
			SOAPNotesBean note_obj = (SOAPNotesBean)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(note_obj));
			needs_comma = true;
		}
		b.append("],");
		*/
		
		if (selected_review_id > 0) {
			
			b.append("\"reason\":[");
			boolean needs_comma = false;
			Iterator itr = ReviewReason.getReviewReasons(_company).iterator();
			while (itr.hasNext()) {
				ReviewReason reason_obj = (ReviewReason)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSONDropdown(reason_obj.getLabel(), reason_obj.getId()));
				needs_comma = true;
			}
			b.append("],");

			b.append("\"practitioner\":[");
			needs_comma = false;
			itr = UKOnlinePersonBean.getPractitioners(_company).iterator();
			while (itr.hasNext()) {
				UKOnlinePersonBean practitioner_obj = (UKOnlinePersonBean)itr.next();
				if (!practitioner_obj.isRoom()) {
					if (needs_comma) { b.append(','); }
					b.append(this.toJSONDropdown(practitioner_obj.getLabel(), practitioner_obj.getId()));
					needs_comma = true;
				}
			}
			b.append("],");

			b.append("\"review\":[");
			needs_comma = false;
			itr = ClientReviewReason.getClientReviewReasons(_person).iterator();
			while (itr.hasNext()) {
				ClientReviewReason review_reason = (ClientReviewReason)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(review_reason));
				needs_comma = true;
			}
			b.append("],");
		}
		
		b.append("\"id\":" + _person.getId() + "}");
		
		
		//return "<person><key>" + _person.getId() + "</key><value><![CDATA[" + value + "]]></value>" + buf.toString() + "<history><![CDATA[" + history_str + "]]></history></person>";
		
		return b.toString();
		
    }
	
    public String
    getSOAPJSON(UKOnlinePersonBean _person)
		throws Exception
    {
		StringBuilder b = new StringBuilder();
		
		b.append("{\"soap\":[");
		boolean needs_comma = false;
		Iterator itr = SOAPNotesBean.getSOAPNotes(_person).iterator();
		while (itr.hasNext()) {
			SOAPNotesBean note_obj = (SOAPNotesBean)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(note_obj));
			needs_comma = true;
		}
		b.append("]}");
		
		return b.toString();
    }
	
    public String
    getFollowupJSON(UKOnlinePersonBean _person, UKOnlineCompanyBean _company, short _reason_type)
		throws Exception
    {
		StringBuilder b = new StringBuilder();
		
		// load stuff for this page:
		//  - follow-up reasons
		//  - practitioners
		//  - existing follow-up items for this client
		
		b.append("{\"reason\":[");
		boolean needs_comma = false;
		Iterator itr = ReviewReason.getReviewReasons(_company, _reason_type).iterator();
		while (itr.hasNext()) {
			ReviewReason reason_obj = (ReviewReason)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSONDropdown(reason_obj.getLabel(), reason_obj.getId()));
			needs_comma = true;
		}
		b.append("],");
		
		b.append("\"practitioner\":[");
		needs_comma = false;
		itr = UKOnlinePersonBean.getPractitioners(_company).iterator();
		while (itr.hasNext()) {
			UKOnlinePersonBean practitioner_obj = (UKOnlinePersonBean)itr.next();
			if (!practitioner_obj.isRoom()) {
				if (needs_comma) { b.append(','); }
				b.append(this.toJSONDropdown(practitioner_obj.getLabel(), practitioner_obj.getId()));
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"staff\":[");
		needs_comma = false;
		itr = UKOnlinePersonBean.getCashiers(_company).iterator();
		while (itr.hasNext()) {
			UKOnlinePersonBean staff_obj = (UKOnlinePersonBean)itr.next();
			if (!staff_obj.isRoom()) {
				if (needs_comma) { b.append(','); }
				b.append(this.toJSONDropdown(staff_obj.getLabel(), staff_obj.getId()));
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"review\":[");
		needs_comma = false;
		itr = ClientReviewReason.getClientReviewReasons(_person).iterator();
		while (itr.hasNext()) {
			ClientReviewReason review_reason = (ClientReviewReason)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(review_reason));
			needs_comma = true;
		}
		b.append("]}");
		
		return b.toString();
    }
	
    public String
    getFollowupReasonsJSON(UKOnlineCompanyBean _company, short _reason_type)
		throws Exception
    {
		StringBuilder b = new StringBuilder();
		
		b.append("{\"reason\":[");
		boolean needs_comma = false;
		Iterator itr = ReviewReason.getReviewReasons(_company, _reason_type).iterator();
		while (itr.hasNext()) {
			ReviewReason reason_obj = (ReviewReason)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSONDropdown(reason_obj.getLabel(), reason_obj.getId()));
			needs_comma = true;
		}
		b.append("]}");
		
		return b.toString();
    }
	
	private String
	assembleScheduleJSON(UKOnlineCompanyBean _company, PracticeAreaBean _practice_area, Calendar _display_date, Calendar _end_date, String _session_id, boolean _get_all_appts)
		throws Exception
	{
		StringBuilder b = new StringBuilder();
		
		
		int num_total = 0;
		int num_cancelled = 0;
		int num_checked_out = 0;
		int num_rescheduled = 0;
		

		
		// has the schedule been updated since the last client request for this date? 
		
		/*
		String date_str = ScheduleServlet2.date_format.format(_display_date.getTime());
		String end_date_str = null;
		if (_end_date != null) {
			end_date_str = ScheduleServlet2.date_format.format(_display_date.getTime());
		} 
		*/
		
		
				
		//returnBuf.append("<date tot=\"" + num_total + "\" cn=\"" + num_cancelled + "\" co=\"" + num_checked_out + "\" rs=\"" + num_rescheduled + "\">" + date_str + "</date>");
		//returnBuf.append("<schedule>");


		b.append("[");

		boolean needs_comma = false;
		
		Vector checked_in = new Vector();
		Vector late = new Vector();
		Vector no_show = new Vector();
		//Iterator appt_itr = AppointmentBean.getAppointments(_company, _practice_area, _display_date.getTime()).iterator();
		
		Iterator appt_itr = null;
		if (_end_date == null) {
			appt_itr = AppointmentBean.getAppointmentsNoCache(_company, _practice_area, _display_date.getTime()).iterator();
		} else {
			appt_itr = AppointmentBean.getAppointmentsNoCache(_company, _practice_area, _display_date.getTime(), _end_date.getTime()).iterator();
		}
		
		while (appt_itr.hasNext())
		{
			AppointmentBean appointment = (AppointmentBean)appt_itr.next();
			if (appointment.isClientAppointment()) {
				short state = appointment.getState();
				switch (state) {
					case AppointmentBean.CANCELLED_APPOINTMENT_STATUS: num_cancelled++; break;
					case AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS: checked_in.addElement(appointment); num_total++; break;
					case AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS: num_checked_out++; num_total++; break;
					case AppointmentBean.DEFAULT_APPOINTMENT_STATUS: num_total++; break;
					case AppointmentBean.LATE_APPOINTMENT_STATUS: late.addElement(appointment); num_total++; break;
					case AppointmentBean.NO_SHOW_APPOINTMENT_STATUS: no_show.addElement(appointment); break;
					case AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS:num_rescheduled++; num_total++; break;
				}
			}
			
			
			if (needs_comma) { b.append(','); }
			b.append(this.toJSONFeed(appointment));
			needs_comma = true;
			
		}
		
		b.append("]");
		
		/*
		b.append("],");
		
		b.append("\"total\":\"" + num_total + "\",");
		b.append("\"cancelled\":\"" + num_cancelled + "\",");
		b.append("\"checkedOut\":\"" + num_checked_out + "\",");
		b.append("\"rescheduled\":\"" + num_rescheduled + "\"");
		
		b.append("}");
		*/
		
		return b.toString();
		
		
		
		
		/*
		


		String key = _session_id + "" + date_str;

		String appt_str = null;
		if (_get_all_appts)
			appt_str = this.getScheduleXML(_company, _practice_area, _display_date.getTime());
		else
		{
			Date last_update = ScheduleServlet2.last_update_hash.get(key);
			
			//System.out.println("last_update >" + CUBean.getUserDateString(last_update));
			
			if (last_update == null)
				appt_str = this.getScheduleXML(_company, _practice_area, _display_date.getTime());
			else
				appt_str = this.getScheduleXML(_company, _practice_area, _display_date.getTime(), last_update);
		}

		if (appt_str.length() > 0)
		{
			returnBuf.append(appt_str);
			ScheduleServlet2.last_update_hash.put(key, new Date());
		}

		returnBuf.append("</schedule>");
		
		returnBuf.append("<c-in num=\"" + checked_in.size() + "\">");
		Iterator itr = checked_in.iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = (AppointmentBean)itr.next();
			returnBuf.append(ScheduleServlet2.getXML(appointment));
		}
		returnBuf.append("</c-in>");

		returnBuf.append("<late num=\"" + late.size() + "\">");
		itr = late.iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = (AppointmentBean)itr.next();
			returnBuf.append(ScheduleServlet2.getXML(appointment));
		}
		returnBuf.append("</late>");
		
		returnBuf.append("<no-show num=\"" + no_show.size() + "\">");
		itr = no_show.iterator();
		while (itr.hasNext())
		{
			AppointmentBean appointment = (AppointmentBean)itr.next();
			returnBuf.append(ScheduleServlet2.getXML(appointment));
		}
		returnBuf.append("</no-show>");

		return returnBuf;
		
		*/
	}
	
	
	private String
	processAuth(String _jsonBlob, HttpServletRequest _request) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String last_value = "";
		
		String email = null;
		String password = null;
		boolean remember = false;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("email")) {
								email = value_strX;
							} else if (last_value.equals("password")) {
								password = value_strX;
							}
						} else {
							if (last_value.equals("remember")) {
								remember = true;
							}
						}
						
					}
				}
			}
		}
		
		if ((email == null) || email.isEmpty()) {
			throw new IllegalValueException("Unable to authenticate.  Please provide an email address.");
		}
		
		if ((password == null) || password.isEmpty()) {
			throw new IllegalValueException("Unable to authenticate.  Please provide a password.");
		}
		

		UKOnlineLoginBean loginBean = new UKOnlineLoginBean();
		//session.setAttribute("loginBean", loginBean); // session is not gonna work for this I'm thinking

		((UKOnlineLoginBean)loginBean).setUsername(email);
		((UKOnlineLoginBean)loginBean).setPassword(password);

		UKOnlinePersonBean person = (UKOnlinePersonBean)((UKOnlineLoginBean)loginBean).getPerson();
		//SessionCounter.add(session, person, _request); // probably need some alternative way to track sessions

		if (person == null) {
			throw new IllegalValueException("Auth failed.");
		}
		
		if (person.getFirstNameString().isEmpty()) {
			throw new IllegalValueException("Please upgrade to the latest version of Fitness Tracker in the Google Play Store.");
		}
		
		HttpSession session = _request.getSession(true);
		System.out.println("session >" + session.getId());
		
		
		
		String auth_key = session.getId();
		auth_key_hash.put(auth_key, person);
		//key_session_hash.put(auth_key, session);
				
		//return "{\"message\":[{\"type\":\"SUCCESS\",\"auth_key\":\"" + auth_key + "\"}]}";
		
		Iterator auth_key_itr = ScheduleServlet2.getKeysForPerson(person).iterator();
		while (auth_key_itr.hasNext()) {
			String old_key = (String)auth_key_itr.next();
			if (!auth_key.equals(old_key)) {
				ScheduleSocketServlet.closeClient(old_key);
			}
		}
		
		
		
		Vector roles = person.getRoles();
		if (person.hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME) ||
				person.hasRole(UKOnlineRoleBean.CASHIER_ROLE_NAME) ||
				person.hasRole(UKOnlineRoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) ||
				person.hasRole(UKOnlineRoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME)) {
			
		} else {
			throw new IllegalValueException("Unable to log in.");
		}
		
		return this.toJSON(person, auth_key);
	}
	
	private UKOnlineCompanyBean
	getSelectedCompany(UKOnlinePersonBean _logged_in_person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		CompanyBean selected_company = null;
		try {
			selected_company = _logged_in_person.getSelectedCompany();
		} catch (Exception x) {
			x.printStackTrace();
		}
		if (selected_company == null) {
			selected_company = _logged_in_person.getDepartment().getCompany();
		}
		
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(selected_company.getId());
	}
	
	private String
	saveAppointment(CompanyBean _company, UKOnlinePersonBean _logged_in_peson, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
			//_jsonBlob = "[{"name":"inputPractitioner","value":"3862"},{"name":"inputType","value":"68"},{"name":"inputAppointmentDate","value":"Thursday, December 1st 2016"},{"name":"inputAppointmentTime","value":"9:25 am"},{"name":"inputDuration","value":"45"},{"name":"clientSearch","value":"stueve"},{"name":"inputClient","value":"3128"},{"name":"inputComment","value":"creepy ways"}]";
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String last_value = "";
		
		/*
			[  
			   {  
				  "name":"inputPractitioner",
				  "value":"3862"
			   },
			   {  
				  "name":"inputType",
				  "value":"68"
			   },
			   {  
				  "name":"inputAppointmentDate",
				  "value":"Thursday, December 1st 2016"
			   },
			   {  
				  "name":"inputAppointmentTime",
				  "value":"9:25 am"
			   },
			   {  
				  "name":"inputDuration",
				  "value":"45"
			   },
			   {  
				  "name":"clientSearch",
				  "value":"stueve"
			   },
			   {  
				  "name":"inputClient",
				  "value":"3128"
			   },
			   {  
				  "name":"inputComment",
				  "value":"creepy ways"
			   }
			]
		*/
		
		int appointmentId = 0;
		int inputPractitioner = 0;
		int inputType = 0;
		String inputAppointmentDate = null;
		String inputAppointmentTime = null;
		short inputDuration = (short)0;
		int inputClient = 0;
		String inputComment = null;
		Short inputApptStatus = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("appointmentId")) {
								appointmentId = Integer.parseInt(value_strX);
							} else if (last_value.equals("inputPractitioner")) {
								inputPractitioner = Integer.parseInt(value_strX);
							} else if (last_value.equals("inputType")) {
								inputType = Integer.parseInt(value_strX);
							} else if (last_value.equals("inputAppointmentDate")) {
								inputAppointmentDate = value_strX;
							} else if (last_value.equals("inputAppointmentTime")) {
								inputAppointmentTime = value_strX;
							} else if (last_value.equals("inputDuration")) {
								inputDuration = Short.parseShort(value_strX);
							} else if (last_value.equals("inputClient")) {
								inputClient = Integer.parseInt(value_strX);
							} else if (last_value.equals("inputComment")) {
								inputComment = value_strX;
							} else if (last_value.equals("inputApptStatus")) {
								inputApptStatus = Short.parseShort(value_strX);
							}
						}
						
					}
				}
			}
		}
		
		if (inputType == 0) {
			throw new IllegalValueException("Unable to save appointment.  You must select an appointment type.");
		}
		
		AppointmentTypeBean appointmentType = AppointmentTypeBean.getAppointmentType(inputType);
		
		if (appointmentType.isClientAppointmentType()) {
			// for a client appointment type, a client must be specified
			if (inputClient == 0) {
				throw new IllegalValueException("Unable to save appointment.  Please specify a client for " + appointmentType.getLabel());
			}
		}
		
		boolean is_new_appt = false;
		AppointmentBean appointment_obj = null;
		String heading = "Appointment Saved";
		if (appointmentId == 0) {
			appointment_obj = new AppointmentBean();
			appointment_obj.setCompany(_company);
			heading = "Appointment Created";
			is_new_appt = true;
		} else {
			appointment_obj = AppointmentBean.getAppointment(appointmentId);
		}
		
		/*
		department_obj.setName(nameInput);
		department_obj.setType(DepartmentTypeBean.getDepartmentType(Integer.parseInt(typeSelect)));
		
		if ((parentSelect != null) && !parentSelect.isEmpty() && !parentSelect.equals("0")) {
			department_obj.setParent(DepartmentBean.getDepartment(Integer.parseInt(parentSelect)));
		}
				
		department_obj.save();
		*/
		
		/*
			execute() invoked in AppointmentAction
			appointmentSelect >-1
			statusSelect >-1
			pSelect >0
			practitionerSelect >3802
			typeSelect >99
			duration >30
			appt_date >Saturday, December 3, 2016
			hr >8
			mn >00
			ampm >AM
			lastname >stueve
			firstname >
			filenumber >
			clientSelect >3124
			comments >this is a sauna
			recurVal >
			recurPeriod >3
			recurPW >0
			recurPWS >2
			stopAfterN >
			stopAfterDate >
			appointmentSelect >-1
			statusSelect >-1
			clientSelect >1
			practitionerSelect >3802
			roomSelect >null
			typeSelect >99
			parentSelect >null
			appointmentDateInput >
			appointmentHourInput >null
			appointmentMinuteInput >null
			appointmentAMPMInput >null
			duration >30
			comments >this is a sauna
			appt_date >Saturday, December 3, 2016
			newAppointmentTimeInput >8:00 AM
			recurVal >0
			recurPeriod >3
			recurPW >0
			recurPWS >2
			stopAfterN >0
			stopAfterDate >
		*/
		
		short statusSelect = (short)0;
		if (inputApptStatus != null) {
			statusSelect = inputApptStatus;
		}
		Integer[] clientSelect = new Integer[1];
		clientSelect[0] = inputClient;
		
		// Thursday, December 1st 2016
		// 9:25 am
		
		//public static final SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEEE, MMMM dd yyyy");
		
		Date inputDate = this.getDateFromDateString(inputAppointmentDate + " " + inputAppointmentTime);
		System.out.println("inputDate dt >" + CUBean.getUserDateString(inputDate));
		System.out.println("inputDate tm >" + CUBean.getUserTimeString(inputDate));
		
		Calendar appointment_date = Calendar.getInstance();
		appointment_date.setTime(inputDate);
		
		appointment_obj = AppointmentAction.saveAppointment(_company, _logged_in_peson, appointmentId, inputType, statusSelect, inputPractitioner, clientSelect, appointment_date, inputDuration, inputComment, (short)0, (short)3, (short)0, (short)2, (short)0, "", false);

		StringBuffer b = new StringBuffer();
		if (appointment_obj.isClientAppointment()) {
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"" + heading + "\",\"text\":\"" + appointment_obj.getType().getLabel() + " appointment " + (is_new_appt ? "created" : "saved") + " for " + appointment_obj.getClient().getLabel() + " at " + appointment_obj.getAppointmentDateTimeString() + " with " + appointment_obj.getPractitioner().getLabel() + ".\"}]}");
		} else {
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"" + heading + "\",\"text\":\"" + appointment_obj.getType().getLabel() + " " + (is_new_appt ? "created" : "saved") + " with " + appointment_obj.getPractitioner().getLabel() + " at " + appointment_obj.getAppointmentDateTimeString() + ".\"}]}");
		}
		
		return b.toString(); 		
                
	}
	
	private String
	saveClientProfile(UKOnlineCompanyBean _company, UKOnlinePersonBean _logged_in_person, String _jsonBlob, UKOnlinePersonBean _client) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
			//_jsonBlob = "[{"name":"inputPractitioner","value":"3862"},{"name":"inputType","value":"68"},{"name":"inputAppointmentDate","value":"Thursday, December 1st 2016"},{"name":"inputAppointmentTime","value":"9:25 am"},{"name":"inputDuration","value":"45"},{"name":"clientSearch","value":"stueve"},{"name":"inputClient","value":"3128"},{"name":"inputComment","value":"creepy ways"}]";
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String last_value = "";
		
		/*
[  
   {  
      "name":"fname",
      "value":"Traci"
   },
   {  
      "name":"lname",
      "value":"Kotula"
   },
   {  
      "name":"fileNumber",
      "value":"212"
   },
   {  
      "name":"email",
      "value":"traciadk@gmail.com"
   },
   {  
      "name":"cellPhone",
      "value":"763-370-0645"
   },
   {  
      "name":"homePhone",
      "value":"952-226-5182"
   },
   {  
      "name":"inputGenderRadio",
      "value":"Female"
   },
   {  
      "name":"address1",
      "value":"3374 Sycamore Trail SW"
   },
   {  
      "name":"address2",
      "value":""
   },
   {  
      "name":"city",
      "value":"Prior Lake"
   },
   {  
      "name":"state",
      "value":"MN"
   },
   {  
      "name":"code",
      "value":"55372"
   }
]
		*/
		
		int id = -1;
		String fname = null;
		String mname = null;
		String lname = null;
		String fileNumber = null;
		String email = null;
		String cellPhone = null;
		String homePhone = null;
		String inputGenderRadio = null;
		String address1 = null;
		String address2 = null;
		String city = null;
		String state = null;
		String code = null;
		
		String password = null;
		String prefix = null;
		String suffix = null;
		String dob = null;
		
		boolean isActive = false;
		
		boolean isNSIPMClient = false;
		BigDecimal NSIPMCoveragePercentage = BigDecimal.ZERO;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("fname")) {
								fname = value_strX;
							} else if (last_value.equals("mname")) {
								mname = value_strX;
							} else if (last_value.equals("lname")) {
								lname = value_strX;
							} else if (last_value.equals("fileNumber")) {
								fileNumber = value_strX;
							} else if (last_value.equals("email")) {
								email = value_strX;
							} else if (last_value.equals("cellPhone")) {
								cellPhone = value_strX;
							} else if (last_value.equals("homePhone")) {
								homePhone = value_strX;
							} else if (last_value.equals("inputGenderRadio")) {
								inputGenderRadio = value_strX;
							} else if (last_value.equals("address1")) {
								address1 = value_strX;
							} else if (last_value.equals("address2")) {
								address2 = value_strX;
							} else if (last_value.equals("city")) {
								city = value_strX;
							} else if (last_value.equals("state")) {
								state = value_strX;
							} else if (last_value.equals("code")) {
								code = value_strX;
							} else if (last_value.equals("id")) {
								try {
									id = Integer.parseInt(value_strX);
								} catch (Exception x) {
									x.printStackTrace();
								}
							} else if (last_value.equals("password")) {
								password = value_strX;
							} else if (last_value.equals("prefix")) {
								prefix = value_strX;
							} else if (last_value.equals("suffix")) {
								suffix = value_strX;
							} else if (last_value.equals("dob")) {
								dob = value_strX;
							} else if (last_value.equals("subscription")) {
								isActive = value_strX.equals("on");
							} else if (last_value.equals("isNSIPMClient")) {
								isNSIPMClient = value_strX.equals("on");
							} else if (last_value.equals("NSIPMCoverage")) {
								try {
									NSIPMCoveragePercentage = new BigDecimal(value_strX);
								} catch (Exception x) {
									x.printStackTrace();
								}
							}
						}
						
					}
				}
			}
		}
		
		if (_client == null) {
			_client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(id);
		}
		
		//ClientProfileAction.saveClient(_company, _logged_in_peson, _client, fname, lname, homePhone, cellPhone, email, fileNumber, inputGenderRadio, address1, address2, city, state, code);
		ClientProfileAction.saveClient(_company, _logged_in_person, _client, fname, mname, lname, homePhone, cellPhone, email, fileNumber, inputGenderRadio, address1, address2, city, state, code, password, prefix, suffix, dob, isActive);
		//saveClient(UKOnlineCompanyBean admin_company, UKOnlinePersonBean _mod_person, UKOnlinePersonBean adminPerson, String firstname, String middlename, String lastname, String phone, String cell, String email, String filenumber, String gender, String address1, String address2, String city, String state, String zipcode, String password, String prefix, String suffix) throws TorqueException, ObjectNotFoundException, IllegalValueException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, Exception {
		
		if (_logged_in_person.isAdministrator()) {
			PersonSettingsBean settings = PersonSettingsBean.getPersonSettings(_client, true);
			settings.setIsNSIPMClient(isNSIPMClient);
			settings.setNSIPMCoveragePercentage(NSIPMCoveragePercentage);
			settings.save();
		}
		
		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Profile Saved\",\"text\":\"Profile saved for " + JSONObject.escape(_client.getLabel()) + ".\"}]}");
		return b.toString();  
	}
	
	private String
	saveNewClient(UKOnlineCompanyBean _company, UKOnlinePersonBean _logged_in_peson, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String last_value = "";
		
		String fname = null;
		String lname = null;
		String fileNumber = null;
		String email = null;
		String cellPhone = null;
		String homePhone = null;
		String gender = null;
		String group = null;
		Integer groupSelect = null;
		Integer marketingPlanSelect = null;
		Short relationshipSelect = null;
		Integer referralClientSelect = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("fname")) {
								fname = value_strX;
							} else if (last_value.equals("lname")) {
								lname = value_strX;
							} else if (last_value.equals("email")) {
								email = value_strX;
							} else if (last_value.equals("cellPhone")) {
								cellPhone = value_strX;
							} else if (last_value.equals("homePhone")) {
								homePhone = value_strX;
							} else if (last_value.equals("fileNumber")) {
								fileNumber = value_strX;
							} else if (last_value.equals("inputGenderRadio")) {
								gender = value_strX;
							} else if (last_value.equals("inputGroupUnderCareRadio")) {
								group = value_strX;
							} else if (last_value.equals("selectGroupUnderCare")) {
								groupSelect = Integer.parseInt(value_strX);
							} else if (last_value.equals("selectMarketingPlan")) {
								marketingPlanSelect = Integer.parseInt(value_strX);
							} else if (last_value.equals("relationshipSelect")) {
								relationshipSelect = Short.parseShort(value_strX);
							} else if (last_value.equals("referralSelect")) {
								referralClientSelect = Integer.parseInt(value_strX);
							}
						}
						
					}
				}
			}
		}
	
		UKOnlinePersonBean adminPerson = new UKOnlinePersonBean();	
		ClientNewAction.saveNewClient(_company, _logged_in_peson, adminPerson, fname, lname, homePhone, cellPhone, email, fileNumber, gender, marketingPlanSelect, referralClientSelect, groupSelect, group, relationshipSelect, -1, Boolean.FALSE);
		
		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"New Client Saved\",\"text\":\"" + JSONObject.escape(adminPerson.getLabel()) + " created.\"}]}");
		return b.toString();
	}
	
	private String
	saveLedgerRow(UKOnlinePersonBean _logged_in_peson, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String last_value = "";
		
		//String prac_sel_ = null;
		//String comm_input_ = null;
		
		ValeoOrderBean order = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.startsWith("prac_sel_")) {
								if (!value_strX.equals("0")) {
									String orderline_id_str = last_value.substring(9);
									System.out.println("orderline_id_str(1) >" + orderline_id_str);
									UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(value_strX));
									System.out.println("practitioner(1) >" + practitioner.getLabel());
									CheckoutOrderline orderline = ValeoOrderBean.getCheckoutOrderline(Integer.parseInt(orderline_id_str));
									orderline.setPractitionerId(practitioner.getId());
									
									try {
										CheckoutCodeBean item = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
										BigDecimal commission_amount_bd = practitioner.getCommission(item, orderline.getActualAmount());
										orderline.setCommission(commission_amount_bd);
									} catch (Exception x) {
										x.printStackTrace();
									}
									
									orderline.save();

									order = (ValeoOrderBean)ValeoOrderBean.getOrder(orderline.getOrderid());
									//prac_sel_ = value_strX;
								}
							} else if (last_value.startsWith("comm_input_")) {
								/* removed 12/28/18 - making this read only - adjust when changing practitioners
								String orderline_id_str = last_value.substring(11);
								System.out.println("orderline_id_str(2) >" + orderline_id_str);
								BigDecimal commission_amount_bd = new BigDecimal(value_strX);
								System.out.println("commission_amount_bd(2) >" + commission_amount_bd);
								CheckoutOrderline orderline = ValeoOrderBean.getCheckoutOrderline(Integer.parseInt(orderline_id_str));
								orderline.setCommission(commission_amount_bd);
								orderline.save();
								*/
								//lname = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		StringBuffer b = new StringBuffer();
		if (order == null) {
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Ledger Changes Saved\",\"text\":\"Ledger changes saved.\"}]}");
		} else {
			order.invalidate();
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Ledger Changes Saved\",\"text\":\"Ledger changes saved for " + PersonBean.getPerson(order.getPersonId()).getLabel() + ".\"}]}");
		}
		return b.toString();
	}
	
	private String
	getAvailableAppointmentTimes(UKOnlineCompanyBean _company, UKOnlinePersonBean _logged_in_peson, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		// [{"name":"inputType","value":"122"},{"name":"prac_3121","value":"on"},{"name":"prac_3853","value":"on"},{"name":"inputGroupUnderCareRadio","value":"New"}]
		
		String last_value = "";
		
		AppointmentTypeBean apptType = null;
		Vector selectedPractitioners = new Vector();
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputType")) {
								apptType = AppointmentTypeBean.getAppointmentType(Integer.parseInt(value_strX));
							} else if (last_value.startsWith("prac_")) {
								selectedPractitioners.addElement(UKOnlinePersonBean.getPerson(Integer.parseInt(last_value.substring(5))));
								//System.out.println("");
							}
						}
					}
				}
			}
		}
	
		//UKOnlinePersonBean adminPerson = new UKOnlinePersonBean();	
		//ClientNewAction.saveNewClient(_company, _logged_in_peson, adminPerson, fname, lname, homePhone, cellPhone, email, fileNumber, gender, marketingPlanSelect, referralClientSelect, groupSelect, group, relationshipSelect, -1, Boolean.FALSE);
		
		Iterator itr = selectedPractitioners.iterator();
		while (itr.hasNext()) {
			UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
			System.out.println("practitioner found >" + practitioner.getLabel());
		}
		
		/*
		PractitionerScheduleBean
		*/
		
		int num_practitioners = selectedPractitioners.size();
		
		if (num_practitioners > 4) {
			throw new IllegalValueException("Unable to coordinate more than 4 practitioner schedules.");
		}
		
		int num_days_to_search = 30;
		int num_results_to_return = 12;
		if (selectedPractitioners.size() == 1) {
			num_results_to_return = 36;
		} else if (selectedPractitioners.size() == 2) {
			num_results_to_return = 24;
		}
		int num_results_found = 0;
		
		if (this.more_map.containsKey(_logged_in_peson)) {
			Integer num_more = (Integer)this.more_map.get(_logged_in_peson);
			num_results_to_return += (num_results_to_return * num_more);
		}
		
		Vector hash_keys = new Vector();
		HashMap results_hash = new HashMap();
		
		Calendar date_in_question = Calendar.getInstance();
		Vector items_for_selected_practitioners = PractitionerScheduleItemBean.getPractitionerScheduleTemplateItems(selectedPractitioners);
		
		//Date now = new Date();
		//System.out.println("START THING >" + now.getTime());
		
		for (int ix = 0; ix < num_days_to_search; ix++) {
			
			date_in_question.add(Calendar.DATE, 1);
			
			UKOnlinePersonBean practitioner_1 = null;
			UKOnlinePersonBean practitioner_2 = null;
			UKOnlinePersonBean practitioner_3 = null;
			UKOnlinePersonBean practitioner_4 = null;

			PractitionerScheduleItemBean practitioner_1_item = null;
			PractitionerScheduleItemBean practitioner_2_item = null;
			PractitionerScheduleItemBean practitioner_3_item = null;
			PractitionerScheduleItemBean practitioner_4_item = null;
					
			itr = items_for_selected_practitioners.iterator();
			while (itr.hasNext()) {
				PractitionerScheduleItemBean item = (PractitionerScheduleItemBean)itr.next();
				if (item.getDayOfWeek() == date_in_question.get(Calendar.DAY_OF_WEEK)) {
		
					if (item.getAppointmentTypeTemplate().isClientAppointmentType()) {
						
						System.out.println("focus date >" + CUBean.getUserDateString(date_in_question.getTime()) );
						
						String date_key = CUBean.getUserDateString(date_in_question.getTime(), "EEEE, MMM d");

						System.out.println("item >" + item.getParent().getPractitioner().getLabel() + " <-> " + item.getLabel());

						UKOnlinePersonBean practitioner_for_item = item.getParent().getPractitioner();
						if (practitioner_1 == null || practitioner_1.equals(practitioner_for_item)) {
							practitioner_1 = practitioner_for_item;
							practitioner_1_item = item;
						} else if (practitioner_2 == null || practitioner_2.equals(practitioner_for_item)) {
							practitioner_2 = practitioner_for_item;
							practitioner_2_item = item;
						} else if (practitioner_3 == null || practitioner_3.equals(practitioner_for_item)) {
							practitioner_3 = practitioner_for_item;
							practitioner_3_item = item;
						} else if (practitioner_4 == null || practitioner_4.equals(practitioner_for_item)) {
							practitioner_4 = practitioner_for_item;
							practitioner_4_item = item;
						}

						int num_not_null = 0;
						if (practitioner_1_item != null) { num_not_null++; }
						if (practitioner_2_item != null) { num_not_null++; }
						if (practitioner_3_item != null) { num_not_null++; }
						if (practitioner_4_item != null) { num_not_null++; }

						if (num_not_null == num_practitioners) {

							System.out.println("found stuff to test >" + num_not_null);
							
							// first ensure that all of the start times are within, say, 30 minutes of each other

							AppointmentBean prac_1_appt = this.testAppt(_company, apptType, practitioner_1, practitioner_1_item, date_in_question);
							AppointmentBean prac_2_appt = this.testAppt(_company, apptType, practitioner_2, practitioner_2_item, date_in_question);
							AppointmentBean prac_3_appt = this.testAppt(_company, apptType, practitioner_3, practitioner_3_item, date_in_question);
							AppointmentBean prac_4_appt = this.testAppt(_company, apptType, practitioner_4, practitioner_4_item, date_in_question);

							Vector appts = new Vector();
							int num_appt_not_null = 0;
							if (prac_1_appt != null) { appts.addElement(prac_1_appt); num_appt_not_null++; }
							if (prac_2_appt != null) { appts.addElement(prac_2_appt); num_appt_not_null++; }
							if (prac_3_appt != null) { appts.addElement(prac_3_appt); num_appt_not_null++; }
							if (prac_4_appt != null) { appts.addElement(prac_4_appt); num_appt_not_null++; }

							if ((num_appt_not_null == num_practitioners) && this.doApptsOverlap(appts)) {
								
								
								System.out.println("    ##$$$$ badda bing!");

								Vector appts_vec = null;
								if (results_hash.containsKey(date_key)) {
									appts_vec = (Vector)results_hash.get(date_key);
								} else {
									appts_vec = new Vector();
									results_hash.put(date_key, appts_vec);
									hash_keys.addElement(date_key);
								}
								appts_vec.addElement(appts);
								num_results_found++;

								if (num_results_found == num_results_to_return) {
									return this.availableApptTimesToJSON(hash_keys, results_hash);
								}
								
							}

							System.out.println("");

						}
					}
				}


			}
		}
		
		if (hash_keys.isEmpty()) {
			StringBuffer b = new StringBuffer();
			b.append("{\"message\":[{\"type\":\"ERROR\",\"heading\":\"No Results Found\",\"text\":\"No available appointment times found for the selected practitioner(s)\"}]}");
			return b.toString();
		} else {
			return this.availableApptTimesToJSON(hash_keys, results_hash);
		}
	}
	
	private String
	saveAppointmentTimes(UKOnlineCompanyBean _company, UKOnlinePersonBean _logged_in_peson, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		// [{"name":"inputType","value":"122"},{"name":"prac_3121","value":"on"},{"name":"prac_3853","value":"on"},{"name":"inputGroupUnderCareRadio","value":"New"}]
		
		String last_value = "";
		
		AppointmentTypeBean apptType = null;
		//Vector selectedPractitioners = new Vector();
		UKOnlinePersonBean inputClient = null;
		Vector selectedApptTimes = new Vector();
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputType")) {
								apptType = AppointmentTypeBean.getAppointmentType(Integer.parseInt(value_strX));
							} else if (last_value.equals("inputClient")) {
								inputClient = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(value_strX));
							} else if (last_value.startsWith("prac_item")) {
								String itemStr = last_value.substring(10);
								System.out.println("itemStr >" + itemStr);
								if (itemStr.indexOf('-') > -1) {
									String[] strSplit = itemStr.split("-");
									for (int i = 0; i < strSplit.length; i++) {
										selectedApptTimes.addElement(strSplit[i]);
									}
								} else {
									selectedApptTimes.addElement(itemStr);
								}
							}
						}
					}
				}
			}
		}
		
		String returnText = "";
		boolean is_new_appt = true;
		
		if (selectedApptTimes.isEmpty()) {
			throw new IllegalValueException("No appointment times selected.");
		}
		
		Iterator itr = selectedApptTimes.iterator();
		while (itr.hasNext()) {
			String itemStr = (String)itr.next();
			String[] innerSplit = itemStr.split("\\|");
			//System.out.println("Integer.parseInt(innerSplit[0]) >" + Integer.parseInt(innerSplit[0]));
			UKOnlinePersonBean pracForItem = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(innerSplit[0]));
			Calendar itemTime = Calendar.getInstance();
			itemTime.setTimeInMillis(Long.parseLong(innerSplit[1]) * 1000l);
			Integer[] arr = new Integer[1];
			arr[0] = inputClient.getId();

			//System.out.println("pracForItem >" + pracForItem.getLabel());
			//System.out.println("inputClient >" + inputClient.getLabel());
			AppointmentBean appointment_obj = AppointmentAction.saveAppointment(_company, _logged_in_peson, 0, apptType.getId(), AppointmentBean.DEFAULT_APPOINTMENT_STATUS, pracForItem.getId(), arr, itemTime, apptType.getDuration(), "", (short)0, (short)3, (short)0, (short)2, (short)0, "", false);
			if (returnText.isEmpty()) {
				returnText = appointment_obj.getType().getLabel() + " appointment " + (is_new_appt ? "created" : "saved") + " for " + appointment_obj.getClient().getLabel() + " at " + appointment_obj.getAppointmentDateTimeString() + " with " + appointment_obj.getPractitioner().getLabel() + ".";
			}
			
		}
		
		
		StringBuffer b = new StringBuffer();
		int num_appts = selectedApptTimes.size();
		if (num_appts > 1) {
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Appointments Created\",\"text\":\"" + returnText + "  " + (num_appts - 1) + " other appointments created.\"}]}");
		} else {
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Appointment Created\",\"text\":\"" + returnText + "\"}]}");
		}
		
		return b.toString();
	}
	
	private HashMap more_map = new HashMap();
	
	private AppointmentBean
	testAppt(UKOnlineCompanyBean _company, AppointmentTypeBean _apptType, UKOnlinePersonBean _practitioner, PractitionerScheduleItemBean _item, Calendar _date_in_question) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		if (_practitioner == null) {
			return null;
		}
		
		AppointmentBean test_appt = new AppointmentBean();
		test_appt.setCompany(_company);
		test_appt.setType(_apptType);
		test_appt.setDuration(_apptType.getDuration());

		test_appt.setPractitioner(_practitioner);
		Calendar practitioner_1_date = (Calendar)_date_in_question.clone();
		practitioner_1_date.set(Calendar.HOUR_OF_DAY, _item.getStartHourOfDay());
		practitioner_1_date.set(Calendar.MINUTE, _item.getStartMinute());
		test_appt.setAppointmentDate(practitioner_1_date.getTime());
		if (!AppointmentBean.hasConflict(test_appt, false)) {
			System.out.println("no conflict for >" + _practitioner.getLabel() + "  >>" + _item.getLabel());
			return test_appt;
		}
		return null;
	}
	
	private boolean
	doApptsOverlap(Vector _appts) {
		// compare each appt with every other appt in the vector
		
		AppointmentBean compare_appt = null;
		
		for (int compare_index = 0; compare_index < _appts.size(); compare_index++) {
			compare_appt = (AppointmentBean)_appts.elementAt(compare_index);
			
			Iterator itr = _appts.iterator();
			for (int i = 0; itr.hasNext(); i++) {
				if (i != compare_index) {
					AppointmentBean appt = (AppointmentBean)itr.next();
					if (!this.doApptsOverlap(appt, compare_appt)) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	
	private boolean
	doApptsOverlap(AppointmentBean _appt1, AppointmentBean _appt2) {
		long overlap_millis = 1200000l; // 20x60x1000 - appointments must overlap at least 20 minutes
		
		long appt_1_start = _appt1.getAppointmentStartUnixTimestampInMillisNonAdjusted();
		long appt_1_end = _appt1.getAppointmentEndUnixTimestampInMillisNonAdjusted();
		//int appt_1_minutes = _appt1.getDuration();
		
		long appt_2_start = _appt2.getAppointmentStartUnixTimestampInMillisNonAdjusted();
		long appt_2_end = _appt2.getAppointmentEndUnixTimestampInMillisNonAdjusted();
		//int appt_2_minutes = _appt2.getDuration();
		
		if ( (appt_1_start == appt_2_start) || (appt_1_end == appt_2_end) ) {
			//if (appt_1_end == appt_2_end) {
			//	return true; // appointment start and end times are identical - return true regardless of amount of time actually overlapped, I guess
			//}
			// appointment end times are not equal, but start times are - make sure that there's proper overlap
			//Math.abs(appt_1_minutes - appt_2_minutes)
			// I'm thinking that if the start, or end times are equal, summarily return a positive everlap condition - I can filter by appt type elsewhere I'm thinking
			return true;
		}
		
		if (appt_1_start < appt_2_start) {
			// appointment 1 starts prior to appointment 2
			if (appt_1_end > appt_2_end) {
				// appointment 1 starts prior to appointment 2, but end after, completely engulfing appt2, therefore, complete overlap
				return true;
			}
			// appt 1 ends prior to appt 2 end
			// ensure that appt 1 doesn't end prior to appt 2 starting
			if (appt_1_end <= appt_2_start) {
				return false; // appt 1 ends prior to appt 2 starting - no overlap
			}
			// there must be some overlap - what's the overlap amount??
			long overlap_amount = appt_1_end - appt_2_start;
			System.out.println("overlap_amount(1) >" + overlap_amount);
			return overlap_amount >= overlap_millis;
		} else { // appt_1_start > appt_2_start
			// appointment 1 starts after appointment 2
			if (appt_2_end > appt_1_end) {
				// appointment 1 starts after appointment 2 and appointment 2 ends after appoinment 1 completely engulfing appt 1, complete overlap
				return true;
			}
			// appt 2 ends prior to appt 1 end
			// ensure that appt 2 doesn't end prior to appt 1 starting
			if (appt_2_end <= appt_1_start) {
				return false; // appt 2 ends prior to appt 1 starting - no overlap
			}
			// there must be some overlap - what's the overlap amount??
			long overlap_amount = appt_2_end - appt_1_start;
			System.out.println("overlap_amount(2) >" + overlap_amount);
			return overlap_amount >= overlap_millis;
		}
		
	}
	
	private String availableApptTimesToJSON(Vector keys, HashMap appt_hash) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuilder b = new StringBuilder();
		b.append("{\"date\":[");
		boolean needs_comma = false;
		Iterator date_itr = keys.iterator();
		for (int i = 1; date_itr.hasNext(); i++) {
			String date_key = (String)date_itr.next();
			if (needs_comma) { b.append(','); }
			b.append("{\"label\":\"" + JSONObject.escape(date_key) + "\",");
			b.append("\"id\":" + i + ",");
			b.append("\"appt\":[");
			boolean needs_comma_2 = false;
			Vector appts_for_date = (Vector)appt_hash.get(date_key);
			Iterator itr = appts_for_date.iterator();
			while (itr.hasNext()) {
				if (needs_comma_2) { b.append(','); }
				b.append(this.apptTimesToJSON((Vector)itr.next()));
				needs_comma_2 = true;
			}
			b.append("]}");
			needs_comma = true;
		}
		b.append("]}");
		return b.toString();
	}
	
	private String apptTimesToJSON(Vector _vec) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		String labelStr = "";
		String valStr = "";
		
		Iterator itr = _vec.iterator();
		while (itr.hasNext()) {
			AppointmentBean test_appt = (AppointmentBean)itr.next();
			String nameStr = test_appt.getPractitioner().getFirstNameString();
			if (nameStr.isEmpty()) {
				nameStr = test_appt.getPractitioner().getLastNameString();
			}
			if (labelStr.isEmpty()) {
				labelStr = nameStr + " " + test_appt.getAppointmentTimeString();
				valStr = test_appt.getPractitioner().getId() + "|" + test_appt.getAppointmentStartUnixTimestamp();
			} else {
				labelStr += (" - " + nameStr + " " + test_appt.getAppointmentTimeString());
				valStr += ("-" + test_appt.getPractitioner().getId() + "|" + test_appt.getAppointmentStartUnixTimestamp());
			}
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(labelStr) + "\",");
		b.append("\"value\":\"" + JSONObject.escape(valStr) + "\"}");
		return b.toString();
	}
	
	private String
	saveFollowUp(UKOnlinePersonBean _logged_in_person, String _jsonBlob, boolean _mark_as_reviewed, boolean _delete) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String last_value = "";
		
		ReviewReason review_reason = null;
		Vector practitioners = new Vector();
		//UKOnlinePersonBean practitioner = null;
		short fuInput = 0;
		short reviewInput = 0;
		String reviewOnInput = null;
		int value = 0;
		int period = 0;
		String note = "";
		ClientReviewReason client_review_reason = null;
		UKOnlinePersonBean client = null;
		
		boolean me_fu = false;
		boolean dr_fu = false;
		boolean email_client_directly = false;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("rrCList")) {
								if (!value_strX.equals("0")) {
									review_reason = ReviewReason.getReviewReason(Integer.parseInt(value_strX));
								}
							} else if (last_value.equals("rpList")) {
								if (!value_strX.equals("0")) {
									practitioners.addElement((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(value_strX)));
								}
							} else if (last_value.equals("fuInput")) {
								fuInput = Short.parseShort(value_strX);
							} else if (last_value.equals("reviewInput")) {
								reviewInput = Short.parseShort(value_strX);
							} else if (last_value.equals("reviewOnInput")) {
								reviewOnInput = value_strX;
							} else if (last_value.equals("rList1")) {
								value = Integer.parseInt(value_strX);
							} else if (last_value.equals("rList2")) {
								period = Integer.parseInt(value_strX);
							} else if (last_value.equals("followUpNote")) {
								note = value_strX;
							} else if (last_value.equals("reviewList")) {
								if (!value_strX.equals("0")) {
									client_review_reason = ClientReviewReason.getClientReviewReason(Integer.parseInt(value_strX));
								}
							} else if (last_value.equals("me_fu")) {
								me_fu = true;
							} else if (last_value.equals("dr_fu")) {
								dr_fu = true;
							} else if (last_value.equals("email_client_directly")) {
								email_client_directly = true;
							} else if (last_value.equals("id")) {
								client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(value_strX));
							}
						}
						
					}
				}
			}
		}


		if (_delete) {
			ClientReviewReason.delete(client_review_reason.getId());
		} else {
			
			if (review_reason == null) {
				throw new IllegalValueException("Please select a Follow-Up reason.");
			}
			
			if (review_reason == null) {
				throw new IllegalValueException("Please select a Follow-Up reason.");
			}

			Date reviewDate = null;
			if (reviewInput == (short)0) {
				if ((reviewOnInput == null) || reviewOnInput.isEmpty()) {
					throw new IllegalValueException("Please specify a Follow-up On date.");
				}
				reviewDate = CUBean.getDateFromUserString(reviewOnInput);
			} else {
				Calendar now = Calendar.getInstance();
				switch (period) {
					case 1: now.add(Calendar.DATE, value); break;
					case 2: now.add(Calendar.WEEK_OF_YEAR, value); break;
					case 3: now.add(Calendar.MONTH, value); break;
					case 4: now.add(Calendar.YEAR, value); break;
				}
				reviewDate = now.getTime();
			}

			String contact_str = "";
			switch (fuInput) {
				case 1: contact_str = "Left Voice Mail"; break;
				case 3: contact_str = "Requested Call Back"; break;
				case 4: contact_str = "Sent Email"; break;
			}

			if (note.isEmpty()) {
				note = contact_str;
			} else if (!contact_str.isEmpty()) {
				note += ", " + contact_str;
			}

			if (client_review_reason == null) {
				client_review_reason = new ClientReviewReason();
			}

			client_review_reason.setPerson(client);
			client_review_reason.setReviewDate(reviewDate);
			client_review_reason.setReviewReason(review_reason);
			client_review_reason.setNote(note);
			
			if (me_fu) {
				practitioners.addElement(_logged_in_person);
			}
			if (dr_fu) {
				practitioners.addElement((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(3121));
			}
			if (!practitioners.isEmpty()) {
				client_review_reason.setPractitioners(practitioners);
			}
			client_review_reason.setReviewed(_mark_as_reviewed);
			client_review_reason.setCreateOrModifyPerson(_logged_in_person);
			client_review_reason.setEmailClient(email_client_directly);
			client_review_reason.save();

		}

		//writer.println(ScheduleServlet.getStatsXML(person, adminCompany));
		
		String heading = "Client Follow-up Saved";
		String text = "Follow-up saved for " + client.getLabel() + ".";
		
		if (_mark_as_reviewed) {
			text = "Follow-up marked as reviewed for " + client.getLabel() + ".";
		} else if (_delete) {
			heading = "Client Follow-up Deleted";
			text = "Follow-up deleted for " + client.getLabel() + ".";
		}
				
		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"" + heading + "\",\"text\":\"" + JSONObject.escape(text) + "\"}],");
		b.append("\"review\":[");
		boolean needs_comma = false;
		Iterator itr = ClientReviewReason.getClientReviewReasons(client).iterator();
		while (itr.hasNext()) {
			ClientReviewReason obj = (ClientReviewReason)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(obj));
			needs_comma = true;
		}
		b.append("]}");
		
		return b.toString();
	}
	
	private String
	saveSOAP(UKOnlineCompanyBean _company, UKOnlinePersonBean _logged_in_peson, String _jsonBlob, UKOnlinePersonBean _person) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String last_value = "";
		
		SOAPNotesBean note = null;
		String s = null;
		String o = null;
		String a = null;
		String p = null;
		
		// [{"name":"s","value":"<p>sdfsdf</p>"},{"name":"o","value":"<p>sdfsdf</p>"},{"name":"a","value":"<p>sdfsdf</p>"},{"name":"p","value":"<p>sdfsdf</p>"}]
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("soapList")) {
								note = SOAPNotesBean.getSOAPNotes(Integer.parseInt(value_strX));
							} else if (last_value.equals("s")) {
								s = value_strX;
							} else if (last_value.equals("o")) {
								o = value_strX;
							} else if (last_value.equals("a")) {
								a = value_strX;
							} else if (last_value.equals("p")) {
								p = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		if (note == null) {
			note = new SOAPNotesBean();
			note.setAnalysisDate(new Date());
		}

		note.setPerson(_person);
		note.setPracticeArea(PracticeAreaBean.getPracticeArea(7)); // shrug - should I force a selection on this??
		note.setSNote(s);
		note.setONote(o);
		note.setANote(a);
		note.setPNote(p);
		note.setCreateOrModifyPerson(_logged_in_peson);
		boolean is_new_note = note.isNew();
		note.save();

		try {
			Iterator itr = CareDetailsBean.getCareDetails(_person).iterator();
			if (itr.hasNext()) {
				String message = "S : " + note.getSNoteString() + "\r\n\r\nO : " + note.getONoteString() + "\r\n\r\nA : " + note.getANoteString() + "\r\n\r\nP : " + note.getPNoteString();
				while (itr.hasNext()) {
					CareDetailsBean care_details = (CareDetailsBean)itr.next();
					CUBean.sendEmail(care_details.getPreferredPractitioner().getEmailString(), CUBean.getProperty("cu.adminEmail"), (is_new_note ? "New" : "Updated") + " SOAP Note for " + _person.getLabel(), message);
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}

		//writer.println(ScheduleServlet.getStatsXML(person, adminCompany));
		
		String heading = "SOAP Note Saved";
		String text = "SOAP Note saved for " + _person.getLabel() + ".";
				
		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"" + heading + "\",\"text\":\"" + JSONObject.escape(text) + "\"}],");
		b.append("\"soap\":[");
		boolean needs_comma = false;
		Iterator itr = SOAPNotesBean.getSOAPNotes(_person).iterator();
		while (itr.hasNext()) {
			SOAPNotesBean obj = (SOAPNotesBean)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(obj));
			needs_comma = true;
		}
		b.append("]}");
		
		return b.toString();
	}
	
	private String
	doCheckout(HttpSession _session, Cookie _workstation_cookie, UKOnlineCompanyBean _company, UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
			//_jsonBlob = "[{"name":"qty1tblCharges","value":"1"},{"name":"inputName1tblCharges","value":"16.74"},{"name":"sel1tblCharges","value":"0"},{"name":"hiddeninputCheck1tblPrevious","value":"50233"},{"name":"hiddeninputCheck2tblPrevious","value":"50128"},{"name":"method","value":"1"},{"name":"amount_1","value":""},{"name":"amount_2","value":"0.00"},{"name":"method1tblCredits","value":"Account"},{"name":"tendered1tblCredits","value":"17.96"},{"name":"change1tblCredits","value":"0.00"},{"name":"id1tblCredits","value":"0"},{"name":"discount_perc","value":""},{"name":"client_taxable","value":"1"},{"name":"numPrevious","value":"0"},{"name":"isCheckout","value":"1"},{"name":"order_id","value":"0"},{"name":"h_subtotal","value":"16.74"},{"name":"h_tax","value":"1.22"},{"name":"h_total","value":"17.959999999999997"},{"name":"h_previousBalance","value":"0"},{"name":"h_charges","value":"17.959999999999997"},{"name":"h_credits","value":"17.96"},{"name":"h_owes","value":"17.96"},{"name":"h_reverse","value":"0"},{"name":"memo","value":""}]";
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String last_value = "";
		
		
		HashMap<String,String> request_param_hash = new HashMap();
		
		/*
		int appointmentId = 0;
		int inputPractitioner = 0;
		int inputType = 0;
		String inputAppointmentDate = null;
		String inputAppointmentTime = null;
		short inputDuration = (short)0;
		int inputClient = 0;
		String inputComment = null;
		*/
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						
						System.out.println("putting key >" + last_value + ",  value >>" + value_strX);
						request_param_hash.put(last_value, value_strX);
						/*
							if (last_value.equals("appointmentId")) {
								appointmentId = Integer.parseInt(value_strX);
							}
						*/
					}
				}
			}
		}
				
		String sales_receipt_url = CheckoutAction.doCheckout(_session, null, _workstation_cookie, request_param_hash, _company, _logged_in_person);
		
		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Checkout Complete\",\"text\":\"Checkout complete.\",\"pdfURL\":\"" + JSONObject.escape(sales_receipt_url) + "\"}]}");
        return b.toString();
		
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	public String getServletInfo() {
		return "Short description";
	}

	@Override
	public void init() throws ServletException {
		super.init(); //To change body of generated methods, choose Tools | Templates.
		AppointmentBean.addAppointmentEventListener(this);
	}

	@Override
	public void newAppointment(NewAppointmentEvent _event) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	
		System.out.println("   @@@@@@@@ --   appointment created");
		
		// push this to the WebSocket clients
		// build JSON string and send it as a plain text message, I guess
		
		try {
			//System.out.println(this.toJSON(_event));
			ScheduleSocketServlet.broadcastMessage(this.toJSON(_event));
		} catch (Exception x) {
			x.printStackTrace();
		}
		
	}

	@Override
	public void updatedAppointment(UpdatedAppointmentEvent _event) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		
		System.out.println("   @@@@@@@@ --   appointment updated");
		
		// push this to the WebSocket clients
		// build JSON string and send it as a plain text message, I guess
		
		try {
			//System.out.println(this.toJSON(_event));
			ScheduleSocketServlet.broadcastMessage(this.toJSON(_event));
		} catch (Exception x) {
			x.printStackTrace();
		}
		
	}

	@Override
	public void deletedAppointment(DeletedAppointmentEvent _event) {
		//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		
		System.out.println("   @@@@@@@@ --   appointment deleted");
		
		// push this to the WebSocket clients
		// build JSON string and send it as a plain text message, I guess
		
		try {
			//System.out.println(this.toJSON(_event));
			ScheduleSocketServlet.broadcastMessage(this.toJSON(_event));
		} catch (Exception x) {
			x.printStackTrace();
		}
		
	}
	
	public static UKOnlinePersonBean
	verifyKey(String _key) throws IllegalValueException {
		
		//System.out.println("verifyKey >" + auth_key_hash + " >" + auth_key_hash.size());
		if (auth_key_hash.containsKey(_key)) {
			return auth_key_hash.get(_key);
		} else {
			throw new IllegalValueException("Unable to verify session key >" + _key + ".  Please sign out and then sign back in.");
		}
	}
	
	/*
	public static HttpSession
	getSessionForKey(String _key, HttpServletRequest _request) throws IllegalValueException {

		// let's try to maintain this session by associating it with the verification key
		// I'll need some kind of mechanism to maintain this session
		
		HttpSession session_obj = key_session_hash.get(_key);
		if (session_obj == null) {
			session_obj = _request.getSession(true);
			System.out.println("NEW SESSION >" + session_obj.getId() + "   mapped to key >> " + _key);
			key_session_hash.put(_key, session_obj);
		}
		return session_obj;
	}
	*/
	
	public static Vector
	getKeysForPerson(UKOnlinePersonBean _person) throws IllegalValueException {
		
		Vector vec = new Vector();
		Iterator itr = auth_key_hash.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String)itr.next();
			UKOnlinePersonBean person_for_key = auth_key_hash.get(key);
			if (person_for_key.equals(_person)) {
				vec.addElement(key);
			}
		}
		return vec;
	}
	
	private static String
	getStringFromBigDecimal(BigDecimal _bd) {
		if (_bd == null) {
			return "";
		}
		String str = _bd.setScale(2, RoundingMode.HALF_UP).toString();
		int index = str.indexOf(".00");
		if (index > -1) {
			return str.substring(0, index);
		}
		return str;
	}
}
