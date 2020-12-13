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
WeightPreferences
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
	public static final BigDecimal lb_per_kg = new BigDecimal("2.2046226218");
	public static final BigDecimal inches_per_cm = new BigDecimal("0.393701");
	
	public static final BigDecimal kg_per_lb = new BigDecimal("0.453592");
	public static final BigDecimal cm_per_inch = new BigDecimal("2.54");
	
	public static final BigDecimal bmr_constant_weight = new BigDecimal("10");
	public static final BigDecimal bmr_constant_height = new BigDecimal("6.25");
	public static final BigDecimal bmr_constant_age = new BigDecimal("5");
	public static final BigDecimal bmr_constant_women = new BigDecimal("-161");
	public static final BigDecimal bmr_constant_men = new BigDecimal("5");
	
	public static final short LBS_TYPE = 1;
	public static final short KG_TYPE = 2;
	
	public static final short MALE = 1;
	public static final short FEMALE = 2;

    protected static HashMap<Integer,WeightPreferences> hash = new HashMap<Integer,WeightPreferences>();
    protected static HashMap<PersonBean,WeightPreferences> person_hash = new HashMap<PersonBean,WeightPreferences>();

    // CLASS METHODS

    public static WeightPreferences
    getWeightPreferences(int _id)
		throws TorqueException, ObjectNotFoundException {
		
		WeightPreferences plan_obj = (WeightPreferences)hash.get(_id);
		if (plan_obj == null) {
			Criteria crit = new Criteria();
			crit.add(WeightPreferencesDbPeer.WEIGHT_PREFERENCES_DB_ID, _id);
			List objList = WeightPreferencesDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate weight preferences with id: " + _id);
			}

			plan_obj = WeightPreferences.getWeightPreferences((WeightPreferencesDb)objList.get(0));
		}

		return plan_obj;
    }

    private static WeightPreferences
    getWeightPreferences(WeightPreferencesDb _obj) throws TorqueException {
		
		WeightPreferences plan_obj = (WeightPreferences)hash.get(_obj.getWeightPreferencesDbId());
		if (plan_obj == null) {
			plan_obj = new WeightPreferences(_obj);
			hash.put(_obj.getWeightPreferencesDbId(), plan_obj);
		}

		return plan_obj;
    }

    public static WeightPreferences
    getWeightPreference(PersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, IllegalValueException, Exception {
		
		WeightPreferences pref_obj = person_hash.get(_person);
		if (pref_obj == null) {
			Criteria crit = new Criteria();
			crit.add(WeightPreferencesDbPeer.PERSON_ID, _person.getId());
			System.out.println("getCurrentWeightGoal crit >" + crit.toString());
			List objList = WeightPreferencesDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				WeightPreferences preferences = new WeightPreferences();
				preferences.setPerson(_person);
				preferences.setUnitType(WeightPreferences.LBS_TYPE);
				preferences.save();
				hash.put(preferences.getId(), preferences);
				person_hash.put(_person, preferences);
				return preferences;
			} else if (objList.size() > 1) {
				throw new UniqueObjectNotFoundException("Could not locate unique weight preferences for " + _person.getLabel());
			}
			
			pref_obj = WeightPreferences.getWeightPreferences((WeightPreferencesDb)objList.get(0));
		}

		return pref_obj;
    }
	
    // SQL

    /*

<table name="WEIGHT_PREFERENCES_DB" idMethod="native">
	<column name="WEIGHT_PREFERENCES_DB_ID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true"/>
	<column name="PERSON_ID" required="false" type="INTEGER"/>
	<column name="UNIT_TYPE" type="SMALLINT"/>
	<!-- 1 = pounds -->
	<!-- 2 = kgs -->
	<column name="GENDER" type="SMALLINT"/>
	<!-- 1 = male -->
	<!-- 2 = female -->
	<column name="WEIGHT" scale="2" size="5" type="DECIMAL"/>
	<column name="AGE" type="SMALLINT"/>
	<column name="HEIGHT_1" type="SMALLINT"/>
	<column name="HEIGHT_2" type="SMALLINT"/>
	<column name="ACTIVITY_LEVEL" scale="3" size="5" type="DECIMAL"/>
	
	<column name="TIME_ZONE" required="false" size="30" type="VARCHAR"/>

    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private WeightPreferencesDb preferences;

    // CONSTRUCTORS

    public
    WeightPreferences() {
		preferences = new WeightPreferencesDb();
		isNew = true;
    }

    public
    WeightPreferences(WeightPreferencesDb _obj) {
		preferences = _obj;
		isNew = false;
    }

    // INSTANCE METHODS

    public int
    getId() {
		return preferences.getWeightPreferencesDbId();
    }

    public String
    getLabel() {
		return this.getValue();
    }
	
	public void
	setPerson(PersonBean _person) throws TorqueException {
		preferences.setPersonId(_person.getId());
	}
	
	public UKOnlinePersonBean
	getPerson() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(preferences.getPersonId());
	}
	
	public short
	getUnitType() {
		return preferences.getUnitType();
	}
	
	public String
	getUnitTypeString() {
		switch (preferences.getUnitType()) {
			case WeightPreferences.LBS_TYPE: return "lbs";
			case WeightPreferences.KG_TYPE: return "kg";
		}
		return "[TYPE NOT FOUND]";
	}
	
	private BigDecimal
	getWeight() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		BigDecimal weight = null;
		if (WeightEntry.hasEntry(this.getPerson())) {
			// if the person has recorded a weight entry, use the latest value there
			weight = WeightEntry.getLastEntry(this.getPerson()).getEntryAmount();
		} else {
			weight = preferences.getWeight();
		}
		
		return weight;
	}
	
	public String
	getWeightString() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		/*
		if (this.getUnitType() == WeightPreferences.LBS_TYPE) {
			return this.getStringFromBigDecimal(preferences.getWeight());
		} else {
			// need to convert to pounds
			return this.getStringFromBigDecimal(this.getWeightInKG());
		}
		*/
		
		// return whatever the user typed in
		
		BigDecimal weight = this.getWeight();
		if (weight == null) {
			return "";
		}
		return this.getStringFromBigDecimal(weight);
		
	}
	
	/*
	public BigDecimal
	getWeightInPounds() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return this.getWeight(); // stored in whatever units the user typed in
	}
	*/
	
	public String
	getWeightInLbsString() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		if (this.getUnitType() == WeightPreferences.LBS_TYPE) {
			// value is stored in KG -return
			return this.getStringFromBigDecimal(this.getWeight());
		} else {
			// value is stored in lbs - need to convert
			return this.getStringFromBigDecimal(WeightPreferences.kg_per_lb.multiply(this.getWeight()));
		}
	}
	
	public String
	getWeightInKGString() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		if (this.getUnitType() == WeightPreferences.LBS_TYPE) {
			// value is stored in lbs - need to convert
			return this.getStringFromBigDecimal(WeightPreferences.kg_per_lb.multiply(this.getWeight()));
		} else {
			// value is stored in KG -return
			return this.getStringFromBigDecimal(this.getWeight());
		}
	}
	
	private BigDecimal
	getWeightInKG() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		//System.out.println("WeightPreferences.kg_per_lb >" + WeightPreferences.kg_per_lb);
		//System.out.println("this.getWeight() >" + this.getWeight());
		
		if (this.getUnitType() == WeightPreferences.LBS_TYPE) {
			// value is stored in lbs - need to convert
			return WeightPreferences.kg_per_lb.multiply(this.getWeight());
		} else {
			// value is store in KG -return
			return this.getWeight();
		}
	}
	
	public void
	setWeight(BigDecimal _bd) {
		// store in pounds - nope - store raw - whatever units selected
		/*
		if (this.getUnitType() == WeightPreferences.LBS_TYPE) {
			preferences.setWeight(_bd);
		} else {
			// need to convert to pounds
			preferences.setWeight(WeightPreferences.lb_per_kg.multiply(_bd));
		}
		*/
		preferences.setWeight(_bd);
	}
	
	public short
	getAge() {
		return preferences.getAge();
	}
	
	public String
	getAgeString() {
		short age = preferences.getAge();
		if (age < 1) {
			return "";
		}
		return age + "";
	}
	
	public void
	setAge(short _s) {
		preferences.setAge(_s);
	}
	
	public BigDecimal
	getActivityLevel() {
		return preferences.getActivityLevel();
	}
	
	public String
	getActivityLevelString() {
		BigDecimal activityLevel = preferences.getActivityLevel();
		if (activityLevel == null) {
			return "";
		}
		return activityLevel.setScale(3, RoundingMode.HALF_UP).toString();
	}
	
	public void
	setActivityLevel(BigDecimal _bd) {
		preferences.setActivityLevel(_bd);
	}
	
	public void
	setUnitType(short _type) {
		preferences.setUnitType(_type);
	}
	
	public short
	getHeightPrimaryUnit() {
		return preferences.getHeight1();
	}
	
	public String
	getHeightPrimaryUnitString() {
		if (preferences.getHeight1() == (short)0) {
			return "";
		}
		return preferences.getHeight1() + "";
	}
	
	public void
	setHeightPrimaryUnit(short _s) {
		preferences.setHeight1(_s);
	}
	
	public short
	getHeightSecondaryUnit() {
		return preferences.getHeight2();
	}
	
	public String
	getHeightSecondaryUnitString() {
		if (preferences.getHeight2() == (short)0) {
			return "";
		}
		return preferences.getHeight2() + "";
	}
	
	public void
	setHeightSecondaryUnit(short _s) {
		preferences.setHeight2(_s);
	}
	
	/*
	public BigDecimal
	getHeightInInches() {
		if (this.getUnitType() == WeightPreferences.LBS_TYPE) {
			return new BigDecimal( (this.getHeightPrimaryUnit() * (short)12) + this.getHeightSecondaryUnit() );
		} else {
			BigDecimal height_in_cm = new BigDecimal( (this.getHeightPrimaryUnit() * (short)100) + this.getHeightSecondaryUnit() );
			return WeightPreferences.inches_per_cm.multiply(height_in_cm);
		}
	}
	*/
	
	private BigDecimal
	getHeightInCm() {
		if (this.getUnitType() == WeightPreferences.LBS_TYPE) {
			// this indicates that the value is stored in imperial units
			BigDecimal height_in_inches = new BigDecimal( (this.getHeightPrimaryUnit() * (short)12) + this.getHeightSecondaryUnit() );
			return WeightPreferences.cm_per_inch.multiply(height_in_inches);
		} else {
			// this indicates that the value is stored in metric units
			return new BigDecimal( (this.getHeightPrimaryUnit() * (short)100) + this.getHeightSecondaryUnit() );
		}
	}
	
	public boolean
	isMale() {
		return preferences.getGender() == WeightPreferences.MALE;
	}
	
	public short
	getGender() {
		return preferences.getGender();
	}
	
	public String
	getGenderString() {
		switch (preferences.getGender()) {
			case WeightPreferences.MALE: return "male";
			case WeightPreferences.FEMALE: return "female";
		}
		return "[GENDER NOT FOUND]";
	}
	
	public void
	setGender(short _gender) {
		preferences.setGender(_gender);
	}

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		preferences.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		preferences.save();
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
	
	public BigDecimal
	getBMR() {
		BigDecimal bmr = BigDecimal.ZERO;
		try {
			BigDecimal weight_adj = WeightPreferences.bmr_constant_weight.multiply(this.getWeightInKG());
			BigDecimal height_adj = WeightPreferences.bmr_constant_height.multiply(this.getHeightInCm());
			BigDecimal age_bd = new BigDecimal(this.getAge());
			BigDecimal age_adj = WeightPreferences.bmr_constant_age.multiply(age_bd);
			bmr = weight_adj.add(height_adj).add(age_adj).add(this.isMale() ? WeightPreferences.bmr_constant_men : WeightPreferences.bmr_constant_women);
		} catch (Exception x) {
			x.printStackTrace();
		}
		return bmr;
	}
	
	public String
	getBMRString() {
		return this.getStringFromBigDecimal(this.getBMR());
	}
	
	public BigDecimal
	getTotalEnergyExpenditure() {
		BigDecimal tee = BigDecimal.ZERO;
		try {
			BigDecimal bmr = this.getBMR();
			tee = bmr.multiply(this.getActivityLevel());
		} catch (Exception x) {
			x.printStackTrace();
		}
		return tee;
	}
	
	public String
	getTotalEnergyExpenditureString() {
		return this.getStringFromBigDecimal(this.getTotalEnergyExpenditure());
	}
	
	public java.util.TimeZone
	getTimeZone() {
		String timeZoneStr = preferences.getTimeZone();
		if (timeZoneStr == null) {
			timeZoneStr = "US/Central";
		}
		return java.util.TimeZone.getTimeZone(timeZoneStr);
	}
	
	public String
	getTimeZoneString() {
		String str = preferences.getTimeZone();
		if (str == null) {
			return "";
		}
		return str;
	}
	
	public void
	setTimeZone(String _time_zone) {
		preferences.setTimeZone(_time_zone);
	}
	
}
