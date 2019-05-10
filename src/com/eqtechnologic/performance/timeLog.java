package com.eqtechnologic.performance;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class timeLog {
	String Operation=null;
	/*String Start=null;
	String End= null;*/
	
	String Start;
	String End;
	long diff;
	public static Calendar cal1 = Calendar.getInstance(); 
	private static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSS");
	Logger logger;
	
	public void setStartTime()
	{
		  Calendar cal1 = Calendar.getInstance(); 
		  SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSS");
		this.Start=format1.format(cal1.getTime());
		//Start=System.currentTimeMillis();
	}
	
	public void setEndTime()
	{
		Calendar cal2 = Calendar.getInstance(); 
		  SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss:SSS");
		this.End=format2.format(cal2.getTime());
		//End=System.currentTimeMillis();
	}
	
	public void setOperation(String s)
	{
		this.Operation=s;
	}
	
	public void calculateTimeDiff() throws ParseException
	{
		setEndTime();
		try{
		
		Date d1=format.parse(Start);
		Date d2=format.parse(End);
		
		this.diff=d2.getTime()-d1.getTime();
			
		}
		catch(Exception e){
			logger.warn(e.getMessage());
		}
	}
	
	public String getStartTime()
	{
		return this.Start;
	}
	public String getEndTime()
	{
		return this.End;
	}
	
	public long getDiff()
	{
		return this.diff;
	}
	public String getOperation()
	{
		return this.Operation;
	}
	
	public static String ConvertMilliSecondsToFormattedDate(long milliSeconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(String.valueOf(milliSeconds)));
        return format.format(calendar.getTime());
    }
}
