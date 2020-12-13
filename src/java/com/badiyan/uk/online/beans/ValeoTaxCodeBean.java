/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.beans.CompanyBean;
import com.badiyan.uk.beans.TaxCodeBean;
import com.badiyan.uk.exceptions.*;
import com.valeo.qb.data.ItemSalesTaxGroupRet;
import com.valeo.qb.data.ItemSalesTaxRet;
import com.valeo.qb.data.SalesTaxCodeRet;
import com.valeo.qb.data.VendorRet;
import java.math.BigDecimal;

import java.util.*;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
ValeoTaxCodeBean
	extends TaxCodeBean
	implements java.io.Serializable
{
	// CLASS VARIABLES
	
	public static final String SALES_TAX_FOR_ADD_REQUEST_NAME = "POS Sales Tax";
	public static final String SALES_TAX_FOR_ADD_REQUEST_DESC = "Tax item used for transactions created in Valonyx POS";
	
	// CLASS METHODS

	public static TaxCodeBean
	getSalesTaxForAddRequest(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(TaxCodePeer.COMPANY_ID, _company.getId());
		crit.add(TaxCodePeer.CODE, ValeoTaxCodeBean.SALES_TAX_FOR_ADD_REQUEST_NAME);
		List objList = TaxCodePeer.doSelect(crit);
		if (objList.size() == 1)
			return (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode((TaxCode)objList.get(0));
		else if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate sales tax for add request.");

		throw new UniqueObjectNotFoundException("Could not locate unique sales tax for add request.");
	}

	public static TaxCodeBean
	getTaxCode(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		ValeoTaxCodeBean code = (ValeoTaxCodeBean)codes.get(key);
		if (code == null)
		{
			Criteria crit = new Criteria();
			crit.add(TaxCodePeer.TAX_CODE_ID, _id);
			List objList = TaxCodePeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate code with id: " + _id);

			code = (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode((TaxCode)objList.get(0));
		}

		return code;
	}

	private static TaxCodeBean
	getTaxCode(TaxCode _code)
	{
		Integer key = new Integer(_code.getTaxCodeId());
		TaxCodeBean code = (TaxCodeBean)codes.get(key);
		if (code == null)
		{
			code = new ValeoTaxCodeBean(_code);
			codes.put(key, code);
		}

		return code;
	}

	public static TaxCodeBean
	getTaxCode(UKOnlineCompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(TaxCodePeer.COMPANY_ID, _company.getId());
		crit.add(TaxCodePeer.QB_LIST_I_D, _list_id);
		List objList = TaxCodePeer.doSelect(crit);
		if (objList.size() == 1)
			return (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode((TaxCode)objList.get(0));
		else if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate code with list id: " + _list_id);

		throw new UniqueObjectNotFoundException("Could not locate unique code with list id: " + _list_id);
	}

	public static TaxCodeBean
	getTaxCode(SalesTaxCodeRet _qbfs_sales_tax_code)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		// this static mapping crap is going to have to be made dynamic somehow
		
		//String sales_tax_code_list_id = _qbfs_sales_tax_code.getListID();
		//if (sales_tax_code_list_id.equals("80000001-1227016701")) // Taxable Sales
		//	return (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode();
		
		return _qbfs_sales_tax_code.getDefaultTaxCode();
		
		/*
		Criteria crit = new Criteria();
		crit.add(TaxCodePeer.COMPANY_ID, _qbfs_sales_tax_code.getCompany().getId());
		crit.add(TaxCodePeer.LIST_I_D, _qbfs_sales_tax_code.getListID());
		List objList = TaxCodePeer.doSelect(crit);
		if (objList.size() == 1)
			return (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode((TaxCode)objList.get(0));
		else if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate code with list id: " + _qbfs_sales_tax_code.getListID());
		
		Iterator itr = objList.iterator();
		while (itr.hasNext())
		{
			TaxCode obj = (TaxCode)itr.next();
			if (obj.getIsDefault() == (short)1)
				return (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode(obj);
		}
		
		// there's no default.  return the first in the list, I guess...
		
		return (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode((TaxCode)objList.get(0));
		*/

		//throw new UniqueObjectNotFoundException("Could not locate unique code with list id: " + _qbfs_sales_tax_code.getListID());
	}

	public static Vector
	getTaxCodes(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		Vector codes = new Vector();

		Criteria crit = new Criteria();
		crit.add(TaxCodePeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(TaxCodePeer.CODE);
		Iterator obj_itr = TaxCodePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			codes.addElement(ValeoTaxCodeBean.getTaxCode((TaxCode)obj_itr.next()));

		return codes;
	}

	// INSTANCE VARIABLES

	//private Vector tenders;

	// CONSTRUCTORS

    /**
     * The base, no-arg constructor.
     */
    public ValeoTaxCodeBean()
    {
		super();
    }

    /**
     * Construct a ValeoTaxCodeBean from an existing TaxCode.
     */
    public ValeoTaxCodeBean(TaxCode _code)
    {
		super(_code);
    }

	// INSTANCE METHODS


	public String
	getSalesTaxCodeListID()
	{
		return code.getListID();
	}

	public String
	getQBListID()
	{
		return code.getQbListID();
	}

	/*
	public Vector
	getTenders()
		throws TorqueException, ObjectNotFoundException
	{
		if (this.tenders == null)
		{
			this.tenders = new Vector();

			Criteria crit = new Criteria();
			crit.add(TenderRetDbOrderMappingPeer.ORDER_ID, this.getId());
			Iterator itr = TenderRetDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				TenderRetDbOrderMapping mapping_obj = (TenderRetDbOrderMapping)itr.next();
				TenderRet tender = TenderRet.getTender(mapping_obj.getTenderRetDbId());
				this.tenders.addElement(tender);
			}
		}
		return this.tenders;
	}

	private void
	saveTenders()
		throws TorqueException, Exception
	{
		if (this.tenders != null)
		{
			HashMap db_tenders_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(TenderRetDbOrderMappingPeer.ORDER_ID, this.getId());
			Iterator itr = TenderRetDbOrderMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{

				TenderRetDbOrderMapping value = (TenderRetDbOrderMapping)itr.next();
				Integer key = new Integer(value.getTenderRetDbId());
				db_tenders_hash.put(key, value);

				System.out.println("found existing ass... >" + key.intValue());
			}

			System.out.println("num members >" + this.tenders.size());

			itr = this.tenders.iterator();
			while (itr.hasNext())
			{
				TenderRet tender = (TenderRet)itr.next();
				Integer key = new Integer(tender.getId());
				TenderRetDbOrderMapping obj = (TenderRetDbOrderMapping)db_tenders_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("association does not exist in db.  need to create >" + key.intValue());

					obj = new TenderRetDbOrderMapping();
					obj.setTenderRetDbId(tender.getId());
					obj.setOrderId(this.getId());
					obj.save();
				}
			}

			itr = db_tenders_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				TenderRetDbOrderMapping obj = (TenderRetDbOrderMapping)db_tenders_hash.get(key);
				System.out.println("deleting...");
				TenderRetDbOrderMappingPeer.doDelete(obj);
			}
		}
	}
	 */


    protected void
    insertObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		super.insertObject();
		//this.saveTenders();
    }

	public void
	invalidate()
	{
		//super.invalidate();
		//this.tenders = null;
	}

	@Override
	public BigDecimal
	getPercentage()
	{
		if (this.isGroup())
		{
			BigDecimal perc = CUBean.zero;
			try
			{
				ItemSalesTaxGroupRet group = ItemSalesTaxGroupRet.getSalesTaxGroupItem(UKOnlineCompanyBean.getCompany(this.getCompanyId()), this.getQBListID());
				Iterator items = group.getSalesTaxItems().iterator();
				while (items.hasNext())
				{
					ItemSalesTaxRet item = (ItemSalesTaxRet)items.next();
					System.out.println("SALES TAX ITEM FOUND >" + item.getName());
					perc = perc.add(item.getTaxRate());
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
			return perc;
		}

		return code.getPercentage();
	}
	
	public boolean
	isChild(UKOnlineCompanyBean _company, ValeoTaxCodeBean _code)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (this.isGroup())
		{
			ItemSalesTaxGroupRet group = ItemSalesTaxGroupRet.getSalesTaxGroupItem(_company, this.getQBListID());
			Iterator items = group.getSalesTaxItems().iterator();
			while (items.hasNext())
			{
				ItemSalesTaxRet item = (ItemSalesTaxRet)items.next();
				if (_code.getQBListID() != null)
				{
					if (_code.getQBListID().equals(item.getListID()))
						return true;
				}
			}
		}

		return false;
	}
	
	public Vector
	getChildren(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		
		if (this.isGroup())
		{
			Iterator codes_itr = ValeoTaxCodeBean.getTaxCodes(_company).iterator();
			if (codes_itr.hasNext())
			{
				while (codes_itr.hasNext())
				{
					ValeoTaxCodeBean obj = (ValeoTaxCodeBean)codes_itr.next();
					if (!obj.isGroup())
					{
						if (this.isChild(_company, obj))
							vec.addElement(obj);
					}
				}
			}
		}

		return vec;
	}

	public void
	setSalesTaxCodeListID(String _list_id)
	{
		code.setListID(_list_id);
	}

	public void
	setQBListID(String _list_id)
	{
		code.setQbListID(_list_id);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		super.updateObject();
		//this.saveTenders();
    }

}
