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
StatementReportFilterAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in StatementReportFilterAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineCompanyBean adminCompany = null;
		UKOnlineCourseReportLister courseReportLister = null;
		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);

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
							person.hasRole(UKOnlineRoleBean.CASHIER_ROLE_NAME) ||
							person.hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME) ||
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

				adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				courseReportLister = (UKOnlineCourseReportLister)session.getAttribute("courseReportLister");
				courseReportLister.invalidateSearchResults();
			}
			else
				return (_mapping.findForward("session_expired"));


			Integer clientSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "clientSelect");
			String start_date = (String)PropertyUtils.getSimpleProperty(_form, "start_date");
			String end_date = (String)PropertyUtils.getSimpleProperty(_form, "end_date");
			Integer[] practice_areas = (Integer[])PropertyUtils.getSimpleProperty(_form, "practice_areas");
			Short[] typeSelect = (Short[])PropertyUtils.getSimpleProperty(_form, "typeSelect");

			System.out.println("");
			System.out.println("clientSelect >" + clientSelect);
			System.out.println("start_date >" + start_date);
			System.out.println("end_date >" + end_date);
			System.out.println("typeSelect >" + typeSelect);
			System.out.println("practice_areas >" + practice_areas);


			if ((clientSelect != null) && (clientSelect.intValue() > 0))
			{
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(clientSelect.intValue());
				courseReportLister.setPerson(practitioner);
			}
			else
				throw new IllegalValueException("You must select a client.");

			Date start_date_obj = CUBean.getDateFromUserString(start_date);
			Date end_date_obj = CUBean.getDateFromUserString(end_date);

			Calendar start_date_cal = Calendar.getInstance();
			Calendar end_date_cal = Calendar.getInstance();
			start_date_cal.setTime(start_date_obj);
			end_date_cal.setTime(end_date_obj);

			System.out.println("start time >" + CUBean.getUserTimeString(start_date_cal.getTime()));
			System.out.println("end time >" + CUBean.getUserTimeString(end_date_cal.getTime()));

			courseReportLister.setStartDateNoModify(start_date_cal.getTime());
			courseReportLister.setEndDateNoModify(end_date_cal.getTime());



			if (typeSelect.length == 0)
				throw new IllegalValueException("You must select one or more checkout code types.");

			Vector types_vec = new Vector();
			if ((typeSelect.length > 0) && (typeSelect[0].intValue() == 0))
				courseReportLister.setShowAllCheckoutCodeTypes(true);
			else
			{
				courseReportLister.setShowAllCheckoutCodeTypes(false);
				for (int i = 0; i < typeSelect.length; i++)
					types_vec.addElement(typeSelect[i]);
			}
			courseReportLister.setCheckoutCodeTypes(types_vec);



			if (practice_areas.length == 0)
				throw new IllegalValueException("You must select one or more practice areas.");

			Vector practice_areas_vec = new Vector();
			if ((practice_areas.length > 0) && (practice_areas[0].intValue() == 0))
				courseReportLister.setShowAllPracticeAreas(true);
			else
			{
				courseReportLister.setShowAllPracticeAreas(false);
				for (int i = 0; i < practice_areas.length; i++)
					practice_areas_vec.addElement(PracticeAreaBean.getPracticeArea(practice_areas[i].intValue()));
			}
			courseReportLister.setPracticeAreas(practice_areas_vec);


			courseReportLister.setShowExcel(true);



			/*
			if (courseSelect.intValue() > 0)
			{
				courseReportLister.setCourse(CourseBean.getCourse(courseSelect.intValue()));
			}
			 */


			/*
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
			 */
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
