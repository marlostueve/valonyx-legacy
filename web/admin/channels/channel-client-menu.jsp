<%

boolean ehrEnabled = (CUBean.getProperty("ehrEnabled") != null) && CUBean.getProperty("ehrEnabled").equals("true");
boolean herbEnabled = (CUBean.getProperty("herbEnabled") != null) && CUBean.getProperty("herbEnabled").equals("true");
boolean hairAnalysisEnabled = (CUBean.getProperty("hairAnalysisEnabled") != null) && CUBean.getProperty("hairAnalysisEnabled").equals("true");


boolean setup_01 = (request.getRequestURI().indexOf("clients-new.jsp") > -1);
boolean setup_02 = (request.getRequestURI().indexOf("client-plan.jsp") > -1);
boolean setup_03 = (request.getRequestURI().indexOf("client-ledger-new.jsp") > -1);
boolean setup_04 = (request.getRequestURI().indexOf("client-merge.jsp") > -1);
boolean setup_05 = (request.getRequestURI().indexOf("client-review-reasons.jsp") > -1);
boolean setup_06 = (request.getRequestURI().indexOf("client-import.jsp") > -1);
boolean setup_07 = (request.getRequestURI().indexOf("herb-dosage.jsp") > -1);
boolean setup_08 = (request.getRequestURI().indexOf("herb-dosages.jsp") > -1);
boolean setup_09 = (request.getRequestURI().indexOf("setup_09.jsp") > -1);

boolean tender_detail = (request.getRequestURI().indexOf("tender-detail.jsp") > -1);

boolean problem_list = (request.getRequestURI().indexOf("client-problem-list.jsp") > -1);
boolean med_list = (request.getRequestURI().indexOf("client-medication-list.jsp") > -1);
boolean med_allergy_list = (request.getRequestURI().indexOf("client-medication-allergy-list.jsp") > -1);
boolean demographics = (request.getRequestURI().indexOf("client-demographics.jsp") > -1);
boolean vitals = (request.getRequestURI().indexOf("client-vitals.jsp") > -1);
boolean smoking_status = (request.getRequestURI().indexOf("client-smoking-status.jsp") > -1);
//boolean smoking_status = (request.getRequestURI().indexOf("client-smoking-status.jsp") > -1);


boolean hair_analysis = (request.getRequestURI().indexOf("admin-user-hair-analysis.jsp") > -1);
boolean hair_analysis_report = (request.getRequestURI().indexOf("admin-user-hair-analysis-report.jsp") > -1);
boolean mineral_ratios = (request.getRequestURI().indexOf("admin-user-mineral-ratios.jsp") > -1);
boolean mineral_ratios_report = (request.getRequestURI().indexOf("admin-user-mineral-ratios-report.jsp") > -1);


%>

					<div class="subnav">

						<a href="clients-new.jsp" class="lvl1" title="Client Profile"><%= setup_01 ? "<strong style=\"font-weight: bolder;\">CLIENT PROFILE</strong>" : "CLIENT PROFILE" %></a>
<%
    if (setup_01 || setup_02 || setup_03 || setup_07 || setup_08 || tender_detail || problem_list || med_list || med_allergy_list || demographics || vitals || smoking_status || hair_analysis || hair_analysis_report || mineral_ratios || mineral_ratios_report)
    {
		if (setup_01)
		{
%>
						<a href="javascript: newClick2();" class="lvl2" title="New Client">New Client</a>
<%
		}
		else
		{
%>
						<span class="lvl2" title="New Client">New Client</span>
<%
		}
%>
						<a href="client-ledger-new.jsp" class="lvl2" title="Ledger"><%= setup_03 ? "<strong style=\"font-weight: bolder;\">Ledger</strong>" : "Ledger" %></a>
						<a href="client-plan.jsp" class="lvl2" title="Payment Plans"><%= setup_02 ? "<strong style=\"font-weight: bolder;\">Payment Plans</strong>" : "Payment Plans" %></a>
<%
		if (herbEnabled) {
%>
						<a href="herb-dosages.jsp" class="lvl2" title="Herb Dosages"><%= setup_08 ? "<strong style=\"font-weight: bolder;\">Herb Dosages</strong>" : "Herb Dosages" %></a>
						<a href="herb-dosage.jsp?new=true" class="lvl3" title="New Herb Dosage"><%= setup_07 ? "<strong style=\"font-weight: bolder;\">New Herb Dosage</strong>" : "New Herb Dosage" %></a>
<%
		}
		if (hairAnalysisEnabled) {
%>
						<a href="admin-user-hair-analysis.jsp" class="lvl2" title="Hair Analysis"><%= hair_analysis || hair_analysis_report ? "<strong style=\"font-weight: bolder;\">Hair Analysis</strong>" : "Hair Analysis" %></a>
						<a href="admin-user-mineral-ratios.jsp?new=true" class="lvl2" title="Mineral Ratios"><%= mineral_ratios || mineral_ratios_report ? "<strong style=\"font-weight: bolder;\">Mineral Ratios</strong>" : "Mineral Ratios" %></a>
<%
		}
		if (ehrEnabled) {
%>
						<span class="lvl2" title="45 CFR 170.302 & 170.304">EHR</span>
						<a href="client-problem-list.jsp" class="lvl3" title="&sect;170.302(c) Maintain up-to-date problem list"><%= problem_list ? "<strong style=\"font-weight: bolder;\">Problem List</strong>" : "Problem List" %></a>
						<a href="client-medication-list.jsp" class="lvl3" title="&sect;170.302(d) Maintain active medication list"><%= med_list ? "<strong style=\"font-weight: bolder;\">Medication List</strong>" : "Medication List" %></a>
						<a href="client-medication-allergy-list.jsp" class="lvl3" title="&sect;170.302(e) Maintain active medication allergy list"><%= med_allergy_list ? "<strong style=\"font-weight: bolder;\">Allergy List</strong>" : "Allergy List" %></a>
						<a href="client-demographics.jsp" class="lvl3" title="&sect;170.304(c) Record demographics"><%= demographics ? "<strong style=\"font-weight: bolder;\">Demographics</strong>" : "Demographics" %></a>
						<a href="client-vitals.jsp" class="lvl3" title="&sect;170.302(f) Record and chart vital signs"><%= vitals ? "<strong style=\"font-weight: bolder;\">Vitals</strong>" : "Vitals" %></a>
						<a href="client-smoking-status.jsp" class="lvl3" title="&sect;170.302(g) Smoking status"><%= smoking_status ? "<strong style=\"font-weight: bolder;\">Smoking Status</strong>" : "Smoking Status" %></a>
<%
		}
    }
%>

						<a href="client-import.jsp" class="lvl1" title="Client Import"><%= setup_06 ? "<strong style=\"font-weight: bolder;\">IMPORT CLIENTS</strong>" : "IMPORT CLIENTS" %></a>
						<a href="client-merge.jsp" class="lvl1" title="Merge Clients"><%= setup_04 ? "<strong style=\"font-weight: bolder;\">MERGE CLIENTS</strong>" : "MERGE CLIENTS" %></a>
						<a href="client-review-reasons.jsp" class="lvl1" title="Client Review"><%= setup_05 ? "<strong style=\"font-weight: bolder;\">CLIENT REVIEW</strong>" : "CLIENT REVIEW" %></a>

<%
if (setup_01 || setup_02 || setup_03 || setup_07 || setup_08)
{
	try {
	java.util.Vector menuClients = new Vector();
			
	//String lastname = parameter;
	if (searchLastName != null && !searchLastName.isEmpty()) {

		java.util.Iterator people_itr = com.badiyan.uk.online.beans.UKOnlinePersonBean.getPersonsByLastName(adminCompany, searchLastName).iterator();
		while (people_itr.hasNext()) {
			menuClients.addElement(people_itr.next());
		}
	}
%>

						
	      &nbsp;<strong><span style="color: #4060af;" >Search Last Name:</span></strong>
	      <div class="clear"></div>
	      &nbsp;<input value="<%= searchLastName %>" onfocus="select();" style="width: 190px;" class="adminInput" id="lastname" type="textbox" onkeyup="if (document.getElementById('lastname').value.length > 0) {processCommand('getPeopleByLastName', document.getElementById('lastname').value.replace(/\'/g,'\\\''));}" name="lastname" />
	      &nbsp;<strong><span style="color: #4060af;" >Search First / File Number:</span></strong>
	      <div class="clear"></div>
	      &nbsp;<input value="<%= searchFirstName %>" onfocus="select();" style="width: 100px;" class="adminInput" id="firstname" type="textbox" onkeyup="if (document.getElementById('firstname').value.length > 0) {processCommand('getPeopleByFirstName', document.getElementById('firstname').value);}" name="firstname" />&nbsp;/&nbsp;<input value="<%= searchFileNumber%>" onfocus="select();" style="width: 60px;" class="adminInput" id="filenumber" type="textbox" onkeyup="if (document.getElementById('filenumber').value.length > 0) {processCommand('getPeopleByFileNumber', document.getElementById('filenumber').value);}" name="filenumber" />
	      <div class="clear"></div>
		  &nbsp;<strong><span style="color: #4060af;" >Client:</span></strong>
	      <div class="clear"></div>
	      &nbsp;<select name="clientSelect" size="7" id="clientSelect" style="width: 190px;" onclick="selectPerson();">
<%
	if (menuClients.isEmpty()) {
%>
		  <option value="-1">-- SEARCH FOR A CLIENT --</option>
<%
	} else {
		java.util.Iterator m_people_itr = menuClients.iterator();
		while (m_people_itr.hasNext()) {
			PersonBean clientMenuPerson = (PersonBean)m_people_itr.next();
%>
		  <option value="<%= clientMenuPerson.getValue() %>"><%= clientMenuPerson.getLabel() %></option>
<%
		}
	}
%>
	      </select>
<%
	} catch (Exception x) {
		x.printStackTrace();
	}
}
%>
					</div>