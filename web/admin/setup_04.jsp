<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminPracticeArea" class="com.badiyan.uk.online.beans.PracticeAreaBean" scope="session" />


<%

session.setAttribute("setup_04", "true");

if (request.getParameter("id") != null)
{
    PracticeAreaBean obj = PracticeAreaBean.getPracticeArea(Integer.parseInt(request.getParameter("id")));
    CompanyBean company_obj = obj.getCompany();

    // ensure that this person is an admin for this company

    if (adminCompany.getId() == company_obj.getId())
    {
	adminPracticeArea = obj;
	session.setAttribute("adminPracticeArea", adminPracticeArea);
    }
}

String message = "<strong style=\"font-weight: bolder;\">Setup the Practice Areas for " + adminCompany.getLabel() + "</strong>.  If your practice offers both Chiropractic and Massage services, for example, these should both be practice areas.";

boolean has_practice_areas = false;
Vector practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany);
if (practice_areas.size() > 0)
    has_practice_areas = true;

CompanySettingsBean settings = adminCompany.getSettings();

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

	    var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", { value: "Add Practice Area" });

        });

    } ();

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
	    <p class="headline"><%= adminCompany.getLabel() %> <%= settings.isSetupComplete() ? "Administration" : "Setup" %> - <%= settings.isSetupComplete() ? "" : "Step 4: " %>Practice Areas</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">

		<!-- <p class="headlineA"><%= settings.isSetupComplete() ? "" : "Step 4: " %>Practice Areas</p> -->
		<p><%= message %><%= has_practice_areas ? "" : "  <strong style=\"font-weight: bolder;\">Your Practice must have at least one Practice Area.</strong>" %></p>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		<struts-html:form action="/admin/setup04" focus="nameInput">

		    <input type="hidden" name="delete_id" />
<%
if (!adminPracticeArea.isNew())
{
%>
		    <div class="adminItem">You have selected to update <strong style="font-weight: bolder;"><%= adminPracticeArea.getLabel() %></strong>.  When you are finished making changes, please click <strong style="font-weight: bolder;">Update Practice Area.</strong></div>
<%
}
%>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide the name</strong> for this Practice Area.  <strong style="font-weight: bolder;">Chiropractic</strong> or <strong style="font-weight: bolder;">Massage</strong>, for example.</div>

		    <div class="adminItem">
			    <div class="leftTM">PRACTICE AREA</div>
			    <div class="right">
				    <input name="nameInput" onfocus="select();" value="<%= adminPracticeArea.getLabel() %>" size="31" class="inputbox" maxlength="100" style="width: 206px; margin-right: 4px;" />
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">You can create practice areas that are children of other practice areas.  For example, Myofascial and CranioSacral Therapy could be children of Massage.  If this Practice Area has no parent, use the default <strong style="font-weight: bolder;">NO PARENT</strong> option.</div>

		    <div class="adminItem">
			    <div class="leftTM">PARENT</div>
			    <div class="right">
				    <select name="parent" class="select" style="width: 309px;">
					    <option value="0">NO PARENT (ROOT PRACTICE AREA)</option>
<%

try
{
    Iterator practice_areas_itr = practice_areas.iterator();
    while (practice_areas_itr.hasNext())
    {
        PracticeAreaBean obj = (PracticeAreaBean)practice_areas_itr.next();
%>
					    <option value="<%= obj.getValue() %>"<%= (obj.getId() == adminPracticeArea.getParentId()) ? " selected" : "" %>><%= obj.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
}
%>
				    </select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem"><strong style="font-weight: bolder;">Select the Practitioners</strong> that practice within this Practice Area.  You can select multiple practitioners by <strong style="font-weight: bolder;">holding down the Ctrl</strong> key on your keyboard <strong style="font-weight: bolder;">while you click</strong> on the Practitioners in the list.</div>

		    <div class="adminItem">
			    <div class="leftTM">PRACTITIONERS/ROOMS</div>
			    <div class="right">
					
				<select name="practitionerSelect" class="multipleSelect" size="10" multiple="multiple" style="width: 309px;">
					    <!-- <option value="0">-- ALL PRACTITIONERS --</option> -->
<%
try
{
    Iterator itr = UKOnlinePersonBean.getPersons(adminCompany, UKOnlineRoleBean.PRACTITIONER_ROLE_NAME).iterator();
	
	Iterator practitioner_itr = PracticeAreaBean.getAllPractitioners().iterator();
				for (int i = 2; practitioner_itr.hasNext(); i++) {
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
					String room_label = practitioner.getFirstNameString();
					if (practitioner.isRoom()) {
						room_label = practitioner.getLastNameString();
					}
					if (room_label.equals("Christine")) {
						room_label = "Christine - TX1";
					} else if (room_label.equals("TX 1")) {
						//room_label = "Lisa - TX3";
						continue;
					} else if (room_label.equals("Kevin")) {
						room_label = "POD 1";
					} else if (room_label.equals("TX 4")) {
						room_label = "Bemer - TX4";
					} else if (room_label.equals("TX4")) {
						room_label = "Bemer - TX4";
					} else if (room_label.equals("Lisa")) {
						//room_label = "Lisa - TX2";
						continue;
					} else if (room_label.equals("Leah")) {
						room_label = "Leah - TX3";
					} else if (room_label.equals("Lesya")) {
						room_label = "Lesya - BW2";
					} else if (room_label.equals("Katie")) {
						room_label = "POD - BW1";
					} else if (room_label.equals("Counseling(C) - TX2")) {
						continue;
					} else if (room_label.equals("Counseling(M) - TX2")) {
						continue;
					} else if (room_label.equals("Neuro - TX4")) {
						room_label = "Angie NF - TX4";
					} else if (room_label.equals("Sauna 1")) {
						room_label = "POD 2";
					} else if (room_label.equals("Sauna 2")) {
						continue;
					} else if (room_label.equals("Bioscan 1")) {
						continue;
					}
					
					
					//if (needs_comma) { b.append(','); }
					//b.append(this.toJSONDropdown(room_label, practitioner.getValue()));
					//b.append("{\"title\":\"" + JSONObject.escape(room_label) + "\",\"id\":" + practitioner.getId() + "}");
					//needs_comma = true;
					
%>
				<option value="<%= practitioner.getValue() %>"<%= adminPracticeArea.contains(practitioner) ? " SELECTED" : "" %>><%= room_label %></option>
<%

				}
	
    if (itr.hasNext() && (1 == 2))
    {
%>
				    <select name="practitionerSelect" class="multipleSelect" size="10" multiple="multiple" style="width: 309px;">
					    <!-- <option value="0">-- ALL PRACTITIONERS --</option> -->
<%
	while (itr.hasNext())
	{
	    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
%>
					<option value="<%= practitioner.getValue() %>"<%= adminPracticeArea.contains(practitioner) ? " SELECTED" : "" %>><%= practitioner.getLabel() %></option>
<%
	}
%>
				    </select>
<%
    }
	else if (1 == 2)
    {
%>
				    <strong style="font-weight: bolder;">No Practitioners Found</strong>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>

				    </select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup2" class="right">
			    <input id="submitbutton4" class="formbutton" type="submit" name="submit_button" value="<%= adminPracticeArea.isNew() ? "Add" : "Update" %> Practice Area" alt="<%= adminPracticeArea.isNew() ? "Add" : "Update" %> Practice Area" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>
<%
if (has_practice_areas)
{
%>
		    <div class="adminItem">Choose a Practice Area from the list below to edit that Practice Area.</div>
<%
}
%>
		    <div class="content_AdministrationTable">

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			<div class="heading">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr style="display: block;">
						<td style="width:  20px; text-align: left;  "></td>
						<td style="width: 402px; text-align: left;  ">Practice Area</td>
						<td style="width:  70px; text-align: center;  "></td>
					</tr>
				</table>
			</div>
			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator practice_areas_itr = practice_areas.iterator();
    if (practice_areas_itr.hasNext())
    {
	while (practice_areas_itr.hasNext())
	{
	    PracticeAreaBean obj = (PracticeAreaBean)practice_areas_itr.next();
%>
			<!-- BEGIN Job Title -->
			<div class="jobTitle">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr>
						<td style="width:  20px; text-align: left;  "></td>
						<td style="width: 402px; text-align: left;  "><a href="setup_04.jsp?id=<%= obj.getValue() %>" title=""><%= obj.getLabel() %></a></td>
						<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= obj.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
					</tr>
				</table>
			</div>
			<!-- END Job Title -->
<%
	}
    }
    else
    {
%>
			<!-- BEGIN Job Title -->
			<div class="jobTitle">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr>
						<td style="width: 492px; text-align: left;  " colspan="3">No Practice Areas Found</td>
					</tr>
				</table>
			</div>
			<!-- END Job Title -->
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

		    </div>

<%
if (!settings.isSetupComplete())
{
%>
		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup" class="right">
			    <input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Previous" alt="Previous" style="margin-right: 10px; "/>
			    <input id="submitbutton2"<%= has_practice_areas ? "" : " disabled=\"true\"" %> class="formbutton" type="submit" name="submit_button" value="Next" alt="Next" style="margin-right: 10px; "/>
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

