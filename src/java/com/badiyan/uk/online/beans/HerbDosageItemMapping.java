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
HerbDosageItemMapping
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    //protected static HashMap<Integer,HerbDosageItemMapping> hash = new HashMap<Integer,HerbDosageItemMapping>(11);

    // CLASS METHODS
	
	public static void
	delete(HerbDosageItemMapping obj) throws TorqueException {
		HerbDosageDbItemMappingPeer.doDelete(obj.item_mapping);
	}

	/*
    public static HerbDosageItemMapping
    getHerbDosageItemMapping(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		HerbDosageItemMapping item_mapping = (HerbDosageItemMapping)hash.get(key);
		if (item_mapping == null)
		{
			Criteria crit = new Criteria();
			crit.add(HerbDosageItemMappingDbPeer.item_mapping_DB_ID, _id);
			List objList = HerbDosageItemMappingDbPeer.doSelect(crit);
			if (objList.isEmpty())
				throw new ObjectNotFoundException("Could not locate Subscription Information with id: " + _id);

			item_mapping = HerbDosageItemMapping.getHerbDosageItemMapping((HerbDosageItemMappingDb)objList.get(0));
		}

		return item_mapping;
    }
	 */

    public static HerbDosageItemMapping
    getHerbDosageItemMapping(HerbDosageDbItemMapping _item_mapping)
		throws TorqueException
    {
		return new HerbDosageItemMapping(_item_mapping);
    }

    public static Vector
    getItems(HerbDosage _dosage)
		throws TorqueException
    {
		Vector vec = new Vector();
		if (_dosage.isNew())
			return vec;
		Criteria crit = new Criteria();
		crit.add(HerbDosageDbItemMappingPeer.HERB_DOSAGE_DB_ID, _dosage.getId());
		Iterator itr = HerbDosageDbItemMappingPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			HerbDosageDbItemMapping obj = (HerbDosageDbItemMapping)itr.next();
			vec.addElement(HerbDosageItemMapping.getHerbDosageItemMapping(obj));
		}
		return vec;
    }

    // SQL

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

    // INSTANCE VARIABLES

    private HerbDosageDbItemMapping item_mapping;
	
	private short mL;

    // CONSTRUCTORS

    public
    HerbDosageItemMapping()
    {
		item_mapping = new HerbDosageDbItemMapping();
		isNew = true;
    }

    public
    HerbDosageItemMapping(HerbDosageDbItemMapping _item_mapping)
    {
		item_mapping = _item_mapping;
		isNew = false;
    }

    // INSTANCE METHODS
	
	public BigDecimal getCostPerML() throws TorqueException, ObjectNotFoundException {
		
		BigDecimal totalML = new BigDecimal(200);
		if (this.getItem().getLabel().indexOf("500") > -1)
			totalML = new BigDecimal(500);
		
		BigDecimal cost = this.getItem().getOrderCost();
		return cost.divide(totalML, 2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getRetailCostPerML() throws TorqueException, ObjectNotFoundException {
		
		BigDecimal totalML = new BigDecimal(200);
		if (this.getItem().getLabel().indexOf("500") > -1)
			totalML = new BigDecimal(500);
		
		BigDecimal retail = this.getItem().getAmount();
		return retail.divide(totalML, 2, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getAmountPerML() throws TorqueException, ObjectNotFoundException {
		
		BigDecimal totalML = new BigDecimal(200);
		if (this.getItem().getLabel().indexOf("500") > -1)
			totalML = new BigDecimal(500);
		
		BigDecimal amount = this.getItem().getAmount();
		return amount.divide(totalML, 2, RoundingMode.HALF_UP);
	}
	
	public String getAmountPerMLString() throws TorqueException, ObjectNotFoundException {
		
		return this.getAmountPerML().toString();
	}
	
	public BigDecimal getDosageAmount() throws TorqueException, ObjectNotFoundException {
		/*
		BigDecimal amountPerML = this.getAmountPerML();
		BigDecimal ml_bd = new BigDecimal(item_mapping.getMl());
		return amountPerML.multiply(ml_bd).setScale(2, RoundingMode.HALF_UP);
		*/
		
		BigDecimal two = new BigDecimal(2);
		return this.getDosageCost().multiply(two).setScale(2, RoundingMode.HALF_UP);
	}
	
	public String getDosageAmountString() throws TorqueException, ObjectNotFoundException {
		
		return this.getDosageAmount().toString();
	}
	
	public BigDecimal getDosageCost() throws TorqueException, ObjectNotFoundException {
		
		BigDecimal retailPerML = this.getRetailCostPerML();
		BigDecimal ml_bd = new BigDecimal(item_mapping.getMl());
		return retailPerML.multiply(ml_bd).setScale(2, RoundingMode.HALF_UP);
	}
	
	public String getDosageCostString() throws TorqueException, ObjectNotFoundException {
		
		return this.getDosageCost().toString();
	}
	
	public BigDecimal getCOGS() throws TorqueException, ObjectNotFoundException {
		
		/*
		BigDecimal costPerML = this.getCostPerML();
		BigDecimal ml_bd = new BigDecimal(item_mapping.getMl());
		BigDecimal fortyPerc = new BigDecimal(0.4);
		return costPerML.multiply(ml_bd).multiply(fortyPerc).setScale(2, RoundingMode.HALF_UP);
		*/
		
		BigDecimal fortyPerc = new BigDecimal(0.4);
		return this.getDosageAmount().multiply(fortyPerc).setScale(2, RoundingMode.HALF_UP);
	}
	
	public String getCOGSString() throws TorqueException, ObjectNotFoundException {
		
		return this.getCOGS().toString();
	}
	
	public BigDecimal getTotalRetail40ML() throws TorqueException, ObjectNotFoundException {
		
		/*
		BigDecimal amountPerML = this.getAmountPerML();
		BigDecimal ml_bd = new BigDecimal(item_mapping.getMl());
		BigDecimal t_amount_bd = amountPerML.multiply(ml_bd).setScale(2, RoundingMode.HALF_UP);
		
		BigDecimal costPerML = this.getCostPerML();
		BigDecimal fortyPerc = new BigDecimal(0.4);
		BigDecimal x_bd = costPerML.multiply(ml_bd).multiply(fortyPerc).setScale(2, RoundingMode.HALF_UP);
		*/
		
		return this.getDosageAmount().add(this.getCOGS().setScale(2, RoundingMode.HALF_UP));
		
		//return t_amount_bd.add(x_bd).setScale(2, RoundingMode.HALF_UP);
	}
	
	public String getTotalRetail40MLString() throws TorqueException, ObjectNotFoundException {
		
		return this.getTotalRetail40ML().toString();
	}
	
	public short getML() {
		return item_mapping.getMl();
	}
	
	public BigDecimal getMLBigDecimal() {
		return new BigDecimal(item_mapping.getMl());
	}

	public void setML(short _ml) {
		item_mapping.setMl(_ml);
	}

	public void
	setHerbDosage(HerbDosage _dosage)
		throws TorqueException
	{
		item_mapping.setHerbDosageDbId(_dosage.getId());
	}

	public CheckoutCodeBean
	getItem()
		throws TorqueException, ObjectNotFoundException
	{
		return CheckoutCodeBean.getCheckoutCode(item_mapping.getCheckoutCodeId());
	}

	public void
	setItem(CheckoutCodeBean _item)
		throws TorqueException
	{
		item_mapping.setCheckoutCodeId(_item.getId());
	}

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException
    {
		if (item_mapping.getCheckoutCodeId() < 1)
			return "[NEW ITEM]";
		return this.getML() + "ml " + this.getItem().getLabel();
    }

	/*
    public String
    getValue()
    {
		return item_mapping.getHerbDosageDbItemMappingId() + "";
    }
	 */

    protected void
    insertObject()
		throws Exception
    {
		item_mapping.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		item_mapping.save();
    }
}