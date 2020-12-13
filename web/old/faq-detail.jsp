<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
FAQBean faqBean = FAQBean.getFAQ(Integer.parseInt(request.getParameter("id")));
%>

<HTML>
<HEAD>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0>
<TABLE WIDTH=500 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<TR>
		<TD WIDTH=500 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=500 HEIGHT=20 ALT=""></TD>
	</TR>
</TABLE>
<TABLE WIDTH=500 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<TD WIDTH=460 ALIGN=left>
<%
Iterator itr = faqBean.getElements();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
        FAQElementBean faqElement = (FAQElementBean)itr.next();
%>
		<P style="font-size: 10pt; font-family: Arial; font-weight: bold; color: #003366;"><%= faqElement.getNameString() %></P>
		<P style="font-size: 10pt; font-family: Arial; color: #000000;"><%= faqElement.getAnswer() %></P>
<%
    }
}
else
{
%>
		<P style="font-size: 10pt; font-family: Arial; font-weight: bold; color: #000000;">No questions found for <%= faqBean.getLabel() %></P>
<%
}
%>
		</TD>
		<TD WIDTH=20 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
</TABLE>
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</HTML>