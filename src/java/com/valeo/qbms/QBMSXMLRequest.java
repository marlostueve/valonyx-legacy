/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbms;

import com.generationjava.io.WritingException;
import com.generationjava.io.xml.XmlWriter;
import com.valeo.qbpos.QBPOSXMLRequest;

/**
 *
 * @author marlo
 */
public abstract class
QBMSXMLRequest
	extends QBPOSXMLRequest {

	// CLASS VARIABLES

	//protected static SimpleDateFormat dateTypeFormat = new SimpleDateFormat("yyyy-MM-dd");

	// CONSTRUCTORS

	public
	QBMSXMLRequest()
	{

	}

	// METHODS

	@Override
	protected String
	assembleRequest()
		throws WritingException
	{
		writer = new java.io.StringWriter();
		xmlwriter = new XmlWriter(writer);

		xmlwriter.writeEntity("QBMSXML");

		assembleRequestBody();

		xmlwriter.endEntity();
		xmlwriter.close();
		
		return null; // just to get it to compile for now
	}

}
