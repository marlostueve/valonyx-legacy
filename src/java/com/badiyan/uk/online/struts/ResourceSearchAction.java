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
ResourceSearchAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in ResourceSearchAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineResourceSearchBean resourceSearchBean = null;
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

				resourceSearchBean = (UKOnlineResourceSearchBean)session.getAttribute("resourceSearch");
				resourceSearchBean.clearSearchCriteria();

			}
			else
				return (_mapping.findForward("session_expired"));

			/*
			 *<form-property name="courseSelect" type="java.lang.String"/>
      <form-property name="certificationSelect" type="java.lang.String"/>
      <form-property name="categorySelect" type="java.lang.String"/>
      <form-property name="userGroupSelect" type="java.lang.String"/>
      <form-property name="departmentSelect" type="java.lang.String"/>
      <form-property name="jobTitleSelect" type="java.lang.String"/>
      <form-property name="expiresDateInput" type="java.lang.String"/>
      <form-property name="keywordSearchBox" type="java.lang.String"/>
      <form-property name="mediaType1" type="java.lang.Boolean"/>
      <form-property name="mediaType2" type="java.lang.Boolean"/>
      <form-property name="mediaType3" type="java.lang.Boolean"/>
      <form-property name="mediaType4" type="java.lang.Boolean"/>
      <form-property name="mediaType5" type="java.lang.Boolean"/>
      <form-property name="mediaType6" type="java.lang.Boolean"/>
      <form-property name="mediaType7" type="java.lang.Boolean"/>
      <form-property name="mediaType8" type="java.lang.Boolean"/>
			 */
			
			Integer courseSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "courseSelect");
			Integer certificationSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "certificationSelect");
			Integer categorySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "categorySelect");
			Integer userGroupSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "userGroupSelect");
			Integer departmentSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "departmentSelect");
			Integer jobTitleSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "jobTitleSelect");
			String keywordSearchBox = (String)PropertyUtils.getSimpleProperty(_form, "keywordSearchBox");

			Boolean mediaType1 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType1");
			Boolean mediaType2 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType2");
			Boolean mediaType3 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType3");
			Boolean mediaType4 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType4");
			Boolean mediaType5 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType5");
			Boolean mediaType6 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType6");
			Boolean mediaType7 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType7");
			Boolean mediaType8 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType8");

			boolean criteriaEntered = false;
			
			/*
			System.out.println("mediaType1 >" + mediaType1);
			System.out.println("mediaType2 >" + mediaType2);
			System.out.println("mediaType3 >" + mediaType3);
			System.out.println("mediaType4 >" + mediaType4);
			System.out.println("mediaType5 >" + mediaType5);
			System.out.println("mediaType6 >" + mediaType6);
			System.out.println("mediaType7 >" + mediaType7);
			System.out.println("mediaType8 >" + mediaType8);
			 */

			if (mediaType1 != null)
			{
				if (mediaType1.booleanValue())
				{
					resourceSearchBean.addResourceType(ResourceBean.WORD_RESOURCE_TYPE);
					criteriaEntered = true;
				}
			}
			if (mediaType2 != null)
			{
				if (mediaType2.booleanValue())
				{
					resourceSearchBean.addResourceType(ResourceBean.POWERPOINT_RESOURCE_TYPE);
					criteriaEntered = true;
				}
			}
			if (mediaType3 != null)
			{
				if (mediaType3.booleanValue())
				{
					resourceSearchBean.addResourceType(ResourceBean.ACROBAT_RESOURCE_TYPE);
					criteriaEntered = true;
				}
			}
			if (mediaType4 != null)
			{
				if (mediaType4.booleanValue())
				{
					resourceSearchBean.addResourceType(ResourceBean.FLASH_RESOURCE_TYPE);
					criteriaEntered = true;
				}
			}
			if (mediaType5 != null)
			{
				if (mediaType5.booleanValue())
				{
					resourceSearchBean.addResourceType(ResourceBean.INTERNET_RESOURCE_TYPE);
					criteriaEntered = true;
				}
			}
			if (mediaType6 != null)
			{
				if (mediaType6.booleanValue())
				{
					resourceSearchBean.addResourceType(ResourceBean.VIDEO_RESOURCE_TYPE);
					criteriaEntered = true;
				}
			}
			if (mediaType7 != null)
			{
				if (mediaType7.booleanValue())
				{
					resourceSearchBean.addResourceType(ResourceBean.CD_RESOURCE_TYPE);
					criteriaEntered = true;
				}
			}
			if (mediaType8 != null)
			{
				if (mediaType8.booleanValue())
				{
					resourceSearchBean.addResourceType(ResourceBean.XLS_RESOURCE_TYPE);
					criteriaEntered = true;
				}
			}

			String expiresDateInputString = (String)PropertyUtils.getSimpleProperty(_form, "expiresDateInput");
			Date expiresDateInput = null;
			if (expiresDateInputString.length() > 0)
			{
				criteriaEntered = true;
				expiresDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "expiresDateInput"));
				resourceSearchBean.setExpirationDate(expiresDateInput);
			}

			if ((courseSelect != null) && (courseSelect.intValue() > 0))
			{
				criteriaEntered = true;
				resourceSearchBean.setCourse(CourseBean.getCourse(courseSelect.intValue()));
			}
			if ((certificationSelect != null) && (certificationSelect.intValue() > 0))
			{
				criteriaEntered = true;
				resourceSearchBean.setCertification(CertificationBean.getCertification(certificationSelect.intValue()));
			}
			if ((categorySelect != null) && (categorySelect.intValue() > 0))
			{
				criteriaEntered = true;
				resourceSearchBean.setCategory(CategoryBean.getCategory(categorySelect.intValue()));
			}
			if ((userGroupSelect != null) && (userGroupSelect.intValue() > 0))
			{
				criteriaEntered = true;
				resourceSearchBean.setUserGroup(PersonGroupBean.getPersonGroup(userGroupSelect.intValue()));
			}
			if ((departmentSelect != null) && (departmentSelect.intValue() > 0))
			{
				criteriaEntered = true;
				resourceSearchBean.setDepartment(DepartmentBean.getDepartment(departmentSelect.intValue()));
			}
			if ((jobTitleSelect != null) && (jobTitleSelect.intValue() > 0))
			{
				criteriaEntered = true;
				resourceSearchBean.setJobTitle(PersonTitleBean.getPersonTitle(jobTitleSelect.intValue()));
			}
			if ((keywordSearchBox != null) && (keywordSearchBox.length() > 0))
			{
				criteriaEntered = true;
				resourceSearchBean.setKeyword(keywordSearchBox);
			}

			/*
			if (criteriaEntered)
				resourceSearchBean.search();
			else
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "No search criteria supplied."));
			 */

			resourceSearchBean.setDisplayOnlyActive(false);
			resourceSearchBean.search();
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
