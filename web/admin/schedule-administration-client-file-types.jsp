<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminFileType" class="com.badiyan.uk.online.beans.GroupUnderCareMemberTypeBean" scope="session" />

<%
boolean new_file_type = false;
if (request.getParameter("id") != null)
{
    try
    {
        String file_type_id_string = request.getParameter("id");
	adminFileType = GroupUnderCareMemberTypeBean.getMemberType(Integer.parseInt(file_type_id_string));
	session.setAttribute("adminFileType", adminFileType);
    }
    catch (Exception x)
    {
	x.printStackTrace();
	new_file_type = true;
    }
}
else
    new_file_type = true;

if (new_file_type)
{
    if (!adminFileType.isNew())
    {
	adminFileType = new GroupUnderCareMemberTypeBean();
	session.setAttribute("adminFileType", adminFileType);
    }
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

						<p class="headlineA">Client File Type</p>
						<p>Use this screen to manage Client File Types</p>
						
						<struts-html:form action="/admin/newClientFileType" focus="descriptionInput" >

							<input id="dummy" name="dummy" type="hidden" />
							
							<div class="adminItem">
								<div class="leftTM">NAME</div>
								<div class="right">
									<input name="descriptionInput" onfocus="select();" value="<%= adminFileType.getLabel() %>" size="31" maxlength="50" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>

							<p><input class="formbutton" type="submit" value="Add&nbsp;&#47;&nbsp;Update" alt="Add&nbsp;&#47;&nbsp;Update" /><input type="hidden" name="delete_id" value="0" /></p>

						</struts-html:form>

						<div class="content_AdministrationTable">

							<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">

									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 472px; text-align: left;  ">File Types</td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
Iterator itr = GroupUnderCareMemberTypeBean.getMemberTypes(adminCompany).iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
        GroupUnderCareMemberTypeBean file_type = (GroupUnderCareMemberTypeBean)itr.next();
%>
							<!-- BEGIN Organization -->

							<div class="organization">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="schedule-administration-client-file-types.jsp?id=<%= file_type.getValue() %>" title=""><%= file_type.getLabel() %></a></td>
										<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= file_type.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
									</tr>
								</table>
							</div>

							<!-- END Organization -->
<%
    }
}
else
{
%>
							<div class="organization">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 472px; text-align: left;  ">No Client File Types Found</td>
									</tr>
								</table>
							</div>
<%
}
%>
							<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

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
