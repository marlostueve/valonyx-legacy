/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.util;


import org.kaaproject.kaa.common.dto.*;
import org.kaaproject.kaa.common.dto.admin.AuthResultDto;
import org.kaaproject.kaa.server.common.admin.AdminClient;


import org.slf4j.*;
import java.util.*;

/**
 *
 * @author marlo
 */
public class KAAAdminClientManager {
	
    private static final Logger LOG = LoggerFactory.getLogger(KAAAdminClientManager.class);

    private static final String TENANT_DEV_USERNAME = "admin_dev";
    private static final String TENANT_DEV_PASSWORD = "KKz2dQLn";
    private static final String DEFAULT_LIMIT = "50";
    private static final String DEFAULT_OFFSET = "0";
    private static final int KAA_PORT = 8080;
	
	
    public static final String ALL_GROUP_ID = "32768";
    public static final String ACTIVE_GROUP_ID = "32769";
    public static final String INACTIVE_GROUP_ID = "32770";
    public static final String ERROR_GROUP_ID = "32771";
	
    public static final int SERVER_PROFILE_VERSION = 1;

    private AdminClient adminClient;
    private static KAAAdminClientManager instance;

    private KAAAdminClientManager(String host, int port) {
		LOG.info("KAAAdminClientManager invoked>" + host + ":" + port);
        adminClient = new AdminClient(host, port);
		LOG.info("got here");
    }

    public static void init(String host) {
		LOG.info("Admin client was not initialized");
        init(host, KAA_PORT);
    }

    public static void init(String host, int port) {
		LOG.info("init host>" + host);
		LOG.info("init port>" + port);
        instance = new KAAAdminClientManager(host, port);
    }

    public static KAAAdminClientManager instance() {
        if (instance == null) {
            LOG.error("Admin client was not initialized");
        }
        return instance;
    }

    
    public boolean checkAuth() {
        AuthResultDto.Result authResult = null;
        try {
            authResult = adminClient.checkAuth().getAuthResult();
        } catch (Exception e) {
            LOG.error("Exception has occurred: " + e.getMessage());
        }
        return authResult == AuthResultDto.Result.OK;
    }

    public void checkAuthorizationAndLogin() {
        if (!checkAuth()) {
			System.out.println("checkAuth failed - logging in");
            adminClient.login(TENANT_DEV_USERNAME, TENANT_DEV_PASSWORD);
        } else {
			System.out.println("already logged in");
		}
    }
    
    public EndpointProfileDto updateServerProfile(String endpointProfileKey, int serverProfileVersion, String serverProfileBody) {
        checkAuthorizationAndLogin();

        EndpointProfileDto endpointProfile = null;
        try {
            endpointProfile = adminClient.updateServerProfile(endpointProfileKey, serverProfileVersion, serverProfileBody);
        } catch (Exception e) {
            LOG.error("Exception has occurred: " + e.getMessage());
        }
        return endpointProfile;
    }
	
	private HashMap<String, ApplicationDto> app_hash = new HashMap<String, ApplicationDto>();

   
    public ApplicationDto getApplicationByName(String applicationName) {
        
		ApplicationDto app = (ApplicationDto)app_hash.get(applicationName);
		if (app != null) {
			return app;
		}
		
		checkAuthorizationAndLogin();

        try {
            List<ApplicationDto> applications = adminClient.getApplications();
            for (ApplicationDto application : applications) {
                if (application.getName().trim().equals(applicationName)) {
					app_hash.put(applicationName, application);
                    return application;
                }
            }
        } catch (Exception e) {
            LOG.error("Exception has occurred: " + e.getMessage());
        }
        return null;
    }

    
    public List<EndpointGroupDto> getEndpointGroups(String applicationId) {
        checkAuthorizationAndLogin();

        List<EndpointGroupDto> endpointGroups = null;
        try {
            endpointGroups = adminClient.getEndpointGroups(applicationId);
			
			Iterator itr = endpointGroups.iterator();
			while (itr.hasNext()) {
				EndpointGroupDto ep_group_obj = (EndpointGroupDto)itr.next();
				System.out.println("ep_group_obj >" + ep_group_obj.getName() + ", id >" + ep_group_obj.getId());
			}
			
        } catch (Exception e) {
            LOG.error("Exception has occurred: " + e.getMessage());
        }
        return endpointGroups;
    }
	
	private HashMap<String, EndpointGroupDto> group_hash = new HashMap<String, EndpointGroupDto>();
	
    public EndpointGroupDto getEndpointGroup(String _groupId) throws Exception {
		
		if (group_hash.containsKey(_groupId)) {
			return (EndpointGroupDto)group_hash.get(_groupId);
		}
		
        checkAuthorizationAndLogin();

        List<EndpointGroupDto> endpointGroups = null;
        try {
            EndpointGroupDto ep_group_obj = adminClient.getEndpointGroup(_groupId);
			group_hash.put(_groupId, ep_group_obj);
			return ep_group_obj;
			
        } catch (Exception e) {
            LOG.error("Exception has occurred: " + e.getMessage());
			throw e;
        }
    }
    
    public EndpointProfilesPageDto getEndpointProfileByEndpointGroupId(String endpointGroupId) {
        checkAuthorizationAndLogin();

        PageLinkDto pageLink = new PageLinkDto(endpointGroupId, DEFAULT_LIMIT, DEFAULT_OFFSET);
        EndpointProfilesPageDto endpointProfile = null;
        try {
            endpointProfile = adminClient.getEndpointProfileByEndpointGroupId(pageLink);
        } catch (Exception e) {
            LOG.error("Exception has occurred: " + e.getMessage());
        }
        return endpointProfile;
    }
    
    public List<EndpointGroupDto> getEndpointGroupsByApplicationName(String applicationName) {
		System.out.println("getEndpointGroupsByApplicationName invoked >" + applicationName);
		
        ApplicationDto applicationDto = getApplicationByName(applicationName);
        if (applicationDto == null) {
            LOG.error("There is no application with {} name", applicationName);
            return null;
        }

        return getEndpointGroups(applicationDto.getId());
    }
   
    public Map<String, EndpointProfileDto> getEndpointProfiles(List<EndpointGroupDto> endpointGroups) {
        Map<String, EndpointProfileDto> endpointProfiles = new LinkedHashMap<>();
        for (EndpointGroupDto endpointGroup : endpointGroups) {
            EndpointProfilesPageDto endpointProfilesDto = getEndpointProfileByEndpointGroupId(endpointGroup.getId());
            if (endpointProfilesDto == null) {
                continue;
            }
            for (EndpointProfileDto endpointProfile : endpointProfilesDto.getEndpointProfiles()) {
                endpointProfiles.put(endpointProfile.getId(), endpointProfile);
            }
        }

        return endpointProfiles;
    }
	
    public Map<String, EndpointProfileDto> getEndpointProfilesForGroupID(String _endpointGroupId) {
        Map<String, EndpointProfileDto> endpointProfiles = new LinkedHashMap<>();
		EndpointProfilesPageDto endpointProfilesDto = getEndpointProfileByEndpointGroupId(_endpointGroupId);
		if (endpointProfilesDto == null) {
			return null;
		}
		for (EndpointProfileDto endpointProfile : endpointProfilesDto.getEndpointProfiles()) {
			endpointProfiles.put(endpointProfile.getId(), endpointProfile);
		}

        return endpointProfiles;
    }
	
    public EndpointProfileDto getEndpointProfilesForKeyHash(String _key_hash) throws Exception {
		return adminClient.getEndpointProfileByKeyHash(_key_hash);
    }
	
	
	
}
