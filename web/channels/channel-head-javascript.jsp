	<script language="JavaScript" type="text/JavaScript">
	    <!--

  		function SwapOutX(x){
  		  eval("document.m" + x + ".src = Rollimage[(x * 2) - 1].src");
		  return true;
		}

		function SwapBackX(x){
		  var mx = eval("document.m" + x + ".src = Rollimage[(x * 2) - 2].src");
		  return true;
  		}

		function showMenux(i){
		    
		  document.getElementById('menux-'+i).style.visibility='visible';
  		}

		function hideMenux(i){
		  document.getElementById('menux-'+i).style.visibility='hidden';
  		}
		
		function
		confirmLogout()
		{
		    var answer = confirm("Are you sure you want to logout?");
		    if (answer) {
			    window.location.href = "logout.jsp?logout=true";
		    }
		    else {}
		}

		function
		getDate(form, name)
		{
			if (eval("document." + form + "." + name + ".value == ''"))
			{
				var now = new Date();
				var nowString = (now.getMonth() + 1) + "/" + now.getDate() + "/" + now.getFullYear();
				eval("document." + form + "." + name + ".value = '" + nowString + "'");
			}
		}

		function
		initErrors()
		{
		    // check for passed errors
		<%
		String oldSeperator = System.getProperty("line.separator");
		System.setProperty("line.separator", "");
		%>
		    var error = "<struts-html:errors/>";
		<%
		System.setProperty("line.separator", oldSeperator);
		%>
		    if (error.length != 0)
			alert(error);
		}
		
		function browserStuff() {

			if (document.forms[0] && document.forms[0].browser) {

				document.forms[0].browser.value = BrowserDetect.browser;
				document.forms[0].version.value = BrowserDetect.version;
			}
		}

	    //-->
	</script>