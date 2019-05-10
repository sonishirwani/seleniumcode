package com.eqtechnologic.framework;
/* It is one of the core classes of framework beacause
 * it is responsible for providing WebDriver driver object that will 
 * be used to drive the browser with other user actions. 
 */
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.openqa.selenium.remote.UnreachableBrowserException;

public class Driver {
/**
	 * This method reads Chrome driver and Firefox driver location from config.properties.
	 * It sets the value for browser to be used according to user input. 
	 * @param browser
	 */
	public static WebDriver setDriver(WebDriver driver,Logger logger){
		
		String browser = System.getProperty("browser");
		Proxy proxy = new Proxy();
		
		String proxyIp = System.getProperty("zap.proxy.ip");
		String proxyPort = System.getProperty("zap.proxy.port");
		String securityTest = System.getProperty("runSecurityTest");
		String maximizeWindow = System.getProperty("maximizeWindow");
						
		if(browser.equalsIgnoreCase("Firefox")){
			FirefoxProfile ffProfile = new FirefoxProfile();
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			
			String firefoxDownloadDefaultLocation = System.getProperty("firefoxDownloadDefaultLocation");
			ffProfile.setPreference("browser.download.folderList", 2);
			ffProfile.setPreference("browser.download.dir", firefoxDownloadDefaultLocation); 
			ffProfile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/x-download");
			
			if(securityTest.equalsIgnoreCase("true") ){ //&& proxyIp !=null && proxyPort!=null
				proxy.setHttpProxy(proxyIp+":"+proxyPort);
				capabilities.setCapability(CapabilityType.PROXY, proxy);
				driver= new FirefoxDriver(new FirefoxBinary(), ffProfile,capabilities);
				logger.info("Set the proxy for browser: "+proxyIp+":"+proxyPort);
			}else
				driver= new FirefoxDriver(ffProfile);
			
			logger.info("Browser set to: Firefox");
			
		}
		else if(browser.equalsIgnoreCase("Chrome")){
			
			System.setProperty("webdriver.chrome.driver", System.getProperty("chromeDriverPath"));
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			
			if(securityTest.equalsIgnoreCase("true") ){ //&& proxyIp !=null && proxyPort!=null
				proxy.setHttpProxy(proxyIp+":"+proxyPort);
				capabilities.setCapability(CapabilityType.PROXY, proxy);
				driver= new ChromeDriver(capabilities);
				logger.info("Set the proxy for browser: "+proxyIp+":"+proxyPort);
			}else
			{
				driver= new ChromeDriver(capabilities);					
				logger.info("Browser set to: Chrome");
			}
		}
		else if(browser.equalsIgnoreCase("IE")){
			
			System.setProperty("webdriver.ie.driver", System.getProperty("ieDriverPath"));
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
		    capabilities.setCapability("ie.ensureCleanSession", true);
		    capabilities.setCapability("ignoreProtectedModeSettings", true);	
		    capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			if(securityTest.equalsIgnoreCase("true") ){ //&& proxyIp !=null && proxyPort!=null
				proxy.setHttpProxy(proxyIp+":"+proxyPort);
				capabilities.setCapability(CapabilityType.PROXY, proxy);
				driver= new InternetExplorerDriver(capabilities);
				logger.info("Set the proxy for browser: "+proxyIp+":"+proxyPort);
			}else
				driver = new InternetExplorerDriver(capabilities);
			logger.info("Browser set to: Internet Explorer");
						
		}
		
		if(maximizeWindow.equalsIgnoreCase("true")){
			driver.manage().window().maximize();
		}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		return driver;
	}
	
	
	/**
	 * This method is used to return the RemoteWebDriver object.
	 * This driver object will use any registered free browser present on the network. 
	 * @param browser
	 */	
public static WebDriver setremoteDriver(WebDriver driver,Logger logger) throws MalformedURLException{
		
	String browser = System.getProperty("browser");
	Proxy proxy = new Proxy();
		
	String proxyIp = System.getProperty("zap.proxy.ip");
	String proxyPort = System.getProperty("zap.proxy.port");
	String securityTest = System.getProperty("runSecurityTest");
	String maximizeWindow = System.getProperty("maximizeWindow");
	DesiredCapabilities capability;
	if(browser.equalsIgnoreCase("Firefox")){
		FirefoxProfile ffProfile = new FirefoxProfile();
		capability = DesiredCapabilities.firefox();
		
		String firefoxDownloadDefaultLocation = System.getProperty("firefoxDownloadDefaultLocation");
		ffProfile.setPreference("browser.download.folderList", 2);
		ffProfile.setPreference("browser.download.dir", firefoxDownloadDefaultLocation); 
		ffProfile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/x-download");
		
		if(securityTest.equalsIgnoreCase("true") ){ //&& proxyIp !=null && proxyPort!=null
			proxy.setHttpProxy(proxyIp+":"+proxyPort);
			capability.setCapability(CapabilityType.PROXY, proxy);
			capability.setCapability(FirefoxDriver.PROFILE, ffProfile);
			driver = new RemoteWebDriver(new URL(System.getProperty("hub")),capability);
		}else
			driver = new RemoteWebDriver(new URL(System.getProperty("hub")),capability);
		
		logger.info("Browser set to: Firefox");
		
	}
	else if(browser.equalsIgnoreCase("Chrome")){
		
		
		capability = DesiredCapabilities.chrome();
		capability.setBrowserName("chrome");
		if(securityTest.equalsIgnoreCase("true") ){ //&& proxyIp !=null && proxyPort!=null
			proxy.setHttpProxy(proxyIp+":"+proxyPort);
			capability.setCapability(CapabilityType.PROXY, proxy);
			driver = new RemoteWebDriver(new URL(System.getProperty("hub")),capability);
			logger.info("Set the proxy for browser: "+proxyIp+":"+proxyPort);
		}
		else
			driver = new RemoteWebDriver(new URL(System.getProperty("hub")),capability);		
		
		logger.info("Browser set to: Chrome");
	}
	else if(browser.equalsIgnoreCase("IE")){
		
		//System.setProperty("webdriver.ie.driver", System.getProperty("ieDriverPath"));
		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
		capabilities.setBrowserName("internet explorer");
		//capabilities.setVersion("11");
		capabilities.setCapability("ignoreProtectedModeSettings", true);	
	    capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
		if(securityTest.equalsIgnoreCase("true") ){ //&& proxyIp !=null && proxyPort!=null
			proxy.setHttpProxy(proxyIp+":"+proxyPort);
			capabilities.setCapability(CapabilityType.PROXY, proxy);
			//driver= new InternetExplorerDriver(capabilities);
			driver = new RemoteWebDriver(new URL(System.getProperty("hub")),capabilities);
			logger.info("Set the proxy for browser: "+proxyIp+":"+proxyPort);
		}else
			driver = new RemoteWebDriver(new URL(System.getProperty("hub")),capabilities);
		logger.info("Browser set to: Internet Explorer");
					
	}
	
	if(maximizeWindow.equalsIgnoreCase("true")){
		driver.manage().window().maximize();
	}
	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	return driver;
	}
	/**
	 * This method closes the current instance of browser.
	 */
	public static void closeDriver(WebDriver driver){
		driver.close();
	}
	/**
	 * This method quits the all available instances of browser.
	 */
	public static void quitDriver(WebDriver driver){
		driver.quit();
	}
	
	/**
	 * This method pastes provided url into the address bar of the browser.
	 * @param url
	 */
	public static void openUrl(String url,WebDriver driver,Logger logger){
		logger.info("Hitting URL: "+url);
		driver.get(url);
		
	}
	
	public static boolean isSessionAvailable(WebDriver driver){
		try{
			driver.getTitle();
			return true;
		}
		catch(SessionNotFoundException s){
			return false;
		}
		catch(UnreachableBrowserException s){
			return false;
		}
		catch(NoSuchWindowException s){
			return false;
		}
		catch(Throwable t){
			return false;
		}
	}
	
	
	//Navigation commands for browser 
		public static void NavigateBack(WebDriver driver,Logger logger)
		{

			logger.info("Navigating Back: ");
			driver.navigate().back();	
		
		}
		public static void NavigateForward(WebDriver driver,Logger logger)
		{
		
			logger.info("Navigating Forward: ");
			driver.navigate().forward();
			
		}
		public static void NavigateRefresh(WebDriver driver,Logger logger)
		{
			logger.info("Refreshing URL: ");
			driver.navigate().refresh();
		}
		
		/*----------------------------------------------------------*/

}
