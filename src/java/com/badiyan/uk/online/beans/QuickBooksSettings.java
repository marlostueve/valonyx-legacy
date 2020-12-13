/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
QuickBooksSettings
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

	private static HashMap hash = new HashMap(3);

	// CLASS METHODS

	public static QuickBooksSettings
	getSettings(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		QuickBooksSettings settings = (QuickBooksSettings)hash.get(key);
		if (settings == null)
		{
			Criteria crit = new Criteria();
			crit.add(QuickbooksSettingsDbPeer.COMPANY_ID, _id);
			List objList = QuickbooksSettingsDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate QuickBooks settings with id: " + _id);

			settings = QuickBooksSettings.getSettings((QuickbooksSettingsDb)objList.get(0));
		}

		return settings;
	}

	private static QuickBooksSettings
	getSettings(QuickbooksSettingsDb _settings)
	{
		Integer key = new Integer(_settings.getCompanyId());
		QuickBooksSettings settings = (QuickBooksSettings)hash.get(key);
		if (settings == null)
		{
			settings = new QuickBooksSettings(_settings);
			hash.put(key, settings);
		}

		return settings;
	}

	public static Vector
	getSettings()
		throws TorqueException, ObjectNotFoundException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		Iterator itr = QuickbooksSettingsDbPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(QuickBooksSettings.getSettings((QuickbooksSettingsDb)itr.next()));

		return vec;
	}

	public static QuickBooksSettings
	getSettings(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_company.getId());
		QuickBooksSettings settings = (QuickBooksSettings)hash.get(key);
		if (settings == null)
		{
			Criteria crit = new Criteria();
			crit.add(QuickbooksSettingsDbPeer.COMPANY_ID, _company.getId());
			List objList = QuickbooksSettingsDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate QuickBooks settings for " + _company.getLabel());

			settings = QuickBooksSettings.getSettings((QuickbooksSettingsDb)objList.get(0));
		}

		return settings;
	}

	public static QuickBooksSettings
	getSettings(String _user_name, String _password)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(QuickbooksSettingsDbPeer.USER_NAME, _user_name);
		crit.add(QuickbooksSettingsDbPeer.PASSWORD, _password);
		List objList = QuickbooksSettingsDbPeer.doSelect(crit);
		if (objList.size() == 1)
			return QuickBooksSettings.getSettings((QuickbooksSettingsDb)objList.get(0));
		else if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate QuickBooks settings with username " + _user_name + ".  Please check your password.");
		else
			throw new UniqueObjectNotFoundException("Log in failure.");
	}

	// INSTANCE VARIABLES

	private QuickbooksSettingsDb settings;
	private StringBuffer message_buffer;

	private CashOut active_cash_out;
	
	private boolean inventory_update_request_in_queue = false;
	private boolean client_update_request_in_queue = false;

	// CONSTRUCTORS

	public
	QuickBooksSettings()
	{
		settings = new QuickbooksSettingsDb();
		isNew = true;
	}

	public
	QuickBooksSettings(QuickbooksSettingsDb _settings)
	{
		settings = _settings;
		isNew = false;
	}

	// INSTANCE METHODS

	/*
	 *<table name="QUICKBOOKS_SETTINGS_DB" idMethod="native">
    <column name="QUICKBOOKS_SETTINGS_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_KEY" required="false" type="VARCHAR" size="200"/>
    <column name="IS_QBFS_ENABLED" type="SMALLINT" default="0"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>

    <foreign-key foreignTable="COMPANY">
	<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
</table>
	 */

	public void
	addMessage(String _message)
	{
		if (message_buffer == null)
			message_buffer = new StringBuffer();
		
		message_buffer.append(_message);
		message_buffer.append('|');
		
	}
	
	public String
	getLastMessage()
	{
		String last_message = "";
		if (message_buffer != null)
		{
			String message_str = message_buffer.substring(0, message_buffer.length() - 1);
			int last_pipe_index = message_str.lastIndexOf('|');
			if (last_pipe_index > -1)
				last_message = message_str.substring(last_pipe_index + 1);
			else
				last_message = message_str;
		}
		return last_message;
	}

	public CashOut
	getActiveCashOut()
	{
		return this.active_cash_out;
	}

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(settings.getCompanyId());
	}

	public String
	getCompanyKeyString()
	{
		String str = settings.getCompanyKey();
		if (str == null)
			return "";
		return str;
	}

	public String
	getFileIDString()
	{
		String str = settings.getFileID();
		if (str == null)
			return "";
		return str;
	}

	public int
	getId()
	{
		return settings.getQuickbooksSettingsDbId();
	}

	public String
	getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return this.getCompany().getLabel() + " QuickBooks settings";
	}
	
	public Date
	getLastClientUpdateDate()
	{
		return settings.getLastClientUpdateDate();
	}
	
	public Date
	getLastInventoryUpdateDate()
	{
		return settings.getLastInventoryUpdateDate();
	}

	public String
	getOwnerIDString()
	{
		String str = settings.getOwnerID();
		if (str == null)
			return "";
		return str;
	}

	public String
	getPasswordString()
	{
		String str = settings.getPassword();
		if (str == null)
			return "";
		return str;
	}

	public String
	getValue()
	{
		return settings.getQuickbooksSettingsDbId() + "";
	}

	public String
	getUserNameString()
	{
		String str = settings.getUserName();
		if (str == null)
			return "";
		return str;
	}

	protected void
	insertObject()
		throws com.badiyan.uk.exceptions.ObjectAlreadyExistsException, com.badiyan.uk.exceptions.IllegalValueException, Exception
	{
		settings.save();

		Integer key = new Integer(settings.getCompanyId());
		QuickBooksSettings.hash.put(key, this);
	}

	public boolean
	isPOSEnabled()
	{
		return (settings.getIsPosEnabled() == (short)1);
	}

	public boolean
	isQuickBooksFSEnabled()
	{
	    return (settings.getIsQbfsEnabled() == (short)1);
	}

	public boolean
	isQuickBooksFSLogInAttempted()
	{
	    return (settings.getIsQbfsLogInAttempted() == (short)1);
	}

	public boolean
	isQuickBooksFSTalking()
	{
	    return (settings.getIsQbfsTalking() == (short)1);
	}

	public boolean
	isSyncAccounts()
	{
	    return (settings.getSyncAccounts() == (short)1);
	}

	public boolean
	isSyncClients()
	{
	    return (settings.getSyncClients() == (short)1);
	}

	public boolean
	isSyncInventory()
	{
	    return (settings.getSyncInventory() == (short)1);
	}

	public boolean
	isSyncVendors()
	{
	    return (settings.getSyncVendors() == (short)1);
	}
	
	public boolean
	isClientUpdateRequestInQueue()
	{
		return client_update_request_in_queue;
	}
	
	public boolean
	isInventoryUpdateRequestInQueue()
	{
		return inventory_update_request_in_queue;
	}

	public void
	setActiveCashOut(CashOut _cash_out)
	{
		this.active_cash_out = _cash_out;
	}

	public void
	setCompany(CompanyBean _company)
		throws TorqueException
	{
		settings.setCompanyId(_company.getId());
	}

	public void
	setCompanyKey(String _company_key)
	{
		settings.setCompanyKey(_company_key);
	}

	public void
	setFileID(String _file_id)
	{
		settings.setFileID(_file_id);
	}
	
	public void
	setClientUpdateRequestInQueue()
	{
		client_update_request_in_queue = true;
	}
	
	public void
	setLastClientUpdateDate(Date _date)
	{
		settings.setLastClientUpdateDate(_date);
	}
	
	public void
	setInventoryUpdateRequestInQueue()
	{
		inventory_update_request_in_queue = true;
	}
	
	public void
	setLastInventoryUpdateDate(Date _date)
	{
		settings.setLastInventoryUpdateDate(_date);
	}

	public void
	setMessageBuffer(StringBuffer _message_buffer)
	{
		message_buffer = _message_buffer;
	}

	public void
	setOwnerID(String _owner_id)
	{
		settings.setOwnerID(_owner_id);
	}

	public void
	setPassword(String _password)
	{
		settings.setPassword(_password);
	}

	public void
	setPOSEnabled(boolean _enabled)
	{
		settings.setIsPosEnabled(_enabled ? (short)1 : (short)0);
	}

	public void
	setQuickBooksFSEnabled(boolean _enabled)
	{
		settings.setIsQbfsEnabled(_enabled ? (short)1 : (short)0);
	}

	public void
	setQuickBooksFSLogInAttempted(boolean _log_in_attempted)
	{
		settings.setIsQbfsLogInAttempted(_log_in_attempted ? (short)1 : (short)0);
	}

	public void
	setQuickBooksFSTalking(boolean _talking)
	{
		settings.setIsQbfsTalking(_talking ? (short)1 : (short)0);
	}

	public void
	setSyncAccounts(boolean _sync)
	{
		settings.setSyncAccounts(_sync ? (short)1 : (short)0);
	}

	public void
	setSyncClients(boolean _sync)
	{
		settings.setSyncClients(_sync ? (short)1 : (short)0);
	}

	public void
	setSyncInventory(boolean _sync)
	{
		settings.setSyncInventory(_sync ? (short)1 : (short)0);
	}

	public void
	setSyncVendors(boolean _sync)
	{
		settings.setSyncVendors(_sync ? (short)1 : (short)0);
	}

	public void
	setUserName(String _user_name)
	{
		settings.setUserName(_user_name);
	}

	public boolean
	showClientIntakeForm() {
	    return (settings.getShowClientIntakeForm() == (short)1);
	}

	public boolean
	showClientWellnessSurvey() {
	    return (settings.getShowClientWellnessSurvey() == (short)1);
	}

	public void
	setShowClientIntakeForm(short _show) {
		settings.setShowClientIntakeForm(_show);
	}

	public void
	setShowClientWellnessSurvey(short _show) {
		settings.setShowClientWellnessSurvey(_show);
	}

	protected void
	updateObject()
		throws com.badiyan.uk.exceptions.ObjectAlreadyExistsException, com.badiyan.uk.exceptions.IllegalValueException, Exception
	{
		settings.save();
	}

	public String
	getXLoginString()
	{
		String str = settings.getXLogin();
		if (str == null)
			return "";
		return str;
	}

	public void
	setXLogin(String _str)
	{
		settings.setXLogin(_str);
	}

	public String
	getXTranKeyString()
	{
		String str = settings.getXTranKey();
		if (str == null)
			return "";
		return str;
	}

	public void
	setXTranKey(String _str)
	{
		settings.setXTranKey(_str);
	}
	
	public boolean
	doesAcceptBTC() {
		return true;
	}

}