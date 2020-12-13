<%@ page language="java" buffer="12kb" contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="prereqSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<jsp:useBean id="adminCourseGroup" class="com.badiyan.uk.beans.CourseGroupBean" scope="session" />

<jsp:useBean id="adminDepartment" class="com.badiyan.uk.beans.DepartmentBean" scope="session" />
<jsp:useBean id="adminDepartmentType" class="com.badiyan.uk.beans.DepartmentTypeBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
adminCourseGroup = new CourseGroupBean();
session.setAttribute("adminCourseGroup", adminCourseGroup);
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
		
		<script language="JavaScript" type="text/JavaScript">
		    <!--

			if (document.images) {
			Rollimage = new Array();

			Rollimage[0]= new Image(17,17);
			Rollimage[0].src = "../images/btnContract.gif";
			Rollimage[1] = new Image(17,17);
			Rollimage[1].src = "../images/btnExpand.gif";
			}

			function SwapOut(x){
			  if (document.images)
			    eval("(document.m" + x + ".src.indexOf(\"btnContract.gif\") > -1) ? document.m" + x + ".src = Rollimage[1].src : document.m" + x + ".src = Rollimage[0].src");
			  return true;
			}

		    //-->
		</script>
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

						<p class="headlineA">Add New Curriculum Level</p>
						<p>Use this screen to create a new curriculum level.</p>

						<struts-html:form action="/admin/courseLesson">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">LEVEL NAME</div>
								<div class="right">
									<input name="nameInput" onfocus="select();" value="<%= adminCourseGroup.getNameString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input class="formbutton" type="submit" value="Add&nbsp;&#47;&nbsp;Update" alt="Add&nbsp;&#47;&nbsp;Update" style="margin-right: 4px;"/><input type="hidden" name="delete_id" value="0" />
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