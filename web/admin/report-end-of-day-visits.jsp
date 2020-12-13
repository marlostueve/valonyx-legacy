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

int detail_level = 1;

if (request.getParameter("detail") != null)
{
    try
    {
	    detail_level = Integer.parseInt(request.getParameter("detail"));
    }
    catch (Exception x)
    {
    }
}

Vector appointments = null;
boolean has_results = false;

int num_kept_total = 0;
int num_missed_total = 0;
int num_rescheduled_total = 0;
int num_cancelled_total = 0;
int num_new_total = 0;
int num_total_total = 0;

Date start_date = courseReportLister.getStartDate();
Date end_date = courseReportLister.getEndDate();

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

	Calendar start_cal = Calendar.getInstance();
	start_cal.setTime(start_date);
	Calendar end_cal = Calendar.getInstance();
	end_cal.setTime(end_date);

	dates_equal = (start_cal.get(Calendar.DATE) == end_cal.get(Calendar.DATE)) && (start_cal.get(Calendar.MONTH) == end_cal.get(Calendar.MONTH)) && (start_cal.get(Calendar.YEAR) == end_cal.get(Calendar.YEAR));

    short sort = courseReportLister.getSort();



	if (dates_equal)
		appointments = AppointmentBean.getAppointmentsForDate(adminCompany, start_date);
	else
		appointments = AppointmentBean.getAppointmentsForDate(adminCompany, start_date, end_date);

	has_results = !appointments.isEmpty();

	System.out.println("num appts >" + appointments.size());


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


			Iterator tender_itr = TenderRet.getTenders(order).iterator();
			while (tender_itr.hasNext())
			{
				TenderRet tender_obj = (TenderRet)tender_itr.next();
				BigDecimal tender_amount = new BigDecimal(tender_obj.getAmount());
				order_payments = order_payments.add(tender_amount);
				total_payments = total_payments.add(tender_amount);
			}


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

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.6.0/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/menu/assets/skins/sam/menu.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/calendar/assets/skins/sam/calendar.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/tabview/assets/skins/sam/tabview.css" />




<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/json/json-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/element/element-beta-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/datasource/datasource-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/charts/charts-experimental-min.js"></script>

<script type="text/javascript" src="../yui/build/yahoo/yahoo-min.js"></script>
<script type="text/javascript" src="../yui/build/event/event-min.js"></script>
<script type="text/javascript" src="../yui/build/dom/dom-min.js"></script>


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

<!--begin custom header content for this example-->
<style type="text/css">
	#chart
	{
		float: left;
		width: 450px;
		height: 300px;
	}

	.chart_title
	{
		display: block;
		font-size: 1.2em;
		font-weight: bold;
		margin-bottom: 0.4em;
	}
</style>
<!--end custom header content for this example-->


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
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
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

	
    <div id="center1" style="background-color: white;">
	
	    <div id="content">

		<div class="content_TextBlock">
			<p class="headline"><%= adminCompany.getLabel() %> Administration - End of Day Visit Report (<%= dates_equal ? CUBean.getUserDateString(start_date) : (CUBean.getUserDateString(start_date) + " - " + CUBean.getUserDateString(end_date)) %>)</p>
			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
		</div>

		<div class="content_Administration">

<%@ include file="channels/channel-report-menu.jsp" %>



		<div class="main" style="width: 692px;">

			<!-- *** BEGIN Content *** -->
			<!-- <div id="content" style="background: white;"> -->

<%


try
{
	if (courseReportLister.showExcel())
	{
%>
				<div class="content_TextBlock">
					<p style="background: white;">Download Link:&nbsp;&nbsp;<a href="<%= "../resources/" + saveFilename %>" target="_blank" title=""><%= saveFilename %></a></p>
				</div>
<%
	}
	if (courseReportLister.showHTML())
	{
%>
				<div class="content_Table">
					<div class="heading"><p>
<%
if (detail_level == 1)
{
%>
						<strong>Basic&nbsp;&nbsp;<a href="report-end-of-day-visits.jsp?detail=2">Detailed</a></strong>
<%
}
else
{
%>
						<strong><a href="report-end-of-day-visits.jsp?detail=1">Basic</a>&nbsp;&nbsp;Detailed</strong>
<%
}
%>
					</p></div>

					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
					<div class="heading">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr style="display: block;">
								<td style="width:   4px; text-align: left;  "></td>
								<td style="width:  86px; text-align: left;  "><a href="report-end-of-day-visits.jsp?sort=<%= (courseReportLister.getSort() == (short)1) ? 2 : 1  %>" title="">Practice Area</a></td>
<%
if (detail_level > 1)
{
%>
								<td style="width:  81px; text-align: right;  "><a href="report-end-of-day-visits.jsp?sort=<%= (courseReportLister.getSort() == (short)3) ? 4 : 3  %>" title="">Practitioner</a></td>
<%
}
%>
								<td style="width:  84px; text-align: right;  "><a href="report-end-of-day-visits.jsp?sort=<%= (courseReportLister.getSort() == (short)5) ? 6 : 5  %>" title="">Kept</a></td>
								<td style="width:  85px; text-align: right;  "><a href="report-end-of-day-visits.jsp?sort=<%= (courseReportLister.getSort() == (short)7) ? 8 : 7  %>" title="">Missed</a></td>
								<td style="width:  84px; text-align: right;  "><a href="report-end-of-day-visits.jsp?sort=<%= (courseReportLister.getSort() == (short)9) ? 10 : 9  %>" title="">Rescheduled</a></td>
								<td style="width:  84px; text-align: right;  "><a href="report-end-of-day-visits.jsp?sort=<%= (courseReportLister.getSort() == (short)11) ? 12 : 11  %>" title="">Cancelled</a></td>
								<td style="width:  84px; text-align: right;  "><a href="report-end-of-day-visits.jsp?sort=<%= (courseReportLister.getSort() == (short)11) ? 12 : 11  %>" title="">New</a></td>
								<td style="width:  84px; text-align: right;  "><a href="report-end-of-day-visits.jsp?sort=<%= (courseReportLister.getSort() == (short)13) ? 14 : 13  %>" title="">Total</a></td>
							</tr>
						</table>
					</div>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%


		int num_client_appointments = 0;

		if (has_results)
		{
			// sort the appointments by practice area

			HashMap appt_hash = new HashMap(7);
			HashMap appt_hash_practitioner = null;
			if (detail_level > 1)
				appt_hash_practitioner = new HashMap(7);
			Iterator appt_itr = appointments.iterator();
			while (appt_itr.hasNext())
			{
				AppointmentBean appointment = (AppointmentBean)appt_itr.next();
				if (appointment.isClientAppointment())
				{
					num_client_appointments++;
					try
					{
						PracticeAreaBean practice_area = appointment.getPracticeArea();
						Vector appointments_for_practice_area = (Vector)appt_hash.get(practice_area);
						if (appointments_for_practice_area == null)
						{
							appointments_for_practice_area = new Vector();
							appt_hash.put(practice_area, appointments_for_practice_area);
						}
						appointments_for_practice_area.addElement(appointment);

						if (detail_level > 1)
						{
							HashMap prac_hash = (HashMap)appt_hash_practitioner.get(practice_area);
							if (prac_hash == null)
							{
								prac_hash = new HashMap(5);
								appt_hash_practitioner.put(practice_area, prac_hash);
							}
							UKOnlinePersonBean practitioner = appointment.getPractitioner();
							Vector appointments_for_practitioner_in_practice_area = (Vector)prac_hash.get(practitioner);
							if (appointments_for_practitioner_in_practice_area == null)
							{
								appointments_for_practitioner_in_practice_area = new Vector();
								prac_hash.put(practitioner, appointments_for_practitioner_in_practice_area);
							}
							appointments_for_practitioner_in_practice_area.addElement(appointment);
						}
					}
					catch (ObjectNotFoundException x)
					{
						x.printStackTrace();
					}
				}
			}

			//System.out.println("num_client_appointments >" + num_client_appointments);

			Iterator practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany).iterator();
			while (practice_areas.hasNext())
			{
				PracticeAreaBean practice_area = (PracticeAreaBean)practice_areas.next();

				int num_kept = 0;
				int num_missed = 0;
				int num_rescheduled = 0;
				int num_cancelled = 0;
				int num_new = 0;
				int num_total = 0;

				// get the appointments for this practice area...

				Vector appointments_for_practice_area = (Vector)appt_hash.get(practice_area);
				if (appointments_for_practice_area != null)
				{
					appt_itr = appointments_for_practice_area.iterator();
					while (appt_itr.hasNext())
					{
						AppointmentBean appointment = (AppointmentBean)appt_itr.next();
						switch (appointment.getState())
						{
							case AppointmentBean.CANCELLED_APPOINTMENT_STATUS: num_cancelled++; num_cancelled_total++; break;
							case AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS: num_kept++; num_kept_total++; break;
							case AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS: num_kept++; num_kept_total++; break;
							case AppointmentBean.DEFAULT_APPOINTMENT_STATUS: num_missed++; num_missed_total++; break;
							case AppointmentBean.LATE_APPOINTMENT_STATUS: num_missed++; num_missed_total++; break;
							case AppointmentBean.NO_SHOW_APPOINTMENT_STATUS: num_missed++; num_missed_total++; break;
							case AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS: num_rescheduled++; num_rescheduled_total++; break;
						}

						if (appointment.isFirstTimeAppointmentForClientInPracticeArea())
						{
						    num_new++;
						    num_new_total++;
						}

						num_total++;
						num_total_total++;
					}
				}
%>
					<!-- BEGIN Report Entry -->
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:   4px; text-align: left;  "></td>
								<td style="width:  86px; text-align: left;  "><%= practice_area.getLabel() %></td>
<%
if (detail_level > 1)
{
%>
								<td style="width:  81px; text-align: right;  "></td>
<%
}
%>
								<td style="width:  84px; text-align: right;  "><%= num_kept %> (<%= CUBean.getPercentageString(num_kept, num_total, 0) %>)</td>
								<td style="width:  85px; text-align: right;  "><%= num_missed %> (<%= CUBean.getPercentageString(num_missed, num_total, 0) %>)</td>
								<td style="width:  84px; text-align: right;  "><%= num_rescheduled %> (<%= CUBean.getPercentageString(num_rescheduled, num_total, 0) %>)</td>
								<td style="width:  84px; text-align: right;  "><%= num_cancelled %> (<%= CUBean.getPercentageString(num_cancelled, num_total, 0) %>)</td>
								<td style="width:  84px; text-align: right;  "><%= num_new %> (<%= CUBean.getPercentageString(num_new, num_total, 0) %>)</td>
								<td style="width:  84px; text-align: right;"><%= num_total %> (<%= CUBean.getPercentageString(num_total, num_client_appointments, 0) %>)</td>
								<!-- <td style="width:  84px; text-align: right;">xxx</td> -->
							</tr>
						</table>
					</div>
					<!-- END Report Entry -->
<%
				if (detail_level > 1)
				{
					// get the practitioners for this practice area

					HashMap prac_hash = (HashMap)appt_hash_practitioner.get(practice_area);
					if (prac_hash != null)
					{
						Iterator itr = prac_hash.keySet().iterator();
						while (itr.hasNext())
						{
							int num_prac_kept = 0;
							int num_prac_missed = 0;
							int num_prac_rescheduled = 0;
							int num_prac_cancelled = 0;
							int num_prac_new = 0;
							int num_prac_total = 0;

							UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
							Vector practitioner_appointments = (Vector)prac_hash.get(practitioner);
							if (practitioner_appointments != null)
							{
								Iterator practitioner_appointments_itr = practitioner_appointments.iterator();
								while (practitioner_appointments_itr.hasNext())
								{
									AppointmentBean appointment = (AppointmentBean)practitioner_appointments_itr.next();
									switch (appointment.getState())
									{
										case AppointmentBean.CANCELLED_APPOINTMENT_STATUS: num_prac_cancelled++; break;
										case AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS: num_prac_kept++; break;
										case AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS: num_prac_kept++; break;
										case AppointmentBean.DEFAULT_APPOINTMENT_STATUS: num_prac_missed++; break;
										case AppointmentBean.LATE_APPOINTMENT_STATUS: num_prac_missed++; break;
										case AppointmentBean.NO_SHOW_APPOINTMENT_STATUS: num_prac_missed++; break;
										case AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS: num_prac_rescheduled++; break;
									}

									if (appointment.isFirstTimeAppointmentForClientInPracticeArea())
									    num_prac_new++;

									num_prac_total++;
								}
							}

%>
					<!-- BEGIN Report Entry -->
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:   4px; text-align: left;  "></td>
								<td style="width:  86px; text-align: left;  "></td>
								<td style="width:  81px; text-align: right;  "><%= practitioner.getLabel() %></td>
								<td style="width:  84px; text-align: right;  "><%= num_prac_kept %> (<%= CUBean.getPercentageString(num_prac_kept, num_kept, 0) %>)</td>
								<td style="width:  85px; text-align: right;  "><%= num_prac_missed %> (<%= CUBean.getPercentageString(num_prac_missed, num_missed, 0) %>)</td>
								<td style="width:  84px; text-align: right;  "><%= num_prac_rescheduled %> (<%= CUBean.getPercentageString(num_prac_rescheduled, num_rescheduled, 0) %>)</td>
								<td style="width:  84px; text-align: right;  "><%= num_prac_cancelled %> (<%= CUBean.getPercentageString(num_prac_cancelled, num_cancelled, 0) %>)</td>
								<td style="width:  84px; text-align: right;  "><%= num_prac_new %> (<%= CUBean.getPercentageString(num_prac_new, num_new, 0) %>)</td>
								<td style="width:  84px; text-align: right;"><%= num_prac_total %> (<%= CUBean.getPercentageString(num_prac_total, num_total, 0) %>)</td>
							</tr>
						</table>
					</div>
					<!-- END Report Entry -->
<%

						}
					}



				}
			}
%>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
					<!-- BEGIN Report Entry -->
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:   4px; text-align: left;  "></td>
								<td style="width:  86px; text-align: left;  ">TOTAL</td>
<%
if (detail_level > 1)
{
%>
								<td style="width:  81px; text-align: right;  "></td>
<%
}
%>
								<td style="width:  84px; text-align: right;  "><%= num_kept_total %> (<%= CUBean.getPercentageString(num_kept_total, num_total_total, 0) %>)</td>
								<td style="width:  85px; text-align: right;  "><%= num_missed_total %> (<%= CUBean.getPercentageString(num_missed_total, num_total_total, 0) %>)</td>
								<td style="width:  84px; text-align: right;  "><%= num_rescheduled_total %> (<%= CUBean.getPercentageString(num_rescheduled_total, num_total_total, 0) %>)</td>
								<td style="width:  84px; text-align: right;  "><%= num_cancelled_total %> (<%= CUBean.getPercentageString(num_cancelled_total, num_total_total, 0) %>)</td>
								<td style="width:  84px; text-align: right;  "><%= num_new_total %> (<%= CUBean.getPercentageString(num_new_total, num_total_total, 0) %>)</td>
								<td style="width:  84px; text-align: right;"><%= num_total_total %></td>
							</tr>
						</table>
					</div>
					<!-- END Report Entry -->

					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>
				<div class="content_Table">
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td colspan="<%= detail_level == 1 ? "7" : "8" %>">

			<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->



<div id="chart">Unable to load Flash content. The YUI Charts Control requires Flash Player 9.0.45 or higher. You can install the latest version at the <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.</p></div>

<script type="text/javascript">

	YAHOO.widget.Chart.SWFURL = "http://yui.yahooapis.com/2.6.0/build//charts/assets/charts.swf";

//--- data

	YAHOO.example.publicOpinion =
	[
		{ response: "Kept", count: <%= num_kept_total %> },
		{ response: "Missed", count: <%= num_missed_total %> },
		{ response: "Rescheduled", count: <%= num_rescheduled_total %> },
		{ response: "Cancelled", count: <%= num_cancelled_total %> }
	]

	var opinionData = new YAHOO.util.DataSource( YAHOO.example.publicOpinion );
	opinionData.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	opinionData.responseSchema = { fields: [ "response", "count" ] };

//--- chart

	var mychart = new YAHOO.widget.PieChart( "chart", opinionData,
	{
		dataField: "count",
		categoryField: "response",
		style:
		{
			padding: 20,
			legend:
			{
				display: "right",
				padding: 10,
				spacing: 5,
				font:
				{
					family: "Arial",
					size: 13
				}
			}
		},
		//only needed for flash player express install
		expressInstall: "assets/expressinstall.swf"
	});


</script>
<!--END SOURCE CODE FOR EXAMPLE =============================== -->

								</td>
							</tr>
						</table>
					</div>
					<!-- END Report Entry -->
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
			<!-- </div> -->



			<!-- *** END Content *** -->


		</div>
			
		<div class="end"></div>

	    </div>

	    <div class="content_TextBlock">
		<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	    </div>

</div>

        
    </div>

    
</div>



</body>



</html>
