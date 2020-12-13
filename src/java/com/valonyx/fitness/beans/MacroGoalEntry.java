/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.fitness.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
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
MacroGoalEntry
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
	public static final short SERVINGS_TYPE = 1;
	public static final short GRAMS_TYPE = 2;
	public static final short ML_TYPE = 3;
	public static final short OUNCES_TYPE = 4;

    protected static HashMap<Long,MacroGoalEntry> hash = new HashMap<Long,MacroGoalEntry>();

    // CLASS METHODS

    public static void
    delete(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		hash.remove(_id);

		Criteria crit = new Criteria();
		crit.add(MacroGoalEntryDbPeer.MACRO_GOAL_ENTRY_DB_ID, _id);
		MacroGoalEntryDbPeer.doDelete(crit);
    }

    public static MacroGoalEntry
    getMacroGoalEntry(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		MacroGoalEntry entry_obj = (MacroGoalEntry)hash.get(_id);
		if (entry_obj == null) {
			Criteria crit = new Criteria();
			crit.add(MacroGoalEntryDbPeer.MACRO_GOAL_ENTRY_DB_ID, _id);
			List objList = MacroGoalEntryDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate entry with id: " + _id);
			}

			entry_obj = MacroGoalEntry.getMacroGoalEntry((MacroGoalEntryDb)objList.get(0));
		}

		return entry_obj;
    }

    public static MacroGoalEntry
    getMacroGoalEntry(MacroGoalEntryDb _obj) throws TorqueException {
		
		MacroGoalEntry entry_obj = (MacroGoalEntry)hash.get(_obj.getMacroGoalEntryDbId());
		if (entry_obj == null) {
			entry_obj = new MacroGoalEntry(_obj);
			hash.put(_obj.getMacroGoalEntryDbId(), entry_obj);
		}

		return entry_obj;
    }

    public static Vector
    getEntries(MacroGoalDaily _goal)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(MacroGoalEntryDbPeer.MACRO_GOAL_DAILY_DB_ID, _goal);
		crit.addAscendingOrderByColumn(MacroGoalEntryDbPeer.ENTRY_DATE);
		Iterator itr = MacroGoalEntryDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(MacroGoalEntry.getMacroGoalEntry((MacroGoalEntryDb)itr.next()));
		}
		
		return vec;
    }

    public static Vector
    getEntries(PersonBean _person, int _field)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Calendar time_frame = Calendar.getInstance();
		time_frame.add(_field, -1);
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(MacroGoalDbPeer.MACRO_GOAL_DB_ID, MacroGoalDailyDbPeer.MACRO_GOAL_DB_ID);
		crit.addJoin(MacroGoalDailyDbPeer.MACRO_GOAL_DAILY_DB_ID, MacroGoalEntryDbPeer.MACRO_GOAL_DAILY_DB_ID);
		crit.add(MacroGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(MacroGoalEntryDbPeer.ENTRY_DATE, time_frame.getTime(), Criteria.GREATER_EQUAL);
		crit.addDescendingOrderByColumn(MacroGoalEntryDbPeer.ENTRY_DATE);
		Iterator itr = MacroGoalEntryDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(MacroGoalEntry.getMacroGoalEntry((MacroGoalEntryDb)itr.next()));
		}
		
		return vec;
    }
	

    // SQL

    /*

<table name="MACRO_GOAL_ENTRY_DB" idMethod="native">
	<column name="MACRO_GOAL_ENTRY_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	<column name="MACRO_GOAL_DAILY_DB_ID" required="true" type="BIGINT"/>
	
	<column name="NAME" required="true" size="100" type="VARCHAR"/>
	
	<column name="CALORIES" scale="2" size="7" type="DECIMAL"/>
	<column name="FAT" scale="2" size="7" type="DECIMAL"/>
	<column name="CARBS" scale="2" size="7" type="DECIMAL"/>
	<column name="PROTEIN" scale="2" size="7" type="DECIMAL"/>
	
	<column name="SERVING_SIZE" scale="2" size="7" type="DECIMAL"/>
	<column name="SERVING_SIZE_TYPE" type="SMALLINT"/>
	<!-- 1 = grams -->
	<!-- 2 = ml -->
	
	<column name="ENTRY_DATE" required="true" type="DATE"/>
	<column name="ENTRY_AMOUNT" scale="2" size="7" type="DECIMAL"/>
	<column name="ENTRY_TYPE" type="SMALLINT"/>
	<!-- 1 = serving -->
	<!-- 2 = grams -->
	<!-- 3 = ml -->
	<!-- 4 = oz -->
	
	<column name="FOOD_DB_ID" required="false" type="BIGINT"/>

    <foreign-key foreignTable="MACRO_GOAL_DAILY_DB">
		<reference local="MACRO_GOAL_DAILY_DB_ID" foreign="MACRO_GOAL_DAILY_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="FOOD_DB">
		<reference local="FOOD_DB_ID" foreign="FOOD_DB_ID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private MacroGoalEntryDb entry;

    // CONSTRUCTORS

    public
    MacroGoalEntry() {
		entry = new MacroGoalEntryDb();
		isNew = true;
    }

    public
    MacroGoalEntry(MacroGoalEntryDb _obj) {
		entry = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public MacroGoalDaily
	getParent() throws TorqueException, ObjectNotFoundException {
		return MacroGoalDaily.getMacroGoalDaily(entry.getMacroGoalDailyDbId());
	}

	public String getName() {
		return entry.getName();
	}

	public void setName(String name) {
		if (name.length() > 250) {
			entry.setName(name.substring(0, 250));
		} else {
			entry.setName(name);
		}
	}

	public BigDecimal getCalories() {
		return entry.getCalories();
	}
	
	/*
	public void
	calculateCalories() throws IllegalValueException {
		
		if (this.getEntryType() == MacroGoalEntry.GRAMS_TYPE) {
			BigDecimal cals = BigDecimal.ZERO;
			cals.add(entry.getFat().multiply(MacroGoal.CALORIES_PER_GRAM_OF_FAT, Food.return_context), Food.return_context);
			cals.add(entry.getCarbs().multiply(MacroGoal.CALORIES_PER_GRAM_OF_CARB, Food.return_context), Food.return_context);
			cals.add(entry.getProtein().multiply(MacroGoal.CALORIES_PER_GRAM_OF_PROTEIN, Food.return_context), Food.return_context);
			entry.setCalories(cals);
		}
	}
	*/
	
	public BigDecimal
	calculateCalories() throws IllegalValueException {
		/*
		if (this.getGoalType() != MacroGoal.GRAMS_TYPE) {
			throw new IllegalValueException("Unable to calculate calories for " + this.getGoalTypeString() + " based macro goal.");
		}
		*/
		
		BigDecimal fat_cals = entry.getFat().multiply(MacroGoal.CALORIES_PER_GRAM_OF_FAT);
		BigDecimal carb_cals = entry.getCarbs().multiply(MacroGoal.CALORIES_PER_GRAM_OF_CARB);
		BigDecimal protein_cals = entry.getProtein().multiply(MacroGoal.CALORIES_PER_GRAM_OF_PROTEIN);
		BigDecimal calculated_calories = fat_cals.add(carb_cals).add(protein_cals);
		entry.setCalories(calculated_calories);
		return calculated_calories;
	}

	private void setCalories(BigDecimal calories) {
		entry.setCalories(calories);
	}
	
	public String
	getCaloriesString() {
		return entry.getCalories().setScale(0, RoundingMode.HALF_UP).toString();
		//return this.getStringFromBigDecimal(entry.getCalories());
	}

	public BigDecimal getFat() {
		return entry.getFat();
	}
	
	public String
	getFatString() {
		return this.getStringFromBigDecimal(entry.getFat());
	}

	public void setFat(BigDecimal fat) {
		entry.setFat(fat);
	}

	public BigDecimal getCarbs() {
		return entry.getCarbs();
	}
	
	public String
	getCarbsString() {
		return this.getStringFromBigDecimal(entry.getCarbs());
	}

	public void setCarbs(BigDecimal carbs) {
		entry.setCarbs(carbs);
	}

	public BigDecimal getProtein() {
		return entry.getProtein();
	}
	
	public String
	getProteinString() {
		return this.getStringFromBigDecimal(entry.getProtein());
	}

	public void setProtein(BigDecimal protein) {
		entry.setProtein(protein);
	}
	
	public Date
	getEntryDate() {
		return entry.getEntryDate();
	}
	
	public String
	getEntryDateString() {
		if (entry.getEntryDate() == null) {
			return "[ENTRY DATE NOT FOUND]";
		}
		return CUBean.getUserDateString(entry.getEntryDate());
	}
	
	public void
	setEntryDate(Date _dt) {
		entry.setEntryDate(_dt);
	}

    public long
    getId() {
		return entry.getMacroGoalEntryDbId();
    }

    public String
    getLabel() {
		String str = this.getName();
		if (str == null) {
			return "[NO ENTRY NAME]";
		}
		return str;
    }
	
	public void
	setMacroGoalDaily(MacroGoalDaily _goal) throws TorqueException {
		entry.setMacroGoalDailyDbId(_goal.getId());
	}
    
    public long getEntryDateUnixTimestamp() {
		return this.entry.getEntryDate().getTime() / 1000;
    }

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		entry.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		entry.save();
    }
	
	public void
	setEntryType(short _type) {
		entry.setEntryType(_type);
	}
	
	public short
	getEntryType() {
		return entry.getEntryType();
	}
	
	public String
	getEntryTypeString(BigDecimal _amount) {
		if (_amount.compareTo(BigDecimal.ONE) == 0) {
			switch (entry.getEntryType()) {
				case MacroGoalEntry.SERVINGS_TYPE: return "serving";
				case MacroGoalEntry.GRAMS_TYPE: return "gram";
				case MacroGoalEntry.OUNCES_TYPE: return "ounce";
				case MacroGoalEntry.ML_TYPE: return "ml";
			}
		} else {
			switch (entry.getEntryType()) {
				case MacroGoalEntry.SERVINGS_TYPE: return "servings";
				case MacroGoalEntry.GRAMS_TYPE: return "grams";
				case MacroGoalEntry.OUNCES_TYPE: return "ounces";
				case MacroGoalEntry.ML_TYPE: return "ml";
			}
		}
		return "[TYPE NOT FOUND]";
	}
	
	public void
	setEntryAmount(BigDecimal _amount) {
		entry.setEntryAmount(_amount);
	}
	
	public BigDecimal
	getEntryAmount() {
		return entry.getEntryAmount();
	}
	
	public String
	getEntryAmountString() {
		return this.getStringFromBigDecimal(entry.getEntryAmount());
	}
	
	public void
	setFood(Food _food) throws TorqueException {
		entry.setFoodDbId(_food.getId());
	}
	
	public Food
	getFood() throws TorqueException, ObjectNotFoundException {
		return Food.getFood(entry.getFoodDbId());
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
	
	public BigDecimal getServingSize() {
		return entry.getServingSize();
	}

	public void setServingSize(BigDecimal servingSize) {
		entry.setServingSize(servingSize);
	}

	public short getServingSizeType() {
		return entry.getServingSizeType();
	}

	public String getServingSizeTypeString() {
		switch (entry.getServingSizeType()) {
			case Food.GRAMS_SERVING_SIZE_TYPE: return "grams";
			case Food.OZ_SERVING_SIZE_TYPE: return "oz";
			case Food.ML_SERVING_SIZE_TYPE: return "ml";
		}
		return "[TYPE NOT FOUND]";
	}

	public void setServingSizeType(short servingSizeType) {
		entry.setServingSizeType(servingSizeType);
	}
	
}
