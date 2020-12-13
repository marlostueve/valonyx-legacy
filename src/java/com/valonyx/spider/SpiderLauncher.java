/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.spider;

import com.badiyan.uk.online.beans.DiagnosisCodeVersion;
import java.util.Timer;
import java.util.Vector;

/**
 *
 * @author marlo
 */
public class SpiderLauncher {
	
	private static Timer timer = new Timer(true);

	public static final String SEARCH = "Search";
	public static final String STOP = "Stop";
	public static final String DISALLOW = "Disallow:";
	public static final int    SEARCH_LIMIT = 50;
	
	public static final int    MAX_THREADS = 5;
	
	// URLs to be searched
	private Vector vectorToSearch = new Vector();
	// URLs already searched
	private Vector vectorSearched = new Vector();
	// URLs which match
	private Vector vectorMatches = new Vector();
	
	private int currentThreads = 0;
	
	//String domainToSearch = "drudgereport.com";
	//String searchURL = "http://www.drudgereport.com/";
	
	String domainToSearch = "valonyx.com";
	//String searchURL = "http://www.icd9data.com/2011/Volume1/780-799/780-789/780/780.2.htm";
	String searchURL = "http://www.icd9data.com/2012/Volume2/A/AAT-to-Ablepsy.htm";
	
	long delay = 5 * 1000;
	
	public void launch (String _domainToSearch, String _searchURL) {
		
		System.out.println("Spider launch");
		
		domainToSearch = _domainToSearch;
		searchURL = _searchURL;
		
		timer.schedule(new DiagnosisCodeSpider(this, domainToSearch, searchURL), delay);
		currentThreads++;
		vectorSearched.addElement(searchURL);
		
	}
	
	public synchronized void addSearchLink(String _searchLink) {
		System.out.println("ADD LINK currentThreads >" + currentThreads + "< " + vectorToSearch.size());
		
		if (!vectorToSearch.contains(_searchLink) && !vectorSearched.contains(_searchLink))
			vectorToSearch.addElement(_searchLink);
		
		if ((currentThreads < SpiderLauncher.MAX_THREADS) && !vectorToSearch.isEmpty()) {
			String nextLink = (String)vectorToSearch.remove(0);
			timer.schedule(new DiagnosisCodeSpider(this, domainToSearch, nextLink), delay);
			currentThreads++;
			vectorSearched.addElement(nextLink);
		}
	}
	
	public synchronized void spiderComplete(String _searchLink) {
		
		currentThreads--;
		System.out.println("SPIDER COMPLETE currentThreads >" + currentThreads + "< " + vectorToSearch.size());
		
		if (!vectorToSearch.isEmpty()) {
			String nextLink = (String)vectorToSearch.remove(0);
			timer.schedule(new DiagnosisCodeSpider(this, domainToSearch, nextLink), delay);
			currentThreads++;
			vectorSearched.addElement(nextLink);
		}
	}
	
}
