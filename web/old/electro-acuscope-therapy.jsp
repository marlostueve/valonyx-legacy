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
				<h2><strong>Electro-Acuscope Therapy</strong></h2>
				<p>The Electro-Acuscope is a sophisticated FDA approved physical therapy instrument that introduces a painless, low- voltage micro-current to tissues such as muscle, bone and neural tissue.   The instrument literally regenerates injured tissue by repairing damage at the cellular level.</p>
				<p>It was invented by Anthony Nebrinski, the inventor of the EKG, EEG, and EMG machines used in hospitals today.</p>
				<p>Because of its profound effects on tissue repair, the Electro-Acuscope can be used successfully in a broad range of neuro-musculo-skeletal disorders - both chronic and acute. When treating acute injuries, recovery time is usually cut in half!  In the treatment of chronic conditions, patients who had lost hope of ever finding relief are amazed at how rapid and long lasting the effects of the treatment are.</p>
				<h2><strong>How it Works</strong></h2>
				<p>The human body functions through electrical conductivity.  When a person is injured, the body's normal conductivity in the damaged tissue is altered.  This condition is generally accompanied by pain in the affected area and often results in the body's inability to completely repair itself.</p>
				<p>The Electro-Acuscope works on a biofeedback system whereby it recognizes "normal" tissue and identifies specific areas of tissue damage, then determines the exact frequency necessary to repair the damaged tissue cell.  As the area begins to repair and the requirements of the tissue cells change, the Electro-Acuscope detects the new tissue requirements and adjusts its waveform to accommodate the specific needs of the regenerating tissue.  Unlike other electro-therapies, <strong>there is no danger of over-treatment</strong>.  The machine <strong>cannot</strong> cause damage to tissue.</p>
				<h2><strong>How it differs from other devices</strong></h2>
				<p>The Electro-Acuscope is different from conventional forms of electrical stimulation (i.e. T.E.N.S., Interferential therapy, and galvanic stim) in that it facilitates tissue repair and promotes cell regeneration at an accelerated rate.</p> 
				<p>In contrast to all milli-amperage nerve stimulation devices (ordinary TENS) designed to bombard the tissue and block pain signals from reaching the brain, the Electro-Acuscope generates only the level of current required to gently encourage nerve fiber to return to conduction of normal electrical impulses.  A series of treatments has a positive cumulative effect which has proven to be long-lasting in contrast to ordinary TENS devices which provide only temporary relief.</p>
				<p>Medical doctors from reputable clinics such as Loma Linda, Cedar Sinai, and Maui Memorial Hospital are referring people with all types of problems including the following:</p>
				<h2><strong>INDICATION FOR ACUSCOPE THERAPY</strong></h2>
				<p><strong>
				    <table border="1" cellpadding="3" cellspacing="0">
					   <tr>
						<td valign="top" width="33%">Accelerated wound healing</td><td valign="top" width="34%">Frozen Shoulder</td><td valign="top" width="33%">Sciatica</td>
					   </tr>
					   <tr>
						<td valign="top">Acute trauma</td><td valign="top">Headaches/Sinus conditions</td><td valign="top">Shin splints</td>
					   </tr>
					   <tr>
						<td valign="top">Adhesive capsulitis</td><td valign="top">Heel Pain</td><td valign="top">Skin Ulcers</td>
					   </tr>
					   <tr>
						<td valign="top">Anxiety</td><td valign="top">Keloid Scars</td><td valign="top">Sleep disorders</td>
					   </tr>
					   <tr>
						<td valign="top">Arthritis</td><td valign="top">Lymphatic drainage</td><td valign="top">Spinal injuries</td>
					   </tr>
					   <tr>
						<td valign="top">Burns</td><td valign="top">Muscle strains, spasms</td><td valign="top">Sprained ankles</td>
					   </tr>
					   <tr>
						<td valign="top">Bursitis</td><td valign="top">Post-op joint replacements</td><td valign="top">Strained ligaments</td>
					   </tr>
					   <tr>
						<td valign="top">Carpal Tunnel Syndrome</td><td valign="top">Post-op shoulder, back, knee, etc...</td><td valign="top">Tendonitis</td>
					   </tr>
					   <tr>
						<td valign="top">Degenerative joint</td><td valign="top">Radiating arm/leg pain</td><td valign="top">Tennis elbow</td>
					   </tr>
					   <tr>
						<td valign="top">Depression</td><td valign="top">Reduction of post-op swelling/edema</td><td valign="top">TMJ disorder</td>
					   </tr>
					   <tr>
						<td valign="top">Fractures</td><td valign="top">Rotator Cuff injuries</td><td valign="top">Whiplash injuries</td>
					   </tr>
					   
				    </table>
				</strong></p>
				<p>The concept of micro-current stimulation has achieved widespread acceptance, even by many of the most conservative medical pain-management facilities, since no one can deny the validity of consistent results.</p>
				<p>These results are, of course, dependent on many variables and are often times patient specific.  Results for the same indications may vary from person to person and diagnosis to diagnosis.</p>
				
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