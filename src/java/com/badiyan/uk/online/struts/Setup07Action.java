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
Setup07Action
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in Setup07Action");

		ActionErrors errors = new ActionErrors();
		String forward_string = "success";

		UKOnlineLoginBean loginBean = null;

		CompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;

		PractitionerScheduleBean adminPractitionerSchedule = null;
		PractitionerScheduleItemBean item_obj = null;

		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)loginBean.getPerson();

				admin_company = (CompanyBean)session.getAttribute("adminCompany");

				adminPractitionerSchedule = (PractitionerScheduleBean)session.getAttribute("adminPractitionerSchedule");
				item_obj = (PractitionerScheduleItemBean)session.getAttribute("adminPractitionerScheduleItem");
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

			/*<form-bean       name="practitionerScheduleForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="practitionerSelect" type="java.lang.Integer"/>
      <form-property name="primarySchedule" type="java.lang.Boolean"/>
      <form-property name="descriptionInput" type="java.lang.String"/>
      <form-property name="startDate" type="java.lang.String"/>
      <form-property name="endDate" type="java.lang.String"/>
      <form-property name="dayOfWeekSelect" type="java.lang.Integer"/>
      <form-property name="startHour" type="java.lang.Integer"/>
      <form-property name="startMinute" type="java.lang.Integer"/>
      <form-property name="endHour" type="java.lang.Integer"/>
      <form-property name="endMinute" type="java.lang.Integer"/>
      <form-property name="appointmentTypeSelect" type="java.lang.Integer"/>
    </form-bean>
				 */


			if ((_request.getParameter("delete_id") != null) && (((String)_request.getParameter("delete_id")).length() > 0) && (Integer.parseInt(_request.getParameter("delete_id")) > 0))
			{
				PractitionerScheduleBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
				adminPractitionerSchedule.save();
				adminPractitionerSchedule.invalidate();
				PractitionerScheduleBean.invalidateHash();

				forward_string = "add";
			}
			else if ((_request.getParameter("delete_item_id") != null) && (((String)_request.getParameter("delete_item_id")).length() > 0) && (Integer.parseInt(_request.getParameter("delete_item_id")) > 0))
			{
				PractitionerScheduleItemBean.delete(Integer.parseInt(_request.getParameter("delete_item_id")));
				adminPractitionerSchedule.save();
				adminPractitionerSchedule.invalidate();
				PractitionerScheduleBean.invalidateHash();

				forward_string = "add";
			}
			else if (submit_button.equals("Add Schedule") || submit_button.equals("Add Schedule Details"))
			{
				Integer practitionerSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "practitionerSelect");
				Boolean primarySchedule = (Boolean)PropertyUtils.getSimpleProperty(_form, "primarySchedule");
				String descriptionInput = (String)PropertyUtils.getSimpleProperty(_form, "descriptionInput");
				String startDate = (String)PropertyUtils.getSimpleProperty(_form, "startDate");
				String endDate = (String)PropertyUtils.getSimpleProperty(_form, "endDate");
				Integer dayOfWeekSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "dayOfWeekSelect");
				Integer startHour = (Integer)PropertyUtils.getSimpleProperty(_form, "startHour");
				Integer startMinute = (Integer)PropertyUtils.getSimpleProperty(_form, "startMinute");
				Integer startAMPMSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "startAMPMSelect");
				Integer endHour = (Integer)PropertyUtils.getSimpleProperty(_form, "endHour");
				Integer endMinute = (Integer)PropertyUtils.getSimpleProperty(_form, "endMinute");
				Integer endAMPMSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "endAMPMSelect");
				Integer appointmentTypeSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "appointmentTypeSelect");

				System.out.println("practitionerSelect >" + practitionerSelect);
				System.out.println("appointmentTypeSelect >" + appointmentTypeSelect);
				System.out.println("primarySchedule >" + primarySchedule);
				System.out.println("descriptionInput >" + descriptionInput);
				System.out.println("startDate >" + startDate);
				System.out.println("endDate >" + endDate);
				System.out.println("dayOfWeekSelect >" + dayOfWeekSelect);
				System.out.println("startHour >" + startHour);
				System.out.println("startMinute >" + startMinute);
				System.out.println("endHour >" + endHour);
				System.out.println("endMinute >" + endMinute);

				if (submit_button.equals("Add Schedule"))
				{
					UKOnlinePersonBean practitioner = null;
					if (practitionerSelect.intValue() != 0)
						practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(practitionerSelect.intValue());

					if (adminPractitionerSchedule.isNew() && (practitioner != null))
						adminPractitionerSchedule.setPractitioner(practitioner);

					adminPractitionerSchedule.setCompany(admin_company);

					/*
					if ((primarySchedule != null) && primarySchedule.booleanValue())
					{
						adminPractitionerSchedule.setBaseSchedule(true);
						adminPractitionerSchedule.setName("Primary");
						adminPractitionerSchedule.setStartDate(null);
						adminPractitionerSchedule.setEndDate(null);
					}
					else
					{
						adminPractitionerSchedule.setBaseSchedule(false);
						adminPractitionerSchedule.setName(descriptionInput);
						adminPractitionerSchedule.setStartDate(CUBean.getDateFromUserString(startDate));
						adminPractitionerSchedule.setEndDate(CUBean.getDateFromUserString(endDate));
					}
					 */

					if (adminPractitionerSchedule.isNew())
						adminPractitionerSchedule.setBaseSchedule(false);

					if (!adminPractitionerSchedule.isBaseSchedule())
					{
						adminPractitionerSchedule.setName(descriptionInput);

						if (startDate.length() > 0)
						{
							try
							{
								adminPractitionerSchedule.setStartDate(CUBean.getDateFromUserString(startDate));
							}
							catch (java.text.ParseException x)
							{
							}
						}

						if (endDate.length() > 0)
						{
							try
							{
								adminPractitionerSchedule.setEndDate(CUBean.getDateFromUserString(endDate));
							}
							catch (java.text.ParseException x)
							{
							}
						}
					}


					if (practitionerSelect.intValue() == 0)
						throw new IllegalValueException("You must select a practitioner.");

					if (!adminPractitionerSchedule.isNew())
					{
						// am I changing practitioners?

						if (!practitioner.equals(adminPractitionerSchedule.getPractitioner()))
							throw new IllegalValueException("You cannot reassign this schedule from " + adminPractitionerSchedule.getPractitioner().getLabel() + " to " + practitioner.getLabel() + ".");
					}

					if (adminPractitionerSchedule.isBaseSchedule())
					{
						if (adminPractitionerSchedule.isNew())
						{
							if (PractitionerScheduleBean.hasBaseSchedule(practitioner))
							{
								adminPractitionerSchedule.setName("");
								throw new IllegalValueException(practitioner.getLabel() + " already has a primary schedule.");
							}
						}
					}
					else
					{
						if (descriptionInput.equals(""))
							throw new IllegalValueException("You must provide a name for this practitioner schedule.");

						if (startDate.equals("") || endDate.equals(""))
							throw new IllegalValueException("You must provide a start and end date for this practitioner schedule.");
					}

					session.removeAttribute("adminPractitionerSchedule");
					session.removeAttribute("adminPractitionerScheduleItem");
					
				} else {

					if (dayOfWeekSelect > -1)
						item_obj.setDayOfWeek(dayOfWeekSelect.intValue());
					if (startAMPMSelect == Calendar.PM)
						item_obj.setStartHourOfDay(startHour.intValue() + 12);
					else
						item_obj.setStartHourOfDay(startHour.intValue());
					item_obj.setStartMinute(startMinute.intValue());

					if (endAMPMSelect == Calendar.PM)
						item_obj.setEndHourOfDay(endHour.intValue() + 12);
					else
						item_obj.setEndHourOfDay(endHour.intValue());
					item_obj.setEndMinute(endMinute.intValue());
					
					if (appointmentTypeSelect != null && appointmentTypeSelect > 0) {
						item_obj.setAppointmentTypeTemplate(AppointmentTypeBean.getAppointmentType(appointmentTypeSelect));
					}


					if (dayOfWeekSelect < 0)
						throw new IllegalValueException("You must select a day of week.");

					if (startAMPMSelect.intValue() == Calendar.PM && endAMPMSelect.intValue() == Calendar.AM)
						throw new IllegalValueException("Start Time must be before End Time.");
					else if (startAMPMSelect.intValue() == endAMPMSelect.intValue())
					{
						if (startHour.shortValue() > endHour.shortValue())
							throw new IllegalValueException("Start Time must be before End Time.");
						else if (startHour.shortValue() == endHour.shortValue())
						{
							if (startMinute.shortValue() > endMinute.shortValue())
								throw new IllegalValueException("Start Time must be before End Time.");
							else if (startMinute.shortValue() == endMinute.shortValue())
								throw new IllegalValueException("Start Time must be before End Time.");
						}
					}

					CompanySettingsBean settings = admin_company.getSettings();

					short open_hour = 7;
					short open_minute = 0;
					int open_ampm = Calendar.AM;
					short close_hour = 8;
					short close_minute = 0;
					int close_ampm = Calendar.PM;

					if (settings.getOpenHour() > (short)0)
					{
						open_hour = (settings.getOpenHour() > 12) ? (short)(settings.getOpenHour() - 12) : settings.getOpenHour();
						open_minute = settings.getOpenMinute();
						open_ampm = (settings.getOpenHour() > 12) ? Calendar.PM : Calendar.AM;

						close_hour = (settings.getCloseHour() > 12) ? (short)(settings.getCloseHour() - 12) : settings.getCloseHour();
						close_minute = settings.getCloseMinute();
						close_ampm = (settings.getCloseHour() > 12) ? Calendar.PM : Calendar.AM;
					}

					// selected start time must be after the practice open time

					if (open_ampm == Calendar.PM && startAMPMSelect.intValue() == Calendar.AM)
						throw new IllegalValueException("Practitioner work hours cannot start before your Practice opens.");
					else if (open_ampm == startAMPMSelect.intValue())
					{
						if (open_hour > startHour.shortValue())
							throw new IllegalValueException("Practitioner work hours cannot start before your Practice opens.");
						else if (open_hour == startHour.shortValue())
						{
							if (open_minute > startMinute.shortValue())
								throw new IllegalValueException("Practitioner work hours cannot start before your Practice opens.");
						}
					}

					// selected end time must be before the practice close time

					if (endAMPMSelect.intValue() == Calendar.PM && close_ampm == Calendar.AM)
						throw new IllegalValueException("Practitioner work hours cannot end after your Practice closes.");
					else if (endAMPMSelect.intValue() == close_ampm)
					{
						if (endHour.shortValue() > close_hour)
							throw new IllegalValueException("Practitioner work hours cannot end after your Practice closes.");
						else if (endHour.shortValue() == close_hour)
						{
							if (endMinute.shortValue() > close_minute)
								throw new IllegalValueException("Practitioner work hours cannot end after your Practice closes.");
						}
					}


					if (item_obj.isNew()) {
						adminPractitionerSchedule.addItem(item_obj);
					} else {
						item_obj.save();
					}

					session.removeAttribute("adminPractitionerScheduleItem");
					
				}

				adminPractitionerSchedule.save();
				adminPractitionerSchedule.invalidate();

				PractitionerScheduleBean.invalidateHash();
				AppointmentBean.invalidateHash();
				
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
			else if (submit_button.equals("finish"))
			{
				CompanySettingsBean settings = admin_company.getSettings();
				settings.setIsSetupComplete(true);
				settings.save();

				forward_string = "finish";
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			session.setAttribute("error-message", x.getMessage());
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


