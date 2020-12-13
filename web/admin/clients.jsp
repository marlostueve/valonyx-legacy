<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*;" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<%
try
{
if (request.getParameter("id") != null)
{
	try
	{
		adminPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(request.getParameter("id")));
		session.setAttribute("adminPerson", adminPerson);
	}
	catch (ObjectNotFoundException x)
	{
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
<link rel="stylesheet" type="text/css" href="../css/checkout.css" />

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


<script type="text/javascript" src="../yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../yui/build/container/container_core.js"></script>
<script type="text/javascript" src="../yui/build/menu/menu.js"></script>
<script type="text/javascript" src="../yui/build/element/element-beta.js"></script>
<script type="text/javascript" src="../yui/build/button/button-beta.js"></script>

<script type="text/javascript" src="../yui/build/utilities/utilities.js"></script>
<script type="text/javascript" src="../yui/build/container/container.js"></script>
<script type="text/javascript" src="../yui/build/calendar/calendar.js"></script>
<script type="text/javascript" src="../yui/build/tabview/tabview.js"></script>


<style type="text/css">

div.tableContainer2 {clear: both;border: 1px solid #963;	height: 450px;	overflow: auto;	width: 100%;}
div.tableContainer2 table {float: left;width: 100%}
div.tableContainer2 table {margin: 0 -16px 0 0}
thead.fixedHeader2 tr {position: relative;top: expression(document.getElementById("tableContainer2").scrollTop);}
head:first-child+body thead[class].fixedHeader2 tr {display: block;}
thead.fixedHeader2 th {background: #C96;border-left: 1px solid #EB8;border-right: 1px solid #B74;border-top: 1px solid #EB8;font-weight: normal;	padding: 4px 3px;text-align: left; font-weight: bold;}
thead.fixedHeader2 a, thead.fixedHeader2 a:link, thead.fixedHeader2 a:visited {color: #FFF;display: block;text-decoration: none;width: 100%}
thead.fixedHeader2 a:hover {color: #FFF;	display: block;	text-decoration: underline;width: 100%}
head:first-child+body tbody[class].scrollContent2 {display: block;height: 425px;overflow: auto;width: 100%}
tbody.scrollContent2 td, tbody.scrollContent2 tr.normalRow td {border-bottom: none;border-left: none;border-right: none;border-top: none;padding: 0px 1px 1px 1px; vertical-align: top; }
tbody.scrollContent2 a, tbody.scrollContent2 a:link, tbody.scrollContent2 a:visited {color: #FFF;text-decoration: none}
tbody.scrollContent2 a:hover {color: #FFF;text-decoration: underline}
head:first-child+body thead[class].fixedHeader2 th {width: 200px}
head:first-child+body thead[class].fixedHeader2 th + th {width: 240px}
head:first-child+body thead[class].fixedHeader2 th + th + th {border-right: none;padding: 4px 4px 4px 3px;width: 316px}
head:first-child+body tbody[class].scrollContent2 td {width: 200px}
head:first-child+body tbody[class].scrollContent2 td + td {width: 240px}
head:first-child+body tbody[class].scrollContent2 td + td + td {border-right: none;padding: 2px 4px 2px 3px;width: 300px}

div.tableContainer3, div.tableContainer4 {clear: both;border: 1px solid #963;	height: 450px;	overflow: auto;	width: 100%;}
div.tableContainer3 table, div.tableContainer4 table {float: left;width: 100%}
div.tableContainer3 table, div.tableContainer4 table {margin: 0 -16px 0 0}
thead.fixedHeader3 tr {position: relative;top: expression(document.getElementById("tableContainer3").scrollTop);}
thead.fixedHeader4 tr {position: relative;top: expression(document.getElementById("tableContainer4").scrollTop);}
head:first-child+body thead[class].fixedHeader3 tr, head:first-child+body thead[class].fixedHeader4 tr {display: block;}
thead.fixedHeader3 th, thead.fixedHeader4 th {background: #C96;border-left: 1px solid #EB8;border-right: 1px solid #B74;border-top: 1px solid #EB8;font-weight: normal;	padding: 4px 3px;text-align: left; font-weight: bold;}
thead.fixedHeader3 a, thead.fixedHeader3 a:link, thead.fixedHeader3 a:visited, thead.fixedHeader4 a, thead.fixedHeader4 a:link, thead.fixedHeader4 a:visited {color: #FFF;display: block;text-decoration: none;width: 100%}
thead.fixedHeader3 a:hover, thead.fixedHeader4 a:hover {color: #FFF;	display: block;	text-decoration: underline;width: 100%}
head:first-child+body tbody[class].scrollContent3, head:first-child+body tbody[class].scrollContent4 {display: block;height: 425px;overflow: auto;width: 100%}
tbody.scrollContent3 td, tbody.scrollContent3 tr.normalRow td, tbody.scrollContent4 td, tbody.scrollContent4 tr.normalRow td {border-bottom: none;border-left: none;border-right: none;border-top: none;padding: 0px 1px 1px 1px; vertical-align: top; }
tbody.scrollContent3 a, tbody.scrollContent3 a:link, tbody.scrollContent3 a:visited, tbody.scrollContent4 a, tbody.scrollContent4 a:link, tbody.scrollContent4 a:visited {color: #FFF;text-decoration: none}
tbody.scrollContent3 a:hover, tbody.scrollContent4 a:hover {color: #FFF;text-decoration: underline}
head:first-child+body thead[class].fixedHeader3 th, head:first-child+body thead[class].fixedHeader4 th {width: 200px}
head:first-child+body thead[class].fixedHeader3 th + th, head:first-child+body thead[class].fixedHeader4 th + th {width: 240px}
head:first-child+body thead[class].fixedHeader3 th + th + th, head:first-child+body thead[class].fixedHeader4 th + th + th {border-right: none;padding: 4px 4px 4px 3px;width: 316px}
head:first-child+body tbody[class].scrollContent3 td, head:first-child+body tbody[class].scrollContent4 td {width: 200px}
head:first-child+body tbody[class].scrollContent3 td + td, head:first-child+body tbody[class].scrollContent4 td + td {width: 240px}
head:first-child+body tbody[class].scrollContent3 td + td + td, head:first-child+body tbody[class].scrollContent4 td + td + td {border-right: none;padding: 2px 4px 2px 3px;width: 300px}

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


<script type="text/javascript">

    var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var httpRequest;
    var request_NextDay;

    var edit = 0;

    var show_group_names = false;
    var show_assign_names = false;
    var show_referral_names = false;
    var show_referral_names2 = false;

    var codeArray = new Array();

    var initial = true;

    var last_order = -1;
    var iteration = 0;
    var selected_order = 0;

<%
Iterator c_itr = codes.iterator();
while (c_itr.hasNext())
{
    CheckoutCodeBean code = (CheckoutCodeBean)c_itr.next();
%>
    codeArray["<%= code.getId() %>"] = {"id" : "<%= code.getId() %>", "code" : "<%= code.getCode() %>", "desc" : "<%= code.getDescription() %>", "label" : "<%= code.getLabel() %>", "amount" : "<%= code.getAmountString() %>" };
<%
}
%>

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
				else if (httpRequest.responseXML.getElementsByTagName("nfn").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("nfn")[0];
					document.forms[1].file_number.value = xml_response.getAttribute("num");
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
	if (show_referral_names)
	    document.forms['newClientForm'].referralClientSelect.options.length = 0;
	else if (show_referral_names2)
	    document.forms['clientProfileForm'].referralProfileSelect.options.length = 0;
	else if (show_assign_names)
	    document.forms['assignGroupUnderCareForm'].groupSelect.options.length = 0;
	else if (show_group_names)
	    document.forms['newClientForm'].groupSelect.options.length = 0;
	else
	    document.getElementById('clientSelect').options.length = 0;

	var index = 0;
	if (xml_str.getElementsByTagName("person")[index] != null)
	{
	    while (xml_str.getElementsByTagName("person")[index] != null)
	    {
		key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
		value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
		eval("var personArray = " + value);

		if (show_referral_names)
		    document.forms['newClientForm'].referralClientSelect.options[index] = new Option(personArray["label"],key);
		else if (show_referral_names2)
		    document.forms['clientProfileForm'].referralProfileSelect.options[index] = new Option(personArray["label"],key);
		else if (show_assign_names)
		    document.forms['assignGroupUnderCareForm'].groupSelect.options[index] = new Option(personArray["label"],key);
		else if (show_group_names)
		    document.forms['newClientForm'].groupSelect.options[index] = new Option(personArray["label"],key);
		else
		    document.getElementById('clientSelect').options[index] = new Option(personArray["label"],key);

		index++;
	    }
	}
	else
	{
	    if (show_referral_names)
		document.forms['newClientForm'].referralClientSelect.options[index] = new Option('No Clients Found',-1);
	    else if (show_referral_names2)
		document.forms['clientProfileForm'].referralProfileSelect.options[index] = new Option('No Clients Found',-1);
	    else if (show_assign_names)
		document.forms['assignGroupUnderCareForm'].groupSelect.options[index] = new Option('No Clients Found',-1);
	    else if (show_group_names)
		document.forms['newClientForm'].groupSelect.options[index] = new Option('No Clients Found',-1);
	    else
		document.getElementById('clientSelect').options[index] = new Option('No Clients Found',-1);
	}

	show_group_names = false;
	show_assign_names = false;
	show_referral_names = false;
	show_referral_names2 = false;

	if (initial)
	{
	    refresh();
	    initial = false;
	}
    }

    function showPerson(xml_str)
    {
	//var key = xml_str.childNodes[0].text;
	//var value = xml_str.childNodes[1].text;

	//.childNodes[0].nodeValue;

	var key = xml_str.childNodes[0].childNodes[0].nodeValue;
	var value = xml_str.childNodes[1].childNodes[0].nodeValue;

	eval("var personArray = " + value);

	document.getElementById('selectedPerson').firstChild.nodeValue = personArray["label"];

	if (personArray["pt"] == "<%= UKOnlinePersonBean.PROSPECT_PERSON_TYPE%>")
	{
	    document.forms[1].c_prospect.checked = true;
	    document.forms[1].hidden_filenumber.value=document.forms[1].file_number.value;
	    document.forms[1].file_number.value='';
	    document.forms[1].file_number.disabled=true;
	}
	else
	{
	    document.forms[1].c_prospect.checked = false;
	    document.forms[1].file_number.disabled=false;
	    document.forms[1].file_number.value=document.forms[1].hidden_filenumber.value;
	}

	document.forms[1].person_id.value = personArray["id"];
	document.forms[1].file_number.value = personArray["file"];
	document.forms[1].prefix.value = personArray["prefix"];
	document.forms[1].firstname.value = personArray["fn"];
	document.forms[1].middle.value = personArray["mn"];
	document.forms[1].lastname.value = personArray["ln"];
	document.forms[1].suffix.value = personArray["suffix"];

	document.getElementById('n_appt').firstChild.nodeValue = personArray["n_appt"];
	document.getElementById('l_appt').firstChild.nodeValue = personArray["l_appt"];
	document.forms[1].pos_id.value = personArray["pos_id"];
	document.forms[1].qb_id.value = personArray["qb_id"];
	document.forms[1].address1.value = personArray["addr1"];
	document.forms[1].address2.value = personArray["addr2"];
	document.forms[1].city.value = personArray["city"];
	document.forms[1].state.value = personArray["state"];
	document.forms[1].zipcode.value = personArray["zip"];

	document.forms[1].ssn.value = personArray["ssn"];
	document.forms[1].phone.value = personArray["phone"];
	document.forms[1].cell.value = personArray["cell"];
	document.forms[1].email.value = personArray["email"];
	document.forms[1].dob.value = personArray["dob"];

	if (personArray["gender"] == "true")
	    document.forms[1].gender[0].checked = true;
	else
	    document.forms[1].gender[1].checked = true;

	if (personArray["deceased"] == "true")
	    document.forms[1].deceased.checked = true;
	else
	    document.forms[1].deceased.checked = false;

	if (personArray["sel"] == "true")
	{
	    document.getElementById('lastname').value = personArray["ln"];
	    document.getElementById('clientSelect').options.length = 0;
	    document.getElementById('clientSelect').options[0] = new Option(personArray["label"],key);
	    document.getElementById('clientSelect').selectedIndex = 0;
	}

	document.forms[1].client_type.value = personArray["ct"];
	document.forms[1].file_type.value = personArray["ft"];

	document.forms[1].groupSelect.options.length = 0;
	var index = 0;
	if (xml_str.getElementsByTagName("person")[index] != null)
	{
	    while (xml_str.getElementsByTagName("person")[index] != null)
	    {
		key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
		value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
		eval("arr = " + value);
		document.forms[1].groupSelect.options[index] = new Option(arr["label"] + ' (' + arr["relate"] + ')',key);
		index++;
	    }
	}
	else
	    document.forms[1].groupSelect.options[index] = new Option('Group Not Found',-1);


	document.forms[1].marketingProfileSelect.value = personArray["mc"];

	document.forms[1].referralProfileSelect.options.length = 0;
	if (personArray["rc"] == "0")
	    document.forms[1].referralProfileSelect.options[0] = new Option('-- SEARCH FOR A CLIENT --',0);
	else
	{
	    document.forms[1].referralProfileSelect.options[0] = new Option(personArray["rcl"],personArray["rc"]);
	    document.forms[1].referralProfileSelect.selectedIndex = 0;
	}

	document.assignGroupUnderCareForm.clientSelect.value = personArray["id"];

	refresh();
    }

    function showPurchasedPlans(xml_str)
    {
		//alert('showPurchasedPlans');
		var select_element = document.getElementById('planSelect');
		select_element.options.length = 0;
		var index = 0;
		if (xml_str.getElementsByTagName("payment-plan-instance")[index] != null)
		{
			while (xml_str.getElementsByTagName("payment-plan-instance")[index] != null)
			{
				key = xml_str.getElementsByTagName("payment-plan-instance")[index].childNodes[0].childNodes[0].nodeValue;
				value = xml_str.getElementsByTagName("payment-plan-instance")[index].childNodes[1].childNodes[0].nodeValue;
				eval("var paymentPlanArray = " + value);

				select_element.options[index] = new Option(paymentPlanArray["label"],key);

				index++;
			}
		}
		else
			select_element.options[index] = new Option('No Plans Purchased',-1);

		var ledger = document.getElementById('tblLedger');
			deleteAllRows(ledger);

		index = 0;
		if (xml_str.getElementsByTagName("order")[index] != null)
		{
			document.getElementById('ledgerTotal').firstChild.nodeValue = xml_str.getElementsByTagName("ledger")[0].getAttribute("total");
			document.getElementById('ledgerPatient').firstChild.nodeValue = xml_str.getElementsByTagName("ledger")[0].getAttribute("client");
			document.getElementById('ledgerBalance').firstChild.nodeValue = xml_str.getElementsByTagName("ledger")[0].getAttribute("balance");

			while (xml_str.getElementsByTagName("order")[index] != null)
			{
				key = xml_str.getElementsByTagName("order")[index].childNodes[0].childNodes[0].nodeValue;
				value = xml_str.getElementsByTagName("order")[index].childNodes[1].childNodes[0].nodeValue;
				//alert(value);
				eval("var orderArray = " + value);

				var status;
				var c = "";
				/*
				if (orderArray["paid"] == 'false')
					status = "OPEN";
				else
					status = "CLOSED";
				if (orderArray["billed"] == 'true')
					status = status + ", BILLED";
				if (orderArray["collect"] == 'true')
					c = "*";
				*/

				for (var line_index = 0; xml_str.getElementsByTagName("order")[index].childNodes[2].childNodes[line_index] != null; line_index++)
				{
					orderline_key = xml_str.getElementsByTagName("order")[index].childNodes[2].childNodes[line_index].childNodes[0].childNodes[0].nodeValue;
					orderline_value = xml_str.getElementsByTagName("order")[index].childNodes[2].childNodes[line_index].childNodes[1].childNodes[0].nodeValue;
					//alert(orderline_value);
					eval("var orderlineArray = " + orderline_value);

					//addLedgerRow('tblLedger', orderArray["value"], orderArray["date"], orderlineArray["code"], orderlineArray["desc"], orderlineArray["amount"], orderlineArray["total"], orderlineArray["patient"], orderlineArray["balance"], status, c);
					addLedgerRow('tblLedger', orderArray["value"], orderArray["date"], orderlineArray["code"], orderlineArray["desc"], orderlineArray["amount"], orderlineArray["total"], orderlineArray["patient"], orderlineArray["balance"], orderArray["status"], c);
				}

				index++;
			}
		}
		else
		{
			document.getElementById('ledgerTotal').firstChild.nodeValue = "0.00";
			document.getElementById('ledgerPatient').firstChild.nodeValue = "0.00";
			document.getElementById('ledgerBalance').firstChild.nodeValue = "0.00";
        }
    }

    function showPaymentPlanInstance(xml_str)
    {
		//var key = xml_str.childNodes[0].text;
		//var value = xml_str.childNodes[1].text;

		var key = xml_str.childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.childNodes[1].childNodes[0].nodeValue;

		eval("var instanceArray = " + value);

		document.clientFinancialForm.includeGroupCF.checked = (instanceArray["pool"] == 'true');
		document.clientFinancialForm.planStartDateCF.value = instanceArray["start"];
		document.clientFinancialForm.prepaidVisitsCF.value = instanceArray["prepaid_visits"];
		document.clientFinancialForm.visitsUsedCF.value = instanceArray["visits_used"];
		document.clientFinancialForm.visitsRemainingCF.value = instanceArray["visits_remaining"];
		document.clientFinancialForm.perVisitChargeCF.value = instanceArray["visit_charge"];
		document.clientFinancialForm.amountPaidCF.value = instanceArray["amount_paid"];
		document.clientFinancialForm.escrowAmountCF.value = instanceArray["escrow"];
		document.clientFinancialForm.dropPlanChargeCF.value = instanceArray["drop_plan_charge"];
    }

    function showBilling(xml_str)
    {
        //alert('showBilling invoked');
        document.getElementById('billSelect').options.length = 0;
        var index = 0;
        //var selectedValue = <%= adminPerson.getId() %>;
        while (xml_str.getElementsByTagName("bill")[index] != null)
        {
                key = xml_str.getElementsByTagName("bill")[index].childNodes[0].childNodes[0].nodeValue;
                value = xml_str.getElementsByTagName("bill")[index].childNodes[1].childNodes[0].nodeValue;
                eval("var billArray = " + value);

                document.getElementById('billSelect').options[index] = new Option(billArray["label"],key);

                index++;
        }
    }

    function showBillingOrders(xml_str)
    {
        var index = 0;

        if (xml_str.getElementsByTagName("order")[index])
        {
            while (xml_str.getElementsByTagName("order")[index])
            {
                    key = xml_str.getElementsByTagName("order")[index].childNodes[0].childNodes[0].nodeValue;
                    value = xml_str.getElementsByTagName("order")[index].childNodes[1].childNodes[0].nodeValue;
                    eval("var orderArray = " + value);
                    addRowToTable('tblLedger', orderArray["value"], orderArray["date"], orderArray["label"], orderArray["balance"]);
                    index++;
            }
        }
        else
        {
            addRowToTable('tblLedger', '', '', '', 'No Orders Found');
        }
    }

    function showBill(xml_str)
    {
        var key = xml_str.childNodes[0].childNodes[0].nodeValue;
        var value = xml_str.childNodes[1].childNodes[0].nodeValue;

        eval("var billArray = " + value);

        document.forms[0].bill_id.value = billArray["value"];
        document.forms[0].amount.value = billArray["amount"];
        document.getElementById('billing_dates').firstChild.nodeValue = billArray["dates"];

        if (billArray["c"] == "true")
        {
                document.forms[0].collections.checked = true;
                document.forms[0].collections_amount.value = billArray["c_amount"];
        }
        else
        {
                document.forms[0].collections.checked = false;
                document.forms[0].collections_amount.value = '';
        }

        var tbl = document.getElementById('tblLedger');
        deleteAllRows(tbl);
        processCommand('getBillingOrderDetails',billArray["value"]);
    }

    function refresh()
    {
	// refresh purchased plans
        //alert('refresh invoked');
        processCommand('refreshFinancial');
    }

    function selectPerson()
    {
		//getPersonDetails

		//alert(document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
		//document.getElementById('clientSelect').options.length = 0;

		unselectPlan();
		processCommand('getPersonDetails', document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
    }

    function unselectPlan()
    {
		document.clientFinancialForm.includeGroupCF.checked = false;
		document.clientFinancialForm.planStartDateCF.value = '';
		document.clientFinancialForm.prepaidVisitsCF.value = '';
		document.clientFinancialForm.visitsUsedCF.value = '';
		document.clientFinancialForm.visitsRemainingCF.value = '';
		document.clientFinancialForm.perVisitChargeCF.value = '';
		document.clientFinancialForm.amountPaidCF.value = '';
		document.clientFinancialForm.escrowAmountCF.value = '';
		document.clientFinancialForm.dropPlanChargeCF.value = '';
    }

    function endsWith(str, s)
    {
		var reg = new RegExp(s + "$");
		return reg.test(str);
    }

    function getDate(form, name)
    {
	if (eval("document." + form + "." + name + ".value == ''"))
	{
	    var now = new Date();
	    var nowString = (now.getMonth() + 1) + "/" + now.getDate() + "/" + now.getFullYear();
	    eval("document." + form + "." + name + ".value = '" + nowString + "'");
	}
    }

    function selectType()
    {
	//alert("selectType invoked");
        document.checkoutForm.codeSelect.options.length = 0;
	var index = 0;
<%
Iterator codes_itr = codes.iterator();
while (codes_itr.hasNext())
{
    CheckoutCodeBean code = (CheckoutCodeBean)codes_itr.next();
%>
        if (document.checkoutForm.typeSelect.value == <%= code.getType() %>)
            document.checkoutForm.codeSelect.options[index++] = new Option('<%= code.getLabel() %>','<%= code.getValue() %>');
<%
}
%>
    }

    function selectPlanInstance()
    {
	//alert('selectPlanInstance invoked');
	var select_element = document.getElementById('planSelect');
        processCommand('getPlanInstanceDetails',select_element.options[select_element.selectedIndex].value);
    }

    function selectBill()
    {
        var select_element = document.getElementById('billSelect');
        processCommand('getBillingDetails',select_element.options[select_element.selectedIndex].value);
    }

    function editAmount(amount, tname)
    {
        //alert('editAmount >' + amount);
        var edit_amount = (parseFloat(amount) ? parseFloat(amount) : 0);
        if (tname == 'tblCharges')
            editing_charge_amount = edit_amount
        else
            editing_credit_amount = edit_amount;
    }

    function addCode(tbl)
    {
        if (document.checkoutForm.codeSelect.value)
        {
            //var bg_color = typeArray[apptArray["type"]]["bg"];
            //alert(codeArray[document.forms[0].codeSelect.value]["amount"]);

	    if (tbl == 'tblCharges')
	    {
		if (numCharges == 0)
		    deleteAllRows(document.getElementById('tblCharges'));
		numCharges++;
            }
	    else
	    {
		if (numCredits == 0)
		    deleteAllRows(document.getElementById('tblCredits'));
		numCredits++;
	    }

            addRowToTable(tbl, codeArray[document.checkoutForm.codeSelect.value]["desc"], codeArray[document.checkoutForm.codeSelect.value]["id"], codeArray[document.checkoutForm.codeSelect.value]["code"], codeArray[document.checkoutForm.codeSelect.value]["amount"]);
        }
        else
            alert('You must select a code to add.');
    }

    function myRowObject(one, two, three, four)
    {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    function addLedgerRow(tname, id, date, code, desc, amount, total, client, balance, status, collections)
    {
		var tbl = document.getElementById(tname);
		var row = tbl.tBodies[0].insertRow(tbl.tBodies[0].rows.length);

		var idStr = '';
		var dateStr = '';
		var statusStr = '';
		var colStr = '';
		if (id != last_order)
		{
			iteration += 1;
			last_order = id;
			idStr = id;
			dateStr = date;
			statusStr = status;
			colStr = collections;
		}

		row.className = 'classy' + (iteration % 2);

		// cell 0 - text
		var cell0 = row.insertCell(0);
		var textNode = document.createTextNode(idStr);
		cell0.style.textAlign = 'left';
		cell0.appendChild(textNode);


		// cell 1 - text
		var cell1 = row.insertCell(1);
		textNode = document.createTextNode(dateStr);
		cell1.style.textAlign = 'left';
		cell1.appendChild(textNode);

		/*
		// cell 2 - text
		var cell2 = row.insertCell(2);
		textNode = document.createTextNode(code);
		cell2.style.textAlign = 'left';
		cell2.appendChild(textNode);
		*/

		// cell 3 - text
		var cell3 = row.insertCell(2);
		textNode = document.createTextNode(desc);
		cell3.style.textAlign = 'left';
		cell3.appendChild(textNode);

		// cell 4 - text
		var cell4 = row.insertCell(3);
		textNode = document.createTextNode(amount);
		cell4.style.textAlign = 'right';
		cell4.appendChild(textNode);

		// cell 5 - text
		var cell5 = row.insertCell(4);
		textNode = document.createTextNode(total);
		cell5.style.textAlign = 'right';
		cell5.appendChild(textNode);

		// cell 6 - text
		var cell6 = row.insertCell(5);
		textNode = document.createTextNode(client);
		cell6.style.textAlign = 'right';
		cell6.appendChild(textNode);

		// cell 7 - text
		var cell7 = row.insertCell(6);
		textNode = document.createTextNode(balance);
		cell7.style.textAlign = 'right';
		cell7.appendChild(textNode);

		// cell 8 - text
		var cell8 = row.insertCell(7);
		textNode = document.createTextNode(statusStr);
		cell8.style.textAlign = 'right';
		cell8.appendChild(textNode);


		var cell9 = row.insertCell(8);
		/*
		if (idStr != '')
		{
			var radInp = document.createElement('input');
			radInp.setAttribute('type', 'radio');
			radInp.setAttribute('name', 'orderSelect');
			radInp.setAttribute('value', idStr);
			radInp.onclick = function () {selected_order = id};
			cell9.appendChild(radInp);
		}
		*/
		//textNode = document.createTextNode(colStr);
		cell9.style.textAlign = 'left';
		//cell9.appendChild(textNode);

		//updateAmount(tname);
    }

    function addRowToTable(tname, desc, code_id, code, amount, orderline_id, actual)
    {
		var tbl = document.getElementById(tname);
		var nextRow = tbl.tBodies[0].rows.length;
		var iteration = nextRow + 1;

			/*
		if (num == null)
			num = nextRow;
		else
			iteration = num + 1;
			*/
			num = nextRow;

		// add the row
		var row = tbl.tBodies[0].insertRow(num);

		//eval("focusControl = document.getElementById('" + index + "').focus();");

		// CONFIG: requires classes named classy0 and classy1
		row.className = 'classy' + (iteration % 2);

			var cell0;
			var textNode;
		if (!code_id)
		{
			cell0 = row.insertCell(0);
			textNode = document.createTextNode(desc);
			cell0.style.textAlign = 'left';
			if (tname == 'tblCharges')
			cell0.colSpan = '6';
			else
			cell0.colSpan = '5';
			cell0.appendChild(textNode);
			return;
			}

		// cell 0 - text
		cell0 = row.insertCell(0);
		if (tname == 'tblCharges')
			cell0.setAttribute('id', 'ch' + iteration);
		else
			cell0.setAttribute('id', 'cr' + iteration);
		textNode = document.createTextNode(iteration);
		cell0.appendChild(textNode);

		// cell 1 - text
		var cell1 = row.insertCell(1);
		textNode = document.createTextNode(code + " " + desc);
		cell1.style.textAlign = 'left';
		cell1.appendChild(textNode);

		// cell 2 - input text
		var cell2 = row.insertCell(2);
		var txtInp = document.createElement('input');
		txtInp.setAttribute('type', 'text');
		txtInp.setAttribute('name', 'inputName' + iteration + tname);
		txtInp.setAttribute('size', '8');
		txtInp.setAttribute('value', amount);
		txtInp.onkeyup = function () {updateAmount(tname)};
		cell2.appendChild(txtInp);
		var hiddenInp = document.createElement('input');
		hiddenInp.setAttribute('type', 'hidden');
		hiddenInp.setAttribute('name', 'hidden' + iteration + tname);
		hiddenInp.setAttribute('value', code_id);
		cell2.appendChild(hiddenInp);
		if (orderline_id)
		{
			var hiddenOrderlineId = document.createElement('input');
			hiddenOrderlineId.setAttribute('type', 'hidden');
			hiddenOrderlineId.setAttribute('name', 'orderlineId' + iteration + tname);
			hiddenOrderlineId.setAttribute('value', orderline_id);
			cell2.appendChild(hiddenOrderlineId);
			}

		// cell 3 - input button
		var cell3 = row.insertCell(3);
		var btnEl = document.createElement('input');
		btnEl.setAttribute('type', 'button');
		btnEl.setAttribute('value', 'Delete');
		btnEl.onclick = function () {deleteCurrentRow(this, tname)};
		cell3.appendChild(btnEl);

		if (tname == 'tblCharges')
		{
			// cell 4 - input check
			var cell4 = row.insertCell(4);
			var checkInp = document.createElement('input');
			checkInp.setAttribute('type', 'checkbox');
			checkInp.setAttribute('name', 'inputCheck' + iteration + tname);
			checkInp.setAttribute('id', 'inputCheck' + iteration + tname);
			cell4.appendChild(checkInp);
			if (actual != '0.00')
			document.getElementById('inputCheck' + iteration + tname).checked = true;
		}

		// Pass in the elements you want to reference later
		// Store the myRow object in each row
		//row.myRow = new myRowObject(textNode, txtInp, cbEl, raEl);
		row.myRow = new myRowObject(textNode, txtInp);

		updateAmount(tname);
    }

    function addRowToTableNoEdit(tname, desc, date, id, code, amount)
    {
		var tbl = document.getElementById(tname);
		var nextRow = tbl.tBodies[0].rows.length;
		var iteration = nextRow + 1;

			/*
		if (num == null)
			num = nextRow;
		else
			iteration = num + 1;
				*/

			num = nextRow;

		// add the row
		var row = tbl.tBodies[0].insertRow(num);

		// CONFIG: requires classes named classy0 and classy1
		row.className = 'classy' + (iteration % 2);

		if (desc == 'No Open Orders Found')
		{
			// cell 0 - text
			var cell0 = row.insertCell(0);
			var textNode = document.createTextNode(desc);
			cell0.style.textAlign = 'left';
			cell0.colSpan = '5';
			cell0.appendChild(textNode);
			return;
			}

		// CONFIG: This whole section can be configured

		// cell 0 - text
		var cell0 = row.insertCell(0);
		var textNode = document.createTextNode(iteration);
		cell0.appendChild(textNode);

		// cell 1 - text
		var cell1 = row.insertCell(1);
		var textNode = document.createTextNode(date);
		cell1.appendChild(textNode);

		// cell 2 - text
		var cell2 = row.insertCell(2);
		var textNode = document.createTextNode(code + " " + desc);
		cell2.style.textAlign = 'left';
		cell2.appendChild(textNode);

		// cell 3 - text
		var cell3 = row.insertCell(3);
		var textNode = document.createTextNode(amount);
		cell3.appendChild(textNode);

		// cell 4 - input check
		var cell4 = row.insertCell(4);
		var checkInp = document.createElement('input');
		checkInp.setAttribute('type', 'checkbox');
		checkInp.setAttribute('name', 'inputCheck' + iteration + tname);
		checkInp.setAttribute('size', '8');
		cell4.appendChild(checkInp);
		var hiddenInp = document.createElement('input');
		hiddenInp.setAttribute('type', 'hidden');
		hiddenInp.setAttribute('name', 'hidden' + 'inputCheck' + iteration + tname);
		hiddenInp.setAttribute('value', id);
		cell4.appendChild(hiddenInp);
    }

    function deleteCurrentRow(obj, tname)
    {
		var delRow = obj.parentNode.parentNode;
		var tbl = delRow.parentNode.parentNode;
		var rIndex = delRow.sectionRowIndex;
		var rowArray = new Array(delRow);
		deleteRows(rowArray);
		reorderRows(tbl, rIndex, tname);
		updateAmount(tname);
    }

    function reorderRows(tbl, startingIndex, tname)
    {
		if (tbl.tBodies[0].rows[startingIndex]) {
			var count = startingIndex + 1;
			for (var i=startingIndex; i<tbl.tBodies[0].rows.length; i++) {

			// CONFIG: next line is affected by myRowObject settings

			//tbl.tBodies[0].rows[i].myRow.one.data = count; // text

			// CONFIG: next line is affected by myRowObject settings
			//tbl.tBodies[0].rows[i].myRow.two.name = 'inputName' + count + tname; // input text

			// CONFIG: next line is affected by myRowObject settings
			//var tempVal = tbl.tBodies[0].rows[i].myRow.two.value.split(' '); // for debug purposes
			//tbl.tBodies[0].rows[i].myRow.two.value = tempVal[0]; // for debug purposes

			// CONFIG: next line is affected by myRowObject settings
			//tbl.tBodies[0].rows[i].myRow.four.value = count; // input radio

			// CONFIG: requires class named classy0 and classy1
					//alert(tbl.tBodies[0].rows[i].cells[0].innerHTML);
					tbl.tBodies[0].rows[i].cells[0].innerHTML = count;
			tbl.tBodies[0].rows[i].className = 'classy' + (count % 2);

			count++;
			}
		}
    }

    function deleteRows(rowObjArray)
    {
		for (var i=0; i<rowObjArray.length; i++) {
			var rIndex = rowObjArray[i].sectionRowIndex;
			rowObjArray[i].parentNode.deleteRow(rIndex);
		}
    }

    function deleteAllRows(tbl)
    {
		iteration = 0;
        for (var i = tbl.tBodies[0].rows.length; i > 0; i--)
            tbl.tBodies[0].rows[i - 1].parentNode.deleteRow(i - 1);
    }

    function updateAmount(tname)
    {
        var table_amount = 0;
        var row_amount = 0;
        var table_obj = document.getElementById(tname);
        for (var i = 0; table_obj.tBodies[0].rows[i]; i++)
        {
            //alert(table_obj.tBodies[0].rows[i].myRow.two.value);
            row_amount = table_obj.tBodies[0].rows[i].myRow.two.value;
            table_amount += (parseFloat(row_amount) ? parseFloat(row_amount) : 0);
        }
        table_amount = Math.round(table_amount * 100) / 100;

        if (tname == 'tblCharges')
        {
            total_charges = table_amount;
            document.getElementById('charges').firstChild.nodeValue = total_charges;
        }
        else
        {
            total_credits = table_amount;
            document.getElementById('credits').firstChild.nodeValue = total_credits;
        }

        client_owes = Math.round((previous_balance + total_charges - total_credits) * 100) / 100;
        document.getElementById('owes').firstChild.nodeValue = client_owes;
    }

    function showCheckout(xml_str)
    {
		//alert("showCheckout() invoked...");

		//var key = xml_str.childNodes[0].childNodes[0].nodeValue;
			//var value = xml_str.childNodes[1].childNodes[0].nodeValue;

		var key = xml_str.getElementsByTagName("person")[0].childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.getElementsByTagName("person")[0].childNodes[1].childNodes[0].nodeValue;

		eval("var arr = " + value);

		document.checkoutForm.is_client.value=1;

			previous_balance = parseFloat(xml_str.getElementsByTagName("previous")[0].childNodes[0].nodeValue);
			total_charges = 0;
			total_credits = 0;
			client_owes = previous_balance;

			document.getElementById('previousBalance').firstChild.nodeValue = previous_balance;
			document.getElementById('owes').firstChild.nodeValue = previous_balance;

		document.checkoutForm.gucSelect.options.length = 0;
			var index = 1;
			if (xml_str.getElementsByTagName("person")[index] != null)
			{
			while (xml_str.getElementsByTagName("person")[index] != null)
				{
					key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
					value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
					eval("arx = " + value);

					document.checkoutForm.gucSelect.options[index - 1] = new Option(arx["label"],key);

					index++;
				}

				document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["label"] + " (" + arr["fts"] + ")";
			}
			else
				document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["label"];


			deleteAllRows(document.getElementById('tblCharges'));
		deleteAllRows(document.getElementById('tblCredits'));
		deleteAllRows(document.getElementById('tblPrevious'));



		numCharges = 0;
		numCredits = 0;
		numOpen = 0;


		//alert(document.clientFinancialForm.purchasePlanSelect.value);
		if (document.clientFinancialForm.purchasePlanSelect.value)
		{
			addRowToTable('tblCharges', codeArray[document.clientFinancialForm.purchasePlanSelect.value]["desc"], codeArray[document.clientFinancialForm.purchasePlanSelect.value]["id"], codeArray[document.clientFinancialForm.purchasePlanSelect.value]["code"], codeArray[document.clientFinancialForm.purchasePlanSelect.value]["amount"]);
			numCharges = 1;
		}

		if ((xml_str.getElementsByTagName("payment-orderlines")[0] != null) || (xml_str.getElementsByTagName("payment-orderlines")[0] != null))
		{
			if (xml_str.getElementsByTagName("charge-orderlines")[0] != null)
			{
			index = 0;
			while (xml_str.getElementsByTagName("charge-orderlines")[0].childNodes[index] != null)
			{
				key = xml_str.getElementsByTagName("charge-orderlines")[0].childNodes[index].childNodes[0].childNodes[0].nodeValue;
				value = xml_str.getElementsByTagName("charge-orderlines")[0].childNodes[index].childNodes[1].childNodes[0].nodeValue;
				eval("arr = " + value);

				//alert("value >" + value);

				//alert(arr["id"]);

				addRowToTable('tblCharges', arr["desc"], arr["code_id"], arr["code"], arr["amount"], arr["id"], arr["actual"]);
				//function addRowToTable(tname, desc, id, code, amount)

				index++;
				numCharges++;
			}
			}

			if (xml_str.getElementsByTagName("payment-orderlines")[0] != null)
			{
			index = 0;
			while (xml_str.getElementsByTagName("payment-orderlines")[0].childNodes[index] != null)
			{
				key = xml_str.getElementsByTagName("payment-orderlines")[0].childNodes[index].childNodes[0].childNodes[0].nodeValue;
				value = xml_str.getElementsByTagName("payment-orderlines")[0].childNodes[index].childNodes[1].childNodes[0].nodeValue;
				eval("arr = " + value);

				//alert("value >" + value);

				addRowToTable('tblCredits', arr["desc"], arr["code_id"], arr["code"], Math.abs(arr["amount"]), arr["id"], arr["actual"]);
				//function addRowToTable(tname, desc, id, code, amount)

				index++;
				numCredits++;
			}
			}
		}
		else
		{
			index = 0;
			if (xml_str.getElementsByTagName("orderline")[index] != null)
			{
			while (xml_str.getElementsByTagName("orderline")[index] != null)
			{
				key = xml_str.getElementsByTagName("orderline")[index].childNodes[0].childNodes[0].nodeValue;
				value = xml_str.getElementsByTagName("orderline")[index].childNodes[1].childNodes[0].nodeValue;
				eval("arr = " + value);

				//alert("value >" + value);

				addRowToTableNoEdit('tblPrevious', arr["desc"], arr["date"], arr["id"], arr["code"], arr["amount"]);

				//document.checkoutForm.gucSelect.options[index - 1] = new Option(arr["label"],key);

				index++;
				numOpen++;
			}
			}

			document.checkoutForm.numPrevious.value=index;
		}

			if (numCharges == 0)
			addRowToTable('tblCharges', 'No Charges Entered');
			if (numCredits == 0)
			addRowToTable('tblCredits', 'No Payments Entered');
			if (numOpen == 0)
			addRowToTableNoEdit('tblPrevious', 'No Open Orders Found');

			if (xml_str.getElementsByTagName("payment-plan-instance")[0] != null)
			{
			key = xml_str.getElementsByTagName("payment-plan-instance")[0].childNodes[0].childNodes[0].nodeValue;
				value = xml_str.getElementsByTagName("payment-plan-instance")[0].childNodes[1].childNodes[0].nodeValue;
				eval("arr = " + value);

				document.getElementById('plan').firstChild.nodeValue = arr["label"];
				document.getElementById('planStart').firstChild.nodeValue = arr["start"];
				document.getElementById('prepaidVisits').firstChild.nodeValue = arr["prepaid_visits"];
				document.getElementById('visitsUsed').firstChild.nodeValue = arr["visits_used"];
				document.getElementById('visitsRemaining').firstChild.nodeValue = arr["visits_remaining"];
				document.getElementById('perVisitCharge').firstChild.nodeValue = arr["visit_charge"];
				document.getElementById('amountPaid').firstChild.nodeValue = arr["amount_paid"];
				document.getElementById('escrowAmount').firstChild.nodeValue = arr["escrow"];
				document.getElementById('dropPlanCharge').firstChild.nodeValue = arr["drop_plan_charge"];
				plan_id = arr["practice_area_id"];
			}
			else
			{
				document.getElementById('plan').firstChild.nodeValue = 'Payment Plan Not Found';
				document.getElementById('planStart').firstChild.nodeValue = '';
				document.getElementById('prepaidVisits').firstChild.nodeValue = '';
				document.getElementById('visitsUsed').firstChild.nodeValue = '';
				document.getElementById('visitsRemaining').firstChild.nodeValue = '';
				document.getElementById('perVisitCharge').firstChild.nodeValue = '';
				document.getElementById('amountPaid').firstChild.nodeValue = '';
				document.getElementById('escrowAmount').firstChild.nodeValue = '';
				document.getElementById('dropPlanCharge').firstChild.nodeValue = '';
				plan_id = 0;
			}

		if (document.clientFinancialForm.purchasePlanSelect.value)
			document.getElementById('charges').firstChild.nodeValue = codeArray[document.clientFinancialForm.purchasePlanSelect.value]["amount"];
		else
			document.getElementById('charges').firstChild.nodeValue = '0.00';
			document.getElementById('credits').firstChild.nodeValue = '0.00';

		document.checkoutForm.isCheckout.value = 0;
		document.checkoutForm.order_id.value = selected_order;

		//addPreviousCharges();
			//addInitialCharges();
		selectType();
		YAHOO.example.container.checkoutDialog.show();
    }

    function showCheckoutXXX(xml_str)
    {
	//alert("showCheckout() invoked");

	//var key = xml_str.childNodes[0].childNodes[0].nodeValue;
        //var value = xml_str.childNodes[1].childNodes[0].nodeValue;

	var key = xml_str.getElementsByTagName("person")[0].childNodes[0].childNodes[0].nodeValue;
	var value = xml_str.getElementsByTagName("person")[0].childNodes[1].childNodes[0].nodeValue;
	eval("var arr = " + value);

	//document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["label"];


        previous_balance = parseFloat(xml_str.getElementsByTagName("previous")[0].childNodes[0].nodeValue);
        total_charges = 0;
        total_credits = 0;
        client_owes = previous_balance;

        document.getElementById('previousBalance').firstChild.nodeValue = previous_balance;
        document.getElementById('owes').firstChild.nodeValue = previous_balance;

	document.checkoutForm.gucSelect.options.length = 0;
        var index = 1;
        while (xml_str.getElementsByTagName("person")[index] != null)
        {
            key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
            eval("arr = " + value);

            document.checkoutForm.gucSelect.options[index - 1] = new Option(arr["label"],key);

            index++;
        }

        deleteAllRows(document.getElementById('tblCharges'));
	deleteAllRows(document.getElementById('tblCredits'));
	deleteAllRows(document.getElementById('tblPrevious'));

	//addRowToTable('tblCharges', 'No Charges Entered');
	addRowToTable('tblCredits', 'No Payments Entered');

	numCharges = 1;
	numCredits = 0;

	addRowToTable('tblCharges', codeArray[document.clientFinancialForm.purchasePlanSelect.value]["desc"], codeArray[document.clientFinancialForm.purchasePlanSelect.value]["id"], codeArray[document.clientFinancialForm.purchasePlanSelect.value]["code"], codeArray[document.clientFinancialForm.purchasePlanSelect.value]["amount"]);

	index = 0;
	if (xml_str.getElementsByTagName("orderline")[index] != null)
	{
	    while (xml_str.getElementsByTagName("orderline")[index] != null)
	    {
		key = xml_str.getElementsByTagName("orderline")[index].childNodes[0].childNodes[0].nodeValue;
		value = xml_str.getElementsByTagName("orderline")[index].childNodes[1].childNodes[0].nodeValue;
		eval("arr = " + value);

		//alert("value >" + value);

		addRowToTableNoEdit('tblPrevious', arr["desc"], arr["date"], arr["id"], arr["code"], arr["amount"]);

		index++;
	    }
	}
	else
	    addRowToTableNoEdit('tblPrevious', 'No Open Orders Found');

        document.checkoutForm.numPrevious.value=index;
	document.checkoutForm.isCheckout.value=0;

	//addPreviousCharges();
        //addInitialCharges();
	selectType();
	YAHOO.example.container.checkoutDialog.show();
    }

</script>

<script>
YAHOO.namespace("example.container");

function init() {

        var layout = new YAHOO.widget.Layout({
                minWidth: 750,
                units: [
		{ position: 'top', height: 21, resize: false, body: 'top1' },
		{ position: 'left', header: 'Client Search', width: 213, resize: false, body: 'left1', gutter: '0px', collapse: true, close: false, scroll: true, animate: true, duration: .1 },
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
        });
        layout.on('render', function() {
            layout.getUnitByPosition('left').on('close', function() {
                closeLeft();
            });
        });
        layout.render();

// Define various event handlers for Dialog
	var hsANG = function() {
	    this.submit();
	};
	var hcANG = function() {
	    YAHOO.example.container.dialogAssignNewGroup.hide();
	};
	var hsxANG = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);

		refresh();
	};
	var hfANG = function(o) {
		alert("Submission failed: " + o.status);
	};

	// Instantiate the Dialog
	YAHOO.example.container.dialogAssignNewGroup = new YAHOO.widget.Dialog("dialogAssignNewGroup",
	    { width : "300px",
	      fixedcenter : true,
	      visible : false,
	      constraintoviewport : true,
	      buttons : [ { text:"Cancel", handler:hcANG }, { text:"Submit", handler:hsANG, isDefault:true } ]
	     } );

	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialogAssignNewGroup.validate = function() {
		var data = this.getData();
		if (data.clientSelect == 0)
		{
		    alert("Please select a client.");
		    return false;
		}
		if (data.typeSelect == 0)
		{
		    alert("Please select an appointment type.");
		    return false;
		}
		if (data.duration == "")
		{
		    alert("Please specify a duration.");
		    return false;
		}

		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialogAssignNewGroup.callback = { success: hsxANG, failure: hfANG };

	// Render the Dialog
	YAHOO.example.container.dialogAssignNewGroup.render();



	// Define various event handlers for Dialog
	var handleSubmit2 = function() {
		this.submit();
	};
	var handleSuccess2 = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);
		else
		{
			//processCommand('getPeopleByLastName', '');
			processCommand('getPersonDetails', '-1');
		}
	};
	var handleFailure2 = function(o) {
		alert("Submission failed: " + o.status);
	};

	// Instantiate the Dialog
	YAHOO.example.container.dialog2 = new YAHOO.widget.Dialog("dialogNewClient",
                                { width : "450px",
								  fixedcenter : false,
								  visible : false,
								  lazyload : true,
								  x : 150,
								  y : 150,
                                  constraintoviewport : true,
                                  buttons : [ { text:"Submit", handler:handleSubmit2, isDefault:true } ]
                                 } );

	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialog2.validate = function() {
		var data = this.getData();
		if (data.firstname == 0)
		{
		    alert("Please select a first name.");
		    return false;
		}
		if (data.lastname == 0)
		{
		    alert("Please select a last name.");
		    return false;
		}

		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialog2.callback = { success: handleSuccess2, failure: handleFailure2 };

	// Render the Dialog
	YAHOO.example.container.dialog2.render();


	function saveClick(p_oEvent) {
            document.forms[1].submit();
        }

	function newClick(p_oEvent) {
            document.newClientForm.firstname.value='';
	    document.newClientForm.lastname.value='';
	    document.newClientForm.relationshipSelect.value='1';
	    document.newClientForm.clientFileTypeSelect.value='-1';
	    YAHOO.example.container.dialog2.show();
        }

	function assignClick(p_oEvent) {
            YAHOO.example.container.dialogAssignNewGroup.show();
        }

	function nextFileNumber(p_oEvent) {
            processCommand('nextFileNumber');
        }

	var oPushButton1 = new YAHOO.widget.Button("sButton1a", { onclick: { fn: saveClick } });
	var oPushButton2 = new YAHOO.widget.Button("sButton1b", { onclick: { fn: newClick } });
	var oPushButton3 = new YAHOO.widget.Button("sButton1c", { onclick: { fn: assignClick } });
	var oPushButton4 = new YAHOO.widget.Button("sButton1d", { onclick: { fn: newClick } });
	var oPushButton5 = new YAHOO.widget.Button("sButton1e", { onclick: { fn: nextFileNumber } });

	function deletePlan(p_oEvent) {
	    if (confirm('Delete plan?  Are you sure?'))
		processCommand('deletePlanInstance', document.getElementById('planSelect').value);
        }

	function savePlan(p_oEvent) {
	    //alert('Save functionality not complete...');
            //document.forms[0].submit();
	    document.clientFinancialForm.submit();
        }

	function newEntry(p_oEvent) {
	    //alert("new enrty");
	    //YAHOO.example.container.dialogNewTransaction.show();
	    processCommand('getCheckoutDetails', -1);
        }

	function editSelected(p_oEvent) {
	    if (selected_order > 0)
		processCommand('getCheckoutDetails', selected_order);
        }

	var oPushButton6 = new YAHOO.widget.Button("deletePlanButton", { onclick: { fn: deletePlan } });
	var oPushButton7 = new YAHOO.widget.Button("savePlanButton", { onclick: { fn: savePlan } });


	//var oPushButton8 = new YAHOO.widget.Button("newEntryButton", { onclick: { fn: newEntry } });
	//var oPushButton10 = new YAHOO.widget.Button("editSelectedButton", { onclick: { fn: editSelected } });

	function addTrx(p_oEvent) {

	    //alert("addTrx invoked");

            //alert (document.forms[2].typeSelect[document.forms[2].typeSelect.selectedIndex].innerHTML);

            if (document.checkoutForm.typeSelect[document.checkoutForm.typeSelect.selectedIndex].innerHTML == 'Payment')
            {
                addCode('tblCredits');
                //updateAmount('tblCredits');
                eval("focusControl = document.getElementById('cr" + document.getElementById('tblCredits').tBodies[0].rows.length + "').focus();");
            }
            else
            {
                addCode('tblCharges');
                eval("focusControl = document.getElementById('ch" + document.getElementById('tblCharges').tBodies[0].rows.length + "').focus();");
            }
        }

	var oPushButton9 = new YAHOO.widget.Button("addButton", { onclick: { fn: addTrx } });


	var tabView = new YAHOO.widget.TabView('demo');











	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};
	var handleSubmit3 = function() {
		this.submit();
	};
	var handleCancel3 = function() {
		YAHOO.example.container.checkoutDialog.hide();
	};
	YAHOO.example.container.checkoutDialog = new YAHOO.widget.Dialog("checkoutDialog",
                        { width : "775px",
                          fixedcenter : true,
                          visible : false,
                          constraintoviewport : true,
                          buttons : [ { text:"Cancel", handler:handleCancel3 },
                                                  { text:"Checkout", handler:handleSubmit3, isDefault:true } ]
                         } );

	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.checkoutDialog.validate = function() {
            if (numCharges == 0)
            {
                alert('No Charges Entered.');
                return false;
            }

		return true;
	};

	var handleCheckoutSuccess = function(o) {
		if (o.responseText.length > 0)
		{
		    alert(o.responseText);
		    YAHOO.example.container.checkoutDialog.show();
		}
		else
		    refresh();

		//var response = o.responseText;
		//document.getElementById("resp").innerHTML = response;

		//document.forms[0].appointmentSelect.value = -1;
		//document.forms[0].typeSelect.selectedIndex = 0;
		//document.forms[0].duration.value = "";

	};

	// Wire up the success and failure handlers
	YAHOO.example.container.checkoutDialog.callback = { success: handleCheckoutSuccess, failure: handleFailure };

	// Render the Dialog
	YAHOO.example.container.checkoutDialog.render();













	var subnt = function() {
		this.submit();
	};
	var sucnt = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);

		/*
		document.forms[0].appointmentSelect.value = -1;
		document.forms[0].typeSelect.selectedIndex = 0;
		document.forms[0].duration.value = "";
		*/

	};

	// Instantiate the Dialog
	YAHOO.example.container.dialogNewTransaction = new YAHOO.widget.Dialog("dialogNewTransaction",
			{ width : "475px",
			  fixedcenter : true,
			  visible : false,
			  constraintoviewport : true,
			  buttons : [ { text:"Submit", handler:subnt, isDefault:true } ]
			 } );

	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialogNewTransaction.validate = function() {
		var data = this.getData();
		/*
		if (data.typeSelect == 0)
		{
		    alert("Please select an appointment type.");
		    return false;
		}
		if (data.duration == "")
		{
		    alert("Please specify a duration.");
		    return false;
		}
		*/

		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialogNewTransaction.callback = { success: sucnt, failure: handleFailure };

	// Render the Dialog
	YAHOO.example.container.dialogNewTransaction.render();






}

YAHOO.util.Event.onDOMReady(init);


</script>

<script>


</script>

</head>

<%
String searchLastName = (String)session.getAttribute("searchLastName");
if (searchLastName == null)
	searchLastName = "";

String searchFirstName = (String)session.getAttribute("searchFirstName");
if (searchFirstName == null)
	searchFirstName = "";

String searchFileNumber = (String)session.getAttribute("searchFileNumber");
if (searchFileNumber == null)
	searchFileNumber = "";
%>

<body class="yui-skin-sam"<%= (searchLastName.length() > 0) ? " onload=\"processCommand('getPeopleByLastName', '" + searchLastName + "');\"" : "" %>>

<!-- <h1>Valeo Schedule</h1> -->


<div id="content" style="height: 100%;">

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>



    <div id="left1">
	<form>

	    &nbsp;<br />
	    <h2 style="padding:3px;margin:0;"><span id="selectedPerson"><%= adminPerson.isNew() ? "No Client Selected" : adminPerson.getLabel() %></span></h2>
	    <input type="button" id="sButton1d" name="sButton1d" value="New Client">
		<br /><br />
	    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div><br />
	      &nbsp;<strong>Search Last Name:</strong>
	      <div class="clear"></div>
	      &nbsp;<input value="<%= searchLastName%>" style="width: 190px;" class="adminInput" id="lastname" type="textbox" onkeyup="if (document.getElementById('lastname').value.length > 0) {processCommand('getPeopleByLastName', document.getElementById('lastname').value.replace(/\'/g,'\\\''));}" name="lastname" />
	      &nbsp;<strong>Search First / File Number:</strong>
	      <div class="clear"></div>
	      &nbsp;<input value="<%= searchFirstName%>" style="width: 100px;" class="adminInput" id="firstname" type="textbox" onkeyup="if (document.getElementById('firstname').value.length > 0) {processCommand('getPeopleByFirstName', document.getElementById('firstname').value);}" name="firstname" />&nbsp;/&nbsp;<input value="<%= searchFileNumber%>" style="width: 60px;" class="adminInput" id="filenumber" type="textbox" onkeyup="if (document.getElementById('filenumber').value.length > 0) {processCommand('getPeopleByFileNumber', document.getElementById('filenumber').value);}" name="filenumber" />
	      <!--
	      <div class="clear"></div>
	      &nbsp;Search First Name:
	      <div class="clear"></div>
	      &nbsp;<input class="adminInput" type="textbox" name="firstname" />
	      -->
	      <div class="clear"></div>
		  &nbsp;<strong>Client:</strong>
	      <div class="clear"></div>
	      &nbsp;<select name="clientSelect" size="20" id="clientSelect" style="width: 190px;" onchange="selectPerson();">
		  <option value="-1">-- SEARCH FOR A CLIENT --</option>
	      </select>

	</form>
    </div>

    <div id="center1">




	<div id="demo" class="yui-navset">
            <ul class="yui-nav">
                <li class="selected"><a href="#tab1"><em>Profile</em></a></li>
                <!-- <li><a href="#tab2"><em>Status</em></a></li> -->
                <li><a href="#tab3"><em>Payment Plans</em></a></li>
                <li><a href="#tab4"><em>Ledger</em></a></li>
                <!-- <li><a href="#tab5"><em>Billing</em></a></li> -->
            </ul>
            <div class="yui-content">
                <div id="tab1" style="height: 90%;">





<%
boolean is_prospect = false;
if (!adminPerson.isNew())
	is_prospect = adminPerson.getPersonType().equals(UKOnlinePersonBean.PROSPECT_PERSON_TYPE);
%>
            <struts-html:form action="/admin/clientProfile"  >
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">


		    <div class="adminItem">
			    <div class="leftTM">Next Appointment</div>
			    <div class="right">
				    <span id="n_appt" class="adminInput"><%= adminPerson.getNextAppointmentString() %></span>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Last Appointment</div>
			    <div class="right">
				    <span id="l_appt" class="adminInput"><%= adminPerson.getLastAppointmentString() %></span>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM"></div>
			    <div class="right"></div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">File Number</div>
			    <div class="right">
				    <input type="hidden" name="person_id" value="<%= adminPerson.getId() %>" />
					<input type="hidden" name="hidden_filenumber">
				    <input value="<%= adminPerson.getFileNumberString() %>" name="file_number" maxlength="100" class="adminInput" style="width: 76px;"<%= is_prospect ? " disabled" : ""  %> /><input type="checkbox" name="c_prospect"<%= is_prospect ? " checked" : ""  %> onclick="if (this.checked) { document.forms[1].hidden_filenumber.value=document.forms[1].file_number.value; document.forms[1].file_number.value=''; document.forms[1].file_number.disabled=true; } else {document.forms[1].file_number.disabled=false; document.forms[1].file_number.value=document.forms[1].hidden_filenumber.value;}" /><strong>Prospect</strong>
					<br /><input type="button" id="sButton1e" name="sButton1e" value="Get Next File Number">
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Prefix</div>
			    <div class="right">
				    <input value="<%= adminPerson.getPrefixString() %>" name="prefix" maxlength="100" class="adminInput" style="width: 76px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">First Name</div>
			    <div class="right">
				    <input value="<%= adminPerson.getFirstNameString() %>" name="firstname" maxlength="100" class="adminInput" style="width: 136px;" />
				    <input value="<%= adminPerson.getMiddleInitialString() %>" name="middle" maxlength="1" class="adminInput" style="width: 26px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Last Name</div>
			    <div class="right">
				    <input value="<%= adminPerson.getLastNameString() %>" name="lastname" maxlength="100" class="adminInput" style="width: 166px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Suffix</div>
			    <div class="right">
				    <input value="<%= adminPerson.getSuffixString() %>" name="suffix" maxlength="100" class="adminInput" style="width: 76px;" />
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			    <div class="leftTM">Address 1</div>
			    <div class="right">
				    <input value="<%= adminPerson.getHomeAddress1String() %>" name="address1" maxlength="250" class="adminInput" style="width: 250px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Address 2</div>
			    <div class="right">
				    <input value="<%= adminPerson.getHomeAddress2String() %>" name="address2" maxlength="250" class="adminInput" style="width: 250px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">City / State / Zip</div>
			    <div class="right">
				    <input value="<%= adminPerson.getHomeAddressCityString() %>" name="city" maxlength="100" class="adminInput" style="width: 136px;" />
				    <input value="<%= adminPerson.getHomeAddressStateString() %>" name="state" maxlength="2" class="adminInput" style="width: 30px;" />
				    <input value="<%= adminPerson.getHomeAddressZipString() %>" name="zipcode" maxlength="10" class="adminInput" style="width: 64px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Client Type</div>
			    <div class="right">
				<select name="client_type" class="adminInput" style="width: 250px;">
<%
Iterator itr = PersonTitleBean.getPersonTitles(adminCompany).iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
	PersonTitleBean client_type = (PersonTitleBean)itr.next();
%>
				    <option value="<%= client_type.getValue() %>"<%= adminPerson.getTitleString().equals(client_type.getLabel()) ? " selected" : "" %>><%= client_type.getLabel() %></option>
<%
    }
}
else
{
%>
				    <option value="-1">-- NO CLIENT TYPES FOUND --</option>
<%
}
%>
%>
				</select>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">File Type</div>
			    <div class="right">
				<select name="file_type" class="adminInput" style="width: 250px;">
<%
itr = GroupUnderCareMemberTypeBean.getMemberTypes(adminCompany).iterator();
if (itr.hasNext())
{
%>
				    <option value="-1">-- SELECT A FILE TYPE --</option>
<%
    while (itr.hasNext())
    {
		GroupUnderCareMemberTypeBean file_type = (GroupUnderCareMemberTypeBean)itr.next();
%>
				    <option value="<%= file_type.getValue() %>"<%= adminPerson.getGroupUnderCareMemberTypeValue().equals(file_type.getValue()) ? " selected" : "" %>><%= file_type.getLabel() %></option>
<%
    }
}
else
{
%>
				    <option value="-1">-- NO FILE TYPES FOUND --</option>
<%
}
%>
				</select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			    <div class="leftTM">Group Under Care</div>
			    <div class="right">
				    <select name="groupSelect" size="5" id="groupSelect" class="adminInput" style="width: 250px;">
<%
try
{
    group_under_care = adminPerson.getGroupUnderCare();
    Iterator group_under_care_members_itr = group_under_care.getMembers().iterator();
    if (group_under_care_members_itr.hasNext())
    {
	while (group_under_care_members_itr.hasNext())
	{
	    //UKOnlinePersonBean member = (UKOnlinePersonBean)group_under_care_members_itr.next();
	    GroupUnderCareMember member = (GroupUnderCareMember)group_under_care_members_itr.next();
	    UKOnlinePersonBean person_member = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(member.getPersonId());
%>
					    <option value="<%= person_member.getValue() %>"><%= person_member.getLabel() + " (" + GroupUnderCareBean.getRelationshipString(member.getRelationshipToPrimaryClient()) + ")" %></option>
<%
	}
    }
    else
    {
%>
					    <option value="-1">-- NO GROUP MEMBERS FOUND --</option>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
%>
					    <option value="-1">-- GROUP NOT FOUND --</option>
<%
}
%>
				    </select>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">&nbsp;</div>
			    <div class="right">
				    <input type="button" id="sButton1c" name="sButton1c" value="Assign to new Group Under Care">
			    </div>
			    <div class="end"></div>
		    </div>


				</td>
				<td><img src="../images/spacer.gif" width="20px;"></td>
				<td valign="top">

		    <div class="adminItem">
			    <div class="leftTM">PoS ID</div>
			    <div class="right">
				    <input value="<%= adminPerson.getEmployeeNumberString() %>" name="pos_id" size="7" class="adminInput" style="width: 166px;" disabled />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">QB ID</div>
			    <div class="right">
				    <input value="<%= adminPerson.getEmail2String() %>" name="qb_id" size="7" class="adminInput" style="width: 166px;" disabled />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Phone</div>
			    <div class="right">
				    <input value="<%= adminPerson.getHomePhoneNumberString() %>" name="phone" size="7" maxlength="14" class="adminInput" style="width: 120px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Cell</div>
			    <div class="right">
				    <input value="<%= adminPerson.getCellPhoneNumberString() %>" name="cell" size="7" maxlength="14" class="adminInput" style="width: 120px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Email</div>
			    <div class="right">
				    <input value="<%= adminPerson.getEmailString() %>" name="email" size="7" maxlength="250" class="adminInput" style="width: 225px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">SSN</div>
			    <div class="right">
				    <input value="<%= adminPerson.getSSNString() %>" name="ssn" size="11" maxlength="250" class="adminInput" style="width: 100px;" />
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			    <div class="leftTM">Date of Birth</div>
			    <div class="right">
				    <input value="<%= adminPerson.getBirthDateString() %>" name="dob" size="7" maxlength="12" class="adminInput" style="width: 80px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM"></div>
			    <div class="right">
				    <input type="radio" name="gender" value="Male"<%= adminPerson.isMale() ? " checked" : "" %> /><strong>Male</strong>
				    <input type="radio" name="gender" value="Female"<%= adminPerson.isFemale() ? " checked" : "" %> /><strong>Female</strong>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM"></div>
			    <div class="right">
				    <input type="checkbox" name="deceased"<%= adminPerson.isDeceased() ? " checked" : "" %> /><strong>Deceased</strong>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM"></div>
			    <div class="right">&nbsp;</div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Referral From</div>
			    <div class="right">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td><select style="width: 300px;" name="marketingProfileSelect" class="adminInput">
				<option value="0">-- SELECT A MARKETING CAMPAIGN --</option>
<%
int selected_marketing_plan_id = 0;
int selected_referred_by_client_id = 0;
String selected_referred_by_client_label = "-- SEARCH FOR A CLIENT --";
try
{
	ReferralSource referral_source = ReferralSource.getReferralSource(adminPerson);
	if (referral_source.isMarketingPlanReferral())
		selected_marketing_plan_id = referral_source.getMarketingPlan().getId();
	else if (referral_source.isClientReferral())
	{
		UKOnlinePersonBean selected_referred_by_client = referral_source.getReferredByClient();
		selected_referred_by_client_id = selected_referred_by_client.getId();
		selected_referred_by_client_label = selected_referred_by_client.getLabel();
	}
}
catch (ObjectNotFoundException x)
{
}
Iterator mp_itrx = MarketingPlan.getMarketingPlans(adminCompany, true).iterator();
while (mp_itrx.hasNext())
{
	MarketingPlan marketing_plan = (MarketingPlan)mp_itrx.next();
%>
				<option  value="<%= marketing_plan.getValue() %>"<%= (selected_marketing_plan_id == marketing_plan.getId()) ? " selected" : "" %>><%= marketing_plan.getLabel() %></option>
<%
}
%>
					</select></td>
				</tr>
			</table></div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">or</div>
			    <div class="right">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td>
	<input id="lnnc3" type="textbox" onkeyup="if (document.getElementById('lnnc3').value.length > 0) {show_referral_names2 = true; processCommand('getPeopleByLastName', document.getElementById('lnnc3').value);}" name="lnnc3" />
					</td>
				</tr>
			</table></div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Client Referral</div>
			    <div class="right">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td><select multiple style="width: 250px;" name="referralProfileSelect" class="adminInput">
<%
if (selected_referred_by_client_id == 0)
{
%>
				<option value="0">-- SEARCH FOR A CLIENT --</option>
<%
}
else
{
%>
				<option value="<%= selected_referred_by_client_id %>" selected><%= selected_referred_by_client_label %></option>
<%
}
%>
					</select></td>
				</tr>
			</table></div>
			    <div class="end"></div>
		    </div>



		    <div class="adminItem">
			    <div class="leftTM">&nbsp;</div>
			    <div class="right">&nbsp;</div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">&nbsp;</div>
			    <div class="right">
				    <input type="button" id="sButton1a" name="sButton1a" value="Save Client">
			    </div>
			    <div class="end"></div>
		    </div>


				</td>
			</tr>
			</table>

	    </struts-html:form>




                </div>
				<!--
                <div id="tab2">
                    <p>Tab Two Content</p>
                </div>
				-->
                <div id="tab3" style="height: 90%;">


		    <struts-html:form action="/admin/clientFinancial" >






            <div id="wrapper">
                <div id="side-a" style="width: 35%;">
		    <strong>Available Payment Plans:</strong>
		    <div class="clear"></div>
<%
boolean plans_exist = false;
Iterator plans_itr = CheckoutCodeBean.getCheckoutCodes(adminCompany, CheckoutCodeBean.PLAN_TYPE).iterator();
if (plans_itr.hasNext())
{
	plans_exist = true;
%>
			    <select name="purchasePlanSelect" size="7" id="purchasePlanSelect" class="adminInput" style="width: 75%">
<%
	while (plans_itr.hasNext())
	{
		CheckoutCodeBean payment_plan_code = (CheckoutCodeBean)plans_itr.next();
%>
				<option value="<%= payment_plan_code.getValue() %>"><%= payment_plan_code.getLabel() %></option>
<%
	}
%>
			    </select>
<%
}
else
{
%>
			    No payment plans found.  Contact your administrator to create the payment plans available in your practice.
<%
}
%>

		    <div class="clear"></div>
		    <br />
		    <strong>Purchased Payment Plans:</strong>
		    <div class="clear"></div>
		    <select name="planSelect" size="5" id="planSelect" class="adminInput" style="width: 75%" onchange="selectPlanInstance();">

		    </select>
		    <div class="clear"></div>
		    <input type="button" id="deletePlanButton" name="deletePlanButton" value="Delete Plan">

				<br />
				<br />
				<br />
				<br />
				<br />
				<br />
				<br />

                </div>
                <div id="side-b" style="width: 65%;">

		    <div class="adminItem">
			    <div class="leftTM"><span title="When checked, plan visits will be poooled among others in the group inder care.">Include Group Under Care Members</span></div>
			    <div class="right">
				    <input type="checkbox" name="includeGroupCF" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Plan Start Date</div>
			    <div class="right">
				    <input value="" name="planStartDateCF" size="7" maxlength="12" class="adminInput" style="width: 80px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Prepaid Visits</div>
			    <div class="right">
				    <input value="" name="prepaidVisitsCF" size="7" maxlength="3" class="adminInput" style="width: 32px;" disabled />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Visits Used</div>
			    <div class="right">
				    <input value="" name="visitsUsedCF" size="7" maxlength="3" class="adminInput" style="width: 32px;" disabled />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Visits Remaining</div>
			    <div class="right">
				    <input value="" name="visitsRemainingCF" size="7" maxlength="3" class="adminInput" style="width: 32px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM"></div>
			    <div class="right"></div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Per Visit Charge</div>
			    <div class="right">
				    <input value="" name="perVisitChargeCF" size="7" maxlength="7" class="adminInput" style="width: 64px;" disabled />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Amount Paid</div>
			    <div class="right">
				    <input value="" name="amountPaidCF" size="7" maxlength="7" class="adminInput" style="width: 64px;" disabled />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Escrow Amount</div>
			    <div class="right">
				    <input value="" name="escrowAmountCF" size="7" maxlength="7" class="adminInput" style="width: 64px;" disabled />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">Drop Plan Charge</div>
			    <div class="right">
				    <input value="" name="dropPlanChargeCF" size="7" maxlength="7" class="adminInput" style="width: 64px;" disabled />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">&nbsp;</div>
			    <div class="right">&nbsp;</div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem">
			    <div class="leftTM">&nbsp;</div>
			    <div class="right">
				    <input type="button" id="savePlanButton" name="savePlanButton" value="Save Plan">
			    </div>
			    <div class="end"></div>
		    </div>

                </div>
            </div>





    </struts-html:form>

                </div>
		<div id="tab4" style="height: 90%;">


			<div id="tableContainer" class="tableContainer">
			    <table id="tblLedger" border="0" cellpadding="0" cellspacing="0" class="scrollTable" >
				<thead class="fixedHeader" id="fixedHeader">
				    <tr>
					<th width="3%">#</th>
					<th width="8%">Date</th>
					<th width="31%">Description</th>
					<th width="7%">Amount</th>
					<th width="7%">Total</th>
					<th width="7%">Client</th>
					<th width="7%">Balance</th>
					<th width="22%">Status</th>
					<th width="3%">&nbsp;</th>
				    </tr>
				</thead>
				<tbody class="scrollContent" >
				</tbody>
			    </table>
			</div>
			<div>
			    <table id="totals" border="0" cellpadding="0" cellspacing="0" width="100%">
				<thead >
				    <tr>
					<th width="3%">&nbsp;</th>
					<th width="8%">&nbsp;</th>
					<th width="9%">&nbsp;</th>
					<th width="22%">
					<!-- <input type="button" id="newEntryButton" name="newEntryButton" value="New Entry"></th> -->
					<th width="7%">&nbsp;</th>
					<th width="7%" align="right">Total:<br /> <span id="ledgerTotal">0.00</span></th>
					<th width="7%" align="right">Client:<br /> <span id="ledgerPatient">0.00</span></th>
					<th width="7%" align="right">Balance:<br /> <span id="ledgerBalance">0.00</span></th>
					<th width="22%" align="right">
					<!-- <input type="button" id="editSelectedButton" name="editSelectedButton" value="Edit Selected"></th> -->
					<th width="3%">&nbsp;</th>
				    </tr>
				</thead>
			    </table>
			</div>



		</div>
		<!-- <div id="tab5">


		    <struts-html:form action="/admin/clientBilling" >




            <div id="wrapper">
                <div id="side-a" style="width: 35%;">
                    Total Amount Billed: xxx.xx
                    <div class="clear"></div>
                    Outstanding Amount: xxx.xx
                    <div class="clear"></div>
                    Unbilled Balance: xxx.xx
                    <div class="clear"></div>
                    <label for="clientSelect">Bills:</label>
                    <div class="clear"></div>
                    <select name="billSelect" size="15" id="billSelect" style="width:90%;" onchange="selectBill();">
                      <option value="-1">-- NO BILLS FOUND --</option>
                    </select>
                    <input type="submit" value="Create New Bill" class="ygbt" onclick="YAHOO.example.container.dialog1.show(); return false;">

                </div>
                <div id="side-b" style="width: 65%;">

                    <div class="adminItem">
                            <div class="leftTM">Billing Date(s)</div>
                            <div class="right"><span id="billing_dates">&nbsp;</span><input type="hidden" value="" name="bill_id" /></div>
                            <div class="end"></div>
                    </div>
                    <div class="adminItem">
                            <div class="leftTM">Amount Billed</div>
                            <div class="right">
                                    <input value="" name="amount" size="7" maxlength="7" class="adminInput" style="width: 64px;" />
                            </div>
                            <div class="end"></div>
                    </div>
                    <div class="adminItem">
                            <div class="left">&nbsp;</div>
                            <div class="right">
                                    <label for="collections">Sent to Collections</label>
                                    <input name="collections" id="collections" type="checkbox"/>
                            </div>
                            <div class="end"></div>
                    </div>
                    <div class="adminItem">
                            <div class="leftTM">Collections Amount</div>
                            <div class="right">
                                    <input value="" name="collections_amount" size="7" maxlength="7" class="adminInput" style="width: 64px;" />
                            </div>
                            <div class="end"></div>
                    </div>
                    <div class="adminItem">
                            <div class="leftTM">Orders On Bill:</div>
                            <div class="right">
                                    &nbsp;
                            </div>
                            <div class="end"></div>
                    </div>

                    <div class="clear"></div>

                    <div id="tableContainerX" class="tableContainer" style="height: 250px; width: 80%;">

                        <table id="tblLedgerX" border="0" cellpadding="0" cellspacing="0" class="scrollTable" >
                            <thead class="fixedHeader" id="fixedHeaderX">
                                <tr>
                                    <th>#</th>
                                    <th>Date</th>
                                    <th>Description</th>
                                    <th>Balance</th>
                                    <th>Status / Billing</th>
                                </tr>
                            </thead>
                            <tbody class="scrollContentX" >
                            </tbody>
                        </table>
                    </div>
                    <div class="adminItem">
                            <div class="leftTM">&nbsp;</div>
                            <div class="right">
                                <input type="submit" name="Re-Bill" value="Re-Bill" class="ygbt">&nbsp;
                                <input type="submit" name="Update" value="Update" class="ygbt">&nbsp;
                                <input type="submit" name="Print" value="Print" class="ygbt"></div>
                            <div class="end"></div>
                    </div>

                </div>
            </div>



    </struts-html:form>




                </div> -->
            </div>

        </div>


    </div>


</div>




<%
if (adminPerson.isNew())
	adminPerson.setFemale();
%>

<%@ include file="channels/channel-new-client.jsp" %>

<div id="dialogAssignNewGroup">
<div class="hd"><h2><span id="apptFormLabel">Assign to new Group Under Care</span></h2></div>
<div class="bd" id="bd-r">
<form name="assignGroupUnderCareForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/assignGroupUnderCare.x"  >

	<input type="hidden" name="clientSelect" value="-1">

	<div class="clear"></div>

	<strong>Search Last Name:</strong>
	<div class="clear"></div>
	<input id="lnaguc" type="textbox" onkeyup="if (document.getElementById('lnaguc').value.length > 0) {show_assign_names = true; processCommand('getPeopleByLastName', document.getElementById('lnaguc').value);}" name="lnaguc" />

	<div class="clear"></div>

	<label for="groupSelect"><strong>Group:</strong></label><br />
	<select multiple name="groupSelect">
	    <option value="-1">-- SEARCH FOR A GROUP --</option>
	</select>
	<div class="clear"></div>


	<strong>Relationship:</strong><br />
	<select name="relationshipSelect">
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_SELF_TYPE %>">Self</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_SPOUSE_TYPE %>">Spouse</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_CHILD_TYPE %>">Child</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_GUARDIAN_TYPE %>">Guardian</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_PARENT_TYPE %>">Parent</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_PARTNER_TYPE %>">Partner</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_OTHER_TYPE %>">Other</option>
	</select>

	<div class="clear"></div>

</form>
</div>
</div>



<%
}
catch (Exception x)
{
	x.printStackTrace();
}
%>


<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
