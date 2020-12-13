<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, com.badiyan.torque.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qb.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />



<%
CompanySettingsBean settings = adminCompany.getSettings();

UKOnlinePersonBean logged_in_person = (UKOnlinePersonBean)loginBean.getPerson();

/*
if (request.getParameter("id") != null)
{
    try
    {
		int purchase_order_id = Integer.parseInt(request.getParameter("id"));
		PurchaseOrder test_purchase_order = PurchaseOrder.getPurchaseOrder(purchase_order_id);
		if (test_purchase_order.getCompany().getId() == adminCompany.getId())
		{
			adminPurchaseOrder = test_purchase_order;
			session.setAttribute("adminPurchaseOrder", adminPurchaseOrder);
		}
    }
    catch (NumberFormatException x)
    {
    }
}

Vector vendors = VendorRet.getVendors(adminCompany);
 */

String search_str = (String)session.getAttribute("invSearchStr");
if (search_str == null)
	search_str = "";

String vendor_search_str = (String)session.getAttribute("vendorSearchStr");
if (vendor_search_str == null)
	vendor_search_str = "";




String searchLastName = (String)session.getAttribute("searchLastName");
if (searchLastName == null)
	searchLastName = "";

String searchFirstName = (String)session.getAttribute("searchFirstName");
if (searchFirstName == null)
	searchFirstName = "";

String searchFileNumber = (String)session.getAttribute("searchFileNumber");
if (searchFileNumber == null)
	searchFileNumber = "";

Vector practitioners = UKOnlinePersonBean.getPersons(adminCompany, UKOnlineRoleBean.PRACTITIONER_ROLE_NAME);

String ledger_view = (String)session.getAttribute("ledger_view");
if (ledger_view == null)
{
	ledger_view = "year";
	session.setAttribute("ledger_view", "year");
}


%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Valonyx</title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-client.css" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />

    <style type="text/css">
    /*
      margin and padding on body element
      can introduce errors in determining
      element position and are not recommended;
      we turn them off as a foundation for YUI
      CSS treatments.
    */
    body {
        margin:0;
        padding:0;
    }
    </style>

    <link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
    <link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/container/assets/skins/sam/container.css" />
    <link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/resize/assets/skins/sam/resize.css" />
    <script type="text/javascript" src="https://www.valonyx.com/yui/build/utilities/utilities.js"></script>
    <script type="text/javascript" src="https://www.valonyx.com/yui/build/container/container.js"></script>
    <script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize.js"></script>


	<link rel="stylesheet" type="text/css"href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>


    <style type="text/css">

    #resizablepanel .bd {
        overflow:auto;
        background-color:#fff;
        padding:10px;
    }

    #resizablepanel .ft {
        height:15px;
        padding:0;
    }

    #resizablepanel .yui-resize-handle-br {
        right:0;
        bottom:0;
        height: 8px;
        width: 8px;
        position:absolute;
    }

    /*
        The following CSS is added to prevent scrollbar bleedthrough on
        Gecko browsers (e.g. Firefox) on MacOS.
    */

    /*
        PLEASE NOTE: It is necessary to toggle the "overflow" property
        of the body element between "hidden" and "auto" in order to
        prevent the scrollbars from remaining visible after the the
        Resizable Panel is hidden.  For more information on this issue,
        read the comments in the "container-core.css" file.

        We use the #reziablepanel_c id based specifier, so that the rule
        is specific enough to over-ride the .bd overflow rule above.
    */

    #resizablepanel_c.hide-scrollbars .yui-resize .bd {
        overflow: hidden;
    }

    #resizablepanel_c.show-scrollbars .yui-resize .bd {
        overflow: auto;
    }

    /*
        PLEASE NOTE: It is necessary to set the "overflow" property of
        the underlay element to "visible" in order for the
        scrollbars on the body of a Resizable Panel instance to be
        visible.  By default the "overflow" property of the underlay
        element is set to "auto" when a Panel is made visible on
        Gecko for Mac OS X to prevent scrollbars from poking through
        it on that browser + platform combintation.  For more
        information on this issue, read the comments in the
        "container-core.css" file.
    */

    #resizablepanel_c.show-scrollbars .underlay {
        overflow: visible;
    }
	
	#ledger-table td, th { padding: 0.25em; font-size:100%; }
	.classy0 { background-color: #E6E6FA; color: #E6E6FA; }
	.classy1 { background-color: #FFFFFF; color: #FFFFFF; }
	
    </style>


	<script type="text/javascript">

    var codeArray = new Array();

    var httpRequest;
    var xml_response;
	var panel;

    var show_group_names = false;
    var show_assign_names = false;
    var show_referral_names = false;
    var show_referral_names2 = false;

    var initial = true;
	
    var last_order = -1;
    var iteration = 0;
    var selected_order = 0;
	
	var showCommission = <%= logged_in_person.isAdministrator() ? "true" : "false" %>

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
					showLedger(xml_response);
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
					document.forms[0].file_number.value = xml_response.getAttribute("num");
				}
				else if (httpRequest.responseXML.getElementsByTagName("purchase_order_report_url").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("purchase_order_report_url")[0];
					document.getElementById('cos_div').innerHTML = '<EMBED SRC="' + xml_response.childNodes[0].nodeValue + '" WIDTH="100%" HEIGHT="100%"></EMBED>';
					panel.show();
				}
				else if (httpRequest.responseXML.getElementsByTagName("sales_receipt_url").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("sales_receipt_url")[0];
					document.getElementById('cos_div').innerHTML = '<EMBED SRC="' + xml_response.childNodes[0].nodeValue + '" WIDTH="100%" HEIGHT="100%"></EMBED>';
					//document.getElementById('reportDialogFormLabel').firstChild.nodeValue = 'Sales Receipt';
					panel.show();
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
			//refresh();
			initial = false;
		}
    }

    function selectPerson()
    {
		//unselectPlan();
		processCommand('showLedger', document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
		
		//processCommand('getPersonDetails', document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
    }

    function showLedger(xml_str)
    {
		document.getElementById('selectedPerson').firstChild.nodeValue = xml_str.getAttribute("cl");
		
		var ledger = document.getElementById('ledger-table');
		deleteAllRows(ledger);

		last_order = '-1';
		index = 0;
		if (xml_str.getElementsByTagName("order")[index] != null)
		{
			//document.getElementById('ledgerTotal').firstChild.nodeValue = xml_str.getElementsByTagName("ledger")[0].getAttribute("total");
			//document.getElementById('ledgerPatient').firstChild.nodeValue = xml_str.getElementsByTagName("ledger")[0].getAttribute("client");
			//document.getElementById('ledgerBalance').firstChild.nodeValue = xml_str.getElementsByTagName("ledger")[0].getAttribute("balance");

			while (xml_str.getElementsByTagName("order")[index] != null)
			{
				key = xml_str.getElementsByTagName("order")[index].childNodes[0].childNodes[0].nodeValue;
				value = xml_str.getElementsByTagName("order")[index].childNodes[1].childNodes[0].nodeValue;
				//alert(value);
				eval("var orderArray = " + value);

				//var status;
				//var c = "";

				for (var line_index = 0; xml_str.getElementsByTagName("order")[index].childNodes[2].childNodes[line_index] != null; line_index++)
				{
					orderline_key = xml_str.getElementsByTagName("order")[index].childNodes[2].childNodes[line_index].childNodes[0].childNodes[0].nodeValue;
					orderline_value = xml_str.getElementsByTagName("order")[index].childNodes[2].childNodes[line_index].childNodes[1].childNodes[0].nodeValue;
					//alert(orderline_value);
					eval("var orderlineArray = " + orderline_value);

					addLedgerRow('ledger-table', orderArray["value"], orderlineArray["id"], orderArray["date"], orderlineArray["code"], orderlineArray["desc"], orderlineArray["amount"], orderArray["status"], orderlineArray["qty"], orderlineArray["prac"], orderlineArray["comm"], orderArray["pr"], orderlineArray["isc"], orderlineArray["ist"]);
				}

				index++;
			}
		}
		else
		{
			//document.getElementById('ledgerTotal').firstChild.nodeValue = "0.00";
			//document.getElementById('ledgerPatient').firstChild.nodeValue = "0.00";
			//document.getElementById('ledgerBalance').firstChild.nodeValue = "0.00";
			
			
			addLedgerRowX('ledger-table', 'No results found');
        }
    }

    function addLedgerRowX(tname, desc)
    {
		var tbl = document.getElementById(tname);
		var row = tbl.tBodies[0].insertRow(tbl.tBodies[0].rows.length);

		// cell 0 - text
		var cell0 = row.insertCell(0);
		var textNode = document.createTextNode(desc);
		cell0.style.width = '792px';
		cell0.style.textAlign = 'left';
		cell0.appendChild(textNode);
	}

    function addLedgerRow(tname, id, lid, date, code, desc, amount, status, qty, prac, comm, pr, isc, ist)
    {
		var tbl = document.getElementById(tname);
		var row = tbl.tBodies[0].insertRow(tbl.tBodies[0].rows.length);

		var idStr = '';
		var dateStr = '';
		var statusStr = '';
		var prStr = '';
		if (id != last_order)
		{
			iteration += 1;
			last_order = id;
			idStr = id;
			dateStr = date;
			statusStr = status;
			prStr = pr;
		}
		
		row.className = 'classy' + (iteration % 2);

		// cell 0 - text
		var cell0 = row.insertCell(0);
		var textNode = document.createTextNode('');
		cell0.style.width = '3px';
		cell0.style.textAlign = 'left';
		cell0.appendChild(textNode);

		// cell 1 - text
		var cell1 = row.insertCell(1);
		cell1.style.width = '72px';
		cell1.style.textAlign = 'left';
		if (ist)
		{
			var anchor = document.createElement('a');
			url = "tender-detail.jsp?id=" + idStr;
			anchor.setAttribute("href",url);
			textNode = document.createTextNode(idStr);
			anchor.appendChild(textNode);
			cell1.appendChild(anchor);
		}
		else if (prStr && prStr != '' && prStr != '0')
		{
			var anchor = document.createElement('a');
			url = "#";
			anchor.setAttribute("href",url);
			textNode = document.createTextNode(idStr);
			anchor.appendChild(textNode);
			anchor.onclick = function () {processCommand('viewReceipt',prStr)};
			cell1.appendChild(anchor);
		}
		else
		{
			textNode = document.createTextNode(idStr);
			cell1.appendChild(textNode);
		}

		// cell 2 - text
		var cell2 = row.insertCell(2);
		textNode = document.createTextNode(dateStr);
		cell2.style.width = '70px';
		cell2.style.textAlign = 'left';
		cell2.appendChild(textNode);

		// cell 3 - text
		var cell3 = row.insertCell(3);
		textNode = document.createTextNode(desc);
		cell3.style.width = '285px';
		cell3.style.textAlign = 'left';
		cell3.appendChild(textNode);

		// cell 4 - text
		var cell4 = row.insertCell(4);
		textNode = document.createTextNode(qty);
		cell4.style.width = '30px';
		cell4.style.textAlign = 'right';
		cell4.appendChild(textNode);

		// cell 5 - text
		var cell5 = row.insertCell(5);
		textNode = document.createTextNode(amount);
		cell5.style.width = '69px';
		cell5.style.textAlign = 'right';
		cell5.appendChild(textNode);
		
		// cell 6 - text
		var cell6 = row.insertCell(6);
		//textNode = document.createTextNode(prac);
		cell6.style.width = '60px';
		cell6.style.textAlign = 'right';
		//cell6.appendChild(textNode);
		
		if (isc)
		{
			var selector = document.createElement('select');
			selector.id = 'prac_sel_' + lid
			selector.name = 'prac_sel_' + lid;
			cell6.appendChild(selector);

			var option = document.createElement('option');
			option.value = '-1';
			option.appendChild(document.createTextNode('-- NONE --'));
			selector.appendChild(option);
		
<%
Iterator itr = practitioners.iterator();
while (itr.hasNext())
{
	UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
%>
			option = document.createElement('option');
			option.value = '<%= practitioner.getValue() %>';
			if (prac == '<%= practitioner.getFirstNameString() %>')
				option.selected = 'true';
			option.appendChild(document.createTextNode('<%= practitioner.getFirstNameString() %>'));
			selector.appendChild(option);
<%
}
%>
		}
		else
		{
			textNode = document.createTextNode('');
			cell6.appendChild(textNode);
		}

		// cell 7 - text
		var cell7 = row.insertCell(7);
		//textNode = document.createTextNode(comm);
		cell7.style.width = '60px';
		cell7.style.textAlign = 'right';
		//cell7.appendChild(textNode);
		
		if (isc)
		{
			if (showCommission)
			{
				var comm_input = document.createElement('input');
				comm_input.type = 'text';
				comm_input.name = 'comm_input_' + lid;
				comm_input.id = 'comm_input_' + lid;
				comm_input.value = comm;
				comm_input.style.width = '50px';
				comm_input.style.textAlign = 'right';
				cell7.appendChild(comm_input);
			}
			else
			{
				var comm_input = document.createElement('input');
				comm_input.type = 'hidden';
				comm_input.name = 'comm_input_' + lid;
				comm_input.id = 'comm_input_' + lid;
				comm_input.value = comm;
				cell7.appendChild(comm_input);
			}
		}
		//else
		//{
		//	textNode = document.createTextNode(comm);
		//	cell7.appendChild(textNode);
		//}
		
		// cell 8 - text
		var cell8 = row.insertCell(8);
		textNode = document.createTextNode(statusStr);
		cell8.style.width = '100px';
		cell8.style.textAlign = 'right';
		cell8.appendChild(textNode);

		// cell 9 - text
		var cell9 = row.insertCell(9);
		cell9.style.width = '40px';
		cell9.style.textAlign = 'right';
		
		if (isc)
		{
			var save_image = document.createElement('img');
			save_image.src = '../images/save-icon.gif';
			save_image.title = 'Save';
			save_image.onclick = function () {if (confirm('Save Orderline?')) {processCommand('saveOrderline',lid+'|'+document.getElementById('prac_sel_' + lid).value+'|'+document.getElementById('comm_input_' + lid).value)}};
			cell9.appendChild(save_image);
			
			var spacerNode = document.createTextNode(' ');
			cell9.appendChild(spacerNode);
			
			var delete_image = document.createElement('img');
			delete_image.src = '../images/delete_icon.gif';
			delete_image.title = 'Delete';
			delete_image.onclick = function () {if (confirm('Delete Orderline?')) {processCommand('deleteOrderline',lid+'|'+document.getElementById('prac_sel_' + lid).value+'|'+document.getElementById('comm_input_' + lid).value)}};
			cell9.appendChild(delete_image);
		}
		else
		{
			var textNode = document.createTextNode('');
			cell9.appendChild(textNode);
		}
		
		// cell 10 - text
		var cell10 = row.insertCell(10);
		var textNode = document.createTextNode('');
		cell10.style.width = '3px';
		cell10.appendChild(textNode);
    }

    var last_code;
	var qty = 0;

	function selectCode(code)
	{
		if (code == last_code)
			qty += 1;
		else
			qty = 1;

		document.getElementById("qty").value = qty;
		document.getElementById("rate").value = codeArray[code]["cost"];

		last_code = code;
		calculateAmount();
	}

	function calculateAmount()
	{
		//alert("hey >" + document.getElementById("rate0"));

		// loop through the items

		var total = 0;

		for (var i = 0; document.getElementById("rate" + i); i++)
		{
			var rate = document.getElementById("rate" + i).value;
			var qty = document.getElementById("received" + i).value;

			var rate_valid = rate != '' && !isNaN(rate);
			var qty_valid = qty != '' && !isNaN(qty);

			if (qty_valid && rate_valid)
				total += (parseFloat(qty) * parseFloat(rate));
		}

		var tax = document.getElementById("tax").value;
		var tax_valid = tax != '' && !isNaN(tax);
		if (tax_valid)
			total += parseFloat(tax);

		var shipping = document.getElementById("shipping").value;
		var shipping_valid = shipping != '' && !isNaN(shipping);
		if (shipping_valid)
			total += parseFloat(shipping);

		var discount = document.getElementById("discount").value;
		var discount_valid = discount != '' && !isNaN(discount);
		if (discount_valid)
			total -= parseFloat(discount);

		//alert(total);
		document.getElementById("total").value = total.formatMoney(2, '.', '');

		/*


		if (qty_valid && rate_valid)
			document.getElementById("amount").value = (parseFloat(qty) * parseFloat(rate)).formatMoney(2, '.', '');
		*/
	}

    Number.prototype.formatMoney = function(c, d, t){
	var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    };

	function showReport()
	{
		//alert('showReport stuff');
		processCommand('purchaseOrderReport', 1);
	}

	function addItemRow(code_id, code, type, pa, dept, com)
	{
		var tbl = document.getElementById('ledger-table');
		var nextRow = tbl.tBodies[0].rows.length;
		var iteration = nextRow + 1;

		num = nextRow;

		// add the row
		var row = tbl.tBodies[0].insertRow(num);
		row.style.height = '18px';
		var textNode;
		var cell0;

		// CONFIG: requires classes named classy0 and classy1
		//row.className = 'classy' + (iteration % 2);

		/*
		if (code == 'No Items Found')
		{
			// cell 0 - text
			cell0 = row.insertCell(0);
			textNode = document.createTextNode(desc);
			cell0.style.textAlign = 'left';
			cell0.colSpan = '5';
			cell0.appendChild(textNode);
			tbl.tBodies[0].rows[0].cells[0].style.width = 518;
			return;
		}
		*/

		// CONFIG: This whole section can be configured

		var cellx = row.insertCell(0);
		textNode = document.createTextNode('');
		cellx.style.width = '3px';
		cellx.appendChild(textNode);

		// cell 0 - text
		cell0 = row.insertCell(1);
		cell0.style.verticalAlign = 'top';
		cell0.style.width = '242px';
		var anchor = document.createElement('a');
		url = "schedule-administration-checkout-code.jsp?id=" + code_id;
		anchor.setAttribute("href",url);
		textNode = document.createTextNode(code);
		anchor.appendChild(textNode);
		cell0.appendChild(anchor);

		// cell 1 - text
		var cell1 = row.insertCell(2);
		textNode = document.createTextNode(type);
		cell1.style.verticalAlign = 'top';
		cell1.style.textAlign = 'left';
		cell1.style.width = '70px';
		cell1.appendChild(textNode);

		// cell 2 - text
		var cell2 = row.insertCell(3);
		textNode = document.createTextNode(pa);
		cell2.style.textAlign = 'left';
		cell2.style.width = '190px';
		cell2.appendChild(textNode);

		// cell 3 - text
		var cell3 = row.insertCell(4);
		textNode = document.createTextNode(dept);
		cell3.style.textAlign = 'left';
		cell3.style.width = '69px';
		cell3.appendChild(textNode);

		// cell 4 - text
		var cell4 = row.insertCell(5);
		textNode = document.createTextNode(com);
		cell4.style.textAlign = 'right';
		cell4.style.width = '20px';
		cell4.appendChild(textNode);

		// cell 5 - text
		var cell5 = row.insertCell(6);
		textNode = document.createTextNode(' ');
		cell5.style.textAlign = 'right';
		cell5.style.width = '3px';
		cell5.appendChild(textNode);
	}

	function deleteAllRows(tbl)
	{
		for (var i = tbl.tBodies[0].rows.length; i > 0; i--)
			tbl.tBodies[0].rows[i - 1].parentNode.deleteRow(i - 1);
	}

	function showDepartments(xml_str)
	{
		deleteAllRows(document.getElementById('ledger-table'));
		for (i = 0; xml_str.childNodes[i]; i++)
		{
			var key = xml_str.childNodes[i].childNodes[0].childNodes[0].nodeValue;
			var value = xml_str.childNodes[i].childNodes[1].childNodes[0].nodeValue;
			//alert(value);

			eval("var deptArray = " + value);
			//element_obj.options[index] = new Option(contactArray["label"],contact_key);
			addDepartmentRow(key, deptArray["label"], deptArray["progress"], deptArray["change"]);
		}

		adjustTable();
	}

	function saveCount()
	{
		processCommand('saveCount');
	}

	function finishCount()
	{
		if (confirm('Finish & Apply Changes to Inventory?  Are you sure?'))
		{
			document.forms[0].submit();
		}
	}

	function showAll()
	{
		processCommand('showLedgerAll');
	}

	function showMonth()
	{
		processCommand('showLedgerThisMonth');
	}

	function showYear()
	{
		processCommand('showLedgerThisYear');
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

    <script type="text/javascript">
    YAHOO.util.Event.onDOMReady(

        function() {

            // Create a panel Instance, from the 'resizablepanel' DIV standard module markup
            panel = new YAHOO.widget.Panel("resizablepanel", {
                draggable: true,
                width: "600px",
                height: "550px",
                autofillheight: "body", // default value, specified here to highlight its use in the example
                constraintoviewport:true,
				visible:false
            });
            panel.render();

            // Create Resize instance, binding it to the 'resizablepanel' DIV
            var resize = new YAHOO.util.Resize("resizablepanel", {
                handles: ["br"],
                autoRatio: false,
                minWidth: 300,
                minHeight: 100,
                status: false
            });

            // Setup startResize handler, to constrain the resize width/height
            // if the constraintoviewport configuration property is enabled.
            resize.on("startResize", function(args) {

    		    if (this.cfg.getProperty("constraintoviewport")) {
                    var D = YAHOO.util.Dom;

                    var clientRegion = D.getClientRegion();
                    var elRegion = D.getRegion(this.element);

                    resize.set("maxWidth", clientRegion.right - elRegion.left - YAHOO.widget.Overlay.VIEWPORT_OFFSET);
                    resize.set("maxHeight", clientRegion.bottom - elRegion.top - YAHOO.widget.Overlay.VIEWPORT_OFFSET);
	            } else {
                    resize.set("maxWidth", null);
                    resize.set("maxHeight", null);
	        	}

            }, panel, true);

            // Setup resize handler to update the Panel's 'height' configuration property
            // whenever the size of the 'resizablepanel' DIV changes.

            // Setting the height configuration property will result in the
            // body of the Panel being resized to fill the new height (based on the
            // autofillheight property introduced in 2.6.0) and the iframe shim and
            // shadow being resized also if required (for IE6 and IE7 quirks mode).
            resize.on("resize", function(args) {
                var panelHeight = args.height;
                this.cfg.setProperty("height", panelHeight + "px");
            }, panel, true);
			
			if (document.getElementById('lastname').value.length > 0) {processCommand('getPeopleByLastName', document.getElementById('lastname').value.replace(/\'/g,'\\\''));}
			else if (document.getElementById('firstname').value.length > 0) {processCommand('getPeopleByFirstName', document.getElementById('firstname').value);}
			else if (document.getElementById('filenumber').value.length > 0) {processCommand('getPeopleByFileNumber', document.getElementById('filenumber').value);}
			
			//processCommand('showLedger');
        }
    );
    </script>




<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup2", function () {

            // Create a Button using an existing <input> element as a data source

            var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", {onclick: {fn: showMonth}});
            var oSubmitButton2 = new YAHOO.widget.Button("submitbutton2", {onclick: {fn: showYear}});
            var oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", {onclick: {fn: showAll}});

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
%>

<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>


</head>
<body class="yui-skin-sam" onload="javascript:initErrors(); <%= ledger_view.equals("year") ? "showYear();" : ledger_view.equals("month") ? "showMonth();" : "showAll();" %>">

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
	    <p class="headline"><%= adminCompany.getLabel() %> - Clients - <span id="selectedPerson"><%= adminPerson.isNew() ? "No Client Selected" : adminPerson.getLabel() %></span> - Ledger</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-client-menu.jsp" %>

	    <div class="mainw">
<!--
		<p class="headlineA"></p>

		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
-->
		<struts-html:form action="/admin/inventoryCount" >

			<input type="hidden" name="delete_id" value="" />

			<div id="resizablepanel">
				<div class="hd">Sales Receipt</div>
				<div class="bd">
					<div style="width: 100%;">
						<div id="cos_div">
						</div>
					</div>
				</div>
				<div class="ft"></div>
			</div>

			<!--
		    <div class="adminItem"><strong>Enter search text</strong> to find matching inventory items or <strong>click to view your entire inventory</strong>.</div>
			-->

			<!--
			<div class="adminItem">
				<div class="leftTM">LEDGER SEARCH</div>
				<div class="right">
					<input onkeyup="if (this.value.length > 1) { processCommand('getCheckoutCodesByDescInv', this.value + '|' + document.forms[0].vendorSearchInput.value); }" name="searchInput" value="<%= search_str %>" onfocus="select();" class="inputbox" style="width: 164px;" />
				</div>
				<div class="end"></div>
			</div>
			-->

			<div class="adminItem">
				<div id="submitbuttonsfrommarkup2">
					<input id="submitbutton1" class="formbutton" type="button" name="submit_button1" value="Show This Month" alt="Show This Month" style="margin-right: 10px; "/>
					<input id="submitbutton2" class="formbutton" type="button" name="submit_button2" value="Show This Year" alt="Show This Year" style="margin-right: 10px; "/>
					<input id="submitbutton3" class="formbutton" type="button" name="submit_button3" value="Show Entire Ledger" alt="Show Entire Ledger" style="margin-right: 10px; "/>
				</div>
				<div class="end"></div>
			</div>

		    <div class="content_AdministrationTable">

			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
			    <div class="heading">
				    <table cellspacing="0" cellpadding="0" border="0" summary="" width="100%" >

					    <tr style="display: block;">
						    <td style="width:   3px; text-align: left;  ">&nbsp;</td>
						    <td style="width:  72px; text-align: left;  ">#</td>
						    <td style="width:  70px; text-align: left;  ">Date</td>
						    <td style="width: 285px; text-align: left;  ">Description</td>
						    <td style="width:  30px; text-align: left;  ">Qty</td>
						    <td style="width:  69px; text-align: left;  ">Amount</td>
						    <td style="width:  60px; text-align: left;  ">Practitioner</td>
						    <td style="width:  60px; text-align: left;  ">Commission</td>
						    <td style="width: 100px; text-align: right;  ">Status</td>
						    <td style="width:  40px; text-align: right;  ">&nbsp;</td>
						    <td style="width:   3px; text-align: right;  ">&nbsp;</td>
					    </tr>
				    </table>
			    </div>
			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>


			    <div class="ledger">
				    <table id="ledger-table" cellspacing="" cellpadding="" border="0" summary="" width="100%">
					    <tr>
						    <td style="width:  792px; text-align: left;  ">Search To Find Items</td>
					    </tr>
				    </table>
			    </div>

			    <!-- END Item -->

			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		    </div>






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
