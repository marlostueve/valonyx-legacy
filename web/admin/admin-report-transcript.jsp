<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*,java.util.*,org.apache.commons.beanutils.*,com.badiyan.torque.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminPerson" class="com.badiyan.uk.beans.PattersonPersonBean" scope="session"/>
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.beans.PattersonLoginBean" scope="session" />

<jsp:useBean id="transcriptReportLister" class="com.badiyan.uk.beans.TranscriptReportLister" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
Iterator itr = CourseBean.getCourses().iterator();
String errorMessage = "";
if (request.getParameter("id") != null)
{
    try
    {
        adminPerson = (PattersonPersonBean)PattersonPersonBean.getPerson(Integer.parseInt(request.getParameter("id")));
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

if (request.getParameter("sort") != null)
    transcriptReportLister.setSort(Short.parseShort(request.getParameter("sort")));
transcriptReportLister.search();
List aList = transcriptReportLister.getListStuff();

%>

<struts-html:html locale="true">
<head>
<struts-html:base/>
<title>Patterson Learning System</title>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--
// -->
</script>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages(); setTableBG('myCourses');">
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
	    <TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	    <td>
		<a href="admin-reports.jsp" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Back to Reports Menu</a>
	    </td>
	    <TD WIDTH=10 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</tr>
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- Begin Right Column Content -->
		<TD WIDTH=522 ALIGN=left VALIGN=top>
<%
for (int i = 0; i < aList.size(); i++)
{
    boolean display = false;
    Person obj = (Person)aList.get(i);
    if (request.getParameter("id") != null)
    {
        if (obj.getPersonid() == adminPerson.getId())
            display = true;
    }
    else
        display = true;
    if (display)
    {
        adminPerson = (PattersonPersonBean)PattersonPersonBean.getPerson(obj.getPersonid());
%>
			<!-- Begin My Courses Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
                           
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD ALIGN=left >
                                                                    <TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                                        <TR>
                                                                                <TD WIDTH=120 ALIGN=left><a href="admin-report-transcript.jsp?sort=<%= (transcriptReportLister.getSort() == TranscriptReportLister.USER_ASC_SORT) ? TranscriptReportLister.USER_DESC_SORT : TranscriptReportLister.USER_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">User</a></TD>
                                                                                <TD WIDTH=120 ALIGN=left><a href="admin-report-transcript.jsp?sort=<%= (transcriptReportLister.getSort() == TranscriptReportLister.GROUP_ASC_SORT) ? TranscriptReportLister.GROUP_DESC_SORT : TranscriptReportLister.GROUP_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Group</a></TD>
                                                                                <TD WIDTH=160 ALIGN=left><a href="admin-report-transcript.jsp?sort=<%= (transcriptReportLister.getSort() == TranscriptReportLister.LOCATION_ASC_SORT) ? TranscriptReportLister.LOCATION_DESC_SORT : TranscriptReportLister.LOCATION_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Location</a></TD>
                                                                                <TD WIDTH=120 ALIGN=left><a href="admin-report-transcript.jsp?sort=<%= (transcriptReportLister.getSort() == TranscriptReportLister.JOB_ASC_SORT) ? TranscriptReportLister.JOB_DESC_SORT : TranscriptReportLister.JOB_ASC_SORT  %>" style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #336699;">Job Title</a></TD>
                                                                        </TR>
<%
String groupName = "[NOT FOUND]";
try
{
    PersonGroupBean group = adminPerson.getGroup();
    groupName = group.getName();
}
catch (Exception x)
{
}
%>
                                                                        <TR>
                                                                            <TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= adminPerson.getFullName() %></TD>
                                                                            <TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= groupName %></TD>
                                                                            <TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= adminPerson.getLocationString() %></TD>
                                                                            <TD ALIGN=left style="font-size: 9pt; font-family: Arial; color: #336699;"><%= adminPerson.getJobTitleString() %></TD>
                                                                    </TR>
                                                                    </TABLE>
                                                                </TD>
							</TR>
							<TR BGCOLOR="#9EBFE9">
								<TD ALIGN=left>
									<TABLE WIDTH=520 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#9EBFE9">
										<TR>
											<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=70 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Completed</P></TD>
											<TD WIDTH=1 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=54 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Score &#37;</P></TD>
											<TD WIDTH=13 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=319 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Course Title</P></TD>
											<TD WIDTH=42 ALIGN=center><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Source</P></TD>
											
										</TR>
									</TABLE>
								</TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
								<TD ALIGN=left>
									<TABLE WIDTH=520 BORDER=0 CELLPADDING=2 CELLSPACING=0 ID="myCourses">
<%
int index = 0;
itr = adminPerson.getEnrollments().iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
        EnrollmentBean enrollment = (EnrollmentBean)itr.next();
        if (enrollment.isCourseEnrollment())
        {
            CourseBean course = enrollment.getCourse();
%>
										<TR HEIGHT=28>
<%
            if (course.isExternal())
            {
%>
											<TD WIDTH=74 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><INPUT type="hidden" name="enrollmentid<%= index %>" value="<%= enrollment.getIdString() %>"><INPUT NAME="date<%= index %>" TYPE="text" ID="date<%= index %>" value="<%= enrollment.getCompletionDateString() %>" STYLE="font-size: 13px; width: 79px;" onfocus="getDate('myCoursesForm','date<%= index %>');"></P></TD>
											<TD WIDTH=52 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><INPUT NAME="score<%= index %>" TYPE="text" ID="score<%= index++ %>" value="<%= enrollment.getScoreString() %>" STYLE="font-size: 13px; width: 30px;" onfocus="select();"></P></TD>
<%
            }
            else
            {
%>
											<TD WIDTH=74 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= enrollment.getCompletionDateString() %></P></TD>
											<TD WIDTH=52 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= enrollment.getScoreString() %></P></TD>
<%
            }
            if (course.getResourceType().equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
            {
%>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Acrobat.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Acrobat"></TD>
<%
            }
            else if (course.getResourceType().equals(ResourceBean.CD_RESOURCE_TYPE))
            {
%>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_CD.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="CD"></TD>
<%
            }
            else if (course.getResourceType().equals(ResourceBean.CLASSROOM_RESOURCE_TYPE))
            {
%>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Classroom.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Classroom"></TD>
<%
            }
            else if (course.getResourceType().equals(ResourceBean.FLASH_RESOURCE_TYPE))
            {
%>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Flash.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Flash"></TD>
<%
            }
            else if (course.getResourceType().equals(ResourceBean.INTERNET_RESOURCE_TYPE))
            {
%>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Internet.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Internet"></TD>
<%
            }
            else if (course.getResourceType().equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
            {
%>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_PPT.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="PowerPoint"></TD>
<%
            }
            else if (course.getResourceType().equals(ResourceBean.VIDEO_RESOURCE_TYPE))
            {
%>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Video.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Video"></TD>
<%
            }
            else if (course.getResourceType().equals(ResourceBean.WORD_RESOURCE_TYPE))
            {
%>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Word.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Word"></TD>
<%
            }
            else
            {
%>
											<TD WIDTH=18 ALIGN=left></TD>
<%
            }
%>
											<TD WIDTH=332 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-course.jsp?id=<%= course.getIdString() %>" style="color: #000000"><%= course.getName() %></A><%= (enrollment.hasParent()) ? "&nbsp;[" + enrollment.getParentNameString() + "]" : ""  %></P></TD>
											<TD WIDTH=44 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= (course.isExternal()) ? "Ext" : "Int" %></P></TD>
										</TR>
<%
        }
        else
        {
            CertificationBean cert = enrollment.getCertification();
%>
										<TR HEIGHT=28>
<%
            if (cert.isExternal())
            {
%>
											<TD WIDTH=74 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><INPUT type="hidden" name="enrollmentid<%= index %>" value="<%= enrollment.getIdString() %>"><INPUT NAME="date<%= index %>" TYPE="text" ID="date<%= index %>" value="<%= enrollment.getCompletionDateString() %>" STYLE="font-size: 13px; width: 79px;" onfocus="getDate('myCoursesForm','date<%= index %>');"></P></TD>
											<TD WIDTH=52 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><INPUT NAME="score<%= index %>" TYPE="text" ID="score<%= index++ %>" value="<%= enrollment.getScoreString() %>" STYLE="font-size: 13px; width: 30px;" onfocus="select();"></P></TD>
<%
            }
            else
            {
%>
											<TD WIDTH=74 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= enrollment.getCompletionDateString() %></P></TD>
											<TD WIDTH=52 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= enrollment.getScoreString() %></P></TD>
<%
            }
%>
											<TD WIDTH=18 ALIGN=left></TD>
											<TD WIDTH=317 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-certification.jsp?id=<%= cert.getIdString() %>" style="color: #000000"><%= cert.getNameString() %></A><%= (enrollment.hasParent()) ? "&nbsp;[" + enrollment.getParentNameString() + "]" : ""  %></P></TD>
											<TD WIDTH=44 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= (cert.isExternal()) ? "Ext" : "Int" %></P></TD>
										</TR>
<%
        }
    }
}
else
{
%>
										<TR>
                                                                                        <td colspan=6><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Enrollments Found</p></td>
										</TR>
<%
}
%>
										
                                                                            
									</TABLE>
								</TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
<%
    }
}
%>
		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
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
</struts-html:html>