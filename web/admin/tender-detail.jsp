<%@page import="com.valeo.qbms.data.QBMSCreditCardResponse"%>
<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, com.badiyan.torque.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qb.data.*, com.valeo.qbpos.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="adminTender" class="com.valeo.qbpos.data.TenderRet" scope="session" />


<%

CompanySettingsBean settings = adminCompany.getSettings();
//QuickBooksSettings qb_settings = adminCompany.getQuickBooksSettings();

if (request.getParameter("id") != null)
{
    try
    {
		int tender_id = Integer.parseInt(request.getParameter("id"));
		TenderRet test_tender = TenderRet.getTender(tender_id);
		if (test_tender.getCompanyId() == adminCompany.getId())
		{
			adminTender = test_tender;
			session.setAttribute("adminTender", adminTender);
		}
    }
    catch (NumberFormatException x)
    {
    }
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

	
boolean is_authorized = false;
boolean is_captured = false;
boolean is_charged = false;
boolean is_refund = false;
boolean is_voice_auth = false;
boolean is_void = false;

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

        document.forms[0].codeSelect.options.length = 0;
        var index = 0;
        while (xml_str.getElementsByTagName("code")[index] != null)
        {
            key = xml_str.getElementsByTagName("code")[index].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("code")[index].childNodes[1].childNodes[0].nodeValue;
            eval("var codeArr = " + value);

			codeArray[key] = codeArr;

			document.forms[0].codeSelect.options[index] = new Option(codeArr["desc"],key);
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
		var qty = document.getElementById("qty").value;
		var rate = document.getElementById("rate").value;

		var qty_valid = qty != '' && !isNaN(qty);
		var rate_valid = rate != '' && !isNaN(rate);

		if (qty_valid && rate_valid)
			document.getElementById("amount").value = (parseFloat(qty) * parseFloat(rate)).formatMoney(2, '.', '');
	}

    Number.prototype.formatMoney = function(c, d, t){
	var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    };

	function showReport()
	{
		//alert('showReport stuff');
		processCommand('purchaseOrderReport', <%= adminTender.getId() %>);
	}

	function openCashDrawer() {
		var applet = document.jZebra;
		if (applet != null) {
			applet.append("\x07");
			// Send to the printer
			applet.print();
		}
		else {
			alert("Applet not loaded!");
		}
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
<%
if (adminTender.getType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE))
{
%>
            var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "void" });
            var oSubmitButton2 = new YAHOO.widget.Button("submitbutton2", { value: "refund" });
<%
}
%>
            var oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", {onclick: {fn: openCashDrawer}});
            //var oSubmitButton5 = new YAHOO.widget.Button("submitbutton5", { value: "submit" });

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
	    <p class="headline"><%= adminCompany.getLabel() %> - Clients - <span id="selectedPerson"><%= adminPerson.isNew() ? "No Client Selected" : adminPerson.getLabel() %></span> - Ledger - Tender Detail</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

		<%@ include file="channels\channel-client-menu.jsp" %>

	    <div class="main">
<!--
		<p class="headlineA"></p>

		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
-->
		<struts-html:form action="/admin/tenderDetail" >

			<input type="hidden" name="delete_id" value="<%= adminTender.getValue() %>" />
		    <input type="hidden" name="delete_item_id" />

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

		    <!-- <div class="adminItem"><strong style="font-weight: bolder;">Provide details</strong> for the purchase order.</div> -->
<!--
<table name="TENDER_RET_DB" idMethod="native">
    <column name="TENDER_RET_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="CLIENT_ID" required="true" type="INTEGER"/>
    <column name="SALES_RECEIPT_RET_DB_ID" required="true" type="INTEGER"/>
    <column name="INVOICE_RET_DB_ID" required="true" type="INTEGER"/>
    <column name="ORDER_ID" required="true" type="INTEGER"/>

    <column name="TENDER_TYPE" required="true" size="30" type="VARCHAR"/>
    <column name="CREDIT_CARD_TYPE" required="true" size="20" type="VARCHAR"/>
    <column name="TENDER_AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="TENDER_CHANGE" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="CHECK_NUMBER" required="false" type="INTEGER"/>

	<column name="GIFT_CERT_CARD_NUMBER" required="false" size="30" type="VARCHAR"/>

    <column name="TENDER_DATE" required="false" type="DATE"/>

    <column name="WORKSTATION_NAME" required="false" size="100" type="VARCHAR"/>
    <column name="IS_PAYMENT_ON_ACCOUNT" type="SMALLINT" default="0"/>
	
	<column name="REQUEST_I_D" required="false" size="50" type="VARCHAR"/>
	<column name="TXN_I_D" required="true" type="VARCHAR" size="20"/>

    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="SALES_RECEIPT_RET_DB">
		<reference local="SALES_RECEIPT_RET_DB_ID" foreign="SALES_RECEIPT_RET_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="INVOICE_RET_DB">
		<reference local="INVOICE_RET_DB_ID" foreign="INVOICE_RET_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSONORDER">
		<reference local="ORDER_ID" foreign="ORDERID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CLIENT_ID" foreign="PERSONID"/>
    </foreign-key>
</table>

<table name="TENDER_RET_DB_ORDER_MAPPING">
    <column name="TENDER_RET_DB_ORDER_MAPPING_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="TENDER_RET_DB_ID" required="true" type="INTEGER"/>
    <column name="ORDER_ID" required="true" type="INTEGER"/>
	<column name="TENDER_AMOUNT_APPLIED" required="false" scale="2" size="7" type="DECIMAL"/>
	
	<column name="REQUEST_I_D" required="false" size="50" type="VARCHAR"/>
	<column name="TXN_I_D" required="true" type="VARCHAR" size="20"/>

    <foreign-key foreignTable="TENDER_RET_DB">
		<reference local="TENDER_RET_DB_ID" foreign="TENDER_RET_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSONORDER">
		<reference local="ORDER_ID" foreign="ORDERID"/>
    </foreign-key>
</table>
-->

							<div class="adminItem">
								<div class="leftTM">TENDER #</div>
								<div class="right">
									<%= adminTender.getValue() %><input type="hidden" name="tenderId" value="<%= adminTender.getValue() %>" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CLIENT</div>
								<div class="right">
									<%= adminTender.getClient().getLabel() %>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">TYPE</div>
								<div class="right">
									<%= adminTender.getType() %>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">AMOUNT</div>
								<div class="right">
									<%= adminTender.getAmountString() %><input type="hidden" name="amount" value="<%= adminTender.getAmountString() %>" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DATE</div>
								<div class="right">
									<%= CUBean.getUserDateString(adminTender.getTenderDate()) %> - <%= CUBean.getUserTimeString(adminTender.getTenderDate()) %>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">WORKSTATION</div>
								<div class="right">
									<%= adminTender.getWorkstationName() %>
								</div>
								<div class="end"></div>
							</div>
<%
if (adminTender.getType().equals(TenderRet.CHECK_TENDER_TYPE)) {
%>
							<div class="adminItem">
								<div class="leftTM">CHECK NUMBER</div>
								<div class="right">
									<%= adminTender.getCheckNumber() %>
								</div>
								<div class="end"></div>
							</div>
<%
}
else if (adminTender.getType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE)) {
	
	// find any associated 

	
	String auth_code = "[NOT FOUND]";
	String credit_card_trans_id = "[NOT FOUND]";
	String card_number = "[NOT FOUND]";
	
	Iterator response_itr = QBMSCreditCardResponse.getResponses(adminTender).iterator();
	while (response_itr.hasNext()) {
		
		QBMSCreditCardResponse credit_card_response = (QBMSCreditCardResponse)response_itr.next();
		
		System.out.println("   xxx credit_card_response.getResponseType() >" + credit_card_response.getResponseType());
		
		switch (credit_card_response.getResponseType()) {
			case QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE: is_authorized = true; break;
			case QBMSCreditCardResponse.CAPTURE_RESPONSE_TYPE: is_captured = true; break;
			case QBMSCreditCardResponse.CHARGE_RESPONSE_TYPE: is_charged = true; break;
			case QBMSCreditCardResponse.REFUND_RESPONSE_TYPE: is_refund = true; break;
			case QBMSCreditCardResponse.VOICE_AUTHORIZATION_RESPONSE_TYPE: is_voice_auth = true; break;
			case QBMSCreditCardResponse.VOID_TRANSACTION_RESPONSE_TYPE: is_void = true; break;
		}
		
		if (credit_card_response.getAuthorizationCode() != null)
			auth_code = credit_card_response.getAuthorizationCode();
		
		if (credit_card_response.getCreditCardTransId() != null)
			credit_card_trans_id = credit_card_response.getCreditCardTransId();
		
		if (credit_card_response.getMerchantAccountNumber() != null)
			card_number = credit_card_response.getMerchantAccountNumber();
		
	}
	
%>
							<div class="adminItem">
								<div class="leftTM">CARD TYPE</div>
								<div class="right">
									<%= adminTender.getCreditCardType() %><%= adminTender.isPaymentOnAccount() ? "&nbsp;Payment on Account" : "" %>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">STATUS</div>
								<div class="right">
									<%= is_void ? "This tender has been voided" : is_voice_auth ? "This tender has been voice authorized" : is_refund ? "This tender has been refunded" : is_charged ? "This tender has been charged" : is_captured ? "This tender has been settled" : is_authorized ? "This tender has been authorized.  Settlement Pending." : "[NOT FOUND]" %>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CARD TYPE</div>
								<div class="right">
									<%= adminTender.getCreditCardType() %>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">AUTH CODE</div>
								<div class="right">
									<%= auth_code %>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">TRANS ID</div>
								<div class="right">
									<%= credit_card_trans_id %>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CARD NUMBER</div>
								<div class="right">
									<%= card_number %>
								</div>
								<div class="end"></div>
							</div>
<%
}
else if (adminTender.getType().equals(TenderRet.GIFT_CARD_TENDER_TYPE)) {
%>
							<div class="adminItem">
								<div class="leftTM">CARD NUMBER</div>
								<div class="right">
									<%= adminTender.getGiftCertCardNumber() %>
								</div>
								<div class="end"></div>
							</div>
<%
}
else if (adminTender.getType().equals(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE)) {
%>
							<div class="adminItem">
								<div class="leftTM">CERT NUMBER</div>
								<div class="right">
									<%= adminTender.getGiftCertCardNumber() %>
								</div>
								<div class="end"></div>
							</div>
<%
}
if (!adminTender.getChangeAmountString().equals("") && !adminTender.getChangeAmountString().equals("0.00")) {
%>
							<div class="adminItem">
								<div class="leftTM">CHANGE</div>
								<div class="right">
									<%= adminTender.getChangeAmountString() %>
								</div>
								<div class="end"></div>
							</div>
<%
}
%>
							<div class="adminItem">
								<div class="leftTM">ORDERS FOR TENDER:</div>
								<div class="right">
									&nbsp;
								</div>
								<div class="end"></div>
							</div>





		    <div class="content_AdministrationTable">

			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
			    <div class="heading">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">
					    <tr style="display: block;">
						    <td style="width:  15px; text-align: left;  "></td>
						    <td style="width:  70px; text-align: left;  ">Date</td>
						    <td style="width: 332px; text-align: left;  ">Order Description</td>
						    <td style="width:  50px; text-align: left;  ">Amount</td>
						    <td style="width:  25px; text-align: left;  ">&nbsp;</td>
					    </tr>
				    </table>
			    </div>
			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%

try
{
	Iterator order_itr = adminTender.getOrders().iterator();
	if (order_itr.hasNext())
	{
		for (int i = 0; order_itr.hasNext(); i++)
		{
			ValeoOrderBean order_objx = (ValeoOrderBean)order_itr.next();

			/*
			String quantity_received_str;
			if (item.getQuantityReceived() == null)
				quantity_received_str = "0.00";
			else
				quantity_received_str = item.getQuantityReceived().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
 			*/

%>
			    <!-- BEGIN Item -->

			    <div class="organization">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">
					    <tr>
						    <td style="width:  15px; text-align: left;  "></td>
						    <td style="width:  70px; text-align: left;  "><%= order_objx.getOrderDateString() %></td>
						    <td style="width: 332px; text-align: left;  "><a href="#" title=""><%= order_objx.getExtendedLabel() %></a></td>
						    <td style="width:  50px; text-align: right;  "><%= order_objx.getTotalString() %></td>
						    <td style="width:  25px; text-align: right;  ">
								&nbsp;
						    </td>
					    </tr>
				    </table>
			    </div>

			    <!-- END Item -->
<%
		}
	}
	else
	{
%>
			    <!-- BEGIN Item -->

			    <div class="organization">
				    <table cellspacing="0" cellpadding="0" border="0" summary="">
					    <tr>
						    <td colspan="5">No Orders Found For Tender</td>
					    </tr>
				    </table>
			    </div>

			    <!-- END Item -->
<%
	}
}
catch (Exception x)
{
	x.printStackTrace();
}
%>
			    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		    </div>



		    <div class="adminItem">
				<div class="leftTM">&nbsp;</div>
				<div id="submitbuttonsfrommarkup" class="right">
<%

/*
boolean is_authorized = false;
boolean is_captured = false;
boolean is_charged = false;
boolean is_refund = false;
boolean is_voice_auth = false;
boolean is_void = false;
 */

boolean can_void = is_authorized && !is_captured && !is_charged && !is_refund && !is_voice_auth && !is_void;
boolean can_refund = is_authorized && (is_captured || is_charged || is_voice_auth) && !is_refund && !is_void;

if (adminTender.getType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE))
{
%>
					<input id="submitbutton1" <%= can_void ? "" : "disabled=\"true\" " %>class="formbutton" type="submit" name="submit_button" value="Void" alt="Void" style="margin-right: 10px; "/>
					<input id="submitbutton2" <%= can_refund ? "" : "disabled=\"true\" " %> class="formbutton" type="submit" name="submit_button" value="Refund" alt="Refund" style="margin-right: 10px; "/>
<%
}
%>
					<input id="submitbutton3" class="formbutton" type="button" name="submit_button" value="Open Cash Drawer" alt="Open Cash Drawer" style="margin-right: 10px; "/>
					
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

<applet name="jZebra" code="jzebra.RawPrintApplet.class" archive="jzebra.jar" width="0" height="0">
  <param name="printer" value="Star TSP100 Cutter (TSP143)" />
  <param name="sleep" value="200" />
</applet>

</body>
</html>
