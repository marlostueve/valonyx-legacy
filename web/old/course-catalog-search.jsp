<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.beans.CourseSearchBean" scope="session"/>
<jsp:useBean id="certSearch" class="com.badiyan.uk.beans.CertificationSearchBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
listHelper.setCompany(adminCompany);

Calendar now = Calendar.getInstance();

// I need to determine whether or not this is a corporate user

boolean applyAudience = true;
try
{
    if (((String)session.getAttribute("corporate")).equals("true"))
        applyAudience = false;
}
catch (Exception x)
{
    x.printStackTrace();
}

if (request.getParameter("page") != null)
{
    if (request.getParameter("page").equals("next"))
        courseSearch.nextPage();
    else if (request.getParameter("page").equals("previous"))
        courseSearch.previousPage();
}
if (request.getParameter("man") != null)
{
    try
    {
        courseSearch.clearSearchCriteria();
        certSearch.clearSearchCriteria();
        if (applyAudience)
        {
            UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
            courseSearch.setUserGroup(person_obj.getGroup());
            courseSearch.setJobTitle(person_obj.getJobTitle());
            courseSearch.setDepartment(person_obj.getDepartment());
            certSearch.setUserGroup(person_obj.getGroup());
            certSearch.setJobTitle(person_obj.getJobTitle());
            certSearch.setDepartment(person_obj.getDepartment());
        }

        /*
        ManufacturerBean manufacturer = ManufacturerBean.getManufacturer(request.getParameter("man"));
        courseSearch.setManufacturer(manufacturer);
        certSearch.setManufacturer(manufacturer);
        */

        courseSearch.search();
        certSearch.search();

        courseSearch.addSearchResults(certSearch);
    }
    catch (Exception x)
    {
        x.printStackTrace();
    }
}
if (request.getParameter("cat") != null)
{
    try
    {
        courseSearch.clearSearchCriteria();
        certSearch.clearSearchCriteria();
        if (applyAudience)
        {
            UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
            courseSearch.setUserGroup(person_obj.getGroup());
            courseSearch.setJobTitle(person_obj.getJobTitle());
            courseSearch.setDepartment(person_obj.getDepartment());
            certSearch.setUserGroup(person_obj.getGroup());
            certSearch.setJobTitle(person_obj.getJobTitle());
            certSearch.setDepartment(person_obj.getDepartment());
        }
        CategoryBean category = CategoryBean.getCategory(Integer.parseInt(request.getParameter("cat")));
        courseSearch.setCategory(category);
        certSearch.setCategory(category);
        courseSearch.search();
        certSearch.search();
        courseSearch.addSearchResults(certSearch);
    }
    catch (Exception x)
    {
        x.printStackTrace();
    }
}
if (request.getParameter("cert") != null)
{
    try
    {
        courseSearch.clearSearchCriteria();
        certSearch.clearSearchCriteria();
        if (applyAudience)
        {
            UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
            courseSearch.setUserGroup(person_obj.getGroup());
            courseSearch.setJobTitle(person_obj.getJobTitle());
            courseSearch.setDepartment(person_obj.getDepartment());
            certSearch.setUserGroup(person_obj.getGroup());
            certSearch.setJobTitle(person_obj.getJobTitle());
            certSearch.setDepartment(person_obj.getDepartment());
        }
        CertificationBean cert = CertificationBean.getCertification(Integer.parseInt(request.getParameter("cert")));
        certSearch.setCertification(cert);
        certSearch.search();
        courseSearch.addSearchResults(certSearch);
    }
    catch (Exception x)
    {
    }
}


%>

<struts-html:html locale="true">
<head>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<struts-html:javascript formName="courseCatalogForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src="staticJavascript.jsp"></script>
<%@ include file="channels\channel-head-javascript.jsp" %>
<SCRIPT TYPE="text/javascript">
<!--

function
initForm()
{
    //document.courseCatalogSearchForm.titleInput.value = '<%= courseSearch.getKeywordString() %>';
    //document.courseCatalogSearchForm.manufacturerSelect.value = 'OLD_MAN';
    //document.courseCatalogSearchForm.categorySelect.value = '<%= courseSearch.getCategoryNameString() %>';
    //document.courseCatalogSearchForm.releasedDateInput.value = '<%= courseSearch.getReleasedDateString() %>';
    //document.courseCatalogSearchForm.releasedDateSelect[<%= courseSearch.getReleasedDateCompareType() - 1 %>].checked = true;
    //document.courseCatalogSearchForm.completeByDateInput.value = '<%= courseSearch.getExpirationDateString() %>';
    //document.courseCatalogSearchForm.completeByDateSelect[<%= courseSearch.getExpirationDateCompareType() - 1 %>].checked = true;
    //document.courseCatalogSearchForm.titleSearchBox.value = '<%= courseSearch.getDescriptionKeywordString() %>';

    document.courseCatalogSearchForm.releasedDateSelect[2].checked = true;
    document.courseCatalogSearchForm.completeByDateSelect[2].checked = true;
    document.courseCatalogSearchForm.mediaType1.checked = false;
    document.courseCatalogSearchForm.mediaType2.checked = false;
    document.courseCatalogSearchForm.mediaType3.checked = false;
    document.courseCatalogSearchForm.mediaType4.checked = false;
    document.courseCatalogSearchForm.mediaType5.checked = false;
    document.courseCatalogSearchForm.mediaType6.checked = false;
    document.courseCatalogSearchForm.mediaType7.checked = false;
}

// -->
</SCRIPT>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages(); setTableBG('searchResultsCourses'); initForm(); ">
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
			<!-- Begin Advanced Search -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR>
					<TD WIDTH=522 ALIGN=left VALIGN=top>
						<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Advanced Search [Course Catalog]</P></TD>
							</TR>
							<TR>
								<TD>
									<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                                        <struts-html:form action="/courseCatalogSearch" focus="titleInput">
										<TR>
											<TD WIDTH=95 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=345 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=80 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR HEIGHT=30>
											<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Title:</P></TD>
											<TD COLSPAN=2 ALIGN=left>
                                                                                            <struts-html:text property="titleInput" style="font-size: 13px; width: 420px;" />
											</TD>
										</TR>
										<TR HEIGHT=30>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Category:</P></TD>
											<TD COLSPAN=2 ALIGN=left>
                                                                                            <struts-html:select property="categorySelect" style="font-size: 13px; width: 420px;" >
                                                                                                <struts-html:option value="0">-- ALL CATEGORIES --</struts-html:option>
                                                                                                <struts-html:optionsCollection name="listHelper" property="categories" />
                                                                                            </struts-html:select>
											</TD>
										</TR>
										<TR HEIGHT=30>
											<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Released:</P></TD>
											<TD COLSPAN=2>
												<P style="font-size: 10pt; font-family: Arial; color: #000000;"><struts-html:text property="releasedDateInput" style="font-size: 13px; width: 80px;" maxlength="10" onfocus="getDate('courseCatalogSearchForm','releasedDateInput');" /><IMG SRC="images/trspacer.gif" WIDTH=8 HEIGHT=1 ALT=""><struts-html:radio property="releasedDateSelect" value="1" />Before<IMG SRC="images/trspacer.gif" WIDTH=5 HEIGHT=1 ALT=""><struts-html:radio property="releasedDateSelect" value="2" />After<IMG SRC="images/trspacer.gif" WIDTH=5 HEIGHT=1 ALT=""><struts-html:radio property="releasedDateSelect" value="3" />Search All Dates</P>
											</TD>
										</TR>
										<TR HEIGHT=30>
											<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Complete By:</P></TD>
											<TD COLSPAN=2>
												<P style="font-size: 10pt; font-family: Arial; color: #000000;"><struts-html:text property="completeByDateInput" style="font-size: 13px; width: 80px;" maxlength="10" onfocus="getDate('courseCatalogSearchForm','completeByDateInput');" /><IMG SRC="images/trspacer.gif" WIDTH=8 HEIGHT=1 ALT=""><struts-html:radio property="completeByDateSelect" value="1" />Before<IMG SRC="images/trspacer.gif" WIDTH=5 HEIGHT=1 ALT=""><struts-html:radio property="completeByDateSelect" value="2" />After<IMG SRC="images/trspacer.gif" WIDTH=5 HEIGHT=1 ALT=""><struts-html:radio property="completeByDateSelect" value="3" />Search All Dates</P>
											</TD>
										</TR>
									</TABLE>
									<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
										<TR HEIGHT=28>
											<TD WIDTH=86 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Type: </P></TD>
											<TD WIDTH=434 ALIGN=left>
                                                                                            <struts-html:checkbox property="mediaType1" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_Word.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Word"><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=15 ALT="">
											    <struts-html:checkbox property="mediaType2" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_PPT.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="PowerPoint"><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=15 ALT="">
                                                                                            <struts-html:checkbox property="mediaType3" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_Acrobat.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Acrobat"><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=15 ALT="">
                                                                                            <struts-html:checkbox property="mediaType4" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_Flash.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Flash"><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=15 ALT="">
											    <struts-html:checkbox property="mediaType5" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_Internet.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Internet"><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=15 ALT="">
											    <struts-html:checkbox property="mediaType6" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_Video.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Video"><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=15 ALT="">
                                                                                            <struts-html:checkbox property="mediaType7" value="true" />
												<IMG SRC="images/PttrsnUK_ICON_CD.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="CD"><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=15 ALT="">
											</TD>
										</TR>
									</TABLE>
									<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
										<TR HEIGHT=30>
											<TD WIDTH=94 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Keyword:</P></TD>
											<TD WIDTH=346 ALIGN=left><struts-html:text property="titleSearchBox" style="font-size: 13px; width: 340px;" /></TD>
											<TD WIDTH=80 ALIGN=right><INPUT TYPE="submit" NAME="Search" VALUE="Search"><IMG SRC="../images/trspacer.gif" WIDTH=7 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR HEIGHT=3>
											<TD COLSPAN=3 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
									</struts-html:form>
									</TABLE>
								</TD>
							</TR>
						</TABLE>
					</TD>
					<TD WIDTH=210 ALIGN=right VALIGN=top><IMG SRC="images/PttrsnUK_SearchPicture.jpg" WIDTH=190 HEIGHT=184 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- End Advanced Search -->
			<!-- Begin 25 Pixel Spacer -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=0 CELLSPACING=0>
				<TR>
					<TD><IMG SRC="images/trspacer.gif" WIDTH=732 HEIGHT=25 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- End 25 Pixel Spacer -->

			<!-- Begin Search Results [Courses] Table -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#CCA34A">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Search Results</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=516 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">To view course details, click on the course title.</P></TD>
								<TD WIDTH=210 ALIGN=right><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#E7D9BA">
							<TR>
								<TD WIDTH=70 ALIGN=center><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Released</P></TD>
								<TD WIDTH=78 ALIGN=center><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Complete By</P></TD>
								<TD WIDTH=19 ALIGN=left></TD>
								<TD WIDTH=279 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Course Title</P></TD>
								<TD WIDTH=126 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Category</P></TD>
								<TD WIDTH=128 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Manufacturer</P></TD>
								<TD WIDTH=30 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;</P></TD>
							</TR>
						</TABLE>
						<TABLE ID="searchResultsCourses" WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#FFFFFF">
<%
boolean resultsFound = false;
Enumeration courses = courseSearch.getList();
while (courses.hasMoreElements())
{
    resultsFound = true;
    Object obj = courses.nextElement();
    boolean isNew = false;
    if (obj instanceof CourseBean)
    {
        CourseBean course = (CourseBean)obj;
        try
        {
            Calendar broadcastDate = course.getBroadcastDate();
            broadcastDate.add(Calendar.DATE, course.getDisplayAsNewFor());
            if (broadcastDate.after(now))
                isNew = true;
        }
        catch (Exception x)
        {
        }
        String title = course.getNameString();
        String categoryString = course.getCategoryNameString();
        //String manufacturerString = course.getManufacturerNameString();
        String iconString = "";
        String iconAltString = "";
        if (course.getResourceType().equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
        {
            iconString = "PttrsnUK_ICON_Acrobat.gif";
            iconAltString = "Acrobat";
        }
        else if (course.getResourceType().equals(ResourceBean.CD_RESOURCE_TYPE))
        {
            iconString = "PttrsnUK_ICON_CD.gif";
            iconAltString = "CD";
        }
        else if (course.getResourceType().equals(ResourceBean.CLASSROOM_RESOURCE_TYPE))
        {
            iconString = "PttrsnUK_ICON_Classroom.gif";
            iconAltString = "Classroom";
        }
        else if (course.getResourceType().equals(ResourceBean.FLASH_RESOURCE_TYPE))
        {
            iconString = "PttrsnUK_ICON_Flash.gif";
            iconAltString = "Flash";
        }
        else if (course.getResourceType().equals(ResourceBean.INTERNET_RESOURCE_TYPE))
        {
            iconString = "PttrsnUK_ICON_Internet.gif";
            iconAltString = "Internet";
        }
        else if (course.getResourceType().equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
        {
            iconString = "PttrsnUK_ICON_PPT.gif";
            iconAltString = "PowerPoint";
        }
        else if (course.getResourceType().equals(ResourceBean.VIDEO_RESOURCE_TYPE))
        {
            iconString = "PttrsnUK_ICON_Video.gif";
            iconAltString = "Video";
        }
        else if (course.getResourceType().equals(ResourceBean.WORD_RESOURCE_TYPE))
        {
            iconString = "PttrsnUK_ICON_Word.gif";
            iconAltString = "Word";
        }
%>
                                                        <!-- Begin One Entry -->
							<TR>
								<TD WIDTH=70 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= course.getReleasedDateString() %></P></TD>
								<TD WIDTH=78 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= course.getDueDateString(loginBean.getPerson()) %></P></TD>
<%
        if (iconString.equals(""))
        {
%>
								<TD WIDTH=18 ALIGN=left><br></TD>
<%
        }
        else
        {
%>
								<TD WIDTH=18 ALIGN=left><IMG SRC="images/<%= iconString %>" WIDTH=18 HEIGHT=15 BORDER=0 ALT="<%= iconAltString %>"></TD>
<%
        }
%>
								<TD WIDTH=278 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="course-detail.jsp?id=<%= course.getId() %>" style="color: #000000"><%= title %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></A></P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= categoryString %></P></TD>
								<TD WIDTH=129 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">OLD_MAN</P></TD>
								<TD WIDTH=30 ALIGN=left><br></TD>
							</TR>
							<!-- End One Entry -->
<%
    }
    else if (obj instanceof CertificationBean)
    {
        CertificationBean certification = (CertificationBean)obj;
        try
        {
            Calendar broadcastDate = certification.getBroadcastDate();
            broadcastDate.add(Calendar.DATE, certification.getDisplayAsNewFor());
            if (broadcastDate.after(now))
                isNew = true;
        }
        catch (Exception x)
        {
        }
        resultsFound = true;
%>
                                                        <!-- Begin One Entry [Certification] -->
							<TR>
								<TD WIDTH=70 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #7A5800;"><%= certification.getReleasedDateString() %></P></TD>
								<TD WIDTH=78 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #7A5800;"><%= certification.getDueDateString(loginBean.getPerson()) %></P></TD>
								<TD WIDTH=18 ALIGN=left></TD>
								<TD WIDTH=278 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #7A5800;">&#8711;&nbsp;<A HREF="certification-detail.jsp?id=<%= certification.getId() %>" style="color: #7A5800"><%= certification.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></A></P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #7A5800;"><%= certification.getCategoryNameString() %></P></TD>
								<TD WIDTH=129 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #7A5800;">OLD_MAN</P></TD>
								<TD WIDTH=30 ALIGN=left></TD>
							</TR>
							<!-- End One Entry [Certification] -->
<%
    }
}
%>
<%
if (!resultsFound)
{
%>
							<!-- Begin One Entry -->
							<TR>
								<TD colspan=7><P style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Results Found</P></TD>
							</TR>
							<!-- End One Entry -->
<%
}
%>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Recommended Courses Table -->

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
					<TD ALIGN=right><P style="font-size: 9pt; font-family: Arial; color: #000000;">Displaying <%= courseSearch.getCurrentPageMinimum() %>-<%= courseSearch.getCurrentPageMaximum() %> of (<%= courseSearch.getNumberOfResults() %>)</P></TD>
					<TD ALIGN=right>
<%
    if (courseSearch.hasPreviousPage())
    {
%>
                                            <A HREF="course-catalog-search.jsp?page=previous"><IMG SRC="images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous"></A>
<%
    }
    else
    {
%>
                                            <IMG SRC="images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous">
<%
    }
    if (courseSearch.hasNextPage())
    {
%>
                                            <A HREF="course-catalog-search.jsp?page=next"><IMG SRC="images/PttrsnUK_BTN-Down.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Next"></A>
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
			<!-- End Search Results [Courses] Table -->
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