<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="adminAssessment" class="com.badiyan.uk.beans.AssessmentBean" scope="session" />
<jsp:useBean id="adminStem" class="com.badiyan.uk.beans.StemBean" scope="session" />
<jsp:useBean id="adminDistractor" class="com.badiyan.uk.beans.DistractorBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
adminAssessment = adminCourse.getAssessment();
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
		
		<struts-html:javascript formName="assessmentQuestionForm" dynamicJavascript="true" staticJavascript="false"/>
		<struts-html:javascript formName="assessmentTFQuestionForm" dynamicJavascript="true" staticJavascript="false"/>
		<script language="Javascript1.1" src="../staticJavascript.jsp"></script>
		
		<script TYPE="text/javascript">
		<!--

		function
		manageChecks(form,checkbox)
		{
		    
		    var thisCheckbox;
		    thisCheckbox = eval("document." + form + "." + checkbox);
		    
		    if (thisCheckbox.checked == true)
		    {
			aCheck = eval("document." + form + ".correctASelect");
			bCheck = eval("document." + form + ".correctBSelect");
			cCheck = eval("document." + form + ".correctCSelect");
			dCheck = eval("document." + form + ".correctDSelect");
			eCheck = eval("document." + form + ".correctESelect");
			
			alert('aCheck >' + aCheck.checked);
			alert('bCheck >' + bCheck.checked);
			alert('cCheck >' + cCheck.checked);
			alert('dCheck >' + dCheck.checked);
			alert('eCheck >' + eCheck.checked);
			
			if (thisCheckbox != aCheck)
			{
			    aCheck.checked = false;
			    alert('aCheck >' + aCheck.checked);
			}
			if (thisCheckbox != bCheck)
			    bCheck.checked = false;
			if ((cCheck != null) && (thisCheckbox != cCheck))
			    cCheck.checked = false;
			if ((dCheck != null) && (thisCheckbox != dCheck))
			    dCheck.checked = false;
			if ((eCheck != null) && (thisCheckbox != eCheck))
			    eCheck.checked = false;
		    }
		}

		// -->
		</script>
		
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

						<p class="headlineA">Activity Details&nbsp;&#47;&nbsp;Questions</p>
						<p class="currentObjectName"><%= adminCourse.getLabel() %></p>
						<p>Use this screen to create or update assessment activities. Click in the table at the bottom
of the screen to update or delete a question, or to modify its location within an
assessment.</p>
<%
if (adminStem.isNew() || adminStem.isMultipleChoice())
{
%>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
						<p class="headlineB">Multiple Choice Question</p>

						<struts-html:form action="/admin/assessmentQuestion" focus="questionInput" onsubmit="return validateAssessmentQuestionForm(this);">

							<input id="dummyA" name="dummyA" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">QUESTION STEM</div>
								<div class="right">
									<textarea name="questionInput" rows="5" cols="35" class="textarea" style="width: 304px;"><%= adminStem.getNameString() %></textarea>
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
								<div class="leftTM">ANSWER (A)
									<label for="mcAnswerACorrect">&nbsp;</label>
									<input name="correctSelect" id="mcAnswerACorrect" type="radio" value="A" class="crirHiddenJS" onclick="" <%= (adminDistractor.isCorrect() || adminStem.isNew()) ? "checked=\"checked\" " : "" %>/>
								</div>
								<div class="right">
									<textarea name="answerAInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ANSWER (A) FEEDBACK</div>
								<div class="right">
									<textarea name="answerAFeedbackInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getPrescriptiveFeedback() %></textarea>
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
								<div class="leftTM">ANSWER (B)
									<label for="mcAnswerBCorrect">&nbsp;</label>
									<input name="correctSelect" id="mcAnswerBCorrect" type="radio" value="B" class="crirHiddenJS" onclick="" <%= adminDistractor.isCorrect() ? "checked=\"checked\" " : "" %>/>
								</div>
								<div class="right">
									<textarea name="answerBInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ANSWER (B) FEEDBACK</div>
								<div class="right">
									<textarea name="answerBFeedbackInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getPrescriptiveFeedback() %></textarea>
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
								<div class="leftTM">ANSWER (C)
									<label for="mcAnswerCCorrect">&nbsp;</label>
									<input name="correctSelect" id="mcAnswerCCorrect" type="radio" value="C" class="crirHiddenJS" onclick="" <%= adminDistractor.isCorrect() ? "checked=\"checked\" " : "" %>/>
								</div>
								<div class="right">
									<textarea name="answerCInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ANSWER (C) FEEDBACK</div>
								<div class="right">
									<textarea name="answerCFeedbackInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getPrescriptiveFeedback() %></textarea>
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
								<div class="leftTM">ANSWER (D)
									<label for="mcAnswerDCorrect">&nbsp;</label>
									<input name="correctSelect" id="mcAnswerDCorrect" type="radio" value="D" class="crirHiddenJS" onclick="" <%= adminDistractor.isCorrect() ? "checked=\"checked\" " : "" %>/>
								</div>
								<div class="right">
									<textarea name="answerDInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ANSWER (D) FEEDBACK</div>
								<div class="right">
									<textarea name="answerDFeedbackInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getPrescriptiveFeedback() %></textarea>
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
								<div class="leftTM">ANSWER (E)
									<label for="mcAnswerECorrect">&nbsp;</label>
									<input name="correctSelect" id="mcAnswerECorrect" type="radio" value="E" class="crirHiddenJS" onclick="" <%= adminDistractor.isCorrect() ? "checked=\"checked\" " : "" %>/>
								</div>
								<div class="right">
									<textarea name="answerEInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ANSWER (E) FEEDBACK</div>
								<div class="right">
									<textarea name="answerEFeedbackInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getPrescriptiveFeedback() %></textarea>
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
if (adminStem.isNew() || !adminStem.isMultipleChoice())
{
%>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<p class="headlineB">True&nbsp;&#47;&nbsp;False Question</p>

						<struts-html:form action="/admin/assessmentTFQuestion" onsubmit="return validateAssessmentTFQuestionForm(this);">

							<input id="dummyB" name="dummyB" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">QUESTION STEM</div>
								<div class="right">
									<textarea name="questionInput" rows="5" cols="35" class="textarea" style="width: 304px;"><%= adminStem.getNameString() %></textarea>
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
								<div class="leftTM">ANSWER (TRUE)
									<label for="tfAnswerACorrect">&nbsp;</label>
									<input name="correctSelect" id="tfAnswerACorrect" type="radio" value="A" class="crirHiddenJS" onclick="" <%= (adminDistractor.isCorrect() || adminStem.isNew()) ? "checked=\"checked\" " : "" %>/>
								</div>
								<div class="right">
									<textarea name="answerAInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ANSWER (TRUE) FEEDBACK</div>
								<div class="right">
									<textarea name="answerAFeedbackInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getPrescriptiveFeedback() %></textarea>
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
								<div class="leftTM">ANSWER (FALSE)
									<label for="tfAnswerBCorrect">&nbsp;</label>
									<input name="correctSelect" id="tfAnswerBCorrect" type="radio" value="B" class="crirHiddenJS" onclick="" <%= adminDistractor.isCorrect() ? "checked=\"checked\" " : "" %>/>
								</div>
								<div class="right">
									<textarea name="answerBInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getNameString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ANSWER (FALSE) FEEDBACK</div>
								<div class="right">
									<textarea name="answerBFeedbackInput" rows="2" cols="35" class="textarea" style="width: 304px;"><%= adminDistractor.getPrescriptiveFeedback() %></textarea>
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
        if (stem.isMultipleChoice())
        {
%>
							<!-- BEGIN Question -->
							<div class="question">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 322px; text-align: left;  "><a href="admin-course-questions.jsp?id=<%= adminAssessment.getId() %>&stemId=<%= stem.getId() %>" title=""><%= stem.getNameString() %></a></td>
										<td style="width:  50px; text-align: center;  "><a href="admin-course-questions.jsp?moveUpId=<%= stem.getId() %>" title="Move Up">Up</a></td>
										<td style="width:  30px; text-align: center;  "><a href="admin-course-questions.jsp?moveDownId=<%= stem.getId() %>" title="Move Down">Down</a></td>
										<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.location.href='admin-course-questions.jsp?deleteId=<%= stem.getId() %>'}" title="Delete">Delete</a></td>
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
                if (distractor.isCorrect())
                    characterDisplayString = "<strong>" + questionCharacter + " " + distractor.getNameString() + "</strong>";
                else
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
										<td style="width: 322px; text-align: left;  "><a href="admin-course-questions.jsp?id=<%= adminAssessment.getId() %>&stemId=<%= stem.getId() %>" title=""><%= stem.getNameString() %></a></td>
										<td style="width:  50px; text-align: center;  "><a href="admin-course-questions.jsp?moveUpId=<%= stem.getId() %>" title="Move Up">Up</a></td>
										<td style="width:  30px; text-align: center;  "><a href="admin-course-questions.jsp?moveDownId=<%= stem.getId() %>" title="Move Down">Down</a></td>
										<td style="width:  70px; text-align: center;  "><a href="admin-course-questions.jsp?deleteId=<%= stem.getId() %>" title="Delete">Delete</a></td>
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
                if (distractor.isCorrect())
                    characterDisplayString = "<strong>" + questionCharacter + " " + distractor.getNameString() + "</strong>";
                else
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