<%@page language="java"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>
 
<html:html>
<html:form enctype="multipart/form-data" action="/upload">
 
<h1>Add Article</h1>
 Please enter file you wish to add: 
 <br><br>
 <html:file property="uploadedFile"/>
 <br><br>
 <html:submit value="Upload File"/> 
</html:form>
 
</html:html>