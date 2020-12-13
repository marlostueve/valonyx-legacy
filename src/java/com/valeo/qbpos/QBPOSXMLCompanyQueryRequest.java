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
QBPOSXMLCompanyQueryRequest
	extends QBPOSXMLRequest
{
	// INSTANCE METHODS
	
	public String
	toXMLString()
	{
		/*
		 * <?xml version="1.0" ?>
<?qbposxml version="1.0"?>
<QBPOSXML>
  <QBPOSXMLMsgsRq onError="stopOnError">
    <CompanyQueryRq requestID = "1">
    </CompanyQueryRq>
  </QBPOSXMLMsgsRq>
</QBPOSXML>

		 */
		
		try
		{
			assembleRequest();
			
			System.out.println("GEN XML >" + writer.toString());
			return "<?xml version=\"1.0\" ?><?qbposxml version=\"1.0\"?>" + writer.toString();
		}
		catch (WritingException x)
		{
			x.printStackTrace();
			return "";
		}
	}

	@Override
	public String
	getLabel()
	{
		return CUBean.getUserTimeString(super.creation_date) + " - Company Query Request";
	}
	
	protected void
	assembleRequestBody()
		throws WritingException
	{
		xmlwriter.writeEntity("CompanyQueryRq");
		xmlwriter.writeAttribute("requestID", getRequestId());
		xmlwriter.endEntity();
	}

	@Override
	public String getRequestType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
