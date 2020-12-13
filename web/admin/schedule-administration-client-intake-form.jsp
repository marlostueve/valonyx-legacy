<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.util.*, java.math.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="adminAssessment" class="com.badiyan.uk.beans.AssessmentBean" scope="session" />
<jsp:useBean id="adminStem" class="com.badiyan.uk.beans.StemBean" scope="session" />
<jsp:useBean id="adminDistractor" class="com.badiyan.uk.beans.DistractorBean" scope="session" />

<%


	
CompanySettingsBean settings = adminCompany.getSettings();
QuickBooksSettings qb_settings = adminCompany.getQuickBooksSettings();


// find the client intake form assessment for this company...

try {
	adminAssessment = AssessmentBean.getAssessment(adminCompany, "Client Intake Form");
} catch (com.badiyan.uk.exceptions.ObjectNotFoundException x) {
	// doesn't exist -  need to create
	adminAssessment = new AssessmentBean();
	adminAssessment.setName("Client Intake Form");
    adminStem = new StemBean();
}

AssessmentCategoryBean.maintainCategory(adminAssessment, "Women Only");

session.setAttribute("adminAssessment", adminAssessment);

if (request.getParameter("stemId") != null)
{
    try
    {
        int assessmentStemId = Integer.parseInt(request.getParameter("stemId"));
        adminStem = StemBean.getStem(assessmentStemId);
    }
    catch (NumberFormatException x)
    {
    }
}
else
    adminStem = new StemBean();

session.setAttribute("adminStem", adminStem);


String deleteIdString = request.getParameter("deleteId");
if (deleteIdString != null)
{
    StemBean.delete(deleteIdString);
    adminAssessment.invalidateStems();
}

String moveUpIdString = request.getParameter("moveUpId");
if (moveUpIdString != null)
{
    StemBean moveUpStem = StemBean.getStem(Integer.parseInt(moveUpIdString));
    adminAssessment.moveUp(moveUpStem);
}
String moveDownIdString = request.getParameter("moveDownId");
if (moveDownIdString != null)
{
    StemBean moveDownStem = StemBean.getStem(Integer.parseInt(moveDownIdString));
    adminAssessment.moveDown(moveDownStem);
}


%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />

	<style type="text/css">
	/*margin and padding on body element
	  can introduce errors in determining
	  element position and are not recommended;
	  we turn them off as a foundation for YUI
	  CSS treatments. */
	body {
		margin:0;
		padding:0;
	}
	</style>

<%
if (settings.isSetupComplete())
{
%>
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/resize.css">
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css">
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>
<%
}
else
{
%>
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>
<%
}
%>

	<script type="text/javascript">

    var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var httpRequest;
    var request_NextDay;

    var edit = 0;

    var email_log_in = <%= (!settings.getLoginLabelString().equals("Username")) ? "true" : "false" %>;

    function processCommand(command, parameter)
    {
	if (window.ActiveXObject)
	    httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
	else if (window.XMLHttpRequest)
	    httpRequest = new XMLHttpRequest();

	httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain")%>/ClientServlet.html', true);
	httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	httpRequest.onreadystatechange = function() {processCommandEvent(); } ;
	eval("httpRequest.send('command=" + command + "&parameter=" + parameter + "')");

	return true;
    }

    function processCommandEvent()
    {
	if (httpRequest.readyState == 4)
	{
	    if (httpRequest.status == 200)
	    {
		if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
		{
		    var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];

		    document.getElementById('today_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
		    document.getElementById('new_appt_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
		    //document.getElementById('newAppointmentDateInput').value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
		    document.forms[0].newAppointmentDateInput.value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;

		    buildScheduleArray(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
		{
		    //alert(httpRequest.responseXML.getElementsByTagName("clients").length);
		    var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
		    showPeople(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
		{
		    //alert(httpRequest.responseXML.getElementsByTagName("clients").length);
		    var xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
		    //showPeople(xml_response);

		    showPerson(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("error").length > 0)
		{
		    var xml_response = httpRequest.responseXML.getElementsByTagName("error")[0];
		    alert("Error : " + xml_response.childNodes[0].nodeValue);
		}
	    }
	    else
	    {
		alert("Error : " + httpRequest.status + " : " + httpRequest.statusText);
	    }
	}
    }

	</script>


<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup", function () {

            // Create a Button using an existing <input> element as a data source
<%
if (!settings.isSetupComplete())
{
%>
            var oSubmitButton1 = new YAHOO.widget.Button("submitbutton1", { value: "previous" });
	    var oSubmitButton2 = new YAHOO.widget.Button("submitbutton2", { value: "next" });
	    var oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", { value: "finish" });
<%
}
%>
        });

    } ();

</script>

<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup2", function () {

            // Create a Button using an existing <input> element as a data source

	    var oSubmitButton4 = new YAHOO.widget.Button("submitbutton4", { value: "Add Practitioner" });

        });

    } ();

</script>

<script type="text/javascript">

    (function () {

    	var Button = YAHOO.widget.Button;

        // "contentready" event handler for the "checkboxbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup", function () {

            // Create Buttons using existing <input> elements as a data source

            var oCheckButton1 = new Button("checkbutton1", { label: "This User is an Administrator" });


        });


    }());

</script>

<%
if (settings.isSetupComplete())
{
%>
<script type="text/javascript">

(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
                minWidth: 750,
                units: [
		{ position: 'top', height: 21, resize: false, body: 'top1' },
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
        });
        layout.render();

    });

})();
</script>
<%
}
else
{
%>
<%@ include file="..\channels\channel-google-analytics.jsp" %>
<%
}
%>

<script type="text/javascript">
    (function () {

    	var Button = YAHOO.widget.Button;
        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup3", function () {
            var oCheckButton2 = new Button("checkbutton3", { label: "Enable Client Intake Form" });
        });

    }());
</script>

<script type="text/javascript">
    (function () {

    	var Button = YAHOO.widget.Button;
        YAHOO.util.Event.onContentReady("checkboxbuttonsfrommarkup4", function () {
            var oCheckButton3 = new Button("checkbutton4", { label: "Enable Client Wellness Survey" });
        });

    }());
</script>

<%@ include file="..\channels\channel-head-javascript.jsp" %>
		
	<script type="text/javascript" src="../scripts/crir.js"></script>

	<title>Valonyx</title>

</head>

<body class=" yui-skin-sam" onload="javascript:initErrors();">

<%
if (settings.isSetupComplete())
{
%>
<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white;">
<%
}

BigDecimal pos_fee = new BigDecimal(50f);
BigDecimal fee = adminCompany.getBaseMonthlySubscriptionFee();
if (qb_settings.isPOSEnabled())
	fee = fee.add(pos_fee);

int num_clients = adminCompany.getNumClientsForSubscriptionFeeCalculation();
String basic_subscription_fee_str = adminCompany.getBaseMonthlySubscriptionFeeString();



%>


    <div id="content">


	<div class="content_TextBlock">
	    <p class="headline"><%= adminCompany.getLabel() %> <%= settings.isSetupComplete() ? "Administration" : "Setup" %> - Client Intake Form</p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-setup-menu.jsp" %>
<%
try {
%>
					<div class="main">

						<!--
						<p class="headlineA"><struts-bean:message key="admin.admin-course-evaluation.headline01" /></p>
						<p class="currentObjectName"><%= adminCourse.getLabel() %></p>
						-->
							
						<p>stuff</p>


<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
if (adminStem.isNew() || adminStem.isFillInTheBlank())
{
%>
						<p class="headlineB">Fill In The Blank Question</p>

						<struts-html:form action="/admin/intakeFITBQuestion" focus="questionInput">

							<input id="dummyA" name="dummyA" type="hidden" />

									
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">HEADING</div>
								<div class="right">
									<textarea name="headingInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminStem.getHeadingTextString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
								
							<div class="adminItem">
								<div class="leftTM">QUESTION STEM</div>
								<div class="right">
									<textarea name="questionInput" rows="5" cols="35" class="textarea" style="width: 304px;"><%= adminStem.getNameString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input class="formbutton" type="submit" value="Add&nbsp;&#47;&nbsp;Edit Question" alt="Add&nbsp;&#47;&nbsp;Edit Question" />
								</div>
								<div class="end"></div>
							</div>

						</struts-html:form>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
}
if (adminStem.isNew() || adminStem.isMultipleChoice() || adminStem.isCheckAllThatApply())
{
%>

						<p class="headlineB">Multiple Choice Question</p>

						<struts-html:form action="/admin/intakeQuestion" onsubmit="return validateAssessmentQuestionForm(this);">

							<input id="dummyA" name="dummyA" type="hidden" />


									
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">HEADING</div>
								<div class="right">
									<textarea name="headingInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminStem.getHeadingTextString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
								
							<div class="adminItem">
								<div class="leftTM">QUESTION STEM</div>
								<div class="right">
									<textarea name="questionInput" rows="5" cols="35" class="textarea" style="width: 304px;"><%= adminStem.getNameString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
<%
boolean isWomenOnly = false;
try {
	if (adminStem.hasCategory()) {
		AssessmentCategoryBean categoryObj = adminStem.getCategory();
		if (categoryObj.getTitle().equals("Women Only")) {
			isWomenOnly = true;
		}
	}
} catch (Exception x) {
	x.printStackTrace();
}
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="right">
									<label for="womenOnly">WOMEN ONLY</label>
									<input name="womenOnly" id="womenOnly" type="checkbox" value="Y" class="crirHiddenJS" <%= (isWomenOnly) ? "checked=\"checked\" " : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="right">
									<label for="mcAnswerZCorrect">ALL THAT APPLY</label>
									<input name="correctSelect" id="mcAnswerZCorrect" type="checkbox" value="Z" class="crirHiddenJS" onclick="" <%= (!adminStem.isNew() && adminStem.isCheckAllThatApply()) ? "checked=\"checked\" " : "" %> />
								</div>
								<div class="end"></div>
							</div>
<%
// get a list of the distractors from the stem
List distractors = adminStem.getDistractors();
Iterator itr = distractors.iterator();
adminDistractor = null;
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">
									<div class="leftTM">ANSWER (A)</div>
								</div>
								<div class="right">
									<textarea name="answerAInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea><input type="hidden" name="answerAFeedbackInput" value="">
								</div>
								<div class="end"></div>
							</div>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">
									<div class="leftTM">ANSWER (B)</div>
								</div>
								<div class="right">
									<textarea name="answerBInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea><input type="hidden" name="answerBFeedbackInput" value="">
								</div>
								<div class="end"></div>
							</div>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">
									<div class="leftTM">ANSWER (C)</div>
								</div>
								<div class="right">
									<textarea name="answerCInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea><input type="hidden" name="answerCFeedbackInput" value="">
								</div>
								<div class="end"></div>
							</div>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">
									<div class="leftTM">ANSWER (D)</div>
								</div>
								<div class="right">
									<textarea name="answerDInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea><input type="hidden" name="answerDFeedbackInput" value="">
								</div>
								<div class="end"></div>
							</div>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">
									<div class="leftTM">ANSWER (E)</div>
								</div>
								<div class="right">
									<textarea name="answerEInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea><input type="hidden" name="answerEFeedbackInput" value="">
								</div>
								<div class="end"></div>
							</div>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">
									<div class="leftTM">ANSWER (F)</div>
								</div>
								<div class="right">
									<textarea name="answerFInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea><input type="hidden" name="answerFFeedbackInput" value="">
								</div>
								<div class="end"></div>
							</div>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">
									<div class="leftTM">ANSWER (G)</div>
								</div>
								<div class="right">
									<textarea name="answerGInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea><input type="hidden" name="answerGFeedbackInput" value="">
								</div>
								<div class="end"></div>
							</div>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">
									<div class="leftTM">ANSWER (H)</div>
								</div>
								<div class="right">
									<textarea name="answerHInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea><input type="hidden" name="answerFHFeedbackInput" value="">
								</div>
								<div class="end"></div>
							</div>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">
									<div class="leftTM">ANSWER (I)</div>
								</div>
								<div class="right">
									<textarea name="answerIInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea><input type="hidden" name="answerIFeedbackInput" value="">
								</div>
								<div class="end"></div>
							</div>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<div class="adminItem" style="margin-bottom: -5px;">
								<div class="leftTM">
									<div class="leftTM">ANSWER (J)</div>
								</div>
								<div class="right">
									<textarea name="answerJInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea><input type="hidden" name="answerJFeedbackInput" value="">
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input class="formbutton" type="submit" value="Add&nbsp;&#47;&nbsp;Edit Question" alt="Add&nbsp;&#47;&nbsp;Edit Question" />
								</div>
								<div class="end"></div>
							</div>

						</struts-html:form>
<%
}
%>
						<div class="content_AdministrationTable">

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 322px; text-align: left;  ">Question</td>
										<td style="width:  50px; text-align: center;  "></td>
										<td style="width:  30px; text-align: center;  "></td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
List stems = adminAssessment.getStems();
if (stems.size() > 0)
{
    Iterator stemItr = stems.iterator();
    while (stemItr.hasNext())
    {
        StemBean stem = (StemBean)stemItr.next();
	
		if (stem.isFillInTheBlank())
		{
%>
							<!-- BEGIN Question -->
							<div class="question">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 322px; text-align: left;  "><a href="schedule-administration-client-intake-form.jsp?id=<%= adminAssessment.getId() %>&stemId=<%= stem.getId() %>" title=""><%= stem.getNameString() %></a></td>
										<td style="width:  50px; text-align: center;  "><a href="schedule-administration-client-intake-form.jsp?moveUpId=<%= stem.getId() %>" title="Move Up">Up</a></td>
										<td style="width:  30px; text-align: center;  "><a href="schedule-administration-client-intake-form.jsp?moveDownId=<%= stem.getId() %>" title="Move Down">Down</a></td>
										<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('<struts-bean:message key="confirm" />')) {document.location.href='schedule-administration-client-intake-form.jsp?deleteId=<%= stem.getId() %>'}" title="Delete">Delete</a></td>
									</tr>
								</table>
							</div>
							<!-- END Question -->
<%
		}
		else if (stem.isMultipleChoice() || stem.isCheckAllThatApply())
        {
			boolean isWomenOnlyX = false;
			try {
				if (stem.hasCategory()) {
					AssessmentCategoryBean categoryObj = stem.getCategory();
					if (categoryObj.getTitle().equals("Women Only")) {
						isWomenOnlyX = true;
					}
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
%>
							<!-- BEGIN Question -->
							<div class="question">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 322px; text-align: left;  "><a href="schedule-administration-client-intake-form.jsp?id=<%= adminAssessment.getId() %>&stemId=<%= stem.getId() %>" title=""><%= stem.getNameString() %></a><%= isWomenOnlyX ? " (women only)" : "" %><%= stem.isCheckAllThatApply() ? " (check all that apply)" : "" %></td>
										<td style="width:  50px; text-align: center;  "><a href="schedule-administration-client-intake-form.jsp?moveUpId=<%= stem.getId() %>" title="Move Up">Up</a></td>
										<td style="width:  30px; text-align: center;  "><a href="schedule-administration-client-intake-form.jsp?moveDownId=<%= stem.getId() %>" title="Move Down">Down</a></td>
										<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('<struts-bean:message key="confirm" />')) {document.location.href='schedule-administration-client-intake-form.jsp?deleteId=<%= stem.getId() %>'}" title="Delete">Delete</a></td>
									</tr>
								</table>
							</div>
<%
            List disList = stem.getDistractors();
            Iterator disItr = disList.iterator();
            char questionCharacter = 'A';
            while (disItr.hasNext())
            {
                DistractorBean distractor = (DistractorBean)disItr.next();
                String characterDisplayString;
                characterDisplayString = questionCharacter + " " + distractor.getNameString();
%>
							<div class="questionAnswer">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 322px; text-align: left;  "><%= characterDisplayString %></td>
										<td style="width:  50px; text-align: center;  "></td>
										<td style="width:  30px; text-align: center;  "></td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
<%
                questionCharacter++;
            }
%>
							<!-- END Question -->
<%
        }
	else if (stem.isTrueFalse())
	{
%>
							<!-- BEGIN Question -->
							<div class="question">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 322px; text-align: left;  "><a href="schedule-administration-client-intake-form.jsp?id=<%= adminAssessment.getId() %>&stemId=<%= stem.getId() %>" title=""><%= stem.getNameString() %></a></td>
										<td style="width:  50px; text-align: center;  "><a href="schedule-administration-client-intake-form.jsp?moveUpId=<%= stem.getId() %>" title="Move Up"><struts-bean:message key="admin.button14" /></a></td>
										<td style="width:  30px; text-align: center;  "><a href="schedule-administration-client-intake-form.jsp?moveDownId=<%= stem.getId() %>" title="Move Down"><struts-bean:message key="admin.button15" /></a></td>
										<td style="width:  70px; text-align: center;  "><a href="schedule-administration-client-intake-form.jsp?deleteId=<%= stem.getId() %>" title="Delete"><struts-bean:message key="admin.button07" /></a></td>
									</tr>
								</table>
							</div>
<%
            List disList = stem.getDistractors();
            Iterator disItr = disList.iterator();
            String questionCharacter = "(True)";
            while (disItr.hasNext())
            {
                DistractorBean distractor = (DistractorBean)disItr.next();
                String characterDisplayString;
                characterDisplayString = questionCharacter + " " + distractor.getNameString();
%>
							<div class="questionAnswer">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 322px; text-align: left;  "><%= characterDisplayString %></td>
										<td style="width:  50px; text-align: center;  "></td>
										<td style="width:  30px; text-align: center;  "></td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
<%
                questionCharacter = "(False)";
            }
	}
    }
}
else
{
%>
							<!-- BEGIN Question -->
							<div class="question">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colpspan="5">No Questions Found</td>
									</tr>
								</table>
							</div>
							<!-- END Question -->
<%
}
%>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						</div>

					</div>
<%
} catch (Exception x) {
	x.printStackTrace();
}
%>
	    <div class="end"></div>
	</div>





	<div class="content_TextBlock">
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

    </div>


<%
if (settings.isSetupComplete())
{
%>
</div>
<%
}
%>


</body>
</html>

