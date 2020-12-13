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
				<h2><strong>What are essential oils (aromatherapy)?</strong></h2>
		<p style="font-weight: normal; font-size: 1em;" align="center"><i>Non-oily, highly active fluids that evaporate easily and completely.</i></p>
		<p style="font-weight: normal; font-size: 1em;" align="center"><i>They don't leave marks on your clothing or towels.</i></p>
		<p style="font-weight: normal; font-size: 1em;" align="center"><i>They may be taken in by inhalation, steams and infusers, or applied topically to the skin.  The therapeutic effects of true essential oils are due both to their pharmacological properties and to their small molecule size, which allows easy penetration through the skin, lymph system, walls of blood vessels and body tissues, pathways that impact the body's organ, hormonal, nervous and immune systems.</i></p>
				<h2><strong>How do essential oils work in the body?</strong></h2>
				<p>
				<ul>
				    <li><strong>Sense of smell</strong> - smell is the most rapid of all your senses because its information is directly relayed to the hypothalamus gland in the brain.    Motivation, moods, emotions and creativity all begin in the hypothalamus, so odors affect them immediately.  Essential oil molecules work through hormone-like chemicals to produce their sensations.  Odors can even influence the glands responsible for hormone levels, metabolism, insulin and stress levels, sex drive, body temperature and appetite.
					<ul>
					    <li>When inhaled, the odors stimulate a release of neurotransmitters, body chemicals responsible for pleasant feelings and pain reduction.  The aromas of apples and cinnamon, for example, have a powerful stabilizing effect on some people, especially those suffering from nervous anxiety.  These aromas are even capable of lowering blood pressure, and preventing severe panic attacks.</li>
					    <li>Scents are also intimately intertwined with thought and memory.  Studies on brain-waves show that scents like lavender increase alpha brain waves associated with relation; scents like jasmine boost beta waves linked to alertness; other scents enhance your emotional equilibrium merely by inhaling them.  Yet, aromatherapy oils effect different people in different ways, on different levels.</li>
					    </ul>
				    </li>
				    <li><strong>Electrical frequency</strong> - a healthy body has a frequency between 62 to 78Hz.  Disease frequency rates begin at 58Hz.  A higher frequency rate destroys an entity of lower frequency.  Based on this knowledge, it's easy to see that certain high frequency essential oils can create an environment in which disease, bacteria, viruses and fungus cannot live.
					<ul>
					    <li>A majority of essential oils can affect pathogenic organisms that are resistant to chemical antibiotics.  They may turn out to be a good choice for overcoming today's virulent supergerms.</li>
					    
					    </ul>
				    </li>
				    </ul></p> 
				    
				<h2><strong>How do essential oils assist in the body's naturally occurring healing processes?</strong></h2>
				
				<p>
				<ul>
				    <li>Stimulate immune response</li>
				    <li>Essential oil molecules can reach every cell of the body in just 20 minutes</li>
				    <li>Essential oil molecules have an electromagnetic charge</li>
				    <li>Natural antioxidants that destroy free radicals</li>
				    <li>Antibiotic properties</li>
				    <li>Act as blood purifiers and normalizers</li>
				    <li>Improve lymph system efficiency</li>
				    <li>Stimulate the removal of heavy mucous from the lungs and bronchial tubes</li>
				    <li>Essential citrus oils help release excess fluid retention</li>
				    <li>Help normalize body chemistry</li>
				    <li>Can pass the blood/brain barrier</li>
				    <li>Improve skin tone</li>
				    <li>Can boost energy</li>
				    </ul></p>
				    
				    <p style="font-weight: normal; font-size: 1em;" align="center"><i>See your alternative health care practitioner for essential oil recommendations.</i></p>
				    <p style="font-weight: bold; font-size: 1em;" align="right"><i>Linda Page, Ph.D</i></p>
				    <p style="font-weight: bold; font-size: 1em;" align="right"><i>Healthy Healing, 12th Edition</i></p>
				    
				<h2><strong>Testimonial</strong></h2>
				
				<p style="font-weight: normal; font-size: 1em;" align="center"><i>Organic, purified essential oils from France have been a key part of my own personal healing journey.</i></p>
				<p style="font-weight: normal; font-size: 1em;" align="center"><i>I love how lavender helps me to relax when stressed and to sleep when hard to "shut my mind down."</i></p>
				<p style="font-weight: normal; font-size: 1em;" align="center"><i>I also use peppermint essential oil daily by inhalation and on sore or tense muscles - works much better than anything I have tried out on the market.</i></p>
				<p style="font-weight: normal; font-size: 1em;" align="center"><i>Combine these with regular massage and you very well may experience a much more joyful life - I know I have!!!!!!</i></p>
				<p style="font-weight: bold; font-size: 1em;" align="right"><i>Christine Stueve, Naturopath</i></p>
				
				
				
		
		
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