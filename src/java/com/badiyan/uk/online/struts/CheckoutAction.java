/*
 * CheckoutAction.java
 *
 * Created on March 8, 2008, 2:18 PM
 * 
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.PDF.SalesReceiptBuilder;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.servlets.ClientServlet;
import com.valeo.qb.data.InvoiceRet;
import com.valeo.qbms.data.QBMSCreditCardResponse;

import com.valeo.qbpos.data.*;
import com.valonyx.beans.PersonSettingsBean;
import com.valonyx.beans.ShoppingCartBean;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.math.*;

import javax.servlet.http.*;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.torque.*;

import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a client profile.
 *
 * @author Marlo Stueve
 */
public final class
CheckoutAction
	extends Action
{

	public static final String workstation_cookie_name = "valeo.workstation_name";
	public static final String is_primary_cookie_name = "valeo.workstation_primary";

	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CheckoutAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		
		HttpSession session = _request.getSession(false);


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

		try {
			HashMap<String,String> request_hash = new HashMap<String,String>();
			
			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements()) {
				String key = (String)enumx.nextElement();
				String value = _request.getParameter(key);
				System.out.println("putting key>"+ key + " value>" + value);
				request_hash.put(key, value);
			}
			
			UKOnlineLoginBean loginBean = null;
			UKOnlineCompanyBean admin_company = null;
			
			if (session == null) {
				if (_mapping == null) {
					return null; // shrug
				}
				return (_mapping.findForward("session_expired"));
			} else {
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				admin_company = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
			}
			
			CheckoutAction.doCheckout(session, _mapping, workstation_cookie, request_hash, admin_company, (UKOnlinePersonBean)loginBean.getPerson());
			
		} catch (Exception x) {
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			session.setAttribute("error-message", x.getMessage());
			forwardString = "failure";
		}

		// Report any errors we have discovered back to the original form
		/*
		if (!errors.isEmpty())
		{
			saveErrors(_request, errors);
			return _mapping.getInputForward();
		}
		 */

		// Remove the obsolete form bean
		if (_mapping.getAttribute() != null)
		{
            if ("request".equals(_mapping.getScope()))
                _request.removeAttribute(_mapping.getAttribute());
            else
                session.removeAttribute(_mapping.getAttribute());
        }

		// Forward control to the specified success URI
		return (_mapping.findForward(forwardString));
	}
	
	public static String
	doCheckout(HttpSession session, ActionMapping _mapping, Cookie workstation_cookie, HashMap<String,String> _request_hash, UKOnlineCompanyBean admin_company, UKOnlinePersonBean _mod_person) throws Exception {
		
		UKOnlinePersonBean adminPerson = null;
		ValeoOrderBean adminOrder = null;
		SalesReceiptRet sales_receipt = null;

		HashMap gift_card_number_hash = null;
		HashMap gift_cert_number_hash = null;
		HashMap gift_card_hash = null;
		HashMap gift_cert_hash = null;

		// Check the session to see if there's a course in progress...
		//hiddenreverseCheck49tblPrevious
		//printed_receipt.setSubtotal((BigDecimal)session.getAttribute("h_reverse"));
		
		//loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
		//admin_company = (UKOnlineCompanyBean)session.getAttribute("adminCompany");

		// 3/2/16 - getting the checkout person from the session seems potentially problematic.  I think it wise to grab from "co_clientSelect" instead (if available, I guess - should be I'm thinking)
		String co_clientSelectStr = _request_hash.get("co_clientSelect");
		System.out.println("found co_clientSelect >" + co_clientSelectStr);
		if ((co_clientSelectStr != null) && !co_clientSelectStr.isEmpty() && !co_clientSelectStr.equals("0")) {
			adminPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(co_clientSelectStr));
		}
		if (adminPerson == null) {
			System.out.println("getting from session");
			adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson"); //getting from session??  
		}

		if (!adminPerson.isSynced()) {
			throw new IllegalValueException("Unable to checkout " + adminPerson.getLabel() + ".  This client has not been synchronized.");
		}

		/*
			adminOrder = (ValeoOrderBean)session.getAttribute("adminOrder");
			if (adminOrder == null)
			{
				adminOrder = new ValeoOrderBean();
				session.setAttribute("adminOrder", adminOrder);
			}

			sales_receipt = (SalesReceiptRet)session.getAttribute("sales_receipt");
			if (sales_receipt == null)
			{
				sales_receipt = new SalesReceiptRet();
				session.setAttribute("sales_receipt", sales_receipt);
			}
		 */


		System.out.println("_request.getParameter(\"h_reverse\") >" + _request_hash.get("h_reverse"));

		if (_request_hash.get("h_reverse").equals("1")) {
			//Vector<ValeoOrderBean> orders_to_reverse = new Vector<ValeoOrderBean>();

			BigDecimal amount_to_credit_to_client_balance = BigDecimal.ZERO;

			for (int i = 1; _request_hash.get("hiddenreverseCheck" + i + "tblPrevious") != null; i++) {
				// reverseCheck1tblPrevious

				String input_check_str = _request_hash.get("reverseCheck" + i + "tblPrevious");
				if ((input_check_str != null) && input_check_str.equals("on")) {
					ValeoOrderBean order_to_reverse = (ValeoOrderBean) ValeoOrderBean.getOrder(Integer.parseInt(_request_hash.get("hiddenreverseCheck" + i + "tblPrevious")));
					System.out.println("FOUND ORDER TO REVERSE >" + order_to_reverse.getLabel());
					//orders_to_reverse.addElement(order_to_reverse);

					// modify "client owes" balance, if necessary
					// if an "on account" order is being reversed, I need to remove the amount put on account for this order from the owed amount
					Iterator itr = order_to_reverse.getTenders().iterator();
					while (itr.hasNext()) {
						TenderRet tender = (TenderRet) itr.next();
						if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE)) {
							// client owes less money...

							System.out.println("found on account tender for order being reversed >" + tender.getAmountString());

							amount_to_credit_to_client_balance = amount_to_credit_to_client_balance.add(tender.getAmountBigDecimal());
						}
					}

					// put stuff back into inventory (if inventory item)
					itr = order_to_reverse.getOrders();
					while (itr.hasNext()) {
						CheckoutOrderline order_line = (CheckoutOrderline) itr.next();
						CheckoutCode checkout_code_obj = (CheckoutCode) order_line.getCheckoutCode();
						CheckoutCodeBean checkout_code = (CheckoutCodeBean) CheckoutCodeBean.getCheckoutCode(checkout_code_obj.getCheckoutCodeId());
						if (checkout_code.getType() == CheckoutCodeBean.INVENTORY_TYPE) {
							BigDecimal qty = order_line.getQuantity();
							short on_hand_qty = checkout_code.getOnHandQuantity();
							on_hand_qty += qty.shortValue();
							checkout_code.setOnHandQuantity(on_hand_qty);
							checkout_code.save();
						}
					}

					order_to_reverse.reverse(); // this will set the status of the order to "REVERSED"
					order_to_reverse.save();
				}
			}

			if (amount_to_credit_to_client_balance.compareTo(BigDecimal.ZERO) == 1) {
				adminPerson.adjustBalanceDown(amount_to_credit_to_client_balance);
				adminPerson.save();
			}
		} else {

			/*
			String firstname = ((String)PropertyUtils.getSimpleProperty(_form, "firstname")).trim();
			String lastname = ((String)PropertyUtils.getSimpleProperty(_form, "lastname")).trim();
			String gender = (String)PropertyUtils.getSimpleProperty(_form, "gender");
			String group = (String)PropertyUtils.getSimpleProperty(_form, "group");
			Short relationshipSelect = (Short)PropertyUtils.getSimpleProperty(_form, "relationshipSelect");
			
			System.out.println("firstname >" + firstname);
			System.out.println("lastname >" + lastname);
			System.out.println("gender >" + gender);
			System.out.println("group >" + group);
			System.out.println("relationshipSelect >" + relationshipSelect);
			 */
			int max_iterator = 0;
			boolean has_current_order_stuff = false;

			//Enumeration enumx = _request.getParameterNames();
			Iterator itrx = _request_hash.keySet().iterator();
			//while (enumx.hasMoreElements()) {
			while (itrx.hasNext()) {
				//String param_name = (String)enumx.nextElement();
				String param_name = (String)itrx.next();
				System.out.println(param_name + " >" + _request_hash.get(param_name));
				if (param_name.indexOf("inputName") > -1) {
					int iterator = Integer.parseInt(param_name.substring(9, param_name.indexOf("tblCharges")));
					if (iterator > max_iterator) {
						max_iterator = iterator;
					}
					has_current_order_stuff = true;
				}
			}

			System.out.println("max_iterator >" + max_iterator);

			AppointmentBean appointment = null;
			boolean isCheckout = false;
			PaymentPlanInstanceBean apply_to_plan = null;

			if (_request_hash.containsKey("co_apptSelect")) {
				try {
					appointment = AppointmentBean.getAppointment(Integer.parseInt(_request_hash.get("co_apptSelect"))); // rather than getting from the session
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
			if (appointment == null) {
				appointment = (AppointmentBean)session.getAttribute("adminAppointment"); // try this, I guess
			}
			System.out.println("ass. appointment >" + appointment);
			if ((appointment != null) && !appointment.isNew()) {
				//System.out.println("adminPerson >" + adminPerson.getLabel());
				//System.out.println("appointment.getClient() >" + appointment.getClient());
				if (adminPerson.equals(appointment.getClient())) {
					//if (appointment.isCheckedOut())
					//	throw new IllegalValueException(appointment.getLabel() + " is already checked out.");
					isCheckout = true;
				}
			}

			try {
				if (appointment != null) {
					apply_to_plan = adminPerson.getPlan(appointment);
				}
			} catch (ObjectNotFoundException x) {
			}

			Vector<TenderRet> tenders = new Vector<TenderRet>();

			if (Integer.parseInt(_request_hash.get("order_id")) > 0) {
				// I'm editing existing orderlines
				System.out.println("editing existing order");

				/*
				int checkout_orderline_id;
				if (_request.getParameter("orderlineId1tblCharges") != null)
					checkout_orderline_id = Integer.parseInt(_request.getParameter("orderlineId1tblCharges"));
				else
					checkout_orderline_id = Integer.parseInt(_request.getParameter("orderlineId1tblCredits"));
				
				Criteria crit = new Criteria();
				 */
				adminOrder = (ValeoOrderBean) ValeoOrderBean.getOrder(Integer.parseInt(_request_hash.get("order_id")));

				// is there an existing Sales Receipt for this order?
				//try
				//{
				sales_receipt = SalesReceiptRet.getSalesReceipt(admin_company, adminOrder);
				//}
				//catch (ObjectNotFoundException x)
				//{
				//	sales_receipt = new SalesReceiptRet();
				//	sales_receipt.set
				//}

				tenders = TenderRet.getTenders(adminOrder);
			} else {
				adminOrder = new ValeoOrderBean();
				session.setAttribute("adminOrder", adminOrder);

				sales_receipt = new SalesReceiptRet();
				session.setAttribute("sales_receipt", sales_receipt);
			}

			boolean current_charges_paid = true;

			Vector charge_order_lines = new Vector();
			Vector charge_sales_receipt_items = new Vector();

			Vector<ValeoOrderBean> all_orders = new Vector<ValeoOrderBean>();
			Vector<ValeoOrderBean> previous_unpaid_orders = new Vector<ValeoOrderBean>();

			//Vector<InvoiceRet> previous_unpaid_orders_legacy = new Vector<InvoiceRet>();
			session.setAttribute("tenders", tenders);
			session.setAttribute("previous_orders", previous_unpaid_orders);

			//Vector applied_charge_order_lines = new Vector();
			//QBMSCreditCardResponse response = null;
			Vector<QBMSCreditCardResponse> responses = new Vector<QBMSCreditCardResponse>();

			BigDecimal discount_perc_services = new BigDecimal(_request_hash.get("discount_perc_services").equals("") ? "0" : _request_hash.get("discount_perc_services"));
			if ((discount_perc_services.floatValue() > 100f) || (discount_perc_services.floatValue() < 0f)) {
				throw new IllegalValueException("Invalid Discount %");
			}
			BigDecimal discount_perc_products = new BigDecimal(_request_hash.get("discount_perc_products").equals("") ? "0" : _request_hash.get("discount_perc_products"));
			if ((discount_perc_products.floatValue() > 100f) || (discount_perc_products.floatValue() < 0f)) {
				throw new IllegalValueException("Invalid Discount %");
			}
			BigDecimal total_discount_products = BigDecimal.ZERO;
			BigDecimal total_discount_services = BigDecimal.ZERO;

			/*
			BigDecimal shipping = new BigDecimal(_request.getParameter("shipping").equals("") ? "0" : _request.getParameter("shipping"));
			if (shipping.floatValue() < 0f)
				throw new IllegalValueException("Invalid Shipping Amount");
			 */
			Boolean client_taxable = new Boolean(_request_hash.get("client_taxable").equals("1"));

			BigDecimal subtotal = new BigDecimal(_request_hash.get("h_subtotal"));
			BigDecimal tax = new BigDecimal(_request_hash.get("h_tax"));
			BigDecimal total = new BigDecimal(_request_hash.get("h_total"));

			boolean is_overpayment = ((_request_hash.get("over_payment") != null) && _request_hash.get("over_payment").equals("on"));

			session.setAttribute("h_subtotal", subtotal);
			session.setAttribute("h_tax", tax);
			session.setAttribute("h_total", total);

			MathContext mc = new MathContext(2, RoundingMode.HALF_UP);

			BigDecimal previousBalance = new BigDecimal(_request_hash.get("h_previousBalance")).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal charges = new BigDecimal(_request_hash.get("h_charges")).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal credits = new BigDecimal(_request_hash.get("h_credits")).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal owes = new BigDecimal(_request_hash.get("h_owes")).setScale(2, BigDecimal.ROUND_HALF_UP);

			// need to ensure that charges == credits
			System.out.println("charges >" + charges.toString());
			System.out.println("credits >" + credits.toString());

			boolean is_return = (charges.compareTo(CUBean.zero) == -1);
			System.out.println("is_return >" + is_return);

			if (is_return && (charges.compareTo(credits) != 0)) {

				StringBuffer buf = new StringBuffer();
				//enumx = _request.getParameterNames();
				itrx = _request_hash.keySet().iterator();
				//while (enumx.hasMoreElements()) {
				while (itrx.hasNext()) {
					//String param_name = (String)enumx.nextElement();
					String param_name = (String)itrx.next();
					buf.append(param_name + " >" + _request_hash.get(param_name) + "\r\n");
				}

				CUBean.sendEmail("marlo@badiyan.com", "marlo@valonyx.com", admin_company.getLabel() + "REFUND ISSUE (Charges != Credits)", buf.toString());
			}

			int num_orders = 0;
			int num_previous_orders = 0;
			int num_tenders = 0;

			BigDecimal current_charges_taxable = CUBean.zero;

			BigDecimal charges_total = CUBean.zero;
			BigDecimal current_charges_sub_total = CUBean.zero;
			BigDecimal current_charges_tax = CUBean.zero;
			BigDecimal current_charges_total = CUBean.zero;

			HashMap orderline_plan_map = new HashMap(3);
			HashMap practice_area_plan_use_map = new HashMap(3);

			HashMap herbs_to_add = new HashMap(100);

			//if (_request.getParameter("inputName1tblCharges") != null)
			if (has_current_order_stuff) {
				System.out.println("found initial order");
				all_orders.addElement(adminOrder);
				num_orders++;

				//for (int i = 1; _request.getParameter("inputName" + i + "tblCharges") != null; i++)
				for (int i = 1; i <= max_iterator; i++) {
					String charge_amount_str = _request_hash.get("inputName" + i + "tblCharges");
					System.out.println("charge_amount_str >" + charge_amount_str);
					if (charge_amount_str != null) {
						String qty_str = _request_hash.get("qty" + i + "tblCharges");
						BigDecimal qty = new BigDecimal(qty_str);
						BigDecimal charge_amount = new BigDecimal(charge_amount_str);
						System.out.println("charge_amount (" + i + ") >" + charge_amount.toString());
						System.out.println("qty (" + i + ") >" + qty.toString());
						System.out.println("discount_perc_products (" + i + ") >" + discount_perc_products.toString());
						System.out.println("discount_perc_services (" + i + ") >" + discount_perc_services.toString());
						
						CheckoutCodeBean checkout_code_obj_x = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(_request_hash.get("hidden" + i + "tblCharges")));
						
						if (checkout_code_obj_x.getType() == CheckoutCodeBean.INVENTORY_TYPE) {
							BigDecimal discount = charge_amount.multiply(discount_perc_products).divide(CUBean.one_hundred);  // calculating the discount on the charge amount and not the (charge amount * qty) is a problem here
							discount = discount.setScale(3, BigDecimal.ROUND_HALF_DOWN);
							total_discount_products = total_discount_products.add(discount);
							System.out.println("product discount >" + discount.toString());
							charge_amount = charge_amount.subtract(discount);
						} else if (checkout_code_obj_x.getType() == CheckoutCodeBean.PROCEDURE_TYPE) {
							BigDecimal discount = charge_amount.multiply(discount_perc_services).divide(CUBean.one_hundred);  // calculating the discount on the charge amount and not the (charge amount * qty) is a problem here
							discount = discount.setScale(3, BigDecimal.ROUND_HALF_DOWN);
							total_discount_services = total_discount_services.add(discount);
							System.out.println("service discount >" + discount.toString());
							charge_amount = charge_amount.subtract(discount);
						}
						
						Vector codes_to_add = new Vector();
						HashMap child_code_hash = null;

						boolean adding_children = false;
						
						if (checkout_code_obj_x.hasChildren()) {
							System.out.println("has children >true");
							child_code_hash = checkout_code_obj_x.getChildCodes();
							Iterator child_code_itr = child_code_hash.keySet().iterator();
							while (child_code_itr.hasNext()) {
								CheckoutCodeBean child_code = (CheckoutCodeBean) child_code_itr.next();
								System.out.println("child_code found >" + child_code.getLabel());
								codes_to_add.addElement(child_code);
							}

							adding_children = true;
						} else if ((admin_company.getId() == 5)
								&& checkout_code_obj_x.getDepartmentString().equals("Herbs")
								&& qty.intValue() > 4) {

							System.out.println("FOUND HERB THING");

							//"Herbal Mixture - 200ml";
							//"Herbal Mixture - 100ml";
							//CheckoutCodeBean.getCheckoutCodeByDesc(admin_company, "");
							herbs_to_add.put(checkout_code_obj_x, qty);

						} else {
							codes_to_add.addElement(checkout_code_obj_x);
						}

						System.out.println("codes_to_add sizer >" + codes_to_add);
						Iterator codes_to_add_itr = codes_to_add.iterator();
						while (codes_to_add_itr.hasNext()) {
							CheckoutCodeBean checkout_code_obj = (CheckoutCodeBean) codes_to_add_itr.next();

							System.out.println("checkout_code_obj.getType() >" + checkout_code_obj.getType());

							if (checkout_code_obj.getType() == CheckoutCodeBean.GIFT_CARD) {
								if (qty.intValue() != 1) {
									throw new IllegalValueException("Only one Gift Card allowed per line item.");
								}
								String gift_card_number_str = _request_hash.get("hiddenGiftCardNumber" + i + "tblCharges");
								System.out.println("gift_card_number_str >" + gift_card_number_str);

								if (gift_card_number_str == null || gift_card_number_str.length() == 0) {
									throw new IllegalValueException("Please scan or enter a number for the gift card.");
								}

								// is this number already in use?
								try {
									GiftCard.getGiftCard(gift_card_number_str);
									throw new IllegalValueException("Gift Card number already in use: " + gift_card_number_str);
								} catch (ObjectNotFoundException x) {
									// it shouldn't be found
								}

								if (gift_card_number_hash == null) {
									gift_card_number_hash = new HashMap();
								}
								gift_card_number_hash.put("hiddenGiftCardNumber" + i + "tblCharges", gift_card_number_str);
							} else if (checkout_code_obj.getType() == CheckoutCodeBean.GIFT_CERTIFICATE) {
								if (qty.intValue() != 1) {
									throw new IllegalValueException("Only one Gift Certificate allowed per line item.");
								}
								String gift_cert_number_str = _request_hash.get("hiddenGiftCertNumber" + i + "tblCharges");
								System.out.println("gift_cert_number_str >" + gift_cert_number_str);

								if (gift_cert_number_str == null || gift_cert_number_str.length() == 0) {
									throw new IllegalValueException("Please enter a number for the gift certificate.");
								}

								// is this number already in use?
								try {
									GiftCard.getGiftCertificate(gift_cert_number_str);
									throw new IllegalValueException("Gift Certificate number already in use: " + gift_cert_number_str);
								} catch (ObjectNotFoundException x) {
									// it shouldn't be found
								}

								if (gift_cert_number_hash == null) {
									gift_cert_number_hash = new HashMap();
								}
								gift_cert_number_hash.put("hiddenGiftCertNumber" + i + "tblCharges", gift_cert_number_str);
							}

							System.out.println("  %%%charge_amount >" + charge_amount);
							System.out.println("  %%%qty >" + qty);
							BigDecimal sub_total = charge_amount.multiply(qty);
							total_discount_products = total_discount_products.multiply(qty);
							total_discount_services = total_discount_services.multiply(qty);
							System.out.println("  %%%total_discount_products >" + total_discount_products);
							System.out.println("  %%%total_discount_services >" + total_discount_services);
							
							BigDecimal tax_amount = BigDecimal.ZERO;
							if (client_taxable.booleanValue()) {
								tax_amount = checkout_code_obj.getTaxAmount(sub_total); // change this
								System.out.println("TAXX AMOUNTZ >" + tax_amount.toString());
								if (tax_amount.compareTo(BigDecimal.ZERO) == 1) {
									current_charges_taxable = current_charges_taxable.add(sub_total);
								}
							}
							current_charges_tax = current_charges_tax.add(tax_amount);

							String next_charge_amount_str = null;
							for (int ii = (i + 1); ii <= max_iterator; ii++) {
								next_charge_amount_str = _request_hash.get("inputName" + ii + "tblCharges"); // gonna have to go up to max_iterator to ensure that I really don't have another line...
							}
							System.out.println("next_charge_amount_str >" + next_charge_amount_str);
							if (next_charge_amount_str == null) {
								// there's not another item.  I have to make the tax work out if there's a rounding error

								// is there a rounding error?
								System.out.println("current_charges_tax >" + current_charges_tax.toString());
								System.out.println("tax >" + tax.toString());

								if (current_charges_tax.compareTo(tax) != 0) {
									// yes.  there's a rounding error

									System.out.println("rounding error found.  correct");

									// un-apply the incorrect tax amount
									System.out.println("tax_amount >" + tax_amount.toString());
									current_charges_tax = current_charges_tax.subtract(tax_amount);
									System.out.println("current_charges_tax >" + current_charges_tax.toString());
									tax_amount = tax.subtract(current_charges_tax);
									System.out.println("tax_amount(2) >" + tax_amount.toString());
									current_charges_tax = current_charges_tax.add(tax_amount);
									System.out.println("current_charges_tax(2) >" + current_charges_tax.toString());
								}

							}

							BigDecimal total_amount = sub_total.add(tax_amount);

							System.out.println("sub_total >" + sub_total.toString());
							System.out.println("tax_amount >" + tax_amount.toString());
							System.out.println("total_amount >" + total_amount.toString());

							CheckoutOrderline order_line = new CheckoutOrderline();
							if (adding_children) {
								Short child_qty_in_parent = (Short) child_code_hash.get(checkout_code_obj);
								BigDecimal child_qty_in_parent_bd = new BigDecimal(child_qty_in_parent);
								BigDecimal child_qty_total = qty.multiply(child_qty_in_parent_bd);
								BigDecimal child_total_amount = checkout_code_obj.getAmount().multiply(child_qty_total);
								order_line.setTax(BigDecimal.ZERO);
								order_line.setPrice(checkout_code_obj.getAmount());
								order_line.setPricePrecise(checkout_code_obj.getAmount()); // shrug
								order_line.setCheckoutCodeId(checkout_code_obj.getId());
								order_line.setQuantity(child_qty_total);
								order_line.setOrderstatus(ValeoOrderBean.OPEN_ORDER_STATUS);
								order_line.setActualAmount(child_total_amount);
								order_line.setCost(checkout_code_obj.getOrderCost());
							} else {
								order_line.setTax(tax_amount);
								order_line.setPrice(charge_amount);
								order_line.setPricePrecise(charge_amount);
								order_line.setCheckoutCodeId(checkout_code_obj.getId());
								order_line.setQuantity(qty);
								order_line.setOrderstatus(ValeoOrderBean.OPEN_ORDER_STATUS);
								order_line.setActualAmount(total_amount);
								order_line.setCost(checkout_code_obj.getOrderCost());
							}

							if (checkout_code_obj.getType() == CheckoutCodeBean.PLAN_TYPE) {
								System.out.println("PURCHASING PLAN");

								// get the plan associated with this checkout code...
								PaymentPlanBean payment_plan = PaymentPlanBean.getPaymentPlan(checkout_code_obj);

								String key = qty.intValue() + "-" + checkout_code_obj.getId();
								orderline_plan_map.put(key, payment_plan);

							}

							if (checkout_code_obj.isPlanUse()) {
								Integer num_usages = (Integer) practice_area_plan_use_map.get(checkout_code_obj.getPracticeArea());
								if (num_usages == null) {
									num_usages = new Integer(1);
								} else {
									num_usages = new Integer(num_usages.intValue() + 1);
								}
								practice_area_plan_use_map.put(checkout_code_obj.getPracticeArea(), num_usages);
							}

							SalesReceiptItemRet receipt_item = new SalesReceiptItemRet();
							String practitioner_id_str = _request_hash.get("sel" + i + "tblCharges");
							if (!practitioner_id_str.equals("0")) {
								UKOnlinePersonBean associate = (UKOnlinePersonBean) UKOnlinePersonBean.getPerson(Integer.parseInt(practitioner_id_str));
								receipt_item.setAssociate(associate);
								System.out.println("found associate for commission >" + associate.getLabel());
								BigDecimal commission_amount_bd = BigDecimal.ZERO;
								if (adding_children) {
									commission_amount_bd = associate.getCommission(checkout_code_obj);
								} else {
									commission_amount_bd = associate.getCommission(checkout_code_obj, sub_total);
								}
								System.out.println("found commission amount >" + commission_amount_bd.toString());
								receipt_item.setCommission(commission_amount_bd.floatValue());

								order_line.setPractitionerId(associate.getId());
								order_line.setCommission(commission_amount_bd);
							}
							receipt_item.setDesc1(checkout_code_obj.getLabel());
							receipt_item.setItemNumber(checkout_code_obj.getItemNumber());
							receipt_item.setListID(checkout_code_obj.getListID());
							receipt_item.setPrice(charge_amount.floatValue());
							receipt_item.setQty(Float.parseFloat(qty_str));
							//receipt_item.setSalesReceipt(_sales_receipt);
							receipt_item.setTaxAmount(checkout_code_obj.getTaxAmount(charge_amount).floatValue());

							//total_current_charges = total_current_charges.add(charge_amount);
							//total_charges_to_apply_credits_to = total_charges_to_apply_credits_to.add(charge_amount);
							//any_charges_applied = true;
							//applied_charge_order_lines.addElement(order_line);
							charge_order_lines.addElement(order_line);
							charge_sales_receipt_items.addElement(receipt_item);

							current_charges_sub_total = current_charges_sub_total.add(sub_total);

							current_charges_total = current_charges_total.add(total_amount);
							charges_total = charges_total.add(total_amount);
						}
					}
				}

			}

			/*
			BigDecimal ten_percent = new BigDecimal("0.10");
			if (current_charges_tax.compareTo(current_charges_taxable.multiply(ten_percent)) == 1) {
				CUBean.sendEmail("marlo@badiyan.com", "marlo@valonyx.com", admin_company.getLabel() + " SANITY CHECK TAX ISSUE ", "Tax amount of " + current_charges_tax.toString() + " given taxable amount of " + current_charges_taxable.toString() + " fails sanity check.  Please restart Valonyx or call Marlo or something.");
				throw new IllegalValueException("Tax amount of " + current_charges_tax.toString() + " given taxable amount of " + current_charges_taxable.toString() + " fails sanity check.  Sorry Megan.  Please restart Valonyx or call Marlo or something (Marlo has been emailed...).");
			}
			*/
			
			boolean hasNSIPMTender = false;

			//boolean is_credit_card_tender = false;
			BigDecimal actual_payments = CUBean.zero;
			BigDecimal on_account_payments = CUBean.zero;
			
			PersonSettingsBean personSettings = PersonSettingsBean.getPersonSettings(adminPerson, true);

			for (int i = 1; _request_hash.get("method" + i + "tblCredits") != null; i++) {
				String credit_method_str = _request_hash.get("method" + i + "tblCredits");
				System.out.println("method" + i + "tblCredits >" + credit_method_str);
				if (credit_method_str != null) {
					// tendered1tblCredits
					// change1tblCredits
					// checkNum1tblCredits

					BigDecimal tendered_amount = new BigDecimal(_request_hash.get("tendered" + i + "tblCredits"));
					//if (tendered_amount.compareTo(CUBean.zero) != 1)
					//	throw new IllegalValueException("Invalid payment found.  Please correct the " + credit_method_str + " payment.");

					BigDecimal change_amount = new BigDecimal(_request_hash.get("change" + i + "tblCredits"));
					String check_num = _request_hash.get("checkNum" + i + "tblCredits");

					TenderRet tender = new TenderRet();
					tender.setCompany(admin_company);
					tender.setClient(adminPerson);

					if (credit_method_str.equals("Credit Card")) {
						String responseStr = _request_hash.get("response" + i + "tblCredits");
						if (responseStr != null) {
							QBMSCreditCardResponse response = QBMSCreditCardResponse.getResponse(Integer.parseInt(responseStr));
							response.setTender(tender);
							responses.addElement(response);

							tender.setCreditCardType(response.getCardType());
						}

						//if (is_credit_card_tender)
						//	throw new IllegalValueException("Multiple credit card payments not allowed.");
						tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE);

						// find the QBMSCreditCardResponse that belongs to this tender.  there needs to be one
						//response = (QBMSCreditCardResponse)session.getAttribute("charge_response");
						/*
						responses = (Vector<QBMSCreditCardResponse>)session.getAttribute("charge_response");
						if ((responses == null) || (responses.size() > 0))
							throw new IllegalValueException("Authorization not found for credit card tender.");

						Iterator responses_itr = responses.iterator();
						boolean response_found = false;
						while (responses_itr.hasNext() && !response_found)
						{
							QBMSCreditCardResponse response = (QBMSCreditCardResponse)responses_itr.next();
							if (tendered_amount.compareTo(response.getChargeAmount()))
						}
						if (!response_found)
							throw new IllegalValueException("Authorization not found for credit card tender.");
						 */
						//is_credit_card_tender = true;
					} else if (credit_method_str.equals("Check")) {
						tender.setType(TenderRet.CHECK_TENDER_TYPE);
					} else if (credit_method_str.equals("Cash")) {
						tender.setType(TenderRet.CASH_TENDER_TYPE);
					} else if (credit_method_str.equals("Account")) {
						if (is_overpayment) {
							throw new IllegalValueException("You cannot use the account payment method to make a payment on account.");
						}

						tender.setType(TenderRet.ACCOUNT_TENDER_TYPE);
						current_charges_paid = false; // I guess if there's any "account" credits, that will indicate that the current order is not gonna get fully paid
					} else if (credit_method_str.equals("NSIPM")) {
						
						if (!personSettings.isNSIPMClient()) {
							throw new IllegalValueException(adminPerson.getLabel() + " is not an NSIPM client.");
						}
						if (is_overpayment) {
							throw new IllegalValueException("You cannot use the NSIPM payment method to make a payment on account.");
						}

						tender.setType(TenderRet.NSIPM_TENDER_TYPE);
						current_charges_paid = false; // I guess if there's any "nsipm" credits, that will indicate that the current order is not gonna get fully paid
						hasNSIPMTender = true;
					} else if (credit_method_str.equals("Gift Cert")) {
						// get the gift cert number
						String gift_cert_num = _request_hash.get("hiddenGiftCertNumber" + i + "tblCredits");
						// find the corresponding gift card
						GiftCard gift_cert = GiftCard.getGiftCertificate(gift_cert_num);
						// can the gift card actually cover the amount tendered?
						if (gift_cert.getBalance().compareTo(tendered_amount) == -1) {
							throw new IllegalValueException("This Gift Certificate has a balance of " + gift_cert.getBalanceString());
						}

						if (gift_cert_hash == null) {
							gift_cert_hash = new HashMap(3);
						}
						gift_cert_hash.put(tender, gift_cert);

						tender.setType(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE);
						tender.setGiftCertCardNumber(gift_cert_num);
					} else if (credit_method_str.equals("Gift Card")) {
						// get the gift card number
						String gift_card_num = _request_hash.get("hiddenGiftCardNumber" + i + "tblCredits");
						// find the corresponding gift card
						GiftCard gift_card = GiftCard.getGiftCard(gift_card_num);
						// can the gift card actually cover the amount tendered?
						if (gift_card.getBalance().compareTo(tendered_amount) == -1) {
							throw new IllegalValueException("This Gift Card has a balance of " + gift_card.getBalanceString());
						}

						if (gift_card_hash == null) {
							gift_card_hash = new HashMap(3);
						}
						gift_card_hash.put(tender, gift_card);

						tender.setType(TenderRet.GIFT_CARD_TENDER_TYPE);
						tender.setGiftCertCardNumber(gift_card_num);
					} else if (credit_method_str.equals("Visa")) {
						tender.setCreditCardType("Visa");
						tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE);
					} else if (credit_method_str.equals("Mastercard")) {
						tender.setCreditCardType("Mastercard");
						tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE);
					} else if (credit_method_str.equals("Discover")) {
						tender.setCreditCardType("Discover");
						tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE);
					}

					if (credit_method_str.equals("Account") || credit_method_str.equals("NSIPM")) {
						// commented out 1/30/11 - I need to be able to treat this as a "payment on account"
						//if (is_return)
						//	throw new IllegalValueException("Account payment not allowed for return.");

						on_account_payments = on_account_payments.add(tendered_amount);
					} else {
						actual_payments = actual_payments.add(tendered_amount.subtract(change_amount));
					}

					tender.setAmount(tendered_amount.floatValue());
					if (check_num != null) {
						tender.setCheckNumber(Integer.parseInt(check_num));
					}

					tender.setChangeAmount(change_amount.floatValue());
					//tender.setOrder(adminOrder); // do after save
					// tender.setSalesReceipt(_sales_receipt); // do after save

					tenders.addElement(tender);

					//has_credits = true;
					num_tenders++;
				}
			}
			
			if (personSettings.isNSIPMClient()) {
				BigDecimal coverage = personSettings.getNSIPMCoveragePercentage();
				if ((coverage != null) && coverage.compareTo(BigDecimal.ZERO) > 0) {
					if (!hasNSIPMTender) {
						
						throw new IllegalValueException("Please specify an NSIPM tender for " + adminPerson.getLabel() + ".  " + CheckoutAction.getStringFromBigDecimal(coverage) + "% of the charges should be billed to NSIPM.");
					}
				}
			}

			//hiddeninputCheck8tblPrevious
			BigDecimal previous_order_charges = CUBean.zero;

			//for (int i = 1; _request.getParameter("hiddeninputCheck" + i + "tblPrevious") != null; i++)
			for (int i = 1; i < 100; i++) {
				// inputCheck1tblPrevious

				String input_check_str = _request_hash.get("inputCheck" + i + "tblPrevious");
				if ((input_check_str != null) && input_check_str.equals("on")) {
					String legacy_str = _request_hash.get("hiddenlegacyInvoice" + i + "tblPrevious");

					if (legacy_str == null) {
						ValeoOrderBean previous_order = (ValeoOrderBean) ValeoOrderBean.getOrder(Integer.parseInt(_request_hash.get("hiddeninputCheck" + i + "tblPrevious")));
						previous_order_charges = previous_order_charges.add(previous_order.getBalance());
						charges_total = charges_total.add(previous_order.getBalance());
						System.out.println("previous_order >" + previous_order.getLabel());
						previous_unpaid_orders.addElement(previous_order);
						all_orders.addElement(previous_order);
						num_orders++;
						num_previous_orders++;
					} else {
						InvoiceRet previous_invoice = InvoiceRet.getInvoiceForTxnNumber(admin_company, Integer.parseInt(legacy_str));
						//getOrder(CompanyBean _company, String _txn_id)

						BigDecimal balance_remaining_bd = new BigDecimal(previous_invoice.getBalanceRemaining()).setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal subtotal_bd = new BigDecimal(previous_invoice.getSubtotal()).setScale(2, BigDecimal.ROUND_HALF_UP);
						BigDecimal tax_bd = new BigDecimal(previous_invoice.getTaxAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);

						previous_order_charges = previous_order_charges.add(balance_remaining_bd);
						charges_total = charges_total.add(balance_remaining_bd);
						System.out.println("previous_invoice >" + previous_invoice.getLabel());

						//previous_unpaid_orders_legacy.addElement(previous_invoice);
						// create an order out of this?
						// I will need to make sure that this order does not get turned into a sales receipt or
						// invoice and communicated back to QuickBooks since the invoice for this already exists in QB
						ValeoOrderBean previous_order = new ValeoOrderBean();
						//adminOrder.setOrders(charge_order_lines);
						previous_order.setCompany(admin_company);
						previous_order.setCreatePerson(_mod_person);
						previous_order.setPerson(adminPerson);
						if (previous_order.isNew()) {
							previous_order.setOrderDate(new Date());
						}
						// basically, as long as there are no payments of "on account", at this point, this order should be flagged as "paid"
						if (current_charges_paid) {
							//adminOrder.setStatus(ValeoOrderBean.CLOSED_ORDER_STATUS);
							previous_order.setIsPaid(true);
							previous_order.setBalance(CUBean.zero);
						}
						//else
						//	adminOrder.setStatus(ValeoOrderBean.OPEN_ORDER_STATUS);

						previous_order.setMemo("Legacy payment for invoice Txn ID " + previous_invoice.getTxnID());
						previous_order.setSubtotal(subtotal_bd);
						previous_order.setTax(tax_bd);
						previous_order.setTotal(balance_remaining_bd);
						previous_order.setBalance(balance_remaining_bd);
						previous_order.setDiscountAmount(BigDecimal.ZERO);
						previous_order.setIsReturn(false);
						previous_order.setStatus(ValeoOrderBean.LEGACY_ORDER_STATUS);

						previous_order.save();

						previous_unpaid_orders.addElement(previous_order);
						all_orders.addElement(previous_order);

						num_orders++;
						num_previous_orders++;

					}
				}
			}

			/*
			if (previous_unpaid_orders_legacy.size() > 0)
			{
				if (num_orders > 0 || num_previous_orders > 0)
					throw new IllegalValueException("Unable to take payment on order from previous system along with current orders.  Please run payment from previous system separately.");
				
				doLegacyStuff(tenders, gift_card_hash, gift_cert_hash, workstation_cookie.getValue());
				doPrintedReceipt(adminPerson, session, total_discount, discount_perc, true);
				
				// Remove the obsolete form bean
				if (_mapping.getAttribute() != null)
				{
					if ("request".equals(_mapping.getScope()))
						_request.removeAttribute(_mapping.getAttribute());
					else
						session.removeAttribute(_mapping.getAttribute());
				}

				// Forward control to the specified success URI
				return (_mapping.findForward(forwardString));
			}
			 *
			 */
			String memo_str = _request_hash.get("memo");
			ValeoOrderBean primary_order = (ValeoOrderBean) session.getAttribute("adminOrder");

			session.setAttribute("h_total_discount_products", total_discount_products);
			session.setAttribute("h_total_discount_services", total_discount_services);
			session.setAttribute("h_discount_percentage_products", discount_perc_products);
			session.setAttribute("h_discount_percentage_services", discount_perc_services);
			session.setAttribute("h_shipping", BigDecimal.ZERO);

			Vector<TenderRet> tenders_vec = (Vector<TenderRet>) session.getAttribute("tenders");
			Vector<ValeoOrderBean> previous_orders = (Vector<ValeoOrderBean>) session.getAttribute("previous_orders");
			BigDecimal h_subtotal = (BigDecimal) session.getAttribute("h_subtotal");
			BigDecimal h_tax = (BigDecimal) session.getAttribute("h_tax");
			BigDecimal h_total = (BigDecimal) session.getAttribute("h_total");

			String sales_receipt_url = CheckoutAction.doCheckoutStuff(admin_company, _mod_person, adminPerson, adminOrder, appointment,
					charges_total, charges, credits, num_orders, num_previous_orders, is_overpayment, num_tenders, current_charges_paid, is_return, isCheckout,
					actual_payments, on_account_payments, current_charges_total, current_charges_sub_total, current_charges_tax, previous_order_charges, total_discount_products, total_discount_services,
					charge_order_lines, orderline_plan_map, tenders, all_orders, previous_unpaid_orders, gift_card_hash, gift_cert_hash, responses,
					memo_str, workstation_cookie.getValue(), primary_order, gift_card_number_hash, gift_cert_number_hash, tenders_vec, previous_orders,
					h_subtotal, h_tax, h_total, discount_perc_products, discount_perc_services, practice_area_plan_use_map, previousBalance);
			
			return sales_receipt_url;

			/*
			if (checkout_code_plan_use_map.size() > 0)
			{

				if ((plan_visits_used > 0) && (apply_to_plan != null))
				{
					apply_to_plan.useVisit();
					apply_to_plan.save();
				}
			}
			 *
			 */
			// if the person is paying for something that was previously placed on account, I need to adjust also
			//herbs_to_add
			//this.doHerbSpreadsheet(admin_company, adminPerson, herbs_to_add);
		}
		
		return "";
	}
	
	private static String
	doCheckoutStuff(UKOnlineCompanyBean admin_company, UKOnlinePersonBean mod_person, UKOnlinePersonBean adminPerson, ValeoOrderBean adminOrder, AppointmentBean appointment,
				BigDecimal charges_total, BigDecimal charges, BigDecimal credits, int num_orders, int num_previous_orders, boolean is_overpayment, int num_tenders, boolean current_charges_paid, boolean is_return, boolean isCheckout,
				BigDecimal actual_payments, BigDecimal on_account_payments, BigDecimal current_charges_total, BigDecimal current_charges_sub_total, BigDecimal current_charges_tax, BigDecimal previous_order_charges, BigDecimal total_discount_products, BigDecimal total_discount_services,
				Vector charge_order_lines, HashMap orderline_plan_map, Vector<TenderRet> tenders, Vector<ValeoOrderBean> all_orders, Vector<ValeoOrderBean> previous_unpaid_orders, HashMap gift_card_hash, HashMap gift_cert_hash, Vector<QBMSCreditCardResponse> responses,
				String memo_str, String cookie_workstation_str, ValeoOrderBean primary_order, HashMap gift_card_number_hash, HashMap gift_cert_number_hash, Vector<TenderRet> tenders_vec, Vector<ValeoOrderBean> previous_orders,
				BigDecimal h_subtotal, BigDecimal h_tax, BigDecimal h_total, BigDecimal discount_perc_products, BigDecimal discount_perc_services, HashMap practice_area_plan_use_map, BigDecimal _previous_balance) throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		
		HashMap orderline_paid_map = new HashMap(3);
		HashMap orderline_amount_paid_map = new HashMap(3);
		HashMap orderline_map = new HashMap(3);
		
		String sales_receipt_url = null;

		charges_total = charges_total.setScale(2, BigDecimal.ROUND_HALF_UP);
		charges = charges.setScale(2, BigDecimal.ROUND_HALF_UP);

		System.out.println("..charges_total >" + charges_total.toString());
		System.out.println("..charges >" + charges.toString());
		System.out.println("..current_charges_total >" + current_charges_total.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

		if (charges_total.compareTo(charges) != 0)
			throw new IllegalValueException("Charge totals do not match. (" + charges_total.toString() + " != " + charges.toString() + ").");

		boolean there_is_a_current_order = (num_orders > num_previous_orders);

		// are there any orders?

		if (!is_overpayment && (num_orders == 0))
			throw new IllegalValueException("No current charges or previous orders entered.");

		//boolean allow_processing_with_no_payment_entered
		if (num_tenders == 0)
		{
			// allow no tenders if the total charges yield no value...

			if (charges.compareTo(BigDecimal.ZERO) != 0)
				throw new IllegalValueException("No payments entered.");
		}


		// I need to allow a credit to be applied to a previous order even if the previous order is not being fully paid...

		if (!is_overpayment)
		{
			if (charges.compareTo(credits) != 0)
			{
				if (num_previous_orders == 0)
				{
					// if there are no previous orders, then the total charges needs to match the total credits (including on account stuff)

					throw new IllegalValueException("Please specify payments to match the charges of " + charges.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				}
				else
				{
					// the charges and the credits don't match and there are old orders in the mix.  determine
				}
			}
			else
			{
			}
		}


		// since the previous orders can't be placed on account...

		BigDecimal total_payments = actual_payments.add(on_account_payments);

		current_charges_total = current_charges_total.setScale(2, BigDecimal.ROUND_HALF_UP);

		System.out.println("actual_payments >" + actual_payments.toString());
		System.out.println("total_payments >" + total_payments.toString());
		System.out.println("previous_order_charges >" + previous_order_charges.toString());
		System.out.println("current_charges_total >" + current_charges_total.toString());


		// this matches the TXN_ID of the corresponding sales receipt.  this can't be obtained until after the QB sync
		// may have to query separately after the add request, or the txn id may be returned as a result of the add in some kind of a response.  need to test
		//adminOrder.setAuthorizationCode("");

		if (charge_order_lines.size() > 0)
		{
			adminOrder.setOrders(charge_order_lines);
			adminOrder.setCompany(admin_company);
			adminOrder.setCreatePerson(mod_person);
			adminOrder.setPerson(adminPerson);
			System.out.println("setting person for order >" + adminPerson.getLabel());
			if (adminOrder.isNew())
				adminOrder.setOrderDate(new Date());
			// basically, as long as there are no payments of "on account", at this point, this order should be flagged as "paid"
			if (current_charges_paid)
			{
				//adminOrder.setStatus(ValeoOrderBean.CLOSED_ORDER_STATUS);
				adminOrder.setIsPaid(true);
				adminOrder.setBalance(CUBean.zero);
			}
			//else
			//	adminOrder.setStatus(ValeoOrderBean.OPEN_ORDER_STATUS);

			adminOrder.setMemo(memo_str);
			adminOrder.setSubtotal(current_charges_sub_total);
			adminOrder.setTax(current_charges_tax);
			adminOrder.setTotal(current_charges_total);
			adminOrder.setDiscountAmount(total_discount_products.add(total_discount_services));
			adminOrder.setIsReturn(is_return);
			adminOrder.setStatus(is_return ? ValeoOrderBean.RETURN_RECEIPT_ORDER_STATUS : ValeoOrderBean.SALES_RECEIPT_ORDER_STATUS);
			//adminOrder.save();

			if (!is_return)
			{
				// do these orderlines contain a plan purchase?

				Iterator charge_order_lines_itr = charge_order_lines.iterator();
				while (charge_order_lines_itr.hasNext())
				{
					CheckoutOrderline order_line = (CheckoutOrderline)charge_order_lines_itr.next();
					String key = order_line.getQuantity().intValue() + "-" + order_line.getCheckoutCodeId();
					PaymentPlanBean payment_plan = (PaymentPlanBean)orderline_plan_map.get(key);
					if (payment_plan != null)
					{
						if (!payment_plan.areMonthlyPaymentsAllowed() && !current_charges_paid)
							throw new IllegalValueException(payment_plan.getLabel() + " can not be placed on account.  Monthly payments are not allowed for this plan.");

						System.out.println("PUTTING PAID MAP " + order_line);
						orderline_paid_map.put(key, new Boolean(current_charges_paid));
						orderline_amount_paid_map.put(key, current_charges_paid ? order_line.getActualAmount() : BigDecimal.ZERO);
						orderline_map.put(key, order_line);
					}
				}
			}


		}


		HashMap<ValeoOrderBean,HashMap> order_to_tender_amount_hash = new HashMap<ValeoOrderBean,HashMap>(3);


		HashMap<ValeoOrderBean,Vector> current_order_to_account_tenders = new HashMap<ValeoOrderBean,Vector>(3);
		HashMap<ValeoOrderBean,Vector> current_order_to_non_account_tenders = new HashMap<ValeoOrderBean,Vector>(3);

		HashMap<TenderRet,ValeoOrderBean> tender_to_order_hash = new HashMap<TenderRet,ValeoOrderBean>(3);
		//HashMap<ValeoOrderBean,TenderRet> order_to_tender_hash = new HashMap<ValeoOrderBean,TenderRet>(3);
		HashMap<TenderRet,Vector> tender_to_orders_hash = new HashMap<TenderRet,Vector>(3);
		HashMap<ValeoOrderBean,Vector> order_to_tenders_hash = new HashMap<ValeoOrderBean,Vector>(3);

		HashMap<TenderRet,Vector> tenders_to_allocate_to_remaining_previous_orders = new HashMap<TenderRet,Vector>(3);
		HashMap<TenderRet,BigDecimal> tender_remaining_balance_to_allocate = new HashMap<TenderRet,BigDecimal>(3);
		HashMap<ValeoOrderBean,Vector> previous_orders_to_allocate_to_remaining_tenders = new HashMap<ValeoOrderBean,Vector>(3);
		HashMap<ValeoOrderBean,BigDecimal> order_remaining_balance_to_cover = new HashMap<ValeoOrderBean,BigDecimal>(3);

		Vector tenders_to_allocate_to_remaining_orders = new Vector();
		Vector remaining_orders = new Vector();


		if (is_return)
		{
			if (num_previous_orders > 0)
				throw new IllegalValueException("Open orders cannot be applied when doing a return.");
			if (num_tenders > 1)
				throw new IllegalValueException("Multiple return methods not allowed.");
			if (credits.compareTo(charges) != 0)
				throw new IllegalValueException("Refund amount must match the value of the items being returned.");


			// there will be no previous orders and only one "tender" at this point
			// the current order should contain items (in negative quantities) that are to be returned

			// the only return scenario.  the customer has one current (or previous) order that they're paying for with one tender
			System.out.println("the only return scenario.  the customer has one current (or previous) order that they're paying for with one tender");
			TenderRet tender = (TenderRet)tenders.get(0);

			if (!(tender.getType().equals(TenderRet.CASH_TENDER_TYPE) || tender.getType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE) || tender.getType().equals(TenderRet.CHECK_TENDER_TYPE) || tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE)))
				throw new IllegalValueException("Invalid return method.");

			ValeoOrderBean order = (ValeoOrderBean)all_orders.get(0);
			tender_to_order_hash.put(tender, order);
			//order_to_tender_hash.put(order, tender);

			order_remaining_balance_to_cover.put(order, charges.subtract(credits));



			HashMap tender_amount_hash = (HashMap)order_to_tender_amount_hash.get(order);
			if (tender_amount_hash == null)
			{
				tender_amount_hash = new HashMap(3);
				order_to_tender_amount_hash.put(order, tender_amount_hash);
			}
			tender_amount_hash.put(tender, tender.getAmountBigDecimal());




			order.setBalance(CUBean.zero);
			order.invalidate();
			//order.setWorkstationName(workstation_cookie.getValue());
			order.setWorkstationName(cookie_workstation_str);
			order.save();
		}
		else
		{
			if (!is_overpayment && (total_payments.compareTo(charges_total) == 1))
				throw new IllegalValueException("Payments entered exceed selected charges.");

			if ((num_previous_orders > 0) && (actual_payments.compareTo(CUBean.zero) == 0))
				throw new IllegalValueException("Open orders cannot be placed on account.");

			// ensure that the current charge is being completely covered

			if (total_payments.compareTo(current_charges_total) == -1)
				throw new IllegalValueException("Please specify payments to match the current charges of " + current_charges_total.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

			if ((num_previous_orders > 0) && (total_payments.compareTo(current_charges_total) != 1))
			{
				// if there are previous orders, the total payments must cover more than just the current order...
				throw new IllegalValueException("Open orders cannot be placed on account.");
			}


			//if (actual_payments.compareTo(previous_order_charges) == -1)
			//	throw new IllegalValueException("Open orders cannot be placed on account.");

			Vector consumed_orders = new Vector();
			Vector consumed_tenders = new Vector();

			// at this point we know that total charges equals total credits and that any previous orders are being tendered for real

			// the most common case will most likely be one tender applied to all current charges and any selected previous orders

			if (on_account_payments.compareTo(CUBean.zero) == 1)
			{
				if (!there_is_a_current_order)
					throw new IllegalValueException("Open orders cannot be placed on account.");
			}

			System.out.println("on_account_payments >" + on_account_payments.toString());
			BigDecimal current_charges_not_covered_by_on_account_payments = current_charges_total.subtract(on_account_payments);
			System.out.println("current_charges_not_covered_by_on_account_payments >" + current_charges_not_covered_by_on_account_payments.toString());
			if (current_charges_not_covered_by_on_account_payments.compareTo(CUBean.zero) == -1)
				throw new IllegalValueException("Open orders cannot be placed on account.");
			BigDecimal actual_payments_to_apply_to_current_order = current_charges_total.subtract(current_charges_total.subtract(current_charges_not_covered_by_on_account_payments));

			if (actual_payments.compareTo(actual_payments_to_apply_to_current_order) == -1)
				throw new IllegalValueException("actual payments unable to cover actual payments needed for current order");

			//BigDecimal actual_payments_to_apply_to_current_order = actual_payments.subtract(current_charges_not_covered_by_on_account_payments); // this is wrong
			System.out.println("actual_payments_to_apply_to_current_order >" + actual_payments_to_apply_to_current_order.toString());
			BigDecimal actual_payments_to_apply_to_previous_orders = actual_payments.subtract(actual_payments_to_apply_to_current_order);
			System.out.println("actual_payments_to_apply_to_previous_orders >" + actual_payments_to_apply_to_previous_orders.toString());

			//BigDecimal actual_payments_to_apply_to_current_order = actual_payments.subtract(previous_order_charges);


			// don't allow multiple orders if the tenders don't cover even one...

			int num_orders_covered = 0;
			if (num_previous_orders > 0)
			{
				BigDecimal order_amount_to_be_covered = CUBean.zero;
				Iterator itr = previous_unpaid_orders.iterator();
				while (itr.hasNext())
				{
					ValeoOrderBean previous_order = (ValeoOrderBean)itr.next();
					order_amount_to_be_covered = order_amount_to_be_covered.add(previous_order.getBalance());
					if (order_amount_to_be_covered.compareTo(actual_payments_to_apply_to_previous_orders) != 1)
						num_orders_covered++;
				}
				//
			}
			if (num_previous_orders > (num_orders_covered + 1))
				throw new IllegalValueException("The payments specified are not sufficient to apply to the selected open orders.");


			// let's break down a few scenarios

			if ((num_tenders == 1) && (num_orders == 1))
			{
				// the most common scenario.  the customer has one current (or previous) order that they're paying for with one tender
				System.out.println("the most common scenario.  the customer has one current (or previous) order that they're paying for with one tender");
				TenderRet tender = (TenderRet)tenders.get(0);
				ValeoOrderBean order = (ValeoOrderBean)all_orders.get(0);
				tender_to_order_hash.put(tender, order);
				//order_to_tender_hash.put(order, tender);

				BigDecimal overpayment_amount = credits.subtract(charges);
				System.out.println("overpayment_amount >" + overpayment_amount.toString());

				if (overpayment_amount.compareTo(BigDecimal.ZERO) == 1) // has overpayment amount
					order_remaining_balance_to_cover.put(order, BigDecimal.ZERO);
				else
					order_remaining_balance_to_cover.put(order, charges.subtract(credits));

				HashMap tender_amount_hash = (HashMap)order_to_tender_amount_hash.get(order);
				if (tender_amount_hash == null)
				{
					tender_amount_hash = new HashMap(3);
					order_to_tender_amount_hash.put(order, tender_amount_hash);
				}

				if (tender.getAmountBigDecimal().compareTo(charges) == 1) // tender exceeds charges (overpayment)
					tender_amount_hash.put(tender, charges);
				else // tender does not exceed charges
					tender_amount_hash.put(tender, tender.getAmountBigDecimal());
			}
			else if ((num_tenders == 1) && (num_orders > 1))
			{
				// a single tender is covering multiple orders
				System.out.println("a single tender is covering multiple orders");

				if (is_overpayment)
					throw new IllegalValueException("Overpayment not alowed for multiple orders yet.");

				TenderRet tender = (TenderRet)tenders.get(0);
				tender_to_orders_hash.put(tender, all_orders);

				//if (num_orders > num_previous_orders)
				//	order_remaining_balance_to_cover.put(adminOrder, tax);

				//actual_payments_to_apply_to_previous_orders

				// loop the orders and distribute actual payments to apply to previous orders

				Iterator itr = previous_unpaid_orders.iterator();
				while (itr.hasNext() && (actual_payments_to_apply_to_previous_orders.compareTo(CUBean.zero) == 1))
				{
					ValeoOrderBean previous_unpaid_order = (ValeoOrderBean)itr.next();

					HashMap tender_amount_hash = (HashMap)order_to_tender_amount_hash.get(previous_unpaid_order);
					if (tender_amount_hash == null)
					{
						tender_amount_hash = new HashMap(3);
						order_to_tender_amount_hash.put(previous_unpaid_order, tender_amount_hash);
					}

					System.out.println();

					BigDecimal amount_to_apply_to_this_order = actual_payments_to_apply_to_previous_orders.subtract(previous_unpaid_order.getBalance());
					BigDecimal amount_applied = CUBean.zero;
					if (amount_to_apply_to_this_order.compareTo(CUBean.zero) == 1)
					{
						// actual payments exceeds this order.  cover completely
						order_remaining_balance_to_cover.put(previous_unpaid_order, CUBean.zero);
						amount_applied = previous_unpaid_order.getBalance();
					}
					else
					{
						// actual payments to apply cannot cover this order completely. cover partially
						order_remaining_balance_to_cover.put(previous_unpaid_order, previous_unpaid_order.getBalance().subtract(actual_payments_to_apply_to_previous_orders));
						amount_applied = actual_payments_to_apply_to_previous_orders;
					}

					System.out.println("amount applied to " + previous_unpaid_order.getId() + " >" + amount_applied.toString());
					actual_payments_to_apply_to_previous_orders = actual_payments_to_apply_to_previous_orders.subtract(amount_applied);

					tender_amount_hash.put(tender, amount_applied);
				}

				System.out.println("there_is_a_current_order >" + there_is_a_current_order);
				if (there_is_a_current_order)
				{
					// are there any actual payments remaining for the current order?

					HashMap tender_amount_hash = (HashMap)order_to_tender_amount_hash.get(adminOrder);
					if (tender_amount_hash == null)
					{
						tender_amount_hash = new HashMap(3);
						order_to_tender_amount_hash.put(adminOrder, tender_amount_hash);
					}
					System.out.println("amount to apply to " + tender.getValue() + " >" + actual_payments_to_apply_to_current_order.toString());
					tender_amount_hash.put(tender, actual_payments_to_apply_to_current_order);
				}

			}
			else if ((num_tenders > 1) && (num_orders == 1))
			{
				// multiple tenders are covering a single order
				System.out.println("multiple tenders are covering a single order");


				if (is_overpayment)
					throw new IllegalValueException("Overpayment not alowed for multiple tenders yet.");

				ValeoOrderBean order = (ValeoOrderBean)all_orders.get(0);
				order_to_tenders_hash.put(order, tenders);

				order_remaining_balance_to_cover.put(order, charges.subtract(credits));

				HashMap tender_amount_hash = (HashMap)order_to_tender_amount_hash.get(order);
				if (tender_amount_hash == null)
				{
					tender_amount_hash = new HashMap(3);
					order_to_tender_amount_hash.put(order, tender_amount_hash);
				}
				Iterator itr = tenders.iterator();
				while (itr.hasNext())
				{
					TenderRet tender = (TenderRet)itr.next();
					tender_amount_hash.put(tender, tender.getAmountBigDecimal());
				}
			}
			else
			{
				// there are multiple tenders and multiple orders
				System.out.println("");
				System.out.println("there are multiple tenders and multiple orders");

				Iterator tender_itr;



				if (there_is_a_current_order)
				{
					System.out.println("there is a current order");

					HashMap tender_amount_hash = (HashMap)order_to_tender_amount_hash.get(adminOrder);
					if (tender_amount_hash == null)
					{
						tender_amount_hash = new HashMap(3);
						order_to_tender_amount_hash.put(adminOrder, tender_amount_hash);
					}

					// apply all "on account" tenders to the current order

					System.out.println("current order found that is not consumed >" + current_charges_total.toString());

					BigDecimal order_amount_to_cover = current_charges_total;
					BigDecimal tender_amount_being_consumed = CUBean.zero;  // the amount (from multiple tenders) being consumed to cover this order

					tender_itr = tenders.iterator();
					while (tender_itr.hasNext() && (tender_amount_being_consumed.compareTo(order_amount_to_cover) == -1))
					{
						// loop as long as I have remaining tenders and the tender amount being consumed is less than the order amount to cover

						TenderRet tender = (TenderRet)tender_itr.next();
						if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
						{
							BigDecimal tender_amount_available_to_allocate = tender.getAmountBigDecimal();

							// can the tender amount be applied to this order?
							System.out.println("can the (on account) tender amount be applied to this order?");

							if (tender_amount_available_to_allocate.compareTo(order_amount_to_cover) < 1) // not greater than
							{
								// yes.  assign this tender to this order
								System.out.println("yes.  assign this tender to this order");

								tender_amount_being_consumed = tender_amount_being_consumed.add(tender_amount_available_to_allocate);

								// I need to exhaust this tender in covering this previous order (current order rather, I believe)

								Vector vec = current_order_to_account_tenders.get(adminOrder);
								if (vec == null) vec = new Vector();
								vec.addElement(tender);
								current_order_to_account_tenders.put(adminOrder, vec);

								// consume the tender
								System.out.println("consume the tender");
								consumed_tenders.addElement(tender);

								tender_amount_hash.put(tender, tender_amount_available_to_allocate);
							}
							else
								throw new IllegalValueException("complete code here!!!");
						}
					}

					System.out.println("on account tender_amount_being_consumed >" + tender_amount_being_consumed.toString());
					order_remaining_balance_to_cover.put(adminOrder, order_amount_to_cover.subtract(tender_amount_being_consumed));

					// there should be no non-consumed account tenders at this point

					tender_itr = tenders.iterator();
					while (tender_itr.hasNext())
					{
						TenderRet tender = (TenderRet)tender_itr.next();
						if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE) && !consumed_tenders.contains(tender))
							throw new IllegalValueException("there should be no non-consumed account tenders at this point");
					}

					// there needs to be an opportunity to mark this order as being consumed also...

					if (tender_amount_being_consumed.compareTo(order_amount_to_cover) == 0)
					{
						System.out.println("the amount tendered for this order is not less than the order amount.  consume the order");
						consumed_orders.addElement(adminOrder);
					}
					else if (tender_amount_being_consumed.compareTo(order_amount_to_cover) == 1)
					{
						// the tender amount being consumed is greater than the order amount to cover somehow...
						throw new IllegalValueException("the tender amount being consumed is greater than the order amount to cover somehow...");
					}
					else
					{
						// the account tenders did not cover the current order.  I need to grab some non-account tenders to cover the rest

						BigDecimal amount_remaining_to_be_paid_on_current_order = order_amount_to_cover.subtract(tender_amount_being_consumed);
						System.out.println("amount_remaining_to_be_paid_on_current_order >" + amount_remaining_to_be_paid_on_current_order.toString());

						if (actual_payments_to_apply_to_current_order.compareTo(amount_remaining_to_be_paid_on_current_order) != 0)
							throw new IllegalValueException("actual payment to apply not equal to amount remaining to be paid on current order");





						tender_amount_being_consumed = CUBean.zero;  // the amount (potentially from multiple tenders) being consumed to cover this order

						tender_itr = tenders.iterator();
						//while (tender_itr.hasNext() && (tender_amount_being_consumed.compareTo(amount_remaining_to_be_paid_on_current_order) == -1))
						while (tender_itr.hasNext() && (amount_remaining_to_be_paid_on_current_order.compareTo(BigDecimal.ZERO) == 1))
						{
							// loop as long as I have remaining tenders and the tender amount being consumed is less than the order amount to cover
							// scratch that.  loop as long as I have remaining tenders and there is still an amount to be covered in the current order

							TenderRet tender = (TenderRet)tender_itr.next();
							if (!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE) && !consumed_tenders.contains(tender))
							{
								BigDecimal tender_amount_available_to_allocate = tender.getAmountBigDecimal();

								// can the tender amount be applied to this order?
								System.out.println("can the tender amount be applied to this order?");

								System.out.println("tender_amount_available_to_allocate >" + tender_amount_available_to_allocate.toString());
								System.out.println("amount_remaining_to_be_paid_on_current_order >" + amount_remaining_to_be_paid_on_current_order.toString());

								if (tender_amount_available_to_allocate.compareTo(amount_remaining_to_be_paid_on_current_order) == 1)
								{
									// the tender amount available to allocate exceeds the amount remaining to be paid.  need to use it
									System.out.println("the tender amount available to allocate is greater than the amount remaining to be paid.");

									tender_amount_being_consumed = tender_amount_being_consumed.add(amount_remaining_to_be_paid_on_current_order);
									System.out.println("put tender_remaining_balance_to_allocate >" + tender_amount_available_to_allocate.subtract(amount_remaining_to_be_paid_on_current_order));
									tender_remaining_balance_to_allocate.put(tender, tender_amount_available_to_allocate.subtract(amount_remaining_to_be_paid_on_current_order));

									tender_amount_hash.put(tender, amount_remaining_to_be_paid_on_current_order);
									amount_remaining_to_be_paid_on_current_order = amount_remaining_to_be_paid_on_current_order.subtract(amount_remaining_to_be_paid_on_current_order);
								}
								else
								{
									// yes.  assign this tender to this order
									System.out.println("yes.  assign this tender to this order");

									tender_amount_being_consumed = tender_amount_being_consumed.add(tender_amount_available_to_allocate);

									// I need to exhaust this tender in covering this previous order

									// consume the tender
									System.out.println("consume the tender");
									consumed_tenders.addElement(tender);

									amount_remaining_to_be_paid_on_current_order = amount_remaining_to_be_paid_on_current_order.subtract(tender_amount_available_to_allocate);
									tender_amount_hash.put(tender, tender_amount_available_to_allocate);
								}

								Vector vec = current_order_to_non_account_tenders.get(adminOrder);
								if (vec == null) vec = new Vector();
								vec.addElement(tender);
								current_order_to_non_account_tenders.put(adminOrder, vec);
							}

							System.out.println("actual amount consumed by current order >" + tender_amount_being_consumed.toString());
							System.out.println("amount_remaining_to_be_paid_on_current_order >" + amount_remaining_to_be_paid_on_current_order.toString());
						}

						System.out.println("tender_amount_being_consumed >" + tender_amount_being_consumed.toString());
						System.out.println("amount_remaining_to_be_paid_on_current_order >" + amount_remaining_to_be_paid_on_current_order.toString());
						order_remaining_balance_to_cover.put(adminOrder, amount_remaining_to_be_paid_on_current_order);

						// there needs to be an opportunity to mark this order as being consumed also...

						if (tender_amount_being_consumed.compareTo(amount_remaining_to_be_paid_on_current_order) != -1)
						{
							System.out.println("the amount tendered for this order is not less than the order amount.  consume the order");
							consumed_orders.addElement(adminOrder);
						}




					}

				}




				System.out.println("A");
				System.out.println("num_consumed_orders >" + consumed_orders.size());
				System.out.println("num_consumed_tenders >" + consumed_tenders.size());

				// loop through the previous orders and match any payments that have the exact amount...

				Iterator previous_unpaid_orders_itr = previous_unpaid_orders.iterator();
				while (previous_unpaid_orders_itr.hasNext())
				{
					ValeoOrderBean previous_order = (ValeoOrderBean)previous_unpaid_orders_itr.next();

					HashMap tender_amount_hash = (HashMap)order_to_tender_amount_hash.get(previous_order);
					if (tender_amount_hash == null)
					{
						tender_amount_hash = new HashMap(3);
						order_to_tender_amount_hash.put(previous_order, tender_amount_hash);
					}

					BigDecimal previous_order_total = previous_order.getBalance();
					System.out.println("previous_order_total >" + previous_order_total.toString());
					tender_itr = tenders.iterator();
					while (tender_itr.hasNext())
					{
						TenderRet tender = (TenderRet)tender_itr.next();
						if (!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE) && !consumed_tenders.contains(tender))
						{
							BigDecimal tender_amount = (tender_remaining_balance_to_allocate.get(tender) == null) ? tender.getAmountBigDecimal() : tender_remaining_balance_to_allocate.get(tender);
							//BigDecimal tender_amount = tender.getAmountBigDecimal();
							System.out.println("tender_amount >" + tender_amount.toString());
							System.out.println("tender_amount.compareTo(previous_order_total) >" + tender_amount.compareTo(previous_order_total));
							if (tender_amount.compareTo(previous_order_total) == 0)
							{
								// found a matching tender to cover a previous order
								System.out.println("found a matching tender to cover a previous order!");
								tender.setOrder(previous_order);

								if ((previous_order.getAuthorizationCode() != null) && !previous_order.getAuthorizationCode().equals(""))
								{
									// try to set the sales receipt for this tender also

									try
									{
										SalesReceiptRet previous_sales_receipt = SalesReceiptRet.getSalesReceipt(admin_company, previous_order.getAuthorizationCode());
										tender.setSalesReceipt(previous_sales_receipt);
									}
									catch (ObjectNotFoundException x)
									{
										x.printStackTrace();
									}
								}

								//previous_order.setStatus(ValeoOrderBean.CLOSED_ORDER_STATUS);
								previous_order.setIsPaid(true);

								consumed_tenders.addElement(tender);
								consumed_orders.addElement(previous_order);

								tender_to_order_hash.put(tender, previous_order);

								tender_amount_hash.put(tender, tender_amount);
							}
						}
					}
				}

				System.out.println("num_consumed_orders >" + consumed_orders.size());
				System.out.println("num_consumed_tenders >" + consumed_tenders.size());

				// loop through the remaining (non account) tenders and allocate them to the previous unpaid orders

				tender_itr = tenders.iterator();
				while (tender_itr.hasNext())
				{
					TenderRet tender = (TenderRet)tender_itr.next();
					if (!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE) && !consumed_tenders.contains(tender))
					{
						// this is a non-account tender that has not been used
						// there will be no exact previous order matches for this tender at this point

						BigDecimal tender_amount_to_allocate = (tender_remaining_balance_to_allocate.get(tender) == null) ? tender.getAmountBigDecimal() : tender_remaining_balance_to_allocate.get(tender);
						System.out.println("tender_amount_to_allocate >" + tender_amount_to_allocate.toString());
						BigDecimal tender_amount_consumed = CUBean.zero;

						BigDecimal order_amount_being_covered = CUBean.zero;

						previous_unpaid_orders_itr = previous_unpaid_orders.iterator();
						while (previous_unpaid_orders_itr.hasNext() && (order_amount_being_covered.compareTo(tender_amount_to_allocate) == -1))
						{
							ValeoOrderBean previous_order = (ValeoOrderBean)previous_unpaid_orders_itr.next();

							if (!consumed_orders.contains(previous_order))
							{
								// can the tender amount cover this order?
								System.out.println("can the tender amount cover this order?");

								BigDecimal previous_order_balance = (order_remaining_balance_to_cover.get(previous_order) == null) ? previous_order.getBalance() : order_remaining_balance_to_cover.get(previous_order);
								System.out.println("previous_order_balance >" + previous_order_balance.toString());
								System.out.println("tender_amount_consumed >" + tender_amount_consumed.toString());
								BigDecimal tender_amount_remaining = tender_amount_to_allocate.subtract(tender_amount_consumed);
								System.out.println("tender_amount_remaining >" + tender_amount_remaining.toString());
								if (previous_order_balance.compareTo(tender_amount_remaining) != 1)
								{
									// the previous order balance is not greater than the tender amount remaining
									// this tender can cover this previous order

									System.out.println("this tender can cover this previous order");

									order_amount_being_covered = order_amount_being_covered.add(previous_order_balance);
									tender_amount_consumed = tender_amount_consumed.add(previous_order_balance);
									Vector vec = tenders_to_allocate_to_remaining_previous_orders.get(tender);
									if (vec == null) vec = new Vector();
										vec.addElement(previous_order);
									tenders_to_allocate_to_remaining_previous_orders.put(tender, vec);
									consumed_orders.addElement(previous_order);

									order_remaining_balance_to_cover.put(adminOrder, CUBean.zero);

									HashMap tender_amount_hash = (HashMap)order_to_tender_amount_hash.get(previous_order);
									if (tender_amount_hash == null)
									{
										tender_amount_hash = new HashMap(3);
										order_to_tender_amount_hash.put(previous_order, tender_amount_hash);
									}
									tender_amount_hash.put(tender, previous_order_balance);
								}
								else if (previous_order_balance.compareTo(tender_amount_remaining) == 1)
									System.out.println("no.  the remaining order amount is greater than the tender.  this should get allocated when I loop the orders");
								//else
								//	throw new IllegalValueException("complete code here...");
							}
						}

						System.out.println("order_amount_being_covered >" + order_amount_being_covered.toString());
						tender_remaining_balance_to_allocate.put(tender, tender_amount_to_allocate.subtract(order_amount_being_covered));

						// there needs to be an opportunity to mark this tender as being consumed also...

						if (order_amount_being_covered.compareTo(tender_amount_to_allocate) != -1)
						{
							System.out.println("the amount covered of this order is not less than the tender amount.  consume the tender");
							consumed_tenders.addElement(tender);
							tender_remaining_balance_to_allocate.put(tender, CUBean.zero);
						}
					}
					else
						System.out.println("consumed or account tender found");

				}

				System.out.println("C");
				System.out.println("num_consumed_orders >" + consumed_orders.size());
				System.out.println("num_consumed_tenders >" + consumed_tenders.size());

				// loop the previous orders and allocate to tenders...

				previous_unpaid_orders_itr = previous_unpaid_orders.iterator();
				while (previous_unpaid_orders_itr.hasNext())
				{
					ValeoOrderBean previous_order = (ValeoOrderBean)previous_unpaid_orders_itr.next();
					if (!consumed_orders.contains(previous_order))
					{
						HashMap tender_amount_hash = (HashMap)order_to_tender_amount_hash.get(previous_order);
						if (tender_amount_hash == null)
						{
							tender_amount_hash = new HashMap(3);
							order_to_tender_amount_hash.put(previous_order, tender_amount_hash);
						}

						System.out.println("previous order found that is not consumed >" + previous_order.getBalance());

						BigDecimal order_amount_to_cover = (order_remaining_balance_to_cover.get(previous_order) == null) ? previous_order.getBalance() : order_remaining_balance_to_cover.get(previous_order);
						System.out.println("order_amount_to_cover >" + order_amount_to_cover.toString());
						BigDecimal tender_amount_being_consumed = CUBean.zero;  // the amount (from multiple tenders) being consumed to cover this order
						BigDecimal order_amount_remaining_to_be_covered = order_amount_to_cover;

						tender_itr = tenders.iterator();
						while (tender_itr.hasNext() && (tender_amount_being_consumed.compareTo(order_amount_to_cover) == -1))
						{
							// loop as long as I have remaining tenders and the tender amount being consumed is less than the order amount to cover
							TenderRet tender = (TenderRet)tender_itr.next();
							if (!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE) && !consumed_tenders.contains(tender))
							{
								BigDecimal tender_amount_available_to_allocate = (tender_remaining_balance_to_allocate.get(tender) == null) ? tender.getAmountBigDecimal() : tender_remaining_balance_to_allocate.get(tender);

								// can the tender amount be applied to this order?
								System.out.println("can the tender amount be applied to this order?");
								System.out.println("tender_amount_available_to_allocate >" + tender_amount_available_to_allocate.toString());
								System.out.println("order_amount_to_cover >" + order_amount_to_cover.toString());
								System.out.println("tender_amount_being_consumed >" + tender_amount_being_consumed.toString());
								System.out.println("order_amount_remaining_to_be_covered >" + order_amount_remaining_to_be_covered.toString());

								if (tender_amount_available_to_allocate.compareTo(order_amount_remaining_to_be_covered) < 1)
								{
									// yes.  assign this tender to this order
									System.out.println("the tender amount available to allocate is less than (or equal to) the order amount to cover.  yes.  assign this tender to this order");

									tender_amount_being_consumed = tender_amount_being_consumed.add(tender_amount_available_to_allocate);
									order_amount_remaining_to_be_covered = order_amount_remaining_to_be_covered.subtract(tender_amount_available_to_allocate);

									// I need to exhaust this tender in covering this previous order

									Vector vec = previous_orders_to_allocate_to_remaining_tenders.get(previous_order);
									if (vec == null) vec = new Vector();
									vec.addElement(tender);
									previous_orders_to_allocate_to_remaining_tenders.put(previous_order, vec);

									// consume the tender
									System.out.println("consume the tender");
									consumed_tenders.addElement(tender);

									tender_amount_hash.put(tender, tender_amount_available_to_allocate);
								}
							}
						}

						System.out.println("tender_amount_being_consumed >" + tender_amount_being_consumed.toString());
						order_remaining_balance_to_cover.put(previous_order, order_amount_to_cover.subtract(tender_amount_being_consumed));

						// there needs to be an opportunity to mark this order as being consumed also...

						if (tender_amount_being_consumed.compareTo(order_amount_to_cover) != -1)
						{
							System.out.println("the amount tendered for this order is not less than the order amount.  consume the order");
							consumed_orders.addElement(previous_order);
							order_remaining_balance_to_cover.put(previous_order, CUBean.zero);
						}
					}
				}

				System.out.println("D");
				System.out.println("num_consumed_orders >" + consumed_orders.size());
				System.out.println("num_consumed_tenders >" + consumed_tenders.size());

				// find tenders with change remaining
				tender_itr = tender_remaining_balance_to_allocate.keySet().iterator();
				if (tender_itr.hasNext())
					System.out.println("tender found with potential change remaining");
				else
					System.out.println("no tenders found with change remaining");
				while (tender_itr.hasNext())
				{
					TenderRet tender = (TenderRet)tender_itr.next();
					if (!consumed_tenders.contains(tender))
					{
						BigDecimal tender_amount_remaining_to_allocate = tender_remaining_balance_to_allocate.get(tender);
						System.out.println("tender_amount_remaining_to_allocate >" + tender_amount_remaining_to_allocate.toString());
						if (tender_amount_remaining_to_allocate.compareTo(CUBean.zero) == 1)
						{
							// there's some juice left in this tender.
							System.out.println("there's some juice left in this tender >" + tender.getAmountString());
							tenders_to_allocate_to_remaining_orders.addElement(tender);
							consumed_tenders.addElement(tender);
						}
					}
					else
						System.out.println("consumed...");
				}

				System.out.println("");

				// find orders that are not completely covered
				Iterator orders_itr = order_remaining_balance_to_cover.keySet().iterator();
				if (orders_itr.hasNext())
					System.out.println("order(s) found that is partially covered");
				else
					System.out.println("no loose orders found that are partially covered");
				while (orders_itr.hasNext())
				{
					ValeoOrderBean partially_covered_order = (ValeoOrderBean)orders_itr.next();
					if (!consumed_orders.contains(partially_covered_order))
					{
						BigDecimal order_amount_remaining_to_cover = order_remaining_balance_to_cover.get(partially_covered_order);
						System.out.println("order_amount_remaining_to_cover >" + order_amount_remaining_to_cover.toString());
						if (order_amount_remaining_to_cover.compareTo(CUBean.zero) == 1)
						{
							// there's some stank left in this order.
							System.out.println("there's some stank left in tha tank >" + partially_covered_order.getBalanceString());
							remaining_orders.addElement(partially_covered_order);
							consumed_orders.addElement(partially_covered_order);
						}
					}
					else
						System.out.println("consumed....");
				}


				tender_itr = tenders.iterator();
				while (tender_itr.hasNext())
				{
					TenderRet tender = (TenderRet)tender_itr.next();
					if (!consumed_tenders.contains(tender))
					{
						tenders_to_allocate_to_remaining_orders.addElement(tender);
						consumed_tenders.addElement(tender);
					}
				}

				orders_itr = all_orders.iterator();
				while (orders_itr.hasNext())
				{
					ValeoOrderBean order = (ValeoOrderBean)orders_itr.next();
					if (!consumed_orders.contains(order))
					{
						remaining_orders.addElement(order);
						consumed_orders.addElement(order);
					}
				}

				int num_consumed_orders = consumed_orders.size();
				int num_consumed_tenders = consumed_tenders.size();

				System.out.println("num_consumed_orders >" + consumed_orders.size());
				System.out.println("num_consumed_tenders >" + consumed_tenders.size());

				if (num_consumed_orders != all_orders.size())
					throw new IllegalValueException("only able to consume " + num_consumed_orders + " orders!");
				if (num_consumed_tenders != tenders.size())
					throw new IllegalValueException("only able to consume " + num_consumed_tenders + " tenders! >" + tenders.size());
				//else
				//	throw new IllegalValueException("everything consumed!!!");




				// all previous orders should be resolved at this point.  there may be a tender with amount left that can be applied to the current charges



				// there may be account orders left at this point, if so, it shouldn't have to cover any previous orders.  apply whatever is left to the
				// this is why my robot didn't work
				//remaining_orders.addElement(adminOrder);
			}


			// loop through the orders and construct the tender vector

			Iterator itr = all_orders.iterator();
			while (itr.hasNext())
			{
				ValeoOrderBean order = (ValeoOrderBean)itr.next();
				if (previous_unpaid_orders.contains(order))
				{
					System.out.println("processing order >" + order.getId());
					BigDecimal balance = (order_remaining_balance_to_cover.get(order) == null) ? CUBean.zero : order_remaining_balance_to_cover.get(order);
					System.out.println("previous Order balance >" + balance.toString());
					order.setBalance(balance);
					order.setModifyPerson(mod_person);
					//order.setBalance(CUBean.zero);
					if (balance.compareTo(CUBean.zero) == 0)
					{
						System.out.println("zero balance");
						//order.setStatus(ValeoOrderBean.CLOSED_ORDER_STATUS);
						order.setIsPaid(true);
					}
					else if (balance.compareTo(CUBean.zero) == 1)
					{
						// do nothing
					}
					else
						throw new IllegalValueException("negative balance for previous order???");
				}
				else
					order.setBalance(current_charges_total.subtract(actual_payments_to_apply_to_current_order));
				order.invalidate();
				order.setWorkstationName(cookie_workstation_str);
				order.save();
			}

		}

		// map tenders to orders

		Iterator itr = tenders.iterator();
		while (itr.hasNext()) {
			TenderRet tender = (TenderRet)itr.next();
			tender.invalidate();
			Vector orders_vec = new Vector();

			ValeoOrderBean order_obj = tender_to_order_hash.get(tender);
			if (order_obj != null)
				orders_vec.addElement(order_obj);
			Vector vec = tender_to_orders_hash.get(tender);
			if (vec != null)
				orders_vec.addAll(vec);

			vec = tenders_to_allocate_to_remaining_previous_orders.get(tender);
			if (vec != null)
				orders_vec.addAll(vec);

			if (tenders_to_allocate_to_remaining_orders.contains(tender))
				orders_vec.addAll(remaining_orders);

			if (all_orders.size() == 1)
				tender.setOrder((ValeoOrderBean)all_orders.get(0));

			System.out.println("SETTING ORDERS FOR TENDER >" + orders_vec.size());
			tender.setOrders(orders_vec);
			tender.setTenderPaymentInfo(order_to_tender_amount_hash);
			tender.setWorkstationName(cookie_workstation_str);

			if (previous_unpaid_orders.contains(order_obj) && !tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE)) {
				tender.setIsPaymentOnAccount(true);
			}

			tender.save();

			if (gift_card_hash != null)
			{
				GiftCard gift_card = (GiftCard)gift_card_hash.get(tender);
				if (gift_card != null)
				{
					System.out.println("Found gift card for tender >" + tender.getLabel());
					BigDecimal gift_card_balance = gift_card.getBalance();
					System.out.println("Tender amount >" + tender.getAmountString());
					gift_card_balance = gift_card_balance.subtract(tender.getAmountBigDecimal());
					gift_card.setBalance(gift_card_balance);
					gift_card.save();
				}
			}

			if (gift_cert_hash != null)
			{
				GiftCard gift_cert = (GiftCard)gift_cert_hash.get(tender);
				if (gift_cert != null)
				{
					System.out.println("Found gift cert for tender >" + tender.getLabel());
					BigDecimal gift_cert_balance = gift_cert.getBalance();
					System.out.println("Tender amount >" + tender.getAmountString());
					gift_cert_balance = gift_cert_balance.subtract(tender.getAmountBigDecimal());
					gift_cert.setBalance(gift_cert_balance);
					gift_cert.save();
				}
			}
		}

		// map orders to tenders

		itr = all_orders.iterator();
		while (itr.hasNext())
		{
			ValeoOrderBean order = (ValeoOrderBean)itr.next();

			// get all of the tenders that have been allocated for this order

			Vector tenders_vec_x = new Vector();


			Vector vec = current_order_to_account_tenders.get(order);
			if (vec != null)
				tenders_vec_x.addAll(vec);

			vec = current_order_to_non_account_tenders.get(order);
			if (vec != null)
				tenders_vec_x.addAll(vec);

			vec = order_to_tenders_hash.get(order);
			if (vec != null)
				tenders_vec_x.addAll(vec);

			vec = previous_orders_to_allocate_to_remaining_tenders.get(order);
			if (vec != null)
				tenders_vec_x.addAll(vec);

			if (tenders_vec_x.size() > 0)
			{
				Vector existing_tenders = order.getTenders();
				tenders_vec_x.addAll(existing_tenders);
				System.out.println("SETTING TENDERS FOR ORDER >" + tenders_vec_x.size());
				order.setTenders(tenders_vec_x);
				order.setTenderPaymentInfo(order_to_tender_amount_hash.get(order));
				order.setWorkstationName(cookie_workstation_str);
				order.save();

				Iterator tenders_itr = tenders_vec_x.iterator();
				while (tenders_itr.hasNext())
				{
					TenderRet tender = (TenderRet)tenders_itr.next();
					if (previous_unpaid_orders.contains(order) && !tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
					{
						tender.setIsPaymentOnAccount(true);
						tender.save();
					}
				}
			}
		}

		Iterator response_itr = responses.iterator();
		while (response_itr.hasNext())
		{
			QBMSCreditCardResponse response = (QBMSCreditCardResponse)response_itr.next();
			response.save();
		}

		System.out.println("isCheckout >" + isCheckout);

		if (isCheckout) {
			// mark the appointment as checked out...
			
			System.out.println("mark the appointment as checked out...");

			appointment.setState(AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
			appointment.setCreateOrModifyPerson(mod_person);
			try {
				appointment.save();
			} catch (Exception x) {
				x.printStackTrace();
			}
		}

		itr = adminOrder.getOrders();
		while (itr.hasNext())
		{
			CheckoutOrderline order_line = (CheckoutOrderline)itr.next();
			CheckoutCode checkout_code_obj = (CheckoutCode)order_line.getCheckoutCode();
			CheckoutCodeBean checkout_code = (CheckoutCodeBean)CheckoutCodeBean.getCheckoutCode(checkout_code_obj.getCheckoutCodeId());
			if (checkout_code.getType() == CheckoutCodeBean.INVENTORY_TYPE)
			{
				BigDecimal qty = order_line.getQuantity();
				short on_hand_qty = checkout_code.getOnHandQuantity();
				on_hand_qty -= qty.shortValue();
				checkout_code.setOnHandQuantity(on_hand_qty);
				checkout_code.save();
			}
		}

		//Vector selected_previous_open_orderlines = new Vector();

		/*
		int numPrevious = Integer.parseInt(_request.getParameter("numPrevious"));
		for (int i = 0; i < numPrevious; i++)
		{
			if (_request.getParameter("inputCheck" + (i + 1) + "tblPrevious") != null)
			{
				Criteria crit = new Criteria();
				crit.add(CheckoutOrderlinePeer.CHECKOUT_ORDERLINE_ID, Integer.parseInt(_request.getParameter("hiddeninputCheck" + (i + 1) + "tblPrevious")));
				List obj_list = CheckoutOrderlinePeer.doSelect(crit);
				if (obj_list.size() == 1)
				{
					CheckoutOrderline orderline = (CheckoutOrderline)obj_list.get(0);
					total_charges_to_apply_credits_to = total_charges_to_apply_credits_to.add(orderline.getActualAmount());
					//selected_previous_open_orderlines.addElement(orderline);
					applied_charge_order_lines.addElement(orderline);
				}
				else if (obj_list.size() == 0)
					throw new ObjectNotFoundException("Checkout Orderline not found for id :" + _request.getParameter("hiddeninputCheck" + (i + 1) + "tblPrevious"));
				else
					throw new UniqueObjectNotFoundException("Unique Checkout Orderline not found for id :" + _request.getParameter("hiddeninputCheck" + (i + 1) + "tblPrevious"));

				any_charges_applied = true;
			}
		}
		 */


		//if (has_credits && !any_charges_applied)
		//	throw new IllegalValueException("You must select a charge to apply the " + total_current_credits.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " to.");


		/*
		Iterator previous_orderline_itr = selected_previous_open_orderlines.iterator();
		while (previous_orderline_itr.hasNext())
		{
			CheckoutOrderline orderline = (CheckoutOrderline)previous_orderline_itr.next();
			orderline.setOrderstatus(ValeoOrderBean.CLOSED_ORDER_STATUS);

			// I need to possibly close the order here too?
			// do I need to track the credit that i
		}
		 */


		/*
		Iterator applied_charge_order_lines_itr = applied_charge_order_lines.iterator();
		while (applied_charge_order_lines_itr.hasNext())
		{
			CheckoutOrderline order_line = (CheckoutOrderline)applied_charge_order_lines_itr.next();
			System.out.println("applied charge orderline >" + order_line.getCheckoutOrderlineId());
			boolean is_in_order = order_lines.contains(order_line);

			boolean orderline_paid_for = false;

			BigDecimal price = order_line.getPrice();
			BigDecimal paid = null;
			boolean are_there_enough_credits_to_pay_for_this = (total_current_credits.compareTo(price) > -1);
			if (are_there_enough_credits_to_pay_for_this)
			{
				order_line.setActualAmount(price);
				total_current_credits = total_current_credits.subtract(price);
				order_line.setOrderstatus(ValeoOrderBean.CLOSED_ORDER_STATUS);
				paid = price;
				orderline_paid_for = true;
			}
			else
			{
				// partial payment
				paid = total_current_credits.add(zero);
				order_line.setActualAmount(paid);
				total_current_credits = zero;

				if (is_in_order)
					all_new_charges_applied = false;
			}

			if (orderline_plan_map.containsKey(order_line))
			{
				// this is a plan being ordered and paid for

				PaymentPlanBean payment_plan = (PaymentPlanBean)orderline_plan_map.remove(order_line);
				PaymentPlanInstanceBean plan_instance = this.doPlan((UKOnlinePersonBean)loginBean.getPerson(), adminPerson, payment_plan, price, paid);
				order_line.setPaymentPlanInstanceId(plan_instance.getId());
			}
			else
			{
				// see if this is a previous charge for a plan
				CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(order_line.getCheckoutCodeId());
				if (checkout_code.getType() == CheckoutCodeBean.PLAN_TYPE)
				{
					// yeah, this must be a previous charge for a plan.  grab the plan instance and update its paid amount

					PaymentPlanInstanceBean plan_instance = PaymentPlanInstanceBean.getPaymentPlanInstance(order_line.getPaymentPlanInstanceId());
					plan_instance.setAmountPaid(plan_instance.getAmountPaid().add(paid));
					plan_instance.save();
				}
			}

			if (!is_in_order)
				order_line.save();
		}
		 */

		/*
		Iterator orderline_plan_map_itr = orderline_plan_map.keySet().iterator();
		while (orderline_plan_map_itr.hasNext())
		{
			CheckoutOrderline order_line = (CheckoutOrderline)orderline_plan_map_itr.next();
			PaymentPlanBean payment_plan = (PaymentPlanBean)orderline_plan_map.get(order_line);				
			PaymentPlanInstanceBean plan_instance = this.doPlan((UKOnlinePersonBean)loginBean.getPerson(), adminPerson, payment_plan, order_line.getPrice(), zero);
			order_line.setPaymentPlanInstanceId(plan_instance.getId());
			all_new_charges_applied = false; // just to make sure
		}
		 */

		

		PrintedReceipt printed_receipt = null;
		try
		{
			printed_receipt = PrintedReceipt.getPrintedReceipt(primary_order);
		}
		catch (ObjectNotFoundException printed_receipt_not_found)
		{
			printed_receipt = new PrintedReceipt();
		}
		catch (UniqueObjectNotFoundException printed_receipt_not_found)
		{
			printed_receipt = new PrintedReceipt();
		}


		printed_receipt.setPrimaryOrder(primary_order);

		printed_receipt.setCashier(mod_person);
		printed_receipt.setPerson(adminPerson);
		printed_receipt.setPreviousOrders(previous_orders);
		printed_receipt.setTenders(tenders_vec);
		printed_receipt.setSubtotal(h_subtotal);
		printed_receipt.setTax(h_tax);
		printed_receipt.setTotal(h_total);
		printed_receipt.setDiscountProducts(total_discount_products);
		printed_receipt.setDiscountServices(total_discount_services);
		printed_receipt.setDiscountPercentageProducts(discount_perc_products);
		printed_receipt.setDiscountPercentageServices(discount_perc_services);
		printed_receipt.setShipping(BigDecimal.ZERO);
		System.out.println("printed_receipt >" + printed_receipt.isNew());
		System.out.println("SAVING RECEIPT");
		printed_receipt.save();
		System.out.println("SAVED?");

		ClientServlet.selectedReceipt = printed_receipt;
		
		
		
		
		
		// empty the cart
		
		ShoppingCartBean.emptyCart(adminPerson);
		
		

		CheckoutAction.doGiftCards(gift_card_number_hash, mod_person, primary_order);
		CheckoutAction.doGiftCertificates(gift_cert_number_hash, mod_person, primary_order);


		boolean balance_adjustment_made = false;

		// make adjustments, if necessary, to this person's balance

		System.out.println("make adjustments, if necessary, to this person's balance");
		itr = tenders_vec.iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();
			System.out.println("tender amount >" + tender.getAmountString());
			if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
			{
				// balance should go up when I place things on account
				adminPerson.adjustBalanceUp(tender.getAmountBigDecimal());
				balance_adjustment_made = true;
			}
			else if (tender.isPaymentOnAccount())
			{
				// the tender contains a payment on account, but the entire tender amount isn't necessarily payment on account.
				// some of the tender amount may be covering the current order...

				BigDecimal amount_mapped_to_current_order = tender.getAmountMappedToOrder(adminOrder);
				System.out.println("amount_mapped_to_current_order >" + amount_mapped_to_current_order.toString());
				BigDecimal amount_remaining_for_on_account_orders = tender.getAmountBigDecimal().subtract(amount_mapped_to_current_order);
				System.out.println("amount_remaining_for_on_account_orders >" + amount_remaining_for_on_account_orders.toString());

				// balance should go down when I pay for things previously placed on account
				adminPerson.adjustBalanceDown(amount_remaining_for_on_account_orders);
				balance_adjustment_made = true;
			}
		}

		if (is_overpayment) {
			BigDecimal overpayment_amount = credits.subtract(charges);
			System.out.println("found overpayment amount >" + overpayment_amount.toString());
			adminPerson.adjustBalanceDown(overpayment_amount);
			balance_adjustment_made = true;
		}

		if (balance_adjustment_made) {
			adminPerson.save();
		}

		Iterator map_itr = orderline_plan_map.keySet().iterator();
		while (map_itr.hasNext()) {
			//CheckoutOrderline key = (CheckoutOrderline)map_itr.next();
			String key = (String)map_itr.next();
			PaymentPlanBean payment_plan = (PaymentPlanBean)orderline_plan_map.get(key);
			System.out.println("PLAN " + payment_plan);
			System.out.println("GETTING PAID MAP " + key);
			Boolean plan_is_paid = (Boolean)orderline_paid_map.get(key);
			BigDecimal actualAmount = (BigDecimal)orderline_amount_paid_map.get(key);

			//UKOnlinePersonBean loginPerson = (UKOnlinePersonBean)loginBean.getPerson();

			boolean b = plan_is_paid.booleanValue();

			PaymentPlanInstanceBean plan_instance = CheckoutAction.doPlan(mod_person, adminPerson, payment_plan, actualAmount, b ? actualAmount : BigDecimal.ZERO);

			CheckoutOrderline orderline = (CheckoutOrderline)orderline_map.get(key);
			System.out.println("CheckoutOrderline FOUND > " + orderline);

			orderline.setPaymentPlanInstanceId(plan_instance.getId());
			orderline.save();
		}


		Iterator plan_usages_itr = practice_area_plan_use_map.keySet().iterator();
		while (plan_usages_itr.hasNext())
		{
			PracticeAreaBean key = (PracticeAreaBean)plan_usages_itr.next();
			Integer num_usages = (Integer)practice_area_plan_use_map.get(key);

			if ((appointment != null) && adminPerson.hasPlan(appointment.getPracticeArea()))
			{
				PaymentPlanInstanceBean payment_plan_instance = adminPerson.getPlan(appointment);
				payment_plan_instance.useVisits(num_usages.shortValue());
				payment_plan_instance.save();
			}
		}
		
		
		
		try {

			/*
			ValeoOrderBean adminOrder = (ValeoOrderBean)session.getAttribute("adminOrder");
			Vector previous_orders = (Vector<ValeoOrderBean>)session.getAttribute("previous_orders");
			Vector tenders = (Vector<TenderRet>)session.getAttribute("tenders");

			BigDecimal subtotal = (BigDecimal)session.getAttribute("h_subtotal");
			BigDecimal tax = (BigDecimal)session.getAttribute("h_tax");
			BigDecimal total = (BigDecimal)session.getAttribute("h_total");

			BigDecimal total_discount = (BigDecimal)session.getAttribute("h_total_discount");
			BigDecimal discount_perc = (BigDecimal)session.getAttribute("h_discount_percentage");

			BigDecimal shipping = (BigDecimal)session.getAttribute("h_shipping");


			//UKOnlinePersonBean client = UKOnlinePersonBean.getPerson(adminCompany, adminOrder.getPersonId());
			UKOnlinePersonBean client = (UKOnlinePersonBean)session.getAttribute("adminPerson");
			*/

			sales_receipt_url = CUBean.getProperty("cu.webDomain") + "/resources/pdf/" + SalesReceiptBuilder.generateSalesReceipt(admin_company, mod_person, adminPerson, adminOrder, previous_orders, tenders, h_subtotal, h_tax, h_total, total_discount_products, total_discount_services, discount_perc_products, discount_perc_services, BigDecimal.ZERO);
			//writer.println("<sales_receipt_url><![CDATA[" + sales_receipt_url + "]]></sales_receipt_url>");
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		return sales_receipt_url;
		
	}
	
	private static PaymentPlanInstanceBean
	doPlan(UKOnlinePersonBean _create_person, UKOnlinePersonBean _plan_person, PaymentPlanBean _payment_plan, BigDecimal _amount_charged_for_plan, BigDecimal _amount_paid_for_plan)
		throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		PaymentPlanInstanceBean payment_plan_instance = new PaymentPlanInstanceBean();
		payment_plan_instance.setPaymentPlan(_payment_plan);
		payment_plan_instance.setPerson(_plan_person);
		Calendar now = Calendar.getInstance();
		payment_plan_instance.setStartDate(now.getTime());

		//BigDecimal amount_charged_for_plan = (BigDecimal)plan_charges.get(payment_plan);
		//BigDecimal amount_paid_for_plan = new BigDecimal(0);
		payment_plan_instance.setNumberOfVisitsRemaining(_payment_plan.getVisits());
		payment_plan_instance.setAmountPaid(_amount_paid_for_plan);
		payment_plan_instance.setAmountCharged(_amount_charged_for_plan);
		payment_plan_instance.setGroupPoolingAllowed(_payment_plan.isGroupPoolingAllowed());
		payment_plan_instance.setCreatePerson(_create_person);
		payment_plan_instance.activate();
		payment_plan_instance.save();
		
		return payment_plan_instance;
	}

	private static void
	doGiftCards(HashMap _hash, UKOnlinePersonBean _cashier, ValeoOrderBean _order)
		throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		if (_hash == null)
			return;
		
		Vector keys_in_order = new Vector();
		int num_keys = _hash.size();
		for (int i = 1; keys_in_order.size() < num_keys; i++)
		{
			String test_key = "hiddenGiftCardNumber" + i + "tblCharges";
			String gift_certificate_number = (String)_hash.get(test_key);
			if (gift_certificate_number != null)
				keys_in_order.addElement(test_key);

			if (i > 100)
				throw new IllegalValueException("Unable to process gift card transaction...");
		}

		Iterator itr = _order.getOrders();
		//for (int i = 0; itr.hasNext(); i++)
		int i = 0;
		while (itr.hasNext())
		{
			CheckoutOrderline order_line = (CheckoutOrderline)itr.next();

			CheckoutCode checkout_code_obj = (CheckoutCode)order_line.getCheckoutCode();
			CheckoutCodeBean checkout_code = (CheckoutCodeBean)CheckoutCodeBean.getCheckoutCode(checkout_code_obj.getCheckoutCodeId());
			if (checkout_code.getType() == CheckoutCodeBean.GIFT_CARD)
			{
				String key = (String)keys_in_order.elementAt(i);
				i++;
				String gift_card_number = (String)_hash.get(key);
				System.out.println("gift_card_number found >" + gift_card_number);

				//short qty = order_line.getQuantity().shortValue();
				//for (short i = 0; i < qty; i++)
				//{
					GiftCard gift_card = new GiftCard();
					gift_card.setType(GiftCard.GIFT_CARD);
					gift_card.setCardNumber(gift_card_number);
					gift_card.setOrder(_order);
					gift_card.setOriginalAmount(order_line.getPrice());
					gift_card.setBalance(order_line.getPrice());
					gift_card.setCreateOrModifyPerson(_cashier);
					gift_card.save();
				//}

			}

		}
	}

	private static void
	doGiftCertificates(HashMap _hash, UKOnlinePersonBean _cashier, ValeoOrderBean _order)
		throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		if (_hash == null)
			return;

		Vector keys_in_order = new Vector();
		int num_keys = _hash.size();
		for (int i = 1; keys_in_order.size() < num_keys; i++)
		{
			String test_key = "hiddenGiftCertNumber" + i + "tblCharges";
			String gift_certificate_number = (String)_hash.get(test_key);
			if (gift_certificate_number != null)
				keys_in_order.addElement(test_key);
			
			if (i > 100)
				throw new IllegalValueException("Unable to process gift card transaction...");
		}

		Iterator itr = _order.getOrders();
		//for (int i = 0; itr.hasNext(); i++)
		int i = 0;
		while (itr.hasNext())
		{
			CheckoutOrderline order_line = (CheckoutOrderline)itr.next();

			CheckoutCode checkout_code_obj = (CheckoutCode)order_line.getCheckoutCode();
			CheckoutCodeBean checkout_code = (CheckoutCodeBean)CheckoutCodeBean.getCheckoutCode(checkout_code_obj.getCheckoutCodeId());
			if (checkout_code.getType() == CheckoutCodeBean.GIFT_CERTIFICATE)
			{
				String key = (String)keys_in_order.elementAt(i);
				i++;
				String gift_certificate_number = (String)_hash.get(key);
				System.out.println("gift_certificate_number found >" + gift_certificate_number);

				//short qty = order_line.getQuantity().shortValue();
				//for (short i = 0; i < qty; i++)
				//{
					GiftCard gift_certificate = new GiftCard();
					gift_certificate.setType(GiftCard.GIFT_CERTIFICATE);
					gift_certificate.setCardNumber(gift_certificate_number);
					gift_certificate.setOrder(_order);
					gift_certificate.setOriginalAmount(order_line.getPrice());
					gift_certificate.setBalance(order_line.getPrice());
					gift_certificate.setCreateOrModifyPerson(_cashier);
					gift_certificate.save();
				//}

			}

		}
	}
	
	private void
	doLegacyStuff(Vector _tenders, HashMap _gift_card_hash, HashMap _gift_cert_hash, String _workstation_name)
		throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		
		System.out.println("doLegacyStuff() invoked in CheckoutAction");
		
		Iterator itr = _tenders.iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();
			if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
				throw new IllegalValueException("Previous open orders or invoices cannot be placed on account.");
		}
				
		

		// save the tenders that are being applied

		itr = _tenders.iterator();
		while (itr.hasNext())
		{
			TenderRet tender = (TenderRet)itr.next();


			//tender.setOrders(orders_vec);
			//tender.setTenderPaymentInfo(order_to_tender_amount_hash);
			tender.setWorkstationName(_workstation_name);
			tender.setIsPaymentOnAccount(true);
			tender.save();

			if (_gift_card_hash != null)
			{
				GiftCard gift_card = (GiftCard)_gift_card_hash.get(tender);
				if (gift_card != null)
				{
					System.out.println("Found gift card for tender >" + tender.getLabel());
					BigDecimal gift_card_balance = gift_card.getBalance();
					System.out.println("Tender amount >" + tender.getAmountString());
					gift_card_balance = gift_card_balance.subtract(tender.getAmountBigDecimal());
					gift_card.setBalance(gift_card_balance);
					gift_card.save();
				}
			}

			if (_gift_cert_hash != null)
			{
				GiftCard gift_cert = (GiftCard)_gift_cert_hash.get(tender);
				if (gift_cert != null)
				{
					System.out.println("Found gift cert for tender >" + tender.getLabel());
					BigDecimal gift_cert_balance = gift_cert.getBalance();
					System.out.println("Tender amount >" + tender.getAmountString());
					gift_cert_balance = gift_cert_balance.subtract(tender.getAmountBigDecimal());
					gift_cert.setBalance(gift_cert_balance);
					gift_cert.save();
				}
			}
		}
		
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
	
	/*
	private void
	doPrintedReceipt(UKOnlinePersonBean _adminPerson, HttpSession _session, BigDecimal _total_discount, BigDecimal _discount_perc, boolean _is_legacy)
		throws TorqueException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		System.out.println("doPrintedReceipt() invoked in CheckoutAction");
		
		ValeoOrderBean primary_order = (ValeoOrderBean)_session.getAttribute("adminOrder");
		
		PrintedReceipt printed_receipt = null;
		if (!primary_order.isNew())
		{
			try
			{
				printed_receipt = PrintedReceipt.getPrintedReceipt(primary_order);
			}
			catch (ObjectNotFoundException printed_receipt_not_found)
			{

			}
		}
		
		if (printed_receipt == null)
			printed_receipt = new PrintedReceipt();

		_session.setAttribute("h_total_discount", _total_discount);
		_session.setAttribute("h_discount_percentage", _discount_perc);
		_session.setAttribute("h_shipping", BigDecimal.ZERO);

		printed_receipt.setPrimaryOrder(primary_order);

		Vector<TenderRet> tenders_vec = (Vector<TenderRet>)_session.getAttribute("tenders");

		printed_receipt.setCashier((UKOnlinePersonBean)loginBean.getPerson());
		printed_receipt.setPerson(_adminPerson);
		printed_receipt.setPreviousOrders((Vector<ValeoOrderBean>)_session.getAttribute("previous_orders"));
		printed_receipt.setTenders(tenders_vec);
		printed_receipt.setSubtotal((BigDecimal)_session.getAttribute("h_subtotal"));
		printed_receipt.setTax((BigDecimal)_session.getAttribute("h_tax"));
		printed_receipt.setTotal((BigDecimal)_session.getAttribute("h_total"));
		printed_receipt.setDiscount(_total_discount);
		printed_receipt.setDiscountPercentage(_discount_perc);
		printed_receipt.setShipping(BigDecimal.ZERO);
		printed_receipt.save();
		
	}
	 * 
	 */
}