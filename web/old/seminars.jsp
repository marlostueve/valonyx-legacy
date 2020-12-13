<%@ page contentType="text/html" import="java.util.*, java.text.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
	<link rel="stylesheet" type="text/css" href="css/mystyle2.css" />
	<title>Welcome to Valeo</title>
    </head>
    <body>

	<div id="main">
	    
<%@ include file="channels\channel-header.jsp" %>
	    <div id="bodyline">
		
		<div id="todaywrapper">

		    <div id="today">

			<div id="serviceswhere">
			    

			    
			    <div id="services-wide">
				<h2><strong>FREE Monthly Seminar Schedule</strong></h2>
		<p><i>All Seminars are located at Valeo</i></p>
		<p>
		<table cellpadding="5" cellspacing="5">
			
			
			<tr>
				<td valign="top" align="right">May 12, 2009 7:00 - 8:00 p.m.</td>
				<td>Female Hormones Seminar</td>
			</tr>
			<tr>
				<td valign="top" align="right">May 19, 2009 7:00 - 8:00 p.m.</td>
				<td>Vaccination Seminar</td>
			</tr>
		</table>
		</p><p><b>Reserve your spot today!</b></p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Seminar Presentations</strong></h2>
<table cellpadding="4">
			<tr>
				<td valign="top">
					<p><b></b></p>
					<p>
						<ul>
							<li><a href="http://www.valeowc.com/Is_Your_Faith_in_the_Flu_Shot/player.html" target="_blank">Is your Faith in the Flu Shot?</a></li>
							<li><a href="http://www.valeowc.com/Vaccinations/player.html" target="_blank">Vaccinations</a></li>
						</ul>
					</p>
				</td>
			</tr>
		</table>
			    </div>
			</div><!-- div#serviceswhere -->
		    </div><!-- div#today -->
		</div><!-- div#todaywrapper -->
<%@ include file="channels\channel-menu-new.jsp" %>
	    </div><!-- div#bodyline -->
	    <div id="footline"><img src="images/footer.gif" alt="footer">
	    </div><!-- div#footline -->
	</div><!-- div#main -->

    </body>
</html>