/*
 * PersonLocationSearchActionX.java
 *
 * Created on March 12, 2007, 9:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

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
 * Implementation of <strong>Action</strong> that implements person searches.
 *
 * @author Marlo Stueve
 */
public final class
PersonLocationSearchActionX
	extends Action
{
    public ActionForward
	execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in PersonLocationSearchActionX");
		
		ActionErrors errors = new ActionErrors();
		
		UKOnlineLoginBean loginBean = null;
		UKOnlinePersonBean adminPerson = null;
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
				
				adminPerson = (UKOnlinePersonBean)session.getAttribute("adminPerson");
				if (!adminPerson.isNew())
				{
					adminPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(adminPerson.getId());
					session.setAttribute("adminPerson", adminPerson);
				}
			}
			else
				return (_mapping.findForward("session_expired"));
			
			if ((_request.getParameter("submit_button") != null) && (_request.getParameter("submit_button").equals("Inactivate User")))
			{
			    adminPerson.setActive(false);
			    adminPerson.save();
			}
			else if ((_request.getParameter("submit_button") != null) && (_request.getParameter("submit_button").equals("Activate User")))
			{
			    adminPerson.setActive(true);
			    adminPerson.save();
			}
			else
			{
			
			    //String username = (String)PropertyUtils.getSimpleProperty(_form, "username");
			    String password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			    String confirm_password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			    String first_name = (String)PropertyUtils.getSimpleProperty(_form, "first_name");
			    String last_name = (String)PropertyUtils.getSimpleProperty(_form, "last_name");
			    String employee_number = (String)PropertyUtils.getSimpleProperty(_form, "employee_number");
			    String email = (String)PropertyUtils.getSimpleProperty(_form, "email");
			    //Integer user_group = (Integer)PropertyUtils.getSimpleProperty(_form, "user_group");
			    //Integer department = (Integer)PropertyUtils.getSimpleProperty(_form, "department");
			    //Integer job_title = (Integer)PropertyUtils.getSimpleProperty(_form, "job_title");
			    //Integer[] managers = (Integer[])PropertyUtils.getSimpleProperty(_form, "managers");
			    //String cost_center = (String)PropertyUtils.getSimpleProperty(_form, "cost_center");

			    adminPerson.setUsername(email);
			    if ((password != null) && !password.equals(""))
				    adminPerson.setPassword(password);
			    if ((confirm_password != null) && !confirm_password.equals(""))
				    adminPerson.setConfirmPassword(confirm_password);
			    adminPerson.setFirstName(first_name);
			    adminPerson.setLastName(last_name);
			    adminPerson.setEmployeeId(employee_number);
			    if (!email.equals(""))
				adminPerson.setEmail(email);
			    //adminPerson.setCostCenter(cost_center);

			    CompanyBean valeo = CompanyBean.getInstance();

			    /*
			    if (department.intValue() > 0)
				    adminPerson.setDepartment(DepartmentBean.getDepartment(department.intValue()));
			    else
				    adminPerson.setDepartment(valeo.getDefaultDepartment());
			     */
			    
			    adminPerson.setDepartment(DepartmentBean.getDepartment(1));
			    
			    /*
			    if (job_title.intValue() > 0)
				    adminPerson.setTitle(PersonTitleBean.getPersonTitle(job_title.intValue()));
			    else
				adminPerson.setTitle(PersonTitleBean.getDefaultTitle(ecolab));
			     */
			    
			    adminPerson.setTitle(PersonTitleBean.getPersonTitle(1));
			    

			    /*
			    Vector supervisors = new Vector();
			    for (int i = 0; i < managers.length; i++)
			    {
				    //adminPerson.setSupervisor(UKOnlinePersonBean.getPerson(managers[i].intValue()), PersonBean.MANAGER_SUPERVISOR_TYPE);
				    supervisors.addElement(UKOnlinePersonBean.getPerson(managers[i].intValue()));
			    }
			    adminPerson.setSupervisors(supervisors);
			     */
			    
			    adminPerson.save();

			    /*
			    if (user_group.intValue() > 0)
				    adminPerson.setGroup(PersonGroupBean.getPersonGroup(user_group.intValue()));
			    else
				adminPerson.setGroup(PersonGroupBean.getDefaultPersonGroup(ecolab));
			     */
			    
			    adminPerson.setGroup(PersonGroupBean.getPersonGroup(1));
			    
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage().replace("\"", " ")));
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