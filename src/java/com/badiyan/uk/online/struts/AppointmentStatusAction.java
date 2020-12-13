/*
 * AppointmentStatusAction.java
 *
 * Created on December 30, 2007, 7:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
AppointmentStatusAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in AppointmentStatusAction");
		String forwardString = "success";

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		AppointmentBean appointment = null;
		
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
			
			Integer appointmentSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "appointmentSelect");
			Short statusSelect = (Short)PropertyUtils.getSimpleProperty(_form, "statusSelect");

			System.out.println("appointmentSelect >" + appointmentSelect);
			System.out.println("statusSelect >" + statusSelect);
			
			appointment = AppointmentBean.getAppointment(appointmentSelect.intValue());
			appointment.setState(statusSelect.shortValue());
			appointment.setCreateOrModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
			appointment.save();
			

			/*
			if ((_request.getParameter("delete_id") != null) && !_request.getParameter("delete_id").equals("0"))
			{
			    //PracticeAreaBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
			}
			else
			{
			    
			    appointment.setCompany(admin_company);
			    appointment.setClient((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(clientSelect.intValue()));
			    appointment.setPractitioner((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(practitionerSelect.intValue()));
			    appointment.setType(AppointmentTypeBean.getAppointmentType(typeSelect.intValue()));
			    
			    Calendar appointment_date = Calendar.getInstance();
			    
			    Date parse_date = null;
			    if (newAppointmentDateInput != null)
			    {
				//SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy - h:mm a");
				appointment_date.setTime(CUBean.getDateFromUserString(newAppointmentDateInput + " - " + newAppointmentTimeInput, "EEEE, MMMM d, yyyy - h:mm a"));
				//CUBean.getDateFromUserString("", 
			    }
			    else
			    {
				appointment_date.setTime(CUBean.getDateFromUserString(appointmentDateInput));
				appointment_date.set(Calendar.HOUR, appointmentHourInput.intValue());
				appointment_date.set(Calendar.MINUTE, appointmentMinuteInput.intValue());
				appointment_date.set(Calendar.AM_PM, appointmentAMPMInput.intValue());
			    }
			    
			    appointment.setAppointmentDate(appointment_date.getTime());
			    appointment.setDuration(Short.parseShort(duration));
			    
			    appointment.save();

			    session.removeAttribute("adminAppointment");
			}
			 */

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
