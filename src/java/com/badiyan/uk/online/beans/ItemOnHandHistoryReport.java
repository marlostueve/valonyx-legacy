package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import java.math.BigDecimal;

/**
 *
 * @author marlo
 */
public class
ItemOnHandHistoryReport
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    protected static HashMap<Integer,ItemOnHandHistoryReport> hash = new HashMap<>(11);

    // CLASS METHODS
	
    public static void
    delete(CompanyBean _company)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(ItemOnHandHistoryReportDbPeer.COMPANY_ID, _company.getId());
		ItemOnHandHistoryReportDbPeer.doDelete(crit);
		hash = new HashMap<>(11);
    }
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(ItemOnHandHistoryReportDbPeer.ITEM_ON_HAND_HISTORY_REPORT_DB_ID, _id);
		ItemOnHandHistoryReportDbPeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }

    public static ItemOnHandHistoryReport
    getItemOnHandHistoryReport(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ItemOnHandHistoryReport obj = hash.get(key);
		if (obj == null) {
			Criteria crit = new Criteria();
			crit.add(ItemOnHandHistoryReportDbPeer.ITEM_ON_HAND_HISTORY_REPORT_DB_ID, _id);
			List objList = ItemOnHandHistoryReportDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Item On Hand History Report with id: " + _id);
			obj = ItemOnHandHistoryReport.getItemOnHandHistoryReport((ItemOnHandHistoryReportDb)objList.get(0));
		}
		return obj;
    }

    private static ItemOnHandHistoryReport
    getItemOnHandHistoryReport(ItemOnHandHistoryReportDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getItemOnHandHistoryReportDbId());
		ItemOnHandHistoryReport obj = (ItemOnHandHistoryReport)hash.get(key);
		if (obj == null) {
			obj = new ItemOnHandHistoryReport(_obj);
			hash.put(key, obj);
		}
		return obj;
    }
	
	public static List<ItemOnHandHistoryReport>
	getItemOnHandHistoryReport(Vector<CheckoutCodeBean> checkoutCodes) throws TorqueException {
		
		Criteria crit = new Criteria();
		
		if (!checkoutCodes.isEmpty()) {
			long[] arr = new long[checkoutCodes.size()];
			for (int i = 0; i < checkoutCodes.size(); i++) {
				CheckoutCodeBean code = (CheckoutCodeBean)checkoutCodes.elementAt(i);
				arr[i] = code.getId();
			}
			crit.add(ItemOnHandHistoryReportDbPeer.CHECKOUT_CODE_ID, (Object)arr, Criteria.IN);
		}
		
		crit.addJoin(ItemOnHandHistoryReportDbPeer.CHECKOUT_CODE_ID, CheckoutCodePeer.CHECKOUT_CODE_ID);
		//crit.addJoin(CheckoutCodePeer.INVENTORY_DEPARTMENT_DB_ID, InventoryDepartmentDbPeer.INVENTORY_DEPARTMENT_DB_ID);
		
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		crit.addDescendingOrderByColumn(ItemOnHandHistoryReportDbPeer.CHANGE_DATE);
		
		List<ItemOnHandHistoryReport> itemOnHandHistoryReportList = new ArrayList<>();
		Iterator itr = ItemOnHandHistoryReportDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ItemOnHandHistoryReportDb obj = (ItemOnHandHistoryReportDb)itr.next();
			itemOnHandHistoryReportList.add(ItemOnHandHistoryReport.getItemOnHandHistoryReport(obj));
		}
		return itemOnHandHistoryReportList;
	}
	
	public static Date
	getLastGenerationDate() throws TorqueException {
		Criteria crit = new Criteria();
		crit.addDescendingOrderByColumn(ItemOnHandHistoryReportDbPeer.ROW_UPDATED);
		List l = ItemOnHandHistoryReportDbPeer.doSelect(crit);
		if (l.size() > 0) {
			ItemOnHandHistoryReportDb obj = (ItemOnHandHistoryReportDb)l.get(0);
			return obj.getRowUpdated();
		}
		return null;
	}
	

	/*
    public static Vector
    getItemOnHandHistoryReport(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException
    {
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(ItemOnHandHistoryReportDbPeer.PERSON_ID, _person.getId());
		crit.addAscendingOrderByColumn(ItemOnHandHistoryReportDbPeer.TO_DO_DATE);
		Iterator itr = ItemOnHandHistoryReportDbPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(ItemOnHandHistoryReport.getItemOnHandHistoryReport((ItemOnHandHistoryReport)itr.next()));
	
		return vec;
    }
	*/
    
    // SQL
    
    /*
<table name="ITEM_ON_HAND_HISTORY_REPORT_DB">
    <column name="ITEM_ON_HAND_HISTORY_REPORT_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="CHECKOUT_CODE_ID" required="true" type="INTEGER"/>
    <column name="ON_HAND_QUANTITY_DELTA" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="ON_HAND_QUANTITY_UPDATED" required="true" scale="2" size="7" type="DECIMAL"/>
	<column name="CHANGE_TYPE" required="true" type="VARCHAR" size="30"/>
    <column name="CHANGE_DATE" required="true" type="DATE"/>
	
    <column name="ROW_UPDATED" required="true" type="DATE"/>

    <foreign-key foreignTable="CHECKOUT_CODE">
		<reference local="CHECKOUT_CODE_ID" foreign="CHECKOUT_CODE_ID"/>
    </foreign-key>
</table>
     */
	
    // INSTANCE VARIABLES
	
    private ItemOnHandHistoryReportDb report;
    
    // CONSTRUCTORS
    
    public
    ItemOnHandHistoryReport() {
		report = new ItemOnHandHistoryReportDb();
		isNew = true;
    }
    
    public
    ItemOnHandHistoryReport(ItemOnHandHistoryReportDb _obj) {
		report = _obj;
		isNew = false;
    }
    
    // INSTANCE METHODS
    
    public int
    getId() {
		return report.getItemOnHandHistoryReportDbId();
    }
    
    public String
    getLabel() {
		return this.getValue();
    }
	
	public BigDecimal
	getOnHandQuantityDelta() {
		return report.getOnHandQuantityDelta();
	}
	
	public void
	setOnHandQuantityDelta(BigDecimal _delta) {
		report.setOnHandQuantityDelta(_delta);
	}
	
	public BigDecimal
	getOnHandQuantityUpdated() {
		return report.getOnHandQuantityUpdated();
	}
	
	public void
	setOnHandQuantityUpdated(BigDecimal _updated) {
		report.setOnHandQuantityUpdated(_updated);
	}

	public String getChangeType() {
		return report.getChangeType();
	}

	public void setChangeType(String changeType) {
		report.setChangeType(changeType);
	}

	public CheckoutCodeBean getCheckoutCode() throws TorqueException, ObjectNotFoundException {
		return CheckoutCodeBean.getCheckoutCode(report.getCheckoutCodeId());
	}

	public void setCheckoutCode(CheckoutCodeBean checkoutCode) throws TorqueException {
		report.setCheckoutCodeId(checkoutCode.getId());
	}

	public Date getChangeDate() {
		return report.getChangeDate();
	}

	public String
	getChangeDateString() {
		if (report.getChangeDate() == null)
			return CUBean.getUserDateString(new Date());
		return CUBean.getUserDateString(report.getChangeDate());
	}

	public void setChangeDate(Date changeDate) {
		report.setChangeDate(changeDate);
	}

	public CompanyBean getCompany() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return CompanyBean.getCompany(report.getCompanyId());
	}

	public void setCompany(CompanyBean company) {
		report.setCompanyId(company.getId());
	}
    
    public String
    getValue() {
		return this.getId() + "";
    }
    
    protected void
    insertObject() throws Exception {
		report.setRowUpdated(new Date());
		report.save();
    }
    
    protected void
    updateObject() throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		report.setRowUpdated(new Date());
		report.save();
    }
}