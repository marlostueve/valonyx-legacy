/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.util;

import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
//import net.glxn.qrgen.QRCode;

/**
 *
 * @author
 * marlo
 */
public class QRCodeGenerator {
	
	public static void generate(String _code, int _width, int _height) {
		
		System.out.println("generate() invoked in QRCodeGenerator");
		
		/*
		try {
			System.out.println("generate() invoked in QRCodeGenerator");
			
			QRCode.from("Hello World");
			
			//File file = QRCode.from("Hello World").file();
			System.out.println("generation complete");
		} catch (Exception x) {
			x.printStackTrace();
		}
		*/
		
		
		String realPath = CUBean.getProperty("cu.realPath");
		String imagesFolder = CUBean.getProperty("cu.imagesFolder");
		String qrPath = realPath + imagesFolder + "qrcodes";
		
		CUBean.createDirectory(realPath, imagesFolder + "qrcodes");

	    URL url = null;
	    try { 
			url = new URL("https://chart.googleapis.com/chart?cht=qr&chl=" + URLEncoder.encode(_code, "UTF-8") + "&choe=UTF-8&chs=" + _width + "x" + _height);
	    } catch (MalformedURLException e) {
			System.out.println("ERROR: invalid URL " );
			return;
	    } catch (UnsupportedEncodingException e) {
			System.out.println("ERROR: invalid URL " );
			return;
	    }
		
		
		String line;
		String result = "";
		
		InputStream rd = null;
		try {
			
			File f = new File(qrPath + "\\out-image.png");
			
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			//rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			//rd = new InputStreamReader(conn.getInputStream());
			
			rd = conn.getInputStream();
			
			OutputStream out = new FileOutputStream(f);
			byte buf[]=new byte[1024];
			int len;
			while((len=rd.read(buf)) > 0) {
				System.out.println("len >" + len);
				out.write(buf,0,len);
			}
			out.close();
			rd.close();
			System.out.println("\nFile is created...................................");
			
			/*
			while ((line = rd.readLine()) != null) {
			   result += line;
			}
			*/
			
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
			rd.close();
			} catch (Exception x) {
				
			}
		}
		
		System.out.println("result >" + result);
		
	}
	
	
}
