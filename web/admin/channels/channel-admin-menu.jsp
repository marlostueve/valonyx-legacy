<%
UKOnlinePersonBean person_ggg = (UKOnlinePersonBean)loginBean.getPerson();
adminCompany = (UKOnlineCompanyBean)person_ggg.getSelectedCompany();
session.setAttribute("adminCompany", adminCompany);


boolean is_system_administrator = loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME);
boolean is_company_administrator = loginBean.getPerson().hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);
boolean is_training_administrator = loginBean.getPerson().hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME);
boolean is_department_administrator = loginBean.getPerson().hasRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME);
   
boolean user_manager = (request.getRequestURI().indexOf("admin-users.jsp") > -1);
boolean import_users = (request.getRequestURI().indexOf("admin-user-import.jsp") > -1);
boolean import_users_results = (request.getRequestURI().indexOf("admin-user-import-results.jsp") > -1);
boolean add_new_user = (request.getRequestURI().indexOf("admin-user-profile-new.jsp") > -1);
boolean admin_user_profile = (request.getRequestURI().indexOf("admin-user-profile.jsp") > -1);
boolean admin_user_roles = (request.getRequestURI().indexOf("admin-user-roles.jsp") > -1);
boolean admin_user_enrollments = (request.getRequestURI().indexOf("admin-user-enrollments.jsp") > -1);
boolean admin_user_certifications = (request.getRequestURI().indexOf("admin-user-certifications.jsp") > -1);
boolean admin_soap_notes = (request.getRequestURI().indexOf("admin-user-soap-notes.jsp") > -1);
boolean admin_user_hair = (request.getRequestURI().indexOf("admin-user-hair-analysis.jsp") > -1);
boolean admin_user_mineral_ratios = (request.getRequestURI().indexOf("admin-user-mineral-ratios.jsp") > -1);

boolean admin_appointment_manager = (request.getRequestURI().indexOf("schedule-administration-appointment-manager.jsp") > -1);
boolean admin_practice_areas = (request.getRequestURI().indexOf("admin-practice-areas.jsp") > -1);
boolean admin_appointment_types = (request.getRequestURI().indexOf("admin-appointment-types.jsp") > -1);
boolean admin_appointment_types_allowed_appointments = (request.getRequestURI().indexOf("admin-appointment-types-allowed-appointments.jsp") > -1);
boolean admin_appointment_new = (request.getRequestURI().indexOf("admin-appointment-new.jsp") > -1);

boolean admin_organization = (request.getRequestURI().indexOf("schedule-administration-practices.jsp") > -1);
boolean admin_organization_new  = (request.getRequestURI().indexOf("admin-organization-new.jsp") > -1);
boolean admin_organization_profile  = (request.getRequestURI().indexOf("schedule-administration-practice-profile.jsp") > -1);
boolean admin_organization_practice_areas  = (request.getRequestURI().indexOf("schedule-administration-practice-areas.jsp") > -1);
boolean admin_organization_payment_plans = (request.getRequestURI().indexOf("schedule-administration-payment-plans.jsp") > -1);
boolean admin_organization_payment_plan_new = (request.getRequestURI().indexOf("schedule-administration-payment-plan-new.jsp") > -1);
boolean admin_organization_payment_plan = (request.getRequestURI().indexOf("schedule-administration-payment-plan.jsp") > -1);
boolean admin_organization_checkout_codes = (request.getRequestURI().indexOf("schedule-administration-checkout-codes.jsp") > -1) || (request.getRequestURI().indexOf("schedule-administration-checkout-code.jsp") > -1);
boolean admin_organization_checkout_code_new = (request.getRequestURI().indexOf("schedule-administration-checkout-code-new.jsp") > -1);
boolean admin_organization_client_file_types = (request.getRequestURI().indexOf("schedule-administration-client-file-types.jsp") > -1);
boolean admin_organization_resource_reasons = (request.getRequestURI().indexOf("schedule-administration-client-review-reasons.jsp") > -1);
boolean admin_organization_file_type_practice_area_visit_charge = (request.getRequestURI().indexOf("schedule-administration-file-type-practice-area-visit-charge.jsp") > -1);
boolean admin_organization_practitioner_schedules = (request.getRequestURI().indexOf("schedule-administration-practitioner-schedules.jsp") > -1);
boolean admin_organization_marketing_plans = (request.getRequestURI().indexOf("schedule-administration-marketing-plans.jsp") > -1) || (request.getRequestURI().indexOf("schedule-administration-marketing-plan.jsp") > -1);
boolean admin_organization_marketing_plan_new = (request.getRequestURI().indexOf("schedule-administration-marketing-plan-new.jsp") > -1);

boolean admin_course_lesson_new = (request.getRequestURI().indexOf("admin-course-lesson-new.jsp") > -1);
boolean admin_course_lesson = (request.getRequestURI().indexOf("admin-course-lesson.jsp") > -1);
   
boolean activity_manager = (request.getRequestURI().indexOf("admin-courses.jsp") > -1);
boolean admin_activity_export = (request.getRequestURI().indexOf("admin-activity-export.jsp") > -1);
boolean new_course = (request.getRequestURI().indexOf("admin-course-new.jsp") > -1);
boolean new_activity = (request.getRequestURI().indexOf("admin-activity-new.jsp") > -1);
boolean admin_course = (request.getRequestURI().indexOf("admin-course.jsp") > -1);
boolean admin_activity = (request.getRequestURI().indexOf("admin-activity.jsp") > -1);
boolean admin_course_audience = (request.getRequestURI().indexOf("admin-course-audience.jsp") > -1);
boolean admin_course_prereq = (request.getRequestURI().indexOf("admin-course-prereq.jsp") > -1);
boolean admin_course_media = (request.getRequestURI().indexOf("admin-course-media.jsp") > -1);
boolean admin_course_window = (request.getRequestURI().indexOf("admin-course-window.jsp") > -1);
boolean admin_course_questions = (request.getRequestURI().indexOf("admin-course-questions.jsp") > -1);
boolean admin_course_instructors = (request.getRequestURI().indexOf("admin-course-instructors.jsp") > -1);
    
boolean admin_certifications = (request.getRequestURI().indexOf("admin-certifications.jsp") > -1);
boolean admin_certification_new = (request.getRequestURI().indexOf("admin-certification-new.jsp") > -1);
boolean admin_certification_main = (request.getRequestURI().indexOf("admin-certification-main.jsp") > -1);

boolean admin_resource_library = (request.getRequestURI().indexOf("admin-resource-library.jsp") > -1);
boolean admin_resource_new = (request.getRequestURI().indexOf("admin-resource-new.jsp") > -1);
boolean admin_resource_basic = (request.getRequestURI().indexOf("admin-resource-basic.jsp") > -1);
boolean admin_resource_associations = (request.getRequestURI().indexOf("admin-resource-associations.jsp") > -1);
boolean admin_resource_upload = (request.getRequestURI().indexOf("admin-resource-upload.jsp") > -1);

boolean admin_reports = (request.getRequestURI().indexOf("schedule-administration-reports.jsp") > -1);
boolean admin_reports_end_of_day = (request.getRequestURI().indexOf("schedule-administration-report-end-of-day-filter.jsp") > -1);
boolean admin_reports_mn_care_tax = (request.getRequestURI().indexOf("schedule-administration-report-mn-care-tax") > -1);
boolean admin_reports_patient_list = (request.getRequestURI().indexOf("schedule-administration-report-patient-list") > -1);
boolean admin_reports_patient_aging = (request.getRequestURI().indexOf("schedule-administration-report-patient-aging") > -1);
boolean admin_reports_new_patient = (request.getRequestURI().indexOf("schedule-administration-report-new-patient") > -1);

boolean site_content = (request.getRequestURI().indexOf("site-content") > -1);

boolean show_stuff = false;
%>
					<div class="subnav">
<%
if (is_system_administrator || is_company_administrator)
{
%>
						<a href="schedule-administration-practices.jsp" class="lvl1" title="Practice Manager"><%= admin_organization ? "<strong>PRACTICE MANAGER</strong>" : "PRACTICE MANAGER" %></a>
<%
    if (admin_organization || admin_organization_profile || admin_organization_practice_areas || admin_organization_payment_plans || admin_organization_payment_plan || admin_organization_payment_plan_new || admin_organization_marketing_plans || admin_organization_marketing_plan_new || admin_organization_checkout_codes || admin_organization_checkout_code_new || admin_organization_client_file_types || admin_organization_resource_reasons || admin_organization_file_type_practice_area_visit_charge || admin_organization_practitioner_schedules)
    {
%>
						<a href="admin-organization-new.jsp" class="lvl2" title="Add New Organization">Add New Practice</a>
						<a href="schedule-administration-practice-profile.jsp?id=1" class="lvl2" title="Organization Profile"><%= admin_organization_profile ? "<strong>Practice Profile</strong>" : "Practice Profile" %></a>
						<a href="schedule-administration-practice-areas.jsp" class="lvl3" title="Practice Areas"><%= admin_organization_practice_areas ? "<strong>Practice Areas</strong>" : "Practice Areas" %></a>
						<a href="schedule-administration-payment-plans.jsp" class="lvl3" title="Payment Plans"><%= (admin_organization_payment_plans || admin_organization_payment_plan) ? "<strong>Payment Plans</strong>" : "Payment Plans" %></a>
						<a href="schedule-administration-payment-plan-new.jsp?new=true" class="lvl4" title="Add New Payment Plan"><%= admin_organization_payment_plan_new ? "<strong>Add New Payment Plan</strong>" : "Add New Payment Plan" %></a>
						<a href="schedule-administration-checkout-codes.jsp?new=true" class="lvl3" title="Checkout Codes"><%= admin_organization_checkout_codes ? "<strong>Checkout Codes</strong>" : "Checkout Codes" %></a>
						<a href="schedule-administration-checkout-code-new.jsp?new=true" class="lvl4" title="Add New Checkout Code"><%= admin_organization_checkout_code_new ? "<strong>Add New Checkout Code</strong>" : "Add New Checkout Code" %></a>
						<a href="schedule-administration-client-file-types.jsp" class="lvl3" title="Client File Types"><%= admin_organization_client_file_types ? "<strong>Client File Types</strong>" : "Client File Types" %></a>
						<a href="schedule-administration-file-type-practice-area-visit-charge.jsp?new=true" class="lvl4" title="Practice Area Visit Charge"><%= admin_organization_file_type_practice_area_visit_charge ? "<strong>Practice Area Visit Charge</strong>" : "Practice Area Visit Charge" %></a>
						<a href="schedule-administration-client-review-reasons.jsp" class="lvl3" title="Client Review Reasons"><%= admin_organization_resource_reasons ? "<strong>Client Review Reasons</strong>" : "Client Review Reasons" %></a>
						<a href="schedule-administration-practitioner-schedules.jsp?new=true" class="lvl3" title="Practitioner Schedules"><%= admin_organization_practitioner_schedules ? "<strong>Practitioner Schedules</strong>" : "Practitioner Schedules" %></a>
						<a href="schedule-administration-marketing-plans.jsp" class="lvl3" title="Marketing Campaigns"><%= admin_organization_marketing_plans ? "<strong>Marketing Campaigns</strong>" : "Marketing Campaigns" %></a>
						<a href="schedule-administration-marketing-plan-new.jsp?new=true" class="lvl4" title="Add New Marketing Campaign"><%= admin_organization_marketing_plan_new ? "<strong>Add New Payment Campaign</strong>" : "Add New Marketing Campaign" %></a>
<%
    }
    else if (admin_organization_new)
    {
%>
						<a href="admin-organization-new.jsp" class="lvl2" title="Add New Organization"><%= admin_organization_new ? "<strong>Add New Practice</strong>" : "Add New Practice" %></a>
						<span class="lvl2">Practice Profile</span>
						<span class="lvl3">Practice Areas</span>
						<span class="lvl3">Payment Plans</span>
						<span class="lvl4">Add New Payment Plan</span>
						<span class="lvl3">Checkout Codes</span>
						<span class="lvl4">Add New Checkout Codes</span>
						<span class="lvl3">Client File Types</span>
						<span class="lvl3">Client Review Reasons</span>
						<span class="lvl4">Practice Area Visit Charge</span>
						<span class="lvl3">Practitioner Schedules</span>
						<span class="lvl3">Marketing Plans</span>
						<span class="lvl4">Add New Marketing Plan</span>
<%
    }
}
%>
						<a href="schedule-administration-appointment-manager.jsp" class="lvl1" title="Appointment Manager"><%= admin_appointment_manager ? "<strong>APPOINTMENT MANAGER</strong>" : "APPOINTMENT MANAGER" %></a>
<%
if (admin_appointment_manager || admin_practice_areas || admin_appointment_types || admin_appointment_new || admin_appointment_types_allowed_appointments)
{
%>
						<a href="admin-practice-areas.jsp" class="lvl2" title="Practice Areas"><%= admin_practice_areas ? "<strong>Practice Areas</strong>" : "Practice Areas" %></a>
						<a href="admin-appointment-types.jsp" class="lvl2" title="Appointment Types"><%= admin_appointment_types ? "<strong>Appointment Types</strong>" : "Appointment Types" %></a>
						<a href="admin-appointment-types-allowed-appointments.jsp" class="lvl3" title="Allowed Appointments"><%= admin_appointment_types_allowed_appointments ? "<strong>Allowed Appointments</strong>" : "Allowed Appointments" %></a>
						<a href="admin-appointment-new.jsp" class="lvl2" title="Add New Appointment"><%= admin_appointment_new ? "<strong>Add New Appointment</strong>" : "Add New Appointment" %></a>
<%
}
%>
						<a href="site-content.jsp" class="lvl1" title="Site Content Manager"><%= site_content ? "<strong>VALEO WEBSITE CONTENT</strong>" : "VALEO WEBSITE CONTENT" %></a>
<%
if (is_system_administrator || is_company_administrator || is_training_administrator || is_department_administrator)
{
%>
						<a href="admin-users.jsp" class="lvl1" title="User Manager"><%= user_manager ? "<strong>USER MANAGER</strong>" : "USER MANAGER" %></a>
<%
    if (user_manager || add_new_user || import_users || import_users_results)
    {
	if (is_system_administrator || is_company_administrator)
	{
%>
						<a href="admin-user-import.jsp" class="lvl2" title="Import Users"><%= import_users ? "<strong>Import Users</strong>" : "Import Users" %></a>
						<a href="admin-user-profile-new.jsp?new=true" class="lvl2" title="Add New User"><%= add_new_user ? "<strong>Add New User</strong>" : "Add New User" %></a>
<%
	}
%>
						<span class="lvl2">User Profile</span>
						<span class="lvl3">Roles</span>
						<!-- <span class="lvl3">Enrollments</span> -->
						<!-- <span class="lvl3">Certifications</span> -->
						<span class="lvl3">SOAP Notes</span>
						<span class="lvl3">Hair Analysis</span>
						<span class="lvl3">Mineral Ratios</span>
<%
    }
    else if (admin_user_profile || admin_user_roles || admin_user_certifications || admin_user_enrollments || admin_soap_notes || admin_user_hair || admin_user_mineral_ratios)
    {
	if (is_system_administrator || is_company_administrator)
	{
%>
						<a href="admin-user-import.jsp" class="lvl2" title="Import Users">Import Users</a>
						<a href="admin-user-profile-new.jsp?new=true" class="lvl2" title="Add New User">Add New User</a>
<%
	}
%>
						<a href="admin-user-profile.jsp" class="lvl2" title="User Profile"><%= admin_user_profile ? "<strong>User Profile</strong>" : "User Profile" %></a>
<%
	if (is_system_administrator || is_company_administrator)
	{
%>
						<a href="admin-user-roles.jsp" class="lvl3" title="Roles"><%= admin_user_roles ? "<strong>Roles</strong>" : "Roles" %></a>
<%
	}
	else
	{
%>
						<!-- <span class="lvl3">Roles</span> -->
<%
	}
%>
						<!-- <a href="admin-user-enrollments.jsp" class="lvl3" title="Enrollments"><%= admin_user_enrollments ? "<strong>Enrollments</strong>" : "Enrollments" %></a> -->
						<!-- <a href="admin-user-certifications.jsp" class="lvl3" title="Certifications"><%= admin_user_certifications ? "<strong>Certifications</strong>" : "Certifications" %></a> -->
						<a href="admin-user-soap-notes.jsp" class="lvl3" title="SOAP Notes"><%= admin_soap_notes ? "<strong>SOAP Notes</strong>" : "SOAP Notes" %></a>
						<a href="admin-user-hair-analysis.jsp" class="lvl3" title="Hair Analysis"><%= admin_user_hair ? "<strong>Hair Analysis</strong>" : "Hair Analysis" %></a>
						<a href="admin-user-mineral-ratios.jsp?new=true" class="lvl3" title="Mineral Ratios"><%= admin_user_mineral_ratios ? "<strong>Mineral Ratios</strong>" : "Mineral Ratios" %></a>
<%
    }
}
if (show_stuff && is_system_administrator || is_company_administrator)
{
%>
						<a href="admin-courses.jsp" class="lvl1" title="Curriculum Manager"><%= activity_manager ? "<strong>CURRICULUM MANAGER</strong>" : "CURRICULUM MANAGER" %></a>
<%
    if (activity_manager || admin_activity_export || new_course || new_activity || admin_course || admin_activity || admin_course_audience || admin_course_prereq || admin_course_media || admin_course_window || admin_course_questions || admin_course_instructors || admin_course_lesson || admin_course_lesson_new)
    {
%>
						<a href="admin-course-lesson-new.jsp" class="lvl2" title="Add New Curriculum Level"><%= admin_course_lesson_new ? "<strong>Add New Curriculum Level</strong>" : "Add New Curriculum Level" %></a>
						<a href="admin-course-new.jsp" class="lvl2" title="Add New Course"><%= new_course ? "<strong>Add New Course</strong>" : "Add New Course" %></a>
						<a href="admin-activity-new.jsp" class="lvl2" title="Add New Learning Activity"><%= new_activity ? "<strong>Add New Learning Activity</strong>" : "Add New Learning Activity" %></a>
<%
	if (admin_course)
	{
%>
						<a href="admin-course.jsp" class="lvl2" title="Course Details"><%= admin_course ? "<strong>Course Details</strong>" : "Course Details" %></a>
						<a href="admin-course-audience.jsp" class="lvl3" title="Audiences"><%= admin_course_audience ? "<strong>Audiences</strong>" : "Audiences" %></a>
						<a href="admin-course-prereq.jsp" class="lvl3" title="Prerequisites"><%= admin_course_prereq ? "<strong>Prerequisites</strong>" : "Prerequisites" %></a>
						<span class="lvl3">Instructors</span>
						<span class="lvl2">Learning Activity Details</span>
						<span class="lvl3">Audiences</span>
						<span class="lvl3">Prerequisites</span>
						<span class="lvl3">Questions</span>
						<span class="lvl3">Media</span>
						<span class="lvl3">Window</span>
<%
	}
	else if (admin_activity || admin_course_audience || admin_course_prereq || admin_course_media || admin_course_window || admin_course_questions || admin_course_instructors)
	{
%>
						<span class="lvl2">Course Details</span>
						<span class="lvl3">Audiences</span>
						<span class="lvl3">Prerequisites</span>
						<span class="lvl3">Instructors</span>
						<a href="admin-activity.jsp" class="lvl2" title="Learning Activity Details"><%= admin_activity ? "<strong>Learning Activity Details</strong>" : "Learning Activity Details" %></a>
						<a href="admin-course-audience.jsp" class="lvl3" title="Audiences"><%= admin_course_audience ? "<strong>Audiences</strong>" : "Audiences" %></a>
						<a href="admin-course-prereq.jsp" class="lvl3" title="Prerequisites"><%= admin_course_prereq ? "<strong>Prerequisites</strong>" : "Prerequisites" %></a>
						<span class="lvl3">Instructors</span>
						<span class="lvl3">Questions</span>
						<span class="lvl3">Media</span>
						<span class="lvl3">Window</span>
<%
	}
	else
	{
	    if (admin_course_lesson)
	    {
%>
						<a href="admin-course-lesson.jsp" class="lvl2" title="Curriculum Level Details"><%= admin_course_lesson ? "<strong>Curriculum Level Details</strong>" : "Curriculum Level Details" %></a>
<%
	    }
	    else
	    {
%>
						<span class="lvl2">Curriculum Level Details</span>
<%
	    }
%>
						<span class="lvl2">Course Details</span>
						<span class="lvl3">Audiences</span>
						<span class="lvl3">Prerequisites</span>
						<span class="lvl3">Instructors</span>
						<span class="lvl2">Learning Activity Details</span>
						<span class="lvl3">Audiences</span>
						<span class="lvl3">Prerequisites</span>
						<span class="lvl3">Questions</span>
						<span class="lvl3">Media</span>
						<span class="lvl3">Window</span>
<%
	}
    }
%>
						<!-- -->
<%
    if (admin_certifications || admin_certification_new || admin_certification_main)
    {
%>
						<a href="admin-certification-new.jsp?new=true" class="lvl2" title="Add New Certification"><%= admin_certification_new ? "<strong>Add New Certification</strong>" : "Add New Certification" %></a>
<%
	if (admin_certification_main)
	{
%>
						<a href="admin-certification-main.jsp" class="lvl2" title="Certification Details"><%= admin_certification_main ? "<strong>Certification Details</strong>" : "Certification Details" %></a>
<%
	}
	else
	{
%>
						<span class="lvl2">Certification Details</span>
<%
	}
    }
%>
						<a href="admin-resource-library.jsp" class="lvl1" title="Resource Manager"><%= admin_resource_library ? "<strong>RESOURCE MANAGER</strong>" : "RESOURCE MANAGER" %></a>
<%
    if (admin_resource_library || admin_resource_new || admin_resource_basic || admin_resource_associations || admin_resource_upload)
    {
%>
						<a href="admin-resource-new.jsp?new=true" class="lvl2" title="Add New Resource"><%= admin_resource_new ? "<strong>Add New Resource</strong>" : "Add New Resource" %></a>
<%
	if (admin_resource_basic || admin_resource_associations || admin_resource_upload)
	{
%>
						<a href="admin-resource-basic.jsp" class="lvl2" title="Resource Details"><%= admin_resource_basic ? "<strong>Resource Details</strong>" : "Resource Details" %></a>
						<a href="admin-resource-associations.jsp" class="lvl3" title="Associations"><%= admin_resource_associations ? "<strong>Associations</strong>" : "Associations" %></a>
						<a href="admin-resource-upload.jsp" class="lvl3" title="Media"><%= admin_resource_upload ? "<strong>Media</strong>" : "Media" %></a>
<%
	}
	else
	{
%>
						<span class="lvl2">Resource Details</span>
						<span class="lvl3">Associations</span>
						<span class="lvl3">Media</span>
<%
	}
    }
}
if (is_system_administrator || is_company_administrator || is_department_administrator || is_training_administrator)
{
%>
						<a href="schedule-administration-reports.jsp" class="lvl1" title="Reports"><%= admin_reports ? "<strong>REPORTS</strong>" : "REPORTS" %></a>
<%
    if (admin_reports || admin_reports_end_of_day || admin_reports_mn_care_tax || admin_reports_patient_list || admin_reports_patient_aging || admin_reports_new_patient)
    {
%>
						<a href="schedule-administration-report-end-of-day-filter.jsp" class="lvl2" title="End of Day Report"><%= admin_reports_end_of_day ? "<strong>End of Day ReportX</strong>" : "End of Day ReportX" %></a>
						<a href="schedule-administration-report-mn-care-tax-filter.jsp" class="lvl2" title="MN Care Tax Report"><%= admin_reports_mn_care_tax ? "<strong>MN Care Tax Report</strong>" : "MN Care Tax Report" %></a>
						<a href="schedule-administration-report-patient-list-filter.jsp" class="lvl2" title="Patient List Report"><%= admin_reports_patient_list ? "<strong>Patient List Report</strong>" : "Patient List Report" %></a>
						<a href="schedule-administration-report-patient-aging-filter.jsp" class="lvl2" title="Patient Aging Report"><%= admin_reports_patient_aging ? "<strong>Patient Aging Report</strong>" : "Patient Aging Report" %></a>
						<a href="schedule-administration-report-new-patient-filter.jsp" class="lvl2" title="New Patient Report"><%= admin_reports_new_patient ? "<strong>New Patient Report</strong>" : "New Patient Report" %></a>
<%
    }
}
%>

<%
if (activity_manager)
{
%>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/courseSearch" focus="keyword">

							<input id="dummy" name="dummy" type="hidden" />

							<p class="headlineB">Search by Type&nbsp;&#47;&nbsp;Keyword</p>
							<p>
								<label for="typeActivityELRN"><img src="../images/icnActivityELRN.gif" width="17" height="17" title="E-Learning" alt="E-Learning" /></label>
								<input name="typeActivityELRN" id="typeActivityELRN" type="checkbox" value="" class="crirHiddenJS" />

								<label for="typeActivityMNTR"><img src="../images/icnActivityMNTR.gif" width="17" height="17" title="Mentor" alt="Mentor" /></label>
								<input name="typeActivityMNTR" id="typeActivityMNTR" type="checkbox" value="" class="crirHiddenJS" />

								<label for="typeActivityILED"><img src="../images/icnActivityILED.gif" width="17" height="17" title="Instructor-Led" alt="Instructor-Led" /></label>
								<input name="typeActivityILED" id="typeActivityILED" type="checkbox" value="" class="crirHiddenJS" />

								<label for="typeActivityASMT"><img src="../images/icnActivityASMT.gif" width="17" height="17" title="Assessment" alt="Assessment" /></label>
								<input name="typeActivityASMT" id="typeActivityASMT" type="checkbox" value="" class="crirHiddenJS" />
							</p>
							<p>
								<input name="keyword" value="" onfocus="select();" size="25" maxlength="100" class="inputbox" style="width: 180px; margin-right: 4px;" />
								<input type="image" src="../images/btnSearch.gif" value="Search" alt="Search" style="_margin-top: 1px;" />
							</p>

						</struts-html:form>
<%
}
if (admin_user_enrollments)
{
%>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/userActivitySearch" focus="keyword">

							<input id="dummy" name="dummy" type="hidden" />

							<p class="headlineB">Search by Type&nbsp;&#47;&nbsp;Keyword</p>
							<p>
								<label for="typeActivityELRN"><img src="../images/icnActivityELRN.gif" width="17" height="17" title="E-Learning" alt="E-Learning" /></label>
								<input name="typeActivityELRN" id="typeActivityELRN" type="checkbox" value="" class="crirHiddenJS" />

								<label for="typeActivityMNTR"><img src="../images/icnActivityMNTR.gif" width="17" height="17" title="Mentor" alt="Mentor" /></label>
								<input name="typeActivityMNTR" id="typeActivityMNTR" type="checkbox" value="" class="crirHiddenJS" />

								<label for="typeActivityILED"><img src="../images/icnActivityILED.gif" width="17" height="17" title="Instructor-Led" alt="Instructor-Led" /></label>
								<input name="typeActivityILED" id="typeActivityILED" type="checkbox" value="" class="crirHiddenJS" />

								<label for="typeActivityASMT"><img src="../images/icnActivityASMT.gif" width="17" height="17" title="Assessment" alt="Assessment" /></label>
								<input name="typeActivityASMT" id="typeActivityASMT" type="checkbox" value="" class="crirHiddenJS" />
							</p>
							<p>
								<input name="keyword" value="" onfocus="select();" size="25" maxlength="100" class="inputbox" style="width: 180px; margin-right: 4px;" />
								<input type="image" src="../images/btnSearch.gif" value="Search" alt="Search" style="_margin-top: 1px;" />
							</p>

						</struts-html:form>
<%
}
if (user_manager)
{
%>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/personSearch" focus="lastNameSearch" onsubmit="return validatePersonSearchForm(this);">

							<input id="dummy" name="dummy" type="hidden" />

							<p class="headlineB">Search by Last Name</p>
							<p>
								<input name="lastNameSearch" onfocus="select();" size="25" maxlength="100" class="inputbox" style="width: 180px; margin-right: 4px;" />
								<input type="image" src="../images/btnSearch.gif" value="Search" alt="Search" style="_margin-top: 1px;" />
							</p>

							<p class="headlineB">Search by Alpha Char</p>
							<p>
								<a href="admin-users.jsp?search=A" title="A" style="display: inline;">A</a>
								<a href="admin-users.jsp?search=B" title="B" style="display: inline;">B</a>
								<a href="admin-users.jsp?search=C" title="C" style="display: inline;">C</a>
								<a href="admin-users.jsp?search=D" title="D" style="display: inline;">D</a>
								<a href="admin-users.jsp?search=E" title="E" style="display: inline;">E</a>
								<a href="admin-users.jsp?search=F" title="F" style="display: inline;">F</a>
								<a href="admin-users.jsp?search=G" title="G" style="display: inline;">G</a>
								<a href="admin-users.jsp?search=H" title="H" style="display: inline;">H</a>
								<a href="admin-users.jsp?search=I" title="I" style="display: inline;">I</a>
								<a href="admin-users.jsp?search=J" title="J" style="display: inline;">J</a>
								<a href="admin-users.jsp?search=K" title="K" style="display: inline;">K</a>
								<a href="admin-users.jsp?search=L" title="L" style="display: inline;">L</a>
								<a href="admin-users.jsp?search=M" title="M" style="display: inline;">M</a>
								<a href="admin-users.jsp?search=N" title="N" style="display: inline;">N</a>
								<a href="admin-users.jsp?search=O" title="O" style="display: inline;">O</a>
								<a href="admin-users.jsp?search=P" title="P" style="display: inline;">P</a>
								<a href="admin-users.jsp?search=Q" title="Q" style="display: inline;">Q</a>
								<a href="admin-users.jsp?search=R" title="R" style="display: inline;">R</a>
								<a href="admin-users.jsp?search=S" title="S" style="display: inline;">S</a>
								<a href="admin-users.jsp?search=T" title="T" style="display: inline;">T</a>
								<a href="admin-users.jsp?search=U" title="U" style="display: inline;">U</a>
								<a href="admin-users.jsp?search=V" title="V" style="display: inline;">V</a>
								<a href="admin-users.jsp?search=W" title="W" style="display: inline;">W</a>
								<a href="admin-users.jsp?search=X" title="X" style="display: inline;">X</a>
								<a href="admin-users.jsp?search=Y" title="Y" style="display: inline;">Y</a>
								<a href="admin-users.jsp?search=Z" title="Z" style="display: inline;">Z</a>
							</p>

						</struts-html:form>
<%
}
if (admin_resource_library)
{
%>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/resourceSearch" focus="keywordSearchBox">

							<input id="dummy" name="dummy" type="hidden" />

							<p class="headlineB">Search by Type&nbsp;&#47;&nbsp;Keyword</p>
							<p>
								<label for="typeResourceDOC"><img src="../images/icnResourceDOC.gif" width="17" height="17" title="Microsoft Word (DOC)" alt="Microsoft Word (DOC)" /></label>
								<input name="mediaType1" id="typeResourceDOC" type="checkbox" class="crirHiddenJS"  />

								<label for="typeResourcePPT"><img src="../images/icnResourcePPT.gif" width="17" height="17" title="Microsoft PowerPoint (PPT)" alt="Microsoft PowerPoint (PPT)" /></label>
								<input name="mediaType2" id="typeResourcePPT" type="checkbox" class="crirHiddenJS"  />

								<label for="typeResourceXLS"><img src="../images/icnResourceXLS.gif" width="17" height="17" title="Microsoft Excel (XLS)" alt="Microsoft Excel (XLS)" /></label>
								<input name="mediaType8" id="typeResourceXLS" type="checkbox" class="crirHiddenJS"  />

								<label for="typeResourcePDF"><img src="../images/icnResourcePDF.gif" width="17" height="17" title="Adobe Acrobat (PDF)" alt="Adobe Acrobat (PDF)" /></label>
								<input name="mediaType3" id="typeResourcePDF" type="checkbox" class="crirHiddenJS"  />

								<br />

								<label for="typeResourceSWF"><img src="../images/icnResourceSWF.gif" width="17" height="17" title="Macromedia Flash (SWF)" alt="Macromedia Flash (SWF)" /></label>
								<input name="mediaType4" id="typeResourceSWF" type="checkbox" class="crirHiddenJS"  />

								<label for="typeResourceWEB"><img src="../images/icnResourceWEB.gif" width="17" height="17" title="Internet" alt="Internet" /></label>
								<input name="mediaType5" id="typeResourceWEB" type="checkbox" class="crirHiddenJS"  />

								<label for="typeResourceVID"><img src="../images/icnResourceVID.gif" width="17" height="17" title="Video" alt="Video" /></label>
								<input name="mediaType6" id="typeResourceVID" type="checkbox" class="crirHiddenJS"  />

								<label for="typeResourceCD"><img src="../images/icnResourceCD.gif" width="17" height="17" title="CD" alt="CD" /></label>
								<input name="mediaType7" id="typeResourceCD" type="checkbox" class="crirHiddenJS"  />
							</p>
							<p>
								<input name="keywordSearchBox" onfocus="select();" size="25" maxlength="100" class="inputbox" style="width: 180px; margin-right: 4px;" />
								<input type="image" src="../images/btnSearch.gif" value="Search" alt="Search" style="_margin-top: 1px;" />
							</p>

						</struts-html:form>
<%
}
%>
					</div>