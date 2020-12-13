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
OrganizationDepartmentTypesAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in OrganizationDepartmentTypesAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		DepartmentTypeBean departmentType = null;
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
				departmentType = (DepartmentTypeBean)session.getAttribute("adminDepartmentType");
			}
			else
				return (_mapping.findForward("session_expired"));

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");

			System.out.println("nameInput >" + nameInput);
			
			String delete_str = _request.getParameter("delete_id");
			System.out.println("delete_str >" + delete_str);
			
			if ((delete_str != null) && !delete_str.equals("0"))
			{
				DepartmentTypeBean delete_department_type = DepartmentTypeBean.getDepartmentType(Integer.parseInt(delete_str));
				Vector departments_that_use_this_type = DepartmentBean.getDepartments(adminCompany, delete_department_type);
				if (departments_that_use_this_type.size() > 0)
				{
				    String dept_string = "";
				    Iterator itr = departments_that_use_this_type.iterator();
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
				    
				    throw new IllegalValueException("Unable to delete " + delete_department_type.getLabel() + ".  This type is in use by:\\n" + dept_string);
				}
				else
				    DepartmentTypeBean.delete(delete_str);
			}
			else
			{
			    if (nameInput.trim().length() == 0)
				throw new IllegalValueException("Please specify a name.");
			    
			    departmentType.setName(nameInput);
			    departmentType.setCompany(adminCompany);
			    departmentType.save();
			}

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