/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.servlets;

import com.badiyan.torque.CheckoutOrderline;
import com.badiyan.torque.InventoryCountItemMapping;
import com.badiyan.torque.PurchaseOrderItemMapping;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.beans.OrderBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.PDF.PurchaseOrderBuilder;
import com.badiyan.uk.online.beans.CheckoutCodeBean;
import com.badiyan.uk.online.beans.InventoryCount;
import com.badiyan.uk.online.beans.InventoryDepartment;
import com.badiyan.uk.online.beans.ItemOnHandHistoryReport;
import com.badiyan.uk.online.beans.PracticeAreaBean;
import com.badiyan.uk.online.beans.ProductWaitList;
import com.badiyan.uk.online.beans.PurchaseOrder;
import com.badiyan.uk.online.beans.QuickBooksSettings;
import com.badiyan.uk.online.beans.UKOnlineCompanyBean;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import com.badiyan.uk.online.beans.ValeoTaxCodeBean;
import com.badiyan.uk.online.struts.CheckoutCodeAction;
import com.valeo.qb.data.AccountRet;
import com.valeo.qb.data.ItemLineRet;
import com.valeo.qb.data.ItemReceiptRet;
import com.valeo.qb.data.SalesTaxCodeRet;
import com.valeo.qb.data.VendorRet;
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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.torque.TorqueException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author marlo
 */
public class InventoryServlet2 extends HttpServlet {
	
	public static HashMap<String,PurchaseOrder> focused_po_hash = new HashMap<String,PurchaseOrder>(11);

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
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
			
			/*
			HttpSession session = _request.getSession(true);
			System.out.println("session >" + session.getId());
			*/
			
			if (command.equals("getCheckoutCodesByDescInv")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				VendorRet vendor = null;
				if ( (arg3 != null) ) {
					int vendor_id = Integer.parseInt(arg3);
					if (vendor_id > 0) {
						vendor = VendorRet.getVendor(vendor_id);
					}
				}
				
				String searchType = "search";
				if (arg4 != null) {
					searchType = arg4;
				}
				
				boolean short_result = false;
				if ( (arg5 != null) && arg5.equals("true") ) {
					short_result = true;
				}
				
				boolean show_only_low_stock = false;
				if ( (arg6 != null) && arg6.equals("true") ) {
					show_only_low_stock = true;
				}
				
				boolean show_inventory_count_stuff = false;
				if ( (arg7 != null) && arg7.equals("true") ) {
					show_inventory_count_stuff = true;
				}

				//session.setAttribute("invSearchStr", item_search_str);
				//session.setAttribute("vendorSearchStr", vendor_search_str);
				boolean needs_comma = false;
				StringBuilder b = new StringBuilder();
				b.append("{\"code\":[");
				Iterator code_itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(selected_bu, arg2, vendor, searchType, show_only_low_stock).iterator();
				while (code_itr.hasNext()) {
					if (needs_comma) { b.append(','); }
					CheckoutCodeBean code = (CheckoutCodeBean)code_itr.next();
					if (show_inventory_count_stuff) {
						InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(selected_bu);
						int count = inventory_count.getCount(code);
						b.append(this.toJSONInventoryCount(code, count));
					} else if (short_result) {
						b.append(this.toJSONShortShowStock(code));
					} else {
						b.append(this.toJSON(code));
					}
					needs_comma = true;
				}
				b.append("]}");
				writer.println(b.toString());
			} else if (command.equals("getInventoryStats")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				CheckoutCodeBean code = null;
				if (arg2 == null || arg2.equals("0")) {
					code = new CheckoutCodeBean();
				} else {
					code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg2));
				}
				
				writer.println(this.toStatsJSON(code, true, selected_bu));
				
			} else if (command.equals("saveInventoryDetail")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				writer.println(this.saveInventoryDetail(arg1, selected_bu, logged_in_person, arg2));
				
			} else if (command.equals("printPurchaseOrder")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				//writer.println(this.saveInventoryDetail(arg1, selected_bu, logged_in_person, arg2));
				
				PurchaseOrder purchase_order = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
				
				String report_url = CUBean.getProperty("cu.webDomain") + "/resources/pdf/" + PurchaseOrderBuilder.generatePurchaseOrder(selected_bu, purchase_order);
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Report Complete\",\"text\":\"Purchase Order " + purchase_order.getPurchaseOrderNumber(selected_bu) + ".\",\"pdfURL\":\"" + JSONObject.escape(report_url) + "\"}]}");
				writer.println(b.toString());
				
			} else if (command.equals("exportToCSVFile")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				//writer.println(this.saveInventoryDetail(arg1, selected_bu, logged_in_person, arg2));
				
				PurchaseOrder purchase_order = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
				
				//String report_url = CUBean.getProperty("cu.webDomain") + "/resources/pdf/" + PurchaseOrderBuilder.generatePurchaseOrder(selected_bu, purchase_order);
				String csv_url = purchase_order.toCSVFile(selected_bu);
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Report Complete\",\"text\":\"Purchase Order " + purchase_order.getPurchaseOrderNumber(selected_bu) + ".\",\"csvURL\":\"" + JSONObject.escape(csv_url) + "\"}]}");
				writer.println(b.toString());
				
			} else if (command.equals("deletePurchaseOrder")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				//PurchaseOrder purchase_order = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
				PurchaseOrder.delete(selected_bu, Integer.parseInt(arg2));
				
				//String report_url = CUBean.getProperty("cu.webDomain") + "/resources/pdf/" + PurchaseOrderBuilder.generatePurchaseOrder(selected_bu, purchase_order);
				//String csv_url = purchase_order.toCSVFile(selected_bu);
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Purchase Order Deleted\",\"text\":\"Purchase Order Deleted.\"}]}");
				writer.println(b.toString());
				
			} else if (command.equals("getPurchaseOrders")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				Iterator itr = null;
				if (arg2 == null || arg2.isEmpty()) {
					itr = PurchaseOrder.getOpenPurchaseOrders(selected_bu).iterator();
				} else {
					itr = PurchaseOrder.getPurchaseOrders(selected_bu, arg2).iterator();
				}
				
				boolean needs_comma = false;
				StringBuilder b = new StringBuilder();
				b.append("{\"purchaseOrder\":[");
				while (itr.hasNext()) {
					if (needs_comma) { b.append(','); }
					PurchaseOrder obj = (PurchaseOrder)itr.next();
					b.append(this.toJSON(obj, selected_bu));
					needs_comma = true;
				}
				b.append("]}");
				writer.println(b.toString());
				
			} else if (command.equals("getPurchaseOrderStats")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				PurchaseOrder obj = null;
				if ( (arg2 != null) && !arg2.equals("0")) {
					/*
					if (arg2.equals("0")) {
						obj = new PurchaseOrder();
						obj.setCompany(selected_bu);
						obj.setCreateOrModifyPerson(logged_in_person);
					} else {
						obj = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
					}
					*/
					obj = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
					focused_po_hash.put(arg1, obj);
				} else {
					//obj = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
					obj = focused_po_hash.get(arg1);
					if (obj == null) {
						obj = new PurchaseOrder();
						obj.setCompany(selected_bu);
						obj.setCreateOrModifyPerson(logged_in_person);
						focused_po_hash.put(arg1, obj);
					} else {
						// ensure that if PO zero was requested, that the PO in the hash is new
						if (!obj.isNew()) {
							// create a new one instead
							obj = new PurchaseOrder();
							obj.setCompany(selected_bu);
							obj.setCreateOrModifyPerson(logged_in_person);
							focused_po_hash.put(arg1, obj);
						}
					}
				}
				
				if (obj.isNew()) {
					if (arg3 != null && !arg3.equals("0")) {
						VendorRet selected_vendor = VendorRet.getVendor(Integer.parseInt(arg3));
						obj.setVendor(selected_vendor);
					} else if ( (arg4 != null) && !arg4.equals("0") && (arg4.length() > 0) ) {
						VendorRet selected_vendor = VendorRet.getVendor(Integer.parseInt(arg4));
						obj.setVendor(selected_vendor);
					}
				}
				
				//PurchaseOrder obj = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
				writer.println(this.toStatsJSON(obj, true, selected_bu, true));
				
			} else if (command.equals("savePurchaseOrder")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				writer.println(this.savePurchaseOrder(arg1, selected_bu, logged_in_person, arg2));
				
			} else if (command.equals("addItemToPurchaseOrder")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				writer.println(this.addItemToPurchaseOrder(arg1, selected_bu, logged_in_person, arg2));
				
			} else if (command.equals("removePurchaseOrderItem")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				PurchaseOrder obj = null;
				if (arg2 != null && !arg2.isEmpty() && !arg2.equals("0")) {
					obj = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
				} else {
					//obj = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
					obj = focused_po_hash.get(arg1); // should exist at this point, I'm thinking
				}
				
				CheckoutCodeBean code_to_remove = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg3));
				System.out.println("found item to remove >" + code_to_remove.getLabel());
				
				int index_to_remove = Integer.parseInt(arg4);
				
				Vector item_vec = obj.getItems();
				Iterator itr = item_vec.iterator();
				for (int i = 0; itr.hasNext(); i++) {
					PurchaseOrderItemMapping item_mapping = (PurchaseOrderItemMapping)itr.next();
					CheckoutCodeBean mapped_item = CheckoutCodeBean.getCheckoutCode(item_mapping.getCheckoutCodeId());
					if (i == index_to_remove) {
						if (code_to_remove.getId() == mapped_item.getId()) {
							item_vec.removeElementAt(i);
							if (item_mapping.getProductWaitListDbId() > 0) {
								try {
									ProductWaitList associated_wait_list_item = ProductWaitList.getProductWaitListEntry(item_mapping.getProductWaitListDbId());
									associated_wait_list_item.removeMapping();
								} catch (Exception x) {
									x.printStackTrace();
								}
							}
							break;
						}
					}
				}
				obj.setItems(item_vec);
				obj.save(); // added to avoid potential confusion - not 100% sure
				
				writer.println(this.toStatsJSON(obj, true, selected_bu, true));
				
			} else if (command.equals("selectPurchaseOrderItem")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				boolean is_numeric_keyword = StringUtils.isNumeric(arg2);
				if (is_numeric_keyword) {
					CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg2));
					writer.println(this.toJSONSelectItemStuff(BigDecimal.ONE, obj.getOrderCost()));
				} else {
					// wait list item selected
					ProductWaitList obj = ProductWaitList.getProductWaitListEntry(Integer.parseInt(arg2.substring(1)));
					writer.println(this.toJSONSelectItemStuff(obj.getQuantity(), obj.getProduct().getOrderCost()));
				}
				
			} else if (command.equals("receiveItems")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				writer.println(this.receivePurchaseOrder(arg1, selected_bu, logged_in_person, arg2));
				
			} else if (command.equals("saveWaitListChanges")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				//writer.println(this.receivePurchaseOrder(arg1, selected_bu, logged_in_person, arg2));
				//jhgjhg;
				
				writer.println(this.saveWaitListChanges(logged_in_person, arg2));
				
			} else if (command.equals("recordItemCount")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(selected_bu);
				CheckoutCodeBean code_obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg2));
				inventory_count.addCount(code_obj, Integer.parseInt(arg3));
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"text\":\"Count recorded for " + JSONObject.escape(code_obj.getLabel()) + "\"}]}");
				writer.println(b.toString());
				
			} else if (command.equals("recordReorderPoint")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				//InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(selected_bu);
				CheckoutCodeBean code_obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(arg2));
				//inventory_count.addCount(code_obj, Integer.parseInt(arg3));
				short reorderPoint = Short.parseShort(arg3);
				if (reorderPoint != code_obj.getReorderPoint()) {
					code_obj.setReorderPoint(reorderPoint);
					code_obj.save();
					
					StringBuffer b = new StringBuffer();
					b.append("{\"message\":[{\"text\":\"Reorder point recorded for " + JSONObject.escape(code_obj.getLabel()) + "\"}]}");
					writer.println(b.toString());
				}
				
			} else if (command.equals("saveCount")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(selected_bu);
				inventory_count.setCreateOrModifyPerson(logged_in_person);
				inventory_count.save();
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"text\":\"Count saved\"}]}");
				writer.println(b.toString());
				
			} else if (command.equals("finishCount")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				InventoryCount inventory_count = InventoryCount.getActiveInventoryCount(selected_bu);
				inventory_count.save();
				inventory_count.setCreateOrModifyPerson(logged_in_person);
				inventory_count.finishAndApplyChanges();
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"text\":\"Count finished\"}]}");
				writer.println(b.toString());
				
			} else if (command.equals("generateOnHandHistoryReport")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				VendorRet vendor = null;
				if ( (arg3 != null) ) {
					int vendor_id = Integer.parseInt(arg3);
					if (vendor_id > 0) {
						vendor = VendorRet.getVendor(vendor_id);
					}
				}
				
				String searchType = "search";
				if (arg4 != null) {
					searchType = arg4;
				}
				
				boolean short_result = false;
				if ( (arg5 != null) && arg5.equals("true") ) {
					short_result = true;
				}
				
				boolean show_only_low_stock = false;
				if ( (arg6 != null) && arg6.equals("true") ) {
					show_only_low_stock = true;
				}
				
				boolean show_inventory_count_stuff = false;
				if ( (arg7 != null) && arg7.equals("true") ) {
					show_inventory_count_stuff = true;
				}
				
				// generate initial on-hand history report
				
				// grab stuff from CheckoutOrderline (does this include returns??)
				// seems to include returns, however, it doesn't look like returns are being sent to QB??!?
				
				
				boolean runReport = false;
				Date lastGenerationDate = ItemOnHandHistoryReport.getLastGenerationDate();
				System.out.println("lastGenerationDate >" + lastGenerationDate);
				if (lastGenerationDate == null) {
					runReport = true;
				} else {
					Calendar now = Calendar.getInstance();
					now.add(Calendar.HOUR, -1);
					if (now.getTime().after(lastGenerationDate)) {
						runReport = true;
					}
				}
				
				System.out.println("runReport >" + runReport);
				
				if (runReport) {

					// 1. delete the records in the report table

					ItemOnHandHistoryReport.delete(selected_bu);

					// 2. get the last inventory count date

					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd");

					InventoryCount lastCount = InventoryCount.getLastCompletedInventoryCount(selected_bu);
					if (lastCount != null) {
						Date lastCountDate = lastCount.getInventoryCountCompletionDate();
						System.out.println("lastCountDate >" + simpleDateFormat.format(lastCountDate));

						HashMap<CheckoutCodeBean,BigDecimal> runningCountHash = new HashMap<>();

						// 3. create report entries from the inventory count

						Vector<InventoryCountItemMapping> count = lastCount.getItems();
						count.forEach(countMap -> {
							try {
								CheckoutCodeBean codeKey = CheckoutCodeBean.getCheckoutCode(countMap.getCheckoutCode());
								System.out.println("codeKey(1) >" + codeKey.getLabel() + " >" + countMap.getCount());
								runningCountHash.put(codeKey, countMap.getCount());

								ItemOnHandHistoryReport report = new ItemOnHandHistoryReport();
								report.setCompany(selected_bu);
								report.setChangeDate(lastCountDate);
								report.setChangeType("InventoryCount");
								report.setCheckoutCode(codeKey);
								report.setOnHandQuantityDelta(BigDecimal.ZERO); // not sure about this...
								report.setOnHandQuantityUpdated(countMap.getCount());
								report.save();
							} catch (Exception x) {
								x.printStackTrace();
							}
						});

						// 4. get all purchase order receipts since last inventory count
						
						HashMap<String,List<ItemOnHandHistoryReport>> reportHash = new HashMap<>();
						

						PurchaseOrder.getPurchaseOrders(selected_bu).forEach(po -> {
							try {
								PurchaseOrder purchaseOrder = (PurchaseOrder)po;
								purchaseOrder.getItems().forEach(m -> {
									PurchaseOrderItemMapping mapping = (PurchaseOrderItemMapping)m;
									Date received_date = mapping.getRowUpdated();
									if (received_date == null) {
										received_date = purchaseOrder.getPurchaseOrderDate();
										// get the received date from the purchase order date if it's not set, I guess
									}
									if (received_date.after(lastCountDate)) {
										try {
											BigDecimal quantityReceived = mapping.getQuantityReceived();
											CheckoutCodeBean codeKey = CheckoutCodeBean.getCheckoutCode(mapping.getCheckoutCode());
											System.out.println("codeKey(2) >" + codeKey.getLabel() + " >" + quantityReceived);

											ItemOnHandHistoryReport report = new ItemOnHandHistoryReport();
											report.setCompany(selected_bu);
											report.setChangeDate(received_date);
											report.setChangeType("PurchaseOrderReceipt");
											report.setCheckoutCode(CheckoutCodeBean.getCheckoutCode(mapping.getCheckoutCodeId()));
											report.setOnHandQuantityDelta(quantityReceived);
											
											String dateKey = simpleDateFormat.format(received_date);
											List<ItemOnHandHistoryReport> reportList = reportHash.get(dateKey);
											if (reportList == null) {
												reportHash.put(dateKey, new ArrayList<ItemOnHandHistoryReport>());
											}
											reportList.add(report);
											
										} catch (Exception x) {
											x.printStackTrace();
										}
									}
								});
							} catch (Exception x) {
								x.printStackTrace();
							}
						});

						// 5. get the orders since the last inventory count

						BigDecimal negativeOne = new BigDecimal(-1);
						Vector orders = OrderBean.getOrders(selected_bu, lastCountDate, new Date());
						for (Object obj : orders) {
							OrderBean order = (OrderBean)obj;
							order.getOrdersVec().forEach(o -> {
								try {
									CheckoutOrderline orderline = (CheckoutOrderline)o;
									CheckoutCodeBean codeKey = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
									if (codeKey.getType() == CheckoutCodeBean.INVENTORY_TYPE) {
										BigDecimal quantitySoldOrReturned = orderline.getQuantity();
										if (order.getStatus().equals(OrderBean.SALES_RECEIPT_ORDER_STATUS)) {
											quantitySoldOrReturned = quantitySoldOrReturned.multiply(negativeOne);
										}
										System.out.println("codeKey(3) >" + codeKey.getLabel() + " >" + quantitySoldOrReturned);

										if (!order.getStatus().equals(OrderBean.REVERSED_RECEIPT_ORDER_STATUS)) { // skip reversed receipts entirely
											ItemOnHandHistoryReport report = new ItemOnHandHistoryReport();
											report.setCompany(selected_bu);
											report.setChangeDate(order.getOrderDate());
											report.setChangeType(order.getStatus());
											report.setCheckoutCode(codeKey);
											report.setOnHandQuantityDelta(quantitySoldOrReturned); // returns are negative

											String dateKey = simpleDateFormat.format(order.getOrderDate());
											List<ItemOnHandHistoryReport> reportList = reportHash.get(dateKey);
											if (reportList == null) {
												reportHash.put(dateKey, new ArrayList<ItemOnHandHistoryReport>());
											}
											reportList.add(report);
										}
										
									}
								} catch (Exception x) {
									x.printStackTrace();
								}
							});
						}
						
						Calendar lastCountCal = Calendar.getInstance();
						lastCountCal.setTime(lastCountDate);
						Date now = new Date();
						
						while (lastCountCal.getTime().before(now)) {
							
							String dateKey = simpleDateFormat.format(lastCountCal.getTime());
							List<ItemOnHandHistoryReport> reportList = reportHash.get(dateKey);
							if (reportList != null) {
								reportList.forEach(report -> {
									try {
										CheckoutCodeBean codeKey = report.getCheckoutCode();
										BigDecimal runningCount = runningCountHash.get(codeKey);
										if (runningCount == null) {
											runningCount = new BigDecimal(0);
											System.out.println("null running count");
										}
										runningCount = runningCount.add(report.getOnHandQuantityDelta());
										runningCountHash.put(codeKey, runningCount);
										
										report.setOnHandQuantityUpdated(runningCount);
										report.save();
									} catch (Exception x) {
										x.printStackTrace();
									}
								});
								
							}
							
							lastCountCal.add(Calendar.DATE, 1);
						}
						
					}
				}
				
				boolean needs_comma = false;
				StringBuilder b = new StringBuilder();
				b.append("{\"code\":[");
				//Iterator code_itr = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(selected_bu, arg2, vendor, searchType, show_only_low_stock).iterator();
				Vector vec = CheckoutCodeBean.getCheckoutCodesByDescAndVendor(selected_bu, arg2, vendor, searchType, show_only_low_stock);
				Iterator itr = ItemOnHandHistoryReport.getItemOnHandHistoryReport(vec).iterator();
				
				while (itr.hasNext()) {
					if (needs_comma) { b.append(','); }
					ItemOnHandHistoryReport report = (ItemOnHandHistoryReport)itr.next();
					b.append(this.toJSONInventoryReport(report.getCheckoutCode(), report));
					needs_comma = true;
				}
				b.append("]}");
				writer.println(b.toString());
				
				
			} else {
				throw new IllegalValueException("Command not implemented >" + command);
			}
		} catch (Exception x) {
			x.printStackTrace();
			
			StringBuffer b = new StringBuffer();
			b.append("{\"message\":[");
			b.append("{\"type\":\"danger\"," +
					"\"heading\":\"Oh snap!\"," +
					"\"text\":\"" + JSONObject.escape(x.getMessage()) + "\"}");
			b.append("]}");
			
			_response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			writer.println(b.toString());
		} finally {			
			writer.close();
		}
	}
	
	private String toJSON(CheckoutCodeBean _code) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_code.getLabel()) + "\",");
		if (_code.getType() == CheckoutCodeBean.INVENTORY_TYPE) {
			b.append("\"qtyOnHand\":" + _code.getOnHandQuantity() + ",");
		}
		b.append("\"amount\":\"" + _code.getAmountString() + "\",");
		b.append("\"taxable\":" + _code.isTaxable()+ ",");
		b.append("\"type\":\"" + _code.getTypeString() + "\",");
		b.append("\"dept\":\"" + JSONObject.escape(_code.getDepartmentString()) + "\",");
		b.append("\"pa\":\"" + JSONObject.escape(_code.getPracticeAreaString()) + "\",");
		b.append("\"vendor\":\"" + JSONObject.escape(_code.getVendorString()) + "\",");
		b.append("\"com\":" + _code.isCommissionable() + ",");
		b.append("\"sync\":" + _code.isSynced() + ",");
		b.append("\"id\":" + _code.getId() + "}");
		return b.toString();
	}
	
	private String toJSONShort(CheckoutCodeBean _code) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_code.getLabel()) + "\",");
		b.append("\"id\":" + _code.getId() + "}");
		return b.toString();
	}
	
	private String toJSONShortShowStock(CheckoutCodeBean _code) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		if (_code.isLowInStock()) {
			b.append("{\"label\":\"" + JSONObject.escape(_code.getLabel()) + " [" + _code.getOnHandQuantityString() + "]" +  "\",");
		} else {
			b.append("{\"label\":\"" + JSONObject.escape(_code.getLabel()) +  "\",");
		}
		b.append("\"id\":" + _code.getId() + "}");
		return b.toString();
	}
	
	private String toJSONInventoryCount(CheckoutCodeBean _code, int _count) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		short on_hand = _code.getOnHandQuantity();
		if (on_hand < (short)0) {
			on_hand = (short)0;
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_code.getLabel()) +  "\",");
		b.append("\"qtyOnHand\":" + on_hand + ",");
		if (_count > 0) {
			b.append("\"count\":" + _count + ",");
			b.append("\"missing\":" + (on_hand - _count) + ",");
		}
		if (_code.getReorderPoint() > 0) {
			b.append("\"reorderPoint\":" + _code.getReorderPoint() + ",");
		}
		b.append("\"id\":" + _code.getId() + "}");
		return b.toString();
	}
	
	private String toJSONInventoryReport(CheckoutCodeBean _code, ItemOnHandHistoryReport _report) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		/*
		short on_hand = _code.getOnHandQuantity();
		if (on_hand < (short)0) {
			on_hand = (short)0;
		}
		*/
		
		BigDecimal on_hand = _report.getOnHandQuantityUpdated();
		BigDecimal delta = _report.getOnHandQuantityDelta();
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_code.getLabel()) +  "\",");
		b.append("\"qtyOnHand\":\"" + InventoryServlet2.getStringFromBigDecimal(on_hand) + "\",");
		b.append("\"delta\":\"" + InventoryServlet2.getStringFromBigDecimal(delta) + "\",");
		b.append("\"changeDate\":\"" + JSONObject.escape(_report.getChangeDateString()) + "\",");
		b.append("\"changeType\":\"" + JSONObject.escape(_report.getChangeType()) + "\",");
		
		/*
		if (_count > 0) {
			b.append("\"count\":" + _count + ",");
			b.append("\"missing\":" + (on_hand - _count) + ",");
		}
		*/
		
		b.append("\"id\":" + _code.getId() + "}");
		return b.toString();
	}
	
	private String toStatsJSON(CheckoutCodeBean _code, boolean _include_dropdown_info, UKOnlineCompanyBean _bu) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_code.getLabel()) + "\",");
		if (_code.getType() == CheckoutCodeBean.INVENTORY_TYPE) {
			b.append("\"qtyOnHand\":" + _code.getOnHandQuantity() + ",");
			b.append("\"reorderPoint\":" + _code.getReorderPoint() + ",");
			b.append("\"availableQty\":" + _code.getAvailableQuantity() + ",");
		}
		//b.append("\"itemNumber\":\"" + _code.getItemNumberString() + "\",");
		b.append("\"itemNumber\":\"" + _code.getVendorProductNumberString() + "\",");
		b.append("\"upc\":\"" + _code.getUPCString() + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_code.getSalesDescriptionString()) + "\",");
		b.append("\"amount\":\"" + _code.getAmountString() + "\",");
		b.append("\"orderCost\":\"" + _code.getOrderCostString() + "\",");
		b.append("\"averageUnitCost\":\"" + _code.getAverageUnitCostString() + "\",");
		b.append("\"active\":" + _code.isActive()+ ",");
		//b.append("\"taxable\":" + _code.isTaxable()+ ",");
		b.append("\"taxCodeId\":" + _code.getSalesTaxCodeId() + ",");
		b.append("\"typeId\":" + _code.getType() + ",");
		b.append("\"departmentId\":" + _code.getDepartmentId() + ",");
		b.append("\"code\":\"" + _code.getCodeString() + "\",");
		b.append("\"dept\":\"" + JSONObject.escape(_code.getDepartmentString()) + "\",");
		b.append("\"paId\":" + _code.getPracticeAreaId() + ",");
		b.append("\"vendorId\":" + _code.getVendorId() + ",");
		//b.append("\"vendor\":\"" + JSONObject.escape(_code.getVendorString()) + "\",");
		b.append("\"status\":\"" + JSONObject.escape(_code.getStatusString()) + "\",");
		b.append("\"isPlanUse\":" + _code.isPlanUse() + ",");
		
		b.append("\"expenseAccountId\":" + _code.getExpenseAccountId() + ",");
		b.append("\"incomeAccountId\":" + _code.getIncomeAccountId() + ",");
		b.append("\"COGSAccountId\":" + _code.getCOGSAccountId() + ",");
		b.append("\"assetAccountId\":" + _code.getAssetAccountId() + ",");
		
		b.append("\"com\":" + _code.isCommissionable() + ",");
		b.append("\"sync\":" + _code.isSynced() + ",");
		
		if (_include_dropdown_info) {
			
			boolean needs_comma = false;
			
			b.append("\"vendor\":[");
			Iterator itr = VendorRet.getVendors(_bu).iterator();
			while (itr.hasNext()) {
				VendorRet obj = (VendorRet)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(obj.getLabel(), obj.getValue()));
				needs_comma = true;
				//<option value="<%= vendor_obj.getValue() %>"<%= (adminCheckoutCode.getVendorId() == vendor_obj.getId()) ? " selected" : ""  %>><%= vendor_obj.getLabel() %></option>
			}
			b.append("],");
			
			needs_comma = false;
			itr = InventoryDepartment.getInventoryDepartments(_bu).iterator();
			b.append("\"department\":[");
			while (itr.hasNext()) {
				InventoryDepartment obj = (InventoryDepartment)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(obj.getLabel(), obj.getValue()));
				needs_comma = true;
			}
			b.append("],");
			
			needs_comma = false;
			itr = ValeoTaxCodeBean.getTaxCodes(_bu).iterator();
			b.append("\"tax\":[");
			while (itr.hasNext()) {
				ValeoTaxCodeBean obj = (ValeoTaxCodeBean)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(obj.getLabel(), obj.getValue()));
				needs_comma = true;
			}
			b.append("],");
			
			b.append("\"type\":[");
			b.append(this.toJSON("Service", CheckoutCodeBean.PROCEDURE_TYPE)); b.append(',');
			b.append(this.toJSON("Inventory", CheckoutCodeBean.INVENTORY_TYPE)); b.append(',');
			b.append(this.toJSON("Non Inventory", CheckoutCodeBean.NON_INVENTORY_TYPE)); b.append(',');
			b.append(this.toJSON("Group", CheckoutCodeBean.GROUP_TYPE)); b.append(',');
			b.append(this.toJSON("Gift Card", CheckoutCodeBean.GIFT_CARD)); b.append(',');
			b.append(this.toJSON("Gift Certificate", CheckoutCodeBean.GIFT_CERTIFICATE)); b.append(',');
			//b.append(this.toJSON("Payment", CheckoutCodeBean.PAYMENT_TYPE)); b.append(',');
			b.append(this.toJSON("Payment Plan", CheckoutCodeBean.PLAN_TYPE)); b.append(',');
			b.append(this.toJSON("Subtotal", CheckoutCodeBean.SUBTOTAL));	
			b.append("],");
			
			needs_comma = false;
			itr = PracticeAreaBean.getPracticeAreas(_bu).iterator();
			b.append("\"practiceArea\":[");
			while (itr.hasNext()) {
				PracticeAreaBean obj = (PracticeAreaBean)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(obj.getLabel(), obj.getValue()));
				needs_comma = true;
			}
			b.append("],");
			
			needs_comma = false;
			QuickBooksSettings qb_settings = _bu.getQuickBooksSettings();
			if (qb_settings.isQuickBooksFSEnabled()) {
				itr = AccountRet.getAccounts(_bu).iterator();
				b.append("\"account\":[");
				while (itr.hasNext()) {
					AccountRet obj = (AccountRet)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(obj.getLabel(), obj.getValue()));
					needs_comma = true;
				}
				b.append("],");
			}
			
			needs_comma = false;
			itr = SalesTaxCodeRet.getSalesTaxCodes(_bu).iterator();
			b.append("\"taxCode\":[");
			while (itr.hasNext()) {
				SalesTaxCodeRet obj = (SalesTaxCodeRet)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(obj.getLabel(), obj.getValue()));
				needs_comma = true;
			}
			b.append("],");
			
		}
		
		b.append("\"id\":" + _code.getId() + "}");
		return b.toString();
	}
	
	private String toJSONSelectItemStuff(BigDecimal _qty, BigDecimal _cost) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		BigDecimal amount = _qty.multiply(_cost);
		StringBuilder b = new StringBuilder();
		b.append("{\"qty\":" + _qty.setScale(0, BigDecimal.ROUND_HALF_UP).toString() + ",");
		b.append("\"cost\":\"" + _cost.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		b.append("\"amount\":\"" + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"}");
		return b.toString();
	}
	
	private String toJSON(PurchaseOrder _obj, UKOnlineCompanyBean _selected_bu) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"poNumber\":\"" + _obj.getPurchaseOrderNumber(_selected_bu) + "\",");
		b.append("\"date\":\"" + JSONObject.escape(_obj.getPurchaseOrderDateString()) + "\",");
		b.append("\"vendor\":\"" + JSONObject.escape(_obj.getVendor().getLabel()) + "\",");
		b.append("\"status\":\"" + _obj.getStatus() + "\",");
		b.append("\"total\":\"" + _obj.getTotalString() + "\",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
	private String toStatsJSON(PurchaseOrder _obj, boolean _include_dropdown_info, UKOnlineCompanyBean _bu, boolean _only_common_vendors) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		float total_received_tax = 0f;
		float total_received_shipping = 0f;
		float total_received_discount = 0f;
		Iterator itr = ItemReceiptRet.getItemReceipts(_obj).iterator();
		while (itr.hasNext()) {
			ItemReceiptRet item_receipt_obj = (ItemReceiptRet)itr.next();
			total_received_tax += item_receipt_obj.getTaxAmount();
			total_received_shipping += item_receipt_obj.getShippingAmount();
			total_received_discount += item_receipt_obj.getDiscountAmount();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"poNumber\":\"" + _obj.getPurchaseOrderNumber(_bu) + "\",");
		b.append("\"date\":\"" + JSONObject.escape(_obj.getPurchaseOrderDateString()) + "\",");
		b.append("\"selectedVendorId\":" + _obj.getVendorId() + ",");
		b.append("\"selectedVendor\":\"" + JSONObject.escape(_obj.getVendorStr()) + "\",");
		b.append("\"status\":\"" + _obj.getStatus() + "\",");
		b.append("\"total\":\"" + _obj.getTotalString() + "\",");
		b.append("\"note\":\"" + JSONObject.escape(_obj.getNoteString()) + "\",");
		boolean needs_comma = false;
		b.append("\"item\":[");
		itr = _obj.getItems().iterator();
		while (itr.hasNext()) {
			PurchaseOrderItemMapping item = (PurchaseOrderItemMapping)itr.next();
			
			// see if this is mapped to a wait list item
			ProductWaitList associated_wait_list_item = null;
			if (item.getProductWaitListDbId() > 0) {
				try {
					associated_wait_list_item = ProductWaitList.getProductWaitListEntry(item.getProductWaitListDbId());
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
			
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(item, associated_wait_list_item));
			needs_comma = true;
		}
		
		if (_obj.getVendorId() > 0) {
			//needs_comma = false;
			VendorRet vendor = VendorRet.getVendor(_obj.getVendorId());
			itr = ProductWaitList.getProductWaitList(vendor, true).iterator();
			while (itr.hasNext()) {
				ProductWaitList obj = (ProductWaitList)itr.next();
				System.out.println("   $$$" + obj.getLabel() + "  hasMapping >" + obj.hasMapping());
				if (!obj.hasMapping()) {
					if (needs_comma) { b.append(','); }
					//b.append(this.toJSON(obj.getLabelAlt(), obj.getValue()));
					b.append(this.toJSON(obj));
					needs_comma = true;
				}
			}
		}
		
		b.append("],");
		
		b.append("\"totalReceivedTax\":\"" + InventoryServlet2.getStringFromBigDecimal(new BigDecimal(total_received_tax)) + "\",");
		b.append("\"totalReceivedShipping\":\"" + InventoryServlet2.getStringFromBigDecimal(new BigDecimal(total_received_shipping)) + "\",");
		b.append("\"totalReceivedDiscount\":\"" + InventoryServlet2.getStringFromBigDecimal(new BigDecimal(total_received_discount)) + "\",");
		
		/*
		b.append("\"waitList\":[");
		if (_obj.getVendorId() > 0) {
			needs_comma = false;
			VendorRet vendor = VendorRet.getVendor(_obj.getVendorId());
			itr = ProductWaitList.getProductWaitList(vendor, true).iterator();
			while (itr.hasNext()) {
				ProductWaitList obj = (ProductWaitList)itr.next();
				if (needs_comma) { b.append(','); }
				//b.append(this.toJSON(obj.getLabelAlt(), obj.getValue()));
				b.append(this.toJSON(obj));
				needs_comma = true;
			}
		}
		b.append("],");
		*/
		
		if (_include_dropdown_info) {
			needs_comma = false;
			b.append("\"vendor\":[");
			if (_only_common_vendors) {
				itr = VendorRet.getCommonVendors(_bu).iterator();
			} else {
				itr = VendorRet.getVendors(_bu).iterator();
			}
			while (itr.hasNext()) {
				VendorRet obj = (VendorRet)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(obj.getLabel(), obj.getValue()));
				needs_comma = true;
			}
			b.append("],");
		}
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
		/*
		 *<table name="PURCHASE_ORDER_ITEM_MAPPING">
				<column name="PURCHASE_ORDER_DB_ID" required="true" type="INTEGER"/>
				<column name="CHECKOUT_CODE_ID" required="true" type="INTEGER"/>
				<column name="QUANTITY" required="true" scale="2" size="7" type="DECIMAL"/>
				<column name="RATE" required="true" scale="2" size="7" type="DECIMAL"/>
				<column name="AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>

				<foreign-key foreignTable="PURCHASE_ORDER_DB">
					<reference local="PURCHASE_ORDER_DB_ID" foreign="PURCHASE_ORDER_DB_ID"/>
				</foreign-key>
				<foreign-key foreignTable="CHECKOUT_CODE">
					<reference local="CHECKOUT_CODE_ID" foreign="CHECKOUT_CODE_ID"/>
				</foreign-key>
			</table>
		 */
	private String toJSON(PurchaseOrderItemMapping _obj, ProductWaitList _associated_wait_list_item) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		String quantity_received_str;
		if (_obj.getQuantityReceived() == null) {
			quantity_received_str = "0";
		} else {
			quantity_received_str = _obj.getQuantityReceived().setScale(0, BigDecimal.ROUND_HALF_UP).toString();
		}
		String quantity_str;
		if (_obj.getQuantity() == null) {
			quantity_str = "0";
		} else {
			quantity_str = _obj.getQuantity().setScale(0, BigDecimal.ROUND_HALF_UP).toString(); // shrug
		}
		
		String checkoutCodeLabel = CheckoutCodeBean.getCheckoutCode(_obj.getCheckoutCodeId()).getLabel();
		
		StringBuilder b = new StringBuilder();
		b.append("{\"qty\":" + quantity_str + ",");
		b.append("\"cost\":\"" + _obj.getRate().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		b.append("\"amount\":\"" + _obj.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		b.append("\"received\":" + quantity_received_str + ",");
		if (_associated_wait_list_item == null) {
			b.append("\"itemName\":\"" + JSONObject.escape(checkoutCodeLabel) + "\",");
		} else {
			b.append("\"itemName\":\"" + JSONObject.escape(checkoutCodeLabel + " [" + _associated_wait_list_item.getWaitingPerson().getLabel() + "]") + "\",");
		}
		b.append("\"itemId\":" + _obj.getCheckoutCodeId() + "}");
		return b.toString();
	}
	
	private String toJSON(ProductWaitList _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		//String quantity_received_str = "0";
		String quantity_str;
		if (_obj.getQuantity() == null) {
			quantity_str = "0";
		} else {
			quantity_str = _obj.getQuantity().setScale(0, BigDecimal.ROUND_HALF_UP).toString(); // shrug
		}
		
		CheckoutCodeBean product = _obj.getProduct();
		
		BigDecimal amount_bd = BigDecimal.ZERO;
		try {
			amount_bd = _obj.getQuantity().multiply(product.getOrderCost());
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"qty\":0,");
		b.append("\"cost\":\"" + product.getOrderCostString() + "\",");
		b.append("\"amount\":\"" + amount_bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
		b.append("\"received\":0,");
		b.append("\"itemName\":\"" + product.getLabel() + "\",");
		b.append("\"waitQty\":" + quantity_str + ",");
		b.append("\"waitingPerson\":\"" + _obj.getWaitingPerson().getLabel() + "\",");
		b.append("\"waitingPersonId\":\"" + _obj.getWaitingPerson().getId() + "\",");
		b.append("\"waitId\":" + _obj.getId() + ",");
		b.append("\"itemId\":" + product.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(String _label, String _value) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return "{\"label\":\"" + JSONObject.escape(_label) + "\",\"id\":" + _value + "}";
	}
	
	private String toJSON(String _label, short _value) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return "{\"label\":\"" + JSONObject.escape(_label) + "\",\"id\":" + _value + "}";
	}
	
	private String
	savePurchaseOrder(String _key, UKOnlineCompanyBean _company, UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
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
		
		int purchaseOrderNumber = 0;
		Date purchaseOrderDate = null;
		int id = 0;
		int vendorSelect = 0;
		String purchaseOrderNote = "";
		
		HashMap<CheckoutCodeBean,Integer> code_qty_hash = new HashMap<CheckoutCodeBean,Integer>();
		HashMap<CheckoutCodeBean,BigDecimal> code_cost_hash = new HashMap<CheckoutCodeBean,BigDecimal>();
		
		HashMap<ProductWaitList,Integer> wait_qty_hash = new HashMap<ProductWaitList,Integer>();
		HashMap<ProductWaitList,BigDecimal> wait_cost_hash = new HashMap<ProductWaitList,BigDecimal>();
		
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
							if (last_value.equals("purchaseOrderNumber")) {
								purchaseOrderNumber = Integer.parseInt(value_strX);
							} else if (last_value.equals("purchaseOrderDate")) {
								purchaseOrderDate = CUBean.getDateFromUserString(value_strX);
							} else if (last_value.equals("id")) {
								id = Integer.parseInt(value_strX);
							} else if (last_value.equals("vendorSelect")) {
								vendorSelect = Integer.parseInt(value_strX);
							} else if (last_value.equals("purchaseOrderNote")) {
								purchaseOrderNote = value_strX;
							} else if (last_value.startsWith("cost")) {
								CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(last_value.substring(4)));
								code_cost_hash.put(checkout_code_obj, new BigDecimal(value_strX));
							} else if (last_value.startsWith("qty")) {
								CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(last_value.substring(3)));
								code_qty_hash.put(checkout_code_obj, Integer.parseInt(value_strX));
							} else if (last_value.startsWith("wcost")) {
								ProductWaitList wait_list_obj = ProductWaitList.getProductWaitListEntry(Integer.parseInt(last_value.substring(5)));
								wait_cost_hash.put(wait_list_obj, new BigDecimal(value_strX));
								//code_cost_hash.put(checkout_code_obj, new BigDecimal(value_strX));
							} else if (last_value.startsWith("wqty")) {
								ProductWaitList wait_list_obj = ProductWaitList.getProductWaitListEntry(Integer.parseInt(last_value.substring(4)));
								if (!value_strX.isEmpty()) {
									wait_qty_hash.put(wait_list_obj, Integer.parseInt(value_strX));
									//code_qty_hash.put(checkout_code_obj, Integer.parseInt(value_strX));
								}
							}
						}
						
					}
				}
			}
		}
		
		if (vendorSelect == 0) {
			throw new IllegalValueException("Unable to save purchase order.  You must select a vendor.");
		}
		if (purchaseOrderDate == null) {
			throw new IllegalValueException("Unable to save purchase order.  You must specify a date.");
		}
		if (purchaseOrderNumber == 0) {
			throw new IllegalValueException("Unable to save purchase order.  You must specify a purchase order number.");
		}
		
		PurchaseOrder obj = null;
		if (id > 0) {
			obj = PurchaseOrder.getPurchaseOrder(id);
			focused_po_hash.put(_key, obj);
		} else {
			//obj = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
			obj = focused_po_hash.get(_key);
			if (obj == null) {
				obj = new PurchaseOrder();
				obj.setCompany(_company);
				obj.setCreateOrModifyPerson(_logged_in_person);
				focused_po_hash.put(_key, obj);
			}
		}
		
		//appointment_obj = AppointmentAction.saveAppointment(_company, _logged_in_peson, appointmentId, inputType, statusSelect, inputPractitioner, clientSelect, appointment_date, inputDuration, inputComment, (short)0, (short)3, (short)0, (short)2, (short)0, "", false);
		//CheckoutCodeBean code_obj = CheckoutCodeAction.saveCheckoutCode(_company, _logged_in_person, id, itemNumberInput, upc, "PoS", itemName, itemDesc, isActive, amount, orderCost, amountPerMLInput, averageUnitCost, typeSelect, vendorSelect, departmentSelect, practiceAreaSelect, isPlanUse, com, taxCodeSelect, qtyOnHand, reorderPoint, cogsAccountSelect, incomeAccountSelect, assetAccountSelect, expenseAccountSelect);
		
		obj.setPurchaseOrderNumber(purchaseOrderNumber);
		obj.setPurchaseOrderDate(purchaseOrderDate);
		obj.setVendor(VendorRet.getVendor(vendorSelect));
		obj.setNote(purchaseOrderNote);
		obj.setTax(BigDecimal.ZERO);
		obj.setShipping(BigDecimal.ZERO);
		obj.setDiscount(BigDecimal.ZERO);
		
		// save the mappings too
		
		// add mappings for wait items with a positive qty
		
		Vector purchase_order_items = obj.getItems();
		
		
		//BigDecimal po_total = BigDecimal.ZERO;
		
		Iterator mappings_itr = purchase_order_items.iterator();
		while (mappings_itr.hasNext()) {
			PurchaseOrderItemMapping mapping = (PurchaseOrderItemMapping)mappings_itr.next();
			CheckoutCodeBean mapped_code = CheckoutCodeBean.getCheckoutCode(mapping.getCheckoutCodeId());
			
			/*
			if (code_cost_hash.containsKey(mapped_code)) {
				BigDecimal cost_bd = code_cost_hash.get(mapped_code);
				mapping.setRate(cost_bd);
				//mapping.save();
			}
			if (code_qty_hash.containsKey(mapped_code)) {
				Integer qty = code_qty_hash.get(mapped_code);
				mapping.setQuantity(new BigDecimal(qty));
				//mapping.save();
			}
			*/
			//BigDecimal mapping_total = mapping.getQuantity().multiply(mapping.getRate());
			//po_total = po_total.add(mapping.)
			
			
			if (code_cost_hash.containsKey(mapped_code)) {
				BigDecimal cost_bd = code_cost_hash.get(mapped_code);
				mapping.setRate(cost_bd);
				mapping.setAmount(cost_bd.multiply(mapping.getQuantity()));
			}
			if (code_qty_hash.containsKey(mapped_code)) {
				BigDecimal qty = new BigDecimal(code_qty_hash.get(mapped_code));
				mapping.setQuantity(qty);
				mapping.setAmount(qty.multiply(mapping.getRate()));
			}
		}
		
		//Vector waitItems = null;
		Iterator new_mapping_itr = wait_cost_hash.keySet().iterator();
		while (new_mapping_itr.hasNext()) {
			ProductWaitList wait_list_obj = (ProductWaitList)new_mapping_itr.next();
			if (wait_qty_hash.containsKey(wait_list_obj)) {
				
				Integer qty_for_wait_item = (Integer)wait_qty_hash.get(wait_list_obj);
				if (qty_for_wait_item > 0) {
			
					// does a purchase order item mapping already exist for this wait item?
					// I'm thinking it generally shouldn't
					// but I'll go ahead and update the cost and qty
					
					PurchaseOrderItemMapping item_mapping = null;
					if (wait_list_obj.hasMapping()) {
						item_mapping = wait_list_obj.getMapping();
					} else {
						item_mapping = new PurchaseOrderItemMapping();
					}
					BigDecimal qty_bd = new BigDecimal(qty_for_wait_item);
					BigDecimal rate_bd = wait_cost_hash.get(wait_list_obj);
					BigDecimal amount = qty_bd.multiply(rate_bd);

					item_mapping.setAmount(amount);
					item_mapping.setCheckoutCodeId(wait_list_obj.getProduct().getId());
					item_mapping.setQuantity(qty_bd);
					item_mapping.setRate(rate_bd);
					item_mapping.setRowUpdated(new Date());
					
					item_mapping.setProductWaitListDbId(wait_list_obj.getId());
					wait_list_obj.addMapping(item_mapping);
					
					purchase_order_items.addElement(item_mapping);
					//wait_list_obj.setMapping(item_mapping);
					
					/*
					if (waitItems == null) {
						waitItems = new Vector();
					}
					waitItems.addElement(wait_list_obj);
					*/
				}
			}
		}
		
		obj.setItems(purchase_order_items);
		//obj.setWaitItems(waitItems);
		
		
		obj.save();
		
		StringBuffer b = new StringBuffer();
		if (id > 0) {
			//b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Purchase Order Saved\",\"text\":\"" + obj.getLabel() + " saved.\"}]}");
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Purchase Order Saved\",\"text\":\"" + obj.getLabel() + " saved.\",\"id\":" + obj.getId() + "}]}");
		} else {
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Purchase Order Created\",\"text\":\"" + obj.getLabel() + " created.\",\"id\":" + obj.getId() + "}]}");
			focused_po_hash.remove(_key);
		}
		
		return b.toString();        
	}
	
	private String
	addItemToPurchaseOrder(String _key, UKOnlineCompanyBean _company, UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
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
		
		int purchaseOrderNumber = 0;
		Date purchaseOrderDate = null;
		int id = 0;
		int vendorSelect = 0;
		String purchaseOrderNote = "";
		//CheckoutCodeBean checkout_code_to_add 
		
		int itemSearchResults = 0;
		int itemSearchQty = 0;
		String itemSearchCost = null;
		
		HashMap<CheckoutCodeBean,Integer> code_qty_hash = new HashMap<CheckoutCodeBean,Integer>();
		HashMap<CheckoutCodeBean,BigDecimal> code_cost_hash = new HashMap<CheckoutCodeBean,BigDecimal>();
		
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
							if (last_value.equals("purchaseOrderNumber")) {
								purchaseOrderNumber = Integer.parseInt(value_strX);
							} else if (last_value.equals("purchaseOrderDate")) {
								purchaseOrderDate = CUBean.getDateFromUserString(value_strX);
							} else if (last_value.equals("id")) {
								id = Integer.parseInt(value_strX);
							} else if (last_value.equals("vendorSelect")) {
								vendorSelect = Integer.parseInt(value_strX);
							} else if (last_value.equals("purchaseOrderNote")) {
								purchaseOrderNote = value_strX;
							} else if (last_value.equals("itemSearchResults")) {
								itemSearchResults = Integer.parseInt(value_strX);
							} else if (last_value.equals("itemSearchQty")) {
								itemSearchQty = Integer.parseInt(value_strX);
							} else if (last_value.equals("itemSearchCost")) {
								itemSearchCost = value_strX;
							} else if (last_value.startsWith("cost")) {
								CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(last_value.substring(4)));
								code_cost_hash.put(checkout_code_obj, new BigDecimal(value_strX));
							} else if (last_value.startsWith("qty")) {
								CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(last_value.substring(3)));
								if (!value_strX.isEmpty()) {
									int tmp_qty = Integer.parseInt(value_strX);
									if (tmp_qty > 0) {
										code_qty_hash.put(checkout_code_obj, tmp_qty);
									}
								}
							}
						}
						
					}
				}
			}
		}
		
		Vector keys_to_remove = new Vector();
		Iterator itr = code_cost_hash.keySet().iterator();
		while (itr.hasNext()) {
			CheckoutCodeBean checkout_code_obj = (CheckoutCodeBean)itr.next();
			if (!code_qty_hash.containsKey(checkout_code_obj)) {
				System.out.println("key to remove >" + checkout_code_obj.getLabel());
				keys_to_remove.addElement(checkout_code_obj);
			}
		}
		itr = keys_to_remove.iterator();
		while (itr.hasNext()) {
			CheckoutCodeBean code_to_remove = (CheckoutCodeBean)itr.next();
			code_cost_hash.remove(code_to_remove);
		}
		
		if (vendorSelect == 0) {
			throw new IllegalValueException("Unable to save purchase order.  You must select a vendor.");
		}
		if (purchaseOrderDate == null) {
			throw new IllegalValueException("Unable to save purchase order.  You must specify a date.");
		}
		if (purchaseOrderNumber == 0) {
			throw new IllegalValueException("Unable to save purchase order.  You must specify a purchase order number.");
		}
		
		PurchaseOrder obj = null;
		if (id > 0) {
			obj = PurchaseOrder.getPurchaseOrder(id);
			focused_po_hash.put(_key, obj);
		} else {
			//obj = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
			obj = focused_po_hash.get(_key);
			if (obj == null) {
				obj = new PurchaseOrder();
				obj.setCompany(_company);
				obj.setCreateOrModifyPerson(_logged_in_person);
				focused_po_hash.put(_key, obj);
			}
		}
		
		//appointment_obj = AppointmentAction.saveAppointment(_company, _logged_in_peson, appointmentId, inputType, statusSelect, inputPractitioner, clientSelect, appointment_date, inputDuration, inputComment, (short)0, (short)3, (short)0, (short)2, (short)0, "", false);
		//CheckoutCodeBean code_obj = CheckoutCodeAction.saveCheckoutCode(_company, _logged_in_person, id, itemNumberInput, upc, "PoS", itemName, itemDesc, isActive, amount, orderCost, amountPerMLInput, averageUnitCost, typeSelect, vendorSelect, departmentSelect, practiceAreaSelect, isPlanUse, com, taxCodeSelect, qtyOnHand, reorderPoint, cogsAccountSelect, incomeAccountSelect, assetAccountSelect, expenseAccountSelect);
		
		obj.setPurchaseOrderNumber(purchaseOrderNumber);
		obj.setPurchaseOrderDate(purchaseOrderDate);
		obj.setVendor(VendorRet.getVendor(vendorSelect));
		obj.setNote(purchaseOrderNote);
		obj.setCreateOrModifyPerson(_logged_in_person);
		//obj.save(); not yet, I guess
		
		
		
		/*
		StringBuffer b = new StringBuffer();
		if (id > 0) {
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Inventory Item Saved\",\"text\":\"" + obj.getLabel() + " saved.\"}]}");
		} else {
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Inventory Item Created\",\"text\":\"" + obj.getLabel() + " created.\"}]}");
		}
		
		return b.toString();
		*/
		

		//PurchaseOrder obj = null;
		/*
		if (arg2 != null && !arg2.isEmpty() && !arg2.equals("0")) {
			obj = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
		} else {
			//obj = PurchaseOrder.getPurchaseOrder(Integer.parseInt(arg2));
			obj = focused_po_hash.get(arg1); // should exist at this point, I'm thinking
		}
		*/

		CheckoutCodeBean code_to_add = code_to_add = CheckoutCodeBean.getCheckoutCode(itemSearchResults);
		
		/*
		if (code_cost_hash.containsKey(code_to_add)) {
			throw new IllegalValueException("Unable to add " + code_to_add + ".  Item already added.");
		}
		*/

		PurchaseOrderItemMapping item_mapping = new PurchaseOrderItemMapping();

		BigDecimal qty_bd = new BigDecimal(itemSearchQty);
		BigDecimal rate_bd = new BigDecimal(itemSearchCost);
		BigDecimal amount = qty_bd.multiply(rate_bd);

		item_mapping.setAmount(amount);
		item_mapping.setCheckoutCodeId(code_to_add.getId());
		item_mapping.setQuantity(qty_bd);
		item_mapping.setRate(rate_bd);
		item_mapping.setRowUpdated(new Date());

		Vector purchase_order_items = obj.getItems();

		if (obj.hasVendor()) {
			// ensure that the item being added is from the same vendor
			if (!obj.getVendor().equals(code_to_add.getVendor())) {
				// are there any other items
				if (purchase_order_items.size() > 0) {
					throw new IllegalValueException("Unable to add item.  This is a purchase order for " + obj.getVendorStr() + ".");
				} else {
					if (!code_to_add.hasVendor()) {
						throw new IllegalValueException("Unable to add item.  Please select a vendor.");
					}
					obj.setVendor(code_to_add.getVendor());
				}
			}
		} else {
			// no vendor assigned to PO.  grab from item being added
			if (!code_to_add.hasVendor()) {
				throw new IllegalValueException("Unable to add item.  Please select a vendor.");
			}
			obj.setVendor(code_to_add.getVendor());
		}

		purchase_order_items.addElement(item_mapping);
		obj.setItems(purchase_order_items);
		
		
		
		// save the mappings too
		
		Iterator mappings_itr = obj.getItems().iterator();
		while (mappings_itr.hasNext()) {
			PurchaseOrderItemMapping mapping = (PurchaseOrderItemMapping)mappings_itr.next();
			CheckoutCodeBean mapped_code = CheckoutCodeBean.getCheckoutCode(mapping.getCheckoutCodeId());
			if (!code_to_add.equals(mapped_code)) {
				if (code_cost_hash.containsKey(mapped_code)) {
					BigDecimal cost_bd = code_cost_hash.get(mapped_code);
					mapping.setRate(cost_bd);
					mapping.setAmount(cost_bd.multiply(mapping.getQuantity()));
				}
				if (code_qty_hash.containsKey(mapped_code)) {
					BigDecimal qty = new BigDecimal(code_qty_hash.get(mapped_code));
					mapping.setQuantity(qty);
					mapping.setAmount(qty.multiply(mapping.getRate()));
				}
			}
		}

		//writer.println(this.toStatsJSON(obj, true, selected_bu, true));
		
		obj.save();
		
		return this.toStatsJSON(obj, true, _company, true);
                
	}
	
	private String
	receivePurchaseOrder(String _key, UKOnlineCompanyBean _company, UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
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
		
		PurchaseOrder purchaseOrder = null;
		BigDecimal receiveTax = BigDecimal.ZERO;
		BigDecimal receiveShipping = BigDecimal.ZERO;
		BigDecimal receiveDiscount = BigDecimal.ZERO;
		
		HashMap<CheckoutCodeBean,BigDecimal> code_qty_hash = new HashMap<CheckoutCodeBean,BigDecimal>();
		HashMap<CheckoutCodeBean,BigDecimal> code_cost_hash = new HashMap<CheckoutCodeBean,BigDecimal>();
		
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
							if (last_value.equals("receiveTax")) {
								receiveTax = new BigDecimal(value_strX);
							} else if (last_value.equals("receiveShipping")) {
								receiveShipping = new BigDecimal(value_strX);
							} else if (last_value.equals("receiveDiscount")) {
								receiveDiscount = new BigDecimal(value_strX);
							} else if (last_value.startsWith("receivedCost")) {
								CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(last_value.substring(12)));
								code_cost_hash.put(checkout_code_obj, new BigDecimal(value_strX));
							} else if (last_value.startsWith("receivedQty")) {
								CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(last_value.substring(11)));
								code_qty_hash.put(checkout_code_obj, new BigDecimal(value_strX));
							} else if (last_value.equals("id")) {
								purchaseOrder = PurchaseOrder.getPurchaseOrder(Integer.parseInt(value_strX));
							}
						}
						
					}
				}
			}
		}
		
		HashMap<CheckoutCodeBean, BigDecimal> receipt_hash = new HashMap<CheckoutCodeBean, BigDecimal>();
		Iterator existing_receipt_itr = ItemReceiptRet.getItemReceipts(purchaseOrder).iterator();
		while (existing_receipt_itr.hasNext()) {
			ItemReceiptRet existing_ret = (ItemReceiptRet)existing_receipt_itr.next();
			Iterator item_line_itr = existing_ret.getItemLines().iterator();
			while (item_line_itr.hasNext()) {
				ItemLineRet item_line_obj = (ItemLineRet)item_line_itr.next();
				if (item_line_obj.getQuantity() != null) {
					if (item_line_obj.getQuantity().compareTo(BigDecimal.ZERO) == 1) {
						CheckoutCodeBean code_obj = item_line_obj.getCheckoutCode();
						BigDecimal total_qty_received = item_line_obj.getQuantity();
						if (receipt_hash.containsKey(code_obj)) {
							total_qty_received = total_qty_received.add(receipt_hash.get(code_obj));
						}
						receipt_hash.put(code_obj, total_qty_received);
					}
				}
			}
		}
				
		ItemReceiptRet item_receipt = new ItemReceiptRet();

		// create ItemReceiptRet and ItemLineRet objects for the items received

		BigDecimal receive_total = CUBean.zero;

		item_receipt.setPurchaseOrder(purchaseOrder);
		item_receipt.setTaxAmount(receiveTax.floatValue());
		receive_total = receive_total.add(receiveTax);
		item_receipt.setShippingAmount(receiveShipping.floatValue());
		receive_total = receive_total.add(receiveShipping);
		item_receipt.setDiscountAmount(receiveDiscount.floatValue());
		receive_total = receive_total.subtract(receiveDiscount);


		Vector items = new Vector();
		String error_message = null;
		
		
		Iterator itr = code_qty_hash.keySet().iterator();
		while (itr.hasNext()) {
			CheckoutCodeBean item_being_received = (CheckoutCodeBean)itr.next();
			BigDecimal qty_bd = code_qty_hash.get(item_being_received);
			BigDecimal cost_bd = code_cost_hash.get(item_being_received);
			
			if (qty_bd.compareTo(BigDecimal.ZERO) == 1) {
			
				PurchaseOrderItemMapping mapping = purchaseOrder.getMapping(item_being_received);

				ItemLineRet item_line = item_receipt.getItemLine(mapping.getPurchaseOrderItemMappingId());
				item_line.setQuantity(qty_bd.floatValue());
				item_line.setCost(cost_bd.floatValue());
				BigDecimal line_total = new BigDecimal(qty_bd.floatValue() * cost_bd.floatValue()); // shrug
				item_line.setAmount(line_total.floatValue());

				//CheckoutCodeBean item_being_received = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(_request.getParameter("code" + i)));
				item_line.setCheckoutCodeId(item_being_received.getId());
				items.addElement(item_line);
				receive_total = receive_total.add(line_total);
				
				if (receipt_hash.containsKey(item_being_received)) {
					qty_bd = qty_bd.add(receipt_hash.get(item_being_received));
				}

				mapping.setQuantityReceived(qty_bd); // this needs to account for possible previous receipts
				mapping.setRate(cost_bd);
				mapping.save();
			}
		}
		
		/*
		for (int i = 0; _request.getParameter("rate" + i) != null; i++)
		{
			boolean rate_valid = true;
			boolean qty_valid = true;

			BigDecimal rate = CUBean.zero;
			try
			{
				rate = new BigDecimal(_request.getParameter("rate" + i));
			}
			catch (NumberFormatException x)
			{
				rate_valid = false;
				if (error_message == null)
					error_message = "Invalid rate >" + _request.getParameter("rate" + i);
			}

			BigDecimal qty = CUBean.zero;
			try
			{
				qty = new BigDecimal(_request.getParameter("received" + i));
			}
			catch (NumberFormatException x)
			{
				qty_valid = false;
				if (error_message == null)
					error_message = "Invalid received quantity >" + _request.getParameter("received" + i);
			}

			int mapping_id = Integer.parseInt(_request.getParameter("mapping" + i));

			ItemLineRet item_line = item_receipt.getItemLine(mapping_id);
			if (rate_valid && qty_valid)
			{
				item_line.setQuantity(qty.floatValue());
				item_line.setCost(rate.floatValue());
				BigDecimal line_total = new BigDecimal(qty.floatValue() * rate.floatValue());
				item_line.setAmount(line_total.floatValue());

				CheckoutCodeBean item_being_received = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(_request.getParameter("code" + i)));
				item_line.setCheckoutCodeId(item_being_received.getId());
				items.addElement(item_line);
				receive_total = receive_total.add(line_total);
			}
		}
		*/
		item_receipt.setItems(items);
		
		if (items.isEmpty()) {
			throw new IllegalValueException("No items selected to receive.");
		}

		if (error_message != null) {
			throw new IllegalValueException(error_message);
		}

		item_receipt.setTotalAmount(receive_total.floatValue());
		item_receipt.save();

		BigDecimal remaining_quantity_to_be_received = purchaseOrder.getRemainingQuantityToBeReceived();
		if (remaining_quantity_to_be_received.compareTo(CUBean.zero) == 0) {
			purchaseOrder.setStatus(PurchaseOrder.COMPLETE_STATUS);
			purchaseOrder.save();
		}

		Iterator item_line_itr = items.iterator();
		while (item_line_itr.hasNext()) {
			ItemLineRet line = (ItemLineRet)item_line_itr.next();
			CheckoutCodeBean checkout_code = line.getCheckoutCode();
			short existing_qty = checkout_code.getOnHandQuantity();
			short qty_received = line.getQuantity().shortValue();

			checkout_code.setOnHandQuantity((short)(existing_qty + qty_received));
			checkout_code.save();
		}
		
		return "{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Items Received\",\"id\":" + purchaseOrder.getId() + ",\"text\":\"Items received for " + purchaseOrder.getLabel() + ".\"}]}";        
	}
	
	private String
	saveInventoryDetail(String _key, UKOnlineCompanyBean _company, UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
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
		
		int id = 0;
		String itemNumber = null;
		String upc = null;
		String amount = null;
		String orderCost = null;
		String itemName = null;
		boolean isActive = false;
		boolean isPlanUse = false;
		short qtyOnHand = (short)0;
		short reorderPoint = (short)0;
		String itemDesc = null;
		boolean com = false;
		int vendorSelect = 0;
		int departmentSelect = 0;
		int taxCodeSelect = 0;
		int practiceAreaSelect = 0;
		short typeSelect = 0;
		int expenseAccountSelect = 0;
		int cogsAccountSelect = 0;
		int incomeAccountSelect = 0;
		int assetAccountSelect = 0;
		
		String amountPerMLInput = null;
		String averageUnitCost = null;
		
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
							if (last_value.equals("id")) {
								id = Integer.parseInt(value_strX);
							} else if (last_value.equals("itemNumber")) {
								itemNumber = value_strX;
							} else if (last_value.equals("upc")) {
								upc = value_strX;
							} else if (last_value.equals("amount")) {
								amount = value_strX;
							} else if (last_value.equals("orderCost")) {
								orderCost = value_strX;
							} else if (last_value.startsWith("itemName")) {
								itemName = value_strX;
							} else if (last_value.startsWith("isActive")) {
								isActive = true;
							} else if (last_value.startsWith("isPlanUse")) {
								isPlanUse = true;
							} else if (last_value.startsWith("qtyOnHand")) {
								qtyOnHand = Short.parseShort(value_strX);
							} else if (last_value.startsWith("reorderPoint")) {
								reorderPoint = Short.parseShort(value_strX);
							} else if (last_value.startsWith("itemDesc")) {
								itemDesc = value_strX;
							} else if (last_value.startsWith("vendorSelect")) {
								vendorSelect = Integer.parseInt(value_strX);
							} else if (last_value.startsWith("departmentSelect")) {
								departmentSelect = Integer.parseInt(value_strX);
							} else if (last_value.startsWith("taxCodeSelect")) {
								taxCodeSelect = Integer.parseInt(value_strX);
							} else if (last_value.startsWith("practiceAreaSelect")) {
								practiceAreaSelect = Integer.parseInt(value_strX);
							} else if (last_value.startsWith("typeSelect")) {
								typeSelect = Short.parseShort(value_strX);
							} else if (last_value.startsWith("expenseAccountSelect")) {
								expenseAccountSelect = Integer.parseInt(value_strX);
							} else if (last_value.startsWith("cogsAccountSelect")) {
								cogsAccountSelect = Integer.parseInt(value_strX);
							} else if (last_value.startsWith("incomeAccountSelect")) {
								incomeAccountSelect = Integer.parseInt(value_strX);
							} else if (last_value.startsWith("assetAccountSelect")) {
								assetAccountSelect = Integer.parseInt(value_strX);
							}
						}
						
					}
				}
			}
		}
		
		if (itemName.isEmpty()) { throw new IllegalValueException("Unable to save.  You must provide an item name."); }
		if (vendorSelect < 1) { throw new IllegalValueException("Unable to save.  You must select a vendor."); }
		if (departmentSelect < 1) { throw new IllegalValueException("Unable to save.  You must select a department."); }
		if (taxCodeSelect < 1) { throw new IllegalValueException("Unable to save.  You must select a tax code."); }
		if (typeSelect < 1) { throw new IllegalValueException("Unable to save.  You must select a type."); }
		
		if (id == 0) {
			// new item, existing name can't exist
			try {
				CheckoutCodeBean.getCheckoutCodeByDesc(_company, itemName);
				throw new ObjectAlreadyExistsException(itemName + " already exists.");
			} catch (ObjectNotFoundException x) {
				// this is OK
			}
		}
		
		CheckoutCodeBean code_obj = CheckoutCodeAction.saveCheckoutCode(_company, _logged_in_person, id, itemNumber, upc, "PoS", itemName, itemDesc, isActive, amount, orderCost, amountPerMLInput, averageUnitCost, typeSelect, vendorSelect, departmentSelect, practiceAreaSelect, isPlanUse, com, taxCodeSelect, qtyOnHand, reorderPoint, cogsAccountSelect, incomeAccountSelect, assetAccountSelect, expenseAccountSelect);
		
		StringBuffer b = new StringBuffer();
		if (id > 0) {
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Inventory Item Saved\",\"text\":\"" + code_obj.getLabel() + " saved.\"}]}");
		} else {
			b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Inventory Item Created\",\"id\":" + code_obj.getId() + ",\"text\":\"" + code_obj.getLabel() + " created.\"}]}");
		}
		
		return b.toString();        
	}
	
	private String
	saveWaitListChanges(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
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
		
		//Vect
		
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
							if (last_value.startsWith("pwlQty")) {
								ProductWaitList wait_list_obj = ProductWaitList.getProductWaitListEntry(Integer.parseInt(last_value.substring(6)));
								BigDecimal updated_qty = new BigDecimal(value_strX);
								if (updated_qty.compareTo(wait_list_obj.getQuantity()) != 0) {
									wait_list_obj.setQuantity(updated_qty);
									wait_list_obj.setCreateOrModifyPerson(_logged_in_person);
									wait_list_obj.save();
								}
							}
						}
						
					}
				}
			}
		}
		
		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Changes Saved\",\"text\":\"Wait List changes saved.\"}]}");
		return b.toString();        
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
		return "Short description";
	}// </editor-fold>

}
