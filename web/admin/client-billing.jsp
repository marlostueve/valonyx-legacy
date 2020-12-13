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

<script type="text/javascript" src="../scripts/crir.js"></script>
<!--[if lte IE 7]><script type="text/javascript" src="scripts/optionDisabledSupport.js"></script><![endif]-->

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
    
    function showPeople(xml_str)
    {
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
                //        document.getElementById('clientSelect').selectedIndex = index;

                index++;
        }
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
        //clearFields();
        processCommand('getBillingOrderDetails',billArray["value"]);
    }
    
    function showPerson(xml_str)
    {
	var key = xml_str.childNodes[0].childNodes[0].nodeValue;
	var value = xml_str.childNodes[1].childNodes[0].nodeValue;

	eval("var personArray = " + value);
	document.getElementById('selectedPerson').firstChild.nodeValue = personArray["label"];
	
        var tbl = document.getElementById('tblLedger');
        deleteAllRows(tbl);
        clearFields();
        refresh();
    }
        
    function clearFields()
    {
        document.forms[0].bill_id.value = '';
        document.forms[0].amount.value = '';
        document.getElementById('billing_dates').firstChild.nodeValue = '';
        document.forms[0].collections_amount.value = '';
        document.forms[0].collections.checked = false;
    }

    function selectPerson()
    {
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
        
    function refresh()
    {
        processCommand('getClientBillingInfo','-1');
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

    function addRowToTable(tname, id, date, desc, amount)
    {
        var tbl = document.getElementById(tname);
        var nextRow = tbl.tBodies[0].rows.length;

        // add the row
        var row = tbl.tBodies[0].insertRow(nextRow);

        // CONFIG: requires classes named classy0 and classy1
        row.className = 'classy' + (nextRow % 2);

        // CONFIG: This whole section can be configured

        // cell 0 - text
        var cell0 = row.insertCell(0);
        var textNode = document.createTextNode(id);
        cell0.appendChild(textNode);

        // cell 1 - text
        var cell1 = row.insertCell(1);
        var textNode = document.createTextNode(date);
        cell1.appendChild(textNode);

        // cell 2 - text
        var cell2 = row.insertCell(2);
        var textNode = document.createTextNode(desc);
        cell2.appendChild(textNode);

        if (amount == 'No Orders Found')
        {
            // cell 3 - text
            var cell3 = row.insertCell(3);
            var textNode = document.createTextNode('');
            cell3.appendChild(textNode);
            
            // cell 4 - text
            var cell4 = row.insertCell(4);
            var textNode = document.createTextNode('No Orders Found');
	    cell4.style.textAlign = 'left';
            cell4.appendChild(textNode);
        }
        else
        {
            // cell 3 - text
            var cell3 = row.insertCell(3);
            var textNode = document.createTextNode(amount);
            cell3.appendChild(textNode);
            
            // cell 4 - input button
            var cell4 = row.insertCell(4);
            var btnEl = document.createElement('input');
            btnEl.setAttribute('type', 'button');
            btnEl.setAttribute('value', 'Delete');
            btnEl.onclick = function () {deleteCurrentRow(this, tname)};
            cell4.appendChild(btnEl);
            var hiddenInp = document.createElement('input');
            hiddenInp.setAttribute('type', 'hidden');
            hiddenInp.setAttribute('name', 'hiddenId' + id);
            hiddenInp.setAttribute('value', id);
            cell4.appendChild(hiddenInp);
        }
    }

    function deleteCurrentRow(obj, tname)
    {
        var delRow = obj.parentNode.parentNode;
        var tbl = delRow.parentNode.parentNode;
        var rIndex = delRow.sectionRowIndex;
        var rowArray = new Array(delRow);
        deleteRows(rowArray);
        reorderRows(tbl, rIndex, tname);
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

    function reorderRows(tbl, startingIndex, tname)
    {
        if (tbl.tBodies[0].rows[startingIndex]) {
            var count = startingIndex + 1;
            for (var i=startingIndex; i<tbl.tBodies[0].rows.length; i++) {
                tbl.tBodies[0].rows[i].className = 'classy' + ((count + 1) % 2);
                count++;
            }
        }
    }
        
    function
    initErrors()
    {
        // check for passed errors
    <%
    String oldSeperator = System.getProperty("line.separator");
    System.setProperty("line.separator", "");
    %>
        var error = "<struts-html:errors/>";
    <%
    System.setProperty("line.separator", oldSeperator);
    %>
        if (error.length != 0)
        alert(error);
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
                refresh();
                
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

<body class="yui-skin-sam"<%= (searchLastName.length() > 0) ? " onload=\"initErrors();processCommand('getPeopleByLastName', '" + searchLastName + "');\"" : " onload=\"initErrors();refresh();\"" %>>

<!-- <h1>Valeo Schedule</h1> -->

<%@ include file="channels/channel-schedule-menu.jsp" %>



<div id="content">
<table width="97%" height="93%"><tr><td valign="top">

<div id="wrapper">
    <struts-html:form action="/admin/clientBilling" >
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

                    <div id="tableContainer" class="tableContainer" style="height: 250px; width: 80%;">

                        <table id="tblLedger" border="0" cellpadding="0" cellspacing="0" class="scrollTable" >
                            <thead class="fixedHeader" id="fixedHeader">
                                <tr>
                                    <th>#</th>
                                    <th>Date</th>
                                    <th>Description</th>
                                    <th>Balance</th>
                                    <th>Status / Billing</th>
                                </tr>
                            </thead>
                            <tbody class="scrollContent" >
                                <tr class="classy0">
                                    <td colspan="5">No Orders Found</td>
                                </tr>
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
            

        </div>
    
    </div>
    </struts-html:form>
</div>

</td></tr></table>
</div>

<div id="dialog1">
<div class="hd"><span id="apptFormLabel">New Billing</span></div>
<div class="bd" id="bd-r">
<form name="transactionForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/newBilling.x"  >
    
	<input type="hidden" name="appointmentSelect" value="-1">
	<input type="hidden" name="statusSelect" value="-1">
	
	<div class="clear"></div>
        
        <label for="transactionDateInput"><strong>Initial Billing Date:</strong></label>
        <input name="transactionDateInput" id="transactionDateInput" onfocus="getDate('transactionForm','transactionDateInput');select();" value="" size="7" maxlength="100" class="adminInput"  />
	<div class="clear"></div>

	<div class="clear"></div>
        
        <label for="tableContainer"><strong>Open Orders:</strong></label>
        
        <div id="tableContainer" class="tableContainer" style="height: 250px;">

            <table id="tblLedger" border="0" cellpadding="0" cellspacing="0" class="scrollTable" >
                <thead class="fixedHeader" id="fixedHeader">
                    <tr>
                        <th>Date</th>
                        <th>Description</th>
                        <th>Amount</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody class="scrollContent" >
<%
BigDecimal zero = new BigDecimal(0);
BigDecimal total = new BigDecimal(0);
BigDecimal patient = new BigDecimal(0);
BigDecimal balance = new BigDecimal(0);

Iterator open_orders_itr = OrderBean.getOpenOrders(adminPerson).iterator();
for (int i = 0; open_orders_itr.hasNext(); i++)
{
    OrderBean order_obj = (OrderBean)open_orders_itr.next();
    Iterator order_lines_itr = order_obj.getOrders();
    for (int x = 0; order_lines_itr.hasNext(); x++)
    {
        CheckoutOrderline order_line = (CheckoutOrderline)order_lines_itr.next();
        CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(order_line.getCheckoutCodeId());
        BigDecimal amount = order_line.getActualAmount();
        balance = balance.add(amount);
        if (amount.compareTo(zero) == 1)
            total = total.add(amount);
        else
            patient = patient.add(amount);
%>
                    <tr class="classy<%= i % 2%>" >
                        <td width="9%"><%= (x == 0) ? order_obj.getOrderDateString() : ""%></td>
                        <td width="67%"><%= code.getLabel()%></td>
                        <td width="8%"><%= amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString()%></td>
                        <td width="11%"><%= (x == 0) ? order_obj.isClosed() ? "PAID" : order_obj.isBilled() ? "BILLED" : "<input type=\"checkbox\" class=\"crirHiddenJS\" checked=\"checked\" name=\"order_check_" + order_obj.getId() + "\">" : ""%></td>
                    </tr>
<%
    }
}
%>
                </tbody>
            </table>
        </div>
        
        <br />
	<label for="amountInput"><strong>Billing Amount:</strong></label><input value="<%= balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString()%>" id="amount" name="amountInput" size="7" maxlength="7" class="adminInput" />
	

</form>
</div>
</div>

<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
