<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*,java.util.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCert" class="com.badiyan.uk.beans.CertificationBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.beans.PattersonLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
if (request.getParameter("deletePrereqCourseId") != null)
{
    try
    {
        CourseBean prereq = CourseBean.getCourse(Integer.parseInt(request.getParameter("deletePrereqCourseId")));
        adminCert.removePrerequisite(prereq);
    }
    catch (Exception x)
    {
    }
}
if (request.getParameter("deletePrereqCertId") != null)
{
    try
    {
        CertificationBean prereq = CertificationBean.getCertification(Integer.parseInt(request.getParameter("deletePrereqCertId")));
        adminCert.removePrerequisite(prereq);
    }
    catch (Exception x)
    {
    }
}
%>

<struts-html:html locale="true">
<HEAD>
<struts-html:base/>
<TITLE>Patterson Learning System</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--

var type;
var manufacturer;
var category;

function
initForm()
{
    document.certificationPrerequisiteForm.typeSelect[0].checked = true;
    type = 0;
    manufacturer = 0;
    category = 0;
    generateList();
}

function
typeClick()
{
    if (document.certificationPrerequisiteForm.typeSelect[0].checked == true)
        type = 0;
    else if (document.certificationPrerequisiteForm.typeSelect[1].checked == true)
        type = 1;
    generateList();
}

function
manufacturerClick()
{
    manufacturer = document.certificationPrerequisiteForm.manufacturerSelect.value;
    generateList();
}

function
categoryClick()
{
    category = document.certificationPrerequisiteForm.categorySelect.value;
    generateList();
}

function
generateList()
{
    var index = 1;
    document.certificationPrerequisiteForm.titleSelect.options.length = 1;
    if (type == 0)
    {
<%
// Get courses
Iterator itr = CourseBean.getCourses().iterator();
while (itr.hasNext())
{
    CourseBean course = (CourseBean)itr.next();
%>
        if (((manufacturer == 0) || (manufacturer == "<%= course.getManufacturerNameString() %>")) &&
                ((category == 0) || (category == "<%= course.getCategoryNameString() %>")))
            eval("document.certificationPrerequisiteForm.titleSelect.options[" + index++ + "] = new Option(\"<%= course.getNameString() %>\", \"<%= course.getIdString() %>\", false, false)");
<%
}
%>
    }
    else
    {
<%
// Get certifications
itr = CertificationBean.getCertifications().iterator();
while (itr.hasNext())
{
    CertificationBean cert = (CertificationBean)itr.next();
%>
        if (((manufacturer == 0) || (manufacturer == "<%= cert.getManufacturerNameString() %>")) &&
                ((category == 0) || (category == "<%= cert.getCategoryNameString() %>")))
            eval("document.certificationPrerequisiteForm.titleSelect.options[" + index++ + "] = new Option(\"<%= cert.getNameString() %>\", \"<%= cert.getIdString() %>\", false, false)");
<%
}
%>
    }
}

// -->
</script>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages();initForm();">
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
		<TD WIDTH=522 ALIGN=left VALIGN=top>
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">Course/Certification Manager&nbsp;&nbsp;&gt;&nbsp;&nbsp;<%= (adminCert.isNew()) ? "Create New" : "Edit" %> Certification</P>
<%@ include file="channels\channel-certification-menu.jsp" %>
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Assign Prerequisite Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Assign Prerequisite</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/certificationPrerequisite">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
                                                        <TR HEIGHT=30>
								<TD></TD>
								<TD>
                                                                        <P style="font-size: 10pt; font-family: Arial; color: #000000;"><struts-html:radio property="typeSelect" value="0" onclick="typeClick()" />Course<IMG SRC="../images/trspacer.gif" WIDTH=8 HEIGHT=1 ALT=""><struts-html:radio property="typeSelect" value="1" onclick="typeClick()" />Certification</P>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Manufacturer:</P></TD>
								<TD>
                                                                    <struts-html:select property="manufacturerSelect" style="font-size: 13px; width: 410px;" onchange="manufacturerClick()" >
                                                                        <struts-html:option value="0">-- [FILTER BY MANUFACTURER] - ALL MANUFACTURERS--</struts-html:option>
                                                                        <struts-html:optionsCollection name="listHelper" property="manufacturers" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Category:</P></TD>
								<TD>
                                                                    <struts-html:select property="categorySelect" style="font-size: 13px; width: 410px;" onchange="categoryClick()" >
                                                                        <struts-html:option value="0">-- [FILTER BY CATEGORY] - ALL CATEGORIES --</struts-html:option>
                                                                        <struts-html:optionsCollection name="listHelper" property="categories" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Title:</P></TD>
								<TD>
                                                                    <struts-html:select property="titleSelect" style="font-size: 13px; width: 410px;" >
                                                                        <struts-html:option value="0">-- SELECT A COURSE/CERTIFICATION --</struts-html:option>
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="assignPrerequisite" VALUE="Assign Prerequisite"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Assign Prerequisite Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Prerequisite Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Certification Prerequisite(s)</P></TD>
							</TR>
							<TR BGCOLOR="#FFFFFF" HEIGHT=26>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;To view the details for a prerequisite, click on the course/certification title.</P></TD>
							</TR>
							<TR BGCOLOR="#9EBFE9" HEIGHT=8>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
								<TD ALIGN=left>
									<TABLE WIDTH=520 BORDER=0 CELLPADDING=0 CELLSPACING=0>
<%
String pageString = "admin-certification-prereq";
String deleteString = "deletePrereqCourseId";
String courseIdString = adminCert.getIdString();
itr = adminCert.getCoursePrerequisites().iterator();
CourseBean course = null;
if (itr.hasNext())
{
    while (itr.hasNext())
    {
        course = (CourseBean)itr.next();
%>
										<TR>
											<TD WIDTH=2 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=50 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=280 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=130 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
<%@ include file="channels\channel-course.jsp" %>
<%
    }
}
else
{
%>
      <tr><td colspan=6><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Courses Found</p></td></tr>
<%
}
%>
<%
itr = adminCert.getCertificationPrerequisites().iterator();
CertificationBean cert = null;
if (itr.hasNext())
{
    while (itr.hasNext())
    {
        cert = (CertificationBean)itr.next();
%>
										<TR>
											<TD WIDTH=2 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=50 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=280 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=130 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<!-- Begin One Result Entry -->
										<TR>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_StatusGreen.jpg" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Active"></TD>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= (cert.getId() + 1000) + "" %></P></TD>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Internet.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Internet"></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-certification.jsp?id=<%= cert.getIdString() %>" style="color: #000000"><%= cert.getNameString() %></A></P></TD>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= cert.getManufacturerNameString() %></P></TD>
											<TD ALIGN=left><A HREF="javascript:if (confirm('Are you sure?')) location = 'admin-certification-prereq.jsp?id=<%= adminCert.getId() %>&deletePrereqCertId=<%= cert.getIdString() %>'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Course/Certification"></A></TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left COLSPAN=5>
												<TABLE WIDTH=448 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#EEEEEE">
													<TR BGCOLOR="#DDDDDD">
														<TD WIDTH=18 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=65 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=95 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=63 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=188 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Created:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= cert.getCreationDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Category:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= cert.getCategoryNameString() %></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Released:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= cert.getReleasedDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">User Group:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Expires:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= cert.getOffDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Sub Group:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course %></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Last Update:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= cert.getModificationDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Region:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Location:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Job Title:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
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
      <tr><td colspan=6><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Certifications Found</p></td></tr>
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
			<!-- End Prerequisite Area -->
		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
</TABLE>



<!-- End Bottom Content Area -->
<!-- Begin Bottom Copyright Info Area -->
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<TR>
		<TD WIDTH=772 ALIGN=center><IMG SRC="../images/trspacer.gif" WIDTH=772 HEIGHT=40 ALT=""></TD>
	</TR>
	<TR>
		<TD WIDTH=772 ALIGN=center><P style="font-size: 8pt; font-family: Arial; color: #000000;">&copy; 2003 Patterson Dental Supply, Inc. All rights reserved.</P></TD>
	</TR>
	<TR>
		<TD WIDTH=772 ALIGN=center><IMG SRC="../images/trspacer.gif" WIDTH=772 HEIGHT=6 ALT=""></TD>
	</TR>
</TABLE>
<!-- End Bottom Copyright Info Area -->
</BODY>
</struts-html:html>