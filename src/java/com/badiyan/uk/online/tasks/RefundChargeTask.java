/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.tasks;


import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.online.beans.QuickBooksSettings;
import com.badiyan.uk.online.beans.UKOnlineCompanyBean;

import java.net.*;
import java.io.*;
import java.util.*;


import javax.servlet.http.HttpSession;

import com.valeo.authorize.net.GatewayResponseParser;

import com.valeo.qbms.data.QBMSCreditCardResponse;
import com.valeo.qbpos.data.TenderRet;
import java.math.BigDecimal;
import org.apache.torque.TorqueException;

public class
RefundChargeTask
    extends TimerTask
{

    //  Initialization

	//boolean is_production = false;

    private static String urlString = null;
    private static String strLog = new String();
    private static String logfile = null;

    // INSTANCE VARIABLES

	private HttpSession session;
	//private StringBuffer status;

	private QBMSCreditCardResponse charge_to_refund = null;
	private TenderRet tender_to_refund = null;
	
	private UKOnlineCompanyBean company = null;

    // CONSTRUCTORS

    public
    RefundChargeTask(UKOnlineCompanyBean _company, HttpSession _session, QBMSCreditCardResponse _charge_to_refund)
    {
		//if (CUBean.getProperty("qbms.isProduction") != null)
		//	is_production = CUBean.getProperty("qbms.isProduction").equals("true");

		charge_to_refund = _charge_to_refund;

		session = _session;
		//status = (StringBuffer)session.getAttribute("refund_status");
		
		company = _company;
    }

    public
    RefundChargeTask(UKOnlineCompanyBean _company, HttpSession _session, TenderRet _tender_to_refund)
		throws TorqueException, ObjectNotFoundException
    {
		//if (CUBean.getProperty("qbms.isProduction") != null)
		//	is_production = CUBean.getProperty("qbms.isProduction").equals("true");

		tender_to_refund = _tender_to_refund;
		charge_to_refund = QBMSCreditCardResponse.getResponse(tender_to_refund);

		session = _session;
		//status = (StringBuffer)session.getAttribute("refund_status");
		
		company = _company;
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in RefundChargeTask");

		try
		{

			//status.append("Connecting to Merchant Services|");

			try
			{
				//URL post_url = new URL("https://cardpresent.authorize.net/gateway/transact.dll");
				URL post_url = new URL(CUBean.getProperty("authorize_net_post_url"));

				// also refund all responses not associated with a tender
				
				QuickBooksSettings qb_settings = company.getQuickBooksSettings();

				HashMap post_values = new HashMap();
				
				
				post_values.put("x_ref_trans_id", charge_to_refund.getCreditCardTransId());
				post_values.put("x_cpversion", CUBean.getProperty("x_cpversion"));
				//post_values.put("x_login", CUBean.getProperty("x_login"));
				//post_values.put("x_tran_key", CUBean.getProperty("x_tran_key"));
						
				if (qb_settings.getXLoginString().isEmpty())
					post_values.put("x_login", CUBean.getProperty("x_login"));
				else
					post_values.put("x_login", qb_settings.getXLoginString());

				if (qb_settings.getXTranKeyString().isEmpty())
					post_values.put("x_tran_key", CUBean.getProperty("x_tran_key"));
				else
					post_values.put("x_tran_key", qb_settings.getXTranKeyString());
						
				post_values.put("x_type", "CREDIT");
				post_values.put("x_market_type", CUBean.getProperty("x_market_type")); // may need info
				post_values.put("x_device_type", CUBean.getProperty("x_device_type")); // need info
				if (tender_to_refund != null)
				{
					if (tender_to_refund.isVoidedOrRefunded())
						throw new IllegalValueException("Tender already voided or refunded.");
					
					post_values.put("x_amount", tender_to_refund.getAmountBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				}
				else
					post_values.put("x_amount", charge_to_refund.getChargeAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				post_values.put("x_card_num", charge_to_refund.getMerchantAccountNumber());
				

				StringBuffer post_string = new StringBuffer();
				Iterator keys = post_values.keySet().iterator();
				while (keys.hasNext())
				{
					String key = (String)keys.next();
					String value = (String)post_values.get(key);
					key = URLEncoder.encode(key, "UTF-8");
					value = URLEncoder.encode(value, "UTF-8");
					post_string.append(key + "=" + value + "&");
				}

				// Open a URLConnection to the specified post url
				URLConnection connection = post_url.openConnection();
				connection.setDoOutput(true);
				connection.setUseCaches(false);

				// this line is not necessarily required but fixes a bug with some servers
				connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

				// submit the post_string and close the connection
				DataOutputStream requestObject = new DataOutputStream( connection.getOutputStream() );
				requestObject.write(post_string.toString().getBytes());
				requestObject.flush();
				requestObject.close();

				// process and read the gateway response
				BufferedReader rawResponse = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String responseData = rawResponse.readLine();
				rawResponse.close();	                     // no more data

				// split the response into an array
				String [] responses = responseData.split("\\|");

				// The results are output to the screen in the form of an html numbered list.
				//out.println("<OL>");
				for(Iterator iter=Arrays.asList(responses).iterator(); iter.hasNext();) {
					//out.println("<LI>" + iter.next() + "&nbsp;</LI>");
					System.out.println(iter.next());
				}

				GatewayResponseParser parser = new GatewayResponseParser();
				parser.parse(responseData);

				int response_code = parser.getResponseCode();
				int error_code = parser.getErrorCode();

				System.out.println("refund response_code >" + response_code);
				System.out.println("refund error_code >" + error_code);

				if (response_code == 1)
				{
					QBMSCreditCardResponse response = new QBMSCreditCardResponse();
					response.setClient(charge_to_refund.getClient());
					response.setResponse(parser);
					response.setResponseType(QBMSCreditCardResponse.REFUND_RESPONSE_TYPE);
					if (response.getCreditCardTransId() == null || response.getCreditCardTransId().equals("0"))
						response.setCreditCardTransId(charge_to_refund.getCreditCardTransId());
					response.setResponseCode((short)response_code);
					response.setErrorCode((short)error_code);
					response.save();


					if (tender_to_refund != null)
					{
						// what do I need to do in way of affecting orders that the tender was connected to...
						// should I delete the tender? no, don't think so.  but I think I need to mark it in some way
						
						tender_to_refund.setVoidedOrRefunded();
							// this will also reverse tender amounts applied to mapped orders
						tender_to_refund.save();
					}
					
				}
				else if (response_code == 2)
				{
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Refund Error: " + parser.getErrorText(), "response_code == 2 - declined");
					
					// declined

					//status.append("Error: " + parser.getErrorText() + "|");
				}
				else if (response_code == 3)
				{
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Refund Error: " + parser.getErrorText(), "response_code == 3 - error");
					
					// error

					//status.append("Error: " + parser.getErrorText() + "|");
				}
				else if (response_code == 4)
				{
					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "Refund Error: " + parser.getErrorText(), "response_code == 4 - being held for review?!?!");
					
					// being held for review?!?!

					//status.append("Error: " + parser.getErrorText() + "|");
				}





				//status.append("Settlement Complete.  Click next to continue.|");
				//session.removeAttribute("settle_merchant_account_status");
			}
			catch (Exception e)
			{
				//status.append("Error: " + e.getMessage() + "|");
				//session.removeAttribute("settle_merchant_account_status");
				e.printStackTrace();
			}



		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		finally
		{

		}
    }

    /*
     * Send the XML request to QBMS
     * @param strURL is the posting URL
     * @param requestFileLoc is the location of the xml file
     *
     */
    public void send_request(String strURL, String _xmlReq){
        try{

			/*
            FileInputStream xmlFile = new FileInputStream(requestFileLoc);
            String xmlReq = readFileIntoString(xmlFile);
			 */

			String xmlReq = _xmlReq;

            //xmlReq = getTransRequestID(xmlReq);

            writeLog("Posting to " + urlString);
            writeLog("\n--- Request Body ---");
            writeLog(xmlReq + "\n");

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-qbmsxml");
            Writer out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

            // Send the request (request body)
            out.write(xmlReq);
            out.flush();

            conn.connect();
            writeLog("Request sent....");
            writeLog("Waiting for response....");
            writeLog("\n--- Response Body ---");
            printResponseBody(conn);

        }
        catch (Exception E)
        {
            E.printStackTrace();
        }

    }

    /*
     * Generate TransRequestID using date
     */
    private static String getTransRequestID(String xml)
    {
        Date d= new Date();
        String strTemp = d.toString();
        strTemp = strTemp.replaceAll(":","_");
        strTemp = strTemp.replaceAll(" ","_");
        System.out.println(strTemp);
        return xml.substring(0,xml.indexOf("<TransRequestID>") + 17 ) + strTemp + xml.substring(xml.indexOf("</TransRequestID>"));
    }

    /*
     * Write debug message
     */
    public static void writeLog(String message){
        System.out.println(message);
        strLog += message + "\r\n";
        return;
    }

    /*
     * Write debug message to physical file
     */
    public static void printLog(){
        try{
            FileOutputStream out = new FileOutputStream(logfile);
            for (int i=0; i < strLog.length(); ++i){
                out.write(strLog.charAt(i));
            }
            out.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return;
    }

    /*
     * Method to read file into a string
     */
    public String readFileIntoString (FileInputStream f) {

        String result = "";

        try {
            byte[] b = new byte[f.available ()];
            f.read(b);
            result = new String (b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * Print the response Body
     */
    public static void printResponseBody (HttpURLConnection c) {

        try {
            // Print the content body
            writeLog("\n");
            InputStream content = (InputStream) c.getContent();
            BufferedReader in   = new BufferedReader (new InputStreamReader (content));
            String line;
            while ((line = in.readLine()) != null) {
                writeLog (line);
            }
            if (logfile != null) {
                printLog();
            }
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }
}
