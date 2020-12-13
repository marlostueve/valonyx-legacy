<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="adminPracticeArea" class="com.badiyan.uk.online.beans.PracticeAreaBean" scope="session" />

<%
if (request.getParameter("id") != null)
{
    adminPracticeArea = PracticeAreaBean.getPracticeArea(Integer.parseInt(request.getParameter("id")));
    session.setAttribute("adminPracticeArea", adminPracticeArea);
}
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />

	<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" />
	<link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" />
	<script type="text/javascript" src="../yui/build/utilities/utilities.js"></script>
	<script type="text/javascript" src="../yui/build/button/button-beta.js"></script>
	<script type="text/javascript" src="../yui/build/container/container.js"></script>

	<script type="text/javascript" src="../../build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="../../build/element/element-beta.js"></script>

	<script type="text/javascript">
    
    var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var httpRequest;
    var request_NextDay;
    
    var edit = 0;

<%
Iterator type_itr = AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
while (type_itr.hasNext())
{
    AppointmentTypeBean appointmentType = (AppointmentTypeBean)type_itr.next();
%>
    typeArray["<%= appointmentType.getId() %>"] = {"bg" : "<%= appointmentType.getBGColorCodeString() %>", "txt" : "<%= appointmentType.getTextColorCodeString() %>", "duration" : "<%= appointmentType.getDurationString() %>" };
<%
}
%>

    function processCommand(command, parameter)
    {
		if (window.ActiveXObject)
			httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
		else if (window.XMLHttpRequest)
			httpRequest = new XMLHttpRequest();

		httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain") %>/ClientServlet.html', true);
		httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
		httpRequest.onreadystatechange = function() {processCommandEvent(); } ;
		eval("httpRequest.send('command=" + command + "&parameter=" + parameter + "')");

		return true;
    }

    function processCommandEvent()
    {
		if (httpRequest.readyState == 4)
		{
			if (httpRequest.status == 200)
			{
				if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];

					document.getElementById('today_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
					document.getElementById('new_appt_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
					//document.getElementById('newAppointmentDateInput').value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
					document.forms[0].newAppointmentDateInput.value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;

					buildScheduleArray(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
				{
					//alert(httpRequest.responseXML.getElementsByTagName("clients").length);
					var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
					showPeople(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
				{
					//alert(httpRequest.responseXML.getElementsByTagName("clients").length);
					var xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
					//showPeople(xml_response);
					
					showPerson(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("error").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("error")[0];
					alert("Error : " + xml_response.childNodes[0].nodeValue);
				}
			}
			else
			{
				alert("Error : " + httpRequest.status + " : " + httpRequest.statusText);
			}
		}
    }

	</script>
	<title>Valonyx</title>
</head>

<body class="yui-skin-sam" >

<!-- <h1>Valeo Schedule</h1> -->

<%@ include file="channels/channel-schedule-menu.jsp" %>

<div id="content">
	

				<div class="content_TextBlock">
					<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Administration">

<%@ include file="channels\channel-admin-menu.jsp" %>

					<div class="main">

						<p class="headlineA">Practice Areas</p>
						<p>Use this screen to add a new Pracice Area or update an existing one.  CTRL-Left Click to select multiple practitioners for this practice area.</p>
						
						<struts-html:form action="/admin/practiceAreas" focus="nameInput">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">PRACTICE AREA</div>
								<div class="right">
									<input name="nameInput" onfocus="select();" value="<%= adminPracticeArea.getLabel() %>" size="31" maxlength="100" style="width: 206px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">PARENT</div>
								<div class="right">
									<select name="parent" class="select" style="width: 309px;">
										<option value="0">NO PARENT (ROOT PRACTICE AREA)</option>
<%

try
{
    Iterator practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany).iterator();
    while (practice_areas.hasNext())
    {
        PracticeAreaBean obj = (PracticeAreaBean)practice_areas.next();
%>
										<option value="<%= obj.getValue() %>"<%= (obj.getId() == adminPracticeArea.getParentId()) ? " selected" : "" %>><%= obj.getLabel() %></option>
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
								<div class="leftTM">PRACTITIONERS</div>
								<div class="right">
<%
try
{
    Iterator itr = UKOnlinePersonBean.getPersons(adminCompany, UKOnlineRoleBean.PRACTITIONER_ROLE_NAME).iterator();
    if (itr.hasNext())
    {
%>
									<select name="practitionerSelect" class="multipleSelect" size="10" multiple="multiple" style="width: 309px;">
										<!-- <option value="0">-- ALL PRACTITIONERS --</option> -->
<%
	while (itr.hasNext())
	{
	    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
%>
				    <option value="<%= practitioner.getValue() %>"<%= adminPracticeArea.contains(practitioner) ? " SELECTED" : "" %>><%= practitioner.getLabel() %></option>
<%
	}
%>
									</select>
<%
    }
    else
    {
%>
									<strong>No Practitioners Found</strong>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
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
										<td style="width: 402px; text-align: left;  ">Practice Area</td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany).iterator();
    if (practice_areas.hasNext())
    {
	while (practice_areas.hasNext())
	{
	    PracticeAreaBean obj = (PracticeAreaBean)practice_areas.next();
%>
							<!-- BEGIN Job Title -->
							<div class="jobTitle">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="schedule-administration-practice-areas.jsp?id=<%= obj.getValue() %>" title=""><%= obj.getLabel() %></a></td>
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
										<td style="width: 492px; text-align: left;  " colspan="3">No Practice Areas Found</td>
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

</body>

</html>
