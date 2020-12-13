<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%

String page_name = "About Us";

%>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> 
 
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
 
 
 
 
 
<div id="contentsectionleft"> 
 
 
 
	<!-- START HISTORY --> 
 
	<div id="history"> 
					
		<h2>History</h2>	
													
		<h3>The Story</h3> 
 
		<p> 
			The <strong>Valonyx</strong> project was launched in January, 2008 by Marlo Stueve.
			The goal was to provide the health care practitioner community with a flexible and affordable alternative
			for practice management using a "software as a service" or "on-demand" model.  For you this means lower startup
			costs and no long-term commitments.
		</p> 
 
		<p> 
			A more detailed account of the origins of Valonyx is coming soon!
		</p> 
 
	</div> 
 
	<!-- END HISTORY --> 
					
 
 
</div> 
 
 
 
<div id="contentsectionright"> 
 
 
 
	<!-- START STAFF --> 
 
	<div id="staff"> 
 
		<h2>Staff</h2> 
 
 
 
		<!-- START MEMBER --> 
 
		<div class="member"> 
 
			<img src="img/marlo-christine.png" width="160" height="120" border="" alt="Marlo Stueve" /> 
 
			<h3>Marlo Stueve</h3> 
 
			<h4>Founder,<br />Senior Developer</h4> 
 
 
			<p> 
				Currently residing in Eden Prairie, Minnesota, Marlo has been designing enterprise web-based solutions
				for over 15 years.  He is married to Christine and they keep busy with their four boys.
			</p> 
 
		</div> 
 
		<!-- END MEMBER --> 
 
	</div> 
 
</div> 
 
 
 
 
 
			</div> 
 
			<!-- END CONTENT --> 
 
 
 
<%@ include file="channels\channel-valonyx-footer.jsp" %>
		
 
 
		</div> 
 
		<!-- END CONTAINER --> 
 
 
 
	</body> 
</html> 
 