<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%
int start_hour_of_day = 7;
int start_minute = 0;
int end_hour_of_day = 23;
int end_minute = 0;
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Basic Example</title>

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

<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/datatable/assets/skins/sam/datatable.css" />
<script type="text/javascript" src="../yui/build/utilities/utilities.js"></script>
<script type="text/javascript" src="../yui/build/datasource/datasource-beta.js"></script>
<script type="text/javascript" src="../yui/build/datatable/datatable-beta.js"></script>

<script type="text/javascript" src="../dwr/interface/JDate.js"></script>
<script type="text/javascript" src="../dwr/engine.js"></script>
<script type="text/javascript" src="../dwr/util.js"></script>


<!--there is no custom header content for this example-->

</head>

<body class=" yui-skin-sam">

<h1>Basic Example</h1>

<div class="exampleIntro">
	<p>A demonstration of the DataTable's basic feature set.</p>

</div>

<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

<div id="schedule_div" style="position:absolute; 
 left:100px;
 top:100px; 
 width:520px;
 height:400px;
 background-color:#ffffff;
 overflow:auto;" >
    <table id="schedule" border="1" cellpadding="0" cellspacing="0" style="width:520px;">
        <thead>
            <tr>
                <th>Time</th>
                <th>Account Number</th>
                <th>Quantity</th>
                <th>Amount Due</th>
            </tr>
        </thead>
        <tbody>
<%
SimpleDateFormat date_format = new SimpleDateFormat("h:mm a");
Calendar open_time = Calendar.getInstance();
open_time.set(Calendar.HOUR_OF_DAY, start_hour_of_day);
open_time.set(Calendar.MINUTE, start_minute);
Calendar closing_time = Calendar.getInstance();
closing_time.set(Calendar.HOUR_OF_DAY, end_hour_of_day);
closing_time.set(Calendar.MINUTE, end_minute);

for (int i = 0; open_time.compareTo(closing_time) < 0; i++)
{
%>
            <tr>
                <td id="<%= i %>"><%= date_format.format(open_time.getTime()) %></td>
                <td>29e8548592d8c82</td>
                <td>12</td>
                <td>$150.00</td>
            </tr>
<%
    open_time.add(Calendar.MINUTE, 5);
}
%>
        </tbody>
    </table>
</div>

<script type="text/javascript" language="JavaScript">
  <!--
  
  //alert('hey >' + document.getElementById('50').innerHTML);
  
  var focusControl = document.getElementById('50');

  if (focusControl.type != "hidden") {
     focusControl.focus();
  }
  // -->
</script>

<div id="basic" style="width:520px;"></div>

<script type="text/javascript">
    
var mTimer;
var datatableX;
var datasourceX;
    
var datatableY;

YAHOO.example.Data = {
    schedule: [
        {time:"12:00 am", date:new Date(1980, 2, 24), quantity:1, amount:4, title:"Some Crap"},
        {time:"12:05 am", date:new Date("January 3, 1983"), quantity:null, amount:12.12345, title:"The Meaning of Life"},
        {time:"12:10 am", date:new Date(1978, 11, 12), quantity:12, amount:1.25, title:"This Book Was Meant to Be Read Aloud"},
        {time:"12:15 am", date:new Date("March 11, 1985"), quantity:6, amount:3.5, title:"Read Me Twice"}
    ]
}

var myColumnDefs = [
            {key:"time", sortable:false, resizeable:true},
            {key:"date", formatter:YAHOO.widget.DataTable.formatDate, sortable:true, resizeable:true},
            {key:"quantity", formatter:YAHOO.widget.DataTable.formatNumber, sortable:true, resizeable:true},
            {key:"amount", formatter:YAHOO.widget.DataTable.formatCurrency, sortable:true, resizeable:true},
            {key:"title", sortable:true, resizeable:true}
        ];

datasourceX = new YAHOO.util.DataSource(YAHOO.example.Data.schedule);
//this.myDataSource = new YAHOO.widget.DwrYuiDataSource(JDate.hashCode);
datasourceX.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
datasourceX.responseSchema = {
    fields: ["time","date","quantity","amount","title"]
};

datatableX = new YAHOO.widget.DataTable("basic", myColumnDefs, datasourceX, {caption:"DataTable Caption"});

var index = 0;
var focusControl;

function handleGetData(str) {
    //alert(str);
  YAHOO.example.Data.schedule[1].title = str;
    
    datasourceX = new YAHOO.util.DataSource(YAHOO.example.Data.schedule);
	//this.myDataSource = new YAHOO.widget.DwrYuiDataSource(JDate.hashCode);
        datasourceX.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        datasourceX.responseSchema = {
            fields: ["id","date","quantity","amount","title"]
        };
    
    datatableX.getDataSource().sendRequest('', datatableX.onDataReturnInitializeTable, datatableX);
    

  eval("focusControl = document.getElementById('" + index + "').focus();");
  index++;
  
  mTimer = setTimeout('JDate.hashCode(handleGetData);',1000);
}

YAHOO.util.Event.addListener(window, "load", function() {
    JDate.hashCode(handleGetData);
});


</script>

<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>
</html>
