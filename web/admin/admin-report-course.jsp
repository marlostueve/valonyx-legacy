<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*,java.util.*,com.badiyan.torque.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.beans.PattersonLoginBean" scope="session" />
<jsp:useBean id="courseReportLister" class="com.badiyan.uk.beans.CourseReportLister" scope="session" />

<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<%
int course_id = -1;
if ((request.getParameter("id") != null))
    course_id = Integer.parseInt(request.getParameter("id"));
if (request.getParameter("sort") != null)
    courseReportLister.setSort(Short.parseShort(request.getParameter("sort")));
courseReportLister.search();
List aList = courseReportLister.getListStuff();
%>

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<struts-html:html locale="true">
<HEAD>
<struts-html:base/>
<TITLE>Patterson Learning System</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--

function
initForm()
{
}

// -->
</script>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="javascript:preloadImages();initForm();">

<!-- Begin Bottom Content Area -->
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<!-- Begin 24 Pixel Spacer -->
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=24 ALT=""></TD>
		<TD WIDTH=732 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=732 HEIGHT=24 ALT=""></TD>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=24 ALT=""></TD>
	</TR>
	<!-- End 24 Pixel Spacer -->
</TABLE>
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<tr>
	    <TD WIDTH=10 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	    <td>
		<a href="admin-reports.jsp" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Back to Reports Menu</a>
	    </td>
	    <TD WIDTH=10 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</tr>
	<TR>
		<TD WIDTH=10 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>

		<!-- <TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD> -->
		<!-- Begin Right Column Content -->
		<TD WIDTH=752 ALIGN=left VALIGN=top>
			<TABLE WIDTH=752 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Course Report</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=750 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR>
								<TD ALIGN=left><a href="admin-report-course.jsp?sort=<%= (courseReportLister.getSort() == CourseReportLister.USER_ASC_SORT) ? CourseReportLister.USER_DESC_SORT : CourseReportLister.USER_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">User</a></TD>
								<TD ALIGN=left><a href="admin-report-course.jsp?sort=<%= (courseReportLister.getSort() == CourseReportLister.GROUP_ASC_SORT) ? CourseReportLister.GROUP_DESC_SORT : CourseReportLister.GROUP_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Group</a></TD>
								<TD ALIGN=left><a href="admin-report-course.jsp?sort=<%= (courseReportLister.getSort() == CourseReportLister.LOCATION_ASC_SORT) ? CourseReportLister.LOCATION_DESC_SORT : CourseReportLister.LOCATION_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Location</a></TD>
								<TD ALIGN=left><a href="admin-report-course.jsp?sort=<%= (courseReportLister.getSort() == CourseReportLister.JOB_ASC_SORT) ? CourseReportLister.JOB_DESC_SORT : CourseReportLister.JOB_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Job Title</a></TD>
                                                                <TD ALIGN=left><a href="admin-report-course.jsp?sort=<%= (courseReportLister.getSort() == CourseReportLister.COURSE_NAME_ASC_SORT) ? CourseReportLister.COURSE_NAME_DESC_SORT : CourseReportLister.COURSE_NAME_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Course</a></TD>
                                                                <TD ALIGN=left><a href="admin-report-course.jsp?sort=<%= (courseReportLister.getSort() == CourseReportLister.STATUS_ASC_SORT) ? CourseReportLister.STATUS_DESC_SORT : CourseReportLister.STATUS_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Status</a></TD>
                                                                <TD ALIGN=left><a href="admin-report-course.jsp?sort=<%= (courseReportLister.getSort() == CourseReportLister.SCORE_ASC_SORT) ? CourseReportLister.SCORE_DESC_SORT : CourseReportLister.SCORE_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Score</a></TD>
							</TR>
<%
for (int i = 0; i < aList.size(); i++)
{
    EnrollmentRef obj = (EnrollmentRef)aList.get(i);
    
    PattersonPersonBean person = (PattersonPersonBean)PattersonPersonBean.getPerson(obj.getPersonid());
    PersonGroupBean group = person.getGroup();
    CourseBean courseObj = CourseBean.getCourseForProductId(obj.getProductid());
    if ((course_id == -1) || (courseObj.getId() == course_id))
    {
%>
							<TR>
								<TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= person.getFullName() %></TD>
								<TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= group.getName() %></TD>
								<TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= person.getLocationString() %></TD>
								<TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= person.getJobTitleString() %></TD>
                                                                <TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= courseObj.getName() %></TD>
                                                                <TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= obj.getEnrollmentstatus() %></TD>
                                                                <TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= (obj.getEnrollmentscore() == null) ? "" : (obj.getEnrollmentscore() + "") %></TD>
							</TR>
<%
    }
}
%>
							
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End User Groups Area -->
		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=10 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
</TABLE>



<!-- End Bottom Content Area -->
<!-- Begin Bottom Copyright Info Area -->
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<TR>
		<TD WIDTH=772 ALIGN=center><IMG SRC="../images/trspacer.gif" WIDTH=772 HEIGHT=40 ALT=""></TD>
	</TR>
	<TR>
		<TD WIDTH=772 ALIGN=center><P style="font-size: 8pt; font-family: Arial; color: #000000;">&copy; 2003 Patterson Dental Supply, Inc. All rights reserved.</P></TD>
	</TR>
	<TR>
		<TD WIDTH=772 ALIGN=center><IMG SRC="../images/trspacer.gif" WIDTH=772 HEIGHT=6 ALT=""></TD>
	</TR>
</TABLE>
<!-- End Bottom Copyright Info Area -->
</BODY>
<struts-error:popupError stop="false" />
</struts-html:html>