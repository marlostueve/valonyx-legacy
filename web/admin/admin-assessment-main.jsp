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
if (request.getParameter("new") != null)
{
    if (request.getParameter("new").equals("true"))
        adminAssessment = new AssessmentBean();
}
else
{
    if (request.getParameter("id") != null)
    {
        try
        {
            int assessmentId = Integer.parseInt(request.getParameter("id"));
            adminAssessment = AssessmentBean.getAssessment(assessmentId);
        }
        catch (NumberFormatException x)
        {
        }
    }
}

session.setAttribute("adminAssessment", adminAssessment);

String stepString = "Step 1: Basic Information";
%>

<struts-html:html locale="true">
<head>
<struts-html:base/>
<title>Universal Knowledge</title>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<struts-html:javascript formName="assessmentMainForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src="../staticJavascript.jsp"></script>
<%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--



function
initForm()
{
<%
if (!adminAssessment.isNew())
{
    if (adminAssessment.allowRetake())
    {
%>
    document.forms[0].allowRetakes.checked = 'true';
<%
    }
}
%>
}

// -->
</script>
</head>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="javascript:preloadImages();initForm();">
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
    <struts-html:form action="/admin/assessmentMain" focus="nameInput" onsubmit="return validateAssessmentMainForm(this);">
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
			<!-- Begin Basic Information Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Basic Information</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=110 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=410 ALIGN=left colspan=3><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Last Update:</P></TD>
								<TD colspan="3"><P style="font-size: 10pt; font-family: Arial; color: #000000;"><%= adminAssessment.getModificationDateString() %></P></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Name:</P></TD>
								<TD colspan=3>
                                                                    <struts-logic:equal name="adminAssessment" property="isNew" value="true">
                                                                        <struts-html:text property="nameInput" style="font-size: 13px; width: 400px;" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminAssessment" property="isNew" value="false">
                                                                        <struts-html:text property="nameInput" style="font-size: 13px; width: 400px;" value="<%= adminAssessment.getNameString() %>" />
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Pass<IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT="">&#47;<IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT="">Fail &#37;</P></TD>
								<TD ALIGN=left>
                                                                    <struts-logic:equal name="adminAssessment" property="isNew" value="true">
                                                                        <struts-html:text property="passFailInput" style="font-size: 13px; width: 30px;" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminAssessment" property="isNew" value="false">
                                                                        <struts-html:text property="passFailInput" style="font-size: 13px; width: 30px;" value="<%= adminAssessment.getPassFailPercentileString() %>" />
                                                                    </struts-logic:equal>
								</TD>
								<TD ALIGN=right>
                                                                    <struts-html:checkbox property="allowRetakes" value="true" />
								</TD>
                                                                <TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Allow Retakes</P></TD>
							</TR>
							<TR HEIGHT=4>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=3 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Introduction:</P></TD>
								<TD colspan=3>
                                                                    <struts-logic:equal name="adminAssessment" property="isNew" value="true">
                                                                        <struts-html:textarea property="introductionInput" rows="8" style="font-size: 13px; width: 400px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminAssessment" property="isNew" value="false">
                                                                        <struts-html:textarea property="introductionInput" rows="8" style="font-size: 13px; width: 400px;" value="<%= adminAssessment.getIntroductionString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Instructions:</P></TD>
								<TD colspan=3>
                                                                    <struts-logic:equal name="adminAssessment" property="isNew" value="true">
                                                                        <struts-html:textarea property="instructionsInput" rows="8" style="font-size: 13px; width: 400px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminAssessment" property="isNew" value="false">
                                                                        <struts-html:textarea property="instructionsInput" rows="8" style="font-size: 13px; width: 400px;" value="<%= adminAssessment.getInstructionsString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=4>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=3 ALT=""></TD>
							</TR>
                                                        <!--
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Created:</P></TD>
								<TD ALIGN=left>
									<INPUT NAME="createdDateInput" TYPE="text" ID="createdDateInput" onFocus="select();" value="XX/XX/XXXX" STYLE="font-size: 13px; width: 80px;">
								</TD>
							</TR>
                                                        -->
							<TR>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
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