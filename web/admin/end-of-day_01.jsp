<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.exceptions.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />


<%
try
{

session.setAttribute("eod_01", "true");


CompanySettingsBean settings = adminCompany.getSettings();


// grab the workstation name / primary setting from the cookie...

String workstation_cookie_name = "valeo.workstation_name";
String is_primary_cookie_name = "valeo.workstation_primary";

Cookie[] cookies = request.getCookies();
Cookie workstation_cookie = null;
Cookie primary_cookie = null;
if (cookies != null)
{
	for (int i = 0; i < cookies.length; i++)
	{
		Cookie cookie = cookies[i];
		if (cookie.getName().equals(workstation_cookie_name))
			workstation_cookie = cookie;
		else if (cookie.getName().equals(is_primary_cookie_name))
			primary_cookie = cookie;
	}
}

if (workstation_cookie == null)
{
	UUID generated_uuid = UUID.randomUUID();
	workstation_cookie = new Cookie(workstation_cookie_name, generated_uuid.toString());
	workstation_cookie.setMaxAge(365 * 24 * 60 * 60); // expires in one year provided server time matches client time.  may be an issue for several reasons...
	response.addCookie(workstation_cookie);

	primary_cookie = new Cookie(is_primary_cookie_name, "false");
	primary_cookie.setMaxAge(365 * 24 * 60 * 60);
	response.addCookie(primary_cookie);
}

%>

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
	<link rel="stylesheet" type="text/css"href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/resize.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>

	<!-- <script language="javascript" src="http://www.shanetomlinson.com/static/jquery-1.3.2.js"></script> -->


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

    var email_log_in = <%= (!settings.getLoginLabelString().equals("Username")) ? "true" : "false" %>;

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

	var last_target;
	function mouseUpHandler(e)
	{
		var event = e || window.event;
		var target = event.target || event.srcElement;
		if (target != last_target)
		{
			e.preventDefault();
			last_target = target;
		}
	}
	document.onmouseup = mouseUpHandler;

	</script>


<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup", function () {

            // Create a Button using an existing <input> element as a data source

		var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "previous" });
	    var oSubmitButton2 = new YAHOO.widget.Button("submitbutton2", { value: "next" });
	    var oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", { value: "finish" });
        });

    } ();

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        // "contentready" event handler for the "checkboxbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton1 = new Button("checkbutton1", { label: "This is the Primary Workstation" });


        });


    }());

</script>


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


<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>

</head>

<body class=" yui-skin-sam" onload="javascript:initErrors();">


<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white;">


    <div id="content">


	<div class="content_TextBlock">
	    <p class="headline"><%= adminCompany.getLabel() %> End Of Day - Step 1: Confirm Workstation</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-eod-menu.jsp" %>

	    <div class="main">
<!--
		<p class="headlineA"></p>

		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
-->
		<struts-html:form action="/admin/eod01" focus="nameInput">

		    <input type="hidden" name="delete_id" />

			<!--
		    <div class="adminItem"><strong style="font-weight: bolder;">Provide a name</strong> for this workstation.  If you haven't previous provided a name, a default name will be automatically displayed.</div>

		    <div class="adminItem">
			<div class="leftTM">WORKSTATION NAME</div>
			<div class="right">
				<input name="nameInput" onfocus="select();" value="<%= workstation_cookie.getValue() %>" size="31" maxlength="100" class="inputbox" style="width: 306px;" />
			</div>
			<div class="end"></div>
		    </div>
			-->

			<input name="nameInput" type="hidden" value="<%= workstation_cookie.getValue() %>" />

		    <div class="adminItem">Please identify whether this workstation is the <strong style="font-weight: bolder;">Primary Workstation</strong> for processing end of day transactions.  The primary workstation will finalize the end of day tasks and reports.  If this is your only workstation for <%= adminCompany.getLabel() %> then it should be your primary.</div>

		    <div class="adminItem" id="checkboxbuttonsfrommarkup">
			<div class="leftTM"></div>
			<div class="right">
				<input id="checkbutton1" type="checkbox" name="is_primary" value="1"<%= primary_cookie.getValue().equals("true") ? " checked" : "" %> />
			</div>
			<div class="end"></div>
		    </div>

			<div class="content_TextBlock">
				<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			</div>

		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup" class="right">
			    <input id="submitbutton1" disabled="true" class="formbutton" type="submit" name="submit_button" value="Previous" alt="Previous" style="margin-right: 10px; "/>
			    <input id="submitbutton2" class="formbutton" type="submit" name="submit_button" value="Next" alt="Next" style="margin-right: 10px; "/>
			    <input id="submitbutton3" disabled="true" class="formbutton" type="submit" name="submit_button" value="Finish" alt="Finish" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>

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
}
catch (Exception x)
{
    x.printStackTrace();
}
%>


</body>
</html>

