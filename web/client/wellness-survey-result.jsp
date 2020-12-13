<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<jsp:useBean id="userAssessment2" class="com.badiyan.uk.beans.AssessmentBean" scope="session" />
<jsp:useBean id="assessmentItr2" class="com.badiyan.uk.beans.AssessmentIterator" scope="session" />

<%

String n = request.getParameter("n");
if (n != null) {
	if (n.equals("b")) {
		assessmentItr2.previous();
	} else if (n.equals("n")) {
		assessmentItr2.next();
	}
}

if (userAssessment2.isNew())
{
	
	// find the client intake form assessment for this company...

	try {
		
		userAssessment2 = AssessmentBean.getAssessment(userCompany, "Client Wellness Survey");
		session.setAttribute("userAssessment2", userAssessment2);
	} catch (com.badiyan.uk.exceptions.ObjectNotFoundException x) {
		x.printStackTrace();
	}

	
	assessmentItr2.setAssessment(loginBean.getPerson(), userAssessment2);
}

EvaluationProgress progress = (EvaluationProgress)session.getAttribute("evaluationProgress2");

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
        max-width: 600px;
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
	
<%

Vector progressVec = new Vector();
boolean hasMultProgress = false;
try {
	
        progressVec = EvaluationProgress.getProgress((UKOnlinePersonBean)loginBean.getPerson(), userAssessment2);
        hasMultProgress = (progressVec.size() > 1);
        if (hasMultProgress) {
            Iterator progressItr = progressVec.iterator();


%>
	
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          ['Date', 'Results']<%= progressItr.hasNext() ? "," : "" %>
<%
	while (progressItr.hasNext()) {
		EvaluationProgress progressObj = (EvaluationProgress)progressItr.next();
%>
          ['<%= CUBean.getUserDateString(progressObj.getProgressDate()) %>',  <%= progressObj.getWellnessSurveyScore() %>]<%= progressItr.hasNext() ? "," : "" %>
<%
	}
%>
        ]);

        var options = {
          title: 'Wellness Survey Results'
        };

        var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
    </script>
<%
   }
} catch (Exception x) {
	x.printStackTrace();
}
%>
	
  </head>
  <body>

<%@ include file="channels\channel-navbar.jsp" %>


    <div class="container">
			
      <div class="hero-unit">
<%
if (progress == null) {
%>
        <h1>Wellness Survey Result</h1>
<%
} else {
%>
        <h1>Wellness Survey Result - Score: <%= progress.getWellnessSurveyScore() %></h1>
<%
}
%>
			
<%

if (loginBean.isLoggedIn() && hasMultProgress) {
%>


		
<div id="chart_div" style="width: 100%; height: 400px;"></div>
			
			
			
			
<%
} else {
%>
<p>Thank you for completing your first wellness survey baseline score. You will see your progression graph appear with the next survey completion.</p>
<%
}
%>

<p><a class="btn btn-large btn-primary" href="index.jsp">Go Home &raquo;</a></p>

      </div>


      <hr>

<%@ include file="channels\channel-footer.jsp" %>

    </div> <!-- /container -->

<%@ include file="channels\channel-foot-js.jsp" %>

  </body>
</html>
