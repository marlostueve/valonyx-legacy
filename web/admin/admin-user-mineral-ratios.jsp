<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, com.badiyan.torque.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qb.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="document" class="com.badiyan.uk.online.beans.MineralRatiosClientDocumentBean" scope="session" />

<%

if (request.getParameter("id") != null)
{
    try
    {
        document = MineralRatiosClientDocumentBean.getMineralRatiosClientDocument(Integer.parseInt(request.getParameter("id")));
	
    }
    catch (Exception x)
    {
    }
}
else if ((request.getParameter("new") != null) && (request.getParameter("new").equals("true")))
    document = new MineralRatiosClientDocumentBean();
    
session.setAttribute("document", document);




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
					showDosages(httpRequest.responseXML.getElementsByTagName("dosages")[0]);
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
	
	function showDosages(xml_str) {

		removeDosageContainer();
		
		var parent_container_div = document.getElementById('dosageParent');
		
		var dosage_container_div = document.createElement('div');
		dosage_container_div.setAttribute('id', 'dosageContainer');
		parent_container_div.appendChild(dosage_container_div);
		
		var dosagesFound = false;
		for (var q = 0; xml_str.childNodes[q]; q++)
		{
			var key = xml_str.childNodes[q].childNodes[0].childNodes[0].nodeValue;
			var value = xml_str.childNodes[q].childNodes[1].childNodes[0].nodeValue;
			//alert(key);
			eval("var dosArray = " + value);
			showDosageItem(key, dosArray["date"], dosArray["desc"], dosArray["mixer"], dosArray["note"], dosArray["days"]);
			dosagesFound = true;
		}
		
		//addBottomLine(xml_str.getAttribute("a1"), xml_str.getAttribute("a2"), xml_str.getAttribute("a3"), xml_str.getAttribute("a4"), xml_str.getAttribute("a5"), xml_str.getAttribute("a6"), xml_str.getAttribute("a7"));
		
		// <div id="dosageBottomLine" class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
		
		if (!dosagesFound) {
			showNoDosagesFound();
		}
		
		
		var bottom_div = document.createElement('div');
		bottom_div.setAttribute('id', 'dosageBottomLine');
		bottom_div.setAttribute('class', 'horizontalRule');
		
		var bottom_img = document.createElement('img');
		bottom_img.setAttribute('src', '../images/trspacer.gif');
		bottom_img.setAttribute('width', '1');
		bottom_img.setAttribute('height', '1');
		bottom_img.setAttribute('alt', '');
		bottom_div.appendChild(bottom_img);
		
		dosage_container_div.appendChild(bottom_div);
		
	
	}

/*
					<div class="organization">
						<table cellspacing="" cellpadding="" border="0" summary="">
							<tr>
								<td style="width:   3px; ">&nbsp;</td>
								<td style="width:  78px; text-align: center; font-weight: bolder;  ">stuff</td>
								<td style="width: 241px; text-align: left; font-weight: bolder;  "><a href="herb-dosage.jsp?id=">stuff</a></td>
								<td style="width: 178px; text-align: center; font-weight: bolder;  ">stuff</td>
								<td style="width:  49px; text-align: center; font-weight: bolder;  ">stuff</td>
								<td style="width:  14px; text-align: center;  "><img src="../images/delete_icon.gif" title="Delete" onclick="if (confirm('Delete?')) {processCommand('deleteHerbDosage','id')}" ></td>
								<td style="width:   3px; ">&nbsp;</td>
							</tr>
							<tr>
								<td style="width:   3px; ">&nbsp;</td>
								<td style="width: 560px; text-align: left; " colspan="5" >fgggg</td>
								<td style="width:   3px; ">&nbsp;</td>
							</tr>
						</table>
					</div>
 */


	function showNoDosagesFound() {
		
		var dosage_container_div = document.getElementById('dosageContainer');
		
		var dosage_item_div = document.createElement('div');
		dosage_item_div.setAttribute('class', 'organization');
		dosage_container_div.appendChild(dosage_item_div);
		
		var dosage_table = document.createElement('table');
		dosage_table.setAttribute('cellspacing', '0');
		dosage_table.setAttribute('cellpadding', '0');
		dosage_table.setAttribute('border', '0');
		dosage_table.setAttribute('summary', '');
		dosage_item_div.appendChild(dosage_table);
		
		var dosage_tr_1 = document.createElement('tr');
		dosage_table.appendChild(dosage_tr_1);
		
		var dosage_td_1_1 = document.createElement('td');
		dosage_td_1_1.setAttribute('style', 'width:   3px; ');
		dosage_tr_1.appendChild(dosage_td_1_1);
		
		var dosage_td_1_2 = document.createElement('td');
		dosage_td_1_2.setAttribute('style', 'width:  78px;  ');
		dosage_tr_1.appendChild(dosage_td_1_2);
		
		var dosage_td_1_3 = document.createElement('td');
		dosage_td_1_3.setAttribute('style', 'width: 241px; text-align: left; font-weight: bolder;  ');
		var labelNode_1_3 = document.createTextNode('No Mixtures Found');
		dosage_td_1_3.appendChild(labelNode_1_3);
		dosage_tr_1.appendChild(dosage_td_1_3);
		
		var dosage_td_1_4 = document.createElement('td');
		dosage_td_1_4.setAttribute('style', 'width: 178px;  ');
		dosage_tr_1.appendChild(dosage_td_1_4);
		
		var dosage_td_1_5 = document.createElement('td');
		dosage_td_1_5.setAttribute('style', 'width:  49px;  ');
		dosage_tr_1.appendChild(dosage_td_1_5);
		
		var dosage_td_1_6 = document.createElement('td');
		dosage_td_1_6.setAttribute('style', 'width:  14px;  ');
		dosage_tr_1.appendChild(dosage_td_1_6);
		
		var dosage_td_1_7 = document.createElement('td');
		dosage_td_1_7.setAttribute('style', 'width:   3px; ');
		dosage_tr_1.appendChild(dosage_td_1_7);
		
	}
	
	function showDosageItem(id, date, desc, mixer, note, days) {
		
		var dosage_container_div = document.getElementById('dosageContainer');
		
		var dosage_item_div = document.createElement('div');
		dosage_item_div.setAttribute('class', 'organization');
		dosage_container_div.appendChild(dosage_item_div);
		
		var dosage_table = document.createElement('table');
		dosage_table.setAttribute('cellspacing', '0');
		dosage_table.setAttribute('cellpadding', '0');
		dosage_table.setAttribute('border', '0');
		dosage_table.setAttribute('summary', '');
		dosage_item_div.appendChild(dosage_table);
		
		var dosage_tr_1 = document.createElement('tr');
		dosage_table.appendChild(dosage_tr_1);
		
		var dosage_td_1_1 = document.createElement('td');
		dosage_td_1_1.setAttribute('style', 'width:   3px; ');
		dosage_tr_1.appendChild(dosage_td_1_1);
		
		var dosage_td_1_2 = document.createElement('td');
		dosage_td_1_2.setAttribute('style', 'width:  78px; text-align: center; font-weight: bolder;  ');
		var labelNode_1_2 = document.createTextNode(date);
		dosage_td_1_2.appendChild(labelNode_1_2);
		dosage_tr_1.appendChild(dosage_td_1_2);
		
		var dosage_td_1_3 = document.createElement('td');
		dosage_td_1_3.setAttribute('style', 'width: 241px; text-align: left; font-weight: bolder;  ');
		var anchor_1_3 = document.createElement('a');
		anchor_1_3.setAttribute('href', 'herb-dosage.jsp?id=' + id);
		var labelNode_1_3 = document.createTextNode(desc);
		anchor_1_3.appendChild(labelNode_1_3);
		dosage_td_1_3.appendChild(anchor_1_3);
		dosage_tr_1.appendChild(dosage_td_1_3);
		
		var dosage_td_1_4 = document.createElement('td');
		dosage_td_1_4.setAttribute('style', 'width: 178px; text-align: center; font-weight: bolder;  ');
		var labelNode_1_4 = document.createTextNode(mixer);
		dosage_td_1_4.appendChild(labelNode_1_4);
		dosage_tr_1.appendChild(dosage_td_1_4);
		
		var dosage_td_1_5 = document.createElement('td');
		dosage_td_1_5.setAttribute('style', 'width:  49px; text-align: center; font-weight: bolder;  ');
		var labelNode_1_5 = document.createTextNode(days);
		dosage_td_1_5.appendChild(labelNode_1_5);
		dosage_tr_1.appendChild(dosage_td_1_5);
		
		var dosage_td_1_6 = document.createElement('td');
		dosage_td_1_6.setAttribute('style', 'width:  14px; text-align: center;  ');
		var image_1_6 = document.createElement('img');
		image_1_6.setAttribute('src', '../images/delete_icon.gif');
		image_1_6.setAttribute('title', 'Delete');
		image_1_6.setAttribute('onclick', "if (confirm('Delete?')) {processCommand('deleteHerbDosage'," + id + ")}");
		dosage_td_1_6.appendChild(image_1_6);
		dosage_tr_1.appendChild(dosage_td_1_6);
		
		var dosage_td_1_7 = document.createElement('td');
		dosage_td_1_7.setAttribute('style', 'width:   3px; ');
		dosage_tr_1.appendChild(dosage_td_1_7);
		
		
		
		var dosage_tr_2 = document.createElement('tr');
		dosage_table.appendChild(dosage_tr_2);
		
		var dosage_td_2_1 = document.createElement('td');
		dosage_td_2_1.setAttribute('style', 'width:   3px; ');
		dosage_tr_2.appendChild(dosage_td_2_1);
		
		var dosage_td_2_2 = document.createElement('td');
		dosage_td_2_2.setAttribute('style', 'width: 560px; text-align: left; ');
		dosage_td_2_2.setAttribute('colspan', '5');
		
		//var labelNode_2_2 = document.createTextNode('stuff<br />more stuff');
		
		var lastPipeIndex = 0;
		var pipeIndex = note.indexOf('|');
		if (pipeIndex > -1) {
			while (pipeIndex > -1) {
				var labelNode_2_2 = document.createTextNode(note.substring(lastPipeIndex, pipeIndex));
				dosage_td_2_2.appendChild(labelNode_2_2);
				var br_td_2_2 = document.createElement('br');
				dosage_td_2_2.appendChild(br_td_2_2);
				lastPipeIndex = pipeIndex + 1;
				pipeIndex = note.indexOf('|', lastPipeIndex);
			}
			var labelNode_2_2x = document.createTextNode(note.substring(lastPipeIndex));
			dosage_td_2_2.appendChild(labelNode_2_2x);
		} else {
			var labelNode_2_2 = document.createTextNode(note);
			dosage_td_2_2.appendChild(labelNode_2_2);
		}
		
		
		dosage_tr_2.appendChild(dosage_td_2_2);
		
		var dosage_td_2_3 = document.createElement('td');
		dosage_td_2_3.setAttribute('style', 'width:   3px; ');
		dosage_tr_2.appendChild(dosage_td_2_3);
		
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
	
	function removeDosageContainer() {
		var parent = document.getElementById('dosageParent');
		if (parent) {
			var child = document.getElementById('dosageContainer');
			if (child)
				parent.removeChild(child);
			child = document.getElementById('dosageBottomLine');
			if (child)
				parent.removeChild(child);
		}
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
		//alert('showReport stuff');
		processCommand('purchaseOrderReport', 1);
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
		
		//alert('arg1 >' + arg1);
		
		processCommand('addDirections',arg1,arg2,arg3,arg4,arg5,arg6);
	}

	function save()
	{
		var arg1 = document.forms[0].mixDate.value;
		var arg2 = document.forms[0].mixDesc.value;
		var arg3 = document.forms[0].notes.value;
		
		processCommand('saveHerbDosage',arg1,arg2,arg3);
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
<body class="yui-skin-sam" onload="javascript:initErrors();<%= (search_str.equals("") && vendor_search_str.equals("")) ? "" : "processCommand('getCheckoutCodesByDescHerbs','" + search_str + "');" %>">

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
	    <p class="headline"><%= adminCompany.getLabel() %> - Clients - <span id="selectedPerson"><%= adminPerson.isNew() ? "No Client Selected" : adminPerson.getLabel() %></span> - Herb Mixtures</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-client-menu.jsp" %>

					<div class="main">

						<p class="headlineA">User Profile&nbsp;&#47;&nbsp;Hair Analysis</p>
						<p class="currentObjectName"><%= adminPerson.getLabel() %></p>
						<p>Use this screen to manage hair analysis data.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/userMineralRatios" focus="dateInput">

							<input id="dummy" name="dummy" type="hidden" />
							
							<!--
							<div class="adminItem">
								<div class="leftTM">OXIDATION TYPE</div>
							</div>
							-->

							<div class="adminItem">
								<div class="leftTM">REPORT DATE</div>
								<div class="right">
									<input name="dateInput" onfocus="getDate('userMineralRatiosForm','dateInput');select();" value="<%= document.getReportDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">INITIAL RECOMMENDATIONS (Prior to Phases I-IV)</div>
								<div class="right">
									<textarea name="initial_recommendations" class="textarea"  style="width: 309px;" rows="3" cols="44"><%= document.getInitialRecommendationsString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">SUPPLEMENTS&nbsp;&#47;&nbsp;HERBAL MIXTURES</div>
								<div class="right">
									<textarea name="supplements_herbal_mixtures" class="textarea"  style="width: 309px;" rows="3" cols="44"><%= document.getSupplementsHerbalMixturesString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DETOX&nbsp;&#47;&nbsp;EXERCISE&nbsp;&#47;&nbsp;<br />DIET&nbsp;&#47;&nbsp;STRESS MANAGEMENT</div>
								<div class="right">
									<textarea name="detox_exercise_diet_stress_management" class="textarea"  style="width: 309px;" rows="3" cols="44"><%= document.getDetoxExerciseDietStressManagementString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">NEXT APPOINTMENT</div>
								<div class="right">
									<textarea name="next_appointment" class="textarea"  style="width: 309px;" rows="3" cols="44"><%= document.getNextAppointmentString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">LOAD FROM TEMPLATE</div>
								<div class="right">
									<select name="template" class="select" style="width: 309px;" onchange="javascript:document.forms[1].submit();">
										<option value="0">-- SELECT A TEMPLATE --</option>
<%
try
{
    Iterator document_itr = MineralRatiosClientDocumentBean.getMineralRatiosClientDocumentTemplates().iterator();
    if (document_itr.hasNext())
    {
	while (document_itr.hasNext())
	{
	    MineralRatiosClientDocumentBean document_obj = (MineralRatiosClientDocumentBean)document_itr.next();
%>
										<option value="<%= document_obj.getValue() %>"><%= document_obj.getTemplateNameString() %></option>
<%
	}
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">PHASE I DATE</div>
								<div class="right">
									<input name="dateInputPhaseI" onfocus="getDate('userMineralRatiosForm','dateInputPhaseI');select();" value="<%= document.getPhaseIDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">
								    <table>
									<tr>
									<td>
									<div >Supplements</div>
									</td>
									<td>
									<div >Detox</div>
									</td>
									<td>
									<div >Exercise</div>
									</td>
									<td>
									<div >Diet</div>
									</td>
									<td>
									<div >Stress</div>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_header_phaseI" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("supplements_header_phaseI").equals("") ? " selected" : "" %>></option>
										<option value="Digestive Support"<%= document.get("supplements_header_phaseI").equals("Digestive Support") ? " selected" : "" %>>Digestive Support</option>
										<option value="Adrenal/Thyroid Support"<%= document.get("supplements_header_phaseI").equals("Adrenal/Thyroid Support") ? " selected" : "" %>>Adrenal/Thyroid Support</option>
										<option value="Thyroid/Adrenal Support"<%= document.get("supplements_header_phaseI").equals("Thyroid/Adrenal Support") ? " selected" : "" %>>Thyroid/Adrenal Support</option>
										<option value="Adrenal Support"<%= document.get("supplements_header_phaseI").equals("Adrenal Support") ? " selected" : "" %>>Adrenal Support</option>
										<option value="Thyroid Support"<%= document.get("supplements_header_phaseI").equals("Thyroid Support") ? " selected" : "" %>>Thyroid Support</option>
										<option value="Heavy Metal Detox"<%= document.get("supplements_header_phaseI").equals("Heavy Metal Detox") ? " selected" : "" %>>Heavy Metal Detox</option>
										<option value="Copper Toxicity"<%= document.get("supplements_header_phaseI").equals("Copper Toxicity") ? " selected" : "" %>>Copper Toxicity</option>
										<option value="Copper Detox/Heavy Metals"<%= document.get("supplements_header_phaseI").equals("Copper Detox/Heavy Metals") ? " selected" : "" %>>Copper Detox/Heavy Metals</option>
										<option value="Maintenance Supplementation"<%= document.get("supplements_header_phaseI").equals("Maintenance Supplementation") ? " selected" : "" %>>Maintenance Supplementation</option>
									</select>
									</td>
									<td>
									<select name="detox_header_phaseI" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("detox_header_phaseI").equals("") ? " selected" : "" %>></option>
										<option value="Liver/GB Flush"<%= document.get("detox_header_phaseI").equals("Liver/GB Flush") ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_header_phaseI").equals("SP Cleanse") ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_header_phaseI").equals("Colon Cleanse") ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_header_phaseI").equals("Juice Fast") ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_header_phaseI").equals("Blood Cleanse") ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_header_phaseI").equals("Homeopathic") ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_header_phaseI" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("exercise_header_phaseI").equals("") ? " selected" : "" %>></option>
										<option value="Walking Daily 30 minutes"<%= document.get("exercise_header_phaseI").equals("Walking Daily 30 minutes") ? " selected" : "" %>>Walking Daily 30 minutes</option>
										<option value="Walking Daily 40-60 minutes"<%= document.get("exercise_header_phaseI").equals("Walking Daily 40-60 minutes") ? " selected" : "" %>>Walking Daily 40-60 minutes</option>
										<option value="Walking & Strength Training"<%= document.get("exercise_header_phaseI").equals("Walking & Strength Training") ? " selected" : "" %>>Walking & Strength Training</option>
										<option value="Cardio 30-60 minutes"<%= document.get("exercise_header_phaseI").equals("Cardio 30-60 minutes") ? " selected" : "" %>>Cardio 30-60 minutes</option>
										<option value="Stretching"<%= document.get("exercise_header_phaseI").equals("Stretching") ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_header_phaseI" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("diet_header_phaseI").equals("") ? " selected" : "" %>></option>
										<option value="Nutrition Plan"<%= document.get("diet_header_phaseI").equals("Nutrition Plan") ? " selected" : "" %>>Nutrition Plan</option>
									</select>
									</td>
									<td>
									<select name="stress_header_phaseI" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("stress_header_phaseI").equals("") ? " selected" : "" %>></option>
										<option value="Emotional"<%= document.get("stress_header_phaseI").equals("Emotional") ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_header_phaseI").equals("Mental") ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_header_phaseI").equals("Physical") ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_header_phaseI").equals("Spiritual") ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_header_phaseI").equals("Toxins") ? " selected" : "" %>>Toxins</option>
										<option value="Toxin (Mercury)"<%= document.get("stress_header_phaseI").equals("Toxin (Mercury)") ? " selected" : "" %>>Toxin (Mercury)</option>
										<option value="Toxin (Copper)"<%= document.get("stress_header_phaseI").equals("Toxin (Copper)") ? " selected" : "" %>>Toxin (Copper)</option>
										<option value="Toxin (Cadmium)"<%= document.get("stress_header_phaseI").equals("Toxin (Cadmium)") ? " selected" : "" %>>Toxin (Cadmium)</option>
										<option value="Toxin (Aluminium)"<%= document.get("stress_header_phaseI").equals("Toxin (Aluminium)") ? " selected" : "" %>>Toxin (Aluminium)</option>
										<option value="Toxin (Nickel)"<%= document.get("stress_header_phaseI").equals("Toxin (Nickel)") ? " selected" : "" %>>Toxin (Nickel)</option>
										<option value="Toxin (Cobalt)"<%= document.get("stress_header_phaseI").equals("Toxin (Cobalt)") ? " selected" : "" %>>Toxin (Cobalt)</option>
										<option value="Toxin (Manganese)"<%= document.get("stress_header_phaseI").equals("Toxin (Manganese)") ? " selected" : "" %>>Toxin (Manganese)</option>
										<option value="Toxin (Lead)"<%= document.get("stress_header_phaseI").equals("Toxin (Lead)") ? " selected" : "" %>>Toxin (Lead)</option>
										<option value="Biochemical"<%= document.get("stress_header_phaseI").equals("Biochemical") ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_header_phaseI").equals("Energetic (EMF)") ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_phaseI" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Multizyme"<%= document.get("supplements_phaseI").indexOf("Multizyme|") > -1 ? " selected" : "" %>>Multizyme</option>
										<option value="Sp Bk Radish"<%= document.get("supplements_phaseI").indexOf("Sp Bk Radish|") > -1 ? " selected" : "" %>>Sp Bk Radish</option>
										<option value="Zypan"<%= document.get("supplements_phaseI").indexOf("Zypan|") > -1 ? " selected" : "" %>>Zypan</option>
										<option value="Pancreatrophin PMG"<%= document.get("supplements_phaseI").indexOf("Pancreatrophin PMG|") > -1 ? " selected" : "" %>>Pancreatrophin PMG</option>
										<option value="Fungal Forte"<%= document.get("supplements_phaseI").indexOf("Fungal Forte|") > -1 ? " selected" : "" %>>Fungal Forte</option>
										<option value="Multi-Probiotic"<%= document.get("supplements_phaseI").indexOf("Multi-Probiotic|") > -1 ? " selected" : "" %>>Multi-Probiotic</option>
										<option value="Cholacol"<%= document.get("supplements_phaseI").indexOf("Cholacol|") > -1 ? " selected" : "" %>>Cholacol</option>
										<option value="Theralac"<%= document.get("supplements_phaseI").indexOf("Theralac|") > -1 ? " selected" : "" %>>Theralac</option>
										<option value="Lactic Acid Yeast waf"<%= document.get("supplements_phaseI").indexOf("Lactic Acid Yeast waf|") > -1 ? " selected" : "" %>>Lactic Acid Yeast waf</option>
										<option value="DiGest (liq)"<%= document.get("supplements_phaseI").indexOf("DiGest (liq)|") > -1 ? " selected" : "" %>>DiGest (liq)</option>
										<option value="DiGest (caps)"<%= document.get("supplements_phaseI").indexOf("DiGest (caps)|") > -1 ? " selected" : "" %>>DiGest (caps)</option>
										<option value="Zymex"<%= document.get("supplements_phaseI").indexOf("Zymex|") > -1 ? " selected" : "" %>>Zymex</option>
										<option value="Zymex waf"<%= document.get("supplements_phaseI").indexOf("Zymex waf|") > -1 ? " selected" : "" %>>Zymex waf</option>
										<option value="Lact-Enz"<%= document.get("supplements_phaseI").indexOf("Lact-Enz|") > -1 ? " selected" : "" %>>Lact-Enz</option>
										<option value="A-F Betafood"<%= document.get("supplements_phaseI").indexOf("A-F Betafood|") > -1 ? " selected" : "" %>>A-F Betafood</option>
										<option value="LivCo"<%= document.get("supplements_phaseI").indexOf("LivCo|") > -1 ? " selected" : "" %>>LivCo</option>
										<option value="DigestPlex (GF)"<%= document.get("supplements_phaseI").indexOf("DigestPlex (GF|") > -1 ? " selected" : "" %>>DigestPlex (GF)</option>
										<option value="Protein Powder"<%= document.get("supplements_phaseI").indexOf("Protein Powder|") > -1 ? " selected" : "" %>>Protein Powder</option>
										<option value="Greens"<%= document.get("supplements_phaseI").indexOf("Greens|") > -1 ? " selected" : "" %>>Greens</option>
										<option value="Cataplex GTF"<%= document.get("supplements_phaseI").indexOf("Cataplex GTF|") > -1 ? " selected" : "" %>>Cataplex GTF</option>
										<option value="Gymnema"<%= document.get("supplements_phaseI").indexOf("Gymnema|") > -1 ? " selected" : "" %>>Gymnema</option>
										<option value="Paraplex"<%= document.get("supplements_phaseI").indexOf("Paraplex|") > -1 ? " selected" : "" %>>Paraplex</option>
										<option value="Inositol"<%= document.get("supplements_phaseI").indexOf("Inositol|") > -1 ? " selected" : "" %>>Inositol</option>
										<option value="Detox Formula (GF)"<%= document.get("supplements_phaseI").indexOf("Detox Formula (GF)|") > -1 ? " selected" : "" %>>Detox Formula (GF)</option>
										<option value="PhytoGreens (GF)"<%= document.get("supplements_phaseI").indexOf("PhytoGreens (GF)|") > -1 ? " selected" : "" %>>PhytoGreens (GF)</option>
										<option value="VitaLiv"<%= document.get("supplements_phaseI").indexOf("VitaLiv|") > -1 ? " selected" : "" %>>VitaLiv</option>
										<option value="BFood"<%= document.get("supplements_phaseI").indexOf("BFood|") > -1 ? " selected" : "" %>>BFood</option>
										<option value="Primal Defense"<%= document.get("supplements_phaseI").indexOf("Primal Defense|") > -1 ? " selected" : "" %>>Primal Defense</option>
										<option value="Chewable Catalyn"<%= document.get("supplements_phaseI").indexOf("Chewable Catalyn|") > -1 ? " selected" : "" %>>Chewable Catalyn</option>
										<option value="Ferrofood"<%= document.get("supplements_phaseI").indexOf("Ferrofood|") > -1 ? " selected" : "" %>>Ferrofood</option>
										<option value="Catalyn"<%= document.get("supplements_phaseI").indexOf("Catalyn|") > -1 ? " selected" : "" %>>Catalyn</option>
										<option value="Boswellia Complex"<%= document.get("supplements_phaseI").indexOf("Boswellia Complex|") > -1 ? " selected" : "" %>>Boswellia Complex</option>
										<option value="Cranberry Complex"<%= document.get("supplements_phaseI").indexOf("Cranberry Complex|") > -1 ? " selected" : "" %>>Cranberry Complex</option>
										<option value="Rehmannia Complex"<%= document.get("supplements_phaseI").indexOf("Rehmannia Complex|") > -1 ? " selected" : "" %>>Rehmannia Complex</option>
										<option value="Withania Complex"<%= document.get("supplements_phaseI").indexOf("Withania Complex|") > -1 ? " selected" : "" %>>Withania Complex</option>
										<option value="Thyroid Complex"<%= document.get("supplements_phaseI").indexOf("Thyroid Complex|") > -1 ? " selected" : "" %>>Thyroid Complex</option>
										<option value="Congaplex"<%= document.get("supplements_phaseI").indexOf("Congaplex|") > -1 ? " selected" : "" %>>Congaplex</option>
										<option value="Cardiotrophin PMG"<%= document.get("supplements_phaseI").indexOf("Cardiotrophin PMG|") > -1 ? " selected" : "" %>>Cardiotrophin PMG</option>
										<option value="Drenatrophin PMG"<%= document.get("supplements_phaseI").indexOf("Drenatrophin PMG|") > -1 ? " selected" : "" %>>Drenatrophin PMG</option>
										<option value="Hypothalamus PMG"<%= document.get("supplements_phaseI").indexOf("Hypothalamus PMG|") > -1 ? " selected" : "" %>>Hypothalamus PMG</option>
										<option value="Mammary PMG"<%= document.get("supplements_phaseI").indexOf("Mammary PMG|") > -1 ? " selected" : "" %>>Mammary PMG</option>
										<option value="Myotrophin PMG"<%= document.get("supplements_phaseI").indexOf("Myotrophin PMG|") > -1 ? " selected" : "" %>>Myotrophin PMG</option>
										<option value="Neurotrophin PMG"<%= document.get("supplements_phaseI").indexOf("Neurotrophin PM|") > -1 ? " selected" : "" %>>Neurotrophin PMG</option>
										<option value="Ostrophin PMG"<%= document.get("supplements_phaseI").indexOf("Ostrophin PMG|") > -1 ? " selected" : "" %>>Ostrophin PMG</option>
										<option value="Ovatrophin PMG"<%= document.get("supplements_phaseI").indexOf("Ovatrophin PMG|") > -1 ? " selected" : "" %>>Ovatrophin PMG</option>
										<option value="Pneumotrophin PMG"<%= document.get("supplements_phaseI").indexOf("Pneumotrophin PMG|") > -1 ? " selected" : "" %>>Pneumotrophin PMG</option>
										<option value="Pituitrophin PMG"<%= document.get("supplements_phaseI").indexOf("Pituitrophin PMG|") > -1 ? " selected" : "" %>>Pituitrophin PMG</option>
										<option value="Prostate PMG"<%= document.get("supplements_phaseI").indexOf("Prostate PMG|") > -1 ? " selected" : "" %>>Prostate PMG</option>
										<option value="Renatrophin PMG"<%= document.get("supplements_phaseI").indexOf("Renatrophin PMG|") > -1 ? " selected" : "" %>>Renatrophin PMG</option>
										<option value="Thymus PMG"<%= document.get("supplements_phaseI").indexOf("Thymus PMG|") > -1 ? " selected" : "" %>>Thymus PMG</option>
										<option value="Thytrophin PMG"<%= document.get("supplements_phaseI").indexOf("Thytrophin PM|") > -1 ? " selected" : "" %>>Thytrophin PMG</option>
										<option value="Utrophin PMG"<%= document.get("supplements_phaseI").indexOf("Utrophin PM|") > -1 ? " selected" : "" %>>Utrophin PMG</option>
										<option value="A-C Carbamide"<%= document.get("supplements_phaseI").indexOf("A-C Carbamide|") > -1 ? " selected" : "" %>>A-C Carbamide</option>
										<option value="FlavoC"<%= document.get("supplements_phaseI").indexOf("FlavoC|") > -1 ? " selected" : "" %>>FlavoC</option>
										<option value="Cat A"<%= document.get("supplements_phaseI").indexOf("Cat A|") > -1 ? " selected" : "" %>>Cat A</option>
										<option value="Cat A-C"<%= document.get("supplements_phaseI").indexOf("Cat A-C|") > -1 ? " selected" : "" %>>Cat A-C</option>
										<option value="Cat A-C-P"<%= document.get("supplements_phaseI").indexOf("Cat A-C-P|") > -1 ? " selected" : "" %>>Cat A-C-P</option>
										<option value="Cat B"<%= document.get("supplements_phaseI").indexOf("Cat B|") > -1 ? " selected" : "" %>>Cat B</option>
										<option value="Cat C"<%= document.get("supplements_phaseI").indexOf("Cat C|") > -1 ? " selected" : "" %>>Cat C</option>
										<option value="Cat D"<%= document.get("supplements_phaseI").indexOf("Cat D|") > -1 ? " selected" : "" %>>Cat D</option>
										<option value="Cat E"<%= document.get("supplements_phaseI").indexOf("Cat E|") > -1 ? " selected" : "" %>>Cat E</option>
										<option value="Cataplex F"<%= document.get("supplements_phaseI").indexOf("Cataplex F|") > -1 ? " selected" : "" %>>Cat F</option>
										<option value="Cataplex G"<%= document.get("supplements_phaseI").indexOf("Cataplex G|") > -1 ? " selected" : "" %>>Cataplex G</option>
										<option value="CalMag"<%= document.get("supplements_phaseI").indexOf("CalMag|") > -1 ? " selected" : "" %>>CalMag</option>
										<option value="Cal Lact Powder"<%= document.get("supplements_phaseI").indexOf("Cal Lact Powder|") > -1 ? " selected" : "" %>>Cal Lact Powder</option>
										<option value="Magnesium Lactate"<%= document.get("supplements_phaseI").indexOf("Magnesium Lactate|") > -1 ? " selected" : "" %>>Magnesium Lactate</option>
										<option value="Manganese B12"<%= document.get("supplements_phaseI").indexOf("Manganese B12|") > -1 ? " selected" : "" %>>Manganese B12</option>
										<option value="Liquid Herbs"<%= document.get("supplements_phaseI").indexOf("Liquid Herbs|") > -1 ? " selected" : "" %>>Liquid Herbs</option>
										<option value="CardioPlus"<%= document.get("supplements_phaseI").indexOf("CardioPlus|") > -1 ? " selected" : "" %>>CardioPlus</option>
										<option value="Adrenal Des"<%= document.get("supplements_phaseI").indexOf("Adrenal Des|") > -1 ? " selected" : "" %>>Adrenal Des</option>
										<option value="Spleen Des"<%= document.get("supplements_phaseI").indexOf("Spleen Des|") > -1 ? " selected" : "" %>>Spleen Des</option>
										<option value="Drenamin"<%= document.get("supplements_phaseI").indexOf("Drenamin|") > -1 ? " selected" : "" %>>Drenamin</option>
										<option value="Fish Oil"<%= document.get("supplements_phaseI").indexOf("Fish Oil|") > -1 ? " selected" : "" %>>Fish Oil</option>
										<option value="OBM"<%= document.get("supplements_phaseI").indexOf("OBM|") > -1 ? " selected" : "" %>>OBM</option>
										<option value="Prolamine Iodine"<%= document.get("supplements_phaseI").indexOf("Prolamine Iodine|") > -1 ? " selected" : "" %>>Prolamine Iodine</option>
										<option value="Parotid"<%= document.get("supplements_phaseI").indexOf("Parotid|") > -1 ? " selected" : "" %>>Parotid</option>
										<option value="Cholacol II"<%= document.get("supplements_phaseI").indexOf("Cholacol II|") > -1 ? " selected" : "" %>>Cholacol II</option>
										<option value="Cholaplex"<%= document.get("supplements_phaseI").indexOf("Cholaplex|") > -1 ? " selected" : "" %>>Cholaplex</option>
										<option value="Zinc Liver Chelate"<%= document.get("supplements_phaseI").indexOf("Zinc Liver Chelate|") > -1 ? " selected" : "" %>>Zinc Liver Chelate</option>
										<option value="Protefood"<%= document.get("supplements_phaseI").indexOf("Protefood|") > -1 ? " selected" : "" %>>Protefood</option>
										<option value="MolyCu"<%= document.get("supplements_phaseI").indexOf("MolyCu|") > -1 ? " selected" : "" %>>MolyCu</option>
										<option value="GB3"<%= document.get("supplements_phaseI").indexOf("GB3|") > -1 ? " selected" : "" %>>GB3</option>
										<option value="EndoPan"<%= document.get("supplements_phaseI").indexOf("EndoPan|") > -1 ? " selected" : "" %>>EndoPan</option>
										<option value="Flaxseed oil"<%= document.get("supplements_phaseI").indexOf("Flaxseed oil|") > -1 ? " selected" : "" %>>Flaxseed oil</option>
										<option value="Arginex"<%= document.get("supplements_phaseI").indexOf("Arginex|") > -1 ? " selected" : "" %>>Arginex</option>
										<option value="Rehmannia (liq)"<%= document.get("supplements_phaseI").indexOf("Rehmannia (liq)|") > -1 ? " selected" : "" %>>Rehmannia (liq)</option>
										<option value="Saligesic"<%= document.get("supplements_phaseI").indexOf("Saligesic|") > -1 ? " selected" : "" %>>Saligesic</option>
										<option value="Vitanox"<%= document.get("supplements_phaseI").indexOf("Vitanox|") > -1 ? " selected" : "" %>>Vitanox</option>
										<option value="St. John's Wort"<%= document.get("supplements_phaseI").indexOf("St. John's Wort|") > -1 ? " selected" : "" %>>St. John's Wort</option>
										<option value="California Poppy"<%= document.get("supplements_phaseI").indexOf("California Poppy|") > -1 ? " selected" : "" %>>California Poppy</option>
										<option value="Broncafect"<%= document.get("supplements_phaseI").indexOf("Broncafect|") > -1 ? " selected" : "" %>>Broncafect</option>
										<option value="Min-Tran"<%= document.get("supplements_phaseI").indexOf("Min-Tran|") > -1 ? " selected" : "" %>>Min-Tran</option>
										<option value="Min-Chex"<%= document.get("supplements_phaseI").indexOf("Min-Chex|") > -1 ? " selected" : "" %>>Min-Chex</option>
										<option value="Progon B"<%= document.get("supplements_phaseI").indexOf("Progon B|") > -1 ? " selected" : "" %>>Progon B</option>
										<option value="Tribulus"<%= document.get("supplements_phaseI").indexOf("Tribulus|") > -1 ? " selected" : "" %>>Tribulus</option>
										<option value="Wheat Germ oil"<%= document.get("supplements_phaseI").indexOf("Wheat Germ oil|") > -1 ? " selected" : "" %>>Wheat Germ oil</option>
										<option value="Symplex F"<%= document.get("supplements_phaseI").indexOf("Symplex F|") > -1 ? " selected" : "" %>>Symplex F</option>
										<option value="Glucosamine Synergy"<%= document.get("supplements_phaseI").indexOf("Glucosamine Synergy|") > -1 ? " selected" : "" %>>Glucosamine Synergy</option>
										<option value="Ligaplex II"<%= document.get("supplements_phaseI").indexOf("Ligaplex II|") > -1 ? " selected" : "" %>>Ligaplex II</option>
										<option value="Collagen C"<%= document.get("supplements_phaseI").indexOf("Collagen C|") > -1 ? " selected" : "" %>>Collagen C</option>
										<option value="Symplex M"<%= document.get("supplements_phaseI").indexOf("Symplex M|") > -1 ? " selected" : "" %>>Symplex M</option>
										<option value="Male Support"<%= document.get("supplements_phaseI").indexOf("Male Support|") > -1 ? " selected" : "" %>>Male Support</option>
										<option value="Orchex"<%= document.get("supplements_phaseI").indexOf("Orchex|") > -1 ? " selected" : "" %>>Orchex</option>
										<option value="Lipoic Acid"<%= document.get("supplements_phaseI").indexOf("Lipoic Acid|") > -1 ? " selected" : "" %>>Lipoic Acid</option>
										<option value="Ambrotose"<%= document.get("supplements_phaseI").indexOf("Ambrotose|") > -1 ? " selected" : "" %>>Ambrotose</option>
									</select>
									</td>
									<td>
									<select name="detox_phaseI" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Liver/GB Flush"<%= document.get("detox_phaseI").indexOf("Liver/GB Flush|") > -1 ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_phaseI").indexOf("SP Cleanse|") > -1 ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_phaseI").indexOf("Colon Cleanse|") > -1 ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_phaseI").indexOf("Juice Fast|") > -1 ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_phaseI").indexOf("Blood Cleanse|") > -1 ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_phaseI").indexOf("Homeopathic|") > -1 ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_phaseI" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Walk Daily 30 min"<%= document.get("exercise_phaseI").indexOf("Walk Daily 30 min|") > -1 ? " selected" : "" %>>Walk Daily 30 min</option>
										<option value="Walk Daily 40-60 min"<%= document.get("exercise_phaseI").indexOf("Walk Daily 40-60 min|") > -1 ? " selected" : "" %>>Walk Daily 40-60 min</option>
										<option value="Walk/Strength Train"<%= document.get("exercise_phaseI").indexOf("Walk/Strength Train|") > -1 ? " selected" : "" %>>Walk/Strength Train</option>
										<option value="Cardio 30-60 min"<%= document.get("exercise_phaseI").indexOf("Cardio 30-60 min|") > -1 ? " selected" : "" %>>Cardio 30-60 min</option>
										<option value="Stretching"<%= document.get("exercise_phaseI").indexOf("Stretching|") > -1 ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_phaseI" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="No sugar/fruit"<%= document.get("diet_phaseI").indexOf("No sugar/fruit|") > -1 ? " selected" : "" %>>No sugar/fruit</option>
										<option value="Protein Shakes"<%= document.get("diet_phaseI").indexOf("Protein Shakes|") > -1 ? " selected" : "" %>>Protein Shakes</option>
										<option value="Protein Shakes w/greens"<%= document.get("diet_phaseI").indexOf("Protein Shakes w/greens|") > -1 ? " selected" : "" %>>Protein Shakes w/greens</option>
										<option value="Protein Shakes w/flax oil"<%= document.get("diet_phaseI").indexOf("Protein Shakes w/flax oil|") > -1 ? " selected" : "" %>>Protein Shakes w/flax oil</option>
										<option value="Evening Primrose oil"<%= document.get("diet_phaseI").indexOf("Evening Primrose oil|") > -1 ? " selected" : "" %>>Evening Primrose oil</option>
										<option value="Flaxseed oil"<%= document.get("diet_phaseI").indexOf("Flaxseed oil|") > -1 ? " selected" : "" %>>Flaxseed oil</option>
										<option value="Garlic"<%= document.get("diet_phaseI").indexOf("Garlic|") > -1 ? " selected" : "" %>>Garlic</option>
										<option value="Water Testing"<%= document.get("diet_phaseI").indexOf("Water Testing|") > -1 ? " selected" : "" %>>Water Testing</option>
									</select>
									</td>
									<td>
									<select name="stress_phaseI" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Emotional"<%= document.get("stress_phaseI").indexOf("Emotional|") > -1 ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_phaseI").indexOf("Mental|") > -1 ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_phaseI").indexOf("Physical|") > -1 ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_phaseI").indexOf("Spiritual|") > -1 ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_phaseI").indexOf("Toxins|") > -1 ? " selected" : "" %>>Toxins</option>
										<option value="Lead"<%= document.get("stress_phaseI").indexOf("Lead|") > -1 ? " selected" : "" %>>Lead</option>
										<option value="Mercury"<%= document.get("stress_phaseI").indexOf("Mercury|") > -1 ? " selected" : "" %>>Mercury</option>
										<option value="Cadmium"<%= document.get("stress_phaseI").indexOf("Cadmium|") > -1 ? " selected" : "" %>>Cadmium</option>
										<option value="Arsenic"<%= document.get("stress_phaseI").indexOf("Arsenic|") > -1 ? " selected" : "" %>>Arsenic</option>
										<option value="Aluminium"<%= document.get("stress_phaseI").indexOf("Aluminium|") > -1 ? " selected" : "" %>>Aluminium</option>
										<option value="Copper"<%= document.get("stress_phaseI").indexOf("Copper|") > -1 ? " selected" : "" %>>Copper</option>
										<option value="Nickel"<%= document.get("stress_phaseI").indexOf("Nickel|") > -1 ? " selected" : "" %>>Nickel</option>
										<option value="Zinc"<%= document.get("stress_phaseI").indexOf("Zinc|") > -1 ? " selected" : "" %>>Zinc</option>
										<option value="Cobalt"<%= document.get("stress_phaseI").indexOf("Cobalt|") > -1 ? " selected" : "" %>>Cobalt</option>
										<option value="Manganese"<%= document.get("stress_phaseI").indexOf("Manganese|") > -1 ? " selected" : "" %>>Manganese</option>
										<option value="Biochemical"<%= document.get("stress_phaseI").indexOf("Biochemical|") > -1 ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_phaseI").indexOf("Energetic (EMF)|") > -1 ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
								    </table>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">PHASE II DATE</div>
								<div class="right">
									<input name="dateInputPhaseII" onfocus="getDate('userMineralRatiosForm','dateInputPhaseII');select();" value="<%= document.getPhaseIIDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">
								    <table>
									<tr>
									<td>
									<div >Supplements</div>
									</td>
									<td>
									<div >Detox</div>
									</td>
									<td>
									<div >Exercise</div>
									</td>
									<td>
									<div >Diet</div>
									</td>
									<td>
									<div >Stress</div>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_header_phaseII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("supplements_header_phaseII").equals("") ? " selected" : "" %>></option>
										<option value="Digestive Support"<%= document.get("supplements_header_phaseII").equals("Digestive Support") ? " selected" : "" %>>Digestive Support</option>
										<option value="Adrenal/Thyroid Support"<%= document.get("supplements_header_phaseII").equals("Adrenal/Thyroid Support") ? " selected" : "" %>>Adrenal/Thyroid Support</option>
										<option value="Thyroid/Adrenal Support"<%= document.get("supplements_header_phaseII").equals("Thyroid/Adrenal Support") ? " selected" : "" %>>Thyroid/Adrenal Support</option>
										<option value="Adrenal Support"<%= document.get("supplements_header_phaseII").equals("Adrenal Support") ? " selected" : "" %>>Adrenal Support</option>
										<option value="Thyroid Support"<%= document.get("supplements_header_phaseII").equals("Thyroid Support") ? " selected" : "" %>>Thyroid Support</option>
										<option value="Heavy Metal Detox"<%= document.get("supplements_header_phaseII").equals("Heavy Metal Detox") ? " selected" : "" %>>Heavy Metal Detox</option>
										<option value="Copper Toxicity"<%= document.get("supplements_header_phaseII").equals("Copper Toxicity") ? " selected" : "" %>>Copper Toxicity</option>
										<option value="Copper Detox/Heavy Metals"<%= document.get("supplements_header_phaseII").equals("Copper Detox/Heavy Metals") ? " selected" : "" %>>Copper Detox/Heavy Metals</option>
										<option value="Maintenance Supplementation"<%= document.get("supplements_header_phaseII").equals("Maintenance Supplementation") ? " selected" : "" %>>Maintenance Supplementation</option>
									</select>
									</td>
									<td>
									<select name="detox_header_phaseII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("detox_header_phaseII").equals("") ? " selected" : "" %>></option>
										<option value="Liver/GB Flush"<%= document.get("detox_header_phaseII").equals("Liver/GB Flush") ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_header_phaseII").equals("SP Cleanse") ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_header_phaseII").equals("Colon Cleanse") ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_header_phaseII").equals("Juice Fast") ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_header_phaseII").equals("Blood Cleanse") ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_header_phaseII").equals("Homeopathic") ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_header_phaseII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("exercise_header_phaseII").equals("") ? " selected" : "" %>></option>
										<option value="Walking Daily 30 minutes"<%= document.get("exercise_header_phaseII").equals("Walking Daily 30 minutes") ? " selected" : "" %>>Walking Daily 30 minutes</option>
										<option value="Walking Daily 40-60 minutes"<%= document.get("exercise_header_phaseII").equals("Walking Daily 40-60 minutes") ? " selected" : "" %>>Walking Daily 40-60 minutes</option>
										<option value="Walking & Strength Training"<%= document.get("exercise_header_phaseII").equals("Walking & Strength Training") ? " selected" : "" %>>Walking & Strength Training</option>
										<option value="Cardio 30-60 minutes"<%= document.get("exercise_header_phaseII").equals("Cardio 30-60 minutes") ? " selected" : "" %>>Cardio 30-60 minutes</option>
										<option value="Stretching"<%= document.get("exercise_header_phaseII").equals("Stretching") ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_header_phaseII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("diet_header_phaseII").equals("") ? " selected" : "" %>></option>
										<option value="Nutrition Plan"<%= document.get("diet_header_phaseII").equals("Nutrition Plan") ? " selected" : "" %>>Nutrition Plan</option>
									</select>
									</td>
									<td>
									<select name="stress_header_phaseII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("stress_header_phaseII").equals("") ? " selected" : "" %>></option>
										<option value="Emotional"<%= document.get("stress_header_phaseII").equals("Emotional") ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_header_phaseII").equals("Mental") ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_header_phaseII").equals("Physical") ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_header_phaseII").equals("Spiritual") ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_header_phaseII").equals("Toxins") ? " selected" : "" %>>Toxins</option>
										<option value="Toxin (Mercury)"<%= document.get("stress_header_phaseII").equals("Toxin (Mercury)") ? " selected" : "" %>>Toxin (Mercury)</option>
										<option value="Toxin (Copper)"<%= document.get("stress_header_phaseII").equals("Toxin (Copper)") ? " selected" : "" %>>Toxin (Copper)</option>
										<option value="Toxin (Cadmium)"<%= document.get("stress_header_phaseII").equals("Toxin (Cadmium)") ? " selected" : "" %>>Toxin (Cadmium)</option>
										<option value="Toxin (Aluminium)"<%= document.get("stress_header_phaseII").equals("Toxin (Aluminium)") ? " selected" : "" %>>Toxin (Aluminium)</option>
										<option value="Toxin (Nickel)"<%= document.get("stress_header_phaseII").equals("Toxin (Nickel)") ? " selected" : "" %>>Toxin (Nickel)</option>
										<option value="Toxin (Cobalt)"<%= document.get("stress_header_phaseII").equals("Toxin (Cobalt)") ? " selected" : "" %>>Toxin (Cobalt)</option>
										<option value="Toxin (Manganese)"<%= document.get("stress_header_phaseII").equals("Toxin (Manganese)") ? " selected" : "" %>>Toxin (Manganese)</option>
										<option value="Toxin (Lead)"<%= document.get("stress_header_phaseII").equals("Toxin (Lead)") ? " selected" : "" %>>Toxin (Lead)</option>
										<option value="Biochemical"<%= document.get("stress_header_phaseII").equals("Biochemical") ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_header_phaseII").equals("Energetic (EMF)") ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_phaseII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Multizyme"<%= document.get("supplements_phaseII").indexOf("Multizyme|") > -1 ? " selected" : "" %>>Multizyme</option>
										<option value="Sp Bk Radish"<%= document.get("supplements_phaseII").indexOf("Sp Bk Radish|") > -1 ? " selected" : "" %>>Sp Bk Radish</option>
										<option value="Zypan"<%= document.get("supplements_phaseII").indexOf("Zypan|") > -1 ? " selected" : "" %>>Zypan</option>
										<option value="Pancreatrophin PMG"<%= document.get("supplements_phaseII").indexOf("Pancreatrophin PMG|") > -1 ? " selected" : "" %>>Pancreatrophin PMG</option>
										<option value="Fungal Forte"<%= document.get("supplements_phaseII").indexOf("Fungal Forte|") > -1 ? " selected" : "" %>>Fungal Forte</option>
										<option value="Multi-Probiotic"<%= document.get("supplements_phaseII").indexOf("Multi-Probiotic|") > -1 ? " selected" : "" %>>Multi-Probiotic</option>
										<option value="Cholacol"<%= document.get("supplements_phaseII").indexOf("Cholacol|") > -1 ? " selected" : "" %>>Cholacol</option>
										<option value="Theralac"<%= document.get("supplements_phaseII").indexOf("Theralac|") > -1 ? " selected" : "" %>>Theralac</option>
										<option value="Lactic Acid Yeast waf"<%= document.get("supplements_phaseII").indexOf("Lactic Acid Yeast waf|") > -1 ? " selected" : "" %>>Lactic Acid Yeast waf</option>
										<option value="DiGest (liq)"<%= document.get("supplements_phaseII").indexOf("DiGest (liq)|") > -1 ? " selected" : "" %>>DiGest (liq)</option>
										<option value="DiGest (caps)"<%= document.get("supplements_phaseII").indexOf("DiGest (caps)|") > -1 ? " selected" : "" %>>DiGest (caps)</option>
										<option value="Zymex"<%= document.get("supplements_phaseII").indexOf("Zymex|") > -1 ? " selected" : "" %>>Zymex</option>
										<option value="Zymex waf"<%= document.get("supplements_phaseII").indexOf("Zymex waf|") > -1 ? " selected" : "" %>>Zymex waf</option>
										<option value="Lact-Enz"<%= document.get("supplements_phaseII").indexOf("Lact-Enz|") > -1 ? " selected" : "" %>>Lact-Enz</option>
										<option value="A-F Betafood"<%= document.get("supplements_phaseII").indexOf("A-F Betafood|") > -1 ? " selected" : "" %>>A-F Betafood</option>
										<option value="LivCo"<%= document.get("supplements_phaseII").indexOf("LivCo|") > -1 ? " selected" : "" %>>LivCo</option>
										<option value="DigestPlex (GF)"<%= document.get("supplements_phaseII").indexOf("DigestPlex (GF|") > -1 ? " selected" : "" %>>DigestPlex (GF)</option>
										<option value="Protein Powder"<%= document.get("supplements_phaseII").indexOf("Protein Powder|") > -1 ? " selected" : "" %>>Protein Powder</option>
										<option value="Greens"<%= document.get("supplements_phaseII").indexOf("Greens|") > -1 ? " selected" : "" %>>Greens</option>
										<option value="Cataplex GTF"<%= document.get("supplements_phaseII").indexOf("Cataplex GTF|") > -1 ? " selected" : "" %>>Cataplex GTF</option>
										<option value="Gymnema"<%= document.get("supplements_phaseII").indexOf("Gymnema|") > -1 ? " selected" : "" %>>Gymnema</option>
										<option value="Paraplex"<%= document.get("supplements_phaseII").indexOf("Paraplex|") > -1 ? " selected" : "" %>>Paraplex</option>
										<option value="Inositol"<%= document.get("supplements_phaseII").indexOf("Inositol|") > -1 ? " selected" : "" %>>Inositol</option>
										<option value="Detox Formula (GF)"<%= document.get("supplements_phaseII").indexOf("Detox Formula (GF)|") > -1 ? " selected" : "" %>>Detox Formula (GF)</option>
										<option value="PhytoGreens (GF)"<%= document.get("supplements_phaseII").indexOf("PhytoGreens (GF)|") > -1 ? " selected" : "" %>>PhytoGreens (GF)</option>
										<option value="VitaLiv"<%= document.get("supplements_phaseII").indexOf("VitaLiv|") > -1 ? " selected" : "" %>>VitaLiv</option>
										<option value="BFood"<%= document.get("supplements_phaseII").indexOf("BFood|") > -1 ? " selected" : "" %>>BFood</option>
										<option value="Primal Defense"<%= document.get("supplements_phaseII").indexOf("Primal Defense|") > -1 ? " selected" : "" %>>Primal Defense</option>
										<option value="Chewable Catalyn"<%= document.get("supplements_phaseII").indexOf("Chewable Catalyn|") > -1 ? " selected" : "" %>>Chewable Catalyn</option>
										<option value="Ferrofood"<%= document.get("supplements_phaseII").indexOf("Ferrofood|") > -1 ? " selected" : "" %>>Ferrofood</option>
										<option value="Catalyn"<%= document.get("supplements_phaseII").indexOf("Catalyn|") > -1 ? " selected" : "" %>>Catalyn</option>
										<option value="Boswellia Complex"<%= document.get("supplements_phaseII").indexOf("Boswellia Complex|") > -1 ? " selected" : "" %>>Boswellia Complex</option>
										<option value="Cranberry Complex"<%= document.get("supplements_phaseII").indexOf("Cranberry Complex|") > -1 ? " selected" : "" %>>Cranberry Complex</option>
										<option value="Rehmannia Complex"<%= document.get("supplements_phaseII").indexOf("Rehmannia Complex|") > -1 ? " selected" : "" %>>Rehmannia Complex</option>
										<option value="Withania Complex"<%= document.get("supplements_phaseII").indexOf("Withania Complex|") > -1 ? " selected" : "" %>>Withania Complex</option>
										<option value="Thyroid Complex"<%= document.get("supplements_phaseII").indexOf("Thyroid Complex|") > -1 ? " selected" : "" %>>Thyroid Complex</option>
										<option value="Congaplex"<%= document.get("supplements_phaseII").indexOf("Congaplex|") > -1 ? " selected" : "" %>>Congaplex</option>
										<option value="Cardiotrophin PMG"<%= document.get("supplements_phaseII").indexOf("Cardiotrophin PMG|") > -1 ? " selected" : "" %>>Cardiotrophin PMG</option>
										<option value="Drenatrophin PMG"<%= document.get("supplements_phaseII").indexOf("Drenatrophin PMG|") > -1 ? " selected" : "" %>>Drenatrophin PMG</option>
										<option value="Hypothalamus PMG"<%= document.get("supplements_phaseII").indexOf("Hypothalamus PMG|") > -1 ? " selected" : "" %>>Hypothalamus PMG</option>
										<option value="Mammary PMG"<%= document.get("supplements_phaseII").indexOf("Mammary PMG|") > -1 ? " selected" : "" %>>Mammary PMG</option>
										<option value="Myotrophin PMG"<%= document.get("supplements_phaseII").indexOf("Myotrophin PMG|") > -1 ? " selected" : "" %>>Myotrophin PMG</option>
										<option value="Neurotrophin PMG"<%= document.get("supplements_phaseII").indexOf("Neurotrophin PM|") > -1 ? " selected" : "" %>>Neurotrophin PMG</option>
										<option value="Ostrophin PMG"<%= document.get("supplements_phaseII").indexOf("Ostrophin PMG|") > -1 ? " selected" : "" %>>Ostrophin PMG</option>
										<option value="Ovatrophin PMG"<%= document.get("supplements_phaseII").indexOf("Ovatrophin PMG|") > -1 ? " selected" : "" %>>Ovatrophin PMG</option>
										<option value="Pneumotrophin PMG"<%= document.get("supplements_phaseII").indexOf("Pneumotrophin PMG|") > -1 ? " selected" : "" %>>Pneumotrophin PMG</option>
										<option value="Pituitrophin PMG"<%= document.get("supplements_phaseII").indexOf("Pituitrophin PMG|") > -1 ? " selected" : "" %>>Pituitrophin PMG</option>
										<option value="Prostate PMG"<%= document.get("supplements_phaseII").indexOf("Prostate PMG|") > -1 ? " selected" : "" %>>Prostate PMG</option>
										<option value="Renatrophin PMG"<%= document.get("supplements_phaseII").indexOf("Renatrophin PMG|") > -1 ? " selected" : "" %>>Renatrophin PMG</option>
										<option value="Thymus PMG"<%= document.get("supplements_phaseII").indexOf("Thymus PMG|") > -1 ? " selected" : "" %>>Thymus PMG</option>
										<option value="Thytrophin PMG"<%= document.get("supplements_phaseII").indexOf("Thytrophin PM|") > -1 ? " selected" : "" %>>Thytrophin PMG</option>
										<option value="Utrophin PMG"<%= document.get("supplements_phaseII").indexOf("Utrophin PM|") > -1 ? " selected" : "" %>>Utrophin PMG</option>
										<option value="A-C Carbamide"<%= document.get("supplements_phaseII").indexOf("A-C Carbamide|") > -1 ? " selected" : "" %>>A-C Carbamide</option>
										<option value="FlavoC"<%= document.get("supplements_phaseII").indexOf("FlavoC|") > -1 ? " selected" : "" %>>FlavoC</option>
										<option value="Cat A"<%= document.get("supplements_phaseII").indexOf("Cat A|") > -1 ? " selected" : "" %>>Cat A</option>
										<option value="Cat A-C"<%= document.get("supplements_phaseII").indexOf("Cat A-C|") > -1 ? " selected" : "" %>>Cat A-C</option>
										<option value="Cat A-C-P"<%= document.get("supplements_phaseII").indexOf("Cat A-C-P|") > -1 ? " selected" : "" %>>Cat A-C-P</option>
										<option value="Cat B"<%= document.get("supplements_phaseII").indexOf("Cat B|") > -1 ? " selected" : "" %>>Cat B</option>
										<option value="Cat C"<%= document.get("supplements_phaseII").indexOf("Cat C|") > -1 ? " selected" : "" %>>Cat C</option>
										<option value="Cat D"<%= document.get("supplements_phaseII").indexOf("Cat D|") > -1 ? " selected" : "" %>>Cat D</option>
										<option value="Cat E"<%= document.get("supplements_phaseII").indexOf("Cat E|") > -1 ? " selected" : "" %>>Cat E</option>
										<option value="Cataplex F"<%= document.get("supplements_phaseII").indexOf("Cataplex F|") > -1 ? " selected" : "" %>>Cat F</option>
										<option value="Cataplex G"<%= document.get("supplements_phaseII").indexOf("Cataplex G|") > -1 ? " selected" : "" %>>Cataplex G</option>
										<option value="CalMag"<%= document.get("supplements_phaseII").indexOf("CalMag|") > -1 ? " selected" : "" %>>CalMag</option>
										<option value="Cal Lact Powder"<%= document.get("supplements_phaseII").indexOf("Cal Lact Powder|") > -1 ? " selected" : "" %>>Cal Lact Powder</option>
										<option value="Magnesium Lactate"<%= document.get("supplements_phaseII").indexOf("Magnesium Lactate|") > -1 ? " selected" : "" %>>Magnesium Lactate</option>
										<option value="Manganese B12"<%= document.get("supplements_phaseII").indexOf("Manganese B12|") > -1 ? " selected" : "" %>>Manganese B12</option>
										<option value="Liquid Herbs"<%= document.get("supplements_phaseII").indexOf("Liquid Herbs|") > -1 ? " selected" : "" %>>Liquid Herbs</option>
										<option value="CardioPlus"<%= document.get("supplements_phaseII").indexOf("CardioPlus|") > -1 ? " selected" : "" %>>CardioPlus</option>
										<option value="Adrenal Des"<%= document.get("supplements_phaseII").indexOf("Adrenal Des|") > -1 ? " selected" : "" %>>Adrenal Des</option>
										<option value="Spleen Des"<%= document.get("supplements_phaseII").indexOf("Spleen Des|") > -1 ? " selected" : "" %>>Spleen Des</option>
										<option value="Drenamin"<%= document.get("supplements_phaseII").indexOf("Drenamin|") > -1 ? " selected" : "" %>>Drenamin</option>
										<option value="Fish Oil"<%= document.get("supplements_phaseII").indexOf("Fish Oil|") > -1 ? " selected" : "" %>>Fish Oil</option>
										<option value="OBM"<%= document.get("supplements_phaseII").indexOf("OBM|") > -1 ? " selected" : "" %>>OBM</option>
										<option value="Prolamine Iodine"<%= document.get("supplements_phaseII").indexOf("Prolamine Iodine|") > -1 ? " selected" : "" %>>Prolamine Iodine</option>
										<option value="Parotid"<%= document.get("supplements_phaseII").indexOf("Parotid|") > -1 ? " selected" : "" %>>Parotid</option>
										<option value="Cholacol II"<%= document.get("supplements_phaseII").indexOf("Cholacol II|") > -1 ? " selected" : "" %>>Cholacol II</option>
										<option value="Cholaplex"<%= document.get("supplements_phaseII").indexOf("Cholaplex|") > -1 ? " selected" : "" %>>Cholaplex</option>
										<option value="Zinc Liver Chelate"<%= document.get("supplements_phaseII").indexOf("Zinc Liver Chelate|") > -1 ? " selected" : "" %>>Zinc Liver Chelate</option>
										<option value="Protefood"<%= document.get("supplements_phaseII").indexOf("Protefood|") > -1 ? " selected" : "" %>>Protefood</option>
										<option value="MolyCu"<%= document.get("supplements_phaseII").indexOf("MolyCu|") > -1 ? " selected" : "" %>>MolyCu</option>
										<option value="GB3"<%= document.get("supplements_phaseII").indexOf("GB3|") > -1 ? " selected" : "" %>>GB3</option>
										<option value="EndoPan"<%= document.get("supplements_phaseII").indexOf("EndoPan|") > -1 ? " selected" : "" %>>EndoPan</option>
										<option value="Flaxseed oil"<%= document.get("supplements_phaseII").indexOf("Flaxseed oil|") > -1 ? " selected" : "" %>>Flaxseed oil</option>
										<option value="Arginex"<%= document.get("supplements_phaseII").indexOf("Arginex|") > -1 ? " selected" : "" %>>Arginex</option>
										<option value="Rehmannia (liq)"<%= document.get("supplements_phaseII").indexOf("Rehmannia (liq)|") > -1 ? " selected" : "" %>>Rehmannia (liq)</option>
										<option value="Saligesic"<%= document.get("supplements_phaseII").indexOf("Saligesic|") > -1 ? " selected" : "" %>>Saligesic</option>
										<option value="Vitanox"<%= document.get("supplements_phaseII").indexOf("Vitanox|") > -1 ? " selected" : "" %>>Vitanox</option>
										<option value="St. John's Wort"<%= document.get("supplements_phaseII").indexOf("St. John's Wort|") > -1 ? " selected" : "" %>>St. John's Wort</option>
										<option value="California Poppy"<%= document.get("supplements_phaseII").indexOf("California Poppy|") > -1 ? " selected" : "" %>>California Poppy</option>
										<option value="Broncafect"<%= document.get("supplements_phaseII").indexOf("Broncafect|") > -1 ? " selected" : "" %>>Broncafect</option>
										<option value="Min-Tran"<%= document.get("supplements_phaseII").indexOf("Min-Tran|") > -1 ? " selected" : "" %>>Min-Tran</option>
										<option value="Min-Chex"<%= document.get("supplements_phaseII").indexOf("Min-Chex|") > -1 ? " selected" : "" %>>Min-Chex</option>
										<option value="Progon B"<%= document.get("supplements_phaseII").indexOf("Progon B|") > -1 ? " selected" : "" %>>Progon B</option>
										<option value="Tribulus"<%= document.get("supplements_phaseII").indexOf("Tribulus|") > -1 ? " selected" : "" %>>Tribulus</option>
										<option value="Wheat Germ oil"<%= document.get("supplements_phaseII").indexOf("Wheat Germ oil|") > -1 ? " selected" : "" %>>Wheat Germ oil</option>
										<option value="Symplex F"<%= document.get("supplements_phaseII").indexOf("Symplex F|") > -1 ? " selected" : "" %>>Symplex F</option>
										<option value="Glucosamine Synergy"<%= document.get("supplements_phaseII").indexOf("Glucosamine Synergy|") > -1 ? " selected" : "" %>>Glucosamine Synergy</option>
										<option value="Ligaplex II"<%= document.get("supplements_phaseII").indexOf("Ligaplex II|") > -1 ? " selected" : "" %>>Ligaplex II</option>
										<option value="Collagen C"<%= document.get("supplements_phaseII").indexOf("Collagen C|") > -1 ? " selected" : "" %>>Collagen C</option>
										<option value="Symplex M"<%= document.get("supplements_phaseII").indexOf("Symplex M|") > -1 ? " selected" : "" %>>Symplex M</option>
										<option value="Male Support"<%= document.get("supplements_phaseII").indexOf("Male Support|") > -1 ? " selected" : "" %>>Male Support</option>
										<option value="Orchex"<%= document.get("supplements_phaseII").indexOf("Orchex|") > -1 ? " selected" : "" %>>Orchex</option>
										<option value="Lipoic Acid"<%= document.get("supplements_phaseII").indexOf("Lipoic Acid|") > -1 ? " selected" : "" %>>Lipoic Acid</option>
										<option value="Ambrotose"<%= document.get("supplements_phaseII").indexOf("Ambrotose|") > -1 ? " selected" : "" %>>Ambrotose</option>
									</select>
									</td>
									<td>
									<select name="detox_phaseII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Liver/GB Flush"<%= document.get("detox_phaseII").indexOf("Liver/GB Flush|") > -1 ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_phaseII").indexOf("SP Cleanse|") > -1 ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_phaseII").indexOf("Colon Cleanse|") > -1 ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_phaseII").indexOf("Juice Fast|") > -1 ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_phaseII").indexOf("Blood Cleanse|") > -1 ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_phaseII").indexOf("Homeopathic|") > -1 ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_phaseII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Walk Daily 30 min"<%= document.get("exercise_phaseII").indexOf("Walk Daily 30 min|") > -1 ? " selected" : "" %>>Walk Daily 30 min</option>
										<option value="Walk Daily 40-60 min"<%= document.get("exercise_phaseII").indexOf("Walk Daily 40-60 min|") > -1 ? " selected" : "" %>>Walk Daily 40-60 min</option>
										<option value="Walk/Strength Train"<%= document.get("exercise_phaseII").indexOf("Walk/Strength Train|") > -1 ? " selected" : "" %>>Walk/Strength Train</option>
										<option value="Cardio 30-60 min"<%= document.get("exercise_phaseII").indexOf("Cardio 30-60 min|") > -1 ? " selected" : "" %>>Cardio 30-60 min</option>
										<option value="Stretching"<%= document.get("exercise_phaseII").indexOf("Stretching|") > -1 ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_phaseII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="No sugar/fruit"<%= document.get("diet_phaseII").indexOf("No sugar/fruit|") > -1 ? " selected" : "" %>>No sugar/fruit</option>
										<option value="Protein Shakes"<%= document.get("diet_phaseII").indexOf("Protein Shakes|") > -1 ? " selected" : "" %>>Protein Shakes</option>
										<option value="Protein Shakes w/greens"<%= document.get("diet_phaseII").indexOf("Protein Shakes w/greens|") > -1 ? " selected" : "" %>>Protein Shakes w/greens</option>
										<option value="Protein Shakes w/flax oil"<%= document.get("diet_phaseII").indexOf("Protein Shakes w/flax oil|") > -1 ? " selected" : "" %>>Protein Shakes w/flax oil</option>
										<option value="Flax seed oil"<%= document.get("diet_phaseII").indexOf("Flax seed oil|") > -1 ? " selected" : "" %>>Flax seed oil</option>
										<option value="Garlic"<%= document.get("diet_phaseII").indexOf("Garlic|") > -1 ? " selected" : "" %>>Garlic</option>
										<option value="Water Testing"<%= document.get("diet_phaseII").indexOf("Water Testing|") > -1 ? " selected" : "" %>>Water Testing</option>
									</select>
									</td>
									<td>
									<select name="stress_phaseII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Emotional"<%= document.get("stress_phaseII").indexOf("Emotional|") > -1 ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_phaseII").indexOf("Mental|") > -1 ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_phaseII").indexOf("Physical|") > -1 ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_phaseII").indexOf("Spiritual|") > -1 ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_phaseII").indexOf("Toxins|") > -1 ? " selected" : "" %>>Toxins</option>
										<option value="Lead"<%= document.get("stress_phaseII").indexOf("Lead|") > -1 ? " selected" : "" %>>Lead</option>
										<option value="Mercury"<%= document.get("stress_phaseII").indexOf("Mercury|") > -1 ? " selected" : "" %>>Mercury</option>
										<option value="Cadmium"<%= document.get("stress_phaseII").indexOf("Cadmium|") > -1 ? " selected" : "" %>>Cadmium</option>
										<option value="Arsenic"<%= document.get("stress_phaseII").indexOf("Arsenic|") > -1 ? " selected" : "" %>>Arsenic</option>
										<option value="Aluminium"<%= document.get("stress_phaseII").indexOf("Aluminium|") > -1 ? " selected" : "" %>>Aluminium</option>
										<option value="Copper"<%= document.get("stress_phaseII").indexOf("Copper|") > -1 ? " selected" : "" %>>Copper</option>
										<option value="Nickel"<%= document.get("stress_phaseII").indexOf("Nickel|") > -1 ? " selected" : "" %>>Nickel</option>
										<option value="Zinc"<%= document.get("stress_phaseII").indexOf("Zinc|") > -1 ? " selected" : "" %>>Zinc</option>
										<option value="Cobalt"<%= document.get("stress_phaseII").indexOf("Cobalt|") > -1 ? " selected" : "" %>>Cobalt</option>
										<option value="Manganese"<%= document.get("stress_phaseII").indexOf("Manganese|") > -1 ? " selected" : "" %>>Manganese</option>
										<option value="Biochemical"<%= document.get("stress_phaseII").indexOf("Biochemical|") > -1 ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_phaseII").indexOf("Energetic (EMF)|") > -1 ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
								    </table>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">PHASE III DATE</div>
								<div class="right">
									<input name="dateInputPhaseIII" onfocus="getDate('userMineralRatiosForm','dateInputPhaseIII');select();" value="<%= document.getPhaseIIIDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">
								    <table>
									<tr>
									<td>
									<div >Supplements</div>
									</td>
									<td>
									<div >Detox</div>
									</td>
									<td>
									<div >Exercise</div>
									</td>
									<td>
									<div >Diet</div>
									</td>
									<td>
									<div >Stress</div>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_header_phaseIII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("supplements_header_phaseIII").equals("") ? " selected" : "" %>></option>
										<option value="Digestive Support"<%= document.get("supplements_header_phaseIII").equals("Digestive Support") ? " selected" : "" %>>Digestive Support</option>
										<option value="Adrenal/Thyroid Support"<%= document.get("supplements_header_phaseIII").equals("Adrenal/Thyroid Support") ? " selected" : "" %>>Adrenal/Thyroid Support</option>
										<option value="Thyroid/Adrenal Support"<%= document.get("supplements_header_phaseIII").equals("Thyroid/Adrenal Support") ? " selected" : "" %>>Thyroid/Adrenal Support</option>
										<option value="Adrenal Support"<%= document.get("supplements_header_phaseIII").equals("Adrenal Support") ? " selected" : "" %>>Adrenal Support</option>
										<option value="Thyroid Support"<%= document.get("supplements_header_phaseIII").equals("Thyroid Support") ? " selected" : "" %>>Thyroid Support</option>
										<option value="Heavy Metal Detox"<%= document.get("supplements_header_phaseIII").equals("Heavy Metal Detox") ? " selected" : "" %>>Heavy Metal Detox</option>
										<option value="Copper Toxicity"<%= document.get("supplements_header_phaseIII").equals("Copper Toxicity") ? " selected" : "" %>>Copper Toxicity</option>
										<option value="Copper Detox/Heavy Metals"<%= document.get("supplements_header_phaseIII").equals("Copper Detox/Heavy Metals") ? " selected" : "" %>>Copper Detox/Heavy Metals</option>
										<option value="Maintenance Supplementation"<%= document.get("supplements_header_phaseIII").equals("Maintenance Supplementation") ? " selected" : "" %>>Maintenance Supplementation</option>
									</select>
									</td>
									<td>
									<select name="detox_header_phaseIII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("detox_header_phaseIII").equals("") ? " selected" : "" %>></option>
										<option value="Liver/GB Flush"<%= document.get("detox_header_phaseIII").equals("Liver/GB Flush") ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_header_phaseIII").equals("SP Cleanse") ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_header_phaseIII").equals("Colon Cleanse") ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_header_phaseIII").equals("Juice Fast") ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_header_phaseIII").equals("Blood Cleanse") ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_header_phaseIII").equals("Homeopathic") ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_header_phaseIII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("exercise_header_phaseIII").equals("") ? " selected" : "" %>></option>
										<option value="Walking Daily 30 minutes"<%= document.get("exercise_header_phaseIII").equals("Walking Daily 30 minutes") ? " selected" : "" %>>Walking Daily 30 minutes</option>
										<option value="Walking Daily 40-60 minutes"<%= document.get("exercise_header_phaseIII").equals("Walking Daily 40-60 minutes") ? " selected" : "" %>>Walking Daily 40-60 minutes</option>
										<option value="Walking & Strength Training"<%= document.get("exercise_header_phaseIII").equals("Walking & Strength Training") ? " selected" : "" %>>Walking & Strength Training</option>
										<option value="Cardio 30-60 minutes"<%= document.get("exercise_header_phaseIII").equals("Cardio 30-60 minutes") ? " selected" : "" %>>Cardio 30-60 minutes</option>
										<option value="Stretching"<%= document.get("exercise_header_phaseIII").equals("Stretching") ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_header_phaseIII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("diet_header_phaseIII").equals("") ? " selected" : "" %>></option>
										<option value="Nutrition Plan"<%= document.get("diet_header_phaseIII").equals("Nutrition Plan") ? " selected" : "" %>>Nutrition Plan</option>
									</select>
									</td>
									<td>
									<select name="stress_header_phaseIII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("stress_header_phaseIII").equals("") ? " selected" : "" %>></option>
										<option value="Emotional"<%= document.get("stress_header_phaseIII").equals("Emotional") ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_header_phaseIII").equals("Mental") ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_header_phaseIII").equals("Physical") ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_header_phaseIII").equals("Spiritual") ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_header_phaseIII").equals("Toxins") ? " selected" : "" %>>Toxins</option>
										<option value="Toxin (Mercury)"<%= document.get("stress_header_phaseIII").equals("Toxin (Mercury)") ? " selected" : "" %>>Toxin (Mercury)</option>
										<option value="Toxin (Copper)"<%= document.get("stress_header_phaseIII").equals("Toxin (Copper)") ? " selected" : "" %>>Toxin (Copper)</option>
										<option value="Toxin (Cadmium)"<%= document.get("stress_header_phaseIII").equals("Toxin (Cadmium)") ? " selected" : "" %>>Toxin (Cadmium)</option>
										<option value="Toxin (Aluminium)"<%= document.get("stress_header_phaseIII").equals("Toxin (Aluminium)") ? " selected" : "" %>>Toxin (Aluminium)</option>
										<option value="Toxin (Nickel)"<%= document.get("stress_header_phaseIII").equals("Toxin (Nickel)") ? " selected" : "" %>>Toxin (Nickel)</option>
										<option value="Toxin (Cobalt)"<%= document.get("stress_header_phaseIII").equals("Toxin (Cobalt)") ? " selected" : "" %>>Toxin (Cobalt)</option>
										<option value="Toxin (Manganese)"<%= document.get("stress_header_phaseIII").equals("Toxin (Manganese)") ? " selected" : "" %>>Toxin (Manganese)</option>
										<option value="Toxin (Lead)"<%= document.get("stress_header_phaseIII").equals("Toxin (Lead)") ? " selected" : "" %>>Toxin (Lead)</option>
										<option value="Biochemical"<%= document.get("stress_header_phaseIII").equals("Biochemical") ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_header_phaseIII").equals("Energetic (EMF)") ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_phaseIII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Multizyme"<%= document.get("supplements_phaseIII").indexOf("Multizyme|") > -1 ? " selected" : "" %>>Multizyme</option>
										<option value="Sp Bk Radish"<%= document.get("supplements_phaseIII").indexOf("Sp Bk Radish|") > -1 ? " selected" : "" %>>Sp Bk Radish</option>
										<option value="Zypan"<%= document.get("supplements_phaseIII").indexOf("Zypan|") > -1 ? " selected" : "" %>>Zypan</option>
										<option value="Pancreatrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Pancreatrophin PMG|") > -1 ? " selected" : "" %>>Pancreatrophin PMG</option>
										<option value="Fungal Forte"<%= document.get("supplements_phaseIII").indexOf("Fungal Forte|") > -1 ? " selected" : "" %>>Fungal Forte</option>
										<option value="Multi-Probiotic"<%= document.get("supplements_phaseIII").indexOf("Multi-Probiotic|") > -1 ? " selected" : "" %>>Multi-Probiotic</option>
										<option value="Cholacol"<%= document.get("supplements_phaseIII").indexOf("Cholacol|") > -1 ? " selected" : "" %>>Cholacol</option>
										<option value="Theralac"<%= document.get("supplements_phaseIII").indexOf("Theralac|") > -1 ? " selected" : "" %>>Theralac</option>
										<option value="Lactic Acid Yeast waf"<%= document.get("supplements_phaseIII").indexOf("Lactic Acid Yeast waf|") > -1 ? " selected" : "" %>>Lactic Acid Yeast waf</option>
										<option value="DiGest (liq)"<%= document.get("supplements_phaseIII").indexOf("DiGest (liq)|") > -1 ? " selected" : "" %>>DiGest (liq)</option>
										<option value="DiGest (caps)"<%= document.get("supplements_phaseIII").indexOf("DiGest (caps)|") > -1 ? " selected" : "" %>>DiGest (caps)</option>
										<option value="Zymex"<%= document.get("supplements_phaseIII").indexOf("Zymex|") > -1 ? " selected" : "" %>>Zymex</option>
										<option value="Zymex waf"<%= document.get("supplements_phaseIII").indexOf("Zymex waf|") > -1 ? " selected" : "" %>>Zymex waf</option>
										<option value="Lact-Enz"<%= document.get("supplements_phaseIII").indexOf("Lact-Enz|") > -1 ? " selected" : "" %>>Lact-Enz</option>
										<option value="A-F Betafood"<%= document.get("supplements_phaseIII").indexOf("A-F Betafood|") > -1 ? " selected" : "" %>>A-F Betafood</option>
										<option value="LivCo"<%= document.get("supplements_phaseIII").indexOf("LivCo|") > -1 ? " selected" : "" %>>LivCo</option>
										<option value="DigestPlex (GF)"<%= document.get("supplements_phaseIII").indexOf("DigestPlex (GF|") > -1 ? " selected" : "" %>>DigestPlex (GF)</option>
										<option value="Protein Powder"<%= document.get("supplements_phaseIII").indexOf("Protein Powder|") > -1 ? " selected" : "" %>>Protein Powder</option>
										<option value="Greens"<%= document.get("supplements_phaseIII").indexOf("Greens|") > -1 ? " selected" : "" %>>Greens</option>
										<option value="Cataplex GTF"<%= document.get("supplements_phaseIII").indexOf("Cataplex GTF|") > -1 ? " selected" : "" %>>Cataplex GTF</option>
										<option value="Gymnema"<%= document.get("supplements_phaseIII").indexOf("Gymnema|") > -1 ? " selected" : "" %>>Gymnema</option>
										<option value="Paraplex"<%= document.get("supplements_phaseIII").indexOf("Paraplex|") > -1 ? " selected" : "" %>>Paraplex</option>
										<option value="Inositol"<%= document.get("supplements_phaseIII").indexOf("Inositol|") > -1 ? " selected" : "" %>>Inositol</option>
										<option value="Detox Formula (GF)"<%= document.get("supplements_phaseIII").indexOf("Detox Formula (GF)|") > -1 ? " selected" : "" %>>Detox Formula (GF)</option>
										<option value="PhytoGreens (GF)"<%= document.get("supplements_phaseIII").indexOf("PhytoGreens (GF)|") > -1 ? " selected" : "" %>>PhytoGreens (GF)</option>
										<option value="VitaLiv"<%= document.get("supplements_phaseIII").indexOf("VitaLiv|") > -1 ? " selected" : "" %>>VitaLiv</option>
										<option value="BFood"<%= document.get("supplements_phaseIII").indexOf("BFood|") > -1 ? " selected" : "" %>>BFood</option>
										<option value="Primal Defense"<%= document.get("supplements_phaseIII").indexOf("Primal Defense|") > -1 ? " selected" : "" %>>Primal Defense</option>
										<option value="Chewable Catalyn"<%= document.get("supplements_phaseIII").indexOf("Chewable Catalyn|") > -1 ? " selected" : "" %>>Chewable Catalyn</option>
										<option value="Ferrofood"<%= document.get("supplements_phaseIII").indexOf("Ferrofood|") > -1 ? " selected" : "" %>>Ferrofood</option>
										<option value="Catalyn"<%= document.get("supplements_phaseIII").indexOf("Catalyn|") > -1 ? " selected" : "" %>>Catalyn</option>
										<option value="Boswellia Complex"<%= document.get("supplements_phaseIII").indexOf("Boswellia Complex|") > -1 ? " selected" : "" %>>Boswellia Complex</option>
										<option value="Cranberry Complex"<%= document.get("supplements_phaseIII").indexOf("Cranberry Complex|") > -1 ? " selected" : "" %>>Cranberry Complex</option>
										<option value="Rehmannia Complex"<%= document.get("supplements_phaseIII").indexOf("Rehmannia Complex|") > -1 ? " selected" : "" %>>Rehmannia Complex</option>
										<option value="Withania Complex"<%= document.get("supplements_phaseIII").indexOf("Withania Complex|") > -1 ? " selected" : "" %>>Withania Complex</option>
										<option value="Thyroid Complex"<%= document.get("supplements_phaseIII").indexOf("Thyroid Complex|") > -1 ? " selected" : "" %>>Thyroid Complex</option>
										<option value="Congaplex"<%= document.get("supplements_phaseIII").indexOf("Congaplex|") > -1 ? " selected" : "" %>>Congaplex</option>
										<option value="Cardiotrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Cardiotrophin PMG|") > -1 ? " selected" : "" %>>Cardiotrophin PMG</option>
										<option value="Drenatrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Drenatrophin PMG|") > -1 ? " selected" : "" %>>Drenatrophin PMG</option>
										<option value="Hypothalamus PMG"<%= document.get("supplements_phaseIII").indexOf("Hypothalamus PMG|") > -1 ? " selected" : "" %>>Hypothalamus PMG</option>
										<option value="Mammary PMG"<%= document.get("supplements_phaseIII").indexOf("Mammary PMG|") > -1 ? " selected" : "" %>>Mammary PMG</option>
										<option value="Myotrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Myotrophin PMG|") > -1 ? " selected" : "" %>>Myotrophin PMG</option>
										<option value="Neurotrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Neurotrophin PM|") > -1 ? " selected" : "" %>>Neurotrophin PMG</option>
										<option value="Ostrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Ostrophin PMG|") > -1 ? " selected" : "" %>>Ostrophin PMG</option>
										<option value="Ovatrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Ovatrophin PMG|") > -1 ? " selected" : "" %>>Ovatrophin PMG</option>
										<option value="Pneumotrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Pneumotrophin PMG|") > -1 ? " selected" : "" %>>Pneumotrophin PMG</option>
										<option value="Pituitrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Pituitrophin PMG|") > -1 ? " selected" : "" %>>Pituitrophin PMG</option>
										<option value="Prostate PMG"<%= document.get("supplements_phaseIII").indexOf("Prostate PMG|") > -1 ? " selected" : "" %>>Prostate PMG</option>
										<option value="Renatrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Renatrophin PMG|") > -1 ? " selected" : "" %>>Renatrophin PMG</option>
										<option value="Thymus PMG"<%= document.get("supplements_phaseIII").indexOf("Thymus PMG|") > -1 ? " selected" : "" %>>Thymus PMG</option>
										<option value="Thytrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Thytrophin PM|") > -1 ? " selected" : "" %>>Thytrophin PMG</option>
										<option value="Utrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Utrophin PM|") > -1 ? " selected" : "" %>>Utrophin PMG</option>
										<option value="A-C Carbamide"<%= document.get("supplements_phaseIII").indexOf("A-C Carbamide|") > -1 ? " selected" : "" %>>A-C Carbamide</option>
										<option value="FlavoC"<%= document.get("supplements_phaseIII").indexOf("FlavoC|") > -1 ? " selected" : "" %>>FlavoC</option>
										<option value="Cat A"<%= document.get("supplements_phaseIII").indexOf("Cat A|") > -1 ? " selected" : "" %>>Cat A</option>
										<option value="Cat A-C"<%= document.get("supplements_phaseIII").indexOf("Cat A-C|") > -1 ? " selected" : "" %>>Cat A-C</option>
										<option value="Cat A-C-P"<%= document.get("supplements_phaseIII").indexOf("Cat A-C-P|") > -1 ? " selected" : "" %>>Cat A-C-P</option>
										<option value="Cat B"<%= document.get("supplements_phaseIII").indexOf("Cat B|") > -1 ? " selected" : "" %>>Cat B</option>
										<option value="Cat C"<%= document.get("supplements_phaseIII").indexOf("Cat C|") > -1 ? " selected" : "" %>>Cat C</option>
										<option value="Cat D"<%= document.get("supplements_phaseIII").indexOf("Cat D|") > -1 ? " selected" : "" %>>Cat D</option>
										<option value="Cat E"<%= document.get("supplements_phaseIII").indexOf("Cat E|") > -1 ? " selected" : "" %>>Cat E</option>
										<option value="Cataplex F"<%= document.get("supplements_phaseIII").indexOf("Cataplex F|") > -1 ? " selected" : "" %>>Cat F</option>
										<option value="Cataplex G"<%= document.get("supplements_phaseIII").indexOf("Cataplex G|") > -1 ? " selected" : "" %>>Cataplex G</option>
										<option value="CalMag"<%= document.get("supplements_phaseIII").indexOf("CalMag|") > -1 ? " selected" : "" %>>CalMag</option>
										<option value="Cal Lact Powder"<%= document.get("supplements_phaseIII").indexOf("Cal Lact Powder|") > -1 ? " selected" : "" %>>Cal Lact Powder</option>
										<option value="Magnesium Lactate"<%= document.get("supplements_phaseIII").indexOf("Magnesium Lactate|") > -1 ? " selected" : "" %>>Magnesium Lactate</option>
										<option value="Manganese B12"<%= document.get("supplements_phaseIII").indexOf("Manganese B12|") > -1 ? " selected" : "" %>>Manganese B12</option>
										<option value="Liquid Herbs"<%= document.get("supplements_phaseIII").indexOf("Liquid Herbs|") > -1 ? " selected" : "" %>>Liquid Herbs</option>
										<option value="CardioPlus"<%= document.get("supplements_phaseIII").indexOf("CardioPlus|") > -1 ? " selected" : "" %>>CardioPlus</option>
										<option value="Adrenal Des"<%= document.get("supplements_phaseIII").indexOf("Adrenal Des|") > -1 ? " selected" : "" %>>Adrenal Des</option>
										<option value="Spleen Des"<%= document.get("supplements_phaseIII").indexOf("Spleen Des|") > -1 ? " selected" : "" %>>Spleen Des</option>
										<option value="Drenamin"<%= document.get("supplements_phaseIII").indexOf("Drenamin|") > -1 ? " selected" : "" %>>Drenamin</option>
										<option value="Fish Oil"<%= document.get("supplements_phaseIII").indexOf("Fish Oil|") > -1 ? " selected" : "" %>>Fish Oil</option>
										<option value="OBM"<%= document.get("supplements_phaseIII").indexOf("OBM|") > -1 ? " selected" : "" %>>OBM</option>
										<option value="Prolamine Iodine"<%= document.get("supplements_phaseIII").indexOf("Prolamine Iodine|") > -1 ? " selected" : "" %>>Prolamine Iodine</option>
										<option value="Parotid"<%= document.get("supplements_phaseIII").indexOf("Parotid|") > -1 ? " selected" : "" %>>Parotid</option>
										<option value="Cholacol II"<%= document.get("supplements_phaseIII").indexOf("Cholacol II|") > -1 ? " selected" : "" %>>Cholacol II</option>
										<option value="Cholaplex"<%= document.get("supplements_phaseIII").indexOf("Cholaplex|") > -1 ? " selected" : "" %>>Cholaplex</option>
										<option value="Zinc Liver Chelate"<%= document.get("supplements_phaseIII").indexOf("Zinc Liver Chelate|") > -1 ? " selected" : "" %>>Zinc Liver Chelate</option>
										<option value="Protefood"<%= document.get("supplements_phaseIII").indexOf("Protefood|") > -1 ? " selected" : "" %>>Protefood</option>
										<option value="MolyCu"<%= document.get("supplements_phaseIII").indexOf("MolyCu|") > -1 ? " selected" : "" %>>MolyCu</option>
										<option value="GB3"<%= document.get("supplements_phaseIII").indexOf("GB3|") > -1 ? " selected" : "" %>>GB3</option>
										<option value="EndoPan"<%= document.get("supplements_phaseIII").indexOf("EndoPan|") > -1 ? " selected" : "" %>>EndoPan</option>
										<option value="Flaxseed oil"<%= document.get("supplements_phaseIII").indexOf("Flaxseed oil|") > -1 ? " selected" : "" %>>Flaxseed oil</option>
										<option value="Arginex"<%= document.get("supplements_phaseIII").indexOf("Arginex|") > -1 ? " selected" : "" %>>Arginex</option>
										<option value="Rehmannia (liq)"<%= document.get("supplements_phaseIII").indexOf("Rehmannia (liq)|") > -1 ? " selected" : "" %>>Rehmannia (liq)</option>
										<option value="Saligesic"<%= document.get("supplements_phaseIII").indexOf("Saligesic|") > -1 ? " selected" : "" %>>Saligesic</option>
										<option value="Vitanox"<%= document.get("supplements_phaseIII").indexOf("Vitanox|") > -1 ? " selected" : "" %>>Vitanox</option>
										<option value="St. John's Wort"<%= document.get("supplements_phaseIII").indexOf("St. John's Wort|") > -1 ? " selected" : "" %>>St. John's Wort</option>
										<option value="California Poppy"<%= document.get("supplements_phaseIII").indexOf("California Poppy|") > -1 ? " selected" : "" %>>California Poppy</option>
										<option value="Broncafect"<%= document.get("supplements_phaseIII").indexOf("Broncafect|") > -1 ? " selected" : "" %>>Broncafect</option>
										<option value="Min-Tran"<%= document.get("supplements_phaseIII").indexOf("Min-Tran|") > -1 ? " selected" : "" %>>Min-Tran</option>
										<option value="Min-Chex"<%= document.get("supplements_phaseIII").indexOf("Min-Chex|") > -1 ? " selected" : "" %>>Min-Chex</option>
										<option value="Progon B"<%= document.get("supplements_phaseIII").indexOf("Progon B|") > -1 ? " selected" : "" %>>Progon B</option>
										<option value="Tribulus"<%= document.get("supplements_phaseIII").indexOf("Tribulus|") > -1 ? " selected" : "" %>>Tribulus</option>
										<option value="Wheat Germ oil"<%= document.get("supplements_phaseIII").indexOf("Wheat Germ oil|") > -1 ? " selected" : "" %>>Wheat Germ oil</option>
										<option value="Symplex F"<%= document.get("supplements_phaseIII").indexOf("Symplex F|") > -1 ? " selected" : "" %>>Symplex F</option>
										<option value="Glucosamine Synergy"<%= document.get("supplements_phaseIII").indexOf("Glucosamine Synergy|") > -1 ? " selected" : "" %>>Glucosamine Synergy</option>
										<option value="Ligaplex II"<%= document.get("supplements_phaseIII").indexOf("Ligaplex II|") > -1 ? " selected" : "" %>>Ligaplex II</option>
										<option value="Collagen C"<%= document.get("supplements_phaseIII").indexOf("Collagen C|") > -1 ? " selected" : "" %>>Collagen C</option>
										<option value="Symplex M"<%= document.get("supplements_phaseIII").indexOf("Symplex M|") > -1 ? " selected" : "" %>>Symplex M</option>
										<option value="Male Support"<%= document.get("supplements_phaseIII").indexOf("Male Support|") > -1 ? " selected" : "" %>>Male Support</option>
										<option value="Orchex"<%= document.get("supplements_phaseIII").indexOf("Orchex|") > -1 ? " selected" : "" %>>Orchex</option>
										<option value="Lipoic Acid"<%= document.get("supplements_phaseIII").indexOf("Lipoic Acid|") > -1 ? " selected" : "" %>>Lipoic Acid</option>
										<option value="Ambrotose"<%= document.get("supplements_phaseIII").indexOf("Ambrotose|") > -1 ? " selected" : "" %>>Ambrotose</option>
									</select>
									</td>
									<td>
									<select name="detox_phaseIII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Liver/GB Flush"<%= document.get("detox_phaseIII").indexOf("Liver/GB Flush|") > -1 ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_phaseIII").indexOf("SP Cleanse|") > -1 ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_phaseIII").indexOf("Colon Cleanse|") > -1 ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_phaseIII").indexOf("Juice Fast|") > -1 ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_phaseIII").indexOf("Blood Cleanse|") > -1 ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_phaseIII").indexOf("Homeopathic|") > -1 ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_phaseIII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Walk Daily 30 min"<%= document.get("exercise_phaseIII").indexOf("Walk Daily 30 min|") > -1 ? " selected" : "" %>>Walk Daily 30 min</option>
										<option value="Walk Daily 40-60 min"<%= document.get("exercise_phaseIII").indexOf("Walk Daily 40-60 min|") > -1 ? " selected" : "" %>>Walk Daily 40-60 min</option>
										<option value="Walk/Strength Train"<%= document.get("exercise_phaseIII").indexOf("Walk/Strength Train|") > -1 ? " selected" : "" %>>Walk/Strength Train</option>
										<option value="Cardio 30-60 min"<%= document.get("exercise_phaseIII").indexOf("Cardio 30-60 min|") > -1 ? " selected" : "" %>>Cardio 30-60 min</option>
										<option value="Stretching"<%= document.get("exercise_phaseIII").indexOf("Stretching|") > -1 ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_phaseIII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="No sugar/fruit"<%= document.get("diet_phaseIII").indexOf("No sugar/fruit|") > -1 ? " selected" : "" %>>No sugar/fruit</option>
										<option value="Protein Shakes"<%= document.get("diet_phaseIII").indexOf("Protein Shakes|") > -1 ? " selected" : "" %>>Protein Shakes</option>
										<option value="Protein Shakes w/greens"<%= document.get("diet_phaseIII").indexOf("Protein Shakes w/greens|") > -1 ? " selected" : "" %>>Protein Shakes w/greens</option>
										<option value="Protein Shakes w/flax oil"<%= document.get("diet_phaseIII").indexOf("Protein Shakes w/flax oil|") > -1 ? " selected" : "" %>>Protein Shakes w/flax oil</option>
										<option value="Flax seed oil"<%= document.get("diet_phaseIII").indexOf("Flax seed oil|") > -1 ? " selected" : "" %>>Flax seed oil</option>
										<option value="Garlic"<%= document.get("diet_phaseIII").indexOf("Garlic|") > -1 ? " selected" : "" %>>Garlic</option>
										<option value="Water Testing"<%= document.get("diet_phaseIII").indexOf("Water Testing|") > -1 ? " selected" : "" %>>Water Testing</option>
									</select>
									</td>
									<td>
									<select name="stress_phaseIII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Emotional"<%= document.get("stress_phaseIII").indexOf("Emotional|") > -1 ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_phaseIII").indexOf("Mental|") > -1 ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_phaseIII").indexOf("Physical|") > -1 ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_phaseIII").indexOf("Spiritual|") > -1 ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_phaseIII").indexOf("Toxins|") > -1 ? " selected" : "" %>>Toxins</option>
										<option value="Lead"<%= document.get("stress_phaseIII").indexOf("Lead|") > -1 ? " selected" : "" %>>Lead</option>
										<option value="Mercury"<%= document.get("stress_phaseIII").indexOf("Mercury|") > -1 ? " selected" : "" %>>Mercury</option>
										<option value="Cadmium"<%= document.get("stress_phaseIII").indexOf("Cadmium|") > -1 ? " selected" : "" %>>Cadmium</option>
										<option value="Arsenic"<%= document.get("stress_phaseIII").indexOf("Arsenic|") > -1 ? " selected" : "" %>>Arsenic</option>
										<option value="Aluminium"<%= document.get("stress_phaseIII").indexOf("Aluminium|") > -1 ? " selected" : "" %>>Aluminium</option>
										<option value="Copper"<%= document.get("stress_phaseIII").indexOf("Copper|") > -1 ? " selected" : "" %>>Copper</option>
										<option value="Nickel"<%= document.get("stress_phaseIII").indexOf("Nickel|") > -1 ? " selected" : "" %>>Nickel</option>
										<option value="Zinc"<%= document.get("stress_phaseIII").indexOf("Zinc|") > -1 ? " selected" : "" %>>Zinc</option>
										<option value="Cobalt"<%= document.get("stress_phaseIII").indexOf("Cobalt|") > -1 ? " selected" : "" %>>Cobalt</option>
										<option value="Manganese"<%= document.get("stress_phaseIII").indexOf("Manganese|") > -1 ? " selected" : "" %>>Manganese</option>
										<option value="Biochemical"<%= document.get("stress_phaseIII").indexOf("Biochemical|") > -1 ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_phaseIII").indexOf("Energetic (EMF)|") > -1 ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
								    </table>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">PHASE IV DATE</div>
								<div class="right">
									<input name="dateInputPhaseIV" onfocus="getDate('userMineralRatiosForm','dateInputPhaseIV');select();" value="<%= document.getPhaseIVDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">
								    <table>
									<tr>
									<td>
									<div >Supplements</div>
									</td>
									<td>
									<div >Detox</div>
									</td>
									<td>
									<div >Exercise</div>
									</td>
									<td>
									<div >Diet</div>
									</td>
									<td>
									<div >Stress</div>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_header_phaseIV" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("supplements_header_phaseIV").equals("") ? " selected" : "" %>></option>
										<option value="Digestive Support"<%= document.get("supplements_header_phaseIV").equals("Digestive Support") ? " selected" : "" %>>Digestive Support</option>
										<option value="Adrenal/Thyroid Support"<%= document.get("supplements_header_phaseIV").equals("Adrenal/Thyroid Support") ? " selected" : "" %>>Adrenal/Thyroid Support</option>
										<option value="Thyroid/Adrenal Support"<%= document.get("supplements_header_phaseIV").equals("Thyroid/Adrenal Support") ? " selected" : "" %>>Thyroid/Adrenal Support</option>
										<option value="Adrenal Support"<%= document.get("supplements_header_phaseIV").equals("Adrenal Support") ? " selected" : "" %>>Adrenal Support</option>
										<option value="Thyroid Support"<%= document.get("supplements_header_phaseIV").equals("Thyroid Support") ? " selected" : "" %>>Thyroid Support</option>
										<option value="Heavy Metal Detox"<%= document.get("supplements_header_phaseIV").equals("Heavy Metal Detox") ? " selected" : "" %>>Heavy Metal Detox</option>
										<option value="Copper Toxicity"<%= document.get("supplements_header_phaseIV").equals("Copper Toxicity") ? " selected" : "" %>>Copper Toxicity</option>
										<option value="Copper Detox/Heavy Metals"<%= document.get("supplements_header_phaseIV").equals("Copper Detox/Heavy Metals") ? " selected" : "" %>>Copper Detox/Heavy Metals</option>
										<option value="Maintenance Supplementation"<%= document.get("supplements_header_phaseIV").equals("Maintenance Supplementation") ? " selected" : "" %>>Maintenance Supplementation</option>
									</select>
									</td>
									<td>
									<select name="detox_header_phaseIV" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("detox_header_phaseIV").equals("") ? " selected" : "" %>></option>
										<option value="Liver/GB Flush"<%= document.get("detox_header_phaseIV").equals("Liver/GB Flush") ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_header_phaseIV").equals("SP Cleanse") ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_header_phaseIV").equals("Colon Cleanse") ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_header_phaseIV").equals("Juice Fast") ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_header_phaseIV").equals("Blood Cleanse") ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_header_phaseIV").equals("Homeopathic") ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_header_phaseIV" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("exercise_header_phaseIV").equals("") ? " selected" : "" %>></option>
										<option value="Walking Daily 30 minutes"<%= document.get("exercise_header_phaseIV").equals("Walking Daily 30 minutes") ? " selected" : "" %>>Walking Daily 30 minutes</option>
										<option value="Walking Daily 40-60 minutes"<%= document.get("exercise_header_phaseIV").equals("Walking Daily 40-60 minutes") ? " selected" : "" %>>Walking Daily 40-60 minutes</option>
										<option value="Walking & Strength Training"<%= document.get("exercise_header_phaseIV").equals("Walking & Strength Training") ? " selected" : "" %>>Walking & Strength Training</option>
										<option value="Cardio 30-60 minutes"<%= document.get("exercise_header_phaseIV").equals("Cardio 30-60 minutes") ? " selected" : "" %>>Cardio 30-60 minutes</option>
										<option value="Stretching"<%= document.get("exercise_header_phaseIV").equals("Stretching") ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_header_phaseIV" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("diet_header_phaseIV").equals("") ? " selected" : "" %>></option>
										<option value="Nutrition Plan"<%= document.get("diet_header_phaseIV").equals("Nutrition Plan") ? " selected" : "" %>>Nutrition Plan</option>
									</select>
									</td>
									<td>
									<select name="stress_header_phaseIV" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("stress_header_phaseIV").equals("") ? " selected" : "" %>></option>
										<option value="Emotional"<%= document.get("stress_header_phaseIV").equals("Emotional") ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_header_phaseIV").equals("Mental") ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_header_phaseIV").equals("Physical") ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_header_phaseIV").equals("Spiritual") ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_header_phaseIV").equals("Toxins") ? " selected" : "" %>>Toxins</option>
										<option value="Toxin (Mercury)"<%= document.get("stress_header_phaseIV").equals("Toxin (Mercury)") ? " selected" : "" %>>Toxin (Mercury)</option>
										<option value="Toxin (Copper)"<%= document.get("stress_header_phaseIV").equals("Toxin (Copper)") ? " selected" : "" %>>Toxin (Copper)</option>
										<option value="Toxin (Cadmium)"<%= document.get("stress_header_phaseIV").equals("Toxin (Cadmium)") ? " selected" : "" %>>Toxin (Cadmium)</option>
										<option value="Toxin (Aluminium)"<%= document.get("stress_header_phaseIV").equals("Toxin (Aluminium)") ? " selected" : "" %>>Toxin (Aluminium)</option>
										<option value="Toxin (Nickel)"<%= document.get("stress_header_phaseIV").equals("Toxin (Nickel)") ? " selected" : "" %>>Toxin (Nickel)</option>
										<option value="Toxin (Cobalt)"<%= document.get("stress_header_phaseIV").equals("Toxin (Cobalt)") ? " selected" : "" %>>Toxin (Cobalt)</option>
										<option value="Toxin (Manganese)"<%= document.get("stress_header_phaseIV").equals("Toxin (Manganese)") ? " selected" : "" %>>Toxin (Manganese)</option>
										<option value="Toxin (Lead)"<%= document.get("stress_header_phaseIV").equals("Toxin (Lead)") ? " selected" : "" %>>Toxin (Lead)</option>
										<option value="Biochemical"<%= document.get("stress_header_phaseIV").equals("Biochemical") ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_header_phaseIV").equals("Energetic (EMF)") ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_phaseIV" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Maintenance Supplementation"<%= document.get("supplements_phaseIV").indexOf("Maintenance Supplementation|") > -1 ? " selected" : "" %>>Maintenance Supplementation</option>
										<option value="Repeat Phase I"<%= document.get("supplements_phaseIV").indexOf("Repeat Phase I|") > -1 ? " selected" : "" %>>Repeat Phase I</option>
										<option value="Repeat Phase II"<%= document.get("supplements_phaseIV").indexOf("Repeat Phase II|") > -1 ? " selected" : "" %>>Repeat Phase II</option>
										<option value="Repeat Phase III"<%= document.get("supplements_phaseIV").indexOf("Repeat Phase III|") > -1 ? " selected" : "" %>>Repeat Phase III</option>
										<option value="Homeopathic"<%= document.get("supplements_phaseIV").indexOf("Homeopathic|") > -1 ? " selected" : "" %>>Homeopathic</option>
										<option value="Multizyme"<%= document.get("supplements_phaseIV").indexOf("Multizyme|") > -1 ? " selected" : "" %>>Multizyme</option>
										<option value="Sp Bk Radish"<%= document.get("supplements_phaseIV").indexOf("Sp Bk Radish|") > -1 ? " selected" : "" %>>Sp Bk Radish</option>
										<option value="Zypan"<%= document.get("supplements_phaseIV").indexOf("Zypan|") > -1 ? " selected" : "" %>>Zypan</option>
										<option value="Pancreatrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Pancreatrophin PMG|") > -1 ? " selected" : "" %>>Pancreatrophin PMG</option>
										<option value="Fungal Forte"<%= document.get("supplements_phaseIV").indexOf("Fungal Forte|") > -1 ? " selected" : "" %>>Fungal Forte</option>
										<option value="Multi-Probiotic"<%= document.get("supplements_phaseIV").indexOf("Multi-Probiotic|") > -1 ? " selected" : "" %>>Multi-Probiotic</option>
										<option value="Cholacol"<%= document.get("supplements_phaseIV").indexOf("Cholacol|") > -1 ? " selected" : "" %>>Cholacol</option>
										<option value="Theralac"<%= document.get("supplements_phaseIV").indexOf("Theralac|") > -1 ? " selected" : "" %>>Theralac</option>
										<option value="Lactic Acid Yeast waf"<%= document.get("supplements_phaseIV").indexOf("Lactic Acid Yeast waf|") > -1 ? " selected" : "" %>>Lactic Acid Yeast waf</option>
										<option value="DiGest (liq)"<%= document.get("supplements_phaseIV").indexOf("DiGest (liq)|") > -1 ? " selected" : "" %>>DiGest (liq)</option>
										<option value="DiGest (caps)"<%= document.get("supplements_phaseIV").indexOf("DiGest (caps)|") > -1 ? " selected" : "" %>>DiGest (caps)</option>
										<option value="Zymex"<%= document.get("supplements_phaseIV").indexOf("Zymex|") > -1 ? " selected" : "" %>>Zymex</option>
										<option value="Zymex waf"<%= document.get("supplements_phaseIV").indexOf("Zymex waf|") > -1 ? " selected" : "" %>>Zymex waf</option>
										<option value="Lact-Enz"<%= document.get("supplements_phaseIV").indexOf("Lact-Enz|") > -1 ? " selected" : "" %>>Lact-Enz</option>
										<option value="A-F Betafood"<%= document.get("supplements_phaseIV").indexOf("A-F Betafood|") > -1 ? " selected" : "" %>>A-F Betafood</option>
										<option value="LivCo"<%= document.get("supplements_phaseIV").indexOf("LivCo|") > -1 ? " selected" : "" %>>LivCo</option>
										<option value="DigestPlex (GF)"<%= document.get("supplements_phaseIV").indexOf("DigestPlex (GF|") > -1 ? " selected" : "" %>>DigestPlex (GF)</option>
										<option value="Protein Powder"<%= document.get("supplements_phaseIV").indexOf("Protein Powder|") > -1 ? " selected" : "" %>>Protein Powder</option>
										<option value="Greens"<%= document.get("supplements_phaseIV").indexOf("Greens|") > -1 ? " selected" : "" %>>Greens</option>
										<option value="Cataplex GTF"<%= document.get("supplements_phaseIV").indexOf("Cataplex GTF|") > -1 ? " selected" : "" %>>Cataplex GTF</option>
										<option value="Gymnema"<%= document.get("supplements_phaseIV").indexOf("Gymnema|") > -1 ? " selected" : "" %>>Gymnema</option>
										<option value="Paraplex"<%= document.get("supplements_phaseIV").indexOf("Paraplex|") > -1 ? " selected" : "" %>>Paraplex</option>
										<option value="Inositol"<%= document.get("supplements_phaseIV").indexOf("Inositol|") > -1 ? " selected" : "" %>>Inositol</option>
										<option value="Detox Formula (GF)"<%= document.get("supplements_phaseIV").indexOf("Detox Formula (GF)|") > -1 ? " selected" : "" %>>Detox Formula (GF)</option>
										<option value="PhytoGreens (GF)"<%= document.get("supplements_phaseIV").indexOf("PhytoGreens (GF)|") > -1 ? " selected" : "" %>>PhytoGreens (GF)</option>
										<option value="VitaLiv"<%= document.get("supplements_phaseIV").indexOf("VitaLiv|") > -1 ? " selected" : "" %>>VitaLiv</option>
										<option value="BFood"<%= document.get("supplements_phaseIV").indexOf("BFood|") > -1 ? " selected" : "" %>>BFood</option>
										<option value="Primal Defense"<%= document.get("supplements_phaseIV").indexOf("Primal Defense|") > -1 ? " selected" : "" %>>Primal Defense</option>
										<option value="Chewable Catalyn"<%= document.get("supplements_phaseIV").indexOf("Chewable Catalyn|") > -1 ? " selected" : "" %>>Chewable Catalyn</option>
										<option value="Ferrofood"<%= document.get("supplements_phaseIV").indexOf("Ferrofood|") > -1 ? " selected" : "" %>>Ferrofood</option>
										<option value="Catalyn"<%= document.get("supplements_phaseIV").indexOf("Catalyn|") > -1 ? " selected" : "" %>>Catalyn</option>
										<option value="Boswellia Complex"<%= document.get("supplements_phaseIV").indexOf("Boswellia Complex|") > -1 ? " selected" : "" %>>Boswellia Complex</option>
										<option value="Cranberry Complex"<%= document.get("supplements_phaseIV").indexOf("Cranberry Complex|") > -1 ? " selected" : "" %>>Cranberry Complex</option>
										<option value="Rehmannia Complex"<%= document.get("supplements_phaseIV").indexOf("Rehmannia Complex|") > -1 ? " selected" : "" %>>Rehmannia Complex</option>
										<option value="Withania Complex"<%= document.get("supplements_phaseIV").indexOf("Withania Complex|") > -1 ? " selected" : "" %>>Withania Complex</option>
										<option value="Thyroid Complex"<%= document.get("supplements_phaseIV").indexOf("Thyroid Complex|") > -1 ? " selected" : "" %>>Thyroid Complex</option>
										<option value="Congaplex"<%= document.get("supplements_phaseIV").indexOf("Congaplex|") > -1 ? " selected" : "" %>>Congaplex</option>
										<option value="Cardiotrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Cardiotrophin PMG|") > -1 ? " selected" : "" %>>Cardiotrophin PMG</option>
										<option value="Drenatrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Drenatrophin PMG|") > -1 ? " selected" : "" %>>Drenatrophin PMG</option>
										<option value="Hypothalamus PMG"<%= document.get("supplements_phaseIV").indexOf("Hypothalamus PMG|") > -1 ? " selected" : "" %>>Hypothalamus PMG</option>
										<option value="Mammary PMG"<%= document.get("supplements_phaseIV").indexOf("Mammary PMG|") > -1 ? " selected" : "" %>>Mammary PMG</option>
										<option value="Myotrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Myotrophin PMG|") > -1 ? " selected" : "" %>>Myotrophin PMG</option>
										<option value="Neurotrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Neurotrophin PM|") > -1 ? " selected" : "" %>>Neurotrophin PMG</option>
										<option value="Ostrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Ostrophin PMG|") > -1 ? " selected" : "" %>>Ostrophin PMG</option>
										<option value="Ovatrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Ovatrophin PMG|") > -1 ? " selected" : "" %>>Ovatrophin PMG</option>
										<option value="Pneumotrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Pneumotrophin PMG|") > -1 ? " selected" : "" %>>Pneumotrophin PMG</option>
										<option value="Pituitrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Pituitrophin PMG|") > -1 ? " selected" : "" %>>Pituitrophin PMG</option>
										<option value="Prostate PMG"<%= document.get("supplements_phaseIV").indexOf("Prostate PMG|") > -1 ? " selected" : "" %>>Prostate PMG</option>
										<option value="Renatrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Renatrophin PMG|") > -1 ? " selected" : "" %>>Renatrophin PMG</option>
										<option value="Thymus PMG"<%= document.get("supplements_phaseIV").indexOf("Thymus PMG|") > -1 ? " selected" : "" %>>Thymus PMG</option>
										<option value="Thytrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Thytrophin PM|") > -1 ? " selected" : "" %>>Thytrophin PMG</option>
										<option value="Utrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Utrophin PM|") > -1 ? " selected" : "" %>>Utrophin PMG</option>
										<option value="A-C Carbamide"<%= document.get("supplements_phaseIV").indexOf("A-C Carbamide|") > -1 ? " selected" : "" %>>A-C Carbamide</option>
										<option value="FlavoC"<%= document.get("supplements_phaseIV").indexOf("FlavoC|") > -1 ? " selected" : "" %>>FlavoC</option>
										<option value="Cat A"<%= document.get("supplements_phaseIV").indexOf("Cat A|") > -1 ? " selected" : "" %>>Cat A</option>
										<option value="Cat A-C"<%= document.get("supplements_phaseIV").indexOf("Cat A-C|") > -1 ? " selected" : "" %>>Cat A-C</option>
										<option value="Cat A-C-P"<%= document.get("supplements_phaseIV").indexOf("Cat A-C-P|") > -1 ? " selected" : "" %>>Cat A-C-P</option>
										<option value="Cat B"<%= document.get("supplements_phaseIV").indexOf("Cat B|") > -1 ? " selected" : "" %>>Cat B</option>
										<option value="Cat C"<%= document.get("supplements_phaseIV").indexOf("Cat C|") > -1 ? " selected" : "" %>>Cat C</option>
										<option value="Cat D"<%= document.get("supplements_phaseIV").indexOf("Cat D|") > -1 ? " selected" : "" %>>Cat D</option>
										<option value="Cat E"<%= document.get("supplements_phaseIV").indexOf("Cat E|") > -1 ? " selected" : "" %>>Cat E</option>
										<option value="Cataplex F"<%= document.get("supplements_phaseIV").indexOf("Cataplex F|") > -1 ? " selected" : "" %>>Cat F</option>
										<option value="Cataplex G"<%= document.get("supplements_phaseIV").indexOf("Cataplex G|") > -1 ? " selected" : "" %>>Cataplex G</option>
										<option value="CalMag"<%= document.get("supplements_phaseIV").indexOf("CalMag|") > -1 ? " selected" : "" %>>CalMag</option>
										<option value="Cal Lact Powder"<%= document.get("supplements_phaseIV").indexOf("Cal Lact Powder|") > -1 ? " selected" : "" %>>Cal Lact Powder</option>
										<option value="Magnesium Lactate"<%= document.get("supplements_phaseIV").indexOf("Magnesium Lactate|") > -1 ? " selected" : "" %>>Magnesium Lactate</option>
										<option value="Manganese B12"<%= document.get("supplements_phaseIV").indexOf("Manganese B12|") > -1 ? " selected" : "" %>>Manganese B12</option>
										<option value="Liquid Herbs"<%= document.get("supplements_phaseIV").indexOf("Liquid Herbs|") > -1 ? " selected" : "" %>>Liquid Herbs</option>
										<option value="CardioPlus"<%= document.get("supplements_phaseIV").indexOf("CardioPlus|") > -1 ? " selected" : "" %>>CardioPlus</option>
										<option value="Adrenal Des"<%= document.get("supplements_phaseIV").indexOf("Adrenal Des|") > -1 ? " selected" : "" %>>Adrenal Des</option>
										<option value="Spleen Des"<%= document.get("supplements_phaseIV").indexOf("Spleen Des|") > -1 ? " selected" : "" %>>Spleen Des</option>
										<option value="Drenamin"<%= document.get("supplements_phaseIV").indexOf("Drenamin|") > -1 ? " selected" : "" %>>Drenamin</option>
										<option value="Fish Oil"<%= document.get("supplements_phaseIV").indexOf("Fish Oil|") > -1 ? " selected" : "" %>>Fish Oil</option>
										<option value="OBM"<%= document.get("supplements_phaseIV").indexOf("OBM|") > -1 ? " selected" : "" %>>OBM</option>
										<option value="Prolamine Iodine"<%= document.get("supplements_phaseIV").indexOf("Prolamine Iodine|") > -1 ? " selected" : "" %>>Prolamine Iodine</option>
										<option value="Parotid"<%= document.get("supplements_phaseIV").indexOf("Parotid|") > -1 ? " selected" : "" %>>Parotid</option>
										<option value="Cholacol II"<%= document.get("supplements_phaseIV").indexOf("Cholacol II|") > -1 ? " selected" : "" %>>Cholacol II</option>
										<option value="Cholaplex"<%= document.get("supplements_phaseIV").indexOf("Cholaplex|") > -1 ? " selected" : "" %>>Cholaplex</option>
										<option value="Zinc Liver Chelate"<%= document.get("supplements_phaseIV").indexOf("Zinc Liver Chelate|") > -1 ? " selected" : "" %>>Zinc Liver Chelate</option>
										<option value="Protefood"<%= document.get("supplements_phaseIV").indexOf("Protefood|") > -1 ? " selected" : "" %>>Protefood</option>
										<option value="MolyCu"<%= document.get("supplements_phaseIV").indexOf("MolyCu|") > -1 ? " selected" : "" %>>MolyCu</option>
										<option value="GB3"<%= document.get("supplements_phaseIV").indexOf("GB3|") > -1 ? " selected" : "" %>>GB3</option>
										<option value="EndoPan"<%= document.get("supplements_phaseIV").indexOf("EndoPan|") > -1 ? " selected" : "" %>>EndoPan</option>
										<option value="Flaxseed oil"<%= document.get("supplements_phaseIV").indexOf("Flaxseed oil|") > -1 ? " selected" : "" %>>Flaxseed oil</option>
										<option value="Arginex"<%= document.get("supplements_phaseIV").indexOf("Arginex|") > -1 ? " selected" : "" %>>Arginex</option>
										<option value="Rehmannia (liq)"<%= document.get("supplements_phaseIV").indexOf("Rehmannia (liq)|") > -1 ? " selected" : "" %>>Rehmannia (liq)</option>
										<option value="Saligesic"<%= document.get("supplements_phaseIV").indexOf("Saligesic|") > -1 ? " selected" : "" %>>Saligesic</option>
										<option value="Vitanox"<%= document.get("supplements_phaseIV").indexOf("Vitanox|") > -1 ? " selected" : "" %>>Vitanox</option>
										<option value="St. John's Wort"<%= document.get("supplements_phaseIV").indexOf("St. John's Wort|") > -1 ? " selected" : "" %>>St. John's Wort</option>
										<option value="California Poppy"<%= document.get("supplements_phaseIV").indexOf("California Poppy|") > -1 ? " selected" : "" %>>California Poppy</option>
										<option value="Broncafect"<%= document.get("supplements_phaseIV").indexOf("Broncafect|") > -1 ? " selected" : "" %>>Broncafect</option>
										<option value="Min-Tran"<%= document.get("supplements_phaseIV").indexOf("Min-Tran|") > -1 ? " selected" : "" %>>Min-Tran</option>
										<option value="Min-Chex"<%= document.get("supplements_phaseIV").indexOf("Min-Chex|") > -1 ? " selected" : "" %>>Min-Chex</option>
										<option value="Progon B"<%= document.get("supplements_phaseIV").indexOf("Progon B|") > -1 ? " selected" : "" %>>Progon B</option>
										<option value="Tribulus"<%= document.get("supplements_phaseIV").indexOf("Tribulus|") > -1 ? " selected" : "" %>>Tribulus</option>
										<option value="Wheat Germ oil"<%= document.get("supplements_phaseIV").indexOf("Wheat Germ oil|") > -1 ? " selected" : "" %>>Wheat Germ oil</option>
										<option value="Symplex F"<%= document.get("supplements_phaseIV").indexOf("Symplex F|") > -1 ? " selected" : "" %>>Symplex F</option>
										<option value="Glucosamine Synergy"<%= document.get("supplements_phaseIV").indexOf("Glucosamine Synergy|") > -1 ? " selected" : "" %>>Glucosamine Synergy</option>
										<option value="Ligaplex II"<%= document.get("supplements_phaseIV").indexOf("Ligaplex II|") > -1 ? " selected" : "" %>>Ligaplex II</option>
										<option value="Collagen C"<%= document.get("supplements_phaseIV").indexOf("Collagen C|") > -1 ? " selected" : "" %>>Collagen C</option>
										<option value="Symplex M"<%= document.get("supplements_phaseIV").indexOf("Symplex M|") > -1 ? " selected" : "" %>>Symplex M</option>
										<option value="Male Support"<%= document.get("supplements_phaseIV").indexOf("Male Support|") > -1 ? " selected" : "" %>>Male Support</option>
										<option value="Orchex"<%= document.get("supplements_phaseIV").indexOf("Orchex|") > -1 ? " selected" : "" %>>Orchex</option>
										<option value="Lipoic Acid"<%= document.get("supplements_phaseIV").indexOf("Lipoic Acid|") > -1 ? " selected" : "" %>>Lipoic Acid</option>
										<option value="Ambrotose"<%= document.get("supplements_phaseIV").indexOf("Ambrotose|") > -1 ? " selected" : "" %>>Ambrotose</option>
									</select>
									</td>
									<td>
									<select name="detox_phaseIV" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Liver/GB Flush"<%= document.get("detox_phaseIV").indexOf("Liver/GB Flush|") > -1 ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_phaseIV").indexOf("SP Cleanse|") > -1 ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_phaseIV").indexOf("Colon Cleanse|") > -1 ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_phaseIV").indexOf("Juice Fast|") > -1 ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_phaseIV").indexOf("Blood Cleanse|") > -1 ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_phaseIV").indexOf("Homeopathic|") > -1 ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_phaseIV" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Walk Daily 30 min"<%= document.get("exercise_phaseIV").indexOf("Walk Daily 30 min|") > -1 ? " selected" : "" %>>Walk Daily 30 min</option>
										<option value="Walk Daily 40-60 min"<%= document.get("exercise_phaseIV").indexOf("Walk Daily 40-60 min|") > -1 ? " selected" : "" %>>Walk Daily 40-60 min</option>
										<option value="Walk/Strength Train"<%= document.get("exercise_phaseIV").indexOf("Walk/Strength Train|") > -1 ? " selected" : "" %>>Walk/Strength Train</option>
										<option value="Cardio 30-60 min"<%= document.get("exercise_phaseIV").indexOf("Cardio 30-60 min|") > -1 ? " selected" : "" %>>Cardio 30-60 min</option>
										<option value="Stretching"<%= document.get("exercise_phaseIV").indexOf("Stretching|") > -1 ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_phaseIV" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="No sugar/fruit"<%= document.get("diet_phaseIV").indexOf("No sugar/fruit|") > -1 ? " selected" : "" %>>No sugar/fruit</option>
										<option value="Protein Shakes"<%= document.get("diet_phaseIV").indexOf("Protein Shakes|") > -1 ? " selected" : "" %>>Protein Shakes</option>
										<option value="Protein Shakes w/greens"<%= document.get("diet_phaseIV").indexOf("Protein Shakes w/greens|") > -1 ? " selected" : "" %>>Protein Shakes w/greens</option>
										<option value="Protein Shakes w/flax oil"<%= document.get("diet_phaseIV").indexOf("Protein Shakes w/flax oil|") > -1 ? " selected" : "" %>>Protein Shakes w/flax oil</option>
										<option value="Flax seed oil"<%= document.get("diet_phaseIV").indexOf("Flax seed oil|") > -1 ? " selected" : "" %>>Flax seed oil</option>
										<option value="Garlic"<%= document.get("diet_phaseIV").indexOf("Garlic|") > -1 ? " selected" : "" %>>Garlic</option>
										<option value="Water Testing"<%= document.get("diet_phaseIV").indexOf("Water Testing|") > -1 ? " selected" : "" %>>Water Testing</option>
									</select>
									</td>
									<td>
									<select name="stress_phaseIV" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Emotional"<%= document.get("stress_phaseIV").indexOf("Emotional|") > -1 ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_phaseIV").indexOf("Mental|") > -1 ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_phaseIV").indexOf("Physical|") > -1 ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_phaseIV").indexOf("Spiritual|") > -1 ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_phaseIV").indexOf("Toxins|") > -1 ? " selected" : "" %>>Toxins</option>
										<option value="Lead"<%= document.get("stress_phaseIV").indexOf("Lead|") > -1 ? " selected" : "" %>>Lead</option>
										<option value="Mercury"<%= document.get("stress_phaseIV").indexOf("Mercury|") > -1 ? " selected" : "" %>>Mercury</option>
										<option value="Cadmium"<%= document.get("stress_phaseIV").indexOf("Cadmium|") > -1 ? " selected" : "" %>>Cadmium</option>
										<option value="Arsenic"<%= document.get("stress_phaseIV").indexOf("Arsenic|") > -1 ? " selected" : "" %>>Arsenic</option>
										<option value="Aluminium"<%= document.get("stress_phaseIV").indexOf("Aluminium|") > -1 ? " selected" : "" %>>Aluminium</option>
										<option value="Copper"<%= document.get("stress_phaseIV").indexOf("Copper|") > -1 ? " selected" : "" %>>Copper</option>
										<option value="Nickel"<%= document.get("stress_phaseIV").indexOf("Nickel|") > -1 ? " selected" : "" %>>Nickel</option>
										<option value="Zinc"<%= document.get("stress_phaseIV").indexOf("Zinc|") > -1 ? " selected" : "" %>>Zinc</option>
										<option value="Cobalt"<%= document.get("stress_phaseIV").indexOf("Cobalt|") > -1 ? " selected" : "" %>>Cobalt</option>
										<option value="Manganese"<%= document.get("stress_phaseIV").indexOf("Manganese|") > -1 ? " selected" : "" %>>Manganese</option>
										<option value="Biochemical"<%= document.get("stress_phaseIV").indexOf("Biochemical|") > -1 ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_phaseIV").indexOf("Energetic (EMF)|") > -1 ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
								    </table>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input class="formbutton" type="submit" name="submit_button" value="Add&nbsp;&#47;&nbsp;Update" alt="Add&nbsp;&#47;&nbsp;Update" style="margin-right: 4px;"/><input type="hidden" name="delete_id" value="0" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"><input name="new_template" onfocus="select();" value="" size="7" maxlength="100" class="inputbox" style="width: 124px; margin-right: 4px;" /></div>
								<div class="right">
									<input class="formbutton" type="submit" name="submit_button" value="Save As Template" alt="Add&nbsp;&#47;&nbsp;Update" style="margin-right: 4px;"/><input type="hidden" name="delete_id" value="0" />
								</div>
								<div class="end"></div>
							</div>

						</struts-html:form>

						<div class="content_AdministrationTable">

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  ">Mineral Ratios Client Documents</td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator document_itr = MineralRatiosClientDocumentBean.getMineralRatiosClientDocuments(adminPerson).iterator();
    if (document_itr.hasNext())
    {
	while (document_itr.hasNext())
	{
	    MineralRatiosClientDocumentBean document_obj = (MineralRatiosClientDocumentBean)document_itr.next();
%>
							
							<div class="audience">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="admin-user-mineral-ratios.jsp?id=<%= document_obj.getId() %>" title=""><%= document_obj.getLabel() %></a></td>
										<td style="width:  70px; text-align: center;  "><a href="admin-user-mineral-ratios-report.jsp?id=<%= document_obj.getId() %>">Reports</a>&nbsp;<a href="javascript:if (confirm('Are you sure?')) {document.forms[1].delete_id.value=<%= document_obj.getId() %>;document.forms[1].submit();}" title="Delete">Delete</a></td>
									</tr>
								</table>
							</div>
							
<%
	}
    }
    else
    {
%>
							
							<div class="audience">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colspan="3">No Documents Found</td>
									</tr>
								</table>
							</div>
							
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						</div>

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
