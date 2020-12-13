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
if (request.getParameter("id") != null)
{
    try
    {
	int courseId = Integer.parseInt(request.getParameter("id"));
	adminCourse = CourseBean.getCourse(courseId);
	session.setAttribute("adminCourse", adminCourse);
    }
    catch (NumberFormatException x)
    {
    }
}
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
		<struts-html:javascript formName="courseBasicForm" dynamicJavascript="true" staticJavascript="false"/>
		<script language="Javascript1.1" src="../staticJavascript.jsp"></script>

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

						<p class="headlineA">Learning Activity Details</p>
						<p>Use this screen to add or update details for an activity.</p>
<p>If an activity is not appearing on users' screens, check the Status, Released, and Expires
fields. If you make changes, click <strong>Update Activity.</strong></p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/activityBasic" focus="titleInput" onsubmit="return validateCourseBasicForm(this);">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">ACTIVITY NAME</div>
								<div class="right">
									<input name="titleInput" onfocus="select();" value="<%= adminCourse.getLabel() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">TYPE</div>
								<div class="right">
									<label for="radio1"><img src="../images/icnActivityELRN.gif" width="17" height="17" title="E-Learning" alt="E-Learning" /></label>
									<input name="typeSelect" id="radio1" type="radio" value="E-Learning Activity" class="crirHiddenJS" onclick="" <%= adminCourse.getType().equals(CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME) ? "checked=\"checked\" " : "" %>/>

									<label for="radio2"><img src="../images/icnActivityMNTR.gif" width="17" height="17" title="Mentor" alt="Mentor" /></label>
									<input name="typeSelect" id="radio2" type="radio" value="Mentor Activity" class="crirHiddenJS" onclick="" <%= adminCourse.getType().equals(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME) ? "checked=\"checked\" " : "" %>/>

									<label for="radio3"><img src="../images/icnActivityILED.gif" width="17" height="17" title="Instructor-Led" alt="Instructor-Led" /></label>
									<input name="typeSelect" id="radio3" type="radio" value="Instructor-Led Activity" class="crirHiddenJS" onclick="" <%= adminCourse.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME) ? "checked=\"checked\" " : "" %>/>

									<label for="radio4"><img src="../images/icnActivityASMT.gif" width="17" height="17" title="Assessment" alt="Assessment" /></label>
									<input name="typeSelect" id="radio4" type="radio" value="Assessment Activity" class="crirHiddenJS" onclick="" <%= adminCourse.getType().equals(CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME) ? "checked=\"checked\" " : "" %>/>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DESCRIPTION</div>
								<div class="right">
									<textarea name="descriptionInput" rows="5" cols="35" class="textarea" style="width: 304px;"><%= adminCourse.getComment() %></textarea>
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
									<option value="<%= course.getValue() %>"<%= (adminCourse.getParentId() == course.getId()) ? " selected" : "" %>><%= course.getLabel() %></option>
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
int owner_id = 0;
try
{
    owner_id = adminCourse.getOwnerPerson().getId();
}
catch (Exception x)
{
}
itr = listHelper.getPossibleCourseOwners().iterator();
while (itr.hasNext())
{
    PersonBean possible_course_owner = (PersonBean)itr.next();
    int possible_id = possible_course_owner.getId();
%>
									<option value="<%= possible_course_owner.getValue() %>"<%= (possible_id == owner_id) ? " selected" : "" %>><%= possible_course_owner.getLabel() %></option>
<%
}
%>
                                                                    </select>
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>

							<div class="adminItem">
								<div class="leftTM">DURATION</div>
								<div class="right">
									<input name="durationHoursInput" onfocus="select();" value="<%= adminCourse.getDurationHours() %>" size="1" maxlength="3" class="inputbox" style="width: 28px;" />
									<strong>:</strong>
									<input name="durationMinutesInput" onfocus="select();" value="<%= adminCourse.getDurationMinutes() %>" size="1" maxlength="3" class="inputbox" style="width: 28px;" />
								</div>
								<div class="end"></div>
							</div>
<%
if (adminCourse.getType().equals(CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME))
{
%>
							<div class="adminItem">
								<div class="leftTM">INTEROPERABILITY</div>
								<div class="right">
									<select name="interoperabilitySelect" class="select" style="width: 309px;">
										<option value="0">-- NONE --</option>
										<option value="1"<%= adminCourse.isAICCCompliant() ? " selected" : "" %>>AICC</option>
										<option value="2"<%= adminCourse.isSCORMCompliant() ? " selected" : "" %>>SCORM</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
<%
}
if (adminCourse.getType().equals(CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME))
{
    String passFailString = "";
    String intro_string = "";
    String intruct_string = "";
    boolean allow_retakes = true;
    try
    {
	AssessmentBean assessment = adminCourse.getAssessment();
	passFailString = assessment.getPassFailPercentileString();
	intro_string = assessment.getIntroductionString();
	intruct_string = assessment.getInstructionsString();
	allow_retakes = assessment.allowRetake();
    }
    catch (Exception x)
    {
    }
%>
							<div class="adminItem">
								<div class="leftTM">PASS&nbsp;&#47;&nbsp;FAIL&nbsp;&#37;</div>
								<div class="right">
									<input name="passFailPercent" onfocus="select();" value="<%= passFailString %>" size="1" maxlength="3" class="inputbox" style="width: 28px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<label for="allowRetakes" style="margin-left: -3px;">Allow Retakes</label>
									<input name="allowRetakes" id="allowRetakes" type="checkbox" class="crirHiddenJS" <%= allow_retakes ? "checked=\"checked\"" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">INTRODUCTION</div>
								<div class="right">
									<textarea name="introduction" rows="5" cols="35" class="textarea" style="width: 304px;"><%= intro_string %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">INSTRUCTIONS</div>
								<div class="right">
									<textarea name="instructions" rows="5" cols="35" class="textarea" style="width: 304px;"><%= intruct_string %></textarea>
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>
<%
}
%>
							<div class="adminItem">
								<div class="leftTM">STATUS</div>
								<div class="right">
									<label for="statusRadio1">In-Development</label>
									<input name="statusSelect" id="statusRadio1" type="radio" value="0" class="crirHiddenJS" onclick="" <%= adminCourse.getStatus().equals(CourseBean.IN_DEVELOPMENT_COURSE_STATUS) ? "checked=\"checked\" " : "" %>/>

									<label for="statusRadio2">Inactive</label>
									<input name="statusSelect" id="statusRadio2" type="radio" value="1" class="crirHiddenJS" onclick="" <%= adminCourse.getStatus().equals(CourseBean.INACTIVE_COURSE_STATUS) ? "checked=\"checked\" " : "" %>/>

									<label for="statusRadio3">Active</label>
									<input name="statusSelect" id="statusRadio3" type="radio" value="2" class="crirHiddenJS" onclick="" <%= adminCourse.getStatus().equals(CourseBean.DEFAULT_COURSE_STATUS) ? "checked=\"checked\" " : "" %>/>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">RELEASED</div>
								<div class="right">
									<input name="releasedDateInput" onfocus="getDate('courseBasicForm','releasedDateInput');select();" value="<%= (adminCourse.getReleasedDateString().equals("")) ? "" : adminCourse.getReleasedDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<!--
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<label for="makeActiveOn" style="margin-left: -3px;">Make Active On:&nbsp;</label>
									<input name="scheduleActive" id="makeActiveOn" type="checkbox" value="" class="crirHiddenJS" checked="checked" />
									<input name="activeDateInput" onfocus="select();" value="" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-top: -1px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<label for="displayAsNewFor" style="margin-left: -3px;">Display as new for&nbsp;</label>
									<input name="displayAsNew" id="displayAsNewFor" type="checkbox" value="" class="crirHiddenJS" checked="checked" onclick="displayAsNewToggle();" />
									<input name="displayAsNewInput" onfocus="select();" value="" size="2" maxlength="4" class="inputbox" style="width: 34px; margin-top: -1px;" />
									&nbsp;days
								</div>
								<div class="end"></div>
							</div>
							-->
							<div class="adminItem">
								<div class="leftTM">EXPIRES</div>
								<div class="right">
									<input name="expiresDateInput" onfocus="getDate('courseBasicForm','expiresDateInput');select();" value="<%= (adminCourse.getOffDateString().equals("")) ? "" : adminCourse.getOffDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>

							<div class="adminItem">
								<div class="leftTM">UPDATE NOTES</div>
								<div class="right">
									<textarea name="updateNotesInput" rows="5" cols="35" class="textarea" style="width: 304px;"><%= adminCourse.getEnrollmentEmailMessageString() %></textarea><input type="hidden" name="creditsInput" value="0">
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>

							<p>
								<!-- <input class="formbutton" type="submit" value="Delete Activity" alt="Delete Activity" style="margin-right: 10px; "/> -->
								<input class="formbutton" type="submit" value="Update Activity" alt="Update Activity" />
							</p>

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