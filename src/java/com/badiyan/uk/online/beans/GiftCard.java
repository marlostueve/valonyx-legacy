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
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
GiftCard
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	public static final short GIFT_CARD = 1;
	public static final short GIFT_CERTIFICATE = 2;

    protected static HashMap<Integer,GiftCard> hash = new HashMap<Integer,GiftCard>(11);

    // CLASS METHODS

    public static GiftCard
    getGiftCard(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		GiftCard gift_card = (GiftCard)hash.get(key);
		if (gift_card == null)
		{
			Criteria crit = new Criteria();
			crit.add(GiftCardDbPeer.GIFT_CARD_DB_ID, _id);
			List objList = GiftCardDbPeer.doSelect(crit);
			if (objList.isEmpty())
				throw new ObjectNotFoundException("Could not locate Gift Card with id: " + _id);

			gift_card = GiftCard.getGiftCard((GiftCardDb)objList.get(0));
		}

		return gift_card;
    }

    private static GiftCard
    getGiftCard(GiftCardDb _gift_card)
		throws TorqueException
    {
		Integer key = new Integer(_gift_card.getGiftCardDbId());
		GiftCard gift_card = (GiftCard)hash.get(key);
		if (gift_card == null)
		{
			gift_card = new GiftCard(_gift_card);
			hash.put(key, gift_card);
		}

		return gift_card;
    }

    public static GiftCard
    getGiftCard(ValeoOrderBean _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(GiftCardDbPeer.ORDER_ID, _order.getId());
		List objList = GiftCardDbPeer.doSelect(crit);
		if (objList.size() == 1)
			return GiftCard.getGiftCard((GiftCardDb)objList.get(0));
		else if (objList.isEmpty())
			throw new ObjectNotFoundException("Could not locate Gift Card for order: " + _order.getLabel());
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Gift Card for order: " + _order.getLabel());
    }

    public static GiftCard
    getGiftCard(String _card_number)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(GiftCardDbPeer.CARD_NUMBER, _card_number);
		crit.add(GiftCardDbPeer.TYPE, GiftCard.GIFT_CARD);
		List objList = GiftCardDbPeer.doSelect(crit);
		if (objList.size() == 1)
			return GiftCard.getGiftCard((GiftCardDb)objList.get(0));
		else if (objList.isEmpty())
			throw new ObjectNotFoundException("Could not locate Gift Card for : " + _card_number);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Gift Card for : " + _card_number);
    }

    public static GiftCard
    getGiftCertificate(String _card_number)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(GiftCardDbPeer.CARD_NUMBER, _card_number);
		crit.add(GiftCardDbPeer.TYPE, GiftCard.GIFT_CERTIFICATE);
		List objList = GiftCardDbPeer.doSelect(crit);
		if (objList.size() == 1)
			return GiftCard.getGiftCard((GiftCardDb)objList.get(0));
		else if (objList.isEmpty())
			throw new ObjectNotFoundException("Could not locate Gift Card for : " + _card_number);
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Gift Card for : " + _card_number);
    }

    // SQL

    /*
     *        <table name="GIFT_CARD_DB" idMethod="native">
				<column name="GIFT_CARD_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
				<column name="COMPANY_ID" required="true" type="INTEGER"/>
				<column name="ORDER_ID" primaryKey="true" required="true" type="INTEGER"/>

				<column name="CARD_NUMBER" required="true" type="VARCHAR" size="100"/>
				<column name="IS_ACTIVE" type="SMALLINT" default="1"/>
				<column name="ORIGINAL_AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>
				<column name="BALANCE" required="true" scale="2" size="7" type="DECIMAL"/>

				<column name="CREATION_DATE" required="true" type="DATE"/>
				<column name="MODIFICATION_DATE" required="false" type="DATE"/>
				<column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
				<column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

				<foreign-key foreignTable="COMPANY">
					<reference local="COMPANY_ID" foreign="COMPANYID"/>
				</foreign-key>
				<foreign-key foreignTable="PERSONORDER">
					<reference local="ORDER_ID" foreign="ORDERID"/>
				</foreign-key>
			</table>
     */

    // INSTANCE VARIABLES

    private GiftCardDb gift_card;
	private ValeoOrderBean primary_order;
	private Vector previous_orders;
	private Vector tenders;

    // CONSTRUCTORS

    public
    GiftCard()
    {
		gift_card = new GiftCardDb();
		isNew = true;
    }

    public
    GiftCard(GiftCardDb _gift_card)
    {
		gift_card = _gift_card;
		isNew = false;
    }

    // INSTANCE METHODS

	public String
	getCardNumberString()
	{
		String str = gift_card.getCardNumber();
		if (str == null)
			return "";
		return str;
	}

	public void
	setCardNumber(String _str)
		throws TorqueException
	{
		gift_card.setCardNumber(_str);
	}

	public ValeoOrderBean
	getOrder()
		throws TorqueException, ObjectNotFoundException
	{
		return (ValeoOrderBean)ValeoOrderBean.getOrder(gift_card.getOrderId());
	}

	public void
	setOrder(ValeoOrderBean _order)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		gift_card.setOrderId(_order.getId());
		gift_card.setCompanyId(_order.getCompany().getId());
	}

	public BigDecimal
	getBalance()
		throws TorqueException
	{
		return gift_card.getBalance();
	}

	public String
	getBalanceString()
		throws TorqueException
	{
		return gift_card.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public void
	setBalance(BigDecimal _v)
		throws TorqueException
	{
		gift_card.setBalance(_v);
	}

	public BigDecimal
	getOriginalAmount()
		throws TorqueException
	{
		return gift_card.getOriginalAmount();
	}

	public String
	getOriginalAmountString()
		throws TorqueException
	{
		return gift_card.getOriginalAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public void
	setOriginalAmount(BigDecimal _v)
		throws TorqueException
	{
		gift_card.setOriginalAmount(_v);
	}

    public int
    getId()
    {
		return gift_card.getGiftCardDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		ValeoOrderBean order = this.getOrder();
		UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(order.getPersonId());
		String label_str = this.getOriginalAmountString() + " Gift Card sold to " + person.getLabel() + " on " + order.getOrderDateString() + " with a remaining balance of " + this.getBalanceString();
		return label_str;
    }

    public String
    getValue()
    {
		return gift_card.getGiftCardDbId() + "";
    }

    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			gift_card.setCreatePersonId(_person.getId());
		else
			gift_card.setModifyPersonId(_person.getId());
    }

	public void
	setType(short _type)
		throws TorqueException
	{
		gift_card.setType(_type);
	}

    protected void
    insertObject()
		throws Exception
    {
		gift_card.setCreationDate(new Date());
		gift_card.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		gift_card.setModificationDate(new Date());
		gift_card.save();
    }
}