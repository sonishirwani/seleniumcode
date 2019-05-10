package com.eqtechnologic.util;

import java.util.HashMap;

/**
 * This class has a static method that will replace a particular string that is present between '${' and '}' with the one 
 * that is present in the System or in the map object passed in the arguments. 
 * E.g. if param1 is set to a value newParam(PARAM1=newParam) in the System or in the map object,
 * it will replace "old_${param1}" with the "old_newParam" 
 * 
 * @author mayank
 *
 */
public class ReplaceMacro {
	private static String replacedString = null;
	
	/**
	 * 
	 * @param arg
	 * @param map
	 * @return
	 */
	public static String replace(String arg,HashMap<String,String> map){
		
		if(arg == null || arg.trim() ==""){
			return arg;
		}
		int variableStart = arg.indexOf("${");
		
		if( variableStart == -1)
				return arg;
		else {
			String newString = arg.substring(variableStart);
						
			int variableEnd = variableStart + newString.indexOf("}");
			if(variableEnd <= variableStart)
				return arg;
			String variable = arg.substring(variableStart+2, variableEnd).toUpperCase();
			String variableValue = System.getProperty(variable);
			
			
			if(variableValue==null || variableValue.equals("")){
				variableValue= map.get(variable);
				if(variableValue != null){
					replacedString = arg.substring(0,variableStart) + variableValue + arg.substring(variableEnd+1,arg.length());
					replace(replacedString,map);
				}else
					return arg;
			}else{
				replacedString = arg.substring(0,variableStart) + variableValue + arg.substring(variableEnd+1,arg.length());
				replace(replacedString,map);
			}
				
		}
		return replacedString;
	}
}
	
