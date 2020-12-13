<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminCert" class="com.badiyan.uk.beans.CertificationBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
if (request.getParameter("new") != null)
{
    if (request.getParameter("new").equals("true"))
        adminCert = new CertificationBean();
}
else
{
    if (request.getParameter("id") != null)
    {
        try
        {
            int certId = Integer.parseInt(request.getParameter("id"));
            adminCert = CertificationBean.getCertification(certId);
        }
        catch (NumberFormatException x)
        {
        }
    }
    //else
    //    adminCert = new CertificationBean();
}
session.setAttribute("adminCert", adminCert);
%>

<struts-html:html locale="true">
<HEAD>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<struts-html:javascript formName="certificationBasicForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src="../staticJavascript.jsp"></script>
<%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--

function
initForm()
{
    document.forms[0].AICCSelect[0].checked = true;
    document.forms[0].rewardsSelect[0].checked = true;
    document.forms[0].surveySelect[0].checked = true;
    document.forms[0].statusSelect[0].checked = true;
    document.forms[0].scheduleActive.checked = false;
<%
if (!adminCert.isNew())
{
    if (adminCert.isAICCCompliant())
    {
%>
    document.forms[0].AICCSelect[1].checked = true;
<%
    }
%>
    document.forms[0].createExternal.checked = <%= (adminCert.isExternal()) ? "true" : "false" %>
<%
    if (adminCert.getStatus().equals(CourseBean.IN_DEVELOPMENT_COURSE_STATUS))
    {
%>
    document.forms[0].statusSelect[0].checked = true;
<%
    }
    else if (adminCert.getStatus().equals(CourseBean.INACTIVE_COURSE_STATUS))
    {
%>
    document.forms[0].statusSelect[1].checked = true;
<%
    }
    else if (adminCert.getStatus().equals(CourseBean.DEFAULT_COURSE_STATUS))
    {
%>
    document.forms[0].statusSelect[2].checked = true;
<%
    }
    if (adminCert.getComment() != null)
    {
        if (!adminCert.getComment().equals(""))
        {
%>
    document.forms[0].rewardsSelect[1].checked = true;
<%
        }
    }
    if (adminCert.getSurveyURLString() != null)
    {
        if (!adminCert.getSurveyURLString().equals(""))
        {
%>
    document.forms[0].surveySelect[1].checked = true;
<%
        }
    }
    if (adminCert.getDisplayAsNewFor() > 0)
    {
%>
    document.forms[0].displayAsNew.checked = true;
    document.forms[0].displayAsNewInput.disabled = false;
<%
    }
    Calendar bDate = null;
    try
    {
        bDate = adminCert.getBroadcastDate();
    }
    catch (Exception xx)
    {
    }
    if (bDate != null)
    {
        if (bDate.getTime().compareTo(adminCert.getReleasedDate()) == 0)
        {
%>
    document.forms[0].scheduleActive.checked = false;
    document.forms[0].activeDateInput.disabled = true;
<%
        }
        else
        {
%>
    document.forms[0].scheduleActive.checked = true;
    document.forms[0].activeDateInput.disabled = false;
<%
        }
    }
    else
    {
%>
    document.forms[0].scheduleActive.checked = false;
    document.forms[0].activeDateInput.disabled = true;
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
    <struts-html:form action="/admin/certificationBasic" focus="titleInput" onsubmit="return validateCertificationBasicForm(this);">
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
<%@ include file="channels\channel-admin-menu.jsp" %>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- Begin Right Column Content -->
		<TD WIDTH=522 ALIGN=left VALIGN=top>
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">Course/Certification Manager&nbsp;&nbsp;&gt;&nbsp;&nbsp;<%= (adminCert.isNew()) ? "Create New" : "Edit" %> Certification</P>
<%@ include file="channels\channel-certification-menu.jsp" %>
                        <TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
                        <!-- Begin External to PLS Course Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Create External to PLS Certification</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR BGCOLOR="#FFFFFF" HEIGHT=26>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;To create an External to PLS Certification, select the checkbox below.</P></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=1>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=520 ALIGN=left>
									<P style="font-size: 10pt; font-family: Arial; color: #000000;">
									<IMG SRC="../images/trspacer.gif" WIDTH=3 HEIGHT=1 ALT=""><struts-html:checkbox property="createExternal" value="true" /><IMG SRC="../images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT="">This certification is an External to PLS Certification
									</P>
								</TD>
							</TR>
							<TR HEIGHT=1>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End External to PLS Course Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Basic Information Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Basic Information</P></TD>
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
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Last Update:</P></TD>
								<TD><P style="font-size: 10pt; font-family: Arial; color: #000000;"><%= adminCert.getModificationDateString() %></P></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Title:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                        <struts-html:text property="titleInput" style="font-size: 13px; width: 410px;" maxlength="30" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                        <struts-html:text property="titleInput" style="font-size: 13px; width: 410px;" value="<%= adminCert.getNameString() %>" maxlength="30" />
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Category:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                        <struts-html:select property="categorySelect" style="font-size: 13px; width: 410px;" >
                                                                            <struts-html:option value="0">-- SELECT A CATEGORY --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="categories" />
                                                                        </struts-html:select>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                        <struts-html:select property="categorySelect" style="font-size: 13px; width: 410px;" value="<%= adminCert.getCategoryIdString() %>" >
                                                                            <struts-html:option value="0">-- SELECT A CATEGORY --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="categories" />
                                                                        </struts-html:select>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Description:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                        <struts-html:textarea property="descriptionInput" rows="7" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                        <struts-html:textarea property="descriptionInput" rows="7" style="font-size: 13px; width: 410px;" value="<%= adminCert.getDescriptionString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Duration:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                        <struts-html:text property="durationHoursInput" style="font-size: 13px; width: 30px;" maxlength="1" /><IMG SRC="../images/PttrsnUK_Colon.jpg" WIDTH=10 HEIGHT=21 ALT=""><struts-html:text property="durationMinutesInput" style="font-size: 13px; width: 30px;" maxlength="2" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                        <struts-html:text property="durationHoursInput" style="font-size: 13px; width: 30px;" maxlength="1" value="<%= adminCert.getDurationHours() + "" %>" /><IMG SRC="../images/PttrsnUK_Colon.jpg" WIDTH=10 HEIGHT=21 ALT=""><struts-html:text property="durationMinutesInput" style="font-size: 13px; width: 30px;" maxlength="2" value="<%= adminCert.getDurationMinutes() + "" %>" />
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Credits:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                        <struts-html:text property="creditsInput" style="font-size: 13px; width: 30px;" maxlength="2" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                        <struts-html:text property="creditsInput" style="font-size: 13px; width: 30px;" maxlength="2" value="<%= adminCert.getCreditsString() %>" />
                                                                    </struts-logic:equal>
								</TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR>
								<TD WIDTH=96><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;AICC Compliant:</P></TD>
								<TD WIDTH=424>
                                                                        <P style="font-size: 10pt; font-family: Arial; color: #000000;"><struts-html:radio property="AICCSelect" value="0" />No<IMG SRC="../images/trspacer.gif" WIDTH=8 HEIGHT=1 ALT=""><struts-html:radio property="AICCSelect" value="1" />Yes</P>
								</TD>
							</TR>
							<TR>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Rewards:</P></TD>
								<TD>
                                                                        <P style="font-size: 10pt; font-family: Arial; color: #000000;"><struts-html:radio property="rewardsSelect" value="0" />No<IMG SRC="../images/trspacer.gif" WIDTH=8 HEIGHT=1 ALT=""><struts-html:radio property="rewardsSelect" value="1" />Yes (if Yes, enter Reward Description below)</P>
								</TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=30>
								<TD WIDTH=100><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                        <struts-html:textarea property="rewardDescInput" rows="3" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                        <struts-html:textarea property="rewardDescInput" rows="3" style="font-size: 13px; width: 410px;" value="<%= adminCert.getComment() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                </TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR>
								<TD WIDTH=96><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Survey:</P></TD>
								<TD WIDTH=424>
                                                                        <P style="font-size: 10pt; font-family: Arial; color: #000000;"><struts-html:radio property="surveySelect" value="0" />No<IMG SRC="../images/trspacer.gif" WIDTH=8 HEIGHT=1 ALT=""><struts-html:radio property="surveySelect" value="1" />Yes (if Yes, enter Survey URL below)</P>
								</TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=30>
								<TD WIDTH=100><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                        <struts-html:text property="surveyURLInput" style="font-size: 13px; width: 410px;" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                        <struts-html:text property="surveyURLInput" style="font-size: 13px; width: 410px;" value="<%= adminCert.getSurveyURLString() %>" />
                                                                    </struts-logic:equal>
                                                                </TD>
							</TR>
							<TR HEIGHT=4>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=4>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<!--
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Created:</P></TD>
								<TD>
                                                                        <struts-html:text property="createdDateInput" value="MM/DD/YYYY" style="font-size: 13px; width: 80px;" maxlength="10" />
								</TD>
							</TR>
-->
						</TABLE>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=96><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Status:</P></TD>
								<TD WIDTH=424>
									<P style="font-size: 10pt; font-family: Arial; color: #000000;"><struts-html:radio property="statusSelect" value="0" />In-Development<IMG SRC="../images/trspacer.gif" WIDTH=8 HEIGHT=1 ALT=""><struts-html:radio property="statusSelect" value="1" />Inactive<IMG SRC="../images/trspacer.gif" WIDTH=8 HEIGHT=1 ALT=""><struts-html:radio property="statusSelect" value="2" />Active</P>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Released:</P></TD>
								<TD>
									<IMG SRC="../images/trspacer.gif" WIDTH=188 HEIGHT=1 ALT="">
                                                                        <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                            <struts-html:text property="releasedDateInput" style="font-size: 13px; width: 100px;" maxlength="10" onfocus="getDate('certificationBasicForm','releasedDateInput');" />
                                                                        </struts-logic:equal>
                                                                        <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                            <struts-html:text property="releasedDateInput" value="<%= (adminCert.getReleasedDateString().equals("")) ? "" : adminCert.getReleasedDateString() %>" style="font-size: 13px; width: 100px;" maxlength="10" onfocus="getDate('certificationBasicForm','releasedDateInput');" />
                                                                        </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD>
									<P style="font-size: 10pt; font-family: Arial; color: #000000;">
									<struts-html:checkbox property="scheduleActive" onclick="makeActiveOnToggle('certificationBasicForm');" />Make Active on:&nbsp;
                                                                        <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                            <struts-html:text property="activeDateInput" style="font-size: 13px; width: 100px;" maxlength="10" disabled="<%= (adminCert.getReleasedDateString().equals("")) %>" onfocus="getDate('certificationBasicForm','activeDateInput');" />
                                                                        </struts-logic:equal>
                                                                        <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                            <struts-html:text property="activeDateInput" value="<%= (adminCert.getBroadcastDateString().equals("")) ? "" : adminCert.getBroadcastDateString() %>" style="font-size: 13px; width: 100px;" maxlength="10" disabled="<%= (adminCert.getBroadcastDateString().equals("")) %>" onfocus="getDate('certificationBasicForm','activeDateInput');" />
                                                                        </struts-logic:equal>
									</P>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD>
									<P style="font-size: 10pt; font-family: Arial; color: #000000;">
									<struts-html:checkbox property="displayAsNew" value="true" onclick="displayAsNewToggle();" />Display as New for&nbsp;&nbsp;<struts-html:text property="displayAsNewInput" value="<%= (adminCert.getDisplayAsNewFor() == 0) ? "0" : (adminCert.getDisplayAsNewFor() + "") %>" style="font-size: 13px; width: 30px;" maxlength="3" disabled="true" />&nbsp;days
									</P>
								</TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=30>
								<TD WIDTH=100><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Expires:</P></TD>
								<TD WIDTH=420>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="true">
                                                                        <struts-html:text property="expiresDateInput" style="font-size: 13px; width: 100px;" maxlength="10" onfocus="getDate('certificationBasicForm','expiresDateInput');" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminCert" property="isNew" value="false">
                                                                        <struts-html:text property="expiresDateInput" value="<%= (adminCert.getOffDateString().equals("")) ? "" : adminCert.getOffDateString() %>" style="font-size: 13px; width: 100px;" maxlength="10" onfocus="getDate('certificationBasicForm','expiresDateInput');" />
                                                                    </struts-logic:equal>
								</TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Basic Information Area -->
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