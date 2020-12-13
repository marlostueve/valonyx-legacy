<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*, com.valeo.qb.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminTaxCode" class="com.badiyan.uk.online.beans.ValeoTaxCodeBean" scope="session" />
<jsp:useBean id="itemSalesTaxRet" class="com.valeo.qb.data.ItemSalesTaxRet" scope="session" />


<%

if (request.getParameter("id") != null)
{
    ValeoTaxCodeBean obj = (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode(Integer.parseInt(request.getParameter("id")));

    // ensure that this person is an admin for this company

    if (adminCompany.getId() == obj.getCompanyId())
    {
		adminTaxCode = obj;
		session.setAttribute("adminTaxCode", adminTaxCode);
    }
}

String message = "<strong style=\"font-weight: bolder;\">Setup the Tax Codes for " + adminCompany.getLabel() + "</strong>.  Tax codes allow you to establish different tax rates that you can assign to inventory items.";

Vector tax_codes = ValeoTaxCodeBean.getTaxCodes(adminCompany);
QuickBooksSettings qb_settings = adminCompany.getQuickBooksSettings();

Vector vendors = VendorRet.getVendors(adminCompany);

if (!adminTaxCode.isNew())
{
	try {
		itemSalesTaxRet = ItemSalesTaxRet.getSalesTaxItem(adminCompany, adminTaxCode);
		session.setAttribute("itemSalesTaxRet", itemSalesTaxRet);
	} catch (Exception x) {
		x.printStackTrace();
	}
}

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
	    <p class="headline"><%= adminCompany.getLabel() %> Administration - Tax Codes</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">

		<p><%= message %></p>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		<struts-html:form action="/admin/taxCodes" focus="nameInput">

		    <input type="hidden" name="delete_id" />
<%

if (!adminTaxCode.isNew())
{
%>
		    <div class="adminItem">You have selected to update <strong style="font-weight: bolder;"><%= adminTaxCode.getLabel() %></strong>.  When you are finished making changes, please click <strong style="font-weight: bolder;">Update Tax Code.</strong></div>
<%
}
%>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide the name</strong> for this Practice Area.  <strong style="font-weight: bolder;">Chiropractic</strong> or <strong style="font-weight: bolder;">Massage</strong>, for example.</div>

		    <div class="adminItem">
			<div class="leftTM">TAX CODE</div>
			<div class="right">
			    <input name="code" onfocus="select();" value="<%= adminTaxCode.getLabel() %>" size="31" class="inputbox" maxlength="50" style="width: 206px; margin-right: 4px;" />
			</div>
			<div class="end"></div>
		    </div>
			
			<div class="adminItem">
				<div class="leftTM">DESCRIPTION</div>
				<div class="right">
					<input name="itemDescInput" value="<%= itemSalesTaxRet.getItemDescString() %>" onfocus="select();" value="" class="inputbox" style="width: 304px;" />
				</div>
				<div class="end"></div>
			</div>

		    <div class="adminItem">
				<div class="leftTM">PERCENTAGE</div>
				<div class="right">
					<input name="percentage" onfocus="select();" value="<%= (adminTaxCode.getPercentage() == null) ? "" : adminTaxCode.getPercentage().toString() %>" size="31" class="inputbox" maxlength="5" style="width: 64px; margin-right: 4px;" <%= adminTaxCode.isGroup() ? "disabled=\"disabled\"" : ""  %> />&nbsp;%
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
						<option value="<%= vendor_obj.getValue() %>"<%= (itemSalesTaxRet.getTaxVendorRefDbId() == vendor_obj.getId()) ? " selected" : ""  %>><%= vendor_obj.getLabel() %></option>
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

<%
if (qb_settings.isQuickBooksFSEnabled())
{
%>

		    <div class="adminItem">
			    <div class="leftTM">QB SALES TAX CODE</div>
			    <div class="right">
				    <select name="sales_tax_code" class="select" style="width: 179px;">
					  <option VALUE="">PLEASE SELECT</option>
<%
	//String sales_tax_code_list_id = (adminTaxCode.getSalesTaxCodeListID() == null) ? "" : adminTaxCode.getSalesTaxCodeListID();
	Iterator itr = SalesTaxCodeRet.getSalesTaxCodes(adminCompany).iterator();
	while (itr.hasNext())
	{
		SalesTaxCodeRet ret = (SalesTaxCodeRet)itr.next();
%>
					  <option VALUE="<%= ret.getValue() %>"<%= (ret.getDefaultTaxCodeId() == adminTaxCode.getId()) ? " SELECTED" : "" %>><%= ret.getLabel() %></option>
<%
	}
%>
					</select>
			    </div>
			    <div class="end"></div>
		    </div>
<%
}
%>

			<div class="adminItem">
				<div class="leftTM">CHILD CODES</div>
				<div class="right">
					<select name="childCodes" multiple="true" size="7" style="width: 304px;">
<%
try
{
    Iterator codes_itr = tax_codes.iterator();
    if (codes_itr.hasNext())
    {
		while (codes_itr.hasNext())
		{
			ValeoTaxCodeBean obj = (ValeoTaxCodeBean)codes_itr.next();
			if (!obj.isGroup())
			{
%>
						<option value="<%= obj.getValue() %>"<%= adminTaxCode.isChild(adminCompany, obj) ? " SELECTED" : "" %>><%= obj.getLabel() %></option>
<%
			}
		}
    }
    else
    {
%>
						<option value="0" selected>-- NO CODES FOUND --</option>
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
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup2" class="right">
			    <input id="submitbutton4" class="formbutton" type="submit" name="submit_button" value="<%= adminTaxCode.isNew() ? "Add" : "Update" %> Tax Code" alt="<%= adminTaxCode.isNew() ? "Add" : "Update" %> Tax Code" style="margin-right: 10px; "/>
<%
if (!qb_settings.isQuickBooksFSEnabled())
{
%>
				<input name="sales_tax_code" type="hidden" value="-1" />
<%
}
%>
			</div>
			<div class="end"></div>
		    </div>

		    <div class="content_AdministrationTable">

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			<div class="heading">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr style="display: block;">
						<td style="width:  20px; text-align: left;  "></td>
						<td style="width: 302px; text-align: left;  ">Tax Code</td>
						<td style="width: 100px; text-align: left;  ">&nbsp;</td>
						<td style="width:  70px; text-align: center;  ">&nbsp;</td>
					</tr>
				</table>
			</div>
			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%

try
{
    Iterator codes_itr = tax_codes.iterator();
    if (codes_itr.hasNext())
    {
		while (codes_itr.hasNext())
		{
			ValeoTaxCodeBean obj = (ValeoTaxCodeBean)codes_itr.next();
%>
			<!-- BEGIN Tax Code -->
			<div class="jobTitle">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr>
						<td style="width:  20px; text-align: left;  "></td>
						<td style="width: 302px; text-align: left;  "><a href="schedule-administration-tax-codes.jsp?id=<%= obj.getValue() %>" title=""><%= obj.getLabel() %></a>&nbsp;<%= obj.isGroup() ? "[GROUP]" : "" %></td>
						<td style="width: 100px; text-align: right;  "><%= (obj.getPercentage() == null) ? "" : (obj.getPercentage().toString() + "%") %></td>
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
						<td style="width: 492px; text-align: left;  " colspan="3">No Tax Codes Found</td>
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