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
GoodsSoldReportFilterAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in GoodsSoldReportFilterAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineCourseReportLister courseReportLister = null;
		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);

		String forward_string = "success";

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
					return (_mapping.findForward("session_expired"));
				else
				{
					try
					{
						UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
						if (!(person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) ||
							person.hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME) ||
							person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME)))
						{
							return (_mapping.findForward("session_expired"));
						}
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				courseReportLister = (UKOnlineCourseReportLister)session.getAttribute("courseReportLister");
				courseReportLister.invalidateSearchResults();
			}
			else
				return (_mapping.findForward("session_expired"));


			String format = (String)PropertyUtils.getSimpleProperty(_form, "format");
			Integer sort = (Integer)PropertyUtils.getSimpleProperty(_form, "sort");
			Integer status = (Integer)PropertyUtils.getSimpleProperty(_form, "status");
			Boolean displayHTML = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayHTML");
			Boolean displayExcel = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayExcel");

			String start_date = (String)PropertyUtils.getSimpleProperty(_form, "start_date");
			String end_date = (String)PropertyUtils.getSimpleProperty(_form, "end_date");

			System.out.println("");
			System.out.println("format >" + format);
			System.out.println("start_date >" + start_date);
			System.out.println("end_date >" + end_date);

			try
			{
				Date start_date_obj = CUBean.getDateFromUserString(start_date);
				Date end_date_obj = CUBean.getDateFromUserString(end_date);
				courseReportLister.setStartDate(start_date_obj);
				courseReportLister.setEndDate(end_date_obj);
			}
			catch (Exception x)
			{

			}

			if (sort != null)
			{
				courseReportLister.setSort(sort.shortValue());
			}

			if (displayHTML != null)
			{
				courseReportLister.setShowHTML(displayHTML.booleanValue());
			}
			else
				courseReportLister.setShowHTML(false);

			if (displayExcel != null)
			{
				courseReportLister.setShowExcel(displayExcel.booleanValue());
			}
			else
				courseReportLister.setShowExcel(false);

			if (status.intValue() > 0)
				courseReportLister.setStatus(status.intValue());
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
