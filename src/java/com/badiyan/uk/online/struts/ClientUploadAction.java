/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.struts;

import com.badiyan.torque.GroupUnderCareMember;
import com.badiyan.torque.PersonTitle;
import com.badiyan.torque.PersonTitlePeer;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.PasswordGenerator;

import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import java.util.Vector;
import javax.servlet.http.*;

import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.torque.TorqueException;


import org.apache.poi.xssf.usermodel.*;
import org.apache.torque.util.Criteria;


/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class ClientUploadAction
		extends Action {

	public ActionForward execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
			throws Exception {
		System.out.println("execute() invoked in ClientUploadAction");


		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		UKOnlineCompanyBean adminCompany = null;
		HttpSession session = _request.getSession(false);

		try {
			// Check the session to see if there's a course in progress...

			if (session != null) {
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				adminCompany = (UKOnlineCompanyBean)session.getAttribute("adminCompany");
				if (loginBean == null) {
					return (_mapping.findForward("session_expired"));
				} else {
					try {
						UKOnlinePersonBean person = (UKOnlinePersonBean) loginBean.getPerson();
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) && !person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME)) {
							return (_mapping.findForward("session_expired"));
						}
					} catch (IllegalValueException x) {
						return (_mapping.findForward("session_expired"));
					}
				}
			} else {
				return (_mapping.findForward("session_expired"));
			}

			String destination_file_path = null;
			String filename = null;

			String realPath = CUBean.getProperty("cu.realPath");
			String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
			if (resourcesFolder == null) {
				throw new IllegalValueException("Property not defined: cu.resourcesFolder");
			}

			CUBean.createDirectory(realPath, resourcesFolder + "user-upload-data");
			// ensure that an upload directory exists for this course

			String destination_folder = realPath + resourcesFolder + "user-upload-data/";


			UserUploadForm fileForm = (UserUploadForm) _form;
			FormFile uploadedFile = fileForm.getUploadInput();

			System.out.println("++++++++++++++ - " + uploadedFile.getFileName());

			filename = uploadedFile.getFileName();
			InputStream uploadInStream = uploadedFile.getInputStream();

			int c = 0;
			c = uploadInStream.read();
			if (c != -1) {
				destination_file_path = destination_folder + filename;
				System.out.println("destination_file_path >" + destination_file_path);
				FileOutputStream fOut = new FileOutputStream(destination_file_path);
				while (c != -1) {
					fOut.write(c);
					c = uploadInStream.read();
				}
				fOut.flush();
				fOut.close();
			} else {
				throw new IllegalValueException("Cannot find specified file.");
			}

			HSSFWorkbook wb = null;
			try {
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(destination_file_path));
				wb = new HSSFWorkbook(fs);
			} catch (IOException x) {
				System.out.println("detected XLXS File");
				
				StringBuffer results = ClientUploadAction.uploadFromBBBYXLSXFile(adminCompany, destination_file_path, false, loginBean.getPerson());
				throw new IllegalValueException("getting out");
				
				/*
				FileInputStream file = new FileInputStream(destination_file_path);
				XSSFWorkbook wb = new XSSFWorkbook(file);
				*/
			}

			int sheet_index = 0;

			StringBuffer results = new StringBuffer();

			HSSFSheet sheet = wb.getSheetAt(sheet_index);
			
			HSSFRow test_row = sheet.getRow(1);
			HSSFCell test_cell = test_row.getCell((short) 0);
			String test_str = this.getStringValueFromCell(test_cell);
			
			
			if (test_str.equals("Patient Recall Report"))
				results = this.processQuixoteUsers(adminCompany, loginBean, sheet);
			else {
			
			
				int rows_modified = 0;
				int row_index;

				int num_matched = 0;
				int num_no_match = 0;
				int num_no_unique = 0;

				int num_found_inactive = 0;

				for (row_index = 1; row_index < 99999; row_index++) {

					HSSFRow obj_row = sheet.getRow(row_index);

					if (obj_row == null) {
						break;
					} else {
						try {

							boolean modified = false;


							HSSFCell patientFirstName_cell = obj_row.getCell((short) 0);
							HSSFCell patientLastName_cell = obj_row.getCell((short) 1);
							HSSFCell patientFileNumber_cell = obj_row.getCell((short) 2);
							HSSFCell patientEMail_cell = obj_row.getCell((short) 3);
							HSSFCell patientPhone_cell = obj_row.getCell((short) 4);
							HSSFCell patientCell_cell = obj_row.getCell((short) 5);
							HSSFCell patientPrefix_cell = obj_row.getCell((short) 6);
							HSSFCell patientSuffix_cell = obj_row.getCell((short) 7);
							HSSFCell patientSex_cell = obj_row.getCell((short) 8);
							HSSFCell patientRelationship_cell = obj_row.getCell((short) 9);
							HSSFCell patientGroupFileNumber_cell = obj_row.getCell((short) 10);

							HSSFCell patientAddress1_cell = obj_row.getCell((short) 11);
							HSSFCell patientAddress2_cell = obj_row.getCell((short) 12);
							HSSFCell patientCity_cell = obj_row.getCell((short) 13);
							HSSFCell patientState_cell = obj_row.getCell((short) 14);
							HSSFCell patientZip_cell = obj_row.getCell((short) 15);

							HSSFCell patientBirthDate_cell = obj_row.getCell((short) 16);
							HSSFCell patientWorkPhone_cell = obj_row.getCell((short) 17);
							HSSFCell patientWorkPhoneExt_cell = obj_row.getCell((short) 18);
							HSSFCell patientActive_cell = obj_row.getCell((short) 19);


							String patientFirstName = this.getStringValueFromCell(patientFirstName_cell);
							String patientLastName = this.getStringValueFromCell(patientLastName_cell);
							String patientFileNumber = this.getStringValueFromCell(patientFileNumber_cell);
							String patientEMail = this.getStringValueFromCell(patientEMail_cell);
							String patientPhone = this.getStringValueFromCell(patientPhone_cell);
							String patientCell = this.getStringValueFromCell(patientCell_cell);
							String patientPrefix = this.getStringValueFromCell(patientPrefix_cell);
							String patientSuffix = this.getStringValueFromCell(patientSuffix_cell);
							String patientSex = this.getStringValueFromCell(patientSex_cell);
							String patientRelationship = this.getStringValueFromCell(patientRelationship_cell);
							String patientGroupFileNumber = this.getStringValueFromCell(patientGroupFileNumber_cell);

							String patientAddress1 = this.getStringValueFromCell(patientAddress1_cell);
							String patientAddress2 = this.getStringValueFromCell(patientAddress2_cell);
							String patientCity = this.getStringValueFromCell(patientCity_cell);
							String patientState = this.getStringValueFromCell(patientState_cell);
							String patientZip = this.getStringValueFromCell(patientZip_cell);

							String patientBirthDate = this.getStringValueFromCell(patientBirthDate_cell);
							String patientWorkPhone = this.getStringValueFromCell(patientWorkPhone_cell);
							String patientWorkPhoneExt = this.getStringValueFromCell(patientWorkPhoneExt_cell);
							String patientActive = this.getStringValueFromCell(patientActive_cell);

							System.out.println("");
							System.out.println("row >" + row_index + "<");
							System.out.println("first_name >" + patientFirstName + "<");
							System.out.println("last_name >" + patientLastName + "<");
							System.out.println("patientFileNumber >" + patientFileNumber + "<");
							System.out.println("patientEMail >" + patientEMail + "<");
							System.out.println("patientPhone >" + patientPhone + "<");
							System.out.println("patientCell >" + patientCell + "<");
							System.out.println("patientBirthDate >" + patientBirthDate + "<");
							System.out.println("patientActive >" + patientActive + "<");

							try
							{
								// ensure that one of home phone, cell phone, or email address has been provided

								if (patientFirstName.length() == 0 || patientLastName.length() == 0)
									throw new IllegalValueException("You must provide a first and last name.");

								if (patientEMail.length() == 0 && patientPhone.length() == 0 && patientCell.length() == 0)
									throw new IllegalValueException("You must provide at least one of home phone, cell phone, or email address.");

								Vector persons = new Vector();

								if ((patientPhone != null) && (patientPhone.length() > 0))
									persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(adminCompany, patientLastName, patientFirstName, patientPhone, false);

								if (persons.size() != 1)
								{
									if ((patientCell != null) && (patientCell.length() > 0))
										persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(adminCompany, patientLastName, patientFirstName, patientCell, false);
								}

								if (persons.size() != 1)
								{
									if ((patientEMail != null) && (patientEMail.length() > 0))
										persons = UKOnlinePersonBean.getPersonsByLastNameFirstNameEmail(adminCompany, patientLastName, patientFirstName, patientEMail, false);
								}

								if (persons.size() != 1)
									persons = UKOnlinePersonBean.getPersonsByLastNameFirstName(adminCompany, patientLastName, patientFirstName);

								if (persons.size() == 1)
								{
									num_matched++;
									System.out.println("Person found for " + patientFirstName + " " + patientLastName + " --- >" + patientActive);


									// found a unique person

									UKOnlinePersonBean person = (UKOnlinePersonBean)persons.get(0);

									if (patientActive.toLowerCase().equals("no") || patientActive.toLowerCase().equals("n"))
									{
										if (person.isActive())
										{
											num_found_inactive++;

											person.setActive(false);
										}
									}
									else
									{
										if (!person.isActive())
											person.setActive(true);
									}


									// update file number

									if (person.getFileNumberString().length() == 0)
									{
										// no existing file number.  update from spreadsheet

										person.setFileNumber(Integer.parseInt(patientFileNumber));
										modified = true;
									}

									// update birth date

									if ((patientBirthDate != null) && (patientBirthDate.length() > 0))
									{
										try
										{
											person.setBirthDate(this.getDateValueFromCell(patientBirthDate_cell));
											modified = true;
										}
										catch (Exception x)
										{
											person.setBirthDate(CUBean.getDateFromUserString(patientBirthDate));
											modified = true;
										}
									}

									// update prefix

									if ((person.getPrefixString().length() == 0) && (patientPrefix != null) && (patientPrefix.length() > 0))
									{
										person.setPrefix(patientPrefix);
										modified = true;
									}

									// update suffix

									if ((person.getSuffixString().length() == 0) && (patientSuffix != null) && (patientSuffix.length() > 0))
									{
										person.setSuffix(patientSuffix);
										modified = true;
									}

									// update email

									if ((person.getEmailString().length() == 0) && (patientEMail != null) && (patientEMail.length() > 0))
									{
										person.setEmail(patientEMail);
										modified = true;
									}

									// update sex

									boolean ss_male = patientSex.toLowerCase().equals("m") || patientSex.toLowerCase().equals("male");
									if (person.isMale() != ss_male)
									{
										if (ss_male)
											person.setMale();
										else
											person.setFemale();
										modified = true;
									}

									// update address

									if ((patientAddress1 != null) && (patientAddress1.length() > 0))
									{
										AddressBean person_address = null;
										boolean modify_address = true;

										try
										{
											person_address = AddressBean.getAddress(person, AddressBean.PERSON_ADDRESS_TYPE);
											if (person_address.getStreet1String().equals(patientAddress1))
												modify_address = false;
										}
										catch (ObjectNotFoundException x)
										{
											person_address = new AddressBean();
										}

										if (modify_address)
										{
											person_address.setCity(patientCity);
											person_address.setCompany(adminCompany);
											person_address.setCreateOrModifyPerson(loginBean.getPerson());
											person_address.setState(patientState);
											person_address.setStreet1(patientAddress1);
											person_address.setStreet2(patientAddress2);
											person_address.setType(AddressBean.PERSON_ADDRESS_TYPE);
											person_address.setZipCode(patientZip);
											person_address.save();

											person.addAddress(person_address);

											modified = true;
										}
									}

									if (modified)
										person.save();

									this.maintainGroupUnderCare((UKOnlinePersonBean)loginBean.getPerson(), adminCompany, person, patientGroupFileNumber, patientRelationship);

									// update home phone


									if ((patientPhone != null) && (patientPhone.length() > 0))
									{
										try
										{
											PhoneNumberBean.getPhoneNumber(person, PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
										}
										catch (ObjectNotFoundException x)
										{
											// no home phone found

											PhoneNumberBean home_phone = new PhoneNumberBean();
											home_phone.setNumber(patientPhone);
											home_phone.setPerson(person);
											home_phone.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
											home_phone.save();

											modified = true;
										}
									}

									// update cell phone

									if ((patientCell != null) && (patientCell.length() > 0))
									{
										try
										{
											PhoneNumberBean.getPhoneNumber(person, PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
										}
										catch (ObjectNotFoundException x)
										{
											// no home phone found

											PhoneNumberBean cell_phone = new PhoneNumberBean();
											cell_phone.setNumber(patientCell);
											cell_phone.setPerson(person);
											cell_phone.setType(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
											cell_phone.save();

											modified = true;
										}
									}

									// update work phone

									if ((patientWorkPhone != null) && (patientWorkPhone.length() > 0))
									{
										try
										{
											PhoneNumberBean.getPhoneNumber(person, PhoneNumberBean.WORK_PHONE_NUMBER_TYPE);
										}
										catch (ObjectNotFoundException x)
										{
											// no home phone found

											PhoneNumberBean work_phone = new PhoneNumberBean();
											if ((patientWorkPhoneExt != null) && (patientWorkPhoneExt.length() > 0))
												work_phone.setNumber(patientWorkPhone + "x" + patientWorkPhoneExt);
											else
												work_phone.setNumber(patientWorkPhone);
											work_phone.setPerson(person);
											work_phone.setType(PhoneNumberBean.WORK_PHONE_NUMBER_TYPE);
											work_phone.save();

											modified = true;
										}
									}

									if (modified)
										rows_modified++;
								}
								else if (persons.size() == 0)
								{
									num_no_match++;
									System.out.println("Person not found for " + patientFirstName + " " + patientLastName);

									UKOnlinePersonBean person = new UKOnlinePersonBean();
									person.setFirstName(patientFirstName);
									person.setLastName(patientLastName);
									if (patientActive.length() > 0)
										person.setActive(patientActive.toLowerCase().equals("yes") || patientActive.toLowerCase().equals("y"));
									if (patientFileNumber.length() > 0)
										person.setFileNumber(Integer.parseInt(patientFileNumber));
									else
										person.setFileNumber(UKOnlinePersonBean.getNextFileNumber(adminCompany));
									if ((patientBirthDate != null) && (patientBirthDate.length() > 0))
									{
										try
										{
											person.setBirthDate(this.getDateValueFromCell(patientBirthDate_cell));
										}
										catch (Exception x)
										{
											person.setBirthDate(CUBean.getDateFromUserString(patientBirthDate));
										}
									}
									if ((patientPrefix != null) && (patientPrefix.length() > 0))
										person.setPrefix(patientPrefix);
									if ((patientSuffix != null) && (patientSuffix.length() > 0))
										person.setSuffix(patientSuffix);
									if ((patientEMail != null) && (patientEMail.length() > 0))
										person.setEmail(patientEMail);

									if (patientSex.toLowerCase().equals("m") || patientSex.toLowerCase().equals("male"))
										person.setMale();
									else
										person.setFemale();

									// update address

									if ((patientAddress1 != null) && (patientAddress1.length() > 0))
									{
										AddressBean person_address = new AddressBean();
										person_address.setCity(patientCity);
										person_address.setCompany(adminCompany);
										person_address.setCreateOrModifyPerson(loginBean.getPerson());
										person_address.setState(patientState);
										person_address.setStreet1(patientAddress1);
										person_address.setStreet2(patientAddress2);
										person_address.setType(AddressBean.PERSON_ADDRESS_TYPE);
										person_address.setZipCode(patientZip);
										person_address.save();

										person.addAddress(person_address);
									}

									//String password = PasswordGenerator.getPassword(3) + "-" + PasswordGenerator.getPassword(3);
									
									String password = PasswordGenerator.getPassword(PersonBean.getMinimumPasswordLength());
									boolean isUniquePw = PasswordGenerator.isUnique(password);
									while (!isUniquePw) {
										password = PasswordGenerator.getPassword(PersonBean.getMinimumPasswordLength());
										isUniquePw = PasswordGenerator.isUnique(password);
									}

									if ((patientEMail != null) && (patientEMail.length() > 0))
										person.setUsername(patientEMail);
									else
										person.setUsername((patientFirstName.charAt(0) + patientLastName).toLowerCase());
									person.setPassword(password);
									person.setConfirmPassword(password);


									person.setDepartment(adminCompany.getDefaultDepartment());
									person.setTitle(PersonTitleBean.getDefaultTitle(adminCompany));

									person.setPersonType(UKOnlinePersonBean.CLIENT_PERSON_TYPE);

									person.save();

									this.maintainGroupUnderCare((UKOnlinePersonBean)loginBean.getPerson(), adminCompany, person, patientGroupFileNumber, patientRelationship);


									// update home phone


									if ((patientPhone != null) && (patientPhone.length() > 0))
									{
										PhoneNumberBean home_phone = new PhoneNumberBean();
										home_phone.setNumber(patientPhone);
										home_phone.setPerson(person);
										home_phone.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
										home_phone.save();
									}

									// update cell phone

									if ((patientCell != null) && (patientCell.length() > 0))
									{
										PhoneNumberBean cell_phone = new PhoneNumberBean();
										cell_phone.setNumber(patientCell);
										cell_phone.setPerson(person);
										cell_phone.setType(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
										cell_phone.save();
									}

									// update work phone

									if ((patientWorkPhone != null) && (patientWorkPhone.length() > 0))
									{
										PhoneNumberBean work_phone = new PhoneNumberBean();
										if ((patientWorkPhoneExt != null) && (patientWorkPhoneExt.length() > 0))
											work_phone.setNumber(patientWorkPhone + "x" + patientWorkPhoneExt);
										else
											work_phone.setNumber(patientWorkPhone);
										work_phone.setPerson(person);
										work_phone.setType(PhoneNumberBean.WORK_PHONE_NUMBER_TYPE);
										work_phone.save();
									}

									rows_modified++;
								}
								else
								{
									num_no_unique++;
									System.out.println("Unique person not found for " + patientFirstName + " " + patientLastName);
								}

							}
							catch (ObjectNotFoundException x)
							{

							}

						} catch (Exception x) {
							results.append("Error processing user :" + x.getMessage() + " <br />");
							x.printStackTrace();
						}
					}


				}

				System.out.println("num_matched >" + num_matched);
				System.out.println("num_no_match >" + num_no_match);
				System.out.println("num_no_unique >" + num_no_unique);
				System.out.println("num_found_inactive >" + num_found_inactive);


				results.append(rows_modified + " user" + ((rows_modified == 1) ? "" : "s") + " affected");
			
			}

			session.setAttribute("upload_results", results);
		} catch (IllegalValueException x) {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		} catch (Exception x) {
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(_request, errors);
			return _mapping.getInputForward();
		}

		// Remove the obsolete form bean
		if (_mapping.getAttribute() != null) {
			if ("request".equals(_mapping.getScope())) {
				_request.removeAttribute(_mapping.getAttribute());
			} else {
				session.removeAttribute(_mapping.getAttribute());
			}
		}

		// Forward control to the specified success URI
		return (_mapping.findForward("success"));
	}
	
	private StringBuffer processQuixoteUsers(UKOnlineCompanyBean adminCompany, UKOnlineLoginBean loginBean, HSSFSheet sheet) {
		
		StringBuffer results = new StringBuffer();
		
		int rows_modified = 0;
		int row_index;

		int num_matched = 0;
		int num_no_match = 0;
		int num_no_unique = 0;

		int num_found_inactive = 0;

		for (row_index = 1; row_index < 99999; row_index++) {

			HSSFRow obj_row = sheet.getRow(row_index);
			HSSFRow addr1_obj_row = sheet.getRow(row_index + 2);
			HSSFRow addr2_obj_row = sheet.getRow(row_index + 3);

			if (obj_row == null) {
				break;
			} else {
				try {

					boolean modified = false;

					HSSFCell patientName_cell = obj_row.getCell((short)1);
					String patientName = this.getStringValueFromCell(patientName_cell);
					
					System.out.println("patientName >" + patientName);
					if (patientName != null && (!patientName.isEmpty() && !patientName.toLowerCase().startsWith("patient"))) {
					
					

						//HSSFCell patientFirstName_cell = obj_row.getCell((short) 0);
						//HSSFCell patientLastName_cell = obj_row.getCell((short) 1);
						//HSSFCell patientFileNumber_cell = obj_row.getCell((short) 2);
						//HSSFCell patientEMail_cell = obj_row.getCell((short) 3);
						HSSFCell patientPhone_cell = obj_row.getCell((short) 10);
						HSSFCell patientWork_cell = obj_row.getCell((short) 15);
						HSSFCell patientCell_cell = obj_row.getCell((short) 19);
						
						//HSSFCell patientPrefix_cell = obj_row.getCell((short) 6);
						//HSSFCell patientSuffix_cell = obj_row.getCell((short) 7);
						//HSSFCell patientSex_cell = obj_row.getCell((short) 8);
						//HSSFCell patientRelationship_cell = obj_row.getCell((short) 9);
						//HSSFCell patientGroupFileNumber_cell = obj_row.getCell((short) 10);

						HSSFCell patientAddress1_cell = addr1_obj_row.getCell((short) 4);
						HSSFCell patientAddress2_cell = addr2_obj_row.getCell((short) 4);
						
						//HSSFCell patientCity_cell = obj_row.getCell((short) 13);
						//HSSFCell patientState_cell = obj_row.getCell((short) 14);
						//HSSFCell patientZip_cell = obj_row.getCell((short) 15);

						//HSSFCell patientBirthDate_cell = obj_row.getCell((short) 16);
						//HSSFCell patientWorkPhone_cell = obj_row.getCell((short) 17);
						//HSSFCell patientWorkPhoneExt_cell = obj_row.getCell((short) 18);
						//HSSFCell patientActive_cell = obj_row.getCell((short) 19);


						
						
						/*
						String patientFirstName = this.getStringValueFromCell(patientFirstName_cell);
						String patientLastName = this.getStringValueFromCell(patientLastName_cell);
						String patientFileNumber = this.getStringValueFromCell(patientFileNumber_cell);
						String patientEMail = this.getStringValueFromCell(patientEMail_cell);
						*/
						
						String patientPhone = this.getStringValueFromCell(patientPhone_cell);
						String patientCell = this.getStringValueFromCell(patientCell_cell);
						String patientWork = this.getStringValueFromCell(patientWork_cell);
						
						/*
						String patientPrefix = this.getStringValueFromCell(patientPrefix_cell);
						String patientSuffix = this.getStringValueFromCell(patientSuffix_cell);
						String patientSex = this.getStringValueFromCell(patientSex_cell);
						String patientRelationship = this.getStringValueFromCell(patientRelationship_cell);
						String patientGroupFileNumber = this.getStringValueFromCell(patientGroupFileNumber_cell);
						*/

						String patientAddress1 = this.getStringValueFromCell(patientAddress1_cell);
						String patientAddress2 = this.getStringValueFromCell(patientAddress2_cell);
						
						/*
						String patientCity = this.getStringValueFromCell(patientCity_cell);
						String patientState = this.getStringValueFromCell(patientState_cell);
						String patientZip = this.getStringValueFromCell(patientZip_cell);
						 * 
						 */
						/*
						String patientBirthDate = this.getStringValueFromCell(patientBirthDate_cell);
						String patientWorkPhone = this.getStringValueFromCell(patientWorkPhone_cell);
						String patientWorkPhoneExt = this.getStringValueFromCell(patientWorkPhoneExt_cell);
						String patientActive = this.getStringValueFromCell(patientActive_cell);
						 * 
						 */

						System.out.println("");
						System.out.println("row >" + row_index + "<");
						//System.out.println("first_name >" + patientFirstName + "<");
						//System.out.println("last_name >" + patientLastName + "<");
						//System.out.println("patientFileNumber >" + patientFileNumber + "<");
						//System.out.println("patientEMail >" + patientEMail + "<");
						System.out.println("patientPhone >" + patientPhone + "<");
						System.out.println("patientCell >" + patientCell + "<");
						//System.out.println("patientBirthDate >" + patientBirthDate + "<");
						//System.out.println("patientActive >" + patientActive + "<");

						/*
						try
						{
							// ensure that one of home phone, cell phone, or email address has been provided

							if (patientFirstName.length() == 0 || patientLastName.length() == 0)
								throw new IllegalValueException("You must provide a first and last name.");

							if (patientEMail.length() == 0 && patientPhone.length() == 0 && patientCell.length() == 0)
								throw new IllegalValueException("You must provide at least one of home phone, cell phone, or email address.");

							Vector persons = new Vector();

							if ((patientPhone != null) && (patientPhone.length() > 0))
								persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(adminCompany, patientLastName, patientFirstName, patientPhone, false);

							if (persons.size() != 1)
							{
								if ((patientCell != null) && (patientCell.length() > 0))
									persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(adminCompany, patientLastName, patientFirstName, patientCell, false);
							}

							if (persons.size() != 1)
							{
								if ((patientEMail != null) && (patientEMail.length() > 0))
									persons = UKOnlinePersonBean.getPersonsByLastNameFirstNameEmail(adminCompany, patientLastName, patientFirstName, patientEMail, false);
							}

							if (persons.size() != 1)
								persons = UKOnlinePersonBean.getPersonsByLastNameFirstName(adminCompany, patientLastName, patientFirstName);

							if (persons.size() == 1)
							{
								num_matched++;
								System.out.println("Person found for " + patientFirstName + " " + patientLastName + " --- >" + patientActive);


								// found a unique person

								UKOnlinePersonBean person = (UKOnlinePersonBean)persons.get(0);

								if (patientActive.toLowerCase().equals("no") || patientActive.toLowerCase().equals("n"))
								{
									if (person.isActive())
									{
										num_found_inactive++;

										person.setActive(false);
									}
								}
								else
								{
									if (!person.isActive())
										person.setActive(true);
								}


								// update file number

								if (person.getFileNumberString().length() == 0)
								{
									// no existing file number.  update from spreadsheet

									person.setFileNumber(Integer.parseInt(patientFileNumber));
									modified = true;
								}

								// update birth date

								if ((patientBirthDate != null) && (patientBirthDate.length() > 0))
								{
									try
									{
										person.setBirthDate(this.getDateValueFromCell(patientBirthDate_cell));
										modified = true;
									}
									catch (Exception x)
									{
										person.setBirthDate(CUBean.getDateFromUserString(patientBirthDate));
										modified = true;
									}
								}

								// update prefix

								if ((person.getPrefixString().length() == 0) && (patientPrefix != null) && (patientPrefix.length() > 0))
								{
									person.setPrefix(patientPrefix);
									modified = true;
								}

								// update suffix

								if ((person.getSuffixString().length() == 0) && (patientSuffix != null) && (patientSuffix.length() > 0))
								{
									person.setSuffix(patientSuffix);
									modified = true;
								}

								// update email

								if ((person.getEmailString().length() == 0) && (patientEMail != null) && (patientEMail.length() > 0))
								{
									person.setEmail(patientEMail);
									modified = true;
								}

								// update sex

								boolean ss_male = patientSex.toLowerCase().equals("m") || patientSex.toLowerCase().equals("male");
								if (person.isMale() != ss_male)
								{
									if (ss_male)
										person.setMale();
									else
										person.setFemale();
									modified = true;
								}

								// update address

								if ((patientAddress1 != null) && (patientAddress1.length() > 0))
								{
									AddressBean person_address = null;
									boolean modify_address = true;

									try
									{
										person_address = AddressBean.getAddress(person, AddressBean.PERSON_ADDRESS_TYPE);
										if (person_address.getStreet1String().equals(patientAddress1))
											modify_address = false;
									}
									catch (ObjectNotFoundException x)
									{
										person_address = new AddressBean();
									}

									if (modify_address)
									{
										person_address.setCity(patientCity);
										person_address.setCompany(adminCompany);
										person_address.setCreateOrModifyPerson(loginBean.getPerson());
										person_address.setState(patientState);
										person_address.setStreet1(patientAddress1);
										person_address.setStreet2(patientAddress2);
										person_address.setType(AddressBean.PERSON_ADDRESS_TYPE);
										person_address.setZipCode(patientZip);
										person_address.save();

										person.addAddress(person_address);

										modified = true;
									}
								}

								if (modified)
									person.save();

								this.maintainGroupUnderCare((UKOnlinePersonBean)loginBean.getPerson(), adminCompany, person, patientGroupFileNumber, patientRelationship);

								// update home phone


								if ((patientPhone != null) && (patientPhone.length() > 0))
								{
									try
									{
										PhoneNumberBean.getPhoneNumber(person, PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
									}
									catch (ObjectNotFoundException x)
									{
										// no home phone found

										PhoneNumberBean home_phone = new PhoneNumberBean();
										home_phone.setNumber(patientPhone);
										home_phone.setPerson(person);
										home_phone.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
										home_phone.save();

										modified = true;
									}
								}

								// update cell phone

								if ((patientCell != null) && (patientCell.length() > 0))
								{
									try
									{
										PhoneNumberBean.getPhoneNumber(person, PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
									}
									catch (ObjectNotFoundException x)
									{
										// no home phone found

										PhoneNumberBean cell_phone = new PhoneNumberBean();
										cell_phone.setNumber(patientCell);
										cell_phone.setPerson(person);
										cell_phone.setType(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
										cell_phone.save();

										modified = true;
									}
								}

								// update work phone

								if ((patientWorkPhone != null) && (patientWorkPhone.length() > 0))
								{
									try
									{
										PhoneNumberBean.getPhoneNumber(person, PhoneNumberBean.WORK_PHONE_NUMBER_TYPE);
									}
									catch (ObjectNotFoundException x)
									{
										// no home phone found

										PhoneNumberBean work_phone = new PhoneNumberBean();
										if ((patientWorkPhoneExt != null) && (patientWorkPhoneExt.length() > 0))
											work_phone.setNumber(patientWorkPhone + "x" + patientWorkPhoneExt);
										else
											work_phone.setNumber(patientWorkPhone);
										work_phone.setPerson(person);
										work_phone.setType(PhoneNumberBean.WORK_PHONE_NUMBER_TYPE);
										work_phone.save();

										modified = true;
									}
								}

								if (modified)
									rows_modified++;
							}
							else if (persons.size() == 0)
							{
								num_no_match++;
								System.out.println("Person not found for " + patientFirstName + " " + patientLastName);

								UKOnlinePersonBean person = new UKOnlinePersonBean();
								person.setFirstName(patientFirstName);
								person.setLastName(patientLastName);
								if (patientActive.length() > 0)
									person.setActive(patientActive.toLowerCase().equals("yes") || patientActive.toLowerCase().equals("y"));
								if (patientFileNumber.length() > 0)
									person.setFileNumber(Integer.parseInt(patientFileNumber));
								else
									person.setFileNumber(UKOnlinePersonBean.getNextFileNumber(adminCompany));
								if ((patientBirthDate != null) && (patientBirthDate.length() > 0))
								{
									try
									{
										person.setBirthDate(this.getDateValueFromCell(patientBirthDate_cell));
									}
									catch (Exception x)
									{
										person.setBirthDate(CUBean.getDateFromUserString(patientBirthDate));
									}
								}
								if ((patientPrefix != null) && (patientPrefix.length() > 0))
									person.setPrefix(patientPrefix);
								if ((patientSuffix != null) && (patientSuffix.length() > 0))
									person.setSuffix(patientSuffix);
								if ((patientEMail != null) && (patientEMail.length() > 0))
									person.setEmail(patientEMail);

								if (patientSex.toLowerCase().equals("m") || patientSex.toLowerCase().equals("male"))
									person.setMale();
								else
									person.setFemale();

								// update address

								if ((patientAddress1 != null) && (patientAddress1.length() > 0))
								{
									AddressBean person_address = new AddressBean();
									person_address.setCity(patientCity);
									person_address.setCompany(adminCompany);
									person_address.setCreateOrModifyPerson(loginBean.getPerson());
									person_address.setState(patientState);
									person_address.setStreet1(patientAddress1);
									person_address.setStreet2(patientAddress2);
									person_address.setType(AddressBean.PERSON_ADDRESS_TYPE);
									person_address.setZipCode(patientZip);
									person_address.save();

									person.addAddress(person_address);
								}

								String password = PasswordGenerator.getPassword(3) + "-" + PasswordGenerator.getPassword(3);

								if ((patientEMail != null) && (patientEMail.length() > 0))
									person.setUsername(patientEMail);
								else
									person.setUsername((patientFirstName.charAt(0) + patientLastName).toLowerCase());
								person.setPassword(password);
								person.setConfirmPassword(password);


								person.setDepartment(adminCompany.getDefaultDepartment());
								person.setTitle(PersonTitleBean.getDefaultTitle(adminCompany));

								person.setPersonType(UKOnlinePersonBean.CLIENT_PERSON_TYPE);

								person.save();

								this.maintainGroupUnderCare((UKOnlinePersonBean)loginBean.getPerson(), adminCompany, person, patientGroupFileNumber, patientRelationship);


								// update home phone


								if ((patientPhone != null) && (patientPhone.length() > 0))
								{
									PhoneNumberBean home_phone = new PhoneNumberBean();
									home_phone.setNumber(patientPhone);
									home_phone.setPerson(person);
									home_phone.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
									home_phone.save();
								}

								// update cell phone

								if ((patientCell != null) && (patientCell.length() > 0))
								{
									PhoneNumberBean cell_phone = new PhoneNumberBean();
									cell_phone.setNumber(patientCell);
									cell_phone.setPerson(person);
									cell_phone.setType(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
									cell_phone.save();
								}

								// update work phone

								if ((patientWorkPhone != null) && (patientWorkPhone.length() > 0))
								{
									PhoneNumberBean work_phone = new PhoneNumberBean();
									if ((patientWorkPhoneExt != null) && (patientWorkPhoneExt.length() > 0))
										work_phone.setNumber(patientWorkPhone + "x" + patientWorkPhoneExt);
									else
										work_phone.setNumber(patientWorkPhone);
									work_phone.setPerson(person);
									work_phone.setType(PhoneNumberBean.WORK_PHONE_NUMBER_TYPE);
									work_phone.save();
								}

								rows_modified++;
							}
							else
							{
								num_no_unique++;
								System.out.println("Unique person not found for " + patientFirstName + " " + patientLastName);
							}

						}
						catch (ObjectNotFoundException x)
						{

						}
						*/
					}

				} catch (Exception x) {
					results.append("Error processing user :" + x.getMessage() + " <br />");
					x.printStackTrace();
				}
			}


		}

		System.out.println("num_matched >" + num_matched);
		System.out.println("num_no_match >" + num_no_match);
		System.out.println("num_no_unique >" + num_no_unique);
		System.out.println("num_found_inactive >" + num_found_inactive);


		results.append(rows_modified + " user" + ((rows_modified == 1) ? "" : "s") + " affected");
		
		return results;
	}

	private String getStringValueFromCell(HSSFCell _cell) {
		if (_cell == null) {
			return "";
		}
		String str = null;
		if (_cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			str = (int) _cell.getNumericCellValue() + "";
		} else {
			str = _cell.getStringCellValue();
		}

		return str;
	}

	private Date getDateValueFromCell(HSSFCell _cell) {
		return _cell.getDateCellValue();
	}
	
	private void
	maintainGroupUnderCare(UKOnlinePersonBean _modify_person, CompanyBean _company, UKOnlinePersonBean _person_to_move, String _move_to_file_number, String _relationshipSelect)
		throws TorqueException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
	{
			/*
	<select name="relationshipSelect">
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_SELF_TYPE %>">Self</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_SPOUSE_TYPE %>">Spouse</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_CHILD_TYPE %>">Child</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_GUARDIAN_TYPE %>">Guardian</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_PARENT_TYPE %>">Parent</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_PARTNER_TYPE %>">Partner</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_OTHER_TYPE %>">Other</option>
	</select>
			 */
		
		short relationship_type = GroupUnderCareBean.RELATIONSHIP_OTHER_TYPE;
		if ((_relationshipSelect != null) && (_relationshipSelect.length() > 0))
		{
			if (_relationshipSelect.toLowerCase().equals("self"))
				relationship_type = GroupUnderCareBean.RELATIONSHIP_SELF_TYPE;
			else if (_relationshipSelect.toLowerCase().equals("spouse"))
				relationship_type = GroupUnderCareBean.RELATIONSHIP_SPOUSE_TYPE;
			else if (_relationshipSelect.toLowerCase().equals("child"))
				relationship_type = GroupUnderCareBean.RELATIONSHIP_CHILD_TYPE;
			else if (_relationshipSelect.toLowerCase().equals("guardian"))
				relationship_type = GroupUnderCareBean.RELATIONSHIP_GUARDIAN_TYPE;
			else if (_relationshipSelect.toLowerCase().equals("parent"))
				relationship_type = GroupUnderCareBean.RELATIONSHIP_PARENT_TYPE;
			else if (_relationshipSelect.toLowerCase().equals("partner"))
				relationship_type = GroupUnderCareBean.RELATIONSHIP_PARTNER_TYPE;
			else if (_relationshipSelect.toLowerCase().equals("other"))
				relationship_type = GroupUnderCareBean.RELATIONSHIP_OTHER_TYPE;
			else
				throw new IllegalValueException("Valid relationships include Self, Spouse, Child, Guardian, Parent, Partner, or Other.");
		}
			
		GroupUnderCareBean current_group_under_care = null;
		try
		{
			current_group_under_care = _person_to_move.getGroupUnderCare();
		}
		catch (ObjectNotFoundException x)
		{
		}

		Vector persons_with_file_number = UKOnlinePersonBean.getPersonsByFileNumber(_company, _move_to_file_number);
		if (persons_with_file_number.size() == 0)
			throw new IllegalValueException("Unable to locate group under care for file number " + _move_to_file_number);
		
		UKOnlinePersonBean group_person = (UKOnlinePersonBean)persons_with_file_number.get(0);
		GroupUnderCareBean new_group_under_care = group_person.getGroupUnderCare();

		if ((current_group_under_care == null) || (current_group_under_care.getId() != new_group_under_care.getId()))
		{
			GroupUnderCareMember member = null;

			if (current_group_under_care != null)
			{
				Vector current_members = current_group_under_care.getMembers();
				for (int i = 0; i < current_members.size(); i++)
				{
					GroupUnderCareMember member_obj = (GroupUnderCareMember)current_members.elementAt(i);
					if (member_obj.getPersonId() == _person_to_move.getId())
					{
						member = member_obj;
						current_members.removeElementAt(i);
						current_group_under_care.setGroupUnderCareMembers(current_members);
						current_group_under_care.setModifyPerson(_modify_person);
						current_group_under_care.save();
						current_group_under_care.invalidate();
						break;
					}
				}

				if (member == null)
					throw new IllegalValueException("Existing group membership not found...");
			}

			member = new GroupUnderCareMember();
			member.setPersonId(_person_to_move.getId());
			member.setRelationshipToPrimaryClient(relationship_type);
			
			new_group_under_care.setModifyPerson(_modify_person);
			Vector members = new_group_under_care.getMembers();
			members.addElement(member);
			new_group_under_care.setGroupUnderCareMembers(members);

			new_group_under_care.save();
			new_group_under_care.invalidate();

			_person_to_move.invalidateGroup();

		}
		else if (current_group_under_care != null)
		{
			//System.out.println("changing relationship stuff");

			if (current_group_under_care.getId() == new_group_under_care.getId())
			{
				// am I changing the relationship?

				//System.out.println("am I changing the relationship?");

				GroupUnderCareMember member = null;

				Vector current_members = current_group_under_care.getMembers();
				for (int i = 0; i < current_members.size(); i++)
				{
					GroupUnderCareMember member_obj = (GroupUnderCareMember)current_members.elementAt(i);
					if (member_obj.getPersonId() == _person_to_move.getId())
					{
						member = member_obj;

						//System.out.println("member_obj.getRelationshipToPrimaryClient() >" + member_obj.getRelationshipToPrimaryClient());
						//System.out.println("relationshipSelect.shortValue() >" + relationshipSelect.shortValue());

						if (member_obj.getRelationshipToPrimaryClient() != relationship_type)
						{
							member_obj.setRelationshipToPrimaryClient(relationship_type);
							member_obj.save();

							current_group_under_care.setModifyPerson(_modify_person);
							current_group_under_care.save();
							current_group_under_care.invalidate();
						}
						break;
					}
				}

				if (member == null)
					throw new IllegalValueException("Existing group membership not found...");
			}
		}
	}
	
	public static synchronized StringBuffer
	uploadFromBBBYXLSXFile(CompanyBean adminCompany, String destination_file_path, boolean _test, PersonBean _create_person)
		throws Exception
	{	
		StringBuffer results = new StringBuffer();
		
		//UKOnlineAccessManager am = (UKOnlineAccessManager)UKOnlineAccessManager.getInstance();
		
		int sheet_index = 0;
		int row_index;
		
		FileInputStream file = new FileInputStream(destination_file_path);
		XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFSheet sheet = wb.getSheetAt(sheet_index);
		
		try {
			for (row_index = 1; row_index < 99999; row_index++) {

				XSSFRow obj_row = sheet.getRow(row_index);

				if (obj_row == null) {
					break;
				} else {
					try {

						String storeNum = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)0)).trim();
						String storeName = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)1)).trim();
						String state = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)2)).trim();
						String address1 = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)3)).trim();
						String address2 = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)4)).trim();
						String city = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)5)).trim();
						String zip = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)6)).trim();
						String phone = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)7)).trim();
						String concept = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)8)).trim();
						String region = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)9)).trim();
						String rvp = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)10)).trim();
						String rdlp = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)11)).trim();
						String rdlp_email = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)12)).trim();
						String rlp = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)13)).trim();
						String rlpm_email = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)14)).trim();
						String alp = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)15)).trim();
						String alpm_email = ClientUploadAction.getStringValueFromCell(obj_row.getCell((short)16)).trim();

						/*
						System.out.println("storeNum >" + storeNum);
						System.out.println("storeName >" + storeName);
						System.out.println("state >" + state);
						System.out.println("address1 >" + address1);
						System.out.println("address2 >" + address2);
						System.out.println("city >" + city);
						System.out.println("zip >" + zip);
						System.out.println("phone >" + phone);
						System.out.println("concept >" + concept);
						System.out.println("region >" + region);
						System.out.println("rvp >" + rvp);
						System.out.println("rdlp >" + rdlp);
						System.out.println("rdlp_email >" + rdlp_email);
						System.out.println("rlp >" + rlp);
						System.out.println("rlpm_email >" + rlpm_email);
						System.out.println("alp >" + alp);
						System.out.println("alpm_email >" + alpm_email);
						*/
						
						if (!storeNum.isEmpty()) {
							DepartmentBean concept_obj = DepartmentBean.maintainDepartment(adminCompany, DepartmentTypeBean.maintainDepartmentType(adminCompany, "Concept"), concept);
							DepartmentBean region_obj = DepartmentBean.maintainDepartment(adminCompany, DepartmentTypeBean.maintainDepartmentType(adminCompany, "Region"), region);
							region_obj.setParent(concept_obj);
							region_obj.save();

							if (!storeName.isEmpty()) {
								DepartmentBean store_obj = DepartmentBean.maintainDepartment(adminCompany, DepartmentTypeBean.maintainDepartmentType(adminCompany, "Store"), Integer.parseInt(storeNum), storeName);
								AddressBean store_address = AddressBean.maintainAddress(adminCompany, address1, address2, city, state, zip, _create_person);
								store_obj.setParent(region_obj);
								store_obj.addAddress(store_address);
								store_obj.save();
							}

							PersonTitleBean vplp_title = PersonTitleBean.maintainPersonTitle(adminCompany, "Vice President of Loss Prevention");
							vplp_title.setShortNameString("VPLP");
							vplp_title.save();

							PersonTitleBean dlp_title = PersonTitleBean.maintainPersonTitle(adminCompany, "Director of Loss Prevention");
							dlp_title.setShortNameString("DLP");
							dlp_title.save();

							PersonTitleBean alpm_title = PersonTitleBean.maintainPersonTitle(adminCompany, "Area Loss Prevention Manager");
							alpm_title.setShortNameString("ALPM");
							alpm_title.save();

							PersonTitleBean rvp_title = PersonTitleBean.maintainPersonTitle(adminCompany, "Regional Vice President");
							rvp_title.setShortNameString("RVP");
							rvp_title.save();

							PersonTitleBean rlpd_title = PersonTitleBean.maintainPersonTitle(adminCompany, "Regional Loss Prevention Director");
							rlpd_title.setShortNameString("RLPD");
							rlpd_title.save();

							PersonTitleBean rlpm_title = PersonTitleBean.maintainPersonTitle(adminCompany, "Regional Loss Prevention Manager");
							rlpm_title.setShortNameString("RLPM");
							rlpm_title.save();

							PersonGroupBean.maintainGroup(adminCompany, "[DEFAULT]");

							if (!rdlp.isEmpty() && !rdlp_email.isEmpty()) {
								int first_space_index = rdlp.indexOf(' ');
								String firstStr = rdlp.substring(0, first_space_index);
								String lastStr = rdlp.substring(first_space_index + 1);
								ClientUploadAction.maintainPerson(adminCompany, lastStr, firstStr, rdlp_email, concept_obj, rlpd_title);
							}

							if (!rlp.isEmpty() && !rlpm_email.isEmpty()) {
								int first_space_index = rlp.indexOf(' ');
								String firstStr = rlp.substring(0, first_space_index);
								String lastStr = rlp.substring(first_space_index + 1);
								ClientUploadAction.maintainPerson(adminCompany, lastStr, firstStr, rlpm_email, concept_obj, rlpm_title);
							}

							if (!alp.isEmpty() && !alpm_email.isEmpty()) {
								int first_space_index = alp.indexOf(' ');
								String firstStr = alp.substring(0, first_space_index);
								String lastStr = alp.substring(first_space_index + 1);
								ClientUploadAction.maintainPerson(adminCompany, lastStr, firstStr, alpm_email, concept_obj, alpm_title);
							}
						}
						

					} catch (Exception x) {
						results.append("Error processing user :" + x.getMessage() + " <br />");
						x.printStackTrace();
					}
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		

		
		return results;
	}

    public static String
    getStringValueFromCell(XSSFCell _cell)
    {
		if (_cell == null)
			return "";
		String str = null;
		if (_cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			str = (int)_cell.getNumericCellValue() + "";
		} else if (_cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
			str = (boolean)_cell.getBooleanCellValue() + "";
		} else {
			str = _cell.getStringCellValue();
		}

		return str;
    }

    public static String
    getStringValueFromFloatCell(XSSFCell _cell)
    {
		if (_cell == null)
			return "";
		String str = null;
		if (_cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			str = _cell.getNumericCellValue() + "";
		} else {
			str = _cell.getStringCellValue();
		}

		return str;
    }

    public static BigDecimal
    getBigDecimalValueFromCell(XSSFCell _cell)
    {
		if (_cell == null)
			return BigDecimal.ZERO;
		BigDecimal bd = null;
		if (_cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			bd = new BigDecimal(_cell.getNumericCellValue());
		} else {
			bd = new BigDecimal(_cell.getStringCellValue());
		}
		return bd;
    }

    public static Date
    getDateValueFromCell(XSSFCell _cell)
    {
		if (_cell == null)
			return null;
		try {
			return CUBean.getDateFromUserString(ClientUploadAction.getStringValueFromCell(_cell), "MM/dd/yyyy");
		} catch (java.text.ParseException x) {
			return _cell.getDateCellValue();
		}
    }
	
	private static void
	maintainPerson(CompanyBean company, String lastStr, String firstStr, String email, DepartmentBean department, PersonTitleBean jobTitle) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {

		System.out.println("BBBY first >" + firstStr + " last >" + lastStr + " -- email >" + email);
		
		if (email.length() > 0) {

			UKOnlinePersonBean bbby_person = null;
			try {
				bbby_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPersonByEmail(email);
			} catch (ObjectNotFoundException x) {
				bbby_person = new UKOnlinePersonBean();
				//bbby_person.setEmployeeId(empNum);
				bbby_person.setFirstName(firstStr);
				bbby_person.setLastName(lastStr);
				bbby_person.setEmail1(email);
				bbby_person.setUsername(email);
				String new_password = PasswordGenerator.getPassword(3) + "-" + PasswordGenerator.getPassword(3);
				bbby_person.setPassword(new_password);
				bbby_person.setConfirmPassword(new_password);

				bbby_person.setTitle(jobTitle);
				bbby_person.setDepartment(department);
				bbby_person.save();

				try {
					bbby_person.setGroup(PersonGroupBean.getDefaultPersonGroup(company));
					bbby_person.save();
				} catch (ObjectAlreadyExistsException y) {
					
				}
			}
		}
	}
	
}