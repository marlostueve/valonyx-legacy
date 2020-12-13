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
				<h2><strong>Self Defense Vision</strong></h2>
				<p><i>2 Timothy 1:7 - "God did not give us a spirit of timidity, but one of power and of love and of self discipline."</i></p>
		<p><image src="images/Christine-missions-163-small.jpg" style="float: right;" alt="">Self Defense for Women is a biblically based ministry committed to educating and training women of all ages to be confident, empowered and safe in the world around us. Walking with confidence is a huge part of the victory that women can and should have each day, anywhere, at all times. The Self Defense for Women ministry is designed to use God?s Word, Christine's testimony and practical instruction to help transform our fear and false sense of security into confidence. Assessing danger and dangerous situations, hands-on self defense training and creating a personal plan of action are a few of the elements within this ministry. Christine's testimony began the journey... God's power made this ministry.</p>
		<p><i>Ephesians 6:10-11 - "Finally, be strong in the Lord and in his mighty power. Put on the full armor of God so that you can take your stand against the devil's schemes."</i></p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Testimonials</strong></h2>
				<p><i>"Christine is amazing with her knowledge and tender heart. If I ever get in a scary situation, I am no longer fearful or completely at the mercy of the attacker. She also taught us some great ways to avoid dangerous situations and to be more aware of our surroundings. The class will give you a new sense of confidence."</i></p>
		<p>-Jennifer Calhoun</p>
		<p><i>"My daughter and I loved this class! The fact that we are learning about self defense together and able to talk through and understand the importance of it, in spite of our age difference, has been very affirming. Christine makes it so much fun - it's a riot to yell and punch the practice bag with all our strength! Very empowering! Thanks." </i></p>
		<p>-Jan and Missy Neville </p>
		<p><img src="images/2-girls.jpg" style="float: left;" alt=""><i>"Christine is a highly motivated person with an amazing testimony. Her energy and commitment to the Lord is contagious! The impact she had on our Jr. High students and mothers at Chapel Hill Academy was life changing. People left her class with a new confidence to respond quickly and the ability to know how to avoid threatening situations. It is great to see self-defense taught from a Christian perspective."</i></p>
		<p>-Jessica Santjer - Chapel Hill Academy</p>

		<p><i>"I think that your ministry and what God has done in your life is an amazing testimony to His grace and faithfulness!  I wish every woman I know could take your class and have the information you present.  God bless you!"</i></p>
		<p>-Karla Erickson</p>
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