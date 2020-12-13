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

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;



/**
 *
 * @author marlo
 */
public class
WeightGoalType
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
	public static final short BULK_TYPE = 1;
	public static final short CUT_TYPE = 2;
	public static final short MAINTENANCE_TYPE = 3;

    protected static HashMap<Integer,WeightGoalType> hash = new HashMap<Integer,WeightGoalType>();
	
	public static final BigDecimal CALORIES_PER_GRAM_OF_FAT = new BigDecimal(9);
	public static final BigDecimal CALORIES_PER_GRAM_OF_CARB = new BigDecimal(4);
	public static final BigDecimal CALORIES_PER_GRAM_OF_PROTEIN = new BigDecimal(4);
	

    // CLASS METHODS

    public static WeightGoalType
    getWeightGoalType(int _id)
		throws TorqueException, ObjectNotFoundException {
		
		WeightGoalType plan_obj = (WeightGoalType)hash.get(_id);
		if (plan_obj == null) {
			Criteria crit = new Criteria();
			crit.add(WeightGoalTypeDbPeer.WEIGHT_GOAL_TYPE_DB_ID, _id);
			List objList = WeightGoalTypeDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate weight goal type with id: " + _id);
			}

			plan_obj = WeightGoalType.getWeightGoalType((WeightGoalTypeDb)objList.get(0));
		}

		return plan_obj;
    }

    private static WeightGoalType
    getWeightGoalType(WeightGoalTypeDb _obj) throws TorqueException {
		
		WeightGoalType plan_obj = (WeightGoalType)hash.get(_obj.getWeightGoalTypeDbId());
		if (plan_obj == null) {
			plan_obj = new WeightGoalType(_obj);
			hash.put(_obj.getWeightGoalTypeDbId(), plan_obj);
		}

		return plan_obj;
    }
	
	public static WeightGoalType
	getWeightGoalForType(short _type) throws TorqueException, ObjectAlreadyExistsException, Exception {
		Criteria crit = new Criteria();
		crit.add(WeightGoalTypeDbPeer.GOAL_TYPE, _type);
		List objList = WeightGoalTypeDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			WeightGoalType type = new WeightGoalType();
			type.setType(_type);
			type.save();
			return type;
		} else {
			return WeightGoalType.getWeightGoalType((WeightGoalTypeDb)objList.get(0));
		}
	}

    public static Vector
    getWeightGoalTypes()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addDescendingOrderByColumn(WeightGoalTypeDbPeer.GOAL_TYPE);
		crit.addDescendingOrderByColumn(WeightGoalTypeDbPeer.DESCRIPTION);
		Iterator itr = WeightGoalTypeDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(WeightGoalType.getWeightGoalType((WeightGoalTypeDb)itr.next()));
		}
		
		return vec;
    }
	

    // SQL

    /*

<table name="WEIGHT_GOAL_TYPE_DB" idMethod="native">
	<column name="WEIGHT_GOAL_TYPE_DB_ID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true"/>
	<column name="GOAL_TYPE" type="SMALLINT"/>
	<!-- 1 = bulk -->
	<!-- 2 = cut -->
	<!-- 3 = maintenance -->
	<column name="DESCRIPTION" type="LONGVARCHAR"/>

</table>

     */

    // INSTANCE VARIABLES

    private WeightGoalTypeDb type;

    // CONSTRUCTORS

    public
    WeightGoalType() {
		type = new WeightGoalTypeDb();
		isNew = true;
    }

    public
    WeightGoalType(WeightGoalTypeDb _obj) {
		type = _obj;
		isNew = false;
    }

    // INSTANCE METHODS

    public int
    getId() {
		return type.getWeightGoalTypeDbId();
    }

    public String
    getLabel() {
		return this.getValue();
    }
	
	public String getDescription() {
		String str = type.getDescription();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setDescription(String description) {
		type.setDescription(description);
	}
	
	public short
	getType() {
		return type.getGoalType();
	}
	
	public String
	getTypeString() {
		switch (type.getGoalType()) {
			case WeightGoalType.BULK_TYPE: return "Bulk";
			case WeightGoalType.CUT_TYPE: return "Cut";
			case WeightGoalType.MAINTENANCE_TYPE: return "Maintenance";
		}
		return "[TYPE NOT FOUND]";
	}
	
	public void
	setType(short _type) {
		type.setGoalType(_type);
	}

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		type.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		type.save();
    }
	

	
	
	
}