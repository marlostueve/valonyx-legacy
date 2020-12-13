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
Move
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
	public static final short LIFT_TYPE = 1;
	public static final short CARDIO_TYPE = 2;
	
    protected static HashMap<Integer,Move> hash = new HashMap<Integer,Move>();
	
    // CLASS METHODS

    public static Move
    getMove(int _id)
		throws TorqueException, ObjectNotFoundException {
		
		Move move_obj = (Move)hash.get(_id);
		if (move_obj == null) {
			Criteria crit = new Criteria();
			crit.add(ExcerciseDbPeer.EXCERCISE_DB_ID, _id);
			List objList = ExcerciseDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate move with id: " + _id);
			}

			move_obj = Move.getMove((ExcerciseDb)objList.get(0));
		}

		return move_obj;
    }

    private static Move
    getMove(ExcerciseDb _obj) throws TorqueException {
		
		Move move_obj = (Move)hash.get(_obj.getExcerciseDbId());
		if (move_obj == null) {
			move_obj = new Move(_obj);
			hash.put(_obj.getExcerciseDbId(), move_obj);
		}

		return move_obj;
    }

    public static Move
    maintainMove(String _name, short _type, boolean _is_compound) throws TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_name.isEmpty()) {
			throw new IllegalValueException("Please specify a move name to maintain.");
		}
		
		String name = _name.toLowerCase();
		
		if (name.equals("low bar squat")) {
			return Move.getMove(46); // Squat
		} else if ( (name.indexOf("pause") > -1) && (name.indexOf("squat") > -1) ) {
			return Move.getMove(128); // Paused Squat
		} else if (name.indexOf("squat") > -1 ) {
			return Move.getMove(46); // Squat
		} else if ( (name.indexOf("db") > -1) && (name.indexOf("bench") > -1) ) {
			return Move.getMove(135); // Flat DB Bench
		} else if ( (name.indexOf("tricep") > -1) && (name.indexOf("exten") > -1) ) {
			return Move.getMove(31); // Tricep Pushdown
		} else if ( (name.indexOf("tricep") > -1) && (name.indexOf("press") > -1) ) {
			return Move.getMove(31); // Tricep Pushdown
		} else if ( (name.indexOf("soft") > -1) && (name.indexOf("pause") > -1) && (name.indexOf("bp") > -1) ) {
			return Move.getMove(134); // 2 ct. Paused bench
		} else if ( (name.indexOf("lat") > -1) && (name.indexOf("pull") > -1) ) {
			return Move.getMove(101); // Lat Pull Down
		} else if ( (name.indexOf("chin") > -1) && (name.indexOf("up") > -1) ) {
			return Move.getMove(59); // Pull Ups
		} else if ( (name.indexOf("pause") > -1) && (name.indexOf("dead") > -1) ) {
			return Move.getMove(113); // Paused Deadlift
		} else if ( (name.indexOf("deadlift") > -1) ) {
			return Move.getMove(32); // Deadlift
		} else if ( (name.indexOf("bench") > -1) && (name.indexOf("press") > -1) ) {
			return Move.getMove(1); // Bench
		} else if (name.equals("curl")) {
			return Move.getMove(122); // Bicep Curl
		}
		
		Criteria crit = new Criteria();
		crit.add(ExcerciseDbPeer.NAME, _name);
		List objList = ExcerciseDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			//throw new ObjectNotFoundException("Could not locate move with name " + _name + ".");
			
			System.out.println("creating new move >" + _name);
			
			Move move = new Move();
			move.setName(_name);
			move.setType(_type);
			move.setIsCompoundLift(_is_compound);
			move.setActive(true);
			move.setPublic(false);
			//move.setCreateOrModifyPerson(_create_person); // if the edit is coming from an external google sheet, I don't think I have a way to determine who the editor is...
			move.save();
			
			return move;
			
		}
		return Move.getMove((ExcerciseDb)objList.get(0));
    }

    public static Move
    getMove(String _name, String _desc) throws TorqueException, ObjectNotFoundException {
		Criteria crit = new Criteria();
		crit.add(ExcerciseDbPeer.NAME, _name);
		crit.add(ExcerciseDbPeer.DESCRIPTION, _desc);
		List objList = ExcerciseDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			throw new ObjectNotFoundException("Could not locate move with name " + _name + " and the provided description.");
		}
		return Move.getMove((ExcerciseDb)objList.get(0));
    }

    public static Iterator<Move>
    getMoves() throws TorqueException, ObjectNotFoundException {
		ArrayList<Move> vec = new ArrayList();
		Criteria crit = new Criteria();
		crit.add(ExcerciseDbPeer.IS_ACTIVE, (short)1);
		crit.addDescendingOrderByColumn(ExcerciseDbPeer.IS_COMPOUND);
		crit.addAscendingOrderByColumn(ExcerciseDbPeer.NAME);
		Iterator itr = ExcerciseDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.add(Move.getMove((ExcerciseDb)itr.next()));
		}
		return vec.iterator();
    }
	
	public static Iterator<Move>
	getCompoundMoves() throws TorqueException {
		ArrayList<Move> vec = new ArrayList();
		Criteria crit = new Criteria();
		crit.add(ExcerciseDbPeer.IS_ACTIVE, (short)1);
		crit.addAscendingOrderByColumn(ExcerciseDbPeer.NAME);
		Iterator itr = ExcerciseDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			Move move = Move.getMove((ExcerciseDb)itr.next());
			if (move.isCompoundLift() && move.isActive()) {
				vec.add(move);
			}
		}
		return vec.iterator();
	}
	
	public static Iterator<Move>
	getCompoundMoves(PersonBean _person) throws TorqueException {
		ArrayList<Move> vec = new ArrayList();
		Criteria crit = new Criteria();
		crit.add(ExcerciseDbPeer.IS_ACTIVE, (short)1);
		crit.add(ExcerciseDbPeer.IS_COMPOUND, (short)1);
		
		// public or personal
		
		Criteria.Criterion crit_a = crit.getNewCriterion(ExcerciseDbPeer.IS_PUBLIC, (short)1, Criteria.EQUAL);
		Criteria.Criterion crit_b = crit.getNewCriterion(ExcerciseDbPeer.CREATE_PERSON_ID, _person.getId(), Criteria.EQUAL);
		crit.add(crit_a.or(crit_b));
		
		//crit.addAscendingOrderByColumn(ExcerciseDbPeer.NAME);
		System.out.println("getCompoundMoves crit >" + crit.toString());
		Iterator itr = ExcerciseDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.add(Move.getMove((ExcerciseDb)itr.next()));
		}
		return vec.iterator();
	}
	
	

    // SQL

    /*

<table name="EXCERCISE_DB" idMethod="native">
	<column name="EXCERCISE_DB_ID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true"/>
	<column name="TRAINING_PLAN_DB_ID" required="true" type="INTEGER"/>
	
	<column name="NAME" required="true" size="100" type="VARCHAR"/>
	<column name="DESCRIPTION" required="false" type="LONGVARCHAR"/>
	<column name="IMAGE_URL" required="false" size="255" type="VARCHAR" />
	<column name="VIDEO_URL" required="false" size="255" type="VARCHAR" />
	
	<column name="IS_COMPOUND" required="true" type="SMALLINT" default="0"/>
	
	<column name="EXCERCISE_TYPE" type="SMALLINT"/>
	<!-- 1 = lifting -->
	<!-- 2 = cardio -->
	
	<column name="IS_ACTIVE" type="SMALLINT" default="1"/>
	<column name="IS_PUBLIC" type="SMALLINT" default="1"/>
	
	<column name="CREATION_DATE" required="false" type="DATE"/>
	<column name="MODIFICATION_DATE" required="false" type="DATE"/>
	<column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
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

    private ExcerciseDb move;
	
	// INNER CLASSES
	
	public class StrengthEntry {
		Date entryDate;
		//String entryAmount;
		BigDecimal entryAmount;
		
		StrengthEntry(Date _d, BigDecimal _bd) {
			entryDate = _d;
			entryAmount = _bd;
		}
		
		public Date getEntryDate() {
			return entryDate;
		}
		
		/*
		public String getEntryAmountString() {
			return entryAmount;
		}
		*/
		
		public BigDecimal getEntryAmount() {
			return entryAmount;
		}
		
    }

    // CONSTRUCTORS

    public
    Move() {
		move = new ExcerciseDb();
		isNew = true;
    }

    public
    Move(ExcerciseDb _obj) {
		move = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public boolean
	equals(Move _m) {
		if (_m == null) {
			return false;
		}
		return ( this.getId() == _m.getId() );
	}

    public int
    getId() {
		return move.getExcerciseDbId();
    }

    public String
    getLabel() {
		String str = move.getName();
		if (str == null) {
			return "";
		}
		return str;
    }
	
	public String getName() {
		String str = move.getName();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setName(String _s) {
		move.setName(_s);
	}
	
	public String getImageURL() {
		String str = move.getImageUrl();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setImageURL(String _s) {
		move.setImageUrl(_s);
	}
	
	public String getVideoURL() {
		String str = move.getVideoUrl();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setVideoURL(String _s) {
		move.setVideoUrl(_s);
	}
	
	public String getDescription() {
		String str = move.getDescription();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setDescription(String description) {
		move.setDescription(description);
	}

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		move.setCreationDate(new Date());
		move.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		if (!this.canEdit(this.createOrModifyPerson)) {
			throw new IllegalValueException("Unable to save move.  You don't have permission to update " + this.getLabel());
		}
		
		move.setModificationDate(new Date());
		move.save();
    }
	
	public boolean isCompoundLift() {
		return (move.getIsCompound() == (short)1);
	}

	public void setIsCompoundLift(boolean _b) {
		move.setIsCompound(_b ? (short)1 : (short)0);
	}
	
	public boolean
	isLift() {
		return ( move.getExcerciseType() == Move.LIFT_TYPE );
	}
	
	public boolean
	isCardio() {
		return ( move.getExcerciseType() == Move.CARDIO_TYPE );
	}
	
	public short
	getType() {
		return move.getExcerciseType();
	}
	
	public String
	getTypeString() {
		switch (move.getExcerciseType()) {
			case Move.LIFT_TYPE: return "Lift";
			case Move.CARDIO_TYPE: return "Cardio";
		}
		return "[TYPE NOT FOUND]";
	}
	
	public void
	setType(short _type) {
		move.setExcerciseType(_type);
	}
	
	public boolean
	canEdit(PersonBean _person) {
		
		if (_person == null) {
			return false;
		}
		
		if (_person.isAdministrator()) {
			return true;
		}
		
		return ( _person.getId() == move.getCreatePersonId() );
		
	}
	
	public boolean
	canView(PersonBean _person) {
		
		if (_person == null) {
			return false;
		}
		
		if (this.isActive() && this.isPublic()) {
			return true;
		}
		
		if (_person.isAdministrator()) {
			return true;
		}
		
		return ( _person.getId() == move.getCreatePersonId() );
	}
	
	public HashMap<PersonBean,String> weight_pr_hash = null;
	
	public void
	setPRWeight(PersonBean _person, BigDecimal _pr) throws TorqueException, Exception {
		
		if (weight_pr_hash == null) {
			weight_pr_hash = new HashMap<PersonBean,String>();
		}
		weight_pr_hash.put(_person, this.getStringFromBigDecimal(_pr));
		
		Criteria crit = new Criteria();
		crit.add(ExcercisePersonMappingPeer.EXCERCISE_DB_ID, this.getId());
		crit.add(ExcercisePersonMappingPeer.PERSON_ID, _person.getId());
		List l = ExcercisePersonMappingPeer.doSelect(crit);
		if (l.isEmpty()) {
			ExcercisePersonMapping mapping = new ExcercisePersonMapping();
			mapping.setPersonId(_person.getId());
			mapping.setExcerciseDbId(this.getId());
			mapping.setPrWeight(_pr);
			mapping.save();
		} else {
			ExcercisePersonMapping mapping = (ExcercisePersonMapping)l.get(0);
			BigDecimal existing_pr = mapping.getPrWeight();
			if (_pr != null) {
				if ( (existing_pr == null) || (existing_pr.compareTo(_pr) != 0) ) {
					mapping.setPrWeight(_pr);
					mapping.save();
				}
			}
		}
	}
	
	public String
	getPRString(PersonBean _person) throws TorqueException {
		
		if (weight_pr_hash == null) {
			weight_pr_hash = new HashMap<PersonBean,String>();
		}
		if (weight_pr_hash.containsKey(_person)) {
			return weight_pr_hash.get(_person);
		} else {
			Criteria crit = new Criteria();
			crit.add(ExcercisePersonMappingPeer.EXCERCISE_DB_ID, this.getId());
			crit.add(ExcercisePersonMappingPeer.PERSON_ID, _person.getId());
			List l = ExcercisePersonMappingPeer.doSelect(crit);
			if (!l.isEmpty()) {
				ExcercisePersonMapping mapping = (ExcercisePersonMapping)l.get(0);
				String pr_weight_str = this.getStringFromBigDecimal(mapping.getPrWeight());
				weight_pr_hash.put(_person, pr_weight_str);
				return pr_weight_str;
			}
		}
		
		return "";
	}
	
	public HashMap<PersonBean,Integer> timer_hash = null;
	
	public void
	setMoveTimer(PersonBean _person, int _seconds) throws TorqueException, Exception {
		
		if (timer_hash == null) {
			timer_hash = new HashMap<PersonBean,Integer>();
		}
		timer_hash.put(_person, _seconds);
		
		Criteria crit = new Criteria();
		crit.add(ExcercisePersonMappingPeer.EXCERCISE_DB_ID, this.getId());
		crit.add(ExcercisePersonMappingPeer.PERSON_ID, _person.getId());
		List l = ExcercisePersonMappingPeer.doSelect(crit);
		if (l.isEmpty()) {
			ExcercisePersonMapping mapping = new ExcercisePersonMapping();
			mapping.setPersonId(_person.getId());
			mapping.setExcerciseDbId(this.getId());
			mapping.setMoveTimerSeconds(_seconds);
			mapping.save();
		} else {
			ExcercisePersonMapping mapping = (ExcercisePersonMapping)l.get(0);
			Integer existing_seconds = mapping.getMoveTimerSeconds();
			if (_seconds > 0) {
				if ( (existing_seconds == 0) || (existing_seconds != _seconds) ) {
					mapping.setMoveTimerSeconds(_seconds);
					mapping.save();
				}
			}
		}
	}
	
	public int
	getMoveTimer(PersonBean _person) throws TorqueException {
		
		if (timer_hash == null) {
			timer_hash = new HashMap<PersonBean,Integer>();
		}
		if (timer_hash.containsKey(_person)) {
			return timer_hash.get(_person);
		} else {
			Criteria crit = new Criteria();
			crit.add(ExcercisePersonMappingPeer.EXCERCISE_DB_ID, this.getId());
			crit.add(ExcercisePersonMappingPeer.PERSON_ID, _person.getId());
			List l = ExcercisePersonMappingPeer.doSelect(crit);
			if (!l.isEmpty()) {
				ExcercisePersonMapping mapping = (ExcercisePersonMapping)l.get(0);
				int move_timer_seconds = mapping.getMoveTimerSeconds();
				timer_hash.put(_person, move_timer_seconds);
				return move_timer_seconds;
			}
		}
		
		return 0;
	}
	
	public boolean
	isActive() {
		return (move.getIsActive() == (short)1);
	}
	
	public void
	setActive(boolean _b) {
		move.setIsActive(_b ? (short)1 : (short)0);
	}
	
	public boolean
	isPublic() {
		return (move.getIsPublic() == (short)1);
	}
	
	public void
	setPublic(boolean _b) {
		move.setIsPublic(_b ? (short)1 : (short)0);
	}
	
	private PersonBean createOrModifyPerson = null;
    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		createOrModifyPerson = _person;
		if (this.isNew())
			move.setCreatePersonId(_person.getId());
		else
			move.setModifyPersonId(_person.getId());
    }
	
	public void
	invalidate() {
		calculated_1rm_hash = new HashMap<PersonBean,BigDecimal>(11);
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
	
	
	//private BigDecimal calculated_1rm = null;
	private HashMap<PersonBean,BigDecimal> calculated_1rm_hash = new HashMap<PersonBean,BigDecimal>(11);
	private HashMap<PersonBean,ArrayList> strength_entry_hash = new HashMap<PersonBean,ArrayList>(11);
	private HashMap<PersonBean,BigDecimal> rm_hash = new HashMap<PersonBean,BigDecimal>(11);
	
	public Iterator<StrengthEntry>
	getStrengthEntries(PersonBean _person, int _field, int _count) throws TorqueException {
		this.getCalculated1RM(_person);
		
		Calendar time_frame = Calendar.getInstance();
		time_frame.add(_field, _count);
		
		ArrayList<StrengthEntry> return_vec = new ArrayList();
		Iterator<StrengthEntry> itr = strength_entry_hash.get(_person).iterator();
		while (itr.hasNext()) {
			StrengthEntry entry = itr.next();
			System.out.println("entry.entryDate >" + entry.entryDate);
			if (entry.entryDate.after(time_frame.getTime())) {
				return_vec.add(entry);
			}
		}
		
		return return_vec.iterator();
	}
	
	/*
	public Iterator<StrengthEntry>
	getStrengthIncreaseEntries(PersonBean _person, int _field, int _count) throws TorqueException {
		this.getCalculated1RM(_person);
		
		Calendar time_frame = Calendar.getInstance();
		time_frame.add(_field, _count);
		
		String previous_entry = "";
		
		ArrayList<StrengthEntry> return_vec = new ArrayList();
		Iterator<StrengthEntry> itr = strength_entry_hash.get(_person).iterator();
		while (itr.hasNext()) {
			StrengthEntry entry = itr.next();
			if (entry.entryDate.after(time_frame.getTime())) {
				if (previous_entry.isEmpty()) {
					previous_entry = 
				} else {
					
				}
				return_vec.add(entry);
			}
		}
		
		return return_vec.iterator();
	}
	*/
	
	public BigDecimal
	getCalculated1RM(PersonBean _person) throws TorqueException {
		
		BigDecimal calculated_1rm = calculated_1rm_hash.get(_person);
		
		if (calculated_1rm == null) {
			
			ArrayList<StrengthEntry> strength_entries = new ArrayList<StrengthEntry>();
			Date last_strength_date = null;
			BigDecimal last_strength_value = null;
		
			calculated_1rm = BigDecimal.ZERO;
			BigDecimal one_rep_max = BigDecimal.ZERO;
			
			// loop through all the sets this person has done for this move and find the highest 1RM calculation

			Criteria crit = new Criteria();
			crit.addJoin(TrainingPlanExcerciseDbPeer.TRAINING_PLAN_EXCERCISE_DB_ID, TrainingPlanSetDbPeer.TRAINING_PLAN_EXCERCISE_DB_ID);
			crit.addJoin(TrainingPlanSetDbPeer.TRAINING_PLAN_SET_DB_ID, SetDbPeer.TRAINING_PLAN_SET_DB_ID);
			crit.addJoin(SetDbPeer.WORKOUT_DB_ID, WorkoutDbPeer.WORKOUT_DB_ID);
			
			crit.add(TrainingPlanExcerciseDbPeer.EXCERCISE_DB_ID, this.getId());
			
			//crit.add(ExcercisePersonMappingPeer.EXCERCISE_DB_ID, this.getId());
			crit.add(WorkoutDbPeer.PERSON_ID, _person.getId());
			crit.addAscendingOrderByColumn(SetDbPeer.SET_DATE);
			
			System.out.println("getCalculated1RMString crit >" + crit.toString());
			
			Iterator itr = SetDbPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				SetDb set_obj = (SetDb)itr.next();
				short actual_reps = set_obj.getActualReps();
				if (set_obj.getActualRpe() != null) {
					short rir = (short)(10 - set_obj.getActualRpe().shortValue());
					actual_reps += rir;
				}
				//if ( (actual_reps > 0) && (actual_reps < 13) ) {
				if ( (actual_reps > 0) ) {
					BigDecimal actual_weight = set_obj.getActualWeight();
					if ( (actual_weight != null) && (actual_weight.compareTo(BigDecimal.ZERO) == 1) ) {
						try {
							BigDecimal calculated_one_rep_max_for_set = Move.getCalculated1RM(actual_reps, actual_weight);
							if (calculated_one_rep_max_for_set.compareTo(calculated_1rm) == 1) {
								calculated_1rm = calculated_one_rep_max_for_set;
								System.out.println("last_strength_date >" + last_strength_date);
								
								if (last_strength_date != null) {
									if (CUBean.getUserDateString(last_strength_date).equals(CUBean.getUserDateString(set_obj.getSetDate()))) {
									//if (last_strength_date.compareTo(set_obj.getSetDate()) == 0) {
										// same date - remove previous because it's a lower weight on the same day
										//strength_entries.remove(strength_entries.size() - 1);
										strength_entries = new ArrayList<StrengthEntry>();
									}
								}
								
								BigDecimal strength_increase = BigDecimal.ZERO;
								if (last_strength_value != null) {
									strength_increase = calculated_one_rep_max_for_set.subtract(last_strength_value);
									//strength_entries.add(new StrengthEntry(set_obj.getSetDate(), this.getStringFromBigDecimal(strength_increase.divide(last_strength_value, 2, RoundingMode.HALF_UP).multiply(one_hundred))));
									//System.out.println("set_obj.getSetDate() >" + set_obj.getSetDate());
									if (set_obj.getSetDate() != null) {
										strength_entries.add(new StrengthEntry(set_obj.getSetDate(), strength_increase.divide(last_strength_value, 2, RoundingMode.HALF_UP).multiply(BigDecimal.TEN)));
									}
								}
								//strength_entries.add(new StrengthEntry(set_obj.getSetDate(), this.getStringFromBigDecimal(calculated_one_rep_max_for_set)));
								
								last_strength_date = set_obj.getSetDate();
								last_strength_value = calculated_one_rep_max_for_set;
							}
						} catch (IllegalValueException x) {
							x.printStackTrace();
						}
					}

				}
				
				if (set_obj.getActualReps() == 1) {
				//if (actual_reps) {
					if (set_obj.getActualWeight() != null) {
						if (set_obj.getActualWeight().compareTo(one_rep_max) == 1) {
							one_rep_max = set_obj.getActualWeight();
						}
					}
				}
			}
			
			calculated_1rm_hash.put(_person, calculated_1rm);
			strength_entry_hash.put(_person, strength_entries);
			rm_hash.put(_person, one_rep_max);
		}
		
		return calculated_1rm;
	}
	
	public String
	getCalculated1RMString(PersonBean _person) throws TorqueException {
		return this.getStringFromBigDecimal(this.getCalculated1RM(_person));
	}
	
	public String
	get1RMString(PersonBean _person) throws TorqueException {
		this.getCalculated1RM(_person);
		BigDecimal one_rep_max = rm_hash.get(_person);
		return this.getStringFromBigDecimal(one_rep_max);
	}
	
	public static HashMap<Short, BigDecimal> one_rep_max_perc_hash = null;
	public static BigDecimal
	getCalculated1RM(short _reps, BigDecimal _weight) throws IllegalValueException {
		
		//System.out.println("getCalculated1RM >" + _reps + ", >" + _weight);
		
		if (_reps == 1) {
			return _weight;
		}
		
		if (Move.one_rep_max_perc_hash == null) {
			Move.one_rep_max_perc_hash = new HashMap();
			Move.one_rep_max_perc_hash.put((short)1, new BigDecimal(1));
			Move.one_rep_max_perc_hash.put((short)2, new BigDecimal(.95));
			Move.one_rep_max_perc_hash.put((short)3, new BigDecimal(.93));
			Move.one_rep_max_perc_hash.put((short)4, new BigDecimal(.90));
			Move.one_rep_max_perc_hash.put((short)5, new BigDecimal(.87));
			Move.one_rep_max_perc_hash.put((short)6, new BigDecimal(.85));
			Move.one_rep_max_perc_hash.put((short)7, new BigDecimal(.83));
			Move.one_rep_max_perc_hash.put((short)8, new BigDecimal(.80));
			Move.one_rep_max_perc_hash.put((short)9, new BigDecimal(.77));
			Move.one_rep_max_perc_hash.put((short)10, new BigDecimal(.75));
			Move.one_rep_max_perc_hash.put((short)11, new BigDecimal(.73));
			Move.one_rep_max_perc_hash.put((short)12, new BigDecimal(.70));
		}
		
		BigDecimal perc = null;
		if (_reps > 12) {
			perc = Move.one_rep_max_perc_hash.get(12);
		} else {
			perc = Move.one_rep_max_perc_hash.get(_reps);
		}
		
		if (perc == null) {
			throw new IllegalValueException("Unable to calculate 1 rep max for " + _reps + " reps.");
		}
		//BigDecimal one_hundred = new BigDecimal(100);
		//BigDecimal reps = new BigDecimal(_reps);
		
		//return one_hundred.multiply(reps).divide(perc, 0, RoundingMode.HALF_UP);
		
		return _weight.divide(perc, 0, RoundingMode.HALF_UP);
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
	
}