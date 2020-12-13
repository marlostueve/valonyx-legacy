
package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author  marlo
 * @version 
 */
public class
TreatmentRoomBean
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES
	
	private static HashMap rooms = new HashMap(19);
	
	/*
	private static Vector types = null;
	private static Vector status = null;
	 */
	
	/*
	public static final String DEFAULT_STATUS = "OK";
	public static final String CLOSED_STATUS = "Closed";
	
	public static final String DEFAULT_TYPE = "TreatmentRoom";
	public static final String AUDITORIUM_TYPE = "Auditorium";
	 */
	
	// CLASS METHODS
	
	public static TreatmentRoomBean
	getTreatmentRoom(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		/*
		if (_id == 0)
			throw new ObjectNotFoundException("Could not locate room with id: " + _id);
		 */
		
		Integer key = new Integer(_id);
		TreatmentRoomBean room = (TreatmentRoomBean)rooms.get(key);
		if (room == null)
		{	
			Criteria crit = new Criteria();
			crit.add(RoomPeer.ROOMID, _id);
			List objList = RoomPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate room with id: " + _id);
		
			room = TreatmentRoomBean.getTreatmentRoom((Room)objList.get(0));
		}
		
		return room;
	}
	
	private static TreatmentRoomBean
	getTreatmentRoom(Room _room)
	{
		Integer key = new Integer(_room.getRoomid());
		TreatmentRoomBean room = (TreatmentRoomBean)rooms.get(key);
		if (room == null)
		{
			room = new TreatmentRoomBean(_room);
			rooms.put(key, room);
		}
		
		return room;
	}
	
	public static Vector
	getTreatmentRooms(BuildingBean _building)
		throws TorqueException
	{
		Vector rooms = new Vector();
				
		Criteria crit = new Criteria();
		crit.add(RoomPeer.BUILDINGID, _building.getId());
		crit.addAscendingOrderByColumn(RoomPeer.ROOMDESC);
		Iterator obj_itr = RoomPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			rooms.addElement(TreatmentRoomBean.getTreatmentRoom((Room)obj_itr.next()));
		
		return rooms;
	}
	
	public static Vector
	getTreatmentRooms(CompanyBean _company)
		throws TorqueException
	{
		Vector rooms = new Vector();
				
		Criteria crit = new Criteria();
		crit.addJoin(RoomPeer.BUILDINGID, BuildingPeer.BUILDINGID);
		crit.addJoin(BuildingPeer.LOCATION_ID, LocationPeer.LOCATION_ID);
		crit.add(LocationPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(RoomPeer.ROOMDESC);
		System.out.println("getTreatmentRooms() crit >" + crit.toString());
		Iterator obj_itr = RoomPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			rooms.addElement(TreatmentRoomBean.getTreatmentRoom((Room)obj_itr.next()));
		
		return rooms;
	}
	
	/*
	public static void
	maintainDefaultData()
		throws TorqueException, Exception
	{
		Criteria crit = new Criteria();
		crit.add(RoomStatusPeer.ROOMSTATUS, TreatmentRoomBean.ELECTIVE_REQ_TYPE);
		List objList = RoomStatusPeer.doSelect(crit);
		if (objList.size() == 0)
			RoomStatusPeer.doInsert(crit);
		
		
	}
	 */
	
	public static void
	maintainTreatmentRoom(CompanyBean _company, DepartmentBean _location, BuildingBean _building, String _room_name) throws TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		Criteria crit = new Criteria();
		crit.addJoin(RoomPeer.BUILDINGID, BuildingPeer.BUILDINGID);
		crit.addJoin(BuildingPeer.LOCATION_ID, LocationPeer.LOCATION_ID);
		crit.add(LocationPeer.COMPANY_ID, _company.getId());
		crit.add(LocationPeer.LOCATION_ID, _location.getId());
		crit.add(BuildingPeer.BUILDINGID, _building.getId());
		crit.add(RoomPeer.ROOMDESC, _room_name);
		List objList = RoomPeer.doSelect(crit);
		if (objList.isEmpty()) {
			
			ClassroomTypeBean type = ClassroomTypeBean.getType(1);
			ClassroomStatusBean status = ClassroomStatusBean.getStatus(1);
			
			TreatmentRoomBean treatment_room = new TreatmentRoomBean();
			treatment_room.setBuilding(_building);
			treatment_room.setName(_room_name);
			treatment_room.setStatus(status);
			treatment_room.setType(type);
			treatment_room.save();
		}
		
	}
	
	// SQL
	
	/*
<table name="ROOM" idMethod="native">
    <column name="ROOMID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true" />
    <column name="ROOMDESC" required="true" size="40" type="VARCHAR"/>
    <column name="BUILDINGID" required="true" type="INTEGER"/>
    <column name="ROOMTYPE_ID" required="true" type="INTEGER"/>
    <column name="ROOMSTATUS_ID" required="true" type="INTEGER"/>
    <column name="CAPACITY" type="SMALLINT"/>
    <column name="ROOMCOMMENT" type="LONGVARCHAR" javaName="comment"/>
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
    <column name="CALENDAR_COLOR" required="false" size="6" type="VARCHAR"/>

    <foreign-key foreignTable="BUILDING">
	<reference local="BUILDINGID" foreign="BUILDINGID"/>
    </foreign-key>
    <foreign-key foreignTable="ROOMTYPE">
	<reference local="ROOMTYPE_ID" foreign="ROOMTYPE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="ROOMSTATUS">
	<reference local="ROOMSTATUS_ID" foreign="ROOMSTATUS_ID"/>
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
	
	private Room room;
	
	// CONSTRUCTORS
	
	public
	TreatmentRoomBean()
	{
		room = new Room();
		isNew = true;
	}
	
	public
	TreatmentRoomBean(Room _room)
	{
		room = _room;
		isNew = false;
	}
	
	// INSTANCE METHODS
	
	public boolean
	equals(TreatmentRoomBean _room)
	{
		if (_room == null)
			return false;
		return (this.getId() == _room.getId());
	}
	
	public BuildingBean
	getBuilding()
		throws TorqueException, ObjectNotFoundException
	{
		return BuildingBean.getBuilding(room.getBuildingid());
	}
	
	public int
	getBuildingId()
	{
		return room.getBuildingid();
	}
	
	public short
	getCapacity()
	{
		return room.getCapacity();
	}
	
	public String
	getCapacityString()
	{
		short capacity = room.getCapacity();
		if (capacity == 0)
			return "";
		return capacity + "";
	}
	
	public String
	getCommentString()
	{
		String str = room.getComment();
		if (str == null)
			return "";
		return str;
	}
	
	public int
	getId()
	{
		return room.getRoomid();
	}
	
	public String
	getLabel()
	{
		String str = room.getRoomdesc();
		if (str == null)
			return "";
		return str;
	}
	
	public ClassroomStatusBean
	getStatus()
		throws TorqueException, ObjectNotFoundException
	{
		return ClassroomStatusBean.getStatus(room.getRoomstatusId());
	}
	
	public int
	getStatusId()
	{
		return room.getRoomstatusId();
	}
	
	public ClassroomTypeBean
	getType()
		throws TorqueException, ObjectNotFoundException
	{
		return ClassroomTypeBean.getType(room.getRoomtypeId());
	}
	
	public int
	getTypeId()
	{
		return room.getRoomtypeId();
	}
	
	public String
	getValue()
	{
		return room.getRoomid() + "";
	}
	
	protected void
	insertObject()
		throws com.badiyan.uk.exceptions.ObjectAlreadyExistsException, com.badiyan.uk.exceptions.IllegalValueException, Exception
	{
		if (room.getCreatePersonId() < 1)
			throw new IllegalValueException("Create person not set for TreatmentRoom");
		
		room.setCreationDate(new Date());
		room.save();
		
		Integer key = new Integer(room.getRoomid());
		TreatmentRoomBean.rooms.put(key, this);
	}
	
	public void
	setBuilding(BuildingBean _building)
		throws TorqueException
	{
		room.setBuildingid(_building.getId());
	}
	
	public void
	setCapacity(short _capacity)
	{
		room.setCapacity(_capacity);
	}
	
	public void
	setComment(String _comment)
	{
		room.setComment(_comment);
	}
	
	public void
	setCreateOrModifyPerson(PersonBean _person)
		throws TorqueException
	{
		if (isNew)
			room.setCreatePersonId(_person.getId());
		else
			room.setModifyPersonId(_person.getId());
	}
	
	public void
	setName(String _name)
	{
		room.setRoomdesc(_name);
	}
	
	public void
	setStatus(ClassroomStatusBean _status)
		throws TorqueException
	{
		room.setRoomstatusId(_status.getId());
	}
	
	public void
	setType(ClassroomTypeBean _type)
		throws TorqueException
	{
		room.setRoomtypeId(_type.getId());
	}
	
	protected void
	updateObject()
		throws com.badiyan.uk.exceptions.ObjectAlreadyExistsException, com.badiyan.uk.exceptions.IllegalValueException, Exception
	{
		if (room.getModifyPersonId() < 1)
			throw new IllegalValueException("Modify person not set for TreatmentRoom");
		
		room.setModificationDate(new Date());
		room.save();
	}
	
	public String
	getCalendarColor() {
		if (room.getCalendarColor() == null || room.getCalendarColor().equals("0")) {
			 return "4169E1";
		} else {
			return room.getCalendarColor();
		}
	}
	
	public void
	setCalendarColor(String _color) {
		room.setCalendarColor(_color);
	}
	
}