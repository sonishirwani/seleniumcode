package com.eqtechnologic.framework;
/**
 * This class switches the control from one window to another window.
 */
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

public class Window {
	/**
	 * winHandleBefore is a static variable to store current window handle.	
	 */
	public static String winHandleBefore=null;
	/**
	 * This method stores the parent window handle in a variable
	 * and switches the control to child window    
	 */
	public static void switchToWindow(WebDriver driver,Logger logger){
		winHandleBefore = driver.getWindowHandle();
        Set<String> windows = driver.getWindowHandles();
        if(windows.size()<2){
        	logger.error("No child Window present to switch");	
        	driver.close();
  		    throw new NoSuchWindowException("No child Window present to switch");		
  	  }
        for (String window : windows) {
    	  if(!window.equals(winHandleBefore)){  
    		  driver.switchTo().window(window);
    		  logger.info("Switching to Window : "+driver.getTitle());  
    	  }
    	  
         }
        
	}
	
	public static void newWindow(WebDriver driver,Logger logger){
		winHandleBefore = driver.getWindowHandle();
        Set<String> windows = driver.getWindowHandles();
		driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
		driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
        /* if(windows.size()<2){
        	logger.error("No child Window present to switch");	
        	driver.close();
  		    throw new NoSuchWindowException("No child Window present to switch");		
  	  }*/
        for (String window : windows) {
    	  if(!window.equals(winHandleBefore)){  
    		  driver.switchTo().window(window);
    		  logger.info("Switching to Window : "+driver.getTitle());  
    	  }
    	  
         }
        
	}

/**
* This method closes the child window 
* and switches the control back to parent window    
*/	
     public static void closeWindow(WebDriver driver,Logger logger){
    	 if(!Driver.isSessionAvailable(driver))
 			return;
    	 
    	logger.info("Closing Window = "+driver.getTitle());
    	if(winHandleBefore == null)
    	  	driver.quit(); 
    	else if(winHandleBefore != null){
    		driver.close();
    	   	driver.switchTo().window(winHandleBefore);    	   	
    	   	logger.info("Switching to Window = "+driver.getTitle());
    	   	winHandleBefore=null;
    	 }
      }
     
}
