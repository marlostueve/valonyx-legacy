package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.util.*;

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
LibrarySearchAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in LibrarySearchAction");

		// Get the username and password...
		ActionErrors errors = new ActionErrors();

		Integer categorySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "categorySelect");
		Boolean mediaType1 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType1");
		Boolean mediaType2 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType2");
		Boolean mediaType3 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType3");
		Boolean mediaType4 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType4");
		Boolean mediaType5 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType5");
		Boolean mediaType6 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType6");
		Boolean mediaType7 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType7");
		String keywordInput = (String)PropertyUtils.getSimpleProperty(_form, "keywordInput");

		System.out.println("categorySelect - " + categorySelect);
		System.out.println("mediaType1 - " + mediaType1);
		System.out.println("mediaType2 - " + mediaType2);
		System.out.println("mediaType3 - " + mediaType3);
		System.out.println("mediaType4 - " + mediaType4);
		System.out.println("mediaType5 - " + mediaType5);
		System.out.println("mediaType6 - " + mediaType6);
		System.out.println("mediaType7 - " + mediaType7);
		System.out.println("keywordInput - " + keywordInput);



		// Check the session to see if there's an existing courseSearch bean...

		UKOnlineResourceSearchBean resourceSearch = null;
		UKOnlineLoginBean loginBean = null;
		
		DepartmentBean selectedDepartment = null;
		PersonGroupBean selectedGroup = null;
		PersonTitleBean selectedTitle = null;

		HttpSession session = _request.getSession(false);

		if (session != null)
		{
			loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");

			resourceSearch = (UKOnlineResourceSearchBean)session.getAttribute("resourceSearch");
			
			selectedDepartment = (DepartmentBean)session.getAttribute("selectedDepartment");
			selectedGroup = (PersonGroupBean)session.getAttribute("selectedGroup");
			selectedTitle = (PersonTitleBean)session.getAttribute("selectedTitle");
		}
		else
			return (_mapping.findForward("session_expired"));

		try
		{
			resourceSearch.clearSearchCriteria();

			if (keywordInput.length() > 0)
				resourceSearch.setKeyword(keywordInput);
			
			if (categorySelect.intValue() > 0)
				resourceSearch.setCategory(CategoryBean.getCategory(categorySelect.intValue()));
			
			resourceSearch.setUserGroup(selectedGroup);
			resourceSearch.setJobTitle(selectedTitle);
			resourceSearch.setDepartment(selectedDepartment);

			if (mediaType1 != null)
				resourceSearch.addResourceType(ResourceBean.WORD_RESOURCE_TYPE);
			if (mediaType2 != null)
				resourceSearch.addResourceType(ResourceBean.POWERPOINT_RESOURCE_TYPE);
			if (mediaType3 != null)
				resourceSearch.addResourceType(ResourceBean.ACROBAT_RESOURCE_TYPE);
			if (mediaType4 != null)
				resourceSearch.addResourceType(ResourceBean.FLASH_RESOURCE_TYPE);
			if (mediaType5 != null)
				resourceSearch.addResourceType(ResourceBean.INTERNET_RESOURCE_TYPE);
			if (mediaType6 != null)
				resourceSearch.addResourceType(ResourceBean.VIDEO_RESOURCE_TYPE);
			if (mediaType7 != null)
				resourceSearch.addResourceType(ResourceBean.CD_RESOURCE_TYPE);

			resourceSearch.setDisplayOnlyActive(true);
			resourceSearch.setDisplayMostActive(false);

			resourceSearch.search();
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