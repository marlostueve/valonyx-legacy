<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%
SimpleDateFormat time_date_format = new SimpleDateFormat("hh:mm a");
SimpleDateFormat short_date_format = new SimpleDateFormat("M/d");

AppointmentBean appt = AppointmentBean.getAppointment(Integer.parseInt(request.getParameter("id")));
Date appt_date = appt.getAppointmentDate();


String status_string = "";
if (appt.isCancelled())
	status_string = "[CANCELLED] ";
else if (appt.isRescheduled())
	status_string = "[RESCHEDULED] ";

String comments = appt.getComments();
if (!comments.equals("") || !status_string.equals(""))
	comments = status_string + comments;

%>
<html><head>
</head>
<body><ul id="OT" title="<%= appt.getLabel() %>">

	  			<li>
					<%= time_date_format.format(appt_date) %> - <%= appt.getLabel() %><br />
					<%= appt.getType().getLabel() %><br />
<%
if (appt.hasClient())
{
%>
					<%= appt.getClient().getPhoneNumbersString("<br />") %><br />
<%
}
%>
					<%= comments %>
				</li>

    </ul>