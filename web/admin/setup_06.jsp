<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />


<%

session.setAttribute("setup_06", "true");

CompanySettingsBean settings = adminCompany.getSettings();

short open_hour = 7;
short open_minute = 0;
int open_ampm = Calendar.AM;
short close_hour = 8;
short close_minute = 0;
int close_ampm = Calendar.PM;

if (settings.getOpenHour() > (short)0)
{
    open_hour = (settings.getOpenHour() > 12) ? (short)(settings.getOpenHour() - 12) : settings.getOpenHour();
    open_minute = settings.getOpenMinute();
    open_ampm = (settings.getOpenHour() > 11) ? Calendar.PM : Calendar.AM;

    close_hour = (settings.getCloseHour() > 12) ? (short)(settings.getCloseHour() - 12) : settings.getCloseHour();
    close_minute = settings.getCloseMinute();
    close_ampm = (settings.getCloseHour() > 11) ? Calendar.PM : Calendar.AM;
}

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

    function
    selectState()
    {
	var name = document.forms[0].state.value;
	document.forms[0].time_zone.length = 0;

	if (name == 'Connecticut' || name == 'Delaware' || name == 'Georgia' || name == 'Maine' || name == 'Maryland' || name == 'District of Columbia' || name == 'Massachusetts' || name == 'New Hampshire' || name == 'New Jersey' || name == 'New York' || name == 'North Carolina' || name == 'Ohio' || name == 'Pennsylvania' || name == 'Rhode Island' || name == 'South Carolina' || name == 'Vermont' || name == 'Virginia' || name == 'West Virginia' || name == 'Kentucky' || name == 'Tennessee' || name == 'Florida' || name == 'Michigan' || name == 'Indiana')
	    document.forms[0].time_zone.options[document.forms[0].time_zone.length] = new Option('Eastern', 'US/Eastern', false, false);

	if (name == 'Alabama' || name == 'Arkansas' || name == 'Illinois' || name == 'Iowa' || name == 'Louisiana' || name == 'Minnesota' || name == 'Missouri' || name == 'Mississippi' || name == 'Oklahoma' || name == 'Wisconsin' || name == 'Florida' || name == 'Indiana' || name == 'Kansas' || name == 'Kentucky' || name == 'Michigan' || name == 'Nebraska' || name == 'North Dakota' || name == 'South Dakota' || name == 'Tennessee' || name == 'Texas')
	    document.forms[0].time_zone.options[document.forms[0].time_zone.length] = new Option('Central', 'US/Central', false, false);

	if (name == 'New Mexico' || name == 'Wyoming' || name == 'Utah' || name == 'Colorado' || name == 'Montana' || name == 'North Dakota' || name == 'South Dakota' || name == 'Nebraska' || name == 'Texas' || name == 'Idaho' || name == 'Kansas' || name == 'Oregon' || name == 'Arizona' || name == 'Nevada')
	    document.forms[0].time_zone.options[document.forms[0].time_zone.length] = new Option('Mountain', 'US/Mountain', false, false);

	if (name == 'Washington' || name == 'California' || name == 'Nevada' || name == 'Oregon' || name == 'Idaho')
	    document.forms[0].time_zone.options[document.forms[0].time_zone.length] = new Option('Pacific', 'US/Pacific', false, false);

	if (name == 'Alaska')
	    document.forms[0].time_zone.options[document.forms[0].time_zone.length] = new Option('Alaska', 'US/Alaska', false, false);

	if (name == 'Alaska' || name == 'Hawaii')
	    document.forms[0].time_zone.options[document.forms[0].time_zone.length] = new Option('Hawaii-Aleutian', 'US/Hawaii', false, false);
    }

	</script>

<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup", function () {

            // Create a Button using an existing <input> element as a data source
<%
if (settings.isSetupComplete())
{
%>
            var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "submit" });
<%
}
else
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
	    <p class="headline"><%= adminCompany.getLabel() %> <%= settings.isSetupComplete() ? "Administration" : "Setup" %> - <%= settings.isSetupComplete() ? "" : "Step 6: " %>Schedule Settings</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">

		<!-- <p class="headlineA"><%= settings.isSetupComplete() ? "" : "Step 6: " %>Schedule Settings</p> -->
		<p>Provide some information about your Practice's schedule.  You can specify schedule information for individual Practitioners on the next screen.</p>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		<struts-html:form action="/admin/setup06" >

		    <input type="hidden" name="delete_id" />

		    <div class="adminItem">Please <strong style="font-weight: bolder;">specify the daily opening time</strong> for your Practice.  The same opening time will be used throughout the week, so specify the earliest opening time for your Practice.</div>

		    <div class="adminItem">
			    <div class="leftTM">OPEN TIME</div>
			    <div class="right">
				    <select name="startHour" class="select">
					    <option value="1"<%= (open_hour == (short)1) ? " selected" : "" %>>1</option>
					    <option value="2"<%= (open_hour == (short)2) ? " selected" : "" %>>2</option>
					    <option value="3"<%= (open_hour == (short)3) ? " selected" : "" %>>3</option>
					    <option value="4"<%= (open_hour == (short)4) ? " selected" : "" %>>4</option>
					    <option value="5"<%= (open_hour == (short)5) ? " selected" : "" %>>5</option>
					    <option value="6"<%= (open_hour == (short)6) ? " selected" : "" %>>6</option>
					    <option value="7"<%= (open_hour == (short)7) ? " selected" : "" %>>7</option>
					    <option value="8"<%= (open_hour == (short)8) ? " selected" : "" %>>8</option>
					    <option value="9"<%= (open_hour == (short)9) ? " selected" : "" %>>9</option>
					    <option value="10"<%= (open_hour == (short)10) ? " selected" : "" %>>10</option>
					    <option value="11"<%= (open_hour == (short)11) ? " selected" : "" %>>11</option>
					    <option value="12"<%= (open_hour == (short)12) ? " selected" : "" %>>12</option>
				    </select>&nbsp;:&nbsp;
				    <select name="startMinute" class="select">
					    <option value="0"<%= (open_minute == (short)0) ? " selected" : "" %>>00</option>
					    <option value="5"<%= (open_minute == (short)5) ? " selected" : "" %>>05</option>
					    <option value="10"<%= (open_minute == (short)10) ? " selected" : "" %>>10</option>
					    <option value="15"<%= (open_minute == (short)15) ? " selected" : "" %>>15</option>
					    <option value="20"<%= (open_minute == (short)20) ? " selected" : "" %>>20</option>
					    <option value="25"<%= (open_minute == (short)25) ? " selected" : "" %>>25</option>
					    <option value="30"<%= (open_minute == (short)30) ? " selected" : "" %>>30</option>
					    <option value="35"<%= (open_minute == (short)35) ? " selected" : "" %>>35</option>
					    <option value="40"<%= (open_minute == (short)40) ? " selected" : "" %>>40</option>
					    <option value="45"<%= (open_minute == (short)45) ? " selected" : "" %>>45</option>
					    <option value="50"<%= (open_minute == (short)50) ? " selected" : "" %>>50</option>
					    <option value="55"<%= (open_minute == (short)55) ? " selected" : "" %>>55</option>
				    </select>&nbsp;
				    <select name="startAMPM" class="select">
					    <option value="<%= Calendar.AM %>"<%= (open_ampm == Calendar.AM) ? " selected" : "" %>>AM</option>
					    <option value="<%= Calendar.PM %>"<%= (open_ampm == Calendar.PM) ? " selected" : "" %>>PM</option>
				    </select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">Please <strong style="font-weight: bolder;">specify the daily closing time</strong> for your Practice.  Once again, the same closing time will be used throughout the week, so specify the latest closing time for your Practice.</div>

		    <div class="adminItem">
			    <div class="leftTM">CLOSING TIME</div>
			    <div class="right">
				    <select name="endHour" class="select">
					    <option value="1"<%= (close_hour == (short)1) ? " selected" : "" %>>1</option>
					    <option value="2"<%= (close_hour == (short)2) ? " selected" : "" %>>2</option>
					    <option value="3"<%= (close_hour == (short)3) ? " selected" : "" %>>3</option>
					    <option value="4"<%= (close_hour == (short)4) ? " selected" : "" %>>4</option>
					    <option value="5"<%= (close_hour == (short)5) ? " selected" : "" %>>5</option>
					    <option value="6"<%= (close_hour == (short)6) ? " selected" : "" %>>6</option>
					    <option value="7"<%= (close_hour == (short)7) ? " selected" : "" %>>7</option>
					    <option value="8"<%= (close_hour == (short)8) ? " selected" : "" %>>8</option>
					    <option value="9"<%= (close_hour == (short)9) ? " selected" : "" %>>9</option>
					    <option value="10"<%= (close_hour == (short)10) ? " selected" : "" %>>10</option>
					    <option value="11"<%= (close_hour == (short)11) ? " selected" : "" %>>11</option>
					    <option value="12"<%= (close_hour == (short)12) ? " selected" : "" %>>12</option>
				    </select>&nbsp;:&nbsp;
				    <select name="endMinute" class="select">
					    <option value="0"<%= (close_minute == (short)0) ? " selected" : "" %>>00</option>
					    <option value="5"<%= (close_minute == (short)5) ? " selected" : "" %>>05</option>
					    <option value="10"<%= (close_minute == (short)10) ? " selected" : "" %>>10</option>
					    <option value="15"<%= (close_minute == (short)15) ? " selected" : "" %>>15</option>
					    <option value="20"<%= (close_minute == (short)20) ? " selected" : "" %>>20</option>
					    <option value="25"<%= (close_minute == (short)25) ? " selected" : "" %>>25</option>
					    <option value="30"<%= (close_minute == (short)30) ? " selected" : "" %>>30</option>
					    <option value="35"<%= (close_minute == (short)35) ? " selected" : "" %>>35</option>
					    <option value="40"<%= (close_minute == (short)40) ? " selected" : "" %>>40</option>
					    <option value="45"<%= (close_minute == (short)45) ? " selected" : "" %>>45</option>
					    <option value="50"<%= (close_minute == (short)50) ? " selected" : "" %>>50</option>
					    <option value="55"<%= (close_minute == (short)55) ? " selected" : "" %>>55</option>
				    </select>&nbsp;
				    <select name="endAMPM" class="select">
					    <option value="<%= Calendar.AM %>"<%= (close_ampm == Calendar.AM) ? " selected" : "" %>>AM</option>
					    <option value="<%= Calendar.PM %>"<%= (close_ampm == Calendar.PM) ? " selected" : "" %>>PM</option>
				    </select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide the State and Time Zone</strong> where your Practice is located.</div>

		    <div class="adminItem">
			    <div class="leftTM">STATE</div>
			    <div class="right">
				    <select name="state" class="select" style="width: 179px;" onchange="javascript:selectState();">
					  <option VALUE="">PLEASE SELECT
					  <option VALUE="Alabama"<%= settings.getStateString().equals("Alabama") ? " SELECTED" : "" %>>Alabama</option>
					  <option VALUE="Alaska"<%= settings.getStateString().equals("Alaska") ? " SELECTED" : "" %>>Alaska</option>
					  <option VALUE="Arizona"<%= settings.getStateString().equals("Arizona") ? " SELECTED" : "" %>>Arizona</option>
					  <option VALUE="Arkansas"<%= settings.getStateString().equals("Arkansas") ? " SELECTED" : "" %>>Arkansas</option>
					  <option VALUE="California"<%= settings.getStateString().equals("California") ? " SELECTED" : "" %>>California</option>
					  <option VALUE="Colorado"<%= settings.getStateString().equals("Colorado") ? " SELECTED" : "" %>>Colorado</option>
					  <option VALUE="Connecticut"<%= settings.getStateString().equals("Connecticut") ? " SELECTED" : "" %>>Connecticut</option>
					  <option VALUE="Delaware"<%= settings.getStateString().equals("Delaware") ? " SELECTED" : "" %>>Delaware</option>
					  <option VALUE="District of Columbia"<%= settings.getStateString().equals("District of Columbia") ? " SELECTED" : "" %>>District of Columbia</option>
					  <option VALUE="Florida"<%= settings.getStateString().equals("Florida") ? " SELECTED" : "" %>>Florida</option>
					  <option VALUE="Georgia"<%= settings.getStateString().equals("Georgia") ? " SELECTED" : "" %>>Georgia</option>
					  <option VALUE="Hawaii"<%= settings.getStateString().equals("Hawaii") ? " SELECTED" : "" %>>Hawaii</option>
					  <option VALUE="Idaho"<%= settings.getStateString().equals("Idaho") ? " SELECTED" : "" %>>Idaho</option>
					  <option VALUE="Illinois"<%= settings.getStateString().equals("Illinois") ? " SELECTED" : "" %>>Illinois</option>
					  <option VALUE="Indiana"<%= settings.getStateString().equals("Indiana") ? " SELECTED" : "" %>>Indiana</option>
					  <option VALUE="Iowa"<%= settings.getStateString().equals("Iowa") ? " SELECTED" : "" %>>Iowa</option>
					  <option VALUE="Kansas"<%= settings.getStateString().equals("Kansas") ? " SELECTED" : "" %>>Kansas</option>
					  <option VALUE="Kentucky"<%= settings.getStateString().equals("Kentucky") ? " SELECTED" : "" %>>Kentucky</option>
					  <option VALUE="Louisiana"<%= settings.getStateString().equals("Louisiana") ? " SELECTED" : "" %>>Louisiana</option>
					  <option VALUE="Maine"<%= settings.getStateString().equals("Maine") ? " SELECTED" : "" %>>Maine</option>
					  <option VALUE="Maryland"<%= settings.getStateString().equals("Maryland") ? " SELECTED" : "" %>>Maryland</option>
					  <option VALUE="Massachusetts"<%= settings.getStateString().equals("Massachusetts") ? " SELECTED" : "" %>>Massachusetts</option>
					  <option VALUE="Michigan"<%= settings.getStateString().equals("Michigan") ? " SELECTED" : "" %>>Michigan</option>
					  <option VALUE="Minnesota"<%= settings.getStateString().equals("Minnesota") ? " SELECTED" : "" %>>Minnesota</option>
					  <option VALUE="Mississippi"<%= settings.getStateString().equals("Mississippi") ? " SELECTED" : "" %>>Mississippi</option>
					  <option VALUE="Missouri"<%= settings.getStateString().equals("Missouri") ? " SELECTED" : "" %>>Missouri</option>
					  <option VALUE="Montana"<%= settings.getStateString().equals("Montana") ? " SELECTED" : "" %>>Montana</option>
					  <option VALUE="Nebraska"<%= settings.getStateString().equals("Nebraska") ? " SELECTED" : "" %>>Nebraska</option>
					  <option VALUE="Nevada"<%= settings.getStateString().equals("Nevada") ? " SELECTED" : "" %>>Nevada</option>
					  <option VALUE="New Mexico"<%= settings.getStateString().equals("New Mexico") ? " SELECTED" : "" %>>New Mexico</option>
					  <option VALUE="New Jersey"<%= settings.getStateString().equals("New Jersey") ? " SELECTED" : "" %>>New Jersey</option>
					  <option VALUE="New York"<%= settings.getStateString().equals("New York") ? " SELECTED" : "" %>>New York</option>
					  <option VALUE="New Hampshire"<%= settings.getStateString().equals("New Hampshire") ? " SELECTED" : "" %>>New Hampshire</option>
					  <option VALUE="North Carolina"<%= settings.getStateString().equals("North Carolina") ? " SELECTED" : "" %>>North Carolina</option>
					  <option VALUE="North Dakota"<%= settings.getStateString().equals("North Dakota") ? " SELECTED" : "" %>>North Dakota</option>
					  <option VALUE="Ohio"<%= settings.getStateString().equals("Ohio") ? " SELECTED" : "" %>>Ohio</option>
					  <option VALUE="Oklahoma"<%= settings.getStateString().equals("Oklahoma") ? " SELECTED" : "" %>>Oklahoma</option>
					  <option VALUE="Oregon"<%= settings.getStateString().equals("Oregon") ? " SELECTED" : "" %>>Oregon</option>
					  <option VALUE="Pennsylvania"<%= settings.getStateString().equals("Pennsylvania") ? " SELECTED" : "" %>>Pennsylvania</option>
					  <option VALUE="Rhode Island"<%= settings.getStateString().equals("Rhode Island") ? " SELECTED" : "" %>>Rhode Island</option>
					  <option VALUE="South Carolina"<%= settings.getStateString().equals("South Carolina") ? " SELECTED" : "" %>>South Carolina</option>
					  <option VALUE="South Dakota"<%= settings.getStateString().equals("South Dakota") ? " SELECTED" : "" %>>South Dakota</option>
					  <option VALUE="Tennessee"<%= settings.getStateString().equals("Tennessee") ? " SELECTED" : "" %>>Tennessee</option>
					  <option VALUE="Texas"<%= settings.getStateString().equals("Texas") ? " SELECTED" : "" %>>Texas</option>
					  <option VALUE="Utah"<%= settings.getStateString().equals("Utah") ? " SELECTED" : "" %>>Utah</option>
					  <option VALUE="Virginia"<%= settings.getStateString().equals("Virginia") ? " SELECTED" : "" %>>Virginia</option>
					  <option VALUE="Vermont"<%= settings.getStateString().equals("Vermont") ? " SELECTED" : "" %>>Vermont</option>
					  <option VALUE="Washington"<%= settings.getStateString().equals("Washington") ? " SELECTED" : "" %>>Washington</option>
					  <option VALUE="West Virginia"<%= settings.getStateString().equals("West Virginia") ? " SELECTED" : "" %>>West Virginia</option>
					  <option VALUE="Wisconsin"<%= settings.getStateString().equals("Wisconsin") ? " SELECTED" : "" %>>Wisconsin</option>
					  <option VALUE="Wyoming"<%= settings.getStateString().equals("Wyoming") ? " SELECTED" : "" %>>Wyoming</option>
					</select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			    <div class="leftTM">TIME ZONE</div>
			    <div class="right">
				    <select name="time_zone" class="select" style="width: 179px;">
<%
if (!settings.getTimeZoneString().equals(""))
{
%>
					<option value="<%= settings.getTimeZoneString() %>"<%= settings.getStateString().equals("Wyoming") ? " SELECTED" : "" %>><%= settings.getTimeZoneString() %></option>
<%
}
%>
				    </select>
			    </div>
			    <div class="end"></div>
		    </div>

<%
if (settings.isSetupComplete())
{
%>
		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup" class="right">
			    <input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Submit" alt="Submit" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>
<%
}
else
{
%>
		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup" class="right">
			    <input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Previous" alt="Previous" style="margin-right: 10px; "/>
			    <input id="submitbutton2" class="formbutton" type="submit" name="submit_button" value="Next" alt="Next" style="margin-right: 10px; "/>
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

