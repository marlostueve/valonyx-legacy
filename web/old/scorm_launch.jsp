<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="scormLaunchForm" type="com.badiyan.uk.conformance.scorm.rte.server.struts.LaunchActionForm" scope="request" />

<html:html>

<head>

<meta http-equiv="expires" content="Wed, 15 Oct 2003 01:15:00 GMT"/>
<meta http-equiv="Pragma" content="no-cache"/>
<title>UK SCORM</title>
<html:base/>
<SCRIPT language="JAVASCRIPT">

function
initializeScorm()
{
    parent.API.launchNewSco("<%= scormLaunchForm.getPersonId() %>",
	   				"<%= scormLaunchForm.getCourseId() %>",
	   				"<%= scormLaunchForm.getEnrollmentId() %>");
    window.location.replace("<%= scormLaunchForm.getLessonUrl() %>");
}
</SCRIPT>
</head>

<body onload="initializeScorm();">

<h1>Please wait...</h1>

</body>
</html:html>
