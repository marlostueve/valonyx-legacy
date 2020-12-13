/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;



import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.online.beans.*;
import com.valeo.qb.data.ItemLineRet;
import com.valeo.qb.data.ItemReceiptRet;
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
PurchaseOrderReceiveAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in PurchaseOrderReceiveAction");

		ActionErrors errors = new ActionErrors();

		CashOut cashOut = null;
		UKOnlineLoginBean loginBean = null;

		PurchaseOrder purchaseOrder;
		ItemReceiptRet item_receipt;
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
				item_receipt = (ItemReceiptRet)session.getAttribute("adminItemReceipt");
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


			// create ItemReceiptRet and ItemLineRet objects for the items received

			boolean tax_valid = true;
			boolean shipping_valid = true;
			boolean discount_valid = true;



			BigDecimal tax = CUBean.zero;
			try
			{
				tax = new BigDecimal(_request.getParameter("taxInput"));
			}
			catch (NumberFormatException x) { tax_valid = false; }

			BigDecimal shipping = CUBean.zero;
			try
			{
				shipping = new BigDecimal(_request.getParameter("shippingInput"));
			}
			catch (NumberFormatException x) { shipping_valid = false; }

			BigDecimal discount = CUBean.zero;
			try
			{
				discount = new BigDecimal(_request.getParameter("discountInput"));
			}
			catch (NumberFormatException x) { discount_valid = false; }


			BigDecimal receive_total = CUBean.zero;
			
			item_receipt.setPurchaseOrder(purchaseOrder);
			if (tax_valid)
			{
				item_receipt.setTaxAmount(tax.floatValue());
				receive_total = receive_total.add(tax);
			}
			if (shipping_valid)
			{
				item_receipt.setShippingAmount(shipping.floatValue());
				receive_total = receive_total.add(shipping);
			}
			if (discount_valid)
			{
				item_receipt.setDiscountAmount(discount.floatValue());
				receive_total = receive_total.subtract(discount);
			}


			Vector items = new Vector();
			String error_message = null;
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
			item_receipt.setItems(items);



			if (!tax_valid)
				throw new IllegalValueException("Invalid tax >" + _request.getParameter("taxInput"));
			if (!shipping_valid)
				throw new IllegalValueException("Invalid shipping >" + _request.getParameter("shippingInput"));
			if (!discount_valid)
				throw new IllegalValueException("Invalid discount >" + _request.getParameter("discountInput"));

			if (error_message != null)
				throw new IllegalValueException(error_message);

			item_receipt.setTotalAmount(receive_total.floatValue());
			item_receipt.save();
			
			BigDecimal remaining_quantity_to_be_received = purchaseOrder.getRemainingQuantityToBeReceived();
			if (remaining_quantity_to_be_received.compareTo(CUBean.zero) == 0)
			{
				purchaseOrder.setStatus(PurchaseOrder.COMPLETE_STATUS);
				purchaseOrder.save();
			}
			
			Iterator itr = items.iterator();
			while (itr.hasNext())
			{
				ItemLineRet line = (ItemLineRet)itr.next();
				CheckoutCodeBean checkout_code = line.getCheckoutCode();
				short existing_qty = checkout_code.getOnHandQuantity();
				short qty_received = line.getQuantity().shortValue();
				
				checkout_code.setOnHandQuantity((short)(existing_qty + qty_received));
				checkout_code.save();
			}

			session.removeAttribute("adminItemReceipt");

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