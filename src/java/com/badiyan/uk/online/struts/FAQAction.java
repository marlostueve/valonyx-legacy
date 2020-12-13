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
FAQAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in FAQAction");

		ActionErrors errors = new ActionErrors();

		FAQSearchBean faqSearchBean = null;
		UKOnlineLoginBean loginBean = null;

		DepartmentBean selectedDepartment = null;
		PersonGroupBean selectedGroup = null;
		PersonTitleBean selectedTitle = null;
		
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				faqSearchBean = (FAQSearchBean)session.getAttribute("faqSearch");
				faqSearchBean.clearSearchCriteria();

				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				
				selectedDepartment = (DepartmentBean)session.getAttribute("selectedDepartment");
				selectedGroup = (PersonGroupBean)session.getAttribute("selectedGroup");
				selectedTitle = (PersonTitleBean)session.getAttribute("selectedTitle");
			}
			else
				return (_mapping.findForward("session_expired"));

			Integer categorySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "categorySelect");
			String keywordInput = (String)PropertyUtils.getSimpleProperty(_form, "keywordInput");

			if (categorySelect.intValue() > 0)
				faqSearchBean.setCategory(CategoryBean.getCategory(categorySelect.intValue()));

			if (keywordInput.length() > 0)
				faqSearchBean.setKeyword(keywordInput);
			
			faqSearchBean.setUserGroup(selectedGroup);
			faqSearchBean.setJobTitle(selectedTitle);
			faqSearchBean.setDepartment(selectedDepartment);

			faqSearchBean.setDefaultSearch(false);
			faqSearchBean.setDisplayOnlyActive(true);
			
			faqSearchBean.search();

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
