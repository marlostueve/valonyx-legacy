// JavaScript Document

$(document).ready(function(){
	$('#imageslider').cycle({ 
    	fx: 'fade', 
    	speed: 'slow', 
    	timeout: 7000,  
		next: '#next', 
    	prev: '#previous' 
	});
});

  
sfHover = function() {
	var sfEls = document.getElementById("nav").getElementsByTagName("LI");
	for (var i=0; i<sfEls.length; i++) {
		sfEls[i].onmouseover=function() {
			this.className+=" sfhover";
		}
		sfEls[i].onmouseout=function() {
			this.className=this.className.replace(new RegExp(" sfhover\\b"), "");
		}
	}
}
if (window.attachEvent) window.attachEvent("onload", sfHover);
