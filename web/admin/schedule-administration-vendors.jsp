<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*, com.valeo.qb.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminVendor" class="com.valeo.qb.data.VendorRet" scope="session" />
<jsp:useBean id="adminAddress" class="com.badiyan.uk.beans.AddressBean" scope="session" />

<%

if (request.getParameter("id") != null)
{
	VendorRet obj = VendorRet.getVendor(Integer.parseInt(request.getParameter("id")));

    // ensure that this person is an admin for this company

    if (adminCompany.getId() == obj.getCompany().getId())
    {
		adminVendor = obj;
		session.setAttribute("adminVendor", adminVendor);
    }
}

//adminPhone.setCompany(adminVendor);
//adminFax.setCompany(adminVendor);

if (!adminVendor.isNew())
{
    try
    {
		adminAddress = adminVendor.getAddress();
    }
    catch (Exception x)
    {
		System.out.println("error 1 >" + x.getMessage());
		adminAddress = new AddressBean();
    }

	session.setAttribute("adminAddress", adminAddress);

	/*
    try
    {
		adminPhone = PhoneNumberBean.getPhoneNumber(adminVendor, PhoneNumberBean.COMPANY_PHONE_NUMBER_TYPE);
		session.setAttribute("adminPhone", adminPhone);
    }
    catch (Exception x)
    {
		System.out.println("error 2 >" + x.getMessage());
		x.printStackTrace();
    }

    try
    {
		adminFax = PhoneNumberBean.getPhoneNumber(adminVendor, PhoneNumberBean.FAX_PHONE_NUMBER_TYPE);
		session.setAttribute("adminFax", adminFax);
    }
    catch (Exception x)
    {
		System.out.println("error 3 >" + x.getMessage());
		x.printStackTrace();
    }
 */

}

String message = "<strong style=\"font-weight: bolder;\">Setup the Vendors for " + adminCompany.getLabel() + "</strong>.";

Vector vendors = VendorRet.getVendors(adminCompany);

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


<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white;">



    <div id="content">


	<div class="content_TextBlock">
	    <p class="headline"><%= adminCompany.getLabel() %> Administration - Vendors</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>

	    <div class="main">

		<p><%= message %></p>
		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

		<struts-html:form action="/admin/vendors" focus="nameInput">

		    <input type="hidden" name="delete_id" />
<%

if (!adminVendor.isNew())
{
%>
		    <div class="adminItem">You have selected to update <strong style="font-weight: bolder;"><%= adminVendor.getLabel() %></strong>.  When you are finished making changes, please click <strong style="font-weight: bolder;">Update Vendor.</strong></div>
<%
}
%>

		    <div class="adminItem"><strong style="font-weight: bolder;">Provide the name</strong> for this Vendor.</div>

		    <div class="adminItem">
			<div class="leftTM">VENDOR NAME</div>
			<div class="right">
			    <input name="name" onfocus="select();" value="<%= adminVendor.getLabel() %>" size="31" class="inputbox" maxlength="50" style="width: 206px; margin-right: 4px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">STREET 1</div>
			<div class="right">
			    <input name="street" onfocus="select();" value="<%= adminAddress.getStreet1String() %>" class="inputbox" maxlength="50" style="width: 206px; margin-right: 4px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">STREET 2</div>
			<div class="right">
			    <input name="street2" onfocus="select();" value="<%= adminAddress.getStreet2String() %>" class="inputbox" maxlength="50" style="width: 206px; margin-right: 4px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">CITY</div>
			<div class="right">
			    <input name="city" onfocus="select();" value="<%= adminAddress.getCityString() %>" class="inputbox" maxlength="50" style="width: 124px; margin-right: 4px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			    <div class="leftTM">STATE</div>
			    <div class="right">
				    <select name="state" class="select" style="width: 179px;" onchange="javascript:selectState();">
					  <option VALUE="">PLEASE SELECT</option>
					  <option VALUE="AL"<%= adminAddress.getStateString().equals("AL") ? " SELECTED" : "" %>>Alabama</option>
					  <option VALUE="AK"<%= adminAddress.getStateString().equals("AK") ? " SELECTED" : "" %>>Alaska</option>
					  <option VALUE="AZ"<%= adminAddress.getStateString().equals("AZ") ? " SELECTED" : "" %>>Arizona</option>
					  <option VALUE="AR"<%= adminAddress.getStateString().equals("AR") ? " SELECTED" : "" %>>Arkansas</option>
					  <option VALUE="CA"<%= adminAddress.getStateString().equals("CA") ? " SELECTED" : "" %>>California</option>
					  <option VALUE="CO"<%= adminAddress.getStateString().equals("CO") ? " SELECTED" : "" %>>Colorado</option>
					  <option VALUE="CT"<%= adminAddress.getStateString().equals("CT") ? " SELECTED" : "" %>>Connecticut</option>
					  <option VALUE="DE"<%= adminAddress.getStateString().equals("DE") ? " SELECTED" : "" %>>Delaware</option>
					  <option VALUE="DC"<%= adminAddress.getStateString().equals("DC") ? " SELECTED" : "" %>>District of Columbia</option>
					  <option VALUE="FL"<%= adminAddress.getStateString().equals("FL") ? " SELECTED" : "" %>>Florida</option>
					  <option VALUE="GA"<%= adminAddress.getStateString().equals("GA") ? " SELECTED" : "" %>>Georgia</option>
					  <option VALUE="HI"<%= adminAddress.getStateString().equals("HI") ? " SELECTED" : "" %>>Hawaii</option>
					  <option VALUE="ID"<%= adminAddress.getStateString().equals("ID") ? " SELECTED" : "" %>>Idaho</option>
					  <option VALUE="IL"<%= adminAddress.getStateString().equals("IL") ? " SELECTED" : "" %>>Illinois</option>
					  <option VALUE="IN"<%= adminAddress.getStateString().equals("IN") ? " SELECTED" : "" %>>Indiana</option>
					  <option VALUE="IA"<%= adminAddress.getStateString().equals("IA") ? " SELECTED" : "" %>>Iowa</option>
					  <option VALUE="KS"<%= adminAddress.getStateString().equals("KS") ? " SELECTED" : "" %>>Kansas</option>
					  <option VALUE="KY"<%= adminAddress.getStateString().equals("KY") ? " SELECTED" : "" %>>Kentucky</option>
					  <option VALUE="LA"<%= adminAddress.getStateString().equals("LA") ? " SELECTED" : "" %>>Louisiana</option>
					  <option VALUE="MA"<%= adminAddress.getStateString().equals("MA") ? " SELECTED" : "" %>>Maine</option>
					  <option VALUE="MD"<%= adminAddress.getStateString().equals("MD") ? " SELECTED" : "" %>>Maryland</option>
					  <option VALUE="MA"<%= adminAddress.getStateString().equals("MA") ? " SELECTED" : "" %>>Massachusetts</option>
					  <option VALUE="MI"<%= adminAddress.getStateString().equals("MI") ? " SELECTED" : "" %>>Michigan</option>
					  <option VALUE="MN"<%= adminAddress.getStateString().equals("MN") ? " SELECTED" : "" %>>Minnesota</option>
					  <option VALUE="MS"<%= adminAddress.getStateString().equals("MS") ? " SELECTED" : "" %>>Mississippi</option>
					  <option VALUE="MO"<%= adminAddress.getStateString().equals("MO") ? " SELECTED" : "" %>>Missouri</option>
					  <option VALUE="MT"<%= adminAddress.getStateString().equals("MT") ? " SELECTED" : "" %>>Montana</option>
					  <option VALUE="NE"<%= adminAddress.getStateString().equals("NE") ? " SELECTED" : "" %>>Nebraska</option>
					  <option VALUE="NV"<%= adminAddress.getStateString().equals("NV") ? " SELECTED" : "" %>>Nevada</option>
					  <option VALUE="NM"<%= adminAddress.getStateString().equals("NM") ? " SELECTED" : "" %>>New Mexico</option>
					  <option VALUE="NJ"<%= adminAddress.getStateString().equals("NJ") ? " SELECTED" : "" %>>New Jersey</option>
					  <option VALUE="NY"<%= adminAddress.getStateString().equals("NY") ? " SELECTED" : "" %>>New York</option>
					  <option VALUE="NH"<%= adminAddress.getStateString().equals("NH") ? " SELECTED" : "" %>>New Hampshire</option>
					  <option VALUE="NC"<%= adminAddress.getStateString().equals("NC") ? " SELECTED" : "" %>>North Carolina</option>
					  <option VALUE="ND"<%= adminAddress.getStateString().equals("ND") ? " SELECTED" : "" %>>North Dakota</option>
					  <option VALUE="OH"<%= adminAddress.getStateString().equals("OH") ? " SELECTED" : "" %>>Ohio</option>
					  <option VALUE="OK"<%= adminAddress.getStateString().equals("OK") ? " SELECTED" : "" %>>Oklahoma</option>
					  <option VALUE="OR"<%= adminAddress.getStateString().equals("OR") ? " SELECTED" : "" %>>Oregon</option>
					  <option VALUE="PA"<%= adminAddress.getStateString().equals("PA") ? " SELECTED" : "" %>>Pennsylvania</option>
					  <option VALUE="RI"<%= adminAddress.getStateString().equals("RI") ? " SELECTED" : "" %>>Rhode Island</option>
					  <option VALUE="SC"<%= adminAddress.getStateString().equals("SC") ? " SELECTED" : "" %>>South Carolina</option>
					  <option VALUE="SD"<%= adminAddress.getStateString().equals("SD") ? " SELECTED" : "" %>>South Dakota</option>
					  <option VALUE="TN"<%= adminAddress.getStateString().equals("TN") ? " SELECTED" : "" %>>Tennessee</option>
					  <option VALUE="TX"<%= adminAddress.getStateString().equals("TX") ? " SELECTED" : "" %>>Texas</option>
					  <option VALUE="UT"<%= adminAddress.getStateString().equals("UT") ? " SELECTED" : "" %>>Utah</option>
					  <option VALUE="VA"<%= adminAddress.getStateString().equals("VA") ? " SELECTED" : "" %>>Virginia</option>
					  <option VALUE="VT"<%= adminAddress.getStateString().equals("VT") ? " SELECTED" : "" %>>Vermont</option>
					  <option VALUE="WA"<%= adminAddress.getStateString().equals("WA") ? " SELECTED" : "" %>>Washington</option>
					  <option VALUE="WV"<%= adminAddress.getStateString().equals("WV") ? " SELECTED" : "" %>>West Virginia</option>
					  <option VALUE="WI"<%= adminAddress.getStateString().equals("WI") ? " SELECTED" : "" %>>Wisconsin</option>
					  <option VALUE="WY"<%= adminAddress.getStateString().equals("WY") ? " SELECTED" : "" %>>Wyoming</option>
					</select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">ZIP</div>
			<div class="right">
			    <input name="zip" onfocus="select();" value="<%= adminAddress.getZipCodeString() %>" class="inputbox" maxlength="15" style="width: 100px; margin-right: 4px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">PHONE</div>
			<div class="right">
			    <input name="phone" onfocus="select();" value="<%= adminVendor.getPhone() %>" class="inputbox" maxlength="50" style="width: 124px; margin-right: 4px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">FAX</div>
			<div class="right">
			    <input name="fax" onfocus="select();" value="<%= adminVendor.getFax() %>" class="inputbox" maxlength="50" style="width: 124px; margin-right: 4px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">ACCOUNT #</div>
			<div class="right">
			    <input name="accountNumber" onfocus="select();" value="<%= adminVendor.getAccountNumber() %>" class="inputbox" maxlength="20" style="width: 164px; margin-right: 4px;" />
			</div>
			<div class="end"></div>
		    </div>

		    <div class="adminItem">
			<div class="leftTM">&nbsp;</div>
			<div id="submitbuttonsfrommarkup2" class="right">
			    <input id="submitbutton4" class="formbutton" type="submit" name="submit_button" value="<%= adminVendor.isNew() ? "Add" : "Update" %> Vendor" alt="<%= adminVendor.isNew() ? "Add" : "Update" %> Vendor" style="margin-right: 10px; "/>
			</div>
			<div class="end"></div>
		    </div>

		    <div class="content_AdministrationTable">

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			<div class="heading">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr style="display: block;">
						<td style="width:  20px; text-align: left;  "></td>
						<td style="width: 302px; text-align: left;  ">Vendor</td>
						<td style="width: 100px; text-align: left;  ">&nbsp;</td>
						<td style="width:  70px; text-align: center;  ">&nbsp;</td>
					</tr>
				</table>
			</div>
			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%



try
{
    Iterator vendor_itr = vendors.iterator();
    if (vendor_itr.hasNext())
    {
		while (vendor_itr.hasNext())
		{
			VendorRet obj = (VendorRet)vendor_itr.next();
%>
			<!-- BEGIN Tax Code -->
			<div class="jobTitle">
				<table cellspacing="0" cellpadding="0" border="0" summary="">
					<tr>
						<td style="width:  20px; text-align: left;  "></td>
						<td style="width: 302px; text-align: left;  "><a href="schedule-administration-vendors.jsp?id=<%= obj.getValue() %>" title=""><%= obj.getLabel() %></a></td>
						<td style="width: 100px; text-align: left;  "></td>
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
						<td style="width: 492px; text-align: left;  " colspan="3">No Vendors Found</td>
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



</body>
</html>