<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%

String page_name = "Support Videos";

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
 

 
 
	<div style="margin-bottom: 20px; float: left;"> 
 
	 
 
<!-- START PAGER --> 
 <!--
<div id="pager"> 
 
	<ul> 
		<li><a href="/designs/browse/page/1/" class="currentpage">1</a></li> 
		<li><a href="/designs/browse/page/2/">2</a></li> 
		<li><a href="/designs/browse/page/3/">3</a></li> 
		<li><a href="/designs/browse/page/4/">4</a></li> 
		<li><a href="/designs/browse/page/5/">5</a></li> 
		<li><a href="/designs/browse/page/6/">6</a></li> 
		<li><span>...</span></li> 
		<li><a href="/designs/browse/page/130/">130</a></li> 
		<li><a href="/designs/browse/page/2/">Next &raquo;</a></li> 
	</ul> 
 
	<div id="results"> 
		( 1-16 of 2080 Designs )
	</div> 
 
</div> 
 -->
<!-- END PAGER --> 
 
 
 
	</div> 
 
 
<!-- START GRID --> 
<div id="design_grid"> 
 
 
 
	<!-- START ROW OF DESIGNS --> 
	<div class="row"> 
 
 
 
<!-- START DESIGN PREVIEW --> 
 
<div class="designpreview" id="id3692"> 
	<a href="support-video.jsp?v=5i1fgGx13KM" title="Setup Overview" class="dpview"><img src="http://img.youtube.com/vi/5i1fgGx13KM/2.jpg" width="120" height="90" alt="Thumbnail of Web Design Template" /></a> 
	<h3><a href="support-video.jsp?v=5i1fgGx13KM" title="View Setup Overview">Setup Overview</a></h3> 
	<ul class="dpdetails"> 
		<li class="dpdate">A guide through setting up a new practice.</li> 
	</ul>
</div> 
 
<!-- END DESIGN PREVIEW --> 
 
 
 
 
<!-- START DESIGN PREVIEW --> 
 
<div class="designpreview" id="id3686"> 
	<a href="support-video.jsp?v=mXWokaQZKwM" title="Schedule Overview" class="dpview"><img src="http://img.youtube.com/vi/mXWokaQZKwM/2.jpg" width="120" height="90" alt="Thumbnail of Web Design Template" /></a> 
	<h3><a href="support-video.jsp?v=mXWokaQZKwM" title="View Schedule Overview">Schedule Overview</a></h3> 
	<ul class="dpdetails"> 
		<li class="dpdate">An overview of the main scheduling page.</li> 
	</ul>	
</div> 
 
<!-- END DESIGN PREVIEW --> 
 
 
 
 
<!-- START DESIGN PREVIEW --> 
 
<div class="designpreview" id="id3692"> 
	<a href="support-video.jsp?v=tzmtvfZGdRA" title="Client Info" class="dpview"><img src="http://img.youtube.com/vi/tzmtvfZGdRA/2.jpg" width="120" height="90" alt="Thumbnail of Web Design Template" /></a> 
	<h3><a href="support-video.jsp?v=tzmtvfZGdRA" title="View Client Info">Client Information Overview</a></h3> 
	<ul class="dpdetails"> 
		<li class="dpdate">An overview of some client information functionality.</li> 
	</ul>
</div> 
 
<!-- END DESIGN PREVIEW --> 
 
 
 
		<!-- START LAST IN ROW --> 
		<div class="last_in_row"> 
 
 
 
<!-- START DESIGN PREVIEW --> 
 
<div class="designpreview" id="id3692"> 
	<a href="support-video.jsp?v=ESB66-5Wsgk" title="New Client" class="dpview"><img src="http://img.youtube.com/vi/ESB66-5Wsgk/2.jpg" width="120" height="90" alt="Thumbnail of Web Design Template" /></a> 
	<h3><a href="support-video.jsp?v=ESB66-5Wsgk" title="View New Client">New Client</a></h3> 
	<ul class="dpdetails"> 
		<li class="dpdate">Demonstrates creating a new client.</li> 
	</ul>
</div> 
 
<!-- END DESIGN PREVIEW --> 
 
 
 
		</div> 
		<!-- END LAST IN ROW --> 
 
 
 
	</div> 
	<!-- END ROW OF DESIGNS --> 
 
 
	<!-- START ROW OF DESIGNS --> 
	<div class="row"> 
 
 
 
<!-- START DESIGN PREVIEW --> 
 <!--
<div class="designpreview" id="id3692"> 
	<a href="support-video.jsp?v=5i1fgGx13KM" title="Setup Overview" class="dpview"><img src="http://img.youtube.com/vi/5i1fgGx13KM/2.jpg" width="120" height="90" alt="Thumbnail of Web Design Template" /></a> 
	<h3><a href="support-video.jsp?v=5i1fgGx13KM" title="View Web Design Template">Setup Overview</a></h3> 
	<ul class="dpdetails"> 
		<li class="dpdate">A guide through setting up a new practice.</li> 
	</ul>
</div> 
 -->
<!-- END DESIGN PREVIEW --> 
 
 
 
 
 
 
 
		<!-- START LAST IN ROW --> 
		<div class="last_in_row"> 
 
 
 
<!-- START DESIGN PREVIEW --> 
 
<div class="designpreview" id="id3692"> 
	<a href="support-video.jsp?v=xWCLQwqjuEM" title="Checkout Overview" class="dpview"><img src="http://img.youtube.com/vi/xWCLQwqjuEM/2.jpg" width="120" height="90" alt="Thumbnail of Web Design Template" /></a> 
	<h3><a href="support-video.jsp?v=xWCLQwqjuEM" title="View Checkout Overview">Checkout Overview</a></h3> 
	<ul class="dpdetails"> 
		<li class="dpdate">Demonstrates basic point of sale functionality.</li> 
	</ul>
</div> 
 
<!-- END DESIGN PREVIEW --> 
 
 
 
		</div> 
		<!-- END LAST IN ROW --> 
 
 
	</div> 
	<!-- END ROW OF DESIGNS --> 
 
 
 
 
 
 
</div> 
<!-- END GRID --> 
 
 
 
 
 
 
		<div style="float: left; margin-top: 20px;"> 
 
 
 
<!-- START PAGER --> 
 <!--
<div id="pager"> 
 
	<ul> 
		<li><a href="/designs/browse/page/1/" class="currentpage">1</a></li> 
		<li><a href="/designs/browse/page/2/">2</a></li> 
		<li><a href="/designs/browse/page/3/">3</a></li> 
		<li><a href="/designs/browse/page/4/">4</a></li> 
		<li><a href="/designs/browse/page/5/">5</a></li> 
		<li><a href="/designs/browse/page/6/">6</a></li> 
		<li><span>...</span></li> 
		<li><a href="/designs/browse/page/130/">130</a></li> 
		<li><a href="/designs/browse/page/2/">Next &raquo;</a></li> 
	</ul> 
 
	<div id="results"> 
		( 1-16 of 2080 Designs )
	</div> 
 
</div> 
 -->
<!-- END PAGER --> 
 
 
 
		</div> 
 
</div> 
 
<!-- END CONTENT SECTION --> 
 
 
 
 
			</div> 
 
			<!-- END CONTENT --> 
 
 
 
<%@ include file="channels\channel-valonyx-footer.jsp" %>
		
 
 
		</div> 
 
		<!-- END CONTAINER --> 
 
 
 
	</body> 
</html> 
 