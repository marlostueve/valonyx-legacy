<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.io.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.conformance.aicc.rte.server.*, com.badiyan.uk.exceptions.*, com.badiyan.torque.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="prereqSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
Vector curriculum_groups = CourseGroupBean.getCourseGroups(CompanyBean.getInstance(), "Curriculum");

boolean has_courses_x = false;
Vector groups_to_display_x = new Vector();
HashMap grouped_courses_x = new HashMap(11);
Vector ungrouped_courses_x = new Vector();

prereqSearch.setCompany(adminCompany);
prereqSearch.setNumToDisplayPerPage(999);
if (!prereqSearch.searchPerformed())
    prereqSearch.search();
Enumeration courses = prereqSearch.getList();

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

						<p class="headlineA">Activity Report</p>
						<p>Use this screen to customize a report. Complete all the fields (multiple selection available)
and then click <strong>View Report.</strong> When the report appears, click any column title to sort by
that column. You will be able to download or print the report. To print it, use the <strong>Print</strong>
button on your browser.</p>




						<struts-html:form action="/admin/courseReportFilter">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">ACTIVITY</div>
								<div class="right">
									<select name="courseSelect" class="multipleSelect" size="8" multiple="multiple" style="width: 309px;">
										<option value="0" style="color: #007ac9; background-color: #fff;" selected>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ALL ACTIVITIES</option>
										<option value="-1" style="color: #000000; background-color: #fff;" disabled="disabled">&nbsp;</option>
<%
if (has_courses_x)
{
    Iterator ungrouped_itr = ungrouped_courses_x.iterator();
    while (ungrouped_itr.hasNext())
    {
	CourseBean course = (CourseBean)ungrouped_itr.next();
%>
										<option value="<%= course.getValue() %>" style="color: #007ac9; background-color: #fff;"<%= adminCourse.isPrerequisite(course) ? " selected" : "" %>>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= course.getLabel() %></option>
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
	    if (groups_to_display_x.contains(course_group))
	    {
		Iterator lesson_group_children = course_group.getChildren().iterator();
%>
										<option value="-1" style="color: #4fa800; background-color: #eee;" disabled="disabled"><%= course_group.getName().toUpperCase() %></option>
<%
		while (lesson_group_children.hasNext())
		{
		    CourseGroupBean lesson_group = (CourseGroupBean)lesson_group_children.next();
		    if (groups_to_display_x.contains(lesson_group))
		    {
			Vector lesson_courses = (Vector)grouped_courses_x.get(lesson_group);
%>
										<option value="-1" style="color: #000000; background-color: #fff;" disabled="disabled">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= lesson_group.getName() %></option>
<%
			if (lesson_courses != null)
			{
			    Iterator lesson_courses_itr = lesson_courses.iterator();
			    while (lesson_courses_itr.hasNext())
			    {
				CourseBean course = (CourseBean)lesson_courses_itr.next();
%>
										<option value="<%= course.getValue() %>" style="color: #007ac9; background-color: #fff;"<%= adminCourse.isPrerequisite(course) ? " selected" : "" %>>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= course.getLabel() %></option>
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
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">USER GROUP</div>
								<div class="right">
									<select name="userGroupSelect" class="select" style="width: 309px;">
										<option value="0">-- ALL USER GROUPS --</option>
<%
Iterator itr = listHelper.getPersonGroups().iterator();
while (itr.hasNext())
{
    PersonGroupBean obj = (PersonGroupBean)itr.next();
%>
										<option value="<%= obj.getValue() %>"><%= obj.getLabel() %></option>
<%
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DEPARTMENT</div>
								<div class="right">
									<select name="locationSelect" class="select" style="width: 309px;">
<%
   /*
DepartmentBean test_dept = DepartmentBean.getDepartment(adminCompany, "Institutional");
System.out.println("test_dept >" + test_dept.getLabel());
Vector children = test_dept.getChildren();
System.out.println("children count >" + children.size());
 */
if (loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) || loginBean.getPerson().hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) || loginBean.getPerson().hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME))
{
%>
										<option value="0">-- ALL DEPARTMENTS --</option>
<%
    itr = DepartmentBean.getDepartments(adminCompany).iterator();
    while (itr.hasNext())
    {
	DepartmentBean obj = (DepartmentBean)itr.next();
%>
										<option value="<%= obj.getValue() %>"><%= obj.getLabel() %></option>
<%
    }
}
else if (loginBean.getPerson().hasRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME))
{
    try
    {
	UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
	DepartmentBean department_obj = person_obj.getDepartment();
%>
										<option value="<%= department_obj.getValue() %>"><%= department_obj.getLabel() %></option>
<%
	itr = department_obj.getChildren().iterator();
	while (itr.hasNext())
	{
	    DepartmentBean obj = (DepartmentBean)itr.next();
%>
										<option value="<%= obj.getValue() %>"><%= obj.getLabel() %></option>
<%
	}
    }
    catch (Exception x)
    {
	x.printStackTrace();
    }
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">JOB TITLE</div>
								<div class="right">
									<select name="jobTitleSelect" class="select" style="width: 309px;">
										<option value="0">-- ALL JOB TITLES --</option>
<%
itr = listHelper.getPersonTitles().iterator();
while (itr.hasNext())
{
    PersonTitleBean obj = (PersonTitleBean)itr.next();
%>
										<option value="<%= obj.getValue() %>"><%= obj.getLabel() %></option>
<%
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">STATUS</div>
								<div class="right">
									<select name="status" class="select" style="width: 309px;">
										<option value="0">-- ANY STATUS --</option>
										<option value="1">Completed</option>
										<option value="2">Started</option>
										<option value="3">Completed OR Started</option>
										<option value="4">Not Started</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">SORT BY</div>
								<div class="right">
									<select name="sort" class="select" style="width: 309px;">
										<option value="1">USER_ASC_SORT</option>
										<option value="2">USER_DESC_SORT</option>
										<option value="3">GROUP_ASC_SORT</option>
										<option value="4">GROUP_DESC_SORT</option>
										<option value="5">LOCATION_ASC_SORT</option>
										<option value="6">LOCATION_DESC_SORT</option>
										<option value="7">JOB_ASC_SORT</option>
										<option value="8">JOB_DESC_SORT</option>
										<option value="9">SCORE_ASC_SORT</option>
										<option value="10">SCORE_DESC_SORT</option>
										<option value="11">STATUS_ASC_SORT</option>
										<option value="12">STATUS_DESC_SORT</option>
										<option value="13">COURSE_NAME_ASC_SORT</option>
										<option value="14">COURSE_NAME_DESC_SORT</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
<%
Calendar now = Calendar.getInstance();
Calendar a_year_ago = Calendar.getInstance();
a_year_ago.add(Calendar.YEAR, -1);
%>
							<div class="adminItem">
								<div class="leftTM">START&nbsp;&#47;&nbsp;END DATE</div>
								<div class="right">
									<input name="start_date" onfocus="select();" value="<%= CUBean.getUserDateString(a_year_ago.getTime()) %>" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
									<img src="../images/gfxDash.gif" width="21" height="21" title="-" alt="-" style="_margin-top: 1px;" />
									<input name="end_date" onfocus="select();" value="<%= CUBean.getUserDateString(now.getTime()) %>" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="left">REPORT FORMAT</div>
								<div class="right">
									<label for="formatHTML">HTML</label>
									<input name="displayHTML" id="formatHTML" type="checkbox" class="crirHiddenJS" checked="checked"/>

									<label for="formatExcel">Excel</label>
									<input name="displayExcel" id="formatExcel" type="checkbox" class="crirHiddenJS" />
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>

							<p>
								<input class="formbutton" type="submit" value="View Report" alt="View Report" />
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