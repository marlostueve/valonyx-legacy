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
				<h2><strong>What is a Hair Analysis (Tissue Mineral Analysis)?</strong></h2>
		<p>Trace mineral analysis is a test which measures the mineral content of your hair.  Mineral content of the hair reflects the mineral content of the body's tissues.  If a mineral deficiency or excess exists in the hair, it usually indicates a mineral deficiency or excess within the body, or bio-unavailability.</p>
		<p>Examples:  If your hair reveals an elevated calcium level two or three times normal, then your calcium level within the body may be elevated also.  Various mineral imbalances, as revealed by hair analysis frequently lead to metabolic dysfunctions before any symptoms become manifest.</p>
		<p><strong>Why test for minerals?</strong></p>
		<p>Minerals are the "sparkplugs" of life.  They are involved in almost all enzyme reactions within the body.  Without enzyme activity, life ceases to exist.  A trace mineral analysis is preventive as well as being useful as a screening tool.</p>
		<p><strong>What are common causes of mineral imbalances?</strong></p>
		<p>
                    <ol>
			<li>Improper diet - such as excessive intake of refined carbohydrates and sugars, strict vegetarian diets or other exclusive diets.</li>
			<li>Taking vitamins and minerals which are not compatible with your current body chemistry.</li>
			<li>Medications.</li>
			<li>Birth control pills.</li>
			<li>Stress.</li>
			<li>Accumulation of toxic metals from the environment, job or hobby.</li>
			<li>Inheritance of mineral patterns from parents.</li>
                    </ol>
		</p>
		<p>Hair analysis is an invaluable screening tool which allows a correct program of diet and supplementation to be designed for each individual's specific needs.  Never before has there been available a metabolic blueprint with such a degree of applicable scientific accuracy.</p>
		<p style="align: right;">Analytical Research Labs, Inc.</p>
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