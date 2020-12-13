<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />


<%


/*
				AppointmentBean appointment = AppointmentBean.getAppointment(48054);
				
				if (appointment.isClientAppointment()) {
					
					
					System.out.println(" appointment.getClient().getEmail1String() >" + appointment.getClient().getEmail1String());
					if (!appointment.getClient().getEmail1String().isEmpty()) {
						System.out.println(" &&** sending email to client >" + appointment.getClient().getLabel());
						CUBean.sendEmailWithMeeting("marlo@badiyan.com", "cstueve@sanowc.com", appointment.getCompany().getLabel() + " Appointment ReminderMM", com.badiyan.uk.online.util.ICalBuilder.toICal(appointment), appointment.getInitialAppointmentEmailString());
					}
						
					
				}
*/
	
	try {

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

	<link rel="stylesheet" type="text/css"href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/resize.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css">
	<script src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>

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

	</script>




<script>

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

<%@ include file="../channels/channel-head-javascript.jsp" %>

	<title>Valonyx</title>

</head>

<body class=" yui-skin-sam" onload="javascript:initErrors();" >

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white; height: 100%;">
    <div id="content">

	<div class="content_TextBlock">
		<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
		<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

				<div class="content_Administration">

<%@ include file="channels/channel-setup-menu.jsp" %>

				<div class="main">
<%
//HashMap hash = (HashMap)com.valonyx.tasks.BitcoinChartsWeightedPricesTask.weighted_prices.get("USD");
//java.math.BigDecimal weighted_daily = (java.math.BigDecimal)hash.get("24h");
		// weighted_daily.toString()
%>
					<p class="headlineA">Administration Home -- 1BTC =  USD</p>
					<p>To view, update, or delete information associated with your practice, practitioners, clients, or appointments click a menu item on the left.</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
					<p>&nbsp;</p>

				</div>

				<div class="end"></div>
			</div>

			<div class="content_TextBlock">
				<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			</div>





	<div class="content_TextBlock">
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

    </div>


</div>

<%
	} catch (Exception x) {
		x.printStackTrace();
	}
%>
</body>
</html>

