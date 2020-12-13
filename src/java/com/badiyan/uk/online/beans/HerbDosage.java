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
import java.math.RoundingMode;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
HerbDosage
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,HerbDosage> hash = new HashMap<Integer,HerbDosage>(11);

    // CLASS METHODS
	
	public static void
	delete(HerbDosage obj) throws TorqueException {
		Integer key = new Integer(obj.getId());
		hash.remove(key);
		HerbDosageDbPeer.doDelete(obj.herb_dosage);
	}

    public static HerbDosage
    getHerbDosage(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		HerbDosage herb_dosage = (HerbDosage)hash.get(key);
		if (herb_dosage == null)
		{
			Criteria crit = new Criteria();
			crit.add(HerbDosageDbPeer.HERB_DOSAGE_DB_ID, _id);
			List objList = HerbDosageDbPeer.doSelect(crit);
			if (objList.isEmpty())
				throw new ObjectNotFoundException("Could not locate Herb Dosage with id: " + _id);

			herb_dosage = HerbDosage.getHerbDosage((HerbDosageDb)objList.get(0));
		}

		return herb_dosage;
    }

    private static HerbDosage
    getHerbDosage(HerbDosageDb _herb_dosage)
		throws TorqueException
    {
		Integer key = new Integer(_herb_dosage.getHerbDosageDbId());
		HerbDosage herb_dosage = (HerbDosage)hash.get(key);
		if (herb_dosage == null)
		{
			herb_dosage = new HerbDosage(_herb_dosage);
			hash.put(key, herb_dosage);
		}

		return herb_dosage;
    }

    public static Vector
    getHerbDosages(CompanyBean _company)
		throws TorqueException
    {
		/*
		Criteria crit = new Criteria();
		crit.add(HerbDosageDbPeer.COMPANY_ID, _company.getId());
		return HerbDosageDbPeer.doSelect(crit).iterator();
		*/
				
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(HerbDosageDbPeer.COMPANY_ID, _company.getId());
		crit.addDescendingOrderByColumn(HerbDosageDbPeer.MIX_DATE);
		Iterator itr = HerbDosageDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			HerbDosageDb obj = (HerbDosageDb)itr.next();
			vec.addElement(HerbDosage.getHerbDosage(obj));
		}
		return vec;
    }

    public static Vector
    getHerbDosages(CompanyBean _company, boolean _active_only)
		throws TorqueException
    {
		/*
		Criteria crit = new Criteria();
		crit.add(HerbDosageDbPeer.COMPANY_ID, _company.getId());
		return HerbDosageDbPeer.doSelect(crit).iterator();
		*/
				
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(HerbDosageDbPeer.COMPANY_ID, _company.getId());
		if (_active_only)
			crit.add(HerbDosageDbPeer.IS_ACTIVE, (short)1);
		crit.addDescendingOrderByColumn(HerbDosageDbPeer.MIX_DATE);
		Iterator itr = HerbDosageDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			HerbDosageDb obj = (HerbDosageDb)itr.next();
			vec.addElement(HerbDosage.getHerbDosage(obj));
		}
		return vec;
    }

    public static Vector
    getHerbDosages(UKOnlinePersonBean _client)
		throws TorqueException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(HerbDosageDbPeer.CLIENT_ID, _client.getId());
		crit.addDescendingOrderByColumn(HerbDosageDbPeer.MIX_DATE);
		Iterator itr = HerbDosageDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			HerbDosageDb obj = (HerbDosageDb)itr.next();
			vec.addElement(HerbDosage.getHerbDosage(obj));
		}
		return vec;
    }

    // SQL

    /*
<table name="HERB_DOSAGE_DB" idMethod="native">
    <column name="HERB_DOSAGE_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    
	<column name="MIXTURE_DESCRIPTION" required="true" type="VARCHAR" size="100"/>
	<column name="NOTES" type="LONGVARCHAR"/>

    <column name="MIX_DATE" required="true" type="DATE"/>
	
    <column name="IS_ACTIVE" type="SMALLINT" default="1"/>
    <column name="EXPIRATION_EMAIL_SENT" type="SMALLINT" default="0"/>
	
    <column name="CLIENT_ID" required="true" type="INTEGER"/>
	<column name="MIXER_ID" required="true" type="INTEGER"/>
	
    <column name="COMPANY_ID" required="true" type="INTEGER"/>

	<foreign-key foreignTable="PERSON">
		<reference local="CLIENT_ID" foreign="PERSONID"/>
	</foreign-key>
	<foreign-key foreignTable="PERSON">
		<reference local="MIXER_ID" foreign="PERSONID"/>
	</foreign-key>
    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>

</table>



     */

    // INSTANCE VARIABLES

    private HerbDosageDb herb_dosage;
	private Vector use_directions;
	private Vector items;
	
	private BigDecimal mh_cost_retail_200ml_total = null;
	private BigDecimal mh_cost_per_1ml_total = null;
	private BigDecimal herb_ml_used_total = null;
	private BigDecimal dosage_cost_total = null;
	private BigDecimal retail_200ml_total = null;
	private BigDecimal forty_perc_COGS_total = null;
	private BigDecimal total_retail_40ml_total = null;
	
	private boolean expirationEmailSent;

    // CONSTRUCTORS

    public
    HerbDosage()
    {
		herb_dosage = new HerbDosageDb();
		isNew = true;
    }

    public
    HerbDosage(HerbDosageDb _herb_dosage)
    {
		herb_dosage = _herb_dosage;
		isNew = false;
    }

    // INSTANCE METHODS

	public boolean isExpirationEmailSent() {
		return herb_dosage.getExpirationEmailSent() == (short)1;
	}

	public void setExpirationEmailSent(boolean expirationEmailSent) {
		herb_dosage.setExpirationEmailSent(expirationEmailSent ? (short)1 : (short)0);
	}
	
	public short getDaysRemaining() throws TorqueException, ObjectNotFoundException {
		
		this.getUseDirections();
		if (use_directions.isEmpty())
			return -1;
		
		short direction_days = -1;
		Iterator dirItr = use_directions.iterator();
		while (dirItr.hasNext()) {
			HerbDosageUseDirections dirObj = (HerbDosageUseDirections)dirItr.next();
			if (dirObj.getEndDay() > direction_days)
				direction_days = dirObj.getEndDay();
		}
		
		Date now = new Date();
		long millis_since_mix = now.getTime() - this.getMixDate().getTime();
		long millis_per_day = 1000 * 60 * 60 * 24;
		short days_since_mix = (short)(millis_since_mix / millis_per_day);
		short days_remaining = (short)(direction_days - days_since_mix);
		return days_remaining;
	}
	
	public String getDaysRemainingString() throws TorqueException, ObjectNotFoundException {
		
		short days_remaining = this.getDaysRemaining();
		if (days_remaining < 1)
			return "";
		return days_remaining + "";
	}

	public String getDosageCostTotalString() throws TorqueException, ObjectNotFoundException {
		if (dosage_cost_total == null)
			this.setItems(this.getItems());
		return dosage_cost_total.setScale(2, RoundingMode.HALF_UP).toString();
	}

	public String getFortyPercCOGSTotalString() throws TorqueException, ObjectNotFoundException {
		if (forty_perc_COGS_total == null)
			this.setItems(this.getItems());
		return forty_perc_COGS_total.setScale(2, RoundingMode.HALF_UP).toString();
	}
	
	public BigDecimal getHerbMLUsedTotal() throws TorqueException, ObjectNotFoundException {
		if (herb_ml_used_total == null)
			this.setItems(this.getItems());
		return herb_ml_used_total.setScale(0, RoundingMode.HALF_UP);
	}

	public String getHerbMLUsedTotalString() throws TorqueException, ObjectNotFoundException {
		if (herb_ml_used_total == null)
			this.setItems(this.getItems());
		return herb_ml_used_total.setScale(0, RoundingMode.HALF_UP).toString();
	}

	public String getMHCostPer1MLTotalString() throws TorqueException, ObjectNotFoundException {
		if (mh_cost_per_1ml_total == null)
			this.setItems(this.getItems());
		return mh_cost_per_1ml_total.setScale(2, RoundingMode.HALF_UP).toString();
	}

	public String getMHCostRetail200MLTotalString() throws TorqueException, ObjectNotFoundException {
		if (mh_cost_retail_200ml_total == null)
			this.setItems(this.getItems());
		return mh_cost_retail_200ml_total.setScale(2, RoundingMode.HALF_UP).toString();
	}

	public String getRetail200MLTotalString() throws TorqueException, ObjectNotFoundException {
		if (retail_200ml_total == null)
			this.setItems(this.getItems());
		return retail_200ml_total.setScale(2, RoundingMode.HALF_UP).toString();
	}

	public BigDecimal getTotalRetail40MLTotal() throws TorqueException, ObjectNotFoundException {
		if (total_retail_40ml_total == null)
			this.setItems(this.getItems());
		return total_retail_40ml_total.setScale(2, RoundingMode.HALF_UP);
	}

	public String getTotalRetail40MLTotalString() throws TorqueException, ObjectNotFoundException {
		if (total_retail_40ml_total == null)
			this.setItems(this.getItems());
		return total_retail_40ml_total.setScale(2, RoundingMode.HALF_UP).toString();
	}
	
	public boolean
	hasClient() {
		return (herb_dosage.getClientId() > 0);
	}

	public UKOnlinePersonBean
	getClient()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(herb_dosage.getClientId());
	}

	public void
	setClient(UKOnlinePersonBean _client)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		herb_dosage.setClientId(_client.getId());
	}

	public CompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return CompanyBean.getCompany(herb_dosage.getCompanyId());
	}

	public void
	setCompany(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		herb_dosage.setCompanyId(_company.getId());
	}

	public String getDescription() {
		String str = herb_dosage.getMixtureDescription();
		if (str == null)
			return "";
		return str;
	}

	public void setDescription(String _description) {
		herb_dosage.setMixtureDescription(_description);
	}

	public Date getMixDate() {
		return herb_dosage.getMixDate();
	}

	public void setMixDate(Date _date) {
		herb_dosage.setMixDate(_date);
	}

	public UKOnlinePersonBean
	getMixer()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(herb_dosage.getMixerId());
	}

	public void
	setMixer(UKOnlinePersonBean _mixer)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		herb_dosage.setMixerId(_mixer.getId());
	}

	/*
	public String getShortNote() {
		String str = herb_dosage.getNotes();
		if (str == null)
			return "";
		int pipe_index = str.indexOf('|');
		if (pipe_index > -1)
			return str.substring(0, pipe_index);
		else
			return str;
	}
	*/

	public String getNotes() {
		String str = herb_dosage.getNotes();
		if (str == null)
			return "";
		//return str.replaceAll("\\|", "\r\n");
		return str;
	}

	public void setNotes(String _notes) {
		herb_dosage.setNotes(_notes);
	}

    public int
    getId()
    {
		return herb_dosage.getHerbDosageDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		if (herb_dosage.getCompanyId() < 1)
			return "[NEW SUBSCRIPTION]";
		return this.getCompany().getLabel() + " - " + (this.isActive() ? "Active" : "Inactive");
    }

    public String
    getValue()
    {
		return herb_dosage.getHerbDosageDbId() + "";
    }

    protected void
    insertObject()
		throws Exception
    {
		herb_dosage.save();
		this.saveUseDirections();
		this.saveItems();
    }
	
	public boolean
	isActive()
	{
		return (herb_dosage.getIsActive() == (short)1);
	}

	public void
	activate()
		throws TorqueException
	{
		herb_dosage.setIsActive((short)1);
	}

	public void
	inActivate()
		throws TorqueException
	{
		herb_dosage.setIsActive((short)0);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		herb_dosage.save();
		this.saveUseDirections();
		this.saveItems();
    }

	public Vector
	getUseDirections()
		throws TorqueException, ObjectNotFoundException
	{
		if (use_directions == null)
		{
			use_directions = new Vector();

			Criteria crit = new Criteria();
			crit.add(HerbDosageUseDirectionsDbPeer.HERB_DOSAGE_DB_ID, this.getId());
			Iterator itr = HerbDosageUseDirectionsDbPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				HerbDosageUseDirectionsDb obj = (HerbDosageUseDirectionsDb)itr.next();
				use_directions.addElement(HerbDosageUseDirections.getHerbDosageUseDirections(obj.getHerbDosageUseDirectionsDbId()));
			}
		}
		return use_directions;
	}

	public void
	setUseDirections(Vector _directions)
	{
		use_directions = _directions;
	}

	private void
    saveUseDirections()
		throws TorqueException, Exception
    {
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

		if (this.use_directions != null)
		{
			System.out.println("sizer >" + this.use_directions.size());

			HashMap db_directions_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(HerbDosageUseDirectionsDbPeer.HERB_DOSAGE_DB_ID, this.getId());
			Iterator itr = HerbDosageUseDirectionsDbPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				HerbDosageUseDirections value = HerbDosageUseDirections.getHerbDosageUseDirections((HerbDosageUseDirectionsDb)itr.next());
				Integer key = new Integer(value.getId());
				db_directions_hash.put(key, value);
			}

			itr = this.use_directions.iterator();
			while (itr.hasNext())
			{
				HerbDosageUseDirections directions = (HerbDosageUseDirections)itr.next();
				Integer key = new Integer(directions.getId());
				Object obj = db_directions_hash.remove(key);
				if (obj == null)
					directions.setParent(this);
				directions.save();
			}

			itr = db_directions_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				HerbDosageUseDirections obj = (HerbDosageUseDirections)db_directions_hash.get(key);
				System.out.println("del obj.getHerbDosageUseDirectionsDbId() >" + obj.getId());
				HerbDosageUseDirections.delete(obj);
			}
		}
    }

	public Vector
	getItems()
		throws TorqueException, ObjectNotFoundException
	{
		if (items == null)
			items = HerbDosageItemMapping.getItems(this);
		return items;
	}
	
	/*
String value = "{\"label\" : \"" + item.getLabel() + "\"," +
						" \"p1\" : \"" + item.getAmountString() + "\"," +
						" \"p2\" : \"" + _obj.getAmountPerMLString() + "\"," +
						" \"p3\" : \"" + _obj.getML() + "\"," +
						" \"p4\" : \"" + _obj.getDosageCostString() + "\"," +
						" \"p5\" : \"-\"," +
						" \"p6\" : \"-\"," +
						" \"p7\" : \"" + _obj.getDosageAmountString() + "\"," +
						" \"p8\" : \"" + _obj.getCOGSString() + "\"," +
						" \"p9\" : \"" + _obj.getTotalRetail40MLString() + "\" }";
	 */

	public void
	setItems(Vector _items) throws TorqueException, ObjectNotFoundException
	{
		items = _items;
		
		mh_cost_retail_200ml_total = BigDecimal.ZERO;
		mh_cost_per_1ml_total = BigDecimal.ZERO;
		herb_ml_used_total = BigDecimal.ZERO;
		dosage_cost_total = BigDecimal.ZERO;
		retail_200ml_total = BigDecimal.ZERO;
		forty_perc_COGS_total = BigDecimal.ZERO;
		total_retail_40ml_total = BigDecimal.ZERO;
		
		Iterator itemItr = items.iterator();
		while (itemItr.hasNext()) {
			HerbDosageItemMapping mappingItem = (HerbDosageItemMapping)itemItr.next();
			CheckoutCodeBean item = mappingItem.getItem();
			mh_cost_retail_200ml_total = mh_cost_retail_200ml_total.add(item.getAmount());
			mh_cost_per_1ml_total = mh_cost_per_1ml_total.add(mappingItem.getAmountPerML());
			herb_ml_used_total = herb_ml_used_total.add(mappingItem.getMLBigDecimal());
			dosage_cost_total = dosage_cost_total.add(mappingItem.getDosageCost());
			retail_200ml_total = retail_200ml_total.add(mappingItem.getDosageAmount());
			forty_perc_COGS_total = forty_perc_COGS_total.add(mappingItem.getCOGS());
			total_retail_40ml_total = total_retail_40ml_total.add(mappingItem.getTotalRetail40ML());
		}
		
	}

	private void
    saveItems()
		throws TorqueException, Exception
    {
		/*
<table name="HERB_DOSAGE_DB_ITEM_MAPPING">
    <column name="HERB_DOSAGE_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="CHECKOUT_CODE_ID" primaryKey="true" required="true" type="INTEGER"/>
	
	<column name="ML" required="true" type="SMALLINT"/>

    <foreign-key foreignTable="HERB_DOSAGE_DB">
		<reference local="HERB_DOSAGE_DB_ID" foreign="HERB_DOSAGE_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="CHECKOUT_CODE">
		<reference local="CHECKOUT_CODE_ID" foreign="CHECKOUT_CODE_ID"/>
    </foreign-key>
</table>
		 */

		if (this.items != null)
		{
			System.out.println("sizer >" + this.items.size());

			HashMap db_items_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(HerbDosageDbItemMappingPeer.HERB_DOSAGE_DB_ID, this.getId());
			Iterator itr = HerbDosageDbItemMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				HerbDosageItemMapping value = HerbDosageItemMapping.getHerbDosageItemMapping((HerbDosageDbItemMapping)itr.next());
				Integer key = new Integer(value.getItem().getId());
				db_items_hash.put(key, value);
			}

			itr = this.items.iterator();
			while (itr.hasNext())
			{
				HerbDosageItemMapping mapping = (HerbDosageItemMapping)itr.next();
				Integer key = new Integer(mapping.getItem().getId());
				Object obj = db_items_hash.remove(key);
				if (obj == null)
					mapping.setHerbDosage(this);
				mapping.save();
			}

			itr = db_items_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				HerbDosageUseDirections obj = (HerbDosageUseDirections)db_items_hash.get(key);
				System.out.println("del obj.getHerbDosageUseDirectionsDbId() >" + obj.getId());
				HerbDosageUseDirections.delete(obj);
			}
		}
		
    }
}