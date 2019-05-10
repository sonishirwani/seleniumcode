package com.eqtechnologic.framework;
/* This class takes the By locator object and sends the first element which is visible in the DOM*/
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class VisibleWebElement {

	/* This method takes the By locator object and sends the first element which is visible in the DOM*/
	public static WebElement getFirstVisibleElement(By by, WebDriver driver){
		
		if(by == null)
			return null;
		List<WebElement> elements = driver.findElements(by);
		
		for(WebElement element:elements){
			if(element.isDisplayed())
				return element;
		}
		return null;
	}
}
