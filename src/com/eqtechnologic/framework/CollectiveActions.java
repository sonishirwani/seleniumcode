package com.eqtechnologic.framework;
/**
 * This class provided the method to execute multiple actions or package of actions.
 */
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CollectiveActions{
	
	/**
	 * This enum contains all the keywords which will be used to perform multiple actions.
	 */
	private enum Keywords {
		DRAG,DROP,MOUSEHOVER,RIGHTCLICK,DOUBLECLICK,PRESSSHIFT,RELEASESHIFT,PRESSCTRL,PRESSCTRLA,RELEASECTRL,ENTER,CLICK,DELETE,BACKSPACE;
	}
	
	/**
	 * This method stores the list of actions to be performed in a string
	 * and performs action one at a time. 
	 * @param locatorMethod
	 * @param locatorValue
	 * @param parameters
	 */
	public static void performActions(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger){
		
		String[] methods = locatorMethod.split(System.getProperty("delimiter"));
		String[] values = locatorValue.split(System.getProperty("delimiter"));
		String[] actions = parameters.split(System.getProperty("delimiter"));
		
		if(methods.length != values.length){
			logger.error("Arguments mistmatch !! Locator method cell " +
					"has: "+methods.length+" arguments while Locator value " +
							"has: "+values.length+" arguments!");
		}
		if(methods.length != actions.length){
			logger.error("Arguments mistmatch !! Locator method cell " +
						"has: "+methods.length+" arguments while Locator parameters " +
								"has: "+actions.length+" arguments!");
		}
		for(int i = 0;i<methods.length;i++){
			logger.info("Performing action: "+actions[i]+" on element identified by: "+values[i]+" using locator method: "+methods[i]);
			processKeyword(methods[i],values[i],actions[i],driver,logger);
		}
				
		
	}
	
	/**
	 * This method reads the action to be performed from the list, matches it with the enum provided
	 * and performs action accordingly.
	 * Keyword is a mandatory input which decides which method of which class 
	 * will be called.
	 * @param locatorMethod
	 * @param locatorValue
	 * @param parameter
	 */
	public static void processKeyword(String locatorMethod,String locatorValue,String parameter, WebDriver driver,Logger logger){
				
		//Keywords keyword = Keywords.valueOf(parameter.trim().toUpperCase());
		Actions builder=new Actions(driver);
		parameter=parameter.trim().toUpperCase();
		Keywords keyword= null;
		try{
			keyword = Keywords.valueOf(parameter);
		}catch(IllegalArgumentException e){
			logger.fatal("Parameter for Perform Action : "+parameter+" is not identified.");
			throw e;
		}
		By by = Locator.getLocator(locatorMethod, locatorValue,logger);
		WebElement  visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);
		if(visibleElement == null && by != null){
			visibleElement = driver.findElement(by);
		}
		switch(keyword){
			case DRAG: builder.clickAndHold(visibleElement).perform();break;
			case DROP: builder.release(visibleElement).perform();break;
			case MOUSEHOVER: builder.moveToElement(visibleElement).perform();break;
			case RIGHTCLICK: builder.contextClick(visibleElement).perform();break;
            case DOUBLECLICK:builder.doubleClick(visibleElement).perform();break;
			case PRESSSHIFT: builder.keyUp(Keys.SHIFT).perform();break;
			case RELEASESHIFT:builder.keyDown(Keys.SHIFT).perform();break;
			case PRESSCTRL:builder.keyUp(Keys.CONTROL).perform();break;
			case PRESSCTRLA:builder.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();break;
			case RELEASECTRL:builder.keyDown(Keys.CONTROL).perform();break;
			case CLICK:builder.click(visibleElement).perform();break;
			case ENTER:builder.sendKeys(Keys.ENTER).perform();break;
			case DELETE:builder.sendKeys(Keys.DELETE).perform();break;
			case BACKSPACE:builder.sendKeys(Keys.BACK_SPACE).perform();break;
			default: ;
		}
		
	}	
	
}
