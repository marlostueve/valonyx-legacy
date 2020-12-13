<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="adminPaymentPlan" class="com.badiyan.uk.online.beans.PaymentPlanBean" scope="session" />

<%
if (request.getParameter("id") != null)
{
	adminPaymentPlan = PaymentPlanBean.getPaymentPlan(Integer.parseInt(request.getParameter("id")));
	session.setAttribute("adminPaymentPlan", adminPaymentPlan);
}
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

	<link rel="stylesheet" type="text/css"href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/resize.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css">
	<script src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>

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

<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<script type="text/javascript" src="../scripts/crir.js"></script>

	<title>Valonyx</title>

</head>

<body class=" yui-skin-sam" onload="javascript:initErrors();" >

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white; height: 100%;">
    <div id="content">

	<div class="content_TextBlock">
		<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
		<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

				<div class="content_Administration">

<%@ include file="channels\channel-setup-menu.jsp" %>

				<div class="main">

						<p class="headlineA">Add New Payment Plan</p>
						<p>Use this screen to add a new payment plan.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/updatePlan" focus="nameInput" >

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">PLAN NAME</div>
								<div class="right">
									<input name="nameInput" onfocus="select();" value="<%= adminPaymentPlan.getLabel() %>" size="31" maxlength="255" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">TYPE</div>
								<div class="right">
									<select name="typeSelect" style="width: 304px;">
										<option value="0">-- SELECT A PAYMENT PLAN TYPE --</option>
										<option value="<%= PaymentPlanBean.CASH_BILLING_TYPE %>"<%= (adminPaymentPlan.getType() == PaymentPlanBean.CASH_BILLING_TYPE) ? " selected" : "" %>>Cash Plan</option>
										<option value="<%= PaymentPlanBean.THIRD_PARTY_INSURANCE_BILLING_TYPE %>"<%= (adminPaymentPlan.getType() == PaymentPlanBean.THIRD_PARTY_INSURANCE_BILLING_TYPE) ? " selected" : "" %>>3rd Party Insurance Billing (Beacon)</option>
										<option value="<%= PaymentPlanBean.DIRECT_INSURANCE_BILLING_TYPE %>"<%= (adminPaymentPlan.getType() == PaymentPlanBean.DIRECT_INSURANCE_BILLING_TYPE) ? " selected" : "" %>>Direct Insurance Billing</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<p>Select a Practice Area that this is a payment plan for.</p>
							<div class="adminItem">
								<div class="leftTM">PRACTICE AREA</div>
								<div class="right">
<%
String selected_practice_area_value = "";
try
{
	selected_practice_area_value = adminPaymentPlan.getPracticeArea().getValue();
}
catch (Exception x)
{
}

try
{
    Iterator practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany).iterator();
    if (practice_areas.hasNext())
    {
%>
									<select name="practiceAreaSelect" style="width: 304px;">
										<option value="0">-- SELECT A PRACTICE AREA --</option>
<%
	while (practice_areas.hasNext())
	{
	    PracticeAreaBean practice_area_obj = (PracticeAreaBean)practice_areas.next();
%>
										<option value="<%= practice_area_obj.getValue() %>"<%= (selected_practice_area_value.equals(practice_area_obj.getValue())) ? " selected" : "" %>><%= practice_area_obj.getLabel() %></option>
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
								<div class="leftTM">PLAN COST</div>
								<div class="right">
									<input name="costInput" onfocus="select();" value="<%= adminPaymentPlan.getCostString() %>" size="31" maxlength="10" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">VISITS</div>
								<div class="right">
									<input name="visitsInput" onfocus="select();" value="<%= adminPaymentPlan.getVisits() %>" size="31" maxlength="3" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">MONTHLY PAYMENTS</div>
								<div class="right">
									<label for="allowMonthlyPaymentsId">Allow payments to be made monthly.  If not checked, plan must be fully paid in advance.</label>
									<input name="allowMonthlyPayments" id="allowMonthlyPaymentsId" type="checkbox" class="crirHiddenJS"<%= adminPaymentPlan.areMonthlyPaymentsAllowed() ? " checked=\"checked\"" : "" %>/>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">MONTHS TO PAY</div>
								<div class="right">
									<input name="monthsInput" onfocus="select();" value="<%= adminPaymentPlan.getMonthsToPay() %>" size="31" maxlength="3" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">VISIT POOLING</div>
								<div class="right">
									<label for="allowPoolingId">Pool visits among group under care members</label>
									<input name="allowPooling" id="allowPoolingId" type="checkbox" class="crirHiddenJS"<%= adminPaymentPlan.isGroupPoolingAllowed() ? " checked=\"checked\"" : "" %>/>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">&nbsp;</div>
								<div class="right">
									<input class="formbutton" type="submit" value="Update Payment Plan" alt="Update Payment Plan" /><input type="hidden" name="delete_id" value="0" />
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





	<div class="content_TextBlock">
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

    </div>


</div>


</body>
</html>

