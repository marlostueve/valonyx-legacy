<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<jsp:useBean id="interactionCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="interactionSCO" class="com.badiyan.uk.beans.SCORMCourseResourceBean" scope="session" />
<jsp:useBean id="interactionEnrollment" class="com.badiyan.uk.beans.EnrollmentBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%
   
if (!session.isNew())
{
       /*
    System.out.println("courseId >" + request.getParameter("courseId"));
    System.out.println("enrollmentId >" + request.getParameter("enrollmentId"));
    System.out.println("scoId >" + request.getParameter("scoId"));
     */

    boolean use_content_dir = false;
    if ((request.getParameter("content") != null) && (request.getParameter("content").equals("true")))
	use_content_dir = true;

    if (request.getParameter("scoId") != null)
    {
	try
	{
	    int scoId = Integer.parseInt(request.getParameter("scoId"));
	    interactionSCO = SCORMCourseResourceBean.getResource(scoId);
	    session.setAttribute("interactionSCO", interactionSCO);
	}
	catch (NumberFormatException x)
	{
	}
    }

    if (request.getParameter("courseId") != null)
    {
	try
	{
	    int courseId = Integer.parseInt(request.getParameter("courseId"));
	    interactionCourse = CourseBean.getCourse(courseId);
	    session.setAttribute("interactionCourse", interactionCourse);

	    interactionEnrollment = loginBean.getPerson().getEnrollment(interactionCourse, true);
	    session.setAttribute("interactionEnrollment", interactionEnrollment);

	    // Record an interaction for this course

	    if (request.getParameter("scoId") != null)
		interactionEnrollment.recordInteraction(interactionCourse, interactionSCO);
	    else
		interactionEnrollment.recordInteraction(interactionCourse);

	    session.setAttribute("recordInteraction", "true");

	}
	catch (NumberFormatException x)
	{
	}
    }

    /*
    System.out.println("interactionCourse.isSCORMCompliant() >" + interactionCourse.isSCORMCompliant());
     */

%>

<!--
	Template used by CourseMainFrame servlet
	Contains top-level course framesets
	Prepared by: Randy Marchessault, Badiyan Productions, Inc.
	Created: 7/8/1999
	Last Revised: 3/31/2007 - Marlo Stueve, Badiyan Productions, Inc.
-->

<html>
<head>
<title><%= interactionCourse.getNameString() %></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script LANGUAGE="JavaScript">
<%
    if (interactionCourse.isSCORMCompliant())
    {
	String sco_url = null;
	if (request.getParameter("scoId") != null)
	{
	    if (use_content_dir)
		sco_url = "/content/courses/" + interactionCourse.getIdString() + "/" + interactionSCO.getHref();
	    else
		sco_url = "/" + System.getProperty("cu.clientPackageName") + "/courses/" + interactionCourse.getIdString() + "/" + interactionSCO.getHref();
	}
	else
	    sco_url = interactionCourse.getUrl();
	System.out.println("interactionCourse.getUrl() >" + interactionCourse.getUrl());
	System.out.println("interactionCourse.getUrlString(loginBean.getPerson()) >" + interactionCourse.getUrlString(loginBean.getPerson()));
%>
// Ensure that we don't load into a frame...
if (top != self)
{
    top.location = location;
}

window.defaultStatus = "Universal Knowlege";

var API = null;

function initScormAPI()
{
    API = window.AppletFrame.document.APIAdapter;
    content.location.href='/<%= System.getProperty("cu.clientPackageName") %>/scorm/launch.x?aicc_sid=<%= loginBean.getPerson().getId() %>.<%= interactionCourse.getId() %>.<%= interactionEnrollment.getId() %>.<%= interactionSCO.getId() %>&lessonUrl=<%= sco_url %>';
}

<%
    }
%>
</script>
</head>
<%
    if (interactionCourse.isSCORMCompliant())
    {
%>
<frameset rows="*,100%" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0" frameborder="NO" border="0" bordercolor="ffffff">
    <frame id="AppletFrame" name="AppletFrame" src="scorm_applet.jsp"  scrolling="NO" TOPMARGIN=0 LEFTMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 FRAMEBORDER="no" BORDER="0" noresize>
    <frame src="about:blank" name="content" scrolling="AUTO" TOPMARGIN=0 LEFTMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 FRAMEBORDER="no" BORDER="0">
<%
    }
    else
    {
%>
<frameset border="0" frameborder="NO" framespacing="0" rows="*">
    <frame src="courses/<%= interactionCourse.getId() %>/<%= interactionCourse.getUrlString(loginBean.getPerson()) %>" name="CourseTopFrame" margin="2" marginwidth="2" marginheight="2" scrolling="YES" noresize>
<%
    }
%>
</frameset>
<noframes>Viewing this page requires a browser capable of displaying frames.</noframes>
<body onBlur="self.focus()">
</body>
</html>
<%
}
else
{
%>
<html>
<title>Ecolab LMS</title>
<body onload="javascript:window.opener.location.reload();window.close();">
</body>
</html>
<%
}
%>