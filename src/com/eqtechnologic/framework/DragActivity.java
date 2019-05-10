package com.eqtechnologic.framework;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class DragActivity {

	public static void dragDropActivity(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger){
		logger.info("Performing Drag and Drop ACtivity Operation");
		
		//This code is for Drag And Drop from a Source Element to Destination Element without any X,Y co-ordinates.
		if(parameters == null || parameters.equals("")){
			
			String[] values = locatorValue.split(System.getProperty("delimiter"));
			String[] methods=locatorMethod.split(System.getProperty("delimiter"));
			
			By dragLocator = Locator.getLocator(methods[0].trim(), values[0].trim(),logger);
			By dropLocator = Locator.getLocator(methods[1].trim(), values[1].trim(),logger);

			WebElement  dragElement = VisibleWebElement.getFirstVisibleElement(dragLocator, driver);
			WebElement  dropElement = VisibleWebElement.getFirstVisibleElement(dropLocator, driver);
			
			if(dragElement == null && dragLocator != null){
				dragElement = driver.findElement(dragLocator);
			}
			if(dropElement == null && dropLocator != null){
				dropElement = driver.findElement(dropLocator);
			}

			Actions builder = new Actions(driver);
			Actions dragAndDrop = builder.dragAndDrop(dragElement, dropElement);
			dragAndDrop.perform();			
		}
		//
		else{
			
			String[] coordinates = parameters.split(System.getProperty("delimiter"));
			By by = Locator.getLocator(locatorMethod, locatorValue,logger);

			WebElement  visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);

			if(visibleElement == null && by != null){
				visibleElement = driver.findElement(by);
			}
			By dragLocator = Locator.getLocator(locatorMethod,locatorValue,logger);

			WebElement  dragElement = VisibleWebElement.getFirstVisibleElement(dragLocator, driver);

			Double xOffSet = Double.parseDouble(coordinates[0].trim());
			Double yOffSet =  Double.parseDouble(coordinates[1].trim());

			Actions builder = new Actions(driver);
			Actions dragAndDrop = builder.dragAndDropBy(dragElement, xOffSet.intValue(),yOffSet.intValue());		
			dragAndDrop.perform();
		}

	}

}
