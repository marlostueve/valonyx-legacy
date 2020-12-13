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
import java.util.concurrent.TimeUnit;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;



/**
 *
 * @author marlo
 */
public class
Workout
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Long,Workout> hash = new HashMap<Long,Workout>();
	
    // CLASS METHODS
	
	/**
	* Get a diff between two dates
	* @param date1 the oldest date
	* @param date2 the newest date
	* @param timeUnit the unit in which you want the diff
	* @return the diff value, in the provided unit
	*/
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	   long diffInMillies = date2.getTime() - date1.getTime();
	   return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
    public static void
    delete(long _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(SetDbPeer.WORKOUT_DB_ID, _id);
		SetDbPeer.doDelete(crit);
		
		crit = new Criteria();
		crit.add(WorkoutDbPeer.WORKOUT_DB_ID, _id);
		WorkoutDbPeer.doDelete(crit);
		hash.remove(_id);
    }

    public static Workout
    getWorkout(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		Workout workout_obj = (Workout)hash.get(_id);
		if (workout_obj == null) {
			Criteria crit = new Criteria();
			crit.add(WorkoutDbPeer.WORKOUT_DB_ID, _id);
			List objList = WorkoutDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate workout with id: " + _id);
			}

			workout_obj = Workout.getWorkout((WorkoutDb)objList.get(0));
		}

		return workout_obj;
    }

    public static Workout
    getWorkout(WorkoutDb _obj) throws TorqueException {
		
		Workout workout_obj = (Workout)hash.get(_obj.getWorkoutDbId());
		if (workout_obj == null) {
			workout_obj = new Workout(_obj);
			hash.put(_obj.getWorkoutDbId(), workout_obj);
		}

		return workout_obj;
    }

    public static boolean
    hasCurrentWorkout(PersonBean _person)
		throws TorqueException {
		Criteria crit = new Criteria();
		//crit.addJoin(PersonTrainingPlanMappingPeer.TRAINING_PLAN_DB_ID, TrainingPlanWorkoutDbPeer.TRAINING_PLAN_DB_ID);
		//crit.addJoin(TrainingPlanWorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, WorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID);
		crit.add(WorkoutDbPeer.WORKOUT_START_DATE, (Object)"", Criteria.ISNOTNULL);
		crit.add(WorkoutDbPeer.WORKOUT_END_DATE, (Object)"", Criteria.ISNULL);
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		return !WorkoutDbPeer.doSelect(crit).isEmpty();
    }

    public static boolean
    isWorkoutInProgress(TrainingPlanWorkout _workout, PersonBean _person)
		throws TorqueException {
		Criteria crit = new Criteria();
		//crit.addJoin(PersonTrainingPlanMappingPeer.TRAINING_PLAN_DB_ID, TrainingPlanWorkoutDbPeer.TRAINING_PLAN_DB_ID);
		//crit.addJoin(TrainingPlanWorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, WorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID);
		crit.add(WorkoutDbPeer.WORKOUT_START_DATE, (Object)"", Criteria.ISNOTNULL);
		crit.add(WorkoutDbPeer.WORKOUT_END_DATE, (Object)"", Criteria.ISNULL);
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		crit.add(WorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, _workout.getId());
		return !WorkoutDbPeer.doSelect(crit).isEmpty();
    }

    public static Workout
    getCurrentWorkout(PersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Criteria crit = new Criteria();
		//crit.addJoin(PersonTrainingPlanMappingPeer.TRAINING_PLAN_DB_ID, TrainingPlanWorkoutDbPeer.TRAINING_PLAN_DB_ID);
		//crit.addJoin(TrainingPlanDbPeer.TRAINING_PLAN_DB_ID, TrainingPlanWorkoutDbPeer.TRAINING_PLAN_DB_ID);
		//crit.addJoin(TrainingPlanWorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, WorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID);
		crit.add(WorkoutDbPeer.WORKOUT_START_DATE, (Object)"", Criteria.ISNOTNULL);
		crit.add(WorkoutDbPeer.WORKOUT_END_DATE, (Object)"", Criteria.ISNULL);
		//crit.add(TrainingPlanDbPeer.PERSON_ID, _person.getId()); // 10-25-17 - need to get this from the map
		//crit.add(PersonTrainingPlanMappingPeer.PERSON_ID, _person.getId());
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		System.out.println("getCurrentWorkout crit >" + crit.toString());
		List l = WorkoutDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			throw new ObjectNotFoundException("Current workout not found");
		} else if (l.size() == 1) {
			return Workout.getWorkout((WorkoutDb)l.get(0));
		} else {
			throw new ObjectNotFoundException("Unique current workout not found");
		}
		
    }

    public static Workout
    getLastWorkout(PersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Criteria crit = new Criteria();
		//crit.add(WorkoutDbPeer.WORKOUT_START_DATE, (Object)"", Criteria.ISNOTNULL);
		//crit.add(WorkoutDbPeer.WORKOUT_END_DATE, (Object)"", Criteria.ISNULL);
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(WorkoutDbPeer.WORKOUT_END_DATE);
		crit.setLimit(1);
		//System.out.println("getLastWorkout crit >" + crit.toString());
		List l = WorkoutDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			throw new ObjectNotFoundException("Last workout not found");
		} else {
			return Workout.getWorkout((WorkoutDb)l.get(0));
		}
    }

    public static Workout
    getLastWorkout(PersonBean _person, Date _prior_to_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Criteria crit = new Criteria();
		//crit.add(WorkoutDbPeer.WORKOUT_START_DATE, (Object)"", Criteria.ISNOTNULL);
		//crit.add(WorkoutDbPeer.WORKOUT_END_DATE, (Object)"", Criteria.ISNULL);
		crit.add(WorkoutDbPeer.WORKOUT_END_DATE, _prior_to_date, Criteria.LESS_THAN);
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(WorkoutDbPeer.WORKOUT_END_DATE);
		crit.setLimit(1);
		System.out.println("getLastWorkout(PersonBean,Date) crit >" + crit.toString());
		List l = WorkoutDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			throw new ObjectNotFoundException("Last workout not found");
		} else {
			return Workout.getWorkout((WorkoutDb)l.get(0));
		}
    }

    public static ArrayList<Workout>
    getLastWorkouts(PersonBean _person, int _num)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		ArrayList<Workout> vec = new ArrayList();
		Criteria crit = new Criteria();
		//crit.add(WorkoutDbPeer.WORKOUT_START_DATE, (Object)"", Criteria.ISNOTNULL);
		//crit.add(WorkoutDbPeer.WORKOUT_END_DATE, (Object)"", Criteria.ISNULL);
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(WorkoutDbPeer.WORKOUT_END_DATE);
		crit.setLimit(_num);
		System.out.println("getLastWorkout crit >" + crit.toString());
		Iterator itr = WorkoutDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.add(Workout.getWorkout((WorkoutDb)itr.next()));
		}
		return vec;
    }

	public static HashMap<PersonBean,HashMap> last_workout_containing_move_hash = null;
    public static Workout
    getLastWorkoutContainingMove(PersonBean _person, Move _move)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		if (Workout.last_workout_containing_move_hash == null) {
			Workout.last_workout_containing_move_hash = new HashMap<PersonBean,HashMap>(11);
		}
		
		HashMap<Move,Workout> move_workout_hash = null;
		if (Workout.last_workout_containing_move_hash.containsKey(_person)) {
			move_workout_hash = Workout.last_workout_containing_move_hash.get(_person);
			if (move_workout_hash.containsKey(_move)) {
				return move_workout_hash.get(_move);
			}
		}
		
		if (move_workout_hash == null) {
			move_workout_hash = new HashMap<Move,Workout>(5);
			Workout.last_workout_containing_move_hash.put(_person, move_workout_hash);
		}
		
		Criteria crit = new Criteria();
		crit.addJoin(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_WORKOUT_DB_ID, WorkoutDbPeer.TRAINING_PLAN_WORKOUT_DB_ID);
		crit.add(TrainingPlanExcerciseDbPeer.EXCERCISE_DB_ID, _move.getId());
		crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(WorkoutDbPeer.WORKOUT_END_DATE);
		crit.setLimit(1);
		System.out.println("getLastWorkoutContainingMove crit >" + crit.toString());
		List l = WorkoutDbPeer.doSelect(crit);
		if (l.isEmpty()) {
			throw new ObjectNotFoundException("Last workout not found");
		} else {
			Workout workout_obj = Workout.getWorkout((WorkoutDb)l.get(0));
			move_workout_hash.put(_move, workout_obj);
			return workout_obj;
		}
		
    }
	
	private static void invalidateLastWorkoutForMoveHash(PersonBean _person) {
		if (Workout.last_workout_containing_move_hash != null) {
			Workout.last_workout_containing_move_hash.remove(_person);
		}
	}

	

    // SQL

    /*

<table name="WORKOUT_DB" idMethod="native">
	<column name="WORKOUT_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	<column name="TRAINING_PLAN_WORKOUT_DB_ID" required="true" type="BIGINT"/>
    <column name="CURRENT_MOVE_ID" required="true" type="BIGINT"/>
	<column name="PERSON_ID" required="false" type="INTEGER"/>
	
	<column name="WORKOUT_START_DATE" required="true" type="DATE"/>
	<column name="WORKOUT_END_DATE" required="false" type="DATE"/>
	
	<column name="LOCATION" required="false" size="200" type="VARCHAR"/>
	<column name="NOTES" type="LONGVARCHAR"/>
	
    <foreign-key foreignTable="TRAINING_PLAN_WORKOUT_DB">
		<reference local="TRAINING_PLAN_WORKOUT_DB_ID" foreign="TRAINING_PLAN_WORKOUT_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="TRAINING_PLAN_EXCERCISE_DB">
		<reference local="CURRENT_MOVE_ID" foreign="TRAINING_PLAN_EXCERCISE_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private WorkoutDb workout;
	private Vector moves = null;

    // CONSTRUCTORS

    public
    Workout() {
		workout = new WorkoutDb();
		isNew = true;
    }

    public
    Workout(WorkoutDb _obj) {
		workout = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public boolean
	equals(Workout _w) {
		if (_w == null) {
			return false;
		}
		return ( this.getId() == _w.getId() );
	}

    public long
    getId() {
		return workout.getWorkoutDbId();
    }

    public String
    getLabel() throws TorqueException, ObjectNotFoundException {
		return this.getParent().getLabel();
    }
	
	public boolean
	isComplete() {
		return ( workout.getWorkoutEndDate() != null );
	}
	
	public String getNotes() {
		String str = workout.getNotes();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setNotes(String _s) {
		workout.setNotes(_s);
	}

	public Date getStartDate() {
		return workout.getWorkoutStartDate();
	}
	
    public long getStartDateUnixTimestamp() {
		return this.workout.getWorkoutStartDate().getTime() / 1000;
    }

	public void setStartDate(Date _d) {
		workout.setWorkoutStartDate(_d);
	}

	public Date getEndDate() {
		return workout.getWorkoutEndDate();
	}
	
    public long getEndDateUnixTimestamp() {
		Date dt = this.workout.getWorkoutEndDate();
		if (dt == null) {
			return 0l;
		}
		return dt.getTime() / 1000;
    }

	public void setEndDate(Date _d) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		workout.setWorkoutEndDate(_d);
		this.getParent().invalidate();
		Workout.invalidateLastWorkoutForMoveHash(this.getPerson());
	}
	
	public String
	getEndDateString() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		Date dt = workout.getWorkoutEndDate();
		if (dt == null) {
			return "";
		}
		
		String dateFormatString = "M/d/yy h:mm a";
		TimeZone timeZone = WeightPreferences.getWeightPreference(this.getPerson()).getTimeZone();
		
		String nowStr = CUBean.getUserDateString(new Date(), timeZone);
		String entryDateStr = CUBean.getUserDateString(dt, timeZone);
		if (nowStr.equals(entryDateStr)) {
			return "Today, " + CUBean.getUserDateString(dt, dateFormatString, timeZone);
		}
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		String yesterdayStr = CUBean.getUserDateString(yesterday.getTime(), timeZone);
		if (yesterdayStr.equals(entryDateStr)) {
			return "Yesterday, " + CUBean.getUserDateString(dt, dateFormatString, timeZone);
		}
		
		return  CUBean.getUserDateString(dt, "EEEE, M/d/yy h:mm a", timeZone);
		
		//return CUBean.getUserDateString(dt, preferences.getTimeZone()) + " " + CUBean.getUserTimeString(dt, preferences.getTimeZone());
	}

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		workout.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		workout.save();
    }
	
	public void
	setParent(TrainingPlanWorkout _plan_workout) throws TorqueException {
		workout.setTrainingPlanWorkoutDbId(_plan_workout.getId());
	}
	
	public TrainingPlanWorkout
	getParent() throws TorqueException, ObjectNotFoundException {
		return TrainingPlanWorkout.getTrainingPlanWorkout(workout.getTrainingPlanWorkoutDbId());
	}
	
	public void
	setPerson(PersonBean _person) throws TorqueException {
		workout.setPersonId(_person.getId());
	}
	
	public UKOnlinePersonBean
	getPerson() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(workout.getPersonId());
	}
	
	public boolean
	isCurrent() {
		return ( (workout.getWorkoutStartDate() != null) && (workout.getWorkoutEndDate() == null) );
	}
	
	public void
	setCurrentMove(TrainingPlanMove _move) throws TorqueException {
		workout.setCurrentMoveId(_move.getId());
	}
	
	private Vector
	getMoves() throws TorqueException, ObjectNotFoundException {
		if (this.moves == null) {
			// make a copy
			this.moves = (Vector)this.getParent().getMoves().clone();
		}
		return this.moves;
	}
	
	public TrainingPlanMove
	getCurrentMove() throws TorqueException, ObjectNotFoundException, Exception {
		try {
			return TrainingPlanMove.getTrainingPlanMove(workout.getCurrentMoveId());
		} catch (ObjectNotFoundException x) {
			x.printStackTrace();
			// the current move no longer exists - return the first move, I guess
			Vector moves = this.getMoves();
			if (moves.isEmpty()) {
				throw new ObjectNotFoundException("No moves found for workout >" + this.getLabel());
			} else {
				TrainingPlanMove move = (TrainingPlanMove)moves.get(0);
				workout.setCurrentMoveId(move.getId());
				workout.save();
				return move;
			}
		}
	}
	
	public boolean
	hasNextMove() throws TorqueException, ObjectNotFoundException, Exception {
		
		System.out.println("hasNextMove() invoked");
		//this.ha
		
		try {
			System.out.println("this.getCurrentMove() >" + this.getCurrentMove());
			
			TrainingPlanMove current_move = this.getCurrentMove();
			Iterator itr = this.getMoves().iterator();
			while (itr.hasNext()) {
				TrainingPlanMove move_obj = (TrainingPlanMove)itr.next();
				if (move_obj.equals(current_move)) {
					return itr.hasNext();
				}
			}
			return false;
		} catch (ObjectNotFoundException x) {
			x.printStackTrace();
			return false;
		}
	}
	
	public TrainingPlanMove
	nextMove() throws TorqueException, ObjectNotFoundException, ObjectAlreadyExistsException, Exception {
		TrainingPlanMove current_move = this.getCurrentMove();
		Iterator itr = this.getMoves().iterator();
		while (itr.hasNext()) {
			TrainingPlanMove move_obj = (TrainingPlanMove)itr.next();
			if (move_obj.equals(current_move)) {
				TrainingPlanMove next_move_obj = (TrainingPlanMove)itr.next();
				this.setCurrentMove(next_move_obj);
				this.save();
				return next_move_obj;
			}
		}
		throw new ObjectNotFoundException("Next move not found!");
	}
	
	public boolean
	hasPreviousMove() throws TorqueException, ObjectNotFoundException, Exception {
		TrainingPlanMove current_move = this.getCurrentMove();
		Iterator itr = this.getMoves().iterator();
		for (int i = 0; itr.hasNext(); i++) {
			TrainingPlanMove move_obj = (TrainingPlanMove)itr.next();
			if (move_obj.equals(current_move)) {
				return ( i > 0 );
			}
		}
		return false;
	}
	
	public TrainingPlanMove
	previousMove() throws TorqueException, ObjectNotFoundException, ObjectAlreadyExistsException, Exception {
		TrainingPlanMove current_move = this.getCurrentMove();
		Vector moves = this.getMoves();
		for (int i = 0; i < moves.size(); i++) {
			TrainingPlanMove move_obj = (TrainingPlanMove)moves.elementAt(i);
			if (move_obj.equals(current_move)) {
				if (i == 0) {
					throw new ObjectNotFoundException("Previous move not found!");
				}
				TrainingPlanMove previous_move_obj = (TrainingPlanMove)moves.elementAt(i - 1);
				this.setCurrentMove(previous_move_obj);
				this.save();
				return previous_move_obj;
			}
		}
		throw new ObjectNotFoundException("Previous move not found!");
	}
	
	public TrainingPlanMove
	lastMove() throws TorqueException, ObjectNotFoundException, ObjectAlreadyExistsException, Exception {
		//TrainingPlanMove current_move = this.getCurrentMove();
		Vector moves = this.getMoves();
		if (!moves.isEmpty()) {
			TrainingPlanMove last_move_obj = (TrainingPlanMove)moves.lastElement();
			this.setCurrentMove(last_move_obj);
			this.save();
			return last_move_obj;
		}
		throw new ObjectNotFoundException("Previous move not found!");
	}
	
	public boolean
	hasMoves() throws TorqueException, ObjectNotFoundException {
		return !this.getMoves().isEmpty();
	}
	
	
	public TrainingPlanMove
	replaceMove(Move _move) throws TorqueException, ObjectNotFoundException, ObjectAlreadyExistsException, Exception {
		
		this.getMoves();
		TrainingPlanMove current_move = this.getCurrentMove();
		
		// get moves from parent.  if the passed move matches the programmed move, get back with the program
		
		Vector parent_moves = this.getParent().getMoves(this.getPerson()); 
		TrainingPlanMove programmed_move = (TrainingPlanMove)parent_moves.elementAt(current_move.getMoveNumber() - 1);
		System.out.println("found programmed move >" + programmed_move.getLabel());
		/* removed 4/18/19 - not sure why not to allow replacement of alt move
		if (programmed_move.isAlternateMove()) {
			throw new IllegalValueException("Programmed move can't be alternate move!!");
		}
		*/
		if (programmed_move.getMove().equals(_move)) {	
			this.moves.setElementAt(programmed_move, current_move.getMoveNumber() - 1);
			this.setCurrentMove(programmed_move);
			this.save();
			return programmed_move;
		} else {

			TrainingPlanMove new_move = new TrainingPlanMove();
			new_move.setWorkout(this.getParent());
			new_move.setIsSuperSet(current_move.isSuperSet()); // shrug
			new_move.setMove(_move);
			new_move.setIsAlternateMove(true);
			new_move.setAlternateMovePerson(this.getPerson());
			new_move.setMoveNumber(current_move.getMoveNumber());
			new_move.save();
			
			this.getParent().invalidate();

			this.moves.setElementAt(new_move, current_move.getMoveNumber() - 1);

			// create 3 sets for the new move

			Vector sets = new_move.getSets();
			for (short i = 0; i < (short)3; i++) {
				TrainingPlanSet set = new TrainingPlanSet();
				set.setParent(new_move);
				set.setIsDropSet(false);
				set.setIsAMRAP(false);
				set.setIsAMWAP(false);
				set.setIsPerc1RM(false);
				set.setSetNumber(i);
				set.save();
				sets.addElement(set);
			}

			new_move.setSets(sets);
			new_move.save();

			this.setCurrentMove(new_move);
			this.save();

			return new_move;
		}
	}
	
	public TrainingPlanMove
	addMove(Move _move, String _desc) throws TorqueException, ObjectNotFoundException, ObjectAlreadyExistsException, Exception {
		
		Vector moves_in_workout = this.getMoves();
		TrainingPlanMove last_move_in_workout = null;
		if (!moves_in_workout.isEmpty()) {
			last_move_in_workout = (TrainingPlanMove)moves_in_workout.lastElement();
		}

		TrainingPlanMove new_move = new TrainingPlanMove();
		new_move.setWorkout(this.getParent());
		new_move.setIsSuperSet(false); // shrug
		new_move.setMove(_move);
		new_move.setIsAlternateMove(true);
		new_move.setAlternateMovePerson(this.getPerson());
		if (last_move_in_workout == null) {
			new_move.setMoveNumber((short)1);
		} else {
			new_move.setMoveNumber(last_move_in_workout.getMoveNumber());
		}
		new_move.setDescription(_desc);
		new_move.save();

		//this.moves.setElementAt(new_move, current_move.getMoveNumber() - 1);
		this.moves.addElement(new_move);

		this.setCurrentMove(new_move);
		this.save();

		return new_move;
		
	}
	
	public ArrayList<WorkoutSet>
	getSets(TrainingPlanMove _training_plan_move) throws TorqueException, ObjectNotFoundException {
		
		ArrayList<WorkoutSet> vec = new ArrayList<WorkoutSet>();
		
		Vector<TrainingPlanSet> sets = _training_plan_move.getSets();
		
		long[] arr = new long[sets.size()];
		for (int i = 0; i < sets.size(); i++) {
			TrainingPlanSet set = (TrainingPlanSet)sets.elementAt(i);
			arr[i] = set.getId();
		}
		
		Criteria crit = new Criteria();
		
		crit.add(SetDbPeer.WORKOUT_DB_ID, this.getId());
		crit.add(SetDbPeer.TRAINING_PLAN_SET_DB_ID, (Object)arr, Criteria.IN);
		crit.addAscendingOrderByColumn(SetDbPeer.SET_DATE);
		
		System.out.println("getSets(TrainingPlanMove) crit >" + crit.toString());
		Iterator itr = SetDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			SetDb obj = (SetDb)itr.next();
			vec.add(WorkoutSet.getSet(obj));
		}
		
		return vec;
		
	}
	
}