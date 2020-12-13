/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.util;


import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.beans.AppointmentBean;
import com.badiyan.uk.online.beans.ClientReviewReason;
import java.net.SocketException;
import java.util.Date;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.UidGenerator;
import org.apache.torque.TorqueException;
	

/**
 *
 * @author marlo
 */
public class ICalBuilder {
	
	
	public static String
	toICal(AppointmentBean _appointment) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, SocketException {
		
		// start date
		java.util.Calendar startDate = java.util.Calendar.getInstance();
		startDate.setTimeZone(_appointment.getCompany().getSettings().getTimeZone());
		startDate.setTime(_appointment.getAppointmentDate());
		
		// end date
		java.util.Calendar endDate = java.util.Calendar.getInstance();
		endDate.setTimeZone(_appointment.getCompany().getSettings().getTimeZone());
		endDate.setTime(_appointment.getAppointmentDate());
		endDate.add(java.util.Calendar.MINUTE, _appointment.getDuration());
		
		// create event
		String eventName = _appointment.getType().getLabel();
		DateTime start = new DateTime(startDate.getTime());
		DateTime end = new DateTime(endDate.getTime());
		VEvent meeting = new VEvent(start, end, eventName);

		// add timezone info..
		VTimeZone timeZone = ICalBuilder.convertToVTimeZone(_appointment.getCompany().getSettings().getTimeZone());
		
		/*
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		VTimeZone timeZone = registry.getTimeZone("America/Mexico_City").getVTimeZone();
		*/
		
		meeting.getProperties().add(timeZone.getTimeZoneId());

		// generate unique identifier..
		UidGenerator ug = new UidGenerator("uidGen");
		Uid uid = ug.generateUid();
		meeting.getProperties().add(uid);

		// Create a calendar
		net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
		icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
		icsCalendar.getProperties().add(CalScale.GREGORIAN);
		
		// Add the event and print
		icsCalendar.getComponents().add(meeting);
		System.out.println(icsCalendar);
		
		return icsCalendar.toString();
		
	}
	
	
	
	
	public static String
	toICal(ClientReviewReason _review_reason) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, SocketException {
		
		// start date
		java.util.Calendar startDate = java.util.Calendar.getInstance();
		startDate.setTimeZone(_review_reason.getPerson().getDepartment().getCompany().getSettings().getTimeZone());
		startDate.setTime(_review_reason.getReviewDate());
		
		// end date
		/*
		java.util.Calendar endDate = java.util.Calendar.getInstance();
		endDate.setTimeZone(_appointment.getCompany().getSettings().getTimeZone());
		endDate.setTime(_appointment.getAppointmentDate());
		endDate.add(java.util.Calendar.MINUTE, _appointment.getDuration());
		*/
		
		// create event
		//String eventName = _appointment.getType().getLabel();
		String eventName = _review_reason.getLabel();
		DateTime start = new DateTime(startDate.getTime());
		//DateTime end = new DateTime(endDate.getTime());
		//VEvent meeting = new VEvent(start, end, eventName);
		VEvent meeting = new VEvent(start, eventName);

		// add timezone info..
		VTimeZone timeZone = ICalBuilder.convertToVTimeZone(_review_reason.getPerson().getDepartment().getCompany().getSettings().getTimeZone());
		
		meeting.getProperties().add(timeZone.getTimeZoneId());

		// generate unique identifier..
		UidGenerator ug = new UidGenerator("uidGen");
		Uid uid = ug.generateUid();
		meeting.getProperties().add(uid);

		// Create a calendar
		net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
		icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
		icsCalendar.getProperties().add(CalScale.GREGORIAN);
		
		// Add the event and print
		icsCalendar.getComponents().add(meeting);
		System.out.println(icsCalendar);
		
		return icsCalendar.toString();
		
	}
	
    
    private static VTimeZone convertToVTimeZone(java.util.TimeZone _tz) {
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		//System.out.println("_tz.getID() >" + _tz.getID());
        net.fortuna.ical4j.model.TimeZone timezone = registry.getTimeZone(_tz.getID());
        return timezone.getVTimeZone();
    }
}
