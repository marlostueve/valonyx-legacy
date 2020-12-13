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
WeightEntry
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Long,WeightEntry> hash = new HashMap<Long,WeightEntry>();

    // CLASS METHODS

    public static void
    delete(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		hash.remove(_id);

		Criteria crit = new Criteria();
		crit.add(WeightEntryDbPeer.WEIGHT_ENTRY_DB_ID, _id);
		WeightEntryDbPeer.doDelete(crit);
    }

    public static WeightEntry
    getWeightEntry(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		WeightEntry entry_obj = (WeightEntry)hash.get(_id);
		if (entry_obj == null) {
			Criteria crit = new Criteria();
			crit.add(WeightEntryDbPeer.WEIGHT_ENTRY_DB_ID, _id);
			List objList = WeightEntryDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate entry with id: " + _id);
			}

			entry_obj = WeightEntry.getWeightEntry((WeightEntryDb)objList.get(0));
		}

		return entry_obj;
    }

    public static WeightEntry
    getWeightEntry(WeightEntryDb _obj) throws TorqueException {
		
		WeightEntry entry_obj = (WeightEntry)hash.get(_obj.getWeightEntryDbId());
		if (entry_obj == null) {
			entry_obj = new WeightEntry(_obj);
			hash.put(_obj.getWeightEntryDbId(), entry_obj);
		}

		return entry_obj;
    }

    public static Vector
    getEntries(WeightGoal _goal)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(WeightEntryDbPeer.WEIGHT_GOAL_DB_ID, _goal.getId());
		crit.addAscendingOrderByColumn(WeightEntryDbPeer.ENTRY_DATE);
		Iterator itr = WeightEntryDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(WeightEntry.getWeightEntry((WeightEntryDb)itr.next()));
		}
		
		return vec;
    }

    public static Vector
    getEntries(PersonBean _person, int _field, int _count)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Calendar time_frame = Calendar.getInstance();
		time_frame.add(_field, _count);
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(WeightGoalDbPeer.WEIGHT_GOAL_DB_ID, WeightEntryDbPeer.WEIGHT_GOAL_DB_ID);
		crit.add(WeightGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(WeightEntryDbPeer.ENTRY_DATE, time_frame.getTime(), Criteria.GREATER_EQUAL);
		//crit.addDescendingOrderByColumn(WeightEntryDbPeer.ENTRY_DATE);
		crit.addAscendingOrderByColumn(WeightEntryDbPeer.ENTRY_DATE);
		Iterator itr = WeightEntryDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(WeightEntry.getWeightEntry((WeightEntryDb)itr.next()));
		}
		
		return vec;
    }

    public static boolean
    hasEntry(PersonBean _person)
		throws TorqueException {
		Criteria crit = new Criteria();
		crit.addJoin(WeightGoalDbPeer.WEIGHT_GOAL_DB_ID, WeightEntryDbPeer.WEIGHT_GOAL_DB_ID);
		crit.add(WeightGoalDbPeer.PERSON_ID, _person.getId());
		List l = WeightEntryDbPeer.doSelect(crit);
		return !l.isEmpty();
    }

    public static WeightEntry
    getLastEntry(PersonBean _person)
		throws TorqueException, ObjectNotFoundException {
		
		Criteria crit = new Criteria();
		crit.addJoin(WeightGoalDbPeer.WEIGHT_GOAL_DB_ID, WeightEntryDbPeer.WEIGHT_GOAL_DB_ID);
		crit.add(WeightGoalDbPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(WeightEntryDbPeer.ENTRY_DATE);
		List l = WeightEntryDbPeer.doSelect(crit);
		if (!l.isEmpty()) {
			return WeightEntry.getWeightEntry((WeightEntryDb)l.get(0));
		} else {
			throw new ObjectNotFoundException("Last weight entry not found for " + _person.getLabel());
		}
    }

    public static WeightEntry
    getEntryBefore(PersonBean _person, WeightEntry _entry)
		throws TorqueException, ObjectNotFoundException {
		
		Criteria crit = new Criteria();
		crit.addJoin(WeightGoalDbPeer.WEIGHT_GOAL_DB_ID, WeightEntryDbPeer.WEIGHT_GOAL_DB_ID);
		crit.add(WeightGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(WeightEntryDbPeer.ENTRY_DATE, _entry.getEntryDate(), Criteria.LESS_THAN);
		crit.addDescendingOrderByColumn(WeightEntryDbPeer.ENTRY_DATE);
		List l = WeightEntryDbPeer.doSelect(crit);
		if (!l.isEmpty()) {
			return WeightEntry.getWeightEntry((WeightEntryDb)l.get(0));
		} else {
			throw new ObjectNotFoundException("Previous weight entry not found for " + _person.getLabel());
		}
    }

    public static ArrayList<WeightEntry>
    getLastEntries(PersonBean _person, int _num)
		throws TorqueException, ObjectNotFoundException {
		
		ArrayList<WeightEntry> vec = new ArrayList();
		Criteria crit = new Criteria();
		crit.addJoin(WeightGoalDbPeer.WEIGHT_GOAL_DB_ID, WeightEntryDbPeer.WEIGHT_GOAL_DB_ID);
		crit.add(WeightGoalDbPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(WeightEntryDbPeer.ENTRY_DATE);
		crit.setLimit(_num);
		Iterator itr = WeightEntryDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.add(WeightEntry.getWeightEntry((WeightEntryDb)itr.next()));
		}
		return vec;
    }
	

    // SQL

    /*

<table name="WEIGHT_ENTRY_DB" idMethod="native">
	<column name="WEIGHT_ENTRY_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	<column name="WEIGHT_GOAL_DB_ID" required="true" type="INTEGER"/>
	
	<column name="ENTRY_AMOUNT" scale="2" size="7" type="DECIMAL"/>
	<column name="ENTRY_DATE" required="true" type="DATE"/>
	<column name="UNIT_TYPE" type="SMALLINT"/>
	<!-- 1 = pounds -->
	<!-- 2 = kgs -->

    <foreign-key foreignTable="WEIGHT_GOAL_DB">
		<reference local="WEIGHT_GOAL_DB_ID" foreign="WEIGHT_GOAL_DB_ID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private WeightEntryDb entry;

    // CONSTRUCTORS

    public
    WeightEntry() {
		entry = new WeightEntryDb();
		isNew = true;
    }

    public
    WeightEntry(WeightEntryDb _obj) {
		entry = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public WeightGoal
	getParent() throws TorqueException, ObjectNotFoundException {
		return WeightGoal.getWeightGoal(entry.getWeightGoalDbId());
	}

	/*
	public String getName() {
		return entry.getName();
	}

	public void setName(String name) {
		entry.setName(name);
	}
	*/
	
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
		return entry.getWeightEntryDbId();
    }

    public String
    getLabel() {
		String str = this.getEntryAmountString();
		if (str == null) {
			return "[NO ENTRY NAME]";
		}
		return str;
    }
	
	public void
	setWeightGoal(WeightGoal _goal) throws TorqueException {
		entry.setWeightGoalDbId(_goal.getId());
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
	setEntryUnitType(short _type) {
		entry.setUnitType(_type);
	}
	
	public short
	getEntryUnitType() {
		return entry.getUnitType();
	}
	
	public String
	getEntryUnitTypeString() {
		switch (entry.getUnitType()) {
				case WeightPreferences.LBS_TYPE: return "lbs";
				case WeightPreferences.KG_TYPE: return "kg";
			}
		return "[ENTRY UNIT NOT FOUND]";
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
	
}
