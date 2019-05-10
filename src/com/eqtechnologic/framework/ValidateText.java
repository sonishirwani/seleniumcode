package com.eqtechnologic.framework;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ValidateText {

	
	public static void compareText(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger){
	
		By by = Locator.getLocator(locatorMethod, locatorValue,logger);
		
		String actualText = driver.findElement(by).getText();
		String expectedText = parameters;
		logger.info("Alert Present ! Actual Text in alert box : "+ actualText);
		System.out.println("*********************************************************");
		System.out.println(" Actual Value = "+actualText+"\n Expected Text = "+expectedText);
		System.out.println("*********************************************************");
		Assert.assertEquals(actualText, expectedText,"Expected text does not match with the given text!");
				
		
	}
	
	
}
