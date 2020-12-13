/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.servlets;

import java.io.*;
import java.text.*;
import java.util.*;
import java.math.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.badiyan.torque.*;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.PDF.SalesReceiptBuilder;
import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.tasks.MergeClientTask;

import com.valeo.qb.data.ReceivePaymentRet;
import com.valeo.qbpos.data.*;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class ClientServlet extends HttpServlet {
	
	public static PrintedReceipt selectedReceipt;

	public static boolean is_integrated_with_qb = true;
	public static boolean is_integrated_with_pos = true;
   
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest _request, HttpServletResponse _response)
    throws ServletException, IOException {
		
		System.out.println("processRequest() invoked in ClientServlet");

		SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");

		HttpSession session = _request.getSession(false);

		//set the content type
		_response.setContentType("text/xml");
		_response.setHeader("Cache-Control", "no-cache");

		//get the PrintWriter object to write the html page
		PrintWriter writer = _response.getWriter();

		StringBuffer returnBuf = new StringBuffer();

		try
		{
			UKOnlineCompanyBean adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
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
			System.out.println("command >" + command);
			System.out.println("parameter >" + parameter);
			System.out.println("arg1 >" + arg1);
			System.out.println("arg2 >" + arg2);
			System.out.println("arg3 >" + arg3);
			System.out.println("arg4 >" + arg4);
			System.out.println("arg5 >" + arg5);
			System.out.println("arg6 >" + arg6);
			System.out.println("arg7 >" + arg7);

			if (command.equals("refreshFinancial"))
			{
				UKOnlinePersonBean client = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				
				// get the purchased plans for the financial tab
				
				StringBuffer status_buf = new StringBuffer();
				status_buf.append("<purchased-plans>");
				Iterator purchased_plan_instances_itr = PaymentPlanInstanceBean.getPaymentPlanInstances(client).iterator();
				while (purchased_plan_instances_itr.hasNext())
				{
					PaymentPlanInstanceBean purchased_plan = (PaymentPlanInstanceBean)purchased_plan_instances_itr.next();
					status_buf.append(this.getXML(purchased_plan));
				}
				status_buf.append("</purchased-plans>");

				// get the receive payments

				//Vector receive_payments = ReceivePaymentRet.getReceivePayments(adminCompany, client.getEmail2String());
				
				// get the ledger transactions for the ledger tab

				BigDecimal total = BigDecimal.ZERO;
				BigDecimal patient = BigDecimal.ZERO;
				BigDecimal balance = BigDecimal.ZERO;
				
				StringBuffer orders_buf = new StringBuffer();
				Iterator orders_itr = ValeoOrderBean.getOrders(client).iterator();
				for (int i = 0; orders_itr.hasNext(); i++)
				{
					ValeoOrderBean order_obj = (ValeoOrderBean)orders_itr.next();

					try
					{
						if ((order_obj.getMemo().indexOf("POS Receipt #") == -1) || !is_integrated_with_pos) // less than ideal
						{
							BigDecimal orderline_tax_total = BigDecimal.ZERO;

							StringBuffer order_line_buf = new StringBuffer();
							Iterator order_lines_itr = order_obj.getOrders();

							while (order_lines_itr.hasNext())
							{
								CheckoutOrderline order_line = (CheckoutOrderline)order_lines_itr.next();
								CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(order_line.getCheckoutCodeId());
								BigDecimal amount = order_line.getPrice();

								if (order_obj.getStatus().equals(ValeoOrderBean.RECEIVE_PAYMENT_ORDER_STATUS)) // rewrite given that there should be no "RECEIVE PAYMENT" orders.  there should always be tenders mapped to orders...
								{
									// treat as payment

									balance = balance.subtract(amount);
									patient = patient.add(amount);

									String orderline_value = "{\"code\" : \"\"," +
													" \"amount\" : \"" + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
													" \"total\" : \"\"," +
													" \"patient\" : \"" + patient.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
													" \"balance\" : \"" + balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
													" \"desc\" : \"" + code.getDescription() + "\"}";
									order_line_buf.append("<orderline><key>" + order_line.getCheckoutOrderlineId() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");
								}
								else
								{
									BigDecimal orderline_total = order_line.getActualAmount();
									balance = balance.add(orderline_total);
									if (amount.compareTo(BigDecimal.ZERO) == 1)
										total = total.add(orderline_total);
									else
										patient = patient.add(amount);

									String orderline_value = "{\"code\" : \"" + code.getCode() + "\"," +
													" \"amount\" : \"" + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
													" \"total\" : \"" + orderline_total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
													" \"patient\" : \"" + patient.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
													" \"balance\" : \"" + balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
													" \"desc\" : \"" + code.getDescription() + "\"}";
									order_line_buf.append("<orderline><key>" + order_line.getCheckoutOrderlineId() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");

									orderline_tax_total = orderline_tax_total.add(order_line.getTax().multiply(order_line.getQuantity()));
								}
							}

							// see if I need to apply a tax rounding adjustment

							if (orderline_tax_total.compareTo(order_obj.getTax()) != 0)
							{
								// the tax for the orderlines doesn't match the tax for the entire order.  lame.  make an adjustment, I guess

								BigDecimal tax_adjustment = order_obj.getTax().subtract(orderline_tax_total);
								balance = balance.add(tax_adjustment);

								String orderline_value = "{\"code\" : \"\"," +
												" \"amount\" : \"" + tax_adjustment.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
												" \"total\" : \"" + tax_adjustment.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
												" \"patient\" : \"\"," +
												" \"balance\" : \"" + balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
												" \"desc\" : \"Tax Rounding Adjustment\"}";
								order_line_buf.append("<orderline><key>a0</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");
							}

							// get the Tenders for this order

							Iterator tender_itr = TenderRet.getTenders(order_obj).iterator();
							while (tender_itr.hasNext())
							{
								TenderRet tender = (TenderRet)tender_itr.next();
								if (!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
								{
									BigDecimal amount = tender.getAmountBigDecimal();
									balance = balance.subtract(amount);
									patient = patient.add(amount);

									String orderline_value = "{\"code\" : \"\"," +
													" \"amount\" : \"" + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
													" \"total\" : \"\"," +
													" \"patient\" : \"" + patient.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
													" \"balance\" : \"" + balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
													" \"desc\" : \"" + tender.getType() + "\"}";
									order_line_buf.append("<orderline><key>t" + tender.getValue() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");
								}
							}

							String value = null;
							if (ClientServlet.is_integrated_with_qb)
							{
								if (order_obj.getStatus().equals(ValeoOrderBean.RECEIVE_PAYMENT_ORDER_STATUS))
								{
									ReceivePaymentRet receive_payment_ret_obj = ReceivePaymentRet.getReceivePayment(adminCompany, order_obj);
									value = "{\"label\" : \"" + order_obj.getLabel() + "\"," +
													" \"value\" : \"" + receive_payment_ret_obj.getTxnNumber() + "\"," +
													" \"date\" : \"" + order_obj.getOrderDateString() + "\"," +
													" \"paid\" : \"" + order_obj.isClosed() + "\"," +
													" \"billed\" : \"" + order_obj.isBilled() + "\"," +
													" \"collect\" : \"" + order_obj.hasBeenSentToCollections() + "\"," +
													" \"status\" : \"" + order_obj.getMemo() + "\"," +
													" \"balance\" : \"" + order_obj.getBalanceString() + "\"}";
								}
								else
								{
									try
									{
										SalesReceiptRet sales_receipt_ret_obj = SalesReceiptRet.getSalesReceipt(adminCompany, order_obj);
										value = "{\"label\" : \"" + order_obj.getLabel() + "\"," +
														" \"value\" : \"" + sales_receipt_ret_obj.getSalesReceiptNumber() + "\"," +
														" \"date\" : \"" + order_obj.getOrderDateString() + "\"," +
														" \"paid\" : \"" + order_obj.isClosed() + "\"," +
														" \"billed\" : \"" + order_obj.isBilled() + "\"," +
														" \"collect\" : \"" + order_obj.hasBeenSentToCollections() + "\"," +
														" \"status\" : \"" + sales_receipt_ret_obj.getSalesReceiptType() + ", " + sales_receipt_ret_obj.getHistoryDocStatus() + "\"," +
														" \"balance\" : \"" + order_obj.getBalanceString() + "\"}";
									}
									catch (ObjectNotFoundException obj_not_found)
									{
										System.out.println(obj_not_found.getMessage());
										value = "{\"label\" : \"" + order_obj.getLabel() + "\"," +
														" \"value\" : \"" + order_obj.getValue() + "\"," +
														" \"date\" : \"" + order_obj.getOrderDateString() + "\"," +
														" \"paid\" : \"" + order_obj.isClosed() + "\"," +
														" \"billed\" : \"" + order_obj.isBilled() + "\"," +
														" \"collect\" : \"" + order_obj.hasBeenSentToCollections() + "\"," +
														" \"balance\" : \"" + order_obj.getBalanceString() + "\"}";
									}
								}
							}
							else
							{
								value = "{\"label\" : \"" + order_obj.getLabel() + "\"," +
												" \"value\" : \"" + order_obj.getValue() + "\"," +
												" \"date\" : \"" + order_obj.getOrderDateString() + "\"," +
												" \"paid\" : \"" + order_obj.isClosed() + "\"," +
												" \"billed\" : \"" + order_obj.isBilled() + "\"," +
												" \"collect\" : \"" + order_obj.hasBeenSentToCollections() + "\"," +
												" \"balance\" : \"" + order_obj.getBalanceString() + "\"}";
							}

							orders_buf.append("<order><key>" + order_obj.getId() + "</key><value><![CDATA[" + value + "]]></value><orderlines>" + order_line_buf.toString() + "</orderlines></order>");

						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
				
				status_buf.append("<ledger total=\"" + total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\" client=\"" + patient.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\" balance=\"" + balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\">");
				status_buf.append(orders_buf.toString());
				status_buf.append("</ledger>");

				System.out.println("<status>" + status_buf.toString() + "</status>");
				writer.println("<status>" + status_buf.toString() + "</status>");
			}
			else if (command.equals("showPurchasedPlans"))
			{
				UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(parameter));
				session.setAttribute("adminPerson", client);
				
				// get the purchased plans for the financial tab
				
				StringBuffer status_buf = new StringBuffer();
				status_buf.append("<purchased-plans>");
				Iterator purchased_plan_instances_itr = PaymentPlanInstanceBean.getPaymentPlanInstances(client).iterator();
				while (purchased_plan_instances_itr.hasNext())
				{
					PaymentPlanInstanceBean purchased_plan = (PaymentPlanInstanceBean)purchased_plan_instances_itr.next();
					status_buf.append(this.getXML(purchased_plan));
				}
				status_buf.append("</purchased-plans>");

				System.out.println("<status>" + status_buf.toString() + "</status>");
				writer.println("<status>" + status_buf.toString() + "</status>");
			}
			else if (command.equals("showLedger"))
			{
				Date ledger_start_date = (Date)session.getAttribute("ledger_start_date");
				ClientServlet.refreshLedger(session, writer, parameter, ledger_start_date);
			}
			else if (command.equals("showLedgerThisMonth"))
			{
				// find the start date for this month
				
				session.setAttribute("ledger_view", "month");
				
				Calendar c = Calendar.getInstance();
				c.set(Calendar.DAY_OF_MONTH, 1);
				Date ledger_start_date = c.getTime();
				session.setAttribute("ledger_start_date", ledger_start_date);
				
				ClientServlet.refreshLedger(session, writer, parameter, ledger_start_date);
			}
			else if (command.equals("showLedgerThisYear"))
			{
				session.setAttribute("ledger_view", "year");
				
				Calendar c = Calendar.getInstance();
				c.set(Calendar.DAY_OF_YEAR, 1);
				Date ledger_start_date = c.getTime();
				session.setAttribute("ledger_start_date", ledger_start_date);
				
				ClientServlet.refreshLedger(session, writer, parameter, ledger_start_date);
			}
			else if (command.equals("showLedgerAll"))
			{
				session.setAttribute("ledger_view", "all");
				
				Calendar c = Calendar.getInstance();
				c.add(Calendar.YEAR, -25); // go back 25 years for entire ledger, I guess.  I'm prolly gonna want to add some paging to the ledger view or something
				Date ledger_start_date = c.getTime();
				session.setAttribute("ledger_start_date", ledger_start_date);
				ClientServlet.refreshLedger(session, writer, parameter, ledger_start_date);
			}
			else if (command.equals("saveOrderline"))
			{
				System.out.println("saveOrderline >" + parameter);
				
				CheckoutOrderline orderline = null;
				UKOnlinePersonBean practitioner = null;
				BigDecimal commission_amount = null;
				
				StringTokenizer tokenizer = new StringTokenizer(parameter, "|");
				for (int i = 0; tokenizer.hasMoreTokens(); i++)
				{
					String token = tokenizer.nextToken();
					switch (i)
					{
						case 0 : {
							orderline = ValeoOrderBean.getCheckoutOrderline(Integer.parseInt(token));
							break;
						}
						case 1 : {
							if (!token.equals("-1"))
								practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(token));
							break;
						}
						case 2 : {
							if (token != null && token.length() > 0)
								commission_amount = new BigDecimal(token);
							break;
						}
					}
				}
				
				if (practitioner != null && commission_amount != null)
				{
					ValeoOrderBean order = (ValeoOrderBean)ValeoOrderBean.getOrder(orderline.getOrderid());
					order.invalidate();
							
					orderline.setPractitionerId(practitioner.getId());
					orderline.setCommission(commission_amount);
					orderline.save();
				}
			}
			else if (command.equals("deleteOrderline"))
			{
				System.out.println("deleteOrderline >" + parameter);
				
				CheckoutOrderline orderline = null;
				UKOnlinePersonBean practitioner = null;
				BigDecimal commission_amount = null;
				
				StringTokenizer tokenizer = new StringTokenizer(parameter, "|");
				for (int i = 0; tokenizer.hasMoreTokens(); i++)
				{
					String token = tokenizer.nextToken();
					switch (i)
					{
						case 0 : {
							orderline = ValeoOrderBean.getCheckoutOrderline(Integer.parseInt(token));
							break;
						}
						case 1 : {
							if (!token.equals("-1"))
								practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(token));
							break;
						}
						case 2 : {
							if (token != null && token.length() > 0)
								commission_amount = new BigDecimal(token);
							break;
						}
					}
				}
				
				
				ValeoOrderBean order = (ValeoOrderBean)ValeoOrderBean.getOrder(orderline.getOrderid());
				ValeoOrderBean.delete(order.getId());
				
				// delete person orders too
				
				Criteria crit = new Criteria();
				crit.add(PersonorderPeer.ORDERID, order.getId());
				PersonorderPeer.doDelete(crit);
				
				// delete printed receipt objects
				
				crit = new Criteria();
				crit.add(PrintedReceiptDbPeer.PRIMARY_ORDER_ID, order.getId());
				PrintedReceiptDbPeer.doDelete(crit);
				
				/*
				orderline.setPractitionerId(practitioner.getId());
				orderline.setCommission(commission_amount);
				orderline.save();
				 */
				
				
				Date ledger_start_date = (Date)session.getAttribute("ledger_start_date");
				ClientServlet.refreshLedger(session, writer, parameter, ledger_start_date);
			}
			else if (command.equals("viewReceipt"))
			{
				System.out.println("viewReceipt invoked");
				
				
				//UKOnlinePersonBean client = (UKOnlinePersonBean)session.getAttribute("adminPerson");

				PrintedReceipt printed_receipt = PrintedReceipt.getPrintedReceipt(Integer.parseInt(parameter));
				
				/*
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
				
				UKOnlinePersonBean cashier = null;
				try
				{
					cashier = printed_receipt.getCashier();
				}
				catch (Exception x)
				{
					cashier = (UKOnlinePersonBean)adminLoginBean.getPerson();
				}
				

				String sales_receipt_url = "../resources/pdf/" + SalesReceiptBuilder.generateSalesReceipt(adminCompany, cashier, client, primary_order, previous_orders, tenders, subtotal, tax, total, total_discount, discount_perc, shipping);
				* */
				
				String sales_receipt_url = printed_receipt.getPrintedReceiptURL();
				if (sales_receipt_url == null) {
					sales_receipt_url = this.getSalesReceiptURL(printed_receipt, adminLoginBean, adminCompany);
					printed_receipt.setPrintedReceiptURL(sales_receipt_url);
				}
				
				writer.println("<sales_receipt_url><![CDATA[" + sales_receipt_url + "]]></sales_receipt_url>");
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
			else if (command.equals("getPersonDetails"))
			{
				UKOnlinePersonBean person = null;
				if (!parameter.equals("-1"))
				{
					person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(parameter));

					// put this person in the session as the selected person
					session.setAttribute("adminPerson", person);
				}
				else
					person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				
				System.out.println(this.getDetailedXML(person, parameter.equals("-1")));
				writer.println(this.getDetailedXML(person, parameter.equals("-1")));
			}
			else if (command.equals("getPlanInstanceDetails"))
			{
				PaymentPlanInstanceBean payment_plan_instance = null;
				if (!parameter.equals("-1"))
				{
					payment_plan_instance = PaymentPlanInstanceBean.getPaymentPlanInstance(Integer.parseInt(parameter));

					// put this plan in the session as the selected plan
					session.setAttribute("adminPlanInstance", payment_plan_instance);
				}
				else
					payment_plan_instance = (PaymentPlanInstanceBean)session.getAttribute("adminPlanInstance");
				
				System.out.println(ClientServlet.getDetailedXML(payment_plan_instance));
				writer.println(ClientServlet.getDetailedXML(payment_plan_instance));
				
				/*
				Calendar display_date = (Calendar)session.getAttribute("display_date");

				returnBuf.append("<date>" + date_format.format(display_date.getTime()) + "</date>");
				returnBuf.append("<schedule>");
				returnBuf.append(this.getOffTimeXML(adminCompany, display_date.getTime()));
				returnBuf.append("</schedule>");

				System.out.println("");
				System.out.println("<status>" + returnBuf.toString() + "</status>");
				System.out.println("");
				writer.println("<status>" + returnBuf.toString() + "</status>");
				 */
			}
			else if (command.equals("getClientBillingInfo"))
			{
				UKOnlinePersonBean person = null;
				if (!parameter.equals("-1"))
				{
					person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(parameter));

					// put this person in the session as the selected person
					session.setAttribute("adminPerson", person);
				}
				else
					person = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				
				StringBuffer buf = new StringBuffer();
				
				Iterator itr = BillingBean.getBillings(person).iterator();
				while (itr.hasNext())
				{
					BillingBean bill = (BillingBean)itr.next();
					buf.append(getXML(bill));
				}

				System.out.println("<client-billing>" + buf.toString() + "</client-billing>");
				writer.println("<client-billing>" + buf.toString() + "</client-billing>");
			}
			else if (command.equals("getBillingDetails"))
			{
				BillingBean billing = BillingBean.getBilling(Integer.parseInt(parameter));
				System.out.println(this.getDetailedXML(billing));
				writer.println(this.getDetailedXML(billing));
			}
			else if (command.equals("getBillingOrderDetails"))
			{
				BillingBean billing = BillingBean.getBilling(Integer.parseInt(parameter));
				
				StringBuffer buf = new StringBuffer();
				
				Iterator itr = billing.getOrders().iterator();
				while (itr.hasNext())
				{
					//PersonBillingPersonorder obj = (PersonBillingPersonorder)itr.next();
					ValeoOrderBean order = (ValeoOrderBean)itr.next();
					//OrderBean order = OrderBean.getOrder(obj.getOrderId());
					buf.append(getDetailedXML(order));
				}

				System.out.println("<billing-orders>" + buf.toString() + "</billing-orders>");
				writer.println("<billing-orders>" + buf.toString() + "</billing-orders>");
			}
			else if (command.equals("getCheckoutDetails"))
			{
				//CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(parameter));
				//PaymentPlanBean payment_plan = PaymentPlanBean.getPaymentPlan(checkout_code);
				//System.out.println("payment_plan >" + payment_plan.getLabel());
				//AppointmentBean selected_appointment = AppointmentBean.getAppointment(Integer.parseInt(parameter));
				
				boolean editingOrder = false;
				int selected_order_id = Integer.parseInt(parameter);
				if (selected_order_id > 0)
					editingOrder = true;

				
				UKOnlinePersonBean client = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				
				//session.setAttribute("adminAppointment", selected_appointment);
				session.setAttribute("adminPerson", client);
				
				String group_under_care_str = "";
				
				try
				{
					GroupUnderCareBean group_under_care = client.getGroupUnderCare();
					group_under_care_str = ScheduleServlet.getXML(group_under_care);
				}
				catch (ObjectNotFoundException x)
				{
				}
				
				StringBuffer orderlines_buf = new StringBuffer();
				if (editingOrder)
				{
					Vector charge_orderlines = new Vector();
					Vector payment_orderlines = new Vector();
					
					ValeoOrderBean order_being_edited = (ValeoOrderBean)ValeoOrderBean.getOrder(selected_order_id);
					Iterator orderlines_itr = order_being_edited.getOrders();
					while (orderlines_itr.hasNext())
					{
						CheckoutOrderline orderline = (CheckoutOrderline)orderlines_itr.next();
						CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
						if (code.isPayment())
							payment_orderlines.addElement(orderline);
						else
							charge_orderlines.addElement(orderline);
					}
					
					
					orderlines_buf.append("<charge-orderlines>");
					orderlines_itr = charge_orderlines.iterator();
					while (orderlines_itr.hasNext())
					{
						CheckoutOrderline orderline = (CheckoutOrderline)orderlines_itr.next();
						orderlines_buf.append(ScheduleServlet.getXML(order_being_edited, orderline));
					}
					orderlines_buf.append("</charge-orderlines>");
					
					orderlines_buf.append("<payment-orderlines>");
					orderlines_itr = payment_orderlines.iterator();
					while (orderlines_itr.hasNext())
					{
						CheckoutOrderline orderline = (CheckoutOrderline)orderlines_itr.next();
						orderlines_buf.append(ScheduleServlet.getXML(order_being_edited, orderline));
					}
					orderlines_buf.append("</payment-orderlines>");
				}
				else
				{
					orderlines_buf.append("<open-orderlines>");
					Iterator open_orders_itr = client.getOpenOrders().iterator();
					while (open_orders_itr.hasNext())
					{
						ValeoOrderBean open_order_obj = (ValeoOrderBean)open_orders_itr.next();
						Iterator orderlines_itr = open_order_obj.getOrders();
						while (orderlines_itr.hasNext())
						{
							CheckoutOrderline orderline = (CheckoutOrderline)orderlines_itr.next();
							if (orderline.getOrderstatus().equals(ValeoOrderBean.OPEN_ORDER_STATUS))
								orderlines_buf.append(ScheduleServlet.getXML(open_order_obj, orderline));
						}
					}
					orderlines_buf.append("</open-orderlines>");
				}
				
				String xml_str = "<checkout>" + ScheduleServlet.getXMLIncludeFileType(client) + "<previous>" + client.getBalanceString() + "</previous>" + group_under_care_str + orderlines_buf.toString() + "</checkout>";
				System.out.println(xml_str);
				writer.println(xml_str);
			}
			else if (command.equals("addPlanInstance"))
			{
				CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(parameter));
				PaymentPlanBean plan = PaymentPlanBean.getPaymentPlan(checkout_code);
				
				UKOnlinePersonBean client = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				
				PaymentPlanInstanceBean payment_plan_instance = new PaymentPlanInstanceBean();
				payment_plan_instance.setPaymentPlan(plan);
				payment_plan_instance.setPerson(client);
				Calendar now = Calendar.getInstance();
				payment_plan_instance.setStartDate(now.getTime());

				payment_plan_instance.setNumberOfVisitsRemaining(plan.getVisits());
				payment_plan_instance.setAmountPaid(BigDecimal.ZERO);
				payment_plan_instance.setAmountCharged(checkout_code.getAmount());
				payment_plan_instance.setGroupPoolingAllowed(plan.isGroupPoolingAllowed());
				payment_plan_instance.setCreatePerson((UKOnlinePersonBean)adminLoginBean.getPerson());
				payment_plan_instance.activate();
				payment_plan_instance.save();
				
				StringBuffer purchased_plans_buf = new StringBuffer();
				purchased_plans_buf.append("<purchased-plans>");
				Iterator purchased_plan_instances_itr = PaymentPlanInstanceBean.getPaymentPlanInstances(client).iterator();
				while (purchased_plan_instances_itr.hasNext())
				{
					PaymentPlanInstanceBean purchased_plan = (PaymentPlanInstanceBean)purchased_plan_instances_itr.next();
					purchased_plans_buf.append(this.getXML(purchased_plan));
				}
				purchased_plans_buf.append("</purchased-plans>");

				System.out.println("<status>" + purchased_plans_buf.toString() + "</status>");
				writer.println("<status>" + purchased_plans_buf.toString() + "</status>");
				
			}
			else if (command.equals("deletePlanInstance"))
			{
				PaymentPlanInstanceBean plan = PaymentPlanInstanceBean.getPaymentPlanInstance(Integer.parseInt(parameter));
				UKOnlinePersonBean client = plan.getPerson();
				
				// determine if this plan has already been paid for...
				
				ValeoOrderBean order_to_delete = null;
				CheckoutOrderline orderline_to_delete = null;
				boolean delete_order = false;
				
				Iterator order_itr = ValeoOrderBean.getOrders(client).iterator();
				while (order_itr.hasNext())
				{
					ValeoOrderBean order_obj = (ValeoOrderBean)order_itr.next();
					Iterator orderline_itr = order_obj.getOrders();
					for (int i = 1; orderline_itr.hasNext(); i++)
					{
						CheckoutOrderline orderline_obj = (CheckoutOrderline)orderline_itr.next();
						if (orderline_obj.getPaymentPlanInstanceId() == plan.getId())
						{
							if (orderline_obj.getActualAmount().intValue() > 0)
								throw new IllegalValueException("Cannot delete.  Payments have been made on plan.");
							order_to_delete = order_obj;
							orderline_to_delete = orderline_obj;
							delete_order = !((i > 1) || orderline_itr.hasNext());
							break;
						}
					}
				}
				
				if (orderline_to_delete != null)
					CheckoutOrderlinePeer.doDelete(orderline_to_delete);
				
				if ((order_to_delete != null) && delete_order)
					ValeoOrderBean.delete(order_to_delete.getId());
					
				
				PaymentPlanInstanceBean.delete(plan.getId());
				
				// I just need to get the purchased plans for the financial page, I guess...
				
				StringBuffer purchased_plans_buf = new StringBuffer();
				purchased_plans_buf.append("<purchased-plans>");
				Iterator purchased_plan_instances_itr = PaymentPlanInstanceBean.getPaymentPlanInstances(client).iterator();
				while (purchased_plan_instances_itr.hasNext())
				{
					PaymentPlanInstanceBean purchased_plan = (PaymentPlanInstanceBean)purchased_plan_instances_itr.next();
					purchased_plans_buf.append(this.getXML(purchased_plan));
				}
				purchased_plans_buf.append("</purchased-plans>");

				System.out.println("<status>" + purchased_plans_buf.toString() + "</status>");
				writer.println("<status>" + purchased_plans_buf.toString() + "</status>");
			}
			else if (command.equals("nextFileNumber"))
			{
				StringBuffer retBuf = new StringBuffer();
				retBuf.append("<nfn num=\"" + UKOnlinePersonBean.getNextFileNumber(adminCompany) + "\"></nfn>");

				//System.out.println(retBuf.toString());
				writer.println(retBuf.toString());
			}
			else if (command.equals("mergeClients"))
			{
				System.out.println("mergeClients invoked >" + parameter);
				
				Calendar now = Calendar.getInstance();
				now.add(Calendar.SECOND, 2);


				UKOnlinePersonBean source = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(parameter.substring(0, parameter.indexOf("|"))));
				UKOnlinePersonBean destination = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(parameter.substring(parameter.indexOf("|") + 1)));
				
				System.out.println("client 1 >" + source.getLabel());
				System.out.println("client 2 >" + destination.getLabel());


				Timer timerObj = new Timer(true);
				timerObj.schedule(new MergeClientTask(source, destination), now.getTime());
				
				
				/*
				StringBuffer retBuf = new StringBuffer();
				retBuf.append("<nfn num=\"" + UKOnlinePersonBean.getNextFileNumber(adminCompany) + "\"></nfn>");

				//System.out.println(retBuf.toString());
				writer.println(retBuf.toString());
				*/
			}
			else if (command.equals("getLastNReceipts"))
			{
				writer.println(getReceipts(adminCompany, adminLoginBean));
			}
			else if (command.equals("selectReceipt"))
			{
				ClientServlet.selectedReceipt = PrintedReceipt.getPrintedReceipt(Integer.parseInt(parameter));
				writer.println(getReceipts(adminCompany, adminLoginBean));
			}
			


		}
		catch (Exception x)
		{
			writer.println("<error><![CDATA[" + x.getMessage() + "]]></error>");
			x.printStackTrace();
		}
        
    }
	
	private String
	getReceipts(UKOnlineCompanyBean _adminCompany, UKOnlineLoginBean _adminLoginBean) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, Exception {
		
		StringBuffer buf = new StringBuffer();
		buf.append("<receipts>");

		//PrintedReceipt selectedReceipt = (PrintedReceipt)session.getAttribute("selectedReceipt");
		if (_adminCompany.isNew())
			_adminCompany = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5);
		Iterator receipt_itr = PrintedReceipt.getPrintedReceipts(_adminCompany, 12).iterator();
		while (receipt_itr.hasNext()) {
			PrintedReceipt receiptObj = (PrintedReceipt)receipt_itr.next();

			if (ClientServlet.selectedReceipt == null) {
				ClientServlet.selectedReceipt = receiptObj;
			}

			String sales_receipt_url = ClientServlet.selectedReceipt.getPrintedReceiptURL();
			System.out.println("sales_receipt_url > " + sales_receipt_url);
			if (sales_receipt_url == null) {
				sales_receipt_url = this.getSalesReceiptURL(ClientServlet.selectedReceipt, _adminLoginBean, _adminCompany);
				ClientServlet.selectedReceipt.setPrintedReceiptURL(sales_receipt_url);
				System.out.println("sales_receipt_url vv > " + sales_receipt_url);
			}

			if (ClientServlet.selectedReceipt.getId() == receiptObj.getId()) {
				receiptObj.setPrintedReceiptURL(sales_receipt_url);
			}

			buf.append(this.getXML(receiptObj, ClientServlet.selectedReceipt.getId() == receiptObj.getId()));
		}
		buf.append("</receipts>");

		//System.out.println(buf.toString());
		return buf.toString();
	}
    
    private String
    getXML(PrintedReceipt _receipt, boolean _is_selected)
		throws Exception
    {
		String nowStr = CUBean.getUserDateString(new Date());
		String dateStr = CUBean.getUserDateString(_receipt.getReceiptDate());
		
		String value = "{\"label\" : \"" + _receipt.getLabel() + "\"," +
						" \"client\" : \"" + _receipt.getPerson().getLabel() + "\"," +
						" \"date\" : \"" + dateStr + "\"," +
						" \"time\" : \"" + CUBean.getUserTimeString(_receipt.getReceiptDate()) + "\"," +
						" \"isToday\" : \"" + nowStr.equals(dateStr) + "\"," +
						" \"isSelected\" : \"" + _is_selected + "\"," +
						" \"url\" : \"" + _receipt.getPrintedReceiptURL() + "\"," +
						" \"total\" : \"" + _receipt.getTotal().toString() + "\"}";
		return "<receipt><key>" + _receipt.getId() + "</key><value><![CDATA[" + value + "]]></value></receipt>";
    }
	
	private String
	getSalesReceiptURL(PrintedReceipt printed_receipt, UKOnlineLoginBean adminLoginBean, UKOnlineCompanyBean adminCompany) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		
		//PrintedReceipt printed_receipt = PrintedReceipt.getPrintedReceipt(Integer.parseInt(parameter));
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

		UKOnlinePersonBean cashier = null;
		try
		{
			cashier = printed_receipt.getCashier();
		}
		catch (Exception x)
		{
			cashier = (UKOnlinePersonBean)adminLoginBean.getPerson();
		}


		//String sales_receipt_url = "../resources/pdf/" + SalesReceiptBuilder.generateSalesReceipt(adminCompany, cashier, client, primary_order, previous_orders, tenders, subtotal, tax, total, total_discount, discount_perc, shipping);
		String sales_receipt_url = "../resources/pdf/" + SalesReceiptBuilder.generateSalesReceipt(adminCompany, cashier, printed_receipt.getPerson(), primary_order, previous_orders, tenders, subtotal, tax, total, total_discount, discount_perc, shipping);
		return sales_receipt_url;
		//writer.println("<sales_receipt_url><![CDATA[" + sales_receipt_url + "]]></sales_receipt_url>");
	}
    
    private String
    getXML(BillingBean _bill)
		throws Exception
    {
		String value = "{\"label\" : \"" + _bill.getLabel() + "\"," +
						" \"value\" : \"" + _bill.getValue() + "\"," +
						" \"amount\" : \"" + _bill.getAmountBilledString() + "\"," +
						" \"dates\" : \"" + _bill.getBillingDatesString() + "\"," +
						" \"c\" : \"" + (_bill.hasBeenSentToCollections() ? "true" : "false") + "\"," +
						" \"c_amount\" : \"" + _bill.getCollectionsAmountString() + "\"}";
		return "<bill><key>" + _bill.getId() + "</key><value><![CDATA[" + value + "]]></value></bill>";
    }
    
    private String
    getXML(UKOnlinePersonBean _person)
		throws Exception
    {
		String value = "{\"label\" : \"" + _person.getLabel() + "\"}";
		return "<person><key>" + _person.getId() + "</key><value><![CDATA[" + value + "]]></value></person>";
    }
    
    private String
    getDetailedXML(BillingBean _bill)
		throws Exception
    {
		String value = "{\"label\" : \"" + _bill.getLabel() + "\"," +
						" \"value\" : \"" + _bill.getValue() + "\"," +
						" \"amount\" : \"" + _bill.getAmountBilledString() + "\"," +
						" \"dates\" : \"" + _bill.getBillingDatesString() + "\"," +
						" \"c\" : \"" + (_bill.hasBeenSentToCollections() ? "true" : "false") + "\"," +
						" \"c_amount\" : \"" + _bill.getCollectionsAmountString() + "\"}";
		return "<bill><key>" + _bill.getId() + "</key><value><![CDATA[" + value + "]]></value></bill>";
    }
    
    private String
    getDetailedXML(ValeoOrderBean _order)
		throws Exception
    {
		String value = "{\"label\" : \"" + _order.getLabel() + "\"," +
						" \"value\" : \"" + _order.getValue() + "\"," +
						" \"date\" : \"" + _order.getOrderDateString() + "\"," +
						" \"balance\" : \"" + _order.getBalanceString() + "\"}";
		return "<order><key>" + _order.getId() + "</key><value><![CDATA[" + value + "]]></value></order>";
    }
    
    private String
    getDetailedXML(UKOnlinePersonBean _person, boolean _selected)
		throws Exception
    {
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

		int selected_marketing_plan_id = 0;
		int selected_referred_by_client_id = 0;
		String selected_referred_by_client_label = "-- SEARCH FOR A CLIENT --";
		try
		{
			ReferralSource referral_source = ReferralSource.getReferralSource(_person);
			if (referral_source.isMarketingPlanReferral())
				selected_marketing_plan_id = referral_source.getMarketingPlan().getId();
			else if (referral_source.isClientReferral())
			{
				UKOnlinePersonBean selected_referred_by_client = referral_source.getReferredByClient();
				selected_referred_by_client_id = selected_referred_by_client.getId();
				selected_referred_by_client_label = selected_referred_by_client.getLabel();
			}
		}
		catch (ObjectNotFoundException x)
		{
		}
		catch (UniqueObjectNotFoundException x)
		{
		}
				
		String value = "{\"label\" : \"" + _person.getLabel() + "\"," +
						" \"prefix\" : \"" + _person.getPrefixString() + "\"," +
						" \"fn\" : \"" + _person.getFirstNameString() + "\"," +
						" \"mn\" : \"" + _person.getMiddleNameString() + "\"," +
						" \"ln\" : \"" + _person.getLastNameString() + "\"," +
						" \"suffix\" : \"" + _person.getSuffixString() + "\"," +
						" \"username\" : \"" + _person.getUsernameString() + "\"," +
						" \"password\" : \"" + _person.getPasswordString() + "\"," +
						" \"addr1\" : \"" + _person.getHomeAddress1String() + "\"," +
						" \"addr2\" : \"" + _person.getHomeAddress2String() + "\"," +
						" \"city\" : \"" + _person.getHomeAddressCityString() + "\"," +
						" \"state\" : \"" + _person.getHomeAddressStateString() + "\"," +
						" \"zip\" : \"" + _person.getHomeAddressZipString() + "\"," +
						" \"phone\" : \"" + _person.getHomePhoneNumberString() + "\"," +
						" \"cell\" : \"" + _person.getCellPhoneNumberString() + "\"," +
						" \"ssn\" : \"" + _person.getSSNString() + "\"," +
						" \"file\" : \"" + _person.getFileNumberString() + "\"," +
						" \"dob\" : \"" + _person.getBirthDateString() + "\"," +
						" \"gender\" : \"" + _person.isMale() + "\"," +
						" \"deceased\" : \"" + _person.isDeceased() + "\"," +
						" \"id\" : \"" + _person.getId() + "\"," +
						" \"n_appt\" : \"" + _person.getNextAppointmentString() + "\"," +
						" \"l_appt\" : \"" + _person.getLastAppointmentString() + "\"," +
						" \"pos_id\" : \"" + _person.getEmployeeNumberString() + "\"," +
						" \"qb_id\" : \"" + _person.getEmail2String() + "\"," +
						" \"sel\" : \"" + _selected + "\"," +
						" \"ft\" : \"" + _person.getGroupUnderCareMemberTypeValue() + "\"," +
						" \"ct\" : \"" + _person.getTitleValueString() + "\"," +
						" \"pt\" : \"" + _person.getPersonTypeString() + "\"," +
						" \"mc\" : \"" + selected_marketing_plan_id + "\"," +
						" \"rc\" : \"" + selected_referred_by_client_id + "\"," +
						" \"rcl\" : \"" + selected_referred_by_client_label + "\"," +
						" \"email\" : \"" + _person.getEmailString() + "\" }";
		return "<person><key>" + _person.getId() + "</key><value><![CDATA[" + value + "]]></value>" + group_under_care_str + "</person>";
    }
	
	private String
	getXML(PaymentPlanInstanceBean _payment_plan_instance)
		throws Exception
	{
		String value = "{\"label\" : \"" + _payment_plan_instance.getLabel() + "\" }";
		return "<payment-plan-instance><key>" + _payment_plan_instance.getId() + "</key><value><![CDATA[" + value + "]]></value></payment-plan-instance>";
	}
    
    public static String
    getDetailedXML(PaymentPlanInstanceBean _payment_plan_instance)
		throws Exception
    {
		String value = "{\"label\" : \"" + _payment_plan_instance.getLabel() + "\"," +
						" \"pool\" : \"" + _payment_plan_instance.isGroupPoolingAllowed() + "\"," +
						" \"start\" : \"" + _payment_plan_instance.getStartDateString() + "\"," +
						" \"prepaid_visits\" : \"" + _payment_plan_instance.getPaymentPlan().getVisits() + "\"," +
						" \"visits_used\" : \"" + _payment_plan_instance.getNumberOfVisitsUsed() + "\"," +
						" \"visits_remaining\" : \"" + _payment_plan_instance.getNumberOfVisitsRemaining() + "\"," +
						" \"visit_charge\" : \"" + _payment_plan_instance.getPerVisitChargeString() + "\"," +
						" \"amount_paid\" : \"" + _payment_plan_instance.getAmountPaidString() + "\"," +
						" \"drop_plan_charge\" : \"" + _payment_plan_instance.getDropChargeAmountString() + "\"," +
						" \"practice_area_id\" : \"" + _payment_plan_instance.getPaymentPlan().getPracticeArea().getValue() + "\"," +
						" \"escrow\" : \"" + _payment_plan_instance.getEscrowAmountString() + "\" }";
		return "<payment-plan-instance><key>" + _payment_plan_instance.getId() + "</key><value><![CDATA[" + value + "]]></value></payment-plan-instance>";
    }
	
	private static void
	refreshLedger(HttpSession _session, PrintWriter _writer, String _parameter, Date _start_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		try
		{
			Date end_date = new Date();

			UKOnlinePersonBean client = null;
			if ((_parameter != null) && !_parameter.equals("undefined") && !_parameter.equals("-1"))
			{
				client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(_parameter));

				// put this person in the session as the selected person
				_session.setAttribute("adminPerson", client);
			}
			else
				client = (UKOnlinePersonBean)_session.getAttribute("adminPerson");

			//UKOnlinePersonBean client = (UKOnlinePersonBean)session.getAttribute("adminPerson");

			// get the purchased plans for the financial tab

			StringBuffer status_buf = new StringBuffer();

			/*
			status_buf.append("<purchased-plans>");
			Iterator purchased_plan_instances_itr = PaymentPlanInstanceBean.getPaymentPlanInstances(client).iterator();
			while (purchased_plan_instances_itr.hasNext())
			{
				PaymentPlanInstanceBean purchased_plan = (PaymentPlanInstanceBean)purchased_plan_instances_itr.next();
				status_buf.append(this.getXML(purchased_plan));
			}
			status_buf.append("</purchased-plans>");
			 * 
			 */

			// get the receive payments

			Vector receive_payments;
			if (_start_date == null)
				receive_payments = ReceivePaymentRet.getReceivePayments(client);
			else
				receive_payments = ReceivePaymentRet.getReceivePayments(client, _start_date, end_date);
			
			System.out.println("receive_payments found >" + receive_payments.size());

			// get the ledger transactions for the ledger tab


			StringBuffer orders_buf = new StringBuffer();


			// get all the printed receipts


			HashMap<ValeoOrderBean,PrintedReceipt> order_to_receipt_hash = new HashMap<ValeoOrderBean,PrintedReceipt>();
			Vector<PrintedReceipt> unmapped_printed_receipt = new Vector<PrintedReceipt>();

			Iterator itr;
			if (_start_date == null)
				itr = PrintedReceipt.getPrintedReceipts(client).iterator();
			else
				itr = PrintedReceipt.getPrintedReceipts(client, _start_date, end_date).iterator();
			while (itr.hasNext())
			{
				PrintedReceipt receipt = (PrintedReceipt)itr.next();
				if (receipt.hasPrimaryOrder())
					order_to_receipt_hash.put(receipt.getPrimaryOrder(), receipt);
				else
					unmapped_printed_receipt.addElement(receipt);
			}





			HashMap<ValeoOrderBean,Vector> order_to_tenders_hash = new HashMap<ValeoOrderBean,Vector>();
			HashMap<ValeoOrderBean,Vector> order_to_amount_hash = new HashMap<ValeoOrderBean,Vector>();
			Vector tenders_with_unmapped_portions = new Vector();
			HashMap<TenderRet,BigDecimal> unmapped_amount_hash = new HashMap<TenderRet,BigDecimal>();

			Vector tenders;
			if (_start_date == null)
				tenders = TenderRet.getTenders(client);
			else
				tenders = TenderRet.getTenders(client, _start_date, end_date);
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
			if (_start_date == null)
				orders_itr = ValeoOrderBean.getOrders(client).iterator();
			else
				orders_itr = ValeoOrderBean.getOrders(client, _start_date, end_date).iterator();

			for (int i = 0; orders_itr.hasNext(); i++)
			{
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
						StringBuffer order_line_buf = new StringBuffer();

						String tender_type_str = "Received " + tender.getType() + " Tender";
						if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
							tender_type_str = "Placed On Account";

						BigDecimal unmapped_amount = unmapped_amount_hash.get(tender);
						//BigDecimal amount = tender.getAmountBigDecimal();
						
						// why show "unmapped amount" here for tender?  doesn't it make more sense to show the actual tender amount???
						
						/*
						 * 
						orderline_value = "{\"code\" : \"\"," +
										" \"amount\" : \"" + unmapped_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
										" \"qty\" : \"\"," +
										" \"prac\" : \"\"," +
										" \"comm\" : \"\"," +
										" \"desc\" : \"" + tender_type_str + "\"}";
						 */

						orderline_value = "{\"code\" : \"\"," +
										" \"amount\" : \"" + tender.getAmountBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
										" \"qty\" : \"\"," +
										" \"prac\" : \"\"," +
										" \"comm\" : \"\"," +
										" \"ist\" : \"true\"," +
										" \"desc\" : \"" + tender_type_str + "\"}";
						order_line_buf.append("<orderline><key>t" + tender.getValue() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");


						String value = "{\"label\" : \"" + tender.getLabel() + "\"," +
												" \"value\" : \"" + tender.getValue() + "\"," +
												" \"date\" : \"" + CUBean.getUserDateString(tender_date) + "\"," +
												" \"paid\" : \"\"," +
												" \"billed\" : \"\"," +
												" \"collect\" : \"\"," +
												" \"status\" : \"\"," +
												" \"balance\" : \"\"}";

						orders_buf.append("<order><key>" + order_obj.getId() + "</key><value><![CDATA[" + value + "]]></value><orderlines>" + order_line_buf.toString() + "</orderlines></order>");


						//tenders_with_unmapped_portions.removeElementAt(0);
						tenders.removeElementAt(0);

						//if (!tenders_with_unmapped_portions.isEmpty())
						if (!tenders.isEmpty())
						{
							//tender = (TenderRet)tenders_with_unmapped_portions.get(0);
							tender = (TenderRet)tenders.get(0);
							tender_date = tender.getTenderDate();
						}
					}
				}

				// are there any receive payments that should be displayed ahead of this order?

				if (!receive_payments.isEmpty())
				{
					// first one should be most current...

					ReceivePaymentRet receivePayment = (ReceivePaymentRet)receive_payments.get(0);

					Date txn_date = receivePayment.getTxnDate();
					while (txn_date.after(order_date) && !receive_payments.isEmpty())
					{
						String orderline_value = null;
						StringBuffer order_line_buf = new StringBuffer();

						String tender_type_str = "Received Payment";

						BigDecimal payment_amount = new BigDecimal(receivePayment.getTotalAmount());
						//BigDecimal amount = tender.getAmountBigDecimal();

						orderline_value = "{\"code\" : \"\"," +
										" \"amount\" : \"" + payment_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
										" \"qty\" : \"\"," +
										" \"prac\" : \"\"," +
										" \"comm\" : \"\"," +
										" \"desc\" : \"" + tender_type_str + "\"}";
						order_line_buf.append("<orderline><key>t" + receivePayment.getValue() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");


						String value = "{\"label\" : \"" + receivePayment.getLabel() + "\"," +
												" \"value\" : \"" + receivePayment.getValue() + "\"," +
												" \"date\" : \"" + CUBean.getUserDateString(txn_date) + "\"," +
												" \"paid\" : \"\"," +
												" \"billed\" : \"\"," +
												" \"collect\" : \"\"," +
												" \"status\" : \"\"," +
												" \"balance\" : \"\"}";

						orders_buf.append("<order><key>" + order_obj.getId() + "</key><value><![CDATA[" + value + "]]></value><orderlines>" + order_line_buf.toString() + "</orderlines></order>");


						receive_payments.removeElementAt(0);

						if (!receive_payments.isEmpty())
						{
							receivePayment = (ReceivePaymentRet)receive_payments.get(0);
							txn_date = receivePayment.getTxnDate();
						}
					}
				}


				try
				{

					String orderline_value = null;
					StringBuffer order_line_buf = new StringBuffer();
					Iterator order_lines_itr = order_obj.getOrders();

					while (order_lines_itr.hasNext())
					{
						CheckoutOrderline order_line = (CheckoutOrderline)order_lines_itr.next();
						CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(order_line.getCheckoutCodeId());
						BigDecimal qty = order_line.getQuantity();
						BigDecimal price = order_line.getPrice();

						String prac_str = "";
						String commission_str = "";
						if (order_line.getPractitionerId() > 0)
						{
							UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(order_line.getPractitionerId());
							prac_str = practitioner.getFirstNameString();
							commission_str = order_line.getCommission().toString();
						}
						
						//.replace('\r', '-').replace('\n', '_')
						
						String desc = code.getDescription().replace('\r', ' ').replace('\n', ' ');
						
						String price_str = price.setScale(2, RoundingMode.HALF_UP).toString();
						String qty_str = qty.setScale(0, RoundingMode.HALF_UP).toString();

						orderline_value = "{\"code\" : \"" + code.getCode() + "\"," +
										" \"amount\" : \"" + price_str + "\"," +
										" \"qty\" : \"" + qty_str + "\"," +
										" \"prac\" : \"" + prac_str + "\"," +
										" \"isc\" : \"" + code.isCommissionable() + "\"," +
										" \"id\" : \"" + order_line.getCheckoutOrderlineId() + "\"," +
										" \"comm\" : \"" + commission_str + "\"," +
										" \"desc\" : \"" + desc + "\"}";
						order_line_buf.append("<orderline><key>" + order_line.getCheckoutOrderlineId() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");

					}

					BigDecimal order_tax = order_obj.getTax();
					if (order_tax.compareTo(BigDecimal.ZERO) != 0)
					{
						// there is some tax on this order.

						orderline_value = "{\"code\" : \"\"," +
										" \"amount\" : \"" + order_tax.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
										" \"qty\" : \"\"," +
										" \"prac\" : \"\"," +
										" \"comm\" : \"\"," +
										" \"desc\" : \"Tax\"}";
						order_line_buf.append("<orderline><key>-1</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");

					}

					orderline_value = "{\"code\" : \"\"," +
									" \"amount\" : \"" + order_obj.getTotalString() + "\"," +
									" \"qty\" : \"\"," +
									" \"prac\" : \"\"," +
									" \"comm\" : \"\"," +
									" \"desc\" : \"Order Total\"}";
					order_line_buf.append("<orderline><key>-1</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");


					// get the Tenders for this order

					Vector tenders_vec = order_to_tenders_hash.get(order_obj);
					if (tenders_vec != null)
					{
						tender_itr = tenders_vec.iterator();
						//tender_itr = TenderRet.getTenders(order_obj).iterator();
						for (int ii = 0; tender_itr.hasNext(); ii++)
						{
							TenderRet tender = (TenderRet)tender_itr.next();
							String tender_type_str = tender.getType() + " Tender (" + tender.getValue() + ")";
							if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
								tender_type_str = "Placed On Account (" + tender.getValue() + ")";

							Vector vec = order_to_amount_hash.get(order_obj);
							BigDecimal amount_allocated_to_order = (BigDecimal)vec.elementAt(ii);

							//BigDecimal amount = tender.getAmountBigDecimal();

							orderline_value = "{\"code\" : \"\"," +
											" \"amount\" : \"" + amount_allocated_to_order.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
											" \"qty\" : \"\"," +
											" \"prac\" : \"\"," +
											" \"comm\" : \"\"," +
											" \"desc\" : \"" + tender_type_str + "\"}";
							order_line_buf.append("<orderline><key>t" + tender.getValue() + "</key><value><![CDATA[" + orderline_value + "]]></value></orderline>");

						}
					}

					String order_status_str = "";
					if (order_obj.isReversed())
						order_status_str = "REVERSED";
					else if (order_obj.isReturn())
						order_status_str = "RETURN";
					else if (order_obj.isLegacy())
						order_status_str = "OLD PAYMENT";
					else if (order_obj.isUpdated())
					{
						order_status_str = "SENT TO QB";
						// sent as?
					}

					String printed_receipt_value = "0";
					PrintedReceipt receipt_for_order = order_to_receipt_hash.get(order_obj);
					if (receipt_for_order != null)
					{
						printed_receipt_value = receipt_for_order.getValue();
					}

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

				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}

			status_buf.append("<ledger total=\"0.00\" client=\"0.00\" balance=\"" + client.getBalanceString() + "\">");
			status_buf.append(orders_buf.toString());
			status_buf.append("</ledger>");

			//System.out.println("<status>" + status_buf.toString() + "</status>");
			_writer.println("<status cl=\"" + client.getLabel() + "\">" + status_buf.toString() + "</status>");
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
