/*
 * MineralRatiosClientDocumentBean.java
 *
 * Created on July 14, 2007, 10:55 AM
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

/**
 *
 * @author marlo
 */
public class
MineralRatiosClientDocumentBean
	extends com.badiyan.uk.beans.CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES
	
	private static HashMap documents = new HashMap(19);
	
	// CLASS METHODS
	
	public static MineralRatiosClientDocumentBean
	getMineralRatiosClientDocument(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		MineralRatiosClientDocumentBean document = (MineralRatiosClientDocumentBean)documents.get(key);
		if (document == null)
		{	
			Criteria crit = new Criteria();
			crit.add(MineralRatiosClientDocumentPeer.MINERAL_RATIOS_CLIENT_DOCUMENT_ID, _id);
			List objList = MineralRatiosClientDocumentPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate mineral ratios client document with id: " + _id);
		
			document = MineralRatiosClientDocumentBean.getMineralRatiosClientDocument((MineralRatiosClientDocument)objList.get(0));
		}
		
		return document;
	}
	
	private static MineralRatiosClientDocumentBean
	getMineralRatiosClientDocument(MineralRatiosClientDocument _document)
	{
		Integer key = new Integer(_document.getMineralRatiosClientDocumentId());
		MineralRatiosClientDocumentBean document = (MineralRatiosClientDocumentBean)documents.get(key);
		if (document == null)
		{
			document = new MineralRatiosClientDocumentBean(_document);
			documents.put(key, document);
		}
		
		return document;
	}
	
	public static Vector
	getMineralRatiosClientDocuments(PersonBean _person)
		throws TorqueException
	{
		Vector vec = new Vector();
				
		Criteria crit = new Criteria();
		crit.add(MineralRatiosClientDocumentPeer.PERSON_ID, _person.getId());
		crit.addAscendingOrderByColumn(MineralRatiosClientDocumentPeer.REPORT_DATE);
		Iterator obj_itr = MineralRatiosClientDocumentPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(MineralRatiosClientDocumentBean.getMineralRatiosClientDocument((MineralRatiosClientDocument)obj_itr.next()));
		
		return vec;
	}
	
	public static Vector
	getMineralRatiosClientDocumentTemplates()
	    throws TorqueException
	{
		Vector vec = new Vector();
				
		Criteria crit = new Criteria();
		crit.add(MineralRatiosClientDocumentPeer.IS_TEMPLATE, (short)1);
		crit.addAscendingOrderByColumn(MineralRatiosClientDocumentPeer.NOTES);
		Iterator obj_itr = MineralRatiosClientDocumentPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(MineralRatiosClientDocumentBean.getMineralRatiosClientDocument((MineralRatiosClientDocument)obj_itr.next()));
		
		return vec;
	}
	
	// INSTANCE VARIABLES
	
	private MineralRatiosClientDocument document;
	
	// CONSTRUCTORS
	
	public
	MineralRatiosClientDocumentBean()
	{
		document = new MineralRatiosClientDocument();
		isNew = true;
	}
	
	public
	MineralRatiosClientDocumentBean(MineralRatiosClientDocument _document)
	{
		document = _document;
		isNew = false;
	}
	
	// INSTANCE METHODS
	
	/*
	 *<table name="MINERAL_RATIOS_CLIENT_DOCUMENT" idMethod="native">
	    <column name="MINERAL_RATIOS_CLIENT_DOCUMENT_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	    
	    <column name="PERSON_ID" required="true" type="INTEGER"/>
	    
	    <column name="IS_TEMPLATE" type="SMALLINT" default="0"/>

	    <column name="NOTES" type="LONGVARCHAR"/>
	    
	    <column name="REPORT_DATE" required="true" type="TIMESTAMP"/>
	    <column name="INITIAL_RECOMMENDATIONS" type="LONGVARCHAR"/>
	    <column name="SUPPLEMENTS_HERBAL_MIXTURES" type="LONGVARCHAR"/>
	    <column name="DETOX_EXCERCISE_DIET_STRESS_MANAGEMENT" type="LONGVARCHAR"/>
	    <column name="NEXT_APPOINTMENT" type="LONGVARCHAR"/>
	    
	    <column name="PHASE_I_DATE" required="true" type="TIMESTAMP"/>
	    <column name="PHASE_I_I_DATE" required="true" type="TIMESTAMP"/>
	    <column name="PHASE_I_I_I_DATE" required="true" type="TIMESTAMP"/>
	    <column name="PHASE_I_V_DATE" required="true" type="TIMESTAMP"/>
	    <column name="PHASE_V_DATE" required="true" type="TIMESTAMP"/>
	    
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
	
	public String
	get(String _key)
	    throws TorqueException, IllegalValueException
	{
	    /*<table name="MINERAL_RATIOS_CLIENT_DOCUMENT_DATA" idMethod="native">
	    <column name="MINERAL_RATIOS_CLIENT_DOCUMENT_DATA_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	    
	    <column name="MINERAL_RATIOS_CLIENT_DOCUMENT_ID" required="true" type="INTEGER"/>
	    
	    <column name="DATA_KEY" size="50" type="VARCHAR"/>
	    <column name="DATA_VALUE" type="LONGVARCHAR"/>

	    <foreign-key foreignTable="MINERAL_RATIOS_CLIENT_DOCUMENT">
		<reference local="MINERAL_RATIOS_CLIENT_DOCUMENT_ID" foreign="MINERAL_RATIOS_CLIENT_DOCUMENT_ID"/>
	    </foreign-key>
	</table>
	     */
	    
	    // determine if the specified key already has a value
	    
	    Criteria crit = new Criteria();
	    crit.add(MineralRatiosClientDocumentDataPeer.MINERAL_RATIOS_CLIENT_DOCUMENT_ID, this.getId());
	    crit.add(MineralRatiosClientDocumentDataPeer.DATA_KEY, _key);
	    List list_obj = MineralRatiosClientDocumentDataPeer.doSelect(crit);
	    if (list_obj.size() == 0)
	    {
		return "";
	    }
	    else if (list_obj.size() == 1)
	    {
		MineralRatiosClientDocumentData data_obj = (MineralRatiosClientDocumentData)list_obj.get(0);
		return data_obj.getDataValue();
	    }
	    else
		throw new IllegalValueException("Multiple keys found >" + _key);
	}
	
	public String
	getDetoxExerciseDietStressManagementString()
	{
	    String str = document.getDetoxExcerciseDietStressManagement();
	    if (str == null)
		return "";
	    return str;
	}
	
	public int
	getId()
	{
	    return document.getMineralRatiosClientDocumentId();
	}
	
	public String
	getInitialRecommendationsString()
	{
	    String str = document.getInitialRecommendations();
	    if (str == null)
		return "";
	    return str;
	}
	
	public String
	getLabel()
	{
	    return this.getReportDateString();
	}
	
	public String
	getNextAppointmentString()
	{
	    String str = document.getNextAppointment();
	    if (str == null)
		return "";
	    return str;
	}
	
	public PersonBean
	getPerson()
	    throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
	    return UKOnlinePersonBean.getPerson(document.getPersonId());
	}
	
	public String
	getPhaseIDateString()
	{
	    Date date = document.getPhaseIDate();
	    if (date == null)
		return "";
	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    if ((cal.get(Calendar.HOUR_OF_DAY) != 0) && (cal.get(Calendar.MINUTE) != 0) && (cal.get(Calendar.SECOND) != 0))
		return "";
	    
	    String str = CUBean.getUserDateString(date);
	    return str;
	}
	
	public String
	getPhaseIIDateString()
	{
	    Date date = document.getPhaseIIDate();
	    if (date == null)
		return "";
	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    if ((cal.get(Calendar.HOUR_OF_DAY) != 0) && (cal.get(Calendar.MINUTE) != 0) && (cal.get(Calendar.SECOND) != 0))
		return "";
	    
	    String str = CUBean.getUserDateString(date);
	    return str;
	}
	
	public String
	getPhaseIIIDateString()
	{
	    Date date = document.getPhaseIIIDate();
	    if (date == null)
		return "";
	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    if ((cal.get(Calendar.HOUR_OF_DAY) != 0) && (cal.get(Calendar.MINUTE) != 0) && (cal.get(Calendar.SECOND) != 0))
		return "";
	    
	    String str = CUBean.getUserDateString(date);
	    return str;
	}
	
	public String
	getPhaseIVDateString()
	{
	    Date date = document.getPhaseIVDate();
	    if (date == null)
		return "";
	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    if ((cal.get(Calendar.HOUR_OF_DAY) != 0) && (cal.get(Calendar.MINUTE) != 0) && (cal.get(Calendar.SECOND) != 0))
		return "";
	    
	    String str = CUBean.getUserDateString(date);
	    return str;
	}
	
	public String
	getReportDateString()
	{
	    Date report_date = document.getReportDate();
	    if (report_date == null)
		return "";
	    
	    String report_date_str = CUBean.getUserDateString(report_date);
	    return report_date_str;
	}
	
	public String
	getSupplementsHerbalMixturesString()
	{
	    String str = document.getSupplementsHerbalMixtures();
	    if (str == null)
		return "";
	    return str;
	}
	
	public String
	getTemplateNameString()
	{
	    return document.getNotes();
	}
	
	public String
	getValue()
	{
	    return this.getId() + "";
	}
	
	protected void
	insertObject()
		throws com.badiyan.uk.exceptions.ObjectAlreadyExistsException, com.badiyan.uk.exceptions.IllegalValueException, Exception
	{
	    if (document.getCreatePersonId() < 1)
		    throw new IllegalValueException("Create person not set for mineral ratios client document");

	    document.setCreationDate(new Date());
	    document.save();

	    Integer key = new Integer(document.getMineralRatiosClientDocumentId());
	    MineralRatiosClientDocumentBean.documents.put(key, this);
	}
	
	public boolean
	isTemplate()
	{
	    return (document.getIsTemplate() == (short)1);
	}
	
	public void
	put(String _key, String _value)
	    throws TorqueException, IllegalValueException, Exception
	{
	    /*<table name="MINERAL_RATIOS_CLIENT_DOCUMENT_DATA" idMethod="native">
	    <column name="MINERAL_RATIOS_CLIENT_DOCUMENT_DATA_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	    
	    <column name="MINERAL_RATIOS_CLIENT_DOCUMENT_ID" required="true" type="INTEGER"/>
	    
	    <column name="DATA_KEY" size="50" type="VARCHAR"/>
	    <column name="DATA_VALUE" type="LONGVARCHAR"/>

	    <foreign-key foreignTable="MINERAL_RATIOS_CLIENT_DOCUMENT">
		<reference local="MINERAL_RATIOS_CLIENT_DOCUMENT_ID" foreign="MINERAL_RATIOS_CLIENT_DOCUMENT_ID"/>
	    </foreign-key>
	</table>
	     */
	    
	    // determine if the specified key already has a value
	    
	    Criteria crit = new Criteria();
	    crit.add(MineralRatiosClientDocumentDataPeer.MINERAL_RATIOS_CLIENT_DOCUMENT_ID, this.getId());
	    crit.add(MineralRatiosClientDocumentDataPeer.DATA_KEY, _key);
	    List list_obj = MineralRatiosClientDocumentDataPeer.doSelect(crit);
	    if (list_obj.size() == 0)
	    {
		if (this.getId() == 0)
		    throw new IllegalValueException("Can't create MineralRatiosClientDocumentData for an unsaved client document.");
		    
		MineralRatiosClientDocumentData data_obj = new MineralRatiosClientDocumentData();
		data_obj.setMineralRatiosClientDocumentId(this.getId());
		data_obj.setDataKey(_key);
		data_obj.setDataValue(_value);
		data_obj.save();
	    }
	    else if (list_obj.size() == 1)
	    {
		MineralRatiosClientDocumentData data_obj = (MineralRatiosClientDocumentData)list_obj.get(0);
		data_obj.setDataValue(_value);
		data_obj.save();
	    }
	    else
		throw new IllegalValueException("Multiple keys found >" + _key);
	}
	
	public void
	setCreateOrModifyPerson(PersonBean _person)
	    throws TorqueException
	{
	    if (isNew)
		document.setCreatePersonId(_person.getId());
	    else
		document.setModifyPersonId(_person.getId());
	}
	
	public void
	setDetoxExerciseDietStressManagementString(String _str)
	{
	    document.setDetoxExcerciseDietStressManagement(_str);
	}
	
	public void
	setInitialRecommendations(String _str)
	{
	    document.setInitialRecommendations(_str);
	}
	
	public void
	setIsTemplate(boolean _is_template)
	{
	    document.setIsTemplate(_is_template ? (short)1 : (short)0);
	}
	
	public void
	setNextAppointmentString(String _str)
	{
	    document.setNextAppointment(_str);
	}
	
	public void
	setPerson(UKOnlinePersonBean _person)
	    throws TorqueException
	{
	    document.setPersonId(_person.getId());
	}
	
	public void
	setPhaseIDate(Date _date)
	{
	    document.setPhaseIDate(_date);
	}
	
	public void
	setPhaseIIDate(Date _date)
	{
	    document.setPhaseIIDate(_date);
	}
	
	public void
	setPhaseIIIDate(Date _date)
	{
	    document.setPhaseIIIDate(_date);
	}
	
	public void
	setPhaseIVDate(Date _date)
	{
	    document.setPhaseIVDate(_date);
	}
	
	public void
	setReportDate(Date _date)
	{
	    document.setReportDate(_date);
	}
	
	public void
	setSupplementsHerbalMixturesString(String _str)
	{
	    document.setSupplementsHerbalMixtures(_str);
	}
	
	public void
	setTemplateName(String _str)
	{
	    document.setNotes(_str);
	}
	
	protected void
	updateObject()
		throws com.badiyan.uk.exceptions.ObjectAlreadyExistsException, com.badiyan.uk.exceptions.IllegalValueException, Exception
	{
		if (document.getModifyPersonId() < 1)
			throw new IllegalValueException("Modify person not set for mineral ratios client document");
		
		document.setModificationDate(new Date());
		document.save();
	}
    
}