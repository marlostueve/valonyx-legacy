
package com.badiyan.uk.online.servlets;

import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.beans.CompanyBean;
import com.badiyan.uk.beans.PersonTitleBean;
import com.badiyan.uk.beans.RoleBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.PDF.EndOfDayLogBuilder;
import com.badiyan.uk.online.beans.UKOnlineLoginBean;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import com.badiyan.uk.online.util.SessionCounter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import org.apache.torque.TorqueException;
import org.json.simple.JSONObject;

/*
import com.google.bitcoin.core.AbstractPeerEventListener;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;
import com.google.bitcoin.utils.BriefLogFormatter;
//import com.google.common.collect.Lists;
*/


/**
 *
 * @author
 * marlo
 */
public class ShopServlet extends HttpServlet {


	protected void processRequest(HttpServletRequest _request, HttpServletResponse _response)
			throws ServletException, IOException {
		//_response.setContentType("text/html;charset=UTF-8");
		_response.setContentType("application/json;charset=UTF-8");
		_response.setHeader("Access-Control-Allow-Origin", "https://valonyx.com");
		//_response.setHeader("Access-Control-Allow-Origin", "http://localhost:8383");
		_response.setHeader("Access-Control-Allow-Credentials", "true");
		
		
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
			System.out.println("session >" + session.getId());
			
			Cookie[] cookieArr = _request.getCookies();
			if (cookieArr != null) {
				for (int i = 0; i < cookieArr.length; i++) {
					Cookie cook = (Cookie)cookieArr[i];
					System.out.println("cookie[" + i + "] >" + cook.getName() + " >" + cook.getValue());
				}
			}
			
			Thread.sleep(2000);
			
			
			if (command.equals("getShopHome")) {
				
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
				
			} else if (command.equals("auth")) {
				
				String email = parameter;
				String password = arg1;
				
				UKOnlinePersonBean person = null;
				Object loginBean = session.getAttribute("loginBean");
				if (loginBean == null) {
					
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
				} else {
					person = (UKOnlinePersonBean)((UKOnlineLoginBean)loginBean).getPerson();
				}
				
				if (person == null) {
					throw new IllegalValueException("Auth failed.");
				}
				
				//CompanyBean defaultCompany = CompanyBean.maintainCompany("[DEFAULT]");
				
				
				StringBuffer b = new StringBuffer();
				b.append("{\"auth\":[");
				b.append(this.toJSON(person));
				b.append("]}");
				
				System.out.println(b.toString());
				
				writer.println(b.toString());
				
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
					b.append(this.toJSON(person));
					b.append("]}");
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
	
	private String toJSON(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		return "{ \"label\":\"" + _person.getLabel() + "\"," +
		" \"empId\":\"" + _person.getEmployeeNumberString() + "\"," +
		" \"active\":\"" + _person.isActive() + "\"," +
		" \"lastLogIn\":\"" + _person.getLastLogInDateString() + "\"," +
		" \"role\":\"" + JSONObject.escape(_person.getRolesString()) + "\"," +
		" \"location\":\"" + JSONObject.escape(_person.getDepartmentNameString()) + "\"," +
		" \"title\":\"" + JSONObject.escape(_person.getJobTitleString()) + "\"," +
		" \"id\":\"" + _person.getId() + "\"}";
	}
	
	private String toJSONShort(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		return "{ \"label\":\"" + _person.getLabel() + "\"," +
		" \"id\":\"" + _person.getId() + "\"" +
		"}";
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
}
