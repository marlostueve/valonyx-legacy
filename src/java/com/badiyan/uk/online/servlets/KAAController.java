/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 *
 * @author marlo
 */
//@Controller
//@RequestMapping("/")
public class KAAController {
	
 
	
    final static Logger LOGGER = LoggerFactory.getLogger(KAAController.class);
 
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "kaaLogAppender")
    public void encryptFile(@RequestBody String json) throws Exception {
        LOGGER.info(json);
    }
	
	
}
