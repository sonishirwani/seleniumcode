package com.eqtechnologic.framework;
/*
 * This class provides method that runs a series of actions for specified number of times.
 */
import java.awt.AWTException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.openqa.selenium.WebDriver;

import com.eqtechnologic.exceptions.ExcelConnectionException;
import com.eqtechnologic.performance.timeLog;
import com.eqtechnologic.util.ExcelProcessor;
import com.eqtechnologic.util.ReplaceMacro;


public class Loop {
	/*
	 * This method checks number of text boxes and selection boxes and handles them for number of times the loop iterates.
	 * It also handles screenshot generation in case of loop. 
	 */
	public static int looping(String count,Integer rownum,ExcelProcessor excel, WebDriver driver, HashMap <String,String> map,Logger logger, HashMap <String,ArrayList<timeLog>> testMap) throws Exception {
		List<String[]> textboxes = new ArrayList<String[]>();
		List<String[]> selectboxes = new ArrayList<String[]>();
		String replacedValue=null;
		int loopcount,loopstartnumber,i,loopsize=0;
		try{
			loopcount=Integer.parseInt(count);
		}
		catch(Exception e){
			logger.fatal("Loop count not specified    !!!!");
			throw new RuntimeException(e);
		}
		loopstartnumber=rownum;		
				
		while(!excel.getCellValue(rownum,0).equalsIgnoreCase("loopEnd")){
			if(excel.getCellValue(rownum,0).equalsIgnoreCase("textbox")){
				String[] textvalues=ReplaceMacro.replace(excel.getCellValue(rownum,3),map).split(System.getProperty("delimiter"));
	     		textboxes.add(textvalues);
			}
			if(excel.getCellValue(rownum,0).equalsIgnoreCase("select")){
				String[] selectvalues=excel.getCellValue(rownum,3).split(System.getProperty("delimiter"));
				selectboxes.add(selectvalues);
			}
			loopsize++;
			rownum++;
		}
		logger.info("Total number of textboxes in the loop : "+textboxes.size());
		logger.info("Total number of selection boxes in the loop : "+selectboxes.size());
		
		for(i=0;i< loopcount;i++){
			int textboxnumber=0;
			int selectboxnumber=0;
			rownum=loopstartnumber+1;
			
			while(!excel.getCellValue(rownum,0).equalsIgnoreCase("loopEnd")){
				if(excel.getCellValue(rownum,0).equalsIgnoreCase("textbox")){
					String[] str=textboxes.get(textboxnumber);
					if(i<str.length)
							{
					Textbox.clearAndType(ReplaceMacro.replace(excel.getCellValue(rownum,1), map),ReplaceMacro.replace(replaceIndex(excel,excel.getCellValue(rownum,2),i),map),ReplaceMacro.replace(replaceIndex(excel,str[i],i),map),driver,logger);
					textboxnumber++;
							}
					else
					{
						Textbox.clearAndType(ReplaceMacro.replace(excel.getCellValue(rownum,1), map),ReplaceMacro.replace(replaceIndex(excel,excel.getCellValue(rownum,2),i),map),ReplaceMacro.replace(replaceIndex(excel,excel.getCellValue(rownum,3),i),map),driver,logger);
						textboxnumber++;
					}
				}
				else if(excel.getCellValue(rownum,0).equalsIgnoreCase("select")){
					String[] str1=selectboxes.get(selectboxnumber);
					if(i<str1.length)
					{
						Selection.selectByText(ReplaceMacro.replace(excel.getCellValue(rownum,1), map),ReplaceMacro.replace(replaceIndex(excel,excel.getCellValue(rownum,2),i),map),ReplaceMacro.replace(replaceIndex(excel,str1[i],i),map),driver,logger);
						selectboxnumber++;
					}
					else
					{
					Selection.selectByText(ReplaceMacro.replace(excel.getCellValue(rownum,1), map),ReplaceMacro.replace(replaceIndex(excel,excel.getCellValue(rownum,2),i),map),ReplaceMacro.replace(replaceIndex(excel,excel.getCellValue(rownum,3),i),map),driver,logger);
					selectboxnumber++;
					}
				}
				else if(excel.getCellValue(rownum,0).equalsIgnoreCase("screenshot")){
						Screenshot.takeScreenshot(excel.sheetName, rownum+"_"+i,ReplaceMacro.replace(excel.getCellValue(rownum,2),map),driver,logger);
					}
				//JSON folder name and overwrite handled in case of Loop
				else if(excel.getCellValue(rownum,0).equalsIgnoreCase("JSCover")){
					JSCover.takeJson(System.getProperty("JSsheetName1"),driver,logger);
				}
				else{
				     String locatorValue=excel.getCellValue(rownum,2);
				     String parameter=excel.getCellValue(rownum,3);
				     String replacedlocatorValue=null;
				     String replacedParameter=null;
				     if(locatorValue!=null &&locatorValue!="")
				     {
				      replacedlocatorValue= replaceIndex(excel,locatorValue,i);
				     }
				     if(parameter!=null &&parameter!="")
				     {
				     replacedParameter= replaceIndex(excel,parameter,i);
				     }
					
					Interpreter interpreter = new Interpreter(excel,driver,map,logger,testMap);
					interpreter.locatorFromloop=replacedlocatorValue;
					interpreter.parameterFromloop=replacedParameter;
					interpreter.processKeywordAtRowNum(rownum);
				}
				rownum++;
			}	
		}
		return loopsize;

	}
	/*
	 * This method replaces the {index} with current index of the loop
	 */
	public static String replaceIndex(ExcelProcessor excel,String Value,int index)
	{
		String replacedValue=null;
		Pattern p = Pattern.compile(Pattern.quote("{index}"));   // the pattern to search for
		Matcher m = p.matcher(Value);
		if(m.find()){
			replacedValue=Value.replaceAll(p.toString(), Integer.toString(index));
			return replacedValue;
			}
		return Value;
	}

}
