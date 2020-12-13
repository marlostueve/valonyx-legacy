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
import java.math.RoundingMode;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
Product
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Long,Product> hash = new HashMap<Long,Product>(11);

    // CLASS METHODS

    public static Product
    getProduct(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		Product product_obj = (Product)hash.get(_id);
		if (product_obj == null) {
			Criteria crit = new Criteria();
			crit.add(ProductDbPeer.PRODUCT_DB_ID, _id);
			List objList = ProductDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate product with id: " + _id);
			}

			product_obj = Product.getProduct((ProductDb)objList.get(0));
		}

		return product_obj;
    }

    public static Product
    getProduct(String _upc)
		throws TorqueException, ObjectNotFoundException {
		
		// to-do: add caching and/or database index for this
		// to-do: perhaps take a company as an argument
		
		Criteria crit = new Criteria();
		crit.add(ProductDbPeer.U_U_I_D, _upc);
		List objList = ProductDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			throw new ObjectNotFoundException("Could not locate product with UPC: " + _upc);
		} else if (objList.size() > 1) {
			throw new ObjectNotFoundException("Could not locate unqiue product with UPC: " + _upc);
		}

		return Product.getProduct((ProductDb)objList.get(0));
    }

    private static Product
    getProduct(ProductDb _obj) throws TorqueException {
		
		Product product_obj = (Product)hash.get(_obj.getProductDbId());
		if (product_obj == null) {
			product_obj = new Product(_obj);
			hash.put(_obj.getProductDbId(), product_obj);
		}

		return product_obj;
    }

    public static Vector
    getProducts(CompanyBean _manfacturer)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ProductDbPeer.MFG_ID, _manfacturer.getId());
		Iterator itr = ProductDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductDb obj = (ProductDb)itr.next();
			vec.addElement(Product.getProduct(obj));
		}
		return vec;
    }

	public static Vector
	getProducts(CompanyBean _manfacturer, String _keyword, int _limit, short _status)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {

		Criteria crit = new Criteria();
		
		if (_manfacturer == null) {
			crit.addJoin(ProductDbPeer.MFG_ID, CompanyPeer.COMPANYID);
		} else {
			crit.add(ProductDbPeer.MFG_ID, _manfacturer.getId());
		}

		if (_keyword != null) {
			String search_string = "%" + _keyword + "%";
			
			crit.addJoin(ProductDbPeer.PRODUCT_DB_ID, ProductInteractionDbPeer.PRODUCT_DB_ID);
			
			Criteria.Criterion crit_a = crit.getNewCriterion(ProductDbPeer.NAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(ProductDbPeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
			//Criteria.Criterion crit_c = crit.getNewCriterion(ProductDbPeer.MODEL_NUMBER, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(ProductInteractionDbPeer.UNIQUE_ID, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_e = crit.getNewCriterion(ProductDbPeer.U_U_I_D, (Object)search_string, Criteria.LIKE);
			if (_manfacturer == null) {
				Criteria.Criterion crit_d = crit.getNewCriterion(CompanyPeer.COMPANYNAME, (Object)search_string, Criteria.LIKE);
				crit.add(crit_a.or(crit_b).or(crit_c).or(crit_d).or(crit_e));
			} else {
				crit.add(crit_a.or(crit_b).or(crit_c).or(crit_e));
			}
		}
		
		if (_status == (short)1) {
			crit.add(ProductDbPeer.IS_ACTIVE, (short)1);
		} else if (_status == (short)2) {
			crit.add(ProductDbPeer.IS_ACTIVE, (short)0);
		}
		
		crit.addAscendingOrderByColumn(ProductDbPeer.NAME);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		crit.setDistinct();

		System.out.println("getProductsCrit >" + crit.toString());
		Iterator itr = ProductDbPeer.doSelect(crit).iterator();
		Vector vec = new Vector();
		while (itr.hasNext()) {
			vec.addElement(Product.getProduct((ProductDb)itr.next()));
		}
		return vec;
	}

	public static Vector
	getProducts(CompanyBean _retailer, DepartmentBean _store, CompanyBean _manfacturer, String _keyword, int _limit, short _status)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {

		Criteria crit = new Criteria();
		
		crit.addJoin(ProductDbPeer.PRODUCT_DB_ID, ProductInteractionDbPeer.PRODUCT_DB_ID);
		
		if (_store != null) {
			crit.add(ProductInteractionDbPeer.STORE_ID, _store.getId());
		} else if (_retailer != null) {
			crit.add(ProductInteractionDbPeer.RETAILER_ID, _retailer.getId());
		}
		
		if (_manfacturer == null) {
			crit.addJoin(ProductDbPeer.MFG_ID, CompanyPeer.COMPANYID);
		} else {
			crit.add(ProductDbPeer.MFG_ID, _manfacturer.getId());
		}

		if (_keyword != null) {
			String search_string = "%" + _keyword + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(ProductDbPeer.NAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(ProductDbPeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(ProductDbPeer.MODEL_NUMBER, (Object)search_string, Criteria.LIKE);
			if (_manfacturer == null) {
				Criteria.Criterion crit_d = crit.getNewCriterion(CompanyPeer.COMPANYNAME, (Object)search_string, Criteria.LIKE);
				crit.add(crit_a.or(crit_b).or(crit_c).or(crit_d));
			} else {
				crit.add(crit_a.or(crit_b).or(crit_c));
			}
		}
		
		if (_status == (short)1) {
			crit.add(ProductDbPeer.IS_ACTIVE, (short)1);
		} else if (_status == (short)2) {
			crit.add(ProductDbPeer.IS_ACTIVE, (short)0);
		}
		
		crit.addAscendingOrderByColumn(ProductDbPeer.NAME);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		crit.setDistinct();

		System.out.println("getProductsCrit >" + crit.toString());
		Iterator itr = ProductDbPeer.doSelect(crit).iterator();
		Vector vec = new Vector();
		while (itr.hasNext()) {
			vec.addElement(Product.getProduct((ProductDb)itr.next()));
		}
		return vec;
	}

	public static long
	calculateShelfSitTimeMillis(CompanyBean _retailer, DepartmentBean _store, Product _product, String _unique_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {

		Criteria crit = new Criteria();
		
		crit.addJoin(ProductDbPeer.PRODUCT_DB_ID, ProductInteractionDbPeer.PRODUCT_DB_ID);
		
		if (_store != null) {
			crit.add(ProductInteractionDbPeer.STORE_ID, _store.getId());
		} else if (_retailer != null) {
			crit.add(ProductInteractionDbPeer.RETAILER_ID, _retailer.getId());
		}

		crit.add(ProductDbPeer.PRODUCT_DB_ID, _product.getId());
		if (_unique_id != null) {
			crit.add(ProductInteractionDbPeer.UNIQUE_ID, _unique_id);
		}
		
		crit.addAscendingOrderByColumn(ProductDbPeer.NAME);
		
		/*
		public static final short ASN_TYPE = 7;

		public static final short INVENTORY_TYPE = 1;
		public static final short SALE_TYPE = 2;
		public static final short SALE_ACTIVATED_TYPE = 3;
		public static final short RETURN_TYPE = 4;
		public static final short DEACTIVATED_OTHER_TYPE = 5;

		public static final short DEACTIVATED_CHARGEBACK_TYPE = 6;
		*/

		long last_entering_inventory_timestamp = 0l;
		long total_shelf_time = 0l;
		
		System.out.println("calculateShelfSitTimeMillis >" + crit.toString());
		Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductInteractionDb interaction_obj = (ProductInteractionDb)itr.next();
			long this_stamp = interaction_obj.getInteractionDate().getTime();
			if (interaction_obj.getInteractionType() == ProductInteraction.INVENTORY_TYPE) {
				// start time
				last_entering_inventory_timestamp = interaction_obj.getInteractionDate().getTime();
			} else if (last_entering_inventory_timestamp > 0l) {
				total_shelf_time += (this_stamp - last_entering_inventory_timestamp);
				last_entering_inventory_timestamp = 0l;
			}
		}
		if (last_entering_inventory_timestamp > 0l && total_shelf_time == 0l) {
			Date now = new Date();
			total_shelf_time = now.getTime() - last_entering_inventory_timestamp;
		}
		
		return total_shelf_time;
	}

    // SQL

    /*
		<table name="PRODUCT_DB" idMethod="native">
			<column name="PRODUCT_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
			<column name="NAME" size="200" type="VARCHAR"/>
			<column name="DESCRIPTION" type="LONGVARCHAR"/>
			<column name="MODEL_NUMBER" size="30" type="VARCHAR"/>
			<column name="MFG_ID" required="true" type="INTEGER"/>

			<column name="CREATION_DATE" required="true" type="DATE"/>
			<column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>

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

    // INSTANCE VARIABLES

    private ProductDb product;
	private Vector changeLog;
	private Vector interactions;
	private Vector images;

    // CONSTRUCTORS

    public
    Product() {
		product = new ProductDb();
		isNew = true;
    }

    public
    Product(ProductDb _obj) {
		product = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	

	public String getNameString() {
		String str = product.getName();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setName(String _name) {
		product.setName(_name);
	}

	public String getDescription() {
		String str = product.getDescription();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setDescription(String _description) {
		product.setDescription(_description);
	}

	public String getModelNumber() {
		String str = product.getModelNumber();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setModelNumber(String _modelNumber) {
		product.setModelNumber(_modelNumber);
	}

	public String getUPC() {
		String str = product.getUUID();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setUPC(String _upc) {
		product.setUUID(_upc);
	}

	public String getEAN() {
		String str = product.getEan();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setEAN(String _ean) {
		product.setEan(_ean);
	}

	public Date getCreationDate() {
		return product.getCreationDate();
	}

	public void setCreationDate(Date _creationDate) {
		product.setCreationDate(_creationDate);
	}
	

	public CompanyBean
	getManufacturer()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return CompanyBean.getCompany(product.getMfgId());
	}

	public void
	setManufacturer(CompanyBean _manufacturer)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		product.setMfgId(_manufacturer.getId());
	}

    public long
    getId() {
		return product.getProductDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		String str = product.getName();
		if (str == null) {
			return "";
		}
		return str;
    }

    public String
    getValue() {
		return product.getProductDbId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		product.save();
		this.saveInteractions();
		this.saveImages();
    }
	
	public boolean
	isActive() {
		return (product.getIsActive() == (short)1);
	}

	public void
	activate()
		throws TorqueException {
		product.setIsActive((short)1);
	}

	public void
	inActivate()
		throws TorqueException {
		product.setIsActive((short)0);
	}

	public void
	setActive(boolean _b)
		throws TorqueException {
		product.setIsActive(_b ? (short)1 : (short)0);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		product.save();
		
		this.saveInteractions(); // any reason why I wasn't saving interactions here???
		this.saveImages();
    }
	
	public void setCreatePerson(PersonBean _person) throws TorqueException {
		product.setCreatePersonId(_person.getId());
	}
	
	/*
	<table name="PRODUCT_IMAGE_MAP">
	<column name="PRODUCT_DB_ID" required="true" primaryKey="true" type="BIGINT"/>
	<column name="IMAGE_U_R_L" size="250" type="VARCHAR" primaryKey="true" />

    <foreign-key foreignTable="PRODUCT_DB">
		<reference local="PRODUCT_DB_ID" foreign="PRODUCT_DB_ID"/>
    </foreign-key>
</table>
	*/
	
	public String
	getImageURLString() throws TorqueException, ObjectNotFoundException {
		if (images == null) {
			this.getImages();
		}
		if (!images.isEmpty()) {
			ProductImageMap obj = (ProductImageMap)images.get(0);
			return obj.getImageURL();
		}
		return "";
	}
	
	public Vector
	getImages() throws TorqueException, ObjectNotFoundException {
		if (images == null) {
			images = new Vector();
			Criteria crit = new Criteria();
			crit.add(ProductImageMapPeer.PRODUCT_DB_ID, this.getId());
			Iterator itr = ProductImageMapPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				ProductImageMap obj = (ProductImageMap)itr.next();
				images.addElement(obj);
			}
		}
		return images;
	}
	
	public void
	setImages(Vector _v) {
		images = _v;
	}

	private void
    saveImages() throws TorqueException, Exception {
		if (this.images != null && (this.images.size() > 0) ) {
			System.out.println("sizer >" + this.images.size());
			
			// delete all current images for this product
			
			Criteria crit = new Criteria();
			crit.add(ProductImageMapPeer.PRODUCT_DB_ID, this.getId());
			ProductImageMapPeer.doDelete(crit);
			
			Iterator itr = this.images.iterator();
			while (itr.hasNext()) {
				String image_url_str = (String)itr.next();
				
				ProductImageMap image_map = new ProductImageMap();
				image_map.setProductDbId(this.getId());
				image_map.setImageURL(image_url_str);
				image_map.save();
			}

		}
    }
	

	public Vector
	getInteractions() throws TorqueException, ObjectNotFoundException {
		if (interactions == null) {
			interactions = new Vector();
			Criteria crit = new Criteria();
			crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, this.getId());
			crit.addAscendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE);
			Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				ProductInteractionDb obj = (ProductInteractionDb)itr.next();
				interactions.addElement(ProductInteraction.getInteraction(obj));
			}
		}
		return interactions;
	}

	public void
	setInteractions(Vector _interactions) {
		interactions = _interactions;
	}

	private void
    saveInteractions() throws TorqueException, Exception {
		if (this.interactions != null) {
			System.out.println("sizer >" + this.interactions.size());

			HashMap<Long,ProductInteractionDb> db_interaction_hash = new HashMap<Long,ProductInteractionDb>(3);
			Criteria crit = new Criteria();
			crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, this.getId());
			Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				System.out.println("found existing ass...");
				ProductInteractionDb value = (ProductInteractionDb)itr.next();
				Long key = new Long(value.getProductInteractionDbId());
				db_interaction_hash.put(key, value);
			}

			System.out.println("num interactions >" + this.interactions.size());
			itr = this.interactions.iterator();
			while (itr.hasNext()) {
				ProductInteraction interaction_obj = (ProductInteraction)itr.next();
				interaction_obj.setProduct(this); // just to be sure
				interaction_obj.save(); // in case this isn't being saved elsewhere, I guess
				Long key = new Long(interaction_obj.getId());
				ProductInteractionDb obj = (ProductInteractionDb)db_interaction_hash.remove(key);
				if (obj == null) { // interaction not already in database
					/*
					obj = new ProductInteractionDb();
					obj.setProd.setSubscriptionInfoDbId(this.getId());
					obj.setOrderId(key.intValue());
					obj.save();
							*/
				}
			}

			itr = db_interaction_hash.keySet().iterator();
			while (itr.hasNext()) {
				Long key = (Long)itr.next();
				ProductInteractionDb obj = (ProductInteractionDb)db_interaction_hash.get(key);
				ProductInteractionDbPeer.doDelete(obj);
			}
		}
    }
	
	
	private HashMap interactions_hash = new HashMap(11);
	
	
	private BigDecimal numInventoryPercentage = BigDecimal.ZERO;
	private BigDecimal numSoldNotActivatedPercentage = BigDecimal.ZERO;
	private BigDecimal numSoldAndActivatedPercentage = BigDecimal.ZERO;
	private BigDecimal numReturnedDeactivated = BigDecimal.ZERO;
	private BigDecimal numDeactivatedOther = BigDecimal.ZERO;
	
	private int num_inventory = 0;
	private int num_sold_not_activated = 0;
	private int num_sold_and_activated = 0;
	private int num_returned_deactivated = 0;
	private int num_deactivated_other = 0;
	
	public void
	calculateInteractionPercentages(CompanyBean _retailer, DepartmentBean _store, Date _start, Date _end) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		
		List interactions = null;
		
		if (_store != null) {
			interactions = (List)interactions_hash.get(_store);
		} else if (_retailer != null) {
			interactions = (List)interactions_hash.get(_retailer);
		} else {
			throw new IllegalValueException("Please specify a retailer or a store.");
		}
		
		if (interactions == null) {
			interactions = this.getInteractions(_retailer, _store);
			if (_store != null) {
				interactions_hash.put(_store, interactions);
			} else if (_retailer != null) {
				interactions_hash.put(_retailer, interactions);
			}
		}

		Iterator itr = interactions.iterator();
		while (itr.hasNext()) {
			ProductInteraction interaction_obj = (ProductInteraction)itr.next();
			Date interaction_date = interaction_obj.getInteractionDate();
			boolean include = true;
			if (_start != null) {
				if (interaction_date.before(_start)) {
					include = false;
				}
			}
			if (_end != null) {
				if (interaction_date.after(_end)) {
					include = false;
				}
			}
			if (include) {
				switch (interaction_obj.getInteractionType()) {
					case (short)1: num_inventory++; break;
					case (short)2: num_sold_not_activated++; break;
					case (short)3: num_sold_and_activated++; break;
					case (short)4: num_returned_deactivated++; break;
					case (short)5: num_deactivated_other++; break;
				}
			}
		}
		
		BigDecimal num_inventory_bd = new BigDecimal(num_inventory);
		BigDecimal num_sold_not_activated_bd = new BigDecimal(num_sold_not_activated);
		BigDecimal num_sold_and_activated_bd = new BigDecimal(num_sold_and_activated);
		BigDecimal num_returned_deactivated_bd = new BigDecimal(num_returned_deactivated);
		BigDecimal num_deactivated_other_bd = new BigDecimal(num_deactivated_other);
		
		int num_total = num_inventory + num_sold_not_activated + num_sold_and_activated + num_returned_deactivated + num_deactivated_other; // must be mutually exclusive
		BigDecimal num_total_bd = new BigDecimal(num_total);
		BigDecimal one_hundred = new BigDecimal(100);
		
		if (!interactions.isEmpty()) {
			if (num_total > 0) {
				numInventoryPercentage = num_inventory_bd.multiply(one_hundred).divide(num_total_bd, RoundingMode.HALF_UP);
				numSoldNotActivatedPercentage = num_sold_not_activated_bd.multiply(one_hundred).divide(num_total_bd, RoundingMode.HALF_UP);
				numSoldAndActivatedPercentage = num_sold_and_activated_bd.multiply(one_hundred).divide(num_total_bd, RoundingMode.HALF_UP);
				numReturnedDeactivated = num_returned_deactivated_bd.multiply(one_hundred).divide(num_total_bd, RoundingMode.HALF_UP);
				numDeactivatedOther = num_deactivated_other_bd.multiply(one_hundred).divide(num_total_bd, RoundingMode.HALF_UP);
			}
		}
		
		/*
		Random randy = new Random();
		
		num_inventory = Math.abs(randy.nextInt() % 50);
		num_sold_not_activated = Math.abs(randy.nextInt() % 30);
		num_sold_and_activated = Math.abs(randy.nextInt() % 100);
		num_returned_deactivated = Math.abs(randy.nextInt() % 10);
		num_deactivated_other = Math.abs(randy.nextInt() % 5);
		*/
	}
	
	public Vector
	getInteractions(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		
		if (_store != null) {
			crit.addJoin(DepartmentPeer.DEPARTMENTID, ProductInteractionDbPeer.STORE_ID);
			crit.add(ProductInteractionDbPeer.STORE_ID, _store.getId());
		} else if (_retailer != null) { // store already specifies retailer
			crit.addJoin(CompanyPeer.COMPANYID, ProductInteractionDbPeer.RETAILER_ID);
			crit.add(ProductInteractionDbPeer.RETAILER_ID, _retailer.getId());
		}
		
		crit.add(ProductInteractionDbPeer.PRODUCT_DB_ID, this.getId());
		crit.addAscendingOrderByColumn(ProductInteractionDbPeer.INTERACTION_DATE); // may not be needed here
		Iterator itr = ProductInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ProductInteractionDb obj = (ProductInteractionDb)itr.next();
			vec.addElement(ProductInteraction.getInteraction(obj));
		}
		
		return vec;
	}
	
	private boolean
	hasBeenCalculated(CompanyBean _retailer, DepartmentBean _store) throws IllegalValueException {
		
		if (_store != null) {
			return interactions_hash.containsKey(_store);
		} else if (_retailer != null) {
			return interactions_hash.containsKey(_store);
		}
		
		throw new IllegalValueException("Please specify a retailer or a store.");
	}
	
	public BigDecimal
	getPercentInventory(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		return numInventoryPercentage;
	}
	
	public BigDecimal
	getPercentSold(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		/*
		if (!this.hasBeenCalculated(_retailer, _store)) {
			this.calculateInteractionPercentages(_retailer, _store);
		}
		*/
		return numSoldNotActivatedPercentage;
	}
	
	public BigDecimal
	getPercentActivated(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		return numSoldAndActivatedPercentage;
	}
	
	public BigDecimal
	getPercentReturned(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		return numReturnedDeactivated;
	}
	
	public BigDecimal
	getPercentDeactivatedOther(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		return numDeactivatedOther;
	}
	
	
	public int
	getNumInventory(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		return num_inventory;
	}
	
	public int
	getNumSold(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		return num_sold_not_activated;
	}
	
	public int
	getNumActivated(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		return num_sold_and_activated;
	}
	
	public int
	getNumReturned(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		return num_returned_deactivated;
	}
	
	public int
	getNumDeactivatedOther(CompanyBean _retailer, DepartmentBean _store) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException {
		return num_deactivated_other;
	}
	
	
}