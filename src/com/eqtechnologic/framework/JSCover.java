package com.eqtechnologic.framework;
/*
 * This class provides method to take JSON per test case
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class JSCover {
	
	/*
	 * This method reads the name of base directory from property file and saves JSON for each test case
	 * in folder with hierarchy
	 * Base Directory > Test suite Name > Test case name > JSON with naming rule as jscoverage.json
	 */
	public static void takeJson(String testCaseName, WebDriver driver,Logger logger){
		
		BufferedWriter bw = null;
		
		//Get name of suite from config.properties in testsuitename string
		String testsuitename = System.getProperty("suiteName");
		//Set path to Base Directory > Test suite Name > Test case name > jscoverage.json
		String path1 = System.getProperty("JSCoverJson")+"/"+testsuitename+"/"+testCaseName+"/"+"jscoverage.json";
		logger.info("JSON"+testCaseName+"_"+".json "+"being saved at location : "+path1);	
	   	try {
	   		//Store return value of jscoverage_serializeCoverageToJSON() function into string
	   		String js = (String) ((JavascriptExecutor)driver).executeScript("return jscoverage_serializeCoverageToJSON();");
	   		//System.out.println("*************************"+ js);
	   		//System.out.println("*************************"+ path1);
	   		//Create new file at path mentioned in string
	   		File file = new File(path1);
	   		file.mkdirs();
	   		//If json already exists, delete the existing json file and then create new file
	   		if(file.exists())
	   			file.delete();
	   		file.createNewFile();
	   		bw = new BufferedWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()),"UTF-8")));
	   		bw.write(js); 

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    IOUtils.closeQuietly(bw);
		}
	}
}
