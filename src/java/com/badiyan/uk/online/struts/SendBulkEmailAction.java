/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.struts;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import com.badiyan.uk.online.util.EncryptionUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;
import org.apache.torque.TorqueException;

/**
 * Implementation of <strong>Action</strong> that validates a client profile.
 *
 * @author Marlo Stueve
 */
public final class
SendBulkEmailAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in SendBulkEmailAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;

		UKOnlineCompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;
		EmailList adminEmailList = null;

		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminPerson = (UKOnlinePersonBean)loginBean.getPerson();

				admin_company = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				adminEmailList = (EmailList)session.getAttribute("emailList");
			}
			else
				return (_mapping.findForward("session_expired"));



			String subjectInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			String emailInput = (String)PropertyUtils.getSimpleProperty(_form, "emailInput");
			Boolean isAllClientsInput = (Boolean)PropertyUtils.getSimpleProperty(_form, "isAllClientsInput");
			
			//String startDate = (String)PropertyUtils.getSimpleProperty(_form, "startDate");
			//String endDate = (String)PropertyUtils.getSimpleProperty(_form, "endDate");
			//Boolean isBulkEmailInput = (Boolean)PropertyUtils.getSimpleProperty(_form, "isBulkEmailInput");
			
			if (subjectInput.isEmpty())
				throw new IllegalValueException("Please provide an email subject");
			
			if (emailInput.isEmpty())
				throw new IllegalValueException("Please provide an email body");

			System.out.println("subjectInput >" + subjectInput);
			System.out.println("nameInput >" + emailInput);
			System.out.println("isAllClientsInput >" + isAllClientsInput);
			
			if (isAllClientsInput != null && isAllClientsInput.booleanValue()) {
				
				System.out.println("send to all clients >" + isAllClientsInput.booleanValue());
				
				// grab all the users in this business unit
				
				System.out.println("admin_company >" + admin_company.getLabel());
				
				int num_emails = 0;
				
				Vector email_addresses_already_processed = new Vector();
				
				Iterator itr = admin_company.getPeople().iterator();
				while (itr.hasNext()) {
					PersonBean person_obj = (PersonBean)itr.next();
					UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person_obj.getId());
					if (person.hasValidEmailAddress() && person.isAdult()) {
						System.out.println("person(adult) w/ Email >" + person.getLabel() + "  >" + person.getEmail1String());
						if (!email_addresses_already_processed.contains(person.getEmail1String().toLowerCase())) {
							String html_str = SendBulkEmailAction.getHTMLEmailString(person, subjectInput, emailInput);
							//System.out.println(html_str);
							CUBean.sendEmail(person.getEmail1String(), "cstueve@sanowc.com", subjectInput, html_str);
							num_emails++;
							System.out.println("sleeping...");
							Thread.sleep(2000);
							
							email_addresses_already_processed.addElement(person.getEmail1String().toLowerCase());
						} else {
							System.out.println("already sent...");
						}
					}
				}
				
				System.out.println("num_emails >" + num_emails);
				
			} else {
			
				// to, from, subj, value
				String html_str = SendBulkEmailAction.getHTMLEmailString(adminPerson, subjectInput, emailInput);
				System.out.println(html_str);
				CUBean.sendEmail("marlo@valonyx.com", "cstueve@sanowc.com", subjectInput + " - test email", html_str);
				CUBean.sendEmail("cstueve@sanowc.com", "cstueve@sanowc.com", subjectInput + " - test email", html_str);
				CUBean.sendEmail("marlo@badiyan.com", "cstueve@sanowc.com", subjectInput + " - test email", html_str);
				
			}

			/*
			adminMarketingPlan.setName(nameInput);

			if ((startDate != null) && (startDate.length() > 0))
				adminMarketingPlan.setStartDate(CUBean.getDateFromUserString(startDate));

			if ((endDate != null) && (endDate.length() > 0))
				adminMarketingPlan.setEndDate(CUBean.getDateFromUserString(endDate));

			if (isBulkEmailInput == null)
				adminMarketingPlan.setIsBulkEmail(false);
			else
				adminMarketingPlan.setIsBulkEmail(isBulkEmailInput.booleanValue());

			if ((nameInput == null) || (nameInput.length() == 0))
				throw new IllegalValueException("You must specify a name for the marketing plan.");

			adminMarketingPlan.setIsActive(true);
			adminMarketingPlan.setCompany(admin_company);
			adminMarketingPlan.save();
			*/

		}
		catch (Exception x)
		{
			x.printStackTrace();
			//errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			session.setAttribute("error-message", x.getMessage());
			forwardString = "failure";
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty())
		{
			saveErrors(_request, errors);
			return _mapping.getInputForward();
		}

		// Remove the obsolete form bean
		if (_mapping.getAttribute() != null)
		{
            if ("request".equals(_mapping.getScope()))
                _request.removeAttribute(_mapping.getAttribute());
            else
                session.removeAttribute(_mapping.getAttribute());
        }

		// Forward control to the specified success URI
		return (_mapping.findForward(forwardString));
	}
	
	
	
	public static String
	getHTMLEmailString(PersonBean _person, String _subject, String _body) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, UnsupportedEncodingException {
		
		AddressBean address = _person.getDepartment().getCompany().getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
		
		StringBuffer buf = new StringBuffer();
		buf.append("<div>");
		buf.append("  <table cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px #acacac solid;width:615px\" align=\"center\">");
		buf.append("    <tbody><tr>");
		buf.append("      <td bgcolor=\"#11100e\" height=\"89\" valign=\"top\">");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:613px\">");
		buf.append("          <tbody><tr>");
		buf.append("            <td valign=\"middle\" height=\"89\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:24px;color:#8e8d8d;padding-left:30px;overflow:hidden;word-wrap:break-word;width:350px\">" + _person.getDepartment().getCompany().getLabel());
		buf.append("            <br><span style=\"font-size:20px\">" + _subject + "</span></td>");
		if (_person.getDepartment().getCompany().getId() == 5)
			buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"><img src=\"https://www.valonyx.com/images/sano-header.jpg\"  /></td>");
		else
			buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"></td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("      </td>");
		buf.append("    </tr>");
		buf.append("    <tr>");
		buf.append("      <td>");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		buf.append("          <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word;padding:14px 26px 14px 26px\">" + _person.getDepartment().getCompany().getLabel());
		buf.append("                  <br>" + address.getStreet1String() + " <br>" + address.getStreet2String() );
		buf.append("                  <br>" + address.getCityString() + ", " + address.getStateString() + " " + address.getZipCodeString());
		buf.append("                  <br><a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a></td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr>");
		buf.append("            <td style=\"padding-left:26px\">");
		buf.append("              <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #acacac;width:559px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td bgcolor=\"#e1eeef\" style=\"padding:13px 20px 13px 20px;font-family:Arial,Helvetica,sans-serif;color:#317679;font-size:18px;font-weight:bold;line-height:24px;overflow:auto;word-wrap:break-word;width:240px\">");
		buf.append("                  Dear " + _person.getFirstNameString() + ",");
		buf.append("                  <br>" + _body + "</td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        <tr>");
		buf.append("            <td style=\"padding:12px 26px 16px 26px\">");
		buf.append("              <table width=\"561\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:561px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word\">");
		buf.append("If you have any questions, please contact " + _person.getDepartment().getCompany().getLabel() + " at <a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a> . ");
		buf.append("                  <br>&nbsp;");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr bgcolor=\"#f2f2f2\">");
		buf.append("            <td style=\"border-top:1px solid #acacac;padding:26px 0px 14px 26px\">");
		buf.append("              <table width=\"587\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:587px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#6e6e6e;width:420px;overflow:auto;word-wrap:break-word\">This e-mail was sent on behalf of  ");
		buf.append("                  <a href=\"http://www.sanowc.com\" style=\"color:#317679!important\" target=\"_blank\">" + _person.getDepartment().getCompany().getLabel() + "</a>");
		buf.append("                  <br>by <a href=\"http://www.valonyx.com\">valonyx.com</a>. This is an automated email; please do not reply. <br /> <a href=\"https://www.valonyx.com/ScheduleServlet2?command=unsubscribe&arg1=" + URLEncoder.encode(EncryptionUtils.encryptString(_person.getEmail1String()), "UTF-8") + "\">Unsubscribe</a> to stop receiving these emails.");
		buf.append("                  <br>&nbsp;");
		buf.append("                  <br>");
		buf.append("                  ");
		buf.append("                  <br></td>");
		buf.append("                  <td width=\"167\" valign=\"bottom\">");
		buf.append("                    <a href=\"http://www.valonyx.com\"><img src=\"http://www.valonyx.com/images/valonyx-logo-grey-198.png\" /></a>");
		buf.append("                  </td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("</div>");
		
		return buf.toString();
	}
}
