package com.eqtechnologic.framework;
/*
 * This class provides method to take screenshot of current instance of application.
 */
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class Screenshot {
	
	/*
	 * This method reads the name of base directory from property file and saves screenshot of application
	 * in folder with hierarchy
	 * Base Directory > Test suite Name > Test case name > Snapshot with naming rule as TestcaseName_excelrownumber.
	 */
	public static void takeScreenshot(String testCaseName, String step,String sleepTime, WebDriver driver,Logger logger){
		
		/*if(!Driver.isSessionAvailable())
			return;*/
		String alertMessage=null;
		Throwable alertException=null;
		try{
			driver.getTitle();
		}catch(UnhandledAlertException e){
			alertMessage = driver.switchTo().alert().getText();
			alertException = e;
			driver.switchTo().alert().accept();
		}
		if(sleepTime!= null){
			try {
				Thread.sleep(Integer.parseInt(sleepTime));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		String testsuitename = System.getProperty("suiteName");
		String path = System.getProperty("snapshotBaseDirectory")+"/"+testsuitename+"/"+testCaseName+"/"+testCaseName+"_"+step+".jpg";
		logger.info("Screenshot "+testCaseName+"_"+step+".jpg "+"being saved at location : "+path);
		File scrFile = null;		
	   	try {
	   		
	   		try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   		scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	   	if(alertMessage!=null){
	   		Assert.fail(alertMessage,alertException);
	   	}
	}
}
