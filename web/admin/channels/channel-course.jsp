                                                                                <!-- Begin One Result Entry -->
										<TR>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
<%
        if (course.getStatus().equals(CourseBean.DEFAULT_COURSE_STATUS))
        {
%>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_StatusGreen.jpg" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Active"></TD>
<%
        }
        else
        {
%>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_StatusRed.jpg" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Inactive"></TD>
<%
        }
%>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;"><%= (course.getId() + 1000) + "" %></P></TD>
<%
        if (course.getResourceType().equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
        {
%>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Acrobat.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Acrobat"></TD>
<%
        }
        else if (course.getResourceType().equals(ResourceBean.CD_RESOURCE_TYPE))
        {
%>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_CD.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="CD"></TD>
<%
        }
        else if (course.getResourceType().equals(ResourceBean.CLASSROOM_RESOURCE_TYPE))
        {
%>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Classroom.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Classroom"></TD>
<%
        }
        else if (course.getResourceType().equals(ResourceBean.FLASH_RESOURCE_TYPE))
        {
%>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Flash.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Flash"></TD>
<%
        }
        else if (course.getResourceType().equals(ResourceBean.INTERNET_RESOURCE_TYPE))
        {
%>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Internet.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Internet"></TD>
<%
        }
        else if (course.getResourceType().equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
        {
%>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_PPT.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="PowerPoint"></TD>
<%
        }
        else if (course.getResourceType().equals(ResourceBean.VIDEO_RESOURCE_TYPE))
        {
%>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Video.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Video"></TD>
<%
        }
        else if (course.getResourceType().equals(ResourceBean.WORD_RESOURCE_TYPE))
        {
%>
											<TD ALIGN=left><IMG SRC="../images/PttrsnUK_ICON_Word.gif" WIDTH=18 HEIGHT=15 BORDER=0 ALT="Word"></TD>
<%
        }
        else
        {
%>
											<TD ALIGN=left></TD>
<%
        }
%>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-course.jsp?id=<%= course.getIdString() %>" style="color: #000000"><%= course.getNameString() %></A>&nbsp; - <%= course %></P></TD>
											<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
											<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; color: #000000;">OLD_MAN</P></TD>
											<TD ALIGN=left><a href="admin-report-course.jsp?id=<%= course.getId() %>"><IMG SRC="../images/PttrsnUK_BTN-TakeCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="View Report"></a><A HREF="javascript:if (confirm('Are you sure?')) location = '<%= pageString %>.jsp?id=<%= courseIdString %>&<%= deleteString %>=<%= course.getIdString() %>'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Course/Certification"></A></TD>
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
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course.getCreationDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Category:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course.getCategoryNameString() %></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Released:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course.getReleasedDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">User Group:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"></TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Expires:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course.getOffDateString() %></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Sub Group:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">&nbsp;</TD>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
													</TR>
													<TR>
														<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;">Last Update:</TD>
														<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #303030;"><%= course.getModificationDateString() %></TD>
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