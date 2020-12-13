                        <!-- Begin New Company Navigation Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;<%= stepString %></P></TD>
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
if (adminPerson.isNew())
{
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 1: Basic Information")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 1:&nbsp;&nbsp;Basic Information<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 2: Roles")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 2:&nbsp;&nbsp;Roles<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 3: Enrollments")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 3:&nbsp;&nbsp;Enrollments<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 4: Certifications")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Step 4:&nbsp;&nbsp;Certifications<br>
<%
}
else
{
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 1: Basic Information")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-user-profile.jsp?id=<%= adminPerson.getId() %>" style="color: #000000">Step 1:&nbsp;&nbsp;Basic Information</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 2: Roles")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-user-roles.jsp" style="color: #000000">Step 2:&nbsp;&nbsp;Roles</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 3: Enrollments")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-user-enrollments.jsp" style="color: #000000">Step 3:&nbsp;&nbsp;Enrollments</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 4: Certifications")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-user-certifications.jsp" style="color: #000000">Step 4:&nbsp;&nbsp;Certifications</A><br>
<%
}
%>
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
			<!-- End New Company Navigation Area -->