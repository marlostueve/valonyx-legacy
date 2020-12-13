<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminNewsItem" class="com.badiyan.uk.online.beans.UKOnlineNewsItemBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
System.out.println("A");
if (request.getParameter("id") != null)
{
    try
    {
        int newsItemId = Integer.parseInt(request.getParameter("id"));
        adminNewsItem = (UKOnlineNewsItemBean)UKOnlineNewsItemBean.getNewsItem(newsItemId);
    }
    catch (NumberFormatException x)
    {
    }
    catch (Exception x)
    {
        x.printStackTrace();
    }
}
else
    adminNewsItem = new UKOnlineNewsItemBean();

session.setAttribute("adminNewsItem", adminNewsItem);

System.out.println("B");
%>

<struts-html:html locale="true">
<HEAD>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<struts-html:javascript formName="newsItemForm" dynamicJavascript="true" staticJavascript="false"/>
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
    <struts-html:form action="/admin/newsItem" focus="nameInput" onsubmit="return validateNewsItemForm(this);">
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
<%@ include file="channels\channel-admin-menu.jsp" %>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- Begin Right Column Content -->
		<TD WIDTH=542 ALIGN=left VALIGN=top>
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">What's New... Manager&nbsp;&nbsp;&gt;&nbsp;&nbsp;Create New News Item</P>
			<!-- Begin New News Item Navigation Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Step 1: Basic Information</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=3>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left COLSPAN=3 BGCOLOR="#EEEEEE"><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=190 ALIGN=left VALIGN=top >
									<P style="font-size: 10pt; font-family: Arial; font-weight: bold; color: #336699;">
<%
if (adminNewsItem.isNew())
{
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/PttrsnUK_CurrentArrow.jpg" WIDTH=10 HEIGHT=10 ALT="">Step 1:&nbsp;&nbsp;Basic Information<br>
<%
}
else
{
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/PttrsnUK_CurrentArrow.jpg" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-news-item.jsp<%= (adminNewsItem.isNew()) ? "" : "?id=" + adminNewsItem.getId() %>" style="color: #000000">Step 1:&nbsp;&nbsp;Basic Information</A><br>
<%
}
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=16 HEIGHT=30 ALT=""><INPUT TYPE="submit" NAME="createNewsItem" VALUE="<%= (adminNewsItem.isNew()) ? "Create" : "Save" %> News Item">
									</P>
								</TD>
								<TD WIDTH=10 ALIGN=left VALIGN=top BGCOLOR="#EEEEEE"><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=310 ALIGN=left VALIGN=top BGCOLOR="#EEEEEE">
									<P style="font-size: 10pt; font-family: Arial; color: #000000;">Instructions:</P>
                                                                        <P style="font-size: 10pt; font-family: Arial; color: #ff0000;"><struts-html:errors/></P>
								</TD>
								<TD WIDTH=10 ALIGN=left VALIGN=top BGCOLOR="#EEEEEE"><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=5>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left COLSPAN=3 BGCOLOR="#EEEEEE"><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
<%
System.out.println("C");
%>
			<!-- End New News Item Navigation Area -->
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
								<TD WIDTH=110 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=410 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Last Update:</P></TD>
								<TD><P style="font-size: 10pt; font-family: Arial; color: #000000;"><%= adminNewsItem.getModificationDateString() %></P></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;News Item Name:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="true">
                                                                        <struts-html:text property="nameInput" style="font-size: 13px; width: 400px;" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="false">
                                                                        <struts-html:text property="nameInput" style="font-size: 13px; width: 400px;" value="<%= adminNewsItem.getName() %>" />
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Short<br>&nbsp;Description:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="true">
                                                                        <struts-html:textarea property="shortDescInput" rows="4" style="font-size: 13px; width: 400px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="false">
                                                                        <struts-html:textarea property="shortDescInput" rows="4" style="font-size: 13px; width: 400px;" value="<%= adminNewsItem.getShortDescription() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;User Group:</P></TD>
								<TD ALIGN=left>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="true">
                                                                        <struts-html:select property="userGroupSelect" style="font-size: 13px; width: 400px;">
                                                                            <struts-html:option value="0">-- SELECT A USER GROUP --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="personGroups" />
                                                                        </struts-html:select>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="false">
                                                                        <struts-html:select property="userGroupSelect" style="font-size: 13px; width: 400px;" value="<%= adminNewsItem.getPersonGroupIdString() %>" >
                                                                            <struts-html:option value="0">-- SELECT A USER GROUP --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="personGroups" />
                                                                        </struts-html:select>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
<%
System.out.println("E");
%>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Owner:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="true">
                                                                        <struts-html:select property="ownerSelect" style="font-size: 13px; width: 400px;" >
                                                                            <struts-html:option value="0">-- SELECT AN OWNER FOR THIS NEWS ITEM --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="possibleCourseOwners" />
                                                                        </struts-html:select>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="false">
                                                                        <struts-html:select property="ownerSelect" style="font-size: 13px; width: 400px;" value="<%= adminNewsItem.getOwnerIdString() %>" >
                                                                            <struts-html:option value="0">-- SELECT AN OWNER FOR THIS NEWS ITEM --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="possibleCourseOwners" />
                                                                        </struts-html:select>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
<%
System.out.println("F");
%>
							<TR HEIGHT=4>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=3 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Published:</P></TD>
								<TD ALIGN=left>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="true">
                                                                        <struts-html:text property="publishedDateInput" style="font-size: 13px; width: 80px;" maxlength="10" onfocus="getDate('newsItemForm','publishedDateInput');" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="false">
                                                                        <struts-html:text property="publishedDateInput" value="<%= (adminNewsItem.getPublishedDateString().equals("")) ? "MM/DD/YYYY" : adminNewsItem.getPublishedDateString() %>" style="font-size: 13px; width: 80px;" maxlength="10" onfocus="getDate('newsItemForm','publishedDateInput');" />
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;News Text:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="true">
                                                                        <struts-html:textarea property="newsTextInput" rows="12" style="font-size: 13px; width: 400px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="false">
                                                                        <struts-html:textarea property="newsTextInput" rows="12" style="font-size: 13px; width: 400px;" value="<%= adminNewsItem.getNewsText() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;URL Link:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="true">
                                                                        <struts-html:text property="URLInput" style="font-size: 13px; width: 400px;" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="false">
                                                                        <struts-html:text property="URLInput" style="font-size: 13px; width: 400px;" value="<%= adminNewsItem.getURLString() %>" />
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=4>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
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
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Released:</P></TD>
								<TD ALIGN=left>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="true">
                                                                        <struts-html:text property="releasedDateInput" style="font-size: 13px; width: 80px;" maxlength="10" onfocus="getDate('newsItemForm','releasedDateInput');" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="false">
                                                                        <struts-html:text property="releasedDateInput" value="<%= (adminNewsItem.getReleasedDateString().equals("")) ? "MM/DD/YYYY" : adminNewsItem.getReleasedDateString() %>" style="font-size: 13px; width: 80px;" maxlength="10" onfocus="getDate('newsItemForm','releasedDateInput');" />
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Expires:</P></TD>
								<TD ALIGN=left>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="true">
                                                                        <struts-html:text property="expiresDateInput" style="font-size: 13px; width: 80px;" maxlength="10" onfocus="getDate('newsItemForm','expiresDateInput');" />
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminNewsItem" property="isNew" value="false">
                                                                        <struts-html:text property="expiresDateInput" value="<%= (adminNewsItem.getExpirationDateString().equals("")) ? "MM/DD/YYYY" : adminNewsItem.getExpirationDateString() %>" style="font-size: 13px; width: 80px;" maxlength="10" onfocus="getDate('newsItemForm','expiresDateInput');" />
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