/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
Setup05Action
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in Setup05Action");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;

		CompanyBean admin_company = null;
		AppointmentTypeBean appointment_type = null;

		HttpSession session = _request.getSession(false);

		String forward_string = "success";

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
				appointment_type = (AppointmentTypeBean)session.getAttribute("adminAppointmentType");
			}
			else
				return (_mapping.findForward("session_expired"));

			String submit_button = _request.getParameter("submit_button");

			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements())
			{
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));
			}

			/*
			 *<form-bean       name="appointmentTypesForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="duration" type="java.lang.String"/>
      <form-property name="image_url" type="java.lang.String"/>
      <form-property name="bg_color" type="java.lang.String"/>
      <form-property name="text_color" type="java.lang.String"/>
      <form-property name="allow_double_booking" type="java.lang.Boolean"/>
      <form-property name="allow_off_hours" type="java.lang.Boolean"/>
      <form-property name="allow_client_scheduling" type="java.lang.Boolean"/>
      <form-property name="is_active" type="java.lang.Boolean"/>
      <form-property name="appointment_type_type" type="java.lang.Short"/>
      <form-property name="practice_area" type="java.lang.Integer"/>
    </form-bean>
			 */

			if ((_request.getParameter("delete_id") != null) && (((String)_request.getParameter("delete_id")).length() > 0) && (Integer.parseInt(_request.getParameter("delete_id")) > 0))
			{
				// is this appointment type in use?

				int appointment_type_id = Integer.parseInt(_request.getParameter("delete_id"));
				if (AppointmentBean.getAppointments(AppointmentTypeBean.getAppointmentType(appointment_type_id)).size() > 0)
					throw new IllegalValueException("Unable to delete the appointment type.  There are appointments that use this type.  You can inactivate this type to prevent it from appearing for scheduling.");
				AppointmentTypeBean.delete(appointment_type_id);
				forward_string = "add";

				session.removeAttribute("adminAppointmentType");
			}
			else if (submit_button.equals("Add Appointment Type"))
			{
			    String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			    String duration = (String)PropertyUtils.getSimpleProperty(_form, "duration");
			    String image_url = (String)PropertyUtils.getSimpleProperty(_form, "image_url");
			    Boolean allow_double_booking = (Boolean)PropertyUtils.getSimpleProperty(_form, "allow_double_booking");
				Boolean allow_off_hours = (Boolean)PropertyUtils.getSimpleProperty(_form, "allow_double_booking");
				Boolean allow_client_scheduling = (Boolean)PropertyUtils.getSimpleProperty(_form, "allow_client_scheduling");
				
			    Boolean is_active = (Boolean)PropertyUtils.getSimpleProperty(_form, "is_active");
				String bg_color = _request.getParameter("yui-picker-hex");
			    String text_color = (String)PropertyUtils.getSimpleProperty(_form, "text_color");
			    Short appointment_type_type = (Short)PropertyUtils.getSimpleProperty(_form, "appointment_type_type");
			    Integer practice_area = (Integer)PropertyUtils.getSimpleProperty(_form, "practice_area");

			    System.out.println("nameInput >" + nameInput);
			    System.out.println("duration >" + duration);
			    System.out.println("image_url >" + image_url);
			    System.out.println("bg_color >" + bg_color);
			    System.out.println("text_color >" + text_color);
			    System.out.println("appointment_type_type >" + appointment_type_type);
			    System.out.println("practice_area >" + practice_area);

			    appointment_type.setCompany(admin_company);
			    appointment_type.setName(nameInput);
			    appointment_type.setImageUrl(image_url);
			    appointment_type.setBGColorCode(bg_color);
			    appointment_type.setTextColorCode(text_color);
			    if (appointment_type_type.shortValue() != (short)0)
					appointment_type.setType(appointment_type_type.shortValue());
			    appointment_type.setPracticeAreaId(practice_area.intValue());

				appointment_type.setDoubleBookingAllowed((allow_double_booking != null) && allow_double_booking.booleanValue());
				appointment_type.setAllowClientScheduling((allow_client_scheduling != null) && allow_client_scheduling.booleanValue());
				appointment_type.setActive((is_active != null) && is_active.booleanValue());

				if (nameInput.length() == 0)
					throw new IllegalValueException("You must specify a Type Name.");

			    try
			    {
					appointment_type.setDuration(Short.parseShort(duration));
			    }
			    catch (Exception x)
			    {
					//throw new IllegalValueException("You must specify a duration.");
			    }

				if (bg_color.toUpperCase().equals("FFFFFF"))
					throw new IllegalValueException("Please specify a color for this Appointment Type other than white.");

			    if (appointment_type_type.shortValue() == (short)0)
					throw new IllegalValueException("You must specify a Type.");

			    if (practice_area.intValue() == 0)
					throw new IllegalValueException("You must specify a Practice Area.");

			    appointment_type.save();
				
				appointment_type.invalidate();
				
				if ((allow_off_hours != null) && allow_off_hours.booleanValue()) {
					// get the off-hours appointment type for this practice.  may not already be created...
					
					try {
						
						Vector offtime_appointments = AppointmentTypeBean.getAppointmentTypes(admin_company, AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE);
						//System.out.println("num offtime_appointments >" + offtime_appointments.size());
						if (offtime_appointments.size() == 1) {
							AppointmentTypeBean offHoursAppointmentType = (AppointmentTypeBean)offtime_appointments.get(0);
							Vector allowed = offHoursAppointmentType.getAllowedAppointmentTypes();
							allowed.addElement(appointment_type);
							offHoursAppointmentType.setAllowedAppointmentTypes(allowed);
							offHoursAppointmentType.save();
						}
						else
							throw new IllegalValueException("Offtime Appointment Type not found.");
						
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}

			    session.removeAttribute("adminAppointmentType");
				forward_string = "add";
			}
			else if (submit_button.equals("next"))
			{
				forward_string = "next";
			}
			else if (submit_button.equals("previous"))
			{
				forward_string = "previous";
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
