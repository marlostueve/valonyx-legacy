<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userAssessment" class="com.badiyan.uk.beans.AssessmentBean" scope="session" />
<jsp:useBean id="assessmentItr" class="com.badiyan.uk.beans.AssessmentIterator" scope="session" />
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
    catch (Exception x)
    {
    }
}

int assessmentScore = 0;
try
{
    assessmentScore = loginBean.getPerson().getAssessmentScore(userAssessment);
}
catch (Exception x)
{
}
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

	</head>

	<body>

		<div id="main">

<%@ include file="channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Assessment</p>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Details">

					<div class="subnav">
					    <struts-html:form action="/assessmentDetail">
<%
if (assessmentItr.hasNext())
{
%>
						<a href="javascript:document.forms[0].submit();" title="Begin Assessment">Begin Assessment</a><input type="hidden" name="submitButton" value="Begin Assessment">
<%
}
else if (userAssessment.allowRetake())
{
%>
						<a href="javascript:document.forms[0].submit();" title="Retake Assessment">Retake Assessment</a><input type="hidden" name="submitButton" value="Retake Assessment">
<%
}
else
{
%>
						<span>Begin Assessment</span>
<%
}
%>
					    </struts-html:form>
					</div>

					<div class="detailItem">
						<div class="left">TITLE</div>
						<div class="right"><%= userCourse.getLabel() %></div>
						<div class="end"></div>
					</div>
					<div class="detailItem">
						<div class="left">PASS&nbsp;&#47;&nbsp;FAIL&nbsp;&#37;</div>
						<div class="right"><%= userAssessment.getPassFailPercentile() %></div>
						<div class="end"></div>
					</div>
					<div class="detailItem">
						<div class="left">YOUR SCORE</div>
						<div class="right"><%= (assessmentItr.hasNext()) ? "Not Complete" : assessmentScore + "" %></div>
						<div class="end"></div>
					</div>
					<div class="detailItem">
						<div class="left">INTRODUCTION</div>
						<div class="right"><%= userAssessment.getIntroduction() %></div>
						<div class="end"></div>
					</div>
					<div class="detailItem">
						<div class="left">INSTRUCTIONS</div>
						<div class="right"><%= userAssessment.getInstructions() %></div>
						<div class="end"></div>
					</div>

				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="channels\channel-footer.jsp" %>

		</div>

	</body>

</html>