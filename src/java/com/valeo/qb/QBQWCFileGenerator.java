/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;


import java.io.*;
import java.util.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.beans.*;

import com.badiyan.uk.exceptions.*;

import com.generationjava.io.WritingException;
import com.generationjava.io.xml.XmlWriter;
import org.apache.torque.TorqueException;

public class
QBQWCFileGenerator
{


	private static Writer writer;
	private static XmlWriter xmlwriter;


	/**
	 * Manages the creation of the printed plan's individual pages.
	 * @param args
	 */
	public static String
	generateQWCFile(UKOnlineCompanyBean _practice)
		throws TorqueException, ObjectAlreadyExistsException, IllegalValueException, WritingException, IOException, Exception
	{
		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		//String pdf_file = "C:\\odd.pdf";
		String file_name = _practice.getLabel() + "-" + System.currentTimeMillis() + ".qwc";
		String qwc_file = resourcesFolder + "qwc/" + file_name;



		String qbwc_string = QBQWCFileGenerator.generateQWCString(_practice);

		System.out.println(qbwc_string);

		Writer output = null;
		File file = new File(qwc_file);
		output = new BufferedWriter(new FileWriter(file));
		output.write(qbwc_string);
		output.close();


		return file_name;

	} // generateClientAppointmentWorksheet

	public static String
	generateQWCString(UKOnlineCompanyBean _practice)
		throws TorqueException, ObjectAlreadyExistsException, IllegalValueException, WritingException, IOException, Exception
	{
		QuickBooksSettings qb_settings = _practice.getQuickBooksSettings();

		String owner_id = qb_settings.getOwnerIDString();
		String file_id = qb_settings.getFileIDString();

		if (owner_id.equals("") || file_id.equals(""))
		{
			if (owner_id.equals(""))
			{
				UUID owner_id_generated_uuid = UUID.randomUUID();
				qb_settings.setOwnerID(owner_id_generated_uuid.toString());
			}

			if (file_id.equals(""))
			{
				UUID file_id_generated_uuid = UUID.randomUUID();
				qb_settings.setFileID(file_id_generated_uuid.toString());
			}

			qb_settings.save();
		}

		writer = new java.io.StringWriter();
		xmlwriter = new XmlWriter(writer);


		/*
<?xml version="1.0"?>
<QBWCXML>
	<AppName>TestWebService01</AppName>
	<AppID>Dexter</AppID>
	<AppURL>http://localhost:8080/valeo/QBWebConnectorSvc?wsdl</AppURL>
	<AppDescription>Happy Thoughts</AppDescription>
	<AppSupport>http://developer.intuit.com</AppSupport>
	<UserName>marlo</UserName>
	<OwnerID>{5f40af70-9eef-11dd-ad8b-0800200c9a66}</OwnerID>
	<FileID>{694784d0-9eef-11dd-ad8b-0800200c9a66}</FileID>
	<QBType>QBPOS</QBType>
	<Scheduler>
		<RunEveryNMinutes>2</RunEveryNMinutes>
	</Scheduler>
</QBWCXML>
		 */

		xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("QBWCXML"); xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("AppName"); xmlwriter.writeText("ValonyxWebService"); xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("AppID"); xmlwriter.writeText("Valonyx"); xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("AppURL"); xmlwriter.writeText("https://www.valonyx.com/QBWebConnectorSvc?wsdl"); xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("AppDescription"); xmlwriter.writeText("Multi-Practitioner Scheduling System"); xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("AppSupport"); xmlwriter.writeText("http://www.valonyx.com"); xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("UserName"); xmlwriter.writeText(qb_settings.getUserNameString()); xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("OwnerID"); xmlwriter.writeText("{" + owner_id + "}"); xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("FileID"); xmlwriter.writeText("{" + file_id + "}"); xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("QBType"); xmlwriter.writeText("QBFS"); xmlwriter.endEntity();
		xmlwriter.writeEntity("Scheduler"); xmlwriter.writeText("\n\r");
		xmlwriter.writeEntity("RunEveryNMinutes"); xmlwriter.writeText("1"); xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.endEntity(); xmlwriter.writeText("\n\r");
		xmlwriter.close();
		
		return "<?xml version=\"1.0\"?>" + writer.toString();

	}
	


} // QBQWCFileGenerator



