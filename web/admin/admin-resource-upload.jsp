<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminResource" class="com.badiyan.uk.beans.ResourceBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
String deleteIdString = request.getParameter("deleteId");
if (deleteIdString != null)
{
    AssessmentBean.delete(deleteIdString);
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>

		<title>Universal Knowledge</title>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="generator" content="TextPad 4.6.2" />
		<meta name="author" content="" />
		<meta name="copyright" content="&copy; 2007" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />

		<link rel="stylesheet" type="text/css" href="../css/web.css" />

		<script type="text/javascript" src="../scripts/crir.js"></script>
		<!--[if lte IE 7]><script type="text/javascript" src="scripts/optionDisabledSupport.js"></script><![endif]-->
		<struts-html:javascript formName="resourceUploadForm" dynamicJavascript="true" staticJavascript="false"/>
		<script language="Javascript1.1" src="../staticJavascript.jsp"></script>
		
<%@ include file="..\channels\channel-head-javascript.jsp" %>

	</head>

	<body onload="javascript:initErrors();">

		<div id="main">

<%@ include file="..\channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Administration">

<%@ include file="channels\channel-admin-menu.jsp" %>

					<div class="main">

						<p class="headlineA">Resource Details&nbsp;&#47;&nbsp;Media</p>
						<p class="currentObjectName"><%= adminResource.getLabel() %></p>
						<p>Use this screen to upload media for new resources. Select media from the list, or browse to
a location. The resource will be updated with the new media.</p>
<p>If you are updating a resource, go to the Resource Details screen to add Update Notes so
users will know what has changed.</p>
						<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form method="POST" action="/admin/resourceUpload" enctype="multipart/form-data" onsubmit="return validateResourceUploadForm(this);">

							<input id="dummy" name="dummy" type="hidden" />

							<!--
							<div class="adminItem">
								<div class="left">CURRENT FILE</div>
								<div class="right">sampleResource.abc
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">UPLOAD</div>
								<div class="right">
									<input name="upload" type="file" onfocus="select();" value="" size="31" maxlength="100" class="inputbox_file" style="width: 307px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
										<input class="formbutton" type="submit" value="Upload Media" alt="Upload Media" style="margin-right: 4px;"/>
								</div>
								<div class="end"></div>
							</div>
							-->

							<div class="adminItem">
								<div class="left">CURRENT FILE</div>
								<div class="right"><%= (adminResource.getType().equals(ResourceBean.INTERNET_RESOURCE_TYPE)) ? "" : (adminResource.getURL() == null) ? "" : adminResource.getURL() %>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="left">UPLOAD</div>
								<div class="right">
									<span id="filenameDisplay"></span>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input name="browse1" id="browse1" type="button" class="formbutton" value="Browse" style="z-index:1; margin-right: 4px;" />
									<input name="uploadInput" id="browse2" type="file" onchange="document.getElementById('filenameDisplay').innerHTML = document.getElementById('browse2').value;" style="position: absolute; left: 260px; _left: 248px; -moz-opacity: 0; filter: alpha(opacity: 0); opacity: 0; z-index: 2;" />
									<input class="formbutton" type="submit" value="Upload File" alt="Upload File" style="margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>

							<!--
							<div class="adminItem">
								<div class="leftTM">RESOURCE URL</div>
								<div class="right">
									<input name="resourceURL" onfocus="select();" value="http://" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input class="formbutton" type="submit" value="Update Resource URL" alt="Update Resource URL" style="margin-right: 4px;"/>
								</div>
								<div class="end"></div>
							</div>
							-->

						</struts-html:form>

					</div>

					<div class="end"></div>
				</div>

				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="..\channels\channel-footer.jsp" %>

		</div>

	</body>

</html>