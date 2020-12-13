/*
 * AppointmentAction.java
 *
 * Created on November 11, 2007, 7:01 PM
 * 
 */

package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.io.PrintWriter;
import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

import org.apache.torque.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
AppointmentAction
	extends Action
{
	
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in AppointmentAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		
		HttpSession session = _request.getSession(false);
		String session_str = session.getId();

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
				
				/*
				appointment = (AppointmentBean)session.getAttribute("adminAppointment");
				
				if (appointment == null)
				{
				    appointment = new AppointmentBean();
				    session.setAttribute("adminAppointment", appointment);
				}
				 */
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 * <form-bean       name="appointmentForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="appointmentSelect" type="java.lang.Integer"/>
      <form-property name="statusSelect" type="java.lang.Short"/>
      <form-property name="clientSelect" type="java.lang.Integer[]"/>
      <form-property name="practitionerSelect" type="java.lang.Integer"/>
      <form-property name="typeSelect" type="java.lang.Integer"/>
      <form-property name="parentSelect" type="java.lang.Integer"/>
      <form-property name="appointmentDateInput" type="java.lang.String"/>
      <form-property name="appointmentHourInput" type="java.lang.Integer"/>
      <form-property name="appointmentMinuteInput" type="java.lang.Integer"/>
      <form-property name="appointmentAMPMInput" type="java.lang.Integer"/>
      <form-property name="duration" type="java.lang.String"/>
      <form-property name="appt_date" type="java.lang.String"/>
      <form-property name="hr" type="java.lang.String"/>
      <form-property name="mn" type="java.lang.String"/>
      <form-property name="ampm" type="java.lang.String"/>
      <form-property name="newAppointmentTimeInput" type="java.lang.String"/>
      <form-property name="comments" type="java.lang.String"/>
	  
      <form-property name="recurVal" type="java.lang.Integer"/>
      <form-property name="recurPeriod" type="java.lang.Integer"/>
      <form-property name="recurPW" type="java.lang.Integer"/>
      <form-property name="recurPWS" type="java.lang.Integer"/>
      <form-property name="stopAfterN" type="java.lang.Integer"/>
      <form-property name="stopAfterDate" type="java.lang.String"/>
    </form-bean>
			 */
			

			boolean include_christine = false;
			
			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements())
			{
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));
				if (param_name.equals("include_christine") && _request.getParameter(param_name).toLowerCase().equals("on")) {
					include_christine = true;
				}
			}
			
			Integer appointmentSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "appointmentSelect");
			Short statusSelect = (Short)PropertyUtils.getSimpleProperty(_form, "statusSelect");
			Integer[] clientSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "clientSelect");
			Integer practitionerSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "practitionerSelect");
			Integer roomSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "roomSelect");
			Integer typeSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "typeSelect");
			Integer parentSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "parentSelect");
			String appointmentDateInput = (String)PropertyUtils.getSimpleProperty(_form, "appointmentDateInput");
			Integer appointmentHourInput = (Integer)PropertyUtils.getSimpleProperty(_form, "appointmentHourInput");
			Integer appointmentMinuteInput = (Integer)PropertyUtils.getSimpleProperty(_form, "appointmentMinuteInput");
			Integer appointmentAMPMInput = (Integer)PropertyUtils.getSimpleProperty(_form, "appointmentAMPMInput");
			String duration = (String)PropertyUtils.getSimpleProperty(_form, "duration");
			String comments = (String)PropertyUtils.getSimpleProperty(_form, "comments");

			String appt_date = (String)PropertyUtils.getSimpleProperty(_form, "appt_date");
			
			String newAppointmentTimeInput;
			
			String hr = (String)PropertyUtils.getSimpleProperty(_form, "hr");
			String mn = (String)PropertyUtils.getSimpleProperty(_form, "mn");
			String ampm = (String)PropertyUtils.getSimpleProperty(_form, "ampm");
			
			if (hr.equals(""))
				newAppointmentTimeInput = (String)PropertyUtils.getSimpleProperty(_form, "newAppointmentTimeInput");
			else
				newAppointmentTimeInput = hr + ":" + mn + " " + ampm;
			
			
			
			Short recurVal = (Short)PropertyUtils.getSimpleProperty(_form, "recurVal");
			Short recurPeriod = (Short)PropertyUtils.getSimpleProperty(_form, "recurPeriod");
			Short recurPW = (Short)PropertyUtils.getSimpleProperty(_form, "recurPW");
			Short recurPWS = (Short)PropertyUtils.getSimpleProperty(_form, "recurPWS");
			Short stopAfterN = (Short)PropertyUtils.getSimpleProperty(_form, "stopAfterN");
			String stopAfterDate = (String)PropertyUtils.getSimpleProperty(_form, "stopAfterDate");
			

			System.out.println("appointmentSelect >" + appointmentSelect);
			System.out.println("statusSelect >" + statusSelect);
			System.out.println("clientSelect >" + clientSelect.length);
			System.out.println("practitionerSelect >" + practitionerSelect);
			System.out.println("roomSelect >" + roomSelect);
			System.out.println("typeSelect >" + typeSelect);
			System.out.println("parentSelect >" + parentSelect);
			System.out.println("appointmentDateInput >" + appointmentDateInput);
			System.out.println("appointmentHourInput >" + appointmentHourInput);
			System.out.println("appointmentMinuteInput >" + appointmentMinuteInput);
			System.out.println("appointmentAMPMInput >" + appointmentAMPMInput);
			System.out.println("duration >" + duration);
			System.out.println("comments >" + comments);

			System.out.println("appt_date >" + appt_date);
			System.out.println("newAppointmentTimeInput >" + newAppointmentTimeInput);
			
			
			System.out.println("recurVal >" + recurVal);
			System.out.println("recurPeriod >" + recurPeriod);
			System.out.println("recurPW >" + recurPW);
			System.out.println("recurPWS >" + recurPWS);
			System.out.println("stopAfterN >" + stopAfterN);
			System.out.println("stopAfterDate >" + stopAfterDate);
			
			
			
 


			if (comments.indexOf('\n') > -1)
				throw new IllegalValueException("Comments can\'t include carriage returns.");
			else if (comments.indexOf('<') > -1)
				throw new IllegalValueException("Comments can\'t include the \"<\" character.");
			else if (comments.indexOf('>') > -1)
				throw new IllegalValueException("Comments can\'t include the \">\" character.");

			//comments = comments.replaceAll("\"", "\\\\\"");
			comments = comments.replaceAll("\"", "'");
			

			if ((_request.getParameter("delete_id") != null) && !_request.getParameter("delete_id").equals("0")) {
			    //PracticeAreaBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
			} else {
				Calendar appointment_date = Calendar.getInstance();
				if (appt_date != null) {
					//SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy - h:mm a");
					try {
						appointment_date.setTime(CUBean.getDateFromUserString(appt_date + " - " + newAppointmentTimeInput, "EEEE, MMMM d, yyyy - h:mm a"));
					} catch (java.text.ParseException x) {
						// 1/28/2013 - 10:30 AM
						appointment_date.setTime(CUBean.getDateFromUserString(appt_date + " - " + newAppointmentTimeInput, "MM/dd/yyyy - h:mm a"));
					}
				} else {
					appointment_date.setTime(CUBean.getDateFromUserString(appointmentDateInput));
					appointment_date.set(Calendar.HOUR, appointmentHourInput.intValue());
					appointment_date.set(Calendar.MINUTE, appointmentMinuteInput.intValue());
					appointment_date.set(Calendar.AM_PM, appointmentAMPMInput.intValue());
				}
				
				if (practitionerSelect.intValue() < 1) {
					throw new IllegalValueException("You must select a practitioner.");
				}
				
				if (typeSelect.intValue() < 1) {
					throw new IllegalValueException("You must select an appointment type.");
				}

				AppointmentTypeBean appt_type_obj = AppointmentTypeBean.getAppointmentType(typeSelect.intValue());
				
				
				
				
				

				String key1 = admin_company.getId() + "|" + CUBean.getUserDateString(appointment_date.getTime());
				String key2 = admin_company.getId() + "|" + appt_type_obj.getPracticeAreaId() + "|" + CUBean.getUserDateString(appointment_date.getTime());

				Calendar start_of_week = Calendar.getInstance();
				start_of_week.setTime(appointment_date.getTime());
				Calendar end_of_week = Calendar.getInstance();
				end_of_week.setTime(appointment_date.getTime());

				start_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				end_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

				String key3 = admin_company.getId() + "|" + practitionerSelect.intValue() + "|" + CUBean.getUserDateString(start_of_week.getTime()) + "|" + CUBean.getUserDateString(end_of_week.getTime());
				String key6 = admin_company.getId() + "|" + CUBean.getUserDateString(start_of_week.getTime()) + "|" + CUBean.getUserDateString(end_of_week.getTime());
				
				
				
				/*
				String key;
				if ((_practice_area != null) && (!_practice_area.isNew()))
					key = _company.getId() + "|" + _practice_area.getId() + "|" + CUBean.getUserDateString(_start_date) + "|" + CUBean.getUserDateString(_end_date);
				else
					key = _company.getId() + "|" + CUBean.getUserDateString(_start_date) + "|" + CUBean.getUserDateString(_end_date);
				*/
				
				
				Calendar start_of_month = Calendar.getInstance();
				start_of_month.setTime(appointment_date.getTime());
				Calendar end_of_month = Calendar.getInstance();
				end_of_month.setTime(appointment_date.getTime());
				start_of_month.set(Calendar.DATE, 1);
				end_of_month.set(Calendar.DATE, start_of_month.getActualMaximum(Calendar.DATE));
				
				String key4 = admin_company.getId() + "|" + CUBean.getUserDateString(start_of_month.getTime()) + "|" + CUBean.getUserDateString(end_of_month.getTime());
				String key5 = admin_company.getId() + "|" + appt_type_obj.getPracticeAreaId() + "|" + CUBean.getUserDateString(start_of_month.getTime()) + "|" + CUBean.getUserDateString(end_of_month.getTime());
				
				
				
				//System.out.println("key1 >" + key1);
				//System.out.println("contains >" + AppointmentBean.appointment_hash.containsKey(key1));
				//System.out.println("key2 >" + key2);
				//System.out.println("contains >" + AppointmentBean.appointment_hash.containsKey(key2));
				//System.out.println("key3 >" + key3);
				//System.out.println("contains >" + AppointmentBean.appointment_hash.containsKey(key3));

				AppointmentBean.appointment_hash.remove(key1);
				AppointmentBean.appointment_hash.remove(key2);
				AppointmentBean.appointment_hash.remove(key3);

				AppointmentBean.appointment_hash.remove(key4);
				AppointmentBean.appointment_hash.remove(key5);
				AppointmentBean.appointment_hash.remove(key6);
			    
			    //int i = 0;
				
				AppointmentAction.saveAppointment(admin_company, (UKOnlinePersonBean)loginBean.getPerson(), appointmentSelect, typeSelect, statusSelect, practitionerSelect, clientSelect, appointment_date, Short.parseShort(duration), comments, recurVal, recurPeriod, recurPW, recurPWS, stopAfterN, stopAfterDate, include_christine);

				Boolean needs_update = (Boolean)com.badiyan.uk.online.servlets.ScheduleServlet.needs_update_hash.get(session_str);
				if (needs_update != null) {
					if (!needs_update.booleanValue())
						com.badiyan.uk.online.servlets.ScheduleServlet.needs_update_hash.put(session_str, new Boolean(true));
				}

				Boolean needs_update_plastique = (Boolean)com.badiyan.uk.online.servlets.ScheduleServletPlastique.needs_update_hash.get(session_str);
				if (needs_update_plastique != null) {
					if (!needs_update_plastique.booleanValue()) {
						com.badiyan.uk.online.servlets.ScheduleServletPlastique.needs_update_hash.put(session_str, Boolean.TRUE);
					}
				}

			    //session.removeAttribute("adminAppointment");

				Date update_timestamp = new Date();
				AppointmentBean.appointment_update_hash.put(key1, update_timestamp);
				AppointmentBean.appointment_update_hash.put(key2, update_timestamp);
				AppointmentBean.appointment_update_hash.put(key3, update_timestamp);
				
				AppointmentBean.appointment_update_hash.put(key4, update_timestamp);
				AppointmentBean.appointment_update_hash.put(key5, update_timestamp);
				AppointmentBean.appointment_update_hash.put(key6, update_timestamp);	
			}

		} catch (Exception x) {
			System.out.println("****   fail ,,,");
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
		
		PrintWriter writer = _response.getWriter();
		writer.println("{\"logOut\": {  \"key\": \"c6793da3-1b58-46f0-8874-41bf99b9b672\"}}");

		// Forward control to the specified success URI
		return (_mapping.findForward(forwardString));
	}
	
	public static AppointmentBean
	saveAppointment(CompanyBean _admin_company, UKOnlinePersonBean _mod_person, int _appointmentSelect, int _typeSelect, short _statusSelect, int _practitionerSelect, Integer[] _clientSelect, Calendar _appointment_date, short _duration, String _comments, short _recurVal, short _recurPeriod, short _recurPW, short _recurPWS, short _stopAfterN, String _stopAfterDate, boolean _include_christine) throws TorqueException, ObjectNotFoundException, IllegalValueException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, Exception {
		
		AppointmentBean appointment = null;
		AppointmentTypeBean appt_type_obj = AppointmentTypeBean.getAppointmentType(_typeSelect);
		
		if (appt_type_obj.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE) {

			if (_clientSelect.length < 1) {
				throw new IllegalValueException("You must select a client.");
			}

			for (int i = 0; i < _clientSelect.length; i++) {
				// is this a recurring appointment? value must be present for recurVal or recurPW

				AppointmentBean orig_appointment = null;
				String orig_comments = null;
				short orig_state = 0;

				boolean appointment_being_rescheduled = false;

				if (_appointmentSelect > 0) {

					orig_appointment = AppointmentBean.getAppointment(_appointmentSelect);

					if (orig_appointment.isCheckedOut()) {
						throw new IllegalValueException("Cannot edit appointment.  " + orig_appointment.getLabel() + " is already checked out.");
					}

					Date orig_appointment_date = orig_appointment.getAppointmentDate();

					//boolean appointment_being_rescheduled = orig_appointment_date.before(appointment_date.getTime()) || orig_appointment_date.after(appointment_date.getTime());
					Calendar orig_cal = Calendar.getInstance();
					orig_cal.setTime(orig_appointment_date);
					appointment_being_rescheduled = !((orig_cal.get(Calendar.DATE) == _appointment_date.get(Calendar.DATE)) && (orig_cal.get(Calendar.MONTH) == _appointment_date.get(Calendar.MONTH)) && (orig_cal.get(Calendar.YEAR) == _appointment_date.get(Calendar.YEAR)));

					UKOnlinePersonBean orig_practitioner = orig_appointment.getPractitioner();
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_practitionerSelect);
					boolean changing_practitioners = !orig_practitioner.equals(practitioner);

					if (appointment_being_rescheduled || changing_practitioners) {

						// this appointment is being rescheduled

						orig_comments = orig_appointment.getComments();
						orig_state = orig_appointment.getState();

						orig_appointment.setState(AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS);
						orig_appointment.setIsFirstTimeAppointmentForClientInPracticeArea(false);
						if (changing_practitioners) {
							if (appointment_being_rescheduled)
								orig_appointment.setComments("Rescheduled to " + CUBean.getUserDateString(_appointment_date.getTime(), "EEEE, MMMM d - h:mm a") + " for " + practitioner.getLabel());
							else
								orig_appointment.setComments("Rescheduled to " + CUBean.getUserDateString(_appointment_date.getTime(), "h:mm a") + " for " + practitioner.getLabel());
						} else {
							orig_appointment.setComments("Rescheduled to " + CUBean.getUserDateString(_appointment_date.getTime(), "EEEE, MMMM d - h:mm a"));
						}

						orig_appointment.save();
						appointment = new AppointmentBean();
						appointment.setParent(orig_appointment);
					} else {
						appointment = orig_appointment;
					}
				} else {
					appointment = new AppointmentBean();
				}

				System.out.println("  *** B >" + appointment.isNew());

				boolean is_new = appointment.isNew();
				boolean changing_recur = false;
				boolean recurring = (!((_recurVal == 0) && (_recurPW == 0)));
				
				if (recurring) {
					// if it's a recurring appt, either stop after or stop by date need to be set

					if ((_stopAfterN == 0) && _stopAfterDate.isEmpty()) {
						throw new IllegalValueException("You must specify either a number of occurences or a stop by date for a recurring appointment.");
					}
					if (!is_new) {
						// this is a recurring appointment that's being edited...

						// is anything changing???

						// if this is a child appt, I need to see if we're changing on the parent

						AppointmentBean test_recur_change_appt = appointment;

						if (appointment.hasParent())
							test_recur_change_appt = appointment.getParent();

						if ((test_recur_change_appt.getRecurVal() != _recurVal) && (_recurVal > 0))
							changing_recur = true;
						if ((test_recur_change_appt.getRecurPeriod() != _recurPeriod) && (_recurPeriod > 0))
							changing_recur = true;
						if ((test_recur_change_appt.getRecurPW() != _recurPW) && (_recurPW > 0))
							changing_recur = true;
						if ((test_recur_change_appt.getRecurPWS() != _recurPWS) && (_recurPWS > 0))
							changing_recur = true;
						if ((test_recur_change_appt.getRecurStopAfterN() != _stopAfterN) && (_stopAfterN > 0))
							changing_recur = true;
						if (!_stopAfterDate.isEmpty()) {
							Date stopAfterDateObj = CUBean.getDateFromUserString(_stopAfterDate, "EEEE, MMMM d, yyyy");
							Date existing_stopAfterDateObj = test_recur_change_appt.getRecurStopByDate();
							if ((existing_stopAfterDateObj == null) || (stopAfterDateObj.compareTo(existing_stopAfterDateObj) != 0))
								changing_recur = true;
						}
					}

					setRecurValues(appointment, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
					if (appointment.hasParent()) {
						AppointmentBean parent = appointment.getParent();
						setRecurValues(parent, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
						parent.save();
					}

				}

				UKOnlinePersonBean orig_practitioner = null;
				if (!appointment.isNew()) {
					orig_practitioner = appointment.getPractitioner();
				}
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_practitionerSelect);
				appointment.setPractitioner(practitioner);

				/*
				TreatmentRoomBean treatment_room = TreatmentRoomBean.getTreatmentRoom(roomSelect.intValue());
				appointment.setRoom(treatment_room);
				*/

				appointment.setAppointmentDate(_appointment_date.getTime());
				appointment.setDuration(_duration);

				if (AppointmentBean.hasPractitionerScheduleConflict(appointment)) {
					// it conflicts, but is it allowed for this appointment type?

					AppointmentTypeBean practitioner_offtime_appointment_type = AppointmentTypeBean.getPractitionerScheduleOfftimeAppointmentType(_admin_company);

					if (!practitioner_offtime_appointment_type.isAllowedAppointmentType(appt_type_obj)) {
						if (orig_practitioner != null)
							appointment.setPractitioner(orig_practitioner);

						if (orig_appointment != null)
						{
							orig_appointment.setState(orig_state);
							orig_appointment.setComments(orig_comments);
							orig_appointment.save();
						}

						throw new Exception("Can't create appointment.  It conflicts with the practitioner schedule for " + practitioner.getLabel() + ".");
					}
				}

				appointment.setType(appt_type_obj);

				appointment.setCompany(_admin_company);
				appointment.setCreateOrModifyPerson(_mod_person);
				UKOnlinePersonBean client_person = null;
				if (appt_type_obj.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE) {
					client_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_clientSelect[i]);
					appointment.setClient(client_person);
				}

				appointment.setComments(_comments);

				if (_statusSelect > 0) {
					Calendar now = Calendar.getInstance();
					if (_appointment_date.after(now)) {
						// clear appt status
						appointment.setState(AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
					} else {
						appointment.setState(_statusSelect);
					}
				} else {
					appointment.setState(AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
				}

				appointment.save();

				if (practitioner.getId() != 3121) {
					if (_include_christine) {
						saveChildAppointmentChristine(appointment, _admin_company, _mod_person, client_person, practitioner, appt_type_obj, _comments, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
					}
				}

				if (appt_type_obj.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE) {
					BlackHoleBean black_hole = BlackHoleBean.getInstance(_admin_company);
					try {
						if (client_person.updateBlackHoleStatus(black_hole)) {
							black_hole.save();
						}
					} catch (Exception x) {
						x.printStackTrace();
					}
				}

				Vector children = null;
				Calendar recur_start_date = Calendar.getInstance();
				if (appointment.hasParent()) {
					AppointmentBean parent = appointment.getParent();
					children = parent.getChildren();
					recur_start_date.setTime(parent.getAppointmentDate());
				} else {
					children = appointment.getChildren();
					recur_start_date.setTime(_appointment_date.getTime());
				}

				int children_used = 0;

				//System.out.println("num children >" + children.size());

				//System.out.println("recurring >" + recurring);
				//System.out.println("is_new >" + is_new);
				//System.out.println("changing_recur >" + changing_recur);
				//System.out.println("appointment_being_rescheduled >" + appointment_being_rescheduled);

				if (recurring && !appointment_being_rescheduled) {
					// this is a recurring appointment.  I need to create the children...

					if (is_new || changing_recur) {

						if (_recurVal > 0) {
							int add_field = 0;
							switch (_recurPeriod) {
								case (short)1: add_field = Calendar.HOUR_OF_DAY; break;
								case (short)2: add_field = Calendar.DATE; break;
								case (short)3: add_field = Calendar.WEEK_OF_YEAR; break;
								case (short)4: add_field = Calendar.MONTH; break;
								case (short)5: add_field = Calendar.YEAR; break;
							}

							if (_stopAfterN > 0) {
								for (short occurrences = 1; occurrences < _stopAfterN; occurrences++) {
									AppointmentBean recurring_appt = null;
									if (occurrences <= children.size()) {
										recurring_appt = (AppointmentBean)children.elementAt(occurrences - 1);
										children_used++;
									} else {
										recurring_appt = new AppointmentBean();
									}
									recur_start_date.add(add_field, _recurVal);
									saveChildAppointment(appointment.hasParent() ? appointment.getParent() : appointment, recurring_appt, _admin_company, _mod_person, client_person, practitioner, appt_type_obj, _comments, recur_start_date.getTime(), _duration, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
								}
							}
							else
							{
								Calendar sad_obj = Calendar.getInstance();
								//sad_obj.setTime(CUBean.getDateFromUserString(stopAfterDate));
								sad_obj.setTime(CUBean.getDateFromUserString(_stopAfterDate, "EEEE, MMMM d, yyyy"));
								recur_start_date.add(add_field, _recurVal);
								for (int x = 0; recur_start_date.before(sad_obj); x++)
								{
									AppointmentBean recurring_appt = null;
									if (x < children.size())
									{
										recurring_appt = (AppointmentBean)children.elementAt(x);
										children_used++;
									}
									else
										recurring_appt = new AppointmentBean();
									saveChildAppointment(appointment.hasParent() ? appointment.getParent() : appointment, recurring_appt, _admin_company, _mod_person, client_person, practitioner, appt_type_obj, _comments, recur_start_date.getTime(), _duration, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
									recur_start_date.add(add_field, _recurVal);
								}
							}
						} else if (_recurPW > 0) {
							if (_stopAfterN > 0) {
								for (short occurrences = 1; occurrences < _stopAfterN; occurrences++) {
									AppointmentBean recurring_appt = null;
									if (occurrences <= children.size()) {
										recurring_appt = (AppointmentBean)children.elementAt(occurrences - 1);
										children_used++;
									} else {
										recurring_appt = new AppointmentBean();
									}
									getDayAndWeekInNextMonth(recur_start_date, _recurPWS, _recurPW);
									saveChildAppointment(appointment.hasParent() ? appointment.getParent() : appointment, recurring_appt, _admin_company, _mod_person, client_person, practitioner, appt_type_obj, _comments, recur_start_date.getTime(), _duration, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
								}
							} else {
								Calendar sad_obj = Calendar.getInstance();
								//sad_obj.setTime(CUBean.getDateFromUserString(stopAfterDate));
								sad_obj.setTime(CUBean.getDateFromUserString(_stopAfterDate, "EEEE, MMMM d, yyyy"));
								getDayAndWeekInNextMonth(recur_start_date, _recurPWS, _recurPW);
								for (int x = 0; recur_start_date.before(sad_obj); x++) {
									AppointmentBean recurring_appt = null;
									if (x < children.size()) {
										recurring_appt = (AppointmentBean)children.elementAt(x);
										children_used++;
									} else {
										recurring_appt = new AppointmentBean();
									}
									saveChildAppointment(appointment.hasParent() ? appointment.getParent() : appointment, recurring_appt, _admin_company, _mod_person, client_person, practitioner, appt_type_obj, _comments, recur_start_date.getTime(), _duration, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
									getDayAndWeekInNextMonth(recur_start_date, _recurPWS, _recurPW);
								}
							}
						}


					}

				}

				// are there remaining children that might need to be removed???

				if (changing_recur)
				{
					for (int rem_child_i = children.size() - 1; rem_child_i >= children_used; rem_child_i--)
					{
						AppointmentBean child_to_delete = (AppointmentBean)children.elementAt(rem_child_i);
						AppointmentBean.delete(child_to_delete.getId());
					}

					appointment.invalidateChildren();
				}

				_appointment_date.add(Calendar.MINUTE, _duration);

			}
		} else {
			if (_appointmentSelect > 0) {
				appointment = AppointmentBean.getAppointment(_appointmentSelect);
			} else {
				appointment = new AppointmentBean();
			}
			
			if (appointment.isCheckedOut()) {
				throw new IllegalValueException("Cannot edit appointment.  " + appointment.getLabel() + " is already checked out.");
			}
			boolean is_new = appointment.isNew();
			boolean changing_recur = false;

			boolean recurring = (!((_recurVal == 0) && (_recurPW == 0)));
			if (recurring) {
				// if it's a recurring appt, either stop after or stop by date need to be set

				if ((_stopAfterN == 0) && _stopAfterDate.isEmpty()) {
					throw new IllegalValueException("You must specify either a number of occurences or a stop by date for a recurring appointment.");
				}
				if (!is_new) {
					// this is a recurring appointment that's being edited...

					// is anything changing???

					// if this is a child appt, I need to see if we're changing on the parent

					AppointmentBean test_recur_change_appt = appointment;

					if (appointment.hasParent())
						test_recur_change_appt = appointment.getParent();

					if ((test_recur_change_appt.getRecurVal() != _recurVal) && (_recurVal > 0))
						changing_recur = true;
					if ((test_recur_change_appt.getRecurPeriod() != _recurPeriod) && (_recurPeriod > 0))
						changing_recur = true;
					if ((test_recur_change_appt.getRecurPW() != _recurPW) && (_recurPW > 0))
						changing_recur = true;
					if ((test_recur_change_appt.getRecurPWS() != _recurPWS) && (_recurPWS > 0))
						changing_recur = true;
					if ((test_recur_change_appt.getRecurStopAfterN() != _stopAfterN) && (_stopAfterN > 0))
						changing_recur = true;
					if (!_stopAfterDate.isEmpty())
					{
						Date stopAfterDateObj = CUBean.getDateFromUserString(_stopAfterDate, "EEEE, MMMM d, yyyy");
						Date existing_stopAfterDateObj = test_recur_change_appt.getRecurStopByDate();
						if ((existing_stopAfterDateObj == null) || (stopAfterDateObj.compareTo(existing_stopAfterDateObj) != 0)) {
							changing_recur = true;
						}
					}
				}

				setRecurValues(appointment, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
				if (appointment.hasParent()) {
					AppointmentBean parent = appointment.getParent();
					setRecurValues(parent, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
					parent.save();
				}
			}

			UKOnlinePersonBean orig_practitioner = null;
			if (!appointment.isNew()) {
				orig_practitioner = appointment.getPractitioner();
			}
			UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_practitionerSelect);

			appointment.setPractitioner(practitioner);

			/*
			TreatmentRoomBean treatment_room = TreatmentRoomBean.getTreatmentRoom(roomSelect.intValue());
			appointment.setRoom(treatment_room);
			*/

			appointment.setAppointmentDate(_appointment_date.getTime());
			appointment.setDuration(_duration);

			if (AppointmentBean.hasPractitionerScheduleConflict(appointment)) {
				// it conflicts, but is it allowed for this appointment type?

				AppointmentTypeBean practitioner_offtime_appointment_type = AppointmentTypeBean.getPractitionerScheduleOfftimeAppointmentType(_admin_company);

				if (!practitioner_offtime_appointment_type.isAllowedAppointmentType(appt_type_obj)) {
					if (orig_practitioner != null) {
						appointment.setPractitioner(orig_practitioner);
					}
					throw new Exception("Can't create appointment.  It conflicts with the practitioner schedule for " + practitioner.getLabel() + ".");
				}
			}


			/*
			if (statusSelect.shortValue() > 0)
				appointment.setState(statusSelect.shortValue());
			else
				appointment.setState(AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
			 */


			if (_statusSelect > 0) {
				Calendar now = Calendar.getInstance();
				if (_appointment_date.after(now)) {
					// clear appt status
					appointment.setState(AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
				} else {
					appointment.setState(_statusSelect);
				}
			} else {
				appointment.setState(AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
			}


			appointment.setCompany(_admin_company);
			appointment.setCreateOrModifyPerson(_mod_person);
			UKOnlinePersonBean client_person = null;
			if (appt_type_obj.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE) {
				client_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_clientSelect[0]);
				appointment.setClient(client_person);
			}

			appointment.setType(appt_type_obj);
			appointment.setComments(_comments);

			appointment.save();

			if (appt_type_obj.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
			{
				if (appt_type_obj.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE)
				{
					BlackHoleBean black_hole = BlackHoleBean.getInstance(_admin_company);
					if (client_person.updateBlackHoleStatus(black_hole))
						black_hole.save();
				}
			}

			Vector children = null;
			Calendar recur_start_date = Calendar.getInstance();
			if (appointment.hasParent())
			{
				AppointmentBean parent = appointment.getParent();
				children = parent.getChildren();
				recur_start_date.setTime(parent.getAppointmentDate());
			}
			else
			{
				children = appointment.getChildren();
				recur_start_date.setTime(_appointment_date.getTime());
			}

			int children_used = 0;

			//System.out.println("num children >" + children.size());

			//System.out.println("recurring >" + recurring);
			//System.out.println("is_new >" + is_new);
			//System.out.println("changing_recur >" + changing_recur);

			if (recurring)
			{
				// this is a recurring appointment.  I need to create the children...

				if (is_new || changing_recur)
				{

					if (_recurVal > 0) {
						int add_field = 0;
						switch (_recurPeriod) {
							case (short)1: add_field = Calendar.HOUR_OF_DAY; break;
							case (short)2: add_field = Calendar.DATE; break;
							case (short)3: add_field = Calendar.WEEK_OF_YEAR; break;
							case (short)4: add_field = Calendar.MONTH; break;
							case (short)5: add_field = Calendar.YEAR; break;
						}

						if (_stopAfterN > 0) {
							for (short occurrences = 1; occurrences < _stopAfterN; occurrences++) {
								AppointmentBean recurring_appt = null;
								if (occurrences <= children.size()) {
									recurring_appt = (AppointmentBean)children.elementAt(occurrences - 1);
									children_used++;
								} else {
									recurring_appt = new AppointmentBean();
								}
								recur_start_date.add(add_field, _recurVal);
								saveChildAppointment(appointment.hasParent() ? appointment.getParent() : appointment, recurring_appt, _admin_company, _mod_person, client_person, practitioner, appt_type_obj, _comments, recur_start_date.getTime(), _duration, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
							}
						} else {
							Calendar sad_obj = Calendar.getInstance();
							//sad_obj.setTime(CUBean.getDateFromUserString(stopAfterDate));
							sad_obj.setTime(CUBean.getDateFromUserString(_stopAfterDate, "EEEE, MMMM d, yyyy"));
							recur_start_date.add(add_field, _recurVal);
							for (int x = 0; recur_start_date.before(sad_obj); x++) {
								AppointmentBean recurring_appt = null;
								if (x < children.size()) {
									recurring_appt = (AppointmentBean)children.elementAt(x);
									children_used++;
								} else {
									recurring_appt = new AppointmentBean();
								}
								saveChildAppointment(appointment.hasParent() ? appointment.getParent() : appointment, recurring_appt, _admin_company, _mod_person, client_person, practitioner, appt_type_obj, _comments, recur_start_date.getTime(), _duration, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
								recur_start_date.add(add_field, _recurVal);
							}
						}
					} else if (_recurPW > 0) {
						if (_stopAfterN > 0) {
							for (short occurrences = 1; occurrences < _stopAfterN; occurrences++) {
								AppointmentBean recurring_appt = null;
								if (occurrences <= children.size()) {
									recurring_appt = (AppointmentBean)children.elementAt(occurrences - 1);
									children_used++;
								} else {
									recurring_appt = new AppointmentBean();
								}
								getDayAndWeekInNextMonth(recur_start_date, _recurPWS, _recurPW);
								saveChildAppointment(appointment.hasParent() ? appointment.getParent() : appointment, recurring_appt, _admin_company, _mod_person, client_person, practitioner, appt_type_obj, _comments, recur_start_date.getTime(), _duration, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
							}
						} else {
							Calendar sad_obj = Calendar.getInstance();
							//sad_obj.setTime(CUBean.getDateFromUserString(stopAfterDate));
							sad_obj.setTime(CUBean.getDateFromUserString(_stopAfterDate, "EEEE, MMMM d, yyyy"));
							getDayAndWeekInNextMonth(recur_start_date, _recurPWS, _recurPW);
							for (int x = 0; recur_start_date.before(sad_obj); x++) {
								AppointmentBean recurring_appt = null;
								if (x < children.size()) {
									recurring_appt = (AppointmentBean)children.elementAt(x);
									children_used++;
								} else {
									recurring_appt = new AppointmentBean();
								}
								saveChildAppointment(appointment.hasParent() ? appointment.getParent() : appointment, recurring_appt, _admin_company, _mod_person, client_person, practitioner, appt_type_obj, _comments, recur_start_date.getTime(), _duration, recurring, _recurVal, _recurPeriod, _recurPW, _recurPWS, _stopAfterN, _stopAfterDate);
								getDayAndWeekInNextMonth(recur_start_date, _recurPWS, _recurPW);
							}
						}
					}


				}

			}

			// are there remaining children that might need to be removed???

			if (changing_recur) {
				for (int rem_child_i = children.size() - 1; rem_child_i >= children_used; rem_child_i--) {
					AppointmentBean child_to_delete = (AppointmentBean)children.elementAt(rem_child_i);
					AppointmentBean.delete(child_to_delete.getId());
				}

				appointment.invalidateChildren();
			}
		}
		
		return appointment;
	}
	
	private static void
	saveChildAppointment(AppointmentBean _parent,
							AppointmentBean _appt,
							CompanyBean _company,
							UKOnlinePersonBean _person,
							UKOnlinePersonBean _client,
							UKOnlinePersonBean _practitioner,
							AppointmentTypeBean _type,
							String _comments,
							Date _date,
							short _duration,
							boolean _recurring,
							short _recurVal,
							short _recurPeriod,
							short _recurPW,
							short _recurPWS,
							short _stopAfterN,
							String _stopAfterDate)
		throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		_appt.setAppointmentDate(_date);
			
		if (_appt.isNew())
		{
			_appt.setState(AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
			_appt.setParent(_parent);
			_appt.setCompany(_company);
			_appt.setCreateOrModifyPerson(_person);
			if (_client != null)
				_appt.setClient(_client);
			_appt.setPractitioner(_practitioner);
			_appt.setType(_type);
			_appt.setComments(_comments);
			_appt.setDuration(_duration);
		}
		
		_appt.setIsRecurring(_recurring);
		_appt.setRecurVal(_recurVal);
		_appt.setRecurPeriod(_recurPeriod);
		_appt.setRecurPW(_recurPW);
		_appt.setRecurPWS(_recurPWS);
		_appt.setRecurStopAfterN(_stopAfterN);
		if (!_stopAfterDate.equals("")) {
			_appt.setRecurStopByDate(CUBean.getDateFromUserString(_stopAfterDate, "EEEE, MMMM d, yyyy"));
		}
		
		_appt.save();


		String key1 = _company.getId() + "|" + CUBean.getUserDateString(_date);
		String key2 = _company.getId() + "|" + _type.getPracticeAreaId() + "|" + CUBean.getUserDateString(_date);

		Calendar start_of_week = Calendar.getInstance();
		start_of_week.setTime(_date);
		Calendar end_of_week = Calendar.getInstance();
		end_of_week.setTime(_date);

		start_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		end_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

		String key3 = _company.getId() + "|" + _practitioner.getId() + "|" + CUBean.getUserDateString(start_of_week.getTime()) + "|" + CUBean.getUserDateString(end_of_week.getTime());

		AppointmentBean.appointment_hash.remove(key1);
		AppointmentBean.appointment_hash.remove(key2);
		AppointmentBean.appointment_hash.remove(key3);

		Date update_timestamp = new Date();
		AppointmentBean.appointment_update_hash.put(key1, update_timestamp);
		AppointmentBean.appointment_update_hash.put(key2, update_timestamp);
		AppointmentBean.appointment_update_hash.put(key3, update_timestamp);
	}
	
	private static void
	saveChildAppointmentChristine(AppointmentBean _appt,
							CompanyBean _company,
							UKOnlinePersonBean _person,
							UKOnlinePersonBean _client,
							UKOnlinePersonBean _practitioner,
							AppointmentTypeBean _type,
							String _comments,
							boolean _recurring,
							short _recurVal,
							short _recurPeriod,
							short _recurPW,
							short _recurPWS,
							short _stopAfterN,
							String _stopAfterDate)
		throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
		System.out.println("saveChildAppointmentChristine() invoked");
		
		AppointmentBean appointment_for_christine = new AppointmentBean();
		
		//_appt.setAppointmentDate(_date);
		appointment_for_christine.setAppointmentDate(_appt.getAppointmentDate());
			
		appointment_for_christine.setState(AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
		appointment_for_christine.setParent(_appt);
		appointment_for_christine.setCompany(_company);
		appointment_for_christine.setCreateOrModifyPerson(_person);
		if (_client != null) {
			appointment_for_christine.setClient(_client);
			appointment_for_christine.setComments(_type.getLabel() + " - " + _client.getLabel());
		} else {
			appointment_for_christine.setComments(_type.getLabel());
		}
		//_appt.setPractitioner(_practitioner);
		UKOnlinePersonBean christine = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(3121);
		appointment_for_christine.setPractitioner(christine);

		AppointmentTypeBean appt_type_for_christine = null;
		if (_practitioner.getFirstNameString().equals("Cara")) {
			appt_type_for_christine = AppointmentTypeBean.getAppointmentType(_company, "Room 1");
		} else if (_practitioner.getFirstNameString().equals("Michelle")) {
			appt_type_for_christine = AppointmentTypeBean.getAppointmentType(_company, "Room 3");
		} else if (_practitioner.getFirstNameString().equals("Stacey")) {
			appt_type_for_christine = AppointmentTypeBean.getAppointmentType(_company, "Room 3");
		}

		appointment_for_christine.setType(appt_type_for_christine);
		appointment_for_christine.setDuration(appt_type_for_christine.getDuration());
			
			
		
		appointment_for_christine.setIsRecurring(_recurring);
		appointment_for_christine.setRecurVal(_recurVal);
		appointment_for_christine.setRecurPeriod(_recurPeriod);
		appointment_for_christine.setRecurPW(_recurPW);
		appointment_for_christine.setRecurPWS(_recurPWS);
		appointment_for_christine.setRecurStopAfterN(_stopAfterN);
		if (!_stopAfterDate.equals("")) {
			appointment_for_christine.setRecurStopByDate(CUBean.getDateFromUserString(_stopAfterDate, "EEEE, MMMM d, yyyy"));
		}
		
		appointment_for_christine.save();


		String key1 = _company.getId() + "|" + CUBean.getUserDateString(appointment_for_christine.getAppointmentDate());
		String key2 = _company.getId() + "|" + _type.getPracticeAreaId() + "|" + CUBean.getUserDateString(appointment_for_christine.getAppointmentDate());

		Calendar start_of_week = Calendar.getInstance();
		start_of_week.setTime(appointment_for_christine.getAppointmentDate());
		Calendar end_of_week = Calendar.getInstance();
		end_of_week.setTime(appointment_for_christine.getAppointmentDate());

		start_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		end_of_week.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

		String key3 = _company.getId() + "|" + _practitioner.getId() + "|" + CUBean.getUserDateString(start_of_week.getTime()) + "|" + CUBean.getUserDateString(end_of_week.getTime());

		AppointmentBean.appointment_hash.remove(key1);
		AppointmentBean.appointment_hash.remove(key2);
		AppointmentBean.appointment_hash.remove(key3);

		Date update_timestamp = new Date();
		AppointmentBean.appointment_update_hash.put(key1, update_timestamp);
		AppointmentBean.appointment_update_hash.put(key2, update_timestamp);
		AppointmentBean.appointment_update_hash.put(key3, update_timestamp);
	}
	
	private static void
	getDayAndWeekInNextMonth(Calendar _appt_date, int _day_of_week, int _week_of_month)
		throws IllegalValueException
	{
		int start_month = _appt_date.get(Calendar.MONTH);
		
		_appt_date.add(Calendar.MONTH, 1);
		int orig_month = _appt_date.get(Calendar.MONTH);
		_appt_date.set(Calendar.WEEK_OF_MONTH, _week_of_month);
		_appt_date.set(Calendar.DAY_OF_WEEK, _day_of_week);
		if (orig_month != _appt_date.get(Calendar.MONTH))
		{
			// messing with the weeks has screwed up the month
			
			if (_week_of_month == 1)
			{
				_appt_date.add(Calendar.MONTH, 1);
				_appt_date.set(Calendar.WEEK_OF_MONTH, 2);
			}
			else
			{
				_appt_date.add(Calendar.MONTH, -1);
				_appt_date.set(Calendar.WEEK_OF_MONTH, _week_of_month - 1);
			}
		}
		
		int end_month = _appt_date.get(Calendar.MONTH);
		if (start_month == end_month)
			throw new IllegalValueException("failed to advance month");
	}
	
	private static void
	setRecurValues(AppointmentBean _appointment, boolean _recurring, short _recurVal, short _recurPeriod, short _recurPW, short _recurPWS, short _stopAfterN, String _stopAfterDate)
		throws java.text.ParseException
	{
		_appointment.setIsRecurring(_recurring);
		_appointment.setRecurVal(_recurVal);
		_appointment.setRecurPeriod(_recurPeriod);
		_appointment.setRecurPW(_recurPW);
		_appointment.setRecurPWS(_recurPWS);
		_appointment.setRecurStopAfterN(_stopAfterN);
		if (!_stopAfterDate.equals("")) {
			_appointment.setRecurStopByDate(CUBean.getDateFromUserString(_stopAfterDate, "EEEE, MMMM d, yyyy"));
		}
	}
}
