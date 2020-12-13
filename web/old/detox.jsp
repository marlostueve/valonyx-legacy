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
				<h2><strong>Detoxification</strong></h2>
				<p><strong>What does detoxification mean?</strong></p>
		<p>Body purification that naturally occurs on a daily basis through the colon, liver, kidneys, lungs, lymph and skin.  The purpose is to neutralize and eliminate toxins ingested and/or exposed to externally that are harmful to health and well-being.  We continually detoxify just as our heart and lungs function without ceasing.  The accumulation of toxic matter is a serious threat in our society today to the health of the human body.</p>
		<p>Signs that you may have reached a toxic tolerance level:</p>
		<p>
		                    <ol>
			<li>Frequent, unexplained headaches, back or joint pain, or arthritis?</li>
			<li>Chronic respiratory problems, sinus problems or asthma?</li>
			<li>Abnormal body odor, bad breath or coated tongue?</li>
			<li>Chronic stress from an unhealthy work environment or home life?</li>
			<li>Environmental sensitivities, especially to odors?</li>
			<li>Food allergies, poor digestion or constipation with intestinal bloating or gas?</li>
			<li>Unusually poor memory, chronic insomnia, depression, irritability, chronic fatigue?</li>
			<li>Brittle nails and hair, psoriasis, adult acne, unexplained weight gain over 10 pounds?</li>
                    </ol>
		    </p>
		<p>Tools such as hair analysis, saliva hormone testing and electro-dermal scanning can shed light on the need for detoxification.</p>
		<p>Healthy Healing, 12th Edition</p>
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