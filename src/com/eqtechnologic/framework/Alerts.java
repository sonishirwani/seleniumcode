package com.eqtechnologic.framework;
/**
 * This class provides method that accepts Alerts.
 */
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class Alerts{
/**
	* This method checks if an Alert is present and accepts Alert.
	*/	
	public static void handleAlert(WebDriver driver,Logger logger){
		boolean present = isAlertPresent(driver);
		if(present){
			Alert alert = driver.switchTo().alert();
			logger.info("Alert Present ! Says : "+alert.getText());
			logger.info("Accepting the Alert.");
			alert.accept();		
		}
		else
			logger.info("Expected alert not present !");
	}
/**
 * * This method returns Boolean value true if Alert is present else false is returned
 * @return boolean true or false
 */

	public static boolean isAlertPresent(WebDriver driver){ 
		int i=0;
		while(i++<5){
			try{
				driver.switchTo().alert();
				return true;
			}catch(Exception e){
				try{
					Thread.sleep(200);
		    	}catch (InterruptedException e1){
					e1.printStackTrace();
		     	}continue;
			}
		}return false;
	}
	/**
	 * * This method verifies the text present in alert box.
	 * If the text matches the expected text, it will continue the execution else it stops
	 * the execution by throwing an exception.
	 * @return
	 */
	public static void verifyAlert(WebDriver driver,String expectedText, Logger logger){ 
		boolean present = isAlertPresent(driver);
		if(present){
			Alert alert = driver.switchTo().alert();
			String actualText = alert.getText();
			logger.info("Alert Present ! Actual Text in alert box : "+ actualText);
			Assert.assertEquals(actualText, expectedText,"Expected text does not match the text present in the Alert box! Text");
			alert.accept();	
		}
		else
			Assert.fail("No alert box present but an Alert was expected!");
	}
	
}
