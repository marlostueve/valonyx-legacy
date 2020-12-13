<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*;" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<%

Vector appointments = null;
HashMap practice_area_appt_hash = new HashMap(5);

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

Vector practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany);

boolean dates_equal = false;

Calendar start_cal = Calendar.getInstance();
//start_cal.setTime(start_date);
start_cal.set(Calendar.MONTH, Calendar.JANUARY);
start_cal.set(Calendar.DATE, 1);

Calendar end_cal = Calendar.getInstance();
//end_cal.setTime(end_date);

dates_equal = (start_cal.get(Calendar.DATE) == end_cal.get(Calendar.DATE)) && (start_cal.get(Calendar.MONTH) == end_cal.get(Calendar.MONTH)) && (start_cal.get(Calendar.YEAR) == end_cal.get(Calendar.YEAR));



if (dates_equal)
    appointments = AppointmentBean.getAppointmentsForDate(adminCompany, start_cal.getTime());
else
    appointments = AppointmentBean.getAppointmentsForDate(adminCompany, start_cal.getTime(), end_cal.getTime());

System.out.println("num appts >" + appointments.size());


Iterator itr = appointments.iterator();
while (itr.hasNext())
{
    AppointmentBean appointment = (AppointmentBean)itr.next();
    if (appointment.isClientAppointment() && (appointment.isCheckedOut() || appointment.isCheckedIn()))
    {
	PracticeAreaBean practice_area = appointment.getPracticeArea();
	Calendar appointment_date = Calendar.getInstance();
	appointment_date.setTime(appointment.getAppointmentDate());
	String display_month = appointment_date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
	HashMap hash = (HashMap)practice_area_appt_hash.get(display_month);
	if (hash == null)
	{
	    hash = new HashMap(11);
	    practice_area_appt_hash.put(display_month, hash);
	}
	Vector vec = (Vector)hash.get(practice_area);
	if (vec == null)
	{
	    vec = new Vector();
	    hash.put(practice_area, vec);
	}
	vec.addElement(appointment);
    }
}

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>


    <meta http-equiv="content-type" content="text/html; charset=utf-8">
<title>Column Chart with Rotated Title and Labels</title>

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

<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/json/json-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/datasource/datasource-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/charts/charts-min.js"></script>


<!--begin custom header content for this example-->
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
<!--end custom header content for this example-->

</head>

<body class=" yui-skin-sam">






<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

<span class="chart_title">YTD Visits (Checked In/Out)</span>
<div id="chart">Unable to load Flash content. The YUI Charts Control requires Flash Player 9.0.45 or higher. You can download the latest version of Flash Player from the <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.</p></div>

<script type="text/javascript">

	YAHOO.widget.Chart.SWFURL = "https://www.valonyx.com/yui/build/charts/assets/charts.swf";

//--- data

	YAHOO.example.monthlyExpenses =
	[
<%

Iterator month_str_itr = months.iterator();
while (month_str_itr.hasNext())
{
    String display_month = (String)month_str_itr.next();
    String practice_area_str = "";
    System.out.println("display_month >" + display_month);
    HashMap hash = (HashMap)practice_area_appt_hash.get(display_month);
    System.out.println("hash >" + hash);
    Iterator practice_area_itr = practice_areas.iterator();
    while (practice_area_itr.hasNext())
    {
	PracticeAreaBean practice_area = (PracticeAreaBean)practice_area_itr.next();
	System.out.println("practice_area >" + practice_area.getLabel());
	Vector vec = null;
	if (hash != null)
	    vec = (Vector)hash.get(practice_area);
	System.out.println("vec >" + vec);
	System.out.println("practice_area >" + practice_area);
	if (vec == null)
	    practice_area_str += ", " + practice_area.getLabel().toLowerCase() + ": 0";
	else
	    practice_area_str += ", " + practice_area.getLabel().toLowerCase() + ": " + vec.size();
    }
%>
		{ month: "<%= display_month %>"<%= practice_area_str %> }<%= month_str_itr.hasNext() ? "," : "" %>
<%
}
%>
	];

	var myDataSource = new YAHOO.util.DataSource( YAHOO.example.monthlyExpenses );
	myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	myDataSource.responseSchema =
	{
<%
String pa_label_str = "";
Iterator practice_area_itr = practice_areas.iterator();
while (practice_area_itr.hasNext())
{
    PracticeAreaBean practice_area = (PracticeAreaBean)practice_area_itr.next();
    pa_label_str += ", \"" + practice_area.getLabel().toLowerCase() + "\"";
}
%>
		fields: [ "month"<%= pa_label_str %> ]
	};

//--- chart

	//series definition for chart
	var seriesDef =
	[
<%
practice_area_itr = practice_areas.iterator();
while (practice_area_itr.hasNext())
{
    PracticeAreaBean practice_area = (PracticeAreaBean)practice_area_itr.next();
%>
		{
			displayName: "<%= practice_area.getLabel() %>",
			yField: "<%= practice_area.getLabel().toLowerCase() %>",
			style:{size:10}
		}<%= practice_area_itr.hasNext() ? "," : "" %>
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
			prefix: "",
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
	currencyAxis.title = "Visits";

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

</body>
</html>