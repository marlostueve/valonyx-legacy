/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.util;

/*
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KAADeviceState {

    private static final Logger LOG = LoggerFactory.getLogger(KAADeviceState.class);

    //@SerializedName("active")
    private ValueType state;

    public ValueType getState() {
        return state;
    }

    public void setState(ValueType state) {
        this.state = state;
    }

    public class ValueType {
		
        //@SerializedName("boolean")
        private boolean active;

        public boolean isActive() {
            return active;
        }
        public void setActive(boolean active) {
            this.active = active;
        }
		
		
		/*
        @SerializedName("string")
        private String serialNumber;
	
        public String getSerialNumber() {
            return serialNumber;
        }
        public void setSerialNumber(String sn) {
            this.serialNumber = sn;
        }
		
		
        @SerializedName("string")
        private String message;
	
        public String getSerialNumber() {
            return serialNumber;
        }
        public void setSerialNumber(String sn) {
            this.serialNumber = sn;
        }
		*/
		
    }

    public static String toJsonString(boolean isActive, String _serialNumber, String _message) {
		
		/*
        KAADeviceState deviceState = new KAADeviceState();
        KAADeviceState.ValueType state = deviceState.new ValueType();
        state.setActive(isActive);
        deviceState.setState(state);

        String jsonString = null;
        try {
            jsonString = new Gson().toJson(deviceState);
        } catch (JsonSyntaxException e) {
            LOG.error("Json parsing exception");
        }

        return jsonString;
		*/
		
		if (isActive) {
			return "{\"active\":{\"boolean\":true},\"serialNumber\":{\"string\":\"\"},\"message\":{\"string\":\"\"}}";
		} else {
			if (_message == null || _message.isEmpty() || _message.equals("none")) {
				return "{\"active\":{\"boolean\":false},\"serialNumber\":{\"string\":\"\"},\"message\":{\"string\":\"\"}}";
			} else {
				return "{\"active\":{\"boolean\":false},\"serialNumber\":{\"string\":\"\"},\"message\":{\"string\":\"" + _message + "\"}}";
			}
		}
		
    }

    public static boolean parseJsonString(String jsonString) {
		
		System.out.println("parseJsonString invoked >" + jsonString);
		
        if (jsonString == null) {
            LOG.error("Invalid params. Json string is null");
            return false;
        }

        //KAADeviceState deviceState = null;
		/* older un-comment
        try {
            deviceState = new Gson().fromJson(jsonString, KAADeviceState.class);
        } catch (JsonSyntaxException e) {
            LOG.error("Json parsing exception");
        }
		*/
		
		/*
		if (deviceState != null) {
			if (deviceState.getState() == null) {
				return false;
			}
		}

        return deviceState != null && deviceState.getState().isActive();
		*/
		
		return jsonString.contains("\"active\" : { \"boolean\" : true}");
		
    }

    public static boolean isError(String jsonString) {
		
		/*
		System.out.println("getMessageFromJSONString invoked >" + jsonString);
		
        if (jsonString == null) {
            LOG.error("Invalid params. Json string is null");
            return false;
        }

        KAADeviceState deviceState = null;
        try {
            deviceState = new Gson().fromJson(jsonString, KAADeviceState.class);
        } catch (JsonSyntaxException e) {
            LOG.error("Json parsing exception");
        }
		
		if (deviceState != null) {
			if (deviceState.getState() == null) {
				return false;
			}
		}

        return deviceState != null && deviceState.getState().isActive();
		*/
		
		//{"active":{"boolean":false},"serialNumber":{"string":""},"message":{"string":"Unable to activate"}}
		return (jsonString.indexOf("Unable to activate") > -1);
    }

}
