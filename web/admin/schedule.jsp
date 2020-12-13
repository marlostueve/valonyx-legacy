<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPracticeArea" class="com.badiyan.uk.online.beans.PracticeAreaBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%

boolean left_expanded = true;
boolean right_expanded = false;

try
{
    UKOnlinePersonBean adminPerson = (UKOnlinePersonBean)loginBean.getPerson();
    left_expanded = adminPerson.isLeftExpanded();
    right_expanded = adminPerson.isRightExpanded();
}
catch (Exception exception1)
{
    RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher("/admin/schedule-login.jsp");
    request.setAttribute("session-expired", "true");
    rd.forward(request, response);
    return;
}

Calendar now = Calendar.getInstance();
Calendar display_date = null;
if (session.getAttribute("display_date") == null)
{
    display_date = Calendar.getInstance();
    session.setAttribute("display_date", display_date);
}
else
    display_date = (Calendar)session.getAttribute("display_date");
   
int start_hour_of_day = adminCompany.getStartHourOfDay();
int start_minute = adminCompany.getStartMinute();
int end_hour_of_day = adminCompany.getEndHourOfDay();
int end_minute = adminCompany.getEndMinute();

Vector practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany);

Vector practitioners = null;
try
{
    if (request.getParameter("practice_area_id") != null)
    {
	if (request.getParameter("practice_area_id").equals("0"))
	    adminPracticeArea = new PracticeAreaBean();
	else
	{
	    adminPracticeArea = PracticeAreaBean.getPracticeArea(adminCompany, Integer.parseInt(request.getParameter("practice_area_id")));
	    practitioners = adminPracticeArea.getPractitioners();
	}
	session.setAttribute("adminPracticeArea", adminPracticeArea);
    }
    else
    {
	if (!adminPracticeArea.isNew())
	    practitioners = adminPracticeArea.getPractitioners();
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
if (practitioners == null)
{
    //practitioners = UKOnlinePersonBean.getPersons(adminCompany, UKOnlineRoleBean.PRACTITIONER_ROLE_NAME);
    practitioners = UKOnlinePersonBean.getPractitioners(adminCompany);
}

SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
SimpleDateFormat time_date_format = new SimpleDateFormat("h:mm a");

Calendar open_time = Calendar.getInstance();
open_time.set(Calendar.HOUR_OF_DAY, start_hour_of_day);
open_time.set(Calendar.MINUTE, start_minute);
Calendar closing_time = Calendar.getInstance();
closing_time.set(Calendar.HOUR_OF_DAY, end_hour_of_day);
closing_time.set(Calendar.MINUTE, end_minute);

Vector codes = CheckoutCodeBean.getCheckoutCodes(adminCompany);

%>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Valonyx</title>

	<style type="text/css" media="all">
	
	html, body {
		height: 100%;
	}
	body {
		font: small arial, helvetica, sans-serif;
		height: 100%;
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
		background: #ddd;
	}

	#header a:hover {
		background: #426fd9;
		color: #fff;
	}

	#header #selected {
	}

	#header #selected a {
		padding-bottom: 3px;
		font-weight: bold;
		color: #fff;
        background: #426fd9;
	}

	#content {
		border-top: 2px solid white;
		padding: 1em;
	}

	#content p {
		margin: 0;
		padding: 0em;
	}
	
	h1 {
		font-size: 1.5em;
		color: #fc0;
	}
	
	#content h2 {
		font-size: 1.25em;
		color: #000;
		margin: 0;
	}
	
	#headline
		{
		/*\*/ overflow: hidden; /* */
		}
	* html #headline
		{
		
		}

.horizontalRule { height: 1px; background-color: #adafaf; }



	</style>


<style type="text/css">
div.tableContainer {clear: both; height: 100%;	overflow: auto;	width: 100%;}
div.tableContainer table {float: left;width: 100%}
\html div.tableContainer table/* */ {margin: 0 -16px 0 0}
thead.fixedHeader tr {position: relative;top: expression(document.getElementById("tableContainer").scrollTop);}
head:first-child+body thead[class].fixedHeader tr {display: block;}
thead.fixedHeader th {background: #426fd9;border-left: 1px solid #edf5ff;border-right: 1px solid #cccccc;border-top: 1px solid #edf5ff;font-weight: normal;	padding: 4px 3px;text-align: left; font-weight: bold;}
thead.fixedHeader a, thead.fixedHeader a:link, thead.fixedHeader a:visited {color: #FFF;display: block;text-decoration: none;width: 100%}
thead.fixedHeader a:hover {color: #FFF;	display: block;	text-decoration: underline;width: 100%}
head:first-child+body tbody[class].scrollContent {display: block;height: 262px;overflow: auto;width: 100%}
tbody.scrollContent td, tbody.scrollContent tr.normalRow td {background: #FFF;border-bottom: none;border-left: none;border-right: 1px solid #CCC;border-top: 1px solid #CCC;padding: 0px 1px 1px 1px; vertical-align: top; }
tbody.scrollContent tr.alternateRow td.alternateRow {background: #EEE;border-bottom: none;border-left: none;	border-right: 1px solid #CCC;border-top: 1px solid #DDD;padding: 0px 1px 1px 1px}
tbody.scrollContent tr.highlightRow td {background: #FFB;border-bottom: none;border-left: none; border-right: 1px solid #CCC;border-top: 1px solid #CCC;padding: 0px 1px 1px 1px}
tbody.scrollContent td.offRow {background: #888}
tbody.scrollContent a, tbody.scrollContent a:link, tbody.scrollContent a:visited {color: #FFF;text-decoration: none}
tbody.scrollContent a:hover {color: #FFF;text-decoration: underline}
head:first-child+body thead[class].fixedHeader th {width: 200px}
head:first-child+body thead[class].fixedHeader th + th {width: 240px}
head:first-child+body thead[class].fixedHeader th + th + th {border-right: none;padding: 4px 4px 4px 3px;width: 316px}
head:first-child+body tbody[class].scrollContent td {width: 200px}
head:first-child+body tbody[class].scrollContent td + td {width: 240px}
head:first-child+body tbody[class].scrollContent td + td + td {border-right: none;padding: 2px 4px 2px 3px;width: 300px}
</style>

<style type="text/css">

div.tableContainer2 {clear: both;border: 1px solid #963;	height: 450px;	overflow: auto;	width: 100%;}
div.tableContainer2 table {float: left;width: 100%}
div.tableContainer2 table {margin: 0 -16px 0 0}
thead.fixedHeader2 tr {position: relative;top: expression(document.getElementById("tableContainer2").scrollTop);}
head:first-child+body thead[class].fixedHeader2 tr {display: block;}
thead.fixedHeader2 th {background: #C96;border-left: 1px solid #EB8;border-right: 1px solid #B74;border-top: 1px solid #EB8;font-weight: normal;	padding: 4px 3px;text-align: left; font-weight: bold;}
thead.fixedHeader2 a, thead.fixedHeader2 a:link, thead.fixedHeader2 a:visited {color: #FFF;display: block;text-decoration: none;width: 100%}
thead.fixedHeader2 a:hover {color: #FFF;	display: block;	text-decoration: underline;width: 100%}
head:first-child+body tbody[class].scrollContent2 {display: block;height: 425px;overflow: auto;width: 100%}
tbody.scrollContent2 td, tbody.scrollContent2 tr.normalRow td {border-bottom: none;border-left: none;border-right: none;border-top: none;padding: 0px 1px 1px 1px; vertical-align: top; }
tbody.scrollContent2 a, tbody.scrollContent2 a:link, tbody.scrollContent2 a:visited {color: #FFF;text-decoration: none}
tbody.scrollContent2 a:hover {color: #FFF;text-decoration: underline}
head:first-child+body thead[class].fixedHeader2 th {width: 200px}
head:first-child+body thead[class].fixedHeader2 th + th {width: 240px}
head:first-child+body thead[class].fixedHeader2 th + th + th {border-right: none;padding: 4px 4px 4px 3px;width: 316px}
head:first-child+body tbody[class].scrollContent2 td {width: 200px}
head:first-child+body tbody[class].scrollContent2 td + td {width: 240px}
head:first-child+body tbody[class].scrollContent2 td + td + td {border-right: none;padding: 2px 4px 2px 3px;width: 300px}

div.tableContainer3, div.tableContainer4 {clear: both;border: 1px solid #963;	height: 450px;	overflow: auto;	width: 100%;}
div.tableContainer3 table, div.tableContainer4 table {float: left;width: 100%}
div.tableContainer3 table, div.tableContainer4 table {margin: 0 -16px 0 0}
thead.fixedHeader3 tr {position: relative;top: expression(document.getElementById("tableContainer3").scrollTop);}
thead.fixedHeader4 tr {position: relative;top: expression(document.getElementById("tableContainer4").scrollTop);}
head:first-child+body thead[class].fixedHeader3 tr, head:first-child+body thead[class].fixedHeader4 tr {display: block;}
thead.fixedHeader3 th, thead.fixedHeader4 th {background: #C96;border-left: 1px solid #EB8;border-right: 1px solid #B74;border-top: 1px solid #EB8;font-weight: normal;	padding: 4px 3px;text-align: left; font-weight: bold;}
thead.fixedHeader3 a, thead.fixedHeader3 a:link, thead.fixedHeader3 a:visited, thead.fixedHeader4 a, thead.fixedHeader4 a:link, thead.fixedHeader4 a:visited {color: #FFF;display: block;text-decoration: none;width: 100%}
thead.fixedHeader3 a:hover, thead.fixedHeader4 a:hover {color: #FFF;	display: block;	text-decoration: underline;width: 100%}
head:first-child+body tbody[class].scrollContent3, head:first-child+body tbody[class].scrollContent4 {display: block;height: 425px;overflow: auto;width: 100%}
tbody.scrollContent3 td, tbody.scrollContent3 tr.normalRow td, tbody.scrollContent4 td, tbody.scrollContent4 tr.normalRow td {border-bottom: none;border-left: none;border-right: none;border-top: none;padding: 0px 1px 1px 1px; vertical-align: top; }
tbody.scrollContent3 a, tbody.scrollContent3 a:link, tbody.scrollContent3 a:visited, tbody.scrollContent4 a, tbody.scrollContent4 a:link, tbody.scrollContent4 a:visited {color: #FFF;text-decoration: none}
tbody.scrollContent3 a:hover, tbody.scrollContent4 a:hover {color: #FFF;text-decoration: underline}
head:first-child+body thead[class].fixedHeader3 th, head:first-child+body thead[class].fixedHeader4 th {width: 200px}
head:first-child+body thead[class].fixedHeader3 th + th, head:first-child+body thead[class].fixedHeader4 th + th {width: 240px}
head:first-child+body thead[class].fixedHeader3 th + th + th, head:first-child+body thead[class].fixedHeader4 th + th + th {border-right: none;padding: 4px 4px 4px 3px;width: 316px}
head:first-child+body tbody[class].scrollContent3 td, head:first-child+body tbody[class].scrollContent4 td {width: 200px}
head:first-child+body tbody[class].scrollContent3 td + td, head:first-child+body tbody[class].scrollContent4 td + td {width: 240px}
head:first-child+body tbody[class].scrollContent3 td + td + td, head:first-child+body tbody[class].scrollContent4 td + td + td {border-right: none;padding: 2px 4px 2px 3px;width: 300px}

#tblCharges td, th { padding: 0.25em; font-size:100%; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }
#tblCredits td, th { padding: 0.25em; font-size:100%; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }
#tblPrevious td, th { padding: 0.25em; font-size:100%; }
#tblPrevious td { text-align: right; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }

</style>



<link rel="stylesheet" type="text/css" href="../css/checkout.css" />

<!-- <link rel="stylesheet" type="text/css" href="../yui/build/reset-fonts-grids/reset-fonts-grids.css" /> -->
<link rel="stylesheet" type="text/css" href="../yui/build/resize/assets/skins/sam/resize.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/layout/assets/skins/sam/layout.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" />


<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/menu/assets/skins/sam/menu.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/calendar/assets/skins/sam/calendar.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/tabview/assets/skins/sam/tabview.css" />






<script type="text/javascript" src="../yui/build/yahoo/yahoo-min.js"></script>
<script type="text/javascript" src="../yui/build/event/event-min.js"></script>
<script type="text/javascript" src="../yui/build/dom/dom-min.js"></script>

<script type="text/javascript" src="../yui/build/element/element-beta-min.js"></script>
<script type="text/javascript" src="../yui/build/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="../yui/build/resize/resize-beta-min.js"></script>
<script type="text/javascript" src="../yui/build/animation/animation-min.js"></script>
<script type="text/javascript" src="../yui/build/layout/layout-beta-min.js"></script>


<script type="text/javascript" src="../yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../yui/build/container/container_core.js"></script>
<script type="text/javascript" src="../yui/build/menu/menu.js"></script>
<script type="text/javascript" src="../yui/build/element/element-beta.js"></script>
<script type="text/javascript" src="../yui/build/button/button-beta.js"></script>

<script type="text/javascript" src="../yui/build/utilities/utilities.js"></script>
<script type="text/javascript" src="../yui/build/container/container.js"></script>
<script type="text/javascript" src="../yui/build/calendar/calendar.js"></script>
<script type="text/javascript" src="../yui/build/tabview/tabview.js"></script>




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
#toggle {
    text-align: center;
    padding: 1em;
}
#toggle a {
    padding: 0 5px;
    border-left: 1px solid black;
}
#tRight {
    border-left: none !important;
}
</style>



<script type="text/javascript">
    
    var typeArray = new Array();
    var practitionerArray = new Array();
    var codeArray = new Array();
    
<%
Iterator type_itr = AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
while (type_itr.hasNext())
{
    AppointmentTypeBean appointmentType = (AppointmentTypeBean)type_itr.next();
    String palabel = "";
    if (appointmentType.getPracticeAreaId() > 0)
	palabel = appointmentType.getPracticeArea().getLabel();
%>
    typeArray["<%= appointmentType.getId() %>"] = {"type" : "<%= appointmentType.getType() %>", "palabel" : "<%= palabel %>", "label" : "<%= appointmentType.getLabel() %>", "bg" : "<%= appointmentType.getBGColorCodeString() %>", "txt" : "<%= appointmentType.getTextColorCodeString() %>", "duration" : "<%= appointmentType.getDurationString() %>" };
<%
}
Iterator practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
%>
	practitionerArray["<%= practitioner.getId() %>"] = {"label" : "<%= practitioner.getLabel() %>", "pa_id" : "<%= practitioner.getPracticeAreasIdString() %>"};
<%
}
%>
		
        var aMenuButton4Menu = [
<%
Iterator itr = practice_areas.iterator();
if (itr.hasNext())
{
%>
            { text: "All Practice Areas", value: 0, onclick: { fn: onMenuItemClick } },
<%
    while (itr.hasNext())
    {
	PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
%>
            { text: "<%= practice_area.getLabel() %>", value: <%= practice_area.getValue() %>, onclick: { fn: onMenuItemClick } }<%= itr.hasNext() ? "," : "" %>
<%
    }
}
%>
        ];
    
    var httpRequest;
    var xml_response;
    
    var pageDateStr = "<%= display_date.get(Calendar.MONTH) + 1 %>/<%= display_date.get(Calendar.YEAR) %>";
    var paLabel = '<%= adminPracticeArea.isNew() ? "All Practice Areas" : adminPracticeArea.getLabel() %>';
    
    var selected_date;
    
    var empty_table;
    
    var show_appts = true;
    var show_off_time = true;
    var appt_type_type = "<%= AppointmentTypeBean.CLIENT_APPOINTMENT_TYPE_TYPE %>";
    var appt_meeting_type = "<%= AppointmentTypeBean.MEETING_APPOINTMENT_TYPE_TYPE %>";

    var show_referral_names = false;
    var show_group_names = false;
    var show_ci_names = false;
    var show_co_names = false;

    var can_block = false;
    var disconnected = false;

    function processCommand(command, parameter)
    {
	if (!empty_table)
		empty_table = document.getElementById("schedule").cloneNode(true);
	    
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
		can_block = true;
		disconnected = false;

		//alert(httpRequest.responseXML);
		if ((httpRequest.responseXML.getElementsByTagName("refresh").length > 0) || (httpRequest.responseXML.getElementsByTagName("status").length > 0))
		{
		    var refresh = false;
		    if (httpRequest.responseXML.getElementsByTagName("refresh").length > 0)
		    {
			    refresh = true;
			    xml_response = httpRequest.responseXML.getElementsByTagName("refresh")[0];
		    }
		    else
			    xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];

		    buildScheduleArray(xml_response.getElementsByTagName("schedule")[0],refresh);

		    if (xml_response.getElementsByTagName("date"))
		    {
			selected_date = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
			document.getElementById('today_date').firstChild.nodeValue = selected_date;

			document.getElementById('ap_x1').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].getAttribute("tot");
			document.getElementById('co_x1').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].getAttribute("co");
			document.getElementById('cn_x1').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].getAttribute("cn");
			document.getElementById('r_x1').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].getAttribute("rs");
			document.getElementById('ci_x1').firstChild.nodeValue = xml_response.getElementsByTagName("c-in")[0].getAttribute("num");
			document.getElementById('lt_x1').firstChild.nodeValue = xml_response.getElementsByTagName("late")[0].getAttribute("num");
			document.getElementById('ns_x1').firstChild.nodeValue = xml_response.getElementsByTagName("no-show")[0].getAttribute("num");

			showAppointments(document.getElementById('checkedInSelect'), xml_response.getElementsByTagName("c-in")[0]);
			showAppointments(document.getElementById('lateSelect'), xml_response.getElementsByTagName("late")[0]);
			showAppointments(document.getElementById('noShowSelect'), xml_response.getElementsByTagName("no-show")[0]);
		    }

		    block = false;
		}
		else if (httpRequest.responseXML.getElementsByTagName("checkout").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("checkout")[0];
		    showCheckout(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
		    showPeople(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("black_hole").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("black_hole")[0];
		    showBlackHole(document.getElementById('blackHoleSelect'), xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("client_review").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("client_review")[0];
		    showBlackHole(document.getElementById('clientReviewList'), xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
		    showPerson(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("checkout_sheet_url").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("checkout_sheet_url")[0];
		    window.open(xml_response.childNodes[0].nodeValue,"Checkout_Sheet","width=800,height=600,toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,copyhistory=no,resizable=yes");
		}
		else if (httpRequest.responseXML.getElementsByTagName("appt_report_url").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("appt_report_url")[0];
		    window.open(xml_response.childNodes[0].nodeValue,"Client_Appointments","width=800,height=600,toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,copyhistory=no,resizable=yes");
		}
		else if (httpRequest.responseXML.getElementsByTagName("nfn").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("nfn")[0];
		    document.getElementById('filenumberx').value = xml_response.getAttribute("num");
		}
		else if (httpRequest.responseXML.getElementsByTagName("note").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("note")[0];
		    showNote(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("soap").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("soap")[0];
		    showSoap(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("statements").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("statements")[0];
		    showStatements(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("error").length > 0)
		{
		    xml_response = httpRequest.responseXML.getElementsByTagName("error")[0];
		    alert("Error : " + xml_response.childNodes[0].nodeValue);
		}
	    }
	    else
	    {
		//alert(".Error : " + httpRequest.status + " : " + httpRequest.statusText);
		document.getElementById('today_date').firstChild.nodeValue = 'Disconnected.';
		disconnected = true;
		block = false;
	    }
	}
    }
    
    function selectType()
    {
        document.forms[2].codeSelect.options.length = 0;
	var index = 0;
<%
itr = codes.iterator();
while (itr.hasNext())
{
    CheckoutCodeBean code = (CheckoutCodeBean)itr.next();
%>
        if (document.forms[2].typeSelect.value == <%= code.getType() %>)
            document.forms[2].codeSelect.options[index++] = new Option('<%= code.getLabel() %>','<%= code.getValue() %>');
<%
}
%>
    }

    function showApptTypes(p_id)
    {
        document.forms['newApptForm'].typeSelect.options.length = 0;
        var index = 0;
		var appt_pa_id = 0;
		var pract_pa_id = practitionerArray[p_id]["pa_id"];
<%
type_itr = AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
while (type_itr.hasNext())
{
    AppointmentTypeBean appointmentType = (AppointmentTypeBean)type_itr.next();
    if (appointmentType.isActive() && !appointmentType.getLabel().equals(AppointmentTypeBean.PRACTITIONER_OFFTIME_APPOINTMENT_TYPE_NAME))
    {
%>
		appt_pa_id = <%= appointmentType.getPracticeAreaId() %>;
		if ((appt_pa_id == 0) || (pract_pa_id.indexOf("|" + appt_pa_id + "|") > -1))
		{
			document.forms['newApptForm'].typeSelect.options[index] = new Option('<%= appointmentType.getLabel() %>','<%= appointmentType.getId() %>');
			index++;
		}
<%
    }
}
%>
    }

    function onMenuItemClick(p_sType, p_aArgs, p_oItem) {
	//alert(this.value);
	document.location.href='schedule.jsp?practice_area_id=' + this.value;
	
	//oMenuButton4.set("label", p_oItem.cfg.getProperty("text"));
    }
    
    function processGotoDay(sel_date)
    {
	block = true;
	processCommand('gotoDay', sel_date);
    }
    
</script>

<script type="text/javascript" src="../scripts/schedule.js"></script>

<script>

(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
                minWidth: 750,
                units: [
		{ position: 'top', height: 21, resize: false, body: 'top1' },
                { position: 'right', header: 'Today\'s Clients', width: 230, resize: true, gutter: '0px', collapse: true, scroll: true, body: 'right1', animate: true, duration: .1 },
		{ position: 'left', header: 'Stats & Calendar', width: 213, resize: false, body: 'left1', gutter: '0px', collapse: true, close: false, scroll: true, animate: true, duration: .1 },
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
        });
	layout.on('render', function() {
		layout.getUnitByPosition('left').on('collapse', function() {
			doWidthStuff();
			processCommand('collapseLeft');
		});
		layout.getUnitByPosition('left').on('expand', function() {
			doWidthStuff();
			processCommand('expandLeft');
		});
		layout.getUnitByPosition('right').on('collapse', function() {
			doWidthStuff();
			processCommand('collapseRight');
		});
		layout.getUnitByPosition('right').on('expand', function() {
			doWidthStuff();
			processCommand('expandRight');
		});
	});
        layout.render();
	<%= left_expanded ? "" : "layout.getUnitByPosition('left').collapse();" %>
	<%= right_expanded ? "" : "layout.getUnitByPosition('right').collapse();" %>
	
    });


})();
</script>

<style>
#example {height:30em;}
label { display:block;float:left;width:45%;clear:left; }
.clear { clear:both; }
#resp { margin:10px;padding:5px;border:1px solid #ccc;background:#fff;}
#resp li { font-family:monospace }
</style>

<style type="text/css">
	#cal2Container { display:none; position:absolute; left:70px; top:0px; z-index:2}
	#cal3Container { display:none; position:absolute; left:260px; top:5px; z-index:2}
	#cal4Container { display:none; position:absolute; left:240px; top:175px; z-index:2}
</style>

<style type="text/css">

    #button-example-form fieldset {

        border: 2px groove #ccc;
        margin: .5em;
        padding: .5em;

    }

    #menubutton3menu,
    #menubutton5menu {
    
        position: absolute;
        visibility: hidden;
        border: solid 1px #000;
        padding: .5em;
        background-color: #ccc;
    
    }

    #button-example-form-postdata {
    
        border: dashed 1px #666;
        background-color: #ccc;
        padding: 1em;
    
    }

    #button-example-form-postdata h2 {
    
        margin: 0 0 .5em 0;
        padding: 0;
        border: none;
    
    }

</style>

<style type="text/css">
	tbody.scrollContent tr.mainhigh {
	  background-color: #ffffbb;
	}
</style>

<!--there is no custom header content for this example-->

</head>

<body class="yui-skin-sam" >

<!-- <h1>Valeo Schedule</h1> -->



<div id="content" style="height: 100%;">

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>

<div id="center1" style="height: 100%;">
<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

<div id="tableContainer" class="tableContainer" style="height: 100%;">
    <table id="schedule" border="0" cellpadding="0" cellspacing="0" width="100%" class="scrollTable" style="height: 100%;">
        <thead class="fixedHeader" id="fixedHeader" style="height: 100%;">
            <tr>
		<th style="width:8%;" ><a href="#"><img onclick="processCommand('previousDay');" style="margin-left:8px;margin-right:5px;" src="../images/blue_arrow_left.gif" border="0" alt="" /><img onclick="processCommand('nextDay');" src="../images/blue_arrow_right.gif" border="0" alt="" /></a></th>
<%
practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
%>
                <th><a href="practitioners.jsp?id=<%= practitioner.getId() %>"><%= practitioner.getLabel() %></a></th>
<%
}
%>
            </tr>
        </thead>
        <tbody class="scrollContent" id="scrollContent" style="height: 100%;">
<%
open_time.set(Calendar.HOUR_OF_DAY, start_hour_of_day);
open_time.set(Calendar.MINUTE, start_minute);

for (int i = 0; open_time.compareTo(closing_time) < 0; i++)
{
    String time_str = time_date_format.format(open_time.getTime());
%>
            <tr onmouseover="this.className='highlightRow';" onmouseout="this.className='normalRow';">
                <td style="width:8%;" align="right" id="<%= i %>"><nobr><%= time_str %></nobr></td>
<%
    practitioner_itr = practitioners.iterator();
    while (practitioner_itr.hasNext())
    {
		UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
		String key = time_str + "|" + practitioner.getId();
%>
                <td onmouseover="cOvr('<%= key %>');" onclick="cOvr('<%= key %>');cClk('<%= practitioner.getId() %>','<%= time_str %>');" id="<%= key %>">&nbsp;</td>
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
</div>

<div id="right1">
   
        <div id="demo" class="yui-navset">
            <ul class="yui-nav">
                <li class="selected"><a href="#tab1"><em>Checked In</em></a></li>
                <li><a href="#tab2"><em>Late</em></a></li>
                <li><a href="#tab3"><em>No Show</em></a></li>
                <li><a href="#tab4" onclick="processCommand('blackHole',document.getElementById('bhPaFilter').value);"><em>Contact List</em></a></li>
            </ul>            
            <div class="yui-content">
                <div id="tab1">
                    <!-- <p>Tab One Content</p> -->
                    <select name="checkedInSelect" id="checkedInSelect" size="2" style="height:500px;width:100%;"  onclick="showClientInfo(this.value);">
                    </select>
                </div>
                <div id="tab2">
                    <!-- <p>Tab Two Content</p> -->
                    <select name="lateSelect" id="lateSelect" size="2" style="height:500px;width:100%;"  onclick="showClientInfo(this.value);">
                    </select>
                </div>
                <div id="tab3">
                    <!-- <p>Tab Three Content</p> -->
                    <select name="noShowSelect" id="noShowSelect" size="2" style="height:500px;width:100%;"  onclick="showClientInfo(this.value);">
                    </select>
                </div>
                <div id="tab4">
						<label for="bhPaFilter"><strong>Practice Area:</strong></label>
						<select id="bhPaFilter" name="bhPaFilter" onchange="processCommand('blackHole',this.value);">
							<option value="0">-- ALL --</option>
<%
itr = practice_areas.iterator();
while (itr.hasNext())
{
	PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
%>
							<option value="<%= practice_area.getValue() %>"><%= practice_area.getLabel() %></option>
<%
}
%>
						</select>
                    <select name="blackHoleSelect" id="blackHoleSelect" size="2" style="height:500px;width:100%;" onchange="showClientInfo(this.value);">
                    </select>
                </div>
                <div id="tab5">
					<label for="dcPaFilter"><strong>Practice Area:</strong></label>
					<select id="dcPaFilter" name="dcPaFilter">
						<option value="0">-- ALL --</option>
<%
itr = practice_areas.iterator();
while (itr.hasNext())
{
	PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
%>
						<option value="<%= practice_area.getValue() %>"><%= practice_area.getLabel() %></option>
<%
}
%>
					</select>
                    <!-- <p>Tab One Content</p> -->
                    <select name="doctorCallList" id="doctorCallList" size="2" style="height:500px;width:100%;" onclick="showClientInfo(this.value);">
                    </select>
                </div>
                <div id="tab6">
					<label for="crPaFilter"><strong>Reason:</strong></label>
					<select id="crPaFilter" name="crPaFilter">
						<option value="0">-- SELECT A REASON --</option>
					</select>
                    <select name="clientReviewList" id="clientReviewList" size="2" style="height:500px;width:100%;" onclick="showClientInfo(this.value);">
                    </select>
                </div>
            </div>
	    
        </div>
	
</div>

<div id="left1">
	
	<h2 style="padding:3px;margin:0;">Valeo</h2>
	<p style="padding:3px;margin:0;font-size:120%;"><strong><span id="today_date"><%= date_format.format(display_date.getTime())%></span></strong></p>
	<br />
	<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
	<table cellpadding="1" cellspacing="1" border="0">
	    <tr><td align="right"><strong>Scheduled:</strong></td><td align="right"><span id="ap_x1">0</span></td><td align="right"></td><td align="right"></td></tr>
	    <tr><td align="right"><strong>Checked In:</strong></td><td align="right"><span id="ci_x1">0</span></td><td align="right"><strong>Cancelled:</strong></td><td align="right"><span id="cn_x1">0</span></td></tr>
	    <tr><td align="right"><strong>Checked Out:</strong></td><td align="right"><span id="co_x1">0</span></td><td align="right"><strong>Late:</strong></td><td align="right"><span id="lt_x1">0</span></td></tr>
	    <tr><td align="right"><strong>Rescheduled:</strong></td><td align="right"><span id="r_x1">0</span></td><td align="right"><strong>No Show:</strong></td><td align="right"><span id="ns_x1">0</span></td></tr>
	</table>

	<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div><br />
	<input type="button" id="b1a" name="b1a" value="New Client">
	<input type="button" id="ci_button" name="ci_button" value="Client Info">
	<input type="button" id="menubutton2" name="menubutton2_button" value="Practice Area">
	<br />
	<br />
		    
	<div id="cal1Container"></div>
</div>

<%@ include file="channels/channel-appointment.jsp" %>
<%@ include file="channels/channel-client-info.jsp" %>
<%@ include file="channels/channel-checkout.jsp" %>
<%@ include file="channels/channel-new-client.jsp" %>

<div id="basicmenu" class="yuimenu">
    <div class="bd">
        <ul class="first-of-type">
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:showClientInfo(editC2);">
                    Client Info...
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:changeState('checkin');">
                    Check In
                </a>
            </li>
<%
if (!adminCompany.getLabel().equals("Valeo"))
{
%>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:changeState('checkout');">
                    Check Out
                </a>
            </li>
<%
}
%>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:cClk();">
                    Edit / Reschedule
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:changeState('cancel');">
                    Cancel Appointment
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:changeState('clear');">
                    Clear Status
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:cut();">
                    Cut
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:paste();">
                    Paste
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:remove();">
                    Delete
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:appointmentReport();">
                    Appointment Report
                </a>
            </li>
        </ul>
    </div>
</div>



</div>

</body>

</html>
