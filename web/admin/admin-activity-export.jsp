<%@ page language="java" buffer="12kb" contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminResource" class="com.badiyan.uk.beans.ResourceBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="prereqSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%


//Calendar now = Calendar.getInstance();

if (request.getParameter("deletePrereqCourseId") != null)
{
    try
    {
        CourseBean prereq = CourseBean.getCourse(Integer.parseInt(request.getParameter("deletePrereqCourseId")));
        adminCourse.removePrerequisite(prereq);
    }
    catch (Exception x)
    {
    }
}
if (request.getParameter("deletePrereqCertId") != null)
{
    try
    {
        CertificationBean prereq = CertificationBean.getCertification(Integer.parseInt(request.getParameter("deletePrereqCertId")));
        adminCourse.removePrerequisite(prereq);
    }
    catch (Exception x)
    {
    }
}

Vector groups_to_display = new Vector();
HashMap group_released_dates = new HashMap(7);

String sortBy = CoursePeer.RELEASEDDATE;
boolean all = true;
boolean applyAudience = true;

boolean desc_sort = true;
boolean has_courses = false;



Vector curriculum_groups = CourseGroupBean.getCourseGroups(CompanyBean.getInstance(), "Curriculum");
Vector course_groups_to_display = new Vector();
Vector lesson_groups_to_display = new Vector();

HashMap grouped_courses = new HashMap(11);
Vector ungrouped_courses = new Vector();

/*
//Enumeration courses = adminCourse.getCoursePrerequisites().elements();
Iterator courses_itr = adminResource.getCourses();
while (courses_itr.hasNext())
{
    CourseBean course = (CourseBean)courses_itr.next();
    has_courses = true;

    try
    {
	CourseGroupBean course_group = course.getCourseGroup();
	System.out.println("group >" + course_group.getLabel());
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
*/


boolean has_courses_x = false;
Vector groups_to_display_x = new Vector();
HashMap grouped_courses_x = new HashMap(11);
Vector ungrouped_courses_x = new Vector();

/*
prereqSearch.setCompany(adminCompany);
prereqSearch.setNumToDisplayPerPage(999);
//if (!prereqSearch.searchPerformed())
prereqSearch.search();
Enumeration courses = prereqSearch.getList();
*/

if (!courseSearch.searchPerformed())
    courseSearch.search();
Enumeration courses = courseSearch.getList();

//courses = adminResource.getCourses().elements();
while (courses.hasMoreElements())
{
    CourseBean course = (CourseBean)courses.nextElement();

    has_courses_x = true;

    try
    {
	CourseGroupBean course_group = course.getCourseGroup();
	Vector vec = (Vector)grouped_courses_x.get(course_group);
	if (vec == null)
	{
	    vec = new Vector();
	    grouped_courses_x.put(course_group, vec);
	}
	vec.addElement(course);

	groups_to_display_x.addElement(course_group);
	CourseGroupBean parent_1 = null;
	if (course_group.hasParent())
	{
	    parent_1 = course_group.getParent();
	    groups_to_display_x.addElement(parent_1);
	}
    }
    catch (ObjectNotFoundException group_not_found)
    {
	ungrouped_courses_x.addElement(course);
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

						<p class="headlineA">Export Activities</p>
						<p class="currentObjectName"><%= adminResource.getLabel() %></p>
						<p>Use this screen to select activities to be exported to CD.</p>

						<struts-html:form action="/admin/activityExport">

							<input id="dummy" name="dummy" type="hidden" />

							<p>
								<select name="titleSelect" class="multipleSelect" size="12" multiple="multiple" style="width: 492px;">
<%
if (has_courses_x)
{
    Iterator ungrouped_itr = ungrouped_courses_x.iterator();
    while (ungrouped_itr.hasNext())
    {
	CourseBean course = (CourseBean)ungrouped_itr.next();
%>
									<option value="<%= course.getValue() %>" style="color: #007ac9; background-color: #fff;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= course.getLabel() %></option>
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
	    //Date released_date_course = (Date)group_released_dates.get(course_group);
	    if (groups_to_display_x.contains(course_group))
	    {
		Iterator lesson_group_children = course_group.getChildren().iterator();
%>
									<option value="0" style="color: #4fa800; background-color: #eee;" disabled="disabled"><%= course_group.getName().toUpperCase() %></option>
<%
		while (lesson_group_children.hasNext())
		{
		    CourseGroupBean lesson_group = (CourseGroupBean)lesson_group_children.next();
		    //Date released_date_lesson = (Date)group_released_dates.get(lesson_group);
		    if (groups_to_display_x.contains(lesson_group))
		    {
			Vector lesson_courses = (Vector)grouped_courses_x.get(lesson_group);
%>
									<option value="0" style="color: #000000; background-color: #fff;" disabled="disabled">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= lesson_group.getName() %></option>
<%
			if (lesson_courses != null)
			{
			    Iterator lesson_courses_itr = lesson_courses.iterator();
			    while (lesson_courses_itr.hasNext())
			    {
				CourseBean course = (CourseBean)lesson_courses_itr.next();
%>
									<option value="<%= course.getValue() %>" style="color: #007ac9; background-color: #fff;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= course.getLabel() %></option>
<%
			    }
			}
		    }
		}
	    }
	}
    }
}
%>
								</select>
							</p>

							<p style="margin-top: 20px; ">
								<input class="formbutton" type="submit" value="Export Selected Activities" alt="Export Selected Activities" />
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