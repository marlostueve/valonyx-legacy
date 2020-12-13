/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;

import com.badiyan.torque.QbRequestDb;
import com.badiyan.torque.QbRequestDbPeer;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.generationjava.io.WritingException;
import com.valeo.qb.data.VendorRet;
import com.valeo.qbpos.QBPOSXMLRequest;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class QBXMLVendorModRequest
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

			qb_request = QBXMLVendorModRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLVendorModRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private VendorRet vendor;
	
	// CONSTRUCTORS

    public
    QBXMLVendorModRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLVendorModRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

	public void
	setVendor(VendorRet _vendor)
	{
		vendor = _vendor;
	}

	@Override
	protected void assembleRequestBody() throws WritingException {

		/*
		 *     <VendorAddRq requestID = "UUIDTYPE">
      <VendorAdd>
        <Name>STRTYPE</Name>                                <!-- max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 100 for QBOE -->
        <IsActive>BOOLTYPE</IsActive>                       <!-- opt, not in QBOE -->
        <CompanyName>STRTYPE</CompanyName>                  <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 50 for QBOE -->
        <Salutation>STRTYPE</Salutation>                    <!-- opt, max length = 15 -->
        <FirstName>STRTYPE</FirstName>                      <!-- opt, max length = 25 -->
        <MiddleName>STRTYPE</MiddleName>                    <!-- opt, max length = 5 for QBD|QBCA|QBUK|QBAU, max length = 25 for QBOE -->
        <LastName>STRTYPE</LastName>                        <!-- opt, max length = 25 -->
        <Suffix>STRTYPE</Suffix>                            <!-- opt, max length = 10 for QBOE, not in QBD|QBCA|QBUK|QBAU -->
        <VendorAddress>                                     <!-- opt -->
          <Addr1>STRTYPE</Addr1>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr2>STRTYPE</Addr2>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr3>STRTYPE</Addr3>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE -->
          <Addr4>STRTYPE</Addr4>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 500 for QBOE, v2.0 -->
          <Addr5>STRTYPE</Addr5>                            <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
          <City>STRTYPE</City>                              <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <State>STRTYPE</State>                            <!-- opt, max length = 21 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <PostalCode>STRTYPE</PostalCode>                  <!-- opt, max length = 13 for QBD|QBCA|QBUK|QBAU, max length = 30 for QBOE -->
          <Country>STRTYPE</Country>                        <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 255 for QBOE -->
          <Note>STRTYPE</Note>                              <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, not in QBOE, v6.0 -->
        </VendorAddress>
        <Phone>STRTYPE</Phone>                              <!-- opt, max length = 21 -->
        <Mobile>STRTYPE</Mobile>                            <!-- opt, max length = 21 for QBOE, not in QBD|QBCA|QBUK|QBAU -->
        <Pager>STRTYPE</Pager>                              <!-- opt, max length = 21 for QBOE, not in QBD|QBCA|QBUK|QBAU -->
        <AltPhone>STRTYPE</AltPhone>                        <!-- opt, max length = 21 -->
        <Fax>STRTYPE</Fax>                                  <!-- opt, max length = 21 -->
        <Email>STRTYPE</Email>                              <!-- opt, max length = 1023 for QBD|QBCA|QBUK|QBAU, max length = 100 for QBOE -->
        <Contact>STRTYPE</Contact>                          <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, not in QBOE -->
        <AltContact>STRTYPE</AltContact>                    <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, not in QBOE -->
        <NameOnCheck>STRTYPE</NameOnCheck>                  <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU, max length = 110 for QBOE -->
        <AccountNumber>STRTYPE</AccountNumber>              <!-- opt, max length = 99 for QBD|QBCA|QBUK|QBAU, max length = 100 for QBOE -->
        <Notes>STRTYPE</Notes>                              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU, not in QBOE, v3.0 -->
        <VendorTypeRef>                                     <!-- opt, not in QBOE -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
        </VendorTypeRef>
        <TermsRef>                                          <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 100 for QBOE -->
        </TermsRef>
        <CreditLimit>AMTTYPE</CreditLimit>                  <!-- opt, not in QBOE -->
        <VendorTaxIdent>STRTYPE</VendorTaxIdent>            <!-- opt, max length = 15 for QBD|QBCA|QBUK|QBAU, max length = 20 for QBOE -->
        <IsVendorEligibleFor1099>BOOLTYPE</IsVendorEligibleFor1099> <!-- opt, not in QBCA|QBUK -->
        <OpenBalance>AMTTYPE</OpenBalance>                  <!-- opt -->
        <OpenBalanceDate>DATETYPE</OpenBalanceDate>         <!-- opt -->
        <BillingRateRef>                                    <!-- opt, not in QBOE, v6.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
        </BillingRateRef>
        <ExternalGUID>GUIDTYPE</ExternalGUID>               <!-- opt, not in QBOE, v8.0 -->
        <SalesTaxCodeRef>                                   <!-- opt, not in QBD|QBOE, v8.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
        </SalesTaxCodeRef>
        <!-- SalesTaxCountry may have one of the following values: Australia, Canada [DEFAULT], UK -->
        <SalesTaxCountry>ENUMTYPE</SalesTaxCountry>         <!-- opt, not in QBD|QBOE, v8.0 -->
        <IsSalesTaxAgency>BOOLTYPE</IsSalesTaxAgency>       <!-- opt, not in QBD|QBOE, v8.0 -->
        <SalesTaxReturnRef>                                 <!-- opt, not in QBD|QBOE, v8.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 41 for QBCA|QBUK|QBAU -->
        </SalesTaxReturnRef>
        <TaxRegistrationNumber>STRTYPE</TaxRegistrationNumber> <!-- opt, max length = 30 for QBCA|QBUK|QBAU, not in QBD|QBOE, v8.0 -->
        <!-- ReportingPeriod may have one of the following values: Monthly, Quarterly [DEFAULT] -->
        <ReportingPeriod>ENUMTYPE</ReportingPeriod>         <!-- opt, not in QBD|QBOE, v8.0 -->
        <IsTaxTrackedOnPurchases>BOOLTYPE</IsTaxTrackedOnPurchases> <!-- opt, not in QBD|QBOE, v8.0 -->
        <TaxOnPurchasesAccountRef>                          <!-- opt, not in QBD|QBOE, v8.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBCA|QBUK|QBAU -->
        </TaxOnPurchasesAccountRef>
        <IsTaxTrackedOnSales>BOOLTYPE</IsTaxTrackedOnSales> <!-- opt, not in QBD|QBOE, v8.0 -->
        <TaxOnSalesAccountRef>                              <!-- opt, not in QBD|QBOE, v8.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBCA|QBUK|QBAU -->
        </TaxOnSalesAccountRef>
        <IsTaxOnTax>BOOLTYPE</IsTaxOnTax>                   <!-- opt, not in QBD|QBOE, v8.0 -->
        <PrefillAccountRef>                                 <!-- opt, may rep, not in QBOE, v8.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
        </PrefillAccountRef>
        <CurrencyRef>                                       <!-- opt, not in QBOE, v8.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 64 for QBD|QBCA|QBUK|QBAU -->
        </CurrencyRef>
      </VendorAdd>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, not in QBOE, v4.0 -->
    </VendorAddRq>
		 *
		 */

		xmlwriter.writeEntity("VendorModRq");
		xmlwriter.writeAttribute("requestID", getRequestId());

		xmlwriter.writeEntity("VendorMod");

		xmlwriter.writeEntity("ListID");
		xmlwriter.writeText(vendor.getListID());
		xmlwriter.endEntity();

		xmlwriter.writeEntity("EditSequence");
		xmlwriter.writeText(vendor.getEditSequence());
		xmlwriter.endEntity();

		xmlwriter.writeEntity("Name");
		xmlwriter.writeText(vendor.getLabel());
		xmlwriter.endEntity();

		xmlwriter.writeEntity("IsActive");
		xmlwriter.writeText(vendor.isActive() ? "1" : "0");
		xmlwriter.endEntity();

		xmlwriter.writeEntity("CompanyName");
		xmlwriter.writeText(vendor.getLabel());
		xmlwriter.endEntity();

		try
		{
			AddressBean vendorAddress = vendor.getAddress();

			xmlwriter.writeEntity("VendorAddress");

			xmlwriter.writeEntity("Addr2");
			xmlwriter.writeText(vendorAddress.getStreet1String());
			xmlwriter.endEntity();
			if (vendorAddress.getStreet2String().length() > 0)
			{
				xmlwriter.writeEntity("Addr3");
				xmlwriter.writeText(vendorAddress.getStreet2String());
				xmlwriter.endEntity();
			}
			xmlwriter.writeEntity("City");
			xmlwriter.writeText(vendorAddress.getCityString());
			xmlwriter.endEntity();
			xmlwriter.writeEntity("State");
			xmlwriter.writeText(vendorAddress.getStateString());
			xmlwriter.endEntity();
			xmlwriter.writeEntity("PostalCode");
			xmlwriter.writeText(vendorAddress.getZipCodeString());
			xmlwriter.endEntity();
			if (vendorAddress.getCountryString().length() > 0)
			{
				xmlwriter.writeEntity("Country");
				xmlwriter.writeText(vendorAddress.getCountryString());
				xmlwriter.endEntity();
			}

			xmlwriter.endEntity(); // </VendorAddress>

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		
		if (vendor.getPhone().length() > 0)
		{
			xmlwriter.writeEntity("Phone");
			xmlwriter.writeText(vendor.getPhone());
			xmlwriter.endEntity();
		}

		if (vendor.getFax().length() > 0)
		{
			xmlwriter.writeEntity("Fax");
			xmlwriter.writeText(vendor.getFax());
			xmlwriter.endEntity();
		}

		if (vendor.getAccountNumber().length() > 0)
		{
			xmlwriter.writeEntity("AccountNumber");
			xmlwriter.writeText(vendor.getAccountNumber());
			xmlwriter.endEntity();
		}

		xmlwriter.endEntity(); // </VendorAdd>

		xmlwriter.endEntity(); // </VendorAddRq>
	}

	@Override
	public String getLabel() {
		return CUBean.getUserTimeString(super.creation_date) + " - Vendor Mod Request";
	}

	@Override
	public String getRequestType() {
		return "VendorModRq";
	}

}
