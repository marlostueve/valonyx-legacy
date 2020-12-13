<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, com.badiyan.torque.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qb.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="adminHerbDosage" class="com.badiyan.uk.online.beans.HerbDosage" scope="session" />

<%
CompanySettingsBean settings = adminCompany.getSettings();

if (request.getParameter("delete") != null)
{
    try
    {
		int code_id = Integer.parseInt(request.getParameter("delete"));
		CheckoutCodeBean test_code = CheckoutCodeBean.getCheckoutCode(code_id);
		if (test_code.getCompany().getId() == adminCompany.getId())
		{
			CheckoutCodeBean.delete(code_id);
			session.removeAttribute("adminCheckoutCode");
		}
    }
    catch (NumberFormatException x)
    {
    }
}

if (request.getParameter("new") != null && request.getParameter("new").equals("true"))
{
    
	adminHerbDosage = new HerbDosage();
	session.setAttribute("adminHerbDosage", adminHerbDosage);
    
}

if (request.getParameter("id") != null)
{
    try
    {
		int dosage_id = Integer.parseInt(request.getParameter("id"));
		HerbDosage test_dosage = HerbDosage.getHerbDosage(dosage_id);
		if (test_dosage.getCompany().getId() == adminCompany.getId())
		{
			adminHerbDosage = test_dosage;
			session.setAttribute("adminHerbDosage", adminHerbDosage);
		}
    }
    catch (NumberFormatException x)
    {
    }
}

Vector vendors = VendorRet.getVendors(adminCompany);
 

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
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/layout/container-min.js"></script>

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

    function processCommand(command, parameter, arg1, arg2, arg3, arg4, arg5, arg6, arg7)
    {
        if (window.ActiveXObject)
            httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
        else if (window.XMLHttpRequest)
            httpRequest = new XMLHttpRequest();

        httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain") %>/InventoryServlet.html', true);
        httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
        httpRequest.onreadystatechange = function() {processCommandEvent(); } ;

        eval("httpRequest.send('command=" + command + "&parameter=" + parameter + "&arg1=" + arg1 + "&arg2=" + arg2 + "&arg3=" + arg3 + "&arg4=" + arg4 + "&arg5=" + arg5 + "&arg6=" + arg6 + "&arg7=" + arg7 + "')");

        return true;
    }

    function processCommandEvent()
    {
		YAHOO.exa.con.wait.hide();
		
		if (httpRequest.readyState == 4)
		{
			if (httpRequest.status == 200)
			{
				if (httpRequest.responseXML.getElementsByTagName("codes").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("codes")[0];
					showCodes(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("directionSet").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("directionSet")[0];
					showDirections(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("mappings").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("mappings")[0];
					showMappings(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("purchase_order_report_url").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("purchase_order_report_url")[0];
					document.getElementById('cos_div').innerHTML = '<EMBED SRC="' + xml_response.childNodes[0].nodeValue + '" WIDTH="100%" HEIGHT="100%"></EMBED>';
					panel.show();
				}
				else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
					showPeople(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
				{
					showPerson(httpRequest.responseXML.getElementsByTagName("person")[0]);
					newDosage();
					//showDosages(httpRequest.responseXML.getElementsByTagName("dosages")[0]);
				}
				else if (httpRequest.responseXML.getElementsByTagName("herbReport").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("herbReport")[0];
					//alert(xml_response.childNodes[0].nodeValue);
					
					//document.getElementById('cos_div').innerHTML = '<EMBED SRC="../resources/' + xml_response.childNodes[0].nodeValue + '" WIDTH="100%" HEIGHT="100%"></EMBED>';
					//panel.show();
					
					//document.getElementById('cos_div').innerHTML = '<EMBED SRC="../resources/' + xml_response.childNodes[0].nodeValue + '" WIDTH="100%" HEIGHT="100%"></EMBED>';
					document.getElementById('cos_div').innerHTML = '<A href="../resources/' + xml_response.childNodes[0].nodeValue + '" WIDTH="100%" HEIGHT="100%">download report</A>';
					panel.show();
					
					//alert('reportvv');
					//var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
					//showPeople(xml_response);
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
	
	function showPerson(xml_str) {

		var key = xml_str.childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.childNodes[1].childNodes[0].nodeValue;
		eval("var pArray = " + value);
		document.getElementById('selectedPerson').firstChild.nodeValue = pArray["label"];
	}
	
	function newDosage() {
		
		removeDirectionsContainer();
		removeMappingContainer();
		
		var now = new Date();
		
		var monthStr = '' + (now.getMonth() + 1);
		if ((now.getMonth() + 1) < 10)
			monthStr = '0' + (now.getMonth() + 1);
		
		var dateStr = '' + now.getDate();
		if (now.getDate() < 10)
			dateStr = '0' + now.getDate();
		
		var yearStr = now.getFullYear() + '';
		yearStr = yearStr.substring(2,4);
		
		document.forms[0].mixDate.value = monthStr + '/' + dateStr + '/' + yearStr;
		document.forms[0].mixDesc.value = '';
		document.forms[0].notes.value = '';
		
		document.forms[0].day1.value = '';
		document.forms[0].day2.value = '';
		document.forms[0].ml.value = '';
		document.forms[0].mixIn.value = '';
		document.forms[0].times.value = '';
		document.forms[0].period.value = '';
	}

    function selectPerson()
    {
		//unselectPlan();
		processCommand('selectPerson', document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
		
		//processCommand('getPersonDetails', document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
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

    function showCodes(xml_str)
    {
		codeArray = new Array();

        //document.forms[0].codeSelect.options.length = 0;

		deleteAllRows(document.getElementById('inventory-table'));

        var index = 0;
        while (xml_str.getElementsByTagName("code")[index] != null)
        {
            key = xml_str.getElementsByTagName("code")[index].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("code")[index].childNodes[1].childNodes[0].nodeValue;
            eval("var codeArr = " + value);

			codeArray[key] = codeArr;

			//document.forms[0].codeSelect.options[index] = new Option(codeArr["desc"],key);

			addItemRow(key, codeArr["desc"], codeArr["type"], codeArr["pa"], codeArr["dept"], codeArr["com"]);

			index++;
        }
    }

	function showMappings(xml_str)
	{
		removeMappingContainer();
		
		var parent_container_div = document.getElementById('mappingParent');
		
		var mapping_container_div = document.createElement('div');
		mapping_container_div.setAttribute('id', 'mappingContainer');
		parent_container_div.appendChild(mapping_container_div);
		
		for (var p = 0; xml_str.childNodes[p]; p++)
		{
			var key = xml_str.childNodes[p].childNodes[0].childNodes[0].nodeValue;
			var value = xml_str.childNodes[p].childNodes[1].childNodes[0].nodeValue;
			eval("var mappingArray = " + value);
			showMappingItem(mappingArray["label"], mappingArray["p1"], mappingArray["p2"], mappingArray["p3"], mappingArray["p4"], mappingArray["p5"], mappingArray["p6"], mappingArray["p7"], mappingArray["p8"], mappingArray["p9"], 'true', p);
		}
		
		addBottomLine(xml_str.getAttribute("a1"), xml_str.getAttribute("a2"), xml_str.getAttribute("a3"), xml_str.getAttribute("a4"), xml_str.getAttribute("a5"), xml_str.getAttribute("a6"), xml_str.getAttribute("a7"));
	}

	function showDirections(xml_str)
	{
		removeDirectionsContainer();
		
		var parent_container_div = document.getElementById('directionsParent');
		
		var directions_container_div = document.createElement('div');
		directions_container_div.setAttribute('id', 'directionsContainer');
		directions_container_div.setAttribute('class', 'right');
		parent_container_div.appendChild(directions_container_div);
		
		for (var p = 0; xml_str.childNodes[p]; p++)
		{
			var key = xml_str.childNodes[p].childNodes[0].childNodes[0].nodeValue;
			var value = xml_str.childNodes[p].childNodes[1].childNodes[0].nodeValue;
			eval("var directionsArray = " + value);
			showDirectionsItem(directionsArray["label"], p);
		}
		addEndTag();
		
		document.forms[0].day1.value = '';
		document.forms[0].day2.value = '';
		document.forms[0].ml.value = '';
		document.forms[0].mixIn.value = '';
		document.forms[0].times.value = '';
		document.forms[0].period.value = '';
	}
			
	function removeDirectionsContainer() {
		var parent = document.getElementById('directionsParent');
		if (parent && parent.hasChildNodes()) {
			var child = document.getElementById('directionsContainer');
			if (child)
				parent.removeChild(child);
			child = document.getElementById('endDiv');
			if (child)
				parent.removeChild(child);
		}
	}
	
	function removeMappingContainer() {
		var parent = document.getElementById('mappingParent');
		if (parent) {
			var child = document.getElementById('mappingContainer');
			if (child)
				parent.removeChild(child);
			child = document.getElementById('mappingBottomLine');
			if (child)
				parent.removeChild(child);
		}
	}
	
	/*
					<div class="organization">
						<table cellspacing="" cellpadding="" border="0" summary="">
							<tr>
								<td style="width:   3px; ">&nbsp;</td>
								<td style="width: 200px; text-align: left;  ">&nbsp;</td>
								<td style="width:  40px; text-align: center;  ">&nbsp;</td>
								<td style="width:  40px; text-align: center;  ">&nbsp;</td>
								<td style="width:  40px; text-align: center;  ">&nbsp;</td>
								<td style="width:  40px; text-align: center;  ">&nbsp;</td>
								<td style="width:  40px; text-align: center;  ">&nbsp;</td>
								<td style="width:  40px; text-align: center;  ">&nbsp;</td>
								<td style="width:  40px; text-align: center;  ">&nbsp;</td>
								<td style="width:  40px; text-align: center;  ">&nbsp;</td>
								<td style="width:  40px; text-align: center;  ">&nbsp;</td>
								<td style="width:   3px; ">&nbsp;</td>
							</tr>
						</table>
					</div>
	 */
			
	function showMappingItem(label, p3, p4, p5, p6, p7, p8, p9, p10, p11, del, pos) {
		
		var mapping_container_div = document.getElementById('mappingContainer');
		
		var item_container_div = document.createElement('div');
		item_container_div.setAttribute('class', 'organization');
		mapping_container_div.appendChild(item_container_div);
		
		var tbl_div = document.createElement('table');
		tbl_div.setAttribute('cellspacing', '0');
		tbl_div.setAttribute('cellpadding', '0');
		tbl_div.setAttribute('border', '0');
		tbl_div.setAttribute('summary', '0');
		item_container_div.appendChild(tbl_div);
		
		var tr_x = document.createElement('tr');
		tbl_div.appendChild(tr_x);
		
		var td_1 = document.createElement('td');
		td_1.setAttribute('style', 'width:   3px; ');
		var labelNode_1 = document.createTextNode('');
		td_1.appendChild(labelNode_1);
		tr_x.appendChild(td_1);
	
		var td_2 = document.createElement('td');
		td_2.setAttribute('style', 'width: 195px; text-align: left;  ');
		var labelNode_2 = document.createTextNode(label);
		td_2.appendChild(labelNode_2);
		tr_x.appendChild(td_2);
	
		var td_3 = document.createElement('td');
		td_3.setAttribute('style', 'width:  39px; text-align: center;  ');
		var labelNode_3 = document.createTextNode(p3);
		td_3.appendChild(labelNode_3);
		tr_x.appendChild(td_3);
	
		var td_4 = document.createElement('td');
		td_4.setAttribute('style', 'width:  39px; text-align: center;  ');
		var labelNode_4 = document.createTextNode(p4);
		td_4.appendChild(labelNode_4);
		tr_x.appendChild(td_4);
	
		var td_5 = document.createElement('td');
		td_5.setAttribute('style', 'width:  39px; text-align: center;  ');
		var labelNode_5 = document.createTextNode(p5);
		td_5.appendChild(labelNode_5);
		tr_x.appendChild(td_5);
	
		var td_6 = document.createElement('td');
		td_6.setAttribute('style', 'width:  39px; text-align: center;  ');
		var labelNode_6 = document.createTextNode(p6);
		td_6.appendChild(labelNode_6);
		tr_x.appendChild(td_6);
	
		var td_7 = document.createElement('td');
		td_7.setAttribute('style', 'width:  39px; text-align: center;  ');
		var labelNode_7 = document.createTextNode(p7);
		td_7.appendChild(labelNode_7);
		tr_x.appendChild(td_7);
	
		var td_8 = document.createElement('td');
		td_8.setAttribute('style', 'width:  39px; text-align: center;  ');
		var labelNode_8 = document.createTextNode(p8);
		td_8.appendChild(labelNode_8);
		tr_x.appendChild(td_8);
	
		var td_9 = document.createElement('td');
		td_9.setAttribute('style', 'width:  39px; text-align: center;  ');
		var labelNode_9 = document.createTextNode(p9);
		td_9.appendChild(labelNode_9);
		tr_x.appendChild(td_9);
	
		var td_10 = document.createElement('td');
		td_10.setAttribute('style', 'width:  39px; text-align: center;  ');
		var labelNode_10 = document.createTextNode(p10);
		td_10.appendChild(labelNode_10);
		tr_x.appendChild(td_10);
	
		var td_11 = document.createElement('td');
		td_11.setAttribute('style', 'width:  39px; text-align: center;  ');
		var labelNode_11 = document.createTextNode(p11);
		td_11.appendChild(labelNode_11);
		tr_x.appendChild(td_11);
	
		var td_12 = document.createElement('td');
		td_12.setAttribute('style', 'width:  14px; ');
		if (del) {
			var delete_image = document.createElement('img');
			delete_image.src = '../images/delete_icon.gif';
			delete_image.title = 'Delete';
			delete_image.onclick = function () {if (confirm('Delete?')) {processCommand('deleteHerb',pos)}};
			td_12.appendChild(delete_image);
		}
		tr_x.appendChild(td_12);
	
		var td_13 = document.createElement('td');
		td_13.setAttribute('style', 'width:   3px; ');
		var labelNode_13 = document.createTextNode('');
		td_13.appendChild(labelNode_13);
		tr_x.appendChild(td_13);
		
	}
			
	function showDirectionsItem(label, pos) {
		
		var directions_container_div = document.getElementById('directionsContainer');
		
		var span_div = document.createElement('span');
		var labelNode = document.createTextNode(label + ' ');
		span_div.appendChild(labelNode);
		
		var delete_image = document.createElement('img');
		delete_image.src = '../images/delete_icon.gif';
		delete_image.title = 'Delete';
		delete_image.onclick = function () {if (confirm('Delete?')) {processCommand('deleteDirections',pos)}};
		span_div.appendChild(delete_image);
		
		directions_container_div.appendChild(span_div);
		var br_div = document.createElement('br');
		directions_container_div.appendChild(br_div);
	}
			
	function addEndTag() {
		
		var parent_container_div = document.getElementById('directionsParent');
		
		// <div id="endDiv" class="end"></div>
		
		var end_div = document.createElement('div');
		end_div.setAttribute('id', 'endDiv');
		end_div.setAttribute('class', 'end');
		parent_container_div.appendChild(end_div);
	}
	
	function addBottomLine(a1, a2, a3, a4, a5, a6, a7) {
		
		/*
		* <div id="mappingBottomLine" class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
		*/
		
		//var parent_container_div = document.getElementById('mappingParent');
		var parent_container_div = document.getElementById('mappingContainer');
		
		var bottom_line_div = document.createElement('div');
		bottom_line_div.setAttribute('id', 'mappingBottomLine');
		bottom_line_div.setAttribute('class', 'horizontalRule');
		var img_tag = document.createElement('img');
		img_tag.setAttribute('src', '../images/trspacer.gif');
		img_tag.setAttribute('width', '1');
		img_tag.setAttribute('height', '1');
		img_tag.setAttribute('alt', '');
		bottom_line_div.appendChild(img_tag);
		
		parent_container_div.appendChild(bottom_line_div);
		
		//alert('bott');
		
		showMappingItem('', a1, a2, a3, a4, '-', '-', a5, a6, a7);
		
		//alert('bott2');
		
		var bottom_line_div2 = document.createElement('div');
		bottom_line_div2.setAttribute('id', 'mappingBottomLine');
		bottom_line_div2.setAttribute('class', 'horizontalRule');
		var img_tag2 = document.createElement('img');
		img_tag2.setAttribute('src', '../images/trspacer.gif');
		img_tag2.setAttribute('width', '1');
		img_tag2.setAttribute('height', '1');
		img_tag2.setAttribute('alt', '');
		bottom_line_div2.appendChild(img_tag);
		
		parent_container_div.appendChild(bottom_line_div2);
	}

    var last_code;
	var qty = 0;

    Number.prototype.formatMoney = function(c, d, t){
	var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    };

	function showReport()
	{
		YAHOO.exa.con.wait.show();
		//alert('showReport stuff');
		processCommand('herbReport', '-1');
	}

	function addHerb(herbId)
	{
		var mlDosage = document.getElementById('ml-' + herbId);
		//alert('addHerb stuff >' + mlDosage.value);
		processCommand('addHerb', herbId, mlDosage.value);
	}

	function addItemRow(code_id, code, type, pa, dept, com)
	{
		var tbl = document.getElementById('inventory-table');
		var nextRow = tbl.tBodies[0].rows.length;
		var iteration = nextRow + 1;

		num = nextRow;

		// add the row
		var row = tbl.tBodies[0].insertRow(num);
		row.style.height = '18px';
		var textNode;
		var cell0;

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
		//textNode = document.createTextNode(type);
		cell1.style.verticalAlign = 'top';
		cell1.style.textAlign = 'left';
		cell1.style.width = '70px';
		//cell1.appendChild(textNode);

		// cell 2 - text
		var cell2 = row.insertCell(3);
		textNode = document.createTextNode(' ml');
		//<input name="ml" value="" onfocus="select();" class="inputbox" style="width: 20px;" />
		
		var ml_input_element = document.createElement('input');
		ml_input_element.setAttribute('id', 'ml-' + code_id);
		ml_input_element.setAttribute('name', 'ml-' + code_id);
		ml_input_element.setAttribute('onfocus', 'select();');
		ml_input_element.setAttribute('class', 'inputbox');
		ml_input_element.setAttribute('maxlength', '3');
		ml_input_element.setAttribute('style', 'width: 32px;');
		cell2.style.textAlign = 'left';
		cell2.style.width = '90px';
		cell2.appendChild(ml_input_element);
		cell2.appendChild(textNode);

		// cell 3 - text
		var cell3 = row.insertCell(4);
		//textNode = document.createTextNode(dept);
		cell3.style.textAlign = 'left';
		cell3.style.width = '69px';
		
		//<input id="submitbutton3" class="formbutton" type="button" name="submit_button3" value="Show All Herbs" alt="Show All Herbs" style="margin-right: 10px; "/>
		
		var submit_element = document.createElement('input');
		submit_element.setAttribute('name', 'sub-' + code_id);
		submit_element.setAttribute('type', 'button');
		submit_element.setAttribute('class', 'formbutton');
		submit_element.setAttribute('id', 'sub-' + code_id);
		submit_element.setAttribute('value', 'Add');
		submit_element.setAttribute('alt', 'Add');
		submit_element.setAttribute('onclick', 'addHerb(' + code_id + ');');
		submit_element.setAttribute('style', 'width: 64px;');
		
		//cell3.appendChild(textNode);
		cell3.appendChild(submit_element);

		// cell 4 - text
		var cell4 = row.insertCell(5);
		//textNode = document.createTextNode(com);
		cell4.style.textAlign = 'right';
		cell4.style.width = '20px';
		//cell4.appendChild(textNode);

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
		deleteAllRows(document.getElementById('inventory-table'));
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

	function showAll()
	{
		processCommand('showAllHerbs');
	}

	function addDirections()
	{
		//alert('xxx');
		var arg1 = document.forms[0].day1.value;
		var arg2 = document.forms[0].day2.value;
		var arg3 = document.forms[0].ml.value;
		var arg4 = document.forms[0].mixIn.value;
		var arg5 = document.forms[0].times.value;
		var arg6 = document.forms[0].period.value;
		var arg7 = document.forms[0].measure.value;
		
		//alert('arg1 >' + arg1);
		
		processCommand('addDirections',arg1,arg2,arg3,arg4,arg5,arg6,arg7);
	}

	function save()
	{
		YAHOO.exa.con.wait.show();
		
		var arg1 = document.forms[0].mixDate.value;
		var arg2 = escape(document.forms[0].mixDesc.value);
		//replacecarriagereturn(document.forms[0].notes, '|');
		//var arg3 = document.forms[0].notes.value;
		var arg3 = escape(replacecarriagereturn(document.forms[0].notes, '|'));
		
		processCommand('saveHerbDosage',arg1,arg2,arg3);
	}
	
	function replacecarriagereturn(textarea,replaceWith) {
		
		var textareaValue = escape(textarea.value);
		//encode all characters in text area
		//to find carriage return character
		for(i = 0; i < textareaValue.length; i++)
		{
			//loop through string, replacing carriage return 
			//encoding with HTML break tag
			if(textareaValue.indexOf("%0D%0A") > -1)
			{
				//Windows encodes returns as \r\n hex
				textareaValue = textareaValue.replace("%0D%0A",replaceWith);
			}
			else if(textareaValue.indexOf("%0A") > -1)
			{
				//Unix encodes returns as \n hex
				textareaValue = textareaValue.replace("%0A",replaceWith);
			}
			else if(textareaValue.indexOf("%0D") > -1)
			{
				//Macintosh encodes returns as \r hex
				textareaValue = textareaValue.replace("%0D",replaceWith);
			}
		}
		//textarea.value=unescape(textarea.value);
		return unescape(textareaValue);
		//decode all characters in text area back
	}
	
	function replacepipe(baseText,replaceWith) {
		
		var textareaValue = escape(baseText);
		//encode all characters in text area
		//to find carriage return character
		for(i = 0; i < textareaValue.length; i++)
		{
			//loop through string, replacing carriage return 
			//encoding with HTML break tag
			
			textareaValue = textareaValue.replace(replaceWith, "%0D%0A");
			
			if(textareaValue.indexOf("%0D%0A") > -1)
			{
				//Windows encodes returns as \r\n hex
				
			}
			else if(textareaValue.indexOf("%0A") > -1)
			{
				//Unix encodes returns as \n hex
				textareaValue = textareaValue.replace("%0A",replaceWith);
			}
			else if(textareaValue.indexOf("%0D") > -1)
			{
				//Macintosh encodes returns as \r hex
				textareaValue = textareaValue.replace("%0D",replaceWith);
			}
		}
		//textarea.value=unescape(textarea.value);
		return unescape(textareaValue);
		//decode all characters in text area back
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
        }
    );
    </script>




<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup2", function () {

            // Create a Button using an existing <input> element as a data source

            var oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", {onclick: {fn: showAll}});

        });

    } ();

</script>

<script type="text/javascript">

    YAHOO.example.init = function () {

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup3", function () {

            var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", {onclick: {fn: addDirections}});

        });

    } ();

</script>

<script type="text/javascript">

    YAHOO.example.init = function () {

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup4", function () {

            var oSubmitButton5 = new YAHOO.widget.Button("submitbutton5", {onclick: {fn: save}});
            var oSubmitButton6 = new YAHOO.widget.Button("submitbutton6", {onclick: {fn: showReport}});

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


				

<script type="text/javascript">
    YAHOO.namespace("exa.con");
    function initX() {
        if (!YAHOO.exa.con.wait) {
            YAHOO.exa.con.wait = new YAHOO.widget.Panel("wait", {width: "240px",fixedcenter: true,close: false,draggable: false,zindex:4,modal: true,visible: false} );
            YAHOO.exa.con.wait.setHeader("Working, please wait...");
			//YAHOO.exa.con.wait.setBody("<img src=\"http://l.yimg.com/a/i/us/per/gr/gp/rel_interstitial_loading.gif\"/>");
            YAHOO.exa.con.wait.setBody("<img src=\"../images/rel_interstitial_loading.gif\"/>");
            YAHOO.exa.con.wait.render(document.body); }
    }
</script>


<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>


</head>
<body class="yui-skin-sam" onload="javascript:initErrors();initX();<%= (search_str.equals("") && vendor_search_str.equals("")) ? "" : "processCommand('getCheckoutCodesByDescHerbs','" + search_str + "');" %>">

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
	    <p class="headline"><%= adminCompany.getLabel() %> - Clients - <span id="selectedPerson"><%= adminPerson.isNew() ? "No Client Selected" : adminPerson.getLabel() %></span> - Herb Mixture</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-client-menu.jsp" %>

	    <div class="main">
<!--
		<p class="headlineA"></p>

		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
-->
		<struts-html:form action="/admin/inventoryCount" >

			<input type="hidden" name="delete_id" value="" />

			<div id="resizablepanel">
				<div class="hd">Herb Spreadsheet</div>
				<div class="bd">
					<div style="width: 100%;">
						<div id="cos_div">
						</div>
					</div>
				</div>
				<div class="ft"></div>
			</div>
<%
// onfocus="getDate('courseBasicForm','releasedDateInput');select();"
Date mixDate = null;
if (adminHerbDosage.isNew())
	mixDate = new Date();
else
	mixDate = adminHerbDosage.getMixDate();
%>
			<div class="adminItem">
				<div class="leftTM">MIX DATE</div>
				<div class="right">
					<input name="mixDate" value="<%= CUBean.getUserDateString(mixDate) %>" onfocus="select();" class="inputbox" style="width: 64px;" />
				</div>
				<div class="end"></div>
			</div>

			<div class="adminItem">
				<div class="leftTM">MIXTURE DESC</div>
				<div class="right">
					<input name="mixDesc" value="<%= adminHerbDosage.getDescription() %>" maxlength="100" onfocus="select();" class="inputbox" style="width: 364px;" />
				</div>
				<div class="end"></div>
			</div>

			<div class="adminItem">
				<div class="leftTM">NOTES</div>
				<div class="right">
					<textarea name="notes" rows="3" style="width: 364px;" ><%= adminHerbDosage.getNotes().replaceAll("\\|", "\r\n") %></textarea>
				</div>
				<div class="end"></div>
			</div>

			<div id="directionsParent" class="adminItem">
				<div class="leftTM">DIRECTIONS</div>
				<div id="directionsContainer" class="right">
<%
try {
	Iterator directionsItr = adminHerbDosage.getUseDirections().iterator();
	for (int pp = 0; directionsItr.hasNext(); pp++) {
		
		HerbDosageUseDirections directions = (HerbDosageUseDirections)directionsItr.next();
		
%>
					<span><%= directions.getLabel() %>&nbsp;<img src="../images/delete_icon.gif" title="Delete" onclick="if (confirm('Delete?')) {processCommand('deleteDirections','<%= pp %>')}" ></span><br />
<%
	}
} catch (Exception x) {
	x.printStackTrace();
}
%>
				</div>
				<div id="endDiv" class="end"></div>
			</div>

			<div class="adminItem">
				<div class="leftTM">&nbsp;</div>
				<div id="submitbuttonsfrommarkup3" class="right">
					Day <input name="day1" value="" onfocus="select();" class="inputbox" style="width: 20px;" />-<input name="day2" value="" onfocus="select();" class="inputbox" style="width: 20px;" />
					&nbsp; <input name="ml" value="" onfocus="select();" class="inputbox" style="width: 20px;" /> <select name="measure" class="inputbox" style="width: 48px;"><option id="1">ml</option><option id="2">drops</option></select>
					in <select name="mixIn" class="inputbox" style="width: 64px;"><option id="1">Water</option><option id="2">Lotion</option><option id="3">Oil</option></select>
					<input name="times" value="" onfocus="select();" class="inputbox" style="width: 20px;" /> x
					daily @ <select name="period" class="inputbox" style="width: 64px;"><option id="1">Bedtime</option><option id="2">Dinner</option><option id="3">Mid-Afternoon</option><option id="4">Lunch</option><option id="5">Mid-Morning</option><option id="6">Breakfast</option><option id="7">Upon Rising</option><option id="8">at Mealtime</option><option id="8">at Mealtime</option><option id="8">pre-Workout</option><option id="8">post-Workout</option></select>
					<br /><br /><input id="submitbutton4" class="formbutton" type="button" name="submit_button4" value="Add Directions" alt="Add Directions" style="margin-right: 10px;"/>
				</div>
				<div class="end"></div>
			</div>

			<div class="adminItem">
				<div class="leftTM">HERBS</div>
				<div id="directionsContainer" class="right">&nbsp;
				</div>
				<div id="endDiv" class="end"></div>
			</div>

		    <div id="mappingParent" class="content_AdministrationTable" style="width: 566px;" >

			    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			    <div class="heading">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">

					    <tr style="display: block;">
						    <td style="width:   3px; ">&nbsp;</td>
						    <td style="width: 195px; text-align: left; vertical-align: bottom;  ">Liquid<br />Herb</td>
						    <td style="width:  39px; text-align: center; vertical-align: bottom;  ">MH Cost<br />Retail<br />200ml</td>
						    <td style="width:  39px; text-align: center; vertical-align: bottom;  ">MH<br />Cost<br />per 1ml</td>
						    <td style="width:  39px; text-align: center; vertical-align: bottom;  ">Herb<br />ml used</td>
						    <td style="width:  39px; text-align: center; vertical-align: bottom;  ">Dosage<br />Cost</td>
						    <td style="width:  39px; text-align: center; vertical-align: bottom;  ">40%<br />COGS<br />40ml</td>
						    <td style="width:  39px; text-align: center; vertical-align: bottom;  ">Retail<br />100ml</td>
						    <td style="width:  39px; text-align: center; vertical-align: bottom;  ">Retail<br />200ml</td>
						    <td style="width:  39px; text-align: center; vertical-align: bottom;  ">40%<br />COGS</td>
						    <td style="width:  39px; text-align: center; vertical-align: bottom;  ">Total<br />Retail<br />40ml</td>
							<td style="width:  14px; ">&nbsp;</td>
						    <td style="width:   3px; ">&nbsp;</td>
					    </tr>
				    </table>
			    </div>
			    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

				<div id="mappingContainer">
<%
try {
	Iterator itemsItr = adminHerbDosage.getItems().iterator();
	if (itemsItr.hasNext()) {
		for (int yy = 0; itemsItr.hasNext(); yy++) {
			HerbDosageItemMapping itemMapping = (HerbDosageItemMapping)itemsItr.next();
			CheckoutCodeBean itemObj = itemMapping.getItem();
			
			/*
 String value = "{\"label\" : \"" + item.getLabel() + "\"," +
						" \"p1\" : \"" + item.getAmountString() + "\"," +
						" \"p2\" : \"" + _obj.getAmountPerMLString() + "\"," +
						" \"p3\" : \"" + _obj.getML() + "\"," +
						" \"p4\" : \"" + _obj.getDosageCostString() + "\"," +
						" \"p5\" : \"-\"," +
						" \"p6\" : \"-\"," +
						" \"p7\" : \"" + _obj.getDosageAmountString() + "\"," +
						" \"p8\" : \"" + _obj.getCOGSString() + "\"," +
						" \"p9\" : \"" + _obj.getTotalRetail40MLString() + "\" }";
 */
%>
					<div class="organization">
						<table cellspacing="" cellpadding="" border="0" summary="">
							<tr>
								<td style="width:   3px; ">&nbsp;</td>
								<td style="width: 195px; text-align: left;  "><%= itemObj.getLabel() %>XX </td>
								<td style="width:  39px; text-align: center;  "><%= itemObj.getAmountString() %></td>
								<td style="width:  39px; text-align: center;  "><%= itemMapping.getAmountPerMLString() %></td>
								<td style="width:  39px; text-align: center;  "><%= itemMapping.getML() %></td>
								<td style="width:  39px; text-align: center;  "><%= itemMapping.getDosageCostString() %></td>
								<td style="width:  39px; text-align: center;  ">-</td>
								<td style="width:  39px; text-align: center;  ">-</td>
								<td style="width:  39px; text-align: center;  "><%= itemMapping.getDosageAmountString() %></td>
								<td style="width:  39px; text-align: center;  "><%= itemMapping.getCOGSString() %></td>
								<td style="width:  39px; text-align: center;  "><%= itemMapping.getTotalRetail40MLString() %></td>
								<td style="width:  14px; text-align: center;  "><img src="../images/delete_icon.gif" title="Delete" onclick="if (confirm('Delete?')) {processCommand('deleteHerb','<%= yy %>')}" ></td>
								<td style="width:   3px; ">&nbsp;</td>
							</tr>
						</table>
					</div>
<%
		}
%>

					<div id="mappingBottomLine" class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

					<div class="organization">
						<table cellspacing="" cellpadding="" border="0" summary="">
							<tr>
								<td style="width:   3px; ">&nbsp;</td>
								<td style="width: 195px; text-align: left;  ">&nbsp;</td>
								<td style="width:  39px; text-align: center;  "><%= adminHerbDosage.getMHCostRetail200MLTotalString() %></td>
								<td style="width:  39px; text-align: center;  "><%= adminHerbDosage.getMHCostPer1MLTotalString() %></td>
								<td style="width:  39px; text-align: center;  "><%= adminHerbDosage.getHerbMLUsedTotalString() %></td>
								<td style="width:  39px; text-align: center;  "><%= adminHerbDosage.getDosageCostTotalString() %></td>
								<td style="width:  39px; text-align: center;  ">-</td>
								<td style="width:  39px; text-align: center;  ">-</td>
								<td style="width:  39px; text-align: center;  "><%= adminHerbDosage.getRetail200MLTotalString() %></td>
								<td style="width:  39px; text-align: center;  "><%= adminHerbDosage.getFortyPercCOGSTotalString() %></td>
								<td style="width:  39px; text-align: center;  "><%= adminHerbDosage.getTotalRetail40MLTotalString() %></td>
								<td style="width:  14px; text-align: center;  ">&nbsp;</td>
								<td style="width:   3px; ">&nbsp;</td>
							</tr>
						</table>
					</div>
<%
	}
	else
	{
%>
					<div class="organization">
						<table id="inventory-table-" cellspacing="" cellpadding="" border="0" summary="">
							<tr>
								<td style="width:   3px; text-align: left;  ">&nbsp;</td>
								<td style="width: 200px; text-align: left;  ">Search To Find Herbs To Add</td>
								<td style="width:  40px; text-align: left;  ">&nbsp;</td>
								<td style="width:  40px; text-align: left;  ">&nbsp;</td>
								<td style="width:  40px; text-align: left;  ">&nbsp;</td>
								<td style="width:  40px; text-align: right;  ">&nbsp;</td>
								<td style="width:  40px; text-align: right;  ">&nbsp;</td>
								<td style="width:  40px; text-align: right;  ">&nbsp;</td>
								<td style="width:  40px; text-align: right;  ">&nbsp;</td>
								<td style="width:  40px; text-align: right;  ">&nbsp;</td>
								<td style="width:  40px; text-align: right;  ">&nbsp;</td>
								<td style="width:   3px; text-align: right;  ">&nbsp;</td>
							</tr>
						</table>
					</div>
<%
	}
} catch (Exception x) {
	x.printStackTrace();
}
%>

			    <div id="mappingBottomLine" class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

			    </div>

		    </div>

		    <div class="adminItem"><strong>Enter search text</strong> to find matching herbs or <strong>click to view all herbs</strong>.</div>

			<div class="adminItem">
				<div class="leftTM">HERB SEARCH</div>
				<div id="submitbuttonsfrommarkup2" class="right">
					<input onkeyup="if (this.value.length > 1) { processCommand('getCheckoutCodesByDescHerbs', this.value); }" name="searchInput" value="<%= search_str %>" onfocus="select();" class="inputbox" style="width: 164px;" />
					<input id="submitbutton3" class="formbutton" type="button" name="submit_button3" value="Show All Herbs" alt="Show All Herbs" style="margin-right: 10px; "/>
				</div>
				<div class="end"></div>
			</div>

		    <div class="content_AdministrationTable">

			    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			    <div class="heading">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">

					    <tr style="display: block;">
						    <td style="width:   3px; text-align: left;  "></td>
						    <td style="width: 242px; text-align: left;  ">Item</td>
						    <td style="width:  70px; text-align: left;  ">&nbsp;</td>
						    <td style="width:  90px; text-align: left;  ">&nbsp;</td>
						    <td style="width:  69px; text-align: left;  ">&nbsp;</td>
						    <td style="width:  20px; text-align: right;  ">&nbsp;</td>
						    <td style="width:   3px; text-align: right;  ">&nbsp;</td>
					    </tr>
				    </table>
			    </div>
			    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>


			    <div class="organization">
				    <table id="inventory-table" cellspacing="" cellpadding="" border="0" summary="">
					    <tr>
						    <td style="width:   3px; text-align: left;  ">&nbsp;</td>
						    <td style="width: 242px; text-align: left;  ">Search To Find Items</td>
						    <td style="width:  70px; text-align: left;  ">&nbsp;</td>
						    <td style="width:  90px; text-align: left;  ">&nbsp;</td>
						    <td style="width:  69px; text-align: left;  ">&nbsp;</td>
						    <td style="width:  20px; text-align: right;  ">&nbsp;</td>
						    <td style="width:   3px; text-align: right;  ">&nbsp;</td>
					    </tr>
				    </table>
			    </div>

			    <!-- END Item -->

			    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

		    </div>



		    <div class="adminItem"><strong>Click <%= adminHerbDosage.isNew() ? "Save" : "Update" %></strong> when you finished making changes.</div>

			<div class="adminItem">
				<div class="leftTM">&nbsp;</div>
				<div id="submitbuttonsfrommarkup4" class="right">
					<input id="submitbutton5" class="formbutton" type="button" name="submit_button5" value="<%= adminHerbDosage.isNew() ? "Save" : "Update" %>" alt="<%= adminHerbDosage.isNew() ? "Save" : "Update" %>" style="margin-right: 10px;"/>
					<input id="submitbutton6" class="formbutton" type="button" name="submit_button6" value="Excel Report" alt="Excel Report" style="margin-right: 10px;"/>
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
