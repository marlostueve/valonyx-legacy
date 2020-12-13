/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.tasks.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.servlets.*;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.valeo.qbpos.*;
import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;

public class
NewClientMonitorTask
    extends TimerTask
{
    // INSTANCE VARIABLES

	private CompanyBean company;

    // CONSTRUCTORS

    public
    NewClientMonitorTask(CompanyBean _company)
    {
		company = _company;
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in NewClientMonitorTask");

		try
		{


			Iterator itr = company.getPeople().iterator();
			while (itr.hasNext())
			{
				PersonBean person_obj = (PersonBean)itr.next();
				UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person_obj.getId());

				System.out.println();
				if (person.getEmployeeNumberString().equals(""))
				{
					/*
					Calendar hour_ago = Calendar.getInstance();
					hour_ago.add(Calendar.HOUR, -1);
					Calendar person_creation_date = Calendar.getInstance();
					person_creation_date.setTime(person.getCreationDate());

					if ()
					 */

					if (!QBWebConnectorSvcSoapImpl.qbpos_request_queue.containsMatchingCustomer(QBWebConnectorSvcSoapImpl.qbpos_company_key, person))
					{

						System.out.println("Try to sync >" + person.getLabel());

						// no employee id for this person.  try to sync it with PoS, I guess

						QBPOSXMLCustomerAddRequest req_obj = new QBPOSXMLCustomerAddRequest();
						req_obj.setEmailString(person.getEmail1String());
						req_obj.setFirstNameString(person.getFirstNameString());
						req_obj.setLastNameString(person.getLastNameString());

						try
						{
							PhoneNumberBean home_phone_number = person.getPhoneNumber(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
							req_obj.setPhoneString(home_phone_number.getNumberString());
						}
						catch (Exception x)
						{

						}

						try
						{
							PhoneNumberBean cell_phone_number = person.getPhoneNumber(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
							req_obj.setPhone2String(cell_phone_number.getNumberString());
						}
						catch (Exception x)
						{

						}

						QBWebConnectorSvcSoapImpl.qbpos_request_queue.add(QBWebConnectorSvcSoapImpl.qbpos_company_key, req_obj);
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
