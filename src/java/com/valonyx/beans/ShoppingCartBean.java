/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.beans;

import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.beans.CheckoutCodeBean;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import com.valeo.qbpos.data.TenderRet;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.apache.torque.TorqueException;

/**
 *
 * @author marlo
 */
public class ShoppingCartBean {
	
	private static HashMap<UKOnlinePersonBean,ShoppingCartBean> carts = new HashMap();
	private static HashMap<UKOnlinePersonBean,ShoppingCartBean> unassigned_carts = new HashMap();
	
	public static boolean hasCart(UKOnlinePersonBean _client) {
		return carts.containsKey(_client);
	}
	public static boolean hasNonEmptyCart(UKOnlinePersonBean _client) {
		if (carts.containsKey(_client)) {
			ShoppingCartBean cart = carts.get(_client);
			return !cart.isEmpty();
		} else {
			return false;
		}
	}
	
	public static ShoppingCartBean getUnassignedCart(UKOnlinePersonBean _mod_person) {
		ShoppingCartBean unassigned_cart = unassigned_carts.get(_mod_person);
		if (unassigned_cart == null) {
			unassigned_cart = new ShoppingCartBean();
			unassigned_carts.put(_mod_person, unassigned_cart);
		}
		return unassigned_cart;
	}
	
	public static ShoppingCartBean getCart(UKOnlinePersonBean _client) {
		ShoppingCartBean cart_obj = carts.get(_client);
		if (cart_obj == null) {
			cart_obj = new ShoppingCartBean();
			cart_obj.setClient(_client);
			carts.put(_client, cart_obj);
		}
		return cart_obj;
	}
	
	public static void assignCartTo(ShoppingCartBean _cart, UKOnlinePersonBean _client, UKOnlinePersonBean _mod_person) {
		unassigned_carts.remove(_mod_person);
		carts.put(_client, _cart);
		System.out.println("assigned cart to >" + _client.getLabel());
		_cart.printCart();
	}
	
	public static void emptyCart(UKOnlinePersonBean _client) {
		carts.remove(_client);
	}
	
	private UKOnlinePersonBean client;
	private Vector<com.badiyan.torque.CheckoutOrderline> cartStuff = new Vector();
	private Vector<TenderRet> tenderStuff = new Vector();

	public ShoppingCartBean() {
	}

	public void add(CheckoutCodeBean _code) throws TorqueException {
		// is this code already in the cart?
		
		boolean exists = false;
		Iterator itr = cartStuff.iterator();
		while (itr.hasNext()) {
			com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
			com.badiyan.torque.CheckoutCode existing_code = orderline.getCheckoutCode();
			if (existing_code.getCheckoutCodeId() == _code.getId()) {
				exists = true;
				BigDecimal qty = orderline.getQuantity();
				qty = qty.add(BigDecimal.ONE);
				orderline.setQuantity(qty);
			}
		}
		
		System.out.println("exists >" + exists);
		
		if (!exists) {
			com.badiyan.torque.CheckoutOrderline orderline = new com.badiyan.torque.CheckoutOrderline();
			orderline.setQuantity(BigDecimal.ONE);
			orderline.setCheckoutCodeId(_code.getId());
			cartStuff.addElement(orderline);
		}
	}

	public com.badiyan.torque.CheckoutOrderline addSkipAlreadyInCartCheck(CheckoutCodeBean _code) throws TorqueException {
		// is this code already in the cart?

		com.badiyan.torque.CheckoutOrderline orderline = new com.badiyan.torque.CheckoutOrderline();
		orderline.setQuantity(BigDecimal.ONE);
		orderline.setCheckoutCodeId(_code.getId());
		cartStuff.addElement(orderline);
		return orderline;
	}

	public com.badiyan.torque.CheckoutOrderline addSkipAlreadyInCartCheck(CheckoutCodeBean _code, boolean _is_return) throws TorqueException {
		// is this code already in the cart?

		com.badiyan.torque.CheckoutOrderline orderline = new com.badiyan.torque.CheckoutOrderline();
		if (_is_return) {
			orderline.setQuantity(new BigDecimal(-1));
		} else {
			orderline.setQuantity(BigDecimal.ONE);
		}
		orderline.setCheckoutCodeId(_code.getId());
		cartStuff.addElement(orderline);
		return orderline;
	}

	public void add(CheckoutCodeBean _code, UKOnlinePersonBean _practitioner) throws TorqueException {
		// is this code already in the cart?
		
		boolean exists = false;
		Iterator itr = cartStuff.iterator();
		while (itr.hasNext()) {
			com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
			com.badiyan.torque.CheckoutCode existing_code = orderline.getCheckoutCode();
			if (existing_code.getCheckoutCodeId() == _code.getId()) {
				exists = true;
				BigDecimal qty = orderline.getQuantity();
				qty = qty.add(BigDecimal.ONE);
				orderline.setQuantity(qty);
			}
		}
		
		System.out.println("exists >" + exists);
		
		if (!exists) {
			com.badiyan.torque.CheckoutOrderline orderline = new com.badiyan.torque.CheckoutOrderline();
			orderline.setQuantity(BigDecimal.ONE);
			orderline.setCheckoutCodeId(_code.getId());
			if (_practitioner != null)
				orderline.setPractitionerId(_practitioner.getId());
			cartStuff.addElement(orderline);
		}
	}

	public void add(CheckoutCodeBean _code, UKOnlinePersonBean _practitioner, BigDecimal _amount) throws TorqueException, UniqueObjectNotFoundException {
		// is this code already in the cart?
		
		boolean exists = false;
		Iterator itr = cartStuff.iterator();
		while (itr.hasNext()) {
			com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
			com.badiyan.torque.CheckoutCode existing_code = orderline.getCheckoutCode();
			if (existing_code.getCheckoutCodeId() == _code.getId()) {
				exists = true;
				BigDecimal qty = orderline.getQuantity();
				qty = qty.add(BigDecimal.ONE);
				orderline.setQuantity(qty);
				orderline.setPrice(_amount);
				orderline.setActualAmount(_amount.add(_code.getTaxAmount(_amount)));
			}
		}
		
		System.out.println("exists >" + exists);
		
		if (!exists) {
			com.badiyan.torque.CheckoutOrderline orderline = new com.badiyan.torque.CheckoutOrderline();
			orderline.setQuantity(BigDecimal.ONE);
			orderline.setCheckoutCodeId(_code.getId());
			orderline.setPrice(_amount);
			orderline.setActualAmount(_amount.add(_code.getTaxAmount(_amount)));
			if (_practitioner != null)
				orderline.setPractitionerId(_practitioner.getId());
			cartStuff.addElement(orderline);
		}
	}
	
	public void add(TenderRet _tender) {
		tenderStuff.addElement(_tender);
	}

	public void remove(CheckoutCodeBean _code) throws TorqueException {
		// is this code already in the cart?
		
		int removeIndex = -1;
		
		//boolean exists = false;
		Iterator itr = cartStuff.iterator();
		for (int i = 0; itr.hasNext(); i++) {
			com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
			com.badiyan.torque.CheckoutCode existing_code = orderline.getCheckoutCode();
			if (existing_code.getCheckoutCodeId() == _code.getId()) {
				//exists = true;
				//cartStuff.remove(orderline);
				
				removeIndex = i;
			}
		}
		
		if (removeIndex > -1)
			cartStuff.removeElementAt(removeIndex);
		
		/*
		if (!exists) {
			com.badiyan.torque.CheckoutOrderline orderline = new com.badiyan.torque.CheckoutOrderline();
			orderline.setQuantity(BigDecimal.ONE);
			orderline.setCheckoutCodeId(_code.getId());
		}
		*/
	}

	public void remove(CheckoutCodeBean _code, int _remove_row) throws TorqueException {
		// is this code already in the cart?
		
		int removeIndex = -1;
		
		//boolean exists = false;
		Iterator itr = cartStuff.iterator();
		for (int i = 0; itr.hasNext(); i++) {
			com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
			com.badiyan.torque.CheckoutCode existing_code = orderline.getCheckoutCode();
			if (existing_code.getCheckoutCodeId() == _code.getId()) {
				
				if ( (i + 1) == _remove_row ) {
					removeIndex = i;
					break;
				}
			}
		}
		
		if (removeIndex > -1) {
			cartStuff.removeElementAt(removeIndex);
		}
		
	}

	public void updateQty(CheckoutCodeBean _code, BigDecimal _qty) throws TorqueException {
		// is this code already in the cart?
		
		int updateIndex = -1;
		
		//boolean exists = false;
		Iterator itr = cartStuff.iterator();
		for (int i = 0; itr.hasNext(); i++) {
			com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
			com.badiyan.torque.CheckoutCode existing_code = orderline.getCheckoutCode();
			if (existing_code.getCheckoutCodeId() == _code.getId()) {
				updateIndex = i;
				break;
			}
		}
		
		if (updateIndex > -1) {
			com.badiyan.torque.CheckoutOrderline orderline_obj = cartStuff.get(updateIndex);
			orderline_obj.setQuantity(_qty);
		}
	}

	public void updateQty(CheckoutCodeBean _code, int _remove_row, BigDecimal _qty) throws TorqueException {
		// is this code already in the cart?
		
		int updateIndex = -1;
		
		//boolean exists = false;
		Iterator itr = cartStuff.iterator();
		for (int i = 0; itr.hasNext(); i++) {
			com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
			com.badiyan.torque.CheckoutCode existing_code = orderline.getCheckoutCode();
			if (existing_code.getCheckoutCodeId() == _code.getId()) {
				
				if ( (i + 1) == _remove_row ) {
					updateIndex = i;
					break;
				}
			}
		}
		
		if (updateIndex > -1) {
			com.badiyan.torque.CheckoutOrderline orderline_obj = cartStuff.get(updateIndex);
			orderline_obj.setQuantity(_qty);
		}
		
	}

	public void updateAmount(CheckoutCodeBean _code, int _remove_row, BigDecimal _amount) throws TorqueException {
		// is this code already in the cart?
		
		int updateIndex = -1;
		
		//boolean exists = false;
		Iterator itr = cartStuff.iterator();
		for (int i = 0; itr.hasNext(); i++) {
			com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
			com.badiyan.torque.CheckoutCode existing_code = orderline.getCheckoutCode();
			if (existing_code.getCheckoutCodeId() == _code.getId()) {
				
				if ( (i + 1) == _remove_row ) {
					updateIndex = i;
					break;
				}
			}
		}
		
		if (updateIndex > -1) {
			com.badiyan.torque.CheckoutOrderline orderline_obj = cartStuff.get(updateIndex);
			orderline_obj.setPrice(_amount);
		}
		
	}
	
	public void remove(TenderRet _tender) {
		tenderStuff.removeElement(_tender);
	}

	public void removeTenderAt(int _remove_row) throws TorqueException {
		tenderStuff.removeElementAt(_remove_row - 1);
	}

	public UKOnlinePersonBean getClient() {
		return client;
	}

	public void setClient(UKOnlinePersonBean client) {
		this.client = client;
	}
	
	public void printCart() {
		
		System.out.println("num cart rows >" + cartStuff.size());
		
		Iterator itr = cartStuff.iterator();
		while (itr.hasNext()) {
			com.badiyan.torque.CheckoutOrderline orderline = (com.badiyan.torque.CheckoutOrderline)itr.next();
			System.out.println("orderline qty >" + orderline.getQuantity());
			System.out.println("orderline code >" + orderline.getCheckoutCodeId());
		}
		
	}
	
	public Iterator getCheckoutOrderlines() {
		return cartStuff.iterator();
	}
	
	public Iterator getTenders() {
		return tenderStuff.iterator();
	}
	
	public boolean isEmpty() {
		if (cartStuff == null) {
			return true;
		}
		return cartStuff.isEmpty();
	}
	
	
}
