<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<jsp:useBean id="adminAppointmentType" class="com.badiyan.uk.online.beans.AppointmentTypeBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
   
/*
if (request.getParameter("id") != null)
{
    try
    {
        AppointmentTypeBean appointment_type_obj = AppointmentTypeBean.getAppointmentType(Integer.parseInt(request.getParameter("id")));
        CompanyBean company_obj = appointment_type_obj.getCompany();

        // ensure that this person is an admin for this company
        
        if (company_obj.isAdministrator(loginBean.getPerson()))
        {
            adminCompany = company_obj;
            session.setAttribute("adminCompany", adminCompany);
            adminAppointmentType = appointment_type_obj;
            session.setAttribute("adminAppointmentType", adminAppointmentType);
        }
    }
    catch (Exception x)
    {
    }
}
 */

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

						<p class="headlineA">Allowed Appointment Types</p>
						<p class="currentObjectName"><%= adminAppointmentType.getLabel() %></p>
						<p>Use this screen to specify the appointment types that are allowed to be scheduled during this off-hours appointment type. You may select multiple roles by holding
the Ctrl or Shift keys while clicking.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						

						<struts-html:form action="/admin/allowedAppointmentTypes">

							<input id="dummy" name="dummy" type="hidden" />

							<p>
								<select name="appointmentTypeSelect" class="multipleSelect" size="12" multiple="multiple" style="width: 492px;">
<%
Iterator itr = AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
while (itr.hasNext())
{
    AppointmentTypeBean appt_type_obj = (AppointmentTypeBean)itr.next();
%>
									<option value="<%= appt_type_obj.getValue() %>"<%= adminAppointmentType.isAllowedAppointmentType(appt_type_obj) ? " selected=\"selected\"" : "" %>><%= appt_type_obj.getLabel() %></option>
<%
}
%>
								</select>
							</p>

							<p style="margin-top: 20px; ">
								<input class="formbutton" type="submit" value="Assign Allowed Appointment Types" alt="Assign Allowed Appointment Types" />
							</p>

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