    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="index.jsp" style="color: gold;">Sano Wellness</a>
          <div class="nav-collapse collapse">
            <ul class="nav">
              <!-- <li class="active"><a href="#">Home</a></li> -->
              <!-- <li><a href="#about">About</a></li> -->
              <!-- <li><a href="#contact">Contact</a></li> -->
              <!--
              <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li><a href="#">Action</a></li>
                  <li><a href="#">Another action</a></li>
                  <li><a href="#">Something else here</a></li>
                  <li class="divider"></li>
                  <li class="nav-header">Nav header</li>
                  <li><a href="#">Separated link</a></li>
                  <li><a href="#">One more separated link</a></li>
                </ul>
              </li>
              -->
            </ul>
            <form class="navbar-form pull-right">
              <!-- <input class="span2" type="password" placeholder="Password"> -->
				  
              <!-- <button type="submit" class="btn">Sign out</button> -->
<%
if (loginBean.isLoggedIn()) {
%>
				<a class="btn" href="change-password.jsp">Change Password</a>
			  <a class="btn" href="sign-out.jsp" style="margin-left: 10px;">Sign out</a>
<%
}
%>
            </form>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>