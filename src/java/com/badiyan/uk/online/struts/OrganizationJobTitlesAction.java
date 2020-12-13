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
OrganizationJobTitlesAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in OrganizationJobTitlesAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		PersonTitleBean personTitle = null;
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
				personTitle = (PersonTitleBean)session.getAttribute("adminJobTitle");
			}
			else
				return (_mapping.findForward("session_expired"));

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			System.out.println("nameInput >" + nameInput);
			
			String delete_str = _request.getParameter("delete_id");
			System.out.println("delete_str >" + delete_str);
			
			if ((delete_str != null) && !delete_str.equals("0"))
			{
				PersonTitleBean delete_title = PersonTitleBean.getPersonTitle(Integer.parseInt(delete_str));
				
				Vector persons_with_title = PersonBean.getPersons(adminCompany, delete_title);
				if (persons_with_title.size() > 0)
				{
				    String person_string = "";
				    Iterator itr = persons_with_title.iterator();
				    for (int i = 0; i < 10 && itr.hasNext(); i++)
				    {
					PersonBean person = (PersonBean)itr.next();
					if (person_string.equals(""))
					    person_string = person.getLabel();
					else
					    person_string += ("\\n" + person.getLabel());
				    }
				    if (itr.hasNext())
					person_string += ("\\n...and others...");
				    
				    throw new IllegalValueException("Unable to delete " + delete_title.getLabel() + ".  This title is in use by:\\n" + person_string);
				}
				
				Vector audiences_with_title = AudienceBean.getAudiences(adminCompany, delete_title);
				if (audiences_with_title.size() > 0)
				{
				    String audience_string = "";
				    Iterator itr = audiences_with_title.iterator();
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
				    
				    throw new IllegalValueException("Unable to delete " + delete_title.getLabel() + ".  This title is in use by audiences:\\n" + audience_string);
				}
				
				PersonTitleBean.delete(delete_str);
			}
			else
			{

			    if (nameInput.trim().length() == 0)
				throw new IllegalValueException("Please specify a name.");
			    
			    personTitle.setName(nameInput);
			    personTitle.setCompany(adminCompany);
			    personTitle.save();
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