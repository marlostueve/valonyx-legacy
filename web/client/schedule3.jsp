<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<jsp:useBean id="adminPracticeArea" class="com.badiyan.uk.online.beans.PracticeAreaBean" scope="session" />

<jsp:useBean id="selected_appointment_type" class="com.badiyan.uk.online.beans.AppointmentTypeBean" scope="session" />
<jsp:useBean id="selected_practitioner" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<%

String start_str = (String)request.getParameter("start");
Date appt_date = null;
if (start_str != null) {
	
	appt_date = new Date(Long.parseLong(start_str) * 1000);
	
}


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

        <h2>Schedule an Appointment</h2>

		
		<p></p>
		
		<struts-html:form styleId="loginform" styleClass="form-signin" action="/client/scheduleConfirm">
			
			<input type="hidden" name="start" value="<%= start_str %>" />
				
			  <h3 class="form-signin-heading">Selected Practitioner:</h3>
			  <h4><%= selected_practitioner.getLabel() %></h4>
			
			  <h3 class="form-signin-heading">Appointment Type:</h3>
			  <h4><%= selected_appointment_type.getLabel() %></h4>
			  
			  <h3 class="form-signin-heading">Appointment Date:</h3>
			  <h4><%= CUBean.getUserDateString(appt_date) %> - <%= CUBean.getUserTimeString(appt_date) %></h4>

			  <p>&nbsp</p>
			  <a style="margin-left: 10px;" class="btn btn-large" href="schedule2.jsp">Go back</a>
			  <button class="btn btn-large btn-primary" type="submit">Submit Request &raquo;</button>
		</struts-html:form>
<%

	String errorMessage = (String)session.getAttribute("forgot-password-message");
	if (errorMessage != null) {
%>
			<div class="alert <%= errorMessage.indexOf("Error") > -1 || errorMessage.indexOf("Failed") > -1 ? "alert-error" : "alert-success" %>">
				<button type="button" class="close" data-dismiss="alert">×</button>
				<h3><%= errorMessage.indexOf("Error") > -1 || errorMessage.indexOf("Failed") > -1 ? "Oh Snap!!" : "Success!!" %></h3>
				<%= errorMessage %>
			  
			</div>
<%
		session.removeAttribute("forgot-password-message");
	}
%>
      </div>

      <hr>

<%@ include file="channels\channel-footer.jsp" %>

    </div> <!-- /container -->

<%@ include file="channels\channel-foot-js.jsp" %>

  </body>
</html>
