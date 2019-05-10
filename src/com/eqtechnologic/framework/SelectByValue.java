package com.eqtechnologic.framework;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class SelectByValue {




	private static String ReplaceXX(String locatorValue, String xxParameters, Logger logger) {
		int i=0;
		String[] xxValues = xxParameters.split(";");

		while(locatorValue.contains("XX") || locatorValue.contains("xx"))
		{
			if(i > xxValues.length)
			{
				logger.fatal("There are some conflicts in XX Parameter values & Locator values. One or More than one XX Parameters values are requires and should be separated by ';'. ");
				throw new NoSuchFieldError();
			}
			if(locatorValue.contains("XX"))
				locatorValue = locatorValue.replaceFirst("XX",xxValues[i]);
			if(locatorValue.contains("xx"))
				locatorValue = locatorValue.replaceFirst("xx",xxValues[i]);
			i++;
		}
		return locatorValue;
	}





	public static void selectValue(String locatorMethod, String locatorValue, String parameter, WebDriver driver,Logger logger){
		logger.info("Selecting: "+ parameter+" " +
				"from dropdown identified by: "+locatorValue+" using locator method: "+locatorMethod);

		String[] methods = locatorMethod.split(System.getProperty("delimiter"));

		String getXpath;
		String getValue = null;
		WebElement webElement;
		Integer i=1;

		do{
			getXpath = ReplaceXX(locatorValue, i.toString(),logger );

			System.out.println("*********Xpath : " + i + getXpath);
			
			By by = Locator.getLocator(methods[0], getXpath, logger);
			webElement=driver.findElement(by);
			getValue = webElement.getText();
			logger.info("****Verifying Value of XPATH  :Actual Value = " + getValue + "**** Expected Value = "+parameter);
			i = i + 1;

		}while (!getValue.equalsIgnoreCase(parameter));	

		logger.info("*********Final matched Xpath : " + getXpath);
		By by = Locator.getLocator(methods[0], getXpath,logger);
		logger.info("*********Final matching text value : " + getValue);
		logger.info("*********Parameter from Excel : " + parameter);

		if(methods[1].equalsIgnoreCase("click")){

			driver.findElement(by).click();
		}
		else if(methods[1].equalsIgnoreCase("doubleclick")) {
			Actions action = new Actions(driver);
			WebElement element=driver.findElement(by);
			action.doubleClick(element).perform();;
		}
	}

	public static void selectLink(String locatorMethod, String locatorValue, String parameter, WebDriver driver,Logger logger){
		logger.info("Selecting link identified by : "+ parameter);
		WebElement webElement  =driver.findElement(By.linkText(parameter));
		webElement.click();
	}
}
