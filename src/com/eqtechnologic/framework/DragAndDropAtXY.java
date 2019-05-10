package com.eqtechnologic.framework;

/* This class is written to do drag and drop at any specified location*/
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class DragAndDropAtXY {

	/* This method calculates the exact pixel position relative from the drop area 
	 * when the drag element would be dropped.
	 */
	public static void perform(String locatorMethod,String locatorValue, String parameters, WebDriver driver,Logger logger){
		
		Double x,y;
		String delay = System.getProperty("delayBetweenDragAndDropInMs");
		String[] methods = locatorMethod.split(System.getProperty("delimiter"));
		String[] values = locatorValue.split(System.getProperty("delimiter"));
		String[] coordinates = parameters.split(System.getProperty("delimiter"));
		
		if(coordinates.length ==3)
			delay = coordinates[2];
		
		if(methods.length!=2 && values.length!=2 && coordinates.length!=2){
			logger.fatal("Arguments of one of the column is incorrect. At least two arguments are required!");
			throw new IllegalArgumentException("Length of one of the column is incorrect. At least two arguments are required!");
		}
		
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
		
		Dimension dropDim = dropElement.getSize();
		try{
			x = dropDim.width * Double.parseDouble(coordinates[0].trim())/100;
			y = dropDim.height * Double.parseDouble(coordinates[1].trim())/100;	
		}catch(NumberFormatException nfe){
			logger.fatal("Can not be parsed: "+coordinates[0].trim()+" "+coordinates[1].trim());
			throw nfe;
		}
		
		Actions builder = new Actions(driver);
		logger.info("Dragging element:  \""+values[0]+"\" using locator method: \""+methods[0] +
					"\" to droppable element: \""+values[1]+"\" using locator method: \""+methods[1]+
					"\" to position (x,y): ("+x.intValue()+","+y.intValue()+")");
		
		/*builder.clickAndHold(dragElement).moveToElement(dropElement, x.intValue(), y.intValue())
										 .moveToElement(dropElement, x.intValue() +2 , y.intValue()+2)
										 .release().perform();*/
		builder.clickAndHold(dragElement).moveToElement(dropElement, x.intValue(), y.intValue()).perform();
		Delay.delay("6000", logger);
		builder.moveToElement(dropElement, x.intValue() +2 , y.intValue()+2).release().perform();				
	}
}
