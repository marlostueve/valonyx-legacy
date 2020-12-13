<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<jsp:useBean id="adminPracticeArea" class="com.badiyan.uk.online.beans.PracticeAreaBean" scope="session" />

<jsp:useBean id="selected_appointment_type" class="com.badiyan.uk.online.beans.AppointmentTypeBean" scope="session" />
<jsp:useBean id="selected_practitioner" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<%

String actionMessage = "[MESSAGE NOT FOUND]";
String action_str = (String)request.getParameter("action");
System.out.println("action_str >" + action_str);

String appt_id = (String)request.getParameter("id");
try {
	AppointmentBean appt = AppointmentBean.getAppointment(Integer.parseInt(appt_id));

	if (action_str.equals("approve")) {
		if (appt.getState() == AppointmentBean.PENDING_APPOINTMENT_STATUS) {
			appt.setState(AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
			appt.save();
			actionMessage = appt.getType().getLabel() + " appointment for " + appt.getClient().getLabel() + " has been approved at " + appt.getAppointmentDateTimeString();
		}
	} else if (action_str.equals("deny")) {
		AppointmentBean.delete(appt.getId());
		actionMessage = appt.getType().getLabel() + " appointment for " + appt.getClient().getLabel() + " has been denied.";
	}
	
	AppointmentBean.invalidateHash();
	
	CUBean.sendEmail(appt.getClient().getEmail1String(), "info@sanowc.com", "Sano Appointment Request", actionMessage);
	
} catch (ObjectNotFoundException x) {
	actionMessage = "Request has already been denied.";
} catch (Exception x) {
	x.printStackTrace();
}


%>

<!DOCTYPE html>
<html lang="en">
  <head>
<%@ include file="..\client\channels\channel-head.jsp" %>

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



    <div class="container">
			
      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">

        <h2>Appointment Action</h2>

		
		<p></p>
		
		<struts-html:form styleId="loginform" styleClass="form-signin" action="/client/scheduleConfirm">
			
				
			  
			  <h3><%= actionMessage %></h3>
			

			  <p>&nbsp</p>
			  <a style="margin-left: 10px;" class="btn btn-large" href="schedule-administration.jsp">Go home</a>
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

<%@ include file="..\client\channels\channel-footer.jsp" %>

    </div> <!-- /container -->

<%@ include file="..\client\channels\channel-foot-js.jsp" %>

  </body>
</html>
