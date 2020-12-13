/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.PDF;


import java.io.*;
import java.util.*;

import com.badiyan.uk.online.beans.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import com.badiyan.uk.beans.*;

import java.awt.Color;

import com.badiyan.uk.exceptions.*;

import com.valeo.qbpos.data.*;

public class
CheckoutSheetBuilderMultiple
{
	private static float abs_pos_x = 40f;
	private static float abs_pos_y = 20f;

	private static float appt_start_y = 670;
	private static float appt_spacing = 12;


	/**
	 * Manages the creation of the printed plan's individual pages.
	 * @param args
	 */
	public static String
	generatePractitionerAppointmentWorksheet(UKOnlineCompanyBean _company, UKOnlinePersonBean _practitioner, Date _start_date, Date _end_date)
		throws IllegalValueException
	{

		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		String file = "client-checkout-sheet-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		String pdf_file_source = resourcesFolder + "pdf/universal-check-out-sheet.pdf";

		try
		{

			

			// we create a reader for a certain document
            PdfReader reader = new PdfReader(pdf_file_source);
            // we retrieve the total number of pages
            int n = reader.getNumberOfPages();

			Vector appointments = null;
			if (_practitioner == null)
				appointments = AppointmentBean.getAppointments(_company, _start_date, _end_date, AppointmentBean.DEFAULT_APPOINTMENT_STATUS);
			else
				appointments = AppointmentBean.getAppointments(_practitioner, _start_date, _end_date, AppointmentBean.DEFAULT_APPOINTMENT_STATUS);

			Iterator appt_itr = appointments.iterator();

			n = appointments.size();

            // we retrieve the size of the first page
            Rectangle psize = reader.getPageSize(1);

            float width = psize.width();
            float height = psize.height();

            // step 1: creation of a document-object
            Document document = new Document(new Rectangle(width, height));
            // step 2: we create a writer that listens to the document
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf_file));
            // step 3: we open the document
            document.open();
            // step 4: we add content
            PdfContentByte cb = writer.getDirectContent();
            int i = 0;
            int p = 0;

			BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);


            while (appt_itr.hasNext()) {

				AppointmentBean appointment = (AppointmentBean)appt_itr.next();
				try
				{
					UKOnlinePersonBean appointment_person = appointment.getClient();

					document.newPage();
					p++;
					i++;
					PdfImportedPage page1 = writer.getImportedPage(reader, 1);
					cb.addTemplate(page1, 0, 0);


					cb.beginText();
					cb.setFontAndSize(bf, 12);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, appointment_person.getFileNumberString() + " - " + appointment_person.getLabel(), 105, 746, 0f);
					cb.endText();

					cb.beginText();
					cb.setFontAndSize(bf, 12);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, CUBean.getUserDateString(appointment.getAppointmentDate()), 121, 733, 0f);
					cb.endText();



					try
					{
						CustomerRet customer_ret_obj = CustomerRet.getCustomer(_company, appointment_person.getEmployeeNumberString());

						cb.beginText();
						cb.setFontAndSize(bf, 12);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, customer_ret_obj.getPriceLevelNumber() + "", 148, 720, 0f);
						cb.endText();
					}
					catch (ObjectNotFoundException x)
					{
						cb.beginText();
						cb.setFontAndSize(bf, 12);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "[NOT FOUND]", 148, 720, 0f);
						cb.endText();
					}


					cb.beginText();
					cb.setFontAndSize(bf, 12);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, appointment_person.getAddressesString(), 350, 746, 0f);
					cb.endText();

					cb.beginText();
					cb.setFontAndSize(bf, 12);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, appointment_person.getPhoneNumbersString(), 345, 733, 0f);
					cb.endText();

					cb.beginText();
					cb.setFontAndSize(bf, 12);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, appointment_person.getEmail(), 347, 720, 0f);
					cb.endText();

				}
				catch (ObjectNotFoundException x)
				{

				}
            }
            // step 5: we close the document
            document.close();


		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		return file;

	} // generateClientAppointmentWorksheet

} // ClientAppointmentBuilder



