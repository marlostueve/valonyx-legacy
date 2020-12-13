/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbpos;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.PasswordGenerator;
import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;

import java.io.IOException;
import java.io.StringReader;

import java.text.SimpleDateFormat;

import java.util.*;
import java.math.BigDecimal;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.torque.*;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.valeo.qbpos.data.*;

/**
 *
 * @author marlo
 */
public class
QBPOSXMLResponseParser
	extends DefaultHandler
{
	// CLASS VARIABLES
	
	private static SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat time_modified_date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			// 2008-10-15T07:41:47-05:00
			// 2008-11-08T09:53:34-06:00
	
	// INSTANCE VARIABLES
	
	private UKOnlineCompanyBean company;
	
	private SalesReceiptQueryRs salesReceiptQueryRsObject;
	private SalesReceiptRet salesReceiptRetObject;
	private SalesReceiptItemRet salesReceiptItemRetObject;
	private TenderRet tenderRetObject;

	private CustomerAddRs customerAddRsObject;
	private CustomerQueryRs customerQueryRsObject;
	private CustomerRet customerRetObject;
	private HashMap address_hash = new HashMap(7);
	//private AddressBean customerRetBillAddress;

	private String current_element;

	private Vector previous_unprocessed_sales_receipts;
	private Vector previous_unprocessed_customers;

	private Vector unprocessed_sales_receipts = new Vector();
	private Vector unprocessed_customers = new Vector();
	
	// CONSTRUCTORS
	
	public
	QBPOSXMLResponseParser(UKOnlineCompanyBean _company, Vector _previous_unprocessed_sales_receipts, Vector _previous_unprocessed_customers)
	{
		company = _company;
		previous_unprocessed_sales_receipts = _previous_unprocessed_sales_receipts;
		previous_unprocessed_customers = _previous_unprocessed_customers;

		if (previous_unprocessed_sales_receipts != null)
			System.out.println("previous_unprocessed_sales_receipts size >" + previous_unprocessed_sales_receipts.size());
		if (previous_unprocessed_customers != null)
			System.out.println("previous_unprocessed_customers size >" + previous_unprocessed_customers.size());
	}
	
	// INSTANCE METHODS
	
	public Vector
	getUnprocessedCustomers()
	{
		return unprocessed_customers;
	}

	public Vector
	getUnprocessedSalesReceipts()
	{
		return unprocessed_sales_receipts;
	}
	
	public void
	parse(String _response)
	{
		System.out.println("parse invoked >" + this);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			//parse the file and also register this class for call backs
			sp.parse(new InputSource(new StringReader(_response)), this);



			if ((customerQueryRsObject != null) || (customerAddRsObject != null))
			{
				if (customerQueryRsObject != null)
					System.out.println("NUM customerQueryRsObject >" + customerQueryRsObject.getCustomerRetObjects().size());
				else
					System.out.println("NUM customerAddRsObject >" + customerAddRsObject.getCustomerRetObjects().size());

				/*
				Iterator itr;
				if (customerQueryRsObject != null)
					itr = customerQueryRsObject.getCustomerRetObjects().iterator();
				else
					itr = customerAddRsObject.getCustomerRetObjects().iterator();
				 */

				for (int i = 0; i < 2; i++)
				{
					Iterator itr = null;
					if (i == 0)
					{
						if (customerQueryRsObject != null)
							itr = customerQueryRsObject.getCustomerRetObjects().iterator();
					}
					else
					{
						if (customerAddRsObject != null)
							itr = customerAddRsObject.getCustomerRetObjects().iterator();
					}

					if (itr != null)
					{
						while (itr.hasNext())
						{
							try
							{
								CustomerRet obj = (CustomerRet)itr.next();
								if (customerQueryRsObject != null)
									obj.setRequestId(customerQueryRsObject.getRequestID());
								else
									obj.setRequestId(customerAddRsObject.getRequestID());

								obj.setCompany(company);

								try
								{
									CustomerRet existing_customer_ret = CustomerRet.getCustomer(company, obj.getListID());
									existing_customer_ret.setAccountBalance(obj.getAccountBalance());
									existing_customer_ret.setEMail(obj.getEMail());
									existing_customer_ret.setFirstName((obj.getFirstName() == null) ? "" : obj.getFirstName());
									existing_customer_ret.setFullName(obj.getFullName());
									existing_customer_ret.setLastName(obj.getLastName());
									existing_customer_ret.setLastSaleDate(obj.getLastSaleDate());
									existing_customer_ret.setPhone(obj.getPhone());
									existing_customer_ret.setPhone2(obj.getPhone2());
									existing_customer_ret.setPriceLevelNumber(obj.getPriceLevelNumber());
									existing_customer_ret.setRequestId(obj.getRequestId());
									existing_customer_ret.setTimeModified(obj.getTimeModified());
									existing_customer_ret.save();
								}
								catch (ObjectNotFoundException x)
								{
									obj.save();
								}

								// find the corresponding Person object

								boolean updated = false;
								UKOnlinePersonBean pos_person = null;

								try
								{
									pos_person = UKOnlinePersonBean.getPersonByEmployeeId(company, obj.getListID());

									// found a person in the db with a corresponding list id.  update some stuff

									if ((obj.getFirstName() != null) && !pos_person.getFirstNameString().equals(obj.getFirstName()))
									{
										pos_person.setFirstName(obj.getFirstName());
										updated = true;
									}
									if (!pos_person.getLastNameString().equals(obj.getLastName()))
									{
										pos_person.setLastName(obj.getLastName());
										updated = true;
									}
									if ((obj.getEMail() != null) && (obj.getEMail().length() > 0))
									{
										if (!pos_person.getEmailString().equals(obj.getEMail()))
										{
											pos_person.setEmail(obj.getEMail());
											updated = true;
										}
									}

									AddressBean customerRetBillAddress = (AddressBean)address_hash.get(obj);
									System.out.println("ADDRESS FOUND for " + pos_person.getLabel() + " >" + customerRetBillAddress);
									updated = this.updateAddress(company, pos_person, customerRetBillAddress);

									if (updated)
										pos_person.save();

									this.updatePhone(pos_person, obj.getPhone(), PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
									this.updatePhone(pos_person, obj.getPhone2(), PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
								}
								catch (ObjectNotFoundException x)
								{
									// no existing person found in the db with the list ID supplied by PoS
									// see if this person conflicts with any users in the db...

									Vector persons = new Vector();

									if ((obj.getPhone() != null) && (obj.getPhone().length() > 0))
										persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(company, obj.getLastName(), obj.getFirstName(), obj.getPhone(), false);

									if (persons.size() == 0)
									{
										if ((obj.getPhone2() != null) && (obj.getPhone2().length() > 0))
											persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(company, obj.getLastName(), obj.getFirstName(), obj.getPhone2(), false);
									}

									if (persons.size() == 0)
									{
										if ((obj.getEMail() != null) && (obj.getEMail().length() > 0))
											persons = UKOnlinePersonBean.getPersonsByLastNameFirstNameEmail(company, obj.getLastName(), obj.getFirstName(), obj.getEMail(), false);
									}

									if (persons.size() == 0)
									{
										// nobody with list ID and no similar names with email match or partial phone match.  new person from PoS, I guess

										pos_person = new UKOnlinePersonBean();

										pos_person.setFirstName((obj.getFirstName() == null) ? "" : obj.getFirstName());
										pos_person.setLastName(obj.getLastName());
										pos_person.setEmployeeId(obj.getListID());

										//String password = PasswordGenerator.getPassword(3) + "-" + PasswordGenerator.getPassword(3);
									
										String password = PasswordGenerator.getPassword(PersonBean.getMinimumPasswordLength());
										boolean isUniquePw = PasswordGenerator.isUnique(password);
										while (!isUniquePw) {
											password = PasswordGenerator.getPassword(PersonBean.getMinimumPasswordLength());
											isUniquePw = PasswordGenerator.isUnique(password);
										}
									
										pos_person.setUsername(password);
										pos_person.setPassword(password);
										pos_person.setConfirmPassword(password);
										pos_person.setDepartment(DepartmentBean.getDepartment(1));
										pos_person.setTitle(PersonTitleBean.getPersonTitle(1));

										AddressBean customerRetBillAddress = (AddressBean)address_hash.get(obj);
										System.out.println("ADDRESS FOUND for " + pos_person.getLabel() + " >" + customerRetBillAddress);
										this.updateAddress(company, pos_person, customerRetBillAddress);

										pos_person.save();

										this.updatePhone(pos_person, obj.getPhone(), PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
										this.updatePhone(pos_person, obj.getPhone2(), PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);

										pos_person.setGroup(PersonGroupBean.getPersonGroup(1));

										// create a new group under care for this person

										GroupUnderCareBean group_under_care = new GroupUnderCareBean();
										group_under_care.setCompany(company);
										group_under_care.setPrimaryClient(pos_person);
										Vector members = new Vector();
										GroupUnderCareMember member = new GroupUnderCareMember();
										member.setPersonId(pos_person.getId());
										member.setRelationshipToPrimaryClient(GroupUnderCareBean.RELATIONSHIP_SELF_TYPE);
										member.setGroupUnderCareMemberTypeId(GroupUnderCareBean.RELATIONSHIP_SELF_TYPE);
										members.addElement(member);
										group_under_care.setGroupUnderCareMembers(members);
										group_under_care.setNote("");
										group_under_care.save();

										CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "New person from POS",
											"New person created >" + obj.getFirstName() + " " + obj.getLastName() + " - (" + obj.getPhone() + ")");
									}
									else if (persons.size() == 1)
									{
										// found a unique person with similar name and either email match or partial phone match
										// grab this person and update with infoz from PoS

										pos_person = (UKOnlinePersonBean)persons.get(0);

										boolean list_id_change = false;
										if ((obj.getListID() != null) && (obj.getListID().length() > 0))
										{
											if (!pos_person.getEmployeeNumberString().equals(obj.getListID()))
											{
												pos_person.setEmployeeId(obj.getListID());
												updated = true;

												System.out.println(pos_person.getLabel() + " list_id_change!?!?! >");
											}
										}

										if ((obj.getEMail() != null) && (obj.getEMail().length() > 0))
										{
											if (!pos_person.getEmailString().equals(obj.getEMail()))
											{
												pos_person.setEmail(obj.getEMail());
												updated = true;
											}
										}

										if (updated)
											pos_person.save();

										this.updatePhone(pos_person, obj.getPhone(), PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
										this.updatePhone(pos_person, obj.getPhone2(), PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
									}
									else
									{
										StringBuffer person_buf = new StringBuffer();

										Iterator person_itr = persons.iterator();
										while (person_itr.hasNext())
										{
											UKOnlinePersonBean person_obj = (UKOnlinePersonBean)person_itr.next();
											person_buf.append(person_obj.getFullName() + "\n");
										}

										CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "Failed to add Person(s) from POS (" + persons.size() + " conflicts)",
											"The existing entries caused a conflict with >" + obj.getFirstName() + " " + obj.getLastName() + " - (" + obj.getPhone() + ") :\n" +
											person_buf.toString());
									}
								}
							}
							catch (ObjectAlreadyExistsException x)
							{
								x.printStackTrace();
							}

						}

					}

				}
			}

			if (salesReceiptQueryRsObject != null)
			{
				System.out.println("NUM SalesRecieptRetObjects >" + salesReceiptQueryRsObject.getSalesReceiptRetObjects().size());
			
				Iterator itr = salesReceiptQueryRsObject.getSalesReceiptRetObjects().iterator();
				while (itr.hasNext())
				{
					try
					{
						SalesReceiptRet obj = (SalesReceiptRet)itr.next();

						// does a corresponding sales receipt already exist?
						SalesReceiptRet existing_ret = null;
						System.out.println("searching for SalesReceipt for id >" + obj.getSalesReceiptNumber());
						try
						{
							// search using sales receipt number
							existing_ret = SalesReceiptRet.getSalesReceipt(company, obj.getSalesReceiptNumber());
							System.out.println("receipt found!");

							// search using txn id
							
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("receipt not found!");

							
							try
							{
								existing_ret = SalesReceiptRet.getSalesReceipt(company, obj.getTxnID());
							}
							catch (ObjectNotFoundException y)
							{
								System.out.println("receipt still not found!?!");
							}
							
						}

						if (existing_ret == null)
						{
							// new transaction, I guess

							obj.setRequestId(salesReceiptQueryRsObject.getRequestID());
							obj.setCompany(company);
							obj.save();
						}
						else
						{
							// transaction already exists.  update...

							try
							{
								existing_ret.setAssociate(obj.getAssociate());
							}
							catch (ObjectNotFoundException associate_not_found)
							{
								associate_not_found.printStackTrace();
							}

							try
							{
								existing_ret.setCashier(obj.getCashier());
							}
							catch (ObjectNotFoundException cashier_not_found)
							{
								cashier_not_found.printStackTrace();
							}
							
							existing_ret.setCompany(company);
							existing_ret.setCustomerListID(obj.getCustomerListID());
							existing_ret.setRequestId(obj.getRequestId());
							existing_ret.setSubtotal(obj.getSubtotal());
							existing_ret.setTaxAmount(obj.getTaxAmount());
							existing_ret.setTenderType(obj.getTenderType());
							existing_ret.setTimeModified(obj.getTimeModified());
							existing_ret.setTotal(obj.getTotal());
							existing_ret.setTxnDate(obj.getTxnDate());
							existing_ret.setTxnID(obj.getTxnID());
							existing_ret.setSalesReceiptNumber(obj.getSalesReceiptNumber());
							existing_ret.setSalesReceiptType(obj.getSalesReceiptType());
							existing_ret.setHistoryDocStatus(obj.getHistoryDocStatus());

							existing_ret.setSalesRecieptItemRetObjects(obj.getSalesRecieptItemRetObjects());
							existing_ret.setTenderRetObjects(obj.getTenderRetObjects());

							existing_ret.save();

							SalesReceiptItemRet.deleteSalesReceiptItems(existing_ret);
							TenderRet.deleteSalesReceiptItems(existing_ret);

							obj = existing_ret;

						}


						Vector order_lines = new Vector();
						UKOnlinePersonBean client = null;
						try
						{
							client = UKOnlinePersonBean.getPersonByPoSListId(company, obj.getCustomerListID());
						}
						catch (ObjectNotFoundException x)
						{
							CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Unable to find person specified by CustomerListID in SalesReceiptRet");
							throw x;
						}

						OrderBean order_obj = null;
						try
						{
							order_obj = SalesReceiptRet.getOrder(company, obj.getTxnID());
						}
						catch (ObjectNotFoundException x)
						{
							order_obj = new OrderBean();
						}

						boolean is_return = false;

						if (obj.getHistoryDocStatus().equals("Reversing") && obj.getSalesReceiptType().equals("Return"))
							is_return = true;

						order_obj.setCompany(company);
						order_obj.setAuthorizationCode(obj.getTxnID());
						order_obj.setOrderDate(obj.getTxnDate());
						order_obj.setPerson(client);
						order_obj.setStatus(OrderBean.SALES_RECEIPT_ORDER_STATUS);
						order_obj.setSubtotal(new BigDecimal(is_return ? (-1 * obj.getSubtotal()) : obj.getSubtotal()));
						order_obj.setTax(new BigDecimal(is_return ? (-1 * obj.getTaxAmount()) : obj.getTaxAmount()));
						order_obj.setTotal(new BigDecimal(is_return ? (-1 * obj.getTotal()) : obj.getTotal()));


						StringBuffer item_buf = new StringBuffer();

						System.out.println("NUM SalesRecieptItemRetObjects >" + obj.getSalesRecieptItemRetObjects().size());
						Iterator item_itr = obj.getSalesRecieptItemRetObjects().iterator();
						while (item_itr.hasNext())
						{
							SalesReceiptItemRet item_obj = (SalesReceiptItemRet)item_itr.next();
							item_obj.setSalesReceipt(obj);
							item_obj.save();

							// is there a corresponding CheckoutCode for this SalesReceiptItemRet?

							CheckoutCodeBean checkout_code = null;

							try
							{
								checkout_code = CheckoutCodeBean.getCheckoutCode(company, item_obj.getListID());
							}
							catch (ObjectNotFoundException x)
							{
								// corresponding checkout code not found.  create one

								checkout_code = new CheckoutCodeBean();
								checkout_code.setCode("PoS");
								checkout_code.setCompany(company);
								checkout_code.setType(CheckoutCodeBean.INVENTORY_TYPE); // how do I determine this???
								checkout_code.setListID(item_obj.getListID());
							}

							checkout_code.setAmount(new BigDecimal(item_obj.getPrice()));
							checkout_code.setDescription(item_obj.getDesc1());
							checkout_code.setItemNumber(item_obj.getItemNumber());
							checkout_code.save();

							// create checkout_orderline(s) for this code

							float qty = item_obj.getQty();

							CheckoutOrderline order_line = new CheckoutOrderline();
							order_line.setPrice(new BigDecimal(item_obj.getPrice()));

							BigDecimal quantity = new BigDecimal(qty);
							BigDecimal actual_amount = new BigDecimal(is_return ? (item_obj.getPrice() * qty * -1) : (item_obj.getPrice() * qty));
							BigDecimal tax_amount = new BigDecimal(is_return ? (item_obj.getTaxAmount() * -1) : item_obj.getTaxAmount());
							if (qty > 0)
								actual_amount = actual_amount.add(tax_amount.multiply(quantity));
							else
								actual_amount = actual_amount.subtract(tax_amount.multiply(quantity));
							if (item_obj.getItemNumber() != 0) // if this isn't a "Payment on Account" or something stupid like that
								order_line.setActualAmount(actual_amount);
							else
								order_line.setActualAmount(BigDecimal.ZERO);
							order_line.setTax(tax_amount);
							order_line.setCheckoutCodeId(checkout_code.getId());
							order_line.setQuantity(new BigDecimal(qty));
							order_line.setCost(checkout_code.getOrderCost());
							order_line.setOrderstatus(OrderBean.SALES_RECEIPT_ORDER_STATUS);
							try
							{
								order_line.setPractitionerId(item_obj.getAssociate().getId());
								order_line.setCommission(item_obj.getCommission());
							}
							catch (ObjectNotFoundException practitioner_not_found)
							{
							}
							order_lines.addElement(order_line);

							item_buf.append(item_obj.getQty() + " - " + item_obj.getItemNumber() + " : " + item_obj.getDesc1() + " - " + item_obj.getPrice() + "\n");

							// do I need to check this person out of any appointment?

							// does this person have an appointment today?

							try
							{
								Vector appointments_on_txn_date = AppointmentBean.getAppointmentsForClientOnDate(client, obj.getTxnDate(), AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
								System.out.println("num appointments_on_txn_date >" + appointments_on_txn_date.size());
								if (appointments_on_txn_date.size() > 0)
								{
									Iterator appointments_on_txn_date_itr = appointments_on_txn_date.iterator();
									while (appointments_on_txn_date_itr.hasNext())
									{
										AppointmentBean appt_obj = (AppointmentBean)appointments_on_txn_date_itr.next();
										if ((checkout_code.getPracticeAreaId() != 0) && (checkout_code.getPracticeAreaId() == appt_obj.getPracticeAreaId()))
										{
											// the checkout code for this item matches the practice area of the appointment.  check it out

											order_line.setAppointmentId(appt_obj.getId());
											appt_obj.setState(AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
											appt_obj.save();
										}

									}
								}
							}
							catch (Exception x)
							{
								x.printStackTrace();
								CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "Auto-Checkout Error", x.getMessage());
							}

							


						}

						order_obj.setOrders(order_lines);
						order_obj.save();

						// process the tenders

						Iterator tender_itr = obj.getTenderRetObjects().iterator();
						while (tender_itr.hasNext())
						{
							TenderRet tender_obj = (TenderRet)tender_itr.next();
							tender_obj.setOrder(order_obj);
							tender_obj.setSalesReceipt(obj);
							tender_obj.setCompany(company);
							tender_obj.save();
						}

						CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "POS Sales Receipt Processed",
								"CLIENT >" + client.getLabel() + "\n" +
								"TOTAL >" + obj.getTotal() + "\n" +
								item_buf.toString());
						System.out.println("NUM SalesRecieptItemRetObjects >" + obj.getSalesRecieptItemRetObjects().size());
						
						
						/*
						// request that the client for this order be updated.  presumably I need to update this client's balance and PoS doesn't feel like sending me update infoz when it updates the balance

						QBPOSXMLCustomerQueryRequest req_obj = new QBPOSXMLCustomerQueryRequest();
						req_obj.setSearchString(_search_str);
						if (email.length() > 0)
							req_obj.setEmailString(email);
						req_obj.setFirstNameString(firstname);
						req_obj.setLastNameString(lastname);
						if (cell.length() > 0)
							req_obj.setPhone2String(cell);
						if (phone.length() > 0)
							req_obj.setPhoneString(phone);
						QBWebConnectorSvcSoapImpl.request_queue.add(QBWebConnectorSvcSoapImpl.company_key, req_obj);
						 */

					}
					catch (ObjectAlreadyExistsException x)
					{
						x.printStackTrace();
					}

				}
			}
			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void
	updatePhone(UKOnlinePersonBean _person, String _phone_str, String _type)
		throws TorqueException, UniqueObjectNotFoundException, IllegalValueException, ObjectNotFoundException, ObjectAlreadyExistsException, Exception
	{
		if ((_phone_str != null) && (_phone_str.length() > 0))
		{
			// update phone

			try
			{
				PhoneNumberBean phone = _person.getPhoneNumber(_type);
				if (!phone.getNumberString().equals(_phone_str))
				{
					phone.setNumber(_phone_str);
					phone.save();
				}
			}
			catch (ObjectNotFoundException y)
			{
				PhoneNumberBean phone = new PhoneNumberBean();
				phone.setNumber(_phone_str);
				phone.setPerson(_person);
				phone.setType(_type);
				phone.save();
			}
		}
	}

	private boolean
	updateAddress(UKOnlineCompanyBean _company, UKOnlinePersonBean _person, AddressBean _address)
		throws TorqueException, UniqueObjectNotFoundException, IllegalValueException, ObjectNotFoundException, ObjectAlreadyExistsException, Exception
	{
		System.out.println("updateAddress() invoked >" + _person.getLabel());

		if (_address != null)
		{
			try
			{
				boolean updated = false;
				AddressBean current_address = _person.getAddress(AddressBean.PERSON_ADDRESS_TYPE);
				if (!current_address.getCityString().equals(_address.getCityString()))
				{
					current_address.setCity(_address.getCityString());
					updated = true;
				}
				if (!current_address.getZipCodeString().equals(_address.getZipCodeString()))
				{
					current_address.setZipCode(_address.getZipCodeString());
					updated = true;
				}
				if (!current_address.getStateString().equals(_address.getStateString()))
				{
					current_address.setState(_address.getStateString());
					updated = true;
				}
				if (!current_address.getStreet1String().equals(_address.getStreet1String()))
				{
					current_address.setStreet1(_address.getStreet1String());
					updated = true;
				}

				if (updated)
					current_address.save();
			}
			catch (ObjectNotFoundException y)
			{
				// no current address exists for this person.  is there an existing address that matches the passed address

				System.out.println(" no current address exists for this person.");

				try
				{
					AddressBean existing_address = AddressBean.getAddress(_company, _address.getStreet1String(), _address.getCityString(), _address.getStateString(), _address.getZipCodeString());
					System.out.println("existing_address match");
					_person.addAddress(existing_address);
				}
				catch (ObjectNotFoundException z)
				{
					System.out.println(z.getMessage());
					_address.setCompany(_company);
					_address.setType(AddressBean.PERSON_ADDRESS_TYPE);
					_address.save();
					_person.addAddress(_address);
				}

				return true;
			}
		}

		return false;
	}
	

	//Event Handlers
	public void startElement(String _uri, String _localName, String _qName, Attributes _attributes) throws SAXException {
		//reset
		System.out.println("ELEMENT >" + _qName);
		current_element = _qName;

		try
		{
			if (_qName.equals("SalesReceiptQueryRs"))
			{
				salesReceiptQueryRsObject = new SalesReceiptQueryRs();
				if (previous_unprocessed_sales_receipts != null)
				{
					Iterator itr = previous_unprocessed_sales_receipts.iterator();
					while (itr.hasNext())
					{
						SalesReceiptRet previous_unprocessed_sales_receipt = (SalesReceiptRet)itr.next();
						System.out.println("adding previous unprocessed >");
						salesReceiptQueryRsObject.add(previous_unprocessed_sales_receipt);
					}
				}
				this.fetchQueryRsAttributes(salesReceiptQueryRsObject, _attributes);
			}
			else if (_qName.equals("SalesReceiptRet"))
			{
				salesReceiptRetObject = new SalesReceiptRet();
				System.out.println("adding new SalesReceiptRet >");
				salesReceiptQueryRsObject.add(salesReceiptRetObject);
				salesReceiptItemRetObject = null;
				customerRetObject = null;
			}
			else if (_qName.equals("SalesReceiptItemRet"))
			{
				salesReceiptItemRetObject = new SalesReceiptItemRet();
				try
				{
					salesReceiptRetObject.add(salesReceiptItemRetObject);
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}
			else if (_qName.equals("CustomerQueryRs"))
			{
				customerAddRsObject = null;
				customerQueryRsObject = new CustomerQueryRs();
				if (previous_unprocessed_customers != null)
				{
					Iterator itr = previous_unprocessed_customers.iterator();
					while (itr.hasNext())
					{
						CustomerRet previous_unprocessed_customer = (CustomerRet)itr.next();
						customerQueryRsObject.add(previous_unprocessed_customer);
					}
				}
				this.fetchQueryRsAttributes(customerQueryRsObject, _attributes);
			}
			else if (_qName.equals("CustomerAddRs"))
			{
				customerQueryRsObject = null;
				customerAddRsObject = new CustomerAddRs();
				if (previous_unprocessed_customers != null)
				{
					Iterator itr = previous_unprocessed_customers.iterator();
					while (itr.hasNext())
					{
						CustomerRet previous_unprocessed_customer = (CustomerRet)itr.next();
						customerAddRsObject.add(previous_unprocessed_customer);
					}
				}
				this.fetchQueryRsAttributes(customerAddRsObject, _attributes);
			}
			else if (_qName.equals("CustomerRet"))
			{
				customerRetObject = new CustomerRet();
				if (customerQueryRsObject != null)
					customerQueryRsObject.add(customerRetObject);
				else
					customerAddRsObject.add(customerRetObject);

				salesReceiptRetObject = null;
				salesReceiptItemRetObject = null;

				//customerRetBillAddress = null;
			}
			else if (_qName.equals("TenderAccountRet"))
			{
				tenderRetObject = new TenderRet();
				tenderRetObject.setType("Account");
				salesReceiptRetObject.add(tenderRetObject);
			}
			else if (_qName.equals("TenderCashRet"))
			{
				tenderRetObject = new TenderRet();
				tenderRetObject.setType("Cash");
				salesReceiptRetObject.add(tenderRetObject);
			}
			else if (_qName.equals("TenderCheckRet"))
			{
				tenderRetObject = new TenderRet();
				tenderRetObject.setType("Check");
				salesReceiptRetObject.add(tenderRetObject);
			}
			else if (_qName.equals("TenderCreditCardRet"))
			{
				tenderRetObject = new TenderRet();
				tenderRetObject.setType("CreditCard");
				salesReceiptRetObject.add(tenderRetObject);
			}
			else if (_qName.equals("BillAddress"))
			{
				if (customerRetObject != null)
				{
					AddressBean customerRetBillAddress = new AddressBean();
					address_hash.put(customerRetObject, customerRetBillAddress);
					//customerRetBillAddress = new AddressBean();
				}
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	private void
	fetchQueryRsAttributes(QueryRs _response_obj, Attributes _attributes)
	{
		_response_obj.setRequestID(_attributes.getValue("requestID"));
		String value = _attributes.getValue("retCount");
		if (value != null)
			_response_obj.setRetCount(Integer.parseInt(value));
		value = _attributes.getValue("statusCode");
		if (value != null)
			_response_obj.setStatusCode(value);
		value = _attributes.getValue("statusMessage");
		if (value != null)
			_response_obj.setStatusMessage(value);
		value = _attributes.getValue("statusSeverity");
		if (value != null)
			_response_obj.setStatusSeverity(value);
		value = _attributes.getValue("iteratorRemainingCount");
		if (value != null)
			_response_obj.setIteratorRemainingCount(Integer.parseInt(value));
		value = _attributes.getValue("iteratorID");
		if (value != null)
		{
			// find the corresponding request object

			QBPOSXMLRequest iterator_request_obj = QBWebConnectorSvcSoapImpl.pending_iterator_hash.get(_response_obj.getRequestID());
			if (iterator_request_obj == null)
				iterator_request_obj = QBWebConnectorSvcSoapImpl.iterator_hash.get(value);
			else
			{
				QBWebConnectorSvcSoapImpl.pending_iterator_hash.remove(value);
				QBWebConnectorSvcSoapImpl.iterator_hash.put(value, iterator_request_obj);
			}

			if (iterator_request_obj != null)
			{

				iterator_request_obj.setIteratorRemainingCount(_response_obj.getIteratorRemainingCount());
				
				if (_response_obj.getIteratorRemainingCount() == 0)
				{
					QBWebConnectorSvcSoapImpl.pending_iterator_hash.remove(_response_obj.getRequestID());
					QBWebConnectorSvcSoapImpl.iterator_hash.remove(value);
					iterator_request_obj.setExhaustedIterator(true);
				}
				else
				{
					// convert to continuation

					iterator_request_obj.setContinuation(true);
					iterator_request_obj.setIteratorID(value);
				}


			}

			_response_obj.setIteratorID(value);
		}
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException {
		String value = new String(ch,start,length);
		try
		{
			System.out.println(current_element + " = " + value);
			if (current_element.equals("TimeModified"))
			{
				if (salesReceiptRetObject != null)
					salesReceiptRetObject.setTimeModified(time_modified_date_format.parse(value.substring(0, value.lastIndexOf("-"))));
				else if (customerRetObject != null)
					customerRetObject.setTimeModified(time_modified_date_format.parse(value.substring(0, value.lastIndexOf("-"))));
			}
			else if (current_element.equals("CustomerListID"))
				salesReceiptRetObject.setCustomerListID(value);
			else if (current_element.equals("Subtotal"))
				salesReceiptRetObject.setSubtotal(Float.parseFloat(value));
			else if (current_element.equals("TaxAmount"))
			{
				if (salesReceiptItemRetObject == null)
					salesReceiptRetObject.setTaxAmount(Float.parseFloat(value));
				else
					salesReceiptItemRetObject.setTaxAmount(Float.parseFloat(value));
			}
			else if (current_element.equals("Associate"))
			{
				try
				{
					if (salesReceiptItemRetObject == null)
						salesReceiptRetObject.setAssociate(UKOnlinePersonBean.getPractitioner(company, value));
					else
						salesReceiptItemRetObject.setAssociate(UKOnlinePersonBean.getPractitioner(company, value));
				}
				catch (ObjectNotFoundException x)
				{
					x.printStackTrace();
				}
			}
			else if (current_element.equals("Cashier"))
			{
				try
				{
					salesReceiptRetObject.setCashier(UKOnlinePersonBean.getCashier(company, value));
				}
				catch (ObjectNotFoundException x)
				{
					x.printStackTrace();
				}
			}
			else if (current_element.equals("TenderType"))
				salesReceiptRetObject.setTenderType(value);
			else if (current_element.equals("Total"))
				salesReceiptRetObject.setTotal(Float.parseFloat(value));
			else if (current_element.equals("TxnDate"))
				salesReceiptRetObject.setTxnDate(date_format.parse(value));
			else if (current_element.equals("TxnID"))
				salesReceiptRetObject.setTxnID(value);
			else if (current_element.equals("SalesReceiptNumber"))
				salesReceiptRetObject.setSalesReceiptNumber(Integer.parseInt(value));
			else if (current_element.equals("SalesReceiptType"))
				salesReceiptRetObject.setSalesReceiptType(value);
			else if (current_element.equals("HistoryDocStatus"))
				salesReceiptRetObject.setHistoryDocStatus(value);
			else if (current_element.equals("CheckNumber"))
				tenderRetObject.setCheckNumber(Integer.parseInt(value));
			else if (current_element.equals("TenderAmount"))
				tenderRetObject.setAmount(Float.parseFloat(value));
			else if (current_element.equals("ItemNumber"))
				salesReceiptItemRetObject.setItemNumber(Integer.parseInt(value));
			else if (current_element.equals("ListID"))
			{
				if (salesReceiptItemRetObject != null)
					salesReceiptItemRetObject.setListID(value);
				else if (customerRetObject != null)
					customerRetObject.setListID(value);
			}
			else if (current_element.equals("Price"))
				salesReceiptItemRetObject.setPrice(Float.parseFloat(value));
			else if (current_element.equals("Qty"))
				salesReceiptItemRetObject.setQty(Float.parseFloat(value));
			else if (current_element.equals("Desc1"))
				salesReceiptItemRetObject.setDesc1(value);
			else if (current_element.equals("Commission"))
				salesReceiptItemRetObject.setCommission(Float.parseFloat(value));
			else if (current_element.equals("AccountBalance"))
			{
				if (customerRetObject != null)
					customerRetObject.setAccountBalance(Float.parseFloat(value));
			}
			else if (current_element.equals("FirstName"))
			{
				if (customerRetObject != null)
					customerRetObject.setFirstName(value);
			}
			else if (current_element.equals("FullName"))
			{
				if (customerRetObject != null)
					customerRetObject.setFullName(value);
			}
			else if (current_element.equals("LastName"))
			{
				if (customerRetObject != null)
					customerRetObject.setLastName(value);
			}
			else if (current_element.equals("LastSale"))
			{
				if (customerRetObject != null)
					customerRetObject.setLastSaleDate(date_format.parse(value));
			}
			else if (current_element.equals("Phone"))
			{
				if (customerRetObject != null)
					customerRetObject.setPhone(value);
			}
			else if (current_element.equals("Phone2"))
			{
				if (customerRetObject != null)
					customerRetObject.setPhone2(value);
			}
			else if (current_element.equals("EMail"))
			{
				if (customerRetObject != null)
					customerRetObject.setEMail(value);
			}
			else if (current_element.equals("PriceLevelNumber"))
			{
				if (customerRetObject != null)
					customerRetObject.setPriceLevelNumber(Short.parseShort(value));
			}
			else if (current_element.equals("Street"))
			{
				System.out.println("customerRetObject >" + customerRetObject);
				if (customerRetObject != null)
				{
					AddressBean customerRetBillAddress = (AddressBean)address_hash.get(customerRetObject);
					System.out.println("customerRetBillAddress >" + customerRetBillAddress);
					if (customerRetBillAddress != null)
						customerRetBillAddress.setStreet1(value);
				}
			}
			else if (current_element.equals("State"))
			{
				if (customerRetObject != null)
				{
					AddressBean customerRetBillAddress = (AddressBean)address_hash.get(customerRetObject);
					if (customerRetBillAddress != null)
						customerRetBillAddress.setState(value);
				}
			}
			else if (current_element.equals("PostalCode"))
			{
				if (customerRetObject != null)
				{
					AddressBean customerRetBillAddress = (AddressBean)address_hash.get(customerRetObject);
					if (customerRetBillAddress != null)
						customerRetBillAddress.setZipCode(value);
				}
			}
			else if (current_element.equals("City"))
			{
				if (customerRetObject != null)
				{
					AddressBean customerRetBillAddress = (AddressBean)address_hash.get(customerRetObject);
					if (customerRetBillAddress != null)
						customerRetBillAddress.setCity(value);
				}
			}


		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		
		
	}
}
