/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.PasswordGenerator;
import com.badiyan.uk.online.webservices.QBWebConnectorSvcSoapImpl;

import java.io.IOException;
import java.io.StringReader;

import java.text.SimpleDateFormat;

import java.util.*;
import java.math.BigDecimal;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.torque.*;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.valeo.qbpos.data.*;
import com.valeo.qb.data.*;
import com.valonyx.events.QBCustomerAddEvent;
import com.valonyx.events.QBCustomerAddEventListener;
import java.math.RoundingMode;

/**
 *
 * @author marlo
 */
public class
QBXMLResponseParser
	extends DefaultHandler
{
	// CLASS VARIABLES

	private static SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat time_modified_date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			// 2008-10-15T07:41:47-05:00
			// 2008-11-08T09:53:34-06:00

	// INSTANCE VARIABLES

	private UKOnlineCompanyBean company;

	private CustomerQueryRs customerQueryRsObject;
	private CustomerRet customerRetObject;

	private ReceivePaymentQueryRs receivePaymentQueryRsObject;
	private ReceivePaymentAddRs receivePaymentAddRsObject;
	private ReceivePaymentRet receivePaymentRetObject;
	private CustomerRef customerRefObject;
	private PaymentMethodRef paymentMethodRefObject;

	private AccountQueryRs accountQueryRsObject;
	private AccountRet accountRetObject;

	private VendorQueryRs vendorQueryRsObject;
	private VendorRet vendorRetObject;

	private PaymentMethodQueryRs paymentMethodQueryRsObject;
	private PaymentMethodRet paymentMethodRetObject;
	
	private ItemGroupRet itemGroupRetObject;
	private ItemGroupLine itemGroupLineObject;

	private ItemQueryRs itemQueryRsObject;
	private ItemRet itemRetObject;
	private ItemSalesTaxRet itemSalesTaxRetObject;
	private ItemSalesTaxGroupRet itemSalesTaxGroupRetObject;
	private ItemPaymentRet itemPaymentRetObject;
	
	private ItemNonInventoryAddRs itemNonInventoryAddRs;
	private ItemPaymentAddRs itemPaymentAddRs;
	private ItemSalesTaxAddRs itemSalesTaxAddRs;

	private boolean doItem = false;
	private boolean doItemSalesTax = false;
	private boolean doItemSalesTaxGroup = false;
	private boolean doItemPayment = false;
	private boolean doItemGroup = false;
	private boolean pref_vendor_ref = false;
	private boolean parent_ref = false;
	
	private boolean income_account_ref = false;
	private boolean expense_account_ref = false;
	private boolean cogs_account_ref = false;
	private boolean asset_account_ref = false;
	
	private boolean tax_vendor_ref = false;

	private SalesTaxCodeQueryRs salesTaxCodeQueryRsObject;
	private SalesTaxCodeRet salesTaxCodeRetObject;

	private SalesReceiptQueryRs salesReceiptQueryRsObject;
	private SalesReceiptAddRs salesReceiptAddRsObject;
	private SalesReceiptRet salesReceiptRetObject;
	private SalesReceiptLineRet salesReceiptLineRetObject;

	private InvoiceQueryRs invoiceQueryRsObject;
	private InvoiceAddRs invoiceAddRsObject;
	private InvoiceRet invoiceRetObject;
	private InvoiceLineRet invoiceLineRetObject;

	private ItemReceiptAddRs itemReceiptAddRsObject;
	private ItemReceiptRet itemReceiptRetObject;

	private PurchaseOrderAddRs purchaseOrderAddRsObject;
	private PurchaseOrderRet purchaseOrderRetObject;

	private BillAddRs billAddRsObject;
	private BillRet billRetObject;

	private String current_element;


	// CONSTRUCTORS

	public
	QBXMLResponseParser(UKOnlineCompanyBean _company)
	{
		company = _company;
	}

	// INSTANCE METHODS

	public QueryRs
	parse(String _response)
	{
		System.out.println("parse invoked >" + this);
		
		QueryRs return_response = null;

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			//parse the file and also register this class for call backs
			
			sp.parse(new InputSource(new StringReader(_response)), this);

			QuickBooksSettings settings = company.getQuickBooksSettings();
			CashOut cash_out = settings.getActiveCashOut();
			Vector log = null;
			if (cash_out == null)
				log = new Vector();
			else
				log = cash_out.getLog();
			
			System.out.println("cash_out >" + cash_out);

			if (customerQueryRsObject != null)
			{
				return_response = customerQueryRsObject;
				
				System.out.println("NUM customerQueryRsObject >" + customerQueryRsObject.getCustomerRetObjects().size());

				if (customerQueryRsObject.getStatusSeverity().equals("Error"))
					log.addElement(customerQueryRsObject.getStatusMessage());

				Iterator itr = customerQueryRsObject.getCustomerRetObjects().iterator();
				while (itr.hasNext())
				{
					try
					{
						CustomerRet obj = (CustomerRet)itr.next();
						obj.setRequestId(customerQueryRsObject.getRequestID());
						obj.setCompany(company);

						CustomerRet existing_ret = null;
						try
						{
							existing_ret = CustomerRet.getCustomerQBFS(company, obj.getQBFSListID());
							System.out.println("existing customer found for list id >" + obj.getQBFSListID());
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("customer not found for list id >" + obj.getQBFSListID());

							Vector existingpersons = UKOnlinePersonBean.getPersonsByLastNameFirstName(company, obj.getLastName(), obj.getFirstName());
							if (existingpersons.size() != 1)
								existingpersons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(company, obj.getLastName(), obj.getFirstName(), obj.getPhone(), false);
							//CustomerRet existing_customer_ret = CustomerRet.getCustomerByFirstLastPhone(company, obj.getFirstName(), obj.getLastName(), obj.getPhone());
							
							// 8/22/11 - I'm thinking I need to search by First, Last, and Email if the above isn't unique.  I could also stipulate no QB ID to further refine this...
							
							if (existingpersons.size() != 1)
								existingpersons = UKOnlinePersonBean.getPersonsByLastNameFirstNameEmail(company, obj.getLastName(), obj.getFirstName(), obj.getPhone(), false);

							if (existingpersons.size() == 0)
							{
								CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "QBXMLResponseParser Issue",
											"Existing customer not found... for " + obj.getFirstName() + " " + obj.getLastName());
								System.out.println("Existing customer not found...  create new, I guess");
							}
							else if (existingpersons.size() == 1)
							{
								System.out.println("Existing customer found for " + obj.getFirstName() + " " + obj.getLastName());
								UKOnlinePersonBean existingperson = (UKOnlinePersonBean)existingpersons.get(0);
								existingperson.setQBFSListID(obj.getQBFSListID());
								existingperson.save();

								try
								{
									existing_ret = CustomerRet.getCustomer(company, existingperson.getEmployeeNumberString());
									existing_ret.setQBFSListID(obj.getQBFSListID());
								}
								catch (ObjectNotFoundException y)
								{
									System.out.println("Unable to locate CustomerRet for QBPOS id >" + existingperson.getEmployeeNumberString() + ".  A new CustomerRet will be created.");
								}
							}
							else
							{
								CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "QBXMLResponseParser Issue",
											"Unique Existing customer not found for " + obj.getFirstName() + " " + obj.getLastName());
								System.out.println("Unique Existing customer not found for " + obj.getFirstName() + " " + obj.getLastName());

								throw new UniqueObjectNotFoundException("Unique Existing customer not found for " + obj.getFirstName() + " " + obj.getLastName());
							}
						}

						if (existing_ret == null)
						{
							// no existing.  create new

							obj.setCompany(company);
							obj.save();

							// create the corresponding UKOnlinePersonBean...
							
							// do I really need to create a new person?  not if there's an existing person with this QBListID
							
							try
							{
								UKOnlinePersonBean.getPersonByQBListId(company, obj.getQBFSListID(), false);
								this.updatePerson(company, obj);
								log.addElement("Customer " + obj.getFullName() + " updated.");
							}
							catch (ObjectNotFoundException x)
							{
								this.createNewPerson(company, obj);
								log.addElement("Customer " + obj.getFullName() + " created.");
							}

							
						}
						else
						{
							// already exists.  update...

							/*
							existing_ret.setAccountBalance(obj.getAccountBalance());
							existing_ret.setEMail(obj.getEMail());
							existing_ret.setFirstName((obj.getFirstName() == null) ? "" : obj.getFirstName());
							existing_ret.setFullName(obj.getFullName());
							existing_ret.setLastName(obj.getLastName());
							existing_ret.setLastSaleDate(obj.getLastSaleDate());
							existing_ret.setPhone(obj.getPhone());
							existing_ret.setPhone2(obj.getPhone2());
							existing_ret.setPriceLevelNumber(obj.getPriceLevelNumber());
							existing_ret.setRequestId(obj.getRequestId());
							existing_ret.setTimeModified(obj.getTimeModified());
							 */
							
							// was there really an update made???
							
							boolean force_update = (cash_out == null);
								// if there is no active cash out, force update of all clients.  I'm assuming that the update request should include all clients if it's not part of a cash-out
							
							boolean updated = existing_ret.setCustomerRet(obj, force_update);
							if (updated)
							{
								existing_ret.save();
								this.updatePerson(company, existing_ret);
								log.addElement("Customer " + obj.getFullName() + " updated.");
							}
						}

					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
				
				if (!settings.isSyncClients())
				{
					settings.setSyncClients(true);
					settings.save();
				}
			}

			if (receivePaymentAddRsObject != null)
			{
				return_response = receivePaymentAddRsObject;
				
				System.out.println("NUM receivePaymentAddRsObject >" + receivePaymentAddRsObject.getReceivePaymentRetObjects().size() + " < should only be one, I'm thinking...");

				if (receivePaymentAddRsObject.getStatusSeverity().equals("Error"))
					log.addElement(receivePaymentAddRsObject.getStatusMessage());

				Iterator itr = receivePaymentAddRsObject.getReceivePaymentRetObjects().iterator();
				while (itr.hasNext())
				{
					ReceivePaymentRet obj = (ReceivePaymentRet)itr.next();

					try
					{
						// see if there's a matching ReceivePaymentRet in the db already - there won't be...

						ReceivePaymentRet existing_ret = null;
						try
						{
							existing_ret = ReceivePaymentRet.getReceivePayment(company, obj.getTxnNumber());
							System.out.println("existing receive payment found!?!?");
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("receive payment not found!");

							try
							{
								existing_ret = ReceivePaymentRet.getReceivePayment(company, obj.getTxnID());
								System.out.println("existing receive payment found!?!?");
							}
							catch (ObjectNotFoundException y)
							{
								// expected result
							}
						}
						
						String requestID = receivePaymentAddRsObject.getRequestID();

						// find the TenderRet mapping object that matches this request (there should be one)
						
						try
						{

							TenderRetDbOrderMapping tender_mapping_obj = TenderRet.getMapping(company, requestID);

							// mark the tender mapping as having been communicated to QBFS

							tender_mapping_obj.setTxnID(obj.getTxnID());
							tender_mapping_obj.save();
						}
						catch (Exception x)
						{
							x.printStackTrace();
							CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Unable to locate tender mapping for ReceivePaymentAdd >" + requestID);
							
							log.addElement(x.getMessage());
						}
						
						UKOnlinePersonBean client = null;
						try
						{
							client = UKOnlinePersonBean.getPersonByQBListId(company, obj.getCustomerListID(), true);
						}
						catch (ObjectNotFoundException x)
						{
							CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Unable to locate client for ReceivePaymentAdd");
							//throw x;

							log.addElement(x.getMessage());
						}

						if (existing_ret == null)
						{
							// no existing.  create new

							obj.setRequestId(receivePaymentAddRsObject.getRequestID());
							obj.setCompany(company);
							obj.save();

							if (client != null)
								log.addElement("Receive payment of " + obj.getTotalAmount() + " created for " + client.getLabel());
						}
						else
						{
							// shouldn't already exist.  don't update...
						}

					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}

			if (receivePaymentQueryRsObject != null)
			{
				return_response = receivePaymentQueryRsObject;
				
				System.out.println("NUM receivePaymentQueryRsObject >" + receivePaymentQueryRsObject.getReceivePaymentRetObjects().size());

				if (receivePaymentQueryRsObject.getStatusSeverity().equals("Error"))
					log.addElement(receivePaymentQueryRsObject.getStatusMessage());

				Iterator itr = receivePaymentQueryRsObject.getReceivePaymentRetObjects().iterator();
				while (itr.hasNext())
				{
					ReceivePaymentRet obj = (ReceivePaymentRet)itr.next();
						
					//if (obj.getCustomerListID() != null && obj.getCustomerListID().equals("800003E4-1230648645"))
					//{
						//System.out.println("found receive payment for Derrick");

					try
					{
						// see if there's a matching ReceivePaymentRet in the db already

						ReceivePaymentRet existing_ret = null;
						try
						{
							existing_ret = ReceivePaymentRet.getReceivePayment(company, obj.getTxnNumber());
							System.out.println("existing receive payment found!");
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("receive payment not found!");

							try
							{
								existing_ret = ReceivePaymentRet.getReceivePayment(company, obj.getTxnID());
							}
							catch (ObjectNotFoundException y)
							{
								System.out.println("receive payment not found!?!");
							}
						}

						Vector order_lines = new Vector();
						UKOnlinePersonBean client = null;
						try
						{
							client = UKOnlinePersonBean.getPersonByQBListId(company, obj.getCustomerListID(), true);
						}
						catch (ObjectNotFoundException x)
						{
							CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Unable to locate client for ReceivePaymentQuery");
							//throw x;

							log.addElement(x.getMessage());
						}

						if (existing_ret == null)
						{
							// no existing.  create new.  this is going to be a receive payment entered in QB and not in Valonyx...
							
							obj.setRequestId(receivePaymentQueryRsObject.getRequestID());
							obj.setCompany(company);
							obj.save();

							if (client != null)
								log.addElement("Receive payment of " + obj.getTotalAmount() + " created for " + client.getLabel());
							
							// since this payment was entered in QB, I can go ahead and create a new Tender for it
							
							TenderRet tender = new TenderRet();
							tender.setAmount(obj.getTotalAmount());
							
							try
							{
								PaymentMethodRet payment_method = PaymentMethodRet.getPaymentMethod(company, obj.getPaymentMethodListID());
								ItemPaymentRet payment_ret = ItemPaymentRet.getPaymentItem(payment_method);
								switch (payment_ret.getCCType())
								{
									case ItemPaymentRet.CASH: tender.setType(TenderRet.CASH_TENDER_TYPE); break;
									case ItemPaymentRet.CHECK: tender.setType(TenderRet.CHECK_TENDER_TYPE); break;
									case ItemPaymentRet.DINERS_CLUB: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
									case ItemPaymentRet.DISCOVER: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
									case ItemPaymentRet.EN_ROUTE: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
									case ItemPaymentRet.GIFT_CARD: tender.setType(TenderRet.GIFT_CARD_TENDER_TYPE); break;
									case ItemPaymentRet.GIFT_CERT: tender.setType(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE); break;
									case ItemPaymentRet.MASTERCARD: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
									case ItemPaymentRet.VISA: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
								}
							}
							catch (Exception x)
							{
								x.printStackTrace();
							}
							
							if (tender.getType() == null)
								tender.setType(TenderRet.UNKNOWN_TENDER_TYPE);
								
							tender.setClient(client);
							tender.setCompany(company);
							//if (order_obj != null)
							//	tender.setOrder(order_obj);
							//tender.setSalesReceipt(obj);
							tender.setTenderDate(obj.getTxnDate());
							tender.setRequestID(receivePaymentQueryRsObject.getRequestID());
							tender.setTxnID(obj.getTxnID());
							tender.save();

							//BigDecimal line_total = new BigDecimal(item_obj.getAmount() * -1);
							//tender_total = tender_total.add(line_total);
							//obj.add(tender);
							
							// find an open order to apply this against
							// a potential problem here is that QB may assign payments to orders differently than I am below.  not sure how much of a problem that might be...
							
							BigDecimal tender_amount = new BigDecimal(obj.getTotalAmount()).setScale(2, RoundingMode.HALF_UP);
							
							Calendar date_range_for_open_orders = Calendar.getInstance();
							date_range_for_open_orders.add(Calendar.YEAR, -5);

							Vector open_orders = client.getOpenOrders(date_range_for_open_orders.getTime()); // newest orders'll be on top
							System.out.println("num open orders >" + open_orders.size());
							
							Vector orders_to_apply_payment_to = new Vector();
							HashMap order_to_tender_amount_hash = new HashMap(3);
							
							// is there a single order to apply this against?  loop the orders and see
							
							Iterator open_orders_itr = open_orders.iterator();
							while (open_orders_itr.hasNext() && orders_to_apply_payment_to.isEmpty())
							{
								ValeoOrderBean open_order_obj = (ValeoOrderBean)open_orders_itr.next();
								
								BigDecimal open_order_balance = open_order_obj.getBalance();
								
								System.out.println("tender_amount.compareTo(open_order_balance) == 0 >" + (tender_amount.compareTo(open_order_balance) == 0));
								System.out.println("tender_amount >" + tender_amount);
								System.out.println("open_order_balance >" + open_order_balance);
								
								if (tender_amount.compareTo(open_order_balance) == 0)
								{
									// found open order to apply receive payment against
									System.out.println("found single order to apply receive payment against - " + open_order_obj.getValue());
									orders_to_apply_payment_to.addElement(open_order_obj);
									HashMap hash = new HashMap(3);
									hash.put(tender, tender_amount);
									order_to_tender_amount_hash.put(open_order_obj, hash);
									
									open_order_obj.setBalance(BigDecimal.ZERO);
									open_order_obj.setIsPaid(true);
									
								}
							}
							
							if (orders_to_apply_payment_to.isEmpty())
							{
								BigDecimal tender_amount_available_to_be_consumed = new BigDecimal(obj.getTotalAmount()).setScale(2, RoundingMode.HALF_UP);
								BigDecimal tender_amount_consumed = BigDecimal.ZERO;
								
								// first apply to the orders that can be covered completely
								
								open_orders_itr = open_orders.iterator();
								while (open_orders_itr.hasNext())
								{
									ValeoOrderBean open_order_obj = (ValeoOrderBean)open_orders_itr.next();

									BigDecimal open_order_balance = open_order_obj.getBalance();
									if ((open_order_balance.compareTo(tender_amount_available_to_be_consumed) == -1) ||
											(open_order_balance.compareTo(tender_amount_available_to_be_consumed) == 0))
									{
										System.out.println("found order amount(1) (" + open_order_balance.toString() + ") to apply against - " + tender_amount_available_to_be_consumed.toString());
										
										orders_to_apply_payment_to.addElement(open_order_obj);
										HashMap hash = new HashMap(3);
										hash.put(tender, open_order_balance);
										order_to_tender_amount_hash.put(open_order_obj, hash);
										tender_amount_available_to_be_consumed = tender_amount_available_to_be_consumed.subtract(open_order_balance);
										tender_amount_consumed = tender_amount_consumed.add(open_order_balance);
									
										open_order_obj.setBalance(BigDecimal.ZERO);
										open_order_obj.setIsPaid(true);
									}
								}
									
								// find an order to apply this to.  the first order with a balance larger than the amount available, I guess

								open_orders_itr = open_orders.iterator();
								while ((tender_amount_available_to_be_consumed.compareTo(BigDecimal.ZERO) == 1) && open_orders_itr.hasNext())
								{
									System.out.println("still some amount available to be consumed >" + tender_amount_available_to_be_consumed.toString());

									ValeoOrderBean open_order_obj = (ValeoOrderBean)open_orders_itr.next();

									if (!order_to_tender_amount_hash.containsKey(open_order_obj))
									{
										BigDecimal open_order_balance = open_order_obj.getBalance();
										if ((open_order_balance.compareTo(tender_amount_available_to_be_consumed) == 1) ||
												(open_order_balance.compareTo(tender_amount_available_to_be_consumed) == 0)) // open order balance is greater than the available tender
										{
											System.out.println("found order amount(2) (" + open_order_balance.toString() + ") to apply against - " + tender_amount_available_to_be_consumed.toString());

											orders_to_apply_payment_to.addElement(open_order_obj);
											HashMap hash = new HashMap(3);
											hash.put(tender, tender_amount_available_to_be_consumed);
											order_to_tender_amount_hash.put(open_order_obj, hash);

											open_order_obj.setBalance(open_order_balance.subtract(tender_amount_available_to_be_consumed));

											tender_amount_consumed = tender_amount_consumed.add(tender_amount_available_to_be_consumed);
											tender_amount_available_to_be_consumed = BigDecimal.ZERO;
										}
									}
								}
								
								
							}
							
							System.out.println("SETTING ORDERS FOR TENDERx >" + orders_to_apply_payment_to.size());
							tender.setOrders(orders_to_apply_payment_to);
							tender.setTenderPaymentInfo(order_to_tender_amount_hash);
							tender.save();
							
							// save the orders
							
							Iterator order_itr = orders_to_apply_payment_to.iterator();
							while (order_itr.hasNext())
							{
								ValeoOrderBean open_order_obj = (ValeoOrderBean)order_itr.next();
								open_order_obj.save();
							}
				
						}
						else
						{
							// already exists.  update...

							existing_ret.setCustomerListID(obj.getCustomerListID());
							existing_ret.setMemo(obj.getMemo());
							existing_ret.setRequestId(receivePaymentQueryRsObject.getRequestID());
							existing_ret.setTimeCreated(obj.getTimeCreated());
							existing_ret.setTimeModified(obj.getTimeModified());
							existing_ret.setTotalAmount(obj.getTotalAmount());
							existing_ret.setTxnDate(obj.getTxnDate());
							existing_ret.setTxnID(obj.getTxnID());
							existing_ret.setTxnNumber(obj.getTxnNumber());
							existing_ret.save();

							if (client != null)
								log.addElement("Receive payment of " + obj.getTotalAmount() + " updated for " + client.getLabel());
						}

						// not quite sure why I was creating an order out of this... removed 11/13/2010
						// I should check where ValeoOrderBean.RECEIVE_PAYMENT may have been used...

						/*
						OrderBean order_obj = null;
						try
						{
							order_obj = SalesReceiptRet.getOrder(company, obj.getTxnID());
						}
						catch (ObjectNotFoundException x)
						{
							order_obj = new OrderBean();
						}

						BigDecimal total_amount = new BigDecimal(obj.getTotalAmount());

						order_obj.setCompany(company);
						order_obj.setAuthorizationCode(obj.getTxnID());
						order_obj.setOrderDate(obj.getTxnDate());
						order_obj.setPerson(client);
						order_obj.setStatus(OrderBean.RECEIVE_PAYMENT_ORDER_STATUS);
						order_obj.setSubtotal(total_amount);
						order_obj.setTax(BigDecimal.ZERO);
						order_obj.setTotal(total_amount);
						order_obj.setMemo(obj.getMemo());

						// grab the receive payment checkout code

						CheckoutCodeBean receive_payment_checkout_code = null;
						Vector receive_payment_checkout_codes = CheckoutCodeBean.getCheckoutCodes(company, CheckoutCodeBean.RECEIVE_PAYMENT_TYPE);
						if (receive_payment_checkout_codes.isEmpty())
						{
							// no receive payment checkout codes found.  create one
							receive_payment_checkout_code = new CheckoutCodeBean();
							receive_payment_checkout_code.setAmount(BigDecimal.ZERO);
							receive_payment_checkout_code.setCode("QB");
							receive_payment_checkout_code.setCompany(company);
							receive_payment_checkout_code.setDescription("Receive Payment");
							receive_payment_checkout_code.setItemNumber(0);
							receive_payment_checkout_code.setListID("");
							receive_payment_checkout_code.setType(CheckoutCodeBean.RECEIVE_PAYMENT_TYPE);
							receive_payment_checkout_code.save();
						}
						else
							receive_payment_checkout_code = (CheckoutCodeBean)receive_payment_checkout_codes.elementAt(0);

						// create checkout_orderline(s) for this code


						CheckoutOrderline order_line = new CheckoutOrderline();
						order_line.setPrice(total_amount);

						order_line.setActualAmount(total_amount);
						order_line.setTax(BigDecimal.ZERO);
						order_line.setCheckoutCodeId(receive_payment_checkout_code.getId());
						order_line.setQuantity(BigDecimal.ONE);
						order_line.setOrderstatus(OrderBean.RECEIVE_PAYMENT_ORDER_STATUS);
						order_lines.addElement(order_line);

						order_obj.setOrders(order_lines);
						order_obj.save();
						 *
						 */

						// create a Tender out of this. it would be good to have a way to tie a Tender to the ReceivePayment

						// 5/5/11 - not sure if creating a tender out of this is the right thing to do.  not sure if payment methods'll be compatible (barter?)
						// I think I'll attempt to just 

					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
					
					//}
				}
			}

			if (accountQueryRsObject != null)
			{
				return_response = accountQueryRsObject;
				
				System.out.println("NUM accountQueryRsObject >" + accountQueryRsObject.getAccountRetObjects().size());

				if (accountQueryRsObject.getStatusSeverity().equals("Error"))
					log.addElement(accountQueryRsObject.getStatusMessage());

				Iterator itr = accountQueryRsObject.getAccountRetObjects().iterator();
				while (itr.hasNext())
				{
					AccountRet obj = (AccountRet)itr.next();

					try
					{
						// see if there's a matching AccountRet in the db already

						AccountRet existing_ret = null;
						try
						{
							existing_ret = AccountRet.getAccount(company, obj.getListID());
							System.out.println("existing account found!");
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("account not found!");
						}

						if (existing_ret == null)
						{
							// no existing.  create new

							//obj.setRequestId(receivePaymentQueryRsObject.getRequestID());
							obj.setCompany(company);
							obj.save();

							log.addElement("Account " + obj.getFullName() + " created.");
						}
						else
						{
							// already exists.  update...

							existing_ret.setAccountNumber(obj.getAccountNumber());
							existing_ret.setAccountType(obj.getAccountType());
							existing_ret.setBalance(obj.getBalance());
							existing_ret.setCashFlowClassification(obj.getCashFlowClassification());
							existing_ret.setDesc(obj.getDesc());
							existing_ret.setEditSequence(obj.getEditSequence());
							existing_ret.setFullName(obj.getFullName());
							existing_ret.setIsActive(obj.isActive());
							existing_ret.setName(obj.getName());
							existing_ret.setParentListID(obj.getParentListID());
							existing_ret.setSublevel(obj.getSublevel());
							existing_ret.setTaxLineID(obj.getTaxLineID());
							existing_ret.setTaxLineName(obj.getTaxLineName());
							existing_ret.setTimeCreated(obj.getTimeCreated());
							existing_ret.setTimeModified(obj.getTimeModified());
							existing_ret.setTotalBalance(obj.getTotalBalance());
							existing_ret.save();

							log.addElement("Account " + obj.getFullName() + " updated.");
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
				
				if (!settings.isSyncAccounts())
				{
					settings.setSyncAccounts(true);
					settings.save();
				}
			}

			if (vendorQueryRsObject != null)
			{
				return_response = vendorQueryRsObject;
				
				settings.addMessage("Updating " + vendorQueryRsObject.getVendorRetObjects().size() + " vendors");

				System.out.println("NUM vendorQueryRsObject >" + vendorQueryRsObject.getVendorRetObjects().size());

				if (vendorQueryRsObject.getStatusSeverity().equals("Error"))
					log.addElement(vendorQueryRsObject.getStatusMessage());

				vendorQueryRsObject.getStatusCode();
				vendorQueryRsObject.getStatusSeverity();
				vendorQueryRsObject.getStatusMessage();

				Iterator itr = vendorQueryRsObject.getVendorRetObjects().iterator();
				while (itr.hasNext())
				{
					VendorRet obj = (VendorRet)itr.next();

					try
					{
						// see if there's a matching VendorRet in the db already

						VendorRet existing_ret = null;
						try
						{
							existing_ret = VendorRet.getVendor(company, obj.getListID());
							System.out.println("existing vendor found for list id >" + obj.getListID());
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("vendor not found for list id >" + obj.getListID());

							Iterator new_vendor_itr = VendorRet.getNewVendors(company).iterator();
							while (new_vendor_itr.hasNext())
							{
								VendorRet new_vendor = (VendorRet)new_vendor_itr.next();
								System.out.println("new_vendor.getName() >" + new_vendor.getName());
								System.out.println("obj.getName() >" + obj.getName());
								if (new_vendor.getName().equals(obj.getName()))
								{
									existing_ret = new_vendor;
									existing_ret.setListID(obj.getListID());
									break;
								}
							}
						}

						if (existing_ret == null)
						{
							// no existing.  create new

							obj.setCompany(company);
							obj.save();

							log.addElement("Vendor " + obj.getName() + " created.");
						}
						else
						{
							// already exists.  update...

							existing_ret.setAddress1(obj.getAddress1());
							existing_ret.setAddress2(obj.getAddress2());
							existing_ret.setBalance(obj.getBalance());
							existing_ret.setCity(obj.getCity());
							existing_ret.setEditSequence(obj.getEditSequence());
							existing_ret.setIsActive(obj.isActive());
							existing_ret.setIsVendorEligibleFor1099(obj.isVendorEligibleFor1099());
							existing_ret.setName(obj.getName());
							existing_ret.setPhone(obj.getPhone());
							existing_ret.setFax(obj.getFax());
							existing_ret.setState(obj.getState());
							existing_ret.setTermsFullName(obj.getTermsFullName());
							existing_ret.setTermsListID(obj.getTermsListID());
							existing_ret.save();

							log.addElement("Vendor " + obj.getName() + " updated.");
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
				
				if (!settings.isSyncVendors())
				{
					settings.setSyncVendors(true);
					settings.save();
				}
			}

			if (itemNonInventoryAddRs != null)
			{
				return_response = itemNonInventoryAddRs;
				
				Vector item_ret_objects = itemNonInventoryAddRs.getItemRetObjects();

				System.out.println("NUM itemNonInventoryAddRs >" + item_ret_objects.size());

				if (item_ret_objects.size() > 1)
					throw new IllegalValueException("Multiple ret objects in response to inventory add request.");
				Iterator itr = item_ret_objects.iterator();
				while (itr.hasNext())
				{
					ItemRet obj = (ItemRet)itr.next();
					
					// I need to find the CheckoutCode that corresponds to the request id
					
					String request_id = itemNonInventoryAddRs.getRequestID();
					System.out.println("request_id >" + request_id);
					
					CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCodeForRequestID(request_id);
					
					boolean updated = checkout_code.setItemRet(obj, true);
					System.out.println("updated >" + updated);
					
					checkout_code.save();
					log.addElement("Item " + obj.getName() + " updated.");
					
				}
			}

			if (itemSalesTaxAddRs != null)
			{
				return_response = itemSalesTaxAddRs;
				
				Vector item_ret_objects = itemSalesTaxAddRs.getItemRetObjects();

				System.out.println("NUM itemNonInventoryAddRs >" + item_ret_objects.size());

				if (item_ret_objects.size() > 1)
					throw new IllegalValueException("Multiple ret objects in response to inventory add request.");
				Iterator itr = item_ret_objects.iterator();
				while (itr.hasNext())
				{
					ItemSalesTaxRet obj = (ItemSalesTaxRet)itr.next();
					
					// I need to find the code that corresponds to the request id
					
					String request_id = itemSalesTaxAddRs.getRequestID();
					System.out.println("request_id >" + request_id);
					
					//CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCodeForRequestID(request_id);
					ItemSalesTaxRet sales_tax_item = ItemSalesTaxRet.getSalesTaxItemForRequestID(request_id);
					
					boolean updated = sales_tax_item.setItemRet(obj, true);
					System.out.println("updated >" + updated);
					
					sales_tax_item.save();
					log.addElement("Item " + obj.getName() + " updated.");
					
					ValeoTaxCodeBean tax_code = sales_tax_item.getTaxCode();
					tax_code.setQBListID(sales_tax_item.getListID());
					tax_code.save();
				}
			}

			System.out.println("itemPaymentAddRs >" + itemPaymentAddRs);
			if (itemPaymentAddRs != null)
			{
				return_response = itemPaymentAddRs;
				
				Vector item_payment_ret_objects = itemPaymentAddRs.getItemPaymentRetObjects();

				System.out.println("NUM itemPaymentAddRs >" + item_payment_ret_objects.size());

				if (item_payment_ret_objects.size() > 1)
					throw new IllegalValueException("Multiple ret objects in response to inventory add request.");
				Iterator itr = item_payment_ret_objects.iterator();
				while (itr.hasNext())
				{
					ItemPaymentRet obj = (ItemPaymentRet)itr.next();
					
					// I need to find the object that corresponds to the request id
					
					String request_id = itemPaymentAddRs.getRequestID();
					System.out.println("request_id >" + request_id);
					
					ItemPaymentRet item_payment_ret = ItemPaymentRet.getPaymentItemForRequestID(request_id);
					
					item_payment_ret.setEditSequence(obj.getEditSequence());
					item_payment_ret.setListID(obj.getListID());
					
					item_payment_ret.save();
					log.addElement("Item " + obj.getName() + " updated.");
					
				}
			}

			if (itemQueryRsObject != null)
			{
				return_response = itemQueryRsObject;
				
				int match = 0;
				int update = 0;
				int no_match = 0;
				int multiple = 0;

				if (itemQueryRsObject.getStatusSeverity().equals("Error"))
					log.addElement(itemQueryRsObject.getStatusMessage());

				Iterator itr = itemQueryRsObject.getItemRetObjects().iterator();
				while (itr.hasNext())
				{
					ItemRet obj = (ItemRet)itr.next();
					
					try
					{

						// find the existing CheckoutCode object (if any) for this ItemRet
						// first search using QB ListID

						Vector codes = CheckoutCodeBean.getCheckoutCodes(company, obj);
						if (codes.size() == 1)
						{
							System.out.println("CODE MATCH FOUND FOR >" + obj.getName());
							match++;

							CheckoutCodeBean checkout_code = (CheckoutCodeBean)codes.get(0);
							boolean force_update = (cash_out == null);
							boolean updated = checkout_code.setItemRet(obj, force_update);
							System.out.println("updated >" + updated);
							if (updated)
							{
								checkout_code.save();
								log.addElement("Item " + obj.getName() + " updated.");
							}
						}
						else if (codes.size() == 0)
						{
							System.out.println("NO CODE MATCH FOUND FOR >" + obj.getName());
							no_match++;

							// any reason why I shouldn't create a new CheckoutCode for this item?

							CheckoutCodeBean checkout_code = new CheckoutCodeBean();
							checkout_code.setCode("");
							checkout_code.setCompany(company);
							
							//checkout_code.setType(CheckoutCodeBean.INVENTORY_TYPE); // how do I determine this??? // added plumbing to determine this 6/5/11
							
							switch (obj.getItemType())
							{
								case ItemRet.INVENTORY_TYPE: checkout_code.setType(CheckoutCodeBean.INVENTORY_TYPE); break;
								case ItemRet.NON_INVENTORY_TYPE: checkout_code.setType(CheckoutCodeBean.NON_INVENTORY_TYPE); break;
								case ItemRet.SERVICE_TYPE: checkout_code.setType(CheckoutCodeBean.PROCEDURE_TYPE); break;
								case ItemRet.SUBTOTAL_TYPE: checkout_code.setType(CheckoutCodeBean.SUBTOTAL); break;
								case ItemRet.GROUP_TYPE: checkout_code.setType(CheckoutCodeBean.GROUP_TYPE); break;
								case ItemRet.PAYMENT_TYPE: checkout_code.setType(CheckoutCodeBean.PAYMENT_TYPE); break;
								default: checkout_code.setType(CheckoutCodeBean.INVENTORY_TYPE); break;
							}
								
							boolean force_update = (cash_out == null);
							checkout_code.setItemRet(obj, force_update);
							checkout_code.save();
							log.addElement("Item " + obj.getName() + " created.");
						}
						else
						{
							System.out.println("MULTIPLE CODE MATCH FOUND FOR >" + obj.getName());
							multiple++;

							log.addElement("Error: Multiple codes found for item " + obj.getName());


							// multiple codes found, but none for the QB ListID, presumably.
							// go ahead and create so that I actually have an item to match the QB ListID otherwise I may never have a matching item

							CheckoutCodeBean checkout_code = new CheckoutCodeBean();
							checkout_code.setCode("");
							checkout_code.setCompany(company);
							
							//checkout_code.setType(CheckoutCodeBean.INVENTORY_TYPE); // how do I determine this???
							
							switch (obj.getItemType())
							{
								case ItemRet.INVENTORY_TYPE: checkout_code.setType(CheckoutCodeBean.INVENTORY_TYPE); break;
								case ItemRet.NON_INVENTORY_TYPE: checkout_code.setType(CheckoutCodeBean.NON_INVENTORY_TYPE); break;
								case ItemRet.SERVICE_TYPE: checkout_code.setType(CheckoutCodeBean.PROCEDURE_TYPE); break;
								case ItemRet.SUBTOTAL_TYPE: checkout_code.setType(CheckoutCodeBean.SUBTOTAL); break;
								case ItemRet.GROUP_TYPE: checkout_code.setType(CheckoutCodeBean.GROUP_TYPE); break;
								case ItemRet.PAYMENT_TYPE: checkout_code.setType(CheckoutCodeBean.PAYMENT_TYPE); break;
								default: checkout_code.setType(CheckoutCodeBean.INVENTORY_TYPE); break;
							}
							
							boolean force_update = (cash_out == null);
							checkout_code.setItemRet(obj, force_update);
							checkout_code.save();
							log.addElement("Item " + obj.getName() + " created.");
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}

				System.out.println("match >" + match);
				System.out.println("update >" + update);
				System.out.println("no_match >" + no_match);
				System.out.println("multiple >" + multiple);

				itr = itemQueryRsObject.getItemSalesTaxRetObjects().iterator();
				while (itr.hasNext())
				{
					ItemSalesTaxRet obj = (ItemSalesTaxRet)itr.next();
					System.out.println("obj >" + obj);

					try
					{
						// see if there's a matching ItemSalesTaxRet in the db already

						ItemSalesTaxRet existing_ret = null;
						try
						{
							existing_ret = ItemSalesTaxRet.getSalesTaxItem(company, obj.getListID());
							System.out.println("existing sales tax item found for list id >" + obj.getListID());
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("sales tax item not found for list id >" + obj.getListID());

							/*
							Iterator new_vendor_itr = VendorRet.getNewVendors(company).iterator();
							while (new_vendor_itr.hasNext())
							{
								VendorRet new_vendor = (VendorRet)new_vendor_itr.next();
								System.out.println("new_vendor.getName() >" + new_vendor.getName());
								System.out.println("obj.getName() >" + obj.getName());
								if (new_vendor.getName().equals(obj.getName()))
								{
									existing_ret = new_vendor;
									existing_ret.setListID(obj.getListID());
									break;
								}
							}
							 */
						}

						if (existing_ret == null)
						{
							// no existing.  create new

							obj.setCompany(company);
							obj.save();

							// create a correspoding ValeoTaxCodeBean

							ValeoTaxCodeBean tax_code = new ValeoTaxCodeBean();
							tax_code.setCode(obj.getName());
							tax_code.setCompany(company);
							tax_code.setPercentage(obj.getTaxRate());
							tax_code.setQBListID(obj.getListID());
							tax_code.save();

							log.addElement("Sales Tax Item " + obj.getName() + " created.");
						}
						else
						{
							// already exists.  update...

							existing_ret.setEditSequence(obj.getEditSequence());
							existing_ret.setIsActive(obj.isActive());
							existing_ret.setItemDesc(obj.getItemDesc());
							existing_ret.setName(obj.getName());
							existing_ret.setTaxRate(obj.getTaxRate());
							existing_ret.setTaxVendorFullName(obj.getTaxVendorFullName());
							existing_ret.setTaxVendorListID(obj.getTaxVendorListID());
							existing_ret.setTimeCreated(obj.getTimeCreated());
							existing_ret.setTimeModified(obj.getTimeModified());
							existing_ret.save();

							// find the existing ValeoTaxCodeBean

							ValeoTaxCodeBean tax_code = null;
							try
							{
								tax_code = (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode(company, obj.getListID());
								tax_code.setCode(obj.getName());
								tax_code.setPercentage(obj.getTaxRate());
								tax_code.save();
							}
							catch (ObjectNotFoundException obj_not_found)
							{
								tax_code = new ValeoTaxCodeBean();
								tax_code.setCode(obj.getName());
								tax_code.setCompany(company);
								tax_code.setPercentage(obj.getTaxRate());
								tax_code.setQBListID(obj.getListID());
								tax_code.save();
							}

							log.addElement("Sales Tax Item " + obj.getName() + " updated.");
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}

				itr = itemQueryRsObject.getItemSalesTaxGroupRetObjects().iterator();
				while (itr.hasNext())
				{
					ItemSalesTaxGroupRet obj = (ItemSalesTaxGroupRet)itr.next();

					try
					{
						// see if there's a matching ItemSalesTaxRet in the db already

						ItemSalesTaxGroupRet existing_ret = null;
						try
						{
							existing_ret = ItemSalesTaxGroupRet.getSalesTaxGroupItem(company, obj.getListID());
							System.out.println("existing sales tax item group found for list id >" + obj.getListID());
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("sales tax item group not found for list id >" + obj.getListID());

							/*
							Iterator new_vendor_itr = VendorRet.getNewVendors(company).iterator();
							while (new_vendor_itr.hasNext())
							{
								VendorRet new_vendor = (VendorRet)new_vendor_itr.next();
								System.out.println("new_vendor.getName() >" + new_vendor.getName());
								System.out.println("obj.getName() >" + obj.getName());
								if (new_vendor.getName().equals(obj.getName()))
								{
									existing_ret = new_vendor;
									existing_ret.setListID(obj.getListID());
									break;
								}
							}
							 */
						}

						if (existing_ret == null)
						{
							// no existing.  create new

							obj.setCompany(company);
							obj.save();

							// create a correspoding ValeoTaxCodeBean...

							ValeoTaxCodeBean tax_code = new ValeoTaxCodeBean();
							tax_code.setCode(obj.getName());
							tax_code.setCompany(company);
							tax_code.setQBListID(obj.getListID());
							tax_code.setIsGroup(true);
							tax_code.save();

							log.addElement("Sales Tax Group Item " + obj.getName() + " created.");
						}
						else
						{
							// already exists.  update...

							existing_ret.setEditSequence(obj.getEditSequence());
							existing_ret.setIsActive(obj.isActive());
							existing_ret.setItemDesc(obj.getItemDesc());
							existing_ret.setName(obj.getName());
							existing_ret.setTimeCreated(obj.getTimeCreated());
							existing_ret.setTimeModified(obj.getTimeModified());
							existing_ret.save();

							// find the existing ValeoTaxCodeBean..
							
							try
							{
								ValeoTaxCodeBean tax_code = (ValeoTaxCodeBean)ValeoTaxCodeBean.getTaxCode(company, obj.getListID());
								tax_code.setCode(obj.getName());
								tax_code.setIsGroup(true);
								tax_code.save();
							}
							catch (ObjectNotFoundException x)
							{
								ValeoTaxCodeBean tax_code = new ValeoTaxCodeBean();
								tax_code.setCode(obj.getName());
								tax_code.setCompany(company);
								tax_code.setQBListID(obj.getListID());
								tax_code.setIsGroup(true);
								tax_code.save();
							}

							log.addElement("Sales Tax Group Item " + obj.getName() + " updated.");
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}

				itr = itemQueryRsObject.getItemPaymentObjects().iterator();
				while (itr.hasNext())
				{
					ItemPaymentRet obj = (ItemPaymentRet)itr.next();

					try
					{
						// see if there's a matching ItemPaymentRet in the db already

						ItemPaymentRet existing_ret = null;
						try
						{
							existing_ret = ItemPaymentRet.getPaymentItem(company, obj.getListID());
							System.out.println("existing payment item found for list id >" + obj.getListID());
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("payment item not found for list id >" + obj.getListID());
						}

						if (existing_ret == null)
						{
							// no existing.  create new

							obj.setCompany(company);
							obj.save();

							log.addElement("Payment Item " + obj.getName() + " created.");
						}
						else
						{
							// already exists.  update...

							existing_ret.setEditSequence(obj.getEditSequence());
							existing_ret.setIsActive(obj.isActive());
							existing_ret.setName(obj.getName());
							existing_ret.setPaymentMethodFullName(obj.getPaymentMethodFullName());
							existing_ret.setPaymentMethodListID(obj.getPaymentMethodListID());
							existing_ret.setTimeCreated(obj.getTimeCreated());
							existing_ret.setTimeModified(obj.getTimeModified());
							existing_ret.save();

							log.addElement("Payment Item " + obj.getName() + " updated.");
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}

				itr = itemQueryRsObject.getItemGroupRetObjects().iterator();
				while (itr.hasNext())
				{
					ItemGroupRet obj = (ItemGroupRet)itr.next();

					try
					{
						// find the existing CheckoutCode object (if any) for this ItemGroupRet
						// first search using QB ListID

						Vector codes = CheckoutCodeBean.getCheckoutCodes(company, obj);
						if (codes.size() == 1)
						{
							System.out.println("CODE MATCH FOUND FOR GROUP >" + obj.getName());

							CheckoutCodeBean checkout_code = (CheckoutCodeBean)codes.get(0);
							checkout_code.setType(CheckoutCodeBean.GROUP_TYPE);
							boolean force_update = (cash_out == null);
							boolean updated = checkout_code.setItemGroupRet(obj, force_update);
							System.out.println("updated >" + updated);

							boolean children_added = false;
							Vector children = obj.getItemGroupRetObjects();
							Iterator children_itr = children.iterator();
							while (children_itr.hasNext())
							{
								ItemGroupLine child = (ItemGroupLine)children_itr.next();
								checkout_code.add(child);
								children_added = true;
							}

							if (updated || children_added)
							{
								checkout_code.save();
								log.addElement("Item Group " + obj.getName() + " updated.");
							}
						}
						else if (codes.size() == 0)
						{
							System.out.println("NO CODE MATCH FOUND FOR GROUP >" + obj.getName());

							// any reason why I shouldn't create a new CheckoutCode for this item?

							CheckoutCodeBean checkout_code = new CheckoutCodeBean();
							checkout_code.setCode("");
							checkout_code.setCompany(company);
							boolean force_update = (cash_out == null);
							checkout_code.setItemGroupRet(obj, force_update);
							checkout_code.setAmount(BigDecimal.ZERO);
							checkout_code.setType(CheckoutCodeBean.GROUP_TYPE);

							Vector children = obj.getItemGroupRetObjects();
							Iterator children_itr = children.iterator();
							while (children_itr.hasNext())
							{
								ItemGroupLine child = (ItemGroupLine)children_itr.next();
								checkout_code.add(child);
							}

							checkout_code.save();
							log.addElement("Item " + obj.getName() + " created.");
						}
						else
							throw new UniqueObjectNotFoundException(obj.getName());
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
				
				if (!settings.isSyncInventory())
				{
					settings.setSyncInventory(true);
					settings.save();
				}
			}

			if (salesTaxCodeQueryRsObject != null)
			{
				return_response = salesTaxCodeQueryRsObject;
				
				settings.addMessage("Updating " + salesTaxCodeQueryRsObject.getItemRetObjects().size() + " vendors");

				System.out.println("NUM salesTaxCodeQueryRsObject >" + salesTaxCodeQueryRsObject.getItemRetObjects().size());

				if (salesTaxCodeQueryRsObject.getStatusSeverity().equals("Error"))
					log.addElement(salesTaxCodeQueryRsObject.getStatusMessage());

				Iterator itr = salesTaxCodeQueryRsObject.getItemRetObjects().iterator();
				while (itr.hasNext())
				{
					SalesTaxCodeRet obj = (SalesTaxCodeRet)itr.next();

					try
					{
						// see if there's a matching SalesTaxCodeRet in the db already

						SalesTaxCodeRet existing_ret = null;
						try
						{
							existing_ret = SalesTaxCodeRet.getSalesTaxCode(company, obj.getListID());
							System.out.println("existing vendor found for list id >" + obj.getListID());
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("vendor not found for list id >" + obj.getListID());
						}

						if (existing_ret == null)
						{
							// no existing.  create new

							obj.setCompany(company);
							obj.save();

							log.addElement("Sales Tax Code " + obj.getName() + " created.");
						}
						else
						{
							// already exists.  update...

							existing_ret.setEditSequence(obj.getEditSequence());
							existing_ret.setIsActive(obj.isActive());
							existing_ret.setDesc(obj.getDesc());
							existing_ret.setName(obj.getName());
							existing_ret.setTimeCreated(obj.getTimeCreated());
							existing_ret.setTimeModified(obj.getTimeModified());
							existing_ret.save();

							log.addElement("Sales Tax Code " + obj.getName() + " updated.");
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}
			
			if (salesReceiptQueryRsObject != null)
			{
				return_response = salesReceiptQueryRsObject;
				
				System.out.println("NUM SalesReceiptQueryRsObjects >" + salesReceiptQueryRsObject.getSalesReceiptRetObjects().size());
				
				Iterator itr = salesReceiptQueryRsObject.getSalesReceiptRetObjects().iterator();
				while (itr.hasNext())
				{
					try
					{
						SalesReceiptRet obj = (SalesReceiptRet)itr.next();
						//if (obj.getCustomerListID() != null && obj.getCustomerListID().equals("80000F3D-1289519344"))
						//{
							//System.out.println("found sales receipt for Amy");

							// does a corresponding sales receipt already exist?
							SalesReceiptRet existing_ret = null;
							System.out.println("searching for SalesReceipt for sales receipt number >" + obj.getSalesReceiptNumber());
							try
							{
								// search using sales receipt number
								existing_ret = SalesReceiptRet.getSalesReceipt(company, obj.getSalesReceiptNumber());
								System.out.println("receipt found!");

								// search using txn id

							}
							catch (ObjectNotFoundException x)
							{
								System.out.println("receipt not found!");


								System.out.println("searching for SalesReceipt for txn id >" + obj.getTxnID());
								try
								{
									existing_ret = SalesReceiptRet.getSalesReceipt(company, obj.getTxnID());
								}
								catch (ObjectNotFoundException y)
								{
									System.out.println("receipt still not found!?!");
								}

							}

							boolean create_orderlines = false;

							if (existing_ret == null)
							{
								// new transaction, I guess

								obj.setRequestId(salesReceiptQueryRsObject.getRequestID());
								obj.setCompany(company);
								obj.save();

								create_orderlines = true;
							}
							else
							{
								// transaction already exists.  update...

								try
								{
									existing_ret.setAssociate(obj.getAssociate());
								}
								catch (ObjectNotFoundException associate_not_found)
								{
									//associate_not_found.printStackTrace();
								}

								try
								{
									existing_ret.setCashier(obj.getCashier());
								}
								catch (ObjectNotFoundException cashier_not_found)
								{
									//cashier_not_found.printStackTrace();
								}

								existing_ret.setCompany(company);
								if (existing_ret.getRequestId() == null || existing_ret.getRequestId().equals(""))
									existing_ret.setRequestId(salesReceiptQueryRsObject.getRequestID());
								existing_ret.setCustomerListID(obj.getCustomerListID());
								existing_ret.setSubtotal(obj.getSubtotal());
								existing_ret.setTaxAmount(obj.getTaxAmount());
								existing_ret.setTenderType(obj.getTenderType());
								existing_ret.setTimeModified(obj.getTimeModified());
								existing_ret.setTotal(obj.getTotal());
								existing_ret.setTxnDate(obj.getTxnDate());
								existing_ret.setTxnID(obj.getTxnID());
								existing_ret.setSalesReceiptNumber(obj.getSalesReceiptNumber());
								existing_ret.setSalesReceiptType(obj.getSalesReceiptType());
								existing_ret.setHistoryDocStatus(obj.getHistoryDocStatus());

								existing_ret.setSalesRecieptItemRetObjects(obj.getSalesRecieptItemRetObjects());
								existing_ret.setTenderRetObjects(obj.getTenderRetObjects());

								existing_ret.save();

								/*
								SalesReceiptItemRet.deleteSalesReceiptItems(existing_ret);
								TenderRet.deleteSalesReceiptItems(existing_ret);
								 */

								obj = existing_ret;
							}


							Vector order_lines = new Vector();
							UKOnlinePersonBean client = null;
							try
							{
								client = UKOnlinePersonBean.getPersonByQBListId(company, obj.getCustomerListID(), true);
							}
							catch (ObjectNotFoundException x)
							{
								CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Unable to find person specified by CustomerListID in SalesReceiptRet");
								throw x;
							}

							ValeoOrderBean order_obj = null;
							try
							{
								order_obj = (ValeoOrderBean)SalesReceiptRet.getOrder(company, obj.getTxnID());
							}
							catch (ObjectNotFoundException x)
							{
								if (existing_ret != null)
									System.out.println("Whaazzaa!?!?!  SalesReceipt found without order");
								if (create_orderlines) // create a new Order only if this is also a new SalesReceiptRet
									order_obj = new ValeoOrderBean();
							}
							
							if (order_obj != null)
							{
								order_obj.setCompany(company);
								order_obj.setAuthorizationCode(obj.getTxnID());
								order_obj.setOrderDate(obj.getTxnDate());
								order_obj.setPerson(client);
								order_obj.setStatus(OrderBean.SALES_RECEIPT_ORDER_STATUS);
								order_obj.setIsUpdated(true);
								order_obj.setIsPaid(true);
								order_obj.setRequestID(salesReceiptQueryRsObject.getRequestID());
							}

							//StringBuffer item_buf = new StringBuffer();
							
							HashMap tender_amount_hash = new HashMap(3);

							BigDecimal tender_total = BigDecimal.ZERO;
							BigDecimal tax_total = BigDecimal.ZERO;
							if (create_orderlines)
							{
								System.out.println("NUM SalesRecieptLineRetObjects >" + obj.getSalesRecieptLineRetObjects().size());
								
								Iterator item_itr = obj.getSalesRecieptLineRetObjects().iterator();
								while (item_itr.hasNext())
								{
									SalesReceiptLineRet item_obj = (SalesReceiptLineRet)item_itr.next();

									/*
									System.out.println("item_obj.getItemListId() >" + item_obj.getItemListId());
									System.out.println("item_obj.getAmount() >" + item_obj.getAmount());
									System.out.println("item_obj.getTxnLineID() >" + item_obj.getTxnLineID());
									item_obj.setSalesReceipt(obj);
									item_obj.save();
									*/

									//SalesReceiptItemRet item_obj = (SalesReceiptItemRet)item_itr.next();

									item_obj.setSalesReceipt(obj);
									item_obj.save();

									// is there a corresponding CheckoutCode for this SalesReceiptItemRet?

									CheckoutCodeBean checkout_code = null;
									
									boolean is_sales_tax = false;
									boolean is_payment = false;

									try
									{
										System.out.println("searching for CheckoutCode using list id >" + item_obj.getItemListId());
										checkout_code = CheckoutCodeBean.getCheckoutCode(company, item_obj.getItemListId());
									}
									catch (ObjectNotFoundException x)
									{
										// corresponding checkout code not found
										
										// is this line item tax?

										try
										{
											ItemSalesTaxRet.getSalesTaxItem(company, item_obj.getItemListId());
											is_sales_tax = true;
											
											BigDecimal line_total = new BigDecimal(item_obj.getAmount());
											tax_total = tax_total.add(line_total);
										}
										catch (ObjectNotFoundException y)
										{
											
										}
										
										// is this line item a payment?

										try
										{
											ItemPaymentRet payment_ret = ItemPaymentRet.getPaymentItem(company, item_obj.getItemListId());
											is_payment = true;
											
											TenderRet tender = new TenderRet();
											tender.setAmount(item_obj.getAmount() * -1);
											switch (payment_ret.getCCType())
											{
												case ItemPaymentRet.CASH: tender.setType(TenderRet.CASH_TENDER_TYPE); break;
												case ItemPaymentRet.CHECK: tender.setType(TenderRet.CHECK_TENDER_TYPE); break;
												case ItemPaymentRet.DINERS_CLUB: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
												case ItemPaymentRet.DISCOVER: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
												case ItemPaymentRet.EN_ROUTE: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
												case ItemPaymentRet.GIFT_CARD: tender.setType(TenderRet.GIFT_CARD_TENDER_TYPE); break;
												case ItemPaymentRet.GIFT_CERT: tender.setType(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE); break;
												case ItemPaymentRet.MASTERCARD: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
												case ItemPaymentRet.VISA: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
											}
											
											tender.setClient(client);
											tender.setCompany(company);
											if (order_obj != null)
												tender.setOrder(order_obj);
											tender.setSalesReceipt(obj);
											tender.setTenderDate(obj.getTxnDate());
											
											BigDecimal line_total = new BigDecimal(item_obj.getAmount() * -1);
											tender_total = tender_total.add(line_total);
											obj.add(tender);
										}
										catch (ObjectNotFoundException y)
										{
											
										}
										
										if (!is_sales_tax && !is_payment)
										{
											// corresponding checkout code still not found.  create one

											System.out.println("corresponding checkout code not found.  create one");

											checkout_code = new CheckoutCodeBean();
											checkout_code.setCode("");
											checkout_code.setCompany(company);
											checkout_code.setType(CheckoutCodeBean.INVENTORY_TYPE); // how do I determine this???
											checkout_code.setListID(item_obj.getItemListId());
											checkout_code.setAmount(new BigDecimal(item_obj.getAmount()));
											checkout_code.setDescription(item_obj.getDesc());
											//checkout_code.setItemNumber(item_obj.get.getItemNumber());
											System.out.println("SAVING CheckoutCode >" + item_obj.getItemListId() + ", DESC >" + item_obj.getDesc());
											if (item_obj.getDesc() != null)
												checkout_code.save();
										}
									}

									if (checkout_code != null)
									{
										if ((checkout_code.getType() == CheckoutCodeBean.INVENTORY_TYPE) ||
												(checkout_code.getType() == CheckoutCodeBean.PROCEDURE_TYPE))
										{
											// create checkout_orderline(s) for this code

											CheckoutOrderline order_line = new CheckoutOrderline();
											if (item_obj.hasRate())
												order_line.setPrice(new BigDecimal(item_obj.getRate()));
											else
												order_line.setPrice(new BigDecimal(item_obj.getAmount()));
											order_line.setActualAmount(new BigDecimal(item_obj.getAmount()));
											order_line.setCost(checkout_code.getOrderCost());
											order_line.setCheckoutCodeId(checkout_code.getId());
											try
											{
												float qty = item_obj.getQuantity();
												order_line.setQuantity(new BigDecimal(qty));
											}
											catch (IllegalValueException x)
											{
												order_line.setQuantity(BigDecimal.ONE);
											}
											order_line.setOrderstatus(OrderBean.SALES_RECEIPT_ORDER_STATUS);

											order_lines.addElement(order_line);
										}
									}

								}
											
								if (obj.getPaymentMethod() != null)
								{
									PaymentMethodRet payment_method = obj.getPaymentMethod();
									ItemPaymentRet payment_ret = ItemPaymentRet.getPaymentItem(payment_method);
									
									TenderRet tender = new TenderRet();
									BigDecimal order_total = new BigDecimal(obj.getTotal());
									tender.setAmount(order_total.subtract(tender_total).floatValue());
									switch (payment_ret.getCCType())
									{
										case ItemPaymentRet.CASH: tender.setType(TenderRet.CASH_TENDER_TYPE); break;
										case ItemPaymentRet.CHECK: tender.setType(TenderRet.CHECK_TENDER_TYPE); tender.setCheckNumber(obj.getCheckNumber()); break;
										case ItemPaymentRet.DINERS_CLUB: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
										case ItemPaymentRet.DISCOVER: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
										case ItemPaymentRet.EN_ROUTE: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
										case ItemPaymentRet.GIFT_CARD: tender.setType(TenderRet.GIFT_CARD_TENDER_TYPE); break;
										case ItemPaymentRet.GIFT_CERT: tender.setType(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE); break;
										case ItemPaymentRet.MASTERCARD: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
										case ItemPaymentRet.VISA: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
									}

									tender.setClient(client);
									tender.setCompany(company);
									if (order_obj != null)
										tender.setOrder(order_obj);
									tender.setTenderDate(obj.getTxnDate());
									tender.setSalesReceipt(obj);
									
									obj.add(tender);
								}
								
							}

							// process the tenders

							Iterator tender_itr = obj.getTenderRetObjects().iterator();
							while (tender_itr.hasNext())
							{
								TenderRet tender_obj = (TenderRet)tender_itr.next();
								if (order_obj != null)
									tender_obj.setOrder(order_obj);
								tender_obj.setSalesReceipt(obj);
								tender_obj.setCompany(company);
								tender_obj.save();
								
								tender_amount_hash.put(tender_obj, tender_obj.getAmountBigDecimal());
							}
							
							BigDecimal order_total = tender_total.add(new BigDecimal(obj.getTotal()));

							if (order_obj != null)
							{
								order_obj.setSubtotal(order_total.subtract(tax_total));
								order_obj.setTax(tax_total);
								order_obj.setTotal(order_total);
								order_obj.setOrders(order_lines);
								order_obj.setTenders(obj.getTenderRetObjects());
								order_obj.setTenderPaymentInfo(tender_amount_hash);
								order_obj.save();
							}

							/*
							CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "POS Sales Receipt Processed",
									"CLIENT >" + client.getLabel() + "\n" +
									"TOTAL >" + obj.getTotal() + "\n" +
									item_buf.toString());
							 */


						//}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}

				}
				
			}

			if (salesReceiptAddRsObject != null)
			{
				return_response = salesReceiptAddRsObject;
				
				System.out.println("NUM SalesReceiptRetObjects >" + salesReceiptAddRsObject.getSalesReceiptRetObjects().size());

				if (salesReceiptAddRsObject.getStatusSeverity().equals("Error"))
					log.addElement(salesReceiptAddRsObject.getStatusMessage());

				Iterator itr = salesReceiptAddRsObject.getSalesReceiptRetObjects().iterator();
				while (itr.hasNext())
				{
					try
					{
						SalesReceiptRet obj = (SalesReceiptRet)itr.next();

						// does a corresponding sales receipt already exist? it shouldn't since this is an add response...

						SalesReceiptRet existing_ret = null;
						System.out.println("searching for SalesReceipt for TxnNumber >" + obj.getTxnNumber());
						try
						{
							// search using sales receipt number
							existing_ret = SalesReceiptRet.getSalesReceiptForTxnNumber(company, obj.getTxnNumber());
							throw new ObjectAlreadyExistsException("Sales Receipt already exists for transaction number " + obj.getTxnNumber());
						}
						catch (ObjectNotFoundException x)
						{
							// expected result
						}

						obj.setRequestId(salesReceiptAddRsObject.getRequestID());
						obj.setCompany(company);
						obj.save();

						UKOnlinePersonBean client = UKOnlinePersonBean.getPersonByQBListId(company, obj.getCustomerListID(), true);
						if (client != null)
							log.addElement("Sales receipt " + obj.getTotal() + " created for " + client.getLabel());


						String requestID = salesReceiptAddRsObject.getRequestID();

						// find the Order that matches this request

						OrderBean order_obj = OrderBean.getOrder(company, requestID);

						// mark the order as having been communicated to QBFS

						order_obj.setAuthorizationCode(obj.getTxnID()); // is there a correspoding thing like this for invoices?  yes, done, I guess (feeling very tired today 5/5/11) - nice :) don't remember making that comment (5/14)
						order_obj.setIsUpdated(true);
						order_obj.save();


						System.out.println("NUM SalesRecieptLineRetObjects >" + obj.getSalesRecieptLineRetObjects().size());
						Iterator item_itr = obj.getSalesRecieptLineRetObjects().iterator();
						while (item_itr.hasNext())
						{
							SalesReceiptLineRet item_obj = (SalesReceiptLineRet)item_itr.next();
							System.out.println("item_obj.getItemListId() >" + item_obj.getItemListId());
							System.out.println("item_obj.getAmount() >" + item_obj.getAmount());
							System.out.println("item_obj.getTxnLineID() >" + item_obj.getTxnLineID());
							item_obj.setSalesReceipt(obj);
							item_obj.save();
						}

						// not sure how this'll apply to QBFS....  ignore for now since this all should have been created based on this order anyway...

						/*
						CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "POS Sales Receipt Processed",
								"CLIENT >" + client.getLabel() + "\n" +
								"TOTAL >" + obj.getTotal() + "\n" +
								item_buf.toString());
						System.out.println("NUM SalesRecieptItemRetObjects >" + obj.getSalesRecieptItemRetObjects().size());
						 *
						 */

					}
					catch (ObjectAlreadyExistsException x)
					{
						log.addElement(x.getMessage());
						x.printStackTrace();
					}

				}
			}
			
			if (invoiceQueryRsObject != null)
			{
				return_response = invoiceQueryRsObject;
				
				System.out.println("NUM InvoiceRetObjects >" + invoiceQueryRsObject.getInvoiceRetObjects().size());

				Iterator itr = invoiceQueryRsObject.getInvoiceRetObjects().iterator();
				while (itr.hasNext())
				{
					try
					{
						InvoiceRet obj = (InvoiceRet)itr.next();
						
						//if (obj.getCustomerListID() != null && obj.getCustomerListID().equals("800003E4-1230648645"))
						//{
							//System.out.println("found invoice for Derrick");

							// does a corresponding invoice already exist?
							
							InvoiceRet existing_ret = null;
							System.out.println("searching for Invoice for TxnNumber >" + obj.getTxnNumber());
							try
							{
								// search using sales txn number
								existing_ret = InvoiceRet.getInvoiceForTxnNumber(company, obj.getTxnNumber());
								System.out.println("Existing invoice found for txn id >" + obj.getTxnID());
							}
							catch (ObjectNotFoundException x)
							{
								System.out.println("invoice not found!");
							}

							boolean create_orderlines = false;

							if (existing_ret == null)
							{
								// new transaction, I guess

								obj.setRequestId(invoiceQueryRsObject.getRequestID());
								obj.setCompany(company);
								obj.save();
								
								create_orderlines = true;
							}
							else
							{
								// transaction already exists.  update...
								
								// don't bother with an update mechanism for now.  only concerned with new invoices atm
								
								/*

								try
								{
									existing_ret.setAssociate(obj.getAssociate());
								}
								catch (ObjectNotFoundException associate_not_found)
								{
									//associate_not_found.printStackTrace();
								}

								try
								{
									existing_ret.setCashier(obj.getCashier());
								}
								catch (ObjectNotFoundException cashier_not_found)
								{
									//cashier_not_found.printStackTrace();
								}

								existing_ret.setCompany(company);
								if (existing_ret.getRequestId() == null || existing_ret.getRequestId().equals(""))
									existing_ret.setRequestId(salesReceiptQueryRsObject.getRequestID());
								existing_ret.setCustomerListID(obj.getCustomerListID());
								existing_ret.setSubtotal(obj.getSubtotal());
								existing_ret.setTaxAmount(obj.getTaxAmount());
								existing_ret.setTenderType(obj.getTenderType());
								existing_ret.setTimeModified(obj.getTimeModified());
								existing_ret.setTotal(obj.getTotal());
								existing_ret.setTxnDate(obj.getTxnDate());
								existing_ret.setTxnID(obj.getTxnID());
								existing_ret.setSalesReceiptNumber(obj.getSalesReceiptNumber());
								existing_ret.setSalesReceiptType(obj.getSalesReceiptType());
								existing_ret.setHistoryDocStatus(obj.getHistoryDocStatus());

								existing_ret.setSalesRecieptItemRetObjects(obj.getSalesRecieptItemRetObjects());
								existing_ret.setTenderRetObjects(obj.getTenderRetObjects());

								existing_ret.save();

								 * 
								 */
								
								obj = existing_ret;
							}


							Vector order_lines = new Vector();
							UKOnlinePersonBean client = null;
							try
							{
								//client = UKOnlinePersonBean.getPersonByPoSListId(company, obj.getCustomerListID());
								client = UKOnlinePersonBean.getPersonByQBListId(company, obj.getCustomerListID(), true);
							}
							catch (ObjectNotFoundException x)
							{
								CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Unable to find person specified by CustomerListID in SalesReceiptRet");
								throw x;
							}

							ValeoOrderBean order_obj = null;
							try
							{
								order_obj = (ValeoOrderBean)InvoiceRet.getOrder(company, obj.getTxnID());
							}
							catch (ObjectNotFoundException x)
							{
								// try harder to find a matching order since there was a time when I wasn't recording TXN_ID's for orders /sigh
								// search for orders on this day to try and match up with this invoice
								
								boolean match_found = false;
								Vector candidate_matching_orders = ValeoOrderBean.getOrdersForDate(client, obj.getTxnDate());
								System.out.println("candidate_matching_orders >" + candidate_matching_orders.size());
								Iterator candidate_itr = candidate_matching_orders.iterator();
								while (!match_found && candidate_itr.hasNext())
								{
									ValeoOrderBean candidate_match = (ValeoOrderBean)candidate_itr.next();
									System.out.println("candidate matching order >" + candidate_match.getLabel());
									match_found = candidate_match.equals(obj);
								}
								
								System.out.println("match_found >" + match_found);
								
								//if (create_orderlines)
								if (!match_found)
								{
									System.out.println("no match found - creating new order");
									order_obj = new ValeoOrderBean();
									
									create_orderlines = true;
								}
							}
							
							System.out.println("obj.getSubtotal() >" + obj.getSubtotal());

							if (order_obj != null)
							{
								order_obj.setCompany(company);
								order_obj.setAuthorizationCode(obj.getTxnID());
								order_obj.setOrderDate(obj.getTxnDate());
								order_obj.setPerson(client);
								order_obj.setStatus(OrderBean.SALES_RECEIPT_ORDER_STATUS);
								order_obj.setIsUpdated(true);
								order_obj.setIsPaid(true);
								order_obj.setRequestID(invoiceQueryRsObject.getRequestID());
							}

							//StringBuffer item_buf = new StringBuffer();
							
							HashMap tender_amount_hash = new HashMap(3);

							BigDecimal tender_total = BigDecimal.ZERO;
							BigDecimal tax_total = BigDecimal.ZERO;
							BigDecimal order_total = BigDecimal.ZERO;
							
							System.out.println("NUM InvoiceLineRetObjects >" + obj.getInvoiceLineRetObjects().size());

							Iterator item_itr = obj.getInvoiceLineRetObjects().iterator();
							while (item_itr.hasNext())
							{
								InvoiceLineRet item_obj = (InvoiceLineRet)item_itr.next();

								/*
								System.out.println("item_obj.getItemListId() >" + item_obj.getItemListId());
								System.out.println("item_obj.getAmount() >" + item_obj.getAmount());
								System.out.println("item_obj.getTxnLineID() >" + item_obj.getTxnLineID());
								item_obj.setSalesReceipt(obj);
								item_obj.save();
								*/

								//SalesReceiptItemRet item_obj = (SalesReceiptItemRet)item_itr.next();

								InvoiceLineRet existing_line_ret;
								try
								{
									existing_line_ret = InvoiceLineRet.getInvoiceLine(existing_ret, item_obj.getTxnLineID());
									System.out.println("existing line ret found");
								}
								catch (ObjectNotFoundException x)
								{
									item_obj.setInvoice(obj);
									item_obj.save();
								}

								// is there a corresponding CheckoutCode for this SalesReceiptItemRet?

								CheckoutCodeBean checkout_code = null;

								boolean is_sales_tax = false;
								boolean is_payment = false;

								try
								{
									System.out.println("searching for CheckoutCode using list id >" + item_obj.getItemListId());
									checkout_code = CheckoutCodeBean.getCheckoutCode(company, item_obj.getItemListId());
								}
								catch (ObjectNotFoundException x)
								{
									// corresponding checkout code not found

									// is this line item tax?

									try
									{
										ItemSalesTaxRet.getSalesTaxItem(company, item_obj.getItemListId());
										is_sales_tax = true;

										BigDecimal line_total = new BigDecimal(item_obj.getAmount());
										tax_total = tax_total.add(line_total);
									}
									catch (ObjectNotFoundException y)
									{

									}

									// is this line item a payment?

									try
									{
										ItemPaymentRet payment_ret = ItemPaymentRet.getPaymentItem(company, item_obj.getItemListId());
										is_payment = true;

										TenderRet tender = new TenderRet();
										tender.setAmount(item_obj.getAmount() * -1);
										switch (payment_ret.getCCType())
										{
											case ItemPaymentRet.CASH: tender.setType(TenderRet.CASH_TENDER_TYPE); break;
											case ItemPaymentRet.CHECK: tender.setType(TenderRet.CHECK_TENDER_TYPE); break;
											case ItemPaymentRet.DINERS_CLUB: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
											case ItemPaymentRet.DISCOVER: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
											case ItemPaymentRet.EN_ROUTE: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
											case ItemPaymentRet.GIFT_CARD: tender.setType(TenderRet.GIFT_CARD_TENDER_TYPE); break;
											case ItemPaymentRet.GIFT_CERT: tender.setType(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE); break;
											case ItemPaymentRet.MASTERCARD: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
											case ItemPaymentRet.VISA: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
										}

										tender.setClient(client);
										tender.setCompany(company);
										if (order_obj != null)
											tender.setOrder(order_obj);
										//tender.setSalesReceipt(obj);
										tender.setTenderDate(obj.getTxnDate());

										BigDecimal line_total = new BigDecimal(item_obj.getAmount() * -1);
										tender_total = tender_total.add(line_total);
										obj.add(tender);
									}
									catch (ObjectNotFoundException y)
									{

									}

									if (!is_sales_tax && !is_payment)
									{
										// corresponding checkout code still not found.  create one

										System.out.println("corresponding checkout code not found.  create one");

										checkout_code = new CheckoutCodeBean();
										checkout_code.setCode("");
										checkout_code.setCompany(company);
										checkout_code.setType(CheckoutCodeBean.INVENTORY_TYPE); // how do I determine this???
										checkout_code.setListID(item_obj.getItemListId());

										checkout_code.setAmount(new BigDecimal(item_obj.getAmount()));
										checkout_code.setDescription(item_obj.getDesc());
										//checkout_code.setItemNumber(item_obj.get.getItemNumber());
										System.out.println("SAVING CheckoutCode >" + item_obj.getItemListId() + ", DESC >" + item_obj.getDesc());
										if (item_obj.getDesc() != null)
											checkout_code.save();
									}
								}

								System.out.println("checkout_code >" + checkout_code);
								System.out.println("create_orderlines >" + create_orderlines);

								if (create_orderlines && (checkout_code != null))
								{
									System.out.println("checkout_code.getType() >" + checkout_code.getType());

									if ((checkout_code.getType() == CheckoutCodeBean.INVENTORY_TYPE) ||
											(checkout_code.getType() == CheckoutCodeBean.PROCEDURE_TYPE))
									{
										// create checkout_orderline(s) for this code

										CheckoutOrderline order_line = new CheckoutOrderline();
										if (item_obj.hasRate())
											order_line.setPrice(new BigDecimal(item_obj.getRate()));
										else
											order_line.setPrice(new BigDecimal(item_obj.getAmount()));
										order_line.setActualAmount(new BigDecimal(item_obj.getAmount()));
										order_line.setCost(checkout_code.getOrderCost());
										order_line.setCheckoutCodeId(checkout_code.getId());
										try
										{
											float qty = item_obj.getQuantity();
											order_line.setQuantity(new BigDecimal(qty));
										}
										catch (IllegalValueException x)
										{
											order_line.setQuantity(BigDecimal.ONE);
										}
										order_line.setOrderstatus(OrderBean.SALES_RECEIPT_ORDER_STATUS);

										order_lines.addElement(order_line);

										order_total = order_total.add(new BigDecimal(item_obj.getAmount()));

										System.out.println("orderline added or somethign");
									}
								}

							}

							if (obj.getPaymentMethod() != null)
							{
								System.out.println("Payment Method for invoice!??!! - " + obj.getTxnID());

								/*
								PaymentMethodRet payment_method = obj.getPaymentMethod();
								ItemPaymentRet payment_ret = ItemPaymentRet.getPaymentItem(payment_method);

								TenderRet tender = new TenderRet();
								BigDecimal order_total = new BigDecimal(obj.getTotal());
								tender.setAmount(order_total.subtract(tender_total).floatValue());
								switch (payment_ret.getCCType())
								{
									case ItemPaymentRet.CASH: tender.setType(TenderRet.CASH_TENDER_TYPE); break;
									case ItemPaymentRet.CHECK: tender.setType(TenderRet.CHECK_TENDER_TYPE); tender.setCheckNumber(obj.getCheckNumber()); break;
									case ItemPaymentRet.DINERS_CLUB: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
									case ItemPaymentRet.DISCOVER: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
									case ItemPaymentRet.EN_ROUTE: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
									case ItemPaymentRet.GIFT_CARD: tender.setType(TenderRet.GIFT_CARD_TENDER_TYPE); break;
									case ItemPaymentRet.GIFT_CERT: tender.setType(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE); break;
									case ItemPaymentRet.MASTERCARD: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
									case ItemPaymentRet.VISA: tender.setType(TenderRet.CREDIT_CARD_TENDER_TYPE); break;
								}

								tender.setClient(client);
								tender.setCompany(company);
								tender.setOrder(order_obj);
								tender.setTenderDate(obj.getTxnDate());
								//tender.setSalesReceipt(obj);

								obj.add(tender);
								 * 
								 */
							}
							

							// process the tenders

							Iterator tender_itr = obj.getTenderRetObjects().iterator();
							while (tender_itr.hasNext())
							{
								System.out.println("Tender found for invoice!??!!");
								
								TenderRet tender_obj = (TenderRet)tender_itr.next();
								if (order_obj != null)
									tender_obj.setOrder(order_obj);
								//tender_obj.setSalesReceipt(obj);
								tender_obj.setCompany(company);
								tender_obj.save();
								
								tender_amount_hash.put(tender_obj, tender_obj.getAmountBigDecimal());
							}
							
							//BigDecimal order_total = tender_total.add(new BigDecimal(obj.getTotal()));
							
							order_total = order_total.add(tax_total);

							if (order_obj != null)
							{
								order_obj.setSubtotal(order_total.subtract(tax_total));
								order_obj.setTax(tax_total);
								order_obj.setTotal(order_total);
								System.out.println("order_lines sizer >" + order_lines.size());
								order_obj.setOrders(order_lines);
								order_obj.setTenders(obj.getTenderRetObjects());
								order_obj.setTenderPaymentInfo(tender_amount_hash);
								order_obj.save();
							}

							/*
							CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", "POS Sales Receipt Processed",
									"CLIENT >" + client.getLabel() + "\n" +
									"TOTAL >" + obj.getTotal() + "\n" +
									item_buf.toString());
							 */


						//}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}

				}
				
			}
			
			/* this seems redundant.  not sure why it's still here...
			if (invoiceQueryRsObject != null)
			{
				System.out.println("NUM InvoiceRetObjects >" + invoiceQueryRsObject.getInvoiceRetObjects().size());

				Iterator itr = invoiceQueryRsObject.getInvoiceRetObjects().iterator();
				while (itr.hasNext())
				{
					InvoiceRet obj = (InvoiceRet)itr.next();
					
					try
					{
						// does a corresponding invoice already exist? it shouldn't since this is an add response...

						InvoiceRet existing_ret = null;
						System.out.println("searching for Invoice for TxnNumber >" + obj.getTxnNumber());
						try
						{
							// search using sales txn number
							existing_ret = InvoiceRet.getInvoiceForTxnNumber(company, obj.getTxnNumber());
							System.out.println("Existing invoice found for txn id >" + obj.getTxnID());
						}
						catch (ObjectNotFoundException x)
						{
							obj.setRequestId(invoiceQueryRsObject.getRequestID());
							obj.setCompany(company);
							obj.save();

							UKOnlinePersonBean client = UKOnlinePersonBean.getPersonByQBListId(company, obj.getCustomerListID(), true);
							if (client != null)
								log.addElement("Invoice " + obj.getSubtotal() + " created for " + client.getLabel());


							System.out.println("NUM SalesRecieptLineRetObjects >" + obj.getInvoiceLineRetObjects().size());
							Iterator item_itr = obj.getInvoiceLineRetObjects().iterator();
							while (item_itr.hasNext())
							{
								InvoiceLineRet item_obj = (InvoiceLineRet)item_itr.next();
								System.out.println("item_obj.getItemListId() >" + item_obj.getItemListId());
								System.out.println("item_obj.getAmount() >" + item_obj.getAmount());
								System.out.println("item_obj.getTxnLineID() >" + item_obj.getTxnLineID());
								item_obj.setInvoice(obj);
								if (item_obj.getItemListId() != null)
									item_obj.save();
							}
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}
			 * 
			 */

			if (invoiceAddRsObject != null)
			{
				return_response = invoiceAddRsObject;
				
				System.out.println("NUM InvoiceRetObjects >" + invoiceAddRsObject.getInvoiceRetObjects().size());

				if (invoiceAddRsObject.getStatusSeverity().equals("Error"))
					log.addElement(invoiceAddRsObject.getStatusMessage());

				Iterator itr = invoiceAddRsObject.getInvoiceRetObjects().iterator();
				while (itr.hasNext())
				{
					try
					{
						InvoiceRet obj = (InvoiceRet)itr.next();

						// does a corresponding invoice already exist? it shouldn't since this is an add response...

						InvoiceRet existing_ret = null;
						System.out.println("searching for Invoice for TxnNumber >" + obj.getTxnNumber());
						try
						{
							// search using sales receipt number
							existing_ret = InvoiceRet.getInvoiceForTxnNumber(company, obj.getTxnNumber());
							throw new ObjectAlreadyExistsException("Invoice already exists for transaction number " + obj.getTxnNumber());
						}
						catch (ObjectNotFoundException x)
						{
							// expected result
						}

						obj.setRequestId(invoiceAddRsObject.getRequestID());
						obj.setCompany(company);
						obj.save();

						UKOnlinePersonBean client = UKOnlinePersonBean.getPersonByQBListId(company, obj.getCustomerListID(), true);
						if (client != null)
							log.addElement("Invoice " + obj.getSubtotal() + " created for " + client.getLabel());

						String requestID = invoiceAddRsObject.getRequestID();

						// find the Order that matches this request

						OrderBean order_obj = null;
						try {
							order_obj = OrderBean.getOrder(company, requestID);
						} catch (ObjectNotFoundException x) {
							// try to find using RefNumber
							order_obj = OrderBean.getOrder(Integer.parseInt(obj.getRefNumber()));
						}

						// mark the order as having been communicated to QBFS

						order_obj.setAuthorizationCode(obj.getTxnID());
						order_obj.setIsUpdated(true);
						order_obj.save();


						System.out.println("NUM SalesRecieptLineRetObjects >" + obj.getInvoiceLineRetObjects().size());
						Iterator item_itr = obj.getInvoiceLineRetObjects().iterator();
						while (item_itr.hasNext())
						{
							InvoiceLineRet item_obj = (InvoiceLineRet)item_itr.next();
							System.out.println("item_obj.getItemListId() >" + item_obj.getItemListId());
							System.out.println("item_obj.getAmount() >" + item_obj.getAmount());
							System.out.println("item_obj.getTxnLineID() >" + item_obj.getTxnLineID());
							item_obj.setInvoice(obj);
							item_obj.save();
						}

						// not sure how this'll apply to QBFS....  ignore for now since this all should have been created based on this order anyway...

					}
					catch (ObjectAlreadyExistsException x)
					{
						log.addElement(x.getMessage());
						x.printStackTrace();
					}

				}
			}

			if (itemReceiptAddRsObject != null)
			{
				return_response = itemReceiptAddRsObject;
				
				settings.addMessage("Updating " + itemReceiptAddRsObject.getItemReceiptRetObjects().size() + " item receipts");

				System.out.println("NUM itemReceiptAddRsObject >" + itemReceiptAddRsObject.getItemReceiptRetObjects().size());

				if (itemReceiptAddRsObject.getStatusSeverity().equals("Error"))
					log.addElement(itemReceiptAddRsObject.getStatusMessage());

				itemReceiptAddRsObject.getStatusCode();
				itemReceiptAddRsObject.getStatusSeverity();
				itemReceiptAddRsObject.getStatusMessage();

				Iterator itr = itemReceiptAddRsObject.getItemReceiptRetObjects().iterator();
				while (itr.hasNext())
				{
					ItemReceiptRet obj = (ItemReceiptRet)itr.next();

					try
					{
						// see if there's a matching ItemReceiptRet in the db already.  there ought to be...

						ItemReceiptRet existing_ret = null;
						String memo = obj.getMemo();
						try
						{
							int item_receipt_id = -1;

							if (obj.getRefNumber() != null)
							{
								try
								{
									item_receipt_id = Integer.parseInt(obj.getRefNumber());
								}
								catch (Exception y)
								{
									y.printStackTrace();
									System.out.println("ItemReceiptRet not found for RefNumber >" + obj.getRefNumber());
								}
							}

							if (item_receipt_id == -1)
							{
								try
								{
									if ((memo.lastIndexOf('#') > -1) && (memo.indexOf('.') > -1))
										item_receipt_id = Integer.parseInt(memo.substring(memo.lastIndexOf('#') + 1, memo.indexOf('.')));
								}
								catch (Exception y)
								{
									y.printStackTrace();
									System.out.println("ItemReceiptRet not found for Memo >" + memo);
								}
							}

							existing_ret = ItemReceiptRet.getItemReceipt(item_receipt_id);

							System.out.println("existing ItemReceiptRet found for id >" + memo);
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("ItemReceiptRet not found for id >" + memo);
						}

						if (existing_ret == null)
						{
							// no existing.  create new

							//obj.setCompany(company);
							//obj.save();

							//log.addElement("Vendor " + obj.getName() + " created.");
						}
						else
						{
							// already exists.  update...

							existing_ret.setTxnID(obj.getTxnID());
							existing_ret.setEditSequence(obj.getEditSequence());
							existing_ret.setTxnNumber(obj.getTxnNumber());
							existing_ret.save();

							log.addElement("ItemReceipt " + existing_ret.getValue() + " updated.");
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}

			if (paymentMethodQueryRsObject != null)
			{
				return_response = paymentMethodQueryRsObject;
				
				System.out.println("NUM paymentMethodQueryRsObject >" + paymentMethodQueryRsObject.getPaymentMethodRetObjects().size());

				if (paymentMethodQueryRsObject.getStatusSeverity().equals("Error"))
					log.addElement(paymentMethodQueryRsObject.getStatusMessage());

				paymentMethodQueryRsObject.getStatusCode();
				paymentMethodQueryRsObject.getStatusSeverity();
				paymentMethodQueryRsObject.getStatusMessage();

				Iterator itr = paymentMethodQueryRsObject.getPaymentMethodRetObjects().iterator();
				while (itr.hasNext())
				{
					PaymentMethodRet obj = (PaymentMethodRet)itr.next();

					try
					{
						// see if there's a matching PaymentMethodRet in the db already

						PaymentMethodRet existing_ret = null;
						try
						{
							existing_ret = PaymentMethodRet.getPaymentMethod(company, obj.getListID());
							System.out.println("existing payment method found for list id >" + obj.getListID());
						}
						catch (ObjectNotFoundException x)
						{
							System.out.println("payment method not found for list id >" + obj.getListID());
						}

						if (existing_ret == null)
						{
							// no existing.  create new

							obj.setCompany(company);
							obj.save();

							log.addElement("Payment Method " + obj.getName() + " created.");
						}
						else
						{
							// already exists.  update...

							existing_ret.setName(obj.getName());
							existing_ret.save();

							log.addElement("Payment Method " + obj.getName() + " updated.");
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}
				}
			}

			if (billAddRsObject != null) {
				
				return_response = billAddRsObject;
				

				if (billAddRsObject.getStatusSeverity().equals("Error"))
					log.addElement(billAddRsObject.getStatusMessage());

				Iterator itr = billAddRsObject.getBillRetObjects().iterator();
				while (itr.hasNext())
				{
					try
					{
						BillRet obj = (BillRet)itr.next();

						// does a corresponding sales receipt already exist? it shouldn't since this is an add response...

						BillRet existing_ret = null;
						System.out.println("searching for Bill for TxnNumber >" + obj.getTxnNumber());
						try
						{
							existing_ret = BillRet.getBillForTxnNumber(company, obj.getTxnNumber());
							throw new ObjectAlreadyExistsException("Bill already exists for transaction number " + obj.getTxnNumber());
						}
						catch (ObjectNotFoundException x)
						{
							// expected result
						}

						//obj.setRequestId(billAddRsObject.getRequestID());
						//obj.setCompany(company);
						obj.save();

						/*
						UKOnlinePersonBean client = UKOnlinePersonBean.getPersonByQBListId(company, obj.getCustomerListID(), true);
						if (client != null)
							log.addElement("Sales receipt " + obj.getTotal() + " created for " + client.getLabel());
						*/

						String requestID = billAddRsObject.getRequestID();

						// find the Order that matches this request

						/*
						OrderBean order_obj = OrderBean.getOrder(company, requestID);

						// mark the order as having been communicated to QBFS

						order_obj.setAuthorizationCode(obj.getTxnID()); // is there a correspoding thing like this for invoices?  yes, done, I guess (feeling very tired today 5/5/11) - nice :) don't remember making that comment (5/14)
						order_obj.setIsUpdated(true);
						order_obj.save();
						*/

						/*
						System.out.println("NUM BillRetObjects >" + obj.getSalesRecieptLineRetObjects().size());
						Iterator item_itr = obj.getSalesRecieptLineRetObjects().iterator();
						while (item_itr.hasNext())
						{
							SalesReceiptLineRet item_obj = (SalesReceiptLineRet)item_itr.next();
							System.out.println("item_obj.getItemListId() >" + item_obj.getItemListId());
							System.out.println("item_obj.getAmount() >" + item_obj.getAmount());
							System.out.println("item_obj.getTxnLineID() >" + item_obj.getTxnLineID());
							item_obj.setSalesReceipt(obj);
							item_obj.save();
						}
						*/


					}
					catch (ObjectAlreadyExistsException x)
					{
						log.addElement(x.getMessage());
						x.printStackTrace();
					}

				}
			}

			if (purchaseOrderAddRsObject != null) {
				
				return_response = purchaseOrderAddRsObject;
				
				if (purchaseOrderAddRsObject.getStatusSeverity().equals("Error"))
					log.addElement(purchaseOrderAddRsObject.getStatusMessage());

				Iterator itr = purchaseOrderAddRsObject.getPurchaseOrderRetObjects().iterator();
				while (itr.hasNext())
				{
					try
					{
						PurchaseOrderRet obj = (PurchaseOrderRet)itr.next();
						
						//String requestID = purchaseOrderAddRsObject.getRequestID();

						// find the Purchase Order that matches this RefNum
						
						PurchaseOrder matching_po = PurchaseOrder.getPurchaseOrderForNumber(company, Integer.parseInt(obj.getRefNumber()));
						System.out.println("found matching po >" + matching_po.getLabel());

						matching_po.setTxnID(obj.getTxnID());
						matching_po.save();

					}
					catch (Exception x)
					{
						log.addElement(x.getMessage());
						x.printStackTrace();
					}

				}
			}
			
			

		}catch(SAXException se) {
			CUBean.sendEmail("marlo@badiyan.com", "valonyx@valeowc.com", "QBXMLResponseParser Issue - SAXException", se.getMessage());
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			CUBean.sendEmail("marlo@badiyan.com", "valonyx@valeowc.com", "QBXMLResponseParser Issue - ParserConfigurationException", pce.getMessage());
			pce.printStackTrace();
		}catch (IOException ie) {
			CUBean.sendEmail("marlo@badiyan.com", "valonyx@valeowc.com", "QBXMLResponseParser Issue - IOException", ie.getMessage());
			ie.printStackTrace();
		}catch (Exception e) {
			CUBean.sendEmail("marlo@badiyan.com", "valonyx@valeowc.com", "QBXMLResponseParser Issue - Exception", e.getMessage());
			e.printStackTrace();
		}
		
		return return_response;
	}

	//Event Handlers
	@Override
	public void startElement(String _uri, String _localName, String _qName, Attributes _attributes) throws SAXException {
		//reset
		System.out.println("ELEMENT >" + _qName);
		current_element = _qName;

		try
		{
			if (_qName.equals("SalesReceiptQueryRs"))
			{
				this.invalidate();
				salesReceiptQueryRsObject = new SalesReceiptQueryRs();
				this.fetchQueryRsAttributes(salesReceiptQueryRsObject, _attributes);
			}
			else if (_qName.equals("CustomerQueryRs") || _qName.equals("CustomerAddRs"))
			{
				this.invalidate();
				customerQueryRsObject = new CustomerQueryRs();
				this.fetchQueryRsAttributes(customerQueryRsObject, _attributes);
			}
			else if (_qName.equals("CustomerRet"))
			{
				customerRetObject = new CustomerRet();
				customerQueryRsObject.add(customerRetObject);
			}
			else if (_qName.equals("ReceivePaymentQueryRs"))
			{
				this.invalidate();
				receivePaymentQueryRsObject = new ReceivePaymentQueryRs();
				this.fetchQueryRsAttributes(receivePaymentQueryRsObject, _attributes);
			}
			else if (_qName.equals("ReceivePaymentAddRs"))
			{
				this.invalidate();
				receivePaymentAddRsObject = new ReceivePaymentAddRs();
				this.fetchQueryRsAttributes(receivePaymentAddRsObject, _attributes);
			}
			else if (_qName.equals("ReceivePaymentRet"))
			{
				receivePaymentRetObject = new ReceivePaymentRet();
				if (receivePaymentQueryRsObject != null)
					receivePaymentQueryRsObject.add(receivePaymentRetObject);
				else
					receivePaymentAddRsObject.add(receivePaymentRetObject);
			}
			else if (_qName.equals("CustomerRef"))
			{
				if (receivePaymentRetObject != null)
				{
					customerRefObject = new CustomerRef();
					receivePaymentRetObject.setCustomer(customerRefObject);
					paymentMethodRefObject = null;
				}
			}
			else if (_qName.equals("ARAccountRef"))
			{
				customerRefObject = null;
				paymentMethodRefObject = null;
			}
			else if (_qName.equals("PaymentMethodRef"))
			{
				if (receivePaymentRetObject != null)
				{
					paymentMethodRefObject = new PaymentMethodRef();
					receivePaymentRetObject.setPaymentMethod(paymentMethodRefObject);
					customerRefObject = null;
				}
			}
			else if (_qName.equals("DepositToAccountRef"))
			{
				customerRefObject = null;
				paymentMethodRefObject = null;
			}
			else if (_qName.equals("AccountQueryRs"))
			{
				this.invalidate();
				accountQueryRsObject = new AccountQueryRs();
				this.fetchQueryRsAttributes(accountQueryRsObject, _attributes);
			}
			else if (_qName.equals("AccountRet"))
			{
				accountRetObject = new AccountRet();
				accountQueryRsObject.add(accountRetObject);
			}
			else if (_qName.equals("PaymentMethodQueryRs"))
			{
				this.invalidate();
				paymentMethodQueryRsObject = new PaymentMethodQueryRs();
				this.fetchQueryRsAttributes(paymentMethodQueryRsObject, _attributes);
			}
			else if (_qName.equals("PaymentMethodRet"))
			{
				paymentMethodRetObject = new PaymentMethodRet();
				paymentMethodQueryRsObject.add(paymentMethodRetObject);
			}
			else if (_qName.equals("VendorQueryRs") || _qName.equals("VendorAddRs"))
			{
				this.invalidate();
				vendorQueryRsObject = new VendorQueryRs();
				this.fetchQueryRsAttributes(vendorQueryRsObject, _attributes);
			}
			else if (_qName.equals("VendorRet"))
			{
				vendorRetObject = new VendorRet();
				vendorQueryRsObject.add(vendorRetObject);
			}
			else if (_qName.equals("ItemQueryRs")
						|| _qName.equals("ItemSalesTaxQueryRs")
						|| _qName.equals("ItemSalesTaxGroupQueryRs")
						|| _qName.equals("ItemGroupQueryRs")
						|| _qName.equals("ItemPaymentQueryRs"))
			{
				this.invalidate();
				itemQueryRsObject = new ItemQueryRs();
				this.fetchQueryRsAttributes(itemQueryRsObject, _attributes);
			}
			else if (_qName.equals("ItemPaymentAddRs"))
			{
				this.invalidate();
				itemPaymentAddRs = new ItemPaymentAddRs();
				this.fetchQueryRsAttributes(itemPaymentAddRs, _attributes);
			}
			else if (_qName.equals("ItemNonInventoryAddRs")
						|| _qName.equals("ItemInventoryAddRs")
						|| _qName.equals("ItemServiceAddRs")
						|| _qName.equals("ItemSubtotalAddRs"))
			{
				this.invalidate();
				itemNonInventoryAddRs = new ItemNonInventoryAddRs();
				this.fetchQueryRsAttributes(itemNonInventoryAddRs, _attributes);
			}
			else if (_qName.equals("ItemSalesTaxAddRs"))
			{
				this.invalidate();
				itemSalesTaxAddRs = new ItemSalesTaxAddRs();
				this.fetchQueryRsAttributes(itemSalesTaxAddRs, _attributes);
			}
			else if (_qName.equals("ItemServiceRet")
						|| _qName.equals("ItemNonInventoryRet")
						|| _qName.equals("ItemOtherChargeRet")
						|| _qName.equals("ItemInventoryRet")
						|| _qName.equals("ItemInventoryAssemblyRet")
						|| _qName.equals("ItemFixedAssetRet")
						|| _qName.equals("ItemSubtotalRet")
						|| _qName.equals("ItemDiscountRet")
						|| _qName.equals("SpecialItemRet"))
			{
				itemRetObject = new ItemRet();
				
				if (_qName.equals("ItemServiceRet"))
					itemRetObject.setItemType(ItemRet.SERVICE_TYPE);
				else if (_qName.equals("ItemNonInventoryRet"))
					itemRetObject.setItemType(ItemRet.NON_INVENTORY_TYPE);
				else if (_qName.equals("ItemOtherChargeRet"))
					itemRetObject.setItemType(ItemRet.OTHER_CHARGE_TYPE);
				else if (_qName.equals("ItemInventoryRet"))
					itemRetObject.setItemType(ItemRet.INVENTORY_TYPE);
				else if (_qName.equals("ItemInventoryAssemblyRet"))
					itemRetObject.setItemType(ItemRet.INVENTORY_ASSEMBLY_TYPE);
				else if (_qName.equals("ItemFixedAssetRet"))
					itemRetObject.setItemType(ItemRet.FIXED_ASSET_TYPE);
				else if (_qName.equals("ItemSubtotalRet"))
					itemRetObject.setItemType(ItemRet.SUBTOTAL_TYPE);
				else if (_qName.equals("ItemDiscountRet"))
					itemRetObject.setItemType(ItemRet.DISCOUNT_TYPE);
				else if (_qName.equals("SpecialItemRet"))
					itemRetObject.setItemType(ItemRet.SPECIAL_TYPE);

				System.out.println("itemQueryRsObject >" + itemQueryRsObject);
				System.out.println("itemNonInventoryAddRs >" + itemNonInventoryAddRs);

				if (itemQueryRsObject != null)
					itemQueryRsObject.add(itemRetObject);
				else if (itemNonInventoryAddRs != null)
					itemNonInventoryAddRs.add(itemRetObject);

				doItem = true;
				doItemSalesTax = false;
				doItemSalesTaxGroup = false;
				doItemPayment = false;
				doItemGroup = false;
			}
			else if (_qName.equals("ItemPaymentRet"))
			{
				itemPaymentRetObject = new ItemPaymentRet();
				//itemPaymentRetObject.setItemType(ItemRet.PAYMENT_TYPE);
				
				if (itemQueryRsObject != null)
					itemQueryRsObject.add(itemPaymentRetObject);
				else if (itemPaymentAddRs != null)
					itemPaymentAddRs.add(itemPaymentRetObject);

				doItem = false;
				doItemSalesTax = false;
				doItemSalesTaxGroup = false;
				doItemPayment = true;
				doItemGroup = false;
			}
			else if (_qName.equals("ItemSalesTaxRet"))
			{
				itemSalesTaxRetObject = new ItemSalesTaxRet();
				//itemSalesTaxRetObject.setItemType(ItemRet.SALES_TAX_TYPE);
				
				if (itemQueryRsObject != null)
					itemQueryRsObject.add(itemSalesTaxRetObject);
				else if (itemSalesTaxAddRs != null)
					itemSalesTaxAddRs.add(itemSalesTaxRetObject);

				doItem = false;
				doItemSalesTax = true;
				doItemSalesTaxGroup = false;
				doItemPayment = false;
				doItemGroup = false;
			}
			else if (_qName.equals("ItemSalesTaxGroupRet"))
			{
				itemSalesTaxGroupRetObject = new ItemSalesTaxGroupRet();
				itemRetObject.setItemType(ItemRet.SALES_TAX_GROUP_TYPE);
				itemQueryRsObject.add(itemSalesTaxGroupRetObject);

				doItem = false;
				doItemSalesTax = false;
				doItemSalesTaxGroup = true;
				doItemPayment = false;
				doItemGroup = false;
			}
			else if (_qName.equals("ItemGroupRet"))
			{
				itemGroupRetObject = new ItemGroupRet();
				itemRetObject.setItemType(ItemRet.GROUP_TYPE);
				itemQueryRsObject.add(itemGroupRetObject);

				doItem = false;
				doItemSalesTax = false;
				doItemSalesTaxGroup = false;
				doItemPayment = false;
				doItemGroup = true;
			}
			else if (_qName.equals("ItemGroupLine"))
			{
				itemGroupLineObject = new ItemGroupLine();
				itemGroupRetObject.add(itemGroupLineObject);
			}
			else if (_qName.equals("SalesTaxCodeQueryRs"))
			{
				this.invalidate();
				salesTaxCodeQueryRsObject = new SalesTaxCodeQueryRs();
				this.fetchQueryRsAttributes(salesTaxCodeQueryRsObject, _attributes);
			}
			else if (_qName.equals("SalesTaxCodeRet"))
			{
				salesTaxCodeRetObject = new SalesTaxCodeRet();
				salesTaxCodeQueryRsObject.add(salesTaxCodeRetObject);
			}
			else if (_qName.equals("SalesReceiptAddRs"))
			{
				this.invalidate();
				salesReceiptAddRsObject = new SalesReceiptAddRs();
				this.fetchQueryRsAttributes(salesReceiptAddRsObject, _attributes);
			}
			else if (_qName.equals("SalesReceiptRet"))
			{
				salesReceiptRetObject = new SalesReceiptRet();
				if (salesReceiptAddRsObject != null)
					salesReceiptAddRsObject.add(salesReceiptRetObject);
				else
					salesReceiptQueryRsObject.add(salesReceiptRetObject);
				salesReceiptLineRetObject = null;
			}
			else if (_qName.equals("SalesReceiptLineRet"))
			{
				salesReceiptLineRetObject = new SalesReceiptLineRet();
				try
				{
					salesReceiptRetObject.add(salesReceiptLineRetObject);
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}
			else if (_qName.equals("InvoiceQueryRs"))
			{
				this.invalidate();
				invoiceQueryRsObject = new InvoiceQueryRs();
				this.fetchQueryRsAttributes(invoiceQueryRsObject, _attributes);
			}
			else if (_qName.equals("InvoiceAddRs"))
			{
				this.invalidate();
				invoiceAddRsObject = new InvoiceAddRs();
				this.fetchQueryRsAttributes(invoiceAddRsObject, _attributes);
			}
			else if (_qName.equals("InvoiceRet"))
			{
				invoiceRetObject = new InvoiceRet();
				if (invoiceAddRsObject != null)
					invoiceAddRsObject.add(invoiceRetObject);
				else if (invoiceQueryRsObject != null)
					invoiceQueryRsObject.add(invoiceRetObject);
				invoiceLineRetObject = null;
			}
			else if (_qName.equals("InvoiceLineRet"))
			{
				invoiceLineRetObject = new InvoiceLineRet();
				try
				{
					invoiceRetObject.add(invoiceLineRetObject);
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}
			else if (_qName.equals("PurchaseOrderAddRs"))
			{
				this.invalidate();
				purchaseOrderAddRsObject = new PurchaseOrderAddRs();
				this.fetchQueryRsAttributes(purchaseOrderAddRsObject, _attributes);
			}
			else if (_qName.equals("PurchaseOrderRet"))
			{
				purchaseOrderRetObject = new PurchaseOrderRet();
				purchaseOrderAddRsObject.add(purchaseOrderRetObject);
			}
			else if (_qName.equals("BillAddRs"))
			{
				this.invalidate();
				billAddRsObject = new BillAddRs();
				this.fetchQueryRsAttributes(billAddRsObject, _attributes);
			}
			else if (_qName.equals("BillRet"))
			{
				billRetObject = new BillRet();
				billAddRsObject.add(billRetObject);
			}
			else if (_qName.equals("ItemReceiptAddRs"))
			{
				this.invalidate();
				itemReceiptAddRsObject = new ItemReceiptAddRs();
				this.fetchQueryRsAttributes(itemReceiptAddRsObject, _attributes);
			}
			else if (_qName.equals("ItemReceiptRet"))
			{
				itemReceiptRetObject = new ItemReceiptRet();
				itemReceiptAddRsObject.add(itemReceiptRetObject);
			}
			else if (_qName.equals("PrefVendorRef"))
			{
				pref_vendor_ref = true;
			}
			else if (_qName.equals("ParentRef"))
			{
				parent_ref = true;
			}
			else if (_qName.equals("IncomeAccountRef") || _qName.equals("AccountRef"))
			{
				income_account_ref = true;
			}
			else if (_qName.equals("ExpenseAccountRef"))
			{
				expense_account_ref = true;
			}
			else if (_qName.equals("COGSAccountRef"))
			{
				cogs_account_ref = true;
			}
			else if (_qName.equals("AssetAccountRef"))
			{
				asset_account_ref = true;
			}
			else if (_qName.equals("SalesOrPurchase"))
			{
				if (itemRetObject != null)
					itemRetObject.setSalesOrPurchase(true);
			}
			else if (_qName.equals("SalesAndPurchase"))
			{
				if (itemRetObject != null)
					itemRetObject.setSalesAndPurchase(true);
			}
			else if (_qName.endsWith("TaxVendorRef"))
			{
				tax_vendor_ref = true;
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	private void
	invalidate()
	{
		customerQueryRsObject = null;
		customerRetObject = null;

		receivePaymentQueryRsObject = null;
		receivePaymentAddRsObject = null;
		receivePaymentRetObject = null;

		customerRefObject = null;
		paymentMethodRefObject = null;

		accountQueryRsObject = null;
		accountRetObject = null;

		paymentMethodQueryRsObject = null;
		paymentMethodRetObject = null;

		vendorQueryRsObject = null;
		vendorRetObject = null;

		salesReceiptQueryRsObject = null;
		salesReceiptAddRsObject = null;
		salesReceiptRetObject = null;
		salesReceiptLineRetObject = null;

		invoiceAddRsObject = null;
		invoiceRetObject = null;
		invoiceLineRetObject = null;
		
		itemGroupRetObject = null;
		itemGroupLineObject = null;
		
		itemQueryRsObject = null;
		itemRetObject = null;
		itemSalesTaxRetObject = null;
		itemSalesTaxGroupRetObject = null;
		
		itemNonInventoryAddRs = null;

		doItem = false;
		doItemSalesTax = false;
		doItemSalesTaxGroup = false;
		doItemPayment = false;
		doItemGroup = false;

		salesTaxCodeQueryRsObject = null;
		salesTaxCodeRetObject = null;
		itemPaymentRetObject = null;

		itemReceiptAddRsObject = null;
		itemReceiptRetObject = null;

		purchaseOrderAddRsObject = null;
		purchaseOrderRetObject = null;

		billAddRsObject = null;
		billRetObject = null;
	}

	private void
	fetchQueryRsAttributes(QueryRs _response_obj, Attributes _attributes)
	{
		_response_obj.setRequestID(_attributes.getValue("requestID"));
		String value = _attributes.getValue("retCount");
		if (value != null)
			_response_obj.setRetCount(Integer.parseInt(value));
		value = _attributes.getValue("statusCode");
		if (value != null)
			_response_obj.setStatusCode(value);
		value = _attributes.getValue("statusMessage");
		if (value != null)
			_response_obj.setStatusMessage(value);
		value = _attributes.getValue("statusSeverity");
		if (value != null)
			_response_obj.setStatusSeverity(value);
		value = _attributes.getValue("iteratorRemainingCount");
		if (value != null)
		{
			System.out.println("found iteratorRemainingCount >" + value);
			_response_obj.setIteratorRemainingCount(Integer.parseInt(value));
		}
		value = _attributes.getValue("iteratorID");
		if (value != null)
		{
			System.out.println("iteratorID >" + value);
			
			// find the corresponding request object

			QBXMLRequest iterator_request_obj = (QBXMLRequest)QBWebConnectorSvcSoapImpl.pending_iterator_hash.get(_response_obj.getRequestID());
			
			System.out.println("iterator_request_obj >" + iterator_request_obj);
			
			if (iterator_request_obj == null)
				iterator_request_obj = (QBXMLRequest)QBWebConnectorSvcSoapImpl.iterator_hash.get(value);
			else
			{
				QBWebConnectorSvcSoapImpl.pending_iterator_hash.remove(value);
				QBWebConnectorSvcSoapImpl.iterator_hash.put(value, iterator_request_obj);
			}

			if (iterator_request_obj != null)
			{
				iterator_request_obj.setIteratorRemainingCount(_response_obj.getIteratorRemainingCount());

				if (_response_obj.getIteratorRemainingCount() == 0)
				{
					QBWebConnectorSvcSoapImpl.pending_iterator_hash.remove(_response_obj.getRequestID());
					QBWebConnectorSvcSoapImpl.iterator_hash.remove(value);
					iterator_request_obj.setExhaustedIterator(true);
				}
				else
				{
					// convert to continuation
					
					System.out.println("convert to continuation");

					iterator_request_obj.setContinuation(true);
					iterator_request_obj.setIteratorID(value);
				}
			}

			_response_obj.setIteratorID(value);
		}
		
		if (_response_obj.getStatusSeverity() != null)
		{
			if (_response_obj.getStatusSeverity().equals("Error")) {
				CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", _response_obj.getStatusCode() + ": " + _response_obj.getStatusMessage(), _response_obj.getRequestID());
			}
		}
	}


	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		String value = new String(ch,start,length);
		value = value.trim();

		//System.out.println("last_element >" + last_element);
		//System.out.println("current_element.equals(last_element) >" + current_element.equals(last_element));
		//System.out.println("last_value >" + last_value);

		/*
		System.out.println("characters invoked");
		
		if ((last_element != null) && current_element.equals(last_element))
		{
			if (last_value != null)
				value = last_value + value;
		}
		else
			last_value = null;

		last_value = value;
		last_element = current_element;
		 */

		try
		{
			if (!value.equals(""))
			{
				System.out.println(">" + current_element + "< = >" + value + "<");

				if (current_element.equals("TimeModified"))
				{
					Date time_modified = time_modified_date_format.parse(value.substring(0, value.lastIndexOf("-")));

					if (salesReceiptRetObject != null)
						salesReceiptRetObject.setTimeModified(time_modified);
					else if (invoiceRetObject != null)
						invoiceRetObject.setTimeModified(time_modified);
					else if (customerRetObject != null)
						customerRetObject.setTimeModified(time_modified);
					else if (receivePaymentRetObject != null)
						receivePaymentRetObject.setTimeModified(time_modified);
					else if (accountRetObject != null)
						accountRetObject.setTimeModified(time_modified);
					else if (vendorRetObject != null)
						vendorRetObject.setTimeModified(time_modified);
					else if ((itemSalesTaxRetObject != null) && doItemSalesTax)
						itemSalesTaxRetObject.setTimeModified(time_modified);
					else if ((itemSalesTaxGroupRetObject != null) && doItemSalesTaxGroup)
						itemSalesTaxGroupRetObject.setTimeModified(time_modified);
					else if (salesTaxCodeRetObject != null)
						salesTaxCodeRetObject.setTimeModified(time_modified);
					else if ((itemPaymentRetObject != null) && doItemPayment)
						itemPaymentRetObject.setTimeModified(time_modified);
					else if ((itemRetObject != null) && doItem)
						itemRetObject.setTimeModified(time_modified);
					else if ((itemGroupRetObject != null) && doItemGroup)
						itemGroupRetObject.setTimeModified(time_modified);
				}
				else if (current_element.equals("Subtotal"))
				{
					if (salesReceiptRetObject != null)
						salesReceiptRetObject.setSubtotal(Float.parseFloat(value));
					else if (invoiceRetObject != null)
						invoiceRetObject.setSubtotal(Float.parseFloat(value));
				}
				else if (current_element.equals("SalesTaxTotal"))
				{
					if (salesReceiptRetObject != null)
						salesReceiptRetObject.setTaxAmount(Float.parseFloat(value));
					else if (invoiceRetObject != null)
						invoiceRetObject.setTaxAmount(Float.parseFloat(value));
				}
				else if (current_element.equals("TxnDate"))
				{
					if (salesReceiptRetObject != null)
						salesReceiptRetObject.setTxnDate(date_format.parse(value));
					else if (receivePaymentRetObject != null)
						receivePaymentRetObject.setTxnDate(date_format.parse(value));
					else if (invoiceRetObject != null)
						invoiceRetObject.setTxnDate(date_format.parse(value));
				}
				else if (current_element.equals("TxnID"))
				{
					if (salesReceiptRetObject != null)
						salesReceiptRetObject.setTxnID(value);
					else if (receivePaymentRetObject != null)
						receivePaymentRetObject.setTxnID(value);
					else if (itemReceiptRetObject != null)
						itemReceiptRetObject.setTxnID(value);
					else if (billRetObject != null)
						billRetObject.setTxnID(value);
					else if (purchaseOrderRetObject != null)
						purchaseOrderRetObject.setTxnID(value);
					else if (invoiceRetObject != null)
						invoiceRetObject.setTxnID(value);
				}
				else if (current_element.equals("CheckNumber"))
				{
					if (salesReceiptRetObject != null)
						salesReceiptRetObject.setCheckNumber(Integer.parseInt(value));

				}
				else if (current_element.equals("TxnLineID"))
				{
					if (salesReceiptLineRetObject != null)
						salesReceiptLineRetObject.setTxnLineID(value);
					else if (invoiceLineRetObject != null)
						invoiceLineRetObject.setTxnLineID(value);
				}
				else if (current_element.equals("Quantity"))
				{
					if (salesReceiptLineRetObject != null)
						salesReceiptLineRetObject.setQuantity(Float.parseFloat(value));
					else if (invoiceLineRetObject != null)
						invoiceLineRetObject.setQuantity(Float.parseFloat(value));
					else if (itemGroupLineObject != null)
						itemGroupLineObject.setQuantity(Float.parseFloat(value));
				}
				else if (current_element.equals("Rate"))
				{
					if (salesReceiptLineRetObject != null)
						salesReceiptLineRetObject.setRate(Float.parseFloat(value));
					else if (invoiceLineRetObject != null)
						invoiceLineRetObject.setRate(Float.parseFloat(value));
				}
				else if (current_element.equals("Amount"))
				{
					if (salesReceiptLineRetObject != null)
						salesReceiptLineRetObject.setAmount(Float.parseFloat(value));
					else if (invoiceLineRetObject != null)
						invoiceLineRetObject.setAmount(Float.parseFloat(value));
				}
				else if (current_element.equals("ListID"))
				{
					if (salesReceiptRetObject != null)
					{
						if (salesReceiptLineRetObject == null)
						{
							// this could be CustomerRef, TemplateRef, PaymentMethodRef, ItemSalesTaxRef, CustomerSalesTaxCodeRef, DepositToAccountRef
							// I'm only interested in CustomerRef for now

							if (salesReceiptRetObject.getCustomerListID() == null)
							{
								UKOnlinePersonBean.getPersonByQBListId(company, value, true);
								salesReceiptRetObject.setCustomerListID(value);
							}
							else
							{
								// test to see is this is a PaymentMethodRef
								
								try
								{
									PaymentMethodRet payment_method = PaymentMethodRet.getPaymentMethod(company, value); // this appears to always be check...
									System.out.println("found payment method >" + payment_method.getLabel());
									salesReceiptRetObject.setPaymentMethod(payment_method);
								}
								catch (ObjectNotFoundException x)
								{
									
								}
							}
						}
						else
						{
							// has the item been located for this

							System.out.println("salesReceiptLineRetObject.getItemListId() >" + salesReceiptLineRetObject.getItemListId());

							if (salesReceiptLineRetObject.getItemListId() == null)
							{
								try
								{
									CheckoutCodeBean.getCheckoutCodeByQBListID(company, value);

									// this is an inventory / service item

									System.out.println("this is an inventory / service item");
								}
								catch (ObjectNotFoundException x1)
								{
									try
									{
										ItemPaymentRet.getPaymentItem(company, value);

										// this is a payment item

										System.out.println("this is a payment item");
									}
									catch (ObjectNotFoundException x2)
									{
										try
										{
											ItemSalesTaxRet.getSalesTaxItem(company, value);

											// this is a sales tax item

											System.out.println("this is a sales tax item");
										}
										catch (ObjectNotFoundException x3)
										{
											try
											{
												ItemSalesTaxGroupRet.getSalesTaxGroupItem(company, value);

												// this is a sales tax group item
											}
											catch (ObjectNotFoundException x4)
											{
												x4.printStackTrace();
											}
										}

									}
								}
								System.out.println("setItemListID >" + value);
								salesReceiptLineRetObject.setItemListID(value);
							}
							else if (salesReceiptLineRetObject.getSalesTaxCodeListID() == null)
							{
								salesReceiptLineRetObject.setSalesTaxCodeListID(value);
								salesReceiptLineRetObject.setTaxCode(SalesTaxCodeRet.getSalesTaxCode(company, value));
							}
						}
					}
					else if (invoiceRetObject != null)
					{
						if (invoiceLineRetObject == null)
						{
							// this could be CustomerRef, TemplateRef, PaymentMethodRef, ItemSalesTaxRef, CustomerSalesTaxCodeRef, DepositToAccountRef
							// I'm only interested in CustomerRef for now

							if (invoiceRetObject.getCustomerListID() == null)
							{
								//UKOnlinePersonBean.getPersonByQBListId(company, value);
								invoiceRetObject.setCustomerListID(value);
							}
							else
							{
								// test to see is this is a PaymentMethodRef
								
								try
								{
									PaymentMethodRet payment_method = PaymentMethodRet.getPaymentMethod(company, value); // this appears to always be check...
									System.out.println("found payment method >" + payment_method.getLabel());
									invoiceRetObject.setPaymentMethod(payment_method);
								}
								catch (ObjectNotFoundException x)
								{
									
								}
							}
						}
						else
						{
							// has the item been located for this

							System.out.println("invoiceLineRetObject.getItemListId() >" + invoiceLineRetObject.getItemListId());

							if (invoiceLineRetObject.getItemListId() == null)
							{
								try
								{
									CheckoutCodeBean.getCheckoutCodeByQBListID(company, value);

									// this is an inventory / service item

									System.out.println("this is an inventory / service item");
								}
								catch (ObjectNotFoundException x1)
								{
									try
									{
										ItemPaymentRet.getPaymentItem(company, value);

										// this is a payment item

										System.out.println("this is a payment item");
									}
									catch (ObjectNotFoundException x2)
									{
										try
										{
											ItemSalesTaxRet.getSalesTaxItem(company, value);

											// this is a sales tax item

											System.out.println("this is a sales tax item");
										}
										catch (ObjectNotFoundException x3)
										{
											try
											{
												ItemSalesTaxGroupRet.getSalesTaxGroupItem(company, value);

												// this is a sales tax group item
											}
											catch (ObjectNotFoundException x4)
											{
												//x4.printStackTrace();
											}
										}

									}
								}
								System.out.println("setItemListID >" + value);
								invoiceLineRetObject.setItemListID(value);
							}
							else if (invoiceLineRetObject.getSalesTaxCodeListID() == null)
							{
								invoiceLineRetObject.setSalesTaxCodeListID(value);
								invoiceLineRetObject.setTaxCode(SalesTaxCodeRet.getSalesTaxCode(company, value));
							}
						}
					}
					else if (customerRetObject != null)
					{
						if (customerRetObject.getQBFSListID() == null)
							customerRetObject.setQBFSListID(value);
					}
					else if (receivePaymentRetObject != null)
					{
						if (customerRefObject != null)
						{
							customerRefObject.setListID(value);
							receivePaymentRetObject.setCustomer(customerRefObject);
						}
						else if (paymentMethodRefObject != null)
						{
							paymentMethodRefObject.setListID(value);
							receivePaymentRetObject.setPaymentMethod(paymentMethodRefObject);
						}
					}
					else if (accountRetObject != null)
					{
						if (accountRetObject.getListID() == null)
							accountRetObject.setListID(value);
						else
							accountRetObject.setParentListID(value);
					}
					else if (vendorRetObject != null)
					{
						if (vendorRetObject.getListID() == null)
							vendorRetObject.setListID(value);
						else
							vendorRetObject.setTermsListID(value);
					}
					else if ((itemRetObject != null) && doItem)
					{
						if (pref_vendor_ref)
						{
							System.out.println("setVendorListID >" + value);
							itemRetObject.setVendorListID(value);
							pref_vendor_ref = false;
						}
						else if (income_account_ref)
						{
							System.out.println("setIncomeAccountListID >" + value);
							itemRetObject.setIncomeAccountListID(value);
							income_account_ref = false;
						}
						else if (expense_account_ref)
						{
							System.out.println("setExpenseAccountListID >" + value);
							itemRetObject.setExpenseAccountListID(value);
							expense_account_ref = false;
						}
						else if (cogs_account_ref)
						{
							System.out.println("setCogsAccountListID >" + value);
							itemRetObject.setCOGSAccountListID(value);
							cogs_account_ref = false;
						}
						else if (asset_account_ref)
						{
							System.out.println("setAssetAccountListID >" + value);
							itemRetObject.setAssetAccountListID(value);
							asset_account_ref = false;
						}
						else if (parent_ref)
						{
							System.out.println("setParentListID >" + value);
							itemRetObject.setParentListID(value);
							parent_ref = false;
						}
						else if (itemRetObject.getListID() == null)
							itemRetObject.setListID(value);
						else if (itemRetObject.getSalesTaxListID() == null)
							itemRetObject.setSalesTaxListID(value);
					}
					else if ((itemGroupRetObject != null) && doItemGroup)
					{
						if (itemGroupRetObject.getListID() == null)
							itemGroupRetObject.setListID(value);
						else if (itemGroupLineObject.getListID() == null)
							itemGroupLineObject.setListID(value);
					}
					else if ((itemSalesTaxRetObject != null) && doItemSalesTax)
					{
						if (itemSalesTaxRetObject.getListID() == null)
							itemSalesTaxRetObject.setListID(value);
						else
							itemSalesTaxRetObject.setTaxVendorListID(value);
					}
					else if ((itemSalesTaxGroupRetObject != null) && doItemSalesTaxGroup)
					{
						if (itemSalesTaxGroupRetObject.getListID() == null)
							itemSalesTaxGroupRetObject.setListID(value);
						else
						{
							//itemSalesTaxGroupRetObject.addSalesTaxItem(ItemSalesTaxRet.getSalesTaxItem(company, value));
							itemSalesTaxGroupRetObject.addSalesTaxItemListID(value);
						}
					}
					else if ((itemPaymentRetObject != null) && doItemPayment)
					{
						if (itemPaymentRetObject.getListID() == null)
							itemPaymentRetObject.setListID(value);
						else if (itemPaymentRetObject.getDepositToAccountRefListID() == null)
							itemPaymentRetObject.setDepositToAccountRefListID(value);
						else
							itemPaymentRetObject.setPaymentMethodListID(value);
					}
					else if (salesTaxCodeRetObject != null)
					{
						if (salesTaxCodeRetObject.getListID() == null)
							salesTaxCodeRetObject.setListID(value);
					}
					else if (paymentMethodRetObject != null)
					{
						if (paymentMethodRetObject.getListID() == null)
							paymentMethodRetObject.setListID(value);
					}
				}
				else if (current_element.equals("AccountBalance"))
				{
					if (customerRetObject != null)
						customerRetObject.setAccountBalance(Float.parseFloat(value));
				}
				else if (current_element.equals("FirstName"))
				{
					if (customerRetObject != null)
						customerRetObject.setFirstName(value);
				}
				else if (current_element.equals("FullName"))
				{
					if (customerRetObject != null)
					{
						if (customerRetObject.getFullName() == null)
							customerRetObject.setFullName(value);
					}
					else if (receivePaymentRetObject != null)
					{
						if (customerRefObject != null)
						{
							customerRefObject.setFullName(value);
							receivePaymentRetObject.setCustomer(customerRefObject);
						}
						else if (paymentMethodRefObject != null)
						{
							paymentMethodRefObject.setFullName(value);
							receivePaymentRetObject.setPaymentMethod(paymentMethodRefObject);
						}
					}
					else if (accountRetObject != null)
						accountRetObject.setFullName(value);
					else if (vendorRetObject != null)
						vendorRetObject.setTermsFullName(value);
					else if ((itemSalesTaxRetObject != null) && doItemSalesTax)
						itemSalesTaxRetObject.setTaxVendorFullName(value);
					else if ((itemPaymentRetObject != null) && doItemPayment)
						itemPaymentRetObject.setPaymentMethodFullName(value);
				}
				else if (current_element.equals("LastName"))
				{
					if (customerRetObject != null)
						customerRetObject.setLastName(value);
				}
				else if (current_element.equals("LastSale"))
				{
					if (customerRetObject != null)
						customerRetObject.setLastSaleDate(date_format.parse(value));
				}
				else if (current_element.equals("Phone"))
				{
					if (customerRetObject != null)
						customerRetObject.setPhone(value);
					else if (vendorRetObject != null)
						vendorRetObject.setPhone(value);
				}
				else if (current_element.equals("Phone2"))
				{
					if (customerRetObject != null)
						customerRetObject.setPhone2(value);
				}
				else if (current_element.equals("AltPhone"))
				{
					if (customerRetObject != null)
						customerRetObject.setPhone2(value);
				}
				else if (current_element.equals("Email") || current_element.equals("EMail"))
				{
					if (customerRetObject != null)
						customerRetObject.setEMail(value);
				}
				else if (current_element.equals("PriceLevelNumber"))
				{
					if (customerRetObject != null)
						customerRetObject.setPriceLevelNumber(Short.parseShort(value));
				}
				else if (current_element.equals("TxnNumber"))
				{
					if (salesReceiptRetObject != null)
						salesReceiptRetObject.setTxnNumber(Integer.parseInt(value));
					else if (receivePaymentRetObject != null)
						receivePaymentRetObject.setTxnNumber(Integer.parseInt(value));
					else if (itemReceiptRetObject != null)
						itemReceiptRetObject.setTxnNumber(Integer.parseInt(value));
					else if (billRetObject != null)
						billRetObject.setTxnNumber(Integer.parseInt(value));
					else if (invoiceRetObject != null)
						invoiceRetObject.setTxnNumber(Integer.parseInt(value));
				}
				else if (current_element.equals("TotalAmount"))
				{
					System.out.println("TOTAL AMOUNT FOUND >" + value);
					System.out.println("salesReceiptRetObject >" + salesReceiptRetObject);
					
					if (salesReceiptRetObject != null)
						salesReceiptRetObject.setTotal(Float.parseFloat(value));
					else if (receivePaymentRetObject != null)
						receivePaymentRetObject.setTotalAmount(Float.parseFloat(value));
					//else if (invoiceRetObject != null)
					//	invoiceRetObject.setTotal(Float.parseFloat(value));
				}
				else if (current_element.equals("Memo"))
				{
					if (receivePaymentRetObject != null)
						receivePaymentRetObject.setMemo(value);
					else if (itemReceiptRetObject != null)
						itemReceiptRetObject.setMemo(value);
					else if (invoiceRetObject != null)
						invoiceRetObject.setMemo(value);
				}
				else if (current_element.equals("TimeCreated"))
				{
					Date time_created = time_modified_date_format.parse(value.substring(0, value.lastIndexOf("-")));

					if (salesReceiptRetObject != null)
						salesReceiptRetObject.setTimeCreated(time_created);
					else if (invoiceRetObject != null)
						invoiceRetObject.setTimeCreated(time_created);
					else if (receivePaymentRetObject != null)
						receivePaymentRetObject.setTimeCreated(time_created);
					else if (accountRetObject != null)
						accountRetObject.setTimeCreated(time_created);
					else if (vendorRetObject != null)
						vendorRetObject.setTimeCreated(time_created);
					else if ((itemSalesTaxRetObject != null) && doItemSalesTax)
						itemSalesTaxRetObject.setTimeCreated(time_created);
					else if ((itemSalesTaxGroupRetObject != null) && doItemSalesTaxGroup)
						itemSalesTaxGroupRetObject.setTimeCreated(time_created);
					else if (salesTaxCodeRetObject != null)
						salesTaxCodeRetObject.setTimeCreated(time_created);
					else if ((itemPaymentRetObject != null) && doItemPayment)
						itemPaymentRetObject.setTimeCreated(time_created);
					else if ((itemRetObject != null) && doItem)
						itemRetObject.setTimeCreated(time_created);
					else if ((itemGroupRetObject != null) && doItemGroup)
						itemGroupRetObject.setTimeCreated(time_created);
				}
				else if (current_element.equals("Balance"))
				{
					if (customerRetObject != null)
						customerRetObject.setAccountBalance(Float.parseFloat(value));
					else if (accountRetObject != null)
						accountRetObject.setBalance(Float.parseFloat(value));
					else if (vendorRetObject != null)
						vendorRetObject.setBalance(Float.parseFloat(value));
				}
				else if (current_element.equals("EditSequence"))
				{
					if (salesReceiptRetObject != null)
						salesReceiptRetObject.setEditSequence(value);
					else if (invoiceRetObject != null)
						invoiceRetObject.setEditSequence(value);
					else if (customerRetObject != null)
						customerRetObject.setEditSequence(value);
					else if (accountRetObject != null)
						accountRetObject.setEditSequence(value);
					else if (vendorRetObject != null)
						vendorRetObject.setEditSequence(value);
					else if ((itemSalesTaxRetObject != null) && doItemSalesTax)
						itemSalesTaxRetObject.setEditSequence(value);
					else if ((itemSalesTaxGroupRetObject != null) && doItemSalesTaxGroup)
						itemSalesTaxGroupRetObject.setEditSequence(value);
					else if (salesTaxCodeRetObject != null)
						salesTaxCodeRetObject.setEditSequence(value);
					else if ((itemPaymentRetObject != null) && doItemPayment)
						itemPaymentRetObject.setEditSequence(value);
					else if (itemReceiptRetObject != null)
						itemReceiptRetObject.setEditSequence(value);
					else if (billRetObject != null)
						billRetObject.setEditSequence(value);
					else if ((itemRetObject != null) && doItem)
						itemRetObject.setEditSequence(value);
					else if ((itemGroupRetObject != null) && doItemGroup)
						itemGroupRetObject.setEditSequence(value);
				}
				else if (current_element.equals("Name"))
				{
					if (accountRetObject != null)
						accountRetObject.setName(value);
					else if (vendorRetObject != null)
						vendorRetObject.setNameFromParse(value);
					else if ((itemRetObject != null) && doItem)
						itemRetObject.setName(value);
					else if ((itemSalesTaxRetObject != null) && doItemSalesTax)
						itemSalesTaxRetObject.setNameFromParse(value);
					else if ((itemSalesTaxGroupRetObject != null) && doItemSalesTaxGroup)
						itemSalesTaxGroupRetObject.setNameFromParse(value);
					else if (salesTaxCodeRetObject != null)
						salesTaxCodeRetObject.setNameFromParse(value);
					else if ((itemPaymentRetObject != null) && doItemPayment)
						itemPaymentRetObject.setNameFromParse(value);
					else if ((itemGroupRetObject != null) && doItemGroup)
						itemGroupRetObject.setName(value);
					else if (paymentMethodRetObject != null)
						paymentMethodRetObject.setName(value);
				}
				else if (current_element.equals("IsActive"))
				{
					boolean is_active = value.toLowerCase().equals("true");
					if (customerRetObject != null)
						customerRetObject.setIsActive(is_active);
					else if (accountRetObject != null)
						accountRetObject.setIsActive(is_active);
					else if (vendorRetObject != null)
						vendorRetObject.setIsActive(is_active);
					else if ((itemSalesTaxRetObject != null) && doItemSalesTax)
						itemSalesTaxRetObject.setIsActive(is_active);
					else if ((itemSalesTaxGroupRetObject != null) && doItemSalesTaxGroup)
						itemSalesTaxGroupRetObject.setIsActive(is_active);
					else if (salesTaxCodeRetObject != null)
						salesTaxCodeRetObject.setIsActive(is_active);
					else if ((itemPaymentRetObject != null) && doItemPayment)
						itemPaymentRetObject.setIsActive(is_active);
				}
				else if (current_element.equals("Sublevel"))
				{
					if (accountRetObject != null)
						accountRetObject.setSublevel(Short.parseShort(value));
				}
				else if (current_element.equals("AccountType"))
				{
					if (accountRetObject != null)
						accountRetObject.setAccountType(value);
				}
				else if (current_element.equals("AccountNumber"))
				{
					if (accountRetObject != null)
						accountRetObject.setAccountNumber(value);
					else if (vendorRetObject != null)
						vendorRetObject.setAccountNumber(value);
				}
				else if (current_element.equals("Desc"))
				{
					if (accountRetObject != null)
						accountRetObject.setDesc(value);
					else if (salesTaxCodeRetObject != null)
						salesTaxCodeRetObject.setDesc(value);
					else if (invoiceLineRetObject != null)
						invoiceLineRetObject.setDesc(value);
					else if (salesReceiptLineRetObject != null)
						salesReceiptLineRetObject.setDesc(value);
				}
				else if (current_element.equals("TotalBalance"))
				{
					if (accountRetObject != null)
						accountRetObject.setTotalBalance(Float.parseFloat(value));
				}
				else if (current_element.equals("TaxLineID"))
				{
					if (accountRetObject != null)
						accountRetObject.setTaxLineID(value);
				}
				else if (current_element.equals("TaxLineName"))
				{
					if (accountRetObject != null)
						accountRetObject.setTaxLineName(value);
				}
				else if (current_element.equals("CashFlowClassification"))
				{
					if (accountRetObject != null)
						accountRetObject.setCashFlowClassification(value);
				}
				else if (current_element.equals("Addr2"))
				{
					if (vendorRetObject != null)
						vendorRetObject.setAddress1(value);
				}
				else if (current_element.equals("Addr3"))
				{
					if (vendorRetObject != null)
						vendorRetObject.setAddress2(value);
				}
				else if (current_element.equals("City"))
				{
					if (vendorRetObject != null)
						vendorRetObject.setCity(value);
				}
				else if (current_element.equals("State"))
				{
					if (vendorRetObject != null)
						vendorRetObject.setState(value);
				}
				else if (current_element.equals("PostalCode"))
				{
					if (vendorRetObject != null)
						vendorRetObject.setZip(value);
				}
				else if (current_element.equals("Fax"))
				{
					if (customerRetObject != null)
						customerRetObject.setPhone2(value);
					else if (vendorRetObject != null)
						vendorRetObject.setFax(value);
				}
				else if (current_element.equals("IsVendorEligibleFor1099"))
				{
					if (vendorRetObject != null)
						vendorRetObject.setIsVendorEligibleFor1099(value.toLowerCase().equals("true"));
				}
				else if (current_element.equals("ItemDesc"))
				{
					if ((itemSalesTaxRetObject != null) && doItemSalesTax)
						itemSalesTaxRetObject.setItemDesc(value);
					else if ((itemSalesTaxGroupRetObject != null) && doItemSalesTaxGroup)
						itemSalesTaxGroupRetObject.setItemDesc(value);
					else if ((itemGroupRetObject != null) && doItemGroup)
						itemGroupRetObject.setDescription(value);
				}
				else if (current_element.equals("TaxRate"))
				{
					if ((itemSalesTaxRetObject != null) && doItemSalesTax)
						itemSalesTaxRetObject.setTaxRate(new BigDecimal(value));
				}
				else if (current_element.equals("IsTaxable"))
				{
					boolean is_taxable = value.toLowerCase().equals("true");
					if (salesTaxCodeRetObject != null)
						salesTaxCodeRetObject.setIsTaxable(is_taxable);
				}
				else if (current_element.equals("BalanceRemaining"))
				{
					if (invoiceRetObject != null)
						invoiceRetObject.setBalanceRemaining(Float.parseFloat(value));
				}
				else if (current_element.equals("RefNumber"))
				{
					if (invoiceRetObject != null)
						invoiceRetObject.setRefNumber(value);
					else if (itemReceiptRetObject != null)
						itemReceiptRetObject.setRefNumber(value);
					else if (billRetObject != null)
						billRetObject.setRefNumber(value);
					else if (purchaseOrderRetObject != null)
						purchaseOrderRetObject.setRefNumber(value);
				}
				else if (current_element.equals("IsPending"))
				{
					if (invoiceRetObject != null)
						invoiceRetObject.setIsPending(value);
				}
				else if (current_element.equals("IsFinanceCharge"))
				{
					if (invoiceRetObject != null)
						invoiceRetObject.setIsFinanceCharge(value);
				}
				else if (current_element.equals("IsPaid"))
				{
					if (invoiceRetObject != null)
						invoiceRetObject.setIsPaid(value);
					else if (billRetObject != null) {
						billRetObject.setIsPaid(value);
					}
				}
				else if (current_element.equals("AmountDue"))
				{
					if ((billRetObject != null))
						billRetObject.setAmountDue(Float.parseFloat(value));
				}
				else if (current_element.equals("OpenAmount"))
				{
					if ((billRetObject != null))
						billRetObject.setOpenAmount(Float.parseFloat(value));
				}
				else if (current_element.equals("DueDate"))
				{
					if (invoiceRetObject != null)
						invoiceRetObject.setDueDate(date_format.parse(value));
				}
				else if (current_element.equals("PONumber"))
				{
					if (invoiceRetObject != null)
						invoiceRetObject.setPONumber(value);
				}
				else if (current_element.equals("SalesPrice"))
				{
					if ((itemRetObject != null) && doItem)
						itemRetObject.setSalesPrice(Float.parseFloat(value));
				}
				else if (current_element.equals("PurchaseCost"))
				{
					if ((itemRetObject != null) && doItem)
						itemRetObject.setPurchaseCost(Float.parseFloat(value));
				}
				else if (current_element.equals("Price"))
				{
					if ((itemRetObject != null) && doItem)
						itemRetObject.setSalesPrice(Float.parseFloat(value));
				}
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

	}

	private void
	createNewPerson(UKOnlineCompanyBean _company, CustomerRet _customer)
		throws TorqueException, IllegalValueException, ObjectNotFoundException, ObjectAlreadyExistsException, Exception
	{
		UKOnlinePersonBean person = new UKOnlinePersonBean();

		person = new UKOnlinePersonBean();

		person.setFirstName(_customer.getFirstName());
		person.setLastName(_customer.getLastName());

		if ((_customer.getEMail() != null) && (_customer.getEMail().length() > 0))
			person.setEmail(_customer.getEMail());

		person.setFemale();

		//String password = PasswordGenerator.getPassword(3) + "-" + PasswordGenerator.getPassword(3);
									
		String password = PasswordGenerator.getPassword(PersonBean.getMinimumPasswordLength());
		boolean isUniquePw = PasswordGenerator.isUnique(password);
		while (!isUniquePw) {
			password = PasswordGenerator.getPassword(PersonBean.getMinimumPasswordLength());
			isUniquePw = PasswordGenerator.isUnique(password);
		}

		person.setUsername(password);
		person.setPassword(password);
		person.setConfirmPassword(password);

		person.setDepartment(_company.getDefaultDepartment());
		person.setTitle(PersonTitleBean.getDefaultTitle(_company));
		
		person.setFileNumber(UKOnlinePersonBean.getNextFileNumber(_company));

		person.setPersonType(UKOnlinePersonBean.CLIENT_PERSON_TYPE);

		person.setActive(_customer.isActive());

		person.save();

		if ((_customer.getPhone() != null) && (_customer.getPhone().length() > 0))
		{
			PhoneNumberBean home_phone = new PhoneNumberBean();
			home_phone.setNumber(_customer.getPhone());
			home_phone.setPerson(person);
			home_phone.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			home_phone.save();
		}

		if ((_customer.getPhone2() != null) && (_customer.getPhone2().length() > 0))
		{
			PhoneNumberBean cell_phone = new PhoneNumberBean();
			cell_phone.setNumber(_customer.getPhone2());
			cell_phone.setPerson(person);
			cell_phone.setType(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			cell_phone.save();
		}

		try
		{
			person.setGroup(PersonGroupBean.getDefaultPersonGroup(_company));
		}
		catch (ObjectAlreadyExistsException already_exists)
		{
			//already_exists.printStackTrace();
		}

		// create a new group under care

		GroupUnderCareBean group_under_care = new GroupUnderCareBean();
		group_under_care.setCreatePerson(_company.getAdministrator());
		group_under_care.setCompany(_company);
		group_under_care.setPrimaryClient(person);
		Vector members = new Vector();
		GroupUnderCareMember member = new GroupUnderCareMember();
		member.setPersonId(person.getId());
		member.setRelationshipToPrimaryClient(GroupUnderCareBean.RELATIONSHIP_SELF_TYPE);

		member.setGroupUnderCareMemberTypeId(0);
		members.addElement(member);
		group_under_care.setGroupUnderCareMembers(members);
		group_under_care.setNote("");
		group_under_care.save();
	}

	private void
	updatePerson(UKOnlineCompanyBean _company, CustomerRet _customer)
		throws TorqueException, IllegalValueException, ObjectNotFoundException, ObjectAlreadyExistsException, Exception
	{
		UKOnlinePersonBean person = null;
		try
		{
			person = UKOnlinePersonBean.getPersonByQBListId(_company, _customer.getQBFSListID(), false);
		}
		catch (ObjectNotFoundException x)
		{
			Vector existingpersons = UKOnlinePersonBean.getPersonsByLastNameFirstName(company, _customer.getLastName(), _customer.getFirstName());
			if (existingpersons.size() != 1)
				existingpersons = UKOnlinePersonBean.getPersonsByLastNameFirstNamePhone(company, _customer.getLastName(), _customer.getFirstName(), _customer.getPhone(), false);

			else if (existingpersons.size() == 1)
			{
				System.out.println("Existing customer found for " + _customer.getFirstName() + " " + _customer.getLastName());
				person = (UKOnlinePersonBean)existingpersons.get(0);
				person.setQBFSListID(_customer.getQBFSListID());
			}
		}

		if (person == null)
			throw new IllegalValueException("Unable to find existing person for QuickBooks List ID >" + _customer.getQBFSListID());

		person.setFirstName(_customer.getFirstName());
		person.setLastName(_customer.getLastName());

		if ((_customer.getEMail() != null) && (_customer.getEMail().length() > 0))
			person.setEmail(_customer.getEMail());

		person.setActive(_customer.isActive());
		
		person.setCurrentBalance(new BigDecimal(_customer.getAccountBalance()));

		person.save();

		if ((_customer.getPhone() != null) && (_customer.getPhone().length() > 0))
		{
			PhoneNumberBean home_phone;
			try
			{
				home_phone = person.getPhoneNumber(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			}
			catch (ObjectNotFoundException x)
			{
				home_phone = new PhoneNumberBean();
				home_phone.setPerson(person);
				home_phone.setType(PhoneNumberBean.HOME_PHONE_NUMBER_TYPE);
			}
			home_phone.setNumber(_customer.getPhone());
			home_phone.save();
		}

		if ((_customer.getPhone2() != null) && (_customer.getPhone2().length() > 0))
		{
			PhoneNumberBean cell_phone;
			try
			{
				cell_phone = person.getPhoneNumber(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			}
			catch (ObjectNotFoundException x)
			{
				cell_phone = new PhoneNumberBean();
				cell_phone.setPerson(person);
				cell_phone.setType(PhoneNumberBean.CELL_PHONE_NUMBER_TYPE);
			}
			cell_phone.setNumber(_customer.getPhone2());
			cell_phone.save();
		}
	}
}
