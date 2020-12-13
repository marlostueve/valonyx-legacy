/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.fitness.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;



/**
 *
 * @author marlo
 */
public class
Food
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
	public static final short GRAMS_SERVING_SIZE_TYPE = 1;
	public static final short OZ_SERVING_SIZE_TYPE = 2;
	public static final short ML_SERVING_SIZE_TYPE = 3;
	
	public static final MathContext return_context = new MathContext(2);
	public static final MathContext conversion_context = new MathContext(5);
	public static final BigDecimal grams_per_oz = new BigDecimal("28.3495", conversion_context);
	public static final BigDecimal oz_per_gram = new BigDecimal("0.035274", conversion_context);
	
	// 0.035274

    protected static HashMap<Long,Food> hash = new HashMap<Long,Food>();

    // CLASS METHODS

    public static Food
    getFood(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		Food food_obj = (Food)hash.get(_id);
		if (food_obj == null) {
			Criteria crit = new Criteria();
			crit.add(FoodDbPeer.FOOD_DB_ID, _id);
			List objList = FoodDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate food with id: " + _id);
			}

			food_obj = Food.getFood((FoodDb)objList.get(0));
		}

		return food_obj;
    }

    private static Food
    getFood(FoodDb _obj) throws TorqueException {
		
		Food food_obj = (Food)hash.get(_obj.getFoodDbId());
		if (food_obj == null) {
			food_obj = new Food(_obj);
			hash.put(_obj.getFoodDbId(), food_obj);
		}

		return food_obj;
    }

    public static Food
    getFood(String _name, BigDecimal _servingSize, short _type, BigDecimal _fat, BigDecimal _carbs, BigDecimal _protein)
		throws TorqueException, ObjectNotFoundException {
		
		Criteria crit = new Criteria();
		crit.add(FoodDbPeer.NAME, _name);
		crit.add(FoodDbPeer.SERVING_SIZE, _servingSize);
		crit.add(FoodDbPeer.SERVING_SIZE_TYPE, _type);
		crit.add(FoodDbPeer.FAT, _fat);
		crit.add(FoodDbPeer.CARBS, _carbs);
		crit.add(FoodDbPeer.PROTEIN, _protein);
		List objList = FoodDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			throw new ObjectNotFoundException("Could not locate food with name: " + _name);
		}

		return Food.getFood((FoodDb)objList.get(0));
    }

    public static Food
    getFoodByUSDANDBNum(String _ndbno)
		throws TorqueException, ObjectNotFoundException {
		
		Criteria crit = new Criteria();
		crit.add(FoodDbPeer.N_DB_NO, _ndbno);
		List objList = FoodDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			throw new ObjectNotFoundException("Could not locate food with ndbno: " + _ndbno);
		}

		return Food.getFood((FoodDb)objList.get(0));
    }

    public static Vector
    getFoodsThatNeedUSDASyncing(int _num)
		throws TorqueException, ObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(FoodDbPeer.N_DB_NO, (Object)"", Criteria.ISNOTNULL);
		crit.add(FoodDbPeer.CATEGORY, (Object)"", Criteria.ISNULL);
		crit.setLimit(_num);
		Iterator itr = FoodDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(Food.getFood((FoodDb)itr.next()));
		}

		return vec;
    }
	
	public static long
	getStartOffset() throws TorqueException, IllegalValueException {
		Criteria crit = new Criteria();
		crit.addDescendingOrderByColumn(FoodDbPeer.OFFSET);
		crit.setLimit(1);
		List l = FoodDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			return 0l;
		} else {
			FoodDb obj = (FoodDb)l.get(0);
			return obj.getOffset();
		}
	}

    public static Vector<Food>
    searchFood(UKOnlinePersonBean _person, String _search_str, int _limit)
		throws TorqueException
    {
		Vector<Food> vec = new Vector<Food>();

		Criteria crit = new Criteria();
		crit.addJoin(FavoriteFoodPeer.FOOD_DB_ID, FoodDbPeer.FOOD_DB_ID);
		crit.add(FavoriteFoodPeer.PERSON_ID, _person.getId());
		
		if ( (_search_str != null) && !_search_str.isEmpty() ) {
			String search_string = "%" + _search_str + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(FoodDbPeer.NAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(FoodDbPeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
			//Criteria.Criterion crit_c = crit.getNewCriterion(FoodDbPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b));
		}
		
		crit.add(FoodDbPeer.IS_ACTIVE, (short)1);
		crit.addAscendingOrderByColumn(FavoriteFoodPeer.FAVORITE_DATE);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		//System.out.println("crit >" + crit.toString());
		
		Iterator itr = FoodDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			Food obj = Food.getFood((FoodDb)itr.next());
			vec.addElement(obj);
		}

		return vec;
    }

    public static Vector
    getFavorites(UKOnlinePersonBean _person)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(FavoriteFoodPeer.FOOD_DB_ID, FoodDbPeer.FOOD_DB_ID);
		//crit.addJoin(FoodDbPeer.FOOD_DB_ID, MacroGoalEntryDbPeer.FOOD_DB_ID);
		crit.add(FavoriteFoodPeer.PERSON_ID, _person.getId());
		crit.add(FoodDbPeer.IS_ACTIVE, (short)1);
		crit.addAscendingOrderByColumn(FoodDbPeer.NAME);
		
		System.out.println("getFavorites() crit >" + crit.toString());
		
		// any way to return results sorted by time of day???
		
		Iterator itr = FoodDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			Food obj = Food.getFood((FoodDb)itr.next());
			vec.addElement(obj);
		}

		return vec;
    }
	
	
	public static Vector
    getFavoritesByTimeOfDay(UKOnlinePersonBean _person)
		throws TorqueException {
		
		Calendar now = Calendar.getInstance();
		int hour_of_day = now.get(Calendar.HOUR_OF_DAY);
		
		now.add(Calendar.MONTH, -1);
		
		//String qs = "SELECT CHECKOUT_CODE_ID, COUNT('CHECKOUT_CODE_ID') AS value_occurrence FROM CHECKOUT_ORDERLINE GROUP BY CHECKOUT_CODE_ID ORDER BY value_occurrence DESC";
		StringBuilder qs = new StringBuilder();
		qs.append("SELECT MACRO_GOAL_ENTRY_DB.FOOD_DB_ID, count(*) AS value_occurrence");
		qs.append(" FROM FAVORITE_FOOD, FOOD_DB, MACRO_GOAL_ENTRY_DB");
		qs.append(" WHERE FAVORITE_FOOD.FOOD_DB_ID=FOOD_DB.FOOD_DB_ID");
		qs.append(" AND FOOD_DB.FOOD_DB_ID=MACRO_GOAL_ENTRY_DB.FOOD_DB_ID");
		qs.append(" AND FAVORITE_FOOD.PERSON_ID=");
		qs.append(_person.getId());
		qs.append(" AND FOOD_DB.IS_ACTIVE=1");
		qs.append(" AND ENTRY_DATE > '" + now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE) + "'");
		qs.append(" AND HOUR( ENTRY_DATE ) BETWEEN ");
		qs.append(hour_of_day - 1);
		qs.append(" AND ");
		qs.append(hour_of_day + 1);
		qs.append(" GROUP BY MACRO_GOAL_ENTRY_DB.FOOD_DB_ID");
		qs.append(" ORDER BY value_occurrence desc");

		System.out.println("qs >" + qs.toString());

		Vector vec = new Vector();
		
		//Iterator itr = CheckoutOrderlinePeer.executeQuery(qs.toString()).iterator();
		try {
			Iterator itr = FoodDbPeer.executeQuery(qs.toString()).iterator();
			while (itr.hasNext()) {
				long food_db_id = (((com.workingdogs.village.Record)itr.next()).getValue(1)).asLong();
				Food obj = Food.getFood(food_db_id);
				vec.addElement(obj);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		return vec;
	}
	

    public static Vector
    searchPublicFood(UKOnlinePersonBean _person, String _search_str, int _limit)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		
		if ( (_search_str != null) && !_search_str.isEmpty() ) {
			
			String[] parts = _search_str.split(" ");
			Criteria.Criterion crit_search = null;
			for (int i = 0; i < parts.length; i++) {
			
				//String search_string = "%" + _search_str + "%";
				String search_string = "%" + parts[i] + "%";
				System.out.println("found part >" + parts[i]);
				Criteria.Criterion crit_a = crit.getNewCriterion(FoodDbPeer.NAME, (Object)search_string, Criteria.LIKE);
				//Criteria.Criterion crit_b = crit.getNewCriterion(FoodDbPeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
				//Criteria.Criterion crit_c = crit.getNewCriterion(FoodDbPeer.CATEGORY, (Object)search_string, Criteria.LIKE);
				Criteria.Criterion crit_d = crit.getNewCriterion(FoodDbPeer.MANUFACTURER, (Object)search_string, Criteria.LIKE);
				
				if (crit_search == null) {
					crit_search = crit_a.or(crit_d);
				} else {
					crit_search = crit_search.and(crit_a.or(crit_d));
				}
			}
			crit.add(crit_search);
		}
		
		crit.add(FoodDbPeer.IS_ACTIVE, (short)1);
		crit.add(FoodDbPeer.IS_PUBLIC, (short)1);
		crit.addAscendingOrderByColumn(FoodDbPeer.NAME);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		System.out.println("searchPublicFood crit >" + crit.toString());
		
		Iterator itr = FoodDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			Food obj = Food.getFood((FoodDb)itr.next());
			vec.addElement(obj);
		}

		return vec;
    }

    public static boolean
    isFavorite(Food _fud, PersonBean _person)
		throws TorqueException, ObjectNotFoundException {
		Criteria crit = new Criteria();
		crit.add(FavoriteFoodPeer.PERSON_ID, _person.getId());
		crit.add(FavoriteFoodPeer.FOOD_DB_ID, _fud.getId());
		return !FavoriteFoodPeer.doSelect(crit).isEmpty();
    }

    public static void
    removeFavorite(Food _fud, PersonBean _person)
		throws TorqueException, ObjectNotFoundException {
		Criteria crit = new Criteria();
		crit.add(FavoriteFoodPeer.PERSON_ID, _person.getId());
		crit.add(FavoriteFoodPeer.FOOD_DB_ID, _fud.getId());
		FavoriteFoodPeer.doDelete(crit);
    }
	

    // SQL

    /*
	

<table name="FAVORITE_FOOD">
	<column name="PERSON_ID" required="false" type="INTEGER"/>
	<column name="FOOD_DB_ID" required="false" type="BIGINT"/>

	<column name="FAVORITE_DATE" required="true" type="DATE"/>

    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="FOOD_DB">
		<reference local="FOOD_DB_ID" foreign="FOOD_DB_ID"/>
    </foreign-key>
</table>
	
	
<table name="FOOD_DB" idMethod="native">
	<column name="FOOD_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	
	<column name="NAME" required="true" size="100" type="VARCHAR"/>
	<column name="DESCRIPTION" type="LONGVARCHAR"/>
	<column name="SCAN_ID" size="20" type="VARCHAR"/>
	
	<column name="IS_ACTIVE" type="SMALLINT" default="1"/>
	<column name="IS_PUBLIC" type="SMALLINT" default="0"/>
	
	<column name="CALORIES" scale="2" size="7" type="DECIMAL"/>
	<column name="FAT" scale="2" size="7" type="DECIMAL"/>
	<column name="CARBS" scale="2" size="7" type="DECIMAL"/>
	<column name="PROTEIN" scale="2" size="7" type="DECIMAL"/>
	
	<column name="SERVING_SIZE" scale="2" size="7" type="DECIMAL"/>
	<column name="SERVING_SIZE_TYPE" type="SMALLINT"/>
	<!-- 1 = grams -->
	<!-- 2 = ml -->
	
	<column name="CREATION_DATE" required="true" type="DATE"/>
	<column name="MODIFICATION_DATE" required="false" type="DATE"/>
	<column name="CREATE_PERSON_ID" required="true" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
		<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
     */
	
    // INSTANCE VARIABLES

    private FoodDb food;
	private Vector servings = null;

    // CONSTRUCTORS

    public
    Food() {
		food = new FoodDb();
		isNew = true;
    }

    public
    Food(FoodDb _obj) {
		food = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public boolean
	equals(Food _f) {
		try {
			if (!this.getNameString().equals(_f.getNameString())) {
				return false;
			}
			if (this.getServingSize().compareTo(_f.getServingSize()) != 0) {
				return false;
			}
			if (this.getServingSizeType() != _f.getServingSizeType()) {
				return false;
			}
			if (this.getFat().compareTo(_f.getFat()) != 0) {
				return false;
			}
			if (this.getCarbs().compareTo(_f.getCarbs()) != 0) {
				return false;
			}
			if (this.getProtein().compareTo(_f.getProtein()) != 0) {
				return false;
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		return true;
	}

    public long
    getId() {
		return food.getFoodDbId();
    }

    public String
    getLabel() {
		return this.getNameString();
    }
	
	public void
	setName(String _name) {
		food.setName(_name);
	}
	
	public String
	getNameString() {
		String str = food.getName();
		if (str == null) {
			return "";
		}
		return str;
	}
	
	
	
	public String getDescription() {
		String str = food.getDescription();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setDescription(String _s) {
		food.setDescription(_s);
	}
	
	public String getCategory() {
		String str = food.getCategory();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setCategory(String _s) {
		food.setCategory(_s);
	}
	
	public String getManufacturer() {
		String str = food.getManufacturer();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setManufacturer(String _s) {
		food.setManufacturer(_s);
	}

	public String getScanId() {
		String str = food.getScanId();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setScanId(String scanId) {
		food.setScanId(scanId);
	}

	public BigDecimal getCalories() {
		return food.getCalories();
	}

	public void setCalories(BigDecimal calories) {
		food.setCalories(calories);
	}

	public BigDecimal getFat() {
		if (food.getFat() == null) {
			return BigDecimal.ZERO;
		}
		return food.getFat();
	}
	
	public String
	getFatString() {
		return this.getStringFromBigDecimal(this.getFat());
	}

	public void setFat(BigDecimal fat) {
		food.setFat(fat);
	}

	public BigDecimal getCarbs() {
		if (food.getCarbs() == null) {
			return BigDecimal.ZERO;
		}
		return food.getCarbs();
	}
	
	public String
	getCarbsString() {
		return this.getStringFromBigDecimal(this.getCarbs());
	}

	public void setCarbs(BigDecimal carbs) {
		food.setCarbs(carbs);
	}

	public BigDecimal getProtein() {
		if (food.getProtein() == null) {
			return BigDecimal.ZERO;
		}
		return food.getProtein();
	}
	
	public String
	getProteinString() {
		return this.getStringFromBigDecimal(this.getProtein());
	}

	public void setProtein(BigDecimal protein) {
		food.setProtein(protein);
	}

	public BigDecimal getServingSize() {
		return food.getServingSize();
	}
	
	public String
	getServingSizeString() {
		if (food.getServingSize() == null) {
			return "0";
		}
		if (food.getServingSize().compareTo(BigDecimal.ZERO) == 0) {
			return "0";
		}
		return this.getStringFromBigDecimal(food.getServingSize());
	}

	public void setServingSize(BigDecimal servingSize) {
		food.setServingSize(servingSize);
	}

	public short getServingSizeType() {
		return food.getServingSizeType();
	}

	public String getServingSizeTypeString() {
		switch (food.getServingSizeType()) {
			case Food.GRAMS_SERVING_SIZE_TYPE: return "grams";
			case Food.OZ_SERVING_SIZE_TYPE: return "oz";
			case Food.ML_SERVING_SIZE_TYPE: return "ml";
		}
		return "[TYPE NOT FOUND]";
	}

	public String getServingSizeTypeString(short _s) {
		switch (_s) {
			case Food.GRAMS_SERVING_SIZE_TYPE: return "grams";
			case Food.OZ_SERVING_SIZE_TYPE: return "oz";
			case Food.ML_SERVING_SIZE_TYPE: return "ml";
		}
		return "[TYPE NOT FOUND]";
	}

	public void setServingSizeType(short servingSizeType) {
		food.setServingSizeType(servingSizeType);
	}

	public Date getCreationDate() {
		return food.getCreationDate();
	}

    
    public long getEntryDateUnixTimestamp() {
		return this.food.getCreationDate().getTime() / 1000;
    }

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		food.setCreationDate(new Date());
		food.save();
		this.saveServings();
    }

    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			food.setCreatePersonId(_person.getId());
		else
			food.setModifyPersonId(_person.getId());
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		food.setModificationDate(new Date());
		food.save();
		this.saveServings();
    }
	
	
	
	public BigDecimal
	getFatPerGram(BigDecimal _grams) throws IllegalValueException {
		
		switch (this.getServingSizeType()) {
			case Food.GRAMS_SERVING_SIZE_TYPE: {
				return this.getFat().multiply(_grams).divide(this.getServingSize(), return_context);
			}
			case Food.OZ_SERVING_SIZE_TYPE: {
				BigDecimal grams_to_ounces = _grams.multiply(Food.oz_per_gram, conversion_context);
				BigDecimal ounces_of_fat =  this.getFat().multiply(grams_to_ounces).divide(this.getServingSize(), conversion_context);
				return ounces_of_fat.multiply(Food.grams_per_oz, return_context);
			}
		}
		
		throw new IllegalValueException("Unable to determine serving size type for " + this.getLabel());
	}
	
	public BigDecimal
	getFatPerOunce(BigDecimal _ounces) throws IllegalValueException {
		
		switch (this.getServingSizeType()) {
			case Food.GRAMS_SERVING_SIZE_TYPE: {
				BigDecimal ounces_to_grams = _ounces.multiply(Food.grams_per_oz, conversion_context);
				BigDecimal grams_of_fat =  this.getFat().multiply(ounces_to_grams).divide(this.getServingSize(), conversion_context);
				return grams_of_fat.multiply(Food.oz_per_gram, return_context);
			}
			case Food.OZ_SERVING_SIZE_TYPE: {
				return this.getFat().multiply(_ounces).divide(this.getServingSize(), return_context);
			}
		}
		
		throw new IllegalValueException("Unable to determine serving size type for " + this.getLabel());
	}
	
	
	public BigDecimal
	getCarbsPerGram(BigDecimal _grams) throws IllegalValueException {
		
		switch (this.getServingSizeType()) {
			case Food.GRAMS_SERVING_SIZE_TYPE: {
				return this.getCarbs().multiply(_grams).divide(this.getServingSize(), return_context);
			}
			case Food.OZ_SERVING_SIZE_TYPE: {
				BigDecimal grams_to_ounces = _grams.multiply(Food.oz_per_gram, conversion_context);
				BigDecimal ounces_of_carbs =  this.getCarbs().multiply(grams_to_ounces).divide(this.getServingSize(), conversion_context);
				return ounces_of_carbs.multiply(Food.grams_per_oz, return_context);
			}
		}
		
		throw new IllegalValueException("Unable to determine serving size type for " + this.getLabel());
	}
	
	public BigDecimal
	getCarbsPerOunce(BigDecimal _ounces) throws IllegalValueException {
		
		switch (this.getServingSizeType()) {
			case Food.GRAMS_SERVING_SIZE_TYPE: {
				BigDecimal ounces_to_grams = _ounces.multiply(Food.grams_per_oz, conversion_context);
				BigDecimal grams_of_carbs =  this.getCarbs().multiply(ounces_to_grams).divide(this.getServingSize(), conversion_context);
				return grams_of_carbs.multiply(Food.oz_per_gram, return_context);
			}
			case Food.OZ_SERVING_SIZE_TYPE: {
				return this.getCarbs().multiply(_ounces).divide(this.getServingSize(), return_context);
			}
		}
		
		throw new IllegalValueException("Unable to determine serving size type for " + this.getLabel());
	}
	
	
	public BigDecimal
	getProteinPerGram(BigDecimal _grams) throws IllegalValueException {
		
		switch (this.getServingSizeType()) {
			case Food.GRAMS_SERVING_SIZE_TYPE: {
				return this.getProtein().multiply(_grams).divide(this.getServingSize(), return_context);
			}
			case Food.OZ_SERVING_SIZE_TYPE: {
				BigDecimal grams_to_ounces = _grams.multiply(Food.oz_per_gram, conversion_context);
				BigDecimal ounces_of_protein =  this.getProtein().multiply(grams_to_ounces).divide(this.getServingSize(), conversion_context);
				return ounces_of_protein.multiply(Food.grams_per_oz, return_context);
			}
		}
		
		throw new IllegalValueException("Unable to determine serving size type for " + this.getLabel());
	}
	
	public BigDecimal
	getProteinPerOunce(BigDecimal _ounces) throws IllegalValueException {
		
		switch (this.getServingSizeType()) {
			case Food.GRAMS_SERVING_SIZE_TYPE: {
				BigDecimal ounces_to_grams = _ounces.multiply(Food.grams_per_oz, conversion_context);
				BigDecimal grams_of_protein =  this.getProtein().multiply(ounces_to_grams).divide(this.getServingSize(), conversion_context);
				return grams_of_protein.multiply(Food.oz_per_gram, return_context);
			}
			case Food.OZ_SERVING_SIZE_TYPE: {
				return this.getProtein().multiply(_ounces).divide(this.getServingSize(), return_context);
			}
		}
		
		throw new IllegalValueException("Unable to determine serving size type for " + this.getLabel());
	}
	
	
	
	private String
	getStringFromBigDecimal(BigDecimal _bd) {
		if (_bd == null) {
			return "";
		}
		String str = _bd.setScale(2, RoundingMode.HALF_UP).toString();
		int index = str.indexOf(".00");
		if (index > -1) {
			return str.substring(0, index);
		}
		return str;
	}
	
	
	public boolean
	isActive() {
		return (food.getIsActive() == (short)1);
	}
	
	public void
	setActive(boolean _b) {
		food.setIsActive(_b ? (short)1 : (short)0);
	}
	
	public boolean
	isPublic() {
		return (food.getIsPublic() == (short)1);
	}
	
	public void
	setPublic(boolean _b) {
		food.setIsPublic(_b ? (short)1 : (short)0);
	}
	
	public String
	getNDbNo() {
		String str = food.getNDbNo();
		if (str == null) {
			return "";
		}
		return str;
	}
	
	public void
	setNDbNo(String _s) {
		food.setNDbNo(_s);
	}
	
	public long
	getOffset() {
		return food.getOffset();
	}
	
	public void
	setOffset(long _l) {
		food.setOffset(_l);
	}
	
	public void
	invalidate() {
		this.servings = null;
	}

	public Vector
	getServings() throws TorqueException, ObjectNotFoundException {
		if (this.servings == null) {
			this.servings = new Vector();
			Criteria crit = new Criteria();
			crit.add(FoodServingDbPeer.FOOD_DB_ID, this.getId());
			crit.addAscendingOrderByColumn(FoodServingDbPeer.SERVING_SIZE); // shrug
			Iterator itr = FoodServingDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				FoodServingDb obj = (FoodServingDb)itr.next();
				this.servings.addElement(obj);
			}
		}
		return this.servings;
	}

	public void
	setServings(Vector _v) {
		this.servings = _v;
	}

	private void
    saveServings() throws TorqueException, Exception {
		if (this.servings != null) {
			//System.out.println("SAVE SERVINGS for >" + this.getLabel());
			//System.out.println("saveServings sizer >" + this.servings.size());

			//HashMap<String,FoodServingDb> db_serving_hash = new HashMap<String,FoodServingDb>(3);
			Criteria crit = new Criteria();
			crit.add(FoodServingDbPeer.FOOD_DB_ID, this.getId());
			FoodServingDbPeer.doDelete(crit);
			
			/*
			Iterator itr = FoodServingDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				System.out.println("found existing ass...");
				FoodServingDb value = (FoodServingDb)itr.next();
				String key = value.getServingLabel();
				//db_serving_hash.put(key, value);
			}
			*/

			System.out.println("num servings >" + this.servings.size());
			Iterator itr = this.servings.iterator();
			while (itr.hasNext()) {
				FoodServingDb serving_obj = (FoodServingDb)itr.next();
				serving_obj.setFoodDbId(this.getId()); // just to be sure
				serving_obj.save(); // in case this isn't being saved elsewhere, I guess
				//db_serving_hash.remove(serving_obj.getServingLabel());
			}

			/*
			itr = db_serving_hash.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String)itr.next();
				FoodServingDb obj = (FoodServingDb)db_serving_hash.get(key);
				FoodServingDbPeer.doDelete(obj);
			}
			*/
		}
    }
	
	public boolean
	isFavorite(PersonBean _person) throws TorqueException {
		Criteria crit = new Criteria();
		crit.addJoin(FavoriteFoodPeer.FOOD_DB_ID, FoodDbPeer.FOOD_DB_ID);
		crit.add(FavoriteFoodPeer.PERSON_ID, _person.getId());
		crit.add(FoodDbPeer.FOOD_DB_ID, this.getId());
		System.out.println("crit >" + crit.toString());
		List l = FavoriteFoodPeer.doSelect(crit);
		return (l.size() > 0);
	}
	
	
}
