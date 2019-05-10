package com.eqtechnologic.framework;
/* This class simply suspend the execution of driver for the time specified in parameters field of excel*/
import org.apache.log4j.Logger;

public class Delay {
	public static void delay(String parameters,Logger logger){
		
		int delaytime=Integer.parseInt(parameters);
		try {
			logger.info("Stopping for : "+parameters+" ms");
			Thread.sleep(delaytime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
