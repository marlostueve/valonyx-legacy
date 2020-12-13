<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.valeo.qbms.data.*, com.valeo.qbpos.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="cashOut" class="com.badiyan.uk.online.beans.CashOut" scope="session" />

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>

<%

session.setAttribute("eod_07", "true");

Calendar now = Calendar.getInstance();

Date start_date = cashOut.getStartDate();
Date end_date = cashOut.getEndDate();

if (start_date == null)
	start_date = CashOut.getSuggestedCashOutStartDate(adminCompany);
if (end_date == null)
	end_date = now.getTime();

Vector responses = cashOut.getAllUncapturedResponses();


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

	var oSubmitButton2;

    var httpRequest;
    var mTimer;
    var doSettleRefresh = false;

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
				if (httpRequest.responseXML.getElementsByTagName("settle-merchant-account-start").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("settle-merchant-account-start")[0];
					//showCheckout(xml_response);
					//alert(xml_response);


					document.getElementById('settle_status').firstChild.nodeValue = 'Settlement Started';
					doSettleRefresh = true;
					refreshSettleStatus();
				}
				else if (httpRequest.responseXML.getElementsByTagName("settle-merchant-account-status").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("settle-merchant-account-status")[0];
					document.getElementById('settle_status').firstChild.nodeValue = xml_response.childNodes[0].nodeValue;
				}
				else if (httpRequest.responseXML.getElementsByTagName("settle-merchant-account-complete").length > 0)
				{
					oSubmitButton2.set('disabled', false);
					doSettleRefresh = false;
					//addPaymentStuff('Charge To', 'Refund');
					//addPaymentStuff('Credit Card', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					//adjustTableWidth('tblCredits');
					//YAHOO.example.container.panel1.hide();
				}
				else if (httpRequest.responseXML.getElementsByTagName("settle-merchant-account-error").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("settle-merchant-account-error")[0];
					document.getElementById('settle_status').firstChild.nodeValue = xml_response.childNodes[0].nodeValue;
					oSubmitButton2.set('disabled', false);
					doSettleRefresh = false;
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

    function refreshSettleStatus()
    {
		if (doSettleRefresh)
		{
			processCommand('settleMerchantAccountStatus');
			mTimer = setTimeout('refreshSettleStatus();',1000);
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
if (cashOut.isMerchantAccountSettlementInProgress())
{
%>
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

		var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "previous" });
	    oSubmitButton2 = new YAHOO.widget.Button("submitbutton2", { value: "next" });
	    var oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", { value: "finish" });
        });

    } ();

</script>

<script type="text/javascript">

	function settleMerchantAccount(p_oEvent) {
			processCommand('settleMerchantAccount', '<%= cashOut.getId() %>');
        }

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup2", function () {

            // Create a Button using an existing <input> element as a data source

		// var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", { value: "previous" });
		var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", {onclick: {fn: settleMerchantAccount}});

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
				<p class="headline"><%= adminCompany.getLabel() %> End Of Day - Step 7: Settle Merchant Account</p>
				<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			</div>

			<div class="content_Administration">

<%@ include file="channels\channel-eod-menu.jsp" %>

				<div class="main">

						<struts-html:form action="/admin/eod07">

							<input id="dummy" name="dummy" type="hidden" />
							<input name="status" type="hidden" value="0" />
							<input name="sort" type="hidden" value="1" />
							<input name="format" type="hidden" value="visits" />
							<input name="displayHTML" type="hidden" value="1" />
<%
Iterator response_itr = responses.iterator();
if (response_itr.hasNext())
{
%>
							<div class="adminItem">
								<div class="leftTM">SETTLE STATUS</div>
								<div class="right"><strong style="font-weight: bolder;"><span id="settle_status">Not Started</span></strong></div>
								<div class="end"></div>
							</div>

							<div class="adminItem"><strong style="font-weight: bolder;">Click the Settle Merchant Account button</strong> to initiate settlement.</div>

							<div class="adminItem">
								<div class="leftTM">&nbsp;</div>
								<div id="submitbuttonsfrommarkup2" class="right">
									<input type="button" id="submitbutton4" name="settle_merchant_account_button" value="Settle Merchant Account" alt="Settle Merchant Account" style="margin-right: 10px; " />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">The following credit card transaction need to be settled with your merchant account:</div>
<%
}
%>
							<div class="adminItem">
								<div class="leftTM">PENDING CHARGES</div>
								<div class="right">
									<table cellpadding="0" cellspacing="0">
<%
boolean charges_exist = false;

if (response_itr.hasNext())
{
	charges_exist = true;
	while (response_itr.hasNext())
	{
		QBMSCreditCardResponse credit_card_response = (QBMSCreditCardResponse)response_itr.next();
		UKOnlinePersonBean client = credit_card_response.getClient();
		TenderRet tender = credit_card_response.getTender();
%>
									  <tr>
										  <td><%= credit_card_response.getAuthorizationCode() %></td>
										  <td><%= client.getLabel() %></td>
										  <td><%= tender.getLabel() %></td>
									  </tr>
<%
	}
}
else
{
%>
									  <tr><td>No charges found to be settled.</td></tr>
<%
}
%>
									</table>
								</div>
								<div class="end"></div>
							</div>

							<div class="content_TextBlock">
								<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">&nbsp;</div>
								<div id="submitbuttonsfrommarkup" class="right">
									<input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Previous" alt="Previous" style="margin-right: 10px; "/>
									<input id="submitbutton2"<%= charges_exist ? " disabled=\"true\"" : "" %> class="formbutton" type="submit" name="submit_button" value="Next" alt="Next" style="margin-right: 10px; "/>
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
