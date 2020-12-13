<%@ page language="java" %>

<html>

<head>

<meta http-equiv="expires" content="Wed, 15 Oct 2003 01:15:00 GMT"/>
<meta http-equiv="Pragma" content="no-cache"/>
<title>UK SCORM</title>
<script LANGUAGE="JavaScript">
    
var return_value;

function
notifyParent()
{
    parent.initScormAPI();
}

function
launchNewSco(personId, courseId, enrollmentId)
{
    //alert('launchNewSco() invoked');
}

function
LMSCommit(emptyString)
{
    alert('LMSCommit() invoked >' + emptyString);
    return "dfg";
}

function
LMSFinish(emptyString)
{
    alert('LMSFinish() invoked >' + emptyString);
    return "dfg";
}

function
LMSGetDiagnostic(diagnosticCode)
{
    alert('LMSGetDiagnostic() invoked >' + diagnosticCode);
    return "dfg";
}

function
LMSGetErrorString(errorCode)
{
    alert('LMSGetErrorString() invoked >' + errorCode);
    return "dfg";
}

function
LMSGetLastError()
{
    alert('LMSGetLastError() invoked');
    return "dfg";
}

function
LMSGetValue(aName)
{
    alert('LMSGetValue() invoked >' + aName);
    if (aName == 'cmi.core.student_id')
	return "12";
    return "dfg";
}

function
LMSInitialize(emptyString)
{
    alert('LMSInitialize() invoked >' + emptyString);
    return "dfg";
}

function
LMSSetValue(aName, aValue)
{
    alert('LMSSetValue() invoked >' + aName + ", " + aValue);
    return "dfg";
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
