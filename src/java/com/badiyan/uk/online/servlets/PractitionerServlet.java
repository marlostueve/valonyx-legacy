/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.servlets;

import com.badiyan.torque.SoapStatement;
import com.badiyan.uk.beans.AddressBean;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.beans.CompanyBean;
import com.badiyan.uk.beans.PersonBean;
import com.badiyan.uk.beans.PersonTitleBean;
import com.badiyan.uk.beans.RoleBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.PDF.EndOfDayLogBuilder;
import com.badiyan.uk.online.beans.AppointmentBean;
import com.badiyan.uk.online.beans.CheckoutCodeBean;
import com.badiyan.uk.online.beans.ClientReviewReason;
import com.badiyan.uk.online.beans.HerbDosage;
import com.badiyan.uk.online.beans.HerbDosageItemMapping;
import com.badiyan.uk.online.beans.HerbDosageUseDirections;
import com.badiyan.uk.online.beans.InventoryDepartment;
import com.badiyan.uk.online.beans.PracticeAreaBean;
import com.badiyan.uk.online.beans.ReviewReason;
import com.badiyan.uk.online.beans.SOAPNotesBean;
import com.badiyan.uk.online.beans.UKOnlineCompanyBean;
import com.badiyan.uk.online.beans.UKOnlineCourseReportLister;
import com.badiyan.uk.online.beans.UKOnlineLoginBean;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import com.badiyan.uk.online.beans.UKOnlineRoleBean;
import com.badiyan.uk.online.util.SessionCounter;
import com.valonyx.beans.ShoppingCartBean;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.torque.TorqueException;
import org.json.simple.JSONObject;


//import com.google.common.collect.Lists;


/**
 *
 * @author
 * marlo
 */
public class PractitionerServlet extends HttpServlet {
	

	private SimpleDateFormat time_date_format = new SimpleDateFormat("hh:mm a");
	private SimpleDateFormat short_date_format = new SimpleDateFormat("M/d");
	
	private SimpleDateFormat longer_date_format = new SimpleDateFormat("EEEE MMM d");
	private SimpleDateFormat date_format = new SimpleDateFormat("MM/dd/yyyy");
	
	//private HashMap<String,Vector> session_map_hash = new HashMap<String,Vector>(11);
	private HashMap<String,UKOnlinePersonBean> selected_practitioner_hash = new HashMap<String,UKOnlinePersonBean>(11);
	private HashMap<String,Calendar> selected_date_hash = new HashMap<String,Calendar>(11);
	private HashMap<String,AppointmentBean> selected_appointment_hash = new HashMap<String,AppointmentBean>(11);
	
	private Vector<UKOnlinePersonBean> practitioners = null;
	
	private HashMap<String,HerbDosage> selected_herb_mix_hash = new HashMap<String,HerbDosage>(11);
	private HashMap<String,String> herb_mix_person_search_hash = new HashMap<String,String>(11);
	private HashMap<String,String> herb_mix_herb_search_hash = new HashMap<String,String>(11);


	protected void processRequest(HttpServletRequest _request, HttpServletResponse _response)
			throws ServletException, IOException {
		
		_response.setContentType("application/json;charset=UTF-8");
		_response.setHeader("Access-Control-Allow-Origin", "*");
		
		PrintWriter writer = _response.getWriter();
		
		try {
			
			String command = _request.getParameter("command");
			String parameter = _request.getParameter("parameter");
			String arg1 = _request.getParameter("arg1");
			String arg2 = _request.getParameter("arg2");
			String arg3 = _request.getParameter("arg3");
			String arg4 = _request.getParameter("arg4");
			String arg5 = _request.getParameter("arg5");
			String arg6 = _request.getParameter("arg6");
			String arg7 = _request.getParameter("arg7");
			String arg8 = _request.getParameter("arg8");
			//String session_str = session.getId();
			
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
			//HttpSession session = null;
			System.out.println("session >" + session.getId());
			
			/*
			if (!arg1.isEmpty()) {
				this.addSessionToHash(arg1, session.getId());
			}
			*/
			
			Cookie[] cookieArr = _request.getCookies();
			if (cookieArr != null) {
				for (int i = 0; i < cookieArr.length; i++) {
					Cookie cook = (Cookie)cookieArr[i];
					System.out.println("cookie[" + i + "] >" + cook.getName() + " >" + cook.getValue());
				}
			} else {
				/*
				System.out.println("no cookies...");
				Cookie sessionCookie = new Cookie("sessionId", session.getId());
				// setting cookie to expiry in 60 mins
				sessionCookie.setMaxAge(60 * 60);
				_response.addCookie(sessionCookie);
				*/
			}
			
			
			
			if (command.equals("getPractitioners")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();

				Vector practice_areas = PracticeAreaBean.getPracticeAreas(selected_company);
				PracticeAreaBean adminPracticeArea = null;

				
				if (practitioners == null || practitioners.isEmpty()) {
					practitioners = new Vector();
					
					/*
					Iterator itr = practice_areas.iterator();
					while (itr.hasNext())
					{
						PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
						Iterator pa_prac_itr = practice_area.getPractitioners().iterator();
						while (pa_prac_itr.hasNext())
						{
							UKOnlinePersonBean practitioner = (UKOnlinePersonBean)pa_prac_itr.next();
							if (!practitioners.contains(practitioner) && practitioner.hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME)) {
								if (practitioner.showPractitioner()) {
									practitioners.addElement(practitioner);
								}
							}
						}
					}
					// add staff also
					Vector staff_vec = UKOnlinePersonBean.getCashiers(selected_company);
					itr = staff_vec.iterator();
					while (itr.hasNext()) {
						UKOnlinePersonBean staff = (UKOnlinePersonBean)itr.next();
						System.out.println("found staff >" + staff.getLabel());
						if (!practitioners.contains(staff)) {
							practitioners.addElement(staff);
						}
					}
					*/
					
					Iterator practitioner_itr = PracticeAreaBean.getAllPractitioners().iterator();
					for (int i = 2; practitioner_itr.hasNext(); i++) {
						UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
						practitioners.addElement(practitioner);
					}
				}				

				/*
				try {
					UKOnlinePersonBean kevin_obj = (UKOnlinePersonBean)practitioners.remove(0);
					practitioners.insertElementAt(kevin_obj, 4);
				} catch (Exception x) {
					x.printStackTrace();
				}
				*/
				
				UKOnlinePersonBean selected_practitioner = selected_practitioner_hash.get(arg1);
				if (selected_practitioner == null) {
					selected_practitioner = logged_in_person;
					selected_practitioner_hash.put(arg1, selected_practitioner);
				}
				
				// grab selected calendar from session
				//Calendar selected_calendar = (Calendar)session.getAttribute("selected_calendar");
				Calendar selected_calendar = selected_date_hash.get(arg1);
				if (selected_calendar == null) {
					selected_calendar = Calendar.getInstance();
					//session.setAttribute("selected_calendar", selected_calendar);
					selected_date_hash.put(arg1, selected_calendar);
				}
				
				Vector appointments = AppointmentBean.getAppointmentsForPractitioner(selected_practitioner, selected_calendar.getTime());
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"num\":" + practitioners.size() + ",");
				b.append("\"date\":\"" + JSONObject.escape(date_format.format(selected_calendar.getTime())) + "\",");
				b.append("\"practitioner\":[");
				Iterator itr = practitioners.iterator();
				while (itr.hasNext()) {
					try {
						if (needs_comma) {
							b.append(",");
						}
						UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
						System.out.println("adding pract >" + practitioner.getLabel());
						//if (practitioner.isActive()) {
							String ggg = this.toJSONPract(practitioner, practitioner.equals(selected_practitioner), true);
							if (!ggg.isEmpty()) {
								b.append(ggg);
								needs_comma = true;
							} else {
								needs_comma = false;
							}
							
						//}
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("],");
				
				needs_comma = false;
				b.append("\"appointment\":[");
				Iterator appt_itr = appointments.iterator();
				while (appt_itr.hasNext()) {
					try {
						if (needs_comma) {
							b.append(",");
						}
						AppointmentBean appt = (AppointmentBean)appt_itr.next();
						b.append(this.toJSONShort(appt));
						needs_comma = true;
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("selectPractitioner")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean selected_practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				//session.setAttribute("selected_practitioner", selected_practitioner);
				selected_practitioner_hash.put(arg1, selected_practitioner);
				
				// grab selected calendar from session
				//Calendar selected_calendar = (Calendar)session.getAttribute("selected_calendar");
				Calendar selected_calendar = selected_date_hash.get(arg1);
				if (selected_calendar == null) {
					selected_calendar = Calendar.getInstance();
					//session.setAttribute("selected_calendar", selected_calendar);
					selected_date_hash.put(arg1, selected_calendar);
				}
				
				Vector appointments = AppointmentBean.getAppointmentsForPractitioner(selected_practitioner, selected_calendar.getTime());
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"date\":\"" + JSONObject.escape(date_format.format(selected_calendar.getTime())) + "\",");
				
				
				b.append("\"practitioner\":[");
				Iterator itr = practitioners.iterator();
				while (itr.hasNext()) {
					try {
						if (needs_comma) {
							b.append(",");
						}
						UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
						String ggg = this.toJSONPract(practitioner, practitioner.equals(selected_practitioner), true);
						if (!ggg.isEmpty()) {
							b.append(ggg);
							needs_comma = true;
						} else {
							needs_comma = false;
						}
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("],");
				
				needs_comma = false;
				b.append("\"appointment\":[");
				Iterator appt_itr = appointments.iterator();
				while (appt_itr.hasNext()) {
					try {
						if (needs_comma) {
							b.append(",");
						}
						AppointmentBean appt = (AppointmentBean)appt_itr.next();
						b.append(this.toJSONShort(appt));
						needs_comma = true;
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
				
			} else if (command.equals("nextAppointmentDate") || command.equals("previousAppointmentDate")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				//UKOnlinePersonBean selected_practitioner = (UKOnlinePersonBean)session.getAttribute("selected_practitioner");
				UKOnlinePersonBean selected_practitioner = selected_practitioner_hash.get(arg1);
				if (selected_practitioner == null) {
					throw new IllegalValueException("You must select a practitioner.");
				}
				
				// grab selected calendar from session
				//Calendar selected_calendar = (Calendar)session.getAttribute("selected_calendar");
				Calendar selected_calendar = selected_date_hash.get(arg1);
				if (selected_calendar == null) {
					selected_calendar = Calendar.getInstance();
					session.setAttribute("selected_calendar", selected_calendar);
				}
				
				if (command.equals("nextAppointmentDate")) {
					selected_calendar.add(Calendar.DATE, 1);
				} else {
					selected_calendar.add(Calendar.DATE, -1);
				}
				
				selected_date_hash.put(arg1, selected_calendar);

				Vector appointments = AppointmentBean.getAppointmentsForPractitioner(selected_practitioner, selected_calendar.getTime());
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"date\":\"" + JSONObject.escape(date_format.format(selected_calendar.getTime())) + "\",");
				b.append("\"appointment\":[");
				Iterator appt_itr = appointments.iterator();
				while (appt_itr.hasNext()) {
					try {
						if (needs_comma) {
							b.append(",");
						}
						AppointmentBean appt = (AppointmentBean)appt_itr.next();
						b.append(this.toJSONShort(appt));
						needs_comma = true;
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
				
			} else if (command.equals("searchClients")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				Iterator itr = UKOnlinePersonBean.getPersonsByKeyword(selected_company, arg2, logged_in_person, 0).iterator();
				
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"date\":\"\",");
				b.append("\"person\":[");
				
				StringBuffer b2 = new StringBuffer();
				while (itr.hasNext()) {
					try {
						if (needs_comma) {
							b2.append(",");
						}
						UKOnlinePersonBean obj = (UKOnlinePersonBean)itr.next();
						b2.append(this.toJSONShort(obj));
						needs_comma = true;
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				
				herb_mix_person_search_hash.put(arg1, b2.toString());
				
				b.append(b2);
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("selectDate")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				//UKOnlinePersonBean selected_practitioner = (UKOnlinePersonBean)session.getAttribute("selected_practitioner");
				UKOnlinePersonBean selected_practitioner = selected_practitioner_hash.get(arg1);
				
				if (selected_practitioner == null) {
					throw new IllegalValueException("You must select a practitioner.");
				}
				
				Vector appointments = new Vector();
				try {
					Calendar selected_calendar = Calendar.getInstance();
					selected_calendar.setTime(CUBean.getDateFromUserString(arg2));
					//session.setAttribute("selected_calendar", selected_calendar);
					selected_date_hash.put(arg1, selected_calendar);
					appointments = AppointmentBean.getAppointmentsForPractitioner(selected_practitioner, selected_calendar.getTime());
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"date\":\"" + JSONObject.escape(arg2) + "\",");
				b.append("\"appointment\":[");
				Iterator appt_itr = appointments.iterator();
				while (appt_itr.hasNext()) {
					try {
						if (needs_comma) {
							b.append(",");
						}
						AppointmentBean appt = (AppointmentBean)appt_itr.next();
						b.append(this.toJSONShort(appt));
						needs_comma = true;
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("selectAppointment")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg2));
				selected_appointment_hash.put(arg1, selected_appointment);
				//selected_appointment_hash.put(session.getId(), selected_appointment);
				
				StringBuffer b = new StringBuffer();
				b.append(this.toJSON(selected_appointment, logged_in_person));
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("saveSoapS")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				// there should be a selected appointment at this point...
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(arg1);
				
				// 11/2/16 - changed to get the appointment from a parameter (arg3) passed by the client
				
				if (arg3 == null || arg3.isEmpty()) {
					throw new IllegalValueException("Unable to find selected appointment.  You may have an old version of the app.  Please try reloading.  Thanks!");
				}
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
				SOAPNotesBean currentNote = this.getCurrentNote(logged_in_person, selected_appointment);
				currentNote.setSNote(arg2.trim());
				currentNote.save();
				
				/*
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"S Note Saved\"}");
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				*/
				
				throw new IllegalValueException("S Note saved for >" + selected_appointment.getClient().getLabel());
				
			} else if (command.equals("saveSoapO")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(arg1); // removed 11/2/16
				if (arg3 == null || arg3.isEmpty()) {
					throw new IllegalValueException("Unable to find selected appointment.  You may have an old version of the app.  Please try reloading.  Thanks!");
				}
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
				SOAPNotesBean currentNote = this.getCurrentNote(logged_in_person, selected_appointment);
				
				String str = arg2.trim();
				
				if (str.indexOf("Time In:") > -1) {
					str = str.substring(0, str.indexOf("Time In:")).trim();
				}
				if (!currentNote.getTimeInStr().isEmpty()) {
					str += "\r\nTime In: " + currentNote.getTimeInStr();
				}
				if (!currentNote.getTimeOutStr().isEmpty()) {
					str += "\r\nTime Out: " + currentNote.getTimeOutStr();
				}
				
				if (str.indexOf("Christine In:") > -1) {
					str = str.substring(0, str.indexOf("Christine In:")).trim();
				}
				if (!currentNote.getTimeInAltStr().isEmpty()) {
					str += "\r\nChristine In: " + currentNote.getTimeInAltStr();
				}
				if (!currentNote.getTimeOutAltStr().isEmpty()) {
					str += "\r\nChristine Out: " + currentNote.getTimeOutAltStr();
				}
				
				System.out.println("str >" + str);
				
				if ( (arg4 == null) || arg4.isEmpty()) {
					currentNote.setONote(str);
				} else {
					currentNote.setONote(str + "~|~" + arg4);
				}
				
				currentNote.save();
				
				/*
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"O Note Saved\"}");
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				*/
				
				throw new IllegalValueException("O Note saved for >" + selected_appointment.getClient().getLabel());
				
			} else if (command.equals("saveSoapA")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(arg1); // removed 11/2/16
				if (arg3 == null || arg3.isEmpty()) {
					throw new IllegalValueException("Unable to find selected appointment.  You may have an old version of the app.  Please try reloading.  Thanks!");
				}
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
				SOAPNotesBean currentNote = this.getCurrentNote(logged_in_person, selected_appointment);
				//currentNote.setANote(arg2.trim());
				
				String str = arg2.trim();
				
				if ( (arg4 == null) || arg4.isEmpty()) {
					currentNote.setANote(str);
				} else {
					currentNote.setANote(str + "~|~" + arg4);
				}
				
				currentNote.save();
				
				/*
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"A Note Saved\"}");
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				*/
				
				throw new IllegalValueException("A Note saved for >" + selected_appointment.getClient().getLabel());
				
			} else if (command.equals("saveSoapP")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(arg1); // removed 11/2/16
				if (arg3 == null || arg3.isEmpty()) {
					throw new IllegalValueException("Unable to find selected appointment.  You may have an old version of the app.  Please try reloading.  Thanks!");
				}
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
				SOAPNotesBean currentNote = this.getCurrentNote(logged_in_person, selected_appointment);
				currentNote.setPNote(arg2.trim());
				currentNote.save();
				
				System.out.println("selected_appointment >" + selected_appointment.getId());
				System.out.println("currentNote >" + currentNote.getId());
				
				/*
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"P Note Saved\"}");
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				*/
				
				throw new IllegalValueException("P Note saved for >" + selected_appointment.getClient().getLabel());
				
			} else if (command.equals("saveTimeIn")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(arg1); // removed 11/2/16
				if (arg3 == null || arg3.isEmpty()) {
					throw new IllegalValueException("Unable to find selected appointment.  You may have an old version of the app.  Please try reloading.  Thanks!");
				}
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
				SOAPNotesBean currentNote = this.getCurrentNote(logged_in_person, selected_appointment);
				currentNote.setTimeInStr(arg2);
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"Time In Saved\"}");
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
			} else if (command.equals("saveTimeOut")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(arg1); // removed 11/2/16
				if (arg3 == null || arg3.isEmpty()) {
					throw new IllegalValueException("Unable to find selected appointment.  You may have an old version of the app.  Please try reloading.  Thanks!");
				}
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
				SOAPNotesBean currentNote = this.getCurrentNote(logged_in_person, selected_appointment);
				currentNote.setTimeOutStr(arg2);
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"Time Out Saved\"}");
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("saveTimeInAlt")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(arg1); // removed 11/2/16
				if (arg3 == null || arg3.isEmpty()) {
					throw new IllegalValueException("Unable to find selected appointment.  You may have an old version of the app.  Please try reloading.  Thanks!");
				}
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
				SOAPNotesBean currentNote = this.getCurrentNote(logged_in_person, selected_appointment);
				currentNote.setTimeInAltStr(arg2);
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"Alt Time In Saved\"}");
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
			} else if (command.equals("saveTimeOutAlt")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(arg1); // removed 11/2/16
				if (arg3 == null || arg3.isEmpty()) {
					throw new IllegalValueException("Unable to find selected appointment.  You may have an old version of the app.  Please try reloading.  Thanks!");
				}
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
				SOAPNotesBean currentNote = this.getCurrentNote(logged_in_person, selected_appointment);
				currentNote.setTimeOutAltStr(arg2);
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"Alt Time Out Saved\"}");
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getRecentPurchases")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg2));
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"selectedAppointmentId\":\"" + selected_appointment.getId() + "\",");
				b.append("\"recentPurchase\":[");
				Iterator itr = selected_appointment.getClient().getRecentMostCommonPurchases().iterator();
				while (itr.hasNext()) {
					try {
						com.badiyan.torque.CheckoutCode code = (com.badiyan.torque.CheckoutCode)itr.next();
						if (code.getIsActive() == (short)1 && code.getCompanyId() == selected_company.getId()) {

							if (needs_comma) {
								b.append(",");
							}
							b.append(this.toJSON(code));
							needs_comma = true;
						}
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("],");
				b.append("\"shoppingCart\":[");
				//Iterator itr = selected_appointment.getClient().getRecentMostCommonPurchases().iterator();
				needs_comma = false;
				UKOnlinePersonBean client = selected_appointment.getClient();
				ShoppingCartBean cart = ShoppingCartBean.getCart(client);
				itr = cart.getCheckoutOrderlines();
				while (itr.hasNext()) {
					try {
						if (needs_comma) {
							b.append(",");
						}
						com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
						b.append(this.toJSON(orderline));
						needs_comma = true;
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("addHerbMixtureToPreCheckout")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				HerbDosage adminHerbDosage = (HerbDosage)selected_herb_mix_hash.get(arg1);
				if (adminHerbDosage == null) {
					throw new IllegalValueException("No herb mixture selected");
				}
				

				ShoppingCartBean cart = ShoppingCartBean.getCart(adminHerbDosage.getClient());
				
				// 50ml - 2058
				// 100ml - 1412
				// 200ml - 1411
				
				int code_id = 2058;
				
				//BigDecimal fifty = new BigDecimal(50);
				BigDecimal one_hundred = new BigDecimal(100);
				BigDecimal two_hundred = new BigDecimal(200);
				
				if (adminHerbDosage.getHerbMLUsedTotal().compareTo(two_hundred) > -1) { // greater than (or equal to) 200
					code_id = 1411;
				} else if (adminHerbDosage.getHerbMLUsedTotal().compareTo(one_hundred) > -1) { // greater than (or equal to) 100
					code_id = 1412;
				}

				
				CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(code_id);
				System.out.println("code found >" + code.getLabel());

				//UKOnlinePersonBean selected_practitioner = selected_appointment.getPractitioner();
				UKOnlinePersonBean selected_practitioner = adminHerbDosage.getMixer();
				try {
					cart.remove(code);
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				cart.add(code, selected_practitioner, adminHerbDosage.getTotalRetail40MLTotal());
				cart.printCart();
					
				
				//writer.println("<status></status>");
				
				/*
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"selectedAppointmentId\":\"" + selected_appointment.getId() + "\",");
				b.append("\"shoppingCart\":[");
				//Iterator itr = selected_appointment.getClient().getRecentMostCommonPurchases().iterator();
				Iterator itr = cart.getCheckoutOrderlines();
				while (itr.hasNext()) {
					try {
						if (needs_comma) {
							b.append(",");
						}
						com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
						b.append(this.toJSON(orderline));
						needs_comma = true;
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				*/
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"report\",\"heading\":\"Herb Mixture Report\",\"text\":\"Mixture Added to Pre-Checkout\"}]}");
				writer.println(b.toString());
				
				
			} else if (command.equals("selectInventory")) {
				
				// does the session client have a cart already?
				
				//<jsp:useBean id="appointment" class="com.badiyan.uk.online.beans.AppointmentBean" scope="session" />
				
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				//AppointmentBean appointment = (AppointmentBean)session.getAttribute("appointment");
				//System.out.println("appointment found in session >" + appointment);
				
				UKOnlinePersonBean client = selected_appointment.getClient();
				ShoppingCartBean cart = ShoppingCartBean.getCart(client);

				CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg2));
				System.out.println("code found >" + code.getLabel());

				//UKOnlinePersonBean selected_practitioner = (UKOnlinePersonBean)session.getAttribute("selected_practitioner");
				UKOnlinePersonBean selected_practitioner = selected_appointment.getPractitioner();
				cart.add(code, selected_practitioner);
				cart.printCart();
					
				//writer.println("<status></status>");
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"selectedAppointmentId\":\"" + selected_appointment.getId() + "\",");
				b.append("\"shoppingCart\":[");
				//Iterator itr = selected_appointment.getClient().getRecentMostCommonPurchases().iterator();
				Iterator itr = cart.getCheckoutOrderlines();
				while (itr.hasNext()) {
					try {
						if (needs_comma) {
							b.append(",");
						}
						com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
						b.append(this.toJSON(orderline));
						needs_comma = true;
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("selectShoppingCart")) {
				
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				UKOnlinePersonBean client = selected_appointment.getClient();
				ShoppingCartBean cart = ShoppingCartBean.getCart(client);
				CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg2));
				cart.remove(code);
					
				
				//writer.println("<status></status>");
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"selectedAppointmentId\":\"" + selected_appointment.getId() + "\",");
				b.append("\"shoppingCart\":[");
				//Iterator itr = selected_appointment.getClient().getRecentMostCommonPurchases().iterator();
				Iterator itr = cart.getCheckoutOrderlines();
				while (itr.hasNext()) {
					try {
						if (needs_comma) {
							b.append(",");
						}
						com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
						b.append(this.toJSON(orderline));
						needs_comma = true;
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("updateShoppingCart")) {
				
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg3));
				
				UKOnlinePersonBean client = selected_appointment.getClient();
				ShoppingCartBean cart = ShoppingCartBean.getCart(client);
				CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg2));
				
				cart.updateQty(code, new BigDecimal(arg4));
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\"}]}");
				writer.println(b.toString());
				
			} else if (command.equals("searchCheckoutCodes")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				String item_search_str = arg2;
				//String item_search_str = parameter.substring(0, parameter.indexOf('|'));
				//String vendor_search_str = parameter.substring(parameter.indexOf('|') + 1);

				session.setAttribute("invSearchStr", item_search_str);
				//session.setAttribute("vendorSearchStr", vendor_search_str);

				//InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany); // not sure why this was here...
				
				StringBuffer buf = new StringBuffer();

				/*
				Iterator code_itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(selected_company, item_search_str, "").iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					if (code.isSynced() || !adminCompany.getQuickBooksSettings().isQuickBooksFSEnabled())
						buf.append(InventoryServlet.getXML(code));
				}
				*/

				//writer.println("<codes>" + buf.toString() + "</codes>");
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"selectedAppointmentId\":\"" + arg3 + "\",");
				b.append("\"searchResult\":[");
				Iterator itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(selected_company, item_search_str, "").iterator();
				while (itr.hasNext()) {
					try {
						
						CheckoutCodeBean code = (CheckoutCodeBean)itr.next();
						//if (code.isSynced() || !selected_company.getQuickBooksSettings().isQuickBooksFSEnabled()) {
						if (code.isSynced()) {
							if (needs_comma) {
								b.append(",");
							}
							//buf.append(InventoryServlet.getXML(code));
							b.append(this.toJSON(code));
							needs_comma = true;
						}
						
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("sendMessage")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				UKOnlinePersonBean send_to_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				
				String message_json = PractitionerServlet.messageToJSON(logged_in_person, send_to_person, arg3, arg4, arg5.equals("true"));
				System.out.println("message_json >" + message_json);
				System.out.println("send_to_person.getValue() >" + send_to_person.getValue());
				
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				String html_message = this.getMessageEmailString(selected_company, send_to_person, logged_in_person, arg3, arg4);
				CUBean.sendEmailWithMeeting(send_to_person.getEmail1String(), logged_in_person.getEmail1String(), arg3, null, html_message);
				CUBean.sendEmailWithMeeting("marlo@badiyan.com", logged_in_person.getEmail1String(), arg3, null, html_message);
				
				if (send_to_person.getEmail1String().equals("wstueve@sanowc.com")) {
					CUBean.sendEmail("9522706486@tmomail.net", logged_in_person.getEmail1String(), arg3, arg4);
					CUBean.sendEmail("6122703795@txt.att.net", logged_in_person.getEmail1String(), arg3, arg4);
					
				}
				
				try {
					PractitionerAppSocketServlet.sendMessage(send_to_person.getValue(), message_json);
				} catch (IllegalValueException x) {
					x.printStackTrace();
					throw new IllegalValueException("Unable to send message.  " + send_to_person.getFirstNameString() + " is not connected.");
				}
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"Message Sent\"}");
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getSelectedUrineTest")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				HerbDosage adminHerbDosage = (HerbDosage)selected_herb_mix_hash.get(arg1);
				if (adminHerbDosage == null) {
					adminHerbDosage = new HerbDosage();
					adminHerbDosage.setMixer(logged_in_person);
					adminHerbDosage.setCompany(selected_company);
					//session.setAttribute("adminHerbDosage", adminHerbDosage);
					selected_herb_mix_hash.put(arg1, adminHerbDosage);
				}
				
				if (!adminHerbDosage.hasClient()) {
					// try to get client from selected appointment
					AppointmentBean selected_appointment = selected_appointment_hash.get(arg1);
					//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
					if (selected_appointment != null) {
						adminHerbDosage.setClient(selected_appointment.getClient());
					}
				}
				
				StringBuffer b = new StringBuffer();
				b.append(this.toJSON(adminHerbDosage, arg1));
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getSelectedHerbMixture")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				HerbDosage adminHerbDosage = (HerbDosage)selected_herb_mix_hash.get(arg1);
				if (adminHerbDosage == null) {
					adminHerbDosage = new HerbDosage();
					adminHerbDosage.setMixer(logged_in_person);
					adminHerbDosage.setCompany(selected_company);
					//session.setAttribute("adminHerbDosage", adminHerbDosage);
					selected_herb_mix_hash.put(arg1, adminHerbDosage);
				}
				
				if (!adminHerbDosage.hasClient()) {
					// try to get client from selected appointment
					AppointmentBean selected_appointment = selected_appointment_hash.get(arg1);
					//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
					if (selected_appointment != null) {
						adminHerbDosage.setClient(selected_appointment.getClient());
					}
				}
				
				StringBuffer b = new StringBuffer();
				b.append(this.toJSON(adminHerbDosage, arg1));
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("selectHerbMixtureClient")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				UKOnlinePersonBean herb_client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				
				
				HerbDosage adminHerbDosage = adminHerbDosage = new HerbDosage();
				selected_herb_mix_hash.put(arg1, adminHerbDosage);
				adminHerbDosage.setMixer(logged_in_person);
				adminHerbDosage.setClient(herb_client);
				adminHerbDosage.setCompany(selected_company);
				
				StringBuffer b = new StringBuffer();
				b.append(this.toJSON(adminHerbDosage, arg1));
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("saveSelectedHerbMixture")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				HerbDosage adminHerbDosage = (HerbDosage)selected_herb_mix_hash.get(arg1);
				if (adminHerbDosage == null) {
					adminHerbDosage = new HerbDosage();
					adminHerbDosage.setCompany(selected_company);
					//session.setAttribute("adminHerbDosage", adminHerbDosage);
					selected_herb_mix_hash.put(arg1, adminHerbDosage);
				}
				
				if (!adminHerbDosage.hasClient()) {
					// try to get client from selected appointment
					AppointmentBean selected_appointment = selected_appointment_hash.get(arg1);
					//AppointmentBean selected_appointment = selected_appointment_hash.get(session.getId());
					if (selected_appointment != null) {
						adminHerbDosage.setClient(selected_appointment.getClient());
					}
				}
				
				try {
					Date mix_date = CUBean.getDateFromUserString(arg2);
					adminHerbDosage.setMixDate(mix_date);
				} catch (Exception x) {
					
				}
				
				if (arg3 != null) {
					adminHerbDosage.setDescription(arg3);
				}
				
				if (arg4 != null) {
					adminHerbDosage.setNotes(arg4);
				}
				
				if ((arg5 != null) && arg5.equals("1")) {
					
					if (arg2 == null || arg2.isEmpty()) {
						throw new IllegalValueException("Please provide a Mix Date.");
					}
					if (arg3 == null || arg3.isEmpty()) {
						throw new IllegalValueException("Please provide a Mixture Description.");
					}
					
					adminHerbDosage.save();
					
					StringBuffer b = new StringBuffer();
					b.append("{\"message\":[{\"type\":\"success\",\"text\":\"Mixture Saved\"}]}");
					writer.println(b.toString());
				} else {
					StringBuffer b = new StringBuffer();
					b.append("{\"message\":[{\"type\":\"info\",\"text\":\"Mixture Updated\"}]}");
					writer.println(b.toString());
				}
				
			} else if (command.equals("addDirections")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				short startDay = Short.parseShort(arg2);
				short endDay = Short.parseShort(arg3);
				short ml = Short.parseShort(arg4);
				String measure = arg5;
				String mixIn = arg6;
				short times = Short.parseShort(arg7);
				String period = arg8;
				
				//HerbDosage adminHerbDosage = (HerbDosage)session.getAttribute("adminHerbDosage");
				HerbDosage adminHerbDosage = (HerbDosage)selected_herb_mix_hash.get(arg1);
				if (adminHerbDosage == null) {
					adminHerbDosage = new HerbDosage();
					adminHerbDosage.setCompany(selected_company);
					//session.setAttribute("adminHerbDosage", adminHerbDosage);
					selected_herb_mix_hash.put(arg1, adminHerbDosage);
				}
				Vector directions = adminHerbDosage.getUseDirections();
				HerbDosageUseDirections obj = new HerbDosageUseDirections();
				obj.setStartDay(startDay);
				obj.setEndDay(endDay);
				obj.setML(ml);
				obj.setMixIn(mixIn);
				obj.setNumTimes(times);
				obj.setPeriod(period);
				obj.setMeasure(measure);
				directions.addElement(obj);
				adminHerbDosage.setUseDirections(directions);
				
				StringBuffer b = new StringBuffer();
				b.append(this.toJSON(adminHerbDosage, arg1));
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("removeDirections")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				int removeIndex = Integer.parseInt(arg2);
				HerbDosage adminHerbDosage = (HerbDosage)selected_herb_mix_hash.get(arg1);
				Vector directions = adminHerbDosage.getUseDirections();
				directions.removeElementAt(removeIndex);
				adminHerbDosage.setUseDirections(directions);
				
				StringBuffer b = new StringBuffer();
				b.append(this.toJSON(adminHerbDosage, arg1));
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getCheckoutCodesByDescHerbs")) {
				
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				String item_search_str = arg2;

				session.setAttribute("invSearchStr", item_search_str);
				
				
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				StringBuffer b2 = new StringBuffer();
				b.append("{\"dummy\":\"\",");
				b.append("\"herbSearchResult\":[");
				InventoryDepartment herbDepartment = InventoryDepartment.getInventoryDepartment(selected_company, "Herbs");
				Iterator code_itr = CheckoutCodeBean.getCheckoutCodesByDescAndDepartment(selected_company, item_search_str, herbDepartment).iterator();
				while (code_itr.hasNext()) {
					try {
						
						CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
						
						if (needs_comma) {
							b2.append(",");
						}
						b2.append(this.toJSON(code));
						needs_comma = true;
						
					} catch (java.util.ConcurrentModificationException y) {
						throw y;
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				b.append(b2);
				herb_mix_herb_search_hash.put(arg1, b2.toString());
				b.append("]}");
				
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("addHerb")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				CheckoutCodeBean herbToAdd = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg2));
				short ml = Short.parseShort(arg3);
				
				HerbDosage adminHerbDosage = (HerbDosage)selected_herb_mix_hash.get(arg1);
				if (adminHerbDosage == null) {
					adminHerbDosage = new HerbDosage();
					adminHerbDosage.setCompany(selected_company);
					//session.setAttribute("adminHerbDosage", adminHerbDosage);
					selected_herb_mix_hash.put(arg1, adminHerbDosage);
				}
				
				Vector mappings = adminHerbDosage.getItems();
				HerbDosageItemMapping mapping = new HerbDosageItemMapping();
				mapping.setItem(herbToAdd);
				mapping.setML(ml);
				mappings.addElement(mapping);
				adminHerbDosage.setItems(mappings);
				
				StringBuffer b = new StringBuffer();
				b.append(this.toJSON(adminHerbDosage, arg1));
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("removeHerb")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				int removeIndex = Integer.parseInt(arg2);
				HerbDosage adminHerbDosage = (HerbDosage)selected_herb_mix_hash.get(arg1);
				Vector mappings = adminHerbDosage.getItems();
				mappings.removeElementAt(removeIndex);
				adminHerbDosage.setItems(mappings);
				
				StringBuffer b = new StringBuffer();
				b.append(this.toJSON(adminHerbDosage, arg1));
				System.out.println("return >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("herbReportExcel")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				HerbDosage adminHerbDosage = (HerbDosage)selected_herb_mix_hash.get(arg1);
				if (adminHerbDosage == null) {
					throw new IllegalValueException("No herb mixture selected");
				}
				
				String ss_str = doHerbSpreadsheet(adminHerbDosage);
				//writer.println("<herbReport>" + ss_str + "</herbReport>");
				
				String excel_url = CUBean.getProperty("cu.webDomain") + "/resources/" + ss_str;
				String report_url = "<a href=\"" + excel_url + "\">" + ss_str + "</a>";

				if (arg2 != null && arg2.equals("true")) {
					// email report
					String html_message = this.getMessageEmailString(selected_company, logged_in_person, logged_in_person, "Herb Mixture Report - " + adminHerbDosage.getClient().getLabel(), report_url);
					CUBean.sendEmailWithMeeting(logged_in_person.getEmail1String(), logged_in_person.getEmail1String(), "Herb Mixture Report - " + adminHerbDosage.getClient().getLabel(), null, html_message);
					CUBean.sendEmailWithMeeting("marlo@badiyan.com", logged_in_person.getEmail1String(), "Herb Mixture Report - " + adminHerbDosage.getClient().getLabel(), null, html_message);
					
					StringBuffer b = new StringBuffer();
					b.append("{\"message\":[{\"type\":\"report\",\"heading\":\"Herb Mixture Report\",\"text\":\"Report sent to " + logged_in_person.getEmail1String() + "\"}]}");
					writer.println(b.toString());
				} else {
					StringBuffer b = new StringBuffer();
					b.append("{\"message\":[{\"type\":\"report\",\"heading\":\"Herb Mixture Report\",\"text\":\"" + JSONObject.escape(report_url) + "\"}]}");
					writer.println(b.toString());
				}
				
			} else if (command.equals("auth")) {
				
				String email = arg2;
				String password = arg3;
				
				UKOnlinePersonBean person = null;
				Object loginBean = session.getAttribute("loginBean");
				//if (loginBean == null) {
					
					// no loginBean in session.  credentials required
					
					if (email == null || email.isEmpty()) {
						throw new IllegalValueException("Please provide an email address.");
					}
					if (password == null || password.isEmpty()) {
						throw new IllegalValueException("Please provide a password.");
					}
					
					loginBean = new UKOnlineLoginBean();
					session.setAttribute("loginBean", loginBean);
					
					((UKOnlineLoginBean)loginBean).setUsername(email);
					((UKOnlineLoginBean)loginBean).setPassword(password);

					person = (UKOnlinePersonBean)((UKOnlineLoginBean)loginBean).getPerson();

					SessionCounter.add(session, person, _request);
					
				//} else {
				//	person = (UKOnlinePersonBean)((UKOnlineLoginBean)loginBean).getPerson();
				//}
				
				if (person == null) {
					throw new IllegalValueException("Auth failed.");
				}
				
				//CompanyBean defaultCompany = CompanyBean.maintainCompany("[DEFAULT]");
				
				
				StringBuffer b = new StringBuffer();
				b.append("{\"auth\":[");
				b.append(this.toJSON(person, false));
				b.append("]}");
				
				System.out.println(b.toString());
				
				writer.println(b.toString());
				
				/*
				Vector sessions_vec = session_map_hash.get(person.getValue());
				if (sessions_vec == null) {
					sessions_vec = new Vector();
				}
				sessions_vec.addElement(session.getId());
				//session_map_hash.put(person.getValue(), session.getId());
				session_map_hash.put(person.getValue(), sessions_vec);
				System.out.println("putting >" + person.getValue() + " >" + session.getId());
				*/
				
				//this.addSessionToHash(person.getValue(), session.getId());
				
				session.setAttribute("logged_in_person", person);
				
			} else if (command.equals("authSession")) {
				
				StringBuffer b = new StringBuffer();
				
				UKOnlinePersonBean person = null;
				Object loginBean = session.getAttribute("loginBean");
				if (loginBean == null) {
					
					// no loginBean in session.  credentials required
					
					b.append("{\"authSession\":[");
					b.append("]}");
					
				} else {
					person = (UKOnlinePersonBean)((UKOnlineLoginBean)loginBean).getPerson();
					b.append("{\"authSession\":[");
					b.append(this.toJSON(person, false));
					b.append("]}");
					

					/*
					Vector sessions_vec = session_map_hash.get(person.getValue());
					if (sessions_vec == null) {
						sessions_vec = new Vector();
					}
					sessions_vec.addElement(session.getId());
					//session_map_hash.put(person.getValue(), session.getId());
					session_map_hash.put(person.getValue(), sessions_vec);
					
					System.out.println("putting >" + person.getValue() + " >" + session.getId());
					*/
					
					//this.addSessionToHash(person.getValue(), session.getId());
				}
				
				System.out.println(b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("deAuth")) {
				
				session.invalidate();
				
				StringBuffer b = new StringBuffer();
				b.append("{\"deAuth\":[");
				b.append("]}");
				
				System.out.println(b.toString());
				writer.println(b.toString());
				
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
				b.append(this.toJSON(new_user, false));
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
				
			} else if (command.equals("submitFollowup")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(arg2));
				
				//AppointmentBean appointment = (AppointmentBean)session.getAttribute("appointment");
				//System.out.println("appointment found in session >" + appointment);
				
				UKOnlinePersonBean client = selected_appointment.getClient();
				

				//UKOnlinePersonBean selected_practitioner = (UKOnlinePersonBean)session.getAttribute("selected_practitioner");
				UKOnlinePersonBean selected_practitioner = selected_appointment.getPractitioner();
				
				ReviewReason reason_obj = ReviewReason.maintainReason(selected_company, arg3);
				
				Calendar alert_date = Calendar.getInstance();
				alert_date.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(arg4));
				
				
				ClientReviewReason client_review_reason = new ClientReviewReason();
				client_review_reason.setPerson(client);
				client_review_reason.setReviewDate(alert_date.getTime());
				client_review_reason.setReviewReason(reason_obj);
				client_review_reason.setNote(arg5);
				//client_review_reason.setPractitioner(selected_practitioner);
				Vector practitioners = new Vector();
				practitioners.addElement(selected_practitioner);
				client_review_reason.setPractitioners(practitioners);
				client_review_reason.save();
				
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"report\",\"heading\":\"Follow Up Created\",\"text\":\"Follow Up created for " + client.getLabel() + "\"}]}");
				writer.println(b.toString());
				
			} else if (command.equals("copyAppointments")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				
				// grab selected calendar from session
				//Calendar selected_calendar = (Calendar)session.getAttribute("selected_calendar");
				Calendar selected_calendar = selected_date_hash.get(arg1);
				if (selected_calendar == null) {
					selected_calendar = Calendar.getInstance();
					//session.setAttribute("selected_calendar", selected_calendar);
					selected_date_hash.put(arg1, selected_calendar);
				}
				
				Date source_date = CUBean.getDateFromUserString(arg2);
				Date destination_date = CUBean.getDateFromUserString(arg3);
				
				System.out.println("selected_company >" + selected_company.getLabel());
				
				Iterator source_appointments = AppointmentBean.getAppointmentsForDate(selected_company, source_date).iterator();
				while (source_appointments.hasNext()) {
					AppointmentBean source_appointment = (AppointmentBean)source_appointments.next();
					System.out.println("source appt found >" + source_appointment.getLabel());
					
					Calendar source_cal = Calendar.getInstance();
					source_cal.setTime(source_appointment.getAppointmentDate());
					
					
					Calendar destination_cal = Calendar.getInstance();
					destination_cal.setTime(destination_date);
					destination_cal.set(Calendar.HOUR_OF_DAY, source_cal.get(Calendar.HOUR_OF_DAY));
					destination_cal.set(Calendar.MINUTE, source_cal.get(Calendar.MINUTE));
					destination_cal.set(Calendar.SECOND, source_cal.get(Calendar.SECOND));
					
					
					if (source_cal.get(Calendar.DAY_OF_WEEK) != destination_cal.get(Calendar.DAY_OF_WEEK)) {
						throw new IllegalValueException("Day of week must match.");
					}
					
					//source_appointment.getAppointmentDate()
					
					try {
						AppointmentBean destination_appointment = new AppointmentBean();
						destination_appointment.setCompany(selected_company);
						if (source_appointment.isClientAppointment()) {
							destination_appointment.setClient(source_appointment.getClient());
						}
						destination_appointment.setPractitioner(source_appointment.getPractitioner());
						destination_appointment.setType(source_appointment.getType());
						destination_appointment.setDuration((short)source_appointment.getDuration());
						destination_appointment.setState(source_appointment.getState());
						destination_appointment.setAppointmentDate(destination_cal.getTime());
						destination_appointment.save();
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				
				//ScheduleServlet.needs_update_hash.clear();
				
				
				
				
				String key1 = selected_company.getId() + "|" + CUBean.getUserDateString(destination_date);
				String key2 = selected_company.getId() + "|" + arg1 + "|" + CUBean.getUserDateString(destination_date);

				Calendar start_of_week = Calendar.getInstance();
				start_of_week.setTime(destination_date);
				Calendar end_of_week = Calendar.getInstance();
				end_of_week.setTime(destination_date);

				start_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				end_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

				String key3 = selected_company.getId() + "|" + arg1 + "|" + CUBean.getUserDateString(start_of_week.getTime()) + "|" + CUBean.getUserDateString(end_of_week.getTime());

				AppointmentBean.appointment_hash.remove(key1);
				AppointmentBean.appointment_hash.remove(key2);
				AppointmentBean.appointment_hash.remove(key3);

				/*
				StringBuffer returnBuf;
				Calendar display_date = (Calendar)session.getAttribute("display_date");
				if (command.equals("delete"))
					returnBuf = this.assembleScheduleXML(selected_company, adminPracticeArea, display_date, session_str, true);
				else
					returnBuf = this.assemblePractitionerScheduleXML(selected_company, adminPractitioner, display_date, session_str, true);

				*/
				
				//String date_str = ScheduleServlet.date_format.format(display_date.getTime());

				//String key = session_str + "" + date_str;
				//ScheduleServlet.last_update_hash.put(key, new Date());

				//writer.println("<status>" + returnBuf.toString() + "</status>");

				Date update_timestamp = new Date();
				AppointmentBean.appointment_update_hash.put(key1, update_timestamp);
				AppointmentBean.appointment_update_hash.put(key2, update_timestamp);
				AppointmentBean.appointment_update_hash.put(key3, update_timestamp);
				
				
				
			
				
				
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"success\"," +
						"\"heading\":\"Oh snap!\"," +
						"\"text\":\"Love you babe! >\"" +
						"}");
				b.append("]}");
				writer.println(b.toString());
				
			} else if (command.equals("getSOAPTree")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				UKOnlineCompanyBean selected_company = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				boolean needs_comma = false;
				StringBuilder b = new StringBuilder();
				b.append("{\"tree\":[");
				Iterator itr = PracticeAreaBean.getPracticeAreas(selected_company).iterator();
				while (itr.hasNext()) {
					PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
					
					if (needs_comma) { b.append(','); }
					
					b.append("{\"practiceArea\":\"" + JSONObject.escape(practice_area.getLabel()) + "\",");
					
					ArrayList<ArrayList> l = SOAPNotesBean.getStatements(selected_company, practice_area);
					ArrayList s_list = l.get(0);
					ArrayList o_list = l.get(1);
					ArrayList a_list = l.get(2);
					ArrayList p_list = l.get(3);
					
					boolean needs_comma_inner = false;
					Iterator itrx = s_list.iterator();
					b.append("\"s\":[");
					while (itrx.hasNext()) {
						if (needs_comma_inner) { b.append(','); }
						SoapStatement statement = (SoapStatement)itrx.next();
						b.append(this.toJSON(statement));
						needs_comma_inner = true;
					}
					b.append("],");
					needs_comma_inner = false;
					itrx = o_list.iterator();
					b.append("\"o\":[");
					while (itrx.hasNext()) {
						if (needs_comma_inner) { b.append(','); }
						SoapStatement statement = (SoapStatement)itrx.next();
						b.append(this.toJSON(statement));
						needs_comma_inner = true;
					}
					b.append("],");
					needs_comma_inner = false;
					itrx = a_list.iterator();
					b.append("\"a\":[");
					while (itrx.hasNext()) {
						if (needs_comma_inner) { b.append(','); }
						SoapStatement statement = (SoapStatement)itrx.next();
						b.append(this.toJSON(statement));
						needs_comma_inner = true;
					}
					b.append("],");
					needs_comma_inner = false;
					itrx = p_list.iterator();
					b.append("\"p\":[");
					while (itrx.hasNext()) {
						if (needs_comma_inner) { b.append(','); }
						SoapStatement statement = (SoapStatement)itrx.next();
						b.append(this.toJSON(statement));
						needs_comma_inner = true;
					}
					b.append("]");
					b.append("}");
					
					needs_comma = true;
					
				}
				b.append("]}");
				
				writer.println(b.toString());
				
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
					"\"text\":\"" + JSONObject.escape(x.getMessage()) + "\"" +
					"}");
			b.append("]}");
			
			System.out.println(b.toString());
			
			_response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			writer.println(b.toString());
		} finally {			
			writer.close();
		}
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
	
			
	private String toJSONPract(UKOnlinePersonBean _person, boolean _is_selected, boolean _hey) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		/*
		return "{ \"label\":\"" + JSONObject.escape(_person.getLabel()) + "\"," +
		" \"empId\":\"" + _person.getEmployeeNumberString() + "\"," +
		" \"active\":\"" + _person.isActive() + "\"," +
		" \"lastLogIn\":\"" + _person.getLastLogInDateString() + "\"," +
		" \"role\":\"" + JSONObject.escape(_person.getRolesString()) + "\"," +
		" \"location\":\"" + JSONObject.escape(_person.getDepartmentNameString()) + "\"," +
		" \"title\":\"" + JSONObject.escape(_person.getJobTitleString()) + "\"," +
		" \"id\":\"" + _person.getId() + "\"}";
		*/
		
		//String label = _person.getLabel();
		
		String room_label = _person.getFirstNameString();
		if (_person.isRoom()) {
			room_label = _person.getLastNameString();
		}
		System.out.println("ggg >" + room_label + "<");
		if (room_label.equals("Christine")) {
			room_label = "Christine - TX1";
		} else if (room_label.equals("TX 1")) {
			//room_label = "Lisa - TX3";
			return "";
		} else if (room_label.equals("Kevin")) {
			System.out.println("grabbing pod 1");
			room_label = "POD 1";
		} else if (room_label.equals("TX 4")) {
			room_label = "Bemer - TX4";
		} else if (room_label.equals("TX4")) {
			room_label = "Bemer - TX4";
		} else if (room_label.equals("Lisa")) {
			//room_label = "Lisa - TX2";
			return "";
		} else if (room_label.equals("Leah")) {
			room_label = "Leah - TX3";
		} else if (room_label.equals("Lesya")) {
			room_label = "Lesya - BW2";
		} else if (room_label.equals("Katie")) {
			room_label = "POD - BW1";
		} else if (room_label.equals("Counseling(C) - TX2")) {
			return "";
		} else if (room_label.equals("Counseling(M) - TX2")) {
			return "";
		} else if (room_label.equals("Neuro - TX4")) {
			room_label = "Angie NF - TX4";
		} else if (room_label.equals("Sauna 1")) {
			room_label = "POD 2";
		} else if (room_label.equals("Sauna 2")) {
			return "";
		} else if (room_label.equals("Bioscan 1")) {
			return "";
		}
		
		System.out.println("room_label >" + room_label);
		System.out.println("_person >" + _person);
		
		
		return "{ \"label\":\"" + JSONObject.escape(room_label) + "\"," +
		" \"isSelected\":" + _is_selected + "," +
		" \"isPractitioner\":" + _person.isPractitioner() + "," +
		" \"isStaff\":" + _person.isStaff() + "," +
		" \"isRoom\":" + _person.isRoom() + "," +
		" \"id\":\"" + _person.getId() + "\"}";
	}
	
			
	private String toJSON(UKOnlinePersonBean _person, boolean _is_selected) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		/*
		return "{ \"label\":\"" + JSONObject.escape(_person.getLabel()) + "\"," +
		" \"empId\":\"" + _person.getEmployeeNumberString() + "\"," +
		" \"active\":\"" + _person.isActive() + "\"," +
		" \"lastLogIn\":\"" + _person.getLastLogInDateString() + "\"," +
		" \"role\":\"" + JSONObject.escape(_person.getRolesString()) + "\"," +
		" \"location\":\"" + JSONObject.escape(_person.getDepartmentNameString()) + "\"," +
		" \"title\":\"" + JSONObject.escape(_person.getJobTitleString()) + "\"," +
		" \"id\":\"" + _person.getId() + "\"}";
		*/
		
		
		
		return "{ \"label\":\"" + JSONObject.escape(_person.getLabel()) + "\"," +
		" \"isSelected\":" + _is_selected + "," +
		" \"isPractitioner\":" + _person.isPractitioner() + "," +
		" \"isStaff\":" + _person.isStaff() + "," +
		" \"isRoom\":" + _person.isRoom() + "," +
		" \"id\":\"" + _person.getId() + "\"}";
	}
			
	private String toJSON(com.badiyan.torque.CheckoutCode _code) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		// <p><a href="javascript:addToCart('<%= code.getCheckoutCodeId() %>','<%= code.getDescription() %>');"><%= code.getDescription() %><span>Add To Cart <span class="arrow">&gt;</span></span></a></p>
		return "{ \"label\":\"" + JSONObject.escape(_code.getDescription()) + "\"," +
		" \"amount\":\"" + _code.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
		" \"active\":" + (_code.getIsActive() == (short)1) + "," +
		" \"id\":\"" + _code.getCheckoutCodeId() + "\"}";
	}
			
	private String toJSON(com.badiyan.torque.CheckoutOrderline _orderline) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		com.badiyan.torque.CheckoutCode code = _orderline.getCheckoutCode();
		
		// <p><a href="javascript:addToCart('<%= code.getCheckoutCodeId() %>','<%= code.getDescription() %>');"><%= code.getDescription() %><span>Add To Cart <span class="arrow">&gt;</span></span></a></p>
		/*
		return "{ \"label\":\"" + JSONObject.escape(code.getDescription()) + "\"," +
		" \"amount\":\"" + code.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
		" \"qty\":\"" + _orderline.getQuantity().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
		" \"active\":" + (code.getIsActive() == (short)1) + "," +
		" \"id\":\"" + code.getCheckoutCodeId() + "\"}";
		*/
		
		return "{ \"label\":\"" + JSONObject.escape(code.getDescription()) + "\"," +
		" \"amount\":\"" + code.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
		" \"qty\":\"" + this.getStringFromBigDecimal(_orderline.getQuantity()) + "\"," +
		" \"active\":" + (code.getIsActive() == (short)1) + "," +
		" \"id\":\"" + code.getCheckoutCodeId() + "\"}";
	}
			
	private String toJSON(CheckoutCodeBean _code) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		
		// <p><a href="javascript:addToCart('<%= code.getCheckoutCodeId() %>','<%= code.getDescription() %>');"><%= code.getDescription() %><span>Add To Cart <span class="arrow">&gt;</span></span></a></p>
		return "{ \"label\":\"" + JSONObject.escape(_code.getLabel()) + "\"," +
		" \"amount\":\"" + _code.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
		" \"active\":" + _code.isActive() + "," +
		" \"id\":\"" + _code.getId() + "\"}";
	}
	
	private String toJSONShort(AppointmentBean _appointment) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {

		//<p><a href="schedule-mobile-appointment.jsp?id=<%= appt.getId() %>"><%= time_date_format.format(appt_date) %> - <%= appt.getLabel() %><span> <span class="arrow">&gt;</span></span></a></p>
		
		Date appt_date = _appointment.getAppointmentDate();
		
		return "{ \"label\":\"" + JSONObject.escape(_appointment.getLabelLong()) + "\"," +
		" \"timeStr\":\"" + JSONObject.escape(time_date_format.format(appt_date)) + "\"," +
		" \"typeStr\":\"" + JSONObject.escape(_appointment.getType().getLabel()) + "\"," +
		" \"isClientAppointment\":" + _appointment.getType().isClientAppointmentType() + "," +
		" \"id\":\"" + _appointment.getId() + "\"}";
	}
	
	private String toJSON(AppointmentBean _appointment, UKOnlinePersonBean _logged_in_person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		
		Date appt_date = _appointment.getAppointmentDate();
		Calendar appt_end = Calendar.getInstance();
		appt_end.setTime(appt_date);
		appt_end.add(Calendar.MINUTE, _appointment.getDuration());
		
		String timeInStr = "";
		String timeOutStr = "";
		String timeInAltStr = "";
		String timeOutAltStr = "";
		
		String currentS = "";
		String currentO = "";
		String currentA = "";
		String currentP = "";
		
		String currentSStatementHTML = "";
		String currentOStatementHTML = "";
		String currentAStatementHTML = "";
		String currentPStatementHTML = "";
		
		String lastDate = "";
		
		String lastS = "";
		String lastO = "";
		String lastA = "";
		String lastP = "";
		
		String lastSStatementHTML = "";
		String lastOStatementHTML = "";
		String lastAStatementHTML = "";
		String lastPStatementHTML = "";
		
		String clientId = "";
		
		SOAPNotesBean currentNote = null;
		if (_appointment.hasClient()) {
			
			clientId = _appointment.getClient().getValue();

			try {
				//currentNote = SOAPNotesBean.getSOAPNotes(_appointment.getClient(), new Date());
				
				currentNote = this.getCurrentNote(_logged_in_person, _appointment);
				currentNote.save();
				
				/*
				currentS = currentNote.getSNoteString().replaceAll("\n", "<br />");
				currentO = currentNote.getONoteString().replaceAll("\n", "<br />");
				currentA = currentNote.getANoteString().replaceAll("\n", "<br />");
				currentP = currentNote.getPNoteString().replaceAll("\n", "<br />");
				*/
				
				currentS = currentNote.getSNoteString();
				currentO = currentNote.getONoteString();
				currentA = currentNote.getANoteString();
				currentP = currentNote.getPNoteString();
				
				currentSStatementHTML = currentNote.getSNoteStatementHTMLString();
				currentOStatementHTML = currentNote.getONoteStatementHTMLString();
				currentAStatementHTML = currentNote.getANoteStatementHTMLString();
				currentPStatementHTML = currentNote.getPNoteStatementHTMLString();
				
				timeInStr = currentNote.getTimeInStr();
				timeOutStr = currentNote.getTimeOutStr();
				
				timeInAltStr = currentNote.getTimeInAltStr();
				timeOutAltStr = currentNote.getTimeOutAltStr();
				
			} catch (Exception x) {
				System.out.println("error >" + x.getMessage());
				x.printStackTrace();
			}
			/*
			System.out.println("more stuff");

			if (soapNote.isNew() || (soapNote.getPerson() == null) || (appointment.getClient().getId() != soapNote.getPerson().getId())) {
				soapNote = new SOAPNotesBean();
				soapNote.setPerson(appointment.getClient());
				soapNote.setPracticeArea(appointment.getPracticeArea());
				session.setAttribute("soapNote", soapNote);
			}
			*/
			
			if (currentNote == null) {
				currentNote = new SOAPNotesBean();
			}

			Iterator oldNotes = SOAPNotesBean.getSOAPNotes(_appointment.getClient()).iterator();
			if (oldNotes.hasNext()) {
				SOAPNotesBean lastNote = (SOAPNotesBean)oldNotes.next();

				if (lastNote.getId() == currentNote.getId()) {
					if (oldNotes.hasNext()) {
						lastNote = (SOAPNotesBean)oldNotes.next();
					}
				}

				if (lastNote.getId() != currentNote.getId()) {
					
					lastDate = lastNote.getAnalysisDateString();
					
					lastS = lastNote.getSNoteString().replaceAll("\n", "<br />");
					lastO = lastNote.getONoteString().replaceAll("\n", "<br />");
					lastA = lastNote.getANoteString().replaceAll("\n", "<br />");
					lastP = lastNote.getPNoteString().replaceAll("\n", "<br />");
				
					lastS += lastNote.getSNoteStatementHTMLString();
					lastO += lastNote.getONoteStatementHTMLString();
					lastA += lastNote.getANoteStatementHTMLString();
					lastP += lastNote.getPNoteStatementHTMLString();
				}

			}
					
		} else {
			if (_appointment.isMeeting()) {
				throw new IllegalValueException("Unable to select.  This is a meeting, not an appointment.");
			}
			throw new IllegalValueException("Unable to select.  This is not an appointment.");
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"clientLabel\":\"" + JSONObject.escape(_appointment.getClient().getLabel()) + "\",");
		b.append("\"comments\":\"" + JSONObject.escape(_appointment.getComments()) + "\",");
		b.append("\"dateStr\":\"" + JSONObject.escape(longer_date_format.format(appt_date)) + "\",");
		b.append("\"startTime\":\"" + JSONObject.escape(time_date_format.format(appt_date)) + "\",");
		b.append("\"endTime\":\"" + JSONObject.escape(time_date_format.format(appt_end.getTime())) + "\",");
		b.append("\"typeStr\":\"" + JSONObject.escape(_appointment.getType().getLabel()) + "\",");
		b.append("\"practitionerLabel\":\"" + JSONObject.escape(_appointment.getPractitioner().getLabel()) + "\",");
		b.append("\"currentS\":\"" + JSONObject.escape(currentS) + "\",");
		b.append("\"currentO\":\"" + JSONObject.escape(currentO) + "\",");
		b.append("\"currentA\":\"" + JSONObject.escape(currentA) + "\",");
		b.append("\"currentP\":\"" + JSONObject.escape(currentP) + "\",");
		
		b.append("\"currentSStatementHTML\":\"" + JSONObject.escape(currentSStatementHTML) + "\",");
		b.append("\"currentOStatementHTML\":\"" + JSONObject.escape(currentOStatementHTML) + "\",");
		b.append("\"currentAStatementHTML\":\"" + JSONObject.escape(currentAStatementHTML) + "\",");
		b.append("\"currentPStatementHTML\":\"" + JSONObject.escape(currentPStatementHTML) + "\",");
		
		b.append("\"lastDate\":\"" + JSONObject.escape(lastDate) + "\",");
		
		b.append("\"lastS\":\"" + JSONObject.escape(lastS) + "\",");
		b.append("\"lastO\":\"" + JSONObject.escape(lastO) + "\",");
		b.append("\"lastA\":\"" + JSONObject.escape(lastA) + "\",");
		b.append("\"lastP\":\"" + JSONObject.escape(lastP) + "\",");
		
		b.append("\"lastSStatementHTML\":\"" + JSONObject.escape(lastSStatementHTML) + "\",");
		b.append("\"lastOStatementHTML\":\"" + JSONObject.escape(lastOStatementHTML) + "\",");
		b.append("\"lastAStatementHTML\":\"" + JSONObject.escape(lastAStatementHTML) + "\",");
		b.append("\"lastPStatementHTML\":\"" + JSONObject.escape(lastPStatementHTML) + "\",");
		
		b.append("\"timeIn\":\"" + JSONObject.escape(timeInStr) + "\",");
		b.append("\"timeOut\":\"" + JSONObject.escape(timeOutStr) + "\",");
		b.append("\"timeInAlt\":\"" + JSONObject.escape(timeInAltStr) + "\",");
		b.append("\"timeOutAlt\":\"" + JSONObject.escape(timeOutAltStr) + "\",");
		b.append("\"clientId\":\"" + clientId + "\",");
		b.append("\"id\":\"" + _appointment.getId() + "\"");
		b.append("}");
		
		return b.toString();
	}
	
	public static String messageToJSON(UKOnlinePersonBean _from, UKOnlinePersonBean _to, String _subject, String _message, boolean _is_urgent) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		StringBuilder b = new StringBuilder();
		b.append("{\"from\":\"" + JSONObject.escape(_from.getLabel()) + "\",");
		b.append("\"to\":\"" + JSONObject.escape(_to.getLabel()) + "\",");
		b.append("\"subject\":\"" + JSONObject.escape(_subject) + "\",");
		b.append("\"message\":\"" + JSONObject.escape(_message) + "\",");
		b.append("\"urgent\":" + _is_urgent + "");
		b.append("}");
		
		return b.toString();
	}
	
	private String toJSONShort(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return "{\"label\":\"" + JSONObject.escape(_person.getLabel()) + "\",\"id\":\"" + _person.getId() + "\"}";
	}
	

	private String
	toJSON(SoapStatement _statement)
		throws Exception
	{
		//String value = "{\"label\" : \"" + _statement.getStatement() + "\" }";
		//return "<item><key>" + _statement.getSoapStatementId() + "</key><value><![CDATA[" + value + "]]></value></item>";
		
		StringBuilder b = new StringBuilder();
		
		b.append("{\"label\":\"" + JSONObject.escape(_statement.getStatement()) + "\",");
		b.append("\"child\":[");
		boolean needs_comma = false;
		Iterator itr = SOAPNotesBean.getChildStatements(_statement).iterator();
		while (itr.hasNext()) {
			SoapStatement child_statement = (SoapStatement)itr.next();
			if (needs_comma) { b.append(","); }
			b.append(this.toJSON(child_statement));
			needs_comma = true;
		}
		b.append("],");
		b.append("\"id\":\"" + _statement.getSoapStatementId() + "\"}");
		
		return b.toString();
		
	}
	
			
	private String toJSON(HerbDosage _obj, String _arg1) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		if (_obj.getMixDate() == null) {
			b.append("\"mixDate\":\"" + JSONObject.escape(CUBean.getUserDateString(new Date(), date_format)) + "\",");
		} else {
			b.append("\"mixDate\":\"" + JSONObject.escape(CUBean.getUserDateString(_obj.getMixDate(), date_format)) + "\",");
		}
		b.append("\"mixDesc\":\"" + JSONObject.escape(_obj.getDescription()) + "\",");
		b.append("\"mixNotes\":\"" + JSONObject.escape(_obj.getNotes()) + "\",");
		
		if (_obj.hasClient()) {
			b.append("\"clientLabel\":\"" + JSONObject.escape(_obj.getClient().getLabel()) + "\",");
			b.append("\"clientId\":\"" + _obj.getClient().getId() + "\",");
		} else {
			b.append("\"clientLabel\":\"[SELECT A CLIENT]\",");
			b.append("\"clientId\":\"0\",");
		}
		
		String person_search_str = herb_mix_person_search_hash.get(_arg1);
		if (person_search_str != null) {
			b.append("\"person\":[");
			b.append(person_search_str);
			b.append("],");
		}
		
		String herb_search_str = herb_mix_herb_search_hash.get(_arg1);
		if (herb_search_str != null) {
			b.append("\"herbSearchResult\":[");
			b.append(herb_search_str);
			b.append("],");
		}
		
		boolean needs_comma = false;
		
		b.append("\"direction\":[");
		Iterator itr = _obj.getUseDirections().iterator();
		while (itr.hasNext()) {
			try {

				HerbDosageUseDirections objX = (HerbDosageUseDirections)itr.next();

				if (needs_comma) {
					b.append(",");
				}
				b.append(this.toJSONShort(objX));
				needs_comma = true;

			} catch (java.util.ConcurrentModificationException y) {
				throw y;
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		b.append("],");
		
		needs_comma = false;
		b.append("\"a1\":\"" + _obj.getMHCostRetail200MLTotalString() + "\",");
		b.append("\"a2\":\"" + _obj.getMHCostPer1MLTotalString() + "\",");
		b.append("\"a3\":\"" + _obj.getHerbMLUsedTotalString() + "\",");
		b.append("\"a4\":\"" + _obj.getDosageCostTotalString() + "\",");
		b.append("\"a5\":\"" + _obj.getRetail200MLTotalString() + "\",");
		b.append("\"a6\":\"" + _obj.getFortyPercCOGSTotalString() + "\",");
		b.append("\"a7\":\"" + _obj.getTotalRetail40MLTotalString() + "\",");
		b.append("\"herbMapping\":[");
		itr = _obj.getItems().iterator();
		while (itr.hasNext()) {
			try {

				HerbDosageItemMapping objX = (HerbDosageItemMapping)itr.next();

				if (needs_comma) {
					b.append(",");
				}
				b.append(this.toJSON(objX));
				needs_comma = true;

			} catch (java.util.ConcurrentModificationException y) {
				throw y;
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		b.append("],");
		
		b.append("\"id\":\"" + _obj.getId() + "\"");
		b.append("}");
		
		return b.toString();
		
	}
	
	private String toJSONShort(HerbDosageUseDirections _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return "{\"label\":\"" + JSONObject.escape(_obj.getLabel()) + "\",\"id\":\"" + _obj.getId() + "\"}";
	}
			
	private String toJSON(HerbDosageItemMapping _obj) throws Exception {
		
		CheckoutCodeBean item = _obj.getItem();
		return "{\"label\" : \"" + item.getLabel() + "\"," +
						" \"p1\" : \"" + item.getAmountString() + "\"," +
						" \"p2\" : \"" + _obj.getAmountPerMLString() + "\"," +
						" \"p3\" : \"" + _obj.getML() + "\"," +
						" \"p4\" : \"" + _obj.getDosageCostString() + "\"," +
						" \"p5\" : \"-\"," +
						" \"p6\" : \"-\"," +
						" \"p7\" : \"" + _obj.getDosageAmountString() + "\"," +
						" \"p8\" : \"" + _obj.getCOGSString() + "\"," +
						" \"p9\" : \"" + _obj.getTotalRetail40MLString() + "\" }";
	}
	
	private CompanyBean
	getSelectedCompanyX(HttpSession _session, UKOnlinePersonBean _logged_in_person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		//System.out.println("getSelectedCompany invoked");
		CompanyBean selected_company = null;
		if (_session != null) {
			selected_company = (CompanyBean)_session.getAttribute("userCompany");
		}
		
		//System.out.println("1 >" + _logged_in_person);
		//System.out.println("2 >" + selected_company);
		
		if ((selected_company == null || selected_company.isNew()) && (_logged_in_person != null)) {
			// try to get the company from the logged in person
			selected_company = _logged_in_person.getDepartment().getCompany();
			//System.out.println("3 >" + selected_company.getLabel());
			if (_session != null) {
				_session.setAttribute("userCompany", selected_company);
				_session.setAttribute("adminCompany", selected_company);
			}
		}
		if  (selected_company == null || selected_company.isNew()) {
			selected_company = CompanyBean.getCompany(1);
			//System.out.println("4 >" + selected_company.getLabel());
			if (_session != null) {
				_session.setAttribute("userCompany", selected_company);
				_session.setAttribute("adminCompany", selected_company);
			}
		}
		return selected_company;
	}
	
	private SOAPNotesBean
	getCurrentNote(UKOnlinePersonBean _logged_in_person, AppointmentBean _selected_appointment) throws TorqueException, UniqueObjectNotFoundException, ObjectNotFoundException {
		SOAPNotesBean currentNote = null;
		try {
			currentNote = SOAPNotesBean.getSOAPNotes(_selected_appointment.getClient(), new Date());
		} catch (ObjectNotFoundException x) {
			System.out.println("creating new note");
			x.printStackTrace();
			currentNote = new SOAPNotesBean();
			currentNote.setAnalysisDate(new Date());
			currentNote.setCreateOrModifyPerson(_logged_in_person);
			currentNote.setPerson(_selected_appointment.getClient());
			currentNote.setPracticeArea(_selected_appointment.getPracticeArea());
		}
		return currentNote;
	}
	
	/*
	private void
	addSessionToHash(String _personValue, String _sessionId) {
		Vector sessions_vec = session_map_hash.get(_personValue);
		if (sessions_vec == null) {
			sessions_vec = new Vector();
		}
		if (!sessions_vec.contains(_sessionId)) {
			sessions_vec.addElement(_sessionId);
		}
		//session_map_hash.put(person.getValue(), session.getId());
		session_map_hash.put(_personValue, sessions_vec);
		System.out.println("putting session to hash >" + _personValue + " >" + _sessionId);
	}
	*/


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
	
	public String
	getMessageEmailString(CompanyBean _company, PersonBean _message_to, PersonBean _message_from, String _subject, String _message) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		AddressBean address = _company.getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
		//String appt_date_str = CUBean.getUserDateString(this.getAppointmentDate(), "EEEE, MMMM d, yyyy");
		
		StringBuffer buf = new StringBuffer();
		buf.append("<div>");
		buf.append("  <table cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px #acacac solid;width:615px\" align=\"center\">");
		buf.append("    <tbody><tr>");
		buf.append("      <td bgcolor=\"#11100e\" height=\"89\" valign=\"top\">");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:613px\">");
		buf.append("          <tbody><tr>");
		buf.append("            <td valign=\"middle\" height=\"89\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:24px;color:#8e8d8d;padding-left:30px;overflow:hidden;word-wrap:break-word;width:350px\">" + _company.getLabel());
		buf.append("            <br><span style=\"font-size:20px\">" + _subject + "</span></td>");
		if (_company.getId() == 5)
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
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word;padding:14px 26px 14px 26px\">" + _company.getLabel());
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
		buf.append("                   " + _message_from.getFirstNameString() + " has sent you the following message:");
		buf.append("                  <p>" + _message + "</p></td>");
		buf.append("                  ");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        <tr>");
		buf.append("            <td style=\"padding:12px 26px 16px 26px\">");
		buf.append("              <table width=\"561\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:561px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word\">");
		buf.append("If you have any questions, please contact " + _company.getLabel() + " at <a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a> . ");
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
		buf.append("                  <a href=\"http://www.sanowc.com\" style=\"color:#317679!important\" target=\"_blank\">" + _company.getLabel() + "</a>");
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
	
	
	private String
	doHerbSpreadsheet(HerbDosage _dosage) throws java.io.IOException, TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		UKOnlineCourseReportLister listerObj = new UKOnlineCourseReportLister();

		//short column_index = 0;
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + "sano-herb-spreadsheet.xls"));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);

		HSSFCellStyle cell_style = wb.createCellStyle();
		cell_style.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
		cell_style.setFillBackgroundColor( (short)0xc );
		cell_style.setFillForegroundColor( (short)0xc );
		HSSFFont header_font = wb.createFont();
		//set font 1 to 12 point type
		header_font.setFontHeightInPoints((short) 12);
		//make it white
		header_font.setColor( (short)0x1 );
		// make it bold
		//arial is the default font
		header_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cell_style.setFont(header_font);
		cell_style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cell_style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cell_style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cell_style.setBorderBottom(HSSFCellStyle.BORDER_THIN);


		HSSFCellStyle cell_style2 = wb.createCellStyle();
		cell_style2.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
		cell_style2.setFillBackgroundColor( (short)0xc );
		cell_style2.setFillForegroundColor( (short)0xc );
		header_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cell_style2.setFont(header_font);
		cell_style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cell_style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cell_style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cell_style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cell_style2.setAlignment(HSSFCellStyle.ALIGN_RIGHT);


		HSSFCellStyle cell_style7 = wb.createCellStyle();
		cell_style7.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
		//color_index = (new org.apache.poi.hssf.util.HSSFColor.LIGHT_GREEN()).getIndex();
		short color_index = (new org.apache.poi.hssf.util.HSSFColor.WHITE()).getIndex();
		cell_style7.setFillBackgroundColor( color_index );
		cell_style7.setFillForegroundColor( color_index );
		HSSFFont header_font7 = wb.createFont();
		header_font7.setFontHeightInPoints((short) 10);
		header_font7.setColor( (short)0x0 );
		// make it bold
		//arial is the default font
		//header_font7.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cell_style7.setFont(header_font7);
		cell_style7.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cell_style7.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cell_style7.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cell_style7.setBorderBottom(HSSFCellStyle.BORDER_THIN);


		HSSFCellStyle cell_style8 = wb.createCellStyle();
		cell_style8.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
		cell_style8.setFillBackgroundColor( color_index );
		cell_style8.setFillForegroundColor( color_index );
		cell_style8.setFont(header_font7);
		cell_style8.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cell_style8.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cell_style8.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cell_style8.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cell_style8.setAlignment(HSSFCellStyle.ALIGN_RIGHT);


		UKOnlinePersonBean client = listerObj.getPerson();
		Date start_date = listerObj.getStartDate();
		Date end_date = listerObj.getEndDate();

		Vector practice_areas = listerObj.getPracticeAreas();
		Vector types = listerObj.getCheckoutCodeTypes();
		
		// practice and address
		
		Date now = new Date();
		
		listerObj.addCell(sheet, 0, (short)4, _dosage.getCompany().getLabel());
		listerObj.addCell(sheet, (17 + 4) , (short)2, _dosage.getDescription());
		Iterator useIterator = _dosage.getUseDirections().iterator();
		int rowU = 19;
		for (; useIterator.hasNext(); rowU++) {
			HerbDosageUseDirections useDirectionsObj = (HerbDosageUseDirections)useIterator.next();
			listerObj.addCell(sheet, rowU, (short)2, useDirectionsObj.getLabel());
		}
		listerObj.addCell(sheet, (24 + 4) , (short)2, CUBean.getUserDateString(_dosage.getMixDate()));
		listerObj.addCell(sheet, (27 + 4) , (short)2, _dosage.getNotes());
		listerObj.addCell(sheet, (24 + 4) , (short)7, _dosage.getMixer().getInitials());
		listerObj.addCell(sheet, 1, (short)1, "Herb Dosage Cost Analysis Part II - " + CUBean.getUserDateString(now) + " - " + _dosage.getClient().getLabel());
		
		int row_index = 7;
		
		short tots_ml = 0;
		
		//Iterator itr = _herb_codes.keySet().iterator();
		Iterator itr = _dosage.getItems().iterator();
		for (; itr.hasNext(); row_index++) {
			HerbDosageItemMapping mapping_obj = (HerbDosageItemMapping)itr.next();
			CheckoutCodeBean key = mapping_obj.getItem();
			//CheckoutCodeBean key = (CheckoutCodeBean)itr.next();
			//BigDecimal ml = _herb_codes.get(key);
			listerObj.addCell(sheet, row_index, (short)0, "MH");
			listerObj.addCell(sheet, row_index, (short)1, key.getLabel());
			listerObj.addCell(sheet, row_index, (short)2, key.getAmount().toString());
			//listerObj.addCell(sheet, row_index, (short)5, ml.toString());
			listerObj.addCell(sheet, row_index, (short)5, mapping_obj.getML() + "");
			
			tots_ml += mapping_obj.getML();
		}

		//listerObj.addCell(sheet, row_index + 3, (short)5, tots_ml); removed 4/24/19

		String saveFilename = "herb-dosage-" + _dosage.getClient().getLabel() + "-" + System.currentTimeMillis() + ".xls";
		FileOutputStream fileOut = new FileOutputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + saveFilename);
		wb.write(fileOut);
		fileOut.close();
    
		return saveFilename;
	}
	
	private String
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
