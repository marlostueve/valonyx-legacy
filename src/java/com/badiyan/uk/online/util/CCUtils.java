/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.util;

/**
 *
 * @author marlo
 */
public class CCUtils {

  public static final int INVALID          = -1;
  public static final int VISA             = 1;
  public static final int MASTERCARD       = 2;
  public static final int DISCOVER		   = 3;
  public static final int AMERICAN_EXPRESS = 4;
  public static final int EN_ROUTE         = 5;
  public static final int DINERS_CLUB      = 6;

  private static final String [] cardNames =
      {   "Visa" ,
          "Mastercard",
          "Discover",
          "American Express",
          "En Route",
          "Diner's Club/Carte Blanche",
      };

  /**
   * Valid a Credit Card number
   */
  public static boolean validCC(String number)
    throws Exception {
    int CardID;
    if ( (CardID = getCardID(number)) != -1)
        return validCCNumber(number);
    return false;
    }
  

  /**
   * Get the Card type
   * returns the credit card type
   *      INVALID          = -1;
   *      VISA             = 0;
   *      MASTERCARD       = 1;
   *      DISCOVER	       = 2;
   *      AMERICAN_EXPRESS = 3;
   *      EN_ROUTE         = 4;
   *      DINERS_CLUB      = 5;
   */
  public static int getCardID(String number) {

    int valid = INVALID;

	if (number != null && number.length() > 3)
	{
		String digit1 = number.substring(0,1);
		String digit2 = number.substring(0,2);
		String digit3 = number.substring(0,3);
		String digit4 = number.substring(0,4);

		if (isNumber(number)) {
		  /* ----
		  ** VISA  prefix=4
		  ** ----  length=13 or 16  (can be 15 too!?! maybe)
		  */
		  if (digit1.equals("4"))  {
			if (number.length() == 13 || number.length() == 16)
			   valid = VISA;
			}
		  /* ----------
		  ** MASTERCARD  prefix= 51 ... 55
		  ** ----------  length= 16
		  */
		  else if (digit2.compareTo("51")>=0 && digit2.compareTo("55")<=0) {
			if (number.length() == 16)
			   valid = MASTERCARD;
			}
		  /* ----
		  ** DISCOVER card prefix = 60
		  ** --------      length = 16
		  */
		  else if (digit2.equals("60") || digit2.equals("66")) {
			if (number.length() == 16)
			   valid = DISCOVER;
			}
		  /* ----
		  ** AMEX  prefix=34 or 37
		  ** ----  length=15
		  */
		  else if (digit2.equals("34") || digit2.equals("37") || digit4.equals("2149")) {
			if (number.length() == 15)
			   valid = AMERICAN_EXPRESS;
			}
		  /* -----
		  ** ENROU prefix=2014 or 2149
		  ** ----- length=15
		  */
		  else if (digit4.equals("1800") || digit4.equals("2014")) {
			 if (number.length() == 15)
				valid = EN_ROUTE;
			 }
		  /* -----
		  ** DCLUB prefix=300 ... 305 or 36 or 38
		  ** ----- length=14
		  */
		  else if (digit2.equals("36") ||
			(digit3.compareTo("300")>=0 && digit3.compareTo("305")<=0)) {
			if (number.length() == 14)
			   valid = DINERS_CLUB;
			   }
		  }
	  }

      return valid;

    }

  public static boolean isNumber(String n) {
    try  {
      double d = Double.valueOf(n).doubleValue();
      return true;
      }
    catch (NumberFormatException e) {
      return false;
      }
    }

  public static String getCardName(int id) {
    return (id > 0 && id < cardNames.length ? cardNames[id - 1] : "");
    }

  public static boolean validCCNumber(String n) {
    try {
      /*
      ** known as the LUHN Formula (mod10)
      */
      int j = n.length();

      String [] s1 = new String[j];
      for (int i=0; i < n.length(); i++) s1[i] = "" + n.charAt(i);

      int checksum = 0;

      for (int i=s1.length-1; i >= 0; i-= 2) {
        int k = 0;

        if (i > 0) {
           k = Integer.valueOf(s1[i-1]).intValue() * 2;
           if (k > 9) {
              String s = "" + k;
              k = Integer.valueOf(s.substring(0,1)).intValue() +
                  Integer.valueOf(s.substring(1)).intValue();
              }
              checksum += Integer.valueOf(s1[i]).intValue() + k;
           }
           else
              checksum += Integer.valueOf(s1[0]).intValue();
        }
      return ((checksum % 10) == 0);
      }
    catch (Exception e) {
      e.printStackTrace();
      return false;
      }
    }

  /*
  ** For testing purpose
  **
  **   java CCUtils [credit card number] or java CCUtils
  **
  */

  /*
  public static void main(String args[]) throws Exception {
    String aCard = "";

    if (args.length > 0)
      aCard = args[0];
    else {
      BufferedReader input =
        new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Card number : ");
      aCard = input.readLine();
      }
    if (getCardID(aCard) > -1) {
       System.out.println("This card is supported.");
       System.out.println("This a " + getCardName(getCardID(aCard)));
       System.out.println
         ("The card number " + aCard + " is "
           + (validCC(aCard)?" good.":" bad."));
       }
    else
       System.out.println("This card is invalid or unsupported!");
    }
   */

	public static String[] parseSwipe(String _swipe_string) {

		System.out.println("_swipe_string >" + _swipe_string);

		//ccField = document.getElementById('ccNum');
		//expirField = document.getElementById('ccExpir');
		//cvvField = document.getElementById('ccCvv');
		//fname = document.getElementById('fname');
		//lname = document.getElementById('lname');

		String[] ret_arr = new String[7];

		if (CCUtils.getCardID(_swipe_string) == CCUtils.INVALID)
		{
			System.out.println("try to mine swipe data");
			// try to mine swipe data
			// %B4323747110844519^STUEVE/CHRISTINE A^1301101000004423101 00822000000?;4323747110844519=13011014423101822?

			String cc_number = "";
			String full_name = "";
			String first_name = "";
			String last_name = "";
			String exp_year = "";
			String exp_month = "";

			String track_1 = _swipe_string.substring(0, _swipe_string.indexOf(';'));
			String track_2 = _swipe_string.substring(_swipe_string.indexOf(';'));
			System.out.println("Track 1 >" + track_1);
			System.out.println("Track 2 >" + track_2);


			//String[] tracks = _swipe_string.substring("\\?");


			String[] track_1_parts = track_1.split("\\^");

			cc_number = track_1_parts[0].substring(2);
			System.out.println("cardNumber >" + cc_number);

			full_name = track_1_parts[1];
			System.out.println("name >" + full_name);
			if (full_name.indexOf('/') > -1)
			{
				last_name = full_name.substring(0, full_name.indexOf('/'));
				System.out.println("last_name >" + last_name);
				first_name = full_name.substring(full_name.indexOf('/') + 1);
				System.out.println("first_name >" + first_name);
			}
			System.out.println("exp >" + track_1_parts[2]);

			exp_year = track_1_parts[2].substring(0, 2);
			System.out.println("YY >" + exp_year);
			exp_month = track_1_parts[2].substring(2, 4);
			System.out.println("MM >" + exp_month);

			ret_arr[0] = cc_number;
			ret_arr[1] = full_name;
			ret_arr[2] = first_name;
			ret_arr[3] = last_name;
			ret_arr[4] = exp_year;
			ret_arr[5] = exp_month;
			ret_arr[6] = track_2;
		}
		else
		{
			// a valid credit card entry was found.  manual card entry (rather than a swipe) apparently
			System.out.println("a valid credit card entry was found.  manual card entry (rather than a swipe) apparently");

		}

		return ret_arr;
	}
	
	public static boolean
	isVisa(String _card_type)
	{
		if (_card_type == null)
			return false;
			
		return _card_type.toLowerCase().equals("visa");
	}
	
	public static boolean
	isMasterCard(String _card_type)
	{
		if (_card_type == null)
			return false;
			
		return (_card_type.toLowerCase().equals("mastercard") || _card_type.toLowerCase().equals("master card"));
	}
	
	public static boolean
	isDiscover(String _card_type)
	{
		if (_card_type == null)
			return false;
			
		return _card_type.toLowerCase().equals("discover");
	}
	
	public static boolean
	isAmericanExpress(String _card_type)
	{
		if (_card_type == null)
			return false;
			
		return (_card_type.toLowerCase().equals("american express") || _card_type.toLowerCase().equals("americanexpress") || _card_type.toLowerCase().equals("amex"));
	}
}
