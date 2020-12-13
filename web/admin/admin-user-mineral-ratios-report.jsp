<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, com.badiyan.torque.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qb.data.*, com.badiyan.uk.online.PDF.*" %>

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
	session.setAttribute("document", document);
	
	//MineralRatiosWorksheetBuilder.generateMineralRatiosWorksheet(document);
    }
    catch (Exception x)
    {
	x.printStackTrace();
    }
}


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
						<p><a href="../resources/pdf/<%= MineralRatiosWorksheetBuilder.generateMineralRatiosWorksheet(document) %>" target="_blank">Mineral Ratios Client Document</a></p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

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
