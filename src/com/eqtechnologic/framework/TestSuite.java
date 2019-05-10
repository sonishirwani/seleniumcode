package com.eqtechnologic.framework;
/**
 * This class executes the suites provided by testng.xml.
 */
import java.awt.AWTException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;
import javax.xml.soap.SOAPException;

import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.Select;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.eqtechnologic.adminconsolelib.LogoutScript;
import com.eqtechnologic.db.oracle.SuiteDBLogger;
import com.eqtechnologic.db.oracle.TestCaseDBLogger;
import com.eqtechnologic.exceptions.ExcelConnectionException;
import com.eqtechnologic.results.ClassesVsTestCasesMapping;
import com.eqtechnologic.testng.custom.CustomGenerateReport;
import com.eqtechnologic.testng.custom.CustomSuiteListenerAdapter;
import com.eqtechnologic.util.ExcelProcessor;
import com.eqtechnologic.util.FrameworkLogger;
import com.eqtechnologic.util.PropertiesToSystem;
import com.gargoylesoftware.htmlunit.WebClient;
import com.eqtechnologic.performance.timeLog;

@Listeners({TestCaseDBLogger.class, CustomSuiteListenerAdapter.class,CustomGenerateReport.class})
public class TestSuite {
	public WebDriver driver;
	private static HashMap<String, String> lacatorsMap;
	private Logger logger;
	String TClog;
	private ExcelProcessor suite= null;
	//private WebClient webClient = new WebClient();
	String newTClog;
	int pos;
	Map<String,Integer> idVsTestCase;
	public static LinkedHashMap<String,List<String>> failedTestCases=new LinkedHashMap<String,List<String>>();
	public static LinkedHashMap<String,List<String>> skippedTestCases=new LinkedHashMap<String,List<String>>();
	/********************************************************************AddedCode************************/
	public static HashMap <String,ArrayList<timeLog>> testMap=new HashMap<String,ArrayList<timeLog>>();
	static int parallelThreads=0;

	public static HashMap<String, String> getLacatorsMap() {
		return lacatorsMap;
	}
	

	private void setLacatorsMap() {

		String useObjectRepository = System.getProperty("objectRepository");
		if(useObjectRepository.equalsIgnoreCase("true"))
		{
			ObjectRepository objRepo;		
			objRepo = new ObjectRepository(this.logger);
			lacatorsMap = objRepo.createLocatorMap();
		}
	}

	public TestSuite()
	{
		suite = null;
	}

	/**
	 * It initialises the suite with the following parameters from the testng.xml.
	 * Also it load the properties file of Configuration folder into the System so that it can be used later.
	 * @param excelPath
	 * @param suiteName
	 * @param browser
	 * @param url
	 * @param hub
	 * @param executionMode
	 * @param exceptionScript
	 * @param configID
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws ExcelConnectionException
	 * @throws InterruptedException
	 */
	@Parameters({"excelPath","suiteName","browser","url", "hub","executionMode","exceptionScript","configID","waitForNUsers","buildNo","noOfTestCases"})
	@BeforeTest
	public void setSuiteDetails(String excelPath, String suiteName, String browser, String url, @Optional("NA") String hub,  
			@Optional("local") String executionMode, @Optional("false") String exceptionScript, @Optional("configID Not Set") String configID,@Optional("1") String waitForNUsers,@Optional("NA") String buildNo,@Optional("NA") String noOfTestCases) 
					throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, ExcelConnectionException, InterruptedException{
		logger = new FrameworkLogger("logs/"+suiteName+".log").getInstance();
		logger.info("Setting up the suite details for suite: "+ suiteName);
		System.setProperty("excelError","false");

		PropertiesToSystem configProperties = new PropertiesToSystem("Configuration/config.properties");
		PropertiesToSystem customVariablesproperties = new PropertiesToSystem("Configuration/custom_variables.properties");
		PropertiesToSystem customKeywordsproperties = new PropertiesToSystem("Configuration/custom_keywords.properties");

		configProperties.load();
		customVariablesproperties.load();
		customKeywordsproperties.load();

		setLacatorsMap();

		System.setProperty("excelPath", excelPath);
		System.setProperty("suiteName", suiteName);
		System.setProperty("browser", browser);
		System.setProperty("URL", url);
		System.setProperty("executionMode", executionMode);
		System.setProperty("hub", hub);
		System.setProperty("exceptionScript",exceptionScript);
		System.setProperty("CONFIGID", configID);
		System.setProperty("waitForNUsers", waitForNUsers);
		System.setProperty("buildNo", buildNo);
		System.setProperty("noOfTestCases", noOfTestCases);

		if(System.getProperty("db.logging").equals("true"))
		{
			SuiteDBLogger dbLogger = new SuiteDBLogger(this.logger);
			idVsTestCase=dbLogger.addSuiteInfo(this.logger);
		}
		String runSecurityTest = System.getProperty("runSecurityTest");
		String proxyIp = System.getProperty("zap.proxy.ip");
		String proxyPort = System.getProperty("zap.proxy.port");

		if(runSecurityTest.equalsIgnoreCase("true")){
			logger.info("Testing Mode set for : Security Testing!!");
			if (proxyIp.equals("") || proxyPort.equals("")){
				logger.fatal("Security Testing is set to TRUE but ZAP Proxy Address or Port are not provided." +
						"\n Please correct the configuration before starting.");
				throw new RuntimeException("Security Testing is set to TRUE but ZAP Proxy Address or Port are not provided." +
						"\n Please correct the configuration before starting.");
			}
		}

	}	

	/**
	 *This method gets parameter from DataProvider and executes the test cases for every suite.
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ExcelConnectionException 
	 * @throws InterruptedException 
	 */
	@Test(dataProvider="testsuite")
	public void suiteExecution(String excelPath, String sheetName, String executeTest, String suitSheetName) throws Throwable{
		Logger TClogger = new FrameworkLogger("logs/"+sheetName+".log").getInstance();
		TClog=TClogger.getName();
		pos=TClog.lastIndexOf("/");
		newTClog=TClog.substring(pos+1,TClog.length());
		WebDriver driver = null;
		HashMap <String,String> map = new HashMap<String,String>();
		Interpreter interpreter = null;

		try {
			ExcelProcessor excel = new ExcelProcessor(excelPath.trim(),sheetName.trim(),TClogger);
			System.setProperty("excelError","false");
			//Testsheet name before traversing to keyword saved in variable for naming rule of folder
			String JSsheetName=sheetName;
			System.setProperty("JSsheetName1", JSsheetName);
			interpreter = new Interpreter(excel,driver,map,TClogger,this.testMap);
			interpreter.iterateRows();
		} 
		catch (Throwable e) {
			if(e instanceof ExcelConnectionException)
				System.setProperty("excelError","true");
			if(e instanceof UnhandledAlertException)
				Alerts.handleAlert(interpreter.driver,TClogger);

			try {
				Screenshot.takeScreenshot(sheetName, map.get("STEP_ID")+"_FAILURE", "2000", interpreter.driver, TClogger);
			} catch (NoSuchWindowException e1) {
				TClogger.error("Screenshot could not be taken as no window was open!! "+ e1.getMessage());
			} catch (Throwable e2){
				TClogger.error("Screenshot could not be taken because of some exception!! "+ e2.getMessage());
			}

			if(System.getProperty("exceptionScript").equalsIgnoreCase("true")){
				TClogger.info("Parameter exceptionScript is: true. Hence performing the logout !");
				LogoutScript logout = new LogoutScript(interpreter.driver);
				logout.logoutAfterError(TClogger);
			}else{
				TClogger.info("Parameter exceptionScript is: false. Not performing the logout !");
			}

			if(interpreter!=null && interpreter.driver != null){
				String generatePageSource = System.getProperty("generatePageSourceForFailedTests");
				if(generatePageSource!=null && generatePageSource.equalsIgnoreCase("TRUE") && e instanceof WebDriverException)
					TClogger.debug(interpreter.driver.getPageSource());
			}
			//Window.closeWindow(interpreter.driver,TClogger);
			TClogger.fatal("Exception occurred. Cause:\n", e);
			throw e;
		}
		finally{
			//Alerts.handleAlert(interpreter.driver,TClogger);
			Window.closeWindow(interpreter.driver,TClogger);
			System.out.println("killing processes: iexplore.exe");
			Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
		}
	}


	public void suiteExecution(String excelPath, String sheetName, WebDriver driver, HashMap<String,String> map,Logger TClogger) throws Exception{
		ExcelProcessor excel = new ExcelProcessor(excelPath.trim(),sheetName.trim(),TClogger);
		Interpreter interpreter = new Interpreter(excel,driver,map,TClogger,this.testMap);
		interpreter.iterateRows();

	}
	@AfterMethod
	public void endTestCase(ITestResult result)
			throws IOException
	{
		InputStream inStream = null;
		OutputStream outStream = null;
		File tc=new File(TClog);
		int status = result.getStatus();
		String testCaseName = (String)(result.getParameters())[1];
		String testsuitename = System.getProperty("suiteName");
		String srcPath = System.getProperty("snapshotBaseDirectory")+"/"+testsuitename+"/"+testCaseName;
		String srcPath1="./logs";
		File F=new File(srcPath1);
		String passedPath = System.getProperty("snapshotBaseDirectory")+"/"+testsuitename+"/Passed/";
		String failedPath = System.getProperty("snapshotBaseDirectory")+"/"+testsuitename+"/Failed/";
		String passedLogs="logs"+"/"+testsuitename+"/"+"Passed"+"/";
		String failedLogs="logs"+"/"+testsuitename+"/"+"Failed"+"/";
		String skippedLogs="logs"+"/"+testsuitename+"/"+"skipped"+"/";
		if((F).exists()){
			if(status == ITestResult.SUCCESS){

				FileUtils.copyFileToDirectory(tc,new File(passedLogs),true);

			}

			if(status == ITestResult.FAILURE){

				FileUtils.copyFileToDirectory(tc,new File(failedLogs),true);

			}

			if(status == ITestResult.SKIP){

				FileUtils.copyFileToDirectory(tc,new File(skippedLogs),true);

			}
		}


		if((new File(srcPath)).exists()){
			if(status == ITestResult.SUCCESS){
				File oldfolder = new File(passedPath+testCaseName);
				if(oldfolder.exists()){
					String[]entries = oldfolder.list();
					for(String s: entries){
						File currentFile = new File(oldfolder.getPath(),s);
						currentFile.delete();
					}
					oldfolder.delete();
					System.out.println("Old file deleted");
				}	
				FileUtils.moveDirectoryToDirectory(new File(srcPath), new File(passedPath), true);
			}
			if(status == ITestResult.FAILURE){
				File oldfolder = new File(failedPath+testCaseName);
				if(oldfolder.exists()){
					String[]entries = oldfolder.list();
					for(String s: entries){
						File currentFile = new File(oldfolder.getPath(),s);
						currentFile.delete();
					}
					oldfolder.delete();
					System.out.println("Old file deleted");
				}
				FileUtils.moveDirectoryToDirectory(new File(srcPath), new File(failedPath), true);
			}
		}

		//Code to generate failed and skipped test cases start here
		String suiteName=System.getProperty("suiteName");
		//This array returns the parameters passed to the method for execution i.e Excel Path and sheet name
		Object[] parameters = result.getParameters();
		//Create blank sheets for passed test cases in failed test suite
		if(status == ITestResult.SUCCESS && !failedTestCases.containsKey(suiteName) ){
			List<String> failedList=new ArrayList<String>();
			failedTestCases.put(suiteName, failedList);
		}

		// check if the test case got failed	
		if(status == ITestResult.FAILURE){
			if(failedTestCases.containsKey(suiteName)){
				List<String> failedList=failedTestCases.get(suiteName);
				String failedTestCasesList=(String)parameters[0]+"#"+(String)parameters[1];
				failedList.add(failedTestCasesList);
				failedTestCases.put(suiteName, failedList);
			}
			else{
				String failedTestCasesList=(String)parameters[0]+"#"+(String)parameters[1];
				List<String> failedList=new ArrayList<String>();
				failedList.add(failedTestCasesList);
				failedTestCases.put(suiteName, failedList);

			}
		}
		if(status == ITestResult.SKIP){
			if(skippedTestCases.containsKey(suiteName)){
				List<String> skippedList=skippedTestCases.get(suiteName);
				String skippedTestCasesList=(String)parameters[0]+"#"+(String)parameters[1];
				skippedList.add(skippedTestCasesList);
				skippedTestCases.put(suiteName, skippedList);
			}
			else{
				String skippedTestCasesList=(String)parameters[0]+"#"+(String)parameters[1];
				List<String> skippedList=new ArrayList<String>();
				skippedList.add(skippedTestCasesList);
				skippedTestCases.put(suiteName, skippedList);

			}
		}

		/*
		 * Test case wise jacoco logic starts here
		 * Logic for Jacoco coverage per test case
		 * creating exec file for the executed test case
		 */
		String testCase;
		testCase=(String)parameters[1];
		ClassesVsTestCasesMapping ClassesVsTestCasesMapping=new ClassesVsTestCasesMapping();
		String  runJacocoCoverage=System.getProperty("runJacocoCoverage");
		if(runJacocoCoverage.equals("true")){
			ClassesVsTestCasesMapping.createMappingExcel(parameters, suiteName, testCase);
		}

		System.out.println("--------------------TESTCASE ENDED------------------------------");

	}


	/*@AfterSuite
	public void createExcelForFailedTestCases (){
		CustomTestListenerAdapter ctla = new CustomTestListenerAdapter();
		ctla.printFailedTestcases();

	}*/
	@DataProvider(name="testsuite", parallel=true)
	public Object[][] queueData() throws IOException, ExcelConnectionException{


		ArrayList<Object[]> objArray = new ArrayList<Object[]>();;
		FileInputStream inputStream ;
		FileInputStream inputStream1 ;

		String excelFilePath = System.getProperty("excelPath");
		String suiteName = System.getProperty("suiteName");


		if(!suiteName.equalsIgnoreCase("blank") && !suiteName.equalsIgnoreCase("Runall"))
		{
			this.suite = new ExcelProcessor(excelFilePath,suiteName,this.logger);
			/************************************Performance code****************************************************/
			String performanceFlag = System.getProperty("performanceFlag");
            int noOfTestCases = this.suite.rowCount;
            if(performanceFlag.equals("true"))
            {
            	 if(noOfTestCases > this.suite.rowCount)
                     throw new RuntimeException("Testsuite contains less number of row than specified in 'noOfTestCases' parameter ");
                noOfTestCases = Integer.parseInt(System.getProperty("noOfTestCases"));
               
            }
            		//End of code

			for(int row=1;row<=noOfTestCases;row++){
				ArrayList<String> obj = new ArrayList<String>();
				String suitePath = null;
				for(int col=0;col<2;col++){
					suitePath = this.suite.getCellValue(row, col);

					if(suitePath == null)
						break;

					obj.add(suitePath);


				}
				obj.add("Temp");
				obj.add("Temp");
				if(suitePath != null)
					objArray.add(obj.toArray(new Object[obj.size()]));
			}

			this.logger.info("\t-----------------------------------------------------------");
			this.logger.info("\t\t\t Executing suite : " +System.getProperty("suiteName"));
			this.logger.info("\t\tNumber of testcases in current suite : "+objArray.size());
			this.logger.info("\t-----------------------------------------------------------");
		}

		else if(suiteName.equalsIgnoreCase("Runall")){

			inputStream = new FileInputStream(new File(excelFilePath));
			Workbook outPutWorkBookx = ObjectRepository.getWorkbook(inputStream, excelFilePath);
			int noOfSheets = outPutWorkBookx.getNumberOfSheets();
			logger.fatal("*** Number of sheets in Suite Excel = "+noOfSheets);

			for(int i = 0 ; i < noOfSheets ; i++){
				logger.fatal("*** Iterating over Sheet Name = "+outPutWorkBookx.getSheetName(i));
				this.suite = new ExcelProcessor(System.getProperty("excelPath"),outPutWorkBookx.getSheetName(i),this.logger);

				int testCaseCount = 0;

				for(int row=1;row<=this.suite.rowCount;row++){
					ArrayList<String> obj = new ArrayList<String>();
					String suitePath = null;
					for(int col=0;col<2;col++){
						suitePath = this.suite.getCellValue(row, col);

						if(suitePath == null)
							break;

						obj.add(suitePath);


					}
					obj.add("Temp");
					obj.add("Temp");
					if(suitePath != null){
						objArray.add(obj.toArray(new Object[obj.size()]));
						testCaseCount++;
					}

				}

				this.logger.info("\t-----------------------------------------------------------");
				this.logger.info("\t\t\t Adding Sheet to Suit : " +outPutWorkBookx.getSheetName(i));
				this.logger.info("\t\tNumber of testcases in current suite : "+testCaseCount);
				this.logger.info("\t-----------------------------------------------------------");
			}	
		}
		else
		{
			inputStream = new FileInputStream(new File(excelFilePath));
			Workbook outPutWorkBookx = ObjectRepository.getWorkbook(inputStream, excelFilePath);
			int noOfSheets = outPutWorkBookx.getNumberOfSheets();
			logger.fatal("*** Number of sheets in Suite Excel = "+noOfSheets);

			for(int i = 0 ; i < noOfSheets ; i++){
				logger.fatal("*** Iterating over Sheet Name = "+outPutWorkBookx.getSheetName(i));
				this.suite = new ExcelProcessor(System.getProperty("excelPath"),outPutWorkBookx.getSheetName(i),this.logger);

				int testCaseCount = 0;

				for(int row=1;row<=this.suite.rowCount;row++){
					ArrayList<String> obj = new ArrayList<String>();
					// Excel value is used store the String data from Excel Test Suit. Value will be retrieved using getValue(row,col)

					String excelPath = this.suite.getCellValue(row, 1);
					String sheetName = this.suite.getCellValue(row, 2);
					String executeTest = this.suite.getCellValue(row, 0);
					String suitSheetName = this.suite.getCellValue(row, 2);

					if(excelPath != null)
					{
						obj.add(excelPath);
						obj.add(sheetName);
						obj.add(executeTest);
						obj.add(suitSheetName);
					}


					/*			for(int col=0;col<3;col++)
					{
						excelValue = this.suite.getCellValue(row, col);
						if(excelValue == null || excelValue.equalsIgnoreCase("N")) // This if checks whether this is to be Executed or not
							break;
						obj.add(excelValue);
						if(col==2) {// Adding Test Suit Sheet name to Object Array for logging purpose
							obj.add(outPutWorkBookx.getSheetName(i));
							testCaseCount = testCaseCount +1;
						}
					}

					 */			
					if(executeTest != null && !executeTest.equalsIgnoreCase("N"))
					{
						objArray.add(obj.toArray(new Object[obj.size()]));
						testCaseCount++;
					}
				}

				this.logger.info("\t-----------------------------------------------------------");
				this.logger.info("\t\t\t Adding Sheet to Suit : " +outPutWorkBookx.getSheetName(i));
				this.logger.info("\t\tNumber of testcases in current suite : "+testCaseCount);
				this.logger.info("\t-----------------------------------------------------------");
			}

		}
		return objArray.toArray(new Object[objArray.size()][]); 


	}

}
