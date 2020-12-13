<%

/*
boolean is_system_admin = false;

try
{
    UKOnlinePersonBean person_ggg = (UKOnlinePersonBean)loginBean.getPerson();
    is_system_admin = person_ggg.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME);
}
catch (Exception xx)
{
}
 */

boolean qbfs_enabled = adminCompany.getQuickBooksSettings().isQuickBooksFSEnabled();

boolean eod_01 = (request.getRequestURI().indexOf("end-of-day_01") > -1);
boolean eod_02 = (request.getRequestURI().indexOf("end-of-day_02") > -1);
boolean eod_03 = (request.getRequestURI().indexOf("end-of-day_03") > -1);
boolean eod_04 = (request.getRequestURI().indexOf("end-of-day_04") > -1);
boolean eod_05 = (request.getRequestURI().indexOf("end-of-day_05") > -1);
boolean eod_06 = (request.getRequestURI().indexOf("end-of-day_06") > -1);
boolean eod_07 = (request.getRequestURI().indexOf("end-of-day_07") > -1);
boolean eod_08 = (request.getRequestURI().indexOf("end-of-day_08") > -1);
//boolean eod_09 = (request.getRequestURI().indexOf("end-of-day_09") > -1);


boolean is_primary_workstation = (session.getAttribute("is_primary_workstation") != null) && ((String)session.getAttribute("is_primary_workstation")).equals("true");


%>

					<div class="subnav">
						<<%= (session.getAttribute("eod_01") == null) ? "span" : "a" %> href="end-of-day_01.jsp" class="lvl1" title="Step 1"><%= eod_01 ? "<strong style=\"font-weight: bolder;\">STEP 1: CONFIRM WORKSTATION</strong>" : "STEP 1: CONFIRM WORKSTATION" %></<%= (session.getAttribute("eod_01") == null) ? "span" : "a" %>>
<%
if (!eod_01)
{
	if (is_primary_workstation)
	{
%>
						<<%= (session.getAttribute("eod_02") == null) ? "span" : "a" %> href="end-of-day_02.jsp" class="lvl1" title="Step 2"><%= eod_02 ? "<strong style=\"font-weight: bolder;\">STEP 2: SELECT DATE RANGE</strong>" : "STEP 2: SELECT DATE RANGE" %></<%= (session.getAttribute("eod_02") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("eod_03") == null) ? "span" : "a" %> href="end-of-day_03.jsp" class="lvl1" title="Step 3"><%= eod_03 ? "<strong style=\"font-weight: bolder;\">STEP 3: X-OUT STATUS REPORT</strong>" : "STEP 3: X-OUT STATUS REPORT" %></<%= (session.getAttribute("eod_03") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("eod_04") == null) ? "span" : "a" %> href="end-of-day_04.jsp" class="lvl1" title="Step 4"><%= eod_04 ? "<strong style=\"font-weight: bolder;\">STEP 4: DRAWER COUNT</strong>" : "STEP 4: DRAWER COUNT" %></<%= (session.getAttribute("eod_04") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("eod_05") == null) ? "span" : "a" %> href="end-of-day_05.jsp" class="lvl1" title="Step 5"><%= eod_05 ? "<strong style=\"font-weight: bolder;\">STEP 5: Z-OUT DRAWER COUNT REPORT</strong>" : "STEP 5: Z-OUT DRAWER COUNT REPORT" %></<%= (session.getAttribute("eod_05") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("eod_06") == null) ? "span" : "a" %> href="end-of-day_06.jsp" class="lvl1" title="Step 6"><%= eod_06 ? "<strong style=\"font-weight: bolder;\">STEP 6: Z-OUT STORE CLOSE</strong>" : "STEP 6: Z-OUT STORE CLOSE" %></<%= (session.getAttribute("eod_06") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("eod_07") == null) ? "span" : "a" %> href="end-of-day_07.jsp" class="lvl1" title="Step 7"><%= eod_07 ? "<strong style=\"font-weight: bolder;\">STEP 7: SETTLE MERCHANT ACCOUNT</strong>" : "STEP 7: SETTLE MERCHANT ACCOUNT" %></<%= (session.getAttribute("eod_07") == null) ? "span" : "a" %>>
<%
		if (qbfs_enabled)
		{
%>
						<<%= (session.getAttribute("eod_08") == null) ? "span" : "a" %> href="end-of-day_08.jsp" class="lvl1" title="Step 8"><%= eod_08 ? "<strong style=\"font-weight: bolder;\">STEP 8: UPDATE QUICKBOOKS</strong>" : "STEP 8: UPDATE QUICKBOOKS" %></<%= (session.getAttribute("eod_08") == null) ? "span" : "a" %>>
<%
		}
	}
	else
	{
%>
						<<%= (session.getAttribute("eod_02") == null) ? "span" : "a" %> href="end-of-day_02_secondary.jsp" class="lvl1" title="Step 2"><%= eod_02 ? "<strong style=\"font-weight: bolder;\">STEP 2: SELECT DATE RANGE</strong>" : "STEP 2: SELECT DATE RANGE" %></<%= (session.getAttribute("eod_02") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("eod_03") == null) ? "span" : "a" %> href="end-of-day_03_secondary.jsp" class="lvl1" title="Step 3"><%= eod_03 ? "<strong style=\"font-weight: bolder;\">STEP 3: DRAWER COUNT</strong>" : "STEP 3: DRAWER COUNT" %></<%= (session.getAttribute("eod_03") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("eod_04") == null) ? "span" : "a" %> href="end-of-day_04_secondary.jsp" class="lvl1" title="Step 4"><%= eod_04 ? "<strong style=\"font-weight: bolder;\">STEP 4: Z-OUT DRAWER COUNT REPORT</strong>" : "STEP 4: Z-OUT DRAWER COUNT REPORT" %></<%= (session.getAttribute("eod_04") == null) ? "span" : "a" %>>
<%
	}
}
%>
					</div>