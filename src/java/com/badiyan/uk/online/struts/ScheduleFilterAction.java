/*
 * To change this template, choose Tools | Templates
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

import com.badiyan.uk.conformance.aicc.rte.datamodel.*;

import com.badiyan.uk.online.tasks.*;

import java.io.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
ScheduleFilterAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute invoked in ScheduleFilterAction");
		
		String forward_string = "success1";
		

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);
		
		UKOnlinePersonBean person;

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				person = (UKOnlinePersonBean)loginBean.getPerson();
			}
			else
				return (_mapping.findForward("session_expired"));

			Integer practitioner_id = (Integer)PropertyUtils.getSimpleProperty(_form, "practitioner_id");
			Integer appointment_type_id = (Integer)PropertyUtils.getSimpleProperty(_form, "appointment_type_id");
			
			System.out.println("practitioner_id >" + practitioner_id);
			System.out.println("appointment_type_id >" + appointment_type_id);

			if (practitioner_id != null) {
				UKOnlinePersonBean selected_practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(practitioner_id);
				session.setAttribute("selected_practitioner", selected_practitioner);
			}
			
			if (appointment_type_id != null) {
				AppointmentTypeBean selected_appointment_type = AppointmentTypeBean.getAppointmentType(appointment_type_id);
				session.setAttribute("selected_appointment_type", selected_appointment_type);
				forward_string = "success2";
			}
			
		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			
			session.setAttribute("forgot-password-message", "<strong>Error:</strong> " + x.getMessage());
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
}