package com.eqtechnologic.framework;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ExecuteScript {

	private enum Keywords {
		CLICK,TEXTAREA;
	}

	public static void actionPerform(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger){
		String[] methods = locatorMethod.split(System.getProperty("delimiter"));
		String values = locatorValue;
		String actions = parameters;

	//	for(int i = 0;i<methods.length;i++){
	//		logger.info("Performing action: "+actions[i]+" on element identified by: "+values[i]+" using locator method: "+methods[i]);
	//		keywordProcess(methods[i],values[i],actions,driver,logger);
	//	}
		
		keywordProcess(methods,values,actions,driver,logger);
		
		
	}

	public static void forClick(String locatorMethod,String locatorValue, WebDriver driver,Logger logger){
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		logger.info("***_Executing Java Script for Click_***");
		jse.executeScript("arguments[0].click();",getWebElement(driver, locatorValue, locatorMethod, logger));
	}

	public static void textArea(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger){
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		logger.info("***_Executing Java Script for Putting Big text in Textarea_***");
		jse.executeScript("arguments[0].value=arguments[1];",getWebElement(driver, locatorValue, locatorMethod, logger),parameters);
	}

	private static WebElement getWebElement(WebDriver driver, String locatorValue, String locatorMethod, Logger logger) {
		try {
			if(locatorMethod.equalsIgnoreCase("ID")){
				return driver.findElement(By.id(locatorValue));
			}

			else if(locatorMethod.equalsIgnoreCase("XPATH")){
				return driver.findElement(By.xpath(locatorValue));
			}
			else if(locatorMethod.equalsIgnoreCase("CSSSELECTOR")){

				return driver.findElement(By.cssSelector(locatorValue));
			}
			else if (locatorMethod.equalsIgnoreCase("NAME")){

				return driver.findElement(By.name(locatorValue));
			}
		}
		catch(NoSuchElementException e){
			logger.error("Invalid Locator Method");
			throw e;
		}
		return null; 


		//return null;
	}

	public static void keywordProcess(String [] locatorMethod,String locatorValue,String actions, WebDriver driver,Logger logger){

		//Keywords keyword = Keywords.valueOf(parameter.trim().toUpperCase());
		//     Actions builder=new Actions(driver);
	//	actions[0]=actions[0].trim().toUpperCase();
		locatorMethod[1]=locatorMethod[1].trim().toUpperCase();
		Keywords keyword= null;
		try{
			keyword = Keywords.valueOf(locatorMethod[1]);
		}catch(IllegalArgumentException e){
			logger.fatal("Parameter for Perform Action : "+actions+" is not identified.");
			throw e;
		}

		By by = Locator.getLocator(locatorMethod[0], locatorValue,logger);
		WebElement  visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);
		if(visibleElement == null && by != null){
			visibleElement = driver.findElement(by);
		}
		switch(keyword){
		case CLICK : forClick(locatorMethod[0], locatorValue, driver, logger); break;
		case TEXTAREA : textArea(locatorMethod[0], locatorValue, actions, driver, logger);

		default: ;
		}

	} 
}



