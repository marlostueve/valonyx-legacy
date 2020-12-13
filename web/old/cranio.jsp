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
				<h2><strong>CranioSacral Therapy</strong></h2>
				<p>CST was pioneered and developed by osteopathic physician John E. Upledger following extensive scientific studies from 1975 to 1983 at Michigan State University, where he served as a clinical researcher and Professor of Biomechanics.</p>
		<p>CST is a gentle, hands-on method of evaluating and enhancing the functioning of a physiological body system called the craniosacral system - comprised of the membranes and cerebrospinal fluid that surround and protect the brain and spinal cord.</p>
		<p>Using a soft touch generally no greater than 5 grams, or about the weight of a nickel, practitioners release restrictions in the craniosacral system to improve the functioning of the central nervous system.</p>
		<p>By complementing the body's natural healing processes, CST is increasingly used as a preventive health measure for its ability to bolster resistance to disease, and is effective for a wide range of medical problems associated with pain and dysfunction, including some of the following:</p>
		<table cellpadding="3">
			<tr>
				<td valign="top">
					<ul>
						<li>Migraine Headaches</li>
						<li>Motor Coordination Impairments</li>
						<li>Colic</li>
						<li>Orthopedic Problems</li>
						<li>Scoliosis</li>
						<li>Learning Disabilities</li>
						<li>Emotional Difficulties</li>
						<li>Fibromyalgia and other connective tissue disorders</li>
						<li>Neurovascular or Immune Disorders</li>
						<li>Post-Surgical Dysfunction</li>
					</ul>
				</td>
				<td valign="top">
					<ul>
						<li>Chronic Neck and Back Pain</li>
						<li>Central Nervous System Disorders</li>
						<li>Autism</li>
						<li>Traumatic Brain & Spinal Cord Injuries</li>
						<li>Infantile Disorders</li>
						<li>Chronic Fatigue</li>
						<li>Stress & Tension Related problems</li>
						<li>Temporomandibular Joint Syndrome (TMJ)</li>
						<li>Post Traumatic Stress Disorder</li>
						<li>Many other conditions</li>
					</ul>
				</td>
			<tr>
		</table>
		<p>By complementing the body's natural healing processes, CST is increasingly used as a preventive health measure for its ability to bolster resistance to disease, and is effective for a wide range of medical problems associated with pain and dysfunction, including some of the following:</p>
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