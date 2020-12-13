/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import com.valeo.qbpos.data.TenderRet;
import com.valonyx.util.KAADeviceState;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import org.kaaproject.kaa.common.dto.EndpointProfileDto;


/**
 *
 * @author marlo
 */
public class
Endpoint
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Long,Endpoint> hash = new HashMap<Long,Endpoint>();

    // CLASS METHODS

    public static Endpoint
    getEndpoint(long _id)
		throws TorqueException, ObjectNotFoundException {
		
		Endpoint endpoint_obj = (Endpoint)hash.get(_id);
		if (endpoint_obj == null) {
			Criteria crit = new Criteria();
			crit.add(EndpointDbPeer.ENDPOINT_DB_ID, _id);
			List objList = EndpointDbPeer.doSelect(crit);
			if (objList.isEmpty()) {
				throw new ObjectNotFoundException("Could not locate endpoint with id: " + _id);
			}

			endpoint_obj = Endpoint.getEndpoint((EndpointDb)objList.get(0));
		}

		return endpoint_obj;
    }

    private static Endpoint
    getEndpoint(EndpointDb _obj) throws TorqueException {
		
		Endpoint endpoint_obj = (Endpoint)hash.get(_obj.getEndpointDbId());
		if (endpoint_obj == null) {
			endpoint_obj = new Endpoint(_obj);
			hash.put(_obj.getEndpointDbId(), endpoint_obj);
		}

		return endpoint_obj;
    }

    public static Endpoint
    getEndpointBySerialNumber(String _sn)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Criteria crit = new Criteria();
		crit.add(EndpointDbPeer.UNIQUE_ID, _sn);
		List objList = EndpointDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			throw new ObjectNotFoundException("Could not locate endpoint with Serial #: " + _sn);
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unqiue endpoint with Serial #: " + _sn);
		}

		return Endpoint.getEndpoint((EndpointDb)objList.get(0));
    }

    public static Endpoint
    getEndpointByProfileID(String _profile_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		// to-do: add caching and/or database index for this
		// to-do: perhaps take a company as an argument
		
		Criteria crit = new Criteria();
		crit.add(EndpointDbPeer.PROFILE_ID, _profile_id);
		List objList = EndpointDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			throw new ObjectNotFoundException("Could not locate endpoint with Profile ID: " + _profile_id);
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unqiue endpoint with Profile ID: " + _profile_id);
		}

		return Endpoint.getEndpoint((EndpointDb)objList.get(0));
    }

    public static Endpoint
    getEndpointByEndpointHash(String _endpoint_hash)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		Criteria crit = new Criteria();
		crit.add(EndpointDbPeer.ENDPOINT_HASH, _endpoint_hash);
		List objList = EndpointDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			throw new ObjectNotFoundException("Could not locate endpoint for hash: " + _endpoint_hash);
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unqiue endpoint for hash: " + _endpoint_hash);
		}

		return Endpoint.getEndpoint((EndpointDb)objList.get(0));
    }

	public static Vector
	getEndpoints(String _keyword, int _limit, short _device_state, CompanyBean _mfg_sel)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {

		Criteria crit = new Criteria();
		
		if ( (_keyword != null && !_keyword.isEmpty()) || (_mfg_sel != null) ) {
			crit.addJoin(ProductDbPeer.PRODUCT_DB_ID, ProductInteractionDbPeer.PRODUCT_DB_ID);
			crit.addJoin(ProductInteractionDbPeer.UNIQUE_ID, EndpointDbPeer.UNIQUE_ID);
		}

		if (_keyword != null && !_keyword.isEmpty()) {
			
			String search_string = "%" + _keyword + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(EndpointDbPeer.UNIQUE_ID, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(ProductDbPeer.NAME, (Object)search_string, Criteria.LIKE);
			
			/* these searches don't really have any value
			Criteria.Criterion crit_b = crit.getNewCriterion(EndpointDbPeer.PROFILE_ID, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(EndpointDbPeer.APPLICATION_ID, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_d = crit.getNewCriterion(EndpointDbPeer.SDK_TOKEN, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b).or(crit_c).or(crit_d));
			*/
			crit.add(crit_a.or(crit_b));
		}
		
		if (_mfg_sel != null) {
			crit.add(ProductDbPeer.MFG_ID, _mfg_sel.getId());
		}
		
		if (_device_state == (short)1) {
			crit.add(EndpointDbPeer.DEVICE_STATE, (short)1);
		} else if (_device_state == (short)2) {
			crit.add(EndpointDbPeer.DEVICE_STATE, (short)0);
		}
		
		crit.addDescendingOrderByColumn(EndpointDbPeer.REGISTRATION_DATE);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		crit.setDistinct();

		Iterator itr = EndpointDbPeer.doSelect(crit).iterator();
		Vector vec = new Vector();
		while (itr.hasNext()) {
			vec.addElement(Endpoint.getEndpoint((EndpointDb)itr.next()));
		}
		return vec;
	}

	
    public static Endpoint
    maintainEndpoint(EndpointProfileDto _endpointProfile, String _profile_id)
		throws TorqueException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		
		String endpointKeyHash = Base64.getEncoder().encodeToString(_endpointProfile.getEndpointKeyHash());
		
		Criteria crit = new Criteria();
		crit.add(EndpointDbPeer.ENDPOINT_HASH, endpointKeyHash);
		List objList = EndpointDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			
			boolean isActive = KAADeviceState.parseJsonString(_endpointProfile.getServerProfileBody());
			boolean isError = KAADeviceState.isError(_endpointProfile.getServerProfileBody());
			
			Endpoint obj = new Endpoint();
			obj.setActive(isActive);
			obj.setApplicationID(_endpointProfile.getApplicationId());
			obj.setEndpointHash(endpointKeyHash);
			obj.setProfileID(_profile_id);
			obj.setRegistrationDate(new Date()); // not the best..  what else can I do?  perhaps logging to Mongo or something...
			obj.setSDKToken(_endpointProfile.getSdkToken());
			//obj.setSerialNumber(); // need some magic sauce
			if (isError) {
				obj.setErrorMessage("Unable to activate");
			} else {
				obj.setErrorMessage("");
			}
			
			obj.save();
			
			return obj;
			
		} else if (objList.size() == 1) {
			// update if necessary
			
			boolean isActive = KAADeviceState.parseJsonString(_endpointProfile.getServerProfileBody()); // value read from KAA endpoint
			Endpoint obj = Endpoint.getEndpoint((EndpointDb)objList.get(0));
			if (obj.isActivated() != isActive) {
				obj.setActive(isActive);
				obj.save();
			}
			
		} else if (objList.size() > 1) {
			throw new UniqueObjectNotFoundException("Could not locate unqiue endpoint for hash: " + endpointKeyHash);
		}

		return Endpoint.getEndpoint((EndpointDb)objList.get(0));
		
    }
	

    // SQL

    /*
<table name="ENDPOINT_DB" idMethod="native">
	<column name="ENDPOINT_DB_ID" required="true" primaryKey="true" type="BIGINT" autoIncrement="true"/>
	
	<column name="UNIQUE_ID" required="false" size="20" type="VARCHAR"/>
	<!-- serial number bar code typically - same as unique id  -->
	<column name="PROFILE_ID" required="true" size="30" type="VARCHAR"/>
	<column name="ENDPOINT_HASH" required="true" size="40" type="VARCHAR"/>
	
	<column name="DEVICE_STATE" type="SMALLINT" default="0"/>
	<column name="APPLICATION_ID" required="true" size="60" type="VARCHAR"/>
	<column name="SDK_TOKEN" required="true" size="60" type="VARCHAR"/>
	
	<column name="REGISTRATION_DATE" required="true" type="DATE"/>

</table>


     */

    // INSTANCE VARIABLES

    private EndpointDb endpoint;

    // CONSTRUCTORS

    public
    Endpoint() {
		endpoint = new EndpointDb();
		isNew = true;
    }

    public
    Endpoint(EndpointDb _obj) {
		endpoint = _obj;
		isNew = false;
    }

    // INSTANCE METHODS

    public long
    getId() {
		return endpoint.getEndpointDbId();
    }

	public String getNameString() {
		String str = endpoint.getProfileId();
		if (str == null) {
			return "";
		}
		return str;
	}

	public String getProfileID() {
		String str = endpoint.getProfileId();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setProfileID(String _str) {
		endpoint.setProfileId(_str);
	}

	public String getEndpointHash() {
		String str = endpoint.getEndpointHash();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setEndpointHash(String _str) {
		endpoint.setEndpointHash(_str);
	}

	public String getApplicationID() {
		String str = endpoint.getApplicationId();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setApplicationID(String _str) {
		endpoint.setApplicationId(_str);
	}

	public String getSDKToken() {
		String str = endpoint.getSdkToken();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setSDKToken(String _str) {
		endpoint.setSdkToken(_str);
	}

	public String getSerialNumber() {
		String str = endpoint.getUniqueId();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setSerialNumber(String _str) {
		endpoint.setUniqueId(_str);
	}

	public Date getRegistrationDate() {
		return endpoint.getRegistrationDate();
	}

	public void setRegistrationDate(Date _date) {
		endpoint.setRegistrationDate(_date);
	}
    
    public long getRegistrationDateUnixTimestamp() {
		return this.endpoint.getRegistrationDate().getTime() / 1000;
    }

	public String getErrorMessage() {
		String str = endpoint.getErrorMessage();
		if (str == null) {
			return "";
		}
		return str;
	}

	public void setErrorMessage(String _str) {
		endpoint.setErrorMessage(_str);
	}
	

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return this.getNameString();
    }

    public String
    getValue() {
		return this.getId() + "";
    }

    protected void
    insertObject()
		throws Exception {
		endpoint.save();
    }
	
	public boolean
	isActivated() {
		return (endpoint.getDeviceState() == (short)1);
	}

	public void
	activate()
		throws TorqueException {
		endpoint.setDeviceState((short)1);
	}

	public void
	inActivate()
		throws TorqueException {
		endpoint.setDeviceState((short)0);
	}

	public void
	setActive(boolean _b)
		throws TorqueException {
		endpoint.setDeviceState(_b ? (short)1 : (short)0);
	}
	
	public void setDeviceState(short _state) {		
		endpoint.setDeviceState(_state);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception {
		endpoint.save();
    }
	
	
	

	
	
	
}