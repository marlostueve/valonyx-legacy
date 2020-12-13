<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminAppointment" class="com.badiyan.uk.online.beans.AppointmentBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
adminAppointment = new AppointmentBean();
session.setAttribute("adminAppointment", adminAppointment);

//listHelper.setCompany(adminAppointment);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>

		<title>Universal Knowledge</title>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="generator" content="TextPad 4.6.2" />
		<meta name="author" content="" />
		<meta name="copyright" content="&copy; 2007" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />

		<link rel="stylesheet" type="text/css" href="../css/web.css" />

		<script type="text/javascript" src="../scripts/crir.js"></script>
		<!--[if lte IE 7]><script type="text/javascript" src="scripts/optionDisabledSupport.js"></script><![endif]-->
		
<%@ include file="..\channels\channel-head-javascript.jsp" %>

	</head>

	<body onload="javascript:initErrors();">

		<div id="main">

<%@ include file="..\channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Administration">

<%@ include file="channels\channel-admin-menu.jsp" %>

					<div class="main">

						<p class="headlineA">Add New Appointment</p>
						<p>Use this screen to add a new appointment.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/newAppointment" focus="appointmentDateInput" >

							<input id="dummy" name="dummy" type="hidden" />
							
							<div class="adminItem">
								<div class="leftTM">CLIENT</div>
								<div class="right">
                                                                    <select name="clientSelect" style="width: 304px;">
									<option value="0">-- SELECT A CLIENT --</option>
<%
Iterator itr = UKOnlinePersonBean.getPersons(adminCompany, UKOnlineRoleBean.CLIENT_ROLE_NAME).iterator();
while (itr.hasNext())
{
    PersonBean person_obj = (PersonBean)itr.next();
%>
									<option value="<%= person_obj.getValue() %>"><%= person_obj.getLabel() %></option>
<%
}
%>
                                                                    </select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">PRACTITIONER</div>
								<div class="right">
                                                                    <select name="practitionerSelect" style="width: 304px;">
									<option value="0">-- SELECT A PRACTITIONER --</option>
<%
itr = UKOnlinePersonBean.getPersons(adminCompany, UKOnlineRoleBean.PRACTITIONER_ROLE_NAME).iterator();
while (itr.hasNext())
{
    PersonBean person_obj = (PersonBean)itr.next();
%>
									<option value="<%= person_obj.getValue() %>"><%= person_obj.getLabel() %></option>
<%
}
%>
                                                                    </select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">APPOINTMENT TYPE</div>
								<div class="right">
                                                                    <select name="typeSelect" style="width: 304px;">
									<option value="0">-- SELECT A TYPE --</option>
<%
itr = AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
while (itr.hasNext())
{
    AppointmentTypeBean obj = (AppointmentTypeBean)itr.next();
%>
									<option value="<%= obj.getValue() %>"><%= obj.getLabel() %></option>
<%
}
%>
                                                                    </select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">PARENT APPOINTMENT</div>
								<div class="right">
                                                                    <select name="parentSelect" style="width: 304px;">
									<option value="0">-- PARENT IF FOLLOWUP --</option>
                                                                    </select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">APPOINTMENT DATE</div>
								<div class="right">
									<input name="appointmentDateInput" onfocus="getDate('appointmentForm','appointmentDateInput');select();" value="" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">APPOINTMENT TIME</div>
								<div class="right">
                                                                    <select name="appointmentHourInput" style="width: 54px;">
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
									<option value="6">6</option>
									<option value="7">7</option>
									<option value="8">8</option>
									<option value="9">9</option>
									<option value="10">10</option>
									<option value="11">11</option>
									<option value="0">12</option>
                                                                    </select>
                                                                    <select name="appointmentMinuteInput" style="width: 54px;">
									<option value="0">00</option>
									<option value="5">05</option>
									<option value="10">10</option>
									<option value="15">15</option>
									<option value="20">20</option>
									<option value="25">25</option>
									<option value="30">30</option>
									<option value="35">35</option>
									<option value="40">40</option>
									<option value="45">45</option>
									<option value="50">50</option>
									<option value="55">55</option>
                                                                    </select>
                                                                    <select name="appointmentAMPMInput" style="width: 54px;">
									<option value="<%= Calendar.AM %>">AM</option>
									<option value="<%= Calendar.PM %>">PM</option>
                                                                    </select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DURATION</div>
								<div class="right">
									<input name="duration" onfocus="select();" value="" size="31" maxlength="2" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>

							<p><input class="formbutton" type="submit" value="Add New Appointment" alt="Add New Appointment" /><input type="hidden" name="delete_id" value="0" /></p>

						</struts-html:form>

					</div>

					<div class="end"></div>
				</div>

				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="..\channels\channel-footer.jsp" %>

		</div>

	</body>

</html>