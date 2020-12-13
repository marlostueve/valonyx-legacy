<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.io.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.conformance.aicc.rte.server.*, com.badiyan.uk.exceptions.*, com.badiyan.torque.*, org.apache.torque.util.*, org.apache.poi.poifs.filesystem.*, org.apache.poi.hssf.usermodel.*, com.valeo.qbpos.data.*, com.valeo.qb.data.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>
<jsp:useBean id="saveFilename" class="java.lang.String" scope="session" />

<%

UKOnlinePersonBean logged_in_person = (UKOnlinePersonBean)loginBean.getPerson();

int course_id = -1;
if ((request.getParameter("id") != null))
    course_id = Integer.parseInt(request.getParameter("id"));
if (request.getParameter("sort") != null)
    courseReportLister.setSort(Short.parseShort(request.getParameter("sort")));

Vector orders_x = new Vector();
HashMap starting_balance_hash = new HashMap(37);

boolean dates_equal = false;

MathContext mc = new MathContext(2, RoundingMode.HALF_UP);

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
	crit.addJoin(PersonorderPeer.ORDERID, CheckoutOrderlinePeer.ORDERID);
	crit.addJoin(CheckoutOrderlinePeer.CHECKOUT_CODE_ID, CheckoutCodePeer.CHECKOUT_CODE_ID);
	crit.addJoin(CheckoutCodePeer.INVENTORY_DEPARTMENT_DB_ID, InventoryDepartmentDbPeer.INVENTORY_DEPARTMENT_DB_ID);
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
	crit.add(PersonorderPeer.ORDERSTATUS, (Object)ValeoOrderBean.REVERSED_RECEIPT_ORDER_STATUS, Criteria.NOT_EQUAL);
	
	crit.add(PersonorderPeer.COMPANY_ID, adminCompany.getId());

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
	
	UKOnlinePersonBean practitioner = courseReportLister.getPerson();
	System.out.println("practitioner >" + practitioner);
	String report_label = "Commission Report";
	if (practitioner != null && practitioner.isNew())
	{
		crit.add(CheckoutOrderlinePeer.PRACTITIONER_ID, 0);
	}
	else if (practitioner != null && (practitioner.getId() > 0))
	{
		crit.add(CheckoutOrderlinePeer.PRACTITIONER_ID, practitioner.getId());
		report_label = practitioner.getLabel() + " - Commission Report";
	}
	
	crit.addAscendingOrderByColumn(InventoryDepartmentDbPeer.DEPARTMENT);
	crit.addAscendingOrderByColumn(CheckoutOrderlinePeer.PRACTITIONER_ID);
	crit.addAscendingOrderByColumn(PersonorderPeer.ORDERDATE);
	
	System.out.println("crit >" + crit.toString());
    Iterator obj_itr = CheckoutOrderlinePeer.doSelect(crit).iterator();
    while (obj_itr.hasNext())
    {
		CheckoutOrderline obj = (CheckoutOrderline)obj_itr.next();
		orders_x.addElement(obj);
    }
	
	System.out.println("orders_x size >" + orders_x.size());


    if (courseReportLister.showExcel())
    {
		
		BigDecimal one_hundred = new BigDecimal(100);

		int row_index = 7;
		short column_index = 0;
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + "Commission Report.xls"));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);

		courseReportLister.addCell(sheet, 0, (short)0, adminCompany.getLabel());
		courseReportLister.addCell(sheet, 2, (short)0, courseReportLister.getStartDateString() + " - " + courseReportLister.getEndDateString());

		BigDecimal total_cost = new BigDecimal(0);
		BigDecimal total_commission = new BigDecimal(0);
		BigDecimal total_ppa = new BigDecimal(0);
			
		String last_department_str = null;
		BigDecimal commission_subtotal = BigDecimal.ZERO;
		BigDecimal ppa_subtotal = BigDecimal.ZERO;
		BigDecimal cost_subtotal = BigDecimal.ZERO;
		BigDecimal amount_per_appt = new BigDecimal(1.50);
		if (practitioner.getFirstNameString().equals("Cara")) {
			amount_per_appt = new BigDecimal(1.50);
		} else if (practitioner.getFirstNameString().equals("Michelle")) {
			amount_per_appt = new BigDecimal(2.50);
		} else if (practitioner.getFirstNameString().equals("Stacey")) {
			amount_per_appt = new BigDecimal(1.50);
		}
		
		HSSFCellStyle style = courseReportLister.getStyle(sheet, 3, (short)0);
		HSSFCellStyle report_label_style = courseReportLister.getStyle(sheet, 1, (short)0);
		HSSFCellStyle right_style = courseReportLister.getStyle(sheet, 8, (short)9);
		HSSFCellStyle total_style = courseReportLister.getStyle(sheet, 8, (short)0);
		HSSFCellStyle total_total_style = courseReportLister.getStyle(sheet, 8, (short)0);
		total_style.setBorderBottom((short)0);
		total_style.setBorderLeft((short)0);
		
		courseReportLister.addCell(sheet, 0, (short)0, adminCompany.getLabel(), report_label_style);
		courseReportLister.addCell(sheet, 1, (short)0, report_label, report_label_style);

		Iterator orders_x_itr = orders_x.iterator();
		while (orders_x_itr.hasNext())
		{
			CheckoutOrderline obj = (CheckoutOrderline)orders_x_itr.next();
			OrderBean order = OrderBean.getOrder(obj.getOrderid());
			String client_label = "[NOT FOUND]";
			if (order.getPersonId() > 0)
			{
				UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(order.getPersonId());
				client_label = client.getLabel();
			}
			CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(obj.getCheckoutCodeId());
			String prac_label = "[NOT FOUND]";
			String commission_perc_str = "0.00";
			if (obj.getPractitionerId() > 0)
			{
				UKOnlinePersonBean practitioner_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(obj.getPractitionerId());
				prac_label = practitioner_obj.getLabel();
				
				/*
				int department_id = checkout_code.getDepartmentId();
				if (department_id > 0)
				{
					InventoryDepartment dept = InventoryDepartment.getInventoryDepartment(department_id);
					commission_perc_str = practitioner_obj.getCommissionPercentageString(dept);
				}
				*/
			}
			
			total_cost = total_cost.add(obj.getPrice());
			
			
				
			String department_str = checkout_code.getDepartmentString();
				
			if (last_department_str != null && !department_str.equals(last_department_str))
			{
				//courseReportLister.shiftRow(sheet, row_index + 2);
				courseReportLister.addCell(sheet, row_index, column_index, last_department_str + " Total", total_style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, cost_subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, commission_subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, ppa_subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
				
				commission_subtotal = BigDecimal.ZERO;
				ppa_subtotal = BigDecimal.ZERO;
				cost_subtotal = BigDecimal.ZERO;

				row_index++;
				column_index = 0;
			}
			
			String commission_str = "0.00";
			if (obj.getCommission() != null)
			{
				commission_str = obj.getCommission().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
				commission_subtotal = commission_subtotal.add(obj.getCommission());
				ppa_subtotal = ppa_subtotal.add(amount_per_appt);
				
				total_commission = total_commission.add(obj.getCommission());
				total_ppa = total_ppa.add(amount_per_appt);
				
				if (obj.getPrice().compareTo(BigDecimal.ZERO) != 0)
				{
					BigDecimal subtotal = obj.getPrice().multiply(obj.getQuantity());
					if (subtotal.compareTo(BigDecimal.ZERO) != 0)
						commission_perc_str = obj.getCommission().multiply(one_hundred).divide(subtotal, 2, RoundingMode.HALF_UP).toString();
				}
			}
			
			cost_subtotal = cost_subtotal.add(obj.getPrice());

			last_department_str = department_str;

			//courseReportLister.shiftRow(sheet, row_index + 2);
			courseReportLister.addCell(sheet, row_index, column_index, department_str, style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, checkout_code.getVendorString(), style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, client_label, style); column_index++;
			if (dates_equal)
			{
				courseReportLister.addCell(sheet, row_index, column_index, CUBean.getUserTimeString(order.getOrderDate()), style); column_index++; column_index++;
			}
			else
			{
				courseReportLister.addCell(sheet, row_index, column_index, CUBean.getUserDateString(order.getOrderDate()), style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, CUBean.getUserTimeString(order.getOrderDate()), style); column_index++;
			}
			courseReportLister.addCell(sheet, row_index, column_index, checkout_code.getLabel(), style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, prac_label, style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, commission_perc_str, style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, obj.getQuantity().setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, obj.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, commission_str, right_style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, amount_per_appt.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;

			row_index++;
			column_index = 0;
		}
		
		
		
		//courseReportLister.shiftRow(sheet, row_index + 2);
		courseReportLister.addCell(sheet, row_index, column_index, last_department_str + " Total", total_style); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, "", style);
		column_index++;
		column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, cost_subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, commission_subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, ppa_subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
		
		
		courseReportLister.addCell(sheet, row_index + 2, (short)0, "Total", total_total_style);
		courseReportLister.addCell(sheet, row_index + 2, (short)9, total_cost.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style);
		courseReportLister.addCell(sheet, row_index + 2, (short)10, total_commission.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style);
		courseReportLister.addCell(sheet, row_index + 2, (short)11, total_ppa.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style);

		commission_subtotal = BigDecimal.ZERO;
		ppa_subtotal = BigDecimal.ZERO;
		cost_subtotal = BigDecimal.ZERO;
		

		/*
		courseReportLister.addCell(sheet, row_index + 2, (short)4, total_charges.setScale(2, BigDecimal.ROUND_HALF_UP).toString()); column_index++;
		courseReportLister.addCell(sheet, row_index + 2, (short)5, total_payments.setScale(2, BigDecimal.ROUND_HALF_UP).toString()); column_index++;
		courseReportLister.addCell(sheet, row_index + 2, (short)6, total_credits.setScale(2, BigDecimal.ROUND_HALF_UP).toString()); column_index++;
		*/

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


<div id="content" style="height: 100%;">

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>


    <div id="center1" style="background-color: white;">

	    <div id="content">

		<div class="content_TextBlock">
			<p class="headline"><%= adminCompany.getLabel() %> Administration - Commission Report</p>
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
								<td style="width:  90px; text-align: left;  "><a href="report-commission.jsp?sort=<%= (courseReportLister.getSort() == (short)1) ? 2 : 1  %>" title="">Department</a></td>
								<td style="width: 104px; text-align: left;  "><a href="report-commission.jsp?sort=<%= (courseReportLister.getSort() == (short)3) ? 4 : 3  %>" title="">Vendor</a></td>
								<td style="width:  94px; text-align: left;  "><a href="report-commission.jsp?sort=<%= (courseReportLister.getSort() == (short)15) ? 16 : 15  %>" title="">Client</a></td>
								<td style="width: 160px; text-align: left;  "><a href="report-commission.jsp?sort=<%= (courseReportLister.getSort() == (short)7) ? 8 : 7  %>" title="">Description</a></td>
								<td style="width:  94px; text-align: left;  "><a href="report-commission.jsp?sort=<%= (courseReportLister.getSort() == (short)13) ? 14 : 13  %>" title="">Practitioner</a></td>
								<td style="width:  46px; text-align: right; "><a href="report-commission.jsp?sort=<%= (courseReportLister.getSort() == (short)5) ? 6 : 5  %>" title="Quantity">Qty</a></td>
								<td style="width:  54px; text-align: right; "><a href="report-commission.jsp?sort=<%= (courseReportLister.getSort() == (short)9) ? 10 : 9  %>" title="Cost w/o Tax">Cost</a></td>
								<td style="width:  54px; text-align: right; "><a href="report-commission.jsp?sort=<%= (courseReportLister.getSort() == (short)11) ? 12 : 11  %>" title="Commission Amount">Comm.</a></td>
							</tr>
						</table>
					</div>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

<%

		if (orders_x.size() > 0)
		{
%>
					<!-- BEGIN Report Entry -->
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
<%
			String last_department_str = null;
			BigDecimal commission_subtotal = BigDecimal.ZERO;
			
			Iterator itr = orders_x.iterator();
			while (itr.hasNext())
			{
				CheckoutOrderline obj = (CheckoutOrderline)itr.next();
				OrderBean order = OrderBean.getOrder(obj.getOrderid());
				String client_label = "[NOT FOUND]";
				String client_value = "-1";
				if (order.getPersonId() > 0)
				{
					UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(order.getPersonId());
					client_label = client.getLabel();
					client_value = client.getValue();
				}
				CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(obj.getCheckoutCodeId());
				String prac_label = "[NOT FOUND]";
				String prac_value = "-1";
				if (obj.getPractitionerId() > 0)
				{
					UKOnlinePersonBean practitioner_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(obj.getPractitionerId());
					prac_label = practitioner_obj.getLabel();
					prac_value = practitioner_obj.getValue();
				}
				
				String department_str = checkout_code.getDepartmentString();
				
				if (last_department_str != null && !department_str.equals(last_department_str))
				{
%>
						</table>
						
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
						
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:  90px; text-align: left; vertical-align: top;  "><strong><%= last_department_str %></strong></td>
								<td style="width: 104px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width:  94px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width: 160px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width:  94px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width:  46px; text-align: right; vertical-align: top; ">&nbsp;</td>
								<td style="width:  54px; text-align: right; vertical-align: top; ">&nbsp;</td>
								<td style="width:  54px; text-align: right; vertical-align: bottom; "><strong><%= commission_subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></strong></td>
							</tr>
						</table>
							
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td colspan="8">&nbsp;</td>
							</tr>
<%
					commission_subtotal = BigDecimal.ZERO;
				}
				
				last_department_str = department_str;
				
				
				String commission_str = "0.00";
				if (obj.getCommission() != null)
				{
					commission_str = obj.getCommission().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
					commission_subtotal = commission_subtotal.add(obj.getCommission());
				}
				
%>

							<tr>
								<td style="width:  90px; text-align: left; vertical-align: top;  "><%= department_str %></td>
								<td style="width: 104px; text-align: left; vertical-align: top;  "><%= checkout_code.getVendorString() %></td>
<%

if (client_value.equals("-1"))
{
%>
								<td style="width:  94px; text-align: left; vertical-align: top;  "><%= client_label %></td>
<%
}
else
{
%>
								<td style="width:  94px; text-align: left; vertical-align: top;  "><a href="clients.jsp?id=<%= client_value %>"><%= client_label %></a></td>
<%
}

if (logged_in_person.hasRole(UKOnlineRoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) || logged_in_person.hasRole(UKOnlineRoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
{
%>
								<td style="width: 160px; text-align: left; vertical-align: top;  "><a href="schedule-administration-checkout-code.jsp?id=<%= checkout_code.getValue() %>"><%= checkout_code.getLabel() %></a></td>
<%
}
else
{
%>
								<td style="width: 160px; text-align: left; vertical-align: top;  "><%= checkout_code.getLabel() %></td>
<%
}

if (logged_in_person.hasRole(UKOnlineRoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) || logged_in_person.hasRole(UKOnlineRoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
{
	if (prac_value.equals("-1"))
	{
%>
								<td style="width:  94px; text-align: left; vertical-align: top;  "><%= prac_label %></td>
<%
	}
	else
	{
%>
								<td style="width:  94px; text-align: left; vertical-align: top;  "><a href="setup_02.jsp?id=<%= prac_value %>"><%= prac_label %></a></td>
<%
	}
}
else
{
%>
								<td style="width:  94px; text-align: left; vertical-align: top;  "><%= prac_label %></td>
<%
}
%>
								<td style="width:  46px; text-align: right; vertical-align: top; "><%= obj.getQuantity().setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  54px; text-align: right; vertical-align: top; "><%= obj.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  54px; text-align: right; vertical-align: top; "><%= commission_str %></td>
							</tr>

<%
					
				
			}
%>
						</table>
						
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
						
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							
							<tr>
								<td style="width:  90px; text-align: left; vertical-align: top;  "><strong><%= last_department_str %></strong></td>
								<td style="width: 104px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width:  94px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width: 160px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width:  94px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width:  46px; text-align: right; vertical-align: top; ">&nbsp;</td>
								<td style="width:  54px; text-align: right; vertical-align: top; ">&nbsp;</td>
								<td style="width:  54px; text-align: right; vertical-align: bottom; "><strong><%= commission_subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></strong></td>
							</tr>
							
						</table>
						
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
						
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

					<!-- <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div> -->
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


    </div>


</div>



</body>



</html>