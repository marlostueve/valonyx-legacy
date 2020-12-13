<%@ page contentType="text/html" import="java.util.*, java.text.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
	<link rel="stylesheet" type="text/css" href="css/mystyle2.css" />
	<title>Welcome to Valeo</title>
    </head>

<script type="text/javascript" src="scripts/browser-detect.js"></script>

    <body>

	<div id="main">
<%@ include file="channels\channel-header.jsp" %>
	    <div id="bodyline">
		<div id="todaywrapper">
		    <div id="today">
<%@ include file="channels\channel-date.jsp" %>
			<div id="serviceswhere">
			    <div id="services">
				<div id="news">
				    <h4>The Valeo Health & Wellness Center staff are thrilled to share our Vision, Mission & Purpose for serving you.</h4>
				</div><!-- div#news -->
				<div id="news">
				    <h2>Vision</h2>
				    <ul>
				    <li><img src="images/vision.jpg" alt="Vision"><strong>For every person to experience wellness.</strong></li>
				    </ul>
				</div><!-- div#news -->
				<div id="news">
				    <h2>Mission</h2>
				    <ul>
				    <li><img src="images/mission.jpg" alt="Mission"><strong>Working as a unified team of healthcare practitioners, a God-Centered and Patient Centered team to define, provide and measure one's individual progress towards wellness.</strong></li>
				    </ul>
				</div><!-- div#news -->
				<div id="news">
				    <h2>Purpose</h2>
				    <ul>
				    <li><img src="images/purpose.png" alt="Purpose"><strong>God has called us to use our gifts and talents to facilitate the health that truly is within us; so that we can have the freedom to live the life God intended.</strong></li>
				    </ul>
				</div><!-- div#news -->

				
				
			    </div><!-- div#services -->
<%@ include file="channels\latest-news-text-new.jsp" %>



			</div><!-- div#serviceswhere -->
			
			
			
		    </div><!-- div#today -->
		    				

		    <div id="downloads">
			<p>Audio and video links may require you to <a href="" onclick="">download additional plug-ins</a></p>
		    </div><!-- div#downloads -->
		</div><!-- div#todaywrapper -->
<%@ include file="channels\channel-menu-new.jsp" %>
	    </div><!-- div#bodyline -->
	    <div id="footline"><img src="images/footer.gif" alt="footer">
	    </div><!-- div#footline -->
	</div><!-- div#main -->



    </body>
</html>