<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminAssessment" class="com.badiyan.uk.beans.AssessmentBean" scope="session" />
<jsp:useBean id="adminStem" class="com.badiyan.uk.beans.StemBean" scope="session" />
<jsp:useBean id="adminDistractor" class="com.badiyan.uk.beans.DistractorBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
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

String stepString = "Step 2: Questions";

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

<struts-html:html locale="true">
<head>
<struts-html:base/>
<title>Universal Knowledge</title>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<struts-html:javascript formName="assessmentQuestionForm" dynamicJavascript="true" staticJavascript="false"/>
<struts-html:javascript formName="assessmentTFQuestionForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src="../staticJavascript.jsp"></script>
<%@ include file="channels\channel-head-javascript.jsp" %>
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
        if (thisCheckbox != aCheck)
            aCheck.checked = false;
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

function
initForm()
{
<%
    if (!adminStem.isNew())
    {
        String formName = "assessmentQuestionForm";
        if (adminStem.isTrueFalse())
            formName = "assessmentTFQuestionForm";
        List distractors = adminStem.getDistractors();
        Iterator itr = distractors.iterator();
        char chVal = 'A';
        int num = 0;
        while (itr.hasNext())
        {
            DistractorBean distractor = (DistractorBean)itr.next();
            if (distractor.isCorrect())
            {
%>
    document.<%= formName %>.correct<%= chVal %>Select.checked = true;
<%
            }
            chVal++;
            num++;
        }
        String otherFormName = "assessmentTFQuestionForm";
        if (adminStem.isTrueFalse())
            otherFormName = "assessmentQuestionForm";
        if (num == 2)
        {
%>
    document.<%= otherFormName %>.questionInput.value = "";
    document.<%= otherFormName %>.answerAInput.value = "";
    document.<%= otherFormName %>.answerAFeedbackInput.value = "";
    document.<%= otherFormName %>.answerBInput.value = "";
    document.<%= otherFormName %>.answerBFeedbackInput.value = "";
    document.<%= otherFormName %>.answerCInput.value = "";
    document.<%= otherFormName %>.answerCFeedbackInput.value = "";
    document.<%= otherFormName %>.answerDInput.value = "";
    document.<%= otherFormName %>.answerDFeedbackInput.value = "";
    document.<%= otherFormName %>.answerEInput.value = "";
    document.<%= otherFormName %>.answerEFeedbackInput.value = "";
    document.<%= otherFormName %>.createQuestion.value = "Create Question";
    document.<%= otherFormName %>.createQuestion.disabled = "true";
<%
        }
        else
        {
%>
    document.<%= otherFormName %>.questionInput.value = "";
    document.<%= otherFormName %>.answerAInput.value = "";
    document.<%= otherFormName %>.answerAFeedbackInput.value = "";
    document.<%= otherFormName %>.answerBInput.value = "";
    document.<%= otherFormName %>.answerBFeedbackInput.value = "";
    document.<%= otherFormName %>.createQuestion.value = "Create Question";
    document.<%= otherFormName %>.createQuestion.disabled = "true";
<%
        }
    }
%>
}

// -->
</script>
</head>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages();initForm();">
<%@ include file="channels\channel-menu.jsp" %>
<!-- Begin Bottom Content Area -->
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<!-- Begin 24 Pixel Spacer -->
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=24 ALT=""></TD>
		<TD WIDTH=732 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=732 HEIGHT=24 ALT=""></TD>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=24 ALT=""></TD>
	</TR>
	<!-- End 24 Pixel Spacer -->
</TABLE>
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
<%@ include file="channels\channel-admin-menu.jsp" %>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- Begin Right Column Content -->
		<TD WIDTH=542 ALIGN=left VALIGN=top>
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">Assessment Generator&nbsp;&nbsp;&gt;&nbsp;&nbsp;<%= (adminAssessment.isNew()) ? "Create New" : "Edit" %> Assessment</P>
<%@ include file="channels\channel-assessment-menu.jsp" %>
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Create Multiple Choice Question</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/assessmentQuestion" focus="questionInput" onsubmit="return validateAssessmentQuestionForm(this);">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Question:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminStem" property="isNew" value="true">
                                                                        <struts-html:textarea property="questionInput" rows="4" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminStem" property="isNew" value="false">
                                                                        <struts-html:textarea property="questionInput" rows="4" style="font-size: 13px; width: 410px;" value="<%= adminStem.getNameString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
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
							<TR HEIGHT=30 BGCOLOR="#D4D4D4">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (A):<BR><IMG SRC="../images/trspacer.gif" WIDTH=22 HEIGHT=1 ALT=""><struts-html:checkbox property="correctASelect" value="true" onclick="manageChecks('assessmentQuestionForm','correctASelect')" /></P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerAInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerAInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getNameString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30 BGCOLOR="#D4D4D4">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (A)<br>&nbsp;Feedback:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerAFeedbackInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerAFeedbackInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getPrescriptiveFeedback() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<TR HEIGHT=30 BGCOLOR="#EBEBEB">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (B):<BR><IMG SRC="../images/trspacer.gif" WIDTH=22 HEIGHT=1 ALT=""><struts-html:checkbox property="correctBSelect" value="true" onclick="manageChecks('assessmentQuestionForm','correctBSelect')" /></P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerBInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerBInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getNameString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30 BGCOLOR="#EBEBEB">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (B)<br>&nbsp;Feedback:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerBFeedbackInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerBFeedbackInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getPrescriptiveFeedback() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>

							<TR HEIGHT=30 BGCOLOR="#D4D4D4">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (C):<BR><IMG SRC="../images/trspacer.gif" WIDTH=22 HEIGHT=1 ALT=""><struts-html:checkbox property="correctCSelect" value="true" onclick="manageChecks('assessmentQuestionForm','correctCSelect')" /></P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerCInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerCInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getNameString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30 BGCOLOR="#D4D4D4">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (C)<br>&nbsp;Feedback:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerCFeedbackInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerCFeedbackInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getPrescriptiveFeedback() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<TR HEIGHT=30 BGCOLOR="#EBEBEB">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (D):<BR><IMG SRC="../images/trspacer.gif" WIDTH=22 HEIGHT=1 ALT=""><struts-html:checkbox property="correctDSelect" value="true" onclick="manageChecks('assessmentQuestionForm','correctDSelect')" /></P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerDInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerDInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getNameString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30 BGCOLOR="#EBEBEB">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (D)<br>&nbsp;Feedback:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerDFeedbackInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerDFeedbackInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getPrescriptiveFeedback() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<TR HEIGHT=30 BGCOLOR="#D4D4D4">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (E):<BR><IMG SRC="../images/trspacer.gif" WIDTH=22 HEIGHT=1 ALT=""><struts-html:checkbox property="correctESelect" value="true" onclick="manageChecks('assessmentQuestionForm','correctESelect')" /></P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerEInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerEInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getNameString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30 BGCOLOR="#D4D4D4">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (E)<br>&nbsp;Feedback:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerEFeedbackInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerEFeedbackInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getPrescriptiveFeedback() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>

							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="createQuestion" VALUE="<%= (adminStem.isNew()) ? "Create" : "Save" %> Question"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Add True/False Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Create True&#47;False Question</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/assessmentTFQuestion" focus="questionInput" onsubmit="return validateAssessmentTFQuestionForm(this);">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Question:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminStem" property="isNew" value="true">
                                                                        <struts-html:textarea property="questionInput" rows="4" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminStem" property="isNew" value="false">
                                                                        <struts-html:textarea property="questionInput" rows="4" style="font-size: 13px; width: 410px;" value="<%= adminStem.getNameString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
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
							<TR HEIGHT=30 BGCOLOR="#D4D4D4">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (True):<BR><IMG SRC="../images/trspacer.gif" WIDTH=34 HEIGHT=1 ALT=""><struts-html:checkbox property="correctASelect" value="true" onclick="manageChecks('assessmentTFQuestionForm','correctASelect')" /></P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerAInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerAInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getNameString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30 BGCOLOR="#D4D4D4">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (True)<br>&nbsp;Feedback:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerAFeedbackInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerAFeedbackInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getPrescriptiveFeedback() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
<%
if (itr.hasNext())
    adminDistractor = (DistractorBean)itr.next();
else
    adminDistractor = new DistractorBean();
session.setAttribute("adminDistractor", adminDistractor);
%>
							<TR HEIGHT=30 BGCOLOR="#EBEBEB">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (False):<BR><IMG SRC="../images/trspacer.gif" WIDTH=34 HEIGHT=1 ALT=""><struts-html:checkbox property="correctBSelect" value="true" onclick="manageChecks('assessmentTFQuestionForm','correctBSelect')" /></P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerBInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerBInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getNameString() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=30 BGCOLOR="#EBEBEB">
								<TD VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Answer (False)<br>&nbsp;Feedback:</P></TD>
								<TD>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="true">
                                                                        <struts-html:textarea property="answerBFeedbackInput" rows="2" style="font-size: 13px; width: 410px;"></struts-html:textarea>
                                                                    </struts-logic:equal>
                                                                    <struts-logic:equal name="adminDistractor" property="isNew" value="false">
                                                                        <struts-html:textarea property="answerBFeedbackInput" rows="2" style="font-size: 13px; width: 410px;" value="<%= adminDistractor.getPrescriptiveFeedback() %>"></struts-html:textarea>
                                                                    </struts-logic:equal>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="createQuestion" VALUE="<%= (adminStem.isNew()) ? "Create" : "Save" %> Question"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Add True/False Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>

			<!-- Begin Assessment Questions Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Assessment Questions</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=40 BGCOLOR="#FFFFFF">
								<TD ALIGN=left COLSPAN=6><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;To edit a question, click on the question text. The correct answer is displayed in <B>bold</B> text.<BR>&nbsp;To adjust the ordering of a question, click on the appropriate arrow next to the question.</P></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=3 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=467 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
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
							<!-- Begin MC Question Entry -->
							<TR>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-assessment-questions.jsp?id=<%= adminAssessment.getId() %>&stemId=<%= stem.getId() %>" style="color: #000000"><%= stem.getNameString() %></A></P></TD>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=<%= request.getParameter("id") %>&moveUpId=<%= stem.getId() %>"><IMG SRC="../images/PttrsnUK_BTN-ArrowUp.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Up"></A></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=<%= request.getParameter("id") %>&moveDownId=<%= stem.getId() %>"><IMG SRC="../images/PttrsnUK_BTN-ArrowDown.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Down"></A></TD>
								<TD ALIGN=left><A HREF="javascript:if (confirm('Are you sure?')) location = 'admin-assessment-questions.jsp?id=<%= request.getParameter("id") %>&deleteId=<%= stem.getId() %>'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Question"></A></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
            List disList = stem.getDistractors();
            Iterator disItr = disList.iterator();
            char questionCharacter = 'A';
            while (disItr.hasNext())
            {
                DistractorBean distractor = (DistractorBean)disItr.next();
                String characterDisplayString;
                if (distractor.isCorrect())
                    characterDisplayString = "<B>" + questionCharacter + " " + distractor.getNameString() + "</B>";
                else
                    characterDisplayString = questionCharacter + " " + distractor.getNameString();
%>
							<TR BGCOLOR="#EEEEEE">
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #000000;"><%= characterDisplayString %> </P></TD>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
                questionCharacter++;
            }
%>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<!-- End MC Question Entry -->
<%
        }
        else if (stem.isTrueFalse())
        {
%>
                                                        <!-- Begin TF Question Entry -->
							<TR>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-assessment-questions.jsp?id=<%= adminAssessment.getId() %>&stemId=<%= stem.getId() %>" style="color: #000000"><%= stem.getNameString() %></A></P></TD>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=<%= request.getParameter("id") %>&moveUpId=<%= stem.getId() %>"><IMG SRC="../images/PttrsnUK_BTN-ArrowUp.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Up"></A></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=<%= request.getParameter("id") %>&moveDownId=<%= stem.getId() %>"><IMG SRC="../images/PttrsnUK_BTN-ArrowDown.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Down"></A></TD>
                                                                <TD ALIGN=left><A HREF="javascript:if (confirm('Are you sure?')) location = 'admin-assessment-questions.jsp?id=<%= request.getParameter("id") %>&deleteId=<%= stem.getId() %>'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Question"></A></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
            List disList = stem.getDistractors();
            Iterator disItr = disList.iterator();
            String questionCharacter = "(True)";
            while (disItr.hasNext())
            {
                DistractorBean distractor = (DistractorBean)disItr.next();
                String characterDisplayString;
                if (distractor.isCorrect())
                    characterDisplayString = "<B>" + questionCharacter + " " + distractor.getNameString() + "</B>";
                else
                    characterDisplayString = questionCharacter + " " + distractor.getNameString();
%>
                                                        <TR BGCOLOR="#EEEEEE">
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #000000;"><%= characterDisplayString %> </P></TD>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
                questionCharacter = "(False)";
            }
%>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<!-- End TF Question Entry -->
<%
        }
    }
}
else
{
%>
                                                        <TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
                                                        <TR>
								<TD ALIGN=left COLSPAN=8><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Questions Found</p></TD>
							</TR>
                                                        <TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
}
%>
							<TR>
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Assessment Questions Area -->

		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>