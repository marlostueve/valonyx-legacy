<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
<title>Chart with Legend Example</title>

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

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.6.0/build/fonts/fonts-min.css" />
<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/json/json-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/element/element-beta-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/datasource/datasource-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/charts/charts-experimental-min.js"></script>


<!--begin custom header content for this example-->
<style type="text/css">
	#chart
	{
		float: left;
		width: 450px;
		height: 300px;
	}

	.chart_title
	{
		display: block;
		font-size: 1.2em;
		font-weight: bold;
		margin-bottom: 0.4em;
	}
</style>
<!--end custom header content for this example-->

</head>

<body class=" yui-skin-sam">


<h1>Chart with Legend Example</h1>

<div class="exampleIntro">
	<p>A legend may be displayed with the <a href="http://developer.yahoo.com/yui/charts/">YUI Charts Control</a> by setting a few simple styles. This example shows you how.</p>
<p>Please note: The YUI Charts Control requires Flash Player 9.0.45 or higher. The latest version of Flash Player is available at the <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.</p>
</div>

<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

<span class="chart_title">Survey: What is your favorite season?</span>
<div id="chart">Unable to load Flash content. The YUI Charts Control requires Flash Player 9.0.45 or higher. You can install the latest version at the <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.</p></div>

<script type="text/javascript">

	YAHOO.widget.Chart.SWFURL = "http://yui.yahooapis.com/2.6.0/build//charts/assets/charts.swf";

//--- data

	YAHOO.example.publicOpinion =
	[
		{ response: "Summer", count: 564815 },
		{ response: "Fall", count: 664182 },
		{ response: "Spring", count: 248124 },
		{ response: "Winter", count: 271214 },
		{ response: "Undecided", count: 81845 }
	]

	var opinionData = new YAHOO.util.DataSource( YAHOO.example.publicOpinion );
	opinionData.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	opinionData.responseSchema = { fields: [ "response", "count" ] };

//--- chart

	var mychart = new YAHOO.widget.PieChart( "chart", opinionData,
	{
		dataField: "count",
		categoryField: "response",
		style:
		{
			padding: 20,
			legend:
			{
				display: "right",
				padding: 10,
				spacing: 5,
				font:
				{
					family: "Arial",
					size: 13
				}
			}
		},
		//only needed for flash player express install
		expressInstall: "assets/expressinstall.swf"
	});


</script>
<!--END SOURCE CODE FOR EXAMPLE =============================== -->


<!--MyBlogLog instrumentation-->
<script type="text/javascript" src="http://track2.mybloglog.com/js/jsserv.php?mblID=2007020704011645"></script>

</body>
</html>

<script type="text/javascript" src="http://l.yimg.com/d/lib/rt/rto1_78.js"></script><script>var rt_page="2012400067:FRTMA"; var rt_ip="71.195.14.13"; if ("function" == typeof(rt_AddVar) ){ rt_AddVar("ys", escape("F54C9345"));}</script><noscript><img src="http://rtb.pclick.yahoo.com/images/nojs.gif?p=2012400067:FRTMA"></noscript><script language=javascript>
if(window.yzq_d==null)window.yzq_d=new Object();
window.yzq_d['XdmsWUWTSQU-']='&U=13eh78im5%2fN%3dXdmsWUWTSQU-%2fC%3d289534.9603437.10326224.9298098%2fD%3dFOOT%2fB%3d4123617%2fV%3d1';
</script><noscript><img width=1 height=1 alt="" src="http://us.bc.yahoo.com/b?P=dy.6O0WTTNI6frMiSTGJwgjVR8MODUmPMhUAACZr&T=1422f3rl2%2fX%3d1234121237%2fE%3d2012400067%2fR%3ddev_net%2fK%3d5%2fV%3d2.1%2fW%3dH%2fY%3dYAHOO%2fF%3d687728573%2fQ%3d-1%2fS%3d1%2fJ%3dF54C9345&U=13eh78im5%2fN%3dXdmsWUWTSQU-%2fC%3d289534.9603437.10326224.9298098%2fD%3dFOOT%2fB%3d4123617%2fV%3d1"></noscript>
<!-- VER-592 -->
<script language=javascript>
if(window.yzq_p==null)document.write("<scr"+"ipt language=javascript src=http://l.yimg.com/d/lib/bc/bc_2.0.4.js></scr"+"ipt>");
</script><script language=javascript>
if(window.yzq_p)yzq_p('P=dy.6O0WTTNI6frMiSTGJwgjVR8MODUmPMhUAACZr&T=13u5edbaf%2fX%3d1234121237%2fE%3d2012400067%2fR%3ddev_net%2fK%3d5%2fV%3d1.1%2fW%3dJ%2fY%3dYAHOO%2fF%3d1222411115%2fS%3d1%2fJ%3dF54C9345');
if(window.yzq_s)yzq_s();
</script><noscript><img width=1 height=1 alt="" src="http://us.bc.yahoo.com/b?P=dy.6O0WTTNI6frMiSTGJwgjVR8MODUmPMhUAACZr&T=1436572pk%2fX%3d1234121237%2fE%3d2012400067%2fR%3ddev_net%2fK%3d5%2fV%3d3.1%2fW%3dJ%2fY%3dYAHOO%2fF%3d4246398040%2fQ%3d-1%2fS%3d1%2fJ%3dF54C9345"></noscript>
<!-- p6.ydn.re1.yahoo.com compressed/chunked Sun Feb  8 11:27:17 PST 2009 --> 