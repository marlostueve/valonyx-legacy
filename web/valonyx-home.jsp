<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> 

<%

CUBean.sendEmail("marlo@badiyan.com", "marlo@valonyx.com", "Valonyx index visit", "nothing to see here...");

String page_name = "Home";

%>
 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en"> 
	<head> 
		<title> Valonyx - Practice ManagementHome </title> 
 
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
 
		
		<meta name="description" content="Web based practice management with support for multiple practitioners, point of sales, and QuickBooks integration." /> 
		<meta name="keywords" content="scheduling software, POS, point of sale, QuickBooks integration, web based practitioner scheduling, point of sale scheduling, multi practitioner scheduling, practice management, wellness center management" /> 
		<meta name="Robots" content="index,follow" />
		<meta name="revisit-after" content="30 days" />
		<meta name="GOOGLEBOT" content="INDEX, FOLLOW" />
 
		<script type="text/javascript" src="js/main.js"></script> 
 
		<link rel="stylesheet" type="text/css" href="css/style.css" />
		
		<struts-html:javascript formName="loginForm" dynamicJavascript="true" staticJavascript="false"/>
		<script language="Javascript1.1" src="staticJavascript.jsp"></script>
		<%@ include file="channels/channel-head-javascript.jsp" %>
 
		<script type="text/javascript" src="scripts/browser-detect.js"></script>
<%@ include file="channels\channel-google-analytics.jsp" %>
 
	</head> 
	<body onload="browserStuff();" > 
 
 
 
		<!-- START CONTAINER --> 
 
		<div id="container"> 
 
 
 
<%@ include file="channels\channel-valonyx-header.jsp" %>
			
			
			
<%@ include file="channels\channel-valonyx-sidebar.jsp" %>
 
 
 
			<!-- START CONTENT --> 
 
			<div id="content"> 
 
 
 
 
 
<!-- START CONTENT SECTION --> 
 
<div id="contentsection"> 
	
	
	
	
		<!-- START HISTORY --> 
 
	<div id="history"> 
					
		<h3>Scheduling</h3>
 
		<p> 
			<a href="support-video.jsp?v=mXWokaQZKwM"><img src="img/valonyx-schedule-thumb.png" style="float: left; padding: 8px;" alt="" border="0" /></a>The main scheduling screen is where the daily activity occurs for your front-desk staff.  All of the daily appointments for all of your practitioners are displayed in one view.  Appointments of different types are color coded and appointment lengths are clearly visible.  You can quickly see areas where practitioners can be scheduled for new appointments.  Clicking on an appointment will allow you to edit it.  Clicking on a client will allow you to view details for that client.  Lists of late and checked in clients are also maintained.  Calendar views are always available that allow you to quickly view other days.<br /><br />
			Click <a href="support-video.jsp?v=mXWokaQZKwM" title="Schedule Overview" >here</a> to view a video that shows an overview of the scheduling functionality.
		</p>
					
		<h3>Point of Sale</h3>
 
		<p> 
			<a href="support-video.jsp?v=xWCLQwqjuEM"><img src="img/valonyx-checkout-thumb.png" style="float: right; padding: 8px;" alt="" border="0" /></a><em>Point of Sale</em> functionality is integrated with the Daysheet.  You don't have to navigate away from your schedule to check your clients out.  You can checkout an appointment simply by clicking on it.  The client is automatically selected.  Easily search through your inventory to select products or services to add to the sale.<br /><br />  Multiple forms of payment are accepted (cash, credit, check, gift card, and more) as well as the ability to view and pay on previous orders placed on account.  We can even guide you through the process of establishing your own merchant account for credit card processing.<br /><br />
			<a href="support-video.jsp?v=xWCLQwqjuEM">This video</a> demonstrates basic point of sale functionality.
		</p>
					
		<h3>QuickBooks&reg; Integration</h3>
 
		<p> 
			Transfer critical sales and financial data to QuickBooks automatically.  No more double entry!  Clients, Inventory, Sales Receipts, Invoices, Payments, Credit Memos, and more all out synchronized daily.
		</p>
					
		<h3>Setup Wizard</h3>
 
		<p> 
			<a href="support-video.jsp?v=5i1fgGx13KM"><img src="img/valonyx-setup_01-thumb.png" style="float: right; padding: 8px;" alt="" border="0" /></a>An easy to use <em>wizard</em> will guide you through the setup process, step by step.  Set up your practitioners, staff, practice areas, appointment types, practitioner schedules and much more.  Once you've completed the wizard, your practice will be ready to use.<br /><br />If you make a mistake you can always go back to fix it.  Even after your practice is up and running, the wizard is available to add staff and practitioners or to change any other settings.<br /><br />
			<a href="support-video.jsp?v=5i1fgGx13KM">View</a> a video showing how to setup a new practice.
		</p>
					
		<h3>Reporting</h3>
		
		<p> 
			Many reports are available that allow you to always take the pulse of your practice.  Client statements, financial, visit, and practitioner commissions reports are all available.
		</p>
					
		<h3>Administration</h3>
		
		<p> 
			Your practice administrators will be able to manage practitioners and staff, practice areas and appointment types.  They'll be able to manage clients, inventory, payment plans, marketing campaigns, and much more.
		</p>
 
	</div> 
 
	<!-- END HISTORY --> 
 
 
 
 
</div> 
 
<!-- END CONTENT SECTION --> 
 
 
 
 
 
			</div> 
 
			<!-- END CONTENT --> 
 
<%@ include file="channels\channel-valonyx-footer.jsp" %>
 
		</div> 
 
		<!-- END CONTAINER --> 
 
 
 
	</body> 
</html> 
 