<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPracticeArea" class="com.badiyan.uk.online.beans.PracticeAreaBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%
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
		background: #fc0;
	}
	
	#content {
		border-top: 2px solid white;
		background: #fc0;
		padding: 1em;
	}
	
	#content p {
		margin: 0;
		padding: 0em;
		background: #fc0;
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
div.tableContainer {clear: both;	height: 100%;	overflow: auto;	width: 100%;}
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


<!-- <link rel="stylesheet" type="text/css" href="../yui/build/resize/assets/skins/sam/resize.css" /> -->
<!-- <link rel="stylesheet" type="text/css" href="../yui/build/layout/assets/skins/sam/layout.css" /> -->
<!-- <link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" /> -->


<!-- <link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" /> -->
<!-- <link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" /> -->
<!-- <link rel="stylesheet" type="text/css" href="../yui/build/menu/assets/skins/sam/menu.css" /> -->
<!-- <link rel="stylesheet" type="text/css" href="../yui/build/calendar/assets/skins/sam/calendar.css" /> -->
<!-- <link rel="stylesheet" type="text/css" href="../yui/build/tabview/assets/skins/sam/tabview.css" /> -->




<script src="http://yui.yahooapis.com/2.5.2/build/yuiloader/yuiloader-beta-min.js"></script>

<!-- <script type="text/javascript" src="../yui/build/yahoo/yahoo-min.js"></script> -->
<script type="text/javascript" src="../yui/build/event/event-min.js"></script>
<!-- <script type="text/javascript" src="../yui/build/dom/dom-min.js"></script> -->

<!-- <script type="text/javascript" src="../yui/build/element/element-beta-min.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/dragdrop/dragdrop-min.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/resize/resize-beta-min.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/animation/animation-min.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/layout/layout-beta-min.js"></script> -->


<!-- <script type="text/javascript" src="../yui/build/yahoo-dom-event/yahoo-dom-event.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/container/container_core.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/menu/menu.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/element/element-beta.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/button/button-beta.js"></script> -->

<!-- <script type="text/javascript" src="../yui/build/utilities/utilities.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/container/container.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/calendar/calendar.js"></script> -->
<!-- <script type="text/javascript" src="../yui/build/tabview/tabview.js"></script> -->




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
%>
    typeArray["<%= appointmentType.getId() %>"] = {"type" : "<%= appointmentType.getType() %>", "bg" : "<%= appointmentType.getBGColorCodeString() %>", "txt" : "<%= appointmentType.getTextColorCodeString() %>", "duration" : "<%= appointmentType.getDurationString() %>" };
<%
}
Iterator practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
%>
    practitionerArray["<%= practitioner.getId() %>"] = {"label" : "<%= practitioner.getLabel() %>"};
<%
}
Iterator itr = codes.iterator();
while (itr.hasNext())
{
    CheckoutCodeBean code = (CheckoutCodeBean)itr.next();
%>
    codeArray["<%= code.getId() %>"] = {"id" : "<%= code.getId() %>", "code" : "<%= code.getCode() %>", "desc" : "<%= code.getDescription() %>", "label" : "<%= code.getLabel() %>", "practice_area" : "<%= code.getPracticeAreaId() %>", "amount" : "<%= code.getAmountString() %>" };
<%
}
%>
		
        var aMenuButton4Menu = [
<%
itr = practice_areas.iterator();
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
    
    var show_group_names = false;

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

		    selected_date = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
                    document.getElementById('today_date').firstChild.nodeValue = selected_date;

                    buildScheduleArray(xml_response.getElementsByTagName("schedule")[0],refresh);
		    
		    document.getElementById('ap_x1').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].getAttribute("tot");
		    document.getElementById('co_x1').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].getAttribute("co");
		    document.getElementById('cn_x1').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].getAttribute("cn");
		    document.getElementById('r_x1').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].getAttribute("rs");
		    document.getElementById('ci_x1').firstChild.nodeValue = xml_response.getElementsByTagName("c-in")[0].getAttribute("num");
		    document.getElementById('lt_x1').firstChild.nodeValue = xml_response.getElementsByTagName("late")[0].getAttribute("num");
		    document.getElementById('ns_x1').firstChild.nodeValue = xml_response.getElementsByTagName("no-show")[0].getAttribute("num");
                    
                    //showAppointments(document.getElementById('checkedInSelect'), xml_response.getElementsByTagName("c-in")[0]);
                    //showAppointments(document.getElementById('lateSelect'), xml_response.getElementsByTagName("late")[0]);
                    //showAppointments(document.getElementById('noShowSelect'), xml_response.getElementsByTagName("no-show")[0]);
		    
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
                else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
                {
                    xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
                    showPerson(xml_response);
                }
                else if (httpRequest.responseXML.getElementsByTagName("error").length > 0)
                {
                    xml_response = httpRequest.responseXML.getElementsByTagName("error")[0];
                    alert("Error : " + xml_response.childNodes[0].nodeValue);
                }
            }
            else
            {
                alert("Error : " + httpRequest.status + " : " + httpRequest.statusText);
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

<script type="text/javascript" src="../scripts/schedule-loader.js"></script>

<script>


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



<div id="content">

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>

<div id="center1" style="height: 100%;">
<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

<div id="tableContainer" class="tableContainer">
    <table id="schedule" border="0" cellpadding="0" cellspacing="0" width="100%" class="scrollTable">
        <thead class="fixedHeader" id="fixedHeader">
            <tr>
		<th style="width:55px;" ><a href="#"><img onclick="processCommand('previousDay');" style="margin-left:8px;margin-right:5px;" src="../images/blue_arrow_left.gif" border="0" alt="" /><img onclick="processCommand('nextDay');" src="../images/blue_arrow_right.gif" border="0" alt="" /></a></th>
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
        <tbody class="scrollContent" id="scrollContent">
<%
open_time.set(Calendar.HOUR_OF_DAY, start_hour_of_day);
open_time.set(Calendar.MINUTE, start_minute);

for (int i = 0; open_time.compareTo(closing_time) < 0; i++)
{
    String time_str = time_date_format.format(open_time.getTime());
%>
            <tr onmouseover="this.className='highlightRow';" onmouseout="this.className='normalRow';">
                <td align="right" id="<%= i %>"><nobr><%= time_str %></nobr></td>
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
   
   <!--
        <div id="demo" class="yui-navset">
            <ul class="yui-nav">
                <li class="selected"><a href="#tab1"><em>Checked In</em></a></li>
                <li><a href="#tab2"><em>Late</em></a></li>
                <li><a href="#tab3"><em>No Show</em></a></li>
                <li><a href="#tab4"><em>Black Hole</em></a></li>
            </ul>            
            <div class="yui-content">
                <div id="tab1">
                    <p>Tab One Content</p>
                    <select name="checkedInSelect" id="checkedInSelect" size="2" style="height:500px;width:100%;"  onclick="showClientInfo(this.value);">
                    </select>
                </div>
                <div id="tab2">
                    <p>Tab Two Content</p>
                    <select name="lateSelect" id="lateSelect" size="2" style="height:500px;width:100%;"  onclick="showClientInfo(this.value);">
                    </select>
                </div>
                <div id="tab3">
                    <p>Tab Three Content</p>
                    <select name="noShowSelect" id="noShowSelect" size="2" style="height:500px;width:100%;"  onclick="showClientInfo(this.value);">
                    </select>
                </div>
                <div id="tab4">
                    <p>Tab Four Content</p>
                    <select name="blackHoleSelect" id="blackHoleSelect" size="2" style="height:500px;width:100%;"  onclick="showClientInfo(this.value);">
                    </select>
                </div>
            </div>
	    
        </div>
	-->
</div>

<div id="left1">
	
	<h2 style="padding:3px;margin:0;">Valeo</h2>
	<p style="padding:3px;margin:0;"><strong><span id="today_date"><%= date_format.format(display_date.getTime())%></span></strong></p>
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
	<input type="button" id="menubutton2" name="menubutton2_button" value="Practice Area">
	<br />
	<br />
		    
	<div id="cal1Container"></div>
</div>

<%@ include file="channels/channel-appointment.jsp" %>

<div id="dialog2">
    <div class="hd"><h2><span id="clientInfoFormLabel">&nbsp;</span></h2></div>
<div class="bd" id="bd-r">
	<form name="clientInfoForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/clientInfo.x"  >

	<input type="hidden" name="cid" value="-1">
	
	<div>
	    <div id="explore" style="width: 50%;">
		<h2>Appointments</h2>
		<p><strong>Next Appt:</strong>&nbsp;<span id="clientInfoFormNext">&nbsp;</span></p>
		<div class="clear"></div>
		<p><strong>Last Appt:</strong>&nbsp;<span id="clientInfoFormLast">&nbsp;</span></p>
		<div class="clear"></div>
		<p><strong>Balance:</strong>&nbsp;<span id="clientInfoFormBalance">&nbsp;</span></p>
		<div class="clear"></div>
	    </div>
	    <div id="stats" style="width: 50%;">
		<h2>Contact</h2>
		<p><strong>Address:</strong>&nbsp;<span id="clientInfoFormAddr">&nbsp;</span></p>
		<div class="clear"></div>
		<p><strong>Phone:</strong>&nbsp;<span id="clientInfoFormPhone">&nbsp;</span></p>
		<div class="clear"></div>
		<p><strong>Email:</strong>&nbsp;<span id="clientInfoFormEmail">&nbsp;</span></p>
		<div class="clear"></div>
	    </div>
	    <div>
		<p>
		    <input type="radio" name="statusInput" value="0" checked>Active
		    <input type="radio" name="statusInput" value="1">Black Hole
		    <input type="radio" name="statusInput" value="2">Inactive
		</p><p><br /></p>
	    </div>
	</div>
	<div id="cal2Container"></div>
        <div id="demo2" class="yui-navset">
            <ul class="yui-nav">
                <li class="selected"><a href="#tab1"><em>Contact Status</em></a></li>
                <li><a href="#tab2"><em>To Do</em></a></li>
                <li><a href="#tab2"><em>Care Details</em></a></li>
            </ul>            
            <div class="yui-content">
                <div id="tab1">
		    <div id="explore" style="width: 50%;">
			<input type="radio" name="contactInput" value="0" onclick="document.forms[1].otherInput.disabled = true;">Scheduled New Appointment<br />
			<input type="radio" name="contactInput" value="1" disabled onclick="document.forms[1].otherInput.disabled = true;">Confirmed Next Appointment<br />
			<input type="radio" name="contactInput" value="2" onclick="document.forms[1].otherInput.disabled = true;">No Answer / Busy<br />
			<input type="radio" name="contactInput" value="3" onclick="document.forms[1].otherInput.disabled = true;">Email<br />
			<input type="radio" name="contactInput" value="4" onclick="document.forms[1].otherInput.disabled = true;">Left without future Appointment<br />
		    </div>
		    <div id="stats" style="width: 50%;">
			<input type="radio" name="contactInput" value="5" onclick="document.forms[1].otherInput.disabled = true;">Left Voice Mail<br />
			<input type="radio" name="contactInput" value="6" onclick="document.forms[1].otherInput.disabled = true;">Left Message with Family Member<br />
			<input type="radio" name="contactInput" value="7" onclick="document.forms[1].otherInput.disabled = true;">Requested Call Back<br />
			<input type="radio" name="contactInput" value="8" onclick="document.forms[1].otherInput.disabled = !this.checked;">Other<br />
			<input type="text" name="otherInput" disabled>
		    </div>
		    <div><input type="button" id="sButton1a" name="sButton1a" value="Add Contact Status">
			<select name="eventList" size="5" style="width: 100%;">
			</select><input type="button" id="sButton1d" name="sButton1d" value="Delete Contact Status">
		    </div>
                </div>
                <div id="tab2">
                    <div id="explore" style="width: 50%;">
			<input type="radio" name="todoInput" value="0">Contact On&nbsp;<input type="text" name="callOnInput" onkeyup="javascript:document.forms[1].todoInput[0].checked = true;"><input type="submit" name="calendar" value="..." onclick="YAHOO.example.calendar.cal2.show();return false;">
		    </div>
		    <div id="stats" style="width: 50%;">
			<input type="radio" name="todoInput" value="1">Contact In
			<select name="eList1" onchange="javascript:document.forms[1].todoInput[1].checked = true;">
			    <option value="1">1
			    <option value="2">2
			    <option value="3">3
			    <option value="4">4
			    <option value="5">5
			    <option value="6">6
			    <option value="15">15
			    <option value="30">30
			    <option value="45">45
			</select>
			<select name="eList2" onchange="javascript:document.forms[1].todoInput[1].checked = true;">
			    <option value="1">Days
			    <option value="2">Weeks
			    <option value="3">Months
			    <option value="4">Years
			</select>
		    </div>
		    <div><input type="button" id="sButton2a" name="sButton2a" value="Add To Do">
			<select name="toDoList" size="5" style="width: 100%;">
			</select><input type="button" id="sButton2d" name="sButton2d" value="Delete To Do">
		    </div>
                </div>
                <div id="tab3">
		    <div>
			<strong>Practice Area</strong>:&nbsp;
			<select name="paList">
			    <option value="0">-- SELECT A PRACTICE AREA --
<%
itr = practice_areas.iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
	PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
%>
			    <option value="<%= practice_area.getValue() %>"><%= practice_area.getLabel() %>
<%
    }
}
else
{
%>

<%
}
%>
			</select><br />
			<strong>Preferred Practitioner</strong>:&nbsp;
			<select name="ppList">
			    <option value="0">-- SELECT A PRACTITIONER --
<%
practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
%>
			    <option value="<%= practitioner.getValue() %>"><%= practitioner.getLabel() %>
<%
}
%>
			</select><br />
			<strong>Frequency of Care</strong>:&nbsp;
			<select name="fList1" >
			    <option value="1">1
			    <option value="2">2
			    <option value="3">3
			    <option value="4">4
			    <option value="5">5
			    <option value="6">6
			    <option value="7">7
			    <option value="8">8
			    <option value="9">9
			</select>
			<strong>time(s) per</strong>
			<select name="fList2" >
			    <option value="1">Day
			    <option value="2">Week
			    <option value="3">Month
			    <option value="4">Year
			</select>
		    </div>
		    <div><input type="button" id="sButton3a" name="sButton3a" value="Add Frequency of Care Details">
			<select name="foqList" size="5" style="width: 100%;">
			</select><input type="button" id="sButton3d" name="sButton3d" value="Delete Frequency of Care Details">
		    </div>
                </div>
            </div>
        </div>

</form>
</div>
</div>

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
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:checkout();">
                    Check Out...
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:changeState('reschedule');">
                    Reschedule
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
                <a class="yuimenuitemlabel" href="javascript:alert('Functionality not available.');">
                    Appointment Report
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:alert('Functionality not available.');">
                    Appointment Calendar
                </a>
            </li>
        </ul>
    </div>
</div>



</div>

</body>

</html>
