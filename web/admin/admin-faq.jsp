<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminFAQ" class="com.badiyan.uk.beans.FAQBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
if (request.getParameter("new") != null)
{
    if (request.getParameter("new").equals("true"))
        adminFAQ = new FAQBean();
}
else
{
    if (request.getParameter("id") != null)
    {
        try
        {
            adminFAQ = FAQBean.getFAQ(Integer.parseInt(request.getParameter("id")));
        }
        catch (NumberFormatException x)
        {
        }
    }
}

session.setAttribute("adminFAQ", adminFAQ);

String menuString = "1";

listHelper.setCompany(adminCompany);
%>

<struts-html:html locale="true">
<HEAD>
<struts-html:base/>
<TITLE>Universal Knowledge</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<struts-html:javascript formName="faqBasicForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src="../staticJavascript.jsp"></script>
<%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--

function
displayAsNewCheck()
{
    if (document.faqBasicForm.displayAsNew.checked == true)
        document.faqBasicForm.displayAsNewInput.disabled = false;
    else
        document.faqBasicForm.displayAsNewInput.disabled = true;
}

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
if (!adminFAQ.isNew())
{
%>
    document.faqBasicForm.nameInput.value = '<%= adminFAQ.getName() %>';
<% if (!adminFAQ.getCategoryNameString().equals("")) { %>   document.faqBasicForm.categorySelect.value = '<%= adminFAQ.getCategoryIdString() %>'; <% } %>
    document.faqBasicForm.releasedDateInput.value = '<%= adminFAQ.getReleasedDateString() %>';
    document.faqBasicForm.ownerSelect.value = '<%= adminFAQ.getOwnerIdString() %>';
<%
    if (adminFAQ.getDisplayAsNewDays() > 0)
    {
%>
    document.faqBasicForm.displayAsNew.checked = true;
    document.faqBasicForm.displayAsNewInput.disabled = false;
    document.faqBasicForm.displayAsNewInput.value = '<%= adminFAQ.getDisplayAsNewDays() %>';
<%
    }
    else
    {
%>
    document.faqBasicForm.displayAsNew.checked = false;
<%
    }
%>
    document.faqBasicForm.expiresDateInput.value = '<%= adminFAQ.getExpirationDateString() %>';

<%
    Iterator audience_itr = listHelper.getAudiences().iterator();
    for (int i = 0; audience_itr.hasNext(); i++)
    {
        AudienceBean audience_obj = (AudienceBean)audience_itr.next();
        if (adminFAQ.isViewableBy(audience_obj))
        {
%>
    document.forms[0].audienceSelect.options[<%= i %>].selected = true;

<%
        }
    }
%>

<%
}
%>

}

// -->
</script>
</HEAD>
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
    <struts-html:form action="/admin/faqBasic" focus="nameInput" onsubmit="return validateFaqBasicForm(this);">
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
<%@ include file="channels\channel-admin-menu.jsp" %>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- Begin Right Column Content -->
		<TD WIDTH=542 ALIGN=left VALIGN=top>
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">FAQ Manager&nbsp;&nbsp;&gt;&nbsp;&nbsp;Create New FAQ</P>
<%@ include file="channels\channel-faq-menu.jsp" %>
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>
			<!-- Begin Basic Information Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Basic Information</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=105 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=415 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Last Update:</P></TD>
								<TD><P style="font-size: 10pt; font-family: Arial; color: #000000;"><%= adminFAQ.getModificationDateString() %></P></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;FAQ Name:</P></TD>
								<TD>
                                                                    <struts-html:text property="nameInput" style="font-size: 9pt; font-family: Arial; width: 405px;" />
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Category:</P></TD>
								<TD ALIGN=left>
                                                                    <struts-html:select property="categorySelect" style="font-size: 9pt; font-family: Arial; width: 405px;" >
                                                                        <struts-html:option value="0">-- SELECT A CATEGORY --</struts-html:option>
                                                                        <struts-html:optionsCollection name="listHelper" property="categories" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=left VALIGN=top><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Audiences:</P></TD>
								<TD ALIGN=left>
                                                                    <struts-html:select property="audienceSelect" style="font-size: 9pt; font-family: Arial; width: 405px;" multiple="true" size="4" >
                                                                        <struts-html:optionsCollection name="listHelper" property="audiences" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Owner:</P></TD>
								<TD>
                                                                    <struts-html:select property="ownerSelect" style="font-size: 9pt; font-family: Arial; width: 405px;" >
                                                                        <struts-html:option value="0">-- SELECT AN OWNER FOR THIS FAQ --</struts-html:option>
                                                                        <struts-html:optionsCollection name="listHelper" property="possibleFAQOwners" />
                                                                    </struts-html:select>
								</TD>
							</TR>
							<TR HEIGHT=4>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
                                                        <!--
							<TR HEIGHT=30>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Created:</P></TD>
								<TD ALIGN=left>
									<INPUT NAME="createdDateInput" TYPE="text" ID="createdDateInput" onFocus="select();" value="XX/XX/XXXX" STYLE="font-size: 13px; width: 80px;">
								</TD>
							</TR>
                                                        -->
							<TR HEIGHT=4>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Released:</P></TD>
								<TD ALIGN=left>
                                                                    <struts-html:text property="releasedDateInput" style="font-size: 9pt; font-family: Arial; width: 80px;" maxlength="10" onfocus="getDate('faqBasicForm','releasedDateInput');" />
								</TD>
							</TR>
							<TR>
								<TD ALIGN=left COLSPAN=2>
									<P style="font-size: 10pt; font-family: Arial; color: #000000;">
									<IMG SRC="../images/trspacer.gif" WIDTH=96 HEIGHT=1 ALT="">
									<struts-html:checkbox property="displayAsNew" value="true" onclick="displayAsNewCheck()" />Display as New for&nbsp;&nbsp;<struts-html:text property="displayAsNewInput" style="font-size: 9pt; font-family: Arial; width: 30px;" disabled="true" />&nbsp;days
									</P>
								</TD>
							</TR>
							<TR>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD WIDTH=105 ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Expires:</P></TD>
								<TD WIDTH=415 ALIGN=left>
                                                                    <struts-html:text property="expiresDateInput" style="font-size: 9pt; font-family: Arial; width: 80px;" maxlength="10" onfocus="getDate('faqBasicForm','expiresDateInput');" />
								</TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Basic Information Area -->
		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
    </struts-html:form>
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>