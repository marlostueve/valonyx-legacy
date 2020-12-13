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
if (adminCompany.isNew())
{
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 1: Basic Information")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Basic Information<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 3: Departments")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Departments<br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 4: Job Titles")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Job Titles<br>
                                                                        <IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 5: User Groups")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">User Groups<br>
                                                                        <IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 6: Categories")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Categories<br>
                                                                        <IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 7: Audiences")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Audiences<br>
                                                                        <IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 8: Classrooms")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Classrooms<br>
                                                                        <IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 9: Addresses")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT="">Addresses<br>
<%
}
else
{
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 1: Basic Information")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-organization-main.jsp<%= (adminCompany.isNew()) ? "" : ("?id=" + adminCompany.getId()) %>" style="color: #000000">Basic Information</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 3: Departments")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-organization-departments.jsp" style="color: #000000">Departments</A><br>
									<IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 4: Job Titles")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-organization-job-titles.jsp" style="color: #000000">Job Titles</A><br>
                                                                        <IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 5: User Groups")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-organization-groups.jsp" style="color: #000000">User Groups</A><br>
                                                                        <IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 6: Categories")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-organization-categories.jsp" style="color: #000000">Categories</A><br>
                                                                        <IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 7: Audiences")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-organization-audiences.jsp" style="color: #000000">Audiences</A><br>
                                                                        <IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 8: Classrooms")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-organization-classrooms.jsp" style="color: #000000">Classrooms</A><br>
                                                                        <IMG SRC="../images/trspacer.gif" WIDTH=5 HEIGHT=10 ALT=""><IMG SRC="<%= (stepString.equals("Step 9: Addresses")) ? "../images/PttrsnUK_CurrentArrow.jpg" : "../images/trspacer.gif" %>" WIDTH=10 HEIGHT=10 ALT=""><A HREF="admin-organization-addresses.jsp" style="color: #000000">Addresses</A><br>
<%
}
%>
									<IMG SRC="../images/trspacer.gif" WIDTH=6 HEIGHT=30 ALT=""><INPUT TYPE="submit" NAME="createAssessment" VALUE="<%= (adminCompany.isNew()) ? "Create" : "Save" %> Company">
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