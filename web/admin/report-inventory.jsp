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

Vector inventory_codes = new Vector();
Vector orders_x = new Vector();
HashMap starting_balance_hash = new HashMap(37);

boolean dates_equal = false;

MathContext mc = new MathContext(2, RoundingMode.HALF_UP);
	
Vector departments = new Vector();
Vector codes = new Vector();

BigDecimal grand_cost_total = BigDecimal.ZERO;
BigDecimal grand_price_total = BigDecimal.ZERO;
	
HashMap<Integer,BigDecimal> code_id_to_qty_hash = new HashMap<Integer,BigDecimal>(99);
HashMap<Integer,BigDecimal> code_id_to_total_hash = new HashMap<Integer,BigDecimal>(99);
HashMap<Integer,BigDecimal> code_id_to_cost_hash = new HashMap<Integer,BigDecimal>(99);

String report_label = "Inventory Report";

try
{
	/*
    Date start_date = courseReportLister.getStartDate();
    Date end_date = courseReportLister.getEndDate();

	Calendar start_cal = Calendar.getInstance();
	start_cal.setTime(start_date);
	Calendar end_cal = Calendar.getInstance();
	end_cal.setTime(end_date);

	dates_equal = (start_cal.get(Calendar.DATE) == end_cal.get(Calendar.DATE)) && (start_cal.get(Calendar.MONTH) == end_cal.get(Calendar.MONTH)) && (start_cal.get(Calendar.YEAR) == end_cal.get(Calendar.YEAR));
	*/
	
	Criteria crit = new Criteria();
	//crit.addJoin(InventoryDepartmentDbPeer.INVENTORY_DEPARTMENT_DB_ID, CheckoutCodePeer.INVENTORY_DEPARTMENT_DB_ID);
	crit.add(CheckoutCodePeer.TYPE, CheckoutCodeBean.INVENTORY_TYPE);
	crit.add(CheckoutCodePeer.ON_HAND_QUANTITY, 0, Criteria.GREATER_THAN);
	crit.add(CheckoutCodePeer.COMPANY_ID, adminCompany.getId());
	//crit.addAscendingOrderByColumn(InventoryDepartmentDbPeer.DEPARTMENT);
	crit.addAscendingOrderByColumn(CheckoutCodePeer.INVENTORY_DEPARTMENT_DB_ID);
	crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
	System.out.println("crit >" + crit.toString());
    Iterator obj_itr = CheckoutCodePeer.doSelect(crit).iterator();

    while (obj_itr.hasNext())
    {
		CheckoutCode obj = (CheckoutCode)obj_itr.next();
		inventory_codes.addElement(CheckoutCodeBean.getCheckoutCode(obj));
    }
	
	/*
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
	
	crit.addAscendingOrderByColumn(InventoryDepartmentDbPeer.DEPARTMENT);
	crit.addAscendingOrderByColumn(CheckoutCodePeer.CHECKOUT_CODE_ID);
	//crit.addAscendingOrderByColumn(PersonorderPeer.ORDERDATE);
	
	System.out.println("crit >" + crit.toString());
    Iterator obj_itr = CheckoutOrderlinePeer.doSelect(crit).iterator();
    while (obj_itr.hasNext())
    {
		CheckoutOrderline obj = (CheckoutOrderline)obj_itr.next();
		orders_x.addElement(obj);
    }
	*/
	
	System.out.println("inventory_codes size >" + inventory_codes.size());
	
	
	
	Iterator inventory_itr = inventory_codes.iterator();
	while (inventory_itr.hasNext())
	{
		CheckoutCodeBean checkout_code = (CheckoutCodeBean)inventory_itr.next();
		
		String department_str = checkout_code.getDepartmentString();
		if (!departments.contains(department_str))
			departments.addElement(department_str);
		
		if (!codes.contains(checkout_code))
			codes.addElement(checkout_code); 
		
		
		Integer code_id = new Integer(checkout_code.getId());
		BigDecimal on_hand_qty = new BigDecimal(checkout_code.getOnHandQuantity());
		BigDecimal inventory_amount = checkout_code.getAmount().multiply(on_hand_qty);
		BigDecimal inventory_cost = checkout_code.getOrderCost().multiply(on_hand_qty);
		
		
		BigDecimal qty = code_id_to_qty_hash.get(code_id);
		if (qty == null)
			qty = on_hand_qty;
		else
			qty = qty.add(on_hand_qty);
		code_id_to_qty_hash.put(code_id, qty);
		
		
		
		BigDecimal amount = code_id_to_total_hash.get(code_id);
		if (amount == null)
			amount = inventory_amount;
		else
			amount = amount.add(inventory_amount);
		code_id_to_total_hash.put(code_id, amount);
		
		
	
		BigDecimal cost = code_id_to_cost_hash.get(code_id);
		if (cost == null)
			cost = inventory_cost;
		else
			cost = cost.add(inventory_cost);
		code_id_to_cost_hash.put(code_id, cost);
		
	}
	
	/*
	Iterator orders_x_itr = orders_x.iterator();
	while (orders_x_itr.hasNext())
	{
		CheckoutOrderline obj = (CheckoutOrderline)orders_x_itr.next();
		CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(obj.getCheckoutCodeId());
		
		String department_str = checkout_code.getDepartmentString();
		if (!departments.contains(department_str))
			departments.addElement(department_str);
		
		if (!codes.contains(checkout_code))
			codes.addElement(checkout_code); 
		
		
		Integer code_id = new Integer(obj.getCheckoutCodeId());
		BigDecimal orderline_qty = obj.getQuantity();
		BigDecimal orderline_amount = obj.getActualAmount();
		BigDecimal orderline_cost = obj.getCost();
		if (orderline_cost == null)
			orderline_cost = checkout_code.getOrderCost();
		
		
		
		BigDecimal qty = code_id_to_qty_hash.get(code_id);
		if (qty == null)
			qty = orderline_qty;
		else
			qty = qty.add(orderline_qty);
		code_id_to_qty_hash.put(code_id, qty);
		
		
		
		BigDecimal amount = code_id_to_total_hash.get(code_id);
		if (amount == null)
			amount = orderline_amount;
		else
			amount = amount.add(orderline_amount);
		code_id_to_total_hash.put(code_id, amount);
		
		
	
		BigDecimal cost = code_id_to_cost_hash.get(code_id);
		if (cost == null)
			cost = orderline_cost;
		else
			cost = cost.add(orderline_cost);
		code_id_to_cost_hash.put(code_id, cost);
		
	}
	*/


    if (courseReportLister.showExcel() || (1 == 1))
    {
		
		BigDecimal one_hundred = new BigDecimal(100);

		int row_index = 7;
		short column_index = 0;
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + "Commission Report.xls"));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);

		courseReportLister.addCell(sheet, 0, (short)0, adminCompany.getLabel());
		courseReportLister.addCell(sheet, 2, (short)0, courseReportLister.getStartDateString() + " - " + courseReportLister.getEndDateString());

		//BigDecimal total_cost = new BigDecimal(0);
		BigDecimal total_commission = new BigDecimal(0);
			
		String last_department_str = null;
		BigDecimal commission_subtotal = BigDecimal.ZERO;
		
		HSSFCellStyle style = courseReportLister.getStyle(sheet, 3, (short)0);
		HSSFCellStyle report_label_style = courseReportLister.getStyle(sheet, 1, (short)0);
		HSSFCellStyle right_style = courseReportLister.getStyle(sheet, 8, (short)9);
		HSSFCellStyle total_style = courseReportLister.getStyle(sheet, 8, (short)0);
		HSSFCellStyle total_total_style = courseReportLister.getStyle(sheet, 8, (short)0);
		total_style.setBorderBottom((short)0);
		total_style.setBorderLeft((short)0);
		
		courseReportLister.addCell(sheet, 0, (short)0, adminCompany.getLabel(), report_label_style);
		courseReportLister.addCell(sheet, 1, (short)0, report_label, report_label_style);
		
		courseReportLister.addCell(sheet, 4, (short)0, "Goods Sold", total_style);
		
		courseReportLister.addCell(sheet, 6, (short)0, "Description", total_style);
		courseReportLister.addCell(sheet, 6, (short)1, "Qty", total_style);
		courseReportLister.addCell(sheet, 6, (short)2, "Total Cost", total_style);
		courseReportLister.addCell(sheet, 6, (short)3, "Avg. Cost", total_style);
		courseReportLister.addCell(sheet, 6, (short)4, "Total Sales", right_style);
		courseReportLister.addCell(sheet, 6, (short)5, "Avg. Sale", right_style);
		courseReportLister.addCell(sheet, 6, (short)6, "", right_style);
		courseReportLister.addCell(sheet, 6, (short)7, "", right_style);
		courseReportLister.addCell(sheet, 6, (short)8, "", right_style);
		courseReportLister.addCell(sheet, 6, (short)9, "", right_style);
		courseReportLister.addCell(sheet, 6, (short)10, "", right_style);
		
		courseReportLister.addCell(sheet, 8, (short)9, "", right_style);
		courseReportLister.addCell(sheet, 8, (short)10, "", right_style);
		
		
		BigDecimal subtotal = BigDecimal.ZERO;
		BigDecimal subtotal_cost = BigDecimal.ZERO;

		if (inventory_codes.size() > 0)
		{

			last_department_str = null;
			commission_subtotal = BigDecimal.ZERO;
			
			//Iterator itr = orders_x.iterator();
			Iterator itr = inventory_codes.iterator();
			while (itr.hasNext())
			{
				column_index = 0;
				
				CheckoutCodeBean checkout_code = (CheckoutCodeBean)itr.next();
				String department_str = checkout_code.getDepartmentString();
				
				if (last_department_str != null && !department_str.equals(last_department_str))
				{
					courseReportLister.shiftRow(sheet, row_index + 2);
					courseReportLister.addCell(sheet, row_index, column_index, last_department_str + " Total", total_style); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, subtotal_cost.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
					courseReportLister.addCell(sheet, row_index, column_index, subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
					
					grand_cost_total = grand_cost_total.add(subtotal_cost);
					grand_price_total = grand_price_total.add(subtotal);
					
					commission_subtotal = BigDecimal.ZERO;
					subtotal = BigDecimal.ZERO;
					subtotal_cost = BigDecimal.ZERO;
					
					column_index = 0;
					row_index++;
					row_index++;
				}
				
				last_department_str = department_str;
				
				Integer key_x = new Integer(checkout_code.getId());
				
				BigDecimal qty = code_id_to_qty_hash.get(key_x);
				BigDecimal total = code_id_to_total_hash.get(key_x);
				BigDecimal total_cost = code_id_to_cost_hash.get(key_x);
				
				BigDecimal avg = BigDecimal.ZERO;
				if (qty.compareTo(BigDecimal.ZERO) == 1)
					avg = total.divide(qty, 2, RoundingMode.HALF_UP);
				
				BigDecimal avg_cost = BigDecimal.ZERO;
				if (qty.compareTo(BigDecimal.ZERO) == 1)
					avg_cost = total_cost.divide(qty, 2, RoundingMode.HALF_UP);
				
				subtotal = subtotal.add(total);
				subtotal_cost = subtotal_cost.add(total_cost);

				courseReportLister.shiftRow(sheet, row_index + 2);
				courseReportLister.addCell(sheet, row_index, column_index, checkout_code.getLabel(), style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, qty.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, avg_cost.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, total_cost.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, avg.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
				courseReportLister.addCell(sheet, row_index, column_index, total.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
				
				row_index++;
				column_index = 0;
			}
			
			
			courseReportLister.shiftRow(sheet, row_index + 2);
			courseReportLister.addCell(sheet, row_index, column_index, last_department_str + " Total", total_style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, subtotal_cost.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
			
			row_index++;
			column_index = 0;

			grand_cost_total = grand_cost_total.add(subtotal_cost);
			grand_price_total = grand_price_total.add(subtotal);
			
			
			courseReportLister.shiftRow(sheet, row_index + 2);
			courseReportLister.addCell(sheet, row_index, column_index, "Grand Total", total_style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, grand_cost_total.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, "", style); column_index++;
			courseReportLister.addCell(sheet, row_index, column_index, grand_price_total.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), right_style); column_index++;
					

		}
		
		

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

System.out.println("GOT HERE 1");
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
			<p class="headline"><%= adminCompany.getLabel() %> Administration - Goods Sold Report</p>
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
	if (courseReportLister.showExcel() || (1 == 1))
	{
%>
					<p style="background: white;">Download Link:&nbsp;&nbsp;<a href="<%= "../resources/" + saveFilename %>" target="_blank" title=""><%= saveFilename %></a></p>
<%
	}
%>
				</div>
<%


	if (courseReportLister.showHTML() || (1 == 1))
	{
%>
				<div class="content_Table">

					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
					<div class="heading">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr style="display: block;">
								<td style="width: 300px; text-align: left;  "><a href="report-inventory.jsp?sort=<%= (courseReportLister.getSort() == (short)1) ? 2 : 1  %>" title="">Description</a></td>
								<td style="width:  66px; text-align: left;  "><a href="report-inventory.jsp?sort=<%= (courseReportLister.getSort() == (short)3) ? 4 : 3  %>" title="">On Hand Qty</a></td>
								<td style="width:  66px; text-align: left;  "><a href="report-inventory.jsp?sort=<%= (courseReportLister.getSort() == (short)15) ? 16 : 15  %>" title="">Cost</a></td>
								<td style="width:  66px; text-align: left;  "><a href="report-inventory.jsp?sort=<%= (courseReportLister.getSort() == (short)15) ? 16 : 15  %>" title="">Total Cost</a></td>
								<td style="width:  66px; text-align: left;  "><a href="report-inventory.jsp?sort=<%= (courseReportLister.getSort() == (short)15) ? 16 : 15  %>" title="">Price</a></td>
								<td style="width:  66px; text-align: left;  "><a href="report-inventory.jsp?sort=<%= (courseReportLister.getSort() == (short)15) ? 16 : 15  %>" title="">Total Price</a></td>
								<td style="width:  66px; text-align: left;  ">&nbsp;</td>
							</tr>
						</table>
					</div>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

<%
		
		BigDecimal subtotal = BigDecimal.ZERO;
		BigDecimal subtotal_cost = BigDecimal.ZERO;

		if (inventory_codes.size() > 0)
		{
%>
					<!-- BEGIN Report Entry -->
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
<%
			String last_department_str = null;
			BigDecimal commission_subtotal = BigDecimal.ZERO;
			
			//Iterator itr = orders_x.iterator();
			Iterator itr = codes.iterator();
			while (itr.hasNext())
			{
				CheckoutCodeBean checkout_code = (CheckoutCodeBean)itr.next();
				String department_str = checkout_code.getDepartmentString();
				
				if (last_department_str != null && !department_str.equals(last_department_str))
				{
%>
						</table>
						
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
						
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width: 300px; text-align: left; vertical-align: top;  "><strong><%= last_department_str %></strong></td>
								<td style="width:  66px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; ">&nbsp;</td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; "><strong><%= subtotal_cost.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></strong></td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; ">&nbsp;</td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; "><strong><%= subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></strong></td>
								<td style="width:  68px; text-align: left;  ">&nbsp;</td>
							</tr>
						</table>
							
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td colspan="3">&nbsp;</td>
							</tr>
<%

					commission_subtotal = BigDecimal.ZERO;
					subtotal = BigDecimal.ZERO;
					subtotal_cost = BigDecimal.ZERO;
					
					
				}
				
				last_department_str = department_str;
				
				Integer key_x = new Integer(checkout_code.getId());
				
				BigDecimal qty = code_id_to_qty_hash.get(key_x);
				BigDecimal total = code_id_to_total_hash.get(key_x);
				BigDecimal total_cost = code_id_to_cost_hash.get(key_x);
				
				BigDecimal avg = BigDecimal.ZERO;
				if (qty.compareTo(BigDecimal.ZERO) == 1)
					avg = total.divide(qty, 2, RoundingMode.HALF_UP);
				
				BigDecimal avg_cost = BigDecimal.ZERO;
				if (qty.compareTo(BigDecimal.ZERO) == 1)
					avg_cost = total_cost.divide(qty, 2, RoundingMode.HALF_UP);
				
				subtotal = subtotal.add(total);
				subtotal_cost = subtotal_cost.add(total_cost);
%>

							<tr>
								<td style="width: 300px; text-align: left; vertical-align: top;  "><a href="schedule-administration-checkout-code.jsp?id=<%= checkout_code.getValue() %>"><%= checkout_code.getLabel() %></a></td>
								<td style="width:  66px; text-align: right; vertical-align: top;  "><%= qty.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  66px; text-align: right; vertical-align: top; "><%= avg_cost.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  66px; text-align: right; vertical-align: top; "><%= total_cost.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  66px; text-align: right; vertical-align: top; "><%= avg.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  66px; text-align: right; vertical-align: top; "><%= total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></td>
								<td style="width:  68px; text-align: left;  ">&nbsp;</td>
							</tr>

<%
					
				
			}
%>
						</table>
						
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
						
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							
							<tr>
								<td style="width: 300px; text-align: left; vertical-align: top;  "><strong><%= last_department_str %></strong></td>
								<td style="width:  66px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; ">&nbsp;</td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; "><strong><%= subtotal_cost.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></strong></td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; ">&nbsp;</td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; "><strong><%= subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></strong></td>
								<td style="width:  68px; text-align: left;  ">&nbsp;</td>
							</tr>
							
						</table>
						
<%

			
%>
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							
							<tr>
								<td colspan="7" style="width:  68px; text-align: left;  ">&nbsp;</td>
							</tr>
							
						</table>
								
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
						
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							
							<tr>
								<td style="width: 300px; text-align: left; vertical-align: top;  "><strong>GRAND TOTAL</strong></td>
								<td style="width:  66px; text-align: left; vertical-align: top;  ">&nbsp;</td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; ">&nbsp;</td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; "><strong><%= grand_cost_total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></strong></td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; ">&nbsp;</td>
								<td style="width:  66px; text-align: right; vertical-align: bottom; "><strong><%= grand_price_total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() %></strong></td>
								<td style="width:  68px; text-align: left;  ">&nbsp;</td>
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
								<td style="width: 696px; text-align: left;  " colspan="8">No Results Found</td>
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