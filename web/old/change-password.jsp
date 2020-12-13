<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

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
		
		<%@ include file="channels/channel-head-javascript.jsp" %>
	</head>

	<body onload="initErrors();">

		<div id="main">

			<!-- *** BEGIN Header *** -->
			<div id="header">
				<img src="images/trspacer.gif" width="100%" height="1" alt="" />
				<div id="header_LogoAndTagline"><img src="images/gfxLogoAndTagline.gif" width="424" height="37" alt="Ecolab Learning Management System" /></div>
				<div id="header_UserProperName"></div>
				<div id="header_MainNavigation">
					<!-- <span>HOME</span> -->
					<!-- <span>ACTIVITY CATALOG</span> -->
					<!-- <span>RESOURCES</span> -->
					<!-- <span>ADMINISTRATION</span> -->
					<!-- <span>SEARCH</span> -->
					<!-- <span>HELP</span> -->
				</div>
				<div id="header_UserNavigation">
					<!-- <a href="stuSynchronize.html" title="Synchronize">SYNCHRONIZE</a> -->
					<!-- <span>SYNCHRONIZE</span> -->
					<!-- <a href="login.html" title="Log Out">LOG OUT</a> -->
				</div>
			</div>
			<!-- *** END Header *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Change Password</p>
					<p>You must change your password.  Please enter a new password, confirm your entry, and then click <strong>Update</strong>.</p>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Login">

					<struts-html:form action="/changePassword" focus="password">

						<input id="dummy" name="dummy" type="hidden" />

						<div class="loginItem">
							<div class="left">PASSWORD</div>
							<div class="right"><input name="password" type="password" onfocus="select();" value="" size="37" maxlength="50" class="inputbox" style="width: 206px;" /></div>
							<div class="end"></div>
						</div>
						
						<div class="loginItem">
							<div class="left">CONFIRM ENTRY</div>
							<div class="right"><input name="confirm_password" type="password" onfocus="select();" value="" size="37" maxlength="50" class="inputbox" style="width: 206px;" /></div>
							<div class="end"></div>
						</div>

						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input class="formbutton" type="submit" value="Update" alt="Update" /></div>
							<div class="end"></div>
						</div>

					</struts-html:form>

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