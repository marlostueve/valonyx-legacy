<%@ page language="java" buffer="12kb" contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
   
UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
Calendar now = Calendar.getInstance();
courseSearch.setNumToDisplayPerPage(9999);

listHelper.setCompany(adminCompany);
courseSearch.setCompany(adminCompany);

HashMap group_released_dates = new HashMap(7);

String sortBy = CoursePeer.RELEASEDDATE;
boolean all = true;
boolean applyAudience = true;

boolean desc_sort = true;
boolean has_courses = false;

Vector curriculum_groups = CourseGroupBean.getCourseGroups(adminCompany, "Level");

HashMap grouped_courses = new HashMap(11);
Vector ungrouped_courses = new Vector();

if (!courseSearch.searchPerformed())
    courseSearch.search();
Enumeration courses = courseSearch.getList();
while (courses.hasMoreElements())
{
    CourseBean course = (CourseBean)courses.nextElement();
    System.out.println("COURSE FOUND >" + course.getLabel());
    has_courses = true;
    
    try
    {
	if (course.hasCourseGroup())
	{
	    CourseGroupBean course_group = course.getCourseGroup();
	    System.out.println("course_group >" + course_group.getLabel());
	    Vector vec = (Vector)grouped_courses.get(course_group);
	    if (vec == null)
	    {
		vec = new Vector();
		grouped_courses.put(course_group, vec);
	    }
	    vec.addElement(course);
	}
	else
	{
	    // group not found for this course.  let's see if it has a parent

	    int parent_id = course.getParentId();
	    if (parent_id == -1)
	    {
		ungrouped_courses.addElement(course);
	    }
	}
    }
    catch (Exception x)
    {
	x.printStackTrace();
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

	<body>

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

						<p class="headlineA">Curriculum Manager</p>
						<p>Use this screen to select an activity to update or delete. You may find an activity by using
the Search check boxes and field below the menu. When you click an activity in the list,
you will go to the Activity Details screen for that activity.</p>



						<div class="content_AdministrationTable">


							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 332px; text-align: left;  ">Title</td>
										<td style="width:  50px; text-align: center;">Type</td>
										<td style="width:  90px; text-align: center;">Released</td>
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
	CourseBean activity = (CourseBean)ungrouped_itr.next();
	String activity_image = "";
	if (activity.getType().equals(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME))
	    activity_image = "<img src=\"../images/icnActivityMNTR.gif\" width=\"17\" height=\"17\" title=\"Mentor\" alt=\"Mentor\" />";
	else if (activity.getType().equals(CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME))
	    activity_image = "<img src=\"../images/icnActivityELRN.gif\" width=\"17\" height=\"17\" title=\"E-Learning\" alt=\"E-Learning\" />";
	else if (activity.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME))
	    activity_image = "<img src=\"../images/icnActivityILED.gif\" width=\"17\" height=\"17\" title=\"Instructor-Led\" alt=\"Instructor-Led\" />";
	else if (activity.getType().equals(CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME))
	    activity_image = "<img src=\"../images/icnActivityASMT.gif\" width=\"17\" height=\"17\" title=\"Assessment\" alt=\"Assessment\" />";
%>
											<!-- BEGIN Activity -->
											<div class="activity">
												<table cellspacing="0" cellpadding="0" border="0" summary="">
													<tr>
														<td style="width:  20px; text-align: left;  "></td>
														<td style="width:  20px; text-align: left;  "></td>
														<td style="width:  20px; text-align: left;  "></td>
														<td style="width: 292px; text-align: left;  "><a href="admin-activity.jsp?id=<%= activity.getId() %>" title=""><%= activity.getLabel() %></a></td>
														<td style="width:  50px; text-align: center;"><%= activity_image %></td>
														<td style="width:  90px; text-align: center;"><%= activity.getReleasedDateString() %></td>
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
	//Iterator lesson_group_children = curriculum_group.getChildren().iterator();
	//Vector course_group_
	//Iterator lesson_group_children = ((Vector)grouped_courses.get(curriculum_group)).iterator();
	System.out.println(curriculum_group.getLabel() + " course_group_vec >" + grouped_courses.get(curriculum_group));
	Vector courses_vec = (Vector)grouped_courses.get(curriculum_group);
%>
							<!-- BEGIN Level -->
							<div class="course">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
<%
	//if (lesson_group_children.hasNext())
	if (courses_vec != null)
	{
%>
										<td style="width:  20px; text-align: left;  "><a href="#" onclick="document.getElementById('lessonContainer_<%= curriculum_group.getId() %>').style.display == 'none' ? document.getElementById('lessonContainer_<%= curriculum_group.getId() %>').style.display = 'block' : document.getElementById('lessonContainer_<%= curriculum_group.getId() %>').style.display = 'none'; SwapOut(<%= curriculum_group.getId() %>);" title="" ><img src="../images/btnContract.gif" width="17" height="17" title="Contract" alt="" name="m<%= curriculum_group.getId() %>" /></a></td>
<%
	}
	else
	{
%>
										<td style="width:  20px; text-align: left;  "></td>
<%
	}
%>
										<td style="width: 332px; text-align: left;  "><strong><a href="admin-course-lesson.jsp?id=<%= curriculum_group.getId() %>"><%= curriculum_group.getName().toUpperCase() %></a></strong></td>
										<td style="width:  50px; text-align: center;"></td>
										<td style="width:  90px; text-align: center;"></td>
									</tr>
								</table>
								<div class="container" id="lessonContainer_<%= curriculum_group.getId() %>" style="display: block;">
<%
	if (courses_vec != null)
	{
	    Iterator courses_itr = courses_vec.iterator();
	    //while (lesson_group_children.hasNext())
	    while (courses_itr.hasNext())
	    {
		//CourseGroupBean lesson_group = (CourseGroupBean)lesson_group_children.next();
		CourseBean course = (CourseBean)courses_itr.next();
		System.out.println("EXpECTED COURSE >" + course.getLabel());
		Vector activities = course.getChildCourses();
		//Vector lesson_courses = (Vector)grouped_courses.get(lesson_group);
%>
									<!-- BEGIN Course -->
									<div class="lesson">
										<table cellspacing="0" cellpadding="0" border="0" summary="">
											<tr>
												<td style="width:  20px; text-align: left;  "></td>
<%
		//if (lesson_courses == null)
		if (activities == null)
		{
%>
												<td style="width:  20px; text-align: left;  "></td>
<%
		}
		else
		{
%>
												<td style="width:  20px; text-align: left;  "><a href="#" onclick="document.getElementById('activityContainer_<%= course.getId() %>').style.display == 'none' ? document.getElementById('activityContainer_<%= course.getId() %>').style.display = 'block' : document.getElementById('activityContainer_<%= course.getId() %>').style.display = 'none'; SwapOut(<%= course.getId() %>);" title=""><img src="../images/btnContract.gif" width="17" height="17" title="Contract" alt="" name="m<%= course.getId() %>" /></a></td>
<%
		}
%>
												<td style="width: 312px; text-align: left;  "><strong><a href="admin-course.jsp?id=<%= course.getId() %>"><%= course.getLabel() %></a></strong></td>
												<td style="width:  50px; text-align: center;"><img src="../images/trspacer.gif" width="17" height="17" alt="" /></td>
												<td style="width:  90px; text-align: center;"><strong><%= course.getReleasedDateString() %></strong></td>
											</tr>
										</table>
										<div class="container" id="activityContainer_<%= course.getId() %>" style="display: block;">
<%
		//if (lesson_courses != null)
		if (activities != null)
		{
		    //Iterator lesson_courses_itr = lesson_courses.iterator();
		    Iterator activities_itr = activities.iterator();
		    while (activities_itr.hasNext())
		    {
			CourseBean activity = (CourseBean)activities_itr.next();
			String activity_image = "";
			if (activity.getType().equals(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME))
			    activity_image = "<img src=\"../images/icnActivityMNTR.gif\" width=\"17\" height=\"17\" title=\"Mentor\" alt=\"Mentor\" />";
			else if (activity.getType().equals(CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME))
			    activity_image = "<img src=\"../images/icnActivityELRN.gif\" width=\"17\" height=\"17\" title=\"E-Learning\" alt=\"E-Learning\" />";
			else if (activity.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME))
			    activity_image = "<img src=\"../images/icnActivityILED.gif\" width=\"17\" height=\"17\" title=\"Instructor-Led\" alt=\"Instructor-Led\" />";
			else if (activity.getType().equals(CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME))
			    activity_image = "<img src=\"../images/icnActivityASMT.gif\" width=\"17\" height=\"17\" title=\"Assessment\" alt=\"Assessment\" />";
%>
											<!-- BEGIN Activity -->
											<div class="activity">
												<table cellspacing="0" cellpadding="0" border="0" summary="">
													<tr>
														<td style="width:  20px; text-align: left;  "></td>
														<td style="width:  20px; text-align: left;  "></td>
														<td style="width:  20px; text-align: left;  "></td>
														<td style="width: 292px; text-align: left;  "><a href="admin-activity.jsp?id=<%= activity.getId() %>" title=""><%= activity.getLabel() %></a></td>
														<td style="width:  50px; text-align: center;"><%= activity_image %></td>
														<td style="width:  90px; text-align: center;"><%= activity.getReleasedDateString() %></td>
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
									<!-- END Course -->
<%
	    }
	}
%>
								</div>
							</div>
							<!-- END Level -->
<%
    }
}
else
{
%>
							<div class="certification">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colspan="4"><%= courseSearch.searchPerformed() ? "No Curriculum Found" : "Please search for or create your curriculum." %></td>
									</tr>
								</table>
							</div>
<%
}
%>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						</div>

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