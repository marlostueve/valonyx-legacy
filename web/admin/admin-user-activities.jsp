<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*,java.util.*,org.apache.commons.beanutils.*,com.badiyan.uk.online.beans.*,com.badiyan.torque.*,com.badiyan.uk.exceptions.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session"/>
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
String errorMessage = "";
if (request.getParameter("id") != null)
{
    try
    {
        adminPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(request.getParameter("id")));
        session.setAttribute("adminPerson", adminPerson);
    }
    catch (NumberFormatException x)
    {
        errorMessage = x.getMessage();
    }
}
else
{
    //adminPerson = new PersonBean();
}

if (request.getParameter("deleteId") != null)
{
    try
    {
        EnrollmentBean.deleteWithErrorMessage(request.getParameter("deleteId"));
        adminPerson.invalidateEnrollments();
    }
    catch (Exception x)
    {
        errorMessage = x.getMessage();
    }
}

//UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();

Calendar now = Calendar.getInstance();

if (request.getParameter("page") != null)
{
    if (request.getParameter("page").equals("next"))
        courseSearch.nextPage();
    else if (request.getParameter("page").equals("previous"))
        courseSearch.previousPage();
}
if (request.getParameter("deleteCourseId") != null)
{
    try
    {
        CourseBean course = CourseBean.getCourse(Integer.parseInt(request.getParameter("deleteCourseId")));
        course.setStatus(CourseBean.INACTIVE_COURSE_STATUS);
    }
    catch (Exception x)
    {
    }
}
listHelper.setCompany(adminCompany);
courseSearch.setCompany(adminCompany);

Vector groups_to_display = new Vector();
HashMap group_released_dates = new HashMap(7);

String sortBy = CoursePeer.RELEASEDDATE;
boolean all = true;
boolean applyAudience = true;

boolean desc_sort = true;
boolean has_courses = false;


HashMap enrolled_courses = new HashMap(11);
Iterator itr = adminPerson.getEnrollments().iterator();
while (itr.hasNext())
{
    EnrollmentBean enrollment = (EnrollmentBean)itr.next();
    CourseBean course = enrollment.getCourse();
    enrolled_courses.put(course, enrollment);
}

Vector enrmes = adminPerson.getEnrollments();

Vector curriculum_groups = CourseGroupBean.getCourseGroups(CompanyBean.getInstance(), "Curriculum");
Vector course_groups_to_display = new Vector();
Vector lesson_groups_to_display = new Vector();

HashMap grouped_courses = new HashMap(11);
Vector ungrouped_courses = new Vector();

//courseSearch.setStatus(CourseBean.DEFAULT_COURSE_STATUS);
if (!courseSearch.searchPerformed())
    courseSearch.search();
Enumeration courses = courseSearch.getList();
while (courses.hasMoreElements())
{
    CourseBean course = (CourseBean)courses.nextElement();
    
    if (course.getStatus().equals(CourseBean.DEFAULT_COURSE_STATUS))
    {
	has_courses = true;
	
	try
	{
	    CourseGroupBean course_group = course.getCourseGroup();
	    Vector vec = (Vector)grouped_courses.get(course_group);
	    if (vec == null)
	    {
		vec = new Vector();
		grouped_courses.put(course_group, vec);
	    }
	    vec.addElement(course);

	    groups_to_display.addElement(course_group);
	    CourseGroupBean parent_1 = null;
	    if (course_group.hasParent())
	    {
		parent_1 = course_group.getParent();
		groups_to_display.addElement(parent_1);
	    }


	    Date released_date = course.getReleasedDate();
	    if (released_date != null)
	    {
		Date existing_released_date = (Date)group_released_dates.get(course_group);
		if (existing_released_date == null)
		    group_released_dates.put(course_group, released_date);
		else
		{
		    if (released_date.before(existing_released_date))
			group_released_dates.put(course_group, released_date);
		}

		if (parent_1 != null)
		{
		    existing_released_date = (Date)group_released_dates.get(parent_1);
		    if (existing_released_date == null)
			group_released_dates.put(parent_1, released_date);
		    else
		    {
			if (released_date.before(existing_released_date))
			    group_released_dates.put(parent_1, released_date);
		    }
		}
	    }


	}
	catch (ObjectNotFoundException group_not_found)
	{
	    ungrouped_courses.addElement(course);
	}
    }
}

boolean is_sys_or_company_admin = loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) || loginBean.getPerson().hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);
boolean can_modify = loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) || loginBean.getPerson().hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) || loginBean.getPerson().hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME);

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

		<script language="JavaScript" type="text/JavaScript">
		    <!--

			if (document.images) {
			Rollimage = new Array();

			Rollimage[0]= new Image(17,17);
			Rollimage[0].src = "../images/btnContract.gif";
			Rollimage[1] = new Image(17,17);
			Rollimage[1].src = "../images/btnExpand.gif";
			}

			function SwapOut(x){
			  if (document.images)
			    eval("(document.m" + x + ".src.indexOf(\"btnContract.gif\") > -1) ? document.m" + x + ".src = Rollimage[1].src : document.m" + x + ".src = Rollimage[0].src");
			  return true;
			}

		    //-->
		</script>

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

						<p class="headlineA">User Profile&nbsp;&#47;&nbsp;Activities</p>

						<p class="currentObjectName"><%= adminPerson.getLabel() %></p>
						<p>Use this screen to mark mentor and instructor-led activities started or completed for a
particular user.</p>

						<struts-html:form action="/admin/myCourses">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="content_AdministrationTable">


								<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
								<div class="heading">
									<table cellspacing="0" cellpadding="0" border="0" summary="">
										<tr style="display: block;">
											<td style="width:  20px; text-align: left;  "></td>
											<td style="width: 242px; text-align: left;  ">Title</td>
											<td style="width:  50px; text-align: center;">Type</td>
											<td style="width:  90px; text-align: center;">Started</td>

											<td style="width:  90px; text-align: center;">Completed</td>
										</tr>
									</table>
								</div>

								<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
if (has_courses)
{
    Iterator ungrouped_itr = ungrouped_courses.iterator();
    while (ungrouped_itr.hasNext())
    {
	CourseBean course = (CourseBean)ungrouped_itr.next();
	String activity_image = "";
	if (course.getType().equals(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME))
	    activity_image = "<img src=\"../images/icnActivityMNTR.gif\" width=\"17\" height=\"17\" title=\"Mentor\" alt=\"Mentor\" />";
	else if (course.getType().equals(CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME))
	    activity_image = "<img src=\"../images/icnActivityELRN.gif\" width=\"17\" height=\"17\" title=\"E-Learning\" alt=\"E-Learning\" />";
	else if (course.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME))
	    activity_image = "<img src=\"../images/icnActivityILED.gif\" width=\"17\" height=\"17\" title=\"Instructor-Led\" alt=\"Instructor-Led\" />";
	else if (course.getType().equals(CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME))
	    activity_image = "<img src=\"../images/icnActivityASMT.gif\" width=\"17\" height=\"17\" title=\"Assessment\" alt=\"Assessment\" />";
%>
									<!-- BEGIN Activity -->
									<div class="activity">
										<table cellspacing="0" cellpadding="0" border="0" summary="">
											<tr>
												<td style="width:  20px; text-align: left;  "></td>
												<td style="width:  20px; text-align: left;  "></td>
												<td style="width:  20px; text-align: left;  "></td>
												<td style="width: 202px; text-align: left;  ">
<%
	if (is_sys_or_company_admin)
	{
%>
												    <label for="displayAssign_<%= course.getId() %>" style="margin-left: -3px;"><a href="admin-course.jsp?id=<%= course.getId() %>" title=""><%= course.getLabel() %></a></label>
<%
	}
	else
	{
%>
												    <label for="displayAssign_<%= course.getId() %>" style="margin-left: -3px;"><%= course.getLabel() %></label>
<%
	}
%>
												    <input name="assign_<%= course.getId() %>" id="displayAssign_<%= course.getId() %>" type="checkbox" class="crirHiddenJS"<%= enrolled_courses.containsKey(course) ? " checked" : "" %><%= can_modify ? "" : " disabled=\"true\"" %> />
												</td>
												<td style="width:  50px; text-align: center;"><%= activity_image %></td>
												<td style="width:  90px; text-align: center;"><input name="started_<%= course.getId() %>" onfocus="getDate('myCoursesForm','started_<%= course.getId() %>');select();" value="" size="7" maxlength="100" class="inputbox" style="width: 64px;"<%= can_modify ? "" : " disabled=\"true\"" %> /></td>
												<td style="width:  90px; text-align: center;"><input name="completed_<%= course.getId() %>" onfocus="getDate('myCoursesForm','completed_<%= course.getId() %>');select();" value="" size="7" maxlength="100" class="inputbox" style="width: 64px;"<%= can_modify ? "" : " disabled=\"true\"" %> /></td>
											</tr>
										</table>
									</div>
									<!-- END Activity -->
<%
    }
    Iterator curriculum_groups_itr = curriculum_groups.iterator();
    while (curriculum_groups_itr.hasNext())
    {
	CourseGroupBean curriculum_group = (CourseGroupBean)curriculum_groups_itr.next();

	Iterator course_group_children = curriculum_group.getChildren().iterator();
	while (course_group_children.hasNext())
	{
	    CourseGroupBean course_group = (CourseGroupBean)course_group_children.next();
	    Date released_date_course = (Date)group_released_dates.get(course_group);
	    if (groups_to_display.contains(course_group))
	    {
		Iterator lesson_group_children = course_group.getChildren().iterator();
%>
							<!-- BEGIN Course -->
							<div class="course">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
<%
		if (lesson_group_children.hasNext())
		{
%>
										<td style="width:  20px; text-align: left;  "><a href="#" onclick="document.getElementById('lessonContainer_<%= course_group.getId() %>').style.display == 'none' ? document.getElementById('lessonContainer_<%= course_group.getId() %>').style.display = 'block' : document.getElementById('lessonContainer_<%= course_group.getId() %>').style.display = 'none'; SwapOut(<%= course_group.getId() %>);" title="" ><img src="../images/btnContract.gif" width="17" height="17" title="Contract" alt="" name="m<%= course_group.getId() %>" /></a></td>
<%
		}
		else
		{
%>
										<td style="width:  20px; text-align: left;  "></td>
<%
		}
%>
										<td style="width: 242px; text-align: left;  "><strong><%= course_group.getName().toUpperCase() %></strong></td>
										<td style="width:  50px; text-align: center;"></td>
										<td style="width:  90px; text-align: center;"></td>
										<td style="width:  90px; text-align: center;"></td>
									</tr>
								</table>
								<div class="container" id="lessonContainer_<%= course_group.getId() %>" style="display: block;">
<%
		while (lesson_group_children.hasNext())
		{
		    CourseGroupBean lesson_group = (CourseGroupBean)lesson_group_children.next();
		    Date released_date_lesson = (Date)group_released_dates.get(lesson_group);
		    if (groups_to_display.contains(lesson_group))
		    {
			Vector lesson_courses = (Vector)grouped_courses.get(lesson_group);
%>
									<!-- BEGIN Lesson -->
									<div class="lesson">
										<table cellspacing="0" cellpadding="0" border="0" summary="">
											<tr>
												<td style="width:  20px; text-align: left;  "></td>
<%
			if (lesson_courses == null)
			{
%>
												<td style="width:  20px; text-align: left;  "></td>
<%
			}
			else
			{
%>
												<td style="width:  20px; text-align: left;  "><a href="#" onclick="document.getElementById('activityContainer_<%= lesson_group.getId() %>').style.display == 'none' ? document.getElementById('activityContainer_<%= lesson_group.getId() %>').style.display = 'block' : document.getElementById('activityContainer_<%= lesson_group.getId() %>').style.display = 'none'; SwapOut(<%= lesson_group.getId() %>);" title=""><img src="../images/btnContract.gif" width="17" height="17" title="Contract" alt="" name="m<%= lesson_group.getId() %>" /></a></td>
<%
			}
%>
												<td style="width: 222px; text-align: left;  "><strong><%= lesson_group.getName() %></strong></td>
												<td style="width:  50px; text-align: center;"><img src="../images/trspacer.gif" width="17" height="17" alt="" /></td>
												<td style="width:  90px; text-align: center;"></td>
												<td style="width:  90px; text-align: center;"></td>
											</tr>
										</table>
										<div class="container" id="activityContainer_<%= lesson_group.getId() %>" style="display: block;">
<%
			if (lesson_courses != null)
			{
			    Iterator lesson_courses_itr = lesson_courses.iterator();
			    while (lesson_courses_itr.hasNext())
			    {
				CourseBean course = (CourseBean)lesson_courses_itr.next();
				String activity_image = "";
				if (course.getType().equals(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME))
				    activity_image = "<img src=\"../images/icnActivityMNTR.gif\" width=\"17\" height=\"17\" title=\"Mentor\" alt=\"Mentor\" />";
				else if (course.getType().equals(CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME))
				    activity_image = "<img src=\"../images/icnActivityELRN.gif\" width=\"17\" height=\"17\" title=\"E-Learning\" alt=\"E-Learning\" />";
				else if (course.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME))
				    activity_image = "<img src=\"../images/icnActivityILED.gif\" width=\"17\" height=\"17\" title=\"Instructor-Led\" alt=\"Instructor-Led\" />";
				else if (course.getType().equals(CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME))
				    activity_image = "<img src=\"../images/icnActivityASMT.gif\" width=\"17\" height=\"17\" title=\"Assessment\" alt=\"Assessment\" />";
				String start_date = "";
				String end_date = "";
				if (enrolled_courses.containsKey(course))
				{
				    EnrollmentBean enrollment_obj = (EnrollmentBean)enrolled_courses.get(course);
				    if (enrollment_obj.isStarted())
				    {
					start_date = enrollment_obj.getEnrollmentDateString();
					if (enrollment_obj.isComplete())
					    end_date = enrollment_obj.getCompletionDateString();
				    }
				}
%>
											<!-- BEGIN Activity -->
											<div class="activity">
												<table cellspacing="0" cellpadding="0" border="0" summary="">
													<tr>
														<td style="width:  20px; text-align: left;  "></td>
														<td style="width:  20px; text-align: left;  "></td>
														<td style="width:  20px; text-align: left;  "></td>
														<td style="width: 202px; text-align: left;  ">
<%
				if (is_sys_or_company_admin)
				{
%>
														    <label for="displayAssign_<%= course.getId() %>" style="margin-left: -3px;"><a href="admin-course.jsp?id=<%= course.getId() %>" title=""><%= course.getLabel() %></a></label>
<%
				}
				else
				{
%>
														    <label for="displayAssign_<%= course.getId() %>" style="margin-left: -3px;"><%= course.getLabel() %></label>
<%
				}
%>
														    <input name="assign_<%= course.getId() %>" id="displayAssign_<%= course.getId() %>" type="checkbox" class="crirHiddenJS"<%= enrolled_courses.containsKey(course) ? " checked" : "" %><%= can_modify ? "" : " disabled=\"true\"" %> />
														</td>
														<td style="width:  50px; text-align: center;"><%= activity_image %></td>
														<td style="width:  90px; text-align: center;"><input name="started_<%= course.getId() %>" onfocus="getDate('myCoursesForm','started_<%= course.getId() %>');select();" value="<%= start_date %>" size="7" maxlength="100" class="inputbox" style="width: 64px;"<%= can_modify ? "" : " disabled=\"true\"" %> /></td>
												<td style="width:  90px; text-align: center;"><input name="completed_<%= course.getId() %>" onfocus="getDate('myCoursesForm','completed_<%= course.getId() %>');select();" value="<%= end_date %>" size="7" maxlength="100" class="inputbox" style="width: 64px;"<%= can_modify ? "" : " disabled=\"true\"" %> /></td>
													</tr>
												</table>
											</div>
											<!-- END Activity -->
<%
			    }
			}
%>
										</div>
									</div>
									<!-- END Lesson -->
<%
		    }
		}
%>
								</div>
							</div>
							<!-- END Course -->
<%
	    }
	}
    }
}
else
{
%>
							<!-- BEGIN Course -->
							<div class="certification">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colspan="4">No Activities Found</td>
									</tr>
								</table>
							</div>
							<!-- END Course -->
<%
}
%>

								<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>



							</div>


<%
if (can_modify)
{
%>
							<p style="margin-top: 20px; ">
								<input class="formbutton" type="submit" value="Update Activities" alt="Update Activities" />

							</p>
<%
}
else
{
%>
							<div class="adminItem">
								You don't have permission to modify activity enrollments.
							</div>
<%
}
%>
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