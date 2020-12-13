<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminPractitioner" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />


<%

session.setAttribute("setup_02", "true");

CompanySettingsBean settings = adminCompany.getSettings();

if ((request.getParameter("id") != null))
{
    try
    {
		adminPractitioner = UKOnlinePersonBean.getPerson(adminCompany, Integer.parseInt(request.getParameter("id")));
		if (adminPractitioner.hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME))
			session.setAttribute("adminPractitioner", adminPractitioner);
		else
		{
			adminPractitioner = new UKOnlinePersonBean();
			session.setAttribute("adminPractitioner", adminPractitioner);
		}
		System.out.println("got here...");
    }
    catch (Exception x)
    {
		x.printStackTrace();
		adminPractitioner = new UKOnlinePersonBean();
		session.setAttribute("adminPractitioner", new UKOnlinePersonBean());
    }
}

if (!adminPractitioner.isActive() && !adminPractitioner.isNew())
{
	adminPractitioner.setActive(true);
	adminPractitioner.save();
}

boolean found_practitioners = false;

UKOnlinePersonBean.invalidatePractitioners(adminCompany);

String prac_message = "Setup the Practitioners for " + adminCompany.getLabel() + ".";
Vector practitioners = UKOnlinePersonBean.getPractitioners(adminCompany);
if (practitioners.size() == 1)
{
    UKOnlinePersonBean org_prac = (UKOnlinePersonBean)practitioners.get(0);
    if (org_prac.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
	prac_message = "You've already setup " + org_prac.getLabel() + " as a Practitioner.  Setup any additional Practitioners for " + adminCompany.getLabel() + ".";
}
if (practitioners.size() > 0)
    found_practitioners = true;

boolean is_administrator = adminPractitioner.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);

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

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        // "contentready" event handler for the "checkboxbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup2", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton2 = new Button("checkbutton2", { label: "This User Already Exists" });


        });


    }());

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        // "contentready" event handler for the "checkboxbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup3", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton3 = new Button("checkbutton3", { label: "Allow Client Scheduling" });


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
	    <p class="headline"><%= adminCompany.getLabel() %> <%= settings.isSetupComplete() ? "Administration" : "Setup" %> - <%= settings.isSetupComplete() ? "" : "Step 2: " %>Practitioners</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">

		<!-- <p class="headlineA"><%= settings.isSetupComplete() ? "" : "Step 2: " %>Practitioners</p> -->
		<p><strong style="font-weight: bolder;"><%= prac_message %><%= found_practitioners ? "" : "  You must create at least one Practitioner for your Practice." %></strong></p>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		<struts-html:form action="/admin/setup02" focus="firstNameInput">

		    <input type="hidden" name="delete_id" />
<%
if (!adminPractitioner.isNew())
{
%>
		    <div class="adminItem">You have selected to update <strong style="font-weight: bolder;"><%= adminPractitioner.getLabel() %></strong>.  When you are finished making changes, please click <strong style="font-weight: bolder;">Update Practitioner.</strong></div>
			<input type="hidden" name="already_exists" value="0" />
<%
} else {
	
	if (settings.isSetupComplete())
	{
%>
		    <div class="adminItem">If you'd like to make an existing user a practitioner, <strong style="font-weight: bolder;">click the This User Already Exists</strong>.  The provided email address must match the email address currently on file.</div>  

		    <div class="adminItem" id="checkboxbuttonsfrommarkup2">
				<div class="leftTM"></div>
				<div class="right">
					<input id="checkbutton2" type="checkbox" name="already_exists" value="1" />
				</div>
				<div class="end"></div>
		    </div>
			
<%
	} else {
%>
			<input type="hidden" name="already_exists" value="0" />
			<input type="hidden" name="allow_client_scheduling" value="0" />
<%
	}
}
%>
		    <div class="adminItem"><strong style="font-weight: bolder;">Provide the First and Last Name</strong> of the practitioner.</div>

		    <div class="adminItem">
			<div class="leftTM">FIRST/LAST NAME</div>
			<div class="right">
			    <input name="firstNameInput" onfocus="select();" value="<%= adminPractitioner.getFirstNameString() %>" size="31" maxlength="20" class="inputbox" style="width: 90px;" />
			    <input name="lastNameInput" onfocus="select();" value="<%= adminPractitioner.getLastNameString() %>" size="31" maxlength="30" class="inputbox" style="width: 113px;" />
			</div>
			<div class="end"></div>
		    </div>
<%
if (adminPractitioner.getId() != adminPerson.getId())
{
%>
		    <div class="adminItem">If you'd like to grant this Practitioner access to Practice Setup and Reports, <strong style="font-weight: bolder;">click the This User is an Administrator button</strong>.</div>

		    <div class="adminItem" id="checkboxbuttonsfrommarkup">
				<div class="leftTM"></div>
				<div class="right">
					<input id="checkbutton1" type="checkbox" name="is_administrator" value="1"<%= is_administrator ? " checked" : "" %> />
				</div>
				<div class="end"></div>
		    </div>
<%
}
else
{
%>
		    <input type="hidden" name="is_administrator" value="1" />
<%
}

if (settings.getLoginLabelString().equals("Username"))
{
%>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide a Username</strong> for this practitioner.  The email address is required, along with the password, to log in.  Also provide an email address for this practitioner if available.</div>

		    <div class="adminItem">
			<div class="leftTM">USERNAME</div>
			<div class="right">
			    <input <%= (!settings.getLoginLabelString().equals("Username")) ? "disabled " : "" %>name="usernameInput" onfocus="select();" value="<%= (!settings.getLoginLabelString().equals("Username")) ? "" : adminPractitioner.getUsernameString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
			</div>
			<div class="end"></div>
		    </div>
<%
}
else
{
%>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide an email address</strong> for this practitioner.  The email address is required, along with the password, to log in.</div>
<%
}
%>
		    <div class="adminItem">
			<div class="leftTM">EMAIL ADDRESS</div>
			<div class="right">
			    <input name="emailInput" onfocus="select();" value="<%= adminPractitioner.getEmail1String() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">Please <strong style="font-weight: bolder;">provide a password</strong> for this practitioner to use to log in.<%= adminPractitioner.isNew() ? "  You can use the randomly generated password if you'd like." : "" %></div>

		    <div class="adminItem">
			    <div class="leftTM">PASSWORD</div>
			    <div class="right">
				    <input name="password" onfocus="select();" value="<%= adminPractitioner.isNew() ? (PasswordGenerator.getPassword(3) + "-" + PasswordGenerator.getPassword(3)) : adminPractitioner.getPasswordString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
			    </div>
			    <div class="end"></div>
		    </div>
				
		    <div class="adminItem">If you'd like to allow your clients to request appointments online for this practitioner, <strong style="font-weight: bolder;">click the Allow Client Scheduling button</strong>.</div>  

		    <div class="adminItem" id="checkboxbuttonsfrommarkup3">
				<div class="leftTM"></div>
				<div class="right">
					<input id="checkbutton3" type="checkbox" name="allow_client_scheduling" value="1"<%= adminPractitioner.allowClientScheduling() ? " checked" : "" %> />
				</div>
				<div class="end"></div>
		    </div>
<%
if (adminCompany.getQuickBooksSettings().isPOSEnabled())
{
%>
		    <div class="adminItem">Please provide <strong style="font-weight: bolder;">Commission</strong> percentages for this practitioner for each department.</div>
<%
	//String[] arr = adminPractitioner.getDefaultCommissions();
	
	Iterator dept_itr = InventoryDepartment.getInventoryDepartments(adminCompany).iterator();
	while (dept_itr.hasNext())
	{
		InventoryDepartment dept_obj = (InventoryDepartment)dept_itr.next();
%>
		    <div class="adminItem">
			    <div class="leftTM"><%= dept_obj.getLabel().toUpperCase() %> COMMISSION</div>
			    <div class="right">
					<input name="commission_<%= dept_obj.getValue() %>" onfocus="select();" value="<%= adminPractitioner.getCommissionPercentageString(dept_obj) %>" class="inputbox" style="width: 66px;" />&nbsp;&#37;
			    </div>
			    <div class="end"></div>
		    </div>
<%
	}
}
%>
		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup2" class="right">
			    <input id="submitbutton4" class="formbutton" type="submit" name="submit_button" value="<%= adminPractitioner.isNew() ? "Add" : "Update" %> Practitioner" alt="<%= adminPractitioner.isNew() ? "Add" : "Update" %> Practitioner" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>
<%
if (found_practitioners)
{
%>
		    <div class="adminItem">Choose a Practitioner name from the list below to edit that Practitioner.</div>
<%
}
%>
		    <div class="content_AdministrationTable">

			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
			    <div class="heading">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">

					    <tr style="display: block;">
						    <td style="width:  20px; text-align: left;  "></td>
						    <td style="width: 472px; text-align: left;  ">Practitioners</td>
					    </tr>
				    </table>
			    </div>
			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%

Iterator itr = practitioners.iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
        UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
		if (!practitioner.isRoom()) {
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
}
else
{
%>
			    <div class="organization">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">
					    <tr>
						    <td style="width:  20px; text-align: left;  "></td>
						    <td style="width: 472px; text-align: left;  ">No Practitioners Found</td>
					    </tr>
				    </table>
			    </div>
<%
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
			    <input id="submitbutton2"<%= found_practitioners ? "" : " disabled=\"true\"" %> class="formbutton" type="submit" name="submit_button" value="Next" alt="Next" style="margin-right: 10px; "/>
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

