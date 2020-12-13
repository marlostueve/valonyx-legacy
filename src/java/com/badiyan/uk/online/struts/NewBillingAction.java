/*
 * NewBillingAction.java
 *
 * Created on April 11, 2008, 9:59 PM
 * 
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.*;

import java.text.*;
import java.util.*;
import java.math.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a client profile.
 *
 * @author Marlo Stueve
 */
public final class
NewBillingAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in NewBillingAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;
		OrderBean adminOrder = null;
		
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
				{
					loginBean = new UKOnlineLoginBean();
					session.setAttribute("loginBean", loginBean);
				}
				
				admin_company = (CompanyBean)session.getAttribute("adminCompany");
				adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				adminOrder = (OrderBean)session.getAttribute("adminOrder");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			String transactionDateInput = (String)PropertyUtils.getSimpleProperty(_form, "transactionDateInput");
			String amountInput = (String)PropertyUtils.getSimpleProperty(_form, "amountInput");
			
			System.out.println("transactionDateInput >" + transactionDateInput);
			System.out.println("amountInput >" + amountInput);
			
			Vector orders_to_be_billed = new Vector();
			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements())
			{
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));
				
				if (param_name.startsWith("order_check_"))
				{
					OrderBean order = ValeoOrderBean.getOrder(Integer.parseInt(param_name.substring(12)));
					System.out.println("order id >" + order.getId());
					orders_to_be_billed.addElement(order);
				}
			}
			
			if (orders_to_be_billed.size() == 0)
				throw new IllegalValueException("Please select one or more orders to bill.");
			if (transactionDateInput.length() == 0)
				throw new IllegalValueException("You must select an initial billing date.");
			
			
			Date initialBillingDate = CUBean.getDateFromUserString(transactionDateInput);
			BigDecimal amount = new BigDecimal(amountInput);
			
			BillingBean bill = new BillingBean();
			
			Vector billing_dates = new Vector();
			billing_dates.addElement(initialBillingDate);
			bill.setBillingDates(billing_dates);
			
			bill.setOrders(orders_to_be_billed);
			
			bill.setAmountBilled(amount);
			bill.setCreateOrModifyPerson(loginBean.getPerson());
			bill.setCompany(admin_company);
			bill.setIsOpen(true);
			bill.setPerson(adminPerson);
			bill.save();
			
			/*
			Vector plans_being_purchased = new Vector();
			HashMap plan_charges = new HashMap();
			
			BigDecimal total_charges_to_apply_credits_to = new BigDecimal(0);
			BigDecimal total_current_charges = new BigDecimal(0);
			BigDecimal total_current_credits = new BigDecimal(0);
			
			boolean has_credits = false;
			boolean any_charges_applied = false;
			boolean all_new_charges_applied = true;
			Vector order_lines = new Vector();
			Vector charge_order_lines = new Vector();
			Vector credit_order_lines = new Vector();
			//System.out.println("_request.getParameter(\"inputName\" + i + \"tblCharges\") != null >" + _request.getParameter("inputName" + i + "tblCharges"));
			for (int i = 1; (_request.getParameter("inputName" + i + "tblCharges") != null) || (_request.getParameter("inputName" + i + "tblCredits") != null); i++)
			{
				String charge_amount_str = _request.getParameter("inputName" + i + "tblCharges");
				if (charge_amount_str != null)
				{
					BigDecimal charge_amount = new BigDecimal(charge_amount_str);
					total_current_charges = total_current_charges.add(charge_amount);
					
					
					
					CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(_request.getParameter("hidden" + i + "tblCharges")));
					if (checkout_code_obj.getType() == CheckoutCodeBean.PLAN_TYPE)
					{
						// user is purchasing a plan
						PaymentPlanBean payment_plan = PaymentPlanBean.getPaymentPlan(checkout_code_obj);
						plans_being_purchased.addElement(payment_plan);
						plan_charges.put(payment_plan, charge_amount);
					}
					
					CheckoutOrderline order_line = new CheckoutOrderline();
					order_line.setPrice(charge_amount);
					order_line.setActualAmount(charge_amount);
					order_line.setCheckoutCodeId(checkout_code_obj.getId());
					order_line.setQuantity(1);
					if (_request.getParameter("inputCheck" + i + "tblCharges") != null)
					{
						total_charges_to_apply_credits_to = total_charges_to_apply_credits_to.add(charge_amount);
						order_line.setOrderstatus(OrderBean.CLOSED_ORDER_STATUS);
						any_charges_applied = true;
					}
					else
					{
						all_new_charges_applied = false;
						order_line.setOrderstatus(OrderBean.OPEN_ORDER_STATUS);
					}
					charge_order_lines.addElement(order_line);
					order_lines.addElement(order_line);
				}
				
				String credit_amount_str = _request.getParameter("inputName" + i + "tblCredits");
				if (credit_amount_str != null)
				{
					CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(_request.getParameter("hidden" + i + "tblCredits")));
					BigDecimal credit_amount = new BigDecimal("-" + credit_amount_str);
					
					total_current_credits = total_current_credits.add(new BigDecimal(credit_amount_str));
					
					CheckoutOrderline order_line = new CheckoutOrderline();
					order_line.setPrice(credit_amount);
					order_line.setActualAmount(credit_amount);
					order_line.setCheckoutCodeId(checkout_code_obj.getId());
					order_line.setQuantity(1);
					order_line.setOrderstatus(OrderBean.CLOSED_ORDER_STATUS);
					credit_order_lines.addElement(order_line);
					order_lines.addElement(order_line);
					
					has_credits = true;
				}
			}
			
			Vector selected_previous_open_orderlines = new Vector();
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
						selected_previous_open_orderlines.addElement(orderline);
					}
					else if (obj_list.size() == 0)
						throw new ObjectNotFoundException("Checkout Orderline not found for id :" + _request.getParameter("hiddeninputCheck" + (i + 1) + "tblPrevious"));
					else
						throw new UniqueObjectNotFoundException("Unique Checkout Orderline not found for id :" + _request.getParameter("hiddeninputCheck" + (i + 1) + "tblPrevious"));
					
					any_charges_applied = true;
				}
			}
			
			
			if (order_lines.size() > 0)
			{
				//OrderBean order = new OrderBean();
				adminOrder.setOrders(order_lines);
				adminOrder.setCompany(admin_company);
				adminOrder.setCreatePerson((UKOnlinePersonBean)loginBean.getPerson());
				adminOrder.setPerson(adminPerson);
				adminOrder.setOrderDate(new Date());
				if (all_new_charges_applied)
					adminOrder.setStatus(OrderBean.CLOSED_ORDER_STATUS);
				else
					adminOrder.setStatus(OrderBean.OPEN_ORDER_STATUS);
				
				if (total_charges_to_apply_credits_to.compareTo(total_current_credits) == 1)
					throw new IllegalValueException(total_current_credits.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " cannot be applied to total charges of " + total_charges_to_apply_credits_to.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

				if (has_credits && !any_charges_applied)
					throw new IllegalValueException("You must select a charge to apply the " + total_current_credits.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " to.");
				
				adminOrder.save();
				
				// mark the appointment as checked out...
			}
			else
				throw new IllegalValueException("No charges or credits entered.");
			
			
			
			Iterator previous_orderline_itr = selected_previous_open_orderlines.iterator();
			while (previous_orderline_itr.hasNext())
			{
				CheckoutOrderline orderline = (CheckoutOrderline)previous_orderline_itr.next();
				orderline.setOrderstatus(OrderBean.CLOSED_ORDER_STATUS);
				
				// I need to possibly close the order here too?
				// do I need to track the credit that i
			}
			
			
			Iterator plans_being_purchased_itr = plans_being_purchased.iterator();
			while (plans_being_purchased_itr.hasNext())
			{
				PaymentPlanBean payment_plan = (PaymentPlanBean)plans_being_purchased_itr.next();
				
				PaymentPlanInstanceBean payment_plan_instance = new PaymentPlanInstanceBean();
				payment_plan_instance.setPaymentPlan(payment_plan);
				payment_plan_instance.setPerson(adminPerson);
				Calendar now = Calendar.getInstance();
				payment_plan_instance.setStartDate(now.getTime());
				
				BigDecimal amount_paid_for_plan = (BigDecimal)plan_charges.get(payment_plan);
				
				payment_plan_instance.setAmountPaid(amount_paid_for_plan);
				payment_plan_instance.setGroupPoolingAllowed(payment_plan.isGroupPoolingAllowed());
				payment_plan_instance.setCreatePerson((UKOnlinePersonBean)loginBean.getPerson());
				payment_plan_instance.activate();
				payment_plan_instance.save();
				
				// create a payment plan instance for this person
			}
			
			
				
			AppointmentBean appointment = (AppointmentBean)session.getAttribute("adminAppointment");
			if ((appointment != null) && !appointment.isNew())
			{
				appointment.setState(AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
				appointment.setCreateOrModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
				appointment.save();
			}
			
			session.removeAttribute("adminOrder");
			 */
		}
		catch (Exception x)
		{
			x.printStackTrace();
			session.setAttribute("error-message", x.getMessage());
			forwardString = "failure";
		}

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
}
