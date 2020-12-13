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
NewsItemAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in NewsItemAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineNewsItemBean newsItemBean = null;
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

				newsItemBean = (UKOnlineNewsItemBean)session.getAttribute("adminNewsItem");
				if (newsItemBean == null)
				{
					newsItemBean = new UKOnlineNewsItemBean();
					session.setAttribute("adminNewsItem", newsItemBean);
				}

				/*
				loginBean = (LoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
				{
					loginBean = new LoginBean();
					session.setAttribute("loginBean", loginBean);
				}
				loginBean.setUsername("admin");
				loginBean.setPassword("spork");
				loginBean.getPerson();
					// This actually does the login.
				 */
			}
			else
				return (_mapping.findForward("session_expired"));

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			String shortDescInput = (String)PropertyUtils.getSimpleProperty(_form, "shortDescInput");
			Integer userGroupSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "userGroupSelect");
			String ownerSelect = (String)PropertyUtils.getSimpleProperty(_form, "ownerSelect");
			Date publishedDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "publishedDateInput"));
			String newsTextInput = (String)PropertyUtils.getSimpleProperty(_form, "newsTextInput");
			String URLInput = (String)PropertyUtils.getSimpleProperty(_form, "URLInput");
			Date releasedDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "releasedDateInput"));
			Date expiresDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "expiresDateInput"));




			newsItemBean.setNewsItemName(nameInput);
			newsItemBean.setShortDescription(shortDescInput);
			if (userGroupSelect.intValue() != 0)
			{
				PersonGroupBean obj = PersonGroupBean.getPersonGroup(userGroupSelect.intValue());
				newsItemBean.setPersonGroup(obj);
			}
			else
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "You must select a User Group."));
			if (!ownerSelect.equals("0"))
				newsItemBean.setOwner(ownerSelect);
			else
				errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "You must select an owner."));
			//newsItemBean.setPublishedDate();
			newsItemBean.setNewsText(newsTextInput);
			newsItemBean.setURLString(URLInput);
			newsItemBean.setReleasedDate(releasedDateInput);
			newsItemBean.setExpirationDate(expiresDateInput);

			if (errors.size() == 0)
				newsItemBean.save();
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