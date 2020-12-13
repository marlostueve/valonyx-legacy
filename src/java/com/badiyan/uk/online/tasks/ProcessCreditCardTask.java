/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.tasks;


import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.util.CCUtils;

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
import java.text.SimpleDateFormat;

public class
ProcessCreditCardTask
    extends TimerTask
{

    //  Initialization

	private static boolean is_production = false;

    private static String urlString = null;
    private static String xmlFileLoc = null;
    private static String strLog = new String();
    private static String logfile = null;
	
    // INSTANCE VARIABLES

	private UKOnlineCompanyBean company;
	private HttpSession session;
	private StringBuffer status;
	private String info_string;

    // CONSTRUCTORS

    public
    ProcessCreditCardTask(UKOnlineCompanyBean _company, HttpSession _session, String _info_string)
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
		info_string = _info_string;
		status = (StringBuffer)session.getAttribute("credit_card_status");
		company = _company;
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in ProcessCreditCardTask ");

		try
		{
			UKOnlinePersonBean client = null;
			BigDecimal amount = null;
			String swipe_str = null;

			boolean card_swiped = false;
			boolean card_present = false;

			String cc_number = "";
			String full_name = "";
			String first_name = "";
			String last_name = "";
			String exp_year = "";
			String exp_month = "";
			String track_2 = "";
			String card_type = "";
			String zip_code = "";
			String street_address = "";
			String card_verification_code = "";
			String is_refund = "";

			String[] str_arr = info_string.split("\\|");
			for (int i = 0; i < str_arr.length; i++)
			{
				System.out.println(i + " >" + str_arr[i]);
				switch (i)
				{
					case 0: client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(str_arr[i])); break;
					case 1: amount = new BigDecimal(str_arr[i]); break;
					case 2: swipe_str = str_arr[i]; break;
					case 3: exp_month = str_arr[i]; break;
					case 4: exp_year = str_arr[i]; break;
					case 5: zip_code = str_arr[i]; break;
					case 6: street_address = str_arr[i]; break;
					case 7: card_verification_code = str_arr[i]; break;
					case 8: is_refund = str_arr[i]; break;
				}
			}

			System.out.println("client >" + client.getLabel());
			System.out.println("amount >" + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			System.out.println("swipe_str >" + swipe_str);
			System.out.println("exp_month >" + exp_month);
			System.out.println("exp_year >" + exp_year);
			System.out.println("zip_code >" + zip_code);
			System.out.println("street_address >" + street_address);
			System.out.println("card_verification_code >" + card_verification_code);
			System.out.println("is_refund >" + is_refund);

			if (CCUtils.getCardID(swipe_str) == CCUtils.INVALID)
			{
				// swipe data that isn't a straight card number, try to grab track 1 and track 2 data from the swipe string.

				try
				{
					String[] parse_arr = CCUtils.parseSwipe(swipe_str);

					cc_number = parse_arr[0];
					full_name = parse_arr[1];
					first_name = parse_arr[2];
					last_name = parse_arr[3];
					exp_year = parse_arr[4];
					exp_month = parse_arr[5];
					track_2 = parse_arr[6];
					
					if (track_2.startsWith(";"))
						track_2 = track_2.substring(1);
					
					if (track_2.endsWith("?"))
						track_2 = track_2.substring(0, track_2.length() - 1);
					
					System.out.println("xtrack_2 >" + track_2);

					if (last_name.length() > 0 && first_name.length() > 0)
						full_name = first_name + " " + last_name;

					card_swiped = true;
				}
				catch (Exception x)
				{
					throw new IllegalValueException("Unable to process card number.");
				}
			}
			else
			{
				// the swip data represents a valid card number...

				cc_number = swipe_str;
				first_name = client.getFirstNameString();
				last_name = client.getLastNameString();
				full_name = first_name + " " + last_name;
			}

			card_type = CCUtils.getCardName(CCUtils.getCardID(cc_number));


			status.append("Swipe Detected|");

			/*
			Thread.sleep(5000); // just for testing

			status.append("Connecting to Merchant Services|");

			Thread.sleep(5000); // just for testing

			status.append("Connection Successful|");

			Thread.sleep(5000); // just for testing

			status.append("Negotiating Authentication|");
			 */

			boolean use_authorize_dot_net = true;


			if (use_authorize_dot_net)
			{
				try
				{
					//https://test.authorize.net/gateway/transact.dll
					//https://cardpresent.authorize.net/gateway/transact.dll
					//https://secure.authorize.net/gateway/transact.dll
					
					//URL post_url = new URL("https://cardpresent.authorize.net/gateway/transact.dll");
					
					URL post_url = new URL(is_production ? CUBean.getProperty("authorize_net_post_url") : CUBean.getProperty("authorize_net_test_post_url"));
					System.out.println("post_url >" + post_url.toString());

					HashMap post_values = new HashMap();

					// the API Login ID and Transaction Key must be replaced with valid values

					boolean is_return = false;
					
					QuickBooksSettings qb_settings = company.getQuickBooksSettings();

					if ((is_refund != null) && is_refund.equals("true"))
					{
						is_return = true;

						// I need to find a prev

						QBMSCreditCardResponse refund_response = QBMSCreditCardResponse.getResponseToRefund(client, amount);

						post_values.put("x_ref_trans_id", refund_response.getCreditCardTransId());
						post_values.put("x_cpversion", CUBean.getProperty("x_cpversion"));
						
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
						post_values.put("x_amount", amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						if (track_2.equals(""))
						{
							post_values.put("x_card_num", cc_number);
							if (exp_year.length() > 0 && exp_month.length() > 0)
							{
								Calendar exp_date = Calendar.getInstance();
								exp_date.set(Calendar.YEAR, Integer.parseInt(exp_year));
								exp_date.set(Calendar.MONTH, Integer.parseInt(exp_month) - 1);
								SimpleDateFormat format = new SimpleDateFormat("MMyy");
								post_values.put("x_exp_date", format.format(exp_date.getTime()));
							}
						}
						else
							post_values.put("x_track2", track_2);
					}
					else
					{

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
						
						post_values.put("x_type", "AUTH_ONLY");
						post_values.put("x_market_type", CUBean.getProperty("x_market_type")); // may need info
						post_values.put("x_device_type", CUBean.getProperty("x_device_type")); // need info
						post_values.put("x_amount", amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
						if (track_2.equals(""))
						{
							post_values.put("x_card_num", cc_number);
							if (exp_year.length() > 0 && exp_month.length() > 0)
							{
								Calendar exp_date = Calendar.getInstance();
								exp_date.set(Calendar.YEAR, Integer.parseInt(exp_year));
								exp_date.set(Calendar.MONTH, Integer.parseInt(exp_month) - 1);
								SimpleDateFormat format = new SimpleDateFormat("MMyy");
								post_values.put("x_exp_date", format.format(exp_date.getTime()));
							}
						}
						else
							post_values.put("x_track2", track_2);
					}
					

					//post_values.put("x_card_num", "4222222222222");
					//post_values.put("x_amount", "201.00");



					/*
					post_values.put("x_version", "3.1");
					post_values.put("x_delim_data", "TRUE");
					post_values.put("x_delim_char", "|");
					post_values.put("x_relay_response", "FALSE");


					post_values.put("x_method", "CC");
					post_values.put("x_card_num", "4111111111111111");
					post_values.put("x_exp_date", "0115");


					post_values.put("x_description", "Sample Transaction");

					post_values.put("x_first_name", "John");
					post_values.put("x_last_name", "Doe");
					post_values.put("x_address", "1234 Street");
					post_values.put("x_state", "WA");
					post_values.put("x_zip", "98004");
					 *
					 */



					// Additional fields can be added here as outlined in the AIM integration
					// guide at: http://developer.authorize.net

					// This section takes the input fields and converts them to the proper format
					// for an http post.  For example: "x_login=username&x_tran_key=a1B2c3D4"
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

					//System.out.println("post_string >" + post_string.toString());
					//System.out.println("bytes >" + post_string.toString().getBytes());

					// The following section provides an example of how to add line item details to
					// the post string.  Because line items may consist of multiple values with the
					// same key/name, they cannot be simply added into the above array.
					//
					// This section is commented out by default.
					/*
					String[] line_items = {
						"item1<|>golf balls<|><|>2<|>18.95<|>Y",
						"item2<|>golf bag<|>Wilson golf carry bag, red<|>1<|>39.99<|>Y",
						"item3<|>book<|>Golf for Dummies<|>1<|>21.99<|>Y"};

					for (int i = 0; i < line_items.length; i++) {
					  String value = line_items[i];
					  post_string.append("&x_line_item=" + URLEncoder.encode(value));
					}
					*/
					
					System.out.println("post_string >" + post_string);

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
					String line;
					String responseData = rawResponse.readLine();
					rawResponse.close();	                     // no more data

					// split the response into an array
					String [] responses = responseData.split("\\|");

					// The results are output to the screen in the form of an html numbered list.
					//out.println("<OL>");
					StringBuffer buf = new StringBuffer();
					try
					{
						for(Iterator iter=Arrays.asList(responses).iterator(); iter.hasNext();) {
							//out.println("<LI>" + iter.next() + "&nbsp;</LI>");

								//String stuff = (String)iter.next();
								//System.out.println(stuff);

								buf.append(iter.next());
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
					}

					CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", is_return ? "CC Response Data RETURN" : "CC Response Data", buf.toString());

					GatewayResponseParser parser = new GatewayResponseParser();
					parser.parse(responseData);



					int response_code = parser.getResponseCode();
					int error_code = parser.getErrorCode();
					if (response_code == 1)
					{
						System.out.println("card approved");
						
						// approved

						QBMSCreditCardResponse response = new QBMSCreditCardResponse();
						response.setClient(client);
						response.setResponse(parser);
						response.setResponseType(is_return ? QBMSCreditCardResponse.REFUND_RESPONSE_TYPE : QBMSCreditCardResponse.AUTHORIZE_RESPONSE_TYPE);
						response.setCVCCode(card_verification_code);
						if (response.getCardType() == null || response.getCardType().equals(""))
						{
							System.out.println("response.getCardType() >" + response.getCardType());
							System.out.println("card_type >" + card_type);
							response.setCardType(card_type);  // shouldn't this be set in setResponse(parser) above???
						}
						response.setChargeAmount(amount);
						response.setExpMM(exp_month);
						response.setExpYY(exp_year);
						response.setStreetAddress(street_address);
						response.setZipCode(zip_code);
						response.setResponseCode((short)response_code);
						response.setErrorCode((short)error_code);
						response.save();

						/*
						Vector<QBMSCreditCardResponse> responses = (Vector<QBMSCreditCardResponse>)session.getAttribute("charge_response");
						if (responses == null)
							responses = new Vector();
						responses.addElement(response);
						 */
						session.setAttribute("charge_response", response);

						status.append("Card Approved|");
						session.removeAttribute("credit_card_status");

					}
					else if (response_code == 2)
					{
						// declined

						status.append("Error: " + parser.getErrorText() + "|");
					}
					else if (response_code == 3)
					{
						// error

						status.append("Error: " + parser.getErrorText() + "|");
					}
					else if (response_code == 4)
					{
						// being held for review?!?!

						status.append("Error: " + parser.getErrorText() + "|");
					}


					//out.println("</OL>");
				}
				catch (Exception e)
				{
					status.append("Error: " + e.getMessage() + "|");
					//session.removeAttribute("credit_card_status");
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




					/*
					ChargeRequest c = new ChargeRequest();
					c.setCreditCardNumber("4111111111111111");
					c.setNameOnCard("John Doe");
					c.setExpirationMonth(12);
					c.setExpirationYear(2010);
					c.setAmount(Double.valueOf("130.00"));
					*/

					//ChargeRequest c = new ChargeRequest();
					AuthorizeRequest ar = new AuthorizeRequest();
					System.out.println("is_production >" + is_production);
					if (is_production)
					{
						ar.setCreditCardNumber(cc_number);
						ar.setNameOnCard(full_name);
						if (card_swiped)
						{
							ar.setTrack2Data(track_2);
							//ar.setIsCardPresent(true); // used when the card is presented to the merchant, but a swipe could not be performed for whatever reason.
						}
						if (exp_month != null && exp_month.length() > 0)
							ar.setExpirationMonth(Integer.parseInt(exp_month));
						if (exp_year != null && exp_year.length() > 0)
							ar.setExpirationYear(Integer.parseInt("20" + exp_year));
						if (card_verification_code != null && card_verification_code.length() > 0)
							ar.setCardSecurityCode(card_verification_code);
						if (street_address != null && street_address.length() > 0)
							ar.setCreditCardAddress(street_address);
						if (zip_code != null && zip_code.length() > 0)
							ar.setCreditCardPostalCode(zip_code);
					}
					else
					{
						ar.setCreditCardNumber("4111111111111111");
						//ar.setTrack2Data(";372449635312118=1202101123456789?");
						ar.setNameOnCard("John Doe");
						//ar.setIsCardPresent(false);
						//ar.set
						ar.setExpirationMonth(12);
						ar.setExpirationYear(2010);
					}
					ar.setAmount(Double.valueOf(amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString()));

					//ChargeResponse resp = conn.charge(connectionTicket, c);

					AuthorizeResponse r = conn.authorize(connectionTicket, ar);
					System.out.println("AuthorizeResponse >" + r);

					// record the response results

					QBMSCreditCardResponse response = new QBMSCreditCardResponse();
					response.setClient(client);
					response.setResponse(r);
					response.setCVCCode(card_verification_code);
					response.setCardType(card_type);
					response.setChargeAmount(amount);
					response.setExpMM(exp_month);
					response.setExpYY(exp_year);
					response.setStreetAddress(street_address);
					response.setZipCode(zip_code);
					response.save();

					/*
					Vector<QBMSCreditCardResponse> responses = (Vector<QBMSCreditCardResponse>)session.getAttribute("charge_response");
					if (responses == null)
						responses = new Vector();
					responses.addElement(response);
					 */
					session.setAttribute("charge_response", response);


					status.append("Card Approved|");

					//Thread.sleep(3000); // just for testing
					session.removeAttribute("credit_card_status");

				}
				catch (QbmsOperationException x)
				{
					status.append("Error: " + x.getMessage() + "|");
					//session.removeAttribute("credit_card_status");
				}
				catch (Exception e)
				{
					status.append("Error: " + e.getMessage() + "|");
					//session.removeAttribute("credit_card_status");
					e.printStackTrace();
				}

			}

		}
		catch (Exception x)
		{
			status.append("Error: " + x.getMessage() + "|");
			x.printStackTrace();
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
