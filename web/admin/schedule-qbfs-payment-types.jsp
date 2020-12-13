<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*, com.valeo.qb.data.*, com.valeo.qb.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="itemPayment" class="com.valeo.qb.data.ItemPaymentRet" scope="session" />


<%

if (request.getParameter("id") != null)
{
    ItemPaymentRet obj = (ItemPaymentRet)ItemPaymentRet.getPaymentItem(Integer.parseInt(request.getParameter("id")));

    // ensure that this person is an admin for this company

    if (adminCompany.getId() == obj.getCompanyId())
    {
		itemPayment = obj;
		session.setAttribute("itemPayment", itemPayment);
    }
}

String message = "<strong style=\"font-weight: bolder;\">Setup the Payment Types for " + adminCompany.getLabel() + "</strong>.  These types will be synchronized with QuickBooks so that payments on Sales Receipts and Receive Payments can be properly communicated.";

//Vector tax_codes = ValeoTaxCodeBean.getTaxCodes(adminCompany);
QuickBooksSettings qb_settings = adminCompany.getQuickBooksSettings();

Vector payment_types = ItemPaymentRet.getPaymentItems(adminCompany);

%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />

	<style type="text/css">
	/*margin and padding on body element
	  can introduce errors in determining
	  element position and are not recommended;
	  we turn them off as a foundation for YUI
	  CSS treatments. */
	body {
		margin:0;
		padding:0;
	}
	</style>

	<link rel="stylesheet" type="text/css"href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/resize.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>

	<script type="text/javascript">

    var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var httpRequest;
    var request_NextDay;

    var edit = 0;

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

	</script>



<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup2", function () {

            // Create a Button using an existing <input> element as a data source

	    var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", { value: "Add Practice Area" });

        });

    } ();

</script>

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


<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>

</head>

<body class=" yui-skin-sam" onload="javascript:initErrors();">
<%
try
{
%>

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white;">



    <div id="content">


	<div class="content_TextBlock">
	    <p class="headline"><%= adminCompany.getLabel() %> Administration - Payment Types</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">

		<p><%= message %></p>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		<struts-html:form action="/admin/paymentTypes" focus="nameInput">

		    <input type="hidden" name="delete_id" />
<%

if (!itemPayment.isNew())
{
%>
		    <div class="adminItem">You have selected to update <strong style="font-weight: bolder;"><%= itemPayment.getLabel() %></strong>.  When you are finished making changes, please click <strong style="font-weight: bolder;">Update Payment Type.</strong></div>
<%
}
%>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide a name</strong> for this Payment Type.  <strong style="font-weight: bolder;">POS Cash</strong> or <strong style="font-weight: bolder;">Visa Credit Card</strong>, for example.</div>

		    <div class="adminItem">
			<div class="leftTM">PAYMENT NAME</div>
			<div class="right">
			    <input name="paymentName" onfocus="select();" value="<%= itemPayment.getLabel() %>" size="31" class="inputbox" maxlength="31" style="width: 206px; margin-right: 4px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">Specify the QuickBooks payment method used for this Payment Type.</div>

			<div class="adminItem">
				<div class="leftTM">QB PAYMENT METHOD</div>
				<div class="right">
					<select name="paymentMethodMapping" style="width: 304px;">
						<option value="-1">-- SELECT A QB PAYMENT METHOD --</option>
						
<%
Iterator itr = PaymentMethodRet.getPaymentMethods(adminCompany).iterator();
while (itr.hasNext())
{
	PaymentMethodRet payment_method = (PaymentMethodRet)itr.next();
%>
						<option value="<%= payment_method.getValue() %>"<%= (itemPayment.getPaymentMethodId() == payment_method.getId() ) ? " SELECTED" : "" %>><%= payment_method.getLabel() %></option>
<%
}
%>
						<option value="0">-- NOT LISTED, CREATE NEW PAYMENT METHOD --</option>
					</select>
				</div>
				<div class="end"></div>
			</div>

		    <div class="adminItem">Choose the correct method of payment below.  This seems redundant, but it's required to map things locally while the above setting is a QuickBooks mapping.</div>

			<div class="adminItem">
				<div class="leftTM">PAYMENT METHOD</div>
				<div class="right">
					<select name="creditCardMapping" style="width: 304px;">
						<option value="-1" selected>-- SELECT A PAYMENT METHOD --</option>
						<option value="<%= ItemPaymentRet.CASH %>"<%= (itemPayment.getCCType() == ItemPaymentRet.CASH) ? " SELECTED" : "" %>>Cash</option>
						<option value="<%= ItemPaymentRet.CHECK %>"<%= (itemPayment.getCCType() == ItemPaymentRet.CHECK) ? " SELECTED" : "" %>>Check</option>
						<option value="<%= ItemPaymentRet.GIFT_CARD %>"<%= (itemPayment.getCCType() == ItemPaymentRet.GIFT_CARD) ? " SELECTED" : "" %>>Gift Card</option>
						<option value="<%= ItemPaymentRet.GIFT_CERT %>"<%= (itemPayment.getCCType() == ItemPaymentRet.GIFT_CERT) ? " SELECTED" : "" %>>Gift Certificate</option>
						<option value="<%= ItemPaymentRet.VISA %>"<%= (itemPayment.getCCType() == ItemPaymentRet.VISA) ? " SELECTED" : "" %>>Visa</option>
						<option value="<%= ItemPaymentRet.MASTERCARD %>"<%= (itemPayment.getCCType() == ItemPaymentRet.MASTERCARD) ? " SELECTED" : "" %>>MasterCard</option>
						<option value="<%= ItemPaymentRet.DISCOVER %>"<%= (itemPayment.getCCType() == ItemPaymentRet.DISCOVER) ? " SELECTED" : "" %>>Discover</option>
						<option value="<%= ItemPaymentRet.AMERICAN_EXPRESS %>"<%= (itemPayment.getCCType() == ItemPaymentRet.AMERICAN_EXPRESS) ? " SELECTED" : "" %>>American Express</option>
					</select>
				</div>
				<div class="end"></div>
			</div>

		    <div class="adminItem">Select the QuickBooks account used to deposit payments of this type.</div>

			<div class="adminItem">
				<div class="leftTM">DEPOSIT TO ACCOUNT</div>
				<div class="right">
<%
try
{
	Vector accounts = AccountRet.getAccounts(adminCompany);
	Iterator account_itr = accounts.iterator();
	if (account_itr.hasNext())
	{
%>
					<select name="depositToAccountSelect" style="width: 304px;">
						<option value="0">-- NONE --</option>
<%
		while (account_itr.hasNext())
		{
		    AccountRet account_obj = (AccountRet)account_itr.next();
%>
						<option value="<%= account_obj.getValue() %>"<%= (itemPayment.getDepositToAccountId() == account_obj.getId()) ? " selected" : ""  %>><%= account_obj.getLabel() %></option>
<%
		}
%>
					</select>
<%
	}
	else
	{
%>
					No Accounts Found<input type="hidden" name="depositToAccountSelect" value="0" />
<%
	}
}
catch (Exception x)
{
	x.printStackTrace();
}
%>
				</div>
				<div class="end"></div>
			</div>

		    <div class="adminItem">If this box is checked, this Payment Type has been synchronized with QuickBooks.  This box is not editable and will become checked automatically upon a successful synchronization.</div>
			
			<div class="adminItem">
				<div class="leftTM">IS SYNCED</div>
				<div class="right">
					<input name="isSynced" disabled="true" type="checkbox" <%= itemPayment.isSynchronized() ? "checked" : "" %> />
				</div>
				<div class="end"></div>
			</div>

		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup2" class="right">
			    <input id="submitbutton4" class="formbutton" type="submit" name="submit_button" value="<%= itemPayment.isNew() ? "Add" : "Update" %> Payment Type" alt="<%= itemPayment.isNew() ? "Add" : "Update" %> Payment Type" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>

		    <div class="content_AdministrationTable">

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			<div class="heading">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr style="display: block;">
						<td style="width:  20px; text-align: left;  "></td>
						<td style="width: 302px; text-align: left;  ">Payment Type</td>
						<td style="width: 100px; text-align: left;  ">&nbsp;</td>
						<td style="width:  70px; text-align: center;  ">&nbsp;</td>
					</tr>
				</table>
			</div>
			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%

try
{
    Iterator payment_types_itr = payment_types.iterator();
    if (payment_types_itr.hasNext())
    {
		while (payment_types_itr.hasNext())
		{
			ItemPaymentRet obj = (ItemPaymentRet)payment_types_itr.next();
%>
			<!-- BEGIN Tax Code -->
			<div class="jobTitle">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr>
						<td style="width:  20px; text-align: left;  "></td>
						<td style="width: 302px; text-align: left;  "><a href="schedule-qbfs-payment-types.jsp?id=<%= obj.getValue() %>" title=""><%= obj.getLabel() %></a><%= obj.getCCType() > 0 ? " [" + ItemPaymentRet.getPaymentMethodString(obj.getCCType()) + "]" : "" %></td>
						<td style="width: 100px; text-align: right;  ">&nbsp;</td>
						<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= obj.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
					</tr>
				</table>
			</div>
			<!-- END Tax Code -->
<%
		}
    }
    else
    {
%>
			<!-- BEGIN Tax Code -->
			<div class="jobTitle">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr>
						<td style="width: 492px; text-align: left;  " colspan="3">No Payment Types Found</td>
					</tr>
				</table>
			</div>
			<!-- END Tax Code -->
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



		</struts-html:form>

	    </div>

	    <div class="end"></div>
	</div>





	<div class="content_TextBlock">
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

    </div>



</div>


<%
}
catch (Exception x)
{
	x.printStackTrace();
}
%>
</body>
</html>