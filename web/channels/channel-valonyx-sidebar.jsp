			<!-- START SIDEBAR --> 
 
			<div id="sidebar"> 
 
 
 
				
				<!-- START USER LOGIN --> 
 
				<div id="userlogin"> 
 
					<h2>Log In</h2> 
 
					
					<struts-html:form action="/admin/login" onsubmit="return validateLoginForm(this);" focus="username">
 
						<div> 
 
							<input type="hidden" name="action" value="login" class="hidden" /> 
	
							<input type="text" name="username" id="username" /> 
							<label for="username">Username</label> 
		
							<input type="password" name="password" id="password" /> 
							<label for="password">Password</label> 
							
							<input type="submit" class="submit" value="Log In to Valonyx" /> 
		
							<a href="forgot-password.jsp">Forgot Your Password?</a> 
		
							<!--
							<strong>or</strong> 
							-->
		
							
							<a href="<%= CUBean.getProperty("signup_url") %>">Register Now to get your Practice Online!</a> 
							
							<input type="hidden" name="browser" />
							<input type="hidden" name="version" />
		
						</div> 
 
					</struts-html:form>
 
 
				</div> 
 
				<!-- END USER LOGIN --> 
 
								
					<div style="margin-top: 20px;"></div> 
 
  				<!-- START NAVIGATION MAIN --> 
 
  				<div id="navigationfeatures"> 
 
					<!-- <h2>Sections</h2>  -->
 
					<ul> 
						<li><a id="navfeatureshome" href="index.jsp">Home</a></li>
						<li><a id="navfeaturesfeatures" href="features.jsp">Features</a></li>
						<li><a id="navfeaturesvideos" href="support-videos.jsp">Support Videos</a></li>
						<li><a id="navfeaturessubscriptions" href="subscriptions.jsp">Subscriptions</a></li>
						<li><a id="navfeaturesrequirements" href="system-requirements.jsp">Requirements</a></li>
					</ul>
 
				</div> 
 
				<!-- END NAVIGATION MAIN --> 
 
 
 
				
				
			</div> 
 
  			<!-- END SIDEBAR --> 