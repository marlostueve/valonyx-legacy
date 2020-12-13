<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.torque.*;" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
CourseWindowSettings settings = adminCourse.getWindowSettings();
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
		
<%@ include file="..\channels\channel-head-javascript.jsp" %>

	</head>

	<body onload="javascript:initErrors();">

		<div id="main">

<%@ include file="..\channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Administration">

<%@ include file="channels\channel-admin-menu.jsp" %>

					<div class="main">

						<p class="headlineA">Activity Details&nbsp;&#47;&nbsp;Window</p>
						<p class="currentObjectName"><%= adminCourse.getLabel() %></p>
						<p>Use this screen to add or update window settings for this activity.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/courseWindow" >

							<input id="dummy" name="dummy" type="hidden" />


							<div class="adminItem">
								<div class="leftTM">WIDTH (PIXELS)</div>
								<div class="right">
									<input name="width" onfocus="select();" value="<%= settings.getWindowWidth() %>" size="2" maxlength="4" class="inputbox" style="width: 34px; margin-top: -1px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">HEIGHT (PIXELS)</div>
								<div class="right">
									<input name="height" onfocus="select();" value="<%= settings.getWindowHeight() %>" size="2" maxlength="4" class="inputbox" style="width: 34px; margin-top: -1px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">WINDOW SETTINGS</div>
								<div class="right">
									<label for="displayMenuBar" style="margin-left: -3px;">Display Menu Bar</label>
									<input name="displayMenuBar" id="displayMenuBar" type="checkbox" class="crirHiddenJS"<%= (settings.getDisplayMenuBar() == (short)1) ? " checked" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<label for="displayToolbar" style="margin-left: -3px;">Display Toolbar</label>
									<input name="displayToolbar" id="displayToolbar" type="checkbox" class="crirHiddenJS"<%= (settings.getDisplayToolbar() == (short)1) ? " checked" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<label for="displayLocation" style="margin-left: -3px;">Display Location</label>
									<input name="displayLocation" id="displayLocation" type="checkbox" class="crirHiddenJS"<%= (settings.getDisplayLocation() == (short)1) ? " checked" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<label for="displayScrollBars" style="margin-left: -3px;">Display Scroll Bars</label>
									<input name="displayScrollBars" id="displayScrollBars" type="checkbox" class="crirHiddenJS"<%= (settings.getDisplayScrollBars() == (short)1) ? " checked" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<label for="resizable" style="margin-left: -3px;">Resizable</label>
									<input name="isResizable" id="resizable" type="checkbox" class="crirHiddenJS"<%= (settings.getIsResizable() == (short)1) ? " checked" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<label for="fullscreen" style="margin-left: -3px;">Fullscreen</label>
									<input name="displayFullscreen" id="fullscreen" type="checkbox" class="crirHiddenJS"<%= (settings.getDisplayFullscreen() == (short)1) ? " checked" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<!-- <input class="formbutton" type="button" value="Test Window" alt="Test Window" style="margin-right: 10px; "/> -->
									<input class="formbutton" type="submit" value="Update Window Settings" alt="Update Window Settings" />
								</div>
								<div class="end"></div>
							</div>

						</struts-html:form>

					</div>

					<div class="end"></div>
				</div>

				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="..\channels\channel-footer.jsp" %>

		</div>

	</body>

</html>