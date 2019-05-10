package com.eqtechnologic.framework;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class current_time
{
  public static void currenttime(String parameters)
  {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
    System.out.println(parameters + " " + sdf.format(cal.getTime()));
  }
}
