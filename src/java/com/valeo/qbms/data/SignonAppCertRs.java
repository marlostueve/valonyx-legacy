/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qbms.data;

import com.valeo.qbpos.data.QueryRs;
import java.util.Date;

/**
 *
 * @author marlo
 */
public class SignonAppCertRs
	extends QueryRs
{
	private Date serverDateTime;
	private String sessionTicket;

	public Date getServerDateTime() {
		return serverDateTime;
	}

	public void setServerDateTime(Date serverDateTime) {
		this.serverDateTime = serverDateTime;
	}

	public String getSessionTicket() {
		return sessionTicket;
	}

	public void setSessionTicket(String sessionTicket) {
		this.sessionTicket = sessionTicket;
	}


}

