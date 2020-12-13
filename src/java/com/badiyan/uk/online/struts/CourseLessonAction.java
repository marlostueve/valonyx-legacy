/*
 * CourseLessonAction.java
 *
 * Created on March 16, 2007, 1:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

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
CourseLessonAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CourseLessonAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		
		CourseGroupBean course_group = null;
		
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
				course_group = (CourseGroupBean)session.getAttribute("adminCourseGroup");
			}
			else
				return (_mapping.findForward("session_expired"));

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			System.out.println("nameInput >" + nameInput);
			
			String delete_str = _request.getParameter("delete_id");
			System.out.println("delete_str >" + delete_str);
			
			if ((delete_str != null) && !delete_str.equals("0"))
			{
				
				CourseGroupBean delete_group = CourseGroupBean.getCourseGroup(Integer.parseInt(delete_str));
				
				if (delete_group.hasCourses())
				    throw new IllegalValueException("Unable to delete " + delete_group.getNameString() + ".  It contains course(s).");
				
				if (delete_group.hasChildren())
				    throw new IllegalValueException("Unable to delete " + delete_group.getNameString() + ".  It contains course(s).");
				
				CourseGroupBean parent_obj = null;
				try
				{
				    parent_obj = delete_group.getParent();
				    
				}
				catch (Exception x)
				{
				}
				
				CourseGroupBean.delete(delete_str);
				
				if (parent_obj != null)
				    parent_obj.invalidate();
			}
			else
			{
			    if (nameInput.trim().length() == 0)
				throw new IllegalValueException("Please specify a name.");
			    
			    CourseGroupBean existing_parent = null;
			    if (!course_group.isNew())
				existing_parent = course_group.getParent();
			    
			    
			    if (course_group.isNew())
			    {
				course_group.setCompany(adminCompany);
				course_group.setType(adminCompany, "Level");
			    }
			    
			    course_group.setName(nameInput);
			    course_group.save();
			}
			
			session.removeAttribute("adminCourseGroup");

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