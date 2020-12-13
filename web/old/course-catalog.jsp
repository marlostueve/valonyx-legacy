<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="selectedDepartment" class="com.badiyan.uk.beans.DepartmentBean" scope="session" />
<jsp:useBean id="selectedGroup" class="com.badiyan.uk.beans.PersonGroupBean" scope="session" />
<jsp:useBean id="selectedTitle" class="com.badiyan.uk.beans.PersonTitleBean" scope="session" />

<jsp:useBean id="recommendedList" class="com.badiyan.uk.beans.ListerBean" scope="session"/>
<jsp:useBean id="allList" class="com.badiyan.uk.beans.ListerBean" scope="session"/>
<jsp:useBean id="list" class="com.badiyan.uk.beans.ListerBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
listHelper.setCompany(adminCompany);
//resourceSearch.setCompany(adminCompany);

boolean all = false;
if (request.getParameter("all") != null)
{
    if (request.getParameter("all").equals("true"))
    {
        all = true;
    }
    //else if (request.getParameter("all").equals("false"))
    //   list.invalidateSearchResults();
}

if (request.getParameter("page") != null)
{
    /*
    if (request.getParameter("page").equals("next"))
        list.nextPage();
    else if (request.getParameter("page").equals("previous"))
        list.previousPage();
        */
    if (request.getParameter("page").equals("next"))
    {
        if (all)
            allList.nextPage();
        else
            recommendedList.nextPage();
    }
    else if (request.getParameter("page").equals("previous"))
    {
        if (all)
            allList.previousPage();
        else
            recommendedList.previousPage();
    }
}
else
{
    allList.invalidateSearchResults();
    recommendedList.invalidateSearchResults();
}

Calendar now = Calendar.getInstance();

boolean applyAudience = true;
session.removeAttribute("groupSearch");

String sortBy = CoursePeer.RELEASEDDATE;
if (request.getParameter("sort") != null)
{
    /*
    if (request.getParameter("sort").equals("0"))
        sortBy = CoursePeer.RELEASEDDATE;
    else if (request.getParameter("sort").equals("1"))
        sortBy = AudiencePeer.COMPLETEBYDATE;
    else if (request.getParameter("sort").equals("2"))
        sortBy = CoursePeer.COURSENAME;
    else if (request.getParameter("sort").equals("3"))
        sortBy = CoursePeer.CATEGORYNAME;
    else if (request.getParameter("sort").equals("4"))
        sortBy = CoursePeer.MANUFACTURERNAME;
     */

}

int sortVal = 0;
try
{
    sortVal = Integer.parseInt(request.getParameter("sort"));
}
catch (Exception x)
{
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
<script TYPE="text/javascript">
<!--

function
processSort()
{
    location='course-catalog.jsp?all=<%= all %>&sort=' + document.sortForm.sort4.selectedIndex;
}

function
initForm()
{

}

// -->
</script>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages(); setTableBG('recommendedCourses'); initForm();">
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
		<TD WIDTH=732 ALIGN=left VALIGN=top>
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR>
<%
PersonGroupBean corporateGroup = (PersonGroupBean)session.getAttribute("groupSearch");

Vector categories = null;
/*
try
{
    UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
    PersonGroupBean group = person.getGroup();
    categories = CategoryBean.getCategories(group, PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
}
catch (Exception x)
{
    x.printStackTrace();
}
 */
if (categories == null)
    categories = CategoryBean.getCategories(adminCompany);




Vector certifications = null;
try
{
    if (applyAudience)
    {
        UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
        certifications = CertificationBean.getCertifications(person);
    }
    else
    {
        if (corporateGroup == null)
            certifications = CertificationBean.getCertifications();
        else
            certifications = CertificationBean.getCertifications(corporateGroup);
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
if (certifications == null)
    certifications = CertificationBean.getCertifications();






if (request.getParameter("qs") == null)
{
%>
					<!-- BEGIN Quick Search, Keyword Search -->
					<TD WIDTH=380 ALIGN=left VALIGN=bottom>
						<TABLE WIDTH=380 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Quick Search</P></TD>
							</TR>
							<TR>
								<TD>
									<TABLE WIDTH=378 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
										<TR>
											<TD COLSPAN=2 BGCOLOR="#EEEEEE"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR>
											<TD COLSPAN=2 BGCOLOR="#EEEEEE"><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Search by Category:</P></TD>
										</TR>
										<TR>
											<TD COLSPAN=2 BGCOLOR="#DDDDDD"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR>
											<TD WIDTH=1><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=64 ALT=""></TD>
											<TD WIDTH=377 ALIGN=left>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
<%
    boolean showNext = false;
    Iterator catItr = categories.iterator();
    for (int i = 0; catItr.hasNext() && i < 15; i++)
    {
        CategoryBean category = (CategoryBean)catItr.next();
        if (!catItr.hasNext() || (i == 14))
        {
%>
												<A HREF="course-catalog-search.jsp?cat=<%= category.getValue() %>" style="color: #336699"><%= category.getNameString() %></A>&nbsp;
<%
        }
        else
        {
%>
												<A HREF="course-catalog-search.jsp?cat=<%= category.getValue() %>" style="color: #336699"><%= category.getNameString() %></A>,&nbsp;
<%
        }
    }
    if (catItr.hasNext())
        showNext = true;
    if (showNext)
    {
%>
												<A HREF="course-catalog.jsp?qs=cat" style="color: #336699">... AND MORE</A>
<%
    }
%>
												</P>
											</TD>
										</TR>
										<TR>
											<TD COLSPAN=2 BGCOLOR="#DDDDDD"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR>
											<TD COLSPAN=2 BGCOLOR="#EEEEEE"><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Search by Certification:</P></TD>
										</TR>
										<TR>
											<TD COLSPAN=2 BGCOLOR="#DDDDDD"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR>
											<TD WIDTH=1><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=18 ALT=""></TD>
											<TD WIDTH=377 ALIGN=left>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
<%
    showNext = false;
    Iterator certItr = certifications.iterator();
    for (int i = 0; certItr.hasNext() && i < 15; i++)
    {
        CertificationBean certification = (CertificationBean)certItr.next();
        if (!certItr.hasNext() || (i == 14))
        {
%>
												<A HREF="course-catalog-search.jsp?cert=<%= certification.getId() %>" style="color: #336699"><%= certification.getNameString() %></A>&nbsp;
<%
        }
        else
        {
%>
												<A HREF="course-catalog-search.jsp?cert=<%= certification.getId() %>" style="color: #336699"><%= certification.getNameString() %></A>,&nbsp;
<%
        }
    }
    if (certItr.hasNext())
        showNext = true;
    if (showNext)
    {
%>
												<A HREF="course-catalog.jsp?qs=cert" style="color: #336699">... AND MORE</A>
<%
    }
%>
												</P>
											</TD>
										</TR>
									</TABLE>
								</TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
								<TD ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=10 ALT=""></TD>
							</TR>
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Keyword Search [Course Catalog]</P></TD>
							</TR>
							<TR>
								<TD>
									<TABLE WIDTH=378 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                                            <struts-html:form action="/courseCatalog" focus="keywordInput" onsubmit="return validateCourseCatalogForm(this);">
										<TR HEIGHT=30>
											<TD ALIGN=left WIDTH=195><IMG SRC="images/trspacer.gif" WIDTH=3 HEIGHT=1 ALT=""><struts-html:text property="keywordInput" style="font-size: 13px; width: 184px;" /></TD>
											<TD ALIGN=left WIDTH=65><INPUT TYPE="submit" NAME="Search" VALUE="Search"></TD>
											<TD ALIGN=right WIDTH=118><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #003366;"><A HREF="course-catalog-search.jsp" style="color: #003366">Advanced Search</A><IMG SRC="images/trspacer.gif" WIDTH=7 HEIGHT=1 ALT=""></P></TD>
										</TR>
                                                                            </struts-html:form>
									</TABLE>
								</TD>
							</TR>
						</TABLE>
					</TD>
					<!-- End Quick Search, Keyword Search -->
					<!-- Begin Picture -->
					<TD WIDTH=352 ALIGN=left VALIGN=bottom>&nbsp;</TD>
					<!-- End Picture -->
<%
}
else if (request.getParameter("qs").equals("cat"))
{
%>
                        <TD WIDTH=380 ALIGN=left VALIGN=bottom>
                        <!-- BEGIN Quick Search -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Quick Search</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR>
								<TD COLSPAN=2 BGCOLOR="#EEEEEE"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD COLSPAN=2 BGCOLOR="#EEEEEE"><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Search by Category:</P></TD>
							</TR>
							<TR>
								<TD COLSPAN=2 BGCOLOR="#DDDDDD"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=1><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=729 ALIGN=left>
									<TABLE WIDTH=729 BORDER=0 CELLPADDING=0 CELLSPACING=0>
										<TR>
											<TD WIDTH=182 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=182 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=182 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=183 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?cat=Air+Abrasion+Unit" style="color: #336699">Air Abrasion Unit</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Alloy" style="color: #336699">Alloy</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Amalgam+Separator" style="color: #336699">Amalgam Separator</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Analgesia+Flowmeter/Manifold" style="color: #336699">Analgesia Flowmeter/Manifold</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Anesthetics+and+Needles" style="color: #336699">Anesthetics and Needles</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Autoclaves" style="color: #336699">Autoclaves</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Bonding+Agents" style="color: #336699">Bonding Agents</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Burrs" style="color: #336699">Burrs</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Cabinets" style="color: #336699">Cabinets</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Cements" style="color: #336699">Cements</A><BR>
												<A HREF="course-catalog-search.jsp?cat=CEREC" style="color: #336699">CEREC</A><BR>
												<A HREF="course-catalog-search.jsp?cat=CEREC+Blocks" style="color: #336699">CEREC Blocks</A><BR>
												<A HREF="course-catalog-search.jsp?cat=CEREC+Inlab" style="color: #336699">CEREC Inlab</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Chair" style="color: #336699">Chair</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Communication+Systems" style="color: #336699">Communication Systems</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Composites" style="color: #336699">Composites</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Compressor" style="color: #336699">Compressor</A><BR>
												</P>
											</TD>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?cat=Computer+Hardware" style="color: #336699">Computer Hardware</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Cosmetic" style="color: #336699">Cosmetic</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Crowns" style="color: #336699">Crowns</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Curing+Lights" style="color: #336699">Curing Lights</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Defibrillator" style="color: #336699">Defibrillator</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Dental+Washers" style="color: #336699">Dental Washers</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Diagnodent" style="color: #336699">Diagnodent</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Diagnostic" style="color: #336699">Diagnostic</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Diamonds" style="color: #336699">Diamonds</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Digital+Pan+Upgrade<" style="color: #336699">Digital Pan Upgrade</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Digital+Scanners+-+Phosphoric" style="color: #336699">Digital Scanners - Phosphoric</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Digital+X-Ray+Sensor" style="color: #336699">Digital X-Ray Sensor</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Digital+X-Ray+Software" style="color: #336699">Digital X-Ray Software</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Endodontics" style="color: #336699">Endodontics</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Evacuation" style="color: #336699">Evacuation</A><BR>
												<A HREF="course-catalog-search.jsp?cat=File+Cabinets" style="color: #336699">File Cabinets</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Film" style="color: #336699">Film</A><BR>
												</P>
											</TD>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?cat=Film+Processors+-+Chemical" style="color: #336699">Film Processors - Chemical</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Film+Processor+Component" style="color: #336699">Film Processor Component</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Finishing+&amp;+Polishing" style="color: #336699">Finishing & Polishing</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Gloves" style="color: #336699">Gloves</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Hand+Care+(Soaps+&amp;+Lotions)" style="color: #336699">Hand Care (Soaps & Lotions)</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Handpiece+-+Electric" style="color: #336699">Handpiece - Electric</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Handpiece+Cleaner" style="color: #336699">Handpiece Cleaner</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Handpiece+Hi-Speed+-+Air" style="color: #336699">Handpiece Hi-Speed - Air</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Handpiece+Low-Speed+-+Air" style="color: #336699">Handpiece Low-Speed - Air</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Impression+Materials" style="color: #336699">Impression Materials</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Instruments" style="color: #336699">Instruments</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Instrument+Management+Systems" style="color: #336699">Instrument Management Systems</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Masks" style="color: #336699">Masks</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Intra+Oral+Camera" style="color: #336699">Intra Oral Camera</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Laboratory" style="color: #336699">Laboratory</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Lab+Equipment" style="color: #336699">Lab Equipment</A><BR>
												</P>
											</TD>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?cat=Lasers" style="color: #336699">Lasers</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Light+Component" style="color: #336699">Light Component</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Lights" style="color: #336699">Lights</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Office+Furniture" style="color: #336699">Office Furniture</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Orthodontics" style="color: #336699">Orthodontics</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Patterson+Brand" style="color: #336699">Patterson Brand</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Preventative+Care" style="color: #336699">Preventative Care</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Scaler" style="color: #336699">Scaler</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Stools" style="color: #336699">Stools</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Surface+Disinfectant" style="color: #336699">Surface Disinfectant</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Temporary+Materials" style="color: #336699">Temporary Materials</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Tooth+Whitening" style="color: #336699">Tooth Whitening</A><BR>
												<A HREF="course-catalog-search.jsp?cat=Unit" style="color: #336699">Unit</A><BR>
												<A HREF="course-catalog-search.jsp?cat=X-Ray+Intra+Oral" style="color: #336699">X-Ray Intra Oral</A><BR>
												<A HREF="course-catalog-search.jsp?cat=X-Ray+Panoramic+-+Digital" style="color: #336699">X-Ray Panoramic - Digital</A><BR>
												<A HREF="course-catalog-search.jsp?cat=X-Ray+Panoramic+-+Film" style="color: #336699">X-Ray Panoramic - Film</A><BR>
												</P>
											</TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=4><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
										</TR>
									</TABLE>
								</TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Quick Search -->
                        </td>
<%
}
else if (request.getParameter("qs").equals("man"))
{
%>
                        <TD WIDTH=380 ALIGN=left VALIGN=bottom>
                        <!-- BEGIN Quick Search -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Quick Search</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR>
								<TD COLSPAN=2 BGCOLOR="#EEEEEE"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD COLSPAN=2 BGCOLOR="#EEEEEE"><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Search by Manufacturer:</P></TD>
							</TR>
							<TR>
								<TD COLSPAN=2 BGCOLOR="#DDDDDD"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=1><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=729 ALIGN=left>
									<TABLE WIDTH=729 BORDER=0 CELLPADDING=0 CELLSPACING=0>
										<TR>
											<TD WIDTH=182 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=182 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=182 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=183 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?man=3M+ESPE" style="color: #336699">3M ESPE</A><BR>
												<A HREF="course-catalog-search.jsp?man=Accutron" style="color: #336699">Accutron</A><BR>
												<A HREF="course-catalog-search.jsp?man=Adec" style="color: #336699">Adec</A><BR>
												<A HREF="course-catalog-search.jsp?man=Air+Tech" style="color: #336699">Air Tech</A><BR>
												<A HREF="course-catalog-search.jsp?man=Air+Techniques" style="color: #336699">Air Techniques</A><BR>
												<A HREF="course-catalog-search.jsp?man=Ansell+Perry" style="color: #336699">Ansell Perry</A><BR>
												<A HREF="course-catalog-search.jsp?man=Apollo" style="color: #336699">Apollo</A><BR>
												<A HREF="course-catalog-search.jsp?man=Allegiance+Healthcare" style="color: #336699">Allegiance Healthcare</A><BR>
												<A HREF="course-catalog-search.jsp?man=Axis" style="color: #336699">Axis</A><BR>
												<A HREF="course-catalog-search.jsp?man=Barnstead+Thermolyne" style="color: #336699">Barnstead Thermolyne</A><BR>
												<A HREF="course-catalog-search.jsp?man=Biomedical+Proma" style="color: #336699">Biomedical Proma</A><BR>
												<A HREF="course-catalog-search.jsp?man=Biotec/Proma" style="color: #336699">Biotec/Proma</A><BR>
												<A HREF="course-catalog-search.jsp?man=Caulk" style="color: #336699">Caulk</A><BR>
												</P>
											</TD>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?man=DentalEZ" style="color: #336699">DentalEZ</A><BR>
												<A HREF="course-catalog-search.jsp?man=Dentsply" style="color: #336699">Dentsply</A><BR>
												<A HREF="course-catalog-search.jsp?man=EMS" style="color: #336699">EMS</A><BR>
												<A HREF="course-catalog-search.jsp?man=Ergotron" style="color: #336699">Ergotron</A><BR>
												<A HREF="course-catalog-search.jsp?man=G.C.+America" style="color: #336699">G.C. America</A><BR>
												<A HREF="course-catalog-search.jsp?man=Gendex" style="color: #336699">Gendex</A><BR>
												<A HREF="course-catalog-search.jsp?man=Hufriedy" style="color: #336699">Hufriedy</A><BR>
												<A HREF="course-catalog-search.jsp?man=Instrumentarium" style="color: #336699">Instrumentarium</A><BR>
												<A HREF="course-catalog-search.jsp?man=Johnson+and+Johnson+Medical" style="color: #336699">Johnson and Johnson Medical</A><BR>
												<A HREF="course-catalog-search.jsp?man=Kimberly+Clark" style="color: #336699">Kimberly Clark</A><BR>
												<A HREF="course-catalog-search.jsp?man=KaVo" style="color: #336699">KaVo</A><BR>
												<A HREF="course-catalog-search.jsp?man=Kerr" style="color: #336699">Kerr</A><BR>
												<A HREF="course-catalog-search.jsp?man=MDS+Matrx" style="color: #336699">MDS Matrx</A><BR>
												</P>
											</TD>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?man=Midmark" style="color: #336699">Midmark</A><BR>
												<A HREF="course-catalog-search.jsp?man=Midwest" style="color: #336699">Midwest</A><BR>
												<A HREF="course-catalog-search.jsp?man=Miele" style="color: #336699">Miele</A><BR>
												<A HREF="course-catalog-search.jsp?man=Oral+B" style="color: #336699">Oral B</A><BR>
												<A HREF="course-catalog-search.jsp?man=P&amp;G" style="color: #336699">P&G</A><BR>
												<A HREF="course-catalog-search.jsp?man=Patterson+Dental+Supply" style="color: #336699">Patterson Dental Supply</A><BR>
												<A HREF="course-catalog-search.jsp?man=Premier" style="color: #336699">Premier</A><BR>
												<A HREF="course-catalog-search.jsp?man=Philips" style="color: #336699">Philips</A><BR>
												<A HREF="course-catalog-search.jsp?man=Planmeca" style="color: #336699">Planmeca</A><BR>
												<A HREF="course-catalog-search.jsp?man=Porter" style="color: #336699">Porter</A><BR>
												<A HREF="course-catalog-search.jsp?man=Progeny" style="color: #336699">Progeny</A><BR>
												<A HREF="course-catalog-search.jsp?man=Proma" style="color: #336699">Proma</A><BR>
												<A HREF="course-catalog-search.jsp?man=Ramvac" style="color: #336699">Ramvac</A><BR>
												</P>
											</TD>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?man=REBEC" style="color: #336699">REBEC</A><BR>
												<A HREF="course-catalog-search.jsp?man=Royal" style="color: #336699">Royal</A><BR>
												<A HREF="course-catalog-search.jsp?man=Schick" style="color: #336699">Schick</A><BR>
												<A HREF="course-catalog-search.jsp?man=SciCan+Sempermed" style="color: #336699">SciCan Sempermed</A><BR>
												<A HREF="course-catalog-search.jsp?man=Septodont" style="color: #336699">Septodont</A><BR>
												<A HREF="course-catalog-search.jsp?man=Sirona" style="color: #336699">Sirona</A><BR>
												<A HREF="course-catalog-search.jsp?man=Soredex" style="color: #336699">Soredex</A><BR>
												<A HREF="course-catalog-search.jsp?man=Star" style="color: #336699">Star</A><BR>
												<A HREF="course-catalog-search.jsp?man=Vident" style="color: #336699">Vident</A><BR>
												<A HREF="course-catalog-search.jsp?man=Zila" style="color: #336699">Zila</A><BR>
												</P>
											</TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=4><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
										</TR>
									</TABLE>
								</TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Quick Search -->
                        </td>
<%
}
else if (request.getParameter("qs").equals("cert"))
{
%>
                        <TD WIDTH=380 ALIGN=left VALIGN=bottom>
                        <!-- BEGIN Quick Search -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Quick Search</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR>
								<TD COLSPAN=2 BGCOLOR="#EEEEEE"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD COLSPAN=2 BGCOLOR="#EEEEEE"><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Search by Manufacturer:</P></TD>
							</TR>
							<TR>
								<TD COLSPAN=2 BGCOLOR="#DDDDDD"><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=1><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=729 ALIGN=left>
									<TABLE WIDTH=729 BORDER=0 CELLPADDING=0 CELLSPACING=0>
										<TR>
											<TD WIDTH=182 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=182 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=182 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=183 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<TR>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">3M ESPE</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Accutron</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Adec</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Air Tech</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Air Techniques</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Ansell Perry</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Apollo</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Allegiance Healthcare</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Axis</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Barnstead Thermolyne</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Biomedical Proma</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Biotec/Proma</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Caulk</A><BR>
												</P>
											</TD>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">DentalEZ</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Dentsply</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">EMS</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Ergotron</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">G.C. America</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Gendex</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Hufriedy</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Instrumentarium</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Johnson and Johnson Medical</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Kimberly Clark</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">KaVo</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Kerr</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">MDS Matrx</A><BR>
												</P>
											</TD>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Midmark</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Midwest</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Miele</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Oral B</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">P&G</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Patterson Dental Supply</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Premier</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Philips</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Planmeca</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Porter</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Progeny</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Proma</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Ramvac</A><BR>
												</P>
											</TD>
											<TD ALIGN=left VALIGN=top>
												<P style="font-size: 8pt; font-family: Arial; font-weight: bold; color: #336699;">
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">REBEC</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Royal</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Schick</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">SciCan Sempermed</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Septodont</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Sirona</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Soredex</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Star</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Vident</A><BR>
												<A HREF="course-catalog-search.jsp?man=Film" style="color: #336699">Zila</A><BR>
												</P>
											</TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=4><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
										</TR>
									</TABLE>
								</TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Quick Search -->
                        </td>
<%
}
%>
				</TR>
			</TABLE>
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#CCA34A">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Sort By -->
			<A NAME="Available">
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=0 CELLSPACING=0>
                            <form name="sortForm">
				<TR>
					<TD ALIGN=right VALIGN=center WIDTH=595><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Sort by&nbsp;&nbsp;</P></TD>
					<TD ALIGN=right VALIGN=center WIDTH=137>
						<SELECT NAME="sort4" SIZE="1" onChange="processSort();">
						<OPTION VALUE="0">Date Released</OPTION>
						<OPTION VALUE="1">Complete By Date</OPTION>
						<OPTION VALUE="2">Course Title</OPTION>
						<OPTION VALUE="3">Category</OPTION>
						<OPTION VALUE="4">Manufacturer&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</OPTION>
						</SELECT>
					</TD>
				</TR>
                            </form>
			</TABLE>
			<!-- End Sort By -->

                        <!-- Begin Recommended Courses Table -->
			<TABLE WIDTH=732 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#CCA34A">

<%
if (all)
{
%>
                            <TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;All Available Courses</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=516 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">To view course details, click on the course title.</P></TD>
								<TD WIDTH=210 ALIGN=right><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #003366;"><A HREF="course-catalog.jsp?all=false&sort=<%= sortVal %>" style="color: #003366">View Recommended Courses</A></P></TD>
<%
}
else
{
%>

                            <TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="images/PttrsnUK_TableHeaderBG.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Recommended Courses</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=730 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD WIDTH=2 ALIGN=left><IMG SRC="images/trspacer.gif" WIDTH=2 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=516 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">To view course details, click on the course title.</P></TD>
								<TD WIDTH=210 ALIGN=right><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #003366;"><A HREF="course-catalog.jsp?all=true&sort=<%= sortVal %>" style="color: #003366">View All Available Courses</A></P></TD>
<%
}
%>
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
						<TABLE ID="recommendedCourses" WIDTH=730 BORDER=0 CELLPADDING=2 CELLSPACING=0 BGCOLOR="#FFFFFF">
<%
try
{
    boolean resultsFound = false;
    UKOnlinePersonBean patPerson = (UKOnlinePersonBean)loginBean.getPerson();
    Enumeration objects = null;
    if (all)
    {
        if (!allList.searchPerformed())
        {
            if (corporateGroup == null)
                allList.setList(patPerson.getRecommendedCourseCertifications(sortBy, all, applyAudience));
            else
                allList.setList(patPerson.getRecommendedCourseCertifications(sortBy, all, corporateGroup));
        }
        objects = allList.getList();
        list = allList;
    }
    else
    {
        if (!recommendedList.searchPerformed())
        {
            if (corporateGroup == null)
                recommendedList.setList(patPerson.getRecommendedCourseCertifications(sortBy, all, applyAudience));
            else
                recommendedList.setList(patPerson.getRecommendedCourseCertifications(sortBy, all, corporateGroup));
        }
        objects = recommendedList.getList();
        list = recommendedList;
    }
    if (objects.hasMoreElements())
    {
        while (objects.hasMoreElements())
        {
            Object obj = objects.nextElement();
            boolean isNew = false;
            if (obj instanceof CourseBean)
            {
                resultsFound = true;
                CourseBean course = (CourseBean)obj;
                try
                {
                    Calendar broadcastDate = course.getBroadcastDate();
                    broadcastDate.add(Calendar.DATE, course.getDisplayAsNewFor());
                    if (broadcastDate.after(now))
                        isNew = true;
                }
                catch (Exception xx)
                {
                }
                String completeByDateString = "";
                Iterator audienceItr = course.getAudiences(loginBean.getPerson()).iterator();
                if (audienceItr.hasNext())
                {
                    CourseAudienceBean audience = (CourseAudienceBean)audienceItr.next();
                    //completeByDateString = audience.getCompleteByDateString();
                }
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
								<TD WIDTH=78 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= completeByDateString %></P></TD>
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
								<TD WIDTH=278 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="course-detail.jsp?id=<%= course.getId() %>" style="color: #000000"><%= course.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></A></P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= course.getCategoryNameString() %></P></TD>
								<TD WIDTH=129 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">OLD_MAN</P></TD>
								<TD WIDTH=30 ALIGN=left><br></TD>
                                                                <!-- <TD WIDTH=30 ALIGN=left><A HREF="#"><IMG SRC="images/PttrsnUK_BTN-TakeCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Take Course"></A><A HREF="#"><IMG SRC="images/PttrsnUK_BTN-TakeAssessment.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Take Assessment"></A></TD> -->
							</TR>
							<!-- End One Entry -->
<%
            }
            else if (obj instanceof CertificationBean)
            {
                resultsFound = true;
                CertificationBean cert = (CertificationBean)obj;
                try
                {
                    Calendar broadcastDate = cert.getBroadcastDate();
                    broadcastDate.add(Calendar.DATE, cert.getDisplayAsNewFor());
                    if (broadcastDate.after(now))
                        isNew = true;
                }
                catch (Exception xx)
                {
                }
                String completeByDateString = "";
                Iterator audienceItr = cert.getAudiences(loginBean.getPerson()).iterator();
                if (audienceItr.hasNext())
                {
                    AudienceBean audience = (AudienceBean)audienceItr.next();
                    //completeByDateString = audience.getCompleteByDateString();
                }
%>
                                                        <!-- Begin One Entry [Certification] -->
							<TR>
								<TD WIDTH=70 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #7A5800;"><%= cert.getReleasedDateString() %></P></TD>
								<TD WIDTH=78 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #7A5800;"><%= completeByDateString %></P></TD>
								<TD WIDTH=18 ALIGN=left><br></TD>
								<TD WIDTH=278 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #7A5800;">&#8711;&nbsp;<A HREF="certification-detail.jsp?id=<%= cert.getId() %>" style="color: #7A5800"><%= cert.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></A></P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #7A5800;"><%= cert.getCategoryNameString() %></P></TD>
								<TD WIDTH=129 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #7A5800;">OLD_MAN</P></TD>
								<TD WIDTH=30 ALIGN=left><br></TD>
                                                                <!-- <TD WIDTH=30 ALIGN=left><A HREF="#"><IMG SRC="images/PttrsnUK_BTN-TakeCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Take Course"></A><A HREF="#"><IMG SRC="images/PttrsnUK_BTN-TakeAssessment.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Take Assessment"></A></TD> -->
							</TR>
							<!-- End One Entry [Certification] -->
<%
            }
        }
    }
    else
    {
%>
                                                        <!-- Begin One Entry [No Results] -->
							<TR>
								<TD colspan=7><P style="font-size: 9pt; font-family: Arial; color: #FF0000;">No Courses Found</P></TD>
							</TR>
							<!-- End One Entry [No Results] -->
<%
    }
%>
							
							<!-- Begin One Entry [Certification Course] -->
                                                        <!--
							<TR>
								<TD WIDTH=70 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #7A5800;">00/00/0000</P></TD>
								<TD WIDTH=78 ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #7A5800;">00/00/0000</P></TD>
								<TD WIDTH=18 ALIGN=left><IMG SRC="images/PttrsnUK_ICON_PPT.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="PowerPoint"></TD>
								<TD WIDTH=278 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #7A5800;"><A HREF="#" style="color: #7A5800">Course Title</A></P></TD>
								<TD WIDTH=127 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #7A5800;">Category</P></TD>
								<TD WIDTH=129 ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #7A5800;">Manufacturer</P></TD>
								<TD WIDTH=30 ALIGN=left><A HREF="#"><IMG SRC="images/PttrsnUK_BTN-TakeCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Take Course"></A><A HREF="#"><IMG SRC="images/PttrsnUK_BTN-TakeAssessment.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Take Assessment"></A></TD>
							</TR>
                                                        -->
							<!-- End One Entry [Certification Course] -->
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
					<TD ALIGN=right><P style="font-size: 9pt; font-family: Arial; color: #000000;">Displaying <%= list.getCurrentPageMinimum() %>-<%= list.getCurrentPageMaximum() %> of (<%= list.getNumberOfResults() %>)</P></TD>
					<TD ALIGN=right>
<%
        if (list.hasPreviousPage())
        {
%>
                                            <A HREF="course-catalog.jsp?page=previous<%= (all) ? "&all=true" : "" %>&sort=<%= sortVal %>"><IMG SRC="images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous"></A>
<%
        }
        else
        {
%>
                                            <IMG SRC="images/PttrsnUK_BTN-Up.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Previous">
<%
        }
        if (list.hasNextPage())
        {
%>
                                            <A HREF="course-catalog.jsp?page=next<%= (all) ? "&all=true" : "" %>&sort=<%= sortVal %>"><IMG SRC="images/PttrsnUK_BTN-Down.gif" WIDTH=21 HEIGHT=21 BORDER=0 ALT="Next"></A>
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
}
catch (Exception x)
{
    x.printStackTrace();
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