/*
 * ClientNewAction.java
 *
 * Created on February 23, 2008, 2:18 PM
 * 
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.*;

import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

import com.valeo.qbpos.*;
import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;
import com.valeo.qb.QBXMLCustomerAddRequest;
import com.valeo.qb.QBXMLItemAddRequest;
import org.apache.torque.TorqueException;

/**
 * Implementation of <strong>Action</strong> that validates a client profile.
 *
 * @author Marlo Stueve
 */
public final class
ClientNewAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in ClientNewAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;
		
		UKOnlineCompanyBean admin_company = null;
		UKOnlinePersonBean adminPerson = null;
		
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
				{
					loginBean = new UKOnlineLoginBean();
					session.setAttribute("loginBean", loginBean);
				}
				
				admin_company = (UKOnlineCompanyBean)session.getAttribute("adminCompany");

			}
			else
				return (_mapping.findForward("session_expired"));
			
			String firstname = ((String)PropertyUtils.getSimpleProperty(_form, "firstname")).trim();
			String lastname = ((String)PropertyUtils.getSimpleProperty(_form, "lastname")).trim();

			String filenumber = ((String)PropertyUtils.getSimpleProperty(_form, "filenumber")).trim();
			String phone = ((String)PropertyUtils.getSimpleProperty(_form, "phone")).trim();
			String cell = ((String)PropertyUtils.getSimpleProperty(_form, "cell")).trim();
			String email = ((String)PropertyUtils.getSimpleProperty(_form, "email")).trim();

			String gender = (String)PropertyUtils.getSimpleProperty(_form, "gender");
			String group = (String)PropertyUtils.getSimpleProperty(_form, "group");
			Short relationshipSelect = (Short)PropertyUtils.getSimpleProperty(_form, "relationshipSelect");
			Integer group_id = (Integer)PropertyUtils.getSimpleProperty(_form, "group_id");
			Integer groupSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "groupSelect");
			Integer clientFileTypeSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "clientFileTypeSelect");

			Boolean prospect = (Boolean)PropertyUtils.getSimpleProperty(_form, "prospect");
			Integer marketingPlanSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "marketingPlanSelect");
			Integer referralClientSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "referralClientSelect");
			
			
			System.out.println("firstname >" + firstname);
			System.out.println("lastname >" + lastname);
			System.out.println("phone >" + phone);
			System.out.println("cell >" + cell);
			System.out.println("email >" + email);
			System.out.println("gender >" + gender);
			System.out.println("group >" + group);
			System.out.println("relationshipSelect >" + relationshipSelect);
			System.out.println("group_id >" + group_id);
			System.out.println("clientFileTypeSelect >" + clientFileTypeSelect);

			System.out.println("prospect >" + prospect);
			System.out.println("marketingPlanSelect >" + marketingPlanSelect);
			System.out.println("referralClientSelect >" + referralClientSelect);
			
			System.out.println("groupSelect >" + groupSelect);
			
			adminPerson = new UKOnlinePersonBean();
			session.setAttribute("adminPerson", adminPerson);
			session.setAttribute("searchLastName", lastname);
			
			ClientNewAction.saveNewClient(admin_company, (UKOnlinePersonBean)loginBean.getPerson(), adminPerson, firstname, lastname, phone, cell, email, filenumber, gender, marketingPlanSelect, referralClientSelect, groupSelect, group, relationshipSelect, clientFileTypeSelect, prospect);
			
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
	
	public static void
	saveNewClient(UKOnlineCompanyBean admin_company, UKOnlinePersonBean _mod_person, UKOnlinePersonBean adminPerson, String firstname, String lastname, String phone, String cell, String email, String filenumber, String gender, Integer marketingPlanSelect, Integer referralClientSelect, Integer groupSelect, String group, Short relationshipSelect, Integer clientFileTypeSelect, Boolean prospect) throws TorqueException, ObjectNotFoundException, IllegalValueException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, Exception {
		
		if ((firstname == null) || (lastname == null) || (firstname.length() == 0) || (lastname.length() == 0)) {
			throw new IllegalValueException("You must provide first and last name.");
		}
		
		if (phone == null) { phone = ""; }
		if (cell == null) { cell = ""; }
		if (email == null) { email = ""; }
                
		if ((phone.length() == 0) && (cell.length() == 0) && (email.length() == 0)) {
			throw new IllegalValueException("You must provide a phone number, cell number, or an email address.");
		}
		
		if (gender == null) {
			throw new IllegalValueException("Please specify a gender.");
		}

		//are there any local conflicts???

		if (email.length() > 0) {
			Vector persons = UKOnlinePersonBean.getPersonsByLastNameFirstNameEmail(admin_company, lastname, firstname, email, false);
			if (persons.size() > 0) {
				// a person with a similar name and a matching email address already exists.
				throw new ObjectAlreadyExistsException("A client already exists with a similar name and email address.");
			}
		}

		if (phone.length() > 0) {
			Vector persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(admin_company, lastname, firstname, phone, false);
			if (persons.size() > 0) {
				// a person with a similar name and a matching email address already exists.
				throw new ObjectAlreadyExistsException("A client already exists with a similar name and phone number.");
			}
		}

		if (cell.length() > 0)
		{
			Vector persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(admin_company, lastname, firstname, cell, false);
			if (persons.size() > 0)
			{
				// a person with a similar name and a matching email address already exists.

				throw new ObjectAlreadyExistsException("A client already exists with a similar name and phone number.");
			}
		}

		if ((marketingPlanSelect != null) && (referralClientSelect != null))
		{
			if ((marketingPlanSelect.intValue() > 0) && (referralClientSelect.intValue() > 0))
				throw new IllegalValueException("Please specify either a marketing campaign referral OR a client referral.");
		}

		int file_num = 0;
		if (filenumber.length() > 0) {
			try {
				file_num = Integer.parseInt(filenumber);
			} catch (NumberFormatException x) {
				throw new IllegalValueException("File # must be numeric.");
			}
		}


		adminPerson.setFirstName(firstname);
		adminPerson.setLastName(lastname);

		if (email.length() > 0)
			adminPerson.setEmail(email);

		if (gender.equals("Male"))
			adminPerson.setMale();
		else
			adminPerson.setFemale();

		//String password = PasswordGenerator.getPassword(3) + "-" + PasswordGenerator.getPassword(3);

		String password = PasswordGenerator.getPassword(PersonBean.getMinimumPasswordLength());
		boolean isUniquePw = PasswordGenerator.isUnique(password);
		while (!isUniquePw) {
			password = PasswordGenerator.getPassword(PersonBean.getMinimumPasswordLength());
			isUniquePw = PasswordGenerator.isUnique(password);
		}

		adminPerson.setUsername(password);
		adminPerson.setPassword(password);
		adminPerson.setConfirmPassword(password);

		adminPerson.setDepartment(admin_company.getDefaultDepartment());
		adminPerson.setTitle(PersonTitleBean.getDefaultTitle(admin_company));

		if (file_num > 0)
			adminPerson.setFileNumber(file_num);

		if ((prospect != null) && prospect.booleanValue())
			adminPerson.setPersonType(UKOnlinePersonBean.PROSPECT_PERSON_TYPE);
		else
			adminPerson.setPersonType(UKOnlinePersonBean.CLIENT_PERSON_TYPE);

		adminPerson.save();


		if (phone.length() > 0)
		{
			PhoneNumberBean home_phone = new PhoneNumberBean();
			home_phone.setNumber(phone);
			home_phone.setPerson(adminPerson);
			home_phone.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			home_phone.save();
		}

		if (cell.length() > 0)
		{
			PhoneNumberBean cell_phone = new PhoneNumberBean();
			cell_phone.setNumber(cell);
			cell_phone.setPerson(adminPerson);
			cell_phone.setType(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			cell_phone.save();
		}

		try
		{
			adminPerson.setGroup(PersonGroupBean.getDefaultPersonGroup(admin_company));
		}
		catch (ObjectAlreadyExistsException already_exists)
		{
			already_exists.printStackTrace();
		}

		// create a new group under care

		if ((groupSelect == null) || group.equals("New"))
		{
			GroupUnderCareBean group_under_care = new GroupUnderCareBean();
			group_under_care.setCreatePerson(_mod_person);
			group_under_care.setCompany(admin_company);
			group_under_care.setPrimaryClient(adminPerson);
			Vector members = new Vector();
			GroupUnderCareMember member = new GroupUnderCareMember();
			member.setPersonId(adminPerson.getId());
			member.setRelationshipToPrimaryClient(GroupUnderCareBean.RELATIONSHIP_SELF_TYPE);
			member.setGroupUnderCareMemberTypeId(clientFileTypeSelect.intValue());
			members.addElement(member);
			group_under_care.setGroupUnderCareMembers(members);
			group_under_care.setNote("");
			group_under_care.save();
		}
		else
		{
			UKOnlinePersonBean group_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(groupSelect.intValue());
			GroupUnderCareBean group_under_care = group_person.getGroupUnderCare();

			group_under_care.setModifyPerson(_mod_person);
			Vector members = group_under_care.getMembers();
			GroupUnderCareMember member = new GroupUnderCareMember();
			member.setPersonId(adminPerson.getId());
			member.setRelationshipToPrimaryClient(relationshipSelect.shortValue());
			member.setGroupUnderCareMemberTypeId(clientFileTypeSelect.intValue());
			members.addElement(member);
			group_under_care.setGroupUnderCareMembers(members);
			group_under_care.save();
		}

		if (((marketingPlanSelect != null) && (marketingPlanSelect.intValue() > 0)) || ((referralClientSelect != null) && (referralClientSelect.intValue() > 0)))
		{
			ReferralSource referral_source = new ReferralSource();
			referral_source.setPerson(adminPerson);
			if ((marketingPlanSelect != null) && (marketingPlanSelect.intValue() > 0))
				referral_source.setMarketingPlan(MarketingPlan.getMarketingPlan(marketingPlanSelect.intValue()));
			else
				referral_source.setReferredBy((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(referralClientSelect.intValue()));
			referral_source.save();
		}

		// attempt to add this new user to QBPoS...

		if (admin_company.getId() == 1)
		{
			QBPOSXMLCustomerAddRequest req_obj = new QBPOSXMLCustomerAddRequest();
			if (email.length() > 0)
				req_obj.setEmailString(email);
			req_obj.setFirstNameString(firstname);
			req_obj.setLastNameString(lastname);
			if (cell.length() > 0)
				req_obj.setPhone2String(cell);
			if (phone.length() > 0)
				req_obj.setPhoneString(phone);
			QBWebConnectorSvcSoapImpl.qbpos_request_queue.add(QBWebConnectorSvcSoapImpl.qbpos_company_key, req_obj);
		}


		if (admin_company.getQuickBooksSettings().isQuickBooksFSEnabled()) {
			QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.getQueue(admin_company.getQuickBooksSettings());
			QBXMLCustomerAddRequest customer_add_req_obj = new QBXMLCustomerAddRequest();
			customer_add_req_obj.setCustomer(adminPerson);
			customer_add_req_obj.setCompany(admin_company);
			queue.add(admin_company.getQuickBooksSettings().getCompanyKeyString(), customer_add_req_obj);
		}
	}
}
