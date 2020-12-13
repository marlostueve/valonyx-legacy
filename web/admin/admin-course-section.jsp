<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminCourseSection" class="com.badiyan.uk.beans.CourseSectionBean" scope="session" />
<jsp:useBean id="adminCourseSectionStatus" class="com.badiyan.uk.beans.CourseSectionStatusBean" scope="session" />
<jsp:useBean id="adminCourseSectionPeriod" class="com.badiyan.uk.beans.CourseSectionPeriodBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%

ClassroomBean selected_room = null;

String deleteIdString = request.getParameter("deleteId");
if (deleteIdString != null)
{
    AudienceBean.delete(deleteIdString);
}

int selected_section_id = 0;
if (request.getParameter("section_id") != null)
{
    try
    {
        selected_section_id = Integer.parseInt(request.getParameter("section_id"));
        CourseSectionBean section_obj = CourseSectionBean.getSection(selected_section_id);
        CompanyBean company_obj = section_obj.getCourse().getCompany();

        // ensure that this person is an admin for this company

        if (company_obj.isAdministrator(loginBean.getPerson()))
        {
            adminCompany = company_obj;
            session.setAttribute("adminCompany", adminCompany);

            adminCourseSection = section_obj;
            session.setAttribute("adminCourseSection", adminCourseSection);

            // I need to load the period definition for this section also

            adminCourseSectionPeriod = adminCourseSection.getPeriod();
            session.setAttribute("adminCourseSectionPeriod", adminCourseSectionPeriod);
        }
    }
    catch (Exception x)
    {
        x.printStackTrace();
    }
}
else
{
    //adminCourseSection = new CourseSectionBean();
    //session.setAttribute("adminCourseSection", adminCourseSection);
}

%>

<struts-html:html locale="true">
<HEAD>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1"><%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--

function
initForm()
{
    //document.forms[0].audienceSelect.value = ;
    //document.forms[0].courseIsSelect.value = '';
    //document.forms[0].completeByDateInput.value = '';
    //document.courseAudienceForm.audienceSelect.selectedIndex = 
}

// -->
</script>
</HEAD>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages(); initForm();">
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
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">Course/Certification Manager&nbsp;&nbsp;&gt;&nbsp;&nbsp;<%= (adminCourse.isNew()) ? "Create New" : "Edit" %> Course</P>
<%@ include file="channels\channel-course-menu.jsp" %>
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Assign Audience Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Add Section</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=1 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
						    <struts-html:form action="/admin/courseSection">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Section Name/Number:</P></TD>
								<TD>
                                                                    <input type="text" name="nameInput" value="<%= adminCourseSection.getLabel() %>" maxlength="50" style="font-size: 9pt; font-family: Arial; width: 400px;">
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=91><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Status:</P></TD>
								<TD WIDTH=429>
									<select name="status" style="font-size: 9pt; font-family: Arial; width: 120px;">
<%
CourseSectionStatusBean selected_status = null;
try
{
    selected_status = adminCourseSection.getStatus();
}
catch (Exception x)
{
}
Iterator status_itr = CourseSectionStatusBean.getStatus(adminCompany).iterator();
while (status_itr.hasNext())
{
    CourseSectionStatusBean obj = (CourseSectionStatusBean)status_itr.next();
    
%>
									    <option value="<%= obj.getValue() %>"<%= (obj.equals(selected_status)) ? " selected" : "" %>><%= obj.getLabel() %></option>
<%
}
%>
									</select>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=91><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Primary Room:</P></TD>
								<TD WIDTH=429>
									<select name="room" style="font-size: 9pt; font-family: Arial; width: 400px;">
									    <option value="0">-- SELECT A CLASSROOM --</option>
<%
try
{
    selected_room = adminCourseSection.getClassroom();
}
catch (Exception x)
{
}
Iterator classrooms = ClassroomBean.getClassrooms(adminCompany).iterator();
while (classrooms.hasNext())
{
    ClassroomBean classroom = (ClassroomBean)classrooms.next();
%>
									    <option value="<%= classroom.getValue() %>"<%= (classroom.equals(selected_room)) ? " selected" : "" %>><%= classroom.getLabel() %></option>
<%
}
%>
									</select>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Capacity:</P></TD>
								<TD>
                                                                    <input type="text" name="capacity" value="<%= adminCourseSection.getCapacityString() %>" maxlength="5" style="font-size: 9pt; font-family: Arial; width: 120px;">
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=91><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Language:</P></TD>
								<TD WIDTH=429>
									<select name="language" style="font-size: 9pt; font-family: Arial; width: 400px;">
<%
LanguageBean selected_language = null;
try
{
    selected_language = adminCourseSection.getLanguage();
}
catch (Exception x)
{
    //x.printStackTrace();
}
Iterator languages = LanguageBean.getLanguages().iterator();
while (languages.hasNext())
{
    LanguageBean language = (LanguageBean)languages.next();
%>
									    <option value="<%= language.getValue() %>"<%= language.equals(selected_language) ? " selected" : "" %>><%= language.getLabel() %></option>
<%
}
%>
									</select>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=91 valign="top"><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Instructors:</P></TD>
								<TD WIDTH=429>
									<select name="instructors" style="font-size: 9pt; font-family: Arial; width: 400px;" multiple size="4">
<%
Iterator intructors = UKOnlinePersonBean.getPersons(adminCompany, RoleBean.INSTRUCTOR_ROLE_NAME).iterator();
while (intructors.hasNext())
{
    PersonBean instructor = (UKOnlinePersonBean)intructors.next();
%>
									    <option value="<%= instructor.getValue() %>"<%= adminCourseSection.contains(instructor) ? " selected" : "" %>><%= instructor.getLabel() %></option>
<%
}
%>
									</select>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Instructor Comment:</P></TD>
								<TD>
                                                                    <textarea name="instructor_comment" style="font-size: 9pt; font-family: Arial; width: 400px;"><%= adminCourseSection.getInstructorCommentString() %></textarea>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Schedule:</P></TD>
								<TD>
                                                                    <textarea name="schedule" style="font-size: 9pt; font-family: Arial; width: 400px;"><%= adminCourseSection.getScheduleString() %></textarea>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Comment:</P></TD>
								<TD>
                                                                    <textarea name="comment" style="font-size: 9pt; font-family: Arial; width: 400px;"><%= adminCourseSection.getCommentString() %></textarea>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Start Date:</P></TD>
								<TD>
									<IMG SRC="../images/trspacer.gif" WIDTH=189 HEIGHT=1 ALT="">
									    <input type="text" name="section_start_date" value="<%= adminCourseSection.getStartDateString() %>" style="font-size: 9pt; font-family: Arial; width: 80px;" maxlength="10" onfocus="getDate('courseSectionForm','section_start_date');">
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;End Date:</P></TD>
								<TD>
									<IMG SRC="../images/trspacer.gif" WIDTH=189 HEIGHT=1 ALT="">
									    <input type="text" name="section_end_date" value="<%= adminCourseSection.getEndDateString() %>" style="font-size: 9pt; font-family: Arial; width: 80px;" maxlength="10" onfocus="getDate('courseSectionForm','section_end_date');">
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Drop Deadline:</P></TD>
								<TD>
									<IMG SRC="../images/trspacer.gif" WIDTH=189 HEIGHT=1 ALT="">
									    <input type="text" name="section_drop_deadline_date" value="<%= adminCourseSection.getDropDeadlineString() %>" style="font-size: 9pt; font-family: Arial; width: 80px;" maxlength="10" onfocus="getDate('courseSectionForm','section_drop_deadline_date');">
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="assignSection" VALUE="<%= (adminCourseSection.isNew()) ? "Assign" : "Save" %> Section"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
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
			
			<!-- Begin Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Section Status</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/courseSectionStatus" focus="nameInput">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Status:</P></TD>
								<TD>
                                                                    <input type="text" name="nameInput" value="" style="font-size: 9pt; font-family: Arial; width: 400px;">
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="createQuestion" VALUE="Save Status"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>

                        
<%
if (!adminCourseSection.isNew())
{ 
%>
			<!-- Begin Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Section Period</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/courseSectionPeriod">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN="4"><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=80 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=180 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
                                                                <TD WIDTH=80 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
                                                                <TD WIDTH=180 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Room:</P></TD>
								<TD colspan="3">
									<select name="room" style="font-size: 9pt; font-family: Arial; width: 400px;">
									    <option value="0">-- SELECT A CLASSROOM --</option>
<%
    ClassroomBean period_selected_room = null;
    try
    {
        period_selected_room = adminCourseSectionPeriod.getClassroom();
    }
    catch (Exception x)
    {
    }
    if (period_selected_room == null)
        period_selected_room = selected_room;
    Iterator classrooms = ClassroomBean.getClassrooms(adminCompany).iterator();
    while (classrooms.hasNext())
    {
        ClassroomBean classroom = (ClassroomBean)classrooms.next();
%>
									    <option value="<%= classroom.getValue() %>"<%= (classroom.equals(period_selected_room)) ? " selected" : "" %>><%= classroom.getLabel() %></option>
<%
    }
%>
									</select>
								</TD>
							</TR>
							<TR>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Start Time:</P></TD>
								<TD>
                                                                    <select name="period_start_time_hour" style="font-size: 9pt; font-family: Arial; width: 40px;">
                                                                        <option value="1"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 1 ? " SELECTED" : "" %>>1</option>
                                                                        <option value="2"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 2 ? " SELECTED" : "" %>>2</option>
                                                                        <option value="3"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 3 ? " SELECTED" : "" %>>3</option>
                                                                        <option value="4"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 4 ? " SELECTED" : "" %>>4</option>
                                                                        <option value="5"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 5 ? " SELECTED" : "" %>>5</option>
                                                                        <option value="6"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 6 ? " SELECTED" : "" %>>6</option>
                                                                        <option value="7"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 7 ? " SELECTED" : "" %>>7</option>
                                                                        <option value="8"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 8 ? " SELECTED" : "" %>>8</option>
                                                                        <option value="9"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 9 ? " SELECTED" : "" %>>9</option>
                                                                        <option value="10"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 10 ? " SELECTED" : "" %>>10</option>
                                                                        <option value="11"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 11 ? " SELECTED" : "" %>>11</option>
                                                                        <option value="12"<%= adminCourseSectionPeriod.getPeriodStartTimeHour() == 12 ? " SELECTED" : "" %>>12</option>
                                                                    </select>:
                                                                    <select name="period_start_time_minute" style="font-size: 9pt; font-family: Arial; width: 40px;">
                                                                        <option value="0"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 0 ? " SELECTED" : "" %>>00</option>
                                                                        <option value="15"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 15 ? " SELECTED" : "" %>>15</option>
                                                                        <option value="30"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 30 ? " SELECTED" : "" %>>30</option>
                                                                        <option value="45"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 45 ? " SELECTED" : "" %>>45</option>
                                                                        <option value="5"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 5 ? " SELECTED" : "" %>>05</option>
                                                                        <option value="10"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 10 ? " SELECTED" : "" %>>10</option>
                                                                        <option value="15"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 15 ? " SELECTED" : "" %>>15</option>
                                                                        <option value="20"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 20 ? " SELECTED" : "" %>>20</option>
                                                                        <option value="25"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 25 ? " SELECTED" : "" %>>25</option>
                                                                        <option value="30"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 30 ? " SELECTED" : "" %>>30</option>
                                                                        <option value="35"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 35 ? " SELECTED" : "" %>>35</option>
                                                                        <option value="40"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 40 ? " SELECTED" : "" %>>40</option>
                                                                        <option value="45"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 45 ? " SELECTED" : "" %>>45</option>
                                                                        <option value="50"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 50 ? " SELECTED" : "" %>>50</option>
                                                                        <option value="55"<%= adminCourseSectionPeriod.getPeriodStartTimeMinute() == 55 ? " SELECTED" : "" %>>55</option>
                                                                    </select>&nbsp;
                                                                    <select name="period_start_time_am_pm" style="font-size: 9pt; font-family: Arial; width: 50px;">
                                                                        <option value="<%= Calendar.AM %>"<%= adminCourseSectionPeriod.getPeriodStartTimeAmPm() == Calendar.AM ? " SELECTED" : "" %>>AM</option>
                                                                        <option value="<%= Calendar.PM %>"<%= adminCourseSectionPeriod.getPeriodStartTimeAmPm() == Calendar.PM ? " SELECTED" : "" %>>PM</option>
                                                                    </select>
								</TD>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Duration:</P></TD>
								<TD>
                                                                    <input type="text" name="duration" value="<%= adminCourseSectionPeriod.getDurationString() %>" maxlength="3" style="font-size: 9pt; font-family: Arial; width: 50px;">&nbsp;
                                                                    <select name="duration_type" style="font-size: 9pt; font-family: Arial; width: 80px;">
                                                                        <option value="<%= Calendar.MINUTE %>"<%= adminCourseSectionPeriod.getDurationType() == Calendar.MINUTE ? " SELECTED" : "" %>>minutes</option>
                                                                        <option value="<%= Calendar.HOUR %>"<%= adminCourseSectionPeriod.getDurationType() == Calendar.HOUR ? " SELECTED" : "" %>>hours</option>
                                                                        <option value="<%= Calendar.DATE %>"<%= adminCourseSectionPeriod.getDurationType() == Calendar.DATE ? " SELECTED" : "" %>>days</option>
                                                                        <option value="<%= Calendar.YEAR %>"<%= adminCourseSectionPeriod.getDurationType() == Calendar.YEAR ? " SELECTED" : "" %>>weeks</option>
                                                                    </select>
								</TD>
							</TR>
							<TR HEIGHT=2 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
                                                        <TR BGCOLOR="#DDDDDD">
                                                                <TD><input type="radio" name="recurrence_pattern" value="1"<%= adminCourseSectionPeriod.getRecurrencePattern() == 1 ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Daily:</span></TD>
								<TD colspan="3"><input type="radio" name="recurrence_pattern_daily" value="1"<%= adminCourseSectionPeriod.getRecurrencePatternDaily() == 1 ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Every</span>&nbsp;
                                                                    <input type="text" name="recurrence_frequency_daily" value="<%= adminCourseSectionPeriod.getRecurrenceFrequencyDailyString() %>" maxlength="2" style="font-size: 9pt; font-family: Arial; width: 35px;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Day(s)</span><br>
                                                                    <input type="radio" name="recurrence_pattern_daily" value="2"<%= adminCourseSectionPeriod.getRecurrencePatternDaily() == 2 ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Every Weekday</span>
								</TD>
							</TR>
                                                        <TR BGCOLOR="#EEEEEE">
                                                                <TD><input type="radio" name="recurrence_pattern" value="2"<%= adminCourseSectionPeriod.getRecurrencePattern() == 2 ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Weekly:</span></TD>
								<TD colspan="3"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Recur Every</span>&nbsp;
                                                                    <input type="text" name="recurrence_frequency_weekly" value="<%= adminCourseSectionPeriod.getRecurrenceFrequencyWeeklyString() %>" maxlength="2" style="font-size: 9pt; font-family: Arial; width: 35px;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Week(s) On:</span><br>
                                                                    <input type="checkbox" name="monday"<%= adminCourseSectionPeriod.getRecurMonday() ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Monday</span>&nbsp;
                                                                    <input type="checkbox" name="tuesday"<%= adminCourseSectionPeriod.getRecurTuesday() ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Tuesday</span>&nbsp;
                                                                    <input type="checkbox" name="wednesday"<%= adminCourseSectionPeriod.getRecurWednesday() ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Wednesday</span>&nbsp;
                                                                    <input type="checkbox" name="thursday"<%= adminCourseSectionPeriod.getRecurThursday() ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Thursday</span>&nbsp;
                                                                    <input type="checkbox" name="friday"<%= adminCourseSectionPeriod.getRecurFriday() ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Friday</span><br>
                                                                    <input type="checkbox" name="saturday"<%= adminCourseSectionPeriod.getRecurSaturday() ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Saturday</span>&nbsp;
                                                                    <input type="checkbox" name="sunday"<%= adminCourseSectionPeriod.getRecurSunday() ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">Sunday</span>&nbsp;
								</TD>
							</TR>
                                                        <TR BGCOLOR="#DDDDDD">
                                                                <TD><input type="radio" name="recurrence_pattern" value="3"<%= adminCourseSectionPeriod.getRecurrencePattern() == 3 ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Monthly:</span></TD>
								<TD colspan="3"><input type="radio" name="recurrence_pattern_monthly" value="1"<%= adminCourseSectionPeriod.getRecurrencePatternMonthly() == 1 ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Day</span>&nbsp;
                                                                    <input type="text" name="recurrence_day_of_month" value="<%= adminCourseSectionPeriod.getRecurrenceDayOfMonthString() %>" maxlength="2" style="font-size: 9pt; font-family: Arial; width: 35px;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;of every&nbsp;</span><input type="text" name="recurrence_frequency_monthly" value="<%= adminCourseSectionPeriod.getRecurrenceFrequencyMonthlyString() %>" maxlength="2" style="font-size: 9pt; font-family: Arial; width: 35px;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Month(s)&nbsp;</span><br>
                                                                    <input type="radio" name="recurrence_pattern_monthly" value="2"<%= adminCourseSectionPeriod.getRecurrencePatternMonthly() == 2 ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;The</span>&nbsp;
                                                                    <select name="recurrence_month_sel" style="font-size: 9pt; font-family: Arial; width: 80px;">
                                                                        <option value="1"<%= adminCourseSectionPeriod.getRecurrenceMonthSel() == 1 ? " SELECTED" : "" %>>first</option>
                                                                        <option value="2"<%= adminCourseSectionPeriod.getRecurrenceMonthSel() == 2 ? " SELECTED" : "" %>>second</option>
                                                                        <option value="3"<%= adminCourseSectionPeriod.getRecurrenceMonthSel() == 3 ? " SELECTED" : "" %>>third</option>
                                                                        <option value="4"<%= adminCourseSectionPeriod.getRecurrenceMonthSel() == 4 ? " SELECTED" : "" %>>fourth</option>
                                                                        <option value="5"<%= adminCourseSectionPeriod.getRecurrenceMonthSel() == 5 ? " SELECTED" : "" %>>last</option>
                                                                    </select>&nbsp;
                                                                    <select name="recurrence_month_week_day" style="font-size: 9pt; font-family: Arial; width: 100px;">
                                                                        <option value="0"<%= adminCourseSectionPeriod.getRecurrenceMonthWeekDay() == 0 ? " SELECTED" : "" %>>day</option>
                                                                        <option value="<%= Calendar.MONDAY %>"<%= adminCourseSectionPeriod.getRecurrenceMonthWeekDay() == Calendar.MONDAY ? " SELECTED" : "" %>>Monday</option>
                                                                        <option value="<%= Calendar.TUESDAY %>"<%= adminCourseSectionPeriod.getRecurrenceMonthWeekDay() == Calendar.TUESDAY ? " SELECTED" : "" %>>Tuesday</option>
                                                                        <option value="<%= Calendar.WEDNESDAY %>"<%= adminCourseSectionPeriod.getRecurrenceMonthWeekDay() == Calendar.WEDNESDAY ? " SELECTED" : "" %>>Wednesday</option>
                                                                        <option value="<%= Calendar.THURSDAY %>"<%= adminCourseSectionPeriod.getRecurrenceMonthWeekDay() == Calendar.THURSDAY ? " SELECTED" : "" %>>Thursday</option>
                                                                        <option value="<%= Calendar.FRIDAY %>"<%= adminCourseSectionPeriod.getRecurrenceMonthWeekDay() == Calendar.FRIDAY ? " SELECTED" : "" %>>Friday</option>
                                                                        <option value="<%= Calendar.SATURDAY %>"<%= adminCourseSectionPeriod.getRecurrenceMonthWeekDay() == Calendar.SATURDAY ? " SELECTED" : "" %>>Saturday</option>
                                                                        <option value="<%= Calendar.SUNDAY %>"<%= adminCourseSectionPeriod.getRecurrenceMonthWeekDay() == Calendar.SUNDAY ? " SELECTED" : "" %>>Sunday</option>
                                                                    </select>
                                                                    <span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;of every&nbsp;</span><input type="text" name="recurrence_frequency_monthly_alt" value="<%= adminCourseSectionPeriod.getRecurrenceFrequencyMonthlyAltString() %>" maxlength="2" style="font-size: 9pt; font-family: Arial; width: 35px;"><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Month(s)&nbsp;</span>
								</TD>
							</TR>
                                                        <TR BGCOLOR="#EEEEEE">
                                                                <TD><input type="radio" name="recurrence_pattern" value="4"<%= adminCourseSectionPeriod.getRecurrencePattern() == 4 ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Yearly</span></TD>
								<TD colspan="3"><input type="radio" name="recurrence_pattern_yearly" value="1"<%= adminCourseSectionPeriod.getRecurrencePatternYearly() == 1 ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Every</span>&nbsp;
                                                                    <select name="recurrence_year_month" style="font-size: 9pt; font-family: Arial; width: 80px;">
                                                                        <option value="<%= Calendar.JANUARY %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.JANUARY ? " SELECTED" : "" %>>January</option>
                                                                        <option value="<%= Calendar.FEBRUARY %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.FEBRUARY ? " SELECTED" : "" %>>February</option>
                                                                        <option value="<%= Calendar.MARCH %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.MARCH ? " SELECTED" : "" %>>March</option>
                                                                        <option value="<%= Calendar.APRIL %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.APRIL ? " SELECTED" : "" %>>April</option>
                                                                        <option value="<%= Calendar.MAY %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.MAY ? " SELECTED" : "" %>>May</option>
                                                                        <option value="<%= Calendar.JUNE %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.JUNE ? " SELECTED" : "" %>>June</option>
                                                                        <option value="<%= Calendar.JULY %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.JULY ? " SELECTED" : "" %>>July</option>
                                                                        <option value="<%= Calendar.AUGUST %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.AUGUST ? " SELECTED" : "" %>>August</option>
                                                                        <option value="<%= Calendar.SEPTEMBER %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.SEPTEMBER ? " SELECTED" : "" %>>September</option>
                                                                        <option value="<%= Calendar.OCTOBER %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.OCTOBER ? " SELECTED" : "" %>>October</option>
                                                                        <option value="<%= Calendar.NOVEMBER %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.NOVEMBER ? " SELECTED" : "" %>>November</option>
                                                                        <option value="<%= Calendar.DECEMBER %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonth() == Calendar.DECEMBER ? " SELECTED" : "" %>>December</option>
                                                                    </select>&nbsp;
                                                                    <input type="text" name="recurrence_year_date" value="<%= adminCourseSectionPeriod.getRecurrenceYearDateString() %>" maxlength="2" style="font-size: 9pt; font-family: Arial; width: 35px;"><br>
                                                                    <input type="radio" name="recurrence_pattern_yearly" value="2"<%= adminCourseSectionPeriod.getRecurrencePatternYearly() == 2 ? " checked" : "" %>><span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;The</span>&nbsp;
                                                                    <select name="recurrence_year_sel" style="font-size: 9pt; font-family: Arial; width: 80px;">
                                                                        <option value="1"<%= adminCourseSectionPeriod.getRecurrenceYearSel() == 1 ? " SELECTED" : "" %>>first</option>
                                                                        <option value="2"<%= adminCourseSectionPeriod.getRecurrenceYearSel() == 2 ? " SELECTED" : "" %>>second</option>
                                                                        <option value="3"<%= adminCourseSectionPeriod.getRecurrenceYearSel() == 3 ? " SELECTED" : "" %>>third</option>
                                                                        <option value="4"<%= adminCourseSectionPeriod.getRecurrenceYearSel() == 4 ? " SELECTED" : "" %>>fourth</option>
                                                                        <option value="5"<%= adminCourseSectionPeriod.getRecurrenceYearSel() == 5 ? " SELECTED" : "" %>>last</option>
                                                                    </select>&nbsp;
                                                                    <select name="recurrence_year_week" style="font-size: 9pt; font-family: Arial; width: 100px;">
                                                                        <option value="0"<%= adminCourseSectionPeriod.getRecurrenceYearWeek() == 0 ? " SELECTED" : "" %>>day</option>
                                                                        <option value="<%= Calendar.MONDAY %>"<%= adminCourseSectionPeriod.getRecurrenceYearWeek() == Calendar.MONDAY ? " SELECTED" : "" %>>Monday</option>
                                                                        <option value="<%= Calendar.TUESDAY %>"<%= adminCourseSectionPeriod.getRecurrenceYearWeek() == Calendar.TUESDAY ? " SELECTED" : "" %>>Tuesday</option>
                                                                        <option value="<%= Calendar.WEDNESDAY %>"<%= adminCourseSectionPeriod.getRecurrenceYearWeek() == Calendar.WEDNESDAY ? " SELECTED" : "" %>>Wednesday</option>
                                                                        <option value="<%= Calendar.THURSDAY %>"<%= adminCourseSectionPeriod.getRecurrenceYearWeek() == Calendar.THURSDAY ? " SELECTED" : "" %>>Thursday</option>
                                                                        <option value="<%= Calendar.FRIDAY %>"<%= adminCourseSectionPeriod.getRecurrenceYearWeek() == Calendar.FRIDAY ? " SELECTED" : "" %>>Friday</option>
                                                                        <option value="<%= Calendar.SATURDAY %>"<%= adminCourseSectionPeriod.getRecurrenceYearWeek() == Calendar.SATURDAY ? " SELECTED" : "" %>>Saturday</option>
                                                                        <option value="<%= Calendar.SUNDAY %>"<%= adminCourseSectionPeriod.getRecurrenceYearWeek() == Calendar.SUNDAY ? " SELECTED" : "" %>>Sunday</option>
                                                                    </select>
                                                                    <span style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;of&nbsp;</span>
                                                                    <select name="recurrence_year_month_sel" style="font-size: 9pt; font-family: Arial; width: 80px;">
                                                                        <option value="<%= Calendar.JANUARY %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.JANUARY ? " SELECTED" : "" %>>January</option>
                                                                        <option value="<%= Calendar.FEBRUARY %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.FEBRUARY ? " SELECTED" : "" %>>February</option>
                                                                        <option value="<%= Calendar.MARCH %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.MARCH ? " SELECTED" : "" %>>March</option>
                                                                        <option value="<%= Calendar.APRIL %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.APRIL ? " SELECTED" : "" %>>April</option>
                                                                        <option value="<%= Calendar.MAY %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.MAY ? " SELECTED" : "" %>>May</option>
                                                                        <option value="<%= Calendar.JUNE %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.JUNE ? " SELECTED" : "" %>>June</option>
                                                                        <option value="<%= Calendar.JULY %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.JULY ? " SELECTED" : "" %>>July</option>
                                                                        <option value="<%= Calendar.AUGUST %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.AUGUST ? " SELECTED" : "" %>>August</option>
                                                                        <option value="<%= Calendar.SEPTEMBER %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.SEPTEMBER ? " SELECTED" : "" %>>September</option>
                                                                        <option value="<%= Calendar.OCTOBER %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.OCTOBER ? " SELECTED" : "" %>>October</option>
                                                                        <option value="<%= Calendar.NOVEMBER %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.NOVEMBER ? " SELECTED" : "" %>>November</option>
                                                                        <option value="<%= Calendar.DECEMBER %>"<%= adminCourseSectionPeriod.getRecurrenceYearMonthSel() == Calendar.DECEMBER ? " SELECTED" : "" %>>December</option>
                                                                    </select>
								</TD>
							</TR>
							<TR HEIGHT=2 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;<nobr>Start Date:</nobr></P></TD>
								<TD>
									    <input type="text" name="period_start_date" value="<%= (adminCourseSectionPeriod.getPeriodStartDate() == null) ? adminCourseSection.getStartDateString() : CUBean.getUserDateString(adminCourseSectionPeriod.getPeriodStartDate()) %>" style="font-size: 9pt; font-family: Arial; width: 80px;" maxlength="10" onfocus="getDate('courseSectionPeriodForm','period_start_date');">
								</TD>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;<nobr>End Date:</nobr></P></TD>
								<TD>
									    <input type="text" name="period_end_date" value="<%= (adminCourseSectionPeriod.getPeriodEndDate() == null) ? adminCourseSection.getEndDateString() : CUBean.getUserDateString(adminCourseSectionPeriod.getPeriodEndDate()) %>" style="font-size: 9pt; font-family: Arial; width: 80px;" maxlength="10" onfocus="getDate('courseSectionPeriodForm','period_end_date');">
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=4><INPUT TYPE="submit" NAME="createQuestion" VALUE="Save Period"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
<%
}
%>
			
			<!-- Begin Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Sections</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=40 BGCOLOR="#FFFFFF">
								<TD ALIGN=left COLSPAN=6><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;To edit a question, click on the question text. The correct answer is displayed in <B>bold</B> text.<BR>&nbsp;To adjust the ordering of a question, click on the appropriate arrow next to the question.</P></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=3 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=467 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
try
{
    // get the sections assigned to this course
    Iterator sections = CourseSectionBean.getSections(adminCourse).iterator();
    if (sections.hasNext())
    {
        while (sections.hasNext())
        {
            CourseSectionBean section_obj = (CourseSectionBean)sections.next();
%>
							<!-- Begin Audience Entry -->
							<TR>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-course-section.jsp?section_id=<%= section_obj.getValue() %>" style="color: #000000"><%= section_obj.getLabel() %></A></P></TD>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=1&moveUpId=1"><IMG SRC="../images/PttrsnUK_BTN-ArrowUp.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Up"></A></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=1&moveDownId=1"><IMG SRC="../images/PttrsnUK_BTN-ArrowDown.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Down"></A></TD>
								<TD ALIGN=left><A HREF="javascript:if (confirm('Are you sure?')) location = 'admin-assessment-questions.jsp?id=1&deleteId=1'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Question"></A></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#EEEEEE">
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #000000;">Course Is: <b>hmmm</b><br>Complete By: <b>hmmm</b></P></TD>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<!-- End Audience Entry -->
<%
        }
    }
    else
    {
%>
                                                        <TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
                                                        <TR>
								<TD ALIGN=left COLSPAN=8><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Sections Found</p></TD>
							</TR>
                                                        <TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
    }
}
catch (Exception x)
{
}
%>
							<TR>
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Assessment Questions Area -->
		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>