<%
boolean is_system_admin = false;

try
{
    UKOnlinePersonBean person_ggg = (UKOnlinePersonBean)loginBean.getPerson();
    is_system_admin = person_ggg.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME);
}
catch (Exception xx)
{
}

CompanySettingsBean settings_xx = adminCompany.getSettings();
QuickBooksSettings qb_settings_xx = adminCompany.getQuickBooksSettings();

boolean setup_01 = (request.getRequestURI().indexOf("setup_01.jsp") > -1);
boolean setup_02 = (request.getRequestURI().indexOf("setup_02.jsp") > -1);
boolean setup_02_rooms = (request.getRequestURI().indexOf("setup_02-rooms.jsp") > -1);
boolean setup_03 = (request.getRequestURI().indexOf("setup_03.jsp") > -1);
boolean setup_04 = (request.getRequestURI().indexOf("setup_04.jsp") > -1);
boolean setup_05 = (request.getRequestURI().indexOf("setup_05.jsp") > -1);
boolean setup_06 = (request.getRequestURI().indexOf("setup_06.jsp") > -1);
boolean setup_07 = (request.getRequestURI().indexOf("setup_07.jsp") > -1);
boolean setup_08 = (request.getRequestURI().indexOf("setup_08.jsp") > -1);
boolean setup_09 = (request.getRequestURI().indexOf("setup_09.jsp") > -1);

boolean setup_10 = (request.getRequestURI().indexOf("schedule-administration-payment-plans.jsp") > -1);
boolean setup_11 = (request.getRequestURI().indexOf("schedule-administration-marketing-plans.jsp") > -1) || (request.getRequestURI().indexOf("schedule-administration-marketing-plan.jsp") > -1);
boolean setup_12 = (request.getRequestURI().indexOf("schedule-administration-checkout-codes.jsp") > -1) || (request.getRequestURI().indexOf("schedule-administration-checkout-code.jsp") > -1);
boolean setup_13 = (request.getRequestURI().indexOf("schedule-administration-client-review-reasons.jsp") > -1);
boolean setup_14 = (request.getRequestURI().indexOf("schedule-administration-tax-codes.jsp") > -1);
boolean setup_15 = (request.getRequestURI().indexOf("schedule-administration-checkout-code-new.jsp") > -1);
boolean setup_16 = (request.getRequestURI().indexOf("schedule-administration-vendors.jsp") > -1);
boolean setup_17 = (request.getRequestURI().indexOf("schedule-qbfs.jsp") > -1);
boolean setup_18 = (request.getRequestURI().indexOf("schedule-qbfs-request-data.jsp") > -1);
boolean setup_19 = (request.getRequestURI().indexOf("site-content.jsp") > -1);
boolean setup_20 = (request.getRequestURI().indexOf("schedule-administration-practices.jsp") > -1);
boolean setup_21 = (request.getRequestURI().indexOf("schedule-qbfs-status.jsp") > -1);
boolean setup_22 = (request.getRequestURI().indexOf("schedule-administration-purchase-orders.jsp") > -1);
boolean setup_23 = (request.getRequestURI().indexOf("schedule-administration-purchase-order.jsp") > -1);
boolean setup_24 = (request.getRequestURI().indexOf("schedule-administration-purchase-order-receive.jsp") > -1);

boolean setup_25 = (request.getRequestURI().indexOf("schedule-administration-inventory-count.jsp") > -1);

boolean setup_26 = (request.getRequestURI().indexOf("schedule-administration-payment-plan-new.jsp") > -1);
boolean setup_27 = (request.getRequestURI().indexOf("schedule-administration-payment-plan.jsp") > -1);

boolean setup_28 = (request.getRequestURI().indexOf("schedule-administration-inventory-departments.jsp") > -1);

boolean setup_29 = (request.getRequestURI().indexOf("schedule-administration-soap-trees.jsp") > -1);

boolean setup_30 = (request.getRequestURI().indexOf("schedule-administration-client-merge.jsp") > -1);

boolean setup_31 = (request.getRequestURI().indexOf("schedule-qbfs-payment-types.jsp") > -1);

boolean setup_32 = (request.getRequestURI().indexOf("schedule-administration-marketing-plan-new.jsp") > -1);

boolean setup_33 = (request.getRequestURI().indexOf("schedule-administration-account.jsp") > -1);

boolean setup_34 = (request.getRequestURI().indexOf("schedule-administration-email-lists.jsp") > -1);
boolean setup_35 = (request.getRequestURI().indexOf("schedule-administration-email-list-new.jsp") > -1);

boolean setup_36 = (request.getRequestURI().indexOf("schedule-administration-client-intake.jsp") > -1);
boolean setup_37 = (request.getRequestURI().indexOf("schedule-administration-client-intake-form.jsp") > -1);
boolean setup_38 = (request.getRequestURI().indexOf("schedule-administration-client-wellness-survey.jsp") > -1);

%>

					<div class="subnav">
<%
if (is_system_admin)
{
%>
						<a href="schedule-administration-practices.jsp" class="lvl1" title="PRACTICE SELECT"><%= setup_20 ? "<strong style=\"font-weight: bolder;\">PRACTICE SELECT</strong>" : "PRACTICE SELECT" %></a>
<%
}
if (settings_xx.isSetupComplete())
{
%>
						<a href="schedule-administration-account.jsp" class="lvl1" title="Account"><%= setup_33 ? "<strong style=\"font-weight: bolder;\">ACCOUNT</strong>" : "ACCOUNT" %></a>
<%
    if (!is_system_admin)
    {
%>
						<a href="schedule-administration-practices.jsp" class="lvl1" title="PRACTICE SELECT"><%= setup_20 ? "<strong style=\"font-weight: bolder;\">PRACTICE SELECT</strong>" : "PRACTICE SELECT" %></a>
<%
    }
    if (setup_20)
    {
%>
						<a href="setup_01.jsp?new=true" class="lvl2" title="Add New Practice">Add New Practice</a>
<%
    }
%>
						<a href="setup_01.jsp" class="lvl1" title="Practice"><%= setup_01 ? "<strong style=\"font-weight: bolder;\">PRACTICE</strong>" : "PRACTICE" %></a>
						<a href="setup_02.jsp" class="lvl1" title="Practitioners"><%= setup_02 ? "<strong style=\"font-weight: bolder;\">PRACTITIONERS</strong>" : "PRACTITIONERS" %></a>
						<a href="setup_02-rooms.jsp" class="lvl1" title="Rooms"><%= setup_02_rooms ? "<strong style=\"font-weight: bolder;\">ROOMS</strong>" : "ROOMS" %></a>
						<a href="setup_03.jsp" class="lvl1" title="Staff"><%= setup_03 ? "<strong style=\"font-weight: bolder;\">STAFF</strong>" : "STAFF" %></a>
						<a href="setup_04.jsp" class="lvl1" title="Practice Areas"><%= setup_04 ? "<strong style=\"font-weight: bolder;\">PRACTICE AREAS</strong>" : "PRACTICE AREAS" %></a>
						<a href="setup_05.jsp" class="lvl1" title="Appointment Types"><%= setup_05 ? "<strong style=\"font-weight: bolder;\">APPOINTMENT TYPES</strong>" : "APPOINTMENT TYPES" %></a>
						<a href="setup_06.jsp" class="lvl1" title="Schedule Settings"><%= setup_06 ? "<strong style=\"font-weight: bolder;\">SCHEDULE SETTINGS</strong>" : "SCHEDULE SETTINGS" %></a>
						<a href="setup_07.jsp" class="lvl1" title="Practitioner Schedules"><%= setup_07 ? "<strong style=\"font-weight: bolder;\">PRACTITIONER SCHEDULES</strong>" : "PRACTITIONER SCHEDULES" %></a>
<!--
						<span class="lvl1"><%= setup_08 ? "<strong style=\"font-weight: bolder;\">CLIENTS</strong>" : "CLIENTS" %></span>
						<span class="lvl1"><%= setup_09 ? "<strong style=\"font-weight: bolder;\">CLIENT TYPES</strong>" : "CLIENT TYPES" %></span>
-->
<%
    if (qb_settings_xx.isQuickBooksFSEnabled())
    {
%>
						<a href="schedule-administration-checkout-codes.jsp" class="lvl1" title="Inventory & Procedures"><%= setup_12 ? "<strong style=\"font-weight: bolder;\">INVENTORY & PROCEDURES</strong>" : "INVENTORY & PROCEDURES" %></a>
<%
		if (setup_12 || setup_14 || setup_15 || setup_16 || setup_22 || setup_23 || setup_24 || setup_25 || setup_28)
		{
%>
						<a href="schedule-administration-checkout-code-new.jsp?new=true" class="lvl2" title="New Inventory"><%= setup_15 ? "<strong style=\"font-weight: bolder;\">New Inventory</strong>" : "New Inventory" %></a>
						<a href="schedule-administration-tax-codes.jsp" class="lvl2" title="Tax Codes"><%= setup_14 ? "<strong style=\"font-weight: bolder;\">Tax Codes</strong>" : "Tax Codes" %></a>
						<a href="schedule-administration-vendors.jsp" class="lvl2" title="Vendors"><%= setup_16 ? "<strong style=\"font-weight: bolder;\">Vendors</strong>" : "Vendors" %></a>
						<a href="schedule-administration-inventory-departments.jsp" class="lvl2" title="Departments"><%= setup_28 ? "<strong style=\"font-weight: bolder;\">Departments</strong>" : "Departments" %></a>
						<a href="schedule-administration-inventory-count.jsp" class="lvl2" title="Inventory Count"><%= setup_25 ? "<strong style=\"font-weight: bolder;\">Inventory Count</strong>" : "Inventory Count" %></a>
						<a href="schedule-administration-purchase-orders.jsp" class="lvl2" title="Purchase Orders"><%= setup_22 ? "<strong style=\"font-weight: bolder;\">Purchase Orders</strong>" : "Purchase Orders" %></a>
						<a href="schedule-administration-purchase-order.jsp?new=true" class="lvl3" title="New Purchase Order"><%= setup_23 ? "<strong style=\"font-weight: bolder;\">New Purchase Order</strong>" : "New Purchase Order" %></a>
<%
		}
	}
%>
						<a href="schedule-administration-client-intake.jsp" class="lvl1" title="Payment Plans"><%= setup_36 ? "<strong style=\"font-weight: bolder;\">CLIENT INTAKE</strong>" : "CLIENT INTAKE" %></a>
<%
if (setup_36 || setup_37 || setup_38) {
%>
						<a href="schedule-administration-client-intake-form.jsp" class="lvl2" title="Client Intake Form"><%= setup_37 ? "<strong style=\"font-weight: bolder;\">Client Intake Form</strong>" : "Client Intake Form" %></a>
						<a href="schedule-administration-client-wellness-survey.jsp" class="lvl2" title="Client Wellness Survey"><%= setup_38 ? "<strong style=\"font-weight: bolder;\">Client Wellness Survey</strong>" : "Client Wellness Survey" %></a>
<%
}
%>
						
						
						<a href="schedule-administration-payment-plans.jsp" class="lvl1" title="Payment Plans"><%= setup_10 || setup_27 ? "<strong style=\"font-weight: bolder;\">PAYMENT PLANS</strong>" : "PAYMENT PLANS" %></a>

<%
	if (setup_10 || setup_26 || setup_27)
	{
%>
						<a href="schedule-administration-payment-plan-new.jsp?new=true" class="lvl2" title="Add New Payment Plan"><%= setup_26 ? "<strong style=\"font-weight: bolder;\">Add New Payment Plan</strong>" : "Add New Payment Plan" %></a>
<%
	}
%>
						<a href="schedule-administration-soap-trees.jsp" class="lvl1" title="SOAP Admin"><%= setup_29 ? "<strong style=\"font-weight: bolder;\">SOAP ADMIN</strong>" : "SOAP ADMIN" %></a>
<!--
						<a href="schedule-administration-client-merge.jsp" class="lvl1" title="Client Merge"><%= setup_30 ? "<strong style=\"font-weight: bolder;\">CLIENT MERGE</strong>" : "CLIENT MERGE" %></a>
-->
						<a href="schedule-administration-marketing-plans.jsp" class="lvl1" title="Marketing Campaigns"><%= setup_11 ? "<strong style=\"font-weight: bolder;\">MARKETING CAMPAIGNS</strong>" : "MARKETING CAMPAIGNS" %></a>
<%
	if (setup_11 || setup_32 || setup_34 || setup_35)
	{
%>
						<a href="schedule-administration-marketing-plan-new.jsp?new=true" class="lvl2" title="Add New Marketing Campaign"><%= setup_32 ? "<strong style=\"font-weight: bolder;\">Add New Marketing Campaign</strong>" : "Add New Marketing Campaign" %></a>
						<a href="schedule-administration-email-lists.jsp" class="lvl2" title="Email Lists"><%= setup_34 ? "<strong style=\"font-weight: bolder;\">Email Lists</strong>" : "Email Lists" %></a>
<%
		if (setup_34 || setup_35) {
%>
						<a href="schedule-administration-email-list-new.jsp?new=true" class="lvl3" title="Add New Email List"><%= setup_35 ? "<strong style=\"font-weight: bolder;\">Add New Email List</strong>" : "Add New Email List" %></a>
<%
		}
%>
						<a href="bulk-email.jsp" class="lvl2" title="Bulk Email"><%= setup_34 ? "<strong style=\"font-weight: bolder;\">Bulk Email</strong>" : "Bulk Email" %></a>
<%
	}
%>
<!--
						<a href="schedule-administration-client-review-reasons.jsp" class="lvl1" title="Client Review"><%= setup_13 ? "<strong style=\"font-weight: bolder;\">CLIENT REVIEW</strong>" : "CLIENT REVIEW" %></a>
-->

<%
    if (qb_settings_xx.isQuickBooksFSEnabled())
    {
%>
						<a href="schedule-qbfs.jsp" class="lvl1" title="Quickbooks"><%= setup_17 ? "<strong style=\"font-weight: bolder;\">QUICKBOOKS</strong>" : "QUICKBOOKS" %></a>
<%
		if (setup_17 || setup_18 || setup_21 || setup_31)
		{
%>
						<a href="schedule-qbfs-status.jsp" class="lvl2" title="Status"><%= setup_21 ? "<strong style=\"font-weight: bolder;\">Status</strong>" : "Status" %></a>
						<a href="schedule-qbfs-request-data.jsp?new=true" class="lvl2" title="Sync Data"><%= setup_18 ? "<strong style=\"font-weight: bolder;\">Sync Data</strong>" : "Sync Data" %></a>
						<a href="schedule-qbfs-payment-types.jsp?new=true" class="lvl2" title="Payment Types"><%= setup_31 ? "<strong style=\"font-weight: bolder;\">Payment Types</strong>" : "Payment Types" %></a>
<%
		}
    }
%>
						<!-- <a href="setup_08.jsp" class="lvl1" title="Clients"><%= setup_08 ? "<strong style=\"font-weight: bolder;\">OPTIONAL: CLIENTS</strong>" : "OPTIONAL: CLIENTS" %></a> -->
						<!-- <a href="setup_09.jsp" class="lvl1" title="Client Types"><%= setup_09 ? "<strong style=\"font-weight: bolder;\">OPTIONAL: CLIENT TYPES</strong>" : "OPTIONAL: CLIENT TYPES" %></a> -->
						<!-- <a href="setup_10.jsp" class="lvl1" title="Payment Plans"><%= setup_10 ? "<strong style=\"font-weight: bolder;\">OPTIONAL: PAYMENT PLANS</strong>" : "OPTIONAL: PAYMENT PLANS" %></a> -->
						<!-- <a href="setup_11.jsp" class="lvl1" title="Marketing Campaigns"><%= setup_11 ? "<strong style=\"font-weight: bolder;\">OPTIONAL: MARKETING CAMPAIGNS</strong>" : "OPTIONAL: MARKETING CAMPAIGNS" %></a> -->
<%
    if (adminCompany.getLabel().equals("Valeo") && is_system_admin)
    {
%>
						<a href="site-content.jsp" class="lvl1" title="Valeo Website Content Management"><%= setup_19 ? "<strong style=\"font-weight: bolder;\">VALEO WEBSITE</strong>" : "VALEO WEBSITE" %></a>
						<a href="admin-users.jsp" class="lvl1" title="Old User Manager">OLD USER MANAGER</a>
						<a href="schedule-administration-client-review-reasons.jsp" class="lvl1" title="Old Client Review Reasons">OLD MENU</a>
<%
    }
}
else
{
%>
						<<%= (session.getAttribute("setup_01") == null) ? "span" : "a" %> href="setup_01.jsp" class="lvl1" title="Practice"><%= setup_01 ? "<strong style=\"font-weight: bolder;\">STEP 1: PRACTICE</strong>" : "STEP 1: PRACTICE" %></<%= (session.getAttribute("setup_01") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("setup_02") == null) ? "span" : "a" %> href="setup_02.jsp" class="lvl1" title="Practitioners"><%= setup_02 ? "<strong style=\"font-weight: bolder;\">STEP 2: PRACTITIONERS</strong>" : "STEP 2: PRACTITIONERS" %></<%= (session.getAttribute("setup_02") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("setup_03") == null) ? "span" : "a" %> href="setup_03.jsp" class="lvl1" title="Staff"><%= setup_03 ? "<strong style=\"font-weight: bolder;\">STEP 3: STAFF</strong>" : "STEP 3: STAFF" %></<%= (session.getAttribute("setup_03") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("setup_04") == null) ? "span" : "a" %> href="setup_04.jsp" class="lvl1" title="Practice Areas"><%= setup_04 ? "<strong style=\"font-weight: bolder;\">STEP 4: PRACTICE AREAS</strong>" : "STEP 4: PRACTICE AREAS" %></<%= (session.getAttribute("setup_04") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("setup_05") == null) ? "span" : "a" %> href="setup_05.jsp" class="lvl1" title="Appointment Types"><%= setup_05 ? "<strong style=\"font-weight: bolder;\">STEP 5: APPOINTMENT TYPES</strong>" : "STEP 5: APPOINTMENT TYPES" %></<%= (session.getAttribute("setup_05") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("setup_06") == null) ? "span" : "a" %> href="setup_06.jsp" class="lvl1" title="Schedule Settings"><%= setup_06 ? "<strong style=\"font-weight: bolder;\">STEP 6: SCHEDULE SETTINGS</strong>" : "STEP 6: SCHEDULE SETTINGS" %></<%= (session.getAttribute("setup_06") == null) ? "span" : "a" %>>
						<<%= (session.getAttribute("setup_07") == null) ? "span" : "a" %> href="setup_07.jsp" class="lvl1" title="Practitioner Schedules"><%= setup_07 ? "<strong style=\"font-weight: bolder;\">STEP 7: PRACTITIONER SCHEDULES</strong>" : "STEP 7: PRACTITIONER SCHEDULES" %></<%= (session.getAttribute("setup_07") == null) ? "span" : "a" %>>
						<!--
						<span class="lvl1"><%= setup_10 ? "<strong style=\"font-weight: bolder;\">OPTIONAL: PAYMENT PLANS</strong>" : "OPTIONAL: PAYMENT PLANS" %></span>
						<span class="lvl1"><%= setup_11 ? "<strong style=\"font-weight: bolder;\">OPTIONAL: MARKETING CAMPAIGNS</strong>" : "OPTIONAL: MARKETING CAMPAIGNS" %></span>
						<span class="lvl1"><%= setup_12 ? "<strong style=\"font-weight: bolder;\">OPTIONAL: INVENTORY & PROCEDURES</strong>" : "OPTIONAL: INVENTORY & PROCEDURES" %></span>
						-->
<%
}
%>
					</div>