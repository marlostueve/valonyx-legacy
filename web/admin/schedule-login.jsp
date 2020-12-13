<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> 
 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en"> 
	<head> 
		<title> Valonyx - Practice ManagementHome </title> 
 
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
 
		
		<meta name="description" content="Web based practice management with support for multiple practitioners, point of sales, and QuickBooks integration." /> 
		<meta name="keywords" content="scheduling software, POS, point of sale, QuickBooks integration, web based practitioner scheduling, point of sale scheduling, multi practitioner scheduling, practice management, wellness center management" /> 
		<meta name="Robots" content="index,follow" />
		<meta name="revisit-after" content="30 days" />
		<meta name="GOOGLEBOT" content="INDEX, FOLLOW" />
 
		<script type="text/javascript" src="../js/main.js"></script> 
 
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
		
		<struts-html:javascript formName="loginForm" dynamicJavascript="true" staticJavascript="false"/>
		<script language="Javascript1.1" src="staticJavascript.jsp"></script>
		<%@ include file="../channels/channel-head-javascript.jsp" %>
 
		<script type="text/javascript" src="../scripts/browser-detect.js"></script>
		<script language="JavaScript" type="text/javascript" src="../js/google-analytics.js"></script>
 
	</head> 
	<body> 
 
 
 
		<!-- START CONTAINER --> 
 
		<div id="container"> 
 
 
 
			<!-- START HEADER --> 
 
			<div id="header"> 
 
 
 
				<!-- START LOGO --> 
 
				<div id="logo"> 
 
					<h1>multi-practitioner practice management solution<a href="schedule-login.jsp"></a></h1> 
 
				</div> 
 
				<!-- END LOGO --> 
 
 
 
				<!-- START NAVIGATION HEADER --> 
 
				<div id="navigationheader"> 
 
					<h2>Site Navigation</h2> 
 
					<ul> 
						<li><a id="navinformation" href="#">About Us</a></li> 
						<li><a id="navsuggestion" href="#">Make a Suggestion!</a></li> 
						<li><a id="navsitemap" href="#">Sitemap</a></li> 
						<li><a id="navsitemap" href="#">More</a></li> 
					</ul> 
 
				</div> 
 
				<!-- END NAVIGATION HEADER --> 
 
 
 
				<!-- START INTRODUCTION --> 
 
				<div id="introduction"> 
 
					<h2>Home</h2> 
 
					<p> 
						<strong>Valonyx</strong> is a web-based multi-practitioner practice management solution.  Get your practice up and running without the hassle.<br /><br />  <!-- Register now for a <strong>free</strong> 30 day trial. -->  Valonyx is currently conducting a closed beta test.  Email <a href="mailto:valonyx@valeowc.com">valonyx@valeowc.com</a> for more information.
					</p> 
 
					<!-- <h3><strong>Make a Suggestion</strong></h3>  -->
 
				</div> 
 
				<!-- END INTRODUCTION --> 
 
 
 
			</div> 
 
			<!-- END HEADER --> 
			
			
			
			<!-- START SIDEBAR --> 
 
			<div id="sidebar"> 
 
 
 
				
				<!-- START USER LOGIN --> 
 
				<div id="userlogin"> 
 
					<h2>Log In</h2> 
 
					
					<struts-html:form action="/admin/loginSchedule" onsubmit="return validateLoginForm(this);" focus="username">
 
						<div> 
 
							<input type="hidden" name="action" value="login" class="hidden" /> 
	
							<input type="text" name="username" id="username" /> 
							<label for="username">Username</label> 
		
							<input type="password" name="password" id="password" /> 
							<label for="password">Password</label> 
							
							<input type="submit" class="submit" value="Log In to Valonyx" /> 
		
							<a href="forgot-password.jsp">Forgot Your Password?</a> 
		
							<!--
							<strong>or</strong> 
							-->
		
							
							<a href="terms-of-service.jsp">Register Now to get your Practice Online!</a> 
							
		
						</div> 
 
					</struts-html:form>
 
 
				</div> 
 
				<!-- END USER LOGIN --> 
 
								
					<div style="margin-top: 20px;"></div> 
 
  				<!-- START NAVIGATION MAIN --> 
 
  				<div id="navigationfeatures"> 
 
					<!-- <h2>Sections</h2>  -->
 
					<ul> 
						<li><a id="navdesignologue" href="schedule-login.jsp">Home</a></li>
						<li><a id="navlinks" href="#">Features</a></li>
						<li><a id="navstatistics" href="#">Support Videos</a></li>
						<li><a id="navdesignologue" href="#">Subscriptions</a></li>
						<li><a id="navlinks" href="#">System Requirements</a></li>
					</ul>
 
				</div> 
 
				<!-- END NAVIGATION MAIN --> 
 
 
 
				
				
			</div> 
 
  			<!-- END SIDEBAR --> 
 
 
 
			<!-- START CONTENT --> 
 
			<div id="content"> 
 
 
 
 
 
<!-- START CONTENT SECTION --> 
 
<div id="contentsection"> 
	
	
	
	
		<!-- START HISTORY --> 
 
	<div id="history"> 
					
		<h3>Scheduling</h3>
 
		<p> 
			<a href="#"><img src="../img/valonyx-schedule-thumb.png" style="float: left; padding: 8px;" alt="" /></a>The main scheduling screen, or <em>Daysheet</em> is the daily activity occurs for your front-desk staff.  All of the daily appointments for all of your practitioners are displayed in one view.  Appointments of different types are color coded and appointment lengths are clearly visible.  You can quickly see areas where practitioners can be scheduled for new appointments.  Clicking on an appointment will allow you to edit it.  Clicking on a client will allow you to view details on for that client.  Lists of late and checked in clients are also maintained.  Calendar views are always available that allow you to quickly view other days.
		</p>
					
		<h3>Point of Sale</h3>
 
		<p> 
			<a href="#"><img src="../img/valonyx-checkout-thumb.png" style="float: right; padding: 8px;" alt="" /></a><em>Point of Sale</em> functionality is integrated with the Daysheet.  You don't have to navigate away from your schedule to check your clients out.  You can checkout an appointment simply by clicking on it.  The client is automatically selected.  Easily search through your inventory to select products or services to add to the sale.<br /><br />  Multiple forms of payment are accepted (cash, credit, check, gift card, and more) as well as the ability to view and pay on previous orders placed on account.  We can even guide you through the process of establishing your own merchant account for credit card processing.
		</p>
					
		<h3>QuickBooks&reg; Integration</h3>
 
		<p> 
			Transfer critical sales and financial data to QuickBooks automatically.  No more double entry!  Clients, Inventory, Sales Receipts, Invoices, Payments, Credit Memos, and more all out synchronized daily.
		</p>
					
		<h3>Setup Wizard</h3>
 
		<p> 
			<a href="#"><img src="../img/valonyx-setup_01-thumb.png" style="float: right; padding: 8px;" alt="" /></a>An easy to use <em>wizard</em> will guide you through the setup process, step by step.  Set up your practitioners, staff, practice areas, appointment types, practitioner schedules and much more.  Once you've completed the wizard, your practice will be ready to use.<br /><br />If you make a mistake you can always go back to fix it.  Even after your practice is up and running, the wizard is available to add staff and practitioners or to change any other settings.
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
 
 
 
			<!-- START FOOTER --> 
 
			<div id="footer"> 
			
				<!-- START COPYRIGHT --> 
 
				<div id="copyright"> 
					&copy; <a href="mailto:staff(at)valonyx.com">Valonyx</a> 2011				</div> 
					
				<!-- END COPYRIGHT --> 
			
 
			
				<!-- START NAVIGATION FOOTER --> 
			
				<div id="navigationfooter"> 
					<ul> 
						<li>Use our <a href="/rss/">RSS</a> feed.</li> 
						<li>This design by <a href="http://www.skettino.com/">fs</a> is Valid <a href="http://validator.w3.org/check?uri=http%3A%2F%2Fwww.oswd.org%2F">XHTML</a> and <a href="http://jigsaw.w3.org/css-validator/validator?uri=http%3A%2F%2Fwww.oswd.org%2Fcss%2Fdefault.css&amp;usermedium=all">CSS</a>.</li> 
						<li>Read our <a href="#">Privacy Policy</a> and <a href="#">Usage Agreement</a>.</li> 
					</ul> 
				</div> 
 
				<!-- END NAVIGATION FOOTER --> 
					
			</div> 
 
			<!-- END FOOTER --> 
		
 
 
		</div> 
 
		<!-- END CONTAINER --> 
 
 
 
	</body> 
</html> 
 