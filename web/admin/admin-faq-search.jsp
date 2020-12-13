<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="faqSearch" class="com.badiyan.uk.beans.FAQSearchBean" scope="session"/>
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
faqSearch.setCompany(adminCompany);

if (request.getParameter("deleteId") != null)
{
    FAQBean.delete(Integer.parseInt(request.getParameter("deleteId")));
    faqSearch.invalidateSearchResults();
}
if (request.getParameter("page") != null)
{
    if (request.getParameter("page").equals("next"))
        faqSearch.nextPage();
    else if (request.getParameter("page").equals("previous"))
        faqSearch.previousPage();
}
%>

<struts-html:html locale="true">
<HEAD>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<struts-html:javascript formName="faqSearchForm" dynamicJavascript="true" staticJavascript="false"/>
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
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">FAQ Manager</P>

			<!-- Begin New FAQ Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Create New FAQ</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
						<FORM NAME="newCourseForm" onSubmit="return false">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD COLSPAN=3 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=34>
								<TD ALIGN=right>
									<INPUT TYPE="button" NAME="createNewFAQ" VALUE="Create New FAQ" style="font-size: 9pt; font-family: Arial;" onClick="window.location.href='admin-faq.jsp?new=true'"><IMG SRC="../images/trspacer.gif" WIDTH=7 HEIGHT=1 ALT="">
								</TD>
							</TR>
						</FORM>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End New FAQ Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Search FAQ(s) Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Search FAQ(s)</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/faqSearch" focus="keywordSearchBox" onsubmit="return validateFaqSearchForm(this);">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD WIDTH=85 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=355 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=80 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Course:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                    <struts-html:select property="courseSelect" style="font-size: 9pt; font-family: Arial; width: 428px;" >
                                                                        <struts-html:option value="0">-- ALL COURSES --</struts-html:option>
                                                                        <struts-html:optionsCollection name="listHelper" property="courses" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Certification:</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                    <struts-html:select property="certificationSelect" style="font-size: 9pt; font-family: Arial; width: 428px;" >
                                                                        <struts-html:option value="0">-- ALL CERTIFICATIONS --</struts-html:option>
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Category:</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                    <struts-html:select property="categorySelect" style="font-size: 9pt; font-family: Arial; width: 428px;" >
                                                                        <struts-html:option value="0">-- ALL CATEGORIES --</struts-html:option>
                                                                        <struts-html:optionsCollection name="listHelper" property="categories" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;User Group:</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                    <struts-html:select property="userGroupSelect" style="font-size: 9pt; font-family: Arial; width: 428px;" >
                                                                        <struts-html:option value="0">-- ALL USER GROUPS --</struts-html:option>
                                                                        <struts-html:optionsCollection name="listHelper" property="personGroups" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Department:</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                    <struts-html:select property="departmentSelect" style="font-size: 9pt; font-family: Arial; width: 428px;" >
                                                                        <struts-html:option value="0">-- ALL DEPARTMENTS --</struts-html:option>
                                                                        <struts-html:optionsCollection name="listHelper" property="departments" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Job Title:</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                    <struts-html:select property="jobTitleSelect" style="font-size: 9pt; font-family: Arial; width: 428px;" >
                                                                        <struts-html:option value="0">-- ALL JOB TITLES --</struts-html:option>
                                                                        <struts-html:optionsCollection name="listHelper" property="personTitles" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Expires:</P></TD>
								<TD>
                                                                    <struts-html:text property="expiresDateInput" style="font-size: 9pt; font-family: Arial; width: 80px;" maxlength="10" onfocus="getDate('faqSearchForm','expiresDateInput');" />
								</TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=30>
								<TD WIDTH=89 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;FAQ Name:</P></TD>
								<TD WIDTH=351 ALIGN=left><struts-html:text property="keywordSearchBox" style="font-size: 9pt; font-family: Arial; width: 350px;" /></TD>
								<TD WIDTH=80 ALIGN=right><INPUT TYPE="submit" NAME="Search" VALUE="Search" style="font-size: 9pt; font-family: Arial;"><IMG SRC="../images/trspacer.gif" WIDTH=7 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=3>
								<TD COLSPAN=3 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Search FAQ(s) Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Search Results Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Search Results</P></TD>
							</TR>
							<TR BGCOLOR="#FFFFFF" HEIGHT=26>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;To edit the details for a FAQ, click on the FAQ name.</P></TD>
							</TR>
							<TR BGCOLOR="#9EBFE9" HEIGHT=8>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
								<TD ALIGN=left>
									<TABLE WIDTH=520 BORDER=0 CELLPADDING=0 CELLSPACING=0>
<%
boolean resultsFound = false;
Enumeration faqs = faqSearch.getList();
FAQBean faq = null;
if (faqs.hasMoreElements())
{
    resultsFound = true;
    while (faqs.hasMoreElements())
    {
        faq = (FAQBean)faqs.nextElement();
%>
										<TR>
											<TD WIDTH=2 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=330 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=150 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<!-- Begin One Result Entry -->
										<TR>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_FAQ.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="FAQ"></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-faq.jsp?id=<%= faq.getIdString() %>" style="color: #000000"><%= faq.getNameString() %></A></P></TD>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">OLD_MAN</P></TD>
											<TD ALIGN=left><A HREF="javascript:if (confirm('Are you sure?')) location = 'admin-faq-search.jsp?deleteId=<%= faq.getIdString() %>'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete FAQ"></A></TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=6>
												<TABLE WIDTH=520 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#EEEEEE">
													<TR BGCOLOR="#DDDDDD">
														<TD WIDTH=18 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=65 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=95 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=63 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=262 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Created:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= faq.getCreationDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Category:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= faq.getCategoryNameString() %></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Released:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= faq.getReleasedDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">User Group:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">[REMOVED PERSON GROUP]</TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Expires:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= faq.getExpirationDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Sub Group:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Last Update:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= faq.getModificationDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Region:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">[REMOVED REGION]</TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Location:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">[REMOVED LOCATION]</TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Job Title:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">[REMOVED JOB TITLE]</TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR BGCOLOR="#DDDDDD">
														<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
													</TR>
												</TABLE>
											</TD>
										</TR>
										<!-- End One Result Entry -->
<%
    }
}
else
{
%>
      <tr><td><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No FAQs found</p></td></tr>
<%
}
%>
									</TABLE>
								</TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Search Results Area -->
			<!-- Begin Spacer -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#CCA34A">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=522 HEIGHT=4 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- End Spacer -->
<%
if (resultsFound)
{
%>
			<!-- Begin Results Display and Arrows -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#CCA34A">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=right><IMG SRC="../images/trspacer.gif" WIDTH=100 HEIGHT=1 ALT=""></TD>
					<TD ALIGN=right><IMG SRC="../images/trspacer.gif" WIDTH=375 HEIGHT=1 ALT=""></TD>
					<TD ALIGN=right><IMG SRC="../images/trspacer.gif" WIDTH=47 HEIGHT=1 ALT=""></TD>
				</TR>
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=right>&nbsp;</TD>
					<TD ALIGN=right><P style="font-size: 9pt; font-family: Arial; color: #000000;">Displaying <%= faqSearch.getCurrentPageMinimum() %>-<%= faqSearch.getCurrentPageMaximum() %> of (<%= faqSearch.getNumberOfResults() %>)</P></TD>
					<TD ALIGN=right>
<%
    if (faqSearch.hasPreviousPage())
    {
%>
                                            <A HREF="admin-faq-search.jsp?page=previous"><IMG SRC="../images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous"></A>
<%
    }
    else
    {
%>
                                            <IMG SRC="../images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous">
<%
    }
    if (faqSearch.hasNextPage())
    {
%>
                                            <A HREF="admin-faq-search.jsp?page=next"><IMG SRC="../images/PttrsnUK_BTN-Down.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Next"></A>
<%
    }
    else
    {
%>
                                            <IMG SRC="../images/PttrsnUK_BTN-Down.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Next">
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
		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>