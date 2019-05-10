package com.eqtechnologic.testng.custom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;

import com.eqtechnologic.exceptions.ExcelConnectionException;
import com.eqtechnologic.framework.TestSuite;
import com.eqtechnologic.performance.PerfXMLResult;
import com.eqtechnologic.performance.timeLog;
import com.eqtechnologic.results.ClassesVsTestCasesMapping;
import com.eqtechnologic.util.ExcelProcessor;
import com.eqtechnologic.util.FrameworkLogger;

import mx4j.log.Logger;
/**
 * This class implements the ISuiteListener interface. 
 * It receives the ISuite object reference and extracts the suite details like failed testcases
 * and saves the result of each suite into an excel workbook 'Failed-TestSuite_<suite-tag-name>' 
 * in the running folder after all the testcases are run.
 * @author mayank
 *
 */

public class CustomSuiteListenerAdapter implements ISuiteListener{
	public static ISuite suite1=null;
	public static ISuite suite2=null;
	ExcelProcessor excel= new ExcelProcessor();
	ExcelProcessor tempExcel;
	private org.apache.log4j.Logger logger;
	public static String classTestCaseMappingPath;
	/*earlier this method was used to create failed test cases file after execution of all the test cases in which 
	the sequence of failed test cases was not the actual sequence of execution.
	Now this logic is implemented in endTestCase method under @AfterMethod annotation 
	
	/*
	@Override
	public void onFinish(ISuite suite) {
		
		Map<String, ISuiteResult> suiteResultMap  = suite.getResults();
		Set<String> suiteResultSet = suiteResultMap.keySet();
		ExcelProcessor excel= new ExcelProcessor();
	  System.out.println();
		String suiteFileName = suite.getName();
		for(String suiteName:suiteResultSet){
			//System.out.println(key);
			//System.out.println(suiteResultMap.get(key));
			ISuiteResult sr = suiteResultMap.get(suiteName);
			ITestContext sc = sr.getTestContext();
			IResultMap rm = sc.getFailedTests();
			Set<ITestResult> testResultSet = rm.getAllResults();
			try {
				excel.createExcel("Failed-TestSuite_"+suiteFileName+".xls", suiteName);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Some error has occurred while creating the file \"Failed-TestSuite_"+suiteFileName+".xls\"");
				return;
			}
			
			int row = 0;
			excel.updateCellValue(row, 0, "Excel Path");
			excel.updateCellValue(row, 1, "Sheet Name");
			for(ITestResult testResult:testResultSet){
				row++;
				Object[] parameters = testResult.getParameters();
				excel.updateCellValue(row, 0, (String)parameters[0]);
				excel.updateCellValue(row, 1, (String)parameters[1]);
			}
			
		}
		
	}
*/

	@Override
	public void onFinish(ISuite suite) {

	    suite2=suite;
		String suiteFileName = suite2.getName();
		tempExcel=new ExcelProcessor();
		String failedPath=System.getProperty("resourcesFolder")+"/Failed-TestSuite_"+suiteFileName+".xls";
		String skippedPath=System.getProperty("resourcesFolder")+"/Skipped-TestSuite_"+suiteFileName+".xls";
		classTestCaseMappingPath="./"+suiteFileName+ "_Classes vs Testcases Mapping.xlsx";
			    //Delete failed and skipped test suit files if already exists
			if(new File(failedPath).exists())
			{
				new File(failedPath).delete();
			}
			if(new File(skippedPath).exists())
			{
				new File(skippedPath).delete();
			}
				try {
				excel.createExcel(failedPath,"Sheet1");
				excel.createExcel(skippedPath,"Sheet1");
					} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	
		ExcelProcessor finalFailedSheet=null;
		ExcelProcessor finalSkippedSheet=null;
	
		 Set<String> keyset = TestSuite.failedTestCases.keySet();
		 int failedFlag=0;
		 int skippedFlag=0;
		 int rowCount;
		 //Logic to iterate through the failed test cases map and write it to the excel
		 for (String key : keyset){
		if(failedFlag==0){
			 try {
					finalFailedSheet=new ExcelProcessor(failedPath,"Sheet1");
				} catch (ExcelConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 finalFailedSheet.createSheet(key);
			 failedFlag=1;
		}
		else{
			 finalFailedSheet.createSheet(key);
		}

			 try {
					finalFailedSheet=new ExcelProcessor(failedPath,key);
				} catch (ExcelConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 rowCount=finalFailedSheet.rowCount;
			 finalFailedSheet.updateCellValue(0, 0, "Excel Path");
			 finalFailedSheet.updateCellValue(0, 1, "TestCases"); 
				 List<String> objArr = TestSuite.failedTestCases.get(key);
				 for (Object obj : objArr) {
					 String[] excelpathplussheetname=obj.toString().split("#");
					 String excelpath=excelpathplussheetname[0];
				     String sheetName=excelpathplussheetname[1];
				     finalFailedSheet.updateCellValue(++rowCount, 0, excelpath);
				     finalFailedSheet.updateCellValue(rowCount, 1, sheetName); 
	                    }
	
	
		 }
	     
		 
		 //Logic to write skipped test cases map into excel
		 Set<String> skippedKeyset = TestSuite.skippedTestCases.keySet();
		 //Logic to iterate through the failed test cases map and write it to the excel
		 for (String key : skippedKeyset){
		if(skippedFlag==0){
			 try {
					finalSkippedSheet	=new ExcelProcessor(skippedPath,"Sheet1");
				} catch (ExcelConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 finalSkippedSheet.createSheet(key);
			 skippedFlag=1;
		}
		else{
			finalSkippedSheet.createSheet(key);
		}

			 try {
				 finalSkippedSheet	=new ExcelProcessor(skippedPath,key);
				} catch (ExcelConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 rowCount=finalSkippedSheet.rowCount;
			 finalSkippedSheet.updateCellValue(0, 0, "Excel Path");
			 finalSkippedSheet.updateCellValue(0, 1, "TestCases"); 
				 List<String> objArr = TestSuite.skippedTestCases.get(key);
				 for (Object obj : objArr) {
					
					 String[] excelpathplussheetname=obj.toString().split("#");
					 String excelpath=excelpathplussheetname[0];
				     String sheetName=excelpathplussheetname[1];
				     finalSkippedSheet.updateCellValue(++rowCount, 0, excelpath);
				     finalSkippedSheet.updateCellValue(rowCount, 1, sheetName); 
	                    }
	
	
		 }
		 //delete the default sheets present in the failed and skipped test suites i.e Sheet1
		 if(failedFlag==1)
		 {
			 finalFailedSheet.deleteSheet("Sheet1");
		 }
		 if(skippedFlag==1)
		 {
			 finalSkippedSheet.deleteSheet("Sheet1");
		 }
		 //Write classes Vs test cases mapping result in excel file
		 if(new File(classTestCaseMappingPath).exists())
			{
				new File(classTestCaseMappingPath).delete();
			}
			try {
				excel.createExcel(classTestCaseMappingPath,"Mapping");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ExcelProcessor excel_mapping_final=null;
			try {
				excel_mapping_final	=new ExcelProcessor(classTestCaseMappingPath,"Mapping");
			} catch (ExcelConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			excel_mapping_final.updateCellValue(0, 0, "Package Name.classname");
			excel_mapping_final.updateCellValue(0, 1, "TestCases");
			
			
			excel_mapping_final.writeTOExcelFromMap(ClassesVsTestCasesMapping.class_testcase,classTestCaseMappingPath, "Mapping");
			
			/****************************************Code Added for performance testing purpose**********************************************************/
			if(System.getProperty("performanceFlag").equalsIgnoreCase("true"))
			{
			HashMap <String,ArrayList<timeLog>> testMap1=TestSuite.testMap;
			 PerfXMLResult p=new PerfXMLResult();	
				
				 for(Map.Entry<String, ArrayList<timeLog>> entry:testMap1.entrySet()){    
				        String key=entry.getKey();  
				        ArrayList<timeLog> al=entry.getValue();  
				        Iterator<timeLog> itr=al.iterator();  
				       
				        System.out.println("\n"+key);
				        while(itr.hasNext()){  
				        	timeLog st=(timeLog)itr.next();  
				        	
				            System.out.println(st.getOperation()+" "+st.getStartTime()+" "+st.getEndTime()+" "+st.getDiff()+"\n");  
				          }  
				        
				    }    
				
				
				 try {
					p.createReport();
					p.createHTMLReport();
				} 
				 catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			}
		
	}

	@Override
	public void onStart(ISuite arg0) {
		
	}
	
	
}
