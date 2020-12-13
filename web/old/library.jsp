<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="selectedDepartment" class="com.badiyan.uk.beans.DepartmentBean" scope="session" />
<jsp:useBean id="selectedGroup" class="com.badiyan.uk.beans.PersonGroupBean" scope="session" />
<jsp:useBean id="selectedTitle" class="com.badiyan.uk.beans.PersonTitleBean" scope="session" />

<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
System.out.println("HIYA!!");

listHelper.setCompany(adminCompany);
resourceSearch.setCompany(adminCompany);

if (session.getAttribute("invalidatelibrary") != null)
{
    if (session.getAttribute("invalidatelibrary").equals("true"))
    {
        session.setAttribute("invalidatelibrary", "false");
        resourceSearch.invalidateSearchResults();
    }
}

System.out.println("HIYA!!A");

Calendar now = Calendar.getInstance();
boolean applyAudience = true;
System.out.println("HIYA!!B");
if (request.getParameter("page") != null)
{
    if (request.getParameter("page").equals("next"))
        resourceSearch.nextPage();
    else if (request.getParameter("page").equals("previous"))
        resourceSearch.previousPage();
}
if (request.getParameter("save") != null)
{
    try
    {
        UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
        person_obj.addResource(ResourceBean.getResource(Integer.parseInt(request.getParameter("save"))));
    }
    catch (Exception x)
    {
    }
}
if (request.getParameter("remove") != null)
{
    try
    {
        UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
        person_obj.removeResource(ResourceBean.getResource(Integer.parseInt(request.getParameter("remove"))));
    }
    catch (Exception x)
    {
    }
}
if (request.getParameter("view") != null)
{
    resourceSearch.clearSearchCriteria();
}

if (request.getParameter("sort") != null)
{
    /*
    if (request.getParameter("sort").equals("0"))
        resourceSearch.setSortBy(ResourcePeer.EFFECTIVEDATE);
    else if (request.getParameter("sort").equals("1"))
        resourceSearch.setSortBy(ResourcePeer.RESOURCENAME);
    else if (request.getParameter("sort").equals("2"))
        resourceSearch.setSortBy(ResourcePeer.CATEGORYNAME);
    resourceSearch.setDisplayOnlyActive(true);
    if (applyAudience)
    {
        UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
        resourceSearch.setUserGroup(person_obj.getGroup());
        resourceSearch.setJobTitle(person_obj.getJobTitleString());
        resourceSearch.setRegion(DepartmentBean.getDepartment(person_obj.getRegionId()));
        resourceSearch.setLocation(DepartmentBean.getDepartment(person_obj.getLocationId()));
    }
    resourceSearch.search();
    */
}

System.out.println("HIYA!!C");

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
    location='library.jsp?sort=' + document.sortForm.sort4.selectedIndex;
}

function
initForm()
{

}

// -->
</script>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages(); setTableBG('mostAccessed'); initForm();">
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
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR>
					<TD WIDTH=420 ALIGN=left VALIGN=top>
						<!-- Begin Library Search Area -->
						<TABLE WIDTH=420 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Library Search</P></TD>
							</TR>
							<TR>
								<TD>
									<TABLE WIDTH=418 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
									<struts-html:form action="/library" focus="keywordInput">
										<TR HEIGHT=40>
											<TD COLSPAN=2 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;Search for brochures, articles, and presentations using the search feature &nbsp;below.</P></TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=2 BGCOLOR="#DDDDDD"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=4 ALT=""></TD>
										</TR>
										<TR>
											<TD WIDTH=112 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
											<TD WIDTH=306 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
										</TR>
										<TR HEIGHT=30>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><IMG SRC="images/trspacer.gif" WIDTH=4 HEIGHT=1 ALT="">Category:</P></TD>
											<TD ALIGN=left>
                                                                                            <struts-html:select property="categorySelect" style="font-size: 13px; width: 306px;" >
                                                                                                <struts-html:option value="0">-- ALL --</struts-html:option>
												<struts-html:optionsCollection name="listHelper" property="categories" />
                                                                                            </struts-html:select>
											</TD>
										</TR>
										<TR HEIGHT=30>
											<TD ALIGN=left COLSPAN=2>
												<P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><IMG SRC="images/trspacer.gif" WIDTH=4 HEIGHT=1 ALT="">Document Type:
                                                                                                <struts-html:checkbox property="mediaType1" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_Word.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Word"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=15 ALT="">
												<struts-html:checkbox property="mediaType2" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_PPT.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="PowerPoint"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=15 ALT="">
												<struts-html:checkbox property="mediaType3" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_Acrobat.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Acrobat"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=15 ALT="">
												<struts-html:checkbox property="mediaType4" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_Flash.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Flash"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=15 ALT="">
												<struts-html:checkbox property="mediaType5" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_Internet.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Internet"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=15 ALT="">
												<struts-html:checkbox property="mediaType6" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_Video.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Video"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=15 ALT="">
												<struts-html:checkbox property="mediaType7" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_CD.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="CD"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=15 ALT="">
												</P>
											</TD>
										</TR>
										<TR HEIGHT=30>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><IMG SRC="images/trspacer.gif" WIDTH=4 HEIGHT=1 ALT="">Keyword(s):</P></TD>
											<TD ALIGN=left><struts-html:text property="keywordInput" style="font-size: 13px; width: 306px;" /></TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=2><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=4 ALT=""></TD>
										</TR>
										<TR>
											<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="Search" VALUE="Search"><IMG SRC="images/trspacer.gif" WIDTH=9 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=2><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=5 ALT=""></TD>
										</TR>
									</struts-html:form>
									</TABLE>
								</TD>
							</TR>
						</TABLE>
						<!-- End Library Search Area -->
					</TD>
					<TD WIDTH=20 ALIGN=left VALIGN=bottom><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
					<TD WIDTH=292 ALIGN=left VALIGN=top>
<%
System.out.println("HIYA!2!");
//if (((String)session.getAttribute("corporate")).equals("false"))
//{
%>
						<TABLE WIDTH=292 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#339999">
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG5.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;My Documents</P></TD>
							</TR>
							<TR>
								<TD>
									<TABLE WIDTH=290 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#FFFFFF">
										<TR BGCOLOR="#B9DCDA">
											<TD WIDTH=15 ALIGN=left></TD>
											<TD WIDTH=275 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Document Name</P></TD>
										</TR>
									</TABLE>
									<TABLE ID="MyDocs" WIDTH=290 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#FFFFFF">
<%
UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
Iterator itr = person_obj.getResources().iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
        ResourceBean resource = (ResourceBean)itr.next();
        boolean isNew = false;
        try
        {
            Calendar broadcastDate = Calendar.getInstance();
            broadcastDate.setTime(resource.getEffectiveDate());
            broadcastDate.add(Calendar.DATE, resource.getDisplayAsNewDays());
            if (broadcastDate.after(now))
                isNew = true;
        }
        catch (Exception xx)
        {
        }
        String iconString = "";
        String iconAltString = "";
        String urlString = "resources/" + resource.getURL();
            if (resource.getType().equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Acrobat.gif";
                iconAltString = "Acrobat";
            }
            else if (resource.getType().equals(ResourceBean.CD_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_CD.gif";
                iconAltString = "CD";
                urlString = "";
            }
            else if (resource.getType().equals(ResourceBean.CLASSROOM_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Classroom.gif";
                iconAltString = "Classroom";
                urlString = "";
            }
            else if (resource.getType().equals(ResourceBean.FLASH_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Flash.gif";
                iconAltString = "Flash";
            }
            else if (resource.getType().equals(ResourceBean.INTERNET_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Internet.gif";
                iconAltString = "Internet";
                urlString = resource.getURL();;
            }
            else if (resource.getType().equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_PPT.gif";
                iconAltString = "PowerPoint";
            }
            else if (resource.getType().equals(ResourceBean.VIDEO_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Video.gif";
                iconAltString = "Video";
            }
            else if (resource.getType().equals(ResourceBean.WORD_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Word.gif";
                iconAltString = "Word";
            }
%>
										<TR>
<%
        if (iconString.equals(""))
        {
%>
											<TD WIDTH=18 ALIGN=left><tr></TD>
<%
        }
        else
        {
%>
											<TD WIDTH=18 ALIGN=left><IMG SRC="images/<%= iconString %>" WIDTH=18 HEIGHT=15 BORDER=0 ALT="<%= iconAltString %>"></TD>
<%
        }
        if (urlString.equals("") || (resource.getURL() == null))
        {
%>
											<TD WIDTH=254 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><%= resource.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></P></TD>
<%
        }
        else
        {
%>
											<TD WIDTH=254 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="#" onClick="window.open('resource.jsp?id=<%= resource.getId() %>','ResourceWin','scrollbars=yes,resizable=yes,toolbar=no,location=no,directories=no,status=no,menubar=yes,width=520,height=500');" style="color: #000000"><%= resource.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></A></P></TD>
<%
        }
%>
											<TD WIDTH=18 ALIGN=left><A HREF="library.jsp?remove=<%= resource.getId() %>"><IMG SRC="images/PttrsnUK_BTN-RemoveDoc.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Remove from My Documents"></A></TD>
										</TR>
<%
    }
}
else
{
%>
                                                                                <TR>
											<TD WIDTH=290 COLSPAN=3><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">No Documents Bookmarked</P></TD>
										</TR>
<%
}
%>
									</TABLE>
								</TD>
							</TR>
						</TABLE>
<%
//}
%>
					</TD>
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
System.out.println("HIYA!2!");
boolean resultsFound = false;
if (!resourceSearch.displayMostActive())
{
%>
			<!-- Begin Search Results Table -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#339999">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG5.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Library Search Results</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=536 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">To view a library document, click on the document name.</P></TD>
								<TD WIDTH=190 ALIGN=right><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #003366;"><A HREF="library.jsp?view=true" style="color: #003366">View Most Accessed Documents</A></P></TD>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#B9DCDA">
							<TR>
								<TD WIDTH=78 ALIGN=center><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Published</P></TD>
								<TD WIDTH=18 ALIGN=left></TD>
								<TD WIDTH=365 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Document Name</P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Category</P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Manufacturer</P></TD>
								<TD WIDTH=15 ALIGN=center></TD>
							</TR>
						</TABLE>
						<TABLE ID="mostAccessed" WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#FFFFFF">
<%
    Enumeration resources = resourceSearch.getList();
    if (resources.hasMoreElements())
    {
        while (resources.hasMoreElements())
        {
            resultsFound = true;
            ResourceBean resource = (ResourceBean)resources.nextElement();
            boolean isNew = false;
            Calendar broadcastDate = Calendar.getInstance();
            broadcastDate.setTime(resource.getEffectiveDate());
            broadcastDate.add(Calendar.DATE, resource.getDisplayAsNewDays());
            if (broadcastDate.after(now))
                isNew = true;
            String iconString = "";
            String iconAltString = "";
            String urlString = "resources/" + resource.getURL();
            if (resource.getType().equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Acrobat.gif";
                iconAltString = "Acrobat";
            }
            else if (resource.getType().equals(ResourceBean.CD_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_CD.gif";
                iconAltString = "CD";
                urlString = "";
            }
            else if (resource.getType().equals(ResourceBean.CLASSROOM_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Classroom.gif";
                iconAltString = "Classroom";
                urlString = "";
            }
            else if (resource.getType().equals(ResourceBean.FLASH_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Flash.gif";
                iconAltString = "Flash";
            }
            else if (resource.getType().equals(ResourceBean.INTERNET_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Internet.gif";
                iconAltString = "Internet";
                urlString = resource.getURL();;
            }
            else if (resource.getType().equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_PPT.gif";
                iconAltString = "PowerPoint";
            }
            else if (resource.getType().equals(ResourceBean.VIDEO_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Video.gif";
                iconAltString = "Video";
            }
            else if (resource.getType().equals(ResourceBean.WORD_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Word.gif";
                iconAltString = "Word";
            }
%>
							<!-- Begin One Entry -->
							<TR>
								<TD WIDTH=76 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= resource.getReleasedDateString() %></P></TD>
<%
            if (iconString.equals(""))
            {
%>
								<TD WIDTH=19 ALIGN=left><br></TD>
<%
            }
            else
            {
%>
								<TD WIDTH=19 ALIGN=left><IMG SRC="images/<%= iconString %>" WIDTH=18 HEIGHT=15 BORDER=0 ALT="<%= iconAltString %>"></TD>
<%
            }
            if (urlString.equals("") || (resource.getURL() == null))
            {
%>
								<TD WIDTH=365 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><%= resource.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></P></TD>
<%
            }
            else
            {
%>
								<TD WIDTH=365 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="#" onClick="window.open('resource.jsp?id=<%= resource.getId() %>','ResourceWin','scrollbars=yes,resizable=yes,toolbar=no,location=no,directories=no,status=no,menubar=yes,width=520,height=500');" style="color: #000000"><%= resource.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></A></P></TD>
<%
            }
%>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= resource.getCategoryNameString() %></P></TD>
								<TD WIDTH=128 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">OLD_MAN</P></TD>
								<TD WIDTH=15 ALIGN=center><A HREF="library.jsp?save=<%= resource.getId() %>"><IMG SRC="images/PttrsnUK_BTN-AddDoc.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Save to My Documents"></A></TD>
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
								<TD COLSPAN=6><P style="font-size: 9pt; font-family: Arial; color: #000000;">No Resources Found</P></TD>
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
else
{
    if (!resourceSearch.searchPerformed())
    {
        try
        {
            resourceSearch.setDisplayOnlyActive(true);
            if (applyAudience)
            {
                UKOnlinePersonBean person_objx = (UKOnlinePersonBean)loginBean.getPerson();
                try
                {
                    resourceSearch.setUserGroup(person_objx.getGroup());
                }
                catch (Exception x)
                {
                    x.printStackTrace();
                }
                try
                {
                    //resourceSearch.setJobTitle(person_objx.getJobTitleString());
                }
                catch (Exception x)
                {
                    x.printStackTrace();
                }
                resourceSearch.setDepartment(person_objx.getDepartment());
            }
            resourceSearch.search();
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
    }
%>
			<!-- Begin Most Accessed Documents Table -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#339999">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG5.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Most Accessed Documents</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=726 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">To view a library document, click on the document name.</P></TD>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#B9DCDA">
							<TR>
								<TD WIDTH=78 ALIGN=center><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Published</P></TD>
								<TD WIDTH=18 ALIGN=left></TD>
								<TD WIDTH=365 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Document Name</P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Category</P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Manufacturer</P></TD>
								<TD WIDTH=15 ALIGN=center></TD>
							</TR>
						</TABLE>
						<TABLE ID="mostAccessed" WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#FFFFFF">
<%
    Enumeration resources = resourceSearch.getList();
    if (resources.hasMoreElements())
    {
        while (resources.hasMoreElements())
        {
            resultsFound = true;
            ResourceBean resource = (ResourceBean)resources.nextElement();
            boolean isNew = false;
            Calendar broadcastDate = Calendar.getInstance();
            broadcastDate.setTime(resource.getEffectiveDate());
            broadcastDate.add(Calendar.DATE, resource.getDisplayAsNewDays());
            if (broadcastDate.after(now))
                isNew = true;
            String iconString = "";
            String iconAltString = "";
            String urlString = "resources/" + resource.getURL();
            if (resource.getType().equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Acrobat.gif";
                iconAltString = "Acrobat";
            }
            else if (resource.getType().equals(ResourceBean.CD_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_CD.gif";
                iconAltString = "CD";
                urlString = "";
            }
            else if (resource.getType().equals(ResourceBean.CLASSROOM_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Classroom.gif";
                iconAltString = "Classroom";
                urlString = "";
            }
            else if (resource.getType().equals(ResourceBean.FLASH_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Flash.gif";
                iconAltString = "Flash";
            }
            else if (resource.getType().equals(ResourceBean.INTERNET_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Internet.gif";
                iconAltString = "Internet";
                urlString = resource.getURL();;
            }
            else if (resource.getType().equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_PPT.gif";
                iconAltString = "PowerPoint";
            }
            else if (resource.getType().equals(ResourceBean.VIDEO_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Video.gif";
                iconAltString = "Video";
            }
            else if (resource.getType().equals(ResourceBean.WORD_RESOURCE_TYPE))
            {
                iconString = "PttrsnUK_ICON_Word.gif";
                iconAltString = "Word";
            }
%>
							<!-- Begin One Entry -->
							<TR>
								<TD WIDTH=76 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= resource.getReleasedDateString() %></P></TD>
<%
            if (iconString.equals(""))
            {
%>
								<TD WIDTH=19 ALIGN=left><br></TD>
<%
            }
            else
            {
%>
								<TD WIDTH=19 ALIGN=left><IMG SRC="images/<%= iconString %>" WIDTH=18 HEIGHT=15 BORDER=0 ALT="<%= iconAltString %>"></TD>
<%
            }
            if (urlString.equals("") || (resource.getURL() == null))
            {
%>
								<TD WIDTH=365 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><%= resource.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></P></TD>
<%
            }
            else
            {
%>
								<TD WIDTH=365 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="#" onClick="window.open('resource.jsp?id=<%= resource.getId() %>','ResourceWin','scrollbars=yes,resizable=yes,toolbar=no,location=no,directories=no,status=no,menubar=yes,width=520,height=500');" style="color: #000000"><%= resource.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></A></P></TD>
<%
            }
%>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= resource.getCategoryNameString() %></P></TD>
								<TD WIDTH=128 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">OLD_MAN</P></TD>
								<TD WIDTH=15 ALIGN=center><A HREF="library.jsp?save=<%= resource.getId() %>"><IMG SRC="images/PttrsnUK_BTN-AddDoc.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Save to My Documents"></A></TD>
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
								<TD COLSPAN=6><P style="font-size: 9pt; font-family: Arial; color: #000000;">No Resources Found</P></TD>
							</TR>
							<!-- End One Entry -->
<%
    }
%>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Most Accessed Documents Table -->
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
					<TD ALIGN=right><P style="font-size: 9pt; font-family: Arial; color: #000000;">Displaying <%= resourceSearch.getCurrentPageMinimum() %>-<%= resourceSearch.getCurrentPageMaximum() %> of (<%= resourceSearch.getNumberOfResults() %>)</P></TD>
					<TD ALIGN=right>
<%
    if (resourceSearch.hasPreviousPage())
    {
%>
                                            <A HREF="library.jsp?page=previous"><IMG SRC="images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous"></A>
<%
    }
    else
    {
%>
                                            <IMG SRC="images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous">
<%
    }
    if (resourceSearch.hasNextPage())
    {
%>
                                            <A HREF="library.jsp?page=next"><IMG SRC="images/PttrsnUK_BTN-Down.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Next"></A>
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