<%@ page contentType="text/html" import="java.util.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();

Calendar now = Calendar.getInstance();

if (request.getParameter("id") != null)
{
    try
    {
        int courseId = Integer.parseInt(request.getParameter("id"));
        userCourse = CourseBean.getCourse(courseId);
        session.setAttribute("userCourse", userCourse);
    }
    catch (NumberFormatException x)
    {
        //errorMessage = x.getMessage();
    }
}

boolean is_connected = false;
boolean has_missing_files = userCourse.hasMissingFiles();
boolean use_content_dir = false;

if (!CUBean.isMasterServer())
{
    // tablet
    
    is_connected = CUBean.isConnected();
    if (has_missing_files)
    {
	// see if the course exists in the content dir
	
	String file_path = System.getProperty("cu.tabletContentPath") + "\\courses\\" + userCourse.getIdString();
	File test_file = new File(file_path);
	has_missing_files = !test_file.exists();
	if (!has_missing_files)
	    use_content_dir = true;
    }
}

String sortBy = EnrollmentRefPeer.ENROLLMENTDATE;
boolean desc_sort = true;

HashMap completed_courses = new HashMap(11);
Iterator itr = person.getCompletedEnrollments().iterator();
while (itr.hasNext())
{
    EnrollmentBean enrollment = (EnrollmentBean)itr.next();
    CourseBean course = enrollment.getCourse();
    completed_courses.put(course, enrollment);
}

Vector curriculum_groups = CourseGroupBean.getCourseGroups(CompanyBean.getInstance(), "Curriculum");
Vector course_groups_to_display = new Vector();
Vector lesson_groups_to_display = new Vector();

HashMap grouped_courses = new HashMap(11);

itr = userCourse.getCoursePrerequisites().iterator();
while (itr.hasNext())
{
    //EnrollmentBean enrollment = (EnrollmentBean)itr.next();
    //CourseBean course = enrollment.getCourse();
    CourseBean course = (CourseBean)itr.next();
    if (!completed_courses.containsKey(course))
    {
	try
	{
	    CourseGroupBean course_group = course.getCourseGroup();
	    Vector vec = (Vector)grouped_courses.get(course_group);
	    if (vec == null)
	    {
		vec = new Vector();
		grouped_courses.put(course_group, vec);
	    }
	    vec.addElement(course);

	    System.out.println("lesson group to display >" + course_group.getName());
	    lesson_groups_to_display.addElement(course_group);
	    
	    CourseGroupBean parent_group = course_group.getParent();
	    System.out.println("course group to display >" + parent_group.getName());
	    course_groups_to_display.addElement(course_group.getParent());
	}
	catch (ObjectNotFoundException group_not_found)
	{
	}
    }
}

boolean has_incomplete_prereqs = (grouped_courses.size() > 0);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>

		<title>Ecolab</title>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="generator" content="TextPad 4.6.2" />
		<meta name="author" content="" />
		<meta name="copyright" content="&copy; 2007" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />

		<link rel="stylesheet" type="text/css" href="css/web.css" />
		<struts-html:javascript formName="indexSearchForm" dynamicJavascript="true" staticJavascript="false"/>
		<script language="JavaScript" type="text/JavaScript">
		    <!--

			if (document.images) {
			Rollimage = new Array();

			Rollimage[0]= new Image(17,17);
			Rollimage[0].src = "images/btnContract.gif";
			Rollimage[1] = new Image(17,17);
			Rollimage[1].src = "images/btnExpand.gif";
			}

			function SwapOut(x){
			  if (document.images)
			    eval("(document.m" + x + ".src.indexOf(\"btnContract.gif\") > -1) ? document.m" + x + ".src = Rollimage[1].src : document.m" + x + ".src = Rollimage[0].src");
			  return true;
			}

		    //-->
		</script>

	</head>

	<body>

		<div id="main">

<%@ include file="channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Activity Details</p>
					<p>This screen lists the details for the activity you selected.</p>
<p>Click the appropriate link on the right to begin the activity. The link will be blue (active) if
you currently <strong>can</strong> complete that action, or gray (inactive) if you <strong>cannot</strong>. For example, if
you want to download an activity from the Internet, the link will be blue if you are on-line,
and gray if you are not.</p>
<p>If <strong>all</strong> items in the list on the right are grayed out, then look at the prerequisites. If
prerequisites are listed, then you must complete those activities before you begin this one.
If an activity is listed but grayed out, then you must take other activities first. Click any
active activity or resource link to go to the Details screen for that activity or resource.</p>
<p><strong>Note:</strong> To enroll in an activity that is not required, click <strong>Enroll</strong>, <strong>Download Activity</strong>, or <strong>Open
Activity</strong> on the right. The activity will be moved to your Home page.  If there are no active links, contact your supervisor to enroll.</p>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Details">
					<div class="subnav">
<%
boolean can_enroll = person.canEnrollIn(userCourse);
boolean is_enrolled = person.isEnrolled(userCourse);
System.out.println("can_enroll >" + can_enroll);
System.out.println("is_enrolled >" + is_enrolled);
if (can_enroll && !is_enrolled && !has_incomplete_prereqs)
{
%>
						<a href="javascript:if (confirm('Are you sure?')) document.forms[1].submit();" title="Enroll">Enroll</a>
<%
}
else
{
%>
						<span>Enroll</span>
<%
}
if (!CUBean.isMasterServer())
{
    if (is_connected && has_missing_files)
    {
%>
						<a href="synchronize-course.jsp?id=<%= userCourse.getId() %>" title="Download Activity">Download Activity</a>
<%
    }
    else
    {
%>
						<span>Download Activity</span>
<%
    }
    if (userCourse.hasOutOfDateFiles())
    {
%>
						<a href="synchronize-course.jsp?id=<%= userCourse.getId() %>&update=true" title="Update Activity">Update Activity</a>
<%
    }
    else
    {
%>
						<span>Update Activity</span>
<%
    }
    //String course_cd_location = userCourse.getCourseCDLocationIfAny();
    String course_cd_location = null;
    if (course_cd_location == null)
    {
%>
						<span>Load&#47;Update Activity From CD</span>
<%
    }
    else
    {
%>
						<a href="synchronize-course.jsp?id=<%= userCourse.getId() %>" title="Load&#47;Update Activity From CD">Load&#47;Update Activity From CD</a>
<%
    }
}

boolean completed = completed_courses.containsKey(userCourse);
if (!has_incomplete_prereqs && !completed && (userCourse.getType().equals(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME) || userCourse.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME)))
{
%>
						<a href="mark-complete.jsp" title="Mark as Completed">Mark as Completed</a>
<%
}
else
{
%>
						<span>Mark as Completed</span>
<%
}

System.out.println("userCourse.getType().equals(\"E-Learning Activity\") >" + userCourse.getType().equals("E-Learning Activity"));
System.out.println("is_enrolled >" + is_enrolled);
System.out.println("has_missing_files >" + has_missing_files);
System.out.println("userCourse.isSCORMCompliant() >" + userCourse.isSCORMCompliant());

boolean allow_open = false;
if (userCourse.getType().equals("E-Learning Activity"))
{
    if (is_enrolled)
    {
	if (!has_missing_files && !has_incomplete_prereqs)
	{
	    if (userCourse.isSCORMCompliant())
	    {
		Iterator sco_itr = userCourse.getSCOs().iterator();
		while (sco_itr.hasNext())
		{
		    allow_open = true;
		    SCORMCourseResourceBean scorm_resource = (SCORMCourseResourceBean)sco_itr.next();
		    CourseWindowSettings window_settings = userCourse.getWindowSettings();
%>
						<a href="#" onclick="javascript:window.open('course.jsp?content=<%= use_content_dir ? "true" : "false" %>&courseId=<%= userCourse.getIdString() %>&scoId=<%= scorm_resource.getId() %>','CourseWin','scrollbars=<%= (window_settings.getDisplayScrollBars() == (short)1) ? "yes" : "no" %>,resizable=<%= (window_settings.getIsResizable() == (short)1) ? "yes" : "no" %>,toolbar=<%= (window_settings.getDisplayToolbar() == (short)1) ? "yes" : "no" %>,location=<%= (window_settings.getDisplayLocation() == (short)1) ? "yes" : "no" %>,directories=no,status=<%= (window_settings.getDisplayStatusBar() == (short)1) ? "yes" : "no" %>,menubar=<%= (window_settings.getDisplayMenuBar() == (short)1) ? "yes" : "no" %>,fullscreen=<%= (window_settings.getDisplayFullscreen() == (short)1) ? "yes" : "no" %>,width=<%= window_settings.getWindowWidth() %>,height=<%= window_settings.getWindowHeight() %>');" title="Open Activity">Open Activity</a>
<%
		}
	    }
	    else
	    {
		allow_open = true;
%>
						<a href="#" onclick="javascript:window.open('course.jsp?courseId=<%= userCourse.getIdString() %>','CourseWin','scrollbars=yes,resizable=yes,toolbar=no,location=yes,directories=no,status=no,menubar=yes,width=520,height=500');" title="Open Activity">Open Activity</a>
<%
	    }
	}
    }

}
else if (userCourse.getType().equals("Assessment Activity"))
{
    if (is_enrolled && !has_incomplete_prereqs)
    {
	try
	{
	    AssessmentBean userAssessment = userCourse.getAssessment();
	    if (userAssessment != null)
	    {
		allow_open = true;
%>
						<a href="assessment.jsp?id=<%= userAssessment.getId() %>" title="Open Activity">Open Activity</a>
<%
	    }
	}
	catch (Exception x)
	{
	    x.printStackTrace();
	}
    }
}
if (!allow_open)
{
%>

						<span>Open Activity</span>
<%
}
%>
						<!-- <a href="#" title="Complete Evaluation">Complete Evaluation</a> -->
						<span>Complete Evaluation</span>
					</div>


					<div class="detailItem">
						<div class="left">TITLE</div>
						<div class="right"><%= userCourse.getLabel() %></div>
						<div class="end"></div>
					</div>
<%
String lesson_string = "[Lesson Not Found]";
String course_string = "[Course Not Found]";
try
{
    CourseGroupBean lesson_group = userCourse.getCourseGroup();
    lesson_string = lesson_group.getNameString();
    course_string = lesson_group.getParent().getNameString();
}
catch (Exception x)
{
}
%>
					<div class="detailItem">
						<div class="left">LESSON</div>
						<div class="right"><%= lesson_string %></div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">COURSE</div>
						<div class="right"><%= course_string %></div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">DESCRIPTION</div>
						<div class="right"><%= userCourse.getComment() %></div>
						<div class="end"></div>
					</div>
<%
String activity_image = "";
try
{
    if (userCourse.getType().equals(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME))
	activity_image = "<img src=\"images/icnActivityMNTR.gif\" width=\"17\" height=\"17\" title=\"Mentor\" alt=\"Mentor\" />";
    else if (userCourse.getType().equals(CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME))
	activity_image = "<img src=\"images/icnActivityELRN.gif\" width=\"17\" height=\"17\" title=\"E-Learning\" alt=\"E-Learning\" />";
    else if (userCourse.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME))
	activity_image = "<img src=\"images/icnActivityILED.gif\" width=\"17\" height=\"17\" title=\"Instructor-Led\" alt=\"Instructor-Led\" />";
    else if (userCourse.getType().equals(CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME))
	activity_image = "<img src=\"images/icnActivityASMT.gif\" width=\"17\" height=\"17\" title=\"Assessment\" alt=\"Assessment\" />";
}
catch (Exception x)
{
}
%>
					<div class="detailItem">
						<div class="left">TYPE</div>
						<div class="right"><%= activity_image %></div>
						<div class="end"></div>
					</div>
<%
if (userCourse.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME))
{
%>
					<div class="detailItem">
						<div class="left">INSTRUCTORS</div>
						<div class="right"><%= userCourse.getInstructorsString("<br />") %></div>
						<div class="end"></div>
					</div>
<%
}
if (completed_courses.containsKey(userCourse))
{
    try
    {
	EnrollmentBean enrollment = (EnrollmentBean)completed_courses.get(userCourse);
%>
					<div class="detailItem">
						<div class="left">SCORE</div>
						<div class="right"><%= enrollment.getScoreString() %></div>
						<div class="end"></div>
					</div>
<%
    }
    catch (Exception x)
    {
    }
}
if ((userCourse.getDurationHours() != 0) || (userCourse.getDurationMinutes() != 0))
{
%>
					<div class="detailItem">
						<div class="left">DURATION</div>
						<div class="right"><%= userCourse.getDurationHours() + "" %> Hours <%= userCourse.getDurationMinutes() + "" %> Minutes</div>
						<div class="end"></div>
					</div>
<%
}
%>
					<div class="detailItem">
						<div class="left">PREREQUISITES</div>
						<div class="right">
<%
if (has_incomplete_prereqs)
{
%>
							This activity has prerequisites that you have not completed:
<%
}
else
{
%>
							This activity has no incomplete prerequisites.
<%
}
if (has_incomplete_prereqs)
{
%>

							<div class="content_DetailsTable">

								<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
								<div class="heading">
									<table cellspacing="0" cellpadding="0" border="0" summary="">
										<tr style="display: block;">
											<td style="width:  20px; text-align: left;  "></td>
											<td style="width: 270px; text-align: left;  ">Title</td>
											<td style="width:  63px; text-align: center;">Type</td>
										</tr>
									</table>
								</div>
								<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
    Iterator curriculum_groups_itr = curriculum_groups.iterator();
    while (curriculum_groups_itr.hasNext())
    {
	CourseGroupBean curriculum_group = (CourseGroupBean)curriculum_groups_itr.next();
	System.out.println("curriculum_group >" + curriculum_group.getName());

	Vector curriculum_courses = (Vector)grouped_courses.get(curriculum_group);
	if (curriculum_courses != null)
	    System.out.println("curriculum_courses >" + curriculum_courses);

	Iterator course_group_children = curriculum_group.getChildren().iterator();
	while (course_group_children.hasNext())
	{
	    CourseGroupBean course_group = (CourseGroupBean)course_group_children.next();
	    System.out.println("course_group >" + course_group.getName());

	    if (course_groups_to_display.contains(course_group))
	    {
		Iterator lesson_group_children = course_group.getChildren().iterator();
%>
								<!-- BEGIN Course -->
								<div class="course">
									<table cellspacing="0" cellpadding="0" border="0" summary="">
										<tr>
<%
		if (lesson_group_children.hasNext())
		{
%>
											<td style="width:  20px; text-align: left;  "><a href="#" onclick="document.getElementById('lessonContainer_<%= course_group.getId() %>').style.display == 'none' ? document.getElementById('lessonContainer_<%= course_group.getId() %>').style.display = 'block' : document.getElementById('lessonContainer_<%= course_group.getId() %>').style.display = 'none'; SwapOut(<%= course_group.getId() %>);" title="" ><img src="images/btnContract.gif" width="17" height="17" title="Contract" alt="" name="m<%= course_group.getId() %>" /></a></td>
<%
		}
		else
		{
%>
											<td style="width:  20px; text-align: left;  "></td>
<%
		}
%>
											<td style="width: 270px; text-align: left;  "><strong><%= course_group.getName() %></strong></td>
											<td style="width:  63px; text-align: center;"></td>
										</tr>
									</table>
									<div class="container" id="lessonContainer_<%= course_group.getId() %>" style="display: block;">
<%
		while (lesson_group_children.hasNext())
		{
		    CourseGroupBean lesson_group = (CourseGroupBean)lesson_group_children.next();
		    System.out.println("lesson_group >" + lesson_group.getName());
		    if (lesson_groups_to_display.contains(lesson_group))
		    {
			Vector lesson_courses = (Vector)grouped_courses.get(lesson_group);
%>
										<!-- BEGIN Lesson -->
										<div class="lesson">
											<table cellspacing="0" cellpadding="0" border="0" summary="">
												<tr>
													<td style="width:  20px; text-align: left;  "></td>
<%
			if (lesson_courses == null)
			{
%>
													<td style="width:  20px; text-align: left;  "></td>
<%
			}
			else
			{
%>
													<td style="width:  20px; text-align: left;  "><a href="#" onclick="document.getElementById('activityContainer_<%= lesson_group.getId() %>').style.display == 'none' ? document.getElementById('activityContainer_<%= lesson_group.getId() %>').style.display = 'block' : document.getElementById('activityContainer_<%= lesson_group.getId() %>').style.display = 'none'; SwapOut(<%= lesson_group.getId() %>);" title=""><img src="images/btnContract.gif" width="17" height="17" title="Contract" alt="" name="m<%= lesson_group.getId() %>" /></a></td>
<%
			}
%>
													<td style="width: 250px; text-align: left;  "><strong><%= lesson_group.getName() %></strong></td>
													<td style="width:  63px; text-align: center;"><img src="images/trspacer.gif" width="17" height="17" alt="" /></td>
												</tr>
											</table>
											<div class="container" id="activityContainer_<%= lesson_group.getId() %>" style="display: block;">
<%
			if (lesson_courses != null)
			{
			    Iterator lesson_courses_itr = lesson_courses.iterator();
			    while (lesson_courses_itr.hasNext())
			    {
				CourseBean course = (CourseBean)lesson_courses_itr.next();
				activity_image = "";
				if (course.getType().equals("Mentor Activity"))
				    activity_image = "<img src=\"images/icnActivityMNTR.gif\" width=\"17\" height=\"17\" title=\"Mentor\" alt=\"Mentor\" />";
				else if (course.getType().equals("E-Learning Activity"))
				    activity_image = "<img src=\"images/icnActivityELRN.gif\" width=\"17\" height=\"17\" title=\"E-Learning\" alt=\"E-Learning\" />";
				else if (course.getType().equals("Instructor-Led Activity"))
				    activity_image = "<img src=\"images/icnActivityILED.gif\" width=\"17\" height=\"17\" title=\"Instructor-Led\" alt=\"Instructor-Led\" />";
%>
												<!-- BEGIN Activity -->
												<div class="activity">
													<table cellspacing="0" cellpadding="0" border="0" summary="">
														<tr>
															<td style="width:  20px; text-align: left;  "></td>
															<td style="width:  20px; text-align: left;  "></td>
															<td style="width:  20px; text-align: left;  "></td>
															<td style="width: 230px; text-align: left;  "><a href="course-detail.jsp?id=<%= course.getId() %>" title=""><%= course.getLabel() %></a></td>
															<td style="width:  63px; text-align: center;"><%= activity_image %></td>
														</tr>
													</table>
												</div>
												<!-- END Activity -->
<%
			    }
			}
%>
											</div>
										</div>
										<!-- END Lesson -->
<%
		    }
		}
%>
									</div>
								</div>
								<!-- END Course -->
<%
	    }
	}
    }

%>
								<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

							</div>
<%
}
%>



						</div>
						<div class="end"></div>
					</div>
<%
String resourceString = "[NO RESOURCE(S) FOUND]";
itr = userCourse.getResources().iterator();
%>
					<div class="detailItem">
						<div class="left">RESOURCES</div>
						<div class="right">
<%
if (itr.hasNext())
{
%>
							This activity has the following resources:
<%
}
else
{
%>
							This activity has no resources.
<%
}
if (itr.hasNext())
{
%>
							<div class="content_DetailsTable">

								<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
								<div class="heading">
									<table cellspacing="0" cellpadding="0" border="0" summary="">
										<tr style="display: block;">
											<td style="width:  20px; text-align: left;  "></td>
											<td style="width: 270px; text-align: left;  ">Title</td>
											<td style="width:  63px; text-align: center;">Type</td>
										</tr>
									</table>
								</div>
								<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
    while (itr.hasNext())
    {
	ResourceBean resourceObj = (ResourceBean)itr.next();
	boolean isNew = false;
	Calendar broadcastDate = Calendar.getInstance();
	broadcastDate.setTime(resourceObj.getEffectiveDate());
	broadcastDate.add(Calendar.DATE, resourceObj.getDisplayAsNewDays());
	if (broadcastDate.after(now))
	    isNew = true;
	String resource_type = resourceObj.getType();
	String icon_string = "";
	String urlString = "resources/" + resourceObj.getURL();
	if (resource_type.equals(ResourceBean.WORD_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourceDOC.gif\" width=\"17\" height=\"17\" title=\"Microsoft Word (DOC)\" alt=\"Microsoft Word (DOC)\" />";
	else if (resource_type.equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourcePDF.gif\" width=\"17\" height=\"17\" title=\"Portable Document Format (PDF)\" alt=\"Portable Document Format (PDF)\" />";
	else if (resource_type.equals(ResourceBean.FLASH_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourceSWF.gif\" width=\"17\" height=\"17\" title=\"Flash (SWF)\" alt=\"Flash (SWF)\" />";
	else if (resource_type.equals(ResourceBean.INTERNET_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourceWEB.gif\" width=\"17\" height=\"17\" title=\"World Wide Web\" alt=\"World Wide Web\" />";
	else if (resource_type.equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourcePPT.gif\" width=\"17\" height=\"17\" title=\"Microsoft Powerpoint (PPT)\" alt=\"Microsoft Powerpoint (PPT)\" />";
	else if (resource_type.equals(ResourceBean.VIDEO_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourceVID.gif\" width=\"17\" height=\"17\" title=\"Video\" alt=\"Video\" />";
	else if (resource_type.equals(ResourceBean.XLS_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourceXLS.gif\" width=\"17\" height=\"17\" title=\"Microsoft Excel (XLS)\" alt=\"Microsoft Excel (XLS)\" />";
	else if (resource_type.equals(ResourceBean.CD_RESOURCE_TYPE))
	{
	    urlString = "";
	    icon_string = "<img src=\"images/icnResourceCD.gif\" width=\"17\" height=\"17\" title=\"Compact Disc (CD)\" alt=\"Compact Disc (CD)\" />";
	}
%>
								<!-- BEGIN Resource -->
								<div class="resource">
									<table cellspacing="0" cellpadding="0" border="0" summary="">
										<tr>
											<td style="width:  20px; text-align: left;  "></td>
											<td style="width: 270px; text-align: left;  "><a href="resource-detail.jsp?id=<%= resourceObj.getId() %>" title=""><%= resourceObj.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></a></td>
											<td style="width:  63px; text-align: center;"><%= icon_string %></td>
										</tr>
									</table>
								</div>
								<!-- END Resource -->
<%
    }
%>

								<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
							</div>
<%
}
%>
						</div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">RELEASED</div>
						<div class="right"><%= userCourse.getReleasedDateString() %></div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">LAST UPDATED</div>
						<div class="right"><%= userCourse.getModificationDateString() %></div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">UPDATE NOTES</div>
						<div class="right"><%= userCourse.getSummary() %></div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">CONTACT</div>
						<div class="right"><%= userCourse.getContactNameString() %></div>
						<div class="end"></div>
					</div>


				</div>


				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div id="content_SimpleSearch">
					<struts-html:form action="/indexSearch" focus="keyword" onsubmit="return validateIndexSearchForm(this);"><input name="keyword" onfocus="select();" value="Search" size="37" maxlength="50" class="inputbox" style="width: 206px; margin-right: 4px;" /><input type="image" src="images/btnSearch.gif" value="Search" alt="Search" style="_margin-top: 1px;" /></struts-html:form>
					<struts-html:form action="/enroll"><input type="hidden" name="course_id" value="<%= userCourse.getId() %>" /></struts-html:form>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="channels\channel-footer.jsp" %>

		</div>

	</body>

</html>