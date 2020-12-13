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
        if (adminCert.mustCompleteCourseBeforeAssessment())
        {
%>
    document.certificationAssessmentForm.mustBeCompleted.checked = true;
<%
        }
        else
        {
%>
    document.certificationAssessmentForm.mustBeCompleted.checked = false;
<%
        }
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
    <struts-html:form action="/admin/certificationAssessment" focus="assessmentSelect">
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
<%@ include file="channels\channel-admin-menu.jsp" %>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- Begin Right Column Content -->
		<TD WIDTH=522 ALIGN=left VALIGN=top>
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">Course/Certification Manager&nbsp;&nbsp;&gt;&nbsp;&nbsp;Create New Course</P>
<%@ include file="channels\channel-certification-menu.jsp" %>
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Assessment Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Course Assessment</P></TD>
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
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Assessment:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                        <struts-html:select property="assessmentSelect" style="font-size: 13px; width: 410px;" >
                                                                            <struts-html:option value="0">-- SELECT AN ASSESSMENT --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="assessments" />
                                                                        </struts-html:select>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                        <struts-html:select property="assessmentSelect" style="font-size: 13px; width: 410px;" value="<%= adminCert.getAssessmentIdString() %>" >
                                                                            <struts-html:option value="0">-- SELECT AN ASSESSMENT --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="assessments" />
                                                                        </struts-html:select>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR>
								<TD ALIGN=left COLSPAN=2>
									<P style="font-size: 10pt; font-family: Arial; color: #000000;">
									<IMG SRC="../images/trspacer.gif" WIDTH=95 HEIGHT=1 ALT=""><struts-html:checkbox property="mustBeCompleted" value="true" /><IMG SRC="../images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT="">Course must be completed before taking assessment
									</P>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Assessment Area -->
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