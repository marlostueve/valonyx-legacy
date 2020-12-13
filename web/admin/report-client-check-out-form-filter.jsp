<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>

<%

		if (request.getParameter("id") != null) {
			try {
				adminPerson = (UKOnlinePersonBean) UKOnlinePersonBean.getPerson(Integer.parseInt(request.getParameter("id")));
				session.setAttribute("adminPerson", adminPerson);
			} catch (ObjectNotFoundException x) {
				x.printStackTrace();
			}
		}

		Vector codes = CheckoutCodeBean.getCheckoutCodes(adminCompany);
		GroupUnderCareBean group_under_care = null;
		Calendar now = Calendar.getInstance();
		SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");

%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<title>Valonyx</title>

		<link rel="stylesheet" type="text/css" href="../css/schedule-client.css" />
		<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />
		<link rel="stylesheet" type="text/css" href="../yui/build/resize/assets/skins/sam/resize.css" />
		<link rel="stylesheet" type="text/css" href="../yui/build/layout/assets/skins/sam/layout.css" />
		<link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" />

		<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />
		<link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" />
		<link rel="stylesheet" type="text/css" href="../yui/build/menu/assets/skins/sam/menu.css" />
		<link rel="stylesheet" type="text/css" href="../yui/build/calendar/assets/skins/sam/calendar.css" />
		<link rel="stylesheet" type="text/css" href="../yui/build/tabview/assets/skins/sam/tabview.css" />

		<script type="text/javascript" src="../yui/build/yahoo/yahoo-min.js"></script>
		<script type="text/javascript" src="../yui/build/event/event-min.js"></script>
		<script type="text/javascript" src="../yui/build/dom/dom-min.js"></script>

		<script type="text/javascript" src="../yui/build/element/element-beta-min.js"></script>
		<script type="text/javascript" src="../yui/build/dragdrop/dragdrop-min.js"></script>
		<script type="text/javascript" src="../yui/build/resize/resize-beta-min.js"></script>
		<script type="text/javascript" src="../yui/build/animation/animation-min.js"></script>
		<script type="text/javascript" src="../yui/build/layout/layout-beta-min.js"></script>

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

		<script type="text/javascript" src="../scripts/crir.js"></script>

		<script type="text/javascript">

			var httpRequest;

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

	</head>

	<%
		String searchLastName = (String) session.getAttribute("searchLastName");
		if (searchLastName == null) {
			searchLastName = "";
		}
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
						<p class="headline"><%= adminCompany.getLabel()%> Reports - Check Out Form Report</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
					</div>

					<div class="content_Administration">

<%@ include file="channels/channel-report-menu.jsp" %>

						<div class="main">

							<p>Use this screen to customize a report. Complete all the fields (multiple selection available)
								and then click <strong>View Report.</strong> When the report appears, click any column title to sort by
								that column. You will be able to download or print the report. To print it, use the <strong>Print</strong>
							button on your browser.</p>

							<struts-html:form action="/admin/clientCheckoutFormReportFilter">

								<input id="dummy" name="dummy" type="hidden" />
								<input name="status" type="hidden" value="0" />
								<input name="sort" type="hidden" value="1" />

								<div class="adminItem">
									<div class="leftTM">PRACTITIONER</div>
									<div class="right">
										<select name="practitionerSelect" class="select" style="width: 309px;">
											<option value="0">-- ALL PRACTITIONERS --</option>
<%
Iterator practitioners_itr = UKOnlinePersonBean.getPractitioners(adminCompany).iterator();
while (practitioners_itr.hasNext())
{
	UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners_itr.next();
%>
											<option value="<%= practitioner.getValue() %>"><%= practitioner.getLabel() %></option>
<%
}
%>
										</select>
									</div>
									<div class="end"></div>
								</div>


								<div class="adminItem">
									<div class="leftTM">START&nbsp;&#47;&nbsp;END DATE</div>
									<div class="right">
										<input name="start_date" onfocus="select();" value="<%= CUBean.getUserDateString(now.getTime())%>" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
										<img src="../images/gfxDash.gif" width="21" height="21" title="-" alt="-" style="_margin-top: 1px;" />
										<input name="end_date" onfocus="select();" value="<%= CUBean.getUserDateString(now.getTime())%>" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
									</div>
									<div class="end"></div>
								</div>


								<div class="adminItem">
									<div class="leftTM">START&nbsp;&#47;&nbsp;END TIME</div>
									<div class="right">
										<select name="start_hr">
											<option value="1">1</option>
											<option value="2">2</option>
											<option value="3">3</option>
											<option value="4">4</option>
											<option value="5">5</option>
											<option value="6">6</option>
											<option value="7">7</option>
											<option value="8">8</option>
											<option value="9">9</option>
											<option value="10">10</option>
											<option value="11">11</option>
											<option value="12">12</option>
										</select>
										<select name="start_mn">
											<option value="00">00</option>
											<option value="05">05</option>
											<option value="10">10</option>
											<option value="15">15</option>
											<option value="20">20</option>
											<option value="25">25</option>
											<option value="30">30</option>
											<option value="35">35</option>
											<option value="40">40</option>
											<option value="45">45</option>
											<option value="50">50</option>
											<option value="55">55</option>
										</select>
										<select name="start_ampm">
											<option value="<%= Calendar.AM %>">AM</option>
											<option value="<%= Calendar.PM %>">PM</option>
										</select>
										<img src="../images/gfxDash.gif" width="21" height="21" title="-" alt="-" style="_margin-top: 1px;" />

										<select name="end_hr">
											<option value="1">1</option>
											<option value="2">2</option>
											<option value="3">3</option>
											<option value="4">4</option>
											<option value="5">5</option>
											<option value="6">6</option>
											<option value="7">7</option>
											<option value="8">8</option>
											<option value="9">9</option>
											<option value="10">10</option>
											<option value="11">11</option>
											<option value="12">12</option>
										</select>
										<select name="end_mn">
											<option value="00">00</option>
											<option value="05">05</option>
											<option value="10">10</option>
											<option value="15">15</option>
											<option value="20">20</option>
											<option value="25">25</option>
											<option value="30">30</option>
											<option value="35">35</option>
											<option value="40">40</option>
											<option value="45">45</option>
											<option value="50">50</option>
											<option value="55">55</option>
										</select>
										<select name="end_ampm">
											<option value="<%= Calendar.AM %>">AM</option>
											<option value="<%= Calendar.PM %>">PM</option>
										</select>
									</div>
									<div class="end"></div>
								</div>

								<p>&nbsp;</p>

								<p>
									<input class="formbutton" type="submit" value="View Report" alt="View Report" />
								</p>

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
