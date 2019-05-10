package com.eqtechnologic.framework;
/**
 * This class provides method to perform click operation.
 */
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// Referenced classes of package com.eqtechnologic.framework:
//            Locator, VisibleWebElement

public class Click
{

    public Click()
    {
    }
	/**
	 * This method finds the button to be clicked using getLocator method
	 * and clicks the button.
	 * @param locatorMethod
	 * @param locatorValue
	 */
    public static void click(String locatorMethod, String locatorValue, WebDriver driver, Logger logger)
    {
        int trial = 0;
        WebDriverException e = null;
        while(trial < 10) 
        {
            try
            {
                int delaytime = Integer.parseInt(System.getProperty("clickDelayInMillisecond"));
                try
                {
                    Thread.sleep(delaytime);
                }
                catch(InterruptedException ie)
                {
                    ie.printStackTrace();
                }
                WebDriverWait wait = new WebDriverWait(driver, 30L);
                logger.info((new StringBuilder("Clicking element: ")).append(locatorValue).append(" identified by: ").append(locatorMethod).toString());
                By by = Locator.getLocator(locatorMethod, locatorValue, logger);
                WebElement visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);
                if(visibleElement == null && by != null)
                {
                    visibleElement = driver.findElement(by);
                }
                ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", new Object[] {
                    visibleElement
                });
                try
                {
                    ((WebElement)wait.until(ExpectedConditions.elementToBeClickable(visibleElement))).click();
                }
                catch(TimeoutException t)
                {
                    visibleElement.click();
                }
                trial = 999;
            }
            catch(WebDriverException wde)
            {
                e = wde;
                if(wde.getMessage().contains("Element is not clickable at point"))
                {
                    trial++;
                    logger.info((new StringBuilder("Getting exception \"Element is not clickable\". Hence retry after 1 second. Curr" +
"ent trial: "
)).append(trial).append(". Trials left=").append(10 - trial).toString());
                } else
                {
                    throw wde;
                }
            }
        }
        if(trial == 10)
        {
            throw e;
        } else
        {
            return;
        }
    }

    public static void clickAtXY(String locatorMethod, String locatorValue, String parameters, WebDriver driver, Logger logger)
    {
        int trial = 0;
        WebDriverException e = null;
        while(trial < 10) 
        {
            try
            {
                String coordinates[] = parameters.split(System.getProperty("delimiter"));
                int delaytime = Integer.parseInt(System.getProperty("clickDelayInMillisecond"));
                try
                {
                    Thread.sleep(delaytime);
                }
                catch(InterruptedException ie)
                {
                    ie.printStackTrace();
                }
                logger.info((new StringBuilder("Clicking element: ")).append(locatorValue).append(" identified by: ").append(locatorMethod).toString());
                By by = Locator.getLocator(locatorMethod, locatorValue, logger);
                WebElement visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);
                if(visibleElement == null && by != null)
                {
                    visibleElement = driver.findElement(by);
                }
                Dimension elementDim = visibleElement.getSize();
                Double x;
                Double y;
                try
                {
                    x = Double.valueOf(((double)elementDim.width * Double.parseDouble(coordinates[0].trim())) / 100D);
                    y = Double.valueOf(((double)elementDim.height * Double.parseDouble(coordinates[1].trim())) / 100D);
                }
                catch(NumberFormatException nfe)
                {
                    logger.fatal((new StringBuilder("Can not be parsed: ")).append(coordinates[0].trim()).append(" ").append(coordinates[1].trim()).toString());
                    throw nfe;
                }
                logger.info((new StringBuilder("Clicking element:  \"")).append(locatorValue).append("\" using locator method: \"").append("\" at position (x,y): (").append(x.intValue()).append(",").append(y.intValue()).append(")").toString());
                ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", new Object[] {
                    visibleElement
                });
                Actions builder = new Actions(driver);
                builder.moveToElement(visibleElement, x.intValue(), y.intValue()).click().perform();
                trial = 999;
            }
            catch(WebDriverException wde)
            {
                e = wde;
                if(wde.getMessage().contains("Element is not clickable at point"))
                {
                    trial++;
                    logger.info((new StringBuilder("Getting exception \"Element is not clickable\". Hence retry after 1 second. Curr" +
"ent trial: "
)).append(trial).append(". Trials left=").append(10 - trial).toString());
                } else
                {
                    throw wde;
                }
            }
        }
        if(trial == 10)
        {
            throw e;
        } else
        {
            return;
        }
    }
    
    public static void clickOrIgnore(String locatorMethod, String locatorValue, WebDriver driver, Logger logger)
    {
        WebDriverWait wait = new WebDriverWait(driver, 5L);
        logger.info((new StringBuilder("Clicking element: ")).append(locatorValue).append(" identified by: ").append(locatorMethod).toString());
        By by = Locator.getLocator(locatorMethod, locatorValue, logger);
        WebElement visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);
		//if(visibleElement == null && by != null){ Commeneted as click_or_Ignore keyword was getting failed.
        Boolean isPresent = Boolean.valueOf(driver.findElements(by).size() != 0);
        if(isPresent.booleanValue())
        {
            try
            {
                ((WebElement)wait.until(ExpectedConditions.elementToBeClickable(visibleElement))).click();
            }
            catch(Exception e)
            {
                logger.warn((new StringBuilder("Failed to click element identified using locator method: ")).append(locatorMethod).append(" ").append("having value:").append(" ").append(locatorValue).toString());
            }
        }
        if(!isPresent.booleanValue())
        {
            try
            {
                ((WebElement)wait.until(ExpectedConditions.elementToBeClickable(visibleElement))).click();
			} catch (TimeoutException t) {
				visibleElement.click();
            }
            catch(Exception e)
            {
                logger.warn((new StringBuilder("Failed to click element identified using locator method: ")).append(locatorMethod).append(" ").append("having value:").append(" ").append(locatorValue).toString());
            }
        }
    }
//}

    public static void clickWOScroll(String locatorMethod, String locatorValue, WebDriver driver, Logger logger)
    {
        int trial = 0;
        WebDriverException e = null;
        while(trial < 10) 
        {
            try
            {
                int delaytime = Integer.parseInt(System.getProperty("clickDelayInMillisecond"));
                try
                {
                    Thread.sleep(delaytime);
                }
                catch(InterruptedException ie)
                {
                    ie.printStackTrace();
                }
                WebDriverWait wait = new WebDriverWait(driver, 30L);
                logger.info((new StringBuilder("Clicking element: ")).append(locatorValue).append(" identified by: ").append(locatorMethod).toString());
                By by = Locator.getLocator(locatorMethod, locatorValue, logger);
                WebElement visibleElement = VisibleWebElement.getFirstVisibleElement(by, driver);
                if(visibleElement == null && by != null)
                {
                    visibleElement = driver.findElement(by);
                }
                try
                {
                    ((WebElement)wait.until(ExpectedConditions.elementToBeClickable(visibleElement))).click();
                }
                catch(TimeoutException t)
                {
                    visibleElement.click();
                }
                trial = 999;
            }
            catch(WebDriverException wde)
            {
                e = wde;
                if(wde.getMessage().contains("Element is not clickable at point"))
                {
                    trial++;
                    logger.info((new StringBuilder("Getting exception \"Element is not clickable\". Hence retry after 1 second. Curr" +
"ent trial: "
)).append(trial).append(". Trials left=").append(10 - trial).toString());
                } else
                {
                    throw wde;
                }
            }
        }
        if(trial == 10)
        {
            throw e;
        } else
        {
            return;
        }
    }
}
