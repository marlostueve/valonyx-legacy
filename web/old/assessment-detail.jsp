<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userAssessment" class="com.badiyan.uk.beans.AssessmentBean" scope="session" />
<jsp:useBean id="assessmentItr" class="com.badiyan.uk.beans.AssessmentIterator" scope="session" />

<jsp:useBean id="pendingEnrollmentList" class="com.badiyan.uk.beans.ListerBean" scope="session"/>
<jsp:useBean id="completedEnrollmentList" class="com.badiyan.uk.beans.ListerBean" scope="session"/>
<jsp:useBean id="userCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
if (request.getParameter("id") != null)
{
    try
    {
        int assessmentId = Integer.parseInt(request.getParameter("id"));
        userAssessment = AssessmentBean.getAssessment(assessmentId);
        session.setAttribute("userAssessment", userAssessment);
        assessmentItr.setAssessment(loginBean.getPerson(), userAssessment);
    }
    catch (NumberFormatException x)
    {
    }
}
pendingEnrollmentList.invalidateSearchResults();
completedEnrollmentList.invalidateSearchResults();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>

		<title>Ecolab</title>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="generator" content="TextPad 4.6.2" />
		<meta name="author" content="" />
		<meta name="copyright" content="&copy; 2007" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />

		<link rel="stylesheet" type="text/css" href="css/web.css" />

		<script type="text/javascript" src="scripts/crir.js"></script>

	</head>

	<body>

		<div id="main">

<%@ include file="channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
if (assessmentItr.hasNext())
{
    StemBean stem = assessmentItr.current();
%>
			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline"><%= userCourse.getLabel() %>&nbsp;(Question <%= assessmentItr.getPosition() + 1 %> of <%= assessmentItr.getMaxPosition() %>)</p>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Assessment">

					<div class="subnav">
						<!-- <a href="#" title="Sample Link">Sample Link</a> -->
						<!-- <span>Sample Link</span> -->
					</div>

					<struts-html:form action="/assessmentDetail">

						<input type="hidden" name="position" value="<%= assessmentItr.getPosition() %>">

						<!-- BEGIN QUESTION -->

						<p><strong><%= stem.getName() %></strong></p>
<%
    Iterator itr = stem.getDistractors().iterator();
    char display_label = 'A';
    String tfDisplay_label = "True";
    for (int i = 1; itr.hasNext(); i++)
    {
        DistractorBean distractor = (DistractorBean)itr.next();
	boolean is_selected_answer = false;
	if (assessmentItr.currentQuestionAnswered())
	{
	    try
	    {
		    DistractorBean answer = assessmentItr.getCurrentDistractor();
		    is_selected_answer = distractor.equals(answer);
	    }
	    catch (Exception p)
	    {
		p.printStackTrace();
	    }
	}
%>
						<p class="choice"><label for="radio<%= i %>"><strong><%= stem.isMultipleChoice() ? display_label + "." : tfDisplay_label %></strong>&nbsp;<%= distractor.getName() %></label><input name="answer" id="radio<%= i %>" type="radio" value="<%= distractor.getDisplayOrder() + "" %>" class="crirHiddenJS" onclick="" <%= is_selected_answer ? "checked=\"checked\" " : "" %>/></p>
<%
	tfDisplay_label = "False";
	display_label++;
    }
%>
<%
    if (assessmentItr.currentQuestionAnswered())
    {
        if ((assessmentItr.getPosition() + 1) != assessmentItr.getMaxPosition())
        //if (assessmentItr.hasNext())
        {
%>
						<input class="formbutton" type="submit" name="submitButton" value="Next Question" alt="Next Question" />
<%
        }
        else
        {
%>
						<input class="formbutton" type="submit" name="submitButton" value="Finish" alt="Finish" />
<%
        }
    }
    else
    {
%>
						<input class="formbutton" type="submit" name="submitButton" value="Submit Answer" alt="Submit Answer" />
<%
    }
%>

						<!-- END QUESTION -->
						<!-- BEGIN ANSWER -->

						<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
						<p style="margin-bottom: 2px;">&nbsp;</p>
<%
    if (assessmentItr.currentQuestionAnswered())
    {
        DistractorBean answer = assessmentItr.getCurrentDistractor();
%>
						<p><strong><%= (answer.isCorrect()) ? "Correct!" : "Sorry, that's incorrect." %></strong></p>

						<p><%= answer.getPrescriptiveFeedback() %></p>
<%
    }
    else
    {
%>
						<p>&nbsp;</p>
<%
    }
%>

						<!-- END ANSWER -->

					</struts-html:form>

				</div>

			</div>
			<!-- *** END Content *** -->
<%
}
%>
			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="channels\channel-footer.jsp" %>

		</div>

	</body>

</html>