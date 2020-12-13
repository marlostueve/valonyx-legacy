/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.UUID;

import com.generationjava.io.WritingException;
import com.generationjava.io.xml.XmlWriter;
import com.valeo.qbpos.QBPOSXMLRequest;

/**
 *
 * @author marlo
 */
public abstract class
QBXMLRequest
	extends QBPOSXMLRequest
{
	// CLASS VARIABLES

	protected static SimpleDateFormat dateTypeFormat = new SimpleDateFormat("yyyy-MM-dd");

	// CONSTRUCTORS

	public
	QBXMLRequest()
	{

	}

	// METHODS

	@Override
	protected String
	assembleRequest()
		throws WritingException
	{
		System.out.println("assembleRequest() invoked in QBXMLRequest");
		
		// I'm having an issue where some object references (references to new inventory, for example) can be "null" since these requests are being generated ahead of time.
		// I'm thinking that I need to go ahead and completely regenerate the request...
		
		System.out.println("xml_str(1) >" + xml_str);
		
		if (xml_str == null || (xml_str.indexOf("null") > -1) || iterate) // when it's an iterator the request string will change as it iterates
		{
			// first check the db for the assembled request.
			
			// I don't think this'll work properly for persisted iterators.  I may have to expand the columns that I persist to make this work right - not sure...
			
			boolean not_null_str_and_contains_null = ((xml_str != null) && (xml_str.indexOf("null") != -1));
			System.out.println("xml_str(2) >" + xml_str);
			
			System.out.println("this.isNew() >" + this.isNew());
			System.out.println("iterate >" + iterate);
			System.out.println("not_null_str_and_contains_null >" + not_null_str_and_contains_null);
			
			if (!this.isNew() && !iterate && !not_null_str_and_contains_null) // if I'm iterating I need to generate a fresh request
			{
				xml_str = this.getRequestXMLString();
			}
			else
			{
				System.out.println("re-assembling request");
				
				writer = new java.io.StringWriter();
				xmlwriter = new XmlWriter(writer);

				xmlwriter.writeEntity("QBXML");
				xmlwriter.writeEntity("QBXMLMsgsRq");
				xmlwriter.writeAttribute("onError", "stopOnError");

				assembleRequestBody();

				xmlwriter.endEntity();
				xmlwriter.endEntity();
				xmlwriter.close();
				
				xml_str = writer.toString();
			}
		}
		
		return xml_str;
	}

	@Override
	public String toXMLString() {

		try
		{
			String str = assembleRequest();
			System.out.println("GEN XML >" + str);
			return "<?xml version=\"1.0\" ?><?qbxml version=\"7.0\"?>" + str;
		}
		catch (WritingException x)
		{
			x.printStackTrace();
			return "";
		}

	}
}
