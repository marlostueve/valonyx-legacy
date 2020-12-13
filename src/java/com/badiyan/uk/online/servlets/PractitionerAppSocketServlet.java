/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.servlets;

import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.EncodeException;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

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
//@WebServlet("/wsocket")
//public class PractitionerAppSocketServlet extends WebSocketServlet {
@ServerEndpoint(value = "/wsocket/{key}", encoders = MessageEncoder.class)
public class PractitionerAppSocketServlet  {
	
	
	private static final Set<PractitionerAppSocketServlet> pracAppEndpoints = new CopyOnWriteArraySet<>();
	private static HashMap<String, String> users = new HashMap<>();
	private Session session;

	// for new clients, <sessionId, streamInBound>
	private static ConcurrentHashMap<String, PractitionerAppSocketServlet> session_clients = new ConcurrentHashMap<String, PractitionerAppSocketServlet>();
	private static ConcurrentHashMap<String, Vector> id_to_session = new ConcurrentHashMap<String, Vector>();
	
	
	
	
	@OnOpen
    public void onOpen(Session session, @PathParam("key") String key) throws IOException, EncodeException {
		
		System.out.println("PractitionerAppSocketServlet onOpen invoked - session >" + session.getId() + ", key >" + key + ", this >" + this);
		
        this.session = session;
        pracAppEndpoints.add(this);
		
        users.put(session.getId(), key);
		
		
		try {
			//UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(key);
			
			UKOnlinePersonBean logged_in_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(key));

			String sessionStr = session.getId();

			System.out.println("createWebSocketInbound invoked for >" + key);
			System.out.println("sessionStr >" + sessionStr);


			Vector session_vec = id_to_session.get(key);
			if (session_vec == null) {
				session_vec = new Vector();
			}
			if (!session_vec.contains(sessionStr)) {
				session_vec.addElement(sessionStr);
			}
			id_to_session.put(key, session_vec);


			
			// find client
			PractitionerAppSocketServlet client = session_clients.get(sessionStr);

			if (client != null) {
				//return client;
			} else {
				//client = new SocketInbound(httpServletRequest);
				System.out.println("putting client session id >" + sessionStr);
				session_clients.put(sessionStr, this);
			}
			
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		
    }
	
	
	/*
	@Override
	protected StreamInbound createWebSocketInbound(String protocol,
			HttpServletRequest httpServletRequest) {

		// Check if exists
		HttpSession session = httpServletRequest.getSession();
		
		String personId = httpServletRequest.getParameter("id");
		String sessionStr = session.getId();
		
		System.out.println("createWebSocketInbound invoked for >" + personId);
		System.out.println("sessionStr >" + sessionStr);
		
		
		Vector session_vec = id_to_session.get(personId);
		if (session_vec == null) {
			session_vec = new Vector();
		}
		if (!session_vec.contains(sessionStr)) {
			session_vec.addElement(sessionStr);
		}
		id_to_session.put(personId, session_vec);
		
		
		// find client
		StreamInbound client = session_clients.get(sessionStr);
		
		if (client != null) {
			return client;
		} else {
			client = new SocketInbound(httpServletRequest);
			System.out.println("putting client session id >" + sessionStr);
			session_clients.put(sessionStr, client);
		}

		return client;
	}
	*/
	
	
	
	public static boolean isConnected(UKOnlinePersonBean _person) {
		
		/*
		users.keySet().forEach(key -> {
			String value = users.get(key);
			
		});
		*/
		
		
		Vector vec = (Vector)PractitionerAppSocketServlet.id_to_session.get(_person.getValue());
		if (vec != null) {
			Iterator itr = vec.iterator();
			while (itr.hasNext()) {
				String session_for_id = (String)itr.next();
				
				//SocketInbound message_websocket = (SocketInbound)session_clients.get(session_for_id);
				PractitionerAppSocketServlet message_websocket = (PractitionerAppSocketServlet)session_clients.get(session_for_id);
				if (message_websocket != null) {
					return true;
				}
				
			}
		}
		return false;
		
	}
	
	public static void sendMessage(String _personId, String _message) throws IllegalValueException {
		
		//SocketInbound message_websocket = (SocketInbound)PractitionerAppSocketServlet.clients.get(_sessionId);
		
		boolean found_client = false;
		
		Vector vec = (Vector)PractitionerAppSocketServlet.id_to_session.get(_personId);
		if (vec != null) {
			Iterator itr = vec.iterator();
			while (itr.hasNext()) {
				String session_for_id = (String)itr.next();
				
				/*
				SocketInbound message_websocket = (SocketInbound)session_clients.get(session_for_id);
				//SocketInbound message_websocket = (SocketInbound)itr.next();
				System.out.println("client for id >" + _personId + ", session >" + session_for_id + ", client >" + message_websocket);

				if (message_websocket != null) {
					message_websocket.sendTextMessage(_message);
					found_client = true;
				}
				*/
				

				//ScheduleSocketServlet client = (ScheduleSocketServlet)websocket_clients.get(_key);
				PractitionerAppSocketServlet client = (PractitionerAppSocketServlet)session_clients.get(session_for_id);
				if (client == null) {
					throw new IllegalValueException("Unable to locate client for _key.  This is a vague error message.");
				} else {
					System.out.println("sending message to client for key >" + _personId);
					//client.sendTextMessage(_message);

					try {
						client.session.getBasicRemote().sendObject(_message);
					} catch (IOException | EncodeException e) {
						e.printStackTrace();
					}

				}
				
			}
			
			if (!found_client) {
				throw new IllegalValueException("Unable to locate WebSocket client for session >" + _personId);
			}
			
		} else {
			throw new IllegalValueException("Unable to locate client for person >" + _personId);
		}
		
		
	}

	


}