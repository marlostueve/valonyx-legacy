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

	<script type="text/javascript">

    
	function selectDistractor(disId)
    {
		if (document.getElementById('answer-dis-' + disId).value == 'off')
			document.getElementById('answer-dis-' + disId).value = 'on';
		else
			document.getElementById('answer-dis-' + disId).value = 'off';
		//alert(document.getElementById('answer-dis-' + disId).value);
    }


	</script>
	
  </head>
  <body>

<%@ include file="channels\channel-navbar.jsp" %>


    <div class="container">
			
      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">
<%
if (progress == null) {
%>
        <h2>Wellness Survey</h2>
<%
} else {
%>
        <h2>Wellness Survey - Score: <%= progress.getWellnessSurveyScore() %></h2>
<%
}
%>

        <p>On a scale of 1 to 10, 10 being highest, rate your current health level in each of the following areas of your life</p>
        <!-- <p><a href="#" class="btn btn-primary btn-large">Sign In &raquo;</a></p> -->
			
			
			
			
			
<%

if (loginBean.isLoggedIn())
{

	StemBean stem = null;
	try {
		stem = assessmentItr2.current();
	} catch (java.lang.ArrayIndexOutOfBoundsException a) {
		stem = assessmentItr2.previous();
	}
    
%>

						
			  <h2 class="form-signin-heading"><%= stem.getHeadingTextString() %> (Question <%= assessmentItr2.getPosition() + 1 %> of <%= assessmentItr2.getMaxPosition() %>)</h2>
			  <h3 class="form-signin-heading"><%= stem.getName() %></h3>

		<struts-html:form styleId="loginform" styleClass="form-signin" action="/client/wellnessSurvey">

			  <input type="hidden" name="position" value="<%= assessmentItr2.getPosition() %>">

<%
	if (stem.isFillInTheBlank()) {
		
		String fitbResponse = "";
		if (progress != null) {
			
			try {
				if (progress.hasInteraction(stem)) {
					EvaluationInteractionDb interaction = progress.retrieveInteraction(stem);
					fitbResponse = interaction.getFillInTheBlankResponse();
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
%>
			  <p><textarea onkeyup="javascript:waitOnKeyUp();" name="fitbResponse" rows="5" style="width: 100%; font-size: larger; font-weight: bolder;"><%= fitbResponse %></textarea></p>
<%
	} else if (stem.isCheckAllThatApply()) {
%>
			  <p><div class="btn-group" style="width: 100%" data-toggle="buttons-checkbox">
<%
		Iterator itr = stem.getDistractors().iterator();
		for (int i = 1; itr.hasNext(); i++)
		{
			DistractorBean distractor = (DistractorBean)itr.next();
			boolean is_selected_answer = false;
			if (assessmentItr2.currentQuestionAnswered())
			{
				try
				{
					DistractorBean answer = assessmentItr2.getCurrentDistractor();
					is_selected_answer = distractor.equals(answer);
				}
				catch (Exception p)
				{
					p.printStackTrace();
				}
			}
%>
				<p>
					<button style="width: 100%" type="button" name="<%= i %>" value="<%= distractor.getDisplayOrder() + "" %>" onclick="javascript:selectDistractor(<%= distractor.getId() %>);" class="btn"><%= distractor.getNameString() %></button>
					<input type="hidden" id="answer-dis-<%= distractor.getId() %>" name="answer-dis-<%= distractor.getId() %>" value="off">
				</p>
<%
		}
%>
			  </div></p>
<%
	} else {
		Iterator itr = stem.getDistractors().iterator();
		for (int i = 1; itr.hasNext(); i++)
		{
			DistractorBean distractor = (DistractorBean)itr.next();
			boolean is_selected_answer = false;
			if (assessmentItr2.currentQuestionAnswered())
			{
				try
				{
					DistractorBean answer = assessmentItr2.getCurrentDistractor();
					is_selected_answer = distractor.equals(answer);
				}
				catch (Exception p)
				{
					p.printStackTrace();
				}
			}
%>
			  <p><button name="answer" value="<%= distractor.getDisplayOrder() + "" %>" class="btn<%= is_selected_answer ? " btn-primary" : "" %> btn-large" type="submit" style="width: 100%" <%= is_selected_answer ? "checked=\"checked\" " : "" %> ><%= distractor.getNameString() %></button></p>
<%
		}
	}


		
    if (assessmentItr2.currentQuestionAnswered())
    {
        DistractorBean answer = assessmentItr2.getCurrentDistractor();
%>
						<!-- <p><strong><%= (answer.isCorrect()) ? "Correct!" : "Sorry, that's incorrect." %></strong></p> -->
						<!-- <p><%= answer.getPrescriptiveFeedback() %></p> -->
<%
    }
    else
    {
%>
						<!-- <p>&nbsp;</p> -->
<%
    }
%>
					</struts-html:form>

			
			
			
				
			
			
			
			
<%

    if (assessmentItr2.currentQuestionAnswered()) {
        if ((assessmentItr2.getPosition() + 1) != assessmentItr2.getMaxPosition()) {
%>
						<!-- <input class="formbutton" type="submit" name="submitButton" value="Next Question" alt="Next Question" /> -->
<%
        } else {
%>
						<!-- <input class="formbutton" type="submit" name="submitButton" value="Finish" alt="Finish" /> -->
<%
        }
    } else {
%>
						<!-- <input class="formbutton" type="submit" name="submitButton" value="Submit Answer" alt="Submit Answer" /> -->
<%
    }


%>
        <p>
<%
	if (assessmentItr2.hasPrevious()) {
%>
			<a href="wellness-survey.jsp?n=b" class="btn btn-primary btn-large">&laquo; Back</a>
<%
	} else {
%>
			<a href="index.jsp" class="btn btn-primary btn-large">&laquo; Back</a>
<%
	}

	//boolean hasNext = assessmentItr2.getPosition() + 1 < assessmentItr2.getMaxPosition();

	if (assessmentItr2.hasNext()) {
%>
			<a href="<%= stem.isFillInTheBlank() || stem.isCheckAllThatApply() ? "javascript:document.forms[1].submit();" : "wellness-survey.jsp?n=n" %>" class="btn btn-primary btn-large pull-right"><%= stem.isFillInTheBlank() || stem.isCheckAllThatApply() ? "Next" : "Skip" %> &raquo;</a>
<%
	} else {
%>
			<a href="<%= stem.isFillInTheBlank() ? "javascript:document.forms[1].submit();" : "index.jsp" %>" class="btn btn-primary btn-large pull-right">Finish &raquo;</a>
<%
	}

%>

		</p>
			
<%
}
%>

      </div>


      <hr>

<%@ include file="channels\channel-footer.jsp" %>

    </div> <!-- /container -->

<%@ include file="channels\channel-foot-js.jsp" %>

  </body>
</html>
