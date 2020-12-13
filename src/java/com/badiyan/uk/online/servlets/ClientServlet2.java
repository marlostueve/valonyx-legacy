/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.servlets;

import com.badiyan.torque.CheckoutOrderline;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.PDF.SalesReceiptBuilder;
import com.badiyan.uk.online.beans.CheckoutCodeBean;
import com.badiyan.uk.online.beans.PrintedReceipt;
import com.badiyan.uk.online.beans.UKOnlineCompanyBean;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import com.badiyan.uk.online.beans.ValeoOrderBean;
import com.valeo.qb.data.ReceivePaymentRet;
import com.valeo.qbpos.data.TenderRet;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.torque.TorqueException;
import org.json.simple.JSONObject;

/**
 *
 * @author marlo
 */
public class ClientServlet2 extends HttpServlet {

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
			
			
			if (command.equals("showLedger")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				
				//Date ledger_start_date = (Date)session.getAttribute("ledger_start_date");
				writer.println(ClientServlet2.ledgerToJSON(client, null, null));
				
			} else if (command.equals("showLedgerThisMonth")) {
				// find the start date for this month
				
				//session.setAttribute("ledger_view", "month");
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				
				Calendar c = Calendar.getInstance();
				c.set(Calendar.DAY_OF_MONTH, 1);
				Date ledger_start_date = c.getTime();
				//session.setAttribute("ledger_start_date", ledger_start_date);
				
				//ClientServlet2.ledgerToJSON(writer, parameter, ledger_start_date);
				writer.println(ClientServlet2.ledgerToJSON(client, ledger_start_date, null));
				
			} else if (command.equals("showLedgerThisYear")) {
				//session.setAttribute("ledger_view", "year");
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				
				Calendar c = Calendar.getInstance();
				c.set(Calendar.DAY_OF_YEAR, 1);
				Date ledger_start_date = c.getTime();
				//session.setAttribute("ledger_start_date", ledger_start_date);
				
				writer.println(ClientServlet2.ledgerToJSON(client, ledger_start_date, null));
				
			} else if (command.equals("showLedgerAll")) {
				//session.setAttribute("ledger_view", "all");
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				
				Calendar c = Calendar.getInstance();
				c.add(Calendar.YEAR, -25); // go back 25 years for entire ledger, I guess.  I'm prolly gonna want to add some paging to the ledger view or something
				Date ledger_start_date = c.getTime();
				//session.setAttribute("ledger_start_date", ledger_start_date);
				//ClientServlet2.ledgerToJSON(session, writer, parameter, ledger_start_date);
				
				writer.println(ClientServlet2.ledgerToJSON(client, ledger_start_date, null));
				
			} else if (command.equals("showLedgerWithMore")) {
				//session.setAttribute("ledger_view", "year");
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				
				Date lastOrderDate = null;
				if (arg4 != null && !arg4.isEmpty()) {
					lastOrderDate = CUBean.getDateFromUserString(arg4);
				}
				
				writer.println(ClientServlet2.ledgerToJSON(client, Integer.parseInt(arg3), 10, lastOrderDate));
				
			} else if (command.equals("viewReceipt")) {
				
				UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(arg1);
				UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				
				PrintedReceipt printed_receipt = PrintedReceipt.getPrintedReceipt((ValeoOrderBean)ValeoOrderBean.getOrder(Integer.parseInt(arg2)));
				
				String receipt_url = printed_receipt.getPrintedReceiptURL();
				if (receipt_url == null) {
					receipt_url = this.getSalesReceiptURL(printed_receipt, logged_in_person, selected_bu);
					printed_receipt.setPrintedReceiptURL(receipt_url);
				}
				
				//writer.println("<sales_receipt_url><![CDATA[" + sales_receipt_url + "]]></sales_receipt_url>");
				
				StringBuffer b = new StringBuffer();
				b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Checkout Complete\",\"text\":\"Checkout complete.\",\"pdfURL\":\"" + JSONObject.escape(receipt_url) + "\"}]}");
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
	
	private static String
	ledgerToJSON(UKOnlinePersonBean client, Date _start_date, Date _end_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		try {
			
			//Date end_date = new Date();
			if (_end_date == null) {
				_end_date = new Date();
			}
			
			// get the receive payments

			Vector receive_payments;
			if (_start_date == null) {
				receive_payments = ReceivePaymentRet.getReceivePayments(client);
			} else {
				receive_payments = ReceivePaymentRet.getReceivePayments(client, _start_date, _end_date);
			}
			
			System.out.println("receive_payments found >" + receive_payments.size());

			// get the ledger transactions for the ledger tab


			StringBuffer orders_buf_x = new StringBuffer();
			boolean orders_buf_x_needs_comma = false;

			// get all the printed receipts


			HashMap<ValeoOrderBean,PrintedReceipt> order_to_receipt_hash = new HashMap<ValeoOrderBean,PrintedReceipt>();
			Vector<PrintedReceipt> unmapped_printed_receipt = new Vector<PrintedReceipt>();

			Iterator itr;
			if (_start_date == null) {
				itr = PrintedReceipt.getPrintedReceipts(client).iterator();
			} else {
				itr = PrintedReceipt.getPrintedReceipts(client, _start_date, _end_date).iterator();
			}
			while (itr.hasNext()) {
				PrintedReceipt receipt = (PrintedReceipt)itr.next();
				if (receipt.hasPrimaryOrder()) {
					order_to_receipt_hash.put(receipt.getPrimaryOrder(), receipt);
				} else {
					unmapped_printed_receipt.addElement(receipt);
				}
			}


			HashMap<ValeoOrderBean,Vector> order_to_tenders_hash = new HashMap<ValeoOrderBean,Vector>();
			HashMap<ValeoOrderBean,Vector> order_to_amount_hash = new HashMap<ValeoOrderBean,Vector>();
			Vector tenders_with_unmapped_portions = new Vector();
			HashMap<TenderRet,BigDecimal> unmapped_amount_hash = new HashMap<TenderRet,BigDecimal>();

			Vector tenders;
			if (_start_date == null) {
				tenders = TenderRet.getTenders(client);
			} else {
				tenders = TenderRet.getTenders(client, _start_date, _end_date);
			}
			Iterator tender_itr = tenders.iterator();
			while (tender_itr.hasNext())
			{
				TenderRet tender = (TenderRet)tender_itr.next();
				BigDecimal tender_amount_not_mapped = tender.getAmountBigDecimal();
				Iterator order_itr = tender.getOrders().iterator();
				while (order_itr.hasNext())
				{
					ValeoOrderBean order = (ValeoOrderBean)order_itr.next();
					BigDecimal amount_mapped_to_order = tender.getAmountMappedToOrder(order);
					System.out.println(tender.getValue() + " mapped to " + order.getValue() + " >" + amount_mapped_to_order.toString());
					tender_amount_not_mapped = tender_amount_not_mapped.subtract(amount_mapped_to_order);

					Vector vec_1 = order_to_tenders_hash.get(order);
					if (vec_1 == null)
					{
						vec_1 = new Vector();
						order_to_tenders_hash.put(order, vec_1);
					}
					vec_1.addElement(tender);

					Vector vec_2 = order_to_amount_hash.get(order);
					if (vec_2 == null)
					{
						vec_2 = new Vector();
						order_to_amount_hash.put(order, vec_2);
					}
					vec_2.addElement(amount_mapped_to_order);
				}

				if (tender_amount_not_mapped.compareTo(BigDecimal.ZERO) == 1)
				{
					tenders_with_unmapped_portions.addElement(tender);
					unmapped_amount_hash.put(tender, tender_amount_not_mapped);
				}
			}

			Iterator orders_itr;
			if (_start_date == null) {
				orders_itr = ValeoOrderBean.getOrders(client).iterator();
			} else {
				orders_itr = ValeoOrderBean.getOrders(client, _start_date, _end_date).iterator();
			}
			
			for (int i = 0; orders_itr.hasNext(); i++) {
				ValeoOrderBean order_obj = (ValeoOrderBean)orders_itr.next();
				Date order_date = order_obj.getOrderDate();
				
				System.out.println(CUBean.getUserDateString(order_date) + " - order_obj found >" + order_obj.getValue());

				// are there any tenders that should be displayed ahead of this order?
				// 7/14/11 - modifying to show all tenders on their own.  I'm thinking I need to provide tool to do things like reverse/refund payments

				//if (!tenders_with_unmapped_portions.isEmpty())
				if (!tenders.isEmpty())
				{
					// first one should be most current...

					//TenderRet tender = (TenderRet)tenders_with_unmapped_portions.get(0);
					TenderRet tender = (TenderRet)tenders.get(0);

					Date tender_date = tender.getTenderDate();
					//while (tender_date.after(order_date) && !tenders_with_unmapped_portions.isEmpty())
					while (tender_date.after(order_date) && !tenders.isEmpty())
					{
						String orderline_value = null;
						StringBuilder order_line_buf_x = new StringBuilder();

						String tender_type_str = "Received " + tender.getType() + " Tender";
						if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE)) {
							tender_type_str = "Placed On Account";
						}

						/*
						orderline_value = "{\"code\" : \"\"," +
										" \"amount\" : \"" + tender.getAmountBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
										" \"qty\" : \"\"," +
										" \"prac\" : \"\"," +
										" \"comm\" : \"\"," +
										" \"ist\" : \"true\"," +
										" \"desc\" : \"" + tender_type_str + "\"}";
						order_line_buf.append("<orderline><key>t" + tender.getValue() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");
						*/
						
						order_line_buf_x.append("{\"amount\":\"" + tender.getAmountBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
						order_line_buf_x.append("\"desc\":\"" + JSONObject.escape(tender_type_str) + "\",");
						order_line_buf_x.append("\"ist\":true,");
						order_line_buf_x.append("\"id\":\"t" + tender.getValue() + "\"}");


						/*
						String value = "{\"label\" : \"" + tender.getLabel() + "\"," +
												" \"value\" : \"" + tender.getValue() + "\"," +
												" \"date\" : \"" + CUBean.getUserDateString(tender_date) + "\"," +
												" \"paid\" : \"\"," +
												" \"billed\" : \"\"," +
												" \"collect\" : \"\"," +
												" \"status\" : \"\"," +
												" \"balance\" : \"\"}";
						orders_buf.append("<order><key>" + order_obj.getId() + "</key><value><![CDATA[" + value + "]]></value><orderlines>" + order_line_buf.toString() + "</orderlines></order>");
						*/
						
						if (orders_buf_x_needs_comma) { orders_buf_x.append(','); }
						orders_buf_x.append("{\"label\":\"" + JSONObject.escape(tender.getLabel()) + "\",");
						orders_buf_x.append("\"date\":\"" + JSONObject.escape(CUBean.getUserDateString(tender_date)) + "\",");
						orders_buf_x.append("\"orderline\":[");
						orders_buf_x.append(order_line_buf_x.toString());
						orders_buf_x.append("],");
						//orders_buf_x.append("\"id\":" + order_obj.getId() + "}");
						orders_buf_x.append("\"type\":\"" + tender.getType() + " Tender\",");
						if (tender.isCreditCardPaymentType()) {
							orders_buf_x.append("\"cardType\":\"" + tender.getCreditCardTypeString()+ " Tender\",");
						}
						boolean tender_is_synced = true;
						Iterator tender_order_itr = tender.getOrders().iterator();
						while (tender_order_itr.hasNext()) {
							ValeoOrderBean associated_order = (ValeoOrderBean)tender_order_itr.next();
							if (!associated_order.isUpdated()) {
								tender_is_synced = false;
							}
						}
						orders_buf_x.append("\"status\":\"" + (tender_is_synced ? "SENT TO QB" : "") + "\",");
						orders_buf_x.append("\"id\":" + tender.getId() + "}");
						orders_buf_x_needs_comma = true;
						
						

						//tenders_with_unmapped_portions.removeElementAt(0);
						tenders.removeElementAt(0);

						//if (!tenders_with_unmapped_portions.isEmpty())
						if (!tenders.isEmpty()) {
							//tender = (TenderRet)tenders_with_unmapped_portions.get(0);
							tender = (TenderRet)tenders.get(0);
							tender_date = tender.getTenderDate();
						}
					}
				}

				// are there any receive payments that should be displayed ahead of this order?

				if (!receive_payments.isEmpty()) {
					
					// first one should be most current...

					ReceivePaymentRet receivePayment = (ReceivePaymentRet)receive_payments.get(0);

					Date txn_date = receivePayment.getTxnDate();
					while (txn_date.after(order_date) && !receive_payments.isEmpty()) {
						String orderline_value = null;
						StringBuilder order_line_buf_x = new StringBuilder();

						String tender_type_str = "Received Payment";

						BigDecimal payment_amount = new BigDecimal(receivePayment.getTotalAmount());
						//BigDecimal amount = tender.getAmountBigDecimal();

						/*
						orderline_value = "{\"code\" : \"\"," +
										" \"amount\" : \"" + payment_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
										" \"qty\" : \"\"," +
										" \"prac\" : \"\"," +
										" \"comm\" : \"\"," +
										" \"desc\" : \"" + tender_type_str + "\"}";
						order_line_buf.append("<orderline><key>t" + receivePayment.getValue() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");
						*/
						
						
						order_line_buf_x.append("{\"amount\":\"" + payment_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
						order_line_buf_x.append("\"desc\":\"" + JSONObject.escape(tender_type_str) + "\",");
						order_line_buf_x.append("\"id\":\"t" + receivePayment.getValue() + "\"}");
						
						
						

						/*
						String value = "{\"label\" : \"" + receivePayment.getLabel() + "\"," +
												" \"value\" : \"" + receivePayment.getValue() + "\"," +
												" \"date\" : \"" + CUBean.getUserDateString(txn_date) + "\"," +
												" \"paid\" : \"\"," +
												" \"billed\" : \"\"," +
												" \"collect\" : \"\"," +
												" \"status\" : \"\"," +
												" \"balance\" : \"\"}";

						orders_buf.append("<order><key>" + order_obj.getId() + "</key><value><![CDATA[" + value + "]]></value><orderlines>" + order_line_buf.toString() + "</orderlines></order>");
						*/
						
						
						
						if (orders_buf_x_needs_comma) { orders_buf_x.append(','); }
						orders_buf_x.append("{\"label\":\"" + JSONObject.escape(receivePayment.getLabel()) + "\",");
						orders_buf_x.append("\"date\":\"" + JSONObject.escape(CUBean.getUserDateString(txn_date)) + "\",");
						orders_buf_x.append("\"orderline\":[");
						orders_buf_x.append(order_line_buf_x.toString());
						orders_buf_x.append("],");
						orders_buf_x.append("\"type\":\"Received Payment (Recorded in QuickBooks)\",");
						orders_buf_x.append("\"id\":" + receivePayment.getValue() + "}");
						orders_buf_x_needs_comma = true;

						receive_payments.removeElementAt(0);

						if (!receive_payments.isEmpty()) {
							receivePayment = (ReceivePaymentRet)receive_payments.get(0);
							txn_date = receivePayment.getTxnDate();
						}
					}
				}


				try {

					String orderline_value = null;
					StringBuilder order_line_buf_x = new StringBuilder();
					boolean order_line_buf_x_needs_comma = false;
					Iterator order_lines_itr = order_obj.getOrders();

					while (order_lines_itr.hasNext()) {
						CheckoutOrderline order_line = (CheckoutOrderline)order_lines_itr.next();
						CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(order_line.getCheckoutCodeId());
						BigDecimal qty = order_line.getQuantity();
						BigDecimal price = order_line.getPrice();
						BigDecimal actual_amount = order_line.getActualAmount();
						
						String desc = code.getDescription().replace('\r', ' ').replace('\n', ' ');
						
						String price_str = price.setScale(2, RoundingMode.HALF_UP).toString();
						//String qty_str = qty.setScale(0, RoundingMode.HALF_UP).toString();
						String qty_str = ClientServlet2.getStringFromBigDecimal(qty);
						
						//kjhkjh;
						String actual_amount_str = actual_amount.setScale(2, RoundingMode.HALF_UP).toString();

						/*
						orderline_value = "{\"code\" : \"" + code.getCode() + "\"," +
										" \"amount\" : \"" + price_str + "\"," +
										" \"qty\" : \"" + qty_str + "\"," +
										" \"prac\" : \"" + prac_str + "\"," +
										" \"isc\" : \"" + code.isCommissionable() + "\"," +
										" \"id\" : \"" + order_line.getCheckoutOrderlineId() + "\"," +
										" \"comm\" : \"" + commission_str + "\"," +
										" \"desc\" : \"" + desc + "\"}";
						order_line_buf.append("<orderline><key>" + order_line.getCheckoutOrderlineId() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");
						*/
						
						
						if (order_line_buf_x_needs_comma) { order_line_buf_x.append(','); }
						order_line_buf_x.append("{\"amount\":\"" + price_str + "\",");
						order_line_buf_x.append("\"total\":\"" + actual_amount_str + "\",");
						order_line_buf_x.append("\"qty\":" + qty_str + ",");
						order_line_buf_x.append("\"code\":\"" + JSONObject.escape(code.getCode()) + "\",");
						if (order_line.getPractitionerId() > 0) {
							order_line_buf_x.append("\"practId\":" + order_line.getPractitionerId() + ",");
							order_line_buf_x.append("\"isc\":" + code.isCommissionable() + ",");
							order_line_buf_x.append("\"comm\":\"" + order_line.getCommission().toString() + "\",");
						}
						order_line_buf_x.append("\"desc\":\"" + JSONObject.escape(desc) + "\",");
						order_line_buf_x.append("\"id\":" + order_line.getCheckoutOrderlineId() + "}");
						order_line_buf_x_needs_comma = true;
						
					}

					BigDecimal order_tax = order_obj.getTax();
					if (order_tax.compareTo(BigDecimal.ZERO) != 0) {
						// there is some tax on this order.

						/*
						orderline_value = "{\"code\" : \"\"," +
										" \"amount\" : \"" + order_tax.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
										" \"qty\" : \"\"," +
										" \"prac\" : \"\"," +
										" \"comm\" : \"\"," +
										" \"desc\" : \"Tax\"}";
						order_line_buf.append("<orderline><key>-1</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");
						*/
						
						if (order_line_buf_x_needs_comma) { order_line_buf_x.append(','); }
						order_line_buf_x.append("{\"amount\":\"" + order_tax.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
						order_line_buf_x.append("\"desc\":\"Tax\",");
						order_line_buf_x.append("\"id\":-1}");
						order_line_buf_x_needs_comma = true;
						

					}

					/*
					orderline_value = "{\"code\" : \"\"," +
									" \"amount\" : \"" + order_obj.getTotalString() + "\"," +
									" \"qty\" : \"\"," +
									" \"prac\" : \"\"," +
									" \"comm\" : \"\"," +
									" \"desc\" : \"Order Total\"}";
					order_line_buf.append("<orderline><key>-1</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");
					*/

					if (order_line_buf_x_needs_comma) { order_line_buf_x.append(','); }
					order_line_buf_x.append("{\"amount\":\"" + order_obj.getTotalString() + "\",");
					order_line_buf_x.append("\"desc\":\"Order Total\",");
					order_line_buf_x.append("\"id\":-1}");
					order_line_buf_x_needs_comma = true;


					// get the Tenders for this order

					Vector tenders_vec = order_to_tenders_hash.get(order_obj);
					if (tenders_vec != null) {
						tender_itr = tenders_vec.iterator();
						//tender_itr = TenderRet.getTenders(order_obj).iterator();
						for (int ii = 0; tender_itr.hasNext(); ii++)
						{
							TenderRet tender = (TenderRet)tender_itr.next();
							String tender_type_str = tender.getType() + " Tender (" + tender.getValue() + ")";
							if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE)) {
								tender_type_str = "Placed On Account (" + tender.getValue() + ")";
							}

							Vector vec = order_to_amount_hash.get(order_obj);
							BigDecimal amount_allocated_to_order = (BigDecimal)vec.elementAt(ii);

							//BigDecimal amount = tender.getAmountBigDecimal();

							/*
							orderline_value = "{\"code\" : \"\"," +
											" \"amount\" : \"" + amount_allocated_to_order.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
											" \"qty\" : \"\"," +
											" \"prac\" : \"\"," +
											" \"comm\" : \"\"," +
											" \"desc\" : \"" + tender_type_str + "\"}";
							order_line_buf.append("<orderline><key>t" + tender.getValue() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");
							*/
							

							if (order_line_buf_x_needs_comma) { order_line_buf_x.append(','); }
							order_line_buf_x.append("{\"amount\":\"" + amount_allocated_to_order.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
							order_line_buf_x.append("\"desc\":\"" + JSONObject.escape(tender_type_str) + "\",");
							order_line_buf_x.append("\"id\":\"t" + tender.getValue() + "\"}");
							order_line_buf_x_needs_comma = true;

						}
					}

					String order_status_str = "";
					if (order_obj.isReversed()) {
						order_status_str = "REVERSED";
					} else if (order_obj.isReturn()) {
						order_status_str = "RETURN";
					} else if (order_obj.isLegacy()) {
						order_status_str = "OLD PAYMENT";
					} else if (order_obj.isUpdated()) {
						order_status_str = "SENT TO QB";
						// sent as?
					}

					String printed_receipt_value = "0";
					PrintedReceipt receipt_for_order = order_to_receipt_hash.get(order_obj);
					if (receipt_for_order != null) {
						printed_receipt_value = receipt_for_order.getValue();
					}

					/*
					String value = "{\"label\" : \"" + order_obj.getLabel() + "\"," +
											" \"value\" : \"" + order_obj.getValue() + "\"," +
											" \"date\" : \"" + order_obj.getOrderDateString() + "\"," +
											" \"paid\" : \"" + order_obj.isClosed() + "\"," +
											" \"billed\" : \"" + order_obj.isBilled() + "\"," +
											" \"collect\" : \"" + order_obj.hasBeenSentToCollections() + "\"," +
											" \"status\" : \"" + order_status_str + "\"," +
											" \"pr\" : \"" + printed_receipt_value + "\"," +
											" \"balance\" : \"" + order_obj.getBalanceString() + "\"}";

					orders_buf.append("<order><key>" + order_obj.getId() + "</key><value><![CDATA[" + value + "]]></value><orderlines>" + order_line_buf.toString() + "</orderlines></order>");
					*/

					if (orders_buf_x_needs_comma) { orders_buf_x.append(','); }
					orders_buf_x.append("{\"label\":\"" + JSONObject.escape(order_obj.getLabel()) + "\",");
					orders_buf_x.append("\"date\":\"" + JSONObject.escape(order_obj.getOrderDateString()) + "\",");
					orders_buf_x.append("\"paid\":" + order_obj.isClosed() + ",");
					orders_buf_x.append("\"billed\":" + order_obj.isBilled() + ",");
					orders_buf_x.append("\"collect\":" + order_obj.hasBeenSentToCollections() + ",");
					orders_buf_x.append("\"status\":\"" + JSONObject.escape(order_status_str) + "\",");
					orders_buf_x.append("\"pr\":\"" + printed_receipt_value + "\",");
					orders_buf_x.append("\"balance\":\"" + order_obj.getBalanceString() + "\",");
					orders_buf_x.append("\"orderline\":[");
					orders_buf_x.append(order_line_buf_x.toString());
					orders_buf_x.append("],");
					
					if (order_obj.hasAccountTender()) {
						// if any part of the order is being placed "On Account", this is communicated to QBs as an Invoice
						orders_buf_x.append("\"type\":\"Invoice\",");
					} else {
						orders_buf_x.append("\"type\":\"Sales Receipt\",");
					}
					
					orders_buf_x.append("\"id\":" + order_obj.getId() + "}");
					orders_buf_x_needs_comma = true;
					
					
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}

			/*
			status_buf.append("<ledger total=\"0.00\" client=\"0.00\" balance=\"" + client.getBalanceString() + "\">");
			status_buf.append(orders_buf.toString());
			status_buf.append("</ledger>");
			*/
			
			StringBuilder b = new StringBuilder();
			b.append("{\"total\":\"0.00\",");
			b.append("\"client\":\"0.00\",");
			b.append("\"balance\":" + client.getBalanceString() + ",");
			b.append("\"cl\":\"" + JSONObject.escape(client.getLabel()) + "\",");
			b.append("\"order\":[");
			b.append(orders_buf_x.toString());
			b.append("]}");

			//System.out.println("<status>" + status_buf.toString() + "</status>");
			//_writer.println("<status cl=\"" + client.getLabel() + "\">" + status_buf.toString() + "</status>");
			
			return b.toString();
		}
		catch (Exception x)
		{
			x.printStackTrace();
			throw x;
		}
		
		
	}
	
	private static UKOnlinePersonBean last_client = null;
	private static Vector cached_receive_payments = null;
	private static Vector cached_printed_receipts = null;
	private static Vector cached_tenders = null;
	private static Vector cached_orders = null;
	
	private static String
	ledgerToJSON(UKOnlinePersonBean client, int _page_to_display, int _stuff_per_page, Date _last_order_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		int page = 1;

		if (cached_receive_payments == null || last_client == null || !client.equals(last_client)) {
			cached_receive_payments = ReceivePaymentRet.getReceivePayments(client);
		}
		Vector receive_payments = cached_receive_payments;

		StringBuffer orders_buf_x = new StringBuffer();
		boolean orders_buf_x_needs_comma = false;

		// get all the printed receipts

		HashMap<ValeoOrderBean,PrintedReceipt> order_to_receipt_hash = new HashMap<ValeoOrderBean,PrintedReceipt>();
		Vector<PrintedReceipt> unmapped_printed_receipt = new Vector<PrintedReceipt>();



		if (cached_printed_receipts == null || last_client == null || !client.equals(last_client)) {
			cached_printed_receipts = PrintedReceipt.getPrintedReceipts(client);
		}
		Iterator itr = cached_printed_receipts.iterator();
		while (itr.hasNext()) {
			PrintedReceipt receipt = (PrintedReceipt)itr.next();
			if (receipt.hasPrimaryOrder()) {
				order_to_receipt_hash.put(receipt.getPrimaryOrder(), receipt);
			} else {
				unmapped_printed_receipt.addElement(receipt);
			}
		}

		HashMap<ValeoOrderBean,Vector> order_to_tenders_hash = new HashMap<ValeoOrderBean,Vector>();
		HashMap<ValeoOrderBean,Vector> order_to_amount_hash = new HashMap<ValeoOrderBean,Vector>();
		Vector tenders_with_unmapped_portions = new Vector();
		HashMap<TenderRet,BigDecimal> unmapped_amount_hash = new HashMap<TenderRet,BigDecimal>();

		if (cached_tenders == null || last_client == null || !client.equals(last_client)) {
			cached_tenders = TenderRet.getTendersByDateDesc(client);
		}
		Vector tenders = cached_tenders;
		Iterator tender_itr = tenders.iterator();
		while (tender_itr.hasNext()) {
			TenderRet tender = (TenderRet)tender_itr.next();
			BigDecimal tender_amount_not_mapped = tender.getAmountBigDecimal();
			Iterator order_itr = tender.getOrders().iterator();
			while (order_itr.hasNext()) {
				ValeoOrderBean order = (ValeoOrderBean)order_itr.next();
				BigDecimal amount_mapped_to_order = tender.getAmountMappedToOrder(order);
				System.out.println(tender.getValue() + " mapped to " + order.getValue() + " >" + amount_mapped_to_order.toString());
				tender_amount_not_mapped = tender_amount_not_mapped.subtract(amount_mapped_to_order);

				Vector vec_1 = order_to_tenders_hash.get(order);
				if (vec_1 == null) {
					vec_1 = new Vector();
					order_to_tenders_hash.put(order, vec_1);
				}
				vec_1.addElement(tender);

				Vector vec_2 = order_to_amount_hash.get(order);
				if (vec_2 == null) {
					vec_2 = new Vector();
					order_to_amount_hash.put(order, vec_2);
				}
				vec_2.addElement(amount_mapped_to_order);
			}

			if (tender_amount_not_mapped.compareTo(BigDecimal.ZERO) == 1) {
				tenders_with_unmapped_portions.addElement(tender);
				unmapped_amount_hash.put(tender, tender_amount_not_mapped);
			}
		}

		
		boolean do_one_more = false;
		if (cached_orders == null || last_client == null || !client.equals(last_client)) {
			cached_orders = ValeoOrderBean.getOrders(client);
		}
		Iterator orders_itr = cached_orders.iterator();
		for (int i = 1; orders_itr.hasNext(); i++) {
			
			ValeoOrderBean order_obj = (ValeoOrderBean)orders_itr.next();
			Date order_date = order_obj.getOrderDate();
			
			System.out.println(CUBean.getUserDateString(order_date) + " - order_obj found >" + order_obj.getValue());
			System.out.println("_last_order_date >" + _last_order_date);
			if (_last_order_date != null) {
				System.out.println("_last_order_date >" + CUBean.getUserDateString(_last_order_date));
			}
			
			String last_order_date_str = CUBean.getUserDateString(order_date);
			/*
			07/19/16 - order_obj found >52192
			_last_order_date >Mon Aug 29 00:00:00 CDT 2016
			_last_order_date >08/29/16
			*/
			
			
			boolean show = true;
			
			
			if (_last_order_date != null) {
				show = false;
				if (order_date.before(_last_order_date)) {
					//System.out.println("doing show thing >");
					show = true;
				}
			}
			
			
			//if ((page == _page_to_display) && show) {
			if ((page == _page_to_display) || do_one_more) {
				
				
				System.out.println("tenders.isEmpty() >" + tenders.isEmpty());

				// are there any tenders that should be displayed ahead of this order?
				// 7/14/11 - modifying to show all tenders on their own.  I'm thinking I need to provide tool to do things like reverse/refund payments

				//if (!tenders_with_unmapped_portions.isEmpty())
				if (!tenders.isEmpty()) {
					// first one should be most current...

					//TenderRet tender = (TenderRet)tenders_with_unmapped_portions.get(0);
					TenderRet tender = (TenderRet)tenders.get(0);

					Date tender_date = tender.getTenderDate();
					String tender_date_str = CUBean.getUserDateString(tender_date);
					//while (tender_date.after(order_date) && !tenders_with_unmapped_portions.isEmpty())
					
					
					System.out.println("CUBean.getUserDateString(tender_date) >" + CUBean.getUserDateString(tender_date));
					System.out.println("tender_date.after(order_date) >" + tender_date.after(order_date));
					
					
					
					while (tender_date.after(order_date) && !tenders.isEmpty()) {
						
						boolean show_tender = true;
						if (_last_order_date != null) {
							show_tender = false;
							if (tender_date.before(_last_order_date)) {
								show_tender = true;
							}
						}
						
						if (show_tender) {
						
							String orderline_value = null;
							StringBuilder order_line_buf_x = new StringBuilder();

							String tender_type_str = "Received " + tender.getType() + " Tender";
							if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE)) {
								tender_type_str = "Placed On Account";
							}

							order_line_buf_x.append("{\"amount\":\"" + tender.getAmountBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
							order_line_buf_x.append("\"desc\":\"" + JSONObject.escape(tender_type_str) + "\",");
							order_line_buf_x.append("\"ist\":true,");
							order_line_buf_x.append("\"id\":\"t" + tender.getValue() + "\"}");

							if (orders_buf_x_needs_comma) { orders_buf_x.append(','); }
							orders_buf_x.append("{\"label\":\"" + JSONObject.escape(tender.getLabel()) + "\",");
							orders_buf_x.append("\"date\":\"" + JSONObject.escape(CUBean.getUserDateString(tender_date)) + "\",");
							orders_buf_x.append("\"orderline\":[");
							orders_buf_x.append(order_line_buf_x.toString());
							orders_buf_x.append("],");
							//orders_buf_x.append("\"id\":" + order_obj.getId() + "}");
							orders_buf_x.append("\"type\":\"" + tender.getType() + " Tender\",");
							if (tender.isCreditCardPaymentType()) {
								orders_buf_x.append("\"cardType\":\"" + tender.getCreditCardTypeString()+ " Tender\",");
							}
							boolean tender_is_synced = true;
							Iterator tender_order_itr = tender.getOrders().iterator();
							while (tender_order_itr.hasNext()) {
								ValeoOrderBean associated_order = (ValeoOrderBean)tender_order_itr.next();
								if (!associated_order.isUpdated()) {
									tender_is_synced = false;
								}
							}
							orders_buf_x.append("\"status\":\"" + (tender_is_synced ? "SENT TO QB" : "") + "\",");
							orders_buf_x.append("\"id\":" + tender.getId() + "}");
							orders_buf_x_needs_comma = true;
						
						}
						

						//tenders_with_unmapped_portions.removeElementAt(0);
						tenders.removeElementAt(0);

						//if (!tenders_with_unmapped_portions.isEmpty())
						if (!tenders.isEmpty()) {
							//tender = (TenderRet)tenders_with_unmapped_portions.get(0);
							tender = (TenderRet)tenders.get(0);
							tender_date = tender.getTenderDate();
						}
					}
				}

				// are there any receive payments that should be displayed ahead of this order?

				if (!receive_payments.isEmpty()) {

					// first one should be most current...

					ReceivePaymentRet receivePayment = (ReceivePaymentRet)receive_payments.get(0);

					Date txn_date = receivePayment.getTxnDate();
					String txn_date_str = CUBean.getUserDateString(txn_date);
					while (txn_date.after(order_date) && !receive_payments.isEmpty()) {
						
						boolean show_payment = true;
						if (_last_order_date != null) {
							show_payment = false;
							if (txn_date.before(_last_order_date) || txn_date_str.equals(last_order_date_str) ) {
								show_payment = true;
							}
						}
						
						if (show_payment) {
						
							StringBuilder order_line_buf_x = new StringBuilder();

							String tender_type_str = "Received Payment";

							BigDecimal payment_amount = new BigDecimal(receivePayment.getTotalAmount());

							order_line_buf_x.append("{\"amount\":\"" + payment_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
							order_line_buf_x.append("\"desc\":\"" + JSONObject.escape(tender_type_str) + "\",");
							order_line_buf_x.append("\"id\":\"t" + receivePayment.getValue() + "\"}");


							if (orders_buf_x_needs_comma) { orders_buf_x.append(','); }
							orders_buf_x.append("{\"label\":\"" + JSONObject.escape(receivePayment.getLabel()) + "\",");
							orders_buf_x.append("\"date\":\"" + JSONObject.escape(CUBean.getUserDateString(txn_date)) + "\",");
							orders_buf_x.append("\"orderline\":[");
							orders_buf_x.append(order_line_buf_x.toString());
							orders_buf_x.append("],");
							orders_buf_x.append("\"type\":\"Received Payment (Recorded in QuickBooks)\",");
							orders_buf_x.append("\"id\":" + receivePayment.getValue() + "}");
							orders_buf_x_needs_comma = true;
						
						}

						receive_payments.removeElementAt(0);

						if (!receive_payments.isEmpty()) {
							receivePayment = (ReceivePaymentRet)receive_payments.get(0);
							txn_date = receivePayment.getTxnDate();
						}
					}
				}


				try {

					String orderline_value = null;
					StringBuilder order_line_buf_x = new StringBuilder();
					boolean order_line_buf_x_needs_comma = false;
					Iterator order_lines_itr = order_obj.getOrders();

					while (order_lines_itr.hasNext()) {
						CheckoutOrderline order_line = (CheckoutOrderline)order_lines_itr.next();
						CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(order_line.getCheckoutCodeId());
						BigDecimal qty = order_line.getQuantity();
						BigDecimal price = order_line.getPrice();
						BigDecimal actual_amount = order_line.getActualAmount();

						String desc = code.getDescription().replace('\r', ' ').replace('\n', ' ');

						String price_str = price.setScale(2, RoundingMode.HALF_UP).toString();
						//String qty_str = qty.setScale(0, RoundingMode.HALF_UP).toString();
						String qty_str = ClientServlet2.getStringFromBigDecimal(qty);
						String actual_amount_str = actual_amount.setScale(2, RoundingMode.HALF_UP).toString();


						if (order_line_buf_x_needs_comma) { order_line_buf_x.append(','); }
						order_line_buf_x.append("{\"amount\":\"" + price_str + "\",");
						order_line_buf_x.append("\"total\":\"" + actual_amount_str + "\",");
						order_line_buf_x.append("\"qty\":" + qty_str + ",");
						order_line_buf_x.append("\"code\":\"" + JSONObject.escape(code.getCode()) + "\",");
						if (order_line.getPractitionerId() > 0) {
							order_line_buf_x.append("\"practId\":" + order_line.getPractitionerId() + ",");
							order_line_buf_x.append("\"isc\":" + code.isCommissionable() + ",");
							if (order_line.getCommission() == null) {
								order_line_buf_x.append("\"comm\":\"\",");
							} else {
								order_line_buf_x.append("\"comm\":\"" + order_line.getCommission().toString() + "\",");
							}
						}
						order_line_buf_x.append("\"desc\":\"" + JSONObject.escape(desc) + "\",");
						order_line_buf_x.append("\"id\":" + order_line.getCheckoutOrderlineId() + "}");
						order_line_buf_x_needs_comma = true;

					}

					BigDecimal order_tax = order_obj.getTax();
					if (order_tax.compareTo(BigDecimal.ZERO) != 0) {
						// there is some tax on this order.

						if (order_line_buf_x_needs_comma) { order_line_buf_x.append(','); }
						order_line_buf_x.append("{\"amount\":\"" + order_tax.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
						order_line_buf_x.append("\"desc\":\"Tax\",");
						order_line_buf_x.append("\"id\":-1}");
						order_line_buf_x_needs_comma = true;

					}

					if (order_line_buf_x_needs_comma) { order_line_buf_x.append(','); }
					order_line_buf_x.append("{\"amount\":\"" + order_obj.getTotalString() + "\",");
					order_line_buf_x.append("\"desc\":\"Order Total\",");
					order_line_buf_x.append("\"id\":-1}");
					order_line_buf_x_needs_comma = true;


					// get the Tenders for this order

					Vector tenders_vec = order_to_tenders_hash.get(order_obj);
					if (tenders_vec != null) {
						tender_itr = tenders_vec.iterator();
						//tender_itr = TenderRet.getTenders(order_obj).iterator();
						for (int ii = 0; tender_itr.hasNext(); ii++)
						{
							TenderRet tender = (TenderRet)tender_itr.next();
							String tender_type_str = tender.getType() + " Tender (" + tender.getValue() + ")";
							if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE)) {
								tender_type_str = "Placed On Account (" + tender.getValue() + ")";
							}

							Vector vec = order_to_amount_hash.get(order_obj);
							BigDecimal amount_allocated_to_order = (BigDecimal)vec.elementAt(ii);


							if (order_line_buf_x_needs_comma) { order_line_buf_x.append(','); }
							order_line_buf_x.append("{\"amount\":\"" + amount_allocated_to_order.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\",");
							order_line_buf_x.append("\"desc\":\"" + JSONObject.escape(tender_type_str) + "\",");
							order_line_buf_x.append("\"id\":\"t" + tender.getValue() + "\"}");
							order_line_buf_x_needs_comma = true;

						}
					}

					String order_status_str = "";
					if (order_obj.isReversed()) {
						order_status_str = "REVERSED";
					} else if (order_obj.isReturn()) {
						order_status_str = "RETURN";
					} else if (order_obj.isLegacy()) {
						order_status_str = "OLD PAYMENT";
					} else if (order_obj.isUpdated()) {
						order_status_str = "SENT TO QB";
						// sent as?
					}

					String printed_receipt_value = "0";
					PrintedReceipt receipt_for_order = order_to_receipt_hash.get(order_obj);
					if (receipt_for_order != null) {
						printed_receipt_value = receipt_for_order.getValue();
					}

					if (show && (page == _page_to_display)) {

						if (orders_buf_x_needs_comma) { orders_buf_x.append(','); }
						orders_buf_x.append("{\"label\":\"" + JSONObject.escape(order_obj.getLabel()) + "\",");
						orders_buf_x.append("\"date\":\"" + JSONObject.escape(order_obj.getOrderDateString()) + "\",");
						orders_buf_x.append("\"paid\":" + order_obj.isClosed() + ",");
						orders_buf_x.append("\"billed\":" + order_obj.isBilled() + ",");
						orders_buf_x.append("\"collect\":" + order_obj.hasBeenSentToCollections() + ",");
						orders_buf_x.append("\"status\":\"" + JSONObject.escape(order_status_str) + "\",");
						orders_buf_x.append("\"pr\":\"" + printed_receipt_value + "\",");
						orders_buf_x.append("\"balance\":\"" + order_obj.getBalanceString() + "\",");
						orders_buf_x.append("\"orderline\":[");
						orders_buf_x.append(order_line_buf_x.toString());
						orders_buf_x.append("],");

						if (order_obj.hasAccountTender()) {
							// if any part of the order is being placed "On Account", this is communicated to QBs as an Invoice
							orders_buf_x.append("\"type\":\"Invoice\",");
						} else {
							orders_buf_x.append("\"type\":\"Sales Receipt\",");
						}

						orders_buf_x.append("\"id\":" + order_obj.getId() + "}");
						orders_buf_x_needs_comma = true;
					}


				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
				
				if ((page == _page_to_display)) {
					do_one_more = true;
				} else {
					do_one_more = false;
				}
			
			}
			
			
			if (i % _stuff_per_page  == 0) {
				page++;
			}
		}

		StringBuilder b = new StringBuilder();
		b.append("{\"total\":\"0.00\",");
		b.append("\"client\":\"0.00\",");
		b.append("\"balance\":" + client.getBalanceString() + ",");
		b.append("\"cl\":\"" + JSONObject.escape(client.getLabel()) + "\",");
		b.append("\"order\":[");
		b.append(orders_buf_x.toString());
		b.append("]}");

		return b.toString();
	}
	
	private String
	getSalesReceiptURL(PrintedReceipt printed_receipt, UKOnlinePersonBean _logged_in_person, UKOnlineCompanyBean adminCompany) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		
		//PrintedReceipt printed_receipt = PrintedReceipt.getPrintedReceipt(Integer.parseInt(parameter));
		ValeoOrderBean primary_order;
		try {
			primary_order = printed_receipt.getPrimaryOrder();
		} catch (ObjectNotFoundException x) {
			primary_order = new ValeoOrderBean();
		}
		Vector previous_orders = printed_receipt.getOrders();
		Vector tenders = printed_receipt.getTenders();

		BigDecimal subtotal = printed_receipt.getSubTotal();
		BigDecimal tax = printed_receipt.getTax();
		BigDecimal total = printed_receipt.getTotal();

		/*
		BigDecimal total_discount = printed_receipt.getDiscount();
		BigDecimal discount_perc = printed_receipt.getDiscountPercentage();
		*/
		
		BigDecimal total_discount_products = printed_receipt.getDiscountProducts();
		BigDecimal total_discount_services = printed_receipt.getDiscountServices();
		BigDecimal discount_perc_products = printed_receipt.getDiscountPercentageProducts();
		BigDecimal discount_perc_services = printed_receipt.getDiscountPercentageServices();

		BigDecimal shipping = printed_receipt.getShipping();

		UKOnlinePersonBean cashier = null;
		try {
			cashier = printed_receipt.getCashier();
		} catch (Exception x) {
			cashier = _logged_in_person;
		}

		String sales_receipt_url = CUBean.getProperty("cu.webDomain") + "/resources/pdf/" + SalesReceiptBuilder.generateSalesReceipt(adminCompany, cashier, printed_receipt.getPerson(), primary_order, previous_orders, tenders, subtotal, tax, total, total_discount_products, total_discount_services, discount_perc_products, discount_perc_services, shipping);
		return sales_receipt_url;
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
