/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.util;

//import com.badiyan.torque.SiteUsage;
//import com.badiyan.torque.SiteUsagePeer;
//import com.badiyan.uk.mtp.tasks.SessionCounterMonitor;


import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author marlo
 */
public class SessionCounter implements HttpSessionListener {
	
	public static SimpleDateFormat day_format = new SimpleDateFormat("yyyy.MM.dd");
	public static SimpleDateFormat hour_format = new SimpleDateFormat("yyyy.MM.dd HH");
	
	private static HashMap<String,Integer> total_log_ins_by_day_student = new HashMap<String,Integer>();
	private static HashMap<String,Integer> total_log_ins_by_hour_student = new HashMap<String,Integer>();
	
	private static HashMap<String,Integer> max_concurrent_by_day = new HashMap<String,Integer>();
	private static HashMap<String,Integer> max_concurrent_by_hour = new HashMap<String,Integer>();
	
	private static HashMap<String,UKOnlinePersonBean> student_sessions = new HashMap<String,UKOnlinePersonBean>();
	private static HashMap<String,UKOnlinePersonBean> expired_sessions = new HashMap<String,UKOnlinePersonBean>();
	
	private static HashMap<String,HttpSession> sessions = new HashMap<String,HttpSession>();
	private static HashMap<String,String> browser_sessions = new HashMap<String,String>();
	
	
	
	private static int numActiveSessions = 0;

	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		this.sessionCreated(session);
	}

	private void sessionCreated(HttpSession session) {
		
		System.out.println("SESSION CREATED " + session.getId());
		numActiveSessions++;
		
		sessions.put(session.getId(), session);
		
		
		Date now = new Date();
		String by_day_key = day_format.format(now);
		String by_hour_key = hour_format.format(now);

		Integer max_concurrent = max_concurrent_by_hour.get(by_hour_key);
		if (max_concurrent == null)
			max_concurrent = new Integer(numActiveSessions);
		else if (numActiveSessions > max_concurrent)
			max_concurrent = numActiveSessions;
		System.out.println("max_concurrent >" + max_concurrent);
		System.out.println("numActiveSessions >" + numActiveSessions);
		max_concurrent_by_hour.put(by_hour_key, max_concurrent);

		Integer max_concurrent_day = max_concurrent_by_day.get(by_day_key);
		if (max_concurrent_day == null)
			max_concurrent_day = new Integer(numActiveSessions);
		else if (numActiveSessions > max_concurrent_day)
			max_concurrent_day = numActiveSessions;
		max_concurrent_by_day.put(by_day_key, max_concurrent_day);
		
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		System.out.println("SESSION DESTROYED " + session.getId());
		
		if (numActiveSessions > 0)
			numActiveSessions--; // should never be negative
		
		
		String session_id = session.getId();
		
		UKOnlinePersonBean expiredPerson = student_sessions.remove(session_id);
		browser_sessions.remove(session_id);
		expired_sessions.put(session_id, expiredPerson);
		
	}

	public static int getNumActiveSessions() {
		return numActiveSessions;
	}

	public static int getNumStudentSessions() {
		return student_sessions.size();
	}

	public static int getNumExpiredStudentSessions() {
		return expired_sessions.size();
	}
	
	public static Iterator
	getActiveSessionKeys() {
		return student_sessions.keySet().iterator();
	}
	
	public static Iterator
	getInactiveSessionKeys() {
		return expired_sessions.keySet().iterator();
	}
	
	public static UKOnlinePersonBean
	getStudent(String _sessionId) {
		return student_sessions.get(_sessionId);
	}
	
	public static UKOnlinePersonBean
	getInactiveStudent(String _sessionId) {
		return expired_sessions.get(_sessionId);
	}
	
	public static long
	getSessionCreationTime(String _sessionId) {
		HttpSession session = sessions.get(_sessionId);
		if (session != null) {
			try {
				return session.getCreationTime();
			} catch (IllegalStateException x) {
			}
		}
		return -1l;
	}
	
	public static long
	getSessionLastAccessedTime(String _sessionId) {
		HttpSession session = sessions.get(_sessionId);
		if (session != null) {
			try {
				return session.getLastAccessedTime();
			} catch (IllegalStateException x) {
			}
		}
		return -1l;
	}
	
	public static void
	setBrowserInfo(String _sessionId, String _browserInfo) {
		browser_sessions.put(_sessionId, _browserInfo);
	}
	
	public static String
	getBrowserInfoString(String _sessionId) {
		String str = browser_sessions.get(_sessionId);
		if (str == null)
			return "";
		return str;
	}

	/*
	public static Integer getMaxConcurrentForHour(Date _date) {
				
		String by_hour_key = SessionCounter.hour_format.format(_date);

		Integer max_concurrent = max_concurrent_by_hour.get(by_hour_key);
		if (max_concurrent == null)
			max_concurrent = getValue(by_hour_key, SessionCounterMonitor.CONCURRENT_BY_HOUR_USAGE_TYPE);
		max_concurrent_by_hour.put(by_hour_key, max_concurrent);
		return max_concurrent;
	}

	public static Integer getMaxConcurrentForDay(Date _date) {
				
		String by_day_key = SessionCounter.day_format.format(_date);

		Integer max_concurrent = max_concurrent_by_day.get(by_day_key);
		if (max_concurrent == null)
			max_concurrent = getValue(by_day_key, SessionCounterMonitor.CONCURRENT_BY_DAY_USAGE_TYPE);
		max_concurrent_by_day.put(by_day_key, max_concurrent);
		return max_concurrent;
	}

	public static Integer getNumStudentLogInsForHour(Date _date) {
				
		String by_hour_key = SessionCounter.hour_format.format(_date);

		Integer log_ins_for_hour = total_log_ins_by_hour_student.get(by_hour_key);
		if (log_ins_for_hour == null)
			log_ins_for_hour = getValue(by_hour_key, SessionCounterMonitor.TOTAL_LOG_INS_BY_HOUR_STUDENT_USAGE_TYPE);
		total_log_ins_by_hour_student.put(by_hour_key, log_ins_for_hour);
		return log_ins_for_hour;
	}
	
	

	public static Integer getNumStudentLogInsForDay(Date _date) {
				
		String by_day_key = SessionCounter.day_format.format(_date);

		Integer log_ins_for_day = total_log_ins_by_day_student.get(by_day_key);
		if (log_ins_for_day == null)
			log_ins_for_day = getValue(by_day_key, SessionCounterMonitor.TOTAL_LOG_INS_BY_DAY_STUDENT_USAGE_TYPE);
		total_log_ins_by_day_student.put(by_day_key, log_ins_for_day);
		return log_ins_for_day;
	}
	*/
	
	public static void add(HttpSession session, UKOnlinePersonBean _person, HttpServletRequest _request)
	{
		try
		{
		
			Date now = new Date();
			String by_day_key = day_format.format(now);
			String by_hour_key = hour_format.format(now);

			Integer max_concurrent = max_concurrent_by_hour.get(by_hour_key);
			if (max_concurrent == null)
				max_concurrent = new Integer(numActiveSessions);
			else if (numActiveSessions > max_concurrent)
				max_concurrent = numActiveSessions;
			
			
			
			max_concurrent_by_hour.put(by_hour_key, max_concurrent);

			Integer max_concurrent_day = max_concurrent_by_day.get(by_day_key);
			if (max_concurrent_day == null)
				max_concurrent_day = new Integer(numActiveSessions);
			else if (numActiveSessions > max_concurrent_day)
				max_concurrent_day = numActiveSessions;
			max_concurrent_by_day.put(by_day_key, max_concurrent_day);
		
		
			sessions.put(session.getId(), session);
			student_sessions.put(session.getId(), _person);

			/*
			Enumeration enx = _request.getHeaderNames();
			while (enx.hasMoreElements()) {
				String headerName = (String)enx.nextElement();
				String headerValue = _request.getHeader(headerName);
				//System.out.println("headerName >" + headerName + ", headerValue >" + headerValue);
				if (headerName.toLowerCase().indexOf("agent")) {
					
				}
			}
			*/
			
			String agent = _request.getHeader("user-agent");
			if (agent != null) {
				browser_sessions.put(session.getId(), agent);
			}
			
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	/*
	private static int getValue(String _date_key, int _usage_type) {
		
		try
		{
			Criteria crit = new Criteria();
			crit.add(SiteUsagePeer.DATE_KEY, _date_key);
			crit.add(SiteUsagePeer.USAGE_TYPE, _usage_type);
			List list = SiteUsagePeer.doSelect(crit);
			if (list.size() == 1)
			{
				SiteUsage obj = (SiteUsage)list.get(0);
				return obj.getUsageValue();
			}
			//else
			//	throw new UniqueObjectNotFoundException("Unique SiteUsage object not found.");
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		
		return 0;
		
	}
	 * 
	 */

}
