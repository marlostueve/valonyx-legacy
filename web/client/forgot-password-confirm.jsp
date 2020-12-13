<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<%
userCompany = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5);
session.setAttribute("userCompany", userCompany);
%>

<!DOCTYPE html>
<html lang="en">
  <head>
<%@ include file="channels\channel-head.jsp" %>

    <style type="text/css">
      body {
        background-color: #f5f5f5;
      }

      .form-signin {
        max-width: 400px;
        padding: 19px 29px 29px;
        margin: 0 auto 20px;
        background-color: #fff;
        border: 1px solid #e5e5e5;
        -webkit-border-radius: 5px;
           -moz-border-radius: 5px;
                border-radius: 5px;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
           -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
                box-shadow: 0 1px 2px rgba(0,0,0,.05);
      }
      .form-signin .form-signin-heading,
      .form-signin .checkbox {
        margin-bottom: 10px;
      }
      .form-signin input[type="text"],
      .form-signin input[type="password"] {
        font-size: 16px;
        height: auto;
        margin-bottom: 15px;
        padding: 7px 9px;
      }

    </style>

  </head>
  <body>

<%@ include file="channels\channel-navbar.jsp" %>


    <div class="container">
			
      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">

        <h1>Send Password Confirmation</h1>

		<p>Please enter your email address below.  If we have your email address on file, we'll send your password to that address.</p>
		
<%
if (!loginBean.isLoggedIn()) {
%>
		<struts-html:form styleId="loginform" styleClass="form-signin" action="/client/lostPassword" focus="email">
			  <h2 class="form-signin-heading">Enter your email address</h2>
			  <input type="text" name="email" class="input-block-level" placeholder="Email Address">
			  <button class="btn btn-large btn-primary" type="submit">Send my password &raquo;</button>
			  <a style="margin-left: 10px;" class="btn btn-large" href="index.jsp">Go back</a>
		</struts-html:form>
<%
}
%>
		


      </div>
<%
if (loginBean.isLoggedIn()) {
%>
      <!-- Example row of columns -->
      <div class="row">
        <div class="span4">
          <h2>Intake Form</h2>
          <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          <p><a class="btn" href="intake.jsp">View details &raquo;</a></p>
        </div>
        <div class="span4">
          <h2>Wellness Survey</h2>
          <p>Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          <p><a class="btn" href="wellness-survey.jsp">View details &raquo;</a></p>
       </div>
        <div class="span4">
          <h2>Supplement Instructions</h2>
          <p>Donec sed odio dui. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Vestibulum id ligula porta felis euismod semper. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</p>
          <p><a class="btn" href="#">View details &raquo;</a></p>
        </div>
      </div>
<%
}
%>

      <hr>

<%@ include file="channels\channel-footer.jsp" %>

    </div> <!-- /container -->

<%@ include file="channels\channel-foot-js.jsp" %>

  </body>
</html>
