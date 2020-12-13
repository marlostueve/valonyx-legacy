<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%
int selected_practice_area_id = 0;
int selected_file_type_id = 0;
String charge = "";
if ((request.getParameter("practice_area_id") != null) && (request.getParameter("file_type_id") != null))
{
    //PracticeAreaBean practice_area_obj = PracticeAreaBean.getPracticeArea(Integer.parseInt(request.getParameter("practice_area_id")));
    //GroupUnderCareMemberTypeBean file_type_obj = GroupUnderCareMemberTypeBean.getMemberType(Integer.parseInt(request.getParameter("file_type_id")));
    selected_practice_area_id = Integer.parseInt(request.getParameter("practice_area_id"));
    selected_file_type_id = Integer.parseInt(request.getParameter("file_type_id"));
    charge = request.getParameter("charge");
}
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />

	<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" />
	<link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" />
	<script type="text/javascript" src="../yui/build/utilities/utilities.js"></script>
	<script type="text/javascript" src="../yui/build/button/button-beta.js"></script>
	<script type="text/javascript" src="../yui/build/container/container.js"></script>

	<script type="text/javascript" src="../../build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="../../build/element/element-beta.js"></script>

	<script type="text/javascript">
    
    var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var httpRequest;
    var request_NextDay;
    
    var edit = 0;

<%
Iterator type_itr = AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
while (type_itr.hasNext())
{
    AppointmentTypeBean appointmentType = (AppointmentTypeBean)type_itr.next();
%>
    typeArray["<%= appointmentType.getId() %>"] = {"bg" : "<%= appointmentType.getBGColorCodeString() %>", "txt" : "<%= appointmentType.getTextColorCodeString() %>", "duration" : "<%= appointmentType.getDurationString() %>" };
<%
}
%>

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

	<script type="text/javascript" src="../scripts/crir.js"></script>
	<title>Valonyx</title>

</head>

<body class="yui-skin-sam" >

<!-- <h1>Valeo Schedule</h1> -->

<%@ include file="channels/channel-schedule-menu.jsp" %>

<div id="content">
	

				<div class="content_TextBlock">
					<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Administration">

<%@ include file="channels\channel-admin-menu.jsp" %>

					<div class="main">

						<p class="headlineA">Practice Area Visit Charge</p>
						<p>Use this screen to add a new checkout code.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/visitChargeMap">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">FILE TYPE</div>
								<div class="right">
<%
Iterator itr = GroupUnderCareMemberTypeBean.getMemberTypes(adminCompany).iterator();
if (itr.hasNext())
{
%>
									<select name="fileTypeSelect" class="select" style="width: 309px;">
										<option value="0">-- SELECT A FILE TYPE --</option>
<%

    while (itr.hasNext())
    {
        GroupUnderCareMemberTypeBean file_type = (GroupUnderCareMemberTypeBean)itr.next();
%>
										<option value="<%= file_type.getValue() %>"<%= (file_type.getId() == selected_file_type_id) ? " SELECTED" : "" %>><%= file_type.getLabel() %></option>
<%
    }

%>
									</select>
<%
}
else
{
%>
								    No File Types Defined
<%
}
%>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">PRACTICE AREA</div>
								<div class="right">
<%
try
{
    Iterator practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany).iterator();
    if (practice_areas.hasNext())
    {
%>
									<select name="practiceAreaSelect" class="select" style="width: 309px;">
										<option value="0">-- NONE --</option>
<%
	while (practice_areas.hasNext())
	{
	    PracticeAreaBean practice_area_obj = (PracticeAreaBean)practice_areas.next();
%>
										<option value="<%= practice_area_obj.getValue() %>"<%= (practice_area_obj.getId() == selected_practice_area_id) ? " SELECTED" : "" %>><%= practice_area_obj.getLabel() %></option>
<%
	}
%>
									</select>
<%
    }
    else
    {
%>
									No Practice Areas Found
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">VISIT CHARGE</div>
								<div class="right">
									<input name="amountInput" onfocus="select();" value="<%= charge %>" size="31" maxlength="10" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input class="formbutton" type="submit" value="Add&nbsp;&#47;&nbsp;Update" alt="Add&nbsp;&#47;&nbsp;Update" style="margin-right: 4px;"/><input type="hidden" name="practice_area_delete_id" value="0" /><input type="hidden" name="file_type_delete_id" value="0" />
								</div>
								<div class="end"></div>
							</div>

						</struts-html:form>

						<div class="content_AdministrationTable">

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  ">Practice Area&nbsp;:&nbsp;File Type&nbsp;:&nbsp;Visit Charge</td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator charges = GroupUnderCareMemberTypeBean.getVisitCharges(adminCompany).iterator();
    if (charges.hasNext())
    {
	while (charges.hasNext())
	{
	    GroupUnderCareMemberPracticeAreaVisitCharge charge_obj = (GroupUnderCareMemberPracticeAreaVisitCharge)charges.next();
	    PracticeAreaBean practice_area = PracticeAreaBean.getPracticeArea(charge_obj.getPracticeAreaId());
	    GroupUnderCareMemberTypeBean file_type = GroupUnderCareMemberTypeBean.getMemberType(charge_obj.getGroupUnderCareMemberTypeId());
	    BigDecimal visit_charge = charge_obj.getVisitCharge();
	    String label = practice_area.getLabel() + "&nbsp;:&nbsp;" + file_type.getLabel() + "&nbsp;:&nbsp;" + visit_charge.toString();
%>
							<!-- BEGIN Department -->
							<div class="department">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="schedule-administration-file-type-practice-area-visit-charge.jsp?practice_area_id=<%= practice_area.getId() %>&file_type_id=<%= file_type.getId() %>&charge=<%= visit_charge.toString() %>" title=""><%= label %></a></td>
										<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].practice_area_delete_id.value=<%= practice_area.getId() %>;document.forms[0].file_type_delete_id.value=<%= file_type.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
									</tr>
								</table>
							</div>
							<!-- END Department -->
<%
	}
    }
    else
    {
%>
							<!-- BEGIN Department -->
							<div class="department">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colspan="3">No Charges Found</td>
									</tr>
								</table>
							</div>
							<!-- END Department -->
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

					</div>

					<div class="end"></div>
				</div>

				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

</div>

</body>

</html>
