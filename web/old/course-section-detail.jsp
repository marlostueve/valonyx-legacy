<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="userCourseSection" class="com.badiyan.uk.beans.CourseSectionBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>

<%@ include file="channels/channel-permissions.jsp" %>


<%

//String errorMessage = "";
if (request.getParameter("id") != null)
{
    try
    {
        int courseSectionId = Integer.parseInt(request.getParameter("id"));
        userCourseSection = CourseSectionBean.getSection(courseSectionId);
        session.setAttribute("userCourseSection", userCourseSection);
        userCourse = userCourseSection.getCourse();
        session.setAttribute("userCourse", userCourse);
    }
    catch (NumberFormatException x)
    {
        //errorMessage = x.getMessage();
    }
}
%>

<struts-html:html locale="true">
<head>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--

function
initForm()
{
    // check for passed errors
    
    //if (error.length != 0)
    //    alert(error);
}

// -->
</script>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="javascript:preloadImages();initForm();initErrors();">
<%@ include file="channels\channel-menu.jsp" %>
<!-- Begin Bottom Content Area -->
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<!-- Begin 24 Pixel Spacer -->
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=24 ALT=""></TD>
		<TD WIDTH=582 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=582 HEIGHT=24 ALT=""></TD>
		<TD WIDTH=150 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=150 HEIGHT=24 ALT=""></TD>
		<TD WIDTH=20 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=24 ALT=""></TD>
	</TR>
	<!-- End 24 Pixel Spacer -->
	<!-- Begin Bottom Content Centering Table -->
	<TR>
		<!-- Begin Bottom Content Centering Table (Left Spacer) -->
		<TD WIDTH=20 ALIGN=left VALIGN=top><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- End Bottom Content Centering Table (Left Spacer) -->
		<!-- Begin Bottom Content Centering Table (Center Area) -->
		<TD WIDTH=582 ALIGN=left VALIGN=top>
			<!-- Begin Available Courses Table -->
			<TABLE WIDTH=582 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#CCA34A">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Course Section Detail</P></TD>
				</TR>
				<TR>
					<TD WIDTH=580>
						<TABLE ID="courseDetail" WIDTH=580 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR>
								<TD WIDTH=580 ALIGN=left>
									<TABLE WIDTH=580 BORDER=0 CELLPADDING=3 CELLSPACING=0 BACKGROUND="images/PttrsnUK_TableBG01.jpg">
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Course Title:</P></TD>
											<TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><a href="course-detail.jsp?id=<%= userCourse.getId() %>"><%= userCourse.getLabel() %></a></P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Section Name/Number:</P></TD>
											<TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCourseSection.getLabel() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Status:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCourseSection.getStatus().getLabel() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Primary Room:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCourseSection.getClassroom().getLabel() %></P></TD>
										</TR>
<%
BuildingBean building_obj = userCourseSection.getClassroom().getBuilding();
LocationBean location_obj = userCourseSection.getClassroom().getBuilding().getLocation();
%>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Building:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= building_obj.getLabel() %>
<%
if (building_obj.hasMap())
{
%>
                                                                                        <a href="<%= building_obj.getMapURLString() %>" target="_blank"><img src="images/map_icon2.gif" border="0"></a>
<%
}
%>
                                                                                    </P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Location:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= location_obj.getLabel() %>

<%
if (location_obj.hasMap())
{
%>
                                                                                        <a href="<%= location_obj.getMapURLString() %>" target="_blank"><img src="images/map_icon2.gif" border="0"></a>
<%
}
int enrollment_count = EnrollmentBean.getEnrollmentCount(userCourseSection);
%>                                                                                    </P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Capacity:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCourseSection.getCapacity() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Enrollments:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= enrollment_count %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Remaining:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCourseSection.getCapacity() - enrollment_count %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Instructors:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCourseSection.getInstructorsString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Instructor Comment:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCourseSection.getInstructorCommentString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Schedule:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCourseSection.getScheduleString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;General Comment:</P></TD>
                                                                                    <TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCourseSection.getCommentString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=180 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Released:</P></TD>
											<TD WIDTH=400 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;">sdfsdfsdf</P></TD>
										</TR>
									</TABLE>
								</TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Available Courses Table -->
		</TD>
<%

// determine if this user is in the audience for this course
if (loginBean.getPerson().canEnrollIn(userCourse))
{
%>
		<!-- Begin Button Column -->
		<TD WIDTH=150 ALIGN=left VALIGN=top BGCOLOR="#FFFFFF">
			<TABLE WIDTH=150 BORDER=0 CELLPADDING=0 CELLSPACING=0>
				<TR>
					<TD WIDTH=10></TD>
					<TD WIDTH=140 BGCOLOR="#6F9BCB" ALIGN=center>
						<TABLE WIDTH=138 BORDER=0 CELLPADDING=0 CELLSPACING=0>
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG3.jpg"></TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
                                                            <struts-html:form action="/courseSectionDetail">
								<TD ALIGN=center VALIGN=top>
									<P style="font-size: 9pt; font-family: Arial; color: #000000;">
									<BR>
<%
    if (userCourseSection.hasRemainingCapacity())
    {
%>
                                                                        <input type="image" name="sectionEnroll" src="images/PttrsnUK_BTN-DSave.jpg" ALT="Save to My Courses" onclick="javascript:if (confirm('Are you sure?')) return true; else return false;">
<%
    }
    else if (userCourseSection.allowWaitlisting())
    {
%>
                                                                        <input type="image" name="sectionEnroll" src="images/PttrsnUK_BTN-DSave.jpg" ALT="Add Yourself to Waitlist" onclick="javascript:if (confirm('Are you sure?')) return true; else return false;">
<%
    }
    else
    {
%>
									<P style="font-size: 9pt; font-family: Arial; color: #000000;">
									You cannot enroll in this course section.  This section is full and waitlisting is not enabled.
									</P>
<%
    }
%>
									<BR><BR>
									</P>
                                                                        <input type="hidden" name="courseId" value="<%= userCourse.getId() %>">
									<input type="hidden" name="courseSectionId" value="<%= userCourseSection.getId() %>">
								</TD>
                                                            </struts-html:form>
							</TR>
							<TR BGCOLOR="#BBBBBB"><TD><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD></TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
		</TD>
		<!-- End Button Column -->
<%
}
else
{
%>
		<!-- Begin Button Column -->
		<TD WIDTH=150 ALIGN=left VALIGN=top BGCOLOR="#FFFFFF">
			<TABLE WIDTH=150 BORDER=0 CELLPADDING=0 CELLSPACING=0>
				<TR>
					<TD WIDTH=10></TD>
					<TD WIDTH=140 BGCOLOR="#6F9BCB" ALIGN=center>
						<TABLE WIDTH=138 BORDER=0 CELLPADDING=0 CELLSPACING=0>
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG3.jpg"></TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
                                                            <struts-html:form action="/courseSectionDetail">
								<TD ALIGN=center VALIGN=top>
									<P style="font-size: 9pt; font-family: Arial; color: #000000;">
									You cannot enroll in this course.  You are not included in the audience for this course.
									</P>
								</TD>
                                                            </struts-html:form>
							</TR>
							<TR BGCOLOR="#BBBBBB"><TD><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD></TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
		</TD>
		<!-- End Button Column -->
<%
}
%>
		<!-- End Bottom Content Centering Table (Center Area) -->
		<!-- Begin Bottom Content Centering Table (Right Spacer) -->
		<TD WIDTH=20 ALIGN=left VALIGN=top><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- End Bottom Content Centering Table (Right Spacer) -->
	</TR>
	<!-- End Bottom Content Centering Table -->
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>