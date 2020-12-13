<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.io.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.conformance.aicc.rte.server.*, com.badiyan.uk.exceptions.*, com.badiyan.torque.*, org.apache.torque.util.*, org.apache.poi.poifs.filesystem.*, org.apache.poi.hssf.usermodel.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.PDF.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<jsp:useBean id="courseReportLister" class="com.badiyan.uk.online.beans.UKOnlineCourseReportLister" scope="session"/>
<jsp:useBean id="saveFilename" class="java.lang.String" scope="session" />

<%

/*
int course_id = -1;
if ((request.getParameter("id") != null))
    course_id = Integer.parseInt(request.getParameter("id"));
if (request.getParameter("sort") != null)
    courseReportLister.setSort(Short.parseShort(request.getParameter("sort")));
courseReportLister.search();
List aList = courseReportLister.getListStuff();
 */


try
{
    if (courseReportLister.showExcel())
    {


		int total_enrollments = 0;

		short column_index = 0;
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + "client-statement.xls"));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);



		HSSFCellStyle cell_style = wb.createCellStyle();
		cell_style.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
		cell_style.setFillBackgroundColor( (short)0xc );
		cell_style.setFillForegroundColor( (short)0xc );
		HSSFFont header_font = wb.createFont();
		//set font 1 to 12 point type
		header_font.setFontHeightInPoints((short) 12);
		//make it white
		header_font.setColor( (short)0x1 );
		// make it bold
		//arial is the default font
		header_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cell_style.setFont(header_font);
		cell_style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cell_style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cell_style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cell_style.setBorderBottom(HSSFCellStyle.BORDER_THIN);


		HSSFCellStyle cell_style2 = wb.createCellStyle();
		cell_style2.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
		cell_style2.setFillBackgroundColor( (short)0xc );
		cell_style2.setFillForegroundColor( (short)0xc );
		header_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cell_style2.setFont(header_font);
		cell_style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cell_style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cell_style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cell_style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cell_style2.setAlignment(HSSFCellStyle.ALIGN_RIGHT);


		HSSFCellStyle cell_style7 = wb.createCellStyle();
		cell_style7.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
		//color_index = (new org.apache.poi.hssf.util.HSSFColor.LIGHT_GREEN()).getIndex();
		short color_index = (new org.apache.poi.hssf.util.HSSFColor.WHITE()).getIndex();
		cell_style7.setFillBackgroundColor( color_index );
		cell_style7.setFillForegroundColor( color_index );
		HSSFFont header_font7 = wb.createFont();
		header_font7.setFontHeightInPoints((short) 10);
		header_font7.setColor( (short)0x0 );
		// make it bold
		//arial is the default font
		//header_font7.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cell_style7.setFont(header_font7);
		cell_style7.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cell_style7.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cell_style7.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cell_style7.setBorderBottom(HSSFCellStyle.BORDER_THIN);


		HSSFCellStyle cell_style8 = wb.createCellStyle();
		cell_style8.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
		cell_style8.setFillBackgroundColor( color_index );
		cell_style8.setFillForegroundColor( color_index );
		cell_style8.setFont(header_font7);
		cell_style8.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cell_style8.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cell_style8.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cell_style8.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cell_style8.setAlignment(HSSFCellStyle.ALIGN_RIGHT);



		UKOnlinePersonBean client = courseReportLister.getPerson();
		Date start_date = courseReportLister.getStartDate();
		Date end_date = courseReportLister.getEndDate();

		Vector practice_areas = courseReportLister.getPracticeAreas();
		Vector types = courseReportLister.getCheckoutCodeTypes();
		
		// practice and address
		
		courseReportLister.addCell(sheet, 4, (short)0, adminCompany.getLabel());
		courseReportLister.addCell(sheet, 4, (short)4, "Practitioners at " + adminCompany.getLabel());

		AddressBean practice_address;
		try
		{
			practice_address = adminCompany.getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
			courseReportLister.addCell(sheet, 5, (short)0, practice_address.getStreet1String());
			courseReportLister.addCell(sheet, 6, (short)0, practice_address.getCityString() + ", " + practice_address.getStateString() + " " + practice_address.getZipCodeString());
			
		}
		catch (Exception x)
		{
			
		}
		
		
		if (adminCompany.getId() != 1) {
			courseReportLister.addCell(sheet, 7, (short)0, ""); // blot out the phone
			courseReportLister.addCell(sheet, 10, (short)5, adminCompany.getLabel() + " Tax ID #: "); // blot out the tax id
			courseReportLister.addCell(sheet, 13, (short)0, ""); // blot out the diagnosis codes
			courseReportLister.addCell(sheet, 14, (short)0, "");
			courseReportLister.addCell(sheet, 13, (short)2, "");
			courseReportLister.addCell(sheet, 14, (short)2, "");
			courseReportLister.addCell(sheet, 13, (short)3, "");
			courseReportLister.addCell(sheet, 14, (short)3, "");
			
			courseReportLister.addCell(sheet, 5, (short)4, ""); // blot out the practitioners
			courseReportLister.addCell(sheet, 6, (short)4, "");
			courseReportLister.addCell(sheet, 7, (short)4, "");
			
			
		
			// get the practitioners...


			try {
				Vector practitioners = UKOnlinePersonBean.getPractitioners(adminCompany);
				int num_practitioners = practitioners.size();
				if (num_practitioners > 0) {

					Iterator practitioner_itr = practitioners.iterator();
					for (int i = 0; practitioner_itr.hasNext(); i++) {
						UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
						String row_str = practitioner.getLabel();
						if (practitioner_itr.hasNext()) {
							practitioner = (UKOnlinePersonBean)practitioner_itr.next();
							row_str += "            " + practitioner.getLabel();
						}
						courseReportLister.addCell(sheet, 5 + i, (short)4, row_str);
					}
				}
			}
			catch (Exception x) {
				x.printStackTrace();
			}
		}

		courseReportLister.addCell(sheet, 9, (short)1, client.getLabel());
		courseReportLister.addCell(sheet, 9, (short)3, "File #: " + client.getFileNumberString());
		courseReportLister.addCell(sheet, 10, (short)1, courseReportLister.getStartDateString() + " - " + courseReportLister.getEndDateString());

		int row_index = 16;

		int row_858 = 0;
		BigDecimal amount_43 = BigDecimal.ZERO;
		BigDecimal amount_47 = BigDecimal.ZERO;

		int row_911 = 0;
		BigDecimal amount_44 = BigDecimal.ZERO;

		int row_857 = 0;
		BigDecimal amount_42 = BigDecimal.ZERO;
		BigDecimal amount_46 = BigDecimal.ZERO;

		int row_860 = 0;
		BigDecimal amount_41 = BigDecimal.ZERO;
		BigDecimal amount_45 = BigDecimal.ZERO;

		BigDecimal total_charges = BigDecimal.ZERO;
		BigDecimal total_payments = BigDecimal.ZERO;
		
		Vector total_tenders = new Vector();
		BigDecimal total_applied = BigDecimal.ZERO;

		Iterator orders = OrderBean.getOrders(client, start_date, end_date).iterator();
		while (orders.hasNext())
		{
			OrderBean orderObj = (OrderBean)orders.next();
			ValeoOrderBean order = (ValeoOrderBean)ValeoOrderBean.getOrder(orderObj.getId());
			
			Iterator order_lines = order.getOrders();
			while (order_lines.hasNext())
			{
				CheckoutOrderline order_line = (CheckoutOrderline)order_lines.next();
				CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(order_line.getCheckoutCodeId());
				int practitioner_id = order_line.getPractitionerId();
				String practitioner_label = "";
				UKOnlinePersonBean practitioner = null;
				if (practitioner_id > 0)
				{
					practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(practitioner_id);
					practitioner_label = practitioner.getLabel();
				}

				boolean show_row = true;
				if (!courseReportLister.showAllCheckoutCodeTypes())
				{
					boolean contains_type = false;
					Iterator types_itr = types.iterator();
					while (types_itr.hasNext())
					{
						Short type = (Short)types_itr.next();
						if (type.shortValue() == checkout_code.getType())
						{
							contains_type = true;
							break;
						}
					}
					show_row = contains_type;

					//show_row = types.contains(new Short(checkout_code.getType()));
				}

				System.out.println("checkout code type >" + checkout_code.getType());
				System.out.println("checkout_code >" + checkout_code.getItemNumber() + " - " + checkout_code.getLabel());

				// what is this crap???
				/*
				if (show_row &&
						(checkout_code.getItemNumber() != 41) &&
						(checkout_code.getItemNumber() != 42) &&
						(checkout_code.getItemNumber() != 43) &&
						(checkout_code.getItemNumber() != 44) &&
						(checkout_code.getItemNumber() != 45) &&
						(checkout_code.getItemNumber() != 46) &&
						(checkout_code.getItemNumber() != 47))
						*/
				if (show_row)
				{
					courseReportLister.addCell(sheet, row_index, (short)0, order.getOrderDateString(), cell_style7);
					courseReportLister.addCell(sheet, row_index, (short)1, "", cell_style7);
					courseReportLister.addCell(sheet, row_index, (short)2, "(" + order.getLabel() + " - " + order.getTotalString() + ") " + checkout_code.getLabel(), cell_style7);
					courseReportLister.addCell(sheet, row_index, (short)3, practitioner_label, cell_style7);
					courseReportLister.addCell(sheet, row_index, (short)4, "", cell_style7);

					if (order.getStatus().equals(OrderBean.RECEIVE_PAYMENT_ORDER_STATUS) || (order_line.getActualAmount().compareTo(BigDecimal.ZERO) == -1))
					{
						courseReportLister.addCell(sheet, row_index, (short)5, "", cell_style8);
						courseReportLister.addCell(sheet, row_index, (short)6, order_line.getActualAmount().abs().toString(), cell_style8);
						total_payments = total_payments.add(order_line.getActualAmount().abs());
					}
					else
					{
						courseReportLister.addCell(sheet, row_index, (short)5, order_line.getActualAmount().toString(), cell_style8);
						courseReportLister.addCell(sheet, row_index, (short)6, "", cell_style8);
						total_charges = total_charges.add(order_line.getActualAmount());
					}
					courseReportLister.addCell(sheet, row_index, (short)7, "", cell_style8);

					/*
					if (checkout_code.getItemNumber() == 858)
						row_858 = row_index;
					else if (checkout_code.getItemNumber() == 911)
						row_911 = row_index;
					else if (checkout_code.getItemNumber() == 857)
						row_857 = row_index;
					else if (checkout_code.getItemNumber() == 860)
						row_860 = row_index;
						*/

					row_index++;
				}

				/*
				if (checkout_code.getItemNumber() == 41)
					amount_41 = order_line.getActualAmount();
				else if (checkout_code.getItemNumber() == 42)
					amount_42 = order_line.getActualAmount();
				else if (checkout_code.getItemNumber() == 43)
					amount_43 = order_line.getActualAmount();
				else if (checkout_code.getItemNumber() == 44)
					amount_44 = order_line.getActualAmount();
				else if (checkout_code.getItemNumber() == 45)
					amount_45 = order_line.getActualAmount();
				else if (checkout_code.getItemNumber() == 46)
					amount_46 = order_line.getActualAmount();
				else if (checkout_code.getItemNumber() == 47)
					amount_47 = order_line.getActualAmount();

				if ((row_858 != 0) && (amount_43.compareTo(BigDecimal.ZERO) != 0) && (amount_47.compareTo(BigDecimal.ZERO) != 0))
				{
					courseReportLister.addCell(sheet, row_858, (short)5, amount_43.toString());
					total_charges = total_charges.add(amount_43);
					courseReportLister.addCell(sheet, row_858, (short)6, amount_47.abs().toString());
					total_payments = total_payments.add(amount_47.abs());
					row_858 = 0;
					amount_43 = BigDecimal.ZERO;
					amount_47 = BigDecimal.ZERO;
				}

				if ((row_911 != 0) && (amount_44.compareTo(BigDecimal.ZERO) != 0) && (amount_47.compareTo(BigDecimal.ZERO) != 0))
				{
					courseReportLister.addCell(sheet, row_911, (short)5, amount_44.toString());
					total_charges = total_charges.add(amount_44);
					courseReportLister.addCell(sheet, row_911, (short)6, amount_47.abs().toString());
					total_payments = total_payments.add(amount_47.abs());
					row_911 = 0;
					amount_44 = BigDecimal.ZERO;
					amount_47 = BigDecimal.ZERO;
				}

				if ((row_857 != 0) && (amount_42.compareTo(BigDecimal.ZERO) != 0) && (amount_46.compareTo(BigDecimal.ZERO) != 0))
				{
					courseReportLister.addCell(sheet, row_857, (short)5, amount_42.toString());
					total_charges = total_charges.add(amount_42);
					courseReportLister.addCell(sheet, row_857, (short)6, amount_46.abs().toString());
					total_payments = total_payments.add(amount_46.abs());
					row_857 = 0;
					amount_42 = BigDecimal.ZERO;
					amount_46 = BigDecimal.ZERO;
				}

				if ((row_860 != 0) && (amount_41.compareTo(BigDecimal.ZERO) != 0) && (amount_45.compareTo(BigDecimal.ZERO) != 0))
				{
					courseReportLister.addCell(sheet, row_860, (short)5, amount_41.toString());
					total_charges = total_charges.add(amount_41);
					courseReportLister.addCell(sheet, row_860, (short)6, amount_45.abs().toString());
					total_payments = total_payments.add(amount_45.abs());
					row_860 = 0;
					amount_41 = BigDecimal.ZERO;
					amount_45 = BigDecimal.ZERO;
				}
				*/
			}
			
			/*
			courseReportLister.addCell(sheet, row_index, (short)0, order.getOrderDateString(), cell_style7);
			courseReportLister.addCell(sheet, row_index, (short)1, "", cell_style7);
			courseReportLister.addCell(sheet, row_index, (short)2, "subtotal", cell_style7);
			courseReportLister.addCell(sheet, row_index, (short)3, "", cell_style7);
			courseReportLister.addCell(sheet, row_index, (short)4, "", cell_style7);
			courseReportLister.addCell(sheet, row_index, (short)5, order.getSubtotalString(), cell_style8);
			courseReportLister.addCell(sheet, row_index, (short)6, "", cell_style8);
			courseReportLister.addCell(sheet, row_index, (short)7, "", cell_style7);

			row_index++;
			
			courseReportLister.addCell(sheet, row_index, (short)0, order.getOrderDateString(), cell_style7);
			courseReportLister.addCell(sheet, row_index, (short)1, "", cell_style7);
			courseReportLister.addCell(sheet, row_index, (short)2, "tax", cell_style7);
			courseReportLister.addCell(sheet, row_index, (short)3, "", cell_style7);
			courseReportLister.addCell(sheet, row_index, (short)4, "", cell_style7);
			courseReportLister.addCell(sheet, row_index, (short)5, order.getTaxString(), cell_style8);
			courseReportLister.addCell(sheet, row_index, (short)6, "", cell_style8);
			courseReportLister.addCell(sheet, row_index, (short)7, "", cell_style7);

			row_index++;
			*/

			Iterator tender_itr = TenderRet.getTenders(order).iterator();
			while (tender_itr.hasNext())
			{
				TenderRet tender_obj = (TenderRet)tender_itr.next();
				if (!tender_obj.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
				{
					BigDecimal tender_amount_applied_to_order = tender_obj.getAmountAppliedToOrder(order);
					
					courseReportLister.addCell(sheet, row_index, (short)0, order.getOrderDateString(), cell_style7);
					courseReportLister.addCell(sheet, row_index, (short)1, "", cell_style7);
					courseReportLister.addCell(sheet, row_index, (short)2, tender_obj.getLabel() + " (" + tender_amount_applied_to_order.toString() + " applied to order " + order.getLabel() + ")", cell_style7);
					courseReportLister.addCell(sheet, row_index, (short)3, "", cell_style7);
					courseReportLister.addCell(sheet, row_index, (short)4, "", cell_style7);
					courseReportLister.addCell(sheet, row_index, (short)5, "", cell_style7);
					courseReportLister.addCell(sheet, row_index, (short)6, tender_amount_applied_to_order.toString(), cell_style8);
					courseReportLister.addCell(sheet, row_index, (short)7, "", cell_style7);
					
					
					if (!total_tenders.contains(tender_obj)) {
						total_payments = total_payments.add(tender_obj.getAmountBigDecimal());
						//total_payments = total_payments.add(tender_amount_applied_to_order);
						total_tenders.addElement(tender_obj);
					}
					
					total_applied = total_applied.add(tender_amount_applied_to_order);
					
					row_index++;
				}
			}
		}

		courseReportLister.addCell(sheet, row_index + 1, (short)0, "", cell_style);
		courseReportLister.addCell(sheet, row_index + 1, (short)1, "", cell_style);
		courseReportLister.addCell(sheet, row_index + 1, (short)2, "TOTAL", cell_style);
		courseReportLister.addCell(sheet, row_index + 1, (short)3, "", cell_style);
		courseReportLister.addCell(sheet, row_index + 1, (short)4, "", cell_style);
		courseReportLister.addCell(sheet, row_index + 1, (short)5, "CHARGES : " + total_charges.toString(), cell_style2);
		courseReportLister.addCell(sheet, row_index + 1, (short)6, "TENDERS : " + total_payments.toString(), cell_style2);
		courseReportLister.addCell(sheet, row_index + 1, (short)7, "", cell_style);
		
		courseReportLister.addCell(sheet, row_index + 2, (short)0, "", cell_style);
		courseReportLister.addCell(sheet, row_index + 2, (short)1, "", cell_style);
		courseReportLister.addCell(sheet, row_index + 2, (short)2, "", cell_style);
		courseReportLister.addCell(sheet, row_index + 2, (short)3, "", cell_style);
		courseReportLister.addCell(sheet, row_index + 2, (short)4, "", cell_style);
		courseReportLister.addCell(sheet, row_index + 2, (short)5, "", cell_style2);
		courseReportLister.addCell(sheet, row_index + 2, (short)6, "APPLIED : " + total_applied.toString(), cell_style2);
		courseReportLister.addCell(sheet, row_index + 2, (short)7, "", cell_style);

		courseReportLister.addCell(sheet, 15, (short)0, "Date", cell_style);
		courseReportLister.addCell(sheet, 15, (short)1, "", cell_style);
		courseReportLister.addCell(sheet, 15, (short)2, "Description", cell_style);
		courseReportLister.addCell(sheet, 15, (short)3, "Rendering Provider", cell_style);
		courseReportLister.addCell(sheet, 15, (short)4, "", cell_style);
		courseReportLister.addCell(sheet, 15, (short)5, "Charges", cell_style);
		courseReportLister.addCell(sheet, 15, (short)6, "Payments", cell_style2);
		courseReportLister.addCell(sheet, 15, (short)7, "Total / Balance", cell_style2);

		String company_str = adminCompany.getLabel();
		if (company_str.equals("Valeo"))
			company_str = "Valeo Health & Wellness Center";
		courseReportLister.addCell(sheet, 0, (short)0, company_str);

		wb.setPrintArea(0, "$A$1:$I$" + (row_index + 1));

		saveFilename = client.getFirstNameString() + "-" + client.getLastNameString() + "-statement.xls";
		FileOutputStream fileOut = new FileOutputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + saveFilename);
		wb.write(fileOut);
		fileOut.close();
    }
}
catch (Exception x)
{
    x.printStackTrace();
}

%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Valonyx</title>

<link rel="stylesheet" type="text/css" href="../css/schedule-client.css" />
<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/resize/assets/skins/sam/resize.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/layout/assets/skins/sam/layout.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" />

<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/menu/assets/skins/sam/menu.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/calendar/assets/skins/sam/calendar.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/tabview/assets/skins/sam/tabview.css" />

<script type="text/javascript" src="../yui/build/yahoo/yahoo-min.js"></script>
<script type="text/javascript" src="../yui/build/event/event-min.js"></script>
<script type="text/javascript" src="../yui/build/dom/dom-min.js"></script>

<script type="text/javascript" src="../yui/build/element/element-beta-min.js"></script>
<script type="text/javascript" src="../yui/build/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="../yui/build/resize/resize-beta-min.js"></script>
<script type="text/javascript" src="../yui/build/animation/animation-min.js"></script>
<script type="text/javascript" src="../yui/build/layout/layout-beta-min.js"></script>

<style type="text/css">

#tblCharges td, th { padding: 0.25em; font-size:100%; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }
#tblCredits td, th { padding: 0.25em; font-size:100%; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }
#tblPrevious td, th { padding: 0.25em; font-size:100%; }
#tblPrevious td { text-align: right; }
.classy0 { background-color: #234567; color: #89abcd; }
.classy1 { background-color: #89abcd; color: #234567; }

</style>

<style type="text/css">
/*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
body {
	margin:0;
	padding:0;
}
#toggle {
    text-align: center;
    padding: 1em;
}
#toggle a {
    padding: 0 5px;
    border-left: 1px solid black;
}
#tRight {
    border-left: none !important;
}
</style>


<script type="text/javascript">

    var httpRequest;

    function processCommand(command, parameter)
    {
		if (window.ActiveXObject)
			httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
		else if (window.XMLHttpRequest)
			httpRequest = new XMLHttpRequest();

		httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain") %>/ClientServlet.html', true);
		httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
		httpRequest.onreadystatechange = function() {processCommandEvent(); } ;
		eval("httpRequest.send('command=" + command + "&parameter=" + parameter + "')");

		return true;
    }

    function processCommandEvent()
	{
		if (httpRequest.readyState == 4)
		{
			if (httpRequest.status == 200)
			{
				if (httpRequest.responseXML.getElementsByTagName("checkout").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("checkout")[0];
					showCheckout(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];
					showPurchasedPlans(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
					showPeople(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
					showPerson(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("payment-plan-instance").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("payment-plan-instance")[0];
					showPaymentPlanInstance(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("client-billing").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("client-billing")[0];
					showBilling(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("bill").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("bill")[0];
					showBill(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("billing-orders").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("billing-orders")[0];
					showBillingOrders(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("error").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("error")[0];
					alert("Error : " + xml_response.childNodes[0].nodeValue);
				}
			}
			else
			{
				alert("Error : " + httpRequest.status + " : " + httpRequest.statusText);
			}
		}
    }

</script>

<script>
YAHOO.namespace("example.container");

function init() {

}

YAHOO.util.Event.onDOMReady(init);


</script>

<script>

(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
                minWidth: 750,
                units: [
		{ position: 'top', height: 21, resize: false, body: 'top1' },
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
        });
        layout.on('render', function() {
            layout.getUnitByPosition('left').on('close', function() {
                closeLeft();
            });
        });
        layout.render();

    });

})();
</script>

</head>

<%
String searchLastName = (String)session.getAttribute("searchLastName");
if (searchLastName == null)
	searchLastName = "";
%>

<body class="yui-skin-sam">

<!-- <h1>Valeo Schedule</h1> -->


<div id="content" style="height: 100%;">

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>

			<div id="center1" style="background-color: white;">

				<div id="content">


					<div class="content_TextBlock">
						<p class="headline"><%= adminCompany.getLabel()%> Reports - Client Statement</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
					</div>

					<div class="content_Administration">

<%@ include file="channels/channel-report-menu.jsp" %>

						<div class="main">

							<p>Click on the link below to view your report.</p>

							    <div class="adminItem">
								    <div class="leftTM">REPORT LINK</div>
								    <div class="right">
<%
try
{
%>
									<a href="<%= "../resources/" + saveFilename %>" title=""><%= saveFilename %></a>
<%
}
catch (Exception x)
{
	x.printStackTrace();
}
%>
								    </div>
								    <div class="end"></div>
							    </div>




						</div>

						<div class="end"></div>
					</div>



					<div class="content_TextBlock">
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
					</div>

				</div>


			</div>



</div>






<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
