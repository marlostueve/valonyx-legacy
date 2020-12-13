
 
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> 
 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en"> 
	<head> 
		<title> Valonyx - Practice ManagementHome </title> 
 
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
 
		
		<meta name="description" content="Download and upload free web designs." /> 
		<meta name="keywords" content="oswd, open source, web design, design, xhtml, web standards, free web templates, web templates, templates, css templates, css" /> 
		<meta name="author" content="Francis J. Skettino" /> 
 
		<script type="text/javascript" src="../js/main.js"></script> 
 
		<link rel="stylesheet" type="text/css" href="../css/style.css" /> 
 
		<link rel="alternate" title="Open Source Web Design" type="application/rss+xml" href="/rss/" /> 
 
		<script type="text/javascript"> 
 
			var _gaq = _gaq || [];
			_gaq.push(['_setAccount', 'UA-90230-1']);
			_gaq.push(['_trackPageview']);
 
			(function() {
				var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
				ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
				var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
			})();
 
		</script> 
 
	</head> 
	<body> 
 
 
 
		<!-- START CONTAINER --> 
 
		<div id="container"> 
 
 
 
			<!-- START HEADER --> 
 
			<div id="header"> 
 
 
 
				<!-- START LOGO --> 
 
				<div id="logo"> 
 
					<h1>Open Source Web Design<a href="/"></a></h1> 
 
				</div> 
 
				<!-- END LOGO --> 
 
 
 
				<!-- START NAVIGATION HEADER --> 
 
				<div id="navigationheader"> 
 
					<h2>Site Navigation</h2> 
 
					<ul> 
						<li><a id="navinformation" href="/site/information/">About Us</a></li> 
						<li><a id="navsuggestion" href="/site/suggestion/">Make a Suggestion!</a></li> 
						<li><a id="navsitemap" href="/site/map/">Sitemap</a></li> 
					</ul> 
 
				</div> 
 
				<!-- END NAVIGATION HEADER --> 
 
 
 
				<!-- START INTRODUCTION --> 
 
				<div id="introduction"> 
 
					<h2>Have an Idea?</h2> 
 
					<p> 
						<strong>Open Source Web Design</strong> is constantly evolving.  We make the site better with your suggestions.  So let us know what you have in mind! 
					</p> 
 
					<h3><strong>Make a Suggestion</strong></h3> 
 
				</div> 
 
				<!-- END INTRODUCTION --> 
 
 
 
			</div> 
 
			<!-- END HEADER --> 
			
			
			
			<!-- START SIDEBAR --> 
 
			<div id="sidebar"> 
 
 
 
  				<!-- START NAVIGATION MAIN --> 
 
  				<div id="navigationmain"> 
 
					<h2>Sections</h2> 
 
					<ul> 
						<li><a id="navhome" href="/">Home</a></li> 
 
						<li><a id="navfavorites" href="/designs/favorites/">Our Favorite Designs</a></li> 
						<li><a id="navbrowse" href="/designs/browse/">Browse All Designs</a></li> 
						<li><a id="navsearch" href="/designs/search/">Search All Designs</a></li> 
						<li><a id="navbrowse" href="/designs/premium/">Premium Designs</a></li> 
					</ul> 
 
				</div> 
 
				<!-- END NAVIGATION MAIN --> 
 
								
					<div style="margin-top: 20px;"></div> 
 
								
				<!-- START NAVIGATION FEATURES --> 
 
  				<div id="navigationfeatures"> 
 
					<h2>Features</h2> 
 
					<ul> 
						<li><a id="navdesignologue" href="/designologue/">Designologue</a></li> 
						<li><a id="navstatistics" href="/designs/statistics/">The Most Popular</a></li> 
						<li><a id="navlinks" href="/links/">See Designs in Use</a></li> 
											</ul> 
 
				</div> 
 
				<!-- END NAVIGATION DESIGNS --> 
 
				
				<!-- START USER LOGIN --> 
 
				<div id="userlogin"> 
 
					<h2>Login</h2> 
 
					
					<form action="/user/login/" method="post" id="formlogin" onsubmit="return dbcklogin.validate();"> 
 
						<div> 
 
							<input type="hidden" name="action" value="login" class="hidden" /> 
	
							<input type="text" name="username" id="username" /> 
							<label for="username">Username</label> 
		
							<input type="password" name="password" id="password" /> 
							<label for="password">Password</label> 
		
							<a href="/user/lostpassword/">Forgot Your Password?</a> 
		
							<input type="submit" class="submit" value="Login to OSWD" /> 
		
							<strong>or</strong> 
		
							<a href="/user/registration/">Register Today!</a> 
		
						</div> 
 
					</form> 
 
					<script type="text/javascript"> 
					//<!-- <![CDATA[
 
						var dbcklogin = new doublecheck();
						dbcklogin.addElement("username", ".+", "You must enter a username.");
						dbcklogin.addElement("password", ".+", "You must enter a password.");
 
					// ]]> //-->
					</script> 
 
 
				</div> 
 
				<!-- END USER LOGIN --> 
 
 
				
				
			</div> 
 
  			<!-- END SIDEBAR --> 
 
 
 
			<!-- START CONTENT --> 
 
			<div id="content"> 
 
 
 
 
 
<!-- START CONTENT SECTION --> 
 
<div id="contentsection"> 
 
 
 
	<!-- SUGGESTION --> 
 
	<div id="suggestion"> 
 
		
		<p> 
			<strong>Let us know how we can improve our site.</strong>  We really love to hear from our visitors
			and often reply to every piece of mail we get.
		</p> 
 
		<form action="/site/suggestion/" method="post" id="formsuggestion" onsubmit="return dbck.validate();"> 
 
			<div> 
 
				<input type="hidden" name="action" value="makesuggestion" /> 
				<input type="hidden" name="key" value="5364a7c0aa" /> 
 
				<dl> 
						
					<dt><label for="email">Email Address</label></dt> 
					<dd><input type="text" id="email" name="email" value="" /></dd> 
							
					<dt><label for="message">Suggestion</label></dt> 
					<dd><textarea id="message" name="message"></textarea></dd> 
 
					<dt><label for="">Security Code</label></dt> 
					<dd><img src="/img/securityimg.php" width="200" height="40" alt="Security Code" /></dd> 
 
					<dt><label for="code">Security Code</label></dt> 
					<dd><input type="text" id="code" name="code" value="" /></dd> 
							
					<dt>&nbsp;</dt> 
					<dd><input type="submit" value="Send Suggestion" class="submit" /></dd> 
 
				</dl> 
 
			</div> 
 
		</form> 
 
		<script type="text/javascript"> 
		//<!-- <![CDATA[
 
			var dbck = new doublecheck();
			dbck.addElement("email", "^[a-zA-Z][a-zA-Z0-9_\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][a-zA-Z0-9_\\.-]*\\.[a-zA-Z]{2,4}$", "You must enter a valid email address.");
			dbck.addElement("message", ".+", "You must enter a suggestion.");
			dbck.addElement("code", ".+", "You must enter the security code.");
 
		// ]]> //-->
		</script> 
 
		
	</div> 
 
	<!-- END SUGGESTION --> 
 
 
 
 
</div> 
 
<!-- END CONTENT SECTION --> 
 
 
 
 
 
			</div> 
 
			<!-- END CONTENT --> 
 
 
 
			<!-- START FOOTER --> 
 
			<div id="footer"> 
			
				<!-- START COPYRIGHT --> 
 
				<div id="copyright"> 
					&copy; <a href="mailto:staff(at)oswd.org">OSWD</a> 2011				</div> 
					
				<!-- END COPYRIGHT --> 
			
 
			
				<!-- START NAVIGATION FOOTER --> 
			
				<div id="navigationfooter"> 
					<ul> 
						<li>Use our <a href="/rss/">RSS</a> feed.</li> 
						<li>This design by <a href="http://www.skettino.com/">fs</a> is Valid <a href="http://validator.w3.org/check?uri=http%3A%2F%2Fwww.oswd.org%2F">XHTML</a> and <a href="http://jigsaw.w3.org/css-validator/validator?uri=http%3A%2F%2Fwww.oswd.org%2Fcss%2Fdefault.css&amp;usermedium=all">CSS</a>.</li> 
						<li>Read our <a href="/site/privacy/">Privacy Policy</a> and <a href="/site/usage/">Usage Agreement</a>.</li> 
					</ul> 
				</div> 
 
				<!-- END NAVIGATION FOOTER --> 
					
			</div> 
 
			<!-- END FOOTER --> 
		
 
 
		</div> 
 
		<!-- END CONTAINER --> 
 
 
 
	</body> 
</html> 
 