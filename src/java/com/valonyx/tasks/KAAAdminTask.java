/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.tasks;


import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.online.servlets.RetailSocketServlet;
import com.valonyx.beans.Endpoint;
import com.valonyx.util.*;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 *
 * @author
 * marlo
 */
public class KAAAdminTask
    extends TimerTask {
	
    //private final HashMap<Peer, String> reverseDnsLookups = new HashMap<Peer, String>();
	
	private static final Logger LOG = LoggerFactory.getLogger(KAAAdminTask.class);
	//private final static String APPLICATION_NAME = "Endpoint activation demo";
	
	private KAAAdminClientManager clientManager;
	
	public KAAAdminTask() {
		System.out.println("KAAAdminTask() invoked");
	}

	@Override
	public void run() {
		
		System.out.println("KAAAdminTask.run() invoked");

		clientManager = KAAAdminClientManager.instance();
				
		// grab endpoints 
		
		Map<String, EndpointProfileDto> endpointProfiles = retrieveAllEndpointProfiles();

		if (endpointProfiles == null || endpointProfiles.isEmpty()) {
			LOG.info("There are no endpoints registered!");
			return;
		}


		for (Map.Entry<String, EndpointProfileDto> entry : endpointProfiles.entrySet()) {
			EndpointProfileDto endpointProfile = entry.getValue();

			// maintain endpoint

			try {
				String profileId = entry.getKey();
				Endpoint.maintainEndpoint(endpointProfile, profileId);

			} catch (Exception x) {
				x.printStackTrace();
			}

			// send test message


			//boolean isActive = KAADeviceState.parseJsonString(endpointProfile.getServerProfileBody());
			//LOG.info("Profile id: " + entry.getKey() + ", endpointHash: " + endpointKeyHash + ", device state: " + (isActive ? "active" : "inactive") );

		}


		try {
			RetailSocketServlet.sendMessage("reload");
		} catch (IllegalValueException x) {
			x.printStackTrace();
		}



		printEndpointProfiles(endpointProfiles);
		
		
		
	}

	/*
    private static void useAdminClient() {
        Map<String, EndpointProfileDto> endpointProfiles = retrieveEndpointProfiles();
        if (endpointProfiles.isEmpty()) {
            LOG.info("There is no endpoints registered!");
            return;
        }
        printAllEndpointProfiles(endpointProfiles);

        for (;;) {
            LOG.info("Specify endpoint profile id# you want to activate/deactivate or print 'exit' to exit");
            String userInput = KAAUtils.getUserInput();
            if (userInput.equalsIgnoreCase("exit")) {
                return;
            }
            if (endpointProfiles.size() > 0) {
                if (endpointProfiles.containsKey(userInput)) {
                    updateServerProfile(endpointProfiles.get(userInput));
                } else {
                    LOG.info("Profile index is incorrect");
                }
            }
            endpointProfiles = retrieveEndpointProfiles();
            printAllEndpointProfiles(endpointProfiles);
        }
    }
	*/


    /**
     * Retrieve all endpoint profiles associated with activation application
     *
     * @return endpoint profiles associated with activation application
     */
    private static Map<String, EndpointProfileDto> retrieveAllEndpointProfiles() {
		KAAAdminClientManager clientManager = KAAAdminClientManager.instance();
        return clientManager.getEndpointProfilesForGroupID(KAAAdminClientManager.ALL_GROUP_ID);
		
        //KAAAdminClientManager clientManager = KAAAdminClientManager.instance();
        //List<EndpointGroupDto> endpointGroups = clientManager.getEndpointGroupsByApplicationName(RetailServlet.KAA_APPLICATION_NAME);
		
		//if (endpointGroups == null) {
		//	return new HashMap<>();
		//} else {
		//	return clientManager.getEndpointProfiles(endpointGroups);
		//}
		

		// not sure why this wasn't compiling...
        //return endpointGroups != null ? clientManager.getEndpointProfiles(endpointGroups) : new HashMap<>();
		//
    }
	
    private static Map<String, EndpointProfileDto> retrieveActiveEndpointProfiles() {
        KAAAdminClientManager clientManager = KAAAdminClientManager.instance();
        return clientManager.getEndpointProfilesForGroupID(KAAAdminClientManager.ACTIVE_GROUP_ID);
    }
	
    private static Map<String, EndpointProfileDto> retrieveInactiveEndpointProfiles() {
        KAAAdminClientManager clientManager = KAAAdminClientManager.instance();
        return clientManager.getEndpointProfilesForGroupID(KAAAdminClientManager.INACTIVE_GROUP_ID);
    }


    
    private static void printEndpointProfiles(Map<String, EndpointProfileDto> endpointProfiles) {
        LOG.info("Endpoint profiles: ");
        for (Map.Entry<String, EndpointProfileDto> entry : endpointProfiles.entrySet()) {
            EndpointProfileDto endpointProfile = entry.getValue();
            //String endpointKeyHash = Base64.getEncoder().encodeToString(endpointProfile.getEndpointKeyHash());
			String endpointKeyHash = "[NOT FOUND]";
            boolean isActive = KAADeviceState.parseJsonString(endpointProfile.getServerProfileBody());
            LOG.info("Profile id: " + entry.getKey() + ", endpointHash: " + endpointKeyHash + ", device state: " + (isActive ? "active" : "inactive") );
        }
    }
	
	
}
