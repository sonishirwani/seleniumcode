package com.eqtechnologic.db.oracle;
import java.sql.Connection;
import java.sql.ResultSet;
/* This  class implements the ITestListener interface.
 * Hence it has to define each method of the interface.
 * All the methods simply update the status of testcases 
 * into the database according to the method called.
 */
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestCaseDBLogger implements ITestListener{

	private Object[] parameters=null;
	
	@Override
	public void onTestSuccess(ITestResult result) {
		parameters = result.getParameters();
		Logger logger = Logger.getLogger("logs/"+parameters[1].toString()+".log");
		logger.info("Test Case: \""+parameters[1]+"\" PASSED.");
		updateStatus("PASS",logger);
	
	}

	@Override
	public void onTestFailure(ITestResult result) {
		parameters = result.getParameters();
		if(parameters.length == 0)
			return;
		Logger logger = Logger.getLogger("logs/"+parameters[1].toString()+".log");
		if(System.getProperty("excelError").equals("true")){
			result.setStatus(ITestResult.SKIP);
			logger.fatal("Test Case: \""+parameters[1]+"\" SKIPPED!!");
			updateStatus("SKIP",logger);
			return;
		}
		logger.fatal("Test Case: \""+parameters[1]+"\" FAILED!!");
			updateStatus("FAIL",logger);
	
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		parameters = result.getParameters();
		Logger logger = Logger.getLogger("logs/"+parameters[1].toString()+".log");
		logger.fatal("Test Case: \""+parameters[1]+"\" SKIPPED!!");	
		updateStatus("SKIP",logger);
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
	@Override
	public void onStart(ITestContext context) {}
	@Override
	public void onFinish(ITestContext context) {}
	@Override
	public void onTestStart(ITestResult result) {}

	private void updateStatus(String status,Logger logger) {
		
		if(System.getProperty("db.logging").equals("false"))
			return;
		String tcID = (String) parameters[1];
		int count=0;
		System.out.println(count);
		try {
			count = getRunID(parameters[1].toString(),logger);
			
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String query = "UPDATE TESTCASE_TABLE SET STATUS='"+status+"', TC_RUN_ID ='"+count+ "' WHERE"+
				 " ID=(SELECT MAX(ID) FROM TESTCASE_TABLE WHERE TESTCASE='"+tcID+"' AND RUN_ID = (SELECT MAX(ID) FROM URL_TABLE WHERE CONFIG_ID='"+System.getProperty("CONFIGID")+"'))";
		
		DBManager db = new DBManager();
		try {
			db.runQuery(query,logger);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			logger.fatal("InstantiationException exception occurred while running query on database !",e);
			throw new RuntimeException("InstantiationException exception occurred while running query on database !",e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.fatal("IllegalAccessException exception occurred while running query on database !",e);
			throw new RuntimeException("IllegalAccessException exception occurred while running query on database !",e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.fatal("ClassNotFoundException exception occurred while running query on database !",e);
			throw new RuntimeException("ClassNotFoundException exception occurred while running query on database !",e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.fatal("Some SQL exception occurred while running query on database !",e);
			throw new RuntimeException("Some SQL exception occurred while running query on database !",e);
		}
	}
	
	public int getRunID(String testCase,Logger logger)throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
	//code added for run id of test cases
		if(System.getProperty("db.logging").equals("false"))
			return 0;
		
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs=null;
		DBManager db = new DBManager();
    	int count=0;
    	
    	String retrieveCount= "Select max(TC_RUN_ID ) Count from TESTCASE_TABLE where TESTCASE='"+ testCase + "' AND SUITE_NAME='"+ 
    	System.getProperty("suiteName")+"' AND RUN_ID = (SELECT MAX(ID) FROM URL_TABLE WHERE CONFIG_ID='"+System.getProperty("CONFIGID") +"')  group by '"
    	+ testCase +"','"+System.getProperty("suiteName")+"' ";  
     
		try {
				connection = db.getConnection();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			stmt = connection.createStatement();
			rs=stmt.executeQuery(retrieveCount);
			 while (rs.next()){
		    count=rs.getInt("Count");
			count+=1;
			 }
			connection.commit();
			return count;
			
	}
}
