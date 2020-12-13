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
			var httpRequest;
			var mTimer;
			var global_line_number = 1;
			
			function downloadInit()
			{
			    hideLayer('syncbutton');
			    SwapWorking(2);
			    document.getElementById('head_sync').innerHTML = '<%= update ? "Updat" : "Download" %>ing...';
			    download();
			}
			
			function download()
			{
			    if (window.ActiveXObject)
				httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
			    else if (window.XMLHttpRequest)
				httpRequest = new XMLHttpRequest();

			    httpRequest.open("POST", 'http://localhost:<%= System.getProperty("cu.localPort") %>/<%= System.getProperty("cu.clientPackageName") %>/ProcessCourseDownloadServlet.html', true);
			    httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
			    httpRequest.onreadystatechange = function() {processDownload(); } ;
			    httpRequest.send('command=<%= update ? "update" : "download" %>');
			}
			
			function processDownload()
			{
			    if (httpRequest.readyState == 4)
			    {
				var killTimer = false;
				if(httpRequest.status == 200)
				{
				    if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];
					eval("document.getElementById('activity_" + global_line_number++ + "').innerHTML = xml_response.childNodes[0].nodeValue");
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("download").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("download")[0];
					eval("document.getElementById('activity_" + global_line_number + "').innerHTML = xml_response.childNodes[0].nodeValue");
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("complete").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("complete")[0];
					eval("document.getElementById('activity_" + global_line_number++ + "').innerHTML = xml_response.childNodes[0].nodeValue");
					document.getElementById('head_sync').innerHTML = '<strong><%= update ? "Update" : "Download" %> Complete</strong>';
					SwapComplete(2);
					killTimer = true;
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("error").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("error")[0];
					eval("document.getElementById('activity_" + global_line_number++ + "').innerHTML = 'Error : ' + xml_response.childNodes[0].nodeValue");
					SwapError(2);
					killTimer = true;
				    }
				}
				else
				{
				    eval("document.getElementById('activity_" + global_line_number++ + "').innerHTML = 'Error : ' + httpRequest.status + ' : ' + httpRequest.statusText");
				    SwapError(2);
				    killTimer = true;
				}
				
				if (!killTimer)
				    mTimer = setTimeout('download();',250);
			    }
			}
			
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
			
			function hideLayer(whichLayer)
			{
			    document.getElementById(whichLayer).style.visibility = "hidden";
			}
			
			function showLayer(whichLayer)
			{
			    document.getElementById(whichLayer).style.visibility = "visible";
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
					<p>As the <%= update ? "update" : "download" %> proceeds, the actions taking place will be listed. Please wait until you see <strong>Installation Complete</strong> before proceeding with other tasks.</p>
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
					<p>As the <%= update ? "update" : "download" %> proceeds, the actions taking place will be listed. Please wait until you see <strong>Installation Complete</strong> before proceeding with other tasks.</p>
					<p><span id="head_sync">Please click <strong><%= update ? "Update" : "Download" %></strong> to begin.</span></p>
					<struts-html:form action="/synchronizeCourse">
						<div class="loginItem" id="syncbutton">
							<div class="left"></div>
							<div class="right"><input class="formbutton" type="submit" onclick="javascript:downloadInit();return false;" value="<%= update ? "Update" : "Download" %>" alt="<%= update ? "Update" : "Download" %>" /><input type="hidden" name="status" value="sync"></div>
							<div class="end"></div>
						</div>
					</struts-html:form>
<%
    }
}
else
{
%>
					<p><span id="head_sync">Unable to communicate with the server.  Please connect to the Internet and click <strong>Retry</strong>.</span></p>
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
						<div class="headline"><img src="images/gfxLoading-stopped.gif" width="32" height="32" title="" alt="" name="m2" /><%= update ? "Update" : "Download" %> Progress</div>
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
session.removeAttribute("sync-course");
%>