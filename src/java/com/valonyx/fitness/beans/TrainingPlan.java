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
import com.badiyan.uk.online.tasks.MaintainBlankGoogleSheetsWorkoutTemplateTask;
import com.valonyx.fitness.beans.TrainingPlanMove.SetDetails;
import com.valonyx.fitness.servlets.FitnessServlet;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;



/**
 *
 * @author marlo
 */
public class
TrainingPlan
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
	public static final short GRAMS_TYPE = 1;
	public static final short PERCENTAGE_TYPE = 2;

    protected static HashMap<Integer,TrainingPlan> hash = new HashMap<Integer,TrainingPlan>();

    // CLASS METHODS
	
    public static void
    delete(int _id) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(TrainingPlanDbPeer.TRAINING_PLAN_DB_ID, _id);
		TrainingPlanDbPeer.doDelete(crit);
		hash.remove(_id);
    }

    public static TrainingPlan
    getTrainingPlan(int _id)
		throws TorqueException, ObjectNotFoundException {
		
		TrainingPlan plan_obj = (TrainingPlan)hash.get(_id);
		if (plan_obj == null) {
			Criteria crit = new Criteria();
			crit.add(TrainingPlanDbPeer.TRAINING_PLAN_DB_ID, _id);
			List objList = TrainingPlanDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate training plan with id: " + _id);
			}

			plan_obj = TrainingPlan.getTrainingPlan((TrainingPlanDb)objList.get(0));
		}

		return plan_obj;
    }

    private static TrainingPlan
    getTrainingPlan(TrainingPlanDb _obj) throws TorqueException {
		
		TrainingPlan plan_obj = (TrainingPlan)hash.get(_obj.getTrainingPlanDbId());
		if (plan_obj == null) {
			plan_obj = new TrainingPlan(_obj);
			hash.put(_obj.getTrainingPlanDbId(), plan_obj);
		}

		return plan_obj;
    }

    public static TrainingPlan
    getCurrentTrainingPlan(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		Criteria crit = new Criteria();
		crit.addJoin(PersonTrainingPlanMappingPeer.TRAINING_PLAN_DB_ID, TrainingPlanDbPeer.TRAINING_PLAN_DB_ID);
		crit.add(PersonTrainingPlanMappingPeer.PERSON_ID, _person.getId());
		//crit.add(TrainingPlanDbPeer.PERSON_ID, _person.getId());
		crit.add(TrainingPlanDbPeer.IS_ACTIVE, (short)1); // removed 5/6/18 // re-added 5/28/18
		crit.add(PersonTrainingPlanMappingPeer.IS_ACTIVE, (short)1); // added 5/6/18
		System.out.println("getCurrentTrainingPlan crit >" + crit.toString());
		List objList = TrainingPlanDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			TrainingPlan new_plan = new TrainingPlan();
			new_plan.setPerson(_person);
			//new_plan.setName(CUBean.getUserDateString(new Date()) + " Training Plan");
			new_plan.setName("My Training Plan");
			new_plan.setDescription("My plan created " + CUBean.getUserDateString(new Date()));
			//new_plan.activate(); // do this elsewhere - I don't think I want to summarily flag a new plan as the active one anyway
			new_plan.setCreateOrModifyPerson(_person);
			new_plan.setPublic(false);
			new_plan.setActive(true);
			new_plan.save();
			
			new_plan.getMapping(_person, true);
			
			hash.put(new_plan.getId(), new_plan);
			return new_plan;
		} else if (objList.size() > 1) {
			//throw new UniqueObjectNotFoundException("Could not locate unique current training plan for " + _person.getLabel());
			
			// inactivate one of them sumarily
			
			/*
			for (int i = 1; i < objList.size(); i++) {
				TrainingPlan plan_to_inactivate = TrainingPlan.getTrainingPlan((TrainingPlanDb)objList.get(i));
				plan_to_inactivate.setActive(false);
				plan_to_inactivate.save();
			}
			*/
		}

		return TrainingPlan.getTrainingPlan((TrainingPlanDb)objList.get(0));
    }

    public static Vector
    getTrainingPlans(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(PersonTrainingPlanMappingPeer.TRAINING_PLAN_DB_ID, TrainingPlanDbPeer.TRAINING_PLAN_DB_ID);
		crit.add(PersonTrainingPlanMappingPeer.PERSON_ID, _person.getId());
		crit.add(TrainingPlanDbPeer.IS_ACTIVE, (short)1);
		//crit.addDescendingOrderByColumn(TrainingPlanDbPeer.IS_ACTIVE);
		crit.addAscendingOrderByColumn(TrainingPlanDbPeer.NAME);
		System.out.println("crit >" + crit.toString());
		Iterator itr = TrainingPlanDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(TrainingPlan.getTrainingPlan((TrainingPlanDb)itr.next()));
		}
		
		// also add public plans
		
		crit = new Criteria();
		crit.add(TrainingPlanDbPeer.IS_PUBLIC, (short)1);
		crit.add(TrainingPlanDbPeer.IS_ACTIVE, (short)1);
		crit.addAscendingOrderByColumn(TrainingPlanDbPeer.NAME);
		//System.out.println("crit >" + crit.toString());
		itr = TrainingPlanDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			TrainingPlan obj = TrainingPlan.getTrainingPlan((TrainingPlanDb)itr.next());
			if (!vec.contains(obj)) {
				vec.addElement(obj);
			}
			
		}
		
		return vec;
    }

    public static Vector
    getTrainingPlans(UKOnlinePersonBean _person, UKOnlinePersonBean _another_person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(PersonTrainingPlanMappingPeer.TRAINING_PLAN_DB_ID, TrainingPlanDbPeer.TRAINING_PLAN_DB_ID);
		crit.add(PersonTrainingPlanMappingPeer.PERSON_ID, _person.getId());
		crit.or(PersonTrainingPlanMappingPeer.PERSON_ID, _another_person.getId());
		crit.add(TrainingPlanDbPeer.IS_ACTIVE, (short)1);
		crit.addDescendingOrderByColumn(TrainingPlanDbPeer.IS_ACTIVE);
		crit.addAscendingOrderByColumn(TrainingPlanDbPeer.NAME);
		crit.setDistinct();
		//System.out.println("crit >" + crit.toString());
		Iterator itr = TrainingPlanDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			TrainingPlan training_plan = TrainingPlan.getTrainingPlan((TrainingPlanDb)itr.next());
			vec.addElement(training_plan);
		}
		
		return vec;
    }
	
	public static PersonTrainingPlanMapping
	getPersonTrainingPlanMappingForGoogleSheetId(String _google_sheet_id) throws TorqueException, ObjectNotFoundException {
		Criteria crit = new Criteria();
		crit.add(PersonTrainingPlanMappingPeer.GOOGLE_SHEET_ID, _google_sheet_id);
		List l = PersonTrainingPlanMappingPeer.doSelect(crit);
		if (l.isEmpty()) {
			throw new ObjectNotFoundException("Unable to locate Training Plan for Google Sheet id >" + _google_sheet_id);
		} else {
			//return TrainingPlan.getTrainingPlan((TrainingPlanDb)l.get(0));
			return (PersonTrainingPlanMapping)l.get(0);
		}
	}
	

    // SQL

    /*

<table name="PERSON_TRAINING_PLAN_MAPPING">
	<column name="TRAINING_PLAN_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="PERSON_ID" primaryKey="true" required="true" type="INTEGER"/>
	
	<column name="CURRENT_TRAINING_PLAN_WORKOUT_DB_ID" required="false" type="BIGINT"/>

	<foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
	</foreign-key>
	<foreign-key foreignTable="TRAINING_PLAN_DB">
		<reference local="TRAINING_PLAN_DB_ID" foreign="TRAINING_PLAN_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="TRAINING_PLAN_WORKOUT_DB">
		<reference local="CURRENT_TRAINING_PLAN_WORKOUT_DB_ID" foreign="TRAINING_PLAN_WORKOUT_DB_ID"/>
    </foreign-key>
</table>
	
<table name="TRAINING_PLAN_DB" idMethod="native">
	<column name="TRAINING_PLAN_DB_ID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true"/>
	<column name="PERSON_ID" required="false" type="INTEGER"/>
    <column name="IS_ACTIVE" type="SMALLINT" default="0"/>
	<column name="IS_PUBLIC" type="SMALLINT" default="0"/>
	
    <column name="AUTO_REPEAT" type="SMALLINT" default="0"/>
    <column name="NUM_WEEKS" type="SMALLINT" />
	
	<column name="PLAN_TYPE" type="SMALLINT"/>
	<!-- 1 = heavy?? -->
	<!-- 2 = hypertrophy?? -->
	
	<column name="NAME" required="true" size="100" type="VARCHAR"/>
	
	<column name="DESCRIPTION" type="LONGVARCHAR"/>
	<column name="NOTES" type="LONGVARCHAR"/>
	
	<!-- move to person mapping
	<column name="CURRENT_TRAINING_PLAN_WORKOUT_DB_ID" required="false" type="BIGINT"/>
	-->
	
	<column name="CREATION_DATE" required="true" type="DATE"/>
	<column name="MODIFICATION_DATE" required="false" type="DATE"/>
	<column name="CREATE_PERSON_ID" required="true" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

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

    private TrainingPlanDb plan;
	private Vector workouts = null;

    // CONSTRUCTORS

    public
    TrainingPlan() {
		plan = new TrainingPlanDb();
		isNew = true;
    }

    public
    TrainingPlan(TrainingPlanDb _obj) {
		plan = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public boolean
	equals(TrainingPlan _w) {
		if (_w == null) {
			return false;
		}
		return ( this.getId() == _w.getId() );
	}

    public int
    getId() {
		return plan.getTrainingPlanDbId();
    }

    public String
    getLabel() {
		/*
		if (this.isActive()) {
			return "Active " + this.getPlanTypeString() + " Plan created " + this.getCreationDateString();
		}
		return "Inactive " + this.getPlanTypeString() + " Plan created " + this.getCreationDateString();
		*/
		
		String str = plan.getName();
		if (str == null) {
			return "";
		}
		return str;
    }
	
	public short
	getPlanType() {
		return plan.getPlanType();
	}
	
	public String
	getPlanTypeString() {
		switch (plan.getPlanType()) {
			case TrainingPlan.GRAMS_TYPE: return "Grams";
			case TrainingPlan.PERCENTAGE_TYPE: return "Percentage";
		}
		return "[TYPE NOT FOUND]";
	}
	
	public void
	setGoalType(short _type) {
		plan.setPlanType(_type);
	}
	
	public UKOnlinePersonBean
	getPerson() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(plan.getPersonId());
	}
	
	public void
	setPerson(PersonBean _person) throws TorqueException {
		plan.setPersonId(_person.getId());
	}

	public Date getCreationDate() {
		return plan.getCreationDate();
	}

	public String getCreationDateString() {
		return CUBean.getUserDateString(plan.getCreationDate());
	}
    
    public long getCreationDateUnixTimestamp() {
		return this.plan.getCreationDate().getTime() / 1000;
    }

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		plan.setCreationDate(new Date());
		plan.save();
				
		// create mapping
		
		PersonTrainingPlanMapping mapping = new PersonTrainingPlanMapping();
		mapping.setPersonId(this.getPerson().getId());
		mapping.setTrainingPlanDbId(this.getId());
		mapping.setIsActive(this.hasCurrentTrainingPlan(this.getPerson()) ? (short)0 : (short)1);
		mapping.save();
		
		this.notifyChange();
		
    }
	
	public boolean
	canEdit(PersonBean _person) throws IllegalValueException {
		
		//System.out.println("_person >" + _person);
		
		if (_person == null) {
			// switched back around on 8/20/18 - this was hard failing when clicking the "Train" tab for a new user
			//throw new IllegalValueException("Create or modify person not set for training plan >" + this.getLabel());
			return false;
		}
		
		if (_person.isAdministrator()) {
			return true;
		}
		
		return false;
		
		/* removed 4/2/19 - only allow admins to modify plans
		return ( _person.getId() == plan.getCreatePersonId() );
		*/
		
	}
	
	public boolean
	isActive() {
		return (plan.getIsActive() == (short)1);
	}
	
	public void
	setActive(boolean _b) {
		plan.setIsActive(_b ? (short)1 : (short)0);
	}
	
	public boolean
	isActive(PersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		try {
			return ( this.getMapping(_person, false).getIsActive() == (short)1 );
		} catch (Exception x) {}
		return false;
	}
	
	public boolean
	isPublic() {
		return (plan.getIsPublic() == (short)1);
	}
	
	public void
	setPublic(boolean _b) {
		plan.setIsPublic(_b ? (short)1 : (short)0);
	}

	public void
	activate(PersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		PersonTrainingPlanMapping mapping = this.getMapping(_person, true);
		mapping.setIsActive((short)1);
		mapping.save();
	}

	public void
	inActivate(PersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		PersonTrainingPlanMapping mapping = this.getMapping(_person, false);
		mapping.setIsActive((short)0);
		mapping.save();
	}

	public void
	setActive(PersonBean _person, boolean _b)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		PersonTrainingPlanMapping mapping = null;
		/* removed 8/20/18
		if (_b) {
			mapping = this.getMapping(_person, true);
		} else {
			mapping = this.getMapping(_person, false);
		}
		*/
		mapping = this.getMapping(_person, true);
		mapping.setIsActive(_b ? (short)1 : (short)0);
		mapping.save();
	}
	
	private UKOnlinePersonBean createOrModifyPerson;
    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		createOrModifyPerson = _person;
		if (this.isNew())
			plan.setCreatePersonId(_person.getId());
		else
			plan.setModifyPersonId(_person.getId());
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		if (!this.canEdit(this.createOrModifyPerson)) {
			throw new IllegalValueException("Unable to save move.  You don't have permission to update " + this.getLabel());
		}
		
		plan.setModificationDate(new Date());
		plan.save();
		
		this.notifyChange();
    }
	
	public String
	getNameString() {
		String str = plan.getName();
		if (str == null) {
			return "";
		}
		return str;
	}
	
	public void
	setName(String _str) {
		plan.setName(_str);
	}
	
	public String
	getDescription() {
		String str = plan.getDescription();
		if (str == null) {
			return "";
		}
		return str;
	}
	
	public void
	setDescription(String _str) {
		plan.setDescription(_str);
	}
	
	public String
	getNotes() {
		String str = plan.getNotes();
		if (str == null) {
			return "";
		}
		return str;
	}
	
	public void
	setNotes(String _str) {
		plan.setNotes(_str);
	}
	
	private short weeks_in_plan = (short)0; // not sure if this makes sense if there're blocks
	private short max_workouts_per_week = (short)0;
	
	public short
	getNumWeeks() throws TorqueException {
		this.getWorkouts();
		return weeks_in_plan;
	}
	
	public short
	getNumWorkoutsPerWeek() throws TorqueException {
		this.getWorkouts();
		return max_workouts_per_week;
	}
	
	public Vector
	getWorkouts() throws TorqueException {
		if (workouts == null) {
			workouts = new Vector();
			Criteria crit = new Criteria();
			crit.add(TrainingPlanWorkoutDbPeer.TRAINING_PLAN_DB_ID, this.getId());
			crit.addAscendingOrderByColumn(TrainingPlanWorkoutDbPeer.BLOCK_NUMBER);
			crit.addAscendingOrderByColumn(TrainingPlanWorkoutDbPeer.WEEK_NUMBER);
			crit.addAscendingOrderByColumn(TrainingPlanWorkoutDbPeer.WORKOUT_NUMBER);
			System.out.println("getWorkouts crit >" + crit.toString());
			Iterator itr = TrainingPlanWorkoutDbPeer.doSelect(crit).iterator();
			
			short last_week_number = (short)0;
			short workouts_in_week_counter = (short)0;
			while (itr.hasNext()) {
				TrainingPlanWorkout trainingPlanWorkout = TrainingPlanWorkout.getTrainingPlanWorkout((TrainingPlanWorkoutDb)itr.next());
				workouts_in_week_counter++;
				if (workouts_in_week_counter > max_workouts_per_week) {
					max_workouts_per_week = workouts_in_week_counter;
				}
				short week_number = trainingPlanWorkout.getWeekNumber();
				if (week_number > weeks_in_plan) {
					weeks_in_plan = week_number;
				}
				if (week_number != last_week_number) {
					workouts_in_week_counter = (short)0;
				}
				workouts.addElement(trainingPlanWorkout);
				last_week_number = week_number;
				
				if (trainingPlanWorkout.getBlockNumber() > 1) {
					this.hasBlocks = true;
				}
				
				if (trainingPlanWorkout.getBlockNumber() > blocks_in_plan) {
					blocks_in_plan = trainingPlanWorkout.getBlockNumber();
				}
			}
		}
		return workouts;
	}
	
	public Vector
	getWorkoutsPadForSpreadsheet() throws TorqueException {
		// need to, potentially, pad with empty workouts for Google sheets update
		
		short workout_number = 1;
		short last_week_number = 1;
		
		HashMap<String,TrainingPlanWorkout> hash = new HashMap<String,TrainingPlanWorkout>();
		Iterator itr = this.getWorkouts().iterator();
		while (itr.hasNext()) {
			TrainingPlanWorkout trainingPlanWorkout = (TrainingPlanWorkout)itr.next();
			
			short current_week_number = trainingPlanWorkout.getWeekNumber();
			if (current_week_number != last_week_number) {
				workout_number = 1;
			}
			
			String key = trainingPlanWorkout.getBlockNumber() + "-" + current_week_number + "-" + workout_number;
			System.out.println("put key >" + key + "   >>> ");
			hash.put(key, trainingPlanWorkout);
			
			last_week_number = current_week_number;
			workout_number++;
		}
		
		Vector<TrainingPlanWorkout> padded_workouts = new Vector<TrainingPlanWorkout>();
		
		short num_blocks = this.getNumBlocks();
		short num_weeks = 4;
		short num_workouts_per_week = 5;
		
		for (short b = 1; b <= num_blocks; b++) {
			for (short w = 1; w <= num_weeks; w++) {
				for (short i = 1; i <= num_workouts_per_week; i++) {
					String key = b + "-" + w + "-" + i;
					
					TrainingPlanWorkout workout = hash.get(key);
					System.out.println("get key >" + key + "   >>> " + workout);
					if (workout == null) {
						workout = new TrainingPlanWorkout();
						workout.setBlockNumber(b);
						workout.setWeekNumber(w);
						workout.setWorkoutNumber(i);
						workout.setName("");
					}
					
					padded_workouts.addElement(workout);
				}
			}
		}
		
		return padded_workouts;
		
	}
	
	private boolean hasBlocks = false;
	public boolean
	hasBlocks() throws TorqueException {
		this.getWorkouts();
		return hasBlocks;
	}
	
	private short blocks_in_plan = (short)0;
	public short
	getNumBlocks() throws TorqueException {
		this.getWorkouts();
		return blocks_in_plan;
	}
	
	public void
	invalidate() {
		workouts = null;
		mapping_hash = null;
	}
	
	public boolean
	hasWorkouts() throws TorqueException {
		this.getWorkouts();
		return !workouts.isEmpty();
	}
	
	private HashMap<PersonBean,PersonTrainingPlanMapping> mapping_hash = null;
	public PersonTrainingPlanMapping
	getMapping(PersonBean _person, boolean _create_mapping) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		
		if (mapping_hash == null) {
			mapping_hash = new HashMap<PersonBean,PersonTrainingPlanMapping>(3);
		}
		
		if (mapping_hash.containsKey(_person)) {
			return mapping_hash.get(_person);
		}
		
		Criteria crit = new Criteria();
		crit.add(PersonTrainingPlanMappingPeer.TRAINING_PLAN_DB_ID, this.getId());
		crit.add(PersonTrainingPlanMappingPeer.PERSON_ID, _person.getId());
		System.out.println("getMapping() crit >" + crit.toString());
		List l = PersonTrainingPlanMappingPeer.doSelect(crit);
		if (l.size() == 1) {
			PersonTrainingPlanMapping mapping = (PersonTrainingPlanMapping)l.get(0);
			mapping_hash.put(_person, mapping);
			return mapping;
		} else if (l.isEmpty()) {
			if (_create_mapping) {
				PersonTrainingPlanMapping mapping = new PersonTrainingPlanMapping();
				mapping.setPersonId(_person.getId());
				mapping.setTrainingPlanDbId(this.getId());
				mapping.setIsActive(this.hasCurrentTrainingPlan(_person) ? (short)0 : (short)1);
				mapping.save();
				mapping_hash.put(_person, mapping);
				return mapping;
			} else {
				throw new ObjectNotFoundException(_person.getLabel() + " is not assigned to training plan " + this.getLabel());
			}
		} else {
			throw new UniqueObjectNotFoundException(_person.getLabel() + " is not uniquely assigned to training plan " + this.getLabel());
		}
	}
	
	public Vector
	getGoogleSheetMappings() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(PersonTrainingPlanMappingPeer.TRAINING_PLAN_DB_ID, this.getId());
		crit.add(PersonTrainingPlanMappingPeer.GOOGLE_SHEET_ID, (Object)"", Criteria.ISNOTNULL);
		//crit.add(PersonTrainingPlanMappingPeer.PERSON_ID, _person.getId());
		System.out.println("getGoogleSheetMappings() crit >" + crit.toString());
		Iterator itr = PersonTrainingPlanMappingPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement((PersonTrainingPlanMapping)itr.next());
		}
		return vec;
	}
	
	private boolean
	hasCurrentTrainingPlan(PersonBean _person) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(PersonTrainingPlanMappingPeer.PERSON_ID, _person.getId());
		crit.add(PersonTrainingPlanMappingPeer.IS_ACTIVE, (short)1);
		System.out.println("hasCurrentTrainingPlan >" + crit.toString());
		List l = PersonTrainingPlanMappingPeer.doSelect(crit);
		return !l.isEmpty();
	}
	
	public void
	setCurrentTrainingPlanWorkout(PersonBean _person, TrainingPlanWorkout _workout) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		PersonTrainingPlanMapping obj = this.getMapping(_person, true);
		obj.setCurrentTrainingPlanWorkoutDbId(_workout.getId());
		obj.save();
	}
	
	public TrainingPlanWorkout
	getCurrentTrainingPlanWorkout(PersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		PersonTrainingPlanMapping mapping = this.getMapping(_person, true);
		long training_plan_workout_id = mapping.getCurrentTrainingPlanWorkoutDbId();
		if (training_plan_workout_id > 0l) {
			TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getTrainingPlanWorkout(training_plan_workout_id);
			if (workout_obj.getParent().equals(this)) {
				return TrainingPlanWorkout.getTrainingPlanWorkout(training_plan_workout_id);
			}
		}
		
		throw new ObjectNotFoundException(_person.getLabel() + " has no current workout in training plan " + this.getLabel());
	}
	
	public boolean
	hasCurrentTrainingPlanWorkout(PersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		PersonTrainingPlanMapping mapping = this.getMapping(_person, false);
		long training_plan_workout_id = mapping.getCurrentTrainingPlanWorkoutDbId();
		if (training_plan_workout_id > 0l) {
			try {
				TrainingPlanWorkout current_workout = TrainingPlanWorkout.getTrainingPlanWorkout(training_plan_workout_id); // is this doing anything??
				if (current_workout.getParent().equals(this)) {
					return true;
				}
			} catch (ObjectNotFoundException x) {
			
			}
		}
		
		return false;
	}
	
	public TrainingPlanWorkout
	nextWorkout(UKOnlinePersonBean _person) throws TorqueException, ObjectAlreadyExistsException, IllegalValueException, UniqueObjectNotFoundException, ObjectNotFoundException, Exception {
		TrainingPlanWorkout current = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(_person, this);
		short current_week_number = current.getWeekNumber();
		Vector vec = TrainingPlanWorkout.getTrainingPlanWorkouts(this);
		if (vec.isEmpty()) {
			throw new ObjectNotFoundException("No workouts found in training plan.");
		}
		TrainingPlanWorkout first_workout_in_week = null;
		Iterator itr = vec.iterator();
		while (itr.hasNext()) {
			TrainingPlanWorkout obj = (TrainingPlanWorkout)itr.next();
			
			if (current_week_number == obj.getWeekNumber()) {
				// same week
				if (first_workout_in_week == null) {
					first_workout_in_week = obj;
				}
			}
			
			if (current.equals(obj)) {
				TrainingPlanWorkout next = null;
				if (itr.hasNext()) {
					next = (TrainingPlanWorkout)itr.next();
				} else {
					next = (TrainingPlanWorkout)vec.elementAt(0);
					if (next.getWeekNumber() < current_week_number) {
						throw new ObjectNotFoundException("No more workouts in training plan.");
					}
				}
				/* 8/21/18 - allow move to the next week.  why not???
				if (next.getWeekNumber() != current_week_number) {
					//throw new IllegalValueException("No more workouts in week " + current_week_number);
					// instead, go back to the first workout in the week
					next = first_workout_in_week;
				}
				*/
				this.setCurrentTrainingPlanWorkout(_person, next);
				
				//this.save(); 8/14/18 - not thinking I need to save the training plan here
				return next;
			}
			
		}
		
		throw new ObjectNotFoundException("No more workouts in training plan.");
	}
	
	public TrainingPlanWorkout
	previousWorkout(UKOnlinePersonBean _person) throws TorqueException, ObjectAlreadyExistsException, IllegalValueException, UniqueObjectNotFoundException, ObjectNotFoundException, Exception {
		TrainingPlanWorkout current = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(_person, this);
		short current_week_number = current.getWeekNumber();
		Vector vec = TrainingPlanWorkout.getTrainingPlanWorkouts(this);
		if (vec.isEmpty()) {
			throw new ObjectNotFoundException("No workouts found in training plan.");
		}
		TrainingPlanWorkout last = null;
		Iterator itr = vec.iterator();
		while (itr.hasNext()) {
			TrainingPlanWorkout obj = (TrainingPlanWorkout)itr.next();
			if (current.equals(obj)) {
				if (last == null) {
					//last = (TrainingPlanWorkout)vec.elementAt(vec.size() - 1); // 2/24/19 - so, this is just grabbing the last element in the Vector ?? - if last is null it should be safe to return
					throw new ObjectNotFoundException("There are no previous workouts.");
				}
				if (last.getWeekNumber() != current_week_number) {
					throw new IllegalValueException("No prior workouts in week " + current_week_number);
				}
				this.setCurrentTrainingPlanWorkout(_person, last);
				//this.save(); 8/14/18 - not thinking I need to save the training plan here
				return last;
			}
			last = obj;
		}
		throw new ObjectNotFoundException("Unable to move to the previous workout");
	}
	
	public TrainingPlanWorkout
	nextWeek(UKOnlinePersonBean _person) throws TorqueException, ObjectAlreadyExistsException, IllegalValueException, UniqueObjectNotFoundException, ObjectNotFoundException, Exception {
		TrainingPlanWorkout current = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(_person, this);
		
		// what is the current week??
		
		short current_week_number = current.getWeekNumber();
		short current_block_number = current.getBlockNumber();
		
		Vector vec = TrainingPlanWorkout.getTrainingPlanWorkouts(this);
		if (vec.isEmpty()) {
			throw new ObjectNotFoundException("No workouts found in training plan.");
		}
		Iterator itr = vec.iterator();
		while (itr.hasNext()) {
			TrainingPlanWorkout obj = (TrainingPlanWorkout)itr.next();
			if (obj.getBlockNumber() == current_block_number) { // only allow week movements within current block
				//if (current.equals(obj)) {
				if (obj.getWeekNumber() > current_week_number) {
					TrainingPlanWorkout next = obj;
					/*
					if (itr.hasNext()) {
						next = (TrainingPlanWorkout)itr.next();
					} else {
						next = (TrainingPlanWorkout)vec.elementAt(0);
					}
					*/
					this.setCurrentTrainingPlanWorkout(_person, next);
					//this.save(); 8/21/18 - not thinking I need to save the training plan here
					return next;
				}
			}
		}
		
		throw new ObjectNotFoundException("Unable to move to the next week.  There are no more weeks in this workout.");
	}
	
	public TrainingPlanWorkout
	previousWeek(UKOnlinePersonBean _person) throws TorqueException, ObjectAlreadyExistsException, IllegalValueException, UniqueObjectNotFoundException, ObjectNotFoundException, Exception {
		
		TrainingPlanWorkout current = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(_person, this);
		
		// what is the current week??
		
		short current_week_number = current.getWeekNumber();
		short current_block_number = current.getBlockNumber();
		
		/*
		if ( ( current_week_number == (short)1 ) &&
				( current.getBlockNumber() == (short)1 ) ) {
			throw new ObjectNotFoundException("There are no previous weeks in training plan.");
		}
		*/
		
		if ( current_week_number == (short)1 ) {
			if (this.hasBlocks()) {
				throw new ObjectNotFoundException("There are no previous weeks in training plan block " + current.getBlockNumber() + ".");
			} else {
				throw new ObjectNotFoundException("There are no previous weeks in training plan.");
			}
		}
		
		Vector vec = TrainingPlanWorkout.getTrainingPlanWorkouts(this);
		if (vec.isEmpty()) {
			throw new ObjectNotFoundException("No workouts found in training plan.");
		}
		//Iterator itr = vec.iterator();
		for ( int i = ( vec.size() - 1 ) ; i >= 0 ; i-- ) {
			TrainingPlanWorkout obj = (TrainingPlanWorkout)vec.elementAt(i);
			if (obj.getBlockNumber() == current_block_number) { // only allow week movements within current block
				//if (current.equals(obj)) {
				if (obj.getWeekNumber() < current_week_number) {
					TrainingPlanWorkout previous = obj;
					/*
					if (itr.hasNext()) {
						next = (TrainingPlanWorkout)itr.next();
					} else {
						next = (TrainingPlanWorkout)vec.elementAt(0);
					}
					*/
					this.setCurrentTrainingPlanWorkout(_person, previous);
					//this.save(); 8/21/18 - not thinking I need to save the training plan here
					return previous;
				}
			}
		}
		throw new ObjectNotFoundException("Unable to move to the previous week");
	}
	
	
	
	
	public TrainingPlanWorkout
	nextBlock(UKOnlinePersonBean _person) throws TorqueException, ObjectAlreadyExistsException, IllegalValueException, UniqueObjectNotFoundException, ObjectNotFoundException, Exception {
		
		TrainingPlanWorkout current = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(_person, this);
		
		Vector vec = TrainingPlanWorkout.getTrainingPlanWorkouts(this);
		if (vec.isEmpty()) {
			throw new ObjectNotFoundException("No workouts found in training plan.");
		}
		
		short current_block_number = current.getBlockNumber();
		Iterator itr = vec.iterator();
		while (itr.hasNext()) {
			TrainingPlanWorkout obj = (TrainingPlanWorkout)itr.next();
			if (obj.getBlockNumber() > current_block_number) {
				TrainingPlanWorkout next = obj;
				this.setCurrentTrainingPlanWorkout(_person, next);
				return next;
			}
		}
		
		throw new ObjectNotFoundException("Unable to move to the next block.  There are no more blocks in this workout.");
	}
	
	public TrainingPlanWorkout
	previousBlock(UKOnlinePersonBean _person) throws TorqueException, ObjectAlreadyExistsException, IllegalValueException, UniqueObjectNotFoundException, ObjectNotFoundException, Exception {
		
		TrainingPlanWorkout current = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(_person, this);
		
		short current_block_number = current.getBlockNumber();
		if ( current_block_number == (short)1 ) {
			throw new ObjectNotFoundException("There are no previous blocks in training plan.");
		}
		
		Vector vec = TrainingPlanWorkout.getTrainingPlanWorkouts(this);
		if (vec.isEmpty()) {
			throw new ObjectNotFoundException("No workouts found in training plan.");
		}
		
		for ( int i = ( vec.size() - 1 ) ; i >= 0 ; i-- ) {
			TrainingPlanWorkout obj = (TrainingPlanWorkout)vec.elementAt(i);
			if (obj.getBlockNumber() < current_block_number) {
				TrainingPlanWorkout previous = obj;
				this.setCurrentTrainingPlanWorkout(_person, previous);
				return previous;
			}
		}
		throw new ObjectNotFoundException("Unable to move to the previous block");
	}
	
	
	
	public boolean
	isRepeating() {
		return ( plan.getAutoRepeat() == (short)1 );
	}
	
	public void
	setIsRepeating(boolean _b) {
		plan.setAutoRepeat( _b ? (short)1 : (short)0 );
	}
	
	public synchronized List<List<Object>>
	toGoogleSheet(PersonBean _person, short _block_number, boolean _pad_stuff) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, Exception {
		
		System.out.println("toGoogleSheet() invoked vvv");
		
		String personLabel = _person.getLabel();
		if (personLabel.isEmpty()) {
			personLabel = _person.getEmail1String() + " - email";
		}
		
		String weightClass = "";
		String division = "";
		String upcomingComp = "";
		String upcomingCompDate = "";
		String squatStyle = "";
		String benchStyle = "";
		String deadliftStyle = "";
		
		String squatMaxStr = "";
		String benchMaxStr = "";
		String deadliftMaxStr = "";
		
		String squatCalcMaxStr = "";
		String benchCalcMaxStr = "";
		String deadliftCalcMaxStr = "";
		
		Move squat = Move.getMove(46);
		Move bench = Move.getMove(1);
		Move deadlift = Move.getMove(32);
		
		if (_person.getId() == 4434) {
			personLabel = "Will Stueve";
			weightClass = "93kg";
			squatStyle = "Low";
			benchStyle = "Moderate";
			deadliftStyle = "Sumo";
			division = "Teen/Junior";
			/*
			squatMaxStr = "375";
			benchMaxStr = "250";
			deadliftMaxStr = "455";
			*/
			
			bench = Move.getMove(134);
			
			//upcomingCompDate = "2/17/19";
			
		} else if (_person.getId() == 4424) {
			personLabel = "Marlo Stueve";
			bench = Move.getMove(134);
			//upcomingCompDate = "2/17/19";
		}
		
		ArrayList<List<Object>> sheetList = new ArrayList<List<Object>>();
		
		String lastWeightEntryLbsStr = "";
		String lastWeightEntryKGStr = "";
		try {
			/*
			WeightEntry lastWeightEntry = WeightEntry.getLastEntry(_person);
			lastWeightEntry.getEntryUnitTypeString();
			*/
			WeightPreferences preferences = WeightPreferences.getWeightPreference(_person);
			lastWeightEntryLbsStr = preferences.getWeightInLbsString();
			lastWeightEntryKGStr = preferences.getWeightInKGString();
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		try {
			
			squatMaxStr = squat.get1RMString(_person);
			squatCalcMaxStr = squat.getCalculated1RMString(_person);
			System.out.println("squatCalcMaxStr >" + squatCalcMaxStr);
			
			benchMaxStr = bench.get1RMString(_person);
			benchCalcMaxStr = bench.getCalculated1RMString(_person);
			
			deadliftMaxStr = deadlift.get1RMString(_person);
			deadliftCalcMaxStr = deadlift.getCalculated1RMString(_person);
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		List<Object> row00 = Arrays.asList();
		List<Object> row01 = Arrays.asList("Athlete:","",personLabel,"","","","","","","Training Max","","Calculated 1RM");
		List<Object> row02 = Arrays.asList("Last Weight:","",lastWeightEntryLbsStr + " lbs","",lastWeightEntryKGStr + " kg","","","Squat:","",squatMaxStr,"",squatCalcMaxStr);
		List<Object> row03 = Arrays.asList("Next Competition:","","","","","","","Bench:","",benchMaxStr,"",benchCalcMaxStr);
		List<Object> row04 = Arrays.asList("Competition Date:","",upcomingCompDate,"","","","","Deadlift:","",deadliftMaxStr,"",deadliftCalcMaxStr);
		List<Object> row05 = Arrays.asList();
		
		sheetList.add(row00);
		sheetList.add(row01);
		sheetList.add(row02);
		sheetList.add(row03);
		sheetList.add(row04);
		sheetList.add(row05);
		
		ArrayList<Object> workout_row_01 = null;
		ArrayList<Object> workout_row_02 = null;
		ArrayList<Object> workout_row_03 = null;
		ArrayList<Object> workout_row_04 = null;
		ArrayList<Object> workout_row_05 = null;
		ArrayList<Object> workout_row_06 = null;
		ArrayList<Object> workout_row_07 = null;
		ArrayList<Object> workout_row_08 = null;
		ArrayList<Object> workout_row_09 = null;
		ArrayList<Object> workout_row_10 = null;
		ArrayList<Object> workout_row_11 = null;
		ArrayList<Object> workout_row_12 = null;
		ArrayList<Object> workout_row_13 = null;
		ArrayList<Object> workout_row_14 = null;
		ArrayList<Object> workout_row_15 = null;
		ArrayList<Object> workout_row_16 = null;
		ArrayList<Object> workout_row_17 = null;
		ArrayList<Object> workout_row_18 = null;
		ArrayList<Object> workout_row_19 = null;
		
		short weekNumber = (short)-1;
		Vector workouts_vec = null;
		if (_pad_stuff) {
			workouts_vec = this.getWorkoutsPadForSpreadsheet();
		} else {
			workouts_vec = this.getWorkouts();
		} 
		//System.out.println("workouts_vec >" + workouts_vec);
		Iterator itr = workouts_vec.iterator();
		while (itr.hasNext()) {
			TrainingPlanWorkout workout_obj = (TrainingPlanWorkout)itr.next();
			Workout workout = null;
			LinkedHashMap<Move,ArrayList<WorkoutSet>> set_hash = null;
			String workout_notes = "";
			boolean has_workout_for_training_plan_workout = false;
			try {
				workout = workout_obj.getWorkoutForTrainingPlanWorkout(_person, false, false, false);
				workout_notes = workout.getNotes();
				set_hash = WorkoutSet.getSets(workout);
				has_workout_for_training_plan_workout = true;
			} catch (Exception x) {
				System.out.println(x.getMessage());
			}
			
			System.out.println("workout_obj.getBlockNumber() >" + workout_obj.getBlockNumber());
			System.out.println("_block_number >" + _block_number);
			
			if (workout_obj.getBlockNumber() == _block_number) {
				
				if (workout_obj.getWeekNumber() != weekNumber) {
					weekNumber = workout_obj.getWeekNumber();
					sheetList.add(Arrays.asList("Block " + _block_number + " \\ Week " + weekNumber));
					
					workout_row_01 = new ArrayList<Object>();
					workout_row_02 = new ArrayList<Object>();
					workout_row_03 = new ArrayList<Object>();
					workout_row_04 = new ArrayList<Object>();
					workout_row_05 = new ArrayList<Object>();
					workout_row_06 = new ArrayList<Object>();
					workout_row_07 = new ArrayList<Object>();
					workout_row_08 = new ArrayList<Object>();
					workout_row_09 = new ArrayList<Object>();
					workout_row_10 = new ArrayList<Object>();
					workout_row_11 = new ArrayList<Object>();
					workout_row_12 = new ArrayList<Object>();
					workout_row_13 = new ArrayList<Object>();
					workout_row_14 = new ArrayList<Object>();
					workout_row_15 = new ArrayList<Object>();
					workout_row_16 = new ArrayList<Object>();
					workout_row_17 = new ArrayList<Object>();
					workout_row_18 = new ArrayList<Object>();
					workout_row_19 = new ArrayList<Object>();
					
					sheetList.add(workout_row_01);
					sheetList.add(workout_row_02);
					sheetList.add(workout_row_03);
					sheetList.add(workout_row_04);
					sheetList.add(workout_row_05);
					sheetList.add(workout_row_06);
					sheetList.add(workout_row_07);
					sheetList.add(workout_row_08);
					sheetList.add(workout_row_09);
					sheetList.add(workout_row_10);
					sheetList.add(workout_row_11);
					sheetList.add(workout_row_12);
					sheetList.add(workout_row_13);
					sheetList.add(workout_row_14);
					sheetList.add(workout_row_15);
					sheetList.add(workout_row_16);
					sheetList.add(workout_row_17);
					sheetList.add(workout_row_18);
					sheetList.add(workout_row_19);
				}
				
				boolean is_first_workout_in_week = workout_row_01.isEmpty();
				
				if (is_first_workout_in_week) {
					workout_row_01.addAll(Arrays.asList(workout_obj.getName(),"","","","","","","",""));
					//workout_row_02.addAll(Arrays.asList("Excercise","","Weight","Actual","Sets","x","Reps","RPE","Actual"));
					workout_row_02.addAll(Arrays.asList());
				} else {
					workout_row_01.addAll(Arrays.asList("",workout_obj.getName(),"","","","","","","",""));
					//workout_row_02.addAll(Arrays.asList("","Excercise","","Weight","Actual","Sets","x","Reps","RPE","Actual"));
					workout_row_02.addAll(Arrays.asList());
				}
				
				Iterator move_itr = workout_obj.getMoves().iterator();
				//Iterator move_itr = workout_obj.getMovesPadForSpreadsheet().iterator();
				
				int set_detail_number = 1;
				String lastMoveStr = "";
				while (move_itr.hasNext()) {
					
					TrainingPlanMove trainingPlanMove = (TrainingPlanMove)move_itr.next();
					String moveStr = trainingPlanMove.getLabel();
					
					if (moveStr.equals(lastMoveStr)) {
						moveStr = "";
					}
					
					Iterator<SetDetails> set_details_itr = trainingPlanMove.getSetDetails(_person, set_hash).iterator();
					while (set_details_itr.hasNext()) {
						SetDetails set_details = set_details_itr.next();
						
						String rpe = set_details.rpe;
						String actualRPE = set_details.actualRPE;
						if (rpe.equals("Medium")) {
							rpe = "Med";
						}
						if (actualRPE.equals("Medium")) {
							actualRPE = "Med";
						}
						
						if (set_details.reps != null) {
							if (set_details.reps.equals("0")) {
								set_details.reps = "";
							}
						}

						List detail_arr = null;
						if (is_first_workout_in_week) {
							detail_arr = Arrays.asList(moveStr,"",set_details.weight,set_details.actualWeight,set_details.sets, set_details.reps.isEmpty() ? "" : "x", set_details.actualReps.isEmpty() ? set_details.reps : set_details.actualReps, rpe, actualRPE);
						} else {
							detail_arr = Arrays.asList("",moveStr,"",set_details.weight,set_details.actualWeight,set_details.sets, set_details.reps.isEmpty() ? "" : "x", set_details.actualReps.isEmpty() ? set_details.reps : set_details.actualReps, rpe, actualRPE);
						}
				
						switch(set_detail_number) {
							case 1: workout_row_03.addAll(detail_arr); break;
							case 2: workout_row_04.addAll(detail_arr); break;
							case 3: workout_row_05.addAll(detail_arr); break;
							case 4: workout_row_06.addAll(detail_arr); break;
							case 5: workout_row_07.addAll(detail_arr); break;
							case 6: workout_row_08.addAll(detail_arr); break;
							case 7: workout_row_09.addAll(detail_arr); break;
							case 8: workout_row_10.addAll(detail_arr); break;
							case 9: workout_row_11.addAll(detail_arr); break;
							case 10: workout_row_12.addAll(detail_arr); break;
							case 11: workout_row_13.addAll(detail_arr); break;
							case 12: workout_row_14.addAll(detail_arr); break;
							case 13: workout_row_15.addAll(detail_arr); break;
						}
						
						set_detail_number++;
					}
					
					lastMoveStr = moveStr;
				}
				
				// need to fill any remaining rows in the workout too
				List fill_arr = null;
				if (is_first_workout_in_week) {
					fill_arr = Arrays.asList("","","","","","","","","");
				} else {
					fill_arr = Arrays.asList("","","","","","","","","","");
				}
				for (int i = set_detail_number; i < 14; i++) { // 13 rows for a workout in the spreadsheet
					switch(i) {
						case 1: workout_row_03.addAll(fill_arr); break;
						case 2: workout_row_04.addAll(fill_arr); break;
						case 3: workout_row_05.addAll(fill_arr); break;
						case 4: workout_row_06.addAll(fill_arr); break;
						case 5: workout_row_07.addAll(fill_arr); break;
						case 6: workout_row_08.addAll(fill_arr); break;
						case 7: workout_row_09.addAll(fill_arr); break;
						case 8: workout_row_10.addAll(fill_arr); break;
						case 9: workout_row_11.addAll(fill_arr); break;
						case 10: workout_row_12.addAll(fill_arr); break;
						case 11: workout_row_13.addAll(fill_arr); break;
						case 12: workout_row_14.addAll(fill_arr); break;
						case 13: workout_row_15.addAll(fill_arr); break;
					}
				}
				
				if (is_first_workout_in_week) {
					//workout_row_16.addAll(Arrays.asList("Workout Notes","","","","","","","",""));
					workout_row_16.addAll(Arrays.asList());
					workout_row_17.addAll(Arrays.asList(workout_notes,"","","","","","","",""));
				} else {
					//workout_row_16.addAll(Arrays.asList("","Workout Notes","","","","","","","",""));
					workout_row_16.addAll(Arrays.asList());
					workout_row_17.addAll(Arrays.asList("",workout_notes,"","","","","","","",""));
				}
				
				workout_row_18.addAll(Arrays.asList());
				workout_row_19.addAll(Arrays.asList());
				
				//TrainingPlanMove trainingPlanMove = workout_obj.getMoves()
			}
		}
		
		
		return sheetList;
	}
	
	
	public synchronized List<List<Object>>
	toGoogleSheetChance(UKOnlinePersonBean _person, short _block_number) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, Exception {
		
		System.out.println("toGoogleSheet() invoked");
		
		String personLabel = _person.getLabel();
		if (personLabel.isEmpty()) {
			personLabel = _person.getEmail1String();
		}
		
		String weightClass = "";
		String division = "";
		String upcomingComp = "";
		String squatStyle = "";
		String benchStyle = "";
		String deadliftStyle = "";
		
		String squatMaxStr = "";
		String benchMaxStr = "";
		String deadliftMaxStr = "";
		
		if (_person.getId() == 4434) {
			personLabel = "Will Stueve";
			weightClass = "93kg";
			squatStyle = "Low";
			benchStyle = "Moderate";
			deadliftStyle = "Sumo";
			division = "Teen/Junior";
			squatMaxStr = "375";
			benchMaxStr = "250";
			deadliftMaxStr = "455";
		}
		
		ArrayList<List<Object>> sheetList = new ArrayList<List<Object>>();
		
		List<Object> row01 = Arrays.asList();
		List<Object> row02 = Arrays.asList("","Athlete","","","","",personLabel,"","","","","","","Style","","Training Max");
		List<Object> row03 = Arrays.asList("","Weight Class","","","","",weightClass,"","","","","","",squatStyle,"","Squat","","","","",squatMaxStr,"","","","",weightClass.isEmpty() ? personLabel : ( personLabel + ", " + weightClass ));
		List<Object> row04 = Arrays.asList("","Division","","","","",division,"","","","","","",benchStyle,"","Bench Press","","","","",benchMaxStr);
		List<Object> row05 = Arrays.asList("","Upcoming Competition","","","","",upcomingComp,"","","","","","",deadliftStyle,"","Deadlift","","","","",deadliftMaxStr);
		List<Object> row06 = Arrays.asList("","Competition Date");
		List<Object> row07 = Arrays.asList();
		List<Object> row08 = Arrays.asList();
		List<Object> row09 = Arrays.asList();
		
		sheetList.add(row01);
		sheetList.add(row02);
		sheetList.add(row03);
		sheetList.add(row04);
		sheetList.add(row05);
		sheetList.add(row06);
		sheetList.add(row07);
		sheetList.add(row08);
		sheetList.add(row09);
		
		ArrayList<Object> workout_row_01 = null;
		ArrayList<Object> workout_row_02 = null;
		ArrayList<Object> workout_row_03 = null;
		ArrayList<Object> workout_row_04 = null;
		ArrayList<Object> workout_row_05 = null;
		ArrayList<Object> workout_row_06 = null;
		ArrayList<Object> workout_row_07 = null;
		ArrayList<Object> workout_row_08 = null;
		ArrayList<Object> workout_row_09 = null;
		ArrayList<Object> workout_row_10 = null;
		ArrayList<Object> workout_row_11 = null;
		ArrayList<Object> workout_row_12 = null;
		ArrayList<Object> workout_row_13 = null;
		ArrayList<Object> workout_row_14 = null;
		ArrayList<Object> workout_row_15 = null;
		ArrayList<Object> workout_row_16 = null;
		ArrayList<Object> workout_row_17 = null;
		
		short weekNumber = (short)-1;
		Vector workouts_vec = this.getWorkouts();
		System.out.println("workouts_vec >" + workouts_vec);
		Iterator itr = this.getWorkouts().iterator();
		while (itr.hasNext()) {
			TrainingPlanWorkout workout_obj = (TrainingPlanWorkout)itr.next();
			
			System.out.println("workout_obj.getBlockNumber() >" + workout_obj.getBlockNumber());
			System.out.println("_block_number >" + _block_number);
			
			if (workout_obj.getBlockNumber() == _block_number) {
				
				if (workout_obj.getWeekNumber() != weekNumber) {
					weekNumber = workout_obj.getWeekNumber();
					sheetList.add(Arrays.asList("","BLOCK " + _block_number + "//W" + weekNumber));
					sheetList.add(Arrays.asList());
					
					workout_row_01 = new ArrayList<Object>();
					workout_row_02 = new ArrayList<Object>();
					workout_row_03 = new ArrayList<Object>();
					workout_row_04 = new ArrayList<Object>();
					workout_row_05 = new ArrayList<Object>();
					workout_row_06 = new ArrayList<Object>();
					workout_row_07 = new ArrayList<Object>();
					workout_row_08 = new ArrayList<Object>();
					workout_row_09 = new ArrayList<Object>();
					workout_row_10 = new ArrayList<Object>();
					workout_row_11 = new ArrayList<Object>();
					workout_row_12 = new ArrayList<Object>();
					workout_row_13 = new ArrayList<Object>();
					workout_row_14 = new ArrayList<Object>();
					workout_row_15 = new ArrayList<Object>();
					workout_row_16 = new ArrayList<Object>();
					workout_row_17 = new ArrayList<Object>();
					
					sheetList.add(workout_row_01);
					sheetList.add(workout_row_02);
					sheetList.add(workout_row_03);
					sheetList.add(workout_row_04);
					sheetList.add(workout_row_05);
					sheetList.add(workout_row_06);
					sheetList.add(workout_row_07);
					sheetList.add(workout_row_08);
					sheetList.add(workout_row_09);
					sheetList.add(workout_row_10);
					sheetList.add(workout_row_11);
					sheetList.add(workout_row_12);
					sheetList.add(workout_row_13);
					sheetList.add(workout_row_14);
					sheetList.add(workout_row_15);
					sheetList.add(workout_row_16);
					sheetList.add(workout_row_17);
				}
				
				workout_row_01.addAll(Arrays.asList("",workout_obj.getName(),"","","","","","","","","","","","","",""));
				workout_row_02.addAll(Arrays.asList("","Exercise","","","","","","Weight","","S","x","R","RPE","","",""));
				
				Iterator move_itr = workout_obj.getMoves().iterator();
				int set_detail_number = 1;
				String lastMoveStr = "";
				while (move_itr.hasNext()) {
					
					TrainingPlanMove trainingPlanMove = (TrainingPlanMove)move_itr.next();
					String moveStr = trainingPlanMove.getLabel();
					if (moveStr.equals(lastMoveStr)) {
						moveStr = "";
					}
					
					/*
					Vector<TrainingPlanSet> sets_vec = trainingPlanMove.getSets();
					trainingPlanMove.getSetsDesc(_person)
					*/
					
					Iterator<SetDetails> set_details_itr = trainingPlanMove.getSetDetails(_person, null).iterator();
					while (set_details_itr.hasNext()) {
						SetDetails set_details = set_details_itr.next();
						
						List detail_arr = Arrays.asList("",moveStr,"","","","","",set_details.weight,"",set_details.sets,"x",set_details.reps,set_details.rpe,"","SQ","670");
						switch(set_detail_number) {
							case 1: workout_row_03.addAll(detail_arr); break;
							case 2: workout_row_04.addAll(detail_arr); break;
							case 3: workout_row_05.addAll(detail_arr); break;
							case 4: workout_row_06.addAll(detail_arr); break;
							case 5: workout_row_07.addAll(detail_arr); break;
							case 6: workout_row_08.addAll(detail_arr); break;
							case 7: workout_row_09.addAll(detail_arr); break;
							case 8: workout_row_10.addAll(detail_arr); break;
							case 9: workout_row_11.addAll(detail_arr); break;
							case 10: workout_row_12.addAll(detail_arr); break;
							case 11: workout_row_13.addAll(detail_arr); break;
						}
						
						set_detail_number++;
					}
					
					lastMoveStr = moveStr;
				}
				
				workout_row_14.addAll(Arrays.asList("","Training Notes Below","","","","","","","","","","","","","",""));
				workout_row_15.addAll(Arrays.asList("","","","","","","","","","","","","","","",""));
				workout_row_16.addAll(Arrays.asList());
				workout_row_17.addAll(Arrays.asList());
				
				
				//TrainingPlanMove trainingPlanMove = workout_obj.getMoves()
			}
		}
		
		/*
		List<Object> row10 = Arrays.asList("","BLOCK " + _block_number + "//W1");
		List<Object> row11 = Arrays.asList();
		List<Object> row12 = Arrays.asList("","Day 1","","","","","","","","","","","","","","","",  "Day 2","","","","","","","","","","","","","","","","Day 3","","","","","","","","","","","","","","","","Day 4","","","","","","","","","","","","","","","","Day 5","","","","","","","","","","","","","","","","Day 6","","","","","","","","","","","","","","","","Day 7");
		List<Object> row13 = Arrays.asList("","Exercise","","","","","","Weight","","S","x","R","RPE","","","","",  "Exercise","","","","","","Weight","","S","x","R","RPE","","","","","Exercise","","","","","","Weight","","S","x","R","RPE","","","","","Exercise","","","","","","Weight","","S","x","R","RPE","","","","","Exercise","","","","","","Weight","","S","x","R","RPE","","","","","Exercise","","","","","","Weight","","S","x","R","RPE","","","","","Exercise","","","","","","Weight","","S","x","R","RPE");
		List<Object> row14 = Arrays.asList("","Squat","","","","","","335","","1","x","2","@sub6","","SQ","670","",  "","","","","","","315","","1","x","4","@sub6","","DL","1,260","","","","","","","","315","","1","x","1","@sub6","","SQ","315","","","","","","","","245","","1","x","1","@7.5","","BP","245","","","","","","","","","","1","x","1","@sub6","","SQ","0","","","","","","","","","","1","x","1","@sub6","","SQ","0","","Low Bar Squat","","","","","","","","1","x","1","@sub6","","SQ","0");
		List<Object> row15 = Arrays.asList("","Deads","","","","","","265","","3","x","8","7","","SQ","6,360","",  "","","","","","","315","","1","x","4","@sub6","","DL","1,260","","","","","","","","275","","6","x","3","@sub6","","SQ","4,950","","","","","","","","-10%","","1","x","3","7","","BP","0","","","","","","","","-8%","","1","x","4","","","SQ","0","","","","","","","","-8%","","1","x","4","","","SQ","0","","","","","","","","-8%","","1","x","4","","","SQ","0");
		List<Object> row16 = Arrays.asList("","Lame Stuff","","","","","","","","","","","","","BP","0",""  ,"","","","","","","315","","1","x","4","@6","","DL","1,260","","","","","","","","225","","1","x","2","@sub6","","BP","450","","","","","","","","175","","4","x","5","5","","BP","3,500","","","","","","","","285","","5","x","3","","","SQ","4,275","","","","","","","","285","","5","x","3","","","SQ","4,275","","","","","","","","285","","5","x","3","","","SQ","4,275");
		List<Object> row17 = Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","285","","1","x","4","6","","DL","1,140","","","","","","","","180","","6","x","4","6","","BP","4,320","","","","","","","","","","3","x","15","High","","","0","","","","","","","","","","1","x","4","@7","","DL","0","","","","","","","","","","1","x","4","@7","","DL","0","","Sumo Deadlift","","","","","","","","1","x","4","@7","","DL","0");
		List<Object> row18 = Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","90","","5","x","6","HIGH","","","2,700","","","","","","","","","","3","x","12","HIGH","","","0","","","","","","","","","","3","x","15","HIGH","","","0","","","","","","","","325","","4","x","4","","","DL","5,200","","","","","","","","325","","4","x","4","","","DL","5,200","","","","","","","","325","","4","x","4","","","DL","5,200");
		List<Object> row19 = Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","BW","","","","","","","","#VALUE!","","","","","","","","BW","","","","","","","","#VALUE!","","","","","","","","","","","","","","","","0","","","","","","","","","","3","x","15","High","","","0","","","","","","","","","","3","x","15","High","","","0","","Leg Extensions","","","","","","","","3","x","15","High","","","0");
		List<Object> row20 = Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","","","3","x","12","MED","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","Chin Ups (20)","","","","","","","","","","","","","","0");
		List<Object> row21 = Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0");
		List<Object> row22 = Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0");
		List<Object> row23 = Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0",""," ","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0");
		List<Object> row24 = Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0");
		List<Object> row25 = Arrays.asList("","Training Notes Below","","","","","","","","","","","","","","","",  "Training Notes Below","","","","","","","","","","","","","","","","Training Notes Below","","","","","","","","","","","","","","","","Training Notes Below","","","","","","","","","","","","","","","","Training Notes Below","","","","","","","","","","","","","","","","Training Notes Below","","","","","","","","","","","","","","","","Training Notes Below");
		List<Object> row26 = Arrays.asList("","","","","","","","","","","","","","","","","",  "","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","@sub6","","","","","","","","","","","","","","","","@sub6","","","","","","","","","","","","","","","","@sub6");
		List<Object> row27 = Arrays.asList();
		List<Object> row28 = Arrays.asList();
		
		List<List<Object>> values = Arrays.asList(
			Arrays.asList(),
			Arrays.asList("","Athlete","","","","",personLabel,"","","","","","","Style","","Training Max"),
			Arrays.asList("","Weight Class","","","","",weightClass,"","","","","","",squatStyle,"","Squat","","","","","375","","","","",weightClass.isEmpty() ? personLabel : ( personLabel + ", " + weightClass )),
			Arrays.asList("","Division","","","","",division,"","","","","","",benchStyle,"","Bench Press","","","","","250"),
			Arrays.asList("","Upcoming Competition","","","","",upcomingComp,"","","","","","",deadliftStyle,"","Deadlift","","","","","455"),
			Arrays.asList("","Competition Date"),
			Arrays.asList(),
			Arrays.asList(),
			Arrays.asList(),
			Arrays.asList("","BLOCK " + _block_number + "//W1"),
			Arrays.asList(),
			Arrays.asList("","Day 1","","","","","","","","","","","","","","","",  "Day 2","","","","","","","","","","","","","","","","Day 3","","","","","","","","","","","","","","","","Day 4","","","","","","","","","","","","","","","","Day 5","","","","","","","","","","","","","","","","Day 6","","","","","","","","","","","","","","","","Day 7"),
			Arrays.asList("","Exercise","","","","","","Weight","","S","x","R","RPE","","","","",  "Exercise","","","","","","Weight","","S","x","R","RPE","","","","","Exercise","","","","","","Weight","","S","x","R","RPE","","","","","Exercise","","","","","","Weight","","S","x","R","RPE","","","","","Exercise","","","","","","Weight","","S","x","R","RPE","","","","","Exercise","","","","","","Weight","","S","x","R","RPE","","","","","Exercise","","","","","","Weight","","S","x","R","RPE"),
			Arrays.asList("","Squat","","","","","","335","","1","x","2","@sub6","","SQ","670","",  "","","","","","","315","","1","x","4","@sub6","","DL","1,260","","","","","","","","315","","1","x","1","@sub6","","SQ","315","","","","","","","","245","","1","x","1","@7.5","","BP","245","","","","","","","","","","1","x","1","@sub6","","SQ","0","","","","","","","","","","1","x","1","@sub6","","SQ","0","","Low Bar Squat","","","","","","","","1","x","1","@sub6","","SQ","0"),
			Arrays.asList("","Deads","","","","","","265","","3","x","8","7","","SQ","6,360","",  "","","","","","","315","","1","x","4","@sub6","","DL","1,260","","","","","","","","275","","6","x","3","@sub6","","SQ","4,950","","","","","","","","-10%","","1","x","3","7","","BP","0","","","","","","","","-8%","","1","x","4","","","SQ","0","","","","","","","","-8%","","1","x","4","","","SQ","0","","","","","","","","-8%","","1","x","4","","","SQ","0"),
			Arrays.asList("","Lame Stuff","","","","","","","","","","","","","BP","0",""  ,"","","","","","","315","","1","x","4","@6","","DL","1,260","","","","","","","","225","","1","x","2","@sub6","","BP","450","","","","","","","","175","","4","x","5","5","","BP","3,500","","","","","","","","285","","5","x","3","","","SQ","4,275","","","","","","","","285","","5","x","3","","","SQ","4,275","","","","","","","","285","","5","x","3","","","SQ","4,275"),
			Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","285","","1","x","4","6","","DL","1,140","","","","","","","","180","","6","x","4","6","","BP","4,320","","","","","","","","","","3","x","15","High","","","0","","","","","","","","","","1","x","4","@7","","DL","0","","","","","","","","","","1","x","4","@7","","DL","0","","Sumo Deadlift","","","","","","","","1","x","4","@7","","DL","0"),
			Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","90","","5","x","6","HIGH","","","2,700","","","","","","","","","","3","x","12","HIGH","","","0","","","","","","","","","","3","x","15","HIGH","","","0","","","","","","","","325","","4","x","4","","","DL","5,200","","","","","","","","325","","4","x","4","","","DL","5,200","","","","","","","","325","","4","x","4","","","DL","5,200"),
			Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","BW","","","","","","","","#VALUE!","","","","","","","","BW","","","","","","","","#VALUE!","","","","","","","","","","","","","","","","0","","","","","","","","","","3","x","15","High","","","0","","","","","","","","","","3","x","15","High","","","0","","Leg Extensions","","","","","","","","3","x","15","High","","","0"),
			Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","","","3","x","12","MED","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","Chin Ups (20)","","","","","","","","","","","","","","0"),
			Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0"),
			Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0"),
			Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0",""," ","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0"),
			Arrays.asList("","","","","","","","","","","","","","","","0","",  "","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0","","","","","","","","","","","","","","","","0"),
			Arrays.asList("","Training Notes Below","","","","","","","","","","","","","","","",  "Training Notes Below","","","","","","","","","","","","","","","","Training Notes Below","","","","","","","","","","","","","","","","Training Notes Below","","","","","","","","","","","","","","","","Training Notes Below","","","","","","","","","","","","","","","","Training Notes Below","","","","","","","","","","","","","","","","Training Notes Below"),
			Arrays.asList("","","","","","","","","","","","","","","","","",  "","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","@sub6","","","","","","","","","","","","","","","","@sub6","","","","","","","","","","","","","","","","@sub6"),
			Arrays.asList(),
			Arrays.asList(),
			Arrays.asList()
			//Arrays.asList("","BLOCK 1//BASE BUILD 1/W1"),
			// Additional rows ...
		);
		*/
		
		return sheetList;
	}
	
	public synchronized String
	getGoogleSheetId(PersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception {
		PersonTrainingPlanMapping mapping = this.getMapping(_person, false);
		String sheet_id = mapping.getGoogleSheetId();
		if (sheet_id == null) {
			// grab a sheet id from the pool
			System.out.println("grab a sheet id from the pool");
			
			Criteria crit = new Criteria();
			List l = UnusedGoogleSheetDbPeer.doSelect(crit);
			UnusedGoogleSheetDb obj = (UnusedGoogleSheetDb)l.get(0);
			sheet_id = obj.getGoogleSheetId();
			System.out.println("grabbed >" + sheet_id);
			mapping.setGoogleSheetId(sheet_id);
			mapping.save();
			
			// delete sheet id from the pool
			
			crit.add(UnusedGoogleSheetDbPeer.GOOGLE_SHEET_ID, sheet_id);
			UnusedGoogleSheetDbPeer.doDelete(crit);
			
			// assuming that the google sheet is blank at this point, whatever workouts/moves currently exist in this TrainingPlan, export to the external sheet
			
			TrainingPlan selectedTrainingPlan = TrainingPlan.getTrainingPlan(mapping.getTrainingPlanDbId());
			
			FitnessServlet.updateSheetFromTrainingPlan(_person, selectedTrainingPlan, sheet_id);
			
			// fetch another
			
			CUBean.timerObj.schedule(new MaintainBlankGoogleSheetsWorkoutTemplateTask(), new Date());

		}
		
		//FitnessServlet.getSpreadsheetRawStuff(sheet_id);
		
		//FitnessServlet.updateSheetFromTrainingPlan(this, sheet_id);
		
		//return "https://docs.google.com/spreadsheets/d/" + sheet_id + "/edit#gid=1007878034";
		
		return sheet_id;
	}
	
	public void
	notifyChange() {
		
		System.out.println("notifyChange() invoked in TrainingPlan");
		
		try {
			
			Iterator itr = this.getGoogleSheetMappings().iterator();
			while (itr.hasNext()) {
				PersonTrainingPlanMapping mapping = (PersonTrainingPlanMapping)itr.next();
				FitnessServlet.updateGoogleSheetFromTrainingPlan(PersonBean.getPerson(mapping.getPersonId()), this, mapping.getGoogleSheetId());
			}
			
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
}