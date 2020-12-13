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

import org.apache.torque.*;
import org.apache.torque.util.Criteria;



/**
 *
 * @author marlo
 */
public class
TrainingPlanSet
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Long,TrainingPlanSet> hash = new HashMap<Long,TrainingPlanSet>();
	

    // CLASS METHODS
	
    public static void
    delete(long _id) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(TrainingPlanSetDbPeer.TRAINING_PLAN_SET_DB_ID, _id);
		TrainingPlanSetDbPeer.doDelete(crit);
		hash.remove(_id);
    }

    public static TrainingPlanSet
    getTrainingPlanSet(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		TrainingPlanSet plan_obj = (TrainingPlanSet)hash.get(_id);
		if (plan_obj == null) {
			Criteria crit = new Criteria();
			crit.add(TrainingPlanSetDbPeer.TRAINING_PLAN_SET_DB_ID, _id);
			List objList = TrainingPlanSetDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate set with id: " + _id);
			}

			plan_obj = TrainingPlanSet.getTrainingPlanSet((TrainingPlanSetDb)objList.get(0));
		}

		return plan_obj;
    }

    public static TrainingPlanSet
    getTrainingPlanSet(TrainingPlanSetDb _obj) throws TorqueException {
		
		TrainingPlanSet plan_obj = (TrainingPlanSet)hash.get(_obj.getTrainingPlanSetDbId());
		if (plan_obj == null) {
			plan_obj = new TrainingPlanSet(_obj);
			hash.put(_obj.getTrainingPlanSetDbId(), plan_obj);
		}

		return plan_obj;
    }
	
	public static TrainingPlanSet
	copy(PersonBean _copyPerson, TrainingPlanMove _parent, TrainingPlanSet _setToCopy) throws TorqueException, IllegalValueException, Exception {
		TrainingPlanSet copy = new TrainingPlanSet();
		copy.setParent(_parent); // should be set in the parent
		copy.setIsAMRAP(_setToCopy.isAMRAP());
		copy.setIsAMWAP(_setToCopy.isAMWAP());
		copy.setIsDropSet(_setToCopy.isDropSet());
		copy.setIsPerc1RM(_setToCopy.isPerc1RM());
		copy.setOneRepMaxPercentage(_setToCopy.getOneRepMaxPercentage());
		copy.setRPEGoal(_setToCopy.getRPEGoal());
		copy.save(); // saved here so that there's a mapping for the personal functions below
		
		System.out.println("REP GOAL MIN >" + _setToCopy.getRepGoalMin(_copyPerson));
		System.out.println("REP GOAL MAX >" + _setToCopy.getRepGoalMax(_copyPerson));
		copy.setRepGoalMin(_copyPerson, _setToCopy.getRepGoalMin(_copyPerson));
		copy.setRepGoalMax(_copyPerson, _setToCopy.getRepGoalMax(_copyPerson));
		copy.setSetNumber(_setToCopy.getSetNumber());
		copy.setWeekNumber(_setToCopy.getWeekNumber());
		System.out.println("WEIGHT GOAL >" + _setToCopy.getWeightGoal(_copyPerson));
		copy.setWeightGoal(_copyPerson, _setToCopy.getWeightGoal(_copyPerson));
		copy.save();
		
		return copy;
	}

	/*
    public static synchronized TrainingPlanSet
    maintainTrainingPlanSet(TrainingPlanMove _plan_move, short _move_number)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		Criteria crit = new Criteria();
		crit.add(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, _plan_workout.getId());
		crit.add(TrainingPlanExcerciseDbPeer.EXCERCISE_DB_ID, _move.getId());
		//crit.add(TrainingPlanExcerciseDbPeer.EXCERCISE_NUMBER, _move_number);
		List objList = TrainingPlanExcerciseDbPeer.doSelect(crit);
		
		if (objList.isEmpty()) {
			// maintain one
			TrainingPlanMove plan_move = new TrainingPlanMove();
			plan_move.setWorkout(_plan_workout);
			plan_move.setIsSuperSet(false); // default - not sure if this is useful
			plan_move.setMove(_move);
			plan_move.setMoveNumber(_move_number);
			plan_move.save();
			
			//plan_move.getMove().setCreateOrModifyPerson(_logged_in_person);
			//plan_move.getMove().save();
			_plan_workout.invalidate();
			return plan_move;
		}
		
		TrainingPlanMove plan_move = TrainingPlanMove.getTrainingPlanMove((TrainingPlanExcerciseDb)objList.get(0));
		if (plan_move.getMoveNumber() != _move_number) {
			plan_move.setMoveNumber(_move_number);
			plan_move.save();
		}
		
		return plan_move;
		
    }
	*/
	

    // SQL

    /*

<table name="TRAINING_PLAN_SET_DB" idMethod="native">
	<column name="TRAINING_PLAN_SET_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
    <column name="TRAINING_PLAN_EXCERCISE_DB_ID" primaryKey="false" required="true" type="BIGINT"/>
	
	<column name="SET_NUMBER" required="true" type="SMALLINT"/>
	<column name="IS_EXT" required="false" type="SMALLINT" default="0"/>
	<column name="IS_DROP_SET" required="false" type="SMALLINT" default="0"/>
	<column name="IS_AMRAP" required="false" type="SMALLINT" default="0"/>
	<column name="IS_AMWAP" required="false" type="SMALLINT" default="0"/>
	<column name="IS_1RM_PERC" required="false" type="SMALLINT" default="0"/>
	<column name="ONE_REP_MAX_PERC" required="false" scale="2" size="5" type="DECIMAL"/>
	
	<column name="WEEK_NUMBER" required="false" type="SMALLINT"/>
	
	<column name="REP_GOAL_MIN" required="false" type="SMALLINT"/>
	<column name="REP_GOAL_MAX" required="false" type="SMALLINT"/>
	<column name="WEIGHT_GOAL" required="false" scale="2" size="5" type="DECIMAL"/>

    <foreign-key foreignTable="TRAINING_PLAN_EXCERCISE_DB">
		<reference local="TRAINING_PLAN_EXCERCISE_DB_ID" foreign="TRAINING_PLAN_EXCERCISE_DB_ID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private TrainingPlanSetDb set;
	private HashMap<PersonBean, PersonTrainingPlanSetMapping> personSet;

    // CONSTRUCTORS

    public
    TrainingPlanSet() {
		set = new TrainingPlanSetDb();
		isNew = true;
    }

    public
    TrainingPlanSet(TrainingPlanSetDb _obj) {
		set = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public boolean
	equals(TrainingPlanSet _set, PersonBean _person) throws TorqueException, IllegalValueException {
		if (_set == null) {
			return false;
		}
		if (this.getRepGoalMin(_person) != _set.getRepGoalMin(_person)) {
			return false;
		}
		/* exclude max for now
		if (this.getRepGoalMax(_person) != _set.getRepGoalMax(_person)) {
			return false;
		}
		*/
		
		if (!this.getRPEGoal().equals(_set.getRPEGoal())) {
			return false;
		}
		
		if (this.isPerc1RM() && !_set.isPerc1RM()) {
			return false;
		}
		
		if (this.isPerc1RM()) {
			return this.getOneRepMaxPercentageString().equals(_set.getOneRepMaxPercentageString());
		} else {
			return this.getWeightGoalString(_person).equals(_set.getWeightGoalString(_person));
		}
		
	}

    public long
    getId() {
		return set.getTrainingPlanSetDbId();
    }

    public String
    getLabel(PersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, Exception {
		
		WeightPreferences preferences = WeightPreferences.getWeightPreference(this.getParent().getParent().getParent().getPerson());
		
		//BigDecimal weightGoal = set.getWeightGoal();
		BigDecimal weightGoal = this.getWeightGoal(_person);
		boolean displayWeightGoal = false;
		if (weightGoal != null) {
			displayWeightGoal = (weightGoal.compareTo(BigDecimal.ZERO) == 1);
		}
		
		String str = null;
		
		if ( this.getRepGoalMin(_person) > (short)0 && this.getRepGoalMax(_person) > (short)0 ) {
			if (displayWeightGoal) {
				str = set.getSetNumber() + ": " + this.getRepGoalMin(_person) + " - " + this.getRepGoalMax(_person) + " Reps @ " + this.getStringFromBigDecimal(weightGoal) + " " + preferences.getUnitTypeString();
			} else {
				str = set.getSetNumber() + ": " + this.getRepGoalMin(_person) + " - " + this.getRepGoalMax(_person) + " Reps";
			}
		} else {
			if ( this.isAMRAP() ) {
				if (displayWeightGoal) {
					str = set.getSetNumber() + ": AMRAP @ " + this.getStringFromBigDecimal(weightGoal) + " " + preferences.getUnitTypeString();
				} else {
					str = set.getSetNumber() + ": AMRAP";
				}
			} else {
				if (displayWeightGoal) {
					str = set.getSetNumber() + ": " + this.getRepGoalMin(_person) + " Reps @ " + this.getStringFromBigDecimal(weightGoal) + " " + preferences.getUnitTypeString();
				} else {
					str = set.getSetNumber() + ": " + this.getRepGoalMin(_person) + " Reps";
				}
			}
		}
		
		if (this.isDropSet()) {
			return str + " (drop set)";
		}
		return str;
    }
	
	public void
	setSetNumber(short _s) {
		set.setSetNumber(_s);
	}
	
	public short
	getSetNumber() {
		return set.getSetNumber();
	}
	
	public void
	setWeekNumber(short _s) {
		set.setWeekNumber(_s);
	}
	
	public short
	getWeekNumber() {
		return set.getWeekNumber();
	}
	

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		//TrainingPlanMove parent = this.getParent();
		//PersonBean sdf = parent.getWorkout().getParent().getPerson();
		//System.out.println("saving set >" + this.getLabel(sdf));
		//System.out.println("parent >" + parent.getLabel());
		//System.out.println("rep goal min >" + set.);
		
		set.save();
		this.getParent().getParent().getParent().notifyChange();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		set.save();
		try {
			this.getParent().getParent().getParent().notifyChange();
		} catch (ObjectNotFoundException x) {
			x.printStackTrace();
		}
    }
	
	public boolean isExt() {
		return (set.getIsExt() == (short)1);
	}

	public void setIsExt(boolean _b) {
		set.setIsExt(_b ? (short)1 : (short)0);
	}
	
	public boolean isDropSet() {
		return (set.getIsDropSet() == (short)1);
	}

	public void setIsDropSet(boolean _b) {
		set.setIsDropSet(_b ? (short)1 : (short)0);
	}

	public boolean isAMRAP() {
		return (set.getIsAmrap() == (short)1);
	}

	public void setIsAMRAP(boolean _b) {
		set.setIsAmrap(_b ? (short)1 : (short)0);
	}

	public boolean isAMWAP() {
		return (set.getIsAmwap() == (short)1);
	}

	public void setIsAMWAP(boolean _b) {
		set.setIsAmwap(_b ? (short)1 : (short)0);
	}

	public boolean isPerc1RM() {
		return (set.getIs1rmPerc() == (short)1);
	}

	public void setIsPerc1RM(boolean _b) {
		set.setIs1rmPerc(_b ? (short)1 : (short)0);
	}

	public BigDecimal getOneRepMaxPercentage() {
		return set.getOneRepMaxPerc();
	}

	public String getOneRepMaxPercentageString() {
		BigDecimal bd = set.getOneRepMaxPerc();
		if (bd == null) {
			return "";
		}
		return this.getStringFromBigDecimal(bd);
	}

	public void setOneRepMaxPercentage(BigDecimal _bd) {
		set.setOneRepMaxPerc(_bd);
	}
	
	private PersonTrainingPlanSetMapping
	getSetMapping(PersonBean _person) throws TorqueException, IllegalValueException {
		if (this.getId() == 0) {
			throw new IllegalValueException("Unable to get person set mapping with id zero...");
		}
		if (personSet == null) {
			personSet = new HashMap(11);
		}
		PersonTrainingPlanSetMapping mapping = personSet.get(_person);
		if (mapping == null) {
			// look in the database...
			Criteria crit = new Criteria();
			crit.add(PersonTrainingPlanSetMappingPeer.PERSON_ID, _person.getId());
			crit.add(PersonTrainingPlanSetMappingPeer.TRAINING_PLAN_SET_DB_ID, this.getId());
			List l = PersonTrainingPlanSetMappingPeer.doSelect(crit);
			if (l.isEmpty()) {
				PersonTrainingPlanSetMapping mapping_obj = new PersonTrainingPlanSetMapping();
				mapping_obj.setPersonId(_person.getId());
				mapping_obj.setTrainingPlanSetDbId(this.getId());
				//mapping_obj.save();
				mapping = mapping_obj;
			} else {
				mapping = (PersonTrainingPlanSetMapping)l.get(0);
			}
			personSet.put(_person, mapping);
		}
		return mapping;
	}
	
	private Vector
	getSetMappings() throws TorqueException {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(PersonTrainingPlanSetMappingPeer.TRAINING_PLAN_SET_DB_ID, this.getId());
		Iterator itr = PersonTrainingPlanSetMappingPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			PersonTrainingPlanSetMapping obj = (PersonTrainingPlanSetMapping)itr.next();
			vec.addElement(obj);
		}
		return vec;
	}
	
	public boolean
	isRPE() {
		return ( set.getRpeGoal() != null );
	}
	
	public String
	getRPEGoal() {
		String str = set.getRpeGoal();
		if (str == null) {
			return "";
		}
		return str;
	}
	
	public void
	setRPEGoal(String _str) {
		set.setRpeGoal(_str);
	}
	
	/*
	public String
	getRPEGoal(PersonBean _person) throws TorqueException, IllegalValueException {
		
		PersonTrainingPlanSetMapping map_obj = this.getSetMapping(_person);
		if ( map_obj.getRpeGoal() == null ) {
			// see if there's a default value...
			String rpe_goal_default = set.getRpeGoal();
			if ( rpe_goal_default == null ) {
				Iterator itr = this.getSetMappings().iterator();
				while (itr.hasNext()) {
					PersonTrainingPlanSetMapping obj = (PersonTrainingPlanSetMapping)itr.next();
					rpe_goal_default = obj.getRpeGoal();
					if ( (rpe_goal_default != null) && !rpe_goal_default.isEmpty() ) {
						break; // found a default
					}
				}
			}
			
			try {
				if ( (rpe_goal_default != null) && !rpe_goal_default.isEmpty() ) {
					set.setRpeGoal(rpe_goal_default);
					set.save();
					map_obj.setRpeGoal(rpe_goal_default);
					map_obj.save();
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return map_obj.getRpeGoal();
		
	}
	
	public void
	setRPEGoal(PersonBean _person, String _rpe_goal_str) throws TorqueException, Exception {
		PersonTrainingPlanSetMapping map_obj = this.getSetMapping(_person);
		map_obj.setRpeGoal(_rpe_goal_str);
		if (map_obj.getTrainingPlanSetDbId() == 0l) {
			map_obj.setTrainingPlanSetDbId(this.getId());
		}
		map_obj.save();
	}
	*/
	
	public BigDecimal
	getWeightGoal(PersonBean _person) throws TorqueException, IllegalValueException {
		
		PersonTrainingPlanSetMapping map_obj = this.getSetMapping(_person);
		//if (map_obj.getWeightGoal() == null || (map_obj.getWeightGoal().compareTo(BigDecimal.ZERO) == 0) ) {
		if ( map_obj.getWeightGoal() == null ) {
			// see if there's a default value...
			BigDecimal weight_goal_default = set.getWeightGoal();
			if (weight_goal_default == null || (weight_goal_default.compareTo(BigDecimal.ZERO) == 0) ) {
				Iterator itr = this.getSetMappings().iterator();
				while (itr.hasNext()) {
					PersonTrainingPlanSetMapping obj = (PersonTrainingPlanSetMapping)itr.next();
					weight_goal_default = obj.getWeightGoal();
					if (weight_goal_default != null && (weight_goal_default.compareTo(BigDecimal.ZERO) == 1) ) {
						break;
					}
				}
			}
			
			try {
				if (weight_goal_default != null && (weight_goal_default.compareTo(BigDecimal.ZERO) == 1) ) {
					set.setWeightGoal(weight_goal_default);
					set.save();
					map_obj.setWeightGoal(weight_goal_default);
					map_obj.save();
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return map_obj.getWeightGoal();
		
	}
	
	public String
	getWeightGoalString(PersonBean _person) throws TorqueException, IllegalValueException {
		return this.getStringFromBigDecimal(this.getWeightGoal(_person));
	}
	
	public void
	setWeightGoal(PersonBean _person, BigDecimal _bd) throws TorqueException, Exception {
		PersonTrainingPlanSetMapping map_obj = this.getSetMapping(_person);
		map_obj.setWeightGoal(_bd);
		if (map_obj.getTrainingPlanSetDbId() == 0l) {
			map_obj.setTrainingPlanSetDbId(this.getId());
			//throw new IllegalValueException("Unable to set weight goal.  Invalid Training Plan Set specified.");
		}
		map_obj.save();
		
		if (set.getWeightGoal() == null) {
			set.setWeightGoal(_bd);
		}
	}
	
	public String
	getCalculatedWeightGoalString(PersonBean _person) throws TorqueException, ObjectNotFoundException {
		
		BigDecimal percentage_of_1_rm = set.getOneRepMaxPerc();
		
		// first try to base this on the supplied 1RM
		
		BigDecimal enteredPR = null;
		try {
			String enteredPRStr = this.getParent().getMove().getPRString(_person);
			if ( (enteredPRStr != null) && !enteredPRStr.isEmpty() ) {
				enteredPR = new BigDecimal(enteredPRStr);
			}
		} catch (Exception x) { }
		
		if ( (enteredPR != null) && (enteredPR.compareTo(BigDecimal.ZERO) == 1) ) {
			if ( (percentage_of_1_rm != null) && (percentage_of_1_rm.compareTo(BigDecimal.ZERO) == 1) ) {
				return this.getStringFromBigDecimal(enteredPR.multiply(percentage_of_1_rm).divide(one_hundred, 0, RoundingMode.HALF_UP));
			}
		} else {
			if ( (percentage_of_1_rm != null) && (percentage_of_1_rm.compareTo(BigDecimal.ZERO) == 1) ) {
				BigDecimal calculated_1_rm = this.getParent().getMove().getCalculated1RM(_person);
				return this.getStringFromBigDecimal(calculated_1_rm.multiply(percentage_of_1_rm).divide(one_hundred, 0, RoundingMode.HALF_UP));
			}
		}
		return "";
	}
	
	public void
	invalidate() {
		personSet = null;
	}
	
	/*
	public String
	getLastWeightString() {
		// search for other instances of workouts that contain this move and set - return the weight recorded for it
		
		this.getParent()
		
		return this.getStringFromBigDecimal(set.getWeightGoal());
	}
	*/
	

	public short getRepGoalMin(PersonBean _person) throws TorqueException, IllegalValueException {
		PersonTrainingPlanSetMapping map_obj = this.getSetMapping(_person);
		if (map_obj.getRepGoalMin() == (short)0) {
			// see if there's a default value...
			short rep_goal_min_default = set.getRepGoalMin();
			if (rep_goal_min_default == (short)0) {
				Iterator itr = this.getSetMappings().iterator();
				while (itr.hasNext()) {
					PersonTrainingPlanSetMapping obj = (PersonTrainingPlanSetMapping)itr.next();
					rep_goal_min_default = obj.getRepGoalMin();
					if (rep_goal_min_default > (short)0) {
						break;
					}
				}
			}
			
			try {
				if (rep_goal_min_default > (short)0) {
					set.setRepGoalMin(rep_goal_min_default);
					set.save();
					map_obj.setRepGoalMin(rep_goal_min_default);
					map_obj.save();
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return map_obj.getRepGoalMin();
	}

	public void setRepGoalMin(PersonBean _person, Short repGoalMin) throws TorqueException, Exception {
		System.out.println("setRepGoalMin() invoked in TrainingPlanSet >" + repGoalMin);
		PersonTrainingPlanSetMapping map_obj = this.getSetMapping(_person);
		map_obj.setRepGoalMin(repGoalMin);
		map_obj.save();
		
		short rep_goal_min_default = set.getRepGoalMin();
		if (rep_goal_min_default == (short)0) {
			set.setRepGoalMin(rep_goal_min_default);
			set.save();
		}
	}

	public short getRepGoalMax(PersonBean _person) throws TorqueException, IllegalValueException {
		//PersonTrainingPlanSetMapping map_obj = this.getSetMapping(_person);
		
		PersonTrainingPlanSetMapping map_obj = this.getSetMapping(_person);
		if (map_obj.getRepGoalMax() == (short)0) {
			// see if there's a default value...
			short rep_goal_max_default = set.getRepGoalMax();
			if (rep_goal_max_default == (short)0) {
				Iterator itr = this.getSetMappings().iterator();
				while (itr.hasNext()) {
					PersonTrainingPlanSetMapping obj = (PersonTrainingPlanSetMapping)itr.next();
					rep_goal_max_default = obj.getRepGoalMax();
					if (rep_goal_max_default > (short)0) {
						break;
					}
				}
			}
			
			try {
				if (rep_goal_max_default > (short)0) {
					set.setRepGoalMax(rep_goal_max_default);
					set.save();
					map_obj.setRepGoalMax(rep_goal_max_default);
					map_obj.save();
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return map_obj.getRepGoalMax();
		
	}

	public void setRepGoalMax(PersonBean _person, Short repGoalMax) throws TorqueException, Exception {
		PersonTrainingPlanSetMapping map_obj = this.getSetMapping(_person);
		map_obj.setRepGoalMax(repGoalMax);
		map_obj.save();
		
		short rep_goal_max_default = set.getRepGoalMax();
		if (rep_goal_max_default == (short)0) {
			set.setRepGoalMax(rep_goal_max_default);
			set.save();
		}
	}
	
	public TrainingPlanMove
	getParent() throws TorqueException, ObjectNotFoundException {
		return TrainingPlanMove.getTrainingPlanMove(set.getTrainingPlanExcerciseDbId());
	}
	
	public void
	setParent(TrainingPlanMove _move) throws TorqueException {
		set.setTrainingPlanExcerciseDbId(_move.getId());
	}
	
	public void
	setMove(TrainingPlanMove _move) throws TorqueException {
		set.setTrainingPlanExcerciseDbId(_move.getId());
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