<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*,java.util.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCert" class="com.badiyan.uk.beans.CertificationBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.beans.PattersonLoginBean" scope="session" />

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
<%
if (!adminCert.isNew())
{
    try
    {
        PersonBean owner = adminCert.getOwnerPerson();
        if (owner != null)
        {
%>
    document.certificationPeopleForm.ownerSelect.value = '<%= owner.getIdString() %>';
<%
        }
    }
    catch (Exception x)
    {
    }
%>
    document.certificationPeopleForm.contactNameInput.value = '<%= adminCert.getContactNameStringAlt() %>';
    document.certificationPeopleForm.contactEmailInput.value = '<%= adminCert.getContactEmailStringAlt() %>';
    document.certificationPeopleForm.contactDescInput.value = '<%= adminCert.getContactDescriptionString() %>';
<%
}
%>
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
    <struts-html:form action="/admin/certificationPeople">
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
<%@ include file="channels\channel-admin-menu.jsp" %>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- Begin Right Column Content -->
		<TD WIDTH=522 ALIGN=left VALIGN=top>
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">Course/Certification Manager&nbsp;&nbsp;&gt;&nbsp;&nbsp;Create New Certification</P>
<%@ include file="channels\channel-certification-menu.jsp" %>
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Owner Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Certification Owner</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Owner:</P></TD>
								<TD>
                                                                    <struts-html:select property="ownerSelect" style="font-size: 13px; width: 410px;" >
                                                                        <struts-html:option value="0">-- SELECT AN OWNER FOR THIS CERTIFICATION --</struts-html:option>
                                                                        <struts-html:optionsCollection name="listHelper" property="possibleCourseOwners" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Owner Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Assign Contact Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Assign Contact</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Name:</P></TD>
								<TD>
                                                                    <struts-html:text property="contactNameInput" style="font-size: 13px; width: 410px;" />
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Email:</P></TD>
								<TD>
                                                                    <struts-html:text property="contactEmailInput" style="font-size: 13px; width: 410px;" />
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Description:</P></TD>
								<TD>
                                                                    <struts-html:text property="contactDescInput" style="font-size: 13px; width: 410px;" />
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="assignContact" VALUE="Assign Contact"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Assign Contact Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Contacts Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Certification Contact(s)</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=3 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=130 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=372 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><A HREF="mailto:<%= adminCert.getContactEmailStringAlt() %>" style="color: #003366"><%= adminCert.getContactNameStringAlt() %></A></P></TD>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= adminCert.getContactDescriptionString() %></P></TD>
								<TD ALIGN=left><A HREF="#"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Contact"></A></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Contacts Area -->
		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
    </struts-html:form>
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