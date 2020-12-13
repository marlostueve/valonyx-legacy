<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<%
Vector codes = CheckoutCodeBean.getCheckoutCodes(adminCompany);
Calendar now = Calendar.getInstance();
SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Valonyx</title>

<link rel="stylesheet" type="text/css" href="../css/schedule-client.css" />
<link rel="stylesheet" type="text/css" href="../css/checkout.css" />

<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" />
<script type="text/javascript" src="../yui/build/utilities/utilities.js"></script>
<script type="text/javascript" src="../yui/build/button/button-beta.js"></script>
<script type="text/javascript" src="../yui/build/container/container.js"></script>

<script type="text/javascript" src="../../build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../../build/element/element-beta.js"></script>

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

<script type="text/javascript">
    
        var codeArray = new Array();
    
<%
Iterator itr = codes.iterator();
while (itr.hasNext())
{
    CheckoutCodeBean code = (CheckoutCodeBean)itr.next();
%>
    codeArray["<%= code.getId() %>"] = {"id" : "<%= code.getId() %>", "code" : "<%= code.getCode() %>", "desc" : "<%= code.getDescription() %>", "label" : "<%= code.getLabel() %>", "amount" : "<%= code.getAmountString() %>" };
<%
}
%>
		
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

                    //document.getElementById('today_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
                    //document.getElementById('new_appt_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
                    //document.getElementById('newAppointmentDateInput').value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
                    //document.forms[0].newAppointmentDateInput.value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;

                    showPurchasedPlans(xml_response);
                }
                else if (httpRequest.responseXML.getElementsByTagName("checkout").length > 0)
                {
                    xml_response = httpRequest.responseXML.getElementsByTagName("checkout")[0];
                    showCheckout(xml_response);
                }
                else if (httpRequest.responseXML.getElementsByTagName("payment-plan-instance").length > 0)
                {
                    xml_response = httpRequest.responseXML.getElementsByTagName("payment-plan-instance")[0];
                    showPaymentPlanInstance(xml_response);
                }
                else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
                {
                    var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
                    showPeople(xml_response);
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
                else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
                {
                    var xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
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
    
    function showPurchasedPlans(xml_str)
    {
	//alert('showPurchasedPlans');
	var select_element = document.getElementById('planSelect');
        select_element.options.length = 0;
        var index = 0;
        //var selectedValue = <%= adminPerson.getId() %>;
	if (xml_str.getElementsByTagName("payment-plan-instance")[index] != null)
	{
	    while (xml_str.getElementsByTagName("payment-plan-instance")[index] != null)
	    {
		key = xml_str.getElementsByTagName("payment-plan-instance")[index].childNodes[0].childNodes[0].nodeValue;
		value = xml_str.getElementsByTagName("payment-plan-instance")[index].childNodes[1].childNodes[0].nodeValue;
		eval("var paymentPlanArray = " + value);

		select_element.options[index] = new Option(paymentPlanArray["label"],key);
		//if (key == selectedValue)
		    //    select_element.selectedIndex = index;

		index++;
	    }
	}
	else
	    select_element.options[index] = new Option('No Plans Purchased',-1);
    }
    
    function showPaymentPlanInstance(xml_str)
    {
		//alert('hrey');

		var key = xml_str.childNodes[0].text;
		var value = xml_str.childNodes[1].text;

		//alert(value);

		eval("var instanceArray = " + value);

		alert(instanceArray["amount_paid"]);

		document.forms[0].includeGroupCF.checked = instanceArray["pool"];
		document.forms[0].planStartDateCF.value = instanceArray["start"];
		document.forms[0].prepaidVisitsCF.value = instanceArray["prepaid_visits"];
		document.forms[0].visitsUsedCF.value = instanceArray["visits_used"];
		document.forms[0].visitsRemainingCF.value = instanceArray["visits_remaining"];
		document.forms[0].perVisitChargeCF.value = instanceArray["visit_charge"];
		document.forms[0].amountPaidCF.value = instanceArray["amount_paid"];
		document.forms[0].escrowAmountCF.value = instanceArray["escrow"];
		document.forms[0].dropPlanChargeCF.value = instanceArray["drop_plan_charge"];

	/*

		document.getElementById('selectedPerson').firstChild.nodeValue = personArray["label"];

		document.forms[1].person_id.value = personArray["id"];
		document.forms[1].prefix.value = personArray["prefix"];
		*/
    }
    
    function showPeople(xml_str)
    {
        document.getElementById('clientSelect').options.length = 0;
        var index = 0;
	if (xml_str.getElementsByTagName("person")[index] != null)
	{
	    while (xml_str.getElementsByTagName("person")[index] != null)
	    {
		key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
		value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
		eval("var personArray = " + value);

		document.getElementById('clientSelect').options[index] = new Option(personArray["label"],key);

		index++;
	    }
	}
	else
	    document.getElementById('clientSelect').options[index] = new Option('No Clients Found',-1);
	
        refresh();
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
    
    function showPerson(xml_str)
    {
	var key = xml_str.childNodes[0].childNodes[0].nodeValue;
	var value = xml_str.childNodes[1].childNodes[0].nodeValue;

	eval("var personArray = " + value);
		
	document.getElementById('selectedPerson').firstChild.nodeValue = personArray["label"];
        //var tbl = document.getElementById('tblLedger');
        //deleteAllRows(tbl);
        refresh();
    }

    function selectPerson()
    {
	unselectPlan();
        processCommand('getPersonDetails', document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
    }
    
    function unselectPlan()
    {
	document.forms[0].includeGroupCF.checked = false;
	document.forms[0].planStartDateCF.value = '';
	document.forms[0].prepaidVisitsCF.value = '';
	document.forms[0].visitsUsedCF.value = '';
	document.forms[0].visitsRemainingCF.value = '';
	document.forms[0].perVisitChargeCF.value = '';
	document.forms[0].amountPaidCF.value = '';
	document.forms[0].escrowAmountCF.value = '';
	document.forms[0].dropPlanChargeCF.value = '';
    }

    function endsWith(str, s)
    {
        var reg = new RegExp(s + "$");
        return reg.test(str);
    }
    
    function selectType()
    {
        document.forms[1].codeSelect.options.length = 0;
	var index = 0;
<%
Iterator codes_itr = codes.iterator();
while (codes_itr.hasNext())
{
    CheckoutCodeBean code = (CheckoutCodeBean)codes_itr.next();
%>
        if (document.forms[1].typeSelect.value == <%= code.getType() %>)
            document.forms[1].codeSelect.options[index++] = new Option('<%= code.getLabel() %>','<%= code.getValue() %>');
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
        
    function refresh()
    {
	// refresh purchased plans
        //alert('refresh invoked');
        processCommand('refreshFinancial');
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
        if (document.forms[1].codeSelect.value)
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
	    
            addRowToTable(tbl, codeArray[document.forms[1].codeSelect.value]["desc"], codeArray[document.forms[1].codeSelect.value]["id"], codeArray[document.forms[1].codeSelect.value]["code"], codeArray[document.forms[1].codeSelect.value]["amount"]);
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

    function addRowToTable(tname, desc, id, code, amount)
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
	if (!id)
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
	hiddenInp.setAttribute('value', id);
	cell2.appendChild(hiddenInp);

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
	    checkInp.setAttribute('size', '8');
	    cell4.appendChild(checkInp);
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
	//alert("showCheckout() invoked");
	
	//var key = xml_str.childNodes[0].childNodes[0].nodeValue;
        //var value = xml_str.childNodes[1].childNodes[0].nodeValue;
	
	var key = xml_str.getElementsByTagName("person")[0].childNodes[0].childNodes[0].nodeValue;
	var value = xml_str.getElementsByTagName("person")[0].childNodes[1].childNodes[0].nodeValue;
	eval("var arr = " + value);
	
	document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["label"];
	
        previous_balance = parseFloat(xml_str.getElementsByTagName("previous")[0].childNodes[0].nodeValue);
        total_charges = 0;
        total_credits = 0;
        client_owes = previous_balance;
        
        document.getElementById('previousBalance').firstChild.nodeValue = previous_balance;
        document.getElementById('owes').firstChild.nodeValue = previous_balance;
	
	document.forms[1].gucSelect.options.length = 0;
        var index = 1;
        while (xml_str.getElementsByTagName("person")[index] != null)
        {
            key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
            eval("arr = " + value);

            document.forms[1].gucSelect.options[index - 1] = new Option(arr["label"],key);

            index++;
        }
	
        deleteAllRows(document.getElementById('tblCharges'));
	deleteAllRows(document.getElementById('tblCredits'));
	deleteAllRows(document.getElementById('tblPrevious'));
	
	//addRowToTable('tblCharges', 'No Charges Entered');
	addRowToTable('tblCredits', 'No Payments Entered');
	
	numCharges = 1;
	numCredits = 0;
		
	addRowToTable('tblCharges', codeArray[document.forms[0].purchasePlanSelect.value]["desc"], codeArray[document.forms[0].purchasePlanSelect.value]["id"], codeArray[document.forms[0].purchasePlanSelect.value]["code"], codeArray[document.forms[0].purchasePlanSelect.value]["amount"]);
	
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

		//document.forms[1].gucSelect.options[index - 1] = new Option(arr["label"],key);

		index++;
	    }
	}
	else
	    addRowToTableNoEdit('tblPrevious', 'No Open Orders Found');
        
        document.forms[1].numPrevious.value=index;
	document.forms[1].isCheckout.value=0;
        
	//addPreviousCharges();
        //addInitialCharges();
	selectType();
	YAHOO.example.container.checkoutDialog.show();
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
		
                //refresh();
                
		//var response = o.responseText;
		//document.getElementById("resp").innerHTML = response;
		
		//document.forms[0].appointmentSelect.value = -1;
		//document.forms[0].typeSelect.selectedIndex = 0;
		//document.forms[0].duration.value = "";
	
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
	
	
	
	function purchasePlan(p_oEvent) {
            if (document.forms[0].purchasePlanSelect.selectedIndex > -1)
		processCommand('getCheckoutDetails', document.forms[0].purchasePlanSelect[document.forms[0].purchasePlanSelect.selectedIndex].value);
	    else
		alert('Select a plan to purchase.');
        }
	
	function deletePlan(p_oEvent) {
            alert('Delete functionality not complete...');
        }
	
	function savePlan(p_oEvent) {
	    alert('Save functionality not complete...');
            //document.forms[0].submit();
        }
	
	var oPushButton1 = new YAHOO.widget.Button("sButton1a", { onclick: { fn: purchasePlan } });
	var oPushButton2 = new YAHOO.widget.Button("sButton1b", { onclick: { fn: deletePlan } });
	var oPushButton3 = new YAHOO.widget.Button("sButton1c", { onclick: { fn: savePlan } });
	
	function addTrx(p_oEvent) {
	    
	    
            
            //alert (document.forms[2].typeSelect[document.forms[2].typeSelect.selectedIndex].innerHTML);
            
            if (document.forms[1].typeSelect[document.forms[1].typeSelect.selectedIndex].innerHTML == 'Payment')
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
    <struts-html:form action="/admin/clientFinancial" >
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
			    <select name="purchasePlanSelect" size="9" id="purchasePlanSelect" class="adminInput" style="width: 95%">
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

<%
if (plans_exist)
{
%>
		    <div class="clear"></div>
		    <input type="button" id="sButton1a" name="sButton1a" value="Purchase Plan">
<%
}
%>
		    <div class="clear"></div>
		    <br />
		    <strong>Purchased Payment Plans:</strong>
		    <div class="clear"></div>
		    <select name="planSelect" size="5" id="planSelect" class="adminInput" style="width: 95%" onchange="selectPlanInstance();">

		    </select>
		    <div class="clear"></div>
		    <input type="button" id="sButton1b" name="sButton1b" value="Delete Plan">

                    
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
		    <input type="button" id="sButton1c" name="sButton1c" value="Save Plan">
                </div>
            </div>
            

        </div>
    
    </div>
    </struts-html:form>
</div>

</td></tr></table>
</div>

<%@ include file="channels/channel-checkout.jsp" %>

<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
