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
	
<script type="text/javascript">

  function doPrice(qty) {
	  if (qty == '1') {
		document.getElementById('tax_id').value = '$1.03';
		document.getElementById('total_id').value = '$16.02';
	  } else if (qty == '2') {
		document.getElementById('tax_id').value = '$2.06';
		document.getElementById('total_id').value = '$32.04';
	  } else if (qty == '3') {
		document.getElementById('tax_id').value = '$3.09';
		document.getElementById('total_id').value = '$48.06';
	  } else if (qty == '4') {
		document.getElementById('tax_id').value = '$4.12';
		document.getElementById('total_id').value = '$64.08';
	  } else if (qty == '5') {
		document.getElementById('tax_id').value = '$5.15';
		document.getElementById('total_id').value = '$80.10';
	  } else if (qty == '6') {
		document.getElementById('tax_id').value = '$6.18';
		document.getElementById('total_id').value = '$96.12';
	  }

  }

</script>

  </head>
  <body>

<%@ include file="channels\channel-navbar.jsp" %>


    <div class="container">
		
		<div class="row">
			<div class="span4">
				<h1>Pre-Order<br />Dust to Diamonds<br /> HERE</h1>
				<img src="images/book.png" alt="Dust to Diamonds" />
				<h1>Projected release<br />November 2013</h1>
			</div>
        <div class="span8">
			
      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">

        <!--<h1>Dust to Diamonds</h1> -->


		<p>Thanks for your order!  Your order is being processed.  You will receive email confirmation shortly.</p>

<%



%>
		
<%

	String errorMessage = (String)session.getAttribute("forgot-password-message");
	if (errorMessage != null) {
%>
			<div class="alert alert-error">
				<button type="button" class="close" data-dismiss="alert">×</button>
				<h3>Oh Snap!!</h3>
				<strong>Unable to sign you in.</strong>  If you don't have your password, click <strong>I don't have my password</strong> above or ask the front desk for assistance.
			  
			</div>
<%
		session.removeAttribute("forgot-password-message");
	}


%>
		


      </div>

</div>
</div>

      <hr>

<%@ include file="channels\channel-footer.jsp" %>

    </div> <!-- /container -->

<%@ include file="channels\channel-foot-js.jsp" %>

  </body>
</html>
