/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbpos;

import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.generationjava.io.WritingException;

/**
 *
 * @author marlo
 */
public class
QBPOSXMLCustomerAddRequest
	extends QBPOSXMLRequest
{
	// INSTANCE VARIABLES

	private String company_str = null;
	private String email_str = null;
	private String first_name_str = null;
	private String last_name_str = null;
	private String phone_str = null;
	private String cell_str = null;

	// INSTANCE METHODS

	public void
	setCompanyString(String _str)
	{
		company_str = _str;
	}

	public void
	setEmailString(String _str)
	{
		email_str = _str;
	}

	public void
	setFirstNameString(String _str)
	{
		first_name_str = _str;
	}

	public void
	setLastNameString(String _str)
	{
		last_name_str = _str;
	}

	public void
	setPhoneString(String _str)
	{
		phone_str = _str;
	}

	public void
	setPhone2String(String _str)
	{
		cell_str = _str;
	}

	public String
	toXMLString()
	{
		/*
		 * <?xml version="1.0" ?>
<?qbposxml version="1.0"?>
<QBPOSXML>
  <QBPOSXMLMsgsRq onError="stopOnError">
    <CustomerAddRq requestID = "26">
      <CustomerAdd>
        <CompanyName>CompanyName</CompanyName>                  <!-- opt, field max = 40 -->
        <!-- CustomerDiscType may have one of the following values: None,PriceLevel,Percentage -->
	<CustomerDiscType>PriceLevel</CustomerDiscType>       <!-- opt -->
        <EMail>EMail</EMail>                              <!-- opt, field max = 99 -->
        <FirstName>Young</FirstName>                      <!-- opt, field max = 30 -->
        <LastName>Yuk</LastName>                        <!-- field max = 30 -->
        <Notes>Notes</Notes>                              <!-- opt, field max = 245 -->
        <Phone>Phone</Phone>                              <!-- opt, field max = 21 -->
        <Phone2>Phone2</Phone2>                        <!-- opt, field max = 21 -->
        <!-- PriceLevelNumber may have one of the following values: 1,2,3,4 -->
        <PriceLevelNumber>1</PriceLevelNumber>       <!-- opt -->
        <Salutation>Mr</Salutation>                    <!-- opt, field max = 15 -->
        <BillAddress>                                       <!-- opt -->
		<City>City</City>
  		<PostalCode>PostalCode</PostalCode>
  		<State>State</State>
  		<Street>BillAddr1</Street>
  	</BillAddress>
        <ShipAddress>                                       <!-- opt -->
  		<City>City</City>
  		<PostalCode>PostalCode</PostalCode>
  		<State>CA</State>
  		<Street>ShipAddr1</Street>
      </ShipAddress>
      </CustomerAdd>
    </CustomerAddRq>
  </QBPOSXMLMsgsRq>
</QBPOSXML>

		 */

		try
		{
			super.assembleRequest();

			System.out.println("GEN XML >" + writer.toString());
			return "<?xml version=\"1.0\" ?><?qbposxml version=\"1.0\"?>" + writer.toString();
		}
		catch (WritingException x)
		{
			x.printStackTrace();
			return "";
		}
	}

	protected void
	assembleRequestBody()
		throws WritingException
	{
		xmlwriter.writeEntity("CustomerAddRq");
		xmlwriter.writeAttribute("requestID", getRequestId());

		xmlwriter.writeEntity("CustomerAdd");

		if (company_str != null)
		{
			xmlwriter.writeEntity("CompanyName");
			xmlwriter.writeText(company_str);
			xmlwriter.endEntity();
		}

		if (email_str != null)
		{
			xmlwriter.writeEntity("EMail");
			xmlwriter.writeText(email_str);
			xmlwriter.endEntity();
		}

		if (first_name_str != null)
		{
			xmlwriter.writeEntity("FirstName");
			xmlwriter.writeText(first_name_str);
			xmlwriter.endEntity();
		}

		if (last_name_str != null)
		{
			xmlwriter.writeEntity("LastName");
			xmlwriter.writeText(last_name_str);
			xmlwriter.endEntity();
		}

		if (phone_str != null)
		{
			xmlwriter.writeEntity("Phone");
			xmlwriter.writeText(phone_str);
			xmlwriter.endEntity();
		}

		if (cell_str != null)
		{
			xmlwriter.writeEntity("Phone2");
			xmlwriter.writeText(cell_str);
			xmlwriter.endEntity();
		}

		xmlwriter.endEntity();
		xmlwriter.endEntity();

	}

	@Override
	public String
	getLabel()
	{
		return CUBean.getUserTimeString(super.creation_date) + " - Customer Add Request";
	}

	@Override
	public String getRequestType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
