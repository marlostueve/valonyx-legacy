<!-- BEGIN Header Graphics and Navigation Buttons -->
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<TR>
		<TD ROWSPAN=2><IMG SRC="../images/PttrsnUK_Logo.jpg" WIDTH=339 HEIGHT=68 ALT=""></TD>
		<TD><IMG SRC="../images/PttrsnUK_MenuTop.jpg" WIDTH=433 HEIGHT=36 ALT=""></TD>
	</TR>
	<TR HEIGHT=32>
		<TD WIDTH=433 BACKGROUND="../images/PttrsnUK_Status.jpg" VALIGN=middle ALIGN=center>
			<P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #003366;">Welcome <%= loginBean.getPerson().getFullName() %> to Universal Knowledge.</P>
		</TD>
	</TR>
</TABLE>
<%
boolean isSomeSortOfAdministrator = false;
try
{
    UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
    if (person != null)
    {
        if (loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) ||
                loginBean.getPerson().hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME) ||
                loginBean.getPerson().hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
            isSomeSortOfAdministrator = true;
    }
}
catch (Exception x)
{
}

boolean showCorp = false;
String corpString = (String)session.getAttribute("corporate");
if (corpString != null)
{
    if (corpString.equals("true"))
        showCorp = true;
    else
        showCorp = false;
}
if (showCorp)
{
    if (isSomeSortOfAdministrator)
    {
%>
<table WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<tr>
<%
        if (request.getRequestURI().indexOf("corporate-home.jsp") > -1)
        {
%>
                <TD><A HREF="../corporate-home.jsp?corporate=true"><img NAME="PttrsnUK_BTN_PLSHome" SRC="../images/PttrsnUK_BTN-PLSHome-current.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Home"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../corporate-home.jsp?corporate=true"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSHome', '../images/PttrsnUK_BTN-PLSHome-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSHome', '../images/PttrsnUK_BTN-PLSHome.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_PLSHome" SRC="../images/PttrsnUK_BTN-PLSHome.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Home"></a></td>
<%
        }
        if (request.getRequestURI().indexOf("course-catalog.jsp") > -1)
        {
%>
                <TD><A HREF="../course-catalog.jsp?corporate=true"><img NAME="PttrsnUK_BTN_PLSCourseCat" SRC="../images/PttrsnUK_BTN-PLSCourseCat-current.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="PLS Course Catalog"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../course-catalog.jsp?corporate=true"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSCourseCat', '../images/PttrsnUK_BTN-PLSCourseCat-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSCourseCat', '../images/PttrsnUK_BTN-PLSCourseCat.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_PLSCourseCat" SRC="../images/PttrsnUK_BTN-PLSCourseCat.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="PLS Course Catalog"></a></td>
<%
        }
%>
<%
        if (request.getRequestURI().indexOf("/library.jsp") > -1)
        {
%>
                <TD><A HREF="../library.jsp?corporate=true"><img NAME="PttrsnUK_BTN_PLSLibrary" SRC="../images/PttrsnUK_BTN-PLSLibrary-current.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Library"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../library.jsp?corporate=true"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSLibrary', '../images/PttrsnUK_BTN-PLSLibrary-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSLibrary', '../images/PttrsnUK_BTN-PLSLibrary.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_PLSLibrary" SRC="../images/PttrsnUK_BTN-PLSLibrary.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Library"></a></td>
<%
        }
%>

		<td><a HREF="../faq.jsp?corporate=true"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSFAQ', '../images/PttrsnUK_BTN-PLSFAQ-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSFAQ', '../images/PttrsnUK_BTN-PLSFAQ.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_PLSFAQ" SRC="../images/PttrsnUK_BTN-PLSFAQ.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS FAQ"></a></td>

		<td><a HREF="../index.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_MyHome', '../images/PttrsnUK_BTN-MyHome-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_MyHome', '../images/PttrsnUK_BTN-MyHome.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_MyHome" SRC="../images/PttrsnUK_BTN-MyHome.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My Home"></a></td>
<%
        if (request.getRequestURI().indexOf("admin") > -1)
        {
%>
                <TD><A HREF="index.jsp?corporate=true"><img NAME="PttrsnUK_BTN_PLSAdmin" SRC="../images/PttrsnUK_BTN-PLSAdmin-current.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="PLS Administration"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="index.jsp?corporate=true"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSAdmin', '../images/PttrsnUK_BTN-PLSAdmin-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSAdmin', '../images/PttrsnUK_BTN-PLSAdmin.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_PLSAdmin" SRC="../images/PttrsnUK_BTN-PLSAdmin.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="PLS Administration"></a></td>
<%
        }
%>
		<td><a HREF="#" ONCLICK="confirmLogout();"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_Logout', '../images/PttrsnUK_BTN-Logout-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_Logout', '../images/PttrsnUK_BTN-Logout.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_Logout" SRC="../images/PttrsnUK_BTN-Logout.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT=""></a></td>
	</tr>
</table>
<%
    }
    else
    {
%>
<table WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<tr>
		<td><img SRC="../images/PttrsnUK_BtmExpander.jpg" WIDTH=151 HEIGHT=27 ALT=""></td>
<%
        if (request.getRequestURI().indexOf("corporate-home.jsp") > -1)
        {
%>
                <TD><A HREF="../corporate-home.jsp?corporate=true"><img NAME="PttrsnUK_BTN_PLSHome" SRC="../images/PttrsnUK_BTN-PLSHome-current.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Home"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../corporate-home.jsp?corporate=true"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSHome', '../images/PttrsnUK_BTN-PLSHome-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSHome', '../images/PttrsnUK_BTN-PLSHome.jpg'); window.status=''; return true;">

				<img NAME="PttrsnUK_BTN_PLSHome" SRC="../images/PttrsnUK_BTN-PLSHome.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT=""></a></td>
<%
        }
%>
<%
        if (request.getRequestURI().indexOf("course-catalog.jsp") > -1)
        {
%>
                <TD><A HREF="../course-catalog.jsp?corporate=true"><img NAME="PttrsnUK_BTN_PLSCourseCat" SRC="../images/PttrsnUK_BTN-PLSCourseCat-current.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="PLS Course Catalog"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../course-catalog.jsp?corporate=true"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSCourseCat', '../images/PttrsnUK_BTN-PLSCourseCat-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSCourseCat', '../images/PttrsnUK_BTN-PLSCourseCat.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_PLSCourseCat" SRC="../images/PttrsnUK_BTN-PLSCourseCat.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="PLS Course Catalog"></a></td>
<%
        }
%>
<%
        if (request.getRequestURI().indexOf("/library.jsp") > -1)
        {
%>
                <TD><A HREF="../library.jsp?corporate=true"><img NAME="PttrsnUK_BTN_PLSLibrary" SRC="../images/PttrsnUK_BTN-PLSLibrary-current.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Library"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../library.jsp?corporate=true"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSLibrary', '../images/PttrsnUK_BTN-PLSLibrary-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSLibrary', '../images/PttrsnUK_BTN-PLSLibrary.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_PLSLibrary" SRC="../images/PttrsnUK_BTN-PLSLibrary.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Library"></a></td>
<%
        }
%>

		<td><a HREF="../faq.jsp?corporate=true"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSFAQ', '../images/PttrsnUK_BTN-PLSFAQ-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSFAQ', '../images/PttrsnUK_BTN-PLSFAQ.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_PLSFAQ" SRC="../images/PttrsnUK_BTN-PLSFAQ.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS FAQ"></a></td>

		<td><a HREF="../index.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_MyHome', '../images/PttrsnUK_BTN-MyHome-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_MyHome', '../images/PttrsnUK_BTN-MyHome.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_MyHome" SRC="../images/PttrsnUK_BTN-MyHome.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My Home"></a></td>

		<td><a HREF="#" ONCLICK="confirmLogout();"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_Logout', '../images/PttrsnUK_BTN-Logout-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_Logout', '../images/PttrsnUK_BTN-Logout.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_Logout" SRC="../images/PttrsnUK_BTN-Logout.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT=""></a></td>
	</tr>
</table>
<%
    }
}
else
{
    if (isSomeSortOfAdministrator)
    {
%>
<table WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<tr>
<%
        if (request.getRequestURI().indexOf("index.jsp") > -1 && request.getRequestURI().indexOf("admin") == -1)
        {
%>
                <TD><A HREF="../index.jsp"><img NAME="PttrsnUK_BTN_MyHome" SRC="../images/PttrsnUK_BTN-MyHome-current.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My Home"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../index.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_MyHome', '../images/PttrsnUK_BTN-MyHome-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_MyHome', '../images/PttrsnUK_BTN-MyHome.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_MyHome" SRC="../images/PttrsnUK_BTN-MyHome.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My Home"></a></td>
<%
        }
%>
<%
        if (request.getRequestURI().indexOf("course-catalog.jsp") > -1)
        {
%>
                <TD><A HREF="../course-catalog.jsp"><img NAME="PttrsnUK_BTN_MyCourseCat" SRC="../images/PttrsnUK_BTN-MyCourseCat-current.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="My Course Catalog"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../course-catalog.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_MyCourseCat', '../images/PttrsnUK_BTN-MyCourseCat-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_MyCourseCat', '../images/PttrsnUK_BTN-MyCourseCat.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_MyCourseCat" SRC="../images/PttrsnUK_BTN-MyCourseCat.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="My Course Catalog"></a></td>
<%
        }
%>
<%
        if (request.getRequestURI().indexOf("/library.jsp") > -1)
        {
%>
                <TD><A HREF="../library.jsp"><img NAME="PttrsnUK_BTN_MyLibrary" SRC="../images/PttrsnUK_BTN-MyLibrary-current.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My Library"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../library.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_MyLibrary', '../images/PttrsnUK_BTN-MyLibrary-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_MyLibrary', '../images/PttrsnUK_BTN-MyLibrary.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_MyLibrary" SRC="../images/PttrsnUK_BTN-MyLibrary.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My Library"></a></td>
<%
        }
%>

		<td><a HREF="../faq.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_MyFAQ', '../images/PttrsnUK_BTN-MyFAQ-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_MyFAQ', '../images/PttrsnUK_BTN-MyFAQ.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_MyFAQ" SRC="../images/PttrsnUK_BTN-MyFAQ.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My FAQ"></a></td>

<%
        if (request.getRequestURI().indexOf("corporate-home.jsp") > -1)
        {
%>
                <TD><A HREF="../corporate-home.jsp"><img NAME="PttrsnUK_BTN_PLSHome" SRC="../images/PttrsnUK_BTN-PLSHome-current.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Home"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../corporate-home.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSHome', '../images/PttrsnUK_BTN-PLSHome-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSHome', '../images/PttrsnUK_BTN-PLSHome.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_PLSHome" SRC="../images/PttrsnUK_BTN-PLSHome.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Home"></a></td>
<%
        }
%>
<%
        if (request.getRequestURI().indexOf("admin") > -1)
        {
%>
                <TD><A HREF="index.jsp"><img NAME="PttrsnUK_BTN_PLSAdmin" SRC="../images/PttrsnUK_BTN-PLSAdmin-current.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="PLS Administration"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="index.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSAdmin', '../images/PttrsnUK_BTN-PLSAdmin-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSAdmin', '../images/PttrsnUK_BTN-PLSAdmin.jpg'); window.status=''; return true;">

				<img NAME="PttrsnUK_BTN_PLSAdmin" SRC="../images/PttrsnUK_BTN-PLSAdmin.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="PLS Administration"></a></td>
<%
        }
%>
		<td><a HREF="#" ONCLICK="confirmLogout();"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_Logout', '../images/PttrsnUK_BTN-Logout-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_Logout', '../images/PttrsnUK_BTN-Logout.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_Logout" SRC="../images/PttrsnUK_BTN-Logout.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT=""></a></td>
	</tr>
</table>
<%
    }
    else
    {
%>
<table WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<tr>
		<td><img SRC="../images/PttrsnUK_BtmExpander.jpg" WIDTH=151 HEIGHT=27 ALT=""></td>
<%
        if (request.getRequestURI().indexOf("index.jsp") > -1 && request.getRequestURI().indexOf("admin") == -1)
        {
%>
                <TD><A HREF="../index.jsp"><img NAME="PttrsnUK_BTN_MyHome" SRC="../images/PttrsnUK_BTN-MyHome-current.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My Home"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../index.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_MyHome', '../images/PttrsnUK_BTN-MyHome-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_MyHome', '../images/PttrsnUK_BTN-MyHome.jpg'); window.status=''; return true;">

				<img NAME="PttrsnUK_BTN_MyHome" SRC="../images/PttrsnUK_BTN-MyHome.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My Home"></a></td>
<%
        }
%>
<%
        if (request.getRequestURI().indexOf("course-catalog.jsp") > -1)
        {
%>
                <TD><A HREF="../course-catalog.jsp"><img NAME="PttrsnUK_BTN_MyCourseCat" SRC="../images/PttrsnUK_BTN-MyCourseCat-current.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="My Course Catalog"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../course-catalog.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_MyCourseCat', '../images/PttrsnUK_BTN-MyCourseCat-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_MyCourseCat', '../images/PttrsnUK_BTN-MyCourseCat.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_MyCourseCat" SRC="../images/PttrsnUK_BTN-MyCourseCat.jpg" WIDTH=151 HEIGHT=27 BORDER=0 ALT="My Course Catalog"></a></td>
<%
        }
%>
<%
        if (request.getRequestURI().indexOf("/library.jsp") > -1)
        {
%>
                <TD><A HREF="../library.jsp"><img NAME="PttrsnUK_BTN_MyLibrary" SRC="../images/PttrsnUK_BTN-MyLibrary-current.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My Library"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../library.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_MyLibrary', '../images/PttrsnUK_BTN-MyLibrary-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_MyLibrary', '../images/PttrsnUK_BTN-MyLibrary.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_MyLibrary" SRC="../images/PttrsnUK_BTN-MyLibrary.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My Library"></a></td>
<%
        }
%>

		<td><a HREF="../faq.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_MyFAQ', '../images/PttrsnUK_BTN-MyFAQ-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_MyFAQ', '../images/PttrsnUK_BTN-MyFAQ.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_MyFAQ" SRC="../images/PttrsnUK_BTN-MyFAQ.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="My FAQ"></a></td>

<%
        if (request.getRequestURI().indexOf("corporate-home.jsp") > -1)
        {
%>
                <TD><A HREF="../corporate-home.jsp"><img NAME="PttrsnUK_BTN_PLSHome" SRC="../images/PttrsnUK_BTN-PLSHome-current.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Home"></A></TD>
<%
        }
        else
        {
%>
		<td><a HREF="../corporate-home.jsp"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_PLSHome', '../images/PttrsnUK_BTN-PLSHome-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_PLSHome', '../images/PttrsnUK_BTN-PLSHome.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_PLSHome" SRC="../images/PttrsnUK_BTN-PLSHome.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT="PLS Home"></a></td>
<%
        }
%>

		<td><a HREF="#" ONCLICK="confirmLogout();"
				ONMOUSEOVER="changeImages('PttrsnUK_BTN_Logout', '../images/PttrsnUK_BTN-Logout-over.jpg'); window.status=''; return true;"
				ONMOUSEOUT="changeImages('PttrsnUK_BTN_Logout', '../images/PttrsnUK_BTN-Logout.jpg'); window.status=''; return true;">
				<img NAME="PttrsnUK_BTN_Logout" SRC="../images/PttrsnUK_BTN-Logout.jpg" WIDTH=94 HEIGHT=27 BORDER=0 ALT=""></a></td>
	</tr>
</table>
<%
    }
}
%>
<!-- END Header Graphics and Navigation Buttons -->