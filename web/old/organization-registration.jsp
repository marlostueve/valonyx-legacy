<%@ page buffer="12kb" contentType="text/html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/loose.dtd">

<struts-html:html locale="true">
 <head>
  <struts-html:base/>
  <title>Universal Knowledge - Register</title>
  <link rel="stylesheet" type="text/css" href="css/corpu-styles.css">
  <struts-html:javascript formName="loginForm" dynamicJavascript="true" staticJavascript="false"/>
  <script language="Javascript1.1" src="staticJavascript.jsp"></script>
  <%@ include file="channels/channel-head-javascript.jsp" %>
 </head>
 <body marginheight=0 marginwidth=0 leftmargin=0 topmargin=0 bgcolor="#ffffff" TEXT="#000000" LINK="#003399" VLINK="#CC0000">
  <table width="100%" cellpadding=4 cellspacing=0>
   <tr>
    <td bgcolor="#003399" align=center width="100%"><font class=SkylineWhite><big>U</big>NIVERSAL <big>K</big>NOWLEDGE</font></td>
   </tr>
  </table>
  <table width="100%" cellspacing=0 cellpadding=2>
   <tr>
    <td bgcolor="#bcd7f0" width="100%">&nbsp;&nbsp;<font class="Breadcrumb"><a href="index.jsp">Home</a></font> &nbsp;&gt;&nbsp; <font class="Breadcrumb"><a href="login.jsp">Login</a></font> &nbsp;&gt;&nbsp; <font class="Breadcrumb">Organization Registration</font></td>
   </tr>
  </table>
  <struts-html:form action="/organizationRegistration">
   <table width="100%">
    <tr><td align=center colspan=3><font class="Error"><struts-html:errors/></font></td></tr>
    <tr><td align=center colspan=3><br></td></tr>
    <tr><td align=center colspan=3><font class="Text2">Organization/Company Information:</font></td></tr>
    <tr><td align=center colspan=3><br></td></tr>
    <tr>
     <td align=right width="45%"><font class="Head3">Organization/Company Name:</font></td>
     <td align=left width="20%"><input type="text" name="organization" size="25" maxlength="255"></td>
     <td width="35%">&nbsp;</td>
    </tr>
    <tr><td align=center colspan=3><br></td></tr>
    <tr><td align=center colspan=3><font class="Text2">Organizational Administrator Information:</font></td></tr>
    <tr><td align=center colspan=3><br></td></tr>
    <tr>
     <td align=right width="45%"><font class="Head3">Username:</font></td>
     <td align=left width="20%"><input type="text" name="username" size="16" maxlength="80"></td>
     <td width="35%">&nbsp;</td>
    </tr>
    <tr>
     <td align=right width="45%"><font class="Head3">Password:</font></td>
     <td align=left width="20%"><input type="password" name="password" size="16" maxlength="50"></td>
     <td width="35%">&nbsp;</td>
    </tr>
    <tr>
     <td align=right width="45%"><font class="Head3">Confirm Password:</font></td>
     <td align=left width="20%"><input type="password" name="confirmPassword" size="16" maxlength="50"></td>
     <td width="35%">&nbsp;</td>
    </tr>
    <tr>
     <td align=right width="45%"><font class="Head3">First Name:</font></td>
     <td align=left width="20%"><input type="text" name="firstName" size="16" maxlength="20"></td>
     <td width="35%">&nbsp;</td>
    </tr>
    <tr>
     <td align=right width="45%"><font class="Head3">Last Name:</font></td>
     <td align=left width="20%"><input type="text" name="lastName" size="16" maxlength="30"></td>
     <td width="35%">&nbsp;</td>
    </tr>
    <tr>
     <td align=right width="45%"><font class="Head3">Email Address:</font></td>
     <td align=left width="20%"><input type="text" name="email" size="16" maxlength="80"></td>
     <td width="35%">&nbsp;</td>
    </tr>
    <tr><td align=center colspan=3><br></td></tr>
    <tr>
     <td align=right width="45%"></td>
     <td align=right width="20%">
      <input type="image" value="Login" src="images/Button_Login.gif" alt="Login">
     </td>
     <td width="35%">&nbsp;</td>
    </tr>
   </table>
   </struts-html:form>
  </table>
 </body>
 <struts-error:popupError stop="false" />
</struts-html:html>