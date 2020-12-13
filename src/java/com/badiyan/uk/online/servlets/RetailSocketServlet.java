/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.servlets;

import com.badiyan.uk.exceptions.IllegalValueException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/*
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
*/

/**
 * WebSocketServlet is contained in catalina.jar. It also needs servlet-api.jar on build path
 *
 * @author wangs
 *
 */
//@WebServlet("/retailWsocket")
//public class RetailSocketServlet extends WebSocketServlet {
public class RetailSocketServlet {

	private static final long serialVersionUID = 1L;

	// for new clients, <sessionId, streamInBound>
	
	private static ConcurrentHashMap<String, Object> session_to_clients = new ConcurrentHashMap<String, Object>();
	private static ConcurrentHashMap<String, Vector> id_to_session = new ConcurrentHashMap<String, Vector>();
	
	private static ConcurrentHashMap<String, Object> id_to_clients = new ConcurrentHashMap<String, Object>();
	
	public static void sendMessage(String _message) throws IllegalValueException {
		
		// broadcast message
		
		Enumeration<String> keys = RetailSocketServlet.id_to_session.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			Vector vec = (Vector)RetailSocketServlet.id_to_session.get(key);
			if (vec != null) {
				/*
				Iterator itr = vec.iterator();
				while (itr.hasNext()) {
					String session_for_id = (String)itr.next();
					SocketInbound message_websocket = (SocketInbound)session_to_clients.get(session_for_id);
					//SocketInbound message_websocket = (SocketInbound)itr.next();
					System.out.println("client for id >" + key + ", session >" + session_for_id + ", client >" + message_websocket);

					if (message_websocket != null) {
						message_websocket.sendTextMessage(_message);
					}
				}
				*/

			}
		}
	}
	
	public static void sendMessage(String _personId, String _message) throws IllegalValueException {
		
		//SocketInbound message_websocket = (SocketInbound)RetailSocketServlet.clients.get(_sessionId);
		
		boolean found_client = false;
		
		Vector vec = (Vector)RetailSocketServlet.id_to_session.get(_personId);
		if (vec != null) {
			Iterator itr = vec.iterator();
			while (itr.hasNext()) {
				String session_for_id = (String)itr.next();
				/*
				SocketInbound message_websocket = (SocketInbound)session_to_clients.get(session_for_id);
				//SocketInbound message_websocket = (SocketInbound)itr.next();
				System.out.println("client for id >" + _personId + ", session >" + session_for_id + ", client >" + message_websocket);

				if (message_websocket != null) {
					message_websocket.sendTextMessage(_message);
					found_client = true;
				}
				*/
			}
			
			if (!found_client) {
				throw new IllegalValueException("Unable to locate WebSocket client for session >" + _personId);
			}
			
		} else {
			throw new IllegalValueException("Unable to locate client for person >" + _personId);
		}
		
	}

	/*
	@Override
	protected StreamInbound createWebSocketInbound(String protocol,
			HttpServletRequest httpServletRequest) {

		// Check if exists
		HttpSession session = httpServletRequest.getSession();
		
		String client_identifier = httpServletRequest.getParameter("id");
		String sessionStr = session.getId();
		
		System.out.println("createWebSocketInbound invoked for >" + client_identifier);
		System.out.println("sessionStr >" + sessionStr);
		
		
		Vector session_vec = id_to_session.get(client_identifier);
		if (session_vec == null) {
			session_vec = new Vector();
		}
		if (!session_vec.contains(sessionStr)) {
			session_vec.addElement(sessionStr);
		}
		id_to_session.put(client_identifier, session_vec);
		
		
		// find client
		StreamInbound client = session_to_clients.get(sessionStr);
		
		if (client != null) {
			return client;
		} else {
			client = new SocketInbound(httpServletRequest);
			System.out.println("putting client session id >" + sessionStr);
			session_to_clients.put(sessionStr, client);
		}

		return client;
	}
	*/

}