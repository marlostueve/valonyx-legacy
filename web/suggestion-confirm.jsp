<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%

String page_name = "Suggestion";

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
 
 
 
 
 
<!-- START CONTENT SECTION --> 
 
<div id="contentsection"> 
	
	
	
	

	<!-- START LOSTPASSWORD --> 
 
	<div id="registration"> 
 
<%
String message = (String)session.getAttribute("suggestion-message");
if (message == null)
	message = "[MESSAGE NOT FOUND]";
session.removeAttribute("suggestion-message");
%>
		<p> 
			<%= message %>
		</p>
 
		
	</div> 
 
	<!-- END LOSTPASSWORD --> 
 
 
 
 
</div> 
 
<!-- END CONTENT SECTION --> 
 
 
 
 
 
			</div> 
 
			<!-- END CONTENT --> 
 
 
 
<%@ include file="channels\channel-valonyx-footer.jsp" %>
		
 
 
		</div> 
 
		<!-- END CONTAINER --> 
 
 
 
	</body> 
</html> 
 