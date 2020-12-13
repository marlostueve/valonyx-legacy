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

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;



/**
 *
 * @author marlo
 */
public class
TrainingPlanWorkout
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
	//public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	public static final short GRAMS_TYPE = 1;
	public static final short PERCENTAGE_TYPE = 2;

    protected static HashMap<Long,TrainingPlanWorkout> hash = new HashMap<Long,TrainingPlanWorkout>();

    // CLASS METHODS
	
    public static void
    delete(long _id) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(TrainingPlanWorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, _id);
		TrainingPlanWorkoutDbPeer.doDelete(crit);
		hash.remove(_id);
    }

    public static TrainingPlanWorkout
    getTrainingPlanWorkout(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		TrainingPlanWorkout workout_obj = (TrainingPlanWorkout)hash.get(_id);
		if (workout_obj == null) {
			Criteria crit = new Criteria();
			crit.add(TrainingPlanWorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, _id);
			List objList = TrainingPlanWorkoutDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate training plan workout with id: " + _id);
			}

			workout_obj = TrainingPlanWorkout.getTrainingPlanWorkout((TrainingPlanWorkoutDb)objList.get(0));
		}

		return workout_obj;
    }

    public static TrainingPlanWorkout
    getTrainingPlanWorkout(TrainingPlanWorkoutDb _obj) throws TorqueException {
		
		TrainingPlanWorkout workout_obj = (TrainingPlanWorkout)hash.get(_obj.getTrainingPlanWorkoutDbId());
		if (workout_obj == null) {
			workout_obj = new TrainingPlanWorkout(_obj);
			hash.put(_obj.getTrainingPlanWorkoutDbId(), workout_obj);
		}

		return workout_obj;
    }

    public static TrainingPlanWorkout
    getCurrentTrainingPlanWorkout(UKOnlinePersonBean _person, TrainingPlan _plan)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		if (_plan.hasCurrentTrainingPlanWorkout(_person)) {
			return _plan.getCurrentTrainingPlanWorkout(_person);
		}
		
		Vector vec = TrainingPlanWorkout.getTrainingPlanWorkouts(_plan);
		if (vec.isEmpty()) {
			throw new ObjectNotFoundException("No workouts found in training plan.");
		}
		
		// what was the last workout that the person did??
		
		Criteria crit = new Criteria();
		crit.addJoin(TrainingPlanDbPeer.TRAINING_PLAN_DB_ID, TrainingPlanWorkoutDbPeer.TRAINING_PLAN_DB_ID);
		crit.addJoin(TrainingPlanWorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, WorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID);
		crit.add(TrainingPlanDbPeer.TRAINING_PLAN_DB_ID, _plan.getId());
		crit.setLimit(1); // I wonder if this will work
		crit.addDescendingOrderByColumn(WorkoutDbPeer.WORKOUT_START_DATE);
		List objList = WorkoutDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			// not workout done yet - get the
			System.out.println("not workout done yet");
			TrainingPlanWorkout obj = (TrainingPlanWorkout)vec.elementAt(0);
			_plan.setCurrentTrainingPlanWorkout(_person, obj);
			//_plan.save(); // removed 8/27/18 - no need to save here
			return obj;
		} else {
			// found some workout instance(s)
			WorkoutDb workout_db_obj = (WorkoutDb)objList.get(0);
			TrainingPlanWorkout last_training_plan_workout = TrainingPlanWorkout.getTrainingPlanWorkout(workout_db_obj.getTrainingPlanWorkoutDbId());
			
			Iterator itr = vec.iterator();
			while (itr.hasNext()) {
				TrainingPlanWorkout workout_obj = (TrainingPlanWorkout)itr.next();
				if (last_training_plan_workout.equals(workout_obj)) {
					if (itr.hasNext()) {
						TrainingPlanWorkout obj = (TrainingPlanWorkout)itr.next();
						_plan.setCurrentTrainingPlanWorkout(_person, obj);
						//_plan.save(); // removed 8/27/18 - no need to save here
						return obj;
					}
				}
			}
			
			// unable to find the next workout - return the first
			
			System.out.println("unable to find the next workout");
			TrainingPlanWorkout obj = (TrainingPlanWorkout)vec.elementAt(0);
			_plan.setCurrentTrainingPlanWorkout(_person, obj);
			//_plan.save(); // removed 8/27/18 - no need to save here
			return obj; 
		}
		
		
    }

    public static synchronized TrainingPlanWorkout
    maintainTrainingPlanWorkout(TrainingPlan _plan, String _name, short _block, short _weeknumber)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		Criteria crit = new Criteria();
		crit.add(TrainingPlanWorkoutDbPeer.TRAINING_PLAN_DB_ID, _plan.getId());
		crit.add(TrainingPlanWorkoutDbPeer.NAME, _name);
		crit.add(TrainingPlanWorkoutDbPeer.BLOCK_NUMBER, _block);
		crit.add(TrainingPlanWorkoutDbPeer.WEEK_NUMBER, _weeknumber);
		
		System.out.println("maintainTrainingPlanWorkout crit >" + crit.toString());
		
		List objList = TrainingPlanWorkoutDbPeer.doSelect(crit);
		
		if (objList.isEmpty()) {
			// maintain one
			TrainingPlanWorkout new_workout = new TrainingPlanWorkout();
			new_workout = new TrainingPlanWorkout();
			new_workout.setParent(_plan);
			new_workout.setName(_name);
			new_workout.setBlockNumber(_block);
			new_workout.setWeekNumber(_weeknumber);
			new_workout.setWorkoutNumber((short)_plan.getWorkouts().size()); // shrug
			new_workout.save();
			_plan.invalidate();
			return new_workout;
		}
		
		return TrainingPlanWorkout.getTrainingPlanWorkout((TrainingPlanWorkoutDb)objList.get(0));
		
    }

    public static Vector
    getTrainingPlanWorkouts(TrainingPlan _plan)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(TrainingPlanWorkoutDbPeer.TRAINING_PLAN_DB_ID, _plan.getId());
		crit.addAscendingOrderByColumn(TrainingPlanWorkoutDbPeer.BLOCK_NUMBER);
		crit.addAscendingOrderByColumn(TrainingPlanWorkoutDbPeer.WEEK_NUMBER);
		crit.addAscendingOrderByColumn(TrainingPlanWorkoutDbPeer.WORKOUT_NUMBER);
		//System.out.println("getTrainingPlanWorkouts crit >" + crit.toString());
		Iterator itr = TrainingPlanWorkoutDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(TrainingPlanWorkout.getTrainingPlanWorkout((TrainingPlanWorkoutDb)itr.next()));
		}
		return vec;
    }
	
	public static LinkedHashMap<Workout,Vector>
	getPreviousWorkoutsSets(PersonBean _person, TrainingPlanWorkout _plan_workout, int _num_workouts) throws TorqueException, ObjectNotFoundException {
		
		Criteria crit = new Criteria();
		
		//Vector vec = new Vector();
		
		LinkedHashMap<Workout,Vector> hash = new LinkedHashMap();
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);
		
		crit.addJoin(TrainingPlanWorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, WorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID);
		crit.addJoin(WorkoutDbPeer.WORKOUT_DB_ID, SetDbPeer.WORKOUT_DB_ID);
		
		//crit.add(WorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, _plan_workout.getId());
		crit.add(TrainingPlanWorkoutDbPeer.NAME, _plan_workout.getName());
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		
		crit.add(SetDbPeer.SET_DATE, start_of_day.getTime(), Criteria.LESS_THAN); // make sure we're not grabbing anything on today's date
		crit.addDescendingOrderByColumn(SetDbPeer.SET_DATE);
		
		System.out.println("getPreviousWorkoutsSets crit >" + crit.toString());
		List l = SetDbPeer.doSelect(crit);
		
		int num_workouts_found = 1;
		Workout last_workout = null;
		//Iterator itr = SetDbPeer.doSelect(crit).iterator();
		for (int i = (l.size() - 1); i >= 0; i--) {
			SetDb set_obj = (SetDb)l.get(i);
			WorkoutSet obj = WorkoutSet.getSet(set_obj.getSetDbId());
			Workout workout_obj = obj.getWorkout();
			if (last_workout != null) {
				if (!workout_obj.equals(last_workout)) {
					num_workouts_found++;
				}
			}
			if (num_workouts_found > _num_workouts) {
				return hash;
			}
			Vector vec = null;
			if (hash.containsKey(workout_obj)) {
				vec = hash.get(workout_obj);
			} else {
				vec = new Vector();
				hash.put(workout_obj, vec);
			}
			vec.addElement(obj);
			last_workout = workout_obj;
		}
		
		return hash;
	}
	
	
	

    // SQL

    /*
	

<table name="TRAINING_PLAN_WORKOUT_DB" idMethod="native">
	<column name="TRAINING_PLAN_WORKOUT_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	<column name="TRAINING_PLAN_DB_ID" required="true" type="INTEGER"/>
	
	<column name="NAME" required="true" size="100" type="VARCHAR"/>
	<column name="DESCRIPTION" type="LONGVARCHAR"/>
	
	<!--
    <column name="WEEK_NUMBER" required="false" type="SMALLINT"/>
	<column name="DAY_NUMBER" required="false" type="SMALLINT"/>
	-->
    <column name="WORKOUT_NUMBER" required="false" type="SMALLINT"/>
	
	<column name="IS_FULL_BODY" required="true" type="SMALLINT" default="0"/>
	<column name="IS_UPPER_BODY" required="true" type="SMALLINT" default="0"/>
	<column name="IS_LEGS" required="true" type="SMALLINT" default="0"/>
	<column name="IS_ARMS" required="true" type="SMALLINT" default="0"/>
	<column name="IS_CHEST" required="true" type="SMALLINT" default="0"/>
	<column name="IS_SHOULDERS" required="true" type="SMALLINT" default="0"/>
	<column name="IS_BACK" required="true" type="SMALLINT" default="0"/>
	<column name="IS_BICEP" required="true" type="SMALLINT" default="0"/>
	<column name="IS_TRICEP" required="true" type="SMALLINT" default="0"/>
	<column name="IS_ABS" required="true" type="SMALLINT" default="0"/>
	
	<column name="IS_PUSH" required="true" type="SMALLINT" default="0"/>
	<column name="IS_PULL" required="true" type="SMALLINT" default="0"/>
	
	<column name="IS_CARDIO" required="true" type="SMALLINT" default="0"/>
	
    <foreign-key foreignTable="TRAINING_PLAN_DB">
		<reference local="TRAINING_PLAN_DB_ID" foreign="TRAINING_PLAN_DB_ID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private TrainingPlanWorkoutDb workout;
	private Vector moves = null;
	//private Vector<Short> weekNumbers = null;
	
	private HashMap<PersonBean, Vector> moveHash = null;

    // CONSTRUCTORS

    public
    TrainingPlanWorkout() {
		workout = new TrainingPlanWorkoutDb();
		isNew = true;
    }

    public
    TrainingPlanWorkout(TrainingPlanWorkoutDb _obj) {
		workout = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public boolean
	equals(TrainingPlanWorkout _w) throws TorqueException, ObjectNotFoundException {
		if (_w == null) {
			return false;
		}
		/*
		if ( (this.getWeekNumber() == _w.getWeekNumber()) && (this.getDayNumber() == _w.getDayNumber()) && (this.getWorkoutNumber() == _w.getWorkoutNumber()) ) {
			return this.getParent().equals(_w.getParent());
		}
		*/
		// 2/3/19 - added weekNumber comparison back in
		if ( this.getLabel().equals(_w.getLabel())
				&& (this.getBlockNumber() == _w.getBlockNumber())
				&& (this.getWeekNumber() == _w.getWeekNumber())
				&&  ( this.getWorkoutNumber() == _w.getWorkoutNumber() ) ) {
			return this.getParent().equals(_w.getParent());
		}
		return false;
	}
	
	

	/*
	public short getDayNumber() {
		return workout.getDayNumber();
	}

	public void setDayNumber(short _s) {
		this.workout.setDayNumber(_s);
	}
	*/

	public short getWorkoutNumber() {
		return workout.getWorkoutNumber();
	}

	public void setWorkoutNumber(short _s) {
		this.workout.setWorkoutNumber(_s);
	}
	
	public TrainingPlan
	getParent() throws TorqueException, ObjectNotFoundException {
		return TrainingPlan.getTrainingPlan(this.workout.getTrainingPlanDbId());
	}
	
	public void
	setParent(TrainingPlan _plan) throws TorqueException {
		this.workout.setTrainingPlanDbId(_plan.getId());
	}

    public long
    getId() {
		return workout.getTrainingPlanWorkoutDbId();
    }

    public String
    getLabel() {
		String str = this.workout.getName();
		return (str == null) ? "" : str;
    }

    public String
    getValue() {
		return this.getId() + "";
    }

	public String getName() {
		return this.workout.getName();
	}

	public void setName(String _s) {
		this.workout.setName(_s);
	}

	public String getDescription() {
		String str = this.workout.getDescription();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setDescription(String _s) {
		this.workout.setDescription(_s);
	}

    protected void
    insertObject()
		throws Exception {
		//workout.setEntryDate(new Date());
		workout.save();
		this.saveMoves();
		//this.saveWeekNumbers();
		
		this.getParent().notifyChange();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		workout.save();
		this.saveMoves();
		//this.saveWeekNumbers();
		
		this.getParent().notifyChange();
    }
	
	
	
	/*
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
	*/
	
	
	
	public void
	invalidate() {
		moves = null;
		//weekNumbers = null;
		
		has_workout_hash = null;
		workout_hash = null;
		
		moveHash = null;
	}

	public Vector
	getMoves() throws TorqueException, ObjectNotFoundException {
		if (moves == null) {
			moves = new Vector();
			Criteria crit = new Criteria();
			crit.add(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, this.getId());
			crit.add(TrainingPlanExcerciseDbPeer.IS_ALTERNATE_MOVE, (short)0);
			crit.addAscendingOrderByColumn(TrainingPlanExcerciseDbPeer.EXCERCISE_NUMBER);
			Iterator itr = TrainingPlanExcerciseDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				TrainingPlanExcerciseDb obj = (TrainingPlanExcerciseDb)itr.next();
				moves.addElement(TrainingPlanMove.getTrainingPlanMove(obj));
			}
		}
		return moves;
	}

	public Vector
	getMoves(PersonBean _person) throws TorqueException, ObjectNotFoundException {
		
		if (moveHash == null) {
			moveHash = new HashMap<PersonBean,Vector>(3);
		}
		
		Vector vec = null;
		if (moveHash.containsKey(_person)) {
			vec = moveHash.get(_person);
		}
		
		if (vec == null) {
			vec = new Vector();
			moveHash.put(_person, vec);
			
			Criteria crit = new Criteria();
			crit.add(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, this.getId());
			
			Criteria.Criterion crit_a = crit.getNewCriterion(TrainingPlanExcerciseDbPeer.IS_ALTERNATE_MOVE, (short)0, Criteria.EQUAL);
			Criteria.Criterion crit_b = crit.getNewCriterion(TrainingPlanExcerciseDbPeer.ALT_PERSON_ID, _person.getId(), Criteria.EQUAL);
			crit.add(crit_a.or(crit_b));
			
			crit.addAscendingOrderByColumn(TrainingPlanExcerciseDbPeer.EXCERCISE_NUMBER);
			
			System.out.println("getMovesForPerson() crit >" + crit.toString());
			
			Iterator itr = TrainingPlanExcerciseDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				TrainingPlanExcerciseDb obj = (TrainingPlanExcerciseDb)itr.next();
				vec.addElement(TrainingPlanMove.getTrainingPlanMove(obj));
			}
		}
		
		return vec;
	}

	public void
	setMoves(Vector _moves) {
		moves = _moves;
	}

	private void
    saveMoves() throws TorqueException, Exception {
		if (this.moves != null) {
			System.out.println("saveMoves sizer >" + this.moves.size());

			HashMap<Long,TrainingPlanExcerciseDb> db_move_hash = new HashMap<Long,TrainingPlanExcerciseDb>(3);
			Criteria crit = new Criteria();
			crit.add(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, this.getId());
			Iterator itr = TrainingPlanExcerciseDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				System.out.println("found existing ass...");
				TrainingPlanExcerciseDb value = (TrainingPlanExcerciseDb)itr.next();
				Long key = value.getTrainingPlanExcerciseDbId();
				db_move_hash.put(key, value);
			}

			System.out.println("num moves >" + this.moves.size());
			itr = this.moves.iterator();
			while (itr.hasNext()) {
				TrainingPlanMove move_obj = (TrainingPlanMove)itr.next();
				move_obj.setWorkout(this); // just to be sure
				move_obj.save(); // in case this isn't being saved elsewhere, I guess
				TrainingPlanExcerciseDb obj = (TrainingPlanExcerciseDb)db_move_hash.remove(move_obj.getId());
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
				TrainingPlanExcerciseDb obj = (TrainingPlanExcerciseDb)db_move_hash.get(key);
				TrainingPlanExcerciseDbPeer.doDelete(obj);
			}
		}
    }
	
	
	public short getBlockNumber() {
		return workout.getBlockNumber();
	}

	public void setBlockNumber(short _s) {
		this.workout.setBlockNumber(_s);
	}
	
	
	public short getWeekNumber() {
		return workout.getWeekNumber();
	}

	public void setWeekNumber(short _s) {
		this.workout.setWeekNumber(_s);
	}

	public Workout
	getWorkoutForTrainingPlanWorkout(PersonBean _person, boolean _create, boolean _start_workout, boolean _finish_workout) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		Criteria crit = new Criteria();
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		crit.add(WorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, this.getId());
		System.out.println("getWorkoutForTrainingPlanWorkout crit >" + crit.toString());
		List l = WorkoutDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			if (_create) {
				Workout w = new Workout();
				w.setPerson(_person);
				w.setParent(this);
				if (_start_workout) {
					w.setStartDate(new Date()); // not sure about this - is there a way to calculate?
				}
				if (_finish_workout) {
					w.setEndDate(new Date());
				}
				// changed 2/13/19 - Workouts created as a result of updating from a 
				w.save();
				return w;
			} else {
				throw new ObjectNotFoundException(this.getLabel() +" workout not found for " + _person.getLabel());
			}
		} else if (l.size() == 1) {
			return Workout.getWorkout((WorkoutDb)l.get(0));
		} else {
			throw new UniqueObjectNotFoundException("Unique " + this.getLabel() + " workout not found for " + _person.getLabel());
		}
	}
	
	private HashMap<PersonBean,Boolean> has_workout_hash = null;
	private HashMap<PersonBean,Workout> workout_hash = null;
	public Workout
	getLastWorkoutForTrainingPlanWorkout(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException {
		
		if (workout_hash == null) {
			workout_hash = new HashMap<PersonBean,Workout>(3);
			has_workout_hash = new HashMap<PersonBean,Boolean>(3);
		}
		
		if (workout_hash.containsKey(_person)) {
			return workout_hash.get(_person);
		}
		
		Criteria crit = new Criteria();
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		crit.add(WorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, this.getId());
		crit.addDescendingOrderByColumn(WorkoutDbPeer.WORKOUT_START_DATE);
		crit.setLimit(1);
		System.out.println("getLastWorkoutForTrainingPlanWorkout crit >" + crit.toString());
		List l = WorkoutDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			throw new ObjectNotFoundException(this.getLabel() + " workout not found for " + _person.getLabel());
		} else {
			Workout obj = Workout.getWorkout((WorkoutDb)l.get(0));
			workout_hash.put(_person, obj);
			return obj;
		}
	}
	
	public boolean
	hasWorkoutForTrainingPlanWorkout(UKOnlinePersonBean _person) throws TorqueException {
		
		if (workout_hash == null) {
			workout_hash = new HashMap<PersonBean,Workout>(3);
			has_workout_hash = new HashMap<PersonBean,Boolean>(3);
		}
		
		if (has_workout_hash.containsKey(_person)) {
			return has_workout_hash.get(_person);
		}
		
		try {
			this.getLastWorkoutForTrainingPlanWorkout(_person);
			has_workout_hash.put(_person, true);
			return true;
		} catch (ObjectNotFoundException x) {
			has_workout_hash.put(_person, false);
			return false;
		}
	}
	
	
	// TRAINING_PLAN_WORKOUT_WEEK_NUMBER_MAPPING

	/*
	public Vector
	getWeekNumbers() throws TorqueException, ObjectNotFoundException {
		if (weekNumbers == null) {
			weekNumbers = new Vector();
			Criteria crit = new Criteria();
			crit.add(TrainingPlanWorkoutWeekNumberMappingPeer.TRAINING_PLAN_WORKOUT_DB_ID, this.getId());
			crit.addAscendingOrderByColumn(TrainingPlanWorkoutWeekNumberMappingPeer.WEEK_NUMBER);
			Iterator itr = TrainingPlanWorkoutWeekNumberMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				TrainingPlanWorkoutWeekNumberMapping obj = (TrainingPlanWorkoutWeekNumberMapping)itr.next();
				weekNumbers.addElement(obj.getWeekNumber());
			}
		}
		return weekNumbers;
	}

	public void
	setWeekNumbers(Vector _weekNumbers) {
		weekNumbers = _weekNumbers;
	}

	private void
    saveWeekNumbers() throws TorqueException, Exception {
		if (this.weekNumbers != null) {
			System.out.println("saveMoves sizer >" + this.weekNumbers.size());

			HashMap<Short,TrainingPlanWorkoutWeekNumberMapping> db_week_hash = new HashMap<Short,TrainingPlanWorkoutWeekNumberMapping>(3);
			Criteria crit = new Criteria();
			crit.add(TrainingPlanWorkoutWeekNumberMappingPeer.TRAINING_PLAN_WORKOUT_DB_ID, this.getId());
			Iterator itr = TrainingPlanWorkoutWeekNumberMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				System.out.println("found existing ass...");
				TrainingPlanWorkoutWeekNumberMapping value = (TrainingPlanWorkoutWeekNumberMapping)itr.next();
				Short key = value.getWeekNumber();
				db_week_hash.put(key, value);
			}

			System.out.println("num weekNumbers >" + this.weekNumbers.size());
			itr = this.weekNumbers.iterator();
			while (itr.hasNext()) {
				Short weekNumber = (Short)itr.next();
				TrainingPlanWorkoutWeekNumberMapping obj = (TrainingPlanWorkoutWeekNumberMapping)db_week_hash.remove(weekNumber);
				if (obj == null) { // mapping not already in database
					obj = new TrainingPlanWorkoutWeekNumberMapping();
					obj.setTrainingPlanWorkoutDbId(this.getId());
					obj.setWeekNumber(weekNumber);
					obj.save();
				}
			}

			itr = db_week_hash.keySet().iterator();
			while (itr.hasNext()) {
				Short key = (Short)itr.next();
				TrainingPlanWorkoutWeekNumberMapping obj = (TrainingPlanWorkoutWeekNumberMapping)db_week_hash.get(key);
				TrainingPlanWorkoutWeekNumberMappingPeer.doDelete(obj);
			}
		}
    }
	*/
	
	public boolean
	isAdHoc() {
		return ( this.workout.getIsAdHoc() == (short)1 );
	}
	
	public void
	setIsAdHoc(boolean _b) {
		this.workout.setIsAdHoc( _b ? (short)1: (short)0 );
	}
	
	
}
