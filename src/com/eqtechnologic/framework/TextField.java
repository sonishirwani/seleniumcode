package com.eqtechnologic.framework;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TextField {

	
	/**
	 * This method finds the text box using getLocator method
	 * and sends text to the text box.
	 * @param locatorMethod
	 * @param locatorValue
	 * @param parameters
	 */
	public static void textFieldType(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger){
		logger.info("Entering text: "+parameters+" into textbox identified by: "
				+locatorValue+" using locator method: "+locatorMethod);
		By by = Locator.getLocator(locatorMethod, locatorValue,logger);
		
		WebElement  visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);
		if(visibleElement == null && by != null){
			visibleElement = driver.findElement(by);
		}
		try {
			visibleElement.clear();
		} catch (InvalidElementStateException e) {
			
		}
		visibleElement.sendKeys(parameters);
		
	}
	
	public static void upload(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger){
		logger.info("Entering path: "+parameters+" into upload directory identified by: "
				+locatorValue+" using locator method: "+locatorMethod);
		By by = Locator.getLocator(locatorMethod, locatorValue,logger);
		driver.findElement(by).sendKeys(parameters);		
	}
	
	
}
