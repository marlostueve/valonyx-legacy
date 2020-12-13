/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.beans;

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
PersonSettingsBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,PersonSettingsBean> hash = new HashMap<Integer,PersonSettingsBean>(11);

    // CLASS METHODS
	
	
	public static void
	delete(PersonSettingsBean obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		Integer key = new Integer(obj.getPerson().getId());
		hash.remove(key);
		PersonSettingsPeer.doDelete(obj.person_settings);
	}
	
	public static boolean
	hasSettings(UKOnlinePersonBean _person) throws TorqueException {
		Integer key = new Integer(_person.getId());
		PersonSettingsBean person_settings = (PersonSettingsBean)hash.get(key);
		if (person_settings == null) {
			Criteria crit = new Criteria();
			crit.add(PersonSettingsPeer.PERSON_ID, key);
			List objList = PersonSettingsPeer.doSelect(crit);
			return !objList.isEmpty();
		}

		return true;
	}

    public static PersonSettingsBean
    getPersonSettings(UKOnlinePersonBean _person, boolean _create)
		throws TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		Integer key = new Integer(_person.getId());
		PersonSettingsBean person_settings = (PersonSettingsBean)hash.get(key);
		if (person_settings == null) {
			Criteria crit = new Criteria();
			crit.add(PersonSettingsPeer.PERSON_ID, key);
			List objList = PersonSettingsPeer.doSelect(crit);
			if (objList.isEmpty()) {
				if (_create) {
					person_settings = new PersonSettingsBean();
					person_settings.setPerson(_person);
					person_settings.save();
				} else {
					throw new ObjectNotFoundException("Could not locate Teacher Settings for: " + _person.getLabel());
				}
			} else {
				person_settings = PersonSettingsBean.getPersonSettings((PersonSettings)objList.get(0));
			}
		}

		return person_settings;
    }

    private static PersonSettingsBean
    getPersonSettings(PersonSettings _person_settings)
		throws TorqueException
    {
		Integer key = new Integer(_person_settings.getPersonId());
		PersonSettingsBean person_settings = (PersonSettingsBean)hash.get(key);
		if (person_settings == null)
		{
			person_settings = new PersonSettingsBean(_person_settings);
			hash.put(key, person_settings);
		}

		return person_settings;
    }

    // SQL

    /*
		<table name="TEACHER_SETTINGS">

			<column name="PERSON_ID" required="true" primaryKey="true" type="INTEGER"/>
			<column name="YEAR_OF_SERVICE_ID" required="true" type="INTEGER" />
			<column name="OCC_CODE_ID" required="false" type="INTEGER"/>
			
			<column name="FTE" required="false" scale="2" size="4" type="DECIMAL"/>
			<column name="PAS_STATUS" size="50" type="VARCHAR"/>
			<column name="IS_TENURED" type="SMALLINT" default="0"/>
			<column name="POC_TRAINED" type="SMALLINT" default="0"/>
			<column name="POC_INTERESTED" type="SMALLINT" default="1"/>
			
			<column name="GRADE_SUBJECT" size="50" type="VARCHAR"/>
			<column name="LAST_EVALUATOR" required="true" type="INTEGER"/>
			<column name="LAST_PEER_EVALUATOR" required="true" type="INTEGER"/>
			
			<column name="SELECTED_COMPONENT_1_ID" required="false" type="INTEGER"/>
			<column name="SELECTED_COMPONENT_2_ID" required="false" type="INTEGER"/>
			<column name="SELECTED_COMPONENT_3_ID" required="false" type="INTEGER"/>
			<column name="SELECTED_COMPONENT_4_ID" required="false" type="INTEGER"/>
			<column name="SELECTED_COMPONENT_5_ID" required="false" type="INTEGER"/>
			
			<column name="Q_COMP_F_T_E" required="true" scale="4" size="5" type="DECIMAL"/>
			<column name="ELIGIBLE" type="SMALLINT" default="1"/>
			
			<column name="EMP_DT" required="false" type="DATE"/>
			<column name="NEXT_EVAL_DT" required="false" type="DATE"/>
			
		</table>
     */

    // INSTANCE VARIABLES

    private PersonSettings person_settings;
	private Vector pie_chart_map;

    // CONSTRUCTORS

    public
    PersonSettingsBean()
    {
		person_settings = new PersonSettings();
		isNew = true;
    }

    public
    PersonSettingsBean(PersonSettings _person_settings)
    {
		person_settings = _person_settings;
		isNew = false;
    }

	// INSTANCE METHODS
	
	public Date getEmpDt() {
		return person_settings.getEmpDt();
	}

	public void setEmpDt(Date empDt) {
		person_settings.setEmpDt(empDt);
	}

	public Date getNextEvalDt() {
		return person_settings.getNextEvalDt();
	}

	public void setNextEvalDt(Date nextEvalDt) {
		person_settings.setNextEvalDt(nextEvalDt);
	}

	public BigDecimal getFTE() {
		return person_settings.getFte();
	}

	public void setFTE(BigDecimal _fte) {
		person_settings.setFte(_fte);
	}

	public BigDecimal getNSIPMCoveragePercentage() {
		return person_settings.getFte();
	}

	public void setNSIPMCoveragePercentage(BigDecimal _bd) {
		person_settings.setFte(_bd);
	}
	
	public void
	setPerson(UKOnlinePersonBean _person) throws TorqueException {
		person_settings.setPersonId(_person.getId());
	}
	
	public UKOnlinePersonBean
	getPerson() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person_settings.getPersonId());
	}
	
	public boolean isNSIPMClient() {
		return person_settings.getIsTenured() == 1;
	}

	public void setIsNSIPMClient(boolean isNSIPMClient) {
		this.person_settings.setIsTenured(isNSIPMClient ? (short)1 : (short)0);
	}

    protected void
    insertObject()
		throws Exception
    {
		person_settings.save();
		savePieChartMap();
		
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		//Criteria crit = new Criteria();
		//PersonSettingsPeer.doUpdate(crit);
		
		System.out.println("&&&SAVE >" + this.getPerson().getLabel());
		
		//PersonSettingsPeer.
		
		person_settings.save();
		
		savePieChartMap();
		
		System.out.println("&&&SAVEDONE");
    }
	
	public void
	setGradeSubject(String _str) {
		person_settings.setGradeSubject(_str);
	}
	
	/*
		<table name="ADMIN_COURSE_PIE_CHART_MAP">
			<column name="ADMIN_ID" required="true" primaryKey="true" type="INTEGER" />
			<column name="COURSE_ID" required="true" primaryKey="true" type="INTEGER" />

			<foreign-key foreignTable="PERSON">
				<reference local="ADMIN_ID" foreign="PERSONID"/>
			</foreign-key>
			<foreign-key foreignTable="COURSE">
				<reference local="COURSE_ID" foreign="COURSEID"/>
			</foreign-key>
		</table>
	 */
	
	
	public void
	setPieChartMap(Vector _v) {
		pie_chart_map = _v;
	}
	
	public synchronized Vector
	getPieChartMap()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		if (pie_chart_map == null) {
			pie_chart_map = new Vector();

			Criteria crit = new Criteria();
			crit.add(AdminProductPieChartMapPeer.ADMIN_ID, this.getPerson().getId());
			Iterator itr = AdminProductPieChartMapPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				AdminProductPieChartMap obj = (AdminProductPieChartMap)itr.next();
				pie_chart_map.addElement(obj);
			}
		}
		return pie_chart_map;
	}
	
	private synchronized void
    savePieChartMap()
		throws TorqueException, Exception {

		if (this.pie_chart_map != null) {
			
			System.out.println("savePieChartMap sizer >" + this.pie_chart_map.size());

			HashMap db_set_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(AdminProductPieChartMapPeer.ADMIN_ID, this.getPerson().getId());
			Iterator itr = AdminProductPieChartMapPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				System.out.println("found existing ass...");
				AdminProductPieChartMap value = (AdminProductPieChartMap)itr.next();
				Long key = new Long(value.getProductDbId());
				db_set_hash.put(key, value);
			}

			itr = this.pie_chart_map.iterator();
			while (itr.hasNext()) {
				AdminProductPieChartMap pie_chart_map = (AdminProductPieChartMap)itr.next();
				//Integer key = new Integer(seq.getObservationFormId());
				Long key = new Long(pie_chart_map.getProductDbId());
				AdminProductPieChartMap existing_map = (AdminProductPieChartMap)db_set_hash.remove(key);
				
				if (existing_map == null) {
					// map doesn't already exist in db, go ahead and save (insert) it
					pie_chart_map.save();
				} else {
					// map already exists.  update it
					/*
					existing_map.setContentArea(pie_chart_map.getContentArea());
					existing_map.setGradeLevel(pie_chart_map.getGradeLevel());
					existing_map.setRequirementType(pie_chart_map.getRequirementType());
					existing_map.setProbationary(pie_chart_map.getProbationary());
					existing_map.setGroups(pie_chart_map.getGroups());
					existing_map.save();
					*/
				}
				
			}

			itr = db_set_hash.keySet().iterator();
			while (itr.hasNext()) {
				Long key = (Long)itr.next();
				AdminProductPieChartMap obj = (AdminProductPieChartMap)db_set_hash.get(key);
				System.out.println("del obj >" + obj);
				AdminProductPieChartMapPeer.doDelete(obj);
			}
			
		}
    }
	

}