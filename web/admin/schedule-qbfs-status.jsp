<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.*, com.valeo.qb.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.webservices.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminQBXMLRequest" class="com.valeo.qb.QBXMLGenericQueryRequest" scope="session" />


<%
QuickBooksSettings qb_settings = adminCompany.getQuickBooksSettings();

String qwc_file_url = QBQWCFileGenerator.generateQWCFile(adminCompany);

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

		httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain")%>/ScheduleServlet.html', true);
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
				if (httpRequest.responseXML.getElementsByTagName("quickbooks-status").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("quickbooks-status")[0];
					if (xml_response.childNodes[0].nodeValue != '')
						document.getElementById('status').firstChild.nodeValue = xml_response.childNodes[0].nodeValue;
					
					//alert(xml_response.childNodes[0].nodeValue);

					//document.getElementById('today_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
					//document.getElementById('new_appt_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
					//document.forms[0].newAppointmentDateInput.value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
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

<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup", function () {

            // Create a Button using an existing <input> element as a data source

            var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "submit" });

        });

    } ();

</script>

<script type="text/javascript">
	
	var doUpdateRefresh = true;

    function
	refreshUpdateStatus()
    {
		if (doUpdateRefresh)
		{
			processCommand('showQuickBooksMessages');
			mTimer = setTimeout('refreshUpdateStatus();',2500);
		}
    }

</script>

<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>

</head>

<body class=" yui-skin-sam" onload="javascript:initErrors(); refreshUpdateStatus();" >

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white; height: 100%;">
    <div id="content">

	<div class="content_TextBlock">
		<p class="headline"><%= adminCompany.getLabel() %> Administration - Quickbooks Status</p>
		<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

				<div class="content_Administration">

<%@ include file="channels\channel-setup-menu.jsp" %>

				<div class="main">


					<p>To view, update, or delete information associated with your practice, practitioners, clients, or appointments click a menu item on the left.</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

					

					    <input type="hidden" name="delete_id" />

					    <div class="adminItem">
						    <div class="leftTM">COMPANY KEY</div>
						    <div class="right">
							<%= qb_settings.getCompanyKeyString().equals("") ? "No Company Key entered." : qb_settings.getCompanyKeyString() %>
						    </div>
						    <div class="end"></div>
					    </div>

					    <div class="adminItem">
						<div class="leftTM">QB PASSWORD</div>
						<div class="right"><strong><%= qb_settings.getPasswordString() %></strong>
						</div>
						<div class="end"></div>
					    </div>
					    <div class="adminItem">
						    <div class="leftTM">STATUS</div>
						    <div class="right">
							<span id="status">
							<%= qb_settings.isQuickBooksFSLogInAttempted() ? (qb_settings.isQuickBooksFSTalking() ? "Your QuickBooks web connector has logged in and has exchanged data." : "Your QuickBooks web connector has logged in, but no data has been transferred.  Please check your company key to ensure that it's correct.") : "No log in attempted from your QuickBooks web connector." %>
							</span>
						    </div>
						    <div class="end"></div>
					    </div>
<%
try
{
if (!qb_settings.getCompanyKeyString().equals(""))
{

    QBPOSXMLRequestQueue qbfs_request_queue = QBWebConnectorSvcSoapImpl.queue_hash.get(qb_settings);
    if (qbfs_request_queue != null)
    {
%>
					<div class="adminItem">
						<div class="leftTM">PENDING REQUESTS</div>
						<div class="right">
						    <%= qbfs_request_queue.size(qb_settings.getCompanyKeyString()) %>
						</div>
						<div class="end"></div>
					</div>
					<div class="adminItem">
						<div class="leftTM">REQUEST QUEUE</div>
						<div class="right">
<%
		Iterator queue_itr = qbfs_request_queue.getQueue(qb_settings.getCompanyKeyString()).iterator();
		while (queue_itr.hasNext())
		{
			QBPOSXMLRequest qb_request = (QBPOSXMLRequest)queue_itr.next();
			//String ticket = (String)QBWebConnectorSvcSoapImpl.request_ticket_map.get(qb_request);
			String ticket = null;
%>
						<%= qb_request.getLabel() %><%= (ticket != null) ? (" (" + ticket + ")") : "" %><%= queue_itr.hasNext() ? "<br />" : "" %>
<%
			System.out.println("GEN XML >" + qb_request.toXMLString());
		}
%>
						</div>
						<div class="end"></div>
					</div>
<!--
					<div class="adminItem">
						<div class="leftTM">COMPLETED REQUESTS</div>
						<div class="right">
<%
		Iterator response_itr = QBWebConnectorSvcSoapImpl.request_response_map.keySet().iterator();
		while (response_itr.hasNext())
		{
			QBPOSXMLRequest qb_request = (QBPOSXMLRequest)response_itr.next();
			String response_str = null;
			if (qb_request != null)
			response_str = (String)QBWebConnectorSvcSoapImpl.request_response_map.get(qb_request);
%>
						<%= qb_request.getLabel() %><%= response_itr.hasNext() ? "<br />" : "" %>
<%
		}
%>
						</div>
						<div class="end"></div>
					</div>
-->

<%
    }
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


	<div class="content_TextBlock">
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

    </div>


</div>


</body>
</html>

