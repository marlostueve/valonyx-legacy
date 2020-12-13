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
OrganizationClassroomsAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in OrganizationClassroomsAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		CompanyBean adminCompany = null;
		ClassroomBean adminClassroom = null;
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
				adminClassroom = (ClassroomBean)session.getAttribute("adminClassroom");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-bean       name="organizationClassroomsForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="building" type="java.lang.Integer"/>
      <form-property name="type" type="java.lang.String"/>
      <form-property name="status" type="java.lang.String"/>
      <form-property name="capacity" type="java.lang.Integer"/>
      <form-property name="comment" type="java.lang.String"/>
    </form-bean>
			 */

			String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			Integer building = (Integer)PropertyUtils.getSimpleProperty(_form, "building");
			Integer type = (Integer)PropertyUtils.getSimpleProperty(_form, "type");
			Integer status = (Integer)PropertyUtils.getSimpleProperty(_form, "status");
			Short capacity = (Short)PropertyUtils.getSimpleProperty(_form, "capacity");
			String comment = (String)PropertyUtils.getSimpleProperty(_form, "comment");
			
			
			adminClassroom.setName(nameInput);
			adminClassroom.setCapacity(capacity.shortValue());
			if (comment != null)
				adminClassroom.setComment(comment);
			
			if (building.intValue() > 0)
				adminClassroom.setBuilding(BuildingBean.getBuilding(building.intValue()));
			if (type.intValue() > 0)
				adminClassroom.setType(ClassroomTypeBean.getType(type.intValue()));
			if (status.intValue() > 0)
				adminClassroom.setStatus(ClassroomStatusBean.getStatus(status.intValue()));
			
			if (nameInput == null || nameInput.equals(""))
				throw new IllegalValueException("Please specify a classroom name.");
			if (capacity.shortValue() == 0)
				throw new IllegalValueException("Please specify a classroom capacity.");
			if (building.intValue() == 0)
				throw new IllegalValueException("You must select a building for " + nameInput);
			
			/*
			if (type.intValue() == 0)
				throw new IllegalValueException("You must select a classroom type for " + nameInput);
			if (status.intValue() == 0)
				throw new IllegalValueException("You must select a classroom status for " + nameInput);
			 */
			
			adminClassroom.setCreateOrModifyPerson(loginBean.getPerson());
			adminClassroom.save();
			
			session.removeAttribute("adminClassroom");

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