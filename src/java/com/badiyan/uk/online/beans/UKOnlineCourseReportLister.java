/*
 * UKOnlineCourseReportLister.java
 *
 * Created on March 19, 2007, 10:38 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.valonyx.beans.IoTLOKReportTemplateBean;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class
UKOnlineCourseReportLister
	extends com.badiyan.uk.online.beans.CourseReportLister
	implements java.io.Serializable
{
	// CLASS VARIABLES
    
	public static final String MENTOR_ACTIVITY_TYPE_NAME = "Mentor Activity";
	public static final String E_LEARNING_ACTIVITY_TYPE_NAME = "E-Learning Activity";
	public static final String INSTRUCTOR_LED_ACTIVITY_TYPE_NAME = "Instructor-Led Activity";
	public static final String ASSESSMENT_ACTIVITY_TYPE_NAME = "Assessment Activity";
	
	public static final short USER_ASC_SORT = 1;
	public static final short USER_DESC_SORT = 2;
	public static final short GROUP_ASC_SORT = 3;
	public static final short GROUP_DESC_SORT = 4;
	public static final short LOCATION_ASC_SORT = 5;
	public static final short LOCATION_DESC_SORT = 6;
	public static final short JOB_ASC_SORT = 7;
	public static final short JOB_DESC_SORT = 8;
	public static final short SCORE_ASC_SORT = 9;
	public static final short SCORE_DESC_SORT = 10;
	public static final short STATUS_ASC_SORT = 11;
	public static final short STATUS_DESC_SORT = 12;
	public static final short COURSE_NAME_ASC_SORT = 13;
	public static final short COURSE_NAME_DESC_SORT = 14;
	
	// INSTANCE VARIABLES
	
	private short sort;

	private CompanyBean company;
	
	private Vector stores;
	private Vector manufacturers;
	private Vector products;
	
	private boolean show_inactives = false;
	private boolean show_html = false;
	private boolean show_excel = false;
	
	private int status = 0;
	
	private Date start_date = null;
	private Date end_date = null;

	private UKOnlinePersonBean person = null;

	private Vector practice_areas;
	private boolean show_all_practice_areas = false;

	private Vector checkout_code_types;
	private boolean show_all_types = false;
	
	// CONSTRUCTORS
	
	public
	UKOnlineCourseReportLister()
	{
		sort = CourseReportLister.USER_ASC_SORT;
	}
	
	// INSTANCE METHODS
	
	protected void
	getAllList()
		throws TorqueException
	{
	}
	
	protected CUBean
	getNewListBeanItem(Object _obj)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		return null;
	}
	
	/*

<table name="PRODUCT_DB" idMethod="native">
	<column name="PRODUCT_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	<column name="NAME" size="250" type="VARCHAR"/>
    <column name="DESCRIPTION" type="LONGVARCHAR"/>
	<column name="MODEL_NUMBER" size="30" type="VARCHAR"/>
	<column name="MFG_ID" required="true" type="INTEGER"/>
	
	<column name="U_U_I_D" size="20" type="VARCHAR"/>
	<column name="EAN" size="20" type="VARCHAR"/>
    
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
	
	<column name="IS_ACTIVE" type="SMALLINT" default="1"/>

    <foreign-key foreignTable="COMPANY">
		<reference local="MFG_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>

<table name="PRODUCT_CHANGE_LOG_DB" idMethod="native">
	<column name="PRODUCT_LOG_DB" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	<column name="MODIFICATION_DATE" required="true" type="DATE"/>
    <column name="MODIFY_PERSON_ID" required="true" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
		<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>

<table name="PRODUCT_IMAGE_MAP">
	<column name="PRODUCT_DB_ID" required="true" primaryKey="true" type="BIGINT"/>
	<column name="IMAGE_U_R_L" size="250" type="VARCHAR" primaryKey="true" />

    <foreign-key foreignTable="PRODUCT_DB">
		<reference local="PRODUCT_DB_ID" foreign="PRODUCT_DB_ID"/>
    </foreign-key>
</table>

<table name="PRODUCT_INTERACTION_DB" idMethod="native">
	<column name="PRODUCT_INTERACTION_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	<column name="RETAILER_ID" required="false" type="INTEGER"/>
	<column name="STORE_ID" required="false" type="INTEGER"/>
	<column name="INTERACTION_PERSON_ID" required="false" type="INTEGER"/>
	<column name="PRODUCT_DB_ID" required="true" type="BIGINT"/>
	
    <column name="UNIQUE_ID" size="20" type="VARCHAR"/>
	<!-- Can be MAC Address, Serial Number, or something else agreed upon as long as it's unique for the unit in question -->
	<!-- must be the same id on sale as on activation -->
	
	<column name="INTERACTION_TYPE" type="SMALLINT"/>
	<!-- 1 = inventory -->
	<!-- 2 = sale - not activated -->
	<!-- 3 = sale - activated -->
	<!-- 4 = return - de-activated -->
	<!-- 5 = de-activation other -->
	
    <column name="INTERACTION_DATE" required="true" type="DATE"/>

    <foreign-key foreignTable="COMPANY">
		<reference local="RETAILER_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="DEPARTMENT">
		<reference local="STORE_ID" foreign="DEPARTMENTID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="INTERACTION_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PRODUCT_DB">
		<reference local="PRODUCT_DB_ID" foreign="PRODUCT_DB_ID"/>
    </foreign-key>
</table>
	*/
	
	public void
	searchProducts()
		throws TorqueException
	{
		boolean needs_secondary_sort = true;
		
		Criteria crit = new Criteria();
		crit.addJoin(ProductDbPeer.PRODUCT_DB_ID, ProductInteractionDbPeer.PRODUCT_DB_ID);
		
		if ((stores != null) && !stores.isEmpty()) { // department
		    //crit.addJoin(ProductDbPeer.PRODUCT_DB_ID, ProductInteractionDbPeer.PRODUCT_DB_ID);
		    int[] arr = new int[stores.size()];
			for (int i = 0; i < stores.size(); i++) {
				DepartmentBean department = (DepartmentBean)stores.elementAt(i);
				arr[i] = department.getId();
			}
			crit.add(ProductInteractionDbPeer.STORE_ID, (Object)arr, Criteria.IN);
		}
		
		if ((manufacturers != null) && !manufacturers.isEmpty()) { // company
		    int[] arr = new int[manufacturers.size()];
			for (int i = 0; i < manufacturers.size(); i++) {
				CompanyBean mfg = (CompanyBean)manufacturers.elementAt(i);
				arr[i] = mfg.getId();
			}
			crit.add(ProductDbPeer.MFG_ID, (Object)arr, Criteria.IN);
		}
		
		if ((products != null) && !products.isEmpty()) {
		    long[] arr = new long[products.size()];
			for (int i = 0; i < products.size(); i++) {
				com.valonyx.beans.Product product = (com.valonyx.beans.Product)products.elementAt(i);
				arr[i] = product.getId();
			}
			crit.add(ProductDbPeer.PRODUCT_DB_ID, (Object)arr, Criteria.IN);
		}
		
		/*
		if (needs_secondary_sort)
		{
			crit.addJoin(EnrollmentRefPeer.PERSONID, PersonPeer.PERSONID);
			crit.add(PersonPeer.ISACTIVE, 1);
			crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
			crit.addAscendingOrderByColumn(PersonPeer.FIRSTNAME);
		}
		*/
		
		if (start_date != null) {
			System.out.println("start_date >>" + start_date);
			crit.add(ProductInteractionDbPeer.INTERACTION_DATE, start_date, Criteria.GREATER_THAN);
			if (end_date != null) {
				crit.and(ProductInteractionDbPeer.INTERACTION_DATE, end_date, Criteria.LESS_THAN);
			}
		} else if (end_date != null) {
			System.out.println("end_date >>" + end_date);
			crit.add(ProductInteractionDbPeer.INTERACTION_DATE, end_date, Criteria.LESS_THAN);
		}
		
		System.out.println("crit >" + crit.toString());
		
		/*
		if (status > 0) {
			switch (status)
			{
				case 1: crit.add(EnrollmentRefPeer.ENROLLMENTSTATUS, EnrollmentBean.COMPLETED_ENROLLMENT_STATUS); break;
				case 2: crit.add(EnrollmentRefPeer.ENROLLMENTSTATUS, EnrollmentBean.STARTED_ENROLLMENT_STATUS); break;
				case 3: crit.add(EnrollmentRefPeer.ENROLLMENTSTATUS, EnrollmentBean.COMPLETED_ENROLLMENT_STATUS); crit.or(EnrollmentRefPeer.ENROLLMENTSTATUS, EnrollmentBean.STARTED_ENROLLMENT_STATUS); break;
				case 4: crit.add(EnrollmentRefPeer.ENROLLMENTSTATUS, EnrollmentBean.NOT_STARTED_ENROLLMENT_STATUS); break;
			}
		}
		*/
		
		//list = ProductDbPeer.doSelect(crit);
		
		list = ProductInteractionDbPeer.doSelect(crit);
		
	}
	
	public void
	searchByKeyword(String _keyword)
		throws TorqueException {
	}
	
	public List
	getListStuff() {
		return list;
	}
	
	public short
	getSort()
	{
		return sort;
	}
	
	public void
	setSort(short _sort)
	{
		sort = _sort;
	}
	
	public void
	invalidateSearchResults()
	{
		super.invalidateSearchResults();
		stores = new Vector();
		manufacturers = new Vector();
		products = new Vector();
	}

	public Vector getStores() {
		return stores;
	}

	public void setStores(Vector stores) {
		this.stores = stores;
	}

	public Vector getManufacturers() {
		return manufacturers;
	}

	public void setManufacturers(Vector manufacturers) {
		this.manufacturers = manufacturers;
	}

	public Vector getProducts() {
		return products;
	}

	public void setProducts(Vector products) {
		this.products = products;
	}
	
	
	
	/*
	public void
	setCourse(CourseBean _course)
	{
	    course = _course;
	}
	
	public void
	addCourse(CourseBean _course)
	{
	    courses.addElement(_course);
	}
	
	public void
	setTitle(PersonTitleBean _title)
	{
		title = _title;
	}
	
	public void
	setLocation(DepartmentBean _department)
	{
		department = _department;
	}
	
	public void
	setDepartments(Vector _departments)
	{
	    departments = _departments;
	}
	
	public String
	getLocationString()
	{
		if (department == null)
			return "ALL LOCATIONS";
		return department.getNameString();
	}
	
	public void
	setGroup(PersonGroupBean _group)
	{
		group = _group;
	}
	*/
	
	public HSSFCellStyle
	getStyle(HSSFSheet _sheet, int _row_index, short _column_index)
	{
		HSSFRow row = _sheet.getRow(_row_index);
		if (row == null)
			row = _sheet.createRow(_row_index);
		HSSFCell cell = row.getCell(_column_index);
		return cell.getCellStyle();
	}

	public void
	shiftRow(HSSFSheet _sheet, int _row_index)
	{
		_sheet.shiftRows(_row_index, _sheet.getLastRowNum(), 1);
	}
	
	public void
	addCell(HSSFSheet _sheet, int _row_index, short _column_index, String _value)
	{
		HSSFRow row = _sheet.getRow(_row_index);
		if (row == null)
			row = _sheet.createRow(_row_index);
		HSSFCell cell = row.getCell(_column_index);
		if (cell == null)
			cell = row.createCell(_column_index);
		//cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(_value);
	}
	
	public void
	addCell(HSSFSheet _sheet, int _row_index, short _column_index, String _value, HSSFCellStyle _cell_style)
	{
		HSSFRow row = _sheet.getRow(_row_index);
		if (row == null)
			row = _sheet.createRow(_row_index);
		HSSFCell cell = row.getCell(_column_index);
		if (cell == null)
			cell = row.createCell(_column_index);
		//cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellStyle(_cell_style);
		cell.setCellValue(_value);
	}
	
	public void
	addCell(HSSFSheet _sheet, int _row_index, short _column_index, int _value)
	{
		HSSFRow row = _sheet.getRow(_row_index);
		if (row == null)
			row = _sheet.createRow(_row_index);
		HSSFCell cell = row.getCell(_column_index);
		if (cell == null)
			cell = row.createCell(_column_index);
		//cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setCellValue(_value);
	}
	
	public void
	addCell(XSSFSheet _sheet, int _row_index, short _column_index, String _value)
	{
		XSSFRow row = _sheet.getRow(_row_index);
		if (row == null) {
			row = _sheet.createRow(_row_index);
		}
		XSSFCell cell = row.getCell(_column_index);
		if (cell == null) {
			cell = row.createCell(_column_index);
		}
		cell.setCellValue(_value);
		
	}
	
	public void
	addCell(XSSFSheet _sheet, int _row_index, short _column_index, int _value)
	{
		XSSFRow row = _sheet.getRow(_row_index);
		if (row == null) {
			row = _sheet.createRow(_row_index);
		}
		XSSFCell cell = row.getCell(_column_index);
		if (cell == null) {
			cell = row.createCell(_column_index);
		}
		cell.setCellValue(_value);
	}
	
	public void
	addCell(XSSFSheet _sheet, int _row_index, short _column_index, String _value, HSSFCellStyle _cell_style)
	{
		XSSFRow row = _sheet.getRow(_row_index);
		if (row == null) {
			row = _sheet.createRow(_row_index);
		}
		XSSFCell cell = row.getCell(_column_index);
		if (cell == null) {
			cell = row.createCell(_column_index);
		}
		cell.setCellStyle(_cell_style);
		cell.setCellValue(_value);
		
	}
	
	public void
	addCell(XSSFSheet _sheet, int _row_index, short _column_index, int _value, HSSFCellStyle _cell_style)
	{
		XSSFRow row = _sheet.getRow(_row_index);
		if (row == null) {
			row = _sheet.createRow(_row_index);
		}
		XSSFCell cell = row.getCell(_column_index);
		if (cell == null) {
			cell = row.createCell(_column_index);
		}
		cell.setCellStyle(_cell_style);
		cell.setCellValue(_value);
	}
	
	public void
	setShowHTML(boolean _show)
	{
		show_html = _show;
	}
	
	public void
	setShowExcel(boolean _show)
	{
		show_excel = _show;
	}
	
	public boolean
	showHTML()
	{
		return show_html;
	}
	
	public boolean
	showExcel()
	{
		return show_excel;
	}
	
	public void
	setStatus(int _status)
	{
		status = _status;
	}
	
	public void
	setStartDate(Date _start)
	{
		Calendar start_cal = Calendar.getInstance();
		start_cal.setTime(_start);
		start_cal.set(Calendar.HOUR_OF_DAY, 0);
		start_cal.set(Calendar.MINUTE, 0);
		start_cal.set(Calendar.SECOND, 0);

		start_date = start_cal.getTime();
	}
	
	public void
	setEndDate(Date _end)
	{
		Calendar end_cal = Calendar.getInstance();
		end_cal.setTime(_end);
		end_cal.set(Calendar.HOUR_OF_DAY, 23);
		end_cal.set(Calendar.MINUTE, 59);
		end_cal.set(Calendar.SECOND, 59);

		end_date = end_cal.getTime();
	}

	public void
	setStartDateNoModify(Date _start)
	{
		start_date = _start;
	}

	public void
	setEndDateNoModify(Date _end)
	{
		end_date = _end;
	}
	
	public Date
	getStartDate()
	{
		return start_date;
	}
	
	public String
	getStartDateString()
	{
		return CUBean.getUserDateString(start_date);
	}
	
	public Date
	getEndDate()
	{
		return end_date;
	}
	
	public String
	getEndDateString()
	{
		return CUBean.getUserDateString(end_date);
	}

	public void
	setPerson(UKOnlinePersonBean _person)
	{
		person = _person;
	}

	public UKOnlinePersonBean
	getPerson()
	{
		return person;
	}

	public void
	setPracticeAreas(Vector _practice_areas)
	{
		practice_areas = _practice_areas;
	}

	public Vector
	getPracticeAreas()
	{
		return practice_areas;
	}

	public void
	setShowAllPracticeAreas(boolean _show_all_practice_areas) {
		this.show_all_practice_areas = _show_all_practice_areas;
	}

	public boolean
	showAllPracticeAreas() {
		return this.show_all_practice_areas;
	}

	public void
	setShowAllCheckoutCodeTypes(boolean _show_all_types) {
		this.show_all_types = _show_all_types;
	}

	public boolean
	showAllCheckoutCodeTypes() {
		return this.show_all_types;
	}

	public void
	setCheckoutCodeTypes(Vector _types) {
		this.checkout_code_types = _types;
	}

	public Vector
	getCheckoutCodeTypes() {
		return this.checkout_code_types;
	}

	public void
	setCompany(CompanyBean _company) {
		company = _company;
	}
	
	public void
	setShowInactives(boolean _show) {
		show_inactives = _show;
	}
	
	public void
	setReportTemplate(IoTLOKReportTemplateBean _template) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		
		System.out.println("setReportTemplate() invoked >");
		
		this.setCompany(_template.getCompany());
		
		this.setProducts(_template.getProducts());
		this.setManufacturers(_template.getManufacturers());
		this.setStores(_template.getDepartments());
		
		//this.setCourseGroups(_template.getCourseGroups());
		//this.setCertifications(_template.getCertifications()); // why was this commented out???

		if (_template.getEndDate() != null) {
			this.setEndDate(_template.getEndDate());
		}
		if (_template.getStartDate() != null) {
			this.setStartDate(_template.getStartDate());
		}
		
		this.setShowExcel(_template.isShowExcel());
		this.setShowInactives(_template.isIncludeInactive());
		
		
		/*
<select class="form-control" name="statusSelect" id="statusSelect"  >
	<option value="0">-- <struts-bean:message key="admin.admin-report-activity-filter.text05" /> --</option>
	<option value="1"<%= (adminReportTemplate.getEnrollmentStatus().equals(EnrollmentBean.COMPLETED_ENROLLMENT_STATUS)) ? " selected" : "" %>>Completed</option>
	<option value="2"<%= (adminReportTemplate.getEnrollmentStatus().equals(EnrollmentBean.NOT_STARTED_ENROLLMENT_STATUS)) ? " selected" : "" %>>Approved & In Progress</option>
	<option value="3"<%= (adminReportTemplate.getEnrollmentStatus().equals(EnrollmentBean.WAITLIST_ENROLLMENT_STATUS)) ? " selected" : "" %>>Wait List</option>
	<option value="4"<%= (adminReportTemplate.getEnrollmentStatus().equals(EnrollmentBean.INSTRUCTOR_CONFIRMED)) ? " selected" : "" %>>Instructor Confirmed</option>
	<option value="5"<%= (adminReportTemplate.getEnrollmentStatus().equals(EnrollmentBean.FINAL_APPROVAL)) ? " selected" : "" %>>Final Approval</option>
	<option value="6"<%= (adminReportTemplate.getEnrollmentStatus().equals(EnrollmentBean.FINAL_APPROVAL_MORE_INFO)) ? " selected" : "" %>>Final Approval - More Info</option>
	<option value="7"<%= (adminReportTemplate.getEnrollmentStatus().equals(EnrollmentBean.DENIED_ENROLLMENT_STATUS)) ? " selected" : "" %>>Denied</option>
	<option value="8"<%= (adminReportTemplate.getEnrollmentStatus().equals(EnrollmentBean.NO_SHOW_STATUS)) ? " selected" : "" %>>No Show</option>
	<option value="9"<%= (adminReportTemplate.getEnrollmentStatus().equals(EnrollmentBean.DROPPED_ENROLLMENT_STATUS)) ? " selected" : "" %>>Dropped</option>
</select>
				case 1: crit.add(EnrollmentRefPeer.ENROLLMENTSTATUS, EnrollmentBean.COMPLETED_ENROLLMENT_STATUS); break;
				case 2: crit.add(EnrollmentRefPeer.ENROLLMENTSTATUS, EnrollmentBean.STARTED_ENROLLMENT_STATUS); break;
				case 3: crit.add(EnrollmentRefPeer.ENROLLMENTSTATUS, EnrollmentBean.COMPLETED_ENROLLMENT_STATUS); crit.or(EnrollmentRefPeer.ENROLLMENTSTATUS, EnrollmentBean.STARTED_ENROLLMENT_STATUS); break;
				case 4: crit.add(EnrollmentRefPeer.ENROLLMENTSTATUS, EnrollmentBean.NOT_STARTED_ENROLLMENT_STATUS); break;
		 */
		
		
		System.out.println("_template.getEnrollmentStatus() >" + _template.getEnrollmentStatus());
		
		/*
		if (_template.getEnrollmentStatus().equals(EnrollmentBean.COMPLETED_ENROLLMENT_STATUS)) {
			this.setStatus(1);
		} else if (_template.getEnrollmentStatus().equals(EnrollmentBean.NOT_STARTED_ENROLLMENT_STATUS)) {
			this.setStatus(12);
		} else if (_template.getEnrollmentStatus().equals(EnrollmentBean.WAITLIST_ENROLLMENT_STATUS)) {
			this.setStatus(5);
		} else if (_template.getEnrollmentStatus().equals(EnrollmentBean.INSTRUCTOR_CONFIRMED)) {
			this.setStatus(6);
		} else if (_template.getEnrollmentStatus().equals(EnrollmentBean.FINAL_APPROVAL)) {
			this.setStatus(7);
		} else if (_template.getEnrollmentStatus().equals(EnrollmentBean.FINAL_APPROVAL_MORE_INFO)) {
			this.setStatus(8);
		} else if (_template.getEnrollmentStatus().equals(EnrollmentBean.DENIED_ENROLLMENT_STATUS)) {
			this.setStatus(9);
		} else if (_template.getEnrollmentStatus().equals(EnrollmentBean.NO_SHOW_STATUS)) {
			this.setStatus(10);
		} else if (_template.getEnrollmentStatus().equals(EnrollmentBean.DROPPED_ENROLLMENT_STATUS)) {
			this.setStatus(11);
		} else if (_template.getEnrollmentStatus().equals(EnrollmentBean.STARTED_ENROLLMENT_STATUS)) {
			this.setStatus(2);
		} else if (_template.getEnrollmentStatus().equals("Not Enrolled")) {
			this.setStatus(-1);
		} else if (_template.getEnrollmentStatus().equals("1")) {
			this.setStatus(1);
		} else if (_template.getEnrollmentStatus().equals("2")) {
			this.setStatus(12);
		} else if (_template.getEnrollmentStatus().equals("3")) {
			this.setStatus(2);
		} else if (_template.getEnrollmentStatus().equals("4")) {
			this.setStatus(5);
		} else if (_template.getEnrollmentStatus().equals("5")) {
			this.setStatus(7); // shrug
		} else if (_template.getEnrollmentStatus().equals("6")) {
			this.setStatus(-1); // shrug
		} else {
			this.setStatus(0);
		}
		*/
		
	}
}