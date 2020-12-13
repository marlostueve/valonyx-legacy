<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*" %>

<%
ResourceBean resource = null;
String urlString = null;
try
{
    resource = ResourceBean.getResource(Integer.parseInt(request.getParameter("id")));
    if (CUBean.isMasterServer())
    {
	resource.recordHit();
	resource.save();
	urlString = "resources/" + resource.getURL();
    }
    else if (resource.hasMissingFiles())
    {
	urlString = "http://localhost:" + System.getProperty("cu.localPort") + "/content/resources/" + resource.getURL();
    }
    
    if (resource.getType().equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
    {
    }
    else if (resource.getType().equals(ResourceBean.CD_RESOURCE_TYPE))
    {
	urlString = "";
    }
    else if (resource.getType().equals(ResourceBean.CLASSROOM_RESOURCE_TYPE))
    {
	urlString = "";
    }
    else if (resource.getType().equals(ResourceBean.FLASH_RESOURCE_TYPE))
    {
    }
    else if (resource.getType().equals(ResourceBean.INTERNET_RESOURCE_TYPE))
    {
	urlString = resource.getURL();
    }
    else if (resource.getType().equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
    {
    }
    else if (resource.getType().equals(ResourceBean.VIDEO_RESOURCE_TYPE))
    {
    }
    else if (resource.getType().equals(ResourceBean.WORD_RESOURCE_TYPE))
    {
    }
    
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
<HTML>
<HEAD>
<TITLE><%= resource.getNameString() %></TITLE>
</HEAD>
<FRAMESET BORDER=0 FRAMEBORDER=NO FRAMESPACING=0 ROWS="*">
	<FRAME SRC="<%= urlString %>" NAME="ResourceTopFrame" MARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 SCROLLING=NO NORESIZE>
	<NOFRAMES>
	<BODY onBlur="self.focus()">
	Viewing this page requires a browser capable of displaying frames.
	</BODY>
	</NOFRAMES>
</FRAMESET>
</HTML>
