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
OrganizationDepartmentsAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in OrganizationDepartmentsAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		DepartmentBean department = null;
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
				{
					loginBean = new UKOnlineLoginBean();
					session.setAttribute("loginBean", loginBean);
				}
				
				adminCompany = (CompanyBean)session.getAttribute("adminCompany");
				department = (DepartmentBean)session.getAttribute("adminDepartment");
			}
			else
				return (_mapping.findForward("session_expired"));

			Integer parent = (Integer)PropertyUtils.getSimpleProperty(_form, "parent");
			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			Integer type = (Integer)PropertyUtils.getSimpleProperty(_form, "type");

			System.out.println("parent >" + parent);
			System.out.println("nameInput >" + nameInput);
			System.out.println("type >" + type);
			
			String delete_str = _request.getParameter("delete_id");
			System.out.println("delete_str >" + delete_str);
			
			if ((delete_str != null) && !delete_str.equals("0"))
			{
				DepartmentBean delete_department = DepartmentBean.getDepartment(Integer.parseInt(delete_str));
				
				if (delete_department.containsPeople())
				    throw new IllegalValueException("Unable to delete " + delete_department.getLabel() + ".  It contains people.");
				
				Vector children_departments = DepartmentBean.getChildDepartments(adminCompany, delete_department);
				if (children_departments.size() > 0)
				{
				    String dept_string = "";
				    Iterator itr = children_departments.iterator();
				    for (int i = 0; i < 10 && itr.hasNext(); i++)
				    {
					DepartmentBean dept = (DepartmentBean)itr.next();
					if (dept_string.equals(""))
					    dept_string = dept.getLabel();
					else
					    dept_string += ("\\n" + dept.getLabel());
				    }
				    if (itr.hasNext())
					dept_string += ("\\n...and others...");
				    
				    throw new IllegalValueException("Unable to delete " + delete_department.getLabel() + ".  This department is the parent for:\\n" + dept_string);
				}
				
				Vector audiences_with_dept = AudienceBean.getAudiences(adminCompany, delete_department);
				if (audiences_with_dept.size() > 0)
				{
				    String audience_string = "";
				    Iterator itr = audiences_with_dept.iterator();
				    for (int i = 0; i < 10 && itr.hasNext(); i++)
				    {
					AudienceBean audience = (AudienceBean)itr.next();
					if (audience_string.equals(""))
					    audience_string = audience.getLabel();
					else
					    audience_string += ("\\n" + audience.getLabel());
				    }
				    if (itr.hasNext())
					audience_string += ("\\n...and others...");
				    
				    throw new IllegalValueException("Unable to delete " + delete_department.getLabel() + ".  This department is in use by audiences:\\n" + audience_string);
				}
				
				DepartmentBean.delete(delete_str);
			}
			else
			{
			    if (nameInput.trim().length() == 0)
				throw new IllegalValueException("Please specify a name.");
			    
			    if ((parent != null) && (parent.intValue() != 0))
				    department.setParent(DepartmentBean.getDepartment(parent.intValue()));
			    department.setName(nameInput);
			    department.setType(DepartmentTypeBean.getDepartmentType(type.intValue()));
			    department.setCompany(adminCompany);
			    department.save();
			}
			
			session.removeAttribute("adminDepartment");

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