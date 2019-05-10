package com.eqtechnologic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
/**
 * This class is used to put properties from the file to the System
 * @author mayank
 *
 */
public class PropertiesToSystem {

	private File propertyFile=null;
	
	public PropertiesToSystem(String fileName){
		this.propertyFile = new File(fileName);
	}
	
	/**
	 * This method loads the properties from the file to the System
	 */
	public void load(){
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(propertyFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Configuration file could not be read. Please check the path !!",e);
		}
		
		Properties properties = new Properties();
		try {
			properties.load(fin);
		} catch (IOException e) {
			throw new RuntimeException("Properties file could not be read. Please verify for its correctness !!",e);
		}
		
		//System.setProperties(properties);
		
		@SuppressWarnings("unchecked")
		Enumeration<String> propertyNames = (Enumeration<String>) properties.propertyNames();
		
		while(propertyNames.hasMoreElements()){
			String name = propertyNames.nextElement();
			String value = properties.getProperty(name);
			
			System.setProperty(name, value);
			//System.out.println(name + " : " + System.getProperty(name));
		}
		
	}
	
}
