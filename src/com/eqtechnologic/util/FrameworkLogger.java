package com.eqtechnologic.util;
/**
 * This class is used as base class to generate logs. 
 */
import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

public class FrameworkLogger {
	
	private String fileName;
	public Logger logger;
	   public FrameworkLogger(String name){
		this.fileName = name;
		logger = Logger.getLogger(fileName);
	}
   public Logger getInstance() {

      PatternLayout layout = new PatternLayout("%d - %p - %m%n");    
      RollingFileAppender rollingFileAppender=null;
	try {
		rollingFileAppender = new RollingFileAppender(layout,this.fileName,false);
	} catch (IOException e) {	}    
      ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%5p [%t] (%F:%L) - %m%n"));
      rollingFileAppender.setMaxBackupIndex(5);
      rollingFileAppender.setMaxFileSize("10MB");
      logger.addAppender(rollingFileAppender);
      logger.addAppender(consoleAppender);
      logger.setLevel((Level) Level.DEBUG);
      return logger;
   }
}
