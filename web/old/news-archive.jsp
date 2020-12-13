<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="newsItemSearch" class="com.badiyan.uk.online.beans.UKOnlineNewsItemSearchBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
if (request.getParameter("page") != null)
{
    if (request.getParameter("page").equals("next"))
        newsItemSearch.nextPage();
    else if (request.getParameter("page").equals("previous"))
        newsItemSearch.previousPage();
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
		<TD WIDTH=732 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=732 HEIGHT=24 ALT=""></TD>
		<TD WIDTH=20 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=24 ALT=""></TD>
	</TR>
	<!-- End 24 Pixel Spacer -->
</TABLE>
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<TR>
		<!-- Begin Left Spacer -->
		<TD WIDTH=20 ALIGN=left VALIGN=top><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- End Left Spacer -->
		<!-- Begin Center Area -->
		<TD WIDTH=732 ALIGN=left VALIGN=top>
			<!-- Begin Archived What's New... Table -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Archived What's New...</P></TD>
				</TR>
				<TR>
					<TD>
<%
boolean resultsFound = false;
if (!newsItemSearch.searchPerformed())
{
    newsItemSearch.search();
}
Enumeration objects = newsItemSearch.getList();
if (objects.hasMoreElements())
{
    resultsFound = true;
    while (objects.hasMoreElements())
    {
        UKOnlineNewsItemBean newsItem = (UKOnlineNewsItemBean)objects.nextElement();
%>
						<!-- Begin One News Item -->
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=20>
								<TD WIDTH=3><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=727 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #003366;"><A HREF="news-item.jsp?id=<%= newsItem.getId() %>" style="color: #003366"><%= newsItem.getNewsItemName() %></A></P></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=2><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#EEEEEE">
								<TD WIDTH=3><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=727 ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #000000;"><%= newsItem.getShortDescription() %></P></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=2><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
						<!-- End One News Item -->
<%
    }
}
else
{
%>
						<!-- Begin One News Item -->
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR>
								<TD COLSPAN=6><P style="font-size: 9pt; font-family: Arial; color: #000000;">No News Items Found</P></TD>
							</TR>
						</TABLE>
						<!-- End One News Item -->
<%
}
%>
					</TD>
				</TR>
			</TABLE>
			<!-- Begin Spacer -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#CCA34A">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=732 HEIGHT=4 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- End Spacer -->
<%
if (resultsFound)
{
%>
			<!-- Begin Results Display and Arrows -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#CCA34A">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=right><IMG SRC="images/trspacer.gif" WIDTH=285 HEIGHT=1 ALT=""></TD>
					<TD ALIGN=right><IMG SRC="images/trspacer.gif" WIDTH=400 HEIGHT=1 ALT=""></TD>
					<TD ALIGN=right><IMG SRC="images/trspacer.gif" WIDTH=47 HEIGHT=1 ALT=""></TD>
				</TR>
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=right>&nbsp;</TD>
					<TD ALIGN=right><P style="font-size: 9pt; font-family: Arial; color: #000000;">Displaying <%= newsItemSearch.getCurrentPageMinimum() %>-<%= newsItemSearch.getCurrentPageMaximum() %> of (<%= newsItemSearch.getNumberOfResults() %>)</P></TD>
					<TD ALIGN=right>
<%
    if (newsItemSearch.hasPreviousPage())
    {
%>
                                            <A HREF="news-archive.jsp?page=previous"><IMG SRC="images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous"></A>
<%
    }
    else
    {
%>
                                            <IMG SRC="images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous">
<%
    }
    if (newsItemSearch.hasNextPage())
    {
%>
                                            <A HREF="news-archive.jsp?page=next"><IMG SRC="images/PttrsnUK_BTN-Down.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Next"></A>
<%
    }
    else
    {
%>
                                            <IMG SRC="images/PttrsnUK_BTN-Down.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Next">
<%
    }
%>
                                        </TD>
				</TR>
			</TABLE>
			<!-- End Results Display and Arrows -->
<%
}
%>
			<!-- End Archived What's New... Table -->
		</TD>
		<!-- End Center Area -->
		<!-- Begin Right Spacer -->
		<TD WIDTH=20 ALIGN=left VALIGN=top><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- End Right Spacer -->
	</TR>
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>