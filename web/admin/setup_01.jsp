<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />


<%
try
{


boolean is_sysadmin = false;

try
{
    //UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
    is_sysadmin = loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME);
}
catch (Exception xx)
{
}


session.setAttribute("setup_01", "true");

System.out.println("request.getParameter(\"new\") >" + request.getParameter("new"));

if ((request.getParameter("new") != null) && ((String)request.getParameter("new")).equals("true"))
{
    adminCompany = new UKOnlineCompanyBean();
    session.setAttribute("adminCompany", adminCompany);
}
else if (request.getParameter("id") != null)
{
    try
    {
		int companyId = Integer.parseInt(request.getParameter("id"));
		UKOnlineCompanyBean company_obj = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(companyId);

		// ensure that this person is an admin for this company
		if (company_obj.isAdministrator(loginBean.getPerson()))
		{
			adminCompany = company_obj;
			session.setAttribute("adminCompany", adminCompany);
			loginBean.getPerson().selectCompany(adminCompany);
		}
    }
    catch (Exception x)
    {
		x.printStackTrace();
    }
}

if (adminCompany.isNew())
    adminPerson = new UKOnlinePersonBean();
else {
	try {
		adminPerson = adminCompany.getAdministrator();
	} catch (Exception x) {
		adminPerson = new UKOnlinePersonBean();
	}
}
session.setAttribute("adminPerson", adminPerson);

CompanySettingsBean settings = adminCompany.getSettings();
Boolean isPractitioner = (Boolean)session.getAttribute("isPractitioner");
if (isPractitioner == null)
{
    if (!adminPerson.isNew())
		isPractitioner = new Boolean(adminPerson.hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME));
    else
		isPractitioner = new Boolean(false);
    session.setAttribute("isPractitioner", isPractitioner);
}

String confirm_password_str = adminPerson.getConfirmPasswordString();
if (!adminPerson.isNew() && confirm_password_str.equals(""))
    confirm_password_str = adminPerson.getPasswordString();

AddressBean practice_address;
try
{
	practice_address = adminCompany.getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
}
catch (Exception x)
{
	practice_address = new AddressBean();
	practice_address.setType(AddressBean.PRACTICE_ADDRESS_TYPE);
}

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
if (settings.isSetupComplete())
{
%>
            var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "submit" });
<%
}
else
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

    (function () {

		var ButtonGroup = YAHOO.widget.ButtonGroup;


        // "checkedButtonChange" event handler for each ButtonGroup instance

        var onCheckedButtonChange = function (p_oEvent) {
	    email_log_in = !email_log_in;
	    document.forms[0].usernameInput.disabled = email_log_in;
        };


        // "contentready" event handler for the "radiobuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("radiobuttonsfrommarkup", function () {

            var oButtonGroup1 = new ButtonGroup("buttongroup1");
            oButtonGroup1.on("checkedButtonChange", onCheckedButtonChange);


        });

    }());

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        // "contentready" event handler for the "checkboxbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton1 = new Button("checkbutton1", { label: "This User is a Practitioner" });


        });


    }());

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        // "contentready" event handler for the "checkboxbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup2", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton2 = new Button("checkbutton2", { label: "QuickBooks Enabled" });


        });


    }());

</script>

<script type="text/javascript">
    (function () {

    	var Button = YAHOO.widget.Button;
        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup3", function () {
            var oCheckButton3 = new Button("checkbutton3", { label: "PoS Enabled" });
        });

    }());
</script>

<script type="text/javascript">
    (function () {

    	var Button = YAHOO.widget.Button;
        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup4", function () {
            var oCheckButton4 = new Button("checkbutton4", { label: "BTC Payment Enabled" });
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
%>

    <div id="content">


	<div class="content_TextBlock">
	    <p class="headline"><%= adminCompany.getLabel() %> <%= settings.isSetupComplete() ? "Administration" : "Setup" %> - <%= settings.isSetupComplete() ? "" : "Step 1: " %>Practice</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">
<!--
		<p class="headlineA"></p>

		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
-->
		<struts-html:form action="/admin/setup01" focus="nameInput">

		    <input type="hidden" name="delete_id" />

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide a name</strong> for your new practice.  If you already have an account, and you want to create an additional practice, please log in and do so from the Administration area.</div>

		    <div class="adminItem">
			<div class="leftTM">PRACTICE NAME</div>
			<div class="right">
			    <input name="nameInput" onfocus="select();" value="<%= adminCompany.getLabel() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">ADDRESS</div>
			<div class="right">
				<input name="addressInput" onfocus="select();" value="<%= practice_address.getStreet1String() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">CITY / STATE / ZIP</div>
			<div class="right">
			    <input name="cityInput" onfocus="select();" value="<%= practice_address.getCityString() %>" size="31" maxlength="100" class="inputbox" style="width: 106px;" />
				<select name="stateInput" class="select" onchange="javascript:selectState();">
				  <option VALUE="">PLEASE SELECT</option>
				  <option VALUE="Alabama"<%= practice_address.getStateString().equals("Alabama") ? " SELECTED" : "" %>>Alabama</option>
				  <option VALUE="Alaska"<%= practice_address.getStateString().equals("Alaska") ? " SELECTED" : "" %>>Alaska</option>
				  <option VALUE="Arizona"<%= practice_address.getStateString().equals("Arizona") ? " SELECTED" : "" %>>Arizona</option>
				  <option VALUE="Arkansas"<%= practice_address.getStateString().equals("Arkansas") ? " SELECTED" : "" %>>Arkansas</option>
				  <option VALUE="California"<%= practice_address.getStateString().equals("California") ? " SELECTED" : "" %>>California</option>
				  <option VALUE="Colorado"<%= practice_address.getStateString().equals("Colorado") ? " SELECTED" : "" %>>Colorado</option>
				  <option VALUE="Connecticut"<%= practice_address.getStateString().equals("Connecticut") ? " SELECTED" : "" %>>Connecticut</option>
				  <option VALUE="Delaware"<%= practice_address.getStateString().equals("Delaware") ? " SELECTED" : "" %>>Delaware</option>
				  <option VALUE="District of Columbia"<%= practice_address.getStateString().equals("District of Columbia") ? " SELECTED" : "" %>>District of Columbia</option>
				  <option VALUE="Florida"<%= practice_address.getStateString().equals("Florida") ? " SELECTED" : "" %>>Florida</option>
				  <option VALUE="Georgia"<%= practice_address.getStateString().equals("Georgia") ? " SELECTED" : "" %>>Georgia</option>
				  <option VALUE="Hawaii"<%= practice_address.getStateString().equals("Hawaii") ? " SELECTED" : "" %>>Hawaii</option>
				  <option VALUE="Idaho"<%= practice_address.getStateString().equals("Idaho") ? " SELECTED" : "" %>>Idaho</option>
				  <option VALUE="Illinois"<%= practice_address.getStateString().equals("Illinois") ? " SELECTED" : "" %>>Illinois</option>
				  <option VALUE="Indiana"<%= practice_address.getStateString().equals("Indiana") ? " SELECTED" : "" %>>Indiana</option>
				  <option VALUE="Iowa"<%= practice_address.getStateString().equals("Iowa") ? " SELECTED" : "" %>>Iowa</option>
				  <option VALUE="Kansas"<%= practice_address.getStateString().equals("Kansas") ? " SELECTED" : "" %>>Kansas</option>
				  <option VALUE="Kentucky"<%= practice_address.getStateString().equals("Kentucky") ? " SELECTED" : "" %>>Kentucky</option>
				  <option VALUE="Louisiana"<%= practice_address.getStateString().equals("Louisiana") ? " SELECTED" : "" %>>Louisiana</option>
				  <option VALUE="Maine"<%= practice_address.getStateString().equals("Maine") ? " SELECTED" : "" %>>Maine</option>
				  <option VALUE="Maryland"<%= practice_address.getStateString().equals("Maryland") ? " SELECTED" : "" %>>Maryland</option>
				  <option VALUE="Massachusetts"<%= practice_address.getStateString().equals("Massachusetts") ? " SELECTED" : "" %>>Massachusetts</option>
				  <option VALUE="Michigan"<%= practice_address.getStateString().equals("Michigan") ? " SELECTED" : "" %>>Michigan</option>
				  <option VALUE="Minnesota"<%= practice_address.getStateString().equals("Minnesota") ? " SELECTED" : "" %>>Minnesota</option>
				  <option VALUE="Mississippi"<%= practice_address.getStateString().equals("Mississippi") ? " SELECTED" : "" %>>Mississippi</option>
				  <option VALUE="Missouri"<%= practice_address.getStateString().equals("Missouri") ? " SELECTED" : "" %>>Missouri</option>
				  <option VALUE="Montana"<%= practice_address.getStateString().equals("Montana") ? " SELECTED" : "" %>>Montana</option>
				  <option VALUE="Nebraska"<%= practice_address.getStateString().equals("Nebraska") ? " SELECTED" : "" %>>Nebraska</option>
				  <option VALUE="Nevada"<%= practice_address.getStateString().equals("Nevada") ? " SELECTED" : "" %>>Nevada</option>
				  <option VALUE="New Mexico"<%= practice_address.getStateString().equals("New Mexico") ? " SELECTED" : "" %>>New Mexico</option>
				  <option VALUE="New Jersey"<%= practice_address.getStateString().equals("New Jersey") ? " SELECTED" : "" %>>New Jersey</option>
				  <option VALUE="New York"<%= practice_address.getStateString().equals("New York") ? " SELECTED" : "" %>>New York</option>
				  <option VALUE="New Hampshire"<%= practice_address.getStateString().equals("New Hampshire") ? " SELECTED" : "" %>>New Hampshire</option>
				  <option VALUE="North Carolina"<%= practice_address.getStateString().equals("North Carolina") ? " SELECTED" : "" %>>North Carolina</option>
				  <option VALUE="North Dakota"<%= practice_address.getStateString().equals("North Dakota") ? " SELECTED" : "" %>>North Dakota</option>
				  <option VALUE="Ohio"<%= practice_address.getStateString().equals("Ohio") ? " SELECTED" : "" %>>Ohio</option>
				  <option VALUE="Oklahoma"<%= practice_address.getStateString().equals("Oklahoma") ? " SELECTED" : "" %>>Oklahoma</option>
				  <option VALUE="Oregon"<%= practice_address.getStateString().equals("Oregon") ? " SELECTED" : "" %>>Oregon</option>
				  <option VALUE="Pennsylvania"<%= practice_address.getStateString().equals("Pennsylvania") ? " SELECTED" : "" %>>Pennsylvania</option>
				  <option VALUE="Rhode Island"<%= practice_address.getStateString().equals("Rhode Island") ? " SELECTED" : "" %>>Rhode Island</option>
				  <option VALUE="South Carolina"<%= practice_address.getStateString().equals("South Carolina") ? " SELECTED" : "" %>>South Carolina</option>
				  <option VALUE="South Dakota"<%= practice_address.getStateString().equals("South Dakota") ? " SELECTED" : "" %>>South Dakota</option>
				  <option VALUE="Tennessee"<%= practice_address.getStateString().equals("Tennessee") ? " SELECTED" : "" %>>Tennessee</option>
				  <option VALUE="Texas"<%= practice_address.getStateString().equals("Texas") ? " SELECTED" : "" %>>Texas</option>
				  <option VALUE="Utah"<%= practice_address.getStateString().equals("Utah") ? " SELECTED" : "" %>>Utah</option>
				  <option VALUE="Virginia"<%= practice_address.getStateString().equals("Virginia") ? " SELECTED" : "" %>>Virginia</option>
				  <option VALUE="Vermont"<%= practice_address.getStateString().equals("Vermont") ? " SELECTED" : "" %>>Vermont</option>
				  <option VALUE="Washington"<%= practice_address.getStateString().equals("Washington") ? " SELECTED" : "" %>>Washington</option>
				  <option VALUE="West Virginia"<%= practice_address.getStateString().equals("West Virginia") ? " SELECTED" : "" %>>West Virginia</option>
				  <option VALUE="Wisconsin"<%= practice_address.getStateString().equals("Wisconsin") ? " SELECTED" : "" %>>Wisconsin</option>
				  <option VALUE="Wyoming"<%= practice_address.getStateString().equals("Wyoming") ? " SELECTED" : "" %>>Wyoming</option>
				</select>
				<input name="zipInput" onfocus="select();" value="<%= practice_address.getZipCodeString() %>" size="31" maxlength="20" class="inputbox" style="width: 70px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">This application requires that users log in using either an email address or a username along with a password.  <strong style="font-weight: bolder;">Please choose</strong> if you prefer to use email addresses or usernames to log into your practice.</div>

		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="radiobuttonsfrommarkup" class="right">
			    <div id="buttongroup1" class="yui-buttongroup">
				<input id="radio1" type="radio" name="log_in_method" value="Log in using Email Address"<%= (!settings.getLoginLabelString().equals("Username")) ? " checked" : "" %> />
				<input id="radio2" type="radio" name="log_in_method" value="Log in using Username"<%= settings.getLoginLabelString().equals("Username") ? " checked" : "" %> />
			    </div>
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide your email address</strong>.  If you selected to log in using a username, please provide a username also.</div>

		    <div class="adminItem">
			<div class="leftTM">EMAIL ADDRESS</div>
			<div class="right">
			    <input name="emailInput" onfocus="select();" value="<%= adminPerson.getEmail1String() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">USERNAME</div>
			<div class="right">
			    <input <%= (!settings.getLoginLabelString().equals("Username")) ? "disabled " : "" %>name="usernameInput" onfocus="select();" value="<%= (!settings.getLoginLabelString().equals("Username")) ? "" : adminPerson.getUsernameString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide your First and Last Name</strong>.  If you are a practitioner for this practice, make sure you <strong style="font-weight: bolder;">click the This User is a Practitioner button</strong>.</div>

		    <div class="adminItem">
			<div class="leftTM">FIRST / LAST NAME</div>
			<div class="right">
			    <input name="firstNameInput" onfocus="select();" value="<%= adminPerson.getFirstNameString() %>" size="31" maxlength="20" class="inputbox" style="width: 90px;" />
			    <input name="lastNameInput" onfocus="select();" value="<%= adminPerson.getLastNameString() %>" size="31" maxlength="30" class="inputbox" style="width: 113px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem" id="checkboxbuttonsfrommarkup">
			<div class="leftTM"></div>
			<div class="right">
			    <input id="checkbutton1" type="checkbox" name="is_practitioner" value="1"<%= isPractitioner.booleanValue() ? " checked" : "" %> />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">Please <strong style="font-weight: bolder;">provide a password</strong> that you'll also use to log in.  For security purposes, the password you type will be hidden.  Please <strong style="font-weight: bolder;">retype</strong> your password to ensure it's correct.</div>

		    <div class="adminItem">
			    <div class="leftTM">PASSWORD</div>
			    <div class="right"><input name="password" onfocus="select();" value="<%= adminPerson.getPasswordString() %>" size="37" maxlength="50" class="inputbox" style="width: 206px;" type="password" /></div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			    <div class="leftTM">RETYPE PASSWORD</div>
			    <div class="right"><input name="confirmPassword" onfocus="select();" value="<%= confirm_password_str %>" size="37" maxlength="50" class="inputbox" style="width: 206px;" type="password" /></div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">Valonyx allows you to accept Bitcoin as a payment type.  <strong style="font-weight: bolder;">Click BTC Payment Enabled</strong> if you would like to accept Bitcoins.</div>


		    <div class="adminItem" id="checkboxbuttonsfrommarkup4">
			<div class="leftTM"></div>
			<div class="right">
			    <input id="checkbutton4" type="checkbox" name="accept_btc" value="1"<%= qb_settings.doesAcceptBTC() ? " checked" : "" %> />
			</div>
			<div class="end"></div>
		    </div>
				
<%
if (is_sysadmin)
{
    
%>
		    <div class="adminItem" id="checkboxbuttonsfrommarkup2">
			<div class="leftTM"></div>
			<div class="right">
			    <input id="checkbutton2" type="checkbox" name="is_quickbooks" value="1"<%= qb_settings.isQuickBooksFSEnabled() ? " checked" : "" %> />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
				<div class="leftTM">QB USERNAME</div>
				<div class="right">
					<input name="qb_username" onfocus="select();" value="<%= qb_settings.getUserNameString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
				</div>
				<div class="end"></div>
		    </div>

		    <div class="adminItem">
				<div class="leftTM">QB PASSWORD</div>
				<div class="right">
					<input name="qb_password" onfocus="select();" value="<%= qb_settings.getPasswordString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
				</div>
				<div class="end"></div>
		    </div>

		    <div class="adminItem" id="checkboxbuttonsfrommarkup3">
				<div class="leftTM"></div>
				<div class="right">
					<input id="checkbutton3" type="checkbox" name="is_pos" value="1"<%= qb_settings.isPOSEnabled() ? " checked" : "" %> />
				</div>
				<div class="end"></div>
		    </div>

		    <div class="adminItem">
				<div class="leftTM">X_LOGIN</div>
				<div class="right">
					<input name="x_login" onfocus="select();" value="<%= qb_settings.getXLoginString() %>" size="31" maxlength="12" class="inputbox" style="width: 206px;" />
				</div>
				<div class="end"></div>
		    </div>

		    <div class="adminItem">
				<div class="leftTM">X_TRAN_KEY</div>
				<div class="right">
					<input name="x_tran_key" onfocus="select();" value="<%= qb_settings.getXTranKeyString() %>" size="31" maxlength="20" class="inputbox" style="width: 206px;" />
				</div>
				<div class="end"></div>
		    </div>
<%
}
if (settings.isSetupComplete())
{
%>
		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup" class="right">
			    <input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Submit" alt="Submit" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>
<%
}
else
{
%>
		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup" class="right">
			    <input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Previous" alt="Previous" style="margin-right: 10px; "/>
			    <input id="submitbutton2" class="formbutton" type="submit" name="submit_button" value="Next" alt="Next" style="margin-right: 10px; "/>
			    <input id="submitbutton3" disabled="true" class="formbutton" type="submit" name="submit_button" value="Finish" alt="Finish" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>
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
}
catch (Exception x)
{
    x.printStackTrace();
}
%>


</body>
</html>

