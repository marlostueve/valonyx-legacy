<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
adminCourse = new CourseBean();
session.setAttribute("adminCourse", adminCourse);

listHelper.setCompany(adminCompany);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>

		<title>Universal Knowledge</title>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="generator" content="TextPad 4.6.2" />
		<meta name="author" content="" />
		<meta name="copyright" content="&copy; 2007" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />

		<link rel="stylesheet" type="text/css" href="../css/web.css" />

		<script type="text/javascript" src="../scripts/crir.js"></script>
		<!--[if lte IE 7]><script type="text/javascript" src="scripts/optionDisabledSupport.js"></script><![endif]-->
		
<%@ include file="..\channels\channel-head-javascript.jsp" %>

	</head>

	<body onload="javascript:initErrors();">

		<div id="main">

<%@ include file="..\channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Administration">

<%@ include file="channels\channel-admin-menu.jsp" %>

					<div class="main">

						<p class="headlineA">Add New Learning Activity</p>
						<p>Use this screen to add a new activity. Enter the activity name, select a type, enter a
description that will appear on users' Activity Detail screens, select the Course and Lesson
it will be contained within, and then select a contact. When you have entered all the
information, click <strong>Add New Activity</strong> to go to the Activity Details screens for this new
activity. You will then be able to add other information about this activity.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/newActivity" focus="titleInput" onsubmit="return validateCourseBasicForm(this);">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">ACTIVITY NAME</div>
								<div class="right">
									<input name="titleInput" onfocus="select();" value="" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">TYPE</div>
								<div class="right">
									<label for="radio1"><img src="../images/icnActivityELRN.gif" width="17" height="17" title="E-Learning" alt="E-Learning" /></label>
									<input name="typeSelect" id="radio1" type="radio" value="E-Learning Activity" class="crirHiddenJS" onclick="" checked="checked" />

									<label for="radio2"><img src="../images/icnActivityMNTR.gif" width="17" height="17" title="Mentor" alt="Mentor" /></label>
									<input name="typeSelect" id="radio2" type="radio" value="Mentor Activity" class="crirHiddenJS" onclick="" />

									<label for="radio3"><img src="../images/icnActivityILED.gif" width="17" height="17" title="Instructor-Led" alt="Instructor-Led" /></label>
									<input name="typeSelect" id="radio3" type="radio" value="Instructor-Led Activity" class="crirHiddenJS" onclick="" />

									<label for="radio4"><img src="../images/icnActivityASMT.gif" width="17" height="17" title="Assessment" alt="Assessment" /></label>
									<input name="typeSelect" id="radio4" type="radio" value="Assessment Activity" class="crirHiddenJS" onclick="" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DESCRIPTION</div>
								<div class="right">
									<textarea name="descriptionInput" rows="5" cols="35" class="textarea" style="width: 304px;"></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">COURSE</div>
								<div class="right">
								    <select name="groupSelect">
									<option value="0">-- NONE --</option>
<%
// I need to get the top level courses that can host activities...

//Iterator itr = CourseGroupBean.getCourseGroups(adminCompany, "Course").iterator();
Iterator itr = CourseBean.getRootCourses(adminCompany).iterator();
while (itr.hasNext())
{
    CourseBean course = (CourseBean)itr.next();
%>
									<option value="<%= course.getValue() %>"><%= course.getLabel() %></option>
<%
}
%>
								    </select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CONTACT</div>
								<div class="right">
                                                                    <select name="contactSelect">
									<option value="0">-- SELECT A CONTACT FOR THIS COURSE --</option>
<%
itr = listHelper.getPossibleCourseOwners().iterator();
while (itr.hasNext())
{
    PersonBean possible_course_owner = (PersonBean)itr.next();
%>
									<option value="<%= possible_course_owner.getValue() %>"><%= possible_course_owner.getLabel() %></option>
<%
}
%>
                                                                    </select>
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>

							<p><input class="formbutton" type="submit" value="Add New Activity" alt="Add New Activity" /></p>

						</struts-html:form>

					</div>

					<div class="end"></div>
				</div>

				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="..\channels\channel-footer.jsp" %>

		</div>

	</body>

</html>