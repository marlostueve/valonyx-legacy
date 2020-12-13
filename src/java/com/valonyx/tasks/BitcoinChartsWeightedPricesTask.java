/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.tasks;

import com.badiyan.uk.exceptions.IllegalValueException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Vector;


import org.json.simple.parser.*;

/**
 *
 * @author
 * marlo
 */
public class BitcoinChartsWeightedPricesTask
    extends TimerTask {
	
	String strURL = "http://api.bitcoincharts.com/v1/weighted_prices.json";
	
	public static Long timestamp = new Long(0);
	public static HashMap<String, HashMap> weighted_prices = new HashMap<String, HashMap>();
	
	public static BigDecimal
	convertAmountToBTC(String _currency, BigDecimal _amount) throws IllegalValueException {
		HashMap hash = (HashMap)com.valonyx.tasks.BitcoinChartsWeightedPricesTask.weighted_prices.get(_currency);
		if (hash == null) {
			throw new IllegalValueException("Unable to find a Bitcoin conversion rate for currency type " + _currency);
		}
		BigDecimal weighted_daily = (java.math.BigDecimal)hash.get("24h");
		if (weighted_daily == null) {
			throw new IllegalValueException("Weighted daily conversion rate not found for " + _currency);
		}
		BigDecimal converted_amount = _amount.divide(weighted_daily, MathContext.DECIMAL32);
		return converted_amount;
	}

	@Override
	public void run() {
		
		System.out.println("BitcoinChartsWeightedPricesTask.run() invoked");

	    URL url = null;
	    try { 
			url = new URL(strURL);
	    } catch (MalformedURLException e) {
			System.out.println("ERROR: invalid URL " + strURL);
			return;
	    }
		
		String line;
		String result = "";

	    try {
			// try opening the URL
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
			   result += line;
			}
			rd.close();
			
	    }
		catch (IOException e) {
			System.out.println("ERROR: couldn't open URL " + strURL);
	    }
		
		System.out.println("result - >" + result);
		
		
		JSONParser parser = new JSONParser();
		
		try {
			//Object obj = parser.parse(result);
			//JSONArray array = (JSONArray)parser.parse(result);
			ContainerFactory containerFactory = new ContainerFactory() {
				public List creatArrayContainer() {
					return new LinkedList();
				}

				public Map createObjectContainer() {
					return new LinkedHashMap();
				}
			};

			Vector vec = new Vector();
			Object json_obj = parser.parse(result, containerFactory);
			if (json_obj instanceof LinkedHashMap) {
			
				LinkedHashMap json = (LinkedHashMap)parser.parse(result, containerFactory);
				Iterator jsonItr = json.keySet().iterator();
				while (jsonItr.hasNext()) {
					//Map jsonX = (Map)jsonItr.next();
					String key = (String)jsonItr.next();
					//Iterator iter = jsonX.entrySet().iterator();
					System.out.println("==iterate result== >" + key);
					
					
					
					Object obj = json.get(key);
					if (obj instanceof LinkedHashMap) {
					
						HashMap<String, BigDecimal> h = new HashMap<String, BigDecimal>();
						BitcoinChartsWeightedPricesTask.weighted_prices.put(key, h);
						
						LinkedHashMap map = (LinkedHashMap)obj;
						Iterator itr_x = map.keySet().iterator();
						while (itr_x.hasNext()) {
							String key_x = (String)itr_x.next();
							//System.out.println("key_x >" + key_x);

							String value = (String)map.get(key_x);
							//System.out.println("value >" + value);
							
							h.put(key_x, new BigDecimal(value));
							
						}
					} else {
						Long value = (Long)obj;
						System.out.println("other value >" + value);
						
						if (key.equals("timestamp")) {
							BitcoinChartsWeightedPricesTask.timestamp = new Long(value);
						}
					}
					
					//System.out.println("jjb >" + jjb);

				}
				
			} else {
				
				System.out.println("what now??");
				
			}
			
			
			
			
			//System.out.println(array.get);
			
		} catch (ParseException x) {
			x.printStackTrace();
		}
		
		
	}
	
}
