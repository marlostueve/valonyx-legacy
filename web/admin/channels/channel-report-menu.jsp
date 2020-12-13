<%
UKOnlinePersonBean person_ggg = (UKOnlinePersonBean)loginBean.getPerson();
adminCompany = (UKOnlineCompanyBean)person_ggg.getSelectedCompany();
session.setAttribute("adminCompany", adminCompany);


boolean is_system_administrator = loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME);
boolean is_company_administrator = loginBean.getPerson().hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);
boolean is_cashier = loginBean.getPerson().hasRole(UKOnlineRoleBean.CASHIER_ROLE_NAME);
boolean is_practitioner = loginBean.getPerson().hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME);
   

boolean admin_reports = (request.getRequestURI().indexOf("reports.jsp") > -1);
boolean client_check_out_form = (request.getRequestURI().indexOf("report-client-check-out-form-filter.jsp") > -1) || (request.getRequestURI().indexOf("report-client-check-out-form.jsp") > -1);
boolean statement = (request.getRequestURI().indexOf("report-statement-filter.jsp") > -1) || (request.getRequestURI().indexOf("report-statement.jsp") > -1);
boolean admin_reports_end_of_day = (request.getRequestURI().indexOf("report-end-of-day-filter.jsp") > -1) || (request.getRequestURI().indexOf("report-end-of-day.jsp") > -1);
boolean admin_reports_end_of_day_visit = (request.getRequestURI().indexOf("report-end-of-day-visit-filter.jsp") > -1) || (request.getRequestURI().indexOf("report-end-of-day-visits.jsp") > -1);
boolean admin_reports_commission = (request.getRequestURI().indexOf("report-commission-filter.jsp") > -1) || (request.getRequestURI().indexOf("report-commission.jsp") > -1);
boolean admin_reports_mn_care_tax = (request.getRequestURI().indexOf("schedule-administration-report-mn-care-tax") > -1);
boolean admin_reports_patient_list = (request.getRequestURI().indexOf("schedule-administration-report-patient-list") > -1);
boolean admin_reports_patient_aging = (request.getRequestURI().indexOf("schedule-administration-report-patient-aging") > -1);
boolean admin_reports_new_patient = (request.getRequestURI().indexOf("schedule-administration-report-new-patient") > -1);

boolean admin_reports_test = (request.getRequestURI().indexOf("reports-visits-ytd") > -1);
boolean admin_reports_test2 = (request.getRequestURI().indexOf("reports-financial-ytd") > -1);
boolean admin_reports_test3 = (request.getRequestURI().indexOf("reports-commissions-ytd") > -1);

boolean admin_reports_goods_sold = (request.getRequestURI().indexOf("report-goods-sold.jsp") > -1) || (request.getRequestURI().indexOf("report-goods-sold-filter.jsp") > -1);
boolean admin_reports_inventory = (request.getRequestURI().indexOf("report-inventory.jsp") > -1);

boolean show_stuff = false;
%>
					<div class="subnav">
<%


if (is_system_administrator || is_company_administrator || is_cashier || is_practitioner)
{
    if (admin_reports || statement || client_check_out_form || admin_reports_end_of_day || admin_reports_commission || admin_reports_mn_care_tax || admin_reports_patient_list || admin_reports_patient_aging || admin_reports_new_patient || admin_reports_end_of_day_visit || admin_reports_test || admin_reports_test2 || admin_reports_test3 || admin_reports_goods_sold || admin_reports_inventory)
    {
		if (adminCompany.getId() == 1) {
%>
						<a href="report-client-check-out-form-filter.jsp" class="lvl1" title="Client Check Out Form Report"><%= client_check_out_form ? "<strong style=\"font-weight: bolder;\">CLIENT CHECKOUT FORM</strong>" : "CLIENT CHECKOUT FORM" %></a>
<%
		}
%>
						<a href="report-statement-filter.jsp" class="lvl1" title="Statement"><%= statement ? "<strong style=\"font-weight: bolder;\">CLIENT STATEMENT</strong>" : "CLIENT STATEMENT" %></a>
						<a href="report-end-of-day-filter.jsp" class="lvl1" title="End of Day Financial"><%= admin_reports_end_of_day ? "<strong style=\"font-weight: bolder;\">END OF DAY FINANCIAL</strong>" : "END OF DAY FINANCIAL" %></a>
						<a href="report-end-of-day-visit-filter.jsp" class="lvl1" title="End of Day Visits"><%= admin_reports_end_of_day_visit ? "<strong style=\"font-weight: bolder;\">END OF DAY VISITS</strong>" : "END OF DAY VISITS" %></a>
						<a href="reports-financial-ytd.jsp" class="lvl1" title="Don't Click on this Cool Report!"><%= admin_reports_test2 ? "<strong style=\"font-weight: bolder;\">YEAR TO DATE FINANCIAL</strong>" : "YEAR TO DATE FINANCIAL" %></a>
						<a href="reports-visits-ytd.jsp" class="lvl1" title="Don't Click on this Cool Report!"><%= admin_reports_test ? "<strong style=\"font-weight: bolder;\">YEAR TO DATE VISITS</strong>" : "YEAR TO DATE VISITS" %></a>
						<a href="report-commission-filter.jsp" class="lvl1" title="Commission Report"><%= admin_reports_commission ? "<strong style=\"font-weight: bolder;\">COMMISSIONS</strong>" : "COMMISSIONS" %></a>
						<a href="reports-commissions-ytd.jsp" class="lvl1" title="Don't Click on this Cool Report!"><%= admin_reports_test3 ? "<strong style=\"font-weight: bolder;\">YEAR TO DATE COMMISSIONS</strong>" : "YEAR TO DATE COMMISSIONS" %></a>
						<a href="report-goods-sold-filter.jsp" class="lvl1" title="Goods Sold Report"><%= admin_reports_goods_sold ? "<strong style=\"font-weight: bolder;\">GOODS SOLD</strong>" : "GOODS SOLD" %></a>
						<a href="report-inventory.jsp" class="lvl1" title="Inventory"><%= admin_reports_inventory ? "<strong style=\"font-weight: bolder;\">INVENTORY</strong>" : "INVENTORY" %></a>
						<!-- <span href="schedule-administration-report-mn-care-tax-filter.jsp" class="lvl1" title="MN Care Tax Report"><%= admin_reports_mn_care_tax ? "<strong style=\"font-weight: bolder;\">MN CARE TAX</strong>" : "MN CARE TAX" %></span> -->
						<a href="schedule-administration-report-patient-list-filter.jsp" class="lvl1" title="Patient List Report"><%= admin_reports_patient_list ? "<strong style=\"font-weight: bolder;\">CLIENT LIST</strong>" : "CLIENT LIST" %></a>
						<!-- <a href="schedule-administration-report-patient-aging-filter.jsp" class="lvl1" title="Patient Aging Report"><%= admin_reports_patient_aging ? "<strong style=\"font-weight: bolder;\">CLIENT AGING</strong>" : "CLIENT AGING" %></a> -->
						<!-- <span href="schedule-administration-report-new-patient-filter.jsp" class="lvl1" title="New Patient Report"><%= admin_reports_new_patient ? "<strong style=\"font-weight: bolder;\">NEW PATIENT</strong>" : "NEW PATIENT" %></span> -->
<%
    }
}
%>


					</div>





















