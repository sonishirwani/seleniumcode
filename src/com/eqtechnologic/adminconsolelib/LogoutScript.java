package com.eqtechnologic.adminconsolelib;
/* This class is not in use anymore. This was designed exclusively for old AdminConsole where
 * user was not able to acquire lock if logout was unsuccessful.*/
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.eqtechnologic.framework.Alerts;
import com.eqtechnologic.framework.Click;
import com.eqtechnologic.framework.Frame;

public class LogoutScript {
	
	private WebDriver driver;
	public LogoutScript(WebDriver driver){
		this.driver = driver;
	}

	public void logoutAfterError(Logger logger){
		
		String framesToLogoutButton = "loginFrame"+System.getProperty("delimiter")+"MenuFrame";
		
		logger.info("Into the logout script !! ");
		try {
			if (Alerts.isAlertPresent(driver)) {
				logger.info("Alert is present. Accepting the alert. ");
				Alert alert = driver.switchTo().alert();
				alert.accept();
			}
			driver.switchTo().defaultContent();
			List<WebElement> list = driver.findElements(By.xpath("//*[@typename='Close']"));
			if (list.size() != 0) {
				for (WebElement e : list) {
					if (e.isDisplayed()) {
						logger.info("Modal Popup is open. Closing it first. ");
						e.click();
						break;
					}
				}
			}
			Frame.switchToMany(framesToLogoutButton,driver,logger);
			logger.info("Logging out from Admin Console. ");
			Click.click("xpath", "//*[@id='Menu7']/table/tbody/tr/td",driver,logger);
			logger.info("Logged out successfully!! ");
			
		} catch (Exception e) {
			logger.fatal("Could Not Logout due to some exception occurred in logout script. Probably login was not successfull !!!!");
		}
		
		//driver.findElement(By.id("Menu7")).click();
		
	}
}
