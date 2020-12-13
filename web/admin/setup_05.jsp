<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminAppointmentType" class="com.badiyan.uk.online.beans.AppointmentTypeBean" scope="session" />


<%

session.setAttribute("setup_05", "true");

String bg_color_hex = "FFFFFF";

if (request.getParameter("id") != null)
{
    AppointmentTypeBean obj = AppointmentTypeBean.getAppointmentType(Integer.parseInt(request.getParameter("id")));
    CompanyBean company_obj = obj.getCompany();

    // ensure that this person is an admin for this company

    if (adminCompany.getId() == company_obj.getId())
    {
		adminAppointmentType = obj;
		session.setAttribute("adminAppointmentType", adminAppointmentType);
    }
}

//if (!adminAppointmentType.isNew())
bg_color_hex = adminAppointmentType.getBGColorCodeString();

String message = "<strong style=\"font-weight: bolder;\">Setup the Appointment Types for " + adminCompany.getLabel() + "</strong>.  Example appointment types might include <strong style=\"font-weight: bolder;\">Adjustment</strong>, <strong style=\"font-weight: bolder;\">60 Minute Massage</strong>, or <strong style=\"font-weight: bolder;\">Orthotics Casting</strong>.";

boolean has_appointment_types = false;
Vector appointment_types = AppointmentTypeBean.getAppointmentTypesNoOfftime(adminCompany);
if (appointment_types.size() > 0)
    has_appointment_types = true;

CompanySettingsBean settings = adminCompany.getSettings();

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
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/slider/assets/skins/sam/slider.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/colorpicker/assets/skins/sam/colorpicker.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/slider/slider-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/colorpicker/colorpicker-min.js"></script>
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

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/slider/assets/skins/sam/slider.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/colorpicker/assets/skins/sam/colorpicker.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/slider/slider-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/colorpicker/colorpicker-min.js"></script>
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

    var picker;
    var hex_value = "<%= bg_color_hex %>";

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

    function HexToR(h) {return parseInt((cutHex(h)).substring(0,2),16)}
    function HexToG(h) {return parseInt((cutHex(h)).substring(2,4),16)}
    function HexToB(h) {return parseInt((cutHex(h)).substring(4,6),16)}
    function cutHex(h) {return (h.charAt(0)=="#") ? h.substring(1,7):h}

	</script>

<script type="text/javascript">
(function() {
    var Event = YAHOO.util.Event;

    Event.onDOMReady(function() {

            picker = new YAHOO.widget.ColorPicker("container", {
                    showhsvcontrols: true,
                    showhexcontrols: true,
					images: {PICKER_THUMB: "http://developer.yahoo.com/yui/examples/colorpicker/assets/picker_thumb.png",
						HUE_THUMB: "http://developer.yahoo.com/yui/examples/colorpicker/assets/hue_thumb.png"}
                });

		picker.setValue([HexToR(hex_value), HexToG(hex_value), HexToB(hex_value)], false);

			//subscribe to the rgbChange event;
			picker.on("rgbChange", onRgbChange);


		

			//use setValue to reset the value to white:
			Event.on("reset", "click", function(e) {
				picker.setValue([255, 255, 255], false); //false here means that rgbChange
													     //wil fire; true would silence it
			});


		


        });



})();
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

	    var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", { value: "Add Appointment Type" });


        });

    } ();

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton1 = new Button("checkbutton1", { label: "Allow Double Booking" });


        });


    }());

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup2", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton2 = new Button("checkbutton2", { label: "This Appointment Type is Active" });


        });


    }());

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup3", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton3 = new Button("checkbutton3", { label: "Allow Off-Hours Scheduling" });


        });


    }());

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup4", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton4 = new Button("checkbutton4", { label: "Allow Client Scheduling" });


        });


    }());

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
%>

    <div id="content">


	<div class="content_TextBlock">
	    <p class="headline"><%= adminCompany.getLabel() %> <%= settings.isSetupComplete() ? "Administration" : "Setup" %> - <%= settings.isSetupComplete() ? "" : "Step 5: " %>Appointment Types</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">

		<!-- <p class="headlineA"><%= settings.isSetupComplete() ? "" : "Step 5: " %>Appointment Types</p> -->
		<p><%= message %><%= has_appointment_types ? "" : "  <strong style=\"font-weight: bolder;\">Your Practice must have at least one Appointment Type.</strong>" %></p>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		<struts-html:form action="/admin/setup05" focus="nameInput">

		    <input type="hidden" name="delete_id" />
<%
if (!adminAppointmentType.isNew())
{
%>
		    <div class="adminItem">You have selected to update <strong style="font-weight: bolder;"><%= adminAppointmentType.getLabel() %></strong>.  When you are finished making changes, please click <strong style="font-weight: bolder;">Update Appointment Type.</strong></div>
<%
}
%>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide a name</strong> for this Appointment Type.  <strong style="font-weight: bolder;">Adjustment</strong> or <strong style="font-weight: bolder;">60 Minute Massage</strong>, for example.</div>

		    <div class="adminItem">
			    <div class="leftTM">TYPE NAME</div>
			    <div class="right">
				    <input name="nameInput" onfocus="select();" value="<%= adminAppointmentType.getLabel() %>" size="31" maxlength="50" class="inputbox" style="width: 206px;" />
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">Select the typical duration, in minutes, of this appointment type.  This can be left blank if there is no default duration.</div>

		    <div class="adminItem">
			    <div class="leftTM">DEFAULT DURATION</div>
			    <div class="right">
				    <input name="duration" onfocus="select();" value="<%= adminAppointmentType.getDurationString() %>" size="31" maxlength="3" class="inputbox" style="width: 206px;" />
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">Appointment types are active by default and available for scheduling.  If you'd like to disable scheduling of this type, <strong style="font-weight: bolder;">click the "This Appointment Type is Active" button</strong> to inactivate.</div>

		    <div class="adminItem" id="checkboxbuttonsfrommarkup2">
			<div class="leftTM"></div>
			<div class="right">
			    <input id="checkbutton2" type="checkbox" name="is_active" value="1"<%= (adminAppointmentType.isNew() || adminAppointmentType.isActive()) ? " checked" : "" %>>
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem"><strong style="font-weight: bolder;">Choose a color</strong> that will be used for all appointments of this type.  <strong style="font-weight: bolder;">Drag the slider</strong> up and down to select a hue.  <strong style="font-weight: bolder;">Click or drag the circle</strong> to select your color.</div>

		    <div class="adminItem" >
			    <div class="leftTM">BACKGROUND COLOR</div>
			    <div class="right" id="pickermarkup">
				    <!-- <input name="bg_color" onfocus="select();" value="<%= adminAppointmentType.getBGColorCodeString() %>" size="31" maxlength="6" class="inputbox" style="width: 206px;" /> -->
				    <div id="container" style="background-color: white; height: 175px;" >
				    </div>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">If you'd like to schedule multiple appointments of this type at the same time, <strong style="font-weight: bolder;">click the "Allow Double Booking" button</strong>.</div>

		    <div class="adminItem" id="checkboxbuttonsfrommarkup">
			<div class="leftTM"></div>
			<div class="right">
			    <input id="checkbutton1" type="checkbox" name="allow_double_booking" value="1"<%= adminAppointmentType.isDoubleBookingAllowed() ? " checked" : "" %>>
			</div>
			<div class="end"></div>
		    </div>
<%
boolean is_allowed_off_hours = false;
try {
	Vector offtime_appointments = AppointmentTypeBean.getAppointmentTypes(adminCompany, AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE);
	//System.out.println("num offtime_appointments >" + offtime_appointments.size());
	if (offtime_appointments.size() == 1) {
		AppointmentTypeBean offHoursAppointmentType = (AppointmentTypeBean)offtime_appointments.get(0);
		Vector allowed = offHoursAppointmentType.getAllowedAppointmentTypes();
		if (allowed.contains(adminAppointmentType))
			is_allowed_off_hours = true;
	}
	else
		throw new Exception("Offtime Appointment Type not found.");
} catch (Exception x) {
	x.printStackTrace();
}
%>
		    <div class="adminItem">If you'd like to be able to schedule <%= adminAppointmentType.isNew() ? "appointments of this type" : adminAppointmentType.getLabel() + " appointments" %> during a practitioner's off-time, <strong style="font-weight: bolder;">click the "Allow Off-Hours Scheduling" button</strong>.</div>

		    <div class="adminItem" id="checkboxbuttonsfrommarkup3">
			<div class="leftTM"></div>
			<div class="right">
			    <input id="checkbutton3" type="checkbox" name="allow_off_hours" value="1"<%= is_allowed_off_hours ? " checked" : "" %>>
			</div>
			<div class="end"></div>
		    </div>

			
			
		    <div class="adminItem">If you'd like to allow your clients schedule appointments of this type online, <strong style="font-weight: bolder;">click the "Allow Client Scheduling" button</strong>.</div>

		    <div class="adminItem" id="checkboxbuttonsfrommarkup4">
			<div class="leftTM"></div>
			<div class="right">
			    <input id="checkbutton4" type="checkbox" name="allow_client_scheduling" value="1"<%= adminAppointmentType.allowClientScheduling() ? " checked" : "" %>>
			</div>
			<div class="end"></div>
		    </div>

			
			
		    <div class="adminItem">Most Appointment Types will be Client Appointments.  You can also create appointments that are Meetings.  <strong style="font-weight: bolder;">Select either Client Appointment or Meeting</strong> for this Appointment Type.</div>

		    <div class="adminItem">
			    <div class="leftTM">TYPE</div>
			    <div class="right">
				    <select name="appointment_type_type" class="select" style="width: 309px;">
					    <option value="0">-- SELECT A TYPE --</option>
					    <option value="<%= AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE %>"<%= (adminAppointmentType.getType() == AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE) ? " selected" : "" %>>Client Appointment</option>
					    <option value="<%= AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE %>"<%= (adminAppointmentType.getType() == AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE) ? " selected" : "" %>>Meeting</option>
				    </select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem"><strong style="font-weight: bolder;">Select a Practice Area</strong> for this Appointment Type.  For an <strong style="font-weight: bolder;">Adjustment</strong> Appointment Type you'd select the <strong style="font-weight: bolder;">Chiropractic</strong> Practice Area, for example.</div>

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
			<div id="submitbuttonsfrommarkup2" class="right">
			    <input id="submitbutton4" class="formbutton" type="submit" name="submit_button" value="<%= adminAppointmentType.isNew() ? "Add" : "Update" %> Appointment Type" alt="<%= adminAppointmentType.isNew() ? "Add" : "Update" %> Appointment Type" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>
<%
if (has_appointment_types)
{
%>
		    <div class="adminItem">Choose a Appointment Type from the list below to edit that Appointment Type.</div>
<%
}
%>
		    <div class="content_AdministrationTable">

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			<div class="heading">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr style="display: block;">
						<td style="width:  10px; text-align: left;  "></td>
						<td style="width: 272px; text-align: left;  ">Appointment Type</td>
						<td style="width: 100px; text-align: center;  ">Practice Area</td>
						<td style="width:  60px; text-align: center;  ">Active/Inactive</td>
						<td style="width:  50px; text-align: center;  "></td>
					</tr>
				</table>
			</div>
			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator appointment_types_itr = appointment_types.iterator();
    if (appointment_types_itr.hasNext())
    {
	while (appointment_types_itr.hasNext())
	{
	    AppointmentTypeBean obj = (AppointmentTypeBean)appointment_types_itr.next();
		String pa_str = "&nbsp;";
		try
		{
			pa_str = obj.getPracticeArea().getLabel();
		}
		catch (Exception x) {}
%>
			    <!-- BEGIN Job Title -->
			    <div class="jobTitle">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">
					    <tr>
						    <td style="width:  10px; text-align: left;  "></td>
						    <td style="width: 272px; text-align: left;  "><a href="setup_05.jsp?id=<%= obj.getValue() %>" title=""><%= obj.getLabel() %></a></td>
						    <td style="width: 100px; text-align: center;"><%= pa_str %></td>
						    <td style="width:  60px; text-align: center;"><%= obj.isActive() ? "Active" : "Inactive" %></td>
						    <td style="width:  50px; text-align: center;"><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= obj.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
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

<%
if (!settings.isSetupComplete())
{
%>
		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup" class="right">
			    <input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Previous" alt="Previous" style="margin-right: 10px; "/>
			    <input id="submitbutton2"<%= has_appointment_types ? "" : " disabled=\"true\"" %> class="formbutton" type="submit" name="submit_button" value="Next" alt="Next" style="margin-right: 10px; "/>
			    <input id="submitbutton3" disabled="true" class="formbutton" type="submit" name="submit_button" value="Finish" alt="Finish" style="margin-right: 10px; "/>
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
%>


</body>
</html>

