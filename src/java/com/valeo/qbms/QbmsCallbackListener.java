/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbms;

/**
 *
 * @author marlo
 */
public class QbmsCallbackListener
	implements com.intuit.qbmsconnector.servlet.QbmsCallbackListener
{

	public QbmsCallbackListener() {
		System.out.println(" QbmsCallbackListener no-arg constructor invoked - ");
	}

	@Override
	public void onMerchantSignup(String connectionTicket, String merchantIdentifier) {
		System.out.println("onMerchantSignup() invoked in QbmsCallbackListener");
		System.out.println("connectionTicket >" + connectionTicket);
		System.out.println("merchantIdentifier >" + merchantIdentifier);
	}

	@Override
	public void onMerchantCancel(String connectionTicket, String merchantIdentifier) {
		System.out.println("onMerchantCancel() invoked in QbmsCallbackListener");
		System.out.println("connectionTicket >" + connectionTicket);
		System.out.println("merchantIdentifier >" + merchantIdentifier);
	}

	@Override
	public void onError(Throwable thrwbl) {
		System.out.println("onError() invoked in QbmsCallbackListener >" + thrwbl.getMessage());
		thrwbl.printStackTrace();
	}
}
