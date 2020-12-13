/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.servlets;

import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import java.util.Set;

import java.io.IOException;

//import javax.servlet.annotation.WebServlet;
import java.util.HashMap;
import java.util.Iterator;
//import javax.servlet.http.HttpSession;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;

import java.util.concurrent.CopyOnWriteArraySet;

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
/*
@WebServlet("/scheduleWebSocket")
*/
@ServerEndpoint(value = "/scheduleWebSocket/{key}", encoders = MessageEncoder.class)
public class ScheduleSocketServlet {

	
	private static final Set<ScheduleSocketServlet> scheduleEndpoints = new CopyOnWriteArraySet<>();
	private static HashMap<String, String> users = new HashMap<>();

	// for new clients, <sessionId, streamInBound>
	
	private static HashMap<UKOnlinePersonBean, String> person_key_hash = new HashMap<UKOnlinePersonBean, String>();
	private static HashMap<String, ScheduleSocketServlet> websocket_clients = new HashMap<>();
	
	/*
	private static ConcurrentHashMap<String, StreamInbound> websocket_clients = new ConcurrentHashMap<String, StreamInbound>();
	private static ConcurrentHashMap<UKOnlinePersonBean, String> person_key_hash = new ConcurrentHashMap<UKOnlinePersonBean, String>();
	*/
	//private static ConcurrentHashMap<String, SocketInbound> websocket_clients = new ConcurrentHashMap<String, SocketInbound>();
	//private static ConcurrentHashMap<String, Vector> id_to_session = new ConcurrentHashMap<String, Vector>();
	// trust not the session
	
	private Session session;
	
	
	
	@OnOpen
    public void onOpen(Session session, @PathParam("key") String key) throws IOException, EncodeException {
		
		System.out.println("onOpen invoked - session >" + session.getId() + ", key >" + key + ", this >" + this);
		

        this.session = session;
        scheduleEndpoints.add(this);
		
		
        users.put(session.getId(), key);
		
		
		
		/*
        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");
		*/
		
		
		try {
			UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(key);

			System.out.println("createWebSocketInbound invoked for >" + key + ", " + logged_in_person.getLabel());
			//System.out.println("sessionStr >" + sessionStr); // can't trust this session, I'm thinking...

			if (ScheduleSocketServlet.person_key_hash.containsKey(logged_in_person)) {
				String old_key = (String)ScheduleSocketServlet.person_key_hash.get(logged_in_person);
				System.out.println("removing old key >" + old_key);
				websocket_clients.remove(old_key);
			}
			System.out.println("person_key_hash putting >" + logged_in_person.getLabel() + " >" + key);
			ScheduleSocketServlet.person_key_hash.put(logged_in_person, key);

			ScheduleSocketServlet.broadcastMessage("{\"chatSignIn\":" + logged_in_person.getId() + "}");

			/*
			if (websocket_clients.containsKey(key)) {
				System.out.println("found existing websocket client for key >" + key);
				//return websocket_clients.get(key);
			} else {
				//System.out.println("creating websocket client for key >" + key);
				//SocketInbound client = new SocketInbound(httpServletRequest);
				//websocket_clients.put(key, client);
				//return client;
				websocket_clients.put(key, this);
			}
			*/
			
			websocket_clients.put(key, this);
			
		} catch (IllegalValueException x) {
			x.printStackTrace();
		}
		
		
    }
 
	
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // Handle new messages
		System.out.println("onMessage has happened");
		System.out.println("message >" + message);
    }
	
 
    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("onClose has happened");
    }
 
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("onError has happened >");
		throwable.printStackTrace();
    }
	
	
 
    public static void broadcastMessage(String message) 
      throws IOException, EncodeException {
		
		
		// send this message to all connected websocket clients
		
		System.out.println("broadcastMessage() invoked >" + message);
		//System.out.println("websocket_clients >" + websocket_clients.size());
		
        scheduleEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().sendObject(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
	
	
	
	
	public static boolean
	isConnected(String _key) {
		
		/*
		SocketInbound client = (SocketInbound)websocket_clients.get(_key);
		if (client == null) {
			return false;
		} else {
			return true;
		}
		*/
		
		
		//SocketInbound client = (SocketInbound)websocket_clients.get(_key);
		
		return websocket_clients.containsKey(_key);
		
		/*
		if (client == null) {
			return false;
		}
		
		return true;
		*/
	}
	
	public static void closeClient(String _key) {
		System.out.println("removing from websocket_clients >" + _key);
		//websocket_clients.remove(_key);
	}
	
	public static void closeClient(SocketInbound _client) throws IllegalValueException {
		
		UKOnlinePersonBean person_being_removed = null;
		
		/*
		Iterator itr = websocket_clients.keySet().iterator(); // prolz need to sync this or something
		while (itr.hasNext()) {
			String key = (String)itr.next();
			SocketInbound client = (SocketInbound)websocket_clients.get(key);
			if (client != null && client.equals(_client)) {
				person_being_removed = ScheduleSocketServlet.getPersonForKey(key);
				websocket_clients.remove(key);
			}
		}
		
		if (person_being_removed != null) {
			System.out.println("removing client from hash >" + person_being_removed.getLabel());
			ScheduleSocketServlet.broadcastMessage("{\"chatSignOut\":" + person_being_removed.getId() + "}");
		}
		*/
		
	}
	
	
	private static UKOnlinePersonBean
	getPersonForKey(String _key) {
		//System.out.println("getPersonForKey >" + _key);
		Iterator itr = ScheduleSocketServlet.person_key_hash.keySet().iterator();
		while (itr.hasNext()) {
			UKOnlinePersonBean hash_key = (UKOnlinePersonBean)itr.next();
			//System.out.println("test person >" + hash_key.getLabel());
			String value = ScheduleSocketServlet.person_key_hash.get(hash_key);
			//System.out.println("value >" + value);
			if (value != null) {
				if (_key.equals(value)) {
					return hash_key;
				}
			}
		}
		return null;
	}
	
	
	
	/*
	public static void broadcastMessage(String _message) throws IllegalValueException {
		
		// send this message to all connected websocket clients
		
		System.out.println("broadcastMessage() invoked >" + _message);
		//System.out.println("websocket_clients >" + websocket_clients.size());
		
		
		
		Iterator itr = websocket_clients.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String)itr.next();
			System.out.println("key >" + key);
			SocketInbound client = (SocketInbound)websocket_clients.get(key);
			if (client != null) {
				System.out.println("sending message to client for key >" + key);
				client.sendTextMessage(_message);
			}
		}
		
	}
	*/
	
	public static void sendMessage(String _key, String _message) throws IllegalValueException {
		
		System.out.println("sendMessage() invoked >" + _key + ", _message >" + _message);
		
		// send this message to all connected websocket clients
		
		/*
		SocketInbound client = (SocketInbound)websocket_clients.get(_key);
		if (client == null) {
			throw new IllegalValueException("Unable to locate client for _key >" + _key + ".  This is a terrible error message.");
		} else {
			System.out.println("sending message to client for key >" + _key);
			client.sendTextMessage(_message);
		}
		*/
		
		
		ScheduleSocketServlet client = (ScheduleSocketServlet)websocket_clients.get(_key);
		if (client == null) {
			throw new IllegalValueException("Unable to locate client for _key >" + _key + ".  This is a terrible error message.");
		} else {
			System.out.println("sending message to client for key >" + _key);
			//client.sendTextMessage(_message);
			
			try {
				client.session.getBasicRemote().sendObject(_message);
			} catch (IOException | EncodeException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}
	

	/*
	@Override
	protected StreamInbound createWebSocketInbound(String protocol, HttpServletRequest httpServletRequest) {

		
		try {

			
			String key = httpServletRequest.getParameter("id");

			UKOnlinePersonBean logged_in_person = ScheduleServlet2.verifyKey(key);

			System.out.println("createWebSocketInbound invoked for >" + key + ", " + logged_in_person.getLabel());
			//System.out.println("sessionStr >" + sessionStr); // can't trust this session, I'm thinking...
			
			if (ScheduleSocketServlet.person_key_hash.containsKey(logged_in_person)) {
				String old_key = (String)ScheduleSocketServlet.person_key_hash.get(logged_in_person);
				System.out.println("removing old key >" + old_key);
				websocket_clients.remove(old_key);
			}
			System.out.println("person_key_hash putting >" + logged_in_person.getLabel() + " >" + key);
			ScheduleSocketServlet.person_key_hash.put(logged_in_person, key);
			//ScheduleSocketServlet.broadcastMessage("in >" + logged_in_person.getId());
			ScheduleSocketServlet.broadcastMessage("{\"chatSignIn\":" + logged_in_person.getId() + "}");

			if (websocket_clients.containsKey(key)) {
				System.out.println("found existing websocket client for key >" + key);
				return websocket_clients.get(key);
			} else {
				System.out.println("creating websocket client for key >" + key);
				SocketInbound client = new SocketInbound(httpServletRequest);
				websocket_clients.put(key, client);
				return client;
			}
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		return null;
	}
	*/
	

}