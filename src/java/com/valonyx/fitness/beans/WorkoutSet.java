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
WorkoutSet
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Long,WorkoutSet> hash = new HashMap<Long,WorkoutSet>();
	
    // CLASS METHODS

    public static WorkoutSet
    getSet(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		WorkoutSet plan_obj = (WorkoutSet)hash.get(_id);
		if (plan_obj == null) {
			Criteria crit = new Criteria();
			crit.add(SetDbPeer.SET_DB_ID, _id);
			List objList = SetDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate set with id: " + _id);
			}

			plan_obj = WorkoutSet.getSet((SetDb)objList.get(0));
		}

		return plan_obj;
    }

    public static WorkoutSet
    getSet(SetDb _obj) throws TorqueException {
		
		WorkoutSet plan_obj = (WorkoutSet)hash.get(_obj.getSetDbId());
		if (plan_obj == null) {
			plan_obj = new WorkoutSet(_obj);
			hash.put(_obj.getSetDbId(), plan_obj);
		}

		return plan_obj;
    }

    public static WorkoutSet
    getSet(Workout _w, TrainingPlanSet _s)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		PersonBean person_obj = _w.getPerson();
		Criteria crit = new Criteria();
		crit.add(SetDbPeer.WORKOUT_DB_ID, _w.getId());
		crit.add(SetDbPeer.TRAINING_PLAN_SET_DB_ID, _s.getId());
		System.out.println("getSet crit >" + crit.toString());
		List l = SetDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			//System.out.println("creating new WorkoutSet");
			WorkoutSet set = new WorkoutSet();
			set.setParent(_w);
			set.setParent(_s);
			set.setActualReps(_s.getRepGoalMin(person_obj)); // default
			set.setActualWeight(_s.getWeightGoal(person_obj)); // default
			set.save();
			return set;
		} else if (l.size() == 1) {
			return WorkoutSet.getSet((SetDb)l.get(0));
		} else {
			// this is pretty lame - added 4/5/18
			try {
				for (int i = 1; i < l.size(); i++) {
					SetDb set_obj = (SetDb)l.get(i);
					SetDbPeer.doDelete(set_obj);
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
			//throw new UniqueObjectNotFoundException("Unique set not found for workout " + _w.getLabel() + " and set " + _s.getLabel(person_obj));
			return WorkoutSet.getSet((SetDb)l.get(0));
		}
    }

    public static LinkedHashMap<Move,ArrayList<WorkoutSet>>
    getSets(Workout _w)
		throws TorqueException, ObjectNotFoundException	{
		
		LinkedHashMap<Move,ArrayList<WorkoutSet>> hash = new LinkedHashMap(7);
		
		Criteria crit = new Criteria();
		crit.add(SetDbPeer.WORKOUT_DB_ID, _w.getId());
		crit.add(SetDbPeer.SET_DATE, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(SetDbPeer.SET_DATE);
		System.out.println("getSets crit >" + crit.toString());
		Iterator itr = SetDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			WorkoutSet obj = WorkoutSet.getSet((SetDb)itr.next());
			// get the Move for the set
			try {
				Move move_for_set = obj.getMove();
				ArrayList<WorkoutSet> sets_for_move = null;
				if (hash.containsKey(move_for_set)) {
					sets_for_move = hash.get(move_for_set);
				} else {
					sets_for_move = new ArrayList();
				}
				sets_for_move.add(obj);
				hash.put(move_for_set, sets_for_move);
			} catch (ObjectNotFoundException x) {
				System.out.println("Unable to find the move for set for workout >" + _w.getValue());
				x.printStackTrace();
			}
		}
		
		return hash;
    }

    public static ArrayList<WorkoutSet>
    getSets(Workout _w, Move _move)
		throws TorqueException, ObjectNotFoundException	{
		
		//LinkedHashMap<Move,ArrayList<WorkoutSet>> hash = new LinkedHashMap(7);
		ArrayList<WorkoutSet> l = new ArrayList<WorkoutSet>();
		
		Criteria crit = new Criteria();
		crit.add(SetDbPeer.WORKOUT_DB_ID, _w.getId());
		crit.add(SetDbPeer.SET_DATE, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(SetDbPeer.SET_DATE);
		System.out.println("getSets crit >" + crit.toString());
		Iterator itr = SetDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			WorkoutSet obj = WorkoutSet.getSet((SetDb)itr.next());
			// get the Move for the set
			try {
				Move move_for_set = obj.getMove();
				if (move_for_set.equals(_move)) {
					l.add(obj);
				}
			} catch (ObjectNotFoundException x) {
				System.out.println("Unable to find the move for set for workout >" + _w.getValue());
				x.printStackTrace();
			}
		}
		
		return l;
    }
	
	private static HashMap<String,Long> lastCorrespondingWorkoutSetHash = null;
	
	public static WorkoutSet
	getLastCorrespondingWorkoutSet(PersonBean _person, TrainingPlanSet _s) throws TorqueException, ObjectNotFoundException {
		
		if (lastCorrespondingWorkoutSetHash == null) {
			lastCorrespondingWorkoutSetHash = new HashMap();
		}
		
		String key = _person.getId() + "-" + _s.getId();
		if (lastCorrespondingWorkoutSetHash.containsKey(key)) {
			Long value = lastCorrespondingWorkoutSetHash.get(key);
			if (value == 0l) {
				throw new ObjectNotFoundException("Last set not found.");
			}
			return WorkoutSet.getSet(value);
		}
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);
		
		Criteria crit = new Criteria();
		crit.addJoin(WorkoutDbPeer.WORKOUT_DB_ID, SetDbPeer.WORKOUT_DB_ID);
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		crit.add(SetDbPeer.TRAINING_PLAN_SET_DB_ID, _s.getId());
		crit.add(SetDbPeer.SET_DATE, start_of_day.getTime(), Criteria.LESS_THAN); // make sure we're not grabbing anything on today's date
		crit.addDescendingOrderByColumn(SetDbPeer.SET_DATE);
		System.out.println("getLastCorrespondingWorkoutSet crit >" + crit.toString());
		List l = SetDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			lastCorrespondingWorkoutSetHash.put(key, 0l);
			throw new ObjectNotFoundException("Last set not found.");
		}
		WorkoutSet obj = WorkoutSet.getSet((SetDb)l.get(0));
		lastCorrespondingWorkoutSetHash.put(key, obj.getId());
		return obj;
	}
	
	private static HashMap<String,Long> lastCorrespondingMoveSetHash = null;
	
	public static WorkoutSet
	getLastCorrespondingWorkoutSet(PersonBean _person, Move _move, short _setNumber) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		if (lastCorrespondingMoveSetHash == null) {
			lastCorrespondingMoveSetHash = new HashMap();
		}
		
		String key = _person.getId() + "-" + _move.getId() + "-" + _setNumber;
		if (lastCorrespondingMoveSetHash.containsKey(key)) {
			Long value = lastCorrespondingMoveSetHash.get(key);
			if (value == 0l) {
				throw new ObjectNotFoundException("Last set not found.");
			}
			return WorkoutSet.getSet(value);
		}
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);
		
		// only select sets from the last time this workout was actually done
		
		Workout last_workout_containing_move = Workout.getLastWorkoutContainingMove(_person, _move);
		
		Criteria crit = new Criteria();
		
		crit.addJoin(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_EXCERCISE_DB_ID, TrainingPlanSetDbPeer.TRAINING_PLAN_EXCERCISE_DB_ID);
		crit.addJoin(TrainingPlanSetDbPeer.TRAINING_PLAN_SET_DB_ID, SetDbPeer.TRAINING_PLAN_SET_DB_ID);
		crit.addJoin(SetDbPeer.WORKOUT_DB_ID, WorkoutDbPeer.WORKOUT_DB_ID);
		
		crit.add(TrainingPlanExcerciseDbPeer.EXCERCISE_DB_ID, _move.getId());
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		crit.add(WorkoutDbPeer.WORKOUT_DB_ID, last_workout_containing_move.getId());
		
		crit.add(SetDbPeer.SET_DATE, start_of_day.getTime(), Criteria.LESS_THAN); // make sure we're not grabbing anything on today's date
		crit.add(TrainingPlanSetDbPeer.SET_NUMBER, _setNumber);
		crit.addDescendingOrderByColumn(SetDbPeer.SET_DATE);
		crit.setLimit(1);
		System.out.println("getLastCorrespondingWorkoutSetXXy crit >" + crit.toString());
		List l = SetDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			lastCorrespondingMoveSetHash.put(key, 0l);
			throw new ObjectNotFoundException("Last set not found.");
		}
		WorkoutSet obj = WorkoutSet.getSet((SetDb)l.get(0));
		lastCorrespondingMoveSetHash.put(key, obj.getId());
		return obj;
	}
	
	public static Vector
	getLastCorrespondingWorkoutSets(PersonBean _person, Vector _sets, int _num_workouts) throws TorqueException, ObjectNotFoundException {
		
		Criteria crit = new Criteria();
		
		if (!_sets.isEmpty()) {
			long[] arr = new long[_sets.size()];
			for (int i = 0; i < _sets.size(); i++) {
				TrainingPlanSet set_obj = (TrainingPlanSet)_sets.elementAt(i);
				arr[i] = set_obj.getId();
			}
			crit.add(SetDbPeer.TRAINING_PLAN_SET_DB_ID, (Object)arr, Criteria.IN);
		}
		
		Vector vec = new Vector();
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);
		
		crit.addJoin(WorkoutDbPeer.WORKOUT_DB_ID, SetDbPeer.WORKOUT_DB_ID);
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		
		crit.add(SetDbPeer.SET_DATE, start_of_day.getTime(), Criteria.LESS_THAN); // make sure we're not grabbing anything on today's date
		crit.addDescendingOrderByColumn(SetDbPeer.SET_DATE);
		//crit.addAscendingOrderByColumn(SetDbPeer.SET_DATE);
		System.out.println("getLastCorrespondingWorkoutSet crit >" + crit.toString());
		List l = SetDbPeer.doSelect(crit);
		
		int num_workouts_found = 1;
		Workout last_workout = null;
		//Iterator itr = SetDbPeer.doSelect(crit).iterator();
		for (int i = (l.size() - 1); i >= 0; i--) {
			WorkoutSet obj = WorkoutSet.getSet((SetDb)l.get(i));
			Workout workout_obj = obj.getWorkout();
			if (last_workout != null) {
				if (!workout_obj.equals(last_workout)) {
					num_workouts_found++;
				}
			}
			if (num_workouts_found > _num_workouts) {
				return vec;
			}
			vec.addElement(obj);
			last_workout = workout_obj;
		}
		
		return vec;
	}
	
	public static Vector
	getLastWorkoutSetsForMove(PersonBean _person, Move _move, int _num_workouts) throws TorqueException, ObjectNotFoundException {
		
		Criteria crit = new Criteria();
		
		Vector vec = new Vector();
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);
		
		crit.addJoin(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_EXCERCISE_DB_ID, TrainingPlanSetDbPeer.TRAINING_PLAN_EXCERCISE_DB_ID);
		crit.addJoin(TrainingPlanSetDbPeer.TRAINING_PLAN_SET_DB_ID, SetDbPeer.TRAINING_PLAN_SET_DB_ID);
		crit.addJoin(SetDbPeer.WORKOUT_DB_ID, WorkoutDbPeer.WORKOUT_DB_ID);
		
		crit.add(TrainingPlanExcerciseDbPeer.EXCERCISE_DB_ID, _move.getId());
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		
		crit.add(SetDbPeer.SET_DATE, start_of_day.getTime(), Criteria.LESS_THAN); // make sure we're not grabbing anything on today's date
		crit.addDescendingOrderByColumn(SetDbPeer.SET_DATE);
		//crit.addAscendingOrderByColumn(SetDbPeer.SET_DATE);
		
		//System.out.println("getLastCorrespondingWorkoutSetXX crit >" + crit.toString());
		List l = SetDbPeer.doSelect(crit);
		
		int num_workouts_found = 1;
		Workout last_workout = null;
		//
		//for (int i = (l.size() - 1); i >= 0; i--) {
		
		Iterator itr = l.iterator();
		//for (int i = 0; i < l.size(); i++) {
		while (itr.hasNext()) {
			//WorkoutSet obj = WorkoutSet.getSet((SetDb)l.get(i));
			WorkoutSet obj = WorkoutSet.getSet((SetDb)itr.next());
			Workout workout_obj = obj.getWorkout();
			if (last_workout != null) {
				if (!workout_obj.equals(last_workout)) {
					num_workouts_found++;
				}
			}
			if (num_workouts_found > _num_workouts) {
				//return vec;
				break;
			}
			vec.addElement(obj);
			last_workout = workout_obj;
		}
		
		// reverse the vec // shrug
		
		Vector reversed = new Vector();
		for (int i = (vec.size() - 1) ; i >= 0; i--) {
			reversed.addElement(vec.elementAt(i));
		}
		
		return reversed;
	}
	
	/*
	public static WorkoutSet
	getCurrentCorrespondingWorkoutSet(TrainingPlanSet _s) throws TorqueException, ObjectNotFoundException {
		
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);
		
		Criteria crit = new Criteria();
		crit.add(SetDbPeer.TRAINING_PLAN_SET_DB_ID, _s.getId());
		crit.and(SetDbPeer.SET_DATE, start_of_day.getTime(), Criteria.LESS_THAN); // make sure we're not grabbing anything on today's date
		crit.addDescendingOrderByColumn(SetDbPeer.SET_DATE);
		System.out.println("getLastCorrespondingWorkoutSet crit >" + crit.toString());
		List l = SetDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			throw new ObjectNotFoundException("Last set not found.");
		} else {
			return WorkoutSet.getSet((SetDb)l.get(0));
		}
	}
	*/
	

    // SQL

    /*

<table name="SET_DB" idMethod="native">
	<column name="SET_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
    <column name="WORKOUT_DB_ID" primaryKey="true" required="true" type="BIGINT"/>
    <column name="TRAINING_PLAN_SET_DB_ID" primaryKey="true" required="true" type="BIGINT"/>
	
	<column name="ACTUAL_REPS" required="true" type="SMALLINT"/>
	<column name="ACTUAL_WEIGHT" required="true" scale="2" size="5" type="DECIMAL"/>
	<column name="SET_DATE" required="true" type="DATE"/>

    <foreign-key foreignTable="WORKOUT_DB">
		<reference local="WORKOUT_DB_ID" foreign="WORKOUT_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="TRAINING_PLAN_SET_DB">
		<reference local="TRAINING_PLAN_SET_DB_ID" foreign="TRAINING_PLAN_SET_DB_ID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private SetDb set;

    // CONSTRUCTORS

    public
    WorkoutSet() {
		set = new SetDb();
		isNew = true;
    }

    public
    WorkoutSet(SetDb _obj) {
		set = _obj;
		isNew = false;
    }

    // INSTANCE METHODS

    public long
    getId() {
		return set.getSetDbId();
    }

    public String
    getLabel() {
		return this.getValue();
    }
	

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		//set.setSetDate(new Date()); // set this on set completion
		set.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		/* set this on set completion
		if (this.getWorkout().isCurrent()) {
			set.setSetDate(new Date());
		}
		*/
		set.save();
    }
	
	public void
	setComplete(boolean _b) {
		set.setSetDate(_b ? new Date() : null);
	}
	
	public boolean
	isComplete() {
		return ( set.getSetDate() != null );
	}
	
	public void
	toggleComplete() {
		set.setSetDate( (set.getSetDate() == null) ? new Date() : null);
	}

	public short getActualReps() {
		return set.getActualReps();
	}

	public String getActualRepsString() {
		short actual_reps = set.getActualReps();
		if (actual_reps == 0) {
			return "n/a";
		}
		return actual_reps + "";
	}

	public void setActualReps(short _s) {
		set.setActualReps(_s);
	}

	public BigDecimal getActualWeight() {
		return set.getActualWeight();
	}
	
	public boolean
	equals(WorkoutSet _set) {
		if (_set == null) {
			return false;
		}
		if (this.getActualReps() != _set.getActualReps()) {
			return false;
		}
		if ( (this.getActualWeight() == null) && (_set.getActualWeight() == null) ) {
			return true;
		}
		if ( (this.getActualWeight() == null) || (_set.getActualWeight() == null) ) {
			return false;
		}
		return (this.getActualWeight().compareTo(_set.getActualWeight()) == 0);
	}

	public BigDecimal getActualRPE() {
		return set.getActualRpe();
	}

	public String getActualRPEString() {
		BigDecimal actual = set.getActualRpe();
		if (actual == null) {
			return "";
		}
		String str = this.getStringFromBigDecimal(actual, 1);
		if (str.equals("5")) {
			return "Sub 6";
		}
		return str; 
	}

	public void setActualRPE(BigDecimal _bd) {
		set.setActualRpe(_bd);
	}

	public String getActualWeightString() {
		BigDecimal actual = set.getActualWeight();
		if (actual == null) {
			return "";
		}
		return this.getStringFromBigDecimal(actual, 2);
	}

	public String getActualWeightAndRPEString() {
		BigDecimal actual = set.getActualWeight();
		if (actual == null) {
			return "";
		}
		String rpe = this.getActualRPEString();
		if (rpe.isEmpty()) {
			return this.getStringFromBigDecimal(actual, 2);
		} else {
			/*
			if (rpe.equals("Sub 6")) {
				return this.getStringFromBigDecimal(actual, 2) + " " + rpe + " RPE";
			} else {
				return this.getStringFromBigDecimal(actual, 2) + " " + rpe + "RPE";
			}
			*/
			return this.getStringFromBigDecimal(actual, 2) + " " + rpe + " RPE";
		}
		
	}

	public void setActualWeight(BigDecimal _bd) {
		set.setActualWeight(_bd);
	}

	public Date getDate() {
		return set.getSetDate();
	}

	public String getSetTimeString() {
		Date dt = set.getSetDate();
		if (dt == null) {
			return "";
		}
		return CUBean.getUserTimeString(dt);
	}

	public String getSetDateString() {
		Date dt = set.getSetDate();
		if (dt == null) {
			return "";
		}
		return CUBean.getUserDateString(dt, "M/d/yy");
	}

	public String getSetDateShortString() {
		Date dt = set.getSetDate();
		if (dt == null) {
			return "";
		}
		return CUBean.getUserDateString(dt, "M/d");
	}

	public String getSetDateTimeString() {
		Date dt = set.getSetDate();
		if (dt == null) {
			return "";
		}
		return CUBean.getUserDateString(dt, "MM/dd/yyyy HH:mm");
	}

	public void setDate(Date _d) {
		set.setSetDate(_d);
	}
	
	public void
	setParent(Workout _w) throws TorqueException {
		set.setWorkoutDbId(_w.getId());
	}
	
	public void
	setParent(TrainingPlanSet _set) throws TorqueException {
		set.setTrainingPlanSetDbId(_set.getId());
	}
	
	public void
	setWorkout(Workout _w) throws TorqueException {
		set.setWorkoutDbId(_w.getId());
	}
	
	public Workout
	getWorkout() throws TorqueException, ObjectNotFoundException {
		return Workout.getWorkout(set.getWorkoutDbId());
	}
	
	public void
	setTrainingPlanSet(TrainingPlanSet _set) throws TorqueException {
		set.setTrainingPlanSetDbId(_set.getId());
	}
	
	public TrainingPlanSet
	getTrainingPlanSet() throws TorqueException, ObjectNotFoundException {
		return TrainingPlanSet.getTrainingPlanSet(set.getTrainingPlanSetDbId());
	}
	
	
	private String
	getStringFromBigDecimal(BigDecimal _bd, int _scale) {
		if (_bd == null) {
			return "";
		}
		String str = _bd.setScale(_scale, RoundingMode.HALF_UP).toString();
		int index = str.indexOf(".00");
		if (index > -1) {
			return str.substring(0, index);
		}
		index = str.indexOf(".0");
		if (index > -1) {
			return str.substring(0, index);
		}
		return str;
	}
	
	
	public Move
	getMove() throws TorqueException, ObjectNotFoundException {
		return this.getTrainingPlanSet().getParent().getMove();
	}
	
	

	public String getThumbnailURLString() {
		String s = set.getThumbnailUrl();
		if (s == null) {
			return "";
		}
		return s;
	}

	public void setThumbnailURL(String _s) {
		set.setThumbnailUrl(_s);
	}

	public String getVideoURLString() {
		String s = set.getVideoUrl();
		if (s == null) {
			return "";
		}
		return s;
	}

	public void setVideoURL(String _s) {
		set.setVideoUrl(_s);
	}
	
	public BigDecimal
	getCalculated1RepMax() throws IllegalValueException {
		if (this.getActualWeight() != null) {

			short actual_reps = this.getActualReps();
			if (this.getActualRPE() != null) {
				short rir = (short)(10 - this.getActualRPE().shortValue()); // this'll strip off any decimal values
				actual_reps += rir;
			}

			return Move.getCalculated1RM(actual_reps, this.getActualWeight());
		}
		return BigDecimal.ZERO;
	}
	
}