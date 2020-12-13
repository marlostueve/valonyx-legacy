<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<%
userCompany = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5);
session.setAttribute("userCompany", userCompany);
%>

<!DOCTYPE html>
<html lang="en">
  <head>
<%@ include file="channels\channel-head.jsp" %>

    <style type="text/css">
      body {
        background-color: #f5f5f5;
      }

      .form-signin {
        max-width: 400px;
        padding: 19px 29px 29px;
        margin: 0 auto 20px;
        background-color: #fff;
        border: 1px solid #e5e5e5;
        -webkit-border-radius: 5px;
           -moz-border-radius: 5px;
                border-radius: 5px;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
           -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
                box-shadow: 0 1px 2px rgba(0,0,0,.05);
      }
      .form-signin .form-signin-heading,
      .form-signin .checkbox {
        margin-bottom: 10px;
      }
      .form-signin input[type="text"],
      .form-signin input[type="password"] {
        font-size: 16px;
        height: auto;
        margin-bottom: 15px;
        padding: 7px 9px;
      }

    </style>

  </head>
  <body>

<%@ include file="channels\channel-navbar.jsp" %>


    <div class="container">
			
      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">
<%
if (!loginBean.isLoggedIn()) {
%>
        <h1>Welcome to Sano Wellness Center!</h1>
<%
} else {
%>
        <h1>Hello <%= loginBean.getPerson().getFirstNameString() %>!</h1>
<%
}
%>

		<p>Our Vision is to address the root causes of health issues in order to facilitate permanent healing.</p>

<%

if (!loginBean.isLoggedIn()) {
%>
		<struts-html:form styleId="loginform" styleClass="form-signin" action="/client/login" focus="password">
			  <h2 class="form-signin-heading">Please sign in</h2>
			  <input type="password" name="password" class="input-block-level" placeholder="Password">
			  <button class="btn btn-large btn-primary" type="submit">Sign In &raquo;</button>
			  <a style="margin-left: 10px;" class="btn btn-large" href="forgot-password.jsp">I don't have my password</a>
		</struts-html:form>
<%

	String errorMessage = (String)session.getAttribute("forgot-password-message");
	if (errorMessage != null) {
%>
			<div class="alert alert-error">
				<button type="button" class="close" data-dismiss="alert">×</button>
				<h3>Oh Snap!!</h3>
				<strong>Unable to sign you in.</strong>  If you don't have your password, click <strong>I don't have my password</strong> above or ask the front desk for assistance.
			  
			</div>
<%
		session.removeAttribute("forgot-password-message");
	}

}
%>
		


      </div>
<%
if (loginBean.isLoggedIn()) {
%>

      <div class="row">
        <div class="span12">
          <h4> When you have completed your intake form and wellness survey, please sign out in the upper right corner of the screen.</h4>
        </div>
      </div>



      <!-- Example row of columns -->
      <div class="row">
        <div class="span4">
          <h2>Intake Form</h2>
          <p>Thank you for completing the following questions prior to your appointment time.</p>
          <p><a class="btn btn-large" href="intake.jsp">View Intake Form &raquo;</a></p>
        </div>
        <div class="span4">
          <h2>Wellness Survey</h2>
          <p>Watch your healing progress! Answer the following 10 questions to view your health chart.</p>
		  <p><a class="btn btn-large" href="wellness-survey.jsp">View Wellness Survey &raquo;</a></p>
<%
try {
	boolean hasResults = EvaluationProgress.hasCompletedProgress((UKOnlinePersonBean)loginBean.getPerson(), AssessmentBean.getAssessment(userCompany, "Client Wellness Survey"));
	System.out.println("hasResults >" + hasResults);
	if (hasResults) {
%>
          <p><a class="btn btn-large" href="wellness-survey-result.jsp">View Survey Results &raquo;</a></p>
<%
	}
} catch (Exception x) {}
%>
       </div>
	   
        <div class="span4">
          <h2>Schedule Appointment</h2>
          <p>Click to view available appointment slots and make a request to schedule.</p>
          <p><a class="btn btn-large" href="schedule.jsp">Schedule an Appointment &raquo;</a></p>
        </div>
		
      </div>
<%
}
%>

      <hr>

<%@ include file="channels\channel-footer.jsp" %>

    </div> <!-- /container -->

<%@ include file="channels\channel-foot-js.jsp" %>

  </body>
</html>
