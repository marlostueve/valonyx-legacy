<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%
Calendar display_date = null;
if (session.getAttribute("display_date") == null)
{
    display_date = Calendar.getInstance();
    session.setAttribute("display_date", display_date);
}
else
    display_date = (Calendar)session.getAttribute("display_date");
   
int start_hour_of_day = 7;
int start_minute = 0;
int end_hour_of_day = 19;
int end_minute = 0;

Vector practitioners = UKOnlinePersonBean.getPersons(adminCompany, UKOnlineRoleBean.PRACTITIONER_ROLE_NAME);
System.out.println("practitioners found >" + practitioners.size());
int num_practitioners = practitioners.size();

HashMap appointments = AppointmentBean.getAppointments(adminCompany, display_date.getTime());
System.out.println("appointments found >" + appointments.size());


Iterator keys = appointments.keySet().iterator();

/*
while (keys.hasNext())
{

    System.out.println("key >" + (String)keys.next());
}
 */

SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
SimpleDateFormat time_date_format = new SimpleDateFormat("h:mm a");

Calendar open_time = Calendar.getInstance();
open_time.set(Calendar.HOUR_OF_DAY, start_hour_of_day);
open_time.set(Calendar.MINUTE, start_minute);
Calendar closing_time = Calendar.getInstance();
closing_time.set(Calendar.HOUR_OF_DAY, end_hour_of_day);
closing_time.set(Calendar.MINUTE, end_minute);

%>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Valonyx</title>

	<style type="text/css" media="all">
	
	body {
		font: small arial, helvetica, sans-serif;
	}
	
	#header ul {
		list-style: none;
		padding: 0;
		margin: 0;
	}
	
	#header li {
		display: inline;
		margin: 0 2px 0 0;
	}
	
	#header a {
		padding: 0 1em;
		text-decoration: none;
		color: #a80;
		background: #fe5;
	}
	
	#header a:hover {
		background: #fc0;
		color: #540;
	}
	
	#header #selected {
	}
	
	#header #selected a {
		padding-bottom: 2px;
		font-weight: bold;
		color: black;
		color: black;
		background: #fc0;
	}
	
	#content {
		border-top: 2px solid white;
		background: #fc0;
		padding: 1em;
	}
	
	#content p {
		margin: 0;
		padding: 1em;
		background: white;
	}
	
	h1 {
		font-size: 1.5em;
		color: #fc0;
	}
	
	</style>


<style type="text/css">
div.tableContainer {clear: both;border: 1px solid #963;	height: 485px;	overflow: auto;	width: 100%;}
div.tableContainer table {float: left;width: 100%}
\html div.tableContainer table/* */ {margin: 0 -16px 0 0}
thead.fixedHeader tr {position: relative;top: expression(document.getElementById("tableContainer").scrollTop);}
head:first-child+body thead[class].fixedHeader tr {display: block;}
thead.fixedHeader th {background: #C96;border-left: 1px solid #EB8;border-right: 1px solid #B74;border-top: 1px solid #EB8;font-weight: normal;	padding: 4px 3px;text-align: left; font-weight: bold;}
thead.fixedHeader a, thead.fixedHeader a:link, thead.fixedHeader a:visited {color: #FFF;display: block;text-decoration: none;width: 100%}
thead.fixedHeader a:hover {color: #FFF;	display: block;	text-decoration: underline;width: 100%}
head:first-child+body tbody[class].scrollContent {display: block;height: 262px;overflow: auto;width: 100%}
tbody.scrollContent td, tbody.scrollContent tr.normalRow td {background: #FFF;border-bottom: none;border-left: none;border-right: 1px solid #CCC;border-top: 1px solid #DDD;padding: 0px 1px 1px 1px; vertical-align: top; }
tbody.scrollContent tr.alternateRow td {background: #EEE;border-bottom: none;border-left: none;	border-right: 1px solid #CCC;border-top: 1px solid #DDD;padding: 0px 1px 1px 1px}
head:first-child+body thead[class].fixedHeader th {width: 200px}
head:first-child+body thead[class].fixedHeader th + th {width: 240px}
head:first-child+body thead[class].fixedHeader th + th + th {border-right: none;padding: 4px 4px 4px 3px;width: 316px}
head:first-child+body tbody[class].scrollContent td {width: 200px}
head:first-child+body tbody[class].scrollContent td + td {width: 240px}
head:first-child+body tbody[class].scrollContent td + td + td {border-right: none;padding: 2px 4px 2px 3px;width: 300px}
</style>



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

	httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain") %>/ScheduleServlet.html', true);
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
		    document.forms[0].appt_date.value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
		    
		    buildScheduleArray(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
		{
		    alert(httpRequest.responseXML.getElementsByTagName("clients").length);
		    var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
		    //showPeople(xml_response);
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
    
    function buildScheduleArray(xml_str)
    {
	clearSchedule();
	
	var index = 0;
	var key;
	var value;
	while (xml_str.getElementsByTagName("appt")[index] != null)
	{
	    key = xml_str.getElementsByTagName("appt")[index].childNodes[0].childNodes[0].nodeValue;
	    value = xml_str.getElementsByTagName("appt")[index].childNodes[1].childNodes[0].nodeValue;
	    
	    eval("var apptArray = " + value);
	    
	    var element_obj = document.getElementById(key);
	    
	    var appt_image = "default.gif";
	    var appt_state = parseInt(apptArray["state"]);
	    if (appt_state > 1)
	    {
		if (appt_state == 2)
		    appt_image = "reschedule.gif";
		else if (appt_state == 3)
		    appt_image = "late.gif";
		else if (appt_state == 4)
		    appt_image = "checked-in.gif";
		else if (appt_state == 5)
		    appt_image = "checked-out.gif";
		else if (appt_state == 6)
		    appt_image = "cancel.gif";
	    }
	    
	    var appt_id = parseInt(apptArray["id"]);
	    //alert(element_obj.innerHTML);
	    //if ((element_obj.innerHTML == "&nbsp;") || (element_obj.innerHTML == "<img src='../images/spacer.gif' width='12' height='12' alt='' />&nbsp;"))
	    if (endsWith(element_obj.innerHTML, "&nbsp;"))
		element_obj.innerHTML = "<img src='../images/" + appt_image + "' border='0' width='14' height='14' alt='" + apptArray["label"] + "' onclick=\"editAppt();\" />" + apptArray["label"];
	    else
	    {
		element_obj.innerHTML += "&nbsp;<img src='../images/" + appt_image + "' border='0' width='14' height='14' alt='" + apptArray["label"] + "' onclick=\"editAppt();\" />" + apptArray["label"];
		//element_obj.innerHTML = "<table border='0' cellpadding='0' cellspacing='0'><tr><td>" + element_obj.innerHTML + "</td><td><img src='../images/" + appt_image + "' border='0' width='14' height='14' alt='" + apptArray["label"] + "' onclick=\"editAppt();\" />" + apptArray["label"] + "</td></tr></table>";
	    }
	    
	    //eval("document.getElementById('t" + key + "').firstChild.nodeValue = ' '" + apptArray['label']);
	    var bg_color = typeArray[apptArray["type"]]["bg"];
	    element_obj.style.borderColor = bg_color;
	    element_obj.style.backgroundColor = bg_color;
	    element_obj.style.color = typeArray[apptArray["type"]]["txt"];

	    idArray[key] = value;
	    scheduleArray[scheduleIndex] = key;
	    scheduleIndex++;
	    var t_array = key.split("|")[0].split(" ");
	    var ampm = t_array[1];
	    var hour = parseInt(t_array[0].split(":")[0]);
	    var minute = parseInt(t_array[0].split(":")[1]);
	    var duration = parseInt(apptArray["duration"]);
	    
	    //element_obj.rowSpan = duration / 5;
	    //alert(element_obj.rowSpan);
	    
	    duration -= 5;
	    while (duration > 0)
	    {
		minute += 5;
		if (minute == 60)
		{
		    minute = 0;
		    hour++;
		    if (hour == 12)
			ampm = "PM";
		    else if (hour == 13)
			hour = 1;
		}
		duration -= 5;
		var next_key;
		if ((minute == 0) || (minute == 5))
		    next_key = hour + ":0" + minute + " " + ampm + "|" + key.split("|")[1];
		else
		    next_key = hour + ":" + minute + " " + ampm + "|" + key.split("|")[1];

		element_obj = document.getElementById(next_key);
		element_obj.style.backgroundColor = bg_color;
		element_obj.style.borderColor = bg_color;
		scheduleArray[scheduleIndex] = next_key;
		scheduleIndex++;
	    }
	    index++;
	}
    }
    
    function clearSchedule()
    {
	for (key in scheduleArray)
	{
	    var element_obj = document.getElementById(scheduleArray[key]);
	    element_obj.style.borderColor = 'CCC';
	    element_obj.style.backgroundColor = 'FFFFFF';
	    element_obj.innerHTML = "<img src='../images/spacer.gif' width='12' height='12' alt='' />&nbsp;";
	    //element_obj.innerHTML = "&nbsp;";
	    element_obj.rowSpan = 1;
	}
	
	scheduleArray = new Array();
	scheduleIndex = 0;
	idArray = new Array();
    }
    
    function cClk(pId, pLabel, tStr)
    {
	//var element_obj = document.getElementById(key);
	document.getElementById('practitionerSelect').value = pId;
	document.getElementById('practitioner').firstChild.nodeValue = pLabel;
	document.getElementById('newAppointmentTimeInput').value = tStr;
	document.getElementById('time').firstChild.nodeValue = tStr;
	
	if (edit == 0)
	{
	    document.getElementById('apptFormLabel').firstChild.nodeValue = 'New Off Time';
	    
	    document.forms[0].appointmentSelect.value = -1;
	    
	    YAHOO.example.container.dialog1.show();
	}
	else
	{
	    var key = tStr + "|" + pId;
	    
	    //alert(idArray[key]);
	    
	    if (idArray[key])
	    {
		document.getElementById('apptFormLabel').firstChild.nodeValue = 'Edit Off Time';
		
		eval("var apptArray = " + idArray[key]);
		
		document.forms[0].appointmentSelect.value = parseInt(apptArray["id"]);
		
		document.forms[0].typeSelect.value = apptArray["type"];
		document.forms[0].duration.value = apptArray["duration"];
	    }
	    else
	    {
		document.getElementById('apptFormLabel').firstChild.nodeValue = 'New Off Time';
		document.getElementById('appointmentSelect').value = '-1';
		document.forms[0].typeSelect.selectedIndex = 0;
		document.forms[0].duration.value = '';
	    }
	    
	    edit = 0;
	    YAHOO.example.container.dialog1.show();
	}
    }
    
    function cOut(key)
    {
	if (document.getElementById(key).style.backgroundColor == '#dddddd') { document.getElementById(key).style.backgroundColor = ''; }
    }
    
    function cOvr(key)
    {
	if (document.getElementById(key).style.backgroundColor == '') { document.getElementById(key).style.backgroundColor = '#dddddd'; }
    }
    
    function editAppt()
    {
	edit = 1;
    }
    
    function recur()
    {
	
	if (document.getElementById('hidden-recur').style.visibility == 'visible')
	{
	    document.getElementById('hidden-recur').style.visibility='hidden';
	    document.getElementById('bd-r').style.height='210';
	}
	else
	{
	    document.getElementById('hidden-recur').style.visibility='visible';
	    document.getElementById('bd-r').style.height='410';
	}
    }
    
    function endsWith(str, s)
    {
	var reg = new RegExp(s + "$");
	return reg.test(str);
    }

</script>

<style>
#example {height:30em;}
label { display:block;float:left;width:45%;clear:left; }
.clear { clear:both; }
#resp { margin:10px;padding:5px;border:1px solid #ccc;background:#fff;}
#resp li { font-family:monospace }
</style>

<script>
YAHOO.namespace("example.container");

function init() {
	
	// Define various event handlers for Dialog
	var handleSubmit = function() {
		this.submit();
	};
	var handleSuccess = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);
		var response = o.responseText;
		document.getElementById("resp").innerHTML = response;
		
		document.forms[0].appointmentSelect.value = -1;
		document.forms[0].typeSelect.selectedIndex = 0;
		document.forms[0].duration.value = "";
		
		processCommand('offTime');
	};
	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};

	// Instantiate the Dialog
	YAHOO.example.container.dialog1 = new YAHOO.widget.Dialog("dialog1", 
																{ width : "475px",
																  fixedcenter : true,
																  visible : false, 
																  constraintoviewport : true,
																  buttons : [ { text:"Submit", handler:handleSubmit, isDefault:true } ]
																 } );
	
	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialog1.validate = function() {
		var data = this.getData();
		if (data.typeSelect == 0)
		{
		    alert("Please select an appointment type.");
		    return false;
		}
		if (data.duration == "")
		{
		    alert("Please specify a duration.");
		    return false;
		}
		
		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialog1.callback = { success: handleSuccess, failure: handleFailure };
	
	// Render the Dialog
	YAHOO.example.container.dialog1.render();

}

YAHOO.util.Event.onDOMReady(init);

</script>

<!--there is no custom header content for this example-->


</head>

<body class="yui-skin-sam" onload="refresh();">

<%@ include file="channels/channel-schedule-menu.jsp" %>

<div id="content">
	


<div class="exampleIntro">
	<p><span id="today_date"><%= date_format.format(display_date.getTime()) %></span></p>
	<p><a href="#" onclick="processCommand('previousDayOffTime');"> <<.. </a>&nbsp;<a href="#" onclick="processCommand('nextDayOffTime');"> ..>> </a></p>
</div>

<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

<div id="tableContainer" class="tableContainer">
    <table id="schedule" border="1" cellpadding="0" cellspacing="0" width="100%" class="scrollTable">
        <thead class="fixedHeader" id="fixedHeader">
            <tr>
                <th>Time</th>
<%
Iterator practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
%>
                <th><a href="practitioner.jsp?id=<%= practitioner.getId() %>"><%= practitioner.getLabel() %></a></th>
<%
}
%>
            </tr>
        </thead>
        <tbody class="scrollContent">
<%
open_time.set(Calendar.HOUR_OF_DAY, start_hour_of_day);
open_time.set(Calendar.MINUTE, start_minute);

for (int i = 0; open_time.compareTo(closing_time) < 0; i++)
{
    String time_str = time_date_format.format(open_time.getTime());
%>
            <tr>
                <td id="<%= i %>"><nobr><%= time_str %></nobr></td>
<%
    practitioner_itr = practitioners.iterator();
    while (practitioner_itr.hasNext())
    {
	UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
	String key = time_str + "|" + practitioner.getId();
	/*
	AppointmentBean appointment = null;
	if (appointments.containsKey(key))
	    appointment = (AppointmentBean)appointments.get(key);
	*/
%>
                <td onclick="cClk('<%= practitioner.getId() %>','<%= practitioner.getLabel() %>','<%= time_str %>');" onmouseout="cOut('<%= time_str + "|" + practitioner.getId() %>');" onmouseover="cOvr('<%= time_str + "|" + practitioner.getId() %>');" id="<%= time_str + "|" + practitioner.getId() %>">&nbsp;</td>
<%
    }
%>
            </tr>
<%
    open_time.add(Calendar.MINUTE, 5);
}
%>
        </tbody>
    </table>
</div>

<div id="basic" style="width:520px;"></div>

<script type="text/javascript">
    
var mTimer;

var index = 0;
var focusControl;

function handleGetData(str) {

  //eval("focusControl = document.getElementById('" + index + "').focus();");
  index++;
  
  //mTimer = setTimeout('JDate.hashCode(handleGetData);',50000);
}

YAHOO.util.Event.addListener(window, "load", function() {
    //JDate.hashCode(handleGetData);
});

function refresh()
{
    processCommand('offTime');
    //mTimer = setTimeout('refresh();',5000);
}

</script>

<div id="dialog1">
<div class="hd"><span id="apptFormLabel">&nbsp;</span></div>
<div class="bd" id="bd-r">
<form name="newApptForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/newAppointment.x"  >
    
	<input type="hidden" name="appointmentSelect" value="-1">
    
	<label for="practitioner">Practitioner:</label>
	<span id="practitioner">&nbsp;</span>
	<input type="hidden" name="practitionerSelect" value="">
	
	<div class="clear"></div>
	
	<label for="new_appt_date">Date:</label>
	<span id="new_appt_date"><%= date_format.format(display_date.getTime()) %></span>
	<input type="hidden" name="appt_date" value="<%= date_format.format(display_date.getTime()) %>">
	
	<div class="clear"></div>
	
	<label for="time">Time:</label>
	<span id="time">&nbsp;</span>
	<input type="hidden" name="newAppointmentTimeInput" value="">
	
	<div class="clear"></div>

	<label for="typeSelect">Appointment Type:</label>
	<select name="typeSelect" onchange="document.forms['newApptForm'].duration.value = typeArray[document.forms['newApptForm'].typeSelect.value]['duration'];">
		<option value="0">-- SELECT A TYPE --</option>
<%
type_itr = AppointmentTypeBean.getAppointmentTypes(adminCompany, AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE).iterator();
while (type_itr.hasNext())
{
    AppointmentTypeBean appointmentType = (AppointmentTypeBean)type_itr.next();
%>
		<option value="<%= appointmentType.getId() %>"><%= appointmentType.getLabel() %></option>
<%
}
%>
	</select>

	<div class="clear"></div>
		
	<label for="duration">Duration:</label><input type="textbox" name="duration" />
	
	<div class="clear"></div>
	<!--
	<label for="checkboxfield1">Recurring:</label><input id="checkbutton1" type="checkbox" name="checkboxfield1" value="1" onclick="recur();">
	
	<div class="clear"></div>
	
	<div id="hidden-recur" style="position: absolute; visibility: hidden; height: 0px;">
            <input type="radio" name="recur-type" value="Daily">Daily
	    <input type="radio" name="recur-type" value="Weekly">Weekly
	    <input type="radio" name="recur-type" value="Monthly">Monthly
	    <input type="radio" name="recur-type" value="Yearly">Yearly
	</div>
	-->
</form>
</div>
</div>

<div id="resp">Server response will be displayed in this area</div>

</div>

<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
