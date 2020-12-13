<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<struts-html:html locale="true">
<HEAD>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
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
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages();initForm();">
<%@ include file="channels\channel-menu.jsp" %>
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
    <struts-html:form action="/admin/courseSettings" >
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
<%@ include file="channels\channel-admin-menu.jsp" %>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- Begin Right Column Content -->
		<TD WIDTH=522 ALIGN=left VALIGN=top>
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">Course/Certification Manager&nbsp;&nbsp;&gt;&nbsp;&nbsp;Create New Course</P>
<%@ include file="channels\channel-course-menu.jsp" %>
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Settings Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Course Enroll/Drop Settings</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=22 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=498 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <input type="checkbox" name="allow_waitlisting" <%= adminCourse.allowWaitlisting() ? "checked " : "" %>style="font-size: 9pt; font-family: Arial;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Allow Waitlist</span>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <input type="checkbox" name="require_approval_for_enroll" <%= adminCourse.requireApprovalForEnroll() ? "checked " : "" %>style="font-size: 9pt; font-family: Arial;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Require Approval For Enroll</span>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <input type="checkbox" name="manager_approve_enroll" <%= adminCourse.allowManagerEnrollApproval() ? "checked " : "" %>style="font-size: 9pt; font-family: Arial;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Manager Can Approve Enroll Request</span>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <input type="checkbox" name="instructor_approve_enroll" <%= adminCourse.allowInstructorEnrollApproval() ? "checked " : "" %>style="font-size: 9pt; font-family: Arial;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Instructor Can Approve Enroll Request</span>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Enrolled Email Message:</span>
								</TD>
							</TR>
							<TR>
								<TD>
								</TD>
								<TD>
								    <textarea name="enrolled_email_message" rows="3" style="font-size: 9pt; font-family: Arial; width: 410px;"><%= adminCourse.getEnrollmentEmailMessageString() %></textarea>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    &nbsp;
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <input type="checkbox" name="allow_drop" <%= adminCourse.allowDrop() ? "checked " : "" %>style="font-size: 9pt; font-family: Arial;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Allow Drop</span>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <input type="checkbox" name="require_approval_for_drop" <%= adminCourse.requireApprovalForDrop() ? "checked " : "" %>style="font-size: 9pt; font-family: Arial;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Require Approval For Drop</span>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <input type="checkbox" name="manager_approve_drop" <%= adminCourse.allowManagerDropApproval() ? "checked " : "" %>style="font-size: 9pt; font-family: Arial;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Manager Can Approve Drop Request</span>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <input type="checkbox" name="instructor_approve_drop" <%= adminCourse.allowInstructorDropApproval() ? "checked " : "" %>style="font-size: 9pt; font-family: Arial;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Instructor Can Approve Drop Request</span>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <input type="checkbox" name="require_reason_for_drop" <%= adminCourse.requireReasonForDrop() ? "checked " : "" %>style="font-size: 9pt; font-family: Arial;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Require Reason For Drop</span>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <input type="checkbox" name="allow_enroll_after_drop" <%= adminCourse.allowEnrollAfterDrop() ? "checked " : "" %>style="font-size: 9pt; font-family: Arial;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Allow Enroll After Drop</span>
								</TD>
							</TR>
							<TR>
								<TD colspan="2">
								    <span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Drop Deadline:</span>&nbsp;<input type="text" name="drop_course_by_datetime" value="<%= adminCourse.getDropDeadlineString() %>" style="font-size: 9pt; font-family: Arial; width: 100px;" maxlength="10" onfocus="getDate('courseSettingsForm','drop_course_by_datetime');">
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Settings Area -->
		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
    </struts-html:form>
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>