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
				<h2><strong>What is Naturopathy?</strong></h2>
				<p>Naturopathy is a system, which is concerned with the whole person, rather than the problems afflicting his/her various organs and systems. Naturopathy recognizes and uses the fact that the body is a self-healing organism, working with the knowledge that if the right environment and opportunity for self-healing can be created; repair, recovery and good health will result.</p>
		
		<p><i>"Naturopathy is strictly scientific, seeing every manifestation of disorder as the result of an understandable cause, or more likely several causes.  
Naturopathy has existed since the days of Hippocrates and provides a comprehensive system of healing which encourages natural immunity." - Stewart Mitchell</i></p>



		<p>Naturopathy consists of a variety of biologically based methods for health promotion, using nutrition plans and herbs, posture, deep breathing, massage, exercise and water therapies, while addressing the body as a whole emotionally, mentally, physically, spiritually.</p>
		<p>Many forces in our world today are working against our living in an optimal environment.  Toxicity and stress as a result of our food, environment, relationships and world views have created much need for focusing on what the bodies' needs are at the cellular level.  Education regarding the study of the cell and/or cellular communication (Glycobiology) within the body is the key to understanding how the breakdown of digestion, immunity and endocrine systems occurs, for example.  Once informed, it becomes easier to incorporate necessary changes to one's lifestyle for optimal health.</p>
		<p>According to Linda Page, Ph.D., Traditional Naturopath, Naturopathy is founded on five therapeutic principles:</p>
		<p>
                    <ol>
			<li><p><strong>Nature is a powerful healing force.</strong><br />This is the belief tht the body has considerable power to heal itself.  The role of the naturopath is to facilitate and enhance this process by educating the client in various approaches to stimulating his or her own internal healing force.  Above all, the naturopath must do no harm.</p></li>
			<li><p><strong>The person is viewed as a whole.</strong> </br>Understanding the client as an individual is essential.  The naturopath must work to understand the client's complex interaction of physical, emotional, spiritual and social factors.</p></li>
			<li><p><strong>The goal is to identify and address the cause of the problem. </strong><br />Naturopathy does not deal in suppressing symptoms, since symptoms are seen as expressions of the body's attempt to heal itself.  Rather it seeks to understand the underlying causes of a disease, which can spring from interacting levels of disharmony in the body, physical, mental, emotional and spiritual.<br />Some illnesses are the result of spiritual disharmony, experienced as a felling of deep unease or inadequate strength of will necessary to support the healing process.  Naturopaths can play an important role in helping clients to discover the appropriate action for overcoming the disharmony.  Underlying causes, like diet or stress may also play a role in symptoms like ear infections, inflammation or fever.  The naturopath can also address the interactive relationship of these conditions and guide the client in finding a natural technique that can alleviate the stress and help to stimulate recovery.</p></li>
			<li><p><strong>The naturopath is a teacher.</strong><br />First and foremost, the naturopath is a teacher, educating, empowering and motivating the client to assume more personal responsibility for his or her own wellness by adopting a healthy attitude, lifestyle and diet.  After identifying the conditions that cause the ill health, the naturopath discusses with the client the various methods for creating a return to health.</p></li>
			<li><p><strong>Prevention is the best approach.</strong><br />Prevention is best accomplished by lifestyle habits which support health.</p></li>
                    </ol>
		</p>
		
		
		<!--
		
		<p>Naturopathy is a system, which is concerned with the whole person, rather than the problems afflicting his/her various organs and systems. Naturopathy recognizes and uses the fact that the body is a self-healing organism, working with the knowledge that if the right environment and opportunity for self-healing can be created; repair, recovery and good health will result.</p>







		<p><b>What is Hair Analysis?</b></p>
		<p>Trace mineral analysis is a test which measures the mineral content of your hair.  Mineral content of the hair reflects the mineral content of the body's tissues.  If a mineral deficiency or excess exists in the hair, it usually indicates a mineral deficiency or excess within the body, or biounavailability.</p>
		<p>Various mineral imbalances, as revealed by hair analysis frequently lead to metabolic dysfunctions before any symptoms become manifest.</p>
		<p>Minerals are the "sparkplugs" of life.  They are involved in almost all enzyme reactions within the body.  Without enzyme activity, life ceases to exist. A trace mineral analysis is preventive as well as being useful as a screening tool.</p>
		<p>Hair analysis is an invaluable screening tool which allows a correct program of diet and supplementation to be designed for each individual's specific needs.  Never before has there been available a metabolic blueprint with such a degree of applicable scientific accuracy.</p>
		<p><i>Portions of this page &copy; Analytical Research Labs, Inc.</a>&#153;</i></p>

		<p><b>What is Nutrition Response Testing (NRT)?</b></p>
		-->
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