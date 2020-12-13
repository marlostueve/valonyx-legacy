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
String deleteIdString = request.getParameter("deleteId");
if (deleteIdString != null)
{
    AudienceBean.delete(deleteIdString);
    adminCert.invalidate();
}
%>

<struts-html:html locale="true">
<HEAD>
<struts-html:base/>
<TITLE>Patterson Learning System</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1"><%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--

function
initForm()
{
    document.certificationAudienceForm.userGroupSelect.selectedIndex = 0;
    document.certificationAudienceForm.subGroupSelect.selectedIndex = 0;
    document.certificationAudienceForm.regionSelect.selectedIndex = 0;
    document.certificationAudienceForm.locationSelect.selectedIndex = 0;
    document.certificationAudienceForm.jobTitleSelect.selectedIndex = 0;
}

// -->
</script>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages(); initForm();">
<struts-html:javascript formName="certificationAudienceForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src="../staticJavascript.jsp"></script>
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
			<!-- Begin Assign Audience Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Assign Audience</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/certificationAudience" focus="audienceNameInput" onsubmit="return validateCertificationAudienceForm(this);">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Name:</P></TD>
								<TD>
                                                                        <struts-html:text property="audienceNameInput" style="font-size: 13px; width: 410px;" />
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;User Group:</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                        <struts-html:select property="userGroupSelect" multiple="true" style="font-size: 13px; width: 410px;" >
                                                                            <struts-html:option value="0">-- ALL USER GROUPS --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="personGroups" />
                                                                        </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Sub Group:</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                    <struts-html:select property="subGroupSelect" style="font-size: 13px; width: 410px;" >
                                                                        <struts-html:option value="0">-- ALL SUB GROUPS --</struts-html:option>
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Region:</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                        <struts-html:select property="regionSelect" multiple="true" style="font-size: 13px; width: 410px;" >
                                                                            <struts-html:option value="0">-- ALL REGIONS --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="regions" />
                                                                        </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Location:</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                        <struts-html:select property="locationSelect" multiple="true" style="font-size: 13px; width: 410px;" >
                                                                            <struts-html:option value="0">-- ALL LOCATIONS --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="branches" />
                                                                        </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Job Title:</P></TD>
								<TD COLSPAN=2 ALIGN=left>
                                                                        <struts-html:select property="jobTitleSelect" multiple="true" style="font-size: 13px; width: 410px;" >
                                                                            <struts-html:option value="0">-- ALL JOB TITLES --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="personTitles" />
                                                                        </struts-html:select>
								</TD>
							</TR>
						</TABLE>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=91><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Certification Is:</P></TD>
								<TD WIDTH=429>
                                                                        <struts-html:select property="courseIsSelect" style="font-size: 13px; width: 120px;" >
                                                                            <struts-html:option value="Elective">Elective</struts-html:option>
                                                                            <struts-html:option value="Recommended">Recommended</struts-html:option>
                                                                            <struts-html:option value="Required">Required</struts-html:option>
                                                                        </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Complete By:</P></TD>
								<TD>
									<IMG SRC="../images/trspacer.gif" WIDTH=189 HEIGHT=1 ALT="">
                                                                            <struts-html:text property="completeByDateInput" style="font-size: 13px; width: 80px;" maxlength="10" onfocus="getDate('certificationAudienceForm','completeByDateInput');" />
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="assignAudience" VALUE="Assign Audience"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Assign Audience Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Audience(s) Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=26>
								<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Certification Audience(s)</P></TD>
							</TR>
<%
List audiences = AudienceBean.getAudiences(adminCert);
int sizer = audiences.size();
if (sizer > 0)
{
    Iterator itr = audiences.iterator();
    while (itr.hasNext())
    {
        AudienceBean audienceObj = (AudienceBean)itr.next();
%>
							<TR BGCOLOR="#9EBFE9" HEIGHT=8>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
								<TD ALIGN=left>
									<TABLE WIDTH=520 BORDER=0 CELLPADDING=0 CELLSPACING=0>
										<TR>
											<TD WIDTH=2 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=70 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=18 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=410 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
										</TR>
										<!-- Begin One Result Entry -->
										<TR>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
											<TD ALIGN=center><P style="font-size: 9pt; font-family: Arial; color: #000000;">001</P></TD>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Audience.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Audience"></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><%= audienceObj.getNameString() %></P></TD>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left><a href="javascript:if (confirm('Are you sure?')) location='admin-certification-audience.jsp?id=<%= request.getParameter("id") %>&deleteId=<%= audienceObj.getIdString() %>'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Audience"></a></TD>
										</TR>
										<TR>
											<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left COLSPAN=4>
												<TABLE WIDTH=448 BORDER=0 CELLPADDING=0 CELLSPACING=0 BGCOLOR="#EEEEEE">
													<TR BGCOLOR="#DDDDDD">
														<TD WIDTH=18 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=80 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=85 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=63 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=183 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
														<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Certification Is:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= audienceObj.getCourseReqType() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">User Group:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= audienceObj.getPersonGroupNameString() %></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Complete By:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= audienceObj.getCompleteByDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Sub Group:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left valign="top"><P style="font-size: 8pt; font-family: Arial; color: #303030;">Region:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= audienceObj.getRegionNameString() %></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left valign="top"><P style="font-size: 8pt; font-family: Arial; color: #303030;">Location:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= audienceObj.getLocationNameString() %></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left valign="top"><P style="font-size: 8pt; font-family: Arial; color: #303030;">Job Title:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= audienceObj.getPersonTitleString() %></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR BGCOLOR="#DDDDDD">
														<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
													</TR>
												</TABLE>
											</TD>
										</TR>
										<!-- End One Result Entry -->
									</TABLE>
								</TD>
							</TR>
							<TR BGCOLOR="#FFFFFF">
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
							</TR>
<%
    }
}
else
{
%>
							<TR BGCOLOR="#FFFFFF">
								<TD ALIGN=left><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Audiences Found</p></TD>
							</TR>
<%
}
%>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Audience(s) Area -->
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