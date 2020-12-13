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
MacroGoal
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
	public static final short MAINTAIN_TYPE = 1;
	public static final short LEAN_BULK_TYPE = 2;
	public static final short RAPID_BULK_TYPE = 3;
	public static final short FAT_LOSS_TYPE = 4;
	public static final short RAPID_FAT_LOSS_TYPE = 5;
	
	public static final short LOW_FAT_GOAL = 1;
	public static final short RECOMMENDED_FAT_GOAL = 2;
	public static final short HIGH_FAT_GOAL = 3;
	public static final short KETO_1_GOAL = 4;
	public static final short KETO_2_GOAL = 5;
	
	public static final short LOW_PROTEIN_GOAL = 1;
	public static final short RECOMMENDED_PROTEIN_GOAL = 2;
	public static final short HIGH_PROTEIN_GOAL = 3;
	public static final short HIGHER_PROTEIN_GOAL = 4;

    protected static HashMap<Integer,MacroGoal> hash = new HashMap<Integer,MacroGoal>();
	
	public static final BigDecimal CALORIES_PER_GRAM_OF_FAT = new BigDecimal(9);
	public static final BigDecimal CALORIES_PER_GRAM_OF_CARB = new BigDecimal(4);
	public static final BigDecimal CALORIES_PER_GRAM_OF_PROTEIN = new BigDecimal(4);
	

    // CLASS METHODS

    public static MacroGoal
    getMacroGoal(int _id)
		throws TorqueException, ObjectNotFoundException {
		
		MacroGoal goal_obj = (MacroGoal)hash.get(_id);
		if (goal_obj == null) {
			Criteria crit = new Criteria();
			crit.add(MacroGoalDbPeer.MACRO_GOAL_DB_ID, _id);
			List objList = MacroGoalDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate goal with id: " + _id);
			}

			goal_obj = MacroGoal.getMacroGoal((MacroGoalDb)objList.get(0));
		}

		return goal_obj;
    }

    private static MacroGoal
    getMacroGoal(MacroGoalDb _obj) throws TorqueException {
		
		MacroGoal goal_obj = (MacroGoal)hash.get(_obj.getMacroGoalDbId());
		if (goal_obj == null) {
			goal_obj = new MacroGoal(_obj);
			hash.put(_obj.getMacroGoalDbId(), goal_obj);
		}

		return goal_obj;
    }
	
	public static boolean
	hasCurrentMacroGoal(UKOnlinePersonBean _person) throws TorqueException {
		Criteria crit = new Criteria();
		crit.add(MacroGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(MacroGoalDbPeer.IS_ACTIVE, (short)1);
		return !MacroGoalDbPeer.doSelect(crit).isEmpty();
	}

    public static MacroGoal
    getCurrentMacroGoal(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		Criteria crit = new Criteria();
		crit.add(MacroGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(MacroGoalDbPeer.IS_ACTIVE, (short)1);
		System.out.println("getCurrentMacroGoal crit >" + crit.toString());
		List objList = MacroGoalDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			throw new ObjectNotFoundException("Current macro goal not found.");
			/*
			MacroGoal new_goal = new MacroGoal();
			new_goal.setPerson(_person);
			new_goal.activate();
			new_goal.setCalories("0.00");
			new_goal.setFat("0.00");
			new_goal.setCarbs("0.00");
			new_goal.setProtein("0.00");
			new_goal.setGoalType(MacroGoal.MAINTAIN_TYPE);
			new_goal.setCreateOrModifyPerson(_person);
			new_goal.save();
			hash.put(new_goal.getId(), new_goal);
			return new_goal;
			*/
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unique current macro goal for " + _person.getLabel());
		}

		return MacroGoal.getMacroGoal((MacroGoalDb)objList.get(0));
    }

	/*
    public static MacroGoal
    getCurrentMacroGoal(UKOnlinePersonBean _person, BigDecimal _fat, BigDecimal _carbs, BigDecimal _protein)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		Criteria crit = new Criteria();
		crit.add(MacroGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(MacroGoalDbPeer.GOAL_TYPE, MacroGoal.GRAMS_TYPE);
		crit.add(MacroGoalDbPeer.FAT, _fat);
		crit.add(MacroGoalDbPeer.CARBS, _carbs);
		crit.add(MacroGoalDbPeer.PROTEIN, _protein);
		crit.add(MacroGoalDbPeer.IS_ACTIVE, (short)1);
		List objList = MacroGoalDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			MacroGoal new_goal = new MacroGoal();
			new_goal.setPerson(_person);
			new_goal.activate();
			//new_goal.setCalories("0.00");
			new_goal.setFat(_fat);
			new_goal.setCarbs(_carbs);
			new_goal.setProtein(_protein);
			new_goal.setGoalType(MacroGoal.GRAMS_TYPE);
			new_goal.setCreateOrModifyPerson(_person);
			new_goal.save();
			hash.put(new_goal.getId(), new_goal);
			return new_goal;
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unique current macro goal for " + _person.getLabel());
		}

		return MacroGoal.getMacroGoal((MacroGoalDb)objList.get(0));
    }
	*/

    public static Vector
    getMacroGoals(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(MacroGoalDbPeer.PERSON_ID, _person.getId());
		crit.add(MacroGoalDbPeer.IS_HIDDEN, (short)0);
		crit.addDescendingOrderByColumn(MacroGoalDbPeer.CREATION_DATE);
		Iterator itr = MacroGoalDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(MacroGoal.getMacroGoal((MacroGoalDb)itr.next()));
		}
		
		return vec;
    }
	

    // SQL

    /*

<table name="MACRO_GOAL_DB" idMethod="native">
	<column name="MACRO_GOAL_DB_ID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true"/>
	<column name="PERSON_ID" required="false" type="INTEGER"/>
    <column name="IS_ACTIVE" type="SMALLINT" default="1"/>
	
	<column name="NAME" required="false" size="200" type="VARCHAR"/>
	
	<column name="CALORIES" scale="2" size="7" type="DECIMAL"/>
	<column name="FAT" scale="2" size="7" type="DECIMAL"/>
	<column name="CARBS" scale="2" size="7" type="DECIMAL"/>
	<column name="PROTEIN" scale="2" size="7" type="DECIMAL"/>
	
	<column name="GOAL_TYPE" type="SMALLINT"/>
	<!-- 1 = maintain -->
	<!-- 2 = lean bulk -->
	<!-- 3 = rapid bulk -->
	<!-- 4 = fat loss -->
	<!-- 5 = rapid fat loss -->
	
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

    private MacroGoalDb goal;

    // CONSTRUCTORS

    public
    MacroGoal() {
		goal = new MacroGoalDb();
		isNew = true;
    }

    public
    MacroGoal(MacroGoalDb _obj) {
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
	equals(MacroGoal _g) {
		if (_g == null) {
			return false;
		}
		return ( this.getId() == _g.getId() );
	}
	
	/*
	public boolean
	equals(MacroGoal _g) {
		try {
			if (this.getGoalType() != _g.getGoalType()) {
				return false;
			}
			if (this.getFat().compareTo(_g.getFat()) != 0) {
				return false;
			}
			if (this.getCarbs().compareTo(_g.getCarbs()) != 0) {
				return false;
			}
			if (this.getProtein().compareTo(_g.getProtein()) != 0) {
				return false;
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		return true;
	}
	*/

    public int
    getId() {
		return goal.getMacroGoalDbId();
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
			return this.getGoalTypeString() + " Goal " + this.getCreationDateString();
		}
		return this.getGoalTypeString() + " Goal " + this.getCreationDateString();
    }
	
	public void
	setCalories(String _cals) {
		//goal.setCalories(new BigDecimal(_cals, Food.return_context));
		goal.setCalories(new BigDecimal(_cals));
	}
	
	public BigDecimal
	getCalories() {
		return goal.getCalories();
	}
	
	public String
	getCaloriesString() throws IllegalValueException, ObjectAlreadyExistsException, Exception {
		BigDecimal calories = goal.getCalories();
		if (calories == null) {
			return "";
		}
		if (calories.compareTo(BigDecimal.ZERO) == 0) {
			this.calculateCalories();
			this.save();
		}
		return calories.setScale(0, RoundingMode.HALF_UP).toString();
	}
	
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
	getFat() {
		return goal.getFat();
	}
	
	public String
	getFatString() {
		//return goal.getFat().setScale(0, RoundingMode.HALF_UP).toString();
		return this.getStringFromBigDecimal(goal.getFat());
	}
	
	public BigDecimal
	getCarbs() {
		return goal.getCarbs();
	}
	
	public String
	getCarbsString() {
		//return goal.getCarbs().setScale(0, RoundingMode.HALF_UP).toString();
		return this.getStringFromBigDecimal(goal.getCarbs());
	}
	
	public BigDecimal
	getProtein() {
		return goal.getProtein();
	}
	
	public String
	getProteinString() {
		//return goal.getProtein().setScale(0, RoundingMode.HALF_UP).toString();
		return this.getStringFromBigDecimal(goal.getProtein());
	}
	
	public void
	setFat(String _fat) throws IllegalValueException {
		//goal.setFat(new BigDecimal(_fat, Food.return_context));
		this.setFat(new BigDecimal(_fat));
	}
	
	public void
	setFat(BigDecimal _bd) throws IllegalValueException {
		if (_bd.compareTo(BigDecimal.ZERO) == -1) {
			throw new IllegalValueException("Please enter a positive daily fat value.");
		}
		goal.setFat(_bd);
	}
	
	public void
	setCarbs(String _carbs) throws IllegalValueException {
		//goal.setCarbs(new BigDecimal(_carbs, Food.return_context));
		this.setCarbs(new BigDecimal(_carbs));
	}
	
	public void
	setCarbs(BigDecimal _bd) throws IllegalValueException {
		if (_bd.compareTo(BigDecimal.ZERO) == -1) {
			throw new IllegalValueException("Please enter a positive daily carbohydrate value.");
		}
		goal.setCarbs(_bd);
	}
	
	public void
	setProtein(String _protein) throws IllegalValueException {
		//goal.setProtein(new BigDecimal(_protein, Food.return_context));
		this.setProtein(new BigDecimal(_protein));
	}
	
	public void
	setProtein(BigDecimal _bd) throws IllegalValueException {
		if (_bd.compareTo(BigDecimal.ZERO) == -1) {
			throw new IllegalValueException("Please enter a positive daily protein value.");
		}
		goal.setProtein(_bd);
	}
	
	public short
	getGoalType() {
		return goal.getGoalType();
	}
	
	public String
	getGoalTypeString() {
		switch (goal.getGoalType()) {
			case MacroGoal.MAINTAIN_TYPE: return "Maintainance";
			case MacroGoal.LEAN_BULK_TYPE: return "Lean Bulk";
			case MacroGoal.RAPID_BULK_TYPE: return "Rapid Bulk";
			case MacroGoal.FAT_LOSS_TYPE: return "Fat Loss";
			case MacroGoal.RAPID_FAT_LOSS_TYPE: return "Rapid Fat Loss";
		}
		return "[TYPE NOT FOUND]";
	}
	
	public String
	getCalorieGoalValueString() {
		switch (goal.getGoalType()) {
			case MacroGoal.MAINTAIN_TYPE: return "0";
			case MacroGoal.LEAN_BULK_TYPE: return "250";
			case MacroGoal.RAPID_BULK_TYPE: return "500";
			case MacroGoal.FAT_LOSS_TYPE: return "-250";
			case MacroGoal.RAPID_FAT_LOSS_TYPE: return "-500";
		}
		return "";
	}
	
	public String
	getFatGoalValueString() {
		switch (goal.getFatGoal()) {
			case MacroGoal.LOW_FAT_GOAL: return ".3";
			case MacroGoal.RECOMMENDED_FAT_GOAL: return ".4";
			case MacroGoal.HIGH_FAT_GOAL: return ".5";
			case MacroGoal.KETO_1_GOAL: return "1.3";
			case MacroGoal.KETO_2_GOAL: return "1.5";
		}
		return "";
	}
	
	public String
	getProteinGoalValueString() {
		switch (goal.getProteinGoal()) {
			case MacroGoal.LOW_PROTEIN_GOAL: return ".8";
			case MacroGoal.RECOMMENDED_PROTEIN_GOAL: return ".95";
			case MacroGoal.HIGH_PROTEIN_GOAL: return "1.1";
			case MacroGoal.HIGHER_PROTEIN_GOAL: return "1.3";
		}
		return "";
	}
	
	public UKOnlinePersonBean
	getPerson() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(goal.getPersonId());
	}
	
	public void
	setPerson(PersonBean _person) throws TorqueException {
		goal.setPersonId(_person.getId());
	}
	
	public void
	setGoalType(short _type) {
		goal.setGoalType(_type);
	}
	
	public void
	setFatGoal(short _type) {
		goal.setFatGoal(_type);
	}
	
	public void
	setProteinGoal(short _type) {
		goal.setProteinGoal(_type);
	}

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
	setActive(boolean _b) {
		goal.setIsActive(_b ? (short)1 : (short)0);
	}
	
	public boolean
	isAuto() {
		if (this.isNew()) {
			return false;
		}
		return (goal.getIsAuto() == (short)1);
	}

	public void
	setAuto(boolean _b) {
		goal.setIsAuto(_b ? (short)1 : (short)0);
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