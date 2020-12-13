<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%
int start_hour_of_day = 7;
int start_minute = 0;
int end_hour_of_day = 23;
int end_minute = 0;
%>




<style type="text/css">
div.tableContainer {clear: both;border: 1px solid #963;	height: 285px;	overflow: auto;	width: 756px;}
div.tableContainer table {float: left;width: 100%}
\html div.tableContainer table/* */ {margin: 0 -16px 0 0}
thead.fixedHeader tr {position: relative;top: expression(document.getElementById("tableContainer").scrollTop);}
head:first-child+body thead[class].fixedHeader tr {display: block;}
thead.fixedHeader th {background: #C96;border-left: 1px solid #EB8;border-right: 1px solid #B74;border-top: 1px solid #EB8;font-weight: normal;	padding: 4px 3px;text-align: left; font-weight: bold;}
thead.fixedHeader a, thead.fixedHeader a:link, thead.fixedHeader a:visited {color: #FFF;display: block;text-decoration: none;width: 100%}
thead.fixedHeader a:hover {color: #FFF;	display: block;	text-decoration: underline;width: 100%}
head:first-child+body tbody[class].scrollContent {display: block;height: 262px;overflow: auto;width: 100%}
tbody.scrollContent td, tbody.scrollContent tr.normalRow td {background: #FFF;border-bottom: none;border-left: none;border-right: 1px solid #CCC;border-top: 1px solid #DDD;padding: 2px 3px 3px 4px}
tbody.scrollContent tr.alternateRow td {background: #EEE;border-bottom: none;border-left: none;	border-right: 1px solid #CCC;border-top: 1px solid #DDD;padding: 2px 3px 3px 4px}
head:first-child+body thead[class].fixedHeader th {width: 200px}
head:first-child+body thead[class].fixedHeader th + th {width: 240px}
head:first-child+body thead[class].fixedHeader th + th + th {border-right: none;padding: 4px 4px 4px 3px;width: 316px}
head:first-child+body tbody[class].scrollContent td {width: 200px}
head:first-child+body tbody[class].scrollContent td + td {width: 240px}
head:first-child+body tbody[class].scrollContent td + td + td {border-right: none;padding: 2px 4px 2px 3px;width: 300px}
</style>
</head><body>

<h1></h1>
<h2></h2>

<div><br/></div>

<h2></h2>
<h3></h3>


<div id="tableContainer" class="tableContainer">

<table border="0" cellpadding="0" cellspacing="0" width="100%" class="scrollTable">
<thead class="fixedHeader" id="fixedHeader">
<tr>
                <th>Time</th>
                <th>Account Number</th>
                <th>Quantity</th>
                <th>Amount Due</th>
	</tr>
</thead>
<tbody class="scrollContent">

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




