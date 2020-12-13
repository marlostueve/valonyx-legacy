/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.*;
import java.math.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a client profile.
 *
 * @author Marlo Stueve
 */
public final class
NewClientReviewReasonAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in NewClientReviewReasonAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;

		CompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;

		ReviewReason review_reason = null;

		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)loginBean.getPerson();

				admin_company = (CompanyBean)session.getAttribute("adminCompany");

				review_reason = (ReviewReason)session.getAttribute("adminReviewReason");
			}
			else
				return (_mapping.findForward("session_expired"));


			if (!_request.getParameter("delete_id").equals("0"))
			{
				GroupUnderCareMemberTypeBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
			}
			else
			{
				String descriptionInput = (String)PropertyUtils.getSimpleProperty(_form, "descriptionInput");
				//String amountInput = (String)PropertyUtils.getSimpleProperty(_form, "amountInput");

				System.out.println("descriptionInput >" + descriptionInput);
				//System.out.println("amountInput >" + amountInput);

				if (descriptionInput.length() == 0)
					throw new IllegalValueException("Please specifiy a name.");

				review_reason.setCompany(admin_company);
				review_reason.setName(descriptionInput);
				/*
				if (amountInput.length() > 0)
				{
					BigDecimal amount = new BigDecimal(amountInput);
					file_type.setChiropracticVisitCharge(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				 */
				review_reason.save();
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
			//errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			session.setAttribute("error-message", x.getMessage());
			forwardString = "failure";
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
		return (_mapping.findForward(forwardString));
	}
}

