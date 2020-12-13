<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminPractitionerSchedule" class="com.badiyan.uk.online.beans.PractitionerScheduleBean" scope="session" />
<jsp:useBean id="adminPractitionerScheduleItem" class="com.badiyan.uk.online.beans.PractitionerScheduleItemBean" scope="session" />

<%

session.setAttribute("setup_07", "true");

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



// loop through the practitioners and make sure that there's a Primary Schedule for each practitioner

Vector practitioners = UKOnlinePersonBean.getPractitioners(adminCompany);
try
{
    Iterator itr = practitioners.iterator();
    while (itr.hasNext())
    {
	UKOnlinePersonBean practitioner_obj = (UKOnlinePersonBean)itr.next();
	if (!PractitionerScheduleBean.hasBaseSchedule(practitioner_obj))
	{
	    PractitionerScheduleBean base_schedule = new PractitionerScheduleBean();
	    base_schedule.setBaseSchedule(true);
	    base_schedule.setCompany(adminCompany);
	    base_schedule.setCreateOrModifyPerson(adminPerson);
	    base_schedule.setName("Primary");
	    base_schedule.setPractitioner(practitioner_obj);
	    base_schedule.save();
	}
    }

}
catch (Exception x)
{
    x.printStackTrace();
}


CompanySettingsBean settings = adminCompany.getSettings();

short start_hour = 7;
short start_minute = 0;
int start_ampm = Calendar.AM;
short end_hour = 8;
short end_minute = 0;
int end_ampm = Calendar.PM;


if (settings.getOpenHour() > (short)0)
{
    start_hour = (settings.getOpenHour() > 12) ? (short)(settings.getOpenHour() - 12) : settings.getOpenHour();
    start_minute = settings.getOpenMinute();
    start_ampm = (settings.getOpenHour() > 12) ? Calendar.PM : Calendar.AM;

    end_hour = (settings.getCloseHour() > 12) ? (short)(settings.getCloseHour() - 12) : settings.getCloseHour();
    end_minute = settings.getCloseMinute();
    end_ampm = (settings.getCloseHour() > 12) ? Calendar.PM : Calendar.AM;
}

if ((adminPractitionerScheduleItem.getStartHourOfDay() > 0) && (adminPractitionerScheduleItem.getEndHourOfDay() > 0))
{
    start_hour = (adminPractitionerScheduleItem.getStartHourOfDay() > 12) ? (short)(adminPractitionerScheduleItem.getStartHourOfDay() - 12) : (short)adminPractitionerScheduleItem.getStartHourOfDay();
    start_minute = (short)adminPractitionerScheduleItem.getStartMinute();
    start_ampm = (adminPractitionerScheduleItem.getStartHourOfDay() > 11) ? Calendar.PM : Calendar.AM;

    end_hour = (adminPractitionerScheduleItem.getEndHourOfDay() > 12) ? (short)(adminPractitionerScheduleItem.getEndHourOfDay() - 12) : (short)adminPractitionerScheduleItem.getEndHourOfDay();
    end_minute = (short)adminPractitionerScheduleItem.getEndMinute();
    end_ampm = (adminPractitionerScheduleItem.getEndHourOfDay() > 11) ? Calendar.PM : Calendar.AM;
}

boolean has_work_hours_defined = false;



%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />

	<style type="text/css">
	/*margin and padding on body element
	  can introduce errors in determining
	  element position and are not recommended;
	  we turn them off as a foundation for YUI
	  CSS treatments. */
	body {
		margin:0;
		padding:0;
	}
	</style>

<%
if (settings.isSetupComplete())
{
%>
	<link rel="stylesheet" type="text/css"href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/resize.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css">
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>
<%
}
else
{
%>
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>
<%
}
%>

	<script type="text/javascript">

    var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var httpRequest;
    var request_NextDay;

    var edit = 0;

    var oCheckButton1;

    function processCommand(command, parameter)
    {
	if (window.ActiveXObject)
	    httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
	else if (window.XMLHttpRequest)
	    httpRequest = new XMLHttpRequest();

	httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain")%>/ClientServlet.html', true);
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

<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup", function () {

            // Create a Button using an existing <input> element as a data source
<%
if (!settings.isSetupComplete())
{
%>
            var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "previous" });
	    var oSubmitButton2 = new YAHOO.widget.Button("submitbutton2", { value: "next" });
	    var oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", { value: "finish" });
<%
}
%>
        });

    } ();

</script>

<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup2", function () {

            // Create a Button using an existing <input> element as a data source

	    var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", { value: "Add Schedule Details" });


        });

    } ();

</script>

<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup3", function () {

            // Create a Button using an existing <input> element as a data source

	    var oSubmitButton5 = new YAHOO.widget.Button("submitbutton5", { value: "Add Schedule" });


        });

    } ();

</script>

<%
if (settings.isSetupComplete())
{
%>
<script type="text/javascript">

(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
                minWidth: 750,
                units: [
		{ position: 'top', height: 21, resize: false, body: 'top1' },
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
        });
        layout.render();

    });

})();
</script>
<%
}
else
{
%>
<%@ include file="..\channels\channel-google-analytics.jsp" %>
<%
}
%>

<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>

<style type="text/css">
  #container { position: relative; padding: 6px; background-color: #eeeeee; width: 420px; height:220px; }
</style>

</head>

<body class=" yui-skin-sam" onload="javascript:initErrors();">

<%
if (settings.isSetupComplete())
{
%>
<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white;">
<%
}
try {
%>

    <div id="content">


	<div class="content_TextBlock">
	    <p class="headline"><%= adminCompany.getLabel() %> <%= settings.isSetupComplete() ? "Administration" : "Setup" %> - <%= settings.isSetupComplete() ? "" : "Step 7: " %>Practitioner Schedules</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">

		<!-- <p class="headlineA"><%= settings.isSetupComplete() ? "" : "Step 7: " %>Practitioner Schedules</p> -->
		<p>Use this screen to manage Practitioner Schedules.</p>

		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
		
		<struts-html:form action="/admin/setup07">

		    <input type="hidden" name="delete_id" />
<%
if (adminPractitionerSchedule.isNew())
{
%>
    		    <div class="adminItem">Practitioners can have a Primary Schedule and other Secondary Schedules.  The Primary Schedule is always in effect for a Practitioner unless there's a Secondary Schedule defined for that Practitioner on a given day.</div>
    		    <div class="adminItem">Primary Schedules have been created for all of your practitioners.  In order to specify the daily hours within each schedule, <strong style="font-weight: bolder;">select a schedule from the list below</strong> to edit it.</div>
<%
}
else
{
%>
		    <div class="adminItem">You have selected to <strong style="font-weight: bolder;">update the <%= adminPractitionerSchedule.getName() %> Schedule for <%= adminPractitionerSchedule.getPractitioner().getLabel() %></strong>.  When you are finished making changes, please click <strong style="font-weight: bolder;">Update Schedule.</strong></div>
<%
}
%>
		    <div class="content_AdministrationTable">

			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
			    <div class="heading">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">

					    <tr style="display: block;">
						    <td style="width:  20px; text-align: left;  "></td>
						    <td style="width: 472px; text-align: left;  ">Practitioner Schedules</td>
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
		UKOnlinePersonBean practObjCC = practitioner_schedule.getPractitioner();
		if (practObjCC.showPractitioner()) {
			
			String room_label = practObjCC.getFirstNameString();
					if (practObjCC.isRoom()) {
						room_label = practObjCC.getLastNameString();
					}
					if (room_label.equals("Christine")) {
						room_label = "Christine - TX1";
					} else if (room_label.equals("TX 1")) {
						//room_label = "Lisa - TX3";
						continue;
					} else if (room_label.equals("Kevin")) {
						room_label = "POD 1";
					} else if (room_label.equals("TX 4")) {
						room_label = "Bemer - TX4";
					} else if (room_label.equals("TX4")) {
						room_label = "Bemer - TX4";
					} else if (room_label.equals("Lisa")) {
						//room_label = "Lisa - TX2";
						continue;
					} else if (room_label.equals("Leah")) {
						room_label = "Leah - TX3";
						continue;
					} else if (room_label.equals("Lesya")) {
						room_label = "Lesya - BW2";
						continue;
					} else if (room_label.equals("Katie")) {
						room_label = "POD - BW1";
						continue;
					} else if (room_label.equals("Counseling(C) - TX2")) {
						continue;
					} else if (room_label.equals("Counseling(M) - TX2")) {
						continue;
					} else if (room_label.equals("Neuro - TX4")) {
						room_label = "Angie NF - TX4";
					} else if (room_label.equals("Sauna 1")) {
						room_label = "POD 2";
					} else if (room_label.equals("Sauna 2")) {
						continue;
					} else if (room_label.equals("Bioscan 1")) {
						continue;
					} else if (room_label.equals("Dr. Biel - TX 5")) {
						continue;
					}
			
	if (practitioner_schedule.hasItems())
	    has_work_hours_defined = true;
%>
			    <!-- BEGIN Organization -->

			    <div class="organization">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">
					    <tr>
						    <td style="width:  20px; text-align: left;  "></td>
						    <td style="width: 402px; text-align: left;  "><a href="setup_07.jsp?id=<%= practitioner_schedule.getValue() %>" title=""><%= room_label %></a><%= practitioner_schedule.hasItems() ? ("&nbsp;&nbsp;[" + practitioner_schedule.getWeekDisplayString() + "]") : "&nbsp;&nbsp;[NO WORK HOURS DEFINED]" %></td>
<%
	if (practitioner_schedule.isBaseSchedule())
	{
%>
						    <td style="width:  70px; text-align: center;  ">&nbsp;</td>
<%
	}
	else
	{
%>
						    <td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= practitioner_schedule.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
<%
	}
%>
					    </tr>
				    </table>
			    </div>

			    <!-- END Organization -->
<%
		}
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

<%
if (adminPractitionerSchedule.isNew())
{
%>
		    <div class="adminItem"><strong style="font-weight: bolder;">Select a Practitioner</strong> to create a new Secondary Schedule for that Practitioner.</div>
<%
}
%>
		    <div class="adminItem">
			    <div class="leftTM">PRACTITIONER/ROOM</div>
			    <div class="right">
<%
try
{
    itr = practitioners.iterator();
    if (itr.hasNext())
    {
%>
				    <select name="practitionerSelect" class="select" style="width: 309px;">
					    <option value="0">-- SELECT A PRACTITIONER/ROOM --</option>
<%
	while (itr.hasNext())
	{
	    UKOnlinePersonBean practitioner_obj = (UKOnlinePersonBean)itr.next();
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
				    No Practitioners Found
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

<%
if (adminPractitionerSchedule.isNew())
{
%>
		    <div class="adminItem">An example of a Secondary Schedule might be if a practitioner has different hours in the summer.  You can create a "Summer" schedule along with start and end dates.  Once these dates pass, the system will automatically revert back to using the Practitioner's Primary Schedule.</div>
		    <div class="adminItem"><strong style="font-weight: bolder;">Provide a Name for this Secondary Schedule.</strong></div>
<%
}
if (adminPractitionerSchedule.isNew() || !adminPractitionerSchedule.isBaseSchedule())
{
%>
		    <div class="adminItem">
			    <div class="leftTM">NAME *</div>
			    <div class="right">
				    <input name="descriptionInput" onfocus="select();"<%= adminPractitionerSchedule.isBaseSchedule() ? " " : "" %> value="<%= adminPractitionerSchedule.getNameString() %>" size="31" maxlength="50" style="width: 206px;" />
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide the Start and End Dates</strong> for this Secondary Schedule.  During this time period, this Schedule will override the Practitioner's Primary Schedule.</div>

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
<%
}
%>
		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup3" class="right">
			    <input id="submitbutton5" class="formbutton" type="submit" name="submit_button" value="<%= adminPractitionerSchedule.isNew() ? "Add" : "Update" %> Schedule" alt="<%= adminPractitionerSchedule.isNew() ? "Add" : "Update" %> Schedule" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>
<%
if (!adminPractitionerSchedule.isNew())
{
%>
		    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
    if (adminPractitionerScheduleItem.isNew())
    {
%>
		    <div class="adminItem"><strong style="font-weight: bolder;">Select the Day of Week and the Start and End Times</strong> for that day.  You can define multiple <strong style="font-weight: bolder;">Schedule Details</strong> for a single day.  For example, a practitioner may work from 7:00 AM to 11:00 AM and from 1:00 PM to 4:00 PM on Mondays.</div>
<%
    }
    else
    {
%>
		    <div class="adminItem">You have selected to <strong style="font-weight: bolder;">update <%= adminPractitionerScheduleItem.getLabel() %></strong>.  When you are finished making changes, please click <strong style="font-weight: bolder;">Update Schedule Details.</strong></div>
<%
    }
%>
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
			    <div class="leftTM">APPOINTMENT TYPE TEMPLATE</div>
			    <div class="right">
				    <select name="appointmentTypeSelect" style="width: 309px;">
					    <option value="-1">-- NONE SELECTED --</option>
<%
	Iterator template_itr = AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
	while (template_itr.hasNext()) {
		AppointmentTypeBean template_obj = (AppointmentTypeBean)template_itr.next();
%>
					    <option value="<%= template_obj.getValue() %>"><%= template_obj.getLabel() %></option>
<%
	}
%>
					</select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			    <div class="leftTM">START HOUR:MINUTE</div>
			    <div class="right">
				    <select name="startHour">
					    <option value="1"<%= (start_hour == 1) ? " SELECTED" : "" %>>1</option>
					    <option value="2"<%= (start_hour == 2) ? " SELECTED" : "" %>>2</option>
					    <option value="3"<%= (start_hour == 3) ? " SELECTED" : "" %>>3</option>
					    <option value="4"<%= (start_hour == 4) ? " SELECTED" : "" %>>4</option>
					    <option value="5"<%= (start_hour == 5) ? " SELECTED" : "" %>>5</option>
					    <option value="6"<%= (start_hour == 6) ? " SELECTED" : "" %>>6</option>
					    <option value="7"<%= (start_hour == 7) ? " SELECTED" : "" %>>7</option>
					    <option value="8"<%= (start_hour == 8) ? " SELECTED" : "" %>>8</option>
					    <option value="9"<%= (start_hour == 9) ? " SELECTED" : "" %>>9</option>
					    <option value="10"<%= (start_hour == 10) ? " SELECTED" : "" %>>10</option>
					    <option value="11"<%= (start_hour == 11) ? " SELECTED" : "" %>>11</option>
					    <option value="0"<%= (start_hour == 12) ? " SELECTED" : "" %>>12</option>
				    </select>
				    &nbsp;:&nbsp;
				    <select name="startMinute">
					    <option value="0"<%= (start_minute == 0) ? " SELECTED" : "" %>>00</option>
					    <option value="5"<%= (start_minute == 5) ? " SELECTED" : "" %>>05</option>
					    <option value="10"<%= (start_minute == 10) ? " SELECTED" : "" %>>10</option>
					    <option value="15"<%= (start_minute == 15) ? " SELECTED" : "" %>>15</option>
					    <option value="20"<%= (start_minute == 20) ? " SELECTED" : "" %>>20</option>
					    <option value="25"<%= (start_minute == 25) ? " SELECTED" : "" %>>25</option>
					    <option value="30"<%= (start_minute == 30) ? " SELECTED" : "" %>>30</option>
					    <option value="35"<%= (start_minute == 35) ? " SELECTED" : "" %>>35</option>
					    <option value="40"<%= (start_minute == 40) ? " SELECTED" : "" %>>40</option>
					    <option value="45"<%= (start_minute == 45) ? " SELECTED" : "" %>>45</option>
					    <option value="50"<%= (start_minute == 50) ? " SELECTED" : "" %>>50</option>
					    <option value="55"<%= (start_minute == 55) ? " SELECTED" : "" %>>55</option>
				    </select>
				    <select name="startAMPMSelect">
					    <option value="<%= Calendar.AM %>"<%= (start_ampm == Calendar.AM) ? " SELECTED" : "" %>>am</option>
					    <option value="<%= Calendar.PM %>"<%= (start_ampm == Calendar.PM) ? " SELECTED" : "" %>>pm</option>
				    </select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			    <div class="leftTM">END HOUR:MINUTE</div>
			    <div class="right">
				    <select name="endHour">
					    <option value="1"<%= (end_hour == 1) ? " SELECTED" : "" %>>1</option>
					    <option value="2"<%= (end_hour == 2) ? " SELECTED" : "" %>>2</option>
					    <option value="3"<%= (end_hour == 3) ? " SELECTED" : "" %>>3</option>
					    <option value="4"<%= (end_hour == 4) ? " SELECTED" : "" %>>4</option>
					    <option value="5"<%= (end_hour == 5) ? " SELECTED" : "" %>>5</option>
					    <option value="6"<%= (end_hour == 6) ? " SELECTED" : "" %>>6</option>
					    <option value="7"<%= (end_hour == 7) ? " SELECTED" : "" %>>7</option>
					    <option value="8"<%= (end_hour == 8) ? " SELECTED" : "" %>>8</option>
					    <option value="9"<%= (end_hour == 9) ? " SELECTED" : "" %>>9</option>
					    <option value="10"<%= (end_hour == 10) ? " SELECTED" : "" %>>10</option>
					    <option value="11"<%= (end_hour == 11) ? " SELECTED" : "" %>>11</option>
					    <option value="0"<%= (end_hour == 12) ? " SELECTED" : "" %>>12</option>
				    </select>
				    &nbsp;:&nbsp;
				    <select name="endMinute">
					    <option value="0"<%= (end_minute == 0) ? " SELECTED" : "" %>>00</option>
					    <option value="5"<%= (end_minute == 5) ? " SELECTED" : "" %>>05</option>
					    <option value="10"<%= (end_minute == 10) ? " SELECTED" : "" %>>10</option>
					    <option value="15"<%= (end_minute == 15) ? " SELECTED" : "" %>>15</option>
					    <option value="20"<%= (end_minute == 20) ? " SELECTED" : "" %>>20</option>
					    <option value="25"<%= (end_minute == 25) ? " SELECTED" : "" %>>25</option>
					    <option value="30"<%= (end_minute == 30) ? " SELECTED" : "" %>>30</option>
					    <option value="35"<%= (end_minute == 35) ? " SELECTED" : "" %>>35</option>
					    <option value="40"<%= (end_minute == 40) ? " SELECTED" : "" %>>40</option>
					    <option value="45"<%= (end_minute == 45) ? " SELECTED" : "" %>>45</option>
					    <option value="50"<%= (end_minute == 50) ? " SELECTED" : "" %>>50</option>
					    <option value="55"<%= (end_minute == 55) ? " SELECTED" : "" %>>55</option>
				    </select>
				    <select name="endAMPMSelect">
					    <option value="<%= Calendar.AM %>"<%= (end_ampm == Calendar.AM) ? " SELECTED" : "" %>>am</option>
					    <option value="<%= Calendar.PM %>"<%= (end_ampm == Calendar.PM) ? " SELECTED" : "" %>>pm</option>
				    </select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup2" class="right">
			    <input id="submitbutton4" class="formbutton" type="submit" name="submit_button" value="<%= adminPractitionerScheduleItem.isNew() ? "Add" : "Update" %> Schedule Details" alt="<%= adminPractitionerScheduleItem.isNew() ? "Add" : "Update" %> Schedule Details" style="margin-right: 10px; "/>
			    <input type="hidden" name="delete_item_id" value="0" />
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
    try {
		Iterator schedule_items = adminPractitionerSchedule.getItems().iterator();
		if (schedule_items.hasNext()) {
			while (schedule_items.hasNext()) {
				PractitionerScheduleItemBean item_obj = (PractitionerScheduleItemBean)schedule_items.next();
%>
					<div class="organization">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:  20px; text-align: left;  "></td>
								<td style="width: 402px; text-align: left;  "><a href="setup_07.jsp?item_id=<%= item_obj.getValue() %>" title=""><%= item_obj.getLabel() %></a></td>
								<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_item_id.value=<%= item_obj.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
							</tr>
						</table>
					</div>
<%
			}
		} else {
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
    } catch (Exception x) {
		x.printStackTrace();
    }
%>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

				</div>

			    </div>
			    <div class="end"></div>
		    </div>
<%
}
%>

		    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%
if (!settings.isSetupComplete())
{
%>
		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup" class="right">
			    <input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Previous" alt="Previous" style="margin-right: 10px; "/>
			    <input id="submitbutton2" disabled="true" class="formbutton" type="submit" name="submit_button" value="Next" alt="Next" style="margin-right: 10px; "/>
			    <input id="submitbutton3"<%= has_work_hours_defined ? "" : " disabled=\"true\"" %> class="formbutton" type="submit" name="submit_button" value="Finish" alt="Finish" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>
<%
}
%>

		</struts-html:form>

	    </div>

	    <div class="end"></div>
	</div>





	<div class="content_TextBlock">
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

    </div>


<%
if (settings.isSetupComplete())
{
%>
</div>
<%
}

} catch (Exception x) {
	x.printStackTrace();
}
%>


</body>
</html>

