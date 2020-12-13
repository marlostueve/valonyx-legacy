package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 * Implementation of <strong>Action</strong> that validates a user login.
 *
 * @author Marlo Stueve
 * @version $Revision: 1.2 $ $Date: 2009/07/17 16:23:52 $
 */
public final class
UserGroupAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in UserGroupAction");

		// Get the username and password...
		ActionErrors errors = new ActionErrors();

		String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
		//String homePageTextInput = (String)PropertyUtils.getSimpleProperty(_form, "homePageTextInput");

		System.out.println("nameInput - " + nameInput);
		//System.out.println("homePageTextInput - " + homePageTextInput);



		// Check the session to see if there's an existing courseSearch bean...

		CompanyBean adminCompany = null;
		PersonGroupBean adminGroup = null;

		HttpSession session = _request.getSession(false);
		if (session != null)
		{
			adminCompany = (CompanyBean)session.getAttribute("adminCompany");
			adminGroup = (PersonGroupBean)session.getAttribute("adminGroup");
		}
		else
			return (_mapping.findForward("session_expired"));

		try
		{
		    
			
			
			String delete_str = _request.getParameter("delete_id");
			System.out.println("delete_str >" + delete_str);
			
			if ((delete_str != null) && !delete_str.equals("0"))
			{
			    PersonGroupBean delete_group = PersonGroupBean.getPersonGroup(Integer.parseInt(delete_str));
			    
			    Vector persons_with_group = PersonBean.getPersons(adminCompany, delete_group);
			    if (persons_with_group.size() > 0)
			    {
				String person_string = "";
				Iterator itr = persons_with_group.iterator();
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

				throw new IllegalValueException("Unable to delete " + delete_group.getLabel() + ".  This group is in use by:\\n" + person_string);
			    }
			    
			    Vector audiences_with_group = AudienceBean.getAudiences(adminCompany, delete_group);
			    if (audiences_with_group.size() > 0)
			    {
				String audience_string = "";
				Iterator itr = audiences_with_group.iterator();
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

				throw new IllegalValueException("Unable to delete " + delete_group.getLabel() + ".  This group is in use by audiences:\\n" + audience_string);
			    }

			    PersonGroupBean.delete(delete_str);
			}
			else
			{
			    if (nameInput.trim().length() == 0)
				throw new IllegalValueException("Please specify a name.");
			    
			    adminGroup.setName(nameInput);
			    adminGroup.setCompany(adminCompany);
			    adminGroup.save();
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
			//_request.setAttribute("error", x.getMessage());
			//RequestDispatcher rd = _request.getRequestDispatcher("/login.jsp");
			//rd.forward(_request, _response);

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
