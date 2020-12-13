/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.beans;

import com.badiyan.torque.ReportTemplate;
import com.badiyan.torque.ReportTemplateMfgMap;
import com.badiyan.torque.ReportTemplateMfgMapPeer;
import com.badiyan.torque.ReportTemplatePeer;
import com.badiyan.torque.ReportTemplateProductMap;
import com.badiyan.torque.ReportTemplateProductMapPeer;
import com.badiyan.uk.beans.CompanyBean;
import com.badiyan.uk.beans.PersonBean;
import com.badiyan.uk.beans.PersonTitleBean;
import com.badiyan.uk.beans.ReportTemplateBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.beans.UKOnlineCompanyBean;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class IoTLOKReportTemplateBean
	extends ReportTemplateBean
	implements java.io.Serializable {
	
	// CLASS METHODS
	
	public static ReportTemplateBean
	getTemplate(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		/*
		if (_id == 0)
			throw new ObjectNotFoundException("Could not locate template with id: " + _id);
		 */
		
		Integer key = new Integer(_id);
		IoTLOKReportTemplateBean template = (IoTLOKReportTemplateBean)templates.get(key);
		if (template == null)
		{	
			Criteria crit = new Criteria();
			crit.add(ReportTemplatePeer.REPORT_TEMPLATE_ID, _id);
			List objList = ReportTemplatePeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate template with id: " + _id);
		
			template = IoTLOKReportTemplateBean.getTemplate((ReportTemplate)objList.get(0));
		}
		
		return template;
	}
	
	private static IoTLOKReportTemplateBean
	getTemplate(ReportTemplate _template) {
		Integer key = new Integer(_template.getReportTemplateId());
		IoTLOKReportTemplateBean template = (IoTLOKReportTemplateBean)templates.get(key);
		if (template == null)
		{
			template = new IoTLOKReportTemplateBean(_template);
			templates.put(key, template);
		}
		
		return template;
	}
	
	public static Vector
	getTemplates(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException
	{
		Vector templates = new Vector();
				
		Criteria crit = new Criteria();
		crit.add(ReportTemplatePeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(ReportTemplatePeer.TEMPLATE_TITLE);
		Iterator obj_itr = ReportTemplatePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext()) {
			templates.addElement(IoTLOKReportTemplateBean.getTemplate((ReportTemplate)obj_itr.next()));
		}
		
		return templates;
	}
	
	public static Vector
	getTemplates(PersonBean _person)
		throws TorqueException, ObjectNotFoundException
	{
		Vector templates = new Vector();
				
		Criteria crit = new Criteria();
		crit.add(ReportTemplatePeer.PERSON_ID, _person.getId());
		crit.addAscendingOrderByColumn(ReportTemplatePeer.TEMPLATE_TITLE);
		Iterator obj_itr = ReportTemplatePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext()) {
			templates.addElement(IoTLOKReportTemplateBean.getTemplate((ReportTemplate)obj_itr.next()));
		}
		
		return templates;
	}
	
	// CONSTRUCTORS
	
	public IoTLOKReportTemplateBean() {
		super();
	}
	
	public IoTLOKReportTemplateBean(ReportTemplate _template) {
		super(_template);
	}
	
	/*
	b.append("\"report\":[");
	b.append(this.toJSONDropdown("Product Report", 12, (report_template.getReportType() == (short)12))); b.append(",");
	b.append(this.toJSONDropdown("Activation Report", 13, (report_template.getReportType() == (short)13))); b.append(",");
	b.append(this.toJSONDropdown("Serial Number Report", 14, (report_template.getReportType() == (short)14))); b.append(",");
	b.append(this.toJSONDropdown("Serial Number Report History Report", 15, (report_template.getReportType() == (short)15))); b.append(",");
	b.append(this.toJSONDropdown("User Report", 6, (report_template.getReportType() == (short)6)));
	b.append("],");
	*/
	
	public static final short PRODUCT_REPORT_TYPE = (short)12;
	public static final short ACTIVATION_REPORT_TYPE = (short)13;
	public static final short SERIAL_NUMBER_REPORT_TYPE = (short)14;
	public static final short SERIAL_NUMBER_HISTORY_REPORT_TYPE = (short)15;
	public static final short INTERACTION_REPORT_TYPE = (short)16;
	
	public String
	getReportTypeString() {
		
		switch (this.getReportType()) {
			case (short)12: return "Product Report";
			case (short)13: return "Activation Report";
			case (short)14: return "Serial Number Report";
			case (short)15: return "Serial Number Report History Report";
			case (short)16: return "Interaction Report";
		}
		
		return super.getReportTypeString();
	}

	public String getProductStatus() {
		String str = template.getEnrollmentStatus();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setProductStatus(String _str) {
		template.setEnrollmentStatus(_str);
	}

	@Override
	protected void insertObject() throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		super.insertObject(); //To change body of generated methods, choose Tools | Templates.
		
		try {
			this.saveManufacturers();
			this.saveProducts();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	@Override
	protected void updateObject() throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		super.updateObject(); //To change body of generated methods, choose Tools | Templates.
		
		try {
			this.saveManufacturers();
			this.saveProducts();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	/*
<table name="REPORT_TEMPLATE_MFG_MAP">
    <column name="REPORT_TEMPLATE_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="COMPANY_ID" required="true" primaryKey="true" type="INTEGER"/>

    <foreign-key foreignTable="REPORT_TEMPLATE">
		<reference local="REPORT_TEMPLATE_ID" foreign="REPORT_TEMPLATE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
</table>
	 */
	
	private Vector<CompanyBean> manufacturers = null;
	
	public void
	setManufacturers(Vector _v) {
		if (_v == null || _v.isEmpty()) {
			this.setIncludeAllCourseGroups(true);
		} else {
			this.setIncludeAllCourseGroups(false);
		}
		manufacturers = _v;
	}
	
	public synchronized Vector
	getManufacturers()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		
		if (manufacturers == null) {
			manufacturers = new Vector();

			Criteria crit = new Criteria();
			crit.add(ReportTemplateMfgMapPeer.REPORT_TEMPLATE_ID, this.getId());
			Iterator itr = ReportTemplateMfgMapPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				ReportTemplateMfgMap obj = (ReportTemplateMfgMap)itr.next();
				try {
					manufacturers.addElement(UKOnlineCompanyBean.getCompany(obj.getCompanyId()));
				} catch (Exception x) {
					
				}
			}
		}
		return manufacturers;
	}
	
	private synchronized void
    saveManufacturers()
		throws TorqueException, Exception {

		if (this.manufacturers != null) {
			
			System.out.println("saveManufacturers sizer >" + this.manufacturers.size());

			HashMap db_set_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(ReportTemplateMfgMapPeer.REPORT_TEMPLATE_ID, this.getId());
			Iterator itr = ReportTemplateMfgMapPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				System.out.println("found existing ass...");
				ReportTemplateMfgMap value = (ReportTemplateMfgMap)itr.next();
				Integer key = new Integer(value.getCompanyId());
				db_set_hash.put(key, value);
			}

			itr = this.manufacturers.iterator();
			while (itr.hasNext()) {
				CompanyBean ass_mfg = (CompanyBean)itr.next();
				Integer key = new Integer(ass_mfg.getId());
				ReportTemplateMfgMap existing_map = (ReportTemplateMfgMap)db_set_hash.remove(key);
				
				if (existing_map == null) {
					ReportTemplateMfgMap map = new ReportTemplateMfgMap();
					map.setCompanyId(ass_mfg.getId());
					map.setReportTemplateId(this.getId());
					map.save();
				}
			}

			itr = db_set_hash.keySet().iterator();
			while (itr.hasNext()) {
				Integer key = (Integer)itr.next();
				ReportTemplateMfgMap obj = (ReportTemplateMfgMap)db_set_hash.get(key);
				System.out.println("del obj >" + obj);
				ReportTemplateMfgMapPeer.doDelete(obj);
			}
			
		}
    }
	
	
	
	/*
<table name="REPORT_TEMPLATE_PRODUCT_MAP">
    <column name="REPORT_TEMPLATE_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="PRODUCT_DB_ID" required="true" primaryKey="true" type="BIGINT" />

    <foreign-key foreignTable="REPORT_TEMPLATE">
		<reference local="REPORT_TEMPLATE_ID" foreign="REPORT_TEMPLATE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PRODUCT_DB">
		<reference local="PRODUCT_DB_ID" foreign="PRODUCT_DB_ID"/>
    </foreign-key>
</table>
	 */
	
	private Vector<Product> products = null; 
	
	public void
	setProducts(Vector _v) {
		if (_v == null || _v.isEmpty()) {
			this.setIncludeAllRequirementSets(true);
		} else {
			this.setIncludeAllRequirementSets(false);
		}
		products = _v;
	}
	
	public synchronized Vector
	getProducts()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		
		if (products == null) {
			products = new Vector();

			Criteria crit = new Criteria();
			crit.add(ReportTemplateProductMapPeer.REPORT_TEMPLATE_ID, this.getId());
			Iterator itr = ReportTemplateProductMapPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				ReportTemplateProductMap obj = (ReportTemplateProductMap)itr.next();
				products.addElement(Product.getProduct(obj.getProductDbId()));
			}
		}
		return products;
	}
	
	private synchronized void
    saveProducts()
		throws TorqueException, Exception {

		if (this.products != null) {
			
			System.out.println("saveProducts sizer >" + this.products.size());

			HashMap db_set_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(ReportTemplateProductMapPeer.REPORT_TEMPLATE_ID, this.getId());
			Iterator itr = ReportTemplateProductMapPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				System.out.println("found existing ass...");
				ReportTemplateProductMap value = (ReportTemplateProductMap)itr.next();
				Long key = new Long(value.getProductDbId());
				db_set_hash.put(key, value);
			}

			itr = this.products.iterator();
			while (itr.hasNext()) {
				Product ass_product = (Product)itr.next();
				Long key = new Long(ass_product.getId());
				ReportTemplateProductMap existing_map = (ReportTemplateProductMap)db_set_hash.remove(key);
				if (existing_map == null) {
					// map doesn't already exist in db, go ahead and save (insert) it
					ReportTemplateProductMap map = new ReportTemplateProductMap();
					map.setProductDbId(ass_product.getId());
					map.setReportTemplateId(this.getId());
					map.save();
				}
			}

			itr = db_set_hash.keySet().iterator();
			while (itr.hasNext()) {
				Integer key = (Integer)itr.next();
				ReportTemplateProductMap obj = (ReportTemplateProductMap)db_set_hash.get(key);
				System.out.println("del obj >" + obj);
				ReportTemplateProductMapPeer.doDelete(obj);
			}
			
		}
    }
}
