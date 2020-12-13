<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.valeo.qbms.data.*, com.valeo.qbpos.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="cashOut" class="com.badiyan.uk.online.beans.CashOut" scope="session" />

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>

<%

session.setAttribute("eod_08", "true");

Calendar now = Calendar.getInstance();

Date start_date = cashOut.getStartDate();
Date end_date = cashOut.getEndDate();

if (start_date == null)
	start_date = CashOut.getSuggestedCashOutStartDate(adminCompany);
if (end_date == null)
	end_date = now.getTime();

%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Valonyx</title>



<link rel="stylesheet" type="text/css" href="../css/schedule-client.css" />
<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/resize/assets/skins/sam/resize.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/layout/assets/skins/sam/layout.css" />

<link rel="stylesheet" type="text/css" href="../yui/build/menu/assets/skins/sam/menu.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/tabview/assets/skins/sam/tabview.css" />

<script type="text/javascript" src="../yui/build/yahoo/yahoo-min.js"></script>
<script type="text/javascript" src="../yui/build/event/event-min.js"></script>
<script type="text/javascript" src="../yui/build/dom/dom-min.js"></script>

<script type="text/javascript" src="../yui/build/element/element-beta-min.js"></script>
<script type="text/javascript" src="../yui/build/resize/resize-beta-min.js"></script>
<script type="text/javascript" src="../yui/build/animation/animation-min.js"></script>
<script type="text/javascript" src="../yui/build/layout/layout-beta-min.js"></script>

<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/container/assets/skins/sam/container.css" />
<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/calendar/assets/skins/sam/calendar.css" />
<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/container/container-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/calendar/calendar-min.js"></script>

<style type="text/css">

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

<style type="text/css">
    /* Clear calendar's float, using dialog inbuilt form element */
    #container .bd form {
        clear:left;
    }

    /* Have calendar squeeze upto bd bounding box */
    #container .bd {
        padding:0;
    }

    #container .hd {
        text-align:left;
    }

    /* Center buttons in the footer */
    #container .ft .button-group {
        text-align:center;
    }

    /* Prevent border-collapse:collapse from bleeding through in IE6, IE7 */
    #container_c.yui-overlay-hidden table {
        *display:none;
    }

    /* Remove calendar's border and set padding in ems instead of px, so we can specify an width in ems for the container */
    #cal {
        border:none;
        padding:1em;
    }

    /* Datefield look/feel */
    .datefield {
        position:relative;
        top:10px;
        left:10px;
        white-space:nowrap;
        border:1px solid black;
        background-color:#eee;
        width:25em;
        padding:5px;
    }

    .datefield input,
    .datefield button,
    .datefield label  {
        vertical-align:middle;
    }

    .datefield label  {
        font-weight:bold;
    }

    .datefield input  {
        width:15em;
    }

    .datefield button  {
        padding:0 5px 0 5px;
        margin-left:2px;
    }

    .datefield button img {
        padding:0;
        margin:0;
        vertical-align:middle;
    }

    /* Example box */
    .box {
        position:relative;
    }
</style>

<script type="text/javascript" src="../scripts/crir.js"></script>

<script type="text/javascript">

	var oSubmitButton3;
	var oSubmitButton1;

    var httpRequest;
    var mTimer;
    var doUpdateRefresh = false;
	
    var updateInProgress = false;

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
				if (httpRequest.responseXML.getElementsByTagName("update-quickbooks-start").length > 0)
				{
					//alert('update-quickbooks-start');
					xml_response = httpRequest.responseXML.getElementsByTagName("update-quickbooks-start")[0];

					document.getElementById('update_status').firstChild.nodeValue = 'Update Started';
					doUpdateRefresh = true;
					refreshUpdateStatus();
				}
				else if (httpRequest.responseXML.getElementsByTagName("update-quickbooks-status").length > 0)
				{
					//alert('update-quickbooks-status');
					xml_response = httpRequest.responseXML.getElementsByTagName("update-quickbooks-status")[0];
					document.getElementById('update_status').firstChild.nodeValue = xml_response.childNodes[0].nodeValue;
				}
				else if (httpRequest.responseXML.getElementsByTagName("update-quickbooks-complete").length > 0)
				{
					//alert('update-quickbooks-complete');
					xml_response = httpRequest.responseXML.getElementsByTagName("update-quickbooks-complete")[0];
					document.getElementById('logFile').firstChild.nodeValue = xml_response.childNodes[0].nodeValue;
					document.getElementById('logFileAnchor').href = '../resources/pdf/' + xml_response.childNodes[0].nodeValue;
					document.getElementById('update_status').firstChild.nodeValue = 'Update Complete';
					oSubmitButton3.set('disabled', false);
					doUpdateRefresh = false;
					oSubmitButton1.set('disabled', true);
					//addPaymentStuff('Charge To', 'Refund');
					//addPaymentStuff('Credit Card', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					//adjustTableWidth('tblCredits');
					//YAHOO.example.container.panel1.hide();
				}
				else if (httpRequest.responseXML.getElementsByTagName("update-quickbooks-error").length > 0)
				{
					//alert('update-quickbooks-error');
					xml_response = httpRequest.responseXML.getElementsByTagName("update-quickbooks-error")[0];
					document.getElementById('update_status').firstChild.nodeValue = xml_response.childNodes[0].nodeValue;
					doUpdateRefresh = false;
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

    function refreshUpdateStatus()
    {
		if (doUpdateRefresh)
		{
			processCommand('updateQuickBooksStatus');
			mTimer = setTimeout('refreshUpdateStatus();',1000);
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

<script>
YAHOO.namespace("example.container");

function init() {
<%
if (cashOut.isQuickBooksUpdateInProgress())
{
%>
	alert('Update already in progress.');
	doUpdateRefresh = true;
	refreshUpdateStatus();
<%
}
%>
}

YAHOO.util.Event.onDOMReady(init);


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

		oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "previous" });
	    var oSubmitButton2 = new YAHOO.widget.Button("submitbutton2", { value: "next" });
	    oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", { value: "finish" });
        });

    } ();

</script>

<script type="text/javascript">

	function updateQuickBooks(p_oEvent) {
			if (!updateInProgress)
			{
				//updateInProgress = true;
				processCommand('updateQuickBooks', '<%= cashOut.getId() %>');
			}
			else
				alert('Update already in progress...');
        }

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup2", function () {

            // Create a Button using an existing <input> element as a data source

		// var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", { value: "previous" });
		var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", {onclick: {fn: updateQuickBooks}});

        });

    } ();

</script>

</head>

<%
String searchLastName = (String)session.getAttribute("searchLastName");
if (searchLastName == null)
	searchLastName = "";
%>

<body class="yui-skin-sam">

<!-- <h1>Valeo Schedule</h1> -->


<div id="content" style="height: 100%;">

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>

    <div id="center1" style="background-color: white;">

		<div id="content">


			<div class="content_TextBlock">
				<p class="headline"><%= adminCompany.getLabel() %> End Of Day - Step 8: Update Quickbooks</p>
				<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			</div>

			<div class="content_Administration">

<%@ include file="channels\channel-eod-menu.jsp" %>

				<div class="main">

						<struts-html:form action="/admin/eod08">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem"><strong style="font-weight: bolder;">Click the Update Quickbooks button</strong> to exchange end of day data with QuickBooks.</div>

							<div class="adminItem">
								<div class="leftTM">&nbsp;</div>
								<div id="submitbuttonsfrommarkup2" class="right">
									<input type="button" id="submitbutton4" name="update_quickbooks_button" value="Update QuickBooks" alt="Update QuickBooks" style="margin-right: 10px; " />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">UPDATE STATUS</div>
								<div class="right"><strong style="font-weight: bolder;"><span id="update_status">Not Started</span></strong></div>
								<div class="end"></div>
							</div>

<!--
							<div class="adminItem">
								<div class="leftTM">SYNCHRONIZE VENDORS</div>
								<div class="right"><strong style="font-weight: bolder;"><span id="sync_vendors_status">Not Started</span></strong></div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">SYNCHRONIZE CUSTOMERS</div>
								<div class="right"><strong style="font-weight: bolder;"><span id="sync_customers_status">Not Started</span></strong></div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">SEND SALES<br />RECEIPTS</div>
								<div class="right"><strong style="font-weight: bolder;"><span id="send_sales_receipts_status">Not Started</span></strong></div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">SEND RECEIVING VOUCHERS</div>
								<div class="right"><strong style="font-weight: bolder;"><span id="send_receiving_vouchers_status">Not Started</span></strong></div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">SEND ADJUSTMENT MEMOS</div>
								<div class="right"><strong style="font-weight: bolder;"><span id="send_adjustment_memos_status">Not Started</span></strong></div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">IMPORT CUSTOMER BALANCES</div>
								<div class="right"><strong style="font-weight: bolder;"><span id="import_customer_balances_status">Not Started</span></strong></div>
								<div class="end"></div>
							</div>
-->
							<div class="adminItem">
								<div class="leftTM">UPDATE LOG</div>
								<div class="right"><a id="logFileAnchor" href="<%= cashOut.getLogFilePDFLocation() == null ? "" : "../resources/pdf/" + cashOut.getLogFilePDFLocation() %>" target="_blank"><span id="logFile"><%= cashOut.getLogFilePDFLocation() == null ? "&nbsp;" : cashOut.getLogFilePDFLocation() %></span></a></div>
								<div class="end"></div>
							</div>

							<div class="content_TextBlock">
								<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">&nbsp;</div>
								<div id="submitbuttonsfrommarkup" class="right">
									<input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Previous" alt="Previous" style="margin-right: 10px; "/>
									<input id="submitbutton2" disabled="true" class="formbutton" type="submit" name="submit_button" value="Next" alt="Next" style="margin-right: 10px; "/>
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


    </div>


</div>






<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
