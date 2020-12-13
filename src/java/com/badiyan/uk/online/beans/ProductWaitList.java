/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import com.valeo.qb.data.VendorRet;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
ProductWaitList
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,ProductWaitList> hash = new HashMap<Integer,ProductWaitList>(11);

    // CLASS METHODS

    public static ProductWaitList
    getProductWaitListEntry(int _id)
		throws TorqueException, ObjectNotFoundException {
		Integer key = new Integer(_id);
		ProductWaitList obj = (ProductWaitList)hash.get(key);
		if (obj == null) {
			Criteria crit = new Criteria();
			crit.add(ProductWaitListDbPeer.PRODUCT_WAIT_LIST_DB_ID, _id);
			List objList = ProductWaitListDbPeer.doSelect(crit);
			if (objList.size() == 0) {
				throw new ObjectNotFoundException("Could not locate Wait List with id: " + _id);
			}

			obj = ProductWaitList.getProductWaitListEntry((ProductWaitListDb)objList.get(0));
		}

		return obj;
    }

    private static ProductWaitList
    getProductWaitListEntry(ProductWaitListDb _obj)
		throws TorqueException {
		Integer key = new Integer(_obj.getProductWaitListDbId());
		ProductWaitList obj = (ProductWaitList)hash.get(key);
		if (obj == null) {
			obj = new ProductWaitList(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static ProductWaitList
    addProductWaitListEntry(UKOnlinePersonBean _person, CheckoutCodeBean _product, UKOnlinePersonBean _mod_person) throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		ProductWaitList obj = null;
		Criteria crit = new Criteria();
		crit.add(ProductWaitListDbPeer.WAITING_PERSON_ID, _person.getId());
		crit.add(ProductWaitListDbPeer.CHECKOUT_CODE_ID, _product.getId());
		crit.add(ProductWaitListDbPeer.IS_ACTIVE, (short)1 );
		List l = ProductWaitListDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			obj = new ProductWaitList();
			obj.setCreateOrModifyPerson(_mod_person);
			obj.setIsActive(true);
			obj.setProduct(_product);
			obj.setWaitingPerson(_person);
			obj.setQuantity(BigDecimal.ONE);
			obj.save();
		} else {
			obj = ProductWaitList.getProductWaitListEntry((ProductWaitListDb)l.get(0));
			obj.setQuantity(obj.getQuantity().add(BigDecimal.ONE));
			obj.save();
		}
		return obj;
    }

    public static void
    removeProductWaitList(int _entry_id)
		throws TorqueException {
		
		// remove from the hash
		
		hash.remove(_entry_id);
		
		// remove from the database

		Criteria crit = new Criteria();
		crit.add(ProductWaitListDbPeer.PRODUCT_WAIT_LIST_DB_ID, _entry_id);
		ProductWaitListDbPeer.doDelete(crit);
    }

    public static Vector
    getProductWaitList(UKOnlinePersonBean _person, boolean _active_only)
		throws TorqueException {
		
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(ProductWaitListDbPeer.WAITING_PERSON_ID, _person.getId());
		if (_active_only) {
			crit.add(ProductWaitListDbPeer.IS_ACTIVE, (short)1 );
		}
		crit.addAscendingOrderByColumn(ProductWaitListDbPeer.CREATION_DATE);
		Iterator itr = ProductWaitListDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(ProductWaitList.getProductWaitListEntry((ProductWaitListDb)itr.next()));
		}

		return vec;
    }

    public static Vector
    getProductWaitList(CheckoutCodeBean _product, boolean _active_only)
		throws TorqueException {
		
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(ProductWaitListDbPeer.CHECKOUT_CODE_ID, _product.getId());
		if (_active_only) {
			crit.add(ProductWaitListDbPeer.IS_ACTIVE, (short)1 );
		}
		crit.addAscendingOrderByColumn(ProductWaitListDbPeer.CREATION_DATE);
		Iterator itr = ProductWaitListDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(ProductWaitList.getProductWaitListEntry((ProductWaitListDb)itr.next()));
		}

		return vec;
    }
	

    /*
	 * 
<table name="PRODUCT_WAIT_LIST_DB">
    <column name="PRODUCT_WAIT_LIST_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="WAITING_PERSON_ID" required="true" type="INTEGER"/>
    <column name="CHECKOUT_CODE_ID" required="true" type="INTEGER"/>
    <column name="QUANTITY" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="IS_ACTIVE" required="true" type="SMALLINT" default="1" />
	
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="true" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
		<reference local="WAITING_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="CHECKOUT_CODE">
		<reference local="CHECKOUT_CODE_ID" foreign="CHECKOUT_CODE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
     */

    public static Vector
    getProductWaitList(VendorRet _vendor, boolean _active_only)
		throws TorqueException {
		
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(CheckoutCodePeer.CHECKOUT_CODE_ID, ProductWaitListDbPeer.CHECKOUT_CODE_ID);
		crit.add(CheckoutCodePeer.VENDOR_ID, _vendor.getId());
		if (_active_only) {
			crit.add(ProductWaitListDbPeer.IS_ACTIVE, (short)1 );
		}
		crit.addAscendingOrderByColumn(ProductWaitListDbPeer.CREATION_DATE);
		Iterator itr = ProductWaitListDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(ProductWaitList.getProductWaitListEntry((ProductWaitListDb)itr.next()));
		}

		return vec;
    }

	/*
	public static Vector
	getPersonsByKeyword(CompanyBean _company, String _keyword, UKOnlinePersonBean _logged_in_person, int _limit)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.add(CompanyPeer.COMPANYID, _company.getId());

		if (_keyword != null) {
			String search_string = "%" + _keyword + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.FILE_NUMBER, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b).or(crit_c));
		}
		
		crit.add(PersonPeer.ISACTIVE, (short)1);
		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		Vector people = new Vector();
		while (itr.hasNext()) {
			UKOnlinePersonBean anoka_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)itr.next());
			people.addElement(anoka_person);
		}
		return people;
	}
	*/

    public static Vector
    searchProductWaitList(CompanyBean _company, String _search_str, int _limit)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(ProductWaitListDbPeer.WAITING_PERSON_ID, PersonPeer.PERSONID);
		crit.addJoin(ProductWaitListDbPeer.CHECKOUT_CODE_ID, CheckoutCodePeer.CHECKOUT_CODE_ID);
		//crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		
		if ( (_search_str != null) && !_search_str.isEmpty() ) {
			String search_string = "%" + _search_str + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b).or(crit_c));
		}
		
		crit.add(ProductWaitListDbPeer.IS_ACTIVE, (short)1); // is this set by anything??  it should be set when the sale is done, at least
		
		crit.addAscendingOrderByColumn(ProductWaitListDbPeer.CREATION_DATE);
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		System.out.println("crit >" + crit.toString());
		
		Iterator itr = ProductWaitListDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductWaitList obj = ProductWaitList.getProductWaitListEntry((ProductWaitListDb)itr.next());
			vec.addElement(obj);
		}

		return vec;
    }

    // SQL

    /*
	 * 
<table name="PRODUCT_WAIT_LIST_DB">
    <column name="PRODUCT_WAIT_LIST_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="WAITING_PERSON_ID" required="true" type="INTEGER"/>
    <column name="CHECKOUT_CODE_ID" required="true" type="INTEGER"/>
    <column name="QUANTITY" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="IS_ACTIVE" required="true" type="SMALLINT" default="1" />
	
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="true" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
		<reference local="WAITING_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="CHECKOUT_CODE">
		<reference local="CHECKOUT_CODE_ID" foreign="CHECKOUT_CODE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
     */

    // INSTANCE VARIABLES

    private ProductWaitListDb product_wait_list;
	//private Vector members;

    // CONSTRUCTORS

    public
    ProductWaitList() {
		product_wait_list = new ProductWaitListDb();
		isNew = true;
    }

    public
    ProductWaitList(ProductWaitListDb _product_wait_list) {
		product_wait_list = _product_wait_list;
		isNew = false;
    }

    // INSTANCE METHODS

    public int
    getId() {
		return product_wait_list.getProductWaitListDbId();
    }

    public String
    getLabel() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		return CUBean.getUserDateString(product_wait_list.getCreationDate()) + " " + "(" + product_wait_list.getQuantity().setScale(0, RoundingMode.HALF_UP).toString() + ") " + UKOnlinePersonBean.getPerson(product_wait_list.getWaitingPersonId()).getLabel();
    }

    public String
    getLabelAlt() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		String waitingPerson = UKOnlinePersonBean.getPerson(product_wait_list.getWaitingPersonId()).getLabel();
		String waitingProduct = this.getProduct().getLabel();
		return waitingProduct + " [" + product_wait_list.getQuantity().setScale(0, RoundingMode.HALF_UP).toString() + " - " + waitingPerson + "]";
		//return CUBean.getUserDateString(product_wait_list.getCreationDate()) + " " + "(" + product_wait_list.getQuantity().setScale(0, RoundingMode.HALF_UP).toString() + ") " + UKOnlinePersonBean.getPerson(product_wait_list.getWaitingPersonId()).getLabel();
    }

    public String
    getValue() {
		return product_wait_list.getProductWaitListDbId() + "";
    }

	@Override
    protected void
    insertObject()
		throws Exception {
		product_wait_list.setCreationDate(new Date());
		product_wait_list.save();
    }

	public boolean
	isActive() {
		return (product_wait_list.getIsActive() == (short)1);
	}

    public BigDecimal
    getQuantity() {
		return product_wait_list.getQuantity();
    }

    public CheckoutCodeBean
    getProduct() throws TorqueException, ObjectNotFoundException {
		return CheckoutCodeBean.getCheckoutCode(product_wait_list.getCheckoutCodeId());
    }

    public UKOnlinePersonBean
    getWaitingPerson() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(product_wait_list.getWaitingPersonId());
    }

    public void
    setProduct(CheckoutCodeBean _product)
		throws TorqueException {
		product_wait_list.setCheckoutCodeId(_product.getId());
    }

    public void
    setWaitingPerson(UKOnlinePersonBean _waiting_person)
		throws TorqueException {
		product_wait_list.setWaitingPersonId(_waiting_person.getId());
    }

    public void
    setQuantity(BigDecimal _quantity) {
		product_wait_list.setQuantity(_quantity);
    }

    public void
    setIsActive(boolean _active) {
		product_wait_list.setIsActive(_active ? (short)1 : (short)0);
    }
    
    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew()) {
			product_wait_list.setCreatePersonId(_person.getId());
		} else {
			product_wait_list.setModifyPersonId(_person.getId());
		}
    }

	@Override
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		product_wait_list.setModificationDate(new Date());
		product_wait_list.save();
    }
	
	boolean mapping_removed = false;
	private PurchaseOrderItemMapping mapping_obj;
	public boolean
	hasMapping() {
		if (mapping_obj != null) {
			return true;
		}
		
		if (mapping_removed) {
			return false;
		}
		
		try {
			mapping_obj = this.getMapping();
		} catch (Exception x) { }
		return mapping_obj != null;
	}
	
	public PurchaseOrderItemMapping
	getMapping() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		if (mapping_obj != null) {
			return mapping_obj;
		}
		
		Criteria crit = new Criteria();
		crit.add(PurchaseOrderItemMappingPeer.PRODUCT_WAIT_LIST_DB_ID, this.getId());
		List l = PurchaseOrderItemMappingPeer.doSelect(crit);
		if (l.size() == 1) {
			return (PurchaseOrderItemMapping)l.get(0);
		} else {
			throw new ObjectNotFoundException("Unable to find purchase order mapping for wait list item: " + this.getLabel());
		}
	}
	
	public void
	removeMapping() {
		mapping_obj = null;
		mapping_removed = true;
		
		/*
		try {
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		*/
	}
	
	public void
	addMapping(PurchaseOrderItemMapping _mapping) {
		mapping_obj = _mapping;
		mapping_removed = false;
		
		/*
		try {
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		*/
	}
	
}