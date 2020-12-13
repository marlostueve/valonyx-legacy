package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a user login.
 *
 * @author Marlo Stueve
 * @version $Revision: 1.2 $ $Date: 2009/07/17 16:23:52 $
 */
public final class
CourseCatalogAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseCatalogAction");

		// Get the username and password...
		ActionErrors errors = new ActionErrors();

		String keywordInput = (String)PropertyUtils.getSimpleProperty(_form, "keywordInput");
		System.out.println("keywordInput - " + keywordInput);



		// Check the session to see if there's an existing courseSearch bean...

		CourseSearchBean courseSearch = null;
		CertificationSearchBean certSearch = null;
		HttpSession session = _request.getSession(false);
		if (session != null)
		{
			courseSearch = (CourseSearchBean)session.getAttribute("courseSearch");
			if (courseSearch == null)
			{
				courseSearch = new CourseSearchBean();
				session.setAttribute("courseSearch", courseSearch);
			}

			certSearch = (CertificationSearchBean)session.getAttribute("certSearch");
			if (certSearch == null)
			{
				certSearch = new CertificationSearchBean();
				session.setAttribute("certSearch", certSearch);
			}
		}
		else
			return (_mapping.findForward("session_expired"));

		try
		{
			courseSearch.clearSearchCriteria();
			certSearch.clearSearchCriteria();

			courseSearch.setKeyword(keywordInput);
			//courseSearch.setShowAll(false);
			//courseSearch.addResourceType(ResourceBean.ACROBAT_RESOURCE_TYPE);
			//courseSearch.addResourceType(ResourceBean.WORD_RESOURCE_TYPE);
			courseSearch.setStatus(CourseBean.DEFAULT_COURSE_STATUS);
			courseSearch.search();

			certSearch.setKeyword(keywordInput);
			certSearch.search();

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