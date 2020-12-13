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
while (keys.hasNext())
{

    System.out.println("key >" + (String)keys.next());
}

SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
SimpleDateFormat time_date_format = new SimpleDateFormat("h:mm a");



%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Dialog Quickstart Example</title>

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

<title>Valonyx</title>

<style type="text/css">
div.tableContainer {clear: both;border: 1px solid #963;	height: 485px;	overflow: auto;	width: 756px;}
div.tableContainer table {float: left;width: 100%}
\html div.tableContainer table/* */ {margin: 0 -16px 0 0}
thead.fixedHeader tr {position: relative;top: expression(document.getElementById("tableContainer").scrollTop);}
head:first-child+body thead[class].fixedHeader tr {display: block;}
thead.fixedHeader th {background: #C96;border-left: 1px solid #EB8;border-right: 1px solid #B74;border-top: 1px solid #EB8;font-weight: normal;	padding: 4px 3px;text-align: left; font-weight: bold;}
thead.fixedHeader a, thead.fixedHeader a:link, thead.fixedHeader a:visited {color: #FFF;display: block;text-decoration: none;width: 100%}
thead.fixedHeader a:hover {color: #FFF;	display: block;	text-decoration: underline;width: 100%}
head:first-child+body tbody[class].scrollContent {display: block;height: 262px;overflow: auto;width: 100%}
tbody.scrollContent td, tbody.scrollContent tr.normalRow td {background: #FFF;border-bottom: none;border-left: none;border-right: 1px solid #CCC;border-top: 1px solid #DDD;padding: 2px 3px 3px 4px}
tbody.scrollContent tr.alternateRow td {background: #EEE;border-bottom: none;border-left: none;	border-right: 1px solid #CCC;border-top: 1px solid #DDD;padding: 2px 3px 3px 4px}
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

<script type="text/javascript">
    
    var scheduleArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var httpRequest;
    var request_NextDay;

<%
Iterator type_itr = AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
while (type_itr.hasNext())
{
    AppointmentTypeBean appointmentType = (AppointmentTypeBean)type_itr.next();
%>
    typeArray["<%= appointmentType.getId() %>"] = {"bg" : "<%= appointmentType.getBGColorCodeString() %>", "txt" : "<%= appointmentType.getTextColorCodeString() %>" };
<%
}
%>

    function processCommand(command, parameter)
    {
	//clearSchedule();
	
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
		    
		    //alert(xml_response.childNodes[1].childNodes[0].childNodes[1].childNodes[0].nodeValue);
		    //alert(xml_response.getElementsByTagName("appt")[0].childNodes[1].childNodes[0].nodeValue);
		    
		   
		    
		    document.getElementById('today_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
		    
		    //alert(document.getElementById('today_date').firstChild.nodeValue);
		    buildScheduleArray(xml_response);
		    
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
	    
	    document.getElementById(key).innerHTML = "<img src='../images/12.gif' width='12' height='12' alt='' />&nbsp;" + apptArray["label"];
	    //eval("document.getElementById('t" + key + "').firstChild.nodeValue = ' '" + apptArray['label']);
	    var bg_color = typeArray[apptArray["type"]]["bg"];
	    document.getElementById(key).style.borderColor = bg_color;
	    document.getElementById(key).style.backgroundColor = bg_color;
	    document.getElementById(key).style.color = typeArray[apptArray["type"]]["txt"];

	    scheduleArray[scheduleIndex] = key;
	    scheduleIndex++;
	    var t_array = key.split("|")[0].split(" ");
	    var ampm = t_array[1];
	    var hour = parseInt(t_array[0].split(":")[0]);
	    var minute = parseInt(t_array[0].split(":")[1]);
	    var duration = parseInt(apptArray["duration"]);

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

		document.getElementById(next_key).style.backgroundColor = bg_color;
		document.getElementById(next_key).style.borderColor = bg_color;
		scheduleArray[scheduleIndex] = next_key;
		scheduleIndex++;
	    }
	    
	    index++;
	}
    }
    
    function clearSchedule()
    {
	//alert(document.getElementById('schedule').rows[0].cells);
	
	//var schedule_table = document.getElementById('schedule');
	//var row_index = 1;
	//var column_index = 1;
	
	//while (schedule_table.rows[row_index])
	//{
	 //   column_index = 1;
	 //   while (schedule_table.rows[row_index].cells[column_index])
	 //   {
	//	schedule_table.rows[row_index].cells[column_index].style.borderColor = 'CCC';
	//        schedule_table.rows[row_index].cells[column_index].style.backgroundColor = 'FFFFFF';
	//	schedule_table.rows[row_index].cells[column_index].innerHTML = "<img src='../images/spacer.gif' width='12' height='12' alt='' />&nbsp;";
	//	column_index++;
	 //   }
	 //   row_index++;
	//}
	
	
	//document.getElementById('schedule').rows[0].cells[0].style.backgroundColor = '000000';
	//document.getElementById('schedule').rows[0].cells[0].style.borderColor = 'CCC';
	
	
	//for (var ff = 0; ff < 10; ff++)
	//{
	for (key in scheduleArray)
	{
	    var element_obj = document.getElementById(scheduleArray[key]);
	    element_obj.style.borderColor = 'CCC';
	    element_obj.style.backgroundColor = 'FFFFFF';
	    
	    element_obj.innerHTML = "<img src='../images/spacer.gif' width='12' height='12' alt='' />&nbsp;";
	    //eval("document.getElementById('t" + scheduleArray[key] + "').firstChild.nodeValue = ''");
	}
	//}
	scheduleArray = new Array();
	scheduleIndex = 0;
    }

</script>

<!--there is no custom header content for this example-->

</head>

<body class=" yui-skin-sam">

<h1>Dialog Quickstart Example</h1>

<div class="exampleIntro">
	<p><span id="today_date"><%= date_format.format(display_date.getTime()) %></span></p>
	<p><a href="#" onclick="processCommand('previousDay');"> <<.. </a>&nbsp;<a href="#" onclick="processCommand('nextDay');"> ..>> </a></p>
</div>

<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

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
	var handleCancel = function() {
		this.cancel();
	};
	var handleSuccess = function(o) {
		var response = o.responseText;
		response = response.split("<!")[0];
		document.getElementById("resp").innerHTML = response;
	};
	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};

	// Instantiate the Dialog
	YAHOO.example.container.dialog1 = new YAHOO.widget.Dialog("dialog1", 
																{ width : "300px",
																  fixedcenter : true,
																  visible : false, 
																  constraintoviewport : true,
																  buttons : [ { text:"Submit", handler:handleSubmit, isDefault:true },
																			  { text:"Cancel", handler:handleCancel } ]
																 } );
	
	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialog1.validate = function() {
		var data = this.getData();
		if (data.firstname == "" || data.lastname == "") {
			alert("Please enter your first and last names.");
			return false;
		} else {
			return true;
		}
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialog1.callback = { success: handleSuccess,
												 failure: handleFailure };
	
	// Render the Dialog
	YAHOO.example.container.dialog1.render();

	YAHOO.util.Event.addListener("show", "click", YAHOO.example.container.dialog1.show, YAHOO.example.container.dialog1, true);
	YAHOO.util.Event.addListener("hide", "click", YAHOO.example.container.dialog1.hide, YAHOO.example.container.dialog1, true);
}

YAHOO.util.Event.onDOMReady(init);
</script>

<div>
<button id="show">Show dialog1</button> 
<button id="hide">Hide dialog1</button>
</div>

<div id="dialog1">
<div class="hd">Please enter your information</div>
<div class="bd">
<form method="POST" action="assets/post.php">
	<label for="firstname">First Name:</label><input type="textbox" name="firstname" />
	<label for="lastname">Last Name:</label><input type="textbox" name="lastname" />
	<label for="email">E-mail:</label><input type="textbox" name="email" /> 

	<label for="state[]">State:</label>
	<select multiple name="state[]">

		<option value="California">California</option>
		<option value="New Jersey">New Jersey</option>
		<option value="New York">New York</option>
	</select> 

		<div class="clear"></div>

	<label for="radiobuttons">Radio buttons:</label>
	<input type="radio" name="radiobuttons[]" value="1" checked/> 1
	<input type="radio" name="radiobuttons[]" value="2" /> 2
	
		<div class="clear"></div>

	<label for="check">Single checkbox:</label><input type="checkbox" name="check" value="1" /> 1
	
		<div class="clear"></div>
		
	<label for="textarea">Text area:</label><textarea name="textarea"></textarea>

		<div class="clear"></div>

	<label for="cbarray">Multi checkbox:</label>

	<input type="checkbox" name="cbarray[]" value="1" /> 1
	<input type="checkbox" name="cbarray[]" value="2" /> 2
</form>
</div>
</div>

<div id="resp">Server response will be displayed in this area</div>	

<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>
</html>
