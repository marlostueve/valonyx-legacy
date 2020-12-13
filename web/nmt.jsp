<%@ page contentType="text/html" import="java.util.*, java.text.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%
NewsItemBean content_index_page = null;
try
{
	content_index_page = NewsItemBean.getNewsItemBean(1);
}
catch (ObjectNotFoundException x)
{
	content_index_page = new NewsItemBean();
	content_index_page.setName("index");
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Stop Smoking Program Eden Prairie - Chanhassen</title>

<meta name="description" content="Valeo Wellness Center in Eden Prairie offers Athlete's Power Tonic, a herbal formula provides rapid recovery from strenuous physical stress. Our Eden Prairie, MN office is open Monday - Friday, starting at 7 a.m.">
<meta name="keywords" content="Stop Smoking, chiropractor, Valeo Wellness Center, natural health care, wellness center, chiropractic, massage, naturopathy,natural health solutions, back pain, lower back pain, headaches, alternative medicine, alternative health care, Eden Prairie, MN, Twin Cities, Edina, Chanhassen">

<link href="styles.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" type="text/javascript" src="js/jquery.js"></script>
<script language="JavaScript" type="text/javascript" src="js/cycle.js"></script>
<script language="JavaScript" type="text/javascript" src="js/behaviors.js"></script>
<script language="JavaScript" type="text/javascript" src="js/google-analytics.js"></script>


 
<style type="text/css"> 
/*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
body {
	margin:0;
	padding:0;
}
</style> 
 
<link type="text/css" rel="stylesheet" href="http://yui.yahooapis.com/3.3.0/build/cssfonts/fonts-min.css" /> 
<script type="text/javascript" src="http://yui.yahooapis.com/3.3.0/build/yui/yui-min.js"></script> 
 
 
<!--begin custom header content for this example--> 
<style id="yui3-style-overrides"> 
 
#main #example-canvas .yui3-tabview .yui3-tab-selected a {
	color:white;
}
 
</style> 
<!--end custom header content for this example--> 
 

<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-7869165-5']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>

</head>
<body class="yui3-skin-sam  yui-skin-sam">
<div class="container">
  <div id="header">
    <div id="logo"></div>
    <!-- end #logo -->
    <div class="imagecycle">
      <div id="imageslider">
        <div><img src="img/Image_Cycle/Image_1.gif" alt="Valeo Health &amp; Wellness Image 1" /></div>
        <div><img src="img/Image_Cycle/Image_2.gif" alt="Valeo Health &amp; Wellness Image 2" /></div>
        <div><img src="img/Image_Cycle/Image_3.gif" alt="Valeo Health &amp; Wellness Image 3" /></div>
      </div>
      <!-- end #imageslider -->
    </div>
    <!-- end .imagecycle -->
    <div class="clear"></div>
    <!-- end .clear -->
    <div id="tagline"></div>
    <!-- end #tagline -->
  </div>
  <!-- end #header -->
<%@ include file="channels\channel-nav.jsp" %>
  <div id="floatwrapper">
    <div id="content">
     <h1>Neuro-Metabolic Therapy (NMT)</h1><h2>Need Content</h2>
		<div ><iframe width=502 height=416 frameborder="0" scrolling="no" src="http://www.screencast-o-matic.com/embed?sc=cXhtb5Ftf&w=500&v=3"></iframe>
		<h2><font style="font-size: larger;" >Price: Need Content</font></h2>
		<p>Need Content<br /></p>
      </div>
      <!-- end #video --><br />
<%

%>
    </div>
    <!-- end #content -->
    <div id="sidebar">

    <!-- BEGIN: Constant Contact Stylish Email Newsletter Form -->

    
	<h1>Survey</h1>
      <p>Please take our online survey to help us better understand better how we can help you stop: </p>
	  <p>Need Content</p>
      
	  
	<h1>Visit Forms</h1>
      <p>Please take a moment to fill out these forms prior to your first visit: </p>
	  <p><a href="forms.jsp" >Click here to download the forms.</a></p>
	  
      
	  
     
    </div>
    <!-- end #sidebar -->
    <div class="clear"> </div>
    <!-- end .clear -->
	
 
<div id="demo"> 
    <ul> 
        <li style="font-size: large; " ><a href="#foo">Overview</a></li> 
        <li style="font-size: large; "><a href="#baz">FAQ</a></li> 
        <li style="font-size: large; "><a href="#res">Resources</a></li> 
        <li style="font-size: large; "><a href="#bax">Testimonials</a></li> 
        <li style="font-size: large; "><a href="#nd">Email questions to a Doctor</a></li> 
        <li style="font-size: large; "><a href="#vi">Instructions</a></li> 
    </ul> 
    <div> 
        <div id="foo"> 
			<ul style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; line-height: 2em; list-style-position: inside ; list-style-image: url(arrow.gif); list-style-type: square;" >
				<li>Need Content</li>
			</ul>
        </div> 
        <div id="baz"> 
            <p><strong>Q: Do I need content?</strong></p> 
            <p>A: Yes.  I need content.</p> 
		</div>
        <div id="res"> <br />Need Content
			
        </div> 
        <div id="bax"> 
            Need Content
        </div> 
        <div id="nd"> 
            <p>

					<struts-html:form action="/emailDoc" onsubmit="return validateLoginForm(this);" focus="email">

						<input id="dummy" name="dummy" type="hidden" />

						<div class="loginItem">
							<div class="left">PROVIDE YOUR EMAIL ADDRESS:</div>
							<div class="right"><input name="email" onfocus="select();" value="" size="37" maxlength="50" class="inputbox" style="width: 206px;" /></div>
							<div class="end"></div>
						</div>

						<div class="loginItem">
							<div class="left">EMAIL SUBJECT:</div>
							<div class="right"><input name="subject" onfocus="select();" value="" size="37" maxlength="50" class="inputbox" style="width: 206px;" /></div>
							<div class="end"></div>
						</div>

						<div class="loginItem">
							<div class="left">EMAIL TEXT:</div>
							<div class="right"><textarea name="text" rows="5"  class="inputbox" style="width: 412px;" ></textarea></div>
							<div class="end"></div>
						</div>

						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input class="formbutton" type="submit" value="Send Email" alt="Send Email" /></div>
							<div class="end"></div>
						</div>

					</struts-html:form></p> 
        </div> 
        <div id="vi"> 
            Need Content
        </div> 
    </div> 
</div> 
<script type="text/javascript"> 
YUI({ filter: 'raw' }).use("yui", "tabview", function(Y) {
    var tabview = new Y.TabView({srcNode:'#demo'});
    tabview.render();
});
</script> 
	<br /><br />
	
	<!--
    <div class="boxesbottom">
    <div class="boxoutside">
    <div class="boxinside">
    <h2>Directions for Use</h2>
    <ul>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_1") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_2") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_3") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_4") %></li>
    </ul>
    </div>
    </div>
    <div class="boxoutside">
    <div class="boxinside">
    <h2>Product Info / FAQ</h2>
    <ul>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_1") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_2") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_3") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_4") %></li>
    </ul>
    </div>
    </div>
    <div class="boxoutside">
    <div class="boxinside">
    <h2>Testimonials</h2>
    <ul>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_1") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_2") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_3") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_4") %></li>
    </ul>
    </div>
    </div>
    </div>
	-->
	
    <!-- end .boxesbottom -->
  </div>
  <!-- end #floatwrapper -->
  <div class="clearfooter"> </div>
  <!-- end .clearfooter -->
</div>
<!-- end .container -->
<%@ include file="channels\channel-footer-new.jsp" %>
</body>
</html> 