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
		Calendar month_ago = Calendar.getInstance();
		month_ago.add(Calendar.MONTH, -1);
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

			function showPeople(xml_str)
			{
				document.getElementById('ci_clientSelect').options.length = 0;

				var index = 0;
				while (xml_str.getElementsByTagName("person")[index] != null)
				{
					key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
					value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
					eval("var personArray = " + value);

					document.getElementById('ci_clientSelect').options[index] = new Option(personArray["label"],key);

					index++;
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

<%@ include file="..\channels\channel-head-javascript.jsp" %>

	</head>

	<%
		String searchLastName = (String) session.getAttribute("searchLastName");
		if (searchLastName == null) {
			searchLastName = "";
		}
	%>

<body class="yui-skin-sam" onload="javascript:initErrors();">
    
    <div id="content">

	<div id="top1">
<%@ include file="channels/channel-schedule-menu.jsp" %>
	</div>

	<div id="center1" style="background-color: white;">

	    <div id="content">


		    <div class="content_TextBlock">
			    <p class="headline"><%= adminCompany.getLabel()%> Reports - Client Statement</p>
			    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
		    </div>

		    <div class="content_Administration">

<%@ include file="channels/channel-report-menu.jsp" %>

		    <div class="main">

			    <p>Use this screen to customize a report. Complete all the fields (multiple selection available)
				    and then click <strong>View Report.</strong> When the report appears, click any column title to sort by
				    that column. You will be able to download or print the report. To print it, use the <strong>Print</strong>
			    button on your browser.</p>

			    <struts-html:form action="/admin/statementReportFilter">

				    <input id="dummy" name="dummy" type="hidden" />
				    <input name="status" type="hidden" value="0" />
				    <input name="sort" type="hidden" value="1" />

				    <div class="adminItem">
					    <div class="leftTM">CLIENT</div>
					    <div class="right">
						    <table border="0" cellpadding="0" cellspacing="0">
							    <tr>
								    <td>
									    <strong>Search Last Name:</strong><br />
									    <input style="width: 200px;" id="ci_lastname" type="textbox" onkeyup="if (document.getElementById('ci_lastname').value.length > 1) {processCommand('getPeopleByLastName', document.getElementById('ci_lastname').value);}" name="ci_lastname" /><br />
									    <strong>Search First / File Number:</strong><br />
									    <input style="width: 120px;" id="ci_firstname" type="textbox" onkeyup="if (document.getElementById('ci_firstname').value.length > 1) {processCommand('getPeopleByFirstName', document.getElementById('ci_firstname').value);}" name="ci_firstname" />&nbsp;/&nbsp;<input id="ci_filenumber" type="textbox" onkeyup="if (document.getElementById('ci_filenumber').value.length > 0) {processCommand('getPeopleByFileNumber', document.getElementById('ci_filenumber').value);}" name="ci_filenumber" style="width: 68px;" />

									    <div class="clear"></div>

									    <select size="4" id="ci_clientSelect" name="clientSelect" multiple="false" style="width: 309px;" >
									    <option value="-1">-- SEARCH FOR A CLIENT --</option>
									    </select>
								    </td>
							    </tr>
						    </table>
					    </div>
					    <div class="end"></div>
				    </div>

				    <div class="adminItem">
					    <div class="leftTM">START&nbsp;&#47;&nbsp;END DATE</div>
					    <div class="right">
						    <input name="start_date" onfocus="select();" value="<%= CUBean.getUserDateString(month_ago.getTime())%>" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
						    <img src="../images/gfxDash.gif" width="21" height="21" title="-" alt="-" style="_margin-top: 1px;" />
						    <input name="end_date" onfocus="select();" value="<%= CUBean.getUserDateString(now.getTime())%>" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
					    </div>
					    <div class="end"></div>
				    </div>

				    <div class="adminItem">
					    <div class="leftTM">TYPE</div>
					    <div class="right">
						    <select name="typeSelect" multiple="true" size="4" style="width: 304px;">
							    <option value="0" selected>-- ALL PRACTICE AREAS --</option>
							    <option value="<%= CheckoutCodeBean.PROCEDURE_TYPE %>">Procedures</option>
							    <option value="<%= CheckoutCodeBean.GROUP_TYPE %>">Group</option>
							    <option value="<%= CheckoutCodeBean.INVENTORY_TYPE %>">Inventory</option>
							    <option value="<%= CheckoutCodeBean.PAYMENT_TYPE %>">Payment</option>
						    </select>
						    <input type="hidden" name="practice_areas" value="0">
					    </div>
					    <div class="end"></div>
				    </div>
<!--
				    <div class="adminItem">
					    <div class="leftTM">PRACTICE AREAS</div>
					    <div class="right">
						    <table border="0" cellpadding="0" cellspacing="0">
							    <tr>
								    <td>
									    <select size="6" name="practice_areas" multiple="true" style="width: 309px;">
									    <option value="0" selected>-- ALL PRACTICE AREAS</option>
<%
Iterator practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany).iterator();
while (practice_areas.hasNext())
{
	PracticeAreaBean practice_area_obj = (PracticeAreaBean)practice_areas.next();
%>
									    <option value="<%= practice_area_obj.getValue() %>"><%= practice_area_obj.getLabel() %></option>
<%
}
%>
									    </select>
								    </td>
							    </tr>
						    </table>
					    </div>
					    <div class="end"></div>
				    </div>
-->
				    <div class="adminItem">
					    <div class="leftTM">&nbsp;</div>
					    <div class="right">
						    <input class="formbutton" type="submit" value="View Report" alt="View Report" />
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
