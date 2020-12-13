package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 * Implementation of <strong>Action</strong> that validates a user login.
 *
 * @author Marlo Stueve
 * @version $Revision: 1.2 $ $Date: 2009/07/17 16:23:51 $
 */
public final class
CategoryTypesAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CategoryTypesAction");

		// Get the username and password...
		ActionErrors errors = new ActionErrors();

		String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");

		System.out.println("nameInput - " + nameInput);

		// Check the session to see if there's an existing courseSearch bean...

		CompanyBean adminCompany = null;
		CategoryTypeBean adminCategoryType = null;

		HttpSession session = _request.getSession(false);
		if (session != null)
		{
			adminCompany = (CompanyBean)session.getAttribute("adminCompany");
			adminCategoryType = (CategoryTypeBean)session.getAttribute("adminCategoryType");
		}
		else
			return (_mapping.findForward("session_expired"));

		try
		{
			adminCategoryType.setName(nameInput);
			adminCategoryType.setCompany(adminCompany);
			adminCategoryType.save();
			
			session.removeAttribute("adminCategoryType");

		}
		catch (Exception x)
		{
			x.printStackTrace();
			//_request.setAttribute("error", x.getMessage());
			//RequestDispatcher rd = _request.getRequestDispatcher("/login.jsp");
			//rd.forward(_request, _response);

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
