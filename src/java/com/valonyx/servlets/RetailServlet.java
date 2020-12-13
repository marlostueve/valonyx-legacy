/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.servlets;

import com.badiyan.torque.AdminProductPieChartMap;
import com.badiyan.uk.beans.AudienceBean;
import com.badiyan.uk.beans.BuildingBean;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.beans.ClassroomBean;
import com.badiyan.uk.beans.CompanyBean;
import com.badiyan.uk.beans.CompanySettingsBean;
import com.badiyan.uk.beans.CourseGroupBean;
import com.badiyan.uk.beans.DepartmentBean;
import com.badiyan.uk.beans.DepartmentTypeBean;
import com.badiyan.uk.beans.LocationBean;
import com.badiyan.uk.beans.PersonBean;
import com.badiyan.uk.beans.PersonGroupBean;
import com.badiyan.uk.beans.PersonTitleBean;
import com.badiyan.uk.beans.ReportTemplateBean;
import com.badiyan.uk.beans.ResourceBean;
import com.badiyan.uk.beans.RoleBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.beans.UKOnlineLoginBean;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import com.badiyan.uk.online.beans.UKOnlineRoleBean;
import com.badiyan.uk.online.servlets.RetailSocketServlet;
import com.badiyan.uk.online.struts.OrganizationMainAction;
import com.badiyan.uk.online.util.PasswordGenerator;
import com.badiyan.uk.online.util.SessionCounter;
import com.valonyx.beans.Endpoint;
import com.valonyx.beans.IoTLOKReportTemplateBean;
import com.valonyx.beans.PersonSettingsBean;
import com.valonyx.beans.Product;
import com.valonyx.beans.ProductInteraction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.torque.TorqueException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.valonyx.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author marlo
 */
public class RetailServlet extends HttpServlet {
	
	private Timer timerObj;
	private static final Logger LOG = LoggerFactory.getLogger(RetailServlet.class);
	private final static String KAA_APPLICATION_NAME = "Navy Plutonium";
	public final static String KAA_SERVER_HOST = "lnn1383.aus.us.siteprotect.com";
	private boolean kaa_initialized = false;
	
	public static HashMap<UKOnlinePersonBean,UKOnlinePersonBean> sim_hash = new HashMap<>(3);
	public static HashMap<String,UKOnlinePersonBean> sim_hash_session = new HashMap<>(3);
	
	public static HashMap<String,String> confirm_email_hash = new HashMap<String,String>(11);
	
	private static HashMap<PersonBean,IoTLOKReportTemplateBean> template_hash = new HashMap<PersonBean,IoTLOKReportTemplateBean>(3);
	
	static String extractPostRequestBody(HttpServletRequest request) {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			Scanner s = null;
			try {
				s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return s.hasNext() ? s.next() : "";
		}
		return "";
	}

	
	protected void processRequest(HttpServletRequest _request, HttpServletResponse _response)
			throws ServletException, IOException {
		
		_response.setContentType("application/json;charset=UTF-8");
		_response.setHeader("Access-Control-Allow-Origin", "*");
		
		PrintWriter writer = _response.getWriter();
		
		this.startKAAMonitorTask();
		
		try {
			
			HttpSession session = _request.getSession(true);

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
			System.out.println("RetailServlet invoked!");
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
			
			if (command == null) {
				
				Enumeration header_enum = _request.getHeaderNames();
				while (header_enum.hasMoreElements()) {
					String header_name = (String)header_enum.nextElement();
					System.out.println("header_name >" + header_name + ", value >" + _request.getHeader(header_name));
					Enumeration headers_enum = _request.getHeaders(header_name);
					while (headers_enum.hasMoreElements()) {
						String header_val = (String)headers_enum.nextElement();
						System.out.println("   value >" + header_val);
					}
				}
				Enumeration params_enum = _request.getParameterNames();
				while (params_enum.hasMoreElements()) {
					String param_name = (String)params_enum.nextElement();
					System.out.println("param_name >" + param_name + ", value >" + _request.getParameter(param_name));
				}
				
				
				/*
				StringBuffer jb = new StringBuffer();
				String line = null;
				try {
				  BufferedReader reader = _request.getReader();
				  while ((line = reader.readLine()) != null)
					jb.append(line);
				  
				} catch (Exception e) {  }
				
				System.out.println("jb >" + jb.toString());
				*/
				
				

				/*
				try {
				  JSONObject jsonObject = org.json.HTTPfromObject(jb.toString());
				} catch (ParseException e) {
				  // crash and burn
				  throw new IOException("Error parsing JSON request string");
				}
				*/
				
				// toggle client
				
				// extract the endpoint hash from the JSON
				
				String jsonBlob = extractPostRequestBody(_request);
				System.out.println("\n\n jsonBlob >" + jsonBlob);
				
				
				Endpoint endpoint = this.mineEndpoint(jsonBlob);
				
				System.out.println("found endpoint >" + endpoint);
				
				command = "toggleEndpoint";
				arg1 = "3138";
				arg2 = endpoint.getValue();
			
				
				/*
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"NOT ERROR\"," +
						"\"heading\":\"Oh Snap!!\"," +
						"\"text\":\"OK\"" +
						"}");
				b.append("]}");

				//_response.setStatus(HttpServletResponse.);
				writer.println(b.toString());
				return;
				*/
			}
			
			
			
			if (command.equals("auth")) {
				
				String email = arg1;
				String password = arg2;
				
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
				
				CompanyBean selected_bu = this.getSelectedCompany(session, person);
				
				
				StringBuffer b = new StringBuffer();
				b.append("{\"auth\":[");
				b.append(this.toJSON(selected_bu, person));
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
				
			} else if (command.equals("productInteraction")) {
				
				UKOnlinePersonBean request_person = null;
				try {
					if (arg1 != null) {
						request_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				CompanyBean selected_bu = this.getSelectedCompany(session, request_person);
				
				System.out.println("found person >" + request_person.getLabel());
				System.out.println("selected_bu >" + selected_bu.getLabel());
				
				// product sale from scanner, what's required??
				
				String scanner_id = arg2;
				String store_id = arg3;
				String pos_upc = arg4;
				String serial_number = arg5;
				String interaction_type = arg6;
				
				// find the product for the pos upc
				
				if ( pos_upc == null || pos_upc.isEmpty() ) {
					throw new IllegalValueException("You must specify a POS Bar Code");
				}
				
				if ( serial_number == null || serial_number.isEmpty() ) {
					throw new IllegalValueException("You must specify a Serial # or unique identifier");
				}
				
				if ((pos_upc != null) && (serial_number != null)) {
					if (pos_upc.equals(serial_number)) {
						throw new IllegalValueException("Unable to record interaction.  Please scan the POS Bar code and the Serial # Car Code.");
					}
				}
				
				Product scanned_product = null;
				
				try {
					scanned_product = Product.getProduct(pos_upc);
					System.out.println("scanned_product >" + scanned_product.getLabel());
				} catch (ObjectNotFoundException x) {
					// https://api.upcitemdb.com/prod/trial/lookup?upc=711719503309
					
					System.out.println("Unable to locate product for code >" + pos_upc);
					
					String product_blob = "";

					try {
						product_blob = this.getProductBlob(pos_upc);
					} catch (IOException e1) {
						try {
							product_blob = this.getProductBlob(serial_number);
						} catch (IOException e2) {
							System.out.println("ERROR: couldn't open URL ");
						}
					}
					
					System.out.println("product_blob >" + product_blob);
					
					if (product_blob.isEmpty()) {
						throw new IllegalValueException("Unable to find product for bar code >" + pos_upc);
					} else {
						scanned_product = this.mineProduct(request_person, product_blob);
						scanned_product.save();
						System.out.println("mined_product >" + scanned_product.getLabel());
					}
				}
				
				String return_json = null;
				
				// grab an interaction for this product/serial number
				
				ProductInteraction interaction = null;
				try {
					interaction = ProductInteraction.getInteraction(request_person, scanned_product, serial_number, Short.parseShort(interaction_type), true);
					System.out.println("interaction >" + interaction.getLabel());
					// find corresponding Endpoint
					Endpoint endpoint_obj = Endpoint.getEndpointBySerialNumber(serial_number);
					System.out.println("found EP to toggle >" + endpoint_obj.getLabel());
					boolean is_active = false;
					if (interaction.getInteractionType() == ProductInteraction.SALE_TYPE ||
							interaction.getInteractionType() == ProductInteraction.SALE_ACTIVATED_TYPE) {
						// try to activate
						is_active = true;
					}
					endpoint_obj.setActive(is_active);
					endpoint_obj.save();

					updateServerProfile(endpoint_obj.getEndpointHash(), KAAAdminClientManager.SERVER_PROFILE_VERSION, is_active);

					//writer.println("{\"message\":[{\"type\":\"SUCCESS\"}]}");
					
				} catch (IllegalValueException x) {
					Endpoint endpoint_obj = Endpoint.getEndpointBySerialNumber(serial_number);
					endpoint_obj.setErrorMessage(x.getMessage());
					endpoint_obj.save();
					throw x;
				} catch (ObjectNotFoundException x) {
					
					if (scanned_product == null) {
						
					} else {
						if (interaction == null) {
							
						} else {
							// has a product and an interaction
							if (interaction.getInteractionType() == ProductInteraction.INVENTORY_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel() + " added to inventory.  " + x.getMessage()) + ".\"}]}";
							} else if (interaction.getInteractionType() == ProductInteraction.SALE_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel() + " sold.  " + x.getMessage()) + ".\"}]}";
							} else if (interaction.getInteractionType() == ProductInteraction.SALE_ACTIVATED_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel() + " sold and activated.  " + x.getMessage()) + ".\"}]}";
							} else if (interaction.getInteractionType() == ProductInteraction.RETURN_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel() + " returned.  " + x.getMessage()) + ".\"}]}";
							} else if (interaction.getInteractionType() == ProductInteraction.DEACTIVATED_CHARGEBACK_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel() + " deactivated due to chargeback.  " + x.getMessage()) + ".\"}]}";
							} else if (interaction.getInteractionType() == ProductInteraction.DEACTIVATED_OTHER_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel() + " deactivated other.  " + x.getMessage()) + ".\"}]}";
							}
							
						}
					}
					
				}
				
				if (return_json == null) {
					
					if (scanned_product == null) {
						
					} else {
						if (interaction == null) {
							
						} else {
							// has a product and an interaction
							if (interaction.getInteractionType() == ProductInteraction.INVENTORY_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel()) + " added to inventory.\"}]}";
							} else if (interaction.getInteractionType() == ProductInteraction.SALE_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel()) + " sold.\"}]}";
							} else if (interaction.getInteractionType() == ProductInteraction.SALE_ACTIVATED_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel()) + " sold and activated.\"}]}";
							} else if (interaction.getInteractionType() == ProductInteraction.RETURN_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel()) + " returned.\"}]}";
							} else if (interaction.getInteractionType() == ProductInteraction.DEACTIVATED_CHARGEBACK_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel()) + " deactivated due to chargeback.\"}]}";
							} else if (interaction.getInteractionType() == ProductInteraction.DEACTIVATED_OTHER_TYPE) {
								return_json = "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(scanned_product.getLabel()) + " deactivated other.\"}]}";
							}
							
						}
					}
					
				}
				
				if (return_json == null) {
					if ( (scanned_product == null) && (interaction == null) ) {
						writer.println("{\"message\":[{\"type\":\"ERROR\",\"text\":\"Unable to find product or interaction.\"}]}");
					} else if (scanned_product == null) {
						writer.println("{\"message\":[{\"type\":\"ERROR\",\"text\":\"Unable to find product.\"}]}");
					} else {
						writer.println("{\"message\":[{\"type\":\"ERROR\",\"text\":\"Unable to find interaction.\"}]}");
					}
					
				} else {
					writer.println(return_json);
				}
				
				
				try {
					RetailSocketServlet.sendMessage("reload");
				} catch (IllegalValueException x) {
					x.printStackTrace();
				}
				
			} else if (command.equals("activateEndpoint")) {
				
				UKOnlinePersonBean request_person = null;
				try {
					if (arg1 != null) {
						request_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				CompanyBean selected_bu = this.getSelectedCompany(session, request_person);
				
				System.out.println("found person >" + request_person.getLabel());
				System.out.println("selected_bu >" + selected_bu.getLabel());
				
				// product sale from scanner, what's required??
				
				String scanner_id = arg2;
				String store_id = arg3;
				String pos_upc = arg4;
				String serial_number = arg5;
				String interaction_type = arg6;
				
				// find the product for the pos upc
				
				if ( pos_upc == null || pos_upc.isEmpty() ) {
					throw new IllegalValueException("You must specify a POS Bar Code");
				}
				
				if ( serial_number == null || serial_number.isEmpty() ) {
					throw new IllegalValueException("You must specify a Serial # or unique identifier");
				}
				
				if ((pos_upc != null) && (serial_number != null)) {
					if (pos_upc.equals(serial_number)) {
						throw new IllegalValueException("Unable to activate.  Please scan the POS Bar code and the Serial # Car Code.");
					}
				}
				
				Endpoint endpoint_obj = Endpoint.getEndpointBySerialNumber(serial_number);
				System.out.println("found EP to activate >" + endpoint_obj.getLabel());
				boolean is_active = true;
				endpoint_obj.setActive(is_active);
				endpoint_obj.save();

				updateServerProfile(endpoint_obj.getEndpointHash(), KAAAdminClientManager.SERVER_PROFILE_VERSION, is_active);

				//writer.println("{\"message\":[{\"type\":\"SUCCESS\"}]}");
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Endpoint activated.\"}]}");
				
				try {
					RetailSocketServlet.sendMessage("reload");
				} catch (IllegalValueException x) {
					x.printStackTrace();
				}
				
			} else if (command.equals("inactivateEndpoint")) {
				
				UKOnlinePersonBean request_person = null;
				try {
					if (arg1 != null) {
						request_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				CompanyBean selected_bu = this.getSelectedCompany(session, request_person);
				
				System.out.println("found person >" + request_person.getLabel());
				System.out.println("selected_bu >" + selected_bu.getLabel());
				
				// product sale from scanner, what's required??
				
				String scanner_id = arg2;
				String store_id = arg3;
				String pos_upc = arg4;
				String serial_number = arg5;
				String interaction_type = arg6;
				
				// find the product for the pos upc
				
				if ( pos_upc == null || pos_upc.isEmpty() ) {
					throw new IllegalValueException("You must specify a POS Bar Code");
				}
				
				if ( serial_number == null || serial_number.isEmpty() ) {
					throw new IllegalValueException("You must specify a Serial # or unique identifier");
				}
				
				if ((pos_upc != null) && (serial_number != null)) {
					if (pos_upc.equals(serial_number)) {
						throw new IllegalValueException("Unable to activate.  Please scan the POS Bar code and the Serial # Car Code.");
					}
				}
				
				Endpoint endpoint_obj = Endpoint.getEndpointBySerialNumber(serial_number);
				System.out.println("found EP to activate >" + endpoint_obj.getLabel());
				boolean is_active = false;
				endpoint_obj.setActive(is_active);
				endpoint_obj.save();

				updateServerProfile(endpoint_obj.getEndpointHash(), KAAAdminClientManager.SERVER_PROFILE_VERSION, is_active);

				//writer.println("{\"message\":[{\"type\":\"SUCCESS\"}]}");
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Endpoint deactivated.\"}]}");
				
				try {
					RetailSocketServlet.sendMessage("reload");
				} catch (IllegalValueException x) {
					x.printStackTrace();
				}
				
			} else if (command.equals("getPieChartDataForAdmin")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				CompanyBean selected_retailer = this.getSelectedCompany(session, logged_in_person); // assuming, for now, that the logged in person is a retail admin - add some permissions or something
				
				boolean is_doing_search = false;
				
				
				CompanyBean filter_mfg = null;
				if ((arg3 != null) && !arg3.isEmpty() && !arg3.equals("0")) {
					filter_mfg = CompanyBean.getCompany(Integer.parseInt(arg3));
					is_doing_search = true;
				}
				
				Date startFilter = null;
				if ((arg4 != null) && !arg4.isEmpty()) {
					try {
						startFilter = CUBean.getDateFromUserString(arg4);
						is_doing_search = true;
					} catch (java.text.ParseException x) { }
				}
				
				Date endFilter = null;
				if ((arg5 != null) && !arg5.isEmpty()) {
					try {
						endFilter = CUBean.getDateFromUserString(arg5);
						is_doing_search = true;
					} catch (java.text.ParseException x) { }
				}
				
				short statusFilter = 1; // default2
				if ((arg6 != null) && !arg6.isEmpty() && !arg6.equals("1")) {
					statusFilter = Short.parseShort(arg6);
					is_doing_search = true;
				}
				
				DepartmentBean filter_dept = null;
				if ((arg7 != null) && !arg7.isEmpty() && !arg7.equals("0")) {
					filter_dept = DepartmentBean.getDepartment(Integer.parseInt(arg7));
					is_doing_search = true;
				}
				
				int limit = 20;
				if ((arg8 != null) && !arg8.isEmpty()) {
					limit = Short.parseShort(arg8);
					is_doing_search = true;
				}
				
				
				
				HashMap sel_hash = new HashMap(7);
				Vector products_in_map = new Vector();
				
				if (arg2 != null && !arg2.isEmpty()) {
					is_doing_search = true;
				}
				
				System.out.println("is_doing_search map >" + is_doing_search);
				
				if (!is_doing_search) {
					// no search - grab the mapped products to display on the dashboard
					
					Vector selected_products = new Vector();
									
					PersonSettingsBean settings = PersonSettingsBean.getPersonSettings(logged_in_person, true);
					Vector pie_map = settings.getPieChartMap();

					Iterator pie_chart_map_itr = pie_map.iterator();
					while (pie_chart_map_itr.hasNext()) {
						AdminProductPieChartMap obj = (AdminProductPieChartMap)pie_chart_map_itr.next();
						Product sel_product = Product.getProduct(obj.getProductDbId());
						selected_products.addElement(sel_product);
						sel_hash.put(sel_product.getValue(), sel_product);
					}
					
					products_in_map = selected_products;
					
				} else {
					Iterator results_itr = Product.getProducts(filter_mfg, arg2, limit, statusFilter).iterator();
					while (results_itr.hasNext()) {
						Product product_x = (Product)results_itr.next();
						products_in_map.addElement(product_x);
					}
				}
				
				StringBuilder b = new StringBuilder();
				
				boolean needs_comma = false;
				
				
				b.append("{\"pieInfo\":[");
				
				needs_comma = false;
				b.append("{\"product\":[");
				Iterator itr = products_in_map.iterator();
				while (itr.hasNext()) {
					Product obj = (Product)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONPieChart(obj, selected_retailer, filter_dept, sel_hash.containsKey(obj.getValue()), startFilter, endFilter));
					needs_comma = true;
				}
				b.append("]}],");
				
				
				
				Iterator mfg_itr = null;
				try {
					mfg_itr = CompanyBean.getCompanies(CompanyBean.MANUFACTURER_COMPANY_TYPE).iterator();
				} catch (Exception x) {
					x.printStackTrace();
				}

				b.append("\"bu\":[");
				if (mfg_itr != null) {
					needs_comma = false;
					while (mfg_itr.hasNext()) {
						CompanyBean obj = (CompanyBean)mfg_itr.next();
						if (needs_comma) { b.append(","); }
						b.append(this.toJSONShort(obj));
						needs_comma = true;
					}
				}
				b.append("]}");
				
				
				writer.println(b.toString());
				
			} else if (command.equals("getInteractionDashboardDataForAdmin")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				CompanyBean selected_retailer = this.getSelectedCompany(session, logged_in_person); // assuming, for now, that the logged in person is a retail admin - add some permissions or something
				
				boolean is_doing_search = false;
				
				
				CompanyBean filter_mfg = null;
				if ((arg3 != null) && !arg3.isEmpty() && !arg3.equals("0")) {
					filter_mfg = CompanyBean.getCompany(Integer.parseInt(arg3));
					is_doing_search = true;
				}
				
				Date startFilter = null;
				if ((arg4 != null) && !arg4.isEmpty()) {
					try {
						startFilter = CUBean.getDateFromUserString(arg4);
						is_doing_search = true;
					} catch (java.text.ParseException x) { }
				}
				
				Date endFilter = null;
				if ((arg5 != null) && !arg5.isEmpty()) {
					try {
						endFilter = CUBean.getDateFromUserString(arg5);
						is_doing_search = true;
					} catch (java.text.ParseException x) { }
				}
				
				short statusFilter = 1; // default2
				if ((arg6 != null) && !arg6.isEmpty() && !arg6.equals("1")) {
					statusFilter = Short.parseShort(arg6);
					is_doing_search = true;
				}
				
				DepartmentBean filter_dept = null;
				if ((arg7 != null) && !arg7.isEmpty() && !arg7.equals("0")) {
					filter_dept = DepartmentBean.getDepartment(Integer.parseInt(arg7));
					is_doing_search = true;
				}
				
				int limit = 20;
				if ((arg8 != null) && !arg8.isEmpty()) {
					limit = Short.parseShort(arg8);
					is_doing_search = true;
				}
				
				
				
				HashMap sel_hash = new HashMap(7);
				Vector products_in_map = new Vector();
				
				if (arg2 != null && !arg2.isEmpty()) {
					is_doing_search = true;
				}
				
				System.out.println("is_doing_search map >" + is_doing_search);
				
				if (!is_doing_search) {
					// no search - grab the mapped products to display on the dashboard
					
					Vector selected_products = new Vector();
									
					PersonSettingsBean settings = PersonSettingsBean.getPersonSettings(logged_in_person, true);
					Vector pie_map = settings.getPieChartMap();

					Iterator pie_chart_map_itr = pie_map.iterator();
					while (pie_chart_map_itr.hasNext()) {
						AdminProductPieChartMap obj = (AdminProductPieChartMap)pie_chart_map_itr.next();
						Product sel_product = Product.getProduct(obj.getProductDbId());
						selected_products.addElement(sel_product);
						sel_hash.put(sel_product.getValue(), sel_product);
					}
					
					products_in_map = selected_products;
					
				} else {
					Iterator results_itr = Product.getProducts(filter_mfg, arg2, 0, statusFilter).iterator();
					System.out.println("results_itr.hasNext() >" + results_itr.hasNext());
					while (results_itr.hasNext()) {
						Product product_x = (Product)results_itr.next();
						products_in_map.addElement(product_x);
					}
				}
				
				StringBuilder b = new StringBuilder();
				
				boolean needs_comma = false;
				
				
				b.append("{\"interaction\":[");
				
				needs_comma = false;
				// get interactions for products in map
				Iterator itr = ProductInteraction.getInteractions(null, null, products_in_map, arg2, startFilter, endFilter, (short)0, limit).iterator();
				while (itr.hasNext()) {
					ProductInteraction obj = (ProductInteraction)itr.next();
					if (needs_comma) { b.append(","); }
					//b.append(this.toJSONPieChart(obj, selected_retailer, filter_dept, sel_hash.containsKey(obj.getValue()), startFilter, endFilter));
					b.append(this.toJSON(obj));
					needs_comma = true;
				}
				b.append("]}");
				
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("setShowProductInAdminPieChart")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				Product selected_product = null;
				if (arg2 != null) {
					selected_product = Product.getProduct(Long.parseLong(arg2));
				}
				
				boolean is_add = false;
				
				PersonSettingsBean settings = PersonSettingsBean.getPersonSettings(logged_in_person, true);
				
				AdminProductPieChartMap map_to_remove = null;
				Vector products_in_map = new Vector();
				Vector pie_mapping = settings.getPieChartMap();
				Iterator pie_chart_map_itr = pie_mapping.iterator();
				while (pie_chart_map_itr.hasNext()) {
					AdminProductPieChartMap obj = (AdminProductPieChartMap)pie_chart_map_itr.next();
					Product product_in_map = Product.getProduct(obj.getProductDbId());
					products_in_map.addElement(product_in_map);
					
					if (product_in_map.getId() == selected_product.getId()) {
						map_to_remove = obj;
					}
				}
				
				if (map_to_remove != null) {
					pie_mapping.removeElement(map_to_remove);
				}
				
				
				if (!products_in_map.contains(selected_product)) {
					AdminProductPieChartMap new_map = new AdminProductPieChartMap();
					new_map.setAdminId(logged_in_person.getId());
					new_map.setProductDbId(selected_product.getId());
					pie_mapping.addElement(new_map);
					is_add = true;
				}
				
				
				settings.setPieChartMap(pie_mapping);
				settings.save();
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				if (is_add) {
					b.append("{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(selected_product.getLabel()) + " added to dashboard.\"}");
				} else {
					b.append("{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(selected_product.getLabel()) + " removed from dashboard.\"}");
				}
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("doCompletionReport")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				String productId = arg2.substring(7);
				Product selected_product = Product.getProduct(Long.parseLong(productId));
				
				StringBuffer b = new StringBuffer();
				String excel_url = CUBean.getProperty("cu.webDomain") + "/resources/" + this.generateInteractionReport(selected_company, logged_in_person, selected_product);
				
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(excel_url) + "\"}");
				b.append("]}");
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getTopics")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				Vector vec = new Vector();
				
				Vector top_level_topics = new Vector();
				Vector groups = CourseGroupBean.getCourseGroups(selected_company);
				Iterator group_itr = groups.iterator();
				while (group_itr.hasNext()) {
					CourseGroupBean key = (CourseGroupBean)group_itr.next();
					if (key.isCourseLevel()) {
						top_level_topics.addElement(key);
					}
				}
				
				Iterator itr = top_level_topics.iterator();
				while (itr.hasNext()) {
					CourseGroupBean top = (CourseGroupBean)itr.next();
					vec.addElement(top);
					vec.addAll(top.getChildren());
				}
				
				
				boolean needs_comma = false;
				StringBuilder b = new StringBuilder();
				b.append("{\"topic\":[");
				itr = vec.iterator();
				while (itr.hasNext()) {
					CourseGroupBean group_obj = (CourseGroupBean)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSON(group_obj));
					needs_comma = true;

				}
				b.append("]}");
				
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getBusinessUnits")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				
				boolean needs_comma = false;
				StringBuilder b = new StringBuilder();
				b.append("{\"bu\":[");
				Iterator itr = CompanyBean.getCompanies(logged_in_person).iterator();
				while (itr.hasNext()) {
					CompanyBean companyObj = (CompanyBean)itr.next();
					if (companyObj.isActive()) {
						if (needs_comma) { b.append(","); }
						b.append(this.toJSONShort(companyObj));
						needs_comma = true;
					}
				}
				b.append("]}");
				writer.println(b.toString());
				
			} else if (command.equals("getBusinessUnit")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				
				if ((arg2 == null) || arg2.isEmpty() || arg2.equals("0")) {
					writer.println(this.toJSONAdminBusinessUnit(null));
				} else {
					// select this BU as well
					CompanyBean selected_bu = CompanyBean.getCompany(Integer.parseInt(arg2));
					logged_in_person.selectCompany(selected_bu);
					
					writer.println(this.toJSONAdminBusinessUnit(selected_bu));
				}
				
			} else if (command.equals("getAdminProducts")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_retailer = this.getSelectedCompany(session, logged_in_person);
				System.out.println("selected_company >" + selected_retailer.getLabel());
				
				DepartmentBean selected_store = null;
				if (arg2 != null && !arg2.isEmpty()) {
					selected_store = DepartmentBean.getDepartment(Integer.parseInt(arg2));
				}
				
				CompanyBean selected_mfg = null;
				if (arg3 != null && !arg3.isEmpty() && !arg3.equals("0")) {
					selected_mfg = CompanyBean.getCompany(Integer.parseInt(arg3));
				}
				
				int limit = 0;
				if (arg5 != null && !arg5.isEmpty()) {
					limit = Integer.parseInt(arg5);
				}
				
				short status = 0;
				if (arg6 != null && !arg6.isEmpty()) {
					status = Short.parseShort(arg6);
				}
				
				boolean needs_comma = false;
				StringBuilder b = new StringBuilder();
				b.append("{\"product\":[");
				Iterator itr = Product.getProducts(selected_retailer, selected_store, selected_mfg, arg4, limit, status).iterator();
				while (itr.hasNext()) {
					Product obj = (Product)itr.next();
					if (needs_comma) { b.append(","); }
					if (selected_store != null) {
						b.append(this.toJSON(obj, selected_store));
					} else {
						b.append(this.toJSON(obj, selected_retailer));
					}
					needs_comma = true;
				}
				b.append("],");
				
				
				
				Iterator mfg_itr = null;
				try {
					mfg_itr = CompanyBean.getCompanies(CompanyBean.MANUFACTURER_COMPANY_TYPE).iterator();
				} catch (Exception x) {
					x.printStackTrace();
				}

				b.append("\"bu\":[");
				if (mfg_itr != null) {
					needs_comma = false;
					while (mfg_itr.hasNext()) {
						CompanyBean obj = (CompanyBean)mfg_itr.next();
						if (needs_comma) { b.append(","); }
						b.append(this.toJSONShort(obj));
						needs_comma = true;
					}
				}
				b.append("]}");
				
				
				writer.println(b.toString());
				
					
			} else if (command.equals("getAdminProduct")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if ((arg2 == null) || arg2.isEmpty() || arg2.equals("0")) {
					writer.println(this.toJSONAdminProduct(selected_company, null));
				} else {
					writer.println(this.toJSONAdminProduct(selected_company, Product.getProduct(Integer.parseInt(arg2))));
				}
				
			} else if (command.equals("getAdminProductInteraction")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if ((arg3 == null) || arg3.isEmpty() || arg3.equals("0")) {
					writer.println(this.toJSONAdminInteraction(arg2, null));
				} else {
					writer.println(this.toJSONAdminInteraction(arg2, ProductInteraction.getInteraction(Long.parseLong(arg3))));
				}
				
			} else if (command.equals("getAdminProductInteractions")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				Product selected_product = Product.getProduct(Long.parseLong(arg2));
				
				// getProducts(CompanyBean _manfacturer, String _keyword, int _limit)
				
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"interaction\":[");
				Iterator itr = ProductInteraction.getLatestInteractions(selected_product).iterator();
				while (itr.hasNext()) {
					ProductInteraction obj = (ProductInteraction)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSON(obj));
					needs_comma = true;
				}
				b.append("]}");
				writer.println(b.toString());
					
			} else if (command.equals("getAdminProductInteractionHistory")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				ProductInteraction selected_product_interaction = ProductInteraction.getInteraction(Long.parseLong(arg2));
				
				// getProducts(CompanyBean _manfacturer, String _keyword, int _limit)
				
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"interaction\":[");
				Iterator itr = ProductInteraction.getInteractions(selected_product_interaction.getProduct(), selected_product_interaction.getUniqueIdentifier()).iterator();
				while (itr.hasNext()) {
					ProductInteraction obj = (ProductInteraction)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSON(obj));
					needs_comma = true;
				}
				b.append("]}");
				writer.println(b.toString());
					
			} else if (command.equals("saveAdminProduct")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				writer.println(this.saveProduct(logged_in_person, arg2, arg3));
				
			} else if (command.equals("saveAdminProductInteraction")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				//saveProductInteraction(UKOnlinePersonBean _mod, String _product_id_str, String _product_interaction_id_str, String _jsonBlob)
				
				writer.println(this.saveProductInteraction(logged_in_person, arg2, arg3, arg4));
				
			} else if (command.equals("getAdminEndpoints")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				//CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				CompanyBean selected_mfg = null;
				if (arg2 != null && !arg2.isEmpty() && !arg2.equals("0")) {
					selected_mfg = CompanyBean.getCompany(Integer.parseInt(arg2));
				}
				
				// getProducts(CompanyBean _manfacturer, String _keyword, int _limit)
				
				short device_state = 0;
				if (arg3 != null && !arg3.isEmpty()) {
					device_state = Short.parseShort(arg3);
				}
				
				int limit = 0;
				if (arg4 != null && !arg4.isEmpty()) {
					limit = Integer.parseInt(arg4);
				}
				
				boolean needs_comma = false;
				StringBuffer b = new StringBuffer();
				b.append("{\"endpoint\":[");
				Iterator itr = Endpoint.getEndpoints(arg5, limit, device_state, selected_mfg).iterator();
				while (itr.hasNext()) {
					Endpoint obj = (Endpoint)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSON(obj));
					needs_comma = true;
				}
				b.append("],");
				
				Iterator mfg_itr = null;
				try {
					mfg_itr = CompanyBean.getCompanies(CompanyBean.MANUFACTURER_COMPANY_TYPE).iterator();
					/*
					if (_obj != null) {
						productName = _obj.getNameString();
						shortName = productName;
						if (shortName.length() > 24) {
							shortName = shortName.substring(0, 23);
						}
						productId = _obj.getId();
						descStr = _obj.getDescription();
						model = _obj.getModelNumber();
						upc = _obj.getUPC();
						isActive = _obj.isActive();
						mfgId = _obj.getManufacturer().getId();
						mfg = _obj.getManufacturer().getLabel();
					}
					*/
				} catch (Exception x) {
					x.printStackTrace();
				}
				

				b.append("\"bu\":[");
				if (mfg_itr != null) {
					needs_comma = false;
					while (mfg_itr.hasNext()) {
						CompanyBean obj = (CompanyBean)mfg_itr.next();
						if (needs_comma) { b.append(","); }
						b.append(this.toJSONShort(obj));
						needs_comma = true;
					}
				}
				b.append("]}");
				
				writer.println(b.toString());
				
					
			} else if (command.equals("getAdminEndpoint")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if ((arg2 == null) || arg2.isEmpty() || arg2.equals("0")) {
					writer.println(this.toJSONAdminEndpoint(selected_company, null));
				} else {
					writer.println(this.toJSONAdminEndpoint(selected_company, Endpoint.getEndpoint(Long.parseLong(arg2))));
				}
					
			} else if (command.equals("toggleKAAEndpoint")) { // this is for manual testing
				
				UKOnlinePersonBean request_person = null;
				try {
					if (arg1 != null) {
						request_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				
				if (!this.kaa_initialized) {
					KAAAdminClientManager.init(KAA_SERVER_HOST);
					this.kaa_initialized = true;
				}
				
				boolean isActive = false;
				
				//updateServerProfile("RWHXlmqIHFgRMO7iADdfowuSOzg=", KAAAdminClientManager.SERVER_PROFILE_VERSION, isActive);
				//updateServerProfile("CdRn2qamOYr2kbtkKTigaXJIfsM=", KAAAdminClientManager.SERVER_PROFILE_VERSION, isActive);
				updateServerProfile("suLjPbCnjLMQC79YvJPdqBVsh8Y=", KAAAdminClientManager.SERVER_PROFILE_VERSION, isActive);
				
								
				Map<String, EndpointProfileDto> endpointProfiles = retrieveEndpointProfiles();
				//updateServerProfile(endpointProfiles.get("56e737e83596291dfcac70cf"));
				//updateServerProfile(endpointProfiles.get("58c97d0e38d20830ea7bcac9"));
				updateServerProfile(endpointProfiles.get("58e00bdb38d208100ee03e4b,"));
				
				
				endpointProfiles = retrieveEndpointProfiles();
				printEndpointProfiles(endpointProfiles);
				
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\"}]}");
				
				
			} else if (command.equals("toggleEndpoint")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if (!this.kaa_initialized) {
					KAAAdminClientManager.init(KAA_SERVER_HOST);
					this.kaa_initialized = true;
				}
				
				Endpoint endpoint_obj = Endpoint.getEndpoint(Long.parseLong(arg2));
				System.out.println("found EP to toggle >" + endpoint_obj.getLabel());
				boolean toggle = !endpoint_obj.isActivated();
				
				String message = null;
				if (toggle) {
					// see if I can activate...  - test not being able to do so
					
					// is the serial number for this endpoint "sold"
					
					ProductInteraction interaction_obj = ProductInteraction.getLastInteraction(null, endpoint_obj.getSerialNumber());
					System.out.println("1 - found interaction of type >" + interaction_obj.getInteractionTypeString());
					
					if (interaction_obj.getInteractionType() == ProductInteraction.SALE_TYPE ||
							interaction_obj.getInteractionType() == ProductInteraction.SALE_ACTIVATED_TYPE ||
							interaction_obj.getInteractionType() == ProductInteraction.DEACTIVATED_OTHER_TYPE) { // do I want DEACTIVATED_OTHER_TYPE here???
						if (interaction_obj.getInteractionType() == ProductInteraction.SALE_TYPE || interaction_obj.getInteractionType() == ProductInteraction.DEACTIVATED_OTHER_TYPE) {
							ProductInteraction interaction = ProductInteraction.getInteraction(logged_in_person, interaction_obj.getProduct(), endpoint_obj.getSerialNumber(), ProductInteraction.SALE_ACTIVATED_TYPE, true);
							System.out.println("Activation interaction created... >" + interaction.getLabel());
						} else {
							System.out.println("Already activated... >");
						}
					} else if (interaction_obj.getInteractionType() == ProductInteraction.INVENTORY_TYPE) {
						message = "Unable to activate.  This item should be in inventory.";
						toggle = false;
					} else {
						/* I have no idea why I was doing this - well, hmmm...
						if (endpoint_obj.getErrorMessage().equals("Unable to activate")) {
							message = "";
						} else {
							message = "Unable to activate";
						}
						*/
						message = "Unable to activate";
						toggle = false;
					}
			
				} else {
					try {
						ProductInteraction interaction_obj = ProductInteraction.getLastInteraction(null, endpoint_obj.getSerialNumber());
						System.out.println("2 - found interaction of type >" + interaction_obj.getInteractionTypeString());
						if (interaction_obj.getInteractionType() == ProductInteraction.SALE_ACTIVATED_TYPE) {
							ProductInteraction interaction = ProductInteraction.getInteraction(logged_in_person, interaction_obj.getProduct(), endpoint_obj.getSerialNumber(), ProductInteraction.DEACTIVATED_OTHER_TYPE, true);
							System.out.println("De-Activation interaction created... >" + interaction.getLabel());
						}
					} catch (ObjectNotFoundException x) {
						x.printStackTrace();
					}
				}
				
				endpoint_obj.setErrorMessage(message);
				endpoint_obj.setActive(toggle);
				endpoint_obj.save();
				
				
				//updateServerProfile(endpoint_obj.getEndpointHash(), KAAAdminClientManager.SERVER_PROFILE_VERSION, toggle, message);
				updateServerProfile(endpoint_obj.getEndpointHash(), KAAAdminClientManager.SERVER_PROFILE_VERSION, toggle);
				//updateServerProfile("suLjPbCnjLMQC79YvJPdqBVsh8Y=", KAAAdminClientManager.SERVER_PROFILE_VERSION, isActive);
				
								
				/*
				Map<String, EndpointProfileDto> endpointProfiles = retrieveEndpointProfiles();
				//updateServerProfile(endpointProfiles.get("56e737e83596291dfcac70cf"));
				//updateServerProfile(endpointProfiles.get("58c97d0e38d20830ea7bcac9"));
				updateServerProfile(endpointProfiles.get("58e00bdb38d208100ee03e4b,"));  // shrug
				
				//
				*/
				
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\"}]}");
				
				try {
					RetailSocketServlet.sendMessage("reload");
				} catch (IllegalValueException x) {
					x.printStackTrace();
				}
					
			} else if (command.equals("saveAdminEndpoint")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				writer.println(this.saveEndpoint(logged_in_person, arg2, arg3));
				
			} else if (command.equals("printAllKAAEndpointProfiles")) {
				
				UKOnlinePersonBean request_person = null;
				try {
					if (arg1 != null) {
						request_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				
				if (!this.kaa_initialized) {
					KAAAdminClientManager.init(KAA_SERVER_HOST);
					this.kaa_initialized = true;
				}
				
				
				Map<String, EndpointProfileDto> endpointProfiles = retrieveEndpointProfiles();
				if (endpointProfiles.isEmpty()) {
					LOG.info("There are no endpoints registered!");
					return;
				}
				printEndpointProfiles(endpointProfiles);
				
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\"}]}");
				
				
			} else if (command.equals("printActiveKAAEndpointProfiles")) {
				
				UKOnlinePersonBean request_person = null;
				try {
					if (arg1 != null) {
						request_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				
				if (!this.kaa_initialized) {
					KAAAdminClientManager.init(KAA_SERVER_HOST);
					this.kaa_initialized = true;
				}
				
				
				Map<String, EndpointProfileDto> endpointProfiles = retrieveActiveEndpointProfiles();
				if (!endpointProfiles.isEmpty()) {
					printEndpointProfiles(endpointProfiles);
				}
				
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\"}]}");
				
				
			} else if (command.equals("printInactiveKAAEndpointProfiles")) {
				
				UKOnlinePersonBean request_person = null;
				try {
					if (arg1 != null) {
						request_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				
				if (!this.kaa_initialized) {
					KAAAdminClientManager.init(KAA_SERVER_HOST);
					this.kaa_initialized = true;
				}
				
				
				Map<String, EndpointProfileDto> endpointProfiles = retrieveInactiveEndpointProfiles();
				if (!endpointProfiles.isEmpty()) {
					printEndpointProfiles(endpointProfiles);
				}
				
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\"}]}");
				
				
			} else if (command.equals("userSearch")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				
				int active_filter = Integer.parseInt(arg3);
				
				int limit = 0;
				if (arg4 != null && !arg4.isEmpty()) {
					limit = Integer.parseInt(arg4);
				}

				String sort = null;
				if (arg5 != null && !arg5.isEmpty()) {
					sort = arg5;
				}

				int course_id = 0;
				if (arg6 != null && !arg6.isEmpty()) {
					course_id = Integer.parseInt(arg6);
				}
				
				StringBuffer b = new StringBuffer();
				b.append("{\"user\":[");
				
				Vector vec = new Vector();
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if (arg2 != null) {
					if (arg2.length() > 2) {

						session.setAttribute("keyword", arg2.replaceAll("\\\\", ""));

						String[] param_arr = arg2.split(" ");

						//getPersonsByKeyword(CompanyBean _company, String _keyword, UKOnlinePersonBean _logged_in_person, int _limit)

						for (int i = 0; i < param_arr.length; i++) {
							Vector new_vec = new Vector();
							Vector temp_vec = null;
							if (sort == null) {
								temp_vec = UKOnlinePersonBean.getPersonsByKeyword(selected_company, JSONObject.escape(param_arr[i]), logged_in_person, limit, 0, active_filter);
							} else {
								temp_vec = UKOnlinePersonBean.getPersonsByKeyword(selected_company, JSONObject.escape(param_arr[i]), logged_in_person, limit, 0, active_filter);
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

						//Iterator itr = AnokaPersonBean.getPersonsByKeyword(selected_company, JSONObject.escape(parameter), logged_in_person, limit, null, poc_trained_only).iterator();
						
					} else if (arg2.length() == 1) {
						vec = UKOnlinePersonBean.getPersonsByKeyword(selected_company, JSONObject.escape(arg2), logged_in_person, limit, 0, active_filter);
					}
				}
				
				Iterator itr = vec.iterator();
				while (itr.hasNext()) {
					UKOnlinePersonBean person = (UKOnlinePersonBean)itr.next();
					b.append(this.toJSONShort(person));
					b.append(itr.hasNext() ? "," : "");
				}
				
				b.append("]}");
				
				System.out.println("retStr >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getDepartment")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				System.out.println("selected_company >" + selected_company.getLabel());
				
				if ((arg2 == null) || arg2.isEmpty() || arg2.equals("0")) {
					writer.println(this.toJSONAdminDepartment(selected_company, null));
				} else {
					writer.println(this.toJSONAdminDepartment(selected_company, DepartmentBean.getDepartment(Integer.parseInt(arg2))));
				}
					
			} else if (command.equals("getDepartmentType")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if ((arg2 == null) || arg2.isEmpty() || arg2.equals("0")) {
					writer.println(this.toJSONAdminDepartmentType(selected_company, null));
				} else {
					writer.println(this.toJSONAdminDepartmentType(selected_company, DepartmentTypeBean.getDepartmentType(Integer.parseInt(arg2))));
				}
				
			} else if (command.equals("getJobTitle")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if ((arg2 == null) || arg2.isEmpty() || arg2.equals("0")) {
					writer.println(this.toJSONAdminTitle(logged_in_person, selected_company, null));
				} else {
					writer.println(this.toJSONAdminTitle(logged_in_person, selected_company, PersonTitleBean.getPersonTitle(Integer.parseInt(arg2))));
				}
				
			} else if (command.equals("getGroup")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if ((arg2 == null) || arg2.isEmpty() || arg2.equals("0")) {
					writer.println(this.toJSONAdminGroup(selected_company, null));
				} else {
					writer.println(this.toJSONAdminGroup(selected_company, PersonGroupBean.getPersonGroup(Integer.parseInt(arg2))));
				}
				
			} else if (command.equals("getAudience")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if ((arg2 == null) || arg2.isEmpty() || arg2.equals("0")) {
					writer.println(this.toJSONAdminAudience(selected_company, null));
				} else {
					writer.println(this.toJSONAdminAudience(selected_company, AudienceBean.getAudience(Integer.parseInt(arg2))));
				}
				
			} else if (command.equals("saveAdminBusinessUnit")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				writer.println(this.saveBusinessUnit(logged_in_person, selected_company, arg2, arg3));
				
			} else if (command.equals("saveAdminDepartment")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				writer.println(this.saveDepartment(selected_company, arg2, arg3));
				
			} else if (command.equals("saveAdminDepartmentType")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				writer.println(this.saveDepartmentType(selected_company, arg2, arg3));
				
			} else if (command.equals("saveAdminTitle")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				writer.println(this.saveTitle(selected_company, arg2, arg3));
				
			} else if (command.equals("saveAdminGroup")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				writer.println(this.saveGroup(selected_company, arg2, arg3));
				
			} else if (command.equals("saveAdminAudience")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				writer.println(this.saveAudience(selected_company, logged_in_person, arg2, arg3));
				
			} else if (command.equals("getAdminIndex")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if (logged_in_person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) ||
						logged_in_person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) ||
						logged_in_person.hasRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME)) {
					
				} else {
					throw new IllegalValueException("Unable to access.  You do not have the proper role.");
				}
				
				
				
				Date now = new Date();
				
				StringBuffer b = new StringBuffer();
				b.append("{\"selectedBU\":\"" + JSONObject.escape(selected_company.getLabel()) + "\",");
				b.append("\"active\":\"" + SessionCounter.getNumStudentSessions() + "\",");
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
				
			} else if (command.equals("getAdminUserProfileDropdownInfo")) {
				
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				
				StringBuilder b = new StringBuilder();
				
				boolean needs_comma = false;
				
				b.append("{\"dropdownInfo\": {");
				
				needs_comma = true;
				b.append("\"department\":[");
				b.append("{\"name\":\"-- SELECT A DEPARTMENT --\",\"id\":\"0\"}");
				Iterator itr = DepartmentBean.getActiveDepartments(selected_company).iterator();
				while (itr.hasNext()) {
					DepartmentBean obj = (DepartmentBean)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj));
					needs_comma = true;
				}
				b.append("],");
				
				
				needs_comma = true;
				b.append("\"title\":[");
				b.append("{\"name\":\"-- SELECT A JOB TITLE --\",\"id\":\"0\"}");
				itr = PersonTitleBean.getPersonTitles(selected_company).iterator();
				while (itr.hasNext()) {
					PersonTitleBean obj = (PersonTitleBean)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj));
					needs_comma = true;
				}
				b.append("],");
				
				
				needs_comma = false;
				b.append("\"group\":[");
				itr = PersonGroupBean.getPersonGroups(selected_company).iterator();
				while (itr.hasNext()) {
					PersonGroupBean obj = (PersonGroupBean)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj));
					needs_comma = true;
				}
				b.append("],");
				
				
				needs_comma = false;
				b.append("\"audience\":[");
				itr = AudienceBean.getAudiences(selected_company).iterator();
				while (itr.hasNext()) {
					AudienceBean obj = (AudienceBean)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj));
					needs_comma = true;
				}
				b.append("]");
				
				
				
				b.append("}}");
				
				
				//System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getAdminUserProfile")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				
				if (arg2.equals("0")) {
					writer.println(this.toJSONAdminUserProfile(null));
				} else {
					writer.println(this.toJSONAdminUserProfile((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2))));
				}
				
			} else if (command.equals("getAdminUserProfileRolesDropdownInfo")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				
				StringBuilder b = new StringBuilder();
				
				boolean needs_comma = false;
				
				b.append("{\"dropdownInfo\":{");
				b.append("\"role\":[");
				Iterator itr = UKOnlineRoleBean.getEssentialAssignableRoles(logged_in_person).iterator();
				while (itr.hasNext()) {
					RoleBean obj = (RoleBean)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj));
					needs_comma = true;
				}
				b.append("]}}");
				
				//System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getAdminResources")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				int topic_id = -1;
				short status = Short.parseShort(arg3);
				
				String json_str = this.getResourcesInTopicHierarchy(logged_in_person, ResourceBean.getResources(selected_company, null, status, arg2), topic_id, true);
				
				//System.out.println("json_str >" + json_str);
				if (json_str.isEmpty()) {
					writer.println("{\"topic\":[],\"ungrouped\":[]}");
				} else {
					writer.println(json_str);
				}
				
			} else if (command.equals("getAdminResource")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if ((arg2 == null) || arg2.isEmpty() || arg2.equals("0")) {
					writer.println(this.toJSONAdminResource(selected_company, null));
				} else {
					writer.println(this.toJSONAdminResource(selected_company, ResourceBean.getResource(Integer.parseInt(arg2))));
				}
				
			} else if (command.equals("saveAdminUserProfile")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				if (arg2.equals("0")) {
					UKOnlinePersonBean person_obj = new UKOnlinePersonBean();
					person_obj.setDepartment(selected_company.getDefaultDepartment());
					person_obj.setTitle(PersonTitleBean.getDefaultTitle(selected_company));
					person_obj.setGroup(PersonGroupBean.getDefaultPersonGroup(selected_company));
					writer.println(this.savePerson(logged_in_person, person_obj, arg3));
				} else {
					writer.println(this.savePerson(logged_in_person, (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2)), arg3));
				}
				
			} else if (command.equals("saveAdminUserRoles")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				
				writer.println(this.savePersonRoles(logged_in_person, (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2)), arg3));
				
			} else if (command.equals("simAdminUser")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				
				int sim_person_id = -1;
				if (arg2 != null) {
					sim_person_id = Integer.parseInt(arg2);
				}
				

				boolean has_role_for_this = logged_in_person.hasRole(UKOnlineRoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) || 
								logged_in_person.hasRole(UKOnlineRoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME);
				
				
				String sessionId = session.getId();

				UKOnlinePersonBean sim_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(sim_person_id);
				if (has_role_for_this) {
					
					RetailServlet.sim_hash.put(logged_in_person, sim_person);
					RetailServlet.sim_hash_session.put(sessionId, logged_in_person);
					
				} else {
					throw new IllegalValueException("Unable to simulate " + sim_person.getLabel() + ".  You do not have rights to complete this operation.");
				}
				
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(sim_person.getLabel()) + " simulated.\",\"token\":\"" + JSONObject.escape(sessionId) + "\"}");
				b.append("]}");
				writer.println(b.toString());
				
				
			} else if (command.equals("authSimToken")) {
				
				String sessionToken = arg1;
				
				UKOnlinePersonBean simulator = RetailServlet.sim_hash_session.get(sessionToken);
				
				System.out.println("found simulator >" + simulator.getLabel());
				
				
				if (simulator.hasRole(UKOnlineRoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME)
						|| simulator.hasRole(UKOnlineRoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME)) {
					// ok
				} else {
					throw new IllegalValueException("Insufficient rights to simulate.");
				}

				
				//UKOnlinePersonBean sim_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPersonByEmployeeId(sim_emp_id);
				UKOnlinePersonBean sim_person = RetailServlet.sim_hash.remove(simulator);
				sim_person.invalidateEnrollments();
				
				/*
				StringBuffer b = new StringBuffer();
				b.append("{\"auth\":[");
				b.append(this.toJSON(sim_person, false, session.getId()));
				b.append("]}");
				*/
				
				CompanyBean selected_bu = this.getSelectedCompany(session, sim_person);
				StringBuffer b = new StringBuffer();
				b.append("{\"auth\":[");
				b.append(this.toJSON(selected_bu, sim_person));
				b.append("]}");
				
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("endSimToken")) {
				
				String sessionToken = arg1;
				
				UKOnlinePersonBean simulator = RetailServlet.sim_hash_session.remove(sessionToken);
				
				if (simulator == null) {
					throw new IllegalValueException("Unable to end simulation.  Please log out manually.");
				}
				System.out.println("found simulator >" + simulator.getLabel());
				
				
				if (simulator.hasRole(UKOnlineRoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME)
						|| simulator.hasRole(UKOnlineRoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME)) {
					// ok
				} else {
					throw new IllegalValueException("Insufficient rights to simulate.");
				}

				
				simulator.invalidateEnrollments();
				
				
				/*
				StringBuffer b = new StringBuffer();
				b.append("{\"auth\":[");
				b.append(this.toJSON(simulator, false, null));
				b.append("]}");
				*/
				
				CompanyBean selected_bu = this.getSelectedCompany(session, simulator);
				StringBuffer b = new StringBuffer();
				b.append("{\"auth\":[");
				b.append(this.toJSON(selected_bu, simulator));
				b.append("]}");
				
				System.out.println("json_str >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("getReportDropdowns")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					if (arg1 != null) {
						logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				StringBuilder b = new StringBuilder();
				
				boolean needs_comma = false;
				
				b.append("{\"reportStuff\": {");
				
				IoTLOKReportTemplateBean report_template = null;
				if ((arg2 != null) && !arg2.isEmpty() && !arg2.equals("0")) {
					report_template = (IoTLOKReportTemplateBean)IoTLOKReportTemplateBean.getTemplate(Integer.parseInt(arg2));
				} else {
					report_template = new IoTLOKReportTemplateBean();
				}
				
				b.append("\"template\":[");
				b.append(this.toJSON(report_template, false));
				b.append("],");
				
				
				b.append("\"report\":[");
				//b.append("{\"name\":\"-- SELECT A REPORT --\",\"id\":\"0\"}");
				//b.append(this.toJSONDropdown("Course Report", 1, (report_template.getReportType() == (short)1) )); b.append(",");
				//b.append(this.toJSONDropdown("Course History Report", 2, (report_template.getReportType() == (short)2))); b.append(",");
				b.append(this.toJSONDropdown("Product Report", 12, (report_template.getReportType() == (short)12))); b.append(",");
				b.append(this.toJSONDropdown("Interaction Report", 16, (report_template.getReportType() == (short)16))); b.append(",");
				b.append(this.toJSONDropdown("Serial Number / Shelf Time Report", 14, (report_template.getReportType() == (short)14))); b.append(",");
				//b.append(this.toJSONDropdown("Serial Number Report History Report", 15, (report_template.getReportType() == (short)15))); b.append(",");
				b.append(this.toJSONDropdown("Activation Report", 13, (report_template.getReportType() == (short)13))); b.append(",");
				b.append(this.toJSONDropdown("User Report", 6, (report_template.getReportType() == (short)6)));
				b.append("],");
				
				/*
				b.append("\"status\":[");
				b.append(this.toJSONDropdown("-- ANY STATUS --", 0, false )); b.append(",");
				b.append(this.toJSONDropdown("Completed", 1, (report_template.getEnrollmentStatus().equals("1")) )); b.append(",");
				b.append(this.toJSONDropdown("Enrolled / Not Started", 2, (report_template.getEnrollmentStatus().equals("2")))); b.append(",");
				b.append(this.toJSONDropdown("Started / In Progress", 3, (report_template.getEnrollmentStatus().equals("3")))); b.append(",");
				b.append(this.toJSONDropdown("Wait List", 4, (report_template.getEnrollmentStatus().equals("4")))); b.append(",");
				b.append(this.toJSONDropdown("Pending", 5, (report_template.getEnrollmentStatus().equals("5")))); b.append(",");
				b.append(this.toJSONDropdown("Not Enrolled", 6, (report_template.getEnrollmentStatus().equals("6")))); b.append(",");
				b.append(this.toJSONDropdown("Not Completed", 10, (report_template.getEnrollmentStatus().equals("10"))));
				b.append("],");
				*/
				
				/*
				
    public static final short ASN_TYPE = 7;
	
	public static final short INVENTORY_TYPE = 1;
    public static final short SALE_TYPE = 2;
    public static final short SALE_ACTIVATED_TYPE = 3;
    public static final short RETURN_TYPE = 4;
    public static final short DEACTIVATED_OTHER_TYPE = 5;
	
    public static final short DEACTIVATED_CHARGEBACK_TYPE = 6;
				*/
				
				b.append("\"status\":[");
				b.append(this.toJSONDropdown("-- ANY STATUS --", 0, false )); b.append(",");
				b.append(this.toJSONDropdown("ASN", 1, (report_template.getProductStatus().equals("1")) )); b.append(",");
				b.append(this.toJSONDropdown("Inventory", 2, (report_template.getProductStatus().equals("2")))); b.append(",");
				b.append(this.toJSONDropdown("Sold (Not Activated)", 3, (report_template.getProductStatus().equals("3")))); b.append(",");
				b.append(this.toJSONDropdown("Sold (Activated)", 4, (report_template.getProductStatus().equals("4")))); b.append(",");
				b.append(this.toJSONDropdown("Returned", 5, (report_template.getProductStatus().equals("5")))); b.append(",");
				b.append(this.toJSONDropdown("Deactivated (Chargeback)", 6, (report_template.getProductStatus().equals("6")))); b.append(",");
				b.append(this.toJSONDropdown("Deactivated (Other)", 10, (report_template.getProductStatus().equals("10"))));
				b.append("],");
				
				//Vector template_courses = report_template.getCourses();
				needs_comma = true;
				b.append("\"course\":[");
				b.append("{\"name\":\"-- ALL COURSES --\",\"id\":\"0\"}");
				b.append("],");
				
				//Vector template_programs = report_template.getCertifications();
				needs_comma = true;
				b.append("\"program\":[");
				b.append("{\"name\":\"-- ALL PROGRAMS --\",\"id\":\"0\"}");
				b.append("],");
				
				needs_comma = true;
				b.append("\"certification\":[");
				b.append("{\"name\":\"-- ALL CERTIFICATIONS --\",\"id\":\"0\"}");
				b.append("],");
				
				Vector template_departments = report_template.getDepartments();
				needs_comma = true;
				b.append("\"department\":[");
				b.append("{\"name\":\"-- ALL STORES --\",\"id\":\"0\"}");
				//Iterator itr = DepartmentBean.getActiveDepartments(selected_company).iterator();
				Iterator itr = DepartmentBean.getDepartments(selected_company).iterator();
				while (itr.hasNext()) {
					DepartmentBean obj = (DepartmentBean)itr.next();
					if (obj.isActive() && !obj.getLabel().isEmpty()) {
						if (needs_comma) { b.append(","); }
						b.append(this.toJSONShort(obj, template_departments.contains(obj)));
						needs_comma = true;
					}
				}
				b.append("],");
				
				Vector template_titles = report_template.getTitles();
				needs_comma = true;
				b.append("\"title\":[");
				b.append("{\"name\":\"-- ALL JOB TITLES --\",\"id\":\"0\"}");
				itr = PersonTitleBean.getPersonTitles(selected_company).iterator();
				while (itr.hasNext()) {
					PersonTitleBean obj = (PersonTitleBean)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj, template_titles.contains(obj)));
					needs_comma = true;
				}
				b.append("],");
				
				Vector template_groups = report_template.getPersonGroups();
				needs_comma = true;
				b.append("\"group\":[");
				b.append("{\"name\":\"-- ALL GROUPS --\",\"id\":\"0\"}");
				itr = PersonGroupBean.getPersonGroups(selected_company).iterator();
				while (itr.hasNext()) {
					PersonGroupBean obj = (PersonGroupBean)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj, template_groups.contains(obj)));
					needs_comma = true;
				}
				b.append("],");
				
				Vector template_audiences = report_template.getAudiences();
				needs_comma = true;
				b.append("\"audience\":[");
				b.append("{\"name\":\"-- ALL AUDIENCES --\",\"id\":\"0\"}");
				itr = AudienceBean.getAudiences(selected_company).iterator();
				while (itr.hasNext()) {
					AudienceBean obj = (AudienceBean)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj, template_audiences.contains(obj)));
					needs_comma = true;
				}
				b.append("],");
				
				
				Vector template_products = new Vector(); // this needs to be updated
				needs_comma = true;
				b.append("\"product\":[");
				b.append("{\"name\":\"-- ALL PRODUCTS --\",\"id\":\"0\"}");
				//itr = AudienceBean.getAudiences(selected_company).iterator();
				//getProducts(CompanyBean _retailer, DepartmentBean _store, CompanyBean _manfacturer, String _keyword, int _limit, short _status)
				itr = Product.getProducts(null, null, null, null, -1, (short)1).iterator();
				while (itr.hasNext()) {
					Product obj = (Product)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj, template_audiences.contains(obj)));
					needs_comma = true;
				}
				b.append("],");
				
				
				
				
				
				
				Vector template_mfgs = new Vector(); // this needs to be updated
				needs_comma = true;
				b.append("\"mfg\":[");
				b.append("{\"name\":\"-- ALL MFGS --\",\"id\":\"0\"}");
				//itr = AudienceBean.getAudiences(selected_company).iterator();
				//getProducts(CompanyBean _retailer, DepartmentBean _store, CompanyBean _manfacturer, String _keyword, int _limit, short _status)
				itr = CompanyBean.getCompanies(CompanyBean.MANUFACTURER_COMPANY_TYPE).iterator();
				while (itr.hasNext()) {
					CompanyBean obj = (CompanyBean)itr.next();
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj, template_audiences.contains(obj)));
					needs_comma = true;
				}
				b.append("],");
				
				
				
				
				
				
				b.append("\"reportTemplate\":[");
				itr = ReportTemplateBean.getTemplates(logged_in_person).iterator();
				while (itr.hasNext()) {
					ReportTemplateBean obj = (ReportTemplateBean)itr.next();
					b.append(this.toJSONDropdown(obj.getLabel(), obj.getId(), false));
					if (itr.hasNext()) { b.append(","); }
				}
				b.append("]");
					
				
				b.append("}}");
				
				writer.println(b.toString());
				
			} else if (command.equals("getReportResults")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				IoTLOKReportTemplateBean report_template = RetailServlet.template_hash.get(logged_in_person);
				if (report_template == null) {
					System.out.println("report template not found");
					report_template = new IoTLOKReportTemplateBean();
					session.setAttribute("adminReportTemplate", report_template);
				} else {
					System.out.println("report template found");
				}
				
				//writer.println("{\"message\":[{\"type\":\"SUCCESS\"}]}");
				writer.println(this.toJSON(report_template, true));
				
			} else if (command.equals("processReportFilterTemplate")) {
				
				UKOnlinePersonBean logged_in_person = null;
				if (arg1 != null) {
					logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg1));
				} else {
					throw new IllegalValueException("Logged in person not specified.");
				}
				CompanyBean selected_company = this.getSelectedCompany(session, logged_in_person);
				
				boolean save_only = (arg4 != null);
				IoTLOKReportTemplateBean report_template = this.processReportTemplate(logged_in_person, selected_company, arg2, arg3, save_only);
				
				RetailServlet.template_hash.put(logged_in_person, report_template);
				
				if (save_only) {
					StringBuffer b = new StringBuffer();
					b.append("{\"reportTemplate\":[");
					Iterator itr = ReportTemplateBean.getTemplates(logged_in_person).iterator();
					while (itr.hasNext()) {
						ReportTemplateBean obj = (ReportTemplateBean)itr.next();
						//b.append(this.toJSONShort(obj.getLabel(), obj.getValue()));
						b.append(this.toJSONDropdown(obj.getLabel(), obj.getId(), false));
						if (itr.hasNext()) { b.append(","); }
					}
					b.append("]}");
					writer.println(b.toString());
				} else {
					writer.println("{\"message\":[{\"type\":\"SUCCESS\"}]}");
				}
				
			} else if (command.equals("newAccountConfirmEmail")) {
				
				String email_address = arg1;
				
				String host_str = CUBean.getProperty("cu.host");
				if (CUBean.getProperty("cu.port").equals("8080")) {
					host_str += (":" + CUBean.getProperty("cu.port"));
				}
				
				//HashMap<UKOnlinePersonBean,HttpSession> session_hash = new HashMap<UKOnlinePersonBean,HttpSession>(11);
				
				String gen_pass = PasswordGenerator.getPassword(3);
				confirm_email_hash.put(gen_pass, email_address);
				
				String subject = "<a href=\"" + host_str + "/#newAccountConfirm/" + gen_pass + "\">Click to verify your email address.</a>";
				
				String html_message = this.getEmailHTMLString("Universal Knowledge - Email Confirmation", subject, null, "");
				CUBean.sendEmailWithMeetingStageTest(email_address, CUBean.getProperty("cu.adminEmail"), "Universal Knowledge", null, html_message);
				CUBean.sendEmailWithMeetingStageTest("marlo@badiyan.com", CUBean.getProperty("cu.adminEmail"), "Universal Knowledge", null, html_message);
				
				String message = "An email has been sent to " + email_address + ".  Please click the link in the email to confirm this address.";
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[");
				b.append("{\"type\":\"SUCCESS\",\"text\":\"" + JSONObject.escape(message) + "\"}");
				b.append("]}");
				writer.println(b.toString());
				
			} else {
				throw new IllegalValueException("Command not implemented >" + command);
			}
		} catch (Exception x) {
			x.printStackTrace();
			
			StringBuffer b = new StringBuffer();
			b.append("{\"message\":[");
			b.append("{\"type\":\"ERROR\"," +
					"\"heading\":\"Oh Snap!\"," +
					"\"text\":\"" + JSONObject.escape(x.getMessage()) + "\"" +
					"}");
			b.append("]}");
			
			_response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			writer.println(b.toString());
		} finally {			
			writer.close();
		}
	}
	
	private String
	getProductBlob(String _pos_upc) throws IOException {
					
		URL url = url = new URL("https://api.upcitemdb.com/prod/trial/lookup?upc=" + _pos_upc);

		String line;
		String result = "";

		// try opening the URL
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null) {
		   result += line;
		}
		rd.close();

		return result;
	}
	
	/*
	private CompanyBean
	getSelectedCompany(HttpSession _session, UKOnlinePersonBean _logged_in_person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		//System.out.println("getSelectedCompany invoked");
		CompanyBean selected_company = (CompanyBean)_session.getAttribute("userCompany");
		//System.out.println("1 >" + _logged_in_person);
		//System.out.println("2 >" + selected_company);
		
		if ((selected_company == null || selected_company.isNew()) && (_logged_in_person != null) && (_logged_in_person.hasDepartment())) {
			// try to get the company from the logged in person
			selected_company = _logged_in_person.getDepartment().getCompany();
			//System.out.println("3 >" + selected_company.getLabel());
			_session.setAttribute("userCompany", selected_company);
		}
		if  (selected_company == null || selected_company.isNew()) {
			selected_company = CompanyBean.getCompany(1);
			//System.out.println("4 >" + selected_company.getLabel());
			_session.setAttribute("userCompany", selected_company);
		}
		return selected_company;
	}
	*/
	
	private CompanyBean
	getSelectedCompany(HttpSession _session, UKOnlinePersonBean _logged_in_person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		CompanyBean selected_company = (CompanyBean)_session.getAttribute("userCompany");
		
		if ((selected_company == null || selected_company.isNew()) && (_logged_in_person != null)) {
			// try to get the company from the logged in person
			
			try {
				selected_company = _logged_in_person.getSelectedCompany();
				
			} catch (Exception x) {
				x.printStackTrace();
			}
			if (selected_company == null) {
				selected_company = _logged_in_person.getDepartment().getCompany();
			}
			//System.out.println("3 >" + selected_company.getLabel());
			_session.setAttribute("userCompany", selected_company);
		}
		if  (selected_company == null || selected_company.isNew()) {
			selected_company = CompanyBean.getCompany(1);
			//System.out.println("4 >" + selected_company.getLabel());
			_session.setAttribute("userCompany", selected_company);
		}
		return selected_company;
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
		" \"id\":\"" + _person.getId() + "\"}";
	}
	
	private String toJSON(CompanyBean _selected_bu, UKOnlinePersonBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
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
		
		//CompanyBean selected_bu = this.getSelectedCompany(session, _obj);
		
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"empId\":\"" + _obj.getEmployeeNumberString() + "\",");
		b.append("\"lastLogIn\":\"" + JSONObject.escape(_obj.getLastLogInDateString()) + "\",");
		b.append("\"role\":\"" + JSONObject.escape(_obj.getRolesString()) + "\",");
		boolean isAdmin = _obj.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) || _obj.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) || _obj.hasRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME);
		b.append("\"isAdmin\":" + isAdmin + ",");
		b.append("\"location\":\"" + JSONObject.escape(_obj.getDepartmentNameString()) + "\",");
		b.append("\"title\":\"" + JSONObject.escape(_obj.getJobTitleString()) + "\",");
		b.append("\"bu\":\"" + JSONObject.escape(_selected_bu.getLabel()) + "\",");
		b.append("\"isActive\":" + _obj.isActive() + ",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}

    // SQL

    /*<table name="ENDPOINT_DB" idMethod="native">
	<column name="ENDPOINT_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	
	<column name="UNIQUE_ID" required="false" size="20" type="VARCHAR"/>
	<!-- serial number bar code typically - same as unique id  -->
	<column name="PROFILE_ID" required="true" size="30" type="VARCHAR"/>
	<column name="ENDPOINT_HASH" required="true" size="40" type="VARCHAR"/>
	
	<column name="DEVICE_STATE" type="SMALLINT" default="0"/>
	<column name="APPLICATION_ID" required="true" size="60" type="VARCHAR"/>
	<column name="SDK_TOKEN" required="true" size="60" type="VARCHAR"/>
	
	<column name="REGISTRATION_DATE" required="true" type="DATE"/>

</table>

     */
	
	private String toJSON(Endpoint _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		ProductInteraction lastInteraction = null;
		String serialNumberStr = _obj.getSerialNumber();
		if (serialNumberStr.isEmpty()) {
			serialNumberStr = "[Serial Number Not Assigned]";
		} else {
			lastInteraction = ProductInteraction.getLastInteraction(null, serialNumberStr); // what if serial number isn't distinct??? - COULD BE BIG PROBLEM!!!!
		}
		
		
		StringBuffer b = new StringBuffer();
		b.append("{\"profileId\":\"" + JSONObject.escape(_obj.getProfileID()) + "\",");
		b.append("\"hash\":\"" + JSONObject.escape(_obj.getEndpointHash()) + "\",");
		b.append("\"serialNumber\":\"" + JSONObject.escape(serialNumberStr) + "\","); // magic sauce needed
		if (lastInteraction != null) {
			b.append("\"lastInteractionType\":\"" + lastInteraction.getInteractionTypeString() + "\",");
			b.append("\"store\":\"" + JSONObject.escape(lastInteraction.getStore().getLabel()) + "\",");
			b.append("\"person\":\"" + JSONObject.escape(lastInteraction.getInteractionPerson().getLabel()) + "\",");
			String productLabel = lastInteraction.getProduct().getLabel();
			if (productLabel.length() > 100) {
				productLabel = productLabel.substring(0, 98);
			}
			b.append("\"productLabel\":\"" + JSONObject.escape(productLabel) + "\",");
		}
		b.append("\"activated\":" + _obj.isActivated() + ",");
		b.append("\"appId\":\"" + JSONObject.escape(_obj.getApplicationID()) + "\",");
		b.append("\"sdkToken\":\"" + JSONObject.escape(_obj.getSDKToken()) + "\",");
		b.append("\"registrationDate\":" + _obj.getRegistrationDateUnixTimestamp() + ",");
		b.append("\"error\":\"" + JSONObject.escape(_obj.getErrorMessage()) + "\",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSON(Product _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_obj.getDescription()) + "\",");
		b.append("\"model\":\"" + JSONObject.escape(_obj.getModelNumber()) + "\",");
		b.append("\"mfgId\":" + _obj.getManufacturer().getId() + ",");
		b.append("\"mfg\":\"" + JSONObject.escape(_obj.getManufacturer().getLabel()) + "\",");
		b.append("\"upc\":\"" + JSONObject.escape(_obj.getUPC()) + "\",");
		b.append("\"isActive\":" + _obj.isActive() + ",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSON(Product _obj, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		// store is being specified, include interaction data for the given product
		
		int num_inventory = 0;
		int num_sale = 0;
		int num_activated = 0;
		int num_return = 0;
		int num_other = 0;
		
		Iterator itr = ProductInteraction.getInteractions(_obj, _store).iterator();
		while (itr.hasNext()) {
			ProductInteraction interaction_obj = (ProductInteraction)itr.next();
			switch (interaction_obj.getInteractionType()) {
				case ProductInteraction.INVENTORY_TYPE : num_inventory++; break;
				case ProductInteraction.SALE_TYPE : num_sale++; break;
				case ProductInteraction.SALE_ACTIVATED_TYPE : num_activated++; break;
				case ProductInteraction.RETURN_TYPE : num_return++; break;
				case ProductInteraction.DEACTIVATED_OTHER_TYPE : num_other++; break;
			}
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_obj.getDescription()) + "\",");
		b.append("\"model\":\"" + JSONObject.escape(_obj.getModelNumber()) + "\",");
		b.append("\"mfgId\":" + _obj.getManufacturer().getId() + ",");
		b.append("\"mfg\":\"" + JSONObject.escape(_obj.getManufacturer().getLabel()) + "\",");
		b.append("\"upc\":\"" + JSONObject.escape(_obj.getUPC()) + "\",");
		b.append("\"isActive\":" + _obj.isActive() + ",");
		b.append("\"numInventory\":" + num_inventory + ",");
		b.append("\"numSale\":" + num_sale + ",");
		b.append("\"numActivated\":" + num_activated + ",");
		b.append("\"numReturn\":" + num_return + ",");
		b.append("\"numOther\":" + num_other + ",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSON(Product _obj, CompanyBean _retailer) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		// retailer is being specified, include interaction data for the given product
		
		int num_inventory = 0;
		int num_sale = 0;
		int num_activated = 0;
		int num_return = 0;
		int num_other = 0;
		
		Iterator itr = ProductInteraction.getInteractions(_obj, _retailer).iterator();
		while (itr.hasNext()) {
			ProductInteraction interaction_obj = (ProductInteraction)itr.next();
			switch (interaction_obj.getInteractionType()) {
				case ProductInteraction.INVENTORY_TYPE : num_inventory++; break;
				case ProductInteraction.SALE_TYPE : num_sale++; break;
				case ProductInteraction.SALE_ACTIVATED_TYPE : num_activated++; break;
				case ProductInteraction.RETURN_TYPE : num_return++; break;
				case ProductInteraction.DEACTIVATED_OTHER_TYPE : num_other++; break;
			}
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_obj.getDescription()) + "\",");
		b.append("\"model\":\"" + JSONObject.escape(_obj.getModelNumber()) + "\",");
		b.append("\"mfgId\":" + _obj.getManufacturer().getId() + ",");
		b.append("\"mfg\":\"" + JSONObject.escape(_obj.getManufacturer().getLabel()) + "\",");
		b.append("\"upc\":\"" + JSONObject.escape(_obj.getUPC()) + "\",");
		b.append("\"isActive\":" + _obj.isActive() + ",");
		b.append("\"numInventory\":" + num_inventory + ",");
		b.append("\"numSale\":" + num_sale + ",");
		b.append("\"numActivated\":" + num_activated + ",");
		b.append("\"numReturn\":" + num_return + ",");
		b.append("\"numOther\":" + num_other + ",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSON(ProductInteraction _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		/*
			shortName = productName;
			if (shortName.length() > 24) {
				shortName = shortName.substring(0, 23);
			}
		*/
		
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getUniqueIdentifier()) + "\",");
		//b.append("\"serialNumber\":\"" + JSONObject.escape(_obj.getUniqueIdentifier()) + "\",");
		b.append("\"type\":" + _obj.getInteractionType() + ",");
		b.append("\"typeStr\":\"" + JSONObject.escape(_obj.getInteractionTypeString()) + "\",");
		b.append("\"interactionDate\":" + _obj.getInteractionDateUnixTimestamp() + ",");
		
		/*
		try {
			Endpoint endpoint_for_interaction = Endpoint.getEndpointBySerialNumber(_obj.getUniqueIdentifier());
			b.append("\"error\":\"" + JSONObject.escape(endpoint_for_interaction.get) + "\",");
			b.append("\"error\":\"" + JSONObject.escape(endpoint_for_interaction.getErrorMessage()) + "\",");
		} catch (Exception x) {
			x.printStackTrace();
		}
		*/
		
		b.append("\"lastInteractionType\":\"" + _obj.getInteractionTypeString() + "\",");
		b.append("\"store\":\"" + JSONObject.escape(_obj.getStore().getLabel()) + "\",");
		b.append("\"person\":\"" + JSONObject.escape(_obj.getInteractionPerson().getLabel()) + "\",");
		/*
		String productLabel = _obj.getProduct().getLabel();
		if (productLabel.length() > 60) {
			productLabel = productLabel.substring(0, 58);
		}
		b.append("\"productLabel\":\"" + JSONObject.escape(productLabel) + "\",");
		*/
		b.append("\"product\":\"" + JSONObject.escape(_obj.getProduct().getNameString()) + "\",");
		b.append("\"productImageURL\":\"" + JSONObject.escape(_obj.getProduct().getImageURLString()) + "\",");
		b.append("\"productId\":\"" + _obj.getProduct().getId() + "\",");
		try {
			long millis = Product.calculateShelfSitTimeMillis(_obj.getRetailer(), null, _obj.getProduct(), _obj.getUniqueIdentifier());
			
			long days = TimeUnit.MILLISECONDS.toDays(millis);
			long hours = TimeUnit.MILLISECONDS.toHours(millis - (days * 86400000l) );
			//long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
			//long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
			b.append("\"shelfTime\":\"" + days + " day" + (days == 1 ? "" : "s") + ", " + hours + " hour" + (hours == 1 ? "" : "s") + "\",");
		} catch (Exception x) {
			x.printStackTrace();
		}
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
		
	}
	
	private String toJSON(CourseGroupBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return "{ \"name\": \"" + JSONObject.escape(_obj.getNameString()) + "\", \"icon\": \"" + JSONObject.escape(_obj.getIcon()) + "\", \"isSub\": " + !_obj.hasChildren() + ", \"id\": " + _obj.getId() + " }";
	}
	
	private String toJSONDropdown(String _name, int _id, boolean _selected) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_name) + "\",");
		b.append("\"selected\":" + _selected + ",");
		b.append("\"id\":" + _id + "}");
		return b.toString();
	}
	
	private String toJSONShort(CompanyBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(DepartmentBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		if (_obj.getDepartmentNumber() > 0) {
			b.append("\"number\":\"" + _obj.getDepartmentNumber() + "\",");
		} else {
			b.append("\"number\":\"\",");
		}
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(DepartmentBean _obj, boolean _selected) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		if (_obj.getDepartmentNumber() > 0) {
			b.append("{\"name\":\"" + JSONObject.escape( (_obj.getDepartmentNumber() + " - " + _obj.getLabel()) ) + "\",");
		} else {
			b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		}
		b.append("\"selected\":" + _selected + ",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(DepartmentTypeBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(AudienceBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(RoleBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(AudienceBean _obj, boolean _selected) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"selected\":" + _selected + ",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(Product _obj, boolean _selected) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"selected\":" + _selected + ",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(CompanyBean _obj, boolean _selected) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"selected\":" + _selected + ",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(PersonGroupBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(PersonGroupBean _obj, boolean _selected) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"selected\":" + _selected + ",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(PersonTitleBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(PersonTitleBean _obj, boolean _selected) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"selected\":" + _selected + ",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONShort(RoleBean _obj, boolean _selected) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"selected\":" + _selected + ",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
	private String toJSONShort(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		String emailStr = _person.getEmail1String();
		String emailDisplay = "[NOT FOUND]";
		int at_index = emailStr.indexOf('@');
		if (at_index > -1) {
			emailDisplay = emailStr.substring(0, at_index);
		}
		String empId = _person.getEmployeeNumberString();
		if (empId.length() > 7) {
			empId = empId.substring(0, 6);
		}
		
		return "{ \"label\":\"" + _person.getLabel() + "\"," +
		" \"empId\":\"" + empId + "\"," +
		" \"active\":\"" + _person.isActive() + "\"," +
		" \"department\":\"" + JSONObject.escape(_person.getDepartmentNameString()) + "\"," +
		" \"email\":\"" + JSONObject.escape(emailStr) + "\"," +
		" \"emailDisplay\":\"" + JSONObject.escape(emailDisplay) + "\"," +
		" \"group\":\"" + JSONObject.escape(_person.getGroupNameString()) + "\"," +
		" \"id\":\"" + _person.getId() + "\"}";
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
	
	private String toJSONAdminUserProfile(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuilder b = new StringBuilder();
		
		if (_person == null) {
			
			b.append("{\"label\":\"\",");
			b.append("\"empId\":\"\",");
			b.append("\"firstName\":\"\",");
			b.append("\"lastName\":\"\",");
			b.append("\"email\":\"\",");
			b.append("\"username\":\"\",");
			b.append("\"active\":true,");
			b.append("\"lastLogIn\":\"Never\",");
			b.append("\"role\":\"\",");
			b.append("\"isInstructor\":false,");
			b.append("\"isManager\":false,");

			b.append("\"departmentId\":0,");
			b.append("\"jobTitleId\":0,");
			
		} else {

			b.append("{\"label\":\"" + JSONObject.escape(_person.getLabel()) + "\",");
			b.append("\"empId\":\"" + _person.getEmployeeNumberString() + "\",");
			b.append("\"firstName\":\"" + JSONObject.escape(_person.getFirstNameString()) + "\",");
			b.append("\"lastName\":\"" + JSONObject.escape(_person.getLastNameString()) + "\",");
			b.append("\"email\":\"" + JSONObject.escape(_person.getEmail1String()) + "\",");
			b.append("\"emailAlt\":\"" + JSONObject.escape(_person.getEmail2String()) + "\",");
			b.append("\"username\":\"" + JSONObject.escape(_person.getUsernameString()) + "\",");
			b.append("\"active\":" + _person.isActive() + ",");
			b.append("\"lastLogIn\":\"" + _person.getLastLogInDateString() + "\",");
			b.append("\"roleStr\":\"" + JSONObject.escape(_person.getRolesString()) + "\",");
			b.append("\"isInstructor\":" + _person.hasRole(RoleBean.INSTRUCTOR_ROLE_NAME) + ",");
			b.append("\"isManager\":\"" + (_person.hasRole(RoleBean.MANAGER_ROLE_NAME) ? "1" : "0") + "\",");

			b.append("\"departmentId\":" + (_person.hasDepartment() ? _person.getDepartment().getId() : 0) + ",");
			b.append("\"jobTitleId\":" + (_person.hasJobTitle() ? _person.getJobTitle().getId() : 0) + ",");
		}
		
		boolean needs_comma = false;
		b.append("\"group\":[");
		if (_person != null) {
			Iterator itr = _person.getGroups().iterator();
			while (itr.hasNext()) {
				PersonGroupBean obj = (PersonGroupBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		needs_comma = false;
		b.append("\"audience\":[");
		if (_person != null) {
			Iterator itr = _person.getAudiences().iterator();
			while (itr.hasNext()) {
				AudienceBean obj = (AudienceBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		needs_comma = false;
		b.append("\"role\":[");
		if (_person != null) {
			Iterator itr = _person.getRoles().iterator();
			while (itr.hasNext()) {
				RoleBean obj = (RoleBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		if (_person == null) {
			b.append("\"id\":0}");
		} else {
			b.append("\"id\":" + _person.getId() + "}");
		}
		
		return b.toString();
	}
	
	private String toJSONAdminBusinessUnit(CompanyBean _bu) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		boolean allowSelfEnroll = false;
		Vector departments = null;
		Vector types = null;
		Vector titles = null;
		Vector groups = null;
		Vector audiences = null;
		Vector classrooms = null;
		Vector buildings = null;
		Vector locations = null;
		
		int bu_id = 0;
		String bu_name = "";
		String bu_account_number = "";
		
		try {
			
			if (_bu != null) {
				bu_id = _bu.getId();
				bu_name = _bu.getNameString();
				bu_account_number = _bu.getSICCodeString();
				CompanySettingsBean settings = _bu.getSettings();
				allowSelfEnroll = settings.allowSelfRegistration();
				
				departments = DepartmentBean.getDepartments(_bu);
				types = DepartmentTypeBean.getDepartmentTypes(_bu);
				titles = PersonTitleBean.getPersonTitles(_bu);
				groups = PersonGroupBean.getPersonGroups(_bu);
				audiences = AudienceBean.getAudiences(_bu);
				classrooms = ClassroomBean.getClassrooms(_bu);
				buildings = BuildingBean.getBuildings(_bu);
				locations = LocationBean.getLocations(_bu);
			}
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(bu_name) + "\",");
		b.append("\"accountNumber\":\"" + JSONObject.escape(bu_account_number) + "\",");
		b.append("\"allowSelfEnroll\":" + allowSelfEnroll + ",");
		
		
		b.append("\"department\":[");
		if (departments != null) {
			boolean needs_comma = false;
			Iterator itr = departments.iterator();
			while (itr.hasNext()) {
				DepartmentBean obj = (DepartmentBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"departmentType\":[");
		if (types != null) {
			boolean needs_comma = false;
			Iterator itr = types.iterator();
			while (itr.hasNext()) {
				DepartmentTypeBean obj = (DepartmentTypeBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"title\":[");
		if (titles != null) {
			boolean needs_comma = false;
			Iterator itr = titles.iterator();
			while (itr.hasNext()) {
				PersonTitleBean obj = (PersonTitleBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		
		b.append("\"group\":[");
		if (groups != null) {
			boolean needs_comma = false;
			Iterator itr = groups.iterator();
			while (itr.hasNext()) {
				PersonGroupBean obj = (PersonGroupBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		
		b.append("\"audience\":[");
		if (audiences != null) {
			boolean needs_comma = false;
			Iterator itr = audiences.iterator();
			while (itr.hasNext()) {
				AudienceBean obj = (AudienceBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"id\":" + bu_id + "}");
		
		return b.toString();
	}
	
	private String toJSONAdminProduct(CompanyBean _bu, Product _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		Iterator mfg_itr = null;
		String productName = "";
		String shortName = "";
		long productId = 0;
		//String typeString = "";
		String descStr = "";
		String model = "";
		int mfgId = 0;
		String mfg = "";
		String upc = "";
		boolean isActive = false;
		
		try {
			mfg_itr = CompanyBean.getCompanies(CompanyBean.MANUFACTURER_COMPANY_TYPE).iterator();
			if (_obj != null) {
				productName = _obj.getNameString();
				shortName = productName;
				if (shortName.length() > 24) {
					shortName = shortName.substring(0, 23);
				}
				productId = _obj.getId();
				descStr = _obj.getDescription();
				model = _obj.getModelNumber();
				upc = _obj.getUPC();
				isActive = _obj.isActive();
				mfgId = _obj.getManufacturer().getId();
				mfg = _obj.getManufacturer().getLabel();
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(productName) + "\",");
		b.append("\"shortName\":\"" + JSONObject.escape(shortName) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(descStr) + "\",");
		b.append("\"isActive\":" + isActive + ",");
		b.append("\"mfgId\":" + mfgId + ",");
		b.append("\"mfg\":\"" + JSONObject.escape(mfg) + "\",");
		b.append("\"model\":\"" + JSONObject.escape(model) + "\",");
		b.append("\"upc\":\"" + upc + "\",");
		
		b.append("\"bu\":[");
		if (mfg_itr != null) {
			boolean needs_comma = false;
			while (mfg_itr.hasNext()) {
				CompanyBean obj = (CompanyBean)mfg_itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		
		b.append("\"id\":" + productId + "}");
		
		return b.toString();
	}

    // SQL

    /*
		<table name="PRODUCT_INTERACTION_DB" idMethod="native">
			<column name="PRODUCT_INTERACTION_DB" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
			<column name="RETAILER_ID" required="false" type="INTEGER"/>
			<column name="STORE_ID" required="false" type="INTEGER"/>
			<column name="INTERACTION_PERSON_ID" required="false" type="INTEGER"/>
			<column name="PRODUCT_DB_ID" required="true" type="BIGINT"/>

			<column name="UNIQUE_ID" size="100" type="VARCHAR"/>
			<!-- Can be MAC Address, Serial Number, or something else agreed upon as long as it's unique for the unit in question -->
			<!-- must be the same id on sale as on activation -->

			<column name="INTERACTION_TYPE" type="SMALLINT"/>
			<!-- 1 = inventory -->
			<!-- 2 = sale - not activated -->
			<!-- 3 = sale - activated -->
			<!-- 4 = return - de-activated -->
			<!-- 5 = de-activation other -->

			<column name="INTERACTION_DATE" required="true" type="DATE"/>

			<foreign-key foreignTable="COMPANY">
				<reference local="RETAILER_ID" foreign="COMPANYID"/>
			</foreign-key>
			<foreign-key foreignTable="DEPARTMENT">
				<reference local="STORE_ID" foreign="DEPARTMENTID"/>
			</foreign-key>
			<foreign-key foreignTable="PERSON">
				<reference local="INTERACTION_PERSON_ID" foreign="PERSONID"/>
			</foreign-key>
			<foreign-key foreignTable="PRODUCT_DB">
				<reference local="PRODUCT_DB_ID" foreign="PRODUCT_DB_ID"/>
			</foreign-key>
		</table>
     */
	
	private String toJSONAdminInteraction(String product_id_str, ProductInteraction _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		Product product_obj = Product.getProduct(Long.parseLong(product_id_str));
		
		String serialNumber = "";
		long interactionId = 0;
		short interactionType = 0;
		String interactionTypeStr = "";
		long interactionDate = 0;
		
		int retailerId = 0;
		String retailerStr = "";
		int storeId = 0;
		String storeStr = "";
		int personId = 0;
		String personStr = "";
		long productId = 0;
		String productStr = "";
		long endpointId = 0;
		String endpointHashStr = "[NOT FOUND]";
		
		try {
			
			productId = product_obj.getId();
			productStr = product_obj.getLabel();
			
			if (_obj != null) {
				interactionId = _obj.getId();
				interactionType = _obj.getInteractionType();
				interactionTypeStr = _obj.getInteractionTypeString();
				interactionDate = _obj.getInteractionDateUnixTimestamp();
				serialNumber = _obj.getUniqueIdentifier();
				
				if (_obj.getRetailer() != null) {
					retailerId = _obj.getRetailer().getId();
					retailerStr = _obj.getRetailer().getLabel();
				}
				if (_obj.getStore() != null) {
					storeId = _obj.getStore().getId();
					storeStr = _obj.getStore().getLabel();
				}
				if (_obj.getInteractionPerson() != null) {
					personId = _obj.getInteractionPerson().getId();
					personStr = _obj.getInteractionPerson().getLabel();
				}
				
				try {
					Endpoint endpoint_obj = Endpoint.getEndpointBySerialNumber(serialNumber);
					endpointId = endpoint_obj.getId();
					endpointHashStr = endpoint_obj.getEndpointHash();
				} catch (ObjectNotFoundException x) {
					System.out.println("error >" + x.getMessage());
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(serialNumber) + "\",");
		b.append("\"type\":" + interactionType + ",");
		b.append("\"typeStr\":\"" + JSONObject.escape(interactionTypeStr) + "\",");
		b.append("\"interactionDate\":" + interactionDate + ",");
		
		b.append("\"productId\":\"" + productId + "\",");
		b.append("\"product\":\"" + JSONObject.escape(productStr) + "\",");
		
		b.append("\"retailerId\":\"" + retailerId + "\",");
		b.append("\"retailer\":\"" + JSONObject.escape(retailerStr) + "\",");
		
		b.append("\"storeId\":\"" + storeId + "\",");
		b.append("\"store\":\"" + JSONObject.escape(storeStr) + "\",");
		
		b.append("\"personId\":\"" + personId + "\",");
		b.append("\"person\":\"" + JSONObject.escape(personStr) + "\",");
		
		b.append("\"endpointId\":\"" + endpointId + "\",");
		b.append("\"endpointHash\":\"" + JSONObject.escape(endpointHashStr) + "\",");
		
		/*
		b.append("\"bu\":[");
		if (mfg_itr != null) {
			boolean needs_comma = false;
			while (mfg_itr.hasNext()) {
				CompanyBean obj = (CompanyBean)mfg_itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		*/
		
		b.append("\"id\":" + interactionId + "}");
		
		return b.toString();
	}
	
	
	private String toJSONAdminEndpoint(CompanyBean _bu, Endpoint _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		
		ProductInteraction lastInteraction = null;
		String serialNumberStr = _obj.getSerialNumber();
		if (serialNumberStr.isEmpty()) {
			serialNumberStr = "[Serial Number Not Assigned]";
		} else {
			lastInteraction = ProductInteraction.getLastInteraction(null, serialNumberStr); // what if serial number isn't distinct??? - COULD BE BIG PROBLEM!!!!
		}
		
		
		long endpointId = 0;
		boolean isActive = false;
		
		String applicationId = "";
		String endpointHash = "";
		String profileId = "";
		long registrationUnixTimestamp = 0l;
		String sdkToken = "";
		String serialNumber = "";
		
		try {
			
			if (_obj != null) {
				endpointId = _obj.getId();
				applicationId = _obj.getApplicationID();
				endpointHash = _obj.getEndpointHash();
				profileId = _obj.getProfileID();
				registrationUnixTimestamp = _obj.getRegistrationDateUnixTimestamp();
				sdkToken = _obj.getSDKToken();
				serialNumber = _obj.getSerialNumber();
				isActive = _obj.isActivated();
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"endpointHash\":\"" + JSONObject.escape(endpointHash) + "\",");
		b.append("\"applicationId\":\"" + applicationId + "\",");
		b.append("\"profileId\":\"" + profileId + "\",");
		b.append("\"isActive\":" + isActive + ",");
		b.append("\"registrationDate\":" + registrationUnixTimestamp + ",");
		b.append("\"sdkToken\":\"" + sdkToken + "\",");
		b.append("\"serialNumber\":\"" + JSONObject.escape(serialNumber) + "\",");
		if (lastInteraction != null) {
			b.append("\"lastInteractionType\":\"" + lastInteraction.getInteractionTypeString() + "\",");
			b.append("\"store\":\"" + JSONObject.escape(lastInteraction.getStore().getLabel()) + "\",");
			b.append("\"person\":\"" + JSONObject.escape(lastInteraction.getInteractionPerson().getLabel()) + "\",");
			String productLabel = lastInteraction.getProduct().getLabel();
			if (productLabel.length() > 60) {
				productLabel = productLabel.substring(0, 58);
			}
			b.append("\"productLabel\":\"" + JSONObject.escape(productLabel) + "\",");
		}
		b.append("\"error\":\"" + JSONObject.escape(_obj.getErrorMessage()) + "\",");
		b.append("\"id\":" + endpointId + "}");
		
		return b.toString();
		
		
		/*
		
		StringBuffer b = new StringBuffer();
		b.append("{\"profileId\":\"" + JSONObject.escape(_obj.getProfileID()) + "\",");
		b.append("\"hash\":\"" + JSONObject.escape(_obj.getEndpointHash()) + "\",");
		b.append("\"serialNumber\":\"" + JSONObject.escape(serialNumberStr) + "\","); // magic sauce needed
		if (lastInteraction != null) {
			b.append("\"lastInteractionType\":\"" + lastInteraction.getInteractionTypeString() + "\",");
			b.append("\"store\":\"" + JSONObject.escape(lastInteraction.getStore().getLabel()) + "\",");
			b.append("\"person\":\"" + JSONObject.escape(lastInteraction.getInteractionPerson().getLabel()) + "\",");
			b.append("\"productLabel\":\"" + JSONObject.escape(lastInteraction.getProduct().getLabel()) + "\",");
		}
		b.append("\"activated\":" + _obj.isActivated() + ",");
		b.append("\"appId\":\"" + JSONObject.escape(_obj.getApplicationID()) + "\",");
		b.append("\"sdkToken\":\"" + JSONObject.escape(_obj.getSDKToken()) + "\",");
		b.append("\"registrationDate\":" + _obj.getRegistrationDateUnixTimestamp() + ",");
		b.append("\"error\":\"" + JSONObject.escape(_obj.getErrorMessage()) + "\",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
		
		*/
		
		
	}
	
	
	private String toJSONAdminDepartment(CompanyBean _bu, DepartmentBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		String departmentName = "";
		int departmentId = 0;
		int parentId = 0;
		int typeId = 0;
		Vector departments = null;
		Vector types = null;
		try {
			departments = DepartmentBean.getDepartments(_bu);
			types = DepartmentTypeBean.getDepartmentTypes(_bu);
			
			if (_obj != null) {
				departmentName = _obj.getNameString();
				departmentId = _obj.getId();
				typeId = _obj.getType().getId();
				parentId = _obj.getParentId();
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(departmentName) + "\",");
		b.append("\"parentId\":" + parentId + ",");
		b.append("\"typeId\":" + typeId + ",");
		
		b.append("\"department\":[");
		if (departments != null) {
			boolean needs_comma = false;
			Iterator itr = departments.iterator();
			while (itr.hasNext()) {
				DepartmentBean obj = (DepartmentBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"departmentType\":[");
		if (types != null) {
			boolean needs_comma = false;
			Iterator itr = types.iterator();
			while (itr.hasNext()) {
				DepartmentTypeBean obj = (DepartmentTypeBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"id\":" + departmentId + "}");
		
		return b.toString();
	}
	
	private String toJSONAdminDepartmentType(CompanyBean _bu, DepartmentTypeBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		String typeName = "";
		int typeId = 0;
		Vector types = null;
		try {
			types = DepartmentTypeBean.getDepartmentTypes(_bu);
			
			if (_obj != null) {
				typeName = _obj.getNameString();
				typeId = _obj.getId();
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(typeName) + "\",");
		b.append("\"departmentType\":[");
		if (types != null) {
			boolean needs_comma = false;
			Iterator itr = types.iterator();
			while (itr.hasNext()) {
				DepartmentTypeBean obj = (DepartmentTypeBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		b.append("\"id\":" + typeId + "}");
		
		return b.toString();
	}
	
	private String toJSONAdminTitle(UKOnlinePersonBean _mod_person, CompanyBean _bu, PersonTitleBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		String titleName = "";
		String shortName = "";
		int titleId = 0;
		
		Vector roles = null;
		Vector mapped_roles = null;
		Vector titles = null;
		try {
			roles = UKOnlineRoleBean.getEssentialAssignableRoles(_mod_person);
			titles = PersonTitleBean.getPersonTitles(_bu);
			
			if (_obj != null) {
				titleName = _obj.getNameString();
				shortName = _obj.getShortNameString();
				titleId = _obj.getId();
				mapped_roles = _obj.getRoles();
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(titleName) + "\",");
		b.append("\"shortName\":\"" + JSONObject.escape(shortName) + "\",");
		
		b.append("\"role\":[");
		if (roles != null) {
			boolean needs_comma = false;
			Iterator itr = roles.iterator();
			while (itr.hasNext()) {
				RoleBean obj = (RoleBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj, ((mapped_roles != null) && mapped_roles.contains(obj)) ));
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"title\":[");
		if (titles != null) {
			boolean needs_comma = false;
			Iterator itr = titles.iterator();
			while (itr.hasNext()) {
				PersonTitleBean obj = (PersonTitleBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"id\":" + titleId + "}");
		
		return b.toString();
	}
	
	private String toJSONAdminGroup(CompanyBean _bu, PersonGroupBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		String groupName = "";
		int groupId = 0;
		Vector groups = null;
		try {
			groups = PersonGroupBean.getPersonGroups(_bu);
			
			if (_obj != null) {
				groupName = _obj.getNameString();
				groupId = _obj.getId();
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(groupName) + "\",");
		b.append("\"group\":[");
		if (groups != null) {
			boolean needs_comma = false;
			Iterator itr = groups.iterator();
			while (itr.hasNext()) {
				PersonGroupBean obj = (PersonGroupBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		b.append("\"id\":" + groupId + "}");
		
		return b.toString();
	}
	
	private String toJSONAdminAudience(CompanyBean _bu, AudienceBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		System.out.println("toJSONAdminAudience >" + _bu.getLabel());
		
		String audienceName = "";
		int audienceId = 0;
		Vector audiences = null;
		Vector departments = null;
		Vector titles = null;
		Vector groups = null;
		try {
			audiences = AudienceBean.getAudiences(_bu);
			departments = DepartmentBean.getDepartments(_bu);
			titles = PersonTitleBean.getPersonTitles(_bu);
			groups = PersonGroupBean.getPersonGroups(_bu);
			
			if (_obj != null) {
				audienceName = _obj.getNameString();
				audienceId = _obj.getId();
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(audienceName) + "\",");
		
		b.append("\"audience\":[");
		if (audiences != null) {
			boolean needs_comma = false;
			Iterator itr = audiences.iterator();
			while (itr.hasNext()) {
				AudienceBean obj = (AudienceBean)itr.next();
				if (needs_comma) { b.append(","); }
				b.append(this.toJSONShort(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"department\":[");
		if (departments != null) {
			boolean needs_comma = false;
			Iterator itr = departments.iterator();
			while (itr.hasNext()) {
				DepartmentBean obj = (DepartmentBean)itr.next();
				if (needs_comma) { b.append(","); }
				if (_obj == null) {
					b.append(this.toJSONShort(obj, false));
				} else {
					b.append(this.toJSONShort(obj, _obj.contains(obj)));
				}
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"title\":[");
		if (titles != null) {
			boolean needs_comma = false;
			Iterator itr = titles.iterator();
			while (itr.hasNext()) {
				PersonTitleBean obj = (PersonTitleBean)itr.next();
				if (needs_comma) { b.append(","); }
				if (_obj == null) {
					b.append(this.toJSONShort(obj, false));
				} else {
					b.append(this.toJSONShort(obj, _obj.contains(obj)));
				}
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"group\":[");
		if (titles != null) {
			boolean needs_comma = false;
			Iterator itr = groups.iterator();
			while (itr.hasNext()) {
				PersonGroupBean obj = (PersonGroupBean)itr.next();
				if (needs_comma) { b.append(","); }
				if (_obj == null) {
					b.append(this.toJSONShort(obj, false));
				} else {
					b.append(this.toJSONShort(obj, _obj.contains(obj)));
				}
				needs_comma = true;
			}
		}
		b.append("],");
		
		b.append("\"id\":" + audienceId + "}");
		
		return b.toString();
	}
	
	/*
	
<table name="PRODUCT_DB" idMethod="native">
	<column name="PRODUCT_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	<column name="NAME" size="200" type="VARCHAR"/>
    <column name="DESCRIPTION" type="LONGVARCHAR"/>
	<column name="MODEL_NUMBER" size="30" type="VARCHAR"/>
	<column name="MFG_ID" required="true" type="INTEGER"/>
    
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
	
	<column name="IS_ACTIVE" type="SMALLINT" default="1"/>

    <foreign-key foreignTable="COMPANY">
		<reference local="MFG_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
	*/
	
	/*
	private BigDecimal numInventoryPercentage = BigDecimal.ZERO;
	private BigDecimal numSoldNotActivatedPercentage = BigDecimal.ZERO;
	private BigDecimal numSoldAndActivatedPercentage = BigDecimal.ZERO;
	private BigDecimal numReturnedDeactivated = BigDecimal.ZERO;
	private BigDecimal numDeactivatedOther = BigDecimal.ZERO;
	*/
	
	private String toJSONPieChart(Product _product, CompanyBean _retailer, DepartmentBean _store, boolean _selected, Date _start_date, Date _end_date) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		_product.calculateInteractionPercentages(_retailer, _store, _start_date, _end_date);
		
		BigDecimal numInventoryPercentage = _product.getPercentInventory(_retailer, _store);
		BigDecimal numSoldNotActivatedPercentage = _product.getPercentSold(_retailer, _store);
		BigDecimal numSoldAndActivatedPercentage = _product.getPercentActivated(_retailer, _store);
		BigDecimal numReturnedDeactivated = _product.getPercentReturned(_retailer, _store);
		BigDecimal numDeactivatedOther = _product.getPercentDeactivatedOther(_retailer, _store);
		
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_product.getLabel()) + "\",");
		b.append("\"isActive\":" + _product.isActive() + ",");
		
		b.append("\"percInventory\":\"" + numInventoryPercentage.setScale(0, RoundingMode.HALF_UP).toString() + "\",");
		b.append("\"percSold\":\"" + numSoldNotActivatedPercentage.setScale(0, RoundingMode.HALF_UP).toString() + "\",");
		b.append("\"percActivated\":\"" + numSoldAndActivatedPercentage.setScale(0, RoundingMode.HALF_UP).toString() + "\",");
		b.append("\"percReturned\":\"" + numReturnedDeactivated.setScale(0, RoundingMode.HALF_UP).toString() + "\",");
		b.append("\"percDeactivatedOther\":\"" + numDeactivatedOther.setScale(0, RoundingMode.HALF_UP).toString() + "\",");
		
		b.append("\"numInventory\":" + _product.getNumInventory(_retailer, _store) + ",");
		b.append("\"numSold\":" + _product.getNumSold(_retailer, _store) + ",");
		b.append("\"numActivated\":" + _product.getNumActivated(_retailer, _store) + ",");
		b.append("\"numReturned\":" + _product.getNumReturned(_retailer, _store) + ",");
		b.append("\"numDeactivatedOther\":" + _product.getNumDeactivatedOther(_retailer, _store) + ",");
		
		b.append("\"isSelected\":" + _selected + ",");
		b.append("\"id\":\"" + _product.getId() + "\"}");
		
		return b.toString();
	}
	
	private String
	getResourcesInTopicHierarchy(UKOnlinePersonBean _person, Object _resources_vec, int _topic_id, boolean _show_empty_schools) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		List resourceList = null;
		Enumeration resourceEnum = null;
		if (_resources_vec instanceof List) {
			resourceList = (List)_resources_vec;
		} else if (_resources_vec instanceof Enumeration) {
			resourceEnum = (Enumeration)_resources_vec;
		}
		
		if ((resourceList != null) && resourceList.isEmpty()) {
			return "{\"topic\":[]}";
		}
		if ((resourceEnum != null) && !resourceEnum.hasMoreElements()) {
			return "{\"topic\":[]}";
		}
		
		Vector ungrouped_resources = new Vector();
		
		HashMap<CourseGroupBean,Vector> hash = new HashMap<CourseGroupBean,Vector>(11);
		if (resourceList != null) {
			Iterator resource_itr = resourceList.iterator();
			while (resource_itr.hasNext()) {
				ResourceBean resource_obj = (ResourceBean)resource_itr.next();
				System.out.println("resource_obj >" + resource_obj.getLabel());
				if (resource_obj.hasCourseGroup()) {
					CourseGroupBean key = resource_obj.getCourseGroup();
					System.out.println("resource_obj key >" + key.getLabel());
					Vector vec = (Vector)hash.get(key);
					if (vec == null) {
						vec = new Vector();
						hash.put(key, vec);
					}
					vec.addElement(resource_obj);
				} else {
					ungrouped_resources.addElement(resource_obj);
				}
			}
		} else if (resourceEnum != null) {
			while (resourceEnum.hasMoreElements()) {
				ResourceBean resource_obj = (ResourceBean)resourceEnum.nextElement();
				System.out.println("resource_obj >" + resource_obj.getLabel());
				if (resource_obj.hasCourseGroup()) {
					CourseGroupBean key = resource_obj.getCourseGroup();
					System.out.println("resource_obj key >" + key.getLabel());
					Vector vec = (Vector)hash.get(key);
					if (vec == null) {
						vec = new Vector();
						hash.put(key, vec);
					}
					vec.addElement(resource_obj);
				} else {
					ungrouped_resources.addElement(resource_obj);
				}
			}
		}
		
		//boolean needs_comma = false;
		StringBuilder b = new StringBuilder();

		b.append("{\"topic\":[");

		boolean needs_comma_x = false;
		Vector groups = CourseGroupBean.getCourseGroups(_person.getDepartment().getCompany());
		System.out.println("groups >" + groups.size());
		Iterator group_itr = groups.iterator();
		while (group_itr.hasNext()) {
			CourseGroupBean key = (CourseGroupBean)group_itr.next();
			
			System.out.println("key >" + key.getLabel());

			//if (key.isGroupForResourceOnly()) {
				if (key.getTypeString().equals("Course") && (_topic_id == -1 || key.getId() == _topic_id)) {

					Vector vec = (Vector)hash.get(key);

					boolean has_child_stuff = false;
					if (key.hasChildren()) {
						Iterator child_itr = key.getChildren().iterator();
						while (child_itr.hasNext()) {
							CourseGroupBean childkey = (CourseGroupBean)child_itr.next();
							if (childkey.getTypeString().equals("Lesson")) {
								if (hash.get(childkey) != null) {
									has_child_stuff = true;
									break;
								}
							}
						}
					}

					if (vec == null && !has_child_stuff && !_show_empty_schools) {
					} else {


						if (needs_comma_x) { b.append(","); }
						b.append("{\"id\":\"" + key.getId() + "\",\"icon\":\"fa-trophy\",\"label\":\"" + key.getNameString()+ "\",\"resource\":[");
						if (vec != null) {
							Iterator itr = vec.iterator();
							boolean needs_comma_y = false;
							while (itr.hasNext()) {
								ResourceBean resource_obj = (ResourceBean)itr.next();
								if (needs_comma_y) { b.append(","); }
								b.append(this.toJSON(resource_obj));
								needs_comma_y = true;
							}
						}
						b.append("]");


						if (key.hasChildren()) {

							//boolean needs_comma_xx = false;
							Iterator child_itr = key.getChildren().iterator();
							while (child_itr.hasNext()) {

								CourseGroupBean childkey = (CourseGroupBean)child_itr.next();

								if (childkey.getTypeString().equals("Lesson")) {
									Vector vec2 = (Vector)hash.get(childkey);
									if (vec2 != null) {
										b.append(",\"subcategory\":[{\"id\":\"" + childkey.getId() + "\",\"label\":\"" + childkey.getNameString()+ "\",\"resource\":[");
										Iterator itr2 = vec2.iterator();
										boolean needs_comma_yy = false;
										while (itr2.hasNext()) {
											ResourceBean resource_obj = (ResourceBean)itr2.next();
											if (needs_comma_yy) { b.append(","); }
											b.append(this.toJSON(resource_obj));
											needs_comma_yy = true;
										}
										b.append("]}]");

									}
								}

							}

						}

						b.append("}");
						needs_comma_x = true;


					}
				}
			//}
		}

		if (ungrouped_resources.isEmpty()) {
			b.append("]}");
		} else {
			b.append("],\"ungrouped\":[");
			boolean needs_comma = false;
			Iterator itr = ungrouped_resources.iterator();
			while (itr.hasNext()) {
				if (needs_comma) { b.append(","); }
				b.append(this.toJSON((ResourceBean)itr.next()));
				needs_comma = true;
			}
			b.append("]}");
		}
		
		return b.toString();
	}
	
	private String toJSON(ResourceBean _resource) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuffer b = new StringBuffer();
		b.append("{\"label\":\"" + JSONObject.escape(_resource.getLabel()) + "\",");
		b.append("\"name\":\"" + JSONObject.escape(_resource.getLabel()) + "\",");
		if (_resource.hasCourseGroup()) {
			CourseGroupBean group = _resource.getCourseGroup();
			b.append("\"topicLabel\":\"" + JSONObject.escape(group.getNameString()) + "\",");
			b.append("\"topicId\":\"" + group.getId() + "\",");
		}
		b.append("\"desc\":\"" + JSONObject.escape(_resource.getDescription()) + "\",");
		b.append("\"type\":\"" + _resource.getTypeString() + "\",");
		b.append("\"start\":\"" + JSONObject.escape(_resource.getReleasedDateString()) + "\",");
		b.append("\"end\":\"" + JSONObject.escape(_resource.getExpirationDateString()) + "\",");
		
		String icon_str = "";
		if (_resource.getTypeString().equals(ResourceBean.ACROBAT_RESOURCE_TYPE)) {
			icon_str = "file-pdf.png";
		} else if (_resource.getTypeString().equals(ResourceBean.WORD_RESOURCE_TYPE)) {
			icon_str = "file-word.png";
		} else if (_resource.getTypeString().equals(ResourceBean.XLS_RESOURCE_TYPE)) {
			icon_str = "file-excel.png";
		} else if (_resource.getTypeString().equals(ResourceBean.POWERPOINT_RESOURCE_TYPE)) {
			icon_str = "file-powerpoint.png";
		} else {
			icon_str = "file.png";
		}
		b.append("\"icon\":\"" + icon_str + "\",");
		
		//String launchUrl = _resource.getURLString();
		
		//System.out.println("URL >" + _resource.getOpenAnchorString(null).getURLString());
		
		String launchUrl = "";
		try {
			//launchUrl = CUBean.getProperty("cu.webDomain") + File.separatorChar + "resources" + File.separatorChar + URLEncoder.encode(_resource.getURL(), "UTF-8");
			launchUrl = CUBean.getProperty("cu.webDomain") + File.separatorChar + "resources" + File.separatorChar + _resource.getURL();
		} catch (Exception x) {
			
		}
		
		b.append("\"launchURL\":\"" + JSONObject.escape(launchUrl) + "\",");
		b.append("\"isAssignment\":" + _resource.isAssignment() + ",");
		b.append("\"id\":\"" + _resource.getId() + "\"}");
		
		return b.toString();
	}
	
	
	
	private String toJSON(IoTLOKReportTemplateBean _template, boolean _include_results) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuffer b = new StringBuffer();
		b.append("{\"label\":\"" + JSONObject.escape(_template.getLabel()) + "\",");
		b.append("\"type\":\"" + JSONObject.escape(_template.getReportTypeString()) + "\",");
		b.append("\"startDate\":\"" + JSONObject.escape(_template.getStartDateString()) + "\",");
		b.append("\"endDate\":\"" + JSONObject.escape(_template.getEndDateString()) + "\",");
		b.append("\"showExcel\":" + _template.isShowExcel() + ",");
		b.append("\"includeInactives\":" + _template.isIncludeInactive() + ",");
		b.append("\"status\":\"" + JSONObject.escape(_template.getEnrollmentStatus()) + "\",");
		
		int numResults = 0;
		
		if (_include_results) {
			
			switch (_template.getReportType()) {
				case IoTLOKReportTemplateBean.PRODUCT_REPORT_TYPE: {
					b.append("\"head\":[");
					b.append("{\"name\":\"Model #\",\"span\":1},");
					b.append("{\"name\":\"Product Name\",\"span\":3},");
					b.append("{\"name\":\"Mfg\",\"span\":1},");
					b.append("{\"name\":\"ASN\",\"span\":1},");
					b.append("{\"name\":\"Inv\",\"span\":1},");
					b.append("{\"name\":\"Sold\",\"span\":1},");
					b.append("{\"name\":\"Activated\",\"span\":1},");
					b.append("{\"name\":\"Return\",\"span\":1},");
					b.append("{\"name\":\"Chargeback\",\"span\":1},");
					b.append("{\"name\":\"Other\",\"span\":1}");
					b.append("],");
					break;
				} case IoTLOKReportTemplateBean.INTERACTION_REPORT_TYPE: {
					b.append("\"head\":[");
					b.append("{\"name\":\"Product\",\"span\":4},");
					b.append("{\"name\":\"Serial #\",\"span\":2},");
					b.append("{\"name\":\"Date\",\"span\":1},");
					b.append("{\"name\":\"Time\",\"span\":1},");
					b.append("{\"name\":\"Type\",\"span\":2},");
					b.append("{\"name\":\"Store\",\"span\":2}");
					b.append("],");
					break;
				} case IoTLOKReportTemplateBean.SERIAL_NUMBER_REPORT_TYPE: {
					b.append("\"head\":[");
					b.append("{\"name\":\"Product\",\"span\":3},");
					b.append("{\"name\":\"Serial #\",\"span\":2},");
					b.append("{\"name\":\"Date\",\"span\":2},");
					b.append("{\"name\":\"Type\",\"span\":2},");
					b.append("{\"name\":\"Store\",\"span\":1},");
					b.append("{\"name\":\"Shelf Time\",\"span\":2}");
					b.append("],");
					break;
				} case IoTLOKReportTemplateBean.ACTIVATION_REPORT_TYPE: {
					b.append("\"head\":[");
					b.append("{\"name\":\"Emp.#\",\"span\":1},");
					b.append("{\"name\":\"User\",\"span\":2},");
					b.append("{\"name\":\"Dept.\",\"span\":2},");
					b.append("{\"name\":\"Course\",\"span\":4},");
					b.append("{\"name\":\"Status\",\"span\":2},");
					b.append("{\"name\":\"Date\",\"span\":1}");
					b.append("],");
					break;
				} case IoTLOKReportTemplateBean.SERIAL_NUMBER_HISTORY_REPORT_TYPE: {
					b.append("\"head\":[");
					b.append("{\"name\":\"Emp.#\",\"span\":1},");
					b.append("{\"name\":\"User\",\"span\":3},");
					b.append("{\"name\":\"Dept.\",\"span\":2},");
					b.append("{\"name\":\"Certification\",\"span\":3},");
					b.append("{\"name\":\"Received\",\"span\":1},");
					b.append("{\"name\":\"Expires\",\"span\":1}");
					b.append("],");
					break;
				} case IoTLOKReportTemplateBean.USER_REPORT_TYPE: {
					b.append("\"head\":[");
					b.append("{\"name\":\"Emp.#\",\"span\":1},");
					b.append("{\"name\":\"User\",\"span\":3},");
					if (_template.getCompany().getId() == 2) { // Kraus-Anderson
						b.append("{\"name\":\"Office\",\"span\":4},");
					} else {
						b.append("{\"name\":\"Dept.\",\"span\":4},");
					}
					b.append("{\"name\":\"Groups\",\"span\":4}");
					b.append("],");
					break;
				}
				
			}
			
		}
		
		// List aList = courseReportLister.getUsers();

		
		b.append("\"result\":[");
		if (_include_results) {
			
			
			com.badiyan.uk.online.beans.UKOnlineCourseReportLister courseReportLister = new com.badiyan.uk.online.beans.UKOnlineCourseReportLister();
			courseReportLister.setReportTemplate(_template);
			
			if (_template.getEnrollmentStatus().equals("6")) {
				if (_template.getCourses().size() > 1) {
					throw new IllegalValueException("Only one course selection allowed in Not Enrolled report.");
				}
			}
			
			switch (_template.getReportType()) {
				case IoTLOKReportTemplateBean.PRODUCT_REPORT_TYPE: {
					
					class ProductReportData {
						int num_asn = 0;
						int num_inventory = 0;
						int num_sale = 0;
						int num_sale_activated = 0;
						int num_return = 0;
						int num_deactivated_other = 0;
						int num_deactivated_chargeback = 0;
					}
					Vector<Product> productList = new Vector<Product>();
					HashMap<Product,ProductReportData> productDataMap = new HashMap<Product,ProductReportData>();
					
					courseReportLister.searchProducts();
					Iterator itr = courseReportLister.getListStuff().iterator();
					while (itr.hasNext()) {
						
						//com.badiyan.torque.ProductDb obj = (com.badiyan.torque.ProductDb)aList.get(i);
						com.badiyan.torque.ProductInteractionDb obj = (com.badiyan.torque.ProductInteractionDb)itr.next();
						ProductInteraction interaction_obj = ProductInteraction.getInteraction(obj);
						interaction_obj.getInteractionType();

						Product product_obj = Product.getProduct(obj.getProductDbId());
						if (!productList.contains(product_obj)) {
							productList.addElement(product_obj);
						}
						ProductReportData productData = null;
						if (productDataMap.containsKey(product_obj)) {
							productData = productDataMap.get(product_obj);
						} else {
							productData = new ProductReportData();
							productDataMap.put(product_obj, productData);
						}

						switch (interaction_obj.getInteractionType()) {
							case ProductInteraction.SALE_ACTIVATED_TYPE: {
								productData.num_sale_activated++;
								break;
							}
							case ProductInteraction.INVENTORY_TYPE: {
								productData.num_inventory++;
								break;
							}
							case ProductInteraction.SALE_TYPE: {
								productData.num_sale++;
								break;
							}
							case ProductInteraction.ASN_TYPE: {
								productData.num_asn++;
								break;
							}
							case ProductInteraction.RETURN_TYPE: {
								productData.num_return++;
								break;
							}
							case ProductInteraction.DEACTIVATED_CHARGEBACK_TYPE: {
								productData.num_deactivated_chargeback++;
								break;
							}
							case ProductInteraction.DEACTIVATED_OTHER_TYPE: {
								productData.num_deactivated_other++;
								break;
							}
						}
						
					}
					
					boolean needs_comma = false;
					itr = productList.iterator();
					while (itr.hasNext()) {
						Product product_obj = (Product)itr.next();
						System.out.println("PROC PROD >" + product_obj.getLabel());
						ProductReportData productData = productDataMap.get(product_obj);
						
						
						if (needs_comma) { b.append(","); }
						Vector vec = new Vector();
						vec.addElement(product_obj.getModelNumber());
						vec.addElement(product_obj.getNameString());
						vec.addElement(product_obj.getManufacturer().getLabel());
						vec.addElement(productData.num_asn + "");
						vec.addElement(productData.num_inventory + "");
						vec.addElement(productData.num_sale + "");
						vec.addElement(productData.num_sale_activated + "");
						vec.addElement(productData.num_return + "");
						vec.addElement(productData.num_deactivated_chargeback + "");
						vec.addElement(productData.num_deactivated_other + "");
						//vec.addElement(completion_date_str);
						b.append(this.toJSONResultRow( vec , product_obj.getId() ));
						numResults++;
						needs_comma = true;
					}
					
					break;
				}
				case IoTLOKReportTemplateBean.INTERACTION_REPORT_TYPE: {
					
					// get selected products from template
					
					Iterator itr = ProductInteraction.getInteractions(_template.getCompany(), null, courseReportLister.getProducts(), null, courseReportLister.getStartDate(), courseReportLister.getEndDate(), (short)0, 0).iterator();
					
					boolean needs_comma = false;
					while (itr.hasNext()) {
						
						/*
						Product product_obj = (Product)itr.next();
						System.out.println("PROC PROD >" + product_obj.getLabel());
						ProductReportData productData = productDataMap.get(product_obj);
						*/
						
						ProductInteraction interaction_obj = (ProductInteraction)itr.next();
						
						/*
						b.append("{\"name\":\"Product\",\"span\":3},");
						b.append("{\"name\":\"Serial #\",\"span\":2},");
						b.append("{\"name\":\"Date\",\"span\":1},");
						b.append("{\"name\":\"Time\",\"span\":\1},");
						b.append("{\"name\":\"Type\",\"span\":2},");
						b.append("{\"name\":\"Store\",\"span\":3},");
						*/
						
						if (needs_comma) { b.append(","); }
						Vector vec = new Vector();
						vec.addElement(interaction_obj.getProduct().getLabel());
						vec.addElement(interaction_obj.getUniqueIdentifier());
						vec.addElement(CUBean.getUserDateString(interaction_obj.getInteractionDate()));
						vec.addElement(CUBean.getUserTimeString(interaction_obj.getInteractionDate()));
						vec.addElement(interaction_obj.getInteractionTypeString());
						vec.addElement(interaction_obj.getStore().getLabel());
						//vec.addElement(completion_date_str);
						b.append(this.toJSONResultRow( vec , interaction_obj.getId(), interaction_obj.getProduct().getId() ));
						numResults++;
						needs_comma = true;
					}
					
					break;
				}
				case IoTLOKReportTemplateBean.SERIAL_NUMBER_REPORT_TYPE: {
					
					// get selected products from template
					
					Iterator itr = ProductInteraction.getLatestInteractions(_template.getCompany(), null, courseReportLister.getProducts(), null, courseReportLister.getStartDate(), courseReportLister.getEndDate(), (short)0, 0).iterator();
					
					
					boolean needs_comma = false;
					while (itr.hasNext()) {
						
						/*
						Product product_obj = (Product)itr.next();
						System.out.println("PROC PROD >" + product_obj.getLabel());
						ProductReportData productData = productDataMap.get(product_obj);
						*/
						
						ProductInteraction interaction_obj = (ProductInteraction)itr.next();
						
						/*
						b.append("{\"name\":\"Product\",\"span\":3},");
						b.append("{\"name\":\"Serial #\",\"span\":2},");
						b.append("{\"name\":\"Date\",\"span\":1},");
						b.append("{\"name\":\"Time\",\"span\":\1},");
						b.append("{\"name\":\"Type\",\"span\":2},");
						b.append("{\"name\":\"Store\",\"span\":3},");
						*/
						
						if (needs_comma) { b.append(","); }
						Vector vec = new Vector();
						vec.addElement(interaction_obj.getProduct().getLabel());
						vec.addElement(interaction_obj.getUniqueIdentifier());
						vec.addElement(CUBean.getUserDateString(interaction_obj.getInteractionDate()) + " " + CUBean.getUserTimeString(interaction_obj.getInteractionDate()));
						vec.addElement(interaction_obj.getInteractionTypeString());
						vec.addElement(interaction_obj.getStore().getLabel());

						String sitTimeStr = "";
						
						try {
							long millis = Product.calculateShelfSitTimeMillis(interaction_obj.getRetailer(), null, interaction_obj.getProduct(), interaction_obj.getUniqueIdentifier());

							long days = TimeUnit.MILLISECONDS.toDays(millis);
							long hours = TimeUnit.MILLISECONDS.toHours(millis - (days * 86400000l) );
							//long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
							//long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
							sitTimeStr = days + " day" + (days == 1 ? "" : "s") + ", " + hours + " hour" + (hours == 1 ? "" : "s");
						} catch (Exception x) {
							x.printStackTrace();
						}
						
						vec.addElement(sitTimeStr);
						//vec.addElement(completion_date_str);
						b.append(this.toJSONResultRow( vec , interaction_obj.getId(), interaction_obj.getProduct().getId() ));
						numResults++;
						needs_comma = true;
					}
					
					break;
				}
				case ReportTemplateBean.COURSE_HISTORY_REPORT_TYPE: {
					
					
					break;
				} case ReportTemplateBean.USER_REPORT_TYPE: {
					
					/*
					System.out.println("ReportTemplateBean.USER_REPORT_TYPE");
					
					Iterator aList = courseReportLister.getUsers().iterator();
					while (aList.hasNext()) {
						
						UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((com.badiyan.torque.Person)aList.next());
						
						if (needs_comma) { b.append(","); }
						Vector vec = new Vector();
						try {
							String emp_num_str = person.getEmployeeNumberString();
							Integer.parseInt(emp_num_str);
							vec.addElement(emp_num_str);
						} catch (Exception x) {
							vec.addElement("");
						}
						vec.addElement(person.getLabel());
						vec.addElement(person.getDepartmentNameString());
						vec.addElement(person.getGroupNameString());
						//vec.addElement(person.getEmailString());
						b.append(this.toJSONResultRow( vec , person.getId() ));
						numResults++;
						needs_comma = true;

					}
					*/
					
					break;
				}
				
			}
			
			
			
		}
		
		b.append("],");
		
		if (_template.isShowExcel() && _include_results) {
			b.append("\"excel\":\"" + JSONObject.escape(this.reportTemplateToExcel(_template)) + "\",");
		}
		
		b.append("\"numResults\":" + numResults + ",");
		
		b.append("\"id\":" + _template.getId() + "}");
		
		return b.toString();
	}
	
	private String toJSONAdminResource(CompanyBean _bu, ResourceBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		Vector groups = null;
		String resourceName = "";
		int resourceId = 0;
		int groupId = 0;
		String typeString = "";
		String descStr = "";
		boolean isActive = false;
		boolean isAssignment = false;
		String startStr = "";
		String endStr = "";
		int typeId = -1;
		String urlStr = "[NOT FOUND]";
		
		
		try {
			groups = CourseGroupBean.getCourseGroups(_bu);
			if (_obj != null) {
				resourceName = _obj.getNameString();
				resourceId = _obj.getId();
				groupId = _obj.getCourseGroupId();
				descStr = _obj.getDescription();
				isActive = _obj.isActive();
				urlStr = _obj.getURLString();
				typeString = _obj.getTypeString();
				
				startStr = _obj.getReleasedDateString();
				endStr = _obj.getExpirationDateString();
				
		
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"name\":\"" + JSONObject.escape(resourceName) + "\",");
		b.append("\"groupId\":" + groupId + ",");
		b.append("\"typeStr\":\"" + JSONObject.escape(typeString) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(descStr) + "\",");
		b.append("\"url\":\"" + JSONObject.escape(urlStr) + "\",");
		b.append("\"isActive\":" + isActive + ",");
		b.append("\"isAssignment\":" + isAssignment + ",");
		b.append("\"start\":\"" + JSONObject.escape(startStr) + "\",");
		b.append("\"end\":\"" + JSONObject.escape(endStr) + "\",");
		
		b.append("\"group\":[");
		if (groups != null) {
			boolean needs_comma = false;
			Iterator itr = groups.iterator();
			while (itr.hasNext()) {
				CourseGroupBean obj = (CourseGroupBean)itr.next();
				if (obj.isCourseOrLessonLevel()) {
					if (needs_comma) { b.append(","); }
					b.append(this.toJSONShort(obj));
					needs_comma = true;
				}
			}
		}
		b.append("],");
		
		b.append("\"id\":" + resourceId + "}");
		
		return b.toString();
	}
	
	private String toJSONShort(CourseGroupBean _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"id\":\"" + _obj.getId() + "\"}");
		return b.toString();
	}
	
	private String toJSONResultRow(Vector _vec, long _id) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_vec == null || _vec.isEmpty()) {
			return "";
		}
		
		Iterator itr = _vec.iterator();
		
		String c1 = (String)itr.next();
		
		/*
		String c1 = (String)itr.next();
		if (c1.length() > 45) {
			c1 = c1.substring(0, 44);
		}
		*/
		
		StringBuilder b = new StringBuilder();
		b.append("{\"c1\":\"" + JSONObject.escape(c1) + "\",");
		
		for (int i = 2; itr.hasNext(); i++) {
			b.append("\"c" + i + "\":\"" + JSONObject.escape((String)itr.next()) + "\",");
		}
		
		b.append("\"id\":" + _id + "}");
		
		return b.toString();
	}
	
	private String toJSONResultRow(Vector _vec, long _id, long _product_id) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_vec == null || _vec.isEmpty()) {
			return "";
		}
		
		Iterator itr = _vec.iterator();
		
		String c1 = (String)itr.next();
		
		StringBuilder b = new StringBuilder();
		b.append("{\"c1\":\"" + JSONObject.escape(c1) + "\",");
		
		for (int i = 2; itr.hasNext(); i++) {
			b.append("\"c" + i + "\":\"" + JSONObject.escape((String)itr.next()) + "\",");
		}
		b.append("\"productId\":" + _product_id + ",");
		b.append("\"id\":" + _id + "}");
		
		return b.toString();
	}
	
	/*
	private String toJSONInteractionDashboard(Product _product, CompanyBean _retailer, DepartmentBean _store, boolean _selected, Date _start_date, Date _end_date) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		
		lkljk;
		_product.calculateInteractionPercentages(_retailer, _store, _start_date, _end_date);
		
		BigDecimal numInventoryPercentage = _product.getPercentInventory(_retailer, _store);
		BigDecimal numSoldNotActivatedPercentage = _product.getPercentSold(_retailer, _store);
		BigDecimal numSoldAndActivatedPercentage = _product.getPercentActivated(_retailer, _store);
		BigDecimal numReturnedDeactivated = _product.getPercentReturned(_retailer, _store);
		BigDecimal numDeactivatedOther = _product.getPercentDeactivatedOther(_retailer, _store);
		
		StringBuffer b = new StringBuffer();
		b.append("{\"name\":\"" + JSONObject.escape(_product.getLabel()) + "\",");
		b.append("\"isActive\":" + _product.isActive() + ",");
		
		b.append("\"percInventory\":\"" + numInventoryPercentage.setScale(0, RoundingMode.HALF_UP).toString() + "\",");
		b.append("\"percSold\":\"" + numSoldNotActivatedPercentage.setScale(0, RoundingMode.HALF_UP).toString() + "\",");
		b.append("\"percActivated\":\"" + numSoldAndActivatedPercentage.setScale(0, RoundingMode.HALF_UP).toString() + "\",");
		b.append("\"percReturned\":\"" + numReturnedDeactivated.setScale(0, RoundingMode.HALF_UP).toString() + "\",");
		b.append("\"percDeactivatedOther\":\"" + numDeactivatedOther.setScale(0, RoundingMode.HALF_UP).toString() + "\",");
		
		b.append("\"numInventory\":" + _product.getNumInventory(_retailer, _store) + ",");
		b.append("\"numSold\":" + _product.getNumSold(_retailer, _store) + ",");
		b.append("\"numActivated\":" + _product.getNumActivated(_retailer, _store) + ",");
		b.append("\"numReturned\":" + _product.getNumReturned(_retailer, _store) + ",");
		b.append("\"numDeactivatedOther\":" + _product.getNumDeactivatedOther(_retailer, _store) + ",");
		
		b.append("\"isSelected\":" + _selected + ",");
		b.append("\"id\":\"" + _product.getId() + "\"}");
		
		return b.toString();
	}
	*/
	
	private String
	saveProduct(UKOnlinePersonBean _mod, String _product_id_str, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");			
			//_jsonBlob = "[{"name":"nameInput","value":"Another Test Resource"},{"name":"typeRadio","value":"Acrobat"},{"name":"descInput","value":"This is a test description"},{"name":"groupSelect","value":"5"},{"name":"statusRadio","value":"Active"},{"name":"startInput","value":"09/21/2015"},{"name":"endInput","value":"09/21/2016"}]";
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
		
		String nameInput = null;
		String descInput = null;
		String modelNumberInput = null;
		String mfgSelect = null;
		String upcInput = null;
		String statusRadio = null;
		
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
							if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("descInput")) {
								descInput = value_strX;
							} else if (last_value.equals("modelNumberInput")) {
								modelNumberInput = value_strX;
							} else if (last_value.equals("upcInput")) {
								upcInput = value_strX;
							} else if (last_value.equals("mfgSelect")) {
								mfgSelect = value_strX;
							} else if (last_value.equals("statusRadio")) {
								statusRadio = value_strX;
							}
						}
						
					}
				}
			}
		}

		Product product_obj = null;

		if ((nameInput == null) || nameInput.isEmpty()) {
			throw new IllegalValueException("Unable to save product.  Please provide a product name.");
		}
		if ((statusRadio == null) || statusRadio.isEmpty() || statusRadio.equals("0")) {
			throw new IllegalValueException("Unable to save status.  You must select a product status.");
		}

		if ((_product_id_str == null) || _product_id_str.isEmpty() || _product_id_str.equals("0")) {
			product_obj = new Product();
			product_obj.setCreatePerson(_mod);
			product_obj.setCreationDate(new Date());
		} else {
			product_obj = Product.getProduct(Long.parseLong(_product_id_str));
		}

		product_obj.setName(nameInput);
		product_obj.setDescription(descInput);
		product_obj.setModelNumber(modelNumberInput);
		product_obj.setUPC(upcInput);
		if (!((mfgSelect == null) || mfgSelect.isEmpty() || mfgSelect.equals("0"))) {
			product_obj.setManufacturer(CompanyBean.getCompany(Integer.parseInt(mfgSelect)));
		}
		product_obj.setActive(statusRadio.equals("Active"));
		
		product_obj.save();
		
		return "{\"message\":[{\"type\":\"SUCCESS\"}]}";
	}
	
	private String
	saveProductInteraction(UKOnlinePersonBean _mod, String _product_id_str, String _product_interaction_id_str, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");			
			//_jsonBlob = "[{"name":"nameInput","value":"Another Test Resource"},{"name":"typeRadio","value":"Acrobat"},{"name":"descInput","value":"This is a test description"},{"name":"groupSelect","value":"5"},{"name":"statusRadio","value":"Active"},{"name":"startInput","value":"09/21/2015"},{"name":"endInput","value":"09/21/2016"}]";
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
		
		String nameInput = null;
		String endpointHashInput = null;
		String typeSelect = null;
		
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
							if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("endpointHashInput")) {
								endpointHashInput = value_strX;
							} else if (last_value.equals("interactionTypeSelect")) {
								typeSelect = value_strX;
							}
						}
						
					}
				}
			}
		}

		System.out.println("0");
		

		if ((nameInput == null) || nameInput.isEmpty()) {
			throw new IllegalValueException("Unable to save product interaction.  Please provide a product serial number.");
		}
		/*
		if ((endpointHashInput == null) || endpointHashInput.isEmpty()) {
			throw new IllegalValueException("Unable to save product interaction.  Please provide an endpoint hash.");
		}
		*/
		if ((typeSelect == null) || typeSelect.isEmpty() || typeSelect.equals("0")) {
			throw new IllegalValueException("Unable to save product interaction.  You must select an interaction type.");
		}
		
		if ((_product_id_str == null) || _product_id_str.isEmpty() || _product_id_str.equals("0")) {
			throw new IllegalValueException("Unable to save product interaction.  You must specify a product.");
		}
		
		
		if ((_product_interaction_id_str == null) || _product_interaction_id_str.isEmpty() || _product_interaction_id_str.equals("0")) {
			ProductInteraction.getInteraction(_mod, Product.getProduct(Long.parseLong(_product_id_str)), nameInput, Short.parseShort(typeSelect), true);
		} else {
			ProductInteraction existing_interaction = ProductInteraction.getInteraction(Long.parseLong(_product_interaction_id_str));
			existing_interaction.setUniqueIdentifier(nameInput);
			existing_interaction.setInteractionType(Short.parseShort(typeSelect));
			existing_interaction.save();
		}
		
		System.out.println("C");
		
		
		return "{\"message\":[{\"type\":\"SUCCESS\"}]}";
	}
	
	private String
	saveEndpoint(UKOnlinePersonBean _mod, String _endpoint_id_str, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");			
			//_jsonBlob = "[{"name":"nameInput","value":"Another Test Resource"},{"name":"typeRadio","value":"Acrobat"},{"name":"descInput","value":"This is a test description"},{"name":"groupSelect","value":"5"},{"name":"statusRadio","value":"Active"},{"name":"startInput","value":"09/21/2015"},{"name":"endInput","value":"09/21/2016"}]";
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
		
		String serialNumberInput = null;
		String statusRadio = null;
		
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
							if (last_value.equals("serialNumberInput")) {
								serialNumberInput = value_strX;
							} else if (last_value.equals("statusRadio")) {
								statusRadio = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		Endpoint endpoint_obj = Endpoint.getEndpoint(Long.parseLong(_endpoint_id_str));
		boolean is_being_made_active = statusRadio.equals("Activated");
		boolean is_active_state_changed = (is_being_made_active != endpoint_obj.isActivated());
		
		System.out.println("is_being_made_active >" + is_being_made_active);
		System.out.println("is_active_state_changed >" + is_active_state_changed);

		endpoint_obj.setSerialNumber(serialNumberInput);
		endpoint_obj.setActive(is_being_made_active);
		endpoint_obj.save();
		
		
		if (is_active_state_changed) {
			/*
			updateServerProfile(endpoint_obj.getEndpointHash(), KAAAdminClientManager.SERVER_PROFILE_VERSION, is_being_made_active);
			*/
		}
		
		return "{\"message\":[{\"type\":\"SUCCESS\"}]}";
	}
	
	
	private String
	saveBusinessUnit(UKOnlinePersonBean _logged_in_person, CompanyBean _company, String _bu_id_str, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
			//_jsonBlob = "[{"name":"organizationInput","value":"Kraus Anderson"},{"name":"accountNumberInput","value":"[NONE]"}]";
					
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
		
		String nameInput = null;
		String accountNumberInput = null;
		boolean allowSelfRegistrationInput = false;
		
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
							if (last_value.equals("organizationInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("accountNumberInput")) {
								accountNumberInput = value_strX;
							}
						} else {
							if (last_value.equals("allowSelfRegistrationInput")) {
								allowSelfRegistrationInput = true;
							}
						}
						
					}
				}
			}
		}
		
		if ((nameInput == null) || nameInput.isEmpty()) {
			throw new IllegalValueException("Unable to save business unit.  Please provide an organization name.");
		}
		
		CompanyBean bu_obj = null;
		
		if ((_bu_id_str == null) || _bu_id_str.isEmpty() || _bu_id_str.equals("0")) {
			bu_obj = new CompanyBean();
		} else {
			bu_obj = CompanyBean.getCompany(Integer.parseInt(_bu_id_str));
		}
		
		OrganizationMainAction.saveCompany(bu_obj, nameInput, accountNumberInput, _logged_in_person);
		
		CompanySettingsBean settings = bu_obj.getSettings();
		settings.setAllowSelfRegistration(allowSelfRegistrationInput);
		settings.save();

		StringBuffer b = new StringBuffer();
		
		b.append("{\"message\":[{\"type\":\"SUCCESS\"}]}");
		
		return b.toString();
	}
	
	private String
	savePerson(UKOnlinePersonBean _logged_in_person, UKOnlinePersonBean _person_to_save, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
			
			//_jsonBlob = "[{\"name\":\"firstInput\",\"value\":\"SusanX\"},{\"name\":\"lastInput\",\"value\":\"Anderson\"},{\"name\":\"empIdInput\",\"value\":\"susan.anderson@krausanderson.com\"},{\"name\":\"emailInput\",\"value\":\"susan.anderson@krausanderson.com\"},{\"name\":\"usernameInput\",\"value\":\"susan.anderson@krausanderson.com\"},{\"name\":\"passwordInput\",\"value\":\"\"},{\"name\":\"confirmInput\",\"value\":\"\"},{\"name\":\"departmentSelect\",\"value\":\"27\"},{\"name\":\"titleSelect\",\"value\":\"78\"}]";
					
		}
		
		//System.out.println("_person_to_save >" + _person_to_save.getLabel());
		//System.out.println("_jsonBlob >" + _jsonBlob);
		
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
		
		String firstNameStr = "";
		String lastNameStr = "";
		String usernameStr = "";
		
		String raceSelect = "";
		String genderSelect = "";
		String disabilityInput = "";
		String commentInput = "";
		
		String phoneInput = "";
		
		boolean activeInput = false;
		
		Vector groups = new Vector();
		Vector audiences = new Vector();
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {

				String keyX = (String)itr.next();
				//System.out.println("      **keyX >" + keyX);
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					//System.out.println("      &&value_objX >" + value_strX);
					
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						//System.out.println("      **OK >");
						if (!value_strX.isEmpty()) {
							if (last_value.equals("firstInput")) {
								firstNameStr = value_strX;
							} else if (last_value.equals("lastInput")) {
								lastNameStr = value_strX;
							} else if (last_value.equals("empIdInput")) {
								_person_to_save.setEmployeeId(value_strX);
							} else if (last_value.equals("emailInput")) {
								_person_to_save.setEmail1(value_strX);
							} else if (last_value.equals("activeInput")) {
								activeInput = true;
							} else if (last_value.equals("altEmailInput")) {
								_person_to_save.setEmail2(value_strX);
							} else if (last_value.equals("usernameInput")) {
								usernameStr = value_strX;
							} else if (last_value.equals("passwordInput")) {
								_person_to_save.setPassword(value_strX);
							} else if (last_value.equals("confirmInput")) {
								_person_to_save.setConfirmPassword(value_strX);
							} else if (last_value.equals("departmentSelect")) {
								if (!value_strX.equals("0")) {
									_person_to_save.setDepartment(DepartmentBean.getDepartment(Integer.parseInt(value_strX)));
								}
							} else if (last_value.equals("titleSelect")) {
								if (!value_strX.equals("0")) {
									_person_to_save.setTitle(PersonTitleBean.getPersonTitle(Integer.parseInt(value_strX)));
								}
							} else if (last_value.equals("groupSelect")) {
								if (!value_strX.equals("0")) {
									groups.addElement(PersonGroupBean.getPersonGroup(Integer.parseInt(value_strX)));
								}
							} else if (last_value.equals("audienceSelect")) {
								if (!value_strX.equals("0")) {
									audiences.addElement(AudienceBean.getAudience(Integer.parseInt(value_strX)));
								}
							} else if (last_value.equals("raceSelect")) {
								raceSelect = value_strX;
							} else if (last_value.equals("genderSelect")) {
								genderSelect = value_strX;
							} else if (last_value.equals("disabilityInput")) {
								disabilityInput = value_strX;
							} else if (last_value.equals("commentInput")) {
								commentInput = value_strX;
							} else if (last_value.equals("phoneInput")) {
								phoneInput = value_strX;
							}
		
						} else {
							// might be trying to clear a value
							
							if (last_value.equals("altEmailInput")) { // allow clearing alt email
								_person_to_save.setEmail2("");
							}
						}
						
					}


				} else if (value_objX instanceof Boolean) {
					Boolean value_booleanX = (Boolean)value_objX;
					//System.out.println("      &&value_booleanX >" + value_booleanX);
					

				}
			}
		}
		
		if (firstNameStr.isEmpty()) {
			throw new IllegalValueException("Unable to save.  Please provide a First Name.");
		}
		if (lastNameStr.isEmpty()) {
			throw new IllegalValueException("Unable to save.  Please provide a Last Name.");
		}
		if (usernameStr.isEmpty()) {
			throw new IllegalValueException("Unable to save.  Please provide a User Name.");
		}
		
		_person_to_save.setFirstName(firstNameStr);
		_person_to_save.setLastName(lastNameStr);
		_person_to_save.setUsername(usernameStr);
		
		System.out.println("num groups >" + groups.size());
		
		_person_to_save.setGroups(groups);
		_person_to_save.setAudiences(audiences);
		
		_person_to_save.setActive(activeInput);
		
		_person_to_save.setComment(commentInput);
		
		_person_to_save.save();
		
		/*
		PersonSettingsBean person_settings = PersonSettingsBean.getPersonSettings(_person_to_save, true);
		person_settings.setDisability(disabilityInput);
		person_settings.setRace(raceSelect);
		person_settings.setGender(genderSelect);
		person_settings.save();
		
		if (phoneInput != null && !phoneInput.isEmpty()) {
			try {
				PhoneNumberBean.maintainDefaultData();
				PhoneNumberBean phone_number = PhoneNumberBean.getPhoneNumber(_person_to_save, PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
				phone_number.setNumber(phoneInput);
				phone_number.save();
			} catch (ObjectNotFoundException x) {
				PhoneNumberBean phone_number = new PhoneNumberBean();
				phone_number.setNumber(phoneInput);
				phone_number.setPerson(_person_to_save);
				phone_number.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
				phone_number.save();
			}
		}
		*/
		
		return "{\"message\":[{\"type\":\"SUCCESS\",\"id\":" + _person_to_save.getId() + "}]}";
	}
	
	private String
	savePersonRoles(UKOnlinePersonBean _logged_in_person, UKOnlinePersonBean _person_to_save, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
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
		
		Vector roles = new Vector();
		
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
							if (last_value.equals("roleSelect")) {
								roles.addElement(RoleBean.getRole(Integer.parseInt(value_strX)));
							}
						}
						
					}


				} else if (value_objX instanceof Boolean) {
					Boolean value_booleanX = (Boolean)value_objX;
					//System.out.println("      &&value_booleanX >" + value_booleanX);

				}
			}
		}
		System.out.println("roles >" + roles.size());
		_person_to_save.setRoles(roles);
		_person_to_save.save();
		
		_person_to_save.invalidate();
		

		StringBuffer b = new StringBuffer();
		
		b.append("{\"message\":[{\"type\":\"SUCCESS\"}]}");
		
		return b.toString();
	}
	
	private String
	saveDepartment(CompanyBean _company, String _department_id_str, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
			
			//_jsonBlob = "[{"name":"nameInput","value":"i am the door"},{"name":"parentSelect","value":"4"},{"name":"typeSelect","value":"1"}]";
					
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
		
		String nameInput = null;
		String parentSelect = null;
		String typeSelect = null;
		
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
							if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("parentSelect")) {
								parentSelect = value_strX;
							} else if (last_value.equals("typeSelect")) {
								typeSelect = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		if ((nameInput == null) || nameInput.isEmpty()) {
			throw new IllegalValueException("Unable to save department.  Please provide a department name.");
		}
		
		if ((typeSelect == null) || typeSelect.isEmpty() || typeSelect.equals("0")) {
			throw new IllegalValueException("Unable to save department.  You must select a department type.");
		}
		
		DepartmentBean department_obj = null;
		
		if ((_department_id_str == null) || _department_id_str.isEmpty() || _department_id_str.equals("0")) {
			department_obj = new DepartmentBean();
			department_obj.setCompany(_company);
		} else {
			department_obj = DepartmentBean.getDepartment(Integer.parseInt(_department_id_str));
		}
		
		department_obj.setName(nameInput);
		department_obj.setType(DepartmentTypeBean.getDepartmentType(Integer.parseInt(typeSelect)));
		
		if ((parentSelect != null) && !parentSelect.isEmpty() && !parentSelect.equals("0")) {
			department_obj.setParent(DepartmentBean.getDepartment(Integer.parseInt(parentSelect)));
		}
				
		department_obj.save();

		StringBuffer b = new StringBuffer();
		
		b.append("{\"message\":[{\"type\":\"SUCCESS\"}]}");
		
		return b.toString();
	}
	
	
	private String
	saveDepartmentType(CompanyBean _company, String _department_type_id_str, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
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
		
		String nameInput = null;
		
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
							if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							}
						}
					}
				}
			}
		}
		
		if ((nameInput == null) || nameInput.isEmpty()) {
			throw new IllegalValueException("Unable to save department type.  Please provide a type name.");
		}
		
		DepartmentTypeBean type_obj = null;
		
		if ((_department_type_id_str == null) || _department_type_id_str.isEmpty() || _department_type_id_str.equals("0")) {
			type_obj = new DepartmentTypeBean();
			type_obj.setCompany(_company);
		} else {
			type_obj = DepartmentTypeBean.getDepartmentType(Integer.parseInt(_department_type_id_str));
		}
		
		type_obj.setName(nameInput);
		type_obj.save();

		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\"}]}");
		return b.toString();
	}
	
	
	private String
	saveTitle(CompanyBean _company, String _title_id_str, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
			
			//_jsonBlob = "[{"name":"nameInput","value":"BIM Specialist"},{"name":"shortNameInput","value":"some short name"},{"name":"roleSelect","value":"7"},{"name":"roleSelect","value":"5"},{"name":"roleSelect","value":"11"}]";
					
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
		
		String nameInput = null;
		String shortInput = null;
		Vector mappedRoles = new Vector();
		
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
							if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("shortNameInput")) {
								System.out.println("shortNameInput >" + value_strX);
								shortInput = value_strX;
							} else if (last_value.equals("roleSelect")) {
								System.out.println("roleSelect >" + value_strX);
								mappedRoles.addElement(RoleBean.getRole(Integer.parseInt(value_strX)));
							}
						}
						
					}
				}
			}
		}
		
		if ((nameInput == null) || nameInput.isEmpty()) {
			throw new IllegalValueException("Unable to save job title.  Please provide a title name.");
		}
		
		PersonTitleBean person_title_obj = null;
		
		if ((_title_id_str == null) || _title_id_str.isEmpty() || _title_id_str.equals("0")) {
			person_title_obj = new PersonTitleBean();
			person_title_obj.setCompany(_company);
		} else {
			person_title_obj = PersonTitleBean.getPersonTitle(Integer.parseInt(_title_id_str));
		}
		
		person_title_obj.setName(nameInput);
		person_title_obj.setShortNameString(shortInput);
		person_title_obj.setRoles(mappedRoles);
		person_title_obj.save();

		StringBuffer b = new StringBuffer();
		
		b.append("{\"message\":[{\"type\":\"SUCCESS\"}]}");
		
		return b.toString();
	}
	
	
	private String
	saveGroup(CompanyBean _company, String _group_id_str, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
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
		
		String nameInput = null;
		
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
							if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							}
						}
					}
				}
			}
		}
		
		if ((nameInput == null) || nameInput.isEmpty()) {
			throw new IllegalValueException("Unable to save person group.  Please provide a group name.");
		}
		
		PersonGroupBean group_obj = null;
		
		if ((_group_id_str == null) || _group_id_str.isEmpty() || _group_id_str.equals("0")) {
			group_obj = new PersonGroupBean();
			group_obj.setCompany(_company);
		} else {
			group_obj = PersonGroupBean.getPersonGroup(Integer.parseInt(_group_id_str));
		}
		
		group_obj.setName(nameInput);
		group_obj.save();

		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\"}]}");
		return b.toString();
	}
	
	
	private String
	saveAudience(CompanyBean _company, PersonBean _mod_person, String _aud_id_str, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
			// [{"name":"nameInput","value":"A Delete Aud"},{"name":"departmentSelect","value":"3"},{"name":"departmentSelect","value":"5"},{"name":"titleSelect","value":"20"},{"name":"titleSelect","value":"50"},{"name":"groupSelect","value":"2"}]
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
		
		String nameInput = null;
		Vector departmentSelect = new Vector();
		Vector titleSelect = new Vector();
		Vector groupSelect = new Vector();
		
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
							if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("departmentSelect")) {
								DepartmentBean dept_obj = DepartmentBean.getDepartment(Integer.parseInt(value_strX));
								departmentSelect.addElement(dept_obj);
								
								// also add the children

								Iterator children_itr = dept_obj.getChildren().iterator();
								while (children_itr.hasNext()) {
									DepartmentBean child_obj = (DepartmentBean)children_itr.next();
									if (!departmentSelect.contains(child_obj)) {
										departmentSelect.addElement(child_obj);
									}
								}
								
							} else if (last_value.equals("titleSelect")) {
								titleSelect.addElement(PersonTitleBean.getPersonTitle(Integer.parseInt(value_strX)));
							} else if (last_value.equals("groupSelect")) {
								groupSelect.addElement(PersonGroupBean.getPersonGroup(Integer.parseInt(value_strX)));
							}
						}
					}
				}
			}
		}
		
		if ((nameInput == null) || nameInput.isEmpty()) {
			throw new IllegalValueException("Unable to save audience.  Please provide an audience name.");
		}
		
		AudienceBean audience_obj = null;
		
		if ((_aud_id_str == null) || _aud_id_str.isEmpty() || _aud_id_str.equals("0")) {
			audience_obj = new AudienceBean();
			audience_obj.setCompany(_company);
			audience_obj.setCreatePerson(_mod_person);
		} else {
			audience_obj = AudienceBean.getAudience(Integer.parseInt(_aud_id_str));
		}
		
		if (departmentSelect.isEmpty()) {
			audience_obj.setAssociateAllDepartments(true);
		} else {
			audience_obj.setAssociateAllDepartments(false);
			audience_obj.setDepartments(departmentSelect);
		}
		
		if (titleSelect.isEmpty()) {
			audience_obj.setAssociateAllPersonTitles(true);
		} else {
			audience_obj.setAssociateAllPersonTitles(false);
			audience_obj.setPersonTitles(titleSelect);
		}
		
		if (groupSelect.isEmpty()) {
			audience_obj.setAssociateAllPersonGroups(true);
		} else {
			audience_obj.setAssociateAllPersonGroups(false);
			audience_obj.setPersonGroups(groupSelect);
		}
		
		audience_obj.setName(nameInput);
		audience_obj.save();

		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\"}]}");
		return b.toString();
	}
	
	private Product
	mineProduct(UKOnlinePersonBean _mod, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");			
			//_jsonBlob = "{\"code\":\"OK\",\"total\":1,\"offset\":0,\"items\":[{\"ean\":\"0711719503309\",\"title\":\"PlayStation 4 500GB Console - Uncharted: The Nathan Drake Collection Bundle (Physical Disc)\",\"upc\":\"711719503309\",\"description\":\"The UNCHARTED: The Nathan Drake Collection PS4&trade; Bundle Play through three critically-acclaimed adventures. Discover the epic story that made Drake a legend in the single player campaigns for UNCHARTED: Drake's Fortune, UNCHARTED 2: Among Thieves, and UNCHARTED 3: Drake's Deception, all re-mastered with the power of your PS4&trade; system. Relive The Legend: Experience one of the most revered game series of all time as you follow the perilous journey of Nathan Drake across the globe, from humble begin...\",\"brand\":\"Sony\",\"model\":\"10034\",\"dimension\":\"\",\"weight\":\"\",\"currency\":\"\",\"lowest_recorded_price\":179,\"images\":[\"http://img1.r10.io/PIC/102230001/0/1/250/102230001.jpg\",\"http://img.bbystatic.com/BestBuy_US/images/products/4512/4512004_sc.jpg\",\"http://scene7.targetimg1.com/is/image/Target/49174644?wid=1000&hei=1000\",\"http://ecx.images-amazon.com/images/I/41IGH4cD61L._SL160_.jpg\",\"http://images.prosperentcdn.com/images/250x250/www.pcrichard.com/images/product/detail/D_3001362.jpg\",\"http://images.prosperentcdn.com/images/250x250/content.abt.com/media/images/products/l_3001169.jpg\",\"http://scene7.samsclub.com/is/image/samsclub/0071171950247_A?$img_size_211x208$\",\"http://ecx.images-amazon.com/images/I/41IGH4cD61L._SL160_.jpg\",\"http://www.techforless.com/cimages/276451.JPG\",\"http://ecx.images-amazon.com/images/I/41IGH4cD61L._SL160_.jpg\"],\"offers\":[{\"merchant\":\"Amazon Marketplace Used\",\"title\":\"PlayStation 4 500GB Console - Uncharted: The Nathan Drake Collection Bundle (Physical Disc)\",\"currency\":\"\",\"list_price\":\"\",\"price\":255.32,\"shipping\":\"Free Shipping\",\"condition\":\"Used\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t2y2t2x2y29444s2&tid=1&seq=1457451583&plt=6bdd4a5b2794c1857b682f194b405076\",\"updated_t\":1457425559},{\"merchant\":\"Tech For Less\",\"title\":\"Sony 3001362 UNCHARTED: The Nathan Drake Collection PS4 Bundle - Game Pad Supported - Wireless - Black - ATI Radeon - Blu-ray Disc Player - 500 GB HDD - Gigabit Ethernet - Bluetooth - Wireless LAN - HDMI - USB - Octa-core (8 Core)\",\"currency\":\"\",\"list_price\":349.99,\"price\":300.97,\"shipping\":\"Free Shipping\",\"condition\":\"New\",\"availability\":\"Out of Stock\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t253y2v2236444z2&tid=1&seq=1457451583&plt=5abf3d05a72549051a83d65e9da2b00d\",\"updated_t\":1455865763},{\"merchant\":\"Amazon Marketplace New\",\"title\":\"PlayStation 4 500GB Console - Uncharted: The Nathan Drake Collection Bundle (Physical Disc)\",\"currency\":\"\",\"list_price\":\"\",\"price\":335.95,\"shipping\":\"Free Shipping\",\"condition\":\"New\",\"availability\":\"Out of Stock\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t2y2t2x2x27454x2&tid=1&seq=1457451583&plt=6f5348e602501588efbbd2e522cb7143\",\"updated_t\":1457425559},{\"merchant\":\"Sam's Club\",\"title\":\"PlayStation 4 500GB Uncharted: The Nathan Drake Collection Console Bundle\",\"currency\":\"\",\"list_price\":\"\",\"price\":349,\"shipping\":\"\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s25323z2x274a4v2&tid=1&seq=1457451583&plt=af2e58d852e73ad26dbd0adcc01ff295\",\"updated_t\":1447948771},{\"merchant\":\"Abt\",\"title\":\"Sony PlayStation 4 Uncharted: The Nathan Drake Collection Bundle\",\"currency\":\"\",\"list_price\":\"\",\"price\":349,\"shipping\":\"\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t2y2u2x2z2e4d4t2&tid=1&seq=1457451583&plt=cb9b4270ab0e29f9f15502109f15915f\",\"updated_t\":1456105796},{\"merchant\":\"PC Richard & Son\",\"title\":\"PlayStation 4 500 GB Uncharted: The Nathan Drake Collection\",\"currency\":\"\",\"list_price\":\"\",\"price\":349.96,\"shipping\":\"\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s23323135364b4r2&tid=1&seq=1457451583&plt=f14a6716bfdc97e8a8f3762caf42ebab\",\"updated_t\":1456107310},{\"merchant\":\"Amazon.com\",\"title\":\"PlayStation 4 500GB Console - Uncharted: The Nathan Drake Collection Bundle (Physical Disc)\",\"currency\":\"\",\"list_price\":\"\",\"price\":349.99,\"shipping\":\"Free Shipping\",\"condition\":\"New\",\"availability\":\"Out of Stock\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t2x20303538444r2&tid=1&seq=1457451583&plt=ec2798c375269d0299e64c8e3474a7db\",\"updated_t\":1456913814},{\"merchant\":\"Target\",\"title\":\"PlayStation 4 500GB Uncharted: The Nathan Drake Collection Bundle 10034\",\"currency\":\"\",\"list_price\":\"\",\"price\":349.99,\"shipping\":\"\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s233u21333f444s2&tid=1&seq=1457451583&plt=6bdd96a6b6b72032680a5b2433ee4c56\",\"updated_t\":1457408715},{\"merchant\":\"Best Buy\",\"title\":\"Sony - Playstation 4 500gb Uncharted: The Nathan Drake Collection Bundle - Black\",\"currency\":\"\",\"list_price\":\"\",\"price\":349.99,\"shipping\":\"\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s233v2y2237444u2&tid=1&seq=1457451583&plt=eeb771191c449f2f637814692aedff23\",\"updated_t\":1456611738},{\"merchant\":\"Rakuten(Buy.com)\",\"title\":\"PS4 500GB HW Bndl Uncharted\",\"currency\":\"\",\"list_price\":\"\",\"price\":359.99,\"shipping\":\"Free Shipping\",\"condition\":\"New\",\"availability\":\"Out of Stock\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s23303x2439494q2&tid=1&seq=1457451583&plt=064963676f26d631fe78ccb35c679052\",\"updated_t\":1452530823},{\"merchant\":\"Wal-Mart.com\",\"title\":\"Sony PlayStation 4 - Uncharted: The Nathan Drake Collection Bundle - game console - 500 GB HDD - jet black\",\"currency\":\"\",\"list_price\":\"\",\"price\":408.99,\"shipping\":\"25.04\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s243x243x2b454w2&tid=1&seq=1457451583&plt=593c9579710b295338b96dbaa01fb7fd\",\"updated_t\":1448582801},{\"merchant\":\"QVC.com\",\"title\":\"Sony PS4 500GB Uncharted: The Nathan Drake Collection Bundle\",\"currency\":\"\",\"list_price\":\"\",\"price\":424.96,\"shipping\":\"14.72\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t203t2z263e474v2&tid=1&seq=1457451583&plt=87f9af4388d1318e6b2474ed3a77e86c\",\"updated_t\":1457240790}]}]}";
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
		
		String code = null;
		int total = 0;
		int offset = 0;
		
		Vector images = new Vector();
		Product product_obj = new Product();
		
		
		LinkedHashMap linked_hash_map = (LinkedHashMap)parser.parse(_jsonBlob, containerFactory);
		Iterator itr = linked_hash_map.keySet().iterator();
		while (itr.hasNext()) {
			String keyX = (String)itr.next();
			Object value_objX = linked_hash_map.get(keyX);
			
			System.out.println("keyX >" + keyX);
			System.out.println("value_objX >" + value_objX);
			
			if (keyX.equals("items")) {
				
				LinkedList items = (LinkedList)value_objX;
				Iterator item_itr = items.iterator();
				while (item_itr.hasNext()) {
					
					LinkedHashMap item_hash_map = (LinkedHashMap)item_itr.next();
					Iterator itrx = item_hash_map.keySet().iterator();
					while (itrx.hasNext()) {
						String item_key = (String)itrx.next();
						Object item_value = item_hash_map.get(item_key);

						System.out.println("   item_key >" + item_key);
						System.out.println("   item_value >" + item_value);
						
						if (item_key.equals("ean")) {
							product_obj.setEAN((String)item_value);
						} else if (item_key.equals("title")) {
							product_obj.setName((String)item_value);
						} else if (item_key.equals("upc")) {
							product_obj.setUPC((String)item_value);
						} else if (item_key.equals("description")) {
							product_obj.setDescription((String)item_value);
						} else if (item_key.equals("brand")) {
							CompanyBean mfg = CompanyBean.maintainCompany((String)item_value, "Mfg");
							product_obj.setManufacturer(mfg);
						} else if (item_key.equals("model")) {
							product_obj.setModelNumber((String)item_value);
						} else if (item_key.equals("images")) {
							LinkedList image_list = (LinkedList)item_value;
							Iterator image_itr = image_list.iterator();
							while (image_itr.hasNext()) {
								String image_str = (String)image_itr.next();
								System.out.println("      image >" + image_str);
								if (!images.contains(image_str)) {
									images.addElement(image_str);
								}
							}
						}
					}
					
				}
				
			} else if (value_objX instanceof String) {
				String value_strX = (String)value_objX;
				if (keyX.equals("code")) {
					code = value_strX;
				}
			} else if (value_objX instanceof Integer) {
				Integer value_strX = (Integer)value_objX;
				if (keyX.equals("total")) {
					total = value_strX;
					if (total > 1) {
						throw new IllegalValueException("Multiple products found.");
					}
				} else if (keyX.equals("offset")) {
					offset = value_strX;
				}
			}
		}
		
		product_obj.setImages(images);
		product_obj.setCreatePerson(_mod);
		product_obj.setCreationDate(new Date());
		
		return product_obj;
	}
	
	private Endpoint
	mineEndpoint(String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");			
			//_jsonBlob = "{\"code\":\"OK\",\"total\":1,\"offset\":0,\"items\":[{\"ean\":\"0711719503309\",\"title\":\"PlayStation 4 500GB Console - Uncharted: The Nathan Drake Collection Bundle (Physical Disc)\",\"upc\":\"711719503309\",\"description\":\"The UNCHARTED: The Nathan Drake Collection PS4&trade; Bundle Play through three critically-acclaimed adventures. Discover the epic story that made Drake a legend in the single player campaigns for UNCHARTED: Drake's Fortune, UNCHARTED 2: Among Thieves, and UNCHARTED 3: Drake's Deception, all re-mastered with the power of your PS4&trade; system. Relive The Legend: Experience one of the most revered game series of all time as you follow the perilous journey of Nathan Drake across the globe, from humble begin...\",\"brand\":\"Sony\",\"model\":\"10034\",\"dimension\":\"\",\"weight\":\"\",\"currency\":\"\",\"lowest_recorded_price\":179,\"images\":[\"http://img1.r10.io/PIC/102230001/0/1/250/102230001.jpg\",\"http://img.bbystatic.com/BestBuy_US/images/products/4512/4512004_sc.jpg\",\"http://scene7.targetimg1.com/is/image/Target/49174644?wid=1000&hei=1000\",\"http://ecx.images-amazon.com/images/I/41IGH4cD61L._SL160_.jpg\",\"http://images.prosperentcdn.com/images/250x250/www.pcrichard.com/images/product/detail/D_3001362.jpg\",\"http://images.prosperentcdn.com/images/250x250/content.abt.com/media/images/products/l_3001169.jpg\",\"http://scene7.samsclub.com/is/image/samsclub/0071171950247_A?$img_size_211x208$\",\"http://ecx.images-amazon.com/images/I/41IGH4cD61L._SL160_.jpg\",\"http://www.techforless.com/cimages/276451.JPG\",\"http://ecx.images-amazon.com/images/I/41IGH4cD61L._SL160_.jpg\"],\"offers\":[{\"merchant\":\"Amazon Marketplace Used\",\"title\":\"PlayStation 4 500GB Console - Uncharted: The Nathan Drake Collection Bundle (Physical Disc)\",\"currency\":\"\",\"list_price\":\"\",\"price\":255.32,\"shipping\":\"Free Shipping\",\"condition\":\"Used\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t2y2t2x2y29444s2&tid=1&seq=1457451583&plt=6bdd4a5b2794c1857b682f194b405076\",\"updated_t\":1457425559},{\"merchant\":\"Tech For Less\",\"title\":\"Sony 3001362 UNCHARTED: The Nathan Drake Collection PS4 Bundle - Game Pad Supported - Wireless - Black - ATI Radeon - Blu-ray Disc Player - 500 GB HDD - Gigabit Ethernet - Bluetooth - Wireless LAN - HDMI - USB - Octa-core (8 Core)\",\"currency\":\"\",\"list_price\":349.99,\"price\":300.97,\"shipping\":\"Free Shipping\",\"condition\":\"New\",\"availability\":\"Out of Stock\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t253y2v2236444z2&tid=1&seq=1457451583&plt=5abf3d05a72549051a83d65e9da2b00d\",\"updated_t\":1455865763},{\"merchant\":\"Amazon Marketplace New\",\"title\":\"PlayStation 4 500GB Console - Uncharted: The Nathan Drake Collection Bundle (Physical Disc)\",\"currency\":\"\",\"list_price\":\"\",\"price\":335.95,\"shipping\":\"Free Shipping\",\"condition\":\"New\",\"availability\":\"Out of Stock\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t2y2t2x2x27454x2&tid=1&seq=1457451583&plt=6f5348e602501588efbbd2e522cb7143\",\"updated_t\":1457425559},{\"merchant\":\"Sam's Club\",\"title\":\"PlayStation 4 500GB Uncharted: The Nathan Drake Collection Console Bundle\",\"currency\":\"\",\"list_price\":\"\",\"price\":349,\"shipping\":\"\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s25323z2x274a4v2&tid=1&seq=1457451583&plt=af2e58d852e73ad26dbd0adcc01ff295\",\"updated_t\":1447948771},{\"merchant\":\"Abt\",\"title\":\"Sony PlayStation 4 Uncharted: The Nathan Drake Collection Bundle\",\"currency\":\"\",\"list_price\":\"\",\"price\":349,\"shipping\":\"\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t2y2u2x2z2e4d4t2&tid=1&seq=1457451583&plt=cb9b4270ab0e29f9f15502109f15915f\",\"updated_t\":1456105796},{\"merchant\":\"PC Richard & Son\",\"title\":\"PlayStation 4 500 GB Uncharted: The Nathan Drake Collection\",\"currency\":\"\",\"list_price\":\"\",\"price\":349.96,\"shipping\":\"\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s23323135364b4r2&tid=1&seq=1457451583&plt=f14a6716bfdc97e8a8f3762caf42ebab\",\"updated_t\":1456107310},{\"merchant\":\"Amazon.com\",\"title\":\"PlayStation 4 500GB Console - Uncharted: The Nathan Drake Collection Bundle (Physical Disc)\",\"currency\":\"\",\"list_price\":\"\",\"price\":349.99,\"shipping\":\"Free Shipping\",\"condition\":\"New\",\"availability\":\"Out of Stock\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t2x20303538444r2&tid=1&seq=1457451583&plt=ec2798c375269d0299e64c8e3474a7db\",\"updated_t\":1456913814},{\"merchant\":\"Target\",\"title\":\"PlayStation 4 500GB Uncharted: The Nathan Drake Collection Bundle 10034\",\"currency\":\"\",\"list_price\":\"\",\"price\":349.99,\"shipping\":\"\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s233u21333f444s2&tid=1&seq=1457451583&plt=6bdd96a6b6b72032680a5b2433ee4c56\",\"updated_t\":1457408715},{\"merchant\":\"Best Buy\",\"title\":\"Sony - Playstation 4 500gb Uncharted: The Nathan Drake Collection Bundle - Black\",\"currency\":\"\",\"list_price\":\"\",\"price\":349.99,\"shipping\":\"\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s233v2y2237444u2&tid=1&seq=1457451583&plt=eeb771191c449f2f637814692aedff23\",\"updated_t\":1456611738},{\"merchant\":\"Rakuten(Buy.com)\",\"title\":\"PS4 500GB HW Bndl Uncharted\",\"currency\":\"\",\"list_price\":\"\",\"price\":359.99,\"shipping\":\"Free Shipping\",\"condition\":\"New\",\"availability\":\"Out of Stock\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s23303x2439494q2&tid=1&seq=1457451583&plt=064963676f26d631fe78ccb35c679052\",\"updated_t\":1452530823},{\"merchant\":\"Wal-Mart.com\",\"title\":\"Sony PlayStation 4 - Uncharted: The Nathan Drake Collection Bundle - game console - 500 GB HDD - jet black\",\"currency\":\"\",\"list_price\":\"\",\"price\":408.99,\"shipping\":\"25.04\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2s243x243x2b454w2&tid=1&seq=1457451583&plt=593c9579710b295338b96dbaa01fb7fd\",\"updated_t\":1448582801},{\"merchant\":\"QVC.com\",\"title\":\"Sony PS4 500GB Uncharted: The Nathan Drake Collection Bundle\",\"currency\":\"\",\"list_price\":\"\",\"price\":424.96,\"shipping\":\"14.72\",\"condition\":\"New\",\"availability\":\"\",\"link\":\"http://www.upcitemdb.com/norob/alink/?id=u2t203t2z263e474v2&tid=1&seq=1457451583&plt=87f9af4388d1318e6b2474ed3a77e86c\",\"updated_t\":1457240790}]}]}";
		}
		
		Endpoint minedEndpoint = null;
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String code = null;
		int total = 0;
		int offset = 0;
		
		Vector images = new Vector();
		Product product_obj = new Product();
		
		
		LinkedHashMap linked_hash_map = (LinkedHashMap)parser.parse(_jsonBlob, containerFactory);
		Iterator itr = linked_hash_map.keySet().iterator();
		while (itr.hasNext()) {
			String keyX = (String)itr.next();
			Object value_objX = linked_hash_map.get(keyX);
			
			System.out.println("keyX >" + keyX);
			System.out.println("value_objX >" + value_objX);
			
			if (keyX.equals("tag")) {
				
				java.util.LinkedHashMap tag_map = (java.util.LinkedHashMap)value_objX;
				Iterator tag_itr = tag_map.keySet().iterator();
				while (tag_itr.hasNext()) {
					String keyY = (String)tag_itr.next();
					String endpoint_hash_str = (String)tag_map.get(keyY);
					
					System.out.println("   keyY >" + keyY);
					System.out.println("   endpoint_hash_str >" + endpoint_hash_str);
					
					minedEndpoint = Endpoint.getEndpointByEndpointHash(endpoint_hash_str);
				
				}
				
			} else if (keyX.equals("message")) {
				
				java.util.LinkedHashMap message_map = (java.util.LinkedHashMap)value_objX;
				Iterator message_itr = message_map.keySet().iterator();
				while (message_itr.hasNext()) {
					String keyY = (String)message_itr.next();
					String value_objY = (String)message_map.get(keyY);
					
					System.out.println("   keyY >" + keyY);
					System.out.println("   value_objY >" + value_objY);
				
				}
			}
		}
		
		/*
		product_obj.setImages(images);
		product_obj.setCreatePerson(_mod);
		product_obj.setCreationDate(new Date());
		
		return product_obj;
		*/
		
		return minedEndpoint;
	}
	
	
	
	

    /**
     * Retrieve all endpoint profiles associated with activation application
     *
     * @return endpoint profiles associated with activation application
     */
    private static Map<String, EndpointProfileDto> retrieveEndpointProfiles() {
        KAAAdminClientManager clientManager = KAAAdminClientManager.instance();
		//List<EndpointGroupDto> endpointGroups = clientManager.getEndpointGroupsByApplicationName(KAA_APPLICATION_NAME);
		return clientManager.getEndpointProfilesForGroupID(KAAAdminClientManager.ALL_GROUP_ID);
		
		/*
		if (endpointGroups == null) {
			return new HashMap<>();
		} else {
			return clientManager.getEndpointProfiles(endpointGroups);
		}
		*/

		// not sure why this wasn't compiling...
        //return endpointGroups != null ? clientManager.getEndpointProfiles(endpointGroups) : new HashMap<>();
		//
    }
	
    private static Map<String, EndpointProfileDto> retrieveActiveEndpointProfiles() {
        KAAAdminClientManager clientManager = KAAAdminClientManager.instance();
        return clientManager.getEndpointProfilesForGroupID(KAAAdminClientManager.ACTIVE_GROUP_ID);
    }
	
    private static Map<String, EndpointProfileDto> retrieveInactiveEndpointProfiles() {
        KAAAdminClientManager clientManager = KAAAdminClientManager.instance();
        return clientManager.getEndpointProfilesForGroupID(KAAAdminClientManager.INACTIVE_GROUP_ID);
    }


    /**
     * Output all available endpoint profiles associated to activation
     * application
     *
     * @param endpointProfiles
     *            endpoint profiles associated to activation application
     */
    private static void printEndpointProfiles(Map<String, EndpointProfileDto> endpointProfiles) {
        LOG.info("Endpoint profiles: ");
        for (Map.Entry<String, EndpointProfileDto> entry : endpointProfiles.entrySet()) {
            EndpointProfileDto endpointProfile = entry.getValue();
			System.out.println("getServerProfileBody >" + endpointProfile.getServerProfileBody());
            String endpointKeyHash = Base64.getEncoder().encodeToString(endpointProfile.getEndpointKeyHash());
			//String endpointKeyHash = "[NOT FOUND]";
            boolean isActive = KAADeviceState.parseJsonString(endpointProfile.getServerProfileBody());
            LOG.info("Profile id: " + entry.getKey() + ", endpointHash: " + endpointKeyHash + ", device state: " + (isActive ? "active" : "inactive") );
        }
    }

    /**
     * Update the server profile object using REST API
     *
     * @param endpointProfile
     *            the endpointProfileDto object
     */
    public static void updateServerProfile(EndpointProfileDto endpointProfile) {
        LOG.info("Update server profile >" + endpointProfile);
        String profileBody = endpointProfile.getServerProfileBody();
        boolean isActive = KAADeviceState.parseJsonString(profileBody);
        int version = endpointProfile.getServerProfileVersion();
        String endpointKeyHash = Base64.getEncoder().encodeToString(endpointProfile.getEndpointKeyHash());
		//String endpointKeyHash = "[NOT FOUND]";
        updateServerProfile(endpointKeyHash, version, !isActive);
    }

    /**
     * Update the server profile object using REST API
     *
     * @param endpointKeyHash
     *            the endpointKeyHash
     * @param profileVersion
     *            the server profile version
     * @param newState
     *            new device state
     */
    public static void updateServerProfile(String endpointKeyHash, int profileVersion, boolean newState) {
        LOG.info("Update server profile >" + endpointKeyHash + ", newState >" + newState);
        KAAAdminClientManager clientManager = KAAAdminClientManager.instance();
        clientManager.updateServerProfile(endpointKeyHash, profileVersion, KAADeviceState.toJsonString(newState, null, null));
    }
	
    public static void updateServerProfile(String endpointKeyHash, int profileVersion, boolean newState, String _message) {
        LOG.info("Update server profile >" + endpointKeyHash + ", newState >" + newState + ", _message >" + _message);
        KAAAdminClientManager clientManager = KAAAdminClientManager.instance();
		String update_json = KAADeviceState.toJsonString(newState, null, _message);
		System.out.println("update_json >" + update_json);
        clientManager.updateServerProfile(endpointKeyHash, profileVersion, update_json);
    }
	
	private void
	startKAAMonitorTask() {
		
		/*
		System.out.println("startKAAMonitorTask() invoked");
		if (timerObj == null) {
			timerObj = new Timer(true);
			
			Date now = new Date();
			int seconds = 1000;
			System.out.println("starting task");
			timerObj.schedule(new KAAAdminTask(), now.getTime(), 20 * seconds); // 20 seconds
		}
		*/
	}
		
	private IoTLOKReportTemplateBean
	processReportTemplate(UKOnlinePersonBean _logged_in_person, CompanyBean _company, String _template_id_str, String _jsonBlob, boolean _save_only) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");			
			//_jsonBlob = "[{"name":"nameInput","value":"dfgdfg"},{"name":"reportSelect","value":"1"},{"name":"statusSelect","value":"1"},{"name":"courseSelect","value":"8"},{"name":"courseSelect","value":"10"},{"name":"departmentSelect","value":"8"},{"name":"departmentSelect","value":"10"},{"name":"titleSelect","value":"20"},{"name":"titleSelect","value":"50"},{"name":"groupSelect","value":"2"},{"name":"audienceSelect","value":"2"},{"name":"audienceSelect","value":"4"},{"name":"startInput","value":"11/15/2015"},{"name":"endInput","value":"11/15/2015"},{"name":"generateExcelInput","value":""},{"name":"inactivesInput","value":""}]";
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
		
		String nameInput = null;
		String reportSelect = null;
		String statusSelect = null;
		Vector mfgSelect = new Vector();
		Vector productSelect = new Vector();
		Vector departmentSelect = new Vector();
		Vector titleSelect = new Vector();
		Vector groupSelect = new Vector();
		Vector audienceSelect = new Vector();
		Vector certificationSelect = new Vector();
		Date startInput = null;
		Date endInput = null;
		boolean generateExcelInput = false;
		boolean inactivesInput = false;
		
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
							if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("reportSelect")) {
								reportSelect = value_strX;
							} else if (last_value.equals("statusSelect")) {
								statusSelect = value_strX;
							} else if (last_value.equals("mfgSelect")) {
								if (!value_strX.equals("0")) {
									mfgSelect.addElement(CompanyBean.getCompany(Integer.parseInt(value_strX)));
								}
							} else if (last_value.equals("productSelect")) {
								if (!value_strX.equals("0")) {
									productSelect.addElement(Product.getProduct(Long.parseLong(value_strX)));
								}
							} else if (last_value.equals("departmentSelect")) {
								if (!value_strX.equals("0")) {
									departmentSelect.addElement(DepartmentBean.getDepartment(Integer.parseInt(value_strX)));
								}
							} else if (last_value.equals("titleSelect")) {
								if (!value_strX.equals("0")) {
									titleSelect.addElement(PersonTitleBean.getPersonTitle(Integer.parseInt(value_strX)));
								}
							} else if (last_value.equals("groupSelect")) {
								if (!value_strX.equals("0")) {
									PersonGroupBean group_obj = PersonGroupBean.getPersonGroup(Integer.parseInt(value_strX));
									System.out.println("group found >" + group_obj.getLabel());
									groupSelect.addElement(group_obj);
								}
							} else if (last_value.equals("audienceSelect")) {
								if (!value_strX.equals("0")) {
									audienceSelect.addElement(AudienceBean.getAudience(Integer.parseInt(value_strX)));
								}
							} else if (last_value.equals("certificationSelect")) {
								if (!value_strX.equals("0")) {
									//certificationSelect.addElement(EcolabCertificationBean.getCertification(Integer.parseInt(value_strX)).getCertification());
								}
							} else if (last_value.equals("startInput")) {
								startInput = CUBean.getDateFromUserString(value_strX);
							} else if (last_value.equals("endInput")) {
								endInput = CUBean.getDateFromUserString(value_strX);
							}
						} else {
							if (last_value.equals("generateExcelInput")) {
								generateExcelInput = true;
							} else if (last_value.equals("inactivesInput")) {
								inactivesInput = true;
							}
						}
					}
				}
			}
		}
		
		if (_save_only) {
			if ((nameInput == null) || nameInput.isEmpty()) {
				throw new IllegalValueException("Unable to save template.  Please specify a report template name.");
			}
		}
		
		IoTLOKReportTemplateBean template_obj = null;
		
		if ((_template_id_str == null) || _template_id_str.isEmpty() || _template_id_str.equals("0")) {
			template_obj = new IoTLOKReportTemplateBean();
			template_obj.setCompany(_company);
			template_obj.setPerson(_logged_in_person);
			
		} else {
			template_obj = (IoTLOKReportTemplateBean)IoTLOKReportTemplateBean.getTemplate(Integer.parseInt(_template_id_str));
		}
		
		template_obj.setCreateOrModifyPerson(_logged_in_person);
		
		template_obj.setTemplateTitle(nameInput);
		template_obj.setManufacturers(mfgSelect);
		template_obj.setProducts(productSelect);
		//template_obj.setCourses(courseSelect);
		template_obj.setDepartments(departmentSelect);
		template_obj.setTitles(titleSelect);
		template_obj.setPersonGroups(groupSelect);
		template_obj.setAudiences(audienceSelect);
		template_obj.setReportCertifications(certificationSelect);
		
		template_obj.setStartDate(startInput);
		template_obj.setEndDate(endInput);
		
		System.out.println("found status >" + statusSelect);
		template_obj.setEnrollmentStatus(statusSelect);
		template_obj.setReportType(Short.parseShort(reportSelect));
		
		template_obj.setShowHTML(true);
		template_obj.setShowExcel(generateExcelInput);
		template_obj.setIncludeInactive(inactivesInput);
		
		if (_save_only) {
			template_obj.save();
		}
				
		/*
		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\"}]}");
		return b.toString();
		*/
		
		return template_obj;
	}
	
	
	
	
	private String
	reportTemplateToExcel(IoTLOKReportTemplateBean _template) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, FileNotFoundException, IOException, IllegalValueException {
		
		int num_results = 0;
		int row_index = 1;
		short column_index = 0;
		
		com.badiyan.uk.online.beans.UKOnlineCourseReportLister courseReportLister = new com.badiyan.uk.online.beans.UKOnlineCourseReportLister();
		courseReportLister.setReportTemplate(_template);
		
		HSSFWorkbook wb = null;

		switch (_template.getReportType()) {
			case IoTLOKReportTemplateBean.PRODUCT_REPORT_TYPE: {

				String report_template = "product-report.xls";

				System.out.println("start search");
				courseReportLister.searchProducts();
				System.out.println("search complete");
				

				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + report_template));
				wb = new HSSFWorkbook(fs);
				HSSFSheet sheet = wb.getSheetAt(0);
				
				class ProductReportData {
					int num_asn = 0;
					int num_inventory = 0;
					int num_sale = 0;
					int num_sale_activated = 0;
					int num_return = 0;
					int num_deactivated_other = 0;
					int num_deactivated_chargeback = 0;
				}
				Vector<Product> productList = new Vector<Product>();
				HashMap<Product,ProductReportData> productDataMap = new HashMap<Product,ProductReportData>();

				Iterator itr = courseReportLister.getListStuff().iterator();
				while (itr.hasNext()) {
					
					com.badiyan.torque.ProductInteractionDb obj = (com.badiyan.torque.ProductInteractionDb)itr.next();
					ProductInteraction interaction_obj = ProductInteraction.getInteraction(obj);
					interaction_obj.getInteractionType();

					Product product_obj = Product.getProduct(obj.getProductDbId());
					if (!productList.contains(product_obj)) {
						productList.addElement(product_obj);
					}
					ProductReportData productData = null;
					if (productDataMap.containsKey(product_obj)) {
						productData = productDataMap.get(product_obj);
					} else {
						productData = new ProductReportData();
						productDataMap.put(product_obj, productData);
					}

					switch (interaction_obj.getInteractionType()) {
						case ProductInteraction.SALE_ACTIVATED_TYPE: {
							productData.num_sale_activated++;
							break;
						}
						case ProductInteraction.INVENTORY_TYPE: {
							productData.num_inventory++;
							break;
						}
						case ProductInteraction.SALE_TYPE: {
							productData.num_sale++;
							break;
						}
						case ProductInteraction.ASN_TYPE: {
							productData.num_asn++;
							break;
						}
						case ProductInteraction.RETURN_TYPE: {
							productData.num_return++;
							break;
						}
						case ProductInteraction.DEACTIVATED_CHARGEBACK_TYPE: {
							productData.num_deactivated_chargeback++;
							break;
						}
						case ProductInteraction.DEACTIVATED_OTHER_TYPE: {
							productData.num_deactivated_other++;
							break;
						}
					}
				}
					
				itr = productList.iterator();
				while (itr.hasNext()) {
					Product product_obj = (Product)itr.next();
					System.out.println("PROC PROD >" + product_obj.getLabel());
					ProductReportData productData = productDataMap.get(product_obj);


					courseReportLister.addCell(sheet, row_index, column_index, product_obj.getModelNumber()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, product_obj.getNameString()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, product_obj.getManufacturer().getLabel()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, product_obj.getDescription()); column_index++;
					
					courseReportLister.addCell(sheet, row_index, column_index, productData.num_asn); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, productData.num_inventory); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, productData.num_sale); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, productData.num_sale_activated); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, productData.num_return); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, productData.num_deactivated_chargeback); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, productData.num_deactivated_other); column_index++;
					

					num_results++;

					row_index++;
					column_index = 0;

				}
				
				courseReportLister.addCell(sheet, row_index + 1, (short)0, "Products in Report: " + num_results); row_index++;
				break;
				
			}
			case IoTLOKReportTemplateBean.INTERACTION_REPORT_TYPE: {

				String report_template = "interaction-report-2.xls";

				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + report_template));
				wb = new HSSFWorkbook(fs);
				HSSFSheet sheet = wb.getSheetAt(0);
				
				Iterator itr = ProductInteraction.getInteractions(_template.getCompany(), null, courseReportLister.getProducts(), null, courseReportLister.getStartDate(), courseReportLister.getEndDate(), (short)0, 0).iterator();
				while (itr.hasNext()) {

					ProductInteraction interaction_obj = (ProductInteraction)itr.next();

					String start_date_str = "";
					String interaction_date_str = CUBean.getUserDateString(interaction_obj.getInteractionDate());
					String interaction_time_str = CUBean.getUserTimeString(interaction_obj.getInteractionDate());

					UKOnlinePersonBean person_x = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(interaction_obj.getInteractionPerson().getId());

					courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getProduct().getLabel()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getUniqueIdentifier()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_date_str); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_time_str); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getInteractionTypeString()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getRetailer().getLabel()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getStore().getLabel()); column_index++;
					String full_name = person_x.getFullName();
					courseReportLister.addCell(sheet, row_index, column_index, person_x.getEmployeeNumberString()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, person_x.getLastNameString()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, person_x.getFirstNameString()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, person_x.getJobTitleString()); column_index++;

					num_results++;

					row_index++;
					column_index = 0;
				}
					
				courseReportLister.addCell(sheet, row_index + 1, (short)0, "Interactions in Report: " + num_results); row_index++;
				break;
				
			}
			case IoTLOKReportTemplateBean.SERIAL_NUMBER_REPORT_TYPE: {

				String report_template = "interaction-report-3.xls";

				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + report_template));
				wb = new HSSFWorkbook(fs);
				HSSFSheet sheet = wb.getSheetAt(0);
				
				Iterator itr = ProductInteraction.getLatestInteractions(_template.getCompany(), null, courseReportLister.getProducts(), null, courseReportLister.getStartDate(), courseReportLister.getEndDate(), (short)0, 0).iterator();
				while (itr.hasNext()) {

					ProductInteraction interaction_obj = (ProductInteraction)itr.next();

					String start_date_str = "";
					String interaction_date_str = CUBean.getUserDateString(interaction_obj.getInteractionDate());
					String interaction_time_str = CUBean.getUserTimeString(interaction_obj.getInteractionDate());

					UKOnlinePersonBean person_x = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(interaction_obj.getInteractionPerson().getId());

					courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getProduct().getLabel()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getUniqueIdentifier()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_date_str); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_time_str); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getInteractionTypeString()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getRetailer().getLabel()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getStore().getLabel()); column_index++;
					String full_name = person_x.getFullName();
					courseReportLister.addCell(sheet, row_index, column_index, person_x.getEmployeeNumberString()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, person_x.getLastNameString()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, person_x.getFirstNameString()); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, person_x.getJobTitleString()); column_index++;
					
					
						String sitTimeStr = "";
						
						try {
							long millis = Product.calculateShelfSitTimeMillis(interaction_obj.getRetailer(), null, interaction_obj.getProduct(), interaction_obj.getUniqueIdentifier());

							long days = TimeUnit.MILLISECONDS.toDays(millis);
							long hours = TimeUnit.MILLISECONDS.toHours(millis - (days * 86400000l) );
							//long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
							//long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
							sitTimeStr = days + " day" + (days == 1 ? "" : "s") + ", " + hours + " hour" + (hours == 1 ? "" : "s");
						} catch (Exception x) {
							x.printStackTrace();
						}
						
						
					courseReportLister.addCell(sheet, row_index, column_index, sitTimeStr); column_index++;
						

					num_results++;

					row_index++;
					column_index = 0;
				}
					
				courseReportLister.addCell(sheet, row_index + 1, (short)0, "Interactions in Report: " + num_results); row_index++;
				break;
				
			} case ReportTemplateBean.CERTIFICATION_REPORT_TYPE: {
			
			} case ReportTemplateBean.EVALUATION_REPORT_TYPE: {
			
			} case ReportTemplateBean.PROGRAM_REPORT_TYPE: {
			
			} case ReportTemplateBean.USER_REPORT_TYPE: {

				/*
				String report_template = "user-report-uk.xls";

				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + report_template));
				wb = new HSSFWorkbook(fs);
				HSSFSheet sheet = wb.getSheetAt(0);

				//courseReportLister.addCell(sheet, 0, (short)1, courseReportLister.getLocationString());
				
				List aList = courseReportLister.getUsers();

				String last_full_name = "";

				for (int i = 0; i < aList.size(); i++)
				{
					com.badiyan.torque.Person obj = (com.badiyan.torque.Person)aList.get(i);

					UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(obj.getPersonid());

					if (person.getCompanyIdString().equals(_template.getCompany().getValue())) {

						DepartmentBean department = DepartmentBean.getDepartment(person.getDepartmentId());



						String full_name = person.getFullName();

						courseReportLister.addCell(sheet, row_index, column_index, person.getFirstNameString()); column_index++;
						courseReportLister.addCell(sheet, row_index, column_index, person.getLastNameString()); column_index++;
						
						try {
							String emp_num_str = person.getEmployeeNumberString();
							Integer.parseInt(emp_num_str);
							courseReportLister.addCell(sheet, row_index, column_index, person.getEmployeeNumberString()); column_index++;
						} catch (Exception x) {
							courseReportLister.addCell(sheet, row_index, column_index, ""); column_index++;
						}
						
						courseReportLister.addCell(sheet, row_index, column_index, person.getEmailString()); column_index++;
						courseReportLister.addCell(sheet, row_index, column_index, person.getUsernameString()); column_index++;
						column_index++; // skip password
						courseReportLister.addCell(sheet, row_index, column_index, person.getGroupNameString()); column_index++;
						courseReportLister.addCell(sheet, row_index, column_index, department.getLabel()); column_index++;
						courseReportLister.addCell(sheet, row_index, column_index, person.getJobTitleString()); column_index++;

						courseReportLister.addCell(sheet, row_index, column_index, person.getCostCenterString()); column_index++;

						courseReportLister.addCell(sheet, row_index, column_index, person.isActive() ? "" : "INACTIVE"); column_index++;


						Date last_log_in_date = person.getLastLogInDate();
						System.out.println("last_log_in_date >" + last_log_in_date);
						String days_ago_str = "";
						String last_log_in_date_str = "Never";
						if (last_log_in_date != null)
						{
							last_log_in_date_str = CUBean.getUserDateString(last_log_in_date);
							Calendar midnight = Calendar.getInstance();
							midnight.set(Calendar.HOUR_OF_DAY, 0);
							midnight.set(Calendar.MINUTE, 0);
							midnight.set(Calendar.SECOND, 0);
							//long diff = now2.getTime() - last_log_in_date.getTime();
							long diff = midnight.getTimeInMillis() - last_log_in_date.getTime();
							long days_ago = diff / (1000 * 60 * 60 * 24);
							if (diff < 0)
								days_ago_str = "&nbsp(today)";
							else if (days_ago == 0)
								days_ago_str = "&nbsp(yesterday)";
							else
								days_ago_str = "&nbsp(" + (days_ago + 1) + " days ago)";

						}

						courseReportLister.addCell(sheet, row_index, column_index, last_log_in_date_str); column_index++;
						courseReportLister.addCell(sheet, row_index, column_index, person.getRolesString()); column_index++;

						//courseReportLister.addCell(sheet, row_index, column_index, person.getDepartment().getNameIncludingParents()); column_index++;

						row_index++;
						column_index = 0;
						total_enrollments++;

					}

				}
				
				courseReportLister.addCell(sheet, row_index + 1, (short)0, "Users in Report: " + total_enrollments); row_index++;
				
				*/

				break;
			
			}
		}
		

		String saveFilename = System.currentTimeMillis() + ".xls";
		FileOutputStream fileOut = new FileOutputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + saveFilename);
		wb.write(fileOut);
		fileOut.close();
		
		return CUBean.getProperty("cu.webDomain") + "/resources/" + saveFilename;
	}
	
	
	
	private String
	getEmailHTMLString(String _subject, String _message, PersonBean _to_person, String _formUrl) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		boolean is_meeting = false;
		boolean is_observation_2 = false;
		String appt_date_str = "";
		
		
		
		String nextStep = "";
		
		//System.out.println("NEXT STEP STR >" + nextStep);
		
		String host_str = CUBean.getProperty("cu.host");
		if (CUBean.getProperty("cu.port").equals("8080")) {
			host_str += (":" + CUBean.getProperty("cu.port"));
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("<div>");
		buf.append("  <table cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px #acacac solid;width:615px\" align=\"center\">");
		buf.append("    <tbody><tr>");
		buf.append("      <td bgcolor=\"#f2f2f2\" height=\"89\" valign=\"top\">");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:613px\">");
		buf.append("          <tbody><tr>");
		buf.append("            <td valign=\"top\" height=\"89\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:24px;color:#11100e;padding-left:30px;overflow:hidden;word-wrap:break-word;width:350px\">");
		buf.append("            <br><span style=\"font-size:20px\">" + _subject + "</span></td>");
		buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"><img style=\"padding-top: 5px; padding-right: 5pc;\" src=\"" + CUBean.getProperty("cu.webDomain") + "/resources/bu-images/2/KAU_Logo_FullColor_Inline_401.png\"  /></td>");
		//buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"></td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("      </td>");
		buf.append("    </tr>");
		buf.append("    <tr>");
		buf.append("      <td>");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		buf.append("          <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word;padding:14px 26px 14px 26px\">");
		
		/*
		buf.append("                  <br />Evaluator Comments: <strong>Some Comments</strong><br />" );
		buf.append("                  <br />Teacher Comments: <strong>Some Comments</strong><br />" );
		buf.append("                  <br /><a href=\"" + _formUrl + "\" target=\"_blank\">Click to View Form</a><br />" );
		*/
		
		if (!nextStep.isEmpty()) {
			buf.append("                  <br>Next Step: <strong>" + nextStep + "</strong>");
			buf.append("                  <br /><a href=\"http://" + host_str + "/index.html#VIEW=WORKFLOW&TEACHERID=22\" target=\"_blank\">Click to View Workflow</a><br />" );
			
		}
		/*
		try {
			System.out.println("getEmailHTMLString() appt_date_str >" + appt_date_str);
			System.out.println("getEmailHTMLString() CUBean.getUserTimeString(_observation.getObservationDate()) >" + CUBean.getUserTimeString(_observation.getObservationDate()));
		} catch (Exception x) {
			x.printStackTrace();
		}
		*/
		buf.append("                </td></tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr>");
		buf.append("            <td style=\"padding-left:26px\">");
		buf.append("              <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #acacac;width:559px\">");
		buf.append("                <tbody><tr>");
		//buf.append("                  <td bgcolor=\"#e1eeef\" style=\"padding:13px 20px 13px 20px;font-family:Arial,Helvetica,sans-serif;color:#317679;font-size:18px;font-weight:bold;line-height:24px;overflow:auto;word-wrap:break-word;width:240px\">");
		buf.append("                  <td bgcolor=\"#e1eeef\" style=\"padding:13px 20px 13px 20px;font-family:Arial,Helvetica,sans-serif;color:#317679;font-size:18px;font-weight:bold;line-height:24px;overflow:auto;word-wrap:break-word\">");
		if (_to_person != null) {
			buf.append("                   " + _to_person.getFirstNameString() + ",");
		}
		buf.append("                  <br>" + _message + "</td>");
		
		/*
		buf.append("                  <td style=\"color:#6e6e6e;font-family:Arial,Helvetica,sans-serif;font-size:14px;padding-left:51px;padding-right:10px;line-height:22px;overflow:auto;word-wrap:break-word;width:218px\">");
		
		
		buf.append("                  Licensed Staff: Hmmm");
		
			buf.append("                  <br>Peer Evaluator: PE");
		
		

	
		buf.append("                  <br></td>");
		*/
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        <tr>");
		buf.append("            <td style=\"padding:12px 26px 16px 26px\">");
		buf.append("              <table width=\"561\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:561px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word\">");
		
		buf.append("Please do not reply to this email.  If you have any questions, please contact your <a target=\"_top\" href=\"mailto:marlo@badiyan.com\">Instructor</a>. ");

		buf.append("                  <br>&nbsp;");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr bgcolor=\"#f2f2f2\">");
		buf.append("            <td style=\"border-top:1px solid #acacac;padding:26px 0px 14px 26px\">");
		buf.append("              <table width=\"587\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:587px\">");
		buf.append("                <tbody><tr>");
		if (_to_person != null) {
			buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#6e6e6e;width:420px;overflow:auto;word-wrap:break-word\">This e-mail was sent on behalf of  ");
			buf.append("                  " + _to_person.getLabel());
		} else {
			buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#6e6e6e;width:420px;overflow:auto;word-wrap:break-word\">  ");
		}
		buf.append("                  <br>by Universal Knowledge. This is an automated email. ");
		buf.append("                  <br>&nbsp;");
		buf.append("                  <br>");
		buf.append("                  ");
		buf.append("                  <br></td>");
		buf.append("                  <td width=\"167\" valign=\"bottom\">");
		//buf.append("                    <a href=\"http://www.valonyx.com\"><img src=\"http://www.valonyx.com/images/valonyx-logo-grey-198.png\" /></a>");
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
	generateInteractionReport(CompanyBean _selected_company, UKOnlinePersonBean _logged_in_person,  Product _product) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, FileNotFoundException, IOException, IllegalValueException {
		
		int total_enrollments = 0;
		int row_index = 1;
		short column_index = 0;
		String report_template = "interaction-report.xls";
		
		com.badiyan.uk.online.beans.UKOnlineCourseReportLister courseReportLister = new com.badiyan.uk.online.beans.UKOnlineCourseReportLister();
		com.badiyan.uk.beans.ReportTemplateBean adminReportTemplate = new com.badiyan.uk.beans.ReportTemplateBean();
		
		Vector courses = new Vector();
		//courses.addElement(_course);
		adminReportTemplate.setCourses(courses);
				
		adminReportTemplate.setCompany(_selected_company);
		adminReportTemplate.setPerson(_logged_in_person);
		
		boolean has_role_to_report_on_all_departments = _logged_in_person.hasRole(UKOnlineRoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) || 
								_logged_in_person.hasRole(UKOnlineRoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) || 
								_logged_in_person.hasRole(UKOnlineRoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME);
		boolean has_role_to_report_on_department = _logged_in_person.hasRole(UKOnlineRoleBean.MANAGER_ROLE_NAME) || 
								_logged_in_person.hasRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME);
		
		/*
		if (!has_role_to_report_on_all_departments && !has_role_to_report_on_department) {
			throw new IllegalValueException("Insufficient role to generate report.");
		}
		*/
		
		if (has_role_to_report_on_department && !has_role_to_report_on_all_departments) {
			Vector departments = new Vector();
			departments.addElement(_logged_in_person.getDepartment());
			adminReportTemplate.setDepartments(departments);
		}
		
		adminReportTemplate.setCreateOrModifyPerson(_logged_in_person);
		adminReportTemplate.setIncludeInactive(false);
		adminReportTemplate.setShowExcel(true);
		adminReportTemplate.setShowHTML(true); // shrug
		adminReportTemplate.setReportType((short)3);	
	
		
		//courseReportLister.setReportTemplate(adminReportTemplate);
		courseReportLister.setPerson(_logged_in_person);
		
		//System.out.println("start search");
		//courseReportLister.search();
		//System.out.println("search complete");
		//List aList = courseReportLister.getListStuff();
		
		
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + report_template));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);

		//courseReportLister.addCell(sheet, 0, (short)1, courseReportLister.getLocationString());
		
		boolean is_manual_completion_date = false;
		int num_manual_completions = 0;

		String last_full_name = "";
		
		Iterator itr = _product.getInteractions().iterator();

		while (itr.hasNext()) {
			
			ProductInteraction interaction_obj = (ProductInteraction)itr.next();

			String start_date_str = "";
			String interaction_date_str = CUBean.getUserDateString(interaction_obj.getInteractionDate());
			String interaction_time_str = CUBean.getUserTimeString(interaction_obj.getInteractionDate());
			
			UKOnlinePersonBean person_x = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(interaction_obj.getInteractionPerson().getId());
			

			courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getUniqueIdentifier()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, interaction_date_str); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, interaction_time_str); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getInteractionTypeString()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getRetailer().getLabel()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, interaction_obj.getStore().getLabel()); column_index++;
			String full_name = person_x.getFullName();
			courseReportLister.addCell(sheet, row_index, column_index, person_x.getEmployeeNumberString()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, person_x.getLastNameString()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, person_x.getFirstNameString()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, person_x.getJobTitleString()); column_index++;


			total_enrollments++;

			row_index++;
			column_index = 0;

			last_full_name = full_name;
			
		}

		courseReportLister.addCell(sheet, row_index + 1, (short)0, "Interactions in Report: " + total_enrollments); row_index++;

		String saveFilename = System.currentTimeMillis() + ".xls";
		FileOutputStream fileOut = new FileOutputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + saveFilename);
		wb.write(fileOut);
		fileOut.close();
		
		return saveFilename;
	}
	

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}
	
	protected void doOptions(HttpServletRequest request, HttpServletResponse _response)
			throws ServletException, IOException {
		
		System.out.println("doOptions() invoked in FileUploadServletUK");
		
		_response.setContentType("application/json;charset=UTF-8");
		
		_response.setHeader("Access-Control-Allow-Origin", "*");
		_response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
		_response.setHeader("Access-Control-Max-Age", "1000");
		_response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");
		
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Retail Servlet";
	}// </editor-fold>

}
