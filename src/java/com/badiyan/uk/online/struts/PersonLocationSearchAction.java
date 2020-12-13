package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that implements person searches.
 *
 * @author Marlo Stueve
 */
public final class
PersonLocationSearchAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in PersonLocationSearchAction");

		// Get the username and password...
		ActionErrors errors = new ActionErrors();
		Integer department = (Integer)PropertyUtils.getSimpleProperty(_form, "department");

		// Test the session for the search bean...

		HttpSession session = _request.getSession(false);
		if (session != null)
		{
			UKOnlineLoginBean loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
			if (loginBean == null)
				return (_mapping.findForward("session_expired"));
			else
			{
				/*
				try
				{
					UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
					if (!(person.hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME) ||
							person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME)))
					{
						return (_mapping.findForward("session_expired"));
					}
				}
				catch (IllegalValueException x)
				{
					return (_mapping.findForward("session_expired"));
				}
				 */
			}

			UKOnlinePersonSearchBean searchEngine = (UKOnlinePersonSearchBean)session.getAttribute("personSearch");

			try
			{
				if (department.intValue() == 0)
				{
					searchEngine.clearSearchCriteria();
					searchEngine.setNumToDisplayPerPage(9999);
					searchEngine.search();
				}
				else
				{
					searchEngine.clearSearchCriteria();
					searchEngine.setLocation(DepartmentBean.getDepartment(department.intValue()));
					searchEngine.search();
				}
			}
			catch (Exception x)
			{
				System.out.println("error :" + x.getMessage());
				x.printStackTrace();
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			}
		}
		else
			return (_mapping.findForward("session_expired"));

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