<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.online.PDF.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session"/>
<jsp:useBean id="adminAudience" class="com.badiyan.uk.beans.AudienceBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<jsp:useBean id="document" class="com.badiyan.uk.online.beans.MineralRatiosClientDocumentBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%

if (request.getParameter("id") != null)
{
    try
    {
        document = MineralRatiosClientDocumentBean.getMineralRatiosClientDocument(Integer.parseInt(request.getParameter("id")));
	
    }
    catch (Exception x)
    {
    }
}
else if ((request.getParameter("new") != null) && (request.getParameter("new").equals("true")))
    document = new MineralRatiosClientDocumentBean();
    
session.setAttribute("document", document);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>

		<title>Universal Knowledge</title>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="generator" content="TextPad 4.6.2" />
		<meta name="author" content="" />
		<meta name="copyright" content="&copy; 2007" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />

		<link rel="stylesheet" type="text/css" href="../css/web.css" />

		<script type="text/javascript" src="../scripts/crir.js"></script>
		<!--[if lte IE 7]><script type="text/javascript" src="scripts/optionDisabledSupport.js"></script><![endif]-->
		
<%@ include file="..\channels\channel-head-javascript.jsp" %>

	</head>

	<body onload="javascript:initErrors();">

		<div id="main">

<%@ include file="..\channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Administration">

<%@ include file="channels\channel-admin-menu.jsp" %>

					<div class="main">

						<p class="headlineA">User Profile&nbsp;&#47;&nbsp;Hair Analysis</p>
						<p class="currentObjectName"><%= adminPerson.getLabel() %></p>
						<p>Use this screen to manage hair analysis data.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/userMineralRatios" focus="dateInput">

							<input id="dummy" name="dummy" type="hidden" />
							
							<!--
							<div class="adminItem">
								<div class="leftTM">OXIDATION TYPE</div>
							</div>
							-->

							<div class="adminItem">
								<div class="leftTM">REPORT DATE</div>
								<div class="right">
									<input name="dateInput" onfocus="getDate('userMineralRatiosForm','dateInput');select();" value="<%= document.getReportDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">INITIAL RECOMMENDATIONS (Prior to Phases I-IV)</div>
								<div class="right">
									<textarea name="initial_recommendations" class="textarea"  style="width: 309px;" rows="3" cols="44"><%= document.getInitialRecommendationsString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">SUPPLEMENTS&nbsp;&#47;&nbsp;HERBAL MIXTURES</div>
								<div class="right">
									<textarea name="supplements_herbal_mixtures" class="textarea"  style="width: 309px;" rows="3" cols="44"><%= document.getSupplementsHerbalMixturesString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DETOX&nbsp;&#47;&nbsp;EXERCISE&nbsp;&#47;&nbsp;<br />DIET&nbsp;&#47;&nbsp;STRESS MANAGEMENT</div>
								<div class="right">
									<textarea name="detox_exercise_diet_stress_management" class="textarea"  style="width: 309px;" rows="3" cols="44"><%= document.getDetoxExerciseDietStressManagementString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">NEXT APPOINTMENT</div>
								<div class="right">
									<textarea name="next_appointment" class="textarea"  style="width: 309px;" rows="3" cols="44"><%= document.getNextAppointmentString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">LOAD FROM TEMPLATE</div>
								<div class="right">
									<select name="template" class="select" style="width: 309px;" onchange="javascript:document.forms[1].submit();">
										<option value="0">-- SELECT A TEMPLATE --</option>
<%
try
{
    Iterator document_itr = MineralRatiosClientDocumentBean.getMineralRatiosClientDocumentTemplates().iterator();
    if (document_itr.hasNext())
    {
	while (document_itr.hasNext())
	{
	    MineralRatiosClientDocumentBean document_obj = (MineralRatiosClientDocumentBean)document_itr.next();
%>
										<option value="<%= document_obj.getValue() %>"><%= document_obj.getTemplateNameString() %></option>
<%
	}
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">PHASE I DATE</div>
								<div class="right">
									<input name="dateInputPhaseI" onfocus="getDate('userMineralRatiosForm','dateInputPhaseI');select();" value="<%= document.getPhaseIDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">
								    <table>
									<tr>
									<td>
									<div >Supplements</div>
									</td>
									<td>
									<div >Detox</div>
									</td>
									<td>
									<div >Exercise</div>
									</td>
									<td>
									<div >Diet</div>
									</td>
									<td>
									<div >Stress</div>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_header_phaseI" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("supplements_header_phaseI").equals("") ? " selected" : "" %>></option>
										<option value="Digestive Support"<%= document.get("supplements_header_phaseI").equals("Digestive Support") ? " selected" : "" %>>Digestive Support</option>
										<option value="Adrenal/Thyroid Support"<%= document.get("supplements_header_phaseI").equals("Adrenal/Thyroid Support") ? " selected" : "" %>>Adrenal/Thyroid Support</option>
										<option value="Thyroid/Adrenal Support"<%= document.get("supplements_header_phaseI").equals("Thyroid/Adrenal Support") ? " selected" : "" %>>Thyroid/Adrenal Support</option>
										<option value="Adrenal Support"<%= document.get("supplements_header_phaseI").equals("Adrenal Support") ? " selected" : "" %>>Adrenal Support</option>
										<option value="Thyroid Support"<%= document.get("supplements_header_phaseI").equals("Thyroid Support") ? " selected" : "" %>>Thyroid Support</option>
										<option value="Heavy Metal Detox"<%= document.get("supplements_header_phaseI").equals("Heavy Metal Detox") ? " selected" : "" %>>Heavy Metal Detox</option>
										<option value="Copper Toxicity"<%= document.get("supplements_header_phaseI").equals("Copper Toxicity") ? " selected" : "" %>>Copper Toxicity</option>
										<option value="Copper Detox/Heavy Metals"<%= document.get("supplements_header_phaseI").equals("Copper Detox/Heavy Metals") ? " selected" : "" %>>Copper Detox/Heavy Metals</option>
										<option value="Maintenance Supplementation"<%= document.get("supplements_header_phaseI").equals("Maintenance Supplementation") ? " selected" : "" %>>Maintenance Supplementation</option>
									</select>
									</td>
									<td>
									<select name="detox_header_phaseI" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("detox_header_phaseI").equals("") ? " selected" : "" %>></option>
										<option value="Liver/GB Flush"<%= document.get("detox_header_phaseI").equals("Liver/GB Flush") ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_header_phaseI").equals("SP Cleanse") ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_header_phaseI").equals("Colon Cleanse") ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_header_phaseI").equals("Juice Fast") ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_header_phaseI").equals("Blood Cleanse") ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_header_phaseI").equals("Homeopathic") ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_header_phaseI" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("exercise_header_phaseI").equals("") ? " selected" : "" %>></option>
										<option value="Walking Daily 30 minutes"<%= document.get("exercise_header_phaseI").equals("Walking Daily 30 minutes") ? " selected" : "" %>>Walking Daily 30 minutes</option>
										<option value="Walking Daily 40-60 minutes"<%= document.get("exercise_header_phaseI").equals("Walking Daily 40-60 minutes") ? " selected" : "" %>>Walking Daily 40-60 minutes</option>
										<option value="Walking & Strength Training"<%= document.get("exercise_header_phaseI").equals("Walking & Strength Training") ? " selected" : "" %>>Walking & Strength Training</option>
										<option value="Cardio 30-60 minutes"<%= document.get("exercise_header_phaseI").equals("Cardio 30-60 minutes") ? " selected" : "" %>>Cardio 30-60 minutes</option>
										<option value="Stretching"<%= document.get("exercise_header_phaseI").equals("Stretching") ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_header_phaseI" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("diet_header_phaseI").equals("") ? " selected" : "" %>></option>
										<option value="Nutrition Plan"<%= document.get("diet_header_phaseI").equals("Nutrition Plan") ? " selected" : "" %>>Nutrition Plan</option>
									</select>
									</td>
									<td>
									<select name="stress_header_phaseI" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("stress_header_phaseI").equals("") ? " selected" : "" %>></option>
										<option value="Emotional"<%= document.get("stress_header_phaseI").equals("Emotional") ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_header_phaseI").equals("Mental") ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_header_phaseI").equals("Physical") ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_header_phaseI").equals("Spiritual") ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_header_phaseI").equals("Toxins") ? " selected" : "" %>>Toxins</option>
										<option value="Toxin (Mercury)"<%= document.get("stress_header_phaseI").equals("Toxin (Mercury)") ? " selected" : "" %>>Toxin (Mercury)</option>
										<option value="Toxin (Copper)"<%= document.get("stress_header_phaseI").equals("Toxin (Copper)") ? " selected" : "" %>>Toxin (Copper)</option>
										<option value="Toxin (Cadmium)"<%= document.get("stress_header_phaseI").equals("Toxin (Cadmium)") ? " selected" : "" %>>Toxin (Cadmium)</option>
										<option value="Toxin (Aluminium)"<%= document.get("stress_header_phaseI").equals("Toxin (Aluminium)") ? " selected" : "" %>>Toxin (Aluminium)</option>
										<option value="Toxin (Nickel)"<%= document.get("stress_header_phaseI").equals("Toxin (Nickel)") ? " selected" : "" %>>Toxin (Nickel)</option>
										<option value="Toxin (Cobalt)"<%= document.get("stress_header_phaseI").equals("Toxin (Cobalt)") ? " selected" : "" %>>Toxin (Cobalt)</option>
										<option value="Toxin (Manganese)"<%= document.get("stress_header_phaseI").equals("Toxin (Manganese)") ? " selected" : "" %>>Toxin (Manganese)</option>
										<option value="Toxin (Lead)"<%= document.get("stress_header_phaseI").equals("Toxin (Lead)") ? " selected" : "" %>>Toxin (Lead)</option>
										<option value="Biochemical"<%= document.get("stress_header_phaseI").equals("Biochemical") ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_header_phaseI").equals("Energetic (EMF)") ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_phaseI" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Multizyme"<%= document.get("supplements_phaseI").indexOf("Multizyme|") > -1 ? " selected" : "" %>>Multizyme</option>
										<option value="Sp Bk Radish"<%= document.get("supplements_phaseI").indexOf("Sp Bk Radish|") > -1 ? " selected" : "" %>>Sp Bk Radish</option>
										<option value="Zypan"<%= document.get("supplements_phaseI").indexOf("Zypan|") > -1 ? " selected" : "" %>>Zypan</option>
										<option value="Pancreatrophin PMG"<%= document.get("supplements_phaseI").indexOf("Pancreatrophin PMG|") > -1 ? " selected" : "" %>>Pancreatrophin PMG</option>
										<option value="Fungal Forte"<%= document.get("supplements_phaseI").indexOf("Fungal Forte|") > -1 ? " selected" : "" %>>Fungal Forte</option>
										<option value="Multi-Probiotic"<%= document.get("supplements_phaseI").indexOf("Multi-Probiotic|") > -1 ? " selected" : "" %>>Multi-Probiotic</option>
										<option value="Cholacol"<%= document.get("supplements_phaseI").indexOf("Cholacol|") > -1 ? " selected" : "" %>>Cholacol</option>
										<option value="Theralac"<%= document.get("supplements_phaseI").indexOf("Theralac|") > -1 ? " selected" : "" %>>Theralac</option>
										<option value="Lactic Acid Yeast waf"<%= document.get("supplements_phaseI").indexOf("Lactic Acid Yeast waf|") > -1 ? " selected" : "" %>>Lactic Acid Yeast waf</option>
										<option value="DiGest (liq)"<%= document.get("supplements_phaseI").indexOf("DiGest (liq)|") > -1 ? " selected" : "" %>>DiGest (liq)</option>
										<option value="DiGest (caps)"<%= document.get("supplements_phaseI").indexOf("DiGest (caps)|") > -1 ? " selected" : "" %>>DiGest (caps)</option>
										<option value="Zymex"<%= document.get("supplements_phaseI").indexOf("Zymex|") > -1 ? " selected" : "" %>>Zymex</option>
										<option value="Zymex waf"<%= document.get("supplements_phaseI").indexOf("Zymex waf|") > -1 ? " selected" : "" %>>Zymex waf</option>
										<option value="Lact-Enz"<%= document.get("supplements_phaseI").indexOf("Lact-Enz|") > -1 ? " selected" : "" %>>Lact-Enz</option>
										<option value="A-F Betafood"<%= document.get("supplements_phaseI").indexOf("A-F Betafood|") > -1 ? " selected" : "" %>>A-F Betafood</option>
										<option value="LivCo"<%= document.get("supplements_phaseI").indexOf("LivCo|") > -1 ? " selected" : "" %>>LivCo</option>
										<option value="DigestPlex (GF)"<%= document.get("supplements_phaseI").indexOf("DigestPlex (GF|") > -1 ? " selected" : "" %>>DigestPlex (GF)</option>
										<option value="Protein Powder"<%= document.get("supplements_phaseI").indexOf("Protein Powder|") > -1 ? " selected" : "" %>>Protein Powder</option>
										<option value="Greens"<%= document.get("supplements_phaseI").indexOf("Greens|") > -1 ? " selected" : "" %>>Greens</option>
										<option value="Cataplex GTF"<%= document.get("supplements_phaseI").indexOf("Cataplex GTF|") > -1 ? " selected" : "" %>>Cataplex GTF</option>
										<option value="Gymnema"<%= document.get("supplements_phaseI").indexOf("Gymnema|") > -1 ? " selected" : "" %>>Gymnema</option>
										<option value="Paraplex"<%= document.get("supplements_phaseI").indexOf("Paraplex|") > -1 ? " selected" : "" %>>Paraplex</option>
										<option value="Inositol"<%= document.get("supplements_phaseI").indexOf("Inositol|") > -1 ? " selected" : "" %>>Inositol</option>
										<option value="Detox Formula (GF)"<%= document.get("supplements_phaseI").indexOf("Detox Formula (GF)|") > -1 ? " selected" : "" %>>Detox Formula (GF)</option>
										<option value="PhytoGreens (GF)"<%= document.get("supplements_phaseI").indexOf("PhytoGreens (GF)|") > -1 ? " selected" : "" %>>PhytoGreens (GF)</option>
										<option value="VitaLiv"<%= document.get("supplements_phaseI").indexOf("VitaLiv|") > -1 ? " selected" : "" %>>VitaLiv</option>
										<option value="BFood"<%= document.get("supplements_phaseI").indexOf("BFood|") > -1 ? " selected" : "" %>>BFood</option>
										<option value="Primal Defense"<%= document.get("supplements_phaseI").indexOf("Primal Defense|") > -1 ? " selected" : "" %>>Primal Defense</option>
										<option value="Chewable Catalyn"<%= document.get("supplements_phaseI").indexOf("Chewable Catalyn|") > -1 ? " selected" : "" %>>Chewable Catalyn</option>
										<option value="Ferrofood"<%= document.get("supplements_phaseI").indexOf("Ferrofood|") > -1 ? " selected" : "" %>>Ferrofood</option>
										<option value="Catalyn"<%= document.get("supplements_phaseI").indexOf("Catalyn|") > -1 ? " selected" : "" %>>Catalyn</option>
										<option value="Boswellia Complex"<%= document.get("supplements_phaseI").indexOf("Boswellia Complex|") > -1 ? " selected" : "" %>>Boswellia Complex</option>
										<option value="Cranberry Complex"<%= document.get("supplements_phaseI").indexOf("Cranberry Complex|") > -1 ? " selected" : "" %>>Cranberry Complex</option>
										<option value="Rehmannia Complex"<%= document.get("supplements_phaseI").indexOf("Rehmannia Complex|") > -1 ? " selected" : "" %>>Rehmannia Complex</option>
										<option value="Withania Complex"<%= document.get("supplements_phaseI").indexOf("Withania Complex|") > -1 ? " selected" : "" %>>Withania Complex</option>
										<option value="Thyroid Complex"<%= document.get("supplements_phaseI").indexOf("Thyroid Complex|") > -1 ? " selected" : "" %>>Thyroid Complex</option>
										<option value="Congaplex"<%= document.get("supplements_phaseI").indexOf("Congaplex|") > -1 ? " selected" : "" %>>Congaplex</option>
										<option value="Cardiotrophin PMG"<%= document.get("supplements_phaseI").indexOf("Cardiotrophin PMG|") > -1 ? " selected" : "" %>>Cardiotrophin PMG</option>
										<option value="Drenatrophin PMG"<%= document.get("supplements_phaseI").indexOf("Drenatrophin PMG|") > -1 ? " selected" : "" %>>Drenatrophin PMG</option>
										<option value="Hypothalamus PMG"<%= document.get("supplements_phaseI").indexOf("Hypothalamus PMG|") > -1 ? " selected" : "" %>>Hypothalamus PMG</option>
										<option value="Mammary PMG"<%= document.get("supplements_phaseI").indexOf("Mammary PMG|") > -1 ? " selected" : "" %>>Mammary PMG</option>
										<option value="Myotrophin PMG"<%= document.get("supplements_phaseI").indexOf("Myotrophin PMG|") > -1 ? " selected" : "" %>>Myotrophin PMG</option>
										<option value="Neurotrophin PMG"<%= document.get("supplements_phaseI").indexOf("Neurotrophin PM|") > -1 ? " selected" : "" %>>Neurotrophin PMG</option>
										<option value="Ostrophin PMG"<%= document.get("supplements_phaseI").indexOf("Ostrophin PMG|") > -1 ? " selected" : "" %>>Ostrophin PMG</option>
										<option value="Ovatrophin PMG"<%= document.get("supplements_phaseI").indexOf("Ovatrophin PMG|") > -1 ? " selected" : "" %>>Ovatrophin PMG</option>
										<option value="Pneumotrophin PMG"<%= document.get("supplements_phaseI").indexOf("Pneumotrophin PMG|") > -1 ? " selected" : "" %>>Pneumotrophin PMG</option>
										<option value="Pituitrophin PMG"<%= document.get("supplements_phaseI").indexOf("Pituitrophin PMG|") > -1 ? " selected" : "" %>>Pituitrophin PMG</option>
										<option value="Prostate PMG"<%= document.get("supplements_phaseI").indexOf("Prostate PMG|") > -1 ? " selected" : "" %>>Prostate PMG</option>
										<option value="Renatrophin PMG"<%= document.get("supplements_phaseI").indexOf("Renatrophin PMG|") > -1 ? " selected" : "" %>>Renatrophin PMG</option>
										<option value="Thymus PMG"<%= document.get("supplements_phaseI").indexOf("Thymus PMG|") > -1 ? " selected" : "" %>>Thymus PMG</option>
										<option value="Thytrophin PMG"<%= document.get("supplements_phaseI").indexOf("Thytrophin PM|") > -1 ? " selected" : "" %>>Thytrophin PMG</option>
										<option value="Utrophin PMG"<%= document.get("supplements_phaseI").indexOf("Utrophin PM|") > -1 ? " selected" : "" %>>Utrophin PMG</option>
										<option value="A-C Carbamide"<%= document.get("supplements_phaseI").indexOf("A-C Carbamide|") > -1 ? " selected" : "" %>>A-C Carbamide</option>
										<option value="FlavoC"<%= document.get("supplements_phaseI").indexOf("FlavoC|") > -1 ? " selected" : "" %>>FlavoC</option>
										<option value="Cat A"<%= document.get("supplements_phaseI").indexOf("Cat A|") > -1 ? " selected" : "" %>>Cat A</option>
										<option value="Cat A-C"<%= document.get("supplements_phaseI").indexOf("Cat A-C|") > -1 ? " selected" : "" %>>Cat A-C</option>
										<option value="Cat A-C-P"<%= document.get("supplements_phaseI").indexOf("Cat A-C-P|") > -1 ? " selected" : "" %>>Cat A-C-P</option>
										<option value="Cat B"<%= document.get("supplements_phaseI").indexOf("Cat B|") > -1 ? " selected" : "" %>>Cat B</option>
										<option value="Cat C"<%= document.get("supplements_phaseI").indexOf("Cat C|") > -1 ? " selected" : "" %>>Cat C</option>
										<option value="Cat D"<%= document.get("supplements_phaseI").indexOf("Cat D|") > -1 ? " selected" : "" %>>Cat D</option>
										<option value="Cat E"<%= document.get("supplements_phaseI").indexOf("Cat E|") > -1 ? " selected" : "" %>>Cat E</option>
										<option value="Cataplex F"<%= document.get("supplements_phaseI").indexOf("Cataplex F|") > -1 ? " selected" : "" %>>Cat F</option>
										<option value="Cataplex G"<%= document.get("supplements_phaseI").indexOf("Cataplex G|") > -1 ? " selected" : "" %>>Cataplex G</option>
										<option value="CalMag"<%= document.get("supplements_phaseI").indexOf("CalMag|") > -1 ? " selected" : "" %>>CalMag</option>
										<option value="Cal Lact Powder"<%= document.get("supplements_phaseI").indexOf("Cal Lact Powder|") > -1 ? " selected" : "" %>>Cal Lact Powder</option>
										<option value="Magnesium Lactate"<%= document.get("supplements_phaseI").indexOf("Magnesium Lactate|") > -1 ? " selected" : "" %>>Magnesium Lactate</option>
										<option value="Manganese B12"<%= document.get("supplements_phaseI").indexOf("Manganese B12|") > -1 ? " selected" : "" %>>Manganese B12</option>
										<option value="Liquid Herbs"<%= document.get("supplements_phaseI").indexOf("Liquid Herbs|") > -1 ? " selected" : "" %>>Liquid Herbs</option>
										<option value="CardioPlus"<%= document.get("supplements_phaseI").indexOf("CardioPlus|") > -1 ? " selected" : "" %>>CardioPlus</option>
										<option value="Adrenal Des"<%= document.get("supplements_phaseI").indexOf("Adrenal Des|") > -1 ? " selected" : "" %>>Adrenal Des</option>
										<option value="Spleen Des"<%= document.get("supplements_phaseI").indexOf("Spleen Des|") > -1 ? " selected" : "" %>>Spleen Des</option>
										<option value="Drenamin"<%= document.get("supplements_phaseI").indexOf("Drenamin|") > -1 ? " selected" : "" %>>Drenamin</option>
										<option value="Fish Oil"<%= document.get("supplements_phaseI").indexOf("Fish Oil|") > -1 ? " selected" : "" %>>Fish Oil</option>
										<option value="OBM"<%= document.get("supplements_phaseI").indexOf("OBM|") > -1 ? " selected" : "" %>>OBM</option>
										<option value="Prolamine Iodine"<%= document.get("supplements_phaseI").indexOf("Prolamine Iodine|") > -1 ? " selected" : "" %>>Prolamine Iodine</option>
										<option value="Parotid"<%= document.get("supplements_phaseI").indexOf("Parotid|") > -1 ? " selected" : "" %>>Parotid</option>
										<option value="Cholacol II"<%= document.get("supplements_phaseI").indexOf("Cholacol II|") > -1 ? " selected" : "" %>>Cholacol II</option>
										<option value="Cholaplex"<%= document.get("supplements_phaseI").indexOf("Cholaplex|") > -1 ? " selected" : "" %>>Cholaplex</option>
										<option value="Zinc Liver Chelate"<%= document.get("supplements_phaseI").indexOf("Zinc Liver Chelate|") > -1 ? " selected" : "" %>>Zinc Liver Chelate</option>
										<option value="Protefood"<%= document.get("supplements_phaseI").indexOf("Protefood|") > -1 ? " selected" : "" %>>Protefood</option>
										<option value="MolyCu"<%= document.get("supplements_phaseI").indexOf("MolyCu|") > -1 ? " selected" : "" %>>MolyCu</option>
										<option value="GB3"<%= document.get("supplements_phaseI").indexOf("GB3|") > -1 ? " selected" : "" %>>GB3</option>
										<option value="EndoPan"<%= document.get("supplements_phaseI").indexOf("EndoPan|") > -1 ? " selected" : "" %>>EndoPan</option>
										<option value="Flaxseed oil"<%= document.get("supplements_phaseI").indexOf("Flaxseed oil|") > -1 ? " selected" : "" %>>Flaxseed oil</option>
										<option value="Arginex"<%= document.get("supplements_phaseI").indexOf("Arginex|") > -1 ? " selected" : "" %>>Arginex</option>
										<option value="Rehmannia (liq)"<%= document.get("supplements_phaseI").indexOf("Rehmannia (liq)|") > -1 ? " selected" : "" %>>Rehmannia (liq)</option>
										<option value="Saligesic"<%= document.get("supplements_phaseI").indexOf("Saligesic|") > -1 ? " selected" : "" %>>Saligesic</option>
										<option value="Vitanox"<%= document.get("supplements_phaseI").indexOf("Vitanox|") > -1 ? " selected" : "" %>>Vitanox</option>
										<option value="St. John's Wort"<%= document.get("supplements_phaseI").indexOf("St. John's Wort|") > -1 ? " selected" : "" %>>St. John's Wort</option>
										<option value="California Poppy"<%= document.get("supplements_phaseI").indexOf("California Poppy|") > -1 ? " selected" : "" %>>California Poppy</option>
										<option value="Broncafect"<%= document.get("supplements_phaseI").indexOf("Broncafect|") > -1 ? " selected" : "" %>>Broncafect</option>
										<option value="Min-Tran"<%= document.get("supplements_phaseI").indexOf("Min-Tran|") > -1 ? " selected" : "" %>>Min-Tran</option>
										<option value="Min-Chex"<%= document.get("supplements_phaseI").indexOf("Min-Chex|") > -1 ? " selected" : "" %>>Min-Chex</option>
										<option value="Progon B"<%= document.get("supplements_phaseI").indexOf("Progon B|") > -1 ? " selected" : "" %>>Progon B</option>
										<option value="Tribulus"<%= document.get("supplements_phaseI").indexOf("Tribulus|") > -1 ? " selected" : "" %>>Tribulus</option>
										<option value="Wheat Germ oil"<%= document.get("supplements_phaseI").indexOf("Wheat Germ oil|") > -1 ? " selected" : "" %>>Wheat Germ oil</option>
										<option value="Symplex F"<%= document.get("supplements_phaseI").indexOf("Symplex F|") > -1 ? " selected" : "" %>>Symplex F</option>
										<option value="Glucosamine Synergy"<%= document.get("supplements_phaseI").indexOf("Glucosamine Synergy|") > -1 ? " selected" : "" %>>Glucosamine Synergy</option>
										<option value="Ligaplex II"<%= document.get("supplements_phaseI").indexOf("Ligaplex II|") > -1 ? " selected" : "" %>>Ligaplex II</option>
										<option value="Collagen C"<%= document.get("supplements_phaseI").indexOf("Collagen C|") > -1 ? " selected" : "" %>>Collagen C</option>
										<option value="Symplex M"<%= document.get("supplements_phaseI").indexOf("Symplex M|") > -1 ? " selected" : "" %>>Symplex M</option>
										<option value="Male Support"<%= document.get("supplements_phaseI").indexOf("Male Support|") > -1 ? " selected" : "" %>>Male Support</option>
										<option value="Orchex"<%= document.get("supplements_phaseI").indexOf("Orchex|") > -1 ? " selected" : "" %>>Orchex</option>
										<option value="Lipoic Acid"<%= document.get("supplements_phaseI").indexOf("Lipoic Acid|") > -1 ? " selected" : "" %>>Lipoic Acid</option>
										<option value="Ambrotose"<%= document.get("supplements_phaseI").indexOf("Ambrotose|") > -1 ? " selected" : "" %>>Ambrotose</option>
									</select>
									</td>
									<td>
									<select name="detox_phaseI" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Liver/GB Flush"<%= document.get("detox_phaseI").indexOf("Liver/GB Flush|") > -1 ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_phaseI").indexOf("SP Cleanse|") > -1 ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_phaseI").indexOf("Colon Cleanse|") > -1 ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_phaseI").indexOf("Juice Fast|") > -1 ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_phaseI").indexOf("Blood Cleanse|") > -1 ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_phaseI").indexOf("Homeopathic|") > -1 ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_phaseI" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Walk Daily 30 min"<%= document.get("exercise_phaseI").indexOf("Walk Daily 30 min|") > -1 ? " selected" : "" %>>Walk Daily 30 min</option>
										<option value="Walk Daily 40-60 min"<%= document.get("exercise_phaseI").indexOf("Walk Daily 40-60 min|") > -1 ? " selected" : "" %>>Walk Daily 40-60 min</option>
										<option value="Walk/Strength Train"<%= document.get("exercise_phaseI").indexOf("Walk/Strength Train|") > -1 ? " selected" : "" %>>Walk/Strength Train</option>
										<option value="Cardio 30-60 min"<%= document.get("exercise_phaseI").indexOf("Cardio 30-60 min|") > -1 ? " selected" : "" %>>Cardio 30-60 min</option>
										<option value="Stretching"<%= document.get("exercise_phaseI").indexOf("Stretching|") > -1 ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_phaseI" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="No sugar/fruit"<%= document.get("diet_phaseI").indexOf("No sugar/fruit|") > -1 ? " selected" : "" %>>No sugar/fruit</option>
										<option value="Protein Shakes"<%= document.get("diet_phaseI").indexOf("Protein Shakes|") > -1 ? " selected" : "" %>>Protein Shakes</option>
										<option value="Protein Shakes w/greens"<%= document.get("diet_phaseI").indexOf("Protein Shakes w/greens|") > -1 ? " selected" : "" %>>Protein Shakes w/greens</option>
										<option value="Protein Shakes w/flax oil"<%= document.get("diet_phaseI").indexOf("Protein Shakes w/flax oil|") > -1 ? " selected" : "" %>>Protein Shakes w/flax oil</option>
										<option value="Evening Primrose oil"<%= document.get("diet_phaseI").indexOf("Evening Primrose oil|") > -1 ? " selected" : "" %>>Evening Primrose oil</option>
										<option value="Flaxseed oil"<%= document.get("diet_phaseI").indexOf("Flaxseed oil|") > -1 ? " selected" : "" %>>Flaxseed oil</option>
										<option value="Garlic"<%= document.get("diet_phaseI").indexOf("Garlic|") > -1 ? " selected" : "" %>>Garlic</option>
										<option value="Water Testing"<%= document.get("diet_phaseI").indexOf("Water Testing|") > -1 ? " selected" : "" %>>Water Testing</option>
									</select>
									</td>
									<td>
									<select name="stress_phaseI" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Emotional"<%= document.get("stress_phaseI").indexOf("Emotional|") > -1 ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_phaseI").indexOf("Mental|") > -1 ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_phaseI").indexOf("Physical|") > -1 ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_phaseI").indexOf("Spiritual|") > -1 ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_phaseI").indexOf("Toxins|") > -1 ? " selected" : "" %>>Toxins</option>
										<option value="Lead"<%= document.get("stress_phaseI").indexOf("Lead|") > -1 ? " selected" : "" %>>Lead</option>
										<option value="Mercury"<%= document.get("stress_phaseI").indexOf("Mercury|") > -1 ? " selected" : "" %>>Mercury</option>
										<option value="Cadmium"<%= document.get("stress_phaseI").indexOf("Cadmium|") > -1 ? " selected" : "" %>>Cadmium</option>
										<option value="Arsenic"<%= document.get("stress_phaseI").indexOf("Arsenic|") > -1 ? " selected" : "" %>>Arsenic</option>
										<option value="Aluminium"<%= document.get("stress_phaseI").indexOf("Aluminium|") > -1 ? " selected" : "" %>>Aluminium</option>
										<option value="Copper"<%= document.get("stress_phaseI").indexOf("Copper|") > -1 ? " selected" : "" %>>Copper</option>
										<option value="Nickel"<%= document.get("stress_phaseI").indexOf("Nickel|") > -1 ? " selected" : "" %>>Nickel</option>
										<option value="Zinc"<%= document.get("stress_phaseI").indexOf("Zinc|") > -1 ? " selected" : "" %>>Zinc</option>
										<option value="Cobalt"<%= document.get("stress_phaseI").indexOf("Cobalt|") > -1 ? " selected" : "" %>>Cobalt</option>
										<option value="Manganese"<%= document.get("stress_phaseI").indexOf("Manganese|") > -1 ? " selected" : "" %>>Manganese</option>
										<option value="Biochemical"<%= document.get("stress_phaseI").indexOf("Biochemical|") > -1 ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_phaseI").indexOf("Energetic (EMF)|") > -1 ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
								    </table>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">PHASE II DATE</div>
								<div class="right">
									<input name="dateInputPhaseII" onfocus="getDate('userMineralRatiosForm','dateInputPhaseII');select();" value="<%= document.getPhaseIIDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">
								    <table>
									<tr>
									<td>
									<div >Supplements</div>
									</td>
									<td>
									<div >Detox</div>
									</td>
									<td>
									<div >Exercise</div>
									</td>
									<td>
									<div >Diet</div>
									</td>
									<td>
									<div >Stress</div>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_header_phaseII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("supplements_header_phaseII").equals("") ? " selected" : "" %>></option>
										<option value="Digestive Support"<%= document.get("supplements_header_phaseII").equals("Digestive Support") ? " selected" : "" %>>Digestive Support</option>
										<option value="Adrenal/Thyroid Support"<%= document.get("supplements_header_phaseII").equals("Adrenal/Thyroid Support") ? " selected" : "" %>>Adrenal/Thyroid Support</option>
										<option value="Thyroid/Adrenal Support"<%= document.get("supplements_header_phaseII").equals("Thyroid/Adrenal Support") ? " selected" : "" %>>Thyroid/Adrenal Support</option>
										<option value="Adrenal Support"<%= document.get("supplements_header_phaseII").equals("Adrenal Support") ? " selected" : "" %>>Adrenal Support</option>
										<option value="Thyroid Support"<%= document.get("supplements_header_phaseII").equals("Thyroid Support") ? " selected" : "" %>>Thyroid Support</option>
										<option value="Heavy Metal Detox"<%= document.get("supplements_header_phaseII").equals("Heavy Metal Detox") ? " selected" : "" %>>Heavy Metal Detox</option>
										<option value="Copper Toxicity"<%= document.get("supplements_header_phaseII").equals("Copper Toxicity") ? " selected" : "" %>>Copper Toxicity</option>
										<option value="Copper Detox/Heavy Metals"<%= document.get("supplements_header_phaseII").equals("Copper Detox/Heavy Metals") ? " selected" : "" %>>Copper Detox/Heavy Metals</option>
										<option value="Maintenance Supplementation"<%= document.get("supplements_header_phaseII").equals("Maintenance Supplementation") ? " selected" : "" %>>Maintenance Supplementation</option>
									</select>
									</td>
									<td>
									<select name="detox_header_phaseII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("detox_header_phaseII").equals("") ? " selected" : "" %>></option>
										<option value="Liver/GB Flush"<%= document.get("detox_header_phaseII").equals("Liver/GB Flush") ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_header_phaseII").equals("SP Cleanse") ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_header_phaseII").equals("Colon Cleanse") ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_header_phaseII").equals("Juice Fast") ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_header_phaseII").equals("Blood Cleanse") ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_header_phaseII").equals("Homeopathic") ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_header_phaseII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("exercise_header_phaseII").equals("") ? " selected" : "" %>></option>
										<option value="Walking Daily 30 minutes"<%= document.get("exercise_header_phaseII").equals("Walking Daily 30 minutes") ? " selected" : "" %>>Walking Daily 30 minutes</option>
										<option value="Walking Daily 40-60 minutes"<%= document.get("exercise_header_phaseII").equals("Walking Daily 40-60 minutes") ? " selected" : "" %>>Walking Daily 40-60 minutes</option>
										<option value="Walking & Strength Training"<%= document.get("exercise_header_phaseII").equals("Walking & Strength Training") ? " selected" : "" %>>Walking & Strength Training</option>
										<option value="Cardio 30-60 minutes"<%= document.get("exercise_header_phaseII").equals("Cardio 30-60 minutes") ? " selected" : "" %>>Cardio 30-60 minutes</option>
										<option value="Stretching"<%= document.get("exercise_header_phaseII").equals("Stretching") ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_header_phaseII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("diet_header_phaseII").equals("") ? " selected" : "" %>></option>
										<option value="Nutrition Plan"<%= document.get("diet_header_phaseII").equals("Nutrition Plan") ? " selected" : "" %>>Nutrition Plan</option>
									</select>
									</td>
									<td>
									<select name="stress_header_phaseII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("stress_header_phaseII").equals("") ? " selected" : "" %>></option>
										<option value="Emotional"<%= document.get("stress_header_phaseII").equals("Emotional") ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_header_phaseII").equals("Mental") ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_header_phaseII").equals("Physical") ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_header_phaseII").equals("Spiritual") ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_header_phaseII").equals("Toxins") ? " selected" : "" %>>Toxins</option>
										<option value="Toxin (Mercury)"<%= document.get("stress_header_phaseII").equals("Toxin (Mercury)") ? " selected" : "" %>>Toxin (Mercury)</option>
										<option value="Toxin (Copper)"<%= document.get("stress_header_phaseII").equals("Toxin (Copper)") ? " selected" : "" %>>Toxin (Copper)</option>
										<option value="Toxin (Cadmium)"<%= document.get("stress_header_phaseII").equals("Toxin (Cadmium)") ? " selected" : "" %>>Toxin (Cadmium)</option>
										<option value="Toxin (Aluminium)"<%= document.get("stress_header_phaseII").equals("Toxin (Aluminium)") ? " selected" : "" %>>Toxin (Aluminium)</option>
										<option value="Toxin (Nickel)"<%= document.get("stress_header_phaseII").equals("Toxin (Nickel)") ? " selected" : "" %>>Toxin (Nickel)</option>
										<option value="Toxin (Cobalt)"<%= document.get("stress_header_phaseII").equals("Toxin (Cobalt)") ? " selected" : "" %>>Toxin (Cobalt)</option>
										<option value="Toxin (Manganese)"<%= document.get("stress_header_phaseII").equals("Toxin (Manganese)") ? " selected" : "" %>>Toxin (Manganese)</option>
										<option value="Toxin (Lead)"<%= document.get("stress_header_phaseII").equals("Toxin (Lead)") ? " selected" : "" %>>Toxin (Lead)</option>
										<option value="Biochemical"<%= document.get("stress_header_phaseII").equals("Biochemical") ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_header_phaseII").equals("Energetic (EMF)") ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_phaseII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Multizyme"<%= document.get("supplements_phaseII").indexOf("Multizyme|") > -1 ? " selected" : "" %>>Multizyme</option>
										<option value="Sp Bk Radish"<%= document.get("supplements_phaseII").indexOf("Sp Bk Radish|") > -1 ? " selected" : "" %>>Sp Bk Radish</option>
										<option value="Zypan"<%= document.get("supplements_phaseII").indexOf("Zypan|") > -1 ? " selected" : "" %>>Zypan</option>
										<option value="Pancreatrophin PMG"<%= document.get("supplements_phaseII").indexOf("Pancreatrophin PMG|") > -1 ? " selected" : "" %>>Pancreatrophin PMG</option>
										<option value="Fungal Forte"<%= document.get("supplements_phaseII").indexOf("Fungal Forte|") > -1 ? " selected" : "" %>>Fungal Forte</option>
										<option value="Multi-Probiotic"<%= document.get("supplements_phaseII").indexOf("Multi-Probiotic|") > -1 ? " selected" : "" %>>Multi-Probiotic</option>
										<option value="Cholacol"<%= document.get("supplements_phaseII").indexOf("Cholacol|") > -1 ? " selected" : "" %>>Cholacol</option>
										<option value="Theralac"<%= document.get("supplements_phaseII").indexOf("Theralac|") > -1 ? " selected" : "" %>>Theralac</option>
										<option value="Lactic Acid Yeast waf"<%= document.get("supplements_phaseII").indexOf("Lactic Acid Yeast waf|") > -1 ? " selected" : "" %>>Lactic Acid Yeast waf</option>
										<option value="DiGest (liq)"<%= document.get("supplements_phaseII").indexOf("DiGest (liq)|") > -1 ? " selected" : "" %>>DiGest (liq)</option>
										<option value="DiGest (caps)"<%= document.get("supplements_phaseII").indexOf("DiGest (caps)|") > -1 ? " selected" : "" %>>DiGest (caps)</option>
										<option value="Zymex"<%= document.get("supplements_phaseII").indexOf("Zymex|") > -1 ? " selected" : "" %>>Zymex</option>
										<option value="Zymex waf"<%= document.get("supplements_phaseII").indexOf("Zymex waf|") > -1 ? " selected" : "" %>>Zymex waf</option>
										<option value="Lact-Enz"<%= document.get("supplements_phaseII").indexOf("Lact-Enz|") > -1 ? " selected" : "" %>>Lact-Enz</option>
										<option value="A-F Betafood"<%= document.get("supplements_phaseII").indexOf("A-F Betafood|") > -1 ? " selected" : "" %>>A-F Betafood</option>
										<option value="LivCo"<%= document.get("supplements_phaseII").indexOf("LivCo|") > -1 ? " selected" : "" %>>LivCo</option>
										<option value="DigestPlex (GF)"<%= document.get("supplements_phaseII").indexOf("DigestPlex (GF|") > -1 ? " selected" : "" %>>DigestPlex (GF)</option>
										<option value="Protein Powder"<%= document.get("supplements_phaseII").indexOf("Protein Powder|") > -1 ? " selected" : "" %>>Protein Powder</option>
										<option value="Greens"<%= document.get("supplements_phaseII").indexOf("Greens|") > -1 ? " selected" : "" %>>Greens</option>
										<option value="Cataplex GTF"<%= document.get("supplements_phaseII").indexOf("Cataplex GTF|") > -1 ? " selected" : "" %>>Cataplex GTF</option>
										<option value="Gymnema"<%= document.get("supplements_phaseII").indexOf("Gymnema|") > -1 ? " selected" : "" %>>Gymnema</option>
										<option value="Paraplex"<%= document.get("supplements_phaseII").indexOf("Paraplex|") > -1 ? " selected" : "" %>>Paraplex</option>
										<option value="Inositol"<%= document.get("supplements_phaseII").indexOf("Inositol|") > -1 ? " selected" : "" %>>Inositol</option>
										<option value="Detox Formula (GF)"<%= document.get("supplements_phaseII").indexOf("Detox Formula (GF)|") > -1 ? " selected" : "" %>>Detox Formula (GF)</option>
										<option value="PhytoGreens (GF)"<%= document.get("supplements_phaseII").indexOf("PhytoGreens (GF)|") > -1 ? " selected" : "" %>>PhytoGreens (GF)</option>
										<option value="VitaLiv"<%= document.get("supplements_phaseII").indexOf("VitaLiv|") > -1 ? " selected" : "" %>>VitaLiv</option>
										<option value="BFood"<%= document.get("supplements_phaseII").indexOf("BFood|") > -1 ? " selected" : "" %>>BFood</option>
										<option value="Primal Defense"<%= document.get("supplements_phaseII").indexOf("Primal Defense|") > -1 ? " selected" : "" %>>Primal Defense</option>
										<option value="Chewable Catalyn"<%= document.get("supplements_phaseII").indexOf("Chewable Catalyn|") > -1 ? " selected" : "" %>>Chewable Catalyn</option>
										<option value="Ferrofood"<%= document.get("supplements_phaseII").indexOf("Ferrofood|") > -1 ? " selected" : "" %>>Ferrofood</option>
										<option value="Catalyn"<%= document.get("supplements_phaseII").indexOf("Catalyn|") > -1 ? " selected" : "" %>>Catalyn</option>
										<option value="Boswellia Complex"<%= document.get("supplements_phaseII").indexOf("Boswellia Complex|") > -1 ? " selected" : "" %>>Boswellia Complex</option>
										<option value="Cranberry Complex"<%= document.get("supplements_phaseII").indexOf("Cranberry Complex|") > -1 ? " selected" : "" %>>Cranberry Complex</option>
										<option value="Rehmannia Complex"<%= document.get("supplements_phaseII").indexOf("Rehmannia Complex|") > -1 ? " selected" : "" %>>Rehmannia Complex</option>
										<option value="Withania Complex"<%= document.get("supplements_phaseII").indexOf("Withania Complex|") > -1 ? " selected" : "" %>>Withania Complex</option>
										<option value="Thyroid Complex"<%= document.get("supplements_phaseII").indexOf("Thyroid Complex|") > -1 ? " selected" : "" %>>Thyroid Complex</option>
										<option value="Congaplex"<%= document.get("supplements_phaseII").indexOf("Congaplex|") > -1 ? " selected" : "" %>>Congaplex</option>
										<option value="Cardiotrophin PMG"<%= document.get("supplements_phaseII").indexOf("Cardiotrophin PMG|") > -1 ? " selected" : "" %>>Cardiotrophin PMG</option>
										<option value="Drenatrophin PMG"<%= document.get("supplements_phaseII").indexOf("Drenatrophin PMG|") > -1 ? " selected" : "" %>>Drenatrophin PMG</option>
										<option value="Hypothalamus PMG"<%= document.get("supplements_phaseII").indexOf("Hypothalamus PMG|") > -1 ? " selected" : "" %>>Hypothalamus PMG</option>
										<option value="Mammary PMG"<%= document.get("supplements_phaseII").indexOf("Mammary PMG|") > -1 ? " selected" : "" %>>Mammary PMG</option>
										<option value="Myotrophin PMG"<%= document.get("supplements_phaseII").indexOf("Myotrophin PMG|") > -1 ? " selected" : "" %>>Myotrophin PMG</option>
										<option value="Neurotrophin PMG"<%= document.get("supplements_phaseII").indexOf("Neurotrophin PM|") > -1 ? " selected" : "" %>>Neurotrophin PMG</option>
										<option value="Ostrophin PMG"<%= document.get("supplements_phaseII").indexOf("Ostrophin PMG|") > -1 ? " selected" : "" %>>Ostrophin PMG</option>
										<option value="Ovatrophin PMG"<%= document.get("supplements_phaseII").indexOf("Ovatrophin PMG|") > -1 ? " selected" : "" %>>Ovatrophin PMG</option>
										<option value="Pneumotrophin PMG"<%= document.get("supplements_phaseII").indexOf("Pneumotrophin PMG|") > -1 ? " selected" : "" %>>Pneumotrophin PMG</option>
										<option value="Pituitrophin PMG"<%= document.get("supplements_phaseII").indexOf("Pituitrophin PMG|") > -1 ? " selected" : "" %>>Pituitrophin PMG</option>
										<option value="Prostate PMG"<%= document.get("supplements_phaseII").indexOf("Prostate PMG|") > -1 ? " selected" : "" %>>Prostate PMG</option>
										<option value="Renatrophin PMG"<%= document.get("supplements_phaseII").indexOf("Renatrophin PMG|") > -1 ? " selected" : "" %>>Renatrophin PMG</option>
										<option value="Thymus PMG"<%= document.get("supplements_phaseII").indexOf("Thymus PMG|") > -1 ? " selected" : "" %>>Thymus PMG</option>
										<option value="Thytrophin PMG"<%= document.get("supplements_phaseII").indexOf("Thytrophin PM|") > -1 ? " selected" : "" %>>Thytrophin PMG</option>
										<option value="Utrophin PMG"<%= document.get("supplements_phaseII").indexOf("Utrophin PM|") > -1 ? " selected" : "" %>>Utrophin PMG</option>
										<option value="A-C Carbamide"<%= document.get("supplements_phaseII").indexOf("A-C Carbamide|") > -1 ? " selected" : "" %>>A-C Carbamide</option>
										<option value="FlavoC"<%= document.get("supplements_phaseII").indexOf("FlavoC|") > -1 ? " selected" : "" %>>FlavoC</option>
										<option value="Cat A"<%= document.get("supplements_phaseII").indexOf("Cat A|") > -1 ? " selected" : "" %>>Cat A</option>
										<option value="Cat A-C"<%= document.get("supplements_phaseII").indexOf("Cat A-C|") > -1 ? " selected" : "" %>>Cat A-C</option>
										<option value="Cat A-C-P"<%= document.get("supplements_phaseII").indexOf("Cat A-C-P|") > -1 ? " selected" : "" %>>Cat A-C-P</option>
										<option value="Cat B"<%= document.get("supplements_phaseII").indexOf("Cat B|") > -1 ? " selected" : "" %>>Cat B</option>
										<option value="Cat C"<%= document.get("supplements_phaseII").indexOf("Cat C|") > -1 ? " selected" : "" %>>Cat C</option>
										<option value="Cat D"<%= document.get("supplements_phaseII").indexOf("Cat D|") > -1 ? " selected" : "" %>>Cat D</option>
										<option value="Cat E"<%= document.get("supplements_phaseII").indexOf("Cat E|") > -1 ? " selected" : "" %>>Cat E</option>
										<option value="Cataplex F"<%= document.get("supplements_phaseII").indexOf("Cataplex F|") > -1 ? " selected" : "" %>>Cat F</option>
										<option value="Cataplex G"<%= document.get("supplements_phaseII").indexOf("Cataplex G|") > -1 ? " selected" : "" %>>Cataplex G</option>
										<option value="CalMag"<%= document.get("supplements_phaseII").indexOf("CalMag|") > -1 ? " selected" : "" %>>CalMag</option>
										<option value="Cal Lact Powder"<%= document.get("supplements_phaseII").indexOf("Cal Lact Powder|") > -1 ? " selected" : "" %>>Cal Lact Powder</option>
										<option value="Magnesium Lactate"<%= document.get("supplements_phaseII").indexOf("Magnesium Lactate|") > -1 ? " selected" : "" %>>Magnesium Lactate</option>
										<option value="Manganese B12"<%= document.get("supplements_phaseII").indexOf("Manganese B12|") > -1 ? " selected" : "" %>>Manganese B12</option>
										<option value="Liquid Herbs"<%= document.get("supplements_phaseII").indexOf("Liquid Herbs|") > -1 ? " selected" : "" %>>Liquid Herbs</option>
										<option value="CardioPlus"<%= document.get("supplements_phaseII").indexOf("CardioPlus|") > -1 ? " selected" : "" %>>CardioPlus</option>
										<option value="Adrenal Des"<%= document.get("supplements_phaseII").indexOf("Adrenal Des|") > -1 ? " selected" : "" %>>Adrenal Des</option>
										<option value="Spleen Des"<%= document.get("supplements_phaseII").indexOf("Spleen Des|") > -1 ? " selected" : "" %>>Spleen Des</option>
										<option value="Drenamin"<%= document.get("supplements_phaseII").indexOf("Drenamin|") > -1 ? " selected" : "" %>>Drenamin</option>
										<option value="Fish Oil"<%= document.get("supplements_phaseII").indexOf("Fish Oil|") > -1 ? " selected" : "" %>>Fish Oil</option>
										<option value="OBM"<%= document.get("supplements_phaseII").indexOf("OBM|") > -1 ? " selected" : "" %>>OBM</option>
										<option value="Prolamine Iodine"<%= document.get("supplements_phaseII").indexOf("Prolamine Iodine|") > -1 ? " selected" : "" %>>Prolamine Iodine</option>
										<option value="Parotid"<%= document.get("supplements_phaseII").indexOf("Parotid|") > -1 ? " selected" : "" %>>Parotid</option>
										<option value="Cholacol II"<%= document.get("supplements_phaseII").indexOf("Cholacol II|") > -1 ? " selected" : "" %>>Cholacol II</option>
										<option value="Cholaplex"<%= document.get("supplements_phaseII").indexOf("Cholaplex|") > -1 ? " selected" : "" %>>Cholaplex</option>
										<option value="Zinc Liver Chelate"<%= document.get("supplements_phaseII").indexOf("Zinc Liver Chelate|") > -1 ? " selected" : "" %>>Zinc Liver Chelate</option>
										<option value="Protefood"<%= document.get("supplements_phaseII").indexOf("Protefood|") > -1 ? " selected" : "" %>>Protefood</option>
										<option value="MolyCu"<%= document.get("supplements_phaseII").indexOf("MolyCu|") > -1 ? " selected" : "" %>>MolyCu</option>
										<option value="GB3"<%= document.get("supplements_phaseII").indexOf("GB3|") > -1 ? " selected" : "" %>>GB3</option>
										<option value="EndoPan"<%= document.get("supplements_phaseII").indexOf("EndoPan|") > -1 ? " selected" : "" %>>EndoPan</option>
										<option value="Flaxseed oil"<%= document.get("supplements_phaseII").indexOf("Flaxseed oil|") > -1 ? " selected" : "" %>>Flaxseed oil</option>
										<option value="Arginex"<%= document.get("supplements_phaseII").indexOf("Arginex|") > -1 ? " selected" : "" %>>Arginex</option>
										<option value="Rehmannia (liq)"<%= document.get("supplements_phaseII").indexOf("Rehmannia (liq)|") > -1 ? " selected" : "" %>>Rehmannia (liq)</option>
										<option value="Saligesic"<%= document.get("supplements_phaseII").indexOf("Saligesic|") > -1 ? " selected" : "" %>>Saligesic</option>
										<option value="Vitanox"<%= document.get("supplements_phaseII").indexOf("Vitanox|") > -1 ? " selected" : "" %>>Vitanox</option>
										<option value="St. John's Wort"<%= document.get("supplements_phaseII").indexOf("St. John's Wort|") > -1 ? " selected" : "" %>>St. John's Wort</option>
										<option value="California Poppy"<%= document.get("supplements_phaseII").indexOf("California Poppy|") > -1 ? " selected" : "" %>>California Poppy</option>
										<option value="Broncafect"<%= document.get("supplements_phaseII").indexOf("Broncafect|") > -1 ? " selected" : "" %>>Broncafect</option>
										<option value="Min-Tran"<%= document.get("supplements_phaseII").indexOf("Min-Tran|") > -1 ? " selected" : "" %>>Min-Tran</option>
										<option value="Min-Chex"<%= document.get("supplements_phaseII").indexOf("Min-Chex|") > -1 ? " selected" : "" %>>Min-Chex</option>
										<option value="Progon B"<%= document.get("supplements_phaseII").indexOf("Progon B|") > -1 ? " selected" : "" %>>Progon B</option>
										<option value="Tribulus"<%= document.get("supplements_phaseII").indexOf("Tribulus|") > -1 ? " selected" : "" %>>Tribulus</option>
										<option value="Wheat Germ oil"<%= document.get("supplements_phaseII").indexOf("Wheat Germ oil|") > -1 ? " selected" : "" %>>Wheat Germ oil</option>
										<option value="Symplex F"<%= document.get("supplements_phaseII").indexOf("Symplex F|") > -1 ? " selected" : "" %>>Symplex F</option>
										<option value="Glucosamine Synergy"<%= document.get("supplements_phaseII").indexOf("Glucosamine Synergy|") > -1 ? " selected" : "" %>>Glucosamine Synergy</option>
										<option value="Ligaplex II"<%= document.get("supplements_phaseII").indexOf("Ligaplex II|") > -1 ? " selected" : "" %>>Ligaplex II</option>
										<option value="Collagen C"<%= document.get("supplements_phaseII").indexOf("Collagen C|") > -1 ? " selected" : "" %>>Collagen C</option>
										<option value="Symplex M"<%= document.get("supplements_phaseII").indexOf("Symplex M|") > -1 ? " selected" : "" %>>Symplex M</option>
										<option value="Male Support"<%= document.get("supplements_phaseII").indexOf("Male Support|") > -1 ? " selected" : "" %>>Male Support</option>
										<option value="Orchex"<%= document.get("supplements_phaseII").indexOf("Orchex|") > -1 ? " selected" : "" %>>Orchex</option>
										<option value="Lipoic Acid"<%= document.get("supplements_phaseII").indexOf("Lipoic Acid|") > -1 ? " selected" : "" %>>Lipoic Acid</option>
										<option value="Ambrotose"<%= document.get("supplements_phaseII").indexOf("Ambrotose|") > -1 ? " selected" : "" %>>Ambrotose</option>
									</select>
									</td>
									<td>
									<select name="detox_phaseII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Liver/GB Flush"<%= document.get("detox_phaseII").indexOf("Liver/GB Flush|") > -1 ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_phaseII").indexOf("SP Cleanse|") > -1 ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_phaseII").indexOf("Colon Cleanse|") > -1 ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_phaseII").indexOf("Juice Fast|") > -1 ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_phaseII").indexOf("Blood Cleanse|") > -1 ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_phaseII").indexOf("Homeopathic|") > -1 ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_phaseII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Walk Daily 30 min"<%= document.get("exercise_phaseII").indexOf("Walk Daily 30 min|") > -1 ? " selected" : "" %>>Walk Daily 30 min</option>
										<option value="Walk Daily 40-60 min"<%= document.get("exercise_phaseII").indexOf("Walk Daily 40-60 min|") > -1 ? " selected" : "" %>>Walk Daily 40-60 min</option>
										<option value="Walk/Strength Train"<%= document.get("exercise_phaseII").indexOf("Walk/Strength Train|") > -1 ? " selected" : "" %>>Walk/Strength Train</option>
										<option value="Cardio 30-60 min"<%= document.get("exercise_phaseII").indexOf("Cardio 30-60 min|") > -1 ? " selected" : "" %>>Cardio 30-60 min</option>
										<option value="Stretching"<%= document.get("exercise_phaseII").indexOf("Stretching|") > -1 ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_phaseII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="No sugar/fruit"<%= document.get("diet_phaseII").indexOf("No sugar/fruit|") > -1 ? " selected" : "" %>>No sugar/fruit</option>
										<option value="Protein Shakes"<%= document.get("diet_phaseII").indexOf("Protein Shakes|") > -1 ? " selected" : "" %>>Protein Shakes</option>
										<option value="Protein Shakes w/greens"<%= document.get("diet_phaseII").indexOf("Protein Shakes w/greens|") > -1 ? " selected" : "" %>>Protein Shakes w/greens</option>
										<option value="Protein Shakes w/flax oil"<%= document.get("diet_phaseII").indexOf("Protein Shakes w/flax oil|") > -1 ? " selected" : "" %>>Protein Shakes w/flax oil</option>
										<option value="Flax seed oil"<%= document.get("diet_phaseII").indexOf("Flax seed oil|") > -1 ? " selected" : "" %>>Flax seed oil</option>
										<option value="Garlic"<%= document.get("diet_phaseII").indexOf("Garlic|") > -1 ? " selected" : "" %>>Garlic</option>
										<option value="Water Testing"<%= document.get("diet_phaseII").indexOf("Water Testing|") > -1 ? " selected" : "" %>>Water Testing</option>
									</select>
									</td>
									<td>
									<select name="stress_phaseII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Emotional"<%= document.get("stress_phaseII").indexOf("Emotional|") > -1 ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_phaseII").indexOf("Mental|") > -1 ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_phaseII").indexOf("Physical|") > -1 ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_phaseII").indexOf("Spiritual|") > -1 ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_phaseII").indexOf("Toxins|") > -1 ? " selected" : "" %>>Toxins</option>
										<option value="Lead"<%= document.get("stress_phaseII").indexOf("Lead|") > -1 ? " selected" : "" %>>Lead</option>
										<option value="Mercury"<%= document.get("stress_phaseII").indexOf("Mercury|") > -1 ? " selected" : "" %>>Mercury</option>
										<option value="Cadmium"<%= document.get("stress_phaseII").indexOf("Cadmium|") > -1 ? " selected" : "" %>>Cadmium</option>
										<option value="Arsenic"<%= document.get("stress_phaseII").indexOf("Arsenic|") > -1 ? " selected" : "" %>>Arsenic</option>
										<option value="Aluminium"<%= document.get("stress_phaseII").indexOf("Aluminium|") > -1 ? " selected" : "" %>>Aluminium</option>
										<option value="Copper"<%= document.get("stress_phaseII").indexOf("Copper|") > -1 ? " selected" : "" %>>Copper</option>
										<option value="Nickel"<%= document.get("stress_phaseII").indexOf("Nickel|") > -1 ? " selected" : "" %>>Nickel</option>
										<option value="Zinc"<%= document.get("stress_phaseII").indexOf("Zinc|") > -1 ? " selected" : "" %>>Zinc</option>
										<option value="Cobalt"<%= document.get("stress_phaseII").indexOf("Cobalt|") > -1 ? " selected" : "" %>>Cobalt</option>
										<option value="Manganese"<%= document.get("stress_phaseII").indexOf("Manganese|") > -1 ? " selected" : "" %>>Manganese</option>
										<option value="Biochemical"<%= document.get("stress_phaseII").indexOf("Biochemical|") > -1 ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_phaseII").indexOf("Energetic (EMF)|") > -1 ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
								    </table>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">PHASE III DATE</div>
								<div class="right">
									<input name="dateInputPhaseIII" onfocus="getDate('userMineralRatiosForm','dateInputPhaseIII');select();" value="<%= document.getPhaseIIIDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">
								    <table>
									<tr>
									<td>
									<div >Supplements</div>
									</td>
									<td>
									<div >Detox</div>
									</td>
									<td>
									<div >Exercise</div>
									</td>
									<td>
									<div >Diet</div>
									</td>
									<td>
									<div >Stress</div>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_header_phaseIII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("supplements_header_phaseIII").equals("") ? " selected" : "" %>></option>
										<option value="Digestive Support"<%= document.get("supplements_header_phaseIII").equals("Digestive Support") ? " selected" : "" %>>Digestive Support</option>
										<option value="Adrenal/Thyroid Support"<%= document.get("supplements_header_phaseIII").equals("Adrenal/Thyroid Support") ? " selected" : "" %>>Adrenal/Thyroid Support</option>
										<option value="Thyroid/Adrenal Support"<%= document.get("supplements_header_phaseIII").equals("Thyroid/Adrenal Support") ? " selected" : "" %>>Thyroid/Adrenal Support</option>
										<option value="Adrenal Support"<%= document.get("supplements_header_phaseIII").equals("Adrenal Support") ? " selected" : "" %>>Adrenal Support</option>
										<option value="Thyroid Support"<%= document.get("supplements_header_phaseIII").equals("Thyroid Support") ? " selected" : "" %>>Thyroid Support</option>
										<option value="Heavy Metal Detox"<%= document.get("supplements_header_phaseIII").equals("Heavy Metal Detox") ? " selected" : "" %>>Heavy Metal Detox</option>
										<option value="Copper Toxicity"<%= document.get("supplements_header_phaseIII").equals("Copper Toxicity") ? " selected" : "" %>>Copper Toxicity</option>
										<option value="Copper Detox/Heavy Metals"<%= document.get("supplements_header_phaseIII").equals("Copper Detox/Heavy Metals") ? " selected" : "" %>>Copper Detox/Heavy Metals</option>
										<option value="Maintenance Supplementation"<%= document.get("supplements_header_phaseIII").equals("Maintenance Supplementation") ? " selected" : "" %>>Maintenance Supplementation</option>
									</select>
									</td>
									<td>
									<select name="detox_header_phaseIII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("detox_header_phaseIII").equals("") ? " selected" : "" %>></option>
										<option value="Liver/GB Flush"<%= document.get("detox_header_phaseIII").equals("Liver/GB Flush") ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_header_phaseIII").equals("SP Cleanse") ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_header_phaseIII").equals("Colon Cleanse") ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_header_phaseIII").equals("Juice Fast") ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_header_phaseIII").equals("Blood Cleanse") ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_header_phaseIII").equals("Homeopathic") ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_header_phaseIII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("exercise_header_phaseIII").equals("") ? " selected" : "" %>></option>
										<option value="Walking Daily 30 minutes"<%= document.get("exercise_header_phaseIII").equals("Walking Daily 30 minutes") ? " selected" : "" %>>Walking Daily 30 minutes</option>
										<option value="Walking Daily 40-60 minutes"<%= document.get("exercise_header_phaseIII").equals("Walking Daily 40-60 minutes") ? " selected" : "" %>>Walking Daily 40-60 minutes</option>
										<option value="Walking & Strength Training"<%= document.get("exercise_header_phaseIII").equals("Walking & Strength Training") ? " selected" : "" %>>Walking & Strength Training</option>
										<option value="Cardio 30-60 minutes"<%= document.get("exercise_header_phaseIII").equals("Cardio 30-60 minutes") ? " selected" : "" %>>Cardio 30-60 minutes</option>
										<option value="Stretching"<%= document.get("exercise_header_phaseIII").equals("Stretching") ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_header_phaseIII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("diet_header_phaseIII").equals("") ? " selected" : "" %>></option>
										<option value="Nutrition Plan"<%= document.get("diet_header_phaseIII").equals("Nutrition Plan") ? " selected" : "" %>>Nutrition Plan</option>
									</select>
									</td>
									<td>
									<select name="stress_header_phaseIII" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("stress_header_phaseIII").equals("") ? " selected" : "" %>></option>
										<option value="Emotional"<%= document.get("stress_header_phaseIII").equals("Emotional") ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_header_phaseIII").equals("Mental") ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_header_phaseIII").equals("Physical") ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_header_phaseIII").equals("Spiritual") ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_header_phaseIII").equals("Toxins") ? " selected" : "" %>>Toxins</option>
										<option value="Toxin (Mercury)"<%= document.get("stress_header_phaseIII").equals("Toxin (Mercury)") ? " selected" : "" %>>Toxin (Mercury)</option>
										<option value="Toxin (Copper)"<%= document.get("stress_header_phaseIII").equals("Toxin (Copper)") ? " selected" : "" %>>Toxin (Copper)</option>
										<option value="Toxin (Cadmium)"<%= document.get("stress_header_phaseIII").equals("Toxin (Cadmium)") ? " selected" : "" %>>Toxin (Cadmium)</option>
										<option value="Toxin (Aluminium)"<%= document.get("stress_header_phaseIII").equals("Toxin (Aluminium)") ? " selected" : "" %>>Toxin (Aluminium)</option>
										<option value="Toxin (Nickel)"<%= document.get("stress_header_phaseIII").equals("Toxin (Nickel)") ? " selected" : "" %>>Toxin (Nickel)</option>
										<option value="Toxin (Cobalt)"<%= document.get("stress_header_phaseIII").equals("Toxin (Cobalt)") ? " selected" : "" %>>Toxin (Cobalt)</option>
										<option value="Toxin (Manganese)"<%= document.get("stress_header_phaseIII").equals("Toxin (Manganese)") ? " selected" : "" %>>Toxin (Manganese)</option>
										<option value="Toxin (Lead)"<%= document.get("stress_header_phaseIII").equals("Toxin (Lead)") ? " selected" : "" %>>Toxin (Lead)</option>
										<option value="Biochemical"<%= document.get("stress_header_phaseIII").equals("Biochemical") ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_header_phaseIII").equals("Energetic (EMF)") ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_phaseIII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Multizyme"<%= document.get("supplements_phaseIII").indexOf("Multizyme|") > -1 ? " selected" : "" %>>Multizyme</option>
										<option value="Sp Bk Radish"<%= document.get("supplements_phaseIII").indexOf("Sp Bk Radish|") > -1 ? " selected" : "" %>>Sp Bk Radish</option>
										<option value="Zypan"<%= document.get("supplements_phaseIII").indexOf("Zypan|") > -1 ? " selected" : "" %>>Zypan</option>
										<option value="Pancreatrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Pancreatrophin PMG|") > -1 ? " selected" : "" %>>Pancreatrophin PMG</option>
										<option value="Fungal Forte"<%= document.get("supplements_phaseIII").indexOf("Fungal Forte|") > -1 ? " selected" : "" %>>Fungal Forte</option>
										<option value="Multi-Probiotic"<%= document.get("supplements_phaseIII").indexOf("Multi-Probiotic|") > -1 ? " selected" : "" %>>Multi-Probiotic</option>
										<option value="Cholacol"<%= document.get("supplements_phaseIII").indexOf("Cholacol|") > -1 ? " selected" : "" %>>Cholacol</option>
										<option value="Theralac"<%= document.get("supplements_phaseIII").indexOf("Theralac|") > -1 ? " selected" : "" %>>Theralac</option>
										<option value="Lactic Acid Yeast waf"<%= document.get("supplements_phaseIII").indexOf("Lactic Acid Yeast waf|") > -1 ? " selected" : "" %>>Lactic Acid Yeast waf</option>
										<option value="DiGest (liq)"<%= document.get("supplements_phaseIII").indexOf("DiGest (liq)|") > -1 ? " selected" : "" %>>DiGest (liq)</option>
										<option value="DiGest (caps)"<%= document.get("supplements_phaseIII").indexOf("DiGest (caps)|") > -1 ? " selected" : "" %>>DiGest (caps)</option>
										<option value="Zymex"<%= document.get("supplements_phaseIII").indexOf("Zymex|") > -1 ? " selected" : "" %>>Zymex</option>
										<option value="Zymex waf"<%= document.get("supplements_phaseIII").indexOf("Zymex waf|") > -1 ? " selected" : "" %>>Zymex waf</option>
										<option value="Lact-Enz"<%= document.get("supplements_phaseIII").indexOf("Lact-Enz|") > -1 ? " selected" : "" %>>Lact-Enz</option>
										<option value="A-F Betafood"<%= document.get("supplements_phaseIII").indexOf("A-F Betafood|") > -1 ? " selected" : "" %>>A-F Betafood</option>
										<option value="LivCo"<%= document.get("supplements_phaseIII").indexOf("LivCo|") > -1 ? " selected" : "" %>>LivCo</option>
										<option value="DigestPlex (GF)"<%= document.get("supplements_phaseIII").indexOf("DigestPlex (GF|") > -1 ? " selected" : "" %>>DigestPlex (GF)</option>
										<option value="Protein Powder"<%= document.get("supplements_phaseIII").indexOf("Protein Powder|") > -1 ? " selected" : "" %>>Protein Powder</option>
										<option value="Greens"<%= document.get("supplements_phaseIII").indexOf("Greens|") > -1 ? " selected" : "" %>>Greens</option>
										<option value="Cataplex GTF"<%= document.get("supplements_phaseIII").indexOf("Cataplex GTF|") > -1 ? " selected" : "" %>>Cataplex GTF</option>
										<option value="Gymnema"<%= document.get("supplements_phaseIII").indexOf("Gymnema|") > -1 ? " selected" : "" %>>Gymnema</option>
										<option value="Paraplex"<%= document.get("supplements_phaseIII").indexOf("Paraplex|") > -1 ? " selected" : "" %>>Paraplex</option>
										<option value="Inositol"<%= document.get("supplements_phaseIII").indexOf("Inositol|") > -1 ? " selected" : "" %>>Inositol</option>
										<option value="Detox Formula (GF)"<%= document.get("supplements_phaseIII").indexOf("Detox Formula (GF)|") > -1 ? " selected" : "" %>>Detox Formula (GF)</option>
										<option value="PhytoGreens (GF)"<%= document.get("supplements_phaseIII").indexOf("PhytoGreens (GF)|") > -1 ? " selected" : "" %>>PhytoGreens (GF)</option>
										<option value="VitaLiv"<%= document.get("supplements_phaseIII").indexOf("VitaLiv|") > -1 ? " selected" : "" %>>VitaLiv</option>
										<option value="BFood"<%= document.get("supplements_phaseIII").indexOf("BFood|") > -1 ? " selected" : "" %>>BFood</option>
										<option value="Primal Defense"<%= document.get("supplements_phaseIII").indexOf("Primal Defense|") > -1 ? " selected" : "" %>>Primal Defense</option>
										<option value="Chewable Catalyn"<%= document.get("supplements_phaseIII").indexOf("Chewable Catalyn|") > -1 ? " selected" : "" %>>Chewable Catalyn</option>
										<option value="Ferrofood"<%= document.get("supplements_phaseIII").indexOf("Ferrofood|") > -1 ? " selected" : "" %>>Ferrofood</option>
										<option value="Catalyn"<%= document.get("supplements_phaseIII").indexOf("Catalyn|") > -1 ? " selected" : "" %>>Catalyn</option>
										<option value="Boswellia Complex"<%= document.get("supplements_phaseIII").indexOf("Boswellia Complex|") > -1 ? " selected" : "" %>>Boswellia Complex</option>
										<option value="Cranberry Complex"<%= document.get("supplements_phaseIII").indexOf("Cranberry Complex|") > -1 ? " selected" : "" %>>Cranberry Complex</option>
										<option value="Rehmannia Complex"<%= document.get("supplements_phaseIII").indexOf("Rehmannia Complex|") > -1 ? " selected" : "" %>>Rehmannia Complex</option>
										<option value="Withania Complex"<%= document.get("supplements_phaseIII").indexOf("Withania Complex|") > -1 ? " selected" : "" %>>Withania Complex</option>
										<option value="Thyroid Complex"<%= document.get("supplements_phaseIII").indexOf("Thyroid Complex|") > -1 ? " selected" : "" %>>Thyroid Complex</option>
										<option value="Congaplex"<%= document.get("supplements_phaseIII").indexOf("Congaplex|") > -1 ? " selected" : "" %>>Congaplex</option>
										<option value="Cardiotrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Cardiotrophin PMG|") > -1 ? " selected" : "" %>>Cardiotrophin PMG</option>
										<option value="Drenatrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Drenatrophin PMG|") > -1 ? " selected" : "" %>>Drenatrophin PMG</option>
										<option value="Hypothalamus PMG"<%= document.get("supplements_phaseIII").indexOf("Hypothalamus PMG|") > -1 ? " selected" : "" %>>Hypothalamus PMG</option>
										<option value="Mammary PMG"<%= document.get("supplements_phaseIII").indexOf("Mammary PMG|") > -1 ? " selected" : "" %>>Mammary PMG</option>
										<option value="Myotrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Myotrophin PMG|") > -1 ? " selected" : "" %>>Myotrophin PMG</option>
										<option value="Neurotrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Neurotrophin PM|") > -1 ? " selected" : "" %>>Neurotrophin PMG</option>
										<option value="Ostrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Ostrophin PMG|") > -1 ? " selected" : "" %>>Ostrophin PMG</option>
										<option value="Ovatrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Ovatrophin PMG|") > -1 ? " selected" : "" %>>Ovatrophin PMG</option>
										<option value="Pneumotrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Pneumotrophin PMG|") > -1 ? " selected" : "" %>>Pneumotrophin PMG</option>
										<option value="Pituitrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Pituitrophin PMG|") > -1 ? " selected" : "" %>>Pituitrophin PMG</option>
										<option value="Prostate PMG"<%= document.get("supplements_phaseIII").indexOf("Prostate PMG|") > -1 ? " selected" : "" %>>Prostate PMG</option>
										<option value="Renatrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Renatrophin PMG|") > -1 ? " selected" : "" %>>Renatrophin PMG</option>
										<option value="Thymus PMG"<%= document.get("supplements_phaseIII").indexOf("Thymus PMG|") > -1 ? " selected" : "" %>>Thymus PMG</option>
										<option value="Thytrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Thytrophin PM|") > -1 ? " selected" : "" %>>Thytrophin PMG</option>
										<option value="Utrophin PMG"<%= document.get("supplements_phaseIII").indexOf("Utrophin PM|") > -1 ? " selected" : "" %>>Utrophin PMG</option>
										<option value="A-C Carbamide"<%= document.get("supplements_phaseIII").indexOf("A-C Carbamide|") > -1 ? " selected" : "" %>>A-C Carbamide</option>
										<option value="FlavoC"<%= document.get("supplements_phaseIII").indexOf("FlavoC|") > -1 ? " selected" : "" %>>FlavoC</option>
										<option value="Cat A"<%= document.get("supplements_phaseIII").indexOf("Cat A|") > -1 ? " selected" : "" %>>Cat A</option>
										<option value="Cat A-C"<%= document.get("supplements_phaseIII").indexOf("Cat A-C|") > -1 ? " selected" : "" %>>Cat A-C</option>
										<option value="Cat A-C-P"<%= document.get("supplements_phaseIII").indexOf("Cat A-C-P|") > -1 ? " selected" : "" %>>Cat A-C-P</option>
										<option value="Cat B"<%= document.get("supplements_phaseIII").indexOf("Cat B|") > -1 ? " selected" : "" %>>Cat B</option>
										<option value="Cat C"<%= document.get("supplements_phaseIII").indexOf("Cat C|") > -1 ? " selected" : "" %>>Cat C</option>
										<option value="Cat D"<%= document.get("supplements_phaseIII").indexOf("Cat D|") > -1 ? " selected" : "" %>>Cat D</option>
										<option value="Cat E"<%= document.get("supplements_phaseIII").indexOf("Cat E|") > -1 ? " selected" : "" %>>Cat E</option>
										<option value="Cataplex F"<%= document.get("supplements_phaseIII").indexOf("Cataplex F|") > -1 ? " selected" : "" %>>Cat F</option>
										<option value="Cataplex G"<%= document.get("supplements_phaseIII").indexOf("Cataplex G|") > -1 ? " selected" : "" %>>Cataplex G</option>
										<option value="CalMag"<%= document.get("supplements_phaseIII").indexOf("CalMag|") > -1 ? " selected" : "" %>>CalMag</option>
										<option value="Cal Lact Powder"<%= document.get("supplements_phaseIII").indexOf("Cal Lact Powder|") > -1 ? " selected" : "" %>>Cal Lact Powder</option>
										<option value="Magnesium Lactate"<%= document.get("supplements_phaseIII").indexOf("Magnesium Lactate|") > -1 ? " selected" : "" %>>Magnesium Lactate</option>
										<option value="Manganese B12"<%= document.get("supplements_phaseIII").indexOf("Manganese B12|") > -1 ? " selected" : "" %>>Manganese B12</option>
										<option value="Liquid Herbs"<%= document.get("supplements_phaseIII").indexOf("Liquid Herbs|") > -1 ? " selected" : "" %>>Liquid Herbs</option>
										<option value="CardioPlus"<%= document.get("supplements_phaseIII").indexOf("CardioPlus|") > -1 ? " selected" : "" %>>CardioPlus</option>
										<option value="Adrenal Des"<%= document.get("supplements_phaseIII").indexOf("Adrenal Des|") > -1 ? " selected" : "" %>>Adrenal Des</option>
										<option value="Spleen Des"<%= document.get("supplements_phaseIII").indexOf("Spleen Des|") > -1 ? " selected" : "" %>>Spleen Des</option>
										<option value="Drenamin"<%= document.get("supplements_phaseIII").indexOf("Drenamin|") > -1 ? " selected" : "" %>>Drenamin</option>
										<option value="Fish Oil"<%= document.get("supplements_phaseIII").indexOf("Fish Oil|") > -1 ? " selected" : "" %>>Fish Oil</option>
										<option value="OBM"<%= document.get("supplements_phaseIII").indexOf("OBM|") > -1 ? " selected" : "" %>>OBM</option>
										<option value="Prolamine Iodine"<%= document.get("supplements_phaseIII").indexOf("Prolamine Iodine|") > -1 ? " selected" : "" %>>Prolamine Iodine</option>
										<option value="Parotid"<%= document.get("supplements_phaseIII").indexOf("Parotid|") > -1 ? " selected" : "" %>>Parotid</option>
										<option value="Cholacol II"<%= document.get("supplements_phaseIII").indexOf("Cholacol II|") > -1 ? " selected" : "" %>>Cholacol II</option>
										<option value="Cholaplex"<%= document.get("supplements_phaseIII").indexOf("Cholaplex|") > -1 ? " selected" : "" %>>Cholaplex</option>
										<option value="Zinc Liver Chelate"<%= document.get("supplements_phaseIII").indexOf("Zinc Liver Chelate|") > -1 ? " selected" : "" %>>Zinc Liver Chelate</option>
										<option value="Protefood"<%= document.get("supplements_phaseIII").indexOf("Protefood|") > -1 ? " selected" : "" %>>Protefood</option>
										<option value="MolyCu"<%= document.get("supplements_phaseIII").indexOf("MolyCu|") > -1 ? " selected" : "" %>>MolyCu</option>
										<option value="GB3"<%= document.get("supplements_phaseIII").indexOf("GB3|") > -1 ? " selected" : "" %>>GB3</option>
										<option value="EndoPan"<%= document.get("supplements_phaseIII").indexOf("EndoPan|") > -1 ? " selected" : "" %>>EndoPan</option>
										<option value="Flaxseed oil"<%= document.get("supplements_phaseIII").indexOf("Flaxseed oil|") > -1 ? " selected" : "" %>>Flaxseed oil</option>
										<option value="Arginex"<%= document.get("supplements_phaseIII").indexOf("Arginex|") > -1 ? " selected" : "" %>>Arginex</option>
										<option value="Rehmannia (liq)"<%= document.get("supplements_phaseIII").indexOf("Rehmannia (liq)|") > -1 ? " selected" : "" %>>Rehmannia (liq)</option>
										<option value="Saligesic"<%= document.get("supplements_phaseIII").indexOf("Saligesic|") > -1 ? " selected" : "" %>>Saligesic</option>
										<option value="Vitanox"<%= document.get("supplements_phaseIII").indexOf("Vitanox|") > -1 ? " selected" : "" %>>Vitanox</option>
										<option value="St. John's Wort"<%= document.get("supplements_phaseIII").indexOf("St. John's Wort|") > -1 ? " selected" : "" %>>St. John's Wort</option>
										<option value="California Poppy"<%= document.get("supplements_phaseIII").indexOf("California Poppy|") > -1 ? " selected" : "" %>>California Poppy</option>
										<option value="Broncafect"<%= document.get("supplements_phaseIII").indexOf("Broncafect|") > -1 ? " selected" : "" %>>Broncafect</option>
										<option value="Min-Tran"<%= document.get("supplements_phaseIII").indexOf("Min-Tran|") > -1 ? " selected" : "" %>>Min-Tran</option>
										<option value="Min-Chex"<%= document.get("supplements_phaseIII").indexOf("Min-Chex|") > -1 ? " selected" : "" %>>Min-Chex</option>
										<option value="Progon B"<%= document.get("supplements_phaseIII").indexOf("Progon B|") > -1 ? " selected" : "" %>>Progon B</option>
										<option value="Tribulus"<%= document.get("supplements_phaseIII").indexOf("Tribulus|") > -1 ? " selected" : "" %>>Tribulus</option>
										<option value="Wheat Germ oil"<%= document.get("supplements_phaseIII").indexOf("Wheat Germ oil|") > -1 ? " selected" : "" %>>Wheat Germ oil</option>
										<option value="Symplex F"<%= document.get("supplements_phaseIII").indexOf("Symplex F|") > -1 ? " selected" : "" %>>Symplex F</option>
										<option value="Glucosamine Synergy"<%= document.get("supplements_phaseIII").indexOf("Glucosamine Synergy|") > -1 ? " selected" : "" %>>Glucosamine Synergy</option>
										<option value="Ligaplex II"<%= document.get("supplements_phaseIII").indexOf("Ligaplex II|") > -1 ? " selected" : "" %>>Ligaplex II</option>
										<option value="Collagen C"<%= document.get("supplements_phaseIII").indexOf("Collagen C|") > -1 ? " selected" : "" %>>Collagen C</option>
										<option value="Symplex M"<%= document.get("supplements_phaseIII").indexOf("Symplex M|") > -1 ? " selected" : "" %>>Symplex M</option>
										<option value="Male Support"<%= document.get("supplements_phaseIII").indexOf("Male Support|") > -1 ? " selected" : "" %>>Male Support</option>
										<option value="Orchex"<%= document.get("supplements_phaseIII").indexOf("Orchex|") > -1 ? " selected" : "" %>>Orchex</option>
										<option value="Lipoic Acid"<%= document.get("supplements_phaseIII").indexOf("Lipoic Acid|") > -1 ? " selected" : "" %>>Lipoic Acid</option>
										<option value="Ambrotose"<%= document.get("supplements_phaseIII").indexOf("Ambrotose|") > -1 ? " selected" : "" %>>Ambrotose</option>
									</select>
									</td>
									<td>
									<select name="detox_phaseIII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Liver/GB Flush"<%= document.get("detox_phaseIII").indexOf("Liver/GB Flush|") > -1 ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_phaseIII").indexOf("SP Cleanse|") > -1 ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_phaseIII").indexOf("Colon Cleanse|") > -1 ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_phaseIII").indexOf("Juice Fast|") > -1 ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_phaseIII").indexOf("Blood Cleanse|") > -1 ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_phaseIII").indexOf("Homeopathic|") > -1 ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_phaseIII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Walk Daily 30 min"<%= document.get("exercise_phaseIII").indexOf("Walk Daily 30 min|") > -1 ? " selected" : "" %>>Walk Daily 30 min</option>
										<option value="Walk Daily 40-60 min"<%= document.get("exercise_phaseIII").indexOf("Walk Daily 40-60 min|") > -1 ? " selected" : "" %>>Walk Daily 40-60 min</option>
										<option value="Walk/Strength Train"<%= document.get("exercise_phaseIII").indexOf("Walk/Strength Train|") > -1 ? " selected" : "" %>>Walk/Strength Train</option>
										<option value="Cardio 30-60 min"<%= document.get("exercise_phaseIII").indexOf("Cardio 30-60 min|") > -1 ? " selected" : "" %>>Cardio 30-60 min</option>
										<option value="Stretching"<%= document.get("exercise_phaseIII").indexOf("Stretching|") > -1 ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_phaseIII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="No sugar/fruit"<%= document.get("diet_phaseIII").indexOf("No sugar/fruit|") > -1 ? " selected" : "" %>>No sugar/fruit</option>
										<option value="Protein Shakes"<%= document.get("diet_phaseIII").indexOf("Protein Shakes|") > -1 ? " selected" : "" %>>Protein Shakes</option>
										<option value="Protein Shakes w/greens"<%= document.get("diet_phaseIII").indexOf("Protein Shakes w/greens|") > -1 ? " selected" : "" %>>Protein Shakes w/greens</option>
										<option value="Protein Shakes w/flax oil"<%= document.get("diet_phaseIII").indexOf("Protein Shakes w/flax oil|") > -1 ? " selected" : "" %>>Protein Shakes w/flax oil</option>
										<option value="Flax seed oil"<%= document.get("diet_phaseIII").indexOf("Flax seed oil|") > -1 ? " selected" : "" %>>Flax seed oil</option>
										<option value="Garlic"<%= document.get("diet_phaseIII").indexOf("Garlic|") > -1 ? " selected" : "" %>>Garlic</option>
										<option value="Water Testing"<%= document.get("diet_phaseIII").indexOf("Water Testing|") > -1 ? " selected" : "" %>>Water Testing</option>
									</select>
									</td>
									<td>
									<select name="stress_phaseIII" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Emotional"<%= document.get("stress_phaseIII").indexOf("Emotional|") > -1 ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_phaseIII").indexOf("Mental|") > -1 ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_phaseIII").indexOf("Physical|") > -1 ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_phaseIII").indexOf("Spiritual|") > -1 ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_phaseIII").indexOf("Toxins|") > -1 ? " selected" : "" %>>Toxins</option>
										<option value="Lead"<%= document.get("stress_phaseIII").indexOf("Lead|") > -1 ? " selected" : "" %>>Lead</option>
										<option value="Mercury"<%= document.get("stress_phaseIII").indexOf("Mercury|") > -1 ? " selected" : "" %>>Mercury</option>
										<option value="Cadmium"<%= document.get("stress_phaseIII").indexOf("Cadmium|") > -1 ? " selected" : "" %>>Cadmium</option>
										<option value="Arsenic"<%= document.get("stress_phaseIII").indexOf("Arsenic|") > -1 ? " selected" : "" %>>Arsenic</option>
										<option value="Aluminium"<%= document.get("stress_phaseIII").indexOf("Aluminium|") > -1 ? " selected" : "" %>>Aluminium</option>
										<option value="Copper"<%= document.get("stress_phaseIII").indexOf("Copper|") > -1 ? " selected" : "" %>>Copper</option>
										<option value="Nickel"<%= document.get("stress_phaseIII").indexOf("Nickel|") > -1 ? " selected" : "" %>>Nickel</option>
										<option value="Zinc"<%= document.get("stress_phaseIII").indexOf("Zinc|") > -1 ? " selected" : "" %>>Zinc</option>
										<option value="Cobalt"<%= document.get("stress_phaseIII").indexOf("Cobalt|") > -1 ? " selected" : "" %>>Cobalt</option>
										<option value="Manganese"<%= document.get("stress_phaseIII").indexOf("Manganese|") > -1 ? " selected" : "" %>>Manganese</option>
										<option value="Biochemical"<%= document.get("stress_phaseIII").indexOf("Biochemical|") > -1 ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_phaseIII").indexOf("Energetic (EMF)|") > -1 ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
								    </table>
								</div>
								<div class="end"></div>
							</div>
							
							<div class="adminItem">
								<div class="leftTM">PHASE IV DATE</div>
								<div class="right">
									<input name="dateInputPhaseIV" onfocus="getDate('userMineralRatiosForm','dateInputPhaseIV');select();" value="<%= document.getPhaseIVDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">
								    <table>
									<tr>
									<td>
									<div >Supplements</div>
									</td>
									<td>
									<div >Detox</div>
									</td>
									<td>
									<div >Exercise</div>
									</td>
									<td>
									<div >Diet</div>
									</td>
									<td>
									<div >Stress</div>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_header_phaseIV" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("supplements_header_phaseIV").equals("") ? " selected" : "" %>></option>
										<option value="Digestive Support"<%= document.get("supplements_header_phaseIV").equals("Digestive Support") ? " selected" : "" %>>Digestive Support</option>
										<option value="Adrenal/Thyroid Support"<%= document.get("supplements_header_phaseIV").equals("Adrenal/Thyroid Support") ? " selected" : "" %>>Adrenal/Thyroid Support</option>
										<option value="Thyroid/Adrenal Support"<%= document.get("supplements_header_phaseIV").equals("Thyroid/Adrenal Support") ? " selected" : "" %>>Thyroid/Adrenal Support</option>
										<option value="Adrenal Support"<%= document.get("supplements_header_phaseIV").equals("Adrenal Support") ? " selected" : "" %>>Adrenal Support</option>
										<option value="Thyroid Support"<%= document.get("supplements_header_phaseIV").equals("Thyroid Support") ? " selected" : "" %>>Thyroid Support</option>
										<option value="Heavy Metal Detox"<%= document.get("supplements_header_phaseIV").equals("Heavy Metal Detox") ? " selected" : "" %>>Heavy Metal Detox</option>
										<option value="Copper Toxicity"<%= document.get("supplements_header_phaseIV").equals("Copper Toxicity") ? " selected" : "" %>>Copper Toxicity</option>
										<option value="Copper Detox/Heavy Metals"<%= document.get("supplements_header_phaseIV").equals("Copper Detox/Heavy Metals") ? " selected" : "" %>>Copper Detox/Heavy Metals</option>
										<option value="Maintenance Supplementation"<%= document.get("supplements_header_phaseIV").equals("Maintenance Supplementation") ? " selected" : "" %>>Maintenance Supplementation</option>
									</select>
									</td>
									<td>
									<select name="detox_header_phaseIV" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("detox_header_phaseIV").equals("") ? " selected" : "" %>></option>
										<option value="Liver/GB Flush"<%= document.get("detox_header_phaseIV").equals("Liver/GB Flush") ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_header_phaseIV").equals("SP Cleanse") ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_header_phaseIV").equals("Colon Cleanse") ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_header_phaseIV").equals("Juice Fast") ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_header_phaseIV").equals("Blood Cleanse") ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_header_phaseIV").equals("Homeopathic") ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_header_phaseIV" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("exercise_header_phaseIV").equals("") ? " selected" : "" %>></option>
										<option value="Walking Daily 30 minutes"<%= document.get("exercise_header_phaseIV").equals("Walking Daily 30 minutes") ? " selected" : "" %>>Walking Daily 30 minutes</option>
										<option value="Walking Daily 40-60 minutes"<%= document.get("exercise_header_phaseIV").equals("Walking Daily 40-60 minutes") ? " selected" : "" %>>Walking Daily 40-60 minutes</option>
										<option value="Walking & Strength Training"<%= document.get("exercise_header_phaseIV").equals("Walking & Strength Training") ? " selected" : "" %>>Walking & Strength Training</option>
										<option value="Cardio 30-60 minutes"<%= document.get("exercise_header_phaseIV").equals("Cardio 30-60 minutes") ? " selected" : "" %>>Cardio 30-60 minutes</option>
										<option value="Stretching"<%= document.get("exercise_header_phaseIV").equals("Stretching") ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_header_phaseIV" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("diet_header_phaseIV").equals("") ? " selected" : "" %>></option>
										<option value="Nutrition Plan"<%= document.get("diet_header_phaseIV").equals("Nutrition Plan") ? " selected" : "" %>>Nutrition Plan</option>
									</select>
									</td>
									<td>
									<select name="stress_header_phaseIV" class="select" style="font-size : 8pt; width: 103px; background-color: #CCFF99;">
										<option value=""<%= document.get("stress_header_phaseIV").equals("") ? " selected" : "" %>></option>
										<option value="Emotional"<%= document.get("stress_header_phaseIV").equals("Emotional") ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_header_phaseIV").equals("Mental") ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_header_phaseIV").equals("Physical") ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_header_phaseIV").equals("Spiritual") ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_header_phaseIV").equals("Toxins") ? " selected" : "" %>>Toxins</option>
										<option value="Toxin (Mercury)"<%= document.get("stress_header_phaseIV").equals("Toxin (Mercury)") ? " selected" : "" %>>Toxin (Mercury)</option>
										<option value="Toxin (Copper)"<%= document.get("stress_header_phaseIV").equals("Toxin (Copper)") ? " selected" : "" %>>Toxin (Copper)</option>
										<option value="Toxin (Cadmium)"<%= document.get("stress_header_phaseIV").equals("Toxin (Cadmium)") ? " selected" : "" %>>Toxin (Cadmium)</option>
										<option value="Toxin (Aluminium)"<%= document.get("stress_header_phaseIV").equals("Toxin (Aluminium)") ? " selected" : "" %>>Toxin (Aluminium)</option>
										<option value="Toxin (Nickel)"<%= document.get("stress_header_phaseIV").equals("Toxin (Nickel)") ? " selected" : "" %>>Toxin (Nickel)</option>
										<option value="Toxin (Cobalt)"<%= document.get("stress_header_phaseIV").equals("Toxin (Cobalt)") ? " selected" : "" %>>Toxin (Cobalt)</option>
										<option value="Toxin (Manganese)"<%= document.get("stress_header_phaseIV").equals("Toxin (Manganese)") ? " selected" : "" %>>Toxin (Manganese)</option>
										<option value="Toxin (Lead)"<%= document.get("stress_header_phaseIV").equals("Toxin (Lead)") ? " selected" : "" %>>Toxin (Lead)</option>
										<option value="Biochemical"<%= document.get("stress_header_phaseIV").equals("Biochemical") ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_header_phaseIV").equals("Energetic (EMF)") ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
									<tr>
									<td>
									<select name="supplements_phaseIV" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Maintenance Supplementation"<%= document.get("supplements_phaseIV").indexOf("Maintenance Supplementation|") > -1 ? " selected" : "" %>>Maintenance Supplementation</option>
										<option value="Repeat Phase I"<%= document.get("supplements_phaseIV").indexOf("Repeat Phase I|") > -1 ? " selected" : "" %>>Repeat Phase I</option>
										<option value="Repeat Phase II"<%= document.get("supplements_phaseIV").indexOf("Repeat Phase II|") > -1 ? " selected" : "" %>>Repeat Phase II</option>
										<option value="Repeat Phase III"<%= document.get("supplements_phaseIV").indexOf("Repeat Phase III|") > -1 ? " selected" : "" %>>Repeat Phase III</option>
										<option value="Homeopathic"<%= document.get("supplements_phaseIV").indexOf("Homeopathic|") > -1 ? " selected" : "" %>>Homeopathic</option>
										<option value="Multizyme"<%= document.get("supplements_phaseIV").indexOf("Multizyme|") > -1 ? " selected" : "" %>>Multizyme</option>
										<option value="Sp Bk Radish"<%= document.get("supplements_phaseIV").indexOf("Sp Bk Radish|") > -1 ? " selected" : "" %>>Sp Bk Radish</option>
										<option value="Zypan"<%= document.get("supplements_phaseIV").indexOf("Zypan|") > -1 ? " selected" : "" %>>Zypan</option>
										<option value="Pancreatrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Pancreatrophin PMG|") > -1 ? " selected" : "" %>>Pancreatrophin PMG</option>
										<option value="Fungal Forte"<%= document.get("supplements_phaseIV").indexOf("Fungal Forte|") > -1 ? " selected" : "" %>>Fungal Forte</option>
										<option value="Multi-Probiotic"<%= document.get("supplements_phaseIV").indexOf("Multi-Probiotic|") > -1 ? " selected" : "" %>>Multi-Probiotic</option>
										<option value="Cholacol"<%= document.get("supplements_phaseIV").indexOf("Cholacol|") > -1 ? " selected" : "" %>>Cholacol</option>
										<option value="Theralac"<%= document.get("supplements_phaseIV").indexOf("Theralac|") > -1 ? " selected" : "" %>>Theralac</option>
										<option value="Lactic Acid Yeast waf"<%= document.get("supplements_phaseIV").indexOf("Lactic Acid Yeast waf|") > -1 ? " selected" : "" %>>Lactic Acid Yeast waf</option>
										<option value="DiGest (liq)"<%= document.get("supplements_phaseIV").indexOf("DiGest (liq)|") > -1 ? " selected" : "" %>>DiGest (liq)</option>
										<option value="DiGest (caps)"<%= document.get("supplements_phaseIV").indexOf("DiGest (caps)|") > -1 ? " selected" : "" %>>DiGest (caps)</option>
										<option value="Zymex"<%= document.get("supplements_phaseIV").indexOf("Zymex|") > -1 ? " selected" : "" %>>Zymex</option>
										<option value="Zymex waf"<%= document.get("supplements_phaseIV").indexOf("Zymex waf|") > -1 ? " selected" : "" %>>Zymex waf</option>
										<option value="Lact-Enz"<%= document.get("supplements_phaseIV").indexOf("Lact-Enz|") > -1 ? " selected" : "" %>>Lact-Enz</option>
										<option value="A-F Betafood"<%= document.get("supplements_phaseIV").indexOf("A-F Betafood|") > -1 ? " selected" : "" %>>A-F Betafood</option>
										<option value="LivCo"<%= document.get("supplements_phaseIV").indexOf("LivCo|") > -1 ? " selected" : "" %>>LivCo</option>
										<option value="DigestPlex (GF)"<%= document.get("supplements_phaseIV").indexOf("DigestPlex (GF|") > -1 ? " selected" : "" %>>DigestPlex (GF)</option>
										<option value="Protein Powder"<%= document.get("supplements_phaseIV").indexOf("Protein Powder|") > -1 ? " selected" : "" %>>Protein Powder</option>
										<option value="Greens"<%= document.get("supplements_phaseIV").indexOf("Greens|") > -1 ? " selected" : "" %>>Greens</option>
										<option value="Cataplex GTF"<%= document.get("supplements_phaseIV").indexOf("Cataplex GTF|") > -1 ? " selected" : "" %>>Cataplex GTF</option>
										<option value="Gymnema"<%= document.get("supplements_phaseIV").indexOf("Gymnema|") > -1 ? " selected" : "" %>>Gymnema</option>
										<option value="Paraplex"<%= document.get("supplements_phaseIV").indexOf("Paraplex|") > -1 ? " selected" : "" %>>Paraplex</option>
										<option value="Inositol"<%= document.get("supplements_phaseIV").indexOf("Inositol|") > -1 ? " selected" : "" %>>Inositol</option>
										<option value="Detox Formula (GF)"<%= document.get("supplements_phaseIV").indexOf("Detox Formula (GF)|") > -1 ? " selected" : "" %>>Detox Formula (GF)</option>
										<option value="PhytoGreens (GF)"<%= document.get("supplements_phaseIV").indexOf("PhytoGreens (GF)|") > -1 ? " selected" : "" %>>PhytoGreens (GF)</option>
										<option value="VitaLiv"<%= document.get("supplements_phaseIV").indexOf("VitaLiv|") > -1 ? " selected" : "" %>>VitaLiv</option>
										<option value="BFood"<%= document.get("supplements_phaseIV").indexOf("BFood|") > -1 ? " selected" : "" %>>BFood</option>
										<option value="Primal Defense"<%= document.get("supplements_phaseIV").indexOf("Primal Defense|") > -1 ? " selected" : "" %>>Primal Defense</option>
										<option value="Chewable Catalyn"<%= document.get("supplements_phaseIV").indexOf("Chewable Catalyn|") > -1 ? " selected" : "" %>>Chewable Catalyn</option>
										<option value="Ferrofood"<%= document.get("supplements_phaseIV").indexOf("Ferrofood|") > -1 ? " selected" : "" %>>Ferrofood</option>
										<option value="Catalyn"<%= document.get("supplements_phaseIV").indexOf("Catalyn|") > -1 ? " selected" : "" %>>Catalyn</option>
										<option value="Boswellia Complex"<%= document.get("supplements_phaseIV").indexOf("Boswellia Complex|") > -1 ? " selected" : "" %>>Boswellia Complex</option>
										<option value="Cranberry Complex"<%= document.get("supplements_phaseIV").indexOf("Cranberry Complex|") > -1 ? " selected" : "" %>>Cranberry Complex</option>
										<option value="Rehmannia Complex"<%= document.get("supplements_phaseIV").indexOf("Rehmannia Complex|") > -1 ? " selected" : "" %>>Rehmannia Complex</option>
										<option value="Withania Complex"<%= document.get("supplements_phaseIV").indexOf("Withania Complex|") > -1 ? " selected" : "" %>>Withania Complex</option>
										<option value="Thyroid Complex"<%= document.get("supplements_phaseIV").indexOf("Thyroid Complex|") > -1 ? " selected" : "" %>>Thyroid Complex</option>
										<option value="Congaplex"<%= document.get("supplements_phaseIV").indexOf("Congaplex|") > -1 ? " selected" : "" %>>Congaplex</option>
										<option value="Cardiotrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Cardiotrophin PMG|") > -1 ? " selected" : "" %>>Cardiotrophin PMG</option>
										<option value="Drenatrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Drenatrophin PMG|") > -1 ? " selected" : "" %>>Drenatrophin PMG</option>
										<option value="Hypothalamus PMG"<%= document.get("supplements_phaseIV").indexOf("Hypothalamus PMG|") > -1 ? " selected" : "" %>>Hypothalamus PMG</option>
										<option value="Mammary PMG"<%= document.get("supplements_phaseIV").indexOf("Mammary PMG|") > -1 ? " selected" : "" %>>Mammary PMG</option>
										<option value="Myotrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Myotrophin PMG|") > -1 ? " selected" : "" %>>Myotrophin PMG</option>
										<option value="Neurotrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Neurotrophin PM|") > -1 ? " selected" : "" %>>Neurotrophin PMG</option>
										<option value="Ostrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Ostrophin PMG|") > -1 ? " selected" : "" %>>Ostrophin PMG</option>
										<option value="Ovatrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Ovatrophin PMG|") > -1 ? " selected" : "" %>>Ovatrophin PMG</option>
										<option value="Pneumotrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Pneumotrophin PMG|") > -1 ? " selected" : "" %>>Pneumotrophin PMG</option>
										<option value="Pituitrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Pituitrophin PMG|") > -1 ? " selected" : "" %>>Pituitrophin PMG</option>
										<option value="Prostate PMG"<%= document.get("supplements_phaseIV").indexOf("Prostate PMG|") > -1 ? " selected" : "" %>>Prostate PMG</option>
										<option value="Renatrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Renatrophin PMG|") > -1 ? " selected" : "" %>>Renatrophin PMG</option>
										<option value="Thymus PMG"<%= document.get("supplements_phaseIV").indexOf("Thymus PMG|") > -1 ? " selected" : "" %>>Thymus PMG</option>
										<option value="Thytrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Thytrophin PM|") > -1 ? " selected" : "" %>>Thytrophin PMG</option>
										<option value="Utrophin PMG"<%= document.get("supplements_phaseIV").indexOf("Utrophin PM|") > -1 ? " selected" : "" %>>Utrophin PMG</option>
										<option value="A-C Carbamide"<%= document.get("supplements_phaseIV").indexOf("A-C Carbamide|") > -1 ? " selected" : "" %>>A-C Carbamide</option>
										<option value="FlavoC"<%= document.get("supplements_phaseIV").indexOf("FlavoC|") > -1 ? " selected" : "" %>>FlavoC</option>
										<option value="Cat A"<%= document.get("supplements_phaseIV").indexOf("Cat A|") > -1 ? " selected" : "" %>>Cat A</option>
										<option value="Cat A-C"<%= document.get("supplements_phaseIV").indexOf("Cat A-C|") > -1 ? " selected" : "" %>>Cat A-C</option>
										<option value="Cat A-C-P"<%= document.get("supplements_phaseIV").indexOf("Cat A-C-P|") > -1 ? " selected" : "" %>>Cat A-C-P</option>
										<option value="Cat B"<%= document.get("supplements_phaseIV").indexOf("Cat B|") > -1 ? " selected" : "" %>>Cat B</option>
										<option value="Cat C"<%= document.get("supplements_phaseIV").indexOf("Cat C|") > -1 ? " selected" : "" %>>Cat C</option>
										<option value="Cat D"<%= document.get("supplements_phaseIV").indexOf("Cat D|") > -1 ? " selected" : "" %>>Cat D</option>
										<option value="Cat E"<%= document.get("supplements_phaseIV").indexOf("Cat E|") > -1 ? " selected" : "" %>>Cat E</option>
										<option value="Cataplex F"<%= document.get("supplements_phaseIV").indexOf("Cataplex F|") > -1 ? " selected" : "" %>>Cat F</option>
										<option value="Cataplex G"<%= document.get("supplements_phaseIV").indexOf("Cataplex G|") > -1 ? " selected" : "" %>>Cataplex G</option>
										<option value="CalMag"<%= document.get("supplements_phaseIV").indexOf("CalMag|") > -1 ? " selected" : "" %>>CalMag</option>
										<option value="Cal Lact Powder"<%= document.get("supplements_phaseIV").indexOf("Cal Lact Powder|") > -1 ? " selected" : "" %>>Cal Lact Powder</option>
										<option value="Magnesium Lactate"<%= document.get("supplements_phaseIV").indexOf("Magnesium Lactate|") > -1 ? " selected" : "" %>>Magnesium Lactate</option>
										<option value="Manganese B12"<%= document.get("supplements_phaseIV").indexOf("Manganese B12|") > -1 ? " selected" : "" %>>Manganese B12</option>
										<option value="Liquid Herbs"<%= document.get("supplements_phaseIV").indexOf("Liquid Herbs|") > -1 ? " selected" : "" %>>Liquid Herbs</option>
										<option value="CardioPlus"<%= document.get("supplements_phaseIV").indexOf("CardioPlus|") > -1 ? " selected" : "" %>>CardioPlus</option>
										<option value="Adrenal Des"<%= document.get("supplements_phaseIV").indexOf("Adrenal Des|") > -1 ? " selected" : "" %>>Adrenal Des</option>
										<option value="Spleen Des"<%= document.get("supplements_phaseIV").indexOf("Spleen Des|") > -1 ? " selected" : "" %>>Spleen Des</option>
										<option value="Drenamin"<%= document.get("supplements_phaseIV").indexOf("Drenamin|") > -1 ? " selected" : "" %>>Drenamin</option>
										<option value="Fish Oil"<%= document.get("supplements_phaseIV").indexOf("Fish Oil|") > -1 ? " selected" : "" %>>Fish Oil</option>
										<option value="OBM"<%= document.get("supplements_phaseIV").indexOf("OBM|") > -1 ? " selected" : "" %>>OBM</option>
										<option value="Prolamine Iodine"<%= document.get("supplements_phaseIV").indexOf("Prolamine Iodine|") > -1 ? " selected" : "" %>>Prolamine Iodine</option>
										<option value="Parotid"<%= document.get("supplements_phaseIV").indexOf("Parotid|") > -1 ? " selected" : "" %>>Parotid</option>
										<option value="Cholacol II"<%= document.get("supplements_phaseIV").indexOf("Cholacol II|") > -1 ? " selected" : "" %>>Cholacol II</option>
										<option value="Cholaplex"<%= document.get("supplements_phaseIV").indexOf("Cholaplex|") > -1 ? " selected" : "" %>>Cholaplex</option>
										<option value="Zinc Liver Chelate"<%= document.get("supplements_phaseIV").indexOf("Zinc Liver Chelate|") > -1 ? " selected" : "" %>>Zinc Liver Chelate</option>
										<option value="Protefood"<%= document.get("supplements_phaseIV").indexOf("Protefood|") > -1 ? " selected" : "" %>>Protefood</option>
										<option value="MolyCu"<%= document.get("supplements_phaseIV").indexOf("MolyCu|") > -1 ? " selected" : "" %>>MolyCu</option>
										<option value="GB3"<%= document.get("supplements_phaseIV").indexOf("GB3|") > -1 ? " selected" : "" %>>GB3</option>
										<option value="EndoPan"<%= document.get("supplements_phaseIV").indexOf("EndoPan|") > -1 ? " selected" : "" %>>EndoPan</option>
										<option value="Flaxseed oil"<%= document.get("supplements_phaseIV").indexOf("Flaxseed oil|") > -1 ? " selected" : "" %>>Flaxseed oil</option>
										<option value="Arginex"<%= document.get("supplements_phaseIV").indexOf("Arginex|") > -1 ? " selected" : "" %>>Arginex</option>
										<option value="Rehmannia (liq)"<%= document.get("supplements_phaseIV").indexOf("Rehmannia (liq)|") > -1 ? " selected" : "" %>>Rehmannia (liq)</option>
										<option value="Saligesic"<%= document.get("supplements_phaseIV").indexOf("Saligesic|") > -1 ? " selected" : "" %>>Saligesic</option>
										<option value="Vitanox"<%= document.get("supplements_phaseIV").indexOf("Vitanox|") > -1 ? " selected" : "" %>>Vitanox</option>
										<option value="St. John's Wort"<%= document.get("supplements_phaseIV").indexOf("St. John's Wort|") > -1 ? " selected" : "" %>>St. John's Wort</option>
										<option value="California Poppy"<%= document.get("supplements_phaseIV").indexOf("California Poppy|") > -1 ? " selected" : "" %>>California Poppy</option>
										<option value="Broncafect"<%= document.get("supplements_phaseIV").indexOf("Broncafect|") > -1 ? " selected" : "" %>>Broncafect</option>
										<option value="Min-Tran"<%= document.get("supplements_phaseIV").indexOf("Min-Tran|") > -1 ? " selected" : "" %>>Min-Tran</option>
										<option value="Min-Chex"<%= document.get("supplements_phaseIV").indexOf("Min-Chex|") > -1 ? " selected" : "" %>>Min-Chex</option>
										<option value="Progon B"<%= document.get("supplements_phaseIV").indexOf("Progon B|") > -1 ? " selected" : "" %>>Progon B</option>
										<option value="Tribulus"<%= document.get("supplements_phaseIV").indexOf("Tribulus|") > -1 ? " selected" : "" %>>Tribulus</option>
										<option value="Wheat Germ oil"<%= document.get("supplements_phaseIV").indexOf("Wheat Germ oil|") > -1 ? " selected" : "" %>>Wheat Germ oil</option>
										<option value="Symplex F"<%= document.get("supplements_phaseIV").indexOf("Symplex F|") > -1 ? " selected" : "" %>>Symplex F</option>
										<option value="Glucosamine Synergy"<%= document.get("supplements_phaseIV").indexOf("Glucosamine Synergy|") > -1 ? " selected" : "" %>>Glucosamine Synergy</option>
										<option value="Ligaplex II"<%= document.get("supplements_phaseIV").indexOf("Ligaplex II|") > -1 ? " selected" : "" %>>Ligaplex II</option>
										<option value="Collagen C"<%= document.get("supplements_phaseIV").indexOf("Collagen C|") > -1 ? " selected" : "" %>>Collagen C</option>
										<option value="Symplex M"<%= document.get("supplements_phaseIV").indexOf("Symplex M|") > -1 ? " selected" : "" %>>Symplex M</option>
										<option value="Male Support"<%= document.get("supplements_phaseIV").indexOf("Male Support|") > -1 ? " selected" : "" %>>Male Support</option>
										<option value="Orchex"<%= document.get("supplements_phaseIV").indexOf("Orchex|") > -1 ? " selected" : "" %>>Orchex</option>
										<option value="Lipoic Acid"<%= document.get("supplements_phaseIV").indexOf("Lipoic Acid|") > -1 ? " selected" : "" %>>Lipoic Acid</option>
										<option value="Ambrotose"<%= document.get("supplements_phaseIV").indexOf("Ambrotose|") > -1 ? " selected" : "" %>>Ambrotose</option>
									</select>
									</td>
									<td>
									<select name="detox_phaseIV" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Liver/GB Flush"<%= document.get("detox_phaseIV").indexOf("Liver/GB Flush|") > -1 ? " selected" : "" %>>Liver/GB Flush</option>
										<option value="SP Cleanse"<%= document.get("detox_phaseIV").indexOf("SP Cleanse|") > -1 ? " selected" : "" %>>SP Cleanse</option>
										<option value="Colon Cleanse"<%= document.get("detox_phaseIV").indexOf("Colon Cleanse|") > -1 ? " selected" : "" %>>Colon Cleanse</option>
										<option value="Juice Fast"<%= document.get("detox_phaseIV").indexOf("Juice Fast|") > -1 ? " selected" : "" %>>Juice Fast</option>
										<option value="Blood Cleanse"<%= document.get("detox_phaseIV").indexOf("Blood Cleanse|") > -1 ? " selected" : "" %>>Blood Cleanse</option>
										<option value="Homeopathic"<%= document.get("detox_phaseIV").indexOf("Homeopathic|") > -1 ? " selected" : "" %>>Homeopathic</option>
									</select>
									</td>
									<td>
									<select name="exercise_phaseIV" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Walk Daily 30 min"<%= document.get("exercise_phaseIV").indexOf("Walk Daily 30 min|") > -1 ? " selected" : "" %>>Walk Daily 30 min</option>
										<option value="Walk Daily 40-60 min"<%= document.get("exercise_phaseIV").indexOf("Walk Daily 40-60 min|") > -1 ? " selected" : "" %>>Walk Daily 40-60 min</option>
										<option value="Walk/Strength Train"<%= document.get("exercise_phaseIV").indexOf("Walk/Strength Train|") > -1 ? " selected" : "" %>>Walk/Strength Train</option>
										<option value="Cardio 30-60 min"<%= document.get("exercise_phaseIV").indexOf("Cardio 30-60 min|") > -1 ? " selected" : "" %>>Cardio 30-60 min</option>
										<option value="Stretching"<%= document.get("exercise_phaseIV").indexOf("Stretching|") > -1 ? " selected" : "" %>>Stretching</option>
									</select>
									</td>
									<td>
									<select name="diet_phaseIV" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="No sugar/fruit"<%= document.get("diet_phaseIV").indexOf("No sugar/fruit|") > -1 ? " selected" : "" %>>No sugar/fruit</option>
										<option value="Protein Shakes"<%= document.get("diet_phaseIV").indexOf("Protein Shakes|") > -1 ? " selected" : "" %>>Protein Shakes</option>
										<option value="Protein Shakes w/greens"<%= document.get("diet_phaseIV").indexOf("Protein Shakes w/greens|") > -1 ? " selected" : "" %>>Protein Shakes w/greens</option>
										<option value="Protein Shakes w/flax oil"<%= document.get("diet_phaseIV").indexOf("Protein Shakes w/flax oil|") > -1 ? " selected" : "" %>>Protein Shakes w/flax oil</option>
										<option value="Flax seed oil"<%= document.get("diet_phaseIV").indexOf("Flax seed oil|") > -1 ? " selected" : "" %>>Flax seed oil</option>
										<option value="Garlic"<%= document.get("diet_phaseIV").indexOf("Garlic|") > -1 ? " selected" : "" %>>Garlic</option>
										<option value="Water Testing"<%= document.get("diet_phaseIV").indexOf("Water Testing|") > -1 ? " selected" : "" %>>Water Testing</option>
									</select>
									</td>
									<td>
									<select name="stress_phaseIV" class="multipleSelect" size="10" multiple="multiple" style="font-size : 8pt; width: 103px;">
										<option value="Emotional"<%= document.get("stress_phaseIV").indexOf("Emotional|") > -1 ? " selected" : "" %>>Emotional</option>
										<option value="Mental"<%= document.get("stress_phaseIV").indexOf("Mental|") > -1 ? " selected" : "" %>>Mental</option>
										<option value="Physical"<%= document.get("stress_phaseIV").indexOf("Physical|") > -1 ? " selected" : "" %>>Physical</option>
										<option value="Spiritual"<%= document.get("stress_phaseIV").indexOf("Spiritual|") > -1 ? " selected" : "" %>>Spiritual</option>
										<option value="Toxins"<%= document.get("stress_phaseIV").indexOf("Toxins|") > -1 ? " selected" : "" %>>Toxins</option>
										<option value="Lead"<%= document.get("stress_phaseIV").indexOf("Lead|") > -1 ? " selected" : "" %>>Lead</option>
										<option value="Mercury"<%= document.get("stress_phaseIV").indexOf("Mercury|") > -1 ? " selected" : "" %>>Mercury</option>
										<option value="Cadmium"<%= document.get("stress_phaseIV").indexOf("Cadmium|") > -1 ? " selected" : "" %>>Cadmium</option>
										<option value="Arsenic"<%= document.get("stress_phaseIV").indexOf("Arsenic|") > -1 ? " selected" : "" %>>Arsenic</option>
										<option value="Aluminium"<%= document.get("stress_phaseIV").indexOf("Aluminium|") > -1 ? " selected" : "" %>>Aluminium</option>
										<option value="Copper"<%= document.get("stress_phaseIV").indexOf("Copper|") > -1 ? " selected" : "" %>>Copper</option>
										<option value="Nickel"<%= document.get("stress_phaseIV").indexOf("Nickel|") > -1 ? " selected" : "" %>>Nickel</option>
										<option value="Zinc"<%= document.get("stress_phaseIV").indexOf("Zinc|") > -1 ? " selected" : "" %>>Zinc</option>
										<option value="Cobalt"<%= document.get("stress_phaseIV").indexOf("Cobalt|") > -1 ? " selected" : "" %>>Cobalt</option>
										<option value="Manganese"<%= document.get("stress_phaseIV").indexOf("Manganese|") > -1 ? " selected" : "" %>>Manganese</option>
										<option value="Biochemical"<%= document.get("stress_phaseIV").indexOf("Biochemical|") > -1 ? " selected" : "" %>>Biochemical</option>
										<option value="Energetic (EMF)"<%= document.get("stress_phaseIV").indexOf("Energetic (EMF)|") > -1 ? " selected" : "" %>>Energetic (EMF)</option>
									</select>
									</td>
									</tr>
								    </table>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input class="formbutton" type="submit" name="submit_button" value="Add&nbsp;&#47;&nbsp;Update" alt="Add&nbsp;&#47;&nbsp;Update" style="margin-right: 4px;"/><input type="hidden" name="delete_id" value="0" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"><input name="new_template" onfocus="select();" value="" size="7" maxlength="100" class="inputbox" style="width: 124px; margin-right: 4px;" /></div>
								<div class="right">
									<input class="formbutton" type="submit" name="submit_button" value="Save As Template" alt="Add&nbsp;&#47;&nbsp;Update" style="margin-right: 4px;"/><input type="hidden" name="delete_id" value="0" />
								</div>
								<div class="end"></div>
							</div>

						</struts-html:form>

						<div class="content_AdministrationTable">

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  ">Mineral Ratios Client Documents</td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator document_itr = MineralRatiosClientDocumentBean.getMineralRatiosClientDocuments(adminPerson).iterator();
    if (document_itr.hasNext())
    {
	while (document_itr.hasNext())
	{
	    MineralRatiosClientDocumentBean document_obj = (MineralRatiosClientDocumentBean)document_itr.next();
%>
							
							<div class="audience">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="admin-user-mineral-ratios.jsp?id=<%= document_obj.getId() %>" title=""><%= document_obj.getLabel() %></a></td>
										<td style="width:  70px; text-align: center;  "><a href="admin-user-mineral-ratios-report.jsp?id=<%= document_obj.getId() %>">Reports</a>&nbsp;<a href="javascript:if (confirm('Are you sure?')) {document.forms[1].delete_id.value=<%= document_obj.getId() %>;document.forms[1].submit();}" title="Delete">Delete</a></td>
									</tr>
								</table>
							</div>
							
<%
	}
    }
    else
    {
%>
							
							<div class="audience">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colspan="3">No Documents Found</td>
									</tr>
								</table>
							</div>
							
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						</div>

					</div>

					<div class="end"></div>
				</div>

				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="..\channels\channel-footer.jsp" %>

		</div>

	</body>

</html>