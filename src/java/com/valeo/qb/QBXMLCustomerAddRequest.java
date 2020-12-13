/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;

import com.badiyan.torque.QbRequestDb;
import com.badiyan.torque.QbRequestDbPeer;
import com.badiyan.uk.beans.AddressBean;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.beans.PhoneNumberBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import com.generationjava.io.WritingException;
import com.valeo.qbpos.QBPOSXMLRequest;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class QBXMLCustomerAddRequest 
	extends QBXMLRequest {

    // CLASS METHODS

    public static QBPOSXMLRequest
    getRequest(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		//QB_REQUEST_DB
		QBPOSXMLRequest qb_request = (QBPOSXMLRequest)hash.get(key);
		if (qb_request == null)
		{
			Criteria crit = new Criteria();
			crit.add(QbRequestDbPeer.QB_REQUEST_DB_ID, _id);
			List objList = QbRequestDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate QB Request with id: " + _id);

			qb_request = QBXMLCustomerAddRequest.getRequest((QbRequestDb)objList.get(0));
		}

		return qb_request;
    }

    private static QBPOSXMLRequest
    getRequest(QbRequestDb _request)
		throws TorqueException
    {
		Integer key = new Integer(_request.getQbRequestDbId());
		QBPOSXMLRequest qb_request = (QBPOSXMLRequest)hash.get(key);
		if (qb_request == null)
		{
			qb_request = new QBXMLCustomerAddRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private UKOnlinePersonBean customer;
	
	// CONSTRUCTORS

    public
    QBXMLCustomerAddRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLCustomerAddRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

	public void
	setCustomer(UKOnlinePersonBean _customer)
	{
		customer = _customer;
	}

	@Override
	protected void assembleRequestBody() throws WritingException {

		xmlwriter.writeEntity("CustomerAddRq");
		xmlwriter.writeAttribute("requestID", getRequestId());

		xmlwriter.writeEntity("CustomerAdd");
		
		String label = customer.getLabel();
		if (label.length() > 41)
			label = label.substring(0, 40);

		xmlwriter.writeEntity("Name");
		xmlwriter.writeText(label);
		xmlwriter.endEntity();

		/*
		xmlwriter.writeEntity("FullName");
		xmlwriter.writeText(customer.getLabel());
		xmlwriter.endEntity();
		 */
		
		xmlwriter.writeEntity("IsActive");
		xmlwriter.writeText(customer.isActive() ? "true" : "false");
		xmlwriter.endEntity();
		xmlwriter.writeEntity("FirstName");
		xmlwriter.writeText(customer.getFirstNameString());
		xmlwriter.endEntity();
		xmlwriter.writeEntity("LastName");
		xmlwriter.writeText(customer.getLastNameString());
		xmlwriter.endEntity();

		try
		{
			AddressBean customerAddress = customer.getAddress(AddressBean.PERSON_ADDRESS_TYPE);

			xmlwriter.writeEntity("BillAddress");

			xmlwriter.writeEntity("Addr1");
			xmlwriter.writeText(customer.getFirstNameString() + " " + customer.getLastNameString());
			xmlwriter.endEntity();
			xmlwriter.writeEntity("Addr2");
			xmlwriter.writeText(customerAddress.getStreet1String());
			xmlwriter.endEntity();
			if (customerAddress.getStreet2String().length() > 0)
			{
				xmlwriter.writeEntity("Addr3");
				xmlwriter.writeText(customerAddress.getStreet2String());
				xmlwriter.endEntity();
			}
			xmlwriter.writeEntity("City");
			xmlwriter.writeText(customerAddress.getCityString());
			xmlwriter.endEntity();
			xmlwriter.writeEntity("State");
			xmlwriter.writeText(customerAddress.getStateString());
			xmlwriter.endEntity();
			xmlwriter.writeEntity("PostalCode");
			xmlwriter.writeText(customerAddress.getZipCodeString());
			xmlwriter.endEntity();
			if (customerAddress.getCountryString().length() > 0)
			{
				xmlwriter.writeEntity("Country");
				xmlwriter.writeText(customerAddress.getCountryString());
				xmlwriter.endEntity();
			}

			xmlwriter.endEntity(); // </BillAddress>

		}
		catch (Exception x)
		{
			//x.printStackTrace();
		}

		try
		{
			PhoneNumberBean home_phone = customer.getPhoneNumber(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			xmlwriter.writeEntity("Phone");
			xmlwriter.writeText(home_phone.getNumberString());
			xmlwriter.endEntity();
		}
		catch (Exception x)
		{
		}

		try
		{
			PhoneNumberBean cell_phone = customer.getPhoneNumber(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			xmlwriter.writeEntity("AltPhone");
			xmlwriter.writeText(cell_phone.getNumberString());
			xmlwriter.endEntity();
		}
		catch (Exception x)
		{
		}

		try
		{
			PhoneNumberBean fax_phone = customer.getPhoneNumber(PhoneNumberBean.FAX_PHONE_NUMBER_TYPE);
			xmlwriter.writeEntity("Fax");
			xmlwriter.writeText(fax_phone.getNumberString());
			xmlwriter.endEntity();
		}
		catch (Exception x)
		{
		}

		if (customer.getEmail1String().length() > 0)
		{
			xmlwriter.writeEntity("Email");
			xmlwriter.writeText(customer.getEmail1String());
			xmlwriter.endEntity();
		}

		if (customer.getCommentString().length() > 0)
		{
			xmlwriter.writeEntity("Notes");
			xmlwriter.writeText(customer.getCommentString());
			xmlwriter.endEntity();
		}

		xmlwriter.endEntity(); // </CustomerAdd>

		xmlwriter.endEntity(); // </CustomerAddRq>
		
	}

	@Override
	public String getLabel() {
		return CUBean.getUserTimeString(super.creation_date) + " - Customer Add Request";
	}

	@Override
	public String getRequestType() {
		return "CustomerAddRq";
	}

}
