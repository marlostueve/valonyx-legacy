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
CourseCatalogSearchAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseCatalogSearchAction");

		// Get the username and password...
		ActionErrors errors = new ActionErrors();

		/*
		<form-property name="titleInput" type="java.lang.String"/>
      <form-property name="manufacturerSelect" type="java.lang.String"/>
      <form-property name="categorySelect" type="java.lang.String"/>
      <form-property name="releasedDateInput" type="java.lang.String"/>
      <form-property name="releasedDateSelect" type="java.lang.Integer"/>
      <form-property name="completeByDateInput" type="java.lang.String"/>
      <form-property name="completeByDateSelect" type="java.lang.Integer"/>
      <form-property name="mediaType1" type="java.lang.Boolean"/>
      <form-property name="mediaType2" type="java.lang.Boolean"/>
      <form-property name="mediaType3" type="java.lang.Boolean"/>
      <form-property name="mediaType4" type="java.lang.Boolean"/>
      <form-property name="mediaType5" type="java.lang.Boolean"/>
      <form-property name="mediaType6" type="java.lang.Boolean"/>
      <form-property name="mediaType7" type="java.lang.Boolean"/>
      <form-property name="titleSearchBox" type="java.lang.String"/>
		 */


		String titleInput = (String)PropertyUtils.getSimpleProperty(_form, "titleInput");
		String manufacturerSelect = (String)PropertyUtils.getSimpleProperty(_form, "manufacturerSelect");
		String categorySelect = (String)PropertyUtils.getSimpleProperty(_form, "categorySelect");
		String releasedDateInput = (String)PropertyUtils.getSimpleProperty(_form, "releasedDateInput");
		Integer releasedDateSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "releasedDateSelect");
		String completeByDateInput = (String)PropertyUtils.getSimpleProperty(_form, "completeByDateInput");
		Integer completeByDateSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "completeByDateSelect");
		Boolean mediaType1 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType1");
		Boolean mediaType2 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType2");
		Boolean mediaType3 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType3");
		Boolean mediaType4 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType4");
		Boolean mediaType5 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType5");
		Boolean mediaType6 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType6");
		Boolean mediaType7 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType7");
		String titleSearchBox = (String)PropertyUtils.getSimpleProperty(_form, "titleSearchBox");

		System.out.println("titleInput - " + titleInput);
		System.out.println("manufacturerSelect - " + manufacturerSelect);
		System.out.println("categorySelect - " + categorySelect);
		System.out.println("releasedDateInput - " + releasedDateInput);
		System.out.println("releasedDateSelect - " + releasedDateSelect);
		System.out.println("completeByDateInput - " + completeByDateInput);
		System.out.println("completeByDateSelect - " + completeByDateSelect);
		System.out.println("mediaType1 - " + mediaType1);
		System.out.println("mediaType2 - " + mediaType2);
		System.out.println("mediaType3 - " + mediaType3);
		System.out.println("mediaType4 - " + mediaType4);
		System.out.println("mediaType5 - " + mediaType5);
		System.out.println("mediaType6 - " + mediaType6);
		System.out.println("mediaType7 - " + mediaType7);
		System.out.println("titleSearchBox - " + titleSearchBox);



		// Check the session to see if there's an existing courseSearch bean...
		boolean media_selected = false;
		CourseSearchBean courseSearch = null;
		CertificationSearchBean certSearch = null;
		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);
		if (session != null)
		{
			loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
			if (loginBean == null)
				return (_mapping.findForward("session_expired"));

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

			if (titleInput.length() > 0)
			{
				courseSearch.setKeyword(titleInput);
				certSearch.setKeyword(titleInput);
			}

			if (!categorySelect.equals("0"))
			{
				/*
				courseSearch.setCategory(CategoryBean.getCategory(categorySelect));
				certSearch.setCategory(CategoryBean.getCategory(categorySelect));
				 */
			}

			if (releasedDateInput != null)
			{
				if (releasedDateInput.length() > 0)
				{
					if (!(releasedDateSelect.intValue() == 3))
					{
						Date releasedDate = CUBean.getDateFromUserString(releasedDateInput);
						courseSearch.setReleasedDate(releasedDate);
						courseSearch.setReleasedDateCompareType(releasedDateSelect.intValue());
						certSearch.setReleasedDate(releasedDate);
						certSearch.setReleasedDateCompareType(releasedDateSelect.intValue());
					}
				}
			}

			if (completeByDateInput != null)
			{
				if (completeByDateInput.length() > 0)
				{
					if (!(completeByDateSelect.intValue() == 3))
					{
						Date completeByDate = CUBean.getDateFromUserString(completeByDateInput);
						courseSearch.setExpirationDate(completeByDate);
						courseSearch.setExpirationDateCompareType(completeByDateSelect.intValue());
						certSearch.setExpirationDate(completeByDate);
						certSearch.setExpirationDateCompareType(completeByDateSelect.intValue());
					}
				}
			}

			if (!(mediaType1 != null && mediaType2 != null && mediaType3 != null && mediaType4 != null && mediaType5 != null && mediaType6 != null && mediaType7 != null))
			{
				if (mediaType1 != null)
				{
					courseSearch.addResourceType(ResourceBean.WORD_RESOURCE_TYPE);
					media_selected = true;
				}
				if (mediaType2 != null)
				{
					courseSearch.addResourceType(ResourceBean.POWERPOINT_RESOURCE_TYPE);
					media_selected = true;
				}
				if (mediaType3 != null)
				{
					courseSearch.addResourceType(ResourceBean.ACROBAT_RESOURCE_TYPE);
					media_selected = true;
				}
				if (mediaType4 != null)
				{
					courseSearch.addResourceType(ResourceBean.FLASH_RESOURCE_TYPE);
					media_selected = true;
				}
				if (mediaType5 != null)
				{
					courseSearch.addResourceType(ResourceBean.INTERNET_RESOURCE_TYPE);
					media_selected = true;
				}
				if (mediaType6 != null)
				{
					courseSearch.addResourceType(ResourceBean.VIDEO_RESOURCE_TYPE);
					media_selected = true;
				}
				if (mediaType7 != null)
				{
					courseSearch.addResourceType(ResourceBean.CD_RESOURCE_TYPE);
					media_selected = true;
				}
			}

			if (titleSearchBox.length() > 0)
			{
				courseSearch.setDescriptionKeyword(titleSearchBox);
				certSearch.setDescriptionKeyword(titleSearchBox);
			}

			courseSearch.setStatus(CourseBean.DEFAULT_COURSE_STATUS);



			boolean applyAudience = true;
			try
			{
				if (((String)session.getAttribute("corporate")).equals("true"))
					applyAudience = false;
				if (applyAudience)
				{
					UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
					Vector groups = PersonGroupBean.getPersonGroups(person_obj.getUserGroupString());
					if (groups.size() > 0)
					{
						courseSearch.setUserGroup((PersonGroupBean)groups.elementAt(0));
						certSearch.setUserGroup((PersonGroupBean)groups.elementAt(0));
					}
					
					courseSearch.setJobTitle(person_obj.getJobTitle());
					/*
					courseSearch.setRegion(DepartmentBean.getDepartment(person_obj.getRegionId()));
					courseSearch.setLocation(DepartmentBean.getDepartment(person_obj.getLocationId()));
					 */
					
					certSearch.setJobTitle(person_obj.getJobTitle());
					/*
					certSearch.setRegion(DepartmentBean.getDepartment(person_obj.getRegionId()));
					certSearch.setLocation(DepartmentBean.getDepartment(person_obj.getLocationId()));
					 */
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}




			courseSearch.search();
			certSearch.search();
			if (!media_selected)
				courseSearch.addSearchResults(certSearch);

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