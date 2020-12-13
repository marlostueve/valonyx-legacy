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
				<h2><strong>Benefits of Massage Therapy</strong></h2>
				<p>Massage is one of the ways you can improve and maintain excellent health.  Massage has a positive effect on every aspect of your body: emotionally, mentally and physically.  Massage is inexpensive and feels great!  How many things in this world can you say that about?  Some of the benefits to massage are the following:</p>
		<p><ul>
			<li>Relaxation/stress reduction</li>
			<li>Reduce tension & pain</li>
			<li>Improves concentration</li>
			<li>Increases metabolism</li>
			<li>Improves lymph drainage</li>
			<li>Promotes healing</li>
			<li>Removes toxins & waste</li>
			<li>Energizes tissues</li>
			<li>Muscle spasms are relieved</li>
			<li>Breakdown stressed tissues and realigns it</li>
			<li>Maintains muscle tone</li>
			<li>Refreshes & rejuvenates muscles & skin</li>
			<li>Increases circulation</li>
			<li>Provides nutrition to the muscle</li>
			<li>Relieves fatigue</li>
			<li>Aids in digestion and elimination</li>
			<li>Increases flexibility and range of motion</li>
			<li>Promotes better sleep</li>
			<li>Improves self-image</li>
		</ul></p>
		<p><img src="images/massage.png" style="float: left;" alt="">You are the only one who can decide to improve and care for yourself.  Proper care for your body should be a priority.  Massage is a healthy way to keep the body balanced and the systems functioning at peak levels.</p>
		<p><b>What is Myofascial Release?</b>&nbsp;<a href="forms/Valeo_Myofascial_Release-sweater.pdf">click here</a></p>
		<p><b>Client Testimonials</b></p>
		<p><i>Many professionals can massage but massage by Leah is absolutely healing. She reads what my body needs and her caring, encouraging nature calms the soul. I gladly drive the two hour round trip for my sessions with this gifted Massage Therapist.</i></p>
		<p>- Tim</p>
		<p><i>Each session is unique with the wonderful combination of Myofascial and Deep Tissue bodywork.  Being a monthly regular is important for my well-being, whether it's my neck, shoulders or back, Leah applies the right modality and technique to free it up.  I've been a client of Leah's for 6 years.  She has always provided a thorough session with specific focus on trouble spots.  My mid back and left shoulder are common areas of restriction and she certainly knows how to gently prepare the area for deep and effective results!</i></p>
		<p>- Richard</p>
		<p><i>I have suffered back pain for most of the last 10 years.  However, when I was pregnant with my twins, it became even worse.  Leah was able to customize my massage to help the specific needs of my situation with the pregnancy and give me much needed relief!  She was a life saver!</i></p>
		<p>- Gina </p>

		<p>"<i>As the world becomes even more technological, there is a growing need for human contact.</i>" - 2003 Touch Resource Guide, Associated Bodywork & Massage Professionals.</p>

		<p><b>Matthew 11:28-30</b> - "<i>Come to me, all you who are weary and burdened, and I will give you rest.  Take my yoke upon you and learn from me, for I am gentle and humble in heart, and you will find rest for your souls.</i>"</p>
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