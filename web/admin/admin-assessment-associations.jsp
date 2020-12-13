<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminAssessment" class="com.badiyan.uk.beans.AssessmentBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
String stepString = "Step 3: Associations";
%>

<struts-html:html locale="true">
<head>
<struts-html:base/>
<title>Universal Knowledge</title>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<%@ include file="channels\channel-head-javascript.jsp" %>
</head>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages();">
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
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
<%@ include file="channels\channel-admin-menu.jsp" %>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- Begin Right Column Content -->
		<TD WIDTH=542 ALIGN=left VALIGN=top>
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">Assessment Generator&nbsp;&nbsp;&gt;&nbsp;&nbsp;<%= (adminAssessment.isNew()) ? "Create New" : "Edit" %> Assessment</P>
<%@ include file="channels\channel-assessment-menu.jsp" %>
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Associated Course(s) Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Associated Course(s)</P></TD>
							</TR>
							<TR BGCOLOR="#FFFFFF" HEIGHT=68>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;To view the details for an associated course, click on the course title.<br>&nbsp;To remove an association below, you must do so in the <I>Course<IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT="">&#47;<IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT="">Certification Manager</I>.<BR>&nbsp;If a course must be completed before taking this assessment, a <B>C</B> will be displayed in the<BR>&nbsp;far right column. To change this, you must do so in the <I>Course<IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT="">&#47;<IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT="">Certification Manager</I>.</P></TD>
							</TR>
							<TR BGCOLOR="#9EBFE9" HEIGHT=8>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
								<TD ALIGN=left>
									<TABLE WIDTH=520 BORDER=0 CELLPADDING=0 CELLSPACING=0>
										<TR>
											<TD WIDTH=2 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=50 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=280 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=130 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
<%
// get the associated course if any
try
{
    CourseBean course = adminAssessment.getCourse();
%>
										<!-- Begin One Result Entry -->
										<TR>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_StatusGreen.jpg" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Active"></TD>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= (course.getId() + 1000) + "" %></P></TD>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Internet.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Internet"></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-course.jsp?id=<%= course.getIdString() %>" style="color: #000000"><%= course.getNameString() %></A></P></TD>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">OLD_MAN</P></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><%= course.mustCompleteCourseBeforeAssessment() ? "C" : "" %></P></TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left COLSPAN=5>
												<TABLE WIDTH=448 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#EEEEEE">
													<TR BGCOLOR="#DDDDDD">
														<TD WIDTH=18 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=65 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=95 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=63 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=188 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Created:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course.getCreationDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Category:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course.getCategoryNameString() %></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Released:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course.getReleasedDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">User Group:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Expires:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course.getOffDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Sub Group:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course %></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Last Update:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course.getModificationDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Region:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Location:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Job Title:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR BGCOLOR="#DDDDDD">
														<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
													</TR>
												</TABLE>
											</TD>
										</TR>
										<!-- End One Result Entry -->
<%
}
catch (Exception x)
{
%>
                                                                                <TR>
                                                                                        <TD ALIGN=left COLSPAN=9><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Associated Course Found</p></TD>
                                                                                </TR>
<%
}
%>
									</TABLE>
								</TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Associated Course(s) Area -->
		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>