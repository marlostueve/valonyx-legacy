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
				<h2><strong>Why Valeo?</strong></h2>
				<p><h3><strong>Because of Who You Are!</strong></h3></p>
		<p>Yes, you read that right.  Not because of who we are, but because of who you are!  I will explain this further, but before I do, it is very important that I tell you about a life-changing experience.</p>
		<p>In 2004, I, Dr. Aaron Morland, graduated from Chiropractic College and started NRG Chiropractic in Chanhassen.  I started helping many people and things were going well.  However, as I have learned when God sees you getting comfortable, He will quickly make you uncomfortable.  I felt convicted that if I were to help people be as healthy as could be, I needed to inspire people to want to take great care of themselves and then teach them how to do that.  I realized that I could not accomplish this vision alone.  I started praying for God to put people in my life who could help me accomplish this vision that God laid on my heart.  Within 24 hours I was introduced to Christine Stueve and Leah Shirley who had been given the same vision from God.</p>
		<p>In 2005, we teamed up and formed Valeo {pronounced Vah-lay-o} Health and Wellness.  Since that day, we have been able to serve God in ways we could not do alone and we are seeing lives changed and saved because of it.</p>
		<p>That brings me to why you would come to our office - because of who you are!  Valeo was created by God with one thing in mind: to help people experience a greater life.  Valeo was created for YOU!  We are here to help you no matter where you are with your health.  Whether you are sick and need healing, or whether you are healthy and just want options to stay that way, we are here for you.  You were created by an awesome God; created for a purpose and we want to help you serve that purpose.  So, no matter where you are with your health, even if you think you no longer have hope, you can choose better options that will lead you to greater health.</p>
		<p>Do not let fear, doubt, finances, opinions, preconceived notions or time prevent you from experiencing a better, healthier life.</p>
		<p><strong>"I can do everything through him who gives me strength."</strong>&nbsp;&nbsp;Philippians 4:13</p>
		
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