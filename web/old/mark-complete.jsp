<%@ page contentType="text/html" import="java.util.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="userCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%

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
		<%@ include file="channels\channel-head-javascript.jsp" %>
	</head>

	<body onload="initErrors();">

		<div id="main">

<%@ include file="channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Mark <%= userCourse.getLabel() %> as Completed</p>
					<p>This screen allows your manager to mark an activity as completed.</p>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_AdvancedSearch">

					<struts-html:form action="/markComplete" focus="manager">

						<input id="dummy" name="dummy" type="hidden" />

						<div class="searchItem">

							<div class="left">MANAGER</div>
							<div class="right">

								<input name="manager" onfocus="select();" value="" size="37" maxlength="50" class="inputbox" style="width: 206px; margin-right: 4px;" />

							</div>
							<div class="end"></div>

						</div>

						<div class="searchItem">

							<div class="left">COMMENT</div>
							<div class="right">

								<textarea name="comment" rows="5" cols="35" class="textarea" style="width: 304px;"></textarea>

							</div>
							<div class="end"></div>

						</div>
						
						<p>&nbsp;</p>

						<p>
<%
if (userCourse.getType().equals(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME))
{
%>
							<input class="formbutton" onclick="javascript:return confirm('Are you sure you want to mark this Field Activity as completed? Remember, only Field Training Managers, District Managers and Route Supervisors are allowed to mark a Field Activity as completed.');" type="submit" value="Mark as Completed" alt="Mark as Completed" />
<%
}
else if (userCourse.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME))
{
%>
							<input class="formbutton" onclick="javascript:return confirm('Are you sure you want to mark this Classroom Activity as completed? Remember, only instructors are allowed to mark a Classroom Activity as completed.');" type="submit" value="Mark as Completed" alt="Mark as Completed" />
<%
}
%>
						</p>

					</struts-html:form>

				</div>

				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>




			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="channels\channel-footer.jsp" %>

		</div>

	</body>

</html>