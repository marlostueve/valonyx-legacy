
package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
ClientMap
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,ClientMap> hash = new HashMap<Integer,ClientMap>();
	
	public static final int STRING_MAP_INITIAL_CAPACITY = 40;
	public static final int SHORT_MAP_INITIAL_CAPACITY = 120;
	
	public static final String SPIRITUAL_LIES_1_40 = "bool_SPIRITUAL_LIES_1_40";
	public static final String SPIRITUAL_TRUTH_1_22 = "bool_SPIRITUAL_TRUTH_1_22";

    // CLASS METHODS

    public static void
    delete(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		hash.remove(_id);

		Criteria crit = new Criteria();
		crit.add(ClientMapDbPeer.CLIENT_MAP_DB_ID, _id);
		ClientMapDbPeer.doDelete(crit);
    }

    public static ClientMap
    getClientMap(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		ClientMap map_obj = (ClientMap)hash.get(_id);
		if (map_obj == null) {
			Criteria crit = new Criteria();
			crit.add(ClientMapDbPeer.CLIENT_MAP_DB_ID, _id);
			List objList = ClientMapDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate map with id: " + _id);
			}

			map_obj = ClientMap.getClientMap((ClientMapDb)objList.get(0));
		}

		return map_obj;
    }

    public static ClientMap
    getClientMap(ClientMapDb _obj) throws TorqueException {
		
		ClientMap map_obj = (ClientMap)hash.get(_obj.getClientMapDbId());
		if (map_obj == null) {
			map_obj = new ClientMap(_obj);
			hash.put(_obj.getClientMapDbId(), map_obj);
		}

		return map_obj;
    }

	/*
    public static Vector
    getEntries(ClientMap _map)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(ClientMapDbPeer.CLIENT_MAP_DB_ID, _goal);
		crit.addAscendingOrderByColumn(ClientMapDbPeer.ENTRY_DATE);
		Iterator itr = ClientMapDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(ClientMap.getClientMap((ClientMapDb)itr.next()));
		}
		
		return vec;
    }

    public static ClientMap
    getLastEntry(PersonBean _person)
		throws TorqueException, ObjectNotFoundException {
		
		Criteria crit = new Criteria();
		crit.addJoin(ClientMapDbPeer.CLIENT_MAP_DB_ID, ClientMapDbPeer.CLIENT_MAP_DB_ID);
		crit.add(ClientMapDbPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(ClientMapDbPeer.ENTRY_DATE);
		List l = ClientMapDbPeer.doSelect(crit);
		if (!l.isEmpty()) {
			return ClientMap.getClientMap((ClientMapDb)l.get(0));
		} else {
			throw new ObjectNotFoundException("Last weight map not found for " + _person.getLabel());
		}
    }
	*/
	

    // SQL

    /*

<table name="CLIENT_MAP_DB" idMethod="native">
    <column name="CLIENT_MAP_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	<column name="CLIENT_ID" required="true" type="INTEGER"/>
	<column name="PRACTITIONER_ID" required="true" type="INTEGER"/>
	
    <column name="DESCRIPTION" required="true" type="VARCHAR" size="250"/>
	<column name="DIAGNOSIS_CODE_VERSION_DB_ID" required="true" type="INTEGER"/>
	
    <column name="IS_ACTIVE" type="SMALLINT" default="1"/>
	
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
    
    <foreign-key foreignTable="PERSON">
		<reference local="CLIENT_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>

</table>

<table name="CLIENT_MAP_DATA_SMALLINT">
	<column name="CLIENT_MAP_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="DATA_KEY" primaryKey="true" required="true" type="VARCHAR" size="50"/>
    <column name="DATA_VALUE" primaryKey="true" type="SMALLINT" />

    <foreign-key foreignTable="CLIENT_MAP_DB">
		<reference local="CLIENT_MAP_DB_ID" foreign="CLIENT_MAP_DB_ID"/>
    </foreign-key>
</table>

<table name="CLIENT_MAP_DATA_STRING">
	<column name="CLIENT_MAP_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="DATA_KEY" primaryKey="true" required="true" type="VARCHAR" size="50"/>
    <column name="DATA_VALUE" required="true" type="VARCHAR" size="250"/>

    <foreign-key foreignTable="CLIENT_MAP_DB">
		<reference local="CLIENT_MAP_DB_ID" foreign="CLIENT_MAP_DB_ID"/>
    </foreign-key>
</table>

<table name="CLIENT_MAP_DATA_TEXT">
	<column name="CLIENT_MAP_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="DATA_KEY" primaryKey="true" required="true" type="VARCHAR" size="50"/>
    <column name="DATA_VALUE" required="true" type="LONGVARCHAR"/>

    <foreign-key foreignTable="CLIENT_MAP_DB">
		<reference local="CLIENT_MAP_DB_ID" foreign="CLIENT_MAP_DB_ID"/>
    </foreign-key>
</table>

     */

    // INSTANCE VARIABLES

    private ClientMapDb map;
	
	private ConcurrentHashMap<String,String> string_values = null;
	private ConcurrentHashMap<String,Short> short_values = null;

    // CONSTRUCTORS

    public
    ClientMap() {
		map = new ClientMapDb();
		isNew = true;
    }

    public
    ClientMap(ClientMapDb _obj) {
		map = _obj;
		isNew = false;
    }

    // INSTANCE METHODS
	
	private void
	loadMapValues() throws TorqueException {
		
		if (string_values == null) {
			string_values = new ConcurrentHashMap(ClientMap.STRING_MAP_INITIAL_CAPACITY);
			short_values = new ConcurrentHashMap(ClientMap.SHORT_MAP_INITIAL_CAPACITY);
			
			Criteria crit = new Criteria();
			crit.add(ClientMapDataStringPeer.CLIENT_MAP_DB_ID, this.getId());
			Iterator itr = ClientMapDataStringPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				ClientMapDataString obj = (ClientMapDataString)itr.next();
				string_values.put(obj.getDataKey(), obj.getDataValue());
			}

			crit = new Criteria();
			crit.add(ClientMapDataTextPeer.CLIENT_MAP_DB_ID, this.getId());
			itr = ClientMapDataTextPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				ClientMapDataString obj = (ClientMapDataString)itr.next();
				string_values.put(obj.getDataKey(), obj.getDataValue());
			}

			crit = new Criteria();
			crit.add(ClientMapDataSmallintPeer.CLIENT_MAP_DB_ID, this.getId());
			itr = ClientMapDataSmallintPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				ClientMapDataSmallint obj = (ClientMapDataSmallint)itr.next();
				short_values.put(obj.getDataKey(), obj.getDataValue());
			}
		}
		
	}
	
	public String
	getMapValueString(String _key) throws TorqueException {
		
		this.loadMapValues();
		String str = string_values.get(_key);
		if (str == null) {
			return "";
		}
		return str;
	}
	
	public Boolean
	getMapValueBoolean(String _key) throws TorqueException {
		
		this.loadMapValues();
		Short shortValue = short_values.get(_key);
		if (shortValue == null) {
			return false;
		}
		return ( shortValue == (short)1 );
	}
	
	public Short
	getMapValueShort(String _key) throws TorqueException {
		
		this.loadMapValues();
		Short shortValue = short_values.get(_key);
		if (shortValue == null) {
			return (short)0;
		}
		return shortValue;
	}
	
	public String
	getMapValueShortString(String _key) throws TorqueException {
		
		this.loadMapValues();
		Short shortValue = short_values.get(_key);
		if (shortValue == null) {
			return "";
		}
		return shortValue + "";
	}
	
	public Date
	getMapDate() {
		return map.getCreationDate();
	}
	
	public String
	getMapDateString() {
		if (map.getCreationDate() == null) {
			return "[MAP DATE NOT FOUND]";
		}
		return CUBean.getUserDateString(map.getCreationDate());
	}
	
	public void
	setMapDate(Date _dt) {
		map.setCreationDate(_dt);
	}

    public long
    getId() {
		return map.getClientMapDbId();
    }
	
	public UKOnlinePersonBean
	getClient() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(map.getClientId());
	}
	
	public UKOnlinePersonBean
	getPractitioner() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(map.getPractitionerId());
	}

    public String
    getLabel() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		UKOnlinePersonBean client = this.getClient();
		if (client == null) {
			return "[CLIENT NOT FOUND FOR MAP]";
		}
		return client.getLabel() + " " + this.getMapDateString() + " Map";
    }
    
    public long getEntryDateUnixTimestamp() {
		return this.map.getCreationDate().getTime() / 1000;
    }

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		map.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		map.save();
    }
	
}
