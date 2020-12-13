<h1>Memory Status:</h1>
<%
  boolean collected = false;
  if (request.getParameter("gc") != null) {
    System.gc();
    collected = true;
  }

  out.println("<HR>");
  Runtime rt = Runtime.getRuntime();
  long tm = rt.totalMemory(),
       mm = rt.maxMemory(),
       fm = rt.freeMemory();

  out.println("Max memory: "+mm+" ("+mm/1024/1024+"MB)<BR>");
  out.println("Total Memory: "+tm+" ("+tm/1024/1024+"MB)<BR>");
  out.println("Free memory: "+fm+" ("+fm/1024/1024+"MB)<BR>");


%>