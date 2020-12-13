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

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;



/**
 *
 * @author marlo
 */
public class
WeightGoal
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,WeightGoal> hash = new HashMap<Integer,WeightGoal>();
	
	private static BigDecimal one_hundred = new BigDecimal(100);

    // CLASS METHODS

    public static WeightGoal
    getWeightGoal(int _id)
		throws TorqueException, ObjectNotFoundException {
		
		WeightGoal goal_obj = (WeightGoal)hash.get(_id);
		if (goal_obj == null) {
			Criteria crit = new Criteria();
			crit.add(WeightGoalDbPeer.WEIGHT_GOAL_DB_ID, _id);
			List objList = WeightGoalDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate goal with id: " + _id);
			}

			goal_obj = WeightGoal.getWeightGoal((WeightGoalDb)objList.get(0));
		}

		return goal_obj;
    }

    private static WeightGoal
    getWeightGoal(WeightGoalDb _obj) throws TorqueException {
		
		WeightGoal goal_obj = (WeightGoal)hash.get(_obj.getWeightGoalDbId());
		if (goal_obj == null) {
			goal_obj = new WeightGoal(_obj);
			hash.put(_obj.getWeightGoalDbId(), goal_obj);
		}

		return goal_obj;
    }
	
	public static boolean
	hasCurrentWeightGoal(UKOnlinePersonBean _person) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(WeightGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(WeightGoalDbPeer.IS_ACTIVE, (short)1);
		return !WeightGoalDbPeer.doSelect(crit).isEmpty();
	}

    public static WeightGoal
    getCurrentWeightGoal(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		Criteria crit = new Criteria();
		crit.add(WeightGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(WeightGoalDbPeer.IS_ACTIVE, (short)1);
		System.out.println("getCurrentWeightGoal crit >" + crit.toString());
		List objList = WeightGoalDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			WeightGoal new_goal = new WeightGoal();
			new_goal.setPerson(_person);
			new_goal.activate();
			new_goal.setGoal("0.00");
			
			new_goal.setType(WeightGoalType.getWeightGoalForType(WeightGoalType.MAINTENANCE_TYPE));
			
			/*
			new_goal.setGoalType(WeightGoal.GRAMS_TYPE);
			*/
			new_goal.setCreateOrModifyPerson(_person);
			new_goal.save();
			hash.put(new_goal.getId(), new_goal);
			return new_goal;
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unique current macro goal for " + _person.getLabel());
		}

		return WeightGoal.getWeightGoal((WeightGoalDb)objList.get(0));
    }

	/*
    public static WeightGoal
    getCurrentWeightGoal(UKOnlinePersonBean _person, BigDecimal _fat, BigDecimal _carbs, BigDecimal _protein)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		Criteria crit = new Criteria();
		crit.add(WeightGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(WeightGoalDbPeer.GOAL_TYPE, WeightGoal.GRAMS_TYPE);
		crit.add(WeightGoalDbPeer.FAT, _fat);
		crit.add(WeightGoalDbPeer.CARBS, _carbs);
		crit.add(WeightGoalDbPeer.PROTEIN, _protein);
		crit.add(WeightGoalDbPeer.IS_ACTIVE, (short)1);
		List objList = WeightGoalDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			WeightGoal new_goal = new WeightGoal();
			new_goal.setPerson(_person);
			new_goal.activate();
			//new_goal.setCalories("0.00");
			new_goal.setFat(_fat);
			new_goal.setCarbs(_carbs);
			new_goal.setProtein(_protein);
			new_goal.setGoalType(WeightGoal.GRAMS_TYPE);
			new_goal.setCreateOrModifyPerson(_person);
			new_goal.save();
			hash.put(new_goal.getId(), new_goal);
			return new_goal;
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unique current macro goal for " + _person.getLabel());
		}

		return WeightGoal.getWeightGoal((WeightGoalDb)objList.get(0));
    }
	*/

    public static Vector
    getWeightGoals(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(WeightGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(WeightGoalDbPeer.IS_HIDDEN, (short)0);
		crit.addDescendingOrderByColumn(WeightGoalDbPeer.CREATION_DATE);
		Iterator itr = WeightGoalDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(WeightGoal.getWeightGoal((WeightGoalDb)itr.next()));
		}
		
		return vec;
    }
	

    // SQL

    /*

<table name="WEIGHT_GOAL_DB" idMethod="native">
	<column name="WEIGHT_GOAL_DB_ID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true"/>
	<column name="PERSON_ID" required="false" type="INTEGER"/>
    <column name="IS_ACTIVE" type="SMALLINT" default="1"/>
	<column name="GOAL_WEIGHT" scale="2" size="7" type="DECIMAL"/>
	
	<column name="NAME" required="false" size="200" type="VARCHAR"/>
	
    <column name="WEIGHT_GOAL_TYPE_DB_ID" required="false" type="INTEGER"/>
	
	<column name="CREATION_DATE" required="true" type="DATE"/>
	<column name="MODIFICATION_DATE" required="false" type="DATE"/>
	<column name="CREATE_PERSON_ID" required="true" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
	
    <foreign-key foreignTable="WEIGHT_GOAL_TYPE_DB">
		<reference local="WEIGHT_GOAL_TYPE_DB_ID" foreign="WEIGHT_GOAL_TYPE_DB_ID"/>
    </foreign-key>

    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
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

    private WeightGoalDb goal;
	private Vector entries;

    // CONSTRUCTORS

    public
    WeightGoal() {
		goal = new WeightGoalDb();
		isNew = true;
    }

    public
    WeightGoal(WeightGoalDb _obj) {
		goal = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public String
	getNameString() {
		String str = goal.getName();
		if (str == null) {
			return "";
		}
		return str;
	}
	
	public void
	setName(String _str) {
		goal.setName(_str);
	}
	
	public boolean
	equals(WeightGoal _g) {
		try {
			
			if (_g.getGoal() == null || this.getGoal() == null) {
				return false;
			}
			
			if (this.getGoal().compareTo(_g.getGoal()) != 0) {
				return false;
			}
			
			if (this.getType().getType() != _g.getType().getType()) {
				return false;
			}
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		return true;
	}
	
	public WeightEntry
	addEntry(Date _entryDate, BigDecimal _entryAmount, WeightPreferences _preferences) throws IllegalValueException, ObjectAlreadyExistsException, Exception {
		WeightEntry entry = new WeightEntry();
		entry.setWeightGoal(this);
		entry.setEntryDate(_entryDate);
		entry.setEntryAmount(_entryAmount);
		entry.setEntryUnitType(_preferences.getUnitType());
		entry.save();
		this.invalidate();
		return entry;
	}

    public int
    getId() {
		return goal.getWeightGoalDbId();
    }
	
    public String
    getLabel() {
		if (this.isNew()) {
			return "";
		}
		String name = goal.getName();
		if ((name != null) && !name.isEmpty()) {
			return name;
		}
		if (this.isActive()) {
			return "Active Goal created " + this.getCreationDateString();
		}
		
		return "Inactive Goal created " + this.getCreationDateString();
    }
	
	public void
	setGoal(String _goal) {
		System.out.println("setting weight goal >" + _goal);
		goal.setGoalWeight(new BigDecimal(_goal));
		try {
		System.out.println("set >" + this.getGoalString());
		} catch (Exception x) {
			
		}
	}
	
	public BigDecimal
	getGoal() {
		return goal.getGoalWeight();
	}
	
	public String
	getGoalString() throws IllegalValueException, ObjectAlreadyExistsException, Exception {
		if (goal.getGoalWeight() == null) {
			return "";
		}
		if (goal.getGoalWeight().compareTo(BigDecimal.ZERO) == 0) {
			return "";
		}
		return this.getStringFromBigDecimal(goal.getGoalWeight());
	}
	
	/*
	public short
	getGoalType() {
		return goal.getGoalType();
	}
	
	public String
	getGoalTypeString() {
		switch (goal.getGoalType()) {
			case WeightGoal.GRAMS_TYPE: return "Grams";
			case WeightGoal.PERCENTAGE_TYPE: return "Percentage";
		}
		return "[TYPE NOT FOUND]";
	}
	*/
	
	public UKOnlinePersonBean
	getPerson() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(goal.getPersonId());
	}
	
	public void
	setPerson(PersonBean _person) throws TorqueException {
		goal.setPersonId(_person.getId());
	}
	
	/*
	public void
	setGoalType(short _type) {
		goal.setGoalType(_type);
	}
	*/

	public Date getCreationDate() {
		return goal.getCreationDate();
	}

	public String getCreationDateString() {
		return CUBean.getUserDateString(goal.getCreationDate());
	}
    
    public long getCreationDateUnixTimestamp() {
		return this.goal.getCreationDate().getTime() / 1000;
    }

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		goal.setCreationDate(new Date());
		goal.save();
		
		this.invalidate();
    }
	
	public boolean
	isActive() {
		if (this.isNew()) {
			return false;
		}
		return (goal.getIsActive() == (short)1);
	}

	public void
	activate() {
		goal.setIsActive((short)1);
	}

	public void
	inActivate() {
		goal.setIsActive((short)0);
	}

	public void
	setActive(boolean _b)
		throws TorqueException {
		goal.setIsActive(_b ? (short)1 : (short)0);
	}
	
	public boolean
	isHidden() {
		if (this.isNew()) {
			return false;
		}
		return (goal.getIsHidden() == (short)1);
	}

	public void
	setHidden(boolean _b) {
		goal.setIsHidden(_b ? (short)1 : (short)0);
	}
	
	public void
	setType(WeightGoalType _type) throws TorqueException {
		goal.setWeightGoalTypeDbId(_type.getId());
	}
	
	public WeightGoalType
	getType() throws TorqueException, ObjectNotFoundException {
		return WeightGoalType.getWeightGoalType(goal.getWeightGoalTypeDbId());
	}

    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew()) {
			goal.setCreatePersonId(_person.getId());
		} else {
			goal.setModifyPersonId(_person.getId());
		}
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		goal.setModificationDate(new Date());
		goal.save();
		
		this.invalidate();
    }
	
	private String
	getStringFromBigDecimal(BigDecimal _bd) {
		if (_bd == null) {
			return "";
		}
		String str = _bd.setScale(1, RoundingMode.HALF_UP).toString();
		int index = str.indexOf(".0");
		if (index > -1) {
			return str.substring(0, index);
		}
		return str;
	}
	
	private BigDecimal goalPerc = null;
	public String
	getGoalPercString(String _units) throws TorqueException {
		if (goalPerc == null) {
			//this.getChangeDaysString(-1, "");
			this.getChangeStrings(_units);
		}
		if (goalPerc == null) {
			return "0";
		}
		return goalPerc.setScale(0, RoundingMode.HALF_UP).toString();
	}
	
	private String lastChangeStr = null;
	private String weekChangeStr = null;
	private String monthChangeStr = null;
	private String yearChangeStr = null;
	private String overallChangeStr = null;
	
	public String
	getLastChangeString(String _units) throws TorqueException {
		if (lastChangeStr == null) {
			this.getChangeStrings(_units);
		}
		return lastChangeStr;
	}
	
	public String
	getWeekChangeString(String _units) throws TorqueException {
		if (weekChangeStr == null) {
			this.getChangeStrings(_units);
		}
		return weekChangeStr;
	}
	
	public String
	getMonthChangeString(String _units) throws TorqueException {
		if (monthChangeStr == null) {
			this.getChangeStrings(_units);
		}
		return monthChangeStr;
	}
	
	public String
	getYearChangeString(String _units) throws TorqueException {
		if (yearChangeStr == null) {
			this.getChangeStrings(_units);
		}
		return yearChangeStr;
	}
	
	public String
	getOverallChangeString(String _units) throws TorqueException {
		if (overallChangeStr == null) {
			this.getChangeStrings(_units);
		}
		return overallChangeStr;
	}
	
	private void
	getChangeStrings(String _units) throws TorqueException {
		this.getEntries(0);
		
		WeightEntry latestEntry = null;
		Iterator itr = entries.iterator(); // changed back to iterator 2/27/19
		while (itr.hasNext()) {
			//WeightEntry entry = (WeightEntry)this.entries.elementAt(i);
			WeightEntry entry = (WeightEntry)itr.next();
			if (latestEntry == null) {
				latestEntry = entry;
			} else {
				if (lastChangeStr == null) {
					BigDecimal diff = latestEntry.getEntryAmount().subtract(entry.getEntryAmount(), Food.return_context);
					if (diff.compareTo(BigDecimal.ZERO) == 1) {
						lastChangeStr = "+" + this.getStringFromBigDecimal(diff) + " " + _units;
					} else {
						lastChangeStr = this.getStringFromBigDecimal(diff) + " " + _units;
					}
					
				}
				if (weekChangeStr == null) {
					// if the difference in dates is greater than a week
					if (this.daysBetween(latestEntry.getEntryDate(), entry.getEntryDate()) > 6) {
						BigDecimal diff = latestEntry.getEntryAmount().subtract(entry.getEntryAmount(), Food.return_context);
						if (diff.compareTo(BigDecimal.ZERO) == 1) {
							weekChangeStr = "+" + this.getStringFromBigDecimal(diff) + " " + _units;
						} else {
							weekChangeStr = this.getStringFromBigDecimal(diff) + " " + _units;
						}
					}
				}
				if (monthChangeStr == null) {
					// if the difference in dates is greater than a week
					if (this.daysBetween(latestEntry.getEntryDate(), entry.getEntryDate()) > 29) {
						BigDecimal diff = latestEntry.getEntryAmount().subtract(entry.getEntryAmount(), Food.return_context);
						if (diff.compareTo(BigDecimal.ZERO) == 1) {
							monthChangeStr = "+" + this.getStringFromBigDecimal(diff) + " " + _units;
						} else {
							monthChangeStr = this.getStringFromBigDecimal(diff) + " " + _units;
						}
					}
				}
				if (yearChangeStr == null) {
					// if the difference in dates is greater than a week
					if (this.daysBetween(latestEntry.getEntryDate(), entry.getEntryDate()) > 364) {
						BigDecimal diff = latestEntry.getEntryAmount().subtract(entry.getEntryAmount(), Food.return_context);
						if (diff.compareTo(BigDecimal.ZERO) == 1) {
							yearChangeStr = "+" + this.getStringFromBigDecimal(diff) + " " + _units;
						} else {
							yearChangeStr = this.getStringFromBigDecimal(diff) + " " + _units;
						}
					}
				}
				
				if (!itr.hasNext()) {
					BigDecimal diff = latestEntry.getEntryAmount().subtract(entry.getEntryAmount(), Food.return_context);
					if (diff.compareTo(BigDecimal.ZERO) == 1) {
						overallChangeStr = "+" + this.getStringFromBigDecimal(diff) + " " + _units;
					} else {
						overallChangeStr = this.getStringFromBigDecimal(diff) + " " + _units;
					}
					
					//System.out.println("    first_entry_amount >" + this.getStringFromBigDecimal(first_entry_amount));
					//System.out.println("    latest_entry_amount >" + this.getStringFromBigDecimal(latest_entry_amount));

					//BigDecimal start_minus_current = first_entry_amount.subtract(latest_entry_amount, Food.return_context);
					
					try {
						BigDecimal latest_minus_goal = this.getGoal().subtract(entry.getEntryAmount(), Food.return_context);

						//System.out.println("    start_minus_current >" + this.getStringFromBigDecimal(start_minus_current));
						//System.out.println("    diff >" + this.getStringFromBigDecimal(diff));
						//System.out.println("    latest_minus_goal >" + this.getStringFromBigDecimal(latest_minus_goal));

						if (latest_minus_goal.compareTo(BigDecimal.ZERO) == 0) {
							goalPerc = BigDecimal.ZERO;
						} else {
							goalPerc = one_hundred.multiply(diff, Food.return_context).divide(latest_minus_goal, Food.return_context);
							
							if (goalPerc.compareTo(one_hundred) == 1) {
								goalPerc = one_hundred;
							}
							
						}
					} catch (Exception x) {
						x.printStackTrace();
					}
				
				}
				
			}
		}
		
		if (lastChangeStr == null) {
			lastChangeStr = "";
		}
		if (weekChangeStr == null) {
			weekChangeStr = "";
		}
		if (monthChangeStr == null) {
			monthChangeStr = "";
		}
		if (yearChangeStr == null) {
			yearChangeStr = "";
		}
		if (overallChangeStr == null) {
			overallChangeStr = "";
		}
		
	}
	
	
	public long daysBetween(Date _d1, Date _d2) {
		
		long diff = _d1.getTime() - _d2.getTime();
		//System.out.println ("    Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
		
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		
		/*
		Calendar dayOne = (Calendar) day1.clone(),
				dayTwo = (Calendar) day2.clone();

		if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
			return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
		} else {
			if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
				//swap them
				Calendar temp = dayOne;
				dayOne = dayTwo;
				dayTwo = temp;
			}
			int extraDays = 0;

			int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

			while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
				dayOne.add(Calendar.YEAR, -1);
				// getActualMaximum() important for leap years
				extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
			}

			return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
		}
		*/
	}
	
	
	/*
	public String
	getChangeDaysString(int _days, String _units) throws TorqueException {
		System.out.println("getChangeDaysString >" + _days);
		this.getEntries(_days);
		Calendar latest_entry_date = null;
		BigDecimal latest_entry_amount = null;
		Calendar entry_after_days_date = null;
		BigDecimal first_entry_amount = null;
		// I'm getting entries in descending order, why am I iterating in reverse order???
		//for (int i = (entries.size() - 1); i > -1; i--) {
		Iterator itr = entries.iterator(); // changed back to iterator 2/27/19
		while (itr.hasNext()) {
			//WeightEntry entry = (WeightEntry)this.entries.elementAt(i);
			WeightEntry entry = (WeightEntry)itr.next();
			
			System.out.println("    entry >" + entry.getEntryDateString());
			System.out.println("    latest_entry_amount >" + latest_entry_amount);
			if (_days > 0) {
				if (latest_entry_amount == null) {
					//latest_entry_date = entry.getEntryDate();
					latest_entry_date = Calendar.getInstance();
					latest_entry_date.setTime(entry.getEntryDate());
					entry_after_days_date = Calendar.getInstance();
					entry_after_days_date.setTime(entry.getEntryDate());
					entry_after_days_date.add(Calendar.DATE, (_days - 1) );
					latest_entry_amount = entry.getEntryAmount();
				} else {
					Calendar entry_date = Calendar.getInstance();
					entry_date.setTime(entry.getEntryDate());
					System.out.println("    entry_date >" + CUBean.getUserDateString(entry_date.getTime()));
					System.out.println("    entry_after_days_date >" + CUBean.getUserDateString(entry_after_days_date.getTime()));
					boolean is_entry_date_after = entry_date.after(entry_after_days_date);
					System.out.println("    is_entry_date_after >" + is_entry_date_after);
					if (is_entry_date_after) {
						BigDecimal diff = entry.getEntryAmount().subtract(latest_entry_amount, Food.return_context);
						return this.getStringFromBigDecimal(diff) + " " + _units;
					}
				}
			} else {
				if (first_entry_amount == null) {
					first_entry_amount = entry.getEntryAmount();
				} else {
					latest_entry_amount = entry.getEntryAmount();
				}
			}
		}
		if (_days > 0) {
			return "[NOT FOUND]";
		} else {
			if (latest_entry_amount == null || first_entry_amount == null) {
				return "[NOT FOUND]";
			} else {
				
				System.out.println("    first_entry_amount >" + this.getStringFromBigDecimal(first_entry_amount));
				System.out.println("    latest_entry_amount >" + this.getStringFromBigDecimal(latest_entry_amount));
				
				BigDecimal start_minus_current = first_entry_amount.subtract(latest_entry_amount, Food.return_context);
				BigDecimal start_minus_goal = first_entry_amount.subtract(this.getGoal(), Food.return_context);
				
				System.out.println("    start_minus_current >" + this.getStringFromBigDecimal(start_minus_current));
				System.out.println("    start_minus_goal >" + this.getStringFromBigDecimal(start_minus_goal));
				
				if (start_minus_goal.compareTo(BigDecimal.ZERO) == 0) {
					goalPerc = BigDecimal.ZERO;
				} else {
					goalPerc = one_hundred.multiply(start_minus_current, Food.return_context).divide(start_minus_goal, Food.return_context);
				}
				
				BigDecimal diff = latest_entry_amount.subtract(first_entry_amount, Food.return_context);
				return this.getStringFromBigDecimal(diff) + " " + _units; 
			}
		}
		
	}
	*/
	
	public String
	getCurrentWeightString() throws TorqueException {
		Vector vec = this.getEntries(1);
		if (vec.isEmpty()) {
			return "[NOT FOUND]";
		}
		WeightEntry entry = (WeightEntry)vec.elementAt(0);
		return this.getStringFromBigDecimal(entry.getEntryAmount()) + " " + entry.getEntryUnitTypeString();
	}
	
	public Vector
	getEntries(int _limit) throws TorqueException {
		if (entries == null) {
			entries = new Vector();
			Criteria crit = new Criteria();
			crit.add(WeightEntryDbPeer.WEIGHT_GOAL_DB_ID, this.getId());
			crit.addDescendingOrderByColumn(WeightEntryDbPeer.ENTRY_DATE);
			/*
			if (_limit > 0) {
				crit.setLimit(_limit);
			}
			*/
			Iterator itr = WeightEntryDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				entries.addElement(WeightEntry.getWeightEntry((WeightEntryDb)itr.next()));
			}
		}
		if (_limit > 0) {
			Vector vec = new Vector();
			for (int i = 0; i < entries.size() && i < _limit; i++) {
				vec.addElement(entries.elementAt(i));
			}
			return vec;
		} else {
			return entries;
		}
		
	}
	
	public void
	invalidate() {
		entries = null;
		
		lastChangeStr = null;
		weekChangeStr = null;
		monthChangeStr = null;
		yearChangeStr = null;
		overallChangeStr = null;
	}
	
	
}