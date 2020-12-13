package com.badiyan.uk.online.servlets;

import com.badiyan.uk.beans.*;

import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.online.beans.*;

import com.badiyan.uk.online.tasks.*;
import com.valonyx.servlets.RetailServlet;

import com.valonyx.tasks.KAAAdminTask;
import com.valonyx.tasks.USDAFoodDetailTask;
import com.valonyx.tasks.USDAFoodListTask;
import com.valonyx.util.KAAAdminClientManager;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Category;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Appender;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;

//import org.apache.poi.poifs.filesystem.*;
//import org.apache.poi.hssf.usermodel.*;

//import org.apache.poi.hssf.usermodel.*;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.standard.*;



/**
 * Replacement for StartupServlet that reads in log4j.xml configuration settings for
 * application servers that do not inherently use log4j (such as Oracle 91 AS).
 *
 * @author Randy Marchessault
 * $Id: UKOnlineStartupServlet.java,v 1.21 2011/06/30 18:28:54 marlo Exp $
 */
public class UKOnlineStartupServlet extends HttpServlet
{
    private static final String LOG4J_CONFIG_FILE = "/WEB-INF/log4j.xml";
    private static final String TORQUE_CONFIG_FILE = "Torque.properties";
    private static Timer timerObj = null;

    // INSTANCE METHODS

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param _request servlet request
     * @param _response servlet response
     */
    protected void
    doGet(HttpServletRequest _request, HttpServletResponse _response)
    throws ServletException, java.io.IOException
    {
	_response.setContentType("text/html");
	java.io.PrintWriter out = _response.getWriter();
	out.print("This servlet doesn't support the GET operation.");
	out.close();
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param _request servlet request
     * @param _response servlet response
     */
    protected void
    doPost(HttpServletRequest _request, HttpServletResponse _response)
    throws ServletException, java.io.IOException
    {
	_response.setContentType("text/html");
	java.io.PrintWriter out = _response.getWriter();
	out.print("This servlet doesn't support the POST operation.");
	out.close();
    }

    /**
     * Returns a short description of the servlet.
     */
    public String
    getServletInfo()
    {
	return "Universal Knowledge Startup Servlet";
    }

    /**
     * Initializes the servlet.
     */
    public void
    init(ServletConfig _config)
	throws ServletException
    {
	System.out.println("init() invoked in UKOnlineStartupServlet");

	super.init(_config);

	// Note: Category class has been deprecated, we should switch to exclusive use of Logger...
	Category  cat = Category.getInstance("com.badiyan");
	Logger log = Logger.getLogger(this.getClass());

	try
	{

	    System.out.println("LOG4J_CONFIG_FILE");

	    String configfile = LOG4J_CONFIG_FILE;
	    URL configURL = getServletContext().getResource(configfile);

	    if (configURL == null)
	    {
		// This may be normal, for appservers such as JBoss which use log4j themselves.
		// In such cases, we really should dynamically merge our configuration with theirs.
		// For now, we have done so manually by editing the external JBoss log4j.xml file...
		System.out.println("Info: " + configfile + " not found");

		// Initialize logging from web.xml settings

		// Get the logging category.
		String logLevel = _config.getInitParameter("logLevel");
		// Get the log level
		if (logLevel == null)
		    // If there's no log level...
		{
		    log.setLevel(Level.INFO);
		    cat.setLevel(Level.INFO);
		    // Default to INFO
		}
		else if (logLevel.equals("debug"))
		{
		    log.setLevel(Level.DEBUG);
		    cat.setLevel(Level.DEBUG);
		}
		else if (logLevel.equals("info"))
		{
		    log.setLevel(Level.INFO);
		    cat.setLevel(Level.INFO);
		}
		else if (logLevel.equals("warn"))
		{
		    log.setLevel(Level.WARN);
		    cat.setLevel(Level.WARN);
		}
		else if (logLevel.equals("error"))
		{
		    log.setLevel(Level.ERROR);
		    cat.setLevel(Level.ERROR);
		}
		else if (logLevel.equals("fatal"))
		{
		    log.setLevel(Level.FATAL);
		    cat.setLevel(Level.FATAL);
		}
		String logPattern = _config.getInitParameter("logPattern");
		// Get the log pattern

		// See if we have a console appender yet...
		boolean haveConsoleAppender = false;
		Enumeration e = log.getAllAppenders();
		while (e.hasMoreElements())
		{
		    Appender a = (Appender)e.nextElement();
		    System.out.println("Found appender: " + a.getClass().getName());
		    if (a instanceof ConsoleAppender)
		    {
			haveConsoleAppender = true;
		    }
		}

		if (!haveConsoleAppender)
		{
		    if (logPattern != null)
			// If the log pattern is setup...
		    {
			log.addAppender(new ConsoleAppender(
			new PatternLayout(logPattern), ConsoleAppender.SYSTEM_OUT));
			// Use the console for logging
		    }
		    else
		    {
			Layout layout = new PatternLayout();
			log.addAppender(new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT));
			// Use the console for logging
		    }
		}

		log.info("Logging is now setup.");

	    }
	    else
	    {
		// Configure log4j according to the configfile contents
		DOMConfigurator.configure(configURL);
		log = Logger.getLogger(this.getClass());
		log.info("DOMConfigurator successfully configured from file: " + configfile);
	    }

	    // Note: Category class has been deprecated, we should switch to exclusive use of Logger...
	    CUBean.setLoggingCategory(cat);

	    log.debug("System properties:");

	    //Properties props = new Properties(System.getProperties());

	    Enumeration parameterNames = _config.getInitParameterNames();
	    while (parameterNames.hasMoreElements())
	    {
		String parameterName = (String)parameterNames.nextElement();
		//props.setProperty(parameterName, _config.getInitParameter(parameterName));

		CUBean.setProperty(parameterName, _config.getInitParameter(parameterName));
		System.out.println(" parameterName >" + parameterName);
	    }
	    ServletContext _context = _config.getServletContext();
	    String realPath = _context.getRealPath("\\");
	    //props.setProperty("cu.realPath", realPath.substring(0, realPath.length() - 1));
		System.out.println(" realPath >" + realPath);
		if (realPath != null) {
			CUBean.setProperty("cu.realPath", realPath.substring(0, realPath.length() - 1));
		}

	    //System.setProperties(props);

	    System.out.println("cu.realPath >" + CUBean.getProperty("cu.realPath"));

	    log.info("System properties set.");

	    // Initialize Torque...
	    try
	    {
		//DBFactory.
		Class theClass = this.getClass();
		ClassLoader theLoader = theClass.getClassLoader();
		PropertiesConfiguration config = new PropertiesConfiguration();
		InputStream in_stream = theLoader.getResourceAsStream(TORQUE_CONFIG_FILE);
		config.load(in_stream);
		Torque.init(config);
		log.info("Torque is now setup.");
	    }
	    catch (TorqueException x)
	    {
		log.error("Torque initialization error >" + x.getMessage());
		System.out.println("Error >" + x.getMessage());
		x.printStackTrace();
	    }
	    /*
	    catch (IOException x)
	    {
		log.error("Torque initialization I/O error >" + x.getMessage());
				System.out.println("Error >" + x.getMessage());
				x.printStackTrace();
	    }
	     */
	    catch (Exception x)
	    {
		log.error("Torque initialization error >" + x.getMessage());
		System.out.println("Error >" + x.getMessage());
		x.printStackTrace();
	    }
	    catch (Error x)
	    {
		System.out.println("Error >" + x.getMessage());
		x.printStackTrace();
	    }

	    // Initialize default data



	    

	    try
	    {

			if (CUBean.isMasterServer())
			{
				initIndex();
				//copyClientJARFiles();
			}
			
			System.out.println("init timer");
			initTimer(false);


			//initIndex();

			UKOnlineRoleBean.maintainDefaultData();
			boolean data_created = CompanyBean.maintainDefaultData();
			if (data_created)
			{
				log.info("Setting up default data...");

				CompanyBean default_company = CompanyBean.getInstance();


				DepartmentBean default_department = default_company.getDefaultDepartment();
				PersonGroupBean.maintainGroup(default_company, PersonGroupBean.DEFAULT_PERSON_GROUP_NAME);
				PersonTitleBean.maintainDefaultData(default_company);

				/*
				DepartmentTypeBean domain = DepartmentTypeBean.maintainDepartmentType(ecolab, "Domain");
				DepartmentTypeBean region = DepartmentTypeBean.maintainDepartmentType(ecolab, "Region");
				DepartmentTypeBean area = DepartmentTypeBean.maintainDepartmentType(ecolab, "Area");
				DepartmentTypeBean district = DepartmentTypeBean.maintainDepartmentType(ecolab, "District");
				DepartmentTypeBean sub_area = DepartmentTypeBean.maintainDepartmentType(ecolab, "Sub-Area");
				DepartmentTypeBean route_group = DepartmentTypeBean.maintainDepartmentType(ecolab, "Route Group");

				DepartmentBean.maintainDepartment(ecolab, domain, "Institutional");

				PersonGroupBean.maintainGroup(ecolab, PersonGroupBean.DEFAULT_PERSON_GROUP_NAME);

				PersonTitleBean.maintainDefaultData(ecolab);
				PersonTitleBean.maintainPersonTitle(ecolab, EcolabPersonTitleBean.TM_PERSON_TITLE_NAME);
				PersonTitleBean.maintainPersonTitle(ecolab, EcolabPersonTitleBean.DM_PERSON_TITLE_NAME);
				PersonTitleBean.maintainPersonTitle(ecolab, EcolabPersonTitleBean.AM_PERSON_TITLE_NAME);
				PersonTitleBean.maintainPersonTitle(ecolab, EcolabPersonTitleBean.RM_PERSON_TITLE_NAME);
				PersonTitleBean.maintainPersonTitle(ecolab, EcolabPersonTitleBean.RVP_PERSON_TITLE_NAME);
				PersonTitleBean.maintainPersonTitle(ecolab, EcolabPersonTitleBean.RS_PERSON_TITLE_NAME);
				PersonTitleBean.maintainPersonTitle(ecolab, EcolabPersonTitleBean.ARM_PERSON_TITLE_NAME);
				PersonTitleBean.maintainPersonTitle(ecolab, EcolabPersonTitleBean.FTM_PERSON_TITLE_NAME);
				 */


				/*
				CategoryTypeBean.maintainDefaultData(ecolab);
				CategoryBean.maintainDefaultData(ecolab);
				 */

				PhoneNumberBean.maintainDefaultData();
				AddressBean.maintainDefaultData();

				UKOnlinePersonBean.maintainDefaultData();

				UKOnlinePersonBean.maintainPersonType(PersonBean.DEFAULT_NEW_USER_PERSON_TYPE);
				UKOnlinePersonBean.maintainPersonType(PersonBean.SALES_EXECUTIVE_PERSON_TYPE);
				UKOnlinePersonBean.maintainPersonType(PersonBean.SALES_MANAGER_PERSON_TYPE);
				UKOnlinePersonBean.maintainPersonType(PersonBean.REGION_MANAGER_PERSON_TYPE);
				UKOnlinePersonBean.maintainPersonType(PersonBean.BRANCH_MANAGER_PERSON_TYPE);
				UKOnlinePersonBean.maintainPersonType(PersonBean.STUDENT_PERSON_TYPE);
				UKOnlinePersonBean.maintainPersonType(PersonBean.INSTRUCTOR_SUPERVISOR_TYPE);
				UKOnlinePersonBean.maintainPersonType(PersonBean.MANAGER_SUPERVISOR_TYPE);
				UKOnlinePersonBean.maintainPersonType(PersonBean.SYSTEM_ADMIN_TYPE);





				//ResourceBean.maintainDefaultData();
				ResourceBean.maintainResourceType(ResourceBean.ACROBAT_RESOURCE_TYPE);
				ResourceBean.maintainResourceType(ResourceBean.CD_RESOURCE_TYPE);
				ResourceBean.maintainResourceType(ResourceBean.CLASSROOM_RESOURCE_TYPE);
				ResourceBean.maintainResourceType(ResourceBean.FLASH_RESOURCE_TYPE);
				ResourceBean.maintainResourceType(ResourceBean.INTERNET_RESOURCE_TYPE);
				ResourceBean.maintainResourceType(ResourceBean.POWERPOINT_RESOURCE_TYPE);
				ResourceBean.maintainResourceType(ResourceBean.VIDEO_RESOURCE_TYPE);
				ResourceBean.maintainResourceType(ResourceBean.WORD_RESOURCE_TYPE);
				ResourceBean.maintainResourceType(ResourceBean.XLS_RESOURCE_TYPE);
				ResourceBean.maintainResourceType(ResourceBean.UNKNOWN_RESOURCE_TYPE);

				CourseBean.maintainCourseType(com.badiyan.uk.online.beans.CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME);
				CourseBean.maintainCourseType(com.badiyan.uk.online.beans.CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME);
				CourseBean.maintainCourseType(com.badiyan.uk.online.beans.CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME);
				CourseBean.maintainCourseType(com.badiyan.uk.online.beans.CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME);

				ProductBean.maintainDefaultData();
				CourseBean.maintainDefaultData();
				EnrollmentBean.maintainDefaultData();
				CertificationRequirerBean.maintainDefaultData();
				AudienceBean.maintainDefaultData();
				LanguageBean.maintainDefaultData();

				//SyncServerIDBean.maintainDefaultData();



			}

			OrderBean.maintainOrderStatus(OrderBean.OPEN_ORDER_STATUS);
			OrderBean.maintainOrderStatus(OrderBean.CLOSED_ORDER_STATUS);


		    UKOnlinePersonBean.maintainPersonType(UKOnlinePersonBean.CLIENT_PERSON_TYPE);
		    UKOnlinePersonBean.maintainPersonType(UKOnlinePersonBean.PROSPECT_PERSON_TYPE);
			
			CompanyBean defaultCompany = CompanyBean.maintainCompany("[DEFAULT]");
			DepartmentBean defaultDepartment = defaultCompany.getDefaultDepartment();
			
			

	    }
	    catch (Exception x)
	    {
			/*
			// shutdown
			CUBean.sendEmail("marlo@badiyan.com", CUBean.getProperty("cu.adminEmail"), CUBean.getProperty("cu.mail.subject"), "startup error >" + x.getMessage());
			log.error(x.getMessage());
			x.printStackTrace();
			//shutdownJetty();
			initTimer(true);
			 */

			x.printStackTrace();
	    }



	    try
	    {
			UKOnlinePersonBean.getPerson(_config.getInitParameter("cu.adminUsername"));
	    }
	    catch (ObjectNotFoundException x)
	    {
			UKOnlinePersonBean adminPerson = new UKOnlinePersonBean();
			adminPerson.setUsername(_config.getInitParameter("cu.adminUsername"));
			adminPerson.setPassword(_config.getInitParameter("cu.adminPassword"));
			adminPerson.setConfirmPassword(_config.getInitParameter("cu.adminPassword"));
			adminPerson.setFirstName(_config.getInitParameter("cu.adminFirstName"));
			adminPerson.setLastName(_config.getInitParameter("cu.adminLastName"));
			adminPerson.setEmail(_config.getInitParameter("cu.adminEmail"));
			adminPerson.setPersonType(PersonBean.SYSTEM_ADMIN_TYPE);
			adminPerson.setTitle(PersonTitleBean.getDefaultTitle(UKOnlineCompanyBean.getCompany(1)));
			adminPerson.save();

			// associate the admin user with the default company

			RoleBean admin_role = RoleBean.getRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME);
			RoleBean company_admin_role = RoleBean.getRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME);

			adminPerson.addRole(admin_role);
			adminPerson.addRole(company_admin_role);

			// associate this person as the admin of the new organization

			CompanyAdministratorMapBean map = new CompanyAdministratorMapBean();
			map.setCompany(UKOnlineCompanyBean.getCompany(1));
			map.setPerson(adminPerson);
			map.save();
	    }

		
		
		/*
		Iterator qb_requset_itr = QBXMLGenericQueryRequest.getUnresolvedRequests().iterator();
		while (qb_requset_itr.hasNext())
		{
			QBXMLGenericQueryRequest qb_request = (QBXMLGenericQueryRequest)qb_requset_itr.next();
			QuickBooksSettings qb_settings = qb_request.getCompany().getQuickBooksSettings();
			QBPOSXMLRequestQueue queue = QBWebConnectorSvcSoapImpl.getQueue(qb_settings);
			queue.add(qb_settings.getCompanyKeyString(), qb_request);
		}
		*/
		
		
	}
	catch (Exception e)
	{
	    log.error(e.getMessage());
	    e.printStackTrace();
	}

	/*
	if (!CUBean.isMasterServer())
	{

	}
	 */
    }

    private static void
    initTimer(boolean _exit)
		throws TorqueException
    {
		if (timerObj == null) {
			
			timerObj = new Timer(true);

			Calendar now = Calendar.getInstance();

			int seconds = 1000;
			now.add(Calendar.MINUTE, 2);
				// start the tasks in two minutes
			
			/*
			QRCodeGenerator.generate("these warning bells are for me", 300, 300);
			timerObj.schedule(new com.valonyx.tasks.BitcoinChartsWeightedPricesTask(), now.getTime(), 15 * 60 * seconds); // every 15 minutes
			timerObj.schedule(new com.valonyx.tasks.BitcoinNetworkMonitorTask(), now.getTime());
			*/

			try {
				
				String clientPackageName = CUBean.getProperty("cu.clientPackageName");
				if (clientPackageName != null && clientPackageName.equals("iotlok")) {
					Date nowX = new Date();
					System.out.println("IoTLok instance found.  Setup KAA Admin task.(1)");
					KAAAdminClientManager.init(RetailServlet.KAA_SERVER_HOST);
					System.out.println("IoTLok instance found.  Setup KAA Admin task.(2)");
					timerObj.schedule(new KAAAdminTask(), 10000, 3 * seconds); // 3 seconds
					
					System.out.println("IoTLok instance found.  Setup KAA Admin task.(3)");
				}
				
				//List companies = UKOnlineCompanyBean.getCompaniesActiveOnly();
				
				UKOnlineCompanyBean sano = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5);
				
						
				System.out.println("CUBean.getProperty(\"cu.webDomain\") >" + CUBean.getProperty("cu.webDomain"));
				boolean is_localhost = ( CUBean.getProperty("cu.webDomain").indexOf("localhost") > -1 );
				System.out.println("is_localhost.. >" + is_localhost);
				
						
				if (!is_localhost) {
					//System.out.println("GOT COMPANIES >" + companies.size());

					//Iterator itr = companies.iterator();
					//while (itr.hasNext()) {
						//UKOnlineCompanyBean company = (UKOnlineCompanyBean)itr.next();
						
						
						timerObj.schedule(new MaintainBlankGoogleSheetsWorkoutTemplateTask(), now.getTime(), 24 * 60 * 60 * seconds); // every day
						now.add(Calendar.MINUTE, 2);
						
						
						timerObj.schedule(new USDAFoodDetailTask(), now.getTime(), 1 * 60 * 60 * seconds); // every hour
						now.add(Calendar.MINUTE, 30);


						timerObj.schedule(new AutoContactPatientTask(sano), now.getTime(), 24 * 60 * 60 * seconds); // every day
						now.add(Calendar.MINUTE, 2);

						timerObj.schedule(new AppointmentMonitorTask(sano), now.getTime(), 60 * seconds); // every minute
						//now.add(Calendar.MINUTE, 2);

						System.out.println("About to start LowInventoryReminderTask >" + sano.getLabel());
						timerObj.schedule(new LowInventoryReminderTask(sano), now.getTime(), 24 * 60 * 60 * seconds); // every day
						now.add(Calendar.MINUTE, 5);


						timerObj.schedule(new AppointmentReminderTask(sano), now.getTime(), 24 * 60 * 60 * seconds); // every day
						now.add(Calendar.MINUTE, 5);
						timerObj.schedule(new ClientReviewReasonReminderTask(sano), now.getTime(), 24 * 60 * 60 * seconds); // every day
						now.add(Calendar.MINUTE, 5);


						timerObj.schedule(new USDAFoodListTask(), now.getTime(), 24 * 60 * 60 * seconds); // every day
						

						// disabled for now.  I'm simply sending the email when the appointment is created
						// re-enabled...
					//}

					//itr = companies.iterator();
					//while (itr.hasNext())
					//{
						//UKOnlineCompanyBean company = (UKOnlineCompanyBean)itr.next();
						//System.out.println("company found >>> " + company.getLabel());
						try {
							QuickBooksSettings settings = sano.getQuickBooksSettings();
							System.out.println("settings found >>> " + settings.getLabel());
							if (settings.isQuickBooksFSEnabled())
							{
								System.out.println("starting QB data sync thread for " + sano.getLabel());
								timerObj.schedule(new QBFSDataRequestTask(sano), now.getTime()); // don't repeat
								now.add(Calendar.SECOND, 5);
							}
						} catch (Exception x) {
							x.printStackTrace();
						}
					//}

					now.add(Calendar.HOUR, 1);
					now.add(Calendar.SECOND, 45);

					//itr = companies.iterator();
					//while (itr.hasNext()) {
						//UKOnlineCompanyBean company = (UKOnlineCompanyBean)itr.next();
						timerObj.schedule(new BlackHoleMonitorTask(sano), now.getTime(), 60 * 60 * seconds); // every hour
						now.add(Calendar.SECOND, 60);
					//}

					now.add(Calendar.HOUR, 1);
					now.add(Calendar.SECOND, 45);

					//itr = companies.iterator();
					//while (itr.hasNext()) {
						//UKOnlineCompanyBean company = (UKOnlineCompanyBean)itr.next();
						//if (company.getId() == 5) {
							timerObj.schedule(new HerbDosageExpirationTask(sano), now.getTime(), 60 * 60 * seconds); // every hour
						//}

					//}

				}

				/* // commented out 5/30/11 - not thinking we need to do POS stuff anymore...
				now.add(Calendar.SECOND, 20);
				timerObj.schedule(new QBPOSXMLRequestQueueMonitorTask(), now.getTime(), 60 * seconds); // every minute
				 */

				/* // commented out 3/18/10 - this kind of update should happen at EoD
				now.add(Calendar.SECOND, 20);
				timerObj.schedule(new QBXMLRequestQueueMonitorTask(), now.getTime(), 60 * seconds); // every minute
				 */

				/* // commented out a long time ago
				now.add(Calendar.MINUTE, 1);
				timerObj.schedule(new NewClientMonitorTask(UKOnlineCompanyBean.getCompany(1)), now.getTime(), 60 * seconds); // every minute
				 */

				/* // commented out 3/18/10 - this kind of update should happen at EoD
				timerObj.schedule(new QBXMLRequestQueueDailyMonitorTask(), midnight.getTime(), 24 * 60 * 60 * seconds); // every day
				midnight.add(Calendar.MINUTE, 10);
				 */

				/* // commented out 5/30/11 - not thinking we need to do POS stuff anymore...
				Calendar midnight = Calendar.getInstance();
				midnight.set(Calendar.HOUR_OF_DAY, 23);
				midnight.set(Calendar.MINUTE, 59);
				midnight.add(Calendar.HOUR_OF_DAY, 1);
				timerObj.schedule(new QBPOSXMLRequestQueueDailyMonitorTask(), midnight.getTime(), 24 * 60 * 60 * seconds); // every day
				 */

			}
			catch (Exception x)
			{
				x.printStackTrace();
			}

		}
		
		/*
		SpiderLauncher launcher = new SpiderLauncher();
		launcher.launch("valonyx.com", "http://www.icd9data.com/2012/Volume2/A/AAT-to-Ablepsy.htm");
		*/
    }

    private static void
    initIndex()
    {
		// I need to determine if the index exists

		System.out.println("initIndex() invoked");

		try
		{
			String indexPath = CUBean.getProperty("cu.resourcesFolder") + "/index";
			//String indexPath = CUBean.getProperty("cu.resourcesFolder") + "/index";


			String realPath = CUBean.getProperty("cu.realPath");
			String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
			if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
			indexPath = realPath + resourcesFolder + "index";

			System.out.println("indexPath >" + indexPath);

			try
			{
				Searcher searcher = new IndexSearcher(indexPath);
			}
			catch (IOException ioException)
			{
				System.out.println("CREATING NEW INDEX");

				Date start = new Date();

				IndexWriter writer = new IndexWriter(indexPath, new StandardAnalyzer(), true);
				writer.close();

				Date end = new Date();

				System.out.print(end.getTime() - start.getTime());
				System.out.println(" total milliseconds");
			}
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }

    private static void
    copyClientJARFiles()
    {
		// make client JAR files available for client download

		try
		{
			String realPath = CUBean.getProperty("cu.realPath");
			String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");

			File jar_file_source = new File(realPath + "/WEB-INF/lib/" + "uk-common.jar");
			File jar_file_destination = new File(realPath + resourcesFolder + "uk-common.jar");
			CUBean.copyFile(jar_file_source, jar_file_destination);

			jar_file_source = new File(realPath + "/WEB-INF/lib/" + "uk-online.jar");
			jar_file_destination = new File(realPath + resourcesFolder + "uk-online.jar");
			CUBean.copyFile(jar_file_source, jar_file_destination);

			jar_file_source = new File(realPath + "/WEB-INF/lib/" + "uk-db.jar");
			jar_file_destination = new File(realPath + resourcesFolder + "uk-db.jar");
			CUBean.copyFile(jar_file_source, jar_file_destination);
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }

    private static void
    shutdownJetty()
    {
		try
		{
			Socket s=new Socket(InetAddress.getByName("127.0.0.1"), 8079);
			OutputStream outStream=s.getOutputStream();
			outStream.write(("stopJetty\r\nstop\r\n").getBytes());
			outStream.flush();
			s.close();
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }

}



