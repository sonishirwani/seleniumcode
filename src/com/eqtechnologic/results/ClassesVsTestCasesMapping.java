package com.eqtechnologic.results;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.eqtechnologic.exceptions.ExcelConnectionException;
import com.eqtechnologic.util.CallAntTask;
import com.eqtechnologic.util.ExcelProcessor;
import com.eqtechnologic.util.csvToxlsConverter;

public class ClassesVsTestCasesMapping {
	
	public static Map<String, ArrayList<String>> class_testcase=new HashMap <String, ArrayList<String>>();
	
	public void createMappingExcel(Object[] parameters,String suiteName,String testCase)throws IOException
	{
		/*
		 * Test case wise jacoco logic starts here
		 * Logic for Jacoco coverage per test case
		 * creating exec file for the executed test case
		 */
	
	
		ExcelProcessor excel_mapping=null;
		int noOfRows;
		int delay=28000;
		String jacocoPath=System.getProperty("jacocoPath");
		String buildFilePath=System.getProperty("buildFilePath");
		String remoteFilePath=System.getProperty("remoteFilePath");
		
	    String execPath=jacocoPath+"/"+"jacoco.exec";
		File  destDir;
		File execFile;
		if(buildFilePath.contains("//"))
		{
			
			CallAntTask.executeAntTask(remoteFilePath,"remoteDump");
			
			//Create testsuite->testcase name folder and dump html file into the folder
			destDir= new File("./Jacoco Test Coverage/"+suiteName+"/" + testCase);
	        execFile=new File(execPath);
			FileUtils.copyFileToDirectory(execFile, destDir);
			CallAntTask.executeAntTask(remoteFilePath,null);
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		else
		{
		
	   CallAntTask.executeAntTask( buildFilePath, "remoteDump");
		
	
		//Call to ant to generate the html after the execution of remote dump target
		
		CallAntTask.executeAntTask(buildFilePath,null);
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
		//Create testsuite->testcase name folder and dump html file into the folder
		destDir= new File("./Jacoco Test Coverage/"+suiteName+"/" + testCase);
        execFile=new File(execPath);
		FileUtils.copyFileToDirectory(execFile, destDir);
		
	
		}
		
		
		//copy the generated excel report to the location same as exec file
		String csvPath=jacocoPath+"/test/target/site/report.csv";
		String xlsPath="./Jacoco Test Coverage/"+suiteName+"/" + testCase+ "/"+ testCase + ".xls";
		File srcFilePath=new File(csvPath);
		destDir=new File("./Jacoco Test Coverage/"+suiteName+"/" + testCase);
		FileUtils.copyFileToDirectory(srcFilePath, destDir);
		String newcsvPath="./Jacoco Test Coverage/"+suiteName+"/" + testCase+"/report.csv";
	
		 File inputFile = new File(newcsvPath);
         //File outputFile = new File(xlsPath);
         csvToxlsConverter.xls(inputFile, xlsPath,testCase);
         //Logic to write class name and corresponding test cases in an excel file
         
         //Step -3: Define logical Map to store class names and corresponding test cases
       
        // class_testcase = new HashMap<String, ArrayList<String>>(); //create a map and define data
         
         String coverage1;
         int coverage=0;
         
         try {
		excel_mapping=new ExcelProcessor(xlsPath,testCase);
		} catch (ExcelConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                 noOfRows=excel_mapping.rowCount;
                 for(int row=1;row<=noOfRows;row++){
                	 coverage1=excel_mapping.getCellValue(row, 3);
                	 if(!coverage1.equals("LINE_COVERED") ){
                		 coverage=Integer.parseInt(excel_mapping.getCellValue(row, 3).toString());
                	 }
                	
                	 if(coverage > 0 ){
                		  String mapKey = excel_mapping.getCellValue(row, 1)+"."+excel_mapping.getCellValue(row, 2);
						ArrayList<String> testCases = class_testcase.get(mapKey);
						if(testCases!=null){
                			 testCases.add(testCase);
                		 }
                		 else{
                			 testCases = new ArrayList<String>();
                			 testCases.add(testCase);
                    		 class_testcase.put(mapKey, testCases);
                		 }
                		
                	 }
                	
                 }
	}

}
