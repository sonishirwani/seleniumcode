package com.eqtechnologic.framework;
/**
 * This class provides method that returns a By locator.
 */
import org.apache.log4j.Logger;
import org.openqa.selenium.By;

public class Locator {
	
	/**
	 * Enum that contains all the locator methods which are used in webdriver
	 * to identify an element
	 */
	private enum LocatorMethod{
		CSSSELECTOR,CLASSNAME,ID,LINKTEXT,NAME,PARTIALLINKTEXT,TAGNAME,XPATH;
	}
	
	/**
	 * This method takes the locator method and locator value as its input
	 * to identify which locator method is to be used to create a By locator to 
	 * identify the webElement.
	 * @param locatorMethod
	 * @param locatorValue
	 * @return
	 */
	public static By getLocator(String locatorMethod, String locatorValue,Logger logger){
		
		//trimming the leading and trailing white spaces
		locatorMethod = locatorMethod.trim().toUpperCase();
		locatorValue = locatorValue.trim();
		if(locatorMethod.equalsIgnoreCase("NA"))
		return null;
		
		LocatorMethod method= null;
		try{
			method = LocatorMethod.valueOf(locatorMethod);
		}catch(IllegalArgumentException e){
			logger.fatal("Locator Method : "+locatorMethod+" is not identified.");
			throw e;
		}
		//LocatorMethod method = LocatorMethod.valueOf(locatorMethod);
		switch(method){
			case CSSSELECTOR: return By.cssSelector(locatorValue);
			case CLASSNAME: return By.className(locatorValue);
			case ID: return By.id(locatorValue);
			case LINKTEXT: return By.linkText(locatorValue);
			case NAME: return By.name(locatorValue);
			case PARTIALLINKTEXT: return By.partialLinkText(locatorValue);
			case TAGNAME: return By.tagName(locatorValue);
			case XPATH: return By.xpath(locatorValue);
		}return null;
	}
	
}
