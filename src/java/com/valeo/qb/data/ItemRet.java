/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb.data;

import java.util.Date;

/**
 *
 * @author marlo
 */
public class ItemRet {
	
	public static final short INVENTORY_TYPE = 1;
	public static final short SERVICE_TYPE = 2;
	public static final short NON_INVENTORY_TYPE = 3;
	public static final short OTHER_CHARGE_TYPE = 4;
	public static final short INVENTORY_ASSEMBLY_TYPE = 5;
	public static final short FIXED_ASSET_TYPE = 6;
	public static final short SUBTOTAL_TYPE = 7;
	public static final short DISCOUNT_TYPE = 8;
	public static final short PAYMENT_TYPE = 9;
	public static final short SALES_TAX_TYPE = 10;
	public static final short SALES_TAX_GROUP_TYPE = 11;
	public static final short GROUP_TYPE = 12;
	public static final short SPECIAL_TYPE = 13;

	private String list_id;
	private String name;
	private String sales_tax_list_id;
	
	private String edit_sequence;
	private float sales_price;
	private float purchase_cost;
	
	private String vendor_list_id;
	private String parent_list_id;
	
	private String income_account_list_id;
	private String expense_account_list_id;
	private String cogs_account_list_id;
	private String asset_account_list_id;
	
	private Date time_created;
	private Date time_modified;
	
	private short item_type;
	
	private boolean sales_and_purchase = false;
	private boolean sales_or_purchase = false;

	public boolean isSalesAndPurchase() {
		return sales_and_purchase;
	}

	public void setSalesAndPurchase(boolean _sales_and_purchase) {
		this.sales_and_purchase = _sales_and_purchase;
	}

	public boolean isSalesOrPurchase() {
		return sales_or_purchase;
	}

	public void setSalesOrPurchase(boolean _sales_or_purchase) {
		this.sales_or_purchase = _sales_or_purchase;
	}

	public short getItemType() {
		return item_type;
	}

	public void setItemType(short item_type) {
		this.item_type = item_type;
	}

	public String getListID() {
		return list_id;
	}

	public void setListID(String list_id) {
		this.list_id = list_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSalesTaxListID() {
		return sales_tax_list_id;
	}

	public void setSalesTaxListID(String sales_tax_list_id) {
		this.sales_tax_list_id = sales_tax_list_id;
	}

	public String getEditSequence() {
		return edit_sequence;
	}

	public void setEditSequence(String edit_sequence) {
		this.edit_sequence = edit_sequence;
	}

	public float getPurchaseCost() {
		return purchase_cost;
	}

	public void setPurchaseCost(float purchase_cost) {
		this.purchase_cost = purchase_cost;
	}

	public float getSalesPrice() {
		return sales_price;
	}

	public void setSalesPrice(float sales_price) {
		this.sales_price = sales_price;
	}

	public String getVendorListID() {
		return vendor_list_id;
	}

	public void setVendorListID(String _vendor_list_id) {
		this.vendor_list_id = _vendor_list_id;
	}

	public String getParentListID() {
		return parent_list_id;
	}

	public void setParentListID(String _parent_list_id) {
		this.parent_list_id = _parent_list_id;
	}

	public String getIncomeAccountListID() {
		return income_account_list_id;
	}

	public void setIncomeAccountListID(String list_id) {
		this.income_account_list_id = list_id;
	}

	public String getExpenseAccountListID() {
		return expense_account_list_id;
	}

	public void setExpenseAccountListID(String list_id) {
		this.expense_account_list_id = list_id;
	}

	public String getCOGSAccountListID() {
		return cogs_account_list_id;
	}

	public void setCOGSAccountListID(String list_id) {
		this.cogs_account_list_id = list_id;
	}

	public String getAssetAccountListID() {
		return asset_account_list_id;
	}

	public void setAssetAccountListID(String list_id) {
		this.asset_account_list_id = list_id;
	}

	public Date getTimeCreated() {
		return time_created;
	}

	public void setTimeCreated(Date time_created) {
		this.time_created = time_created;
	}

	public Date getTimeModified() {
		return time_modified;
	}

	public void setTimeModified(Date time_modified) {
		this.time_modified = time_modified;
	}

	
}
