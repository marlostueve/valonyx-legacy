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
				<h2><strong>What is Acupuncture?</strong></h2>
				<p>Acupuncture is one of the modalities of Traditional Chinese Medicine (TCM) which has been practiced for over 2,000 years and has helped billions of people.  Acupuncture is the insertion of disposable, fine needles into the body at specific points shown to be effective in the treatment of specific health problems.  Acupuncture points are stimulated to balance the movement of energy (Qi) in the body and promote natural healing.  Acupuncture is a safe, painless and effective way to treat a wide variety of medical problems.</p>
				<h2><strong>What can you expect from the initial consultation and treatment?</strong></h2>
				<p>Initial treatments last 70 - 90 minutes with the majority of that time spent discussing your health concerns and collecting a detailed history. There is also ample time for questions and answers. Expect your acupuncturist to look at your tongue (please don't brush your tongue or chew gum if you can remember!), take your pulse, and possibly palpate acupuncture points. You should wear loose clothes that are comfortable to lie down in for 30 minutes and be sure to eat within 4 hours of your appointment. Set aside enough time so that you are not rushing to and from your visit. Physical strain immediately before or after acupuncture can weaken your body.</p>
				<h2><strong>Does acupuncture hurt?</strong></h2>
				<p>No, acupuncture should not hurt.  Sometimes a momentary prick is felt at the insertion of the needle, but most patients say they barely feel the needle go in.  The needle is placed in specific acupuncture points to create stimulation and so therefore, the patient most likely will feel different sensations.  Some people describe the sensation as heavy, dull pressure, tingling and aching for example.  Most people are amazed by how relaxed they feel during and after the treatment.  This can only occur if the treatment is comfortable and gentle.</p>
				<h2><strong>How does acupuncture work?</strong></h2>
				<p>TCM theory includes the study of 14 meridians covering the entire body that connect with or represent organs.  Meridians are like rivers inside the body.  Qi, like water in a river, flows through meridians as an invisible current nourishing and energizing every cell, tissue, organ and muscle in the body.  Acupuncturists develop diagnostic skills to effectively evaluate the quality and quantity of Qi flowing within the body.  Licensed acupuncturists use tiny needles to stimulate specific points to help nurture your body back to health by helping resolve energy imbalances.</p>
				<h2><strong>How many treatments will I need?</strong></h2>
				<p>This is unique to the individual and the condition that is being treated.  Some people experience immediate relief, others may take months to achieve results.  Chronic conditions usually take longer to resolve than acute ones.  For example, if the problem is acute sometimes improvement is felt after one treatment and then maybe you will only need 3-5 treatments to resolve.  On the other hand, if you suffer from a chronic condition, it may take many treatments to help resolve.</p>
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