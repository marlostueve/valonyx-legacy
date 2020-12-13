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
BioterrainMarker
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,BioterrainMarker> hash = new HashMap<Integer,BioterrainMarker>(11);

    // CLASS METHODS

    public static BioterrainMarker
    getMarker(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		BioterrainMarker marker_obj = (BioterrainMarker)hash.get(_id);
		if (marker_obj == null) {
			Criteria crit = new Criteria();
			crit.add(BioterrainMarkerDbPeer.BIOTERRAIN_MARKER_DB_ID, _id);
			List objList = BioterrainMarkerDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate bioterrain marker with id: " + _id);
			}

			marker_obj = BioterrainMarker.getMarker((BioterrainMarkerDb)objList.get(0));
		}

		return marker_obj;
    }

    private static BioterrainMarker
    getMarker(BioterrainMarkerDb _obj) throws TorqueException {
		
		BioterrainMarker marker_obj = (BioterrainMarker)hash.get(_obj.getBioterrainMarkerDbId());
		if (marker_obj == null) {
			marker_obj = new BioterrainMarker(_obj);
			hash.put(_obj.getBioterrainMarkerDbId(), marker_obj);
		}

		return marker_obj;
    }

    public static Vector
    getMarkers(PersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(BioterrainMarkerDbPeer.DATA_PERSON_ID, _person.getId());
		Iterator itr = BioterrainMarkerDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			BioterrainMarkerDb obj = (BioterrainMarkerDb)itr.next();
			vec.addElement(BioterrainMarker.getMarker(obj));
		}
		return vec;
    }

    // SQL

    /*
<table name="BIOTERRAIN_MARKER_DB" idMethod="native">
    <column name="BIOTERRAIN_MARKER_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="DATA_KEY" required="true" size="50" type="VARCHAR"/>
    <column name="DATA_VALUE_STR" required="true" size="20" type="VARCHAR"/>
	<column name="DATA_VALUE_NUM" required="true" scale="2" size="7" type="DECIMAL"/>
	<column name="DATA_DATE" required="true" type="DATE"/>
	
	<column name="DATA_PERSON_ID" required="false" type="INTEGER"/>
	
	<column name="CREATION_DATE" required="true" type="DATE"/>
	<column name="MODIFICATION_DATE" required="true" type="DATE"/>
	<column name="CREATE_PERSON_ID" required="true" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
		<reference local="DATA_PERSON_ID" foreign="PERSONID"/>
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

    private BioterrainMarkerDb marker;
	private PersonBean createOrModifyPerson;
	//private Vector changeLog;
	//private Vector interactions;

    // CONSTRUCTORS

    public
    BioterrainMarker() {
		marker = new BioterrainMarkerDb();
		isNew = true;
    }

    public
    BioterrainMarker(BioterrainMarkerDb _obj) {
		marker = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	

	public String getKey() {
		String str = marker.getDataKey();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setKey(String _key) {
		marker.setDataKey(_key);
	}

	public String getValueString() {
		String str = marker.getDataValueStr();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setValueString(String _value) {
		marker.setDataValueStr(_value);
	}

	public BigDecimal getValueNumeric() {
		return marker.getDataValueNum();
	}

	public void setValueNumeric(BigDecimal _value) {
		marker.setDataValueNum(_value);
	}

	public Date getDataDate() {
		return marker.getDataDate();
	}

	public void setDataDate(Date dataDate) {
		marker.setDataDate(dataDate);
	}

	public PersonBean getDataPerson() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return UKOnlinePersonBean.getPerson(marker.getDataPersonId());
	}

	public void setPerson(PersonBean _person) throws TorqueException {
		marker.setDataPersonId(_person.getId());
	}

	public Date getModificationDate() {
		return marker.getModificationDate();
	}

	public void setCreateOrModifyPerson(PersonBean _createOrModifyPerson) {
		this.createOrModifyPerson = _createOrModifyPerson;
	}
	
	


    public long
    getId() {
		return marker.getBioterrainMarkerDbId();
    }

    public String
    getLabel() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		String str = this.getDataPerson().getLabel() + ": key >" + this.getKey() + ", value >" + this.getValueString();
		if (str == null) {
			return "";
		}
		return str;
    }

    public String
    getValue() {
		return marker.getBioterrainMarkerDbId() + "";
    }

    protected void
    insertObject() throws Exception {
		marker.setCreationDate(new Date());
		marker.save();
    }

    protected void
    updateObject() throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		marker.setModificationDate(new Date());
		marker.save();
    }

	
}