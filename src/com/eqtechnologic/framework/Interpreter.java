package com.eqtechnologic.framework;
import static com.eqtechnologic.framework.TestSuite.parallelThreads;

/**
 * This class is the heart of the system. It provide methods to read
 * the excel sheet and iterate them row by row and build the logic accordingly.
 * Its constructor takes ExcelProcessor object as its input
 * to initialize the interpreter.
 */
import java.awt.AWTException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;

import com.eqtechnologic.exceptions.ExcelConnectionException;
import com.eqtechnologic.performance.timeLog;
import com.eqtechnologic.security.ZAPTasks;
import com.eqtechnologic.util.ExcelProcessor;
import com.eqtechnologic.util.ReplaceMacro;

import javax.xml.soap.SOAPException;
import org.json.JSONException;


public class Interpreter{
	
	/**
	 * This enum contains all the keywords which will be used to decide the logic.
	 */
	private enum Keywords {
		ACCEPTALERT,JSCOVER,CLICK,CLICKATXY,CLICK_OR_IGNORE,CLICK_WO_SCROLL,CLOSEWINDOW,DELAY,DND,FRAME,FRAMES,GETPAGESOURCE,LOOPEND,LOOPSTART,PERFORM,SC_ACTIVESCAN,SC_NEWSESSION,SC_SPIDER,SCREENSHOT,SELECT,SWITCHTOWINDOW,TEXTBOX,UPLOAD,UPLOADALLFROMPATH,URL,VERIFY_ALERT,NEWWINDOW,DATA_EXPORTS,DRAGACTIVITY,NUMERICBOX,VALIDATETEXT,EXECUTESCRIPT,TEXTFIELD,SELECTVALUE,SELECTLINK,TRIGGER,PRINTCTIME,DEFAULT,TC_EXPORTS_LOGOUT, STARTTIME, ENDTIME, COUNTTHREADS, WAITFORNUSERS, WAITUNTIL,BACK,FORWARD,REFRESH,GETTEXT,SETTEXT;
	}
	public WebDriver driver;
	public Logger logger;
	public HashMap<String,String> map;
	private ExcelProcessor script;
	
	//Variables to collect loop index
	public String locatorFromloop;
	public String parameterFromloop;
	private HashMap<String, String> lacatorsMap;
	static int wait_count=0;
	public String text=null;
	/****************************Code Added for performance testing purpose****************************/
	public HashMap <String,ArrayList<timeLog>> testMap;
	public ArrayList<timeLog> testList= new ArrayList<timeLog>();
	Object b=null;
	private int op=1;
	/**
	 * Constructor which takes ExcelProcessor as its input
	 * @param excel
	 */
	
	public Interpreter(ExcelProcessor excel, WebDriver driver,
			HashMap<String, String> map,Logger logger,HashMap <String,ArrayList<timeLog>> testMap) {
		this.script = excel;
		this.driver=driver;
		this.map=map;
		this.logger = logger;
		//this.JSsheetName = JSsheetName;
		this.lacatorsMap = TestSuite.getLacatorsMap();
		/****************************Code Added for performance testing purpose****************************/
		this.testMap=testMap;
	}
	/**
	 * It takes input as parameters and then split it and then put all the
	 * parameter values in the map with key as ${PARAM_X}. _X is the sequential order.
	 * @param excel
	 */
	private void createProperties(String parameters){
		if(parameters!= null && parameters!=""){
			String values[] = parameters.split("%%");
			int counter=1;
			if(values.length!=0){
				for(String value:values){
					value=ReplaceMacro.replace(value, this.map);
					this.map.put("PARAM"+counter, value.trim());
					counter++;
				}
			}
		}
	}
	/** It iterates each row contained in the script(excel sheet).
	 * @throws Exception 
	 * @throws Throwable 
	 */
	public void iterateRows() throws Exception {
		
		String executionMode=System.getProperty("executionMode");
		String url = System.getProperty("URL");
		//String hub=System.getProperty("hub");
		
		logger.info("\t---------------------------------------------------------------------------------------------------------");
		logger.info("\t\t\t Executing Test Case : " +script.sheetName);
		logger.info("\t\t Number of steps in current testcase : "+script.rowCount);
		logger.info("\t---------------------------------------------------------------------------------------------------------");
		
		if(this.driver == null){
			//this.driver=Driver.setDriver(browser,this.driver,logger);
			if(executionMode.equalsIgnoreCase("remote"))
			{
				this.driver=Driver.setremoteDriver(this.driver,logger);	
			}
			else
			{
				this.driver=Driver.setDriver(this.driver,logger);
			}
			Driver.openUrl(url,this.driver,logger);
		}
		int i,size;
		if(script.rowCount == 0){
			logger.error("Number of test steps is zero. Quitting iterations");
			return;
		}
			for(i=1;i<=script.rowCount;i++){
				logger.info("Excel : " +script.sheetName+", Iterating row : "+(i+1));
				size=processKeywordAtRowNum(i);
				i=i+size;
				
			}	
			/****************************Code Added for performance testing purpose****************************/
			if(System.getProperty("performanceFlag").equalsIgnoreCase("true"))
			{
			this.testMap.put(script.sheetName, this.testList);
			}
			//this.t.put(System.getProperty("JSsheetName1"), this.a);
	}

	/**
	 * It takes the rowNumber as its input  and then reads the whole row
	 * and store keyword, locator method, locator value and parameter in a string.
	 * Keyword is a mandatory input which decides which method of which class 
	 * will be called.
	 * @param rowNumber
	 * @throws Exception 
	 * @throws Throwable 
	 */
	public int processKeywordAtRowNum(int rowNumber) throws Exception{
		map.put("STEP_ID", script.sheetName +"_"+ Integer.toString(rowNumber+1));
		final int KEYWORD_COL_LOC = 0;
		final int LOCATOR_METHOD_COL_LOC = 1;
		final int LOCATOR_VALUE_COL_LOC = 2;
		final int PARAMETERS_COL_LOC = 3;
		final int XX_PARAMETERS_COL_LOC = 4;
		int size=0;
		
		
		String keywordStr = script.getCellValue(rowNumber, KEYWORD_COL_LOC);
		String parameters = script.getCellValue(rowNumber, PARAMETERS_COL_LOC);
		
		String locatorMethod = script.getCellValue(rowNumber, LOCATOR_METHOD_COL_LOC);
		String locatorValue = script.getCellValue(rowNumber, LOCATOR_VALUE_COL_LOC);
		//String parameters = script.getCellValue(rowNumber, PARAMETERS_COL_LOC);
		//Insert JSCover jsonGeneration value into string
		String json = System.getProperty("jsonGeneration");
		if(keywordStr == null || keywordStr == "")
			return size;
		
		keywordStr = keywordStr.trim().toUpperCase();
		
		//Assign loop index values to {index} pattern found in locator method and parameters
				if(this.locatorFromloop!=null){
					locatorValue=locatorFromloop;
				}
				if(this.parameterFromloop!=null){
					parameters=parameterFromloop;
				}
	
		Keywords keyword= null;
		try{
			keyword = Keywords.valueOf(keywordStr);
		}catch(IllegalArgumentException e){
			
			if(System.getProperty(keywordStr) != null && keywordStr!=""){
				TestSuite ts = new TestSuite();
				createProperties(parameters);
				logger.info("\t---------------------------------------------------------------------------------------------------------");
				logger.info("\t\t\t Executing KEYWORD : " +keywordStr);
				logger.info("\t---------------------------------------------------------------------------------------------------------");
				ts.suiteExecution(System.getProperty(keywordStr), keywordStr,this.driver,this.map,this.logger);
				return size;
			}
			logger.fatal("Keyword : "+keywordStr+" not identified.");
			throw e;
			
		}
		// @Author shantanud
				// Code to get Locator key from Hashmap
				// Also add a Property into config.properties file to Enable Object Repository.
				//String locatorValue ;
				String locatorKey ;
				String xxParameters;
				String useObjectRepository = System.getProperty("objectRepository");
				//System.out.println("**********"+ useObjectRepository);
				if(useObjectRepository.equalsIgnoreCase("true"))
				{
					if(keywordStr.matches("URL|ACCEPTALERT|SWITCHTOWINDOW|CLOSEWINDOW|LOOPSTART|LOOPEND|SCREENSHOT|DELAY|SC_NEWSESSION|SC_ACTIVESCAN|SC_SPIDER|GETPAGESOURCE|VERIFY_ALERT|SELECTLINK|PRINTCTIME|DEFAULT"))
					{
						locatorValue = "";
						locatorKey = "";
					}
					else
					{
						locatorKey = script.getCellValue(rowNumber, LOCATOR_VALUE_COL_LOC);
						locatorValue = lacatorsMap.get(locatorKey);
						locatorValue= ReplaceMacro.replace(locatorValue, map);
						if(locatorValue.contains("XX") || locatorValue.contains("xx"))
						{
							xxParameters = script.getCellValue(rowNumber, XX_PARAMETERS_COL_LOC);
							xxParameters = ReplaceMacro.replace(xxParameters,map);
							if(!keywordStr.matches("SELECTVALUE"))
							{
								locatorValue =replaceXXParameters(locatorValue,xxParameters);
								logger.fatal("Locator value after replacing with XX Parameter is : "+locatorValue);
							}
						}

						if(locatorValue == null)
						{
							logger.fatal("Locator value not found in Map. Kindly check Object Repository file. For Key = "+locatorKey);
							throw new NoSuchFieldError("Locator value not found in Map. Kindly check Object Repository file. For Key = "+locatorKey);
						}
					}
				}
				else
				{
					locatorValue = script.getCellValue(rowNumber, LOCATOR_VALUE_COL_LOC);
				}
				
		locatorValue= ReplaceMacro.replace(locatorValue, map);
		parameters= ReplaceMacro.replace(parameters,map);
			switch(keyword){
			
				case FRAME: Frame.switchTo(locatorMethod,locatorValue,this.driver,logger); break;
				case FRAMES: Frame.switchToMany(locatorValue,this.driver,logger); break;
				case CLICK: Click.click(locatorMethod,locatorValue,this.driver,logger); break;
				case CLICKATXY:Click.clickAtXY(locatorMethod, locatorValue, parameters, driver, logger);break;
				case CLICK_OR_IGNORE: Click.clickOrIgnore(locatorMethod, locatorValue, driver, logger); break;
				case CLICK_WO_SCROLL: Click.clickWOScroll(locatorMethod, locatorValue, driver, logger); break;
				case TEXTBOX: Textbox.clearAndType(locatorMethod,locatorValue,parameters,this.driver,logger); break;
				case PERFORM: CollectiveActions.performActions(locatorMethod,locatorValue,parameters,this.driver,logger); break;
				case SELECT: Selection.selectByText(locatorMethod, locatorValue, parameters,this.driver,logger); break;
				case ACCEPTALERT: Alerts.handleAlert(this.driver,logger); break;
				case SWITCHTOWINDOW: Window.switchToWindow(this.driver,logger); break;
				case NEWWINDOW: Window.newWindow(this.driver,logger); break;
				case CLOSEWINDOW: 
					//If jsonGeneration is set to true, it would generate json and then closewindow. Else, in case of false, it would close window without saving json
					if(json.equalsIgnoreCase("true"))
					{
						System.out.println("JSONgenerationis"+json);
						JSCover.takeJson(System.getProperty("JSsheetName1"),this.driver,logger);
					}
					Window.closeWindow(this.driver,logger); break;
				case LOOPSTART:size= Loop.looping(parameters,rowNumber,script,this.driver,map,logger,testMap); break;
				case LOOPEND:; break;
				case SCREENSHOT:Screenshot.takeScreenshot(script.sheetName,rowNumber+"",parameters,this.driver,logger); break;
				case DELAY:Delay.delay(parameters,logger); break;
				case UPLOAD:Textbox.upload(locatorMethod,locatorValue,parameters,this.driver,logger); break;
				case UPLOADALLFROMPATH:Textbox.uploadAllFromPath(locatorMethod,locatorValue,parameters,logger); break;
				case DATA_EXPORTS:String S1=System.getProperty("excelBaseDirectory");
				//File F = new File(S1);
				            //System.out.println("**********"+F.getAbsolutePath());
				            //logger.info("%%%"+F.getAbsolutePath());
					Textbox.DATA_EXPORTS(locatorMethod, locatorValue,S1,parameters,rowNumber+"",script.sheetName,this.driver,logger); break;
				case SC_NEWSESSION: ZAPTasks.newSession(script.sheetName,logger); break;
				case SC_ACTIVESCAN: ZAPTasks.activeScan(logger); break;
				case SC_SPIDER: ZAPTasks.spider(logger); break;
				case URL: Driver.openUrl(parameters,this.driver,logger); break;
				case DND: DragAndDropAtXY.perform(locatorMethod, locatorValue, parameters,this.driver,logger); break;
				case GETPAGESOURCE:this.logger.info("Page Source for the current browser instance:\n\n"+this.driver.getPageSource()); break;
				case VERIFY_ALERT: Alerts.verifyAlert(driver, parameters, logger); break;
				case JSCOVER: JSCover.takeJson(System.getProperty("JSsheetName1"),this.driver,logger); break;
				case NUMERICBOX:Textbox.numericbox(locatorMethod,locatorValue,parameters,this.driver,logger); break;
				case DRAGACTIVITY : DragActivity.dragDropActivity(locatorMethod, locatorValue, parameters, driver, logger); break;
				case VALIDATETEXT : ValidateText.compareText(locatorMethod, locatorValue, parameters, driver, logger); break;
				case EXECUTESCRIPT : ExecuteScript.actionPerform(locatorMethod, locatorValue, parameters, driver, logger);break;
				case TEXTFIELD : TextField.textFieldType(locatorMethod,locatorValue,parameters,this.driver,logger); break;
				case SELECTVALUE : SelectByValue.selectValue(locatorMethod, locatorValue, parameters, driver, logger);break; 
				case SELECTLINK : SelectByValue.selectLink(locatorMethod, locatorValue, parameters, driver, logger);break;
				case TRIGGER : TriggerTxn.triggerTransaction(locatorMethod, locatorValue,parameters, driver, logger);break;
				case TC_EXPORTS_LOGOUT:String S2=System.getProperty("excelBaseDirectory");
				Textbox.TC_EXPORTS_LOGOUT(S2,parameters,rowNumber+"",script.sheetName,this.driver,logger); break;
				case DEFAULT:  Switchdefaultwindow.switchd(this.driver, this.logger);break; 
				/****************************Code Added for performance testing purpose****************************/
				case STARTTIME:timeLog tL= new timeLog();
								tL.setStartTime();
								if (parameters==null)
									parameters="Opeartion"+op;
								tL.setOperation(parameters);
								op++;
								b=tL;
								break;
				case ENDTIME:((timeLog) b).calculateTimeDiff();
								this.testList.add((timeLog)this.b);
								break;
				case COUNTTHREADS:parallelThreads++; break;
				case WAITFORNUSERS: int temp=Integer.parseInt(System.getProperty("waitForNUsers")); 
								System.out.println("Waiting for other users \n Thread value reached to  "+parallelThreads+ " Wait till Thread value reach:"+temp); 
								while(parallelThreads < temp && wait_count<240000 )
								{	wait_count++;
									Thread.sleep(100);}
								if(wait_count>=240000 && parallelThreads < temp)
								{   System.out.println("One or more threads failed and stopped execution, continuing execution of this thread");}
								break;
				case WAITUNTIL: long tempTime=Long.parseLong(System.getProperty("waitForPageLoadInseconds"));
					     		this.driver.manage().timeouts().pageLoadTimeout(tempTime, TimeUnit.SECONDS);
								WebDriverWait wait = new WebDriverWait(this.driver, tempTime);
								wait.until(ExpectedConditions.elementToBeClickable(Locator.getLocator(locatorMethod, locatorValue, logger)));
								break;
								
				case BACK     : Driver.NavigateBack(this.driver, logger);break;
				case FORWARD  : Driver.NavigateForward(this.driver, logger);break;
				case REFRESH  : Driver.NavigateRefresh(this.driver, logger);break;
				case GETTEXT:   text=Textbox.getText(locatorMethod, locatorValue, driver, logger); break;
                case SETTEXT:   Textbox.clearAndType(locatorMethod,locatorValue,text.trim(),this.driver,logger); break;
				default: ;
			}
			
		return size;
	}
	private String replaceXXParameters(String locatorValue, String xxParameters) {
		int i=0;
		String[] xxValues = xxParameters.split(";");
		//	String[] locatorValueList = locatorValue.split(";");

		while(locatorValue.contains("XX") || locatorValue.contains("xx"))
		{
			if(i > xxValues.length)
			{
				logger.fatal("There are some conflicts in XX Parameter values & Locator values. One or More than one XX Parameters values are requires and should be separated by ';'. ");
				throw new NoSuchFieldError();
			}
			if(locatorValue.contains("XX"))
				locatorValue = locatorValue.replaceFirst("XX",xxValues[i]);
			if(locatorValue.contains("xx"))
				locatorValue = locatorValue.replaceFirst("xx",xxValues[i]);
			i++;
		}
		return locatorValue;
		// TODO Auto-generated method stub
	}
}
