<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>
<jsp:useBean id="loginBean" class="com.badiyan.uk.beans.PattersonLoginBean" scope="session" />
<jsp:useBean id="userNewsItem" class="com.badiyan.uk.beans.PattersonNewsItemBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
if (request.getParameter("id") != null)
{
    try
    {
        int newsItemId = Integer.parseInt(request.getParameter("id"));
        userNewsItem = (PattersonNewsItemBean)NewsItemBean.getNewsItemBean(newsItemId);
        session.setAttribute("userNewsItem", userNewsItem);
    }
    catch (Exception x)
    {
    }
}
%>

<struts-html:html locale="true">
<head>
<struts-html:base/>
<title>Universal Knowledge</title>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<%@ include file="channels\channel-head-javascript.jsp" %>
</head>
<body BGCOLOR=#FFFFFF LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages();">
<%@ include file="channels\channel-menu.jsp" %>
<!-- BEGIN Bottom Content Area -->
<table WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<tr><!-- DEFINE COLUMNS -->
		<td ALIGN=left VALIGN=top><img SRC="images/trspacer.gif" WIDTH=100 HEIGHT=1 ALT=""></td>
		<td ALIGN=left VALIGN=top><img SRC="images/trspacer.gif" WIDTH=572 HEIGHT=1 ALT=""></td>
		<td ALIGN=left VALIGN=top><img SRC="images/trspacer.gif" WIDTH=100 HEIGHT=1 ALT=""></td>

	</tr>
	<tr><!-- 23 Pixel Spacer -->
		<td ALIGN=left VALIGN=top COLSPAN=3><img SRC="images/trspacer.gif" WIDTH=772 HEIGHT=23 ALT=""></td>
	</tr>
	<!-- BEGIN Question -->
	<tr>
		<td ALIGN=left VALIGN=top><img SRC="images/trspacer.gif" WIDTH=100 HEIGHT=1 ALT=""></td>
		<td ALIGN=left VALIGN=top>
			<table WIDTH=572 BORDER=0 CELLPADDING=0 CELLSPACING=0>
				<tr><!-- DEFINE COLUMNS & Blue Line -->
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=10 HEIGHT=1 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=550 HEIGHT=1 ALT=""></td>

					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=10 HEIGHT=1 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></td>
				</tr>
				<tr><!-- 10 Pixel Spacer -->
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=1 HEIGHT=10 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="B1CBE6" COLSPAN=3><img SRC="images/trspacer.gif" WIDTH=570 HEIGHT=10 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=1 HEIGHT=10 ALT=""></td>
				</tr>
                                <struts-html:form action="/assessmentDetail">
				<tr><!-- Text Area -->
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="B1CBE6"><img SRC="images/trspacer.gif" WIDTH=10 HEIGHT=1 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="B1CBE6">
                                                <span style="font-size: 10pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;<%= userNewsItem.getName() %><br></span>
                                                <span style="font-size: 3pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;<br></span>
                                                <span style="font-size: 10pt; font-family: Arial; color: #000000;">&nbsp;<%= userNewsItem.getCreationDateString() %> - <%= userNewsItem.getNewsText() %><br></span>
						<span style="font-size: 3pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;<br></span>
                                                <span style="font-size: 10pt; font-family: Arial; color: #000000;">&nbsp;<a href="<%= userNewsItem.getURLString() %>"><%= userNewsItem.getURLString() %></a><br></span>
					</td>
					<td ALIGN=left VALIGN=top BGCOLOR="B1CBE6"><img SRC="images/trspacer.gif" WIDTH=10 HEIGHT=1 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></td>
				</tr>
                                <tr>
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=1 HEIGHT=10 ALT=""></td>
                                        <td ALIGN=right VALIGN=top BGCOLOR="B1CBE6" COLSPAN=2><span style="font-size: 10pt; font-family: Arial; font-weight: bold; color: #ff0000;"><struts-html:errors/></span>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                        <td ALIGN=left VALIGN=top BGCOLOR="B1CBE6"><img SRC="images/trspacer.gif" WIDTH=10 HEIGHT=10 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=1 HEIGHT=10 ALT=""></td>
				</tr>
                                </struts-html:form>
				<tr><!-- 10 Pixel Spacer -->
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=1 HEIGHT=10 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="B1CBE6" COLSPAN=3><img SRC="images/trspacer.gif" WIDTH=570 HEIGHT=10 ALT=""></td>
					<td ALIGN=left VALIGN=top BGCOLOR="003366"><img SRC="images/trspacer.gif" WIDTH=1 HEIGHT=10 ALT=""></td>
				</tr>
				<tr><!-- Blue Line -->
					<td ALIGN=left VALIGN=top BGCOLOR="003366" COLSPAN=5><img SRC="images/trspacer.gif" WIDTH=572 HEIGHT=1 ALT=""></td>
				</tr>
			</table>
		</td>

		<td ALIGN=left VALIGN=top><img SRC="images/trspacer.gif" WIDTH=100 HEIGHT=1 ALT=""></td>
	</tr>
	<!-- END Question -->
</table>
<!-- END Bottom Content Area -->
<!-- Begin Bottom Copyright Info Area -->
<table WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<tr>
		<td WIDTH=772 ALIGN=center><img SRC="images/trspacer.gif" WIDTH=772 HEIGHT=40 ALT=""></td>
	</tr>
	<tr>

		<td WIDTH=772 ALIGN=center><p style="font-size: 8pt; font-family: Arial; color: #000000;">&copy; 2003 Patterson Dental Supply, Inc. All rights reserved.</p></td>
	</tr>
	<tr>
		<td WIDTH=772 ALIGN=center><img SRC="images/trspacer.gif" WIDTH=772 HEIGHT=6 ALT=""></td>
	</tr>
</table>
<!-- End Bottom Copyright Info Area -->
</body>
</struts-html:html>