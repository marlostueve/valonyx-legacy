/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb.data;

import java.util.Date;
import java.util.Vector;

/**
 *
 * @author marlo
 */
public class ItemGroupRet {

	private String list_id;
	private String name;
	private String description;
	
	private String edit_sequence;
	
	private Date time_created;
	private Date time_modified;
	
	private Vector itemGroupLines = new Vector();
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEditSequence() {
		return edit_sequence;
	}

	public void setEditSequence(String edit_sequence) {
		this.edit_sequence = edit_sequence;
	}

	public Vector getItemGroupRetObjects() {
		return itemGroupLines;
	}

	public void setItemGroupRetObjects(Vector _itemGroupLines) {
		this.itemGroupLines = _itemGroupLines;
	}
	
	public void add(ItemGroupLine _obj) {
		this.itemGroupLines.addElement(_obj);
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
