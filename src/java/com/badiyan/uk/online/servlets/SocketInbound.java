/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.servlets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import javax.servlet.http.HttpServletRequest;

/*
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
*/

/**
 * Need tomcat-koyote.jar on class path, otherwise has compile error "the hierarchy of the type ... is inconsistent"
 *
 * @author wangs
 *
 */
public class SocketInbound {

	private String name;

	//private WsOutbound myoutbound;

	public SocketInbound(HttpServletRequest httpServletRequest) {

	}
	
    public synchronized boolean sendTextMessage(String message) {
		
		/*
        try {
            CharBuffer buffer = CharBuffer.wrap(message);
            this.myoutbound.writeTextMessage(buffer);
            this.myoutbound.flush();
			return true;
        } catch (IOException e) {
			return false;
        }
		*/
		
		return false;
    }

	/*
	@Override
	public void onOpen(WsOutbound outbound) {
		System.out.println("on open..");
		this.myoutbound = outbound;
		
	}

	@Override
	public void onClose(int status) {
		System.out.println("Close client");
		//remove from list
		try {
			ScheduleSocketServlet.closeClient(this);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	@Override
	protected void onBinaryMessage(ByteBuffer arg0) throws IOException {

	}

	@Override
	protected void onTextMessage(CharBuffer inChar) throws IOException {

		System.out.println("Accept msg >" + inChar);
		CharBuffer outbuf = CharBuffer.wrap("- " + this.name + " says : ");
		CharBuffer buf = CharBuffer.wrap(inChar);


	}
*/

}
