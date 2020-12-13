<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<%
Vector codes = CheckoutCodeBean.getCheckoutCodes(adminCompany);
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Valonyx</title>

<link rel="stylesheet" type="text/css" href="../css/schedule-client.css" />

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

					//buildScheduleArray(xml_response);
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
    
    function showPeople(xml_str)
    {
	//alert('show people invoked');
		document.getElementById('clientSelect').options.length = 0;
		var index = 0;
		var selectedValue = <%= adminPerson.getId() %>;
		while (xml_str.getElementsByTagName("person")[index] != null)
		{
			key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
			value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
			eval("var personArray = " + value);
			
			document.getElementById('clientSelect').options[index] = new Option(personArray["label"],key);
			//if (key == selectedValue)
			//	document.getElementById('clientSelect').selectedIndex = index;

			index++;
		}
    }
    
    function showPerson(xml_str)
    {
	//document.getElementById('clientSelect').options.length = 0;
	
	alert("showPerson");


	var key = xml_str.childNodes[0].childNodes[0].nodeValue;
	var value = xml_str.childNodes[1].childNodes[0].nodeValue;

	eval("var personArray = " + value);
	
	document.getElementById('selectedPerson').firstChild.nodeValue = personArray["label"];
        refresh();
    }

    function selectPerson()
    {
	//getPersonDetails

	//alert(document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
	//document.getElementById('clientSelect').options.length = 0;

	processCommand('getPersonDetails', document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
    }

    function endsWith(str, s)
    {
	var reg = new RegExp(s + "$");
	return reg.test(str);
    }
	
    function selectType()
    {
        document.transactionForm.paymentCodeSelect.options.length = 0;
	var index = 0;
<%
Iterator codes_itr = codes.iterator();
while (codes_itr.hasNext())
{
    CheckoutCodeBean code = (CheckoutCodeBean)codes_itr.next();
%>
        if (document.transactionForm.transactionTypeSelect.value == <%= code.getType() %>)
            document.transactionForm.paymentCodeSelect.options[index++] = new Option('<%= code.getLabel() %>','<%= code.getValue() %>');
<%
}
%>
    }

    function
    getDate(form, name)
    {
            if (eval("document." + form + "." + name + ".value == ''"))
            {
                    var now = new Date();
                    var nowString = (now.getMonth() + 1) + "/" + now.getDate() + "/" + now.getFullYear();
                    eval("document." + form + "." + name + ".value = '" + nowString + "'");
            }
    }

</script>

<script>
YAHOO.namespace("example.container");

function init() {
	
	// Define various event handlers for Dialog
	var handleSubmit = function() {
		this.submit();
	};
	var handleSuccess = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);
		var response = o.responseText;
		document.getElementById("resp").innerHTML = response;
		
		document.forms[0].appointmentSelect.value = -1;
		document.forms[0].typeSelect.selectedIndex = 0;
		document.forms[0].duration.value = "";
	
	};
	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};

	// Instantiate the Dialog
	YAHOO.example.container.dialog1 = new YAHOO.widget.Dialog("dialog1", 
																{ width : "475px",
																  fixedcenter : true,
																  visible : false, 
																  constraintoviewport : true,
																  buttons : [ { text:"Submit", handler:handleSubmit, isDefault:true } ]
																 } );
	
	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialog1.validate = function() {
		var data = this.getData();
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
	YAHOO.example.container.dialog1.callback = { success: handleSuccess, failure: handleFailure };
	
	// Render the Dialog
	YAHOO.example.container.dialog1.render();

}

YAHOO.util.Event.onDOMReady(init);

</script>

<!--there is no custom header content for this example-->


</head>

<%
String searchLastName = (String)session.getAttribute("searchLastName");
if (searchLastName == null)
	searchLastName = "";
%>

<body class="yui-skin-sam"<%= (searchLastName.length() > 0) ? " onload=\"processCommand('getPeopleByLastName', '" + searchLastName + "');\"" : "" %>>

<!-- <h1>Valeo Schedule</h1> -->

<%@ include file="channels/channel-schedule-menu.jsp" %>



<div id="content">

<table width="97%" height="93%"><tr><td valign="top">

<div id="wrapper">

    <div id="side-a">
	<h2><span id="selectedPerson"><%= adminPerson.isNew() ? "No Client Selected" : adminPerson.getLabel() %></span></h2><br />
          Search Last Name:
          <div class="clear"></div>
          <input value="<%= searchLastName%>" class="adminInput" id="lastname" type="textbox" onkeyup="if (document.getElementById('lastname').value.length > 0) {processCommand('getPeopleByLastName', document.getElementById('lastname').value);}" name="lastname" />
          <div class="clear"></div>
          Search First Name:
          <div class="clear"></div>
          <input class="adminInput" type="textbox" name="firstname" />
          <div class="clear"></div>
          <label for="clientSelect">Client:</label>
          <div class="clear"></div>
          <select name="clientSelect" size="20" id="clientSelect" style="width:90%;" onchange="selectPerson();">
              <option value="-1">-- SEARCH FOR A CLIENT --</option>
          </select>
    </div>
	
    <div id="side-b">
    <%@ include file="channels/channel-schedule-menu-clients.jsp" %>

        <div id="content">
            <input type="submit" value="New Entry" class="ygbt" onclick="YAHOO.example.container.dialog1.show(); return false;">
            <div id="tableContainer" class="tableContainer" >
              <table id="tblLedger" border="0" cellpadding="0" cellspacing="0" class="scrollTable" >
                  <thead class="fixedHeader" id="fixedHeader">
                      <tr>
                          <th>#</th>
                          <th>Date</th>
                          <th>Code</th>
                          <th>Description</th>
                          <th>Amount</th>
                          <th>Total</th>
                          <th>Client</th>
                          <th>Balance</th>
                          <th>Status / Billing</th>
                          <th>C</th>
                      </tr>
                  </thead>
                  <tbody class="scrollContent" >
<%


BigDecimal zero = new BigDecimal(0);

BigDecimal total = zero;
BigDecimal patient = zero;
BigDecimal balance = zero;



Iterator orders_itr = OrderBean.getOrders(adminPerson).iterator();


try
{
if (orders_itr.hasNext())
{
    for (int i = 0; orders_itr.hasNext(); i++)
    {
        OrderBean order_obj = (OrderBean)orders_itr.next();
        Iterator order_lines_itr = order_obj.getOrders();
	for (int x = 0; order_lines_itr.hasNext(); x++)
	{
            CheckoutOrderline order_line = (CheckoutOrderline)order_lines_itr.next();
            CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(order_line.getCheckoutCodeId());
            BigDecimal amount = order_line.getPrice();
            balance = balance.add(amount);
            if (amount.compareTo(zero) == 1)
                total = total.add(amount);
            else
                patient = patient.add(amount);
			
%>
                      <tr class="classy<%= i % 2 %>" >
                          <td width="3%"><%= (x == 0) ? order_obj.getValue() : "" %></td>
                          <td width="8%"><%= (x == 0) ? order_obj.getOrderDateString() : "" %></td>
                          <td width="9%"><%= code.getCode() %></td>
                          <td width="22%"><%= code.getDescription() %></td>
                          <td width="7%"><%= amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
                          <td width="7%"><%= total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
                          <td width="7%"><%= patient.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
                          <td width="7%"><%= balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
                          <td width="22%"><%= (x == 0) ? order_obj.isClosed() ? "PAID" : order_obj.isBilled() ? "BILLED" : "<input type=\"checkbox\" name=\"bill_check_" + i + "\">" : "" %></td>
                          <td width="3%"><%= (x == 0) ? order_obj.hasBeenSentToCollections() ? "*" : "" : "" %></td>
                      </tr>
<%
        }
    }
}
else
{
%>
                      <tr class="classy1">
                          <td colspan="10">No Orders Found</td>
                      </tr>
<%
}
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
                      </tbody>
                  </table>
              </div>
              <div>
                  <table id="totals" border="0" cellpadding="0" cellspacing="0" width="97%">
                      <thead >
                          <tr>
                              <th width="3%">&nbsp;</th>
                              <th width="8%">&nbsp;</th>
                              <th width="9%">&nbsp;</th>
                              <th width="22%">&nbsp;</th>
                              <th width="7%">&nbsp;</th>
                              <th width="7%" align="right"><%= total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></th>
                              <th width="7%" align="right"><%= patient.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></th>
                              <th width="7%" align="right"><%= balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></th>
                              <th width="22%" align="right"><input type="button" name="create_bill" value="Bill Selected"></th>
                              <th width="3%">&nbsp;</th>
                          </tr>
                      </thead>
                  </table>
              </div>
        </div>
    
    </div>

</div>

</td></tr></table>

</div>

<div id="dialog1">
<div class="hd"><span id="apptFormLabel">New Transaction</span></div>
<div class="bd" id="bd-r">
<form name="transactionForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/newTransaction.x"  >
    
	<input type="hidden" name="appointmentSelect" value="-1">
	<input type="hidden" name="statusSelect" value="-1">
	
	<div class="clear"></div>
        
        <label for="transactionDate">Transaction Date:</label>
        <input name="transactionDate" onfocus="getDate('transactionForm','transactionDate');select();" value="" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
	<div class="clear"></div>
        
	<label for="transactionTypeSelect">Transaction Type:</label>
        <select name="transactionTypeSelect" size="4" id="transactionTypeSelect" class="adminInput" style="width: 166px;" onchange="selectType();">
            <option value="<%= CheckoutCodeBean.PROCEDURE_TYPE%>" selected>Procedures</option>
            <option value="<%= CheckoutCodeBean.GROUP_TYPE%>">Group</option>
            <option value="<%= CheckoutCodeBean.INVENTORY_TYPE%>">Inventory</option>
            <option value="<%= CheckoutCodeBean.PAYMENT_TYPE%>">Payment</option>
        </select>
	<div class="clear"></div>

	<label for="paymentCodeSelect">Transaction Code:</label>
	<select multiple name="paymentCodeSelect">
	    <option value="-1">-- SELECT A PAYMENT CODE --</option>
<%
codes_itr = codes.iterator();
while (codes_itr.hasNext())
{
    CheckoutCodeBean code = (CheckoutCodeBean)codes_itr.next();
%>
	    <option value="<%= code.getValue() %>"><%= code.getLabel() %></option>
<%
}
%>
	</select>
	<div class="clear"></div>
    
	<label for="amountInput">Amount:</label><input id="amount" type="textbox" name="amountInput" />
	
	<div class="clear"></div>

</form>
</div>
</div>

<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
