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
ResourceBasicAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in ResourceBasicAction");

		ActionErrors errors = new ActionErrors();

		CompanyBean adminCompany = null;
		ResourceBean resourceBean = null;
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

				resourceBean = (ResourceBean)session.getAttribute("adminResource");
				adminCompany = (CompanyBean)session.getAttribute("adminCompany");
			}
			else
				return (_mapping.findForward("session_expired"));

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			String descriptionInput = (String)PropertyUtils.getSimpleProperty(_form, "descriptionInput");
			String updateNotesInput = (String)PropertyUtils.getSimpleProperty(_form, "updateNotesInput");
			//Integer categorySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "categorySelect");
			Integer[] audienceSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "audienceSelect");
			Integer ownerSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "ownerSelect");
			Integer statusSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "statusSelect");
			Boolean displayAsNew = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayAsNew");
			Integer displayAsNewInput = (Integer)PropertyUtils.getSimpleProperty(_form, "displayAsNewInput");
			String typeSelect = (String)PropertyUtils.getSimpleProperty(_form, "typeSelect");

			boolean criteriaEntered = false;
			
			System.out.println("statusSelect >" + statusSelect);

			if (displayAsNew == null)
				displayAsNew = new Boolean(false);

			Date releasedDateInput = null;
			Date expiresDateInput = null;
			String releasedDateInputString = (String)PropertyUtils.getSimpleProperty(_form, "releasedDateInput");
			String expiresDateInputString = (String)PropertyUtils.getSimpleProperty(_form, "expiresDateInput");
			
			if (!releasedDateInputString.equals("none"))
			{
			    releasedDateInput = CUBean.getDateFromUserString(releasedDateInputString);
			    resourceBean.setReleasedDate(releasedDateInput);
			}
			if (!expiresDateInputString.equals("none"))
			{
			    expiresDateInput = CUBean.getDateFromUserString(expiresDateInputString);
			    resourceBean.setExpirationDate(expiresDateInput);
			}

			resourceBean.setCompany(adminCompany);
			resourceBean.setName(nameInput);
			
			if (statusSelect == null)
			    resourceBean.setActive(true);
			else
			{
			    if (statusSelect.intValue() == 2)
				resourceBean.setActive(true);
			    else
				resourceBean.setActive(false);
			}
			
			System.out.println("descriptionInput >" + descriptionInput);
			System.out.println("updateNotesInput >" + updateNotesInput);
			resourceBean.setDescription(descriptionInput);
			resourceBean.setUpdateNotes(updateNotesInput);
			
			if (displayAsNew.booleanValue())
				resourceBean.setDisplayAsNewDays(displayAsNewInput.shortValue());
			else
				resourceBean.setDisplayAsNewDays((short)0);

			/*
			if ((categorySelect != null) && (categorySelect.intValue() > 0))
				resourceBean.associate(CategoryBean.getCategory(categorySelect.intValue()));
			 */
			
			
			resourceBean.setOwner(loginBean.getPerson());
			System.out.println("~~ABOUT TO SAVE - " + resourceBean.isNew());
			
			if (typeSelect == null)
			    throw new IllegalValueException("Please select a resource type.");
			resourceBean.setType(typeSelect);
			
			resourceBean.setActionType(ResourceBean.UNKNOWN_ACTION_TYPE);
			resourceBean.setContentType(ResourceBean.UNKNOWN_CONTENT_TYPE);
			resourceBean.setTextType(ResourceBean.UNKNOWN_TEXT_TYPE);

			if ((ownerSelect != null) && (ownerSelect.intValue() != 0))
				resourceBean.setOwner(PersonBean.getPerson(ownerSelect.intValue()));
			//else
			//	errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "You must select an owner."));

			if (errors.size() == 0)
			{
				resourceBean.save();
				System.out.println("~~SAVED - " + resourceBean.isNew());

				resourceBean.deleteAssociations();
				
				Vector audiences = new Vector();
				for (int i = 0; i < audienceSelect.length; i++)
					audiences.addElement(AudienceBean.getAudience(audienceSelect[i].intValue()));
				
				resourceBean.setAudiences(audiences);
				resourceBean.saveAudiences();

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
					categoryPersonGroup.setAssigntype(PersonGroupBean.RESOURCE_PERSON_GROUP_ASSIGN_TYPE);
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
					manufacturerPersonGroup.setAssigntype(PersonGroupBean.RESOURCE_PERSON_GROUP_ASSIGN_TYPE);
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
							personTitlePersonGroup.setAssigntype(PersonGroupBean.RESOURCE_PERSON_GROUP_ASSIGN_TYPE);
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
						personTitlePersonGroup.setAssigntype(PersonGroupBean.RESOURCE_PERSON_GROUP_ASSIGN_TYPE);
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
			x.printStackTrace();
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
