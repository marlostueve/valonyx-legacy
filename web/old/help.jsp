<%@ page contentType="text/html" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>

		<title>Ecolab</title>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="generator" content="TextPad 4.6.2" />
		<meta name="author" content="" />
		<meta name="copyright" content="&copy; 2007" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />

		<link rel="stylesheet" type="text/css" href="css/web.css" />

	</head>

	<body>

		<div id="main">

<%@ include file="channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Welcome</p>
					<p>Welcome to the Ecolab LMS Help screen. Special care has been taken to design the LMS interface intuitively, so as a rule you will find what you need on the page of the LMS you are on, or under a tab related to the direction you wish to explore. Here are a few tips to make basic navigation easier:</p>
					<ul>
						<li>The main tabs across the top of the page divide functional sections of the LMS.</li>
						<li>All links in this LMS are blue (active) if you currently <strong>can</strong> complete that action, or gray (inactive) if you <strong>cannot</strong>. For example, if you want to download an activity from the Internet, the link will be blue if you are on-line, and gray if you are not.</li>
						<li>On your Home page, in the Activities section, Courses are listed in green, Lessons in bold, and Activities in blue.</li>
						<li>When you download or enroll in an activity, it is removed from the Activity Catalog and placed on your Home page.</li>
					</ul>
				</div>

				<div class="content_TextBlock">
					<p class="headline">Getting Started</p>
					<p>Always make sure you are logged into the LMS under your own ID and password. It goes without saying, but completing activities or resources while logged in under someone else's name does little good for your completion records, and causes huge administrative challenges keeping the system current. It's up to each user to be consistent with IT standards to always be logged into a system under your own domain name and password.</p>
				</div>

				<div class="content_TextBlock">
					<p class="headline">Icon Key</p>
					<div style="width: 300px; float: left;">
						<p><strong>Activity-Type Icons</strong></p>
						<table cellspacing="0" cellpadding="0" border="0" summary="" class="help">
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnActivityELRN.gif" width="17" height="17" title="E-Learning" alt="E-Learning" /></td>
								<td style="width: 200px; text-align: left;">E-Learning</td>
							</tr>
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnActivityMNTR.gif" width="17" height="17" title="Mentor" alt="Mentor" /></td>
								<td style="width: 200px; text-align: left;  ">Mentor</td>
							</tr>
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnActivityILED.gif" width="17" height="17" title="Instructor-Led" alt="Instructor-Led" /></td>
								<td style="width: 200px; text-align: left;  ">Instructor-Led</td>
							</tr>
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnActivityASMT.gif" width="17" height="17" title="Assessment" alt="Assessment" /></td>
								<td style="width: 200px; text-align: left;  ">Assessment</td>
							</tr>
						</table>
					</div>

					<div style="width: 300px; float: left;">
						<p><strong>Resource-Type Icons</strong></p>
						<table cellspacing="0" cellpadding="0" border="0" summary="" class="help">
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnResourceDOC.gif" width="17" height="17" title="Microsoft Word (DOC)" alt="Microsoft Word (DOC)" /></td>
								<td style="width: 200px; text-align: left;  ">Microsoft Word (DOC)</td>
							</tr>
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnResourcePPT.gif" width="17" height="17" title="Microsoft PowerPoint (PPT)" alt="Microsoft PowerPoint (PPT)" /></td>
								<td style="width: 200px; text-align: left;  ">Microsoft PowerPoint (PPT)</td>
							</tr>
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnResourceXLS.gif" width="17" height="17" title="Microsoft Excel (XLS)" alt="Microsoft Excel (XLS)" /></td>
								<td style="width: 200px; text-align: left;  ">Microsoft Excel (XLS)</td>
							</tr>
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnResourcePDF.gif" width="17" height="17" title="Adobe Acrobat (PDF)" alt="Adobe Acrobat (PDF)" /></td>
								<td style="width: 200px; text-align: left;  ">Adobe Acrobat (PDF)</td>
							</tr>
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnResourceSWF.gif" width="17" height="17" title="Macromedia Flash (SWF)" alt="Macromedia Flash (SWF)" /></td>
								<td style="width: 200px; text-align: left;  ">Macromedia Flash (SWF)</td>
							</tr>
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnResourceWEB.gif" width="17" height="17" title="Internet" alt="Internet" /></td>
								<td style="width: 200px; text-align: left;  ">Internet</td>
							</tr>
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnResourceVID.gif" width="17" height="17" title="Video" alt="Video" /></td>
								<td style="width: 200px; text-align: left;  ">Video</td>
							</tr>
							<tr>
								<td style="width:  25px; text-align: center;"><img src="images/icnResourceCD.gif" width="17" height="17" title="CD" alt="CD" /></td>
								<td style="width: 200px; text-align: left;  ">CD</td>
							</tr>
						</table>
					</div>
					<div style="clear: left;"></div>
				</div>
<%
if (isSomeSortOfAdministrator)
{
%>
				<div class="content_TextBlock">
					<p class="headline">Glossary</p>
					<p><strong>Note:</strong> This glossary defines Ecolab roles as they pertain to user permissions within the LMS. If you are a manager and can't view particular areas or print reports within the LMS that you think you should be able to access, it may be because of permission level limitations (based on the role you are assigned).</p>
					<div>
						<table cellspacing="0" cellpadding="0" border="0" summary="" class="help">
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Area Manager (AM)</strong></td>
								<td style="width: 528px; text-align: left;">A Manager of an Area. Area managers have the ability to run reports on their area only. Area managers can also start&#47;complete Mentor Activities for anyone in their Area.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Area Route Manager (ARM)</strong></td>
								<td style="width: 528px; text-align: left;">Also know as a Sub-Area manager for purposes of the LSM diagram. This user has access to reports for their sub-area only and can start and complete Mentor Activities for their Sub-Area as well.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Assessment</strong></td>
								<td style="width: 528px; text-align: left;">A test that can be created by an Administrator and offered to users. No programming or additional course files are needed.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Certification</strong></td>
								<td style="width: 528px; text-align: left;">A certification is like a mentor activity that has a completion date in the future&nbsp;&#8212;&nbsp;the expiration date. Only Administrators can mark that a user has the certification and when it expires.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>District Manager (DM)</strong></td>
								<td style="width: 528px; text-align: left;">A Manager of a District. District managers have the ability to run reports on their District only. District Managers can also start&#47;complete Mentor Activities for anyone in their district.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Domain Administrator</strong></td>
								<td style="width: 528px; text-align: left;">A user that has full administrative rights over a domain. Rights include adding users, activities, certifications and resources; editing user data, editing user transcripts, full domain reporting, etc.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Domain</strong></td>
								<td style="width: 528px; text-align: left;">An LMS portal. Examples of Domains are: Ecolab Institutional, EcoSure, Darden's (External Client Portal).</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>E-Learning Activity</strong></td>
								<td style="width: 528px; text-align: left;">Also know as CBT or WBT, these will be SCOs, or collections of SCOs in the LMS. Users can enroll online for these activities. E-learning activities can only be created and modified by Administrators.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Evaluation</strong></td>
								<td style="width: 528px; text-align: left;">Attached to another activity (Assessment, Instructor-led or e-Learning). The evaluation is used to gather data from users about a specific offering.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Field Training Manager (FTM)</strong></td>
								<td style="width: 528px; text-align: left;">A user will full domain reporting and full domain mentor activity start&#47;completion rights. This user has a preferred Region and Area but can work anywhere.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Instructor-Led Activity</strong></td>
								<td style="width: 528px; text-align: left;">Users can't enroll in an Instructor-Led Activity course on their own. The Administrators must manually enroll users for these activities. Once enrolled, the user will see the record like an other activity. The only people that can modify Instructor-led Activity records for users (completion, score) are the Administrators.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Learning Management System (LMS)</strong></td>
								<td style="width: 528px; text-align: left;">The full implementation of the LMS. LMS will represent the entire project and systems.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>LMS Administrator</strong></td>
								<td style="width: 528px; text-align: left;">A user will full administrative rights over the entire LMS. In addition to the Domain Administrator rights on any domain, the LMS administrator will be able to create Domain Administrators and create Domains. The LMS Administrator is the only person who can run cross-domain reports.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Mentor Activity</strong></td>
								<td style="width: 528px; text-align: left;">A mentor activity is a record similar to an e-learning activity in SCO form with a few differences. The main difference is that only a user's Manager can mark the mentor activity as completed. Examples of mentor activities are Skull Sessions, Validation Checklists and other Field activities a Manager conducts with their reports. Users must enroll in a mentor activity like they would a course.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Non-Field Manager</strong></td>
								<td style="width: 528px; text-align: left;">A manager who doesn't belong to any geography, only a domain. This user can run any reports on the entire domain but has no Activity rights.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Non-Field User</strong></td>
								<td style="width: 528px; text-align: left;">A user who can enroll and launch courses but doesn't belong to any geography, only a domain. The user has no reporting capabilities or Activity rights.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Regional Vice President (RVP)</strong></td>
								<td style="width: 528px; text-align: left;">A user with full domain reporting rights but no activity rights. This user will have a regional preference.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Reports</strong></td>
								<td style="width: 528px; text-align: left;">The LMS report feature allows users with sufficient permissions to view or print learner data, that can be sorted or filtered in different ways similar to an Excel spreadsheet. If you are a manager and can't view or print a report within the LMS that you think you should be able to access, it may be because of your permission level (based on the role you are assigned within the LMS).</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Resource</strong></td>
								<td style="width: 528px; text-align: left;">A resource is an activity available to a user that follows SCORM standards. The resource isn't tracked or added to the user's transcript. Resources will be available by searching the offering Catalog or linked from other offering summaries and objectives.  All resources must be attached an activity.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Route Manager (RM)</strong></td>
								<td style="width: 528px; text-align: left;">A user responsible a set of accounts in a route. This standard user has standard access to courses, activities, resources and certificates with no reporting capabilities.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Route Supervisor</strong></td>
								<td style="width: 528px; text-align: left;">A user responsible for a route group. Route Supervisors have reporting abilities for their Route Group and have mentor activity rights for that route group as well.</td>
							</tr>
							<tr>
								<td style="width: 200px; text-align: left;"><strong>Screen</strong></td>
								<td style="width: 528px; text-align: left;">A screen is what the users view on their monitors and interact with for each task within the LMS.</td>
							</tr>
						</table>
					</div>
<%
}
%>
				<div class="content_TextBlock">
				    <p>If you still need assistance, ask your supervisor or contact your FTM.</p>
				</div>
				
			</div>
			
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="channels\channel-footer.jsp" %>

		</div>

	</body>

</html>