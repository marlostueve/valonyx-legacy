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
 *
 * @author Marlo Stueve
 */
public final class
OrganizationAudiencesAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in OrganizationAudiencesAction");

		ActionErrors errors = new ActionErrors();

		CompanyBean adminCompany = null;
		AudienceBean adminAudience = null;
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

				adminCompany = (CompanyBean)session.getAttribute("adminCompany");
				adminAudience = (AudienceBean)session.getAttribute("adminAudience");
			}
			else
				return (_mapping.findForward("session_expired"));

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			Integer[] userGroupSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "userGroupSelect");
			Integer[] departmentSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "departmentSelect");
			Integer[] jobTitleSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "jobTitleSelect");
			
			System.out.println("nameInput >" + nameInput);
			System.out.println("userGroupSelect >" + userGroupSelect);
			System.out.println("departmentSelect >" + departmentSelect);
			System.out.println("jobTitleSelect >" + jobTitleSelect);
			
			
			String delete_str = _request.getParameter("delete_id");
			System.out.println("delete_str >" + delete_str);
			
			if ((delete_str != null) && !delete_str.equals("0"))
			{
			    AudienceBean delete_audience = AudienceBean.getAudience(Integer.parseInt(delete_str));
			    
			    Vector courses_with_audience = CourseAudienceBean.getCourseAudiences(delete_audience);
			    if (courses_with_audience.size() > 0)
			    {
				String course_string = "";
				Iterator itr = courses_with_audience.iterator();
				for (int i = 0; i < 10 && itr.hasNext(); i++)
				{
				    CourseAudienceBean co_obj = (CourseAudienceBean)itr.next();
				    CourseBean course = co_obj.getCourse();
				    if (course_string.equals(""))
					course_string = course.getLabel();
				    else
					course_string += ("\\n" + course.getLabel());
				}
				if (itr.hasNext())
				    course_string += ("\\n...and others...");

				throw new IllegalValueException("Unable to delete " + delete_audience.getLabel() + ".  This audience is in use by activities:\\n" + course_string);
			    }
			    
			    AudienceBean.delete(delete_str);
			}
			else
			{
			    if (nameInput.trim().length() == 0)
				throw new IllegalValueException("Please specify a name.");
			    
			    adminAudience.invalidate();
			    adminAudience.deleteAssociations();
			    adminAudience.setName(nameInput);

			    adminAudience.setAssociateAllPersonGroups(false);
			    adminAudience.setAssociateAllDepartments(false);
			    adminAudience.setAssociateAllPersonTitles(false);

			    Vector groups = new Vector();
			    if (userGroupSelect.length > 0)
			    {
				    if (userGroupSelect[0].intValue() == 0) // all user groups specified
				    {
					    adminAudience.setAssociateAllPersonGroups(true);

					    Iterator itr = PersonGroupBean.getPersonGroups(adminCompany).iterator();
					    while (itr.hasNext())
					    {
						    PersonGroupBean obj = (PersonGroupBean)itr.next();
						    groups.addElement(obj);
					    }
				    }
				    else
				    {
					    adminAudience.setAssociateAllPersonGroups(false);

					    for (int i = 0; i < userGroupSelect.length; i++)
					    {
						    PersonGroupBean obj = PersonGroupBean.getPersonGroup(userGroupSelect[i].intValue());
						    groups.addElement(obj);
					    }
				    }
			    }
			    adminAudience.setPersonGroups(groups);

			    Vector audiences = new Vector();
			    if (departmentSelect.length > 0)
			    {
				    if (departmentSelect[0].intValue() == 0) // all user groups specified
				    {
					    adminAudience.setAssociateAllDepartments(true);

					    Iterator itr = DepartmentBean.getDepartments(adminCompany).iterator();
					    while (itr.hasNext())
					    {
						    DepartmentBean obj = (DepartmentBean)itr.next();
						    audiences.addElement(obj);
					    }
				    }
				    else
				    {
					    adminAudience.setAssociateAllDepartments(false);

					    for (int i = 0; i < departmentSelect.length; i++)
					    {
						    DepartmentBean obj = DepartmentBean.getDepartment(departmentSelect[i].intValue());
						    audiences.addElement(obj);
					    }
				    }
			    }
			    adminAudience.setDepartments(audiences);

			    Vector titles = new Vector();
			    if (jobTitleSelect.length > 0)
			    {
				    if (jobTitleSelect[0].intValue() == 0) // all user groups specified
				    {
					    adminAudience.setAssociateAllPersonTitles(true);

					    Iterator itr = PersonTitleBean.getPersonTitles(adminCompany).iterator();
					    while (itr.hasNext())
					    {
						    PersonTitleBean obj = (PersonTitleBean)itr.next();
						    titles.addElement(obj);
					    }
				    }
				    else
				    {
					    adminAudience.setAssociateAllPersonTitles(false);

					    for (int i = 0; i < jobTitleSelect.length; i++)
					    {
						    PersonTitleBean obj = PersonTitleBean.getPersonTitle(jobTitleSelect[i].intValue());
						    titles.addElement(obj);
					    }
				    }
			    }
			    adminAudience.setPersonTitles(titles);

			    adminAudience.setCompany(adminCompany);
			    if (adminAudience.isNew())
				    adminAudience.setCreatePerson(loginBean.getPerson());

			    adminAudience.setModifyPerson(loginBean.getPerson());
			    
			    
			    if (departmentSelect.length == 0)
				throw new IllegalValueException("Please choose department(s) for your audience.");
			    if (jobTitleSelect.length == 0)
				throw new IllegalValueException("Please choose job title(s) for your audience.");
			    if (userGroupSelect.length == 0)
				throw new IllegalValueException("Please choose user group(s) for your audience.");
			    
			    adminAudience.save();
			}
			
			session.removeAttribute("adminAudience");

			
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