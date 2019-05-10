package com.eqtechnologic.db.oracle;
/* This class logs all the test cases present for a suite into the DB
 *  with status equal to 'NOT RUN'.
 * */
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.eqtechnologic.exceptions.ExcelConnectionException;
import com.eqtechnologic.util.ExcelProcessor;

public class SuiteDBLogger {
	private static Object lock=new Object();
	private String suiteDir;
	private String suiteName;
	private ExcelProcessor excel=null;
	
	/* The constructor takes the argument from System for the suite being run and put the values 
	 * into the object's string objects */
	public SuiteDBLogger(Logger logger) throws ExcelConnectionException{
		this.suiteDir = System.getProperty("excelPath");
		this.suiteName = System.getProperty("suiteName");
		excel = new ExcelProcessor(this.suiteDir, this.suiteName,logger);
	}
	
	/* It reads the excel and returns an ArrayList object containing all the testcases*/
	private ArrayList<String> getTestCases(){
		
		ArrayList<String> tcList =  new ArrayList<String>();
		
		for(int i=1; i<=excel.rowCount; i++){
			String  excelName = excel.getCellValue(i, 0);
			String sheetName = excel.getCellValue(i, 1);
			if(excelName != null && sheetName != null)
				tcList.add(sheetName);
				
		}
		return tcList;
	}
	
	
	/* It insert a new entry into the database with the status of all testcases as 'NOT RUN'*/
	public Map<String, Integer> addSuiteInfo(Logger logger)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		if (System.getProperty("db.logging").equals("false"))
			return Collections.emptyMap();

		logger.info("DB Logging is ON !!");

		ArrayList<String> testCaseList = getTestCases();
		Map<String, Integer> idVsTestCase = new HashMap<>();
		synchronized (lock) {
			DBManager db = new DBManager();
			for (String tc : testCaseList) {
				try {
					int id = db.addSuiteInfo(suiteName, tc, logger);
					idVsTestCase.put(tc, id);
				} catch (SQLException e) {

				}
			}
		}
		return idVsTestCase;
	}
}
