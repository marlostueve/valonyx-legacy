<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> 

<%

CUBean.sendEmail("marlo@badiyan.com", "marlo@valonyx.com", "Valonyx index visit", "nothing to see here...");

String page_name = "Features";

%>
 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en"> 
	<head> 
		<title> Valonyx - Practice Management - <%= page_name %> </title> 
 
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
 
 
 
	<!-- START DESIGNOLOGUE --> 
 
	<div id="designologue"> 
 
		<p> 
			What can <strong>Valonyx</strong> do for you?
		</p> 
 
		<div id="links"> 
 
			
			<h4>Scheduling Module - <a href="support-video.jsp?v=mXWokaQZKwM">view overview</a></h4> 
 
			<ul>
				<li>Setup wizard to get you up and running quickly.</li>
				<li>All of your practitioner's schedules in one daily view.</li>
				<li>A weekly view is available for a single practitioner.</li>
				<li>Quickly print daily practitioner appointment reports.</li>
				<li>Color-coded appointment types.</li>
				<li>Create new clients from the main scheduling view.</li>
				<li>Create and edit appointments without leaving the schedule view.</li>
				<li>Access and edit extensive client information all from the main schedule view with a single click.</li>
				<li>Maintain notes, custom care details, contact status, client followup, SOAP Notes, Health History, and Electronic Health Records all from the main scheduling view.</li>
				<li>Easily email clients from the main scheduling page.</li>
				<li>Calendar controls allow for quick navigation to other dates.</li>
				<li>Icons notify of new clients, client notes, and birthdays.</li>
				<li>Right-click to check-in, checkout, edit or reschedule, cancel appointment, cut and paste appointments, and generate client appointment reports.</li>
				<li>Practitioner meetings can be maintained along with client appointments.</li>
				<li>Counts are displayed for the number of scheduled appointments, number checked-in, cancelled, checked-out, late, rescheduled, and no shows.</li>
				<li>Lists are displayed for checked in clients, late clients, no show clients, clients to contact by practice area, practitioner call list, and clients that require follow-up for any reason.</li>
				<li>Valonyx is web-based.  You can access your schedule from anywhere.</li>
				<li>Touch-screen compatible.</li>
			</ul> 
 
			<h4>Point of Sale Module - <a href="support-video.jsp?v=xWCLQwqjuEM">view overview</a></h4> 
 
			<ul> 
				<li>Client checkout without leaving schedule view.</li>
				<li>Quick search for client or checkout directly from client appointment.</li>
				<li>Quick search for service or inventory items being purchased.</li>
				<li>Automatic assignment of practitioner for commissions for each transaction.</li>
				<li>Easily update quantities and amounts.</li>
				<li>Apply discounts and tax status.</li>
				<li>Allows for payments on account, reversing receipts, and refunds.</li>
				<li>Common payment methods available including cash, check, credit card, account, gift certificates, and gift cards.</li>
				<li>Quick printing of last receipt.</li>
				<li>Integrated with inventory management and purchase ordering system.</li>
				<li>End of day financial reconciliation via an End of Day wizard that synchronizes sales receipts, invoices, inventory, clients and more with QuickBooks&reg;</li>
			</ul> 
 
			<h4>Reports</h4> 
 
			<ul> 
				<li>Client Statement Report.</li>
				<li>End of Day Financial Report.</li>
				<li>End of Day Visit Report.</li>
				<li>Year to Date Financial Graph.</li>
				<li>Year to Date Visit Graph.</li>
				<li>Commissions Report and Year to Date Commissions Report</li>
				<li>Goods Sold Report</li>
				<li>Inventory Report</li>
				<li>Client List Report</li>
			</ul>
 
			<h4>Administration</h4> 
 
			<ul> 
				<li>Administer your practitioners, staff, practice areas, appointment types, schedule settings, and practitioner schedules.</li>
				<li>Manage Inventory and Procedures, Tax Codes, Vendors, Inventory Departments, Inventory Counts, and Purchase Orders.</li>
				<li>Manage and view Payment Plans.</li>
				<li>Manage and customize your own SOAP statement trees by practice area.</li>
				<li>Administration of your marketing campaigns with the ability to link clients to campaigns.</li>
				<li>Synchronize your client and daily financial data with QuickBooks&reg;.</li>
			</ul>
 
			<h4>Future Features</h4> 
 
			<ul> 
				<li>Insurance Module including automatic billing, applying insurance payments to customer accounts, functionality for in-network provider, customization for out-of-network provider</li>
				
			</ul>
 
 
		</div>
 
 
 
	</div>
 
	<!-- END DESIGNOLOGUE --> 
 
 
 
 
</div>
 
<!-- END CONTENT SECTION --> 
 
 
 
 
 
			</div> 
 
			<!-- END CONTENT --> 
 
<%@ include file="channels\channel-valonyx-footer.jsp" %>
 
		</div> 
 
		<!-- END CONTAINER --> 
 
 
 
	</body> 
</html> 
 