<script TYPE="text/javascript">
<!--

function newImage(arg) {
	if (document.images) {
		rslt = new Image();
		rslt.src = arg;
		return rslt;
	}
}

function changeImages() {
	if (document.images && (preloadFlag == true)) {
		for (var i=0; i<changeImages.arguments.length; i+=2) {
			document[changeImages.arguments[i]].src = changeImages.arguments[i+1];
		}
	}
}

var preloadFlag = false;
function preloadImages() {
	if (document.images) {
		PttrsnUK_BTN_Home_over = newImage("../images/PttrsnUK_BTN-Home-over.jpg");
		PttrsnUK_BTN_Course_over = newImage("../images/PttrsnUK_BTN-Course-over.jpg");
		PttrsnUK_BTN_Libary_over = newImage("../images/PttrsnUK_BTN-Libary-over.jpg");
		PttrsnUK_BTN_FAQ_over = newImage("../images/PttrsnUK_BTN-FAQ-over.jpg");
		PttrsnUK_BTN_Reports_over = newImage("../images/PttrsnUK_BTN-Reports-over.jpg");
		PttrsnUK_BTN_Admin_over = newImage("../images/PttrsnUK_BTN-Admin-over.jpg");
		PttrsnUK_BTN_Logout_over = newImage("../images/PttrsnUK_BTN-Logout-over.jpg");
		preloadFlag = true;
	}
}

function setTableBG(tableId) {

    // this function alternates table row colors
    // define a table, give it a table id, and call this function in the onLoad function
    // in the BODY tag

    var tbl = document.getElementById(tableId)
    var color0 = "#FFFFFF"
    var color1 = "#EEEEEE"

    var i;
    for(i = 0;i<tbl.rows.length;i++){
        eval("tbl.rows(i).style.backgroundColor = color"+(i%2));
    }
}

function confirmLogout() {
   var answer = confirm("Are you sure you want to logout?");
   if (answer) {
      window.location.href = "http://www.teampatterson.com/";
   }
   else {}
}

function myVoid() {
}

function displayAsNewToggle()
{
    if (document.forms[0].displayAsNewInput.disabled == true)
        document.forms[0].displayAsNewInput.disabled = false;
    else
        document.forms[0].displayAsNewInput.disabled = true;
}

function makeActiveOnToggle(form)
{
    if (eval("document." + form + ".activeDateInput.disabled == true"))
        eval("document." + form + ".activeDateInput.disabled = false");
    else
        eval("document." + form + ".activeDateInput.disabled = true");
}

function
getDate(form, name)
{
        if (eval("document." + form + "." + name + ".value == ''"))
        {
                var now = new Date();
                var nowString = now.getMonth() + 1 + "/" + now.getDate() + "/" + now.getYear();
                eval("document." + form + "." + name + ".value = '" + nowString + "'");
        }
}

// -->
</script>