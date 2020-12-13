<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.tasks.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
boolean is_connected = CUBean.isConnected();
boolean local_data_exists = SyncObjectBean.dataExists();
boolean is_tablet_user_approved = false;
boolean employee_id_exists = false;
boolean employee_id_available = false;
boolean update_user_from_server_success = false;

boolean pretty_bad_error = false;
String pretty_bad_error_message = "";
boolean really_bad_error = false;
String really_bad_error_message = "";

boolean need_to_confirm_name = false;
String name_to_confirm = "";

boolean name_confirmed = false;

try
{
    if (is_connected)
    {
	UKOnlinePersonBean tablet_user = UKOnlinePersonBean.getTabletUser();
	System.out.println("tablet_user >" + tablet_user);
	String employee_id = tablet_user.getUsername();
	is_tablet_user_approved = tablet_user.isServerApproved();
	if (!is_tablet_user_approved)
	{
	    employee_id_exists = UKOnlinePersonBean.doesEmployeeIDExist(employee_id);
	    employee_id_available = UKOnlinePersonBean.isEmployeeIDAvailable(employee_id);
	    
	    if (employee_id_exists && employee_id_available)
	    {
		//if ((request.getParameter("name_confirm") != null) && request.getParameter("name_confirm").equals("true"))
		if (request.getParameter("name_confirm") == null)
		{
		    // get the name from the server so that it can be confirmed
		    
		    need_to_confirm_name = true;
		    name_to_confirm = UKOnlinePersonBean.getFullName(employee_id);
		}
		else if (request.getParameter("name_confirm").equals("true"))
		{
		    name_confirmed = true;
		    DBSyncMessage sync_obj = UKOnlinePersonBean.approveTabletUser(employee_id);
		    System.out.println("employee_id >" + employee_id);
		    System.out.println("sync_obj >" + sync_obj);
		    update_user_from_server_success = CUBean.dbSync(sync_obj).booleanValue();
		    if (update_user_from_server_success)
		    {
			tablet_user.setActive(false);
			tablet_user.save();
			UKOnlinePersonBean.cacheClear();
			//UKOnlinePersonBean tablet_user_from_server = UKOnlinePersonBean.getTabletUser();
			UKOnlinePersonBean tablet_user_from_server = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(employee_id);
			tablet_user_from_server.setServerApproved(true);
			tablet_user_from_server.setPersonType(UKOnlinePersonBean.TABLET_USER_PERSON_TYPE);
			tablet_user_from_server.save();
			boolean confirmed = UKOnlinePersonBean.confirmApproveTabletUser(employee_id);
			if (confirmed)
			{
			    // log the "new" user in

			    loginBean.setUsername(tablet_user_from_server.getUsername());
			    loginBean.setPassword("spork");
			    loginBean.getPerson();

			    is_tablet_user_approved = true;
			}
			else
			{
			    really_bad_error = true;
			    really_bad_error_message = "The server was unable to confirm your employee id as a tablet user.  Please re-enter your employee id and try to synchronize again.";
			}
		    }
		}
	    }
	}
    }
}
catch (Exception x)
{
    pretty_bad_error = true;
    pretty_bad_error_message = x.getMessage();
    x.printStackTrace();
}

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Synch</title>
    </head>
    <body>

    <h1><struts-html:errors/></h1>
    <a href="index.jsp">done</a><br>
<%
if (pretty_bad_error)
{
%>
<%= pretty_bad_error_message %><br>
<%
}

if (really_bad_error)
{
%>
<%= really_bad_error_message %><br>
<%
}
if (is_connected)
{
%>
You're connected.  Yes!!!  SYNCH at will...<br>

local data?<%= local_data_exists %><br>
is_tablet_user_approved?<%= is_tablet_user_approved %><br><br>

<%
    if (!is_tablet_user_approved)
    {
%>
employee_id_exists?<%= employee_id_exists %><br>
employee_id_available?<%= employee_id_available %><br>
update_user_from_server_success?<%= update_user_from_server_success %><br>
<%
       if (!employee_id_exists)
       {
%>
The employee id that you supplied could not be found on the server.  Click <a href="registration.jsp">here</a> to verify your employee id.
<%
       }
       else if (!employee_id_available)
       {
%>
The employee id that you supplied is already in use by another user.  Click <a href="registration.jsp">here</a> to verify your employee id.
<%
       }
       else if (need_to_confirm_name)
       {
%>
confirm name ><%= name_to_confirm %><br>
&nbsp;<a href="synch.jsp?name_confirm=true">That's me!</a><br>
&nbsp;That's not me!<a href="registration.jsp">Re-enter employee id</a><br>
<%
       }
    }
    else
    {
	try
	{
%>
    <%= CUBean.receiveSyncObjectsFromServer() %>
<%
	}
	catch (Exception x)
	{
	    x.printStackTrace();
%>
There's a problem receiving synch data from the server:<br> <%= x.getMessage() %>
<%
	}
	
	
	try
	{
%>
    <%= CUBean.receivePersonSyncObjectsFromServer(loginBean.getPerson()) %>
<%
	}
	catch (Exception x)
	{
	    x.printStackTrace();
%>
There's a problem receiving synch data from the server:<br> <%= x.getMessage() %>
<%
	}
	
	
	try
	{
	    while (SyncObjectBean.dataExists() && CUBean.isConnected())
	    {
		// try to send the local data to the server

		System.out.println("local data exists");

		// grab the data to be sent

		SyncObjectBean obj = SyncObjectBean.getData();
		obj.setState(SyncObjectBean.PROCESSING_STATE);
		obj.save();
%>
    <%= CUBean.sendSyncObjectToServer(obj) %><br>
<%
	    }
	}
	catch (Exception x)
	{
	    x.printStackTrace();
%>
There's a problem sending synch data to the server:<br> <%= x.getMessage() %>
<%
	}

    }
}
else
{
%>
Please connect to the InterWeb and then click SYNCH or something...<br>
<%
}
%>
    </body>
</html>
