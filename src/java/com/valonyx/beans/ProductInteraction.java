/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import com.valeo.qbpos.data.TenderRet;

import java.math.BigDecimal;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
ProductInteraction
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
    public static final short ASN_TYPE = 7;
	
	public static final short INVENTORY_TYPE = 1;
    public static final short SALE_TYPE = 2;
    public static final short SALE_ACTIVATED_TYPE = 3;
    public static final short RETURN_TYPE = 4;
    public static final short DEACTIVATED_OTHER_TYPE = 5;
	
    public static final short DEACTIVATED_CHARGEBACK_TYPE = 6;

    protected static HashMap<Long,ProductInteraction> hash = new HashMap<Long,ProductInteraction>(11);

    // CLASS METHODS

    public static ProductInteraction
    getInteraction(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		ProductInteraction interaction_obj = (ProductInteraction)hash.get(_id);
		if (interaction_obj == null) {
			Criteria crit = new Criteria();
			crit.add(ProductInteractionDbPeer.PRODUCT_INTERACTION_DB_ID, _id);
			List objList = ProductInteractionDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate interaction with id: " + _id);
			}

			interaction_obj = ProductInteraction.getInteraction((ProductInteractionDb)objList.get(0));
		}

		return interaction_obj;
    }

    public static ProductInteraction
    getInteraction(ProductInteractionDb _obj) throws TorqueException {
		
		ProductInteraction interaction_obj = (ProductInteraction)hash.get(_obj.getProductInteractionDbId());
		if (interaction_obj == null) {
			interaction_obj = new ProductInteraction(_obj);
			hash.put(_obj.getProductInteractionDbId(), interaction_obj);
		}

		return interaction_obj;
    }

    public static ProductInteraction
    getInteraction(PersonBean _interaction_person, Product _product, String _serial_number, short _interaction_type, boolean _create)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_serial_number == null || _serial_number.isEmpty()) {
			throw new IllegalValueException("Serial Number not specified in getInteraction()");
		}
		
		ProductInteraction current_interaction_obj = null;
		
		Criteria crit = new Criteria();
		crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, _product.getId()); // I'll want to add indexing...
		crit.add(ProductInteractionDbPeer.UNIQUE_ID, _serial_number);
		crit.addDescendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE);
		System.out.println("crit >" + crit.toString());
		Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
		if (itr.hasNext()) {
			while (itr.hasNext()) {
				ProductInteraction interaction_obj = ProductInteraction.getInteraction((ProductInteractionDb)itr.next());
				if (current_interaction_obj == null) {
					current_interaction_obj = interaction_obj;
				}
			}
		}
		
		// am I allowed to create an interaction of this type??
		
		if (_create) {
			if (current_interaction_obj != null) {
				if (current_interaction_obj.getInteractionType() == ProductInteraction.SALE_ACTIVATED_TYPE) {
					if (_interaction_type == ProductInteraction.SALE_ACTIVATED_TYPE) {
						throw new IllegalValueException("Unable to activate.  This device is already activated.");
					} else if (_interaction_type == ProductInteraction.SALE_TYPE) {
						throw new IllegalValueException("Unable to create sale.  This device has already been activated.");
					}
				} else if (current_interaction_obj.getInteractionType() == ProductInteraction.INVENTORY_TYPE) {
					if (_interaction_type == ProductInteraction.INVENTORY_TYPE) {
						throw new IllegalValueException("Unable to add to inventory.  This device is already in inventory.");
					}
				} else if (current_interaction_obj.getInteractionType() == ProductInteraction.SALE_TYPE) {
					if (_interaction_type == ProductInteraction.SALE_TYPE) {
						throw new IllegalValueException("Unable to create sale.  This device has already been sold.");
					}
				} else if (current_interaction_obj.getInteractionType() == ProductInteraction.RETURN_TYPE) {
					if (_interaction_type == ProductInteraction.RETURN_TYPE) {
						throw new IllegalValueException("Unable to return.  This device has already been returned.");
					}
				}
				
				// complete whatever other error correction...
				
			}
			
			ProductInteraction interaction_obj = new ProductInteraction();
			interaction_obj.setProduct(_product);
			interaction_obj.setUniqueIdentifier(_serial_number);
			interaction_obj.setInteractionType(_interaction_type);
			interaction_obj.setInteractionPerson(_interaction_person);
			if (_interaction_person.hasDepartment()) {
				interaction_obj.setStore(_interaction_person.getDepartment());
				interaction_obj.setRetailer(_interaction_person.getDepartment().getCompany());
			}
			interaction_obj.setInteractionDate(new Date());
			interaction_obj.save();
			return interaction_obj;
				
		} else {
			if (current_interaction_obj.getInteractionType() == _interaction_type) {
				return current_interaction_obj;
			} else {
				throw new ObjectNotFoundException("Interaction not found for type >" + _interaction_type);
			}
		}
		
		
		/*
		if (l.isEmpty()) {
			if (_create) {
				interaction_obj = new ProductInteraction();
				interaction_obj.setProduct(_product);
				interaction_obj.setUniqueIdentifier(_serial_number);
				return interaction_obj;
			} else {
				throw new ObjectNotFoundException("Unable to locate product for >" + _serial_number);
			}
		} else if (l.size() == 1) {
			return ProductInteraction.getInteraction((ProductInteractionDb)l.get(0));
		} else {
			throw new UniqueObjectNotFoundException("Unable to locate unique product for >" + _serial_number);
		}
		*/
		
    }

    public static Vector
    getLatestInteractions(Product _product)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		HashMap hash = new HashMap();
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, _product.getId());
		crit.addDescendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE);
		System.out.println("getLatestInteractions crit >" + crit.toString());
		Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductInteractionDb obj = (ProductInteractionDb)itr.next();
			String serialNumber = obj.getUniqueId();
			System.out.println("serialNumber >" + serialNumber + ", >" + hash.containsKey(serialNumber));
			if (!hash.containsKey(serialNumber)) {
				ProductInteraction productInteraction_obj = ProductInteraction.getInteraction(obj.getProductInteractionDbId());
				System.out.println("adding >" + productInteraction_obj.getUniqueIdentifier());
				vec.addElement(productInteraction_obj);
				hash.put(serialNumber, ""); // is there a better way?
			}
		}
		
		return vec;
    }

    public static ProductInteraction
    getLastInteraction(Product _product, String _serialNumber) // I need to find a way to tie product AND serial number to an endpoint - or at least mfg
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		if (_product != null) { // see note above
			crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, _product.getId());
		}
		crit.add(ProductInteractionDbPeer.UNIQUE_ID, _serialNumber);
		crit.addDescendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE);
		List l = ProductInteractionDbPeer.doSelect(crit);
		if (!l.isEmpty()) {
			ProductInteractionDb obj = (ProductInteractionDb)l.get(0);
			return ProductInteraction.getInteraction(obj);
		}
	
		throw new ObjectNotFoundException("Unable to locate product interaction for Serial Number >" + _serialNumber);
    }

    public static Vector
    getInteractions(Product _product, String _serialNumber)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, _product.getId());
		crit.add(ProductInteractionDbPeer.UNIQUE_ID, _serialNumber);
		crit.addAscendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE);
		Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductInteractionDb obj = (ProductInteractionDb)itr.next();
			vec.addElement(ProductInteraction.getInteraction(obj));
		}
		
		return vec;
    }

    public static Vector
    getInteractions(Product _product)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, _product.getId());
		crit.addAscendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE);
		Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductInteractionDb obj = (ProductInteractionDb)itr.next();
			vec.addElement(ProductInteraction.getInteraction(obj));
		}
		
		return vec;
    }

    public static Vector
    getInteractions(Product _product, DepartmentBean _store)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, _product.getId());
		crit.add(ProductInteractionDbPeer.STORE_ID, _store.getId());
		crit.addAscendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE);
		Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductInteractionDb obj = (ProductInteractionDb)itr.next();
			vec.addElement(ProductInteraction.getInteraction(obj));
		}
		
		return vec;
    }

    public static Vector
    getInteractions(Product _product, CompanyBean _retailer)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, _product.getId());
		crit.add(ProductInteractionDbPeer.RETAILER_ID, _retailer.getId());
		crit.addAscendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE);
		Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductInteractionDb obj = (ProductInteractionDb)itr.next();
			vec.addElement(ProductInteraction.getInteraction(obj));
		}
		
		return vec;
    }

    public static Vector
    getInteractions(CompanyBean _retailer, DepartmentBean _store, Vector _products, String _serialNumberSearchTerm, Date _startDate, Date _endDate, short _type, int _limit)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		
		if (!_products.isEmpty()) {
			long[] arr = new long[_products.size()];
			for (int i = 0; i < _products.size(); i++) {
				Product product_obj = (Product)_products.elementAt(i);
				arr[i] = product_obj.getId();
			}
			crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, (Object)arr, Criteria.IN);
		}
		
		/*
		if ((_serialNumberSearchTerm != null) && !_serialNumberSearchTerm.isEmpty()) {
			String search_string = "%" + _serialNumberSearchTerm + "%";
			
			//Criteria.Criterion crit_a = crit.getNewCriterion(ProductInteractionDbPeer.UNIQUE_ID, (Object)search_string, Criteria.LIKE);
			//crit.add(crit_a);
			
			crit.add(ProductInteractionDbPeer.UNIQUE_ID, (Object)search_string, Criteria.LIKE);
		}
		*/
		
		if (_type > (short)0) {
			crit.add(ProductInteractionDbPeer.INTERACTION_TYPE, _type);
		}
		
		if (_store != null) {
			crit.add(ProductInteractionDbPeer.STORE_ID, _store.getId());
		} else if (_retailer != null) {
			crit.add(ProductInteractionDbPeer.RETAILER_ID, _retailer.getId());
		}
		
		
		if ((_startDate != null) && (_endDate != null)) {
			crit.add(ProductInteractionDbPeer.INTERACTION_DATE, _startDate, Criteria.GREATER_EQUAL);
			crit.and(ProductInteractionDbPeer.INTERACTION_DATE, _endDate, Criteria.LESS_EQUAL);
		} else if (_startDate != null) {
			crit.add(ProductInteractionDbPeer.INTERACTION_DATE, _startDate, Criteria.GREATER_EQUAL);
		} else if (_endDate != null) {
			crit.add(ProductInteractionDbPeer.INTERACTION_DATE, _endDate, Criteria.LESS_EQUAL);
		}
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		crit.addDescendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE);
		System.out.println("crit >" + crit.toString());
		Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductInteractionDb obj = (ProductInteractionDb)itr.next();
			vec.addElement(ProductInteraction.getInteraction(obj));
		}
		
		return vec;
    }

    public static Vector
    getLatestInteractions(CompanyBean _retailer, DepartmentBean _store, Vector _products, String _serialNumberSearchTerm, Date _startDate, Date _endDate, short _type, int _limit)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		
		if (!_products.isEmpty()) {
			long[] arr = new long[_products.size()];
			for (int i = 0; i < _products.size(); i++) {
				Product product_obj = (Product)_products.elementAt(i);
				arr[i] = product_obj.getId();
			}
			crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, (Object)arr, Criteria.IN);
		}
		
		/*
		if ((_serialNumberSearchTerm != null) && !_serialNumberSearchTerm.isEmpty()) {
			String search_string = "%" + _serialNumberSearchTerm + "%";
			
			//Criteria.Criterion crit_a = crit.getNewCriterion(ProductInteractionDbPeer.UNIQUE_ID, (Object)search_string, Criteria.LIKE);
			//crit.add(crit_a);
			
			crit.add(ProductInteractionDbPeer.UNIQUE_ID, (Object)search_string, Criteria.LIKE);
		}
		*/
		
		if (_type > (short)0) {
			crit.add(ProductInteractionDbPeer.INTERACTION_TYPE, _type);
		}
		
		if (_store != null) {
			crit.add(ProductInteractionDbPeer.STORE_ID, _store.getId());
		} else if (_retailer != null) {
			crit.add(ProductInteractionDbPeer.RETAILER_ID, _retailer.getId());
		}
		
		
		if ((_startDate != null) && (_endDate != null)) {
			crit.add(ProductInteractionDbPeer.INTERACTION_DATE, _startDate, Criteria.GREATER_EQUAL);
			crit.and(ProductInteractionDbPeer.INTERACTION_DATE, _endDate, Criteria.LESS_EQUAL);
		} else if (_startDate != null) {
			crit.add(ProductInteractionDbPeer.INTERACTION_DATE, _startDate, Criteria.GREATER_EQUAL);
		} else if (_endDate != null) {
			crit.add(ProductInteractionDbPeer.INTERACTION_DATE, _endDate, Criteria.LESS_EQUAL);
		}
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		Vector sn = new Vector();
		
		crit.addDescendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE);
		System.out.println("crit >" + crit.toString());
		Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductInteractionDb obj = (ProductInteractionDb)itr.next();
			if (!sn.contains(obj.getUniqueId())) {
				vec.addElement(ProductInteraction.getInteraction(obj));
				sn.addElement(obj.getUniqueId());
			}
		}
		
		return vec;
    }

    // SQL

    /*
		<table name="PRODUCT_INTERACTION_DB" idMethod="native">
			<column name="PRODUCT_INTERACTION_DB" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
			<column name="RETAILER_ID" required="false" type="INTEGER"/>
			<column name="STORE_ID" required="false" type="INTEGER"/>
			<column name="INTERACTION_PERSON_ID" required="false" type="INTEGER"/>
			<column name="PRODUCT_DB_ID" required="true" type="BIGINT"/>

			<column name="UNIQUE_ID" size="100" type="VARCHAR"/>
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

    // INSTANCE VARIABLES

    private ProductInteractionDb interaction;

    // CONSTRUCTORS

    public
    ProductInteraction() {
		interaction = new ProductInteractionDb();
		isNew = true;
    }

    public
    ProductInteraction(ProductInteractionDb _obj) {
		interaction = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	/*
	private CompanyBean retailer;
	private DepartmentBean store;
	private PersonBean interactionPerson;
	private Product product;
	private String uniqueIdentifier;
	private short interactionType;
	private Date interactionDate;
	*/

	public CompanyBean getRetailer() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return CompanyBean.getCompany(this.interaction.getRetailerId());
	}

	public void setRetailer(CompanyBean _retailer) throws TorqueException {
		this.interaction.setRetailerId(_retailer.getId());
	}

	public DepartmentBean getStore() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return DepartmentBean.getDepartment(this.interaction.getStoreId());
	}

	public void setStore(DepartmentBean _store) throws TorqueException {
		this.interaction.setStoreId(_store.getId());
	}

	public PersonBean getInteractionPerson() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return PersonBean.getPerson(this.interaction.getInteractionPersonId());
	}

	public void setInteractionPerson(PersonBean _interactionPerson) throws TorqueException {
		this.interaction.setInteractionPersonId(_interactionPerson.getId());
	}

	public com.valonyx.beans.Product getProduct() throws TorqueException, ObjectNotFoundException {
		return Product.getProduct(this.interaction.getProductDbId());
	}

	public void setProduct(com.valonyx.beans.Product _product) throws TorqueException {
		this.interaction.setProductDbId(_product.getId());
	}

	public String getUniqueIdentifier() {
		return this.interaction.getUniqueId();
	}

	public void setUniqueIdentifier(String _uniqueIdentifier) {
		this.interaction.setUniqueId(_uniqueIdentifier);
	}

	public short getInteractionType() {
		return this.interaction.getInteractionType();
	}

	public void setInteractionType(short _interactionType) {
		this.interaction.setInteractionType(_interactionType);
	}

	public String getInteractionTypeString() {
		switch (this.interaction.getInteractionType()) {
			case ProductInteraction.SALE_ACTIVATED_TYPE: return "Activated";
			case ProductInteraction.INVENTORY_TYPE: return "Inventory";
			case ProductInteraction.SALE_TYPE: return "Sold (Activated)";
			case ProductInteraction.RETURN_TYPE: return "Returned";
			case ProductInteraction.DEACTIVATED_OTHER_TYPE: return "Deactivated (Other)";
			case ProductInteraction.ASN_TYPE: return "Advanced Shipping Notice";
			case ProductInteraction.DEACTIVATED_CHARGEBACK_TYPE: return "Deactivated (Chargeback)";
		}
		return "[NOT FOUND]";
	}
	
	public boolean isActivated() {
		return this.interaction.getInteractionType() == ProductInteraction.SALE_ACTIVATED_TYPE;
	}
	
	public boolean isInventory() {
		return this.interaction.getInteractionType() == ProductInteraction.INVENTORY_TYPE;
	}
	
	public boolean isSoldNotActivated() {
		return this.interaction.getInteractionType() == ProductInteraction.SALE_TYPE;
	}
	
	public boolean isReturn() {
		return this.interaction.getInteractionType() == ProductInteraction.RETURN_TYPE;
	}
	
	public boolean isDeactivatedChargeback() {
		return this.interaction.getInteractionType() == ProductInteraction.DEACTIVATED_CHARGEBACK_TYPE;
	}
	
	public boolean isDeactivatedOther() {
		return this.interaction.getInteractionType() == ProductInteraction.DEACTIVATED_OTHER_TYPE;
	}
	
	public boolean isASN() {
		return this.interaction.getInteractionType() == ProductInteraction.ASN_TYPE;
	}

	public Date getInteractionDate() {
		return this.interaction.getInteractionDate();
	}

	public void setInteractionDate(Date _interactionDate) {
		this.interaction.setInteractionDate(_interactionDate);
	}
    
    public long getInteractionDateUnixTimestamp() {
		return this.interaction.getInteractionDate().getTime() / 1000;
    }

    public long
    getId() {
		return interaction.getProductInteractionDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		if (this.isNew) {
			return "[NEW PRODUCT INTERACTION]";
		} else {
			return this.getProduct().getLabel() + " - " + this.getUniqueIdentifier() + " [" + this.getInteractionTypeString() + "]";
		}
    }

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject() throws Exception {
		interaction.save();
    }

    protected void
    updateObject() throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		interaction.save();
    }
	
	
}