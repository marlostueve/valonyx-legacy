                        <!-- Begin New Certification Navigation Area -->
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
if (adminCert.isNew())
{
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 1:&nbsp;&nbsp;Basic Information<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification-audience.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 2:&nbsp;&nbsp;Audience<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification-prereq.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 3:&nbsp;&nbsp;Prerequisites<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification-courses.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 4:&nbsp;&nbsp;Associated Courses<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification-assessment.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 5:&nbsp;&nbsp;Assessment<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification-owner.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 6:&nbsp;&nbsp;Owner/Contacts<br>
<%
}
else
{
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-certification.jsp<%= (adminCert.isNew()) ? "" : "?id=" + adminCert.getIdString() %>" style="color: #000000">Step 1:&nbsp;&nbsp;Basic Information</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification-audience.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-certification-audience.jsp<%= (adminCert.isNew()) ? "" : "?id=" + adminCert.getIdString() %>" style="color: #000000">Step 2:&nbsp;&nbsp;Audience</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification-prereq.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-certification-prereq.jsp<%= (adminCert.isNew()) ? "" : "?id=" + adminCert.getIdString() %>" style="color: #000000">Step 3:&nbsp;&nbsp;Prerequisites</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification-courses.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-certification-courses.jsp<%= (adminCert.isNew()) ? "" : "?id=" + adminCert.getIdString() %>" style="color: #000000">Step 4:&nbsp;&nbsp;Associated Courses</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification-assessment.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-certification-assessment.jsp<%= (adminCert.isNew()) ? "" : "?id=" + adminCert.getIdString() %>" style="color: #000000">Step 5:&nbsp;&nbsp;Assessment</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-certification-owner.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-certification-owner.jsp<%= (adminCert.isNew()) ? "" : "?id=" + adminCert.getIdString() %>" style="color: #000000">Step 6:&nbsp;&nbsp;Owner/Contacts</A><br>
<%
}
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=30 HEIGHT=30 ALT=""><INPUT TYPE="submit" NAME="createCertification" VALUE="<%= (adminCert.isNew()) ? "Create" : "Save" %> Certification">
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
			<!-- End New Certification Navigation Area -->