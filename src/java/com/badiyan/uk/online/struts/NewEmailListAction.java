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
NewEmailListAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in NewEmailListAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;

		UKOnlineCompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;
		EmailList adminEmailList = null;
		
		

		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)loginBean.getPerson();

				admin_company = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				adminEmailList = (EmailList)session.getAttribute("emailList");
			}
			else
				return (_mapping.findForward("session_expired"));

			/*
			 * <form-bean       name="newMarketingPlanForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="startDate" type="java.lang.String"/>
      <form-property name="endDate" type="java.lang.String"/>
      <form-property name="activeInput" type="java.lang.Boolean"/>
    </form-bean>
			 */

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			String startDate = (String)PropertyUtils.getSimpleProperty(_form, "startDate");
			String endDate = (String)PropertyUtils.getSimpleProperty(_form, "endDate");
			Boolean isBulkEmailInput = (Boolean)PropertyUtils.getSimpleProperty(_form, "isBulkEmailInput");

			System.out.println("nameInput >" + nameInput);

			/*
			adminMarketingPlan.setName(nameInput);

			if ((startDate != null) && (startDate.length() > 0))
				adminMarketingPlan.setStartDate(CUBean.getDateFromUserString(startDate));

			if ((endDate != null) && (endDate.length() > 0))
				adminMarketingPlan.setEndDate(CUBean.getDateFromUserString(endDate));

			if (isBulkEmailInput == null)
				adminMarketingPlan.setIsBulkEmail(false);
			else
				adminMarketingPlan.setIsBulkEmail(isBulkEmailInput.booleanValue());

			if ((nameInput == null) || (nameInput.length() == 0))
				throw new IllegalValueException("You must specify a name for the marketing plan.");

			adminMarketingPlan.setIsActive(true);
			adminMarketingPlan.setCompany(admin_company);
			adminMarketingPlan.save();
			*/

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
