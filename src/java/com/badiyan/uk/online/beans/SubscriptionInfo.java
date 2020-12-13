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
SubscriptionInfo
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,SubscriptionInfo> hash = new HashMap<Integer,SubscriptionInfo>(11);
	
	public static final int TRIAL_PERIOD_MONTHS = 0;
	public static final int TRIAL_PERIOD_WEEKS = 2;
	public static final int TRIAL_PERIOD_DAYS = 0;
	public static final String TRIAL_PERIOD_STRING = "";

    // CLASS METHODS

    public static SubscriptionInfo
    getSubscriptionInfo(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		SubscriptionInfo subscription_info = (SubscriptionInfo)hash.get(key);
		if (subscription_info == null)
		{
			Criteria crit = new Criteria();
			crit.add(SubscriptionInfoDbPeer.SUBSCRIPTION_INFO_DB_ID, _id);
			List objList = SubscriptionInfoDbPeer.doSelect(crit);
			if (objList.isEmpty())
				throw new ObjectNotFoundException("Could not locate Subscription Information with id: " + _id);

			subscription_info = SubscriptionInfo.getSubscriptionInfo((SubscriptionInfoDb)objList.get(0));
		}

		return subscription_info;
    }

    private static SubscriptionInfo
    getSubscriptionInfo(SubscriptionInfoDb _subscription_info)
		throws TorqueException
    {
		Integer key = new Integer(_subscription_info.getSubscriptionInfoDbId());
		SubscriptionInfo subscription_info = (SubscriptionInfo)hash.get(key);
		if (subscription_info == null)
		{
			subscription_info = new SubscriptionInfo(_subscription_info);
			hash.put(key, subscription_info);
		}

		return subscription_info;
    }

    public static SubscriptionInfo
    getSubscriptionInfo(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(SubscriptionInfoDbPeer.COMPANY_ID, _company.getId());
		List list = SubscriptionInfoDbPeer.doSelect(crit);
		if (list.size() == 1) {
			SubscriptionInfoDb obj = (SubscriptionInfoDb)list.get(0);
			return SubscriptionInfo.getSubscriptionInfo(obj);
		}
		else if (list.isEmpty()) {
			throw new ObjectNotFoundException("Subscription Information not found.");
		}
		else
			throw new UniqueObjectNotFoundException("Unique Subscription Information not found.");
    }

    // SQL

    /*
     *        <table name="SUBSCRIPTION_INFO_DB" idMethod="native">
				<column name="SUBSCRIPTION_INFO_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
				<column name="COMPANY_ID" required="true" type="INTEGER"/>

				<column name="IS_ACTIVE" type="SMALLINT" default="1"/>

				<column name="SUBSCRIPTION_START_DATE" required="true" type="DATE"/>
				<column name="SUBSCRIBER_ID" required="false" type="INTEGER"/>

				<foreign-key foreignTable="COMPANY">
					<reference local="COMPANY_ID" foreign="COMPANYID"/>
				</foreign-key>
			</table>

     */

    // INSTANCE VARIABLES

    private SubscriptionInfoDb subscription_info;
	private Vector orders;

    // CONSTRUCTORS

    public
    SubscriptionInfo()
    {
		subscription_info = new SubscriptionInfoDb();
		isNew = true;
    }

    public
    SubscriptionInfo(SubscriptionInfoDb _subscription_info)
    {
		subscription_info = _subscription_info;
		isNew = false;
    }

    // INSTANCE METHODS

	public UKOnlinePersonBean getSubscriber() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(subscription_info.getSubscriberId());
	}

	public void setSubscriber(UKOnlinePersonBean _subscriber) {
		subscription_info.setSubscriberId(_subscriber.getId());
	}

	public Date getSubscriptionStartDate() {
		return subscription_info.getSubscriptionStartDate();
	}

	public void setSubscriptionStartDate(Date _subscriptionStartDate) {
		subscription_info.setSubscriptionStartDate(_subscriptionStartDate);
	}

	public CompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return CompanyBean.getCompany(subscription_info.getCompanyId());
	}

	public void
	setCompany(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		subscription_info.setCompanyId(_company.getId());
	}

    public int
    getId()
    {
		return subscription_info.getSubscriptionInfoDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		if (subscription_info.getCompanyId() < 1)
			return "[NEW SUBSCRIPTION]";
		return this.getCompany().getLabel() + " - " + (this.isActive() ? "Active" : "Inactive");
    }

    public String
    getValue()
    {
		return subscription_info.getSubscriptionInfoDbId() + "";
    }

    protected void
    insertObject()
		throws Exception
    {
		subscription_info.save();
		this.saveOrders();
    }
	
	public boolean
	isActive()
	{
		return (subscription_info.getIsActive() == (short)1);
	}

	public void
	activate()
		throws TorqueException
	{
		subscription_info.setIsActive((short)1);
	}

	public void
	inActivate()
		throws TorqueException
	{
		subscription_info.setIsActive((short)0);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		subscription_info.save();
		this.saveOrders();
    }

	public Vector
	getOrders()
		throws TorqueException, ObjectNotFoundException
	{
		if (orders == null)
		{
			orders = new Vector();

			Criteria crit = new Criteria();
			crit.add(SubscriptionInfoDbOrderMappingPeer.SUBSCRIPTION_INFO_DB_ID, this.getId());
			Iterator itr = SubscriptionInfoDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				SubscriptionInfoDbOrderMapping obj = (SubscriptionInfoDbOrderMapping)itr.next();
				orders.addElement(ValeoOrderBean.getOrder(obj.getOrderId()));
			}
		}
		return orders;
	}

	public void
	setOrders(Vector _orders)
	{
		orders = _orders;
	}

	private void
    saveOrders()
		throws TorqueException, Exception
    {
		/*
		 *<table name="SUBSCRIPTION_INFO_DB_ORDER_MAPPING">
			<column name="SUBSCRIPTION_INFO_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
			<column name="ORDER_ID" primaryKey="true" required="true" type="INTEGER"/>

			<foreign-key foreignTable="SUBSCRIPTION_INFO_DB">
				<reference local="SUBSCRIPTION_INFO_DB_ID" foreign="SUBSCRIPTION_INFO_DB_ID"/>
			</foreign-key>
			<foreign-key foreignTable="PERSONORDER">
				<reference local="ORDER_ID" foreign="ORDERID"/>
			</foreign-key>
		</table>
		 */

		if (this.orders != null)
		{
			System.out.println("sizer >" + this.orders.size());

			HashMap db_order_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(SubscriptionInfoDbOrderMappingPeer.SUBSCRIPTION_INFO_DB_ID, this.getId());
			Iterator itr = SubscriptionInfoDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				SubscriptionInfoDbOrderMapping value = (SubscriptionInfoDbOrderMapping)itr.next();
				Integer key = new Integer(value.getOrderId());
				db_order_hash.put(key, value);
			}

			System.out.println("num orders >" + this.orders.size());
			itr = this.orders.iterator();
			while (itr.hasNext())
			{
				ValeoOrderBean order = (ValeoOrderBean)itr.next();
				Integer key = new Integer(order.getId());
				SubscriptionInfoDbOrderMapping obj = (SubscriptionInfoDbOrderMapping)db_order_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					//System.out.println("creating new ass for " + orderline);

					obj = new SubscriptionInfoDbOrderMapping();
					obj.setSubscriptionInfoDbId(this.getId());
					obj.setOrderId(key.intValue());
					obj.save();

				}
			}

			itr = db_order_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				SubscriptionInfoDbOrderMapping obj = (SubscriptionInfoDbOrderMapping)db_order_hash.get(key);
				System.out.println("del obj.getSubscriptionInfoDbId() >" + obj.getSubscriptionInfoDbId());
				System.out.println("del obj.getOrderId() >" + obj.getOrderId());
				SubscriptionInfoDbOrderMappingPeer.doDelete(obj);
			}
		}
    }
	
	public boolean
	isTrial() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MONTH, (SubscriptionInfo.TRIAL_PERIOD_MONTHS * -1));
		now.add(Calendar.WEEK_OF_YEAR, (SubscriptionInfo.TRIAL_PERIOD_WEEKS * -1));
		now.add(Calendar.DATE, (SubscriptionInfo.TRIAL_PERIOD_DAYS * -1));
		
		Calendar subscription_start = Calendar.getInstance();
		subscription_start.setTime(this.getSubscriptionStartDate());
		
		return now.before(subscription_start);
		
	}
	
	public int
	getDaysLeftInTrial() {
		
		if (!this.isTrial())
			return 0;
		
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MONTH, (SubscriptionInfo.TRIAL_PERIOD_MONTHS * -1));
		now.add(Calendar.WEEK_OF_YEAR, (SubscriptionInfo.TRIAL_PERIOD_WEEKS * -1));
		now.add(Calendar.DATE, (SubscriptionInfo.TRIAL_PERIOD_DAYS * -1));
		
		Calendar subscription_start = Calendar.getInstance();
		subscription_start.setTime(this.getSubscriptionStartDate());
		
		long diff = subscription_start.getTimeInMillis() - now.getTimeInMillis();
		
		return (int)(diff / (1000 * 60 * 60 * 24));
		
	}
}