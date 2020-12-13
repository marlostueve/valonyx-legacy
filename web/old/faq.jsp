<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="selectedDepartment" class="com.badiyan.uk.beans.DepartmentBean" scope="session" />
<jsp:useBean id="selectedGroup" class="com.badiyan.uk.beans.PersonGroupBean" scope="session" />
<jsp:useBean id="selectedTitle" class="com.badiyan.uk.beans.PersonTitleBean" scope="session" />

<jsp:useBean id="faqSearch" class="com.badiyan.uk.beans.FAQSearchBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
listHelper.setCompany(adminCompany);
faqSearch.setCompany(adminCompany);


if (session.getAttribute("invalidatefaq") != null)
{
    if (session.getAttribute("invalidatefaq").equals("true"))
    {
        session.setAttribute("invalidatefaq", "false");
        faqSearch.invalidateSearchResults();
    }
}

Calendar now = Calendar.getInstance();
boolean applyAudience = true;

if (request.getParameter("page") != null)
{
    if (request.getParameter("page").equals("next"))
        faqSearch.nextPage();
    else if (request.getParameter("page").equals("previous"))
        faqSearch.previousPage();
}
if (request.getParameter("view") != null)
{
    faqSearch.clearSearchCriteria();
    try
    {
        if (applyAudience)
        {
            UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
            faqSearch.setUserGroup(selectedGroup);
            faqSearch.setJobTitle(selectedTitle);
	    faqSearch.setDepartment(selectedDepartment);
        }
    }
    catch (Exception x)
    {
        x.printStackTrace();
    }
    faqSearch.search();
}
if (request.getParameter("sort") != null)
{
    if (request.getParameter("sort").equals("0"))
        faqSearch.setSortBy(FaqPeer.EFFECTIVEDATE);
    else if (request.getParameter("sort").equals("1"))
        faqSearch.setSortBy(FaqPeer.FAQNAME);
    /*
    else if (request.getParameter("sort").equals("2"))
        faqSearch.setSortBy(FaqPeer.CATEGORYNAME);
    else if (request.getParameter("sort").equals("3"))
        faqSearch.setSortBy(FaqPeer.MANUFACTURERNAME);
    */
    try
    {
        if (applyAudience)
        {
            UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
            faqSearch.setUserGroup(selectedGroup);
            faqSearch.setJobTitle(selectedTitle);
	    faqSearch.setDepartment(selectedDepartment);
        }
    }
    catch (Exception x)
    {
        x.printStackTrace();
    }
    faqSearch.search();
}

%>

<struts-html:html locale="true">
<head>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--

function
processSort()
{
    location='faq.jsp?sort=' + document.sortForm.sort4.selectedIndex;
}

function
initForm()
{

}

// -->
</script>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages(); setTableBG('FAQList'); initForm();">
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
	<!-- Begin Bottom Content Centering Table -->
	<TR>
		<!-- Begin Bottom Content Centering Table (Left Spacer) -->
		<TD WIDTH=20 ALIGN=left VALIGN=top><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- End Bottom Content Centering Table (Left Spacer) -->
		<!-- Begin Bottom Content Centering Table (Center Area) -->
		<TD WIDTH=732 ALIGN=center>
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR>
					<TD WIDTH=380 ALIGN=left VALIGN=top>
						<!-- Begin FAQ Search Area -->
						<TABLE WIDTH=380 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;FAQ Search</P></TD>
							</TR>
							<TR>
								<TD>
									<TABLE WIDTH=378 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
									<struts-html:form action="/faq" focus="keywordInput">
										<TR HEIGHT=26>
											<TD COLSPAN=2 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;Search for FAQs using the search feature below.</P></TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=2 BGCOLOR="#DDDDDD"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=4 ALT=""></TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=2><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
										</TR>
										<TR HEIGHT=30>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><IMG SRC="images/trspacer.gif" WIDTH=4 HEIGHT=1 ALT="">Category:</P></TD>
											<TD ALIGN=left>
                                                                                            <struts-html:select property="categorySelect" style="font-size: 13px; width: 260px;" >
                                                                                                <struts-html:option value="0">-- ALL --</struts-html:option>
                                                                                                <struts-html:optionsCollection name="listHelper" property="categories" />
                                                                                            </struts-html:select>
											</TD>
										</TR>
										<TR HEIGHT=30>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><IMG SRC="images/trspacer.gif" WIDTH=4 HEIGHT=1 ALT="">Keyword(s):</P></TD>
											<TD ALIGN=left><struts-html:text property="keywordInput" style="font-size: 13px; width: 259px;" /></TD>
										</TR>
										<TR>
											<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="Search" VALUE="Search"><IMG SRC="images/trspacer.gif" WIDTH=7 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=2><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=3 ALT=""></TD>
										</TR>
									</struts-html:form>
									</TABLE>
								</TD>
							</TR>
						</TABLE>
						<!-- End FAQ Search Area -->
					</TD>
					<TD WIDTH=20 ALIGN=left></TD>
					<TD WIDTH=332 ALIGN=left VALIGN=top><BR></TD>
				</TR>
			</TABLE>
			<!-- Begin 20 Pixel Spacer -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=0 CELLSPACING=0>
				<TR>
					<TD><IMG SRC="images/trspacer.gif" WIDTH=732 HEIGHT=20 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- End 20 Pixel Spacer -->
			<!-- Begin Sort By for Completed Courses -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=0 CELLSPACING=0>
                            <form name="sortForm">
				<TR>
					<TD ALIGN=right VALIGN=center WIDTH=595><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Sort by&nbsp;&nbsp;</P></TD>
					<TD ALIGN=right VALIGN=center WIDTH=137>
						<SELECT NAME="sort4" SIZE="1" onChange="processSort();">
						<OPTION VALUE="1">Date Published</OPTION>
						<OPTION VALUE="2">Document Name</OPTION>
						<OPTION VALUE="3">Category</OPTION>
						<OPTION VALUE="4">Manufacturer&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</OPTION>
						</SELECT>
					</TD>
				</TR>
                            </form>
			</TABLE>
			<!-- End Sort By for Completed Courses -->
<%
boolean resultsFound = false;
if (faqSearch.isDefaultSearch())
{
%>
			<!-- Begin FAQs Table -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#9966CC">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG6.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;FAQ Topics</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=726 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">To view a FAQ, click on the FAQ name.</P></TD>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#DACCE1">
							<TR>
								<TD WIDTH=76 ALIGN=center><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Updated</P></TD>
								<TD WIDTH=400 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">FAQ Name</P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Category</P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Manufacturer</P></TD>
							</TR>
						</TABLE>
						<TABLE ID="FAQList" WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#FFFFFF">
<%
    Enumeration resources = faqSearch.getList();
    if (resources.hasMoreElements())
    {
        resultsFound = true;
        while (resources.hasMoreElements())
        {
            FAQBean faq = (FAQBean)resources.nextElement();
            boolean isNew = false;
            try
            {
                Calendar broadcastDate = Calendar.getInstance();
                broadcastDate.setTime(faq.getEffectiveDate());
                broadcastDate.add(Calendar.DATE, faq.getDisplayAsNewDays());
                if (broadcastDate.after(now))
                    isNew = true;
            }
            catch (Exception xx)
            {
            }
%>
							<!-- Begin One Entry -->
							<TR>
								<TD WIDTH=74 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= faq.getReleasedDateString() %></P></TD>
								<TD WIDTH=400 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="#" onClick="window.open('faq-detail.jsp?id=<%= faq.getId() %>','FAQWin','scrollbars=yes,resizable=yes,toolbar=no,location=no,directories=no,status=no,menubar=yes,width=520,height=500');" style="color: #000000"><%= faq.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></A></P></TD>
								<TD WIDTH=128 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= faq.getCategoryNameString() %></P></TD>
								<TD WIDTH=128 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">OLD_MAN</P></TD>
							</TR>
							<!-- End One Entry -->
<%
        }
    }
    else
    {
%>
							<!-- Begin One Entry -->
							<TR>
								<TD COLSPAN=4><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">No FAQs Found</P></TD>
							</TR>
							<!-- End One Entry -->
<%
    }
%>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End FAQs Table -->
<%
}
else
{
%>
                        <!-- Begin Search Results Table -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#9966CC">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG6.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;FAQ Search Results</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=566 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">To view a FAQ, click on the FAQ name.</P></TD>
								<TD WIDTH=160 ALIGN=right><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #003366;"><A HREF="faq.jsp?view=true" style="color: #003366">View All FAQ Topics</A></P></TD>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#DACCE1">
							<TR>
								<TD WIDTH=76 ALIGN=center><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Updated</P></TD>
								<TD WIDTH=400 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">FAQ Name</P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Category</P></TD>
							</TR>
						</TABLE>
						<TABLE ID="FAQList" WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#FFFFFF">
<%
    Enumeration resources = faqSearch.getList();
    if (resources.hasMoreElements())
    {
        resultsFound = true;
        while (resources.hasMoreElements())
        {
            FAQBean faq = (FAQBean)resources.nextElement();
            boolean isNew = false;
            Calendar broadcastDate = Calendar.getInstance();
            broadcastDate.setTime(faq.getEffectiveDate());
            broadcastDate.add(Calendar.DATE, faq.getDisplayAsNewDays());
            if (broadcastDate.after(now))
                isNew = true;
%>
							<!-- Begin One Entry -->
							<TR>
								<TD WIDTH=74 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= faq.getReleasedDateString() %></P></TD>
								<TD WIDTH=400 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="#" onClick="window.open('faq-detail.jsp?id=<%= faq.getId() %>','FAQWin','scrollbars=yes,resizable=yes,toolbar=no,location=no,directories=no,status=no,menubar=yes,width=520,height=500');" style="color: #000000"><%= faq.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></A></P></TD>
								<TD WIDTH=128 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= faq.getCategoryNameString() %></P></TD>
								<TD WIDTH=128 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">OLD_MAN</P></TD>
							</TR>
							<!-- End One Entry -->
<%
        }
    }
    else
    {
%>
							<!-- Begin One Entry -->
							<TR>
								<TD COLSPAN=4><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">No FAQs Found</P></TD>
							</TR>
							<!-- End One Entry -->
<%
    }
%>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Search Results Table -->
<%
}
%>
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
					<TD ALIGN=right><P style="font-size: 9pt; font-family: Arial; color: #000000;">Displaying <%= faqSearch.getCurrentPageMinimum() %>-<%= faqSearch.getCurrentPageMaximum() %> of (<%= faqSearch.getNumberOfResults() %>)</P></TD>
					<TD ALIGN=right>
<%
    if (faqSearch.hasPreviousPage())
    {
%>
                                            <A HREF="faq.jsp?page=previous"><IMG SRC="images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous"></A>
<%
    }
    else
    {
%>
                                            <IMG SRC="images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous">
<%
    }
    if (faqSearch.hasNextPage())
    {
%>
                                            <A HREF="faq.jsp?page=next"><IMG SRC="images/PttrsnUK_BTN-Down.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Next"></A>
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
		</TD>
		<!-- End Bottom Content Centering Table (Center Area) -->
		<!-- Begin Bottom Content Centering Table (Right Spacer) -->
		<TD WIDTH=20 ALIGN=left VALIGN=top><IMG SRC="images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- End Bottom Content Centering Table (Right Spacer) -->
	</TR>
	<!-- End Bottom Content Centering Table -->
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>