package com.eqtechnologic.framework;



/**
 * This class provides method to enter text in Text box.
 */
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;



import com.eqtechnologic.util.ExcelProcessor;

public class Textbox {
	/**
	 * This method finds the text box using getLocator method
	 * and sends text to the text box.
	 * @param locatorMethod
	 * @param locatorValue
	 * @param parameters
	 */
	public static void clearAndType(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger){
		//parameters=parameters.toString();
		logger.info("Entering text: "+parameters+" into textbox identified by: "
				+locatorValue+" using locator method: "+locatorMethod);
		By by = Locator.getLocator(locatorMethod, locatorValue,logger);
		
		WebElement  visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);
		if(visibleElement == null && by != null){
			visibleElement = driver.findElement(by);
		}
	
		visibleElement.sendKeys(Keys.HOME,Keys.chord(Keys.SHIFT,Keys.END),parameters);
		
		//earlier code commented as now there is no issue in detection of textbox
		
//		String writtenText = null;
//		boolean inputBox = true;
//		writtenText =visibleElement.getAttribute("value");
//		
//		if(writtenText == null){
//			inputBox = false;
//			writtenText = visibleElement.getText();
//		}
//		
//		int retryCount = 5;
//		while(!writtenText.equals(parameters) && retryCount >=1 ){
//			System.out.println("writtenText = "+writtenText);
//	
//			visibleElement.sendKeys(Keys.HOME,Keys.chord(Keys.SHIFT,Keys.END),parameters);
//			if(inputBox)
//				writtenText = visibleElement.getAttribute("value");
//			else
//				writtenText = visibleElement.getText();
//			retryCount--;
//		}
//		
//		if(retryCount == 0)
//			logger.error("Provided text and entered text didn't match!! \nText in parameter = \""+parameters+"\" while entered text is =\""+writtenText+"\"");
	
		
		    /* robot loimplementation
		By by = Locator.getLocator(locatorMethod, locatorValue,logger);
		     WebElement element = driver.findElement(by);
		    Actions navigator = new Actions(driver);
		    navigator.click(element)
		        .sendKeys(Keys.END)
		        .keyDown(Keys.SHIFT)
		        .sendKeys(Keys.HOME)
		        .keyUp(Keys.SHIFT)
		        .sendKeys(Keys.BACK_SPACE)
		        .sendKeys(parameters)
		        .perform();*/
		
	}
	public static void DATA_EXPORTS(String locatorMethod, String locatorValue,String S2,String parameters,String step,String testCaseName,WebDriver driver,Logger logger) throws AWTException, InterruptedException, IOException{
		/*************************************************line added by suhel***************************************************/
		String[] methods=locatorMethod.split(";");
		String[] values= locatorValue.split(";");
		By by=null;
		
		/***********************************************************************************************************************/
		String Str1 = System.getProperty("user.home");
		System.out.println(Str1);
		String DownloadFolderPath = Str1+"\\Downloads";
		System.out.println(DownloadFolderPath);
		
		//finding Downloads folder path for copying downloaded excels into another folder
		
		//driver.findElement(By.id("share-link-group")).click();
		//clicking on Share option of BNP
		
		//Code for set flag
		 String setflag=System.getProperty("setstepnumber");
		 Thread.sleep(1000);
		 //driver.findElement(By.id("export-to-excel")).click();
		 /*by = Locator.getLocator(methods[0], values[0], logger);
		driver.findElement(by).click();
		//Clicking on Export to Excel Option of BNP
		 Thread.sleep(5000);
		//Clicking on Export option of Export to excel popup
		//driver.findElement(By.cssSelector(".k-button.eq-extwindow-buttonset-button.js-exportToExcel.eq-widget")).click();
		 by = Locator.getLocator(methods[1], values[1], logger);
			driver.findElement(by).click();
		 Thread.sleep(6000);*/

		 /*************************************/
		  for(int i=0;i<methods.length;i++){
			  Thread.sleep(5000);
			  by = Locator.getLocator(methods[i], values[i], logger);
				driver.findElement(by).click();
			 Thread.sleep(6000);
		  }
		 
		 
		String testsuitename = System.getProperty("suiteName");
		//Suite Name is getting stored into testsuitename
		logger.info("Suite Name "+ testsuitename);
		String path ="Excels" +"/"+  testsuitename+"/"+testCaseName;
		File f=new File(path);
		 f.mkdirs();
		 //Folder created - Excels, TestSuitename and TestCase Name
		 //Thread.sleep(3000);
		 logger.info("Path is" + path);
		 String Path2 = testsuitename+"/"+testCaseName;
		 String replac=Path2.replace("/", "\\");
		 logger.info("Path is" + replac);
		 File F = new File(S2);

	/********Code added For FlagSet to export the multiple excels ***********************/
				 
		 String path1=null;
		 
		 if(setflag.equalsIgnoreCase("false")){
		 path1 = F.getAbsolutePath() +"\\"+ replac+"\\" + testCaseName +".xls";
		 }else{
			 path1 = F.getAbsolutePath() +"\\"+ replac+"\\" + testCaseName + step +".xls"; 
		 }
	/***********************************************************************************/
		// Above line stores the complete path for downloading the excel

		 File lastModifiedFile=getLatestFilefromDir(DownloadFolderPath);
		 lastModifiedFile.renameTo(new File(path1));
		 
		 //Above line copy downloaded excel file into Excel folder
		 Thread.sleep(3000);
		 if(setflag.equalsIgnoreCase("false")){
		 //driver.findElement(By.id("app-logout")).click();
		 }
		}
	
/***********************Keyword added For TC Report Export To Excel*****************************************************/
	
	public static void TC_EXPORTS_LOGOUT(String S2,String parameters,String step,String testCaseName,WebDriver driver,Logger logger) throws AWTException, InterruptedException, IOException{
		
		String Str1 = System.getProperty("user.home");
		System.out.println(Str1);
		String DownloadFolderPath = Str1+"\\Downloads";
		System.out.println(DownloadFolderPath);
		
		//finding Downloads folder path for copying downloaded excels into another folder
		
		
		driver.findElement(By.id("share-link-group")).click();
		//clicking on Share option of BNP
		
		//Code for set flag
		 String setflag=System.getProperty("setstepnumber");
		
		 Thread.sleep(1000);
		driver.findElement(By.id("export-to-excel")).click();
		//Clicking on Export to Excel Option of BNP
		Thread.sleep(5000);
		 driver.findElement(By.cssSelector(".k-button.eq-extwindow-buttonset-button.js-exportToExcel.eq-widget")).click();
		 Thread.sleep(6000);
			String testsuitename = System.getProperty("suiteName");
			logger.info("Suite Name "+ testsuitename);
			String path ="Excels" +"/"+  testsuitename+"/"+testCaseName;
			File f=new File(path);
			 f.mkdirs();
			 //Thread.sleep(3000);
			 logger.info("Path is" + path);

			 String Path2 = testsuitename+"/"+testCaseName;
			 String replac=Path2.replace("/", "\\");
			 String path1=null;
			 logger.info("Path is" + replac);
				File F = new File(S2);
				if(setflag.equalsIgnoreCase("false")){
					path1 = F.getAbsolutePath() +"\\"+ replac+"\\" + testCaseName +".xls";
				}else{
					path1 = F.getAbsolutePath() +"\\"+ replac+"\\" + testCaseName + step +".xls";
				}

				 File lastModifiedFile=getLatestFilefromDir(DownloadFolderPath);
				 lastModifiedFile.renameTo(new File(path1));
				
				Thread.sleep(3000);
			 if(setflag.equalsIgnoreCase("false")){
				 driver.findElement(By.id("app-logout")).click();
			 }
			
}
	
	/**************************************************************************************************************************/
	/********Code added For copying downloaded excel file into Excels folder ***********************/
	
	private static File getLatestFilefromDir(String dirPath) throws InterruptedException{
		    File dir = new File(dirPath);
		    File[] files = dir.listFiles();
		    if (files == null || files.length == 0) {
		        return null;
		    }

		    File lastModifiedFile = files[0];
		    for (int i = 1; i < files.length; i++) {
		       if (lastModifiedFile.lastModified() < files[i].lastModified()) {
		           lastModifiedFile = files[i];
		       }
		       while(lastModifiedFile.length()==0){
		  		 Thread.sleep(3000);
		  	 }
		    }
		    /*while(lastModifiedFile.isFile()== false){
		    Thread.sleep(3000);
		    }*/
		    return lastModifiedFile;
		}	
	
	
	public static boolean isAlertPresent(WebDriver driver) {
		 
		  boolean presentFlag = false;
		 
		  try {
		 
		   // Check the presence of Dataset alert
		   Alert alert = driver.switchTo().alert();
		   // Alert present; set the flag
		   presentFlag = true;
		   // if present consume the alert
		   //alert.accept();
		 
		  } catch (NoAlertPresentException ex) {
		   // Alert not present
		   //ex.printStackTrace();
		  }
		 
		  return presentFlag;
		 
		 }
	
	public static void numericbox(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger){
		{
			By by = Locator.getLocator(locatorMethod, locatorValue,logger);
			WebElement  visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);
			if(visibleElement == null && by != null){
				visibleElement = driver.findElement(by);
				visibleElement.sendKeys(parameters);
			}
			    
			else
			{
				//WebElement element = driver.findElement(by);
			    Actions navigator = new Actions(driver);
			    navigator.click(visibleElement)
			        .sendKeys(Keys.END)
			        .keyDown(Keys.SHIFT)
			        .sendKeys(Keys.HOME)
			        .keyUp(Keys.SHIFT)
			        .sendKeys(Keys.BACK_SPACE)
			        .sendKeys(parameters)
			        .perform();
			}
		}
	}
	
	public static void upload(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger) throws AWTException, InterruptedException, IOException{
		logger.info("Entering path: "+parameters+" into upload directory identified by: "
				+locatorValue+" using locator method: "+locatorMethod);
		if(locatorMethod==null && locatorValue==null)
		{
			upload1(null,null,parameters,logger);
		}
		else
		{
		By by = Locator.getLocator(locatorMethod,locatorValue,logger);
		WebElement uploadElement = driver.findElement(by);
		if(!uploadElement.isDisplayed())
			((JavascriptExecutor)driver).executeScript("" +
					"arguments[0].style.visibility = 'visible';" +
					" arguments[0].style.height = '1px';" +
					" arguments[0].style.width = '1px';" +
					" arguments[0].style.opacity = 1", uploadElement);
		
		if(driver.getClass() == RemoteWebDriver.class){
			RemoteWebDriver rwd = (RemoteWebDriver)driver;
			rwd.setFileDetector(new LocalFileDetector());
		}
		//((Textbox) driver.findElement(by)).upload1(parameters);
		//if(locatorMethod!=null && locatorValue!=null)
		//{
			driver.findElement(by).sendKeys(parameters);
		//}
		/*else
		{
		upload1("NA","NA",parameters,logger);
		}*/
		}
	}
	
	public static void upload1(String locatorMethod,String locatorValue,String parameters,Logger logger) throws AWTException, InterruptedException, IOException
	{
		if(locatorMethod==null && locatorValue==null)
		{
		Thread.sleep(2000);
		logger.info("Entering path: "+parameters+" into upload directory");
		setclipboarddata(parameters);
		System.out.println("parameters is"+parameters);
		Thread.sleep(2000);
	    //Runtime.getRuntime().exec("D:\\Download.exe");
		Robot robot=new Robot();
		Thread.sleep(2000);
		robot.keyPress(KeyEvent.VK_CONTROL);
		Thread.sleep(2000);
		robot.keyPress(KeyEvent.VK_V);
		Thread.sleep(2000);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		Thread.sleep(2000);
		robot.keyRelease(KeyEvent.VK_V);
		Thread.sleep(2000);
		//Runtime.getRuntime().exec("D://Download.exe");
		
		
		/*String jacobDllVersionToUse;
		jacobDllVersionToUse = "jacob-1.18-x64.dll";
		File file = new File("lib", jacobDllVersionToUse);
		System.setProperty(LibraryLoader.JACOB_DLL_PATH, file.getAbsolutePath());*/

		/*AutoItX a = new AutoItX();
		//a.run("D://Download.exe");
	/*	a.controlFocus("Save As","","Button1");
		a.controlClick("Save As","","Button1");
		a.controlFocus("Save As","","DirectUIHWND1");
		a.controlClick("Save As","","Button1");*/
	

		
		robot.keyPress(KeyEvent.VK_ENTER);
		Thread.sleep(2000);
		robot.keyRelease(KeyEvent.VK_ENTER);
		Thread.sleep(2000);
		}
		
	}
	
	// New method added to upload all files from given path
	public static void uploadAllFromPath(String locatorMethod,String locatorValue,String parameters,Logger logger) throws AWTException, InterruptedException, IOException
	{
		if(locatorMethod==null && locatorValue==null)
		{
		Thread.sleep(2000);
		logger.info("Entering path: "+parameters+" into upload directory");
		setclipboarddata(parameters);
		System.out.println("parameters is"+parameters);
		Thread.sleep(2000);
	    //Runtime.getRuntime().exec("D:\\Download.exe");
		Robot robot=new Robot();
		Thread.sleep(2000);
	
		
		
		
	for (int i = 0; i < 4; i++) {
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(500);
		robot.keyRelease(KeyEvent.VK_TAB);
		Thread.sleep(500);
			
	}
			
	robot.keyPress(KeyEvent.VK_ENTER);
	Thread.sleep(500);
	robot.keyRelease(KeyEvent.VK_ENTER);
	Thread.sleep(500);
	
	
	robot.keyPress(KeyEvent.VK_CONTROL);
	Thread.sleep(500);
	robot.keyPress(KeyEvent.VK_V);
	Thread.sleep(500);
	
	robot.keyRelease(KeyEvent.VK_CONTROL);
	Thread.sleep(500);
	robot.keyRelease(KeyEvent.VK_V);
	Thread.sleep(500);
	
	robot.keyPress(KeyEvent.VK_ENTER);
	Thread.sleep(500);
	robot.keyRelease(KeyEvent.VK_ENTER);
	Thread.sleep(500);
	
	for (int i = 0; i < 3; i++) {
		robot.keyPress(KeyEvent.VK_TAB);
		Thread.sleep(500);
		robot.keyRelease(KeyEvent.VK_TAB);
		Thread.sleep(500);
	
	}	
	
	    robot.keyPress(KeyEvent.VK_CONTROL);
	    Thread.sleep(500);
	    robot.keyPress(KeyEvent.VK_A);
	    Thread.sleep(500);
	
     	robot.keyRelease(KeyEvent.VK_CONTROL);
	    Thread.sleep(500);
	    robot.keyRelease(KeyEvent.VK_A);
	    Thread.sleep(500);
		
		robot.keyPress(KeyEvent.VK_ENTER);
		Thread.sleep(500);
		robot.keyRelease(KeyEvent.VK_ENTER);
		Thread.sleep(500);
	
		}
		
	}
	
	public static String getText(String locatorMethod,String locatorValue,WebDriver driver,Logger logger)
	{
		By by = Locator.getLocator(locatorMethod, locatorValue,logger);
		String text=null;
		WebElement  visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);

			visibleElement = driver.findElement(by);
			text=visibleElement.getText();

		System.out.println("Text captured is"+text);
		  return text;
	}
	
	
	public
	static String jvmBitVersion(){
	 return System.getProperty("sun.arch.data.model");
	}
	public static void setclipboarddata(String string1){
		StringSelection stringselection=new StringSelection(string1);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, null);
	}
	
	
}
