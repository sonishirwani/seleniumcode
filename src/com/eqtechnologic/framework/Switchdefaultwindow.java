package com.eqtechnologic.framework;

import java.io.PrintStream;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;

public class Switchdefaultwindow
{
  public static void switchd(WebDriver driver, Logger logger)
  {
    System.out.println("Switching to default contents");
    driver.switchTo().window("");
  }
}
