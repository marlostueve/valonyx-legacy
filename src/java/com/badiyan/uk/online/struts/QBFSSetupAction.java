/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;
import com.valeo.qb.QBXMLGenericQueryRequest;
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
QBFSSetupAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in QBFSSetupAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;

		UKOnlineCompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;

		QBXMLGenericQueryRequest adminQBXMLRequest = null;

		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)loginBean.getPerson();

				admin_company = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				adminQBXMLRequest = (QBXMLGenericQueryRequest)session.getAttribute("adminQBXMLRequest");
			}
			else
				return (_mapping.findForward("session_expired"));

			/*
			 *
    <form-bean       name="qbfsSetupForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="company_key" type="java.lang.String"/>
      <form-property name="sync_accounts" type="java.lang.Boolean"/>
      <form-property name="sync_vendors" type="java.lang.Boolean"/>
      <form-property name="sync_clients" type="java.lang.Boolean"/>
      <form-property name="sync_inventory" type="java.lang.Boolean"/>
    </form-bean>
			 */

			String company_key = (String)PropertyUtils.getSimpleProperty(_form, "company_key");
			
			/*
			Boolean sync_accounts = (Boolean)PropertyUtils.getSimpleProperty(_form, "sync_accounts");
			Boolean sync_vendors = (Boolean)PropertyUtils.getSimpleProperty(_form, "sync_vendors");
			Boolean sync_clients = (Boolean)PropertyUtils.getSimpleProperty(_form, "sync_clients");
			Boolean sync_inventory = (Boolean)PropertyUtils.getSimpleProperty(_form, "sync_inventory");
			 * 
			 */
			
			Boolean sync_accounts = new Boolean(false);
			Boolean sync_vendors = new Boolean(false);
			Boolean sync_clients = new Boolean(false);
			Boolean sync_inventory = new Boolean(false);

			QuickBooksSettings qb_settings = admin_company.getQuickBooksSettings();
			qb_settings.setCompanyKey(company_key);
			if (qb_settings.isNew())
			{
				qb_settings.setSyncAccounts((sync_accounts == null) ? false : sync_accounts.booleanValue());
				qb_settings.setSyncClients((sync_clients == null) ? false : sync_clients.booleanValue());
				qb_settings.setSyncInventory((sync_inventory == null) ? false : sync_inventory.booleanValue());
				qb_settings.setSyncVendors((sync_vendors == null) ? false : sync_vendors.booleanValue());
			}
			qb_settings.save();

			// do I want to put something in the queue now?

			//qb_settings.is

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
		return (_mapping.findForward(forwardString));
	}
}

