<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<jsp:useBean id="adminPracticeArea" class="com.badiyan.uk.online.beans.PracticeAreaBean" scope="session" />

<jsp:useBean id="selected_appointment_type" class="com.badiyan.uk.online.beans.AppointmentTypeBean" scope="session" />
<jsp:useBean id="selected_practitioner" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<%

//getNextAvailableAppointmentTimeForPractitioner(UKOnlineCompanyBean _company, UKOnlinePersonBean _practitioner, AppointmentTypeBean _type, Calendar _date, short _duration)
//AppointmentBean.getNextAvailableAppointmentTimeForPractitioner(_company, _practitioner, _type, _date, _duration);
	


AppointmentBean next_available = AppointmentBean.getNextAvailableAppointmentTimeForPractitioner(userCompany, selected_practitioner, selected_appointment_type, Calendar.getInstance(), selected_appointment_type.getDuration());
next_available.setClient((UKOnlinePersonBean)loginBean.getPerson());
	
Vector next_available_appointments = AppointmentBean.getNextAvailableAppointmentTimesForPractitioner(userCompany, selected_practitioner, selected_appointment_type, Calendar.getInstance(), selected_appointment_type.getDuration(), 60);
System.out.println("next_available_appointments >" + next_available_appointments.size());


%>

<!DOCTYPE html>
<html lang="en">
  <head>
	  
	  
	
	<link href='../fullcalendar-1.6.1/fullcalendar/fullcalendar.css' rel='stylesheet' />
	<link href='../fullcalendar-1.6.1/fullcalendar/fullcalendar.print.css' rel='stylesheet' media='print' />
	<script src='../fullcalendar-1.6.1/jquery/jquery-1.9.1.min.js'></script>
	<script src='../fullcalendar-1.6.1/jquery/jquery-ui-1.10.2.custom.min.js'></script>
	<script src='../fullcalendar-1.6.1/fullcalendar/fullcalendar.min.js'></script>
	
	  
<%@ include file="channels\channel-head.jsp" %>

    <style type="text/css">
      body {
        background-color: #f5f5f5;
      }

      .form-signin {
        max-width: 400px;
        padding: 19px 29px 29px;
        margin: 0 auto 20px;
        background-color: #fff;
        border: 1px solid #e5e5e5;
        -webkit-border-radius: 5px;
           -moz-border-radius: 5px;
                border-radius: 5px;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
           -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
                box-shadow: 0 1px 2px rgba(0,0,0,.05);
      }
      .form-signin .form-signin-heading,
      .form-signin .checkbox {
        margin-bottom: 10px;
      }
      .form-signin input[type="text"],
      .form-signin input[type="password"] {
        font-size: 16px;
        height: auto;
        margin-bottom: 15px;
        padding: 7px 9px;
      }

    </style>
	
<style>


	#calendar {
		width: 900px;
		margin: 0 auto;
		}

</style>
	
<script>

	$(document).ready(function() {
	
		var date = new Date();
		var d = date.getDate();
		var m = date.getMonth();
		var y = date.getFullYear();
		
		$('#calendar').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: ''
			},
			editable: false,
			selectable: false,
			weekends: false,
			height: 200,
			timeFormat: 'h:mmt',
			slotMinutes: 15,
			eventClick: function(calEvent, jsEvent, view) {
				//alert('eventClick:' + (calEvent.start.getTime() / 1000));
				//if (confirm('Sure ?')) {alert('');}
				document.location.href = 'schedule3.jsp?start=' + (calEvent.start.getTime() / 1000);
			},
			events: [
<%
Iterator itr = next_available_appointments.iterator();
while (itr.hasNext()) {
	AppointmentBean available_appt = (AppointmentBean)itr.next();
	available_appt.setClient((UKOnlinePersonBean)loginBean.getPerson());
	//Date appt_date = available_appt.getAppointmentDate();
	//Calendar appt_cal = Calendar.getInstance();
	
%>
				{
					title: '<%= available_appt.getType().getLabel() %>',
					start: <%= available_appt.getAppointmentStartUnixTimestamp() %>,
					end: <%= available_appt.getAppointmentEndUnixTimestamp() %>,
					backgroundColor: '#<%= available_appt.getType().getBGColorCodeString() %>',
					borderColor: '#<%= available_appt.getType().getBGColorCodeString() %>',
					allDay: false
				}<%= itr.hasNext() ? "," : "" %>
<%
}
%>
			]
		});
		
	});

</script>

  </head>
  <body>

<%@ include file="channels\channel-navbar.jsp" %>


    <div class="container">
			
      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">

        <h2>Schedule an Appointment</h2>

		<p>Click an appointment slot to make a request:</p>
	  
	  <div id='calendar'></div>
		
		<struts-html:form styleId="loginform" styleClass="form-signin" action="/client/scheduleFilter" focus="password">
				
	  <div id='calendar'></div>
		
		<a style="margin-left: 10px;" class="btn btn-large" href="schedule1.jsp">Go back</a>
		</struts-html:form>
<%

	String errorMessage = (String)session.getAttribute("forgot-password-message");
	if (errorMessage != null) {
%>
			<div class="alert <%= errorMessage.indexOf("Error") > -1 || errorMessage.indexOf("Failed") > -1 ? "alert-error" : "alert-success" %>">
				<button type="button" class="close" data-dismiss="alert">×</button>
				<h3><%= errorMessage.indexOf("Error") > -1 || errorMessage.indexOf("Failed") > -1 ? "Oh Snap!!" : "Success!!" %></h3>
				<%= errorMessage %>
			  
			</div>
<%
		session.removeAttribute("forgot-password-message");
	}
%>
      </div>

      <hr>

<%@ include file="channels\channel-footer.jsp" %>

    </div> <!-- /container -->


  </body>
</html>
