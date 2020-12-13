/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;


import com.badiyan.torque.*;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import com.valeo.qb.data.VendorRet;
import java.math.BigDecimal;

import java.util.*;

import javax.servlet.http.*;

import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
PurchaseOrderAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in PurchaseOrderAction");

		ActionErrors errors = new ActionErrors();

		CashOut cashOut = null;
		UKOnlineLoginBean loginBean = null;

		PurchaseOrder purchaseOrder;
		UKOnlineCompanyBean adminCompany;

		HttpSession session = _request.getSession(false);

		String forward_string = "success";

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				cashOut = (CashOut)session.getAttribute("cashOut");
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");

				purchaseOrder = (PurchaseOrder)session.getAttribute("adminPurchaseOrder");
				adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
			}
			else
				return (_mapping.findForward("session_expired"));


			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements())
			{
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));
			}

			String submit_button = _request.getParameter("submit_button");
			String submit_button2 = _request.getParameter("submit_button2");
			String submit_button3 = _request.getParameter("submit_button3");
			String submit_button4 = _request.getParameter("submit_button4");
			String submit_button5 = _request.getParameter("submit_button5");
			String delete_item_id = _request.getParameter("delete_item_id");
			String delete_id = _request.getParameter("delete_id");



			boolean invalid_number = false;
			boolean invalid_date = false;
			boolean invalid_vendor = false;


			try
			{
				int purchase_order_number = Integer.parseInt(_request.getParameter("purhaseOrderNumberInput"));
				purchaseOrder.setPurchaseOrderNumber(purchase_order_number);
			}
			catch (Exception x)
			{
				invalid_number = true;
			}

			try
			{
				Date purchase_order_date = CUBean.getDateFromUserString(_request.getParameter("dateInput"));
				purchaseOrder.setPurchaseOrderDate(purchase_order_date);
			}
			catch (Exception x)
			{
				invalid_date = true;
			}


			int vendor_id = Integer.parseInt(_request.getParameter("vendorSelect"));
			if (vendor_id == 0)
				invalid_vendor = true;
			else
				purchaseOrder.setVendor(VendorRet.getVendor(vendor_id));


			purchaseOrder.setNote(_request.getParameter("noteInput"));


			System.out.println("delete_item_id >" + delete_item_id);
			if (delete_item_id != null && delete_item_id.length() > 0)
			{
				Vector items = purchaseOrder.getItems();
				items.removeElementAt(Integer.parseInt(delete_item_id));
				purchaseOrder.setItems(items);

				if (!purchaseOrder.isNew())
					purchaseOrder.save();
			}
			else if (submit_button4 != null)
			{
				// print
				//forward_string = "print";
			}
			else if (submit_button2 != null)
			{
				// add item

				Vector items = purchaseOrder.getItems();

				PurchaseOrderItemMapping item = new PurchaseOrderItemMapping();
				
				item.setRowUpdated(new Date());

				CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(Integer.parseInt(_request.getParameter("codeSelect")));
				item.setCheckoutCodeId(checkout_code.getId());

				BigDecimal qty;
				try
				{
					qty = new BigDecimal(_request.getParameter("qty"));
				}
				catch (NumberFormatException x)
				{
					if (_request.getParameter("qty").length() == 0)
						throw new IllegalValueException("You must provide a quantity to add an item.");
					throw new IllegalValueException("Invalid quantity >" + _request.getParameter("qty"));
				}

				BigDecimal rate;
				try
				{
					rate = new BigDecimal(_request.getParameter("rate"));
				}
				catch (NumberFormatException x)
				{
					if (_request.getParameter("rate").length() == 0)
						throw new IllegalValueException("You must provide a rate to add an item.");
					throw new IllegalValueException("Invalid rate >" + _request.getParameter("rate"));
				}
				
				BigDecimal amount = qty.multiply(rate);

				item.setQuantity(qty);
				item.setRate(rate);
				item.setAmount(amount);
				item.setQuantityReceived(BigDecimal.ZERO);

				items.addElement(item);
				purchaseOrder.setItems(items);

				if (!purchaseOrder.isNew())
					purchaseOrder.save();

				if (checkout_code.getOrderCostString().equals(""))
				{
					// no order cost set.  set it to the given rate

					checkout_code.setOrderCost(rate);
					checkout_code.save();
				}
			}
			else if (submit_button3 != null)
			{
				// delete po

				PurchaseOrder.delete(adminCompany, Integer.parseInt(delete_id));
			}
			else
			{
				if (invalid_number)
					throw new IllegalValueException("Invalid purchase order number.");

				if (invalid_date)
					throw new IllegalValueException("Invalid purchase order date.");

				if (invalid_vendor)
					throw new IllegalValueException("You must select a vendor.");

				purchaseOrder.setTax(BigDecimal.ZERO);
				purchaseOrder.setShipping(BigDecimal.ZERO);
				purchaseOrder.setDiscount(BigDecimal.ZERO);
				//purchaseOrder.setTotal(BigDecimal.ZERO); // commented out 2/17 huh?!?!

				purchaseOrder.setCompany(adminCompany);
				purchaseOrder.save();
			}



		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty())
		{
			saveErrors(_request, errors);
			return _mapping.getInputForward();
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
		return (_mapping.findForward(forward_string));
	}
}