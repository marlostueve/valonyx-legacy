<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="cashOut" class="com.badiyan.uk.online.beans.CashOut" scope="session" />

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>

<%

session.setAttribute("eod_02", "true");

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

</script>

<script>
YAHOO.namespace("example.container");

function init() {

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
    YAHOO.util.Event.onDOMReady(function(){

        var Event = YAHOO.util.Event,
            Dom = YAHOO.util.Dom,
            dialog,
            calendar;

        var showStartBtn = Dom.get("show_start_date");

        Event.on(showStartBtn, "click", function() {


            // Lazy Dialog Creation - Wait to create the Dialog, and setup document click listeners, until the first time the button is clicked.
            //if (!dialog) {

                // Hide Calendar if we click anywhere in the document other than the calendar
                Event.on(document, "click", function(e) {
                    var el = Event.getTarget(e);
                    var dialogEl = dialog.element;
                    if (el != dialogEl && !Dom.isAncestor(dialogEl, el) && el != showStartBtn && !Dom.isAncestor(showStartBtn, el)) {
                        dialog.hide();
                    }
                });

                function resetHandler() {
                    // Reset the current calendar page to the select date, or
                    // to today if nothing is selected.
                    var selDates = calendar.getSelectedDates();
                    var resetDate;

                    if (selDates.length > 0) {
                        resetDate = selDates[0];
                    } else {
                        resetDate = calendar.today;
                    }

                    calendar.cfg.setProperty("pagedate", resetDate);
                    calendar.render();
                }

                function closeHandler() {
                    dialog.hide();
                }

                dialog = new YAHOO.widget.Dialog("container", {
                    visible:false,
                    context:["show_start_date", "tl", "bl"],
                    buttons:[ {text:"Reset", handler: resetHandler, isDefault:true}, {text:"Close", handler: closeHandler}],
                    draggable:false,
                    close:true
                });
                dialog.setHeader('Pick A Date');
                dialog.setBody('<div id="cal"></div>');
                dialog.render(document.body);

                dialog.showEvent.subscribe(function() {
                    if (YAHOO.env.ua.ie) {
                        // Since we're hiding the table using yui-overlay-hidden, we
                        // want to let the dialog know that the content size has changed, when
                        // shown
                        dialog.fireEvent("changeContent");
                    }
                });
            //}

            // Lazy Calendar Creation - Wait to create the Calendar until the first time the button is clicked.
            //if (!calendar) {

                calendar = new YAHOO.widget.Calendar("cal", {
                    iframe:false,          // Turn iframe off, since container has iframe support.
                    hide_blank_weeks:true  // Enable, to demonstrate how we handle changing height, using changeContent
                });
                calendar.render();

                calendar.selectEvent.subscribe(function() {
                    if (calendar.getSelectedDates().length > 0) {

                        var selDate = calendar.getSelectedDates()[0];

                        // Pretty Date Output, using Calendar's Locale values: Friday, 8 February 2008
                        var wStr = calendar.cfg.getProperty("WEEKDAYS_LONG")[selDate.getDay()];
                        var dStr = selDate.getDate();
                        var mStr = calendar.cfg.getProperty("MONTHS_LONG")[selDate.getMonth()];
                        var yStr = selDate.getFullYear();

                        //Dom.get("start_date").value = wStr + ", " + dStr + " " + mStr + " " + yStr;
			Dom.get("start_date").value = (selDate.getMonth() + 1) + "/" + selDate.getDate() + "/" + selDate.getFullYear();
                    } else {
                        Dom.get("start_date").value = "";
                    }
                    dialog.hide();
                });

                calendar.renderEvent.subscribe(function() {
                    // Tell Dialog it's contents have changed, which allows
                    // container to redraw the underlay (for IE6/Safari2)
                    dialog.fireEvent("changeContent");
                });
            //}


            var seldate = calendar.getSelectedDates();

            if (seldate.length > 0) {
                // Set the pagedate to show the selected date if it exists
                calendar.cfg.setProperty("pagedate", seldate[0]);
                calendar.render();
            }

            dialog.show();
        });
    });
</script>

<script type="text/javascript">
    YAHOO.util.Event.onDOMReady(function(){

        var Event2 = YAHOO.util.Event,
            Dom2 = YAHOO.util.Dom,
            dialog2,
            calendar2;

        var showEndBtn = Dom2.get("show_end_date");

        Event2.on(showEndBtn, "click", function() {

	    //alert('show2');

            // Lazy Dialog Creation - Wait to create the Dialog, and setup document click listeners, until the first time the button is clicked.
            //if (!dialog2) {

                // Hide Calendar if we click anywhere in the document other than the calendar
                Event2.on(document, "click", function(e) {
                    var el = Event2.getTarget(e);
                    var dialogEl = dialog2.element;
                    if (el != dialogEl && !Dom2.isAncestor(dialogEl, el) && el != showEndBtn && !Dom2.isAncestor(showEndBtn, el)) {
                        dialog2.hide();
                    }
                });

                function resetHandler() {
                    // Reset the current calendar page to the select date, or
                    // to today if nothing is selected.
                    var selDates = calendar2.getSelectedDates();
                    var resetDate;

                    if (selDates.length > 0) {
                        resetDate = selDates[0];
                    } else {
                        resetDate = calendar2.today;
                    }

                    calendar2.cfg.setProperty("pagedate", resetDate);
                    calendar2.render();
                }

                function closeHandler() {
                    dialog2.hide();
                }

                dialog2 = new YAHOO.widget.Dialog("container", {
                    visible:false,
                    context:["show_end_date", "tl", "bl"],
                    buttons:[ {text:"Reset", handler: resetHandler, isDefault:true}, {text:"Close", handler: closeHandler}],
                    draggable:false,
                    close:true
                });
                dialog2.setHeader('Pick A Date');
                dialog2.setBody('<div id="cal"></div>');
                dialog2.render(document.body);

                dialog2.showEvent.subscribe(function() {
                    if (YAHOO.env.ua.ie) {
                        // Since we're hiding the table using yui-overlay-hidden, we
                        // want to let the dialog know that the content size has changed, when
                        // shown
                        dialog2.fireEvent("changeContent");
                    }
                });
            //}

            // Lazy Calendar Creation - Wait to create the Calendar until the first time the button is clicked.
            //if (!calendar2) {

                calendar2 = new YAHOO.widget.Calendar("cal", {
                    iframe:false,          // Turn iframe off, since container has iframe support.
                    hide_blank_weeks:true  // Enable, to demonstrate how we handle changing height, using changeContent
                });
                calendar2.render();

                calendar2.selectEvent.subscribe(function() {
                    if (calendar2.getSelectedDates().length > 0) {

                        var selDate = calendar2.getSelectedDates()[0];

                        // Pretty Date Output, using Calendar's Locale values: Friday, 8 February 2008
                        var wStr = calendar2.cfg.getProperty("WEEKDAYS_LONG")[selDate.getDay()];
                        var dStr = selDate.getDate();
                        var mStr = calendar2.cfg.getProperty("MONTHS_LONG")[selDate.getMonth()];
                        var yStr = selDate.getFullYear();

                        //Dom2.get("end_date").value = wStr + ", " + dStr + " " + mStr + " " + yStr;
			Dom2.get("end_date").value = (selDate.getMonth() + 1) + "/" + selDate.getDate() + "/" + selDate.getFullYear();
                    } else {
                        Dom2.get("end_date").value = "";
                    }
                    dialog2.hide();
                });

                calendar2.renderEvent.subscribe(function() {
                    // Tell Dialog it's contents have changed, which allows
                    // container to redraw the underlay (for IE6/Safari2)
                    dialog2.fireEvent("changeContent");
                });
            //}

            var seldate = calendar2.getSelectedDates();

            if (seldate.length > 0) {
                // Set the pagedate to show the selected date if it exists
                calendar2.cfg.setProperty("pagedate", seldate[0]);
                calendar2.render();
            }

            dialog2.show();
        });
    });
</script>

<script type="text/javascript">

    (function () {

	        var Dom3 = YAHOO.util.Dom;

		var ButtonGroup = YAHOO.widget.ButtonGroup;
		var oButtonGroup1;
		var start_date_str = "";
		var end_date_str = "";


        // "checkedButtonChange" event handler for each ButtonGroup instance

        var onCheckedButtonChange = function (p_oEvent) {

	    //alert(YAHOO.util.Event.getRelatedTarget(p_oEvent));

	    //alert(oButtonGroup1.get('checkedButton'));

	    //if (oButtonGroup1.get('checkedButton') == 'Button radio1')
	    //{
		//alert('nappy');
	    //}

	    for (var i = 0; i < oButtonGroup1.getCount(); i++)
	    {
		var rad_but = oButtonGroup1.getButton(i);

		//alert(rad_but.get("checked"));

		if (rad_but.get("checked"))
		{
		    switch (i)
		    {
			case 0:
<%
// today
Calendar c = Calendar.getInstance();
%>
			    end_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
<%
//c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
%>
			    start_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
			    break;
			case 1:
<%
// last week
c = Calendar.getInstance();
c.add(Calendar.DAY_OF_WEEK, -1);
%>
			    end_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
<%
//c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
%>
			    start_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
			    break;
			case 2:
<%
// this week
c = Calendar.getInstance();
%>
			    end_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
<%
c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
%>
			    start_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
			    break;
			case 3:
<%
// last month
c = Calendar.getInstance();
c.add(Calendar.MONTH, -1);
c.set(Calendar.DATE, 1);
%>
			    start_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
<%
c = Calendar.getInstance();
c.set(Calendar.DATE, 1);
c.add(Calendar.DATE, -1);
%>
			    end_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
			    break;
			case 4:
<%
// this year
c = Calendar.getInstance();
%>
			    end_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
<%
c.set(Calendar.DAY_OF_YEAR, 1);
%>
			    start_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
			    break;
			case 5:
<%
// last year
c = Calendar.getInstance();
c.add(Calendar.YEAR, -1);
c.set(Calendar.DAY_OF_YEAR, 1);
%>
			    start_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
<%
c = Calendar.getInstance();
c.set(Calendar.DAY_OF_YEAR, 1);
c.add(Calendar.DATE, -1);
%>
			    end_date_str = "<%= (c.get(Calendar.MONTH) + 1) %>/<%= c.get(Calendar.DATE) %>/<%= (c.get(Calendar.YEAR) + "").substring(2) %>";
			    break;
		    }
		    break;
		}

	    }

	    Dom3.get("start_date").value = start_date_str;
	    Dom3.get("end_date").value = end_date_str;

	    //alert(oButtonGroup1.getCount());

	    //email_log_in = !email_log_in;
	    //document.forms[0].usernameInput.disabled = email_log_in;
        };


        // "contentready" event handler for the "radiobuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("radiobuttonsfrommarkup", function () {

            oButtonGroup1 = new ButtonGroup("buttongroup1");
            oButtonGroup1.on("checkedButtonChange", onCheckedButtonChange);


        });

    }());

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
				<p class="headline"><%= adminCompany.getLabel() %> End Of Day - Step 2: Select Date Range</p>
				<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			</div>

			<div class="content_Administration">

<%@ include file="channels\channel-eod-menu.jsp" %>

				<div class="main">

						<struts-html:form action="/admin/eod02">

							<input id="dummy" name="dummy" type="hidden" />
							<input name="status" type="hidden" value="0" />
							<input name="sort" type="hidden" value="1" />
							<input name="format" type="hidden" value="visits" />
							<input name="displayHTML" type="hidden" value="1" />

							<div class="adminItem"><strong style="font-weight: bolder;">Select a date range</strong> for end of day processing. Today's date is selected by default.</div>

							<div class="adminItem">
								<div class="leftTM">START&nbsp;&#47;&nbsp;END DATE</div>
								<div class="right">
									<table cellpadding="0" cellspacing="0">
										<tr>
											<td>
												<input type="text" onfocus="select();" class="inputbox" id="start_date" name="start_date" style="width: 64px;" value="<%= CUBean.getUserDateString(start_date) %>" /><button type="button" id="show_start_date" title="Show Calendar"><img src="http://developer.yahoo.com/yui/examples/calendar/assets/calbtn.gif" width="18" height="18" alt="Calendar" /></button>
											</td>
											<td>
												<img src="../images/gfxDash.gif" width="21" height="21" title="-" alt="-" style="_margin-top: 1px;" />
											</td>
											<td>
												<input type="text" onfocus="select();" class="inputbox" id="end_date" name="end_date" style="width: 64px;" value="<%= CUBean.getUserDateString(end_date) %>" /><button type="button" id="show_end_date" title="Show Calendar"><img src="http://developer.yahoo.com/yui/examples/calendar/assets/calbtn.gif" width="18" height="18" alt="Calendar" /></button>
											</td>
										</tr>
									  <tr>
										  <td>
											&nbsp;12:00 AM&nbsp;
											</td>
										  <td>
											&nbsp;&nbsp;
											</td>
										  <td>
											&nbsp;11:59 PM&nbsp;
											</td>
									  </tr>
									</table>
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem"><strong style="font-weight: bolder;">Click a button</strong> to select another date range.</div>

							<div class="adminItem">
							    <div class="leftTM">&nbsp;</div>
								<div id="radiobuttonsfrommarkup" class="right">
									<div id="buttongroup1" class="yui-buttongroup">
										<input id="radio1" type="radio" name="date_range" value="Today" />
										<input id="radio2" type="radio" name="date_range" value="Yesterday" />
										<input id="radio3" type="radio" name="date_range" value="This Week" />
								    </div>
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






<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
