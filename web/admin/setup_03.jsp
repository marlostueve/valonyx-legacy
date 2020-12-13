<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminStaff" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<%

session.setAttribute("setup_03", "true");

CompanySettingsBean settings = adminCompany.getSettings();

if ((request.getParameter("id") != null))
{
    try
    {
	adminStaff = UKOnlinePersonBean.getPerson(adminCompany, Integer.parseInt(request.getParameter("id")));
	if (adminStaff.hasRole(UKOnlineRoleBean.CASHIER_ROLE_NAME)) {
	    session.setAttribute("adminStaff", adminStaff);
	} else {
	    //adminStaff = new UKOnlinePersonBean();
		//adminStaff.addRole
	    session.setAttribute("adminStaff", adminStaff);
	}
	System.out.println("got here...");
    }
    catch (Exception x)
    {
	x.printStackTrace();
	adminStaff = new UKOnlinePersonBean();
	session.setAttribute("adminStaff", new UKOnlinePersonBean());
    }
}

String staff_message = "Setup any additional Staff members for " + adminCompany.getLabel() + ".";

boolean is_administrator = adminStaff.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);

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
	<link rel="stylesheet" type="text/css"href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css">
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

	    var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", { value: "Add Staff" });

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
	    <p class="headline"><%= adminCompany.getLabel() %> <%= settings.isSetupComplete() ? "Administration" : "Setup" %> - <%= settings.isSetupComplete() ? "" : "Step 3: " %>Staff</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">

		<!-- <p class="headlineA"><%= settings.isSetupComplete() ? "" : "Step 3: " %>Staff</p> -->
		<p><strong style="font-weight: bolder;"><%= staff_message %></strong></p>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		<struts-html:form action="/admin/setup03" focus="firstNameInput">

		    <input type="hidden" name="delete_id" />
<%
if (!adminStaff.isNew())
{
%>
		    <div class="adminItem">You have selected to update <strong style="font-weight: bolder;"><%= adminStaff.getLabel() %></strong>.  When you are finished making changes, please click <strong style="font-weight: bolder;">Update Staff Member.</strong></div>
<%
}
%>
		    <div class="adminItem"><strong style="font-weight: bolder;">Provide the First and Last Name</strong> for this staff member.</div>

		    <div class="adminItem">
			<div class="leftTM">FIRST/LAST NAME</div>
			<div class="right">
			    <input name="firstNameInput" onfocus="select();" value="<%= adminStaff.getFirstNameString() %>" size="31" maxlength="20" class="inputbox" style="width: 90px;" />
			    <input name="lastNameInput" onfocus="select();" value="<%= adminStaff.getLastNameString() %>" size="31" maxlength="30" class="inputbox" style="width: 113px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">If you'd like to grant this Staff member access to Practice Setup and Reports, <strong style="font-weight: bolder;">click the This User is an Administrator button</strong>.</div>

		    <div class="adminItem" id="checkboxbuttonsfrommarkup">
			<div class="leftTM"></div>
			<div class="right">
			    <input id="checkbutton1" type="checkbox" name="is_administrator" value="1"<%= is_administrator ? " checked" : "" %>>
			</div>
			<div class="end"></div>
		    </div>
<%
if (settings.getLoginLabelString().equals("Username"))
{
%>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide a Username</strong> for this staff member.  The email address is required, along with the password, to log in.  Also provide an email address if available.</div>

		    <div class="adminItem">
			<div class="leftTM">USERNAME</div>
			<div class="right">
			    <input <%= (!settings.getLoginLabelString().equals("Username")) ? "disabled " : "" %>name="usernameInput" onfocus="select();" value="<%= (!settings.getLoginLabelString().equals("Username")) ? "" : adminStaff.getUsernameString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
			</div>
			<div class="end"></div>
		    </div>
<%
}
else
{
%>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide an email address</strong> for this staff member.  The email address is required, along with the password, to log in.</div>
<%
}
%>
		    <div class="adminItem">
			<div class="leftTM">EMAIL ADDRESS</div>
			<div class="right">
			    <input name="emailInput" onfocus="select();" value="<%= adminStaff.getEmail1String() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">Please <strong style="font-weight: bolder;">provide a password</strong> for this staff member to use to log in.<%= adminStaff.isNew() ? "  You can use the randomly generated password if you'd like." : "" %></div>

		    <div class="adminItem">
			    <div class="leftTM">PASSWORD</div>
			    <div class="right">
				    <input name="password" onfocus="select();" value="<%= adminStaff.isNew() ? (PasswordGenerator.getPassword(3) + "-" + PasswordGenerator.getPassword(3)) : adminStaff.getPasswordString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup2" class="right">
			    <input id="submitbutton4" class="formbutton" type="submit" name="submit_button" value="<%= adminStaff.isNew() ? "Add" : "Update" %> Staff Member" alt="<%= adminStaff.isNew() ? "Add" : "Update" %> Staff Member" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>
<%
Vector staff_vec = UKOnlinePersonBean.getCashiers(adminCompany);
Iterator itr = staff_vec.iterator();
if (itr.hasNext())
{
%>
		    <div class="adminItem">Choose a Staff member from the list below to edit that Staff member.</div>
<%
}
%>
		    <div class="content_AdministrationTable">

			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
			    <div class="heading">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">

					    <tr style="display: block;">
						    <td style="width:  20px; text-align: left;  "></td>
						    <td style="width: 472px; text-align: left;  ">Staff</td>
					    </tr>
				    </table>
			    </div>
			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%

try {
if (itr.hasNext())
{
    while (itr.hasNext())
    {
        UKOnlinePersonBean staff = (UKOnlinePersonBean)itr.next();
%>
			    <!-- BEGIN Organization -->

			    <div class="organization">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">
					    <tr>
						    <td style="width:  20px; text-align: left;  "></td>
						    <td style="width: 402px; text-align: left;  "><a href="setup_03.jsp?id=<%= staff.getValue() %>" title=""><%= staff.getLabel() %></a></td>
						    <td style="width:  70px; text-align: center;  ">
<%
	//if (!staff.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
	//{
%>
							<a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= staff.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a>
<%
	//}
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
						    <td style="width: 472px; text-align: left;  ">No Staff Found</td>
					    </tr>
				    </table>
			    </div>
<%
}
}
catch (Exception x)
			{
	x.printStackTrace();
}
%>
			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		    </div>

<%
if (!settings.isSetupComplete())
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
%>


</body>
</html>

