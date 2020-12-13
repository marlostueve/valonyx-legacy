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
				<h2><strong>Contact Us</strong></h2>
				<p><b>Valeo Health & Wellness Center</b><br>470 West 78th Street, Suite 120<br>Chanhassen, MN 55317</p>
		<p>952-949-0676 (office)<br>952-949-0868 (fax)</p>
		
		<p><b>Hours</b><br>Mon, Thurs, Fri  7:00 a.m. to 6:00 p.m.<br>Tues/Wed 7:00 a.m. to 7:00 p.m.<br>Saturday by appointment only</p>
		
		<p><a href="privacy-policy.jsp">privacy policy</a></p>
		
		<table cellpadding="5" cellspacing="5">
		<tr>
		<td>
		<p><b>Chiropractic:</b></p>
		<p>Dr. Rob Lindsey, Doctor of Chiropractic<br><a href="mailto:drrlindsey@valeowc.com">drrlindsey@valeowc.com</a></p>
		<p>Dr. Aaron Morland, Doctor of Chiropractic<br><a href="mailto:dramorland@valeowc.com">dramorland@valeowc.com</a></p>
		<p><b>Acupuncture:</b></p>
		<p>Erica Dolan, L.Ac.<br><a href="mailto:edolan@valeowc.com">edolan@valeowc.com</a></p>
		</td>
		<td>
		<p><b>Massage:</b></p>
		<p>Leah Shirley, CMT, EDST<br><a href="mailto:lshirley@valeowc.com">lshirley@valeowc.com</a></p>
		<p>Darren Ellingson, CMT, CEAS<br><a href="mailto:dellingson@valeowc.com">dellingson@valeowc.com</a></p>
		<p><b>Naturopathy / Nutrition:</b></p>
		<p>Christine Stueve, Naturopath, EDST<br><a href="mailto:cstueve@valeowc.com">cstueve@valeowc.com</a></p>
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