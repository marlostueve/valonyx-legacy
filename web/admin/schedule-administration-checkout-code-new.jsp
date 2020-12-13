<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qb.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="adminCheckoutCode" class="com.badiyan.uk.online.beans.CheckoutCodeBean" scope="session" />

<%
CompanySettingsBean settings = adminCompany.getSettings();
QuickBooksSettings qb_settings = adminCompany.getQuickBooksSettings();

if (request.getParameter("new") != null)
{
    adminCheckoutCode = new CheckoutCodeBean();
    session.setAttribute("adminCheckoutCode", adminCheckoutCode);
}

Vector vendors = VendorRet.getVendors(adminCompany);
Vector departments = InventoryDepartment.getInventoryDepartments(adminCompany);

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

<%
if (settings.isSetupComplete())
{
%>
	<link rel="stylesheet" type="text/css"href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/resize.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css">
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>
<%
}
else
{
%>
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>
<%
}
%>

	<script type="text/javascript">

    var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var httpRequest;
    var request_NextDay;

    var edit = 0;

    var email_log_in = <%= (!settings.getLoginLabelString().equals("Username")) ? "true" : "false" %>;

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

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup", function () {

            // Create a Button using an existing <input> element as a data source

            var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "submit" });

        });

    } ();

</script>

<script type="text/javascript">

    (function () {

		var ButtonGroup = YAHOO.widget.ButtonGroup;


        // "checkedButtonChange" event handler for each ButtonGroup instance

        var onCheckedButtonChange = function (p_oEvent) {
	    email_log_in = !email_log_in;
	    document.forms[0].usernameInput.disabled = email_log_in;
        };


        // "contentready" event handler for the "radiobuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("radiobuttonsfrommarkup", function () {

            var oButtonGroup1 = new ButtonGroup("buttongroup1");
            oButtonGroup1.on("checkedButtonChange", onCheckedButtonChange);


        });

    }());

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        // "contentready" event handler for the "checkboxbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton1 = new Button("checkbutton1", { label: "This User is a Practitioner" });


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

<body class=" yui-skin-sam" onload="javascript:initErrors();">

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
	    <p class="headline"><%= adminCompany.getLabel() %> <%= settings.isSetupComplete() ? "Administration" : "Setup" %> - New Inventory Item</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">
<!--
		<p class="headlineA"></p>

		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
-->
		<struts-html:form action="/admin/newCheckoutCode" focus="codeInput" >

		    <input type="hidden" name="delete_id" />

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide details</strong> for the new inventory item.</div>

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">ITEM #</div>
								<div class="right">
									<input name="itemNumberInput" value="<%= (adminCheckoutCode.getItemNumberString().length() == 0) ? CheckoutCodeBean.getNextItemNumber(adminCompany) : adminCheckoutCode.getItemNumberString() %>" onfocus="select();" value="" maxlength="10" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">UPC</div>
								<div class="right">
									<input name="upcInput" value="<%= adminCheckoutCode.getUPCString() %>" onfocus="select();" value="" maxlength="50" class="inputbox" style="width: 164px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ITEM NAME</div>
								<div class="right">
									<input name="descriptionInput" value="<%= adminCheckoutCode.getDescriptionString() %>" onfocus="select();" value="" maxlength="31" class="inputbox" style="width: 304px;" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">DESCRIPTION</div>
								<div class="right">
									<input name="salesDescInput" value="<%= adminCheckoutCode.getSalesDescriptionString() %>" onfocus="select();" value="" class="inputbox" style="width: 304px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ON-HAND QTY</div>
								<div class="right">
									<input name="onHandInput" value="<%= adminCheckoutCode.getOnHandQuantity() %>" onfocus="select();" value="" maxlength="5" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">REORDER POINT</div>
								<div class="right">
									<input name="reorderPointInput" value="<%= adminCheckoutCode.getReorderPoint() %>" onfocus="select();" value="" maxlength="5" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">AVAILABLE QTY</div>
								<div class="right">
								    <input name="availableInput" value="<%= adminCheckoutCode.getAvailableQuantity() %>" onfocus="select();" value="" maxlength="5" class="inputbox" style="width: 64px;" disabled />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">REGULAR PRICE</div>
								<div class="right">
									<input name="amountInput" value="<%= adminCheckoutCode.getAmountString() %>" onfocus="select();" value="" maxlength="10" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ORDER COST</div>
								<div class="right">
								    <input name="costInput" value="<%= adminCheckoutCode.getOrderCostString() %>" onfocus="select();" value="" maxlength="10" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">AVG UNIT COST</div>
								<div class="right">
									<input name="unitCostInput" value="<%= adminCheckoutCode.getAverageUnitCostString() %>" onfocus="select();" value="" maxlength="10" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">TAXABLE</div>
								<div class="right">
<%
try
{
    Iterator tax_codes = SalesTaxCodeRet.getSalesTaxCodes(adminCompany).iterator();
    if (tax_codes.hasNext())
    {
%>
									<select name="taxCode" style="width: 304px;">
										<option value="0">-- NOT TAXABLE --</option>
<%
		while (tax_codes.hasNext())
		{
			SalesTaxCodeRet code_obj = (SalesTaxCodeRet)tax_codes.next();
%>
										<option value="<%= code_obj.getValue() %>"<%= (adminCheckoutCode.getSalesTaxCodeId() == code_obj.getId()) ? " selected" : ""  %>><%= code_obj.getLabel() %></option>
<%
		}
%>
									</select>
<%
    }
    else
    {
%>
									No Taxable Code Information Found<input type="hidden" name="taxCode" value="0" />
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
							<div class="adminItem">
								<div class="leftTM">VENDOR</div>
								<div class="right">
									<select name="vendorSelect" style="width: 304px;">
										<option value="0">-- NO VENDOR --</option>
<%
try
{
    Iterator vendor_itr = vendors.iterator();
    while (vendor_itr.hasNext())
    {
	    VendorRet vendor_obj = (VendorRet)vendor_itr.next();
%>
										<option value="<%= vendor_obj.getValue() %>"<%= (adminCheckoutCode.getVendorId() == vendor_obj.getId()) ? " selected" : ""  %>><%= vendor_obj.getLabel() %></option>
<%
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
								<div class="leftTM">TYPE</div>
								<div class="right">
									<select name="typeSelect" style="width: 304px;">
										<option value="0">-- SELECT A CHECKOUT CODE TYPE --</option>
                                        <option value="<%= CheckoutCodeBean.PROCEDURE_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.PROCEDURE_TYPE) ? " selected" : ""  %>>Service</option>
                                        <option value="<%= CheckoutCodeBean.INVENTORY_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.INVENTORY_TYPE) ? " selected" : ""  %>>Inventory</option>
                                        <option value="<%= CheckoutCodeBean.NON_INVENTORY_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.NON_INVENTORY_TYPE) ? " selected" : ""  %>>Non Inventory</option>
                                        <option value="<%= CheckoutCodeBean.GROUP_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.GROUP_TYPE) ? " selected" : ""  %>>Group</option>
                                        <option value="<%= CheckoutCodeBean.GIFT_CARD %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.GIFT_CARD) ? " selected" : ""  %>>Gift Card</option>
                                        <option value="<%= CheckoutCodeBean.GIFT_CERTIFICATE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.GIFT_CERTIFICATE) ? " selected" : ""  %>>Gift Certificate</option>
                                        <!-- <option value="<%= CheckoutCodeBean.PAYMENT_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.PAYMENT_TYPE) ? " selected" : ""  %>>Payment</option> -->
										<option value="<%= CheckoutCodeBean.PLAN_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.PLAN_TYPE) ? " selected" : ""  %>>Payment Plan</option>
										<option value="<%= CheckoutCodeBean.SUBTOTAL %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.SUBTOTAL) ? " selected" : ""  %>>Subtotal</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DEPARTMENT</div>
								<div class="right">
									<select name="departmentSelect" style="width: 304px;">
										<option value="0">-- NO DEPARTMENT --</option>
<%
try
{
    Iterator department_itr = departments.iterator();
    while (department_itr.hasNext())
    {
	    InventoryDepartment department_obj = (InventoryDepartment)department_itr.next();
%>
										<option value="<%= department_obj.getValue() %>"<%= (adminCheckoutCode.getDepartmentId() == department_obj.getId()) ? " selected" : ""  %>><%= department_obj.getLabel() %></option>
<%
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
								<div class="leftTM">CODE</div>
								<div class="right">
									<input name="codeInput" value="<%= adminCheckoutCode.getCodeString() %>" onfocus="select();" value="" size="31" maxlength="50" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">PRACTICE AREA</div>
								<div class="right">
<%
try
{
    Iterator practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany).iterator();
    if (practice_areas.hasNext())
    {
%>
									<select name="practiceAreaSelect" style="width: 304px;">
										<option value="0">-- NONE --</option>
<%
		while (practice_areas.hasNext())
		{
			PracticeAreaBean practice_area_obj = (PracticeAreaBean)practice_areas.next();
%>
										<option value="<%= practice_area_obj.getValue() %>"<%= (adminCheckoutCode.getPracticeAreaId() == practice_area_obj.getId()) ? " selected" : ""  %>><%= practice_area_obj.getLabel() %></option>
<%
		}
%>
									</select>
<%
    }
    else
    {
%>
									No Practice Areas Found<input type="hidden" name="practiceAreaSelect" value="0" />
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
							<div class="adminItem">
								<div class="leftTM">IS PLAN USE</div>
								<div class="right">
									<input name="planUse" type="checkbox" <%= adminCheckoutCode.isPlanUse() ? "checked" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">COMMISSION</div>
								<div class="right">
									<input name="commission" type="checkbox" <%= adminCheckoutCode.isCommissionable() ? "checked" : "" %> />
								</div>
								<div class="end"></div>
							</div>


<%
if (qb_settings.isQuickBooksFSEnabled())
{
    Vector accounts = AccountRet.getAccounts(adminCompany);
%>
							<div class="adminItem"><strong style="font-weight: bolder;">QuickBooks Information</strong></div>

							<div class="adminItem">
								<div class="leftTM">EXPENSE ACCOUNT</div>
								<div class="right">
<%
    try
    {
	Iterator account_itr = accounts.iterator();
	if (account_itr.hasNext())
	{
%>
									<select name="expenseAccountSelect" style="width: 304px;">
										<option value="0">-- NONE --</option>
<%
		while (account_itr.hasNext())
		{
		    AccountRet account_obj = (AccountRet)account_itr.next();
%>
										<option value="<%= account_obj.getValue() %>"<%= (adminCheckoutCode.getExpenseAccountId() == account_obj.getId()) ? " selected" : ""  %>><%= account_obj.getLabel() %></option>
<%
		}
%>
									</select>
<%
	}
	else
	{
%>
									No Accounts Found<input type="hidden" name="assetAccountSelect" value="0" />
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

							<div class="adminItem">
								<div class="leftTM">INCOME ACCOUNT</div>
								<div class="right">
<%
    try
    {
		Iterator account_itr = accounts.iterator();
		if (account_itr.hasNext())
		{
%>
									<select name="incomeAccountSelect" style="width: 304px;">
										<option value="0">-- NONE --</option>
<%
			while (account_itr.hasNext())
			{
				AccountRet account_obj = (AccountRet)account_itr.next();
%>
										<option value="<%= account_obj.getValue() %>"<%= (adminCheckoutCode.getIncomeAccountId() == account_obj.getId()) ? " selected" : ""  %>><%= account_obj.getLabel() %></option>
<%
			}
%>
									</select>
<%
		}
		else
		{
%>
									No Accounts Found<input type="hidden" name="incomeAccountSelect" value="0" />
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
							
							<div class="adminItem">
								<div class="leftTM">COGS ACCOUNT</div>
								<div class="right">
<%
    try
    {
		Iterator account_itr = accounts.iterator();
		if (account_itr.hasNext())
		{
%>
									<select name="cogsAccountSelect" style="width: 304px;">
										<option value="0">-- NONE --</option>
<%
			while (account_itr.hasNext())
			{
				AccountRet account_obj = (AccountRet)account_itr.next();
%>
										<option value="<%= account_obj.getValue() %>"<%= (adminCheckoutCode.getCOGSAccountId() == account_obj.getId()) ? " selected" : ""  %>><%= account_obj.getLabel() %></option>
<%
			}
%>
									</select>
<%
		}
		else
		{
%>
									No Accounts Found<input type="hidden" name="cogsAccountSelect" value="0" />
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

							<div class="adminItem">
								<div class="leftTM">ASSET ACCOUNT</div>
								<div class="right">
<%
    try
    {
	Iterator account_itr = accounts.iterator();
	if (account_itr.hasNext())
	{
%>
									<select name="assetAccountSelect" style="width: 304px;">
										<option value="0">-- NONE --</option>
<%
		while (account_itr.hasNext())
		{
		    AccountRet account_obj = (AccountRet)account_itr.next();
%>
										<option value="<%= account_obj.getValue() %>"<%= (adminCheckoutCode.getAssetAccountId() == account_obj.getId()) ? " selected" : ""  %>><%= account_obj.getLabel() %></option>
<%
		}
%>
									</select>
<%
	}
	else
	{
%>
									No Accounts Found<input type="hidden" name="assetAccountSelect" value="0" />
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
							
							<input type="hidden" name="cogsAccountSelect" value="0" />
							<input type="hidden" name="assetAccountSelect" value="0" />
<%
}
else
{
%>
							<input type="hidden" name="cogsAccountSelect" value="0" />
							<input type="hidden" name="incomeAccountSelect" value="0" />
							<input type="hidden" name="assetAccountSelect" value="0" />
							<input type="hidden" name="expenseAccountSelect" value="0" />
<%
}
%>


								<!--
							<div class="adminItem">
								<div class="leftTM">&nbsp;</div>
								<div class="right">
									<input class="formbutton" type="submit" value="Add New Checkout Code" alt="Add New Checkout Code" /><input type="hidden" name="delete_id" value="0" />
								</div>
								<div class="end"></div>
							</div>
	-->

		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup" class="right">
			    <input id="submitbutton1" class="formbutton" type="submit" name="submit_button" value="Add New Inventory Item" alt="Add New Inventory Item" style="margin-right: 10px; "/>
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

