function show(elementid) {
	for (i=0;i<document.getElementById(elementid).getElementsByTagName("div").length; i++) {
		if (document.getElementById(elementid).getElementsByTagName("div").item(i).className == "dinfo"){
			document.getElementById(elementid).getElementsByTagName("div").item(i).style.display = "block";
		}
	}
	for (i=0;i<document.getElementById(elementid).getElementsByTagName("div").length; i++) {
		if (document.getElementById(elementid).getElementsByTagName("div").item(i).className == "dimg"){
			document.getElementById(elementid).getElementsByTagName("div").item(i).style.display = "none";
		}
	}
}

function hide(elementid) {
	for (i=0;i<document.getElementById(elementid).getElementsByTagName("div").length; i++) {
		if (document.getElementById(elementid).getElementsByTagName("div").item(i).className == "dinfo"){
			document.getElementById(elementid).getElementsByTagName("div").item(i).style.display = "none";
		}
	}
	for (i=0;i<document.getElementById(elementid).getElementsByTagName("div").length; i++) {
		if (document.getElementById(elementid).getElementsByTagName("div").item(i).className == "dimg"){
			document.getElementById(elementid).getElementsByTagName("div").item(i).style.display = "block";
		}
	}
}

function p(title, summary) {
	document.getElementById('summary').innerHTML = "<h3>" + title + "</h3><p>" + summary + "</p>";
}

/*
	@obj doublecheck
	@desc This provides a JavaScript object that allows form-submission regular expression checking.
	@version 1.0.1
	@author Brian Legrow <brian@legrow.net>
	@legal For information on reproducing this script, please contact the author.  This script may not be used without the author's consent.
*/

function doublecheck()
{
	this.elements = new Array();

	this.validate = 
		function() {
			var rV = true;
			var errorArray = new Array();
			for(i in this.elements)
			{
				var validation = private_validateElement(this.elements[i]);
				if (!(validation[private_getValidationIndex("success")]))
				{
					errorArray.push(validation[private_getValidationIndex("error")]);
					rV = false;
				}
			}
			if (!rV)
				alert(errorArray.join("\n"));
			return rV;
		};

	this.addElement =
		function(id, regex, error) {
			this.elements.push(private_createElement(id, regex, error));
		};

	function private_validateElement(arr)
	{
		if (arr[private_getPropertyIndex("regex")] == "")
			return new Array(true, "");
		var el = document.getElementById(arr[private_getPropertyIndex("id")]);
		var reg = new RegExp(arr[private_getPropertyIndex("regex")]);
		var err = arr[private_getPropertyIndex("error")];
		var val = el.value;
		var rV = val.match(reg);
		return new Array(rV, err);
	}

	function private_createElement(id, regex, error)
	{
		if (!regex) regex = "";
		if (!error) error = "There was some problem with the '" + id + "' field.";
		return new Array(id, regex, error)
	}

	function private_getPropertyIndex(name)
	{
		if (name == "id")
			return 0;
		else if (name == "regex")
			return 1;
		else if (name == "error")
			return 2;
		else
			return -1;
	}

	function private_getValidationIndex(name)
	{
		if (name == "success")
			return 0;
		else if (name == "error")
			return 1;
		else
			return -1;
	}
}


function hasClass(obj) {
	var result = false;
	if (obj.getAttributeNode("class") != null) {
		result = obj.getAttributeNode("class").value;
	}
	return result;
}   

function stripe(id) {

	// the flag we'll use to keep track of 
	// whether the current row is odd or even
    var even = false;
  
    // if arguments are provided to specify the colours
    // of the even & odd rows, then use the them;
    // otherwise use the following defaults:
    var evenColor = arguments[1] ? arguments[1] : "#fff";
    var oddColor = arguments[2] ? arguments[2] : "#f4f4f4";
  
    // obtain a reference to the desired table
    // if no such table exists, abort
    var table = document.getElementById(id);
    if (! table) { return; }
    
    // by definition, tables can have more than one tbody
    // element, so we'll have to get the list of child
    // &lt;tbody&gt;s 
    var tbodies = table.getElementsByTagName("tbody");

    // and iterate through them...
    for (var h = 0; h < tbodies.length; h++) {
    
     // find all the &lt;tr&gt; elements... 
      var trs = tbodies[h].getElementsByTagName("tr");
      
      // ... and iterate through them
      for (var i = 0; i < trs.length; i++) {

        // avoid rows that have a class attribute
        // or backgroundColor style
        if (! hasClass(trs[i]) &&
            ! trs[i].style.backgroundColor) {
 		  
          // get all the cells in this row...
          var tds = trs[i].getElementsByTagName("td");
        
          // and iterate through them...
          for (var j = 0; j < tds.length; j++) {
        
            var mytd = tds[j];

            // avoid cells that have a class attribute
            // or backgroundColor style
            if (! hasClass(mytd) &&
                ! mytd.style.backgroundColor) {
        
              mytd.style.backgroundColor =
                even ? evenColor : oddColor;
            
            }
          }
        }
        // flip from odd to even, or vice-versa
        even =  ! even;
      }
    }
  }

function requestObject()
{
  var xmlhttp, bComplete = false;
  try { xmlhttp = new ActiveXObject("Msxml2.XMLHTTP"); }
  catch (e) { try { xmlhttp = new ActiveXObject("Microsoft.XMLHTTP"); }
  catch (e) { try { xmlhttp = new XMLHttpRequest(); }
  catch (e) { xmlhttp = false; }}}
  if (!xmlhttp) return null;
  this.connect = function(sURL, sMethod, sVars, fnDone)
  {
    if (!xmlhttp) return false;
    bComplete = false;
    sMethod = sMethod.toUpperCase();

    try {
      if (sMethod == "GET")
      {
        xmlhttp.open(sMethod, sURL+"?"+sVars, true);
        sVars = "";
      }
      else
      {
        xmlhttp.open(sMethod, sURL, true);
        xmlhttp.setRequestHeader("Method", "POST "+sURL+" HTTP/1.1");
        xmlhttp.setRequestHeader("Content-Type",
          "application/x-www-form-urlencoded");
      }
      xmlhttp.onreadystatechange = function(){
        if (xmlhttp.readyState == 4 && !bComplete)
        {
          bComplete = true;
          fnDone(xmlhttp);
        }};
      xmlhttp.send(sVars);
    }
    catch(z) { return false; }
    return true;
  };
  return this;
}

document.getElementsByClassName = function(searchClass, node, tag)
{  
	if(node == null) node = document;
	var ce = new Array();
	
	if(tag == null || tag == '*') tag = '*';
	var els = new Array();
	
	if (tag == '*' && document.evaluate)
	{
		var xpr = document.evaluate("//*", document, null, 0, null);
		var t = true;
		while (t=xpr.iterateNext())
		{
			if(els.push)
			els.push(t);
		else
			els[els.length]=t;
		}; 
	}
	else
	{
		els = node.getElementsByTagName(tag);
	}
	
	var elsLen = els.length;
	var pattern = new RegExp("(^|\\s)"+searchClass+"(\\s|$)");
	var i; var j;
	
	for (i = 0, j = 0; i < elsLen; i++)
	{
		if ( pattern.test(els[i].className) )
		if (ce.push)
			ce.push(els[i]);
		else
			ce[j++] = els[i];
	}
	
	return ce;
}

function getNodeValue(tree, el)
{
	return tree.getElementsByTagName(el)[0].firstChild.nodeValue;
}

var myRequestObject = new requestObject();

if(!myRequestObject)
{
	// browser can't handle AJAX
	showMessage("Your browser cannot handle AJAX.  Please upgrade.");
}

function rate(rating, designID)
{

	rating = parseInt(rating);
	if(rating != 1 && rating != 2 && rating != 3 && rating != 4 && rating != 5)
	{
		return false;
	}
	
	designID = parseInt(designID);
	if(!isFinite(designID))
	{
		return false;
	}
	
	myRequestObject.connect("/ajax/rate/index.php", "POST", "rating=" + rating + "&" + "designID=" + designID,
		function(xml)
		{

			status = getNodeValue(xml.responseXML, "status");

			if(status == "ok")
			{
			
				var designBlock = document.getElementById("id" + designID);
				var widthOfStar = 16;
				var paddingBetweenStars = 4;
				
				// updating the rating sample by incrementing the current one shown on the page
				var ratingSampleElements = document.getElementsByClassName("rsample", designBlock, "div");
				if(ratingSampleElements[0].innerHTML == "Not Yet Rated")
				{
					var currentRatingSample = 0;
				}
				else
				{
					var currentRatingSample = parseInt( (ratingSampleElements[0].innerHTML).match(/\b\d+\b/) );
				}
				var newRatingSample = currentRatingSample + 1;
				var tmp = "from " + newRatingSample;
				if(newRatingSample == 1)
				{
					tmp = tmp + " Rating";
				}
				else
				{
					tmp = tmp + " Ratings";
				}
				ratingSampleElements[0].innerHTML = tmp;

				// grabbing the current rating shown on the page
				var ratingElements = document.getElementsByClassName("rcurrent", designBlock, "li");
				var currentRating = parseFloat( (ratingElements[0].innerHTML).match(/(\d\.\d+)|(\d)/) );

				// calculating new rating
				var newRating = ( (currentRating * currentRatingSample) + rating) / newRatingSample;
				newRating = newRating.toFixed(2);
				
				// updating pixel width of stars
				var ratingWidth = parseInt( (parseInt(newRating) * paddingBetweenStars) + (newRating * widthOfStar) );
				ratingElements[0].style.width = ratingWidth + "px";

				// hiding stars so user is unable to vote again
				var tmpArray = new Array();
				tmpArray.push(designID);
				disableRatings(tmpArray);
				
			}
			
			if(status == "unauthorized")
			{
				showMessage("You must have an account to rate designs.  <a href=\"http://www.oswd.org/user/registration/\">Sign-Up</a> or <a href=\"http://www.oswd.org/\">Login</a>.");
			}
		}
	);
	
	return false;
	
}

function favorite(designID)
{

	designID = parseInt(designID);
	if(!isFinite(designID))
	{
		return false;
	}
	
	myRequestObject.connect("/ajax/favorite/index.php", "POST", "designID=" + designID,
		function(xml)
		{
			status = getNodeValue(xml.responseXML, "status");

			if(status == "added")
			{
				var designBlock = document.getElementById("id" + designID);
				var favoriteLinkElements = document.getElementsByClassName("dpfavorite", designBlock, "a");
				var favoriteElements = document.getElementsByClassName("add", designBlock, "span");
				favoriteElements[0].className = "remove";
				favoriteElements[0].innerHTML = "Remove from Favorites";
				favoriteLinkElements[0].title = "Remove from Favorites";
			}
			else if(status == "removed")
			{
				var designBlock = document.getElementById("id" + designID);
				var favoriteLinkElements = document.getElementsByClassName("dpfavorite", designBlock, "a");
				var favoriteElements = document.getElementsByClassName("remove", designBlock, "span");
				favoriteElements[0].className = "add";
				favoriteElements[0].innerHTML = "Add to Favorites";
				favoriteLinkElements[0].title = "Add to Favorites";
			}
		}
	);

	return false;

}


/*
function addLoadEvent(func)
{	
	var oldonload = window.onload;
	if (typeof window.onload != 'function')
	{
		alert("yes");
		window.onload = func;
	} 
	else
	{
		window.onload = function()
		{
			oldonload();
			func();
		}
	}
}
*/

function disableRatings(designArray)
{
	
	if(designArray.length == 0) { return false; }
	
	var designBlock;
	var ratingBlocks;
	var ratingStars;

	for(i = 0; i < designArray.length; i++)
	{
		designBlock = document.getElementById("id" + designArray[i]);	
		ratingBlocks = document.getElementsByClassName("rating", designBlock, "div");
		ratingStars = ratingBlocks[0].getElementsByTagName("li");
		for(p = 0; p < ratingStars.length; p++)
		{
			if(ratingStars.item(p).className != "rcurrent")
			{
				ratingStars[p].style.display = "none";
			}
		}
	}

}

function toggleDesignFavorites(designArray)
{
	if(designArray.length == 0) { return false; }
	
	var designBlock;
	var favoriteLinkElements;
	var favoriteElements;

	for(i = 0; i < designArray.length; i++)
	{
		designBlock = document.getElementById("id" + designArray[i]);
		favoriteLinkElements = document.getElementsByClassName("dpfavorite", designBlock, "a");
		favoriteElements = document.getElementsByClassName("add", designBlock, "span");
		favoriteElements[0].className = "remove";
		favoriteElements[0].innerHTML = "Remove from Favorites";
		favoriteLinkElements[0].title = "Remove from Favorites";
	}
}



function getPageSize(){
	
	var xScroll, yScroll;
	
	if (window.innerHeight && window.scrollMaxY)
	{	
		xScroll = document.body.scrollWidth;
		yScroll = window.innerHeight + window.scrollMaxY;
	}
	else if (document.body.scrollHeight > document.body.offsetHeight)
	{
		xScroll = document.body.scrollWidth;
		yScroll = document.body.scrollHeight;
	}
	else
	{
		xScroll = document.body.offsetWidth;
		yScroll = document.body.offsetHeight;
	}
	
	var windowWidth, windowHeight;
	if (self.innerHeight)
	{
		windowWidth = self.innerWidth;
		windowHeight = self.innerHeight;
	}
	else if (document.documentElement && document.documentElement.clientHeight)
	{
		windowWidth = document.documentElement.clientWidth;
		windowHeight = document.documentElement.clientHeight;
	}
	else if (document.body)
	{
		windowWidth = document.body.clientWidth;
		windowHeight = document.body.clientHeight;
	}	
	
	if(yScroll < windowHeight)
	{
		pageHeight = windowHeight;
	}
	else
	{ 
		pageHeight = yScroll;
	}

	if(xScroll < windowWidth)
	{	
		pageWidth = windowWidth;
	}
	else
	{
		pageWidth = xScroll;
	}


	arrayPageSize = new Array(pageWidth,pageHeight,windowWidth,windowHeight) 
	return arrayPageSize;
}

function selectBoxVisibility(visibility)
{
    selects = document.getElementsByTagName('select');
    for(i = 0; i < selects.length; i++)
	{
        selects[i].style.visibility = visibility;
    }
}

function browserIsIE()
{
	var agent = navigator.userAgent.toLowerCase();
	if(agent.indexOf("msie") < 0)
	{
		return false;
	}
	else
	{
		return true;
	}
}

function prepareIE(height, overflow)
{
	bod = document.getElementsByTagName('body')[0];
	bod.style.height = height;
	bod.style.overflow = overflow;
 
	htm = document.getElementsByTagName('html')[0];
	htm.style.height = height;
	htm.style.overflow = overflow; 
}

function getPageScroll()
{

	var yScroll;

	if (self.pageYOffset)
	{
		yScroll = self.pageYOffset;
	}
	else if (document.documentElement && document.documentElement.scrollTop)
	{
		yScroll = document.documentElement.scrollTop;
	}
	else if (document.body)
	{
		yScroll = document.body.scrollTop;
	}

	arrayPageScroll = new Array('',yScroll) 
	return arrayPageScroll;
}

function position()
{

	var objMessage = document.getElementById("message");
	var arrayPageScroll = getPageScroll();
	var arrayPageSize = getPageSize();
	var messageTop = arrayPageScroll[1] + ( (arrayPageSize[3] - objMessage.offsetHeight) / 2 );
	var messageLeft = (arrayPageSize[0] - 20 - objMessage.offsetWidth) / 2;
	

	
	objMessage.style.top = (messageTop < 0) ? "0px" : messageTop + "px";
	objMessage.style.left = (messageLeft < 0) ? "0px" : messageLeft + "px";
	
};

function showMessage(message)
{
	var objBody = document.getElementsByTagName("body").item(0);
	
	var objOverlay = document.createElement("div");
	objOverlay.setAttribute('id','overlay');
	objOverlay.onclick = function () { hideMessage(); return false; }
	objOverlay.setAttribute("title", "Click anywhere to close this message.");
	objBody.insertBefore(objOverlay, objBody.firstChild);
	
	var arrayPageSize = getPageSize();
	
	if(browserIsIE())
	{
		selectBoxVisibility("hidden");
	}
	
	objOverlay.style.height = (arrayPageSize[1] + 'px');
	objOverlay.style.display = 'block';

	var objMessage = document.createElement("div");
	objMessage.setAttribute('id','message');
	objMessage.style.display = "block";
	objMessage.style.visibility = "hidden";
	objMessage.style.position = 'absolute';
	objMessage.style.zIndex = '5001';
	objBody.insertBefore(objMessage, objOverlay.nextSibling);
	
	var objContent = document.createElement("div");
	objContent.setAttribute("id", "messagecontent");
	objContent.innerHTML = message;
	for (i=0;i<objContent.getElementsByTagName("a").length; i++)
	{
		objContent.getElementsByTagName("a").item(i).onclick = function () { hideMessage(); }
	}
	objMessage.appendChild(objContent);

	var objLink = document.createElement("a");
	objLink.setAttribute('id','close');
	objLink.setAttribute('href','#');
	objLink.setAttribute('title','Click to close this message.');
	objLink.onclick = function () {hideMessage(); return false;}
	objMessage.appendChild(objLink);
	
	var objImage = document.createElement("img");
	objImage.setAttribute('src', '/img/close_message.gif');
	objLink.appendChild(objImage);
	
	var arrayPageScroll = getPageScroll();
	
	var messageTop = arrayPageScroll[1] + ((arrayPageSize[3] - objMessage.offsetHeight) / 2);
	var messageLeft = (arrayPageSize[0] - 20 - objMessage.offsetWidth) / 2;

	objMessage.style.top = (messageTop < 0) ? "0px" : messageTop + "px";
	objMessage.style.left = (messageLeft < 0) ? "0px" : messageLeft + "px";
	
	objMessage.style.visibility = "visible";
	
	window.onresize = function() { position(); };
	window.onscroll = function() { position(); };
	
}

function hideMessage()
{
	objOverlay = document.getElementById('overlay');
	objMessage = document.getElementById('message');

	objOverlay.style.display = "none";
	objMessage.style.display = "none";
	objMessage.style.visibility = "hidden";

	if(browserIsIE())
	{
		selectBoxVisibility("visible");
	}
}