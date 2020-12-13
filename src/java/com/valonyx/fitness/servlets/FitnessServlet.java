
package com.valonyx.fitness.servlets;

import com.badiyan.torque.FavoriteFood;
import com.badiyan.torque.FoodServingDb;
import com.badiyan.torque.PersonTrainingPlanMapping;
import com.badiyan.torque.UnusedGoogleSheetDb;
import com.badiyan.uk.beans.CUBean;
import com.badiyan.uk.beans.PersonBean;
import com.badiyan.uk.beans.RoleBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.exceptions.UniqueObjectNotFoundException;
import com.badiyan.uk.online.beans.PracticeAreaBean;
import com.badiyan.uk.online.beans.UKOnlinePersonBean;
import com.badiyan.uk.online.util.EncryptionUtils;
import com.badiyan.uk.online.util.PasswordGenerator;
import com.valonyx.fitness.beans.Food;
import com.valonyx.fitness.beans.MacroGoal;
import com.valonyx.fitness.beans.MacroGoalDaily;
import com.valonyx.fitness.beans.MacroGoalEntry;
import com.valonyx.fitness.beans.Move;
import com.valonyx.fitness.beans.TrainingPlan;
import com.valonyx.fitness.beans.TrainingPlanMove;
import com.valonyx.fitness.beans.TrainingPlanSet;
import com.valonyx.fitness.beans.TrainingPlanWorkout;
import com.valonyx.fitness.beans.WeightEntry;
import com.valonyx.fitness.beans.WeightGoal;
import com.valonyx.fitness.beans.WeightGoalType;
import com.valonyx.fitness.beans.WeightPreferences;
import com.valonyx.fitness.beans.Workout;
import com.valonyx.fitness.beans.WorkoutSet;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.torque.TorqueException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DuplicateSheetRequest;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;

import com.valonyx.tasks.UpdateGoogleSheetFromTrainingPlanTask;
import com.valonyx.tasks.UpdateTrainingPlanFromGoogleSheetTask;
import java.security.GeneralSecurityException;
import java.util.TimeZone;
import java.util.Timer;
import java.util.function.Consumer;
//import org.mortbay.log.Log;


class ContainerFactoryImpl implements ContainerFactory {

	@Override
	public Map createObjectContainer() {
		return new LinkedHashMap();
	}

	@Override
	public List creatArrayContainer() {
		return new LinkedList();
	}
	
}


/**
 *
 * @author marlo
 */
public class FitnessServlet extends HttpServlet {
	
	private static HashMap<String,UKOnlinePersonBean> auth_key_hash = new HashMap<String,UKOnlinePersonBean>(11);
	private static HashMap<String,Calendar> auth_key_selected_display_date = new HashMap<String,Calendar>(11);
	
	private static HashMap<String,Timer> timer_hash = new HashMap<String,Timer>(11);
	
	private static HashMap<String, UKOnlinePersonBean> sim_hash = null;
	
	private static HashMap<UKOnlinePersonBean, Date> last_set_completion_date_hash = null;
	
	private static Vector confirmed_emails = new Vector();

	protected void processRequest(HttpServletRequest _request, HttpServletResponse _response)
			throws ServletException, IOException {
		
		_response.setContentType("application/json;charset=UTF-8");
		_response.setHeader("Access-Control-Allow-Origin", "*");
		
		PrintWriter writer = _response.getWriter();
		
		try {

			String command = _request.getParameter("command");
			String key = _request.getParameter("key");
			String parameter = _request.getParameter("parameter");
			String arg1 = _request.getParameter("arg1");
			String arg2 = _request.getParameter("arg2");
			String arg3 = _request.getParameter("arg3");
			String arg4 = _request.getParameter("arg4");
			String arg5 = _request.getParameter("arg5");
			String arg6 = _request.getParameter("arg6");
			String arg7 = _request.getParameter("arg7");
			String arg8 = _request.getParameter("arg8");
			String arg9 = _request.getParameter("arg9");
			
			Date stamper = new Date();
			System.out.println("");
			System.out.println("stamp >" + CUBean.getUserDateString(stamper, "HH:mm:ss.SSS"));
			System.out.println("command >" + command);
			System.out.println("parameter >" + parameter);
			System.out.println("arg1 >" + arg1);
			System.out.println("arg2 >" + arg2);
			System.out.println("arg3 >" + arg3);
			System.out.println("arg4 >" + arg4);
			System.out.println("arg5 >" + arg5);
			System.out.println("arg6 >" + arg6);
			System.out.println("arg7 >" + arg7);
			System.out.println("arg9 >" + arg9);
			
			
			HttpSession session = _request.getSession(true);
			System.out.println("session >" + session.getId());
			
			if (command.equals("getChartData")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				UKOnlinePersonBean report_person = null;
				if ( (arg4 != null) && !arg4.isEmpty()) {
					report_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg4));
				}
				
				int calendar_field = Integer.parseInt(arg2);
				int calendar_count = Integer.parseInt(arg3);
				
				boolean excludeTimeLine = true;
				if ( (arg5 != null) && !arg5.isEmpty()) {
					excludeTimeLine = false;
				}
				
				boolean includeCurrentProgramInfo = false;
				if ( (arg6 != null) && !arg6.isEmpty()) {
					includeCurrentProgramInfo = true;
				}
				
				WeightPreferences preferences = null;
				if ( (arg7 != null) && !arg7.isEmpty()) {
					//String time_zone = "America/Chicago";
					try {
						preferences = WeightPreferences.getWeightPreference(logged_in_person);
						String tzStr = preferences.getTimeZoneString();
						if ( tzStr.isEmpty() || !tzStr.equals(arg7) ) {
							preferences.setTimeZone(arg7);
							preferences.getTimeZone(); // just to test that it's a Java valid time zone string
							preferences.save();
						}
						
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
				
				if (preferences == null) {
					preferences = WeightPreferences.getWeightPreference(logged_in_person);
				}
				
				StringBuilder b = new StringBuilder();
				boolean needs_comma = false;
				//b.append('[');
				if (includeCurrentProgramInfo) {
					TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan((report_person == null) ? logged_in_person : report_person);
					b.append("{\"p\":\"" + JSONObject.escape(current_training_plan.getLabel()) + "\",");
					b.append("\"pId\":" + current_training_plan.getId() + ",");
					b.append("\"weight\":[");
				} else {
					b.append("{\"weight\":[");
				}
				Iterator itr = WeightEntry.getEntries( (report_person == null) ? logged_in_person : report_person, calendar_field, calendar_count).iterator();
				while (itr.hasNext()) {
					WeightEntry entry_obj = (WeightEntry)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toChartJSON(entry_obj.getEntryDate(), entry_obj.getEntryAmountString(), preferences.getTimeZone()));
					needs_comma = true;
				}
				
				needs_comma = false;
				
				
				
				b.append("],\"strength\":[");
				
				// get compound moves
				// loop through compound moves to get  - will need to filter personal or public moves
				
				HashMap<String,BigDecimal> strength_hash = new HashMap<String,BigDecimal>();
				
				BigDecimal weight_offset = BigDecimal.ZERO;
				try {
					if (report_person != null) {
						System.out.println("report_person >" + report_person.getLabel());
					}
					if (logged_in_person != null) {
						System.out.println("logged_in_person >" + logged_in_person.getLabel());
					}
					weight_offset = WeightEntry.getLastEntry( (report_person == null) ? logged_in_person : report_person ).getEntryAmount();
					weight_offset = weight_offset.add(new BigDecimal(3)); // shrug
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				Iterator<Move> compound_move_itr = Move.getCompoundMoves( (report_person == null) ? logged_in_person : report_person );
				while (compound_move_itr.hasNext()) {
					Move compound_move = compound_move_itr.next();
					System.out.println("compound move found >" + compound_move.getLabel());
					itr = compound_move.getStrengthEntries( (report_person == null) ? logged_in_person : report_person, calendar_field, calendar_count );
					while (itr.hasNext()) {
						Move.StrengthEntry entry_obj = (Move.StrengthEntry)itr.next();
						
						String entry_date_key = CUBean.getUserDateString(entry_obj.getEntryDate());
						if (strength_hash.containsKey(entry_date_key)) {
							strength_hash.put(entry_date_key, entry_obj.getEntryAmount().add(strength_hash.get(entry_date_key)));
						} else {
							strength_hash.put(entry_date_key, entry_obj.getEntryAmount());
						}
					}
				}
				
				ArrayList<String> sortedKeys = new ArrayList<String>(strength_hash.keySet());
				Collections.sort(sortedKeys);
				
				BigDecimal total_increase = BigDecimal.ZERO;
				itr = sortedKeys.iterator();
				//itr = strength_hash.keySet().iterator();
				//itr = bench.getStrengthEntries( (report_person == null) ? logged_in_person : report_person, calendar_field, calendar_count );
				while (itr.hasNext()) {
					String date_key = (String)itr.next();
					BigDecimal strength_perc_increase_value = (BigDecimal)strength_hash.get(date_key);
					total_increase = total_increase.add(strength_perc_increase_value);
					//Move.StrengthEntry entry_obj = (Move.StrengthEntry)itr.next();
					if (needs_comma) { b.append(','); }
					//b.append(this.toChartJSON(entry_obj.getEntryDate(), entry_obj.getEntryAmountString()));
					b.append(this.toChartJSON(date_key, total_increase.add(weight_offset)));
					needs_comma = true;
					
				}
				
				needs_comma = false;
				
				Vector macro_daily_vec = MacroGoalDaily.getMacroGoalDailys((report_person == null) ? logged_in_person : report_person, calendar_field, calendar_count);
				b.append("],\"date\":[");
				itr = macro_daily_vec.iterator();
				while (itr.hasNext()) {
					MacroGoalDaily entry_obj = (MacroGoalDaily)itr.next();
					if (needs_comma) { b.append(','); }
					b.append("\"" + entry_obj.getEntryDateString() + " 12:00\"");
					needs_comma = true;
				}
				needs_comma = false;
				b.append("],\"fat\":[");
				itr = macro_daily_vec.iterator();
				while (itr.hasNext()) {
					MacroGoalDaily entry_obj = (MacroGoalDaily)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(entry_obj.getFatCaloriesString());
					needs_comma = true;
				}
				needs_comma = false;
				b.append("],\"carbs\":[");
				itr = macro_daily_vec.iterator();
				while (itr.hasNext()) {
					MacroGoalDaily entry_obj = (MacroGoalDaily)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(entry_obj.getCarbsCaloriesString());
					needs_comma = true;
				}
				needs_comma = false;
				b.append("],\"protein\":[");
				itr = macro_daily_vec.iterator();
				while (itr.hasNext()) {
					MacroGoalDaily entry_obj = (MacroGoalDaily)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(entry_obj.getProteinCaloriesString());
					needs_comma = true;
				}
				
				if (excludeTimeLine) {
					b.append("],\"tl\":[");

					int num_timeline_items = 10;
					if (arg5 != null) {
						num_timeline_items = Integer.parseInt(arg5);
					}

					ArrayList<Workout> last_workouts = Workout.getLastWorkouts((report_person == null) ? logged_in_person : report_person, num_timeline_items);
					ArrayList<MacroGoalDaily> last_macro_dailys = MacroGoalDaily.getLastMacroGoalDailys((report_person == null) ? logged_in_person : report_person, num_timeline_items);
					ArrayList<WeightEntry> last_weight_entries = WeightEntry.getLastEntries((report_person == null) ? logged_in_person : report_person, num_timeline_items);

					// I need an ArrayList of dates

					HashMap<Date, ArrayList> event_hash = new HashMap();
					//ArrayList<Date> event_date_keys = new ArrayList();
					itr = last_workouts.iterator();
					while (itr.hasNext()) {
						Workout obj = (Workout)itr.next();
						Date date_key = obj.getStartDate();
						//event_date_keys.add(date_key);
						ArrayList vec = null;
						if (event_hash.containsKey(date_key)) {
							vec = event_hash.get(date_key);
						} else {
							vec = new ArrayList();
						}
						vec.add(obj);
						event_hash.put(date_key, vec);
					}
					itr = last_macro_dailys.iterator();
					while (itr.hasNext()) {
						MacroGoalDaily obj = (MacroGoalDaily)itr.next();
						Date date_key = obj.getEntryDate();
						//event_date_keys.add(date_key);
						ArrayList vec = null;
						if (event_hash.containsKey(date_key)) {
							vec = event_hash.get(date_key);
						} else {
							vec = new ArrayList();
						}
						vec.add(obj);
						event_hash.put(date_key, vec);
					}
					itr = last_weight_entries.iterator();
					while (itr.hasNext()) {
						WeightEntry obj = (WeightEntry)itr.next();
						Date date_key = obj.getEntryDate();
						//event_date_keys.add(date_key);
						ArrayList vec = null;
						if (event_hash.containsKey(date_key)) {
							vec = event_hash.get(date_key);
						} else {
							vec = new ArrayList();
						}
						vec.add(obj);
						event_hash.put(date_key, vec);
					}

					ArrayList<Date> sortedDateKeys = new ArrayList<Date>(event_hash.keySet());
					Collections.sort(sortedDateKeys, Collections.reverseOrder());

					needs_comma = false;
					itr = sortedDateKeys.iterator();
					for (int i = 0; itr.hasNext() && ( i < num_timeline_items ); i++ ) {
						Date date_key = (Date)itr.next();
						//System.out.println("found date >" + CUBean.getUserDateString(date_key));

						ArrayList<Date> event_list = event_hash.get(date_key);
						Iterator event_itr = event_list.iterator();
						while (event_itr.hasNext()) {
							Object raw_obj = event_itr.next();
							if (raw_obj instanceof com.valonyx.fitness.beans.Workout) {
								Workout entry_obj = (Workout)raw_obj;
								if (needs_comma) { b.append(','); }
								b.append(this.toJSONTimeline(entry_obj));
								needs_comma = true;
							} else if (raw_obj instanceof MacroGoalDaily) {
								MacroGoalDaily entry_obj = (MacroGoalDaily)raw_obj;
								if (needs_comma) { b.append(','); }
								b.append(this.toJSONTimeline(entry_obj));
								needs_comma = true;
							} else if (raw_obj instanceof WeightEntry) {
								WeightEntry entry_obj = (WeightEntry)raw_obj;
								if (needs_comma) { b.append(','); }
								b.append(this.toJSONTimeline((report_person == null) ? logged_in_person : report_person, entry_obj));
								needs_comma = true;
							}
						}

					}

					b.append("]}");
				} else {
					b.append("]}");
				}
				
				writer.println(b.toString());
				
			} else if (command.equals("getSetTimer")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				if (last_set_completion_date_hash != null &&
						last_set_completion_date_hash.containsKey(logged_in_person)) {
					Date now = new Date();
					Date lastSetCompletionDt = last_set_completion_date_hash.get(logged_in_person);
					long setSeconds = Workout.getDateDiff(lastSetCompletionDt, now, TimeUnit.SECONDS);
					long setMinutes = setSeconds / 60;
					setSeconds = setSeconds % 60;
					
					writer.println("{\"timer\":[{\"setMinutes\":" + setMinutes + ",\"setSeconds\":" + setSeconds + "}]}");
					
				} else {
					writer.println("{\"timer\":[]}");
				}
				
			} else if (command.equals("timelineMore")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				/*
				UKOnlinePersonBean report_person = null;
				if (arg4 != null && !arg4.isEmpty()) {
					report_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg4));
				}
				*/
				
				int start_timeline_item = 0;
				if (arg2 != null) {
					start_timeline_item = Integer.parseInt(arg2);
				}
				
				StringBuilder b = new StringBuilder();
				boolean needs_comma = false;
				//b.append('[');
				b.append("{\"tl\":[");
				
				int num_timeline_items = 10;
				/*
				if (arg5 != null) {
					num_timeline_items = Integer.parseInt(arg5);
				}
				*/
				
				ArrayList<Workout> last_workouts = Workout.getLastWorkouts(logged_in_person, start_timeline_item + num_timeline_items);
				ArrayList<MacroGoalDaily> last_macro_dailys = MacroGoalDaily.getLastMacroGoalDailys(logged_in_person, start_timeline_item + num_timeline_items);
				ArrayList<WeightEntry> last_weight_entries = WeightEntry.getLastEntries(logged_in_person, start_timeline_item + num_timeline_items);
				
				// I need an ArrayList of dates
				
				HashMap<Date, ArrayList> event_hash = new HashMap();
				//ArrayList<Date> event_date_keys = new ArrayList();
				Iterator itr = null;
				
				/*
				Iterator itr = last_workouts.iterator();
				while (itr.hasNext()) {
					Workout obj = (Workout)itr.next();
					Date date_key = obj.getStartDate();
					//event_date_keys.add(date_key);
					ArrayList vec = null;
					if (event_hash.containsKey(date_key)) {
						vec = event_hash.get(date_key);
					} else {
						vec = new ArrayList();
					}
					vec.add(obj);
					event_hash.put(date_key, vec);
				}
				*/
				
				last_workouts.forEach( workout -> {
					Date date_key = workout.getStartDate();
					ArrayList vec;
					if (event_hash.containsKey(date_key)) {
						vec = event_hash.get(date_key);
					} else {
						vec = new ArrayList();
					}
					vec.add(workout);
					event_hash.put(date_key, vec);
				} );
				
				/*
				itr = last_macro_dailys.iterator();
				while (itr.hasNext()) {
					MacroGoalDaily obj = (MacroGoalDaily)itr.next();
					Date date_key = obj.getEntryDate();
					//event_date_keys.add(date_key);
					ArrayList vec = null;
					if (event_hash.containsKey(date_key)) {
						vec = event_hash.get(date_key);
					} else {
						vec = new ArrayList();
					}
					vec.add(obj);
					event_hash.put(date_key, vec);
				}
				*/
				
				last_macro_dailys.forEach( macroGoalDaily -> {
					Date date_key = macroGoalDaily.getEntryDate();
					ArrayList vec;
					if (event_hash.containsKey(date_key)) {
						vec = event_hash.get(date_key);
					} else {
						vec = new ArrayList();
					}
					vec.add(macroGoalDaily);
					event_hash.put(date_key, vec);
				} );
				
				/*
				itr = last_weight_entries.iterator();
				while (itr.hasNext()) {
					WeightEntry obj = (WeightEntry)itr.next();
					Date date_key = obj.getEntryDate();
					//event_date_keys.add(date_key);
					ArrayList vec = null;
					if (event_hash.containsKey(date_key)) {
						vec = event_hash.get(date_key);
					} else {
						vec = new ArrayList();
					}
					vec.add(obj);
					event_hash.put(date_key, vec);
				}
				*/
				
				last_weight_entries.forEach( weightEntry -> {
					Date date_key = weightEntry.getEntryDate();
					//event_date_keys.add(date_key);
					ArrayList vec = null;
					if (event_hash.containsKey(date_key)) {
						vec = event_hash.get(date_key);
					} else {
						vec = new ArrayList();
					}
					vec.add(weightEntry);
					event_hash.put(date_key, vec);
				} );
				
				ArrayList<Date> sortedDateKeys = new ArrayList<Date>(event_hash.keySet());
				Collections.sort(sortedDateKeys, Collections.reverseOrder());
				
				needs_comma = false;
				itr = sortedDateKeys.iterator();
				for (int i = 0; itr.hasNext() && ( i < (start_timeline_item + num_timeline_items) ); i++ ) {
					Date date_key = (Date)itr.next();
					//System.out.println("found date >" + CUBean.getUserDateString(date_key));
					
					if (i >= start_timeline_item) {
						//ArrayList<Date> event_list = event_hash.get(date_key);
						ArrayList<Object> event_list = event_hash.get(date_key);
						
						/*
						Iterator event_itr = event_list.iterator();
						while (event_itr.hasNext()) {
							Object raw_obj = event_itr.next();
							if (raw_obj instanceof com.valonyx.fitness.beans.Workout) {
								Workout entry_obj = (Workout)raw_obj;
								if (needs_comma) { b.append(','); }
								b.append(this.toJSONTimeline(entry_obj));
								needs_comma = true;
							} else if (raw_obj instanceof MacroGoalDaily) {
								MacroGoalDaily entry_obj = (MacroGoalDaily)raw_obj;
								if (needs_comma) { b.append(','); }
								b.append(this.toJSONTimeline(entry_obj));
								needs_comma = true;
							} else if (raw_obj instanceof WeightEntry) {
								WeightEntry entry_obj = (WeightEntry)raw_obj;
								if (needs_comma) { b.append(','); }
								b.append(this.toJSONTimeline(logged_in_person, entry_obj));
								needs_comma = true;
							}
						}
						*/
						
						for (Object raw_obj : event_list) {
							if (raw_obj instanceof com.valonyx.fitness.beans.Workout) {
								Workout entry_obj = (Workout)raw_obj;
								if (needs_comma) { b.append(','); }
								b.append(this.toJSONTimeline(entry_obj));
								needs_comma = true;
							} else if (raw_obj instanceof MacroGoalDaily) {
								MacroGoalDaily entry_obj = (MacroGoalDaily)raw_obj;
								if (needs_comma) { b.append(','); }
								b.append(this.toJSONTimeline(entry_obj));
								needs_comma = true;
							} else if (raw_obj instanceof WeightEntry) {
								WeightEntry entry_obj = (WeightEntry)raw_obj;
								if (needs_comma) { b.append(','); }
								b.append(this.toJSONTimeline(logged_in_person, entry_obj));
								needs_comma = true;
							}
						}
						
					}
					
				}
				
				b.append("]}");
				
				writer.println(b.toString());
				
			} else if (command.equals("getMoveHistory")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				writer.println(this.toJSON(logged_in_person, Move.getMove(Integer.parseInt(arg2)), true));
				
			} else if (command.equals("addMacroEntry")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				writer.println(this.addMacroEntry(logged_in_person, arg2, arg3, arg4));
				
			} else if (command.equals("starSearch")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				StringBuilder b = new StringBuilder();
				b.append("{\"food\":[");
				boolean needs_comma = false;
				for (Food favorite_food : Food.searchFood(logged_in_person, arg2, -1)) {
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(favorite_food, true, true));
					needs_comma = true;
				}
				b.append("]}");
				writer.println(b.toString());
				
			} else if (command.equals("searchFoods")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				StringBuilder b = new StringBuilder();
				b.append("{\"food\":[");
				Iterator itr = Food.searchPublicFood(logged_in_person, arg2, 25).iterator();
				boolean needs_comma = false;
				while (itr.hasNext()) {
					Food fud = (Food)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(fud, true, fud.isFavorite(logged_in_person)));
					needs_comma = true;
				}
				b.append("]}");
				writer.println(b.toString());
				
			} else if (command.equals("showFavorites")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				boolean show_all_favorites = false;
				if (arg2 != null) {
					show_all_favorites = true;
				}
				
				StringBuilder b = new StringBuilder();
				b.append("{\"food\":[");
				Iterator itr = null;
				if (show_all_favorites) {
					itr = Food.getFavorites(logged_in_person).iterator();
				} else {
					itr = Food.getFavoritesByTimeOfDay(logged_in_person).iterator();
				}
				//Iterator itr = Food.getFavorites(logged_in_person).iterator();
				//Iterator itr = Food.getFavoritesByTimeOfDay(logged_in_person).iterator();
				boolean needs_comma = false;
				while (itr.hasNext()) {
					Food favorite_food = (Food)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(favorite_food, false, true));
					needs_comma = true;
				}
				b.append("],");
				b.append("\"all\":" + show_all_favorites + "}");
				writer.println(b.toString());
				
			} else if (command.equals("addMacroServing")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				//writer.println(this.addMacroEntry(logged_in_person, MacroGoalDaily.getCurrentMacroGoalDaily(logged_in_person), arg2, arg3, arg4));
				
				Food fud = Food.getFood(Long.parseLong(arg2));
				System.out.println("food_obj >" + fud.getLabel());
				String label = java.net.URLDecoder.decode(arg3, "UTF-8");
				System.out.println("serving >" + label);
				
				MacroGoalDaily daily = MacroGoalDaily.getCurrentMacroGoalDaily(logged_in_person);
				
				if (label.equals("Add One Serving")) {
					
					MacroGoalEntry entry = daily.addEntryByServing(fud.getLabel(), fud.getFat(), fud.getCarbs(), fud.getProtein(), BigDecimal.ONE, fud.getServingSize(), fud.getServingSizeTypeString(), fud, new Date());
				
				} else {
				
					FoodServingDb selected_serving = null;
					Iterator itr = fud.getServings().iterator();
					while (itr.hasNext()) {
						FoodServingDb obj = (FoodServingDb)itr.next();
						if (obj.getServingLabel().equals(label)) {
							//System.out.println("found serving!!");
							selected_serving = obj;
						}
					}

					if (selected_serving == null) {
						throw new IllegalValueException("Selected serving not found.");
					}

					BigDecimal amount_consumed = selected_serving.getServingSize();

					BigDecimal calculated_fat = fud.getFat().multiply(selected_serving.getServingSize()).divide(CUBean.one_hundred, 2, RoundingMode.HALF_UP);
					BigDecimal calculated_carbs = fud.getCarbs().multiply(selected_serving.getServingSize()).divide(CUBean.one_hundred, 2, RoundingMode.HALF_UP);
					BigDecimal calculated_protein = fud.getProtein().multiply(selected_serving.getServingSize()).divide(CUBean.one_hundred, 2, RoundingMode.HALF_UP);

					MacroGoalEntry entry = daily.addEntryByServing(fud.getLabel() + " (" + label + ")", calculated_fat, calculated_carbs, calculated_protein, BigDecimal.ONE, fud.getServingSize(), fud.getServingSizeTypeString(), fud, new Date());
				}
				writer.println(this.toJSON(daily));
		
			} else if (command.equals("addWeightEntry")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				writer.println(this.addWeightEntry(logged_in_person, arg2));
				
			} else if (command.equals("getMacroGoal")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				MacroGoalDaily goal = null;
				if (arg2 == null || arg2.isEmpty()) {
					//goal = MacroGoal.getCurrentMacroGoal(logged_in_person);
					goal = MacroGoalDaily.getCurrentMacroGoalDaily(logged_in_person);
					auth_key_selected_display_date.remove(arg1);
				} else if (arg2.equals("p")) { // previous day
					/*
					goal = MacroGoal.getMacroGoal(Integer.parseInt(key));
					if (!goal.getPerson().equals(logged_in_person)) {
						throw new IllegalValueException("Plan not found.");
					}
					*/
					
					Calendar selected_display_date = auth_key_selected_display_date.get(arg1);
					if (selected_display_date == null) {
						selected_display_date = Calendar.getInstance();
					}
					selected_display_date.add(Calendar.DATE, -1);
					auth_key_selected_display_date.put(arg1, selected_display_date);
					goal = MacroGoalDaily.getMacroGoalDaily(logged_in_person, selected_display_date.getTime());
				}
				
				writer.println(this.toJSON(goal));
				
			} else if (command.equals("getMacroGoals")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				UKOnlinePersonBean clientPerson = null;
				if (arg3 != null && !arg3.isEmpty()) {
					clientPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
					if (!logged_in_person.supervises(clientPerson)) {
						throw new IllegalValueException("Unable to get macro goals.  You're not the trainer for this person.");
					}
				}
				
				MacroGoal selected_macro_goal = null;
				
				//TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				Vector goals = MacroGoal.getMacroGoals((clientPerson == null) ? logged_in_person : clientPerson);
				
				if (arg2 != null && !arg2.isEmpty() && !arg2.equals("0")) {
					selected_macro_goal = MacroGoal.getMacroGoal(Integer.parseInt(arg2));
					if (!goals.contains(selected_macro_goal)) {
						throw new IllegalValueException("Unable to select goal.");
					}
				}
				
				writer.println(this.toJSON((clientPerson == null) ? logged_in_person : clientPerson, selected_macro_goal, goals, ( arg2 != null && arg2.equals("0") )));
				
			} else if (command.equals("getWeightGoals")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				UKOnlinePersonBean clientPerson = null;
				if (arg3 != null && !arg3.isEmpty()) {
					clientPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
					if (!logged_in_person.supervises(clientPerson)) {
						throw new IllegalValueException("Unable to get weight goals.  You're not the trainer for this person.");
					}
				}
				
				WeightGoal selected_weight_goal = null;
				
				Vector goals = WeightGoal.getWeightGoals((clientPerson == null) ? logged_in_person : clientPerson);
				
				if (arg2 != null && !arg2.isEmpty() && !arg2.equals("0")) {
					selected_weight_goal = WeightGoal.getWeightGoal(Integer.parseInt(arg2));
					if (!goals.contains(selected_weight_goal)) {
						throw new IllegalValueException("Unable to select goal.");
					}
				}
				writer.println(this.toJSON((clientPerson == null) ? logged_in_person : clientPerson, selected_weight_goal, goals, ( arg2 != null && arg2.equals("0") ) ));
				
			} else if (command.equals("getWeightGoal")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				WeightGoal goal = null;
				if (arg2 == null || arg2.isEmpty()) {
					//goal = MacroGoal.getCurrentMacroGoal(logged_in_person);
					goal = WeightGoal.getCurrentWeightGoal(logged_in_person);
					auth_key_selected_display_date.remove(arg1);
				} else if (arg2.equals("p")) { // previous day
					/*
					goal = MacroGoal.getMacroGoal(Integer.parseInt(key));
					if (!goal.getPerson().equals(logged_in_person)) {
						throw new IllegalValueException("Plan not found.");
					}
					*/
					
					/*
					Calendar selected_display_date = auth_key_selected_display_date.get(arg1);
					if (selected_display_date == null) {
						selected_display_date = Calendar.getInstance();
					}
					selected_display_date.add(Calendar.DATE, -1);
					auth_key_selected_display_date.put(arg1, selected_display_date);
					goal = MacroGoalDaily.getMacroGoalDaily(logged_in_person, selected_display_date.getTime());
					*/
				}
				int limit = 14; // two weeks by default
				if (arg3 != null && !arg3.isEmpty()) {
					limit = Integer.parseInt(arg3);
				}
				
				writer.println(this.toJSON(goal, limit));
				
				//this.trainingPlanToExcel((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4434));
				//FitnessServlet.modifyExternalGoogleSheet((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4434));
				
			} else if (command.equals("starEntry")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				MacroGoalEntry entry_to_star = MacroGoalEntry.getMacroGoalEntry(Long.parseLong(arg2));
				
				Food fud = entry_to_star.getFood(); // ought to exist at this point
		
				boolean is_favorite = Food.isFavorite(fud, logged_in_person);
				System.out.println("is_favorite >" + is_favorite);
				if (is_favorite) {
					/*
					Food.removeFavorite(fud, logged_in_person);
					
					writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Favorite Removed\",\"text\":\"" + JSONObject.escape(fud.getLabel()) + " removed as a favorite.\"}]}");
					*/
					
					throw new IllegalValueException(fud.getLabel() + " is already a favorite.");
					
				} else {
					FavoriteFood fav_food = new FavoriteFood();
					fav_food.setPersonId(logged_in_person.getId());
					fav_food.setFoodDbId(fud.getId());
					fav_food.setFavoriteDate(new Date());
					fav_food.save();
					
					writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Favorite Created\",\"text\":\"" + JSONObject.escape(fud.getLabel()) + " marked as a favorite.\"}]}");
				
				}
				
			} else if (command.equals("starToggle")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				Food fud = Food.getFood(Long.parseLong(arg2));
				
				boolean is_favorite = Food.isFavorite(fud, logged_in_person);
				System.out.println("is_favorite >" + is_favorite);
				if (is_favorite) {
					
					Food.removeFavorite(fud, logged_in_person);	
					writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Favorite Removed\",\"text\":\"" + JSONObject.escape(fud.getLabel()) + " removed as a favorite.\"}]}");
				
				} else {
					FavoriteFood fav_food = new FavoriteFood();
					fav_food.setPersonId(logged_in_person.getId());
					fav_food.setFoodDbId(fud.getId());
					fav_food.setFavoriteDate(new Date());
					fav_food.save();
					
					writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Favorite Created\",\"text\":\"" + JSONObject.escape(fud.getLabel()) + " marked as a favorite.\"}]}");
				}
				
			} else if (command.equals("deleteEntry")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				MacroGoalEntry entry_to_delete = MacroGoalEntry.getMacroGoalEntry(Long.parseLong(arg2));
				if (entry_to_delete.getParent().getParent().getPerson().equals(logged_in_person)) {
					MacroGoalEntry.delete(Long.parseLong(arg2));
					entry_to_delete.getParent().invalidate();
					entry_to_delete.getParent().recalculateMacros();

					//writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Entry Deleted\",\"text\":\"" + entry_to_delete.getLabel() + " deleted.\"}]}");
					
					writer.println(this.toJSON(entry_to_delete.getParent()));
				}
				
			} else if (command.equals("deleteWeightEntry")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				WeightEntry entry_to_delete = WeightEntry.getWeightEntry(Long.parseLong(arg2));
				if (entry_to_delete.getParent().getPerson().equals(logged_in_person)) {
					WeightEntry.delete(Long.parseLong(arg2));
					entry_to_delete.getParent().invalidate();
					writer.println(this.toJSON(entry_to_delete.getParent()));
				}
				
			} else if (command.equals("beginWorkout")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				
				boolean create_new_training_plan_workout = (arg2 != null) && arg2.equals("true");
				
				TrainingPlanWorkout workout_obj = null;
				
				if (!create_new_training_plan_workout) {
					try {
						workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
					} catch (ObjectNotFoundException x) {
						
					}
				}
				
				Workout workout_to_start = null;
				
				if ( ( arg3 != null ) && !arg3.isEmpty() ) {
					//workout_obj = TrainingPlanWorkout.getTrainingPlanWorkout(Integer.parseInt(arg3));
					workout_to_start = Workout.getWorkout(Long.parseLong(arg3));
					workout_obj = workout_to_start.getParent();
				}
				
				if (workout_obj == null) {
					workout_obj = new TrainingPlanWorkout();
					workout_obj.setParent(current_training_plan);
					workout_obj.setWorkoutNumber((short)current_training_plan.getWorkouts().size());
					workout_obj.setName(CUBean.getUserDateString(new Date()) + " Workout");
					workout_obj.setBlockNumber((short)1); // I should be able to calculate this as well
					workout_obj.setWeekNumber((short)1); // calculate this based on the first workout in the plan or something
					workout_obj.setIsAdHoc(true);
					workout_obj.save();
					current_training_plan.setCurrentTrainingPlanWorkout(logged_in_person, workout_obj);
					current_training_plan.invalidate();
				}
				
				
				System.out.println("workout_obj >" + workout_obj.getLabel());
				
				/*
				b.append("\"currentWorkoutLabel\":\"" + JSONObject.escape(current_workout.getLabel()) + "\",");
				b.append("\"currentWorkoutDesc\":\"" + JSONObject.escape(current_workout.getDescription()) + "\",");
				//b.append("\"currentWorkoutInProgress\":" + Workout.hasCurrentWorkout(_logged_in_person) + ","); // seems like this'll work
				b.append("\"currentWorkoutInProgress\":" + Workout.isWorkoutInProgress(current_workout, _logged_in_person) + ",");
				b.append("\"currentWorkoutBlock\":" + current_workout.getBlockNumber() + ",");
				b.append("\"currentWorkoutWeek\":" + current_workout.getWeekNumber() + ",");
				*/
				
				// I'll need to spin out a workout "object" from this workout "class"
				
				
				
				boolean create_new_workout = true;
				
				if (Workout.hasCurrentWorkout(logged_in_person)) {
					create_new_workout = false;
					// there's already a workout in progress.  return to it
					if (workout_to_start == null) {
						workout_to_start = Workout.getCurrentWorkout(logged_in_person);
					}
					
					if (!Workout.isWorkoutInProgress(workout_obj, logged_in_person)) {
						
						// should I automatically end workout_to_start?
						
						long nowstamp = (new Date()).getTime() / 1000;
						long timestamp = workout_to_start.getStartDateUnixTimestamp();
						long seconds_diff = nowstamp - timestamp;
						System.out.println("seconds_diff >" + seconds_diff);
						
						if ( (seconds_diff / 3600) > 4 ) {
							// automatically end other workout if started more than 4 hours ago
							System.out.println("automatically end other workout if started more than 4 hours ago");
							workout_to_start.setEndDate(new Date());
							workout_to_start.save();
							create_new_workout = true;
						} else {
							throw new IllegalValueException("Unable to start workout.  " + workout_to_start.getLabel() + " must be finished first.");
						}
					}
				}
				
				if (create_new_workout) {
					workout_to_start = new Workout();
					workout_to_start.setPerson(logged_in_person);
					workout_to_start.setParent(workout_obj);
					workout_to_start.setStartDate(new Date());
					// find the first move
					Vector moves = workout_obj.getMoves();
					/*
					if (moves.isEmpty()) {
						throw new ObjectNotFoundException("No moves in workout >" + workout_obj.getLabel());
					}
					*/
					if (!moves.isEmpty()) {
						workout_to_start.setCurrentMove((TrainingPlanMove)moves.elementAt(0));
					}
					workout_to_start.save();
				}
				
				
				//writer.println(this.toJSON(logged_in_person, workout_to_start, true, include_all_moves, false));
				
				//System.out.println("workout_to_start.hasNextMove() >" + workout_to_start.hasNextMove());
				
				//if (workout_to_start.hasNextMove()) {
				if (workout_to_start.hasMoves()) { // changed to this 4/4/19
					//workout_to_start.nextMove();
					//boolean include_all_moves = (arg4 != null) && arg4.equals("true");
					boolean include_all_moves = true; // changed to this 5/1/19 - first, arg2 is being used above already, second, I'm not sure if this is required or what the original ideas is/was
					writer.println(this.toJSON(logged_in_person, workout_to_start, true, include_all_moves, false));
				} else {
					//throw new IllegalValueException("Next move not found in workout!!");
					writer.println(this.toJSON(logged_in_person, workout_to_start, true, false, true));
				}
				
			} else if (command.equals("finishWorkout")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				System.out.println("workout_obj >" + workout_obj.getLabel());
				
				// I'll need to spin out a workout "object" from this workout "class"
				
				if (!Workout.hasCurrentWorkout(logged_in_person)) {
					// there's already a workout in progress.  return to it
					throw new IllegalValueException("Unable to find workout to finish!!");
				}
				
				Workout workout_being_finished = this.finishWorkout(logged_in_person, arg2);
				
				try {
					current_training_plan.nextWorkout(logged_in_person);
					
					PersonTrainingPlanMapping mapping = current_training_plan.getMapping(logged_in_person, false);
					if (mapping.getGoogleSheetId() != null) {
						FitnessServlet.updateGoogleSheetFromTrainingPlan(logged_in_person, current_training_plan, mapping.getGoogleSheetId());
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				try {
					// also email the coach

					UKOnlinePersonBean will = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4434);
					logged_in_person.setSupervisor(will, "Trainer");
					if (logged_in_person.hasSupervisor("Trainer")) {
						
						StringBuilder b = new StringBuilder();
						//b.append("<p>" + confirmed_person.getLabel() + " has requested training.</p>");
						//b.append("<p>Training goals:</p>");
						//b.append("<p>" + inputTrainingGoals + "</p>");
						
						LinkedHashMap<Move,ArrayList<WorkoutSet>> set_hash = WorkoutSet.getSets(workout_being_finished);
						Iterator itr = set_hash.keySet().iterator();
						while (itr.hasNext()) {
							Move move_obj = (Move)itr.next();
							ArrayList<WorkoutSet> sets = (ArrayList<WorkoutSet>)set_hash.get(move_obj);
							b.append("<p>" + move_obj.getLabel() + "<br/>" + this.getSetString(move_obj, sets) + "</p>");
						}
						b.append("<p>" + workout_being_finished.getNotes() + "</p>");
						
						String html_email_str = this.getNewClientHTMLEmailString(will, logged_in_person.getLabel() + " has finished workout " + workout_being_finished.getLabel() + ".", b.toString());
						CUBean.sendEmailWithMeeting(will.getEmail1String(), "do_not_reply@valonyx.com", logged_in_person.getLabel() + " has finished a workout.", null, html_email_str);
						CUBean.sendEmailWithMeeting("marlo@valonyx.com", "do_not_reply@valonyx.com", logged_in_person.getLabel() + " has finished a workout.", null, html_email_str);
					}
				} catch (Exception x) {
					x.printStackTrace();
				}
				
				writer.println(this.toJSON(logged_in_person, current_training_plan, false, false));
				
			} else if (command.equals("cancelWorkout")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				System.out.println("workout_obj >" + workout_obj.getLabel());
				System.out.println("workout_obj id >" + workout_obj.getValue());
				
				// I'll need to spin out a workout "object" from this workout "class"
				
				if (!Workout.hasCurrentWorkout(logged_in_person)) {
					// there's already a workout in progress.  return to it
					throw new IllegalValueException("Unable to find workout to cancel!!");
				}
				
				Workout workout_to_cancel = Workout.getCurrentWorkout(logged_in_person);
				Workout.delete(workout_to_cancel.getId());
				
				
				System.out.println("delete the training plan workout also >" + workout_obj.isAdHoc());
				if (workout_obj.isAdHoc()) {
					TrainingPlanWorkout.delete(workout_obj.getId());
				}
				
				current_training_plan.invalidate();
				
				if (last_set_completion_date_hash != null) {
					last_set_completion_date_hash.remove(logged_in_person);
				}

				writer.println(this.toJSON(logged_in_person, current_training_plan, false, false));
				
			} else if (command.equals("toggleSetComplete")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				System.out.println("workout_obj >" + workout_obj.getLabel());
				System.out.println("workout_obj id >" + workout_obj.getId());
				
				if (!Workout.hasCurrentWorkout(logged_in_person)) {
					throw new IllegalValueException("No current workout found!!");
				}
				Workout workout = Workout.getCurrentWorkout(logged_in_person);
				System.out.println("current workout id >" + workout.getId());
				
				TrainingPlanSet set_obj = null;
				try {
					set_obj = TrainingPlanSet.getTrainingPlanSet(Long.parseLong(arg2));
				} catch (NumberFormatException x) {
					
					// create the new training plan set, but don't display for everyone
					
					int setNumber = Integer.parseInt(arg2.substring(3));
					System.out.println("found set number >" + setNumber);
					
					TrainingPlanMove current_move = workout.getCurrentMove();
					System.out.println("current_move >" + current_move.getLabel());
					
					// determine if there's already a move for this set number
					
					Vector sets = current_move.getSets();
					if (setNumber > sets.size()) {
						set_obj = new TrainingPlanSet();
						set_obj.setParent(current_move);
						set_obj.setIsPerc1RM(false);
						set_obj.setSetNumber((short)sets.size());
						set_obj.setIsExt(true);
						set_obj.save();
						set_obj.invalidate();
						sets.addElement(set_obj); // no need to add if it already exists
					} else {
						// already exists
						set_obj = (TrainingPlanSet)sets.elementAt(setNumber - 1);
					}
					
					//set_obj.save(); // shrug
					
				}
				
				System.out.println("found TrainingPlanSet id >" + set_obj.getId());
				WorkoutSet set_to_toggle = WorkoutSet.getSet(workout, set_obj);
				System.out.println("found set_to_toggle id >" + set_to_toggle.getId());
				
				if (arg3 != null && !arg3.isEmpty()) {
					set_to_toggle.setActualReps(Short.parseShort(arg3));
				}
				if (arg4 != null && !arg4.isEmpty()) {
					set_to_toggle.setActualWeight(new BigDecimal(arg4));
				}
				if (arg5 != null && !arg5.isEmpty() && !arg5.equals("-1")) {
					set_to_toggle.setActualRPE(new BigDecimal(arg5));
				}
				
				/* shrug
				if (!set_to_toggle.getWorkout().getParent().equals(workout_obj)) {
					throw new IllegalValueException("Invalid attempt to mark set complete.");
				}
				*/
				
				if (!set_to_toggle.getWorkout().getPerson().equals(logged_in_person)) {
					throw new IllegalValueException("Invalid attempt to mark set complete.");
				}
				
				set_to_toggle.toggleComplete();
				set_to_toggle.save();
				if (set_to_toggle.isComplete()) {
					
					String setTextSuccessText = "Set marked as complete.";
					
					// calc 1 rep max
					
					try {
						if (set_to_toggle.getTrainingPlanSet().getParent().getMove().isCompoundLift()) {
							setTextSuccessText += "  " + FitnessServlet.getStringFromBigDecimal(set_to_toggle.getCalculated1RepMax()) + " estimated 1RM.";
						}
					} catch (Exception x) {
						x.printStackTrace();
					}
					
					writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Set Complete\",\"text\":\"" + setTextSuccessText + "\"}]}");
					
					// reset (or start) set timer
					if (last_set_completion_date_hash == null) {
						last_set_completion_date_hash = new HashMap<UKOnlinePersonBean, Date>();
					}
					last_set_completion_date_hash.put(logged_in_person, new Date());
					
				} else {
					writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Set Not Complete\",\"text\":\"Set marked as incomplete.\"}]}");
				}
				
			} else if (command.equals("nextMove")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				System.out.println("workout_obj >" + workout_obj.getLabel());
				
				if (!Workout.hasCurrentWorkout(logged_in_person)) {
					throw new IllegalValueException("No current workout found!!");
				}
				Workout workout = Workout.getCurrentWorkout(logged_in_person);
				
				if (workout.hasNextMove()) {
					workout.nextMove();
					writer.println(this.toJSON(logged_in_person, workout, true, false, false));
				} else {
					//throw new IllegalValueException("Next move not found in workout!!");
					writer.println(this.toJSON(logged_in_person, workout, true, false, true));
				}
				
			} else if (command.equals("previousMove")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				System.out.println("workout_obj >" + workout_obj.getLabel());
				
				if (!Workout.hasCurrentWorkout(logged_in_person)) {
					throw new IllegalValueException("No current workout found!!");
				}
				Workout workout = Workout.getCurrentWorkout(logged_in_person);
				
				if (workout.hasPreviousMove()) {
					workout.previousMove();
				} else {
					throw new IllegalValueException("Previous move not found in workout!!");
				}
				
				writer.println(this.toJSON(logged_in_person, workout, true, false, false));
				
			} else if (command.equals("lastMove")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				System.out.println("workout_obj >" + workout_obj.getLabel());
				
				if (!Workout.hasCurrentWorkout(logged_in_person)) {
					throw new IllegalValueException("No current workout found!!");
				}
				Workout workout = Workout.getCurrentWorkout(logged_in_person);
				workout.lastMove();
				
				writer.println(this.toJSON(logged_in_person, workout, true, false, false));
				
			} else if (command.equals("saveWorkout")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				writer.println(this.saveWorkout(logged_in_person, TrainingPlan.getCurrentTrainingPlan(logged_in_person), arg2));
				
			} else if (command.equals("saveMoveChanges")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				writer.println(this.saveMoveChanges(logged_in_person, arg2));
				
			} else if (command.equals("addMove")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				TrainingPlanWorkout workout_obj = null;
				try {
					workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				} catch (ObjectNotFoundException x) {
					x.printStackTrace();
				}
				
				boolean save_move = false;
				if (arg3 != null && arg3.equals("true")) {
					save_move = true;
				}
				boolean add_move = false;
				if (arg4 != null && arg4.equals("true")) {
					add_move = true;
				}
				
				boolean add_alt_move = false;
				if (arg5 != null && arg5.equals("true")) {
					add_alt_move = true;
				}
				
				this.addMove(logged_in_person, workout_obj, arg2, save_move, add_move, add_alt_move);
				
				if (add_alt_move) {
					Workout workout = Workout.getCurrentWorkout(logged_in_person);

					/*
					if (workout.hasNextMove()) {
						workout.nextMove();
					} else {
						throw new IllegalValueException("Next move not found in workout!!");
					}
					*/

					writer.println(this.toJSON(logged_in_person, workout, true, false, false));
				} else {
					writer.println(this.toJSON(logged_in_person, current_training_plan, false, false));
				}
				
			} else if (command.equals("replaceMove")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				System.out.println("workout_obj >" + workout_obj.getLabel());
				
				if (!Workout.hasCurrentWorkout(logged_in_person)) {
					throw new IllegalValueException("No current workout found!!");
				}
				Workout workout = Workout.getCurrentWorkout(logged_in_person);
				workout.replaceMove(Move.getMove(Integer.parseInt(arg2)));
				
				writer.println(this.toJSON(logged_in_person, workout, true, false, false));
				
			} else if (command.equals("getMove")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				TrainingPlanMove move_obj = TrainingPlanMove.getTrainingPlanMove(Long.parseLong(arg2));
				if (!move_obj.getParent().equals(workout_obj)) {
					throw new IllegalValueException("Unable to find move...");
				}
				writer.println(this.toJSON(logged_in_person, move_obj, true));
				
			} else if (command.equals("removeGoal")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				MacroGoal goal_to_remove = MacroGoal.getMacroGoal(Integer.parseInt(arg2));
				
				if (!goal_to_remove.getPerson().equals(logged_in_person)) {
					throw new IllegalValueException("Unable to remove goal!!");
				}
				if (goal_to_remove.isActive()) {
					throw new IllegalValueException("Unable to remove your active macro goal.");
				}
				
				//TrainingPlan.delete(Integer.parseInt(arg2));
				goal_to_remove.setHidden(true);
				goal_to_remove.save();
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Macro Goal Removed\",\"text\":\"" + JSONObject.escape(goal_to_remove.getLabel()) + " removed.\"}]}");
				
			} else if (command.equals("removeWeightGoal")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				WeightGoal goal_to_remove = WeightGoal.getWeightGoal(Integer.parseInt(arg2));
				
				if (!goal_to_remove.getPerson().equals(logged_in_person)) {
					throw new IllegalValueException("Unable to remove weight goal!!");
				}
				if (goal_to_remove.isActive()) {
					throw new IllegalValueException("Unable to remove your active weight goal.");
				}
				
				//TrainingPlan.delete(Integer.parseInt(arg2));
				goal_to_remove.setHidden(true);
				goal_to_remove.save();
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Weight Goal Removed\",\"text\":\"" + JSONObject.escape(goal_to_remove.getLabel()) + " removed.\"}]}");
				
			} else if (command.equals("removePlan")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan plan_to_remove = TrainingPlan.getTrainingPlan(Integer.parseInt(arg2));

				if (!logged_in_person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME)) {
					if (!plan_to_remove.getPerson().equals(logged_in_person)) {
						throw new IllegalValueException("Unable to remove plan!!");
					}
				}
				if (plan_to_remove.isActive(logged_in_person)) {
					throw new IllegalValueException("Unable to remove your active plan.");
				}
				
				//TrainingPlan.delete(Integer.parseInt(arg2));
				plan_to_remove.setActive(false);
				plan_to_remove.setCreateOrModifyPerson(logged_in_person);
				plan_to_remove.save();
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Plan Removed\",\"text\":\"" + JSONObject.escape(plan_to_remove.getLabel()) + " removed.\"}]}");
				
			} else if (command.equals("removeWorkout")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlanWorkout workout_to_remove = TrainingPlanWorkout.getTrainingPlanWorkout(Long.parseLong(arg2));
				
				if (!workout_to_remove.getParent().canEdit(logged_in_person)) {
					throw new IllegalValueException("Unable to remove workout.  You don't have permission to modify training plan " + workout_to_remove.getParent().getLabel());
				}
				
				/*
				if (!logged_in_person.hasRole("Manager") && !logged_in_person.hasRole("Training Administrator")) {
					if (!workout_to_remove.getParent().getPerson().equals(logged_in_person)) {
						throw new IllegalValueException("Unable to remove workout!!");
					}
				}
				*/
				
				TrainingPlanWorkout.delete(Long.parseLong(arg2));
				workout_to_remove.getParent().invalidate();
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Workout Removed\",\"text\":\"" + JSONObject.escape(workout_to_remove.getLabel()) + " removed.\"}]}");
				
			} else if (command.equals("removeMove")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				
				if (!current_training_plan.canEdit(logged_in_person)) {
					throw new IllegalValueException("Unable to remove move.  You don't have permission to modify training plan " + current_training_plan.getLabel());
				}
				
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				TrainingPlanMove move_to_remove = TrainingPlanMove.getTrainingPlanMove(Long.parseLong(arg2));
				
				/*
				if (!logged_in_person.hasRole("Manager") && !logged_in_person.hasRole("Training Administrator")) {
					if (!move_to_remove.getParent().getParent().getPerson().equals(logged_in_person)) {
						throw new IllegalValueException("Unable to remove move!!");
					}
				}
				*/
				
				TrainingPlanMove.delete(Long.parseLong(arg2));
				workout_obj.invalidate();
				
				Iterator itr = workout_obj.getMoves().iterator();
				for (short moveNumber = 1; itr.hasNext(); moveNumber++) {
					TrainingPlanMove training_plan_move = (TrainingPlanMove)itr.next();
					if (training_plan_move.getMoveNumber() != moveNumber) {
						training_plan_move.setMoveNumber(moveNumber);
						training_plan_move.save();
					}
				}
				
				writer.println(this.toJSON(logged_in_person, current_training_plan, false, false));
				
			} else if (command.equals("moveUp")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				if (!current_training_plan.canEdit(logged_in_person)) {
					throw new IllegalValueException("Unable to move up.  You don't have permission to update moves in this training plan.");
				}
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				TrainingPlanMove move_to_move = TrainingPlanMove.getTrainingPlanMove(Long.parseLong(arg2));
				short orig_number = move_to_move.getMoveNumber();
				if (orig_number == (short)1) {
					throw new IllegalValueException("Unable to move up.");
				}
				short new_number_for_move = (short)( move_to_move.getMoveNumber() - 1 );
				move_to_move.setMoveNumber(new_number_for_move);
				move_to_move.save();
				
				Iterator itr = workout_obj.getMoves().iterator();
				while (itr.hasNext()) {
					TrainingPlanMove training_plan_move = (TrainingPlanMove)itr.next();
					if ( (training_plan_move.getMoveNumber() == new_number_for_move) && !move_to_move.equals(training_plan_move)) {
						training_plan_move.setMoveNumber(orig_number); // do the swap
						training_plan_move.save();
					}
				}
				
				workout_obj.invalidate();
				writer.println(this.toJSON(logged_in_person, current_training_plan, false, false));
				
			} else if (command.equals("moveDown")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				if (!current_training_plan.canEdit(logged_in_person)) {
					throw new IllegalValueException("Unable to move down.  You don't have permission to update moves in this training plan.");
				}
				TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				Vector moves = workout_obj.getMoves();
				
				TrainingPlanMove move_to_move = TrainingPlanMove.getTrainingPlanMove(Long.parseLong(arg2));
				short orig_number = move_to_move.getMoveNumber();
				if (orig_number == (short)moves.size()) {
					throw new IllegalValueException("Unable to move down.");
				}
				short new_number_for_move = (short)( move_to_move.getMoveNumber() + 1 );
				move_to_move.setMoveNumber(new_number_for_move);
				move_to_move.save();
				
				Iterator itr = moves.iterator();
				while (itr.hasNext()) {
					TrainingPlanMove training_plan_move = (TrainingPlanMove)itr.next();
					if ( (training_plan_move.getMoveNumber() == new_number_for_move) && !move_to_move.equals(training_plan_move)) {
						training_plan_move.setMoveNumber(orig_number); // do the swap
						training_plan_move.save();
					}
				}
				
				workout_obj.invalidate();
				writer.println(this.toJSON(logged_in_person, current_training_plan, false, false));
				
			} else if (command.equals("moveWorkoutUp")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				
				if (!current_training_plan.canEdit(logged_in_person)) {
					throw new IllegalValueException("Unable to move up.  You don't have permission to modify training plan " + current_training_plan.getLabel());
				}
		
				//TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);
				
				TrainingPlanWorkout workout_to_move = TrainingPlanWorkout.getTrainingPlanWorkout(Long.parseLong(arg2));
				short orig_number = workout_to_move.getWorkoutNumber();
				if (orig_number == (short)1) {
					throw new IllegalValueException("Unable to move up.");
				}
				short new_number_for_workout = (short)( orig_number - 1 );
				System.out.println("new_number_for_workout >" + new_number_for_workout);
				workout_to_move.setWorkoutNumber(new_number_for_workout);
				workout_to_move.save();
				
				//Iterator itr = workout_obj.getMoves().iterator();
				Iterator itr = current_training_plan.getWorkouts().iterator();
				while (itr.hasNext()) {
					TrainingPlanWorkout training_plan_workout = (TrainingPlanWorkout)itr.next();
					if ( (training_plan_workout.getWorkoutNumber() == new_number_for_workout) &&
							!workout_to_move.equals(training_plan_workout)) {
						training_plan_workout.setWorkoutNumber(orig_number); // do the swap
						training_plan_workout.save();
					}
				}
				
				current_training_plan.invalidate();
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Workout moved\"}]}");
				
			} else if (command.equals("moveWorkoutDown")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				
				if (!current_training_plan.canEdit(logged_in_person)) {
					throw new IllegalValueException("Unable to move down.  You don't have permission to modify training plan " + current_training_plan.getLabel());
				}
				
				Vector workouts = current_training_plan.getWorkouts();
				
				TrainingPlanWorkout workout_to_move = TrainingPlanWorkout.getTrainingPlanWorkout(Long.parseLong(arg2));
				short orig_number = workout_to_move.getWorkoutNumber();
				if (orig_number == (short)workouts.size()) {
					throw new IllegalValueException("Unable to move down.");
				}
				short new_number_for_workout = (short)( orig_number + 1 );
				workout_to_move.setWorkoutNumber(new_number_for_workout);
				workout_to_move.save();
				
				Iterator itr = workouts.iterator();
				while (itr.hasNext()) {
					TrainingPlanWorkout training_plan_workout = (TrainingPlanWorkout)itr.next();
					if ( (training_plan_workout.getWorkoutNumber() == new_number_for_workout) &&
							!workout_to_move.equals(training_plan_workout)) {
						training_plan_workout.setWorkoutNumber(orig_number); // do the swap
						training_plan_workout.save();
					}
				}
				
				current_training_plan.invalidate();
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Workout moved\"}]}");
				
			} else if (command.equals("addSetX")) {
				
				writer.println(this.addSet(FitnessServlet.verifyKey(arg1), arg2));
				
			} else if (command.equals("addSet")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlanMove move = TrainingPlanMove.getTrainingPlanMove(Long.parseLong(arg2));
				if (!move.getParent().getParent().canEdit(logged_in_person)) {
					throw new IllegalValueException("Unable to add set.  You don't have permission to modify training plan " + move.getParent().getParent().getLabel());
				}
				
				TrainingPlanSet set = new TrainingPlanSet();
				
				boolean is_asrap = false;
				if (arg7 != null && arg7.equals("true")) {
					is_asrap = true;
				} else {
					if (arg3 == null || arg3.isEmpty()) {
						throw new IllegalValueException("Please specify # of reps.");
					}
				}
				short num_reps = (short)0;
				if (arg3 != null && !arg3.isEmpty()) {
					num_reps = Short.parseShort(arg3);
				}
				short num_reps_max = (short)0;
				if (arg4 != null && !arg4.isEmpty()) {
					num_reps_max = Short.parseShort(arg4);
				}
				boolean is_drop_set = false;
				if (arg6 != null && arg6.equals("true")) {
					is_drop_set = true;
				}
				boolean is_aswap = false;
				if (arg8 != null && arg8.equals("true")) {
					is_aswap = true;
				}
				boolean is_perc_of_1rm = false;
				if (arg9 != null && arg9.equals("true")) {
					is_perc_of_1rm = true;
				}
				
				set.setParent(move);
				set.setIsDropSet(is_drop_set);
				set.setIsAMRAP(is_asrap);
				set.setIsAMWAP(is_aswap);
				set.setIsPerc1RM(is_perc_of_1rm);
				
				Vector sets = move.getSets();
				set.setSetNumber((short)sets.size());
				
				set.save();
				set.invalidate();
				
				// the following 3 sets require that the set already be saved
				if (num_reps > 0) {
					set.setRepGoalMin(logged_in_person, num_reps);
				}
				if (num_reps_max > 0) {
					set.setRepGoalMax(logged_in_person, num_reps_max);
				}
				if (arg5 != null && !arg5.isEmpty()) {
					if (is_perc_of_1rm) {
						set.setOneRepMaxPercentage(new BigDecimal(arg5));
					} else {
						set.setWeightGoal(logged_in_person, new BigDecimal(arg5));
					}
				}
				
				set.save();
				
				sets.addElement(set);
				
				move.save();
				
				writer.println(this.toJSON(logged_in_person, move.getParent().getParent(), false, false));
				
			} else if (command.equals("removeSet")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				
				if (!current_training_plan.canEdit(logged_in_person)) {
					throw new IllegalValueException("Unable to remove set.  You don't have permission to modify training plan " + current_training_plan.getLabel());
				}
				
				TrainingPlanSet set_to_remove = TrainingPlanSet.getTrainingPlanSet(Long.parseLong(arg2));
				
				TrainingPlanSet.delete(Long.parseLong(arg2));
				set_to_remove.getParent().invalidate();
				writer.println(this.toJSON(logged_in_person, current_training_plan, false, false));
				
			} else if (command.equals("auth")) {
				writer.println(this.auth(arg1, session));
			} else if (command.equals("setMacroGoal")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				UKOnlinePersonBean clientPerson = null;
				if (arg3 != null && !arg3.isEmpty()) {
					clientPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
					if (!logged_in_person.supervises(clientPerson)) {
						throw new IllegalValueException("Unable to set macro goal.  You're not the trainer for this person.");
					}
				}
				
				writer.println(this.setMacroGoal(clientPerson == null ? logged_in_person : clientPerson, arg2));
				
			} else if (command.equals("activateMacroGoal")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				UKOnlinePersonBean clientPerson = null;
				if (arg3 != null && !arg3.isEmpty()) {
					clientPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
					if (!logged_in_person.supervises(clientPerson)) {
						throw new IllegalValueException("Unable to set macro goal.  You're not the trainer for this person.");
					}
				}

				MacroGoal goal_obj = MacroGoal.getMacroGoal(Integer.parseInt(arg2));
				goal_obj.setCreateOrModifyPerson(logged_in_person);

				boolean has_current_macro_goal = MacroGoal.hasCurrentMacroGoal(clientPerson == null ? logged_in_person : clientPerson);
				MacroGoal current_goal = null;
				if (has_current_macro_goal) {
					current_goal = MacroGoal.getCurrentMacroGoal(clientPerson == null ? logged_in_person : clientPerson);

					if (!current_goal.equals(goal_obj)) {
						current_goal.inActivate();
						current_goal.save();
						goal_obj.save();

						// if there are daily entries, move them to the new goal
						System.out.println("moving dailies...");
						/*
						Iterator itr = MacroGoalDaily.getMacroGoalDailys(current_goal).iterator();
						while (itr.hasNext()) {
							MacroGoalDaily daily_obj = (MacroGoalDaily)itr.next();
							daily_obj.setMacroGoal(goal_obj);
							daily_obj.save();
						}
						*/
						

						try {
							MacroGoalDaily current_daily = MacroGoalDaily.getCurrentMacroGoalDaily(clientPerson == null ? logged_in_person : clientPerson);
							current_daily.setMacroGoal(goal_obj);
							current_daily.save();
						} catch (ObjectNotFoundException x) {
							x.printStackTrace();
						}
					}
				}
				goal_obj.setActive(true);
				goal_obj.save();

				writer.println(this.toJSON(MacroGoalDaily.getCurrentMacroGoalDaily(clientPerson == null ? logged_in_person : clientPerson)));
		
			} else if (command.equals("setWeightGoal")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				UKOnlinePersonBean clientPerson = null;
				if (arg3 != null && !arg3.isEmpty()) {
					clientPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
					if (!logged_in_person.supervises(clientPerson)) {
						throw new IllegalValueException("Unable to set weight goal.  You're not the trainer for this person.");
					}
				}
				
				writer.println(this.setWeightGoal(clientPerson == null ? logged_in_person : clientPerson, arg2));
				
			} else if (command.equals("verifyEmail")) {
				writer.println(this.verifyEmail(arg1));
			} else if (command.equals("verifyEmailResponse")) {
				
				String decryptedStr = EncryptionUtils.decryptString(arg1);
				System.out.println("confirmed email >" + decryptedStr);
				confirmed_emails.addElement(decryptedStr);
				StringBuilder b = new StringBuilder();
				b.append("<!DOCTYPE html>");
				b.append("<html>");
				b.append("<head><title>Email Verification</title></head>");
				b.append("<body><h1>Email verified.  You can close this page.</h1></body>");
				b.append("</html>");
				
				_response.setContentType("text/html");
				writer.println(b.toString());
				
			} else if (command.equals("procStep1")) {
				writer.println(this.procWizardStep1(arg1));
			} else if (command.equals("procStep2")) {
				writer.println(this.procWizardStep2(arg1));
			} else if (command.equals("procStep3")) {
				writer.println(this.procWizardStep3(arg1));
			} else if (command.equals("procFinish")) {
				writer.println(this.procWizardFinish(arg1, session));
			} else if (command.equals("saveProfile")) {
				
				UKOnlinePersonBean logged_in_person = null;
				try {
					logged_in_person = FitnessServlet.verifyKey(arg1);
				} catch (ObjectNotFoundException x) {	}
				writer.println(this.saveProfile(logged_in_person, arg2, arg1));
				
			} else if (command.equals("getProfile")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				//writer.println(this.setWeightGoal(logged_in_person, arg2));

				writer.println(this.toJSON(logged_in_person, arg1));
				
			} else if (command.equals("getTrainingPlan")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				if (arg4 != null) {
					
					TrainingPlanWorkout selected_training_plan_workout = TrainingPlanWorkout.getTrainingPlanWorkout(Long.parseLong(arg4));
					if (selected_training_plan_workout.getParent().equals(current_training_plan)) {
						current_training_plan.setCurrentTrainingPlanWorkout(logged_in_person, selected_training_plan_workout);
					} else {
						current_training_plan.previousWorkout(logged_in_person);
					}
				} else if (arg2 != null) {
					if (arg2.equals("n")) {
						//current_training_plan.nextWorkout(logged_in_person);
						current_training_plan.nextWeek(logged_in_person);
					} else if (arg2.equals("p")) {
						
						//current_training_plan.previousWorkout(logged_in_person);
						
						/*
						Date dt_for_previous = null;
						if ( (arg3 == null) || arg3.isEmpty() ) {
							dt_for_previous = new Date();
						} else {
							Workout focused_workout = Workout.getWorkout(Integer.parseInt(arg3));
							System.out.println("focused_workout >" + focused_workout.getLabel());
							dt_for_previous = focused_workout.getEndDate();
						}
						
						// get the workout done previous to this
						try {
							Workout previous_workout = Workout.getLastWorkout(logged_in_person, dt_for_previous);
							System.out.println("previous_workout >" + previous_workout.getLabel());
							if (previous_workout.getParent().getParent().equals(current_training_plan)) {
								current_training_plan.setCurrentTrainingPlanWorkout(logged_in_person, previous_workout.getParent());
							} else {
								current_training_plan.previousWorkout(logged_in_person);
							}
						} catch (ObjectNotFoundException x) {
							current_training_plan.previousWorkout(logged_in_person);
						}
						*/
						//current_training_plan.previousWorkout(logged_in_person);
						current_training_plan.previousWeek(logged_in_person);
					} else if (arg2.equals("nw")) {
						//current_training_plan.nextWeek(logged_in_person);
						current_training_plan.nextBlock(logged_in_person);
					} else if (arg2.equals("pw")) {
						//current_training_plan.previousWeek(logged_in_person);
						current_training_plan.previousBlock(logged_in_person);
					}
				}
				
				writer.println(this.toJSON(logged_in_person, current_training_plan, false, false));
				
			} else if (command.equals("getTrainingPlans")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				UKOnlinePersonBean clientPerson = null;
				if (arg3 != null && !arg3.isEmpty()) {
					clientPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
					if (!logged_in_person.supervises(clientPerson)) {
						throw new IllegalValueException("Unable to get training plans.  You're not the trainer for this person.");
					}
				}
				
				boolean is_copy = false;
				if (arg4 != null && arg4.equals("true")) {
					is_copy = true;
				}
				
				TrainingPlan selected_training_plan = null;
				
				//TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				Vector plans = null;
				if (clientPerson == null) {
					plans = TrainingPlan.getTrainingPlans(logged_in_person);
				} else {
					plans = TrainingPlan.getTrainingPlans(logged_in_person, clientPerson);
				}
				
				if (arg2 != null && !arg2.isEmpty() && !arg2.equals("0")) {
					selected_training_plan = TrainingPlan.getTrainingPlan(Integer.parseInt(arg2));
					if (!plans.contains(selected_training_plan)) {
						throw new IllegalValueException("Unable to select plan.");
					}
				}
				
				writer.println(this.toJSON(selected_training_plan, plans, clientPerson == null ? logged_in_person : clientPerson, is_copy, ( arg2 != null && arg2.equals("0") ) ));
				
			} else if (command.equals("saveTrainingPlan")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				UKOnlinePersonBean clientPerson = null;
				if (arg3 != null && !arg3.isEmpty()) {
					clientPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
					if (!logged_in_person.supervises(clientPerson)) {
						throw new IllegalValueException("Unable to save training plan.  You're not the trainer for this person.");
					}
				}
				
				writer.println(this.saveTrainingPlan(clientPerson == null ? logged_in_person : clientPerson, arg2));
				
			} else if (command.equals("activatePlan")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				UKOnlinePersonBean clientPerson = null;
				if (arg3 != null && !arg3.isEmpty()) {
					clientPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg3));
					if (!logged_in_person.supervises(clientPerson)) {
						throw new IllegalValueException("Unable to save training plan.  You're not the trainer for this person.");
					}
				}
				
				boolean isCurrent = true;
				
				TrainingPlan plan = TrainingPlan.getTrainingPlan(Integer.parseInt(arg2));

				Iterator itr = TrainingPlan.getTrainingPlans(clientPerson == null ? logged_in_person : clientPerson).iterator();
				while (itr.hasNext()) {
					TrainingPlan obj = (TrainingPlan)itr.next();
					if ( obj.isActive(clientPerson == null ? logged_in_person : clientPerson) && !obj.equals(plan) ) {
						if (isCurrent) {
							obj.setActive(clientPerson == null ? logged_in_person : clientPerson, false);
							//obj.save(); 8/11/18 - saved in mapping
						}
					}
				}

				plan.setActive(clientPerson == null ? logged_in_person : clientPerson, isCurrent); // this saves a mapping

				writer.println(this.toJSON(clientPerson == null ? logged_in_person : clientPerson, plan, false, false));				
				
			} else if (command.equals("getWorkouts")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);

				TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(logged_in_person);
				//System.out.println("FOUND CURRENT TRAINING PLAN >" + current_training_plan.getLabel());
				TrainingPlanWorkout selected_workout = null;
				
				/*
				if (arg2 != null && !arg2.isEmpty() && !arg2.equals("0")) {
					selected_training_plan = TrainingPlan.getTrainingPlan(Integer.parseInt(arg2));
					if (!plans.contains(selected_training_plan)) {
						throw new IllegalValueException("Unable to select plan.");
					}
				}
				*/
				
				Vector workouts = current_training_plan.getWorkouts();
				if (arg2 != null && !arg2.isEmpty() && !arg2.equals("0")) {
					selected_workout = TrainingPlanWorkout.getTrainingPlanWorkout(Long.parseLong(arg2));
					if (!workouts.contains(selected_workout)) {
						throw new IllegalValueException("Unable to select workout.");
					}
				}
				
				boolean is_copy = false;
				if (arg3 != null && arg3.equals("true")) {
					is_copy = true;
				}
				
				writer.println(this.toJSON(selected_workout, is_copy, workouts, ( arg2 != null && arg2.equals("0") )));
				
			} else if (command.equals("getMoves")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				short move_type = 0;
				if (arg2 != null) {
					move_type = Short.parseShort(arg2);
				}
				
				StringBuilder b = new StringBuilder();
				b.append("{\"move\":[");
				Iterator<Move> itr = Move.getMoves();
				boolean needs_comma = false;
				while (itr.hasNext()) {
					Move move_obj = itr.next();
					if (move_obj.canView(logged_in_person)) {
						
						boolean display = true;
						if (move_type > 0) {
							switch (move_type) {
								case (short)1: display = move_obj.isCompoundLift(); break;  // compound lift
								case (short)2: display = ( move_obj.isLift() && !move_obj.isCompoundLift() ); break; // other lift
								case (short)3: display = move_obj.isCardio(); break; // cardio
							}
						}
						
						if (display) {
							if (needs_comma) { b.append(','); }
							b.append(this.toJSON(move_obj));
							needs_comma = true;
						}
					}
				}
				b.append("]}");
				writer.println(b.toString());
				
			} else if (command.equals("selectMove")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				
				Move selected_move = Move.getMove(Integer.parseInt(arg2));
				System.out.println("selected_move >" + selected_move);
				writer.println(this.toJSON(logged_in_person, selected_move));
				
			} else if (command.equals("personSearch")) {
				
				// verify the key
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				//UKOnlineCompanyBean selected_bu = (UKOnlineCompanyBean)logged_in_person.getSelectedCompany();
				PracticeAreaBean selected_practice_area = null;
				
				//int active_filter = Integer.parseInt(arg3);
				
				int limit = 50;
				if (arg4 != null && !arg4.isEmpty()) {
					limit = Integer.parseInt(arg4);
				}

				String sort = null;
				if (arg5 != null && !arg5.isEmpty()) {
					sort = arg5;
				}

				boolean show_details = false;
				if (arg6 != null && !arg6.isEmpty()) {
					show_details = true;
				}
				
				Vector vec = new Vector();
				
				//if (arg2 != null) {
					vec = UKOnlinePersonBean.getManagedByPersonsByKeyword(logged_in_person, arg2, limit);
				//}
				
				StringBuffer b = new StringBuffer();
				b.append("{\"user\":[");
				Iterator itr = vec.iterator();
				while (itr.hasNext()) {
					UKOnlinePersonBean person = (UKOnlinePersonBean)itr.next();
					b.append(this.toJSON(person));
					b.append(itr.hasNext() ? "," : "");
				}
				b.append("]}");
				
				//System.out.println("retStr >" + b.toString());
				writer.println(b.toString());
				
			} else if (command.equals("addClient")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				this.saveClient(logged_in_person, arg2);
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Client Added\"}]}");
				
			} else if (command.equals("sim")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				UKOnlinePersonBean person_to_sim = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(arg2));
				if (!logged_in_person.supervises(person_to_sim)) {
					throw new IllegalValueException("Unable to simulate.  You're not the trainer for this person.");
				}
				
				if (sim_hash == null) {
					sim_hash = new HashMap<String, UKOnlinePersonBean>(11);
				}
				sim_hash.put(arg1, person_to_sim);
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Simulating " + person_to_sim.getLabel() + "\"}]}");
				
			} else if (command.equals("simEnd")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				if (sim_hash != null) {
					sim_hash.remove(arg1);
				}
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Simulation ended\"}]}");
				
			} else if (command.equals("verifyVID")) {
				
				//String str_to_encrypt = inputEmail + "~~|~~" + inputPassword + "~~|~~" + defaultUnit;

				String decryptedStr = EncryptionUtils.decryptString(arg1);
				
				// split the string
				
				String decryptedEmail = null;
				String decryptedPassword = null;
				String decryptedUnits = null;
				
				String[] parts = decryptedStr.split("~~|~~");
				for (int i = 0; i < parts.length; i++) {
					switch (i) {
						case 0: decryptedEmail = parts[i]; break;
						case 2: decryptedPassword = parts[i]; break;
						case 4: decryptedUnits = parts[i]; break;
					}
				}
				
				System.out.println("decryptedEmail: " + decryptedEmail);
				System.out.println("decryptedPassword: " + decryptedPassword);
				System.out.println("decryptedUnits: " + decryptedUnits);
				
				// does a person with this email address already exist?
				
				try {
					UKOnlinePersonBean.getPersonByEmail(decryptedEmail);
					//throw new IllegalValueException("A person with email address " + decryptedEmail + " already exists.");
				} catch (ObjectNotFoundException x) {
					UKOnlinePersonBean confirmed_person = new UKOnlinePersonBean();
					confirmed_person.setUsername(decryptedEmail);
					confirmed_person.setEmail1(decryptedEmail);
					confirmed_person.setPassword(decryptedPassword);
					confirmed_person.setConfirmPassword(decryptedPassword);
					confirmed_person.save();

					WeightPreferences new_preferences = WeightPreferences.getWeightPreference(confirmed_person);
					new_preferences.setUnitType(decryptedUnits.equals("lbs") ? WeightPreferences.LBS_TYPE : WeightPreferences.KG_TYPE  );
					new_preferences.save();
				}
				
				writer.println(this.auth(decryptedEmail, decryptedPassword, session));
				
				//writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Client Verified\"}]}");
				
			} else if (command.equals("getTrained")) {
				
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				this.getTrained(logged_in_person, arg2);
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Training request submitted.\"}]}");
				
			} else if (command.equals("newGoogleSheetAvailable")) {
				System.out.println("newGoogleSheetAvailable >" + arg1);
				
				// UnusedGoogleSheetDbPeer
				
				UnusedGoogleSheetDb obj = new UnusedGoogleSheetDb();
				obj.setGoogleSheetId(arg1);
				obj.save();
				
			} else if (command.equals("viewGoogleSheet")) {
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan selectedTrainingPlan = TrainingPlan.getTrainingPlan(Integer.parseInt(arg2));
				System.out.println("found training plan >" + selectedTrainingPlan.getLabel());
				
				// get the mapping for this person/trainingplan
				
				FitnessServlet.updateSheetFromTrainingPlan(logged_in_person, selectedTrainingPlan, arg1);
				
				String sheet_url_str = "https://docs.google.com/spreadsheets/d/" + selectedTrainingPlan.getGoogleSheetId(logged_in_person) + "/edit#gid=1007878034";
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"url\":\"" + JSONObject.escape(sheet_url_str) + "\"}]}");
				
			} else if (command.equals("syncGoogleSheet")) {
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				TrainingPlan selectedTrainingPlan = TrainingPlan.getTrainingPlan(Integer.parseInt(arg2));
				//System.out.println("found training plan >" + selectedTrainingPlan.getLabel());
				
				// get the mapping for this person/trainingplan
				
				String sheet_id = selectedTrainingPlan.getGoogleSheetId(logged_in_person);
				
				Timer timerObj = timer_hash.get(sheet_id);
				System.out.println("timer found >" + timerObj);
				
				if (timerObj != null) {
					timerObj.cancel();
				}
				
				timerObj = new Timer(true);
				timer_hash.put(sheet_id, timerObj);
				
				Calendar now = Calendar.getInstance();
				now.add(Calendar.MINUTE, 1);
				timerObj.schedule(new UpdateTrainingPlanFromGoogleSheetTask(sheet_id), now.getTime());
				
				//writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"url\":\"" + JSONObject.escape(sheet_url_str) + "\"}]}");
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Please wait a few moments....\"}]}");
				
			} else if (command.equals("googleSheetEditEvent")) {
				
				/*
				System.out.println("googleSheetEditEvent >" + arg1);
				
				Timer timerObj = timer_hash.get(arg1);
				System.out.println("timer found >" + timerObj);
				
				if (timerObj != null) {
					timerObj.cancel();
				}
				
				timerObj = new Timer(true);
				timer_hash.put(arg1, timerObj);
				
				Calendar now = Calendar.getInstance();
				now.add(Calendar.MINUTE, 1);
				timerObj.schedule(new UpdateTrainingPlanFromGoogleSheetTask(arg1), now.getTime());
				*/
				
				FitnessServlet.updateTrainingPlanFromSpreadsheet(null);
				
			} else if (command.equals("updateSetReps")) {
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				Workout workout_obj = Workout.getWorkout(Long.parseLong(arg2));
				if (!workout_obj.getPerson().equals(logged_in_person)) {
					throw new IllegalValueException("Unable to update set...");
				}
				WorkoutSet set_being_updated = WorkoutSet.getSet(workout_obj, TrainingPlanSet.getTrainingPlanSet(Long.parseLong(arg3)));
				set_being_updated.setActualReps(Short.parseShort(arg4));
				set_being_updated.save();
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Actual set reps updated.\"}]}");
				
			} else if (command.equals("updateSetWeight")) {
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				Workout workout_obj = Workout.getWorkout(Long.parseLong(arg2));
				if (!workout_obj.getPerson().equals(logged_in_person)) {
					throw new IllegalValueException("Unable to update set...");
				}
				WorkoutSet set_being_updated = WorkoutSet.getSet(workout_obj, TrainingPlanSet.getTrainingPlanSet(Long.parseLong(arg3)));
				set_being_updated.setActualWeight(new BigDecimal(arg4));
				set_being_updated.save();
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Actual set weight updated.\"}]}");
				
			} else if (command.equals("updateSetRPE")) {
				UKOnlinePersonBean logged_in_person = FitnessServlet.verifyKey(arg1);
				Workout workout_obj = Workout.getWorkout(Long.parseLong(arg2));
				if (!workout_obj.getPerson().equals(logged_in_person)) {
					throw new IllegalValueException("Unable to update set...");
				}
				WorkoutSet set_being_updated = WorkoutSet.getSet(workout_obj, TrainingPlanSet.getTrainingPlanSet(Long.parseLong(arg3)));
				set_being_updated.setActualRPE(new BigDecimal(arg4));
				set_being_updated.save();
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Actual set RPE updated.\"}]}");
				
			} else if (command.equals("testNutritionixEndpoint")) {
				
				System.out.println("testNutritionixEndpoint invoked");

				URL url = null;
				try { 
					url = new URL("https://trackapi.nutritionix.com/v2/search/instant?query=turkey");
					//url = new URL("https://trackapi.nutritionix.com/v2/search/item?nix_item_id=513fc9e73fe3ffd40300109f");
					//url = new URL("https://trackapi.nutritionix.com/v2/natural/nutrients");
					
				} catch (MalformedURLException e) {
					System.out.println("ERROR: invalid URL ");
					return;
				}

				String line;
				String result = "";

				try {
					// try opening the URL
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.setRequestMethod("GET");
					conn.setRequestProperty("x-app-key", "cb67688bed25360261caed9b4a698b64");
					conn.setRequestProperty("x-app-id", "6cbb9f70");
					//conn.setRequestProperty("Content-Type","application/json");
					
					BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					while ((line = rd.readLine()) != null) {
					   result += line;
					}
					rd.close();

				}
				catch (IOException e) {
					System.out.println("ERROR: couldn't open URL ");
				}

				System.out.println("result - >" + result);
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"testNutritionixEndpoint.\"}]}");
				
			} else if (command.equals("testNutritionixEndpoint2")) {
				
				System.out.println("testNutritionixEndpoint2 invoked");
				//System.out.println("RESULT >" + FitnessServlet.getJSON("https://trackapi.nutritionix.com/v2/natural/nutrients", "{\"query\":\"turkey ground\",\"timezone\": \"US/Eastern\"}", 5000, "POST"));
				
				//System.out.println("RESULT >" + FitnessServlet.getJSON("https://api.nal.usda.gov/ndb/V2/reports?ndbno=45364040&type=f&format=json&api_key=15IvtHBVZPtEWSgEdGisLrpYe79IdVU9uj7pM8oP", null, 5000, "GET"));
				
				//System.out.println("JSON RESULT >" + );
				
				long offset = Food.getStartOffset();
				offset++;
				boolean keep_doing_stuff = true;
				while (keep_doing_stuff) {
					keep_doing_stuff = FitnessServlet.procUSDAList(FitnessServlet.getJSON("https://api.nal.usda.gov/ndb/list?format=json&lt=f&sort=n&api_key=15IvtHBVZPtEWSgEdGisLrpYe79IdVU9uj7pM8oP&max=100&offset=" + offset, null, 5000, "GET"));
					offset += 100;
				}
				
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"testNutritionixEndpoint.\"}]}");
				
			} else if (command.equals("testNutritionixEndpoint3")) {
				
				System.out.println("testNutritionixEndpoint3 invoked");
				
				// grab a bunch of foods that need nutrition data...
				
				Iterator itr = Food.getFoodsThatNeedUSDASyncing(3000).iterator();
				while (itr.hasNext()) {
					Food fud = (Food)itr.next();
					System.out.println("found food to sync >" + fud.getLabel());
					FitnessServlet.procUSDAItem(FitnessServlet.getJSON("https://api.nal.usda.gov/ndb/V2/reports?ndbno=" + fud.getNDbNo() + "&type=f&format=json&api_key=15IvtHBVZPtEWSgEdGisLrpYe79IdVU9uj7pM8oP", null, 5000, "GET"));
				}
				
				//System.out.println("JSON RESULT >" + this.procUSDAItem(null));
				writer.println("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"testNutritionixEndpoint.\"}]}");
				
			} else {
				throw new IllegalValueException("Command not implemented >" + command);
			}
			
		} catch (Exception x) {
			x.printStackTrace();
			/*
			StringBuffer b = new StringBuffer();
			b.append("{\"message\": {");
			b.append(" \"type\": \"ERROR\",");
			b.append(" \"heading\": \"Oh Snap!\",");
			b.append(" \"text\": \"" + JSONObject.escape(x.getMessage()) + "\"");
			b.append("}}");
			*/
			
			StringBuffer b = new StringBuffer();
			b.append("{\"message\":[");
			b.append("{\"type\":\"danger\"," +
					"\"heading\":\"Oh snap!\"," +
					"\"text\":\"" + JSONObject.escape(x.getMessage()) + "\"}");
			b.append("]}");
			
			System.out.println(b.toString());
			
			_response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			writer.println(b.toString());
		} finally {			
			writer.close();
		}
	}
	
	public static String getJSON(String url, String json, int timeout, String method) {
		URLConnection connection = null;
		try {
			
			
			



			// Create a trust manager that does not validate certificate chains
			final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
				}
				@Override
				public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
				}
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} };

			// Install the all-trusting trust manager
			final SSLContext sslContext = SSLContext.getInstance( "SSL" );
			sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
			// Create an ssl socket factory with our all-trusting manager
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


			/*
			// All set up, we can get a resource through https now:
			final URLConnection urlCon = new URL( "https://ahconnect.anoka.k12.mn.us/idp" ).openConnection();
			// Tell the url connection object to use our socket factory which bypasses security checks
			( (HttpsURLConnection) urlCon ).setSSLSocketFactory( sslSocketFactory );


			( (HttpsURLConnection) urlCon ).setHostnameVerifier(new javax.net.ssl.HostnameVerifier()
			{      
				public boolean verify(String hostname, javax.net.ssl.SSLSession session)
				{
				System.out.println("stuff2 - gonna verify");
					return true;
				}
			});
			*/
			
			
			

			//URL u = new URL(url);
			//connection = (HttpURLConnection) u.openConnection();
			
			
			// All set up, we can get a resource through https now:
			connection = new URL( url ).openConnection();
			// Tell the url connection object to use our socket factory which bypasses security checks
			( (HttpsURLConnection) connection ).setSSLSocketFactory( sslSocketFactory );


			( (HttpsURLConnection) connection ).setHostnameVerifier(new javax.net.ssl.HostnameVerifier()
			{      
				public boolean verify(String hostname, javax.net.ssl.SSLSession session)
				{
				System.out.println("stuff2 - gonna verify");
					return true;
				}
			});
			
			
			
			( (HttpsURLConnection) connection ).setRequestMethod(method);

			//connection.setRequestProperty("x-app-key", "cb67688bed25360261caed9b4a698b64");
			//connection.setRequestProperty("x-app-id", "6cbb9f70");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");

			connection.setAllowUserInteraction(false);
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);

			if (json != null) {
				//set the content length of the body
				connection.setRequestProperty("Content-length", json.getBytes().length + "");
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);

				//send the json as body of the request
				OutputStream outputStream = connection.getOutputStream();
				outputStream.write(json.getBytes("UTF-8"));
				outputStream.close();
			}

			//Connect to the server
			connection.connect();

			int status = ( (HttpsURLConnection) connection ).getResponseCode();
			System.out.println("HTTP status code : " + status);
			switch (status) {
				case 200:
				case 201:
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						sb.append(line + "\n");
					}
					bufferedReader.close();
					System.out.println("Received String : " + sb.toString());
					//return received string
					return sb.toString();
			}

		} catch (MalformedURLException ex) {
			System.out.println("Error in http connection" + ex.toString());
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("Error in http connection" + ex.toString());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Error in http connection" + ex.toString());
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					( (HttpsURLConnection) connection ).disconnect();
				} catch (Exception ex) {
					System.out.println("Error in http connection" + ex.toString());
					ex.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static UKOnlinePersonBean
	verifyKey(String _key) throws TorqueException, IllegalValueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		//System.out.println("verifyKey >" + auth_key_hash + " >" + auth_key_hash.size());
		if (auth_key_hash.containsKey(_key)) {
			if ( (sim_hash != null) && sim_hash.containsKey(_key) ) {
				System.out.println("found sim");
				return sim_hash.get(_key);
			}
			return auth_key_hash.get(_key);
		} else {
			
			UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(_key);
			
			// allow
			/*
			UKOnlineLoginBean loginBean = new UKOnlineLoginBean();
			((UKOnlineLoginBean)loginBean).setUsername(_key);
			((UKOnlineLoginBean)loginBean).setPassword(_key);
			UKOnlinePersonBean person = (UKOnlinePersonBean)((UKOnlineLoginBean)loginBean).getPerson();
			*/
			
			System.out.println("found person >" + person.getId());
			
			if (person == null) {
				throw new IllegalValueException("Unable to verify session key >" + _key + ".  Please sign out and then sign back in.");
			}
			
			auth_key_hash.put(_key, person);
			
			return person;
		}
	}
	
	public static Vector
	getKeysForPerson(UKOnlinePersonBean _person) throws IllegalValueException {
		
		Vector vec = new Vector();
		Iterator itr = auth_key_hash.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String)itr.next();
			UKOnlinePersonBean person_for_key = auth_key_hash.get(key);
			if (person_for_key.equals(_person)) {
				vec.addElement(key);
			}
		}
		return vec;
	}
	
	private String toJSON(Move _move) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_move.getLabel()) + "\",");
		b.append("\"id\":" + _move.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _person, Move _move) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_move.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_move.getDescription()) + "\",");
		b.append("\"videoURL\":\"" + JSONObject.escape(_move.getVideoURL()) + "\",");
		
		String prString = _move.getPRString(_person);
		if ( (prString != null) && !prString.isEmpty() ) {
			b.append("\"pr\":\"" + prString + "\",");
		}
				
		b.append("\"type\":" + _move.getType() + ",");
		b.append("\"isActive\":" + _move.isActive() + ",");
		b.append("\"isPublic\":" + _move.isPublic() + ",");
		b.append("\"isCompound\":" + _move.isCompoundLift() + ",");
		b.append("\"id\":" + _move.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _person, String _key) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {

		WeightPreferences preferences = WeightPreferences.getWeightPreference(_person);
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_person.getLabel()) + "\",");
		b.append("\"isAdmin\":" + _person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) + ",");
		b.append("\"isTrainer\":" + _person.hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME) + ",");
		b.append("\"email\":\"" + _person.getEmail1String() + "\",");
		b.append("\"password\":\"" + ( _person.getPasswordString().equals(_key) ? "" : _person.getPasswordString() ) + "\",");
		b.append("\"unitType\":" + preferences.getUnitType() + ",");
		b.append("\"first\":\"" + _person.getFirstNameString() + "\",");
		b.append("\"last\":\"" + _person.getLastNameString() + "\",");
		b.append("\"gender\":" + preferences.getGender() + ",");
		b.append("\"weight\":\"" + preferences.getWeightString() + "\",");
		b.append("\"age\":\"" + preferences.getAgeString() + "\",");
		b.append("\"height1\":\"" + preferences.getHeightPrimaryUnitString() + "\",");
		b.append("\"height2\":\"" + preferences.getHeightSecondaryUnitString() + "\",");
		b.append("\"activityLevel\":\"" + preferences.getActivityLevelString() + "\",");
		b.append("\"key\":\"" + _key + "\",");
		b.append("\"id\":" + _person.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_person.getLabelWithEmail()) + "\",");
		
		try {
			MacroGoal macro_goal = MacroGoal.getCurrentMacroGoal(_person);
			b.append("\"calories\":\"" + macro_goal.getCaloriesString() + "\",");
			b.append("\"fat\":\"" + macro_goal.getFatString() + "\",");
			b.append("\"carbs\":\"" + macro_goal.getCarbsString() + "\",");
			b.append("\"protein\":\"" + macro_goal.getProteinString() + "\",");
		} catch (Exception x) {  }
		
		try {
			WeightGoal weight_goal = WeightGoal.getCurrentWeightGoal(_person);
			b.append("\"weight\":\"" + weight_goal.getCurrentWeightString() + "\",");
		} catch (Exception x) {  }
		
		try {
			Workout last_workout = Workout.getLastWorkout(_person);
			b.append("\"lastWorkoutName\":\"" + last_workout.getLabel() + "\",");
			WeightPreferences preferences = WeightPreferences.getWeightPreference(_person);
			b.append("\"lastWorkoutDate\":\"" + CUBean.getUserDateString(last_workout.getStartDate(), preferences.getTimeZone()) + "\",");
		} catch (Exception x) {  }
		
		/*
		b.append("\"empId\":\"" + _person.getEmployeeNumberString() + "\",");
		b.append("\"active\":" + _person.isActive() + ",");
		b.append("\"lastLogIn\":\"" + _person.getLastLogInDateString() + "\",");
		b.append("\"role\":\"" + JSONObject.escape(_person.getRolesString()) + "\",");
		b.append("\"location\":\"" + JSONObject.escape(_person.getDepartmentNameString()) + "\",");
		b.append("\"title\":\"" + JSONObject.escape(_person.getJobTitleString()) + "\",");
		*/
		b.append("\"key\":\"" + _person.getUsernameString() + "\",");
		b.append("\"id\":" + _person.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(MacroGoalDaily daily_goal) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		
		MacroGoal _goal = daily_goal.getParent();
		
		String fat_cals_str = daily_goal.getFat().multiply(MacroGoal.CALORIES_PER_GRAM_OF_FAT).setScale(0, RoundingMode.HALF_UP).toString();
		String carbs_cals_str = daily_goal.getCarbs().multiply(MacroGoal.CALORIES_PER_GRAM_OF_CARB).setScale(0, RoundingMode.HALF_UP).toString();
		String protein_cals_str = daily_goal.getProtein().multiply(MacroGoal.CALORIES_PER_GRAM_OF_PROTEIN).setScale(0, RoundingMode.HALF_UP).toString();
		
		b.append("{\"label\":\"" + JSONObject.escape(daily_goal.getLabel()) + "\",");
		b.append("\"personLabel\":\"" + _goal.getPerson().getLabel() + "\",");
		b.append("\"personId\":" + _goal.getPerson().getId() + ",");
		b.append("\"active\":" + _goal.isActive() + ",");
		b.append("\"caloriesGoal\":" + _goal.getCaloriesString() + ",");
		b.append("\"caloriesToday\":" + daily_goal.getCaloriesString() + ",");
		b.append("\"caloriesPerc\":" + daily_goal.getCaloriesPercentageString() + ",");
		b.append("\"fat\":" + _goal.getFatString() + ",");
		b.append("\"fatToday\":" + daily_goal.getFatString() + ",");
		b.append("\"fatPerc\":" + daily_goal.getFatPercentageString() + ",");
		b.append("\"fatCals\":" + fat_cals_str + ",");
		b.append("\"carbs\":" + _goal.getCarbsString() + ",");
		b.append("\"carbsToday\":" + daily_goal.getCarbsString() + ",");
		b.append("\"carbsPerc\":" + daily_goal.getCarbsPercentageString() + ",");
		b.append("\"carbsCals\":" + carbs_cals_str + ",");
		b.append("\"protein\":" + _goal.getProteinString() + ",");
		b.append("\"proteinToday\":" + daily_goal.getProteinString() + ",");
		b.append("\"proteinPerc\":" + daily_goal.getProteinPercentageString() + ",");
		b.append("\"proteinCals\":" + protein_cals_str + ",");
		b.append("\"type\":" + _goal.getGoalType() + ",");
		Calendar entryDate = Calendar.getInstance(); // time zone issue??
		entryDate.setTime(daily_goal.getEntryDate());
		b.append("\"mn\":" + entryDate.get(Calendar.MONTH) + ",");
		b.append("\"dt\":" + entryDate.get(Calendar.DATE) + ",");
		b.append("\"yr\":" + entryDate.get(Calendar.YEAR) + ",");
		b.append("\"entry\":[");
		
		//TimeZone zone = java.util.TimeZone.getTimeZone("US/Eastern");
		//TimeZone zone = java.util.TimeZone.getTimeZone("Asia/Kolkata");
		
		WeightPreferences preferences = WeightPreferences.getWeightPreference(_goal.getPerson());
		
		Iterator itr = daily_goal.getEntries().iterator();
		boolean needs_comma = false;
		while (itr.hasNext()) {
			MacroGoalEntry entry = (MacroGoalEntry)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(entry, preferences.getTimeZone()));
			needs_comma = true;
		}
		b.append("],");
		b.append("\"id\":" + _goal.getId() + "}");
		return b.toString();
	}
	
	private String toJSONTimeline(MacroGoalDaily daily_goal) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		
		MacroGoal _goal = daily_goal.getParent();
		
		/*
		String fat_cals_str = daily_goal.getFat().multiply(MacroGoal.CALORIES_PER_GRAM_OF_FAT).setScale(0, RoundingMode.HALF_UP).toString();
		String carbs_cals_str = daily_goal.getCarbs().multiply(MacroGoal.CALORIES_PER_GRAM_OF_CARB).setScale(0, RoundingMode.HALF_UP).toString();
		String protein_cals_str = daily_goal.getProtein().multiply(MacroGoal.CALORIES_PER_GRAM_OF_PROTEIN).setScale(0, RoundingMode.HALF_UP).toString();
		*/
		
		b.append("{\"label\":\"" + JSONObject.escape(daily_goal.getParent().getNameString()) + "\",");
		b.append("\"caloriesGoal\":" + _goal.getCaloriesString() + ",");
		b.append("\"caloriesToday\":" + daily_goal.getCaloriesString() + ",");
		b.append("\"caloriesPerc\":" + daily_goal.getCaloriesPercentageString() + ",");
		b.append("\"fat\":" + _goal.getFatString() + ",");
		b.append("\"fatToday\":" + daily_goal.getFatString() + ",");
		//b.append("\"fatPerc\":" + daily_goal.getFatPercentageString() + ",");
		//b.append("\"fatCals\":" + fat_cals_str + ",");
		b.append("\"carbs\":" + _goal.getCarbsString() + ",");
		b.append("\"carbsToday\":" + daily_goal.getCarbsString() + ",");
		//b.append("\"carbsPerc\":" + daily_goal.getCarbsPercentageString() + ",");
		//b.append("\"carbsCals\":" + carbs_cals_str + ",");
		b.append("\"protein\":" + _goal.getProteinString() + ",");
		b.append("\"proteinToday\":" + daily_goal.getProteinString() + ",");
		//b.append("\"proteinPerc\":" + daily_goal.getProteinPercentageString() + ",");
		//b.append("\"proteinCals\":" + protein_cals_str + ",");
		b.append("\"type\":" + _goal.getGoalType() + ",");
		b.append("\"dt\":" + daily_goal.getEntryDateUnixTimestamp() + ",");
		b.append("\"id\":" + _goal.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(MacroGoalEntry _entry, TimeZone _time_zone) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_entry.getLabel()) + "\",");
		
		/*
		Calendar entry_date = Calendar.getInstance();
		entry_date.setTimeZone(_time_zone);
		//entry_date.setTime(_entry.getEntryDate());
		
		DateFormat df = new SimpleDateFormat("HH:mm");
        //df.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
		df.setTimeZone(_time_zone);

		// Read more: http://www.java67.com/2012/12/how-to-display-date-in-multiple-timezone-java.html#ixzz5geuJeDyt
		
		//CUBean.getUserTimeString(dt)
		
		// df.format(_entry.getEntryDate())
		*/
		
		b.append("\"timeStr\":\"" + JSONObject.escape( CUBean.getUserTimeString(_entry.getEntryDate(), _time_zone) ) + "\",");
		b.append("\"calories\":\"" + _entry.getCaloriesString() + "\",");
		b.append("\"fat\":\"" + _entry.getFatString() + "\",");
		b.append("\"carbs\":\"" + _entry.getCarbsString() + "\",");
		b.append("\"protein\":\"" + _entry.getProteinString() + "\",");
		b.append("\"amount\":\"" + _entry.getEntryAmountString() + "\",");
		b.append("\"units\":\"" + _entry.getEntryTypeString(_entry.getEntryAmount()) + "\",");
		
		/*
		PersonBean __person = _entry.getParent().getParent().getPerson();
		Food _ffud = _entry.getFood();
		
		System.out.println("__person >" + __person.getLabel());
		System.out.println("_ffud >" + _ffud.getLabel());
		*/
		
		if (_entry.getFood().isFavorite(_entry.getParent().getParent().getPerson())) {
			b.append("\"isStar\":true,"); // is this needed at all???
		}
		
		b.append("\"id\":" + _entry.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(WeightEntry _entry) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"entryDate\":\"" + JSONObject.escape(_entry.getEntryDateString()) + "\",");
		b.append("\"entryAmount\":\"" + JSONObject.escape(_entry.getEntryAmountString()) + "\",");
		b.append("\"entryUnits\":\"" + _entry.getEntryUnitTypeString() + "\",");
		b.append("\"id\":" + _entry.getId() + "}");
		return b.toString();
	}
	
	private String toJSONTimeline(UKOnlinePersonBean _person, WeightEntry _entry) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		WeightEntry previousWeightEntry = null;
		try {
			previousWeightEntry = WeightEntry.getEntryBefore(_person, _entry);
		} catch (ObjectNotFoundException x) { }
		StringBuilder b = new StringBuilder();
		b.append("{\"dt\":" + _entry.getEntryDateUnixTimestamp() + ",");
		b.append("\"goal\":\"" + _entry.getParent().getGoalString() + "\",");
		b.append("\"entryAmount\":\"" + _entry.getEntryAmountString() + "\",");
		b.append("\"entryUnits\":\"" + _entry.getEntryUnitTypeString() + "\",");
		if (previousWeightEntry != null) {
			BigDecimal delta = _entry.getEntryAmount().subtract(previousWeightEntry.getEntryAmount());
			int dc = delta.compareTo(BigDecimal.ZERO);
			if (dc == -1) {
				b.append("\"d\":\"" + this.getStringFromBigDecimal(delta) + "\",");
			} else if (dc == 1) {
				b.append("\"d\":\"+" + this.getStringFromBigDecimal(delta) + "\",");
			} else {
				b.append("\"d\":\"No\",");
			}
		}
		b.append("\"id\":" + _entry.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(Food _food, boolean _include_servings, boolean _is_favorite) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		boolean has_single_serving = false;
		Vector servings = null;
		if (_include_servings) {
			servings = _food.getServings();
			has_single_serving = ( servings.size() == 1 );
		}
		
		StringBuilder b = new StringBuilder();
		
		b.append("{\"label\":\"" + JSONObject.escape(_food.getLabel()) + "\",");
		b.append("\"isStar\":" + _is_favorite + ",");
		b.append("\"manu\":\"" + JSONObject.escape(_food.getManufacturer()) + "\",");
		b.append("\"description\":\"" + JSONObject.escape(_food.getDescription()) + "\",");
		if (has_single_serving) {
			
			// calculate macros based on single serving
			
			FoodServingDb obj = (FoodServingDb)servings.get(0);
			
			BigDecimal calculated_fat = _food.getFat().multiply(obj.getServingSize()).divide(CUBean.one_hundred, 2, RoundingMode.HALF_UP);
			BigDecimal calculated_carbs = _food.getCarbs().multiply(obj.getServingSize()).divide(CUBean.one_hundred, 2, RoundingMode.HALF_UP);
			BigDecimal calculated_protein = _food.getProtein().multiply(obj.getServingSize()).divide(CUBean.one_hundred, 2, RoundingMode.HALF_UP);
			
			b.append("\"fat\":\"" + this.getStringFromBigDecimal(calculated_fat) + "\",");
			b.append("\"carbs\":\"" + this.getStringFromBigDecimal(calculated_carbs) + "\",");
			b.append("\"protein\":\"" + this.getStringFromBigDecimal(calculated_protein) + "\",");
			b.append("\"servingSize\":\"" + this.getStringFromBigDecimal(obj.getServingSize()) + "\",");
			b.append("\"units\":\"" + _food.getServingSizeTypeString(obj.getServingSizeType()) + "\",");
			b.append("\"servingLabel\":\"" + JSONObject.escape(obj.getServingLabel()) + "\",");
			
			// potentially save these calculations for the Food so that I don't have the redo them
			
		} else {
			b.append("\"fat\":\"" + _food.getFatString() + "\",");
			b.append("\"carbs\":\"" + _food.getCarbsString() + "\",");
			b.append("\"protein\":\"" + _food.getProteinString() + "\",");
			b.append("\"servingSize\":\"" + _food.getServingSizeString() + "\",");
			b.append("\"units\":\"" + _food.getServingSizeTypeString() + "\",");
			if (_include_servings && !servings.isEmpty()) {
				b.append("\"serving\":[");
				Iterator itr = servings.iterator();
				boolean needs_comma = false;
				while (itr.hasNext()) {
					FoodServingDb obj = (FoodServingDb)itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(obj));
					needs_comma = true;
				}
				b.append("],");
			}
		}
		b.append("\"id\":" + _food.getId() + "}");
		return b.toString();
	}
	
	/*
	<table name="FOOD_SERVING_DB">
	<column name="FOOD_DB_ID" required="true" primaryKey="true" type="BIGINT"/>
	<column name="SERVING_LABEL" required="true" primaryKey="true" size="50" type="VARCHAR"/>
	
	<column name="SERVING_SIZE" scale="2" size="7" type="DECIMAL"/>
	<column name="SERVING_SIZE_TYPE" type="SMALLINT"/>

    <foreign-key foreignTable="FOOD_DB">
		<reference local="FOOD_DB_ID" foreign="FOOD_DB_ID"/>
    </foreign-key>
</table>
	*/
	
	private String toJSON(FoodServingDb _serving) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_serving.getServingLabel()) + "\",");
		b.append("\"size\":\"" + this.getStringFromBigDecimal(_serving.getServingSize()) + "\",");
		b.append("\"type\":" + _serving.getServingSizeType() + "}");
		return b.toString();
	}
	
	private String toJSON(WeightGoal weight_goal) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		return this.toJSON(weight_goal, 14);
	}
	
	private String toJSON(WeightGoal weight_goal, int _entry_limit) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		
		String unitStr = WeightPreferences.getWeightPreference(weight_goal.getPerson()).getUnitTypeString();

		b.append("{\"label\":\"" + JSONObject.escape(weight_goal.getLabel()) + "\",");
		b.append("\"personId\":" + weight_goal.getPerson().getId() + ",");
		b.append("\"active\":" + weight_goal.isActive() + ",");
		b.append("\"currentWeight\":\"" + weight_goal.getCurrentWeightString()+ "\",");
		b.append("\"weightGoal\":\"" + weight_goal.getGoalString()+ "\",");
		//b.append("\"type\":\"" + weight_goal.getType().getTypeString() + "\",");
		if (_entry_limit > 0) {
			b.append("\"entry\":[");
			Iterator itr = weight_goal.getEntries(_entry_limit).iterator();
			boolean needs_comma = false;
			while (itr.hasNext()) {
				WeightEntry entry = (WeightEntry)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(entry));
				needs_comma = true;
			}
			b.append("],");
		}
		
		/*
		b.append("\"lastChange\":\"" + weight_goal.getChangeDaysString(1, unitStr) + "\",");
		b.append("\"weekChange\":\"" + weight_goal.getChangeDaysString(7, unitStr) + "\",");
		b.append("\"monthChange\":\"" + weight_goal.getChangeDaysString(30, unitStr) + "\",");
		b.append("\"totalGoalChange\":\"" + weight_goal.getChangeDaysString(-1, unitStr) + "\",");
		*/
		
		String lastChange = weight_goal.getLastChangeString(unitStr);
		String weekChange = weight_goal.getWeekChangeString(unitStr);
		String monthChange = weight_goal.getMonthChangeString(unitStr);
		String yearChange = weight_goal.getYearChangeString(unitStr);
		String totalGoalChange = weight_goal.getOverallChangeString(unitStr);
		
		if (!lastChange.isEmpty()) {
			b.append("\"lastChange\":\"" + lastChange + "\",");
		}
		if (!weekChange.isEmpty()) {
			b.append("\"weekChange\":\"" + weekChange + "\",");
		}
		if (!monthChange.isEmpty()) {
			b.append("\"monthChange\":\"" + monthChange + "\",");
		}
		if (!yearChange.isEmpty()) {
			b.append("\"yearChange\":\"" + yearChange + "\",");
		}
		if (!totalGoalChange.isEmpty()) {
			b.append("\"totalGoalChange\":\"" + totalGoalChange + "\",");
		}
		
		b.append("\"goalPerc\":\"" + weight_goal.getGoalPercString(unitStr) + "\",");
		//b.append("\"totalChange\":\"" + weight_goal.getChangeDaysString(-1, unitStr) + "\","); // add this later probz
		b.append("\"id\":" + weight_goal.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _logged_in_person, TrainingPlan _plan, boolean _include_workout_chart_data, boolean _include_move_chart_data) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		
		boolean canEdit = _plan.canEdit(_logged_in_person);
		
		b.append("{\"label\":\"" + JSONObject.escape(_plan.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_plan.getDescription()) + "\",");
		b.append("\"active\":" + _plan.isActive(_logged_in_person) + ",");
		b.append("\"canEditPlan\":" + canEdit + ",");
		b.append("\"hasBlocks\":" + _plan.hasBlocks() + ",");
		b.append("\"hasWorkouts\":" + _plan.hasWorkouts() + ",");
		
		short current_block_number = 1;
		short current_week_number = 1;
		
		TrainingPlanWorkout current_workout = null;
		try {
			current_workout = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(_logged_in_person, _plan);
			b.append("\"currentWorkoutLabel\":\"" + JSONObject.escape(current_workout.getLabel()) + "\",");
			b.append("\"currentWorkoutDesc\":\"" + JSONObject.escape(current_workout.getDescription()) + "\",");
			//b.append("\"currentWorkoutInProgress\":" + Workout.hasCurrentWorkout(_logged_in_person) + ","); // seems like this'll work
			b.append("\"currentWorkoutInProgress\":" + Workout.isWorkoutInProgress(current_workout, _logged_in_person) + ",");
			b.append("\"currentWorkoutBlock\":" + current_workout.getBlockNumber() + ",");
			b.append("\"currentWorkoutWeek\":" + current_workout.getWeekNumber() + ",");
			//b.append("\"currentWorkoutDay\":" + current_workout.getDayNumber() + ",");
			b.append("\"currentWorkoutWorkout\":" + ( current_workout.getWorkoutNumber() + (short)1 ) + ",");
			b.append("\"currentWorkoutId\":" + current_workout.getId() + ",");
			
			current_block_number = current_workout.getBlockNumber();
			current_week_number = current_workout.getWeekNumber();
			
		} catch (ObjectNotFoundException x) {
			x.printStackTrace();
		}
		
		
		boolean isCompleted = false;
		Workout workout_obj = null;
		if (current_workout != null) {
			if (current_workout.hasWorkoutForTrainingPlanWorkout(_logged_in_person)) {
				workout_obj = current_workout.getLastWorkoutForTrainingPlanWorkout(_logged_in_person);
				isCompleted = workout_obj.isComplete();
				b.append("\"isCompleted\":" + isCompleted + ",");
				b.append("\"wId\":" + workout_obj.getId() + ",");
				b.append("\"completionDate\":\"" + JSONObject.escape(workout_obj.getEndDateString()) + "\",");
			}
		}
		
		
		if (isCompleted) {
			b.append("\"canEdit\":false,");
		} else {
			b.append("\"canEdit\":" + _plan.canEdit(_logged_in_person) + ",");
		}
		
		//b.append("\"canEdit\":" + _plan.canEdit(_logged_in_person) + ",");
		
		b.append("\"workout\":[");
		boolean needs_comma = false;
		Iterator itr = _plan.getWorkouts().iterator();
		while (itr.hasNext()) {
			TrainingPlanWorkout workout = (TrainingPlanWorkout)itr.next();
			//if (workout.equals(current_workout)) {
			if ( (workout.getBlockNumber() == current_block_number) &&
					(workout.getWeekNumber() == current_week_number) ) {
				
				if (needs_comma) { b.append(','); }
				if (workout.equals(current_workout)) {
					b.append(this.toJSON(_logged_in_person, workout, workout.equals(current_workout), workout_obj, true, _include_workout_chart_data, _include_move_chart_data));
				} else {
					b.append(this.toJSON(_logged_in_person, workout, workout.equals(current_workout), workout_obj, false, false, false));
				}
				needs_comma = true;
			}
		}
		b.append("],");
		b.append("\"id\":" + _plan.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(TrainingPlan _selected_plan, Vector _plans, UKOnlinePersonBean _person, boolean _is_copy, boolean _show_new) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_selected_plan == null) {
			_selected_plan = new TrainingPlan();
		}
		
		StringBuilder b = new StringBuilder();
		if (_is_copy) {
			b.append("{\"label\":\"" + JSONObject.escape(_selected_plan.getLabel()) + " [COPY]\",");
			b.append("\"isCopy\":" + _is_copy + ",");
		} else {
			b.append("{\"label\":\"" + JSONObject.escape(_selected_plan.getLabel()) + "\",");
		}
		
		if (_show_new) {
			b.append("\"showNew\":true,");
		}
		
		b.append("\"desc\":\"" + JSONObject.escape(_selected_plan.getDescription()) + "\",");
		b.append("\"notes\":\"" + JSONObject.escape(_selected_plan.getNotes()) + "\",");
		
		if (_selected_plan.isNew()) {
			b.append("\"isPublic\":false,");
			b.append("\"active\":false,");
			b.append("\"repeat\":false,");
			b.append("\"canEdit\":true,");
		} else {
			b.append("\"isPublic\":" + _selected_plan.isPublic() + ",");
			b.append("\"active\":" + _selected_plan.isActive(_person) + ",");
			b.append("\"repeat\":" + _selected_plan.isRepeating() + ",");
			if (_is_copy) {
				b.append("\"canEdit\":true,");
			} else {
				b.append("\"canEdit\":" + _selected_plan.canEdit(_person) + ",");
			}
		}

		b.append("\"plan\":[");
		boolean needs_comma = false;
		Iterator itr = _plans.iterator();
		while (itr.hasNext()) {
			TrainingPlan plan = (TrainingPlan)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSONShort(plan, _person));
			needs_comma = true;
		}
		b.append("],");
		b.append("\"id\":" + _selected_plan.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _person, MacroGoal _selected_goal, Vector _goals, boolean _show_new) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_selected_goal == null) {
			_selected_goal = new MacroGoal();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_selected_goal.getLabel()) + "\",");
		b.append("\"active\":" + _selected_goal.isActive() + ",");
		if (_show_new) {
			b.append("\"showNew\":true,");
		}
		b.append("\"auto\":" + _selected_goal.isAuto() + ",");
		b.append("\"calories\":\"" + _selected_goal.getCaloriesString() + "\",");
		b.append("\"fat\":\"" + _selected_goal.getFatString() + "\",");
		b.append("\"carbs\":\"" + _selected_goal.getCarbsString() + "\",");
		b.append("\"protein\":\"" + _selected_goal.getProteinString() + "\",");
		//b.append("\"type\":" + _selected_goal.getGoalType() + ",");
		b.append("\"calorieGoalSelect\":\"" + _selected_goal.getCalorieGoalValueString() + "\",");
		b.append("\"fatGoalSelect\":\"" + _selected_goal.getFatGoalValueString() + "\",");
		b.append("\"proteinGoalSelect\":\"" + _selected_goal.getProteinGoalValueString() + "\",");
		
		WeightPreferences preferences = WeightPreferences.getWeightPreference(_person);
		b.append("\"bmr\":\"" + preferences.getBMRString() + "\",");
		b.append("\"tee\":\"" + preferences.getTotalEnergyExpenditureString() + "\",");
		b.append("\"bodyWeight\":\"" + preferences.getWeightString() + "\",");
		
		b.append("\"goal\":[");
		boolean needs_comma = false;
		Iterator itr = _goals.iterator();
		while (itr.hasNext()) {
			MacroGoal goal = (MacroGoal)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(goal));
			needs_comma = true;
		}
		b.append("],");
		b.append("\"id\":" + _selected_goal.getId() + "}");
		return b.toString();
		
		/*
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_selected_plan.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_selected_plan.getDescription()) + "\",");
		b.append("\"notes\":\"" + JSONObject.escape(_selected_plan.getNotes()) + "\",");
		

		b.append("\"plan\":[");
		boolean needs_comma = false;
		Iterator itr = _plans.iterator();
		while (itr.hasNext()) {
			TrainingPlan plan = (TrainingPlan)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSONShort(plan, _person));
			needs_comma = true;
		}
		b.append("],");
		b.append("\"id\":" + _selected_plan.getId() + "}");
		return b.toString();
		*/
		
	}
	
	private String toJSON(UKOnlinePersonBean _person, WeightGoal _selected_goal, Vector _goals, boolean _show_new) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_selected_goal == null) {
			_selected_goal = new WeightGoal();
		}
		
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_selected_goal.getLabel()) + "\",");
		b.append("\"active\":" + _selected_goal.isActive() + ",");
		b.append("\"weightGoal\":\"" + _selected_goal.getGoalString() + "\",");
		if (_show_new) {
			b.append("\"showNew\":true,");
		}
		
		WeightPreferences preferences = WeightPreferences.getWeightPreference(_person);
		b.append("\"bmr\":\"" + preferences.getBMRString() + "\",");
		b.append("\"tee\":\"" + preferences.getTotalEnergyExpenditureString() + "\",");
		b.append("\"bodyWeight\":\"" + preferences.getWeightString() + "\",");
		b.append("\"unitTypeStr\":\"" + preferences.getUnitTypeString() + "\",");
		
		b.append("\"goal\":[");
		boolean needs_comma = false;
		Iterator itr = _goals.iterator();
		while (itr.hasNext()) {
			WeightGoal goal = (WeightGoal)itr.next();
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(goal, 0));
			needs_comma = true;
		}
		b.append("],");
		b.append("\"id\":" + _selected_goal.getId() + "}");
		return b.toString();
		
	}
	
	private String toJSONTimeline(Workout _obj) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {

		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_obj.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_obj.getNotes()) + "\",");
		b.append("\"program\":\"" + JSONObject.escape(_obj.getParent().getParent().getLabel()) + "\",");
		b.append("\"isComplete\":" + _obj.isComplete() + ",");
		
		b.append("\"move\":[");
		boolean needs_comma = false;
		LinkedHashMap<Move,ArrayList<WorkoutSet>> set_hash = WorkoutSet.getSets(_obj);
		Iterator itr = set_hash.keySet().iterator();
		while (itr.hasNext()) {
			Move key = (Move)itr.next();
			ArrayList<WorkoutSet> sets = (ArrayList<WorkoutSet>)set_hash.get(key);
			if (needs_comma) { b.append(','); }
			b.append(this.toJSON(key, sets));
			needs_comma = true;
		}
		b.append("],");
		
		b.append("\"dt\":" + _obj.getStartDateUnixTimestamp() + ",");
		b.append("\"id\":" + _obj.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(Move _move, ArrayList<WorkoutSet> _sets) {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_move.getLabel()) + "\",");
		
		String setStr = "No sets found";
		short volume = (short)0;
		BigDecimal calculated1RM = BigDecimal.ZERO;
		//String firstStr = "";
		
		WorkoutSet last_set = null;
		//boolean all_sets_equal = true;
		short equal_streak = (short)0;
		//boolean this_set_equals_last = false;
		Iterator itr = _sets.iterator();
		if (itr.hasNext()) {
			setStr = "";
			while (itr.hasNext()) {
				WorkoutSet set_obj = (WorkoutSet)itr.next();
				set_obj.getActualWeight();
				if (last_set != null) {
					if (set_obj.equals(last_set)) {
						//this_set_equals_last = true;
						
						if (equal_streak == (short)0) {
							equal_streak = (short)2;
						} else {
							equal_streak++;
						}
						
					} else {
						//all_sets_equal = false;
						equal_streak = (short)0;
					}
				}
				last_set = set_obj;
				
				//String actual_weight_str = set_obj.getActualWeightString();
				String actual_weight_str = set_obj.getActualWeightAndRPEString();
				
				if (setStr.isEmpty()) {
					if (actual_weight_str.isEmpty()) {
						setStr = set_obj.getActualRepsString();
					} else {
						setStr = set_obj.getActualRepsString() + "@" + actual_weight_str;
					}
				} else {
					
					if ( equal_streak == (short)0 ) {
						if (actual_weight_str.isEmpty()) {
							setStr += ", " + set_obj.getActualRepsString();
						} else {
							setStr += ", " + set_obj.getActualRepsString() + "@" + actual_weight_str;
						}
					} else {
						int comma_index = setStr.lastIndexOf(',');
						if (comma_index == -1) {
							// no comma
							if (actual_weight_str.isEmpty()) {
								setStr = equal_streak + "x" + set_obj.getActualRepsString();
							} else {
								setStr = equal_streak + "x" + set_obj.getActualRepsString() + "@" + actual_weight_str;
							}
						} else {
							// there is a comma, and this set equals the last - remove the last
							setStr = setStr.substring(0, comma_index);
							if (actual_weight_str.isEmpty()) {
								setStr += ", " + equal_streak + "x" + set_obj.getActualRepsString();
							} else {
								setStr += ", " + equal_streak + "x" + set_obj.getActualRepsString() + "@" + actual_weight_str;
							}
							
						}
						
					}
				}
				
				volume += set_obj.getActualReps();
				try {
					if (_move.isCompoundLift()) {
						if (set_obj.getActualWeight() != null) {
							
							short actual_reps = set_obj.getActualReps();
							if (set_obj.getActualRPE() != null) {
								short rir = (short)(10 - set_obj.getActualRPE().shortValue());
								actual_reps += rir;
							}
							
							BigDecimal calculated1RM_forSet = Move.getCalculated1RM(actual_reps, set_obj.getActualWeight());
							if (calculated1RM_forSet.compareTo(calculated1RM) == 1) {
								calculated1RM = calculated1RM_forSet;
							}
						}
					}
				} catch (IllegalValueException x) {
					x.printStackTrace();
				}
			}

			/*
			if (all_sets_equal) {
				setStr = _sets.size() + "x" + last_set.getActualRepsString() + " @ " + last_set.getActualWeightString();
			}
			*/
		}
		
		b.append("\"setStr\":\"" + JSONObject.escape(setStr) + "\",");
		b.append("\"volume\":" + volume + ",");
		if (_move.isCompoundLift()) {
			b.append("\"c1rm\":" + calculated1RM.setScale(0, RoundingMode.HALF_UP).toString() + ",");
		}
		b.append("\"id\":" + _move.getId() + "}");
		return b.toString();
	}
	
	private String getSetString(Move _move, ArrayList<WorkoutSet> _sets) {
		
		String setStr = "No sets found";
		
		BigDecimal calculated1RM = BigDecimal.ZERO;
		//String firstStr = "";
		
		WorkoutSet last_set = null;
		//boolean all_sets_equal = true;
		short equal_streak = (short)0;
		//boolean this_set_equals_last = false;
		Iterator itr = _sets.iterator();
		if (itr.hasNext()) {
			setStr = "";
			while (itr.hasNext()) {
				WorkoutSet set_obj = (WorkoutSet)itr.next();
				set_obj.getActualWeight();
				if (last_set != null) {
					if (set_obj.equals(last_set)) {
						//this_set_equals_last = true;
						
						if (equal_streak == (short)0) {
							equal_streak = (short)2;
						} else {
							equal_streak++;
						}
						
					} else {
						//all_sets_equal = false;
						equal_streak = (short)0;
					}
				}
				last_set = set_obj;
				
				//String actual_weight_str = set_obj.getActualWeightString();
				String actual_weight_str = set_obj.getActualWeightAndRPEString();
				
				if (setStr.isEmpty()) {
					if (actual_weight_str.isEmpty()) {
						setStr = set_obj.getActualRepsString();
					} else {
						setStr = set_obj.getActualRepsString() + "@" + actual_weight_str;
					}
				} else {
					
					if ( equal_streak == (short)0 ) {
						if (actual_weight_str.isEmpty()) {
							setStr += ", " + set_obj.getActualRepsString();
						} else {
							setStr += ", " + set_obj.getActualRepsString() + "@" + actual_weight_str;
						}
					} else {
						int comma_index = setStr.lastIndexOf(',');
						if (comma_index == -1) {
							// no comma
							if (actual_weight_str.isEmpty()) {
								setStr = equal_streak + "x" + set_obj.getActualRepsString();
							} else {
								setStr = equal_streak + "x" + set_obj.getActualRepsString() + "@" + actual_weight_str;
							}
						} else {
							// there is a comma, and this set equals the last - remove the last
							setStr = setStr.substring(0, comma_index);
							if (actual_weight_str.isEmpty()) {
								setStr += ", " + equal_streak + "x" + set_obj.getActualRepsString();
							} else {
								setStr += ", " + equal_streak + "x" + set_obj.getActualRepsString() + "@" + actual_weight_str;
							}
							
						}
						
					}
				}
				
				
				try {
					if (_move.isCompoundLift()) {
						if (set_obj.getActualWeight() != null) {
							
							short actual_reps = set_obj.getActualReps();
							if (set_obj.getActualRPE() != null) {
								short rir = (short)(10 - set_obj.getActualRPE().shortValue());
								actual_reps += rir;
							}
							
							BigDecimal calculated1RM_forSet = Move.getCalculated1RM(actual_reps, set_obj.getActualWeight());
							if (calculated1RM_forSet.compareTo(calculated1RM) == 1) {
								calculated1RM = calculated1RM_forSet;
							}
						}
					}
				} catch (IllegalValueException x) {
					x.printStackTrace();
				}
			}

		}
		
		return setStr;
	}
	
	private String toJSON(MacroGoal _plan) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_plan.getLabel()) + "\",");
		b.append("\"active\":" + _plan.isActive() + ",");
		//b.append("\"desc\":\"" + JSONObject.escape(_plan.getDescription()) + "\",");
		b.append("\"id\":" + _plan.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(TrainingPlanWorkout _selected_workout, boolean _is_copy, Vector _workouts, boolean _show_new) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_selected_workout == null) {
			_selected_workout = new TrainingPlanWorkout();
		}
		
		StringBuilder b = new StringBuilder();
		if (_is_copy) {
			b.append("{\"label\":\"" + JSONObject.escape(_selected_workout.getLabel()) + " [COPY]\",");
		} else {
			b.append("{\"label\":\"" + JSONObject.escape(_selected_workout.getLabel()) + "\",");
		}
		if (_show_new) {
			b.append("\"showNew\":true,");
		}
		b.append("\"desc\":\"" + JSONObject.escape(_selected_workout.getDescription()) + "\",");
		
		if (_selected_workout.getBlockNumber() > 0) {
			b.append("\"block\":" + _selected_workout.getBlockNumber() + ",");
		}
		if (_selected_workout.getWeekNumber() > 0) {
			b.append("\"week\":" + _selected_workout.getWeekNumber() + ",");
		}
		
		/*
		if (_selected_workout.getDayNumber() > 0) {
			b.append("\"day\":" + _selected_workout.getDayNumber() + ",");
		}
		*/
		if (_selected_workout.getWorkoutNumber() > 0) {
			b.append("\"workout\":" + _selected_workout.getWorkoutNumber() + ",");
		}
		b.append("\"isCopy\":" + _is_copy + ",");
		//b.append("\"workoutArr\":[");
		short block_number = 0;
		short week_number = 0;
		boolean needs_comma_block = false;
		boolean needs_comma_week = false;
		Iterator itr = _workouts.iterator();
		while (itr.hasNext()) {
			TrainingPlanWorkout workout = (TrainingPlanWorkout)itr.next();
			
			System.out.println("found workout to add >" + workout.getId());
			
			if (workout.getBlockNumber() != block_number) {
				if (block_number > 0) {
					b.append("]}],");
				}
				b.append("\"Block " + workout.getBlockNumber() + "\":[{");
				needs_comma_block = false;
				needs_comma_week = false;
				week_number = 0;
				block_number = workout.getBlockNumber();
			}
			
			if (workout.getWeekNumber() != week_number) {
				if (week_number > 0) {
					b.append("],");
				}
				b.append("\"Week " + workout.getWeekNumber() + "\":[");
				needs_comma_week = false;
				week_number = workout.getWeekNumber();
			}
			
			if (needs_comma_week) { b.append(','); }
			//if (needs_comma_block) { b.append(','); }
			b.append(this.toJSON(workout));
			needs_comma_week = true;
			//needs_comma_block = true;
		}
		//if (week_number > 0 ) { b.append("],"); }
		if (week_number > 0 ) { b.append("]"); }
		if (block_number > 0 ) { b.append("}],"); }
		b.append("\"id\":" + _selected_workout.getId() + "}");
		return b.toString();
	}
	
	private String toJSONShort(TrainingPlan _plan, UKOnlinePersonBean _person) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_plan.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_plan.getDescription()) + "\",");
		b.append("\"notes\":\"" + JSONObject.escape(_plan.getNotes()) + "\",");
		b.append("\"active\":" + _plan.isActive(_person) + ",");
		b.append("\"repeat\":" + _plan.isRepeating() + ",");
		b.append("\"id\":" + _plan.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(TrainingPlanWorkout _workout) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_workout.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_workout.getDescription()) + "\",");
		//b.append("\"week\":" + _workout.getWeekNumber() + ",");
		//b.append("\"day\":" + _workout.getDayNumber() + ",");
		b.append("\"workout\":" + _workout.getWorkoutNumber() + ",");
		b.append("\"id\":" + _workout.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _logged_in_person, Workout _workout, boolean _include_chart_data, boolean _include_move_array, boolean _is_end) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		
		b.append("{\"label\":\"" + JSONObject.escape(_workout.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_workout.getNotes()) + "\",");
		
		Date workoutStartDt = _workout.getStartDate();
		WeightPreferences preferences = WeightPreferences.getWeightPreference(_logged_in_person);
		
		b.append("\"startDate\":\"" + CUBean.getUserDateString(workoutStartDt, preferences.getTimeZone()) + "\",");
		b.append("\"startTime\":\"" + CUBean.getUserTimeString(workoutStartDt, preferences.getTimeZone()) + "\",");
		
		Date now = new Date();
		//long hours = Workout.getDateDiff(workoutStartDt, now, TimeUnit.HOURS);
		long minutes = Workout.getDateDiff(workoutStartDt, now, TimeUnit.MINUTES);
		long seconds = Workout.getDateDiff(workoutStartDt, now, TimeUnit.SECONDS);
		//System.out.println("toJSON secs >" + seconds);
		long hours = minutes / 60;
		minutes = minutes % 60;
		seconds = seconds % 60;
		
		b.append("\"elapsedHours\":" + hours + ",");
		b.append("\"elapsedMinutes\":" + minutes + ",");
		b.append("\"elapsedSeconds\":" + seconds + ",");
		
		if ( (last_set_completion_date_hash != null) &&
				last_set_completion_date_hash.containsKey(_logged_in_person)) {
			Date lastSetCompletionDt = last_set_completion_date_hash.get(_logged_in_person);
			long setSeconds = Workout.getDateDiff(lastSetCompletionDt, now, TimeUnit.SECONDS);
			long setMinutes = setSeconds / 60;
			setSeconds = setSeconds % 60;
			b.append("\"setMinutes\":" + setMinutes + ",");
			b.append("\"setSeconds\":" + setSeconds + ",");
		}
		
		if (!_is_end) {
			b.append("\"move\":[");
			b.append(this.toJSON(_logged_in_person, _workout.getCurrentMove(), _workout, _include_chart_data));
			b.append("],");
			if (_include_move_array) {
				b.append("\"moveArr\":[");
				Iterator<Move> itr = Move.getMoves();
				boolean needs_comma = false;
				while (itr.hasNext()) {
					Move move_obj = itr.next();
					if (needs_comma) { b.append(','); }
					b.append(this.toJSON(move_obj));
					needs_comma = true;
				}
				b.append("],");
			}
		} else {
			b.append("\"isEnd\":true,");
		}
		b.append("\"id\":" + _workout.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _logged_in_person, TrainingPlanWorkout _workout, boolean _is_current, Workout _w, boolean _include_moves, boolean _include_workout_chart_data, boolean _include_move_chart_data) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_workout.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_workout.getDescription()) + "\",");
		
		//if (current_workout != null) {
			//try {
				if (_workout.hasWorkoutForTrainingPlanWorkout(_logged_in_person)) {
					Workout workout_obj = _workout.getLastWorkoutForTrainingPlanWorkout(_logged_in_person);
					boolean isCompleted = workout_obj.isComplete();
					//b.append("\"c\":" + isCompleted + ",");
					b.append("\"wId\":" + workout_obj.getId() + ",");
					if (isCompleted) {
						b.append("\"cDate\":\"" + JSONObject.escape(workout_obj.getEndDateString()) + "\",");
					}
				}
			//} catch (Exception x) {
				//x.printStackTrace();
			//}
		//}
		
		//b.append("\"cDate\":\"" + JSONObject.escape(workout_obj.getEndDateString()) + "\",");
		
		//b.append("\"week\":" + _workout.getWeekNumber() + ",");
		//b.append("\"day\":" + _workout.getDayNumber() + ",");
		b.append("\"workout\":" + _workout.getWorkoutNumber() + ",");
		b.append("\"current\":" + _is_current + ",");
		b.append("\"inProgress\":" + Workout.isWorkoutInProgress(_workout, _logged_in_person) + ",");
		
		//String movesDesc = "";
		Vector compound_move_names = new Vector();
		Vector accessory_move_names = new Vector();
		String lastMoveLabel = "";
		boolean needs_comma = false;
		
		if (_include_moves) {
			b.append("\"move\":[");
		}
		//Iterator itr = _workout.getMoves().iterator();
		Iterator itr = _workout.getMoves(_logged_in_person).iterator();
		while (itr.hasNext()) {
			TrainingPlanMove move = (TrainingPlanMove)itr.next();
			String moveLabel = move.getLabel();
			if (!moveLabel.equals(lastMoveLabel)) {
				if (move.getMove().isCompoundLift()) {
					compound_move_names.addElement(moveLabel);
				} else {
					accessory_move_names.addElement(moveLabel);
				}
			}
			/*
			if (movesDesc.indexOf(move.getLabel()) == -1) {
				if (movesDesc.isEmpty()) {
					movesDesc += move.getLabel();
				} else {
					movesDesc += (", " + move.getLabel() );
				}
			}
			*/
			//movesDesc += "<p>" + move.getSetsDesc(_logged_in_person) + "</p>";
			if (_include_moves) {
				if (needs_comma) { b.append(','); }
				b.append(this.toJSON(_logged_in_person, move, _w, _include_move_chart_data));
				needs_comma = true;
			}
			lastMoveLabel = moveLabel;
		}
		if (_include_moves) {
			b.append("],");
		}
		
		needs_comma = false;
		b.append("\"cl\":[");
		itr = compound_move_names.iterator();
		while (itr.hasNext()) {
			if (needs_comma) { b.append(','); }
			b.append("{\"l\":\"" + JSONObject.escape((String)itr.next()) + "\"}");
			needs_comma = true;
		}
		b.append("],");
		needs_comma = false;
		b.append("\"al\":[");
		itr = accessory_move_names.iterator();
		while (itr.hasNext()) {
			if (needs_comma) { b.append(','); }
			b.append("{\"l\":\"" + JSONObject.escape((String)itr.next()) + "\"}");
			needs_comma = true;
		}
		b.append("],");
		
		//b.append("\"setDesc\":\"" + JSONObject.escape(movesDesc) + "\",");
		if (_is_current && _include_workout_chart_data) {
			
			LinkedHashMap<Workout,Vector> sets_for_workout = TrainingPlanWorkout.getPreviousWorkoutsSets(_logged_in_person, _workout, 20);
			if (!sets_for_workout.isEmpty()) {
			
				b.append("\"chartLabel\":[");
				Iterator key_itr = sets_for_workout.keySet().iterator();
				needs_comma = false;
				while (key_itr.hasNext()) {
					Workout workout_obj = (Workout)key_itr.next();
					//System.out.println("  found workout >" + CUBean.getUserDateString(workout_obj.getStartDate()));
					WeightPreferences preferences = WeightPreferences.getWeightPreference(_logged_in_person);
					if (needs_comma) { b.append(','); }
					b.append("\"" + JSONObject.escape(CUBean.getUserDateString(workout_obj.getStartDate(), "M/d/yy", preferences.getTimeZone())) + "\"");
					needs_comma = true;
				}
				b.append("],");
				b.append("\"chartWorkout\":[");

				Vector<Short> total_reps_vec = new Vector();
				Vector<BigDecimal> total_volume_vec = new Vector();
				key_itr = sets_for_workout.keySet().iterator();
				while (key_itr.hasNext()) {
					short total_reps_in_workout = 0;
					BigDecimal total_volume_in_workout_bd = BigDecimal.ZERO;
					Workout workout_obj = (Workout)key_itr.next();
					Iterator sets_in_workout_itr = sets_for_workout.get(workout_obj).iterator();
					while (sets_in_workout_itr.hasNext()) {
						WorkoutSet set_obj = (WorkoutSet)sets_in_workout_itr.next();
						total_reps_in_workout += set_obj.getActualReps();
						if (set_obj.getActualWeight() != null) {
							BigDecimal actual_reps_bd = new BigDecimal(set_obj.getActualReps());
							total_volume_in_workout_bd = total_volume_in_workout_bd.add(actual_reps_bd.multiply(set_obj.getActualWeight()));
						}
					}
					total_reps_vec.addElement(total_reps_in_workout);
					total_volume_vec.addElement(total_volume_in_workout_bd);
				}


				b.append("[");
				needs_comma = false;
				Iterator chartWorkout_itr = total_reps_vec.iterator();
				while (chartWorkout_itr.hasNext()) {
					Short total_reps_in_workout = (Short)chartWorkout_itr.next();
					if (needs_comma) { b.append(','); }
					b.append("\"" + total_reps_in_workout + "\"");
					needs_comma = true;
				}
				b.append("],");


				b.append("[");
				needs_comma = false;
				chartWorkout_itr = total_volume_vec.iterator();
				while (chartWorkout_itr.hasNext()) {
					BigDecimal total_volume_in_workout = (BigDecimal)chartWorkout_itr.next();
					if (needs_comma) { b.append(','); }
					b.append("\"" + FitnessServlet.getStringFromBigDecimal(total_volume_in_workout) + "\"");
					needs_comma = true;
				}
				b.append("]],");
			}
		}
		b.append("\"id\":" + _workout.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _logged_in_person, TrainingPlanMove _move, Workout _workout, boolean _include_move_chart_data) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_move.getLabel()) + "\",");
		b.append("\"moveDesc\":\"" + JSONObject.escape(_move.getMove().getDescription()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_move.getDescription()) + "\",");
		String videoURL = _move.getMove().getVideoURL();
		if (!videoURL.isEmpty()) {
			b.append("\"videoURL\":\"" + JSONObject.escape(videoURL) + "\",");
		}
		b.append("\"moveNumber\":" + _move.getMoveNumber() + ",");
		b.append("\"type\":\"" + _move.getTypeString() + "\",");
		if (_move.getMove().isCompoundLift()) {
			
			String prString = _move.getMove().getPRString(_logged_in_person);
			if ( (prString != null) && !prString.isEmpty() ) {
				b.append("\"pr\":\"" + prString + "\",");
			}
					
			String calculated_1_rep_max = _move.getMove().getCalculated1RMString(_logged_in_person);
			if (!calculated_1_rep_max.equals("0")) {
				b.append("\"calculated1RM\":\"" + _move.getMove().getCalculated1RMString(_logged_in_person) + "\",");
			}
		}
		b.append("\"setDesc\":\"" + _move.getSetsDesc(_logged_in_person) + "\",");
		b.append("\"isSuperSet\":" + _move.isSuperSet() + ",");
		b.append("\"isCompound\":" + _move.getMove().isCompoundLift()+ ",");
		
		// get desc of last time
		
		
		
		/*
		try {
			//WorkoutSet last_set = WorkoutSet.getLastCorrespondingWorkoutSet(_person, _set);
			WorkoutSet last_set = WorkoutSet.getLastCorrespondingWorkoutSet(_logged_in_person, _move.getMove(), (short)(_setNumber - 1) );
			System.out.println("last_set date >" + last_set.getSetDateString());
			lastRepsStr = last_set.getActualRepsString();
			lastWeightStr = last_set.getActualWeightString();
		} catch (ObjectNotFoundException x) {
			//System.out.println(x.getMessage());
		}
		*/
		
		try {
			Workout last_workout_containing_move = Workout.getLastWorkoutContainingMove(_logged_in_person, _move.getMove());
			//ArrayList<WorkoutSet> last_sets_for_move = WorkoutSet.getSets(last_workout_containing_move, _move.getMove());
			//Iterator itr = last_sets_for_move.iterator();
			//while (itr.hasNext()) {
				//Move move_obj = (Move)itr.next();
				//ArrayList<WorkoutSet> sets = (ArrayList<WorkoutSet>)set_hash.get(move_obj);
				//b.append("<p>" + move_obj.getLabel() + "<br/>" + this.getSetString(move_obj, sets) + "</p>");
			//}

			b.append("\"lastSets\":\"" + this.getSetString(_move.getMove(), WorkoutSet.getSets(last_workout_containing_move, _move.getMove())) + "\",");
			b.append("\"lastSetsDate\":" + last_workout_containing_move.getEndDateUnixTimestamp() + ",");
		} catch (ObjectNotFoundException x) {
			System.out.println(x.getMessage());
		}
		
		b.append("\"set\":[");
		Vector sets = _move.getSets();
		boolean needs_comma = false;
		Iterator itr = sets.iterator();
		for (int setNumber = 1; itr.hasNext(); setNumber++) {
			TrainingPlanSet set = (TrainingPlanSet)itr.next();
			String str = this.toJSON(_logged_in_person, set, _workout, setNumber);
			if (str != null) {
				if (needs_comma) { b.append(','); }
				b.append(str);
				needs_comma = true;
			}
		}
		b.append("],");
		
		if (_include_move_chart_data) {
			
			//Vector corresponding_sets = WorkoutSet.getLastCorrespondingWorkoutSets(_logged_in_person, sets, 10); // include last 10 workouts
			Vector corresponding_sets = WorkoutSet.getLastWorkoutSetsForMove(_logged_in_person, _move.getMove(), 10); // include last 10 workouts
			
			HashMap<String,BigDecimal> volume_hash = new HashMap();
			HashMap<String,Vector> rep_hash = new HashMap();
			HashMap<Short,String[]> set_hash = new HashMap();
			
			short max_set_number = 0;
			short num_workouts = 0;
			Workout last_workout = null;
			
			HashMap<Workout,Short> workout_number_hash = new HashMap();
			
			short set_number = (short)0;
			String last_date_key = "";
			
			itr = corresponding_sets.iterator();
			while (itr.hasNext()) {
				WorkoutSet workout_set = (WorkoutSet)itr.next();
				String date_key = workout_set.getSetDateString();
				
				/*
				if (!date_key.equals(last_date_key)) {
					set_number = (short)0;
				}
				last_date_key = date_key;
				*/
				
				BigDecimal weight_lifted_for_set = workout_set.getActualWeight();
				if (weight_lifted_for_set != null) {
					//BigDecimal set_volume = workout_set.getActualWeight().multiply(new BigDecimal(workout_set.getActualReps()));
					// 7/27/18 - change set volume calculation to *NOT* include weight, so, just reps * sets
					BigDecimal set_volume = new BigDecimal(workout_set.getActualReps());
					if (volume_hash.containsKey(date_key)) {
						BigDecimal volume_bd = volume_hash.get(date_key);
						volume_hash.put(date_key, volume_bd.add(set_volume));
					} else {
						volume_hash.put(date_key, set_volume);
					}
				}
				
				short actual_reps = workout_set.getActualReps();
				if (rep_hash.containsKey(date_key)) {
					Vector<Short> vec = rep_hash.get(date_key);
					vec.addElement(actual_reps);
				} else {
					Vector<Short> vec = new Vector();
					vec.addElement(actual_reps);
					rep_hash.put(date_key, vec);
				}
				
				//short set_number = workout_set.getTrainingPlanSet().getSetNumber();
				
				Workout workout_obj = workout_set.getWorkout();
				if (last_workout == null) {
					last_workout = workout_obj;
					num_workouts++;
					workout_number_hash.put(workout_obj, num_workouts);
				} else {
					if (!last_workout.equals(workout_obj)) {
						last_workout = workout_obj;
						num_workouts++;
						workout_number_hash.put(workout_obj, num_workouts);
					}
				}
				
			}
			
			
			
			set_number = (short)0;
			last_date_key = "";
			
			
			Vector label_vec = new Vector();
			itr = corresponding_sets.iterator();
			while (itr.hasNext()) {
				
				set_number = (short)0;
				
				WorkoutSet workout_set = (WorkoutSet)itr.next();
				String date_key = workout_set.getSetDateString();
				if (rep_hash.containsKey(date_key)) {
					
					boolean all_reps_equal = true;
					Short last_rep_count = -1;
					String set_label = "";
					
					Vector reps_vec = rep_hash.get(date_key);
					Iterator rep_itr = reps_vec.iterator();
					while (rep_itr.hasNext()) {
						Short rep_count = (Short)rep_itr.next();
						if (last_rep_count > -1) {
							if (rep_count != last_rep_count) {
								all_reps_equal = false;
							}
						}
						
						if (set_label.isEmpty()) {
							set_label = "" + rep_count;
						} else {
							set_label += ( "," + rep_count );
						}
						
						last_rep_count = rep_count;
						
						set_number++;
					}
					
					if (all_reps_equal) {
						set_label = reps_vec.size() + "x" + last_rep_count;
					}
					
					// ["1/18/18","4x10"]
					String label_str = "[\"" + date_key + "\",\"" + set_label + "\"]";
					if (!label_vec.contains(label_str)) {
						label_vec.addElement(label_str);
					}
				}
				
				
				if (set_number > max_set_number) {
					max_set_number = set_number;
				}
				
				
			}
			
			max_set_number--;
			
			for (short i = (short)0; i <= max_set_number; i++) {
				String[] set_str_arr = new String[num_workouts];
				for (int x = 0; x < num_workouts; x++) {
					set_str_arr[x] = "0";
				}
				set_hash.put(i, set_str_arr);
			}
			
			b.append("\"chartLabel\":[");
			needs_comma = false;
			//itr = corresponding_sets.iterator();
			// [["1/18/18","4x10"], ["1/21/18","9,10,10,10"], "1/22 (9,5,5,5)", "1/24 (9,5,5,5)"]
			itr = label_vec.iterator();
			while (itr.hasNext()) {
				String set_label = (String)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(set_label);
				needs_comma = true;
			}
			b.append("],");
			
			b.append("\"volume\":[");
			needs_comma = false;
			//System.out.println("num corresponding_sets >" + corresponding_sets.size());
			itr = corresponding_sets.iterator();
			while (itr.hasNext()) {
				WorkoutSet workout_set = (WorkoutSet)itr.next();
				String date_key = workout_set.getSetDateString();
				//System.out.println("set_date >" + date_key);
				//System.out.println("volume_hash.containsKey(date_key) >" + volume_hash.containsKey(date_key));
				//System.out.println("(!date_key.equals(last_date_key)) >" + (!date_key.equals(last_date_key)));
				if ( !date_key.equals(last_date_key) ) {
					if (needs_comma) { b.append(','); }
					BigDecimal volume = BigDecimal.ZERO;
					if (volume_hash.containsKey(date_key)) {
						volume = volume_hash.get(date_key);
					}
					b.append(volume.setScale(0, RoundingMode.HALF_UP));
					needs_comma = true;
					//System.out.println("date_key >" + date_key);
					//System.out.println("last_date_key >" + last_date_key);
					if (!date_key.equals(last_date_key)) {
						set_number = (short)0;
					}
					last_date_key = date_key;
				}
				
				Workout workout_obj = workout_set.getWorkout();
				short workout_number = workout_number_hash.get(workout_obj);
				String actual_weight_str = workout_set.getActualWeightString();
				//short set_number = workout_set.getTrainingPlanSet().getSetNumber();
				//System.out.println("workout_set XXX >" + workout_set.getSetDateShortString());
				//System.out.println("set_number XXX >" + set_number);
				String[] arr = set_hash.get(set_number);
				//System.out.println("arr XXX >" + arr);
				arr[workout_number - 1] = actual_weight_str;
				
				set_number++;
			}
			b.append("],");
			
			
			b.append("\"chartSet\":[");
			needs_comma = false;
			for (short setNumber = (short)0; setNumber < 100; setNumber++) {
				//System.out.println("setNumber >" + setNumber);
				if (set_hash.containsKey(setNumber)) {
					if (needs_comma) { b.append(','); }
					b.append("[");
					//Vector<String> set_vec = set_hash.get(setNumber);
					String[] set_arr = set_hash.get(setNumber);
					boolean needs_comma_x = false;
					//itr = set_vec.iterator();
					//while (itr.hasNext()) {
					for (int i = 0; i < set_arr.length; i++) {
						//String actual_weight_str = (String)itr.next();
						String actual_weight_str = set_arr[i];
						if (needs_comma_x) { b.append(','); }
						b.append("\"" + actual_weight_str + "\"");
						needs_comma_x = true;
					}
					b.append("]");
					needs_comma = true;
				} else {
					break;
				}
			}
			b.append("],");
		}
		b.append("\"id\":" + _move.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _logged_in_person, Move _move, boolean _include_move_chart_data) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_move.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_move.getDescription()) + "\",");
		String videoURL = _move.getVideoURL();
		if (!videoURL.isEmpty()) {
			b.append("\"videoURL\":\"" + JSONObject.escape(videoURL) + "\",");
		}
		b.append("\"type\":\"" + _move.getTypeString() + "\",");
		if (_move.isCompoundLift()) {
					
			String calculated_1_rep_max = _move.getCalculated1RMString(_logged_in_person);
			if (!calculated_1_rep_max.equals("0")) {
				b.append("\"calculated1RM\":\"" + _move.getCalculated1RMString(_logged_in_person) + "\",");
			}
		}
		
		if (_include_move_chart_data) {
			
			Vector corresponding_sets = WorkoutSet.getLastWorkoutSetsForMove(_logged_in_person, _move, 20); // include last 10 workouts
			
			HashMap<String,BigDecimal> volume_hash = new HashMap();
			HashMap<String,Vector> rep_hash = new HashMap();
			HashMap<Short,String[]> set_hash = new HashMap();
			
			short max_set_number = 0;
			short num_workouts = 0;
			Workout last_workout = null;
			
			HashMap<Workout,Short> workout_number_hash = new HashMap();
			
			short set_number = (short)0;
			String last_date_key = "";
			
			Iterator itr = corresponding_sets.iterator();
			while (itr.hasNext()) {
				WorkoutSet workout_set = (WorkoutSet)itr.next();
				String date_key = workout_set.getSetDateString();
				
				BigDecimal weight_lifted_for_set = workout_set.getActualWeight();
				if (weight_lifted_for_set != null) {
					//BigDecimal set_volume = workout_set.getActualWeight().multiply(new BigDecimal(workout_set.getActualReps()));
					// 7/27/18 - change set volume calculation to *NOT* include weight, so, just reps * sets
					BigDecimal set_volume = new BigDecimal(workout_set.getActualReps());
					if (volume_hash.containsKey(date_key)) {
						BigDecimal volume_bd = volume_hash.get(date_key);
						volume_hash.put(date_key, volume_bd.add(set_volume));
					} else {
						volume_hash.put(date_key, set_volume);
					}
				}
				
				short actual_reps = workout_set.getActualReps();
				if (rep_hash.containsKey(date_key)) {
					Vector<Short> vec = rep_hash.get(date_key);
					vec.addElement(actual_reps);
				} else {
					Vector<Short> vec = new Vector();
					vec.addElement(actual_reps);
					rep_hash.put(date_key, vec);
				}
				
				Workout workout_obj = workout_set.getWorkout();
				if (last_workout == null) {
					last_workout = workout_obj;
					num_workouts++;
					workout_number_hash.put(workout_obj, num_workouts);
				} else {
					if (!last_workout.equals(workout_obj)) {
						last_workout = workout_obj;
						num_workouts++;
						workout_number_hash.put(workout_obj, num_workouts);
					}
				}
				
			}
			
			set_number = (short)0;
			last_date_key = "";
			
			Vector label_vec = new Vector();
			itr = corresponding_sets.iterator();
			while (itr.hasNext()) {
				
				set_number = (short)0;
				
				WorkoutSet workout_set = (WorkoutSet)itr.next();
				String date_key = workout_set.getSetDateString();
				if (rep_hash.containsKey(date_key)) {
					
					boolean all_reps_equal = true;
					Short last_rep_count = -1;
					String set_label = "";
					
					Vector reps_vec = rep_hash.get(date_key);
					Iterator rep_itr = reps_vec.iterator();
					while (rep_itr.hasNext()) {
						Short rep_count = (Short)rep_itr.next();
						if (last_rep_count > -1) {
							if (rep_count != last_rep_count) {
								all_reps_equal = false;
							}
						}
						
						if (set_label.isEmpty()) {
							set_label = "" + rep_count;
						} else {
							set_label += ( "," + rep_count );
						}
						
						last_rep_count = rep_count;
						
						set_number++;
					}
					
					if (all_reps_equal) {
						set_label = reps_vec.size() + "x" + last_rep_count;
					}
					
					// ["1/18/18","4x10"]
					String label_str = "[\"" + date_key + "\",\"" + set_label + "\"]";
					if (!label_vec.contains(label_str)) {
						label_vec.addElement(label_str);
					}
				}
				
				
				if (set_number > max_set_number) {
					max_set_number = set_number;
				}
				
				
			}
			
			max_set_number--;
			//System.out.println("max_set_number >" + max_set_number);
			
			
			// ensure that all the arrays in set_hash are padded with zeros
			
			for (short i = (short)0; i <= max_set_number; i++) {
				String[] set_str_arr = new String[num_workouts];
				for (int x = 0; x < num_workouts; x++) {
					set_str_arr[x] = "0";
				}
				set_hash.put(i, set_str_arr);
			}
			
			
			
			
			b.append("\"chartLabel\":[");
			boolean needs_comma = false;
			//itr = corresponding_sets.iterator();
			// [["1/18/18","4x10"], ["1/21/18","9,10,10,10"], "1/22 (9,5,5,5)", "1/24 (9,5,5,5)"]
			itr = label_vec.iterator();
			while (itr.hasNext()) {
				String set_label = (String)itr.next();
				if (needs_comma) { b.append(','); }
				b.append(set_label);
				needs_comma = true;
			}
			b.append("],");
			
			b.append("\"volume\":[");
			needs_comma = false;
			//System.out.println("num corresponding_sets >" + corresponding_sets.size());
			itr = corresponding_sets.iterator();
			while (itr.hasNext()) {
				WorkoutSet workout_set = (WorkoutSet)itr.next();
				String date_key = workout_set.getSetDateString();
				//System.out.println("set_date >" + date_key);
				//System.out.println("volume_hash.containsKey(date_key) >" + volume_hash.containsKey(date_key));
				//System.out.println("(!date_key.equals(last_date_key)) >" + (!date_key.equals(last_date_key)));
				if ( !date_key.equals(last_date_key) ) {
					if (needs_comma) { b.append(','); }
					BigDecimal volume = BigDecimal.ZERO;
					if (volume_hash.containsKey(date_key)) {
						volume = volume_hash.get(date_key);
					}
					b.append(volume.setScale(0, RoundingMode.HALF_UP));
					needs_comma = true;
					//System.out.println("date_key >" + date_key);
					//System.out.println("last_date_key >" + last_date_key);
					if (!date_key.equals(last_date_key)) {
						set_number = (short)0;
					}
					last_date_key = date_key;
				}
				
				Workout workout_obj = workout_set.getWorkout();
				short workout_number = workout_number_hash.get(workout_obj);
				String actual_weight_str = workout_set.getActualWeightString();
				//short set_number = workout_set.getTrainingPlanSet().getSetNumber();
				//System.out.println("workout_set XXX >" + workout_set.getSetDateShortString());
				//System.out.println("set_number XXX >" + set_number);
				String[] arr = set_hash.get(set_number);
				//System.out.println("arr XXX >" + arr);
				arr[workout_number - 1] = actual_weight_str;
				
				set_number++;
			}
			b.append("],");
			
			
			b.append("\"chartSet\":[");
			needs_comma = false;
			for (short setNumber = (short)0; setNumber < 100; setNumber++) {
				//System.out.println("setNumber >" + setNumber);
				if (set_hash.containsKey(setNumber)) {
					if (needs_comma) { b.append(','); }
					b.append("[");
					//Vector<String> set_vec = set_hash.get(setNumber);
					String[] set_arr = set_hash.get(setNumber);
					boolean needs_comma_x = false;
					//itr = set_vec.iterator();
					//while (itr.hasNext()) {
					for (int i = 0; i < set_arr.length; i++) {
						//String actual_weight_str = (String)itr.next();
						String actual_weight_str = set_arr[i];
						if (needs_comma_x) { b.append(','); }
						b.append("\"" + actual_weight_str + "\"");
						needs_comma_x = true;
					}
					b.append("]");
					needs_comma = true;
				} else {
					break;
				}
			}
			b.append("],");
		}
		b.append("\"id\":" + _move.getId() + "}");
		return b.toString();
	}
	
	/*
	private String toChartDataSet(String _label, String _type, String _backgroundColor, String _data) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + _label + "\",");
		b.append("\"type\":\"" + _type + "\",");
		String videoURL = _move.getMove().getVideoURL();
		if (!videoURL.isEmpty()) {
			b.append("\"videoURL\":\"" + JSONObject.escape(videoURL) + "\",");
		}
		b.append("\"moveNumber\":" + _move.getMoveNumber() + ",");
		b.append("\"type\":\"" + _move.getTypeString() + "\",");
		b.append("\"pr\":\"" + _move.getMove().getPRString(_logged_in_person) + "\",");
		b.append("\"isSuperSet\":" + _move.isSuperSet() + ",");
		b.append("\"moveId\":" + _move.getMove().getId() + ",");
		b.append("\"id\":" + _move.getId() + "}");
		return b.toString();
	}
	*/
	
	private String toJSON(UKOnlinePersonBean _logged_in_person, TrainingPlanMove _move, boolean _include_move_list) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		StringBuilder b = new StringBuilder();
		b.append("{\"label\":\"" + JSONObject.escape(_move.getLabel()) + "\",");
		b.append("\"desc\":\"" + JSONObject.escape(_move.getDescription()) + "\",");
		String videoURL = _move.getMove().getVideoURL();
		if (!videoURL.isEmpty()) {
			b.append("\"videoURL\":\"" + JSONObject.escape(videoURL) + "\",");
		}
		b.append("\"moveNumber\":" + _move.getMoveNumber() + ",");
		b.append("\"type\":\"" + _move.getTypeString() + "\",");
		b.append("\"pr\":\"" + _move.getMove().getPRString(_logged_in_person) + "\",");
		b.append("\"isSuperSet\":" + _move.isSuperSet() + ",");
		b.append("\"isCompound\":" + _move.getMove().isCompoundLift() + ",");
		b.append("\"moveId\":" + _move.getMove().getId() + ",");
		b.append("\"id\":" + _move.getId() + "}");
		return b.toString();
	}
	
	private String toJSON(UKOnlinePersonBean _person, TrainingPlanSet _set, Workout _workout, int _setNumber) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		//String lastRepsStr = "";
		//String lastWeightStr = "";
		
		boolean isComplete = false;
		String actualRepsString = null;
		String actualWeightStr = null;
		String actualRPEStr = null;
		
		/*
		try {
			//WorkoutSet last_set = WorkoutSet.getLastCorrespondingWorkoutSet(_person, _set);
			WorkoutSet last_set = WorkoutSet.getLastCorrespondingWorkoutSet(_person, _set.getParent().getMove(), (short)(_setNumber - 1) );
			System.out.println("last_set date >" + last_set.getSetDateString());
			lastRepsStr = last_set.getActualRepsString();
			lastWeightStr = last_set.getActualWeightString();
		} catch (ObjectNotFoundException x) {
			//System.out.println(x.getMessage());
		}
		*/
		
		//System.out.println("_workout >" + _workout);
		String setTimeStr = "n/a";
		if (_workout != null) {
			try {
				WorkoutSet set_obj = WorkoutSet.getSet(_workout, _set); // 10/14/17 possible performance concern
				actualRepsString = set_obj.getActualRepsString();
				actualWeightStr = set_obj.getActualWeightString();
				actualRPEStr = set_obj.getActualRPEString();
				isComplete = set_obj.isComplete();
				System.out.println("   ...isComplete >" + isComplete);
				setTimeStr = set_obj.getSetTimeString();
			} catch (ObjectNotFoundException x) {
				x.printStackTrace();
			}
		}
		
		if (_set.isExt() && !isComplete) {
			return null;
		}
		
		StringBuilder b = new StringBuilder();
		//b.append("{\"label\":\"" + JSONObject.escape(_set.getLabel(_person)) + "\",");
		if (_set.getWeekNumber() > (short)0) {
			b.append("{\"setNumber\":\"" + _set.getWeekNumber() + "-" + _setNumber + "\",");
		} else {
			b.append("{\"setNumber\":\"" + _setNumber + "\",");
		}
		//b.append("\"lastReps\":\"" + JSONObject.escape(lastRepsStr) + "\",");
		short repGoalMin = _set.getRepGoalMin(_person);
		if (repGoalMin > (short)0) {
			b.append("\"repMin\":" + repGoalMin + ",");
		}
		if (_set.getRepGoalMax(_person) > _set.getRepGoalMin(_person)) {
			b.append("\"repMax\":" + _set.getRepGoalMax(_person) + ",");
		}
		
		if (_set.isPerc1RM()) {
			String calculated_weight_goal_for_set = _set.getCalculatedWeightGoalString(_person);
			if (!calculated_weight_goal_for_set.isEmpty()) {
				b.append("\"calculatedWeightGoal\":\"" + calculated_weight_goal_for_set + "\",");
			}
			b.append("\"weightGoal\":\"" + _set.getOneRepMaxPercentageString() + "\",");
		} else {
			b.append("\"weightGoal\":\"" + _set.getWeightGoalString(_person) + "\",");
		}
		
		//b.append("\"lastWeight\":\"" + JSONObject.escape(lastWeightStr) + "\",");
		b.append("\"is1RMPerc\":" + _set.isPerc1RM() + ",");
		b.append("\"isDropSet\":" + _set.isDropSet() + ",");
		b.append("\"isAMRAP\":" + _set.isAMRAP() + ",");
		b.append("\"isAMWAP\":" + _set.isAMWAP() + ",");
		
		String rpeStr = _set.getRPEGoal();
		if (rpeStr != null) {
			b.append("\"rpe\":\"" + JSONObject.escape(rpeStr) + "\",");
		}
		b.append("\"isComplete\":" + isComplete + ",");
		b.append("\"setTime\":\"" + JSONObject.escape(setTimeStr) + "\",");
		if (isComplete) {
			if (actualRepsString != null) {
				b.append("\"actualReps\":\"" + actualRepsString + "\",");
			}
			if (actualWeightStr != null) {
				b.append("\"actualWeight\":\"" + actualWeightStr + "\",");
			}
			if ( (actualRPEStr != null) && !actualRPEStr.isEmpty() ) {
				b.append("\"actualRPE\":\"" + actualRPEStr + "\",");
			}
			
		}
		b.append("\"id\":" + _set.getId() + "}");
		return b.toString();
	}
	
	private String
	toChartJSON(Date _date, String _value, TimeZone _time_zone) {
		// 08/25/2017 13:39
		return "{\"x\":\"" + CUBean.getUserDateString(_date, "MM/dd/yyyy HH:mm", _time_zone) + "\",\"y\":" + _value + "}";
		//return "{\"x\":\"" + CUBean.getUserDateString(_date) + "\",\"y\":" + _value + "}";
	}
	
	private String
	toChartJSON(String _date, BigDecimal _value) {
		// 08/25/2017 13:39  + " 12:00\"
		return "{\"x\":\"" + _date + " 12:00\",\"y\":" + FitnessServlet.getStringFromBigDecimal(_value) + "}";
		//return "{\"x\":\"" + CUBean.getUserDateString(_date) + "\",\"y\":" + _value + "}";
	}
	
	private String
	addMacroEntry(UKOnlinePersonBean _logged_in_person, String _jsonBlob, String _actualAmountConsumed, String _unitType) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		System.out.println("  *** addMacroEntry() invoked");
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		if (_actualAmountConsumed == null || _actualAmountConsumed.isEmpty()) {
			throw new IllegalValueException("Invalid amount for " + _unitType);
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String inputEntryName = null;
		BigDecimal inputServingSize = null;
		String servingSizeUnit = null;
		BigDecimal inputFat = BigDecimal.ZERO;
		BigDecimal inputCarbs = BigDecimal.ZERO;
		BigDecimal inputProtein = BigDecimal.ZERO;
		Food fud = null;
		
		Calendar entryDate = Calendar.getInstance();
		
		int monthSelect = entryDate.get(Calendar.MONTH);
		int dateSelect = entryDate.get(Calendar.DATE);
		int yearSelect = entryDate.get(Calendar.YEAR);
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputEntryName")) {
								inputEntryName = value_strX;
							} else if (last_value.equals("inputServingSize")) {
								inputServingSize = new BigDecimal(value_strX);
							} else if (last_value.equals("servingSizeUnit")) {
								servingSizeUnit = value_strX;
							} else if (last_value.equals("inputFat")) {
								inputFat = new BigDecimal(value_strX);
							} else if (last_value.equals("inputCarbs")) {
								inputCarbs = new BigDecimal(value_strX);
							} else if (last_value.equals("inputProtein")) {
								inputProtein = new BigDecimal(value_strX);
							} else if (last_value.equals("foodId")) {
								long food_id = Long.parseLong(value_strX);
								if (food_id > 0l) {
									fud = Food.getFood(food_id);
								}
							} else if (last_value.equals("monthSelect")) {
								monthSelect = Integer.parseInt(value_strX);
							} else if (last_value.equals("dateSelect")) {
								dateSelect = Integer.parseInt(value_strX);
							} else if (last_value.equals("yearSelect")) {
								yearSelect = Integer.parseInt(value_strX);
							}
						}
						
					}
				}
			}
		}
		
		
		// resolve the date from this
		
		entryDate.set(Calendar.YEAR, yearSelect);
		entryDate.set(Calendar.MONTH, monthSelect);
		entryDate.set(Calendar.DATE, dateSelect);
		
		if ( (inputEntryName == null) || inputEntryName.isEmpty()) {
			throw new IllegalValueException("Please choose an entry name.");
		}
		
		
		if (fud == null) {
			
			short servingSizeUnitValue = Food.GRAMS_SERVING_SIZE_TYPE;
			if (servingSizeUnit != null) {
				servingSizeUnitValue = servingSizeUnit.equals("grams") ? Food.GRAMS_SERVING_SIZE_TYPE : servingSizeUnit.equals("ml") ? Food.ML_SERVING_SIZE_TYPE : Food.OZ_SERVING_SIZE_TYPE;
			}
			//short servingSizeUnitValue = servingSizeUnit.equals("grams") ? Food.GRAMS_SERVING_SIZE_TYPE : servingSizeUnit.equals("ml") ? Food.ML_SERVING_SIZE_TYPE : Food.OZ_SERVING_SIZE_TYPE;
				
			try {
				fud = Food.getFood(inputEntryName, inputServingSize, servingSizeUnitValue, inputFat, inputCarbs, inputProtein);
			} catch (ObjectNotFoundException x) {
				fud = new Food();
				fud.setCreateOrModifyPerson(_logged_in_person);
				fud.setName(inputEntryName);
				//fud.setCalories(BigDecimal.ONE);
				fud.setFat(inputFat);
				fud.setCarbs(inputCarbs);
				fud.setProtein(inputProtein);

				fud.setServingSize(inputServingSize);
				fud.setServingSizeType(servingSizeUnitValue);
				fud.setActive(true);
				fud.save();
			}
		}
		
		BigDecimal amount_consumed = new BigDecimal(_actualAmountConsumed);
		
		MacroGoalEntry entry = null;
		
		MacroGoalDaily daily = MacroGoalDaily.getCurrentMacroGoalDaily(_logged_in_person, entryDate.getTime());
		
		if (_unitType.equals("grams")) {
			entry = daily.addEntryByGrams(inputEntryName, inputFat, inputCarbs, inputProtein, amount_consumed, inputServingSize, servingSizeUnit, fud, entryDate.getTime());
		} else if (_unitType.equals("serving")) {
			entry = daily.addEntryByServing(inputEntryName, inputFat, inputCarbs, inputProtein, amount_consumed, inputServingSize, servingSizeUnit, fud, entryDate.getTime());
		} else if (_unitType.equals("ounces")) {
			entry = daily.addEntryByOunces(inputEntryName, inputFat, inputCarbs, inputProtein, amount_consumed, inputServingSize, servingSizeUnit, fud, entryDate.getTime());
		}
		
		return this.toJSON(daily);
		
		//return this.toJSON(entry);
		
	}
	
	private String
	saveWorkout(UKOnlinePersonBean _logged_in_person, TrainingPlan _plan, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (!_plan.canEdit(_logged_in_person)) {
			throw new IllegalValueException("Unable to save workout.  You don't have permission to modify training plan " + _plan.getLabel());
		}
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		// [{"name":"selectedWorkoutId","value":""},{"name":"nameInput","value":"sdfgsdfg"},{"name":"descInput","value":"sdfgsdfg"},{"name":"inputWeekNumber","value":"1"},{"name":"inputDayNumber","value":"-1"},{"name":"inputWorkoutNumber","value":"2"}]
		
		String last_value = "";
		
		TrainingPlanWorkout selectedWorkout = null;
		String nameInput = null;
		String descInput = null;
		
		Short inputBlockNumber = null;
		Short inputWeekNumber = null;
		//Short inputDayNumber = null;
		//Short inputWorkoutNumber = null;
		
		boolean isCopy = false;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("selectedWorkoutId")) {
								long training_plan_workout_id = Long.parseLong(value_strX);
								if (training_plan_workout_id > 0l) {
									selectedWorkout = TrainingPlanWorkout.getTrainingPlanWorkout(training_plan_workout_id);
								}
							} else if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("descInput")) {
								descInput = value_strX;
							} else if (last_value.equals("inputWeekNumber")) {
								inputWeekNumber = Short.parseShort(value_strX);
							} else if (last_value.equals("inputBlockNumber")) {
								inputBlockNumber = Short.parseShort(value_strX);
							} else if (last_value.equals("inputDayNumber")) {
								//inputDayNumber = Short.parseShort(value_strX);
							} else if (last_value.equals("inputWorkoutNumber")) {
								//inputWorkoutNumber = Short.parseShort(value_strX);
							} else if (last_value.equals("isCopy")) {
								isCopy = value_strX.equals("true");
							}
						}
						
					}
				}
			}
		}
		
		if (nameInput == null) {
			throw new IllegalValueException("Please specify a workout name.");
		}
		
		if (inputWeekNumber == null) {
			throw new IllegalValueException("Please specify a week number.");
		}
		
		if (isCopy && (selectedWorkout == null)) {
			throw new IllegalValueException("Unable to copy workout!");
		}
		
		if (isCopy) {
			
			System.out.println("selectedWorkout >" + selectedWorkout.getLabel());
			
			TrainingPlanWorkout copy = new TrainingPlanWorkout();
			
			copy.setParent(_plan);
			copy.setWorkoutNumber((short)_plan.getWorkouts().size());
			_plan.invalidate();
			copy.setName(nameInput);
			if (descInput != null) {
				copy.setDescription(descInput);
			}
			if (inputBlockNumber != null) {
				copy.setBlockNumber(inputBlockNumber);
			}
			if (inputWeekNumber != null) {
				copy.setWeekNumber(inputWeekNumber);
			}
			copy.save();
			
			Vector moves = new Vector();
			Iterator itr = selectedWorkout.getMoves().iterator();
			while (itr.hasNext()) {
				TrainingPlanMove move_to_copy = (TrainingPlanMove)itr.next();
				System.out.println("TrainingPlanMove TO COPY >" + move_to_copy.getLabel());
				moves.addElement(move_to_copy.copy(_logged_in_person, copy));
			}
			copy.setMoves(moves);
			copy.save();
			
			
		} else {
		
			if (selectedWorkout == null) {
				selectedWorkout = new TrainingPlanWorkout();
				selectedWorkout.setParent(_plan);
				selectedWorkout.setWorkoutNumber((short)_plan.getWorkouts().size());
				_plan.invalidate();
			}

			selectedWorkout.setName(nameInput);
			if (descInput != null) {
				selectedWorkout.setDescription(descInput);
			}
			if (inputBlockNumber != null) {
				selectedWorkout.setBlockNumber(inputBlockNumber);
			}
			if (inputWeekNumber != null) {
				selectedWorkout.setWeekNumber(inputWeekNumber);
			}

			selectedWorkout.save();

		}
		
		
		return this.toJSON(_logged_in_person, _plan, false, false);
		
		//return this.toJSON(entry);
		
	}
	
	private String
	saveMoveChanges(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		// [{"name":"selectedWorkoutId","value":""},{"name":"nameInput","value":"sdfgsdfg"},{"name":"descInput","value":"sdfgsdfg"},{"name":"inputWeekNumber","value":"1"},{"name":"inputDayNumber","value":"-1"},{"name":"inputWorkoutNumber","value":"2"}]
		
		String last_value = "";
		
		BigDecimal inputPR = null;
		boolean isSuperSet = false;
		
		HashMap<Long,TrainingPlanSet> hash = new HashMap<Long,TrainingPlanSet>();
		HashMap<Long,TrainingPlanSet> drop_set_hash = new HashMap<Long,TrainingPlanSet>();
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (last_value.equals("inputPR")) {
							if (!value_strX.isEmpty()) {
								inputPR = new BigDecimal(value_strX);
							}
						} else if (last_value.equals("isSuperSet")) {
							if (!value_strX.isEmpty()) {
								isSuperSet = value_strX.equals("on");
							}
						} else if (last_value.startsWith("inputRepsSetMax")) {  // 15
							Long set_id = Long.parseLong(last_value.substring(15));
							TrainingPlanSet set_obj = TrainingPlanSet.getTrainingPlanSet(set_id);
							if (!set_obj.getParent().getParent().getParent().canEdit(_logged_in_person)) {
								throw new IllegalValueException("Unable to save.  You don't have permission to modify training plan " + set_obj.getParent().getParent().getParent().getLabel());
							}
							if (value_strX.isEmpty()) {
								set_obj.setRepGoalMax(_logged_in_person, (short)0);
							} else {
								set_obj.setRepGoalMax(_logged_in_person, Short.parseShort(value_strX));
							}
							hash.put(set_id, set_obj);
						} else if (last_value.startsWith("inputRepsSet")) { // 12
							Long set_id = Long.parseLong(last_value.substring(12));
							TrainingPlanSet set_obj = TrainingPlanSet.getTrainingPlanSet(set_id);
							if (!set_obj.getParent().getParent().getParent().canEdit(_logged_in_person)) {
								throw new IllegalValueException("Unable to save.  You don't have permission to modify training plan " + set_obj.getParent().getParent().getParent().getLabel());
							}
							if (value_strX.isEmpty()) {
								set_obj.setRepGoalMin(_logged_in_person, (short)0);
							} else {
								set_obj.setRepGoalMin(_logged_in_person, Short.parseShort(value_strX));
							}
							hash.put(set_id, set_obj);
						} else if (last_value.startsWith("inputGoalWeightSet")) { // 18
							Long set_id = Long.parseLong(last_value.substring(18));
							TrainingPlanSet set_obj = TrainingPlanSet.getTrainingPlanSet(set_id);
							if (!set_obj.getParent().getParent().getParent().canEdit(_logged_in_person)) {
								throw new IllegalValueException("Unable to save.  You don't have permission to modify training plan " + set_obj.getParent().getParent().getParent().getLabel());
							}
							if (value_strX.isEmpty()) {
								set_obj.setWeightGoal(_logged_in_person, null);
							} else {
								if (set_obj.isPerc1RM()) {
									set_obj.setOneRepMaxPercentage(new BigDecimal(value_strX));
								} else if (set_obj.isRPE()) {
									set_obj.setRPEGoal(value_strX);
								} else {
									set_obj.setWeightGoal(_logged_in_person, new BigDecimal(value_strX));
								}
							}
							hash.put(set_id, set_obj);
						} else if (last_value.startsWith("isDropSet")) { // 9
							Long set_id = Long.parseLong(last_value.substring(9));
							TrainingPlanSet set_obj = TrainingPlanSet.getTrainingPlanSet(set_id);
							if (!set_obj.getParent().getParent().getParent().canEdit(_logged_in_person)) {
								throw new IllegalValueException("Unable to save.  You don't have permission to modify training plan " + set_obj.getParent().getParent().getParent().getLabel());
							}
							hash.put(set_id, set_obj);
							drop_set_hash.put(set_id, set_obj);
						}
						
						
					}
				}
			}
		}
		
		Move move = null;
		
		
		Iterator itr = hash.keySet().iterator();
		while (itr.hasNext()) {
			Long set_id = (Long)itr.next();
			TrainingPlanSet set_obj = TrainingPlanSet.getTrainingPlanSet(set_id);
			set_obj.setIsDropSet(drop_set_hash.containsKey(set_id));
			set_obj.save();
			
			if (move == null) {
				move = set_obj.getParent().getMove();
			}
			//set_obj.getParent()
		}
		
		System.out.println("inputPR >" + inputPR);
		System.out.println("isSuperSet >" + isSuperSet);
		
		if (move != null) {
			if (inputPR != null) {
				move.setPRWeight(_logged_in_person, inputPR);
			}
			//move.
		}
		
		//return this.toJSON(_plan);
		
		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Changes Saved\"}]}");
		return b.toString();
	}
	
	
	private void
	addMove(UKOnlinePersonBean _logged_in_person, TrainingPlanWorkout _workout, String _jsonBlob, boolean _save_move, boolean _add_move, boolean _add_alt_move) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		System.out.println("addMove() invoked");
		System.out.println("_save_move >" + _save_move);
		System.out.println("_add_move >" + _add_move);
		System.out.println("_add_alt_move >" + _add_alt_move);
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String inputMoveName = null;
		String moveType = null;
		String inputMoveURL = null;
		String inputDescInput = null;
		BigDecimal inputPR = null;
		
		boolean isCompound = false;
		boolean isActive = false;
		boolean isPublic = false;
		boolean isSuperSet = false;
		
		short inputSets = (short)0;
		short inputRepsMin = (short)0;
		short inputRepsMax = (short)0;
		boolean addIs1RMPerc = false;
		BigDecimal addGoalWeightSet = BigDecimal.ZERO;
		
		String inputRPEGoal = null;
		
		/*
		Short inputRepsSet = null;
		Short inputGoalWeightSet = null;
		*/
		Move move = null;
		TrainingPlanMove plan_move = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (value_strX.isEmpty()) {
							/*
							if (last_value.equals("isSuperSet")) {
								isSuperSet = true;
							}
							*/
						} else {
							if (last_value.equals("inputMoveName")) {
								inputMoveName = value_strX;
							} else if (last_value.equals("moveType")) {
								moveType = value_strX;
							} else if (last_value.equals("inputMoveURL")) {
								inputMoveURL = value_strX;
							} else if (last_value.equals("inputDescInput")) {
								inputDescInput = value_strX;
							} else if (last_value.equals("inputPR")) {
								try {
									inputPR = new BigDecimal(value_strX);
								} catch (Exception x) { }
							} else if (last_value.equals("moveId")) {
								int move_id = Integer.parseInt(value_strX);
								if (move_id > 0) {
									move = Move.getMove(move_id);
								}
							} else if (last_value.equals("moveSelect")) {
								int move_id = Integer.parseInt(value_strX);
								if (move_id > 0) {
									move = Move.getMove(move_id);
								}
							} else if (last_value.equals("id")) {
								long plan_move_id = Long.parseLong(value_strX);
								if (plan_move_id > 0) {
									plan_move = TrainingPlanMove.getTrainingPlanMove(plan_move_id);
								}
							} else if (last_value.equals("isSuperSet")) {
								isSuperSet = true;
							} else if (last_value.equals("isCompound")) {
								isCompound = true;
							} else if (last_value.equals("isActive")) {
								isActive = true;
							} else if (last_value.equals("isPublic")) {
								isPublic = true;
							} else if (last_value.equals("inputSets")) {
								inputSets = Short.parseShort(value_strX);
							} else if (last_value.equals("inputRepsMin")) {
								inputRepsMin = Short.parseShort(value_strX);
							} else if (last_value.equals("inputRepsMax")) {
								inputRepsMax = Short.parseShort(value_strX);
							} else if (last_value.equals("inputRPEGoal")) {
								inputRPEGoal = value_strX;
							} else if (last_value.equals("addIs1RMPerc")) {
								addIs1RMPerc = value_strX.equals("on");
							} else if (last_value.equals("addGoalWeightSet")) {
								addGoalWeightSet = new BigDecimal(value_strX);
							}
						}
						
					}
				}
			}
		}
		
		
		
		if (_save_move) {
			if (inputMoveName == null || inputMoveName.isEmpty()) {
				throw new IllegalValueException("Please enter a move name.");
			}

			if (moveType == null) {
				throw new IllegalValueException("Please select a move type, lift or cardio.");
			}

			if (move == null) {
				/*
				try {
					move = Move.getMove(inputMoveName, inputDescInput);
				} catch (ObjectNotFoundException x) {
					move = new Move();
					//move.setCreateOrModifyPerson(_logged_in_person);
				}
				*/
				move = new Move();
			}

			move.setName(inputMoveName);
			move.setDescription(inputDescInput);
			move.setVideoURL(inputMoveURL);
			move.setType(moveType.toLowerCase().equals("lift") ? Move.LIFT_TYPE : Move.CARDIO_TYPE);
			if (inputPR != null) {
				move.setPRWeight(_logged_in_person, inputPR);
			}
			//plan_move.getMove().setIsCompoundLift(isCompound);
			move.setIsCompoundLift(isCompound);
			move.setActive(isActive);
			move.setPublic(isPublic);
			move.setCreateOrModifyPerson(_logged_in_person);
			move.save();
		}
		
		if (_add_move) {
			
			
			
			if (move == null) {
				throw new IllegalValueException("Please select a move to add.");
			}
			
			if (_workout == null) {
				throw new IllegalValueException("You must add a workout to the training plan and then add moves to the workout.");
			}
			
			
			Vector workout_moves = _workout.getMoves();

			if (plan_move == null) {
				plan_move = new TrainingPlanMove();
				plan_move.setMoveNumber( (short)(workout_moves.size() + 1) );
			}
			plan_move.setWorkout(_workout);
			plan_move.setIsSuperSet(isSuperSet);
			plan_move.setMove(move);
			plan_move.setDescription(inputDescInput);
			
			move.setActive(true); // active to start with
			move.setPublic(isPublic);
			
			plan_move.save();
			//plan_move.getMove().setIsCompoundLift(isCompound);
			plan_move.getMove().setCreateOrModifyPerson(_logged_in_person);
			plan_move.getMove().save();

			/*
			short inputSets = (short)0;
		short inputRepsMin = (short)0;
		short inputRepsMax = (short)0;
		boolean addIs1RMPerc = false;
		short addGoalWeightSet = (short)0;
			*/
		

			for (short i = (short)0; i < inputSets; i++) {

				TrainingPlanSet set = new TrainingPlanSet();

				set.setParent(plan_move);
				//set.setIsDropSet(is_drop_set);
				//set.setIsAMRAP(is_asrap);
				//set.setIsAMWAP(is_aswap);
				set.setIsPerc1RM(addIs1RMPerc);

				Vector sets = plan_move.getSets();
				set.setSetNumber((short)sets.size());

				set.save();
				set.invalidate();

				// the following 3 sets require that the set already be saved
				if (inputRepsMin > 0) {
					set.setRepGoalMin(_logged_in_person, inputRepsMin);
				}
				if (inputRepsMax > 0) {
					set.setRepGoalMax(_logged_in_person, inputRepsMax);
				}
				if (inputRPEGoal != null) {
					set.setRPEGoal(inputRPEGoal);
				}
				if (addGoalWeightSet.compareTo(BigDecimal.ZERO) == 1) {
					if (addIs1RMPerc) {
						set.setOneRepMaxPercentage(addGoalWeightSet);
					} else {
						set.setWeightGoal(_logged_in_person, addGoalWeightSet);
					}
				}

				set.save();

				sets.addElement(set);
			}
		

			_workout.invalidate();
		}
		
		if (_add_alt_move) {
			
			if (move == null) {
				throw new IllegalValueException("Please select a move to add.");
			}
			
			Workout workout = Workout.getCurrentWorkout(_logged_in_person);
			TrainingPlanMove added_alt_move = workout.addMove(move, inputDescInput);
			
			for (short i = (short)0; i < inputSets; i++) {

				TrainingPlanSet set = new TrainingPlanSet();

				set.setParent(added_alt_move);
				//set.setIsDropSet(is_drop_set);
				//set.setIsAMRAP(is_asrap);
				//set.setIsAMWAP(is_aswap);
				set.setIsPerc1RM(addIs1RMPerc);

				Vector sets = added_alt_move.getSets();
				set.setSetNumber((short)sets.size());

				set.save();
				set.invalidate();

				// the following 3 sets require that the set already be saved
				if (inputRepsMin > 0) {
					set.setRepGoalMin(_logged_in_person, inputRepsMin);
				}
				if (inputRepsMax > 0) {
					set.setRepGoalMax(_logged_in_person, inputRepsMax);
				}
				
				if (inputRPEGoal != null) {
					set.setRPEGoal(inputRPEGoal);
				}
				
				if (addGoalWeightSet.compareTo(BigDecimal.ZERO) == 1) {
					if (addIs1RMPerc) {
						set.setOneRepMaxPercentage(addGoalWeightSet);
					} else {
						set.setWeightGoal(_logged_in_person, addGoalWeightSet);
					}
				}

				set.save();

				sets.addElement(set);
			}
			
			
			
		}
		
		
		
		/*
		MacroGoalEntry entry = null;
		
		if (_unitType.equals("grams")) {
			entry = _daily.addEntryByGrams(inputEntryName, inputFat, inputCarbs, inputProtein, amount_consumed, inputServingSize, servingSizeUnit, fud);
		} else if (_unitType.equals("serving")) {
			entry = _daily.addEntryByServing(inputEntryName, inputFat, inputCarbs, inputProtein, amount_consumed, inputServingSize, servingSizeUnit, fud);
		} else if (_unitType.equals("ounces")) {
			entry = _daily.addEntryByOunces(inputEntryName, inputFat, inputCarbs, inputProtein, amount_consumed, inputServingSize, servingSizeUnit, fud);
		}
		
		return this.toJSON(_daily);
		*/
		
		//return this.toJSON(entry);
		
	}
	
	/*
	private String
	addSetEntry(UKOnlinePersonBean _logged_in_person, MacroGoalDaily _daily, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		
		String last_value = "";
		
		String inputEntryName = null;
		BigDecimal inputServingSize = null;
		String servingSizeUnit = null;
		BigDecimal inputFat = BigDecimal.ZERO;
		BigDecimal inputCarbs = BigDecimal.ZERO;
		BigDecimal inputProtein = BigDecimal.ZERO;
		Food fud = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputEntryName")) {
								inputEntryName = value_strX;
							} else if (last_value.equals("inputServingSize")) {
								inputServingSize = new BigDecimal(value_strX);
							} else if (last_value.equals("servingSizeUnit")) {
								servingSizeUnit = value_strX;
							} else if (last_value.equals("inputFat")) {
								inputFat = new BigDecimal(value_strX);
							} else if (last_value.equals("inputCarbs")) {
								inputCarbs = new BigDecimal(value_strX);
							} else if (last_value.equals("inputProtein")) {
								inputProtein = new BigDecimal(value_strX);
							} else if (last_value.equals("foodId")) {
								long food_id = Long.parseLong(value_strX);
								if (food_id > 0l) {
									fud = Food.getFood(food_id);
								}
							}
						}
						
					}
				}
			}
		}
		
		short servingSizeUnitValue = servingSizeUnit.equals("grams") ? Food.GRAMS_SERVING_SIZE_TYPE : servingSizeUnit.equals("ml") ? Food.ML_SERVING_SIZE_TYPE : Food.OZ_SERVING_SIZE_TYPE;

		if (fud == null) {
			try {
				fud = Food.getFood(inputEntryName, inputServingSize, servingSizeUnitValue, inputFat, inputCarbs, inputProtein);
			} catch (ObjectNotFoundException x) {
				fud = new Food();
				fud.setCreateOrModifyPerson(_logged_in_person);
				fud.setName(inputEntryName);
				//fud.setCalories(BigDecimal.ONE);
				fud.setFat(inputFat);
				fud.setCarbs(inputCarbs);
				fud.setProtein(inputProtein);

				fud.setServingSize(inputServingSize);
				fud.setServingSizeType(servingSizeUnitValue);
				fud.save();
			}
		}
		
		BigDecimal amount_consumed = new BigDecimal(_actualAmountConsumed);
		
		MacroGoalEntry entry = null;
		
		if (_unitType.equals("grams")) {
			entry = _daily.addEntryByGrams(inputEntryName, inputFat, inputCarbs, inputProtein, amount_consumed, inputServingSize, servingSizeUnit, fud);
		} else if (_unitType.equals("serving")) {
			entry = _daily.addEntryByServing(inputEntryName, inputFat, inputCarbs, inputProtein, amount_consumed, inputServingSize, servingSizeUnit, fud);
		} else if (_unitType.equals("ounces")) {
			entry = _daily.addEntryByOunces(inputEntryName, inputFat, inputCarbs, inputProtein, amount_consumed, inputServingSize, servingSizeUnit, fud);
		}
		
		return this.toJSON(_daily);
		
		//return this.toJSON(entry);
		
	}
	*/
	
	
	
	private String
	addWeightEntry(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		Calendar entryDate = Calendar.getInstance();
		
		int monthSelect = entryDate.get(Calendar.MONTH);
		int dateSelect = entryDate.get(Calendar.DATE);
		int yearSelect = entryDate.get(Calendar.YEAR);
		BigDecimal inputWeight = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("monthSelect")) {
								monthSelect = Integer.parseInt(value_strX);
							} else if (last_value.equals("dateSelect")) {
								dateSelect = Integer.parseInt(value_strX);
							} else if (last_value.equals("yearSelect")) {
								yearSelect = Integer.parseInt(value_strX);
							} else if (last_value.equals("inputWeight")) {
								inputWeight = new BigDecimal(value_strX);
							}
						}
					}
				}
			}
		}
		
		if (inputWeight == null) {
			throw new IllegalValueException("Please enter a weight.");
		}
		
		// resolve the date from this
		
		entryDate.set(Calendar.YEAR, yearSelect);
		entryDate.set(Calendar.MONTH, monthSelect);
		entryDate.set(Calendar.DATE, dateSelect);
		
		// get the current weight goal for this person
		
		WeightGoal weight_goal = WeightGoal.getCurrentWeightGoal(_logged_in_person);
		WeightPreferences preferences = WeightPreferences.getWeightPreference(_logged_in_person);
		WeightEntry entry = weight_goal.addEntry(entryDate.getTime(), inputWeight, preferences);
		
		
		//return this.toJSON(entry);
		
		return this.toJSON(weight_goal, 14);
		
	}
	
	private Workout
	finishWorkout(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String inputWorkoutNotes = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputWorkoutNotes")) {
								inputWorkoutNotes = value_strX;
							}
						}
					}
				}
			}
		}
		
		Workout workout_to_finish = Workout.getCurrentWorkout(_logged_in_person);
		workout_to_finish.setEndDate(new Date());
		workout_to_finish.setNotes(inputWorkoutNotes);
		workout_to_finish.save();
		
		return workout_to_finish;
		
	}
	
	private String
	addSet(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		// [{"name":"addRepsSet","value":"1"},{"name":"addRepsSetMax","value":"2"},{"name":"addRepsSetRPW","value":"8"},{"name":"addGoalWeightSet","value":"66"},{"name":"addIsDropSet","value":""},{"name":"addIsAMRAP","value":""},{"name":"addIsAMWAP","value":""},{"name":"id","value":"1562"}]
		
		String last_value = "";
		
		Short addRepsSet = null;
		Short addRepsSetMax = null;
		String addRepsSetRPE = null;
		BigDecimal addGoalWeightSet = null;
		boolean addIs1RMPerc = false;
		boolean addIsDropSet = false;
		boolean addIsAMRAP = false;
		boolean addIsAMWAP = false;
		TrainingPlanMove move = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (value_strX.isEmpty()) {
							if (last_value.equals("addIsDropSet")) {
								addIsDropSet = true;
							} else if (last_value.equals("addIsAMRAP")) {
								addIsAMRAP = true;
							} else if (last_value.equals("addIsAMWAP")) {
								addIsAMWAP = true;
							}
						} else {
							if (last_value.equals("addRepsSet")) {
								addRepsSet = Short.parseShort(value_strX);
							} else if (last_value.equals("addRepsSetMax")) {
								addRepsSetMax = Short.parseShort(value_strX);
							} else if (last_value.equals("addRepsSetRPE")) {
								addRepsSetRPE = value_strX;
							} else if (last_value.equals("addGoalWeightSet")) {
								addGoalWeightSet = new BigDecimal(value_strX);
							} else if (last_value.equals("addIs1RMPerc")) {
								addIs1RMPerc = value_strX.equals("on");
							} else if (last_value.equals("id")) {
								long id = Long.parseLong(value_strX);
								if (id > 0l) {
									move = TrainingPlanMove.getTrainingPlanMove(id);
								}
							}
						}
						
					}
				}
			}
		}
		
		//TrainingPlanMove move = TrainingPlanMove.getTrainingPlanMove(Long.parseLong(arg2));
		if (!move.getParent().getParent().canEdit(_logged_in_person)) {
			throw new IllegalValueException("Unable to add set.  You don't have permission to modify training plan " + move.getParent().getParent().getLabel());
		}
		
		if ( ( addGoalWeightSet != null ) && ( addRepsSetRPE != null ) ) {
			throw new IllegalValueException("Please choose either an RPE Goal OR a Weight Goal, not both.");
		}
		
		TrainingPlanSet set = new TrainingPlanSet();

		set.setParent(move);
		set.setIsDropSet(addIsDropSet);
		set.setIsAMRAP(addIsAMRAP);
		set.setIsAMWAP(addIsAMWAP);
		set.setIsPerc1RM(addIs1RMPerc);

		Vector sets = move.getSets();
		set.setSetNumber((short)sets.size());

		set.save();
		set.invalidate();

		// the following 3 sets require that the set already be saved
		if (addRepsSet != null) {
			set.setRepGoalMin(_logged_in_person, addRepsSet);
		}
		if (addRepsSetMax != null) {
			set.setRepGoalMax(_logged_in_person, addRepsSetMax);
		}
		if (addRepsSetRPE != null) {
			set.setRPEGoal(addRepsSetRPE);
		}
		if (addGoalWeightSet != null) {
			if (addIs1RMPerc) {
				set.setOneRepMaxPercentage(addGoalWeightSet);
			} else {
				set.setWeightGoal(_logged_in_person, addGoalWeightSet);
			}
		}

		set.save();
		sets.addElement(set);
		move.save();
		
		return this.toJSON(_logged_in_person, move.getParent().getParent(), false, false);
		
	}
	
	private String
	setMacroGoal(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		// [{"name":"nameInput","value":"prom"},{"name":"goalSelect","value":""},{"name":"inputCalories","value":""},{"name":"setGoalBy","value":"grams"},{"name":"fatSelect","value":""},{"name":"proteinSelect","value":""},{"name":"inputFat","value":""},{"name":"inputCarbs","value":""},{"name":"inputProtein","value":""},{"name":"goalId","value":"0"}]
		
		String last_value = "";
		
		String nameInput = null;
		String goalSelect = null;
		String inputCalories = null;
		
		String inputFat = null;
		String inputCarbs = null;
		String inputProtein = null;
		//String goalType = null;
		
		String fatSelect = null;
		String proteinSelect = null;
		
		//boolean isCurrent = false;
		boolean isAuto = false;
		
		String goalId = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("inputCalories")) {
								inputCalories = value_strX;
							} else if (last_value.equals("goalSelect")) {
								goalSelect = value_strX;
							} else if (last_value.equals("fatSelect")) {
								fatSelect = value_strX;
							} else if (last_value.equals("proteinSelect")) {
								proteinSelect = value_strX;
							} else if (last_value.equals("inputFat")) {
								inputFat = value_strX;
							} else if (last_value.equals("inputCarbs")) {
								inputCarbs = value_strX;
							} else if (last_value.equals("inputProtein")) {
								inputProtein = value_strX;
							} else if (last_value.equals("isCurrent")) {
								if (value_strX.equals("on")) {
									//isCurrent = true;
								}
							} else if (last_value.equals("isAuto")) {
								if (value_strX.equals("on")) {
									isAuto = true;
								}
							} else if (last_value.equals("goalId")) {
								goalId = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		//new BigDecimal(_fat, Food.return_context)
		
		if (inputFat == null) {
			throw new IllegalValueException("Please enter your fat goal in grams.");
		}
		if (inputCarbs == null) {
			throw new IllegalValueException("Please enter your carbs goal in grams.");
		}
		if (inputProtein == null) {
			throw new IllegalValueException("Please enter your protein goal in grams.");
		}
		
		
		MacroGoal goal_obj = null;
		if ( (goalId == null) || goalId.equals("0")) {
			goal_obj = new MacroGoal();
			goal_obj.setActive(false);
		} else {
			goal_obj = MacroGoal.getMacroGoal(Integer.parseInt(goalId));
		}
		
		goal_obj.setPerson(_logged_in_person);
		
		goal_obj.setName(nameInput);
		
		goal_obj.setFat(inputFat);
		goal_obj.setCarbs(inputCarbs);
		goal_obj.setProtein(inputProtein);
		goal_obj.setCalories(inputCalories);
		
		//goal_obj.setActive(isCurrent);
		goal_obj.setAuto(isAuto);
		
		if (goalSelect == null) {
			throw new IllegalValueException("Please select a calorie goal");
		}
		
		if (goalSelect.equals("0")) {
			goal_obj.setGoalType(MacroGoal.MAINTAIN_TYPE);
		} else if (goalSelect.equals("250")) {
			goal_obj.setGoalType(MacroGoal.LEAN_BULK_TYPE);
		} else if (goalSelect.equals("500")) {
			goal_obj.setGoalType(MacroGoal.RAPID_BULK_TYPE);
		} else if (goalSelect.equals("-250")) {
			goal_obj.setGoalType(MacroGoal.FAT_LOSS_TYPE);
		} else if (goalSelect.equals("-500")) {
			goal_obj.setGoalType(MacroGoal.RAPID_FAT_LOSS_TYPE);
		}
		
		if (fatSelect != null) {
			if (fatSelect.equals(".3")) {
				goal_obj.setFatGoal(MacroGoal.LOW_FAT_GOAL);
			} else if (fatSelect.equals(".4")) {
				goal_obj.setFatGoal(MacroGoal.RECOMMENDED_FAT_GOAL);
			} else if (fatSelect.equals(".5")) {
				goal_obj.setFatGoal(MacroGoal.HIGH_FAT_GOAL);
			} else if (fatSelect.equals("1.3")) {
				goal_obj.setFatGoal(MacroGoal.KETO_1_GOAL);
			} else if (fatSelect.equals("1.5")) {
				goal_obj.setFatGoal(MacroGoal.KETO_2_GOAL);
			}
		}
		
		if (proteinSelect != null) {
			if (proteinSelect.equals(".8")) {
				goal_obj.setProteinGoal(MacroGoal.LOW_PROTEIN_GOAL);
			} else if (proteinSelect.equals(".95")) {
				goal_obj.setProteinGoal(MacroGoal.RECOMMENDED_PROTEIN_GOAL);
			} else if (proteinSelect.equals("1.1")) {
				goal_obj.setProteinGoal(MacroGoal.HIGH_PROTEIN_GOAL);
			} else if (proteinSelect.equals("1.3")) {
				goal_obj.setProteinGoal(MacroGoal.HIGHER_PROTEIN_GOAL);
			}
		}
		
		goal_obj.setCreateOrModifyPerson(_logged_in_person);
		
		/*
		if (isCurrent) {
			boolean has_current_macro_goal = MacroGoal.hasCurrentMacroGoal(_logged_in_person);
			MacroGoal current_goal = null;
			if (has_current_macro_goal) {
				current_goal = MacroGoal.getCurrentMacroGoal(_logged_in_person);

				if (!current_goal.equals(goal_obj)) {
					current_goal.inActivate();
					current_goal.save();
					goal_obj.save();

					// if there are daily entries, move them to the new goal
					System.out.println("moving dailies...");
					

					MacroGoalDaily current_daily = MacroGoalDaily.getCurrentMacroGoalDaily(_logged_in_person);
					current_daily.setMacroGoal(goal_obj);
					current_daily.save();
				}
			}
		}
		*/
		
		goal_obj.save();
		
		
		
		/*
		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Maco Goal Saved\"}]}");
		return b.toString();
		*/
		
		return this.toJSON(MacroGoalDaily.getCurrentMacroGoalDaily(_logged_in_person));
		
		
	}
	
	private String
	setWeightGoal(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		// [{"name":"nameInput","value":"some new goal"},{"name":"inputWeightGoal","value":"225"},{"name":"goalId","value":"0"}]
		
		String last_value = "";
		
		String nameInput = null;
		String inputWeightGoal = null;
		WeightGoal goal_obj = null;
		boolean isCurrent = false;
		//String weightUnit = null;
		//String goalType = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputWeightGoal")) {
								inputWeightGoal = value_strX;
							} else if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("goalId")) {
								int goal_id = Integer.parseInt(value_strX);
								if (goal_id > 0) {
									goal_obj = WeightGoal.getWeightGoal(goal_id);
								}
							} else if (last_value.equals("isCurrent")) {
								isCurrent = value_strX.equals("on");
							}
						}
						
					}
				}
			}
		}
		
		if (inputWeightGoal == null) {
			throw new IllegalValueException("Please specify a weight goal.");
		}
		
		if (isCurrent) {
			if (WeightGoal.hasCurrentWeightGoal(_logged_in_person)) {
				WeightGoal current_goal = WeightGoal.getCurrentWeightGoal(_logged_in_person);
				current_goal.inActivate();
				current_goal.setCreateOrModifyPerson(_logged_in_person);
				current_goal.save();
			}
		}
		
		if (goal_obj == null) {
			goal_obj = new WeightGoal();
		}
		
		goal_obj.setPerson(_logged_in_person);
		goal_obj.setName(nameInput);
		goal_obj.setGoal(inputWeightGoal);
		goal_obj.setActive(isCurrent);
		//goal_obj.setType(goalType.equals("cut") ? WeightGoalType.getWeightGoalForType(WeightGoalType.CUT_TYPE) : goalType.equals("bulk") ? WeightGoalType.getWeightGoalForType(WeightGoalType.BULK_TYPE) : WeightGoalType.getWeightGoalForType(WeightGoalType.MAINTENANCE_TYPE) );
		goal_obj.setCreateOrModifyPerson(_logged_in_person);
		goal_obj.save();
		
		
		
		/*
		boolean current_goal_already_updated = false;
		boolean has_current_weight_goal = WeightGoal.hasCurrentWeightGoal(_logged_in_person);
		WeightGoal current_goal = null;
		if (has_current_weight_goal) {
			current_goal = WeightGoal.getCurrentWeightGoal(_logged_in_person);
			if (current_goal.getGoal().compareTo(BigDecimal.ZERO) == 0) { // the current goal is but an empty shell
				current_goal.setGoal(inputWeightGoal);
				current_goal.setType(goalType.equals("cut") ? WeightGoalType.getWeightGoalForType(WeightGoalType.CUT_TYPE) : goalType.equals("bulk") ? WeightGoalType.getWeightGoalForType(WeightGoalType.BULK_TYPE) : WeightGoalType.getWeightGoalForType(WeightGoalType.MAINTENANCE_TYPE) );
				current_goal.setCreateOrModifyPerson(_logged_in_person);
				current_goal.save();
				current_goal_already_updated = true;
			}
		}
		
		
		if (!current_goal_already_updated) {
			
			goal_obj.setPerson(_logged_in_person);
			goal_obj.setGoal(inputWeightGoal);
			goal_obj.setType(goalType.equals("cut") ? WeightGoalType.getWeightGoalForType(WeightGoalType.CUT_TYPE) : goalType.equals("bulk") ? WeightGoalType.getWeightGoalForType(WeightGoalType.BULK_TYPE) : WeightGoalType.getWeightGoalForType(WeightGoalType.MAINTENANCE_TYPE) );
			goal_obj.setCreateOrModifyPerson(_logged_in_person);

			if (has_current_weight_goal) {
				if (!current_goal.equals(goal_obj)) { // there is a current goal, and the passed goal has different values - a new current goal is taking its place
					current_goal.inActivate();
					current_goal.setCreateOrModifyPerson(_logged_in_person);
					current_goal.save();
					goal_obj.save();
				}
			} else {
				goal_obj.save();
			}
		}
		*/
		
		return this.toJSON(WeightGoal.getCurrentWeightGoal(_logged_in_person));
		
	}
	
	private String
	auth(String _jsonBlob, HttpSession _session) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String inputEmail = null;
		String inputPassword = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputEmail")) {
								inputEmail = value_strX;
							} else if (last_value.equals("inputPassword")) {
								inputPassword = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		return this.auth(inputEmail, inputPassword, _session);
		
	}
	
	private String
	auth(String _inputEmail, String _inputPassword, HttpSession _session) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		
		if (_inputPassword == null || _inputEmail == null) {
			throw new IllegalValueException("Please provide an email address and password.");
		}
		
		UKOnlinePersonBean existing_person = null;
		//if ( (_logged_in_person == null) || _logged_in_person.getEmail1String().isEmpty() ) {
			
			// logged in person has no email address - look to see if this person already exists
		
			try {
				existing_person = UKOnlinePersonBean.getPersonByEmailPassword(_inputEmail, _inputPassword);
				
				// person already exists - I could copy over any goals and stuff to this new user if I wanted to...
				
			} catch (Exception x) {
				x.printStackTrace();
			}
		//}
		
		if (existing_person == null) {
			throw new IllegalValueException("Unable to authenticate.");
		} else {
			
			if (existing_person.getUsernameString().equals(existing_person.getEmail1String())) {
				// I need to update the username with the session id
				String sessionId = _session.getId();
				// ensure that it's not somehow already in use
				if (UKOnlinePersonBean.usernameExists(sessionId)) {
					throw new IllegalValueException("Unable to authenticate.  Session already in use.");
				}
				existing_person.setUsername(sessionId);
				existing_person.save();
			}
			
			return this.toJSON(existing_person, existing_person.getUsernameString());
		}
		
	}
	
	private String
	verifyEmail(String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		// [{"name":"inputEmail","value":""},{"name":"inputPassword","value":""},{"name":"inputConfirmPassword","value":""},{"name":"defaultUnit","value":"lbs"},{"name":"gender","value":"male"},{"name":"inputWeight","value":"197"},{"name":"inputAge","value":"47"},{"name":"inputHeightFeet","value":"6"},{"name":"inputHeightInch","value":"0"},{"name":"activityLevelSelect","value":"1.375"}]
		
		String last_value = "";
		
		String inputEmail = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputEmail")) {
								inputEmail = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		if ( (inputEmail == null) || inputEmail.isEmpty() || inputEmail.equals("text")) {
			throw new IllegalValueException("Please provide an email address to verify.");
		}
		
		// is this email address already in use??
		
		try {
			UKOnlinePersonBean.getPersonByEmail(inputEmail);
			throw new IllegalValueException("Email address already in use.");
		} catch (ObjectNotFoundException x) { }

		// assuming that this is a new registration

		// send an email with an encrypted string that verifies the email address

		String encryptedStr = EncryptionUtils.encryptString(inputEmail);

		// send then email
		StringBuilder b = new StringBuilder();
		b.append("<p>Welcome to Fitness Tracker!</p>");
		b.append("<p>Click this link:</p>");
		b.append("<a href=\"https://www.valonyx.com/FitnessServlet?command=verifyEmailResponse&arg1=" + URLEncoder.encode(encryptedStr, "UTF-8") + "\" target=\"_blank\">Fitness Tracker</a>");
		b.append("<p>to verify your email address and complete your registration.</p>");
		
		System.out.println("confirm link local >http://localhost:8080/valeo/FitnessServlet?command=verifyEmailResponse&arg1=" + URLEncoder.encode(encryptedStr, "UTF-8"));
		System.out.println("confirm link server >https://www.valonyx.com/FitnessServlet?command=verifyEmailResponse&arg1=" + URLEncoder.encode(encryptedStr, "UTF-8"));

		String html_email_str = this.getNewClientHTMLEmailString(null, "Welcome to Fitness Tracker", b.toString());
		CUBean.sendEmailWithMeeting(inputEmail, "marlo@valonyx.com", "Welcome to Fitness Tracker", null, html_email_str);
		CUBean.sendEmailWithMeeting("marlo@badiyan.com", "marlo@valonyx.com", "Welcome to Fitness Tracker", null, html_email_str);

		return "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Check your email to confirm your address.\"}]}";
		
	}
	
	private String
	procWizardStep1(String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String inputEmail = null;
		String inputPassword = null;
		String inputConfirmPassword = null;
		String defaultUnit = "lbs";
		
		String gender = "Male";
		String inputWeight = null;
		String inputAge = null;
		String inputHeightFeet = null;
		String inputHeightInch = null;
		String activityLevelSelect = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputEmail")) {
								inputEmail = value_strX;
							} else if (last_value.equals("inputPassword")) {
								inputPassword = value_strX;
							} else if (last_value.equals("inputConfirmPassword")) {
								inputConfirmPassword = value_strX;
							} else if (last_value.equals("defaultUnit")) {
								defaultUnit = value_strX;
							} else if (last_value.equals("gender")) {
								gender = value_strX;
							} else if (last_value.equals("inputWeight")) {
								inputWeight = value_strX;
							} else if (last_value.equals("inputAge")) {
								inputAge = value_strX;
							} else if (last_value.equals("inputHeightFeet")) {
								inputHeightFeet = value_strX;
							} else if (last_value.equals("inputHeightInch")) {
								inputHeightInch = value_strX;
							} else if (last_value.equals("activityLevelSelect")) {
								activityLevelSelect = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		if (inputPassword == null || inputConfirmPassword == null || inputEmail == null) {
			throw new IllegalValueException("Please provide an email address and password.");
		}
		if (!inputPassword.equals(inputConfirmPassword)) {
			throw new IllegalValueException("Passwords don't match.");
		}
		
		UKOnlinePersonBean confirmed_person = null;
		try {
			//confirmed_person = UKOnlinePersonBean.getPersonByEmail(inputEmail);
			confirmed_person = UKOnlinePersonBean.getPersonByEmailPassword(inputEmail, inputPassword);
		} catch (ObjectNotFoundException x) {
			if (!FitnessServlet.confirmed_emails.contains(inputEmail)) {
				throw new IllegalValueException("Please verify your email address.");
			}
			confirmed_person = new UKOnlinePersonBean();
			confirmed_person.setUsername(inputEmail);
			confirmed_person.setEmail1(inputEmail);
			confirmed_person.setPassword(inputPassword);
			confirmed_person.setConfirmPassword(inputConfirmPassword);
			confirmed_person.save();
		}
		
		
		
		if ( (inputHeightFeet != null) && (inputHeightFeet.length() > 1) ) {
			throw new IllegalValueException("Invalid height.");
		}
		
		WeightPreferences preferences = WeightPreferences.getWeightPreference(confirmed_person);
		preferences.setUnitType( defaultUnit.equals("lbs") ? WeightPreferences.LBS_TYPE : WeightPreferences.KG_TYPE );

		if (gender != null) {
			preferences.setGender(gender.equals("Male") ? WeightPreferences.MALE : WeightPreferences.FEMALE);
		}
		if (inputWeight != null) {
			preferences.setWeight(new BigDecimal(inputWeight));
		}
		if (inputAge != null) {
			preferences.setAge(Short.parseShort(inputAge));
		}
		if (inputHeightFeet != null) {
			preferences.setHeightPrimaryUnit(Short.parseShort(inputHeightFeet));
		}
		if (inputHeightInch != null) {
			preferences.setHeightSecondaryUnit(Short.parseShort(inputHeightInch));
		}
		if (activityLevelSelect != null) {
			preferences.setActivityLevel(new BigDecimal(activityLevelSelect));
		}

		preferences.save();

		System.out.println("BMR >" + preferences.getBMRString());
		System.out.println("TEE >" + preferences.getTotalEnergyExpenditureString());

		StringBuilder b = new StringBuilder();
		b.append("{\"bmr\":\"" + preferences.getBMRString() + "\",");
		b.append("\"tee\":\"" + preferences.getTotalEnergyExpenditureString() + "\",");
		b.append("\"id\":" + confirmed_person.getId() + "}");
		return b.toString();
		
	}
	
	private String
	procWizardStep2(String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String inputFat = null;
		String inputCarbs = null;
		String inputProtein = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputFat")) {
								inputFat = value_strX;
							} else if (last_value.equals("inputCarbs")) {
								inputCarbs = value_strX;
							} else if (last_value.equals("inputProtein")) {
								inputProtein = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		if (inputFat == null || inputCarbs == null || inputProtein == null) {
			throw new IllegalValueException("Please specify daily fat, carbs, and protein goals.");
		}
		
		return "{\"message\":[{\"type\":\"SUCCESS\"}]}";
		
	}
	
	private String
	procWizardStep3(String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String inputWeightGoal = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputWeightGoal")) {
								inputWeightGoal = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		if (inputWeightGoal == null) {
			throw new IllegalValueException("Please specify your weight goal.");
		}
		
		return "{\"message\":[{\"type\":\"SUCCESS\"}]}";
		
	}
	
	private String
	procWizardFinish(String _jsonBlob, HttpSession _session) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String inputEmail = null;
		String inputPassword = null;
		String inputConfirmPassword = null;
		String defaultUnit = "lbs";
		
		String gender = "Male";
		String inputWeight = null;
		String inputAge = null;
		String inputHeightFeet = null;
		String inputHeightInch = null;
		String activityLevelSelect = null;
		
		String goalSelect = null;
		String inputCalories = "0";
		String fatSelect = null;
		String proteinSelect = null;
		String inputFat = "0";
		String inputCarbs = "0";
		String inputProtein = "0";
		String inputWeightGoal = "0";
		boolean isAuto = false;
		String plan = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputEmail")) {
								inputEmail = value_strX;
							} else if (last_value.equals("inputPassword")) {
								inputPassword = value_strX;
							} else if (last_value.equals("inputConfirmPassword")) {
								inputConfirmPassword = value_strX;
							} else if (last_value.equals("defaultUnit")) {
								defaultUnit = value_strX;
							} else if (last_value.equals("gender")) {
								gender = value_strX;
							} else if (last_value.equals("inputWeight")) {
								inputWeight = value_strX;
							} else if (last_value.equals("inputAge")) {
								inputAge = value_strX;
							} else if (last_value.equals("inputHeightFeet")) {
								inputHeightFeet = value_strX;
							} else if (last_value.equals("inputHeightInch")) {
								inputHeightInch = value_strX;
							} else if (last_value.equals("activityLevelSelect")) {
								activityLevelSelect = value_strX;
							} else if (last_value.equals("goalSelect")) {
								goalSelect = value_strX;
							} else if (last_value.equals("inputCalories")) {
								inputCalories = value_strX;
							} else if (last_value.equals("fatSelect")) {
								fatSelect = value_strX;
							} else if (last_value.equals("proteinSelect")) {
								proteinSelect = value_strX;
							} else if (last_value.equals("inputFat")) {
								inputFat = value_strX;
							} else if (last_value.equals("inputCarbs")) {
								inputCarbs = value_strX;
							} else if (last_value.equals("inputProtein")) {
								inputProtein = value_strX;
							} else if (last_value.equals("inputWeightGoal")) {
								inputWeightGoal = value_strX;
							} else if (last_value.equals("plan")) {
								plan = value_strX;
							} else if (last_value.equals("isAuto")) {
								isAuto = value_strX.equals("on");
							}
						}
						
					}
				}
			}
		}
		
		if (inputPassword == null || inputConfirmPassword == null || inputEmail == null) {
			throw new IllegalValueException("Please provide an email address and password.");
		}
		if (!inputPassword.equals(inputConfirmPassword)) {
			throw new IllegalValueException("Passwords don't match.");
		}
		
		UKOnlinePersonBean confirmed_person = null;
		try {
			//confirmed_person = UKOnlinePersonBean.getPersonByEmail(inputEmail);
			confirmed_person = UKOnlinePersonBean.getPersonByEmailPassword(inputEmail, inputPassword);
		} catch (ObjectNotFoundException x) {
			if (!FitnessServlet.confirmed_emails.contains(inputEmail)) {
				throw new IllegalValueException("Please verify your email address.");
			}
			confirmed_person = new UKOnlinePersonBean();
			confirmed_person.setUsername(inputEmail);
			confirmed_person.setEmail1(inputEmail);
			confirmed_person.setPassword(inputPassword);
			confirmed_person.setConfirmPassword(inputConfirmPassword);
			confirmed_person.save();
		}
		
		if ( (inputHeightFeet != null) && (inputHeightFeet.length() > 1) ) {
			throw new IllegalValueException("Invalid height.");
		}
		
		WeightPreferences preferences = WeightPreferences.getWeightPreference(confirmed_person);
		preferences.setUnitType( defaultUnit.equals("lbs") ? WeightPreferences.LBS_TYPE : WeightPreferences.KG_TYPE );

		if (gender != null) {
			preferences.setGender(gender.equals("Male") ? WeightPreferences.MALE : WeightPreferences.FEMALE);
		}
		if (inputWeight != null) {
			preferences.setWeight(new BigDecimal(inputWeight));
		}
		if (inputAge != null) {
			preferences.setAge(Short.parseShort(inputAge));
		}
		if (inputHeightFeet != null) {
			preferences.setHeightPrimaryUnit(Short.parseShort(inputHeightFeet));
		}
		if (inputHeightInch != null) {
			preferences.setHeightSecondaryUnit(Short.parseShort(inputHeightInch));
		}
		if (activityLevelSelect != null) {
			preferences.setActivityLevel(new BigDecimal(activityLevelSelect));
		}

		preferences.save();

		
		
		MacroGoal goal_obj = null;
		if ( MacroGoal.hasCurrentMacroGoal(confirmed_person) ) {
			//goal_obj = MacroGoal.getMacroGoal(Integer.parseInt(goalId));
			goal_obj = MacroGoal.getCurrentMacroGoal(confirmed_person);
		} else {
			goal_obj = new MacroGoal();
			goal_obj.setPerson(confirmed_person);
			//goal_obj.setName(nameInput);
			
			if (goalSelect != null) {
				if (goalSelect.equals("0")) {
					goal_obj.setName("Maintenance Goal");
				} else if (goalSelect.equals("250")) {
					goal_obj.setName("Lean Bulk Goal");
				} else if (goalSelect.equals("500")) {
					goal_obj.setName("Rapid Bulk Goal");
				} else if (goalSelect.equals("-250")) {
					goal_obj.setName("Weight Loss Goal");
				} else if (goalSelect.equals("-500")) {
					goal_obj.setName("Rapid Weight Loss Goal");
				}
			}
		}
		
		goal_obj.setFat(inputFat);
		goal_obj.setCarbs(inputCarbs);
		goal_obj.setProtein(inputProtein);
		goal_obj.setCalories(inputCalories);
		
		goal_obj.setActive(true);
		goal_obj.setAuto(isAuto);
		
		String goalType = "cut";
		if (goalSelect == null) {
			//throw new IllegalValueException("Please select a calorie goal");
		} else {
		
			if (goalSelect.equals("0")) {
				goal_obj.setGoalType(MacroGoal.MAINTAIN_TYPE);
				goalType = "maintain";
			} else if (goalSelect.equals("250")) {
				goal_obj.setGoalType(MacroGoal.LEAN_BULK_TYPE);
				goalType = "bulk";
			} else if (goalSelect.equals("500")) {
				goal_obj.setGoalType(MacroGoal.RAPID_BULK_TYPE);
				goalType = "bulk";
			} else if (goalSelect.equals("-250")) {
				goal_obj.setGoalType(MacroGoal.FAT_LOSS_TYPE);
				goalType = "cut";
			} else if (goalSelect.equals("-500")) {
				goal_obj.setGoalType(MacroGoal.RAPID_FAT_LOSS_TYPE);
				goalType = "cut";
			}
		}
		
		if (fatSelect != null) {
			if (fatSelect.equals(".3")) {
				goal_obj.setFatGoal(MacroGoal.LOW_FAT_GOAL);
			} else if (fatSelect.equals(".4")) {
				goal_obj.setFatGoal(MacroGoal.RECOMMENDED_FAT_GOAL);
			} else if (fatSelect.equals(".5")) {
				goal_obj.setFatGoal(MacroGoal.HIGH_FAT_GOAL);
			} else if (fatSelect.equals("1.3")) {
				goal_obj.setFatGoal(MacroGoal.KETO_1_GOAL);
			} else if (fatSelect.equals("1.5")) {
				goal_obj.setFatGoal(MacroGoal.KETO_2_GOAL);
			}
		}
		
		if (proteinSelect != null) {
			if (proteinSelect.equals(".8")) {
				goal_obj.setProteinGoal(MacroGoal.LOW_PROTEIN_GOAL);
			} else if (proteinSelect.equals(".95")) {
				goal_obj.setProteinGoal(MacroGoal.RECOMMENDED_PROTEIN_GOAL);
			} else if (proteinSelect.equals("1.1")) {
				goal_obj.setProteinGoal(MacroGoal.HIGH_PROTEIN_GOAL);
			} else if (proteinSelect.equals("1.3")) {
				goal_obj.setProteinGoal(MacroGoal.HIGHER_PROTEIN_GOAL);
			}
		}
		
		goal_obj.setCreateOrModifyPerson(confirmed_person);
		goal_obj.save();
		
		
		
		boolean current_goal_already_updated = false;
		boolean has_current_weight_goal = WeightGoal.hasCurrentWeightGoal(confirmed_person);
		WeightGoal current_goal = null;
		WeightGoal weight_goal_obj = new WeightGoal();
		if (has_current_weight_goal) {
			current_goal = WeightGoal.getCurrentWeightGoal(confirmed_person);
			if (current_goal.getGoal().compareTo(BigDecimal.ZERO) == 0) { // the current goal is but an empty shell
				current_goal.setGoal(inputWeightGoal);
				current_goal.setType(goalType.equals("cut") ? WeightGoalType.getWeightGoalForType(WeightGoalType.CUT_TYPE) : goalType.equals("bulk") ? WeightGoalType.getWeightGoalForType(WeightGoalType.BULK_TYPE) : WeightGoalType.getWeightGoalForType(WeightGoalType.MAINTENANCE_TYPE) );
				current_goal.setCreateOrModifyPerson(confirmed_person);
				current_goal.save();
				current_goal_already_updated = true;
			}
		}
		
		if (!current_goal_already_updated) {
			
			weight_goal_obj.setPerson(confirmed_person);
			weight_goal_obj.setGoal(inputWeightGoal);
			weight_goal_obj.setType(goalType.equals("cut") ? WeightGoalType.getWeightGoalForType(WeightGoalType.CUT_TYPE) : goalType.equals("bulk") ? WeightGoalType.getWeightGoalForType(WeightGoalType.BULK_TYPE) : WeightGoalType.getWeightGoalForType(WeightGoalType.MAINTENANCE_TYPE) );
			weight_goal_obj.setCreateOrModifyPerson(confirmed_person);

			if (has_current_weight_goal) {
				if (!current_goal.equals(weight_goal_obj)) { // there is a current goal, and the passed goal has different values - a new current goal is taking its place
					current_goal.inActivate();
					current_goal.setCreateOrModifyPerson(confirmed_person);
					current_goal.save();
					weight_goal_obj.save();
				}
			} else {
				weight_goal_obj.save();
			}
		}
		
		
		// 55 - Starting Strength
		// 56 - Starting Size
		
		if (plan != null) {
			if (plan.equals("Size")) {
				TrainingPlan.getTrainingPlan(56).setActive(confirmed_person, true);
			} else if (plan.equals("Strength")) {
				TrainingPlan.getTrainingPlan(55).setActive(confirmed_person, true);
			} else if (plan.equals("Novice")) {
				//TrainingPlan.getTrainingPlan(58).setActive(confirmed_person, true);
				TrainingPlan.getTrainingPlan(180).setActive(confirmed_person, true);
			} else if (plan.equals("Custom")) {
				
				UKOnlinePersonBean will = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4434);
				confirmed_person.setSupervisor(will, "Trainer");

				StringBuilder b = new StringBuilder();
				b.append("<p>" + confirmed_person.getLabel() + " has requested training.</p>");
				//b.append("<p>Training goals:</p>");
				//b.append("<p>" + inputTrainingGoals + "</p>");

				String html_email_str = this.getNewClientHTMLEmailString(will, confirmed_person.getLabel() + " has requested training.", b.toString());
				CUBean.sendEmailWithMeeting(will.getEmail1String(), "marlo@valonyx.com", confirmed_person.getLabel() + " has requested training.", null, html_email_str);
				CUBean.sendEmailWithMeeting("marlo@valonyx.com", "marlo@valonyx.com", confirmed_person.getLabel() + " has requested training.", null, html_email_str);

			} else if (plan.equals("Skip")) {
				// create an empty training plan
			}
		}
		
		return this.auth(inputEmail, inputPassword, _session);
		
	}
	
	private String
	saveProfile(UKOnlinePersonBean _logged_in_person, String _jsonBlob, String _key) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		// [{"name":"inputEmail","value":""},{"name":"inputPassword","value":""},{"name":"inputConfirmPassword","value":""},{"name":"defaultUnit","value":"lbs"},{"name":"gender","value":"male"},{"name":"inputWeight","value":"197"},{"name":"inputAge","value":"47"},{"name":"inputHeightFeet","value":"6"},{"name":"inputHeightInch","value":"0"},{"name":"activityLevelSelect","value":"1.375"}]
		
		String last_value = "";
		
		String inputEmail = null;
		String inputPassword = null;
		String inputConfirmPassword = null;
		String defaultUnit = null;
		
		String gender = null;
		String inputWeight = null;
		String inputAge = null;
		String inputHeightFeet = null;
		String inputHeightInch = null;
		String activityLevelSelect = null;
		
		String first = null;
		String last = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputEmail")) {
								inputEmail = value_strX;
							} else if (last_value.equals("inputPassword")) {
								inputPassword = value_strX;
							} else if (last_value.equals("inputConfirmPassword")) {
								inputConfirmPassword = value_strX;
							} else if (last_value.equals("defaultUnit")) {
								defaultUnit = value_strX;
							} else if (last_value.equals("gender")) {
								gender = value_strX;
							} else if (last_value.equals("inputWeight")) {
								inputWeight = value_strX;
							} else if (last_value.equals("inputAge")) {
								inputAge = value_strX;
							} else if (last_value.equals("inputHeightFeet")) {
								inputHeightFeet = value_strX;
							} else if (last_value.equals("inputHeightInch")) {
								inputHeightInch = value_strX;
							} else if (last_value.equals("activityLevelSelect")) {
								activityLevelSelect = value_strX;
							} else if (last_value.equals("inputFirst")) {
								first = value_strX;
							} else if (last_value.equals("inputLast")) {
								last = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		if (inputPassword == null || inputConfirmPassword == null || inputEmail == null) {
			throw new IllegalValueException("Please provide an email address and password.");
		}
		if (!inputPassword.equals(inputConfirmPassword)) {
			throw new IllegalValueException("Passwords don't match.");
		}
		
		if ( (inputHeightFeet != null) && (inputHeightFeet.length() > 1) ) {
			throw new IllegalValueException("Invalid height.");
		}
		
		UKOnlinePersonBean existing_person = null;
		if ( (_logged_in_person == null) || _logged_in_person.getEmail1String().isEmpty() ) {
			
			// logged in person has no email address - look to see if this person already exists
		
			try {
				existing_person = UKOnlinePersonBean.getPersonByEmailPassword(inputEmail, inputPassword);
				
				// person already exists - I could copy over any goals and stuff to this new user if I wanted to...
				
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		
		// what if logged_in_person == null AND existing person is null???
		
		System.out.println("_logged_in_person >" + _logged_in_person);
		System.out.println("existing_person >" + existing_person);
		
		if ( (_logged_in_person == null) && (existing_person == null) ) {
			
			// assuming that this is a new registration
			
			// send an email with an encrypted string that verifies the email address
			
			String encryptedStr = EncryptionUtils.encryptString(inputEmail + "~~|~~" + inputPassword + "~~|~~" + defaultUnit + "~~|~~" + gender + "~~|~~" + inputWeight + "~~|~~" + inputAge + "~~|~~" + inputHeightFeet + "~~|~~" + inputHeightInch + "~~|~~" + activityLevelSelect);
			
			// send then email
			StringBuilder b = new StringBuilder();
			b.append("<p>Welcome to the Fitness Tracker!</p>");
			b.append("<p>Click this link:</p>");
			b.append("<a href=\"https://www.valonyx.com/fit/#index/" + URLEncoder.encode(encryptedStr, "UTF-8") + "\">Fitness Tracker</a>");
			b.append("<p>to verify your email address and complete your registration.</p>");
			b.append("<p>Your email address and password:</p>");
			b.append("<p>" + inputEmail + "<br />" + inputPassword + "</p>");
		
			String html_email_str = this.getNewClientHTMLEmailString(null, "Weclome to Fitness Tracker", b.toString());
			CUBean.sendEmailWithMeeting(inputEmail, "marlo@valonyx.com", "Weclome to Fitness Tracker", null, html_email_str);
			CUBean.sendEmailWithMeeting("marlo@valonyx.com", "marlo@valonyx.com", "Weclome to Fitness Tracker", null, html_email_str);

			return "{\"message\":[{\"type\":\"SUCCESS\",\"text\":\"Check your email to confirm your registration.\"}]}";
			
		} else {
		
			WeightPreferences preferences = WeightPreferences.getWeightPreference( (existing_person == null) ? _logged_in_person : existing_person);
			preferences.setUnitType( defaultUnit.equals("lbs") ? WeightPreferences.LBS_TYPE : WeightPreferences.KG_TYPE );
			
			/*
			
		String gender = null;
		String weight = null;
		String inputAge = null;
		String inputHeightFeet = null;
		String inputHeightInch = null;
		String activityLevelSelect = null;
			*/
			
			if (gender != null) {
				preferences.setGender(gender.equals("male") ? WeightPreferences.MALE : WeightPreferences.FEMALE);
			}
			if (inputWeight != null) {
				preferences.setWeight(new BigDecimal(inputWeight));
			}
			if (inputAge != null) {
				preferences.setAge(Short.parseShort(inputAge));
			}
			if (inputHeightFeet != null) {
				preferences.setHeightPrimaryUnit(Short.parseShort(inputHeightFeet));
			}
			if (inputHeightInch != null) {
				preferences.setHeightSecondaryUnit(Short.parseShort(inputHeightInch));
			}
			if (activityLevelSelect != null) {
				preferences.setActivityLevel(new BigDecimal(activityLevelSelect));
			}
			
			preferences.save();
			
			System.out.println("BMR >" + preferences.getBMRString());
			System.out.println("TEE >" + preferences.getTotalEnergyExpenditureString());
			
			if (last != null && !last.isEmpty()) {
				_logged_in_person.setLastName(last);
			}
			if (first != null && !first.isEmpty()) {
				_logged_in_person.setFirstName(first);
			}

			if (existing_person == null) {
				_logged_in_person.setEmail1(inputEmail);
				_logged_in_person.setPassword(inputPassword);
				_logged_in_person.setConfirmPassword(inputConfirmPassword);
				_logged_in_person.save();
				return this.toJSON(_logged_in_person, _key);
			} else {
				return this.toJSON(existing_person, existing_person.getUsernameString());
			}
		
		}
		
	}
	
	private String
	saveTrainingPlan(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		boolean setCurrent = false;
		
		String nameInput = null;
		String descInput = null;
		boolean isCurrent = false;
		boolean isRepeating = false;
		boolean isPublic = false;
		boolean isCopy = false;
		
		TrainingPlan plan = null;
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("nameInput")) {
								nameInput = value_strX;
							} else if (last_value.equals("descInput")) {
								descInput = value_strX;
							} else if (last_value.equals("isCurrent")) {
								isCurrent = true;
								setCurrent = true;
							} else if (last_value.equals("isPublic")) {
								isPublic = true;
							} else if (last_value.equals("isCopy")) {
								isCopy = true;
							} else if (last_value.equals("isRepeating")) {
								isRepeating = true;
							} else if (last_value.equals("planId")) {
								int plan_id = Integer.parseInt(value_strX);
								if (plan_id > 0) {
									plan = TrainingPlan.getTrainingPlan(plan_id);
								}
							}
						}
					}
				}
			}
		}
		
		
		if (isCopy) {
			
			if (plan == null) {
				throw new IllegalValueException("Please specify a plan to copy.");
			}
			
			if (nameInput == null || nameInput.isEmpty()) {
				nameInput = plan.getNameString();
			}
			if (descInput == null || descInput.isEmpty()) {
				descInput = plan.getDescription();
			}
			
			TrainingPlan plan_copy = new TrainingPlan();
			plan_copy.setName(nameInput);
			plan_copy.setDescription(descInput);
			plan_copy.setCreateOrModifyPerson(_logged_in_person);
			plan_copy.setPerson(_logged_in_person);
			plan_copy.setActive(true); // so that it shows up at all
			plan_copy.setPublic(false);
			plan_copy.save();
			
			plan_copy.setActive(_logged_in_person, false); // inactive initially for this person
			
			Iterator workouts_to_copy = plan.getWorkouts().iterator();
			while (workouts_to_copy.hasNext()) {
				TrainingPlanWorkout workout_to_copy = (TrainingPlanWorkout)workouts_to_copy.next();

				System.out.println("workout_to_copy >" + workout_to_copy.getLabel());

				TrainingPlanWorkout copy = new TrainingPlanWorkout();

				copy.setParent(plan_copy);
				plan_copy.invalidate();
				copy.setName(workout_to_copy.getName());
				copy.setDescription(workout_to_copy.getDescription());
				copy.setBlockNumber(workout_to_copy.getBlockNumber());
				copy.setWeekNumber(workout_to_copy.getWeekNumber());
				copy.setWorkoutNumber(workout_to_copy.getWorkoutNumber());
				copy.save();

				Vector moves = new Vector();
				Iterator itr = workout_to_copy.getMoves().iterator();
				while (itr.hasNext()) {
					TrainingPlanMove move_to_copy = (TrainingPlanMove)itr.next();
					System.out.println("TrainingPlanMove TO COPY >" + move_to_copy.getLabel());
					moves.addElement(move_to_copy.copy(_logged_in_person, copy));
				}
				copy.setMoves(moves);
				copy.save();
			}
			
			return this.toJSON(_logged_in_person, plan_copy, false, false);
			
		} else {

			// inactivate other

			boolean found_active_plan = false;

			Iterator itr = TrainingPlan.getTrainingPlans(_logged_in_person).iterator();
			while (itr.hasNext()) {
				TrainingPlan obj = (TrainingPlan)itr.next();
				if ( obj.isActive(_logged_in_person) && !obj.equals(plan) ) {
					
					
					if (isCurrent && setCurrent) {
						obj.setActive(_logged_in_person, false);
						//obj.save(); 8/11/18 - saved in mapping
					}
					
					
					found_active_plan = true;
				}
			}
			
			
			if (!found_active_plan && !isCurrent && setCurrent) {
				throw new IllegalValueException("You should specify a current plan");
			}
			
		
			if (plan == null) {
				plan = new TrainingPlan();
				plan.setActive(true); // this doesn't mean it's active for the person...
			}

			if (plan.canEdit(_logged_in_person)) {
				
				if ( (nameInput != null) && !nameInput.isEmpty()) {
					plan.setName(nameInput);
				}
				if ( (descInput != null) && !descInput.isEmpty()) {
					plan.setDescription(descInput);
				}

				plan.setPublic(isPublic);

				plan.setIsRepeating(isRepeating);
				plan.setCreateOrModifyPerson(_logged_in_person);
				plan.setPerson(_logged_in_person); // 5/26/18 - this doesn't do anything anymore, I'm thinking.  use mapping instead so plans can be shared.
				// added back in 7/22/18 - it uses this person to setup the mapping, at least initially...
				plan.save();
			}

			if (setCurrent) {
				plan.setActive(_logged_in_person, isCurrent);
			}

			return this.toJSON(_logged_in_person, plan, false, false);
			
		}
		
		/*
		StringBuffer b = new StringBuffer();
		b.append("{\"message\":[{\"type\":\"SUCCESS\",\"heading\":\"Training Plan Created\",\"text\":\"" + nameInput + " created.\"}]}");
		return b.toString();
		*/
	}
	
	private void
	saveClient(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String inputClientFirst = null;
		String inputClientLast = null;
		String inputClientEmail = null;
		String systemPassword = null;
		String sendEmailCheck = null;
		String inputClientPassword = "";
		String inputClientPasswordConfirm = "";
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputClientFirst")) {
								inputClientFirst = value_strX;
							} else if (last_value.equals("inputClientLast")) {
								inputClientLast = value_strX;
							} else if (last_value.equals("inputClientEmail")) {
								inputClientEmail = value_strX;
							} else if (last_value.equals("systemPassword")) {
								systemPassword = value_strX;
							} else if (last_value.equals("sendEmailCheck")) {
								sendEmailCheck = value_strX;
							} else if (last_value.equals("inputClientPassword")) {
								inputClientPassword = value_strX;
							} else if (last_value.equals("inputClientPasswordConfirm")) {
								inputClientPasswordConfirm = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		// first see if a person with this email address already exists

		try {
			//new_client = UKOnlinePersonBean.getPersonByEmailPassword(inputEmail, inputPassword);
			UKOnlinePersonBean.getPersonByEmail(inputClientEmail);
			
			throw new IllegalValueException("A person with email address " + inputClientEmail + " already exists.");

			// person already exists - I could copy over any goals and stuff to this new user if I wanted to...

		} catch (ObjectNotFoundException x) {
			x.printStackTrace();
		}
		
		if ( (systemPassword != null) && systemPassword.equals("on") ) {
			// auto generate a password
			inputClientPassword = PasswordGenerator.getPassword(3) + "-" + PasswordGenerator.getPassword(3);
			inputClientPasswordConfirm = inputClientPassword;
			
		} else {
			if (inputClientPassword.isEmpty()) {
				throw new IllegalValueException("Please specify a password.");
			}
			if (!inputClientPassword.equals(inputClientPasswordConfirm)) {
				throw new IllegalValueException("Passwords don't match.");
			}
		}
		
		UKOnlinePersonBean new_client = new UKOnlinePersonBean();
		new_client.setEmail1(inputClientEmail);
		new_client.setFirstName(inputClientFirst);
		new_client.setLastName(inputClientLast);
		new_client.setPassword(inputClientPassword);
		new_client.setConfirmPassword(inputClientPassword);
		new_client.setUsername(inputClientEmail); // this might have to be changed to the session id or something the first time they log in...
		new_client.save();
		
		// create an association between this person and the logged in person (trainer)
		
		new_client.setSupervisor(_logged_in_person, "Trainer");
		
		
		WeightPreferences new_preferences = WeightPreferences.getWeightPreference(new_client);
		// get the default units from the logged in person
		new_preferences.setUnitType( WeightPreferences.getWeightPreference(_logged_in_person).getUnitType() );
		new_preferences.save();
		
		
		if ( (sendEmailCheck != null) && sendEmailCheck.equals("on") ) {
			// send then email
			StringBuilder b = new StringBuilder();
			b.append("<p>Welcome to the Fitness Tracker!</p>");
			b.append("<p>Click this link:</p>");
			b.append("<a href=\"https://www.valonyx.com/fit\">Fitness Tracker</a>");
			b.append("<p>and sign in using your email address and password:</p>");
			b.append("<p>" + inputClientEmail + "<br />" + inputClientPassword + "</p>");
		
			String html_email_str = this.getNewClientHTMLEmailString(new_client, _logged_in_person.getLabel() + " has invited you to get fit!", b.toString());
			CUBean.sendEmailWithMeeting(inputClientEmail, "marlo@valonyx.com", _logged_in_person.getLabel() + " has invited you to get fit!", null, html_email_str);
			CUBean.sendEmailWithMeeting("marlo@valonyx.com", "marlo@valonyx.com", _logged_in_person.getLabel() + " has invited you to get fit!", null, html_email_str);
		}
		
	}
	
	private void
	getTrained(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String defaultUnit = null;
		
		String gender = null;
		String inputWeight = null;
		String inputAge = null;
		String inputHeightFeet = null;
		String inputHeightInch = null;
		String activityLevelSelect = null;
		
		String first = null;
		String last = null;
		
		String trainingGoals = null;
		String exerciseHistory = "";
		String injuries = "";
		String addtlNotes = "";
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("defaultUnit")) {
								defaultUnit = value_strX;
							} else if (last_value.equals("gender")) {
								gender = value_strX;
							} else if (last_value.equals("inputWeight")) {
								inputWeight = value_strX;
							} else if (last_value.equals("inputAge")) {
								inputAge = value_strX;
							} else if (last_value.equals("inputHeightFeet")) {
								inputHeightFeet = value_strX;
							} else if (last_value.equals("inputHeightInch")) {
								inputHeightInch = value_strX;
							} else if (last_value.equals("activityLevelSelect")) {
								activityLevelSelect = value_strX;
							} else if (last_value.equals("inputFirst")) {
								first = value_strX;
							} else if (last_value.equals("inputLast")) {
								last = value_strX;
							} else if (last_value.equals("inputTrainingGoals")) {
								trainingGoals = value_strX;
							} else if (last_value.equals("inputTrainingHistory")) {
								exerciseHistory = value_strX;
							} else if (last_value.equals("inputInjury")) {
								injuries = value_strX;
							} else if (last_value.equals("inputAdditionalNotes")) {
								addtlNotes = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		if ( (first == null) || first.isEmpty()) {
			throw new IllegalValueException("Please provide your first and last name.");
		}
		if ( (last == null) || last.isEmpty()) {
			throw new IllegalValueException("Please provide your first and last name.");
		}
		if ( (trainingGoals == null) || trainingGoals.isEmpty()) {
			throw new IllegalValueException("Please describe your training goals.");
		}
		
		if ( (inputHeightFeet != null) && (inputHeightFeet.length() > 1) ) {
			throw new IllegalValueException("Invalid height.");
		}
		
		if ( (inputWeight == null) || inputWeight.isEmpty()) {
			throw new IllegalValueException("Please provide your weight.");
		}
		
		String activityLevelDesc = "[NOT ENTERED]";
		if (activityLevelSelect != null) {
			if (activityLevelSelect.equals("1.200")) {
				activityLevelDesc = "Sedentary (little or no exercise)";
			} else if (activityLevelSelect.equals("1.375")) {
				activityLevelDesc = "Lightly active (light exercise/sports 1-3 days/week)";
			} else if (activityLevelSelect.equals("1.550")) {
				activityLevelDesc = "Moderately active (moderate exercise/sports 3-5 days/week)";
			} else if (activityLevelSelect.equals("1.725")) {
				activityLevelDesc = "Very active (hard exercise/sports 6-7 days a week)";
			} else if (activityLevelSelect.equals("1.900")) {
				activityLevelDesc = "Extra active (very hard exercise/sports & physical job or 2x training)";
			}
		}
		
		WeightPreferences preferences = WeightPreferences.getWeightPreference(_logged_in_person);
		preferences.setUnitType( defaultUnit.equals("lbs") ? WeightPreferences.LBS_TYPE : WeightPreferences.KG_TYPE );

		if (gender != null) {
			preferences.setGender(gender.equals("male") ? WeightPreferences.MALE : WeightPreferences.FEMALE);
		}
		if (inputWeight != null) {
			preferences.setWeight(new BigDecimal(inputWeight));
		}
		if (inputAge != null) {
			preferences.setAge(Short.parseShort(inputAge));
		}
		if (inputHeightFeet != null) {
			preferences.setHeightPrimaryUnit(Short.parseShort(inputHeightFeet));
		}
		if (inputHeightInch != null) {
			preferences.setHeightSecondaryUnit(Short.parseShort(inputHeightInch));
		}
		if (activityLevelSelect != null) {
			preferences.setActivityLevel(new BigDecimal(activityLevelSelect));
		}

		preferences.save();

		System.out.println("BMRx >" + preferences.getBMRString());
		System.out.println("TEEx >" + preferences.getTotalEnergyExpenditureString());

		if (last != null && !last.isEmpty()) {
			_logged_in_person.setLastName(last);
		}
		if (first != null && !first.isEmpty()) {
			_logged_in_person.setFirstName(first);
		}

		_logged_in_person.save();
		
		UKOnlinePersonBean will = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4434);
		_logged_in_person.setSupervisor(will, "Trainer");

		StringBuilder b = new StringBuilder();
		b.append("<p>" + _logged_in_person.getLabel() + " has requested training.</p>");
		b.append("<p>Email:</p>");
		b.append("<p>" + _logged_in_person.getEmail1String() + "</p>");
		b.append("<p>Gender:</p>");
		b.append("<p>" + ( preferences.isMale() ? "Male" : "Female" ) + "</p>");
		b.append("<p>Age:</p>");
		b.append("<p>" + ( (inputAge == null) ? "" : inputAge) + "</p>");
		b.append("<p>Height:</p>");
		b.append("<p>" + preferences.getHeightPrimaryUnitString() + " - " + preferences.getHeightSecondaryUnitString() + "</p>");
		b.append("<p>Weight:</p>");
		b.append("<p>" + ( preferences.getWeightString() + " " + preferences.getUnitTypeString() ) + "</p>");
		b.append("<p>Activity level:</p>");
		b.append("<p>" + activityLevelDesc + "</p>");
		b.append("<p>Training goals:</p>");
		b.append("<p>" + trainingGoals + "</p>");
		b.append("<p>Exercise history:</p>");
		b.append("<p>" + exerciseHistory + "</p>");
		b.append("<p>Pre-existing injuries / conditions / pains:</p>");
		b.append("<p>" + injuries + "</p>");
		b.append("<p>Additional notes:</p>");
		b.append("<p>" + addtlNotes + "</p>");

		String html_email_str = this.getNewClientHTMLEmailString(will, _logged_in_person.getLabel() + " has requested training.", b.toString());
		CUBean.sendEmailWithMeeting(will.getEmail1String(), "marlo@valonyx.com", _logged_in_person.getLabel() + " has requested training.", null, html_email_str);
		CUBean.sendEmailWithMeeting("marlo@valonyx.com", "marlo@valonyx.com", _logged_in_person.getLabel() + " has requested training.", null, html_email_str);
		
	}
	
	private void
	getTrainedOld(UKOnlinePersonBean _logged_in_person, String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		String last_value = "";
		
		String inputTrainingGoals = "[NO GOALS SPECIFIED]";
		
		LinkedList list_obj = (LinkedList)parser.parse(_jsonBlob, containerFactory);
		Iterator list_itr = list_obj.iterator();
		while (list_itr.hasNext()) {
			LinkedHashMap linkedHashMap = (LinkedHashMap)list_itr.next();
			Iterator itr = linkedHashMap.keySet().iterator();
			while (itr.hasNext()) {
				String keyX = (String)itr.next();
				Object value_objX = linkedHashMap.get(keyX);
				if (value_objX instanceof String) {
					String value_strX = (String)value_objX;
					if (keyX.equals("name")) {
						last_value = value_strX;
					} else {
						if (!value_strX.isEmpty()) {
							if (last_value.equals("inputTrainingGoals")) {
								inputTrainingGoals = value_strX;
							}
						}
						
					}
				}
			}
		}
		
		UKOnlinePersonBean will = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4434);
		_logged_in_person.setSupervisor(will, "Trainer");

		StringBuilder b = new StringBuilder();
		b.append("<p>" + _logged_in_person.getLabel() + " has requested training.</p>");
		b.append("<p>Training goals:</p>");
		b.append("<p>" + inputTrainingGoals + "</p>");

		String html_email_str = this.getNewClientHTMLEmailString(will, _logged_in_person.getLabel() + " has requested training.", b.toString());
		CUBean.sendEmailWithMeeting(will.getEmail1String(), "marlo@valonyx.com", _logged_in_person.getLabel() + " has requested training.", null, html_email_str);
		CUBean.sendEmailWithMeeting("marlo@valonyx.com", "marlo@valonyx.com", _logged_in_person.getLabel() + " has requested training.", null, html_email_str);
		
	}
	
	public static boolean
	procUSDAList(String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		boolean food_found = false;
		/*
		_jsonBlob = "{\n" +
"    \"list\": {\n" +
"        \"lt\": \"f\",\n" +
"        \"start\": 5,\n" +
"        \"end\": 5,\n" +
"        \"total\": 5,\n" +
"        \"sr\": \"Legacy\",\n" +
"        \"sort\": \"n\",\n" +
"        \"item\": [\n" +
"            {\n" +
"                \"offset\": 5,\n" +
"                \"id\": \"45062764\",\n" +
"                \"name\": \" BERRY NUT BLEND BREAKFAST IN THE GO! , UPC: 010300064220\"\n" +
"            },\n" +
"            {\n" +
"                \"offset\": 6,\n" +
"                \"id\": \"45236813\",\n" +
"                \"name\": \" CARAMELIZED RED ONION CHUTNEY, UPC: 858723003033\"\n" +
"            },\n" +
"            {\n" +
"                \"offset\": 7,\n" +
"                \"id\": \"45051425\",\n" +
"                \"name\": \" CARROT CAKE & FROSTING MIX, UPC: 072036708830\"\n" +
"            },\n" +
"            {\n" +
"                \"offset\": 8,\n" +
"                \"id\": \"45364040\",\n" +
"                \"name\": \" CLASSIC CHICKEN NOODLE WITH WHITE MEAT CHICKEN SOUP, UPC: 051000247186\"\n" +
"            },\n" +
"            {\n" +
"                \"offset\": 9,\n" +
"                \"id\": \"45238262\",\n" +
"                \"name\": \" COCONUT WATER BARS, UPC: 853883003374\"\n" +
"            }\n" +
"        ]\n" +
"    }\n" +
"}";
		*/
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		// [{"name":"inputEmail","value":""},{"name":"inputPassword","value":""},{"name":"inputConfirmPassword","value":""},{"name":"defaultUnit","value":"lbs"},{"name":"gender","value":"male"},{"name":"inputWeight","value":"197"},{"name":"inputAge","value":"47"},{"name":"inputHeightFeet","value":"6"},{"name":"inputHeightInch","value":"0"},{"name":"activityLevelSelect","value":"1.375"}]
		
		String last_value = "";
		
		
		LinkedHashMap linked_hash_map = (LinkedHashMap)parser.parse(_jsonBlob, containerFactory);
		Iterator hash_itr = linked_hash_map.keySet().iterator();
		while (hash_itr.hasNext()) {
			String keyX = (String)hash_itr.next();
			Object value_objX = linked_hash_map.get(keyX);
			
			System.out.println("keyX >" + keyX);
			System.out.println("value_objX >" + value_objX);
			System.out.println("value_objX ii >" + ( value_objX instanceof LinkedHashMap ) );
			
			LinkedHashMap hash2 = (LinkedHashMap)value_objX;
			Iterator hash_itr_2 = hash2.keySet().iterator();
			while (hash_itr_2.hasNext()) {
				String keyXy = (String)hash_itr_2.next();
				Object value_objXy = hash2.get(keyXy);
				System.out.println("keyXy >" + keyXy);
				System.out.println("value_objXy >" + value_objXy);

				if (value_objXy instanceof LinkedHashMap) {
					System.out.println("this is a LinkedHashMap !");
					LinkedHashMap hash3 = (LinkedHashMap)value_objXy;
					Iterator hash_itr_3 = hash3.keySet().iterator();
					while (hash_itr_3.hasNext()) {
						String keyXyz = (String)hash_itr_3.next();
						Object value_objXyz = hash3.get(keyXyz);
						System.out.println("keyXyz >" + keyXyz);
						System.out.println("value_objXyz >" + value_objXyz);
					}
				} else if (value_objXy instanceof LinkedList) {
					System.out.println("this is a LinkedList !");

					LinkedList list_obj_2 = (LinkedList)value_objXy;
					Iterator list_itr_2 = list_obj_2.iterator();
					while (list_itr_2.hasNext()) {
						
						Long offset = null;
						String id = null;
						String name = null;
						String upc = null;
					
						LinkedHashMap linkedHashMap2 = (LinkedHashMap)list_itr_2.next();
						Iterator itr = linkedHashMap2.keySet().iterator();
						while (itr.hasNext()) {
							String keyXccC = (String)itr.next();
							Object value_objXccC = linkedHashMap2.get(keyXccC);
							System.out.println("this is a keyXccC >" + keyXccC);
							System.out.println("this is a value_objXccC >" + value_objXccC);
							
							if (keyXccC.equals("offset")) {
								offset = (Long)value_objXccC;
							} else if (keyXccC.equals("id")) {
								id = (String)value_objXccC;
							} else if (keyXccC.equals("name")) {
								name = (String)value_objXccC;
							}
							
						}
						
						
						System.out.println("id found !! >" + id);
						if (name.indexOf("UPC:") > -1) {
							int upc_index = name.indexOf("UPC:");
							upc = name.substring(upc_index + 4).trim();
							System.out.println("upc found !! >" + upc);
							name = name.substring(0, name.lastIndexOf(", ")).trim();
						}
						
						
						StringBuilder sb = new StringBuilder();
						for( String oneString : name.split(" ") ) {
							if (!oneString.trim().isEmpty()) {
								sb.append( oneString.substring(0,1) );
								sb.append( oneString.substring(1).toLowerCase() );
								sb.append(' ');
							}
						}
						name = sb.toString().trim();
						
						System.out.println("name found !! >" + name);
						
						food_found = true;
						
						System.out.println("creating food");
						
						
						Food fud = null;
						try {
							fud = Food.getFoodByUSDANDBNum(id);
							System.out.println("existing food found >" + fud.getLabel());
						} catch (ObjectNotFoundException x) {
							fud = new Food();
							fud.setCreateOrModifyPerson((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4424));
							if (name.length() > 200) {
								fud.setName(name.substring(0, 199));
							} else {
								fud.setName(name);
							}
							fud.setNDbNo(id);
							fud.setScanId(upc);
							fud.setOffset(offset);
							//fud.setCalories(BigDecimal.ONE);
							/*
							fud.setFat(inputFat);
							fud.setCarbs(inputCarbs);
							fud.setProtein(inputProtein);
							fud.setServingSize(inputServingSize);
							fud.setServingSizeType(servingSizeUnitValue);
							*/
							fud.setActive(true);
							fud.save();
						}
						
					
					}
					
					
				} else if (value_objXy instanceof String) {
					System.out.println("this is a String !");
				} else if (value_objXy instanceof Long) {
					System.out.println("this is a Long !");
				}
				
				
			}
			
			
		}
		
		return food_found;
		
	}
	
	public static void
	procUSDAItem(String _jsonBlob) throws ParseException, TorqueException, ObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		
		if (_jsonBlob == null || _jsonBlob.isEmpty()) {
			throw new IllegalValueException("jsonBlob not found");
		}
		
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactoryImpl();
		
		
		String last_value = "";
		
		Food fud = null;
		String name = null;
		String sd = null;
		String fg = "[NOT FOUND]";
		String manu = null;
		String ru = null;
		HashMap servings_hash = new HashMap();
		
		BigDecimal fat = null;
		BigDecimal carbs = null;
		BigDecimal protein = null;
		
		LinkedHashMap linked_hash_map = (LinkedHashMap)parser.parse(_jsonBlob, containerFactory);
		Iterator hash_itr = linked_hash_map.keySet().iterator();
		while (hash_itr.hasNext()) {
			String keyX = (String)hash_itr.next();
			Object value_objX = linked_hash_map.get(keyX);
			
			System.out.println("keyX >" + keyX);
			System.out.println("value_objX >" + value_objX);
			System.out.println("value_objX is LinkedHashMap >" + ( value_objX instanceof LinkedHashMap ) );
			System.out.println("value_objX is LinkedList >" + ( value_objX instanceof LinkedList ) );
			
			
			if (value_objX instanceof LinkedList) {
				LinkedList list_obj_2 = (LinkedList)value_objX;
				Iterator list_itr_2 = list_obj_2.iterator();
				while (list_itr_2.hasNext()) {

					LinkedHashMap linkedHashMap2 = (LinkedHashMap)list_itr_2.next();
					Iterator itr = linkedHashMap2.keySet().iterator();
					while (itr.hasNext()) {
						String keyXccC = (String)itr.next();
						Object value_objXccC = linkedHashMap2.get(keyXccC);
						System.out.println("this is a keyXccC >" + keyXccC);
						System.out.println("this is a value_objXccC >" + value_objXccC);
						System.out.println("value_objXccC is LinkedHashMap >" + ( value_objXccC instanceof LinkedHashMap ) );
						System.out.println("value_objXccC is LinkedList >" + ( value_objXccC instanceof LinkedList ) );
						
						if ( value_objXccC instanceof LinkedHashMap ) {
							System.out.println("this is a LinkedHashMap !");
							LinkedHashMap hash3 = (LinkedHashMap)value_objXccC;
							Iterator hash_itr_3 = hash3.keySet().iterator();
							while (hash_itr_3.hasNext()) {
								String keyXyz = (String)hash_itr_3.next();
								Object value_objXyz = hash3.get(keyXyz);
								System.out.println("keyXyz >" + keyXyz);
								System.out.println("value_objXyz >" + value_objXyz);
								System.out.println("value_objXyz is LinkedHashMap >" + ( value_objXyz instanceof LinkedHashMap ) );
								System.out.println("value_objXyz is LinkedList >" + ( value_objXyz instanceof LinkedList ) );
								
								if ( value_objXyz instanceof LinkedHashMap ) {
									LinkedHashMap hash4 = (LinkedHashMap)value_objXyz;
									Iterator hash_itr_4 = hash4.keySet().iterator();
									while (hash_itr_4.hasNext()) {
										String key_1 = (String)hash_itr_4.next();
										Object value_1 = hash4.get(key_1);
										System.out.println("key_1 >" + key_1);
										System.out.println("value_1 >" + value_1);
										System.out.println("value_1 is LinkedHashMap >" + ( value_1 instanceof LinkedHashMap ) );
										System.out.println("value_1 is LinkedList >" + ( value_1 instanceof LinkedList ) );
		
										if (key_1.equals("ndbno")) {
											fud = Food.getFoodByUSDANDBNum((String)value_1);
											System.out.println("found food >" + fud.getLabel());
										} else if (key_1.equals("name")) {
											name = (String)value_1;
										} else if (key_1.equals("sd")) {
											sd = (String)value_1;
										} else if (key_1.equals("fg")) {
											fg = (String)value_1;
										} else if (key_1.equals("manu")) {
											manu = (String)value_1;
										} else if (key_1.equals("ru")) {
											ru = (String)value_1;
										}
									}
								} else if ( value_objXyz instanceof LinkedList ) {

									LinkedList list_obj_4 = (LinkedList)value_objXyz;
									Iterator list_itr_4 = list_obj_4.iterator();
									while (list_itr_4.hasNext()) {
										
										System.out.println("___________________");
										
										//Long nutrient_id = null;
										String nutrient_name = null;
										String nutrient_unit = null;
										BigDecimal nutrient_value = null;

										LinkedHashMap linkedHashMap5 = (LinkedHashMap)list_itr_4.next();
										Iterator itr5 = linkedHashMap5.keySet().iterator();
										while (itr5.hasNext()) {
											String key_5 = (String)itr5.next();
											Object value_5 = linkedHashMap5.get(key_5);
											System.out.println("this is a key_5 >" + key_5);
											System.out.println("this is a value_5 >" + value_5);
											
											
											if (key_5.equals("measures")) {

												LinkedList list_obj_6 = (LinkedList)value_5;
												Iterator list_itr_6 = list_obj_6.iterator();
												
												while (list_itr_6.hasNext()) {
													
													String label = null;
													Double eqv = null;
													String eunit = null;
													//Double qty = null;
													//Double value = null;

													LinkedHashMap linkedHashMap7 = (LinkedHashMap)list_itr_6.next();
													System.out.println("linkedHashMap7 >" + linkedHashMap7);
													if (linkedHashMap7 != null) {
														Iterator itr7 = linkedHashMap7.keySet().iterator();
														while (itr7.hasNext()) {
															String key_7 = (String)itr7.next();
															Object value_7 = (Object)linkedHashMap7.get(key_7);

															System.out.println("this is a key_7 >" + key_7);
															System.out.println("this is a value_7 >" + value_7);

															if (key_7.equals("label")) {
																label = (String)value_7;
															} else if (key_7.equals("eqv")) {
																eqv = (Double)value_7;
															} else if (key_7.equals("eunit")) {
																eunit = (String)value_7;
															} else if (key_7.equals("qty")) {
																//qty = (Double)value_7;
															} else if (key_7.equals("value")) {
																//value = (Double)value_7;
															}

														}

														if (!servings_hash.containsKey(label)) {
															System.out.println("found new serving >" + label);
															if (label.length() > 50) {
																label = label.substring(0, 50);
															}
															FoodServingDb serving = new FoodServingDb();
															serving.setServingLabel(label);
															serving.setServingSize(new BigDecimal(eqv));
															if (eunit.equals("g")) {
																serving.setServingSizeType(Food.GRAMS_SERVING_SIZE_TYPE);
															} else if (eunit.equals("ml")) {
																serving.setServingSizeType(Food.ML_SERVING_SIZE_TYPE);
															} else if (eunit.equals("oz")) {
																serving.setServingSizeType(Food.OZ_SERVING_SIZE_TYPE);
															} else {
																throw new IllegalValueException("SERVING SIZE TYPE NOT FOUND >" + eunit);
															}
															servings_hash.put(label, serving);
														}
													
													}


												}
												
												
											} else if (key_5.equals("nutrient_id")) {
												//if (value_5 instanceof Long) {
												//	nutrient_id = (Long)value_5;
												//}
												
											} else if (key_5.equals("name")) {
												nutrient_name = (String)value_5;
											} else if (key_5.equals("unit")) {
												nutrient_unit = (String)value_5;
											} else if (key_5.equals("value")) {
												if (value_5 instanceof String) {
													nutrient_value = new BigDecimal((String)value_5);
												} else {
													nutrient_value = new BigDecimal((Double)value_5);
												}
												
											}

											/*
											Long nutrient_id = null;
											String nutrient_name = null;
											String nutrient_unit = null;
											BigDecimal nutrient_value = null;
											*/

										}
										

										if (nutrient_name != null) {
											System.out.println("nutrient_name >" + nutrient_name);

											if (nutrient_name.toLowerCase().equals("protein")) {
												System.out.println("protein found >" + nutrient_value);
												protein = nutrient_value;
											} else if (nutrient_name.toLowerCase().equals("total lipid (fat)")) {
												System.out.println("fat found >" + nutrient_value);
												fat = nutrient_value;
											} else if (nutrient_name.toLowerCase().equals("carbohydrate, by difference")) {
												System.out.println("carb found >" + nutrient_value);
												carbs = nutrient_value;
											}
										}


									}
									
									

					
									
								}
													
							
							
							}
						}

					}

				}
			} else if (value_objX instanceof Long) {
				
			}

			
			
		}
		
		/*
		Food fud = null;
		String name = null;
		String fg = null;
		String manu = null;
		HashMap servings_hash = new HashMap();
		*/
		
		if (fud == null) {
			throw new IllegalValueException("Unable to mine fud from JSON blob!!");
		}
		
		/*
		if (fat == null || carbs == null || protein == null) {
			try {
				throw new IllegalValueException("Nutrient values not found for " + fud.getLabel());
			} catch (Exception x) {
				x.printStackTrace();
			}
		} else {
		*/

			//fud.setName(name);
			fud.setDescription(sd);
			fud.setManufacturer(manu);
			fud.setCategory(fg);

			fud.setFat(fat);
			fud.setCarbs(carbs);
			fud.setProtein(protein);
			fud.setServingSize(new BigDecimal(100));
			if (ru.equals("g")) {
				fud.setServingSizeType(Food.GRAMS_SERVING_SIZE_TYPE);
			} else if (ru.equals("ml")) {
				fud.setServingSizeType(Food.ML_SERVING_SIZE_TYPE);
			} else {
				throw new IllegalValueException("Unknown reporting unit for " + name + " >" + ru);
			}
			Vector servings = new Vector();
			Iterator itr = servings_hash.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String)itr.next();
				FoodServingDb value = (FoodServingDb)servings_hash.get(key);
				servings.addElement(value);
			}
			fud.setServings(servings);
			fud.setPublic(true);
			fud.setCreateOrModifyPerson((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(4424));
			fud.save();
			
		/*
		}
			*/
	}
	
	private static String
	getStringFromBigDecimal(BigDecimal _bd) {
		if (_bd == null) {
			return "";
		}
		String str = _bd.setScale(2, RoundingMode.HALF_UP).toString();
		int index = str.indexOf(".00");
		if (index > -1) {
			return str.substring(0, index);
		}
		return str;
	}
	
	private String
	getNewClientHTMLEmailString(UKOnlinePersonBean _person, String _subject, String _body) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		//AddressBean address = _person.getDepartment().getCompany().getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
		
		StringBuffer buf = new StringBuffer();
		buf.append("<div>");
		buf.append("  <table cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px #acacac solid;width:615px\" align=\"center\">");
		buf.append("    <tbody><tr>");
		buf.append("      <td bgcolor=\"#11100e\" height=\"89\" valign=\"top\">");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:613px\">");
		buf.append("          <tbody><tr>");
		buf.append("            <td valign=\"middle\" height=\"89\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:24px;color:#8e8d8d;padding-left:30px;overflow:hidden;word-wrap:break-word;width:350px\">");
		buf.append("            <br><span style=\"font-size:20px\">" + _subject + "</span></td>");
		buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"><img src=\"https://www.valonyx.com/images/fitness-tracker-logo-163.jpg\"  /></td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("      </td>");
		buf.append("    </tr>");
		buf.append("    <tr>");
		buf.append("      <td>");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		buf.append("          <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word;padding:14px 26px 14px 26px\">");
		//buf.append("                  <br>" + address.getStreet1String() + " <br>" + address.getStreet2String() );
		//buf.append("                  <br>" + address.getCityString() + ", " + address.getStateString() + " " + address.getZipCodeString());
		//buf.append("                  <br><a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a></td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr>");
		buf.append("            <td style=\"padding-left:26px\">");
		buf.append("              <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #acacac;width:559px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td bgcolor=\"#e1eeef\" style=\"padding:13px 20px 13px 20px;font-family:Arial,Helvetica,sans-serif;color:#317679;font-size:18px;font-weight:bold;line-height:24px;overflow:auto;word-wrap:break-word;width:240px\">");
		
		/*
		if (_person == null) {
			buf.append("                  Welcome to the Fitness Tracker!");
		} else {
			buf.append("                  Welcome to the Fitness Tracker, " + _person.getFirstNameString() + "!");
		}
		*/
		
		buf.append("                  <br>" + _body + "</td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        <tr>");
		buf.append("            <td style=\"padding:12px 26px 16px 26px\">");
		buf.append("              <table width=\"561\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:561px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word\">");
		//buf.append("If you have any questions, please contact Remove Company at <a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a> . ");
		buf.append("                  <br>&nbsp;");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr bgcolor=\"#f2f2f2\">");
		buf.append("            <td style=\"border-top:1px solid #acacac;padding:26px 0px 14px 26px\">");
		buf.append("              <table width=\"587\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:587px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#6e6e6e;width:420px;overflow:auto;word-wrap:break-word\">  ");
		//buf.append("                  <a href=\"http://www.sanowc.com\" style=\"color:#317679!important\" target=\"_blank\">Remove Company</a>");
		buf.append("                  <br>This is an automated email; please do not reply. ");
		buf.append("                  <br>&nbsp;");
		buf.append("                  <br>");
		buf.append("                  ");
		buf.append("                  <br></td>");
		buf.append("                  <td width=\"167\" valign=\"bottom\">");
		buf.append("                    <a href=\"http://www.valonyx.com\"><img src=\"http://www.valonyx.com/images/valonyx-logo-grey-198.png\" /></a>");
		buf.append("                  </td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("</div>");
		
		return buf.toString();
	}
	
	
	
	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "/home/marlo/tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        //InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		InputStream in = FitnessServlet.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		System.out.println("in >" + in);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
	
	
	public static String updateSheetFromTrainingPlan(PersonBean _logged_in_person, TrainingPlan _plan, String _spreadsheetId) throws IOException, GeneralSecurityException {

		System.out.println("updateSheetFromTrainingPlan invoked");
		
		try {
			// Build a new authorized API client service.
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			//final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
			//final String spreadsheetId = "1jEw7pBO5gqODuyNFaAsBDxvgZcNiE0LIfpulf2GBmqY";
			//final String spreadsheetId = "1pvDLLB6unbASUGg2vXL8naxxZx20-ovoZe5DclFMkw0";
			//final String spreadsheetId = "1-Xhnd5CtPYbWQkpRt_JXIoDYG_tEZicNvOhdlx4WDCU";  // this is a copy of Will's sheet
			
			
			//final String spreadsheetId = "1IOuTST9Eqztcx2CgDYl9h8iXiZd0zERxd7c1LJVrxL0";  // this is the new template
			//final String spreadsheetId = "1t7_e7Y9rJEMelMEnrACvK11BGyOjNsr2kYmv2a64-PY";  // this is a copy of the new template
			
			
			
			
			Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
					.setApplicationName(APPLICATION_NAME)
					.build();
			
			Spreadsheet sheet_metadata = service.spreadsheets().get(_spreadsheetId).execute();
			List<Sheet> sheets = sheet_metadata.getSheets();
			
			Sheet sourceSheet = sheets.get(0);
			String sourceTitle = sourceSheet.getProperties().getTitle();
			System.out.println("sourceTitle >" + sourceTitle);
			int source_sheet_id = sourceSheet.getProperties().getSheetId();
			System.out.println("source_sheet_id >" + source_sheet_id);
			
			ArrayList<Integer> blocks_created = null;
			
			if (sheets.size() < _plan.getNumBlocks()) {
				System.out.println("need to add sheets to spreadsheet");
				
				if (blocks_created == null) {
					blocks_created = new ArrayList<Integer>();
				}
				
				//AddSheetRequest dfgdfg;
				
				//Set sheet name
				//Can be any string, I chose to set it to the account name
				//String sheetName = mCredential.getSelectedAccountName();
				
				//Create batchUpdateSpreadsheetRequest
				BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
				
				//Create requestList and set it on the batchUpdateSpreadsheetRequest
				List<com.google.api.services.sheets.v4.model.Request> requestsList = new ArrayList<com.google.api.services.sheets.v4.model.Request>();
				batchUpdateSpreadsheetRequest.setRequests(requestsList); // <-- this thing
					
				for (int block_number_to_create = (sheets.size() + 1) ; block_number_to_create <= _plan.getNumBlocks(); block_number_to_create++) {
					
					System.out.println("block_number_to_create >" + block_number_to_create);
					blocks_created.add(block_number_to_create);
					
					String sheetName = "Block " + block_number_to_create;

					/*
					//Create a new AddSheetRequest
					AddSheetRequest addSheetRequest = new AddSheetRequest();
					SheetProperties sheetProperties = new SheetProperties();

					//Add the sheetName to the sheetProperties
					addSheetRequest.setProperties(sheetProperties);
					addSheetRequest.setProperties(sheetProperties.setTitle(sheetName));
					*/
					
					DuplicateSheetRequest duplicateRequest = new DuplicateSheetRequest();
					duplicateRequest.setSourceSheetId(source_sheet_id);
					duplicateRequest.setInsertSheetIndex(block_number_to_create - 1);
					duplicateRequest.setNewSheetName(sheetName);
					
					//Create a new request with containing the addSheetRequest and add it to the requestList
					com.google.api.services.sheets.v4.model.Request request = new com.google.api.services.sheets.v4.model.Request();
					//request.setAddSheet(addSheetRequest);
					request.setDuplicateSheet(duplicateRequest);
					requestsList.add(request);
					
				}
				
				//Add the requestList to the batchUpdateSpreadsheetRequest
				batchUpdateSpreadsheetRequest.setRequests(requestsList);
				//Call the sheets API to execute the batchUpdate
				service.spreadsheets().batchUpdate(_spreadsheetId, batchUpdateSpreadsheetRequest).execute();
				
			}
			
			
			if (blocks_created != null) {
				
				// re-fetch the sheets
				
				sheet_metadata = service.spreadsheets().get(_spreadsheetId).execute();
				sheets = sheet_metadata.getSheets();
				
			}
			
			short blockNumber = 0;
			
			Iterator sheetItr = sheets.iterator(); // reading all the sheets - treating them as blocks, I guess
			while (sheetItr.hasNext()) {
				
				Sheet sheetObj = (Sheet)sheetItr.next();
				blockNumber++;
					
				String sheetTitle = sheetObj.getProperties().getTitle();
				//String range = "'" + sheetTitle + "'!A1:DH168";
				String range = "'" + sheetTitle + "'!A1:AW85";
				
				ValueRange response = service.spreadsheets().values()
						.get(_spreadsheetId, range)
						.execute();

				/*
				System.out.println(sheetTitle  + " >" + response);
				
				List<List<Object>> values = response.getValues();
				
				for (int i = 0; i < values.size(); i++) {
					System.out.println("row >" + i);
					List<Object> inner_list = (List<Object>)values.get(i);
					Iterator itr = inner_list.iterator();
					for (int c = 0; itr.hasNext(); c++) {
						Object obj = (Object)itr.next();
						String obj_str = (String)obj;
						if (c == 0) {
							System.out.print("\"" + obj_str + "\"");
						} else {
							System.out.print(",\"" + obj_str + "\"");
						}
					}
					
					System.out.println("");
				}
				*/
				
				
				
				try {
					
					short numWeeks = _plan.getNumWeeks();
					short numWorkoutsPerWeek = _plan.getNumWorkoutsPerWeek();
					
					System.out.println(".numWeeks >" + numWeeks);
					System.out.println(".numWorkoutsPerWeek >" + numWorkoutsPerWeek);
					
					List<List<Object>> values_xy = _plan.toGoogleSheet(_logged_in_person, blockNumber, ( blocks_created != null) );

					for (int i = 0; i < values_xy.size(); i++) {
						System.out.println("row_xy >" + i);
						List<Object> inner_list = (List<Object>)values_xy.get(i);
						Iterator itr = inner_list.iterator();
						for (int c = 0; itr.hasNext(); c++) {
							Object obj = (Object)itr.next();
							String obj_str = (String)obj;
							if (c == 0) {
								System.out.print("\"" + obj_str + "\"");
							} else {
								System.out.print(",\"" + obj_str + "\"");
							}
						}
						System.out.println("");
					}
					
					response.setValues(values_xy);
					Sheets.Spreadsheets.Values.Update request = service.spreadsheets().values().update(_spreadsheetId, range, response);
					request.setValueInputOption("RAW");
					UpdateValuesResponse responseX = request.execute();
					System.out.println("responseX >" + responseX);

				} catch (Exception x) {
					x.printStackTrace();
				}
					
			}

			
		} catch (Exception x) {
			x.printStackTrace();
		}
		
		return null;
	}
	
	
	
	public static String updateTrainingPlanFromSpreadsheet(String _spreadsheetId) throws IOException, GeneralSecurityException {

		System.out.println("updateTrainingPlanFromSpreadsheet invoked >" + _spreadsheetId);
		
		try {
			
			//String _spreadsheetId = "1skVHQkkPQQxqIEDH-8p3lpYWJ4iPJEkrt_XMtQCCUvE";
			
			//TrainingPlan training_plan_for_sheet = TrainingPlan.getTrainingPlanForGoogleSheetId(_spreadsheetId);
			PersonTrainingPlanMapping mapping = TrainingPlan.getPersonTrainingPlanMappingForGoogleSheetId(_spreadsheetId);
			TrainingPlan training_plan_for_sheet = TrainingPlan.getTrainingPlan(mapping.getTrainingPlanDbId());
			UKOnlinePersonBean plan_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(mapping.getPersonId());
			
			
			//training_plan_for_sheet.getWorkouts();
			
			// Build a new authorized API client service.
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			//final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
			//final String spreadsheetId = "1jEw7pBO5gqODuyNFaAsBDxvgZcNiE0LIfpulf2GBmqY";
			//final String spreadsheetId = "1pvDLLB6unbASUGg2vXL8naxxZx20-ovoZe5DclFMkw0";
			//final String spreadsheetId = "1-Xhnd5CtPYbWQkpRt_JXIoDYG_tEZicNvOhdlx4WDCU";  // this is a copy of Will's sheet
			
			
			//final String spreadsheetId = "1IOuTST9Eqztcx2CgDYl9h8iXiZd0zERxd7c1LJVrxL0";  // this is the new template
			//final String spreadsheetId = "1t7_e7Y9rJEMelMEnrACvK11BGyOjNsr2kYmv2a64-PY";  // this is a copy of the new template
			
			
			//String range = "L19";
			//String range = "'WS BB 2'!B14:O24";
			
			//String range = "'WS BB 2'!A1:DH168";
			//String range = "Sheet1!A1:DH168";
			
			
			
			Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
					.setApplicationName(APPLICATION_NAME)
					.build();
			
			Spreadsheet sheet_metadata = service.spreadsheets().get(_spreadsheetId).execute();
			List<Sheet> sheets = sheet_metadata.getSheets();
			
			System.out.println("sheets size >" + sheets.size());
			
			/*
			title = sheets[0].get("properties", {}).get("title", "Sheet1")
			sheet_id = sheets[0].get("properties", {}).get("sheetId", 0)
			*/
			
			//com.google.api.services.sheets.v4.model.Sheet sheetObj = sheets.get(1);
			//System.out.println("SHEET TITLE >" + sheetObj.getProperties().getTitle());
			
			short blockNumber = 0;
			
			Iterator sheetItrX = sheets.iterator(); // reading all the sheets - treating them as blocks, I guess
			while (sheetItrX.hasNext()) {
				
				try {
				
				
					Sheet sheetObj = (Sheet)sheetItrX.next();
					blockNumber++;

					String sheetTitle = sheetObj.getProperties().getTitle();
					System.out.println("sheetTitle >" + sheetTitle);
					String range = "'" + sheetTitle + "'!A1:AW85";
					ValueRange response = service.spreadsheets().values()
							.get(_spreadsheetId, range)
							.execute();

					System.out.println("response >" + response);

					int row_start_moves = 9;
					int row_week_offset = 20;

					int column_start_workout = 0;
					int column_workout_offset = 10;

					List<List<Object>> values = response.getValues();
					if (values == null || values.isEmpty()) {
						System.out.println("No data found.");
					} else {

						/*
						for (List row : values) {
							// Print columns A and E, which correspond to indices 0 and 4.
							//System.out.printf("%s, %s\n", row.get(0), row.get(4));
							//System.out.println(row.get(0));

							Iterator row_itr = row.iterator();
							while (row_itr.hasNext()) {
								System.out.println(row_itr.next());
							}

							//System.out.println(row.get(1));
							//System.out.println(row.get(2));
							//Log.info("Thing", row.get(0));

							// for writing

							//String obj = "12";
							//row.set(0, obj);
							//
						}
						*/


						for (short weekNumber = 0; weekNumber < 4; weekNumber++) { // 4 weeks
							System.out.println("week number >" + (weekNumber + 1) );
							for (int workoutNumber = 0; workoutNumber < 5; workoutNumber++) { // 5 workouts

								System.out.println("workoutNumber >" + (workoutNumber + 1) );

								int move_offset = column_start_workout + (workoutNumber * column_workout_offset);
								List workout_title_row = values.get((weekNumber * row_week_offset) + (row_start_moves - 2) );

								String workoutName = (String)workout_title_row.get(move_offset);
								System.out.println(" - workout name >" + workoutName);

								// maintain the TrainingPlanWorkout
								TrainingPlanWorkout trainingPlanWorkout = TrainingPlanWorkout.maintainTrainingPlanWorkout(training_plan_for_sheet, workoutName, blockNumber, (short)(weekNumber + (short)1) );
								Workout workoutInstance = null;

								String move_name = null;

								for (short i = 0; i < 13; i++) {

									int move_row_offset = (weekNumber * row_week_offset) + row_start_moves + i;

									List move_row = values.get(move_row_offset);
									if (!move_row.isEmpty() && (move_offset < move_row.size()) ) {



										String move_str = (String)move_row.get(move_offset);
										if (!move_str.isEmpty()) {
											if ( (move_name) == null || !move_name.equals(move_str)) {
												move_name = move_str;
											}
										}
										System.out.print("  move >" + move_name);

										String weightStr = "";
										String actualWeightStr = "";
										String setsStr = "";
										String repsStr = "";
										String rpeStrX = "";
										String actualRPEStr = "";

										try {
											weightStr = (String)move_row.get(move_offset + 2);
											actualWeightStr = (String)move_row.get(move_offset + 3);
											setsStr = (String)move_row.get(move_offset + 4);
											repsStr = (String)move_row.get(move_offset + 6);
											rpeStrX = (String)move_row.get(move_offset + 7);
											actualRPEStr = (String)move_row.get(move_offset + 8);
										} catch (IndexOutOfBoundsException x) {

										}


										System.out.print(", weight >" + weightStr);
										System.out.print(", actualWeightStr >" + actualWeightStr);
										System.out.print(", sets >" + setsStr);
										System.out.print(", reps >" + repsStr);
										System.out.print(", rpe >" + rpeStrX);
										System.out.println(", actualRPEStr >" + actualRPEStr);

										// assume if the move has an abbreviation that it's a compound move

										boolean row_contains_stuff_to_add = ( !move_str.isEmpty() ) || ( !move_name.isEmpty() && !setsStr.isEmpty() && !repsStr.isEmpty());
										System.out.println("row_contains_stuff_to_add >" + row_contains_stuff_to_add);
										if (row_contains_stuff_to_add) {

											/*
											boolean is_lift = true;
											boolean is_compound = ( (abbrStr != null) && !abbrStr.isEmpty());
											if (is_compound) {
												if (abbrStr.toLowerCase().equals("c") || abbrStr.toLowerCase().equals("cardio")) {
													is_compound = false;
													is_lift = false;
												}
											}
											*/

											// seems like there should be a better way... - consider a dropdown maybe
											boolean is_lift = ( !(move_name.toLowerCase().indexOf("cardio") > -1) );
											boolean is_compound = ( (move_name.toLowerCase().indexOf("squat") > -1) || (move_name.toLowerCase().indexOf("bench") > -1) || (move_name.toLowerCase().indexOf("dead") > -1) );

											Move move = Move.maintainMove(move_name, is_lift ? Move.LIFT_TYPE : Move.CARDIO_TYPE, is_compound);

											TrainingPlanMove trainingPlanMove = TrainingPlanMove.maintainTrainingPlanMove(move, trainingPlanWorkout, (short)( i + (short)1 ) );

											if (!setsStr.isEmpty()) {
												try {
													Iterator sets_itr = trainingPlanMove.getSets(Integer.parseInt(setsStr)).iterator(); // this will create filler sets if need be
													//lkjhlkj;
													while (sets_itr.hasNext()) {
														TrainingPlanSet plan_set = (TrainingPlanSet)sets_itr.next();
														if (!weightStr.isEmpty()) {
															try {
																plan_set.setWeightGoal(plan_person, new BigDecimal(weightStr));
															} catch (Exception x) { x.printStackTrace(); }
														}

														if (repsStr.isEmpty()) {
															// this is an AMRAP, I guess
															plan_set.setIsAMRAP(true);
														} else {
															try {
																plan_set.setRepGoalMin(plan_person, Short.parseShort(repsStr));
															} catch (Exception x) { x.printStackTrace(); }
														}

														if (!rpeStrX.isEmpty()) {
															plan_set.setRPEGoal(rpeStrX);
														}

														plan_set.save();

														System.out.println("actualWeightStr >" + actualWeightStr);
														System.out.println("actualRPEStr >" + actualRPEStr);

														WorkoutSet workoutSet = null;
														if (!actualWeightStr.isEmpty() || !actualRPEStr.isEmpty()) {
															if (workoutInstance == null) {
																workoutInstance = trainingPlanWorkout.getWorkoutForTrainingPlanWorkout(plan_person, true, true, true);
															}
															workoutSet = WorkoutSet.getSet(workoutInstance, plan_set); // this won't work if the TrainingPlanSet is new

															if (!actualWeightStr.isEmpty()) {
																try {
																	workoutSet.setActualWeight(new BigDecimal(actualWeightStr));
																} catch (Exception x) {	}
															}
															if (!actualRPEStr.isEmpty()) {
																try {
																	workoutSet.setActualRPE(new BigDecimal(actualRPEStr));
																} catch (Exception x) {	}
															}
															if (!repsStr.isEmpty()) {
																try {
																	workoutSet.setActualReps(Short.parseShort(repsStr));
																} catch (Exception x) {	}
															}
														}

														System.out.println("workoutSet >" + workoutSet);

														if (workoutSet != null) {
															workoutSet.setParent(plan_set);
															workoutSet.save();
															System.out.println("     &&&&&&workoutSet saved >" + workoutSet.getValue());

														}
													}
												} catch (Exception x) {
													x.printStackTrace();
												}
											}



										} else {
											// row does not contain stuff to add - remove the row/move from the TrainingPlanWorkout


											//TrainingPlanWorkout workout_obj = TrainingPlanWorkout.getCurrentTrainingPlanWorkout(logged_in_person, current_training_plan);

											Vector moves = trainingPlanWorkout.getMoves();

											if (i < moves.size()) {

												//TrainingPlanMove move_to_remove = TrainingPlanMove.getTrainingPlanMove(Long.parseLong(arg2));
												TrainingPlanMove move_to_remove = (TrainingPlanMove)moves.get(i);
												System.out.println("row does not contain stuff to add - remove the row/move >" + move_to_remove.getLabel());
												TrainingPlanMove.delete(move_to_remove.getId());

												//TrainingPlanMove.delete(Long.parseLong(arg2));
												trainingPlanWorkout.invalidate();

												Iterator itr = moves.iterator();
												for (short moveNumber = 1; itr.hasNext(); moveNumber++) {
													TrainingPlanMove training_plan_move = (TrainingPlanMove)itr.next();
													if (training_plan_move.getMoveNumber() != moveNumber) {
														training_plan_move.setMoveNumber(moveNumber);
														training_plan_move.save();
													}
												}

											}

										}



									}

								}

							}
						}


					}
				
				} catch (Exception x) {
					x.printStackTrace();
				}
						
			}
			
		} catch (Exception x) {
	
			x.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
	/*
	public static String updateTrainingPlanFromSpreadsheetChance(String _spreadsheetId) throws IOException, GeneralSecurityException {

		System.out.println("updateTrainingPlanFromSpreadsheet invoked >" + _spreadsheetId);
		
		try {
			
			//TrainingPlan training_plan_for_sheet = TrainingPlan.getTrainingPlanForGoogleSheetId(_spreadsheetId);
			PersonTrainingPlanMapping mapping = TrainingPlan.getPersonTrainingPlanMappingForGoogleSheetId(_spreadsheetId);
			TrainingPlan training_plan_for_sheet = TrainingPlan.getTrainingPlan(mapping.getTrainingPlanDbId());
			UKOnlinePersonBean plan_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(mapping.getPersonId());
			
			
			//training_plan_for_sheet.getWorkouts();
			
			// Build a new authorized API client service.
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			//final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
			//final String spreadsheetId = "1jEw7pBO5gqODuyNFaAsBDxvgZcNiE0LIfpulf2GBmqY";
			//final String spreadsheetId = "1pvDLLB6unbASUGg2vXL8naxxZx20-ovoZe5DclFMkw0";
			//final String spreadsheetId = "1-Xhnd5CtPYbWQkpRt_JXIoDYG_tEZicNvOhdlx4WDCU";  // this is a copy of Will's sheet
			
			
			//final String spreadsheetId = "1IOuTST9Eqztcx2CgDYl9h8iXiZd0zERxd7c1LJVrxL0";  // this is the new template
			final String spreadsheetId = "1t7_e7Y9rJEMelMEnrACvK11BGyOjNsr2kYmv2a64-PY";  // this is a copy of the new template
			
			
			//String range = "L19";
			//String range = "'WS BB 2'!B14:O24";
			
			//String range = "'WS BB 2'!A1:DH168";
			//String range = "Sheet1!A1:DH168";
			
			
			
			Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
					.setApplicationName(APPLICATION_NAME)
					.build();
			
			Spreadsheet sheet_metadata = service.spreadsheets().get(spreadsheetId).execute();
			List<Sheet> sheets = sheet_metadata.getSheets();
			
			
			//com.google.api.services.sheets.v4.model.Sheet sheetObj = sheets.get(1);
			//System.out.println("SHEET TITLE >" + sheetObj.getProperties().getTitle());
			
			short blockNumber = 0;
			
			Iterator sheetItr = sheets.iterator(); // reading all the sheets - treating them as blocks, I guess
			while (sheetItr.hasNext()) {
				
				Sheet sheetObj = (Sheet)sheetItr.next();
				blockNumber++;
					
				String sheetTitle = sheetObj.getProperties().getTitle();
				String range = "'" + sheetTitle + "'!A1:DH168";
				ValueRange response = service.spreadsheets().values()
						.get(spreadsheetId, range)
						.execute();

				System.out.println("response >" + response);

				int row_start_weeks = 11;
				int row_start_moves = 13;
				int row_week_offset = 20;

				int column_start_workout = 1;
				int column_workout_offset = 16;

				List<List<Object>> values = response.getValues();
				if (values == null || values.isEmpty()) {
					System.out.println("No data found.");
				} else {
					


					for (short weekNumber = 0; weekNumber < 8; weekNumber++) {
						System.out.println("week number >" + (weekNumber + 1) );
						for (int workoutNumber = 0; workoutNumber < 7; workoutNumber++) {

							System.out.println("workoutNumber >" + (workoutNumber + 1) );
							
							int move_offset = column_start_workout + (workoutNumber * column_workout_offset);
							List workout_title_row = values.get((weekNumber * row_week_offset) + row_start_moves - 2);
							
							String workoutName = (String)workout_title_row.get(move_offset);
							System.out.println(" - workout name >" + workoutName);
							
							// maintain the TrainingPlanWorkout
							TrainingPlanWorkout trainingPlanWorkout = TrainingPlanWorkout.maintainTrainingPlanWorkout(training_plan_for_sheet, workoutName, blockNumber, weekNumber);
							
							String move_name = null;
							
							for (short i = 0; i < 11; i++) {
								int move_row_offset = (weekNumber * row_week_offset) + row_start_moves + i;

								List move_row = values.get(move_row_offset);
								
								String move_str = (String)move_row.get(move_offset);
								if (!move_str.isEmpty()) {
									if ( (move_name) == null || !move_name.equals(move_str)) {
										move_name = move_str;
									}
								}
								System.out.print("  move >" + move_name);
								
								
								String weightStr = (String)move_row.get(move_offset + 6);
								String setsStr = (String)move_row.get(move_offset + 8);
								String repsStr = (String)move_row.get(move_offset + 10);
								String rpeStr = (String)move_row.get(move_offset + 11);
								String abbrStr = (String)move_row.get(move_offset + 13);
								
								
								System.out.print(", weight >" + weightStr);
								System.out.print(", sets >" + setsStr);
								System.out.print(", reps >" + repsStr);
								System.out.println(", rpe >" + rpeStr);
								System.out.println(", abbr >" + abbrStr);
								
								// assume if the move has an abbreviation that it's a compound move
								
								boolean row_contains_stuff_to_add = ( !move_str.isEmpty() ) || ( !move_name.isEmpty() && !setsStr.isEmpty() && !repsStr.isEmpty());
								System.out.println("row_contains_stuff_to_add >" + row_contains_stuff_to_add);
								if (row_contains_stuff_to_add) {
								
									boolean is_lift = true;
									boolean is_compound = ( (abbrStr != null) && !abbrStr.isEmpty());
									if (is_compound) {
										if (abbrStr.toLowerCase().equals("c") || abbrStr.toLowerCase().equals("cardio")) {
											is_compound = false;
											is_lift = false;
										}
									}

									Move move = Move.maintainMove(move_name, is_lift ? Move.LIFT_TYPE : Move.CARDIO_TYPE, is_compound);
									TrainingPlanMove trainingPlanMove = TrainingPlanMove.maintainTrainingPlanMove(move, trainingPlanWorkout, (short)( i + (short)1 ) );
									
									
									if (!setsStr.isEmpty()) {
										try {
											Iterator sets_itr = trainingPlanMove.getSets(Integer.parseInt(setsStr)).iterator();
											while (sets_itr.hasNext()) {
												TrainingPlanSet plan_set = (TrainingPlanSet)sets_itr.next();
												if (!weightStr.isEmpty()) {
													try {
														plan_set.setWeightGoal(plan_person, new BigDecimal(weightStr));
													} catch (Exception x) {	}
												}
												if (repsStr.isEmpty()) {
													// this is an AMRAP, I guess
													plan_set.setIsAMRAP(true);
												} else {
													try {
														plan_set.setRepGoalMin(plan_person, Short.parseShort(rpeStr));
													} catch (Exception x) {	}
												}
											}
										} catch (Exception x) {
											x.printStackTrace();
										}
									}
									
									
								}

							}
						}
					}


				}
			}
			
		} catch (Exception x) {
	
			x.printStackTrace();
		}
		
		return null;
	}
	*/
	
	
	
	
	
	public static String
	createBlankGoogleSheet() {
		URL url = null;
		try { 
			//url = new URL("https://script.google.com/macros/s/AKfycbxPmwAnQIEIxGwtWibHoQX5rM7HbCIT0LdlUS7_DNiohowgKVo/exec");
			url = new URL("https://script.google.com/macros/s/AKfycbz9gAgFlRMKTsm3UOwHdwogbhjtd6Z9XkGYUBYRWchLGwIHo7w/exec");
		} catch (MalformedURLException e) {
			System.out.println("ERROR: invalid URL ");
			return null;
		}
				
		String line;
		String result = "";

		try {
			// try opening the URL
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			//conn.setRequestProperty("x-app-key", "cb67688bed25360261caed9b4a698b64");
			//conn.setRequestProperty("x-app-id", "6cbb9f70");
			// //conn.setRequestProperty("Content-Type","application/json");

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
			   result += line;
			}
			rd.close();

		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR: couldn't open URL ");
		}

		System.out.println("result - >" + result);
		return result;
	}

	
	
	
	private String
	trainingPlanToExcel(UKOnlinePersonBean _person) throws TorqueException, IllegalValueException, ObjectNotFoundException, UniqueObjectNotFoundException, ObjectAlreadyExistsException, FileNotFoundException, IOException, Exception {
		
		short column_index = (short)1;
		short column_workout_offset = (short)16;
		
		int row_index = 13;
		int row_week_offset = 20;
		
		short sheet_index = (short)0;
		
		TrainingPlan current_training_plan = TrainingPlan.getCurrentTrainingPlan(_person);
		
		
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + "will-stueve.xlsx"));
		XSSFSheet sheet = wb.getSheetAt(sheet_index);
		
		com.badiyan.uk.online.beans.UKOnlineCourseReportLister courseReportLister = new com.badiyan.uk.online.beans.UKOnlineCourseReportLister();
		
		short last_week_number = 1;
		Iterator itr = current_training_plan.getWorkouts().iterator();
		while (itr.hasNext()) {
			TrainingPlanWorkout plan_workout = (TrainingPlanWorkout)itr.next();
			short week_number = plan_workout.getWeekNumber();
			if (week_number != last_week_number) {
				last_week_number = week_number;
				row_index += row_week_offset;
				column_index = (short)1;
			}
			
			//try {
				if (plan_workout.hasWorkoutForTrainingPlanWorkout(_person)) {
					Workout workout = plan_workout.getLastWorkoutForTrainingPlanWorkout(_person);
					LinkedHashMap<Move,ArrayList<WorkoutSet>> set_hash = WorkoutSet.getSets(workout);
					Iterator set_hash_itr = set_hash.keySet().iterator();
					for (int i = 0; set_hash_itr.hasNext(); i++) {
						Move key = (Move)set_hash_itr.next();
						ArrayList<WorkoutSet> sets = (ArrayList<WorkoutSet>)set_hash.get(key);

						//courseReportLister.addCell(sheet, row_index + i, column_index, plan_workout.getLabel() + "--" + week_number + "**" + key.getLabel());
						courseReportLister.addCell(sheet, row_index + i, column_index, key.getLabel());

						//b.append(this.toJSON(key, sets));

						short last_reps = -1;
						BigDecimal last_weight = null;
						short num_sets_at_weight = 0;
						short actual_reps = 0;
						boolean didThing = false;

						WorkoutSet previous_set_obj = null;

						Iterator set_itr = sets.iterator();
						while (set_itr.hasNext()) {
							WorkoutSet set_obj = (WorkoutSet)set_itr.next();
							actual_reps = set_obj.getActualReps();
							BigDecimal weight_for_set = set_obj.getActualWeight();
							System.out.println("last_reps >" + last_reps);
							System.out.println("actual_reps >" + actual_reps);
							System.out.println("last_weight >" + FitnessServlet.getStringFromBigDecimal(last_weight));
							System.out.println("weight_for_set >" + FitnessServlet.getStringFromBigDecimal(weight_for_set));

							String str = set_obj.getTrainingPlanSet().getParent().getDescription();
							if (!str.isEmpty()) {
								System.out.println("set desc >" + str + " -" + key.getLabel());
							}
							if (last_weight == null) {
								num_sets_at_weight++;
							} else if (weight_for_set == null) {
								num_sets_at_weight++;
							} else if (last_weight.compareTo(weight_for_set) != 0) {
								courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)6), FitnessServlet.getStringFromBigDecimal(last_weight));
								courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)8), num_sets_at_weight);
								courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)9), "x");
								courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)10), actual_reps);
								courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)14), num_sets_at_weight * actual_reps * last_weight.intValue() );

								if (previous_set_obj != null) {
									courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)11), previous_set_obj.getTrainingPlanSet().getParent().getDescription());
								}

								didThing = true;
								i++;
								num_sets_at_weight = 0;
							} else {
								num_sets_at_weight++;
							}

							//last_reps = actual_reps;
							last_weight = weight_for_set;
							previous_set_obj = set_obj;

						}

						if (last_weight != null) {
							courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)6), FitnessServlet.getStringFromBigDecimal(last_weight));
							if (didThing) {
								courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)8), (num_sets_at_weight + 1) );
								courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)14), (num_sets_at_weight + 1) * actual_reps * last_weight.intValue() );
							} else {
								courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)8), num_sets_at_weight);
								courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)14), num_sets_at_weight * actual_reps * last_weight.intValue() );
							}
							courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)9), "x");
							courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)10), actual_reps);

							if (previous_set_obj != null) {
								courseReportLister.addCell(sheet, row_index + i, (short)(column_index + (short)11), previous_set_obj.getTrainingPlanSet().getParent().getDescription());					
							}
						}

					}
				}
			//} catch (ObjectNotFoundException x) {
			//	x.printStackTrace();
			//}
			
			column_index += column_workout_offset;
		}
		
		//courseReportLister.addCell(sheet, row_index, column_index, enrollment_person.getEmployeeNumberString()); column_index++;
		

		//courseReportLister.addCell(sheet, 4, (short)6, "Monster BaSH " );

		String saveFilename = System.currentTimeMillis() + ".xlsx";
		FileOutputStream fileOut = new FileOutputStream(CUBean.getProperty("cu.realPath") + CUBean.getProperty("cu.resourcesFolder") + saveFilename);
		wb.write(fileOut);
		fileOut.close();
		
		return saveFilename;
	}
	
	public static void
	updateGoogleSheetFromTrainingPlan(PersonBean _person, TrainingPlan _plan, String _sheet_id) {
		
		System.out.println("updateGoogleSheetFromTrainingPlan invoked in FitnessServlet >" + _sheet_id);

		Timer timerObj = timer_hash.get(_sheet_id);
		System.out.println("timer found >" + timerObj);

		if (timerObj != null) {
			timerObj.cancel(); // this could potentially cancel a pending update of a training plan from a google sheet - probz not though
		}

		timerObj = new Timer(true);
		timer_hash.put(_sheet_id, timerObj);

		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, 1);
		//timerObj.schedule(new UpdateTrainingPlanFromGoogleSheetTask(arg1), now.getTime());
		timerObj.schedule(new UpdateGoogleSheetFromTrainingPlanTask(_person, _plan, _sheet_id), now.getTime());

	}
	
	
	

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
