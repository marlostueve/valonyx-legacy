/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbms;

import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.generationjava.io.WritingException;
import com.valeo.qb.QBXMLRequest;
import java.util.Date;

/**
 *
 * @author marlo
 */
public class
QBMSXMLSignonAppCertRq
	extends QBMSXMLRequest
{

	@Override
	protected void assembleRequestBody() throws WritingException {

		/*
		 * <?xml version="1.0" ?>
			<?qbmsxml version="2.0"?>
			<QBMSXML>
			<SignonMsgsRq>
			<SignonAppCertRq>
			<ClientDateTime>2006-09-20T15:49:26</ClientDateTime>
			<ApplicationLogin>WebTxnTester.intuit.com</ApplicationLogin>
			<ConnectionTicket>TGT-77-102983765412908762935Q</ConnectionTicket>
			</SignonAppCertRq>
			</SignonMsgsRq>
			</QBMSXML>
		 */

		xmlwriter.writeEntity("SignonMsgsRq");
		xmlwriter.writeEntity("SignonAppCertRq");
		xmlwriter.writeAttribute("requestID", getRequestId());

		xmlwriter.writeEntity("ClientDateTime"); xmlwriter.writeText(QBXMLRequest.dateTimeTypeFormat.format(new Date())); xmlwriter.endEntity();
		xmlwriter.writeEntity("ApplicationLogin"); xmlwriter.writeText("WebTxnTester.intuit.com"); xmlwriter.endEntity();
		xmlwriter.writeEntity("ConnectionTicket"); xmlwriter.writeText("TGT-77-102983765412908762935Q"); xmlwriter.endEntity();

		xmlwriter.endEntity();
		xmlwriter.endEntity();
	}

	@Override
	public String getLabel() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String toXMLString() {

		try
		{
			super.assembleRequest();

			System.out.println("GEN XML >" + writer.toString());
			return "<?xml version=\"1.0\" ?><?qbmsxml version=\"2.0\"?>" + writer.toString();
		}
		catch (WritingException x)
		{
			x.printStackTrace();
			return "";
		}
	}

	@Override
	public String getRequestType() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
