<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPractitionerSchedule" class="com.badiyan.uk.online.beans.PractitionerScheduleBean" scope="session" />
<jsp:useBean id="adminPractitionerScheduleItem" class="com.badiyan.uk.online.beans.PractitionerScheduleItemBean" scope="session" />

<%
int selected_practitioner_id = 0;
//boolean new_practitioner_schedule = false;
if (request.getParameter("id") != null)
{
    try
    {
        String practitioner_schedule_id_string = request.getParameter("id");
	adminPractitionerSchedule = PractitionerScheduleBean.getPractitionerSchedule(Integer.parseInt(practitioner_schedule_id_string));
	session.setAttribute("adminPractitionerSchedule", adminPractitionerSchedule);
    }
    catch (Exception x)
    {
	x.printStackTrace();
	//new_practitioner_schedule = true;
    }
}

if (!adminPractitionerSchedule.isNew())
    selected_practitioner_id = adminPractitionerSchedule.getPractitioner().getId();

/*
else
    new_practitioner_schedule = true;

if (new_practitioner_schedule)
{
    if (!adminPractitionerSchedule.isNew())
    {
	adminPractitionerSchedule = new PractitionerScheduleBean();
	session.setAttribute("adminPractitionerSchedule", adminPractitionerSchedule);
    }
}
 */

if (request.getParameter("item_id") != null)
{
    try
    {
        String practitioner_schedule_item_id_string = request.getParameter("item_id");
	adminPractitionerScheduleItem = PractitionerScheduleItemBean.getPractitionerScheduleItem(Integer.parseInt(practitioner_schedule_item_id_string));
	session.setAttribute("adminPractitionerScheduleItem", adminPractitionerScheduleItem);
    }
    catch (Exception x)
    {
	x.printStackTrace();
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
    var httpRequest;
    var request_NextDay;
    
    var edit = 0;

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
	
	<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>

</head>

<body class="yui-skin-sam" onload="initErrors();" >

<!-- <h1>Valeo Schedule</h1> -->

<%@ include file="channels/channel-schedule-menu.jsp" %>

<div id="content">
	

				<div class="content_TextBlock">
					<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>
				
				<!--
				<table name="PRACTITIONER_SCHEDULE" idMethod="native">
    <column name="PRACTITIONER_SCHEDULE_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PRACTITIONER_ID" required="true" type="INTEGER"/>
	
	<column name="NAME" required="true" type="VARCHAR" size="50"/>
	<column name="IS_BASE_SCHEDULE" required="true" type="SMALLINT" />
    <column name="START_DATE" required="false" type="DATE"/>
	<column name="END_DATE" required="false" type="DATE"/>
    
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
    
    <foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
				-->

				<div class="content_Administration">

<%@ include file="channels\channel-admin-menu.jsp" %>

					<div class="main">

						<p class="headlineA">Practitioner Schedules</p>
						<p>Use this screen to manage Practitioner Schedules</p>
						
						<struts-html:form action="/admin/practitionerSchedule" focus="descriptionInput" >

							<input id="dummy" name="dummy" type="hidden" />
							
							<div class="adminItem">
								<div class="leftTM">PRACTITIONER</div>
								<div class="right">
<%
try
{
    Iterator practitioners = UKOnlinePersonBean.getPersons(adminCompany, UKOnlineRoleBean.PRACTITIONER_ROLE_NAME).iterator();
    if (practitioners.hasNext())
    {
%>
									<select name="practitionerSelect" class="select" style="width: 309px;">
										<option value="0">-- SELECT A PRACTITIONER --</option>
<%
	while (practitioners.hasNext())
	{
	    UKOnlinePersonBean practitioner_obj = (UKOnlinePersonBean)practitioners.next();
%>
										<option value="<%= practitioner_obj.getValue() %>"<%= (practitioner_obj.getId() == selected_practitioner_id) ? " SELECTED" : "" %>><%= practitioner_obj.getLabel() %></option>
<%
	}
%>
									</select>
<%
    }
    else
    {
%>
									No Practice Areas Found
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
								<div class="leftTM">PRIMARY SCHEDULE</div>
								<div class="right">
									<input name="primarySchedule" type="checkbox" <%= adminPractitionerSchedule.isBaseSchedule() ? "checked" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">NAME *</div>
								<div class="right">
									<input name="descriptionInput" onfocus="select();"<%= adminPractitionerSchedule.isBaseSchedule() ? " " : "" %> value="<%= adminPractitionerSchedule.getNameString() %>" size="31" maxlength="50" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">START DATE *</div>
								<div class="right">
									<input name="startDate" onfocus="getDate('practitionerScheduleForm','startDate');select();" value="<%= adminPractitionerSchedule.getStartDateString() %>" size="31" maxlength="10" style="width: 70px;" />
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">END DATE *</div>
								<div class="right">
									<input name="endDate" onfocus="getDate('practitionerScheduleForm','endDate');select();" value="<%= adminPractitionerSchedule.getEndDateString() %>" size="31" maxlength="10" style="width: 70px;" />
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">* Not Required for Primary Schedule</div>
								<div class="end"></div>
							</div>

							<p><input class="formbutton" type="submit" name="submitButton" value="Add&nbsp;&#47;&nbsp;Update" alt="Add&nbsp;&#47;&nbsp;Update" /><input type="hidden" name="delete_id" value="0" /></p>
							
							<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
							
							<!--
							<table name="PRACTITIONER_SCHEDULE_ITEM" idMethod="native">
    <column name="PRACTITIONER_SCHEDULE_ITEM_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PRACTITIONER_SCHEDULE_ID" required="true" type="INTEGER"/>
	
    <column name="DAY_OF_WEEK" required="true" type="INTEGER" />
	<column name="START_HOUR_OF_DAY" required="true" type="INTEGER" />
	<column name="START_MINUTE" required="true" type="INTEGER" />
	<column name="END_HOUR_OF_DAY" required="true" type="INTEGER" />
	<column name="END_MINUTE" required="true" type="INTEGER" />
    
    <foreign-key foreignTable="PRACTITIONER_SCHEDULE">
		<reference local="PRACTITIONER_SCHEDULE_ID" foreign="PRACTITIONER_SCHEDULE_ID"/>
    </foreign-key>
</table>
							-->
							
							
							<div class="adminItem">
								<div class="leftTM">DAY OF WEEK</div>
								<div class="right">
									<select name="dayOfWeekSelect" style="width: 309px;">
										<option value="-1">-- SELECT A DAY OF WEEK --</option>
										<option value="<%= Calendar.SUNDAY %>"<%= (adminPractitionerScheduleItem.getDayOfWeek() == Calendar.SUNDAY) ? " SELECTED" : "" %>>Sunday</option>
										<option value="<%= Calendar.MONDAY %>"<%= (adminPractitionerScheduleItem.getDayOfWeek() == Calendar.MONDAY) ? " SELECTED" : "" %>>Monday</option>
										<option value="<%= Calendar.TUESDAY %>"<%= (adminPractitionerScheduleItem.getDayOfWeek() == Calendar.TUESDAY) ? " SELECTED" : "" %>>Tuesday</option>
										<option value="<%= Calendar.WEDNESDAY %>"<%= (adminPractitionerScheduleItem.getDayOfWeek() == Calendar.WEDNESDAY) ? " SELECTED" : "" %>>Wednesday</option>
										<option value="<%= Calendar.THURSDAY %>"<%= (adminPractitionerScheduleItem.getDayOfWeek() == Calendar.THURSDAY) ? " SELECTED" : "" %>>Thursday</option>
										<option value="<%= Calendar.FRIDAY %>"<%= (adminPractitionerScheduleItem.getDayOfWeek() == Calendar.FRIDAY) ? " SELECTED" : "" %>>Friday</option>
										<option value="<%= Calendar.SATURDAY %>"<%= (adminPractitionerScheduleItem.getDayOfWeek() == Calendar.SATURDAY) ? " SELECTED" : "" %>>Saturday</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">START HOUR:MINUTE</div>
								<div class="right">
									<select name="startHour">
										<option value="1"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 1 || adminPractitionerScheduleItem.getStartHourOfDay() == 13) ? " SELECTED" : "" %>>1</option>
										<option value="2"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 2 || adminPractitionerScheduleItem.getStartHourOfDay() == 14) ? " SELECTED" : "" %>>2</option>
										<option value="3"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 3 || adminPractitionerScheduleItem.getStartHourOfDay() == 15) ? " SELECTED" : "" %>>3</option>
										<option value="4"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 4 || adminPractitionerScheduleItem.getStartHourOfDay() == 16) ? " SELECTED" : "" %>>4</option>
										<option value="5"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 5 || adminPractitionerScheduleItem.getStartHourOfDay() == 17) ? " SELECTED" : "" %>>5</option>
										<option value="6"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 6 || adminPractitionerScheduleItem.getStartHourOfDay() == 18) ? " SELECTED" : "" %>>6</option>
										<option value="7"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 7 || adminPractitionerScheduleItem.getStartHourOfDay() == 19) ? " SELECTED" : "" %>>7</option>
										<option value="8"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 8 || adminPractitionerScheduleItem.getStartHourOfDay() == 20) ? " SELECTED" : "" %>>8</option>
										<option value="9"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 9 || adminPractitionerScheduleItem.getStartHourOfDay() == 21) ? " SELECTED" : "" %>>9</option>
										<option value="10"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 10 || adminPractitionerScheduleItem.getStartHourOfDay() == 22) ? " SELECTED" : "" %>>10</option>
										<option value="11"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 11 || adminPractitionerScheduleItem.getStartHourOfDay() == 23) ? " SELECTED" : "" %>>11</option>
										<option value="0"<%= (adminPractitionerScheduleItem.getStartHourOfDay() == 0 || adminPractitionerScheduleItem.getStartHourOfDay() == 12) ? " SELECTED" : "" %>>12</option>
									</select>
									&nbsp;:&nbsp;
									<select name="startMinute">
										<option value="0"<%= (adminPractitionerScheduleItem.getStartMinute() == 0) ? " SELECTED" : "" %>>00</option>
										<option value="5"<%= (adminPractitionerScheduleItem.getStartMinute() == 5) ? " SELECTED" : "" %>>05</option>
										<option value="10"<%= (adminPractitionerScheduleItem.getStartMinute() == 10) ? " SELECTED" : "" %>>10</option>
										<option value="15"<%= (adminPractitionerScheduleItem.getStartMinute() == 15) ? " SELECTED" : "" %>>15</option>
										<option value="20"<%= (adminPractitionerScheduleItem.getStartMinute() == 20) ? " SELECTED" : "" %>>20</option>
										<option value="25"<%= (adminPractitionerScheduleItem.getStartMinute() == 25) ? " SELECTED" : "" %>>25</option>
										<option value="30"<%= (adminPractitionerScheduleItem.getStartMinute() == 30) ? " SELECTED" : "" %>>30</option>
										<option value="35"<%= (adminPractitionerScheduleItem.getStartMinute() == 35) ? " SELECTED" : "" %>>35</option>
										<option value="40"<%= (adminPractitionerScheduleItem.getStartMinute() == 40) ? " SELECTED" : "" %>>40</option>
										<option value="45"<%= (adminPractitionerScheduleItem.getStartMinute() == 45) ? " SELECTED" : "" %>>45</option>
										<option value="50"<%= (adminPractitionerScheduleItem.getStartMinute() == 50) ? " SELECTED" : "" %>>50</option>
										<option value="55"<%= (adminPractitionerScheduleItem.getStartMinute() == 55) ? " SELECTED" : "" %>>55</option>
									</select>
									<!--
									<input name="startHour" onfocus="select();" value="" size="31" maxlength="2" style="width: 40px;" />&nbsp;:&nbsp;
									<input name="startMinute" onfocus="select();" value="" size="31" maxlength="2" style="width: 40px;" />
									-->
									<select name="startAMPMSelect">
										<option value="<%= Calendar.AM %>">am</option>
										<option value="<%= Calendar.PM %>"<%= (adminPractitionerScheduleItem.getStartHourOfDay() > 11) ? " SELECTED" : "" %>>pm</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">END HOUR:MINUTE</div>
								<div class="right">
									<select name="endHour">
										<option value="1"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 1 || adminPractitionerScheduleItem.getEndHourOfDay() == 13) ? " SELECTED" : "" %>>1</option>
										<option value="2"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 2 || adminPractitionerScheduleItem.getEndHourOfDay() == 14) ? " SELECTED" : "" %>>2</option>
										<option value="3"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 3 || adminPractitionerScheduleItem.getEndHourOfDay() == 15) ? " SELECTED" : "" %>>3</option>
										<option value="4"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 4 || adminPractitionerScheduleItem.getEndHourOfDay() == 16) ? " SELECTED" : "" %>>4</option>
										<option value="5"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 5 || adminPractitionerScheduleItem.getEndHourOfDay() == 17) ? " SELECTED" : "" %>>5</option>
										<option value="6"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 6 || adminPractitionerScheduleItem.getEndHourOfDay() == 18) ? " SELECTED" : "" %>>6</option>
										<option value="7"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 7 || adminPractitionerScheduleItem.getEndHourOfDay() == 19) ? " SELECTED" : "" %>>7</option>
										<option value="8"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 8 || adminPractitionerScheduleItem.getEndHourOfDay() == 20) ? " SELECTED" : "" %>>8</option>
										<option value="9"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 9 || adminPractitionerScheduleItem.getEndHourOfDay() == 21) ? " SELECTED" : "" %>>9</option>
										<option value="10"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 10 || adminPractitionerScheduleItem.getEndHourOfDay() == 22) ? " SELECTED" : "" %>>10</option>
										<option value="11"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 11 || adminPractitionerScheduleItem.getEndHourOfDay() == 23) ? " SELECTED" : "" %>>11</option>
										<option value="0"<%= (adminPractitionerScheduleItem.getEndHourOfDay() == 0 || adminPractitionerScheduleItem.getEndHourOfDay() == 12) ? " SELECTED" : "" %>>12</option>
									</select>
									&nbsp;:&nbsp;
									<select name="endMinute">
										<option value="0"<%= (adminPractitionerScheduleItem.getEndMinute() == 0) ? " SELECTED" : "" %>>00</option>
										<option value="5"<%= (adminPractitionerScheduleItem.getEndMinute() == 5) ? " SELECTED" : "" %>>05</option>
										<option value="10"<%= (adminPractitionerScheduleItem.getEndMinute() == 10) ? " SELECTED" : "" %>>10</option>
										<option value="15"<%= (adminPractitionerScheduleItem.getEndMinute() == 15) ? " SELECTED" : "" %>>15</option>
										<option value="20"<%= (adminPractitionerScheduleItem.getEndMinute() == 20) ? " SELECTED" : "" %>>20</option>
										<option value="25"<%= (adminPractitionerScheduleItem.getEndMinute() == 25) ? " SELECTED" : "" %>>25</option>
										<option value="30"<%= (adminPractitionerScheduleItem.getEndMinute() == 30) ? " SELECTED" : "" %>>30</option>
										<option value="35"<%= (adminPractitionerScheduleItem.getEndMinute() == 35) ? " SELECTED" : "" %>>35</option>
										<option value="40"<%= (adminPractitionerScheduleItem.getEndMinute() == 40) ? " SELECTED" : "" %>>40</option>
										<option value="45"<%= (adminPractitionerScheduleItem.getEndMinute() == 45) ? " SELECTED" : "" %>>45</option>
										<option value="50"<%= (adminPractitionerScheduleItem.getEndMinute() == 50) ? " SELECTED" : "" %>>50</option>
										<option value="55"<%= (adminPractitionerScheduleItem.getEndMinute() == 55) ? " SELECTED" : "" %>>55</option>
									</select>
									<!--
									<input name="endHour" onfocus="select();" value="" size="31" maxlength="2" style="width: 40px;" />&nbsp;:&nbsp;
									<input name="endMinute" onfocus="select();" value="" size="31" maxlength="2" style="width: 40px;" />
									-->
									<select name="endAMPMSelect">
										<option value="<%= Calendar.AM %>">am</option>
										<option value="<%= Calendar.PM %>"<%= (adminPractitionerScheduleItem.getEndHourOfDay() > 11) ? " SELECTED" : "" %>>pm</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">SCHEDULE DETAILS</div>
								<div class="right">
								    <div class="content_AdministrationTable">

									    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
									    <div class="heading">
										    <table cellspacing="0" cellpadding="0" border="0" summary="">

											    <tr style="display: block;">
												    <td style="width:  20px; text-align: left;  "></td>
												    <td style="width: 472px; text-align: left;  ">Schedule Details</td>
											    </tr>
										    </table>
									    </div>
									    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator schedule_items = adminPractitionerSchedule.getItems().iterator();
    if (schedule_items.hasNext())
    {
	while (schedule_items.hasNext())
	{
	    PractitionerScheduleItemBean item_obj = (PractitionerScheduleItemBean)schedule_items.next();
%>
									    <!-- BEGIN Organization -->

									    <div class="organization">
										    <table cellspacing="0" cellpadding="0" border="0" summary="">
											    <tr>
												    <td style="width:  20px; text-align: left;  "></td>
												    <td style="width: 402px; text-align: left;  "><a href="schedule-administration-practitioner-schedules.jsp?item_id=<%= item_obj.getValue() %>" title=""><%= item_obj.getLabel() %></a></td>
												    <td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_item_id.value=<%= item_obj.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
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
												    <td style="width: 472px; text-align: left;  ">No Schedule Details Found</td>
											    </tr>
										    </table>
									    </div>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}

%>
									    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

								    </div>
						
								</div>
								<div class="end"></div>
							</div>
							
							<p><input class="formbutton" type="submit" name="submitButton" value="Add&nbsp;&#47;&nbsp;Update Details" alt="Add&nbsp;&#47;&nbsp;Update Details" /><input type="hidden" name="delete_item_id" value="0" /></p>
							<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

						</struts-html:form>

						<div class="content_AdministrationTable">

							<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">

									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 472px; text-align: left;  ">Schedules</td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
Iterator itr = PractitionerScheduleBean.getPractitionerSchedules(adminCompany).iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
        PractitionerScheduleBean practitioner_schedule = (PractitionerScheduleBean)itr.next();
%>
							<!-- BEGIN Organization -->

							<div class="organization">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="schedule-administration-practitioner-schedules.jsp?id=<%= practitioner_schedule.getValue() %>" title=""><%= practitioner_schedule.getLabel() %></a></td>
										<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= practitioner_schedule.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
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
										<td style="width: 472px; text-align: left;  ">No Practitioner Schedules Found</td>
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
