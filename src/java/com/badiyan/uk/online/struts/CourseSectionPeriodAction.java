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
 * Implementation of <strong>Action</strong> that does course section period stuff
 *
 * @author Marlo Stueve
 */
public final class
CourseSectionPeriodAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseSectionPeriodAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		CourseSectionBean section = null;
		CourseSectionPeriodBean period = null;
		
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
				
				adminCompany = (CompanyBean)session.getAttribute("adminCompany");
				section = (CourseSectionBean)session.getAttribute("adminCourseSection");
				period = (CourseSectionPeriodBean)session.getAttribute("adminCourseSectionPeriod");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-bean       name="courseSectionPeriodForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      
      <form-property name="period_start_time_hour" type="java.lang.Short"/>
      <form-property name="period_start_time_minute" type="java.lang.Short"/>
      <form-property name="period_start_time_am_pm" type="java.lang.Short"/>
      <form-property name="duration" type="java.lang.Short"/>
      <form-property name="duration_type" type="java.lang.Short"/>
      
      <form-property name="recurrence_pattern" type="java.lang.Short"/>
      <form-property name="recurrence_pattern_daily" type="java.lang.Short"/>
      <form-property name="recurrence_pattern_monthly" type="java.lang.Short"/>
      <form-property name="recurrence_pattern_yearly" type="java.lang.Short"/>
      
      <form-property name="recurrence_frequency_daily" type="java.lang.Short"/>
      
      <form-property name="recurrence_frequency_weekly" type="java.lang.Short"/>
      <form-property name="sunday" type="java.lang.Short"/>
      <form-property name="monday" type="java.lang.Short"/>
      <form-property name="tuesday" type="java.lang.Short"/>
      <form-property name="wednesday" type="java.lang.Short"/>
      <form-property name="thursday" type="java.lang.Short"/>
      <form-property name="friday" type="java.lang.Short"/>
      <form-property name="saturday" type="java.lang.Short"/>
      
      <form-property name="recurrence_day_of_month" type="java.lang.Short"/>
      <form-property name="recurrence_frequency_monthly" type="java.lang.Short"/>
      <form-property name="recurrence_month_sel" type="java.lang.Short"/>
      <form-property name="recurrence_month_week_day" type="java.lang.Short"/>
      <form-property name="recurrence_frequency_monthly_alt" type="java.lang.Short"/>
      
      <form-property name="recurrence_year_month" type="java.lang.Short"/>
      <form-property name="recurrence_year_date" type="java.lang.Short"/>
      <form-property name="recurrence_year_sel" type="java.lang.Short"/>
      <form-property name="recurrence_year_week" type="java.lang.Short"/>
      <form-property name="recurrence_year_month_sel" type="java.lang.Short"/>
      
      <form-property name="period_start_date" type="java.lang.String"/>
      <form-property name="period_end_date" type="java.lang.String"/>
      
      <form-property name="room" type="java.lang.Integer"/>
    </form-bean>
			 */

			Short period_start_time_hour = (Short)PropertyUtils.getSimpleProperty(_form, "period_start_time_hour");
			Short period_start_time_minute = (Short)PropertyUtils.getSimpleProperty(_form, "period_start_time_minute");
			Short period_start_time_am_pm = (Short)PropertyUtils.getSimpleProperty(_form, "period_start_time_am_pm");
			Short duration = (Short)PropertyUtils.getSimpleProperty(_form, "duration");
			Short duration_type = (Short)PropertyUtils.getSimpleProperty(_form, "duration_type");
			Short recurrence_pattern = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_pattern");
			Short recurrence_pattern_daily = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_pattern_daily");
			Short recurrence_pattern_monthly = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_pattern_monthly");
			Short recurrence_pattern_yearly = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_pattern_yearly");
			Short recurrence_frequency_daily = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_frequency_daily");
			Short recurrence_frequency_weekly = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_frequency_weekly");
			Boolean sunday = (Boolean)PropertyUtils.getSimpleProperty(_form, "sunday");
			Boolean monday = (Boolean)PropertyUtils.getSimpleProperty(_form, "monday");
			Boolean tuesday = (Boolean)PropertyUtils.getSimpleProperty(_form, "tuesday");
			Boolean wednesday = (Boolean)PropertyUtils.getSimpleProperty(_form, "wednesday");
			Boolean thursday = (Boolean)PropertyUtils.getSimpleProperty(_form, "thursday");
			Boolean friday = (Boolean)PropertyUtils.getSimpleProperty(_form, "friday");
			Boolean saturday = (Boolean)PropertyUtils.getSimpleProperty(_form, "saturday");
			Short recurrence_day_of_month = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_day_of_month");
			Short recurrence_frequency_monthly = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_frequency_monthly");
			Short recurrence_month_sel = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_month_sel");
			Short recurrence_month_week_day = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_month_week_day");
			Short recurrence_frequency_monthly_alt = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_frequency_monthly_alt");
			Short recurrence_year_month = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_year_month");
			Short recurrence_year_date = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_year_date");
			Short recurrence_year_sel = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_year_sel");
			Short recurrence_year_week = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_year_week");
			Short recurrence_year_month_sel = (Short)PropertyUtils.getSimpleProperty(_form, "recurrence_year_month_sel");
			
			String period_start_date = (String)PropertyUtils.getSimpleProperty(_form, "period_start_date");
			String period_end_date = (String)PropertyUtils.getSimpleProperty(_form, "period_end_date");
			
			Integer room = (Integer)PropertyUtils.getSimpleProperty(_form, "room");

			System.out.println("period_start_time_hour >" + period_start_time_hour);
			System.out.println("period_start_time_minute >" + period_start_time_minute);
			System.out.println("period_start_time_am_pm >" + period_start_time_am_pm);
			System.out.println("duration >" + duration);
			System.out.println("duration_type >" + duration_type);
			System.out.println("recurrence_pattern >" + recurrence_pattern);
			System.out.println("recurrence_pattern_daily >" + recurrence_pattern_daily);
			System.out.println("recurrence_pattern_monthly >" + recurrence_pattern_monthly);
			System.out.println("recurrence_pattern_yearly >" + recurrence_pattern_yearly);
			System.out.println("recurrence_frequency_daily >" + recurrence_frequency_daily);
			System.out.println("recurrence_frequency_weekly >" + recurrence_frequency_weekly);
			System.out.println("sunday >" + sunday);
			System.out.println("monday >" + monday);
			System.out.println("tuesday >" + tuesday);
			System.out.println("wednesday >" + wednesday);
			System.out.println("thursday >" + thursday);
			System.out.println("friday >" + friday);
			System.out.println("saturday >" + saturday);
			System.out.println("recurrence_day_of_month >" + recurrence_day_of_month);
			System.out.println("recurrence_frequency_monthly >" + recurrence_frequency_monthly);
			System.out.println("recurrence_month_sel >" + recurrence_month_sel);
			System.out.println("recurrence_month_week_day >" + recurrence_month_week_day);
			System.out.println("recurrence_frequency_monthly_alt >" + recurrence_frequency_monthly_alt);
			System.out.println("recurrence_year_month >" + recurrence_year_month);
			System.out.println("recurrence_year_date >" + recurrence_year_date);
			System.out.println("recurrence_year_sel >" + recurrence_year_sel);
			System.out.println("recurrence_year_week >" + recurrence_year_week);
			System.out.println("recurrence_year_month_sel >" + recurrence_year_month_sel);
			System.out.println("period_start_date >" + period_start_date);
			System.out.println("period_end_date >" + period_end_date);
			
			period.setPeriodStartTimeHour(period_start_time_hour.shortValue());
			period.setPeriodStartTimeMinute(period_start_time_minute.shortValue());
			period.setPeriodStartTimeAmPm(period_start_time_am_pm.shortValue());
			period.setDuration(duration.shortValue());
			period.setDurationType(duration_type.shortValue());
			if (recurrence_pattern != null)
				period.setRecurrencePattern(recurrence_pattern.shortValue());
			if (recurrence_pattern_daily != null)
				period.setRecurrencePatternDaily(recurrence_pattern_daily.shortValue());
			if (recurrence_pattern_monthly != null)
				period.setRecurrencePatternMonthly(recurrence_pattern_monthly.shortValue());
			if (recurrence_pattern_yearly != null)
				period.setRecurrencePatternYearly(recurrence_pattern_yearly.shortValue());
			period.setRecurrenceFrequencyDaily(recurrence_frequency_daily.shortValue());
			period.setRecurrenceFrequencyWeekly(recurrence_frequency_weekly.shortValue());
			
			if (monday == null)
				period.setRecurMonday(false);
			else
				period.setRecurMonday(monday.booleanValue());
			
			if (friday == null)
				period.setRecurFriday(false);
			else
				period.setRecurFriday(friday.booleanValue());
			
			if (saturday == null)
				period.setRecurSaturday(false);
			else
				period.setRecurSaturday(saturday.booleanValue());
			
			if (sunday == null)
				period.setRecurSunday(false);
			else
				period.setRecurSunday(sunday.booleanValue());
			
			if (thursday == null)
				period.setRecurThursday(false);
			else
				period.setRecurThursday(thursday.booleanValue());
			
			if (tuesday == null)
				period.setRecurTuesday(false);
			else
				period.setRecurTuesday(tuesday.booleanValue());
			
			if (wednesday == null)
				period.setRecurWednesday(false);
			else
				period.setRecurWednesday(wednesday.booleanValue());
			
			period.setRecurrenceDayOfMonth(recurrence_day_of_month.shortValue());
			period.setRecurrenceFrequencyMonthly(recurrence_frequency_monthly.shortValue());
			period.setRecurrenceMonthSel(recurrence_month_sel.shortValue());
			period.setRecurrenceMonthWeekDay(recurrence_month_week_day.shortValue());
			period.setRecurrenceFrequencyMonthlyAlt(recurrence_frequency_monthly_alt.shortValue());
			period.setRecurrenceYearMonth(recurrence_year_month.shortValue());
			period.setRecurrenceYearDate(recurrence_year_date.shortValue());
			period.setRecurrenceYearSel(recurrence_year_sel.shortValue());
			period.setRecurrenceYearWeek(recurrence_year_week.shortValue());
			period.setRecurrenceYearMonthSel(recurrence_year_month_sel.shortValue());
			
			Date period_start_date_obj = null;
			if (!period_start_date.equals(""))
			{
				period_start_date_obj = CUBean.getDateFromUserString(period_start_date);
				period.setPeriodStartDate(period_start_date_obj);
			}
			
			Date period_end_date_obj = null;
			if (!period_end_date.equals(""))
			{
				period_end_date_obj = CUBean.getDateFromUserString(period_end_date);
				Calendar end_date_cal = Calendar.getInstance();
				end_date_cal.setTime(period_end_date_obj);
				end_date_cal.set(end_date_cal.get(Calendar.YEAR), end_date_cal.get(Calendar.MONTH), end_date_cal.get(Calendar.DATE), 23, 59, 59);
				period.setPeriodEndDate(end_date_cal.getTime());
			}
			
			if (room.intValue() > 0)
				period.setRoom(ClassroomBean.getClassroom(room.intValue()));
			
			period.setSection(section);
			
			period.generatePeriods();
			
			period.save();
			
			
			//period.set
			
			//session.removeAttribute("adminCourseSectionStatus");

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