<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.*, com.valeo.qb.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.webservices.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />


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

    (function () {

    	var Button = YAHOO.widget.Button;

        // "contentready" event handler for the "checkboxbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton1 = new Button("checkbutton1", { label: "Synchronize Chart of Accounts" });
            var oCheckButton2 = new Button("checkbutton2", { label: "Synchronize Vendors" });
            var oCheckButton3 = new Button("checkbutton3", { label: "Synchronize Clients" });
            var oCheckButton4 = new Button("checkbutton4", { label: "Synchronize Inventory" });


        });


    }());

</script>

<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>

</head>

<body class=" yui-skin-sam" onload="javascript:initErrors();" >

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white; height: 100%;">
    <div id="content">

	<div class="content_TextBlock">
		<p class="headline"><%= adminCompany.getLabel() %> Administration - Quickbooks</p>
		<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

				<div class="content_Administration">

<%@ include file="channels\channel-setup-menu.jsp" %>

				<div class="main">

				    <struts-html:form action="/admin/qbfsSetup" >

					<input type="hidden" name="delete_id" />
					
					<p>Follow the steps below to setup QuickBooks so that it can share data with Valonyx.</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
					<div class="adminItem">
						<div class="leftTM">STEP 1</div>
						<div class="right">
						    If you don't already have it installed, <a href="http://marketplace.intuit.com/webconnector/" target="_blank">Download the QuickBooks Web Connector</a> installation program.  After the download is complete, run the installation program to install the Web Connector.
						</div>
						<div class="end"></div>
					</div>
					<div class="adminItem">
						<div class="leftTM">STEP 2</div>
						<div class="right">
						    <a href="<%= CUBean.getProperty("cu.webDomain") %><%= CUBean.getProperty("cu.resourcesFolder") %>qwc/<%= qwc_file_url %>" target="_blank">Download your QWC file</a>.  This program tells the QuickBooks Web Connector how to talk to Valonyx.  You may have to <strong>right-click</strong> this link and <strong>select Save link as... or Save as</strong> and choose a download location for the file.<br /><br />Alternately, you can paste the text below into your own file.  You'll want to create the file with a QWC extension.<br /><br />
							<textarea rows="14" style="width: 500px;" ><%= QBQWCFileGenerator.generateQWCString(adminCompany).replaceAll("\n\r", "\n") %></textarea>
						</div>
						<div class="end"></div>
					</div>
					<div class="adminItem">
						<div class="leftTM">STEP 3</div>
						<div class="right">
						    Open the QWC file (that you downloaded in STEP 2) and add it to the web connector so that the web connector is able to connect to the web service.  You should be able to just double-click on the QWC file to add it.
						</div>
						<div class="end"></div>
					</div>
					<div class="adminItem">
						<div class="leftTM">STEP 4</div>
						<div class="right">
						    Enter your password, below, where you are prompted to do so in QuickBooks Web Connector.
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
						<div class="leftTM">STEP 5</div>
						<div class="right">
						    Enter your Company Key.  This is typically the full path and name of your QuickBooks data file.<br /><div style="font-weight: bold">For example:<br /> C:\Documents and Settings\All Users\Documents\Intuit\QuickBooks\Company Files\Valeo.QBW</div>
						</div>
						<div class="end"></div>
					</div>
					    
					<div class="adminItem">
						<div class="leftTM">COMPANY KEY</div>
						<div class="right">
							<TEXTAREA class="inputbox" style="width: 375px; height: 60px;" NAME="company_key" ROWS=6><%= qb_settings.getCompanyKeyString() %></TEXTAREA>
						</div>
						<div class="end"></div>
					</div>

					<!--
					<div id="checkboxbuttonsfrommarkup">
					    <div class="adminItem">
						<div class="leftTM">SYNCHRONIZE SETTINGS</div>
						<div class="right">
						    <input id="checkbutton1" type="checkbox" name="sync_accounts" value="1"<%= qb_settings.isSyncAccounts() ? " checked" : "" %>>
						    <input id="checkbutton2" type="checkbox" name="sync_vendors" value="1"<%= qb_settings.isSyncVendors() ? " checked" : "" %>>
						    <input id="checkbutton3" type="checkbox" name="sync_clients" value="1"<%= qb_settings.isSyncClients() ? " checked" : "" %>>
						    <input id="checkbutton4" type="checkbox" name="sync_inventory" value="1"<%= qb_settings.isSyncInventory() ? " checked" : "" %>>
						</div>
						<div class="end"></div>
					    </div>
					</div>
					-->

					<div class="adminItem">
					    <div class="leftTM">&nbsp;</div>
					    <div id="submitbuttonsfrommarkup" class="right">
						<input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Submit" alt="Submit" style="margin-right: 10px; "/>
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


</div>


</body>
</html>

