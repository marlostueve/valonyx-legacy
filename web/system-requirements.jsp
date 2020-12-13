<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%

String page_name = "Requirements";

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


<script type="text/javascript">

    function checkBrowser()	{
		document.getElementById('browser-display').firstChild.nodeValue = BrowserDetect.browser;
		
		if (BrowserDetect.browser == 'Chrome')
			document.getElementById('browser-version').firstChild.nodeValue = BrowserDetect.version + ', so you\'re good to go';
		else if (BrowserDetect.browser == 'Explorer' && BrowserDetect.version == '9') {
			document.getElementById('browser-version').firstChild.nodeValue = BrowserDetect.version + ', which is OK';
		}
		else if (BrowserDetect.browser == 'Firefox') {
				document.getElementById('browser-version').firstChild.nodeValue = BrowserDetect.version + ', which won\'t work well.  We like Firefox too and are working to support it';
		}
		else
			document.getElementById('browser-version').firstChild.nodeValue = BrowserDetect.version + ', which probably won\'t work well';
	}

</script>

	</head> 
	<body onload="browserStuff(); checkBrowser();"> 
 
 
 
		<!-- START CONTAINER --> 
 
		<div id="container"> 
 
 
 
<%@ include file="channels\channel-valonyx-header.jsp" %>
			
			
			
<%@ include file="channels\channel-valonyx-sidebar.jsp" %>
 
 
 
			<!-- START CONTENT --> 
 
			<div id="content"> 
 
 
 
 
 
<!-- START CONTENT SECTION --> 
 
<div id="contentsection"> 
	
	
	
	

	<!-- START LOSTPASSWORD --> 
 
	<div id="history">
 

		<h3>Internet Connection</h3>
		<p> 
			An Internet connection is required to use Valonyx.  It is recommended that you have a reasonably fast Internet connection, like DSL or cable modem service.
		</p> 
		
		<h3>Hardware</h3>
		<p> 
			Nearly any Internet connected Windows PC will do.  It's best to have something fairly new with 2GB or more of RAM.
		</p> 
		
		<h3>Browser</h3>
		<p> 
			Valonyx works best using the Google Chrome browser.  You can download it <a href="http://www.google.com/chrome/">here</a>.  It also works with the latest version of Internet Explorer which is available <a href="http://windows.microsoft.com/en-US/internet-explorer/products/ie/home">here</a>.<br /><br />It looks like you're currently running the <span id="browser-display">&nbsp;</span> browser version <span id="browser-version">&nbsp;</span>.
		</p>  
		
		<h3>Point of Sale</h3>
		<p> 
			If you want to utilize the Valonyx Point of Sale module, you'll need a <a href="http://www.amazon.com/Triple-Track-Magnetic-Stripe-Reader/dp/B004IATGGM/ref=sr_1_3?ie=UTF8&qid=1312044894&sr=8-3">credit card magnetic stripe reader</a>, <a href="http://www.amazon.com/Star-TSP143-USB-Future-Print/dp/B000FCP92C/ref=sr_1_1?ie=UTF8&qid=1312044556&sr=8-1">receipt printer</a>, and a <a href="http://www.amazon.com/QuickBooks-Point-Sale-Cash-Drawer/dp/B004D4H2OG">cash drawer</a> for each point of sale computer.
		<br /><br />
			You'll also need a merchant account for the Authorize.Net payment gateway to process credit card transactions.  You can register for your account <a href="https://ems.authorize.net/oap/home.aspx?SalesRepID=98&ResellerID=19854">here</a>.
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
 