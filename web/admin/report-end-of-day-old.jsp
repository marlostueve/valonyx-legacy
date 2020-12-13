<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.io.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.conformance.aicc.rte.server.*, com.badiyan.uk.exceptions.*, com.badiyan.torque.*, org.apache.torque.util.*, org.apache.poi.poifs.filesystem.*, org.apache.poi.hssf.usermodel.*, com.valeo.qbpos.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>
<jsp:useBean id="saveFilename" class="java.lang.String" scope="session" />

<%

int course_id = -1;
if ((request.getParameter("id") != null))
    course_id = Integer.parseInt(request.getParameter("id"));
if (request.getParameter("sort") != null)
    courseReportLister.setSort(Short.parseShort(request.getParameter("sort")));


Vector orders_x = new Vector();
HashMap starting_balance_hash = new HashMap(37);

boolean dates_equal = false;

try
{
    Date start_date = courseReportLister.getStartDate();
    Date end_date = courseReportLister.getEndDate();

	Calendar start_cal = Calendar.getInstance();
	start_cal.setTime(start_date);
	Calendar end_cal = Calendar.getInstance();
	end_cal.setTime(end_date);

	dates_equal = (start_cal.get(Calendar.DATE) == end_cal.get(Calendar.DATE)) && (start_cal.get(Calendar.MONTH) == end_cal.get(Calendar.MONTH)) && (start_cal.get(Calendar.YEAR) == end_cal.get(Calendar.YEAR));

    short sort = courseReportLister.getSort();

    Criteria crit = new Criteria();
    if (start_date != null)
    {
		crit.add(PersonorderPeer.ORDERDATE, start_date, Criteria.GREATER_EQUAL);
		if (end_date != null)
			crit.and(PersonorderPeer.ORDERDATE, end_date, Criteria.LESS_EQUAL);
    }
    else if (end_date != null)
    {
		crit.add(PersonorderPeer.ORDERDATE, end_date, Criteria.LESS_EQUAL);
    }
    switch (sort)
    {
		/*
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
		*/
    }
	crit.addAscendingOrderByColumn(PersonorderPeer.ORDERDATE);
    Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
    while (obj_itr.hasNext())
    {
		Personorder obj = (Personorder)obj_itr.next();
		orders_x.addElement(OrderBean.getOrder(obj.getOrderid()));
    }

	if (orders_x.size() > 0)
	{
		BigDecimal total_charges = new BigDecimal(0);
		BigDecimal total_payments = new BigDecimal(0);
		BigDecimal total_credits = new BigDecimal(0);

		BigDecimal order_charges;
		BigDecimal order_payments;
		BigDecimal order_credits;

		Iterator orders_x_itr = orders_x.iterator();
		while (orders_x_itr.hasNext())
		{
			OrderBean order = (OrderBean)orders_x_itr.next();
			UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(order.getPersonId());

			BigDecimal ending_balance = (BigDecimal)starting_balance_hash.get(person);
			if (ending_balance == null)
			{
				ending_balance = person.getPreviousBalance(adminCompany); // I know
				starting_balance_hash.put(person, ending_balance);
			}

			order_charges = new BigDecimal(0);
			order_payments = new BigDecimal(0);
			order_credits = new BigDecimal(0);

			BigDecimal order_total = order.getTotal();
			order_charges = order_charges.add(order_total);
			total_charges = total_charges.add(order_total);

			/*
			Iterator orderline_itr = order.getOrdersVec().iterator();
			while (orderline_itr.hasNext())
			{
				CheckoutOrderline orderline_obj = (CheckoutOrderline)orderline_itr.next();
				BigDecimal orderline_amount = orderline_obj.getActualAmount();
				if (orderline_obj.getQuantity().floatValue() > 0)
				{
					order_charges = order_charges.add(orderline_amount);
					total_charges = total_charges.add(orderline_amount);
				}
				else
				{
					// must be a credit or something

					order_credits = order_credits.add(orderline_amount.abs());
					total_credits = total_credits.add(orderline_amount.abs());
				}

			}
			*/

			Iterator tender_itr = TenderRet.getTenders(order).iterator();
			while (tender_itr.hasNext())
			{
				TenderRet tender_obj = (TenderRet)tender_itr.next();
				BigDecimal tender_amount = new BigDecimal(tender_obj.getAmount());

				System.out.println("tender_amount >" + tender_amount.toString());

				if (tender_obj.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
				{
					// not a real payment.  do anything with this value???
				}
				else
				{
					order_payments = order_payments.add(tender_amount);
					total_payments = total_payments.add(tender_amount);
				}

				/*
				if (tender_obj.getAmount() < 0)
				{
					// must be a credit or something
					order_credits = order_credits.add(tender_amount.abs());
					total_credits = total_credits.add(tender_amount.abs());
				}
				else
				{
					order_payments = order_payments.add(tender_amount);
					total_payments = total_payments.add(tender_amount);
				}
				*/


			}

			BigDecimal starting_balance = ending_balance.subtract(order_charges);
			starting_balance = starting_balance.add(order_credits);
			starting_balance = starting_balance.add(order_payments);

			starting_balance_hash.put(person, starting_balance);

			System.out.println(person.getLabel() + " >" + starting_balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

		}
	}




    if (courseReportLister.showExcel())
    {

		int row_index = 7;
		short column_index = 0;
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + "21-End-of-Day-Report.xls"));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);

		courseReportLister.addCell(sheet, 0, (short)0, adminCompany.getLabel());
		courseReportLister.addCell(sheet, 2, (short)0, courseReportLister.getStartDateString() + " - " + courseReportLister.getEndDateString());

		BigDecimal total_charges = new BigDecimal(0);
		BigDecimal total_payments = new BigDecimal(0);
		BigDecimal total_credits = new BigDecimal(0);

		BigDecimal order_charges;
		BigDecimal order_payments;
		BigDecimal order_credits;

		Iterator orders_x_itr = orders_x.iterator();
		while (orders_x_itr.hasNext())
		{
			HSSFCellStyle style = courseReportLister.getStyle(sheet, 7, (short)0);

			OrderBean order = (OrderBean)orders_x_itr.next();
			UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(order.getPersonId());order_charges = new BigDecimal(0);

			order_payments = new BigDecimal(0);
			order_credits = new BigDecimal(0);

			BigDecimal order_total = order.getTotal();
			order_charges = order_charges.add(order_total);
			total_charges = total_charges.add(order_total);

			/*
			Iterator orderline_itr = order.getOrdersVec().iterator();
			while (orderline_itr.hasNext())
			{
				CheckoutOrderline orderline_obj = (CheckoutOrderline)orderline_itr.next();
				BigDecimal orderline_amount = orderline_obj.getActualAmount();
				if (orderline_obj.getQuantity().floatValue() > 0)
				{
					order_charges = order_charges.add(orderline_amount);
					total_charges = total_charges.add(orderline_amount);
				}
				else
				{
					// must be a credit or something

					order_credits = order_credits.add(orderline_amount.abs());
					total_credits = total_credits.add(orderline_amount.abs());
				}

			}
			*/

			Iterator tender_itr = TenderRet.getTenders(order).iterator();
			while (tender_itr.hasNext())
			{
				TenderRet tender_obj = (TenderRet)tender_itr.next();
				BigDecimal tender_amount = new BigDecimal(tender_obj.getAmount());
				order_payments = order_payments.add(tender_amount);
				total_payments = total_payments.add(tender_amount);
			}

			/*
			BigDecimal starting_balance = ending_balance.subtract(order_charges);
			starting_balance = starting_balance.add(order_credits);
			starting_balance = starting_balance.add(order_payments);
			*/

			BigDecimal starting_balance = (BigDecimal)starting_balance_hash.get(person);
			BigDecimal ending_balance = starting_balance.add(order_charges);
			ending_balance = ending_balance.subtract(order_credits);
			ending_balance = ending_balance.subtract(order_payments);

			starting_balance_hash.put(person, ending_balance);

			courseReportLister.shiftRow(sheet, row_index + 2);
			courseReportLister.addCell(sheet, row_index, column_index, person.getLabel(), style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, person.getFileNumberString(), style); column_index++;
			if (dates_equal)
				courseReportLister.addCell(sheet, row_index, column_index, CUBean.getUserTimeString(order.getOrderDate()), style);
			else
				courseReportLister.addCell(sheet, row_index, column_index, CUBean.getUserDateString(order.getOrderDate()) + " - " + CUBean.getUserTimeString(order.getOrderDate()), style);
			column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, starting_balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, order_charges.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, order_payments.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, order_credits.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, ending_balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), style); column_index++;

			row_index++;
			column_index = 0;
		}

		courseReportLister.addCell(sheet, row_index + 2, (short)4, total_charges.setScale(2, BigDecimal.ROUND_HALF_UP).toString()); column_index++;
		courseReportLister.addCell(sheet, row_index + 2, (short)5, total_payments.setScale(2, BigDecimal.ROUND_HALF_UP).toString()); column_index++;
		courseReportLister.addCell(sheet, row_index + 2, (short)6, total_credits.setScale(2, BigDecimal.ROUND_HALF_UP).toString()); column_index++;

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
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Valonyx</title>

<link rel="stylesheet" type="text/css" href="../css/schedule-client.css" />
<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/resize/assets/skins/sam/resize.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/layout/assets/skins/sam/layout.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" />

<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/menu/assets/skins/sam/menu.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/calendar/assets/skins/sam/calendar.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/tabview/assets/skins/sam/tabview.css" />

<script type="text/javascript" src="../yui/build/yahoo/yahoo-min.js"></script>
<script type="text/javascript" src="../yui/build/event/event-min.js"></script>
<script type="text/javascript" src="../yui/build/dom/dom-min.js"></script>

<script type="text/javascript" src="../yui/build/element/element-beta-min.js"></script>
<script type="text/javascript" src="../yui/build/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="../yui/build/resize/resize-beta-min.js"></script>
<script type="text/javascript" src="../yui/build/animation/animation-min.js"></script>
<script type="text/javascript" src="../yui/build/layout/layout-beta-min.js"></script>

<style type="text/css">

#tblCharges td, th { padding: 0.25em; font-size:100%; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }
#tblCredits td, th { padding: 0.25em; font-size:100%; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }
#tblPrevious td, th { padding: 0.25em; font-size:100%; }
#tblPrevious td { text-align: right; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }

</style>

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
#toggle {
    text-align: center;
    padding: 1em;
}
#toggle a {
    padding: 0 5px;
    border-left: 1px solid black;
}
#tRight {
    border-left: none !important;
}
</style>


<script type="text/javascript">
    
    var httpRequest;

    function processCommand(command, parameter)
    {
		if (window.ActiveXObject)
			httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
		else if (window.XMLHttpRequest)
			httpRequest = new XMLHttpRequest();

		httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain") %>/ClientServlet.html', true);
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
				if (httpRequest.responseXML.getElementsByTagName("checkout").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("checkout")[0];
					showCheckout(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];
					showPurchasedPlans(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
					showPeople(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
					showPerson(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("payment-plan-instance").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("payment-plan-instance")[0];
					showPaymentPlanInstance(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("client-billing").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("client-billing")[0];
					showBilling(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("bill").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("bill")[0];
					showBill(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("billing-orders").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("billing-orders")[0];
					showBillingOrders(xml_response);
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

<script>
YAHOO.namespace("example.container");

function init() {
	
}

YAHOO.util.Event.onDOMReady(init);


</script>

<script>

(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
                minWidth: 750,
                units: [
		{ position: 'top', height: 21, resize: false, body: 'top1' },
		{ position: 'left', header: 'Report Menu', width: 213, resize: false, body: 'left1', gutter: '0px', collapse: true, close: false, scroll: true, animate: true, duration: .1 },
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
        });
        layout.on('render', function() {
            layout.getUnitByPosition('left').on('close', function() {
                closeLeft();
            });
        });
        layout.render();
	
    });

})();
</script>

</head>

<%
String searchLastName = (String)session.getAttribute("searchLastName");
if (searchLastName == null)
	searchLastName = "";
%>

<body class="yui-skin-sam">

<!-- <h1>Valeo Schedule</h1> -->


<div id="content" style="height: 100%;">

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>


    
    <div id="left1">

			<div class="content_Administration">

<%@ include file="channels/channel-report-menu.jsp" %>

			</div>
			
    </div>
	
    <div id="center1">
	
		<div id="content">


		<div id="main">

			<!-- *** BEGIN Header *** -->
			<div id="header">
				<img src="../images/trspacer.gif" width="100%" height="1" alt="" />
				<div id="header_UserProperName"></div>
				<div id="header_MainNavigation">
					<a href="report-end-of-day-filter.jsp" title="BACK TO PREVIOUS PAGE">BACK TO PREVIOUS PAGE</a>
				</div>
				<div id="header_UserNavigation"></div>
			</div>
			<!-- *** END Header *** -->

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content" style="background: white;">

				<div class="content_TextBlock">
					<p class="headline" style="background: white;">End of Day Report</p>
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
								<td style="width:  86px; text-align: left;  "><a href="schedule-administration-report-end-of-day.jsp?sort=<%= (courseReportLister.getSort() == (short)1) ? 2 : 1  %>" title="">Patient Name</a></td>
								<td style="width:  81px; text-align: right;  "><a href="schedule-administration-report-end-of-day.jsp?sort=<%= (courseReportLister.getSort() == (short)3) ? 4 : 3  %>" title="">File Number</a></td>
								<td style="width: 104px; text-align: center;  "><a href="schedule-administration-report-end-of-day.jsp?sort=<%= (courseReportLister.getSort() == (short)5) ? 6 : 5  %>" title="">Time</a></td>
								<td style="width:  85px; text-align: right;  "><a href="schedule-administration-report-end-of-day.jsp?sort=<%= (courseReportLister.getSort() == (short)7) ? 8 : 7  %>" title="">Previous Balance</a></td>
								<td style="width:  84px; text-align: right;  "><a href="schedule-administration-report-end-of-day.jsp?sort=<%= (courseReportLister.getSort() == (short)9) ? 10 : 9  %>" title="">Charges</a></td>
								<td style="width:  84px; text-align: right;  "><a href="schedule-administration-report-end-of-day.jsp?sort=<%= (courseReportLister.getSort() == (short)11) ? 12 : 11  %>" title="">Payments</a></td>
								<td style="width:  84px; text-align: right;  "><a href="schedule-administration-report-end-of-day.jsp?sort=<%= (courseReportLister.getSort() == (short)13) ? 14 : 13  %>" title="">Credits & Adj.</a></td>
								<td style="width:  84px; text-align: right;  "><a href="schedule-administration-report-end-of-day.jsp?sort=<%= (courseReportLister.getSort() == (short)15) ? 16 : 15  %>" title="">Ending Balance</a></td>
							</tr>
						</table>
					</div>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%

		if (orders_x.size() > 0)
		{
			BigDecimal total_charges = new BigDecimal(0);
			BigDecimal total_payments = new BigDecimal(0);
			BigDecimal total_credits = new BigDecimal(0);

			BigDecimal order_charges;
			BigDecimal order_payments;
			BigDecimal order_credits;

			//HashMap previous_balance_hash = new HashMap(37);

			for (int i = 0; i < orders_x.size(); i++)
			{
				OrderBean order = (OrderBean)orders_x.get(i);
				UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(order.getPersonId());

				System.out.println("PERSON >" + person.getLabel());

				order_charges = new BigDecimal(0);
				order_payments = new BigDecimal(0);
				order_credits = new BigDecimal(0);

				BigDecimal order_total = order.getTotal();
				order_charges = order_charges.add(order_total);
				total_charges = total_charges.add(order_total);

				/*
				Iterator orderline_itr = order.getOrdersVec().iterator();
				while (orderline_itr.hasNext())
				{
					CheckoutOrderline orderline_obj = (CheckoutOrderline)orderline_itr.next();
					BigDecimal orderline_amount = orderline_obj.getActualAmount();
					System.out.println(order.getId() + " .. orderline_amount >" + orderline_amount.toString());
					if (orderline_obj.getQuantity().floatValue() > 0)
					{
						order_charges = order_charges.add(orderline_amount);
						total_charges = total_charges.add(orderline_amount);
					}
					else
					{
						// must be a credit or something

						//order_credits = order_credits.add(orderline_amount.abs());
						//total_credits = total_credits.add(orderline_amount.abs());
					}

				}
				*/

				Iterator tender_itr = TenderRet.getTenders(order).iterator();
				while (tender_itr.hasNext())
				{
					TenderRet tender_obj = (TenderRet)tender_itr.next();
					BigDecimal tender_amount = new BigDecimal(tender_obj.getAmount());

					System.out.println("tender_amount >" + tender_amount.toString());

					if (tender_obj.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
					{
						// not a real payment.  do anything with this value???
					}
					else
					{
						order_payments = order_payments.add(tender_amount);
						total_payments = total_payments.add(tender_amount);
					}

					/*
					if (tender_obj.getAmount() < 0)
					{
						// must be a credit or something
						order_credits = order_credits.add(tender_amount.abs());
						total_credits = total_credits.add(tender_amount.abs());
					}
					else
					{
						order_payments = order_payments.add(tender_amount);
						total_payments = total_payments.add(tender_amount);
					}
					*/
					
					
				}

				/*
				BigDecimal starting_balance = ending_balance.subtract(order_charges);
				starting_balance = starting_balance.add(order_credits);
				starting_balance = starting_balance.add(order_payments);
				*/

				BigDecimal starting_balance = ((BigDecimal)starting_balance_hash.get(person));
				BigDecimal ending_balance = starting_balance.add(order_charges);
				ending_balance = ending_balance.subtract(order_credits);
				ending_balance = ending_balance.subtract(order_payments);

				starting_balance_hash.put(person, ending_balance);


%>
					<!-- BEGIN Report Entry -->
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:   4px; text-align: left;  "></td>
								<td style="width:  86px; text-align: left;  "><%= person.getLabel() %></td>
								<td style="width:  81px; text-align: right;  "><%= person.getFileNumberString() %></td>
<%
				if (dates_equal)
				{
%>
								<td style="width: 104px; text-align: right;  "><%= CUBean.getUserTimeString(order.getOrderDate()) %></td>
<%
				}
				else
				{
%>
								<td style="width: 104px; text-align: right;  "><%= CUBean.getUserDateString(order.getOrderDate()) + " - " + CUBean.getUserTimeString(order.getOrderDate())%></td>
<%
				}
%>
								<td style="width:  85px; text-align: right;  "><%= starting_balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  84px; text-align: right;  "><%= order_charges.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  84px; text-align: right;  "><%= order_payments.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  84px; text-align: right;"><%= order_credits.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  84px; text-align: right;"><%= ending_balance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
							</tr>
						</table>
					</div>
					<!-- BEGIN Report Entry -->
<%

			}
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
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Footer *** -->
			<div id="footer">
				<div id="footer_Copyright"><p>&copy;2008</p></div>
			</div>
			<!-- *** END Footer *** -->

		</div>

</div>

        
    </div>

    
</div>






<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
