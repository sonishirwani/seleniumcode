package com.eqtechnologic.framework;
/**
 * This class provides method to select option from Drop down menu.
 */
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class Selection {
	/**
	 * This method finds the drop down menu using getLocator method
	 * and selects value from it.
	 * @param locatorMethod
	 * @param locatorValue
	 * @param parameters
	 */
	public static void selectByText(String locatorMethod, String locatorValue, String parameter, WebDriver driver,Logger logger){
		logger.info("Selecting: "+ parameter+" " +
				"from dropdown identified by: "+locatorValue+" using locator method: "+locatorMethod);
		By by = Locator.getLocator(locatorMethod, locatorValue,logger);
		WebElement  visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);
		if(visibleElement == null && by != null){
			visibleElement = driver.findElement(by);
		}
		Select select = new Select(visibleElement);
		select.selectByVisibleText(parameter);		
	}

}
