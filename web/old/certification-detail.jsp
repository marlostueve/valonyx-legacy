<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.beans.PattersonLoginBean" scope="session" />
<jsp:useBean id="userCert" class="com.badiyan.uk.beans.CertificationBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>

<%@ include file="channels/channel-permissions.jsp" %>

<%
if (request.getParameter("id") != null)
{
    try
    {
        int certificationId = Integer.parseInt(request.getParameter("id"));
        userCert = CertificationBean.getCertification(certificationId);
        session.setAttribute("userCert", userCert);
    }
    catch (NumberFormatException x)
    {
    }
}
%>

<struts-html:html locale="true">
<head>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<%@ include file="channels\channel-head-javascript.jsp" %>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages();">
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
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Certification Detail</P></TD>
				</TR>
				<TR>
					<TD WIDTH=580>
						<TABLE ID="courseDetail" WIDTH=580 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR>
								<TD WIDTH=580 ALIGN=left>
									<TABLE WIDTH=580 BORDER=0 CELLPADDING=3 CELLSPACING=0 BACKGROUND="images/PttrsnUK_TableBG01.jpg">
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Title:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><%= userCert.getNameString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Category:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCert.getCategoryNameString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Manufacturer:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCert.getManufacturerNameString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Released:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCert.getReleasedDateString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Program Type:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;">N/A</P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Audience:</P></TD>
<%
String audienceString = "[NO AUDIENCE(S) FOUND]";
Iterator itr = userCert.getAudiences().iterator();
while (itr.hasNext())
{
    AudienceBean audience = (AudienceBean)itr.next();
    if (audienceString.equals("[NO AUDIENCE(S) FOUND]"))
        audienceString = audience.getNameString();
    else
        audienceString += ", " + audience.getNameString();
}
%>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= audienceString %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Description:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCert.getDescriptionString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Duration:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCert.getDurationHours() + "" %> Hours <%= userCert.getDurationMinutes() + "" %> Minutes</P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Complete By:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCert.getOffDateString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Credits:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCert.getCreditsString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Courses:</P></TD>
<%
String courseString = "[NO COURSE(S) FOUND]";
itr = userCert.getCourses().iterator();
while (itr.hasNext())
{
    CourseBean course = (CourseBean)itr.next();
    if (courseString.equals("[NO COURSE(S) FOUND]"))
        courseString = "<a href=\"course-detail.jsp?id=" + course.getId() + "\">" + course.getNameString() + "</a>";
    else
        courseString += ", <a href=\"course-detail.jsp?id=" + course.getId() + "\">" + course.getNameString() + "</a>";
}
%>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= courseString %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Prerequisites:</P></TD>
<%
String prereqString = "[NO PREREQUISITE(S) FOUND]";
itr = userCert.getCertificationPrerequisites().iterator();
while (itr.hasNext())
{
    CertificationBean certReq = (CertificationBean)itr.next();
    if (prereqString.equals("[NO PREREQUISITE(S) FOUND]"))
        prereqString = "<a href=\"certification-detail.jsp?id=" + certReq.getId() + "\">" + certReq.getNameString() + "</a>";
    else
        prereqString = ", <a href=\"certification-detail.jsp?id=" + certReq.getId() + "\">" + certReq.getNameString() + "</a>";
}
itr = userCert.getCoursePrerequisites().iterator();
if (!prereqString.equals("[NO PREREQUISITE(S) FOUND]") && itr.hasNext())
    prereqString += "<br>";
while (itr.hasNext())
{
    CourseBean courseReq = (CourseBean)itr.next();
    if (prereqString.equals("[NO PREREQUISITE(S) FOUND]"))
        prereqString = "<a href=\"course-detail.jsp?id=" + courseReq.getId() + "\">" + courseReq.getNameString() + "</a>";
    else
        prereqString = ", <a href=\"course-detail.jsp?id=" + courseReq.getId() + "\">" + courseReq.getNameString() + "</a>";
}
%>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= prereqString %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Assessment:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCert.getAssessmentString() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Resources:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;">
<%
String resourceString = "[NO RESOURCES FOUND]";
Iterator itrX = userCert.getResources().iterator();
while (itrX.hasNext())
{
    ResourceBean resource = (ResourceBean)itrX.next();
    if (resourceString.equals("[NO RESOURCES FOUND]"))
        resourceString = resource.getNameString();
    else
        resourceString += ", " + resource.getNameString();
}
%>
                                                                                        <%= resourceString %>
                                                                                        </P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;FAQs:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;">
<%
String faqString = "[NO FAQs FOUND]";
itrX = userCert.getFAQs().iterator();
while (itrX.hasNext())
{
    FAQBean faq = (FAQBean)itrX.next();
    if (faqString.equals("[NO FAQs FOUND]"))
        faqString = "<a href=\"#\" onclick=\"javascript:window.open('faq-detail.jsp?id=" + faq.getNameString() + "','FAQWin','scrollbars=yes,resizable=yes,toolbar=no,location=no,directories=no,status=no,menubar=yes,width=520,height=500');\">" + faq.getNameString() + "</a>";
    else
        faqString += ", <a href=\"#\" onclick=\"javascript:window.open('faq-detail.jsp?id=" + faq.getNameString() + "','FAQWin','scrollbars=yes,resizable=yes,toolbar=no,location=no,directories=no,status=no,menubar=yes,width=520,height=500');\">" + faq.getNameString() + "</a>";
}
%>
                                                                                        <%= faqString %>
                                                                                        </P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Reward Details:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCert.getComment() %></P></TD>
										</TR>
										<TR>
											<TD WIDTH=110 VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Contact:</P></TD>
											<TD WIDTH=470 VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= userCert.getContactNameString() %></P></TD>
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
if (((String)session.getAttribute("corporate")).equals("false"))
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
                                                            <struts-html:form action="/certificationDetail">
								<TD ALIGN=center VALIGN=top>
									<P style="font-size: 9pt; font-family: Arial; color: #000000;">
									<BR>
									<!-- <A HREF="#"><IMG SRC="images/PttrsnUK_BTN-DTakeCourse.jpg" WIDTH=111 HEIGHT=30 BORDER=0 ALT="Take Course"></A>
									<A HREF="#"><IMG SRC="images/PttrsnUK_BTN-DTakeAssessment.jpg" WIDTH=111 HEIGHT=42 BORDER=0 ALT="Take Assessment"></A></A>
									<BR><BR><BR> -->
                                                                        <input type="image" name="enroll" src="images/PttrsnUK_BTN-DSave.jpg" ALT="Save to My Courses" onclick="javascript:if (confirm('Are you sure?')) return true; else return false;">
									<!-- <A HREF="#"><IMG SRC="images/PttrsnUK_BTN-DSave.jpg" WIDTH=111 HEIGHT=42 BORDER=0 ALT="Save to My Courses"></A><BR><BR> -->
									</P>
                                                                        <input type="hidden" name="certificationId" value="<%= userCert.getId() %>">
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
<!-- Begin Bottom Copyright Info Area -->
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<TR>
		<TD WIDTH=772 ALIGN=center><IMG SRC="images/trspacer.gif" WIDTH=772 HEIGHT=40 ALT=""></TD>
	</TR>
	<TR>
		<TD WIDTH=772 ALIGN=center><P style="font-size: 8pt; font-family: Arial; color: #000000;">&copy; 2003 Patterson Dental Supply, Inc. All rights reserved.</P></TD>
	</TR>
	<TR>
		<TD WIDTH=772 ALIGN=center><IMG SRC="images/trspacer.gif" WIDTH=772 HEIGHT=6 ALT=""></TD>
	</TR>
</TABLE>
<!-- End Bottom Copyright Info Area -->
</BODY>
<struts-error:popupError stop="false" />
</struts-html:html>