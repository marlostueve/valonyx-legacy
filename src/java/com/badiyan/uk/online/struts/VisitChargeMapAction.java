/*
 * VisitChargeMapAction.java - 6/29/08 1:13 PM
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

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 * Implementation of <strong>Action</strong> that validates a client profile.
 *
 * @author Marlo Stueve
 */
public final class
VisitChargeMapAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in VisitChargeMapAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;
		
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)loginBean.getPerson();
				
				admin_company = (CompanyBean)session.getAttribute("adminCompany");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			
			if (!_request.getParameter("practice_area_delete_id").equals("0") && !_request.getParameter("file_type_delete_id").equals("0"))
			{
				Criteria crit = new Criteria();
				crit.add(GroupUnderCareMemberPracticeAreaVisitChargePeer.GROUP_UNDER_CARE_MEMBER_TYPE_ID, Integer.parseInt(_request.getParameter("file_type_delete_id")));
				crit.add(GroupUnderCareMemberPracticeAreaVisitChargePeer.PRACTICE_AREA_ID, Integer.parseInt(_request.getParameter("practice_area_delete_id")));
				System.out.println("");
				System.out.println("");
				System.out.println("");
				System.out.println("crit >" + crit.toString());
				GroupUnderCareMemberPracticeAreaVisitChargePeer.doDelete(crit);
			}
			else
			{
				Integer fileTypeSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "fileTypeSelect");
				Integer practiceAreaSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "practiceAreaSelect");
				String amountInput = (String)PropertyUtils.getSimpleProperty(_form, "amountInput");
			
				System.out.println("fileTypeSelect >" + fileTypeSelect);
				System.out.println("practiceAreaSelect >" + practiceAreaSelect);
				System.out.println("amountInput >" + amountInput);

				if (amountInput.length() == 0)
					throw new IllegalValueException("Please specifiy a visit charge.");
				
				GroupUnderCareMemberTypeBean file_type = GroupUnderCareMemberTypeBean.getMemberType(fileTypeSelect.intValue());
				PracticeAreaBean practice_area = PracticeAreaBean.getPracticeArea(practiceAreaSelect.intValue());
				BigDecimal visit_charge = new BigDecimal(amountInput);
				
				file_type.setVisitCharge(practice_area, visit_charge);
				

				/*
				file_type.setCompany(admin_company);
				file_type.setName(descriptionInput);
				
				if (amountInput.length() > 0)
				{
					BigDecimal amount = new BigDecimal(amountInput);
					file_type.setChiropracticVisitCharge(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				
				file_type.save();
				  */
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

