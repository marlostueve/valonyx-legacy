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
import java.math.RoundingMode;
import java.text.SimpleDateFormat;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;



/**
 *
 * @author marlo
 */
public class
MacroGoalDaily
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	public static final short GRAMS_TYPE = 1;
	public static final short PERCENTAGE_TYPE = 2;

    protected static HashMap<Long,MacroGoalDaily> hash = new HashMap<Long,MacroGoalDaily>();

    // CLASS METHODS

    public static MacroGoalDaily
    getMacroGoalDaily(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		MacroGoalDaily goal_obj = (MacroGoalDaily)hash.get(_id);
		if (goal_obj == null) {
			Criteria crit = new Criteria();
			crit.add(MacroGoalDailyDbPeer.MACRO_GOAL_DAILY_DB_ID, _id);
			List objList = MacroGoalDailyDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate goal with id: " + _id);
			}

			goal_obj = MacroGoalDaily.getMacroGoalDaily((MacroGoalDailyDb)objList.get(0));
		}

		return goal_obj;
    }

    private static MacroGoalDaily
    getMacroGoalDaily(MacroGoalDailyDb _obj) throws TorqueException {
		
		MacroGoalDaily goal_obj = (MacroGoalDaily)hash.get(_obj.getMacroGoalDailyDbId());
		if (goal_obj == null) {
			goal_obj = new MacroGoalDaily(_obj);
			hash.put(_obj.getMacroGoalDailyDbId(), goal_obj);
		}

		return goal_obj;
    }

    public static MacroGoalDaily
    getCurrentMacroGoalDaily(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		//System.out.println("getCurrentMacroGoalDaily invoked for " + _person.getId());
		
		Date now = new Date(); // time zone issues??  probz
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.setTime(now);
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);

		Calendar end_of_day = Calendar.getInstance();
		end_of_day.setTime(now);
		end_of_day.set(Calendar.HOUR_OF_DAY, 23);
		end_of_day.set(Calendar.MINUTE, 59);
		end_of_day.set(Calendar.SECOND, 59);
		
		Criteria crit = new Criteria();
		//crit.add(MacroGoalDailyDbPeer.MACRO_GOAL_DB_ID, _goal.getId());
	
		crit.addJoin(MacroGoalDbPeer.MACRO_GOAL_DB_ID, MacroGoalDailyDbPeer.MACRO_GOAL_DB_ID);
		crit.add(MacroGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(MacroGoalDbPeer.IS_ACTIVE, (short)1); // added 2/25/19
		
		crit.add(MacroGoalDailyDbPeer.ENTRY_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(MacroGoalDailyDbPeer.ENTRY_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		
		System.out.println("getCurrentMacroGoalDaily crit >" + crit.toString());
		
		List objList = MacroGoalDailyDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			MacroGoalDaily new_goal = new MacroGoalDaily();
			MacroGoal _goal = MacroGoal.getCurrentMacroGoal(_person);
			new_goal.setMacroGoal(_goal);
			new_goal.setCalories(BigDecimal.ZERO);
			new_goal.setFat(BigDecimal.ZERO);
			new_goal.setCarbs(BigDecimal.ZERO);
			new_goal.setProtein(BigDecimal.ZERO);
			new_goal.save();
			hash.put(new_goal.getId(), new_goal);
			return new_goal;
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unique daily macro goal for " + CUBean.getUserDateString(now));
		}

		return MacroGoalDaily.getMacroGoalDaily((MacroGoalDailyDb)objList.get(0));
    }

    public static MacroGoalDaily
    getCurrentMacroGoalDaily(UKOnlinePersonBean _person, Date _entry_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		//System.out.println("getCurrentMacroGoalDaily invoked for " + _person.getId());
		
		//Date now = new Date(); // time zone issues??  probz
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.setTime(_entry_date);
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);

		Calendar end_of_day = Calendar.getInstance();
		end_of_day.setTime(_entry_date);
		end_of_day.set(Calendar.HOUR_OF_DAY, 23);
		end_of_day.set(Calendar.MINUTE, 59);
		end_of_day.set(Calendar.SECOND, 59);
		
		Criteria crit = new Criteria();
		//crit.add(MacroGoalDailyDbPeer.MACRO_GOAL_DB_ID, _goal.getId());
	
		crit.addJoin(MacroGoalDbPeer.MACRO_GOAL_DB_ID, MacroGoalDailyDbPeer.MACRO_GOAL_DB_ID);
		crit.add(MacroGoalDbPeer.PERSON_ID, _person.getId());
		
		crit.add(MacroGoalDailyDbPeer.ENTRY_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(MacroGoalDailyDbPeer.ENTRY_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		
		List objList = MacroGoalDailyDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			MacroGoalDaily new_goal = new MacroGoalDaily();
			MacroGoal _goal = MacroGoal.getCurrentMacroGoal(_person);
			new_goal.setMacroGoal(_goal);
			new_goal.setCalories(BigDecimal.ZERO);
			new_goal.setFat(BigDecimal.ZERO);
			new_goal.setCarbs(BigDecimal.ZERO);
			new_goal.setProtein(BigDecimal.ZERO);
			new_goal.save();
			hash.put(new_goal.getId(), new_goal);
			return new_goal;
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unique daily macro goal for " + CUBean.getUserDateString(_entry_date));
		}

		return MacroGoalDaily.getMacroGoalDaily((MacroGoalDailyDb)objList.get(0));
    }

    public static MacroGoalDaily
    getMacroGoalDaily(UKOnlinePersonBean _person, Date _display_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		//MacroGoal _goal = MacroGoal.getCurrentMacroGoal(_person);
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.setTime(_display_date);
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);

		Calendar end_of_day = Calendar.getInstance();
		end_of_day.setTime(_display_date);
		end_of_day.set(Calendar.HOUR_OF_DAY, 23);
		end_of_day.set(Calendar.MINUTE, 59);
		end_of_day.set(Calendar.SECOND, 59);
		
		Criteria crit = new Criteria();
		//crit.add(MacroGoalDailyDbPeer.MACRO_GOAL_DB_ID, _goal.getId());
		
		crit.addJoin(MacroGoalDbPeer.MACRO_GOAL_DB_ID, MacroGoalDailyDbPeer.MACRO_GOAL_DB_ID);
		
		crit.add(MacroGoalDbPeer.PERSON_ID, _person.getId());
		
		crit.add(MacroGoalDailyDbPeer.ENTRY_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(MacroGoalDailyDbPeer.ENTRY_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		
		List objList = MacroGoalDailyDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			//throw new UniqueObjectNotFoundException("Could not locate daily macro goal for " + CUBean.getUserDateString(_display_date));
			
			MacroGoalDaily new_goal = new MacroGoalDaily();
			MacroGoal _goal = MacroGoal.getCurrentMacroGoal(_person);
			new_goal.setMacroGoal(_goal);
			new_goal.setCalories(BigDecimal.ZERO);
			new_goal.setFat(BigDecimal.ZERO);
			new_goal.setCarbs(BigDecimal.ZERO);
			new_goal.setProtein(BigDecimal.ZERO);
			new_goal.setEntryDate(_display_date);
			new_goal.save();
			hash.put(new_goal.getId(), new_goal);
			return new_goal;
			
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unique daily macro goal for " + CUBean.getUserDateString(_display_date));
		}

		return MacroGoalDaily.getMacroGoalDaily((MacroGoalDailyDb)objList.get(0));
    }

    public static Vector
    getMacroGoalDailys(MacroGoal _goal)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(MacroGoalDailyDbPeer.MACRO_GOAL_DB_ID, _goal.getId());
		crit.addAscendingOrderByColumn(MacroGoalDailyDbPeer.ENTRY_DATE);
		Iterator itr = MacroGoalDailyDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(MacroGoalDaily.getMacroGoalDaily((MacroGoalDailyDb)itr.next()));
		}
		
		return vec;
    }

    public static ArrayList<MacroGoalDaily>
    getLastMacroGoalDailys(PersonBean _person, int _num)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		ArrayList<MacroGoalDaily> vec = new ArrayList();
		Criteria crit = new Criteria();
		crit.addJoin(MacroGoalDbPeer.MACRO_GOAL_DB_ID, MacroGoalDailyDbPeer.MACRO_GOAL_DB_ID);
		crit.add(MacroGoalDbPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(MacroGoalDailyDbPeer.ENTRY_DATE);
		crit.setLimit(_num);
		Iterator itr = MacroGoalDailyDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			MacroGoalDaily daily = MacroGoalDaily.getMacroGoalDaily((MacroGoalDailyDb)itr.next());
			if (daily.hasEntries()) {
				vec.add(daily);
			}
		}
		
		return vec;
    }

    public static Vector
    getMacroGoalDailys(PersonBean _person, int _field, int _count)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Calendar time_frame = Calendar.getInstance();
		time_frame.add(_field, _count);
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(MacroGoalDbPeer.MACRO_GOAL_DB_ID, MacroGoalDailyDbPeer.MACRO_GOAL_DB_ID);
		crit.add(MacroGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(MacroGoalDailyDbPeer.ENTRY_DATE, time_frame.getTime(), Criteria.GREATER_EQUAL);
		//crit.addDescendingOrderByColumn(MacroGoalDailyDbPeer.ENTRY_DATE);
		crit.addAscendingOrderByColumn(MacroGoalDailyDbPeer.ENTRY_DATE);
		Iterator itr = MacroGoalDailyDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			MacroGoalDaily daily = MacroGoalDaily.getMacroGoalDaily((MacroGoalDailyDb)itr.next());
			if (daily.hasEntries()) {
				vec.addElement(daily);
			}
		}
		
		return vec;
    }
	

    // SQL

    /*


<table name="MACRO_GOAL_DAILY_DB" idMethod="native">
	<column name="MACRO_GOAL_DAILY_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	<column name="MACRO_GOAL_DB_ID" required="true" type="INTEGER"/>
	
	<column name="CALORIES" scale="2" size="7" type="DECIMAL"/>
	<column name="FAT" scale="2" size="7" type="DECIMAL"/>
	<column name="CARBS" scale="2" size="7" type="DECIMAL"/>
	<column name="PROTEIN" scale="2" size="7" type="DECIMAL"/>
	
	<column name="ENTRY_DATE" required="true" type="DATE"/>

    <foreign-key foreignTable="MACRO_GOAL_DB">
		<reference local="MACRO_GOAL_DB_ID" foreign="MACRO_GOAL_DB_ID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private MacroGoalDailyDb goal;
	private Vector entries;

    // CONSTRUCTORS

    public
    MacroGoalDaily() {
		goal = new MacroGoalDailyDb();
		isNew = true;
    }

    public
    MacroGoalDaily(MacroGoalDailyDb _obj) {
		goal = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public void
	calculateCalories() throws IllegalValueException {
		/*
		if (this.getGoalType() != MacroGoal.GRAMS_TYPE) {
			throw new IllegalValueException("Unable to calculate calories for " + this.getGoalTypeString() + " based macro goal.");
		}
		*/
		
		BigDecimal fat_cals = goal.getFat().multiply(MacroGoal.CALORIES_PER_GRAM_OF_FAT);
		BigDecimal carb_cals = goal.getCarbs().multiply(MacroGoal.CALORIES_PER_GRAM_OF_CARB);
		BigDecimal protein_cals = goal.getProtein().multiply(MacroGoal.CALORIES_PER_GRAM_OF_PROTEIN);
		goal.setCalories(fat_cals.add(carb_cals).add(protein_cals));
	}
	
	public BigDecimal
	getCalories() {
		return goal.getCalories();
	}
	
	public String
	getCaloriesString() throws IllegalValueException, ObjectAlreadyExistsException, Exception {
		if (goal.getCalories().compareTo(BigDecimal.ZERO) == 0) {
			this.calculateCalories();
			this.save();
		}
		return goal.getCalories().setScale(0, RoundingMode.HALF_UP).toString();
	}
	
	public String
	getCaloriesPercentageString() throws IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		BigDecimal calories_goal = this.getParent().getCalories();
		if (calories_goal.compareTo(BigDecimal.ZERO) == 0) {
			return "0";
		}
		BigDecimal perc = goal.getCalories().multiply(MacroGoalDaily.ONE_HUNDRED).divide(calories_goal, 2, RoundingMode.HALF_UP);
		if (perc.compareTo(MacroGoalDaily.ONE_HUNDRED) == 1) {
			return "100";
		}
		return perc.setScale(0, RoundingMode.HALF_UP).toString();
	}
	
	private void
	setCalories(BigDecimal _calories) {
		goal.setCalories(_calories);
	}
	
	public MacroGoal
	getParent() throws TorqueException, ObjectNotFoundException {
		return MacroGoal.getMacroGoal(goal.getMacroGoalDbId());
	}

	public BigDecimal getFat() {
		return goal.getFat();
	}
	
	public String
	getFatString() {
		return goal.getFat().setScale(0, RoundingMode.HALF_UP).toString();
	}
	
	public String
	getFatPercentageString() throws TorqueException, ObjectNotFoundException {
		
		BigDecimal fat_goal = this.getParent().getFat();
		if (fat_goal.compareTo(BigDecimal.ZERO) == 0) {
			return "0";
		}
		
		BigDecimal perc = goal.getFat().multiply(MacroGoalDaily.ONE_HUNDRED).divide(fat_goal, 2, RoundingMode.HALF_UP);
		if (perc.compareTo(MacroGoalDaily.ONE_HUNDRED) == 1) {
			return "100";
		}
		return perc.setScale(0, RoundingMode.HALF_UP).toString();
	}
	
	public String
	getFatCaloriesString() {
		return goal.getFat().multiply(MacroGoal.CALORIES_PER_GRAM_OF_FAT).setScale(0, RoundingMode.HALF_UP).toString();
	}

	public void setFat(BigDecimal fat) {
		goal.setFat(fat);
	}

	public BigDecimal getCarbs() {
		return goal.getCarbs();
	}
	
	public String
	getCarbsString() {
		return goal.getCarbs().setScale(0, RoundingMode.HALF_UP).toString();
	}
	
	public String
	getCarbsPercentageString() throws TorqueException, ObjectNotFoundException {
		
		BigDecimal carbs_goal = this.getParent().getCarbs();
		if (carbs_goal.compareTo(BigDecimal.ZERO) == 0) {
			return "0";
		}
		
		BigDecimal perc = goal.getCarbs().multiply(MacroGoalDaily.ONE_HUNDRED).divide(carbs_goal, 2, RoundingMode.HALF_UP);
		if (perc.compareTo(MacroGoalDaily.ONE_HUNDRED) == 1) {
			return "100";
		}
		return perc.setScale(0, RoundingMode.HALF_UP).toString();
	}
	
	public String
	getCarbsCaloriesString() {
		return goal.getCarbs().multiply(MacroGoal.CALORIES_PER_GRAM_OF_CARB).setScale(0, RoundingMode.HALF_UP).toString();
	}

	public void setCarbs(BigDecimal carbs) {
		goal.setCarbs(carbs);
	}

	public BigDecimal getProtein() {
		return goal.getProtein();
	}
	
	public String
	getProteinString() {
		return goal.getProtein().setScale(0, RoundingMode.HALF_UP).toString();
	}
	
	public String
	getProteinPercentageString() throws TorqueException, ObjectNotFoundException {
		
		BigDecimal protein_goal = this.getParent().getProtein();
		if (protein_goal.compareTo(BigDecimal.ZERO) == 0) {
			return "0";
		}
		
		BigDecimal perc = goal.getProtein().multiply(MacroGoalDaily.ONE_HUNDRED).divide(protein_goal, 2, RoundingMode.HALF_UP);
		if (perc.compareTo(MacroGoalDaily.ONE_HUNDRED) == 1) {
			return "100";
		}
		return perc.setScale(0, RoundingMode.HALF_UP).toString();
	}
	
	public String
	getProteinCaloriesString() {
		return goal.getProtein().multiply(MacroGoal.CALORIES_PER_GRAM_OF_PROTEIN).setScale(0, RoundingMode.HALF_UP).toString();
	}

	public void setProtein(BigDecimal protein) {
		goal.setProtein(protein);
	}
	
	public void
	recalculateMacros() throws TorqueException, IllegalValueException, Exception {
		BigDecimal fat_to_add = BigDecimal.ZERO;
		BigDecimal carbs_to_add = BigDecimal.ZERO;
		BigDecimal protein_to_add = BigDecimal.ZERO;
		Iterator itr = this.getEntries().iterator();
		while (itr.hasNext()) {
			MacroGoalEntry entry_obj = (MacroGoalEntry)itr.next();
			fat_to_add = fat_to_add.add(entry_obj.getFat());
			carbs_to_add = carbs_to_add.add(entry_obj.getCarbs());
			protein_to_add = protein_to_add.add(entry_obj.getProtein());
		}
		this.setFat(fat_to_add);
		this.setCarbs(carbs_to_add);
		this.setProtein(protein_to_add);
		this.calculateCalories();
		this.save();
	}
	
	public MacroGoalEntry
	addEntryByGrams(String _entryName, BigDecimal _fat, BigDecimal _carbs, BigDecimal _protein, BigDecimal _grams, BigDecimal _packageServingSize, String _servingSizeUnitFromRadio, Food _fud, Date _entryDate) throws TorqueException, IllegalValueException, Exception {
		MacroGoalEntry entry = new MacroGoalEntry();
		entry.setName(_entryName);
		BigDecimal fat_to_add = this.getCalculatedAmount(_packageServingSize, _fat, _grams);
		BigDecimal carbs_to_add = this.getCalculatedAmount(_packageServingSize, _carbs, _grams);
		BigDecimal protein_to_add = this.getCalculatedAmount(_packageServingSize, _protein, _grams);
		entry.setFat(fat_to_add);
		entry.setCarbs(carbs_to_add);
		entry.setProtein(protein_to_add);
		//entry.setEntryDate(new Date());
		entry.setEntryDate(_entryDate);
		entry.setMacroGoalDaily(this);
		entry.setEntryType(MacroGoalEntry.GRAMS_TYPE);
		entry.setEntryAmount(_grams);
		
		entry.setServingSize(_packageServingSize);
		if (_servingSizeUnitFromRadio.equals("grams")) {
			entry.setServingSizeType(Food.GRAMS_SERVING_SIZE_TYPE);
		} else if (_servingSizeUnitFromRadio.equals("ml")) {
			entry.setServingSizeType(Food.ML_SERVING_SIZE_TYPE);
		} else {
			
			// amount consumed was added by grams, the serving size was entered in ounces
			
			entry.setServingSizeType(Food.OZ_SERVING_SIZE_TYPE);
		}
		
		BigDecimal calories_to_add = entry.calculateCalories();
		
		if (_fud != null) {
			entry.setFood(_fud);
		}
		
		entry.save();
		this.invalidate();
		
		this.recalculateMacros();
		
		return entry;
	}
	
	public MacroGoalEntry
	addEntryByServing(String _entryName, BigDecimal _fat, BigDecimal _carbs, BigDecimal _protein, BigDecimal _servings, BigDecimal _packageServingSize, String _servingSizeUnitFromRadio, Food _fud, Date _entryDate) throws TorqueException, IllegalValueException, Exception  {
		MacroGoalEntry entry = new MacroGoalEntry();
		entry.setName(_entryName);
		//entry.setEntryDate(new Date());
		entry.setEntryDate(_entryDate);
		entry.setMacroGoalDaily(this);
		entry.setEntryType(MacroGoalEntry.SERVINGS_TYPE);
		entry.setEntryAmount(_servings);
		
		entry.setServingSize(_packageServingSize);
		if (_servingSizeUnitFromRadio.equals("grams")) {
			entry.setServingSizeType(Food.GRAMS_SERVING_SIZE_TYPE);
			
			entry.setFat(this.getCalculatedAmount(BigDecimal.ONE, _fat, _servings));
			entry.setCarbs(this.getCalculatedAmount(BigDecimal.ONE, _carbs, _servings));
			entry.setProtein(this.getCalculatedAmount(BigDecimal.ONE, _protein, _servings));
			
		} else if (_servingSizeUnitFromRadio.equals("ml")) {
			entry.setServingSizeType(Food.ML_SERVING_SIZE_TYPE);
			
			entry.setFat(this.getCalculatedAmount(BigDecimal.ONE, _fat, _servings));
			entry.setCarbs(this.getCalculatedAmount(BigDecimal.ONE, _carbs, _servings));
			entry.setProtein(this.getCalculatedAmount(BigDecimal.ONE, _protein, _servings));
			
		} else {
			entry.setServingSizeType(Food.OZ_SERVING_SIZE_TYPE);
			
			entry.setFat(this.getCalculatedAmount(BigDecimal.ONE, _fat, _servings));
			entry.setCarbs(this.getCalculatedAmount(BigDecimal.ONE, _carbs, _servings));
			entry.setProtein(this.getCalculatedAmount(BigDecimal.ONE, _protein, _servings));
		}
		
		entry.calculateCalories();
		
		if (_fud != null) {
			entry.setFood(_fud);
		}
		entry.save();
		this.invalidate();
		
		this.recalculateMacros();
		
		return entry;
	}
	
	public MacroGoalEntry
	addEntryByOunces(String _entryName, BigDecimal _fat, BigDecimal _carbs, BigDecimal _protein, BigDecimal _oz, BigDecimal _packageServingSize, String _servingSizeUnitFromRadio, Food _fud, Date _entryDate) throws TorqueException, IllegalValueException, Exception {
		
		MacroGoalEntry entry = new MacroGoalEntry();
		entry.setName(_entryName);
		
		//entry.setEntryDate(new Date());
		entry.setEntryDate(_entryDate);
		entry.setMacroGoalDaily(this);
		entry.setEntryType(MacroGoalEntry.OUNCES_TYPE);
		entry.setEntryAmount(_oz);
		
		entry.setServingSize(_packageServingSize);
		if (_servingSizeUnitFromRadio.equals("grams")) {
			entry.setServingSizeType(Food.GRAMS_SERVING_SIZE_TYPE);
			
			// amount consumed was added by ounce, the serving size was entered in grams
			
			BigDecimal oz_to_grams = _oz.multiply(Food.grams_per_oz, Food.conversion_context);
			
			entry.setFat(this.getCalculatedAmount(_packageServingSize, _fat, oz_to_grams)); // always store entry in grams
			entry.setCarbs(this.getCalculatedAmount(_packageServingSize, _carbs, oz_to_grams)); // always store entry in grams
			entry.setProtein(this.getCalculatedAmount(_packageServingSize, _protein, oz_to_grams)); // always store entry in grams
			
		} else if (_servingSizeUnitFromRadio.equals("ml")) {
			entry.setServingSizeType(Food.ML_SERVING_SIZE_TYPE);
			
			// amount consumed was added by ounce, the serving size was entered in grams
			
			BigDecimal oz_to_grams = _oz.multiply(Food.grams_per_oz, Food.conversion_context);
			
			entry.setFat(this.getCalculatedAmount(_packageServingSize, _fat, oz_to_grams)); // always store entry in grams
			entry.setCarbs(this.getCalculatedAmount(_packageServingSize, _carbs, oz_to_grams)); // always store entry in grams
			entry.setProtein(this.getCalculatedAmount(_packageServingSize, _protein, oz_to_grams)); // always store entry in grams
			
		} else {
			entry.setServingSizeType(Food.OZ_SERVING_SIZE_TYPE);
			
			// amount consumed was added by ounce, the serving size was entered in ounces - convert package serving size from ounces to grams for storage
			
			BigDecimal package_serving_size_to_grams = _packageServingSize.multiply(Food.grams_per_oz, Food.conversion_context);
			BigDecimal oz_to_grams = _oz.multiply(Food.grams_per_oz, Food.conversion_context);
			
			entry.setFat(this.getCalculatedAmount(package_serving_size_to_grams, _fat, oz_to_grams)); // always store entry in grams
			entry.setCarbs(this.getCalculatedAmount(package_serving_size_to_grams, _carbs, oz_to_grams)); // always store entry in grams
			entry.setProtein(this.getCalculatedAmount(package_serving_size_to_grams, _protein, oz_to_grams)); // always store entry in grams
		}
		
		entry.calculateCalories();
		
		if (_fud != null) {
			entry.setFood(_fud);
		}
		
		entry.save();
		this.invalidate();
		
		this.recalculateMacros();
		
		return entry;
	}
	
	private BigDecimal
	getCalculatedAmount(BigDecimal _packageServingSize, BigDecimal _packageAmount, BigDecimal _actualServingSize) throws IllegalValueException {
		//return _actualServingSize.multiply(_packageAmount, Food.return_context).divide(_packageServingSize, Food.return_context);
		/*
		System.out.println("_packageServingSize >" + _packageServingSize);
		System.out.println("_packageAmount >" + _packageAmount);
		System.out.println("_actualServingSize >" + _actualServingSize);
		*/
		
		if (_packageServingSize == null || _packageServingSize.compareTo(BigDecimal.ZERO) == 0) {
			throw new IllegalValueException("Please specify package serving size.");
		}
		
		return _actualServingSize.multiply(_packageAmount).divide(_packageServingSize, 2, RoundingMode.HALF_UP);
	}
	
	
	public MacroGoalEntry
	addEntryByGrams(Food _food, BigDecimal _grams) throws TorqueException, IllegalValueException, Exception {
		MacroGoalEntry entry = new MacroGoalEntry();
		entry.setName(_food.getNameString());
		entry.setFat(_food.getFatPerGram(_grams));
		entry.setCarbs(_food.getCarbsPerGram(_grams));
		entry.setProtein(_food.getProteinPerGram(_grams));
		entry.setEntryDate(new Date());
		entry.setMacroGoalDaily(this);
		entry.setEntryType(MacroGoalEntry.GRAMS_TYPE);
		entry.setEntryAmount(_grams);
		entry.calculateCalories();
		entry.save();
		this.invalidate();
		
		this.recalculateMacros();
		
		return entry;
	}
	
	public void
	addEntryByOunces(Food _food, BigDecimal _oz) throws TorqueException, IllegalValueException, Exception {
		MacroGoalEntry entry = new MacroGoalEntry();
		entry.setName(_food.getNameString());
		entry.setFat(_food.getFatPerOunce(_oz));
		entry.setCarbs(_food.getCarbsPerOunce(_oz));
		entry.setProtein(_food.getProteinPerOunce(_oz));
		entry.setEntryDate(new Date());
		entry.setMacroGoalDaily(this);
		entry.setEntryType(MacroGoalEntry.OUNCES_TYPE);
		entry.setEntryAmount(_oz);
		entry.calculateCalories();
		entry.save();
		this.invalidate();
		
		this.recalculateMacros();
	}
	
	public void
	addEntryByServing(Food _food, BigDecimal _servings) {
		
	}

    public long
    getId() {
		return goal.getMacroGoalDailyDbId();
    }

    public String
    getLabel() throws TorqueException, ObjectNotFoundException {
		
		String goalName = this.getParent().getNameString();
		
		/*
		WeightPreferences preferences = WeightPreferences.getWeightPreference(this.getParent().getPerson());
		*/
		
		String nowStr = CUBean.getUserDateString(new Date());
		String entryDateStr = this.getEntryDateString();
		if (nowStr.equals(entryDateStr)) {
			if (goalName.isEmpty()) {
				return "Today, " + entryDateStr;
			} else {
				return goalName + " - Today, " + entryDateStr;
			}
		}
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		String yesterdayStr = CUBean.getUserDateString(yesterday.getTime());
		if (yesterdayStr.equals(entryDateStr)) {
			if (goalName.isEmpty()) {
				return "Yesterday, " + entryDateStr;
			} else {
				return this.getParent().getNameString() + " - Yesterday, " + entryDateStr;
			}
		}
		if (goalName.isEmpty()) {
			return  CUBean.getUserDateString(goal.getEntryDate(), "EEEE, MM/dd/yy");
		} else {
			return  goalName + " - " + CUBean.getUserDateString(goal.getEntryDate(), "EEEE, MM/dd/yy");
		}
		
    }
	
	public void
	setMacroGoal(MacroGoal _goal) throws TorqueException {
		goal.setMacroGoalDbId(_goal.getId());
	}
	
	public void
	setEntryDate(Date _dt) {
		goal.setEntryDate(_dt);
	}

	public Date getEntryDate() {
		return goal.getEntryDate();
	}

	public String getEntryDateString() {
		return CUBean.getUserDateString(goal.getEntryDate());
	}
    
    public long getEntryDateUnixTimestamp() {
		return this.goal.getEntryDate().getTime() / 1000;
    }

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		if (goal.getEntryDate() == null) {
			goal.setEntryDate(new Date());
		}
		goal.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		goal.save();
    }
	
	public Vector
	getEntries() throws TorqueException {
		if (entries == null) {
			entries = new Vector();
			Criteria crit = new Criteria();
			crit.add(MacroGoalEntryDbPeer.MACRO_GOAL_DAILY_DB_ID, this.getId());
			crit.addDescendingOrderByColumn(MacroGoalEntryDbPeer.ENTRY_DATE);
			Iterator itr = MacroGoalEntryDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				entries.addElement(MacroGoalEntry.getMacroGoalEntry((MacroGoalEntryDb)itr.next()));
			}
			
		}
		return entries;
	}
	
	public void
	invalidate() {
		entries = null;
	}
	
	public boolean
	hasEntries() throws TorqueException {
		this.getEntries();
		return !entries.isEmpty();
	}
	
	
}
