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
				<h2><strong>Forms</strong></h2>
				<table cellpadding="4" cellspacing="5">
			<tr>
				<td valign="top">
					<!-- <p><b>New Forms</b></p> -->
					<p>
						<ul>
							<li><a target="_blank" href="http://www.valeowc.com/forms/AuthforCare09.pdf">Authorization of Care</a></li>
							<li><a target="_blank" href="http://www.valeowc.com/forms/ClientBillofRights.pdf">Client Bill of Rights</a></li>
							<li><a target="_blank" href="http://www.valeowc.com/forms/HealthHistory.pdf">Health History</a></li>
							<li><a target="_blank" href="http://www.valeowc.com/forms/HIPAAPolicy.pdf">Notice of Privacy Practices</a></li>
							<li><a target="_blank" href="http://www.valeowc.com/forms/PaymentPolicy.pdf">Payment Policy</a></li>
						</ul>
					</p>
					<!--
					<p><b>Chiropractic</b></p>
					<p>
						<ul>
							<li><a href="forms/Valeo_Chiropractic_In-take_History_Form-Adult.pdf">Chiropractic In-take History Form (Adult)</a></li>
							<li><a href="forms/Valeo_Chiropractic_In-take_History_Form-Child.pdf">Chiropractic In-take History Form (Child)</a></li>
							<li><a href="forms/Valeo_Terms_of_acceptance.pdf">Terms of Acceptance</a></li>
						</ul>
					</p>
					<p><b>Massage</b></p>
					<p>
						<ul>
							<li><a href="forms/Valeo_Massage_Health_History.pdf">Health History</a></li>
						</ul>
					</p>
					<p><b>Acupuncture</b></p>
					<p>
						<ul>
							<li><a href="forms/Valeo_acupuncture_patient_health_history.pdf">Patient Health History</a></li>
						</ul>
					</p>
					-->
				</td>
				<!--
				<td valign="top">
					<p><b>Naturopathy</b></p>
					<p>
						<ul>
							<li><a href="forms/Valeo_Client_Health_Survey_001.pdf">Client Health Survey</a></li>
							<li><a href="forms/Client_Health_Survey_002.pdf">Symptom Survey Form</a></li>
							<li><a href="forms/Client_Health_Survey_003.pdf">Toxicity Symptoms Questionnaire</a></li>
							<li><a href="forms/Valeo_EDS_Consent_Form.pdf">EDS Consent Form</a></li>
							<li><a href="forms/Valeo_Late_no_show_policy.pdf">Late Policy & Procedures</a></li>
							<li><a href="forms/Valeo_Client_Bill_of_Rights.pdf">Client Bill of Rights</a></li>
							<li><a href="forms/Valeo_HIPAA_Signature_Page.pdf">HIPAA Acknowledgement Of Receipt of Notice</a></li>
							<li><a href="forms/Valeo_Insurance_Patient_Pment_Agreement.pdf">Patient Insurance Payment Agreement</a></li>
							<li><a href="forms/Valeo_Credit_Card_Preauth.pdf">Credit Card Preauthorization</a></li>
							<li><a href="forms/Valeo_HIPAA_Policy.pdf">HIPAA Policy</a></li>
						</ul>
					</p>
				</td>
				-->
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