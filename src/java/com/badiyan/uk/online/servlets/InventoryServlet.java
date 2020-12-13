/*
 * ScheduleServlet.java
 *
 * Created on November 23, 2007, 9:15 AM
 */

package com.badiyan.uk.online.servlets;

import com.badiyan.torque.*;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.*;

import com.badiyan.uk.online.PDF.*;
import com.valonyx.beans.ShoppingCartBean;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import org.apache.torque.*;

/**
 *
 * @author marlo
 * @version
 */
public class
InventoryServlet
    extends HttpServlet
{
	// "Tue Feb 12 00:00:00 CST 2008" - Internet Explorer
	// "Tue Feb 12 2008 00:00:00 GMT-0600 (Central Standard Time)"
	private static SimpleDateFormat ie_date_format = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
	private static SimpleDateFormat ns_date_format = new SimpleDateFormat("EEE MMM d yyyy HH");
	private static SimpleDateFormat yui_date_format = new SimpleDateFormat("M/d/yyyy"); // 2/1/2008

	private static SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");

	private static HashMap<String,Date> last_update_hash = new HashMap<String,Date>(11);
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
		//System.out.println("processRequest() invoked in ScheduleServlet");

		HttpSession session = _request.getSession(true); // was false - /shrug

		//set the content type
		_response.setContentType("text/xml");
		_response.setHeader("Cache-Control", "no-cache");

		//get the PrintWriter object to write the html page
		PrintWriter writer = _response.getWriter();

		try
		{
			UKOnlineCompanyBean adminCompany = null;
			if (session.getAttribute("adminCompany") == null) {
				adminCompany = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5);
			} else {
				adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
			}
			PracticeAreaBean adminPracticeArea = (PracticeAreaBean)session.getAttribute("adminPracticeArea");
			UKOnlinePersonBean adminPractitioner = (UKOnlinePersonBean)session.getAttribute("adminPractitioner");
			UKOnlineLoginBean adminLoginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
			UKOnlinePersonBean adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
			

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
			System.out.println("stamp >" + CUBean.getUserTimeString(stamper));
			System.out.println("command >" + command);
			System.out.println("parameter >" + parameter);
			System.out.println("arg1 >" + arg1);
			System.out.println("arg2 >" + arg2);
			System.out.println("arg3 >" + arg3);
			System.out.println("arg4 >" + arg4);
			System.out.println("arg5 >" + arg5);
			System.out.println("arg6 >" + arg6);
			System.out.println("arg7 >" + arg7);
			

			if (command.equals("purchaseOrderReport"))
			{
				PurchaseOrder purchase_order = PurchaseOrder.getPurchaseOrder(Integer.parseInt(parameter));

				String url = "../resources/pdf/" + PurchaseOrderBuilder.generatePurchaseOrder(adminCompany, purchase_order);
				writer.println("<purchase_order_report_url><![CDATA[" + url + "]]></purchase_order_report_url>");
			}
			else if (command.equals("getCheckoutCodesByDescInv"))
			{
				String item_search_str = parameter.substring(0, parameter.indexOf('|'));
				String vendor_search_str = parameter.substring(parameter.indexOf('|') + 1);

				session.setAttribute("invSearchStr", item_search_str);
				session.setAttribute("vendorSearchStr", vendor_search_str);

				//InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany); // not sure why this was here...
				
				StringBuffer buf = new StringBuffer();

				Iterator code_itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(adminCompany, item_search_str, vendor_search_str).iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					buf.append(InventoryServlet.getXML(code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("getCheckoutCodesByDescInvSynced"))
			{
				String item_search_str = parameter.substring(0, parameter.indexOf('|'));
				String vendor_search_str = parameter.substring(parameter.indexOf('|') + 1);

				session.setAttribute("invSearchStr", item_search_str);
				session.setAttribute("vendorSearchStr", vendor_search_str);

				//InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany); // not sure why this was here...
				
				StringBuffer buf = new StringBuffer();

				Iterator code_itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(adminCompany, item_search_str, vendor_search_str).iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					if (code.isSynced() || !adminCompany.getQuickBooksSettings().isQuickBooksFSEnabled())
						buf.append(InventoryServlet.getXML(code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("getCheckoutCodesByDesc"))
			{
				String item_search_str = parameter.substring(0, parameter.indexOf('|'));
				String vendor_search_str = parameter.substring(parameter.indexOf('|') + 1);

				session.setAttribute("invSearchStr", item_search_str);
				session.setAttribute("vendorSearchStr", vendor_search_str);

				//InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany); // again...
				
				StringBuffer buf = new StringBuffer();

				Iterator code_itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(adminCompany, item_search_str, vendor_search_str, CheckoutCodeBean.INVENTORY_TYPE).iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					buf.append(InventoryServlet.getXML(code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("getCheckoutCodesByDescNoVendor"))
			{
				String item_search_str = parameter;
				String vendor_search_str = "";

				session.setAttribute("invSearchStr", item_search_str);
				session.setAttribute("vendorSearchStr", vendor_search_str);

				//InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany); // hmmm
				
				StringBuffer buf = new StringBuffer();

				Iterator code_itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(adminCompany, item_search_str, vendor_search_str, CheckoutCodeBean.INVENTORY_TYPE).iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					buf.append(InventoryServlet.getXML(code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("getCheckoutCodesByDescCount"))
			{
				String item_search_str = parameter.substring(0, parameter.indexOf('|'));
				String vendor_search_str = parameter.substring(parameter.indexOf('|') + 1);

				session.setAttribute("invSearchStr", item_search_str);
				session.setAttribute("vendorSearchStr", vendor_search_str);

				InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany);
				StringBuffer buf = new StringBuffer();

				Iterator code_itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(adminCompany, item_search_str, vendor_search_str, CheckoutCodeBean.INVENTORY_TYPE).iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					buf.append(InventoryServlet.getXML(inventory_count, code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("recordItemCount"))
			{
				InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany);

				CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(parameter.substring(0, parameter.indexOf('|'))));
				String count_str = parameter.substring(parameter.indexOf('|') + 1);
				try
				{
					int count = Integer.parseInt(count_str);
					inventory_count.addCount(code, count);
				}
				catch (NumberFormatException x)
				{

				}
			}
			else if (command.equals("saveCount"))
			{
				InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany);
				inventory_count.setCreateOrModifyPerson((UKOnlinePersonBean)adminLoginBean.getPerson());
				inventory_count.save();
			}
			else if (command.equals("showAllInv"))
			{
				//InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany);
				
				StringBuffer buf = new StringBuffer();

				Iterator code_itr = CheckoutCodeBean.getCheckoutCodes(adminCompany).iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					buf.append(InventoryServlet.getXML(code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("showAll"))
			{
				//InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany);
				
				StringBuffer buf = new StringBuffer();

				Iterator code_itr = CheckoutCodeBean.getCheckoutCodes(adminCompany, CheckoutCodeBean.INVENTORY_TYPE).iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					buf.append(InventoryServlet.getXML(code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("showAllCount"))
			{
				InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(adminCompany);
				StringBuffer buf = new StringBuffer();

				Iterator code_itr = CheckoutCodeBean.getCheckoutCodes(adminCompany, CheckoutCodeBean.INVENTORY_TYPE).iterator();
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					buf.append(InventoryServlet.getXML(inventory_count, code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("showSOAPRoot"))
			{
				session.setAttribute("soap_button", parameter);
				
				StringBuffer buf = new StringBuffer();
				
				PracticeAreaBean selected_practice_area = (PracticeAreaBean)session.getAttribute("adminPracticeArea");
				if (selected_practice_area != null)
				{
					Iterator itr = SOAPNotesBean.getStatements(adminCompany, selected_practice_area, parameter.equals("Button radio1") ? "S" : parameter.equals("Button radio2") ? "O" : parameter.equals("Button radio3") ? "A" : "P").iterator();
					while (itr.hasNext())
					{
						SoapStatement statement = (SoapStatement)itr.next();
						buf.append(ScheduleServlet.getXML(statement));
					}
				}
				
				writer.println("<statements root=\"true\">" + buf.toString() + "</statements>");
			}
			else if (command.equals("showSOAPPracticeArea"))
			{
				PracticeAreaBean selected_practice_area = PracticeAreaBean.getPracticeArea(Integer.parseInt(parameter));
				session.setAttribute("adminPracticeArea", selected_practice_area);

				String soap_button_str = (String)session.getAttribute("soap_button");
				if (soap_button_str == null)
					soap_button_str = "Button radio1";
				
				StringBuffer buf = new StringBuffer();
				
				if (selected_practice_area != null)
				{
					Iterator itr = SOAPNotesBean.getStatements(adminCompany, selected_practice_area, soap_button_str.equals("Button radio1") ? "S" : soap_button_str.equals("Button radio2") ? "O" : soap_button_str.equals("Button radio3") ? "A" : "P").iterator();
					while (itr.hasNext())
					{
						SoapStatement statement = (SoapStatement)itr.next();
						buf.append(ScheduleServlet.getXML(statement));
					}
				}
				
				writer.println("<statements root=\"true\">" + buf.toString() + "</statements>");
			}
			else if (command.equals("selectStatement"))
			{
				SoapStatement stmt = SOAPNotesBean.getStatement(Integer.parseInt(parameter));
				session.setAttribute("adminStatement", stmt);
				
				StringBuffer buf = new StringBuffer();
				
				Iterator itr = SOAPNotesBean.getChildStatements(stmt).iterator();
				while (itr.hasNext())
				{
					SoapStatement statement = (SoapStatement)itr.next();
					buf.append(ScheduleServlet.getXML(statement));
				}
				
				writer.println("<statements root=\"false\" parent=\"" + stmt.getStatement() + "\" parent_id=\"" + parameter + "\">" + buf.toString() + "</statements>");
			}
			else if (command.equals("back"))
			{
				SoapStatement child = (SoapStatement)session.getAttribute("adminStatement");
				
				int parent_id = child.getParentId();
				if (parent_id == 0)
				{
					StringBuffer buf = new StringBuffer();

					PracticeAreaBean selected_practice_area = (PracticeAreaBean)session.getAttribute("adminPracticeArea");
					if (selected_practice_area != null)
					{
						String soap_button_str = (String)session.getAttribute("soap_button");
						if (soap_button_str == null)
							soap_button_str = "Button radio1";
				
						Iterator itr = SOAPNotesBean.getStatements(adminCompany, selected_practice_area, soap_button_str.equals("Button radio1") ? "S" : soap_button_str.equals("Button radio2") ? "O" : soap_button_str.equals("Button radio3") ? "A" : "P").iterator();
						while (itr.hasNext())
						{
							SoapStatement statement = (SoapStatement)itr.next();
							buf.append(ScheduleServlet.getXML(statement));
						}
					}

					writer.println("<statements root=\"true\">" + buf.toString() + "</statements>");
					
					session.setAttribute("adminStatement", new SoapStatement());
				}
				else
				{
				
					SoapStatement stmt = SOAPNotesBean.getStatement(parent_id);
					session.setAttribute("adminStatement", stmt);

					StringBuffer buf = new StringBuffer();

					Iterator itr = SOAPNotesBean.getChildStatements(stmt).iterator();
					while (itr.hasNext())
					{
						SoapStatement statement = (SoapStatement)itr.next();
						buf.append(ScheduleServlet.getXML(statement));
					}

					writer.println("<statements root=\"false\" parent=\"" + stmt.getStatement() + "\" parent_id=\"" + parameter + "\">" + buf.toString() + "</statements>");
				}
			}
			else if (command.equals("addChildStatement"))
			{
				SoapStatement parent = (SoapStatement)session.getAttribute("adminStatement");
				PracticeAreaBean selected_practice_area = (PracticeAreaBean)session.getAttribute("adminPracticeArea");
				
				String soap_button_str = (String)session.getAttribute("soap_button");
				if (soap_button_str == null)
					soap_button_str = "Button radio1";
				
				SoapStatement child = new SoapStatement();
				child.setCompanyId(adminCompany.getId());
				child.setParentId(parent.getSoapStatementId());
				child.setPracticeAreaId(selected_practice_area.getId());
				child.setStatement(parameter);
				child.setType(soap_button_str.equals("Button radio1") ? "S" : soap_button_str.equals("Button radio2") ? "O" : soap_button_str.equals("Button radio3") ? "A" : "P");
				child.save();
				
				StringBuffer buf = new StringBuffer();
				
				Iterator itr = SOAPNotesBean.getChildStatements(parent).iterator();
				while (itr.hasNext())
				{
					SoapStatement statement = (SoapStatement)itr.next();
					buf.append(ScheduleServlet.getXML(statement));
				}
				
				writer.println("<statements root=\"false\" parent=\"" + parent.getStatement() + "\" parent_id=\"" + parent.getSoapStatementId() + "\">" + buf.toString() + "</statements>");
			}
			else if (command.equals("addRootStatement"))
			{
				PracticeAreaBean selected_practice_area = (PracticeAreaBean)session.getAttribute("adminPracticeArea");
				
				String soap_button_str = (String)session.getAttribute("soap_button");
				if (soap_button_str == null)
					soap_button_str = "Button radio1";
				
				SoapStatement child = new SoapStatement();
				child.setCompanyId(adminCompany.getId());
				child.setParentId(0);
				child.setPracticeAreaId(selected_practice_area.getId());
				child.setStatement(parameter);
				child.setType(soap_button_str.equals("Button radio1") ? "S" : soap_button_str.equals("Button radio2") ? "O" : soap_button_str.equals("Button radio3") ? "A" : "P");
				child.save();
				
				StringBuffer buf = new StringBuffer();

				if (selected_practice_area != null)
				{
					Iterator itr = SOAPNotesBean.getStatements(adminCompany, selected_practice_area, soap_button_str.equals("Button radio1") ? "S" : soap_button_str.equals("Button radio2") ? "O" : soap_button_str.equals("Button radio3") ? "A" : "P").iterator();
					while (itr.hasNext())
					{
						SoapStatement statement = (SoapStatement)itr.next();
						buf.append(ScheduleServlet.getXML(statement));
					}
				}

				writer.println("<statements root=\"true\">" + buf.toString() + "</statements>");
			}
			else if (command.equals("refreshSoap"))
			{
				SoapStatement current = (SoapStatement)session.getAttribute("adminStatement");
				
				int current_id = current.getSoapStatementId();
				if (current_id == 0)
				{
					StringBuffer buf = new StringBuffer();

					PracticeAreaBean selected_practice_area = (PracticeAreaBean)session.getAttribute("adminPracticeArea");
					if (selected_practice_area != null)
					{
						String soap_button_str = (String)session.getAttribute("soap_button");
						if (soap_button_str == null)
							soap_button_str = "Button radio1";
				
						Iterator itr = SOAPNotesBean.getStatements(adminCompany, selected_practice_area, soap_button_str.equals("Button radio1") ? "S" : soap_button_str.equals("Button radio2") ? "O" : soap_button_str.equals("Button radio3") ? "A" : "P").iterator();
						while (itr.hasNext())
						{
							SoapStatement statement = (SoapStatement)itr.next();
							buf.append(ScheduleServlet.getXML(statement));
						}
					}

					writer.println("<statements root=\"true\">" + buf.toString() + "</statements>");
				}
				else
				{
					StringBuffer buf = new StringBuffer();

					Iterator itr = SOAPNotesBean.getChildStatements(current).iterator();
					while (itr.hasNext())
					{
						SoapStatement statement = (SoapStatement)itr.next();
						buf.append(ScheduleServlet.getXML(statement));
					}

					writer.println("<statements root=\"false\" parent=\"" + current.getStatement() + "\" parent_id=\"" + parameter + "\">" + buf.toString() + "</statements>");
				}
			}
			else if (command.equals("deleteStatement"))
			{
				SOAPNotesBean.deleteStatement(Integer.parseInt(parameter));
				
				SoapStatement current = (SoapStatement)session.getAttribute("adminStatement");
				
				int current_id = current.getSoapStatementId();
				if (current_id == 0)
				{
					StringBuffer buf = new StringBuffer();

					PracticeAreaBean selected_practice_area = (PracticeAreaBean)session.getAttribute("adminPracticeArea");
					if (selected_practice_area != null)
					{
						String soap_button_str = (String)session.getAttribute("soap_button");
						if (soap_button_str == null)
							soap_button_str = "Button radio1";
				
						Iterator itr = SOAPNotesBean.getStatements(adminCompany, selected_practice_area, soap_button_str.equals("Button radio1") ? "S" : soap_button_str.equals("Button radio2") ? "O" : soap_button_str.equals("Button radio3") ? "A" : "P").iterator();
						while (itr.hasNext())
						{
							SoapStatement statement = (SoapStatement)itr.next();
							buf.append(ScheduleServlet.getXML(statement));
						}
					}

					writer.println("<statements root=\"true\">" + buf.toString() + "</statements>");
				}
				else
				{
					StringBuffer buf = new StringBuffer();

					Iterator itr = SOAPNotesBean.getChildStatements(current).iterator();
					while (itr.hasNext())
					{
						SoapStatement statement = (SoapStatement)itr.next();
						buf.append(ScheduleServlet.getXML(statement));
					}

					writer.println("<statements root=\"false\" parent=\"" + current.getStatement() + "\" parent_id=\"" + parameter + "\">" + buf.toString() + "</statements>");
				}
			}
			else if (command.equals("updateSoap"))
			{
				SoapStatement current = (SoapStatement)session.getAttribute("adminStatement");
				if (parameter.length() > 0)
				{
					current.setStatement(parameter);
					current.save();
				}
				
				int current_id = current.getSoapStatementId();
				if (current_id == 0)
				{
					StringBuffer buf = new StringBuffer();

					PracticeAreaBean selected_practice_area = (PracticeAreaBean)session.getAttribute("adminPracticeArea");
					if (selected_practice_area != null)
					{
						String soap_button_str = (String)session.getAttribute("soap_button");
						if (soap_button_str == null)
							soap_button_str = "Button radio1";
				
						Iterator itr = SOAPNotesBean.getStatements(adminCompany, selected_practice_area, soap_button_str.equals("Button radio1") ? "S" : soap_button_str.equals("Button radio2") ? "O" : soap_button_str.equals("Button radio3") ? "A" : "P").iterator();
						while (itr.hasNext())
						{
							SoapStatement statement = (SoapStatement)itr.next();
							buf.append(ScheduleServlet.getXML(statement));
						}
					}

					writer.println("<statements root=\"true\">" + buf.toString() + "</statements>");
				}
				else
				{
					StringBuffer buf = new StringBuffer();

					Iterator itr = SOAPNotesBean.getChildStatements(current).iterator();
					while (itr.hasNext())
					{
						SoapStatement statement = (SoapStatement)itr.next();
						buf.append(ScheduleServlet.getXML(statement));
					}

					writer.println("<statements root=\"false\" parent=\"" + current.getStatement() + "\" parent_id=\"" + parameter + "\">" + buf.toString() + "</statements>");
				}
			}
			else if (command.equals("showAllHerbs"))
			{
				StringBuffer buf = new StringBuffer();
				InventoryDepartment herbDepartment = InventoryDepartment.getInventoryDepartment(adminCompany, "Herbs");
				Iterator code_itr = CheckoutCodeBean.getCheckoutCodes(herbDepartment).iterator();
				while (code_itr.hasNext()) {
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					buf.append(InventoryServlet.getXML(code));
				}

				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("getCheckoutCodesByDescHerbs"))
			{
				String item_search_str = parameter;

				session.setAttribute("invSearchStr", item_search_str);
				
				StringBuffer buf = new StringBuffer();
				InventoryDepartment herbDepartment = InventoryDepartment.getInventoryDepartment(adminCompany, "Herbs");
				Iterator code_itr = CheckoutCodeBean.getCheckoutCodesByDescAndDepartment(adminCompany, item_search_str, herbDepartment).iterator();
				//System.out.println("code_itr >" + code_itr.hasNext());
				while (code_itr.hasNext())
				{
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					buf.append(InventoryServlet.getXML(code));
				}

				//System.out.println("<codes>" + buf.toString() + "</codes>");
				writer.println("<codes>" + buf.toString() + "</codes>");
			}
			else if (command.equals("addDirections")) {
				
				short startDay = Short.parseShort(parameter);
				short endDay = Short.parseShort(arg1);
				short ml = Short.parseShort(arg2);
				String mixIn = arg3;
				short times = Short.parseShort(arg4);
				String period = arg5;
				String measure = arg6;
				
				HerbDosage adminHerbDosage = (HerbDosage)session.getAttribute("adminHerbDosage");
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
				
				StringBuffer buf = new StringBuffer();
				Iterator itr = directions.iterator();
				while (itr.hasNext()) {
					HerbDosageUseDirections objX = (HerbDosageUseDirections)itr.next();
					buf.append(this.getXML(objX));
				}
				
				//System.out.println("<directionSet>" + buf.toString() + "</directionSet>");
				writer.println("<directionSet>" + buf.toString() + "</directionSet>");
				
			}
			else if (command.equals("addHerb")) {
				
				CheckoutCodeBean herbToAdd = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(parameter));
				System.out.println("herbToAdd >" + herbToAdd.getLabel());
				short ml = Short.parseShort(arg1);
				
				HerbDosage adminHerbDosage = (HerbDosage)session.getAttribute("adminHerbDosage");
				Vector mappings = adminHerbDosage.getItems();
				HerbDosageItemMapping mapping = new HerbDosageItemMapping();
				mapping.setItem(herbToAdd);
				mapping.setML(ml);
				mappings.addElement(mapping);
				adminHerbDosage.setItems(mappings);
				
				
				
				StringBuffer buf = new StringBuffer();
				Iterator itr = mappings.iterator();
				while (itr.hasNext()) {
					HerbDosageItemMapping objX = (HerbDosageItemMapping)itr.next();
					buf.append(this.getXML(objX));
				}
				
				StringBuffer aBuf = new StringBuffer();
				aBuf.append("a1=\"" + adminHerbDosage.getMHCostRetail200MLTotalString() + "\"");
				aBuf.append(" a2=\"" + adminHerbDosage.getMHCostPer1MLTotalString() + "\"");
				aBuf.append(" a3=\"" + adminHerbDosage.getHerbMLUsedTotalString() + "\"");
				aBuf.append(" a4=\"" + adminHerbDosage.getDosageCostTotalString() + "\"");
				aBuf.append(" a5=\"" + adminHerbDosage.getRetail200MLTotalString() + "\"");
				aBuf.append(" a6=\"" + adminHerbDosage.getFortyPercCOGSTotalString() + "\"");
				aBuf.append(" a7=\"" + adminHerbDosage.getTotalRetail40MLTotalString() + "\"");
				
				System.out.println("<mappings " + aBuf.toString() + ">" + buf.toString() + "</mappings>");
				writer.println("<mappings " + aBuf.toString() + ">" + buf.toString() + "</mappings>");
				
			}
			else if (command.equals("deleteHerb")) {
				
				HerbDosage adminHerbDosage = (HerbDosage)session.getAttribute("adminHerbDosage");
				Vector mappings = adminHerbDosage.getItems();
				mappings.removeElementAt(Integer.parseInt(parameter));
				adminHerbDosage.setItems(mappings);
				
				StringBuffer buf = new StringBuffer();
				Iterator itr = mappings.iterator();
				while (itr.hasNext()) {
					HerbDosageItemMapping objX = (HerbDosageItemMapping)itr.next();
					buf.append(this.getXML(objX));
				}
				
				StringBuffer aBuf = new StringBuffer();
				aBuf.append("a1=\"" + adminHerbDosage.getMHCostRetail200MLTotalString() + "\"");
				aBuf.append(" a2=\"" + adminHerbDosage.getMHCostPer1MLTotalString() + "\"");
				aBuf.append(" a3=\"" + adminHerbDosage.getHerbMLUsedTotalString() + "\"");
				aBuf.append(" a4=\"" + adminHerbDosage.getDosageCostTotalString() + "\"");
				aBuf.append(" a5=\"" + adminHerbDosage.getRetail200MLTotalString() + "\"");
				aBuf.append(" a6=\"" + adminHerbDosage.getFortyPercCOGSTotalString() + "\"");
				aBuf.append(" a7=\"" + adminHerbDosage.getTotalRetail40MLTotalString() + "\"");
				
				System.out.println("<mappings " + aBuf.toString() + ">" + buf.toString() + "</mappings>");
				writer.println("<mappings " + aBuf.toString() + ">" + buf.toString() + "</mappings>");
				
			}
			else if (command.equals("deleteDirections")) {
				
				HerbDosage adminHerbDosage = (HerbDosage)session.getAttribute("adminHerbDosage");
				Vector directions = adminHerbDosage.getUseDirections();
				directions.removeElementAt(Integer.parseInt(parameter));
				adminHerbDosage.setUseDirections(directions);
				
				StringBuffer buf = new StringBuffer();
				Iterator itr = directions.iterator();
				while (itr.hasNext()) {
					HerbDosageUseDirections objX = (HerbDosageUseDirections)itr.next();
					buf.append(this.getXML(objX));
				}
				
				//System.out.println("<directionSet>" + buf.toString() + "</directionSet>");
				writer.println("<directionSet>" + buf.toString() + "</directionSet>");
				
			}
			else if (command.equals("saveHerbDosage")) {
				
				HerbDosage adminHerbDosage = (HerbDosage)session.getAttribute("adminHerbDosage");
				
				/*
				 * var arg1 = document.forms[0].mixDate.value; // param
		var arg2 = document.forms[0].mixDesc.value; // arg1
		var arg3 = document.forms[0].notes.value; // arg2
				 */
				
				
				adminHerbDosage.setClient(adminPerson);
				adminHerbDosage.setCompany(adminCompany);
				adminHerbDosage.setDescription(arg1);
				adminHerbDosage.setNotes(arg2);
				adminHerbDosage.setMixer((UKOnlinePersonBean)adminLoginBean.getPerson());
				adminHerbDosage.setMixDate(CUBean.getDateFromUserString(parameter));
				
				if (adminPerson.isNew())
					throw new IllegalValueException("No client selected.");
				if (arg1.isEmpty())
					throw new IllegalValueException("Please provide a mixture description.");
				
				adminHerbDosage.save();
				
				//writer.println("<directionSet>" + buf.toString() + "</directionSet>");
				
			}
			else if (command.equals("herbReport")) {
				
				HerbDosage adminHerbDosage = (HerbDosage)session.getAttribute("adminHerbDosage");
				if (parameter.equals("-1"))
					adminHerbDosage = (HerbDosage)session.getAttribute("adminHerbDosage");
				
				String ss_str = doHerbSpreadsheet(adminHerbDosage);
				writer.println("<herbReport>" + ss_str + "</herbReport>");
			}
			else if (command.equals("selectPerson"))
			{
				// <jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
				
				adminPerson = UKOnlinePersonBean.getPerson(adminCompany, Integer.parseInt(parameter));
				session.setAttribute("adminPerson", adminPerson);
				
				HerbDosage adminHerbDosage = new HerbDosage();
				session.setAttribute("adminHerbDosage", adminHerbDosage);
				
				StringBuffer buf = new StringBuffer();
				buf.append(this.getXML(adminPerson));
				
				// get the HerbDosages for this person
				
				buf.append("<dosages>");
				Iterator itr = HerbDosage.getHerbDosages(adminPerson).iterator();
				while (itr.hasNext()) {
					HerbDosage dosage = (HerbDosage)itr.next();
					buf.append(this.getXML(dosage));
				}
				buf.append("</dosages>");
				
				writer.println("<status>" + buf.toString() + "</status>");
			}
			else if (command.equals("getPeopleByLastName"))
			{
				// place the search string into the session
				
				String lastname = parameter;
				
				if (!parameter.equals(""))
					session.setAttribute("searchLastName", parameter);
				else
					lastname = (String)session.getAttribute("searchLastName");
				
				StringBuffer buf = new StringBuffer();

				Iterator people_itr = UKOnlinePersonBean.getPersonsByLastName(adminCompany, lastname).iterator();
				while (people_itr.hasNext())
				{
					UKOnlinePersonBean person = (UKOnlinePersonBean)people_itr.next();
					buf.append(getXML(person));
				}

				//System.out.println("<clients>" + buf.toString() + "</clients>");
				writer.println("<clients>" + buf.toString() + "</clients>");
			}
			else if (command.equals("getPeopleByFirstName"))
			{
				// place the search string into the session

				String firstname = parameter;

				if (!parameter.equals(""))
					session.setAttribute("searchFirstName", parameter);
				else
					firstname = (String)session.getAttribute("searchFirstName");

				StringBuffer buf = new StringBuffer();

				Iterator people_itr = UKOnlinePersonBean.getPersonsByFirstName(adminCompany, firstname).iterator();
				while (people_itr.hasNext())
				{
					UKOnlinePersonBean person = (UKOnlinePersonBean)people_itr.next();
					buf.append(getXML(person));
				}

				//System.out.println("<clients>" + buf.toString() + "</clients>");
				writer.println("<clients>" + buf.toString() + "</clients>");
			}
			else if (command.equals("getPeopleByFileNumber"))
			{
				String filenumber = parameter;

				if (!parameter.equals(""))
					session.setAttribute("searchFileNumber", parameter);
				else
					filenumber = (String)session.getAttribute("searchFileNumber");

				StringBuffer buf = new StringBuffer();

				Iterator people_itr = UKOnlinePersonBean.getPersonsByFileNumber(adminCompany, filenumber).iterator();
				while (people_itr.hasNext())
				{
					UKOnlinePersonBean person = (UKOnlinePersonBean)people_itr.next();
					buf.append(ScheduleServlet.getXML(person));
				}

				writer.println("<clients>" + buf.toString() + "</clients>");
			}
			else if (command.equals("deleteHerbDosage"))
			{
				HerbDosage.delete(HerbDosage.getHerbDosage(Integer.parseInt(parameter)));
				
				StringBuffer buf = new StringBuffer();
				buf.append(this.getXML(adminPerson));
				
				// get the HerbDosages for this person
				
				buf.append("<dosages>");
				Iterator itr = HerbDosage.getHerbDosages(adminPerson).iterator();
				while (itr.hasNext()) {
					HerbDosage dosage = (HerbDosage)itr.next();
					buf.append(this.getXML(dosage));
				}
				buf.append("</dosages>");
				
				writer.println("<status>" + buf.toString() + "</status>");
			}
			else if (command.equals("addToCart"))
			{
				System.out.println("addToCart invoked in InventoryServlet");
				
				// does the session client have a cart already?
				
				//<jsp:useBean id="appointment" class="com.badiyan.uk.online.beans.AppointmentBean" scope="session" />
				
				AppointmentBean appointment = (AppointmentBean)session.getAttribute("appointment");
				System.out.println("appointment found in session >" + appointment);
				if (appointment != null) {
					UKOnlinePersonBean client = appointment.getClient();
					ShoppingCartBean cart = ShoppingCartBean.getCart(client);
					
					CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(parameter));
					System.out.println("code found >" + code.getLabel());
					
					UKOnlinePersonBean selected_practitioner = (UKOnlinePersonBean)session.getAttribute("selected_practitioner");
					cart.add(code, selected_practitioner);
					cart.printCart();
					
				}
				
				writer.println("<status></status>");
			}
			else if (command.equals("removeFromCart"))
			{
				System.out.println("removeFromCart invoked in InventoryServlet");
				
				// does the session client have a cart already?
				
				//<jsp:useBean id="appointment" class="com.badiyan.uk.online.beans.AppointmentBean" scope="session" />
				
				AppointmentBean appointment = (AppointmentBean)session.getAttribute("appointment");
				System.out.println("appointment found in session >" + appointment);
				if (appointment != null) {
					UKOnlinePersonBean client = appointment.getClient();
					ShoppingCartBean cart = ShoppingCartBean.getCart(client);
					
					CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(parameter));
					System.out.println("code found >" + code.getLabel());
					
					cart.remove(code);
					
					cart.printCart();
					
				}
				
				writer.println("<status></status>");
			}

		}
		catch (Exception x)
		{
			writer.println("<error><![CDATA[Unexpected Error >" + x.getMessage() + "]]></error>");
			x.printStackTrace();
		}
    }
    
    public static String
    getXML(UKOnlinePersonBean _person)
		throws Exception
    {
		String value = "{\"label\" : \"" + _person.getLabel() + "\"}";
		return "<person><key>" + _person.getId() + "</key><value><![CDATA[" + value + "]]></value></person>";
    }

	public static String
	getXML(HerbDosage _obj)
		throws Exception
	{
		
		/*
					<div class="organization">
						<table cellspacing="" cellpadding="" border="0" summary="">
							<tr>
								<td style="width:   3px; ">&nbsp;</td>
								<td style="width:  78px; text-align: center; font-weight: bolder;  "><%= CUBean.getUserDateString(dosage.getMixDate()) %></td>
								<td style="width: 241px; text-align: left; font-weight: bolder;  "><a href="herb-dosage.jsp?id=<%= dosage.getId() %>"><%= dosage.getDescription() %></a></td>
								<td style="width: 178px; text-align: center; font-weight: bolder;  "><%= dosage.getMixer().getLabel() %></td>
								<td style="width:  49px; text-align: center; font-weight: bolder;  "><%= dosage.getDaysRemainingString() %></td>
								<td style="width:  14px; text-align: center;  "><img src="../images/delete_icon.gif" title="Delete" onclick="if (confirm('Delete?')) {processCommand('deleteHerbDosage','<%= yy %>')}" ></td>
								<td style="width:   3px; ">&nbsp;</td>
							</tr>
							<tr>
								<td style="width:   3px; ">&nbsp;</td>
								<td style="width: 560px; text-align: left; " colspan="5" ><%= dosage.getNotes() %></td>
								<td style="width:   3px; ">&nbsp;</td>
							</tr>
						</table>
					</div>
		 */
		
		
		String value = "{\"date\" : \"" + CUBean.getUserDateString(_obj.getMixDate()) + "\"," +
						" \"desc\" : \"" + _obj.getDescription() + "\"," +
						" \"mixer\" : \"" + _obj.getMixer().getLabel() + "\"," +
						" \"note\" : \"" + _obj.getNotes() + "\"," +
						" \"days\" : \"" + _obj.getDaysRemainingString() + "\" }";
		return "<dosage><key>" + _obj.getId() + "</key><value><![CDATA[" + value + "]]></value></dosage>";
	}

	public static String
	getXML(HerbDosageItemMapping _obj)
		throws Exception
	{
		CheckoutCodeBean item = _obj.getItem();
		
		String value = "{\"label\" : \"" + item.getLabel() + "\"," +
						" \"p1\" : \"" + item.getAmountString() + "\"," +
						" \"p2\" : \"" + _obj.getAmountPerMLString() + "\"," +
						" \"p3\" : \"" + _obj.getML() + "\"," +
						" \"p4\" : \"" + _obj.getDosageCostString() + "\"," +
						" \"p5\" : \"-\"," +
						" \"p6\" : \"-\"," +
						" \"p7\" : \"" + _obj.getDosageAmountString() + "\"," +
						" \"p8\" : \"" + _obj.getCOGSString() + "\"," +
						" \"p9\" : \"" + _obj.getTotalRetail40MLString() + "\" }";
		return "<mapping><key>0</key><value><![CDATA[" + value + "]]></value></mapping>";
	}

	public static String
	getXML(HerbDosageUseDirections _obj)
		throws Exception
	{
		String value = "{\"label\" : \"" + _obj.getLabel() + "\"," +
						" \"id\" : \"" + _obj.getId() + "\" }";
		return "<directions><key>" + _obj.getId() + "</key><value><![CDATA[" + value + "]]></value></directions>";
	}

	public static String
	getXML(InventoryCount inventory_count, CheckoutCodeBean _code)
		throws Exception
	{
		int count = inventory_count.getCount(_code);
		short on_hand = _code.getOnHandQuantity();
		
		String value = "{\"desc\" : \"" + _code.getDescription() + "\"," +
						" \"id\" : \"" + _code.getId() + "\"," +
						" \"exp\" : \"" + on_hand + "\"," +
						" \"count\" : \"" + count + "\"," +
						" \"diff\" : \"" + (on_hand - count) + "\" }";
		return "<code><key>" + _code.getId() + "</key><value><![CDATA[" + value + "]]></value></code>";
	}

	public static String
	getXML(CheckoutCodeBean _code)
		throws Exception
	{
		
		/*
		 * 
	<select name="typeSelect" style="width: 304px;">
		<option value="0">-- SELECT A CHECKOUT CODE TYPE --</option>
		<option value="<%= CheckoutCodeBean.PROCEDURE_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.PROCEDURE_TYPE) ? " selected" : ""  %>>Service</option>
		<option value="<%= CheckoutCodeBean.INVENTORY_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.INVENTORY_TYPE) ? " selected" : ""  %>>Inventory</option>
		<option value="<%= CheckoutCodeBean.NON_INVENTORY_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.NON_INVENTORY_TYPE) ? " selected" : ""  %>>Non Inventory</option>
		<option value="<%= CheckoutCodeBean.GROUP_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.GROUP_TYPE) ? " selected" : ""  %>>Group</option>
		<option value="<%= CheckoutCodeBean.GIFT_CARD %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.GIFT_CARD) ? " selected" : ""  %>>Gift Card</option>
		<option value="<%= CheckoutCodeBean.GIFT_CERTIFICATE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.GIFT_CERTIFICATE) ? " selected" : ""  %>>Gift Certificate</option>
		<!-- <option value="<%= CheckoutCodeBean.PAYMENT_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.PAYMENT_TYPE) ? " selected" : ""  %>>Payment</option> -->
		<option value="<%= CheckoutCodeBean.PLAN_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.PLAN_TYPE) ? " selected" : ""  %>>Payment Plan</option>
		<option value="<%= CheckoutCodeBean.SUBTOTAL %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.SUBTOTAL) ? " selected" : ""  %>>Subtotal</option>
	</select>
		 */
		
		String type_str = "";
		switch (_code.getType())
		{
			case CheckoutCodeBean.PROCEDURE_TYPE: type_str = "Service"; break;
			case CheckoutCodeBean.INVENTORY_TYPE: type_str = "Inventory"; break;
			case CheckoutCodeBean.NON_INVENTORY_TYPE: type_str = "Non Inv."; break;
			case CheckoutCodeBean.GROUP_TYPE: type_str = "Group"; break;
			case CheckoutCodeBean.PLAN_TYPE: type_str = "Plan"; break;
			case CheckoutCodeBean.GIFT_CARD: type_str = "Gift Card"; break;
			case CheckoutCodeBean.GIFT_CERTIFICATE: type_str = "Gift Cert."; break;
			case CheckoutCodeBean.PAYMENT_TYPE: type_str = "Payment"; break;
			case CheckoutCodeBean.SUBTOTAL: type_str = "Subtotal"; break;
			case CheckoutCodeBean.RECEIVE_PAYMENT_TYPE: type_str = "Rec. Pmt."; break;
		}
		
		String practice_area_str = "";
		int practice_area_id = _code.getPracticeAreaId();
		if (practice_area_id > 0)
		{
			PracticeAreaBean practice_area = PracticeAreaBean.getPracticeArea(practice_area_id);
			practice_area_str = practice_area.getLabel();
		}
		
		String value = "{\"desc\" : \"" + _code.getDescription() + "\"," +
						" \"id\" : \"" + _code.getId() + "\"," +
						" \"type\" : \"" + type_str + "\"," +
						" \"cost\" : \"" + _code.getOrderCostString() + "\"," +
						" \"com\" : \"" + (_code.isCommissionable() ? "X" : "") + "\"," +
						" \"sync\" : \"" + (_code.isSynced() ? "X" : "") + "\"," +
						" \"dept\" : \"" + _code.getDepartmentString() + "\"," +
						" \"pa\" : \"" + practice_area_str + "\" }";
		return "<code><key>" + _code.getId() + "</key><value><![CDATA[" + value + "]]></value></code>";
	}

	/*
	public static String
	getXML(SoapStatement _statement)
		throws Exception
	{
		String type_str = "";
		switch (_code.getType())
		{
			case CheckoutCodeBean.PROCEDURE_TYPE: type_str = "Procedure"; break;
			case CheckoutCodeBean.INVENTORY_TYPE: type_str = "Inventory"; break;
			case CheckoutCodeBean.PLAN_TYPE: type_str = "Plan"; break;
			case CheckoutCodeBean.GROUP_TYPE: type_str = "Group"; break;
			case CheckoutCodeBean.PAYMENT_TYPE: type_str = "Payment"; break;
		}
		String practice_area_str = "";
		int practice_area_id = _code.getPracticeAreaId();
		if (practice_area_id > 0)
		{
			PracticeAreaBean practice_area = PracticeAreaBean.getPracticeArea(practice_area_id);
			practice_area_str = practice_area.getLabel();
		}
		
		String value = "{\"desc\" : \"" + _code.getDescription() + "\"," +
						" \"id\" : \"" + _code.getId() + "\"," +
						" \"type\" : \"" + type_str + "\"," +
						" \"pa\" : \"" + practice_area_str + "\" }";
		return "<code><key>" + _code.getId() + "</key><value><![CDATA[" + value + "]]></value></code>";
	}
	*/
	
	
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
	
	/*
	private String
	doHerbSpreadsheet(HerbDosage _dosage) throws java.io.IOException, TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		UKOnlineCourseReportLister listerObj = new UKOnlineCourseReportLister();

		short column_index = 0;
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
		listerObj.addCell(sheet, 17, (short)2, _dosage.getDescription());
		Iterator useIterator = _dosage.getUseDirections().iterator();
		int rowU = 19;
		for (; useIterator.hasNext(); rowU++) {
			HerbDosageUseDirections useDirectionsObj = (HerbDosageUseDirections)useIterator.next();
			listerObj.addCell(sheet, rowU, (short)2, useDirectionsObj.getLabel());
		}
		listerObj.addCell(sheet, 24, (short)2, CUBean.getUserDateString(_dosage.getMixDate()));
		listerObj.addCell(sheet, 27, (short)2, _dosage.getNotes());
		listerObj.addCell(sheet, 24, (short)7, _dosage.getMixer().getInitials());
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

		listerObj.addCell(sheet, row_index + 3, (short)5, tots_ml);
		

		String saveFilename = "herb-dosage-" + _dosage.getClient().getLabel() + "-" + System.currentTimeMillis() + ".xls";
		FileOutputStream fileOut = new FileOutputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + saveFilename);
		wb.write(fileOut);
		fileOut.close();
    
		return saveFilename;
	}
	*/

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
