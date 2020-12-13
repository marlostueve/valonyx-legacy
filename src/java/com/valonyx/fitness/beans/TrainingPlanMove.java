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
TrainingPlanMove
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
    protected static HashMap<Long,TrainingPlanMove> hash = new HashMap<Long,TrainingPlanMove>();
	
    // CLASS METHODS
	
    public static void
    delete(long _id) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_EXCERCISE_DB_ID, _id);
		TrainingPlanExcerciseDbPeer.doDelete(crit);
		hash.remove(_id);
    }

    public static TrainingPlanMove
    getTrainingPlanMove(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		TrainingPlanMove move_obj = (TrainingPlanMove)hash.get(_id);
		if (move_obj == null) {
			Criteria crit = new Criteria();
			crit.add(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_EXCERCISE_DB_ID, _id);
			List objList = TrainingPlanExcerciseDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate move with id: " + _id);
			}

			move_obj = TrainingPlanMove.getTrainingPlanMove((TrainingPlanExcerciseDb)objList.get(0));
		}

		return move_obj;
    }

    public static TrainingPlanMove
    getTrainingPlanMove(TrainingPlanExcerciseDb _obj) throws TorqueException {
		
		TrainingPlanMove move_obj = (TrainingPlanMove)hash.get(_obj.getTrainingPlanExcerciseDbId());
		if (move_obj == null) {
			move_obj = new TrainingPlanMove(_obj);
			hash.put(_obj.getTrainingPlanExcerciseDbId(), move_obj);
		}

		return move_obj;
    }

    public static synchronized TrainingPlanMove
    maintainTrainingPlanMove(Move _move, TrainingPlanWorkout _plan_workout, short _move_number)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		Criteria crit = new Criteria();
		crit.add(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, _plan_workout.getId());
		crit.add(TrainingPlanExcerciseDbPeer.EXCERCISE_DB_ID, _move.getId());
		crit.add(TrainingPlanExcerciseDbPeer.EXCERCISE_NUMBER, _move_number);
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

	/*
    public static Vector
    getTrainingPlanMoves()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addDescendingOrderByColumn(TrainingPlanExcerciseDbPeer.GOAL_TYPE);
		crit.addDescendingOrderByColumn(TrainingPlanExcerciseDbPeer.DESCRIPTION);
		Iterator itr = TrainingPlanExcerciseDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(TrainingPlanMove.getTrainingPlanMove((TrainingPlanExcerciseDb)itr.next()));
		}
		
		return vec;
    }
	*/
	

    // SQL

    /*

<table name="TRAINING_PLAN_EXCERCISE_DB" idMethod="native">
	<column name="TRAINING_PLAN_EXCERCISE_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
    <column name="TRAINING_PLAN_WORKOUT_DB_ID" required="true" primaryKey="false" type="BIGINT"/>
    <column name="EXCERCISE_DB_ID" required="true" primaryKey="false" type="INTEGER"/>
	
	<column name="EXCERCISE_NUMBER" required="true" type="SMALLINT"/>
	<column name="IS_SUPER_SET" required="true" type="SMALLINT" default="0"/>
	<!--
	<column name="PR_WEIGHT" required="false" scale="2" size="5" type="DECIMAL"/>
	-->
	
	<column name="IS_ALTERNATE_MOVE" required="true" type="SMALLINT" default="0"/>

    <foreign-key foreignTable="TRAINING_PLAN_WORKOUT_DB">
		<reference local="TRAINING_PLAN_WORKOUT_DB_ID" foreign="TRAINING_PLAN_WORKOUT_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="EXCERCISE_DB">
		<reference local="EXCERCISE_DB_ID" foreign="EXCERCISE_DB_ID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private TrainingPlanExcerciseDb move;
	private Vector sets = null;
	
	// INNER CLASSES
	
	public class SetDetails {
		
		String sets = "";
		String reps = "";
		String weight = "";
		String rpe = "";
		
		String actualWeight = "";
		String actualRPE = "";
		String actualReps = "";
    }

    // CONSTRUCTORS

    public
    TrainingPlanMove() {
		move = new TrainingPlanExcerciseDb();
		isNew = true;
    }

    public
    TrainingPlanMove(TrainingPlanExcerciseDb _obj) {
		move = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public TrainingPlanMove
	copy(PersonBean _copyPerson, TrainingPlanWorkout _parent) throws TorqueException, ObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		TrainingPlanMove copy = new TrainingPlanMove();
		copy.setIsSuperSet(this.isSuperSet());
		copy.setMove(this.getMove());
		copy.setMoveNumber(this.getMoveNumber());
		copy.setParent(_parent); // should be done in the parent  // added back in 5/16/18
		copy.save(); // should get saved in the parent... // added back in 5/16/18
		
		Vector sets = new Vector();
		Iterator itr = this.getSets().iterator();
		while (itr.hasNext()) {
			TrainingPlanSet set_to_copy = (TrainingPlanSet)itr.next();
			System.out.println("TrainingPlanSet to copy >" + set_to_copy.getLabel(_copyPerson));
			//sets.addElement(set_to_copy.copy(_copyPerson, copy));
			sets.addElement(TrainingPlanSet.copy(_copyPerson, copy, set_to_copy));
		}
		copy.setSets(sets);
		copy.save();
		return copy;
	}
	
	public boolean
	equals(TrainingPlanMove _move) {
		if (_move == null) {
			return false;
		}
		return ( this.getId() == _move.getId() );
	}

    public long
    getId() {
		return move.getTrainingPlanExcerciseDbId();
    }

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		move.save();
		this.saveSets();
		
		this.getParent().getParent().notifyChange();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		move.save();
		this.saveSets();
		
		this.getParent().getParent().notifyChange();
    }
	
	public short
	getMoveNumber() {
		return move.getExcerciseNumber();
	}
	
	public void
	setMoveNumber(short _s) {
		move.setExcerciseNumber(_s);
	}
	
	/*
	public void
	setPRWeight(BigDecimal _pr) {
		move.setPrWeight(_pr);
	}
	
	public String
	getPRString() {
		return this.getStringFromBigDecimal(move.getPrWeight());
	}
	*/
	
	public Move
	getMove() throws TorqueException, ObjectNotFoundException {
		return Move.getMove(move.getExcerciseDbId());
	}
	
	public void
	setMove(Move _move) throws TorqueException {
		move.setExcerciseDbId(_move.getId());
	}
	
	public TrainingPlanWorkout
	getParent() throws TorqueException, ObjectNotFoundException {
		return TrainingPlanWorkout.getTrainingPlanWorkout(move.getTrainingPlanWorkoutDbId());
	}
	
	public TrainingPlanWorkout
	getWorkout() throws TorqueException, ObjectNotFoundException {
		return TrainingPlanWorkout.getTrainingPlanWorkout(move.getTrainingPlanWorkoutDbId());
	}
	
	public void
	setParent(TrainingPlanWorkout _w) throws TorqueException {
		move.setTrainingPlanWorkoutDbId(_w.getId());
	}
	
	public void
	setWorkout(TrainingPlanWorkout _w) throws TorqueException {
		move.setTrainingPlanWorkoutDbId(_w.getId());
	}

	public boolean isSuperSet() {
		return (move.getIsSuperSet() == (short)1);
	}

	public void setIsSuperSet(boolean _b) {
		move.setIsSuperSet(_b ? (short)1 : (short)0);
	}

	public boolean isAlternateMove() {
		return (move.getIsAlternateMove() == (short)1);
	}

	public void setIsAlternateMove(boolean _b) {
		move.setIsAlternateMove(_b ? (short)1 : (short)0);
	}
	
	public int getAlternateMovePersonId() {
		return move.getAltPersonId();
	}
	
	public void setAlternateMovePerson(PersonBean _person) throws TorqueException {
		move.setAltPersonId(_person.getId());
	}

	/*
	public boolean isCompoundLift() {
		return (move.getIsCompound() == (short)1);
	}

	public void setIsCompoundLift(boolean _b) {
		move.setIsCompound(_b ? (short)1 : (short)0);
	}
	*/
	
	public String
	getLabel() throws TorqueException, ObjectNotFoundException {
		
		int move_id = move.getExcerciseDbId();
		if (move_id == 0) {
			return "";
		}
		
		Move move_obj = Move.getMove(move_id);
		return move_obj.getLabel();
	}
	
	public void
	setDescription(String _s) {
		move.setDescription(_s);
	}
	
	public String
	getDescription() throws TorqueException, ObjectNotFoundException {
		/*
		Move move_obj = Move.getMove(move.getExcerciseDbId());
		return move_obj.getDescription();
		*/
		String str = move.getDescription();
		if (str == null) {
			return "";
		}
		return str;
	}
	
	public short
	getType() throws TorqueException, ObjectNotFoundException {
		Move move_obj = Move.getMove(move.getExcerciseDbId());
		return move_obj.getType();
	}
	
	public String
	getTypeString() throws TorqueException, ObjectNotFoundException {
		Move move_obj = Move.getMove(move.getExcerciseDbId());
		return move_obj.getTypeString();
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
	
	public void
	invalidate() {
		this.sets = null;
		setsStringMap = null;
	}
	
	public boolean
	canEdit(PersonBean _person) {
		
		if (_person == null) {
			return false;
		}
		
		if (_person.isAdministrator()) {
			return true;
		}
		
		//return ( _person.getId() == move.getCreatePersonId() );
		
		return false;
	}
	

	public Vector
	getSets() throws TorqueException, ObjectNotFoundException {
		if (this.sets == null) {
			this.sets = new Vector();
			Criteria crit = new Criteria();
			crit.add(TrainingPlanSetDbPeer.TRAINING_PLAN_EXCERCISE_DB_ID, this.getId());
			crit.addAscendingOrderByColumn(TrainingPlanSetDbPeer.SET_NUMBER);
			
			Iterator itr = TrainingPlanSetDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				TrainingPlanSetDb obj = (TrainingPlanSetDb)itr.next();
				this.sets.addElement(TrainingPlanSet.getTrainingPlanSet(obj));
			}
		}
		return this.sets;
	}
	
	public Vector
	getSets(int _num_sets) throws TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		this.getSets();
		int sets_to_create = _num_sets - this.sets.size();
		for (int i = 0; i < sets_to_create; i++) {
			TrainingPlanSet set_obj = new TrainingPlanSet();
			set_obj.setParent(this);
			set_obj.setIsDropSet(false);
			set_obj.setIsPerc1RM(false);
			set_obj.setSetNumber((short)this.sets.size()); // zero is allowed
			set_obj.setIsExt(false);
			set_obj.save();
			//set_obj.invalidate();
			this.sets.addElement(set_obj);
		}
		return this.sets;
	}

	public void
	setSets(Vector _sets) {
		this.sets = _sets;
		setsStringMap = null;
	}

	private void
    saveSets() throws TorqueException, Exception {
		if (this.sets != null) {
			System.out.println("SAVE SETS for >" + this.getLabel());
			System.out.println("saveSets sizer >" + this.sets.size());

			HashMap<Long,TrainingPlanSetDb> db_move_hash = new HashMap<Long,TrainingPlanSetDb>(3);
			Criteria crit = new Criteria();
			crit.add(TrainingPlanSetDbPeer.TRAINING_PLAN_EXCERCISE_DB_ID, this.getId());
			Iterator itr = TrainingPlanSetDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				System.out.println("found existing ass...");
				TrainingPlanSetDb value = (TrainingPlanSetDb)itr.next();
				Long key = value.getTrainingPlanSetDbId();
				db_move_hash.put(key, value);
			}

			System.out.println("num sets >" + this.sets.size());
			itr = this.sets.iterator();
			while (itr.hasNext()) {
				TrainingPlanSet set_obj = (TrainingPlanSet)itr.next();
				set_obj.setMove(this); // just to be sure
				set_obj.save(); // in case this isn't being saved elsewhere, I guess
				TrainingPlanSetDb obj = (TrainingPlanSetDb)db_move_hash.remove(set_obj.getId());
				if (obj == null) { // interaction not already in database
					/*
					obj = new TrainingPlanExcerciseDb();
					obj.setProd.setSubscriptionInfoDbId(this.getId());
					obj.setOrderId(key.intValue());
					obj.save();
							*/
				}
			}

			itr = db_move_hash.keySet().iterator();
			while (itr.hasNext()) {
				Long key = (Long)itr.next();
				TrainingPlanSetDb obj = (TrainingPlanSetDb)db_move_hash.get(key);
				TrainingPlanSetDbPeer.doDelete(obj);
			}
		}
    }
	
	private HashMap<PersonBean,String> setsStringMap = null;
	
	public String
	getSetsDesc(PersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		
		if (setsStringMap == null) {
			setsStringMap = new HashMap<PersonBean,String>();
		}
		
		if (!setsStringMap.containsKey(_person)) {
			
			this.getSets();
			if (this.sets.isEmpty()) {
				setsStringMap.put(_person, "No Sets Found");
			} else {
				
				int set_size = this.sets.size();
			
				short min_reps = 0;
				short max_reps = 0;
				BigDecimal min_goal = BigDecimal.ZERO;
				BigDecimal max_goal = BigDecimal.ZERO;
				Iterator itr = this.sets.iterator();
				while (itr.hasNext()) {
					TrainingPlanSet set = (TrainingPlanSet)itr.next();
					if ((min_reps == 0) || (set.getRepGoalMin(_person) < min_reps)) {
						min_reps = set.getRepGoalMin(_person);
					}
					if (set.getRepGoalMax(_person) > max_reps) {
						max_reps = set.getRepGoalMax(_person);
					}
					BigDecimal weight_goal = set.getWeightGoal(_person);
					if (weight_goal != null) {
						if ( (min_goal.compareTo(BigDecimal.ZERO) == 0) || (weight_goal.compareTo(min_goal) == -1) ) {
							min_goal = weight_goal;
						}
						if ( (max_goal.compareTo(BigDecimal.ZERO) == 0) || (weight_goal.compareTo(max_goal) == 1) ) {
							max_goal = weight_goal;
						}
					}
				}
				if (min_reps == 0) {
					if (set_size == 1) {
						setsStringMap.put(_person, set_size + " Set: Rep range not found");
					} else {
						setsStringMap.put(_person, set_size + " Sets: Rep range not found");
					}
				} else {
					WeightPreferences preferences = WeightPreferences.getWeightPreference(this.getParent().getParent().getPerson());
					String weight_str = "";
					if (min_goal.compareTo(BigDecimal.ZERO) == 1) {
						if (min_goal.compareTo(max_goal) == 0) {
							weight_str = " " + this.getStringFromBigDecimal(min_goal) + " " + preferences.getUnitTypeString();
						} else {
							weight_str = " " + this.getStringFromBigDecimal(min_goal) + "-" + this.getStringFromBigDecimal(max_goal) + " " + preferences.getUnitTypeString();
						}
					}
					if ( (min_reps == max_reps) || (max_reps == 0) ) {
						if (set_size == 1) {
							setsStringMap.put(_person, set_size + " Set: " + min_reps + " Reps" + weight_str);
						} else {
							setsStringMap.put(_person, set_size + " Sets: " + min_reps + " Reps" + weight_str);
						}
					} else {
						if (set_size == 1) {
							setsStringMap.put(_person, set_size + " Set:" + min_reps + "-" + max_reps + " Reps" + weight_str);
						} else {
							setsStringMap.put(_person, set_size + " Sets:" + min_reps + "-" + max_reps + " Reps" + weight_str);
						}
					}
				}
			}
		}
		
		return setsStringMap.get(_person);
	}
	
	
	public ArrayList<SetDetails>
	getSetDetails(PersonBean _person, LinkedHashMap<Move,ArrayList<WorkoutSet>> _set_hash) throws TorqueException, ObjectNotFoundException, ObjectAlreadyExistsException, UniqueObjectNotFoundException, IllegalValueException, Exception {
		
		ArrayList<SetDetails> vec = new ArrayList<SetDetails>();
		
		/*
		boolean has_workout_for_training_plan = false;
		Workout workout_obj = null;
		try {
			workout_obj = this.getParent().getWorkoutForTrainingPlanWorkout(_person, false, false, false);
			has_workout_for_training_plan = true;
		} catch (ObjectNotFoundException x) {
			
		}
		*/
		
		//WorkoutSet last_set = null;
		TrainingPlanSet last_set = null;
		short equal_streak = (short)0;
		short last_streak = (short)0;
		Iterator itr = this.getSets().iterator();
		if (itr.hasNext()) {
			//setStr = "";
			for (int i = 0; itr.hasNext(); i++) {
				TrainingPlanSet set_obj = (TrainingPlanSet)itr.next();
				//set_obj.getActualWeight(); // this is from WorkoutSet
				
				if (last_set != null) {
					if (set_obj.equals(last_set, _person)) {
						
						if (equal_streak == (short)0) {
							equal_streak = (short)2;
						} else {
							equal_streak++;
						}
						
					} else {
						last_streak = equal_streak;
						equal_streak = (short)0;
					}
				}
				
				//String actual_weight_str = set_obj.getActualWeightString();
				
				if ( (equal_streak == (short)0) && (last_streak > (short)0) ) {
					
					SetDetails details = new SetDetails();
					details.sets = last_streak + "";
					details.reps = last_set.getRepGoalMin(_person) + "";
					if (last_set.isPerc1RM()) {
						details.weight = last_set.getCalculatedWeightGoalString(_person);
					} else {
						details.weight = last_set.getWeightGoalString(_person);
					}
					details.rpe = last_set.getRPEGoal();
					if (_set_hash != null && _set_hash.containsKey(this.getMove())) {
						ArrayList<WorkoutSet> workout_set_list = _set_hash.get(this.getMove());
						Iterator<WorkoutSet> list_itr = workout_set_list.iterator();
						while (list_itr.hasNext()) {
							WorkoutSet workoutSetObj = list_itr.next();
							if (workoutSetObj.getTrainingPlanSet().getId() == last_set.getId()) {
								details.actualRPE = workoutSetObj.getActualRPEString();
								details.actualWeight = workoutSetObj.getActualWeightString();
								details.actualReps = workoutSetObj.getActualReps() + "";
								break;
							}
						}
						
					}
					vec.add(details);
				}
				
				
				last_set = set_obj;
				
				
			} // while (itr.hasNext()) {
			
			if (equal_streak == (short)0) {
				equal_streak = (short)1;
			}
			SetDetails details = new SetDetails();
			details.sets = equal_streak + "";
			details.reps = last_set.getRepGoalMin(_person) + "";
			if (last_set.isPerc1RM()) {
				details.weight = last_set.getCalculatedWeightGoalString(_person);
			} else {
				details.weight = last_set.getWeightGoalString(_person);
			}
			details.rpe = last_set.getRPEGoal();
			if (_set_hash != null && _set_hash.containsKey(this.getMove())) {
				ArrayList<WorkoutSet> workout_set_list = _set_hash.get(this.getMove());
				Iterator<WorkoutSet> list_itr = workout_set_list.iterator();
				while (list_itr.hasNext()) {
					WorkoutSet workoutSetObj = list_itr.next();
					if (workoutSetObj.getTrainingPlanSet().getId() == last_set.getId()) {
						details.actualRPE = workoutSetObj.getActualRPEString();
						details.actualWeight = workoutSetObj.getActualWeightString();
						details.actualReps = workoutSetObj.getActualReps() + "";
						break;
					}
				}
			}
			
			vec.add(details);
			

		} // if (itr.hasNext()) {
		
		return vec;
		
	}
	
	
	
}