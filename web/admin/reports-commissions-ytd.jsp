<%@ page language="java" buffer="12kb" contentType="text/html" import="org.apache.torque.util.*, com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*;" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<%

Vector months = new Vector();
months.addElement("January");
months.addElement("February");
months.addElement("March");
months.addElement("April");
months.addElement("May");
months.addElement("June");
months.addElement("July");
months.addElement("August");
months.addElement("September");
months.addElement("October");
months.addElement("November");
months.addElement("December");

Vector practitioners = UKOnlinePersonBean.getPractitioners(adminCompany);

Calendar start_cal = Calendar.getInstance();
start_cal.set(Calendar.MONTH, Calendar.JANUARY);
start_cal.set(Calendar.DATE, 1);

Calendar end_cal = Calendar.getInstance();

HashMap month_order_hash = new HashMap(13);

try
{
    Criteria crit = new Criteria();
    crit.add(PersonorderPeer.COMPANY_ID, adminCompany.getId());
    crit.add(PersonorderPeer.ORDERDATE, start_cal.getTime(), Criteria.GREATER_EQUAL);
    crit.and(PersonorderPeer.ORDERDATE, end_cal.getTime(), Criteria.LESS_EQUAL);
    crit.addAscendingOrderByColumn(PersonorderPeer.ORDERDATE);
	System.out.println("crit >" + crit.toString());
    Iterator obj_itr = PersonorderPeer.doSelect(crit).iterator();
    while (obj_itr.hasNext())
    {
		Personorder obj = (Personorder)obj_itr.next();
		OrderBean order_obj = ValeoOrderBean.getOrder(obj.getOrderid());
		Iterator order_lines_itr = order_obj.getOrders();
		while (order_lines_itr.hasNext())
		{
			CheckoutOrderline order_line = (CheckoutOrderline)order_lines_itr.next();
			try
			{
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(order_line.getPractitionerId());
				BigDecimal commission = order_line.getCommission();

				Calendar order_date = Calendar.getInstance();
				order_date.setTime(order_obj.getOrderDate());

				String display_month = order_date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
				HashMap practitioner_hash = (HashMap)month_order_hash.get(display_month);
				if (practitioner_hash == null)
				{
					practitioner_hash = new HashMap(11);
					month_order_hash.put(display_month, practitioner_hash);
				}
				BigDecimal commission_total = (BigDecimal)practitioner_hash.get(practitioner);
				if (commission_total == null)
					commission_total = new BigDecimal(0);

				commission_total = commission_total.add(commission);
				practitioner_hash.put(practitioner, commission_total);
			}
			catch (ObjectNotFoundException x)
			{
			}
		}

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

<link rel="stylesheet" type="text/css" href="../css/schedule-client.css" />
<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/resize/assets/skins/sam/resize.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/layout/assets/skins/sam/layout.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" />

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

<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/json/json-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/datasource/datasource-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/charts/charts-min.js"></script>

<style type="text/css">
	#chart
	{
		width: 500px;
		height: 350px;
	}

	.chart_title
	{
		display: block;
		font-size: 1.2em;
		font-weight: bold;
		margin-bottom: 0.4em;
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


        <div id="center1" style="background-color: white; height: 100%;">


		<div id="content">


			<div class="content_TextBlock">
				<p class="headline"><%= adminCompany.getLabel() %> Reports - Year To Date Visits</p>
				<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			</div>

			<div class="content_Administration">

<%@ include file="channels\channel-report-menu.jsp" %>

				<div class="main" style="width: 692px;">


					<div class="content_Table">
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td>

<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

<span class="chart_title">Year To Date Commissions</span>
<div id="chart">Unable to load Flash content. The YUI Charts Control requires Flash Player 9.0.45 or higher. You can download the latest version of Flash Player from the <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.</p></div>

<script type="text/javascript">

	YAHOO.widget.Chart.SWFURL = "https://www.valonyx.com/yui/build/charts/assets/charts.swf";

//--- data

	YAHOO.example.monthlyExpenses =
	[
<%
String current_display_month = end_cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
Iterator month_str_itr = months.iterator();
while (month_str_itr.hasNext())
{
    String display_month = (String)month_str_itr.next();
    System.out.println("display_month >" + display_month);
    String practitioner_str = "";
    //System.out.println("display_month >" + display_month);
    HashMap hash = (HashMap)month_order_hash.get(display_month);
    //System.out.println("hash >" + hash);
    Iterator practitioner_itr = practitioners.iterator();
    while (practitioner_itr.hasNext())
    {
		UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
		System.out.println("practitioner >" + practitioner.getLabel());
		System.out.println("hash >" + hash);
		if (hash != null)
		{
			BigDecimal commission = (BigDecimal)hash.get(practitioner);
			System.out.println("commission >" + commission);
			if (commission == null)
				practitioner_str += ", " + practitioner.getLastNameString().toLowerCase() + ": 0.00";
			else
				practitioner_str += ", " + practitioner.getLastNameString().toLowerCase() + ": " + commission.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		}
    }
%>
		{ month: "<%= display_month %>"<%= practitioner_str %> }<%= display_month.equals(current_display_month) ? "" : "," %>
<%
    if (display_month.equals(current_display_month))
	break;
}
%>
	];

	var myDataSource = new YAHOO.util.DataSource( YAHOO.example.monthlyExpenses );
	myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	myDataSource.responseSchema =
	{
<%
String p_label_str = "";
Iterator practitioners_itr = practitioners.iterator();
while (practitioners_itr.hasNext())
{
    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners_itr.next();
    p_label_str += ", \"" + practitioner.getLastNameString().toLowerCase() + "\"";
}
%>
		fields: [ "month"<%= p_label_str %> ]
	};

//--- chart

	//series definition for chart
	var seriesDef =
	[
<%
practitioners_itr = practitioners.iterator();
while (practitioners_itr.hasNext())
{
    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioners_itr.next();
%>
		{
			displayName: "<%= practitioner.getLabel() %>",
			yField: "<%= practitioner.getLastNameString().toLowerCase() %>",
			style:{size:10}
		}<%= practitioners_itr.hasNext() ? "," : "" %>
<%
}
%>
	];

	//Style object for chart
	var styleDef =
	{
		xAxis:
		{
			labelRotation:-90
		},
		yAxis:
		{
			titleRotation:-90
		}
	}

	//format currency
	YAHOO.example.formatCurrencyAxisLabel = function( value )
	{
		return YAHOO.util.Number.format( value,
		{
			prefix: "$",
			thousandsSeparator: ",",
			decimalPlaces: 0
		});
	}

	//DataTip function for the chart
	YAHOO.example.getDataTipText = function( item, index, series )
	{
		var toolTipText = series.displayName + " for " + item.month;
		toolTipText += "\n" + YAHOO.example.formatCurrencyAxisLabel( item[series.yField] );
		return toolTipText;
	}

	//create a Numeric Axis for displaying dollars
	var currencyAxis = new YAHOO.widget.NumericAxis();
	currencyAxis.labelFunction = YAHOO.example.formatCurrencyAxisLabel;
	currencyAxis.title = "Commission";

	//create Category Axis to specify a title for the months
	var categoryAxis = new YAHOO.widget.CategoryAxis();
	categoryAxis.title = "Month";

	//create a Chart
	var mychart = new YAHOO.widget.ColumnChart( "chart", myDataSource,
	{
		series: seriesDef,
		xField: "month",
		yAxis: currencyAxis,
		xAxis: categoryAxis,
		style: styleDef,
		dataTipFunction: YAHOO.example.getDataTipText,
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


					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
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






<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
