/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;


import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.tasks.RefundChargeTask;
import com.badiyan.uk.online.tasks.VoidChargeTask;
import com.valeo.qbms.data.QBMSCreditCardResponse;


import com.valeo.qbpos.data.TenderRet;
import java.util.Enumeration;
import java.util.Timer;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
TenderDetailAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in TenderDetailAction");
		
		
		Enumeration enumx = _request.getParameterNames();
		while (enumx.hasMoreElements())
		{
			String param_name = (String)enumx.nextElement();
			System.out.println(param_name + " >" + _request.getParameter(param_name));
		}

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;

		UKOnlineCompanyBean admin_company = null;
		TenderRet adminTender = null;

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

				admin_company = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				adminTender = (TenderRet)session.getAttribute("adminTender");
			}
			else
				return (_mapping.findForward("session_expired"));

			/*
			 *<form-bean       name="vendorForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="name" type="java.lang.String"/>
      <form-property name="street" type="java.lang.String"/>
      <form-property name="city" type="java.lang.String"/>
      <form-property name="state" type="java.lang.String"/>
      <form-property name="zip" type="java.lang.String"/>
      <form-property name="phone" type="java.lang.String"/>
      <form-property name="fax" type="java.lang.String"/>
      <form-property name="accountNumber" type="java.lang.String"/>
    </form-bean>
			 */

			System.out.println("_request.getParameter(\"delete_id\") >" + _request.getParameter("delete_id"));

			
			
			Integer tenderId = (Integer)PropertyUtils.getSimpleProperty(_form, "tenderId");
			String amount = (String)PropertyUtils.getSimpleProperty(_form, "amount");

			System.out.println("tenderId >" + tenderId);
			System.out.println("amount >" + amount);
			
			String submit_button = _request.getParameter("submit_button");
			
			
			if (submit_button.equals("refund"))
			{
				if (adminTender.getId() == tenderId)
				{
					QBMSCreditCardResponse charge_to_refund = QBMSCreditCardResponse.getResponse(adminTender);
					System.out.println("charge_to_refund >" + charge_to_refund);

					//Timer timerObj = new Timer(true);
					//timerObj.schedule(new RefundChargeTask(admin_company, session, charge_to_refund), (long)0);
				}
			}
			else if (submit_button.equals("void"))
			{
				if (adminTender.getId() == tenderId)
				{
					QBMSCreditCardResponse charge_to_void = QBMSCreditCardResponse.getResponse(adminTender);
					System.out.println("charge_to_void >" + charge_to_void);

					//Timer timerObj = new Timer(true);
					//timerObj.schedule(new VoidChargeTask(admin_company, session, charge_to_void), (long)0);
				}
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
		return (_mapping.findForward("success"));
	}
}
