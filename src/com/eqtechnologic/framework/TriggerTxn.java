package com.eqtechnologic.framework;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

public class TriggerTxn {

	static String prfPath = System.getProperty("prfPath");
	
	public static void triggerTransaction(String locatorMethod,String locatorValue,String parameters, WebDriver driver,Logger logger) throws SOAPException, IOException, JSONException{
		
		locatorMethod = locatorMethod.trim().toUpperCase();
		
		try{
				if(locatorMethod.equalsIgnoreCase("NV"))
					TriggerNVTransactionForProcess(parameters, driver, logger);
				else if(locatorMethod.equalsIgnoreCase("SOAP"))
					TriggerSOAPTransactionForProcess(parameters, driver, logger);
				else if(locatorMethod.equalsIgnoreCase("JSON"))
					TriggerJSONTransactionForProcess(parameters, driver, logger);
				
			}
		catch(IllegalArgumentException e){
			
			logger.fatal("Invalid Trigger Type.....   " +locatorMethod );
			throw e;
			
		}
		
	}
	
	
	
	
	
	public static void TriggerNVTransactionForProcess(String parameter, WebDriver driver,Logger logger) throws MalformedURLException{
		
		String sURL = System.getProperty("URL") + "/eQHTTPListener?" + parameter;
		
		logger.info("---------------------------------------------------------------------------------------------------------");
		logger.info("Triggering NV Process : "+sURL);
		logger.info("---------------------------------------------------------------------------------------------------------");

		
		
		URL hp = new URL(sURL);
		HttpURLConnection hpCon = null;
		int respCode = 0;
		String respMsg;
					
		try
		{
		
			hpCon =(HttpURLConnection) hp.openConnection();
			respCode = hpCon.getResponseCode();
			System.out.println("The response Code from MI is : " + respCode);
			//hpCon.setDoOutput(true);
			
			if (respCode == 200) {
				
				logger.info("----------NV Process Triggered successfully---------------------");
				respMsg = hpCon.getResponseMessage();
				logger.info("-----Response Message : " + respMsg + "----------");
			    BufferedReader br = new BufferedReader(new InputStreamReader((hpCon.getInputStream())));
			    logger.info("---------Transaction Details : " + br.readLine() + "-----------------");
			} else {
				logger.info("----------NV Process Trigger Failed---------------------");
			}
		}
		catch (Exception Throwable) 
		{
			Throwable.printStackTrace();
		}
			
	}
		

	public static void TriggerSOAPTransactionForProcess(String parameter, WebDriver driver,Logger logger) throws SOAPException, IOException{
		
		
		//MI URL is formed here
		String sURL = System.getProperty("URL") + "/eQHTTPListener?" ;
		
		//split parameter input string from excel
		String[] str  = new String[3];
		str = parameter.split(";");
		str[1]=str[1].trim();
		str[1] = prfPath + "/" + str[1];
		FileInputStream fstream = new FileInputStream(str[1]);    // FileInputStream object points to File which contains SOAP message details
		MessageFactory messageFactory = null;
		MimeHeaders mhHdr = new MimeHeaders();
		str[0]=str[0].trim().toUpperCase();
				
		if(str[0].equals("SOAP11")){
			messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);      // Create instance of messageFactory with SOAP11 protocol
		}
		else if(str[0].equals("SOAP12")){
			messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);      // Create instance of messageFactory with SOAP12 protocol
		}
		
		SOAPMessage soapmsg = messageFactory.createMessage(mhHdr, fstream);                   // Create SOAP message
		
		logger.info("---------------------------------------------------------------------------------------------------------");
		logger.info("Triggering SOAP Process : "+sURL);
		
		
		
		ByteArrayOutputStream soapBOS = new ByteArrayOutputStream();
		soapmsg.writeTo(soapBOS);
		String soapMessageStream = soapBOS.toString();
		
		logger.info(soapMessageStream);
		logger.info("---------------------------------------------------------------------------------------------------------");
		URL hp = new URL(sURL);
		HttpURLConnection hpcon = (HttpURLConnection) hp.openConnection();
		hpcon.setDoOutput(true);
		hpcon.setUseCaches(false);
		hpcon.setRequestProperty("Content-Type", "text/xml");
		
		OutputStreamWriter wr = new OutputStreamWriter(hpcon.getOutputStream());
		wr.write(soapMessageStream);
		wr.flush();
		
		if (hpcon.getResponseCode() == 200) {
			logger.info("----------SOAP Process Triggered successfully---------------------");
			String respMsg = hpcon.getResponseMessage();
			logger.info("-----Response Message : " + respMsg + "----------");
		    BufferedReader br = new BufferedReader(new InputStreamReader((hpcon.getInputStream())));
		    logger.info("---------Transaction Details : " + br.readLine() + "-----------------");
		} else {
			logger.info("----------SOAP Process Trigger Failed---------------------");
		}
		
	}
	
	
	public static void TriggerJSONTransactionForProcess(String parameter, WebDriver driver,Logger logger) throws JSONException, IOException{
		
		
		//MI URL is formed here
		String sURL = System.getProperty("URL") + "/eQHTTPListener?" ;
		
		logger.info("---------------------------------------------------------------------------------------------------------");
		logger.info("Triggering JSON Process : "+sURL);
		//FileReader fr = new FileReader(parameter);
		//BufferedReader textReader = new BufferedReader(fr);
		//String jsonMsg;
		
		//FileInputStream fstream = new FileInputStream(parameter);    // FileInputStream object points to File which contains JSON message details
		
		
		parameter = prfPath + "/" + parameter.trim();
		
		File file = new File(parameter);
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();

		String str = new String(data, "UTF-8");
		
		JSONObject jsonobj = new JSONObject(str);
		
		URL hp = new URL(sURL);
		HttpURLConnection hpcon = (HttpURLConnection) hp.openConnection();
		hpcon.setDoOutput(true);
		hpcon.setUseCaches(false);
		hpcon.setRequestProperty("Content-Type", "application/json");
		hpcon.setRequestProperty("Accept", "application/json");
		hpcon.setRequestMethod("POST");
		
		OutputStreamWriter wr = new OutputStreamWriter(hpcon.getOutputStream());
		wr.write(jsonobj.toString());
		wr.flush();
		
		if (hpcon.getResponseCode() == 200) {
			logger.info("----------JSON Process Triggered successfully---------------------");
			String respMsg = hpcon.getResponseMessage();
			logger.info("-----Response Message : " + respMsg + "----------");
		    BufferedReader br = new BufferedReader(new InputStreamReader((hpcon.getInputStream())));
		    logger.info("---------Transaction Details : " + br.readLine() + "-----------------");
		} else {
			logger.info("----------JSON Process Trigger Failed---------------------");
		}
	
	}
	
	
	
		
}
	
	
