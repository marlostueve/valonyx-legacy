<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%



// 

%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Sano Wellness Center - Sign In</title>
		<meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
		
		
		
	
		<link rel="stylesheet" href="http://twitter.github.io/bootstrap/assets/css/bootstrap.css" />
		<style type="text/css">
		  body {
			padding-top: 60px;
			padding-bottom: 40px;
		  }
		</style>

		<link rel="stylesheet" href="http://twitter.github.io/bootstrap/assets/css/bootstrap-responsive.css" />
	
		<link rel="stylesheet" href="css/bootstrap.min.css" />
		<link rel="stylesheet" href="css/bootstrap-responsive.min.css" />
        <link rel="stylesheet" href="css/unicorn.login.css" />
		
    </head>
    <body style="background-color: black;">
        <div id="logo">
            <img src="images/Sano logo - 8-11.jpg" alt="Sano Wellness Center" />
        </div>
        <div id="loginbox" >
			<struts-html:form styleId="loginform" styleClass="form-vertical" action="/client/login" focus="password">
				<p>Please enter your password to sign in.</p>
                <div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"><i class="icon-lock"></i></span><input name="password" type="password" placeholder="Password" />
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <span class="pull-left"><a href="#" class="flip-link" id="fliptorecover">Don't have your password?</a></span>
                    <span class="pull-right"><input type="submit" class="btn btn-inverse" value="Sign In" /></span>
                </div>
			</struts-html:form>
            <form id="recoverform" action="#" class="form-vertical">
				<p>Enter your e-mail address below and we will send you instructions how to recover a password.</p>
				<div class="control-group">
                    <div class="controls">
                        <div class="input-prepend">
                            <span class="add-on"><i class="icon-envelope"></i></span><input type="text" placeholder="E-mail address" />
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <span class="pull-left"><a href="#" class="flip-link" id="fliptologin">&lt; Back to login</a></span>
                    <span class="pull-right"><input type="submit" class="btn btn-inverse" value="Recover" /></span>
                </div>
            </form>
        </div>
        
		<script src="http://twitter.github.io/bootstrap/assets/js/jquery.js"></script>
        <!-- <script src="js/jquery.min.js"></script>  -->
        <script>
			$(function(){

				// Checking for CSS 3D transformation support
				$.support.css3d = supportsCSS3D();

				var formContainer = $('#loginbox');

				// Listening for clicks on the ribbon links
				$('.flip-link').click(function(e){

					// Flipping the forms
					formContainer.toggleClass('flipped');

					// If there is no CSS3 3D support, simply
					// hide the login form (exposing the recover one)
					if(!$.support.css3d){
						$('#loginform').toggle();
					}
					e.preventDefault();
				});

				// A helper function that checks for the
				// support of the 3D CSS3 transformations.
				function supportsCSS3D() {
					var props = [
						'perspectiveProperty', 'WebkitPerspective', 'MozPerspective'
					], testDom = document.createElement('a');

					for(var i=0; i<props.length; i++){
						if(props[i] in testDom.style){
							return true;
						}
					}

					return false;
				}
			});        
        </script>  
    </body>
</html>
