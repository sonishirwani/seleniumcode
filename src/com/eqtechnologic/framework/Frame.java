package com.eqtechnologic.framework;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Frame {
	/**
	 * This method switches to single frame based on
	 * input provided from user which may be default or Popup or frame.
	 * @param locatorMethod
	 * @param locatorValue
	 */
	public static void switchTo(String locatorMethod,String locatorValue, WebDriver driver,Logger logger){
		
		WebDriverWait wait = new WebDriverWait(driver,20);
		if(locatorMethod == null){
			if(locatorValue.equalsIgnoreCase("default")){
				logger.info("Switching to default frame.");
				driver.switchTo().defaultContent();
			}
			else if(locatorValue.equalsIgnoreCase("Popup"))
				switchToModalPopup(driver,logger);	
			else{
				logger.info("Switching to frame: "+locatorValue+" using: id/name");
				wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locatorValue));
			}
			
		}
		else{
				logger.info("Switching to frame: "+locatorValue+" using: "+locatorMethod);
				By by = Locator.getLocator(locatorMethod, locatorValue,logger);
				wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));
		}
		
	}
	/**
	 * This method takes list of frames as input
	 * and switches to the frames as per provided in the list.
	 * @param locatorMethod
	 * @param locatorValue
	 */
	public static void switchToMany(String locatorValue, WebDriver driver,Logger logger){
		WebDriverWait wait = new WebDriverWait(driver,20);
		
		String[] values = locatorValue.split(System.getProperty("delimiter"));
				
		/* Switching to default frame first and then going into inner frames.*/
		logger.info("Switching to default frame.");
		driver.switchTo().defaultContent();
		for(int i = 0;i<values.length;i++){
			logger.info("Switching to frame: "+values[i]+" using: id/name");
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(values[i].trim()));
		}
	}
/**
 * This method is specific to eQubeBI.
 * It is used to handle frame whose id is generated runtime and also when multiple such frames are available at same instance. 
 * @param driver
 */
	private static void switchToModalPopup(WebDriver driver,Logger logger){
		driver.switchTo().defaultContent();
	    List<WebElement>element = driver.findElements(By.xpath("//iframe[contains(@id,'modalWindowPopup')]"));	             
	    List<WebElement>list1 = new ArrayList<WebElement>();
	    WebDriverWait wait = new WebDriverWait(driver,20);
	    for(WebElement e : element){
	        try{
	        	if(e.isDisplayed()){
	            list1.add(e);
	        	}	        	
	        }catch (Exception e1){
	        	// catching the exception for StaleElementException which are stale now.
	        }
	     }
	    
	     String id = list1.get(list1.size()-1).getAttribute("id");
	     By by = Locator.getLocator("id", id,logger);
	     wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by)); //new code
	     //driver.switchTo().frame(list1.get(list1.size()-1)); //old code
	     logger.info("Switching to modalpopup: "+ id);
	     list1.clear();
	 }
	
}
