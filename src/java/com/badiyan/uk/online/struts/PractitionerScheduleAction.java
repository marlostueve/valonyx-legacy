/*
 * PractitionerScheduleAction.java - 08/08/08 11:08 AM
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
PractitionerScheduleAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in PractitionerScheduleAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

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
			
			
			if (!_request.getParameter("delete_id").equals("0"))
			{
				GroupUnderCareMemberTypeBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
			}
			else
			{
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
    </form-bean>
				 */
				
				String submitButtonStr = _request.getParameter("submitButton");
				System.out.println("submitButton >" + submitButtonStr);
				
				
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
			
				System.out.println("practitionerSelect >" + practitionerSelect);
				System.out.println("primarySchedule >" + primarySchedule);
				System.out.println("descriptionInput >" + descriptionInput);
				System.out.println("startDate >" + startDate);
				System.out.println("endDate >" + endDate);
				System.out.println("dayOfWeekSelect >" + dayOfWeekSelect);
				System.out.println("startHour >" + startHour);
				System.out.println("startMinute >" + startMinute);
				System.out.println("endHour >" + endHour);
				System.out.println("endMinute >" + endMinute);
				
				System.out.println("dfg jkhk>" + _request.getParameter("delete_item_id"));
				
				if (!_request.getParameter("delete_item_id").equals("0"))
					PractitionerScheduleItemBean.delete(Integer.parseInt(_request.getParameter("delete_item_id")));
				else if (!_request.getParameter("delete_id").equals("0"))
					PractitionerScheduleBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
				else if (submitButtonStr.indexOf("Details") == -1)
				{
					if (practitionerSelect.intValue() == 0)
						throw new IllegalValueException("You must select a practitioner.");
					
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(practitionerSelect.intValue());
						
					if (adminPractitionerSchedule.isNew())
						adminPractitionerSchedule.setPractitioner(practitioner);
					else
					{
						// am I changing practitioners?
						
						if (!practitioner.equals(adminPractitionerSchedule.getPractitioner()))
							throw new IllegalValueException("You cannot reassign this schedule from " + adminPractitionerSchedule.getPractitioner().getLabel() + " to " + practitioner.getLabel() + ".");
					}
					
					adminPractitionerSchedule.setCompany(admin_company);
					if ((primarySchedule != null) && primarySchedule.booleanValue())
					{
						if (adminPractitionerSchedule.isNew())
						{
							if (PractitionerScheduleBean.hasBaseSchedule(practitioner))
								throw new IllegalValueException(practitioner.getLabel() + " already has a primary schedule.");
						}

						adminPractitionerSchedule.setBaseSchedule(true);
						adminPractitionerSchedule.setName("Primary");
					}
					else
					{
						if (descriptionInput.equals(""))
							throw new IllegalValueException("You must provide a name for a non-primary practitioner schedule.");

						if (startDate.equals("") || endDate.equals(""))
							throw new IllegalValueException("You must provide a start and end date for a non-primary practitioner schedule.");

						adminPractitionerSchedule.setBaseSchedule(false);
						adminPractitionerSchedule.setName(descriptionInput);
						adminPractitionerSchedule.setStartDate(CUBean.getDateFromUserString(startDate));
						adminPractitionerSchedule.setEndDate(CUBean.getDateFromUserString(endDate));
					}
					
					session.removeAttribute("adminPractitionerSchedule");
					session.removeAttribute("adminPractitionerScheduleItem");
				}
				else
				{
					if (dayOfWeekSelect > -1)
					{
						// create a schedule item, I guess

						//PractitionerScheduleItemBean item_obj = new PractitionerScheduleItemBean();
						
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

						if (item_obj.isNew())
							adminPractitionerSchedule.addItem(item_obj);
						else
							item_obj.save();
						
						session.removeAttribute("adminPractitionerScheduleItem");
					}
					else
						throw new IllegalValueException("You must select a day of week.");
				}
				
				adminPractitionerSchedule.save();
				adminPractitionerSchedule.invalidate();
					
				PractitionerScheduleBean.invalidateHash();
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
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

