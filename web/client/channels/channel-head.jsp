
    <meta charset="utf-8">
    <title>Sano Wellness Center</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
	
	<link rel="stylesheet" href="https://www.valonyx.com/bootstrap/bootstrap/css/bootstrap.css" />
    <style type="text/css">
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
    </style>
		
	<link rel="stylesheet" href="https://www.valonyx.com/bootstrap/bootstrap/css/bootstrap-responsive.css" />

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="https://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <link rel="shortcut icon" href="https://www.valonyx.com/bootstrap/bootstrap/css/ico/favicon.ico">
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="https://www.valonyx.com/bootstrap/bootstrap/css/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="https://www.valonyx.com/bootstrap/bootstrap/css/ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="https://www.valonyx.com/bootstrap/bootstrap/css/ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="https://www.valonyx.com/bootstrap/bootstrap/css/ico/apple-touch-icon-57-precomposed.png">
	


	<script type="text/javascript">
<%
if (loginBean.isLoggedIn()) {
%>
    var mTimer;
	function autoSignOut()
    {
		if (mTimer) {
			document.location.href = 'sign-out.jsp';
		}
		else
			mTimer = setTimeout('autoSignOut();',600000);
    }

	autoSignOut();
	
	function waitOnKeyUp() {
		clearTimeout(mTimer);
		mTimer = setTimeout('autoSignOut();',600000);
	}
<%
}
%>

	</script>