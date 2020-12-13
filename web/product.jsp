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
<title>Athlete's Power Tonic Eden Prairie - Chanhassen</title>

<meta name="description" content="Valeo Wellness Center in Eden Prairie offers Athlete's Power Tonic, a herbal formula provides rapid recovery from strenuous physical stress. Our Eden Prairie, MN office is open Monday - Friday, starting at 7 a.m.">
<meta name="keywords" content="Athlete's Power Tonic, Acupuncture, chiropractor, Valeo Wellness Center, natural health care, wellness center, chiropractic, massage, naturopathy,natural health solutions, back pain, lower back pain, headaches, alternative medicine, alternative health care, Eden Prairie, MN, Twin Cities, Edina, Chanhassen">

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
     <h1>Athlete's Power Tonic</h1><h2>The safe alternative to synthetic anabolic steroids for both men and women!!</h2>
		<div ><img src="img/bottle-hand234x395.gif" alt="Athlete's Power Tonic" style="float: left; padding: 10px;" />
		<h2>Price: 76.99<br />100 ml<br />Standard Formula</h2>
		<p>This herbal formula provides rapid recovery from strenuous physical stress. Used
when body/mind are being trained to perform optimally
and are being pushed beyond the normal limits.<br /><br />Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras tellus nibh, hendrerit non mollis in, imperdiet ac orci. Cras semper urna ac turpis mollis tincidunt. Ut fermentum vestibulum justo, eu placerat libero tincidunt ac. Curabitur volutpat pretium hendrerit. <br /><br />Aenean eget consequat risus. Maecenas dictum, tortor vitae scelerisque faucibus, nisi justo convallis metus, vel rhoncus dolor nulla ac libero. Cras malesuada sem non arcu sagittis imperdiet. Donec nec sem pretium velit venenatis consectetur. In et ante sed elit pulvinar luctus. Donec turpis nibh, elementum at interdum ac, ultrices eget sapien. <br /><br />Suspendisse in purus pellentesque erat venenatis feugiat eget sit amet lacus. </p>
      </div>
      <!-- end #video --><br />
<%

%>
    </div>
    <!-- end #content -->
    <div id="sidebar">

    <!-- BEGIN: Constant Contact Stylish Email Newsletter Form -->

    <form name="ccoptin" action="http://visitor.constantcontact.com/d.jsp" target="_blank" method="post" style="margin-bottom: 3em;">
	<h1>Order Form</h1>
      <p>Beginning weight prior to herb use ______ lbs; Weight this week _______ lbs.</p>
	  <p>Changes in digestion and/or bowel movements __ Yes __ No ~ please explain:</p>
      <div align="center">
	<input type="text" name="ea" size="20" value="" class="newsletterinput" style="font-family:Verdana,Geneva,Arial,Helvetica,sans-serif; font-size:14px; border:1px solid #999999;" />
	
	<input type="hidden" name="m" value="1102288667365" />
	<input type="hidden" name="p" value="oi" />
      </div>
	  
      <p>Changes in libido or sex drive __ Yes __ No ~ please explain (scale 1-10 ~ 10 highest):</p>
      <div align="center">
	<input type="text" name="ea" size="20" value="" class="newsletterinput" style="font-family:Verdana,Geneva,Arial,Helvetica,sans-serif; font-size:14px; border:1px solid #999999;" />
	
	<input type="hidden" name="m" value="1102288667365" />
	<input type="hidden" name="p" value="oi" />
      </div>
	  
      <p>Changes in energy level morning, afternoon, evening __ Yes __ No
~ please explain (scale 1-10):</p>
      <div align="center">
	<input type="text" name="ea" size="20" value="" class="newsletterinput" style="font-family:Verdana,Geneva,Arial,Helvetica,sans-serif; font-size:14px; border:1px solid #999999;" />
	
	<input type="hidden" name="m" value="1102288667365" />
	<input type="hidden" name="p" value="oi" />
      </div>
	  
      <p>Changes in mood __ Yes __ No ~ please explain (scale 1-10):</p>
      <div align="center">
	<input type="text" name="ea" size="20" value="" class="newsletterinput" style="font-family:Verdana,Geneva,Arial,Helvetica,sans-serif; font-size:14px; border:1px solid #999999;" />
	<br /><input type="submit" name="go" value="Submit Order" class="submit"  style="font-family:Verdana,Arial,Helvetica,sans-serif; font-size:10px;" />
	<input type="hidden" name="m" value="1102288667365" />
	<input type="hidden" name="p" value="oi" />
      </div>
    </form>
    <!-- END: Constant Contact Stylish Email Newsletter Form -->
<!--
    <h1>Newsletter Signup</h1>
      <p>Please enter your email address below...</p>
      <p align="center">
        <input name="newsletter" type="text" class="newsletterinput" />
      </p>
      <p align="center">
        <input type="image" src="img/buttonbg_newsletter.gif" name="submit" class="newsletterbutton" value="Send" />
      </p>
-->
      <!-- <h1>Latest News</h1> -->
      <h2>KEEP OUT OF REACH OF CHILDREN
SHAKE WELL BEFORE USE
STORE BELOW 86 F AND OUT OF DIRECT SUNLIGHT</h2>
      <p></p>
    </div>
    <!-- end #sidebar -->
    <div class="clear"> </div>
    <!-- end .clear -->
	
 
<div id="demo"> 
    <ul> 
        <li style="font-size: large; " ><a href="#foo">Instructions for Use</a></li> 
        <li style="font-size: large; "><a href="#bar">Product Info</a></li> 
        <li style="font-size: large; "><a href="#baz">FAQ</a></li> 
        <li style="font-size: large; "><a href="#bax">Testimonials</a></li> 
        <li style="font-size: large; "><a href="#nd">Email questions to an ND</a></li> 
    </ul> 
    <div> 
        <div id="foo"> 
			<ul style="font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; line-height: 2em; list-style-position: inside ; list-style-image: url(arrow.gif); list-style-type: square;" >
				<li>Men, please take a photo before you begin the herbs with your shirt off. Also take
one of your legs/thighs if that's an area you will be looking to develop. We need
before and after pictures.</li>
				<li>After initial visit to qualify for this herbal support, plan to come in two more times
one week apart to be rechecked for correct support amounts ~ it could change a
little in the first couple weeks depending on response and workouts.</li>
				<li>Best to use in conjunction with Rehydrate homeopathic for optimal cellular
hydration. (Product and information provided at Valeo)</li>
				<li>If you aren't able to get a workout in on any given day, skip the "post workout"
drops in water. Continue to take the regular drops in water as suggested at time of
initial visit or recheck.</li>
				<li>If you find you are fatigued post workout, please see Christine Stueve, ND @ Valeo
for another assessment.</li>
				<li>If you have trouble sleeping at night while taking the herbs, please contact or
schedule with Christine Stueve, ND @ Valeo for another assessment.</li>
				<li>Please track any and all changes you experience in how you feel pre and post
workouts, taking into consideration energy levels, sleep, mood, strength, appetite,
recovery after workouts, workout length, types of workouts, etc.</li>
				<li>Feel free to email feedback on a weekly basis to Christine Stueve, ND @
cstueve@valeowc.com</li>
			</ul>
        </div> 
        <div id="bar"> 
            <p>need content</p> 
        </div> 
        <div id="baz"> 
            <p>need content</p> 
        </div> 
        <div id="bax"> 
            <p>need content</p> 
        </div> 
        <div id="nd"> 
            <p>Email Form</p> 
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