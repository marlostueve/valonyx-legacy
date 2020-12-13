<%
boolean chrome = (session.getAttribute("chrome") != null);
chrome = true;
boolean daysheet = (request.getRequestURI().indexOf("schedule.jsp") > -1) || (request.getRequestURI().indexOf("schedule-chrome.jsp") > -1);
boolean prac = (request.getRequestURI().indexOf("practitioners.jsp") > -1) || (request.getRequestURI().indexOf("practitioners-chrome.jsp") > -1);
boolean clients = (request.getRequestURI().indexOf("clients.jsp") > -1) || (request.getRequestURI().indexOf("client-financial.jsp") > -1) || (request.getRequestURI().indexOf("client-ledger-new.jsp") > -1) || (request.getRequestURI().indexOf("clients-new.jsp") > -1);;
boolean reports = (request.getRequestURI().indexOf("report") > -1);
boolean administration = !reports && ((request.getRequestURI().indexOf("schedule-administration") > -1) || (request.getRequestURI().indexOf("setup_") > -1));
boolean eod = (!reports && request.getRequestURI().indexOf("end-of-day") > -1);
%>

<div id="header" >
<ul>
	<li<%= daysheet ? " id=\"selected\"" : "" %>><a href="schedule<%= chrome ? "-chrome" : "" %>.jsp">Schedule</a></li><!-- these comments between li's solve a problem in IE that prevents spaces appearing between list items that appear on different lines in the source
	--><li<%= prac ? " id=\"selected\"" : "" %>><a href="practitioners<%= chrome ? "-chrome" : "" %>.jsp">Practitioners</a></li><!--
	--><li<%= clients ? " id=\"selected\"" : "" %>><a href="clients-new.jsp">Clients</a></li><!--
<%
//System.out.println("loginBean.getPerson().isAdministrator2() >" + loginBean.getPerson().isAdministrator());

if (adminCompany.isPOSEnabled())
{
%>
	--><li<%= eod ? " id=\"selected\"" : "" %>><a href="end-of-day_01.jsp">End Of Day</a></li><!--
<%
}
%>
	--><li<%= reports ? " id=\"selected\"" : "" %>><a href="reports.jsp">Reports</a></li><!--
<%
//System.out.println("loginBean.getPerson().isAdministrator2() >" + loginBean.getPerson().isAdministrator());

if (((UKOnlinePersonBean)loginBean.getPerson()).isAdministrator())
{
%>
	--><li<%= administration ? " id=\"selected\"" : "" %>><a href="schedule-administration.jsp">Administration</a></li><!--
<%
}
%>
	--><li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Practice: <span style="font-weight: bold;"><%= adminCompany.getLabel() %></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Logged in as: <span style="font-weight: bold;"><%= loginBean.getPerson().getLabel() %></span>&nbsp;&nbsp;</li><!--
	--><li><a href="../logout.jsp">Logout</a></li>
</ul>

</div>