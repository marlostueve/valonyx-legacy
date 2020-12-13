<%@ page contentType="text/html" import="java.util.*, java.net.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.tasks.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="courseBean" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>
<%@ include file="channels/channel-permissions-tablet.jsp" %>

<%
boolean update = false;
String session_sync_string = (String)session.getAttribute("sync-course");
boolean sync_started = ((session_sync_string != null) && (session_sync_string.equals("true")));
boolean is_connected = CUBean.isConnected();

try
{
    int courseId = Integer.parseInt(request.getParameter("id"));
    courseBean = CourseBean.getCourse(courseId);
    session.setAttribute("courseBean", courseBean);
}
catch (Exception x)
{
}

if (request.getParameter("update") != null)
{
    if (request.getParameter("update").equals("true"))
    {
	update = true;
	session.setAttribute("course-update", "true");
    }
}

if (session.getAttribute("course-update") != null)
{
    String update_str = (String)session.getAttribute("course-update");
    if (update_str.equals("true"))
	update = true;
}

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
		<script language="JavaScript" type="text/JavaScript">
		    <!--

			if (document.images) {
			Rollimage = new Array();

			Rollimage[0]= new Image(32,32);
			Rollimage[0].src = "images/gfxLoading-error.gif";
			Rollimage[1] = new Image(32,32);
			Rollimage[1].src = "images/gfxLoading-complete.gif";
			Rollimage[2] = new Image(32,32);
			Rollimage[2].src = "images/gfxLoading-working.gif";
			}

			function SwapError(x){
			  if (document.images)
				eval("document.m" + x + ".src = Rollimage[0].src");
			  return true;
			}
			
			function SwapComplete(x){
			  if (document.images)
				eval("document.m" + x + ".src = Rollimage[1].src");
			  return true;
			}
			
			function SwapWorking(x){
			  if (document.images)
				eval("document.m" + x + ".src = Rollimage[2].src");
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
					<p class="headline"><%= update ? "Update" : "Download" %> Activity</p>
<%
if (is_connected)
{
    if (sync_started)
    {
%>
					<p><span id="head_sync"><%= update ? "Updating" : "Downloading" %>...</span></p>
					<struts-html:form action="/synchronizeCourse">
						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input type="hidden" name="status" value="sync"></div>
							<div class="end"></div>
						</div>
					</struts-html:form>
<%
    }
    else
    {
%>
					<p>Please click <strong><%= update ? "Update" : "Download" %></strong> to begin.</p>
					<struts-html:form action="/synchronizeCourse">
						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input class="formbutton" type="submit" value="<%= update ? "Update" : "Download" %>" alt="<%= update ? "Update" : "Download" %>" /><input type="hidden" name="status" value="sync"></div>
							<div class="end"></div>
						</div>
					</struts-html:form>
<%
    }
}
else
{
%>
					<p>Unable to communicate with the server.  Please connect to the Internet and click <strong>Retry</strong>.</p>
					<struts-html:form action="/synchronizeCourse">
						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input class="formbutton" type="submit" value="Retry" alt="Retry" /><input type="hidden" name="status" value="retry"></div>
							<div class="end"></div>
						</div>
					</struts-html:form>
<%
}
%>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>


				<div class="content_Synchronize">
					<div class="statusColumn">
<%
if (!sync_started)
{
%>
						<div class="headline"><img src="images/gfxLoading-stopped.gif" width="32" height="32" title="" alt="" name="m1" /><%= update ? "Update" : "Download" %> Progress</div>
<%
}
else
{
%>
						<div class="headline"><img src="images/gfxLoading-working.gif" width="32" height="32" title="" alt="" name="m2" /><%= update ? "Update" : "Download" %> Progress</div>
<%
}
%>
						<!-- <div class="headline"><img src="images/gfxLoading-working.gif" width="24" height="24" title="" alt="" />Activities&nbsp;&#47;&nbsp;Resources</div> -->
						<!-- <div class="headline"><img src="images/gfxLoading-complete.gif" width="24" height="24" title="" alt="" />Activities&nbsp;&#47;&nbsp;Resources</div> -->
						<!-- <div class="headline"><img src="images/gfxLoading-error.gif" width="24" height="24" title="" alt="" />Activities&nbsp;&#47;&nbsp;Resources</div> -->
						<div class="status"><span id="activity_1">&nbsp;</span></div>
						<div class="status"><span id="activity_2">&nbsp;</span></div>
						<div class="status"><span id="activity_3">&nbsp;</span></div>
						<div class="status"><span id="activity_4">&nbsp;</span></div>
						<div class="status"><span id="activity_5">&nbsp;</span></div>
						<div class="status"><span id="activity_6">&nbsp;</span></div>
						<div class="status"><span id="activity_7">&nbsp;</span></div>
					</div>
					<div class="end"></div>
				</div>




				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="channels\channel-footer.jsp" %>

		</div>

	</body>

</html>

<%
if (sync_started)
{
    int id_index = 1;
%>
<script type="text/javascript" language="JavaScript">
  <!--
  SwapWorking(2);
  document.getElementById('activity_<%= id_index %>').innerHTML = '<%= update ? "Updating" : "Downloading" %> Activity...';
  // -->
</script>
<%
    id_index++;
    //out.flush();
    //response.flushBuffer();
    
    try
    {
	boolean in_audience = false;
	Iterator audienceItr = courseBean.getAudiences().iterator();
	while (audienceItr.hasNext())
	{
	    CourseAudienceBean course_audience = (CourseAudienceBean)audienceItr.next();
	    AudienceBean audience = course_audience.getAudience();
	    if (audience.contains(loginBean.getPerson()))
	    {
		in_audience = true;
		break;
	    }
	}
	if (!in_audience)
	    throw new Exception("Cannot " + (update ? "update" : "download") + ".  You're not in the audience for this course.");
	
	String realPath = System.getProperty("cu.realPath");
	String coursesFolder = System.getProperty("cu.coursesFolder");

	CUBean.createDirectory(realPath, coursesFolder + courseBean.getIdString());
		// ensure that an upload directory exists for this course
	String courseFolder = realPath + coursesFolder + courseBean.getIdString() + "/";
	
	Iterator itr = courseBean.getFiles().iterator();
	int i = 0;
	while (itr.hasNext())
	{
	    CourseFile course_file = (CourseFile)itr.next();
	    boolean fetch_file = false;
	    if (update)
		fetch_file = courseBean.isFileOutOfDate(course_file);
	    else
		fetch_file = courseBean.isFileMissing(course_file);
	    if (fetch_file)
	    {
		i++;
		String course_url_string = course_file.getUrl();
		int folder_index = course_url_string.lastIndexOf('/');
		if (folder_index == -1)
		    course_url_string.lastIndexOf('\\');
		if (folder_index > -1)
		{
		    // ensure that this folder is created...
		    CUBean.createDirectory(courseFolder, course_url_string.substring(0, folder_index));
		}
%>
<script type="text/javascript" language="JavaScript">
  <!--
  document.getElementById('activity_<%= id_index %>').innerHTML = '<%= update ? "Updating" : "Downloading" %> File <%= course_url_string %>...';
  // -->
</script>
<%
		//out.flush();
		//response.flushBuffer();
		String url_string = "http://" + System.getProperty("cu.host") + ":" + System.getProperty("cu.port") + "/" + System.getProperty("cu.clientPackageName") + coursesFolder + courseBean.getIdString() + "/" + course_url_string;
		//URL url = new URL(java.net.URLEncoder.encode(url_string));
		URL url = new URL(url_string.replace(" ", "%20"));
		InputStream in_stream = url.openStream();
		BufferedInputStream in = new BufferedInputStream(in_stream);
		CUBean.createFile(in, new BufferedOutputStream(new FileOutputStream(courseFolder + course_url_string)));
		
		
	    }
	}
	
	// download index files also
		
	Iterator index_file_names_itr = UKOnlineResourceSearchBean.getIndexFileNames().iterator();
	while (index_file_names_itr.hasNext())
	{
	    String index_file = (String)index_file_names_itr.next();
	    System.out.println("index file >" + index_file);
	    String url_string = "http://" + System.getProperty("cu.host") + ":" + System.getProperty("cu.port") + "/" + System.getProperty("cu.clientPackageName") + System.getProperty("cu.resourcesFolder") + "index/" + index_file;
	    URL url = new URL(url_string.replace(" ", "%20"));
	    InputStream in_stream = url.openStream();
	    BufferedInputStream in = new BufferedInputStream(in_stream);
	    CUBean.createFile(in, new BufferedOutputStream(new FileOutputStream(realPath + System.getProperty("cu.resourcesFolder") + "index/" + index_file)));
	}
%>
<script type="text/javascript" language="JavaScript">
  <!--
  document.getElementById('activity_<%= id_index %>').innerHTML = '<%= i %> File<%= (i == 1) ? "" : "s" %> <%= update ? "Updated" : "Downloaded" %>';
  // -->
</script>
<%
	//out.flush();
	//response.flushBuffer();
	id_index++;
%>
<script type="text/javascript" language="JavaScript">
  <!--
  document.getElementById('activity_<%= id_index %>').innerHTML = '<%= update ? "Update" : "Download" %> Complete';
  document.getElementById('activity_<%= id_index + 1 %>').innerHTML = 'Installing Activity';
  document.getElementById('activity_<%= id_index + 2 %>').innerHTML = '<strong>Installation Complete</strong>';
  document.getElementById('head_sync').innerHTML = '<strong><%= update ? "Update" : "Download" %> Complete</strong>';
  SwapComplete(2);
  // -->
</script>
<%
	//out.flush();
	//response.flushBuffer();
    }
    catch (Exception x)
    {
	x.printStackTrace();
%>
<script type="text/javascript" language="JavaScript">
  <!--
  document.getElementById('activity_<%= id_index %>').innerHTML = 'Error : <%= x.getMessage() %>';
  SwapError(2);
  // -->
</script>
<%
       //out.flush();
       //response.flushBuffer();
    }
    
    session.removeAttribute("course-update");
}
%>



<%
session.removeAttribute("sync-course");
%>