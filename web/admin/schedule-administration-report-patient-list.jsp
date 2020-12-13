<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.io.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.conformance.aicc.rte.server.*, com.badiyan.uk.exceptions.*, com.badiyan.torque.*,org.apache.torque.util.*,org.apache.poi.poifs.filesystem.*,org.apache.poi.hssf.usermodel.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>
<jsp:useBean id="saveFilename" class="java.lang.String" scope="session" />

<%
CompanySettingsBean settings = adminCompany.getSettings();


int course_id = -1;
if ((request.getParameter("id") != null))
    course_id = Integer.parseInt(request.getParameter("id"));
if (request.getParameter("sort") != null)
    courseReportLister.setSort(Short.parseShort(request.getParameter("sort")));


Vector clients_x = new Vector();


try
{
    Date start_date = courseReportLister.getStartDate();
    Date end_date = courseReportLister.getEndDate();
    short sort = courseReportLister.getSort();
	
	
	
	/*
			people = new Vector();
			Criteria crit = new Criteria();
			crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
			crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
			crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
			crit.add(CompanyPeer.COMPANYID, this.getId());
			//System.out.println("CompanyBean getPeople() crit >" + crit.toString());
			List objList = PersonPeer.doSelect(crit);
			Iterator itr = objList.iterator();
			while (itr.hasNext())
			{
				PersonBean obj = PersonBean.getPerson((Person)itr.next());
				people.addElement(obj);
			}
 */
	
	
	
    Criteria crit = new Criteria();
	
	crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
	crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
	crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
	crit.add(CompanyPeer.COMPANYID, adminCompany.getId());
	
    if (start_date != null)
    {
		crit.add(PersonPeer.CREATIONDATE, start_date, Criteria.GREATER_THAN);
		if (end_date != null)
			crit.and(PersonPeer.CREATIONDATE, end_date, Criteria.LESS_THAN);
    }
    else if (end_date != null)
    {
		crit.add(PersonPeer.CREATIONDATE, end_date, Criteria.LESS_THAN);
    }
    switch (sort)
    {
		case (short)1: crit.addAscendingOrderByColumn(PersonPeer.LASTNAME); break;
		case (short)2: crit.addDescendingOrderByColumn(PersonPeer.LASTNAME); break;
		case (short)3: crit.addAscendingOrderByColumn(PersonPeer.FIRSTNAME); break;
		case (short)4: crit.addDescendingOrderByColumn(PersonPeer.FIRSTNAME); break;
		case (short)5: crit.addAscendingOrderByColumn(PersonPeer.ISACTIVE); break;
		case (short)6: crit.addDescendingOrderByColumn(PersonPeer.ISACTIVE); break;
		case (short)7:
		{
			crit.addJoin(AddressPeer.ADDRESSID, PersonAddressPeer.ADDRESS_ID);
			crit.addJoin(PersonAddressPeer.PERSON_ID, PersonPeer.PERSONID);
			crit.addAscendingOrderByColumn(AddressPeer.STREET1);
			break;
		}
		case (short)8:
		{
			crit.addJoin(AddressPeer.ADDRESSID, PersonAddressPeer.ADDRESS_ID);
			crit.addJoin(PersonAddressPeer.PERSON_ID, PersonPeer.PERSONID);
			crit.addDescendingOrderByColumn(AddressPeer.STREET1);
			break;
		}
		case (short)9: crit.addAscendingOrderByColumn(PersonPeer.EMPLOYEEID); break;
		case (short)10: crit.addDescendingOrderByColumn(PersonPeer.EMPLOYEEID); break;
		case (short)11:
		{
			crit.addJoin(PhonenumberPeer.PERSONID, PersonPeer.PERSONID);
			crit.add(PhonenumberPeer.PHONETYPE, PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			crit.addAscendingOrderByColumn(PhonenumberPeer.PHONENUMBER);
			break;
		}
		case (short)12:
		{
			crit.addJoin(PhonenumberPeer.PERSONID, PersonPeer.PERSONID);
			crit.add(PhonenumberPeer.PHONETYPE, PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			crit.addDescendingOrderByColumn(PhonenumberPeer.PHONENUMBER);
			break;
		}
    }
    Iterator obj_itr = PersonPeer.doSelect(crit).iterator();
    while (obj_itr.hasNext())
    {
		Person obj = (Person)obj_itr.next();
		clients_x.addElement((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(obj.getPersonid()));
    }

    if (courseReportLister.showExcel())
    {
		int total_enrollments = 0;
		int row_index = 7;
		short column_index = 0;
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + "1-Patient-List.xls"));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);

		courseReportLister.addCell(sheet, 0, (short)0, adminCompany.getLabel());
		courseReportLister.addCell(sheet, 4, (short)0, courseReportLister.getStartDateString() + " - " + courseReportLister.getEndDateString());

		Iterator client_itr = clients_x.iterator();
		while (client_itr.hasNext())
		{
			UKOnlinePersonBean person_obj = (UKOnlinePersonBean)client_itr.next();
			courseReportLister.addCell(sheet, row_index, column_index, person_obj.getLastNameString()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, person_obj.getFirstNameString()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, person_obj.isActive() ? "Active" : "Inactive"); column_index++;
			
			try {
				AddressBean addr = person_obj.getAddress(AddressBean.PERSON_ADDRESS_TYPE);
				String street = addr.getStreet1String();
				if (!addr.getStreet2String().isEmpty())
					street = addr.getStreet1String() + " " + addr.getStreet2String();
				courseReportLister.addCell(sheet, row_index, column_index, street); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, addr.getCityString()); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, addr.getStateString()); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, addr.getZipCodeString()); column_index++;
			} catch (Exception x) {
				column_index++;
				column_index++;
				column_index++;
				column_index++;
			}
			courseReportLister.addCell(sheet, row_index, column_index, person_obj.getFileNumberString()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, person_obj.getPhoneNumbersString()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, person_obj.getEmail1String()); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, person_obj.isMale() ? "Male" : "Female"); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, person_obj.getBirthDateString()); column_index++;

			row_index++;
			column_index = 0;
		}

		saveFilename = System.currentTimeMillis() + ".xls";
		FileOutputStream fileOut = new FileOutputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + saveFilename);
		wb.write(fileOut);
		fileOut.close();
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
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
	

	<script type="text/javascript" src="../scripts/crir.js"></script>


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

    var last_code;
	var qty = 0;

    Number.prototype.formatMoney = function(c, d, t){
	var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    };
	
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

	function showAll()
	{
		processCommand('showAll');
	}

	function update()
	{
		processCommand('updateSoap', document.forms[0].nameInput.value);
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
            var oSubmitButton5 = new YAHOO.widget.Button("submitbutton5", {onclick: {fn: goBack}});

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
			<p class="headline"><%= adminCompany.getLabel() %> Administration - Client List Report</p>
			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
		</div>

		<div class="content_Administration">

<%@ include file="channels/channel-report-menu.jsp" %>



		<div class="main" style="width: 692px;">

			<div id="content" style="background: white;">

				<div class="content_TextBlock">
<%
try
{
	if (courseReportLister.showExcel())
	{
%>
					<p style="background: white;">Download Link:&nbsp;&nbsp;<a href="<%= "../resources/" + saveFilename %>" target="_blank" title=""><%= saveFilename %></a></p>
<%
	}
%>
				</div>
<%


	if (courseReportLister.showHTML())
	{
%>
				<div class="content_Table">

					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
					<div class="heading">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr style="display: block;">
								<td style="width:   4px; text-align: left;  "></td>
								<td style="width: 113px; text-align: left;  "><a href="schedule-administration-report-patient-list.jsp?sort=<%= (courseReportLister.getSort() == (short)1) ? 2 : 1  %>" title="">Last Name</a></td>
								<td style="width: 113px; text-align: left;  "><a href="schedule-administration-report-patient-list.jsp?sort=<%= (courseReportLister.getSort() == (short)3) ? 4 : 3  %>" title="">First Name</a></td>
								<td style="width:  63px; text-align: left;  "><a href="schedule-administration-report-patient-list.jsp?sort=<%= (courseReportLister.getSort() == (short)5) ? 6 : 5  %>" title="">Status</a></td>
								<td style="width: 163px; text-align: left;  "><a href="schedule-administration-report-patient-list.jsp?sort=<%= (courseReportLister.getSort() == (short)7) ? 8 : 7  %>" title="">Address</a></td>
								<td style="width:  57px; text-align: left;  "><a href="schedule-administration-report-patient-list.jsp?sort=<%= (courseReportLister.getSort() == (short)9) ? 10 : 9  %>" title="">File Number</a></td>
								<td style="width: 215px; text-align: left;  "><a href="schedule-administration-report-patient-list.jsp?sort=<%= (courseReportLister.getSort() == (short)11) ? 12 : 11  %>" title="">Phone</a></td>
							</tr>
						</table>
					</div>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
	Iterator clientsItr = clients_x.iterator();
    if (clientsItr.hasNext())
    {
%>
					<!-- BEGIN Report Entry -->
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
<%
		for (int i = 0; clientsItr.hasNext(); i++)
		{
			UKOnlinePersonBean person = (UKOnlinePersonBean)clientsItr.next();
%>
							<tr>
								<td style="width:   4px; text-align: left;  "></td>
								<td style="width: 113px; text-align: left;  "><%= person.getLastNameString() %></td>
								<td style="width: 113px; text-align: left;  "><%= person.getFirstNameString() %></td>
								<td style="width:  63px; text-align: left;  "><%= person.isActive() ? "Active" : "Inactive" %></td>
								<td style="width: 163px; text-align: left;  "><%= person.getAddressesString() %></td>
								<td style="width:  57px; text-align: left;  "><%= person.getFileNumberString() %></td>
								<td style="width: 215px; text-align: left;  "><%= person.getPhoneNumbersString() %></td>
							</tr>
<%
	    
		}
%>
						</table>
					</div>
					<!-- BEGIN Report Entry -->
<%
    }
    else
    {
%>
					<!-- BEGIN Report Entry -->
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width: 728px; text-align: left;  " colspan="8">No Results Found</td>
							</tr>
						</table>
					</div>
					<!-- BEGIN Report Entry -->
<%
    }
%>

					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>
<%
	}
}
catch (Exception x)
{
	x.printStackTrace();
}
%>
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
