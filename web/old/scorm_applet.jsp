<%@ page language="java" %>

<html>

<head>

<meta http-equiv="expires" content="Wed, 15 Oct 2003 01:15:00 GMT"/>
<meta http-equiv="Pragma" content="no-cache"/>
<title>UK SCORM</title>
<script LANGUAGE="JavaScript">

function
notifyParent()
{
    parent.initScormAPI();
}

</script>
</head>
<%
System.out.println("PATH >" + request.getContextPath());
%>

<body onload="javascript:notifyParent();">
<APPLET
            code="com.badiyan.uk.conformance.scorm.rte.client.ScormApplet.class"
            archive="scorm_applet.jar"
            codebase="/<%= System.getProperty("cu.clientPackageName") %>/"
            height="1"
            width="2"
            id="APIAdapter"
            name="APIAdapter"
            mayscript="true">
    <PARAM name="initializeUrl" value="scorm/initialize.x"/>
    <PARAM name="commitUrl" value="scorm/commit.x"/>
    <PARAM name="debug" value="true"/>
    <PARAM name="info" value="true"/>
</APPLET>
</body>
</html>
