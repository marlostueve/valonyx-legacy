/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.servlets;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

//import com.google.gson.Gson;


/**
 *
 * @author marlo
 */
public class MessageEncoder implements Encoder.Text<String> {
	
    //private static Gson gson = new Gson();

    @Override
    public String encode(String message) throws EncodeException {
        /*
		String json = gson.toJson(message);
        return json;
		*/
		return message;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
