<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />

	<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />

	<script type="text/javascript" src="../scripts/crir.js"></script>
		
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

					<p class="headlineA">Patient Aging Report</p>
						<p>Use this screen to customize a report. Complete all the fields (multiple selection available)
and then click <strong>View Report.</strong> When the report appears, click any column title to sort by
that column. You will be able to download or print the report. To print it, use the <strong>Print</strong>
button on your browser.</p>
					
						<struts-html:form action="/admin/patientListReportFilter">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">STATUS</div>
								<div class="right">
									<select name="status" class="select" style="width: 309px;">
										<option value="0">-- ANY STATUS --</option>
										<option value="1">Active</option>
										<option value="2">Inactive</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">SORT BY</div>
								<div class="right">
									<select name="sort" class="select" style="width: 309px;">
										<option value="1">Last Name</option>
										<option value="3">First Name</option>
										<option value="5">Status</option>
										<option value="7">Address</option>
										<option value="9">Patient Number</option>
										<option value="11">Phone</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
<%
Calendar now = Calendar.getInstance();
Calendar a_year_ago = Calendar.getInstance();
a_year_ago.add(Calendar.YEAR, -1);
%>
							<div class="adminItem">
								<div class="leftTM">START&nbsp;&#47;&nbsp;END DATE</div>
								<div class="right">
									<input name="start_date" onfocus="select();" value="<%= CUBean.getUserDateString(a_year_ago.getTime()) %>" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
									<img src="../images/gfxDash.gif" width="21" height="21" title="-" alt="-" style="_margin-top: 1px;" />
									<input name="end_date" onfocus="select();" value="<%= CUBean.getUserDateString(now.getTime()) %>" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="left">REPORT FORMAT</div>
								<div class="right">
									<label for="formatHTML">HTML</label>
									<input name="displayHTML" id="formatHTML" type="checkbox" class="crirHiddenJS" checked="checked"/>

									<label for="formatExcel">Excel</label>
									<input name="displayExcel" id="formatExcel" type="checkbox" class="crirHiddenJS" />
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>

							<p>
								<input class="formbutton" type="submit" value="View Report" alt="View Report" />
							</p>

						</struts-html:form>
				</div>

				<div class="end"></div>
			</div>

			<div class="content_TextBlock">
				<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			</div>

</div>

</body>

</html>