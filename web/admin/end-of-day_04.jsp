<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="cashOut" class="com.badiyan.uk.online.beans.CashOut" scope="session" />

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>

<%
session.setAttribute("eod_04", "true");

// is ther an existing cash out in progress?  if so, perhaps this workstation already has a count...


String workstation_cookie_name = "valeo.workstation_name";
String is_primary_cookie_name = "valeo.workstation_primary";
String dollars_begin_cookie_name = "valeo.dollars_begin";

Cookie[] cookies = request.getCookies();
Cookie workstation_cookie = null;
Cookie primary_cookie = null;
Cookie dollars_begin_cookie = null;
if (cookies != null)
{
	for (int i = 0; i < cookies.length; i++)
	{
		Cookie cookie = cookies[i];
		if (cookie.getName().equals(workstation_cookie_name))
			workstation_cookie = cookie;
		else if (cookie.getName().equals(is_primary_cookie_name))
			primary_cookie = cookie;
		else if (cookie.getName().equals(dollars_begin_cookie_name))
			dollars_begin_cookie = cookie;
	}
}

String dollars_begin_str = "100.00";
if (dollars_begin_cookie == null)
{
	if ((primary_cookie != null) && (primary_cookie.getValue().equals("true")))
		dollars_begin_str = "300.00";
	dollars_begin_cookie = new Cookie(dollars_begin_cookie_name, dollars_begin_str);
	dollars_begin_cookie.setMaxAge(365 * 24 * 60 * 60); // expires in one year provided server time matches client time.  may be an issue for several reasons...
	response.addCookie(dollars_begin_cookie);
}
else
	dollars_begin_str = dollars_begin_cookie.getValue();

BigDecimal dollars_begin_bd = new BigDecimal(dollars_begin_str);
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

    var httpRequest;

    function processCommand(command, parameter)
    {
		if (window.ActiveXObject)
			httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
		else if (window.XMLHttpRequest)
			httpRequest = new XMLHttpRequest();

		httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain") %>/ClientServlet.html', true);
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
				if (httpRequest.responseXML.getElementsByTagName("checkout").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("checkout")[0];
					showCheckout(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];
					showPurchasedPlans(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
					showPeople(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
					showPerson(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("payment-plan-instance").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("payment-plan-instance")[0];
					showPaymentPlanInstance(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("client-billing").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("client-billing")[0];
					showBilling(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("bill").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("bill")[0];
					showBill(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("billing-orders").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("billing-orders")[0];
					showBillingOrders(xml_response);
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

	function updateCount()
	{
		var total_count = 0;

		if (!document.forms[0].count_100.value || isNaN(document.forms[0].count_100.value))
			document.forms[0].count_100_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_100.value) * 100);
			document.forms[0].count_100_total.value = (parseFloat(document.forms[0].count_100.value) * 100).formatMoney(2, '.', ',');
		}

		if (!document.forms[0].count_50.value || isNaN(document.forms[0].count_50.value))
			document.forms[0].count_50_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_50.value) * 50);
			document.forms[0].count_50_total.value = (parseFloat(document.forms[0].count_50.value) * 50).formatMoney(2, '.', ',');
		}

		if (!document.forms[0].count_20.value || isNaN(document.forms[0].count_20.value))
			document.forms[0].count_20_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_20.value) * 20);
			document.forms[0].count_20_total.value = (parseFloat(document.forms[0].count_20.value) * 20).formatMoney(2, '.', ',');
		}

		if (!document.forms[0].count_10.value || isNaN(document.forms[0].count_10.value))
			document.forms[0].count_10_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_10.value) * 10);
			document.forms[0].count_10_total.value = (parseFloat(document.forms[0].count_10.value) * 10).formatMoney(2, '.', ',');
		}

		if (!document.forms[0].count_5.value || isNaN(document.forms[0].count_5.value))
			document.forms[0].count_5_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_5.value) * 5);
			document.forms[0].count_5_total.value = (parseFloat(document.forms[0].count_5.value) * 5).formatMoney(2, '.', ',');
		}

		if (!document.forms[0].count_1.value || isNaN(document.forms[0].count_1.value))
			document.forms[0].count_1_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_1.value) * 1);
			document.forms[0].count_1_total.value = (parseFloat(document.forms[0].count_1.value) * 1).formatMoney(2, '.', ',');
		}

		if (!document.forms[0].count_50cent.value || isNaN(document.forms[0].count_50cent.value))
			document.forms[0].count_50cent_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_50cent.value) * .5);
			document.forms[0].count_50cent_total.value = (parseFloat(document.forms[0].count_50cent.value) * .5).formatMoney(2, '.', ',');
		}

		if (!document.forms[0].count_25cent.value || isNaN(document.forms[0].count_25cent.value))
			document.forms[0].count_25cent_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_25cent.value) * .25);
			document.forms[0].count_25cent_total.value = (parseFloat(document.forms[0].count_25cent.value) * .25).formatMoney(2, '.', ',');
		}

		if (!document.forms[0].count_10cent.value || isNaN(document.forms[0].count_10cent.value))
			document.forms[0].count_10cent_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_10cent.value) * .10);
			document.forms[0].count_10cent_total.value = (parseFloat(document.forms[0].count_10cent.value) * .10).formatMoney(2, '.', ',');
		}

		if (!document.forms[0].count_5cent.value || isNaN(document.forms[0].count_5cent.value))
			document.forms[0].count_5cent_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_5cent.value) * .05);
			document.forms[0].count_5cent_total.value = (parseFloat(document.forms[0].count_5cent.value) * .05).formatMoney(2, '.', ',');
		}

		if (!document.forms[0].count_1cent.value || isNaN(document.forms[0].count_1cent.value))
			document.forms[0].count_1cent_total.value = "0.00";
		else
		{
			total_count += (parseFloat(document.forms[0].count_1cent.value) * .01);
			document.forms[0].count_1cent_total.value = (parseFloat(document.forms[0].count_1cent.value) * .01).formatMoney(2, '.', ',');
		}

		document.forms[0].count.value = total_count.formatMoney(2, '.', ',');

		var leave_amount = 0;
		if (!document.forms[0].leave.value || isNaN(document.forms[0].leave.value))
			leave_amount = 0;
		else
			leave_amount = parseFloat(document.forms[0].leave.value);
		
		var deposit_amount = total_count - leave_amount;
		if (deposit_amount < 0)
			document.forms[0].deposit.value = "0.00";
		else
			document.forms[0].deposit.value = deposit_amount.formatMoney(2, '.', ',');

		//document.forms[0].deposit.value = (total_count - parseFloat(document.forms[0].leave.value)).formatMoney(2, '.', ',');

		//deposit = count - leave;

	}

    Number.prototype.formatMoney = function(c, d, t){
	var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    };

</script>

<script>
YAHOO.namespace("example.container");

function init() {
	updateCount();
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
	    var oSubmitButton2 = new YAHOO.widget.Button("submitbutton2", { value: "next" });
	    var oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", { value: "finish" });
        });

    } ();

</script>

<script type="text/javascript">

	function openCashDrawer(p_oEvent) {
            //alert('Open Sesame!!');
			openCashDrawer();
        }

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup2", function () {

            // Create a Button using an existing <input> element as a data source

		// var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", { value: "previous" });
		var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", {onclick: {fn: openCashDrawer}});

        });

    } ();

</script>

<script type="text/javascript">

	function openCashDrawer() {
		var applet = document.jZebra;
		if (applet != null) {
			applet.append("\x07");
			// Send to the printer
			applet.print();
		}
		else {
			alert("Applet not loaded!");
		}
	}

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
				<p class="headline"><%= adminCompany.getLabel() %> End Of Day - Step 4: Drawer Count</p>
				<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			</div>

			<div class="content_Administration">

<%@ include file="channels\channel-eod-menu.jsp" %>

				<div class="main">

						<struts-html:form action="/admin/eod04">

							<input id="dummy" name="dummy" type="hidden" />
							<input name="status" type="hidden" value="0" />
							<input name="sort" type="hidden" value="1" />
							<input name="format" type="hidden" value="visits" />
							<input name="displayHTML" type="hidden" value="1" />

							<div class="adminItem">Change the <strong style="font-weight: bolder;">Begin amount</strong> if necessary.  Since this is the primary workstation, the <strong style="font-weight: bolder;">Begin amount</strong> is the total begin amount of all workstations.  The <strong style="font-weight: bolder;">Count amount</strong> should update automatically as you count your drawer and provide values below.  The <strong style="font-weight: bolder;">Leave amount</strong> is typically the same as the begin amount.</div>

							<div class="adminItem">
								<div class="leftTM">Dollars</div>
								<div class="right">
									<table cellpadding="0" cellspacing="0">
									  <tr>
										  <td align="right"><strong style="font-weight: bolder;">Begin:</strong></td>
										  <td align="right"><strong style="font-weight: bolder;">Paid Out:</strong></td>
										  <td align="right"><strong style="font-weight: bolder;">Count:</strong></td>
										  <td align="right"><strong style="font-weight: bolder;">Leave:</strong></td>
										  <td align="right"><strong style="font-weight: bolder;">Deposit:</strong></td>
									  </tr>
										<tr>
											<td><input type="text" onfocus="select();" class="inputbox" id="begin" name="begin" style="width: 64px; text-align:right;" value="<%= dollars_begin_bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %>" /></td>
											<td>&nbsp;<input disabled="true" type="text" onfocus="select();" class="inputbox" id="paid_out" name="paid_out" style="width: 64px; text-align:right;" value="0.00" /></td> <!-- I still need to figure out where this comes from... -->
											<td>&nbsp;<input type="text" onfocus="select();" class="inputbox" id="count" name="count" style="width: 64px; text-align:right;" value="0.00" /></td>
											<td>&nbsp;<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="leave" name="leave" style="width: 64px; text-align:right;" value="<%= dollars_begin_bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %>" /></td>
											<td>&nbsp;<input disabled="true" type="text" onfocus="select();" class="inputbox" id="deposit" name="deposit" style="width: 64px; text-align:right;" value="0.00" /></td>
										</tr>
									</table>
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem"><strong style="font-weight: bolder;">Click the Open Cash Drawer button</strong> to begin your count.</div>

							<div class="adminItem">
								<div class="leftTM">&nbsp;</div>
								<div id="submitbuttonsfrommarkup2" class="right">
									<input type="button" id="submitbutton4" name="open_drawer_button" value="Open Cash Drawer" alt="Open Cash Drawer" style="margin-right: 10px; " />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem"><strong style="font-weight: bolder;">Provide counts</strong> for each currency in your cash drawer.</div>

							<div class="adminItem">
								<div class="leftTM">100's</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_100" name="count_100" style="width: 64px;" value="<%= cashOut.getCountHundreds(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_100_total" name="count_100_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">50's</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_50" name="count_50" style="width: 64px;" value="<%= cashOut.getCountFifties(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_50_total" name="count_50_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">20's</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_20" name="count_20" style="width: 64px;" value="<%= cashOut.getCountTwenties(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_20_total" name="count_20_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">10's</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_10" name="count_10" style="width: 64px;" value="<%= cashOut.getCountTens(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_10_total" name="count_10_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">5's</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_5" name="count_5" style="width: 64px;" value="<%= cashOut.getCountFives(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_5_total" name="count_5_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">1's</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_1" name="count_1" style="width: 64px;" value="<%= cashOut.getCountOnes(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_1_total" name="count_1_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">Half-Dollars</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_50cent" name="count_50cent" style="width: 64px;" value="<%= cashOut.getCountHalfDollars(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_50cent_total" name="count_50cent_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">Quarters</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_25cent" name="count_25cent" style="width: 64px;" value="<%= cashOut.getCountQuarters(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_25cent_total" name="count_25cent_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">Dimes</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_10cent" name="count_10cent" style="width: 64px;" value="<%= cashOut.getCountDimes(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_10cent_total" name="count_10cent_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">Nickels</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_5cent" name="count_5cent" style="width: 64px;" value="<%= cashOut.getCountNickels(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_5cent_total" name="count_5cent_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">Pennies</div>
								<div class="right">
									<input type="text" onfocus="select();" onkeyup="updateCount();" class="inputbox" id="count_1cent" name="count_1cent" style="width: 64px;" value="<%= cashOut.getCountPennies(workstation_cookie.getValue()) %>" />
									<input type="text" onfocus="select();" class="inputbox" id="count_1cent_total" name="count_1cent_total" style="width: 64px; text-align:right;" value="0.00" disabled="true" />
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


    </div>


</div>

<applet name="jZebra" code="jzebra.RawPrintApplet.class" archive="jzebra.jar" width="0" height="0">
  <param name="printer" value="Star TSP100 Cutter (TSP143)" />
  <param name="sleep" value="200" />
</applet>

</body>

</html>
