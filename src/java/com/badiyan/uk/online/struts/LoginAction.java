package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import com.badiyan.uk.online.util.CCUtils;
import com.badiyan.uk.online.util.PasswordGenerator;
import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;




import com.valeo.qbpos.data.*;
import java.math.BigDecimal;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
LoginAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in LoginActionX");

		String forward_string = "success";

		/*
		String file_path = CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + "index";
		System.out.println("file_path >" + file_path);
		File test_file = new File(file_path);
		if (test_file.isDirectory())
		{
		    System.out.println("directory...");

		    String[] file_list = test_file.list();
		    for (int i = 0; i < file_list.length; i++)
		    {
			System.out.println("file name >" + file_list[i]);
		    }
		}
		 */

		/*
		Iterator course_file_itr = this.getFiles().iterator();
		for (int i = 0; course_file_itr.hasNext(); i++)
		{
		    CourseFile course_file_obj = (CourseFile)course_file_itr.next();
		    String course_file_url = course_file_obj.getUrl();
		    String file_path = CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.coursesFolder") + this.getIdString() + File.separator + course_file_url;
		    File test_file = new File(file_path);
		    boolean exists = test_file.exists();
		    if (!exists)
			return true;
		}
		 */


		/*
		RoleBean student_role = RoleBean.getRole(RoleBean.STUDENT_ROLE_NAME);
		RoleBean manager_role = RoleBean.getRole(RoleBean.MANAGER_ROLE_NAME);
		RoleBean training_administrator_role = RoleBean.getRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME); // department reporting access
		RoleBean department_administrator_role = RoleBean.getRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME); // department activity start/completion

		Iterator person_itr = PersonBean.getPersons(CompanyBean.getInstance()).iterator();
		while (person_itr.hasNext())
		{
		    PersonBean person_obj = (PersonBean)person_itr.next();
		    System.out.println("person >" + person_obj.getLabel());



		    PersonTitleBean title = person_obj.getTitle();

		    if (title.getLabel().equals(EcolabPersonTitleBean.TRAINING_MANAGER_TITLE_NAME) ||
			    title.getLabel().equals(EcolabPersonTitleBean.DEPARTMENT_MANAGER_TITLE_NAME))
		    {

			person_obj.addRole(manager_role);
		    }

		    if (title.getLabel().equals(EcolabPersonTitleBean.DEPARTMENT_MANAGER_TITLE_NAME))
		    {

			person_obj.addRole(department_administrator_role);
		    }

		    if (title.getLabel().equals(EcolabPersonTitleBean.TRAINING_MANAGER_TITLE_NAME))
		    {

			person_obj.addRole(training_administrator_role);
		    }
		}
		 */


		/*
		UKOnlinePersonBean christine = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4);
		PhoneNumberBean christine_cell_number = PhoneNumberBean.getPhoneNumber(christine, PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
		boolean match = christine_cell_number.partiallyMatches("612.270.3795", 4);
		System.out.println("match >" + match);
		 */

		/*

		int num_found = 0;
		int num_not_found = 0;
		int num_not_unique = 0;

		int customer_ret_found = 0;
		int customer_ret_not_found = 0;
		int customer_ret_not_unique = 0;

		int person_found = 0;

		UKOnlineCompanyBean company_instance = (UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance();
		Iterator sales_receipt_itr = SalesReceiptRet.getSalesReceipts(company_instance).iterator();
		while (sales_receipt_itr.hasNext())
		{
			SalesReceiptRet obj = (SalesReceiptRet)sales_receipt_itr.next();
			System.out.println("sales receipt found >" + obj.getLabel());

			// does the person referenced in this ListId still exist?

			String customer_list_id = obj.getCustomerListID();

			try
			{
				UKOnlinePersonBean.getPersonByEmployeeId(company_instance, customer_list_id);
				//UKOnlinePersonBean.getPersonByPoSListId(company_instance, customer_list_id);
				System.out.println("Person found for >" + customer_list_id);
				num_found++;
			}
			catch (ObjectNotFoundException x)
			{
				System.out.println(" *Person not found for >" + customer_list_id);
				num_not_found++;

				try
				{
					CustomerRet customer_ret_obj = CustomerRet.getCustomer(company_instance, customer_list_id);
					customer_ret_found++;

					Vector persons = new Vector();

					String patientPhone = customer_ret_obj.getPhone();
					String patientCell = customer_ret_obj.getPhone2();
					String patientEMail = customer_ret_obj.getEMail();
					String patientFirstName = customer_ret_obj.getFirstName();
					String patientLastName = customer_ret_obj.getLastName();

					if ((patientPhone != null) && (patientPhone.length() > 0))
						persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(company_instance, patientLastName, patientFirstName, patientPhone);

					if (persons.size() != 1)
					{
						if ((patientCell != null) && (patientCell.length() > 0))
							persons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(company_instance, patientLastName, patientFirstName, patientCell);
					}

					if (persons.size() != 1)
					{
						if ((patientEMail != null) && (patientEMail.length() > 0))
							persons = UKOnlinePersonBean.getPersonsByLastNameFirstNameEmail(company_instance, patientLastName, patientFirstName, patientEMail);
					}

					if (persons.size() != 1)
						persons = UKOnlinePersonBean.getPersonsByLastNameFirstName(company_instance, patientLastName, patientFirstName);

					if (persons.size() == 1)
					{
						person_found++;

						UKOnlinePersonBean person_for_orphaned_sales_receipt = (UKOnlinePersonBean)persons.get(0);
						obj.setCustomerListID(person_for_orphaned_sales_receipt.getEmployeeNumberString());
						obj.save();
					}

					//String proper_list_id = customer_ret_obj.getListID();
					//obj.setCustomerListID();
				}
				catch (ObjectNotFoundException y)
				{
					customer_ret_not_found++;
				}
				catch (UniqueObjectNotFoundException y)
				{
					customer_ret_not_unique++;
				}
			}
			catch (UniqueObjectNotFoundException x)
			{
				System.out.println(" *Unique person not found for >" + customer_list_id);
				num_not_unique++;
			}
		}

		System.out.println("num_found >" + num_found);
		System.out.println("num_not_found >" + num_not_found);
		System.out.println("num_not_unique >" + num_not_unique);

		System.out.println("customer_ret_found >" + customer_ret_found);
		System.out.println("customer_ret_not_found >" + customer_ret_not_found);
		System.out.println("customer_ret_not_unique >" + customer_ret_not_unique);

		System.out.println("person_found >" + person_found);

		*/


		/*
		UKOnlineCompanyBean company_instance = (UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance();
		System.out.println("NEXT FILE NUMBER >" + UKOnlinePersonBean.getNextFileNumber(company_instance));
		 */



		/*
		BigDecimal negative_one = new BigDecimal(-1);

		UKOnlineCompanyBean company_instance = (UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance();
		Iterator sales_receipt_itr = SalesReceiptRet.getSalesReceipts(company_instance, "Return", "Reversing").iterator();
		while (sales_receipt_itr.hasNext())
		{
			SalesReceiptRet sales_receipt_obj = (SalesReceiptRet)sales_receipt_itr.next();
			System.out.println("REVERSE >" + sales_receipt_obj.getLabel());

			// find the corresponding Order

			OrderBean order_obj = OrderBean.getOrder(sales_receipt_obj.getTxnID());
			System.out.println("ORDER FOUND >" + order_obj.getLabel());

			Iterator orderlines_itr = order_obj.getOrders();
			while (orderlines_itr.hasNext())
			{
				CheckoutOrderline orderline = (CheckoutOrderline)orderlines_itr.next();
				System.out.println("ORDERLINE FOUND >" + orderline.getActualAmount());
				BigDecimal actual_amount = orderline.getActualAmount();
				actual_amount = actual_amount.multiply(negative_one);
				orderline.setActualAmount(actual_amount);
				orderline.save();
			}
		}
		 */

		/*
		Criteria crit = new Criteria();
		crit.add(CheckoutOrderlinePeer.QUANTITY, 1, Criteria.GREATER_THAN);
		Iterator obj_itr = CheckoutOrderlinePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			CheckoutOrderline ob = (CheckoutOrderline)obj_itr.next();
			System.out.println("QUANT >" + ob.getQuantity());

			BigDecimal extended_price = ob.getPrice().multiply(ob.getQuantity());
			BigDecimal extended_tax = ob.getTax().multiply(ob.getQuantity());
			BigDecimal extended_total = extended_price.add(extended_tax);
			ob.setActualAmount(extended_total);
			ob.save();
		}
		*/


















		/*


		// I need to associate Orders with

		HashMap x_hash = new HashMap();

		int num_found = 0;
		int num_not_found = 0;
		int num_not_unique = 0;
		
		int practice_area_orders = 0;
		int multi_practice_area_orders = 0;

		int multi_appt_on_day = 0;
		int multi_appt_on_day_2 = 0;
		int multi_appt_on_day_3 = 0;
		int multi_appt_on_day_4 = 0;

		int multi_appt_pa_on_day = 0;
		int multi_appt_pa_on_day_2 = 0;
		int multi_appt_pa_on_day_3 = 0;
		int multi_appt_pa_on_day_4 = 0;

		int practice_area_appt_matches = 0;
		int multi_practice_area_appt_matches = 0;

		int num_matching_appts = 0;

		UKOnlineCompanyBean company_instance = (UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance();
		Iterator sales_receipt_itr = SalesReceiptRet.getSalesReceipts(company_instance).iterator();
		while (sales_receipt_itr.hasNext())
		{
			SalesReceiptRet obj = (SalesReceiptRet)sales_receipt_itr.next();
			System.out.println("sales receipt found >" + obj.getLabel());

			try
			{
				OrderBean order_obj = SalesReceiptRet.getOrder(company_instance, obj.getTxnID());

				UKOnlinePersonBean person_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(order_obj.getPersonId());
				String key = order_obj.getOrderDateString() + "|" + person_obj.getValue();

				HashMap practice_area_orderlines_map = (HashMap)x_hash.get(key);
				if (practice_area_orderlines_map == null)
				{
					practice_area_orderlines_map = new HashMap(3);
					x_hash.put(key, practice_area_orderlines_map);
				}
				
				boolean practice_area_found_for_order = false;
				boolean multi_practice_area_found_for_order = false;
				int found_practice_area_id = 0;
				Vector practice_areas_in_order = new Vector();
				Iterator orderline_itr = order_obj.getOrders();
				while (orderline_itr.hasNext())
				{
					CheckoutOrderline orderline = (CheckoutOrderline)orderline_itr.next();
					CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
					int practice_area_id = checkout_code.getPracticeAreaId();
					if (practice_area_id > 0)
					{
						if (practice_area_found_for_order)
						{
							if (!multi_practice_area_found_for_order)
							{
								if (found_practice_area_id != practice_area_id)
								{
									multi_practice_area_found_for_order = true;
									multi_practice_area_orders++;
								}
							}
						}
						else
						{
							practice_area_found_for_order = true;
							practice_area_orders++;
							found_practice_area_id = practice_area_id;
						}

						PracticeAreaBean practice_area_obj = PracticeAreaBean.getPracticeArea(practice_area_id);
						practice_areas_in_order.addElement(practice_area_obj);

						Vector orderlines = (Vector)practice_area_orderlines_map.get(practice_area_obj);
						if (orderlines == null)
						{
							orderlines = new Vector();
							practice_area_orderlines_map.put(practice_area_obj, orderlines);
						}
						orderlines.addElement(orderline);
					}

				}

				System.out.println("Person found for >" + person_obj.getLabel());
				num_found++;

			}
			catch (ObjectNotFoundException x)
			{
				System.out.println(" *Person not found for >" + obj.getLabel());
				num_not_found++;


			}
			catch (UniqueObjectNotFoundException x)
			{
				System.out.println(" *Unique person not found for >" + obj.getLabel());
				num_not_unique++;
			}
		}

		int num_orderline_match_appt_pa = 0;
		int num_orderline_no_match_appt_pa = 0;

		// find an appointment associated with this order / person

		Iterator x_hash_itr = x_hash.keySet().iterator();
		while (x_hash_itr.hasNext())
		{
			String key = (String)x_hash_itr.next();
			HashMap practice_area_orderlines_map = (HashMap)x_hash.get(key);

			Date txn_date = CUBean.getDateFromUserString(key.substring(0, key.indexOf('|')));
			UKOnlinePersonBean person_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(key.substring(key.indexOf('|') + 1)));


			Vector consumed_orderlines = new Vector();

			boolean practice_area_found_for_appt = false;
			boolean multi_practice_area_found_for_appt = false;

			Vector appointments = AppointmentBean.getAppointmentsForClientOnDate(person_obj, txn_date, AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
			System.out.println("checked out appointments >" + appointments.size());
			Vector checked_in_appointments = AppointmentBean.getAppointmentsForClientOnDate(person_obj, txn_date, AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
			System.out.println("checked in appointments >" + checked_in_appointments.size());
			appointments.addAll(checked_in_appointments);
			System.out.println("appointments >" + appointments.size());
			if (appointments.size() > 1)
			{
				multi_appt_on_day++;
				if (appointments.size() > 3)
					multi_appt_on_day_4++;
				else if (appointments.size() > 2)
					multi_appt_on_day_3++;
				else
					multi_appt_on_day_2++;
			}

			HashMap practice_area_appointment_hash = new HashMap(3);
			Iterator appt_itr = appointments.iterator();
			while (appt_itr.hasNext())
			{
				AppointmentBean appointment_obj = (AppointmentBean)appt_itr.next();
				PracticeAreaBean practice_area = appointment_obj.getPracticeArea();

				Vector hash_appointments = (Vector)practice_area_appointment_hash.get(practice_area);
				if (hash_appointments == null)
				{
					hash_appointments = new Vector();
					practice_area_appointment_hash.put(practice_area, hash_appointments);
				}
				hash_appointments.addElement(appointment_obj);
			}
			if (practice_area_appointment_hash.size() > 1)
			{
				multi_appt_pa_on_day++;
				if (practice_area_appointment_hash.size() > 3)
					multi_appt_pa_on_day_4++;
				else if (practice_area_appointment_hash.size() > 2)
					multi_appt_pa_on_day_3++;
				else
					multi_appt_pa_on_day_2++;
			}

			Vector common_practice_areas = new Vector();

			String pa_orderline_str = "";
			Iterator itr = practice_area_orderlines_map.keySet().iterator();
			while (itr.hasNext())
			{
				PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
				if (pa_orderline_str.equals(""))
					pa_orderline_str = practice_area.getLabel();
				else
					pa_orderline_str += "," + practice_area.getLabel();

				if (practice_area_appointment_hash.containsKey(practice_area))
					common_practice_areas.addElement(practice_area);
			}
			String pa_appt_str = "";
			itr = practice_area_appointment_hash.keySet().iterator();
			while (itr.hasNext())
			{
				PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
				if (pa_appt_str.equals(""))
					pa_appt_str = practice_area.getLabel();
				else
					pa_appt_str += "," + practice_area.getLabel();
			}

			System.out.println(key + " - " + person_obj.getLabel() + "    pa orderlines (" + pa_orderline_str + ") >" + practice_area_orderlines_map.size() + "    pa appt (" + pa_appt_str + ") >" + practice_area_appointment_hash.size());
			
			if (practice_area_appointment_hash.size() == practice_area_orderlines_map.size())
				num_orderline_match_appt_pa++;
			else
				num_orderline_no_match_appt_pa++;

			itr = common_practice_areas.iterator();
			while (itr.hasNext())
			{
				PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();

				Vector orderlines_vec = (Vector)practice_area_orderlines_map.get(practice_area);
				System.out.println("orderlines_vec >" + orderlines_vec.size());
				Iterator orderlines_vec_itr = orderlines_vec.iterator();
				while (orderlines_vec_itr.hasNext())
				{
					CheckoutOrderline obj = (CheckoutOrderline)orderlines_vec_itr.next();
					CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(obj.getCheckoutCodeId());
					System.out.println("checkout_code_obj >" + checkout_code_obj.getLabel());
				}
				Vector appointments_vec = (Vector)practice_area_appointment_hash.get(practice_area);
				System.out.println("appointments_vec >" + appointments_vec.size());
				Iterator appointments_vec_itr = appointments_vec.iterator();
				while (appointments_vec_itr.hasNext())
				{
					AppointmentBean obj = (AppointmentBean)appointments_vec_itr.next();
					System.out.println("appointment type >" + obj.getType().getLabel());
				}
			}



			appt_itr = appointments.iterator();
			while (appt_itr.hasNext())
			{
				AppointmentBean appointment_obj = (AppointmentBean)appt_itr.next();

				// matching appointment if the order contains a checkout_orderline that for a practice area that matches the practice area of the appointment

				PracticeAreaBean practice_area = appointment_obj.getPracticeArea();

				Vector orderlines_for_practice_area = (Vector)practice_area_orderlines_map.get(practice_area);
				if (orderlines_for_practice_area != null)
				{
					Iterator orderlines_itr = orderlines_for_practice_area.iterator();
					while (orderlines_itr.hasNext())
					{
						CheckoutOrderline orderline = (CheckoutOrderline)orderlines_itr.next();
						if (!consumed_orderlines.contains(orderline))
						{
							consumed_orderlines.addElement(orderline);
							//if (orderline.getAppointmentId() == 0)
							//	orderline.setAppointmentId(appointment_obj.getId());
						}
					}

				}

			}

		}


		System.out.println("num_found >" + num_found);
		System.out.println("num_not_found >" + num_not_found);
		System.out.println("num_not_unique >" + num_not_unique);


		System.out.println("practice_area_orders >" + practice_area_orders);
		System.out.println("multi_practice_area_orders >" + multi_practice_area_orders);

		System.out.println("practice_area_appt_matches >" + practice_area_appt_matches);
		System.out.println("multi_practice_area_appt_matches >" + multi_practice_area_appt_matches);


		System.out.println("multi_appt_on_day >" + multi_appt_on_day);
		System.out.println("multi_appt_on_day_2 >" + multi_appt_on_day_2);
		System.out.println("multi_appt_on_day_3 >" + multi_appt_on_day_3);
		System.out.println("multi_appt_on_day_4 >" + multi_appt_on_day_4);

		System.out.println("multi_appt_pa_on_day >" + multi_appt_pa_on_day);
		System.out.println("multi_appt_pa_on_day_2 >" + multi_appt_pa_on_day_2);
		System.out.println("multi_appt_pa_on_day_3 >" + multi_appt_pa_on_day_3);
		System.out.println("multi_appt_pa_on_day_4 >" + multi_appt_pa_on_day_4);


		System.out.println("num_orderline_match_appt_pa >" + num_orderline_match_appt_pa);
		System.out.println("num_orderline_no_match_appt_pa >" + num_orderline_no_match_appt_pa);


		*/








		/*

		String qb_str = "<QBXML>" +
"<QBXMLMsgsRs>" +
"<CustomerQueryRs requestID=\"80a6c8c1-c178-42d4-a8c5-4620ea88a0ea\" statusCode=\"0\" statusSeverity=\"Info\" statusMessage=\"Status OK\">" +
"<CustomerRet>" +
"<ListID>80000227-1230648596</ListID>" +
"<TimeCreated>2008-12-30T08:49:56-06:00</TimeCreated>" +
"<TimeModified>2008-12-30T08:49:56-06:00</TimeModified>" +
"<EditSequence>1230648596</EditSequence>" +
"<Name>Aaron Bohler</Name>" +
"<FullName>Aaron Bohler</FullName>" +
"<IsActive>true</IsActive>" +
"<Sublevel>0</Sublevel>" +
"<FirstName>Aaron</FirstName>" +
"<LastName>Bohler</LastName>" +
"<BillAddress>" +
"<Addr1>Aaron Bohler</Addr1>" +
"<Addr2>1195 Lilac Circle</Addr2>" +
"<City>Victoria</City>" +
"<State>MN</State>" +
"<PostalCode>55386</PostalCode>" +
"</BillAddress>" +
"<BillAddressBlock>" +
"<Addr1>Aaron Bohler</Addr1>" +
"<Addr2>1195 Lilac Circle</Addr2>" +
"<Addr3>Victoria, MN 55386</Addr3>" +
"</BillAddressBlock>" +
"<Phone>952.443.2602</Phone>" +
"<Balance>0.00</Balance>" +
"<TotalBalance>0.00</TotalBalance>" +
"<SalesTaxCodeRef>" +
"<ListID>80000001-1227016701</ListID>" +
"<FullName>Tax</FullName>" +
"</SalesTaxCodeRef>" +
"<ItemSalesTaxRef>" +
"<ListID>80000005-1230648596</ListID>" +
"<FullName>MN Sales Tax</FullName>" +
"</ItemSalesTaxRef>" +
"<JobStatus>None</JobStatus>" +
"</CustomerRet>" +
"</CustomerQueryRs>" +
"</QBXMLMsgsRs>" +
"</QBXML>";

		try
		{
			QBXMLResponseParser request_parser = new QBXMLResponseParser((UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance());
			request_parser.parse(qb_str);
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		*/
		





		/*

		int num_match = 0;
		int num_no_match = 0;
		UKOnlineCompanyBean company_instance = (UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance();
		Iterator itr = UKOnlinePersonBean.getPersons(company_instance).iterator();
		while (itr.hasNext())
		{
			PersonBean person_obj = (PersonBean)itr.next();
			Iterator order_itr = OrderBean.getOrders(person_obj).iterator();
			while (order_itr.hasNext())
			{
				OrderBean order_obj = (OrderBean)order_itr.next();
				if (!order_obj.getStatus().equals(OrderBean.RECEIVE_PAYMENT_ORDER_STATUS))
				{

					HashMap hash = new HashMap(3);
					SalesReceiptRet sales_receipt_obj = SalesReceiptRet.getSalesReceipt(company_instance, order_obj);
					Iterator sales_receipt_item_itr = sales_receipt_obj.getSalesRecieptItemRetObjects().iterator();
					while (sales_receipt_item_itr.hasNext())
					{
						SalesReceiptItemRet item_obj = (SalesReceiptItemRet)sales_receipt_item_itr.next();
						CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(company_instance, item_obj.getListID());
						BigDecimal quantity = new BigDecimal(item_obj.getQty());
						String key = checkout_code_obj.getId() + "|" + quantity.toString() + ".00";
						System.out.println("key >" + key);
						hash.put(key, item_obj);
					}

					Iterator order_lines_itr = order_obj.getOrders();
					while (order_lines_itr.hasNext())
					{
						CheckoutOrderline orderline_obj = (CheckoutOrderline)order_lines_itr.next();
						CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(orderline_obj.getCheckoutCodeId());
						String key = checkout_code_obj.getId() + "|" + orderline_obj.getQuantity().toString();
						if (hash.containsKey(key))
						{
							SalesReceiptItemRet item_obj = (SalesReceiptItemRet)hash.get(key);
							try
							{
								UKOnlinePersonBean practitioner = item_obj.getAssociate();
								orderline_obj.setPractitionerId(practitioner.getId());
								orderline_obj.setCommission(item_obj.getCommission());
								orderline_obj.save();
							}
							catch(ObjectNotFoundException x)
							{
							}
							num_match++;
						}
						else
						{
							System.out.println("key not found in hash >" + key);
							num_no_match++;
						}
					}
				}
			}
		}

		System.out.println("num_match >" + num_match);
		System.out.println("num_no_match >" + num_no_match);

		 */

		/*
		try {
			UKOnlineCompanyBean valeo = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany("Valeo");
			UKOnlineCompanyBean sano = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany("Sano Consulting");
			Iterator cc_itr = CheckoutCodeBean.getCheckoutCodes(valeo, "", CheckoutCodeBean.INVENTORY_TYPE).iterator();
			while (cc_itr.hasNext()) {
				CheckoutCodeBean checkout_code = (CheckoutCodeBean)cc_itr.next();
				System.out.println("checkout_code >" + checkout_code.getLabel());

				CheckoutCodeBean new_code = new CheckoutCodeBean();
				new_code.setAmount(checkout_code.getAmount());
				//new_Code.setAverageUnitCost(checkout_code.);
				new_code.setCode(checkout_code.getCodeString());
				new_code.setCommissionable(checkout_code.isCommissionable());
				new_code.setCompany(sano);
				new_code.setDescription(checkout_code.getDescriptionString());
				new_code.setOrderCost(checkout_code.getOrderCost());
				new_code.setIsActive(checkout_code.isActive());
				new_code.setPlanUse(checkout_code.isPlanUse());
				new_code.setReorderPoint(checkout_code.getReorderPoint());
				new_code.setSalesDescription(checkout_code.getSalesDescriptionString());
				new_code.setType(checkout_code.getType());
				new_code.save();
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		 * 
		 */



		 //doPlanUseStuff();
		
		
		//System.out.println("card_type >" + card_type);
		/*
		System.out.println("CCUtils.VISA >" + CCUtils.getCardName(CCUtils.VISA));
		System.out.println("CCUtils.MASTERCARD >" + CCUtils.getCardName(CCUtils.MASTERCARD));
		System.out.println("CCUtils.DISCOVER >" + CCUtils.getCardName(CCUtils.DISCOVER));
		System.out.println("CCUtils.AMERICAN_EXPRESS >" + CCUtils.getCardName(CCUtils.AMERICAN_EXPRESS));
		*/
		
		
		/*
		UKOnlineCompanyBean sanowc = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5);
		Iterator peopleItr = sanowc.getPeople().iterator();
		while (peopleItr.hasNext()) {
			PersonBean personObj = (PersonBean)peopleItr.next();
			UKOnlinePersonBean sanoPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(personObj.getId());
			String pw = sanoPerson.getPasswordString();
			if (pw.indexOf('-') > -1) {
				System.out.println("found person with hyphen pw >" + sanoPerson.getLabel() + "  --  >" + pw);
				String newPassword = PasswordGenerator.getPassword(3);
				boolean isUniquePw = PasswordGenerator.isUnique(newPassword);
				while (!isUniquePw) {
					newPassword = PasswordGenerator.getPassword(3);
					isUniquePw = PasswordGenerator.isUnique(newPassword);
				}
				sanoPerson.setPasswordAnyLength(newPassword);
				sanoPerson.setConfirmPassword(newPassword);
				sanoPerson.save();
			}
			
		}
		*/


		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
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
			}
			else
				return (_mapping.findForward("session_expired"));

			String password = (String)PropertyUtils.getSimpleProperty(_form, "password");
			loginBean.setPassword(password);
			String username = null;
			
			UKOnlinePersonBean person;
			
			try {
				username = (String)PropertyUtils.getSimpleProperty(_form, "username");
				loginBean.setUsername(username);
				person = (UKOnlinePersonBean)loginBean.getPerson();
			} catch (java.lang.NoSuchMethodException x) {
				person = (UKOnlinePersonBean)loginBean.getPersonPasswordOnly();
				
				if (!person.hasEmail()) {
					forward_string = "enter_email";
				} else if (AppointmentBean.hasAppointmentTodayForClientNotAlreadyCheckedOut(person)) {
					forward_string = "intake";
				}
			}

			
			

			UKOnlineCompanyBean adminCompany = (UKOnlineCompanyBean)person.getSelectedCompany();
			session.setAttribute("adminCompany", adminCompany);

			try
			{
				session.setAttribute("selectedGroup", person.getGroup());
			}
			catch (ObjectNotFoundException x)
			{
			}

			try
			{
				session.setAttribute("selectedTitle", person.getJobTitle());
			}
			catch (ObjectNotFoundException x)
			{
			}

			try
			{
				session.setAttribute("selectedDepartment", DepartmentBean.getDepartment(person.getDepartmentId()));
			}
			catch (ObjectNotFoundException x)
			{
			}

			//System.out.println("USERNAME >" + loginBean.getUsername());
			

			if (person.hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME))
			{
				session.setAttribute("adminPractitioner", person);
				forward_string = "practitioner";
			}
			else if (person.hasRole(UKOnlineRoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) || person.hasRole(UKOnlineRoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
			{
				forward_string = "administrator";
			}

			if (password.equals("password"))
			    forward_string = "change_password";

			CompanySettingsBean settings = adminCompany.getSettings();
			if (!settings.isSetupComplete())
			{
				session.setAttribute("adminPerson", person);
				forward_string = "setup_incomplete";
			}
			
			String browser = "[UNKNOWN]";
			String version = "[UNKNOWN]";
			
			if (_request.getParameter("browser") != null)
				browser = _request.getParameter("browser");
			if (_request.getParameter("version") != null)
				version = _request.getParameter("version");
			
			session.setAttribute("browser", browser);
			session.setAttribute("version", version);
		}
		catch (Exception x)
		{
			x.printStackTrace();
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			
			session.setAttribute("forgot-password-message", "<strong>" + x.getMessage() + "</strong>  If you've forgotten your password, provide your email address to have your password sent to you.");
		}

		// Report any errors we have discovered back to the original form
		/*
		if (!errors.isEmpty())
		{
			saveErrors(_request, errors);
			return _mapping.getInputForward();
			
		}
		 * 
		 */

		// Remove the obsolete form bean
		if (_mapping.getAttribute() != null)
		{
            if ("request".equals(_mapping.getScope()))
                _request.removeAttribute(_mapping.getAttribute());
            else
                session.removeAttribute(_mapping.getAttribute());
        }

		// Forward control to the specified success URI
		//return (_mapping.findForward(forward_string));
		
		ActionForward af = null;
		
		if (!errors.isEmpty())
		{
			saveErrors(_request, errors);
			//return _mapping.getInputForward();
			//af = _mapping.getInputForward();
			forward_string = "forgot_password";
		}
		//else
		//{
			af = _mapping.findForward(forward_string);
			if (af == null)
				af = _mapping.findForward("success");
			//af.setContextRelative(true);
			//return af;
		//}
		
		ActionForward af2 = new ActionForward();
		//af2.setContextRelative(true);
		af2.setName(af.getName());
		af2.setRedirect(true);
		af2.setPath(af.getPath());
		af2.freeze();
		
		// Forward control to the specified success URI
		return af2;
	}

	private void
	doPlanUseStuff()
		throws Exception
	{

		UKOnlineCompanyBean valeo = (UKOnlineCompanyBean)UKOnlineCompanyBean.getInstance();
		Iterator itr = CheckoutCodeBean.getCheckoutCodes(valeo).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean checkout_code_obj = (CheckoutCodeBean)itr.next();
			if (checkout_code_obj.getType() == CheckoutCodeBean.PLAN_TYPE)
			{
				// see if there's an existing plan for this checkout code

				PaymentPlanBean payment_plan = null;

				try
				{
					payment_plan = PaymentPlanBean.getPaymentPlan(checkout_code_obj);
				}
				catch (ObjectNotFoundException x)
				{
					System.out.println(x.getMessage());

					// payment plan does not exist for this checkout code.  create it

					try
					{
						payment_plan = new PaymentPlanBean();
						payment_plan.setAllowGroupPooling(false);
						payment_plan.setAllowMonthlyPayments(true);
						payment_plan.setCheckoutCode(checkout_code_obj);
						payment_plan.setCompany(valeo);
						payment_plan.setCost(checkout_code_obj.getAmount());
						payment_plan.setName(checkout_code_obj.getDescription());
						payment_plan.setNote("");
						payment_plan.setPracticeArea(checkout_code_obj.getPracticeArea());
						payment_plan.setType(PaymentPlanBean.CASH_BILLING_TYPE);
						payment_plan.save();
					}
					catch (ObjectNotFoundException y)
					{
						System.out.println("practice area not found for checkout code >" + checkout_code_obj.getLabel());
						y.printStackTrace();
					}
				}
			}
		}


		int num_plans_purchased = 0;
		int num_no_match = 0;
		int appointments_found = 0;
		int appointments_not_found = 0;

		itr = UKOnlinePersonBean.getPersons(valeo).iterator();
		while (itr.hasNext())
		{
			HashMap plan_purchase_date_hash = new HashMap();
			PersonBean person_obj_x = (PersonBean)itr.next();
			UKOnlinePersonBean person_obj = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person_obj_x.getId());

			if (!person_obj.getLabel().equals("Correction Sales"))
			{
				System.out.println("PERSON >" + person_obj.getLabel());
				Iterator order_itr = OrderBean.getOrders(person_obj).iterator();
				HashMap practice_area_plan_hash = new HashMap(11);
				while (order_itr.hasNext())
				{
					OrderBean order_obj = (OrderBean)order_itr.next();
					BigDecimal amount_paid_on_order = BigDecimal.ZERO;
					Iterator tender_itr = TenderRet.getTenders(order_obj).iterator();
					while (tender_itr.hasNext())
					{
						TenderRet tender = (TenderRet)tender_itr.next();
						amount_paid_on_order = amount_paid_on_order.add(tender.getAmountBigDecimal());
					}
					//System.out.println("order_obj.getTotal() >" + order_obj.getTotal().toString());
					//System.out.println("amount_paid_on_order >" + amount_paid_on_order.toString());
					boolean order_paid_for = (order_obj.getTotal().compareTo(amount_paid_on_order) != 1);
					//System.out.println("order_paid_for >" + order_paid_for);

					Iterator order_lines_itr = order_obj.getOrders();
					while (order_lines_itr.hasNext())
					{
						CheckoutOrderline orderline_obj = (CheckoutOrderline)order_lines_itr.next();
						CheckoutCodeBean checkout_code_obj = CheckoutCodeBean.getCheckoutCode(orderline_obj.getCheckoutCodeId());

						String order_date_string = order_obj.getOrderDateString();


						if (checkout_code_obj.getType() == CheckoutCodeBean.PLAN_TYPE)
						{
							PaymentPlanBean purchased_plan = PaymentPlanBean.getPaymentPlan(checkout_code_obj);
							PracticeAreaBean practice_area_for_checkout_code = checkout_code_obj.getPracticeArea();
							String key = practice_area_for_checkout_code.getValue();

							// there needs to be a payment plan instance of this plan for this person
							// is there an existing?

							//PaymentPlanInstanceBean.getPaymentPlanInstances(_person)

							System.out.println(order_date_string + "   - qty >" + orderline_obj.getQuantity().toString() + "  " + checkout_code_obj.getLabel() );

							if (orderline_obj.getQuantity().compareTo(BigDecimal.ZERO) == 1)
							{
								PaymentPlanInstanceBean plan_instance = null;

								try
								{
									plan_instance = PaymentPlanInstanceBean.getPaymentPlanInstance(person_obj, practice_area_for_checkout_code, order_obj.getOrderDate());
								}
								catch (ObjectNotFoundException x)
								{
									System.out.println(x.getMessage());

									// couldn't find an existing payment plan instance for this practice area on the given date.  create a new one

									plan_instance = new PaymentPlanInstanceBean();
									plan_instance.setAmountCharged(orderline_obj.getActualAmount());
									if (order_paid_for)
										plan_instance.setAmountPaid(orderline_obj.getActualAmount());
									else
										plan_instance.setAmountPaid(BigDecimal.ZERO);
									plan_instance.setCreatePerson((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(1));
									plan_instance.setGroupPoolingAllowed(true);
									plan_instance.setIsPaidFor(order_paid_for);
									plan_instance.setPaymentPlan(purchased_plan);
									plan_instance.setPerson(person_obj);
									plan_instance.setStartDate(order_obj.getOrderDate());
									plan_instance.activate();
									plan_instance.setNumberOfVisitsRemaining(purchased_plan.getVisits());
									plan_instance.save();
								}
								catch (UniqueObjectNotFoundException x)
								{
									System.out.println(x.getMessage());
								}

								num_plans_purchased++;

								/*
								if (plan_purchase_date_hash.containsKey(key))
									System.out.println("contains key... " + key + " -  " + person_obj.getLabel());
								else
									plan_purchase_date_hash.put(key, "thurple");
								 */

								System.out.println("plan_instance >" + plan_instance);
								if (plan_instance != null)
								{
									Vector practice_area_plan_hash_vec = (Vector)practice_area_plan_hash.get(key);
									if (practice_area_plan_hash_vec == null)
									{
										practice_area_plan_hash_vec = new Vector();
										System.out.println("putting plan instance >" + key);
										practice_area_plan_hash.put(key, practice_area_plan_hash_vec);
									}
									practice_area_plan_hash_vec.addElement(plan_instance);
								}
							}

							// get the Payment Plan for this

							/*
							PaymentPlanBean payment_plan = PaymentPlanBean.getPaymentPlan(checkout_code_obj);
							PaymentPlanInstanceBean plan_instance = null;
							try
							{
								PaymentPlanInstanceBean
							}
							 */
						}
						else if (checkout_code_obj.getType() == CheckoutCodeBean.PROCEDURE_TYPE)
						{
							// see if this counts against a plan

							if (checkout_code_obj.isPlanUse())
							{
								PracticeAreaBean practice_area_for_checkout_code = checkout_code_obj.getPracticeArea();
								String key = practice_area_for_checkout_code.getValue();

								try
								{
									System.out.println(order_date_string + "  " + checkout_code_obj.getLabel() +  "    -   found plan use for practice area >" + practice_area_for_checkout_code.getLabel());

									System.out.println("getting plan instance >" + key);
									Vector plan_instances = (Vector)practice_area_plan_hash.get(key);
									System.out.println("plan_instances >" + plan_instances);
									if (plan_instances == null)
										System.out.println("no plan instances for visit >" + practice_area_for_checkout_code.getLabel());
									else
									{
										System.out.println("plan instances found >" + plan_instances.size());

										// loop through the vec and find a free plan instance

										Iterator plan_instance_itr = plan_instances.iterator();
										while (plan_instance_itr.hasNext())
										{
											PaymentPlanInstanceBean plan_instance = (PaymentPlanInstanceBean)plan_instance_itr.next();
											System.out.println("plan_instance >" + plan_instance);
											System.out.println("plan_instance.hasVisitsRemaining() >" + plan_instance.hasVisitsRemaining());
											if (plan_instance.hasVisitsRemaining())
											{
												// associate the appointment that corresponds to this order(?)

												// OK.  I really think that Appointments should be associated with plan_instances.  but I think it's best to do this after I
												// eliminate PoS.  Problems include duplicate clients - person being checked out of PoS doesn't match person on schedule
												// - plan sharing among family members perhaps???

												try
												{
													System.out.println("orderline_obj.getAppointmentId() >" + orderline_obj.getAppointmentId());
													AppointmentBean appointment_plan_use = AppointmentBean.getAppointment(orderline_obj.getAppointmentId());
													appointments_found++;
												}
												catch (ObjectNotFoundException x)
												{
													System.out.println("APPT NOT FOUND >" + checkout_code_obj.getLabel());
													System.out.println("orderline_obj.getOrderid() >" + orderline_obj.getOrderid());

													// try to find the appointment for this...

													Vector appointments = AppointmentBean.getAppointmentsForClientOnDate(person_obj, practice_area_for_checkout_code, order_obj.getOrderDate(), AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS);
													if (appointments.size() == 0)
														appointments = AppointmentBean.getAppointmentsForClientOnDate(person_obj, practice_area_for_checkout_code, order_obj.getOrderDate(), AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS);
													System.out.println("appointments >" + appointments.size());


													appointments_not_found++;
												}

												plan_instance.useVisit();
												plan_instance.save();
											}
										}
									}
								}
								catch (Exception x)
								{
									x.printStackTrace();
								}
							}

						}

					}

				}

			}
		}

		System.out.println("num_plans_purchased >" + num_plans_purchased);
		System.out.println("num_no_match >" + num_no_match);
		System.out.println("appointments_found >" + appointments_found);
		System.out.println("appointments_not_found >" + appointments_not_found);

	}
}