/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;


import com.valeo.qb.data.VendorRet;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
InventoryDepartmentsAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in InventoryDepartmentsAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;

		UKOnlineCompanyBean admin_company = null;
		InventoryDepartment admin_inventory_department = null;

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

				admin_inventory_department = (InventoryDepartment)session.getAttribute("adminDepartment");
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

			if (_request.getParameter("delete_id").equals(""))
			{
			    String name = (String)PropertyUtils.getSimpleProperty(_form, "name");

			    System.out.println("name >" + name);

				if (name.equals(""))
					throw new IllegalValueException("Please specify a vendor name.");

				admin_inventory_department.setCompany(admin_company);

				admin_inventory_department.setName(name);

				admin_inventory_department.save();
			}
			else
			{
			    //UKOnlineCompanyBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
			}

			session.removeAttribute("adminDepartment");

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
