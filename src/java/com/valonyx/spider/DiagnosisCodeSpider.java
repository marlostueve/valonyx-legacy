/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.spider;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.TimerTask;

/**
 *
 * @author marlo
 */
public class DiagnosisCodeSpider
    extends TimerTask {
	
	SpiderLauncher parent;
	String domainToSearch = "drudgereport.com";
	String strURL = "http://www.drudgereport.com/";
	

	public DiagnosisCodeSpider(SpiderLauncher _parent, String _domainToSearch, String _searchUrl) {
		parent = _parent;
		domainToSearch = _domainToSearch;
		strURL = _searchUrl;
	}
	
	@Override
	public void run() {
		System.out.println("************Spider run - searching " + strURL);

	    URL url;
	    try { 
			url = new URL(strURL);
	    } catch (MalformedURLException e) {
			System.out.println("ERROR: invalid URL " + strURL);
			return;
	    }

	    try {
			// try opening the URL
			URLConnection urlConnection = url.openConnection();
		
		
			urlConnection.setAllowUserInteraction(false);

			InputStream urlStream = url.openStream();
			String type = urlConnection.guessContentTypeFromStream(urlStream);
			
			//System.out.println("type >" + type);
			
			/*
			if (type == null)
				return;
			if (type.compareTo("text/html") != 0) 
				return;
			 * 
			 */

			// search the input stream for links
			// first, read in the entire URL
			byte b[] = new byte[1000];
			int numRead = urlStream.read(b);
			String content = new String(b, 0, numRead);
			while (numRead != -1) {
				//if (Thread.currentThread() != searchThread)
				//	return;
				numRead = urlStream.read(b);
				if (numRead != -1) {
					String newContent = new String(b, 0, numRead);
					content += newContent;
				}
			}
			urlStream.close();

			//if (Thread.currentThread() != searchThread)
			//    break;

			String lowerCaseContent = content.toLowerCase();
			
			try {
				DiagnosisCodeMiner miner = new DiagnosisCodeMiner(content);
				miner.mine();
			} catch (Exception x) {
				x.printStackTrace();
			}
			
			//System.out.println("lowerCaseContent >" + lowerCaseContent);

			int index = 0;
			while ((index = lowerCaseContent.indexOf("<a", index)) != -1)
			{
				//System.out.println("index >" + index);
				
				if ((index = lowerCaseContent.indexOf("href", index)) == -1) 
					return;
				if ((index = lowerCaseContent.indexOf("=", index)) == -1) 
					return;

				//if (Thread.currentThread() != searchThread)
				//	return;

				index++;
				String remaining = content.substring(index);

				StringTokenizer st = new StringTokenizer(remaining, "\t\n\r\">#");
				String strLink = st.nextToken();
				
				
				
					//System.out.println("strLink >" + strLink);
					URL urlLink;
					try {
						urlLink = new URL(url, strLink);
						strLink = urlLink.toString();
					} catch (MalformedURLException e) {
						System.out.println("ERROR: bad URL " + strLink);
						continue;
					}
					
					String protocol = urlLink.getProtocol();
					String host = urlLink.getHost();
					
					
					if (protocol != null && protocol.equals("http")) {
						if (host.indexOf(domainToSearch) > -1)
							parent.addSearchLink(strLink);
					}
					
					
				
				
				
				
				
			}
			
			
	    }
		catch (IOException e) {
			System.out.println("ERROR: couldn't open URL " + strURL);
	    }
		
		parent.spiderComplete(strURL);
	}
	
}