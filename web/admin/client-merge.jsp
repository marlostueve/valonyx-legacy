<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, com.badiyan.torque.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qb.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="adminPracticeArea" class="com.badiyan.uk.online.beans.PracticeAreaBean" scope="session" />
<jsp:useBean id="adminStatement" class="com.badiyan.torque.SoapStatement" scope="session" />
<jsp:useBean id="soap_button" class="java.lang.String" scope="session" />

<%
CompanySettingsBean settings = adminCompany.getSettings();



String search_str = (String)session.getAttribute("invSearchStr");
if (search_str == null)
	search_str = "";

String vendor_search_str = (String)session.getAttribute("vendorSearchStr");
if (vendor_search_str == null)
	vendor_search_str = "";

String soap_button_str = (String)session.getAttribute("soap_button");
if (soap_button_str == null || soap_button_str.equals(""))
{
	soap_button_str = "Button radio1";
	session.setAttribute("soap_button", soap_button_str);
}

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

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Valonyx</title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
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
	
	var addedChild = '';
	var rootLevel = '<%= (adminStatement.getParentId() == 0) ? "true" : "false" %>';
	
	var client_select = 'c1_clientSelect';
	
	var merge_message = 'Merge?  Are you sure?';
	var merge_label_1;
	var merge_label_2;
	
	var client_id_1;
	var client_id_2;

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
				if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
					showPeople(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
					showPerson(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("codes").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("codes")[0];
					showCodes(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("statements").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("statements")[0];
					showStatements(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("purchase_order_report_url").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("purchase_order_report_url")[0];
					document.getElementById('cos_div').innerHTML = '<EMBED SRC="' + xml_response.childNodes[0].nodeValue + '" WIDTH="100%" HEIGHT="100%"></EMBED>';
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
		document.getElementById(client_select).options.length = 0;

		var index = 0;
		while (xml_str.getElementsByTagName("person")[index] != null)
		{
			key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
			value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
			eval("var personArray = " + value);

			document.getElementById(client_select).options[index] = new Option(personArray["label"],key);

			index++;
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
		
		if (client_select == 'c1_clientSelect')
		{
			merge_label_1 = personArray["file"] + ' : ' + personArray["label"];
			client_id_1 = personArray["id"];
		}
		else
		{
			merge_label_2 = personArray["file"] + ' : ' + personArray["label"];
			client_id_2 = personArray["id"];
		}
		
		merge_message = 'Merge ' + merge_label_1 + ' to ' + merge_label_2 + '?  Are you sure?';

		document.getElementById(client_select + '_label').firstChild.nodeValue = personArray["file"] + ' : ' + personArray["label"];
		document.getElementById(client_select + '_qb').firstChild.nodeValue = personArray["qb_id"];
		
		document.getElementById(client_select + '_email').firstChild.nodeValue = personArray["email"];
		document.getElementById(client_select + '_phone').firstChild.nodeValue = personArray["phone"];
		document.getElementById(client_select + '_cell').firstChild.nodeValue = personArray["cell"];
		document.getElementById(client_select + '_address1').firstChild.nodeValue = personArray["addr1"];
		
		/*

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
		*/
    }

    function showCodes(xml_str)
    {
		codeArray = new Array();

        //document.forms[0].codeSelect.options.length = 0;

		deleteAllRows(document.getElementById('statement-table'));

        var index = 0;
        while (xml_str.getElementsByTagName("code")[index] != null)
        {
            key = xml_str.getElementsByTagName("code")[index].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("code")[index].childNodes[1].childNodes[0].nodeValue;
            eval("var codeArr = " + value);

			codeArray[key] = codeArr;

			//document.forms[0].codeSelect.options[index] = new Option(codeArr["desc"],key);

			addItemRow(key, codeArr["desc"], codeArr["type"], codeArr["pa"]);

			index++;
        }
    }

    function showStatements(xml_str)
    {
		codeArray = new Array();

        //document.forms[0].codeSelect.options.length = 0;

		deleteAllRows(document.getElementById('statement-table'));

        var index = 0;
		
		//alert(xml_str.getAttribute("parent"));
		//alert(xml_str.getAttribute("parent_id"));
		
		rootLevel = xml_str.getAttribute("root");
		
		//alert(xml_str.getAttribute("parent"));
		if (xml_str.getAttribute("parent"))
			document.forms[0].nameInput.value = xml_str.getAttribute("parent");
		else
			document.forms[0].nameInput.value = '';
		
		while (xml_str.getElementsByTagName("item")[index] != null)
		{
			key = xml_str.getElementsByTagName("item")[index].childNodes[0].childNodes[0].nodeValue;
			value = xml_str.getElementsByTagName("item")[index].childNodes[1].childNodes[0].nodeValue;
			eval("var codeArr = " + value);

			codeArray[key] = codeArr;

			//document.forms[0].codeSelect.options[index] = new Option(codeArr["desc"],key);

			addItemRow(key, codeArr["label"]);

			index++;
		}
		
		if (index == 0)
			addItemRow('', 'No Statements Found');
		
		addChildRow();
			
    }

    var last_code;
	var qty = 0;

    Number.prototype.formatMoney = function(c, d, t){
	var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    };

	function addItemRow(code_id, code, type, pa)
	{
		var tbl = document.getElementById('statement-table');
		var nextRow = tbl.tBodies[0].rows.length;
		var iteration = nextRow + 1;

		num = nextRow;

		// add the row
		var row = tbl.tBodies[0].insertRow(num);
		row.style.height = '18px';
		
		if (addedChild == code)
		{
			row.style.backgroundColor = 'yellow';
			addedChild = '';
		}
		
		var textNode;
		var cell0;

		// CONFIG: requires classes named classy0 and classy1
		//row.className = 'classy' + (iteration % 2);

		var cellx = row.insertCell(0);
		textNode = document.createTextNode('');
		cellx.style.width = '5px';
		cellx.appendChild(textNode);

		if (code == 'No Statements Found')
		{
			// cell 0 - text
			cell0 = row.insertCell(1);
			textNode = document.createTextNode(code);
			cell0.style.textAlign = 'left';
			cell0.colSpan = '4';
			cell0.appendChild(textNode);
			//tbl.tBodies[0].rows[0].cells[0].style.width = 518;
			return;
		}

		// CONFIG: This whole section can be configured

		// cell 0 - text
		cell0 = row.insertCell(1);
		cell0.style.verticalAlign = 'top';
		cell0.style.width = '282px';
		var anchor = document.createElement('a');
		//url = "schedule-administration-checkout-code.jsp?id=" + code_id;
		anchor.setAttribute("href", "javascript: selectChild(" + code_id + ");");
		textNode = document.createTextNode(code);
		anchor.appendChild(textNode);
		cell0.appendChild(anchor);

		// cell 1 - text
		var cell1 = row.insertCell(2);
		textNode = document.createTextNode(' ');
		cell1.style.verticalAlign = 'top';
		cell1.style.textAlign = 'right';
		cell1.style.width = '70px';
		cell1.appendChild(textNode);

		// cell 2 - text
		var cell2 = row.insertCell(3);
		//textNode = document.createTextNode(' ');
		cell2.style.textAlign = 'right';
		cell2.style.width = '100px';
		//cell2.appendChild(textNode);
		
		anchor = document.createElement('a');
		//url = "schedule-administration-checkout-code.jsp?id=" + code_id;
		anchor.setAttribute("href", "javascript: deleteChild(" + code_id + ",'" + code + "');");
		textNode = document.createTextNode('Delete');
		anchor.appendChild(textNode);
		cell2.appendChild(anchor);

		// cell 3 - text
		var cell3 = row.insertCell(4);
		textNode = document.createTextNode(' ');
		cell3.style.textAlign = 'right';
		cell3.style.width = '40px';
		cell3.appendChild(textNode);
	}

	function addChildRow()
	{
		var tbl = document.getElementById('statement-table');
		var nextRow = tbl.tBodies[0].rows.length;
		var iteration = nextRow + 1;

		num = nextRow;

		// add the row
		var row = tbl.tBodies[0].insertRow(num);
		row.style.height = '18px';
		var textNode;

		// CONFIG: requires classes named classy0 and classy1
		//row.className = 'classy' + (iteration % 2);

		var cellx = row.insertCell(0);
		textNode = document.createTextNode('');
		cellx.style.width = '5px';
		cellx.appendChild(textNode);

		// CONFIG: This whole section can be configured

		// cell 0 - text
		var cell0 = row.insertCell(1);
		cell0.style.verticalAlign = 'top';
		cell0.style.width = '282px';
		//alert('1');
		//var childInput = document.createElement("<input name='childInput' onfocus='select();' maxlength='150' class='inputbox' style='width: 164px;' />");
		var childInput = document.createElement('input');
		//alert('2');
		childInput.setAttribute("name", "childInput");
		childInput.setAttribute("type", "text");
		childInput.setAttribute("onfocus", "select();");
		childInput.setAttribute("maxlength", "75");
		childInput.setAttribute("class", "inputbox");
		childInput.setAttribute("style", "width: 164px;");
		cell0.appendChild(childInput);
		
		var inputButton = document.createElement('input');
		inputButton.setAttribute("type", "button");
		inputButton.setAttribute("name", "submit_button6");
		//inputButton.setAttribute("id", "submitbutton6");
		//inputButton.setAttribute("class", "formbutton");
		inputButton.setAttribute("style", "margin-left: 5px; ");
		
		if (rootLevel == 'true')
		{
			inputButton.setAttribute("onclick", "addRoot();");
			inputButton.setAttribute("value", " Add Root ");
			inputButton.setAttribute("alt", " Add Root ");
		}
		else
		{
			inputButton.setAttribute("onclick", "addChild();");
			inputButton.setAttribute("value", " Add Child ");
			inputButton.setAttribute("alt", " Add Child ");
		}
		
		cell0.appendChild(inputButton);

		// cell 1 - text
		var cell1 = row.insertCell(2);
		textNode = document.createTextNode(' ');
		cell1.style.verticalAlign = 'top';
		cell1.style.textAlign = 'right';
		cell1.style.width = '70px';
		cell1.appendChild(textNode);

		// cell 2 - text
		var cell2 = row.insertCell(3);
		textNode = document.createTextNode(' ');
		cell2.style.textAlign = 'right';
		cell2.style.width = '100px';
		cell2.appendChild(textNode);

		// cell 3 - text
		var cell3 = row.insertCell(4);
		textNode = document.createTextNode(' ');
		cell3.style.textAlign = 'right';
		cell3.style.width = '40px';
		cell3.appendChild(textNode);
	}
	
	function selectChild(child_id)
	{
		//alert('select child invoked >' + child_id);
		processCommand('selectStatement', child_id);
	}
	
	function deleteChild(child_id, label)
	{
		if (confirm('Delete ' + label + '?'))
			processCommand('deleteStatement', child_id);
	}

	function deleteAllRows(tbl)
	{
		for (var i = tbl.tBodies[0].rows.length; i > 0; i--)
			tbl.tBodies[0].rows[i - 1].parentNode.deleteRow(i - 1);
	}

	function showDepartments(xml_str)
	{
		deleteAllRows(document.getElementById('statement-table'));
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
		processCommand('showAll');
	}

	function update()
	{
		//processCommand('updateSoap', document.forms[0].nameInput.value);
	
		if (confirm(merge_message))
			processCommand('mergeClients', client_id_1 + "|" + client_id_2);
	}

	function goBack()
	{
		processCommand('back');
	}
	
	function addRoot()
	{
		addedChild = document.forms[0].childInput.value;
		processCommand('addRootStatement', document.forms[0].childInput.value);
	}
	
	function addChild()
	{
		addedChild = document.forms[0].childInput.value;
		processCommand('addChildStatement', document.forms[0].childInput.value);
	}

    function selectPerson()
    {
		//getPersonDetails

		//alert(document.getElementById(client_select).options[document.getElementById(client_select).selectedIndex].value);
		//document.getElementById('clientSelect').options.length = 0;

		//unselectPlan();
		processCommand('getPersonDetails', document.getElementById(client_select).options[document.getElementById(client_select).selectedIndex].value);
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

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup3", function () {

            // Create a Button using an existing <input> element as a data source

            var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", {onclick: {fn: update}});

        });

    } ();

</script>

<script type="text/javascript">

    (function () {

		var ButtonGroup = YAHOO.widget.ButtonGroup;


        // "checkedButtonChange" event handler for each ButtonGroup instance

        var onCheckedButtonChange = function (p_oEvent) {
			processCommand('showSOAPRoot', p_oEvent.newValue);
        };


        // "contentready" event handler for the "radiobuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("radiobuttonsfrommarkup", function () {

            var oButtonGroup1 = new ButtonGroup("buttongroup1");
            oButtonGroup1.on("checkedButtonChange", onCheckedButtonChange);


        });

    }());

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
<body class="yui-skin-sam" onload="javascript:initErrors();">

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
	    <p class="headline"><%= adminCompany.getLabel() %> Administration - Client Merge</p>
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
				<div class="hd">Purchase Order</div>
				<div class="bd">
					<div style="width: 100%;">
						<div id="cos_div">
						</div>
					</div>
				</div>
				<div class="ft"></div>
			</div>

		    <div class="adminItem">This tool is used to merge data to eliminate duplicate clients.</div>

		    <div class="adminItem">Search for and select a <strong style="font-weight: bolder;">SOURCE CLIENT</strong>.  This is the client that you're moving data from.  Then search for and select a <strong style="font-weight: bolder;">DESTINATION CLIENT</strong>.  This is the client that you're moving data to.</div>

		    <div class="adminItem">Data that's connected to the source client, like appointments and orders, will be moved to the destination client.  The source client will then be removed.</div>

			<div class="adminItem">
				<div class="leftTM">CLIENT</div>
				<div class="right">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><strong style="font-weight: bolder; color: blue; ">SOURCE CLIENT</strong><br />
								<strong>Search Last Name:</strong><br />
								<input style="width: 200px;" id="c1_lastname" type="textbox" onkeyup="if (document.getElementById('c1_lastname').value.length > 0) {client_select = 'c1_clientSelect'; processCommand('getPeopleByLastName', document.getElementById('c1_lastname').value.replace(/\'/g,'\\\''));}" name="c1_lastname" /><br />
								<strong>Search First / File Number:</strong><br />
								<input style="width: 120px;" id="c1_firstname" type="textbox" onkeyup="if (document.getElementById('c1_firstname').value.length > 0) {client_select = 'c1_clientSelect'; processCommand('getPeopleByFirstName', document.getElementById('c1_firstname').value);}" name="c1_firstname" />&nbsp;/&nbsp;<input id="c1_filenumber" type="textbox" onkeyup="if (document.getElementById('c1_filenumber').value.length > 0) {client_select = 'c1_clientSelect'; processCommand('getPeopleByFileNumber', document.getElementById('c1_filenumber').value);}" name="c1_filenumber" style="width: 68px;" />

								<div class="clear"></div>

								<select size="6" id="c1_clientSelect" name="clientSelect" multiple="false" style="width: 200px;"  onclick="client_select = 'c1_clientSelect'; selectPerson();" >
								<option value="-1">-- SEARCH FOR A CLIENT --</option>
								</select>
							</td>
							<td>
								&nbsp;
							</td>
							<td><strong style="font-weight: bolder; color: blue;">DESTINATION CLIENT</strong><br />
								<strong>Search Last Name:</strong><br />
								<input style="width: 200px;" id="c2_lastname" type="textbox" onkeyup="if (document.getElementById('c2_lastname').value.length > 0) {client_select = 'c2_clientSelect'; processCommand('getPeopleByLastName', document.getElementById('c2_lastname').value.replace(/\'/g,'\\\''));}" name="c2_lastname" /><br />
								<strong>Search First / File Number:</strong><br />
								<input style="width: 120px;" id="c2_firstname" type="textbox" onkeyup="if (document.getElementById('c2_firstname').value.length > 0) {client_select = 'c2_clientSelect'; processCommand('getPeopleByFirstName', document.getElementById('c2_firstname').value);}" name="c2_firstname" />&nbsp;/&nbsp;<input id="c2_filenumber" type="textbox" onkeyup="if (document.getElementById('c2_filenumber').value.length > 0) {client_select = 'c2_clientSelect'; processCommand('getPeopleByFileNumber', document.getElementById('c2_filenumber').value);}" name="c2_filenumber" style="width: 68px;" />

								<div class="clear"></div>

								<select size="6" id="c2_clientSelect" name="clientSelect" multiple="false" style="width: 200px;"  onclick="client_select = 'c2_clientSelect'; selectPerson();" >
								<option value="-1">-- SEARCH FOR A CLIENT --</option>
								</select>
							</td>
						</tr>
					</table>
				</div>
				<div class="end"></div>
			</div>
			
			<div class="adminItem">
				<div class="leftTM">SELECTED</div>
				<div class="right">
					<table border="0" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c1_clientSelect_label">&nbsp;</span></strong>
							</td>
							<td>
								&nbsp;
							</td>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c2_clientSelect_label">&nbsp;</span></strong>
							</td>
						</tr>
					</table>
				</div>
				<div class="end"></div>
			</div>
			
			<div class="adminItem">
				<div class="leftTM">QBFS ID</div>
				<div class="right">
					<table border="0" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c1_clientSelect_qb">&nbsp;</span></strong>
							</td>
							<td>
								&nbsp;
							</td>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c2_clientSelect_qb">&nbsp;</span></strong>
							</td>
						</tr>
					</table>
				</div>
				<div class="end"></div>
			</div>
			
			<div class="adminItem">
				<div class="leftTM">CELL</div>
				<div class="right">
					<table border="0" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c1_clientSelect_cell">&nbsp;</span></strong>
							</td>
							<td>
								&nbsp;
							</td>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c2_clientSelect_cell">&nbsp;</span></strong>
							</td>
						</tr>
					</table>
				</div>
				<div class="end"></div>
			</div>
			
			<div class="adminItem">
				<div class="leftTM">PHONE</div>
				<div class="right">
					<table border="0" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c1_clientSelect_phone">&nbsp;</span></strong>
							</td>
							<td>
								&nbsp;
							</td>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c2_clientSelect_phone">&nbsp;</span></strong>
							</td>
						</tr>
					</table>
				</div>
				<div class="end"></div>
			</div>
			
			<div class="adminItem">
				<div class="leftTM">EMAIL</div>
				<div class="right">
					<table border="0" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c1_clientSelect_email">&nbsp;</span></strong>
							</td>
							<td>
								&nbsp;
							</td>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c2_clientSelect_email">&nbsp;</span></strong>
							</td>
						</tr>
					</table>
				</div>
				<div class="end"></div>
			</div>
			
			<div class="adminItem">
				<div class="leftTM">ADDRESS</div>
				<div class="right">
					<table border="0" cellpadding="0" cellspacing="0" width="100%">
						<tr>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c1_clientSelect_address1">&nbsp;</span></strong>
							</td>
							<td>
								&nbsp;
							</td>
							<td width="50%">
								<strong style="font-weight: bolder; color: blue; "><span id="c2_clientSelect_address1">&nbsp;</span></strong>
							</td>
						</tr>
					</table>
				</div>
				<div class="end"></div>
			</div>

		    <div class="adminItem">Review the selected information to ensure that it's describing the same client, then click <strong style="font-weight: bolder; ">Merge</strong></div>


		    <div class="adminItem">
			    <div class="leftTM"></div>
			    <div id="submitbuttonsfrommarkup3" class="right">
					<table border="0" width="100%"><tr><td align="center" >
					<input id="submitbutton4" class="formbutton" type="button" name="submit_button4" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Merge&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" alt="Merge" style="margin-right: 10px; "/>
					</td></tr></table>
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
