/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.tasks;


import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.online.beans.*;

import java.net.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;


import javax.servlet.http.HttpSession;

import com.intuit.qbmsconnector.*;
import com.intuit.qbmsconnector.jaxb.JaxbQbmsConnector;
import com.intuit.qbmsconnector.request.*;
import com.intuit.qbmsconnector.response.*;
import com.valeo.authorize.net.GatewayResponseParser;

import com.valeo.qbms.data.QBMSCreditCardResponse;

public class
SettleMerchantAccountTask
    extends TimerTask
{

    //  Initialization

	boolean is_production = false;

    private static String urlString = null;
    private static String xmlFileLoc = null;
    private static String strLog = new String();
    private static String logfile = null;

    // INSTANCE VARIABLES

	private CashOut cash_out;

	private HttpSession session;
	private StringBuffer status;

    // CONSTRUCTORS

    public
    SettleMerchantAccountTask(HttpSession _session, CashOut _cash_out)
    {
		if (CUBean.getProperty("qbms.isProduction") != null)
			is_production = CUBean.getProperty("qbms.isProduction").equals("true");

		if (is_production)
			urlString = "https://merchantaccount.quickbooks.com/j/AppGateway";
		else
			urlString = "https://merchantaccount.ptc.quickbooks.com/j/AppGateway";

		// urls for diagnostics...
		//https://webmerchantaccount.ptc.quickbooks.com/j/diag/http
		//https://webmerchantaccount.quickbooks.com/j/diag/http

		session = _session;
		cash_out = _cash_out;
		status = (StringBuffer)session.getAttribute("settle_merchant_account_status");
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		try
		{
			if (cash_out.isMerchantAccountSettlementInProgress())
				throw new IllegalValueException("Merchant Account Settlement already in progress.");
			cash_out.setIsMerchantAccountSettlementInProgress(true);

			/*
			UKOnlinePersonBean client = null;
			BigDecimal amount = null;
			String card_number = null;

			String[] str_arr = info_string.split("\\|");
			for (int i = 0; i < str_arr.length; i++)
			{
				System.out.println(i + " >" + str_arr[i]);
				switch (i)
				{
					case 0: client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(str_arr[i])); break;
					case 1: amount = new BigDecimal(str_arr[i]); break;
					case 2: card_number = str_arr[i]; break;
				}
			}

			System.out.println("client >" + client.getLabel());
			System.out.println("amount >" + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			System.out.println("card_number >" + card_number);
			 *
			 */



			status.append("Connecting to Merchant Services|");

			/*

			Thread.sleep(5000); // just for testing

			status.append("Connection Successful|");

			Thread.sleep(5000); // just for testing

			status.append("Negotiating Settlement|");
			 *
			 */



			boolean use_authorize_dot_net = true;

			if (use_authorize_dot_net)
			{

				try
				{
					//URL post_url = new URL("https://cardpresent.authorize.net/gateway/transact.dll");
					URL post_url = new URL(CUBean.getProperty("authorize_net_post_url"));

					// the API Login ID and Transaction Key must be replaced with valid values


					Iterator itr = cash_out.getAllUncapturedResponses().iterator();
					while (itr.hasNext())
					{
						QBMSCreditCardResponse uncaptured_response = (QBMSCreditCardResponse)itr.next();
						
						String auth_code = uncaptured_response.getAuthorizationCode();
						System.out.println("auth_code >" + auth_code);
						if (!auth_code.equals("000000"))
						{
							HashMap post_values = new HashMap();

							post_values.put("x_cpversion", CUBean.getProperty("x_cpversion"));
							post_values.put("x_login", CUBean.getProperty("x_login"));
							post_values.put("x_tran_key", CUBean.getProperty("x_tran_key"));
							post_values.put("x_type", "PRIOR_AUTH_CAPTURE");
							post_values.put("x_market_type", CUBean.getProperty("x_market_type")); // may need info
							post_values.put("x_device_type", CUBean.getProperty("x_device_type")); // need info
							post_values.put("x_ref_trans_id", uncaptured_response.getCreditCardTransId());

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
							
							System.out.println("settle post_string >" + post_string);

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
							//parser.getErrorText()

							QBMSCreditCardResponse response = new QBMSCreditCardResponse();
							response.setClient(uncaptured_response.getClient());
							response.setResponse(parser);
							response.setResponseType(QBMSCreditCardResponse.CAPTURE_RESPONSE_TYPE);
							response.setResponseCode((short)response_code);
							response.setErrorCode((short)error_code);
							response.save();
							
							status.append("AuthCode " + auth_code + " response code " + response_code + " |");
						}
					}

					// also void all responses not associated with a tender

					itr = cash_out.getAllOrphanedUncapturedResponses().iterator();
					while (itr.hasNext())
					{
						QBMSCreditCardResponse orphaned_response = (QBMSCreditCardResponse)itr.next();
						
						HashMap post_values = new HashMap();
						post_values.put("x_cpversion", CUBean.getProperty("x_cpversion"));
						post_values.put("x_login", CUBean.getProperty("x_login"));
						post_values.put("x_tran_key", CUBean.getProperty("x_tran_key"));
						post_values.put("x_type", "VOID");
						post_values.put("x_market_type", CUBean.getProperty("x_market_type")); // may need info
						post_values.put("x_device_type", CUBean.getProperty("x_device_type")); // need info
						post_values.put("x_ref_trans_id", orphaned_response.getCreditCardTransId());

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

						QBMSCreditCardResponse response = new QBMSCreditCardResponse();
						response.setClient(orphaned_response.getClient());
						response.setResponse(parser);
						response.setResponseType(QBMSCreditCardResponse.VOID_TRANSACTION_RESPONSE_TYPE);
						if (response.getCreditCardTransId() == null || response.getCreditCardTransId().equals("0"))
							response.setCreditCardTransId(orphaned_response.getCreditCardTransId());
						response.setResponseCode((short)response_code);
						response.setErrorCode((short)error_code);
						response.save();
					}

					status.append("Settlement Complete.  Click next to continue.|");
					//session.removeAttribute("settle_merchant_account_status");
				}
				catch (Exception e)
				{
					status.append("Error: " + e.getMessage() + "|");
					//session.removeAttribute("settle_merchant_account_status");
					e.printStackTrace();
				}

			}
			else
			{

				try
				{
					// construct QBMS XML or something

					QbmsConfiguration conf = null;
					if (is_production)
						conf = new QbmsConfiguration("/hosted_qbmsconnector.properties");
					else
					{
						//conf = new QbmsConfiguration("/hosted_qbmsconnector_test.properties");
						conf = new QbmsConfiguration("/hosted_qbmsconnector.properties");
					}

					QbmsConnector conn = new JaxbQbmsConnector(conf);

					String connectionTicket = "TGT-169-OrBY_DvllGaEPGwz2PJk6w";
						// ummm...  I need a better way to specify this, obviously...

					// get the auth responses associated with this cash out

					Iterator itr = cash_out.getAllUncapturedResponses().iterator();
					while (itr.hasNext())
					{
						QBMSCreditCardResponse uncaptured_response = (QBMSCreditCardResponse)itr.next();

						CaptureRequest cr = new CaptureRequest();
						if (is_production)
						{
							cr.setCreditCardTransID(uncaptured_response.getCreditCardTransId());
							//cr.setRequestId(strLog); // is this needed???
							//cr.setTransRequestId(strLog); // is this needed???
						}
						else
						{
							cr.setCreditCardTransID(uncaptured_response.getCreditCardTransId());
							//cr.setRequestId(strLog); // is this needed???
							//cr.setTransRequestId(strLog); // is this needed???
						}
						cr.setAmount(Double.valueOf(uncaptured_response.getTender().getAmountBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toString())); // not sure if this is needed or not...

						CaptureResponse r = conn.capture(connectionTicket, cr);
						System.out.println("CaptureResponse >" + r);

						// record the response results

						QBMSCreditCardResponse response = new QBMSCreditCardResponse();
						response.setClient(uncaptured_response.getClient());
						response.setResponse(r);
						response.save();
					}

					// also void all responses not associated with a tender

					itr = cash_out.getAllOrphanedUncapturedResponses().iterator();
					while (itr.hasNext())
					{
						QBMSCreditCardResponse orphaned_response = (QBMSCreditCardResponse)itr.next();

						VoidTransactionRequest vr = new VoidTransactionRequest();
						vr.setCreditCardTransId(strLog);
						if (is_production)
						{
							vr.setCreditCardTransId(orphaned_response.getCreditCardTransId());
							//cr.setRequestId(strLog); // is this needed???
							//cr.setTransRequestId(strLog); // is this needed???
						}
						else
						{
							vr.setCreditCardTransId(orphaned_response.getCreditCardTransId());
							//cr.setRequestId(strLog); // is this needed???
							//cr.setTransRequestId(strLog); // is this needed???
						}

						VoidTransactionResponse r = conn.voidTransaction(connectionTicket, vr);
						System.out.println("VoidTransactionResponse >" + r);

						// record the response results

						QBMSCreditCardResponse response = new QBMSCreditCardResponse();
						response.setClient(orphaned_response.getClient());
						response.setResponse(r);
						response.save();
					}


					status.append("Settlement Complete.  Click next to continue.|");
					//session.removeAttribute("settle_merchant_account_status");
				}
				catch (QbmsOperationException x)
				{
					status.append("Error: " + x.getMessage() + "|");
					//session.removeAttribute("settle_merchant_account_status");
				}
				catch (Exception e)
				{
					status.append("Error: " + e.getMessage() + "|");
					//session.removeAttribute("settle_merchant_account_status");
					e.printStackTrace();
				}

			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		finally
		{
			cash_out.setIsMerchantAccountSettlementInProgress(false);
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
