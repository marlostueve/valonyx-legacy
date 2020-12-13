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
 
					<h2>Lost Password</h2> 
 
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
		
							<a href="#">Forgot Your Password?</a> 
		
							<!--
							<strong>or</strong> 
							-->
		
							
							<a href="#">Register Now to get your Practice Online!</a> 
							
		
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
	
	
	
	

	<!-- START LOSTPASSWORD --> 
 
	<div id="registration"> 
 
<%
String message = (String)session.getAttribute("forgot-password-message");
if (message == null)
	message = "Please <strong>provide your email address below</strong>.  We'll send your password to that email address.";
session.removeAttribute("forgot-password-message");
%>
		
		<p> 
			<%= message %>
		</p> 
 
		<struts-html:form action="/admin/lostPassword" focus="email" onsubmit="return dbck.validate();"  >
 
			<div> 
 
				<dl> 
 
					<dt><label for="femail">Email Address</label></dt> 
					<dd><input type="text" id="femail" name="email" value="" /></dd>									
 
					<dt>&nbsp;</dt> 
					<dd><input type="submit" value="Send My Password" class="submit" /></dd> 
 
				</dl> 
 
			</div> 
 
		</struts-html:form> 
 
		<script type="text/javascript"> 
		//<!-- <![CDATA[
 
			var dbck = new doublecheck();
			dbck.addElement("femail", "^[a-zA-Z][a-zA-Z0-9_\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][a-zA-Z0-9_\\.-]*\\.[a-zA-Z]{2,4}$", "You must enter a valid email address.");
 
		// ]]> //-->
		</script> 
 
		
	</div> 
 
	<!-- END LOSTPASSWORD --> 
 
 
 
 
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
 