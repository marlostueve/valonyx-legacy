/*
 * MineralRatioBean.java
 *
 * Created on June 25, 2007, 8:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import java.math.BigDecimal;

/**
 *
 * @author  marlo
 * @version 
 */
public class
MineralRatioBean
	extends com.badiyan.uk.beans.CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES
	
	private static HashMap ratios = new HashMap(19);
	
	// CLASS METHODS
	
	public static void
	delete(int _id)
	    throws TorqueException
	{
	    Criteria crit = new Criteria();
	    crit.add(MineralRatioPeer.MINERAL_RATIOS_ID, _id);
	    MineralRatioPeer.doDelete(crit);
	}
	
	public static MineralRatioBean
	getMineralRatio(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		MineralRatioBean ratio = (MineralRatioBean)ratios.get(key);
		if (ratio == null)
		{	
			Criteria crit = new Criteria();
			crit.add(MineralRatioPeer.MINERAL_RATIOS_ID, _id);
			List objList = MineralRatioPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate mineral ratio with id: " + _id);
		
			ratio = MineralRatioBean.getMineralRatio((MineralRatio)objList.get(0));
		}
		
		return ratio;
	}
	
	private static MineralRatioBean
	getMineralRatio(MineralRatio _ratio)
	{
		Integer key = new Integer(_ratio.getMineralRatiosId());
		MineralRatioBean ratio = (MineralRatioBean)ratios.get(key);
		if (ratio == null)
		{
			ratio = new MineralRatioBean(_ratio);
			ratios.put(key, ratio);
		}
		
		return ratio;
	}
	
	public static Vector
	getMineralRatios(PersonBean _person)
		throws TorqueException
	{
		Vector vec = new Vector();
				
		Criteria crit = new Criteria();
		crit.add(MineralRatioPeer.PERSON_ID, _person.getId());
		crit.addAscendingOrderByColumn(MineralRatioPeer.ANALYSIS_DATE);
		Iterator obj_itr = MineralRatioPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(MineralRatioBean.getMineralRatio((MineralRatio)obj_itr.next()));
		
		return vec;
	}
	
	// INSTANCE VARIABLES
	
	private MineralRatio ratio;
	
	// CONSTRUCTORS
	
	public
	MineralRatioBean()
	{
		ratio = new MineralRatio();
		isNew = true;
	}
	
	public
	MineralRatioBean(MineralRatio _ratio)
	{
		ratio = _ratio;
		isNew = false;
	}
	
	// INSTANCE METHODS
	
	/*
	 *<table name="MINERAL_RATIO" idMethod="native">
	    <column name="MINERAL_RATIOS_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	    
	    <column name="PERSON_ID" required="true" type="INTEGER"/>
	    
	    <column name="CA_MG" type="DECIMAL"/>
	    <column name="CA_K" type="DECIMAL"/>
	    <column name="NA_MG" type="DECIMAL"/>
	    <column name="NA_K" type="DECIMAL"/>
	    <column name="ZN_CU" type="DECIMAL"/>
	    <column name="CA_P" type="DECIMAL"/>
	    
	    <column name="OXIDATION_TYPE" size="100" type="VARCHAR"/>

	    <column name="NOTES" type="LONGVARCHAR"/>
	    
	    <column name="ANALYSIS_DATE" required="true" type="TIMESTAMP"/>
	    <column name="CREATION_DATE" required="true" type="TIMESTAMP"/>
	    <column name="MODIFICATION_DATE" required="false" type="TIMESTAMP"/>
	    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
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
	
	public boolean
	hiddenCopper()
	{
	    if ((this.getCu() != null) && (this.getCu().floatValue() < 1.0f))
		return true;
	    
	    if ((this.getCa() != null) && (this.getCa().floatValue() > 50.0f))
		return true;
	    
	    if ((this.getHg() != null) && (this.getHg().floatValue() > 0.06f))
		return true;
	    
	    if ((this.getNaK() != null) && (this.getNaK().floatValue() < 2.50f))
		return true;
	    
	    if ((this.getK() != null) && (this.getK().floatValue() < 4.0f))
		return true;
	    
	    return false;
	}
	
	public String
	getHiddenCopperString()
	{
	    String str = null;
	    
	    if ((this.getCu() != null) && (this.getCu().floatValue() < 1.0f))
		str = "Cu < 1";
	    
	    if ((this.getCa() != null) && (this.getCa().floatValue() > 50.0f))
	    {
		if (str == null)
		    str = "Ca > 50";
		else
		    str += ", Ca > 50";
	    }
	    
	    if ((this.getHg() != null) && (this.getHg().floatValue() > 0.06f))
	    {
		if (str == null)
		    str = "Hg >.06";
		else
		    str += ", Hg >.06";
	    }
	    
	    if ((this.getNaK() != null) && (this.getNaK().floatValue() < 2.50f))
	    {
		if (str == null)
		    str = "Na/K ratio < 2.5";
		else
		    str += ", Na/K ratio < 2.5";
	    }
	    
	    if ((this.getK() != null) && (this.getK().floatValue() < 4.0f))
	    {
		if (str == null)
		    str = "K < 4";
		else
		    str += ", K < 4";
	    }
	    
	    if (str == null)
		return "No Hidden CU found";
	    
	    return str;
	}
	
	public PersonBean
	getPerson()
	    throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
	    return UKOnlinePersonBean.getPerson(ratio.getPersonId());
	}
	
	public void
	setPerson(UKOnlinePersonBean _person)
	    throws TorqueException
	{
	    ratio.setPersonId(_person.getId());
	}
	
	public String
	getNotesString()
	{
	    String str = ratio.getNotes();
	    if (str == null)
		return "";
	    return str;
	}
	
	public int
	getId()
	{
		return ratio.getMineralRatiosId();
	}
	
	public String
	getLabel()
	{
	    Date analysis_date = ratio.getAnalysisDate();
	    if (analysis_date == null)
		return "";
	    
	    String analysis_date_str = CUBean.getUserDateString(analysis_date);
	    return analysis_date_str + " (" + this.getCaMgString() + " - " + this.getCaKString() + " - " + this.getNaMgString() + " - " + this.getNaKString() + " - " + this.getZnCuString() + " - " + this.getCaPString() + ") " + this.getOxidationType();
	}
	
	public String
	getValue()
	{
		return ratio.getMineralRatiosId() + "";
	}
	
	public Date
	getAnalysisDate()
	{
	    return ratio.getAnalysisDate();
	}
	
	public String
	getAnalysisDateString()
	{
	    Date analysis_date = ratio.getAnalysisDate();
	    if (analysis_date == null)
		return "";
	    
	    String analysis_date_str = CUBean.getUserDateString(analysis_date);
	    return analysis_date_str;
	}
	
	public BigDecimal
	getCa()
	{
	    return ratio.getCa();
	}
	
	public BigDecimal
	getK()
	{
	    return ratio.getK();
	}
	
	public BigDecimal
	getCu()
	{
	    return ratio.getCu();
	}
	
	public BigDecimal
	getHg()
	{
	    return ratio.getHg();
	}
	
	public BigDecimal
	getCaMg()
	{
	    return ratio.getCaMg();
	}
	
	public BigDecimal
	getCaK()
	{
	    return ratio.getCaK();
	}
	
	public BigDecimal
	getNaMg()
	{
	    return ratio.getNaMg();
	}
	
	public BigDecimal
	getNaK()
	{
	    return ratio.getNaK();
	}
	
	public BigDecimal
	getZnCu()
	{
	    return ratio.getZnCu();
	}
	
	public BigDecimal
	getCaP()
	{
	    return ratio.getCaP();
	}
	
	public String
	getCaString()
	{
	    BigDecimal value = ratio.getCa();
	    if (value == null)
		return "";
	    return value.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getKString()
	{
	    BigDecimal value = ratio.getK();
	    if (value == null)
		return "";
	    return value.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getCuString()
	{
	    BigDecimal value = ratio.getCu();
	    if (value == null)
		return "";
	    return value.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getHgString()
	{
	    BigDecimal value = ratio.getHg();
	    if (value == null)
		return "";
	    return value.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getCaMgString()
	{
	    BigDecimal value = ratio.getCaMg();
	    if (value == null)
		return "";
	    return value.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getCaKString()
	{
	    BigDecimal value = ratio.getCaK();
	    if (value == null)
		return "";
	    return value.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getNaMgString()
	{
	    BigDecimal value = ratio.getNaMg();
	    if (value == null)
		return "";
	    return value.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getNaKString()
	{
	    BigDecimal value = ratio.getNaK();
	    if (value == null)
		return "";
	    return value.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getZnCuString()
	{
	    BigDecimal value = ratio.getZnCu();
	    if (value == null)
		return "";
	    return value.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getCaPString()
	{
	    BigDecimal value = ratio.getCaP();
	    if (value == null)
		return "";
	    return value.setScale(3, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getOxidationType()
	{
	    return ratio.getOxidationType();
	}
	
	public void
	getNotes(String _notes)
	{
	    ratio.setNotes(_notes);
	}
	
	protected void
	insertObject()
		throws com.badiyan.uk.exceptions.ObjectAlreadyExistsException, com.badiyan.uk.exceptions.IllegalValueException, Exception
	{
	    if (ratio.getCreatePersonId() < 1)
		    throw new IllegalValueException("Create person not set for mineral ratio");

	    ratio.setCreationDate(new Date());
	    ratio.save();

	    Integer key = new Integer(ratio.getMineralRatiosId());
	    MineralRatioBean.ratios.put(key, this);
	}
	
	public void
	setAnalysisDate(Date _date)
	{
	    ratio.setAnalysisDate(_date);
	}
	
	public void
	setCa(String _val)
	    throws NumberFormatException
	{
	    BigDecimal value = new BigDecimal(_val);
	    value.setScale(3, BigDecimal.ROUND_HALF_UP);
	    ratio.setCa(value);
	}
	
	public void
	setK(String _val)
	    throws NumberFormatException
	{
	    BigDecimal value = new BigDecimal(_val);
	    value.setScale(3, BigDecimal.ROUND_HALF_UP);
	    ratio.setK(value);
	}
	
	public void
	setCu(String _val)
	    throws NumberFormatException
	{
	    BigDecimal value = new BigDecimal(_val);
	    value.setScale(3, BigDecimal.ROUND_HALF_UP);
	    ratio.setCu(value);
	}
	
	public void
	setHg(String _val)
	    throws NumberFormatException
	{
	    BigDecimal value = new BigDecimal(_val);
	    value.setScale(3, BigDecimal.ROUND_HALF_UP);
	    ratio.setHg(value);
	}
	
	public void
	setCaMg(String _val)
	    throws NumberFormatException
	{
	    BigDecimal value = new BigDecimal(_val);
	    value.setScale(3, BigDecimal.ROUND_HALF_UP);
	    ratio.setCaMg(value);
	}
	
	public void
	setCaK(String _val)
	    throws NumberFormatException
	{
	    BigDecimal value = new BigDecimal(_val);
	    value.setScale(3, BigDecimal.ROUND_HALF_UP);
	    ratio.setCaK(value);
	}
	
	public void
	setNaMg(String _val)
	    throws NumberFormatException
	{
	    BigDecimal value = new BigDecimal(_val);
	    value.setScale(3, BigDecimal.ROUND_HALF_UP);
	    ratio.setNaMg(value);
	}
	
	public void
	setNaK(String _val)
	    throws NumberFormatException
	{
	    BigDecimal value = new BigDecimal(_val);
	    value.setScale(3, BigDecimal.ROUND_HALF_UP);
	    ratio.setNaK(value);
	}
	
	public void
	setZnCu(String _val)
	    throws NumberFormatException
	{
	    BigDecimal value = new BigDecimal(_val);
	    value.setScale(3, BigDecimal.ROUND_HALF_UP);
	    ratio.setZnCu(value);
	}
	
	public void
	setCaP(String _val)
	    throws NumberFormatException
	{
	    BigDecimal value = new BigDecimal(_val);
	    value.setScale(3, BigDecimal.ROUND_HALF_UP);
	    ratio.setCaP(value);
	}
	
	public void
	setOxidationType(String _oxidationType)
	{
	    ratio.setOxidationType(_oxidationType);
	}
	
	public void
	setNotes(String _notes)
	{
	    ratio.setNotes(_notes);
	}
	
	public void
	setCreateOrModifyPerson(PersonBean _person)
		throws TorqueException
	{
		if (isNew)
			ratio.setCreatePersonId(_person.getId());
		else
			ratio.setModifyPersonId(_person.getId());
	}
	
	protected void
	updateObject()
		throws com.badiyan.uk.exceptions.ObjectAlreadyExistsException, com.badiyan.uk.exceptions.IllegalValueException, Exception
	{
		if (ratio.getModifyPersonId() < 1)
			throw new IllegalValueException("Modify person not set for mineral ratio");
		
		ratio.setModificationDate(new Date());
		ratio.save();
	}
	
}