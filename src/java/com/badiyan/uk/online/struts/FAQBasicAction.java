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
FAQBasicAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in FAQBasicAction");

		ActionErrors errors = new ActionErrors();

		CompanyBean adminCompany = null;
		FAQBean faqBean = null;
		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);

		ListHelperBean listHelper = null;

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

				faqBean = (FAQBean)session.getAttribute("adminFAQ");
				System.out.println("~~FAQ FOUND - " + faqBean.isNew());
				if (faqBean == null)
				{
					faqBean = new FAQBean();
					session.setAttribute("adminFAQ", faqBean);
				}

				System.out.println("~~FAQ FOUND - " + faqBean.isNew());

				listHelper = (ListHelperBean)session.getAttribute("listHelper");
				adminCompany = (CompanyBean)session.getAttribute("adminCompany");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-bean       name="faqBasicForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="categorySelect" type="java.lang.String"/>
      <form-property name="audienceSelect" type="java.lang.Integer[]"/>
      <form-property name="ownerSelect" type="java.lang.String"/>
      <form-property name="releasedDateInput" type="java.lang.String"/>
      <form-property name="displayAsNew" type="java.lang.Boolean"/>
      <form-property name="displayAsNewInput" type="java.lang.Integer"/>
      <form-property name="expiresDateInput" type="java.lang.String"/>
    </form-bean>
			 */

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			Integer categorySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "categorySelect");

			Integer[] audienceSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "audienceSelect");


			String ownerSelect = (String)PropertyUtils.getSimpleProperty(_form, "ownerSelect");
			Boolean displayAsNew = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayAsNew");
			Integer displayAsNewInput = (Integer)PropertyUtils.getSimpleProperty(_form, "displayAsNewInput");

			if (displayAsNew == null)
				displayAsNew = new Boolean(false);

			Date releasedDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "releasedDateInput"));
			Date expiresDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "expiresDateInput"));

			faqBean.setName(nameInput);
			if (displayAsNew.booleanValue() && (displayAsNewInput != null))
				faqBean.setDisplayAsNewDays(displayAsNewInput.shortValue());
			else
				faqBean.setDisplayAsNewDays((short)0);
			faqBean.setReleasedDate(releasedDateInput);

			
			if (categorySelect.intValue() > 0)
				faqBean.associate(CategoryBean.getCategory(categorySelect.intValue()));
			
			
			faqBean.setExpirationDate(expiresDateInput);
			if (!ownerSelect.equals("0"))
				faqBean.setOwner(PersonBean.getPerson(Integer.parseInt(ownerSelect)));
			else
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "You must select an owner."));
			
			faqBean.setCompany(adminCompany);

			if (errors.size() == 0)
			{
				System.out.println("~~ABOUT TO SAVE - " + faqBean.isNew());
				faqBean.save();
				System.out.println("~~SAVED - " + faqBean.isNew());

				faqBean.deleteAssociations();

				/*
				if (!userGroupSelect.equals("0"))
					faqBean.associate(PersonGroupBean.getPersonGroup(userGroupSelect));
				//if (!subGroupSelect.equals("0"))
				//	faqSearchBean.setSub(PersonGroupBean.getPersonGroup(userGroupSelect));
				if (!regionSelect.equals("0"))
					faqBean.associate(DepartmentBean.getDepartment(Integer.parseInt(regionSelect)));
				if (!locationSelect.equals("0"))
					faqBean.associate(DepartmentBean.getDepartment(Integer.parseInt(locationSelect)));
				if (!jobTitleSelect.equals("0"))
					faqBean.associate(jobTitleSelect);
				*/
				
				if (audienceSelect.length > 0)
				{
					for (int i = 0; i < audienceSelect.length; i++)
						faqBean.associate(AudienceBean.getAudience(audienceSelect[i].intValue()));
				}

				/*
				if (userGroupSelect.length > 0)
				{
					if (userGroupSelect[0].equals("0"))
					{
						// associate all person groups to this resource
						Iterator itr = PersonGroupBean.getPersonGroups().iterator();
						while (itr.hasNext())
						{
							PersonGroupBean personGroup = (PersonGroupBean)itr.next();
							faqBean.associate(personGroup);
						}
					}
					else
					{
						for (int i = 0; i < userGroupSelect.length; i++)
							faqBean.associate(PersonGroupBean.getPersonGroup(userGroupSelect[i]));
					}
				}
				 */

				/*
				if (regionSelect.length > 0)
				{
					
					if (regionSelect[0].equals("0"))
					{
						// associate all regions to this resource
						Iterator itr = listHelper.getRegions().iterator();
						while (itr.hasNext())
						{
							DepartmentBean region = (DepartmentBean)itr.next();
							faqBean.associate(region);
						}
					}
					else
					{
						for (int i = 0; i < regionSelect.length; i++)
							faqBean.associate(DepartmentBean.getDepartment(Integer.parseInt(regionSelect[i])));
					}
					 
					
					// commented out 10.17.05
				}
				 */

				/*
				if (locationSelect.length > 0)
				{
					
					if (locationSelect[0].equals("0"))
					{
						// associate all regions to this resource
						Iterator itr = listHelper.getBranches().iterator();
						while (itr.hasNext())
						{
							DepartmentBean location = (DepartmentBean)itr.next();
							faqBean.associate(location);
						}
					}
					else
					{
						for (int i = 0; i < locationSelect.length; i++)
							faqBean.associate(DepartmentBean.getDepartment(Integer.parseInt(locationSelect[i])));
					}
					 
					
					// commented out 10.17.05
				}
				 */

				/*
				if (jobTitleSelect.length > 0)
				{
					if (jobTitleSelect[0].equals("0"))
					{
						// associate all regions to this resource
						Iterator itr = listHelper.getPersonTitles().iterator();
						while (itr.hasNext())
						{
							PersonTitleBean title = (PersonTitleBean)itr.next();
							faqBean.associate(title.getName());
						}
					}
					else
					{
						for (int i = 0; i < jobTitleSelect.length; i++)
							faqBean.associate(jobTitleSelect[i]);
					}
				}
				 */



				/*
				PersonGroupBean group = PersonGroupBean.getPersonGroup(userGroupSelect);
				ManufacturerBean manufacturer = ManufacturerBean.getManufacturer(manufacturerSelect);
				CategoryBean category = CategoryBean.getCategory(categorySelect);


				// create an association in categorypersongroup
				try
				{
					Categorypersongroup categoryPersonGroup = new Categorypersongroup();
					categoryPersonGroup.setCategoryname(category.getName());
					categoryPersonGroup.setPersongroup(group.getName());
					categoryPersonGroup.setAssigntype(PersonGroupBean.FAQ_PERSON_GROUP_ASSIGN_TYPE);
					categoryPersonGroup.save();
				}
				catch (Exception xx)
				{
				}

				// create an association in manufacturerpersongroup
				try
				{
					Manufacturerpersongroup manufacturerPersonGroup = new Manufacturerpersongroup();
					manufacturerPersonGroup.setManufacturername(manufacturer.getName());
					manufacturerPersonGroup.setPersongroup(group.getName());
					manufacturerPersonGroup.setAssigntype(PersonGroupBean.FAQ_PERSON_GROUP_ASSIGN_TYPE);
					manufacturerPersonGroup.save();
				}
				catch (Exception xx)
				{
				}

				if (jobTitleSelect.equals("0"))
				{
					Iterator jobTitleItr = PersonTitleBean.getPersonTitles().iterator();
					while (jobTitleItr.hasNext())
					{
						try
						{
							PersonTitleBean personTitle = (PersonTitleBean)jobTitleItr.next();
							Persontitlepersongroup personTitlePersonGroup = new Persontitlepersongroup();
							personTitlePersonGroup.setTitle(personTitle.getName());
							personTitlePersonGroup.setPersongroup(group.getName());
							personTitlePersonGroup.setAssigntype(PersonGroupBean.FAQ_PERSON_GROUP_ASSIGN_TYPE);
							personTitlePersonGroup.save();
						}
						catch (Exception xx)
						{
						}
					}
				}
				else
				{
					try
					{
						PersonTitleBean personTitle = PersonTitleBean.getPersonTitle(jobTitleSelect);
						Persontitlepersongroup personTitlePersonGroup = new Persontitlepersongroup();
						personTitlePersonGroup.setTitle(personTitle.getName());
						personTitlePersonGroup.setPersongroup(group.getName());
						personTitlePersonGroup.setAssigntype(PersonGroupBean.FAQ_PERSON_GROUP_ASSIGN_TYPE);
						personTitlePersonGroup.save();
					}
					catch (Exception xx)
					{
					}
				}
				 */
			}
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
