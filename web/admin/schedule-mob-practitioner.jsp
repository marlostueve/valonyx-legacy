<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%
SimpleDateFormat time_date_format = new SimpleDateFormat("hh:mm a");
SimpleDateFormat short_date_format = new SimpleDateFormat("M/d");

Calendar c = Calendar.getInstance();
boolean is_tomorrow = false;
if (request.getParameter("tomorrow") != null)
{
	c.add(Calendar.DATE, 1);
	is_tomorrow = true;
}

UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(request.getParameter("id")));
Vector appointments = AppointmentBean.getAppointmentsForPractitioner(practitioner, c.getTime());
Iterator appt_itr = appointments.iterator();
%>
<html><head>
</head>
<body><ul id="OT" title="<%= short_date_format.format(c.getTime()) %> - <%= practitioner.getLabel() %>">
<%
if (appt_itr.hasNext())
{
	while (appt_itr.hasNext())
	{
		AppointmentBean appt = (AppointmentBean)appt_itr.next();
		Date appt_date = appt.getAppointmentDate();
%>
	  			<li><a href="schedule-mob-appointment.jsp?id=<%= appt.getId() %>"><%= time_date_format.format(appt_date) %> - <%= appt.getLabel() %></a></li>
<%
	}
}
else
{
%>
	  			<li>No Appointments</li>
<%
}

if (!is_tomorrow)
{
%>
	  			<li><a href="schedule-mob-practitioner.jsp?id=<%= practitioner.getId() %>&tomorrow=true" class="no_bold">Tomorrow</a></li>
<%
}
%>
    </ul>