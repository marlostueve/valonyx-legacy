                        <!-- Begin New Course Navigation Area -->
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
if (adminCourse.isNew())
{
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 1:&nbsp;&nbsp;Basic Information<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-audience.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 2:&nbsp;&nbsp;Audiences<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-instructors.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 3:&nbsp;&nbsp;Instructors<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-prereq.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 4:&nbsp;&nbsp;Prerequisites<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-media.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 5:&nbsp;&nbsp;Media<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-window.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 6:&nbsp;&nbsp;Window<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-assessment.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 7:&nbsp;&nbsp;Assessment<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-owner.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 8:&nbsp;&nbsp;Owner/Contacts<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-settings.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 9:&nbsp;&nbsp;Enroll/Drop Settings<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-section.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 10:&nbsp;&nbsp;Sections<br>
<%
}
else
{
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-course.jsp<%= (adminCourse.isNew()) ? "" : "?id=" + adminCourse.getIdString() %>" style="color: #000000">Step 1:&nbsp;&nbsp;Basic Information</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-audience.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-course-audience.jsp<%= (adminCourse.isNew()) ? "" : "?id=" + adminCourse.getIdString() %>" style="color: #000000">Step 2:&nbsp;&nbsp;Audiences</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-instructors.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-course-instructors.jsp<%= (adminCourse.isNew()) ? "" : "?id=" + adminCourse.getIdString() %>" style="color: #000000">Step 3:&nbsp;&nbsp;Instructors</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-prereq.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-course-prereq.jsp<%= (adminCourse.isNew()) ? "" : "?id=" + adminCourse.getIdString() %>" style="color: #000000">Step 4:&nbsp;&nbsp;Prerequisites</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-media.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-course-media.jsp<%= (adminCourse.isNew()) ? "" : "?id=" + adminCourse.getIdString() %>" style="color: #000000">Step 5:&nbsp;&nbsp;Media</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-window.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-course-window.jsp<%= (adminCourse.isNew()) ? "" : "?id=" + adminCourse.getIdString() %>" style="color: #000000">Step 6:&nbsp;&nbsp;Window</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-assessment.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-course-assessment.jsp<%= (adminCourse.isNew()) ? "" : "?id=" + adminCourse.getIdString() %>" style="color: #000000">Step 7:&nbsp;&nbsp;Assessment</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-owner.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-course-owner.jsp<%= (adminCourse.isNew()) ? "" : "?id=" + adminCourse.getIdString() %>" style="color: #000000">Step 8:&nbsp;&nbsp;Owner/Contacts</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-settings.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-course-settings.jsp<%= (adminCourse.isNew()) ? "" : "?id=" + adminCourse.getIdString() %>" style="color: #000000">Step 9:&nbsp;&nbsp;Enroll/Drop Settings</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="../images/<%= (request.getRequestURI().indexOf("admin-course-section.jsp") > -1) ? "PttrsnUK_CurrentArrow.jpg" : "trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-course-section.jsp<%= (adminCourse.isNew()) ? "" : "?id=" + adminCourse.getIdString() %>" style="color: #000000">Step 10:&nbsp;&nbsp;Sections</A><br>
<%
}
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=30 HEIGHT=30 ALT=""><INPUT TYPE="submit" NAME="createCourse" VALUE="<%= (adminCourse.isNew()) ? "Create" : "Save" %> Course">
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
			<!-- End New Course Navigation Area -->