/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import com.valeo.qbpos.data.TenderRet;

import java.math.BigDecimal;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
HerbDosageUseDirections
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,HerbDosageUseDirections> hash = new HashMap<Integer,HerbDosageUseDirections>(11);

    // CLASS METHODS
	
	public static void
	delete(HerbDosageUseDirections obj) throws TorqueException {
		Integer key = new Integer(obj.getId());
		hash.remove(key);
		HerbDosageUseDirectionsDbPeer.doDelete(obj.use_directions);
	}

    public static HerbDosageUseDirections
    getHerbDosageUseDirections(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		HerbDosageUseDirections use_directions = (HerbDosageUseDirections)hash.get(key);
		if (use_directions == null)
		{
			Criteria crit = new Criteria();
			crit.add(HerbDosageUseDirectionsDbPeer.HERB_DOSAGE_USE_DIRECTIONS_DB_ID, _id);
			List objList = HerbDosageUseDirectionsDbPeer.doSelect(crit);
			if (objList.isEmpty())
				throw new ObjectNotFoundException("Could not locate Herb Dosage Use Directions with id: " + _id);

			use_directions = HerbDosageUseDirections.getHerbDosageUseDirections((HerbDosageUseDirectionsDb)objList.get(0));
		}

		return use_directions;
    }

    public static HerbDosageUseDirections
    getHerbDosageUseDirections(HerbDosageUseDirectionsDb _use_directions)
		throws TorqueException
    {
		Integer key = new Integer(_use_directions.getHerbDosageUseDirectionsDbId());
		HerbDosageUseDirections use_directions = (HerbDosageUseDirections)hash.get(key);
		if (use_directions == null)
		{
			use_directions = new HerbDosageUseDirections(_use_directions);
			hash.put(key, use_directions);
		}

		return use_directions;
    }

    // SQL

    /*
<table name="HERB_DOSAGE_USE_DIRECTIONS_DB">
    <column name="HERB_DOSAGE_USE_DIRECTIONS_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="HERB_DOSAGE_DB_ID" required="true" type="INTEGER"/>
	
	<column name="START_DAY" type="SMALLINT"/>
	<column name="END_DAY" type="SMALLINT"/>
	<column name="ML" type="SMALLINT"/>
	<column name="MIX_IN" type="VARCHAR" size="30"/>
	<column name="TIMES" type="SMALLINT"/>
	<column name="PERIOD" type="VARCHAR" size="20"/>
	<column name="AT" type="VARCHAR" size="20"/>

    <foreign-key foreignTable="HERB_DOSAGE_DB">
		<reference local="HERB_DOSAGE_DB_ID" foreign="HERB_DOSAGE_DB_ID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private HerbDosageUseDirectionsDb use_directions;

    // CONSTRUCTORS

    public
    HerbDosageUseDirections()
    {
		use_directions = new HerbDosageUseDirectionsDb();
		isNew = true;
    }

    public
    HerbDosageUseDirections(HerbDosageUseDirectionsDb _use_directions)
    {
		use_directions = _use_directions;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public void setParent(HerbDosage _parent) throws TorqueException {
		use_directions.setHerbDosageDbId(_parent.getId());
	}

	public String getAtDesc() {
		return "daily";
	}

	public void setAtDesc(String atDesc) {
		this.use_directions.setAt(atDesc);
	}

	public short getEndDay() {
		return use_directions.getEndDay();
	}

	public void setEndDay(short endDay) {
		this.use_directions.setEndDay(endDay);
	}

	public short getML() {
		return use_directions.getMl();
	}

	public void setML(short mL) {
		this.use_directions.setMl(mL);
	}

	public String getMixIn() {
		return use_directions.getMixIn();
	}

	public void setMixIn(String mixIn) {
		this.use_directions.setMixIn(mixIn);
	}

	public String getPeriod() {
		String str = use_directions.getPeriod();
		if (str == null)
			return "";
		return str;
	}

	public void setPeriod(String period) {
		this.use_directions.setPeriod(period);
	}

	public String getMeasure() {
		String str = use_directions.getMeasure();
		if (str == null)
			return "ml";
		return str;
	}

	public String getMeasureWithPossibleLeadingSpace() {
		String str = use_directions.getMeasure();
		if (str == null)
			return "ml";
		if (str.equals("drops"))
			return " drops";
		return str;
	}

	public void setMeasure(String period) {
		this.use_directions.setMeasure(period);
	}

	public short getStartDay() {
		return use_directions.getStartDay();
	}

	public void setStartDay(short startDay) {
		this.use_directions.setStartDay(startDay);
	}

	public short getNumTimes() {
		return use_directions.getTimes();
	}

	public void setNumTimes(short times) {
		this.use_directions.setTimes(times);
	}

    public int
    getId()
    {
		return use_directions.getHerbDosageUseDirectionsDbId();
    }
	
	private String
	getTspString() {
		if (this.getMeasure().equals("ml")) {
			try {
				if (this.getML() < 5)
					return "";
				return " (" + (this.getML() / 5) + " tsp)";
			} catch (Exception x) {
				return "";
			}
		}
		
		return "";
	}

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return "Day " + this.getStartDay() + "-" + this.getEndDay() + " " + this.getML() + this.getMeasureWithPossibleLeadingSpace() + this.getTspString() + " in " + this.getMixIn() + " " + this.getNumTimes() + " x " + this.getAtDesc() + " @ " + this.getPeriod();
    }

    public String
    getValue()
    {
		return use_directions.getHerbDosageUseDirectionsDbId() + "";
    }

    protected void
    insertObject()
		throws Exception
    {
		use_directions.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		use_directions.save();
    }

	
}