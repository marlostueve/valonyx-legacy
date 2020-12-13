<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.math.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*;" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="adminAppointment" class="com.badiyan.uk.online.beans.AppointmentBean" scope="session" />

<jsp:useBean id="adminOrder" class="com.badiyan.uk.beans.OrderBean" scope="session" />

<%
BigDecimal zero = new BigDecimal(0);
BigDecimal negative_one = new BigDecimal(-1);

if (request.getParameter("id") != null)
{
    try
    {
        adminPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(request.getParameter("id")));
        session.setAttribute("adminPerson", adminPerson);
    }
    catch (Exception x)
    {
        x.printStackTrace();
    }
}

Vector initial_checkout_codes = new Vector();
System.out.println("");
System.out.println("");
System.out.println("");
System.out.println("session.getAttribute(\"purchasePlan\") >" + session.getAttribute("purchasePlan"));
if (session.getAttribute("purchasePlan") != null)
{
    Integer planSelect = (Integer)session.getAttribute("purchasePlan");
    System.out.println("planSelect >" + planSelect);
    PaymentPlanBean payment_plan = PaymentPlanBean.getPaymentPlan(planSelect.intValue());
    //initial_checkout_code = payment_plan.getCheckoutCode();
    initial_checkout_codes.addElement(payment_plan.getCheckoutCode());
    session.removeAttribute("purchasePlan");
}
else
{
    if (request.getParameter("appt_id") != null)
    {
            try
            {
                    session.setAttribute("adminAppointment", AppointmentBean.getAppointment(Integer.parseInt(request.getParameter("appt_id"))));
            }
            catch (Exception x)
            {
                    x.printStackTrace();
                    session.removeAttribute("adminAppointment");
            }
    }
    else
            session.removeAttribute("adminAppointment");
}

// find any initial charges from the session OrderBean
Vector initial_orderlines = new Vector();
Vector session_orders = adminOrder.getOrdersVec();
if (session_orders != null)
{
    Iterator session_orders_itr = session_orders.iterator();
    while (session_orders_itr.hasNext())
    {
        CheckoutOrderline session_orderline = (CheckoutOrderline)session_orders_itr.next();
        initial_orderlines.addElement(session_orderline);
    }
}

// find any previous open orders that need to be paid off

Calendar now = Calendar.getInstance();
SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
GroupUnderCareBean group_under_care = null;

Vector codes = CheckoutCodeBean.getCheckoutCodes(adminCompany);

%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<style type="text/css" media="all">
	
	body {
		font: small arial, helvetica, sans-serif;
	}
	
	#header ul {
		list-style: none;
		padding: 0;
		margin: 0;
	}
	
	#header li {
		display: inline;
		margin: 0 2px 0 0;
	}
	
	#header a {
		padding: 0 1em;
		text-decoration: none;
		color: #a80;
		background: #fe5;
	}
	
	#header a:hover {
		background: #fc0;
		color: #540;
	}
	
	#header #selected {
	}
	
	#header #selected a {
		padding-bottom: 2px;
		font-weight: bold;
		color: black;
		color: black;
		background: #fc0;
	}
	
	#content {
		border-top: 2px solid white;
		background: #fc0;
		padding: 1em;
	}
	
	#content p {
		margin: 0;
		padding: 1em;
		background: white;
	}
	
	h1 {
		font-size: 1.5em;
		color: #fc0;
	}
	 
	#content h2 {
		font-size: 1.25em;
		color: #000;
		margin: 0;
	}

	#headline
		{
		/*\*/ overflow: hidden; /* */
		}
	* html #headline
		{
		
		}
		 
	#explore
		{
		float: left;
		width: 14em;
		}
		 
	#stats
		{
		float: left;
		}
	* html #stats
		{
		/*\*/ display: inline; /* */
		}
	 
#ygunav {background:#fc0; border-bottom:2px solid #ccc; padding:0 10px;font-size:78%;text-align:right;margin-bottom:6px;height:2.5em;line-height:2.5em;}
html>body #ygunav {overflow:hidden;}
#ygunav strong {font-family:verdana;}
#ygunav p {display:inline;margin:0;padding:0;}
#ygunav p em {float:left;text-align:left;font-style:normal; padding-top:.7em}
* html #ygunav p em {margin-top:1px;}
#ygunav p em i {visibility:hidden;}
#ygunav a {color:#000;}
#ygunav form {display:inline;margin:0 0 0 1em;}
#ygsp {width:8em;font-size:110%;padding:0;vertical-align:middle;}
#ygunav .ygbt {background:#dcdcdc;font:110% verdana;position:relative;top:1px;}
* html #ygunav .ygbt {top:4px;}
* html>body #ygunav .ygbt {line-height:0;top:-4px;}
#ygunav label {color:#666;font-family:tahoma;position:relative;top:1px;}

.adminInput {width:8em;font-size:110%;padding:0;vertical-align:middle;}

div.adminItem            { width: 292px;              margin: 2px 0px 3px  0px; }
div.adminItem div.left   { width: 100px; float: left; }
div.adminItem div.leftTM { width: 100px; float: left; margin-top: 2px; }
div.adminItem div.right  { width: 192px; float: left; }
div.adminItem div.end    { clear: left; }

div.adminItem div.left   { color: #4060af; font-weight: bold; }
div.adminItem div.leftTM { color: #4060af; font-weight: bold; }
div.adminItem div.right  { color: #000000;                    }

.horizontalRule { height: 1px; background-color: #adafaf; }
	
	</style>

	<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" />
	<link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" />
	<script type="text/javascript" src="../yui/build/utilities/utilities.js"></script>
	<script type="text/javascript" src="../yui/build/button/button-beta.js"></script>
	<script type="text/javascript" src="../yui/build/container/container.js"></script>

	<script type="text/javascript" src="../../build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="../../build/element/element-beta.js"></script>
	 
<style type="text/css">
<!--
#tblCharges td, th { padding: 0.5em; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }
#tblCredits td, th { padding: 0.5em; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }
#tblPrevious td, th { padding: 0.5em; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }
-->
</style>

	<script type="text/javascript">
    
    var codeArray = new Array();
    var httpRequest;
    
    var INPUT_NAME_PREFIX = 'inputName'; // this is being set via script
    var INPUT_CHECK_PREFIX = 'inputCheck'; // this is being set via script
    var ROW_BASE = 1; // first number (for display)
    var hasLoaded = false;
    
    var previous_balance = <%= adminPerson.getPreviousBalanceString() %>;
    var total_charges = 0;
    var total_credits = 0;
    var client_owes = 0;
    
    var editing_charge_amount = 0;
    var editing_credit_amount = 0;

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
			if (key == selectedValue)
				document.getElementById('clientSelect').selectedIndex = index;

			index++;
		}
    }
    
    function showPerson(xml_str)
    {
		//document.getElementById('clientSelect').options.length = 0;
		
		 
		var key = xml_str.childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.childNodes[1].childNodes[0].nodeValue;
		
		eval("var personArray = " + value);
		//alert(personArray["id"]);
		document.forms[0].person_id.value = personArray["id"];
		document.forms[0].prefix.value = personArray["prefix"];
		document.forms[0].firstname.value = personArray["fn"];
		document.forms[0].middle.value = personArray["mn"];
		document.forms[0].lastname.value = personArray["ln"];
		document.forms[0].suffix.value = personArray["suffix"];

		document.forms[0].address1.value = personArray["addr1"];
		document.forms[0].address2.value = personArray["addr2"];
		document.forms[0].city.value = personArray["city"];
		document.forms[0].state.value = personArray["state"];
		document.forms[0].zipcode.value = personArray["zip"];

		document.forms[0].ssn.value = personArray["ssn"];
		document.forms[0].phone.value = personArray["phone"];
		document.forms[0].cell.value = personArray["cell"];
		document.forms[0].email.value = personArray["email"];
		document.forms[0].dob.value = personArray["dob"];
		
		if (personArray["gender"] == "true")
			document.forms[0].gender[0].checked = true;
		else
			document.forms[0].gender[1].checked = true;

		if (personArray["deceased"] == "true")
			document.forms[0].deceased.checked = true;
		else
			document.forms[0].deceased.checked = false;
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
        document.forms[0].codeSelect.options.length = 0;
	var index = 0;
<%
itr = codes.iterator();
while (itr.hasNext())
{
    CheckoutCodeBean code = (CheckoutCodeBean)itr.next();
%>
        if (document.forms[0].typeSelect.value == <%= code.getType() %>)
            document.forms[0].codeSelect.options[index++] = new Option('<%= code.getLabel() %>','<%= code.getValue() %>');
<%
}
%>
    }
    
    function addCode(tbl)
    {
        if (document.forms[0].codeSelect.value)
        {
            //var bg_color = typeArray[apptArray["type"]]["bg"];
            //alert(codeArray[document.forms[0].codeSelect.value]["amount"]);
            addRowToTable(tbl, codeArray[document.forms[0].codeSelect.value]["id"], codeArray[document.forms[0].codeSelect.value]["code"], codeArray[document.forms[0].codeSelect.value]["desc"], codeArray[document.forms[0].codeSelect.value]["amount"]);
        }
        else
            alert('You must select a code to add.');
    }
        
    function addPreviousCharges()
    {
<%
int numPrevious = 0;
boolean has_previous_orders = false;
Iterator open_orders_itr = adminPerson.getOpenOrders().iterator();
for (;open_orders_itr.hasNext(); numPrevious++)
{
    has_previous_orders = true;
    OrderBean open_order_obj = (OrderBean)open_orders_itr.next();
    Iterator orderlines_itr = open_order_obj.getOrders();
    while (orderlines_itr.hasNext())
    {
        CheckoutOrderline orderline = (CheckoutOrderline)orderlines_itr.next();
        if (orderline.getOrderstatus().equals(OrderBean.OPEN_ORDER_STATUS))
	{
            CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
%>
        addRowToTableNoEdit('tblPrevious', '<%= open_order_obj.getOrderDateString() %>', '<%= orderline.getCheckoutOrderlineId() %>', '<%= checkout_code.getCode() %>', '<%= checkout_code.getDescription() %>', '<%= orderline.getActualAmount().setScale(2, BigDecimal.ROUND_HALF_UP) %>');
<%
        }
    }
}
%>
        document.forms[0].numPrevious.value=<%= numPrevious %>;
    }
    
    function addInitialCharges()
    {
<%
Iterator initial_checkout_codes_itr = initial_checkout_codes.iterator();
while (initial_checkout_codes_itr.hasNext())
{
    CheckoutCodeBean initial_checkout_code = (CheckoutCodeBean)initial_checkout_codes_itr.next();
%>
        addRowToTable('tblCharges', '<%= initial_checkout_code.getId() %>', '<%= initial_checkout_code.getCode() %>', '<%= initial_checkout_code.getDescription() %>', '<%= initial_checkout_code.getAmountString() %>');
<%
}
Iterator initial_orderlines_itr = initial_orderlines.iterator();
while (initial_orderlines_itr.hasNext())
{
    CheckoutOrderline initial_orderline = (CheckoutOrderline)initial_orderlines_itr.next();
    CheckoutCodeBean initial_checkout_code = CheckoutCodeBean.getCheckoutCode(initial_orderline.getCheckoutCodeId());
    BigDecimal amount = initial_orderline.getActualAmount();
    int compare_val = amount.compareTo(zero);
    if (compare_val == 0)
    {
    }
    else if (compare_val == 1)
    {
%>
        addRowToTable('tblCharges', '<%= initial_checkout_code.getId() %>', '<%= initial_checkout_code.getCode() %>', '<%= initial_checkout_code.getDescription() %>', '<%= initial_orderline.getActualAmount().setScale(2, BigDecimal.ROUND_HALF_UP) %>');
<%
    }
    else
    {
%>
        addRowToTable('tblCredits', '<%= initial_checkout_code.getId() %>', '<%= initial_checkout_code.getCode() %>', '<%= initial_checkout_code.getDescription() %>', '<%= initial_orderline.getActualAmount().multiply(negative_one).setScale(2, BigDecimal.ROUND_HALF_UP) %>');
<%
    }
}
%>
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
        
        client_owes = Math.round((previous_balance + total_charges - total_credits) * 100) / 100;;
        document.getElementById('owes').firstChild.nodeValue = client_owes;
    }

    function fillInRows()
    {
        hasLoaded = true;
        addPreviousCharges();
        addInitialCharges();
    }

    // CONFIG:
    // myRowObject is an object for storing information about the table rows
    function myRowObject(one, two, three, four)
    {
        this.one = one; // text object
        this.two = two; // input text object
        this.three = three; // input checkbox object
        this.four = four; // input radio object
    }

    /*
     * addRowToTable
     * Inserts at row 'num', or appends to the end if no arguments are passed in. Don't pass in empty strings.
     */
    function addRowToTable(tname, id, code, desc, amount, num)
    {
        
        //alert('code >' + code + ', desc >' + desc + ', amount >' + amount);
        if (hasLoaded) {
            var tbl = document.getElementById(tname);
            var nextRow = tbl.tBodies[0].rows.length;
            var iteration = nextRow + ROW_BASE;
            if (num == null) { 
                num = nextRow;
            } else {
                iteration = num + ROW_BASE;
            }

            // add the row
            var row = tbl.tBodies[0].insertRow(num);

            // CONFIG: requires classes named classy0 and classy1
            row.className = 'classy' + (iteration % 2);

            // CONFIG: This whole section can be configured

            // cell 0 - text
            var cell0 = row.insertCell(0);
            var textNode = document.createTextNode(iteration);
            cell0.appendChild(textNode);

            // cell 1 - text
            var cell1 = row.insertCell(1);
            var textNode = document.createTextNode(code);
            cell1.appendChild(textNode);

            // cell 2 - text
            var cell2 = row.insertCell(2);
            var textNode = document.createTextNode(desc);
            cell2.appendChild(textNode);

            // cell 3 - input text
            var cell3 = row.insertCell(3);
            var txtInp = document.createElement('input');
            txtInp.setAttribute('type', 'text');
            txtInp.setAttribute('name', INPUT_NAME_PREFIX + iteration + tname);
            txtInp.setAttribute('size', '8');
            txtInp.setAttribute('value', amount);
            txtInp.onkeyup = function () {updateAmount(tname)};
            cell3.appendChild(txtInp);
            var hiddenInp = document.createElement('input');
            hiddenInp.setAttribute('type', 'hidden');
            hiddenInp.setAttribute('name', 'hidden' + iteration + tname);
            hiddenInp.setAttribute('value', id);
            cell3.appendChild(hiddenInp);

            // cell 4 - input button
            var cell4 = row.insertCell(4);
            var btnEl = document.createElement('input');
            btnEl.setAttribute('type', 'button');
            btnEl.setAttribute('value', 'Delete');
            btnEl.onclick = function () {deleteCurrentRow(this, amount, tname)};
            cell4.appendChild(btnEl);
            
            if (tname == 'tblCharges')
            {
                // cell 5 - input check
                var cell5 = row.insertCell(5);
                var checkInp = document.createElement('input');
                checkInp.setAttribute('type', 'checkbox');
                checkInp.setAttribute('name', INPUT_CHECK_PREFIX + iteration + tname);
                checkInp.setAttribute('size', '8');
                cell5.appendChild(checkInp);
            }

            // Pass in the elements you want to reference later
            // Store the myRow object in each row
            //row.myRow = new myRowObject(textNode, txtInp, cbEl, raEl);
            row.myRow = new myRowObject(textNode, txtInp);
            
            updateAmount(tname);
        }
    }

    function addRowToTableNoEdit(tname, date, id, code, desc, amount, num)
    {
        if (hasLoaded) {
            var tbl = document.getElementById(tname);
            var nextRow = tbl.tBodies[0].rows.length;
            var iteration = nextRow + ROW_BASE;
            if (num == null) { 
                num = nextRow;
            } else {
                iteration = num + ROW_BASE;
            }

            // add the row
            var row = tbl.tBodies[0].insertRow(num);

            // CONFIG: requires classes named classy0 and classy1
            row.className = 'classy' + (iteration % 2);

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
            var textNode = document.createTextNode(code);
            cell2.appendChild(textNode);

            // cell 3 - text
            var cell3 = row.insertCell(3);
            var textNode = document.createTextNode(desc);
            cell3.appendChild(textNode);

            // cell 4 - text
            var cell4 = row.insertCell(4);
            var textNode = document.createTextNode(amount);
            cell4.appendChild(textNode);

            // cell 5 - input check
            var cell5 = row.insertCell(5);
            var checkInp = document.createElement('input');
            checkInp.setAttribute('type', 'checkbox');
            checkInp.setAttribute('name', INPUT_CHECK_PREFIX + iteration + tname);
            checkInp.setAttribute('size', '8');
            cell5.appendChild(checkInp);
            var hiddenInp = document.createElement('input');
            hiddenInp.setAttribute('type', 'hidden');
            hiddenInp.setAttribute('name', 'hidden' + INPUT_CHECK_PREFIX + iteration + tname);
            hiddenInp.setAttribute('value', id);
            cell5.appendChild(hiddenInp);

            // cell 4 - input button
            /*
            var cell4 = row.insertCell(4);
            var btnEl = document.createElement('input');
            btnEl.setAttribute('type', 'button');
            btnEl.setAttribute('value', 'Delete');
            btnEl.onclick = function () {deleteCurrentRow(this, amount, tname)};
            cell4.appendChild(btnEl);
            */

            // Pass in the elements you want to reference later
            // Store the myRow object in each row
            //row.myRow = new myRowObject(textNode, txtInp, cbEl, raEl);
            //row.myRow = new myRowObject(textNode, txtInp);
            
            //updateAmount(tname);
        }
    }

    // If there isn't an element with an onclick event in your row, then this function can't be used.
    function deleteCurrentRow(obj, amount, tname)
    {
        if (hasLoaded) {
            var delRow = obj.parentNode.parentNode;
            var tbl = delRow.parentNode.parentNode;
            var rIndex = delRow.sectionRowIndex;
            var rowArray = new Array(delRow);
            deleteRows(rowArray);
            reorderRows(tbl, rIndex, tname);
            
            updateAmount(tname);
        }
    }

    function reorderRows(tbl, startingIndex, tname)
    {
        if (hasLoaded) {
            if (tbl.tBodies[0].rows[startingIndex]) {
                var count = startingIndex + ROW_BASE;
                for (var i=startingIndex; i<tbl.tBodies[0].rows.length; i++) {

                    // CONFIG: next line is affected by myRowObject settings
                    tbl.tBodies[0].rows[i].myRow.one.data = count; // text

                    // CONFIG: next line is affected by myRowObject settings
                    tbl.tBodies[0].rows[i].myRow.two.name = INPUT_NAME_PREFIX + count + tname; // input text

                    // CONFIG: next line is affected by myRowObject settings
                    var tempVal = tbl.tBodies[0].rows[i].myRow.two.value.split(' '); // for debug purposes
                    tbl.tBodies[0].rows[i].myRow.two.value = tempVal[0]; // for debug purposes

                    // CONFIG: next line is affected by myRowObject settings
                    //tbl.tBodies[0].rows[i].myRow.four.value = count; // input radio

                    // CONFIG: requires class named classy0 and classy1
                    tbl.tBodies[0].rows[i].className = 'classy' + (count % 2);

                    count++;
                }
            }
        }
    }

    function deleteRows(rowObjArray)
    {
        if (hasLoaded) {
            for (var i=0; i<rowObjArray.length; i++) {
                var rIndex = rowObjArray[i].sectionRowIndex;
                rowObjArray[i].parentNode.deleteRow(rIndex);
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

	<title>Valonyx</title>

</head>

<%
String searchLastName = (String)session.getAttribute("searchLastName");
if (searchLastName == null)
	searchLastName = "";
%>

<body class="yui-skin-sam" onload="fillInRows();selectType();initErrors();">

<!-- <h1>Valeo Schedule</h1> -->

<%@ include file="channels/channel-schedule-menu.jsp" %>

<div id="content">

<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

<div id="headline">
	
	<div id="explore">
		<h2><%= date_format.format(now.getTime()) %></h2>
		<div class="adminItem">
			<div class="leftTM">Group Under Care</div>
			<div class="end"></div>
		</div>
		<div class="adminItem">
			<div class="leftTM">
				<select multiple name="clientSelect" size="5" id="clientSelect" class="adminInput" style="width: 166px;">
<%
try
{
	group_under_care = adminPerson.getGroupUnderCare();
	Iterator group_under_care_members_itr = group_under_care.getMembers().iterator();
	if (group_under_care_members_itr.hasNext())
	{
		while (group_under_care_members_itr.hasNext())
		{
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
		<h2><%= adminPerson.getLabel() %></h2>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
		<div class="adminItem">
			<div class="leftTM">Previous Balance</div>
			<div class="right">
				<%= adminPerson.getPreviousBalanceString() %>
			</div>
			<div class="end"></div>
		</div>
		<div class="adminItem">
			<div class="leftTM">Charges</div>
			<div class="right">
				<span id="charges">0.00</span>
			</div>
			<div class="end"></div>
		</div>
		<div class="adminItem">
			<div class="leftTM">Credits</div>
			<div class="right">
				<span id="credits">0.00</span>
			</div>
			<div class="end"></div>
		</div>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
		<div class="adminItem">
			<div class="leftTM">Client Owes</div>
			<div class="right">
				<span id="owes">0.00</span>
			</div>
			<div class="end"></div>
		</div>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
		 
	</div>
</div>

<div >
    <table id="clients" border="1" cellpadding="0" cellspacing="0">
        <tbody>
            <tr>
				<td valign="top">
					 

					
					<div id="ygunav">
                                            <table id="clients" border="1" cellpadding="0" cellspacing="0">
                                                <struts-html:form action="/admin/checkout"  >
                                                    <tr>
                                                        <td align="left" valign="top" width="33%">
                                                            Types
                                                            <select name="typeSelect" size="4" id="typeSelect" class="adminInput" style="width: 166px;" onchange="selectType();">
                                                                <option value="<%= CheckoutCodeBean.PROCEDURE_TYPE%>" selected>Procedures</option>
                                                                <option value="<%= CheckoutCodeBean.GROUP_TYPE%>">Group</option>
                                                                <option value="<%= CheckoutCodeBean.INVENTORY_TYPE%>">Inventory</option>
                                                                <option value="<%= CheckoutCodeBean.PAYMENT_TYPE%>">Payment</option>
                                                            </select>
                                                            Codes
                                                            <div class="right">
                                                            <select name="codeSelect" size="10" id="codeSelect" class="adminInput" style="width: 166px;">
<%
itr = codes.iterator();
while (itr.hasNext())
{
        CheckoutCodeBean code = (CheckoutCodeBean) itr.next();
%>
                                                                <option value="<%= code.getValue()%>"><%= code.getLabel()%></option>
<%
}
%>
                                                            </select>
                                                            <input type="button" value="Charge" onclick="addCode('tblCharges');" />
                                                            <input type="button" value="Credit" onclick="addCode('tblCredits');" />
                                                        </td>
                                                        <td align="left"  valign="top">
                                                            <table border="0" cellspacing="0" id="tblCharges">
                                                                <thead>
                                                                    <tr>
                                                                        <th colspan="4">Charges</th>
                                                                    </tr>
                                                                    <tr>
                                                                        <th>#</th><th>Code</th><th>Description</th><th>Amount</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody></tbody>
                                                            </table>
                                                            <table border="0" cellspacing="0" id="tblCredits">
                                                                <thead>
                                                                    <tr>
                                                                        <th colspan="4">Credits</th>
                                                                    </tr>
                                                                    <tr>
                                                                        <th>#</th><th>Code</th><th>Description</th><th>Amount</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody></tbody>
                                                            </table>
<%
if (has_previous_orders)
{
%>
                                                            <table border="0" cellspacing="0" id="tblPrevious">
                                                                <thead>
                                                                    <tr>
                                                                        <th colspan="4">Open Orders</th>
                                                                    </tr>
                                                                    <tr>
                                                                        <th>#</th><th>Order Date</th><th>Code</th><th>Description</th><th>Amount</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody></tbody>
                                                            </table>
<%
}
%>
                                                            <input type="hidden" name="numPrevious" value="">
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align="left"  valign="top">&nbsp;</td>
                                                        <td valign="top">
                                                            <div class="adminItem">
                                                                <div class="leftTM">&nbsp;</div>
                                                                <div class="right">
                                                                    <input type="submit" value="Save" class="ygbt">
                                                                </div>
                                                                <div class="end"></div>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </struts-html:form>
                                            </table>
					  </div>
				</td>
            </tr>

        </tbody>
    </table>
	
	
	
</div>

<form action="tableaddrow_nw.html" method="get">

</form>



</div>

<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
