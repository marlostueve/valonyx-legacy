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
ScheduleConfirmAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute invoked in ScheduleConfirmAction");
		
		String forward_string = "success";
		

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		HttpSession session = _request.getSession(false);
		
		UKOnlinePersonBean person;
		UKOnlineCompanyBean userCompany;

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				person = (UKOnlinePersonBean)loginBean.getPerson();
				userCompany = (UKOnlineCompanyBean)session.getAttribute("userCompany");
				
			}
			else
				return (_mapping.findForward("session_expired"));

			String start_str = (String)PropertyUtils.getSimpleProperty(_form, "start");
			
			System.out.println("start >" + start_str);
			
			UKOnlinePersonBean selected_practitioner = (UKOnlinePersonBean)session.getAttribute("selected_practitioner");
			AppointmentTypeBean selected_appointment_type = (AppointmentTypeBean)session.getAttribute("selected_appointment_type");
			
			
			Date appt_date = null;
			if (start_str != null) {

				appt_date = new Date(Long.parseLong(start_str) * 1000);

			}
			
			// does this person already have a submitted appointment request?
			
			if (AppointmentBean.hasPendingAppointmentRequest(person)) {
				throw new IllegalValueException("Unable to submit request.  You already have a pending appointment request.");
			}
			
			
			AppointmentBean test_appt = new AppointmentBean();
			test_appt.setCompany(userCompany);
			test_appt.setClient(person);
			test_appt.setPractitioner(selected_practitioner);
			test_appt.setType(selected_appointment_type);
			test_appt.setDuration(selected_appointment_type.getDuration());
			test_appt.setState(AppointmentBean.PENDING_APPOINTMENT_STATUS);
			test_appt.setAppointmentDate(appt_date);
			test_appt.save();
			
			AppointmentBean.invalidateHash();
			
			Vector staff_vec = UKOnlinePersonBean.getCashiers(userCompany);
			Iterator itr = staff_vec.iterator();
			while (itr.hasNext()) {
				UKOnlinePersonBean staff = (UKOnlinePersonBean)itr.next();
				String subj = userCompany.getLabel() + " Appointment Request";
				StringBuffer b = new StringBuffer();
				b.append("Captain " + staff.getLabel() + ",<br /><strong>" + person.getLabel() + "</strong> has requested a <strong>" + selected_appointment_type.getLabel() + "</strong> appointment with <strong>" + selected_practitioner.getLabel() + "</strong> at <strong>" + CUBean.getUserDateString(appt_date) + "</strong> - <strong>" + CUBean.getUserTimeString(appt_date) + "</strong>.<br />");
				b.append("<a style=\"font-size: larger;\" href=\"http://www.valonyx.com/admin/process-appointment-request.jsp?action=approve&id=" + test_appt.getValue() + "\">Approve Request</a><br />");
				b.append("<a style=\"font-size: larger;\" href=\"http://www.valonyx.com/admin/process-appointment-request.jsp?action=deny&id=" + test_appt.getValue() + "\">Deny Request</a><br />");
				if (person.hasEmail()) {
					CUBean.sendEmail("marlo@valonyx.com", person.getEmail1String(), subj, b.toString());
					CUBean.sendEmail("cstueve@sanowc.com", person.getEmail1String(), subj, b.toString());
					CUBean.sendEmail(staff.getEmail1String(), person.getEmail1String(), subj, b.toString());
				} else {
					CUBean.sendEmail("marlo@valonyx.com", "admin@valonyx.com", subj, b.toString());
					CUBean.sendEmail("cstueve@sanowc.com", "admin@valonyx.com", subj, b.toString());
					CUBean.sendEmail(staff.getEmail1String(), "admin@valonyx.com", subj, b.toString());
				}
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