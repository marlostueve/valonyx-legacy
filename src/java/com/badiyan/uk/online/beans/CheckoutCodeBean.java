/*
 * CheckoutCodeBean.java
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.math.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.valeo.qb.data.AccountRet;
import com.valeo.qb.data.ItemGroupLine;
import com.valeo.qb.data.ItemGroupRet;
import com.valeo.qb.data.ItemRet;
import com.valeo.qb.data.ItemSalesTaxRet;
import com.valeo.qb.data.SalesTaxCodeRet;
import com.valeo.qb.data.VendorRet;
import com.valeo.qbpos.QBPOSXMLRequest;

import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author marlo
 */
public class
CheckoutCodeBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
	
	public static final String SUBTOTAL_FOR_ADD_REQUEST_NAME = "POS Subtotal";
	public static final String SUBTOTAL_FOR_ADD_REQUEST_DESC = "Subtotal used for transactions created in Valonyx POS";
	
	public static final short PROCEDURE_TYPE = 1;
    public static final short GROUP_TYPE = 2;
	public static final short PAYMENT_TYPE = 3;
	public static final short INVENTORY_TYPE = 4;
	public static final short RECEIVE_PAYMENT_TYPE = 5;
	public static final short PLAN_TYPE = 6;
	public static final short GIFT_CARD = 7;
	public static final short GIFT_CERTIFICATE = 8;
	public static final short SUBTOTAL = 9;
	public static final short NON_INVENTORY_TYPE = 10;

    protected static HashMap<Integer,CheckoutCodeBean> hash = new HashMap<Integer,CheckoutCodeBean>(11);

    // CLASS METHODS
	
	public static void
	delete(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.CHECKOUT_CODE_ID, _id);
		CheckoutCodePeer.doDelete(crit);
		
		Integer key = new Integer(_id);
		hash.remove(key);
	}
	
	public static CheckoutCodeBean
	getCheckoutCode(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		CheckoutCodeBean code = (CheckoutCodeBean)hash.get(key);
		if (code == null)
		{
			Criteria crit = new Criteria();
			crit.add(CheckoutCodePeer.CHECKOUT_CODE_ID, _id);
			List objList = CheckoutCodePeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate checkout code with id: " + _id);

			code = CheckoutCodeBean.getCheckoutCode((CheckoutCode)objList.get(0));
		}

		return code;
	}
	
	public static CheckoutCodeBean
	getCheckoutCodeForRequestID(String _requestID)
		throws TorqueException, ObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.REQUEST_I_D, _requestID);
		List objList = CheckoutCodePeer.doSelect(crit);
		if (objList.size() != 1)
			throw new ObjectNotFoundException("Could not locate checkout code for request: " + _requestID);

		return CheckoutCodeBean.getCheckoutCode((CheckoutCode)objList.get(0));
	}
	
	public static CheckoutCodeBean
	getCheckoutCodeForVendorAndProductId(VendorRet _vendor, int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.ITEM_NUMBER, _id);
		crit.add(CheckoutCodePeer.VENDOR_ID, _vendor.getId());
		List objList = CheckoutCodePeer.doSelect(crit);
		if (objList.size() != 1) {
			throw new ObjectNotFoundException("Could not locate checkout code for item number: " + _id);
		}
		return CheckoutCodeBean.getCheckoutCode((CheckoutCode)objList.get(0));
	}

    public static CheckoutCodeBean
    getCheckoutCode(CheckoutCode _code)
		throws TorqueException
    {
		Integer key = new Integer(_code.getCheckoutCodeId());
		CheckoutCodeBean code = (CheckoutCodeBean)hash.get(key);
		if (code == null)
		{
			code = new CheckoutCodeBean(_code);
			hash.put(key, code);
		}

		return code;
    }

    public static CheckoutCodeBean
    getCheckoutCode(CompanyBean _company, String _list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.LIST_I_D, _list_id);
		List obj_list = CheckoutCodePeer.doSelect(crit);
		if (obj_list.size() == 1)
			return CheckoutCodeBean.getCheckoutCode((CheckoutCode)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Cannot find Checkout Code for List ID >" + _list_id);
		else
			throw new UniqueObjectNotFoundException("Cannot find unique Checkout Code for List ID >" + _list_id);
    }

    public static CheckoutCodeBean
    getCheckoutCodeByQBListID(CompanyBean _company, String _qb_list_id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.QB_LIST_I_D, _qb_list_id);
		List obj_list = CheckoutCodePeer.doSelect(crit);
		if (obj_list.size() == 1)
			return CheckoutCodeBean.getCheckoutCode((CheckoutCode)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Cannot find Checkout Code for QB List ID >" + _qb_list_id);
		else
			throw new UniqueObjectNotFoundException("Cannot find unique Checkout Code for QB List ID >" + _qb_list_id);
    }

    public static CheckoutCodeBean
    getCheckoutCodeByDesc(CompanyBean _company, String _desc)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.DESCRIPTION, _desc);
		List obj_list = CheckoutCodePeer.doSelect(crit);
		if (obj_list.size() == 1)
			return CheckoutCodeBean.getCheckoutCode((CheckoutCode)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Cannot find Checkout Code for desc >" + _desc);
		else
			throw new ObjectNotFoundException("Cannot find unique Checkout Code for desc >" + _desc);
    }
    
    public static Vector
    getCheckoutCodes(CompanyBean _company)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
		}

		return vec;
    }
    
    public static Vector
    getCheckoutCodes(CompanyBean _company, short _type)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.TYPE, _type);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.VENDOR_ID);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodes(CompanyBean _company, String _desc)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		String search_string = "%" + _desc + "%";
		crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.VENDOR_ID);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodes(CompanyBean _company, String _desc, int _limit)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		String search_string = "%" + _desc + "%";
		crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.VENDOR_ID);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodesStartingWith(CompanyBean _company, String _desc, int _limit)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		String search_string = _desc + "%";
		crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.VENDOR_ID);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodes(CompanyBean _company, long _upc)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.UPC, _upc + "");
		crit.addAscendingOrderByColumn(CheckoutCodePeer.VENDOR_ID);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodes(CompanyBean _company, int _item_number)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.ITEM_NUMBER, _item_number);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		System.out.println("crit >" + crit.toString());
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodes(CompanyBean _company, String _desc, short _type)
		throws TorqueException
    {
		Vector vec = new Vector();

		String search_string = "%" + _desc + "%";

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
		crit.add(CheckoutCodePeer.TYPE, _type);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }
    
    public static Vector
    getCheckoutCodes(InventoryDepartment _department)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.INVENTORY_DEPARTMENT_DB_ID, _department.getId());
		crit.addAscendingOrderByColumn(CheckoutCodePeer.VENDOR_ID);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodesByDescAndVendor(CompanyBean _company, String _desc, String _vendor_desc)
		throws TorqueException
    {
		Vector vec = new Vector();

		String search_string = "%" + _desc + "%";
		String vendor_search_string = "%" + _vendor_desc + "%";

		Criteria crit = new Criteria();
		if (_vendor_desc.length() > 1)
			crit.addJoin(VendorRetDbPeer.VENDOR_RET_DB_ID, CheckoutCodePeer.VENDOR_ID);
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
		if (_vendor_desc.length() > 1)
			crit.add(VendorRetDbPeer.NAME, (Object)vendor_search_string, Criteria.LIKE);
		//crit.add(CheckoutCodePeer.TYPE, _type);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		System.out.println("crit >" + crit.toString());
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodesByDescAndVendor(CompanyBean _company, String _desc)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(CheckoutCodePeer.VENDOR_ID, VendorRetDbPeer.VENDOR_RET_DB_ID);
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		if (_desc != null) {
			String search_string = "%" + _desc + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(VendorRetDbPeer.NAME, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b));
		}
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		System.out.println("crit >" + crit.toString());
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj)) {
				vec.addElement(obj);
			}
		}

		return vec;
    }

    public static Vector
    getCheckoutCodesByDescAndVendor(CompanyBean _company, String _desc, VendorRet _vendor)
		throws TorqueException
    {
		Vector vec = new Vector();

		String search_string = "%" + _desc + "%";
		//String vendor_search_string = "%" + _vendor_desc + "%";

		Criteria crit = new Criteria();
		if (_vendor != null) {
			crit.addJoin(VendorRetDbPeer.VENDOR_RET_DB_ID, CheckoutCodePeer.VENDOR_ID);
		}
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
		if (_vendor != null) {
			//crit.add(VendorRetDbPeer.NAME, (Object)vendor_search_string, Criteria.LIKE);
			crit.add(VendorRetDbPeer.VENDOR_RET_DB_ID, _vendor.getId());
		}
		//crit.add(CheckoutCodePeer.TYPE, _type);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		System.out.println("crit >" + crit.toString());
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodesByDescAndVendor(CompanyBean _company, String _desc, VendorRet _vendor, String _searchType, boolean _show_only_low_stock)
		throws TorqueException
    {
		Vector vec = new Vector();
		
		if (_searchType.equals("search")) {
			
			Criteria crit = new Criteria();
			String search_string = "%" + _desc + "%";

			if (_vendor != null) {
				crit.addJoin(VendorRetDbPeer.VENDOR_RET_DB_ID, CheckoutCodePeer.VENDOR_ID);
			}
			crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
			crit.add(CheckoutCodePeer.TYPE, CheckoutCodeBean.INVENTORY_TYPE);
			
			//crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
			if (_desc != null) {
				//String search_string = "%" + _desc + "%";
				crit.addJoin(CheckoutCodePeer.VENDOR_ID, VendorRetDbPeer.VENDOR_RET_DB_ID);
				Criteria.Criterion crit_a = crit.getNewCriterion(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
				Criteria.Criterion crit_b = crit.getNewCriterion(VendorRetDbPeer.NAME, (Object)search_string, Criteria.LIKE);
				crit.add(crit_a.or(crit_b));
			}
			
			if (_vendor != null) {
				//crit.add(VendorRetDbPeer.NAME, (Object)vendor_search_string, Criteria.LIKE);
				crit.add(VendorRetDbPeer.VENDOR_RET_DB_ID, _vendor.getId());
			}
			//crit.add(CheckoutCodePeer.TYPE, _type);
			//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
			
			crit.add(CheckoutCodePeer.IS_ACTIVE, (short)1);
			crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
			System.out.println("crit >" + crit.toString());
			Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
				if (CheckoutCodeBean.isOKToReturnCode(obj)) {
					if (_show_only_low_stock) {
						if (obj.isLowInStock()) {
							vec.addElement(obj);
						}
					} else {
						vec.addElement(obj);
					}
				}

				//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
			}
			
		} else if (_searchType.equals("common")) {
			
			//crit.addJoin(CheckoutCodePeer.CHECKOUT_CODE_ID, CheckoutOrderlinePeer.CHECKOUT_CODE_ID);
			
			//String qs = "SELECT CHECKOUT_CODE_ID, COUNT('CHECKOUT_CODE_ID') AS value_occurrence FROM CHECKOUT_ORDERLINE GROUP BY CHECKOUT_CODE_ID ORDER BY value_occurrence DESC";
			StringBuilder qs = new StringBuilder();
			qs.append("SELECT CHECKOUT_ORDERLINE.CHECKOUT_CODE_ID, COUNT('CHECKOUT_ORDERLINE.CHECKOUT_CODE_ID') AS value_occurrence");
			qs.append(" FROM CHECKOUT_ORDERLINE, CHECKOUT_CODE");
			qs.append(" WHERE CHECKOUT_ORDERLINE.CHECKOUT_CODE_ID = CHECKOUT_CODE.CHECKOUT_CODE_ID");
			qs.append(" AND CHECKOUT_CODE.COMPANY_ID = 5");
			qs.append(" AND CHECKOUT_CODE.TYPE = 4");
			if (_vendor != null) {
				qs.append(" AND CHECKOUT_CODE.VENDOR_ID = " + _vendor.getId());
			}
			if (_desc != null) {
				qs.append(" AND CHECKOUT_CODE.DESCRIPTION LIKE \"%" + _desc + "%\"");
			}
			qs.append(" AND CHECKOUT_CODE.IS_ACTIVE = 1");
			qs.append(" GROUP BY CHECKOUT_ORDERLINE.CHECKOUT_CODE_ID");
			qs.append(" ORDER BY value_occurrence DESC");
			
			System.out.println("qs >" + qs.toString());
			
			Iterator itr = CheckoutOrderlinePeer.executeQuery(qs.toString()).iterator();
			while (itr.hasNext()) {
				try {
					int checkout_code_id = (((com.workingdogs.village.Record)itr.next()).getValue(1)).asInt();
					CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode(checkout_code_id);
					if (CheckoutCodeBean.isOKToReturnCode(obj)) {
						if (_show_only_low_stock) {
							if (obj.isLowInStock()) {
								vec.addElement(obj);
							}
						} else {
							vec.addElement(obj);
						}
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		} else if (_searchType.equals("wait")) {
			
			Criteria crit = new Criteria();
			String search_string = "%" + _desc + "%";

			crit.addJoin(ProductWaitListDbPeer.CHECKOUT_CODE_ID, CheckoutCodePeer.CHECKOUT_CODE_ID);
			if (_vendor != null) {
				crit.addJoin(CheckoutCodePeer.VENDOR_ID, VendorRetDbPeer.VENDOR_RET_DB_ID);
			}
			crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
			//if (_desc != null) {
			//	crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
			//}
			if (_desc != null) {
				//String search_string = "%" + _desc + "%";
				crit.addJoin(CheckoutCodePeer.VENDOR_ID, VendorRetDbPeer.VENDOR_RET_DB_ID);
				Criteria.Criterion crit_a = crit.getNewCriterion(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
				Criteria.Criterion crit_b = crit.getNewCriterion(VendorRetDbPeer.NAME, (Object)search_string, Criteria.LIKE);
				crit.add(crit_a.or(crit_b));
			}
			if (_vendor != null) {
				//crit.add(VendorRetDbPeer.NAME, (Object)vendor_search_string, Criteria.LIKE);
				crit.add(VendorRetDbPeer.VENDOR_RET_DB_ID, _vendor.getId());
			}
			crit.addAscendingOrderByColumn(ProductWaitListDbPeer.CREATION_DATE);
			System.out.println("crit >" + crit.toString());
			Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
				if (CheckoutCodeBean.isOKToReturnCode(obj)) {
					if (_show_only_low_stock) {
						if (obj.isLowInStock()) {
							vec.addElement(obj);
						}
					} else {
						vec.addElement(obj);
					}
				}

				//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
			}
		}

		return vec;
    }
	
	
	
	
	/*
	public static Vector
	getPersonsByKeyword(CompanyBean _company, String _keyword, UKOnlinePersonBean _logged_in_person, int _limit)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		boolean restrict_search_to_logged_in_person_department = false;

		Criteria crit = new Criteria();
		if (!restrict_search_to_logged_in_person_department) {
			crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		}
		crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		crit.addJoin(DepartmentpersonPeer.PERSONID, PersonPeer.PERSONID);
		crit.addJoin(PersonPeer.PERSON_TITLE_ID, PersonTitlePeer.PERSON_TITLE_ID);
		if (restrict_search_to_logged_in_person_department && (_logged_in_person != null)) {
			crit.add(DepartmentPeer.DEPARTMENTID, _logged_in_person.getDepartment().getId());
		} else {
			crit.add(CompanyPeer.COMPANYID, _company.getId());
		}

		if (_keyword != null) {
			String search_string = _keyword + "%";
			//crit.add(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			//crit.or(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			//crit.or(DepartmentPeer.DEPARTMENTNAME, (Object)search_string, Criteria.LIKE);

			Criteria.Criterion crit_a = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.EMPLOYEEID, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_d = crit.getNewCriterion(DepartmentPeer.DEPARTMENTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_e = crit.getNewCriterion(PersonTitlePeer.TITLE, (Object)search_string, Criteria.LIKE);

			crit.add(crit_a.or(crit_b).or(crit_c).or(crit_d).or(crit_e));
			//crit.add(crit_a.or(crit_b));
		}
		
		crit.add(PersonPeer.ISACTIVE, (short)1);

		crit.addAscendingOrderByColumn(PersonPeer.LASTNAME);
		
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		//System.out.println("crit .. >" + crit.toString());

		Iterator itr = PersonPeer.doSelect(crit).iterator();
		Vector people = new Vector();
		while (itr.hasNext()) {
			UKOnlinePersonBean anoka_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson((Person)itr.next());
			
			people.addElement(anoka_person);
		}
		return people;
	}
	*/
	
	

    public static Vector
    getCheckoutCodesByDescAndVendor(CompanyBean _company, String _desc, String _vendor_desc, int _limit)
		throws TorqueException
    {
		Vector vec = new Vector();

		String search_string = "%" + _desc + "%";
		String vendor_search_string = "%" + _vendor_desc + "%";

		Criteria crit = new Criteria();
		if (_vendor_desc.length() > 1)
			crit.addJoin(VendorRetDbPeer.VENDOR_RET_DB_ID, CheckoutCodePeer.VENDOR_ID);
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
		if (_vendor_desc.length() > 1)
			crit.add(VendorRetDbPeer.NAME, (Object)vendor_search_string, Criteria.LIKE);
		//crit.add(CheckoutCodePeer.TYPE, _type);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		crit.setLimit(_limit);
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodesByDescAndVendor(CompanyBean _company, String _desc, String _vendor_desc, short _type)
		throws TorqueException
    {
		Vector vec = new Vector();

		String search_string = "%" + _desc + "%";
		String vendor_search_string = "%" + _vendor_desc + "%";

		Criteria crit = new Criteria();
		if (_vendor_desc.length() > 1)
			crit.addJoin(VendorRetDbPeer.VENDOR_RET_DB_ID, CheckoutCodePeer.VENDOR_ID);
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
		if (_vendor_desc.length() > 1)
			crit.add(VendorRetDbPeer.NAME, (Object)vendor_search_string, Criteria.LIKE);
		crit.add(CheckoutCodePeer.TYPE, _type);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		//System.out.println("crit >" + crit.toString());
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    private static Vector
    getCheckoutCodesForDesc(CompanyBean _company, String _desc)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.DESCRIPTION, _desc);
		//crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNOTNULL);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodesByDescAndDepartment(CompanyBean _company, String _desc, InventoryDepartment _department)
		throws TorqueException
    {
		//System.out.println("_descX >" + _desc);
		
		String search_string = "%" + _desc + "%";
		
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.INVENTORY_DEPARTMENT_DB_ID, _department.getId());
		if (_desc.length() > 1)
			crit.add(CheckoutCodePeer.DESCRIPTION, (Object)search_string, Criteria.LIKE);
		crit.addAscendingOrderByColumn(CheckoutCodePeer.DESCRIPTION);
		//System.out.println("critX >" + crit.toString());
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
			
			//vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next()));
		}

		return vec;
    }

    public static Vector
    getCheckoutCodes(CompanyBean _company, ItemRet _obj)
		throws TorqueException
    {
		Vector vec = new Vector();

		try
		{
			CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCodeByQBListID(_company, _obj.getListID());
			vec.addElement(checkout_code);
			return vec;
		}
		catch (Exception x)
		{
			int item_number = -1;
			String name_str = _obj.getName();
			try
			{
				if (name_str.indexOf("#") > -1)
					item_number = Integer.parseInt(name_str.substring(name_str.indexOf("#") + 1));
			}
			catch (Exception y)
			{
			}
			
			//System.out.println("ITEM NUMBER FOUND FOR " + name_str + " >" + item_number);

			if (item_number != -1)
			{
				vec = CheckoutCodeBean.getCheckoutCodes(_company, item_number);
				if (vec.size() == 1)
					return vec;
			}
			

			String desc_string = _obj.getName();
			//System.out.println("desc_string >" + desc_string);
			Vector matches = CheckoutCodeBean.getCheckoutCodesForDesc(_company, desc_string);
			if (matches.size() > 0)
				return matches;
				

			return vec;
		}
    }

    public static Vector
    getCheckoutCodes(CompanyBean _company, ItemGroupRet _obj)
		throws TorqueException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();

		try
		{
			CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCodeByQBListID(_company, _obj.getListID());
			vec.addElement(checkout_code);
		}
		catch (ObjectNotFoundException x)
		{
			
		}
		
		return vec;
    }
	
	public static Vector
	getCheckoutCodesModifiedAfter(CompanyBean _company, Date _date)
		throws TorqueException
	{
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.MODIFICATION_DATE, _date, Criteria.GREATER_THAN);
		System.out.println("getCheckoutCodesModifiedAfter crit >" + crit.toString());
		Iterator itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			CheckoutCodeBean obj = CheckoutCodeBean.getCheckoutCode((CheckoutCode)itr.next());
			if (CheckoutCodeBean.isOKToReturnCode(obj))
				vec.addElement(obj);
		}

		return vec;
	}
    
    public static CheckoutCodeBean
    getSubtotalForAddRequest(CompanyBean _company)
		throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.TYPE, CheckoutCodeBean.SUBTOTAL);
		crit.add(CheckoutCodePeer.DESCRIPTION, CheckoutCodeBean.SUBTOTAL_FOR_ADD_REQUEST_NAME);
		List objList = CheckoutCodePeer.doSelect(crit);
		if (objList.size() > 0)
			return CheckoutCodeBean.getCheckoutCode((CheckoutCode)objList.get(0));
		
		CheckoutCodeBean subtotal_code = new CheckoutCodeBean();
		subtotal_code.setDescription(CheckoutCodeBean.SUBTOTAL_FOR_ADD_REQUEST_NAME);
		subtotal_code.setSalesDescription(CheckoutCodeBean.SUBTOTAL_FOR_ADD_REQUEST_DESC);
		subtotal_code.setType(CheckoutCodeBean.SUBTOTAL);
		subtotal_code.setIsActive(true);
		subtotal_code.setCompany(_company);
		subtotal_code.save();
		
		return subtotal_code;
    }

    public static int
    getNextItemNumber(CompanyBean _company)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.addDescendingOrderByColumn(CheckoutCodePeer.ITEM_NUMBER);
		List obj_list = CheckoutCodePeer.doSelect(crit);
		if (obj_list.size() > 0)
		{
			CheckoutCode code = (CheckoutCode)obj_list.get(0);
			return (code.getItemNumber() + 1);
		}
		else
			return 1;
    }

    public static Vector
    getNewCheckoutCodes(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(CheckoutCodePeer.COMPANY_ID, _company.getId());
		crit.add(CheckoutCodePeer.QB_LIST_I_D, (Object)"", Criteria.ISNULL);
		crit.add(CheckoutCodePeer.LIST_I_D, (Object)"", Criteria.ISNULL);
		//System.out.println("crit >" + crit.toString());
		Iterator obj_itr = CheckoutCodePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(CheckoutCodeBean.getCheckoutCode((CheckoutCode)obj_itr.next()));

		return vec;
    }
	
	private static boolean
	isOKToReturnCode(CheckoutCodeBean _obj)
	{
		if (_obj.getQBListID() == null)
		{
			if (_obj.getListID() == null)
				return true;
			return false;
		}
		else
			return true;
	}
    
    // SQL
    
    /*
     * <table name="CHECKOUT_CODE" idMethod="native">
    <column name="CHECKOUT_CODE_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="CODE" required="true" type="VARCHAR" size="50"/>
    <column name="DESCRIPTION" required="true" type="VARCHAR" size="255"/>
    <column name="AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="TYPE" required="true" type="SMALLINT"/>
    <column name="PRACTICE_AREA_ID" required="false" type="INTEGER" />
	
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
	
    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PRACTICE_AREA">
		<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
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
	
    private CheckoutCode code;
	private HashMap child_codes;
    
    // CONSTRUCTORS
    
    public
    CheckoutCodeBean()
    {
		code = new CheckoutCode();
		isNew = true;
    }
    
    public
    CheckoutCodeBean(CheckoutCode _code)
    {
		code = _code;
		isNew = false;
    }
    
    // INSTANCE METHODS
    
    public boolean
    equals(Object _obj)
    {
		if (_obj == null)
			return false;
		if (_obj instanceof CheckoutCodeBean)
			return (this.getId() == ((CheckoutCodeBean)_obj).getId());
		else
			return false;
    }

    public BigDecimal
    getAmount()
    {
		return code.getAmount();
    }
    
    public String
    getAmountString()
    {
		if (code.getAmount() == null)
			return "";
		return code.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

	public int
	getAssetAccountId()
	{
		return code.getAssetAccountId();
	}

	public short
	getAvailableQuantity()
	{
		return code.getAvailableQuantity();
	}

    public String
    getAverageUnitCostString()
    {
		if (code.getAverageUnitCost() != null)
			return code.getAverageUnitCost().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		else
			return "";
    }
	
	public String
	getCode()
	{
		return code.getCode();
	}

	public String
	getCodeString()
	{
		String str = code.getCode();
		if (str == null)
			return "";
		return str;
	}
    
    public CompanyBean
    getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return CompanyBean.getCompany(code.getCompanyId());
    }

	public int
	getCOGSAccountId()
	{
		return code.getCOGSAccountId();
	}
	
	public String
	getDescription()
	{
		return code.getDescription();
	}

	public String
	getDescriptionString()
	{
		String str = code.getDescription();
		if (str == null)
			return "";
		return str;
	}
	
	public String
	getSalesDescription()
	{
		return code.getSalesDesc();
	}

	public String
	getSalesDescriptionString()
	{
		String str = code.getSalesDesc();
		if (str == null)
			return this.getDescriptionString();
		return str;
	}
    
    public int
    getId()
    {
		return code.getCheckoutCodeId();
    }

	public int
	getIncomeAccountId()
	{
		return code.getIncomeAccountId();
	}

	public int
	getExpenseAccountId()
	{
		return code.getExpenseAccountId();
	}

    public int
    getItemNumber()
    {
		return code.getItemNumber();
    }

    public String
    getItemNumberString()
    {
		if (code.getItemNumber() == 0)
			return "";
		return code.getItemNumber() + "";
    }
    
    public String
    getLabel() {
		String str = code.getDescription();
		if (str == null) {
			return "";
		}
		return str;
    }
    
    public String
    getLabelJavascript()
    {
		return StringEscapeUtils.escapeJavaScript(code.getDescription());
    }

    public String
    getListID()
    {
		return code.getListID();
    }

    public BigDecimal
    getOrderCost()
    {
		return code.getOrderCost();
    }

    public String
    getEditSequence()
    {
		return code.getEditSequence();
    }

    public String
    getQBListID()
    {
		return code.getQbListID();
    }

	public short
	getOnHandQuantity()
	{
		return code.getOnHandQuantity();
	}

	public String
	getOnHandQuantityString() {
		short qty = code.getOnHandQuantity();
		if (qty < 0) {
			return "0";
		}
		return ( qty + "" );
	}

    public String
    getOrderCostString()
    {
		if (code.getOrderCost() != null)
			return code.getOrderCost().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
		else
			return "";
    }

	public PracticeAreaBean
	getPracticeArea()
		throws TorqueException, ObjectNotFoundException
	{
		return PracticeAreaBean.getPracticeArea(code.getPracticeAreaId());
	}
	
	public int
	getPracticeAreaId()
	{
		return code.getPracticeAreaId();
	}
	
	public String
	getPracticeAreaString() throws TorqueException, ObjectNotFoundException {
		
		String practice_area_str = "[NOT FOUND]";
		int practice_area_id = code.getPracticeAreaId();
		if (practice_area_id > 0) {
			PracticeAreaBean practice_area = PracticeAreaBean.getPracticeArea(practice_area_id);
			practice_area_str = practice_area.getLabel();
		}
		
		return practice_area_str;
	}

	public short
	getReorderPoint()
	{
		return code.getReorderPoint();
	}

    public BigDecimal
    getTaxAmount(BigDecimal _amount)
		throws TorqueException, UniqueObjectNotFoundException
    {
		if (code.getAmount() == null)
			return CUBean.zero;
		//if (code.getTaxCodeId() == 0)
		//	return CUBean.zero;
		
		try
		{
			BigDecimal perc = this.getTaxCode().getPercentage().divide(CUBean.one_hundred);
			//BigDecimal perc = new BigDecimal(this.getTaxPercentageString());
			return _amount.multiply(perc).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		catch (ObjectNotFoundException x)
		{
			return CUBean.zero;
		}
    }

    public BigDecimal
    getTaxAmountNoRound(BigDecimal _amount)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		if (code.getAmount() == null)
			return CUBean.zero;
		//if (code.getTaxCodeId() == 0)
		//	return CUBean.zero;
		BigDecimal perc = this.getTaxCode().getPercentage().divide(CUBean.one_hundred);
		//BigDecimal perc = new BigDecimal(this.getTaxPercentageString());
		return _amount.multiply(perc);
    }

    public String
    getTaxAmountString()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		if (code.getAmount() == null)
			return "";
		//if (code.getTaxCodeId() == 0)
		//	return "0.00";
		BigDecimal perc = this.getTaxCode().getPercentage().divide(CUBean.one_hundred);
		//BigDecimal perc = new BigDecimal(this.getTaxPercentageString());
		return code.getAmount().multiply(perc).setScale(2, BigDecimal.ROUND_HALF_UP).toString();

    }

	public ValeoTaxCodeBean
	getTaxCode()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		// return (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode(code.getTaxCodeId());
		
		SalesTaxCodeRet sales_tax_code_ret = SalesTaxCodeRet.getSalesTaxCode(this.getSalesTaxCodeId());
		
		// is there a default Tax Item for this sales Tax Code?
		
		//ValeoTaxCodeBean tax_code = (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode(sales_tax_code_ret);
		
		ValeoTaxCodeBean tax_code = sales_tax_code_ret.getDefaultTaxCode();
		
		return tax_code;
	}

	public String
	getTaxCodeString()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		// return (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode(code.getTaxCodeId());
		
		String taxCodeString = "";
		try {
			SalesTaxCodeRet sales_tax_code_ret = SalesTaxCodeRet.getSalesTaxCode(this.getSalesTaxCodeId());
			ValeoTaxCodeBean tax_code = sales_tax_code_ret.getDefaultTaxCode();
			taxCodeString = tax_code.getLabel();
		} catch (Exception x) {}
		
		return taxCodeString;
	}

	public int
	getTaxCodeId()
		throws TorqueException, ObjectNotFoundException
	{
		return code.getTaxCodeId();
	}

    public String
    getTaxPercentageString()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		/*
		if (code.getTaxCodeId() == 0)
			return "0.00";
		return this.getTaxCode().getPercentage().toString();
		 */
		
		if (code.getSalesTaxCodeRetDbId() == 0)
			return "0.00";
		
		try
		{
			return this.getTaxCode().getPercentage().toString();
		}
		catch (ObjectNotFoundException x)
		{
			System.out.println(this.getLabel() + " TAX CODE ERROR >" + x.getMessage());
			x.printStackTrace();
		}
		
		return "0.00";
		
		// this is lame and needs to be made to be dynamic
		// I'm still very much confused about how ItemSalesTaxRet maps to SalesTaxCodeRet
		
		/*
		
		if (code.getSalesTaxCodeRetDbId() < 1)
			return "0.00";
		
		switch (code.getSalesTaxCodeRetDbId()) {
			case 1: return "2.00";
			case 2: return "6.75";
			case 3: return "6.75";
			case 4: return "6.75";
			case 5: return "0.00";
			case 6: return "0.00";
		}
		
		return "0.00";
		 */
    }
    
    public short
    getType() {
		return code.getType();
    }
	
	public String
	getTypeString() {
		
		String type_str = "[NOT FOUND]";
		switch (code.getType()) {
			case CheckoutCodeBean.PROCEDURE_TYPE: type_str = "Service"; break;
			case CheckoutCodeBean.INVENTORY_TYPE: type_str = "Inventory"; break;
			case CheckoutCodeBean.NON_INVENTORY_TYPE: type_str = "Non Inv."; break;
			case CheckoutCodeBean.GROUP_TYPE: type_str = "Group"; break;
			case CheckoutCodeBean.PLAN_TYPE: type_str = "Plan"; break;
			case CheckoutCodeBean.GIFT_CARD: type_str = "Gift Card"; break;
			case CheckoutCodeBean.GIFT_CERTIFICATE: type_str = "Gift Cert."; break;
			case CheckoutCodeBean.PAYMENT_TYPE: type_str = "Payment"; break;
			case CheckoutCodeBean.SUBTOTAL: type_str = "Subtotal"; break;
			case CheckoutCodeBean.RECEIVE_PAYMENT_TYPE: type_str = "Rec. Pmt."; break;
		}
		
		return type_str;
	}

    public String
    getUPCString()
    {
		String str = code.getUpc();
		if (str == null)
			return "";
		return str;
    }
    
    public String
    getValue()
    {
		return code.getCheckoutCodeId() + "";
    }

	public VendorRet
	getVendor()
		throws TorqueException, ObjectNotFoundException
	{
		return VendorRet.getVendor(code.getVendorId());
	}

	public int
	getVendorId()
	{
		return code.getVendorId();
	}

	public boolean
	hasVendor() {
		return ( code.getVendorId() > 0 );
	}

	public String
	getVendorString()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (code.getVendorId() == 0)
			return "";
		VendorRet vendor = VendorRet.getVendor(code.getVendorId());
		return vendor.getLabel();
	}

	public int
	getDepartmentId()
	{
		return code.getInventoryDepartmentDbId();
	}

	public String
	getDepartmentString()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (code.getInventoryDepartmentDbId() == 0)
			return "";
		InventoryDepartment department = InventoryDepartment.getInventoryDepartment(code.getInventoryDepartmentDbId());
		return department.getLabel();
	}
    
    protected void
    insertObject()
		throws Exception
    {	
		code.setCreationDate(new Date());
		code.save();

		this.saveChildCodes();
    }

	public boolean
	isActive()
	{
		return (code.getIsActive() == (short)1);
	}

	public boolean
	isCommissionable()
	{
		return (code.getIsCommissionable() == (short)1);
	}
	
	public boolean
	isPayment()
	{
		return (code.getType() == CheckoutCodeBean.PAYMENT_TYPE);
	}

	public boolean
	isPlanUse()
	{
		return (code.getIsPlanUse() == (short)1);
	}

	public boolean
	isReimbursable()
	{
		return (code.getIsReimbursable() == (short)1);
	}

	public boolean
	isSynced() {
		return (code.getQbListID() != null);
	}

	public boolean
	isTaxable()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		//return (code.getTaxCodeId() > 0);
		
		if (code.getSalesTaxCodeRetDbId() > 0)
		{
			SalesTaxCodeRet sales_tax_code_ret = SalesTaxCodeRet.getSalesTaxCode(this.getSalesTaxCodeId());
			return sales_tax_code_ret.isTaxable();
		}
		else
			return false;
	}

	public boolean
	isTippable()
	{
		//return (code.getIsTippable() == (short)1);
		return false;
	}

    public void
    removeTaxCode()
		throws TorqueException
    {
		code.setTaxCodeId(0);
    }

    public void
    removeSalesTaxCode()
		throws TorqueException
    {
		code.setSalesTaxCodeRetDbId(0);
    }

    public void
    removeVendor()
		throws TorqueException
    {
		code.setVendorId(0);
    }

    public void
    removeDepartment()
		throws TorqueException
    {
		code.setInventoryDepartmentDbId(0);
    }
    
    public void
    setAmount(BigDecimal _amount)
    {
		code.setAmount(_amount);
    }

	public void
	setAssetAccount(AccountRet _account)
		throws TorqueException
	{
		code.setAssetAccountId(_account.getId());
	}

	public void
	setAvailableQuantity(short _quantity)
	{
		code.setAvailableQuantity(_quantity);
	}

    public void
    setAverageUnitCost(BigDecimal _amount)
    {
		code.setAverageUnitCost(_amount);
    }
    
    public void
    setCode(String _code)
    {
		code.setCode(_code);
    }

	public void
	setCommissionable(boolean _is_commissionable)
	{
		code.setIsCommissionable(_is_commissionable ? (short)1 : (short)0);
	}
    
    public void
    setCompany(CompanyBean _company)
		throws TorqueException
    {
		code.setCompanyId(_company.getId());
    }

	public void
	setCostOfGoodsSoldAccount(AccountRet _account)
		throws TorqueException
	{
		code.setCOGSAccountId(_account.getId());
	}

	public void
	setExpenseAccount(AccountRet _account)
		throws TorqueException
	{
		code.setExpenseAccountId(_account.getId());
	}
    
    public void
    setCreatePerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		code.setCreatePersonId(_person.getId());
    }
	
    public void
    setModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		code.setModifyPersonId(_person.getId());
    }
	
    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			code.setCreatePersonId(_person.getId());
		else
			code.setModifyPersonId(_person.getId());
    }
	
    
    public void
    setDescription(String _desc)
    {
		code.setDescription(_desc);
    }
    
    public void
    setSalesDescription(String _desc)
    {
		code.setSalesDesc(_desc);
    }

	public void
	setIncomeAccount(AccountRet _account)
		throws TorqueException
	{
		code.setIncomeAccountId(_account.getId());
	}
    
    public void
    setItemNumber(int _id)
		throws TorqueException
    {
		code.setItemNumber(_id);
    }
	
	public void
	setEditSequence(String _str)
	{
		code.setEditSequence(_str);
	}
	
	public void
	setIsActive(boolean _b)
	{
		code.setIsActive(_b ? (short)1 : (short)0);
	}

	public void
	setIsReimbursable(boolean _is_reimbursable)
	{
		code.setIsReimbursable(_is_reimbursable ? (short)1 : (short)0);
	}
	
	public int
	getSalesTaxCodeId()
	{
		return code.getSalesTaxCodeRetDbId();
	}
	
	public void
	setSalesTaxCode(SalesTaxCodeRet _sales_tax_code)
		throws TorqueException
	{
		code.setSalesTaxCodeRetDbId(_sales_tax_code.getId());
	}
	
	public void
	setItemSalesTax(ItemSalesTaxRet _item_sales_tax)
		throws TorqueException
	{
		code.setItemSalesTaxRetDbId(_item_sales_tax.getId());
	}
	
	public short
	getQBItemType()
		throws IllegalValueException
	{
		if (code.getQbItemType() == 0)
			return getQBItemTypeForCurrentType();
			
		return code.getQbItemType();
	}
	
	private short
	getQBItemTypeForCurrentType()
		throws IllegalValueException
	{
		/*
		<option value="<%= CheckoutCodeBean.PROCEDURE_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.PROCEDURE_TYPE) ? " selected" : ""  %>>Service</option>
		<option value="<%= CheckoutCodeBean.INVENTORY_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.INVENTORY_TYPE) ? " selected" : ""  %>>Inventory</option>
		<option value="<%= CheckoutCodeBean.NON_INVENTORY_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.NON_INVENTORY_TYPE) ? " selected" : ""  %>>Non Inventory</option>
		<option value="<%= CheckoutCodeBean.GROUP_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.GROUP_TYPE) ? " selected" : ""  %>>Group</option>
		<option value="<%= CheckoutCodeBean.GIFT_CARD %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.GIFT_CARD) ? " selected" : ""  %>>Gift Card</option>
		<option value="<%= CheckoutCodeBean.GIFT_CERTIFICATE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.GIFT_CERTIFICATE) ? " selected" : ""  %>>Gift Certificate</option>
		<!-- <option value="<%= CheckoutCodeBean.PAYMENT_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.PAYMENT_TYPE) ? " selected" : ""  %>>Payment</option> -->
		<option value="<%= CheckoutCodeBean.PLAN_TYPE %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.PLAN_TYPE) ? " selected" : ""  %>>Payment Plan</option>
		<option value="<%= CheckoutCodeBean.SUBTOTAL %>"<%= (adminCheckoutCode.getType() == CheckoutCodeBean.SUBTOTAL) ? " selected" : ""  %>>Subtotal</option>
		 * 
		 */
		
		switch (this.getType())
		{
			case CheckoutCodeBean.PROCEDURE_TYPE: return ItemRet.SERVICE_TYPE;
			case CheckoutCodeBean.INVENTORY_TYPE: return ItemRet.INVENTORY_TYPE;
			case CheckoutCodeBean.NON_INVENTORY_TYPE: return ItemRet.NON_INVENTORY_TYPE;
			case CheckoutCodeBean.GROUP_TYPE: return ItemRet.GROUP_TYPE;
			case CheckoutCodeBean.PAYMENT_TYPE: return ItemRet.PAYMENT_TYPE;
			case CheckoutCodeBean.PLAN_TYPE: return ItemRet.INVENTORY_TYPE;
		}
		
		throw new IllegalValueException("Unable to determine QB item type for type >" + this.getType());
	}
	
	public void
	setQBItemType(short _type)
	{
		code.setQbItemType(_type);
	}
	
	public boolean
	setItemRet(ItemRet _obj, boolean _force_update)
		throws TorqueException, UniqueObjectNotFoundException
	{
		boolean modified = false;
		
		Date item_modification_date = _obj.getTimeModified();
		Date code_modification_date = code.getModificationDate();
		if (_force_update || ( (code_modification_date == null) || item_modification_date.after(code_modification_date) ) )
		{
			// the passed object has been modified since this checkout code has
			
			if (code.getQbItemType() != _obj.getItemType())
			{
				code.setQbItemType(_obj.getItemType());
				modified = true;
			}
			
			if (code.getEditSequence() == null || !code.getEditSequence().equals(_obj.getEditSequence()))
			{
				code.setEditSequence(_obj.getEditSequence());
				modified = true;
			}
			
			if (code.getListID() == null || !code.getListID().equals(_obj.getListID()))
			{
				code.setListID(_obj.getListID());
				modified = true;
			}
			
			if (code.getQbListID() == null || !code.getQbListID().equals(_obj.getListID()))
			{
				code.setQbListID(_obj.getListID());
				modified = true;
			}
			
			if (code.getDescription() == null || !code.getDescription().equals(_obj.getName()))
			{
				code.setDescription(_obj.getName());
				modified = true;
			}
			
			if (code.getOrderCost() == null || code.getOrderCost().floatValue() != _obj.getPurchaseCost())
			{
				code.setOrderCost(new BigDecimal(_obj.getPurchaseCost()));
				modified = true;
			}
			
			if (code.getAmount() == null || code.getAmount().floatValue() != _obj.getSalesPrice())
			{
				code.setAmount(new BigDecimal(_obj.getSalesPrice()));
				modified = true;
			}
			
			if (_obj.isSalesAndPurchase() && !this.isReimbursable())
			{
				this.setIsReimbursable(true);
				modified = true;
			}
			
			if (_obj.isSalesOrPurchase() && this.isReimbursable())
			{
				this.setIsReimbursable(false);
				modified = true;
			}
			
			System.out.println("_obj.getSalesTaxListID() >" + _obj.getSalesTaxListID());
			if (_obj.getSalesTaxListID() != null)
			{
				try
				{
					SalesTaxCodeRet sales_tax_code = SalesTaxCodeRet.getSalesTaxCode(this.getCompany(), _obj.getSalesTaxListID());
					System.out.println("code.getSalesTaxCodeRetDbId() >" + code.getSalesTaxCodeRetDbId());
					System.out.println("sales_tax_code >" + sales_tax_code.getId());
					if (code.getSalesTaxCodeRetDbId() != sales_tax_code.getId())
					{
						code.setSalesTaxCodeRetDbId(sales_tax_code.getId());
						modified = true;
					}
				}
				catch (ObjectNotFoundException x)
				{
					x.printStackTrace();
				}
			}
			
			System.out.println("_obj.getVendorListID() >" + _obj.getVendorListID());
			if (_obj.getVendorListID() != null)
			{
				VendorRet existing_vendor = null;
				try
				{
					existing_vendor = this.getVendor();
				}
				catch (ObjectNotFoundException x)
				{
				}
				if (existing_vendor == null || !existing_vendor.getListID().equals(_obj.getVendorListID()))
				{
					try
					{
						VendorRet vendor_ret = VendorRet.getVendor(this.getCompany(), _obj.getVendorListID());
						code.setVendorId(vendor_ret.getId());
						modified = true;
					}
					catch (ObjectNotFoundException x)
					{
						x.printStackTrace();
					}
				}
			}
			
			System.out.println("_obj.getExpenseAccountListID() >" + _obj.getExpenseAccountListID());
			if (_obj.getExpenseAccountListID() != null)
			{
				AccountRet existing_account = null;
				try
				{
					existing_account = AccountRet.getAccount(this.getExpenseAccountId());
				}
				catch (ObjectNotFoundException x)
				{
				}
				if (existing_account == null || !existing_account.getListID().equals(_obj.getExpenseAccountListID()))
				{
					try
					{
						AccountRet account_ret = AccountRet.getAccount(this.getCompany(), _obj.getExpenseAccountListID());
						code.setExpenseAccountId(account_ret.getId());
						modified = true;
					}
					catch (ObjectNotFoundException x)
					{
						x.printStackTrace();
					}
				}
			}
			
			System.out.println("_obj.getIncomeAccountListID() >" + _obj.getIncomeAccountListID());
			if (_obj.getIncomeAccountListID() != null)
			{
				AccountRet existing_account = null;
				try
				{
					existing_account = AccountRet.getAccount(this.getIncomeAccountId());
				}
				catch (ObjectNotFoundException x)
				{
				}
				if (existing_account == null || !existing_account.getListID().equals(_obj.getIncomeAccountListID()))
				{
					try
					{
						AccountRet account_ret = AccountRet.getAccount(this.getCompany(), _obj.getIncomeAccountListID());
						code.setIncomeAccountId(account_ret.getId());
						modified = true;
					}
					catch (ObjectNotFoundException x)
					{
						x.printStackTrace();
					}
				}
			}
			
			System.out.println("_obj.getCOGSAccountListID() >" + _obj.getCOGSAccountListID());
			if (_obj.getCOGSAccountListID() != null)
			{
				AccountRet existing_account = null;
				try
				{
					existing_account = AccountRet.getAccount(this.getCOGSAccountId());
				}
				catch (ObjectNotFoundException x)
				{
				}
				if (existing_account == null || !existing_account.getListID().equals(_obj.getCOGSAccountListID()))
				{
					try
					{
						AccountRet account_ret = AccountRet.getAccount(this.getCompany(), _obj.getCOGSAccountListID());
						code.setCOGSAccountId(account_ret.getId());
						modified = true;
					}
					catch (ObjectNotFoundException x)
					{
						x.printStackTrace();
					}
				}
			}
			
			System.out.println("_obj.getAssetAccountListID() >" + _obj.getAssetAccountListID());
			if (_obj.getAssetAccountListID() != null)
			{
				AccountRet existing_account = null;
				try
				{
					existing_account = AccountRet.getAccount(this.getAssetAccountId());
				}
				catch (ObjectNotFoundException x)
				{
				}
				if (existing_account == null || !existing_account.getListID().equals(_obj.getAssetAccountListID()))
				{
					try
					{
						AccountRet account_ret = AccountRet.getAccount(this.getCompany(), _obj.getAssetAccountListID());
						code.setAssetAccountId(account_ret.getId());
						modified = true;
					}
					catch (ObjectNotFoundException x)
					{
						x.printStackTrace();
					}
				}
			}
		}
		
		return modified;
		
	}
	
	public boolean
	setItemGroupRet(ItemGroupRet _obj, boolean _force_update)
		throws TorqueException, UniqueObjectNotFoundException
	{
		boolean modified = false;
		
		Date item_modification_date = _obj.getTimeModified();
		Date code_modification_date = code.getModificationDate();
		if (_force_update || ( (code_modification_date == null) || item_modification_date.after(code_modification_date) ) )
		{
			// the passed object has been modified since this checkout code has
			
			if (code.getEditSequence() == null || !code.getEditSequence().equals(_obj.getEditSequence()))
			{
				code.setEditSequence(_obj.getEditSequence());
				modified = true;
			}
			
			if (code.getListID() == null || !code.getListID().equals(_obj.getListID()))
			{
				code.setListID(_obj.getListID());
				modified = true;
			}
			
			if (code.getQbListID() == null || !code.getQbListID().equals(_obj.getListID()))
			{
				code.setQbListID(_obj.getListID());
				modified = true;
			}
			
			if (code.getDescription() == null || !code.getDescription().equals(_obj.getName()))
			{
				code.setDescription(_obj.getName());
				modified = true;
			}
		}
		
		return modified;
		
	}

    public void
    setListID(String _str)
		throws TorqueException
    {
		code.setListID(_str);
    }

    public void
    setQBListID(String _str)
		throws TorqueException
    {
		code.setQbListID(_str);
    }

	public void
	setOnHandQuantity(short _quantity)
	{
		code.setOnHandQuantity(_quantity);
	}

    public void
    setOrderCost(BigDecimal _amount)
    {
		code.setOrderCost(_amount);
    }

	public void
	setPlanUse(boolean _is_plan_use)
	{
		code.setIsPlanUse(_is_plan_use ? (short)1 : (short)0);
	}
	
	public void
	setPracticeArea(PracticeAreaBean _practice_area)
		throws TorqueException
	{
		code.setPracticeAreaId(_practice_area.getId());
	}

	public void
	setReorderPoint(short _quantity)
	{
		code.setReorderPoint(_quantity);
	}

    public void
    setTaxCode(TaxCodeBean _tax_code)
		throws TorqueException
    {
		code.setTaxCodeId(_tax_code.getId());
    }
    
    public void
    setType(short _type)
		throws IllegalValueException
    {
		code.setType(_type);
		this.setQBItemType(this.getQBItemTypeForCurrentType());
    }

	public void
	setUPC(String _upc)
	{
		code.setUpc(_upc);
	}

	/*
	public void
	setVendor(UKOnlineCompanyBean _vendor)
		throws TorqueException, IllegalValueException
	{
		if (_vendor.getType().equals("Vendor"))
			code.setVendorId(_vendor.getId());
		else
			throw new IllegalValueException(_vendor.getLabel() + " is not a vendor.");
	}
	 */

	public void
	setVendor(VendorRet _vendor)
		throws TorqueException, IllegalValueException
	{
		code.setVendorId(_vendor.getId());
	}

	public void
	setInventoryDepartment(InventoryDepartment _department)
		throws TorqueException, IllegalValueException
	{
		code.setInventoryDepartmentDbId(_department.getId());
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		code.setModificationDate(new Date());
		code.save();

		this.saveChildCodes();
    }
	
	public void
	setRequestID(String _str)
	{
		code.setRequestID(_str);
	}
	
	public String
	getRequestID()
	{
		return code.getRequestID();
	}

	public void
	setChildCodes(HashMap _children)
	{
		child_codes = _children;
	}

	private void
    saveChildCodes()
		throws TorqueException, Exception
    {
		/*
			<table name="CHECKOUT_CODE_CHILD_MAPPING">
				<column name="PARENT_ID" primaryKey="true" required="true" type="INTEGER"/>
				<column name="CHILD_ID" primaryKey="true" required="true" type="INTEGER"/>
				<column name="QTY_IN_PARENT" required="true" type="SMALLINT"/>

				<foreign-key foreignTable="CHECKOUT_CODE">
					<reference local="PARENT_ID" foreign="CHECKOUT_CODE_ID"/>
				</foreign-key>
				<foreign-key foreignTable="CHECKOUT_CODE">
					<reference local="CHILD_ID" foreign="CHECKOUT_CODE_ID"/>
				</foreign-key>
			</table>
		 */

		if (this.child_codes != null)
		{
			System.out.println("child_codes sizer >" + this.child_codes.size());

			HashMap db_child_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(CheckoutCodeChildMappingPeer.PARENT_ID, this.getId());
			Iterator itr = CheckoutCodeChildMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				//System.out.println("found existing ass...");
				CheckoutCodeChildMapping value = (CheckoutCodeChildMapping)itr.next();
				Integer key = new Integer(value.getChildId());
				db_child_hash.put(key, value);
			}

			//System.out.println("num orders >" + this.orders.size());
			itr = this.child_codes.keySet().iterator();
			while (itr.hasNext())
			{
				CheckoutCodeBean child_code = (CheckoutCodeBean)itr.next();
				Integer key = new Integer(child_code.getId());
				CheckoutCodeChildMapping obj = (CheckoutCodeChildMapping)db_child_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					//System.out.println("creating new ass for " + orderline);

					Short qty = (Short)child_codes.get(child_code);

					obj = new CheckoutCodeChildMapping();
					obj.setParentId(this.getId());
					obj.setChildId(key.intValue());
					obj.setQtyInParent(qty.shortValue());
					obj.save();

				}
			}

			itr = db_child_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				CheckoutCodeChildMapping obj = (CheckoutCodeChildMapping)db_child_hash.get(key);
				CheckoutCodeChildMappingPeer.doDelete(obj);
			}
		}
    }

	public HashMap
	getChildCodes()
		throws TorqueException, ObjectNotFoundException
	{
		if (this.child_codes == null)
		{
			this.child_codes = new HashMap(3);

			Criteria crit = new Criteria();
			crit.add(CheckoutCodeChildMappingPeer.PARENT_ID, this.getId());
			Iterator itr = CheckoutCodeChildMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				CheckoutCodeChildMapping obj = (CheckoutCodeChildMapping)itr.next();
				CheckoutCodeBean child = CheckoutCodeBean.getCheckoutCode(obj.getChildId());
				Short qty = new Short(obj.getQtyInParent());
				this.child_codes.put(child, qty);
			}
		}
		return this.child_codes;
	}

	public void
	add(ItemGroupLine _child)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		this.getChildCodes();
		CheckoutCodeBean child = CheckoutCodeBean.getCheckoutCodeByQBListID(this.getCompany(), _child.getListID());
		BigDecimal qty = new BigDecimal(_child.getQuantity());
		this.child_codes.put(child, new Short(qty.shortValue()));
	}

	public boolean
	hasChildren()
	{
		try
		{
			return (this.getChildCodes().size() > 0);
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		return false;
	}
	
	public String
	getStatusString() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		if (this.getRequestID() != null) {
			String str = QBPOSXMLRequest.getResponseStatusMessageForRequestID(this.getRequestID());
			if (str != null) {
				return str;
			}
		}
		return "[NOT FOUND]";
	}
	
	public boolean
	isLowInStock() {
		if (this.getOnHandQuantity() > (short)-1) {
			if (this.getReorderPoint() > (short)0) {
				if (this.getOnHandQuantity() < this.getReorderPoint()) {
					return true;
				}
			}
		}
		return false;
	}
	
	// VENDOR_PRODUCT_NUMBER
	
	public String
	getVendorProductNumberString() throws Exception {
		String str = code.getVendorProductNumber();
		if (str == null) {
			int product_number = code.getItemNumber();
			if (product_number > 0) {
				str = product_number + "";
				this.setVendorProductNumber(str);
				this.save();
				return str;
			}
			return "";
		}
		return str;
	}
	
	public void
	setVendorProductNumber(String _str) {
		code.setVendorProductNumber(_str);
	}
}