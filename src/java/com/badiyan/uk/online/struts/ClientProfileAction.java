/*
 * ClientProfileAction.java
 *
 * Created on February 9, 2008, 2:18 PM
 * 
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.*;
import java.util.*;

import javax.servlet.*;
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
ClientProfileAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in ClientProfileAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
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
				
				admin_company = (CompanyBean)session.getAttribute("adminCompany");

			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 * <form-bean       name="clientProfileForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="person_id" type="java.lang.Integer"/>
      <form-property name="file_number" type="java.lang.String"/>
      <form-property name="prefix" type="java.lang.String"/>
      <form-property name="firstname" type="java.lang.String"/>
      <form-property name="middle" type="java.lang.String"/>
      <form-property name="lastname" type="java.lang.String"/>
      <form-property name="suffix" type="java.lang.String"/>
      <form-property name="phone" type="java.lang.String"/>
      <form-property name="cell" type="java.lang.String"/>
      <form-property name="email" type="java.lang.String"/>
      <form-property name="ssn" type="java.lang.String"/>
      <form-property name="address1" type="java.lang.String"/>
      <form-property name="address2" type="java.lang.String"/>
      <form-property name="city" type="java.lang.String"/>
      <form-property name="state" type="java.lang.String"/>
      <form-property name="zipcode" type="java.lang.String"/>
      <form-property name="dob" type="java.lang.String"/>
      <form-property name="gender" type="java.lang.String"/>
      <form-property name="deceased" type="java.lang.Boolean"/>
      <form-property name="file_type" type="java.lang.Integer"/>

      <form-property name="prospect" type="java.lang.Boolean"/>
      <form-property name="marketingPlanSelect" type="java.lang.Integer"/>
      <form-property name="referralProfileSelect" type="java.lang.Integer"/>
    </form-bean>
			 */
			
			Integer person_id = (Integer)PropertyUtils.getSimpleProperty(_form, "person_id");
			String file_number = (String)PropertyUtils.getSimpleProperty(_form, "file_number");
			String prefix = (String)PropertyUtils.getSimpleProperty(_form, "prefix");
			String firstname = (String)PropertyUtils.getSimpleProperty(_form, "firstname");
			String middle = (String)PropertyUtils.getSimpleProperty(_form, "middle");
			String lastname = (String)PropertyUtils.getSimpleProperty(_form, "lastname");
			String suffix = (String)PropertyUtils.getSimpleProperty(_form, "suffix");
			String phone = (String)PropertyUtils.getSimpleProperty(_form, "phone");
			String cell = (String)PropertyUtils.getSimpleProperty(_form, "cell");
			String email = (String)PropertyUtils.getSimpleProperty(_form, "email");
			String password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			String ssn = (String)PropertyUtils.getSimpleProperty(_form, "ssn");
			String address1 = (String)PropertyUtils.getSimpleProperty(_form, "address1");
			String address2 = (String)PropertyUtils.getSimpleProperty(_form, "address2");
			String city = (String)PropertyUtils.getSimpleProperty(_form, "city");
			String state = (String)PropertyUtils.getSimpleProperty(_form, "state");
			String zipcode = (String)PropertyUtils.getSimpleProperty(_form, "zipcode");
			String dob = (String)PropertyUtils.getSimpleProperty(_form, "dob");
			String gender = (String)PropertyUtils.getSimpleProperty(_form, "gender");
			Boolean deceased = (Boolean)PropertyUtils.getSimpleProperty(_form, "deceased");
			Integer client_type = (Integer)PropertyUtils.getSimpleProperty(_form, "client_type");
			Integer file_type = (Integer)PropertyUtils.getSimpleProperty(_form, "file_type");

			Boolean prospect = (Boolean)PropertyUtils.getSimpleProperty(_form, "c_prospect");
			Integer marketingPlanSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "marketingProfileSelect");
			Integer referralClientSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "referralProfileSelect");

			System.out.println("person_id >" + person_id);
			System.out.println("file_number >" + file_number);
			System.out.println("prefix >" + prefix);
			System.out.println("firstname >" + firstname);
			System.out.println("middle >" + middle);
			System.out.println("lastname >" + lastname);
			System.out.println("suffix >" + suffix);
			System.out.println("phone >" + phone);
			System.out.println("cell >" + cell);
			System.out.println("email >" + email);
			System.out.println("ssn >" + ssn);
			System.out.println("address1 >" + address1);
			System.out.println("address2 >" + address2);
			System.out.println("city >" + city);
			System.out.println("state >" + state);
			System.out.println("zipcode >" + zipcode);
			System.out.println("dob >" + dob);
			System.out.println("gender >" + gender);
			System.out.println("deceased >" + deceased);
			System.out.println("client_type >" + client_type.intValue());
			System.out.println("file_type >" + file_type.intValue());
			
			adminPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person_id.intValue());
			session.setAttribute("adminPerson", adminPerson);

			if (file_number.length() > 0)
			{
				try
				{
					adminPerson.setFileNumber(Integer.parseInt(file_number));
				}
				catch (NumberFormatException x)
				{
					x.printStackTrace();
				}
			}
			
			adminPerson.setPrefix(prefix);
			adminPerson.setFirstName(firstname);
			adminPerson.setMiddleName(middle);
			adminPerson.setLastName(lastname);
			adminPerson.setSuffix(suffix);
			
			PhoneNumberBean home_phone_number = null;
			try
			{
				home_phone_number = adminPerson.getPhoneNumber(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			}
			catch (ObjectNotFoundException x)
			{
				home_phone_number = new PhoneNumberBean();
			}
			home_phone_number.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			home_phone_number.setPerson(adminPerson);
			home_phone_number.setNumber(phone);
			home_phone_number.save();
			adminPerson.addPhoneNumber(home_phone_number);
			
			PhoneNumberBean cell_phone_number = null;
			try
			{
				cell_phone_number = adminPerson.getPhoneNumber(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			}
			catch (ObjectNotFoundException x)
			{
				cell_phone_number = new PhoneNumberBean();
			}
			cell_phone_number.setType(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			cell_phone_number.setPerson(adminPerson);
			cell_phone_number.setNumber(cell);
			cell_phone_number.save();
			adminPerson.addPhoneNumber(cell_phone_number);
			
			AddressBean home_address = null;
			try
			{
				home_address = adminPerson.getAddress(AddressBean.PERSON_ADDRESS_TYPE);
			}
			catch (ObjectNotFoundException x)
			{
				home_address = new AddressBean();
			}
			home_address.setStreet1(address1);
			home_address.setStreet2(address2);
			home_address.setCity(city);
			home_address.setState(state);
			home_address.setZipCode(zipcode);
			home_address.setCreateOrModifyPerson(loginBean.getPerson());
			home_address.setType(AddressBean.PERSON_ADDRESS_TYPE);
			home_address.setCompany(admin_company);
			home_address.save();
			adminPerson.addAddress(home_address);
			
			adminPerson.setSSN(ssn);
			
			if (email.length() > 0)
				adminPerson.setEmail(email);
			
			if (password.length() > 0)
				adminPerson.setPassword(password);
			
			if (gender.equals("Male"))
				adminPerson.setMale();
			else
				adminPerson.setFemale();
			
			if (deceased != null)
				adminPerson.setDeceased(true);
			else
				adminPerson.setDeceased(false);
			
			if (dob.length() > 0)
				adminPerson.setBirthDate(CUBean.getDateFromUserString(dob));

			if ((prospect != null) && prospect.booleanValue())
				adminPerson.setPersonType(UKOnlinePersonBean.PROSPECT_PERSON_TYPE);
			else
				adminPerson.setPersonType(UKOnlinePersonBean.CLIENT_PERSON_TYPE);

			if (client_type.intValue() > -1)
				adminPerson.setTitle(PersonTitleBean.getPersonTitle(client_type.intValue()));
			
			adminPerson.save();
			
			if (file_type.intValue() > -1)
			{
				GroupUnderCareMemberTypeBean file_type_obj = GroupUnderCareMemberTypeBean.getMemberType(file_type.intValue());
				adminPerson.setGroupUnderCareMemberType(file_type_obj);
			}

			if (((marketingPlanSelect != null) && (marketingPlanSelect.intValue() > 0)) || ((referralClientSelect != null) && (referralClientSelect.intValue() > 0)))
			{
				ReferralSource referral_source = null;
				try
				{
					referral_source = ReferralSource.getReferralSource(adminPerson);
				}
				catch (ObjectNotFoundException x)
				{
					referral_source = new ReferralSource();
					referral_source.setPerson(adminPerson);
				}
				
				if ((marketingPlanSelect != null) && (marketingPlanSelect.intValue() > 0))
					referral_source.setMarketingPlan(MarketingPlan.getMarketingPlan(marketingPlanSelect.intValue()));
				else
					referral_source.setReferredBy((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(referralClientSelect.intValue()));
				referral_source.save();
			}

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
	saveClient(UKOnlineCompanyBean admin_company, UKOnlinePersonBean _mod_person, UKOnlinePersonBean adminPerson, String firstname, String lastname, String phone, String cell, String email, String filenumber, String gender, String address1, String address2, String city, String state, String zipcode) throws TorqueException, ObjectNotFoundException, IllegalValueException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, Exception {
		
		if ((firstname == null) || (lastname == null) || (firstname.length() == 0) || (lastname.length() == 0)) {
			throw new IllegalValueException("You must provide first and last name.");
		}
                
		if (phone == null && cell == null && email == null) {
			throw new IllegalValueException("You must provide a phone number, cell number, or an email address.");
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

		if (adminPerson.isNew()) {
			
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

			if (cell.length() > 0) {
				Vector persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(admin_company, lastname, firstname, cell, false);
				if (persons.size() > 0) {
					// a person with a similar name and a matching email address already exists.
					throw new ObjectAlreadyExistsException("A client already exists with a similar name and phone number.");
				}
			}
		}


		int file_num = 0;
		if (filenumber != null && filenumber.length() > 0) {
			try {
				file_num = Integer.parseInt(filenumber);
			} catch (NumberFormatException x) {
				throw new IllegalValueException("File # must be numeric.");
			}
		}

		if (file_num > 0) {
			adminPerson.setFileNumber(file_num);
		}


		adminPerson.setFirstName(firstname);
		adminPerson.setLastName(lastname);

		if (email.length() > 0)
			adminPerson.setEmail(email);

		if (gender.equals("Male")) {
			adminPerson.setMale();
		} else {
			adminPerson.setFemale();
		}


		if (phone.length() > 0) {
			PhoneNumberBean home_phone = null;
			try {
				home_phone = PhoneNumberBean.getPhoneNumber(adminPerson, PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			} catch (ObjectNotFoundException x) {
				home_phone = new PhoneNumberBean();
				home_phone.setPerson(adminPerson);
				home_phone.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			}
			home_phone.setNumber(phone);
			home_phone.save();
		}

		if (cell.length() > 0) {
			PhoneNumberBean cell_phone = null;
			try {
				cell_phone = PhoneNumberBean.getPhoneNumber(adminPerson, PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			} catch (ObjectNotFoundException x) {
				cell_phone = new PhoneNumberBean();
				cell_phone.setPerson(adminPerson);
				cell_phone.setType(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			}
			
			cell_phone.setNumber(cell);
			cell_phone.save();
		}

		AddressBean home_address = null;
		try {
			home_address = adminPerson.getAddress(AddressBean.PERSON_ADDRESS_TYPE);
		} catch (ObjectNotFoundException x) {
			home_address = new AddressBean();
		}
		home_address.setStreet1(address1);
		home_address.setStreet2(address2);
		home_address.setCity(city);
		home_address.setState(state);
		home_address.setZipCode(zipcode);
		home_address.setCreateOrModifyPerson(_mod_person);
		home_address.setType(AddressBean.PERSON_ADDRESS_TYPE);
		home_address.setCompany(admin_company);
		home_address.save();
		adminPerson.addAddress(home_address);
		
		adminPerson.save();
		
		/*

		if (admin_company.getQuickBooksSettings().isQuickBooksFSEnabled()) {
			QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.getQueue(admin_company.getQuickBooksSettings());
			QBXMLCustomerAddRequest customer_add_req_obj = new QBXMLCustomerAddRequest();
			customer_add_req_obj.setCustomer(adminPerson);
			customer_add_req_obj.setCompany(admin_company);
			queue.add(admin_company.getQuickBooksSettings().getCompanyKeyString(), customer_add_req_obj);
		}
		*/
		
	}
	
	public static void
	saveClient(UKOnlineCompanyBean admin_company, UKOnlinePersonBean _mod_person, UKOnlinePersonBean adminPerson, String firstname, String middlename, String lastname, String phone, String cell, String email, String filenumber, String gender, String address1, String address2, String city, String state, String zipcode, String password, String prefix, String suffix, String dob, boolean _isActive) throws TorqueException, ObjectNotFoundException, IllegalValueException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, Exception {
		
		if ((firstname == null) || (lastname == null) || (firstname.length() == 0) || (lastname.length() == 0)) {
			throw new IllegalValueException("You must provide first and last name.");
		}
                
		if (phone == null && cell == null && email == null) {
			throw new IllegalValueException("You must provide a phone number, cell number, or an email address.");
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

		if (adminPerson.isNew()) {
			
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

			if (cell.length() > 0) {
				Vector persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(admin_company, lastname, firstname, cell, false);
				if (persons.size() > 0) {
					// a person with a similar name and a matching email address already exists.
					throw new ObjectAlreadyExistsException("A client already exists with a similar name and phone number.");
				}
			}
		}


		int file_num = 0;
		if (filenumber != null && filenumber.length() > 0) {
			try {
				file_num = Integer.parseInt(filenumber);
			} catch (NumberFormatException x) {
				throw new IllegalValueException("File # must be numeric.");
			}
		}

		if (file_num > 0) {
			adminPerson.setFileNumber(file_num);
		}


		adminPerson.setFirstName(firstname);
		adminPerson.setLastName(lastname);
		
		if (middlename != null && !middlename.isEmpty()) {
			adminPerson.setMiddleName(middlename);
		}
		
		if (password != null && !password.isEmpty()) {
			adminPerson.setPassword(password);
			adminPerson.setConfirmPassword(password);
		}
		
		if (prefix != null && !prefix.isEmpty()) {
			adminPerson.setPrefix(prefix);
		}
		
		if (suffix != null && !suffix.isEmpty()) {
			adminPerson.setSuffix(suffix);
		}
		
		if (dob != null && !dob.isEmpty()) {
			adminPerson.setBirthDate(CUBean.getDateFromUserString(dob));
		}
		

		if (email.length() > 0)
			adminPerson.setEmail(email);

		if (gender.equals("Male")) {
			adminPerson.setMale();
		} else {
			adminPerson.setFemale();
		}
		
		adminPerson.setActive(_isActive);

		if (phone.length() > 0) {
			PhoneNumberBean home_phone = null;
			try {
				home_phone = PhoneNumberBean.getPhoneNumber(adminPerson, PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			} catch (ObjectNotFoundException x) {
				home_phone = new PhoneNumberBean();
				home_phone.setPerson(adminPerson);
				home_phone.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			}
			home_phone.setNumber(phone);
			home_phone.save();
		}

		if (cell.length() > 0) {
			PhoneNumberBean cell_phone = null;
			try {
				cell_phone = PhoneNumberBean.getPhoneNumber(adminPerson, PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			} catch (ObjectNotFoundException x) {
				cell_phone = new PhoneNumberBean();
				cell_phone.setPerson(adminPerson);
				cell_phone.setType(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			}
			
			cell_phone.setNumber(cell);
			cell_phone.save();
		}

		AddressBean home_address = null;
		try {
			home_address = adminPerson.getAddress(AddressBean.PERSON_ADDRESS_TYPE);
		} catch (ObjectNotFoundException x) {
			home_address = new AddressBean();
		}
		home_address.setStreet1(address1);
		home_address.setStreet2(address2);
		home_address.setCity(city);
		home_address.setState(state);
		home_address.setZipCode(zipcode);
		home_address.setCreateOrModifyPerson(_mod_person);
		home_address.setType(AddressBean.PERSON_ADDRESS_TYPE);
		home_address.setCompany(admin_company);
		home_address.save();
		adminPerson.addAddress(home_address);
		
		adminPerson.save();
		
		/*

		if (admin_company.getQuickBooksSettings().isQuickBooksFSEnabled()) {
			QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.getQueue(admin_company.getQuickBooksSettings());
			QBXMLCustomerAddRequest customer_add_req_obj = new QBXMLCustomerAddRequest();
			customer_add_req_obj.setCustomer(adminPerson);
			customer_add_req_obj.setCompany(admin_company);
			queue.add(admin_company.getQuickBooksSettings().getCompanyKeyString(), customer_add_req_obj);
		}
		*/
		
	}
}
