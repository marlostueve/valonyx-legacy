package com.badiyan.uk.online.PDF;


import java.io.*;
import java.util.*;

import com.badiyan.uk.online.beans.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
//import com.lowagie.text.pdf.codec.TiffImage;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;

import java.awt.Color;

import java.math.*;
import com.badiyan.uk.exceptions.*;

public class
HairAnalysisRatioWorksheetBuilder
{
	private static float abs_pos_x = 40f;
	private static float abs_pos_y = 20f;

	private static float jobber = 12.5f;
	private static float grrt = .4f;
	private static float v_jobber = 10.2f;

	private static float jobber_8 = 10.428f;

	private static float teacher_name_pos_x = abs_pos_x + 24f;
	private static float teacher_name_pos_y = abs_pos_y + 334f;

	private static float class_name_pos_x = abs_pos_x + 24f;
	private static float class_name_pos_y = abs_pos_y + 510f;

	private static float first_pos_x = abs_pos_x + 86.5f;
	private static float first_pos_y = abs_pos_y + 87f;

	private static float first_pos_y_8 = abs_pos_y + 85.5f;

	private static float student_name_y = abs_pos_y + 14f;

	private static float state_standard_x = abs_pos_x + 57.75f;


	/**
	 * Manages the creation of the printed plan's individual pages.
	 * @param args
	 */
	public static String
	generateHairAnalysisRatioWorksheet(MineralRatioBean _ratio_obj)
		throws IllegalValueException
	{
		BigDecimal one_hundred = new BigDecimal(100);
		BigDecimal one = new BigDecimal(1);

		Vector pre_totals = new Vector();
		Vector post_totals = new Vector();
		Vector alt_totals = new Vector();
		Vector pre_correct = new Vector();
		Vector post_correct = new Vector();
		Vector alt_correct = new Vector();
		for (int i = 0; i < 77; i++)
		{
			BigDecimal a1 = new BigDecimal(0);
			BigDecimal a2 = new BigDecimal(0);
			BigDecimal a3 = new BigDecimal(0);
			BigDecimal a4 = new BigDecimal(0);
			BigDecimal a5 = new BigDecimal(0);
			BigDecimal a6 = new BigDecimal(0);
			pre_totals.addElement(a1);
			post_totals.addElement(a2);
			alt_totals.addElement(a3);
			pre_correct.addElement(a4);
			post_correct.addElement(a5);
			alt_correct.addElement(a6);
		}

		// Hair-analysis-ratio-worksheet-new-blank.pdf

		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		int grade = 0;

		boolean show_alt_rows = false;
		int max_students = 21;
		int rows_per_student = 2;






		//resourcesFolder =

		//String tiff_file = resourcesFolder + "g" + grade + " class record sheet.tif";
		String tiff_file = resourcesFolder + "g" + grade + " class record 2.tif";
		if (show_alt_rows)
			tiff_file = resourcesFolder + "g" + grade + " class record 3.tif";

		//String pdf_file = "C:\\odd.pdf";
		String file = "hair-analysis-worksheet-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		String pdf_file_source = resourcesFolder + "pdf/Hair-analysis-ratio-worksheet-new-blank.pdf";


		//Document document = new Document();
		try
		{
		    //PdfReader reader = new PdfReader(pdf_file_source);
		    //PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(pdf_file));

		    // we create a reader for a certain document
		    PdfReader reader = new PdfReader(pdf_file_source);
		    int n = reader.getNumberOfPages();
		    // we create a stamper that will copy the document to a new file
		    PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(pdf_file));
		    // adding some metadata
		    HashMap moreInfo = new HashMap();
		    moreInfo.put("Author", "Valeo");
		    stamp.setMoreInfo(moreInfo);
		    // adding content to each page
		    int i = 0;
		    PdfContentByte under;
		    PdfContentByte over;
		    //Image img = Image.getInstance("watermark.jpg");
		    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		    //img.setAbsolutePosition(200, 400);
		    while (i < n) {
			i++;
			// watermark under the existing page
			under = stamp.getUnderContent(i);
			//under.addImage(img);
			// text over the existing page
			over = stamp.getOverContent(i);

			over.beginText();
			over.setFontAndSize(bf, 18);
			over.setTextMatrix(30, 30);

			//over.showText("page " + i);
			over.setFontAndSize(bf, 16);
			over.showTextAligned(Element.ALIGN_LEFT, "", 200, 200, 0);
			over.endText();

			/*
			over.beginText();
			over.setFontAndSize(bf, 18);
			over.setTextMatrix(30, 30);

			//over.showText("page " + i);
			over.setFontAndSize(bf, 16);
			over.showTextAligned(Element.ALIGN_LEFT, "DUPLICATE", 200, 430, 0);

			over.showTextAligned(Element.ALIGN_LEFT, "DUPLICATE2", 230, 630, 0);

			over.endText();
			 */

			/*
			over.beginText();
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, "thurp", 24.5f, teacher_name_pos_y, 0f);
			over.endText();
			 */

			over.beginText();
			over.setFontAndSize(bf, 12);
			over.setColorFill(new Color(0x00, 0x00, 0xFF));
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getPerson().getLabel(), 87, 562, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 12);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getAnalysisDateString(), 292, 562, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 12);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getOxidationType(), 452, 562, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 12);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getCaString(), 594, 562, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 12);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getKString(), 700, 562, 0f);
			over.endText();


			over.beginText();
			over.setFontAndSize(bf, 16);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getCaMgString(), 35, 482, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 16);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getCaKString(), 35, 400, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 16);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getNaMgString(), 35, 340, 0f);
			over.endText();


			over.beginText();
			over.setFontAndSize(bf, 16);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getNaKString(), 35, 241, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 16);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getZnCuString(), 35, 130, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 16);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _ratio_obj.getCaPString(), 35, 73, 0f);
			over.endText();



			boolean hidden_cu = _ratio_obj.hiddenCopper();
			String hidden_copper_str = _ratio_obj.getHiddenCopperString();


			if (_ratio_obj.getCaMg() != null)
			{
			    BigDecimal ca_mg = _ratio_obj.getCaMg();

			    if (ca_mg.floatValue() == 6.67f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Ideal Ratio", 117, 539, 0f);
				over.endText();
			    }
			    else if (ca_mg.floatValue() < 2.5f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "< 2.5 = Extreme Low: Magnesium loss, may also have blood sugar issues, hidden Na/K inversion", 117, 539, 0f);
				over.endText();
			    }
			    else if (ca_mg.floatValue() < 3.3f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "2.5 � 3.3 = Moderate Low: Magnesium loss, may also have blood sugar issues, hidden Na/K inversion", 117, 539, 0f);
				over.endText();
			    }
			    else if (ca_mg.floatValue() <= 10.0f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "3.3 - 10 = Good Range", 117, 539, 0f);
				over.endText();
			    }
			    else if (ca_mg.floatValue() <= 13.0f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "10 - 13 = Overeating carbs, tendency toward Insulin Resistance", 117, 539, 0f);
				over.endText();
			    }
			    else if (ca_mg.floatValue() <= 18.0f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "13-18 = Moderate High: May be overeating carbs, emotional defensiveness/conflict, defending a lifestyle imbalance that is not in their best interest, cognitive dissidence", 117, 539, 0f);
				over.endText();
			    }
			    else
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "> 18 = Extreme High: May be overeating carbs, emotional defensiveness/conflict, defending a lifestyle imbalance that is not in their best interest, cognitive dissidence", 117, 539, 0f);
				over.endText();

				/*
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "You're really messed up too!", 117, 528, 0f);
				over.endText();
				 */
			    }

			    if (_ratio_obj.getCa() != null)
			    {
				BigDecimal ca = _ratio_obj.getCa();

				if (ca.floatValue() > 250.0f)
				{
				    over.beginText();
				    over.setFontAndSize(bf, 8);
				    over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Ca >250 = Extreme High: Protection, defended, defensive, lowered cell permeability, calcium shell", 117, 528, 0f);
				    over.endText();
				}
				else if (ca.floatValue() > 200.0f)
				{
				    over.beginText();
				    over.setFontAndSize(bf, 8);
				    over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Ca >200 = Moderate High: Protection, defended, defensive, lowered cell permeability, calcium shell", 117, 528, 0f);
				    over.endText();
				}
				else if (ca.floatValue() > 150.0f)
				{
				    over.beginText();
				    over.setFontAndSize(bf, 8);
				    over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Ca >150 = Mild High: Protection, defended, defensive, lowered cell permeability, calcium shell", 117, 528, 0f);
				    over.endText();
				}
			    }

			}

			if (_ratio_obj.getCaK() != null)
			{
			    BigDecimal ca_k = _ratio_obj.getCaK();

			    if (ca_k.floatValue() == 4.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Ideal Ratio", 117, 460, 0f);
				over.endText();
			    }
			    else if (ca_k.floatValue() < 1.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "< 2.5 = Extreme Low: Magnesium loss, may also have blood sugar issues, hidden Na/K inversion", 117, 460, 0f);
				over.endText();
			    }
			    else if (ca_k.floatValue() < 3.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "1 - 3 = Moderate Low: Magnesium loss, may also have blood sugar issues, hidden Na/K inversion", 117, 460, 0f);
				over.endText();
			    }
			    else if (ca_k.floatValue() < 8.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "3 - 8 = Good Range", 117, 460, 0f);
				over.endText();
			    }
			    else if (ca_k.floatValue() < 50.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "8 - 50 = Moderate High: Decreased thyroid effect (at the cellular level)", 117, 460, 0f);
				over.endText();
			    }
			    else
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "> 50 = Extreme High: Decreased thyroid effect (at the cellular level)", 117, 460, 0f);
				over.endText();
			    }

			    if (_ratio_obj.getK() != null)
			    {
				BigDecimal k = _ratio_obj.getK();

				if (k.floatValue() < 4.0f)
				{
				    over.beginText();
				    over.setFontAndSize(bf, 8);
				    over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Low K (<4) = Body exhausted but mind keeps pushing \"running on fumes\"" + (((_ratio_obj.getCa() != null) && (_ratio_obj.getCa().floatValue() > 50.0f)) ? ", and Cu toxic" : "") + (((_ratio_obj.getCa() != null) && (_ratio_obj.getCa().floatValue() < 40.0f)) ? " - Low Ca = hypersensitivity, hyperkinetic, anxiety, nervousness, muscle cramps, increased cell..." : ""), 117, 449, 0f);
				    over.endText();
				}
				else if ((_ratio_obj.getCa() != null) && (_ratio_obj.getCa().floatValue() < 40.0f))
				{
				    over.beginText();
				    over.setFontAndSize(bf, 8);
				    over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Low Ca = hypersensitivity, hyperkinetic, anxiety, nervousness, muscle cramps, increased cell permeability, unprotected psychologically, tendency to Ca deficiency, lead toxicity...", 117, 449, 0f);
				    over.endText();
				}
			    }
			}


			if (_ratio_obj.getNaMg() != null)
			{
			    BigDecimal na_mg = _ratio_obj.getNaMg();

			    if (na_mg.floatValue() == 4.70f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Ideal Ratio", 117, 376, 0f);
				over.endText();
			    }
			    else if (na_mg.floatValue() < 1.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "< 1 = Extreme Low: Decreased adrenal effect, chronic stress, exhaustion reaction", 117, 376, 0f);
				over.endText();
			    }
			    else if (na_mg.floatValue() < 2.50f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "1 - 2.5 = Moderate Low: Decreased adrenal effect, chronic stress, exhaustion reaction", 117, 376, 0f);
				over.endText();
			    }
			    else if (na_mg.floatValue() < 3.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "2.5 - 3 = Slightly Low", 117, 376, 0f);
				over.endText();
			    }
			    else if (na_mg.floatValue() < 6.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "3 - 6 = Good Range", 117, 376, 0f);
				over.endText();
			    }
			    else if (na_mg.floatValue() <= 20.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "6 - 20 = Moderate High: Excessive adrenal effect, alarm reaction, acute stress, and/or toxins (which can push Na up), tendency for Mg deficiency", 117, 376, 0f);
				over.endText();
			    }
			    else
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "> 20 = Extreme High: Excessive adrenal effect, alarm reaction, acute stress, and/or toxins (which can push Na up), tendency for Mg deficiency", 117, 376, 0f);
				over.endText();
			    }

			}

			if (_ratio_obj.getNaK() != null)
			{
			    BigDecimal na_k = _ratio_obj.getNaK();

			    if (na_k.floatValue() == 2.50f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Ideal Ratio", 117, 317, 0f);
				over.endText();
			    }
			    else if (na_k.floatValue() < 1.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "< 1 = Extreme Low: (inversion) = decreased adrenal effect (exhaustion), chronic stress, lowered energy & energy reserves, decreased immunity, protein catabolism, poor digestion...", 117, 317, 0f);
				over.endText();
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "(In addition to above possibilities) delusional, false ideas, out of touch, decreased awareness of signs & symptoms, feels like you are �beating your head against the wall,� pos...", 117, 306, 0f);
				over.endText();
			    }
			    else if (na_k.floatValue() < 2.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "1 - 2 = Severe Low: (inversion) = decreased adrenal effect (exhaustion), chronic stress, lowered energy & energy reserves, decreased immunity, protein catabolism, poor digestion,", 117, 317, 0f);
				over.endText();
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "allergic tendencies, carbohydrate intolerance, diabetic tendency, liver & kidney stress, cardiovascular stress, tendency toward degenerative disease, frustration, resentment...", 117, 306, 0f);
				over.endText();
			    }
			    else if (na_k.floatValue() < 2.30f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "2 - 2.3 = Moderate Low: (inversion) = decreased adrenal effect (exhaustion), chronic stress, lowered energy & energy reserves, decreased immunity, protein catabolism, poor digestion,", 117, 317, 0f);
				over.endText();
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "allergic tendencies, carbohydrate intolerance, diabetic tendency, liver & kidney stress, cardiovascular stress, tendency toward degenerative disease, frustration, resentment...", 117, 306, 0f);
				over.endText();
			    }
			    else if (na_k.floatValue() < 5.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "2.3 - 5 = Good Range", 117, 317, 0f);
				over.endText();
			    }
			    else if (na_k.floatValue() <= 12.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "5 - 12 = Moderate High: Alarm reaction, acute stress, inflammation, anger, toxins (which can push Na up)", 117, 317, 0f);
				over.endText();
			    }
			    else
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "> 12 = Extreme High: Alarm reaction, acute stress, inflammation, anger, toxins (which can push Na up)", 117, 317, 0f);
				over.endText();
			    }
			}

			if (_ratio_obj.getZnCu() != null)
			{
			    BigDecimal zn_cu = _ratio_obj.getZnCu();

			    if (zn_cu.floatValue() == 8.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Ideal Ratio", 117, 211, 0f);
				over.endText();
			    }
			    else if (zn_cu.floatValue() < 3.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "< 3 = Extreme Low: Cu toxicity, Estrogen Dominance, female or male hormone imbalance, emotional problems, PMS, volatile, depressed, detached, cardiovascular stress...", 117, 211, 0f);
				over.endText();
			    }
			    else if (zn_cu.floatValue() < 6.50f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "3 - 6.5 = Moderate Low: Cu toxicity, Estrogen Dominance, female or male hormone imbalance, emotional problems, PMS, volatile, depressed, detached, cardiovascular stress...", 117, 211, 0f);
				over.endText();
			    }
			    else if (zn_cu.floatValue() <= 10.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "6.5 - 10 = Good Range: (Zn roughly correlates with progesterone effect in women, testosterone effect in men   Cu roughly correlates with estrogen effect in both sexes.)", 117, 211, 0f);
				over.endText();
			    }
			    else if (zn_cu.floatValue() <= 15.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "10 - 15 = Moderate High: " + (hidden_cu ? "***CAUTION " : "") + "(Female or male hormone imbalance, cardiovascular stress, tendency toward atherosclerosis, Zn loss, look for hidden Cu.* (see below))", 117, 211, 0f);
				over.endText();

				if (hidden_cu)
				{
				    over.beginText();
				    over.setFontAndSize(bf, 8);
				    over.showTextAligned(PdfContentByte.ALIGN_LEFT, "*Hidden Cu Toxicity found" + (_ratio_obj.getOxidationType().equals("FAST") ? " (Fast oxidizers usually have a low Cu & Zn) " : "") + ": " + hidden_copper_str + " " + (_ratio_obj.getOxidationType().equals("FAST") ? "- (Note: with hidden Cu, the symptoms of a low Zn/Cu ratio will be present)" : ""), 117, 200, 0f);
				    over.endText();
				}
			    }
			    else if (zn_cu.floatValue() > 15.00f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "> 15 = Extreme High: " + (hidden_cu ? "***CAUTION " : "") + "(Female or male hormone imbalance, cardiovascular stress, tendency toward atherosclerosis, Zn loss, look for hidden Cu.* (see below))", 117, 211, 0f);
				over.endText();

				if (hidden_cu)
				{
				    over.beginText();
				    over.setFontAndSize(bf, 8);
				    over.showTextAligned(PdfContentByte.ALIGN_LEFT, "*Hidden Cu Toxicity found" + (_ratio_obj.getOxidationType().equals("FAST") ? " (Fast oxidizers usually have a low Cu & Zn) " : "") + ": " + hidden_copper_str + " " + (_ratio_obj.getOxidationType().equals("FAST") ? "- (Note: with hidden Cu, the symptoms of a low Zn/Cu ratio will be present)" : ""), 117, 200, 0f);
				    over.endText();
				}
			    }

			    /*
			    if (hidden_cu && _ratio_obj.getOxidationType().equals("SLOW"))
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Hidden Cu Toxicity found (SLOW oxidizer) :" + hidden_copper_str, 117, 199, 0f);
				over.endText();
			    }
			     */
			}

			if (_ratio_obj.getCaP() != null)
			{
			    BigDecimal ca_p = _ratio_obj.getCaP();

			    if (ca_p.floatValue() == 2.50f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "Ideal Ratio", 117, 110, 0f);
				over.endText();
			    }
			    else if (ca_p.floatValue() < 1.5f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "< 1.5 = Extreme Low: sympathetic state", 117, 110, 0f);
				over.endText();
			    }
			    else if (ca_p.floatValue() < 2.3f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "1.5 - 2.3 = Moderate Low: sympathetic state", 117, 110, 0f);
				over.endText();
			    }
			    else if (ca_p.floatValue() <= 2.7f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "2.3 - 2.7 = Good Range", 117, 110, 0f);
				over.endText();
			    }
			    else if (ca_p.floatValue() <= 2.7f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "2.3 - 2.7 = Good Range", 117, 110, 0f);
				over.endText();
			    }
			    else if (ca_p.floatValue() <= 8.0f)
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "2.7 - 8 = Moderate High: parasympathetic state", 117, 110, 0f);
				over.endText();
			    }
			    else
			    {
				over.beginText();
				over.setFontAndSize(bf, 8);
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, "> 8 = Extreme High: parasympathetic state", 117, 110, 0f);
				over.endText();
			    }
			}



		    }
		    // adding an extra page
		    //stamp.insertPage(1, PageSize.A4);
		    over = stamp.getOverContent(1);

		    /*
		    over.beginText();
		    over.setFontAndSize(bf, 18);
		    over.showTextAligned(Element.ALIGN_LEFT, "DUPLICATE OF AN EXISTING PDF DOCUMENT", 30, 600, 0);
		    over.endText();
		     */

		    // adding a page from another document
		    //PdfReader reader2 = new PdfReader("SimpleAnnotations1.pdf");
		    //under = stamp.getUnderContent(1);
		    //under.addTemplate(stamp.getImportedPage(reader2, 3), 1, 0, 0, 1, 0, 0);
		    // closing PdfStamper will generate the new PDF file
		    stamp.close();


		    /*
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf_file));
			int pages = 0;
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			RandomAccessFileOrArray ra = null;
			int comps = 0;
		     */



			/*
			try
			{
				ra = new RandomAccessFileOrArray(tiff_file);
				comps = TiffImage.getNumberOfPages(ra);
			}
			catch (Throwable e)
			{
				System.out.println("Exception in " + tiff_file + " " + e.getMessage());
			}

			System.out.println("Processing: " + tiff_file);
			 */


		    /*
			try
			{
			    BaseFont arial = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			    cb.setFontAndSize(arial, 10);


			    cb.beginText();
			    cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "thurp", 24.5f, teacher_name_pos_y, 90f);
			    cb.endText();
			}
			catch (Throwable e)
			{
				System.out.println("Error >" + e.getMessage());
				e.printStackTrace();
			}
		     */



		    System.out.println("thurpXX");




			/*
			for (int cx = 0; cx < comps; ++cx)
			{
				try
				{
					Image img = TiffImage.getTiffImage(ra, cx + 1);

					if (img != null)
					{
						//img.scaleToFit(800f, 1000f);
						img.scaleToFit(800f, 1000f);
						img.setRotationDegrees(90f);

						float offset = -10.5f;
						if (grade == 3)
							offset = -4f;
						else if (grade == 8)
							offset = -13f;

						img.setAbsolutePosition(offset, abs_pos_y);

						cb.addImage(img);

						BaseFont arial = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
						cb.setFontAndSize(arial, 10);

						// write teacher name
						cb.beginText();
						//cb.moveText(teacher_name_pos_x, teacher_name_pos_y);
						if (grade == 3)
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _class.getTeacher().getLabel(), 24.5f, teacher_name_pos_y, 90f);
						else
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _class.getTeacher().getLabel(), 18.0f, teacher_name_pos_y, 90f);
						cb.endText();

						cb.beginText();
						//cb.moveText(class_name_pos_x, class_name_pos_y);
						if (grade == 3)
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _class.getLabel(), 24.5f, class_name_pos_y, 90f);
						else
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _class.getLabel(), 18.0f, class_name_pos_y, 90f);
						cb.endText();

						Vector students = _class.getStudents();

						HashMap student_correct_pre_hash = new HashMap();
						HashMap student_correct_post_hash = new HashMap();
						HashMap student_correct_alt_hash = new HashMap();
						AssessmentBean pre_test = null;
						AssessmentBean post_test = null;
						AssessmentBean alt_test = null;
						int num_stems = 0;
						try
						{
							pre_test = _class.getPreTest();
						}
						catch (ObjectNotFoundException x)
						{
						}
						try
						{
							post_test = _class.getPostTest();
						}
						catch (ObjectNotFoundException x)
						{
						}
						try
						{
							alt_test = _class.getAltTest();
						}
						catch (ObjectNotFoundException x)
						{
						}
						HashMap pre_test_hash = new HashMap();
						HashMap post_test_hash = new HashMap();
						HashMap alt_test_hash = new HashMap();
						if (pre_test != null)
						{
							pre_test_hash = AssessmentProgressBean.getInteractionsStudentHashByObjective(_class, pre_test);
							student_correct_pre_hash = AssessmentProgressBean.getInteractionsStudentHashCorrectOnly(_class, pre_test);
							num_stems = pre_test.getStems().size();
						}
						if (post_test != null)
						{
							post_test_hash = AssessmentProgressBean.getInteractionsStudentHashByObjective(_class, post_test);
							student_correct_post_hash = AssessmentProgressBean.getInteractionsStudentHashCorrectOnly(_class, post_test);
						}
						if (alt_test != null)
						{
							alt_test_hash = AssessmentProgressBean.getInteractionsStudentHashByObjective(_class, alt_test);
							student_correct_alt_hash = AssessmentProgressBean.getInteractionsStudentHashCorrectOnly(_class, alt_test);
						}

						cb.setFontAndSize(arial, 7);

						SchoolDistrictBean school_district_obj = SchoolDistrictBean.getSchoolDistrict(_teacher);
						String state = school_district_obj.getStateString();

						//Vector display_numbers = new Vector();


						//System.out.println("GETTING STANDARDS...");

						for (int i = 1; i < 62; i++)
						{
							try
							{
								ObjectiveBean objective = ObjectiveBean.getObjective(i, _class);
								//Integer display_number = new Integer(i);
								//display_numbers.addElement(display_number);
								StateStandardBean standard = StateStandardBean.getStateStandard(objective, state, _class);

								cb.beginText();
								if (grade == 3)
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, standard.getLabel(), 104.65f, getY(_class, i - 1) + 5.5f, 180f);
								else
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, standard.getLabel(), 98.15f, getY(_class, i - 1) + 5.5f, 180f);
								cb.endText();
							}
							catch (Exception x)
							{
								//System.out.println("ERROR >" + x.getMessage());
								//x.printStackTrace();
							}
						}

						int num_students_on_current_page = 0;
						Iterator students_itr = students.iterator();
						//for (int i = 0; students_itr.hasNext() && i < max_students; i++)

						for (int i = 0; students_itr.hasNext(); i++)
						{
							if (num_students_on_current_page == max_students)
							{
								//doNewPage(document, grade, show_alt_rows, resourcesFolder, cb, img, _class, _teacher);
								num_students_on_current_page = 0;
							}

							MTPPersonBean student = (MTPPersonBean)students_itr.next();

							int total = 0;
							int num_pre_correct = 0;
							HashMap pre_hash = (HashMap)pre_test_hash.get(student);
							int column = 0;

							if (pre_hash != null)
							{
								cb.setFontAndSize(arial, 8);

								cb.beginText();
								String student_name = student.getFirstNameString() + " " + student.getLastInitial();
								if (student_name.length() > 10)
									student_name = student_name.substring(0, 11);
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, student_name, getX(_class, num_students_on_current_page * rows_per_student), student_name_y, 90f);
								cb.endText();

								cb.beginText();
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, AssessmentProgressBean.PRE_TEST_SHORT_NAME, getX(_class, num_students_on_current_page * rows_per_student), getY(_class, - 2), 90f);
								cb.endText();

								cb.setFontAndSize(arial, 10);
								if (grade == 8)
									cb.setFontAndSize(arial, 8);



								for (int x = 0; x < 70; x++)
								{
									Integer disp_num = new Integer(x);
									AssessmentInteraction obj = (AssessmentInteraction)pre_hash.get(disp_num);
									boolean correct = false;
									if (obj != null)
									{
										correct = (obj.getIsCorrect() == (short)1);
										if (!correct)
										{
											cb.beginText();
											cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", getX(_class, num_students_on_current_page * rows_per_student), getY(_class, column - 1), 90f);
											cb.endText();
										}
										else
										{
											num_pre_correct++;
											BigDecimal bd = (BigDecimal)pre_correct.elementAt(x);
											pre_correct.setElementAt(bd.add(one), x);
										}

										total++;
										BigDecimal bd = (BigDecimal)pre_totals.elementAt(x);
										pre_totals.setElementAt(bd.add(one), x);
									}
									column++;
								}

								cb.setFontAndSize(arial, 8);
								cb.beginText();
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, num_pre_correct + "", getX(_class, num_students_on_current_page * rows_per_student), 742f, 90f);
								cb.endText();

								BigDecimal t = new BigDecimal(total);
								BigDecimal c = new BigDecimal(num_pre_correct);
								BigDecimal temp = new BigDecimal(0);
								if (total > 0)
									temp = c.multiply(one_hundred).divide(t, 0, BigDecimal.ROUND_HALF_UP);

								cb.setFontAndSize(arial, 8);
								cb.beginText();
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString() + "%", getX(_class, num_students_on_current_page * rows_per_student), 772f, 90f);
								cb.endText();

								cb.setFontAndSize(arial, 8);
								cb.beginText();
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, student.getDaysAbsent() + "", getX(_class, num_students_on_current_page * rows_per_student), 797f, 90f);
								cb.endText();

							}
							else
							{
								cb.setFontAndSize(arial, 8);

								cb.beginText();
								String student_name = student.getFirstNameString() + " " + student.getLastInitial();
								if (student_name.length() > 10)
									student_name = student_name.substring(0, 11);
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, student_name, getX(_class, num_students_on_current_page * rows_per_student), student_name_y, 90f);
								cb.endText();

								cb.beginText();
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, AssessmentProgressBean.PRE_TEST_SHORT_NAME, getX(_class, num_students_on_current_page * rows_per_student), getY(_class, - 2), 90f);
								cb.endText();

								cb.setFontAndSize(arial, 10);
								if (grade == 8)
									cb.setFontAndSize(arial, 8);

								cb.beginText();
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Did not take test.", getX(_class, num_students_on_current_page * rows_per_student), getY(_class, 0), 90f);
								cb.endText();
							}

							cb.beginText();
							cb.setFontAndSize(arial, 8);
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, AssessmentProgressBean.POST_TEST_SHORT_NAME, getX(_class, (num_students_on_current_page * rows_per_student) + 1), getY(_class, - 2), 90f);
							cb.endText();

							cb.setFontAndSize(arial, 10);
							if (grade == 8)
								cb.setFontAndSize(arial, 8);

							int num_post_correct = 0;
							HashMap post_hash = (HashMap)post_test_hash.get(student);
							column = 0;
							if (post_hash != null)
							{
								for (int x = 0; x < 70; x++)
								{
									Integer disp_num = new Integer(x);
									AssessmentInteraction obj = (AssessmentInteraction)post_hash.get(disp_num);
									boolean correct = false;
									if (obj != null)
									{
										correct = (obj.getIsCorrect() == (short)1);
										if (!correct)
										{
											cb.beginText();
											cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", getX(_class, (num_students_on_current_page * rows_per_student) + 1), getY(_class, column - 1), 90f);
											cb.endText();
										}
										else
										{
											num_post_correct++;
											BigDecimal bd = (BigDecimal)post_correct.elementAt(x);
											post_correct.setElementAt(bd.add(one), x);
										}

										BigDecimal bd = (BigDecimal)post_totals.elementAt(x);
										post_totals.setElementAt(bd.add(one), x);
									}
									column++;
								}

								cb.setFontAndSize(arial, 8);
								cb.beginText();
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, num_post_correct + "", getX(_class, (num_students_on_current_page * rows_per_student) + 1), 742f, 90f);
								cb.endText();

								BigDecimal t = new BigDecimal(total);
								BigDecimal c = new BigDecimal(num_post_correct);
								BigDecimal temp = new BigDecimal(0);
								if (total > 0)
									temp = c.multiply(one_hundred).divide(t, 0, BigDecimal.ROUND_HALF_UP);

								cb.setFontAndSize(arial, 8);
								cb.beginText();
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString() + "%", getX(_class, (num_students_on_current_page * rows_per_student) + 1), 772f, 90f);
								cb.endText();
							}
							else
							{
								cb.setFontAndSize(arial, 10);
								if (grade == 8)
									cb.setFontAndSize(arial, 8);

								cb.beginText();
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Did not take test.", getX(_class, (num_students_on_current_page * rows_per_student) + 1), getY(_class, 0), 90f);
								cb.endText();
							}

							if (show_alt_rows)
							{
								cb.beginText();
								cb.setFontAndSize(arial, 8);
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, AssessmentProgressBean.ALT_TEST_SHORT_NAME, getX(_class, (num_students_on_current_page * rows_per_student) + 2), getY(_class, - 2), 90f);
								cb.endText();

								cb.setFontAndSize(arial, 10);
								if (grade == 8)
									cb.setFontAndSize(arial, 8);

								int num_alt_correct = 0;
								HashMap alt_hash = (HashMap)alt_test_hash.get(student);
								column = 0;
								//System.out.println("alt_hash >" + alt_hash);
								if (alt_hash != null)
								{
									for (int x = 0; x < 70; x++)
									{
										Integer disp_num = new Integer(x);
										AssessmentInteraction obj = (AssessmentInteraction)alt_hash.get(disp_num);
										boolean correct = false;
										//System.out.println("obj >" + obj);
										if (obj != null)
										{
											correct = (obj.getIsCorrect() == (short)1);
											//System.out.println("correct >" + correct);
											if (!correct)
											{
												cb.beginText();
												cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "X", getX(_class, (num_students_on_current_page * 3) + 2), getY(_class, column - 1), 90f);
												cb.endText();
											}
											else
											{
												num_alt_correct++;
												BigDecimal bd = (BigDecimal)alt_correct.elementAt(x);
												alt_correct.setElementAt(bd.add(one), x);
											}

											BigDecimal bd = (BigDecimal)alt_totals.elementAt(x);
											alt_totals.setElementAt(bd.add(one), x);
										}
										column++;
									}

									cb.setFontAndSize(arial, 8);
									cb.beginText();
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, num_alt_correct + "", getX(_class, (num_students_on_current_page * 3) + 2), 742f, 90f);
									cb.endText();

									BigDecimal t = new BigDecimal(total);
									BigDecimal c = new BigDecimal(num_alt_correct);
									BigDecimal temp = new BigDecimal(0);
									if (total > 0)
										temp = c.multiply(one_hundred).divide(t, 0, BigDecimal.ROUND_HALF_UP);

									cb.setFontAndSize(arial, 8);
									cb.beginText();
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString() + "%", getX(_class, (num_students_on_current_page * 3) + 2), 772f, 90f);
									cb.endText();
								}
								else
								{
									cb.setFontAndSize(arial, 10);
									if (grade == 8)
										cb.setFontAndSize(arial, 8);

									cb.beginText();
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Did not take test.", getX(_class, (num_students_on_current_page * 3) + 2), getY(_class, 0), 90f);
									cb.endText();
								}

							}

							//doNewPage(document, grade, show_alt_rows, resourcesFolder, cb, img, _class);

							//doNewPage(Document document, int grade, boolean show_alt_rows, String resourcesFolder, PdfContentByte cb, Image img)

							num_students_on_current_page++;
						}

						BigDecimal c_bd_pre = new BigDecimal(0);
						BigDecimal t_bd_pre = new BigDecimal(0);

						BigDecimal c_bd_post = new BigDecimal(0);
						BigDecimal t_bd_post = new BigDecimal(0);

						BigDecimal c_bd_alt = new BigDecimal(0);
						BigDecimal t_bd_alt = new BigDecimal(0);

						cb.setFontAndSize(arial, 8);

						for (int y = 0; y < 77; y++)
						{
							BigDecimal t = (BigDecimal)pre_totals.elementAt(y);
							BigDecimal c = (BigDecimal)pre_correct.elementAt(y);
							if (t.intValue() != 0)
							{
								BigDecimal temp = c.multiply(one_hundred).divide(t, 0, BigDecimal.ROUND_HALF_UP);
								float goat = 1f;
								if (temp.equals(one_hundred))
								{
									goat = 2.5f;
									cb.setFontAndSize(arial, 7);
									if (grade == 8)
										cb.setFontAndSize(arial, 6);
								}
								else
								{
									cb.setFontAndSize(arial, 8);
									if (grade == 8)
										cb.setFontAndSize(arial, 7);
								}

								cb.beginText();
								if (grade == 3)
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString(), getX(_class, 42), getY(_class, y - 1) - goat, 90f);
								else
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString(), getX(_class, 42), getY(_class, y - 1) - goat, 90f);
								cb.endText();

								c_bd_pre = c_bd_pre.add(c);
								t_bd_pre = t_bd_pre.add(t);
							}

							t = (BigDecimal)post_totals.elementAt(y);
							c = (BigDecimal)post_correct.elementAt(y);
							if (t.intValue() != 0)
							{
								BigDecimal temp = c.multiply(one_hundred).divide(t, 0, BigDecimal.ROUND_HALF_UP);
								float goat = 1f;
								if (temp.equals(one_hundred))
								{
									goat = 2.5f;
									cb.setFontAndSize(arial, 7);
									if (grade == 8)
										cb.setFontAndSize(arial, 6);
								}
								else
								{
									cb.setFontAndSize(arial, 8);
									if (grade == 8)
										cb.setFontAndSize(arial, 7);
								}

								cb.beginText();
								if (grade == 3)
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString(), getX(_class, 43), getY(_class, y - 1) - goat, 90f);
								else
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString(), getX(_class, 43), getY(_class, y - 1) - goat, 90f);
								cb.endText();

								c_bd_post = c_bd_post.add(c);
								t_bd_post = t_bd_post.add(t);
							}

							t = (BigDecimal)alt_totals.elementAt(y);
							c = (BigDecimal)alt_correct.elementAt(y);
							if (t.intValue() != 0)
							{
								BigDecimal temp = c.multiply(one_hundred).divide(t, 0, BigDecimal.ROUND_HALF_UP);
								float goat = 1f;
								if (temp.equals(one_hundred))
								{
									goat = 2.5f;
									cb.setFontAndSize(arial, 7);
									if (grade == 8)
										cb.setFontAndSize(arial, 6);
								}
								else
								{
									cb.setFontAndSize(arial, 8);
									if (grade == 8)
										cb.setFontAndSize(arial, 7);
								}

								cb.beginText();
								if (grade == 3)
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString(), getX(_class, 44), getY(_class, y - 1) - goat, 90f);
								else
									cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString(), getX(_class, 44), getY(_class, y - 1) - goat, 90f);

								cb.endText();

								c_bd_alt = c_bd_alt.add(c);
								t_bd_alt = t_bd_alt.add(t);
							}
						}

						BigDecimal temp = new BigDecimal(0);
						if (t_bd_pre.intValue() > 0)
							temp = c_bd_pre.multiply(one_hundred).divide(t_bd_pre, 0, BigDecimal.ROUND_HALF_UP);

						if (!temp.toString().equals("0"))
						{
							cb.setFontAndSize(arial, 8);
							cb.beginText();
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString(), getX(_class, 42), 742f, 90f);
							cb.endText();
						}

						temp = new BigDecimal(0);
						if (t_bd_post.intValue() > 0)
							temp = c_bd_post.multiply(one_hundred).divide(t_bd_post, 0, BigDecimal.ROUND_HALF_UP);

						if (!temp.toString().equals("0"))
						{
							cb.setFontAndSize(arial, 8);
							cb.beginText();
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString(), getX(_class, 43), 742f, 90f);
							cb.endText();
						}

						temp = new BigDecimal(0);
						if (t_bd_alt.intValue() > 0)
							temp = c_bd_alt.multiply(one_hundred).divide(t_bd_alt, 0, BigDecimal.ROUND_HALF_UP);

						if (!temp.toString().equals("0"))
						{
							cb.setFontAndSize(arial, 8);
							cb.beginText();
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, temp.toString(), getX(_class, 44), 742f, 90f);
							cb.endText();
						}

						cb.setFontAndSize(arial, 10);



						// write class name

						document.newPage();
						++pages;
					}
				}
				catch (Throwable e)
				{
					System.out.println("Exception " + tiff_file + " page " + (cx + 1) + " " + e.getMessage());
					e.printStackTrace();
				}
			}
			 */


			/*
			if (ra != null)
				ra.close();
			if (document != null)
				document.close();
			 */



		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		return file;

	} // generatePDF


	/*
	private void
	doTextStuff(String _text, int _x, int _y)
	{
	    over.beginText();
	    over.setFontAndSize(bf, 8);
	    over.showTextAligned(PdfContentByte.ALIGN_LEFT, "You're really messed up too!", 117, 528, 0f);
	    over.endText();
	}
	 */


	/*
	private static void
	doNewPage(Document document, int grade, boolean show_alt_rows, String resourcesFolder, PdfContentByte cb, Image img, MTPClassBean _class, MTPPersonBean _teacher)
		throws Exception
	{
		String tiff_file = resourcesFolder + "g" + grade + " class record 2.tif";
		if (show_alt_rows)
			tiff_file = resourcesFolder + "g" + grade + " class record 3.tif";

		//String pdf_file = "C:\\odd.pdf";
		String file = "class-record-sheet-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;



		float offset = -10.5f;
		if (grade == 3)
			offset = -4f;
		else if (grade == 8)
			offset = -13f;




		document.newPage();

		cb.addImage(img);

		BaseFont arial = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		cb.setFontAndSize(arial, 10);

		// write teacher name
		cb.beginText();
		//cb.moveText(teacher_name_pos_x, teacher_name_pos_y);
		if (grade == 3)
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _class.getTeacher().getLabel(), 24.5f, teacher_name_pos_y, 90f);
		else
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _class.getTeacher().getLabel(), 18.0f, teacher_name_pos_y, 90f);
		cb.endText();

		cb.beginText();
		//cb.moveText(class_name_pos_x, class_name_pos_y);
		if (grade == 3)
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _class.getLabel(), 24.5f, class_name_pos_y, 90f);
		else
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _class.getLabel(), 18.0f, class_name_pos_y, 90f);
		cb.endText();



		cb.setFontAndSize(arial, 7);

		SchoolDistrictBean school_district_obj = SchoolDistrictBean.getSchoolDistrict(_teacher);
		String state = school_district_obj.getStateString();

		//Vector display_numbers = new Vector();

		for (int i = 1; i < 62; i++)
		{
			try
			{
				ObjectiveBean objective = ObjectiveBean.getObjective(i, _class);
				//Integer display_number = new Integer(i);
				//display_numbers.addElement(display_number);
				StateStandardBean standard = StateStandardBean.getStateStandard(objective, state, _class);

				cb.beginText();
				if (grade == 3)
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, standard.getLabel(), 104.65f, getY(_class, i - 1) + 5.5f, 180f);
				else
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, standard.getLabel(), 98.15f, getY(_class, i - 1) + 5.5f, 180f);
				cb.endText();
			}
			catch (Exception x)
			{
				System.out.println("ERROR >" + x.getMessage());
			}
		}
	}
	 */

	/*
	private float[]
	getColumns(MTPClassBean _class)
	{
		return null;
	}
	 */

	/*
	private static float
	getX(int _row)
	{
		return first_pos_x + (_row * v_jobber);
	}
	 */

	/*
	private static float
	getX(MTPClassBean _class, int _row)
		throws IllegalValueException
	{
		float x = 0.0f;
		switch (_row)
		{
			case 0: x = 125.8f; break;
			case 1: x = 136.0f; break;
			case 2: x = 146.2f; break;
			case 3: x = 156.4f; break;
			case 4: x = 166.6f; break;
			case 5: x = 176.8f; break;
			case 6: x = 187.0f; break;
			case 7: x = 197.2f; break;
			case 8: x = 207.4f; break;
			case 9: x = 217.6f; break;
			case 10: x = 227.8f; break;
			case 11: x = 238.0f; break;
			case 12: x = 248.2f; break;
			case 13: x = 258.4f; break;
			case 14: x = 268.6f; break;
			case 15: x = 278.8f; break;
			case 16: x = 289.0f; break;
			case 17: x = 299.2f; break;
			case 18: x = 309.4f; break;
			case 19: x = 319.6f; break;
			case 20: x = 329.8f; break;
			case 21: x = 340.0f; break;
			case 22: x = 350.2f; break;
			case 23: x = 360.4f; break;
			case 24: x = 370.6f; break;
			case 25: x = 380.8f; break;
			case 26: x = 391.0f; break;
			case 27: x = 401.2f; break;
			case 28: x = 411.4f; break;
			case 29: x = 421.6f; break;
			case 30: x = 431.8f; break;
			case 31: x = 442.0f; break;
			case 32: x = 452.2f; break;
			case 33: x = 462.4f; break;
			case 34: x = 472.6f; break;
			case 35: x = 482.8f; break;
			case 36: x = 493.0f; break;
			case 37: x = 503.2f; break;
			case 38: x = 513.4f; break;
			case 39: x = 523.6f; break;
			case 40: x = 533.8f; break;
			case 41: x = 544.0f; break;

			case 42: x = 554.2f; break;
			case 43: x = 564.4f; break;
			case 44: x = 574.6f; break;
			case 45: x = 584.8f; break;
			case 46: x = 595.0f; break;
		}

		if (_class.getGrade() == 3)
		{
			//return (x - 3.0f);
			return (x + 6.5f);
		}

		return x;
	}

	private static float
	getY(MTPClassBean _class, int _column)
	{
		int wok = 0;

		if (_class.getBookCodeString().equals("4E") || _class.getBookCodeString().equals("3E"))
		{
			if (_column > 46)
				wok = 8;
			else if (_column > 39)
				wok = 7;
			else if (_column > 33)
				wok = 6;
			else if (_column > 28)
				wok = 5;
			else if (_column > 23)
				wok = 4;
			else if (_column > 18)
				wok = 3;
			else if (_column > 13)
				wok = 2;
			else if (_column > 8)
				wok = 1;
		}
		else if (_class.getBookCodeString().equals("5E"))
		{
			if (_column > 49)
				wok = 7;
			else if (_column > 42)
				wok = 6;
			else if (_column > 34)
				wok = 5;
			else if (_column > 29)
				wok = 4;
			else if (_column > 19)
				wok = 3;
			else if (_column > 9)
				wok = 2;
			else if (_column > 4)
				wok = 1;
		}
		else if (_class.getBookCodeString().equals("6E"))
		{
			if (_column > 46)
				wok = 8;
			else if (_column > 39)
				wok = 7;
			else if (_column > 33)
				wok = 6;
			else if (_column > 28)
				wok = 5;
			else if (_column > 23)
				wok = 4;
			else if (_column > 18)
				wok = 3;
			else if (_column > 13)
				wok = 2;
			else if (_column > 8)
				wok = 1;
		}
		else if (_class.getBookCodeString().equals("7E") || _class.getBookCodeString().equals("8E"))
		{
			if (_column > 46)
				wok = 8;
			else if (_column > 40)
				wok = 7;
			else if (_column > 32)
				wok = 6;
			else if (_column > 27)
				wok = 5;
			else if (_column > 23)
				wok = 4;
			else if (_column > 16)
				wok = 3;
			else if (_column > 9)
				wok = 2;
			else if (_column > 5)
				wok = 1;

			if (_class.getBookCodeString().equals("8E"))
				return first_pos_y_8 + (_column * jobber_8) + (wok * grrt);
		}

		return first_pos_y + (_column * jobber) + (wok * grrt);
	}
	 */

} // PDFBuilder



