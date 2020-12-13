<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userEnrollment" class="com.badiyan.uk.beans.EnrollmentBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>

<%@ include file="channels/channel-permissions.jsp" %>


<%

String creation_date_string = "[NOT FOUND]";
String student_string = "[NOT FOUND]";
String course_string = "[NOT FOUND]";
String student_comment = "[NOT FOUND]";
try
{
    if (ApprovalQueueBean.isQueueOwner(loginBean.getPerson()))
    {
	ApprovalQueueBean queue = ApprovalQueueBean.getQueue(loginBean.getPerson());

	// get the queue item for userEnrollment

	ApprovalQueueItemBean queue_entry = queue.getQueueItem(userEnrollment);
	creation_date_string = CUBean.getUserDateString(queue_entry.getExpirationDate());
	student_string = queue_entry.getLastActionPerson().getLabel();
	student_comment = queue_entry.getComment();
	if (student_comment == null)
	    student_comment = "";
	course_string = userEnrollment.getCourse().getLabel();
    }
}
catch (Exception x)
{
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
}

// -->
</script>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="javascript:preloadImages();initErrors();initForm();">
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
	    <struts-html:form action="/courseEnrollRequestResponse">
		<!-- Begin Bottom Content Centering Table (Left Spacer) -->
		<TD WIDTH=20 ALIGN=left VALIGN=top><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- End Bottom Content Centering Table (Left Spacer) -->
		<!-- Begin Bottom Content Centering Table (Center Area) -->
		<TD WIDTH=582 ALIGN=left VALIGN=top>
			<!-- Begin Available Courses Table -->
			<TABLE WIDTH=582 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#CCA34A">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Course Enroll Request Response</P></TD>
				</TR>
				<TR>
					<TD WIDTH=580>
						<TABLE ID="courseDetail" WIDTH=580 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR>
								<TD WIDTH=580 ALIGN=left>
									<TABLE WIDTH=580 BORDER=0 CELLPADDING=3 CELLSPACING=0 BACKGROUND="images/PttrsnUK_TableBG01.jpg">
										<TR>
											<TD WIDTH=250 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Request Date:</P></TD>
											<TD WIDTH=330 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><%= creation_date_string %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=250 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Student:</P></TD>
											<TD WIDTH=330 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><%= student_string %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=250 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Course:</P></TD>
											<TD WIDTH=330 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><%= course_string %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=250 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Student Comment:</P></TD>
											<TD WIDTH=330 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><%= student_comment %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=250 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Response Comment:</P></TD>
											<TD WIDTH=330 VALIGN=top><textarea name="comment" rows="4" style="font-size: 9pt; font-family: Arial; color: #000000; width: 280px;"></textarea></TD>
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
                                                           
								<TD ALIGN=center VALIGN=top>
									<P style="font-size: 9pt; font-family: Arial; color: #000000;">
									<BR>
                                                                        <input type="image" name="approve_request" src="images/PttrsnUK_BTN-DSave.jpg" ALT="Approve Enrollment Request" onclick="javascript:if (confirm('Approve Request.  Are you sure?')) return true; else return false;">
									<BR><BR>
									</P>
								</TD>
							</TR>
							<TR BGCOLOR="#BBBBBB"><TD><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD></TR>
							<TR BGCOLOR="#FFFFFF">
                                                           
								<TD ALIGN=center VALIGN=top>
									<P style="font-size: 9pt; font-family: Arial; color: #000000;">
									<BR>
                                                                        <input type="image" name="deny_request" src="images/PttrsnUK_BTN-DSave.jpg" ALT="Deny Enrollment Request" onclick="javascript:if (confirm('Deny Request.  Are you sure?')) return true; else return false;">
									<BR><BR>
									</P>
								</TD>
							</TR>
							<TR BGCOLOR="#BBBBBB"><TD><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD></TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
		</TD>
		<!-- End Button Column -->

		<!-- End Bottom Content Centering Table (Center Area) -->
		<!-- Begin Bottom Content Centering Table (Right Spacer) -->
		<TD WIDTH=20 ALIGN=left VALIGN=top><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- End Bottom Content Centering Table (Right Spacer) -->
	    </struts-html:form>
	</TR>
	<!-- End Bottom Content Centering Table -->
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>