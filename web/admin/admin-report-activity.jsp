<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.io.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.conformance.aicc.rte.server.*, com.badiyan.uk.exceptions.*, com.badiyan.torque.*,org.apache.poi.poifs.filesystem.*,org.apache.poi.hssf.usermodel.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>
<jsp:useBean id="saveFilename" class="java.lang.String" scope="session" />

<%
int course_id = -1;
if ((request.getParameter("id") != null))
    course_id = Integer.parseInt(request.getParameter("id"));
if (request.getParameter("sort") != null)
    courseReportLister.setSort(Short.parseShort(request.getParameter("sort")));
courseReportLister.search();
List aList = courseReportLister.getListStuff();


try
{
    if (courseReportLister.showExcel())
    {
	int total_enrollments = 0;
	int row_index = 4;
	short column_index = 0;
	POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(System.getProperty("cu.realPath") + System.getProperty("cu.resourcesFolder") + "course-report.xls"));
	HSSFWorkbook wb = new HSSFWorkbook(fs);
	HSSFSheet sheet = wb.getSheetAt(0);
	
	courseReportLister.addCell(sheet, 0, (short)1, courseReportLister.getLocationString());

	String last_full_name = "";

	for (int i = 0; i < aList.size(); i++)
	{
	    EnrollmentRef obj = (EnrollmentRef)aList.get(i);

	    UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(obj.getPersonid());
	    DepartmentBean department = DepartmentBean.getDepartment(person.getDepartmentId());
	    /*
	    String location_number_str = "";
	    try
	    {
		DepartmentBean location = DepartmentBean.getDepartment(person.getLocationId());
		int dept_num = location.getDepartmentNumber();
		if (dept_num > 0)
		    location_number_str = dept_num + "";
	    }
	    catch (Exception xxy)
	    {
	    }
	    */
	    //String group_str = "";
	    //PersonGroupBean group = 
	    CourseBean courseObj = CourseBean.getCourseForProductId(obj.getProductid());
	    if ((course_id == -1) || (courseObj.getId() == course_id))
	    {
		String full_name = person.getFullName();

		if (!full_name.equals(last_full_name))
		{
		    courseReportLister.addCell(sheet, row_index, column_index, full_name); column_index++;
		    courseReportLister.addCell(sheet, row_index, column_index, person.getGroupNameString()); column_index++;
		    courseReportLister.addCell(sheet, row_index, column_index, person.getJobTitleString()); column_index++;
		    //courseReportLister.addCell(sheet, row_index, column_index, person.getLocationString()); column_index++;
		    courseReportLister.addCell(sheet, row_index, column_index, department.getNameIncludingParents()); column_index++;
		}
		else
		    column_index += 4;

		courseReportLister.addCell(sheet, row_index, column_index, courseObj.getName()); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, obj.getEnrollmentstatus()); column_index++;
		courseReportLister.addCell(sheet, row_index, column_index, (obj.getEnrollmentscore() == null) ? "" : (obj.getEnrollmentscore() + ""));
		
		total_enrollments++;

		row_index++;
		column_index = 0;

		last_full_name = full_name;
	    }
	}
	
	courseReportLister.addCell(sheet, row_index + 1, (short)0, "Enrollments in Report: " + total_enrollments);

	saveFilename = System.currentTimeMillis() + ".xls";
	FileOutputStream fileOut = new FileOutputStream(System.getProperty("cu.realPath") + System.getProperty("cu.resourcesFolder") + saveFilename);
	wb.write(fileOut);
	fileOut.close();
    }
}
catch (Exception x)
{
    x.printStackTrace();
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

	</head>

	<body>

		<div id="main">

			<!-- *** BEGIN Header *** -->
			<div id="header">
				<img src="../images/trspacer.gif" width="100%" height="1" alt="" />
				<div id="header_LogoAndTagline"><img src="../images/gfxLogoAndTagline.gif" width="424" height="37" alt="Ecolab Learning Management System" /></div>
				<div id="header_UserProperName"></div>
				<div id="header_MainNavigation">
					<a href="admin-report-activity-filter.jsp" title="BACK TO PREVIOUS PAGE">BACK TO PREVIOUS PAGE</a>
				</div>
				<div id="header_UserNavigation"></div>
			</div>
			<!-- *** END Header *** -->

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Activity Report</p>
<%
if (courseReportLister.showExcel())
{
%>
					<p>Download Link:&nbsp;&nbsp;<a href="<%= "../resources/" + saveFilename %>" target="_blank" title=""><%= saveFilename %></a></p>
<%
}
%>
				</div>
<%
if (courseReportLister.showHTML())
{
%>
				<div class="content_Table">

					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
					<div class="heading">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr style="display: block;">
								<td style="width:   4px; text-align: left;  "></td>
								<td style="width: 113px; text-align: left;  "><a href="admin-report-activity.jsp?sort=<%= (courseReportLister.getSort() == UKOnlineCourseReportLister.USER_ASC_SORT) ? UKOnlineCourseReportLister.USER_DESC_SORT : UKOnlineCourseReportLister.USER_ASC_SORT  %>" title="">User</a></td>
								<td style="width: 113px; text-align: left;  "><a href="admin-report-activity.jsp?sort=<%= (courseReportLister.getSort() == UKOnlineCourseReportLister.GROUP_ASC_SORT) ? UKOnlineCourseReportLister.GROUP_DESC_SORT : UKOnlineCourseReportLister.GROUP_ASC_SORT  %>" title="">User Group</a></td>
								<td style="width: 113px; text-align: left;  "><a href="admin-report-activity.jsp?sort=<%= (courseReportLister.getSort() == UKOnlineCourseReportLister.LOCATION_ASC_SORT) ? UKOnlineCourseReportLister.LOCATION_DESC_SORT : UKOnlineCourseReportLister.LOCATION_ASC_SORT  %>" title="">Department</a></td>
								<td style="width: 113px; text-align: left;  "><a href="admin-report-activity.jsp?sort=<%= (courseReportLister.getSort() == UKOnlineCourseReportLister.JOB_ASC_SORT) ? UKOnlineCourseReportLister.JOB_DESC_SORT : UKOnlineCourseReportLister.JOB_ASC_SORT  %>" title="">Job Title</a></td>
								<td style="width: 183px; text-align: left;  "><a href="admin-report-activity.jsp?sort=<%= (courseReportLister.getSort() == UKOnlineCourseReportLister.COURSE_NAME_ASC_SORT) ? UKOnlineCourseReportLister.COURSE_NAME_DESC_SORT : UKOnlineCourseReportLister.COURSE_NAME_ASC_SORT  %>" title="">Activity</a></td>
								<td style="width:  57px; text-align: left;  "><a href="admin-report-activity.jsp?sort=<%= (courseReportLister.getSort() == UKOnlineCourseReportLister.STATUS_ASC_SORT) ? UKOnlineCourseReportLister.STATUS_DESC_SORT : UKOnlineCourseReportLister.STATUS_ASC_SORT  %>" title="">Status</a></td>
								<td style="width:  32px; text-align: center;"><a href="admin-report-activity.jsp?sort=<%= (courseReportLister.getSort() == UKOnlineCourseReportLister.SCORE_ASC_SORT) ? UKOnlineCourseReportLister.SCORE_DESC_SORT : UKOnlineCourseReportLister.SCORE_ASC_SORT  %>" title="">Score</a></td>
							</tr>
						</table>
					</div>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
    if (aList.size() > 0)
    {
	for (int i = 0; i < aList.size(); i++)
	{
	    EnrollmentRef obj = (EnrollmentRef)aList.get(i);
	    UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(obj.getPersonid());
	    DepartmentBean department = DepartmentBean.getDepartment(person.getDepartmentId());
	    //PersonGroupBean group = person.getGroup();
	    CourseBean courseObj = CourseBean.getCourseForProductId(obj.getProductid());
	    if ((course_id == -1) || (courseObj.getId() == course_id))
	    {
%>
					<!-- BEGIN Report Entry -->
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:   4px; text-align: left;  "></td>
								<td style="width: 113px; text-align: left;  "><%= person.getFullName() %></td>
								<td style="width: 113px; text-align: left;  "><%= person.getGroupNameString() %></td>
								<td style="width: 113px; text-align: left;  "><%= department.getLabel() %></td>
								<td style="width: 113px; text-align: left;  "><%= person.getJobTitleString() %></td>
								<td style="width: 183px; text-align: left;  "><%= courseObj.getName() %></td>
								<td style="width:  57px; text-align: left;  "><%= obj.getEnrollmentstatus() %></td>
								<td style="width:  32px; text-align: center;"><%= (obj.getEnrollmentscore() == null) ? "" : (obj.getEnrollmentscore() + "") %></td>
							</tr>
						</table>
					</div>
					<!-- BEGIN Report Entry -->
<%
	    }
	}
    }
    else
    {
%>
					<!-- BEGIN Report Entry -->
					<div class="report">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width: 728px; text-align: left;  " colspan="8">No Results Found</td>
							</tr>
						</table>
					</div>
					<!-- BEGIN Report Entry -->
<%
    }
%>

					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>
<%
}
%>
			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Footer *** -->
			<div id="footer">
				<div id="footer_Copyright"><p>&copy;2008</p></div>
			</div>
			<!-- *** END Footer *** -->

		</div>

	</body>

</html>