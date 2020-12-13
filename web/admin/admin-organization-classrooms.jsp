<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminClassroom" class="com.badiyan.uk.beans.ClassroomBean" scope="session" />
<jsp:useBean id="adminClassroomType" class="com.badiyan.uk.beans.ClassroomTypeBean" scope="session" />
<jsp:useBean id="adminClassroomStatus" class="com.badiyan.uk.beans.ClassroomStatusBean" scope="session" />
<jsp:useBean id="adminBuilding" class="com.badiyan.uk.beans.BuildingBean" scope="session" />
<jsp:useBean id="adminLocation" class="com.badiyan.uk.beans.LocationBean" scope="session" />

<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
String stepString = "Step 8: Classrooms";

if (request.getParameter("room") != null)
{
    try
    {
        ClassroomBean classroom_obj = ClassroomBean.getClassroom(Integer.parseInt(request.getParameter("room")));
        CompanyBean company_obj = classroom_obj.getBuilding().getLocation().getCompany();

        // ensure that this person is an admin for this company
        
        if (company_obj.isAdministrator(loginBean.getPerson()))
        {
            adminCompany = company_obj;
            session.setAttribute("adminCompany", adminCompany);
            adminClassroom = classroom_obj;
            session.setAttribute("adminClassroom", adminClassroom);
        }
    }
    catch (Exception x)
    {
    }
}
else
{
    adminClassroom = new ClassroomBean();
    session.setAttribute("adminClassroom", adminClassroom);
}

if (request.getParameter("building") != null)
{
    try
    {
        BuildingBean building_obj = BuildingBean.getBuilding(Integer.parseInt(request.getParameter("building")));
        CompanyBean company_obj = building_obj.getLocation().getCompany();

        // ensure that this person is an admin for this company
        
        if (company_obj.isAdministrator(loginBean.getPerson()))
        {
            adminCompany = company_obj;
            session.setAttribute("adminCompany", adminCompany);
            adminBuilding = building_obj;
            session.setAttribute("adminBuilding", adminBuilding);
        }
    }
    catch (Exception x)
    {
    }
}
else
{
    adminBuilding = new BuildingBean();
    session.setAttribute("adminBuilding", adminBuilding);
}

if (request.getParameter("location") != null)
{
    try
    {
        LocationBean location_obj = LocationBean.getLocation(Integer.parseInt(request.getParameter("location")));
        CompanyBean company_obj = location_obj.getCompany();

        // ensure that this person is an admin for this company
        
        if (company_obj.isAdministrator(loginBean.getPerson()))
        {
            adminCompany = company_obj;
            session.setAttribute("adminCompany", adminCompany);
            adminLocation = location_obj;
            session.setAttribute("adminLocation", adminLocation);
        }
    }
    catch (Exception x)
    {
    }
}
else
{
    adminLocation = new LocationBean();
    session.setAttribute("adminLocation", adminLocation);
}
%>

<struts-html:html locale="true">
<head>
<struts-html:base/>
<title>Universal Knowledge</title>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<%@ include file="channels\channel-head-javascript.jsp" %>
<script TYPE="text/javascript">
<!--

function
initForm()
{
}

// -->
</script>
</head>
<BODY BGCOLOR="#FFFFFF" LEFTMARGIN=0 TOPMARGIN=0 MARGINWIDTH=0 MARGINHEIGHT=0 ONLOAD="preloadImages();initForm();">
<%@ include file="channels\channel-menu.jsp" %>
<!-- Begin Bottom Content Area -->
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<!-- Begin 24 Pixel Spacer -->
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=24 ALT=""></TD>
		<TD WIDTH=732 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=732 HEIGHT=24 ALT=""></TD>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=24 ALT=""></TD>
	</TR>
	<!-- End 24 Pixel Spacer -->
</TABLE>
<TABLE WIDTH=772 BORDER=0 CELLPADDING=0 CELLSPACING=0>
	<TR>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
<%@ include file="channels\channel-admin-menu.jsp" %>
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
		<!-- Begin Right Column Content -->
		<TD WIDTH=542 ALIGN=left VALIGN=top>
			<P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">Organization Manager&nbsp;&nbsp;&gt;&nbsp;&nbsp;<%= (adminCompany.isNew()) ? "Create New" : "Edit" %> Organization</P>
<%@ include file="channels\channel-organization-menu.jsp" %>
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>

<!-- 

<form-bean       name="organizationClassroomsForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="building" type="java.lang.Integer"/>
      <form-property name="type" type="java.lang.String"/>
      <form-property name="status" type="java.lang.String"/>
      <form-property name="capacity" type="java.lang.Integer"/>
      <form-property name="comment" type="java.lang.String"/>
    </form-bean>
 
-->
			<!-- Begin Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Classroom</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/organizationClassrooms" focus="nameInput">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30 BGCOLOR="#EBEBEB">
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Name:</P></TD>
								<TD>
                                                                    <input type="text" name="nameInput" value="<%= adminClassroom.getLabel() %>" maxlength="40" style="font-size: 9pt; font-family: Arial; width: 400px;">
								</TD>
							</TR>
                                                        <TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Capacity:</P></TD>
								<TD>
                                                                    <input type="text" name="capacity" value="<%= adminClassroom.getCapacityString() %>" maxlength="5" style="font-size: 9pt; font-family: Arial; width: 400px;">
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Type:</P></TD>
								<TD>
                                                                    <select name="type" value="1" style="font-size: 9pt; font-family: Arial; width: 400px;">
                                                                        <option value="0">-- SELECT CLASSROOM TYPE --</option>
<%
try
{
    Iterator types = ClassroomTypeBean.getTypes(adminCompany).iterator();
    while (types.hasNext())
    {
        ClassroomTypeBean type = (ClassroomTypeBean)types.next();
%>
                                                                        <option value="<%= type.getValue() %>"<%= (adminClassroom.getTypeId() == type.getId()) ? " SELECTED" : "" %>><%= type.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
}
%>
                                                                    </select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Status:</P></TD>
								<TD>
                                                                    <select name="status" style="font-size: 9pt; font-family: Arial; width: 400px;">
                                                                        <option value="0">-- SELECT CLASSROOM STATUS --</option>
<%
try
{
    Iterator status_itr = ClassroomStatusBean.getStatus(adminCompany).iterator();
    while (status_itr.hasNext())
    {
        ClassroomStatusBean status = (ClassroomStatusBean)status_itr.next();
%>
                                                                        <option value="<%= status.getValue() %>"<%= (adminClassroom.getStatusId() == status.getId()) ? " SELECTED" : "" %>><%= status.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
}
%>
                                                                    </select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Building:</P></TD>
								<TD>
                                                                    <select name="building" style="font-size: 9pt; font-family: Arial; width: 400px;">
                                                                        <option value="0">-- SELECT A BUILDING --</option>
<%
try
{
    Iterator buildings = BuildingBean.getBuildings(adminCompany).iterator();
    while (buildings.hasNext())
    {
        BuildingBean building_obj = (BuildingBean)buildings.next();
%>
                                                                        <option value="<%= building_obj.getValue() %>"<%= (adminClassroom.getBuildingId() == building_obj.getId()) ? " SELECTED" : "" %>><%= building_obj.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
}
%>
                                                                    </select>
								</TD>
							</TR>
                                                        <TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Comment:</P></TD>
								<TD>
                                                                    <textarea cols="40" rows="5" name="comment" style="font-size: 9pt; font-family: Arial; width: 400px;"><%= adminClassroom.getCommentString() %></textarea>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="createQuestion" VALUE="<%= (adminClassroom.isNew()) ? "Create" : "Save" %> Classroom"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>


                        <TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Classroom Type</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/organizationClassroomTypes">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Type:</P></TD>
								<TD>
                                                                    <input type="text" name="nameInput" value="<%= adminClassroomType.getLabel() %>" style="font-size: 9pt; font-family: Arial; width: 400px;">
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="createQuestion" VALUE="<%= (adminClassroomType.isNew()) ? "Create" : "Save" %> Type"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>



                        <TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Classroom Status</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/organizationClassroomStatus">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Status:</P></TD>
								<TD>
                                                                    <input type="text" name="nameInput" value="<%= adminClassroomStatus.getLabel() %>" style="font-size: 9pt; font-family: Arial; width: 400px;">
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="createQuestion" VALUE="<%= (adminClassroomStatus.isNew()) ? "Create" : "Save" %> Status"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>

			
			<!-- Begin Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Building</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/organizationBuildings">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Name:</P></TD>
								<TD>
                                                                    <input type="text" name="nameInput" value="<%= adminBuilding.getLabel() %>" style="font-size: 9pt; font-family: Arial; width: 400px;">
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Map Link:</P></TD>
								<TD>
                                                                    <input type="text" name="map_url" value="<%= adminBuilding.getMapURLString() %>" style="font-size: 9pt; font-family: Arial; width: 400px;">
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Address:</P></TD>
								<TD>
                                                                    <select name="address" style="font-size: 9pt; font-family: Arial; width: 400px;">
                                                                        <option value="0">-- NONE --</option>
<%
try
{
    Iterator addresses = AddressBean.getAddresses(adminCompany).iterator();
    while (addresses.hasNext())
    {
        AddressBean address_obj = (AddressBean)addresses.next();
%>
                                                                        <option value="<%= address_obj.getValue() %>"<%= (adminBuilding.getAddressId() == address_obj.getId()) ? " SELECTED" : "" %>><%= address_obj.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
}
%>
                                                                    </select>
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Location:</P></TD>
								<TD>
                                                                    <select name="location" style="font-size: 9pt; font-family: Arial; width: 400px;">
                                                                        <option value="0">-- NONE --</option>
<%
try
{
    Iterator locations = LocationBean.getLocations(adminCompany).iterator();
    while (locations.hasNext())
    {
        LocationBean obj = (LocationBean)locations.next();
%>
                                                                        <option value="<%= obj.getValue() %>"<%= (adminBuilding.getLocationId() == obj.getId()) ? " SELECTED" : "" %>><%= obj.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
}
%>
                                                                    </select>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="createQuestion" VALUE="<%= (adminBuilding.isNew()) ? "Create" : "Save" %> Building"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>

			<!-- Begin Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#BBBBBB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG2.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Location</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
                                                <struts-html:form action="/admin/organizationLocations">
							<TR HEIGHT=8 BGCOLOR="#EEEEEE">
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=100 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=420 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Name:</P></TD>
								<TD>
                                                                    <input type="text" name="nameInput" value="<%= adminLocation.getLabel() %>" maxlength="50" style="font-size: 9pt; font-family: Arial; width: 400px;">
								</TD>
							</TR>
                                                        <TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Map Link:</P></TD>
								<TD>
                                                                    <input type="text" name="map_url" value="<%= adminLocation.getMapURLString() %>" style="font-size: 9pt; font-family: Arial; width: 400px;">
								</TD>
							</TR>
							<TR HEIGHT=30>
								<TD><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Address:</P></TD>
								<TD>
                                                                    <select name="address" style="font-size: 9pt; font-family: Arial; width: 400px;">
                                                                        <option value="0">-- NONE --</option>
<%
try
{
    Iterator addresses = AddressBean.getAddresses(adminCompany).iterator();
    while (addresses.hasNext())
    {
        AddressBean address_obj = (AddressBean)addresses.next();
%>
                                                                        <option value="<%= address_obj.getValue() %>"<%= (adminLocation.getAddressId() == address_obj.getId()) ? " SELECTED" : "" %>><%= address_obj.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
}
%>
                                                                    </select>
								</TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=30>
								<TD ALIGN=right COLSPAN=2><INPUT TYPE="submit" NAME="createQuestion" VALUE="<%= (adminLocation.isNew()) ? "Create" : "Save" %> Location"><IMG SRC="../images/trspacer.gif" WIDTH=12 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR HEIGHT=2>
								<TD ALIGN=left COLSPAN=2><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
						</struts-html:form>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Add Multiple Choice Question Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>

			<!-- Begin Assessment Questions Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Classrooms</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=40 BGCOLOR="#FFFFFF">
								<TD ALIGN=left COLSPAN=6><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;To edit a question, click on the question text. The correct answer is displayed in <B>bold</B> text.<BR>&nbsp;To adjust the ordering of a question, click on the appropriate arrow next to the question.</P></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=3 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=467 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
try
{
    Iterator classrooms = ClassroomBean.getClassrooms(adminCompany).iterator();
    if (classrooms.hasNext())
    {
        while (classrooms.hasNext())
        {
            ClassroomBean obj = (ClassroomBean)classrooms.next();
%>
							<!-- Begin MC Question Entry -->
							<TR>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-organization-classrooms.jsp?room=<%= obj.getValue() %>" style="color: #000000"><%= obj.getLabel() %></A></P></TD>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=1&moveUpId=1"><IMG SRC="../images/PttrsnUK_BTN-ArrowUp.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Up"></A></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=1&moveDownId=1"><IMG SRC="../images/PttrsnUK_BTN-ArrowDown.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Down"></A></TD>
								<TD ALIGN=left><A HREF="javascript:if (confirm('Are you sure?')) location = 'admin-assessment-questions.jsp?id=1&deleteId=1'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Question"></A></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#EEEEEE">
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 8pt; font-family: Arial; color: #000000;">hmmm</P></TD>
								<TD ALIGN=left COLSPAN=4><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<!-- End MC Question Entry -->
<%
        }
    }
    else
    {
%>
                                                        <TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
                                                        <TR>
								<TD ALIGN=left COLSPAN=8><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Questions Found</p></TD>
							</TR>
                                                        <TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
    }
}
catch (Exception x)
{
}
%>
							<TR>
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>
			<!-- End Assessment Questions Area -->
			
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>

			<!-- Begin Assessment Questions Area -->
			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Buildings</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=40 BGCOLOR="#FFFFFF">
								<TD ALIGN=left COLSPAN=6><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;To edit a question, click on the question text. The correct answer is displayed in <B>bold</B> text.<BR>&nbsp;To adjust the ordering of a question, click on the appropriate arrow next to the question.</P></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=3 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=467 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
try
{
    Iterator buildings = BuildingBean.getBuildings(adminCompany).iterator();
    if (buildings.hasNext())
    {
        while (buildings.hasNext())
        {
            BuildingBean obj = (BuildingBean)buildings.next();
%>
							<!-- Begin DepartmentTypeBean Entry -->
							<TR>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-organization-classrooms.jsp?building=<%= obj.getValue() %>" style="color: #000000"><%= obj.getLabel() %></A></P></TD>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=1&moveUpId=1"><IMG SRC="../images/PttrsnUK_BTN-ArrowUp.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Up"></A></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=1&moveDownId=1"><IMG SRC="../images/PttrsnUK_BTN-ArrowDown.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Down"></A></TD>
								<TD ALIGN=left><A HREF="javascript:if (confirm('Are you sure?')) location = 'admin-assessment-questions.jsp?id=1&deleteId=1'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Question"></A></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<!-- End DepartmentTypeBean Entry -->
<%
        }
    }
    else
    {
%>
                                                        <TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
                                                        <TR>
								<TD ALIGN=left COLSPAN=8><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Questions Found</p></TD>
							</TR>
                                                        <TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
							<TR>
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>

			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
				<TR BGCOLOR="#FFFFFF">
					<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=26 ALT=""></TD>
				</TR>
			</TABLE>

			<TABLE WIDTH=522 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#6F9BCB">
				<TR HEIGHT=26>
					<TD ALIGN=left BACKGROUND="../images/PttrsnUK_TableHeaderBG3.jpg"><P style="font-size: 13pt; font-family: Arial; font-weight: bold; color: #000000;">&nbsp;Locations</P></TD>
				</TR>
				<TR>
					<TD>
						<TABLE WIDTH=520 BORDER=0 CELLPADDING=1 CELLSPACING=0 BGCOLOR="#FFFFFF">
							<TR HEIGHT=40 BGCOLOR="#FFFFFF">
								<TD ALIGN=left COLSPAN=6><P style="font-size: 9pt; font-family: Arial; color: #000000;">&nbsp;To edit a question, click on the question text. The correct answer is displayed in <B>bold</B> text.<BR>&nbsp;To adjust the ordering of a question, click on the appropriate arrow next to the question.</P></TD>
							</TR>
							<TR HEIGHT=8 BGCOLOR="#9EBFE9">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<TR>
								<TD WIDTH=3 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=467 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=5 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD WIDTH=15 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
try
{
    Iterator locations = LocationBean.getLocations(adminCompany).iterator();
    if (locations.hasNext())
    {
        while (locations.hasNext())
        {
            LocationBean obj = (LocationBean)locations.next();
%>
							<!-- Begin DepartmentTypeBean Entry -->
							<TR>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=20 ALT=""></TD>
								<TD ALIGN=left><P style="font-size: 9pt; font-family: Arial; font-weight: bold; color: #000000;"><A HREF="admin-organization-classrooms.jsp?location=<%= obj.getValue() %>" style="color: #000000"><%= obj.getLabel() %></A></P></TD>
								<TD ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=1&moveUpId=1"><IMG SRC="../images/PttrsnUK_BTN-ArrowUp.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Up"></A></TD>
								<TD ALIGN=left><A HREF="admin-assessment-questions.jsp?id=1&moveDownId=1"><IMG SRC="../images/PttrsnUK_BTN-ArrowDown.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Move Down"></A></TD>
								<TD ALIGN=left><A HREF="javascript:if (confirm('Are you sure?')) location = 'admin-assessment-questions.jsp?id=1&deleteId=1'"><IMG SRC="../images/PttrsnUK_BTN-RemoveCourse.gif" WIDTH=15 HEIGHT=15 BORDER=0 ALT="Delete Question"></A></TD>
							</TR>
							<TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
							<!-- End DepartmentTypeBean Entry -->
<%
        }
    }
    else
    {
%>
                                                        <TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
                                                        <TR>
								<TD ALIGN=left COLSPAN=8><p style="font-size: 9pt; font-family: Arial; color: #ff0000;">No Locations Found</p></TD>
							</TR>
                                                        <TR BGCOLOR="#DDDDDD">
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=1 ALT=""></TD>
							</TR>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
							<TR>
								<TD ALIGN=left COLSPAN=6><IMG SRC="../images/trspacer.gif" WIDTH=1 HEIGHT=2 ALT=""></TD>
							</TR>
						</TABLE>
					</TD>
				</TR>
			</TABLE>



		</TD>
		<!-- End Right Column Content -->
		<TD WIDTH=20 ALIGN=left><IMG SRC="../images/trspacer.gif" WIDTH=20 HEIGHT=1 ALT=""></TD>
	</TR>
</TABLE>
<!-- End Bottom Content Area -->
<%@ include file="channels\channel-footer.jsp" %>
</BODY>
</struts-html:html>