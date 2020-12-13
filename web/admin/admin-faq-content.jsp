<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminFAQ" class="com.badiyan.uk.beans.FAQBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
if (request.getParameter("deleteId") != null)
{
    try
    {
        FAQElementBean.delete(request.getParameter("deleteId"));
    }
    catch (Exception x)
    {
    }
}

String menuString = "3";

%>

<struts-html:html locale="true">
<HEAD>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<struts-html:javascript formName="faqContentForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src="../staticJavascript.jsp"></script>
<%@ include file="channels\channel-head-javascript.jsp" %>
</HEAD>
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
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">FAQ Manager&nbsp;&nbsp;&gt;&nbsp;&nbsp;Create New FAQ</P>
<%@ include file="channels\channel-faq-menu.jsp" %>
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Create Question/Answer Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Create Question<IMG SRC="../images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT="">/<IMG SRC="../images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT="">Answer</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/faqContent" focus="questionInput" onsubmit="return validateFaqContentForm(this);">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Question:</P></TD>
								<TD VALIGN=top>
                                                                    <struts-html:textarea property="questionInput" rows="3" style="font-size: 13px; width: 410px;"></struts-html:textarea>
								</TD>
							</TR>
							<TR HEIGHT=6>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer:</P></TD>
								<TD VALIGN=top>
                                                                    <struts-html:textarea property="answerInput" rows="7" style="font-size: 13px; width: 410px;"></struts-html:textarea>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="createQuestionAnswer" VALUE="Create Question&nbsp;/&nbsp;Answer"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Create Question/Answer Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Question/Answer Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Question<IMG SRC="../images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT="">/<IMG SRC="../images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT="">Answer</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
						<FORM NAME="newCourseForm" onSubmit="return false">
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=3><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>

<%
Iterator faqElements = adminFAQ.getElements();
if (faqElements.hasNext())
{
    while (faqElements.hasNext())
    {
        FAQElementBean element = (FAQElementBean)faqElements.next();
%>
							<TR>
								<TD WIDTH=3 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=502 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<!-- Begin One Result Entry -->
							<TR HEIGHT=19>
								<TD ALIGN=left VALIGN=top><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Q: <%= element.getName() %></P></TD>
								<TD ALIGN=left VALIGN=top><A HREF="javascript:if (confirm('Are you sure?')) location = 'admin-faq-content.jsp?id=<%= request.getParameter("id") %>&deleteId=<%= element.getId() %>'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Question/Answer"></A></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=3><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#EEEEEE">
								<TD ALIGN=left VALIGN=top><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left VALIGN=top><P style="font-size: 9pt; font-family: Arial; color: #000000;">A: <%= element.getAnswer() %></P></TD>
								<TD ALIGN=left VALIGN=top><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=3><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
							</TR>
							<!-- End One Result Entry -->
<%
    }
}
else
{
%>
      <tr><td><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No content found</p></td></tr>
<%
}
%>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=3><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</FORM>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Question/Answer Area -->
		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>