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
FAQSearchAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in FAQSearchAction");

		ActionErrors errors = new ActionErrors();

		FAQSearchBean faqSearchBean = null;
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
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) && !person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
							return (_mapping.findForward("session_expired"));
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				faqSearchBean = (FAQSearchBean)session.getAttribute("faqSearch");
				if (faqSearchBean == null)
				{
					faqSearchBean = new FAQSearchBean();
					session.setAttribute("faqSearch", faqSearchBean);
				}
				else
					faqSearchBean.clearSearchCriteria();
			}
			else
				return (_mapping.findForward("session_expired"));

			Integer courseSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "courseSelect");
			Integer certificationSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "certificationSelect");
			Integer categorySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "categorySelect");
			Integer userGroupSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "userGroupSelect");
			Integer departmentSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "departmentSelect");
			Integer jobTitleSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "jobTitleSelect");
			String keywordSearchBox = (String)PropertyUtils.getSimpleProperty(_form, "keywordSearchBox");

			boolean criteriaEntered = false;

			String expiresDateInputString = (String)PropertyUtils.getSimpleProperty(_form, "expiresDateInput");
			Date expiresDateInput = null;
			if (expiresDateInputString.length() > 0)
			{
				criteriaEntered = true;
				expiresDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "expiresDateInput"));
				faqSearchBean.setExpirationDate(expiresDateInput);
			}

			if (courseSelect.intValue() > 0)
			{
				criteriaEntered = true;
				faqSearchBean.setCourse(CourseBean.getCourse(courseSelect.intValue()));
			}
			if (certificationSelect.intValue() > 0)
			{
				criteriaEntered = true;
				faqSearchBean.setCertification(CertificationBean.getCertification(certificationSelect.intValue()));
			}

			if (categorySelect.intValue() > 0)
			{
				criteriaEntered = true;
				faqSearchBean.setCategory(CategoryBean.getCategory(categorySelect.intValue()));
			}
			if (userGroupSelect.intValue() > 0)
			{
				criteriaEntered = true;
				faqSearchBean.setUserGroup(PersonGroupBean.getPersonGroup(userGroupSelect.intValue()));
			}
			if (departmentSelect.intValue() > 0)
			{
				criteriaEntered = true;
				faqSearchBean.setDepartment(DepartmentBean.getDepartment(departmentSelect.intValue()));
			}
			if (jobTitleSelect.intValue() > 0)
			{
				criteriaEntered = true;
				faqSearchBean.setJobTitle(PersonTitleBean.getPersonTitle(jobTitleSelect.intValue()));
			}
			if (keywordSearchBox.length() > 0)
			{
				criteriaEntered = true;
				faqSearchBean.setKeyword(keywordSearchBox);
			}

			//if (criteriaEntered)
				faqSearchBean.search();
			//else
			//	errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "No search criteria supplied."));


		}
        catch (IllegalValueException x)
        {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
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
