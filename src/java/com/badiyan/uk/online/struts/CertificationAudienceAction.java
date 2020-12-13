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
CertificationAudienceAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CertificationAudienceAction");

		ActionErrors errors = new ActionErrors();

		CertificationBean certBean = null;
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
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
							return (_mapping.findForward("session_expired"));
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				certBean = (CertificationBean)session.getAttribute("adminCert");
				if (certBean == null)
				{
					certBean = new CertificationBean();
					session.setAttribute("adminCert", certBean);
				}

			}
			else
				return (_mapping.findForward("session_expired"));

			/*
			String audienceNameInput = (String)PropertyUtils.getSimpleProperty(_form, "audienceNameInput");
			String userGroupSelect = (String)PropertyUtils.getSimpleProperty(_form, "userGroupSelect");
			String subGroupSelect = (String)PropertyUtils.getSimpleProperty(_form, "subGroupSelect");
			Integer regionSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "regionSelect");
			Integer locationSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "locationSelect");
			String jobTitleSelect = (String)PropertyUtils.getSimpleProperty(_form, "jobTitleSelect");
			String courseIsSelect = (String)PropertyUtils.getSimpleProperty(_form, "courseIsSelect");
			String completeByDateInputString = (String)PropertyUtils.getSimpleProperty(_form, "completeByDateInput");
			 */

			String audienceNameInput = (String)PropertyUtils.getSimpleProperty(_form, "audienceNameInput");
			String[] userGroupSelect = (String[])PropertyUtils.getSimpleProperty(_form, "userGroupSelect");
			String subGroupSelect = (String)PropertyUtils.getSimpleProperty(_form, "subGroupSelect");
			Integer[] regionSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "regionSelect");
			Integer[] locationSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "locationSelect");
			String[] jobTitleSelect = (String[])PropertyUtils.getSimpleProperty(_form, "jobTitleSelect");
			String courseIsSelect = (String)PropertyUtils.getSimpleProperty(_form, "courseIsSelect");
			String completeByDateInputString = (String)PropertyUtils.getSimpleProperty(_form, "completeByDateInput");


			AudienceBean audience = new AudienceBean();
			audience.setName(audienceNameInput);
			/*
			if (!userGroupSelect.equals("0"))
				audience.setPersonGroup(userGroupSelect);
			if (regionSelect.intValue() > 0)
				audience.setRegion(regionSelect.intValue());
			if (locationSelect.intValue() > 0)
				audience.setLocation(locationSelect.intValue());
			if (!jobTitleSelect.equals("0"))
				audience.setPersonTitle(jobTitleSelect);
			 */
			
			/*
			audience.setCourseReqType(courseIsSelect);
			 */
			
			/*
			if (completeByDateInputString != null && completeByDateInputString.length() > 0)
				audience.setCompleteByDate(CUBean.getDateFromUserString(completeByDateInputString));
			 */

			/*
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~AUDIENCE SAVED/CREATED >" + certBean.getNameString());
			audience.setCertification(certBean);
			 */


			if (userGroupSelect.length > 0)
			{
				/*
				if (userGroupSelect[0].equals("0"))
				{
					Iterator itr = PersonGroupBean.getPersonGroups().iterator();
					while (itr.hasNext())
					{
						PersonGroupBean obj = (PersonGroupBean)itr.next();
						audience.addPersonGroup(obj);
					}
				}
				else
				{
					for (int i = 0; i < userGroupSelect.length; i++)
						audience.addPersonGroup(userGroupSelect[i]);
				}
				 */
				
				// commented out 10.17.05
			}

			/*
			for (int i = 0; i < userGroupSelect.length; i++)
			{
				if (userGroupSelect[i].equals("0"))
					break;
				audience.addPersonGroup(userGroupSelect[i]);
			}
			*/

			/*
			for (int i = 0; i < regionSelect.length; i++)
			{
				if (regionSelect[i].intValue() == 0)
					break;
				audience.addRegion(regionSelect[i].intValue());
			}
			for (int i = 0; i < locationSelect.length; i++)
			{
				if (locationSelect[i].intValue() == 0)
					break;
				audience.addLocation(locationSelect[i].intValue());
			}
			for (int i = 0; i < jobTitleSelect.length; i++)
			{
				if (jobTitleSelect[i].equals("0"))
					break;
				audience.addPersonTitle(jobTitleSelect[i]);
			}
			 */



			if (regionSelect.length > 0)
			{
				/*
				if (regionSelect[0].intValue() == 0)
				{
					Iterator itr = DepartmentBean.getDepartments(DepartmentBean.REGION_DEPARTMENT_TYPE).iterator();
					while (itr.hasNext())
					{
						DepartmentBean obj = (DepartmentBean)itr.next();
						audience.addRegion(obj.getId());
					}
				}
				else
				{
					for (int i = 0; i < regionSelect.length; i++)
						audience.addRegion(regionSelect[i].intValue());
				}
				 */
				
				//commented out 10.17.05
			}

			if (locationSelect.length > 0)
			{
				/*
				if (locationSelect[0].intValue() == 0)
				{
					Iterator itr = DepartmentBean.getDepartments(DepartmentBean.BRANCH_DEPARTMENT_TYPE).iterator();
					while (itr.hasNext())
					{
						DepartmentBean obj = (DepartmentBean)itr.next();
						audience.addLocation(obj.getId());
					}
				}
				else
				{
					for (int i = 0; i < locationSelect.length; i++)
						audience.addLocation(locationSelect[i].intValue());
				}
				 */
				
				// commented out 10.17.05
			}

			if (jobTitleSelect.length > 0)
			{
				/*
				if (jobTitleSelect[0].equals("0"))
				{
					Iterator itr = PersonTitleBean.getPersonTitles().iterator();
					while (itr.hasNext())
					{
						PersonTitleBean obj = (PersonTitleBean)itr.next();
						audience.addPersonTitle(obj.getName());
					}
				}
				else
				{
					for (int i = 0; i < jobTitleSelect.length; i++)
						audience.addPersonTitle(jobTitleSelect[i]);
				}
				 */
				
				// commented out 10.25.05
			}


			audience.save();

			CategoryBean category = certBean.getCategory();

			if (userGroupSelect.length > 0)
			{
				Vector userGroups = null;
				if (userGroupSelect[0].equals("0"))
					userGroups = PersonGroupBean.getPersonGroups();
				else
				{
					userGroups = new Vector();
					for (int i = 0; i < userGroupSelect.length; i++)
					{
						/*
						PersonGroupBean group = PersonGroupBean.getPersonGroup(userGroupSelect[i]);
						userGroups.addElement(group);
						 */
					}
				}

				Iterator groupItr = userGroups.iterator();
				while (groupItr.hasNext())
				{

					PersonGroupBean group = (PersonGroupBean)groupItr.next();

					// create an association in categorypersongroup
					try
					{
						CategoryPersongroup categoryPersonGroup = new CategoryPersongroup();
						categoryPersonGroup.setCategoryId(category.getId());
						categoryPersonGroup.setPersonGroupId(group.getId());
						categoryPersonGroup.setAssigntype(PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
						categoryPersonGroup.save();
					}
					catch (Exception xx)
					{
					}

					// create associations for job title

					for (int index = 0; index < jobTitleSelect.length; index++)
					{
						if (jobTitleSelect[index].equals("0"))
						{
							// get all the job titles
							Iterator itr = PersonTitleBean.getPersonTitles().iterator();
							while (itr.hasNext())
							{
								try
								{
									PersonTitleBean personTitle = (PersonTitleBean)itr.next();
									PersonTitlePersonGroup personTitlePersonGroup = new PersonTitlePersonGroup();
									personTitlePersonGroup.setPersonTitleId(personTitle.getId());
									personTitlePersonGroup.setPersonGroupId(group.getId());
									personTitlePersonGroup.setAssigntype(PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
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
								/*
								PersonTitleBean personTitle = PersonTitleBean.getPersonTitle(jobTitleSelect[index]);
								Persontitlepersongroup personTitlePersonGroup = new Persontitlepersongroup();
								personTitlePersonGroup.setTitle(personTitle.getName());
								personTitlePersonGroup.setPersongroup(group.getName());
								personTitlePersonGroup.setAssigntype(PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
								personTitlePersonGroup.save();
								 */
							}
							catch (Exception xx)
							{
							}
						}
					}
				}
			}

			certBean.invalidate();

			System.out.println("AUDIENCE SAVED/CREATED >" + certBean.getNameString());
		}
        catch (IllegalValueException x)
        {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}
		/*
		catch (ParseException x)
		{
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}
		 */
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