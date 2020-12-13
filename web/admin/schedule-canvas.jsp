
<html> 
 <head> 
  <script type="application/x-javascript"> 
    function draw() {
      var canvas = document.getElementById("canvas");
      if (canvas.getContext) {
        var ctx = canvas.getContext("2d");
 
		
        ctx.fillStyle = "rgb(40,0,0)";
        ctx.fillRect (20, 20, 65, 60);
 
        ctx.fillStyle = "rgba(0, 0, 160, 0.5)";
        ctx.fillRect (40, 40, 65, 60);
		
		
		
		for (var x = 0.5; x < 500; x += 10) {
			ctx.moveTo(x, 0);
			ctx.lineTo(x, 375);
		}
		
		for (var y = 0.5; y < 375; y += 10) {
			ctx.moveTo(0, y);
			ctx.lineTo(500, y);
		}
		
		ctx.strokeStyle = "#ccc";
		ctx.stroke();
		
		ctx.fillStyle = "rgb(40,0,0)";
		
		ctx.font = "bold 12px sans-serif";
		ctx.textBaseline = "top";
		ctx.fillText("she's getting away with it", 248, 43);
		ctx.fillText("y", 58, 165);
		
      }
    }
  </script> 
 </head> 
 <body onLoad="draw();"> 
   <canvas id="canvas" width="750" height="750"></canvas> 
 </body> 
</html> 