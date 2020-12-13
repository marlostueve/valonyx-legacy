package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;
import org.apache.torque.TorqueException;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
OrganizationMainAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in OrganizationMainAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		HttpSession session = _request.getSession(false);
		
		String forward_string = "success";

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
			}
			else
				return (_mapping.findForward("session_expired"));
			
			String submit_button = _request.getParameter("submit_button");
			
			System.out.println("submit_button >" + submit_button);
			
			if ((submit_button != null) && submit_button.equals("Delete Organization"))
			{
			    CompanyBean.delete(adminCompany.getValue());
			    forward_string = "delete";
			}
			else
			{

			    String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");

			    boolean is_new_company = adminCompany.isNew();

			    adminCompany.setName(nameInput);
			    adminCompany.setType("Corporation");
			    adminCompany.save();

			    // associate this person as the admin of the new organization

			    if (is_new_company)
			    {
					CompanyAdministratorMapBean map = new CompanyAdministratorMapBean();
					map.setCompany(adminCompany);
					map.setPerson(loginBean.getPerson());
					map.save();

					PersonGroupBean.maintainGroup(adminCompany, PersonGroupBean.DEFAULT_PERSON_GROUP_NAME);
					DepartmentBean default_department = adminCompany.getDefaultDepartment();
					PersonTitleBean.maintainDefaultData(adminCompany);
			    }
			}
			

			// select this company
			
			//loginBean.getPerson().selectCompany(adminCompany);
			
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
		return (_mapping.findForward(forward_string));
	}
	
	public static void
	saveCompany(CompanyBean adminCompany, String nameInput, String accountNumber, PersonBean modPerson) throws IllegalValueException, TorqueException, Exception {
		
		if (nameInput.length() > 0) {
			adminCompany.setName(nameInput);
		}
		adminCompany.setType("Corporation");
		adminCompany.setSICCode(accountNumber);
		adminCompany.save();

		// create stuff for new company

		DepartmentTypeBean default_dept_type = DepartmentTypeBean.maintainDepartmentType(adminCompany, "[DEFAULT]");

		DepartmentBean.maintainDepartment(adminCompany, default_dept_type, "[DEFAULT]");

		PersonTitleBean.maintainDefaultData(adminCompany);

		CategoryTypeBean.maintainDefaultData(adminCompany);
		CategoryBean.maintainDefaultData(adminCompany);

		// associate this person as the admin of the new organization

		try {
			CompanyAdministratorMapBean map = new CompanyAdministratorMapBean();
			map.setCompany(adminCompany);
			map.setPerson(modPerson);
			map.save();
		} catch (Exception e) {
			System.out.println("ERROR >" + e.getMessage());
			e.printStackTrace();
		}

		modPerson.invalidateCompanies();
		// select this company
		modPerson.selectCompany(adminCompany);
	}
}