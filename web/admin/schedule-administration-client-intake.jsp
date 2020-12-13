<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*, java.math.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%

CompanySettingsBean settings = adminCompany.getSettings();



QuickBooksSettings qb_settings = adminCompany.getQuickBooksSettings();




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
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/resize.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css">
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>
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

	</script>


<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup", function () {

            // Create a Button using an existing <input> element as a data source
<%
if (!settings.isSetupComplete())
{
%>
            var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "previous" });
	    var oSubmitButton2 = new YAHOO.widget.Button("submitbutton2", { value: "next" });
	    var oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", { value: "finish" });
<%
}
%>
        });

    } ();

</script>

<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup2", function () {

            // Create a Button using an existing <input> element as a data source

	    var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", { value: "Add Practitioner" });

        });

    } ();

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        // "contentready" event handler for the "checkboxbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton1 = new Button("checkbutton1", { label: "This User is an Administrator" });


        });


    }());

</script>

<%
if (settings.isSetupComplete())
{
%>
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
<%
}
else
{
%>
<%@ include file="..\channels\channel-google-analytics.jsp" %>
<%
}
%>

<script type="text/javascript">
    (function () {

    	var Button = YAHOO.widget.Button;
        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup3", function () {
            var oCheckButton2 = new Button("checkbutton3", { label: "Enable Client Intake Form" });
        });

    }());
</script>

<script type="text/javascript">
    (function () {

    	var Button = YAHOO.widget.Button;
        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup4", function () {
            var oCheckButton3 = new Button("checkbutton4", { label: "Enable Client Wellness Survey" });
        });

    }());
</script>

<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>
		

</head>

<body class=" yui-skin-sam" onload="javascript:initErrors();">

<%
if (settings.isSetupComplete())
{
%>
<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white;">
<%
}

BigDecimal pos_fee = new BigDecimal(50f);
BigDecimal fee = adminCompany.getBaseMonthlySubscriptionFee();
if (qb_settings.isPOSEnabled())
	fee = fee.add(pos_fee);

int num_clients = adminCompany.getNumClientsForSubscriptionFeeCalculation();
String basic_subscription_fee_str = adminCompany.getBaseMonthlySubscriptionFeeString();



%>


    <div id="content">


	<div class="content_TextBlock">
	    <p class="headline"><%= adminCompany.getLabel() %> <%= settings.isSetupComplete() ? "Administration" : "Setup" %> - Client Intake Settings</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">

		<p><strong style="font-weight: bolder;">Manage your client intake settings.</strong> </p>
		
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		<struts-html:form action="/admin/login" focus="firstNameInput">

		    <input type="hidden" name="delete_id" />
			
		    <div class="adminItem">
				<div class="leftTM">INTAKE FORM</div>
				<div class="right">
					<%= qb_settings.showClientIntakeForm() ? "The client intake form is enabled." : "The client intake form is not enabled." %>
				</div>
				<div class="end"></div>
		    </div>
		    <div class="adminItem" id="checkboxbuttonsfrommarkup3">
				<div class="leftTM"></div>
				<div class="right">
					<input id="checkbutton3" type="checkbox" name="show_client_intake_form" value="1"<%= qb_settings.showClientIntakeForm()? " checked" : "" %> />
				</div>
				<div class="end"></div>
		    </div>
<%
if (!qb_settings.showClientIntakeForm()) {
%>
		    <div class="adminItem">Select <strong style="font-weight: bolder;">Enable Client Intake Form</strong> to allow your clients to complete your intake form online.  You can setup a kiosk for client intake or they can complete this via a mobile device or PC.</div>
<%
} else {
%>
		    <div class="adminItem">Deselect <strong style="font-weight: bolder;">Enable Client Intake Form</strong> to disable your online client intake form.</div>
<%
}
%>


				

			
		    <div class="adminItem">
				<div class="leftTM">WELLNESS SURVEY</div>
				<div class="right">
					<%= qb_settings.showClientWellnessSurvey() ? "The client wellness survey is enabled." : "The client wellness survey is not enabled." %>
				</div>
				<div class="end"></div>
		    </div>
		    <div class="adminItem" id="checkboxbuttonsfrommarkup4">
				<div class="leftTM"></div>
				<div class="right">
					<input id="checkbutton4" type="checkbox" name="show_client_wellness_survey" value="1"<%= qb_settings.showClientIntakeForm()? " checked" : "" %> />
				</div>
				<div class="end"></div>
		    </div>
<%
if (!qb_settings.showClientIntakeForm()) {
%>
		    <div class="adminItem">Select <strong style="font-weight: bolder;">Enable Client Wellness Survey</strong> to allow your clients to complete your wellness survey online.  You can setup a kiosk for the wellness survey or they can complete this via a mobile device or PC.</div>
<%
} else {
%>
		    <div class="adminItem">Deselect <strong style="font-weight: bolder;">Enable Client Wellness Survey</strong> to disable your online client wellness survey.</div>
<%
}
%>
				
				





		    <div class="adminItem">
				<div class="leftTM">&nbsp;</div>
				<div id="submitbuttonsfrommarkup2" class="right">
					<input id="submitbutton4" class="formbutton" type="submit" name="submit_button" value="Save Changes to Intake Settings" alt="Save Changes to Intake Settings" style="margin-right: 10px; "/>
				</div>
				<div class="end"></div>
		    </div>

			<!--
		    <div class="content_AdministrationTable">

			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
			    <div class="heading">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">

					    <tr style="display: block;">
						    <td style="width:  20px; text-align: left;  "></td>
						    <td style="width: 472px; text-align: left;  ">Account Payment History</td>
					    </tr>
				    </table>
			    </div>
			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
Vector practitioners = new Vector();
Iterator itr = practitioners.iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
        UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
%>
			    <!-- BEGIN Organization -->

			    <div class="organization">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">
					    <tr>
						    <td style="width:  20px; text-align: left;  "></td>
						    <td style="width: 402px; text-align: left;  "><a href="setup_02.jsp?id=<%= practitioner.getValue() %>" title=""><%= practitioner.getLabel() %></a></td>
						    <td style="width:  70px; text-align: center;  ">
<%
		if (practitioner.getId() != adminPerson.getId())
		{
%>
							<a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= practitioner.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a>
<%
		}
%>
						    </td>
					    </tr>
				    </table>
			    </div>

			    <!-- END Organization -->
<%
	}
}
else
{
%>
			    <div class="organization">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">
					    <tr>
						    <td style="width:  20px; text-align: left;  "></td>
						    <td style="width: 472px; text-align: left;  ">No Payments Found</td>
					    </tr>
				    </table>
			    </div>
<%
}
%>
			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		    </div>
				
			-->

<%
if (!settings.isSetupComplete())
{
	boolean found_practitioners = true;
%>
			<!--
		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup" class="right">
			    <input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Previous" alt="Previous" style="margin-right: 10px; "/>
			    <input id="submitbutton2"<%= found_practitioners ? "" : " disabled=\"true\"" %> class="formbutton" type="submit" name="submit_button" value="Next" alt="Next" style="margin-right: 10px; "/>
			    <input id="submitbutton3" disabled="true" class="formbutton" type="submit" name="submit_button" value="Finish" alt="Finish" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>
			-->
<%
}
%>

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
%>


</body>
</html>

