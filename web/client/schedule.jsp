<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<jsp:useBean id="adminPracticeArea" class="com.badiyan.uk.online.beans.PracticeAreaBean" scope="session" />

<jsp:useBean id="selected_appointment_type" class="com.badiyan.uk.online.beans.AppointmentTypeBean" scope="session" />
<jsp:useBean id="selected_practitioner" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<%

//getNextAvailableAppointmentTimeForPractitioner(UKOnlineCompanyBean _company, UKOnlinePersonBean _practitioner, AppointmentTypeBean _type, Calendar _date, short _duration)
//AppointmentBean.getNextAvailableAppointmentTimeForPractitioner(_company, _practitioner, _type, _date, _duration);
	


Vector practice_areas = PracticeAreaBean.getPracticeAreas(userCompany);

Vector practitioners = null;
try
{
    if (request.getParameter("practice_area_id") != null)
    {
		if (request.getParameter("practice_area_id").equals("0"))
			adminPracticeArea = new PracticeAreaBean();
		else
		{
			adminPracticeArea = PracticeAreaBean.getPracticeArea(userCompany, Integer.parseInt(request.getParameter("practice_area_id")));
			practitioners = adminPracticeArea.getPractitioners();
		}
		session.setAttribute("adminPracticeArea", adminPracticeArea);
    }
    else
    {
		if (!adminPracticeArea.isNew())
			practitioners = adminPracticeArea.getPractitioners();
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
if (practitioners == null)
{
    //practitioners = UKOnlinePersonBean.getPractitioners(adminCompany);
    practitioners = new Vector();
    Iterator itr = practice_areas.iterator();
    while (itr.hasNext())
    {
		PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
		Iterator pa_prac_itr = practice_area.getPractitioners().iterator();
		while (pa_prac_itr.hasNext())
		{
			UKOnlinePersonBean practitioner = (UKOnlinePersonBean)pa_prac_itr.next();
			if (!practitioners.contains(practitioner) && practitioner.hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME))
			practitioners.addElement(practitioner);
		}
    }
}



Vector appt_types = AppointmentTypeBean.getAppointmentTypes(userCompany);


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
		
		<struts-html:form styleId="loginform" styleClass="form-signin" action="/client/scheduleFilter" focus="password">
				
			  <h3 class="form-signin-heading">Select a practitioner:</h3>

<%
Iterator practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext()) {
	UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
	boolean show = practitioner.allowClientScheduling();
	if (userCompany.getId() == 5) {
		if (!practitioner.getFirstNameString().equals("Christine")) {
			//show = false;
		}
	}
	if (show) {
%>
			  <button style="width: 100%; margin-top: 5px;" type="submit" name="practitioner_id" value="<%= practitioner.getValue() %>" class="btn btn-large<%= (practitioner.getId() == selected_practitioner.getId()) ? " btn-primary" : ""%>"><%= practitioner.getLabel() %></button>
<%
	}
}
%>

			  <p>&nbsp</p>
			  <a style="margin-left: 10px;" class="btn btn-large" href="index.jsp">Go back</a>
			  <!-- <button class="btn btn-large btn-primary" type="submit">Next &raquo;</button> -->
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
