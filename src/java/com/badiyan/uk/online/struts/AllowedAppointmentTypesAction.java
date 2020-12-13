/*
 * AllowedAppointmentTypesAction.java
 *
 * Created on January 22, 2008, 10:15 AM
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
AllowedAppointmentTypesAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in AllowedAppointmentTypesAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		AppointmentTypeBean appointment_type = null;
		
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
				appointment_type = (AppointmentTypeBean)session.getAttribute("adminAppointmentType");
			}
			else
				return (_mapping.findForward("session_expired"));

			System.out.println("appointment_type >" + appointment_type);
			    
			Integer[] appointmentTypeSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "appointmentTypeSelect");
			System.out.println("appointmentTypeSelect >" + appointmentTypeSelect);
			
			Vector allowed_apppointment_types = new Vector();
			for (int i = 0; i < appointmentTypeSelect.length; i++)
			    allowed_apppointment_types.addElement(AppointmentTypeBean.getAppointmentType(appointmentTypeSelect[i].intValue()));
			
			appointment_type.setAllowedAppointmentTypes(allowed_apppointment_types);
			appointment_type.save();
			
			/*

			appointment_type.setCompany(admin_company);
			appointment_type.setName(nameInput);
			appointment_type.setImageUrl(image_url);
			appointment_type.setBGColorCode(bg_color);
			appointment_type.setTextColorCode(text_color);
			if (appointment_type_type.shortValue() != (short)0)
			    appointment_type.setType(appointment_type_type.shortValue());
			appointment_type.setPracticeAreaId(practice_area.intValue());

			try
			{
			    appointment_type.setDuration(Short.parseShort(duration));
			}
			catch (Exception x)
			{
			    //throw new IllegalValueException("You must specify a duration.");
			}

			if (appointment_type_type.shortValue() == (short)0)
			    throw new IllegalValueException("You must specify a type.");

			appointment_type.save();
			*/

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
