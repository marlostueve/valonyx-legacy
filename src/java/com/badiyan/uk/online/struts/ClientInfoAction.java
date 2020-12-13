/*
 * ClientInfoAction.java
 *
 * Created on May 10, 2008, 11:30 AM
 *
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.*;
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
ClientInfoAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in ClientInfoAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		
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
				
				admin_company = (CompanyBean)session.getAttribute("adminCompany");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			Integer cid = (Integer)PropertyUtils.getSimpleProperty(_form, "cid");
			Integer statusInput = (Integer)PropertyUtils.getSimpleProperty(_form, "statusInput");
			String editorInput = (String)PropertyUtils.getSimpleProperty(_form, "editorInput");
			
			System.out.println("cid >" + cid);
			System.out.println("statusInput >" + statusInput);
			System.out.println("editorInput >" + editorInput);
			
			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements())
			{
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));
			}
			
			UKOnlinePersonBean adminPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(cid.intValue());
			session.setAttribute("adminPerson", adminPerson);
			
			System.out.println("adminPerson found >" + adminPerson.getLabel());
			
			boolean modified = false;

			if (editorInput != null)
			{
				if (!editorInput.equals(adminPerson.getCommentString()))
				{
					adminPerson.setComment(editorInput);
					modified = true;
				}
			}
			
			if (statusInput.intValue() == 0)
			{
				// active
				
				if (!adminPerson.isActive())
				{
					adminPerson.setActive(true);
				
					// remove from Black Hole

					if (adminPerson.isInBlackHole())
					{
						BlackHoleBean black_hole = BlackHoleBean.getInstance(admin_company, (UKOnlinePersonBean)loginBean.getPerson());
						if (black_hole.contains(adminPerson))
						{
							black_hole.remove(adminPerson);
							black_hole.setCreateOrModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
							black_hole.save();
						}

						adminPerson.setIsInBlackHole(false);
					}

					//adminPerson.save();
					
					modified = true;
				}
			}
			else if (statusInput.intValue() == 1)
			{
				// black hole
				
				if (!adminPerson.isInBlackHole())
				{
				
					BlackHoleBean black_hole = BlackHoleBean.getInstance(admin_company, (UKOnlinePersonBean)loginBean.getPerson());

					if (!black_hole.contains(adminPerson))
					{
						black_hole.add(adminPerson);
						black_hole.setCreateOrModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
						black_hole.save();
					}
					
					adminPerson.setIsInBlackHole(true);
					//adminPerson.save();

					modified = true;
				}
			}
			else
			{
				if (adminPerson.isInBlackHole())
				{
					BlackHoleBean black_hole = BlackHoleBean.getInstance(admin_company, (UKOnlinePersonBean)loginBean.getPerson());
					if (black_hole.contains(adminPerson))
					{
						black_hole.remove(adminPerson);
						black_hole.setCreateOrModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
						black_hole.save();
					}

					adminPerson.setIsInBlackHole(false);

					modified = true;
				}
				
				// inactivate
				
				if (adminPerson.isActive())
				{
					adminPerson.setActive(false);
				
					adminPerson.save();
					
					modified = true;
				}
			}

			if (modified)
				adminPerson.save();

		}
		catch (Exception x)
		{
			x.printStackTrace();
			//errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			session.setAttribute("error-message", x.getMessage());
			forwardString = "failure";
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
		return (_mapping.findForward(forwardString));
	}
}
