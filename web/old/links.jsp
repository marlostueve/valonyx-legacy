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
				<h2><strong>Links</strong></h2>
<table cellpadding="4">
			<tr>
				<td valign="top">
					<p><b>Chiropractic</b></p>
					<p>
						<ul>
							<li><a href="http://www.icpa4kids.com/">Kids & Chiropractic</a></li>
							<li><a href="http://www.chiro.org/">Chiropractic Research</a></li>
						</ul>
					</p>
					<p><b>EDS Scanning</b></p>
					<p>
						<ul>
							<li><a href="http://www.biomeridian.com/">BioMeridian</a></li>
							<li><a href="http://www.eavnet.com/">ElectroAcupuncture</a></li>
						</ul>
					</p>
					<p><b>Nutrition</b></p>
					<p>
						<ul>
							<li><a href="http://www.standardprocess.com/">Whole Food Nutrition</a></li>
							<li><a href="http://www.mealsmatter.org/">Meals Matter</a></li>
							<li><a href="http://www.glycoscience.org/">Glyconutrients</a></li>
							<li><a href="http://www.nutritionalwellness.com/">Nutritional Wellness</a></li>
							<li><a href="http://www.drsears.com/">Sears Lab</a></li>
							<li><a href="http://www.healthymomshealthyfamilies.com/">Healthy Moms Healthy Families</a></li>
							<li><a href="http://www.macnutoil.com/">Macadamia Nut Oil</a></li>
						</ul>
					</p>
				</td>
				<td valign="top">
					<p><b>Health Research</b></p>
					<p>
						<ul>
							<li><a href="http://www.mercola.com/">Dr. Mercola</a></li>
							<li><a href="http://nvic.org/">Vaccinations</a></li>
						</ul>
					</p>
					<p><b>Massage</b></p>
					<p>
						<ul>
							<li><a href="http://www.myofascialrelease.com/">Myofascial Release</a></li>
						</ul>
					</p>
					<p><b>CranioSacral Therapy</b></p>
					<p>
						<ul>
							<li><a href="http://www.upledger.com/home.htm">The Upledger Institute</a></li>
						</ul>
						<ul>
							<li><a href="http://www.iahe.com/html/therapies/cst.jsp">IAHE - CranioSacral Therapy</a></li>
						</ul>
					</p>
					<p><b>Professional Services</b></p>
					<p>
						<ul>
							<li><a href="http://www.markhochmd.com/">Dr. Mark Hoch</a></li>
							<li><a href="http://www.atgrace.com/ministries/counseling/theophosticmin.php">Theophostic Prayer</a></li>
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