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
else
{
    adminAppointmentType = new AppointmentTypeBean();
    session.setAttribute("adminAppointmentType", adminAppointmentType);
}

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

						<p class="headlineA">Appointment Types</p>
						<p>Use this screen to add a new Pracice Area or update an existing one.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/appointmentTypes" focus="nameInput">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">TYPE NAME</div>
								<div class="right">
									<input name="nameInput" onfocus="select();" value="<%= adminAppointmentType.getLabel() %>" size="31" maxlength="50" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DEFAULT DURATION</div>
								<div class="right">
									<input name="duration" onfocus="select();" value="<%= adminAppointmentType.getDurationString() %>" size="31" maxlength="2" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">IMAGE URL</div>
								<div class="right">
									<input name="image_url" onfocus="select();" value="<%= adminAppointmentType.getImageUrlString() %>" size="31" maxlength="255" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">BACKGROUND COLOR</div>
								<div class="right">
									<input name="bg_color" onfocus="select();" value="<%= adminAppointmentType.getBGColorCodeString() %>" size="31" maxlength="6" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">TEXT COLOR</div>
								<div class="right">
									<input name="text_color" onfocus="select();" value="<%= adminAppointmentType.getTextColorCodeString() %>" size="31" maxlength="6" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">TYPE</div>
								<div class="right">
									<select name="appointment_type_type" class="select" style="width: 309px;">
										<option value="0">-- SELECT A TYPE --</option>
										<option value="<%= AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE %>"<%= (adminAppointmentType.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE) ? " selected" : "" %>>Client Appointment</option>
										<option value="<%= AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE %>"<%= (adminAppointmentType.getType() == AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE) ? " selected" : "" %>>Practitioner Off-Time</option>
										<option value="<%= AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE %>"<%= (adminAppointmentType.getType() == AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE) ? " selected" : "" %>>Meeting</option>
<%
   /*
    public static final short CLIENT_APPOINTMENT_TYPE_TYPE = 1;
    public static final short OFFTIME_APPOINTMENT_TYPE_TYPE = 2;
    public static final short MEETING_APPOINTMENT_TYPE_TYPE = 3;
 */
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">PRACTICE AREA</div>
								<div class="right">
									<select name="practice_area" class="select" style="width: 309px;">
										<option value="0">-- SELECT A PRACTICE AREA --</option>
<%

try
{
    Iterator practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany).iterator();
    while (practice_areas.hasNext())
    {
        PracticeAreaBean obj = (PracticeAreaBean)practice_areas.next();
%>
										<option value="<%= obj.getValue() %>"<%= (obj.getId() == adminAppointmentType.getPracticeAreaId()) ? " selected" : "" %>><%= obj.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">&nbsp;</div>
								<div class="right">
									<input class="formbutton" type="submit" value="Add&nbsp;&#47;&nbsp;Update" alt="Add&nbsp;&#47;&nbsp;Update" style="margin-right: 4px;"/><input type="hidden" name="delete_id" value="0" />
								</div>
								<div class="end"></div>
							</div>

						</struts-html:form>

						<div class="content_AdministrationTable">

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  ">Appointment Type</td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator appointment_types = AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
    if (appointment_types.hasNext())
    {
	while (appointment_types.hasNext())
	{
	    AppointmentTypeBean obj = (AppointmentTypeBean)appointment_types.next();
%>
							<!-- BEGIN Job Title -->
							<div class="jobTitle">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="admin-appointment-types.jsp?id=<%= obj.getValue() %>" title=""><%= obj.getLabel() %></a></td>
										<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= obj.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
									</tr>
								</table>
							</div>
							<!-- END Job Title -->
<%
	}
    }
    else
    {
%>
							<!-- BEGIN Job Title -->
							<div class="jobTitle">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colspan="3">No Appointment Types Found</td>
									</tr>
								</table>
							</div>
							<!-- END Job Title -->
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						</div>

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