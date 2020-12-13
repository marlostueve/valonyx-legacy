
package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.ParseException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

import com.badiyan.uk.conformance.aicc.rte.datamodel.*;

import com.badiyan.uk.online.tasks.*;

import java.io.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
DustToDiamondsAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute invoked in DustToDiamondsAction");
		
		String forward_string = "success";
		

		ActionErrors errors = new ActionErrors();

		
		HttpSession session = _request.getSession(false);
		
		

		try
		{
			// Check the session to see if there's a course in progress...

			

			
			
			/*
<form-bean       name="dustToDiamondsOrderForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="first" type="java.lang.String"/>
      <form-property name="last" type="java.lang.String"/>
      <form-property name="street" type="java.lang.String"/>
      <form-property name="city" type="java.lang.String"/>
      <form-property name="state" type="java.lang.String"/>
      <form-property name="zip" type="java.lang.String"/>
      <form-property name="email" type="java.lang.String"/>
      <form-property name="qty" type="java.lang.Integer"/>
      <form-property name="ccnumber" type="java.lang.String"/>
      <form-property name="name_on_card" type="java.lang.String"/>
      <form-property name="exp_month" type="java.lang.Integer"/>
      <form-property name="exp_year" type="java.lang.Integer"/>
      <form-property name="ccv" type="java.lang.String"/>
      <form-property name="select" type="java.lang.String"/>
    </form-bean>
			 */
			
			String first = (String)PropertyUtils.getSimpleProperty(_form, "first");
			String last = (String)PropertyUtils.getSimpleProperty(_form, "last");
			String street = (String)PropertyUtils.getSimpleProperty(_form, "street");
			String city = (String)PropertyUtils.getSimpleProperty(_form, "city");
			String state = (String)PropertyUtils.getSimpleProperty(_form, "state");
			String zip = (String)PropertyUtils.getSimpleProperty(_form, "zip");
			String email = (String)PropertyUtils.getSimpleProperty(_form, "email");
			Integer qty = (Integer)PropertyUtils.getSimpleProperty(_form, "qty");
			String ccnumber = (String)PropertyUtils.getSimpleProperty(_form, "ccnumber");
			String name_on_card = (String)PropertyUtils.getSimpleProperty(_form, "name_on_card");
			Integer exp_month = (Integer)PropertyUtils.getSimpleProperty(_form, "exp_month");
			Integer exp_year = (Integer)PropertyUtils.getSimpleProperty(_form, "exp_year");
			String ccv = (String)PropertyUtils.getSimpleProperty(_form, "ccv");
			String select = (String)PropertyUtils.getSimpleProperty(_form, "select");
			
			System.out.println("first >" + first);
			System.out.println("last >" + last);
			System.out.println("street >" + street);
			System.out.println("city >" + city);
			System.out.println("state >" + state);
			System.out.println("zip >" + zip);
			System.out.println("email >" + email);
			System.out.println("qty >" + qty);
			System.out.println("name_on_card >" + name_on_card);
			System.out.println("exp_month >" + exp_month);
			System.out.println("exp_year >" + exp_year);
			System.out.println("ccv >" + ccv);
			
			session.setAttribute("first", first);
			session.setAttribute("last", last);
			session.setAttribute("street", street);
			session.setAttribute("city", city);
			session.setAttribute("state", state);
			session.setAttribute("zip", zip);
			session.setAttribute("email", email);
			session.setAttribute("qty", qty);
			session.setAttribute("name_on_card", name_on_card);
			session.setAttribute("exp_month", exp_month);
			session.setAttribute("exp_year", exp_year);
			session.setAttribute("ccv", ccv);
			session.setAttribute("ccnumber", ccnumber);
			session.setAttribute("select", select);
			
			
			if (first.isEmpty()) {
				throw new IllegalValueException("Please provide your first name.");
			} else if (last.isEmpty()) {
				throw new IllegalValueException("Please provide your last name.");
			} else if (street.isEmpty()) {
				throw new IllegalValueException("Please provide your street address.");
			} else if (city.isEmpty()) {
				throw new IllegalValueException("Please provide your city.");
			} else if (state.isEmpty()) {
				throw new IllegalValueException("Please provide your state.");
			} else if (zip.isEmpty()) {
				throw new IllegalValueException("Please provide your zip code.");
			} else if (email.isEmpty()) {
				throw new IllegalValueException("Please provide your email address.");
			} else if (ccnumber.isEmpty()) {
				throw new IllegalValueException("Please provide your credit card number.");
			} else if (name_on_card.isEmpty()) {
				throw new IllegalValueException("Please provide the name that appears on your credit card.");
			} else if (ccv.isEmpty()) {
				throw new IllegalValueException("Please provide the credit card CCV number.  It's typically a 3 digit number found on the back of your card.");
			}
			
			
			StringBuffer b = new StringBuffer();
			
			b.append("first >" + first + "\r\n<br />");
			b.append("last >" + last + "\r\n<br />");
			b.append("street >" + street + "\r\n<br />");
			b.append("city >" + city + "\r\n<br />");
			b.append("state >" + state + "\r\n<br />");
			b.append("zip >" + zip + "\r\n<br />");
			b.append("email >" + email + "\r\n<br />");
			b.append("qty >" + qty + "\r\n<br />");
			b.append("name_on_card >" + name_on_card + "\r\n<br />");
			b.append("ccnumber >" + ccnumber + "\r\n<br />");
			b.append("exp_month >" + exp_month + "\r\n<br />");
			b.append("exp_year >" + exp_year + "\r\n<br />");
			b.append("ccv >" + ccv + "\r\n<br />");
			
			
			b.append("selected package >" + select + "\r\n");
			
			
			CUBean.sendEmail("marlo@badiyan.com", email, "Dust to Diamonds Book Order", b.toString());
			CUBean.sendEmail("cstueve@sanowc.com", email, "Dust to Diamonds Book Order", b.toString());
			CUBean.sendEmail("mstewart@sanowc.com", email, "Dust to Diamonds Book Order", b.toString());
			
			
		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			
			session.setAttribute("d2d-message", "<strong>Error:</strong> " + x.getMessage());
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