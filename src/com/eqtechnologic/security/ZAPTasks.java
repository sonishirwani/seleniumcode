package com.eqtechnologic.security;
/**
 * This class has three keywords SC_NEWSESSION, SC_ACTIVESCAN, SC_SPIDER.
 * It simply does the tasks as the keyword describes.
 */
import java.io.File;

import org.apache.log4j.Logger;
import org.zaproxy.clientapi.ant.ActiveScanUrlTask;
import org.zaproxy.clientapi.ant.NewSessionTask;
import org.zaproxy.clientapi.ant.SpiderUrlTask;

public class ZAPTasks {
	
	private static String proxyIp;
	private static int proxyPort;
	private static String runSecurityTest;
	static{
		proxyIp = System.getProperty("zap.proxy.ip");
		proxyPort = Integer.parseInt(System.getProperty("zap.proxy.port"));
		runSecurityTest = System.getProperty("runSecurityTest");
	}
	
	/**
	 * It will create set up the proxy and URL in the ZAP tool and begins the execution.
	 * @param logger
	 */
	public static void spider(Logger logger){
		if(!runSecurityTest.equalsIgnoreCase("true")){
			logger.fatal("Suite is not running in Security Test mode." +
					"Usage of this keyword: SC_SPIDER is prohibitted in functional testing mode. " +
					"\n please check the configuration.");
			throw new RuntimeException("Suite is not running in Security Test mode." +
					"Usage of this keyword: Spider is prohibitted in functional testing mode. " +
					"\n please check the configuration.");
		}
		SpiderUrlTask zt = new SpiderUrlTask();
		zt.setZapAddress(proxyIp);
		zt.setZapPort(proxyPort);
		zt.setDebug(true);
		zt.setUrl(System.getProperty("URL"));
		zt.execute();
	}
	
	/**
	 * It will perform the active scanning task after it is called in the testcase.
	 * @param logger
	 */
	public static void activeScan(Logger logger){
		if(!runSecurityTest.equalsIgnoreCase("true")){
			logger.fatal("Suite is not running in Security Test mode." +
					"Usage of this keyword: SC_ACTIVESCAN is prohibitted in functional testing mode. " +
					"\n please check the configuration.");
			throw new RuntimeException("Suite is not running in Security Test mode." +
					"Usage of this keyword: Spider is prohibitted in functional testing mode. " +
					"\n please check the configuration.");
		}
		ActiveScanUrlTask zt = new ActiveScanUrlTask();
		zt.setZapAddress(proxyIp);
		zt.setZapPort(proxyPort);
		zt.setDebug(true);
		zt.setUrl(System.getProperty("URL"));
		zt.execute();
	}
	
	/*private void saveSession(){
		SaveSessionTask zt = new SaveSessionTask();
		zt.setZapAddress("localhost");
		zt.setZapPort(8090);
		zt.setDebug(true);
		zt.setName("E:\\New Folder\\111");
		zt.execute();
		//zt.setUrl("http://192.168.5.249:5050/EQBE7DEV1A");
				
	}*/
	
	/**
	 * It will create a new session for the current testcase execution.
	 * @param sessionName
	 * @param logger
	 */
	public static void newSession(String sessionName,Logger logger){
		if(!runSecurityTest.equalsIgnoreCase("true")){
			logger.fatal("Suite is not running in Security Test mode." +
					"Usage of this keyword: Spider is prohibitted in functional testing mode. " +
					"\n please check the configuration.");
			throw new RuntimeException("Suite is not running in Security Test mode." +
					"Usage of this keyword: SC_NEWSSEION is prohibitted in functional testing mode. " +
					"\n please check the configuration.");
		}
		String securityBaseDirectory = System.getProperty("securityBaseDirectory");
		String testsuitename = System.getProperty("suiteName");
		String path = securityBaseDirectory+"/"+testsuitename+"/"+sessionName;
		
		File file = new File(path);
		file.mkdirs();
		String sessionAbsolutePath = file.getAbsolutePath()+"//"+sessionName;
		
		NewSessionTask zt = new NewSessionTask();
		zt.setZapAddress(proxyIp);
		zt.setZapPort(proxyPort);
		zt.setDebug(true);
		zt.setName(sessionAbsolutePath);
		zt.execute();
				
	}
	
}
