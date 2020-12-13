<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, com.badiyan.torque.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qb.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />



<%
CompanySettingsBean settings = adminCompany.getSettings();

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

    function processCommand(command, parameter)
    {
        if (window.ActiveXObject)
            httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
        else if (window.XMLHttpRequest)
            httpRequest = new XMLHttpRequest();

        httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain") %>/InventoryServlet.html', true);
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
				if (httpRequest.responseXML.getElementsByTagName("codes").length > 0)
				{

					xml_response = httpRequest.responseXML.getElementsByTagName("codes")[0];
					showCodes(xml_response);
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

			addItemRow(key, codeArr["desc"], codeArr["exp"], codeArr["count"], codeArr["diff"]);

			index++;
        }
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

	function addItemRow(code_id, code, expected, counted, difference)
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
		cellx.style.width = '5px';
		cellx.appendChild(textNode);

		// cell 0 - text
		cell0 = row.insertCell(1);
		cell0.style.verticalAlign = 'top';
		cell0.style.width = '282px';
		var anchor = document.createElement('a');
		url = "schedule-administration-checkout-code.jsp?id=" + code_id;
		anchor.setAttribute("href",url);
		textNode = document.createTextNode(code);
		anchor.appendChild(textNode);
		cell0.appendChild(anchor);

		// cell 1 - text
		var cell1 = row.insertCell(2);
		textNode = document.createTextNode(expected);
		cell1.style.verticalAlign = 'top';
		cell1.style.textAlign = 'right';
		cell1.style.width = '70px';
		cell1.appendChild(textNode);

		
		// cell 2 - input text
		var cell2 = row.insertCell(3);
		cell2.style.verticalAlign = 'top';
		var textInp = document.createElement('input');
		textInp.setAttribute('type', 'text');
		textInp.setAttribute('name', 'countedInput' + iteration);
		//textInp.setAttribute('size', '2');
		textInp.setAttribute('value', counted);
		textInp.setAttribute('style', 'width: 44px; text-align: right;');
		textInp.setAttribute('onfocus', 'select();');
		textInp.setAttribute('onkeyup', 'processCommand(\'recordItemCount\', ' + code_id + ' + \'|\' + this.value);');
		cell2.style.textAlign = 'right';
		cell2.style.width = '70px';
		//textInp.onclick = function () {previousOrderClicked(this, tname)};
		cell2.appendChild(textInp);
		

		// cell 2 - text
		/*
		var cell2 = row.insertCell(3);
		textNode = document.createTextNode(difference);
		cell2.style.textAlign = 'left';
		cell2.style.width = '50px';
		cell2.appendChild(textNode);
		*/

		// cell 3 - text
		var cell3 = row.insertCell(4);
		textNode = document.createTextNode(difference);
		cell3.style.textAlign = 'right';
		cell3.style.width = '70px';
		cell3.appendChild(textNode);

		// cell 4 - text
		/*
		var cell4 = row.insertCell(5);
		textNode = document.createTextNode(available);
		cell4.style.textAlign = 'right';
		cell4.style.width = '60px';
		cell4.appendChild(textNode);
		*/

		/*

		// cell 4 - input check
		var cell4 = row.insertCell(4);
		cell4.style.verticalAlign = 'top';
		var checkInp = document.createElement('input');
		checkInp.setAttribute('type', 'checkbox');
		checkInp.setAttribute('name', 'inputCheck' + iteration + tname);
		checkInp.setAttribute('size', '8');
		checkInp.onclick = function () {previousOrderClicked(this, tname)};
		cell4.appendChild(checkInp);
		var hiddenInp = document.createElement('input');
		hiddenInp.setAttribute('type', 'hidden');
		hiddenInp.setAttribute('name', 'hidden' + 'inputCheck' + iteration + tname);
		hiddenInp.setAttribute('value', id);
		cell4.appendChild(hiddenInp);
		*/

		//row.myRow = new myRowObject(textNode, hiddenAmount, checkInp);

		//updateAmount();

		//deleteAllRows(document.getElementById('inventory-table')); 
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
		processCommand('showAllCount');
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

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup", function () {

            // Create a Button using an existing <input> element as a data source

            var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", {onclick: {fn: saveCount}});
            var oSubmitButton2 = new YAHOO.widget.Button("submitbutton2", {onclick: {fn: finishCount}});

        });

    } ();

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
<body class="yui-skin-sam" onload="javascript:initErrors();<%= (search_str.equals("") && vendor_search_str.equals("")) ? "" : "processCommand('getCheckoutCodesByDescCount','" + search_str + "|" + vendor_search_str + "');" %>">

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
	    <p class="headline"><%= adminCompany.getLabel() %> Administration - Inventory Count</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

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

		    <div class="adminItem"><strong>Enter search text</strong> to find matching inventory items or <strong>click to view your entire inventory</strong>.</div>

			<div class="adminItem">
				<div class="leftTM">INVENTORY SEARCH</div>
				<div id="submitbuttonsfrommarkup2" class="right">
					<input onkeyup="if (this.value.length > 1) { processCommand('getCheckoutCodesByDescCount', this.value + '|' + document.forms[0].vendorSearchInput.value); }" name="searchInput" value="<%= search_str %>" onfocus="select();" class="inputbox" style="width: 164px;" />
					<input id="submitbutton3" class="formbutton" type="button" name="submit_button3" value="Show Entire Inventory" alt="Show Entire Inventory" style="margin-right: 10px; "/>
				</div>
				<div class="end"></div>
			</div>

			<div class="adminItem">
				<div class="leftTM">VENDOR SEARCH</div>
				<div class="right">
					<input onkeyup="if (this.value.length > 1) { processCommand('getCheckoutCodesByDescCount', document.forms[0].searchInput.value + '|' + this.value); }" name="vendorSearchInput" value="<%= vendor_search_str %>" onfocus="select();" class="inputbox" style="width: 164px;" />
				</div>
				<div class="end"></div>
			</div>

		    <div class="content_AdministrationTable">

			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
			    <div class="heading">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">

					    <tr style="display: block;">
						    <td style="width:   5px; text-align: left;  "></td>
						    <td style="width: 282px; text-align: left;  ">Item</td>
						    <td style="width:  70px; text-align: right;  ">Expected</td>
						    <td style="width:  70px; text-align: right;  ">Counted</td>
						    <td style="width:  70px; text-align: right;  ">Missing</td>
					    </tr>
				    </table>
			    </div>
			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>


			    <div class="organization">
				    <table id="inventory-table" cellspacing="0" cellpadding="0" border="0" summary="">
					    <tr>
						    <td style="width:   5px; text-align: left;  ">&nbsp;</td>
						    <td style="width: 282px; text-align: left;  ">Search To Find Items</td>
						    <td style="width:  70px; text-align: right;  ">&nbsp;</td>
						    <td style="width:  70px; text-align: right;  ">&nbsp;</td>
						    <td style="width:  70px; text-align: right;  ">&nbsp;</td>
					    </tr>
				    </table>
			    </div>

			    <!-- END Item -->

			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		    </div>



		    <div class="adminItem">
				<div class="leftTM">&nbsp;</div>
				<div id="submitbuttonsfrommarkup" class="right">
					<input id="submitbutton1" class="formbutton" type="button" name="submit_button" value="Save Count" alt="Save Count" style="margin-right: 10px; "/>
					<input id="submitbutton2" class="formbutton" type="button" name="submit_button2" value="Finish & Apply Changes to Inventory" alt="Finish" style="margin-right: 10px; "/>
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
